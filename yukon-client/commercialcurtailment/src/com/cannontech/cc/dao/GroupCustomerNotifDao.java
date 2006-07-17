package com.cannontech.cc.dao;

import java.util.List;

import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.core.dao.support.StandardDaoOperations;

public interface GroupCustomerNotifDao  extends StandardDaoOperations<GroupCustomerNotif> {
    public List<GroupCustomerNotif> getAllForGroup(Group group);

    public void saveNotifsForGroup(Group group, List<GroupCustomerNotif> required);

}
