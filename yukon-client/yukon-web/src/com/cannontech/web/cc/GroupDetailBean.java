package com.cannontech.web.cc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.cc.service.GroupService;
import com.cannontech.database.data.lite.LiteYukonUser;

public class GroupDetailBean {
    private final class CustomerListComparator implements Comparator<GroupCustomerNotif> {
        public int compare(GroupCustomerNotif o1, GroupCustomerNotif o2) {
            return o1.getCustomer().compareTo(o2.getCustomer());
        }
    }

    private LiteYukonUser yukonUser;
    private GroupService groupService;
    private Group group = null;
    private DataModel groupCustomerModel = new ListDataModel();
    private DataModel customerModel = new ListDataModel();
    private List<GroupCustomerNotif> groupCustomerList;
    private List<GroupCustomerNotif> customerList;
    
    public Group getGroup() {
        return group;
    }

    public String editGroup() {
        ExternalContext externalContext = 
            FacesContext.getCurrentInstance().getExternalContext();
        String groupIdStr = 
            (String) externalContext.getRequestParameterMap().get("groupId");
        int groupId = Integer.parseInt(groupIdStr);
        group = groupService.getGroup(groupId);
        
        groupCustomerList = groupService.getAssignedCustomers(group);
        Collections.sort(groupCustomerList, new CustomerListComparator());
        groupCustomerModel.setWrappedData(groupCustomerList);
        
        customerList = groupService.getUnassignedCustomers(getGroup(), false);
        Collections.sort(customerList, new CustomerListComparator());
        customerModel.setWrappedData(customerList);
        
        return "groupDetail";

    }
    
    public String newGroup() {
        group = groupService.createNewGroup(getYukonUser());
        
        groupCustomerList = new ArrayList<GroupCustomerNotif>();
        groupCustomerModel.setWrappedData(groupCustomerList);
        
        customerList = groupService.getUnassignedCustomers(getGroup(), true);
        Collections.sort(customerList, new CustomerListComparator());
        customerModel.setWrappedData(customerList);

        return "groupDetail";
    }

    public void setGroup(Group group) {
        this.group = group;
    }
    
    public DataModel getCustomerNotifsModel() {
        return groupCustomerModel;
    }
    
    public GroupService getGroupService() {
        return groupService;
    }

    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    public LiteYukonUser getYukonUser() {
        return yukonUser;
    }

    public void setYukonUser(LiteYukonUser yukonUser) {
        this.yukonUser = yukonUser;
    }

    public void doSave() {
        groupService.saveGroup(getGroup(), groupCustomerList);
    }
    
    public String save() {
        doSave();
        return "groupList";
    }
    
    public String apply() {
        doSave();
        return null;
    }
    
    public String cancel() {
        return "groupList";
    }
    
    public String delete() {
        groupService.deleteGroup(getGroup());
        return "groupList";
    }
    
    public void deleteNotif(ActionEvent event) {
        GroupCustomerNotif rowData = (GroupCustomerNotif) groupCustomerModel.getRowData();
        groupCustomerList.remove(rowData);
        customerList.add(rowData);
        Collections.sort(customerList, new CustomerListComparator());
    }

    public void addNotif(ActionEvent event) {
        GroupCustomerNotif rowData = (GroupCustomerNotif) customerModel.getRowData();
        customerList.remove(rowData);
        groupCustomerList.add(rowData);
        Collections.sort(groupCustomerList, new CustomerListComparator());
    }

    public DataModel getCustomerModel() {
        return customerModel;
    }

    public void setCustomerModel(DataModel customerModel) {
        this.customerModel = customerModel;
    }

}
