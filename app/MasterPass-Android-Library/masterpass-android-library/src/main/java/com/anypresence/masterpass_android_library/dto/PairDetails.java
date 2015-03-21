package com.anypresence.masterpass_android_library.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by diego.rotondale on 1/21/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class PairDetails extends StatusWithError {
    public static final String PAIRING_REQUEST_TOKEN_KEY = "pairing_request_token";
    public static final String MERCHANT_CHECKOUT_ID_KEY = "merchant_checkout_id";
    public static final String CALLBACK_URL_KEY = "callback_url";

    @SerializedName(PAIRING_REQUEST_TOKEN_KEY)
    public String pairingRequestToken;
    @SerializedName(MERCHANT_CHECKOUT_ID_KEY)
    public String merchantCheckoutId;
    @SerializedName(CALLBACK_URL_KEY)
    public String callbackUrl;
}
