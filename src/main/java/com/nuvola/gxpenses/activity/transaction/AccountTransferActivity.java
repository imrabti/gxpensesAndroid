package com.nuvola.gxpenses.activity.transaction;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.nuvola.gxpenses.R;
import com.nuvola.gxpenses.activity.MainActivity;
import com.nuvola.gxpenses.adapter.AccountSpinnerAdapter;
import com.nuvola.gxpenses.client.request.GxpensesRequestFactory;
import com.nuvola.gxpenses.client.request.TransactionRequest;
import com.nuvola.gxpenses.client.request.proxy.AccountProxy;
import com.nuvola.gxpenses.client.request.proxy.TransferTransactionProxy;
import com.nuvola.gxpenses.util.Constants;
import com.nuvola.gxpenses.util.LoadingDialog;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.transfert_account_ui)
public class AccountTransferActivity extends RoboActivity {
    public static final String TAG = AccountTransferActivity.class.getName();
    public static final boolean DEBUG = Constants.DEBUG;

    @Inject
    GxpensesRequestFactory requestFactory;

    @InjectView(R.id.account_source)
    private Spinner sourceAccount;
    @InjectView(R.id.account_target)
    private Spinner targetAccount;
    @InjectView(R.id.transfer_amount)
    private EditText amount;

    private LoadingDialog loadingDialog;
    private TransactionRequest context;
    private AccountSpinnerAdapter spinnerData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        context = requestFactory.transactionService();

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        loadingDialog = new LoadingDialog(this);
        new LoadAccountsTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_account_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                redirectToHomeActivity();
                return true;

            case R.id.save_account_menu:
                TransferTransactionProxy transfer = context.create(TransferTransactionProxy.class);
                transfer.setSourceAccount(((AccountProxy) sourceAccount.getSelectedItem()));
                transfer.setTargetAccount(((AccountProxy) targetAccount.getSelectedItem()));
                if (amount.getText().toString().length() > 0) {
                    transfer.setAmount(Double.parseDouble(amount.getText().toString()));
                } else {
                    transfer.setAmount(0d);
                }

                new SaveTransfertTask().execute(transfer);

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

    private void setUpAccountSpinnerData(List<AccountProxy> accounts) {
        spinnerData = new AccountSpinnerAdapter(this, accounts);
        sourceAccount.setAdapter(spinnerData);
        targetAccount.setAdapter(spinnerData);
    }

    private class LoadAccountsTask extends AsyncTask<Void, Void, List<AccountProxy>> {
        @Override
        protected void onPreExecute() {
            loadingDialog.show();
        }

        @Override
        protected List<AccountProxy> doInBackground(Void... params) {
            final List<AccountProxy> listAccounts = new ArrayList<AccountProxy>();
            requestFactory.accountService().findAllAccountsByUserId().fire(new Receiver<List<AccountProxy>>() {
                @Override
                public void onSuccess(List<AccountProxy> results) {
                    listAccounts.addAll(results);
                }
            });

            return listAccounts;
        }

        @Override
        protected void onPostExecute(List<AccountProxy> accounts) {
            if (DEBUG) Log.d(TAG, "Loading Accounts finish, Number of Accounts =>" + accounts.size());
            loadingDialog.dismiss();
            setUpAccountSpinnerData(accounts);
        }

        @Override
        public void onCancelled() {
            if (DEBUG) Log.d(TAG, "Operation Canceled...");
            loadingDialog.dismiss();
        }
    }

    private class SaveTransfertTask extends AsyncTask<TransferTransactionProxy, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            loadingDialog.show();
        }

        @Override
        protected Boolean doInBackground(TransferTransactionProxy... params) {
            context.createNewTransferTransaction(params[0]).fire(new Receiver<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Transfet Done with succes");
                    context = requestFactory.transactionService();
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
