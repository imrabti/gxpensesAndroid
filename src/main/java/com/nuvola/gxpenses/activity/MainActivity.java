
package com.nuvola.gxpenses.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.nuvola.gxpenses.R;
import com.nuvola.gxpenses.activity.transaction.AccountFragment;
import com.nuvola.gxpenses.client.request.GxpensesRequestFactory;
import com.nuvola.gxpenses.security.CurrentUser;
import com.nuvola.gxpenses.security.SecurityUtils;
import com.nuvola.gxpenses.util.Constants;
import com.nuvola.gxpenses.util.RoboSlidingFragmentActivity;
import com.nuvola.gxpenses.util.SuggestionListFactory;
import com.nuvola.gxpenses.util.ValueListFactory;
import com.slidingmenu.lib.SlidingMenu;

import javax.inject.Inject;

public class MainActivity extends RoboSlidingFragmentActivity {
    private static final String TAG = MainActivity.class.getName();
    private static final boolean DEBUG = Constants.DEBUG;

    @Inject
    GxpensesRequestFactory requestFactory;
    @Inject
    CurrentUser currentUser;
    @Inject
    SuggestionListFactory suggestionListFactory;
    @Inject
    ValueListFactory valueListFactory;
    @Inject
    SecurityUtils securityUtils;

    private Fragment menuFragment;
    private Fragment currentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (securityUtils.isLoggedIn()) {
            if (DEBUG) Log.d(TAG, "Credential saved, Authentication & init Data");
            if (DEBUG) Log.d(TAG, "Checking if it is necessary to load Payee and Tags");
            new PrepareApplicationTask().execute();
        } else {
            if (DEBUG) Log.d(TAG, "No credential saved.");
            redirectToLoginActivity();
        }

        final ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);

        // Setup and configure the menu fragment
        setBehindContentView(R.layout.menu_frame);
        FragmentTransaction menuTransaction = this.getSupportFragmentManager().beginTransaction();
        menuFragment = new MenuFragment();
        menuTransaction.replace(R.id.menu_frame, menuFragment);
        menuTransaction.commit();

        // Configure the sliding
        SlidingMenu sm = getSlidingMenu();
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setFadeDegree(0.35f);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        if (savedInstanceState != null) {
            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, "fragment");
        }

        if (currentFragment == null) {
            currentFragment = new AccountFragment();
        }

        // set the Above View
        setContentView(R.layout.content_frame);
        FragmentTransaction contentTransaction = getSupportFragmentManager().beginTransaction();
        contentTransaction.replace(R.id.content_frame, currentFragment);
        contentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                if (DEBUG) Log.d(TAG, "Loggin out, cleaning all user data...");
                securityUtils.clearCredentials();
                redirectToLoginActivity();
                return true;

            case R.id.setting_menu:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "fragment", currentFragment);
    }

    public void switchContent(Fragment fragment) {
        currentFragment = fragment;
        FragmentTransaction contentTransaction = getSupportFragmentManager().beginTransaction();
        contentTransaction.replace(R.id.content_frame, fragment);
        contentTransaction.commit();
        getSlidingMenu().showContent();
    }

    private void redirectToLoginActivity() {
        setVisible(false);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private class PrepareApplicationTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... tokens) {
            String email = securityUtils.getUsername();
            String password = securityUtils.getPassword();

            requestFactory.authenticationService().authenticate(email, password).fire(new Receiver<Boolean>() {
                @Override
                public void onSuccess(Boolean isLoggedIn) {
                    if (isLoggedIn) {
                        currentUser.init();
                        suggestionListFactory.getListPayee();
                        suggestionListFactory.getListTags();
                        valueListFactory.getListAccounts();
                    } else {
                        Log.d(TAG, "User Authentication FAILED");
                        redirectToLoginActivity();
                    }
                }
            });

            return null;
        }
    }
}
