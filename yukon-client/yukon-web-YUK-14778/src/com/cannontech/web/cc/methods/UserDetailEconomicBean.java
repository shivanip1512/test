package com.cannontech.web.cc.methods;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.servlet.http.HttpServletRequest;

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
import com.cannontech.cc.service.exception.EventModificationException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.cc.CommercialCurtailmentBean;
import com.cannontech.web.util.JSFUtil;


public class UserDetailEconomicBean implements BaseDetailBean {
    // stateful services
    private CommercialCurtailmentBean commercialCurtailment;
    
    // stateless services
    private ProgramService programService;
    private EconomicService economicService;
    private StrategyFactory strategyFactory;
    private EconomicStrategy strategy;

    // this state
    private EconomicEventParticipant eventParticipant;
    private EconomicEventPricing currentRevision;

    private DataModel windowDataModel;

    private List<EconomicEventPricingWindow> pricingWindows;

    private Map<EconomicEventParticipantSelectionWindow, Boolean> priceEditable;

    private boolean oneWindowEditable;
    
    public String showDetail(BaseEvent event) {
        LiteYukonUser yukonUser = commercialCurtailment.getYukonUser();
        EconomicEventParticipant participant = 
            economicService.getParticipant((EconomicEvent)event, yukonUser);
        setEventParticipant(participant);
        return "userEconEvent";
    }
    
    public EconomicEvent getEvent() {
        return eventParticipant.getEvent();
    }
    
    public void updateModels() {
        windowDataModel = null;
        priceEditable = null;
    }
    
    public DataModel getWindowModel() {
        if (windowDataModel == null) {
            Collection<EconomicEventPricingWindow> values = getEvent().getInitialRevision().getWindows().values();
            pricingWindows = new ArrayList<EconomicEventPricingWindow>(values);
            Collections.sort(pricingWindows);
            windowDataModel = new ListDataModel(pricingWindows);
        }
        return windowDataModel;
    }

    public BigDecimal getCurrentRowPrice() {
        EconomicEventPricingWindow pricingWindow = 
            (EconomicEventPricingWindow) windowDataModel.getRowData();
        EconomicEventPricingWindow window = 
            economicService.getFallThroughWindow(getCurrentRevision(), 
                                                 pricingWindow.getOffset());
        return window.getEnergyPrice();
    }
    
    public BigDecimal getCurrentRowBuyThrough() {
        EconomicEventParticipantSelectionWindow selectionWindow = getCurrentRowSelectionWindow();
        return selectionWindow.getEnergyToBuy();
    }

    // no need for this, just aldskfjaldkfj
    public void setCurrentRowBuyThrough(BigDecimal buyThrough) {
        EconomicEventParticipantSelectionWindow selectionWindow = getCurrentRowSelectionWindow();
        selectionWindow.setEnergyToBuy(buyThrough);
    }

    private EconomicEventParticipantSelectionWindow getCurrentRowSelectionWindow() {
        EconomicEventPricingWindow pricingWindow = 
            (EconomicEventPricingWindow) windowDataModel.getRowData();
        EconomicEventParticipantSelection selection = 
            eventParticipant.getSelection(getCurrentRevision());
        
        return economicService.getFallThroughWindowSelection(selection, pricingWindow.getOffset());
    }
    
    public boolean isCurrentRowPriceEditable() {
        initPriceEditableLookup();
        EconomicEventParticipantSelectionWindow selectionWindow = getCurrentRowSelectionWindow();
        return priceEditable.get(selectionWindow);
    }

    public boolean isBuyThroughExpired() {
    	return !strategy.isBeforeElectionCutoff(currentRevision, new Date());
    }    
    
    private void initPriceEditableLookup () {
        if (priceEditable != null) {
            return;
        }
        Date now = new Date();
        LiteYukonUser user = commercialCurtailment.getYukonUser();
        oneWindowEditable = false;
        priceEditable = new TreeMap<EconomicEventParticipantSelectionWindow, Boolean>();
        for (EconomicEventPricingWindow pricingWindow : pricingWindows) {
            EconomicEventParticipantSelection selection = 
                eventParticipant.getSelection(getCurrentRevision());
            EconomicEventParticipantSelectionWindow windowSelection = 
                economicService.getFallThroughWindowSelection(selection, pricingWindow.getOffset());
            boolean pricingSelectionCanBeEdited = 
                strategy.canPricingSelectionBeEdited(windowSelection, user, now);
            priceEditable.put(windowSelection, pricingSelectionCanBeEdited);
            oneWindowEditable |= pricingSelectionCanBeEdited;
        }
    }
    
    public boolean isShowSaveButton() {
        return oneWindowEditable;
    }
    
    public String savePrices() {
        EconomicEventParticipantSelection selection = eventParticipant.getSelection(currentRevision);
        String username = commercialCurtailment.getYukonUser().getUsername();
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest)context.getRequest();
        String ip = request.getRemoteAddr();
        String previousAudit = selection.getConnectionAudit();
        selection.setConnectionAudit(previousAudit + "; Modified by " + username + " from " + ip);
        try {
            strategy.saveParticipantSelection(selection, commercialCurtailment.getYukonUser());
            JSFUtil.addNullInfoMessage("kW Buy Through has been saved");
        } catch (EventModificationException e) {
            JSFUtil.handleException("Unable to save prices", e);
        }
        return null;
    }
    
    public ProgramService getProgramService() {
        return programService;
    }
    
    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }
    
    public EconomicEventParticipant getEventParticipant() {
        return eventParticipant;
    }

    public EconomicStrategy getStrategy() {
        return strategy;
    }
    
    public void setEventParticipant(EconomicEventParticipant eventParticipant) {
        this.eventParticipant = eventParticipant;
        currentRevision = eventParticipant.getEvent().getLatestRevision();
        strategy = (EconomicStrategy) strategyFactory.getStrategy(eventParticipant.getEvent().getProgram());
        updateModels();
    }

    public void setEconomicService(EconomicService economicService) {
        this.economicService = economicService;
    }

    public void setCurrentRevision(EconomicEventPricing currentRevision) {
        this.currentRevision = currentRevision;
        updateModels();
    }

    public void setStrategy(EconomicStrategy strategy) {
        this.strategy = strategy;
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

    public void setCommercialCurtailment(CommercialCurtailmentBean commercialCurtailment) {
        this.commercialCurtailment = commercialCurtailment;
    }
    
}
