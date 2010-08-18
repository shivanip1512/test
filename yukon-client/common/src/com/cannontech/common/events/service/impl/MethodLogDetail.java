package com.cannontech.common.events.service.impl;

import java.util.Collections;
import java.util.Map;

import com.cannontech.common.events.model.ArgumentColumn;
import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventParameter;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.google.common.collect.Maps;

public class MethodLogDetail {
    private ExecutorTransactionality transactionality = ExecutorTransactionality.TRANSACTIONAL;
    private ObjectMapper<Object[], Object[]> valueMapper = null;
    private EventCategory eventCategory = null;
    private String methodName = null;
    private boolean isLogging = true;
    
    private Map<EventParameter, ArgumentColumn> parameterToColumnMapping = Maps.newLinkedHashMap() ; 
    private Map<ArgumentColumn, EventParameter> columnToParameterMapping = Maps.newLinkedHashMap();
    
    public Map<EventParameter, ArgumentColumn> getParameterToColumnMapping() {
        return Collections.unmodifiableMap(parameterToColumnMapping);
    }
    
    public Map<ArgumentColumn, EventParameter> getColumnToParameterMapping() {
        return Collections.unmodifiableMap(columnToParameterMapping);
    }
    
    public EventCategory getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(EventCategory eventCategory) {
        this.eventCategory = eventCategory;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void addColomnAndParameterMapping(ArgumentColumn argumentColumn, EventParameter eventParameter) {
        parameterToColumnMapping.put(eventParameter, argumentColumn);
        columnToParameterMapping.put(argumentColumn, eventParameter);
    }

    public String getEventType() {
        String eventType = eventCategory.getFullName() + "." + methodName;
        return eventType;
    }
    
    public void setValueMapper(ObjectMapper<Object[], Object[]> valueMapper) {
        this.valueMapper = valueMapper;
    }

    public ObjectMapper<Object[], Object[]> getValueMapper() {
        return valueMapper;
    }

    public void setLogging(boolean isLogging) {
        this.isLogging = isLogging;
    }

    public boolean isLogging() {
        return isLogging;
    }

    public String getFullPath(){
        return eventCategory.getFullName()+"."+methodName;
    }

    public void setTransactionality(ExecutorTransactionality transactionality) {
        this.transactionality = transactionality;
    }

    public ExecutorTransactionality getTransactionality() {
        return transactionality;
    }
    
    @Override
    public String toString() {
        return "MethodLogDetail [eventCategory=" + eventCategory.getFullName() + ", "+
                                 "methodName=" + methodName + ", "+
                                 "isLogging=" + isLogging + ", "+
                                 "parameterToColumnMapping=" + parameterToColumnMapping + ", "+
                                 "columnToParameterMapping="+ columnToParameterMapping + ", "+
                                 "transactionality=" + transactionality + ", "+
                                 "valueMapper=" + valueMapper + "]";
    }
    
}