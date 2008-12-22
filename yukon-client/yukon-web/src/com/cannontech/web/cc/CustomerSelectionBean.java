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
import com.cannontech.cc.service.builder.VerifiedNotifCustomer;
import com.cannontech.common.exception.PointException;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.web.cc.methods.EventCreationBase;
import com.cannontech.web.cc.util.SelectableCustomer;
import com.cannontech.web.updater.point.PointDataRegistrationService;
import com.cannontech.web.util.JSFUtil;

public class CustomerSelectionBean {
    private EventCreationBase eventBean;
    private ProgramService programService;
    private GroupService groupService;
    private List<Group> selectedGroupList;
    private List<SelectableCustomer> customerList;
    private DataModel customerListModel;
    private PointDataRegistrationService registrationService;
    
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
            JSFUtil.addNullWarnMessage("At least one Group must be selected.");
            return null;
        }
        customerList = new ArrayList<SelectableCustomer>();
        List<VerifiedNotifCustomer> availableCustomerList = 
            eventBean.getVerifiedCustomerList(selectedGroupList);
        for (VerifiedNotifCustomer vCustomer : availableCustomerList) {
            customerList.add(new SelectableCustomer(vCustomer));
        }
        customerListModel = new ListDataModel(customerList);
        
        
        return "customerConfirmation";
    }
    
    public String doCustomerVerificationComplete() {
        if (getSelectedCustomers().isEmpty()) {
            JSFUtil.addNullWarnMessage("At least one Customer must be selected.");
            return null;
        }
        return eventBean.doAfterCustomerPage();
    }
    
    public String getLoadPointUpdaterStr() {
        SelectableCustomer sCustomer = (SelectableCustomer) customerListModel.getRowData();
        try {
        	int currentLoadPointId = eventBean.getStrategy().getCurrentLoadPoint(sCustomer.getCustomer()).getPointID();
    	    String format = Format.VALUE.toString();
        	return registrationService.getRawPointDataUpdaterSpan(currentLoadPointId, format, JSFUtil.getYukonUserContext());
        } catch (PointException e) {
            return "n/a";
        }
    }

    public String getContractFirmDemandUpdaterStr() {
        SelectableCustomer sCustomer = (SelectableCustomer) customerListModel.getRowData();
        try {
        	int fslPointId = eventBean.getStrategy().getContractFirmDemandPoint(sCustomer.getCustomer()).getPointID();
    	    String format = Format.VALUE.toString();
        	return registrationService.getRawPointDataUpdaterSpan(fslPointId, format, JSFUtil.getYukonUserContext());
        } catch (PointException e) {
            return "n/a";
        }
    }
    
    public String getConstraintStatus() {
    	SelectableCustomer sCustomer = (SelectableCustomer) customerListModel.getRowData();
        return eventBean.getStrategy().getConstraintStatus(sCustomer.getCustomer());
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

    public void setRegistrationService(
            PointDataRegistrationService registrationService) {
        this.registrationService = registrationService;
    }
}
