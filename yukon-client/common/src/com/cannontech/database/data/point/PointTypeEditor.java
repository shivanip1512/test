package com.cannontech.database.data.point;

import java.beans.PropertyEditorSupport;

import com.cannontech.spring.filtering.exceptions.InvalidFilteringParametersException;

public class PointTypeEditor extends PropertyEditorSupport {
    public void setAsText(String pointTypeString) {
        try {
            setValue(PointType.valueOf(pointTypeString));
        } catch (Exception ex) {
            throw new InvalidFilteringParametersException(pointTypeString + " is an invalid PointType.");
        }
    }
}
