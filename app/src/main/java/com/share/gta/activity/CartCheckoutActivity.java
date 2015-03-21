package com.share.gta.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.anypresence.masterpass_android_library.MPManager;
import com.anypresence.masterpass_android_library.dto.CreditCard;
import com.anypresence.masterpass_android_library.dto.PairCheckoutResponse;
import com.anypresence.masterpass_android_library.dto.PreCheckoutResponse;
import com.share.gta.GadgetShopApplication;
import com.share.gta.MPConstants;
import com.share.gta.R;
import com.share.gta.fragment.checkout.BlueInformationCardFragment;
import com.share.gta.fragment.checkout.CardFragment;
import com.share.gta.fragment.checkout.MasterPassCardFragment;
import com.share.gta.util.CurrencyUtil;

/**
 * Created by diego.rotondale on 10/09/2014.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class CartCheckoutActivity extends BaseActivity {
    private ViewPager mCartPager;
    private PairCheckoutResponse pairCheckoutData;
    private int MANUAL_CHECKOUT = 1;
    private PreCheckoutResponse preCheckoutData;
    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            mCartPager.setCurrentItem(position, false);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };
    private int countOfCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_checkout);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(MPConstants.PAIR_CHECKOUT_PARAMETER))
                pairCheckoutData = (PairCheckoutResponse) extras.getSerializable(MPConstants.PAIR_CHECKOUT_PARAMETER);
            if (extras.containsKey(MPConstants.PRE_CHECKOUT_PARAMETER))
                preCheckoutData = (PreCheckoutResponse) extras.getSerializable(MPConstants.PRE_CHECKOUT_PARAMETER);
        }

        double subTotalPrice = GadgetShopApplication.getInstance().getSubtotal();
        double totalPrice = GadgetShopApplication.getInstance().getTotal();
        double tax = GadgetShopApplication.getInstance().getTax();
        double shipping = GadgetShopApplication.getInstance().getShipping();
        ((TextView) findViewById(R.id.cart_checkout_items)).setText(CurrencyUtil.getStringValue(subTotalPrice));
        ((TextView) findViewById(R.id.cart_checkout_tax)).setText(CurrencyUtil.getStringValue(tax));
        ((TextView) findViewById(R.id.cart_checkout_shipping)).setText(CurrencyUtil.getStringValue(shipping));
        ((TextView) findViewById(R.id.cart_checkout_total)).setText(CurrencyUtil.getStringValue(totalPrice));

        if (preCheckoutData == null) {
            countOfCards = MANUAL_CHECKOUT;
        } else
            countOfCards = preCheckoutData.cards.size() + MANUAL_CHECKOUT;
        ViewPager mCartHeaderPager = (ViewPager) findViewById(R.id.card_header_pager);
        CardCheckoutHeaderAdapter mCardCheckoutHeaderAdapter = new CardCheckoutHeaderAdapter(getSupportFragmentManager());
        mCartHeaderPager.setAdapter(mCardCheckoutHeaderAdapter);
        mCartHeaderPager.setOnPageChangeListener(onPageChangeListener);

        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -175, getResources().getDisplayMetrics());
        mCartHeaderPager.setPageMargin(Math.round(px));

        mCartPager = (ViewPager) findViewById(R.id.card_information_pager);
        CollectionPagerAdapter mCollectionPagerAdapter = new CollectionPagerAdapter(getSupportFragmentManager());
        mCartPager.setAdapter(mCollectionPagerAdapter);
        mCartPager.setOffscreenPageLimit(countOfCards);
        mCartHeaderPager.setOffscreenPageLimit(countOfCards);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        restoreActionBar();
        return super.onCreateOptionsMenu(menu);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(R.string.cart);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return super.onCreateView(parent, name, context, attrs);
    }

    public int cardImageForCardType(String cardType, String lastFour) {

        // We can't just do a random card because when the view
        // in the slider is recycled and/or reloaded we will
        // get a new image.

        // So we are going to use the last four as a 'seed' and
        // decide which image to use based on whether it is odd
        // or even. That way, we get the same image each time.
        Integer lastFourValue = Integer.valueOf(lastFour);
        int index = lastFourValue % 2;
        if (cardType != null) {
            if (cardType.equals(MPManager.CARD_TYPE_AMEX)) {
                switch (index) {
                    case 0:
                        return R.drawable.amex_black;
                    case 1:
                        return R.drawable.amex_blue;
                }
            }
            if (cardType.equals(MPManager.CARD_TYPE_DISCOVER)) {
                switch (index) {
                    case 0:
                        return R.drawable.discover_grey;
                    case 1:
                        return R.drawable.discover_orange;
                }
            }
            if (cardType.equals(MPManager.CARD_TYPE_MASTERCARD)) {
                switch (index) {
                    case 0:
                        return R.drawable.mastercard_blue;
                    case 1:
                        return R.drawable.mastercard_green;
                }
            }
            if (cardType.equals(MPManager.CARD_TYPE_VISA)) {
                switch (index) {
                    case 0:
                        return R.drawable.visa_blue;
                    case 1:
                        return R.drawable.visa_red;
                }
            }
            if (cardType.equals(MPManager.CARD_TYPE_MAESTRO)) {
                switch (index) {
                    case 0:
                        return R.drawable.maestro_blue;
                    case 1:
                        return R.drawable.maestro_yellow;
                }
            }
        }
        // Some Generic Card
        index = lastFourValue % 3;
        switch (index) {
            case 0:
                return R.drawable.generic_card_blue;
            case 1:
                return R.drawable.generic_card_green;
            case 2:
                return R.drawable.generic_card_orange;
        }
        //Never must come here
        return R.drawable.blue_card;
    }

    @Override
    public void checkoutDidComplete(Boolean success, Throwable error) {
        super.checkoutDidComplete(success, error);
        if(success){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(CartCheckoutActivity.this, CheckoutActivity.class);
                    intent.putExtra(MPConstants.PAIR_PARAMETER, true);
                    startActivity(intent);
                }
            });
        }
    }

    public class CollectionPagerAdapter extends FragmentPagerAdapter {

        public CollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            MasterPassCardFragment fragment = new MasterPassCardFragment();
            Bundle args = new Bundle();
            if (pairCheckoutData != null) {
                args.putBoolean(MPConstants.PAIR_PARAMETER, true);
                args.putSerializable(MPConstants.ADDRESSES_PARAMETER, pairCheckoutData.getShippingAddresses());
                //args.putString(Constants.MASTERPASS_LOGO_URL);
                //args.putString(Constants.WALLET_PARTNER_LOGO_URL);
                args.putSerializable(MPConstants.CARD_PARAMETER, pairCheckoutData.checkout.card);
                args.putBoolean(MPConstants.PAIR_PARAMETER, true);
            }
            if (preCheckoutData != null && position != countOfCards - 1) {
                args.putBoolean(MPConstants.PAIR_PARAMETER, false);
                args.putSerializable(MPConstants.ADDRESSES_PARAMETER, (java.io.Serializable) preCheckoutData.addresses);
                args.putString(MPConstants.MASTERPASS_LOGO_URL, preCheckoutData.walletInfo.masterpassLogoUrl);
                args.putString(MPConstants.WALLET_PARTNER_LOGO_URL, preCheckoutData.walletInfo.walletPartnerLogoUrl);
                args.putSerializable(MPConstants.CARD_PARAMETER, preCheckoutData.cards.get(position));
            }
            if (position == 0) {
                if (getCount() == MANUAL_CHECKOUT && pairCheckoutData == null) {
                    return new BlueInformationCardFragment();
                } else {
                    fragment.setArguments(args);
                    return fragment;
                }
            }
            if (position == countOfCards - 1) {
                return new BlueInformationCardFragment();
            }

            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            boolean pairing = isAppPaired();
            if (pairCheckoutData != null)
                return MANUAL_CHECKOUT;
            if (pairing)
                return countOfCards;
            return MANUAL_CHECKOUT;
        }
    }

    public class CardCheckoutHeaderAdapter extends FragmentPagerAdapter {

        public CardCheckoutHeaderAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            CardFragment fragment = new CardFragment();
            Bundle args = new Bundle();
            if (countOfCards - 1 != position) {
                CreditCard creditCard = preCheckoutData.cards.get(position);
                args.putInt(CardFragment.DRAWABLE, cardImageForCardType(creditCard.brandId, creditCard.lastFour));
                args.putSerializable(CardFragment.CREDIT_CARD, creditCard);
            } else {
                if (pairCheckoutData != null) {
                    CreditCard creditCard = pairCheckoutData.checkout.card;
                    args.putInt(CardFragment.DRAWABLE, cardImageForCardType(creditCard.brandId, creditCard.lastFour));
                    args.putSerializable(CardFragment.CREDIT_CARD, creditCard);
                } else
                    args.putInt(CardFragment.DRAWABLE, R.drawable.blue_card);
            }
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            boolean pairing = isAppPaired();
            if (pairCheckoutData != null)
                return MANUAL_CHECKOUT;
            if (pairing)
                return countOfCards;
            else
                return MANUAL_CHECKOUT;
        }
    }
}