package com.cannontech.stars.dr.account.dao;

import org.springframework.dao.DataAccessException;

import com.cannontech.stars.dr.account.model.AccountProgram;
import com.cannontech.stars.dr.account.model.ProgramLoadGroup;

public interface ApplianceAndProgramDao {

    public ProgramLoadGroup getProgramByLMGroupId(final int lmGroupId) throws DataAccessException;
    
}
