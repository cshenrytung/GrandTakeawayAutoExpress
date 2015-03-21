package com.anypresence.masterpass_android_library.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by diego.rotondale on 1/21/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class Checkout implements Serializable {
    public static final String TRANSACTION_ID_KEY = "transaction_id";
    public static final String SHIPPING_ADDRESS_KEY = "shipping_address";
    public static final String WALLET_ID_KEY = "wallet_id";
    public static final String PRE_CHECKOUT_TRANSACTION_ID_KEY = "pre_checkout_transaction_id";

    @SerializedName(SHIPPING_ADDRESS_KEY)
    public Address shippingAddress;
    public CreditCard card;
    @SerializedName(WALLET_ID_KEY)
    public String walletId;
    public Contact contact;
    @SerializedName(PRE_CHECKOUT_TRANSACTION_ID_KEY)
    public String preCheckoutTransactionId;
    @SerializedName(TRANSACTION_ID_KEY)
    public String transactionId;
}
