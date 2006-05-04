package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.Group;

public interface GroupDao extends StandardDaoOperations<Group> {
    public List<Group> getGroupsForEnergyCompany(int energyCompanyId);

}
