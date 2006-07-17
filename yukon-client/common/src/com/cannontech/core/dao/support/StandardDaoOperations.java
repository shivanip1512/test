package com.cannontech.core.dao.support;


public interface StandardDaoOperations<T> {

    public void save(T object);

    public void delete(T object);

    public T getForId(Integer id);
}