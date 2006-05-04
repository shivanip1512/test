package com.cannontech.cc.dao;


public interface StandardDaoOperations<T> {

    public void save(T object);

    public void delete(T object);

    public T getForId(Integer id);
}