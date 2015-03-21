package com.anypresence.masterpass_android_library.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by diego.rotondale on 1/19/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class PairCheckoutResponse extends StatusWithError {
    public Checkout checkout;

    public Serializable getShippingAddresses() {
        List<Address> addresses = new ArrayList<Address>();
        addresses.add(checkout.shippingAddress);
        return (Serializable) addresses;
    }
}
