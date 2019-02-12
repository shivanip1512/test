package com.cannontech.dr.itron.dao;

import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;

public interface ItronDao {
    /**
     * @return Itron Device Id
     */
    int getGroup();
    
    /**
     * @return Itron Device Id
     */
    int getProgram();

    void addProgram(int itronId, PaoIdentifier paoId);
    
    void addGroup(int itronId, PaoIdentifier paoId);
    
    List<Integer> getAllGroupIds(PaoIdentifier paoId);

    List<Integer> getAllProgramIds(PaoIdentifier paoId);
}
