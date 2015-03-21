package com.anypresence.masterpass_android_library.interfaces;

/**
 * Created by diego.rotondale on 1/19/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public interface FutureCallback<V> {
    void onSuccess(V v);

    void onFailure(java.lang.Throwable throwable);
}
