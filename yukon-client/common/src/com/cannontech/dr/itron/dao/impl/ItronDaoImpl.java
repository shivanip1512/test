package com.cannontech.dr.itron.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public List<Long> getItronProgramIds(Collection<Integer> programPaoIds) {
        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        list.add(3L);
        return list;
    }
}
