package com.anypresence.masterpass_android_library;

import android.util.Log;

import com.anypresence.masterpass_android_library.activities.MPLightBox;
import com.anypresence.masterpass_android_library.dto.Details;
import com.anypresence.masterpass_android_library.dto.LightBoxParams;
import com.anypresence.masterpass_android_library.dto.Order;
import com.anypresence.masterpass_android_library.dto.PairCheckoutResponse;
import com.anypresence.masterpass_android_library.dto.PreCheckoutResponse;
import com.anypresence.masterpass_android_library.dto.RestaurantRequest;
import com.anypresence.masterpass_android_library.dto.StatusWithError;
import com.anypresence.masterpass_android_library.dto.Wallet;
import com.anypresence.masterpass_android_library.dto.WebViewOptions;
import com.anypresence.masterpass_android_library.exception.BadRequestException;
import com.anypresence.masterpass_android_library.exception.CheckoutException;
import com.anypresence.masterpass_android_library.exception.ManualCheckoutException;
import com.anypresence.masterpass_android_library.exception.NotPairedException;
import com.anypresence.masterpass_android_library.exception.PairCheckoutException;
import com.anypresence.masterpass_android_library.interfaces.FutureCallback;
import com.anypresence.masterpass_android_library.interfaces.ILightBox;
import com.anypresence.masterpass_android_library.interfaces.IManager;
import com.anypresence.masterpass_android_library.interfaces.ViewController;
import com.anypresence.masterpass_android_library.util.ConnectionUtil;
import com.anypresence.masterpass_android_library.xml.StackOverflowXmlParser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by diego.rotondale on 1/17/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class MPManager implements ILightBox {
    private static final String LOG_TAG = MPManager.class.getSimpleName();

    public static String DATA_TYPE_CARD = "CARD";
    public static String DATA_TYPE_ADDRESS = "ADDRESS";
    public static String DATA_TYPE_PROFILE = "PROFILE";

    public static String CARD_TYPE_AMEX = "amex";
    public static String CARD_TYPE_MASTERCARD = "master";
    public static String CARD_TYPE_DISCOVER = "discover";
    public static String CARD_TYPE_VISA = "visa";
    public static String CARD_TYPE_MAESTRO = "maestro";

    public static String MP_VERSION = "v6";
    private static MPManager instance;

    private IManager delegate;

    private String prefixURL;
    private String pairURL;
    private String preCheckoutURL;
    private String checkoutURL;
    private String pairAndCheckoutURL;
    private String completePairCheckout;
    private String manualCheckoutURL;
    private String expressCheckoutURL;
    private String restaurantLocationURL;

    //Init

    /**
     * Initializes (for returns the shared instance of) the manager to interact with
     * the MasterPass services
     *
     * @return MPManager instance
     */
    public static MPManager getInstance() {
        if (instance == null)
            instance = new MPManager();
        return instance;
    }

    public void setDelegate(IManager delegate) {
        this.delegate = delegate;
        initializeURL();
    }

    private void initializeURL() {
        prefixURL = delegate.getServerAddress() + "/masterpass/";
        pairURL = prefixURL + "pair";
        preCheckoutURL = prefixURL + "precheckout";
        checkoutURL = prefixURL + "return_checkout";
        pairAndCheckoutURL = prefixURL + "pair_and_checkout";
        completePairCheckout = prefixURL + "complete_checkout";
        manualCheckoutURL = prefixURL + "non_masterpass_checkout";
        expressCheckoutURL = prefixURL + "express_checkout";
        restaurantLocationURL = "http://dmartin.org:8021/restaurants/v1/restaurant";
    }
    //Pairing

    /**
     * Opens the pairing modal over a viewController.
     *
     * @param viewController The viewController to show the pairing modal over
     */
    public void pair(final ViewController viewController) {
        FutureCallback<Details> futureCallback = new FutureCallback<Details>() {
            @Override
            public void onSuccess(Details details) {
                LightBoxParams options = new LightBoxParams();
                options.setRequestedDataTypes(delegate.getSupportedDataTypes());
                options.setDetails(details);
                options.setRequestPairing(true);
                options.setVersion(MP_VERSION);
                options.setType(MPLightBox.MPLightBoxType.MPLightBoxTypeConnect);
                options.setRequestExpressCheckout(delegate.isExpressCheckoutEnabled());
                showLightBoxWindowOfType(MPLightBox.MPLightBoxType.MPLightBoxTypeConnect, options, viewController);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(MPManager.class.getSimpleName(), "Error Requesting Pairing: " + throwable.getLocalizedMessage());
                delegate.pairingDidComplete(false, throwable);
            }
        };
        this.requestPairing(viewController, futureCallback);
    }

    public void restaurantList(final ViewController viewController, String lon, String lat) {
        FutureCallback<List<StackOverflowXmlParser.Entry>> futureCallback = new FutureCallback<List<StackOverflowXmlParser.Entry>>() {
            @Override
            public void onSuccess(List<StackOverflowXmlParser.Entry> details) {
//                LightBoxParams options = new LightBoxParams();
//                options.setRequestedDataTypes(delegate.getSupportedDataTypes());
//                options.setDetails(details);
//                options.setRequestPairing(true);
//                options.setVersion(MP_VERSION);
//                options.setType(MPLightBox.MPLightBoxType.MPLightBoxTypeConnect);
//                options.setRequestExpressCheckout(delegate.isExpressCheckoutEnabled());
//                showLightBoxWindowOfType(MPLightBox.MPLightBoxType.MPLightBoxTypeConnect, options, viewController);
                  viewController.callDone(null, false);

            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(MPManager.class.getSimpleName(), "Error Requesting Pairing: " + throwable.getLocalizedMessage());
                delegate.pairingDidComplete(false, throwable);
            }
        };
        this.requestRestaurantsList(viewController, futureCallback, lon, lat);
    }

    protected void requestPairing(ViewController viewController, final FutureCallback<Details> callback) {
        FutureCallback<JSONObject> listener = new FutureCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                String responseString = response.toString();
                Log.d(LOG_TAG, "Approved Pairing Request: " + responseString);
                callback.onSuccess(new Gson().fromJson(responseString, Details.class));
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e("Error Pairing Request: ", error.toString());
                callback.onFailure(error);
            }
        };
        ConnectionUtil.call(true, getPairURL(), viewController.getXSessionId(), null, listener);
    }

    protected void requestRestaurantsList(ViewController viewController, final FutureCallback<List<StackOverflowXmlParser.Entry>> callback, String lon, String lat) {
        FutureCallback<String> listener = new FutureCallback<String>() {
            @Override
            public void onSuccess(String response) {

                List<StackOverflowXmlParser.Entry> restaurants = null;
                Log.d(LOG_TAG, "Approved Pairing Request: " + response);

                StackOverflowXmlParser soxp = new StackOverflowXmlParser();
                try {
                    restaurants = soxp.parse(response);
                } catch (Exception e) {
                    Log.d(LOG_TAG, "Approved Pairing Request: " + e.getMessage());
                }
                callback.onSuccess(restaurants);
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e("Error Pairing Request: ", error.toString());
                callback.onFailure(error);
            }
        };

        RestaurantRequest rr = new RestaurantRequest();
        rr.PageOffset = "0";
        rr.PageLength = "10";
        rr.Longitude = "-90.286781";
        rr.Latitude = "-90.286781";


        ConnectionUtil.call(getRestaurantLocationURL(lon, lat), viewController.getXSessionId(), null, listener);
    }

    private void showLightBoxWindowOfType(final MPLightBox.MPLightBoxType type, final LightBoxParams options, ViewController viewController) {
        viewController.presentViewController(new MPLightBox(), true, new WebViewOptions(type, options));
    }

    /**
     * The current pairing status as defined by the delegate.
     * This is just a convenience method for [self.delegate isAppPaired]
     *
     * @return the current pairing status
     */
    private Boolean isAppPaired() {
        return delegate.isAppPaired();
    }

    //Return Checkout

    /**
     * Retrieves the preCheckout data from the MasterPass service
     */
    public void preCheckout(final ViewController viewController, final FutureCallback<PreCheckoutResponse> callback) {
        //TODO:Check with David
        //checkoutPaired();
        viewController.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FutureCallback callbackCall = new FutureCallback<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        String responseString = response.toString();
                        Log.d(LOG_TAG, "Received PreCheckout Data: " + responseString);
                        PreCheckoutResponse preCheckoutResponse = new Gson().fromJson(responseString, PreCheckoutResponse.class);
                        if (preCheckoutResponse.hasError()) {
                            if (preCheckoutResponse.isNotPaired()) {
                                // User is not paired. They may have disconnected via the MasterPass console.
                                // We will optionally reset that pairing status here
                                delegate.resetUserPairing();
                                callback.onFailure(new BadRequestException(preCheckoutResponse.errors));
                            }
                        } else {
                            callback.onSuccess(preCheckoutResponse);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e(LOG_TAG, throwable.toString());
                        // User is not paired. They may have disconnected via the MasterPass console.
                        // We will optionally reset that pairing status here
                        delegate.resetUserPairing();
                        callback.onFailure(new BadRequestException(throwable.toString()));
                    }
                };

                ConnectionUtil.call(true, getPreCheckoutURL(), viewController.getXSessionId(), getPreCheckoutParams(), callbackCall);
            }
        });
    }

    public JSONObject getPreCheckoutParams() {
        JSONObject params = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            List<String> supportedDataTypes = delegate.getSupportedDataTypes();
            for (String supportedDataType : supportedDataTypes) {
                jsonArray.put(supportedDataType);
            }
            params.put("requested_data_types", jsonArray);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.toString());
        }
        return params;
    }

    private void checkoutPaired() {
        if (!isAppPaired())
            throw new NotPairedException();
    }

    private void requestReturnCheckoutForOrder(Order order, ViewController viewController, final FutureCallback callback) {
        checkoutPaired();
        FutureCallback<JSONObject> listener = new FutureCallback<JSONObject>() {

            @Override
            public void onSuccess(JSONObject response) {
                String responseString = response.toString();
                Log.d(LOG_TAG, "Approved Return Checkout Request: " + responseString);
                Details details = new Gson().fromJson(responseString, Details.class);
                if (details.hasError()) {
                    callback.onFailure(new CheckoutException(details.errors));
                } else {
                    callback.onSuccess(details);
                }
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(LOG_TAG, "Error Return Checkout Request: " + error.toString());
                callback.onFailure(error);
            }
        };
        ConnectionUtil.call(true, getCheckoutURL(), viewController.getXSessionId(), order.getParams(), listener);
    }

    public void returnCheckout(final Order order, final ViewController viewController) {
        FutureCallback<Details> callback = new FutureCallback<Details>() {
            @Override
            public void onSuccess(Details details) {
                LightBoxParams options = new LightBoxParams();
                options.setRequestedDataTypes(delegate.getSupportedDataTypes());
                options.setDetails(details);
                options.setRequestPairing(false);
                options.setVersion(MP_VERSION);
                options.setOrder(order);
                options.setType(MPLightBox.MPLightBoxType.MPLightBoxTypeCheckout);
                showLightBoxWindowOfType(MPLightBox.MPLightBoxType.MPLightBoxTypeCheckout, options, viewController);
            }

            @Override
            public void onFailure(Throwable error) {
                delegate.pairingDidComplete(false, error);
            }
        };
        requestReturnCheckoutForOrder(order, viewController, callback);
    }

    // Express Checkout

    public boolean isExpressCheckoutEnabled(){
        return delegate.isExpressCheckoutEnabled();
    }

    public void expressCheckoutForOrder(Order order, ViewController viewController){
        checkoutPaired();
        FutureCallback<JSONObject> listener = new FutureCallback<JSONObject>() {

            @Override
            public void onSuccess(JSONObject response) {
                String responseString = response.toString();
                Log.d(LOG_TAG, "Approved Express Checkout Request: " + responseString);
                Details details = new Gson().fromJson(responseString, Details.class);
                if (details.hasError()) {
                    delegate.checkoutDidComplete(false,new CheckoutException(details.errors));
                } else {
                    delegate.checkoutDidComplete(true,null);
                }
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(LOG_TAG, "Error Express Checkout Request: " + error.toString());
                delegate.checkoutDidComplete(false,error);
            }
        };
        ConnectionUtil.call(true, getExpressCheckoutURL(), viewController.getXSessionId(), order.getExpressParams(), listener);
    }

    //Pair Checkout

    public void pairCheckoutForOrder(final Order order, final ViewController viewController) {
        FutureCallback<Details> callback = new FutureCallback<Details>() {
            @Override
            public void onSuccess(Details details) {
                LightBoxParams options = new LightBoxParams();
                options.setRequestedDataTypes(delegate.getSupportedDataTypes());
                options.setDetails(details);
                options.setOrder(order);
                options.setAllowedCardType(delegate.getSupportedCardTypes());
                options.setRequestPairing(true);
                options.setVersion(MP_VERSION);
                options.setType(MPLightBox.MPLightBoxType.MPLightBoxTypePreCheckout);
                options.setRequestExpressCheckout(delegate.isExpressCheckoutEnabled());
                showLightBoxWindowOfType(MPLightBox.MPLightBoxType.MPLightBoxTypePreCheckout, options, viewController);
            }

            @Override
            public void onFailure(Throwable error) {
                delegate.pairingDidComplete(false, error);
            }
        };
        requestPairCheckoutForOrder(order, viewController, callback);
    }

    private void requestPairCheckoutForOrder(Order order, ViewController viewController, final FutureCallback callback) {
        FutureCallback<JSONObject> listener = new FutureCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                String responseString = response.toString();
                Log.d(LOG_TAG, "Approved Pair Checkout Request: " + responseString);
                Details details = new Gson().fromJson(responseString, Details.class);
                if (details.hasError()) {
                    callback.onFailure(new CheckoutException(details.errors));
                } else {
                    callback.onSuccess(details);
                }
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(LOG_TAG, "Error Return Pair Checkout Request: " + error.toString());
                callback.onFailure(error);
            }
        };
        ConnectionUtil.call(true, getPairAndCheckoutURL(), viewController.getXSessionId(), order.getParams(), listener);
    }

    public void completePairCheckoutForOrder(Order order, ViewController viewController) {
        FutureCallback<JSONObject> listener = new FutureCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                String responseString = response.toString();
                Log.d(LOG_TAG, "Completed Checkout Successfully: " + responseString);
                StatusWithError status = new Gson().fromJson(responseString, StatusWithError.class);
                if (status.hasError()) {
                    delegate.pairCheckoutDidComplete(false, new PairCheckoutException(status.errors));
                } else {
                    delegate.pairCheckoutDidComplete(true, null);
                }
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(LOG_TAG, "Error in Completed Checkout Successfully: " + error.toString());
                delegate.pairCheckoutDidComplete(false, new PairCheckoutException(error.toString()));
            }
        };
        ConnectionUtil.call(true, getCompletePairCheckout(), viewController.getXSessionId(), order.getCompleteParams(), listener);
    }

    //Manual Checkout

    public void completeManualCheckout(Order order, ViewController viewController) {
        FutureCallback<JSONObject> listener = new FutureCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                String responseString = response.toString();
                Log.d(LOG_TAG, "Completed Manual Checkout Successfully: " + responseString);
                StatusWithError status = new Gson().fromJson(responseString, StatusWithError.class);
                if (status.hasError()) {
                    delegate.manualCheckoutDidComplete(false, new ManualCheckoutException(status.errors));
                } else {
                    delegate.manualCheckoutDidComplete(true, null);
                }
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(LOG_TAG, "Error in Completed Manual Checkout Successfully: " + error.toString());
            }
        };
        ConnectionUtil.call(true, getManualCheckoutURL(), viewController.getXSessionId(), order.getParams(), listener);
    }

    //ILightBox
    @Override
    public void pairingViewDidCompletePairing(ViewController pairingViewController, final Boolean success, final Throwable error) {
        delegate.pairingDidComplete(success, error);
    }

    @Override
    public void lightBoxDidCompletePreCheckout(ViewController lightBoxViewController, final Boolean success, final PairCheckoutResponse data, final Throwable error) {
        delegate.preCheckoutDidComplete(success, data, error);
    }

    @Override
    public void lightBoxDidCompleteCheckout(ViewController pairingViewController, final Boolean success, final Throwable error) {
        delegate.checkoutDidComplete(success, error);
    }

    public String getPreCheckoutURL() {
        return preCheckoutURL;
    }

    public String getPairURL() {
        return pairURL;
    }

    public String getCheckoutURL() {
        return checkoutURL;
    }

    public String getPairAndCheckoutURL() {
        return pairAndCheckoutURL;
    }

    public String getCompletePairCheckout() {
        return completePairCheckout;
    }

    public String getManualCheckoutURL() {
        return manualCheckoutURL;
    }

    public String getExpressCheckoutURL(){
        return expressCheckoutURL;
    }

    public String getRestaurantLocationURL(String lon, String lat) {
        return "http://dmartin.org:8021/restaurants/v1/restaurant?PageOffset=0&PageLength=10&Latitude="+lat+"&Longitude="+lon;
    }
}
