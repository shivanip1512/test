package com.cannontech.common.bulk.filter;

import java.util.List;

public interface PostProcessingFilter<T> {
    public List<T> process(List<T> objectsFromDb);
}
