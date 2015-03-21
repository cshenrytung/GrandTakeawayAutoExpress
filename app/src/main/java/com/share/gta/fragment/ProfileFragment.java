package com.share.gta.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anypresence.masterpass_android_library.dto.PreCheckoutResponse;
import com.anypresence.masterpass_android_library.interfaces.FutureCallback;
import com.anypresence.sdk.gadget_app_sample.models.User;
import com.share.gta.GadgetShopApplication;
import com.share.gta.R;
import com.share.gta.activity.BaseActivity;
import com.share.gta.activity.GTAExpressActivity;
import com.share.gta.dialog.GadgetShopDialog;

/**
 * Created by diego.rotondale on 10/09/2014.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class ProfileFragment extends Fragment {

    View rootView;
    private View connect;
    private View connectAlert;
    private FutureCallback<PreCheckoutResponse> preCheckoutCallback = new FutureCallback<PreCheckoutResponse>() {

        @Override
        public void onSuccess(PreCheckoutResponse preCheckoutResponse) {
            BaseActivity activity = (BaseActivity) getActivity();
            if (activity != null && !activity.isFinishing()) {
                activity.setPairStatus(true);
                updatePairStatus();
            }
        }

        @Override
        public void onFailure(Throwable throwable) {
            BaseActivity activity = (BaseActivity) getActivity();
            if (activity != null && !activity.isFinishing()) {
                activity.setPairStatus(false);
                updatePairStatus();
            }
        }
    };

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(int sectionNumber) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(GTAExpressActivity.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        rootView.findViewById(R.id.profile_learn_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GadgetShopDialog dialog = new GadgetShopDialog(getActivity(), getString(R.string.profile_dialog_title), getString(R.string.profile_learn_more_dialog_message));
                dialog.setCancelable(false);
                dialog.show();
            }
        });
        BaseActivity activity = (BaseActivity) getActivity();
        activity.getMCLibrary().preCheckout(activity, preCheckoutCallback);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        BaseActivity activity = (BaseActivity) getActivity();
        boolean pairing = activity.isAppPaired();
        connect = rootView.findViewById(R.id.profile_connect_masterpass);
        connectAlert = rootView.findViewById(R.id.profile_connect_alert);
        User user = GadgetShopApplication.getInstance().getUser();
        ((TextView) rootView.findViewById(R.id.profile_email)).setText(user.getEmail());
        if (pairing) {
            connectAlert.setVisibility(View.VISIBLE);
            connect.setVisibility(View.GONE);
        } else {
            connect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BaseActivity activity = (BaseActivity) getActivity();
                    activity.showProgress();
                    activity.getMCLibrary().pair(activity);
                }
            });
            connect.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((GTAExpressActivity) activity).onSectionAttached(
                getArguments().getInt(GTAExpressActivity.ARG_SECTION_NUMBER));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.global, menu);
        showSettingsContextActionBar();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void showSettingsContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(R.string.profile);
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    private void updatePairStatus() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (((BaseActivity) getActivity()).isAppPaired()) {
                    connectAlert.setVisibility(View.VISIBLE);
                    connect.setVisibility(View.GONE);
                } else {
                    connectAlert.setVisibility(View.GONE);
                    connect.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
