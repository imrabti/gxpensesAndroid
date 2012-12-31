package com.nuvola.gxpenses.ioc;

import com.google.inject.AbstractModule;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.google.web.bindery.requestfactory.vm.RequestFactorySource;
import com.nuvola.gxpenses.adapter.AdapterModule;
import com.nuvola.gxpenses.client.request.GxpensesRequestFactory;
import com.nuvola.gxpenses.security.CurrentUser;
import com.nuvola.gxpenses.security.CurrentUserImpl;
import com.nuvola.gxpenses.security.SecuredRequestTransport;
import com.nuvola.gxpenses.security.SecurityUtils;
import com.nuvola.gxpenses.util.Constants;
import com.nuvola.gxpenses.util.SuggestionListFactory;
import com.nuvola.gxpenses.util.ValueListFactory;
import roboguice.inject.SharedPreferencesName;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

public class MainModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new AdapterModule());

        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
        bind(GxpensesRequestFactory.class).toProvider(RequestFactoryProvider.class).in(Singleton.class);
        bind(CurrentUser.class).to(CurrentUserImpl.class).in(Singleton.class);

        bind(SecurityUtils.class).in(Singleton.class);
        bind(SuggestionListFactory.class).in(Singleton.class);
        bind(ValueListFactory.class).in(Singleton.class);

        bindConstant().annotatedWith(SharedPreferencesName.class).to(Constants.PREFERENCES_NAME);
        bindConstant().annotatedWith(ApiURL.class).to(Constants.API_URL);
        bind(String.class).annotatedWith(Currency.class).toProvider(CurrencyProvider.class);
        bind(Integer.class).annotatedWith(PageSize.class).toProvider(PageSizeProvider.class);
    }

    static class RequestFactoryProvider implements Provider<GxpensesRequestFactory> {
        private final GxpensesRequestFactory requestFactory;

        @Inject
        public RequestFactoryProvider(final EventBus eventBus, final SecuredRequestTransport requestTransport) {
            requestFactory = RequestFactorySource.create(GxpensesRequestFactory.class);
            requestFactory.initialize(eventBus, requestTransport);
        }

        public GxpensesRequestFactory get() {
            return requestFactory;
        }
    }
}
