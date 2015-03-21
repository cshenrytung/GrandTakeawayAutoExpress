package com.share.gta.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.anypresence.rails_droid.IAPFutureCallback;
import com.anypresence.sdk.gadget_app_sample.models.User;
import com.share.gta.R;

/**
 * Created by diego.rotondale on 1/27/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class RegisterActivity extends BaseActivity {
    private static final String LOG_TAG = RegisterActivity.class.getSimpleName();
    private IAPFutureCallback<?> userCallback = new IAPFutureCallback<Object>() {
        @Override
        public void finished(Object o, Throwable throwable) {
            Log.e(LOG_TAG, throwable.toString());
            dismissProgressInThread();
        }

        @Override
        public void onSuccess(Object o) {
            finishActivity();
        }

        @Override
        public void onFailure(Throwable throwable) {
            Log.e(LOG_TAG, throwable.toString());
            dismissProgressInThread();
        }
    };
    private View.OnClickListener onRegisterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String email = emailEdit.getText().toString();
            if (email.isEmpty()) {
                emailEdit.setError(RegisterActivity.this.getString(R.string.register_empty));
            } else {
                mConnectionProgressDialog.show();
                User user = new User();
                user.setEmail(email);
                user.saveInBackground(userCallback);
            }
        }
    };
    private View.OnClickListener onEmailEditClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            emailEdit.setError(null);
        }
    };
    private EditText emailEdit;

    private void dismissProgressInThread() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EditText emailEdit = (EditText) RegisterActivity.this.findViewById(R.id.dialog_edit);
                emailEdit.setError(getString(R.string.register_error));
                mConnectionProgressDialog.dismiss();
            }
        });
    }

    private void finishActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionProgressDialog.dismiss();
                RegisterActivity.this.finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        View register = this.findViewById(R.id.dialog_ok);
        register.setOnClickListener(onRegisterClickListener);
        emailEdit = (EditText) this.findViewById(R.id.dialog_edit);
        emailEdit.setOnClickListener(onEmailEditClickListener);
        restoreActionBar();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(R.string.title_register_user);
        }
    }

}
