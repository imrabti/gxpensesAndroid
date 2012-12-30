package com.nuvola.gxpenses.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.nuvola.gxpenses.client.request.proxy.AccountProxy;

import java.util.List;

public class AccountSpinnerAdapter extends ArrayAdapter<AccountProxy> {
    private List<AccountProxy> accountDataItems;
    private Activity context;

    public AccountSpinnerAdapter(Activity context, List<AccountProxy> objects) {
        super(context, android.R.layout.simple_spinner_item, objects);
        this.accountDataItems = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(android.R.layout.simple_spinner_item, null);
        }

        AccountProxy account = accountDataItems.get(position);
        if (account != null) {
            // Set the Name
            TextView nameTextView = (TextView) view.findViewById(android.R.id.text1);
            nameTextView.setText(account.getName());
        }

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(android.R.layout.simple_spinner_dropdown_item, null);
        }

        AccountProxy account = accountDataItems.get(position);
        if (account != null) {
            // Set the Name
            TextView nameTextView = (TextView) view.findViewById(android.R.id.text1);
            nameTextView.setText(account.getName());
        }

        return view;
    }
}
