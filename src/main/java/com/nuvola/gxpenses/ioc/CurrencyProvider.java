package com.nuvola.gxpenses.ioc;

import com.google.inject.Provider;
import com.nuvola.gxpenses.security.CurrentUser;

import javax.inject.Inject;

public class CurrencyProvider implements Provider<String> {
    private final CurrentUser currentUser;

    @Inject
    public CurrencyProvider(final CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public String get() {
        return currentUser.get().getCurrency().getValue();
    }
}
