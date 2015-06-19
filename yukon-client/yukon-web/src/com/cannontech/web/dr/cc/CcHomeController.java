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

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.cc.dao.AccountingEventDao;
import com.cannontech.cc.dao.BaseEventDao;
import com.cannontech.cc.dao.CurtailmentEventDao;
import com.cannontech.cc.dao.CurtailmentEventNotifDao;
import com.cannontech.cc.dao.EconomicEventDao;
import com.cannontech.cc.dao.EconomicEventParticipantDao;
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
    
    private DataModel windowDataModel = null;
    private EconomicEventPricing currentRevision = null;
    private ListDataModel otherRevisionsModel = null;
    private List<EconomicEventParticipant> participantList = null;
    private DataModel participantDataModel = null;
    private EconomicStrategy strategy = null;
    private String eventHeadingBase = "yukon.web.modules.commercialcurtailment.ccurtSetup.ccurtEvent_heading_";
    private String companyHeadingBase = "yukon.web.modules.commercialcurtailment.ccurtSetup.";
    private DataModel assignedGroupModel = new ListDataModel();
    private DataModel unassignedGroupModel = new ListDataModel();
    private List<ProgramParameter> programParameters;
    private List<Group> assignedGroups;
    private ArrayList<Group> unassignedGroups;
    private List<LiteNotificationGroup> assignedNotificationGroups;
    private List<LiteNotificationGroup> unassignedNotificationGroups;
    private boolean programDeletable; // TODO: determine if this is needed
    private DataModel groupCustomerModel = new ListDataModel();
    private DataModel customerModel = new ListDataModel();
    private List<GroupCustomerNotif> assignedGroupCustomerList;
    private List<GroupCustomerNotif> unassignedGroupCustomerList;
    private Group currentGroup = null;

    @RequestMapping("/cc/home")
    public String home (ModelMap model, YukonUserContext userContext) {
        
        LiteYukonUser user = userContext.getYukonUser();
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
    
    private Program lookupProgramById(int id, YukonUserContext userContext) {
        List<ProgramType> programTypeList = programService.getProgramTypeList(userContext.getYukonUser());
        Program nullProgram = null;
        for (ProgramType programType : programTypeList) {
            List<Program> programs = programService.getProgramList(programType);
            for (Program program : programs) {
                if (program.getId() == id) {
                    return program;
                }
            }
        }
        return nullProgram;
    }
    @RequestMapping("/cc/program/{id}/init")
    public String init (ModelMap model, YukonUserContext userContext, @PathVariable int id) {
        
        Program program = lookupProgramById(id, userContext);
        CICurtailmentStrategy strategy = strategyFactory.getStrategy(program);
        
        model.addAttribute("program", program);
        // TODO: this is the initialization of the wizard invoked by the "Start" link.
        // See how the legacy app looks and works.
        //List<? extends BaseEvent> eventList = strategy.getEventsForProgram(program);
        
        return "dr/cc/init.jsp";
    }

    @RequestMapping("/cc/program/{programId}/history")
    public String history (ModelMap model,
            YukonUserContext userContext,
            @PathVariable int programId) {
        
        Program program = lookupProgramById(programId, userContext);
        CICurtailmentStrategy strategy = strategyFactory.getStrategy(program);
        List<? extends BaseEvent> eventList = strategy.getEventsForProgram(program);
        Collections.reverse(eventList);
        model.addAttribute("program", program);
        model.addAttribute("eventHistory", eventList);
        return "dr/cc/history.jsp";
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
    
    private EconomicEventParticipant getCustomerById(List<EconomicEventParticipant> participantList, int companyId) {
        for (EconomicEventParticipant participant : participantList) {
            if (participant.getCustomer().getId() == companyId) {
                return participant;
            }
        }
        return null;
    }
    
    @RequestMapping("/cc/program/{programId}/event/{eventId}/companyInfo/{companyId}/companyDetail")
    public String companyDetail (ModelMap model,
            YukonUserContext userContext,
            @PathVariable int programId,
            @PathVariable int eventId,
            @PathVariable int companyId) {

        EconomicEvent event = economicEventDao.getForId(eventId);
        participantList = economicEventParticipantDao.getForEvent(event);
        EconomicEventParticipant participant = getCustomerById(participantList, companyId);
        List<EconomicEventNotif> eventNotifications = economicService.getNotifications(participant);
        model.addAttribute("event", event);
        String tz = userContext.getTimeZone().getDisplayName();
        model.addAttribute("tz", tz);
        
        String programType = event.getProgram().getProgramType().getName();
        String programName = event.getProgram().getName();
        
        model.addAttribute("programType", programType);
        model.addAttribute("programName", programName);
        model.addAttribute("participant", participant.getCustomer().getCompanyName());
        model.addAttribute("eventNotifications", eventNotifications);
        return "dr/cc/detail.jsp";
    }

    @RequestMapping("/cc/groupList")
    public String groupList (ModelMap model,
            YukonUserContext userContext) {
        
        LiteYukonUser user = userContext.getYukonUser();
        List<Group> allGroups = groupService.getAllGroups(user);
        model.addAttribute("groups", allGroups);
        return "dr/cc/groupList.jsp";
    }

    @RequestMapping("/cc/groupDetail/{groupId}")
    public String groupDetail (ModelMap model,
            YukonUserContext userContext,
            @PathVariable int groupId) {
        
        Group group = groupService.getGroup(groupId);
        group.getEnergyCompanyId(); // TODO: remove
        model.addAttribute("group", group);
        assignedGroupCustomerList = groupService.getAssignedCustomers(group);
        groupCustomerModel.setWrappedData(assignedGroupCustomerList);
        unassignedGroupCustomerList = groupService.getUnassignedCustomers(group, false);
        customerModel.setWrappedData(unassignedGroupCustomerList);
        model.addAttribute("assignedCustomers", assignedGroupCustomerList);
        model.addAttribute("availableCustomers", unassignedGroupCustomerList);
        return "dr/cc/groupDetail.jsp";
    }
    
    @RequestMapping("/cc/groupCreate")
    public String groupCreate (ModelMap model,
            YukonUserContext userContext) {
        
        currentGroup = groupService.createNewGroup(userContext.getYukonUser());
        assignedGroupCustomerList = new ArrayList<GroupCustomerNotif>();
        groupCustomerModel.setWrappedData(assignedGroupCustomerList);
        unassignedGroupCustomerList = groupService.getUnassignedCustomers(currentGroup, true);
        Collections.sort(unassignedGroupCustomerList, new CustomerListComparator());
        customerModel.setWrappedData(unassignedGroupCustomerList);
        model.addAttribute("group", currentGroup);
        model.addAttribute("assignedCustomers", assignedGroupCustomerList);
        model.addAttribute("availableCustomers", unassignedGroupCustomerList);
        return "dr/cc/groupDetail.jsp";
    }

    @RequestMapping("/cc/groupSave/{groupId}")
    public String groupSave (ModelMap model,
            YukonUserContext userContext,
            @PathVariable int groupId,
            String[] emails,
            String[] voice,
            String[] sms) {
        
        // TODO: implement saving of notification types (emails, voice, sms)
        // See API for assignedGroupCustomerList.get(0).getNotifMap();
        // the following actually works correctly
        Group group = groupService.getGroup(groupId);
        groupService.saveGroup(group, assignedGroupCustomerList);
        return "redirect:/dr/cc/groupList";
    }

    @RequestMapping("/cc/programList")
    public String programList (ModelMap model,
            YukonUserContext userContext) {
        
        List<ProgramType> programTypeList = programService.getProgramTypeList(userContext.getYukonUser());
        ArrayList<Program> allPrograms = new ArrayList<Program>();
        Map<String, List<Program>> programMap = new HashMap<>();
        Program nameLess = null;
        for (ProgramType programType : programTypeList) {
            List<Program> programs = programService.getProgramList(programType);
            for (Program program : programs) {
                String programName = program.getName();
                if (StringUtils.isEmpty(programName)) {
                    program.setName("BOGUS");
                    nameLess = program;
                }
                allPrograms.add(program);
            }
            programMap.put(programType.getName(), programs);
        }
        if (nameLess != null) {
            programService.deleteProgram(nameLess);
        }
        model.addAttribute("programMap", programMap);
        return "/dr/cc/programList.jsp";
    }

    @RequestMapping("/cc/programDetail/{programId}")
    public String programDetail (ModelMap model,
            YukonUserContext userContext,
            @PathVariable int programId) {
        
        Program program = programService.getProgram(programId);
        updateData(program);
        model.addAttribute("program", program);
        CICurtailmentStrategy strategy = strategyFactory.getStrategy(program);
        programParameters = strategy.getParameters(program);
        model.addAttribute("programParameters", programParameters);
        Set<Group> unassignedProgramGroups = programService.getUnassignedGroups(program);
        
        assignedNotificationGroups = new ArrayList<LiteNotificationGroup>(programService.getAssignedNotificationGroups(program));
        Collections.sort(assignedNotificationGroups, LiteComparators.liteNameComparator);
        List<AvailableProgramGroup> assignedProgramGroups = programService.getAvailableProgramGroups(program);
        Set<LiteNotificationGroup> allNotificationGroups = notificationGroupDao.getAllNotificationGroups();
        allNotificationGroups.removeAll(assignedNotificationGroups);
        unassignedNotificationGroups = new ArrayList<LiteNotificationGroup>(allNotificationGroups);
        
        Collections.sort(unassignedNotificationGroups, LiteComparators.liteNameComparator);
        model.addAttribute("unassignedProgramGroups", unassignedProgramGroups);
        model.addAttribute("assignedNotificationGroups", assignedNotificationGroups);
        model.addAttribute("unassignedNotificationGroups", unassignedNotificationGroups);
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
        
        assignedGroupModel = new ListDataModel();
        unassignedGroupModel = new ListDataModel();
        Program program = new Program();
        program.setName(name);
        programParameters = Collections.emptyList();
        assignedGroups = Collections.emptyList();
        assignedNotificationGroups = Collections.emptyList();
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
        Set<LiteNotificationGroup> assignedNotifGroupsSet = new HashSet<LiteNotificationGroup>(assignedNotificationGroups);
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
            Integer CUSTOMER_ELECTION_CUTOFF_MINUTES
            ) {
        
        Program program = programService.getProgram(programId);
        program.setName(programName);
        program.setIdentifierPrefix(programIdentifierPrefix);
        program.setLastIdentifier(programLastIdentifier);
        Set<LiteNotificationGroup> assignedNotifGroupsSet = new HashSet<LiteNotificationGroup>(assignedNotificationGroups);
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
        programService.saveProgram(program, programParameters, assignedGroups, assignedNotifGroupsSet);
        return "redirect:/dr/cc/programList";
    }

    private void setParameter (ProgramParameter parameter, Integer parameterInput) {
        if (parameterInput != null) {
            parameter.setParameterValue(parameterInput.toString());
        }
    }
    
    private void setParameter (ProgramParameter parameter, Double parameterInput) {
        if (parameterInput != null) {
            parameter.setParameterValue(parameterInput.toString());
        }
    }
    
    @RequestMapping(value="/cc/program/{programId}/assignGroup/{groupId}", method=RequestMethod.POST)
    @ResponseBody
    public String assignGroup (LiteYukonUser user,
            @PathVariable int programId,
            @PathVariable int groupId) {

        Group toAdd = getGroupById(unassignedGroupModel, groupId);
        unassignedGroups.remove(toAdd);
        assignedGroups.add(toAdd);
        return null;
    }
    
    @RequestMapping(value="/cc/program/{programId}/unassignGroup/{groupId}", method=RequestMethod.POST)
    @ResponseBody
    public String unassignGroup (LiteYukonUser user,
            @PathVariable int programId,
            @PathVariable int groupId) {

        Group toDelete = getGroupById(assignedGroupModel, groupId);
        assignedGroups.remove(toDelete);
        unassignedGroups.add(toDelete);
        return null;
    }

    @RequestMapping(value="/cc/program/{programId}/assignNotificationGroup/{groupId}", method=RequestMethod.POST)
    @ResponseBody
    public String assignNotificationGroup (LiteYukonUser user,
            @PathVariable int programId,
            @PathVariable int groupId) {

        LiteNotificationGroup toassign = notificationGroupDao.getLiteNotificationGroup(groupId);
        unassignedNotificationGroups.remove(toassign);
        assignedNotificationGroups.add(toassign);
        return null;
    }

    @RequestMapping(value="/cc/program/{programId}/unassignNotificationGroup/{groupId}", method=RequestMethod.POST)
    @ResponseBody
    public String unassignNotificationGroup (LiteYukonUser user,
            @PathVariable int programId,
            @PathVariable int groupId) {

        LiteNotificationGroup tounassign = notificationGroupDao.getLiteNotificationGroup(groupId);
        assignedNotificationGroups.remove(tounassign);
        unassignedNotificationGroups.add(tounassign);
        return null;
    }

    @RequestMapping(value="/cc/group/{groupId}/assignCustomer/{customerId}", method=RequestMethod.POST)
    @ResponseBody
    public String assignCustomer (LiteYukonUser user,
            @PathVariable int groupId,
            @PathVariable int customerId) {

        GroupCustomerNotif customerNotif = getCustomerNotifById(customerModel, customerId);
        unassignedGroupCustomerList.remove(customerNotif);
        assignedGroupCustomerList.add(customerNotif);
        return null;
    }
    
    @RequestMapping(value="/cc/group/{groupId}/unassignCustomer/{customerId}", method=RequestMethod.POST)
    @ResponseBody
    public String unassignCustomer (LiteYukonUser user,
            @PathVariable int groupId,
            @PathVariable int customerId) {

        GroupCustomerNotif customerNotif = getCustomerNotifById(groupCustomerModel, customerId);
        assignedGroupCustomerList.remove(customerNotif);
        unassignedGroupCustomerList.add(customerNotif);
        return null;
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
    
    @RequestMapping("/cc/program/{programId}/event/{eventId}/revision/{revision}")
    public String revision(ModelMap model,
            YukonUserContext userContext,
            @PathVariable int programId,
            @PathVariable int eventId,
            @PathVariable int revision,
            HttpServletRequest request) {
        return economicDetail(model, userContext, 0, programId, eventId, revision, request);
    }
    
    public String economicDetail(ModelMap model,
            YukonUserContext userContext,
            Integer programTypeId,
            int programId,
            int eventId,
            int revisionNumber,
            HttpServletRequest request) {
        
        EconomicEvent event = economicEventDao.getForId(eventId);
        participantList = economicEventParticipantDao.getForEvent(event);
        participantDataModel = new ListDataModel(participantList);
        currentRevision = event.getLatestRevision();
        strategy = (EconomicStrategy) strategyFactory.getStrategy(event.getProgram());
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
        EconomicEventPricing curEconEventPricingRevision =
            revisionNumber == -1 ? getCurrentRevision() : getRevisionAt(revisions, revisionNumber);
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
            pricingData.add(getAckForRow(pRowData));
            String imgPath = request.getContextPath() + getNotifForRow(pRowData);
            pricingData.add(imgPath);
            tableData.add(pricingData);

            EconomicEventParticipantSelectionWindow selection;
            for (columnIndex = 0; columnIndex < countRevisions; columnIndex += 1) {
                    selection = economicService.getCustomerSelectionWindow(getCurrentRevision(),pRowData,columnIndex);
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
    
    protected void updateData(Program program) {
        List<AvailableProgramGroup> availableProgramGroups = 
            programService.getAvailableProgramGroups(program);
        assignedGroups = new ArrayList<Group>(availableProgramGroups.size());
        for (AvailableProgramGroup apg : availableProgramGroups) {
            assignedGroups.add(apg.getGroup());
        }
        assignedGroupModel.setWrappedData(assignedGroups);

        Set<Group> allGroups = programService.getUnassignedGroups(program);
        unassignedGroups = new ArrayList<Group>(allGroups);
        unassignedGroupModel.setWrappedData(unassignedGroups);
        
        assignedNotificationGroups = 
            new ArrayList<LiteNotificationGroup>(programService.getAssignedNotificationGroups(program));
        Collections.sort(assignedNotificationGroups, LiteComparators.liteNameComparator);

        Set<LiteNotificationGroup> allNotificationGroups = notificationGroupDao.getAllNotificationGroups();
        allNotificationGroups.removeAll(assignedNotificationGroups);
        unassignedNotificationGroups = new ArrayList<LiteNotificationGroup>(allNotificationGroups);
        Collections.sort(unassignedNotificationGroups, LiteComparators.liteNameComparator);

        CICurtailmentStrategy strategy = strategyFactory.getStrategy(program);
        programParameters = strategy.getParameters(program);
        
        programDeletable = !programService.isEventsExistForProgram(program);
    }

    EconomicEventPricing getRevisionAt(Map<Integer, EconomicEventPricing> revisions, int revisionKey) {
        Set<Entry<Integer, EconomicEventPricing>> revisionSet = revisions.entrySet();
        
        //EconomicEventPricing revision = revisions.get(key)
        for (Entry<Integer, EconomicEventPricing> revision : revisionSet) {
            if (revision.getKey() == revisionKey) {
                return revision.getValue();
            }
        }
        return null;
    }
    
    public final class CustomerListComparator implements Comparator<GroupCustomerNotif> {
        @Override
        public int compare(GroupCustomerNotif o1, GroupCustomerNotif o2) {
            return o1.getCustomer().compareTo(o2.getCustomer());
        }
    }

    // TODO: determine if this will ever be needed
    private BaseEvent lookupEventById(Program program, int id) {
        CICurtailmentStrategy strategy = strategyFactory.getStrategy(program);
        List<? extends BaseEvent> eventList = strategy.getEventsForProgram(program);
        for (BaseEvent event : eventList) {
            if (event.getId() == id) {
                return event;
            }
        }
        return null;
    }
    
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
    
    private String getAckForRow(EconomicEventParticipant rowData) {
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
    
    private String getNotifForRow(EconomicEventParticipant participant) {
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
    public boolean isConsideredActive(BaseEvent event) {
        
        CICurtailmentStrategy strategy = strategyFactory.getStrategy(event.getProgram());
        
        return strategy.isConsideredActive(event);
    }

    public class CurrentEventPredicate implements Predicate<BaseEvent> {
        private Date now;
        public CurrentEventPredicate() {
            now = new Date();
        }
        @Override
        public boolean evaluate(BaseEvent event) {
            
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
    
    public class PendingEventPredicate implements Predicate<BaseEvent> {
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
    
    public class RecentEventPredicate implements Predicate<BaseEvent> {
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
    
    public DataModel getWindowModel(EconomicEvent economicEvent) {
        if (windowDataModel == null) {
            Collection<EconomicEventPricingWindow> values = economicEvent.getInitialRevision().getWindows().values();
            List<EconomicEventPricingWindow> windows = new ArrayList<EconomicEventPricingWindow>(values);
            Collections.sort(windows);
            windowDataModel = new ListDataModel(windows);
        }
        return windowDataModel;
    }

//    public DataModel getWindowModel() {
//        if (windowDataModel == null) {
//            Collection<EconomicEventPricingWindow> values = event.getInitialRevision().getWindows().values();
//            List<EconomicEventPricingWindow> windows = new ArrayList<EconomicEventPricingWindow>(values);
//            Collections.sort(windows);
//            windowDataModel = new ListDataModel(windows);
//        }
//        return windowDataModel;
//    }


    public BigDecimal getColumnPrice(EconomicEvent economicEvent) {
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
    
//    public BigDecimal getColumnPrice() {
//        DataModel columnModel = getWindowModel();
//        if (columnModel.isRowAvailable()) { // read: isColumnAvailable()
//            EconomicEventPricingWindow pricingWindow = 
//                (EconomicEventPricingWindow) columnModel.getRowData(); // read: getColumnData()
//            getCurrentRevision();
//            if (pricingWindow != null) {
//                EconomicEventPricing revision = getCurrentRevision();
//                int offset = pricingWindow.getOffset();
//                try {
//                EconomicEventPricingWindow window = 
//                    economicService.getFallThroughWindow(revision, 
//                                                         offset);
//                return window.getEnergyPrice();
//                } catch (IllegalArgumentException except) {
//                    
//                }
//            }
//        }
//        return null;
//    }

    public BigDecimal getColumnValue(DataModel rowModel, EconomicEvent event) {
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
    
//    public BigDecimal getColumnValue() {
//        DataModel rowModel = getParticipantModel();
//        if (rowModel.isRowAvailable()) {
//            EconomicEventParticipant row = 
//                (EconomicEventParticipant) rowModel.getRowData();
//            DataModel columnModel = getWindowModel();
//            if (columnModel.isRowAvailable()) { // read: isColumnAvailable()
//                EconomicEventPricingWindow pricingWindow = 
//                    (EconomicEventPricingWindow) columnModel.getRowData(); // read: getColumnData()
//                Integer column = pricingWindow.getOffset();
//                try {
//                    EconomicEventParticipantSelectionWindow selection = 
//                        economicService.getCustomerSelectionWindow(getCurrentRevision(),row,column);
//                    return selection.getEnergyToBuy();
//                } catch (IllegalArgumentException except) {
//                    
//                }
//            }
//        }
//        return null;
//    }

//    public BigDecimal getColumnTotal() {
//        DataModel columnModel = getWindowModel();
//        if (columnModel.isRowAvailable()) { // read: isColumnAvailable()
//            EconomicEventPricingWindow pricingWindow = 
//                (EconomicEventPricingWindow) columnModel.getRowData(); // read: getColumnData()
//            long total = 0;
//            try {
//                for (EconomicEventParticipant participant : participantList) {
//                    EconomicEventParticipantSelection selection = participant.getSelection(event.getLatestRevision());
//                    EconomicEventParticipantSelectionWindow selectionWindow = 
//                        economicService.getFallThroughWindowSelection(selection, pricingWindow.getOffset());
//                    total += selectionWindow.getEnergyToBuy().longValue();
//                }
//                return BigDecimal.valueOf(total);
//            } catch (IllegalArgumentException except) {
//                
//            }
//        }
//        return null;
//    }
    
    public ListDataModel getOtherRevisionsModel(EconomicEvent economicEvent) {
        if (otherRevisionsModel == null) {
            ArrayList<EconomicEventPricing> others = 
                new ArrayList<EconomicEventPricing>(economicEvent.getRevisions().values());
            others.remove(economicEvent.getLatestRevision());
            otherRevisionsModel = new ListDataModel(others);
        }
        return otherRevisionsModel;
    }
    
//    public ListDataModel getOtherRevisionsModel() {
//        if (otherRevisionsModel == null) {
//            ArrayList<EconomicEventPricing> others = 
//                new ArrayList<EconomicEventPricing>(event.getRevisions().values());
//            others.remove(getCurrentRevision());
//            otherRevisionsModel = new ListDataModel(others);
//        }
//        return otherRevisionsModel;
//    }
    
    public EconomicEventPricing getCurrentRevision() {
        return currentRevision;
    }

    public DataModel getParticipantModel() {
        return participantDataModel;
    }

}
