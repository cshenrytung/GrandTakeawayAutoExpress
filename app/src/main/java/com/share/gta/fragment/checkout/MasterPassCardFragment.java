package com.share.gta.fragment.checkout;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.anypresence.masterpass_android_library.dto.Address;
import com.anypresence.masterpass_android_library.dto.CreditCard;
import com.anypresence.masterpass_android_library.dto.Order;
import com.share.gta.GadgetShopApplication;
import com.share.gta.MPConstants;
import com.share.gta.R;
import com.share.gta.activity.BaseActivity;
import com.share.gta.adapter.ShippingAddressAdapter;
import com.share.gta.view.LogoImage;

import java.util.List;


/**
 * Created by diego.rotondale on 11/09/2014.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class MasterPassCardFragment extends Fragment {

    public static final String CHECKOUT = "checkout";

    ProgressDialog progress;
    private List<Address> addresses;
    private AdapterView.OnItemSelectedListener onClickShipping = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Order order = GadgetShopApplication.getInstance().getOrder();
            order.shippingAddress = shippingAddressAdapter.getItem(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
    private ShippingAddressAdapter shippingAddressAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_masterpass, container, false);
        View checkout = rootView.findViewById(R.id.cart_checkout);
        View masterPass = rootView.findViewById(R.id.cart_masterpass);
        Spinner shippingAddress = (Spinner) rootView.findViewById(R.id.shipping_address);

        final Bundle arguments = getArguments();

        boolean partnerLogo = arguments.containsKey(MPConstants.WALLET_PARTNER_LOGO_URL);
        if (partnerLogo) {
            LogoImage partner = (LogoImage) rootView.findViewById(R.id.masterpass_bank_icon);
            partner.setProgress(rootView.findViewById(R.id.masterpass_bank_icon_progress));
            partner.setImageUrl(arguments.getString(MPConstants.WALLET_PARTNER_LOGO_URL), GadgetShopApplication.getInstance().getImageLoader());
        }
        boolean walletLogo = arguments.containsKey(MPConstants.MASTERPASS_LOGO_URL);
        if (walletLogo) {
            LogoImage masterpass = (LogoImage) rootView.findViewById(R.id.masterpass_logo);
            masterpass.setProgress(rootView.findViewById(R.id.masterpass_logo_progress));
            masterpass.setImageUrl(arguments.getString(MPConstants.MASTERPASS_LOGO_URL), GadgetShopApplication.getInstance().getImageLoader());
        }

        if (!partnerLogo && !walletLogo) {
            rootView.findViewById(R.id.logo_container).setVisibility(View.GONE);
            rootView.findViewById(R.id.masterpass_bank_icon_progress).setVisibility(View.GONE);
            rootView.findViewById(R.id.masterpass_logo_progress).setVisibility(View.GONE);
            rootView.findViewById(R.id.logo_masterpass).setVisibility(View.VISIBLE);
        }

        if (arguments.getBoolean(MPConstants.PAIR_PARAMETER, false)) {
            checkout.setVisibility(View.VISIBLE);
            masterPass.setVisibility(View.GONE);
        } else {
            masterPass.setVisibility(View.VISIBLE);
            checkout.setVisibility(View.GONE);
        }

        if (arguments.containsKey(MPConstants.ADDRESSES_PARAMETER)) {
            addresses = (List<Address>) arguments.get(MPConstants.ADDRESSES_PARAMETER);
            shippingAddressAdapter = new ShippingAddressAdapter(getActivity(), addresses);
            shippingAddress.setAdapter(shippingAddressAdapter);
            shippingAddress.setOnItemSelectedListener(onClickShipping);
        }

        progress = new ProgressDialog(getActivity(), R.style.ProgressTheme);
        progress.setIndeterminate(true);
        progress.setMessage(getString(R.string.processing));

        masterPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity baseActivity = (BaseActivity) getActivity();
                boolean pairing = baseActivity.isAppPaired();
                if (pairing) {

                    baseActivity.showProgress();
                    Order order = GadgetShopApplication.getInstance().getOrder();
                    order.card = (CreditCard) arguments.get(MPConstants.CARD_PARAMETER);

                    if (baseActivity.isExpressCheckoutEnabled()){
                        baseActivity.getMCLibrary().expressCheckoutForOrder(order, baseActivity);
                    }
                    else {
                        baseActivity.getMCLibrary().returnCheckout(order, baseActivity);
                    }
                }
            }
        });
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                BaseActivity baseActivity = (BaseActivity) getActivity();
                Order order = GadgetShopApplication.getInstance().getOrder();
                order.card = (CreditCard) arguments.get(MPConstants.CARD_PARAMETER);
                baseActivity.getMCLibrary().completePairCheckoutForOrder(order, baseActivity);
            }
        });


        return rootView;
    }
}
