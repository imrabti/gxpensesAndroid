package com.nuvola.gxpenses.client.request.proxy;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;
import com.nuvola.gxpenses.shared.type.AccountType;

@ProxyForName("com.nuvola.gxpenses.server.business.Account")
public interface AccountProxy extends ValueProxy {
    Long getId();

    void setId(Long id);

    String getName();

    void setName(String name);

    AccountType getType();

    void setType(AccountType type);

    Double getBalance();

    void setBalance(Double balance);

    UserProxy getUser();

    void setUser(UserProxy user);
}
