package com.cannontech.web.cc.methods;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.VariableResolver;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.cc.model.EconomicEventParticipantSelection;
import com.cannontech.cc.model.EconomicEventParticipantSelectionWindow;
import com.cannontech.cc.model.EconomicEventPricing;
import com.cannontech.cc.model.EconomicEventPricingWindow;
import com.cannontech.cc.service.EconomicService;
import com.cannontech.cc.service.EconomicStrategy;
import com.cannontech.cc.service.ProgramService;
import com.cannontech.cc.service.StrategyFactory;
import com.cannontech.cc.service.NotificationStatus;
import com.cannontech.cc.service.exception.EventModificationException;
import com.cannontech.web.cc.CommercialCurtailmentBean;
import com.cannontech.web.util.JSFUtil;


public class DetailEconomicBean implements BaseDetailBean {
    //private CreateEconomicBean createEconomicBean;
    private ProgramService programService;
    private EconomicStrategy strategy;
    private EconomicService economicService;
    private EconomicEvent event;
    private EconomicEventPricing currentRevision;
    private DataModel participantDataModel;
    private DataModel windowDataModel;
    private StrategyFactory strategyFactory;
    private CommercialCurtailmentBean commercialCurtailment;
    private EconomicEventPricing nextRevision;
    private ListDataModel otherRevisionsModel;
    private List<EconomicEventParticipant> participantList;
    
    public String showDetail(BaseEvent event) {
        setEvent((EconomicEvent) event);
        return "econDetail";
    }
    
    public String showInitialEvent() {
        setEvent(getEvent().getInitialEvent());
        return "econDetail";
    }
    
    public void updateModels() {
        participantList = economicService.getParticipants(event);
        participantDataModel = new ListDataModel(participantList);
        windowDataModel = null;
        otherRevisionsModel = null;
    }
    
    public void setCommercialCurtailment(CommercialCurtailmentBean commercialCurtailment) {
        this.commercialCurtailment = commercialCurtailment;
    }

    public DataModel getParticipantModel() {
        return participantDataModel;
    }
    
    public DataModel getWindowModel() {
        if (windowDataModel == null) {
            Collection<EconomicEventPricingWindow> values = event.getInitialRevision().getWindows().values();
            List<EconomicEventPricingWindow> windows = new ArrayList<EconomicEventPricingWindow>(values);
            Collections.sort(windows);
            windowDataModel = new ListDataModel(windows);
        }
        return windowDataModel;
    }
    
    public BigDecimal getColumnPrice() {
        DataModel columnModel = getWindowModel();
        if (columnModel.isRowAvailable()) { // read: isColumnAvailable()
            EconomicEventPricingWindow pricingWindow = 
                (EconomicEventPricingWindow) columnModel.getRowData(); // read: getColumnData()
            EconomicEventPricingWindow window = 
                economicService.getFallThroughWindow(getCurrentRevision(), 
                                                     pricingWindow.getOffset());
            return window.getEnergyPrice();
        }
        return null;
    }
    
    public BigDecimal getColumnTotal() {
        DataModel columnModel = getWindowModel();
        if (columnModel.isRowAvailable()) { // read: isColumnAvailable()
            EconomicEventPricingWindow pricingWindow = 
                (EconomicEventPricingWindow) columnModel.getRowData(); // read: getColumnData()
            long total = 0;
            for (EconomicEventParticipant participant : participantList) {
                EconomicEventParticipantSelection selection = participant.getSelection(getCurrentRevision());
                EconomicEventParticipantSelectionWindow selectionWindow = 
                    economicService.getFallThroughWindowSelection(selection, pricingWindow.getOffset());
                total += selectionWindow.getEnergyToBuy().longValue();
            }
            return BigDecimal.valueOf(total);
        }
        return null;
    }
    
    public BigDecimal getColumnValue() {
        DataModel rowModel = getParticipantModel();
        if (rowModel.isRowAvailable()) {
            EconomicEventParticipant row = 
                (EconomicEventParticipant) rowModel.getRowData();
            DataModel columnModel = getWindowModel();
            if (columnModel.isRowAvailable()) { // read: isColumnAvailable()
                EconomicEventPricingWindow pricingWindow = 
                    (EconomicEventPricingWindow) columnModel.getRowData(); // read: getColumnData()
                Integer column = pricingWindow.getOffset();
                EconomicEventParticipantSelectionWindow selection = 
                    economicService.getCustomerSelectionWindow(getCurrentRevision(),row,column);
                return selection.getEnergyToBuy();
            }
        }
        return null;
    }

    public ListDataModel getOtherRevisionsModel() {
        if (otherRevisionsModel == null) {
            ArrayList<EconomicEventPricing> others = 
                new ArrayList<EconomicEventPricing>(event.getRevisions().values());
            others.remove(getCurrentRevision());
            otherRevisionsModel = new ListDataModel(others);
        }
        return otherRevisionsModel;
    }
    
    public String getCustomerAckForRow() {
        DataModel participantModel = getParticipantModel();
        EconomicEventParticipant rowData = (EconomicEventParticipant) participantModel.getRowData();
        EconomicEventParticipantSelection selection = rowData.getSelection(getCurrentRevision());
        switch (selection.getState()) {
        case DEFAULT:
            return "D";
        case MANUAL:
            return "A";
        default:
            return "-";
        }
    }
    
    public String getCustomerNotifForRow() {
        DataModel participantModel = getParticipantModel();
        EconomicEventParticipant participant = (EconomicEventParticipant) participantModel.getRowData();
        NotificationStatus status = strategy.getNotificationSuccessStatus(participant);
        switch (status) {
        case MIXED:
            return "/WebConfig/yukon/Icons/warning.gif";
        case NO_FAILURES:
            return "/WebConfig/yukon/Icons/accept.png";
        case NO_SUCCESS:
            return "/WebConfig/yukon/Icons/error.gif";
        case PENDING:
            return "/WebConfig/yukon/Icons/time.gif";
        }
        return "/WebConfig/yukon/Icons/information.gif";
    }
    
    public String switchRevision() {
        EconomicEventPricing revision = (EconomicEventPricing) otherRevisionsModel.getRowData();
        setCurrentRevision(revision);
        return null;
    }
    
    public ProgramService getProgramService() {
        return programService;
    }

    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }
    
    
    public Boolean getShowDeleteButton() {
        return getStrategy().canEventBeDeleted(getEvent(), commercialCurtailment.getYukonUser());
    }
    
    public Boolean getShowCancelButton() {
        return getStrategy().canEventBeCancelled(getEvent(), commercialCurtailment.getYukonUser());
    }
    
    public Boolean getShowExtendButton() {
        return getStrategy().canEventBeExtended(getEvent(), commercialCurtailment.getYukonUser());
    }
    
    public Boolean getShowReviseButton() {
        return getStrategy().canEventBeRevised(getEvent(), commercialCurtailment.getYukonUser());
    }
    
    public Boolean getShowSuppressButton() {
        return getStrategy().canEventBeSuppressed(getEvent(), commercialCurtailment.getYukonUser());
    }
    
    public String cancelEvent() {
        getStrategy().cancelEvent(getEvent(),commercialCurtailment.getYukonUser());
        return null;
    }

    public String deleteEvent() {
        try {
            getStrategy().deleteEvent(event,commercialCurtailment.getYukonUser());
        } catch (EventModificationException e) {
            JSFUtil.handleException("Unable to delete Event", e);
            return null;
        }
        return "programSelect";
    }
    
    public String reviseEvent() {
        try {
            nextRevision = 
                getStrategy().createEventRevision(event, commercialCurtailment.getYukonUser());
        } catch (EventModificationException e) {
            JSFUtil.handleException("The event can not be revised.", e);
            return null;
        }
        updateModels();
        return "reviseEconomicEvent";
    }
    
    public String cancelRevision() {
        return "econDetail";
    }
    
    public String extendEvent() {
        getCreateEconomicBean().setStrategy(getStrategy());
        return getCreateEconomicBean().initExtension(getEvent());
        
    }
    
    public String suppressEvent() {
        strategy.suppressEvent(getEvent(), commercialCurtailment.getYukonUser());
        return null;
    }
    
    public String refresh() {
        updateModels();
        return null;
    }
    
    public String doCreateRevision() {
        strategy.saveRevision(nextRevision);
        return "econDetail";
    }
    
    public List<EconomicEventPricingWindow> getNextRevisionPrices() {
        ArrayList<EconomicEventPricingWindow> list = 
            new ArrayList<EconomicEventPricingWindow>(nextRevision.getWindows().values());
        Collections.sort(list);
        return list;
    }

    public EconomicEvent getEvent() {
        return event;
    }

    public EconomicStrategy getStrategy() {
        return strategy;
    }
    
    public void setEvent(EconomicEvent event) {
        this.event = event;
        currentRevision = event.getLatestRevision();
        strategy = (EconomicStrategy) strategyFactory.getStrategy(event.getProgram());
        updateModels();
    }

    public void setEconomicService(EconomicService economicService) {
        this.economicService = economicService;
    }

    public void setCurrentRevision(EconomicEventPricing currentRevision) {
        this.currentRevision = currentRevision;
        updateModels();
    }

    public void setParticipantDataModel(DataModel participantDataModel) {
        this.participantDataModel = participantDataModel;
    }

    public void setStrategy(EconomicStrategy strategy) {
        this.strategy = strategy;
    }

    public void setWindowDataModel(DataModel windowDataModel) {
        this.windowDataModel = windowDataModel;
    }

    public EconomicEventPricing getCurrentRevision() {
        return currentRevision;
    }

    public StrategyFactory getStrategyFactory() {
        return strategyFactory;
    }

    public void setStrategyFactory(StrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    public EconomicEventPricing getNextRevision() {
        return nextRevision;
    }

    public CreateEconomicBean getCreateEconomicBean() {
        // this kind of sucks, but there is a circular refernce that can't be expressed
        // in the xml config file alone
        FacesContext currentInstance = FacesContext.getCurrentInstance();
        VariableResolver variableResolver = currentInstance.getApplication().getVariableResolver();
        Object object = variableResolver.resolveVariable(currentInstance, "sEconomicCreate");
        return (CreateEconomicBean) object;
    }

    /**
     * This should be set not by the framework, but by CreateEconomicBean
     * when this class is set on it.
     * @param createEconomicBean
     */
    public void setCreateEconomicBean(CreateEconomicBean createEconomicBean) {
        //this.createEconomicBean = createEconomicBean;
    }

}
