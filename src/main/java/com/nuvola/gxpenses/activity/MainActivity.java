
package com.nuvola.gxpenses.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.nuvola.gxpenses.util.ValueListFactory;
import roboguice.activity.RoboFragmentActivity;

import javax.inject.Inject;

public class MainActivity extends RoboFragmentActivity {
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

        // Adding Fragments Tabs
        actionBar.addTab(actionBar.newTab()
                .setText(R.string.accounts_label)
                .setTabListener(new TabListener<AccountFragment>(this, "accounts",
                        AccountFragment.class)));
        actionBar.addTab(actionBar.newTab()
                .setText(R.string.budgets_label)
                .setTabListener(new TabListener<BudgetFragment>(this, "budgets",
                        BudgetFragment.class)));

        if (savedInstanceState != null) {
            actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
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

    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;
        private final Bundle mArgs;
        private Fragment mFragment;

        public TabListener(Activity activity, String tag, Class<T> clz) {
            this(activity, tag, clz, null);
        }

        public TabListener(Activity activity, String tag, Class<T> clz, Bundle args) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
            mArgs = args;

            mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
            if (mFragment != null && !mFragment.isDetached()) {
                FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
                ft.detach(mFragment);
                ft.commit();
            }
        }

        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            if (mFragment == null) {
                mFragment = Fragment.instantiate(mActivity, mClass.getName(), mArgs);
                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                ft.attach(mFragment);
            }
        }

        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                ft.detach(mFragment);
            }
        }

        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) { }
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
                        valueListFactory.getListAccounts();
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
