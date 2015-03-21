package com.share.gta.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.share.gta.R;

/**
 * Created by diego.rotondale on 11/09/2014.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class SelectCardDialog extends Dialog {

    String titleValue;
    String contentValue;
    SelectCardDialog.Callback callback;
    private Context context;

    public SelectCardDialog(Context context, SelectCardDialog.Callback callback, String titleValue, String contentValue) {
        super(context);
        this.context = context;
        this.callback = callback;
        this.titleValue = titleValue;
        this.contentValue = contentValue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_select_card);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.findViewById(R.id.dialog_mastercard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onSelectCard(context.getString(R.string.dialog_card_mastercard));
                SelectCardDialog.this.dismiss();
            }
        });
        this.findViewById(R.id.dialog_visa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onSelectCard(context.getString(R.string.dialog_card_visa));
                SelectCardDialog.this.dismiss();
            }
        });
        this.findViewById(R.id.dialog_discover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onSelectCard(context.getString(R.string.dialog_card_discover));
                SelectCardDialog.this.dismiss();
            }
        });
        this.findViewById(R.id.dialog_american_express).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onSelectCard(context.getString(R.string.dialog_card_american_express));
                SelectCardDialog.this.dismiss();
            }
        });
        this.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectCardDialog.this.dismiss();
            }
        });
    }

    public interface Callback {
        void onSelectCard(String cart);
    }
}
