package com.share.gta.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by diego.rotondale on 1/23/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class JsonUtil {

    private static final String LOG_TAG = JsonUtil.class.getSimpleName();

    public static String getJsonFileContent(Context context, String jsonFileName) {
        InputStream is;
        String response = null;
        try {
            is = context.getAssets().open(jsonFileName);
            response = readStream(is);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return response;
    }

    public static String readStream(InputStream input) {
        BufferedReader in = null;
        String result = null;
        try {
            InputStreamReader isr = new InputStreamReader(input);
            in = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            String page = sb.toString();
            result = page;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
