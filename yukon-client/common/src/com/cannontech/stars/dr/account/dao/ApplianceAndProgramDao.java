package com.cannontech.stars.dr.account.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.cannontech.stars.dr.account.model.ProgramLoadGroup;

public interface ApplianceAndProgramDao {

    public List<ProgramLoadGroup> getProgramsByLMGroupId(final int lmGroupId) throws DataAccessException;
    
    public List<ProgramLoadGroup> getAllProgramsForAnEC(final int energyCompanyId) throws DataAccessException;
    
    /**
     * Returns a list of programs assigned to the given energy companies appliance categories
     * and the appliance categories from the parent energy company. This is assuming they are
     * inherited.  If they are not inherited, use getAllProgramsForAnEC.
     * @param energyCompanyId
     * @return
     * @throws DataAccessException
     */
    public List<ProgramLoadGroup> getAllProgramsForAnECAndParentEC(final int energyCompanyId) throws DataAccessException;
}
