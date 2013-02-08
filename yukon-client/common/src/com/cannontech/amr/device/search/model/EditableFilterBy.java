package com.cannontech.amr.device.search.model;

import com.cannontech.common.i18n.DisplayableEnum;

public interface EditableFilterBy<T> extends FilterBy, DisplayableEnum {
    /**
     * Method to return the name of the filter
     * @return
     */
    String getName();

    /**
     * Method to get the value of the filter
     * @return
     */
    T getFilterValue();

    /**
     * Method to set the value of the filter
     * @param filterValue
     */
    void setFilterValue(T filterValue);
}
