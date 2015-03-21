package com.share.gta.util;

import com.anypresence.sdk.gadget_app_sample.models.OrderHeader;
import com.anypresence.sdk.gadget_app_sample.models.User;

/**
 * Created by diego.rotondale on 29/01/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public interface IECommerceManager {
    User getUser();

    OrderHeader getOrderHeader();

    void setOrderHeader(OrderHeader orderHeaderFound);
}
