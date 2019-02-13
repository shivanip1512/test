package com.cannontech.dr.itron.dao.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.dr.itron.dao.ItronDao;

public class ItronDaoImpl implements ItronDao {

    @Override
    public int getGroup(int groupPaoId) throws NotFoundException {
        return 0;
    }

    @Override
    public int getProgram(int programPaoId) throws NotFoundException{
        return 0;
    }

    @Override
    public void addProgram(long itronId, int pogramPaoId) {
    }

    @Override
    public void addGroup(long itronId, int groupPaoId) {
    }

    @Override
    public Map<Integer, Long> getItronProgramIds(Collection<Integer> programPaoIds) {
        Map<Integer, Long> list = new HashMap<>();
        list.put(1,1L);
        list.put(2,2L);
        list.put(3,3L);
        return list;
    }
    
    @Override
    public Map<Integer, Long> getItronGroupIds(Collection<Integer> groupPaoIds) {
        Map<Integer, Long> list = new HashMap<>();
        list.put(1,1L);
        list.put(2,2L);
        list.put(3,3L);
        return list;
    }
}
