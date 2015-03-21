package com.anypresence.masterpass_android_library.dto;

import java.io.Serializable;

/**
 * Created by diego.rotondale on 1/20/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class Status implements Serializable {
    public String status;

    public boolean hasError() {
        return status != null && status.equals("error");
    }
}
