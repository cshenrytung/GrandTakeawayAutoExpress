package com.share.gta.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.anypresence.masterpass_android_library.dto.PreCheckoutResponse;
import com.anypresence.masterpass_android_library.interfaces.FutureCallback;
import com.anypresence.sdk.acl.AuthManager;
import com.anypresence.sdk.acl.IAuthenticatable;
import com.anypresence.sdk.callbacks.APCallback;
import com.anypresence.sdk.gadget_app_sample.models.User;
import com.share.gta.GadgetShopApplication;
import com.share.gta.MPConstants;
import com.share.gta.R;
import com.share.gta.interfaces.LoginListener;


/**
 * Created by diego.rotondale on 19/09/2014.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public class LoginActivity extends BaseActivity {
    private boolean canOpenMainActivity = true;
    private FutureCallback<PreCheckoutResponse> callbackPreCheckout = new FutureCallback<PreCheckoutResponse>() {

        @Override
        public void onSuccess(PreCheckoutResponse preCheckoutResponse) {
            checkOpenMainActivity();
            LoginActivity.this.setPairStatus(true);
        }

        @Override
        public void onFailure(Throwable throwable) {
            checkOpenMainActivity();
            LoginActivity.this.setPairStatus(false);
        }
    };

    private LoginListener loginListener = new LoginListener() {
        @Override
        public void onLoginFailed(Throwable ex) {
            unsuccessfulLogin(ex);
        }

        @Override
        public void onLoginSuccess(IAuthenticatable user) {
            if (user instanceof User) {
                GadgetShopApplication.getInstance().setUser((User) user);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setRememberUser();
                    if (canOpenMainActivity) {
                        getMCLibrary().preCheckout(LoginActivity.this, callbackPreCheckout);
                    } else {
                        LoginActivity.this.finish();
                    }
                }
            });
        }
    };
    private TextView username;
    private TextView password;
    private TextView.OnEditorActionListener passwordActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onLogin();
                return true;
            }
            return false;
        }
    };
    private View.OnClickListener onSignInClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onLogin();
        }

    };
    private View.OnClickListener onRegister = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        }
    };

    private void checkOpenMainActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionProgressDialog.dismiss();
                if (canOpenMainActivity) {
                    loginSuccessful();
                } else {
                    LoginActivity.this.finish();
                }
            }
        });
    }

    public void callLogin(User user, final LoginListener listener) {
        AuthManager manager = new AuthManager.Builder(user.getClass()).useAnyPresenceAuth(user).build();
        manager.authenticate(new APCallback<IAuthenticatable>() {
            @Override
            public void finished(IAuthenticatable result, Throwable ex) {
                if (ex != null) {
                    ex.printStackTrace();
                    if (listener != null) listener.onLoginFailed(ex);
                    return;
                }
                // Call listener
                if (listener != null) listener.onLoginSuccess(result);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_login);
        username = (TextView) findViewById(R.id.login_username);
        password = (TextView) findViewById(R.id.login_password);
        password.setOnEditorActionListener(passwordActionListener);
        this.findViewById(R.id.login_sign_in).setOnClickListener(onSignInClick);
        loadRememberUser();
        findViewById(R.id.login_register_label).setOnClickListener(onRegister);
    }

    private void setRememberUser() {
        CheckBox remember = (CheckBox) LoginActivity.this.findViewById(R.id.login_remember_password);
        String username = this.username.getText().toString();
        if (!remember.isChecked())
            username = null;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(MPConstants.SP_USERNAME, username);
        edit.commit();
    }

    private void loadRememberUser() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String usernameValue = sp.getString(MPConstants.SP_USERNAME, null);
        if (usernameValue != null) {
            username.setText(usernameValue);
            password.requestFocus();
        }
    }

    private void onLogin() {
        String usernameValue = username.getText().toString();
        String passwordValue = password.getText().toString();

        if (usernameValue.isEmpty() || passwordValue.isEmpty()) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(LoginActivity.this, getString(R.string.username_password_empty), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            mConnectionProgressDialog.show();
            User user = new User();
            user.setEmail(usernameValue);
            user.setPassword(passwordValue);
            callLogin(user, loginListener);
        }
    }

    public void unsuccessfulLogin(final Throwable e) {
        mConnectionProgressDialog.dismiss();
        runOnUiThread(new Runnable() {
            public void run() {
                if (e instanceof NullPointerException) {
                    Toast.makeText(LoginActivity.this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(LoginActivity.this, R.string.invalid_username_or_password, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loginSuccessful() {
        startActivity(new Intent(this, GTAExpressActivity.class));
        LoginActivity.this.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        canOpenMainActivity = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        canOpenMainActivity = true;
    }
}
