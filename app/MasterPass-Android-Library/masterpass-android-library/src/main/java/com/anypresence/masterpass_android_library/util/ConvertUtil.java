package com.anypresence.masterpass_android_library.util;

import java.util.Map;

/**
 * Created by diego.rotondale on 1/17/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class ConvertUtil {
    public static Integer getInteger(Map<String, Object> dictionary, String key) {
        return (Integer) getObject(dictionary, key);
    }

    public static String getString(Map<String, Object> dictionary, String key) {
        String value = (String) getObject(dictionary, key);
        if (value != null && !value.isEmpty())
            return value;
        return null;
    }

    public static Object getObject(Map<String, Object> dictionary, String key) {
        if (dictionary.containsKey(key)) {
            return dictionary.get(key);
        }
        return null;
    }
}
