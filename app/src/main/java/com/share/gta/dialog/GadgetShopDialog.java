package com.share.gta.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.share.gta.R;

/**
 * Created by diego.rotondale on 11/09/2014.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class GadgetShopDialog extends Dialog {

    private String titleValue;
    private String contentValue;
    private Context context;

    public GadgetShopDialog(Context context, String titleValue, String contentValue) {
        super(context);
        this.context = context;
        this.titleValue = titleValue;
        this.contentValue = contentValue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ((TextView) this.findViewById(R.id.dialog_title)).setText(titleValue);
        ((TextView) this.findViewById(R.id.dialog_content)).setText(contentValue);

        this.findViewById(R.id.dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GadgetShopDialog.this.dismiss();
            }
        });
    }
}
