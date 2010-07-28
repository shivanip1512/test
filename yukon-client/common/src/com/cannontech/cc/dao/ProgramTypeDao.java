package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.ProgramType;
import com.cannontech.core.dao.support.IdAccessible;

public interface ProgramTypeDao extends IdAccessible<ProgramType> {
    public List<ProgramType> getAllProgramTypes(Integer energyCompanyId);
}
