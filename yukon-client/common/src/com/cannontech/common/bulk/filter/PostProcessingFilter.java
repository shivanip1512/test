package com.cannontech.common.bulk.filter;

public interface PostProcessingFilter<T> {
    public boolean matches(T object);
}
