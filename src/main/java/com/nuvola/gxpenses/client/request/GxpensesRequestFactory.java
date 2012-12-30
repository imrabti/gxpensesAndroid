package com.nuvola.gxpenses.client.request;

import com.google.web.bindery.requestfactory.shared.RequestFactory;

public interface GxpensesRequestFactory extends RequestFactory {
    AccountRequest accountService();

    AuthenticationRequest authenticationService();

    BudgetRequest budgetService();

    TransactionRequest transactionService();

    UserRequest userService();
}
