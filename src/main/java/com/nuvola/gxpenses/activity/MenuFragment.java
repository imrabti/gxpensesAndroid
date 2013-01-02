package com.nuvola.gxpenses.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.nuvola.gxpenses.R;
import com.nuvola.gxpenses.activity.budget.BudgetFragment;
import com.nuvola.gxpenses.activity.transaction.AccountFragment;
import com.nuvola.gxpenses.adapter.MenuItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends ListFragment {
    public enum MenuItem {
        ACCOUNT("Account"),
        BUDGET("Budget"),
        REPORT("Report");

        private String label;

        private MenuItem(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static List<MenuItem> getValues() {
            List<MenuItem> menuItems = new ArrayList<MenuItem>();
            menuItems.add(ACCOUNT);
            menuItems.add(BUDGET);
            menuItems.add(REPORT);

            return menuItems;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menu_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MenuItemAdapter colorAdapter = new MenuItemAdapter(getActivity(), R.layout.menu_item, MenuItem.getValues());
        setListAdapter(colorAdapter);
    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {
        Fragment newContent = null;
        switch (position) {
            case 0:
                newContent = new AccountFragment();
                break;
            case 1:
                newContent = new BudgetFragment();
                break;
            case 2:
                newContent = new BudgetFragment();
                break;
        }

        if (newContent != null) {
            switchFragment(newContent);
        }
    }

    private void switchFragment(Fragment fragment) {
        if (getActivity() == null) {
            return;
        }

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.switchContent(fragment);
        }
    }
}

