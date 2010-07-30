package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.Group;
import com.cannontech.core.dao.support.IdentifiableObjectProvider;

public interface GroupDao extends IdentifiableObjectProvider<Group>{
    public List<Group> getGroupsForEnergyCompany(int energyCompanyId);
    public Group getForId(Integer id);
    public void save(Group object);
    public void delete(Group object);
}
