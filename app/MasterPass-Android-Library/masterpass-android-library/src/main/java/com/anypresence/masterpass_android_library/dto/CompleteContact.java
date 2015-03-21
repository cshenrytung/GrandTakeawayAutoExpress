package com.anypresence.masterpass_android_library.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by diego.rotondale on 1/21/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class CompleteContact extends Contact {
    public static final String SHIPPING_ADDRESS_KEY = "shipping_address";
    public static final String WALLET_ID_KEY = "wallet_id";
    public static final String PRE_CHECKOUT_TRANSACTION_ID_KEY = "pre_checkout_transaction_id";

    @SerializedName(SHIPPING_ADDRESS_KEY)
    Address shippingAddress;
    @SerializedName(WALLET_ID_KEY)
    String walletId;
    @SerializedName(PRE_CHECKOUT_TRANSACTION_ID_KEY)
    Integer preCheckoutTransactionId;
}
