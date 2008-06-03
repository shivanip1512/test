package com.cannontech.common.bulk.field;


/**
 * Factory interface used to create BulkField (There is not
 * implementation of this class - we use the spring ServiceLocatorFactoryBean
 * which auto-implements this interface)
 */
public interface BulkFieldFactory {

    /**
     * Create a BulkField for the given BulkField type
     * 
     */
    public BulkField<?, ?> getBulkField(String fieldName);
    
}
