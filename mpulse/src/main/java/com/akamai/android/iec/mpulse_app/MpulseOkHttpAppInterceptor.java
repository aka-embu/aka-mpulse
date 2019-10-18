package com.akamai.android.iec.mpulse_app;

import android.support.annotation.NonNull;

import com.akamai.android.sdk.net.AkaUrlStat;
import com.akamai.android.sdk.net.AkaUrlStatCollector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;


public class MpulseOkHttpAppInterceptor implements Interceptor {

    private static final String LOG_TAG = "MpulseOkHttpAppInterceptor";
    private final ExecutorService mHandler = Executors.newSingleThreadExecutor();


    @Override
    public Response intercept(Chain chain) throws IOException {

        final URL url = chain.request().url().url();
        final Request request;
        request = chain.request();

        final long startTime = getCurrentUTCTimeInMillis();
        AkaUrlStat info = new AkaUrlStat();
        info.mUrl = url;
        info.mStartTime = startTime;
        AkaUrlStatCollector.getInstance().urlReqStart(request, info);
        final Response response = chain.proceed(request);
        final long connectTime = getCurrentUTCTimeInMillis();
        if (response.body() != null && response.body().byteStream() != null) {
            AkaResponseBody responseBody = new AkaResponseBody(response.body(),
                    startTime, connectTime, request);
            Response wrappedResponse = new Response.Builder()
                    .request(response.request())
                    .protocol(response.protocol())
                    .code(response.code())
                    .message(response.message())
                    .handshake(response.handshake())
                    .headers(response.headers())
                    .body(responseBody)
                    .networkResponse(response.networkResponse())
                    .cacheResponse(response.cacheResponse())
                    .priorResponse(response.priorResponse())
                    .sentRequestAtMillis(response.sentRequestAtMillis())
                    .receivedResponseAtMillis(response.receivedResponseAtMillis())
                    .build();
            responseBody.setResponse(wrappedResponse);
            return wrappedResponse;
        } else {
            logStats(startTime, connectTime, 0, getCurrentUTCTimeInMillis(),
                    0, response, null, request);
            return response;
        }
    }

    private void logStats(long startTime, long connectTime, long ttfb,
                          long endTime, long bytesRead, Response response,
                          Exception exception, Request request) {

        long reqTime = (endTime > startTime && startTime > 0) ? endTime - startTime : 0;
        long ttfbCorrected = (ttfb > startTime && startTime > 0) ? ttfb - startTime : 0;
        AkaUrlStat info = new AkaUrlStat();
        info.mUrl = response.request().url().url();
        info.mContentSize = bytesRead;
        info.mResponseCode = response.code();
        info.mTtfb = (int) ttfbCorrected;
        info.mTimeStamp = new Date(startTime);
        info.mDuration = (int) reqTime;
        info.mReasonPhrase = response.message();
        AkaUrlStatCollector.getInstance().urlReqEnd(request, info);
    }

    private static long getCurrentUTCTimeInMillis() {
        return Calendar.getInstance(TimeZone.getTimeZone("utc")).getTimeInMillis();
    }

    private class AkaResponseBody extends ResponseBody {
        private ResponseBody mBody;
        private Response mResponse;
        private long mStartTime;
        private long mConnectTime;
        private long mTTFB;
        private long mEndTime;
        private Exception mException;
        private boolean mReadComplete;
        private boolean mReadStart;
        private long mBytesRead = 0L;
        private InputStream is;
        private Request mRequest;

        private AkaResponseBody(ResponseBody body, long startTime, long connectTime, Request request) {
            mBody = body;
            mStartTime = startTime;
            mConnectTime = connectTime;
            is = new WrappedInputStream(mBody.byteStream());
            mRequest = request;
        }

        @Override
        public MediaType contentType() {
            return mBody.contentType();
        }

        @Override
        public long contentLength() {
            return mBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            return Okio.buffer(Okio.source(is));
        }

        @Override
        public void close() {
            mBody.close();
            onReadComplete();
        }

        private void onReadComplete() {
            if (!mReadComplete) {
                mReadComplete = true;
                mEndTime = getCurrentUTCTimeInMillis();
                logStatsAsync();
            }
        }

        private void setResponse(Response response) {
            mResponse = response;
        }

        private void logStatsAsync() {
            // On a separate thread
            mHandler.submit(new Runnable() {
                @Override
                public void run() {
                    logStats(mStartTime, mConnectTime, mTTFB, mEndTime, mBytesRead, mResponse,
                            mException, mRequest);
                }
            });
        }

        private class WrappedInputStream extends InputStream {

            private InputStream mInputStream;

            public WrappedInputStream(InputStream inputStream) {
                mInputStream = inputStream;
            }

            @Override
            public int read() throws IOException {
                try {
                    int ret = mInputStream.read();
                    if (ret == -1) {
                        onReadComplete();
                    } else {
                        ++mBytesRead;
                        onReadStart();
                    }
                    return ret;
                } catch (IOException e) {
                    updateException(e);
                    throw e;
                }
            }


            @Override
            public int available() throws IOException {
                try {
                    return mInputStream.available();
                } catch (IOException e) {
                    updateException(e);
                    throw e;
                }
            }

            @Override
            public void close() throws IOException {
                onReadComplete();
                closeStreamSilently();
            }

            @Override
            public void mark(int readlimit) {
                mInputStream.mark(readlimit);
            }

            @Override
            public boolean markSupported() {
                return mInputStream.markSupported();
            }

            @Override
            public int read(@NonNull byte[] buffer) throws IOException {
                try {
                    return read(buffer, 0, buffer.length);
                }  catch (IOException e) {
                    updateException(e);
                    throw e;
                }
            }

            @Override
            public int read(@NonNull byte[] buffer, int byteOffset, int byteCount) throws IOException {
                try {
                    int ret = mInputStream.read(buffer, byteOffset, byteCount);
                    if (ret == -1) {
                        onReadComplete();
                    } else {
                        mBytesRead += ret;
                        onReadStart();
                    }
                    return ret;
                }  catch (IOException e) {
                    updateException(e);
                    throw e;
                }
            }

            @Override
            public synchronized void reset() throws IOException {
                try {
                    mInputStream.reset();
                }  catch (IOException e) {
                    updateException(e);
                    throw e;
                }
            }

            @Override
            public long skip(long byteCount) throws IOException {
                try {
                    return mInputStream.skip(byteCount);
                }  catch (IOException e) {
                    updateException(e);
                    throw e;
                }
            }

            private void closeStreamSilently() {
                if (mInputStream != null) {
                    try {
                        mInputStream.close();
                    } catch (Exception ignored) {}
                }
            }

            private void onReadStart() {
                if (!mReadStart) {
                    mReadStart = true;
                    mTTFB = getCurrentUTCTimeInMillis();
                }
            }

            private void updateException (Exception e) {
                mException = e;
            }
        }
    }
}
