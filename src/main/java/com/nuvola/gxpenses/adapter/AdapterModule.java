package com.nuvola.gxpenses.adapter;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class AdapterModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().build(AccountAdapterFactory.class));
    }
}