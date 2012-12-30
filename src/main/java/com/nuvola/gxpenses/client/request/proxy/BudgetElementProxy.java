package com.nuvola.gxpenses.client.request.proxy;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyForName("com.nuvola.gxpenses.server.business.BudgetElement")
public interface BudgetElementProxy extends ValueProxy {
    Long getId();

    void setId(Long id);

    String getTag();

    void setTag(String tag);

    Double getAmount();

    void setAmount(Double amount);

    Double getConsumedAmount();

    void setConsumedAmount(Double consumedAmount);

    Double getLeftAmount();

    void setLeftAmount(Double leftAmount);

    BudgetProxy getBudget();

    void setBudget(BudgetProxy budget);
}
