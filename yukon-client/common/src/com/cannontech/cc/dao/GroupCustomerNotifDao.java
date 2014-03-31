package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.GroupCustomerNotif;

public interface GroupCustomerNotifDao {
    public List<GroupCustomerNotif> getAllForGroup(Group group);

    public void saveNotifsForGroup(Group group, List<GroupCustomerNotif> required);
    public void deleteFor(Group object);
    public void save(GroupCustomerNotif object);
    public void delete(GroupCustomerNotif object);
}
