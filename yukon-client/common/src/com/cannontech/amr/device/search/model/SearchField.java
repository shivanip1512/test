package com.cannontech.amr.device.search.model;

import com.cannontech.common.i18n.DisplayableEnum;

public interface SearchField extends DisplayableEnum {
    /**
     * Name of the field, for reference purpose
     * 
     * @return the name of the field
     */
    String getFieldName();
    
    /**
     * The name of the field to be used in SQL queries
     * 
     * Examples:
     * my_database_field
     * my_alias.my_field
     * 
     * @return the name of the field
     */
    String getQueryField();
    
    /**
     * Returns if the field should be visible or not
     * 
     * @return
     */
    boolean isVisible();
}
