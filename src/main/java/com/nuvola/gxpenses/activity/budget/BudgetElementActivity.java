package com.nuvola.gxpenses.activity.budget;

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
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.nuvola.gxpenses.R;
import com.nuvola.gxpenses.activity.MainActivity;
import com.nuvola.gxpenses.adapter.BudgetElementAdapter;
import com.nuvola.gxpenses.adapter.BudgetElementAdapterFactory;
import com.nuvola.gxpenses.client.request.GxpensesRequestFactory;
import com.nuvola.gxpenses.client.request.proxy.BudgetElementProxy;
import com.nuvola.gxpenses.client.request.proxy.BudgetProxy;
import com.nuvola.gxpenses.security.CurrentUser;
import com.nuvola.gxpenses.util.Constants;
import com.nuvola.gxpenses.util.DateUtils;
import com.nuvola.gxpenses.util.LoadingDialog;
import com.nuvola.gxpenses.util.ValueListFactory;
import roboguice.activity.RoboListActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import javax.inject.Inject;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

@ContentView(R.layout.budget_elements_ui)
public class BudgetElementActivity extends RoboListActivity {
    public static final String TAG = BudgetElementActivity.class.getName();
    public static final boolean DEBUG = Constants.DEBUG;

    @Inject
    GxpensesRequestFactory requestFactory;
    @Inject
    CurrentUser currentUser;
    @Inject
    ValueListFactory valueListFactory;
    @Inject
    BudgetElementAdapterFactory budgetElementAdapterFactory;

    @InjectView(R.id.element_period)
    TextView elementPeriod;
    @InjectView(R.id.budget_name)
    TextView budgetName;
    @InjectView(R.id.budget_amount)
    TextView budgetAmount;
    @InjectView(R.id.budget_total_progress)
    ProgressBar budgetProgress;

    private Date currentDate;
    private BudgetProxy budget;

    private NumberFormat currencyFormat;
    private BudgetElementAdapter budgetElementsAdapter;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget_elements_ui);

        //Load selected account information
        budget = valueListFactory.getBudgetById((Long) getIntent().getSerializableExtra("budgetId"));
        currentDate = new Date();

        //Configure the ActionBar
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        loadingDialog = new LoadingDialog(this);
        new LoadBudgetElementsTask().execute(budget.getId());

        String currency = currentUser.get().getCurrency().getValue();
        currencyFormat = NumberFormat.getCurrencyInstance();
        currencyFormat.setCurrency(java.util.Currency.getInstance(currency));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.budget_elements_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                redirectToHomeActivity();
                return true;

            case R.id.previous_period_menu:
                currentDate = DateUtils.getPreviousDate(currentDate, budget.getPeriodicity());
                new LoadBudgetElementsTask().execute(budget.getId());
                return true;

            case R.id.next_period_menu:
                currentDate = DateUtils.getNextDate(currentDate, budget.getPeriodicity());
                new LoadBudgetElementsTask().execute(budget.getId());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeBudgetElementsAdapter(List<BudgetElementProxy> budgetElements) {
        if (budgetElementsAdapter == null) {
            budgetElementsAdapter = budgetElementAdapterFactory.create(this,
                    R.layout.list_item_budget_elements, budgetElements);
            setListAdapter(budgetElementsAdapter);
        } else {
            budgetElementsAdapter.setBudgetElementsDataItems(budgetElements);
            budgetElementsAdapter.notifyDataSetChanged();
        }

        //Set up Budget Period And Informations
        Integer percentage = budget.getPercentageConsumed() > 100 ? 100 : budget.getPercentageConsumed();
        budgetName.setText(budget.getName());
        budgetAmount.setText(currencyFormat.format(budget.getTotalAllowed() - budget.getTotalConsumed()));
        elementPeriod.setText(DateUtils.getDateToDisplay(currentDate, budget.getPeriodicity()));
        budgetProgress.setMax(100);
        budgetProgress.setProgress(percentage);
    }

    private void redirectToHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private class LoadBudgetElementsTask extends AsyncTask<Long, Void, Void> {
        private List<BudgetElementProxy> budgetElements;

        @Override
        protected void onPreExecute() {
            loadingDialog.show();
        }

        @Override
        protected Void doInBackground(Long... param) {
            requestFactory.budgetService().findAllBudgetElementsByBudget(param[0], currentDate)
                    .fire(new Receiver<List<BudgetElementProxy>>() {
                @Override
                public void onSuccess(List<BudgetElementProxy> result) {
                    budgetElements = result;
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void nothing) {
            loadingDialog.dismiss();
            initializeBudgetElementsAdapter(budgetElements);
        }

        @Override
        public void onCancelled() {
            if (DEBUG) Log.d(TAG, "Operation Canceled...");
            loadingDialog.dismiss();
        }
    }
}
