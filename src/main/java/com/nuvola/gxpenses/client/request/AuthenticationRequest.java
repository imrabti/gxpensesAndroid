package com.nuvola.gxpenses.client.request;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;
import com.nuvola.gxpenses.client.request.proxy.UserProxy;

@ServiceName(value = "com.nuvola.gxpenses.server.security.AuthenticationServiceImpl",
             locator = "com.nuvola.gxpenses.server.util.SpringServiceLocator")
public interface AuthenticationRequest extends RequestContext {
    Request<UserProxy> currentUser();

    Request<Boolean> authenticate(String username, String password);
}
