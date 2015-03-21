package com.share.gta;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.anypresence.APSetup;
import com.anypresence.masterpass_android_library.dto.Order;
import com.anypresence.masterpass_android_library.dto.PairCheckoutResponse;
import com.anypresence.rails_droid.ObjectId;
import com.anypresence.sdk.config.Config;
import com.anypresence.sdk.gadget_app_sample.models.OrderHeader;
import com.anypresence.sdk.gadget_app_sample.models.User;
import com.share.gta.util.IECommerceManager;
import com.share.gta.volley.LruBitmapCache;

/**
 * Created by diego.rotondale on 10/09/2014.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class GadgetShopApplication extends Application implements IECommerceManager {
    private static GadgetShopApplication instance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private User user;
    private OrderHeader orderHeader;
    private Order order = new Order();
    private PairCheckoutResponse pairCheckout;

    public static GadgetShopApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        APSetup.setupOrm(getApplicationContext());
        APSetup.setup();
        Config.getInstance().setBaseUrl(MPConstants.BACKEND_URL);
        Config.getInstance().setAppUrl(MPConstants.BACKEND_URL + "/api/" + MPConstants.VERSION);
        Config.getInstance().setAuthUrl(MPConstants.BACKEND_URL + "/auth/password/callback");
        Config.getInstance().setDeauthUrl(MPConstants.BACKEND_URL + "/auth/signout");
        Config.getInstance().setStrictQueryFieldCheck(false);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue, new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    @Override
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public OrderHeader getOrderHeader() {
        return orderHeader;
    }

    @Override
    public void setOrderHeader(OrderHeader orderHeader) {
        this.orderHeader = orderHeader;
        order = new Order();
        if (orderHeader != null) {
            ObjectId orderHeaderId = orderHeader.getId();
            if (orderHeaderId != null)
                order.orderNumber = orderHeaderId.toString();
        }
    }

    public double getTotal() {
        double total = 0;
        if (orderHeader != null) {
            total = orderHeader.getTotal() / 100.0;
        }
        return total;
    }

    public double getSubtotal() {
        double subTotal = 0;
        if (orderHeader != null) {
            subTotal = orderHeader.getSubtotal() / 100.0;
        }
        return subTotal;
    }

    public double getTax() {
        double tax = 0;
        if (orderHeader != null) {
            tax = orderHeader.getTax() / 100.0;
        }
        return tax;
    }

    public double getShipping() {
        double shipping = 0;
        if (orderHeader != null) {
            shipping = orderHeader.getShipping() / 100.0;
        }
        return shipping;
    }

    public Order getOrder() {
        return order;
    }

    public PairCheckoutResponse getPairCheckout() {
        return pairCheckout;
    }

    public void setPairCheckout(PairCheckoutResponse pairCheckout) {
        this.pairCheckout = pairCheckout;
    }
}
