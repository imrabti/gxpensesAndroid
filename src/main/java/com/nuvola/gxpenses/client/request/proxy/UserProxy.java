package com.nuvola.gxpenses.client.request.proxy;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;
import com.nuvola.gxpenses.shared.type.CurrencyType;
import com.nuvola.gxpenses.shared.type.PaginationType;

import java.util.Date;

@ProxyForName("com.nuvola.gxpenses.server.business.User")
public interface UserProxy extends ValueProxy {
    Long getId();

    void setId(Long id);

    String getUserName();

    void setUserName(String userName);

    String getEmail();

    void setEmail(String email);

    String getPassword();

    void setPassword(String password);

    CurrencyType getCurrency();

    void setCurrency(CurrencyType currency);

    String getFirstName();

    void setFirstName(String firstName);

    String getLastName();

    void setLastName(String lastName);

    Date getDateCreation();

    void setDateCreation(Date dateCreation);

    PaginationType getPageSize();

    void setPageSize(PaginationType pageSize);
}
