package com.nuvola.gxpenses.activity.transaction;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.nuvola.gxpenses.R;
import com.nuvola.gxpenses.adapter.AccountAdapter;
import com.nuvola.gxpenses.ioc.Currency;
import com.nuvola.gxpenses.client.request.GxpensesRequestFactory;
import com.nuvola.gxpenses.client.request.proxy.AccountProxy;
import com.nuvola.gxpenses.util.Constants;
import roboguice.fragment.RoboListFragment;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class AccountFragment extends RoboListFragment {
    public static final String TAG = AccountFragment.class.getName();
    public static final boolean DEBUG = Constants.DEBUG;

    @Inject
    private GxpensesRequestFactory requestFactory;
    @Currency
    private String currency;

    private AccountAdapter accountDataItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View v = layoutInflater.inflate(R.layout.account_list, container, false);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        //new LoadAccountsTask().execute();
    }

    private void initialiseData(List<AccountProxy> accounts) {
        accountDataItems = new AccountAdapter(getActivity(), R.layout.list_item_account, accounts, currency);
        setListAdapter(accountDataItems);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.accounts_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_account_menu:
                if (DEBUG) Log.d(TAG, "Add new Account Item Clicked");
                Intent intent = new Intent(getActivity(), AddAccountActivity.class);
                startActivity(intent);
                return true;

            case R.id.transfert_money_menu:
                if (DEBUG) Log.d(TAG, "Transfer money Clicked");
                Intent intentTransfer = new Intent(getActivity(), AccountTransferActivity.class);
                startActivity(intentTransfer);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (DEBUG) Log.d(TAG, "Account Item Clicked");
        Intent intent = new Intent(getActivity(), TransactionActivity.class);
        intent.putExtra("accountId", accountDataItems.getItem(position).getId());
        intent.putExtra("accountName", accountDataItems.getItem(position).getName());
        intent.putExtra("accountBalance", accountDataItems.getItem(position).getBalance());
        startActivity(intent);
    }

    private class LoadAccountsTask extends AsyncTask<Void, Void, List<AccountProxy>> {
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
            initialiseData(accounts);
        }
    }
}
