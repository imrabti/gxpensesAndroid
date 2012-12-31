package com.nuvola.gxpenses.security;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.nuvola.gxpenses.client.request.GxpensesRequestFactory;
import com.nuvola.gxpenses.client.request.proxy.UserProxy;

import javax.inject.Inject;

public class CurrentUserImpl implements CurrentUser {
    private final GxpensesRequestFactory requestFactory;
    private final SecurityUtils securityUtils;

    private UserProxy currentUser;

    @Inject
    public CurrentUserImpl(final GxpensesRequestFactory requestFactory,
                           final SecurityUtils securityUtils) {
        this.requestFactory = requestFactory;
        this.securityUtils = securityUtils;
    }

    @Override
    public void init() {
        if (securityUtils.isLoggedIn() && currentUser != null) {
            requestFactory.authenticationService().currentUser().fire(new Receiver<UserProxy>() {
                @Override
                public void onSuccess(UserProxy userProxy) {
                    currentUser = userProxy;
                }
            });
        }
    }

    @Override
    public UserProxy get() {
        return currentUser;
    }
}
