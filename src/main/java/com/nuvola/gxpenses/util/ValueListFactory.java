package com.nuvola.gxpenses.util;

import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.nuvola.gxpenses.client.request.GxpensesRequestFactory;
import com.nuvola.gxpenses.client.request.proxy.AccountProxy;
import com.nuvola.gxpenses.client.request.proxy.BudgetProxy;

import java.util.Date;
import java.util.List;

public class ValueListFactory {
    private final GxpensesRequestFactory requestFactory;

    private List<AccountProxy> listAccounts;
    private List<BudgetProxy> listBudgets;

    @Inject
    public ValueListFactory(final GxpensesRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    public List<AccountProxy> getListAccounts() {
        if (listAccounts == null) {
            updateListAccounts();
        }

        return listAccounts;
    }

    public List<BudgetProxy> getListBudgets() {
        if (listBudgets == null) {
            updateListBudgets();
        }

        return listBudgets;
    }

    public AccountProxy getAccountById(Long accountId) {
        for (AccountProxy account : listAccounts) {
            if (accountId == account.getId()) {
                return account;
            }
        }

        return null;
    }

    public BudgetProxy getBudgetById(Long budgetId) {
        for (BudgetProxy budget : listBudgets) {
            if (budgetId == budget.getId()) {
                return budget;
            }
        }

        return null;
    }

    public void updateListAccounts() {
        requestFactory.accountService().findAllAccountsByUserId().fire(new Receiver<List<AccountProxy>>() {
            @Override
            public void onSuccess(List<AccountProxy> accounts) {
                listAccounts = accounts;
            }
        });
    }

    public void updateListBudgets() {
        requestFactory.budgetService().findAllBudgetsByUserId(new Date()).fire(new Receiver<List<BudgetProxy>>() {
            @Override
            public void onSuccess(List<BudgetProxy> budgets) {
                listBudgets = budgets;
            }
        });
    }
}
