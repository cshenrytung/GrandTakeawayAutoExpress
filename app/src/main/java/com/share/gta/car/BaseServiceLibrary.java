package com.share.gta.car;

import android.content.Context;
import android.util.Log;

import com.anypresence.masterpass_android_library.MPManager;
import com.anypresence.masterpass_android_library.interfaces.ViewController;
import com.anypresence.sdk.gadget_app_sample.models.User;
import com.share.gta.GadgetShopApplication;
import com.share.gta.activity.BaseActivity;

/**
 * Created by Edmund on 22/3/2015.
 */
public class BaseServiceLibrary {

    public static Boolean isAppPaired() {
        Log.d("BaseServiceLibrary", "GadgetShopApplication.getInstance:" + GadgetShopApplication.getInstance());
        User user = GadgetShopApplication.getInstance().getUser();
        return user.getIsPaired();
    }

    public static Boolean isExpressCheckoutEnabled(){
        return true;
    }

    public static MPManager getMCLibrary(BaseActivity baseActivity) {
        MPManager mpManager = MPManager.getInstance();
        mpManager.setDelegate(baseActivity);
        return mpManager;
    }

    public static ViewController getViewController() {
        return new BaseActivity();
    }
}
