package com.nuvola.gxpenses.client.request;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;
import com.nuvola.gxpenses.client.request.proxy.AccountProxy;

import java.util.List;

@ServiceName(value = "com.nuvola.gxpenses.server.service.AccountServiceImpl",
        locator = "com.nuvola.gxpenses.server.util.SpringServiceLocator")
public interface AccountRequest extends RequestContext {
    Request<Void> createAccount(AccountProxy account);

    Request<Void> removeAccount(Long accountId);

    Request<List<AccountProxy>> findAllAccountsByUserId();
}
