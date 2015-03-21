package com.share.gta.activity;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.anypresence.rails_droid.IAPFutureCallback;
import com.anypresence.sdk.gadget_app_sample.models.OrderDetail;
import com.share.gta.MPConstants;
import com.share.gta.GadgetShopApplication;
import com.share.gta.R;
import com.share.gta.adapter.CartAdapter;
import com.share.gta.util.CurrencyUtil;
import com.share.gta.view.EmptyLoadingView;

import java.util.List;

/**
 * Created by diego.rotondale on 19/09/2014.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class CheckoutActivity extends BaseActivity {
    private static String LOG_TAG = CheckoutActivity.class.getSimpleName();

    private IAPFutureCallback<List<OrderDetail>> currentCartCallback = new IAPFutureCallback<List<OrderDetail>>() {
        @Override
        public void finished(List<OrderDetail> orderDetails, Throwable throwable) {
            Log.e(LOG_TAG, throwable.toString());
            emptyLoadingView.setErrorView();
        }

        @Override
        public void onSuccess(final List<OrderDetail> orderDetails) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    emptyLoadingView.setEmptyView();
                    adapter.addAll(orderDetails);
                }
            });
        }

        @Override
        public void onFailure(Throwable throwable) {
            Log.e(LOG_TAG, throwable.toString());
            emptyLoadingView.setErrorView();
        }
    };
    private EmptyLoadingView emptyLoadingView;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        findViewById(R.id.checkout_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        emptyLoadingView = (EmptyLoadingView) findViewById(R.id.empty_loading_view);
        this.getMPECommerceManager().getCurrentCart(currentCartCallback);
        ListView list = (ListView) findViewById(R.id.checkout_list);
        adapter = new CartAdapter(this);
        list.setEmptyView(emptyLoadingView);
        list.setAdapter(adapter);

        double totalPrice = GadgetShopApplication.getInstance().getTotal();
        ((TextView) findViewById(R.id.checkout_total_value)).setText(
                CurrencyUtil.getStringValue(totalPrice));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            View view = findViewById(R.id.checkout_purchased);
            if (extras.getBoolean(MPConstants.PAIR_PARAMETER, false)) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        GadgetShopApplication.getInstance().setOrderHeader(null);
        NavUtils.navigateUpFromSameTask(this);
    }
}
