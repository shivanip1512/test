package com.cannontech.common.events.service.impl;

import java.util.Collections;
import java.util.Map;

import com.cannontech.common.events.model.ArgumentColumn;
import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventParameter;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class MethodLogDetail {
    private ExecutorTransactionality transactionality = ExecutorTransactionality.TRANSACTIONAL;
    private ObjectMapper<Object[], Object[]> valueMapper = null;
    EventCategory eventCategory = null;
    String methodName = null;
    private boolean isLogging = true; 
    private BiMap<EventParameter, ArgumentColumn> parameterToColumnMapping = HashBiMap.create(15);
    
    public Map<EventParameter, ArgumentColumn> getParameterToColumnMapping() {
        return Collections.unmodifiableMap(parameterToColumnMapping);
    }
    
    public Map<ArgumentColumn, EventParameter> getColumnToParameterMapping() {
        return Collections.unmodifiableMap(parameterToColumnMapping.inverse());
    }
    
    public void addColomnAndParameterMapping(ArgumentColumn argumentColumn, EventParameter eventParameter) {
        parameterToColumnMapping.put(eventParameter, argumentColumn);
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

    @Override
    public String toString() {
        return eventCategory.getFullName() + "." + methodName + ": " + getValueMapper();
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
}