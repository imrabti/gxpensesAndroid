package com.nuvola.gxpenses.client.request;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;
import com.nuvola.gxpenses.client.request.proxy.DataPageProxy;
import com.nuvola.gxpenses.client.request.proxy.PagedTransactionsProxy;
import com.nuvola.gxpenses.client.request.proxy.TransactionProxy;
import com.nuvola.gxpenses.client.request.proxy.TransferTransactionProxy;

@ServiceName(value = "com.nuvola.gxpenses.server.service.TransactionServiceImpl",
        locator = "com.nuvola.gxpenses.server.util.SpringServiceLocator")
public interface TransactionRequest extends RequestContext {
    Request<Void> createNewTransaction(TransactionProxy transaction);

    Request<Void> removeTransaction(Long transactionId);

    Request<Void> createNewTransferTransaction(TransferTransactionProxy transfer);

    Request<PagedTransactionsProxy> findByAccount(Long accountId, DataPageProxy dataPage);
}
