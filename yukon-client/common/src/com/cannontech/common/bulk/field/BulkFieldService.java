package com.cannontech.common.bulk.field;

import java.util.List;
import java.util.Set;

import com.cannontech.common.bulk.field.processor.impl.BulkYukonDeviceFieldProcessor;
import com.cannontech.common.bulk.mapper.ObjectMappingException;

public interface BulkFieldService {

    public List<BulkField<?, ?>> getBulkFields();
    
    public List<BulkField<?, ?>> getMassChangableBulkFields();
    
    public Set<BulkFieldColumnHeader> getUpdateableBulkFieldColumnHeaders();
    
    public Set<BulkFieldColumnHeader> getUpdateIdentifierBulkFieldColumnHeaders();
    
    // hard code YukonDevice to make things simpler for now...
    public List<BulkYukonDeviceFieldProcessor> getBulkFieldProcessors();
    
    public <T> boolean processorExistsForBulkFieldColumnHeaders(List<BulkFieldColumnHeader> bulkFieldColumnHeaders);
    
    public BulkFieldColumnHeader getColumnHeaderForFieldName(String fieldName);
    
    public <T, O> O getYukonDeviceForIdentifier(BulkField<T, O> bulkField, String identifier) throws ObjectMappingException, IllegalArgumentException;

    // also hard coded to YukonDevice to make things simpler for now...
    public <T> List<BulkYukonDeviceFieldProcessor> findProcessorsForFields(List<BulkField<?, T>> bulkFields);
}
