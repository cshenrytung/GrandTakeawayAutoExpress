package com.share.gta.fragment.checkout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anypresence.masterpass_android_library.dto.CreditCard;
import com.share.gta.R;

/**
 * Created by diego.rotondale on 15/09/2014.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class CardFragment extends Fragment {

    public static final String CREDIT_CARD = "credit_card";
    public static String DRAWABLE = "drawable";

    int drawable;
    private CreditCard creditCard;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(DRAWABLE))
                drawable = getArguments().getInt(DRAWABLE);
            if (getArguments().containsKey(CREDIT_CARD))
                creditCard = (CreditCard) getArguments().getSerializable(CREDIT_CARD);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.card, container, false);
        ImageView image = (ImageView) view.findViewById(R.id.card);
        image.setImageDrawable(getResources().getDrawable(drawable));

        TextView number = (TextView) view.findViewById(R.id.card_number);
        TextView name = (TextView) view.findViewById(R.id.card_name);
        TextView exp = (TextView) view.findViewById(R.id.card_exp_date);
        if (creditCard == null) {
            name.setText(R.string.card_name_default);
            number.setText(R.string.card_number_default);
            exp.setText(R.string.card_exp_date_default);
        } else {
            name.setText(creditCard.cardHolderName);
            number.setText(getString(R.string.card_number) + " " + creditCard.lastFour);
            String expiryMonth = creditCard.expiryMonth;
            String expiryYear = creditCard.expiryYear;
            if (expiryMonth != null && expiryYear != null && !expiryMonth.isEmpty() && !expiryYear.isEmpty()) {
                exp.setText(expiryMonth + "/" + expiryYear);
            }
        }
        return view;
    }
}
