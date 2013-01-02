package com.nuvola.gxpenses.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.inject.assistedinject.Assisted;
import com.nuvola.gxpenses.R;
import com.nuvola.gxpenses.client.request.proxy.BudgetElementProxy;
import com.nuvola.gxpenses.ioc.Currency;

import javax.inject.Inject;
import java.text.NumberFormat;
import java.util.List;

public class BudgetElementAdapter extends ArrayAdapter<BudgetElementProxy> {
    private List<BudgetElementProxy> budgetElementsDataItems;
    private Activity context;
    private NumberFormat currencyFormat;

    @Inject
    public BudgetElementAdapter(@Assisted Activity context, @Assisted Integer textViewResourceId,
                                @Assisted List<BudgetElementProxy> objects, @Currency String currency) {
        super(context, textViewResourceId, objects);
        this.budgetElementsDataItems = objects;
        this.context = context;
        currencyFormat = NumberFormat.getCurrencyInstance();
        currencyFormat.setCurrency(java.util.Currency.getInstance(currency));
    }

    public void setBudgetElementsDataItems(List<BudgetElementProxy> objects) {
        if (objects != null) {
            budgetElementsDataItems.clear();
            budgetElementsDataItems.addAll(objects);
        } else {
            budgetElementsDataItems = objects;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.list_item_budget_elements, null);
        }

        BudgetElementProxy element = budgetElementsDataItems.get(position);
        if (element != null) {
            Integer purcentage = (int) ((element.getConsumedAmount() * 100) / element.getAmount());

            // Set the Status
            ImageView statusImageView = (ImageView) view.findViewById(R.id.element_status);
            if (purcentage > 100) {
                statusImageView.setImageResource(R.drawable.status_red);
            } else if (purcentage > 70 && purcentage < 100) {
                statusImageView.setImageResource(R.drawable.status_orange);
            } else {
                statusImageView.setImageResource(R.drawable.status_green);
            }

            //Set the tag
            TextView tagTextView = (TextView) view.findViewById(R.id.element_tag);
            tagTextView.setText(element.getTag());

            //Set the amount
            TextView elementBalance = (TextView) view.findViewById(R.id.budget_element_allowed);
            elementBalance.setText(currencyFormat.format(element.getAmount()));

            //Set the goal amount
            TextView goalTextView = (TextView) view.findViewById(R.id.element_goal);
            goalTextView.setText(currencyFormat.format(element.getLeftAmount()));
            if (element.getLeftAmount() < 0d) goalTextView.setTextColor(context.getResources().getColor(R.color.red));
            if (element.getLeftAmount() > 0d) goalTextView.setTextColor(context.getResources().getColor(R.color.green));

            //Set the goal status
            TextView goalStatusTextView = (TextView) view.findViewById(R.id.element_goal_status);
            goalStatusTextView.setText(element.getLeftAmount() < 0d ? "Over" : "Remaining");
            if (element.getLeftAmount() < 0d) {
                goalStatusTextView.setTextColor(context.getResources().getColor(R.color.red));
            }

            if (element.getLeftAmount() > 0d) {
                goalStatusTextView.setTextColor(context.getResources().getColor(R.color.green));
            }
        }

        return view;
    }
}
