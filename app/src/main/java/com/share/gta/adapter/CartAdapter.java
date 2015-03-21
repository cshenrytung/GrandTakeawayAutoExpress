package com.share.gta.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.anypresence.sdk.gadget_app_sample.models.OrderDetail;
import com.share.gta.R;
import com.share.gta.activity.BaseActivity;
import com.share.gta.util.CurrencyUtil;
import com.share.gta.util.ImageUtil;

import java.util.Collection;
import java.util.List;

/**
 * Created by diego.rotondale on 10/09/2014.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class CartAdapter extends ArrayAdapter<OrderDetail> {
    private static final String LOG_TAG = CartAdapter.class.getSimpleName();
    BaseActivity context;
    private List<OrderDetail> items;

    public CartAdapter(BaseActivity activity) {
        super(activity, R.layout.item_cart);
        this.context = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProductItemHolder holder;
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            holder = new ProductItemHolder();
            view = inflater.inflate(R.layout.item_cart, parent, false);
            holder.image = (NetworkImageView) view.findViewById(R.id.item_cart_image);
            holder.name = (TextView) view.findViewById(R.id.item_cart_name);
            holder.quantity = (TextView) view.findViewById(R.id.item_cart_quantity_value);
            holder.price = (TextView) view.findViewById(R.id.item_cart_price);
            view.setTag(holder);
        } else {
            holder = (ProductItemHolder) view.getTag();
        }
        OrderDetail orderDetail = items.get(position);
        ImageUtil.setImageUrl(holder.image, orderDetail.getProductImageUrl());
        holder.name.setText(orderDetail.getProductName());
        holder.quantity.setText(String.valueOf(orderDetail.getQuantity()));
        holder.price.setText(CurrencyUtil.getStringValue(orderDetail.getProductPrice()));
        return view;
    }

    @Override
    public void addAll(Collection<? extends OrderDetail> collection) {
        items = (List<OrderDetail>) collection;
        super.addAll(collection);
    }

    private static class ProductItemHolder {
        NetworkImageView image;
        TextView name;
        TextView quantity;
        TextView price;
    }
}
