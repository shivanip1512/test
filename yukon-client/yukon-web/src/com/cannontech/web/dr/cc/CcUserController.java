package com.cannontech.web.dr.cc;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.cc.dao.AccountingEventDao;
import com.cannontech.cc.dao.CurtailmentEventDao;
import com.cannontech.cc.dao.CurtailmentEventNotifDao;
import com.cannontech.cc.model.AccountingEvent;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramTypeEnum;
import com.cannontech.cc.service.CustomerEventService;
import com.cannontech.cc.service.ProgramService;
import com.cannontech.cc.service.StrategyFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;

@Controller
@RequestMapping("/cc/user/*")
public class CcUserController {
    @Autowired private AccountingEventDao accountingEventDao;
    @Autowired private CurtailmentEventDao curtailmentEventDao;
    @Autowired private CurtailmentEventNotifDao curtailmentEventNotifDao;
    @Autowired private CustomerEventService customerEventService;
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
                              HttpServletRequest request) {
        
        ProgramTypeEnum programType = programService.getProgramType(programId);
        switch (programType) {
            case CAPACITY_CONTINGENCY:
            case DIRECT_CONTROL:
                return capacityDetail(model, userContext, eventId);
            case ACCOUNTING:
                return accountingDetail(model, userContext, eventId);
            case ECONOMIC:
                return economicDetail(model, userContext, programId, eventId, -1, request);
            default:
                throw new IllegalArgumentException("No case for program type: " + programType);
        }
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
        model.addAttribute("startDate", event.getStartTime());
        model.addAttribute("notificationTime", event.getNotificationTime());
        model.addAttribute("startTime", event.getStartTime());
        model.addAttribute("stopTime", event.getStopTime());
        model.addAttribute("message", event.getMessage());
        model.addAttribute("tz", userContext.getTimeZone().getID());
        
        return "dr/cc/detail.jsp";
    }
    
    private String economicDetail(ModelMap model, 
                                  YukonUserContext userContext, 
                                  int programId, 
                                  int eventId, 
                                  int revisionId,
                                  HttpServletRequest request) {
        //TODO
        return null;
    }
}
