package com.share.gta.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.anypresence.masterpass_android_library.dto.PairCheckoutResponse;
import com.anypresence.masterpass_android_library.dto.PreCheckoutResponse;
import com.share.gta.MPConstants;
import com.share.gta.R;
import com.share.gta.fragment.CartFragment;

/**
 * Created by diego.rotondale on 10/09/2014.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class CartActivity extends BaseActivity implements CartFragment.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new CartFragment())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        restoreActionBar();
        return super.onCreateOptionsMenu(menu);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                    | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(R.string.products);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
    public void preCheckoutDidComplete(Boolean success, PairCheckoutResponse data, Throwable error) {
        super.preCheckoutDidComplete(success, data, error);
        if (success) {
            Intent intent = new Intent(this, CartCheckoutActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(MPConstants.PAIR_CHECKOUT_PARAMETER, data);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
