package com.anypresence.masterpass_android_library.dto;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by diego.rotondale on 1/19/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class Order implements Serializable {
    private static final String LOG_TAG = Order.class.getSimpleName();
    public String orderNumber;
    public Wallet walletInfo;
    public CreditCard card;
    public Address shippingAddress;
    public String transactionId;
    public String preCheckoutTransactionId;

    public JSONObject getParams() {
        JSONObject params = new JSONObject();
        try {
            params.put("order_header_id", orderNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }

    public JSONObject getCompleteParams() {
        JSONObject params = new JSONObject();
        try {
            params.put("order_header_id", orderNumber);
            params.put("transaction_id", transactionId);
            params.put("pre_checkout_transaction_id", preCheckoutTransactionId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }

    public JSONObject getExpressParams() {
        JSONObject params = new JSONObject();
        try {
            params.put("order_header_id", orderNumber);
            params.put("precheckout_transaction_id", preCheckoutTransactionId);
            params.put("card_id", card.cardId.toString());
            params.put("shipping_id",shippingAddress.addressId.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }
}
