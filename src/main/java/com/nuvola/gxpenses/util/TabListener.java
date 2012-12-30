package com.nuvola.gxpenses.util;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

public class TabListener<T extends Fragment> implements ActionBar.TabListener {
    private Fragment mFragment;
    private final Activity mActivity;
    private final String mTag;
    private ViewPager mPager;

    public TabListener(Activity activity, String tag, ViewPager pager) {
        mActivity = activity;
        mTag = tag;
        mPager = pager;
    }

    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        mPager.setCurrentItem(Integer.parseInt(mTag));
    }

    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }
}

