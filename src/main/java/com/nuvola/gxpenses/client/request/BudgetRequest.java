package com.nuvola.gxpenses.client.request;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;
import com.nuvola.gxpenses.client.request.proxy.BudgetElementProxy;
import com.nuvola.gxpenses.client.request.proxy.BudgetProxy;

import java.util.Date;
import java.util.List;

@ServiceName(value = "com.nuvola.gxpenses.server.service.BudgetServiceImpl",
        locator = "com.nuvola.gxpenses.server.util.SpringServiceLocator")
public interface BudgetRequest extends RequestContext {
    Request<Void> createBudget(BudgetProxy budget);

    Request<Void> createBudgetElement(Long budgetId, BudgetElementProxy element);

    Request<Void> removeBudgetElement(Long budgetElementId);

    Request<List<BudgetProxy>> findAllBudgetsByUserId(Date period);

    Request<List<BudgetElementProxy>> findAllBudgetElementsByBudget(Long budgetId);

    Request<List<BudgetElementProxy>> findAllBudgetElementsByBudget(Long budgetId, Date period);
}
