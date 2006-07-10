package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.Group;
import com.cannontech.database.cache.functions.StandardDaoOperations;

public interface GroupDao extends StandardDaoOperations<Group> {
    public List<Group> getGroupsForEnergyCompany(int energyCompanyId);

}
