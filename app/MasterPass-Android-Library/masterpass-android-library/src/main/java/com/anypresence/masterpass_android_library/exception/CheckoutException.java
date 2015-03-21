package com.anypresence.masterpass_android_library.exception;

/**
 * Created by diego.rotondale on 1/20/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class CheckoutException extends Throwable {
    //public static String MP_ERROR_DOMAIN = "MasterPassErrorDomain";
    public static Integer MPErrorCode = 783;

    public CheckoutException(String detailMessage) {
        super(detailMessage);
    }
}
