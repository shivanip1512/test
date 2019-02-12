package com.cannontech.dr.itron.dao.impl;

import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.dr.itron.dao.ItronDao;

public class ItronDaoImpl implements ItronDao {

    @Override
    public int getGroup() {
        return 0;
    }

    @Override
    public int getProgram() {
        return 0;
    }

    @Override
    public void addProgram(int itronId, PaoIdentifier paoId) {
    }

    @Override
    public void addGroup(int itronId, PaoIdentifier paoId) {
    }

    @Override
    public List<Integer> getAllGroupIds(PaoIdentifier paoId) {
        return null;
    }

    @Override
    public List<Integer> getAllProgramIds(PaoIdentifier paoId) {
        return null;
    }

}
