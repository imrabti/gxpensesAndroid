package com.nuvola.gxpenses.activity.budget;

import java.text.NumberFormat;
import java.util.Date;

import com.nuvola.gxpenses.R;
import com.nuvola.gxpenses.activity.MainActivity;
import com.nuvola.gxpenses.adapter.BudgetElementAdapter;
import com.nuvola.gxpenses.client.request.GxpensesRequestFactory;
import com.nuvola.gxpenses.client.request.proxy.BudgetProxy;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import javax.inject.Inject;

@ContentView(R.layout.budget_elements_ui)
public class BudgetElementActivity extends RoboListActivity {
	public static final String TAG = BudgetElementActivity.class.getName();
	public static final boolean DEBUG = Constants.DEBUG;
	
	@Inject
    GxpensesRequestFactory requestFactory;
	
	@InjectView(R.id.element_period)
    TextView elementPeriod;
	@InjectView(R.id.budget_name)
    TextView budgetName;
	@InjectView(R.id.budget_amount)
    TextView budgetAmount;
	@InjectView(R.id.budget_total_progress)
    ProgressBar budgetProgress;
	
	private String budgetId;
	private Date currentDate;
	private BudgetProxy budget;
	
	private NumberFormat currencyFormat;
	private BudgetElementAdapter budgetElementsAdapter;
	private LoadingDialog loadingDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if(DEBUG) Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.budget_elements_ui);
		
		//Load selected account information
		budgetId = (String)getIntent().getSerializableExtra("budgetId");
		currentDate = new Date();
		
		//Configure the ActionBar
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		loadingDialog = new LoadingDialog(this);
		new LoadBudgetElementsTask().execute(budgetId);
		
		currencyFormat = NumberFormat.getCurrencyInstance();
		//currencyFormat.setCurrency(Currency.getInstance(preferences.getString("currency", "USD")));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.budget_elements_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			redirectToHomeActivity();
			return true;
			
		case R.id.previous_period_menu:
			//currentDate = DateUtils.getPreviousDate(currentDate, budget.getPeriodicityEnum());
			new LoadBudgetElementsTask().execute(budgetId);
			return true;
			
		case R.id.next_period_menu:
			//currentDate = DateUtils.getNextDate(currentDate, budget.getPeriodicityEnum());
			new LoadBudgetElementsTask().execute(budgetId);
			return true;
		
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void initializeBudgetElementsAdapter() {
		if(budgetElementsAdapter == null) {
			//budgetElementsAdapter = new BudgetElementsAdapter(this, R.layout.list_item_budget_elements,
					//budget.getElements(), preferences.getString("currency", "USD"));
			setListAdapter(budgetElementsAdapter);
		} else {
			//budgetElementsAdapter.setBudgetElementsDataItems(budget.getElements());
			budgetElementsAdapter.notifyDataSetChanged();
		}
		
		//Set up Budget Period And Informations
		//Integer purcentage = budget.getPurcentageConsumed() > 100 ? 100 : budget.getPurcentageConsumed();
		budgetName.setText(budget.getName());
		//budgetAmount.setText(currencyFormat.format(budget.getTotalAlowed() - budget.getTotalConsumed()));
		//elementPeriod.setText(DateUtils.getDateToDisplay(currentDate, budget.getPeriodicityEnum()));
		budgetProgress.setMax(100);
		//budgetProgress.setProgress(purcentage);
	}
	
	private void redirectToHomeActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	private class LoadBudgetElementsTask extends AsyncTask<String, Void, Void> {
		@Override
		protected void onPreExecute() {
			loadingDialog.show();
		}

		@Override
		protected Void doInBackground(String... param) {
			/*requestFactory.getBudgetRequest().findBudgetByIdAndPeriod(param[0], currentDate).fire(new Receiver<BudgetProxy>() {
				@Override
				public void onSuccess(BudgetProxy results) {
					budget = results;
					if(DEBUG) Log.d(TAG, "Budget having the => " + budget.getName() + " is loaded");
				}
			});*/
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void nothing) {
			loadingDialog.dismiss();
			initializeBudgetElementsAdapter();
		}

		@Override
		public void onCancelled() {
			if (DEBUG) Log.d(TAG, "Operation Canceled...");
			loadingDialog.dismiss();
		}
	}
}
