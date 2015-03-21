package com.anypresence.masterpass_android_library.util;

import android.os.Looper;

import com.anypresence.masterpass_android_library.interfaces.FutureCallback;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by diego.rotondale on 02/02/2015.
 */
public class ConnectionUtil {
    private static final String LOG_TAG = ConnectionUtil.class.getSimpleName();
    private static final Integer TIMEOUT = 10000;

    public static void call(final Boolean post, final String url, final String xSessionId, final JSONObject json, final FutureCallback<JSONObject> listener) {
        Thread t = new Thread() {
            public void run() {
                Looper.prepare();
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), TIMEOUT);
                HttpResponse response;
                try {
                    HttpUriRequest request = null;
                    if (post) {
                        request = new HttpPost(url);
                        request.setHeader("Accept", "application/json");
                        request.setHeader("Content-type", "application/json");
                        if (json != null) {
                            StringEntity se = new StringEntity(json.toString());
                            se.setContentType("application/json");
                            ((HttpPost) request).setEntity(se);
                        }
                    } else {
                        request = new HttpGet(url);
                    }
                    request.setHeader("Cookie", "_session_id=" + xSessionId);
                    response = client.execute(request);
                    if (response != null) {
                        StatusLine statusLine = response.getStatusLine();
                        int statusCode = statusLine.getStatusCode();

                        InputStream in = response.getEntity().getContent();
                        BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                        StringBuilder responseStrBuilder = new StringBuilder();

                        String inputStr;
                        while ((inputStr = streamReader.readLine()) != null)
                            responseStrBuilder.append(inputStr);
                        if (statusCode == 200) {
                            listener.onSuccess(new JSONObject(responseStrBuilder.toString()));
                        } else {
                            listener.onFailure(new Throwable(responseStrBuilder.toString()));
                        }
                    }
                } catch (Exception e) {
                    listener.onFailure(e);
                }
                Looper.loop();
            }
        };
        t.start();
    }
}
