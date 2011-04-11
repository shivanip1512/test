package com.cannontech.database;

public interface SimpleFieldMapper<T> {
    public void extractValues(SqlParameterSink p, T object);
}
