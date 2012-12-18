package com.cannontech.amr.device.search.model;

import com.cannontech.common.bulk.filter.SqlFilter;

public interface FilterBy extends SqlFilter {
    /**
     * Method to return a friendly string representation of this FilterBy
     * @return Filter friendly string
     */
    String toSearchString();

    /**
     * Method to return if the filter has a value to filter with
     * @return
     */
    boolean hasValue();
}
