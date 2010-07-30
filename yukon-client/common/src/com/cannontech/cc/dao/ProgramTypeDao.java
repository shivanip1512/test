package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.ProgramType;
import com.cannontech.core.dao.support.IdentifiableObjectProvider;

public interface ProgramTypeDao extends IdentifiableObjectProvider<ProgramType> {
    public List<ProgramType> getAllProgramTypes(Integer energyCompanyId);
}
