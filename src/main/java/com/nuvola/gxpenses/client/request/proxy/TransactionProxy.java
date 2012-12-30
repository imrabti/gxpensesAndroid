package com.nuvola.gxpenses.client.request.proxy;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;
import com.nuvola.gxpenses.shared.type.TransactionType;

import java.util.Date;

@ProxyForName("com.nuvola.gxpenses.server.business.Transaction")
public interface TransactionProxy extends ValueProxy {
    Long getId();

    void setId(Long id);

    String getPayee();

    void setPayee(String payee);

    TransactionType getType();

    void setType(TransactionType type);

    Date getDate();

    void setDate(Date date);

    Double getAmount();

    void setAmount(Double amount);

    String getTags();

    void setTags(String tags);

    AccountProxy getAccount();

    void setAccount(AccountProxy account);

    TransactionProxy getDestTransaction();

    void setDestTransaction(TransactionProxy destTransaction);
}
