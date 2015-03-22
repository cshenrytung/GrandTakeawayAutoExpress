package com.share.gta.fragment;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.anypresence.masterpass_android_library.dto.CreditCard;
import com.anypresence.masterpass_android_library.xml.StackOverflowXmlParser;
import com.share.gta.R;
import com.share.gta.activity.GTAExpressActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by diego.rotondale on 15/09/2014.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class LocationFragment extends Fragment {

    List<StackOverflowXmlParser.Entry> restaurants;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.plain, container, false);

        final ListView listview = (ListView) view.findViewById(R.id.listview);
        List<StackOverflowXmlParser.Entry> values = restaurants;

        final ArrayList<StackOverflowXmlParser.Entry> list = new ArrayList<StackOverflowXmlParser.Entry>();
        for (int i = 0; i < values.size(); ++i) {
            list.add(values.get(i));
        }
        final StableArrayAdapter adapter = new StableArrayAdapter(getActivity().getApplicationContext(),
                R.layout.item_restaurant, list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

            }

        });

        return view;
    }

    public void setDetail(List<StackOverflowXmlParser.Entry> details) {
        restaurants = details;
    }

    public void updateList() {

    }

    public static LocationFragment newInstance(int sectionNumber) {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        args.putInt(GTAExpressActivity.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private class StableArrayAdapter extends ArrayAdapter<StackOverflowXmlParser.Entry> {

        private final List<StackOverflowXmlParser.Entry> values;

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<StackOverflowXmlParser.Entry> objects) {
            super(context, textViewResourceId, objects);
            values = objects;
        }

        @Override
        public long getItemId(int position) {
            StackOverflowXmlParser.Entry item = getItem(position);
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.item_restaurant, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.firstLine);
            TextView textView2 = (TextView) rowView.findViewById(R.id.secondLine);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
            textView.setText(values.get(position).name);
            textView2.setText(values.get(position).id);
            // change the icon for Windows and iPhone

            return rowView;
        }

    }
}
