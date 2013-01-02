package com.nuvola.gxpenses.adapter;

import android.app.Activity;
import com.nuvola.gxpenses.client.request.proxy.BudgetProxy;

import java.util.List;

public interface BudgetAdapterFactory {
    BudgetAdapter create(Activity context, Integer textViewResourceId, List<BudgetProxy> objects);
}
