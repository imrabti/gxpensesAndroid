package com.nuvola.gxpenses.adapter;

import android.app.Activity;
import com.nuvola.gxpenses.client.request.proxy.BudgetElementProxy;

import java.util.List;

public interface BudgetElementAdapterFactory {
    BudgetElementAdapter create(Activity context, Integer textViewResourceId, List<BudgetElementProxy> objects);
}
