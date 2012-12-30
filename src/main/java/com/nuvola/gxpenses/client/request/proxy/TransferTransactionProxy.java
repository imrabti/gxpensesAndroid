package com.nuvola.gxpenses.client.request.proxy;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyForName("com.nuvola.gxpenses.server.dto.TransferTransaction")
public interface TransferTransactionProxy extends ValueProxy {
    public AccountProxy getSourceAccount();

    public void setSourceAccount(AccountProxy sourceAccount);

    public AccountProxy getTargetAccount();

    public void setTargetAccount(AccountProxy targetAccount);

    public Double getAmount();

    public void setAmount(Double amount);
}
