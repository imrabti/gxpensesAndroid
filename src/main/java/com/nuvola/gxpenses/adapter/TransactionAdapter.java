package com.nuvola.gxpenses.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.google.inject.assistedinject.Assisted;
import com.nuvola.gxpenses.R;
import com.nuvola.gxpenses.client.request.proxy.TransactionProxy;
import com.nuvola.gxpenses.ioc.Currency;
import com.nuvola.gxpenses.ioc.PageSize;
import com.nuvola.gxpenses.shared.type.TransactionType;

import javax.inject.Inject;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class TransactionAdapter extends ArrayAdapter<TransactionProxy> {
    private Activity context;
    private List<TransactionProxy> transactions;
    private Long numPages;
    private Integer currentPage;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;

    @Inject
    public TransactionAdapter(@Assisted Activity context, @Assisted Integer textViewResourceId,
                              @Assisted List<TransactionProxy> objects, @Assisted Long numTransactions,
                              @Currency String currency, @PageSize Integer pageSize) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.transactions = objects;
        this.numPages = (numTransactions / pageSize + ((numTransactions % pageSize) == 0 ? 0 : 1));
        this.currentPage = 0;
        currencyFormat = NumberFormat.getCurrencyInstance();
        currencyFormat.setCurrency(java.util.Currency.getInstance(currency));
        dateFormat = new SimpleDateFormat("LLL d yyyy");
    }

    /**
     * This Method check if there is another page
     * to load, If So it return it number and Update
     * the current loaded Page to ++
     *
     * @return The number of the Next Page to load or -1 if
     *         there nothing to load
     */
    public Integer getNextPage() {
        if (currentPage >= numPages) {
            return -1;
        } else {
            currentPage += 1;
            return currentPage;
        }
    }

    /**
     * This Method return the current showed page
     *
     * @return the currentPage value
     */
    public Integer getCurrentPage() {
        return currentPage;
    }

    /**
     * This Method append newly loaded transactions
     * to the List (Add them to the End)
     *
     * @param objects
     */
    public void appendData(List<TransactionProxy> objects) {
        transactions.addAll(objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        TransactionProxy transaction = transactions.get(position);
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.list_item_transaction, null);
        }

        if (transaction != null) {
            //Set the date
            TextView dateTextView = (TextView) view.findViewById(R.id.transaction_date);
            dateTextView.setText(dateFormat.format(transaction.getDate()));

            //Set the name
            TextView nameTextView = (TextView) view.findViewById(R.id.transaction_name);
            nameTextView.setText(transaction.getPayee());

            //Set the tags
            TextView tagsTextView = (TextView) view.findViewById(R.id.transaction_tags);
            tagsTextView.setText(transaction.getTags());
            if (transaction.getTags() == null)
                tagsTextView.setVisibility(View.GONE);
            else
                tagsTextView.setVisibility(View.VISIBLE);

            //Set the amount
            Double amount = transaction.getAmount() != null ? transaction.getAmount() : 0d;
            amount = transaction.getType() == TransactionType.EXPENSE ? -amount : amount;
            TextView amountTextView = (TextView) view.findViewById(R.id.transaction_amount);
            amountTextView.setText(currencyFormat.format(amount));

            if (transaction.getType() == TransactionType.EXPENSE) {
                amountTextView.setTextColor(context.getResources().getColor(R.color.red));
            }

            if (transaction.getType() == TransactionType.INCOME) {
                amountTextView.setTextColor(context.getResources().getColor(R.color.green));
            }
        }

        return view;
    }
}
