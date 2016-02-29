package com.cannontech.web.dr.cc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.cc.dao.AccountingEventDao;
import com.cannontech.cc.dao.CurtailmentEventDao;
import com.cannontech.cc.dao.CurtailmentEventNotifDao;
import com.cannontech.cc.dao.EconomicEventDao;
import com.cannontech.cc.model.AccountingEvent;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.cc.model.EconomicEventParticipantSelection;
import com.cannontech.cc.model.EconomicEventParticipantSelectionWindow;
import com.cannontech.cc.model.EconomicEventPricing;
import com.cannontech.cc.model.EconomicEventPricingWindow;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.CurtailmentProgramType;
import com.cannontech.cc.service.CustomerEventService;
import com.cannontech.cc.service.EconomicService;
import com.cannontech.cc.service.EconomicStrategy;
import com.cannontech.cc.service.ProgramService;
import com.cannontech.cc.service.StrategyFactory;
import com.cannontech.cc.service.exception.EventModificationException;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;

@Controller
@RequestMapping("/cc/user/*")
public class CcUserController {
    private static Logger log = YukonLogManager.getLogger(CcUserController.class);
    @Autowired private AccountingEventDao accountingEventDao;
    @Autowired private CurtailmentEventDao curtailmentEventDao;
    @Autowired private CurtailmentEventNotifDao curtailmentEventNotifDao;
    @Autowired private CustomerEventService customerEventService;
    @Autowired private EconomicEventDao economicEventDao;
    @Autowired private EconomicService economicService;
    @Autowired private ProgramService programService;
    @Autowired private StrategyFactory strategyFactory;
    
    @RequestMapping("overview")
    public String overview(ModelMap model, LiteYukonUser user) {
        
        List<BaseEvent> currentEvents = customerEventService.getCurrentEvents(user);
        model.addAttribute("currentEvents", currentEvents);
        List<BaseEvent> pendingEvents = customerEventService.getPendingEvents(user);
        model.addAttribute("pendingEvents", pendingEvents);
        List<BaseEvent> recentEvents = customerEventService.getRecentEvents(user);
        model.addAttribute("recentEvents", recentEvents);
        
        return "dr/cc/user/overview.jsp";
    }
    
    @RequestMapping("program/{programId}/event/{eventId}/detail")
    public String eventDetail(ModelMap model, 
                              YukonUserContext userContext,
                              @PathVariable int programId,
                              @PathVariable int eventId,
                              FlashScope flash) {
        
        CurtailmentProgramType programType = programService.getProgramType(programId);
        switch (programType) {
            case CAPACITY_CONTINGENCY:
            case DIRECT_CONTROL:
                return capacityDetail(model, userContext, eventId);
            case ACCOUNTING:
                return accountingDetail(model, userContext, eventId);
            case ECONOMIC:
                return economicDetail(model, flash, userContext, eventId);
            default:
                throw new IllegalArgumentException("No case for program type: " + programType);
        }
    }
    
    @RequestMapping("program/{programId}/event/{eventId}")
    public String updateEconEvent(@PathVariable int programId,
                                  @PathVariable int eventId,
                                  @ModelAttribute("windowPrices") PriceMapWrapper priceMap,
                                  LiteYukonUser user,
                                  HttpServletRequest request,
                                  FlashScope flash) {
        
        EconomicEvent event = economicEventDao.getForId(eventId);
        EconomicEventParticipant participant = economicService.getParticipant(event, user);
        EconomicEventPricing latestRevision = event.getLatestRevision();
        EconomicEventParticipantSelection selection = participant.getSelection(latestRevision);
        Program program = event.getProgram();
        EconomicStrategy strategy = (EconomicStrategy) strategyFactory.getStrategy(program);
        
        // Add audit log
        String userName = user.getUsername();
        String ip = request.getRemoteAddr();
        String previousAudit = selection.getConnectionAudit();
        selection.setConnectionAudit(previousAudit + "; Modified by " + userName + " from " + ip);
        
        // Modify selection to update prices
        Map<Integer, Double> windowPrices = priceMap.getWindowPrices();
        for (int windowId : windowPrices.keySet()) {
            double price = windowPrices.get(windowId);
            boolean updateSuccessful = false;
            for (EconomicEventParticipantSelectionWindow window : selection.getSelectionWindows()) {
                if (window.getId() == windowId) {
                    window.setEnergyToBuy(new BigDecimal(price));
                    updateSuccessful = true;
                    break;
                }
            }
            if (!updateSuccessful) {
                log.error("Unable to find window with id " + windowId + " for event \"" + event.getDisplayName() 
                          + "\" with id " + event.getId());
                flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.user.event.detail.saveBuyThrough.failure"));
            }
        }
        
        try {
            strategy.saveParticipantSelection(selection, user);
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.user.event.detail.saveBuyThrough.success"));
        } catch (EventModificationException e) {
            log.error("Unable to save prices for CI economic event.", e);
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.user.event.detail.saveBuyThrough.failure", e));
        }
        
        return "redirect:/dr/cc/user/program/" + programId + "/event/" + eventId + "/detail";
    }
    
    private String accountingDetail(ModelMap model,
                                    YukonUserContext userContext,
                                    int id) {
        
        AccountingEvent event = accountingEventDao.getForId(id);
        Program acctProgram = event.getProgram();
        model.addAttribute("program", acctProgram);
        model.addAttribute("displayName", event.getDisplayName());
        model.addAttribute("startTime", event.getStartTime());
        model.addAttribute("duration", event.getDuration());
        model.addAttribute("reason", event.getReason());
        model.addAttribute("tz", userContext.getTimeZone().getID());
        
        return "dr/cc/user/detail.jsp";
    }
    
    private String capacityDetail(ModelMap model,
                                  YukonUserContext userContext,
                                  int eventId) {
        
        CurtailmentEvent event = curtailmentEventDao.getForId(eventId);
        Program program = event.getProgram();
        model.addAttribute("program", program);
        model.addAttribute("displayName", event.getDisplayName());
        model.addAttribute("startDate", event.getNotificationTime());
        model.addAttribute("notificationTime", event.getNotificationTime());
        model.addAttribute("startTime", event.getStartTime());
        model.addAttribute("stopTime", event.getStopTime());
        model.addAttribute("message", event.getMessage());
        model.addAttribute("tz", userContext.getTimeZone().getID());
        
        return "dr/cc/user/detail.jsp";
    }
    
    private String economicDetail(ModelMap model, 
                                  FlashScope flash,
                                  YukonUserContext userContext, 
                                  int eventId) {
        
        LiteYukonUser user = userContext.getYukonUser();
        EconomicEvent event = economicEventDao.getForId(eventId);
        model.addAttribute("eventId", eventId);
        model.addAttribute("displayName", event.getDisplayName());
        model.addAttribute("startDate", event.getNotificationTime());
        model.addAttribute("notificationTime", event.getNotificationTime());
        model.addAttribute("startTime", event.getStartTime());
        model.addAttribute("stopTime", event.getStopTime());
        
        Program program = event.getProgram();
        model.addAttribute("program", program);
        
        EconomicStrategy strategy = (EconomicStrategy) strategyFactory.getStrategy(program);
        boolean buyThroughExpired = !strategy.isBeforeElectionCutoff(event.getLatestRevision(), new Date());
        if (buyThroughExpired) {
            flash.setWarning(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.user.event.detail.buyThroughExpired"));
        }
        
        EconomicEventPricing latestRevision = event.getLatestRevision();
        model.addAttribute("latestRevision", latestRevision.getRevision());
        
        Collection<EconomicEventPricingWindow> pricingWindows = event.getInitialRevision().getWindows().values();
        List<EconomicEventPricingWindow> sortedPricingWindows = new ArrayList<EconomicEventPricingWindow>(pricingWindows);
        Collections.sort(sortedPricingWindows);
        
        List<PricingRow> rows = new ArrayList<>();
        boolean atLeastOneWindowEditable = false;
        for (EconomicEventPricingWindow window : sortedPricingWindows) {
            Date startTime = window.getStartTime();
            
            EconomicEventPricingWindow fallThroughWindow = economicService.getFallThroughWindow(latestRevision, window.getOffset());
            BigDecimal energyPrice = fallThroughWindow.getEnergyPrice();
            
            EconomicEventParticipant participant = economicService.getParticipant(event, user);
            EconomicEventParticipantSelection selection = participant.getSelection(latestRevision);
            EconomicEventParticipantSelectionWindow selectionWindow = economicService.getFallThroughWindowSelection(selection, window.getOffset());
            BigDecimal buyThrough = selectionWindow.getEnergyToBuy();
            
            boolean editable = strategy.canPricingSelectionBeEdited(selectionWindow, user, new Date());
            if (editable) {
                atLeastOneWindowEditable = true;
            }
            
            PricingRow row = new PricingRow(startTime, energyPrice, buyThrough, selectionWindow.getId(), editable);
            rows.add(row);
        }
        model.addAttribute("pricingRows", rows);
        model.addAttribute("atLeastOneWindowEditable", atLeastOneWindowEditable);
        
        model.addAttribute("priceMap", new PriceMapWrapper());
        
        return "dr/cc/user/detail.jsp";
    }
    
    public static final class PriceMapWrapper {
        private Map<Integer, Double> windowPrices;
        
        public void setWindowPrices(Map<Integer, Double> windowPrices) {
            this.windowPrices = windowPrices;
        }
        
        public Map<Integer, Double> getWindowPrices() {
            return windowPrices;
        }
    }
    
    public static final class PricingRow {
        private Date startTime;
        private BigDecimal energyPrice;
        private BigDecimal buyThrough;
        private int windowId;
        private boolean isPriceEditable;
        
        public PricingRow(Date startTime, BigDecimal energyPrice, BigDecimal buyThrough, int windowId, boolean isPriceEditable) {
            this.startTime = startTime;
            this.energyPrice = energyPrice;
            this.buyThrough = buyThrough;
            this.windowId = windowId;
            this.isPriceEditable = isPriceEditable;
        }
        
        public Date getStartTime() {
            return startTime;
        }

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }

        public BigDecimal getEnergyPrice() {
            return energyPrice;
        }

        public void setEnergyPrice(BigDecimal energyPrice) {
            this.energyPrice = energyPrice;
        }

        public BigDecimal getBuyThrough() {
            return buyThrough;
        }

        public void setBuyThrough(BigDecimal buyThrough) {
            this.buyThrough = buyThrough;
        }

        public int getWindowId() {
            return windowId;
        }

        public void setWindowId(int windowId) {
            this.windowId = windowId;
        }

        public boolean isPriceEditable() {
            return isPriceEditable;
        }

        public void setPriceEditable(boolean isPriceEditable) {
            this.isPriceEditable = isPriceEditable;
        }
    }
}
