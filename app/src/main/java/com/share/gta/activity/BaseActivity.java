package com.share.gta.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.anypresence.masterpass_android_library.Constants;
import com.anypresence.masterpass_android_library.MPManager;
import com.anypresence.masterpass_android_library.activities.MPLightBox;
import com.anypresence.masterpass_android_library.dto.Order;
import com.anypresence.masterpass_android_library.dto.PairCheckoutResponse;
import com.anypresence.masterpass_android_library.dto.WebViewOptions;
import com.anypresence.masterpass_android_library.interfaces.IManager;
import com.anypresence.masterpass_android_library.interfaces.OnCompleteCallback;
import com.anypresence.masterpass_android_library.interfaces.ViewController;
import com.anypresence.rails_droid.IAPFutureCallback;
import com.anypresence.sdk.gadget_app_sample.models.User;
import com.share.gta.GadgetShopApplication;
import com.share.gta.MPConstants;
import com.share.gta.R;
import com.share.gta.util.MPECommerceManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by diego.rotondale on 1/23/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class BaseActivity extends FragmentActivity implements ViewController, IManager {

    protected ProgressDialog mConnectionProgressDialog;
    private String LOG_TAG = BaseActivity.class.getSimpleName();
    private IAPFutureCallback<?> saveUserCallback = new IAPFutureCallback<Object>() {
        @Override
        public void finished(Object o, Throwable throwable) {
            Log.e(LOG_TAG, throwable.toString());
        }

        @Override
        public void onSuccess(Object o) {
            Log.d(LOG_TAG, "saved");
        }

        @Override
        public void onFailure(Throwable throwable) {
            Log.e(LOG_TAG, throwable.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConnectionProgressDialog = new ProgressDialog(this, R.style.ProgressTheme);
        mConnectionProgressDialog.setMessage(getString(R.string.please_wait));
        mConnectionProgressDialog.setCancelable(false);
    }

    @Override
    public void presentViewController(Activity activity, Boolean animated, WebViewOptions options) {
        mConnectionProgressDialog.dismiss();
        Intent intent = new Intent(this, activity.getClass());
        Bundle bundle = new Bundle();
        bundle.putString(Constants.X_SESSION_ID, getXSessionId());
        bundle.putSerializable(Constants.OPTIONS_PARAMETER, options);
        intent.putExtras(bundle);
        startActivityForResult(intent, options.type.value);
    }

    public void callDone(Activity activity, Boolean animated) {
        mConnectionProgressDialog.dismiss();
        Intent intent = new Intent(this, activity.getClass());
        Bundle bundle = new Bundle();
        startActivityForResult(intent, 0);
    }

    @Override
    public void dismissViewControllerAnimated(boolean animated, OnCompleteCallback callback) {

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public String getXSessionId() {
        String xSessionId = "";
        User user = GadgetShopApplication.getInstance().getUser();
        if (user != null) {
            xSessionId = user.getXSessionId();
        }
        return xSessionId;
    }

    public void dismissProgress() {
        mConnectionProgressDialog.dismiss();
    }

    public void showProgress() {
        showProgress(getString(R.string.please_wait));
    }

    public void showProgress(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionProgressDialog.setMessage(message);
                mConnectionProgressDialog.show();
            }
        });
    }

    //IManager
    @Override
    public String getServerAddress() {
        return MPConstants.BACKEND_URL;
    }

    @Override
    public Boolean isAppPaired() {
        User user = GadgetShopApplication.getInstance().getUser();
        return user.getIsPaired();
    }

    @Override
    public Boolean isExpressCheckoutEnabled(){
        return true;
    }

    @Override
    public List<String> getSupportedDataTypes() {
        List<String> list = new ArrayList<String>();
        list.add(MPManager.DATA_TYPE_PROFILE);
        list.add(MPManager.DATA_TYPE_ADDRESS);
        list.add(MPManager.DATA_TYPE_CARD);
        return list;
    }

    @Override
    public List<String> getSupportedCardTypes() {
        List<String> list = new ArrayList<String>();
        list.add(MPManager.CARD_TYPE_MAESTRO);
        list.add(MPManager.CARD_TYPE_AMEX);
        list.add(MPManager.CARD_TYPE_DISCOVER);
        list.add(MPManager.CARD_TYPE_MASTERCARD);
        return list;
    }

    @Override
    public void pairingDidComplete(Boolean success, Throwable error) {
        Log.d(LOG_TAG, success ? "ConnectedMasterPass" : "MasterPassConnectionCancelled");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissProgress();
            }
        });
        setPairStatus(success);
    }

    public void setPairStatus(Boolean success) {
        User user = GadgetShopApplication.getInstance().getUser();
        user.setIsPaired(success);
    }

    @Override
    public void checkoutDidComplete(Boolean success, Throwable error) {
        Log.d(LOG_TAG, success ? "MasterPassCheckoutComplete" : "MasterPassCheckoutCancelled");
    }

    @Override
    public void preCheckoutDidComplete(Boolean success, PairCheckoutResponse data, Throwable error) {
        Log.d(LOG_TAG, success ? "MasterPassPreCheckoutComplete" : "MasterPassPreCheckoutCancelled");
        setPairStatus(success);
        setPairCheckout(data);
    }

    @Override
    public void pairCheckoutDidComplete(Boolean success, Throwable error) {
        Log.d(LOG_TAG, success ? "MasterPassPairCheckoutComplete" : "MasterPassPairCheckoutCancelled");
        dismissProgress();
        if (success) {
            Intent intent = new Intent(this, CheckoutActivity.class);
            intent.putExtra(MPConstants.PAIR_PARAMETER, true);
            startActivity(intent);
        }
    }

    @Override
    public void manualCheckoutDidComplete(Boolean success, Throwable error) {
        if (success) {
            Intent intent = new Intent(this, CheckoutActivity.class);
            intent.putExtra(MPConstants.PAIR_PARAMETER, false);
            startActivity(intent);
        }
    }

    @Override
    public void resetUserPairing() {
        setPairStatus(false);
    }

    public MPManager getMCLibrary() {
        MPManager mpManager = MPManager.getInstance();
        mpManager.setDelegate(this);
        return mpManager;
    }

    public MPECommerceManager getMPECommerceManager() {
        MPECommerceManager mpeCommerceManager = MPECommerceManager.getInstance();
        mpeCommerceManager.setDelegate(GadgetShopApplication.getInstance());
        return mpeCommerceManager;
    }

    protected void dismissOnUiThread() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionProgressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean status = resultCode == RESULT_OK;
        Serializable result = null;
        if (data != null)
            result = data.getSerializableExtra(Constants.LIGHT_BOX_EXTRA);
        if (requestCode == MPLightBox.MPLightBoxType.MPLightBoxTypeConnect.value) {
            getMCLibrary().pairingViewDidCompletePairing(this, status, null);
        }
        if (requestCode == MPLightBox.MPLightBoxType.MPLightBoxTypeCheckout.value) {
            getMCLibrary().lightBoxDidCompleteCheckout(this, status, null);
        }
        if (requestCode == MPLightBox.MPLightBoxType.MPLightBoxTypePreCheckout.value) {
            getMCLibrary().lightBoxDidCompletePreCheckout(this, status, (PairCheckoutResponse) result, null);
        }
    }

    public void setPairCheckout(PairCheckoutResponse pairCheckout) {
        GadgetShopApplication gadgetShopApplication = GadgetShopApplication.getInstance();
        gadgetShopApplication.setPairCheckout(pairCheckout);
        Order order = gadgetShopApplication.getOrder();
        if (pairCheckout != null && pairCheckout.checkout != null) {
            order.transactionId = pairCheckout.checkout.transactionId;
            order.preCheckoutTransactionId = pairCheckout.checkout.preCheckoutTransactionId;
        }
    }
}
