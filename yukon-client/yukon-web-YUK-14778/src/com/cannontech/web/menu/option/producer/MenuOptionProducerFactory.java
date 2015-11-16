package com.cannontech.web.menu.option.producer;

/**
 * Factory interface used to create a menu option producer (There is not
 * implementation of this class - we use the spring ServiceLocatorFactoryBean
 * which auto-implements this interface)
 */
public interface MenuOptionProducerFactory {

    /**
     * Create a menu option producer for the given menu option type
     * @param menuOptionType - Type of menu option producer to create
     * @return The menu option producer for the type
     */
    public DynamicMenuOptionProducer createMenuOptions(String menuOptionType);

}
