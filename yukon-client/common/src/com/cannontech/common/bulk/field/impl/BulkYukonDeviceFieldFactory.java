package com.cannontech.common.bulk.field.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cannontech.common.bulk.field.BulkField;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.bulk.field.BulkFieldFactory;
import com.cannontech.common.device.model.SimpleDevice;

public class BulkYukonDeviceFieldFactory implements BulkFieldFactory {

    private List<BulkField<?, SimpleDevice>> bulkFields = Collections.emptyList();
    
    public BulkField<?, SimpleDevice> getBulkField(String fieldName) {
        
        for (BulkField<?, SimpleDevice> bulkField : getBulkFields()) {
            if (bulkField.getInputSource().getField().equals(fieldName)) {
                return bulkField;
            }
        }
        
        return null;
    }
    
    
    public List<BulkField<?, SimpleDevice>> getBulkFields() {
        return bulkFields;
    }
    
    public void setBulkFields(List<BulkField<?, SimpleDevice>> bulkFields) {
        this.bulkFields = bulkFields;
    }
    
    public List<BulkField<?, SimpleDevice>> getBulkFieldsForBulkColumnHeaders(List<BulkFieldColumnHeader> columnHeaders) {
        
        final List<BulkField<?, SimpleDevice>> bulkFieldList = new ArrayList<BulkField<?, SimpleDevice>>();
        for (BulkFieldColumnHeader bulkFieldColumnHeader : columnHeaders) {
            BulkField<?, SimpleDevice> bulkField = this.getBulkField(bulkFieldColumnHeader.getFieldName());
            if (bulkField != null) {
                bulkFieldList.add(bulkField);
            }
        }
        
        return bulkFieldList;
    }
}
