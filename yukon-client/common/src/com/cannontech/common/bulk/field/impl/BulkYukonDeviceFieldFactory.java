package com.cannontech.common.bulk.field.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cannontech.common.bulk.field.BulkField;
import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.bulk.field.BulkFieldFactory;
import com.cannontech.common.device.YukonDevice;

public class BulkYukonDeviceFieldFactory implements BulkFieldFactory {

    private List<BulkField<?, YukonDevice>> bulkFields = Collections.emptyList();
    
    public BulkField<?, YukonDevice> getBulkField(String fieldName) {
        
        for (BulkField<?, YukonDevice> bulkField : getBulkFields()) {
            if (bulkField.getInputSource().getField().equals(fieldName)) {
                return bulkField;
            }
        }
        
        return null;
    }
    
    
    public List<BulkField<?, YukonDevice>> getBulkFields() {
        return bulkFields;
    }
    
    public void setBulkFields(List<BulkField<?, YukonDevice>> bulkFields) {
        this.bulkFields = bulkFields;
    }
    
    public List<BulkField<?, YukonDevice>> getBulkFieldsForBulkColumnHeaders(List<BulkFieldColumnHeader> columnHeaders) {
        
        final List<BulkField<?, YukonDevice>> bulkFieldList = new ArrayList<BulkField<?, YukonDevice>>();
        for (BulkFieldColumnHeader bulkFieldColumnHeader : columnHeaders) {
            BulkField<?, YukonDevice> bulkField = this.getBulkField(bulkFieldColumnHeader.getFieldName());
            bulkFieldList.add(bulkField);
        }
        
        return bulkFieldList;
    }
}
