package com.cannontech.dr;

import java.util.Comparator;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.user.YukonUserContext;

/**
 * Interface for demand response fields 
 * @param <T>
 */
public interface DemandResponseBackingField<T> {
    
    public String getFieldName();

    /**
     * Method to get this field's value from the object passed in
     * @param object - Object to get field value for
     * @param userContext - Current userContext
     * @return Value of this field
     */
    public Object getValue(T object, YukonUserContext userContext);

    /**
     * Method to get a DisplayablePao Comparator for this field 
     * @param userContext - Current userContext
     * @return Comparator for this field
     */
    public Comparator<DisplayablePao> getSorter(YukonUserContext userContext);
}
