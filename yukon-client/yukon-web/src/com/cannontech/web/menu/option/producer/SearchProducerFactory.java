package com.cannontech.web.menu.option.producer;

/**
 * Factory interface used to create a menu option producer (There is not
 * implementation of this class - we use the spring ServiceLocatorFactoryBean
 * which auto-implements this interface)
 */
public interface SearchProducerFactory {

    /**
     * Get a SearchProducer that will provide details about how to render the search field.
     */
    public SearchProducer getSearchProducer(String beanName);

}
