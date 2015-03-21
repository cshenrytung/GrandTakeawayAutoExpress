package com.share.gta.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.anypresence.sdk.gadget_app_sample.models.Product;
import com.share.gta.R;
import com.share.gta.domain.dto.ToCart;
import com.share.gta.util.CurrencyUtil;
import com.share.gta.util.ImageUtil;
import com.share.gta.view.RoundedImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by diego.rotondale on 09/09/2014.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class ProductAdapter extends ArrayAdapter<Product> implements Filterable {
    public final static int LOOPS = 1000;

    public static final String SECOND = "second";
    public static final CharSequence THIRD = "third";
    public static final CharSequence FOURTH = "fourth";
    Context context;
    private Filter productFilter;
    private List<Product> items = new ArrayList<Product>();
    private List<Product> originalItems = new ArrayList<Product>();

    public ProductAdapter(Context context, List<Product> items) {
        super(context, R.layout.item_product, items);
        originalItems = items;
        this.context = context;
    }

    public int getCount() {
        return items.size() * LOOPS;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ProductItemHolder holder;
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            holder = new ProductItemHolder();
            view = inflater.inflate(R.layout.item_product, parent, false);
            holder.image = (RoundedImageView) view.findViewById(R.id.product_image);
            holder.name = (TextView) view.findViewById(R.id.product_name);
            holder.description = (TextView) view.findViewById(R.id.product_description);
            holder.price = (TextView) view.findViewById(R.id.product_price);
            holder.add = (Button) view.findViewById(R.id.product_add);
            view.setTag(holder);
        } else {
            holder = (ProductItemHolder) view.getTag();
        }

        final Product product = items.get(getRealPosition(position));
        ImageUtil.setImageUrl(holder.image, product.getImageUrl());
        holder.name.setText(product.getName());
        holder.description.setText(product.getDesc());
        holder.price.setText(CurrencyUtil.getStringValue(product.getPrice()));
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] loc = new int[2];
                v.getLocationInWindow(loc);
                ToCart toCart = new ToCart(product, loc[0] + v.getWidth(), loc[1]);
                ((IProduct) getContext()).addProduct(toCart);
            }
        });
        return view;
    }

    private int getRealPosition(int position) {
        int size = items.size();
        int loopCount = new Double(position / size).intValue();
        return position - loopCount * size;
    }

    public void resetData() {
        items = originalItems;
    }

    @Override
    public Filter getFilter() {
        if (productFilter == null)
            productFilter = new ProductFilter();

        return productFilter;
    }

    @Override
    public void addAll(Collection<? extends Product> collection) {
        items = (List<Product>) collection;
        super.addAll(collection);
    }

    public interface IProduct {
        void addProduct(ToCart toCart);
    }

    private static class ProductItemHolder {
        RoundedImageView image;
        TextView name;
        TextView description;
        TextView price;
        Button add;
    }

    private class ProductFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence type) {
            FilterResults results = new FilterResults();
            if (type == null || type.length() == 0) {
                results.values = originalItems;
                results.count = originalItems.size();
            } else {
                List<Product> mProductList = new ArrayList<Product>();
                for (Product p : originalItems) {
                    Integer price = p.getPrice() / 100;
                    if (type.equals(SECOND) && (price >= 0 && price <= 50)) {
                        mProductList.add(p);
                    }
                    if (type.equals(THIRD) && (price >= 50 && price <= 100)) {
                        mProductList.add(p);
                    }
                    if (type.equals(FOURTH) && (price >= 100)) {
                        mProductList.add(p);
                    }
                }
                results.values = mProductList;
                results.count = mProductList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                items = (List<Product>) results.values;
                notifyDataSetChanged();
            }
        }
    }
}
