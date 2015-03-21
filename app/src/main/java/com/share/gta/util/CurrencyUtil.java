package com.share.gta.util;

import java.text.NumberFormat;
import java.util.Currency;

/**
 * Created by diego.rotondale on 1/23/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class CurrencyUtil {

    private static NumberFormat getCurrencyFormat() {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setCurrency(Currency.getInstance("USD"));
        return format;
    }

    public static String getStringValue(Integer price) {
        NumberFormat format = CurrencyUtil.getCurrencyFormat();
        format.setMaximumFractionDigits(0);
        return format.format(price / 100);
    }

    public static String getStringValue(double price) {
        NumberFormat format = CurrencyUtil.getCurrencyFormat();
        format.setMaximumFractionDigits(2);
        return format.format(price);
    }
}
