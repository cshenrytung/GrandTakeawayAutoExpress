package com.share.gta.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anypresence.rails_droid.IAPFutureCallback;
import com.anypresence.sdk.gadget_app_sample.models.Product;
import com.share.gta.R;
import com.share.gta.activity.AnimationActivity;
import com.share.gta.activity.BaseActivity;
import com.share.gta.activity.GTAExpressActivity;
import com.share.gta.adapter.ProductAdapter;
import com.share.gta.domain.dto.ToCart;
import com.share.gta.util.MPECommerceManager;
import com.share.gta.view.EmptyLoadingView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by diego.rotondale on 09/09/2014.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class HomeFragment extends Fragment implements ProductAdapter.IProduct {

    private static String LOG_TAG = HomeFragment.class.getSimpleName();
    //Callback
    private IAPFutureCallback<List<Product>> allProductsCallback = new IAPFutureCallback<List<Product>>() {
        @Override
        public void finished(List<Product> products, Throwable throwable) {
            Log.e(LOG_TAG, throwable.getMessage());
            emptyLoadingView.setErrorView();
        }

        @Override
        public void onSuccess(List<Product> products) {
            emptyLoadingView.setEmptyView();
            addAllProducts(products);

        }

        @Override
        public void onFailure(Throwable throwable) {
            Log.e(LOG_TAG, throwable.toString());
            emptyLoadingView.setErrorView();
        }
    };
    private IAPFutureCallback<Integer> cartQuantityCallback = new IAPFutureCallback<Integer>() {
        @Override
        public void finished(Integer integer, Throwable throwable) {
            Log.e(LOG_TAG, throwable.toString());
            showErrorNotification();
        }

        @Override
        public void onSuccess(final Integer size) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (size != 0) {
                        notification.setVisibility(View.VISIBLE);
                        notification.setText(String.valueOf(size));
                    } else
                        notification.setVisibility(View.GONE);
                    notificationProgress.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void onFailure(Throwable throwable) {
            Log.e(LOG_TAG, throwable.toString());
            showErrorNotification();
        }
    };
    private ProductAdapter adapter;
    private TextView notification;
    private Callback mCallback;
    private RadioGroup radioGroup;
    private EmptyLoadingView emptyLoadingView;
    private ProgressBar notificationProgress;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(int sectionNumber) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(GTAExpressActivity.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private void showErrorNotification() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notificationProgress.setVisibility(View.GONE);
                    notification.setVisibility(View.GONE);
                    notification.setText("");
                }
            });
        }
    }

    private void addAllProducts(final List<Product> products) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.addAll(products);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initializeGridView(rootView);
        initializeHeader(rootView);
        return rootView;
    }

    private void initializeHeader(View rootView) {
        radioGroup = (RadioGroup) rootView.findViewById(R.id.home_header);
        radioGroup.check(R.id.home_rb_1);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.home_rb_1:
                        adapter.getFilter().filter(null);
                        break;
                    case R.id.home_rb_2:
                        adapter.getFilter().filter(ProductAdapter.SECOND);
                        break;
                    case R.id.home_rb_3:
                        adapter.getFilter().filter(ProductAdapter.THIRD);
                        break;
                    case R.id.home_rb_4:
                        adapter.getFilter().filter(ProductAdapter.FOURTH);
                        break;
                }
            }
        });
    }

    private void initializeGridView(View rootView) {
        GridView grid = (GridView) rootView.findViewById(R.id.home_grid);
        emptyLoadingView = (EmptyLoadingView) rootView.findViewById(R.id.empty_loading_view);
        adapter = new ProductAdapter(getActivity(), new ArrayList<Product>());
        grid.setEmptyView(emptyLoadingView);
        grid.setAdapter(adapter);
        MPECommerceManager.getInstance().getAllProducts(allProductsCallback);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((GTAExpressActivity) activity).onSectionAttached(
                getArguments().getInt(GTAExpressActivity.ARG_SECTION_NUMBER));
        mCallback = (Callback) activity;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
        final Menu m = menu;
        final MenuItem item = menu.findItem(R.id.action_cart);

        RelativeLayout badgeLayout = (RelativeLayout) item.getActionView();
        notification = (TextView) badgeLayout.findViewById(R.id.notification);
        notificationProgress = (ProgressBar) badgeLayout.findViewById(R.id.notification_progress);
        item.getActionView().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                m.performIdentifierAction(item.getItemId(), 0);
            }

        });
        updateMenuItemStatus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_cart:
                mCallback.openCart();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void addProduct(ToCart toCart) {
        updateMenuItemStatus();
        Intent intent = new Intent(getActivity(), AnimationActivity.class);
        Bundle b = new Bundle();
        b.putFloat(AnimationActivity.POSITION_X, toCart.x);
        b.putFloat(AnimationActivity.POSITION_Y, toCart.y);
        intent.putExtras(b);
        startActivity(intent);
    }

    private void updateMenuItemStatus() {
        if (notification != null) {
            notification.setVisibility(View.VISIBLE);
            notification.setText("");
            notificationProgress.setVisibility(View.VISIBLE);
            ((BaseActivity) getActivity()).getMPECommerceManager().getCartQuantity(cartQuantityCallback);
        }
    }

    public interface Callback {
        void openCart();
    }
}
