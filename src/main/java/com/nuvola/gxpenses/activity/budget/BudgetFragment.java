package com.nuvola.gxpenses.activity.budget;

import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.nuvola.gxpenses.R;
import com.nuvola.gxpenses.adapter.BudgetAdapter;
import com.nuvola.gxpenses.ioc.Currency;
import com.nuvola.gxpenses.client.request.GxpensesRequestFactory;
import com.nuvola.gxpenses.client.request.proxy.BudgetProxy;
import com.nuvola.gxpenses.util.Constants;
import roboguice.RoboGuice;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BudgetFragment extends ListFragment {
    public static final String TAG = BudgetFragment.class.getName();
    public static final boolean DEBUG = Constants.DEBUG;

    @Inject
    private GxpensesRequestFactory requestFactory;
    @Currency
    private String currency;

    private BudgetAdapter budgetDataItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        new LoadBudgetsTask().execute();
    }

    private void initialiseData(List<BudgetProxy> budgets) {
        budgetDataItems = new BudgetAdapter(getActivity(), R.layout.list_item_budget, budgets, currency);
        setListAdapter(budgetDataItems);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.budgets_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_budget_menu:
                if (DEBUG) Log.d(TAG, "Add new Budget Item Clicked");
                //Intent intent = new Intent(getActivity(), AddBudgetActivity.class);
                //startActivity(intent);
                return true;

            case R.id.reload_budgets_menu:
                new LoadBudgetsTask().execute();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (DEBUG) Log.d(TAG, "Budget Item Clicked");
        Intent intent = new Intent(getActivity(), BudgetElementActivity.class);
        intent.putExtra("budgetId", budgetDataItems.getItem(position).getId());
        startActivity(intent);
    }

    private class LoadBudgetsTask extends AsyncTask<Void, Void, List<BudgetProxy>> {
        @Override
        protected List<BudgetProxy> doInBackground(Void... params) {
            final List<BudgetProxy> listBudgets = new ArrayList<BudgetProxy>();
            requestFactory.budgetService().findAllBudgetsByUserId(new Date()).fire(new Receiver<List<BudgetProxy>>() {
                @Override
                public void onSuccess(List<BudgetProxy> results) {
                    listBudgets.addAll(results);
                }
            });

            return listBudgets;
        }

        @Override
        protected void onPostExecute(List<BudgetProxy> budgets) {
            if (DEBUG) Log.d(TAG, "Loading Budgets finish, Number of Budhets =>" + budgets.size());
            initialiseData(budgets);
        }
    }
}
