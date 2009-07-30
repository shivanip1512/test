package com.cannontech.common.bulk.field.processor.impl;

import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.bulk.field.BulkField;
import com.cannontech.common.bulk.field.impl.YukonDeviceDto;
import com.cannontech.common.bulk.field.processor.BulkFieldProcessor;
import com.cannontech.common.device.model.SimpleDevice;

public abstract class BulkYukonDeviceFieldProcessor implements BulkFieldProcessor<SimpleDevice, YukonDeviceDto> {

    private Set<BulkField<?, SimpleDevice>> updateableFields = Collections.emptySet();
    
    @Override
    public Set<BulkField<?, SimpleDevice>> getUpdatableFields() {
        return updateableFields;
    }
    
    @Required
    public void setUpdateableFields(Set<BulkField<?, SimpleDevice>> updateableFields) {
        this.updateableFields = updateableFields;
    }
}
