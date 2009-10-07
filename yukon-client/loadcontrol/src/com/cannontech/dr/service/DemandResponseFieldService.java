package com.cannontech.dr.service;

import com.cannontech.dr.DemandResponseBackingField;

/**
 * Interface used to get demand response backing field objects
 * 
 * @param <T> - Type of demand response backing filed to get
 */
public interface DemandResponseFieldService<T> {

    public DemandResponseBackingField<T> getBackingField(String fieldName);
}
