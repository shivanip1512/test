package com.cannontech.web.updater.dr;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class EstimatedLoadFieldServiceImpl implements EstimatedLoadFieldService {

    private final Map<String, EstimatedLoadBackingField> backingFieldMap;

    @Autowired
    public EstimatedLoadFieldServiceImpl(List<EstimatedLoadBackingField> backingFieldList) {
        Builder<String, EstimatedLoadBackingField> builder = 
            new ImmutableMap.Builder<String, EstimatedLoadBackingField>();
        for(EstimatedLoadBackingField backingField : backingFieldList) {
            builder.put(backingField.getFieldName(), backingField);
        }
        backingFieldMap = builder.build();
    }

    @Override
    public EstimatedLoadBackingField getBackingField(String fieldName) {
        return backingFieldMap.get(fieldName);
    }

}
