package com.nuvola.gxpenses.client.request.proxy;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyForName("com.nuvola.gxpenses.server.business.Tag")
public interface TagProxy extends ValueProxy {
    Long getId();

    void setId(Long id);

    String getValue();

    void setValue(String value);

    UserProxy getUser();

    void setUser(UserProxy user);
}
