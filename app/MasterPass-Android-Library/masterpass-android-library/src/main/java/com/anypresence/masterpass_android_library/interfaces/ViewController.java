package com.anypresence.masterpass_android_library.interfaces;

import android.app.Activity;
import android.content.Context;

import com.anypresence.masterpass_android_library.dto.WebViewOptions;

/**
 * Created by diego.rotondale on 1/19/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public interface ViewController {
    void presentViewController(Activity activity, Boolean animated, WebViewOptions options);

    void dismissViewControllerAnimated(boolean animated, OnCompleteCallback callback);

    Context getContext();

    void runOnUiThread(Runnable runnable);

    String getXSessionId();
}
