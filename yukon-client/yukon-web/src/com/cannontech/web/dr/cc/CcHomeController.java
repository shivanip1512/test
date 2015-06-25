package com.cannontech.web.dr.cc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.servlet.http.HttpServletRequest;

import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.cc.dao.AccountingEventDao;
import com.cannontech.cc.dao.BaseEventDao;
import com.cannontech.cc.dao.CurtailmentEventDao;
import com.cannontech.cc.dao.CurtailmentEventNotifDao;
import com.cannontech.cc.dao.EconomicEventDao;
import com.cannontech.cc.dao.EconomicEventParticipantDao;
import com.cannontech.cc.dao.GroupDao;
import com.cannontech.cc.model.AccountingEvent;
import com.cannontech.cc.model.AccountingEventParticipant;
import com.cannontech.cc.model.AvailableProgramGroup;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.CurtailmentEventNotif;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventNotif;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.cc.model.EconomicEventParticipantSelection;
import com.cannontech.cc.model.EconomicEventParticipantSelectionWindow;
import com.cannontech.cc.model.EconomicEventPricing;
import com.cannontech.cc.model.EconomicEventPricingWindow;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramParameter;
import com.cannontech.cc.model.ProgramType;
import com.cannontech.cc.service.AccountingStrategy;
import com.cannontech.cc.service.CICurtailmentStrategy;
import com.cannontech.cc.service.CustomerLMProgramService;
import com.cannontech.cc.service.CustomerPointService;
import com.cannontech.cc.service.EconomicService;
import com.cannontech.cc.service.EconomicStrategy;
import com.cannontech.cc.service.EventService;
import com.cannontech.cc.service.GroupService;
import com.cannontech.cc.service.NotificationStatus;
import com.cannontech.cc.service.ProgramService;
import com.cannontech.cc.service.StrategyFactory;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.predicate.Predicate;
import com.cannontech.core.dao.NotificationGroupDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.customer.CICustomerPointType;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.cc.EventDetailHelper;
import com.cannontech.web.cc.EventListBean;
import com.cannontech.web.cc.methods.DetailAccountingBean;
import com.cannontech.web.cc.methods.DetailEconomicBean;
import com.cannontech.web.cc.methods.DetailNotificationBean;

@Controller
public class CcHomeController {

    @Autowired private BaseEventDao baseEventDao;
    @Autowired private EventService eventService;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private StrategyFactory strategyFactory;
    @Autowired private ProgramService programService;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private EconomicEventDao economicEventDao;
    @Autowired private EconomicEventParticipantDao economicEventParticipantDao;
    @Autowired private EconomicService economicService;
    @Autowired private AccountingEventDao accountingEventDao;
    @Autowired private EventListBean eventListBean;
    @Autowired private EventDetailHelper eventDetailHelper;
    @Autowired private DetailEconomicBean detailEconomicBean;
    @Autowired private DetailAccountingBean detailAccountingBean;
    @Autowired private DetailNotificationBean detailNotificationBean;
    @Autowired private CurtailmentEventDao notificationEventDao;
    @Autowired private CurtailmentEventNotifDao curtailmentNotifDao;
    @Autowired private CustomerPointService customerPointService;
    @Autowired private GroupService groupService;
    @Autowired private CustomerLMProgramService customerLMProgramService;
    @Autowired private NotificationGroupDao notificationGroupDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private GroupDao groupDao;
    
    private String eventHeadingBase = "yukon.web.modules.commercialcurtailment.ccurtSetup.ccurtEvent_heading_";
    private String companyHeadingBase = "yukon.web.modules.commercialcurtailment.ccurtSetup.";
    
    private final Comparator<GroupCustomerNotif> customerListComparator = new Comparator<GroupCustomerNotif>() {
        @Override
        public int compare(GroupCustomerNotif o1, GroupCustomerNotif o2) {
            return o1.getCustomer().compareTo(o2.getCustomer());
        }
    };

    @RequestMapping("/cc/home")
    public String home(ModelMap model, LiteYukonUser user) {
        
        EnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(user);
        
        //Retrieve current, pending, recent events
        List<BaseEvent> currentEvents = baseEventDao.getAllForEnergyCompany(energyCompany, new CurrentEventPredicate());
        List<BaseEvent> pendingEvents = baseEventDao.getAllForEnergyCompany(energyCompany, new PendingEventPredicate());
        List<BaseEvent> recentEvents = baseEventDao.getAllForEnergyCompany(energyCompany, new RecentEventPredicate());
        Collections.reverse(currentEvents);
        Collections.reverse(pendingEvents);
        Collections.reverse(recentEvents);
        
        //Map events by type
        Map<EventType, List<BaseEvent>> events = new HashMap<>();
        events.put(EventType.CURRENT, currentEvents);
        events.put(EventType.PENDING, pendingEvents);
        events.put(EventType.RECENT, recentEvents);
        model.addAttribute("events", events);
        
        //Retrieve program types for user
        List<ProgramType> programTypeList = programService.getProgramTypeList(user);
        
        //Retrieve programs by type
        List<Program> allPrograms = new ArrayList<>();
        for (ProgramType programType : programTypeList) {
            List<Program> programs = programService.getProgramList(programType);
            for (Program program : programs) {
                allPrograms.add(program);
            }
        }
        model.addAttribute("programs", allPrograms);
        
        return "dr/cc/home.jsp";
    }
    
    @RequestMapping("/cc/program/{id}/init")
    public String init(ModelMap model, YukonUserContext userContext, @PathVariable int id) {
        
        Program program = lookupProgramById(id, userContext);
        //CICurtailmentStrategy strategy = strategyFactory.getStrategy(program);
        
        model.addAttribute("program", program);
        // TODO: this is the initialization of the wizard invoked by the "Start" link.
        // See how the legacy app looks and works.
        //List<? extends BaseEvent> eventList = strategy.getEventsForProgram(program);
        
        return "dr/cc/init.jsp";
    }

    @RequestMapping("/cc/program/{programId}/history")
    public String history(ModelMap model, YukonUserContext userContext, @PathVariable int programId) {
        
        Program program = lookupProgramById(programId, userContext);
        model.addAttribute("program", program);
        
        CICurtailmentStrategy strategy = strategyFactory.getStrategy(program);
        List<? extends BaseEvent> eventList = strategy.getEventsForProgram(program);
        Collections.reverse(eventList);
        model.addAttribute("eventHistory", eventList);
        
        return "dr/cc/history.jsp";
    }
    
    private Program lookupProgramById(int id, YukonUserContext userContext) {
        List<ProgramType> programTypeList = programService.getProgramTypeList(userContext.getYukonUser());
        for (ProgramType programType : programTypeList) {
            List<Program> programs = programService.getProgramList(programType);
            for (Program program : programs) {
                if (program.getId() == id) {
                    return program;
                }
            }
        }
        return null;
    }
    
    @RequestMapping("/cc/program/{programId}/event/{eventId}/companyInfo/{companyId}/companyDetail")
    public String companyDetail(ModelMap model,
                                YukonUserContext userContext,
                                @PathVariable int programId,
                                @PathVariable int eventId,
                                @PathVariable int companyId) {

        EconomicEvent event = economicEventDao.getForId(eventId);
        model.addAttribute("event", event);
        
        List<EconomicEventParticipant> participantList = economicEventParticipantDao.getForEvent(event);
        EconomicEventParticipant participant = getCustomerById(participantList, companyId);
        model.addAttribute("participant", participant.getCustomer().getCompanyName());
        
        List<EconomicEventNotif> eventNotifications = economicService.getNotifications(participant);
        model.addAttribute("eventNotifications", eventNotifications);
        
        String tz = userContext.getTimeZone().getDisplayName();
        model.addAttribute("tz", tz);
        
        String programType = event.getProgram().getProgramType().getName();
        model.addAttribute("programType", programType);
        
        String programName = event.getProgram().getName();
        model.addAttribute("programName", programName);
        
        return "dr/cc/detail.jsp";
    }
    
    private EconomicEventParticipant getCustomerById(List<EconomicEventParticipant> participantList, int companyId) {
        for (EconomicEventParticipant participant : participantList) {
            if (participant.getCustomer().getId() == companyId) {
                return participant;
            }
        }
        return null;
    }
    
    @RequestMapping("/cc/groupList")
    public String groupList(ModelMap model, LiteYukonUser user) {
        
        List<Group> allGroups = groupService.getAllGroups(user);
        model.addAttribute("groups", allGroups);
        
        return "dr/cc/groupList.jsp";
    }

    @RequestMapping("/cc/groupDetail/{groupId}")
    public String groupDetail(ModelMap model, @PathVariable int groupId) {
        
        Group group = groupService.getGroup(groupId);
        model.addAttribute("group", group);
        
        List<GroupCustomerNotif> assignedGroupCustomerList = groupService.getAssignedCustomers(group);
        model.addAttribute("assignedCustomers", assignedGroupCustomerList);
        
        List<GroupCustomerNotif> unassignedGroupCustomerList = groupService.getUnassignedCustomers(group, false);
        model.addAttribute("availableCustomers", unassignedGroupCustomerList);
        
        return "dr/cc/groupDetail.jsp";
    }
    
    @RequestMapping("/cc/groupCreate")
    public String groupCreate(ModelMap model, LiteYukonUser user) {
        
        Group currentGroup = groupService.createNewGroup(user);
        model.addAttribute("group", currentGroup);
        
        List<GroupCustomerNotif> assignedGroupCustomerList = new ArrayList<GroupCustomerNotif>();
        model.addAttribute("assignedCustomers", assignedGroupCustomerList);
        
        List<GroupCustomerNotif> unassignedGroupCustomerList = groupService.getUnassignedCustomers(currentGroup, true);
        Collections.sort(unassignedGroupCustomerList, customerListComparator);
        model.addAttribute("availableCustomers", unassignedGroupCustomerList);
        
        return "dr/cc/groupDetail.jsp";
    }
    
    @RequestMapping("/cc/groupSave/{groupId}")
    public String groupSave(ModelMap model,
            YukonUserContext userContext,
            @PathVariable int groupId,
            String[] emails,
            String[] voice,
            String[] sms) {
        
        // TODO: implement saving of notification types (emails, voice, sms)
        // See API for assignedGroupCustomerList.get(0).getNotifMap();
        // the following actually works correctly
        Group group = groupService.getGroup(groupId);
        //groupService.saveGroup(group, assignedGroupCustomerList);
        return "redirect:/dr/cc/groupList";
    }

    @RequestMapping("/cc/programList")
    public String programList(ModelMap model, LiteYukonUser user) {
        
        List<ProgramType> programTypeList = programService.getProgramTypeList(user);
        Map<String, List<Program>> programMap = new HashMap<>();
        for (ProgramType programType : programTypeList) {
            List<Program> programs = programService.getProgramList(programType);
            programMap.put(programType.getName(), programs);
        }
        model.addAttribute("programMap", programMap);
        return "/dr/cc/programList.jsp";
    }
    
    @RequestMapping("/cc/programDetail/{programId}")
    public String programDetail (ModelMap model,
            YukonUserContext userContext,
            @PathVariable int programId) {
        
        Program program = programService.getProgram(programId);
        model.addAttribute("program", program);
        
        CICurtailmentStrategy strategy = strategyFactory.getStrategy(program);
        List<ProgramParameter> programParameters = strategy.getParameters(program);
        model.addAttribute("programParameters", programParameters);
        
        Set<Group> unassignedProgramGroups = programService.getUnassignedGroups(program);
        model.addAttribute("unassignedProgramGroups", unassignedProgramGroups);
        
        List<LiteNotificationGroup> assignedNotificationGroups = new ArrayList<LiteNotificationGroup>(programService.getAssignedNotificationGroups(program));
        Collections.sort(assignedNotificationGroups, LiteComparators.liteNameComparator);
        model.addAttribute("assignedNotificationGroups", assignedNotificationGroups);
        
        Set<LiteNotificationGroup> allNotificationGroups = notificationGroupDao.getAllNotificationGroups();
        allNotificationGroups.removeAll(assignedNotificationGroups);
        List<LiteNotificationGroup> unassignedNotificationGroups = new ArrayList<LiteNotificationGroup>(allNotificationGroups);
        Collections.sort(unassignedNotificationGroups, LiteComparators.liteNameComparator);
        model.addAttribute("unassignedNotificationGroups", unassignedNotificationGroups);
        
        List<AvailableProgramGroup> assignedProgramGroups = programService.getAvailableProgramGroups(program);
        model.addAttribute("assignedProgramGroups", assignedProgramGroups);
        
        model.addAttribute("deletable", !programService.isEventsExistForProgram(program));
        
        return "dr/cc/programDetail.jsp";
    }
    
    @RequestMapping("/cc/programCreate")
    public String programCreate (ModelMap model,
            YukonUserContext userContext) {
        
        List<ProgramType> programTypeList = programService.getProgramTypeList(userContext.getYukonUser());
        model.addAttribute("programTypes", programTypeList);
        return "dr/cc/programCreate.jsp";
    }

    
    @RequestMapping("/cc/programDelete/{programId}")
    public String programDelete (ModelMap model,
            @PathVariable int programId,
            YukonUserContext userContext) {
        
        Program program = programService.getProgram(programId);
        programService.deleteProgram(program);
        return "redirect:/dr/cc/programList";
    }

    @RequestMapping("/cc/programDetailCreate/{programTypeId}/{name}")
    public String programDetailCreate (ModelMap model,
            YukonUserContext userContext,
            @PathVariable int programTypeId,
            @PathVariable String name) {
        
        //TODO validation
        Program program = new Program();
        program.setName(name);
        List<ProgramParameter> programParameters = Collections.emptyList();
        List<Group> assignedGroups = Collections.emptyList();
        List<LiteNotificationGroup> assignedNotificationGroups = Collections.emptyList();
        program.setIdentifierPrefix("EVENT-");
        program.setLastIdentifier(0);
        
        ProgramType newProgramType = null;
        List<ProgramType> programTypeList = programService.getProgramTypeList(userContext.getYukonUser());
        for (ProgramType programType : programTypeList) {
            if (programType.getId() == programTypeId) {
                newProgramType = programType;
                break;
            }
        }
        program.setProgramType(newProgramType);
        Set<LiteNotificationGroup> assignedNotifGroupsSet = new HashSet<>(assignedNotificationGroups);
        programService.saveProgram(program, programParameters, assignedGroups, assignedNotifGroupsSet);
        return "redirect:/dr/cc/programDetail/" + program.getId();
    }
    
    @RequestMapping("/cc/programSave/{programId}")
    public String programSave (ModelMap model,
            YukonUserContext userContext,
            @PathVariable int programId,
            String programName,
            String programIdentifierPrefix,
            Integer programLastIdentifier,
            Integer DEFAULT_EVENT_OFFSET_MINUTES,
            Integer DEFAULT_NOTIFICATION_OFFSET_MINUTES,
            Integer MINIMUM_NOTIFICATION_MINUTES,
            Integer DEFAULT_EVENT_DURATION_MINUTES,
            Integer MINIMUM_EVENT_DURATION_MINUTES,
            Double DEFAULT_ENERGY_PRICE,
            Integer CUSTOMER_ELECTION_CUTOFF_MINUTES,
            @RequestParam("assignedGroup") List<Integer> assignedGroupIds,
            @RequestParam("assignedNotifGroup") List<Integer> assignedNotifGroupIds
            ) {
        
        Program program = programService.getProgram(programId);
        program.setName(programName);
        program.setIdentifierPrefix(programIdentifierPrefix);
        program.setLastIdentifier(programLastIdentifier);
        
        CICurtailmentStrategy strategy = strategyFactory.getStrategy(program);
        List<ProgramParameter> programParameters = strategy.getParameters(program);
        
        for (ProgramParameter parameter : programParameters) {
            switch(parameter.getParameterKey()) {
            case DEFAULT_EVENT_OFFSET_MINUTES:
                setParameter(parameter, DEFAULT_EVENT_OFFSET_MINUTES);
                break;
            case DEFAULT_NOTIFICATION_OFFSET_MINUTES:
                setParameter(parameter, DEFAULT_NOTIFICATION_OFFSET_MINUTES);
                break;
            case MINIMUM_NOTIFICATION_MINUTES:
                setParameter(parameter, MINIMUM_NOTIFICATION_MINUTES);
                break;
            case DEFAULT_EVENT_DURATION_MINUTES:
                setParameter(parameter, DEFAULT_EVENT_DURATION_MINUTES);
                break;
            case MINIMUM_EVENT_DURATION_MINUTES:
                setParameter(parameter, MINIMUM_EVENT_DURATION_MINUTES);
                break;
            case DEFAULT_ENERGY_PRICE:
                setParameter(parameter, DEFAULT_ENERGY_PRICE);
                break;
            case CUSTOMER_ELECTION_CUTOFF_MINUTES:
                setParameter(parameter, CUSTOMER_ELECTION_CUTOFF_MINUTES);
                break;
            default:
                break;
            }
        }
        
        Set<LiteNotificationGroup> assignedNotifGroups = new HashSet<>();
        Set<LiteNotificationGroup> allNotificationGroups = notificationGroupDao.getAllNotificationGroups();
        for (LiteNotificationGroup notifGroup : allNotificationGroups) {
            if (assignedNotifGroupIds.contains(notifGroup.getNotificationGroupID())) {
                assignedNotifGroups.add(notifGroup);
            }
        }
        
        List<Group> assignedGroups = groupDao.getForIds(assignedGroupIds);

        programService.saveProgram(program, programParameters, assignedGroups, assignedNotifGroups);
        return "redirect:/dr/cc/programList";
    }

    private void setParameter (ProgramParameter parameter, Number parameterInput) {
        if (parameterInput != null) {
            parameter.setParameterValue(parameterInput.toString());
        }
    }
    
    private GroupCustomerNotif getCustomerNotifById(DataModel listModel, int customerId) {
        GroupCustomerNotif customerNotif;
        for (int i = 0; i < listModel.getRowCount(); i += 1) {
            listModel.setRowIndex(i);
            customerNotif = (GroupCustomerNotif) listModel.getRowData();
            if (customerNotif.getCustomer().getId() == customerId) {
                return customerNotif;
            }
        }
        return null;
    }
    
    @RequestMapping("/cc/program/{programId}/event/{eventId}/revision/{revision}")
    public String revision(ModelMap model,
            YukonUserContext userContext,
            @PathVariable int programId,
            @PathVariable int eventId,
            @PathVariable int revision,
            HttpServletRequest request) {
        return economicDetail(model, userContext, 0, programId, eventId, revision, request);
    }
    
    @RequestMapping("/cc/program/{programId}/event/{eventId}/detail")
    public String detail (ModelMap model,
            YukonUserContext userContext,
            HttpServletRequest request,
            @PathVariable int programId,
            @PathVariable int eventId) {
        
        Integer programTypeId = programService.getProgram(programId).getProgramType().getId();
        switch(programTypeId) {
        case 1:
        case 2:
            return capacityDetail(model, userContext, programTypeId, eventId);
        case 3:
            return accountingDetail(model, userContext, programTypeId, eventId);
        case 4:
            return economicDetail(model, userContext, programTypeId, programId, eventId, -1, request);
        default:
            break;
        }
        return "we have a problem";
    }
    
    private String accountingDetail(ModelMap model,
                                    YukonUserContext userContext,
                                    Integer programTypeId,
                                    int id) {
        
        AccountingEvent acctEvent = accountingEventDao.getForId(id);
        model.addAttribute("event", acctEvent);
        model.addAttribute("programTypeId", programTypeId);
        model.addAttribute("reason", acctEvent.getReason());
        model.addAttribute("duration", acctEvent.getDuration());
        
        Program acctProgram = acctEvent.getProgram();
        AccountingStrategy strategy = (AccountingStrategy) strategyFactory.getStrategy(acctProgram);
        List<AccountingEventParticipant> eventNotifications = strategy.getParticipants(acctEvent);
        model.addAttribute("eventNotifications", eventNotifications);
        
        model.addAttribute("programType", acctEvent.getProgram().getProgramType().getName());
        model.addAttribute("programName", acctEvent.getProgram().getName());
        model.addAttribute("affectedCustomers", composeEventHeading(eventHeadingBase, userContext, "accounting_affected_customers"));
        
        return "dr/cc/detail.jsp";
    }
    
    private String capacityDetail(ModelMap model,
                                  YukonUserContext userContext,
                                  Integer programTypeId,
                                  int eventId) {
                              
                              CurtailmentEvent curtailmentEvent = notificationEventDao.getForId(eventId);
                              List<CurtailmentEventNotif> eventNotifications = curtailmentNotifDao.getForEvent(curtailmentEvent);
                              
                              model.addAttribute("event", curtailmentEvent);
                              model.addAttribute("programTypeId", programTypeId);
                              model.addAttribute("duration", curtailmentEvent.getDuration());
                              model.addAttribute("modificationState", curtailmentEvent.getStateDescription());
                              model.addAttribute("message", curtailmentEvent.getMessage());
                              model.addAttribute("eventNotifications", eventNotifications);
                              String[] headingSuffixes = new String[]{
                                      "ccurtEvent_heading_accounting_company_heading",
                                      "ccurtEvent_heading_reason",
                                      "ccurtEvent_heading_notif_type",
                                      "ccurtEvent_heading_time",
                                      "programState"};
                              
                              List<String> notificationTableHead = assembleHeading(userContext, companyHeadingBase, headingSuffixes);;
                              model.addAttribute("notificationTableHead", notificationTableHead);
                              
                              model.addAttribute("programType", curtailmentEvent.getProgram().getProgramType().getName());
                              model.addAttribute("programName", curtailmentEvent.getProgram().getName());
                              
                              return "dr/cc/detail.jsp";
                          }
    
    public String economicDetail(ModelMap model,
            YukonUserContext userContext,
            Integer programTypeId,
            int programId,
            int eventId,
            int revisionNumber,
            HttpServletRequest request) {
        
        EconomicEvent event = economicEventDao.getForId(eventId);
        List<EconomicEventParticipant> participantList = economicEventParticipantDao.getForEvent(event);
        DataModel participantDataModel = new ListDataModel(participantList);
        EconomicStrategy strategy = (EconomicStrategy) strategyFactory.getStrategy(event.getProgram());
        getOtherRevisionsModel(event);
        Map<Integer, EconomicEventPricing> revisions = event.getRevisions();
        getWindowModel(event);
        model.addAttribute("event", event);

        DateTimeFormatter dateTimeFormatter = dateFormattingService.getDateTimeFormatter(DateFormatEnum.TIME, userContext);
        List<String> pricingTableTotals = new ArrayList<>();

        int nParticipants = participantDataModel.getRowCount();
        List<List<Object>> tableData = new ArrayList<>();
        participantDataModel.setRowIndex(0);
        
        // determine pricing revision to use, if not passed in, use current
        EconomicEventPricing currentRevision = event.getLatestRevision();
        EconomicEventPricing curEconEventPricingRevision =
            revisionNumber == -1 ? currentRevision : getRevisionAt(revisions, revisionNumber);
        EconomicEventPricingWindow curPriceWindow;
        BigDecimal curEnergyPrice;
        String curEnergyPriceStr = null;
        Date pricingWindowStartTime;
        Instant pricingWindowStartTimeInstant;
        int countRevisions =
            economicService.getFallThroughWindow(curEconEventPricingRevision, 0).getPricingRevision().getWindows().size();
        BigDecimal[] columnTotals = new BigDecimal[countRevisions];
        for (int bigInd = 0; bigInd < columnTotals.length; bigInd += 1) {
            columnTotals[bigInd] = new BigDecimal(0);
        }
        List<List<String>> pricingTableHead = new ArrayList<>();
        for (int rev = 0; rev < countRevisions; rev += 1) {
            curPriceWindow = economicService.getFallThroughWindow(curEconEventPricingRevision, rev);
            if (curPriceWindow == null) {
                break;
            }
            pricingWindowStartTime = curPriceWindow.getStartTime();
            pricingWindowStartTimeInstant = new Instant(pricingWindowStartTime.getTime());
            String startTimeStr = pricingWindowStartTimeInstant.toString(dateTimeFormatter);
            curEnergyPrice = curPriceWindow.getEnergyPrice();
            curEnergyPriceStr = curEnergyPrice.toString();
            List<String> priceHeadings = new ArrayList<>();
            priceHeadings.add(startTimeStr);
            priceHeadings.add(curEnergyPriceStr);
            pricingTableHead.add(priceHeadings);
        }
        Set<Entry<Integer, EconomicEventPricing>> revisionSet = revisions.entrySet();
        List<List<EconomicEventPricingWindow>> pricingWindows = new ArrayList<List<EconomicEventPricingWindow>>();
        List<Integer> revisionList = new ArrayList<Integer>();
        for (Entry<Integer, EconomicEventPricing> price : revisionSet) {
            EconomicEventPricing pricingValue = price.getValue();
            List<EconomicEventPricingWindow> pricingWindowsForRevision = new ArrayList<>();
            for (int rev = 0; rev < countRevisions; rev += 1) {
                curPriceWindow = economicService.getFallThroughWindow(pricingValue, rev);
                pricingWindowsForRevision.add(curPriceWindow);
            }
            // get revision number so jsp knows how many revisions
            Integer revision = pricingValue.getRevision();
            revisionList.add(revision);
            pricingWindows.add(pricingWindowsForRevision);
        }
        model.addAttribute("pricingWindows", pricingWindows); // TODO: needed? remove if not
        model.addAttribute("revisionList", revisionList);
        model.addAttribute("selectedRevision", curEconEventPricingRevision.getRevision());
        pricingWindows.get(0).get(0).getPricingRevision().getRevision();
        Integer columnIndex = 0;
        for (int rowInd = 0; rowInd < nParticipants; rowInd += 1) {
            
            List<Object> pricingData = new ArrayList<>();
            participantDataModel.setRowIndex(rowInd);
            EconomicEventParticipant pRowData = (EconomicEventParticipant) participantDataModel.getRowData();
            String companyName = pRowData.getCustomer().getCompanyName();
            Integer idInteger = pRowData.getCustomer().getId();
            Map<String, Object> companyInfo = new HashMap<>();
            companyInfo.put("name", companyName);
            companyInfo.put("id", idInteger);
            pricingData.add(companyInfo);
            pricingData.add(getAckForRow(pRowData, currentRevision));
            String imgPath = request.getContextPath() + getNotifForRow(pRowData, strategy);
            pricingData.add(imgPath);
            tableData.add(pricingData);

            EconomicEventParticipantSelectionWindow selection;
            for (columnIndex = 0; columnIndex < countRevisions; columnIndex += 1) {
                    selection = economicService.getCustomerSelectionWindow(currentRevision, pRowData, columnIndex);
                BigDecimal energyToBuy = selection.getEnergyToBuy();
                pricingData.add(energyToBuy.toString());
                columnTotals[columnIndex] = columnTotals[columnIndex].add(energyToBuy);
            }
        }
        for (Integer totalsIndex = 0; totalsIndex < columnIndex; totalsIndex += 1) {
            pricingTableTotals.add(columnTotals[totalsIndex] == null ? "-----" : columnTotals[totalsIndex].toString());
        }
        model.addAttribute("pricingTableHead", pricingTableHead);
        model.addAttribute("tableData", tableData);
        model.addAttribute("pricingTableTotals", pricingTableTotals);
        
        model.addAttribute("programType", event.getProgram().getProgramType().getName());
        model.addAttribute("programName", event.getProgram().getName());
        
        model.addAttribute("economicDetail", "yes");
        model.addAttribute("legend", composeEventHeading(companyHeadingBase, userContext, "ccurtEvent_pricing_legend"));
        Program program = programService.getProgram(programId);
        model.addAttribute("program", program);
        return "dr/cc/detail.jsp";
    }
    
    @RequestMapping("/cc/customerList")
    public String customerList (ModelMap model, YukonUserContext userContext) {
        
        EnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(userContext.getYukonUser());
        List<CICustomerStub> customerList = customerPointService.getCustomers(energyCompany);
        model.addAttribute("customerList", customerList);
        return "dr/cc/customerList.jsp";
    }
    
    @RequestMapping("/cc/customerDetail/{customerId}")
    public String customerList (ModelMap model, YukonUserContext userContext,
            @PathVariable int customerId) {
        
        CICustomerStub customer = customerPointService.getCustomer(customerId);
        model.addAttribute("customer", customer);
        Map<CICustomerPointType, BigDecimal> pointValueCache = customerPointService.getPointValueCache(customer);
        model.addAttribute("pointValueCache", pointValueCache);
        List<LiteYukonPAObject> activePrograms =
            new ArrayList<LiteYukonPAObject>(customerLMProgramService.getProgramsForCustomer(customer));
        List<LiteYukonPAObject> availablePrograms =
            new ArrayList<LiteYukonPAObject>(customerLMProgramService.getAvailableProgramsForCustomer(customer));
        model.addAttribute("activePrograms", activePrograms);
        model.addAttribute("availablePrograms", availablePrograms);
        
        List<CICustomerPointType> pointTypeList = customerPointService.getPointTypeList(customer);
        model.addAttribute("pointTypeList", pointTypeList);
        return "dr/cc/customerDetail.jsp";
    }
    
    private Group getGroupById(DataModel groupModel, int groupId) {
        Group group;
        for (int i = 0; i < groupModel.getRowCount(); i += 1) {
            groupModel.setRowIndex(i);
            group = (Group) groupModel.getRowData();
            if (group.getId() == groupId) {
                return group;
            }
        }
        return null;
    }

    private EconomicEventPricing getRevisionAt(Map<Integer, EconomicEventPricing> revisions, int revisionKey) {
        Set<Entry<Integer, EconomicEventPricing>> revisionSet = revisions.entrySet();
        
        for (Entry<Integer, EconomicEventPricing> revision : revisionSet) {
            if (revision.getKey() == revisionKey) {
                return revision.getValue();
            }
        }
        return null;
    }

    // TODO: determine if this will ever be needed
//    private BaseEvent lookupEventById(Program program, int id) {
//        CICurtailmentStrategy strategy = strategyFactory.getStrategy(program);
//        List<? extends BaseEvent> eventList = strategy.getEventsForProgram(program);
//        for (BaseEvent event : eventList) {
//            if (event.getId() == id) {
//                return event;
//            }
//        }
//        return null;
//    }
    
    private String composeEventHeading(String headingBase, YukonUserContext userContext, String specific) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String resolvable = headingBase + specific;
        String msg = accessor.getMessage(resolvable);
        return msg;
    }
    
    private List<String> assembleHeading(YukonUserContext userContext, String keyBase, String[] suffixes) {
        List<String> headingList = new ArrayList<String>();
        for (int ind = 0; ind < suffixes.length; ind += 1) {
            String msg = composeEventHeading(keyBase, userContext, suffixes[ind]);
            headingList.add(msg);
        }
        return headingList;
    }
    
    private String getAckForRow(EconomicEventParticipant rowData, EconomicEventPricing currentRevision) {
        EconomicEventParticipantSelection selection = rowData.getSelection(currentRevision);
        switch (selection.getState()) {
        case DEFAULT:
            return "D";
        case MANUAL:
            return "A";
        default:
            return "-";
        }
    }
    
    private String getNotifForRow(EconomicEventParticipant participant, EconomicStrategy strategy) {
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
    
    private boolean isConsideredActive(BaseEvent event) {
        
        CICurtailmentStrategy strategy = strategyFactory.getStrategy(event.getProgram());
        
        return strategy.isConsideredActive(event);
    }
    
    private DataModel getWindowModel(EconomicEvent economicEvent) {
        Collection<EconomicEventPricingWindow> values = economicEvent.getInitialRevision().getWindows().values();
        List<EconomicEventPricingWindow> windows = new ArrayList<EconomicEventPricingWindow>(values);
        Collections.sort(windows);
        DataModel windowDataModel = new ListDataModel(windows);
        return windowDataModel;
    }

    private BigDecimal getColumnPrice(EconomicEvent economicEvent) {
        DataModel columnModel = getWindowModel(economicEvent);
        if (columnModel.isRowAvailable()) { // read: isColumnAvailable()
            EconomicEventPricingWindow pricingWindow = 
                (EconomicEventPricingWindow) columnModel.getRowData(); // read: getColumnData()
            EconomicEventPricingWindow window = 
                economicService.getFallThroughWindow(economicEvent.getLatestRevision(), 
                                                     pricingWindow.getOffset());
            return window.getEnergyPrice();
        }
        return null;
    }

    private BigDecimal getColumnValue(DataModel rowModel, EconomicEvent event) {
        if (rowModel.isRowAvailable()) {
            EconomicEventParticipant row = 
                (EconomicEventParticipant) rowModel.getRowData();
            DataModel columnModel = getWindowModel(event);
            if (columnModel.isRowAvailable()) { // read: isColumnAvailable()
                EconomicEventPricingWindow pricingWindow = 
                    (EconomicEventPricingWindow) columnModel.getRowData(); // read: getColumnData()
                Integer column = pricingWindow.getOffset();
                EconomicEventParticipantSelectionWindow selection = 
                    economicService.getCustomerSelectionWindow(event.getLatestRevision(),row,column);
                return selection.getEnergyToBuy();
            }
        }
        return null;
    }
    
    private ListDataModel getOtherRevisionsModel(EconomicEvent economicEvent) {
        ArrayList<EconomicEventPricing> others = 
            new ArrayList<EconomicEventPricing>(economicEvent.getRevisions().values());
        others.remove(economicEvent.getLatestRevision());
        ListDataModel otherRevisionsModel = new ListDataModel(others);
        return otherRevisionsModel;
    }
    
    private class RecentEventPredicate implements Predicate<BaseEvent> {
        private Date now;
        private Date sixMonthsAgo;
        public RecentEventPredicate () {
            now = new Date();
            // get for last six months
            // used computer's TZ because the length of time is so long it won't matter
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.MONTH, -6);
            sixMonthsAgo = calendar.getTime();
        }

        @Override
        public boolean evaluate(BaseEvent event) {
            if (event.getStopTime().before(sixMonthsAgo)) {
                return false;
            } else if (event.getStopTime().after(now)) {
                return !isConsideredActive(event);
            }
                return true;
        }
    }
    
    private class CurrentEventPredicate implements Predicate<BaseEvent> {
        @Override
        public boolean evaluate(BaseEvent event) {
            Date now = new Date();
            if (event.getStopTime().before(now)) {
                return false;
            } else if (event.getStartTime().after(now)) {
                return false;
            } else if (!isConsideredActive(event)) {
                return false;
            } else {
                return true;
            }
        }
    }
    
    private class PendingEventPredicate implements Predicate<BaseEvent> {
        private Date now = new Date();
        @Override
        public boolean evaluate(BaseEvent event) {
            if (event.getStartTime().before(now)) {
                return false;
            } else if (!isConsideredActive(event)) {
                return false;
            } else {
                return true;
            }
        }
    }
    
}