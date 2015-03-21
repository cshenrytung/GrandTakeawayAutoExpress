package com.anypresence.masterpass_android_library.exception;

/**
 * Created by diego.rotondale on 1/19/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class NotPairedException extends RuntimeException {
    public NotPairedException() {
        super("User must be paired with MasterPass before calling precheckout");
    }
}
