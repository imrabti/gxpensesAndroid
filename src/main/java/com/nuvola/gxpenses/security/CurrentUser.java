package com.nuvola.gxpenses.security;

import com.nuvola.gxpenses.client.request.proxy.UserProxy;

public interface CurrentUser {
    void init();

    UserProxy get();
}
