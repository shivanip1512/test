package com.cannontech.core.dao.support;

public interface IdentifiableObjectProvider<T extends Identifiable> {
    public T getForId(Integer id);
}