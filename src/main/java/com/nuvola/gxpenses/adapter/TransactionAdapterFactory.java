package com.nuvola.gxpenses.adapter;

import android.app.Activity;
import com.nuvola.gxpenses.client.request.proxy.TransactionProxy;

import java.util.List;

public interface TransactionAdapterFactory {
    TransactionAdapter create(Activity context, Integer textViewResourceId, List<TransactionProxy> objects,
                              Long numTransactions);
}
