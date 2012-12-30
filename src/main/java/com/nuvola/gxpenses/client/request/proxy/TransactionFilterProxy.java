package com.nuvola.gxpenses.client.request.proxy;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;
import com.nuvola.gxpenses.shared.type.PeriodType;
import com.nuvola.gxpenses.shared.type.TransactionType;

@ProxyForName("com.nuvola.gxpenses.server.dto.TransactionFilter")
public interface TransactionFilterProxy extends ValueProxy {
    Long getAccountId();

    void setAccountId(Long accountId);

    PeriodType getPeriod();

    void setPeriod(PeriodType period);

    TransactionType getType();

    void setType(TransactionType type);
}
