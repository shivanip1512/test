package com.cannontech.common.bulk.field.processor.impl;

import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.bulk.field.BulkField;
import com.cannontech.common.bulk.field.impl.YukonDeviceDto;
import com.cannontech.common.bulk.field.processor.BulkFieldProcessor;
import com.cannontech.common.device.YukonDevice;

public abstract class BulkYukonDeviceFieldProcessor implements BulkFieldProcessor<YukonDevice, YukonDeviceDto> {

    private Set<BulkField<?, YukonDevice>> updateableFields = Collections.emptySet();
    
    @Override
    public Set<BulkField<?, YukonDevice>> getUpdatableFields() {
        return updateableFields;
    }
    
    @Required
    public void setUpdateableFields(Set<BulkField<?, YukonDevice>> updateableFields) {
        this.updateableFields = updateableFields;
    }
}
