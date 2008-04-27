package com.cannontech.stars.dr.account.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.cannontech.stars.dr.account.model.ProgramLoadGroup;

public interface ApplianceAndProgramDao {

    public List<ProgramLoadGroup> getProgramsByLMGroupId(final int lmGroupId) throws DataAccessException;
    
    public List<ProgramLoadGroup> getAllProgramsForAnEC(final int energyCompanyId) throws DataAccessException;
}
