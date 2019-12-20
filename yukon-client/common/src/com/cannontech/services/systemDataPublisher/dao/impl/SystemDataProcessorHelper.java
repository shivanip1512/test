package com.cannontech.services.systemDataPublisher.dao.impl;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.cannontech.services.systemDataPublisher.service.model.SystemData;
import com.cannontech.services.systemDataPublisher.yaml.model.DictionariesField;

public class SystemDataProcessorHelper {

    private static final Double MAGIC_NUMBER = Double.valueOf(168);
    private static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    /**
     * Process query result , currently the query in YAML will return either single value or List of values.
     * 
     */
    public static SystemData processQueryResult(DictionariesField dictionariesField, List<Map<String, Object>> queryResult) {
        SystemData systemData = null;
        String fieldValue = null;
        if (!queryResult.isEmpty()) {
            systemData = new SystemData();
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
            systemData.setFieldName(dictionariesField.getField());
            systemData.setFieldValue(fieldValue);
            systemData.setIotDataType(dictionariesField.getIotType());
            systemData.setTimestamp(new DateTime());
        }
        return systemData;
    }
}
