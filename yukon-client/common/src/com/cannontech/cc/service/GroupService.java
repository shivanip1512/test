package com.cannontech.cc.service;

import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface GroupService {

    public List<Group> getAllGroups(LiteYukonUser user);

    public Group createNewGroup(LiteYukonUser yukonUser);

    @Transactional
    public Group getGroup(Integer groupId);

    @Transactional
    public void deleteGroup(Group group);

    @Transactional
    public void saveGroup(Group group, List<GroupCustomerNotif> required);

    @Transactional
    public List<GroupCustomerNotif> getUnassignedCustomers(Group group, boolean newGroup);

    @Transactional
    public List<GroupCustomerNotif> getAssignedCustomers(Group group);

}