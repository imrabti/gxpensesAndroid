package com.nuvola.gxpenses.util;

import android.app.ListFragment;
import android.os.Bundle;
import roboguice.RoboGuice;

public abstract class EnhancedListFragment extends ListFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);
    }
}
