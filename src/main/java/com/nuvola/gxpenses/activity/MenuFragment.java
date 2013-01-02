package com.nuvola.gxpenses.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.nuvola.gxpenses.R;
import com.nuvola.gxpenses.activity.budget.BudgetFragment;
import com.nuvola.gxpenses.activity.transaction.AccountFragment;
import com.nuvola.gxpenses.adapter.MenuItemAdapter;
import com.nuvola.gxpenses.security.SecurityUtils;
import com.nuvola.gxpenses.util.Constants;
import roboguice.fragment.RoboListFragment;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends RoboListFragment {
    private static final String TAG = MenuFragment.class.getName();
    private static final boolean DEBUG = Constants.DEBUG;

    public enum MenuItem {
        ACCOUNT("Account"),
        BUDGET("Budget"),
        REPORT("Report"),
        SETTING("Setting"),
        LOGOUT("Log out");

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
            menuItems.add(SETTING);
            menuItems.add(LOGOUT);

            return menuItems;
        }

        public static MenuItem valueOf(int index) {
            MenuItem[] values = values();
            return values[index];
        }
    }

    @Inject
    SecurityUtils securityUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
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
        MenuItem item = MenuItem.valueOf(position);
        Fragment newContent = null;
        switch (item) {
            case ACCOUNT:
                newContent = new AccountFragment();
                break;
            case BUDGET:
                newContent = new BudgetFragment();
                break;
            case REPORT:
                newContent = new BudgetFragment();
                break;
            case SETTING:
                newContent = new BudgetFragment();
                break;
            case LOGOUT:
                if (DEBUG) Log.d(TAG, "Loggin out, cleaning all user data...");
                securityUtils.clearCredentials();
                redirectToLoginActivity();
                break;
        }

        if (newContent != null) {
            switchFragment(newContent);
        }
    }

    private void redirectToLoginActivity() {
        getActivity().setVisible(false);
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().finish();
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

