package com.cannontech.common.bulk.field.impl;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.bulk.field.BulkField;
import com.cannontech.common.bulk.mapper.ObjectMapperFactory;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.web.input.InputSource;

public abstract class BulkFieldBase<T, O> implements BulkField<T, O> {

    private final String keyPrefix = "yukon.common.device.bulk.massChange.";
    
    protected PaoDao paoDao = null;
    protected ObjectMapperFactory objectMapperFactory = null;
    
    private boolean massChangable;
    private InputSource<T> inputSource;
    
    @Override
    public InputSource<T> getInputSource() {
        return inputSource;
    }
    
    public void setInputSource(InputSource<T> inputSource) {
        this.inputSource = inputSource;
    }
    
    // default mapper is null for fields that are not identifier fields 
    // (those which can be used to return an object of the field's owners type [such as YukonDevice])
    public ObjectMapper<T, O> getIdentifierMapper() {
        return null;
    }

    // I18N
    public String getDisplayKey() {
        return this.keyPrefix + this.inputSource.getField();
    }

    public String getDescriptionKey() {
        return this.getDisplayKey() + ".description";
    }
    
    
    @Override
    public String toString() {
        
        return this.inputSource.getDisplayName();
    }
    
    
    // ATTRIBUTES
    public boolean getMassChangable() {
        return massChangable;
    }
    
    @Required
    public void setMassChangable(boolean massChangable) {
        this.massChangable = massChangable;
    }

    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    @Required
    public void setObjectMapperFactory(ObjectMapperFactory objectMapperFactory) {
        this.objectMapperFactory = objectMapperFactory;
    }
}
