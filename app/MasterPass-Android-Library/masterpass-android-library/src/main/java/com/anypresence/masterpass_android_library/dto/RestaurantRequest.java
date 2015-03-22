package com.anypresence.masterpass_android_library.dto;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by diego.rotondale on 1/16/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class RestaurantRequest implements Serializable {
    public static final String PageOffset_key = "PageOffset";
    public static final String PageLength_key = "PageLength";
    public static final String Latitude_key = "Latitude";
    public static final String Longitude_key = "Longitude";

    @SerializedName(PageOffset_key)
    public String PageOffset;
    @SerializedName(PageLength_key)
    public String PageLength;
    @SerializedName(Latitude_key)
    public String Latitude;
    @SerializedName(Longitude_key)
    public String Longitude;

    public JSONObject getExpressParams() {
        JSONObject params = new JSONObject();
        try {
            params.put("PageOffset_key", PageOffset);
            params.put("PageLength_key", PageLength);
            params.put("Latitude_key", Latitude);
            params.put("Longitude_key", Longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }

}
