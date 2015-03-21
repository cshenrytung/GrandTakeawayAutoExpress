package com.anypresence.masterpass_android_library.dto;

import android.util.Log;

import com.anypresence.masterpass_android_library.activities.MPLightBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by diego.rotondale on 1/19/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class LightBoxParams implements Serializable {
    private static final String LOG_TAG = LightBoxParams.class.getSimpleName();
    private Details details;
    private String version;
    private List<String> requestedDataTypes;
    private Boolean requestPairing;
    private List<String> allowedCardType;
    private Order order;
    private MPLightBox.MPLightBoxType type;
    private Boolean requestExpressCheckout;

    public MPLightBox.MPLightBoxType getType() {
        return type;
    }

    public void setType(MPLightBox.MPLightBoxType type) {
        this.type = type;
    }

    public void setRequestedDataTypes(List<String> requestedDataTypes) {
        this.requestedDataTypes = requestedDataTypes;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    public void setRequestPairing(Boolean requestPairing) {
        this.requestPairing = requestPairing;
    }

    public void setRequestExpressCheckout(Boolean requestExpressCheckout){this.requestExpressCheckout = requestExpressCheckout;};

    public void setVersion(String version) {
        this.version = version;
    }

    public void setAllowedCardType(List<String> allowedCardType) {
        this.allowedCardType = allowedCardType;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String toJson() {
        JSONObject json = new JSONObject();
        try {
            //PAIR CHECKOUT
            if (type == MPLightBox.MPLightBoxType.MPLightBoxTypePreCheckout) {
                getCheckoutParams(json);
                return json.toString();
            }
            //PAIR
            if (type == MPLightBox.MPLightBoxType.MPLightBoxTypeConnect) {
                getParams(json);
                return json.toString();
            }
            //RETURN CHECKOUT
            if (type == MPLightBox.MPLightBoxType.MPLightBoxTypeCheckout) {
                getReturnCheckoutParams(json);
                return json.toString();
            }
        } catch (JSONException e) {
            Log.e(LightBoxParams.class.getSimpleName(), e.getMessage());
        }
        return json.toString();
    }

    private void getParams(JSONObject json) throws JSONException {
        if (requestedDataTypes != null)
            json.put("requestedDataTypes", getRequestDataTypes());
        if (details.checkoutRequestToken != null)
            json.put("requestToken", details.checkoutRequestToken);
        if (details.merchantCheckoutId != null)
            json.put("merchantCheckoutId", details.merchantCheckoutId);
        if (details.callbackUrl != null)
            json.put("callbackUrl", getCallbackURL());
        if (version != null)
            json.put("version", version);
        if (details.pairingRequestToken != null)
            json.put("pairingRequestToken", details.pairingRequestToken);
        if (requestExpressCheckout != null)
            json.put("requestExpressCheckout",requestExpressCheckout);
        if (order != null) {
            if (order.card != null)
                json.put("cardId", order.card.cardId);
            if (order.shippingAddress != null)
                json.put("shippingId", order.shippingAddress.addressId);
            if (order.walletInfo != null) {
                json.put("precheckoutTransactionId", order.walletInfo.preCheckoutTransactionId);
                json.put("walletName", order.walletInfo.walletName);
                json.put("consumerWalletId", order.walletInfo.consumerWalletId);
            }
        }
    }

    private void getCheckoutParams(JSONObject json) throws JSONException {
        if (details.checkoutRequestToken != null)
            json.put("requestToken", details.checkoutRequestToken);
        if (details.merchantCheckoutId != null)
            json.put("merchantCheckoutId", details.merchantCheckoutId);
        if (requestExpressCheckout != null)
            json.put("requestExpressCheckout",requestExpressCheckout);
        if (requestedDataTypes != null)
            json.put("requestedDataTypes", getRequestDataTypes());
        if (details.callbackUrl != null)
            json.put("callbackUrl", getCallbackURL());
        if (details.pairingRequestToken != null)
            json.put("pairingRequestToken", details.pairingRequestToken);
        if (allowedCardType != null)
            json.put("allowedCardTypes", getAllowedCardTypes());
        if (requestPairing != null)
            json.put("requestPairing", requestPairing);
        if (version != null)
            json.put("version", version);
    }


    private void getReturnCheckoutParams(JSONObject json) throws JSONException {
        if (details.callbackUrl != null)
            json.put("callbackUrl", getCallbackURL());
        if (order != null) {
            if (order.card != null)
                json.put("cardId", order.card.cardId);
            if (order.shippingAddress != null)
                json.put("shippingId", order.shippingAddress.addressId);
            if (order.walletInfo != null) {
                json.put("precheckoutTransactionId", order.walletInfo.preCheckoutTransactionId);
                json.put("walletName", order.walletInfo.walletName);
                json.put("consumerWalletId", order.walletInfo.consumerWalletId);
            }
        }
        if (details.merchantCheckoutId != null)
            json.put("merchantCheckoutId", details.merchantCheckoutId);
        if (version != null)
            json.put("version", version);
        if (details.pairingRequestToken != null)
            json.put("pairingRequestToken", details.pairingRequestToken);
        if (details.checkoutRequestToken != null)
            json.put("requestToken", details.checkoutRequestToken);
    }

    public JSONArray getRequestDataTypes() {
        JSONArray jsonArray = new JSONArray();
        for (String supportedDataType : requestedDataTypes) {
            jsonArray.put(supportedDataType);
        }
        return jsonArray;
    }

    public JSONArray getAllowedCardTypes() {
        JSONArray jsonArray = new JSONArray();
        for (String supportedDataType : allowedCardType) {
            jsonArray.put(supportedDataType);
        }
        return jsonArray;
    }

    public String getCallbackURL() {
        return details.callbackUrl.replace("\\", "");
    }
}
