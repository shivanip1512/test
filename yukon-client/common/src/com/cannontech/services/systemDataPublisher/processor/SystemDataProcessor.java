package com.cannontech.services.systemDataPublisher.processor;

import com.cannontech.services.systemDataPublisher.service.model.SystemDataFieldType.FieldType;

public interface SystemDataProcessor {

    /**
     * Checks if the passed field is supported by the processor.
     */
    abstract boolean supportsField(FieldType field);
}
