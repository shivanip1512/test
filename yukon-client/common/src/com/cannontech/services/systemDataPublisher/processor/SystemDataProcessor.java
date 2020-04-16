package com.cannontech.services.systemDataPublisher.processor;

import com.cannontech.services.systemDataPublisher.service.model.SystemData;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;

public interface SystemDataProcessor {

    /**
     * Checks if the passed field is supported by the processor.
     */
    abstract boolean supportsField(String field);

    /**
     * Build SystemData by executing the queries from database. Based on field name we are building the arguments
     * needed for the query. Process the query result to get the field value.
     */
    abstract SystemData buildSystemData(CloudDataConfiguration configuration);
}
