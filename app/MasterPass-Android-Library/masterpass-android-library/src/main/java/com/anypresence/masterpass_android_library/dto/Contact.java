package com.anypresence.masterpass_android_library.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by diego.rotondale on 1/21/2015.
 */
public class Contact implements Serializable {

    public static final String FIRST_NAME_KEY = "first_name";
    public static final String LAST_NAME_KEY = "last_name";
    public static final String COUNTRY_KEY = "country";
    public static final String EMAIL_ADDRESS_KEY = "email_address";
    public static final String PHONE_NUMBER_KEY = "phone_number";

    @SerializedName(FIRST_NAME_KEY)
    String firstName;
    @SerializedName(LAST_NAME_KEY)
    String lastName;
    @SerializedName(COUNTRY_KEY)
    String country;
    @SerializedName(EMAIL_ADDRESS_KEY)
    String emailAddress;
    @SerializedName(PHONE_NUMBER_KEY)
    String phoneNumber;

}
