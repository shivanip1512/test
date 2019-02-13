package com.cannontech.dr.itron.dao;

import java.util.Collection;
import java.util.Map;

import com.cannontech.core.dao.NotFoundException;

public interface ItronDao {
    
    int getGroup(int groupPaoId) throws NotFoundException;
    
    int getProgram(int programPaoId) throws NotFoundException;
    
    void addGroup(long itronId, int programPaoId);
    
    void addProgram(long itronId, int groupPaoId);

    Map<Integer, Long> getItronProgramIds(Collection<Integer> programPaoIds);

    Map<Integer, Long> getItronGroupIds(Collection<Integer> groupPaoIds);
}
