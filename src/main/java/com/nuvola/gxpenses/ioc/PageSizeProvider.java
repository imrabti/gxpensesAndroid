package com.nuvola.gxpenses.ioc;

import com.google.inject.Provider;
import com.nuvola.gxpenses.security.CurrentUser;

import javax.inject.Inject;

public class PageSizeProvider implements Provider<Integer> {
    private final CurrentUser currentUser;

    @Inject
    public PageSizeProvider(final CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public Integer get() {
        return currentUser.get().getPageSize().getValue();
    }
}
