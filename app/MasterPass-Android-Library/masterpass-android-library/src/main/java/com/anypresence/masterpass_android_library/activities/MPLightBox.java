package com.anypresence.masterpass_android_library.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.anypresence.masterpass_android_library.Constants;
import com.anypresence.masterpass_android_library.R;
import com.anypresence.masterpass_android_library.dto.LightBoxParams;
import com.anypresence.masterpass_android_library.dto.PairCheckoutResponse;
import com.anypresence.masterpass_android_library.dto.Status;
import com.anypresence.masterpass_android_library.dto.WebViewOptions;
import com.anypresence.masterpass_android_library.interfaces.FutureCallback;
import com.anypresence.masterpass_android_library.util.ConnectionUtil;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by diego.rotondale on 16/09/2014.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class MPLightBox extends Activity {
    private static final String LOG_TAG = MPLightBox.class.getSimpleName();
    private static final String URL = "file:///android_asset/mp_lightbox_base.html";
    private MPLightBoxType type;
    private LightBoxParams options;
    private WebView web;
    private ProgressDialog progressDialog;
    private String xSessionId;

    public MPLightBox() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        web = (WebView) findViewById(R.id.webview_content);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            xSessionId = extras.getString(Constants.X_SESSION_ID);
            WebViewOptions options = (WebViewOptions) extras.getSerializable(Constants.OPTIONS_PARAMETER);
            initiateLightBoxOfTypeWithOptions(options.type, options.options);
        }
    }

    public void initiateLightBoxOfTypeWithOptions(MPLightBoxType type, LightBoxParams options) {
        this.type = type;
        this.options = options;
        web.loadUrl(URL);
        web.getSettings().setJavaScriptEnabled(true);
        web.setWebViewClient(new MPLibraryWebViewClient());
    }

    public void checkIfLoadDone(WebView webView) {
        if (progressDialog != null)
            progressDialog.dismiss();
        web.loadUrl(getJs());
    }

    private String getJs() {
        String value = null;
        if (type != null && options != null) {
            value = "javascript:initiateLightbox(" + getType() + ", " + options.toJson() + ");";
        }
        return value;
    }

    private String getType() {
        if (type != null) {
            int JSType = type.value;
            if (type == MPLightBoxType.MPLightBoxTypePreCheckout)
                JSType = 1;
            return String.valueOf(JSType);
        }
        return null;
    }

    private JSONObject getJson(URI uri) {
        JSONObject json = new JSONObject();
        List<NameValuePair> parameters = URLEncodedUtils.parse(uri, "UTF-8");
        if (parameters != null) {
            for (NameValuePair p : parameters) {
                try {
                    json.put(p.getName(), p.getValue());
                } catch (JSONException e) {
                    Log.e(MPLightBox.class.getSimpleName(), e.getMessage());
                }
            }
        }
        return json;
    }

    public enum MPLightBoxType {
        MPLightBoxTypeConnect(0),
        MPLightBoxTypeCheckout(1),
        MPLightBoxTypePreCheckout(2);

        public int value;

        MPLightBoxType(int value) {
            this.value = value;
        }
    }

    private class MPLibraryWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                URI uri = new URI(url);
                String urlConverted = uri.getScheme() + "://" + uri.getHost() + uri.getPath();
                Log.d("urlConverted: ", urlConverted);
                Log.d("getCallbackURL: ", options.getCallbackURL());
                if (urlConverted.equals(options.getCallbackURL())) {
                    FutureCallback<JSONObject> listener = new FutureCallback<JSONObject>() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            String responseString = response.toString();
                            Status status = new Gson().fromJson(responseString, Status.class);
                            if (status != null && status.status != null) {
                                boolean success = status.status.equals("success");
                                Intent returnIntent = new Intent();
                                if (type == MPLightBoxType.MPLightBoxTypePreCheckout) {
                                    PairCheckoutResponse pairCheckoutResponse = new Gson().fromJson(responseString, PairCheckoutResponse.class);
                                    returnIntent.putExtra(Constants.LIGHT_BOX_EXTRA, pairCheckoutResponse);
                                } else {
                                    returnIntent.putExtra(Constants.LIGHT_BOX_EXTRA, success);
                                }
                                setResult(success ? RESULT_OK : RESULT_CANCELED, returnIntent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Throwable error) {
                            Log.e(LOG_TAG, error.toString());
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra(Constants.LIGHT_BOX_EXTRA, false);
                            setResult(RESULT_CANCELED, returnIntent);
                            finish();
                        }
                    };

                    //JSONObject json = getJson(uri);
                    //boolean post = false;
                    //if (json.length() != 1)
                    //  post = true;
                    ConnectionUtil.call(false, url, xSessionId, null, listener);

                } else {
                    web.loadUrl(url);
                    //web.postUrl(urlConverted, EncodingUtils.getBytes(uri.getQuery(), "BASE64"));
                    return false;
                }
            } catch (URISyntaxException e) {
                Log.e(LOG_TAG, e.getMessage());
            } finally {
                return true;
            }
        }

        @Override
        public void onPageFinished(WebView webView, String url) {
            Log.d(LOG_TAG, url);
            if (url.equals(URL))
                checkIfLoadDone(webView);
        }
    }
}
