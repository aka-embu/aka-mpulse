package com.akamai.android.iec.mpulse_app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.akamai.android.sdk.Logger;
import com.akamai.mpulse.android.MPulse;
import com.akamai.mpulse.core.MPulseMetricTimerOptions;
import com.akamai.mpulse.core.config.MPulseSettings;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "BasicApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Logger.setLevel(Logger.LEVEL.DEBUG);
        while (!MPulse.sharedInstance().isInstanceInitialized()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                // Ignore
            }
        }
    }


    public void onClickDebugMode(View view) {
        // Toggles debug mode.
        // NOTE - This is only for testing and verifying stuff. Make sure the level is NOT set to DEBUG for release builds.
        // It may incur performance implications.
        Logger.LEVEL currentLevel = Logger.getCurrentLogLevel();
        if (currentLevel == Logger.LEVEL.INFO) {
            Logger.setLevel(Logger.LEVEL.DEBUG);
            Toast.makeText(getApplicationContext(), "Log level set to DEBUG", Toast.LENGTH_SHORT).show();
        } else {
            Logger.setLevel(Logger.LEVEL.INFO);
            Toast.makeText(getApplicationContext(), "Log level set to INFO", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickHttpGET(View view) {
        // HttpURLConnection usage. Make sure to call HttpURLConnection#disconnect() to release resources and collect stats.
        Button button = (Button) view;
        Logger.d("Clicked " + button.getText());
        final StringBuilder sb = new StringBuilder();
        final String uri = "HTTP://23.79.234.224/Anaina/cache/TestData2/2.jpg";
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(null, uri);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(10000);
                    urlConnection.setReadTimeout(10000);
                    // Set request parameters if needed
                    // urlConnection.setRequestProperty("User-Agent", getPackageName());
                    //Get response headers if needed
                    Map<String, List<String>> responseHeaders = urlConnection.getHeaderFields();
                    // Get response code if needed
                    int responseCode = urlConnection.getResponseCode();
                    // Download content if needed
                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    byte[] readBuffer = new byte[8192];
                    int bytesRead;
                    int total = 0;
                    while ((bytesRead = inputStream.read(readBuffer)) != -1) {
                        //Save readBuffer if needed!
                        total += bytesRead;
                    }
                    // Close the stream once done with the download.
                    inputStream.close();

                    sb.append("Response Code: ");
                    sb.append(responseCode);
                    sb.append(" | ");
                    sb.append("Downloaded: ");
                    sb.append(total / 1024);
                    sb.append(" KB");
                } catch (IOException e) {
                    e.printStackTrace();
                    sb.append(e);
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void onClickHttpsGET(View view) {
        // HttpURLConnection usage. Make sure to call HttpURLConnection#disconnect() to release resources and collect stats.
        Button button = (Button) view;
        Logger.d("Clicked " + button.getText());
        final StringBuilder sb = new StringBuilder();
        final String uri = "https://www.akamai.com";
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(null, uri);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(10000);
                    urlConnection.setReadTimeout(10000);
                    // Set request parameters if needed
                    // urlConnection.setRequestProperty("User-Agent", getPackageName());
                    //Get response headers if needed
                    Map<String, List<String>> responseHeaders = urlConnection.getHeaderFields();
                    // Get response code if needed
                    int responseCode = urlConnection.getResponseCode();
                    // Download content if needed
                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    byte[] readBuffer = new byte[8192];
                    int bytesRead;
                    int total = 0;
                    while ((bytesRead = inputStream.read(readBuffer)) != -1) {
                        //Save readBuffer if needed!
                        total += bytesRead;
                    }
                    // Close the stream once done with the download.
                    inputStream.close();

                    sb.append("Response Code: ");
                    sb.append(responseCode);
                    sb.append(" | ");
                    sb.append("Downloaded: ");
                    sb.append(total / 1024);
                    sb.append(" KB");
                } catch (IOException e) {
                    e.printStackTrace();
                    sb.append(e);
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onClickPOST(View view) {
        // HttpURLConnection usage. Make sure to call HttpURLConnection#disconnect() to release resources and collect stats.
        Button button = (Button) view;
        Logger.d("Clicked " + button.getText());
        final StringBuilder sb = new StringBuilder();
        /* POST URL */
        final String uri = "https://postman-echo.com/post";
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(null, uri);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    // set request method
                    urlConnection.setRequestMethod("POST");
                    // set request headers if any
                    urlConnection.setRequestProperty("x-hdr-1", "val1");
                    urlConnection.setRequestProperty("x-hdr-2", "val2");
                    // set do output to true.
                    urlConnection.setDoOutput(true);
                    // Set the appropriate content type for the request body.
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setConnectTimeout(10000);
                    urlConnection.setReadTimeout(10000);
                    // prepare POST data
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("fName", "First");
                    jsonObject.put("lName", "Last");
                    jsonObject.put("age", 11);
                    jsonObject.put("ts", System.currentTimeMillis());
                    byte[] postData = jsonObject.toString().getBytes();
                    int contentLength = postData.length;
                    urlConnection.setFixedLengthStreamingMode(contentLength);

                    // Now write post data.
                    OutputStream outputStream = urlConnection.getOutputStream();
                    outputStream.write(postData);
                    outputStream.flush();
                    outputStream.close();
                    // Read server response once request is sent.
                    urlConnection.connect();
                    // Get response code if needed
                    int responseCode = urlConnection.getResponseCode();
                    //Get response headers if needed
                    Map<String, List<String>> responseHeaders = urlConnection.getHeaderFields();

                    // Download content if needed
                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    byte[] readBuffer = new byte[8192];
                    int bytesRead;
                    int total = 0;
                    while ((bytesRead = inputStream.read(readBuffer)) != -1) {
                        //Save readBuffer if needed!
                        total += bytesRead;
                    }
                    // Close the stream once done with the download.
                    inputStream.close();

                    sb.append("Response Code: ");
                    sb.append(responseCode);
                    sb.append(" | ");
                    sb.append("Downloaded: ");
                    sb.append(total);
                    sb.append(" bytes");
                } catch (Exception e) {
                    e.printStackTrace();
                    sb.append(e);
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void onClickViewGroup(View view) {
        MPulse.sharedInstance().setViewGroup("TestViewGroup");
        onClickHttpGET(view);
        onClickHttpsGET(view);
        onClickPOST(view);
        MPulse.sharedInstance().resetViewGroup();
    }


    public void onClickViewGroupWithoutReset(View view) {
        MPulse.sharedInstance().setViewGroup("TestViewGroupWithoutReset");
        onClickHttpGET(view);
        onClickHttpsGET(view);
        onClickPOST(view);
    }

    public void onClickActionAutoNoSettings(View view) {

        MPulse.sharedInstance().startAction("TestActionAutoNoSettings");
        onClickHttpGET(view);
        onClickHttpsGET(view);
        onClickPOST(view);
    }

    public void onClickActionAutoTimeout(View view) {
        MPulseSettings settings = new MPulseSettings();
        settings.setActionName("MyAction");
        settings.setActionTimeout(2000);
        settings.setActionMaxResources(200);
        settings.setTimeoutForStop();
        MPulse.sharedInstance().startAction("TestActionAuto");
        onClickHttpGET(view);
        onClickHttpsGET(view);
        onClickPOST(view);
    }

    public void onClickActionWithStop(View view) {
        MPulse.sharedInstance().startAction("TestActionWithStop");
        onClickHttpGET(view);
        onClickHttpsGET(view);
        onClickPOST(view);
        MPulse.sharedInstance().stopAction();
    }

    public void onClickMetric(View view) {
        MPulse.sharedInstance().sendMetric("metric-1", 22);
    }

    public void onClickMetricWithAction(View view) {
        MPulseMetricTimerOptions options = new MPulseMetricTimerOptions();

// include on the Action beacon (instead of sending a separate beacon)
        options.duringAction = MPulseMetricTimerOptions.DuringAction.INCLUDE_ON_ACTION_BEACON;

// if the same Custom Metric was used twice on this Action, SUM the results
        options.onActionDuplicate = MPulseMetricTimerOptions.OnActionDuplicate.SUM;

        MPulse.sharedInstance().sendMetric("metric-1", 23, options);
        MPulse.sharedInstance().sendMetric("metric-1", 2, options);
        MPulse.sharedInstance().sendMetric("metric-2", 2);
    }

    public void onClickTimer(View view) {
        String timerID = MPulse.sharedInstance().startTimer("timer-1");
        onClickHttpGET(view);
        onClickPOST(view);
        MPulse.sharedInstance().stopTimer(timerID);
    }

    public void onClickSendTimer(View view) {
        // value is in milliseconds
        MPulse.sharedInstance().sendTimer("timer-1", 4);
    }

    public void onClickSendTimerWithAction(View view) {
        MPulseMetricTimerOptions options = new MPulseMetricTimerOptions();

// include on the Action beacon (instead of sending a separate beacon)
        options.duringAction = MPulseMetricTimerOptions.DuringAction.INCLUDE_ON_ACTION_BEACON;

// if the same Custom Timer was used twice on this Action, SUM the results
        options.onActionDuplicate = MPulseMetricTimerOptions.OnActionDuplicate.SUM;

        MPulse.sharedInstance().sendTimer("timer-1", 100, options);

        MPulse.sharedInstance().sendTimer("timer-1", 100, options);
    }

    public void onClickDimension(View view) {
        MPulse.sharedInstance().setDimension("MyDimension", "new value");
        onClickHttpGET(view);
        onClickPOST(view);
        MPulse.sharedInstance().resetDimension("MyDimension");
        onClickPOST(view);
    }

    public void onClickABTest(View view) {
        MPulse.sharedInstance().setABTest("A");
        onClickHttpsGET(view);
        MPulse.sharedInstance().resetABTest();
    }

    public void onClickEnabled(View view) {
        MPulse.sharedInstance().enable();
    }


    public void onClickDisabled(View view) {
        MPulse.sharedInstance().disable();
    }

    public void onClickInitializeWithDifferentKey(View view) {
        MPulse.initializeWithAPIKey("VDQTR-V52WN-U2PK3-CSC6T-7FHZQ", this);
    }

    public void onClickEnableNW(View view) {
        MPulse.sharedInstance().enableNetworkMonitoring();
    }

    public void onClickDisableNW(View view) {
        MPulse.sharedInstance().disableNetworkMonitoring();
    }

    public void onClickEnableFilteredNW(View view) {
        MPulse.sharedInstance().addUrlWhiteListFilter("https filter", ".*https.*");
        MPulse.sharedInstance().addUrlBlackListFilter("akamai filter", ".*akamai.*");
        MPulse.sharedInstance().enableFilteredNetworkMonitoring();
    }

    public void onClickOkHttp(View view) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(new MpulseOkHttpAppInterceptor())
                        .build();

                Request request = new Request.Builder()
                        .url("http://www.publicobject.com/helloworld.txt")
                        .header("User-Agent", "OkHttp Example")
                        .build();

                Response response = null;
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                response.body().close();
            }

        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}