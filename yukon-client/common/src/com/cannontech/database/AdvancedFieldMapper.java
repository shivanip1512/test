package com.cannontech.database;

public interface AdvancedFieldMapper<T> extends BaseFieldMapper<T> {
    public void extractValues(SqlParameterSink p, T rule);
    
}
