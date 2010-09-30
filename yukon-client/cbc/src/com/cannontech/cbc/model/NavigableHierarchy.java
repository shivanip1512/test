package com.cannontech.cbc.model;

import java.util.List;

public interface NavigableHierarchy<T> {
    public T getNode();
    
    public List<? extends NavigableHierarchy<T>> getChildren();
}
