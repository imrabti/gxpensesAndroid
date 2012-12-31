package com.nuvola.gxpenses.activity.transaction;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.nuvola.gxpenses.R;
import com.nuvola.gxpenses.activity.MainActivity;
import com.nuvola.gxpenses.client.request.AccountRequest;
import com.nuvola.gxpenses.client.request.GxpensesRequestFactory;
import com.nuvola.gxpenses.client.request.proxy.AccountProxy;
import com.nuvola.gxpenses.shared.type.AccountType;
import com.nuvola.gxpenses.security.CurrentUser;
import com.nuvola.gxpenses.util.Constants;
import com.nuvola.gxpenses.util.LoadingDialog;
import com.nuvola.gxpenses.util.ValueListFactory;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import javax.inject.Inject;

@ContentView(R.layout.add_account_ui)
public class AddAccountActivity extends RoboActivity {
    public static final String TAG = AddAccountActivity.class.getName();
    public static final boolean DEBUG = Constants.DEBUG;

    @Inject
    GxpensesRequestFactory requestFactory;
    @Inject
    ValueListFactory valueListFactory;
    @Inject
    CurrentUser currentUser;

    @InjectView(R.id.account_name)
    EditText accountName;
    @InjectView(R.id.account_balance)
    EditText accounBalance;
    @InjectView(R.id.account_type)
    Spinner accountType;

    private LoadingDialog loadingDialog;
    private AccountRequest context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        context = requestFactory.accountService();

        //Setting Up AccountType Spinner
        ArrayAdapter<AccountType> types = new ArrayAdapter<AccountType>(this,
                android.R.layout.simple_spinner_item, AccountType.values());
        types.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountType.setAdapter(types);

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        loadingDialog = new LoadingDialog(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_account_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                redirectToHomeActivity();
                return true;

            case R.id.save_account_menu:
                AccountProxy newAccount = context.create(AccountProxy.class);
                newAccount.setName(accountName.getText().toString());
                newAccount.setType((AccountType) accountType.getSelectedItem());
                newAccount.setUser(context.edit(currentUser.get()));
                if (accounBalance.getText().toString().length() > 0) {
                    newAccount.setBalance(Double.parseDouble(accounBalance.getText().toString()));
                }

                new SaveAccountTask().execute(newAccount);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void redirectToHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private class SaveAccountTask extends AsyncTask<AccountProxy, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            loadingDialog.show();
        }

        @Override
        protected Boolean doInBackground(AccountProxy... params) {
            context.createAccount(params[0]).fire(new Receiver<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d(TAG, "Account Created with succes");
                    context = requestFactory.accountService();
                    valueListFactory.updateListAccount();
                }
            });

            return true;
        }

        @Override
        protected void onPostExecute(Boolean status) {
            loadingDialog.dismiss();
            redirectToHomeActivity();
        }

        @Override
        public void onCancelled() {
            if (DEBUG) Log.d(TAG, "Operation Canceled...");
            loadingDialog.dismiss();
        }
    }
}
