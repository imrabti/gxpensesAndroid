package com.nuvola.gxpenses.util;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.nuvola.gxpenses.client.request.GxpensesRequestFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuggestionListFactory {
    private final GxpensesRequestFactory requestFactory;

    private List<String> listTags;
    private List<String> listPayee;

    @Inject
    public SuggestionListFactory(final GxpensesRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    public List<String> getListTags() {
        if (listTags == null) {
            requestFactory.userService().findAllTagsForUser().fire(new Receiver<List<String>>() {
                @Override
                public void onSuccess(List<String> tags) {
                    listTags = tags;
                }
            });
        }

        return listTags;
    }

    public List<String> getListPayee() {
        if (listPayee == null) {
            requestFactory.userService().findAllPayeeForUser().fire(new Receiver<List<String>>() {
                @Override
                public void onSuccess(List<String> payees) {
                    listPayee = payees;
                }
            });
        }

        return listPayee;
    }

    public void updatePayeeList(String payee) {
        if (!listPayee.contains(payee)) {
            listPayee.add(payee);
        }
    }

    public void updateTagsList(String tag) {
        if (!Strings.isNullOrEmpty(tag)) {
            List<String> tags = Arrays.asList(tag.split(","));
            List<String> toAdd = new ArrayList<String>();

            for (String item : tags) {
                if (!listTags.contains(item.trim().toLowerCase())) {
                    listTags.add(item);
                    toAdd.add(item);
                }
            }

            requestFactory.userService().createTags(toAdd).fire(new Receiver<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            });
        }
    }
}
