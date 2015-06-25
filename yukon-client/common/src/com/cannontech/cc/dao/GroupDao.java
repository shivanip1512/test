package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.Group;
import com.cannontech.core.dao.support.IdentifiableObjectProvider;

public interface GroupDao extends IdentifiableObjectProvider<Group>{
    
    List<Group> getGroupsForEnergyCompany(int energyCompanyId);
    
    @Override
    Group getForId(Integer id);
    
    List<Group> getForIds(Iterable<Integer> ids);
    
    void save(Group object);
    
    void delete(Group object);
}
