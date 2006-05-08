package com.cannontech.web.cc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import com.cannontech.cc.model.AvailableProgramGroup;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.cc.service.GroupService;
import com.cannontech.cc.service.ProgramService;
import com.cannontech.cc.service.builder.VerifiedCustomer;
import com.cannontech.cc.service.exception.NoPointException;
import com.cannontech.web.cc.methods.EventCreationBase;
import com.cannontech.web.cc.util.SelectableCustomer;
import com.cannontech.web.util.JSFUtil;

public class CustomerSelectionBean {
    private EventCreationBase eventBean;
    private ProgramService programService;
    private GroupService groupService;
    private List<Group> selectedGroupList;
    private List<SelectableCustomer> customerList;
    private DataModel customerListModel;
    
    public List<Group> getSelectedGroupList() {
        return selectedGroupList;
    }
    
    public void getSelectedGroupList(List<Group> selectedGroupList) {
        this.selectedGroupList = selectedGroupList;
    }
    
    public List<SelectItem> getAvailableGroupList() {
        List<AvailableProgramGroup> groups = 
            programService.getAvailableProgramGroups(getEventBean().getProgram());
        List<SelectItem> result = new ArrayList<SelectItem>();
        for (AvailableProgramGroup group : groups) {
            SelectItem item = new SelectItem(group.getGroup(), group.getGroup().getName());
            result.add(item);
        }
        return result;
    }
    
    public EventCreationBase getEventBean() {
        return eventBean;
    }

    public void setEventBean(EventCreationBase curtailmentBase) {
        this.eventBean = curtailmentBase;
        selectedGroupList = Collections.emptyList();
    }
    
    public String doGroupSelectionComplete() {
        if (selectedGroupList.isEmpty()) {
            JSFUtil.addNullMessage("At least one Group must be selected.");
            return null;
        }
        customerList = new ArrayList<SelectableCustomer>();
        List<VerifiedCustomer> availableCustomerList = 
            eventBean.getVerifiedCustomerList(selectedGroupList);
        for (VerifiedCustomer vCustomer : availableCustomerList) {
            customerList.add(new SelectableCustomer(vCustomer));
        }
        customerListModel = new ListDataModel(customerList);
        
        
        return "customerConfirmation";
    }
    
    public String doCustomerVerificationComplete() {
        if (getSelectedCustomers().isEmpty()) {
            JSFUtil.addNullMessage("At least one Customer must be selected.");
        }
        return eventBean.doAfterCustomerPage();
    }
    
    public String getInterruptibleLoad() {
        SelectableCustomer sCustomer = (SelectableCustomer) customerListModel.getRowData();
        try {
            return eventBean.getStrategy().getInterruptibleLoad(sCustomer.getCustomer()).toPlainString();
        } catch (NoPointException e) {
            return "n/a";
        }
    }
    
    public String cancel() {
        return eventBean.cancel();
    }

    public GroupService getGroupService() {
        return groupService;
    }

    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    public ProgramService getProgramService() {
        return programService;
    }

    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }

    public List<SelectableCustomer> getCustomerList() {
        return customerList;
    }

    public void setSelectedGroupList(List<Group> selectedGroupList) {
        this.selectedGroupList = selectedGroupList;
    }
    
    public List<GroupCustomerNotif> getSelectedCustomers() {
        List<GroupCustomerNotif> result = new LinkedList<GroupCustomerNotif>();
        for (SelectableCustomer selCustomer : customerList) {
            if (selCustomer.isSelected()) {
                GroupCustomerNotif customer = selCustomer.getCustomerNotif();
                result.add(customer);
            }
        }
        return result;
    }

    public DataModel getCustomerListModel() {
        return customerListModel;
    }

    public void setCustomerListModel(DataModel customerListModel) {
        this.customerListModel = customerListModel;
    }

}
