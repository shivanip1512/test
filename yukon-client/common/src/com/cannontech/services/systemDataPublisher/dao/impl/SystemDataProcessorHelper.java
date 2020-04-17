package com.cannontech.services.systemDataPublisher.dao.impl;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.cannontech.services.systemDataPublisher.service.model.SystemData;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;

public class SystemDataProcessorHelper {

    private static final Double MAGIC_NUMBER = Double.valueOf(168);
    private static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    /**
     * Process query result , currently the query in YAML will return either single value or List of values.
     * 
     */
    public static SystemData processQueryResult(CloudDataConfiguration cloudDataConfiguration, List<Map<String, Object>> queryResult) {
        String fieldValue = null;
        if (!queryResult.isEmpty()) {
            if (queryResult.size() == 1 ) {
                fieldValue = queryResult.get(0).entrySet()
                                               .stream()
                                               .map(entity -> entity.getValue())
                                               .findFirst().get().toString();;
            } else {
                Double actual = queryResult.stream()
                                            .flatMap(entity -> entity.entrySet().stream())
                                            .map(entity -> Double.valueOf(entity.getValue().toString()))
                                            .reduce(Double.valueOf(0), (x,y) -> x + y);
                Double expected = queryResult.size() * MAGIC_NUMBER;
                Double finalValue = (actual / expected) * 100 ;
                fieldValue = decimalFormat.format(finalValue);
            }
        }
        SystemData systemData = buildSystemData(cloudDataConfiguration, fieldValue);
        return systemData;
    }
    
    /**
     * Builds System Data object.
     */
    public static SystemData buildSystemData(CloudDataConfiguration cloudDataConfiguration, String fieldValue) {
        SystemData systemData = new SystemData();
        systemData.setFieldName(cloudDataConfiguration.getField().getStringValue());
        systemData.setFieldValue(fieldValue);
        systemData.setIotDataType(cloudDataConfiguration.getIotType());
        systemData.setTimestamp(new DateTime());
        
        return systemData;
    }
    
    /**
     * Process data for "Other" field types available in YAML.
     */
    public static SystemData processOtherData(CloudDataConfiguration cloudDataConfiguration) {
        SystemData systemData = new SystemData();
        systemData.setFieldName(cloudDataConfiguration.getField().getStringValue());
        systemData.setFieldValue(cloudDataConfiguration.getSource());
        systemData.setIotDataType(cloudDataConfiguration.getIotType());
        systemData.setTimestamp(new DateTime());
        return systemData;
    }
}
