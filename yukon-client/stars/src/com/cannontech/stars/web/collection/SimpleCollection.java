package com.cannontech.stars.web.collection;

import java.util.List;

import com.cannontech.core.dao.PersistenceException;

public interface SimpleCollection<E> extends Iterable<E> {

    public List<E> getList() throws PersistenceException;
    
    public List<E> getList(int fromIndex, int toIndex) throws PersistenceException;
    
    public int getCount() throws PersistenceException;
    
}
