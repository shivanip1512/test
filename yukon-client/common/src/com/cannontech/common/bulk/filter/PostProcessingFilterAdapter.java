package com.cannontech.common.bulk.filter;

import java.util.List;

import com.google.common.collect.Lists;

public abstract class PostProcessingFilterAdapter<T> implements
        PostProcessingFilter<T> {

    protected abstract boolean matches(T object);
    
    @Override
    public List<T> process(List<T> objects) {

        List<T> resultList = Lists.newArrayList();
        
        for(T object : objects) {
            if(matches(object)) {
                resultList.add(object);
            }
        }
        return resultList;
    }
}
