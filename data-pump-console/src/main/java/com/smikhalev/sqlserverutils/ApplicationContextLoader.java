package com.smikhalev.sqlserverutils;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ApplicationContextLoader {

    protected ConfigurableApplicationContext applicationContext;

    protected void loadApplicationContext(String... configLocations) {
        applicationContext = new ClassPathXmlApplicationContext(configLocations);
        applicationContext.registerShutdownHook();
    }

    protected void injectDependencies(Object obj) {
        applicationContext.getBeanFactory().autowireBeanProperties(
                obj, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);


    }

    public void load(Object obj, String... configLocations) {
        loadApplicationContext(configLocations);
        injectDependencies(obj);
    }
}