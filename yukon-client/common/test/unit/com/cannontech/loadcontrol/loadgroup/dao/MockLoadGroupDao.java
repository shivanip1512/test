package com.cannontech.loadcontrol.loadgroup.dao;

import java.util.Collection;
import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.MethodNotImplementedException;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.google.common.collect.SetMultimap;

public class MockLoadGroupDao implements LoadGroupDao {

    @Override
    public LoadGroup getById(int loadGroupId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<LoadGroup> getByIds(Iterable<Integer> loadGroupIds) {
        throw new MethodNotImplementedException();
    }

    @Override
    public LoadGroup getByLoadGroupName(String loadGroupName) {
        throw new MethodNotImplementedException();
    }

    @Override
    public boolean isLoadGroupInUse(int loadGroupId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<PaoIdentifier> getParentMacroGroups(PaoIdentifier pao) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<LoadGroup> getByStarsProgramId(int programId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public SetMultimap<PaoIdentifier, PaoIdentifier> getMacroGroupToGroupMappings(Collection<PaoIdentifier> groups) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<LoadGroup> getByProgramId(int programId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public int getProgramIdByGroupId(int lmGroupId) {
        throw new MethodNotImplementedException();
    }

}
