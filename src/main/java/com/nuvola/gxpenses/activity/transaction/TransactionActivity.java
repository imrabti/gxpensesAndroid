package com.nuvola.gxpenses.activity.transaction;

import java.text.NumberFormat;


import java.util.List;

import com.nuvola.gxpenses.R;
import com.nuvola.gxpenses.activity.MainActivity;
import com.nuvola.gxpenses.adapter.TransactionAdapter;
import com.nuvola.gxpenses.ioc.Currency;
import com.nuvola.gxpenses.ioc.PageSize;
import com.nuvola.gxpenses.client.request.GxpensesRequestFactory;
import com.nuvola.gxpenses.client.request.proxy.TransactionProxy;
import com.nuvola.gxpenses.util.Constants;
import com.nuvola.gxpenses.util.LoadingDialog;

import roboguice.activity.RoboListActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TextView;

import javax.inject.Inject;

@ContentView(R.layout.transaction_ui)
public class TransactionActivity extends RoboListActivity implements OnScrollListener {
	public static final String TAG = TransactionActivity.class.getName();
	public static final boolean DEBUG = Constants.DEBUG;
	
	public static final int ADD_TRANSACTION = 100;
	public static final int EDIT_TRANSACTION = 200; 
	
	@Inject
    GxpensesRequestFactory requestFactory;
	
	@InjectView(R.id.account_name)
    TextView accountNameLabel;
	@InjectView(R.id.account_balance)
    TextView accountBalanceLabel;

    @Currency
    private String currency;
    @PageSize
    private Integer pageSize;

	private String accountId;
	private String accountName;
	private Double accountBalance;
	
	private TransactionAdapter transactionAdapter;
	private LoadingDialog loadingDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if(DEBUG) Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		
		//Load selected account information
		accountId = (String)getIntent().getSerializableExtra("accountId");
		accountName = (String)getIntent().getSerializableExtra("accountName");
		accountBalance = (Double)getIntent().getSerializableExtra("accountBalance");
		setAccountInformations();
		
		//Configure the ActionBar
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		loadingDialog = new LoadingDialog(this);
		getListView().setOnScrollListener(this);
		new SetUpTransactionsTask().execute();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == ADD_TRANSACTION && resultCode == RESULT_OK) {
			if(DEBUG) Log.d(TAG, "New transaction added, update account balance");
			Double amount = data.getDoubleExtra("amount", 0d);
			accountBalance = accountBalance + amount;
			accountBalanceLabel.setText(accountBalance.toString());
			transactionAdapter = null;
			new SetUpTransactionsTask().execute();
		} else if(requestCode == EDIT_TRANSACTION && resultCode == RESULT_OK) {
			if(DEBUG) Log.d(TAG, "Updated transaction, update account balance");
			Double oldAmount = data.getDoubleExtra("oldAmount", 0d);
			Double newAmount = data.getDoubleExtra("amount", 0d);
			accountBalance = accountBalance + oldAmount + newAmount;
			accountBalanceLabel.setText(accountBalance.toString());
			transactionAdapter = null;
			new SetUpTransactionsTask().execute();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.transactions_menu, menu);
		return true;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (DEBUG) Log.d(TAG, "Transaction Item Clicked");
		//Intent intent = new Intent(this, UpdateTransactionActivity.class);
		//intent.putExtra("transactionId", transactionAdapter.getItem(position).getId());
		//startActivityForResult(intent, EDIT_TRANSACTION);
	}
	
	private void setAccountInformations() {
		NumberFormat format = NumberFormat.getCurrencyInstance();
		format.setCurrency(java.util.Currency.getInstance(currency));
		accountNameLabel.setText(accountName);
		accountBalanceLabel.setText(format.format(accountBalance));
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
		
		if(loadMore && transactionAdapter != null) {
			Integer nextPage = transactionAdapter.getNextPage();
			if(nextPage > 0) {
				new LoadMoreTransactionsTask().execute(nextPage);
			}
		}
	}
	
	private void initializeTransactionAdatpter(Long numTransactions, List<TransactionProxy> data) {
		if(transactionAdapter == null) {
			if(DEBUG) Log.d(TAG, "Transaction Adapter CREATED");
            transactionAdapter = new TransactionAdapter(this, R.layout.list_item_transaction,
                    data, numTransactions, currency, pageSize);
			setListAdapter(transactionAdapter);
		} else {
			if(DEBUG) Log.d(TAG, "Transaction Adapter APPENDED");
			transactionAdapter.appendData(data);
			transactionAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			redirectToHomeActivity();
			return true;
			
		case R.id.add_transaction_menu:
			redirectToAddTransactionActivity();
			return true;
			
		case R.id.reload_transactions_menu:
			transactionAdapter = null;
			new SetUpTransactionsTask().execute();
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
	
	private void redirectToAddTransactionActivity() {
		Intent intent = new Intent(this, AddTransactionActivity.class);
		intent.putExtra("accountId", accountId);
		startActivityForResult(intent, ADD_TRANSACTION);
	}
	
	private class SetUpTransactionsTask extends AsyncTask<Void, Void, Void> {
		private Long numTransaction;
		private List<TransactionProxy> data;
		
		@Override
		protected void onPreExecute() {
			loadingDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			//Load Transaction count
			/*requestFactory.transactionService().countByAccountId(accountId).fire(new Receiver<Long>() {
				@Override
				public void onSuccess(Long results) {
					if(DEBUG) Log.d(TAG, "Loaded count of transaction => " + results);
					numTransaction = results;
				}
			});*/
			
			//Load Transaction Data
			/*requestFactory.getTransactionRequest().findByAccountId(accountId, 0, Constants.PAGE_SIZE).fire(new Receiver<List<TransactionProxy>>() {
				@Override
				public void onSuccess(List<TransactionProxy> results) {
					if(DEBUG) Log.d(TAG, "Number of currently loaded transactions => " + results.size());
					data = results;
				}
			});*/
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void nothing) {
			loadingDialog.dismiss();
			initializeTransactionAdatpter(numTransaction, data);
		}

		@Override
		public void onCancelled() {
			if (DEBUG) Log.d(TAG, "Operation Canceled...");
			loadingDialog.dismiss();
		}
	}
	
	private class LoadMoreTransactionsTask extends AsyncTask<Integer, Void, Void> {
		private List<TransactionProxy> data;

		@Override
		protected void onPreExecute() {
			loadingDialog.show();
		}
		
		@Override
		protected Void doInBackground(Integer... params) {
			/*requestFactory.getTransactionRequest().findByAccountId(accountId, params[0], Constants.PAGE_SIZE).fire(new Receiver<List<TransactionProxy>>() {
				@Override
				public void onSuccess(List<TransactionProxy> results) {
					if(DEBUG) Log.d(TAG, "Number of currently loaded transactions => " + results.size());
					data = results;
				}
			});*/
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void nothing) {
			loadingDialog.dismiss();
			initializeTransactionAdatpter(null, data);
		}

		@Override
		public void onCancelled() {
			if (DEBUG) Log.d(TAG, "Operation Canceled...");
			loadingDialog.dismiss();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) { }
}
