package com.anypresence.masterpass_android_library.dto;

import com.anypresence.masterpass_android_library.activities.MPLightBox;

import java.io.Serializable;

/**
 * Created by diego.rotondale on 1/26/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class WebViewOptions implements Serializable {
    public MPLightBox.MPLightBoxType type;
    public LightBoxParams options;

    public WebViewOptions(MPLightBox.MPLightBoxType type, LightBoxParams options) {
        this.type = type;
        this.options = options;
    }
}
