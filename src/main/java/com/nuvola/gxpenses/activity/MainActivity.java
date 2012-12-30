
package com.nuvola.gxpenses.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.nuvola.gxpenses.R;
import com.nuvola.gxpenses.activity.budget.BudgetFragment;
import com.nuvola.gxpenses.activity.transaction.AccountFragment;
import com.nuvola.gxpenses.client.request.GxpensesRequestFactory;
import com.nuvola.gxpenses.security.CurrentUser;
import com.nuvola.gxpenses.security.SecurityUtils;
import com.nuvola.gxpenses.util.Constants;
import com.nuvola.gxpenses.util.LoadingDialog;
import com.nuvola.gxpenses.util.SuggestionListFactory;
import com.nuvola.gxpenses.util.TabListener;
import roboguice.activity.RoboFragmentActivity;

import javax.inject.Inject;

public class MainActivity extends RoboFragmentActivity {
    private static final String TAG = MainActivity.class.getName();
    private static final boolean DEBUG = Constants.DEBUG;
    private static final int NUM_ITEMS = 2;

    @Inject
    GxpensesRequestFactory requestFactory;
    @Inject
    CurrentUser currentUser;
    @Inject
    SuggestionListFactory suggestionListFactory;
    @Inject
    SecurityUtils securityUtils;

    private ViewPager viewPager;
    private TabsAdapter tabsAdapter;
    private LoadingDialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingDialog = new LoadingDialog(this);

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
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        viewPager = new ViewPager(this);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                actionBar.getTabAt(i).select();
            }

            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        tabsAdapter = new TabsAdapter(getSupportFragmentManager());
        actionBar.addTab(actionBar.newTab()
                .setText(R.string.accounts_label)
                .setTabListener(new TabListener<android.app.Fragment>(this, "0", viewPager)));
        actionBar.addTab(actionBar.newTab()
                .setText(R.string.budgets_label)
                .setTabListener(new TabListener<android.app.Fragment>(this, "1", viewPager)));

        viewPager.setAdapter(tabsAdapter);
        setContentView(viewPager);
        actionBar.setSelectedNavigationItem(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
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


    public static class TabsAdapter extends FragmentPagerAdapter {
        public TabsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new AccountFragment();
                case 1:
                    return new BudgetFragment();
                default:
                    return null;
            }
        }
    }

    private class PrepareApplicationTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            loadingDialog.show();
        }

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
                    } else {
                        Log.d(TAG, "User Authentication FAILED");
                        redirectToLoginActivity();
                    }
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void argument) {
            loadingDialog.dismiss();
        }

        @Override
        public void onCancelled() {
            if (DEBUG) Log.d(TAG, "Operation Canceled...");
            loadingDialog.dismiss();
        }
    }
}
