package com.cannontech.web.dr.cc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
import com.cannontech.cc.model.GroupBean;
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
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.predicate.Predicate;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.GraphDao;
import com.cannontech.core.dao.NotificationGroupDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.customer.CICustomerPointType;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.cc.EventDetailHelper;
import com.cannontech.web.cc.EventListBean;
import com.cannontech.web.cc.methods.DetailAccountingBean;
import com.cannontech.web.cc.methods.DetailEconomicBean;
import com.cannontech.web.cc.methods.DetailNotificationBean;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.dr.cc.CustomerModel.ProgramPaoModel;
import com.cannontech.web.tools.trends.TrendUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
public class CcHomeController {
    private static Logger log = YukonLogManager.getLogger(CcHomeController.class);
    private static String eventHeadingBase = "yukon.web.modules.commercialcurtailment.ccurtSetup.ccurtEvent_heading_";
    private static String companyHeadingBase = "yukon.web.modules.commercialcurtailment.ccurtSetup.";
    
    @Autowired private AccountingEventDao accountingEventDao;
    @Autowired private BaseEventDao baseEventDao;
    @Autowired private CurtailmentEventDao curtailmentEventDao;
    @Autowired private CurtailmentEventNotifDao curtailmentNotifDao;
    @Autowired private CustomerLMProgramService customerLMProgramService;
    @Autowired private CustomerPointService customerPointService;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private DetailEconomicBean detailEconomicBean;
    @Autowired private DetailAccountingBean detailAccountingBean;
    @Autowired private DetailNotificationBean detailNotificationBean;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private EconomicEventDao economicEventDao;
    @Autowired private EconomicEventParticipantDao economicEventParticipantDao;
    @Autowired private EconomicService economicService;
    @Autowired private EventDetailHelper eventDetailHelper;
    @Autowired private EventListBean eventListBean;
    @Autowired private EventService eventService;
    @Autowired private GraphDao graphDao;
    @Autowired private GroupBeanValidator groupBeanValidator;
    @Autowired private GroupDao groupDao;
    @Autowired private GroupService groupService;
    @Autowired private NotificationGroupDao notificationGroupDao;
    @Autowired private PaoDao paoDao;
    @Autowired private ProgramService programService;
    @Autowired private StrategyFactory strategyFactory;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @RequestMapping("/cc/home")
    public String home(ModelMap model, YukonUserContext userContext, Integer trendId) throws JsonProcessingException {
        
        EnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(userContext.getYukonUser());
        
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
        List<ProgramType> programTypeList = programService.getProgramTypeList(userContext.getYukonUser());
        
        //Retrieve programs by type
        List<Program> allPrograms = new ArrayList<>();
        for (ProgramType programType : programTypeList) {
            List<Program> programs = programService.getProgramList(programType);
            for (Program program : programs) {
                allPrograms.add(program);
            }
        }
        model.addAttribute("programs", allPrograms);
        
        //Set up trends
        List<LiteGraphDefinition> trends = graphDao.getGraphDefinitions();
        model.addAttribute("trends", trends);
        
        if (trendId != null) {
            LiteGraphDefinition trend = graphDao.getLiteGraphDefinition(trendId);
            model.addAttribute("trendId", trendId);
            model.addAttribute("trendName", trend.getName());
            model.addAttribute("showTrends", true);
        } else if (!trends.isEmpty()) {
            model.addAttribute("trendId", trends.get(0).getGraphDefinitionID());
            model.addAttribute("trendName", trends.get(0).getName());
        }
        
        model.addAttribute("labels", TrendUtils.getLabels(userContext, messageSourceResolver));
        
        return "dr/cc/home.jsp";
    }
    
    @RequestMapping("/cc/program/{id}/init")
    public String init(ModelMap model, YukonUserContext userContext, @PathVariable int id) {
        
        Program program = programService.getProgramById(id);
        //CICurtailmentStrategy strategy = strategyFactory.getStrategy(program);
        
        model.addAttribute("program", program);
        // TODO: this is the initialization of the wizard invoked by the "Start" link.
        // See how the legacy app looks and works.
        //List<? extends BaseEvent> eventList = strategy.getEventsForProgram(program);
        
        return "dr/cc/init.jsp";
    }

    @RequestMapping("/cc/program/{programId}/history")
    public String history(ModelMap model, @PathVariable int programId) {
        
        Program program = programService.getProgramById(programId);
        model.addAttribute("program", program);
        
        CICurtailmentStrategy strategy = strategyFactory.getStrategy(program);
        List<? extends BaseEvent> eventList = strategy.getEventsForProgram(program);
        Collections.reverse(eventList);
        model.addAttribute("eventHistory", eventList);
        
        return "dr/cc/history.jsp";
    }
    
    @RequestMapping(value="/cc/program/{programId}/event/{eventId}", method=RequestMethod.DELETE)
    public String deleteEvent(ModelMap model,
                              @PathVariable int programId,
                              @PathVariable int eventId,
                              FlashScope flashScope) {
        
        Program program = programService.getProgramById(programId);
        model.addAttribute("program", program);
        
        CICurtailmentStrategy strategy = strategyFactory.getStrategy(program);
        List<? extends BaseEvent> eventList = strategy.getEventsForProgram(program);
        Collections.reverse(eventList);
        
        boolean success = false;
        BaseEvent deletedEvent = null;
        for (BaseEvent event : eventList) {
            if (event.getId() == eventId) {
                strategy.forceDelete(event);
                deletedEvent = event;
                success = true;
                break;
            }
        }
        
        if (success) {
            eventList.remove(deletedEvent);
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.history.eventDelete.success"));
        } else {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.history.eventDelete.failure"));
        }
        
        model.addAttribute("eventHistory", eventList);
        
        return "dr/cc/history.jsp";
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
    public String groupDetail(ModelMap model, @PathVariable int groupId, @ModelAttribute("group") GroupBean groupBean) {
        
        Group group = groupService.getGroup(groupId);
        List<GroupCustomerNotif> assignedGroupCustomerList = groupService.getAssignedCustomers(group);
        List<GroupCustomerNotif> unassignedGroupCustomerList = groupService.getUnassignedCustomers(group, false);
        
        groupBean = getGroupBean(group, assignedGroupCustomerList, unassignedGroupCustomerList);
        model.addAttribute("group", groupBean);
        
        return "dr/cc/groupDetail.jsp";
    }
    
    private GroupBean getGroupBean(Group group, List<GroupCustomerNotif> assigned, List<GroupCustomerNotif> unassigned) {
        GroupBean groupBean = new GroupBean();
        groupBean.setId(group.getId());
        groupBean.setName(group.getName());
        
        List<GroupBean.Customer> assignedCustomers = new ArrayList<>();
        for (GroupCustomerNotif assignedNotif : assigned) {
            GroupBean.Customer customer = GroupBean.Customer.of(assignedNotif);
            assignedCustomers.add(customer);
        }
        groupBean.setAssignedCustomers(assignedCustomers);
        
        List<GroupBean.Customer> unassignedCustomers = new ArrayList<>();
        for (GroupCustomerNotif unassignedNotif : unassigned) {
            GroupBean.Customer customer = GroupBean.Customer.of(unassignedNotif);
            unassignedCustomers.add(customer);
        }
        groupBean.setAvailableCustomers(unassignedCustomers);
        
        return groupBean;
    }
    
    @RequestMapping("/cc/groupCreate")
    public String groupCreate(ModelMap model, LiteYukonUser user, @ModelAttribute("group") GroupBean groupBean) {
        
        Group newGroup = groupService.createNewGroup(user);        
        List<GroupCustomerNotif> assignedGroupCustomerList = new ArrayList<>();
        List<GroupCustomerNotif> unassignedGroupCustomerList = groupService.getUnassignedCustomers(newGroup, true);
        
        groupBean = getGroupBean(newGroup, assignedGroupCustomerList, unassignedGroupCustomerList);
        model.addAttribute("group", groupBean);
        
        return "dr/cc/groupDetail.jsp";
    }
    
    @RequestMapping("/cc/groupDelete/{groupId}")
    public String groupDelete(@PathVariable int groupId, FlashScope flash) {
        
        Group group = groupService.getGroup(groupId);
        groupService.deleteGroup(group);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.groupDelete.deleteSuccessful"));
        
        return "redirect:/dr/cc/groupList";
    }
    
    @RequestMapping("/cc/groupSave")
    public String groupSave(ModelMap model, 
                            YukonUserContext userContext,
                            @ModelAttribute("group") GroupBean groupBean,
                            BindingResult bindingResult,
                            FlashScope flash) {
        
        groupBeanValidator.validate(groupBean, bindingResult);
        if (bindingResult.hasErrors()) {
            return "dr/cc/groupDetail.jsp";
        }
        
        Group group;
        if (groupBean.getId() == null) {
            group = groupService.createNewGroup(userContext.getYukonUser());
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.groupCreate.createSuccessful"));
        } else {
            group = groupService.getGroup(groupBean.getId());
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.groupDetail.updateSuccessful"));
        }
        group.setName(groupBean.getName());
        
        List<GroupCustomerNotif> assignedGroupCustomerNotifs = getCustomerNotificationSettings(group, groupBean);
        
        groupService.saveGroup(group, assignedGroupCustomerNotifs);
        return "redirect:/dr/cc/groupList";
    }
    
    private List<GroupCustomerNotif> getCustomerNotificationSettings(Group group, GroupBean groupBean) {
        //Get list of all customers - available and unassigned
        List<GroupCustomerNotif> allCustomers = groupService.getAssignedCustomers(group);
        allCustomers.addAll(groupService.getUnassignedCustomers(group, false));
        
        //Build a list of assigned customer notifications, and update settings
        List<GroupCustomerNotif> assignedGroupCustomerNotifs = new ArrayList<>();
        
        if (groupBean.getAssignedCustomers() != null) {
            for (GroupBean.Customer customer : groupBean.getAssignedCustomers()) {
                int customerId = customer.getId();
                for (GroupCustomerNotif groupCustomerNotif : allCustomers) {
                    if (groupCustomerNotif.getCustomer().getId() == customerId) {
                        //update notif values
                        boolean emails = customer.isEmails();
                        boolean voice = customer.isVoice();
                        boolean sms = customer.isSms();
                        groupCustomerNotif.getNotifMap().setSendEmails(emails);
                        groupCustomerNotif.getNotifMap().setSendOutboundCalls(voice);
                        groupCustomerNotif.getNotifMap().setSendSms(sms);
                        //add to list
                        assignedGroupCustomerNotifs.add(groupCustomerNotif);
                    }
                }
            }
        }
        return assignedGroupCustomerNotifs;
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
        
        Program program = programService.getProgramById(programId);
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
        
        Set<Group> assignedProgramGroups = programService.getAssignedGroups(program);
        model.addAttribute("assignedProgramGroups", assignedProgramGroups);
        
        model.addAttribute("deletable", !programService.isEventsExistForProgram(program));
        
        return "dr/cc/programDetail.jsp";
    }
    
    @RequestMapping("/cc/programCreate")
    public String programCreate (ModelMap model, LiteYukonUser user) {
        
        List<ProgramType> programTypeList = programService.getProgramTypeList(user);
        model.addAttribute("programTypes", programTypeList);
        return "dr/cc/programCreate.jsp";
    }

    @RequestMapping("/cc/programDetailCreate/{programTypeId}/{name}")
    public String programDetailCreate(ModelMap model, 
                                      LiteYukonUser user, 
                                      @PathVariable int programTypeId,
                                      @PathVariable String name, 
                                      FlashScope flash) {
        
        Program program = new Program();
        program.setName(name);
        program.setIdentifierPrefix("EVENT-");
        program.setLastIdentifier(0);
        
        ProgramType newProgramType = null;
        List<ProgramType> programTypeList = programService.getProgramTypeList(user);
        for (ProgramType programType : programTypeList) {
            if (programType.getId() == programTypeId) {
                newProgramType = programType;
                break;
            }
        }
        program.setProgramType(newProgramType);
        
        List<ProgramParameter> programParameters = new ArrayList<>();
        List<Group> assignedGroups = new ArrayList<>();
        Set<LiteNotificationGroup> assignedNotifGroupsSet = new HashSet<>();
        
        programService.saveProgram(program, programParameters, assignedGroups, assignedNotifGroupsSet);
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.programCreate.createSuccessful"));
        
        return "redirect:/dr/cc/programDetail/" + program.getId();
    }
    
    @RequestMapping("/cc/programDelete/{programId}")
    public String programDelete (ModelMap model, @PathVariable int programId, FlashScope flash) {
        
        Program program = programService.getProgramById(programId);
        programService.deleteProgram(program);
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.programDetail.deleteSuccessful"));
        
        return "redirect:/dr/cc/programList";
    }
    
    @RequestMapping("/cc/programSave/{programId}")
    public String programSave (ModelMap model,
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
                               @RequestParam(value="assignedGroup", required=false) List<Integer> assignedGroupIds,
                               @RequestParam(value="assignedNotifGroup", required=false) List<Integer> assignedNotifGroupIds,
                               @RequestParam(value="unassignedGroup", required=false) List<Integer> unassignedGroupIds,
                               @RequestParam(value="unassignedNotifGroup", required=false) List<Integer> unassignedNotifGroupIds,
                               FlashScope flash) {
                
        Program program = programService.getProgramById(programId);
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
        
        Set<LiteNotificationGroup> assignedNotificationGroups = new HashSet<>();
        Set<LiteNotificationGroup> unassignedNotificationGroups = new HashSet<>();
        Set<LiteNotificationGroup> allNotificationGroups = notificationGroupDao.getAllNotificationGroups();
        for (LiteNotificationGroup notifGroup : allNotificationGroups) {
            if (assignedNotifGroupIds != null && assignedNotifGroupIds.contains(notifGroup.getNotificationGroupID())) {
                assignedNotificationGroups.add(notifGroup);
            } else {
                unassignedNotificationGroups.add(notifGroup);
            }
        }
        
        List<Group> assignedGroups;
        if (assignedGroupIds != null) {
            assignedGroups = groupDao.getForIds(assignedGroupIds);
        } else {
            assignedGroups = new ArrayList<>();
        }
        
        //Validation
        DataBinder binder = new DataBinder(new ProgramFields(programName, programIdentifierPrefix));
        BindingResult bindingResult = binder.getBindingResult();
        YukonValidationUtils.checkIsBlankOrExceedsMaxLength(bindingResult, "programName", programName, false, 255);
        YukonValidationUtils.checkIsBlankOrExceedsMaxLength(bindingResult, "programIdentifierPrefix", programIdentifierPrefix, false, 32);
        FieldError nameError = bindingResult.getFieldError("programName");
        FieldError prefixError = bindingResult.getFieldError("programIdentifierPrefix");
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("nameError", nameError == null ? null : nameError.getCode());
            model.addAttribute("prefixError", prefixError == null ? null : prefixError.getCode());
            model.addAttribute("program", program);
            model.addAttribute("programParameters", programParameters);
            model.addAttribute("assignedProgramGroups", assignedGroups);
            Set<Group> unassignedGroups = programService.getUnassignedGroups(program);
            model.addAttribute("unassignedProgramGroups", unassignedGroups);
            model.addAttribute("assignedNotificationGroups", assignedNotificationGroups);
            model.addAttribute("unassignedNotificationGroups", unassignedNotificationGroups);
            model.addAttribute("deletable", !programService.isEventsExistForProgram(program));
            
            return "/dr/cc/programDetail.jsp";
        }
        
        programService.saveProgram(program, programParameters, assignedGroups, assignedNotificationGroups);
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.programDetail.updateSuccessful"));
        
        return "redirect:/dr/cc/programList";
    }
    
    private void setParameter(ProgramParameter parameter, Number parameterInput) {
        if (parameterInput != null) {
            parameter.setParameterValue(parameterInput.toString());
        }
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
        
        Integer programTypeId = programService.getProgramById(programId).getProgramType().getId();
        switch(programTypeId) {
        case 1:
        case 2:
            return capacityDetail(model, userContext, programTypeId, eventId);
        case 3:
            return accountingDetail(model, userContext, programTypeId, eventId);
        case 4:
            return economicDetail(model, userContext, programTypeId, programId, eventId, -1, request);
        default:
            throw new IllegalArgumentException("Invalid program type id: " + programTypeId);
        }
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
                              
        CurtailmentEvent curtailmentEvent = curtailmentEventDao.getForId(eventId);
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
    
    private String economicDetail(ModelMap model,
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
        Program program = programService.getProgramById(programId);
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
    public String customerDetail(ModelMap model, 
                                 @PathVariable int customerId, 
                                 @ModelAttribute CustomerModel customerModel) {
        
        CICustomerStub customer = customerPointService.getCustomer(customerId);
        model.addAttribute("customer", customer);
        
        List<String> satisfiedPointGroups = customerPointService.getSatisfiedPointGroups(customer);
        model.addAttribute("satisfiedPointGroups", satisfiedPointGroups);
        
        List<CICustomerPointType> pointTypes = customerPointService.getPointTypeList(customer);
        Map<CICustomerPointType, BigDecimal> pointValues = customerPointService.getPointValueCache(customer);
        List<LiteYukonPAObject> activePrograms =
            new ArrayList<>(customerLMProgramService.getProgramsForCustomer(customer));
        List<LiteYukonPAObject> availablePrograms =
            new ArrayList<>(customerLMProgramService.getAvailableProgramsForCustomer(customer));
        
        customerModel.setPointTypes(pointTypes);
        customerModel.setPointValues(pointValues);
        customerModel.populateActivePrograms(activePrograms);
        customerModel.populateAvailablePrograms(availablePrograms);
        
        return "dr/cc/customerDetail.jsp";
    }
    
    @RequestMapping("/cc/customerSave/{customerId}")
    @Transactional
    public String customerSave(@PathVariable int customerId,
                               @ModelAttribute CustomerModel customerModel, 
                               BindingResult bindingResult,
                               FlashScope flash) {
        
        CICustomerStub customer = customerPointService.getCustomer(customerId);
        customerPointService.savePointValues(customer, customerModel.getPointValues());
        
        List<Integer> activeProgramIds = new ArrayList<>();
        for(ProgramPaoModel model : customerModel.getActivePrograms()) {
            activeProgramIds.add(model.getPaoId());
        }
        List<LiteYukonPAObject> activeProgramPaos = paoDao.getLiteYukonPaos(activeProgramIds);
        
        customerLMProgramService.saveProgramList(customer, activeProgramPaos);
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.customerDetail.update.success", 
                                                          customer.getCompanyName()));
        return "redirect:/dr/cc/customerList";
    }
    
    @RequestMapping("/cc/customerDetail/{customerId}/createPoint/{pointType}")
    public String customerCreatePoint(ModelMap model,
                                      @PathVariable int customerId, 
                                      @PathVariable CICustomerPointType pointType,
                                      FlashScope flash) {
        
        CICustomerStub customer = customerPointService.getCustomer(customerId);
        
        try {
            customerPointService.createPoint(customer, pointType);
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.customerDetail.createPoint.success", 
                                                          pointType.getLabel()));
        } catch (Exception e) {
            log.error("Error creating point \"" + pointType + "\" for curtailment customer " + customer.getCompanyName(), e);
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.customerDetail.createPoint.failure",
                                                            pointType.getLabel()));
        }
        
        return "redirect:/dr/cc/customerDetail/" + customerId;        
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
    
    @SuppressWarnings("unused")
    private class ProgramFields {
        private String programName;
        private String programIdentifierPrefix;
        public ProgramFields(String programName, String identifierPrefix) {
            this.programName = programName;
            programIdentifierPrefix = identifierPrefix;
        }
        public String getProgramName() {
            return programName;
        }
        public void setProgramName(String programName) {
            this.programName = programName;
        }
        public String getProgramIdentifierPrefix() {
            return programIdentifierPrefix;
        }
        public void setProgramIdentifierPrefix(String identifierPrefix) {
            programIdentifierPrefix = identifierPrefix;
        }
    }
}