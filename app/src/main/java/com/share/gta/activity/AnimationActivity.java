package com.share.gta.activity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

import com.share.gta.R;

/**
 * Created by diego.rotondale on 9/26/2014.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class AnimationActivity extends BaseActivity {

    public static final String POSITION_X = "x";
    public static final String POSITION_Y = "y";
    private float fromXDelta;
    private float fromYDelta;
    private View addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Bundle b = getIntent().getExtras();
        addButton = findViewById(R.id.product_add);
        fromXDelta = b.getFloat(POSITION_X) - addButton.getWidth() - 100f;
        fromYDelta = b.getFloat(POSITION_Y) - addButton.getHeight() - 100f;
        showAnimation();
    }

    private void showAnimation() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        float toXDelta = width - 30f - addButton.getWidth();
        float toYDelta = 30f;

        AnimationSet animSet = new AnimationSet(true);
        RotateAnimation animRotate = new RotateAnimation(0.10f, 90.0f);
        animRotate.setDuration(500);
        animSet.addAnimation(animRotate);

        Animation animation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
        animation.setDuration(500);
        animSet.addAnimation(animation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 0.5f);
        alphaAnimation.setDuration(500);
        animSet.addAnimation(alphaAnimation);
        addButton.startAnimation(animSet);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                addButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                addButton.setVisibility(View.GONE);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
