package com.cannontech.cc.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cc.dao.CustomerDao;
import com.cannontech.cc.dao.GroupCustomerNotifDao;
import com.cannontech.cc.dao.GroupDao;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.database.cache.functions.EnergyCompanyFuncs;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.support.CustomerPointTypeHelper;

public class GroupService {
    private GroupDao groupDao;
    private CustomerDao customerDao;
    private GroupCustomerNotifDao groupCustomerNotifDao;
    private CustomerPointTypeHelper pointTypeHelper;

    public GroupCustomerNotifDao getGroupCustomerNotifDao() {
        return groupCustomerNotifDao;
    }

    public void setGroupCustomerNotifDao(GroupCustomerNotifDao groupCustomerNotifDao) {
        this.groupCustomerNotifDao = groupCustomerNotifDao;
    }

    public List<Group> getAllGroups(LiteYukonUser user) {
        LiteEnergyCompany energyCompany = EnergyCompanyFuncs.getEnergyCompany(user);
        List<Group> groupsForEnergyCompany = groupDao.getGroupsForEnergyCompany(energyCompany.getEnergyCompanyID());
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

    public GroupDao getGroupDao() {
        return groupDao;
    }

    public void setGroupDao(GroupDao groupDao) {
        this.groupDao = groupDao;
    }

    public CustomerDao getCustomerDao() {
        return customerDao;
    }

    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public Group createNewGroup(LiteYukonUser yukonUser) {
        LiteEnergyCompany energyCompany = EnergyCompanyFuncs.getEnergyCompany(yukonUser);
        Group newGroup = new Group();
        
        newGroup.setEnergyCompanyId(energyCompany.getEnergyCompanyID());
        return newGroup;
    }

    @Transactional
    public Group getGroup(Integer groupId) {
        return groupDao.getForId(groupId);
    }

    @Transactional
    public void deleteGroup(Group group) {
        groupDao.delete(group);
    }

    @Transactional
    public void saveGroup(Group group, List<GroupCustomerNotif> required) {
        groupDao.save(group);
        groupCustomerNotifDao.saveNotifsForGroup(group, required);
    }

    @Transactional
    public List<GroupCustomerNotif> getUnassignedCustomers(Group group, boolean newGroup) {
        List<CICustomerStub> customers;
        if (newGroup) {
            customers = customerDao.getCustomersForEC(group.getEnergyCompanyId());
        } else {
            customers = customerDao.getUnassignedCustomers(group);
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
    
    @Transactional
    public List<GroupCustomerNotif> getAssignedCustomers(Group group) {
        return groupCustomerNotifDao.getAllForGroup(group);
    }

    @Transactional
    public void addGroupCustomerNotif(Group group, CICustomerStub customer) {
        GroupCustomerNotif gcn = new GroupCustomerNotif();
        gcn.setCustomer(customer);
        gcn.setGroup(group);
        groupCustomerNotifDao.save(gcn);
    }

    @Transactional
    public void deleteGroupCustomerNotif(GroupCustomerNotif rowData) {
        groupCustomerNotifDao.delete(rowData);
    }

    public void setPointTypeHelper(CustomerPointTypeHelper pointTypeHelper) {
        this.pointTypeHelper = pointTypeHelper;
    }
}

