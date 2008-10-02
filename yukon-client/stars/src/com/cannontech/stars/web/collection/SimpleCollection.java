package com.cannontech.stars.web.collection;

import java.util.List;

public interface SimpleCollection<E> extends Iterable<E> {

    public List<E> getList();
    
    public List<E> getList(int fromIndex, int toIndex);
    
    public int getCount();
    
}
