package com.cannontech.web.stars.dr.consumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.survey.dao.SurveyDao;
import com.cannontech.common.survey.model.Question;
import com.cannontech.common.survey.model.Survey;
import com.cannontech.common.survey.service.SurveyService;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.itron.service.ItronCommunicationException;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.service.AccountCheckerService;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.displayable.dao.DisplayableInventoryDao;
import com.cannontech.stars.dr.displayable.model.DisplayableInventory;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutCountHolder;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.model.OptOutEventDto;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.stars.dr.optout.service.OptOutStatusService;
import com.cannontech.stars.dr.optout.service.OptOutSurveyService;
import com.cannontech.stars.dr.optout.util.OptOutUtil;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.model.OptOutBackingBean;
import com.cannontech.web.stars.dr.operator.model.OptOutBackingBean.SurveyResult;
import com.cannontech.web.stars.dr.operator.model.SurveyResultValidator;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

@CheckRoleProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_PROGRAMS_OPT_OUT)
@Controller
public class OptOutController extends AbstractConsumerController {
	
	private static int MAX_NUMBER_OF_OPT_OUT_HISTORY = 6;
	
	@Autowired private AccountEventLogService accountEventLogService;
	@Autowired private RolePropertyDao rolePropertyDao;
	@Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
	@Autowired private LmHardwareBaseDao lmHardwareBaseDao;
	@Autowired private OptOutService optOutService; 
	@Autowired private OptOutEventDao optOutEventDao;
	@Autowired private OptOutStatusService optOutStatusService;
	@Autowired private OptOutSurveyService optOutSurveyService;
	@Autowired private SurveyDao surveyDao;
	@Autowired private SurveyService surveyService;
	@Autowired private OptOutControllerHelper helper;
	@Autowired private AccountCheckerService accountCheckerService;
	@Autowired private DisplayableInventoryDao displayableInventoryDao;
	@Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
	@Autowired private InventoryBaseDao inventoryBaseDao;

    private static class StartDateException extends IllegalArgumentException {
        private final static long serialVersionUID = 1L;

        private StartDateException(String message) {
            super(message);
        }
    };

    @RequestMapping(value = "/consumer/optout", method = RequestMethod.GET)
    public String view(@ModelAttribute CustomerAccount customerAccount,
            YukonUserContext yukonUserContext, ModelMap model) {
    	LiteYukonUser user = yukonUserContext.getYukonUser();
    	if (!optOutStatusService.getOptOutEnabled(user).isOptOutEnabled()) {
    	    return "consumer/optout/optOutDisabled.jsp";
    	}
    	
        Calendar cal = Calendar.getInstance(yukonUserContext.getTimeZone());
    	Date currentDate = cal.getTime();
    	model.addAttribute("currentDate", currentDate);

    	int accountId = customerAccount.getAccountId();

    	// Get the list of current and scheduled opt outs
		List<OptOutEventDto> currentOptOutList = 
    		optOutEventDao.getCurrentOptOuts(accountId);
    	model.addAttribute("currentOptOutList", currentOptOutList);

    	// Get the current counts for used opt outs and remaining allowed opt outs for each device
    	List<DisplayableInventory> displayableInventories = displayableInventoryDao.getOptOutSupportingInventory(customerAccount.getAccountId());
    	
        Map<Integer, OptOutCountHolder> optOutCounts =
            getOptOutCountsForInventories(displayableInventories, customerAccount.getAccountId());

    	boolean allOptedOut = true;
    	boolean optOutsAvailable = false;
    	for(DisplayableInventory inventory : displayableInventories) {
    	    OptOutCountHolder optOutCountHolder =  optOutCounts.get(inventory.getInventoryId());
    	    if (!inventory.isCurrentlyOptedOut()) {
				allOptedOut = false;
			}
    	    
            // Checks if the device can receive an OptOut command from the user. If the device is
            // not currently opted out and/or has no OptOut scheduled, the flag is set to true
            if (optOutCountHolder.isOptOutsRemaining() &&
                (!inventory.isCurrentlyOptedOut() || optOutCountHolder.getScheduledOptOuts() == 0)) {
                optOutsAvailable = true;
            }
    	}
    	model.addAttribute("displayableInventories", displayableInventories);
        model.addAttribute("optOutCounts", optOutCounts);

    	// Get the list of completed and canceled opt outs
    	List<OptOutEventDto> previousOptOutList = 
    		optOutEventDao.getOptOutHistoryForAccount(accountId, MAX_NUMBER_OF_OPT_OUT_HISTORY);
    	model.addAttribute("previousOptOutList", previousOptOutList);
    	model.addAttribute("allOptedOut", allOptedOut);
    	model.addAttribute("optOutsAvailable", optOutsAvailable);
    	String optOutPeriodString = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.RESIDENTIAL_OPT_OUT_PERIOD,  user);
    	List<Integer> optOutPeriodList = OptOutUtil.parseOptOutPeriodString(optOutPeriodString);
    	model.addAttribute("optOutPeriodList", optOutPeriodList);
    	    	
    	return "consumer/optout/optOut.jsp";
    }

    @RequestMapping(value = "/consumer/optout/deviceSelection")
    public String deviceSelection(@ModelAttribute CustomerAccount customerAccount,
            @ModelAttribute OptOutBackingBean optOutBackingBean,
            BindingResult bindingResult, FlashScope flashScope, ModelMap model,
            YukonUserContext userContext) throws CommandCompletionException {
    	final LiteYukonUser user = userContext.getYukonUser();
        if (!optOutStatusService.getOptOutEnabled(user).isOptOutEnabled()) {
            return "consumer/optout/optOutDisabled.jsp";
        }

        // Validate the start date
        try {
            validateStartDate(optOutBackingBean.getStartDate(), userContext, customerAccount);
        } catch (StartDateException exception) {
            MessageSourceResolvable message = new YukonMessageSourceResolvable(exception.getMessage());
            model.addAttribute("result", message);
            return "consumer/optout/optOutResult.jsp";
        }

        LocalDate today = new LocalDate(userContext.getJodaTimeZone());
        boolean isSameDay = today.isEqual(optOutBackingBean.getStartDate());
        model.addAttribute("isSameDay", isSameDay);

        List<DisplayableInventory> displayableInventories = displayableInventoryDao.getOptOutSupportingInventory(customerAccount.getAccountId());
        
        List<DisplayableInventory> optOutableInventories = new ArrayList<DisplayableInventory>();
        
        Map<Integer, OptOutCountHolder> optOutCounts = getOptOutCountsForInventories(displayableInventories, customerAccount.getAccountId());
        Map<DisplayableInventory, Boolean> noOptOutsAvailableLookup = Maps.newHashMap();
        for (DisplayableInventory inventory : displayableInventories) {
            OptOutCountHolder optOutCountHolder = optOutCounts.get(inventory.getInventoryId());
            
            // Check if device can't be opted out on one specific day provided by the user.
            if (!optOutCountHolder.isOptOutsRemaining() ||
                (isSameDay && inventory.isCurrentlyOptedOut()) ||
                (!isSameDay && inventory.getCurrentlyScheduledOptOut() != null)) {
                noOptOutsAvailableLookup.put(inventory, true);
            } else {
                noOptOutsAvailableLookup.put(inventory, false);
                optOutableInventories.add(inventory);
            }
        }

        model.addAttribute("noOptOutsAvailableLookup", noOptOutsAvailableLookup);
        
        model.addAttribute("displayableInventories", displayableInventories);
        model.addAttribute("optOutCounts", optOutCounts);
        
        boolean shouldOptOutAllDevices =
            rolePropertyDao.checkProperty(YukonRoleProperty.RESIDENTIAL_OPT_OUT_ALL_DEVICES, user);

        // blanketDevices:  case 1: exactly 1 device - don't show individual device selection
        //                  case 2: more than one device AND RESIDENTIAL_..._DEVICES property set FALSE - show individual selection
        boolean blanketDevices = ((optOutableInventories.size() == 1) || (optOutableInventories.size() > 1 && shouldOptOutAllDevices));
        if (blanketDevices) {
            Integer[] inventoryIds = new Integer[optOutableInventories.size()];
            for (int index = 0; index < optOutableInventories.size(); index++) {
                inventoryIds[index] = optOutableInventories.get(index).getInventoryId();
            }

            return "consumer/optout/confirmOptOutAllDevices.jsp";
        }
        return "consumer/optout/optOutDeviceSelection.jsp";
    }

    /**
     * This method can be called more than once if the operator has more than
     * one opt out survey to complete. Because of this, it also handles
     * committing an opt out.
     * @throws CommandCompletionException 
     */
    @RequestMapping("/consumer/optout/optOutQuestions")
    public String optOutQuestions(
            @ModelAttribute CustomerAccount customerAccount,
            YukonUserContext userContext,
            @ModelAttribute OptOutBackingBean optOutBackingBean,
            BindingResult bindingResult, FlashScope flashScope, ModelMap model) throws CommandCompletionException {
    	LiteYukonUser user = userContext.getYukonUser();
        if (!optOutStatusService.getOptOutEnabled(user).isOptOutEnabled()) {
            return "consumer/optout/optOutDisabled.jsp";
        }

        accountCheckerService.checkInventory(user,
                                             optOutBackingBean.getInventoryIds());
        Integer[] inventoryIds = optOutBackingBean.getInventoryIds();
        if (inventoryIds == null || inventoryIds.length == 0) {
            model.addAttribute("error", "yukon.dr.consumer.optoutlist.noInventorySelected");
            return deviceSelection(customerAccount, optOutBackingBean,         
                                   bindingResult, flashScope, model,
                                   userContext);
        }

        // Validating to make sure there are opt outs left to use that have not been scheduled.
        List<Integer> inventoryIdList = Arrays.asList(inventoryIds);
        MessageSourceResolvable validateOptOutsRemainingMessage = 
            validateOptOutsRemaining(inventoryIdList, customerAccount);
        if (validateOptOutsRemainingMessage != null) {
            model.addAttribute("result", validateOptOutsRemainingMessage);
            return "consumer/optout/optOutResult.jsp";
        }
        
        
        boolean noSurveysToTake = false;
        int surveyId = 0;
        Integer currentSurveyIndex = optOutBackingBean.getCurrentSurveyIndex();
        if (currentSurveyIndex != null) {
            // Validate the survey they just took and send them back to it
            // if there are problems.
            SurveyResult currentResults =
                optOutBackingBean.getSurveyResults().get(currentSurveyIndex);
            Map<Integer, Question> questionsById =
                surveyDao.getQuestionMapBySurveyId(currentResults.getSurveyId());
            SurveyResultValidator resultsValidator =
                new SurveyResultValidator(currentSurveyIndex, questionsById);
            resultsValidator.validate(optOutBackingBean, bindingResult);
            if (bindingResult.hasErrors()) {
                List<MessageSourceResolvable> messages =
                    YukonValidationUtils.errorsForBindingResult(bindingResult);
                flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
                surveyId = currentResults.getSurveyId();
            }
        }

        Multimap<Integer, Integer> surveyIdsByInventoryId =
            optOutSurveyService.getActiveSurveyIdsByInventoryId(inventoryIdList);
        if (surveyId == 0) {
            // They haven't yet taken a survey or they have and it passed
            // validation.  Check to see if there are any (more).
            if (surveyIdsByInventoryId.isEmpty()) {
                noSurveysToTake = true;
            } else {
                if (optOutBackingBean.getSurveyResults().isEmpty()) {
                    // This is the first time here; create the results.

                    // It's arbitrary that we're sorting by survey id but there
                    // isn't really anything else that makes sense (there
                    // normally won't be more than a single survey in this
                    // list). This will at least keep the ordering consistent.
                    Set<Integer> surveyIds = Sets.newTreeSet();
                    surveyIds.addAll(surveyIdsByInventoryId.values());
                    optOutBackingBean.createSurveyResults(surveyIds);
                }

                // Find the oldest incomplete survey and make it the current.
                int index = 0;
                for (SurveyResult surveyResult : optOutBackingBean.getSurveyResults()) {
                    int possibleSurveyId = surveyResult.getSurveyId();
                    if (!surveyResult.isAnswered() && surveyService.areAllSurveyKeysForContextValid(possibleSurveyId, userContext)) {
                        surveyId = possibleSurveyId;
                        optOutBackingBean.setCurrentSurveyIndex(index);
                        break;
                    }
                    index++;
                }
            }
        }

        // We found a survey they should take or they just took one and it
        // failed validation.
        if (surveyId != 0) {
            Survey survey = surveyDao.getSurveyById(surveyId);
            List<Question> questions = surveyDao.getQuestionsBySurveyId(survey.getSurveyId());
            model.addAttribute("survey", survey);
            model.addAttribute("questions", questions);
            return "consumer/optout/optOutSurvey.jsp";
        }

        if (noSurveysToTake && optOutBackingBean.getLegacyQuestions().isEmpty()) {
            // There are no surveys.  Check for legacy questions.
            List<String> questions =
                helper.getConfirmQuestions(messageSourceResolver,
                    userContext, "yukon.dr.consumer.optoutconfirm.question.");

            if (!questions.isEmpty()) {
                model.addAttribute("questions", questions);
                return "consumer/optout/optOutQuestions.jsp";
            }
        }

        // At this point, we have verified that no surveys/questions exist
        // or that all have been taken if any did exist.

        // Validate info entered on first page.
		try {
            validateStartDate(optOutBackingBean.getStartDate(), userContext, customerAccount);
        } catch (StartDateException exception) {
            MessageSourceResolvable message = new YukonMessageSourceResolvable("yukon.dr.consumer.optoutresult.invalidStartDate");
            model.addAttribute("result", message);
            return "consumer/optout/optOutResult.jsp";
        }

        // If there were any surveys taken, double-check-validate them.
        int index = 0;
        for (SurveyResult surveyResult : optOutBackingBean.getSurveyResults()) {
            Map<Integer, Question> questionsById =
                surveyDao.getQuestionMapBySurveyId(surveyResult.getSurveyId());
            SurveyResultValidator resultsValidator =
                new SurveyResultValidator(index++, questionsById);
            resultsValidator.validate(optOutBackingBean, bindingResult);
        }

        if (bindingResult.hasErrors()) {
            // This should only happen if there is a coding error or
            // they muck with a request along the way somewhere.
            throw new RuntimeException("validation failed; contact administrator");
        }

        // Save survey or legacy questions and do opt out.
        for (int inventoryId : optOutBackingBean.getInventoryIds()) {
            LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
            DateTime startDate =
                optOutBackingBean.getStartDate().toDateTimeAtStartOfDay(userContext.getJodaTimeZone());
            accountEventLogService.optOutAttempted(userContext.getYukonUser(), 
                                                             customerAccount.getAccountNumber(), 
                                                             lmHardwareBase.getManufacturerSerialNumber(),
                                                             startDate,
                                                             EventSource.CONSUMER);
        }

        try {
            helper.processOptOut(optOutBackingBean, userContext, customerAccount, surveyIdsByInventoryId);
            MessageSourceResolvable result = new YukonMessageSourceResolvable("yukon.dr.consumer.optoutresult.success");
            model.addAttribute("result", result);
        } catch (CommandCompletionException e) {
            MessageSourceResolvable result = new YukonMessageSourceResolvable("yukon.dr.consumer.optoutresult.failure");
            model.addAttribute("result", result);
        } catch (ItronCommunicationException e) {
            model.addAttribute("result", e.getItronMessage());
        }

        return "consumer/optout/optOutResult.jsp";
    }

    @RequestMapping(value = "/consumer/optout/confirmCancel")
    public String confirmCancel(
            @ModelAttribute CustomerAccount customerAccount,
            Integer eventId, YukonUserContext yukonUserContext, ModelMap model) {
        // Make sure the event is the current user's event
        helper.checkEventAgainstAccount(eventId, customerAccount.getAccountId());

        // Get the list of current and scheduled opt outs
        List<OptOutEventDto> currentOptOutList = 
            optOutEventDao.getCurrentOptOuts(customerAccount.getAccountId());
        OptOutEventDto optOut = null;
        for (OptOutEventDto event : currentOptOutList) {
            if (event.getEventId().equals(eventId)) {
                optOut = event;
            }
        }

        if (optOut == null) {
            // the opt out has disappeared since they clicked cancel
            return "/stars/consumer/optout";
        }
        model.addAttribute("optOut", optOut);

        return "consumer/optout/confirmCancel.jsp";
    }

    @RequestMapping(value = "/consumer/optout/cancel", method = RequestMethod.POST)
    public String cancel(@ModelAttribute CustomerAccount customerAccount,
    		Integer eventId, YukonUserContext userContext, ModelMap model) throws Exception {

        // Log consumer opt out cancel attempt
        OptOutEvent optOutEvent = optOutEventDao.getOptOutEventById(eventId);
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(optOutEvent.getInventoryId());
        accountEventLogService.optOutCancelAttempted(userContext.getYukonUser(), 
                                                              customerAccount.getAccountNumber(),
                                                              lmHardwareBase.getManufacturerSerialNumber(),
                                                              optOutEvent.getStartDate(),
                                                              optOutEvent.getStopDate(),
                                                              EventSource.CONSUMER);
        
    	// Make sure opt outs are enabled for the user
    	LiteYukonUser user = userContext.getYukonUser();
        if (!optOutStatusService.getOptOutEnabled(user).isCommunicationEnabled()) {
            return "consumer/optout/optOutDisabled.jsp";
        }
    	
    	// Make sure the event is the current user's event
    	helper.checkEventAgainstAccount(eventId, customerAccount.getAccountId());
    	
    	optOutService.cancelOptOut(Collections.singletonList(eventId), user);
        
    	return "redirect:/stars/consumer/optout";
    }

    private void validateStartDate(LocalDate startDate,
            YukonUserContext userContext, CustomerAccount customerAccount)
            throws StartDateException {
        String startDateErrorCode =
            optOutService.checkOptOutStartDate(customerAccount.getAccountId(),
                                               startDate, userContext, false);

        // Error found while checking the start date
        if (startDateErrorCode != null) {
            throw new StartDateException("yukon.dr.consumer.optout." + startDateErrorCode);
        }
    }

    private Map<Integer, OptOutCountHolder> getOptOutCountsForInventories(
            Iterable<DisplayableInventory> inventories, int customerAccountId) {
        Map<Integer, OptOutCountHolder> retVal = new HashMap<Integer, OptOutCountHolder>();
        for (DisplayableInventory inventory : inventories) {
            int inventoryId = inventory.getInventoryId();
            OptOutCountHolder holder = 
                optOutService.getCurrentOptOutCount(inventoryId, customerAccountId);
            retVal.put(inventoryId, holder);
        }
        return retVal;
    }

    private MessageSourceResolvable validateOptOutsRemaining(List<Integer> inventoryIds,
                                                             CustomerAccount customerAccount) {

        List<LiteInventoryBase> inventoryBases = inventoryBaseDao.getByIds(inventoryIds);

        // Check that there are opt outs remaining for each inventory being opted out
        for (LiteInventoryBase inventoryBase : inventoryBases) {
            OptOutCountHolder optOutCount = 
                optOutService.getCurrentOptOutCount(inventoryBase.getInventoryID(), 
                                                    customerAccount.getAccountId());

            if(!optOutCount.isOptOutsRemaining()){
                return new YukonMessageSourceResolvable("yukon.dr.consumer.optout.noOptOutsAvailableForThisInventory", 
                                                        inventoryBase.getDeviceLabel());
            }
        }

        return null;
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        binder.registerCustomEditor(LocalDate.class,
                                    "startDate",
                                    datePropertyEditorFactory.getLocalDatePropertyEditor(DateFormatEnum.DATE, userContext));
    }
}