package com.nuvola.gxpenses.client.request.proxy;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

import java.util.List;

@ProxyForName("com.nuvola.gxpenses.server.dto.PagedTransactions")
public interface PagedTransactionsProxy extends ValueProxy {
    List<TransactionProxy> getTransactions();

    void setTransactions(List<TransactionProxy> transactions);

    Integer getTotalElements();

    void setTotalElements(Integer totalElements);
}
