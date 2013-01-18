package com.cannontech.amr.meter.search.model;

import com.cannontech.amr.meter.model.PointSortField;

/**
 * Class used to sort a collection of points
 */
public class PointSortOrderBy {

    private boolean descending = false;
    private PointSortField field = PointSortField.POINTNAME;

    public PointSortOrderBy(String field, boolean descending) {
        if (field != null && field.length() > 0) {
            this.field = PointSortField.valueOf(field);
        }

        this.descending = descending;
    }

    public boolean isDescending() {
        return descending;
    }

    public void setDescending(boolean descending) {
        this.descending = descending;
    }

    public PointSortField getField() {
        return field;
    }

    public void setField(PointSortField field) {
        this.field = field;
    }
}
