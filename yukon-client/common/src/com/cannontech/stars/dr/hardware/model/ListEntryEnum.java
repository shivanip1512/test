package com.cannontech.stars.dr.hardware.model;

/**
 * Iterface for enums that represent a Yukon selection list
 */
public interface ListEntryEnum {

    /**
     * Method to get the definition id for this instance
     * @return Definition id
     */
    public int getDefinitionId();

    /**
     * method to get the yukon selection list name for this enum
     * @return List name
     */
    public String getListName();
}
