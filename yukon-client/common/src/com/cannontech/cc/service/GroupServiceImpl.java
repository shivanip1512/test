package com.cannontech.cc.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cc.dao.CustomerStubDao;
import com.cannontech.cc.dao.GroupCustomerNotifDao;
import com.cannontech.cc.dao.GroupDao;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.support.CustomerPointTypeHelper;

public class GroupServiceImpl implements GroupService {
    @Autowired private GroupDao groupDao;
    @Autowired private CustomerStubDao customerStubDao;
    @Autowired private GroupCustomerNotifDao groupCustomerNotifDao;
    @Autowired private CustomerPointTypeHelper pointTypeHelper;
    @Autowired private EnergyCompanyDao ecDao;

    @Override
    public List<Group> getAllGroups(LiteYukonUser user) {
        EnergyCompany energyCompany = ecDao.getEnergyCompany(user);
        List<Group> groupsForEnergyCompany = groupDao.getGroupsForEnergyCompany(energyCompany.getId());
        Collections.sort(groupsForEnergyCompany);
        return groupsForEnergyCompany;
    }
    
    public Set<String> getSatisfiedPointGroups(List<GroupCustomerNotif> notifList) {
        List<CICustomerStub> customerList = new ArrayList<CICustomerStub>(notifList.size());
        for (GroupCustomerNotif notif : notifList) {
            customerList.add(notif.getCustomer());
        }
        return pointTypeHelper.getSatisfiedPointGroups(customerList);
    }

    @Override
    public Group createNewGroup(LiteYukonUser user) {
        EnergyCompany energyCompany = ecDao.getEnergyCompany(user);
        Group newGroup = new Group();
        newGroup.setEnergyCompanyId(energyCompany.getId());
        return newGroup;
    }
    
    @Override
    public List<Group> getGroupsById(Iterable<Integer> groupIds) {
        return groupDao.getForIds(groupIds);
    }
    
    @Override
    @Transactional
    public Group getGroup(Integer groupId) {
        return groupDao.getForId(groupId);
    }

    @Override
    @Transactional
    public void deleteGroup(Group group) {
        groupDao.delete(group);
    }

    @Override
    @Transactional
    public void saveGroup(Group group, List<GroupCustomerNotif> required) {
        groupDao.save(group);
        groupCustomerNotifDao.saveNotifsForGroup(group, required);
    }

    @Override
    @Transactional
    public List<GroupCustomerNotif> getUnassignedCustomers(Group group, boolean newGroup) {
        List<CICustomerStub> customers;
        if (newGroup) {
            customers = customerStubDao.getCustomersForEC(group.getEnergyCompanyId());
        } else {
            customers = customerStubDao.getUnassignedCustomers(group);
        }
        ArrayList<GroupCustomerNotif> customerList = new ArrayList<GroupCustomerNotif>(customers.size());
        for (CICustomerStub customer : customers) {
            GroupCustomerNotif notif = new GroupCustomerNotif();
            notif.setCustomer(customer);
            notif.setGroup(group); // so it'll be ready if we want to save it
            customerList.add(notif);
        }
        return customerList;
    }
    
    @Override
    @Transactional
    public List<GroupCustomerNotif> getAssignedCustomers(Group group) {
        return groupCustomerNotifDao.getAllForGroup(group);
    }

    public void setPointTypeHelper(CustomerPointTypeHelper pointTypeHelper) {
        this.pointTypeHelper = pointTypeHelper;
    }

}

