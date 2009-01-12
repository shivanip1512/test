package com.cannontech.web.cc.methods;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.service.NotificationService;
import com.cannontech.cc.service.NotificationStrategy;
import com.cannontech.cc.service.ProgramService;
import com.cannontech.cc.service.StrategyFactory;
import com.cannontech.cc.service.builder.CurtailmentChangeBuilder;
import com.cannontech.cc.service.builder.CurtailmentRemoveCustomerBuilder;
import com.cannontech.cc.service.builder.VerifiedPlainCustomer;
import com.cannontech.common.exception.PointException;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.cc.util.RemoveableCustomer;
import com.cannontech.web.updater.point.PointDataRegistrationService;
import com.cannontech.web.util.JSFUtil;


public class DetailNotificationBean implements BaseDetailBean {
    private DetailNotificationHelperBean helper;
    private ProgramService programService;
    private NotificationService notificationService;
    private NotificationStrategy strategy;
    private StrategyFactory strategyFactory;
    private CurtailmentEvent event;
    private LiteYukonUser yukonUser;
    private CurtailmentChangeBuilder changeBuilder;
    private CurtailmentRemoveCustomerBuilder removeBuilder;
    private PointDataRegistrationService registrationService;
    private List<RemoveableCustomer> removeCustomerList;
    private DataModel removeCustomerListModel;

    public void setRegistrationService(PointDataRegistrationService registrationService) {
        this.registrationService = registrationService;
    }
    
    public String showDetail(BaseEvent event) {
        setEvent((CurtailmentEvent) event);
        return "notifDetail";
    }

    public ProgramService getProgramService() {
        return programService;
    }

    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }
    
    public Boolean getShowDeleteButton() {
        return getStrategy().canEventBeDeleted(getEvent(), getYukonUser());
    }
    
    public Boolean getShowCancelButton() {
        return getStrategy().canEventBeCancelled(getEvent(), getYukonUser());
    }
    
    public Boolean getShowAdjustButton() {
        return getStrategy().canEventBeAdjusted(getEvent(), getYukonUser());
    }

    public Boolean getShowRemoveButton() {
        return getStrategy().canCustomersBeRemovedFromEvent(getEvent(), getYukonUser());
    }

    public String cancelEvent() {
        getStrategy().cancelEvent(event,getYukonUser());
        
        return null;
    }

    public String prepareAdjustEvent() {
        changeBuilder = getStrategy().createChangeBuilder(getEvent());
        
        return "prepareAdjustEvent";
    }
    
    public String adjustEvent() {
        getStrategy().adjustEvent(changeBuilder, getYukonUser());
        
        return "notifDetail";
    }
    
    public String cancelAdjust() {
        return "notifDetail";
    }

    public String prepareSplitEvent() {
        removeBuilder = getStrategy().createRemoveBuilder(getEvent());
        
        removeCustomerList = new ArrayList<RemoveableCustomer>();
        List<VerifiedPlainCustomer> availableCustomerList = removeBuilder.getRemoveCustomerList();
        for (VerifiedPlainCustomer vCustomer : availableCustomerList) {
            removeCustomerList.add(new RemoveableCustomer(vCustomer));
        }
        removeCustomerListModel = new ListDataModel(removeCustomerList);
        
        return "prepareSplitEvent";
    }

    public String splitEvent() {
        getStrategy().splitEvent(removeBuilder, getYukonUser());
        
        return "notifDetail";
    }
    
    public String cancelRemove() {
        return "notifDetail";
    }

    public String deleteEvent() {
        getStrategy().deleteEvent(event,getYukonUser());
        return "programSelect";
    }
    
    public String refresh() {
        updateModels();
        return null;
    }

    public CurtailmentEvent getEvent() {
        return event;
    }

    public NotificationStrategy getStrategy() {
        return strategy;
    }
    
    public void setEvent(CurtailmentEvent event) {
        this.event = event;
        strategy = (NotificationStrategy) strategyFactory.getStrategy(event.getProgram());
        getHelper().setEvent(event);
        updateModels();
    }

    private void updateModels() {
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public StrategyFactory getStrategyFactory() {
        return strategyFactory;
    }

    public void setStrategyFactory(StrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    public LiteYukonUser getYukonUser() {
        return yukonUser;
    }

    public void setYukonUser(LiteYukonUser yukonUser) {
        this.yukonUser = yukonUser;
    }

    public DetailNotificationHelperBean getHelper() {
        return helper;
    }

    public void setHelper(DetailNotificationHelperBean helper) {
        this.helper = helper;
    }

    public CurtailmentRemoveCustomerBuilder getRemoveBuilder() {
        return removeBuilder;
    }
    
    public CurtailmentChangeBuilder getChangeBuilder() {
        return changeBuilder;
    }
    
    public String getLoadPointUpdaterStr() {
        RemoveableCustomer rCustomer = (RemoveableCustomer) removeCustomerListModel.getRowData();
        try {
            int pointId = strategy.getCurrentLoadPoint(rCustomer.getCustomer()).getPointID();
    	    String format = Format.VALUE.toString();
            return registrationService.getRawPointDataUpdaterSpan(pointId, format, JSFUtil.getYukonUserContext());
        } catch (PointException e) {
            return "n/a";
        }
    }

    public String getContractFirmDemandUpdaterStr() {
    	RemoveableCustomer rCustomer = (RemoveableCustomer) removeCustomerListModel.getRowData();
        try {
        	int fslPointId = strategy.getContractFirmDemandPoint(rCustomer.getCustomer()).getPointID();
    	    String format = Format.VALUE.toString();
        	return registrationService.getRawPointDataUpdaterSpan(fslPointId, format, JSFUtil.getYukonUserContext());
        } catch (PointException e) {
            return "n/a";
        }
    }
    
    public String getConstraintStatus() {
    	RemoveableCustomer rCustomer = (RemoveableCustomer) removeCustomerListModel.getRowData();
        return strategy.getConstraintStatus(rCustomer.getCustomer());
    }
    
    public String doCustomerRemoveComplete() {
        for (RemoveableCustomer remCustomer : removeCustomerList) {
            if (!remCustomer.isSelected()) {
                VerifiedPlainCustomer customer = remCustomer.getCustomerDelegate();
                removeBuilder.getRemoveCustomerList().remove(customer);
            }
        }
        if (removeBuilder.getRemoveCustomerList().isEmpty()) {
            JSFUtil.addNullWarnMessage("At least one Customer must be selected.");
            return null;
        }
        return splitEvent();
    }
    
    public List<RemoveableCustomer> getRemoveCustomerList() {
        return removeCustomerList;
    }
    
    public DataModel getRemoveCustomerListModel() {
        return removeCustomerListModel;
    }
}
