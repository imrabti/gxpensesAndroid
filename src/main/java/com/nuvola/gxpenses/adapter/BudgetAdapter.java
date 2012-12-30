package com.nuvola.gxpenses.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.nuvola.gxpenses.R;
import com.nuvola.gxpenses.client.request.proxy.BudgetProxy;

import java.text.NumberFormat;
import java.util.List;

public class BudgetAdapter extends ArrayAdapter<BudgetProxy> {
    private List<BudgetProxy> budgetDataItems;
    private Activity context;
    private NumberFormat currencyFormat;

    public BudgetAdapter(Activity context, Integer textViewResourceId, List<BudgetProxy> objects, String currency) {
        super(context, textViewResourceId, objects);
        this.budgetDataItems = objects;
        this.context = context;
        currencyFormat = NumberFormat.getCurrencyInstance();
        currencyFormat.setCurrency(java.util.Currency.getInstance(currency));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.list_item_budget, null);
        }

        BudgetProxy budget = budgetDataItems.get(position);
        if (budget != null) {
            //Set budget name
            TextView nameTextView = (TextView) view.findViewById(R.id.budget_name);
            nameTextView.setText(budget.getName());

            //Set budget balance
            TextView balanceTextView = (TextView) view.findViewById(R.id.budget_balance);
            TextView goalTextView = (TextView) view.findViewById(R.id.budget_goal);
            String consumedBalance = currencyFormat.format(budget.getTotalConsumed());
            String allowedBalance = currencyFormat.format(budget.getTotalAllowed());
            balanceTextView.setText(consumedBalance);
            goalTextView.setText(allowedBalance);

            //Set budget consumed purcentage
            Integer purcentage = budget.getPercentageConsumed() > 100 ? 100 : budget.getPercentageConsumed();
            ProgressBar purcentageProgress = (ProgressBar) view.findViewById(R.id.budget_progress);
            purcentageProgress.setMax(100);
            purcentageProgress.setProgress(purcentage);

            if (budget.getPercentageConsumed() > 100) {
                purcentageProgress.setProgressDrawable(context.getResources().getDrawable(
                        R.drawable.progress_bar_states_red));
            } else if (budget.getPercentageConsumed() > 70 && budget.getPercentageConsumed() < 100) {
                purcentageProgress.setProgressDrawable(context.getResources().getDrawable(
                        R.drawable.progress_bar_states_orange));
            } else {
                purcentageProgress.setProgressDrawable(context.getResources().getDrawable(
                        R.drawable.progress_bar_states_green));
            }
        }

        return view;
    }
}
