package com.nuvola.gxpenses.client.request.proxy;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;
import com.nuvola.gxpenses.shared.type.FrequencyType;

@ProxyForName("com.nuvola.gxpenses.server.business.Budget")
public interface BudgetProxy extends ValueProxy {
    Long getId();

    void setId(Long id);

    String getName();

    void setName(String name);

    FrequencyType getPeriodicity();

    void setPeriodicity(FrequencyType periodicity);

    Double getTotalAllowed();

    void setTotalAllowed(Double totalAllowed);

    Double getTotalConsumed();

    void setTotalConsumed(Double totalConsumed);

    Integer getPercentageConsumed();

    void setPercentageConsumed(Integer percentageConsumed);

    UserProxy getUser();

    void setUser(UserProxy user);
}
