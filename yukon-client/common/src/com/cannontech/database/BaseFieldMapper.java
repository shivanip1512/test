package com.cannontech.database;

public interface BaseFieldMapper<T> {
    public Number getPrimaryKey(T object);
    public void setPrimaryKey(T object, int value);
}
