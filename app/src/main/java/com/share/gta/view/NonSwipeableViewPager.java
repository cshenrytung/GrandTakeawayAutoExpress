package com.share.gta.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by diego.rotondale on 1/23/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */

public class NonSwipeableViewPager extends ViewPager {
    public NonSwipeableViewPager(Context context) {
        super(context);
    }

    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent me) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        return false;
    }
}
