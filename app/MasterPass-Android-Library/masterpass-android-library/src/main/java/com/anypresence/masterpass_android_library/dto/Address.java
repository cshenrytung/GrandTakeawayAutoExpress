package com.anypresence.masterpass_android_library.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by diego.rotondale on 1/16/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class Address implements Serializable {
    public static final String ADDRESS_ID_KEY = "address_id";
    public static final String CITY_KEY = "city";
    public static final String COUNTRY_KEY = "country";
    public static final String COUNTRY_SUBDIVISION_KEY = "country_subdivision";
    public static final String LINE_1_KEY = "line1";
    public static final String LINE_2_KEY = "line2";
    public static final String LINE_3_KEY = "line3";
    public static final String POSTAL_CODE_KEY = "postal_code";
    public static final String RECIPIENT_NAME_KEY = "recipient_name";
    public static final String RECIPIENT_PHONE_NUMBER_KEY = "recipient_phone_number";
    public static final String SELECTED_AS_DEFAULT_KEY = "selected_as_default";
    public static final String SHIPPING_ALIAS_KEY = "shipping_alias";

    @SerializedName(ADDRESS_ID_KEY)
    public Integer addressId;
    @SerializedName(CITY_KEY)
    public String city;
    @SerializedName(COUNTRY_KEY)
    public String country;
    @SerializedName(COUNTRY_SUBDIVISION_KEY)
    public String countrySubdivision;
    @SerializedName(LINE_1_KEY)
    public String lineOne;
    @SerializedName(LINE_2_KEY)
    public String lineTwo;
    @SerializedName(LINE_3_KEY)
    public String lineThree;
    @SerializedName(POSTAL_CODE_KEY)
    public String postalCode;
    @SerializedName(RECIPIENT_NAME_KEY)
    public String recipientName;
    @SerializedName(RECIPIENT_PHONE_NUMBER_KEY)
    public String recipientPhoneNumber;
    @SerializedName(SELECTED_AS_DEFAULT_KEY)
    public Boolean selectedAsDefault;
    @SerializedName(SHIPPING_ALIAS_KEY)
    public String shippingAlias;
}
