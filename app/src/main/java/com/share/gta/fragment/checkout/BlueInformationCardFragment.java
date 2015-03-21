package com.share.gta.fragment.checkout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.anypresence.masterpass_android_library.dto.Address;
import com.share.gta.GadgetShopApplication;
import com.share.gta.R;
import com.share.gta.activity.BaseActivity;
import com.share.gta.adapter.ShippingAddressAdapter;
import com.share.gta.dialog.SelectCardDialog;

import java.util.ArrayList;

/**
 * Created by diego.rotondale on 11/09/2014.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class BlueInformationCardFragment extends Fragment implements SelectCardDialog.Callback {
    private TextView selectCart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_blue_information, container, false);
        rootView.findViewById(R.id.checkout_select_cart_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectCardDialog dialog = new SelectCardDialog(getActivity(), BlueInformationCardFragment.this, getString(R.string.profile_dialog_title), getString(R.string.profile_learn_more_dialog_message));
                dialog.setCancelable(false);
                dialog.show();
            }
        });
        selectCart = (TextView) rootView.findViewById(R.id.checkout_select_cart);


        rootView.findViewById(R.id.cart_checkout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity activity = (BaseActivity) getActivity();
                activity.showProgress(getString(R.string.processing));
                activity.getMCLibrary().completeManualCheckout(GadgetShopApplication.getInstance().getOrder(), activity);
            }
        });
        //TODO: Copy from iOS version see if is necessary have this
        Spinner shippingAddress = (Spinner) rootView.findViewById(R.id.shipping_address);
        shippingAddress.setAdapter(new ShippingAddressAdapter(getActivity(), new ArrayList<Address>()));
        return rootView;
    }

    @Override
    public void onSelectCard(String cart) {
        selectCart.setText(cart);
    }
}
