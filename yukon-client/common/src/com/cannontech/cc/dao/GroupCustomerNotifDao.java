package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.GroupCustomerNotif;

public interface GroupCustomerNotifDao {
    
    List<GroupCustomerNotif> getAllForGroup(Group group);

    void saveNotifsForGroup(Group group, List<GroupCustomerNotif> required);
    
    void deleteFor(Group object);
    
    void save(GroupCustomerNotif object);
    
    void delete(GroupCustomerNotif object);

    List<GroupCustomerNotif> getByIds(Iterable<Integer> ids);
}
