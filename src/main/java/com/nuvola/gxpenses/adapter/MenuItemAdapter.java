package com.nuvola.gxpenses.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nuvola.gxpenses.R;
import com.nuvola.gxpenses.activity.MenuFragment.MenuItem;

import java.util.List;

public class MenuItemAdapter extends ArrayAdapter<MenuItem> {
    private List<MenuItem> menuItems;
    private Activity context;

    public MenuItemAdapter(Activity context, Integer textViewResourceId, List<MenuItem> menuItems) {
        super(context, textViewResourceId, menuItems);
        this.context = context;
        this.menuItems = menuItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.menu_item, null);
        }

        MenuItem menuItem = menuItems.get(position);
        if (menuItem != null) {
            // Set the Status
            ImageView menuImageView = (ImageView) view.findViewById(R.id.menu_icon);
            menuImageView.setImageResource(R.drawable.status_red);

            // Set the Name
            TextView menuLabelTextView = (TextView) view.findViewById(R.id.menu_label);
            menuLabelTextView.setText(menuItem.getLabel());
        }

        return view;
    }
}
