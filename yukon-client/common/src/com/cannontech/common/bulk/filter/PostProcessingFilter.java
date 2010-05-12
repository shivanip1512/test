package com.cannontech.common.bulk.filter;

import java.util.List;

public interface PostProcessingFilter<T> {
    public boolean matches(T object);
    public List<T> process(List<T> objectsFromDb);
}
