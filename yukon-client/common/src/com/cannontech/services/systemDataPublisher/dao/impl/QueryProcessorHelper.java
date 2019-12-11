package com.cannontech.services.systemDataPublisher.dao.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.cannontech.services.systemDataPublisher.service.model.SystemData;
import com.cannontech.services.systemDataPublisher.yaml.model.DictionariesField;
import com.google.common.collect.Lists;

public class QueryProcessorHelper {

    private static final String ELECTRIC_READ_RATE = "electricreadrate";
    private static final String WATER_READ_RATE = "waterreadrate";
    private static final String GAS_READ_RATE = "gasreadrate";
    private static final String DC_ELECTRIC = "dcelectric";
    private static final String DC_WATER = "dcwater";
    private static final Double MAGIC_NUMBER = Double.valueOf(168);
    private static DecimalFormat decimalFormat = new DecimalFormat("0.00");
    /**
     * This method is to build query arguments based on field name.Currently we have few fields which need 
     * parameters inside query, this method will create the arguments based on fields.
     * 
     */
    public static List<Object> getQueryArguments(DictionariesField dictionariesField) {

        List<Object> args = Lists.newArrayList();

        String fromDate = new SimpleDateFormat("yyyy-MM-dd").format((new DateTime()).minusDays(10).toDate());
        String toDate = new SimpleDateFormat("yyyy-MM-dd").format((new DateTime()).minusDays(3).toDate());

        switch (dictionariesField.getField().toLowerCase()) {
        case ELECTRIC_READ_RATE:
        case WATER_READ_RATE:
        case GAS_READ_RATE:
            args.add(toDate);
            break;
        case DC_ELECTRIC:
        case DC_WATER:
            args.add(fromDate);
            args.add(toDate);
            break;
        }

        return args;
    }

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
