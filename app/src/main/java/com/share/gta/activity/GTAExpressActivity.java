package com.share.gta.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.anypresence.masterpass_android_library.dto.PreCheckoutResponse;
import com.anypresence.masterpass_android_library.xml.StackOverflowXmlParser;
import com.anypresence.rails_droid.IAPFutureCallback;
import com.anypresence.sdk.gadget_app_sample.models.OrderDetail;
import com.share.gta.MPConstants;
import com.share.gta.R;
import com.share.gta.adapter.ProductAdapter;
import com.share.gta.dialog.GadgetShopDialog;
import com.share.gta.domain.dto.ToCart;
import com.share.gta.fragment.CartFragment;
import com.share.gta.fragment.HomeFragment;
import com.share.gta.fragment.LocationFragment;
import com.share.gta.fragment.NavigationDrawerFragment;
import com.share.gta.fragment.ProfileFragment;
import com.share.gta.helper.LocationHelper_;

import java.util.List;

/**
 * Created by diego.rotondale on 1/23/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */

public class GTAExpressActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, ProductAdapter.IProduct, HomeFragment.Callback, CartFragment.Callback {
    public static final String ARG_SECTION_NUMBER = "section_number";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private ToCart toCart;
    private IAPFutureCallback<List<OrderDetail>> addProductCallback = new IAPFutureCallback<List<OrderDetail>>() {
        @Override
        public void finished(List<OrderDetail> orderDetails, Throwable throwable) {
            dismissOnUiThread();
        }

        @Override
        public void onSuccess(List<OrderDetail> orderDetails) {
            dismissProgress();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Fragment fragmentActive = getFragmentManager().findFragmentById(R.id.container);
                    if (fragmentActive instanceof ProductAdapter.IProduct) {
                        ((ProductAdapter.IProduct) fragmentActive).addProduct(toCart);
                    }
                }
            });
        }

        @Override
        public void onFailure(Throwable throwable) {
            dismissOnUiThread();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gadget_shop);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment fragment;
        FragmentManager fm = getFragmentManager();
        int sectionNumber = position + 1;
        switch (position) {
            case 0:
                fragment = HomeFragment.newInstance(sectionNumber);
                break;
            case 1:
                fragment = CartFragment.newInstance(sectionNumber);
                break;
            case 2:
                fragment = ProfileFragment.newInstance(sectionNumber);
                break;
            case 3:
                fragment = HomeFragment.newInstance(sectionNumber);
                requestRestaurantList();
                break;
            default:
                fragment = HomeFragment.newInstance(sectionNumber);
                break;
        }
        fm.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void requestRestaurantList() {
        BaseActivity activity = (BaseActivity) this;
        activity.showProgress();
        activity.getMCLibrary().restaurantList(activity, LocationHelper_.getInstance_(getContext()).getLat(), LocationHelper_.getInstance_(getContext()).getLon());
    }

    @Override
    public void callStartLocationFragment(Activity activity, List<StackOverflowXmlParser.Entry> details) {
        super.callStartLocationFragment(activity, details);

        Fragment fragment;
        FragmentManager fm = getFragmentManager();
        LocationFragment lf = LocationFragment.newInstance(4);
        lf.setDetail(details);
        fragment = lf;

        fm.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.home);
                break;
            case 2:
                mTitle = getString(R.string.cart);
                break;
            case 3:
                mTitle = getString(R.string.profile);
                break;
            case 4:
                mTitle = "Restaurants";
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                    | ActionBar.DISPLAY_SHOW_HOME);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setIcon(R.drawable.ic_logo);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setTitle(mTitle);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void addProduct(ToCart toCart) {
        this.toCart = toCart;
        showProgress(getString(R.string.adding_product_message));
        getMPECommerceManager().addProductToCart(toCart.product, addProductCallback);
    }

    @Override
    public void openCart() {
        startActivity(new Intent(this, CartActivity.class));
    }

    @Override
    public void openCartCheckout() {
        startActivity(new Intent(this, CartCheckoutActivity.class));
    }

    @Override
    public void openCartCheckout(PreCheckoutResponse preCheckoutData) {
        Intent intent = new Intent(this, CartCheckoutActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MPConstants.PRE_CHECKOUT_PARAMETER, preCheckoutData);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void pairingDidComplete(Boolean success, Throwable error) {
        super.pairingDidComplete(success, error);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Fragment fragmentActive = getFragmentManager().findFragmentById(R.id.container);
                if (fragmentActive instanceof ProfileFragment) {
                    GadgetShopDialog dialog = new GadgetShopDialog(GTAExpressActivity.this, getString(R.string.profile_dialog_title), getString(R.string.profile_dialog_message));
                    dialog.setCancelable(false);
                    dialog.show();
                }
            }
        });
    }

}
