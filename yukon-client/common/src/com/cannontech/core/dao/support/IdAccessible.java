package com.cannontech.core.dao.support;


public interface IdAccessible<T> {
    public T getForId(Integer id);
}