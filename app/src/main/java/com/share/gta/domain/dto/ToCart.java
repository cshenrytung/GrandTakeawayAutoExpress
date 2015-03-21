package com.share.gta.domain.dto;

import com.anypresence.sdk.gadget_app_sample.models.Product;

/**
 * Created by diego.rotondale on 29/01/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class ToCart {
    public Product product;
    public float x;
    public float y;

    public ToCart(Product product, float x, float y) {
        this.product = product;
        this.x = x;
        this.y = y;
    }
}
