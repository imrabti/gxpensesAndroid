package com.nuvola.gxpenses.client.request;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;
import com.nuvola.gxpenses.client.request.proxy.PasswordProxy;
import com.nuvola.gxpenses.client.request.proxy.UserProxy;

import java.util.List;

@ServiceName(value = "com.nuvola.gxpenses.server.service.UserServiceImpl",
             locator = "com.nuvola.gxpenses.server.util.SpringServiceLocator")
public interface UserRequest extends RequestContext {
    Request<Void> createUser(UserProxy user);

    Request<Void> updateUser(UserProxy user);

    Request<Void> updatePassword(PasswordProxy password);

    Request<List<String>> findAllPayeeForUser();

    Request<List<String>> findAllTagsForUser();

    Request<Void> createTags(List<String> tags);

    Request<Void> updateTags(List<String> tags);
}
