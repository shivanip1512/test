package com.cannontech.core.dao;

import com.cannontech.core.dynamic.RphServiceTag;

public interface RPHServiceTagDao {
    
    public void insert(RphServiceTag rphServiceTag);
    public void save(RphServiceTag rphServiceTag);
    public void update(RphServiceTag rphServiceTag);
    
    public RphServiceTag getRphServiceTag(String serviceName, String serviceNameType);

}