package com.share.gta.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.anypresence.masterpass_android_library.dto.Address;
import com.share.gta.R;

import java.util.List;

/**
 * Created by diego.rotondale on 2/4/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class ShippingAddressAdapter extends ArrayAdapter<Address> {
    private Context context;

    public ShippingAddressAdapter(Context context, List<Address> items) {
        super(context, R.layout.item_shipping_address);
        this.context = context;
        super.addAll(items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = (TextView) convertView;
        ShippingAddressHolder holder;
        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.item_shipping_address, parent, false);
            holder = new ShippingAddressHolder();
            holder.address = (TextView) view.findViewById(R.id.address_label);
            view.setTag(holder);
        } else {
            holder = (ShippingAddressHolder) view.getTag();
        }
        holder.address.setText(getAddress(position) + "\n" + getCountry(position));
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = (TextView) convertView;
        ShippingAddressHolder holder;
        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.item_shipping_address, parent, false);
            holder = new ShippingAddressHolder();
            holder.address = (TextView) view.findViewById(R.id.address_label);
            view.setTag(holder);
        } else {
            holder = (ShippingAddressHolder) view.getTag();
        }
        holder.address.setText(getAddress(position));
        return view;
    }

    private String getCountry(int position) {
        Address address = getItem(position);
        String country = "";
        String countrySubdivision = address.countrySubdivision;
        if (hasValue(countrySubdivision)) {
            country = countrySubdivision;
        }
        String countryValue = address.country;
        String postalCode = address.postalCode;
        if (hasValue(countryValue) && hasValue(postalCode)) {
            country = country + " , " + countryValue + " " + postalCode;
        }
        return country;
    }

    private String getAddress(int position) {
        Address address = getItem(position);
        String addressValue = "";
        String shippingAlias = address.shippingAlias;
        if (hasValue(shippingAlias))
            addressValue = shippingAlias;
        else {
            String lineOne = address.lineOne;
            if (hasValue(lineOne))
                addressValue = lineOne;
        }
        return addressValue;
    }

    private boolean hasValue(String address) {
        return address != null && !address.isEmpty();
    }

    private static class ShippingAddressHolder {
        TextView address;
    }
}
