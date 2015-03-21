package com.share.gta.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.anypresence.masterpass_android_library.dto.Order;
import com.anypresence.masterpass_android_library.dto.PreCheckoutResponse;
import com.anypresence.masterpass_android_library.interfaces.FutureCallback;
import com.anypresence.rails_droid.IAPFutureCallback;
import com.anypresence.sdk.gadget_app_sample.models.OrderDetail;
import com.share.gta.GadgetShopApplication;
import com.share.gta.R;
import com.share.gta.activity.BaseActivity;
import com.share.gta.activity.GTAExpressActivity;
import com.share.gta.adapter.CartAdapter;
import com.share.gta.util.CurrencyUtil;
import com.share.gta.view.EmptyLoadingView;

import java.util.List;

/**
 * Created by diego.rotondale on 10/09/2014.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class CartFragment extends Fragment {
    private static String LOG_TAG = CartFragment.class.getSimpleName();
    private IAPFutureCallback<List<OrderDetail>> currentCartCallback = new IAPFutureCallback<List<OrderDetail>>() {
        @Override
        public void finished(List<OrderDetail> orderDetails, Throwable throwable) {
            Log.e(LOG_TAG, throwable.toString());
            emptyLoadingView.setErrorView();
        }

        @Override
        public void onSuccess(final List<OrderDetail> orderDetails) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    emptyLoadingView.setEmptyView();
                    adapter.addAll(orderDetails);
                    if (!orderDetails.isEmpty())
                        cartCheckoutContainer.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public void onFailure(Throwable throwable) {
            Log.e(LOG_TAG, throwable.toString());
            emptyLoadingView.setErrorView();
        }
    };
    private boolean checkoutAvailable = true;
    private CartAdapter adapter;
    private EmptyLoadingView emptyLoadingView;
    private View connect;
    private View or;
    private Callback mCallback;
    private PreCheckoutResponse preCheckoutData;
    private boolean waitingForCheckout = false;

    private FutureCallback<PreCheckoutResponse> preCheckoutCallback = new FutureCallback<PreCheckoutResponse>() {
        @Override
        public void onSuccess(PreCheckoutResponse preCheckoutResponse) {
            ((BaseActivity) getActivity()).setPairStatus(true);
            preCheckoutData = preCheckoutResponse;
            Order order = GadgetShopApplication.getInstance().getOrder();
            order.preCheckoutTransactionId = preCheckoutResponse.walletInfo.preCheckoutTransactionId;
            order.walletInfo = preCheckoutResponse.walletInfo;
            order.card = preCheckoutResponse.getDefaultCard();
            order.shippingAddress = preCheckoutResponse.getDefaultAddress();
            order.transactionId = preCheckoutResponse.transactionId;

            if (waitingForCheckout) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((BaseActivity) getActivity()).dismissProgress();
                        mCallback.openCartCheckout(preCheckoutData);
                    }
                });
            }
            waitingForCheckout = false;
            checkoutAvailable = true;
        }

        @Override
        public void onFailure(Throwable throwable) {
            Log.e(LOG_TAG, throwable.toString());
            waitingForCheckout = false;
            checkoutAvailable = true;
            if (getActivity() != null && !getActivity().isFinishing()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((BaseActivity) getActivity()).dismissProgress();
                        checkout.setVisibility(View.GONE);
                    }
                });
            }

        }
    };
    private View checkout;
    private View cartCheckoutContainer;

    public CartFragment() {
    }

    public static CartFragment newInstance(int sectionNumber) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putInt(GTAExpressActivity.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);
        ListView list = (ListView) rootView.findViewById(R.id.cart_list_view);

        emptyLoadingView = (EmptyLoadingView) rootView.findViewById(R.id.empty_loading_view);
        BaseActivity activity = (BaseActivity) getActivity();
        activity.getMPECommerceManager().getCurrentCart(currentCartCallback);
        adapter = new CartAdapter(activity);
        list.setEmptyView(emptyLoadingView);
        list.setAdapter(adapter);

        TextView total = (TextView) rootView.findViewById(R.id.cart_total_value);
        total.setText(CurrencyUtil.getStringValue(GadgetShopApplication.getInstance().getSubtotal()));

        connect = rootView.findViewById(R.id.cart_masterpass);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity activity = (BaseActivity) getActivity();
                activity.showProgress();
                activity.getMCLibrary().pairCheckoutForOrder(GadgetShopApplication.getInstance().getOrder(), activity);
            }
        });
        or = rootView.findViewById(R.id.cart_or);

        cartCheckoutContainer = rootView.findViewById(R.id.cart_checkout_container);
        checkout = rootView.findViewById(R.id.cart_checkout);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BaseActivity activity = (BaseActivity) getActivity();
                if (activity.isAppPaired()) {
                    if (checkoutAvailable)
                        mCallback.openCartCheckout(preCheckoutData);
                    else {
                        waitingForCheckout = true;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activity.showProgress();
                            }
                        });
                    }
                } else
                    mCallback.openCartCheckout();
            }
        });
        if (activity.isAppPaired()) {
            checkoutAvailable = false;
            activity.getMCLibrary().preCheckout(activity, preCheckoutCallback);
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!((BaseActivity) getActivity()).isAppPaired()) {
            connect.setVisibility(View.VISIBLE);
            or.setVisibility(View.VISIBLE);
        } else {
            connect.setVisibility(View.GONE);
            or.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof GTAExpressActivity) {
            ((GTAExpressActivity) activity).onSectionAttached(
                    getArguments().getInt(GTAExpressActivity.ARG_SECTION_NUMBER));
        }
        mCallback = (Callback) activity;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.global, menu);
        showSettingsContextActionBar();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void showSettingsContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(R.string.cart);
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    public interface Callback {
        void openCartCheckout();

        void openCartCheckout(PreCheckoutResponse preCheckoutData);
    }
}
