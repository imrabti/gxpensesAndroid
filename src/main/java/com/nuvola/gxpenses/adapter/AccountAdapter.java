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
import com.nuvola.gxpenses.client.request.proxy.AccountProxy;
import com.nuvola.gxpenses.ioc.Currency;

import javax.inject.Inject;
import java.text.NumberFormat;
import java.util.List;

public class AccountAdapter extends ArrayAdapter<AccountProxy> {
    private List<AccountProxy> accountDataItems;
    private Activity context;
    private NumberFormat currencyFormat;

    @Inject
    public AccountAdapter(@Assisted Activity context, @Assisted  Integer textViewResourceId,
                          @Assisted  List<AccountProxy> objects, @Currency String currency) {
        super(context, textViewResourceId, objects);
        this.accountDataItems = objects;
        this.context = context;
        currencyFormat = NumberFormat.getCurrencyInstance();
        currencyFormat.setCurrency(java.util.Currency.getInstance(currency));
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.list_item_account, null);
        }

        AccountProxy account = accountDataItems.get(position);
        if (account != null) {
            // Set the Status
            ImageView statusImageView = (ImageView) view.findViewById(R.id.account_status);
            if (account.getBalance() < 0) {
                statusImageView.setImageResource(R.drawable.status_red);
            } else if (account.getBalance() > 0 && account.getBalance() < 100) {
                statusImageView.setImageResource(R.drawable.status_orange);
            } else if (account.getBalance() > 100) {
                statusImageView.setImageResource(R.drawable.status_green);
            }

            // Set the Name
            TextView nameTextView = (TextView) view.findViewById(R.id.account_name);
            nameTextView.setText(account.getName());

            // Set the Type of account
            TextView typeTextView = (TextView) view.findViewById(R.id.account_type);
            typeTextView.setText(account.getType().getLabel());

            // Set the Amount of account
            TextView balanceTextView = (TextView) view.findViewById(R.id.account_amount);
            if (account.getBalance() < 0) {
                balanceTextView.setTextColor(context.getResources().getColor(R.color.red));
            } else if (account.getBalance() > 0) {
                balanceTextView.setTextColor(context.getResources().getColor(R.color.green));
            }
            balanceTextView.setText(currencyFormat.format(account.getBalance()));
        }

        return view;
    }
}
