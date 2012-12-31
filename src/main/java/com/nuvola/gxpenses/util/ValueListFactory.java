package com.nuvola.gxpenses.util;

import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.nuvola.gxpenses.client.request.GxpensesRequestFactory;
import com.nuvola.gxpenses.client.request.proxy.AccountProxy;

import java.util.List;

public class ValueListFactory {
    private final GxpensesRequestFactory requestFactory;

    private List<AccountProxy> listAccounts;

    @Inject
    public ValueListFactory(final GxpensesRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    public List<AccountProxy> getListAccounts() {
        if (listAccounts == null) {
            updateListAccount();
        }

        return listAccounts;
    }

    public AccountProxy getAccountById(Long accountId) {
        for (AccountProxy account : listAccounts) {
            if (accountId == account.getId()) {
                return account;
            }
        }

        return null;
    }

    public void updateListAccount() {
        requestFactory.accountService().findAllAccountsByUserId().fire(new Receiver<List<AccountProxy>>() {
            @Override
            public void onSuccess(List<AccountProxy> accounts) {
                listAccounts = accounts;
            }
        });
    }
}
