package com.share.gta.interfaces;

import com.anypresence.sdk.acl.IAuthenticatable;

/**
 * Created by diego.rotondale on 1/23/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public interface LoginListener {
    void onLoginFailed(Throwable ex);

    void onLoginSuccess(IAuthenticatable result);
}
