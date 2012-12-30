package com.nuvola.gxpenses.client.request.proxy;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyForName("com.nuvola.gxpenses.server.dto.DataPage")
public interface DataPageProxy extends ValueProxy {
    Integer getPageNumber();

    void setPageNumber(Integer pageNumber);

    Integer getLength();

    void setLength(Integer length);
}
