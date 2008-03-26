package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.common.opc.model.FdrInterfaceType;
import com.cannontech.common.opc.model.FdrTranslation;

public interface FdrTranslationDao {   
    
    public boolean add( FdrTranslation translation);
    
    public List<FdrTranslation> getByInterfaceType(FdrInterfaceType type);
    
    public FdrTranslation getByPointIdAndType(int pointId, FdrInterfaceType type);
}
