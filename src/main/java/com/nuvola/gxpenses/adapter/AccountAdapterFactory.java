package com.nuvola.gxpenses.adapter;

import android.app.Activity;
import com.nuvola.gxpenses.client.request.proxy.AccountProxy;

import java.util.List;

public interface AccountAdapterFactory {
    AccountAdapter create(Activity context, Integer textViewResourceId, List<AccountProxy> objects);
}
