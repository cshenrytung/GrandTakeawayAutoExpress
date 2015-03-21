package com.share.gta.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.joanzapata.android.iconify.Iconify;
import com.share.gta.R;
import com.share.gta.domain.model.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by diego.rotondale on 08/09/2014.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class MenuAdapter extends ArrayAdapter<MenuItem> {
    Context context;
    private List<MenuItem> items = new ArrayList<MenuItem>();

    public MenuAdapter(Context context) {
        super(context, R.layout.item_menu);
        items.add(new MenuItem("{fa-home}  ", context.getString(R.string.home)));
        items.add(new MenuItem("{fa-shopping-cart}  ", context.getString(R.string.cart)));
        items.add(new MenuItem("{fa-user}  ", context.getString(R.string.profile)));
        items.add(new MenuItem("icon_location2", "Restaurants"));
        super.addAll(items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MenuItemHolder holder;
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            holder = new MenuItemHolder();
            view = inflater.inflate(R.layout.item_menu, parent, false);
            holder.itemName = (TextView) view.findViewById(R.id.menu_item_label);
            view.setTag(holder);
        } else {
            holder = (MenuItemHolder) view.getTag();
        }
        MenuItem menuItem = items.get(position);
        holder.itemName.setText(menuItem.getIconAndLabel());
        Iconify.addIcons(holder.itemName);
        return view;
    }

    private static class MenuItemHolder {
        TextView itemName;
    }
}