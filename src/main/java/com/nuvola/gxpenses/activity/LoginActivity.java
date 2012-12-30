package com.nuvola.gxpenses.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.nuvola.gxpenses.R;
import com.nuvola.gxpenses.client.request.GxpensesRequestFactory;
import com.nuvola.gxpenses.security.SecurityUtils;
import com.nuvola.gxpenses.util.Constants;
import com.nuvola.gxpenses.util.LoadingDialog;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import javax.inject.Inject;

@ContentView(R.layout.login_ui)
public class LoginActivity extends RoboActivity {
    public static final String TAG = LoginActivity.class.getName();
    public static final boolean DEBUG = Constants.DEBUG;

    @Inject
    GxpensesRequestFactory requestFactory;
    @Inject
    SecurityUtils securityUtils;

    @InjectView(R.id.email)
    EditText emailField;
    @InjectView(R.id.password)
    EditText passwordField;
    @InjectView(R.id.loginError)
    TextView loginError;
    @InjectView(R.id.loginButton)
    Button loginButton;

    private LoadingDialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        loadingDialog = new LoadingDialog(this);
        loginButton.setOnClickListener(onLoginClicked);
    }

    private OnClickListener onLoginClicked = new OnClickListener() {
        public void onClick(View view) {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            if (DEBUG) Log.d(TAG, "The Typed Email is : " + email);
            if (DEBUG) Log.d(TAG, "The Typed Password is : " + password);
            new AuthenticationTask().execute(email, password);
        }
    };

    private void redirectToMainActivity() {
        setVisible(false);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void wrongCredentials() {
        loginError.setVisibility(View.VISIBLE);
    }

    private class AuthenticationTask extends AsyncTask<String, Void, Boolean> {
        private Boolean isLoggedIn;

        protected void onPreExecute() {
            loadingDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... tokens) {
            final String email = tokens[0];
            final String password = tokens[1];

            requestFactory.authenticationService().authenticate(email, password).fire(new Receiver<Boolean>() {
                @Override
                public void onSuccess(Boolean success) {
                    isLoggedIn = success;
                    if (isLoggedIn) {
                        securityUtils.setCredentials(email, password);
                    } else {
                        Log.d(TAG, "User Authentication FAILED");
                    }
                }
            });

            return isLoggedIn;
        }

        protected void onPostExecute(Boolean isLoggedIn) {
            loadingDialog.dismiss();

            if (isLoggedIn) {
                if (DEBUG) Log.d(TAG, "Forwarding to the MainActivity...");
                redirectToMainActivity();
            } else {
                if (DEBUG) Log.d(TAG, "Authentication FAILED => Show Toast");
                wrongCredentials();
            }
        }

        public void onCancelled() {
            if (DEBUG) Log.d(TAG, "Operation Canceled...");
            loadingDialog.dismiss();
        }
    }
}
