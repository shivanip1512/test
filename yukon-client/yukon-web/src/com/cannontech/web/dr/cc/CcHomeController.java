package com.cannontech.web.dr.cc;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
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
import com.cannontech.cc.dao.GroupCustomerNotifDao;
import com.cannontech.cc.dao.GroupDao;
import com.cannontech.cc.dao.ProgramParameterDao;
import com.cannontech.cc.model.AccountingEvent;
import com.cannontech.cc.model.AccountingEventParticipant;
import com.cannontech.cc.model.AvailableProgramGroup;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.CiEventType;
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
import com.cannontech.cc.model.ProgramParameterKey;
import com.cannontech.cc.model.ProgramType;
import com.cannontech.cc.service.AccountingStrategy;
import com.cannontech.cc.service.CICurtailmentStrategy;
import com.cannontech.cc.service.CustomerLMProgramService;
import com.cannontech.cc.service.CustomerPointService;
import com.cannontech.cc.service.EconomicService;
import com.cannontech.cc.service.EconomicStrategy;
import com.cannontech.cc.service.GroupService;
import com.cannontech.cc.service.NotificationStatus;
import com.cannontech.cc.service.NotificationStrategy;
import com.cannontech.cc.service.ProgramService;
import com.cannontech.cc.service.StrategyFactory;
import com.cannontech.cc.service.exception.EventCreationException;
import com.cannontech.cc.service.exception.EventModificationException;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.NotificationGroupDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.customer.CICustomerPointType;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.dr.cc.model.CiEventStatus;
import com.cannontech.web.dr.cc.model.CiInitEventModel;
import com.cannontech.web.dr.cc.model.CustomerModel;
import com.cannontech.web.dr.cc.model.Exclusion;
import com.cannontech.web.dr.cc.service.CiCurtailmentService;
import com.cannontech.web.dr.cc.service.CiCustomerVerificationService;
import com.cannontech.web.dr.cc.service.CiEventCreationService;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yukon.IDatabaseCache;

//TODO JAVA 8 - Replace Filters and Functions with lambdas
@Controller
@CheckRoleProperty(YukonRoleProperty.CURTAILMENT_IS_OPERATOR)
public class CcHomeController {
    private static Logger log = YukonLogManager.getLogger(CcHomeController.class);
    private static String eventHeadingBase = "yukon.web.modules.commercialcurtailment.ccurtSetup.ccurtEvent_heading_";
    private static String companyHeadingBase = "yukon.web.modules.commercialcurtailment.ccurtSetup.";
    
    @Autowired private AccountingEventDao accountingEventDao;
    @Autowired private BaseEventDao baseEventDao;
    @Autowired private CcTrendHelper trendHelper;
    @Autowired private CiEventCreationService ciEventCreationService;
    @Autowired private CiInitEventModelValidator eventModelValidator;
    @Autowired private CiCurtailmentService ciCurtailmentService;
    @Autowired private CiCustomerVerificationService customerVerificationService;
    @Autowired private CurtailmentEventDao curtailmentEventDao;
    @Autowired private CurtailmentEventNotifDao curtailmentNotifDao;
    @Autowired private CustomerLMProgramService customerLMProgramService;
    @Autowired private CustomerPointService customerPointService;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private EconomicEventDao economicEventDao;
    @Autowired private EconomicEventParticipantDao economicEventParticipantDao;
    @Autowired private EconomicService economicService;
    @Autowired private GroupBeanValidator groupBeanValidator;
    @Autowired private GroupCustomerNotifDao groupCustomerNotifDao;
    @Autowired private GroupDao groupDao;
    @Autowired private GroupService groupService;
    @Autowired private NotificationGroupDao notificationGroupDao;
    @Autowired private ProgramParameterDao programParameterDao;
    @Autowired private ProgramService programService;
    @Autowired private StrategyFactory strategyFactory;
    @Autowired private IDatabaseCache serverDatabaseCache;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @InitBinder
    public void initBinder(WebDataBinder binder, final YukonUserContext userContext) {
        // increased array size from default value of 256 to 1000
        binder.setAutoGrowCollectionLimit(1000);
        datePropertyEditorFactory.setupDateTimePropertyEditor(binder, userContext, BlankMode.CURRENT);
    }
    
    @RequestMapping("/cc/home")
    public String home(ModelMap model, YukonUserContext userContext, Integer trendId) {
        
        EnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(userContext.getYukonUser());
        
        //Retrieve current, pending, recent events
        List<BaseEvent> currentEvents = baseEventDao.getAllForEnergyCompany(energyCompany, ciCurtailmentService.getCurrentEventPredicate());
        List<BaseEvent> pendingEvents = baseEventDao.getAllForEnergyCompany(energyCompany, ciCurtailmentService.getPendingEventPredicate());
        List<BaseEvent> recentEvents = baseEventDao.getAllForEnergyCompany(energyCompany, ciCurtailmentService.getRecentEventPredicate());
        Collections.reverse(currentEvents);
        Collections.reverse(pendingEvents);
        Collections.reverse(recentEvents);
        
        //Map events by type
        Map<CiEventStatus, List<BaseEvent>> events = new LinkedHashMap<>();
        events.put(CiEventStatus.CURRENT, currentEvents);
        events.put(CiEventStatus.PENDING, pendingEvents);
        events.put(CiEventStatus.RECENT, recentEvents);
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
        
        Collections.sort(allPrograms, new Comparator<Program>() {
            @Override
            public int compare(Program one, Program two) {
                int typeComparison = one.getProgramType().compareTo(two.getProgramType());
                if (typeComparison != 0) {
                    return typeComparison;
                }
                return one.getName().compareTo(two.getName());
            }
        });
        model.addAttribute("programs", allPrograms);
        
        trendHelper.setUpTrends(model, userContext, trendId, true);
        
        return "dr/cc/home.jsp";
    }
    
    @RequestMapping("/cc/program/{programId}/init")
    public String init(ModelMap model,
                       YukonUserContext userContext,
                       @ModelAttribute("event") CiInitEventModel event, 
                       @PathVariable int programId) {
        
        Program program = programService.getProgramById(programId);
        model.addAttribute("program", program);
        event.setProgramId(program.getId());
        
        String strategyString = program.getProgramType().getStrategy();
        CiEventType eventType = CiEventType.of(strategyString);
        event.setEventType(eventType);
        
        DateTime currentTime = new DateTime(userContext.getJodaTimeZone());
        DateTime nextHourTime = TimeUtil.roundUpToNextHour(currentTime);
        if (event.getEventType().isNotification() || event.getEventType().isEconomic()) {
            event.setNotificationTime(nextHourTime);
            event.setStartTime(nextHourTime.plus(Duration.standardHours(1)));
        } else {
            event.setStartTime(nextHourTime);
        }
        
        int programDefaultDuration = programParameterDao.getParameterValueInt(program, ProgramParameterKey.DEFAULT_EVENT_DURATION_MINUTES);
        event.setDuration(programDefaultDuration);
        
        if (event.getEventType().isEconomic()) {
            event.setNumberOfWindows(programDefaultDuration / 60);
        }
        
        return "dr/cc/init.jsp";
    }
    
    @RequestMapping("/cc/program/{programId}/pricing")
    public String pricing(ModelMap model, 
                          @ModelAttribute("event") CiInitEventModel event,
                          BindingResult bindingResult,
                          @PathVariable int programId) {
        
        Program program = programService.getProgramById(programId);
        model.addAttribute("program", program);
        
        // Validate input so far
        CiInitEventModelValidator validator = eventModelValidator.getAfterInitValidator();
        validator.doValidation(event, bindingResult);
        if (bindingResult.hasErrors()) {
            return "dr/cc/init.jsp";
        }
        
        int numberOfWindows = event.getNumberOfWindows();
        float defaultPriceParameter = programParameterDao.getParameterValueFloat(program, ProgramParameterKey.DEFAULT_ENERGY_PRICE);
        BigDecimal defaultEnergyPrice = new BigDecimal(defaultPriceParameter, new MathContext(5));
        
        // Populate times for window price fields
        List<DateTime> windowTimes = ciCurtailmentService.getEconEventWindowTimes(event);
        model.addAttribute("windowTimes", windowTimes);
        
        // Set window price values in event model to the default
        List<BigDecimal> windowPrices = new ArrayList<>(numberOfWindows);
        for (int i = 0; i < numberOfWindows; i++) {
            windowPrices.add(defaultEnergyPrice);
        }
        event.setWindowPrices(windowPrices);
        
        return "dr/cc/pricing.jsp";
    }
    
    @RequestMapping("/cc/program/{programId}/groupSelection")
    public String groupSelection(ModelMap model,
                                 @ModelAttribute("event") CiInitEventModel event,
                                 BindingResult bindingResult,
                                 @PathVariable int programId) {
        
        Program program = programService.getProgramById(programId);
        model.addAttribute("program", program);
        
        // Validate input so far
        if (event.getEventType().isEconomic()) {
            CiInitEventModelValidator validator = eventModelValidator.getAfterPricingValidator();
            validator.doValidation(event, bindingResult);
            if (bindingResult.hasErrors()) {
                List<DateTime> windowTimes = ciCurtailmentService.getEconEventWindowTimes(event);
                model.addAttribute("windowTimes", windowTimes);
                return "dr/cc/pricing.jsp";
            }
        } else {
            CiInitEventModelValidator validator = eventModelValidator.getAfterInitValidator();
            validator.doValidation(event, bindingResult);
            if (bindingResult.hasErrors()) {
                return "dr/cc/init.jsp";
            }
        }
        
        if (event.isEventExtension()) {
            // Copy customers from previous event, remove if they've exceeded their limit.
            // Then go straight to confirmation
            EconomicEvent initialEvent = economicEventDao.getForId(event.getInitialEventId());
            List<EconomicEventParticipant> initialParticipants = economicEventParticipantDao.getForEvent(initialEvent);
            List<Integer> extensionCustomerIds = new ArrayList<>();
            List<CICustomerStub> extensionCustomers = new ArrayList<>();
            for (EconomicEventParticipant originalParticipant : initialParticipants) {
                CICustomerStub customer = originalParticipant.getCustomer();
                if (ciCurtailmentService.canCustomerParticipateInExtension(event, customer)) {
                    extensionCustomerIds.add(customer.getId());
                    extensionCustomers.add(customer);
                }
            }
            event.setSelectedCustomerIds(extensionCustomerIds);
            model.addAttribute("extensionCustomers", extensionCustomers);
            
            List<DateTime> windowTimes = getWindowTimes(event);
            model.addAttribute("windowTimes", windowTimes);
            
            return "/dr/cc/confirmation.jsp";
        } else {
            List<AvailableProgramGroup> availableGroups = programService.getAvailableProgramGroups(program);
            model.addAttribute("availableGroups", availableGroups);
        }
        
        return "dr/cc/groupSelection.jsp";
    }
    
    @RequestMapping("/cc/program/{programId}/customerVerification")
    public String customerVerification(ModelMap model,
                                       @ModelAttribute("event") CiInitEventModel event,
                                       BindingResult bindingResult,
                                       @PathVariable int programId,
                                       YukonUserContext userContext) {
        
        Program program = programService.getProgramById(programId);
        model.addAttribute("program", program);
        
        CiInitEventModelValidator validator = eventModelValidator.getAfterGroupSelectionValidator();
        validator.doValidation(event, bindingResult);
        if (bindingResult.hasErrors()) {
            List<AvailableProgramGroup> availableGroups = programService.getAvailableProgramGroups(program);
            model.addAttribute("availableGroups", availableGroups);
            return "dr/cc/groupSelection.jsp";
        }
        
        setUpCustomerVerificationModel(model, event, userContext);
        
        return "dr/cc/customerVerification.jsp";
    }
    
    private List<GroupCustomerNotif> setUpCustomerVerificationModel(ModelMap model, CiInitEventModel event, YukonUserContext userContext) {
        //Determine which customers to display (and exclusions which prevent customers from being selected)
        Map<Integer, List<Exclusion>> exclusions = new HashMap<>();
        List<Group> selectedGroups = groupService.getGroupsById(event.getSelectedGroupIds());
        List<GroupCustomerNotif> customerNotifs = customerVerificationService.getVerifiedCustomerList(event, selectedGroups, exclusions);
        Collections.sort(customerNotifs, new Comparator<GroupCustomerNotif>() {
            @Override
            public int compare(GroupCustomerNotif thing1, GroupCustomerNotif thing2) {
                String name1 = thing1.getCustomer().getCompanyName();
                String name2 = thing2.getCustomer().getCompanyName();
                return name1.compareTo(name2);
            }
        });
        model.addAttribute("customerNotifs", customerNotifs);
        model.addAttribute("exclusions", exclusions);
        
        //Determine which customers should be checked
        if (event.getSelectedCustomerIds() == null) {
            List<Integer> selectedCustomerIds = new ArrayList<>();
            for (GroupCustomerNotif customerNotif : customerNotifs) {
                List<Exclusion> customerExclusions = exclusions.get(customerNotif.getId());
                if (customerExclusions.isEmpty()) {
                    selectedCustomerIds.add(customerNotif.getId());
                }
            }
            event.setSelectedCustomerIds(selectedCustomerIds);
        }
        
        //Get constraint status strings
        Map<Integer, String> customerConstraintStatuses = new HashMap<>();
        for (GroupCustomerNotif customerNotif : customerNotifs) {
            String status = customerVerificationService.getConstraintStatus(event, customerNotif.getCustomer(), userContext);
            customerConstraintStatuses.put(customerNotif.getId(), status);
        }
        model.addAttribute("customerConstraintStatuses", customerConstraintStatuses);
        
        //Set up "current load" point updaters
        Map<Integer, String> currentLoadUpdaters = new HashMap<>();
        for (GroupCustomerNotif customerNotif : customerNotifs) {
            String updaterString = ciCurtailmentService.getUpdaterString(customerNotif, CICustomerPointType.CurrentLoad, userContext);
            currentLoadUpdaters.put(customerNotif.getId(), updaterString);
        }
        model.addAttribute("currentLoadUpdaters", currentLoadUpdaters);
        
        //Set up contract firm demand updaters
        Map<Integer, String> cfdUpdaters = new HashMap<>();
        for (GroupCustomerNotif customerNotif : customerNotifs) {
            String updaterString = ciCurtailmentService.getUpdaterString(customerNotif, CICustomerPointType.ContractFrmDmd, userContext);
            cfdUpdaters.put(customerNotif.getId(), updaterString);
        }
        model.addAttribute("cfdUpdaters", cfdUpdaters);
        
        return customerNotifs;
    }
    
    @RequestMapping("/cc/program/{programId}/confirmation")
    public String confirmation(ModelMap model,
                               @ModelAttribute("event") CiInitEventModel event,
                               BindingResult bindingResult,
                               @PathVariable int programId,
                               YukonUserContext userContext) {
        
        CiInitEventModelValidator validator = eventModelValidator.getAfterCustomerVerificationValidator();
        validator.doValidation(event, bindingResult);
        if (bindingResult.hasErrors()) {
            Program program = programService.getProgramById(programId);
            model.addAttribute("program", program);
            setUpCustomerVerificationModel(model, event, userContext);
            return "dr/cc/customerVerification.jsp";
        }
        
        setUpConfirmationModel(model, event, programId);
        
        return "dr/cc/confirmation.jsp";
    }
    
    private void setUpConfirmationModel(ModelMap model, CiInitEventModel event, int programId) {
        Program program = programService.getProgramById(programId);
        model.addAttribute("program", program);
        
        List<GroupCustomerNotif> customerNotifs = groupCustomerNotifDao.getByIds(event.getSelectedCustomerIds());
        model.addAttribute("customerNotifs", customerNotifs);
        
        if (event.getEventType().isEconomic()) {
            List<DateTime> windowTimes = getWindowTimes(event);
            model.addAttribute("windowTimes", windowTimes);
        }
    }
    
    private List<DateTime> getWindowTimes(CiInitEventModel event) {
        int numberOfWindows = event.getNumberOfWindows();
        List<DateTime> windowTimes = new ArrayList<>(numberOfWindows);
        for (int i = 0; i < numberOfWindows; i++) {
            DateTime windowStart = event.getStartTime().plus(Duration.standardHours(i));
            windowTimes.add(windowStart);
        }
        return windowTimes;
    }
    
    @RequestMapping("/cc/program/{programId}/createEvent")
    public String createEvent(ModelMap model,
                              @ModelAttribute("event") CiInitEventModel event,
                              BindingResult bindingResult,
                              @PathVariable int programId) {
        
        CiInitEventModelValidator validator = eventModelValidator.getPreCreateValidator();
        validator.doValidation(event, bindingResult);
        if (bindingResult.hasErrors()) {
            setUpConfirmationModel(model, event, programId);
            return "dr/cc/confirmation.jsp";
        }
        
        try {
            int eventId = ciEventCreationService.createEvent(event);
            return "redirect:/dr/cc/program/" + programId + "/event/" + eventId + "/detail";
        } catch (EventCreationException e) {
            bindingResult.reject("yukon.web.modules.dr.cc.init.error.noAdvancedBuyThrough");
            setUpConfirmationModel(model, event, programId);
            return "dr/cc/confirmation.jsp";
        } catch (ConnectionException e) {
            bindingResult.reject("yukon.web.modules.dr.cc.init.error.noConnection");
            setUpConfirmationModel(model, event, programId);
            return "dr/cc/confirmation.jsp";
        }
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
        
        Optional<? extends BaseEvent> eventToDelete = eventList.stream()
                                                               .filter(ciCurtailmentService.getEventIdFilter(eventId))
                                                               .findFirst();
        
        if (eventToDelete.isPresent()) {
            strategy.forceDelete(eventToDelete.get());
            eventList.remove(eventToDelete.get());
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.history.eventDelete.success"));
        } else {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.history.eventDelete.failure"));
        }
        
        model.addAttribute("eventHistory", eventList);
        
        return "dr/cc/history.jsp";
    }
    
    @RequestMapping("/cc/program/{programId}/event/{eventId}/cancel")
    public String cancelEvent(@PathVariable int programId,
                              @PathVariable int eventId,
                              LiteYukonUser user,
                              FlashScope flash) {
        
        Program program = programService.getProgramById(programId);
        String strategyString = program.getProgramType().getStrategy();
        CiEventType eventType = CiEventType.of(strategyString);
        
        try {
            if (eventType.isNotification()) {
                NotificationStrategy strategy = (NotificationStrategy) strategyFactory.getStrategy(program);
                CurtailmentEvent event = curtailmentEventDao.getForId(eventId);
                strategy.cancelEvent(event, user);
            } else {
                EconomicStrategy strategy = (EconomicStrategy) strategyFactory.getStrategy(program);
                EconomicEvent event = economicEventDao.getForId(eventId);
                strategy.cancelEvent(event, user);
            }
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.detail.cancel.success"));
        } catch (RuntimeException e) {
            // Unfortunate to have to catch this. 
            // Consider changing to a more specific exception in strategy.
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.detail.cancel.error"));
            log.error("Error cancelling event.", e);
        }
        
        return "redirect:/dr/cc/program/" + programId + "/event/" + eventId + "/detail";
    }
    
    @RequestMapping("/cc/program/{programId}/event/{eventId}/suppress")
    public String suppressEvent(@PathVariable int programId,
                                @PathVariable int eventId,
                                LiteYukonUser user,
                                FlashScope flash) {
        
        EconomicEvent event = economicEventDao.getForId(eventId);
        EconomicStrategy strategy = (EconomicStrategy) strategyFactory.getStrategy(event.getProgram());
        strategy.suppressEvent(event, user);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.detail.suppress.success"));
        
        return "redirect:/dr/cc/program/" + programId + "/event/" + eventId + "/detail";
    }
    
    @RequestMapping("/cc/program/{programId}/event/{eventId}/revise")
    public String reviseEvent(ModelMap model,
                              @PathVariable int programId,
                              @PathVariable int eventId,
                              LiteYukonUser user,
                              FlashScope flash) {
        
        Program program = programService.getProgramById(programId);
        model.addAttribute("program", program);
        model.addAttribute("programId", programId);
        model.addAttribute("eventId", eventId);
        
        EconomicEvent event = economicEventDao.getForId(eventId);
        EconomicStrategy strategy = (EconomicStrategy) strategyFactory.getStrategy(event.getProgram());
        
        try {
            EconomicEventPricing nextRevision = strategy.createEventRevision(event, user);
            List<EconomicEventPricingWindow> nextRevisionPrices = 
                    new ArrayList<EconomicEventPricingWindow>(nextRevision.getWindows().values());
            Collections.sort(nextRevisionPrices);
            model.addAttribute("nextRevisionPrices", nextRevisionPrices);
            
            return "dr/cc/reviseEconomicEvent.jsp";
        } catch (EventModificationException e) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.detail.revise.error"));
            return "redirect:/dr/cc/program/" + programId + "/event/" + eventId + "/detail";
        }
    }
    
    @RequestMapping("/cc/program/{programId}/event/{eventId}/reviseComplete")
    public String reviseEventComplete(@PathVariable int programId,
                                      @PathVariable int eventId,
                                      @RequestParam double[] prices,
                                      LiteYukonUser user,
                                      FlashScope flash) {
        
        EconomicEvent event = economicEventDao.getForId(eventId);
        EconomicStrategy strategy = (EconomicStrategy) strategyFactory.getStrategy(event.getProgram());
        
        EconomicEventPricing nextRevision = strategy.createEventRevision(event, user);
        List<EconomicEventPricingWindow> pricingWindows = 
                new ArrayList<EconomicEventPricingWindow>(nextRevision.getWindows().values());
        Collections.sort(pricingWindows);
        for (int i = 0; i < pricingWindows.size(); i++) {
            BigDecimal newPrice = new BigDecimal(prices[i]);
            pricingWindows.get(i).setEnergyPrice(newPrice);
        }
        
        strategy.saveRevision(nextRevision);
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.init.reviseEconomicEvent.success"));
        return "redirect:/dr/cc/program/" + programId + "/event/" + eventId + "/detail";
    }
    
    @RequestMapping("/cc/program/{programId}/event/{eventId}/extend")
    public String extendEvent(ModelMap model,
                              YukonUserContext userContext,
                              @ModelAttribute("event") CiInitEventModel event, 
                              @PathVariable int programId,
                              @PathVariable int eventId) {
               
        Program program = programService.getProgramById(programId);
        model.addAttribute("program", program);
        event.setProgramId(program.getId());
        
        String strategyString = program.getProgramType().getStrategy();
        CiEventType eventType = CiEventType.of(strategyString);
        event.setEventType(eventType);
        event.setInitialEventId(eventId); //this marks the event as an extension
        model.addAttribute("eventType", eventType);
        
        EconomicEvent previousEvent = economicEventDao.getForId(eventId);
        
        // Set the start time to the previous event's stop time
        DateTime startTime = new DateTime(previousEvent.getStopTime(), userContext.getJodaTimeZone());
        event.setStartTime(startTime);
        
        DateTime now = new DateTime(userContext.getJodaTimeZone());
        // Add 5 minutes, then round to the nearest 5
        DateTime notificationTime = TimeUtil.roundDateTimeUp(now.plusMinutes(5), 5);
        // If the new notif time is before the previous event's notif time, use the previous event's time
        DateTime oldNotificationTime = new DateTime(previousEvent.getNotificationTime(), userContext.getJodaTimeZone());
        if (notificationTime.isBefore(oldNotificationTime)) {
            notificationTime = oldNotificationTime;
        }
        event.setNotificationTime(notificationTime);
        
        int programDefaultDuration = programParameterDao.getParameterValueInt(program, ProgramParameterKey.DEFAULT_EVENT_DURATION_MINUTES);
        event.setDuration(programDefaultDuration);
        event.setNumberOfWindows(programDefaultDuration / 60 / 2);
        
        return "dr/cc/init.jsp";
    }
    
    @RequestMapping("/cc/program/{programId}/event/{eventId}/adjust")
    public String adjustEvent(ModelMap model,
                              @ModelAttribute("event") CiInitEventModel event, 
                              @PathVariable int programId,
                              @PathVariable int eventId) {
        
        Program program = programService.getProgramById(programId);
        model.addAttribute("program", program);
        event.setProgramId(program.getId());
        
        String strategyString = program.getProgramType().getStrategy();
        CiEventType eventType = CiEventType.of(strategyString);
        event.setEventType(eventType);
        
        model.addAttribute("isAdjust", true);
        
        CurtailmentEvent originalEvent = curtailmentEventDao.getForId(eventId);
        event.setStartTime(new DateTime(originalEvent.getStartTime()));
        event.setDuration(originalEvent.getDuration());
        event.setAdjustEventId(eventId);
        
        return "dr/cc/init.jsp";
    }
    
    @RequestMapping("/cc/program/{programId}/event/{eventId}/completeEventAdjustment")
    public String completeAdjustEvent(LiteYukonUser user,
                                      @ModelAttribute("event") CiInitEventModel event, 
                                      @PathVariable int programId,
                                      @PathVariable int eventId,
                                      FlashScope flash) {
        
        CurtailmentEvent originalEvent = curtailmentEventDao.getForId(eventId);
        if (!ciCurtailmentService.canCurtailmentEventBeAdjusted(originalEvent, user)) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.modify.eventModifyFailed"));
            return "redirect:/dr/cc/program/" + programId + "/event/" + eventId + "/detail";
        }
        
        ciEventCreationService.adjustEvent(originalEvent, event);
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.modify.success"));
        return "redirect:/dr/cc/program/" + programId + "/event/" + eventId + "/detail";
    }
    
    //A.K.A "Split" event
    @RequestMapping("/cc/program/{programId}/event/{eventId}/remove")
    public String removeEvent(ModelMap model,
                              YukonUserContext userContext,
                              @ModelAttribute("event") CiInitEventModel event, 
                              @PathVariable int programId,
                              @PathVariable int eventId) {
        
        Program program = programService.getProgramById(programId);
        model.addAttribute("program", program);
        model.addAttribute("eventId", eventId);
        
        String strategyString = program.getProgramType().getStrategy();
        CiEventType eventType = CiEventType.of(strategyString);
        event.setEventType(eventType);
        
        // Populate groups for program
        List<AvailableProgramGroup> availableGroups = programService.getAvailableProgramGroups(program);
        List<Integer> groupIds = availableGroups.stream()
                                                .map(new Function<AvailableProgramGroup, Integer>() {
                                                    @Override
                                                    public Integer apply(AvailableProgramGroup apg) {
                                                        return apg.getGroup().getId();
                                                    }
                                                }).collect(Collectors.toList());
        event.setSelectedGroupIds(groupIds);
        
        // Copy over parameters from original event
        CurtailmentEvent originalEvent = curtailmentEventDao.getForId(eventId);
        event.setStartTime(new DateTime(originalEvent.getStartTime()));
        event.setNotificationTime(new DateTime(originalEvent.getNotificationTime()));
        event.setDuration(originalEvent.getDuration());
        
        // Set up customers, exclusions, etc. just like original customer verification
        List<GroupCustomerNotif> notifs = setUpCustomerVerificationModel(model, event, userContext);
        
        // Only show customers if they participated in the original event
        List<Integer> customers = ciCurtailmentService.getCurtailmentCustomerIds(eventId);
        notifs = notifs.stream()
                       .filter(new Predicate<GroupCustomerNotif>() {
                           @Override
                           public boolean test(GroupCustomerNotif notif) {
                               return customers.contains(notif.getCustomer().getId());
                           }
                       })
                       .collect(Collectors.toList());
        model.addAttribute("customerNotifs", notifs);
        
        // Tell the page we're doing a split, not regular customer verification
        model.addAttribute("isSplit", true);
        
        return "dr/cc/customerVerification.jsp";
    }
    
    @RequestMapping("/cc/program/{programId}/event/{eventId}/split")
    public String splitEvent(LiteYukonUser user,
                             @ModelAttribute("event") CiInitEventModel event, 
                             @PathVariable int programId,
                             @PathVariable int eventId,
                             FlashScope flash) {
        
        CurtailmentEvent originalEvent = curtailmentEventDao.getForId(eventId);
        if (!ciCurtailmentService.canCurtailmentEventBeAdjusted(originalEvent, user)) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.modify.eventModifyFailed"));
            return "redirect:/dr/cc/program/" + programId + "/event/" + eventId + "/detail";
        }
        
        // Determine customers to be removed
        List<GroupCustomerNotif> customerNotifsToRemove = groupCustomerNotifDao.getByIds(event.getSelectedCustomerIds());
        List<CICustomerStub> customersToRemove = customerNotifsToRemove.stream()
                .map(new Function<GroupCustomerNotif, CICustomerStub>() {
                    @Override
                    public CICustomerStub apply(GroupCustomerNotif notif) {
                        int id = notif.getCustomer().getId();
                        return customerPointService.getCustomer(id);
                    }
                })
                .collect(Collectors.toList());
        
        int splitEventId = ciEventCreationService.splitEvent(originalEvent, customersToRemove);
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.modify.success"));
        return "redirect:/dr/cc/program/" + programId + "/event/" + splitEventId + "/detail";
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
        
        model.addAttribute("program", event.getProgram());
        
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
        
        groupBean = ciCurtailmentService.buildGroupBean(group, assignedGroupCustomerList, unassignedGroupCustomerList);
        model.addAttribute("group", groupBean);
        
        return "dr/cc/groupDetail.jsp";
    }
    
    @RequestMapping("/cc/groupCreate")
    public String groupCreate(ModelMap model, LiteYukonUser user, @ModelAttribute("group") GroupBean groupBean) {
        
        Group newGroup = groupService.createNewGroup(user);        
        List<GroupCustomerNotif> assignedGroupCustomerList = new ArrayList<>();
        List<GroupCustomerNotif> unassignedGroupCustomerList = groupService.getUnassignedCustomers(newGroup, true);
        
        groupBean = ciCurtailmentService.buildGroupBean(newGroup, assignedGroupCustomerList, unassignedGroupCustomerList);
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
    public String groupSave(YukonUserContext userContext,
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
        
        List<GroupCustomerNotif> assignedGroupCustomerNotifs = ciCurtailmentService.buildAssignedGroupCustomerNotifSettings(group, groupBean);
        
        groupService.saveGroup(group, assignedGroupCustomerNotifs);
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
    public String programDetail(ModelMap model, @PathVariable int programId) {
        
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
    public String programCreate(ModelMap model, LiteYukonUser user) {
        
        List<ProgramType> programTypeList = programService.getProgramTypeList(user);
        model.addAttribute("programTypes", programTypeList);
        return "dr/cc/programCreate.jsp";
    }

    @RequestMapping("/cc/programDetailCreate/{programTypeId}/{name}")
    public String programDetailCreate(LiteYukonUser user, 
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
    public String programDelete(@PathVariable int programId, FlashScope flash) {
        
        Program program = programService.getProgramById(programId);
        programService.deleteProgram(program);
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.programDetail.deleteSuccessful"));
        
        return "redirect:/dr/cc/programList";
    }
    
    @RequestMapping("/cc/programSave/{programId}")
    public String programSave(ModelMap model,
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

        ProgramFields programFields =new ProgramFields(programName, programIdentifierPrefix, programLastIdentifier);
        //Validation
        DataBinder binder = new DataBinder(programFields);
        BindingResult bindingResult = binder.getBindingResult();
        YukonValidationUtils.checkIsBlankOrExceedsMaxLengthOrBlacklistedChars(bindingResult, "programName", programName, false, 255);
        YukonValidationUtils.checkIsBlankOrExceedsMaxLengthOrBlacklistedChars(bindingResult, "programIdentifierPrefix", programIdentifierPrefix, false, 32);
        YukonValidationUtils.checkIsPositiveInt(bindingResult, "programLastIdentifier", programLastIdentifier);

        CICurtailmentStrategy strategy = strategyFactory.getStrategy(program);
        List<ProgramParameter> programParameters = strategy.getParameters(program);

        for (ProgramParameter parameter : programParameters) {
            switch(parameter.getParameterKey()) {
            case DEFAULT_EVENT_OFFSET_MINUTES:
                setProgramParameter(parameter, DEFAULT_EVENT_OFFSET_MINUTES, "eventTimeOffsetMinutes", bindingResult);
                break;
            case DEFAULT_NOTIFICATION_OFFSET_MINUTES:
                setProgramParameter(parameter, DEFAULT_NOTIFICATION_OFFSET_MINUTES, "notificationTimeOffsetMinutes", bindingResult);
                break;
            case MINIMUM_NOTIFICATION_MINUTES:
                setProgramParameter(parameter, MINIMUM_NOTIFICATION_MINUTES, "minimumNotificationTimeMinutes", bindingResult);
                break;
            case DEFAULT_EVENT_DURATION_MINUTES:
                setProgramParameter(parameter, DEFAULT_EVENT_DURATION_MINUTES, "defaultEventDuration", bindingResult);
                break;
            case MINIMUM_EVENT_DURATION_MINUTES:
                setProgramParameter(parameter, MINIMUM_EVENT_DURATION_MINUTES, "minimumEventDuration", bindingResult);
                break;
            case DEFAULT_ENERGY_PRICE:
                setProgramParameter(parameter, DEFAULT_ENERGY_PRICE, "energyPrice", bindingResult);
                break;
            case CUSTOMER_ELECTION_CUTOFF_MINUTES:
                setProgramParameter(parameter, CUSTOMER_ELECTION_CUTOFF_MINUTES, "customerElectionCutoff", bindingResult);
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

        FieldError nameError = bindingResult.getFieldError("programName");
        FieldError prefixError = bindingResult.getFieldError("programIdentifierPrefix");
        FieldError postfixError = bindingResult.getFieldError("programLastIdentifier");
        FieldError eventTimeOffsetMinutesError = bindingResult.getFieldError("eventTimeOffsetMinutes");
        FieldError notificationTimeOffsetMinutesError = bindingResult.getFieldError("notificationTimeOffsetMinutes");
        FieldError minimumNotificationTimeMinutesError = bindingResult.getFieldError("minimumNotificationTimeMinutes");
        FieldError defaultEventDurationError = bindingResult.getFieldError("defaultEventDuration");
        FieldError minimumEventDurationError = bindingResult.getFieldError("minimumEventDuration");
        FieldError energyPriceError = bindingResult.getFieldError("energyPrice");
        FieldError customerElectionCutoffError = bindingResult.getFieldError("customerElectionCutoff");
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("nameError", nameError == null ? null : nameError.getCode());
            model.addAttribute("prefixError", prefixError == null ? null : prefixError.getCode());
            model.addAttribute("postfixError", postfixError == null ? null : postfixError.getCode());
            model.addAttribute("error_DEFAULT_EVENT_OFFSET_MINUTES", eventTimeOffsetMinutesError == null ? null : eventTimeOffsetMinutesError.getCode());
            model.addAttribute("error_DEFAULT_NOTIFICATION_OFFSET_MINUTES", notificationTimeOffsetMinutesError == null ? null : notificationTimeOffsetMinutesError.getCode());
            model.addAttribute("error_MINIMUM_NOTIFICATION_MINUTES", minimumNotificationTimeMinutesError == null ? null : minimumNotificationTimeMinutesError.getCode());
            model.addAttribute("error_DEFAULT_EVENT_DURATION_MINUTES", defaultEventDurationError == null ? null : defaultEventDurationError.getCode());
            model.addAttribute("error_MINIMUM_EVENT_DURATION_MINUTES", minimumEventDurationError == null ? null : minimumEventDurationError.getCode());
            model.addAttribute("error_DEFAULT_ENERGY_PRICE", energyPriceError == null ? null : energyPriceError.getCode());
            model.addAttribute("error_CUSTOMER_ELECTION_CUTOFF_MINUTES", customerElectionCutoffError == null ? null : customerElectionCutoffError.getCode()); 
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
    
    private void setProgramParameter(ProgramParameter parameter, Number parameterInput, String programFields, BindingResult bindingResult) {
        if (parameterInput != null) {
            parameter.setParameterValue(parameterInput.toString());
        } else {
            parameter.setParameterValue(null);
        }
        YukonValidationUtils.checkIsNumberPositiveIntOrDouble(bindingResult, programFields, parameterInput);
    }
    
    @RequestMapping("/cc/program/{programId}/event/{eventId}/revision/{revision}")
    public String revision(ModelMap model,
            YukonUserContext userContext,
            @PathVariable int programId,
            @PathVariable int eventId,
            @PathVariable int revision,
            HttpServletRequest request) {
        return economicDetail(model, userContext, programId, eventId, revision, request);
    }
    
    @RequestMapping("/cc/program/{programId}/event/{eventId}/detail")
    public String detail (ModelMap model,
            YukonUserContext userContext,
            HttpServletRequest request,
            @PathVariable int programId,
            @PathVariable int eventId) {
        
        String programTypeStrategy  = programService.getProgramById(programId).getProgramType().getStrategy();
        CiEventType ciEventType = CiEventType.of(programTypeStrategy);
        Integer programTypeId = programService.getProgramById(programId).getProgramType().getId();
        if (ciEventType.isNotification() || ciEventType.isDirect()) {
            return capacityDetail(model, userContext, programTypeId, eventId);
        } else if (ciEventType.isAccounting()) {
            return accountingDetail(model, userContext, programTypeId, eventId);
        } else if (ciEventType.isEconomic()) {
            return economicDetail(model, userContext, programId, eventId, -1, request);
        } else {
            throw new IllegalArgumentException("Invalid program type id: " + programTypeId);
        }
    }
    
    private String accountingDetail(ModelMap model,
                                    YukonUserContext userContext,
                                    Integer programTypeId,
                                    int id) {
        
        model.addAttribute("showDeleteButton", true);
        
        AccountingEvent acctEvent = accountingEventDao.getForId(id);
        model.addAttribute("event", acctEvent);
        model.addAttribute("programTypeId", programTypeId);
        model.addAttribute("reason", acctEvent.getReason());
        model.addAttribute("duration", acctEvent.getDuration());
        
        Program acctProgram = acctEvent.getProgram();
        model.addAttribute("program", acctProgram);
        
        AccountingStrategy strategy = (AccountingStrategy) strategyFactory.getStrategy(acctProgram);
        List<AccountingEventParticipant> eventNotifications = strategy.getParticipants(acctEvent);
        model.addAttribute("eventNotifications", eventNotifications);

        model.addAttribute("affectedCustomers", composeEventHeading(eventHeadingBase, userContext, "accounting_affected_customers"));
        
        String tz = userContext.getTimeZone().getDisplayName();
        model.addAttribute("tz", tz);
        
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
        
        List<String> notificationTableHead = assembleHeading(userContext, companyHeadingBase, headingSuffixes);
        model.addAttribute("notificationTableHead", notificationTableHead);
        
        Program program = curtailmentEvent.getProgram();
        model.addAttribute("program", program);
        
        LiteYukonUser user = userContext.getYukonUser();
        NotificationStrategy notifStrategy = (NotificationStrategy) strategyFactory.getStrategy(program);
        
        Boolean canEventBeDeleted = notifStrategy.canEventBeDeleted(curtailmentEvent, user);
        model.addAttribute("showDeleteButton", canEventBeDeleted);
        
        Boolean canEventBeCancelled = notifStrategy.canEventBeCancelled(curtailmentEvent, user);
        model.addAttribute("showCancelButton", canEventBeCancelled);
        
        Boolean canEventBeAdjusted = notifStrategy.canEventBeAdjusted(curtailmentEvent, user);
        model.addAttribute("showAdjustButton", canEventBeAdjusted);
        
        Boolean canEventBeRemoved = notifStrategy.canCustomersBeRemovedFromEvent(curtailmentEvent, user);
        model.addAttribute("showRemoveButton", canEventBeRemoved);
        
        String tz = userContext.getTimeZone().getDisplayName();
        model.addAttribute("tz", tz);
        
        return "dr/cc/detail.jsp";
    }
    
    private String economicDetail(ModelMap model,
            YukonUserContext userContext,
            int programId,
            int eventId,
            int revisionNumber,
            HttpServletRequest request) {
        
        EconomicEvent event = economicEventDao.getForId(eventId);
        model.addAttribute("event", event);
        
        EconomicStrategy strategy = (EconomicStrategy) strategyFactory.getStrategy(event.getProgram());
        Map<Integer, EconomicEventPricing> revisions = event.getRevisions();
        
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
        
        for (int bigInd = 0; bigInd < columnTotals.length; bigInd++) {
            columnTotals[bigInd] = new BigDecimal(0);
        }
        
        DateTimeFormatter dateTimeFormatter = dateFormattingService.getDateTimeFormatter(DateFormatEnum.TIME, userContext);
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
        
        model.addAttribute("pricingWindows", pricingWindows);
        model.addAttribute("revisionList", revisionList);
        model.addAttribute("selectedRevision", curEconEventPricingRevision.getRevision());
        
        List<EconomicEventParticipant> participantList = economicEventParticipantDao.getForEvent(event);
        List<List<Object>> tableData = new ArrayList<>();
        Integer columnIndex = 0;
        for (EconomicEventParticipant participant : participantList) {
            
            List<Object> pricingData = new ArrayList<>();
            String companyName = participant.getCustomer().getCompanyName();
            Integer idInteger = participant.getCustomer().getId();
            Map<String, Object> companyInfo = new HashMap<>();
            companyInfo.put("name", companyName);
            companyInfo.put("id", idInteger);
            pricingData.add(companyInfo);
            pricingData.add(getAckForRow(participant, currentRevision));
            String imgPath = request.getContextPath() + getNotifForRow(participant, strategy);
            pricingData.add(imgPath);
            tableData.add(pricingData);

            EconomicEventParticipantSelectionWindow selection;
            for (columnIndex = 0; columnIndex < countRevisions; columnIndex++) {
                    selection = economicService.getCustomerSelectionWindow(currentRevision, participant, columnIndex);
                BigDecimal energyToBuy = selection.getEnergyToBuy();
                pricingData.add(energyToBuy.toString());
                columnTotals[columnIndex] = columnTotals[columnIndex].add(energyToBuy);
            }
        }
        
        List<String> pricingTableTotals = new ArrayList<>();
        for (int totalsIndex = 0; totalsIndex < columnIndex; totalsIndex++) {
            pricingTableTotals.add(columnTotals[totalsIndex] == null ? "-----" : columnTotals[totalsIndex].toString());
        }
        
        model.addAttribute("pricingTableHead", pricingTableHead);
        model.addAttribute("tableData", tableData);
        model.addAttribute("pricingTableTotals", pricingTableTotals);
        
        model.addAttribute("economicDetail", "yes");
        model.addAttribute("legend", composeEventHeading(companyHeadingBase, userContext, "ccurtEvent_pricing_legend"));
        Program program = programService.getProgramById(programId);
        model.addAttribute("program", program);
        
        LiteYukonUser user = userContext.getYukonUser();
        
        Boolean canEventBeDeleted = strategy.canEventBeDeleted(event, user);
        model.addAttribute("showDeleteButton", canEventBeDeleted);
        
        Boolean canEventBeCancelled = strategy.canEventBeCancelled(event, user);
        model.addAttribute("showCancelButton", canEventBeCancelled);
        
        Boolean canEventBeSuppressed = strategy.canEventBeSuppressed(event, user);
        model.addAttribute("showSuppressButton", canEventBeSuppressed);
        
        Boolean canEventBeRevised = strategy.canEventBeRevised(event, user);
        model.addAttribute("showReviseButton", canEventBeRevised);
        
        Boolean canEventBeExtended = strategy.canEventBeExtended(event, user);
        model.addAttribute("showExtendButton", canEventBeExtended);
        
        String tz = userContext.getTimeZone().getDisplayName();
        model.addAttribute("tz", tz);
        
        return "dr/cc/detail.jsp";
    }
    
    @RequestMapping("/cc/customerList")
    public String customerList(ModelMap model, YukonUserContext userContext) {
        
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
                               FlashScope flash) {
        CICustomerStub customer = customerPointService.getCustomer(customerId);
        try {
            if (customerModel.getPointValues() != null) {
                customerPointService.savePointValues(customer, customerModel.getPointValues());
            }

            List<LiteYukonPAObject> activeProgramPaos = new ArrayList<>();      //empty list will remove all
            if (customerModel.getActivePrograms() != null) {
                activeProgramPaos = customerModel.getActivePrograms().stream()
                        .mapToInt(model -> model.getPaoId())
                        .mapToObj(paoId -> serverDatabaseCache.getAllPaosMap().get(paoId)).collect(Collectors.toList());
            }
            customerLMProgramService.saveProgramList(customer, activeProgramPaos);

            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.customerDetail.update.success",
                    customer.getCompanyName()));
            return "redirect:/dr/cc/customerList";

        } catch (Exception e) {
            log.error("Error saving customer {}", customer.getCompanyName(), e);
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dr.cc.customerDetail.update.failure",
                    customer.getCompanyName()));
            return "redirect:/dr/cc/customerDetail/" + customerId;
        }
    }
    
    @RequestMapping("/cc/customerDetail/{customerId}/createPoint/{pointType}")
    public String customerCreatePoint(@PathVariable int customerId, 
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
    
    @SuppressWarnings("unused")
    private class ProgramFields {
        private String programName;
        private String programIdentifierPrefix;
        private Integer programLastIdentifier;
        private Integer eventTimeOffsetMinutes;
        private Integer notificationTimeOffsetMinutes;
        private Integer minimumNotificationTimeMinutes;
        private Integer defaultEventDuration;
        private Integer minimumEventDuration;
        private double energyPrice;
        private Integer customerElectionCutoff;

        public ProgramFields(String programName, String identifierPrefix, Integer programLastIdentifier) {
            this.programName = programName;
            programIdentifierPrefix = identifierPrefix;
            this.programLastIdentifier = programLastIdentifier;
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
        public Integer getProgramLastIdentifier() {
            return programLastIdentifier;
        }
        public void setProgramLastIdentifier(Integer programLastIdentifier) {
            this.programLastIdentifier = programLastIdentifier;
        }
        public Integer getEventTimeOffsetMinutes() {
            return eventTimeOffsetMinutes;
        }
        public void setEventTimeOffsetMinutes(Integer eventTimeOffsetMinutes) {
            this.eventTimeOffsetMinutes = eventTimeOffsetMinutes;
        }
        public Integer getNotificationTimeOffsetMinutes() {
            return notificationTimeOffsetMinutes;
        }
        public void setNotificationTimeOffsetMinutes(Integer notificationTimeOffsetMinutes) {
            this.notificationTimeOffsetMinutes = notificationTimeOffsetMinutes;
        }
        public Integer getMinimumNotificationTimeMinutes() {
            return minimumNotificationTimeMinutes;
        }
        public void setMinimumNotificationTimeMinutes(Integer minimumNotificationTimeMinutes) {
            this.minimumNotificationTimeMinutes = minimumNotificationTimeMinutes;
        }
        public Integer getDefaultEventDuration() {
            return defaultEventDuration;
        }
        public void setDefaultEventDuration(Integer defaultEventDuration) {
            this.defaultEventDuration = defaultEventDuration;
        }
        public Integer getMinimumEventDuration() {
            return minimumEventDuration;
        }
        public void setMinimumEventDuration(Integer minimumEventDuration) {
            this.minimumEventDuration = minimumEventDuration;
        }
        public double getEnergyPrice() {
            return energyPrice;
        }
        public void setEnergyPrice(double energyPrice) {
            this.energyPrice = energyPrice;
        }
        public Integer getCustomerElectionCutoff() {
            return customerElectionCutoff;
        }
        public void setCustomerElectionCutoff(Integer customerElectionCutoff) {
            this.customerElectionCutoff = customerElectionCutoff;
        }       
    }
}