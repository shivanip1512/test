package com.cannontech.core.dao;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrTranslation;

public interface FdrTranslationDao {

    public boolean add(FdrTranslation translation);

    public List<FdrTranslation> getByInterfaceType(FdrInterfaceType type);

    public FdrTranslation getByPointIdAndType(int pointId, FdrInterfaceType type);
    
    public List<FdrTranslation> getByPaobjectIdAndType(int paoId, FdrInterfaceType type);
    
	public List<FdrTranslation> getByInterfaceTypeAndTranslation(FdrInterfaceType type, String translation);

}
