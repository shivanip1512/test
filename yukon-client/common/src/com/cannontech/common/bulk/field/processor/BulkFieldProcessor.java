package com.cannontech.common.bulk.field.processor;

import java.util.Set;

import com.cannontech.common.bulk.field.BulkField;

public interface BulkFieldProcessor<I, V> {

    public I updateField(I identifier, V value);
    
    public Set<BulkField<?, I>> getUpdatableFields();
    
}
