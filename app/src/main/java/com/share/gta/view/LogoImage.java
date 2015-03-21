package com.share.gta.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;

import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by diego.rotondale on 2/9/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class LogoImage extends NetworkImageView {
    View progress;

    public LogoImage(Context context) {
        super(context);
    }

    public LogoImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LogoImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (bm != null && getProgress() != null)
            getProgress().setVisibility(View.GONE);
        super.setImageBitmap(bm);
    }

    public View getProgress() {
        return progress;
    }

    public void setProgress(View progress) {
        this.progress = progress;
    }
}
