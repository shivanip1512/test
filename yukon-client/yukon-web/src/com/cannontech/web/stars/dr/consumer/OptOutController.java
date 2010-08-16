package com.cannontech.web.stars.dr.consumer;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.displayable.model.DisplayableInventory;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutCountHolder;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.model.OptOutEventDto;
import com.cannontech.stars.dr.optout.model.ScheduledOptOutQuestion;
import com.cannontech.stars.dr.optout.service.OptOutRequest;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.stars.dr.optout.service.OptOutStatusService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_PROGRAMS_OPT_OUT)
@Controller
public class OptOutController extends AbstractConsumerController {
	
	private static int MAX_NUMBER_OF_OPT_OUT_HISTORY = 6;
	
	private AccountEventLogService accountEventLogService;
    private RolePropertyDao rolePropertyDao;
    private DateFormattingService dateFormattingService;
    private LMHardwareBaseDao lmHardwareBaseDao;
    private OptOutService optOutService; 
    private OptOutEventDao optOutEventDao;
    private OptOutStatusService optOutStatusService;

    private static class StartDateException extends Exception {
        private final static long serialVersionUID = 1L;

        private StartDateException(String message) {
            super(message);
        }
    };

    @RequestMapping(value = "/consumer/optout", method = RequestMethod.GET)
    public String view(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            YukonUserContext yukonUserContext, ModelMap map) {
    	
    	LiteYukonUser user = yukonUserContext.getYukonUser();
    	if (!optOutStatusService.getOptOutEnabled(user)) {
    	    return "consumer/optout/optOutDisabled.jsp";
    	}
    	
        Calendar cal = Calendar.getInstance(yukonUserContext.getTimeZone());
    	Date currentDate = cal.getTime();
    	map.addAttribute("currentDate", currentDate);

    	int accountId = customerAccount.getAccountId();

    	// Get the list of current and scheduled opt outs
		List<OptOutEventDto> currentOptOutList = 
    		optOutEventDao.getCurrentOptOuts(accountId);
    	map.addAttribute("currentOptOutList", currentOptOutList);

    	// Get the current counts for used opt outs and remaining allowed opt outs for each device
    	List<DisplayableInventory> displayableInventories =
            displayableInventoryDao.getDisplayableInventory(customerAccount.getAccountId());
        Map<Integer, OptOutCountHolder> optOutCounts =
            getOptOutCountsForInventories(displayableInventories, customerAccount.getAccountId());

    	boolean allOptedOut = true;
    	boolean optOutsAvailable = false;
    	for(DisplayableInventory inventory : displayableInventories) {
    	    if (!inventory.isCurrentlyOptedOut()) {
				allOptedOut = false;
			}
    	    if (optOutCounts.get(inventory.getInventoryId()).isOptOutsRemaining()) {
    	        optOutsAvailable = true;
    	    }
    	}
    	map.addAttribute("displayableInventories", displayableInventories);
        map.addAttribute("optOutCounts", optOutCounts);

    	// Get the list of completed and canceled opt outs
    	List<OptOutEventDto> previousOptOutList = 
    		optOutEventDao.getOptOutHistoryForAccount(accountId, MAX_NUMBER_OF_OPT_OUT_HISTORY);
    	map.addAttribute("previousOptOutList", previousOptOutList);
    	map.addAttribute("allOptedOut", allOptedOut);
    	map.addAttribute("optOutsAvailable", optOutsAvailable);
    	
    	List<Integer> optOutPeriodList = optOutService.getAvailableOptOutPeriods(user);
    	map.addAttribute("optOutPeriodList", optOutPeriodList);
    	    	
    	return "consumer/optout/optOut.jsp";
    }

    @RequestMapping(value = "/consumer/optout/view2")
    public String view2(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            YukonUserContext yukonUserContext, String startDate, 
            int durationInDays, String error, ModelMap map) {

    	final LiteYukonUser user = yukonUserContext.getYukonUser();
        if (!optOutStatusService.getOptOutEnabled(user)) {
            return "consumer/optout/optOutDisabled.jsp";
        }

        final boolean hasDeviceSelection =
            rolePropertyDao.checkProperty(YukonRoleProperty.RESIDENTIAL_OPT_OUT_DEVICE_SELECTION, user);

        map.addAttribute("durationInDays", durationInDays);
        map.addAttribute("startDate", startDate);
        map.addAttribute("error", error);

        // Validate the start date
        Date startDateObj = parseDate(startDate, yukonUserContext);
        TimeZone userTimeZone = yukonUserContext.getTimeZone();
        final Date today = TimeUtil.getMidnight(new Date(), userTimeZone);
        try {
            validateStartDate(startDateObj, today, durationInDays, yukonUserContext, customerAccount);
        } catch (StartDateException exception) {
            MessageSourceResolvable message = new YukonMessageSourceResolvable(
                    exception.getMessage());
            map.addAttribute("result", message);
            return "consumer/optout/optOutResult.jsp";
        }

        boolean isSameDay = TimeUtil.isSameDay(startDateObj, today, yukonUserContext.getTimeZone());
        map.addAttribute("isSameDay", isSameDay);

        List<DisplayableInventory> displayableInventories =
            displayableInventoryDao.getDisplayableInventory(customerAccount.getAccountId());
        List<DisplayableInventory> optOutableInventories = new ArrayList<DisplayableInventory>();
        Map<Integer, OptOutCountHolder> optOutCounts =
            getOptOutCountsForInventories(displayableInventories, customerAccount.getAccountId());

        for (DisplayableInventory inventory : displayableInventories) {
        	if (optOutCounts.get(inventory.getInventoryId()).isOptOutsRemaining()) {
        	    optOutableInventories.add(inventory);
        	}
        }

        boolean blanketDevices = (optOutableInventories.size() < 2 && !hasDeviceSelection);
        if (blanketDevices) {
            final JSONArray jsonArray = new JSONArray();

            for (final DisplayableInventory inventory : optOutableInventories) {
        		jsonArray.put(inventory.getInventoryId());
            }

            String jsonInventoryIds = StringEscapeUtils.escapeHtml(jsonArray.toString());
            map.addAttribute("jsonInventoryIds", jsonInventoryIds);

            return "redirect:/spring/stars/consumer/optout/confirm";
        }

        map.addAttribute("displayableInventories", displayableInventories);
        map.addAttribute("optOutCounts", optOutCounts);
        return "consumer/optout/optOutList.jsp";
    }

    @RequestMapping("/consumer/optout/confirm")
    public String confirm(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            YukonUserContext yukonUserContext, String startDate, int durationInDays,
            String jsonInventoryIds, ModelMap map) {
        
    	LiteYukonUser user = yukonUserContext.getYukonUser();
        if (!optOutStatusService.getOptOutEnabled(user)) {
            return "consumer/optout/optOutDisabled.jsp";
        }

        map.addAttribute("startDate", startDate);
        map.addAttribute("durationInDays", durationInDays);

        List<Integer> inventoryIds = getInventoryIds(yukonUserContext, jsonInventoryIds);
        if (inventoryIds.size() == 0) {
            map.addAttribute("error", "yukon.dr.consumer.optoutlist.noInventorySelected");
            return "redirect:/spring/stars/consumer/optout/view2";
        }
        validateInventoryIds(inventoryIds, customerAccount);
        
        String escaped = StringEscapeUtils.escapeHtml(jsonInventoryIds);
        map.addAttribute("jsonInventoryIds", escaped);

        List<String> questions = OptOutControllerHelper.getConfirmQuestions(
        		messageSourceResolver, 
        		yukonUserContext,
        		"yukon.dr.consumer.optoutconfirm.question.");
        if (questions.size() == 0) return "redirect:/spring/stars/consumer/optout/update";

        map.addAttribute("questions", questions);
        return "consumer/optout/optOutConfirm.jsp";
    }
    
    @RequestMapping("/consumer/optout/update")
    public String update(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            YukonUserContext userContext, String startDate, int durationInDays,
            String jsonInventoryIds, HttpServletRequest request, ModelMap map) throws Exception {


        Date startDateObj = parseDate(startDate, userContext);
        Instant optOutStartDateMidnight = new Instant(startDateObj);
        List<Integer> inventoryIds = getInventoryIds(userContext, jsonInventoryIds);
        for (int inventoryId : inventoryIds) {
            LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
            accountEventLogService.optOutAttemptedByConsumer(userContext.getYukonUser(), 
                                                             customerAccount.getAccountNumber(), 
                                                             lmHardwareBase.getManufacturerSerialNumber(),
                                                             optOutStartDateMidnight);
        }
        
    	if (!optOutStatusService.getOptOutEnabled(userContext.getYukonUser())) {
            return "consumer/optout/optOutDisabled.jsp";
        }
    	validateInventoryIds(inventoryIds, customerAccount);

        MessageSourceResolvable result = new YukonMessageSourceResolvable(
			"yukon.dr.consumer.optoutresult.success");
        
        // Validate the start date
        TimeZone userTimeZone = userContext.getTimeZone();
        final Date now = new Date();
		final Date today = TimeUtil.getMidnight(now, userTimeZone);
		try {
		    validateStartDate(startDateObj, today, durationInDays, userContext, customerAccount);
        } catch (StartDateException exception) {
        	result = new YukonMessageSourceResolvable(
        			"yukon.dr.consumer.optoutresult.invalidStartDate");

            map.addAttribute("result", result);
            return "consumer/optout/optOutResult.jsp";
        }

        boolean isSameDay = TimeUtil.isSameDay(startDateObj,
                                               today,
                                               userContext.getTimeZone());

        String jsonQuestions = ServletRequestUtils.getStringParameter(request,
                                                                      "jsonQuestions");
        List<ScheduledOptOutQuestion> questionList = OptOutControllerHelper.toOptOutQuestionList(jsonQuestions);

        OptOutRequest optOutRequest = new OptOutRequest();
        if (isSameDay) {
            int extraHours = 0;
            // If durationInDays is 1 that means the rest of today only
            if (durationInDays > 1) {
                // Today counts as the first day
                extraHours = (durationInDays - 1) * 24;
            }
            
            int hoursRemainingInDay = TimeUtil.getHoursTillMidnight(now, userTimeZone);
            optOutRequest.setDurationInHours(hoursRemainingInDay + extraHours);
            optOutRequest.setStartDate(null); // Same day OptOut's have null
                                              // startDates.
        } else {
            Instant start = new Instant(startDateObj);
            Period optOutPeriod = Period.days(durationInDays);
            Interval optOutInterval = new Interval(start, optOutPeriod);
            optOutRequest.setStartDate(start);
            optOutRequest.setDurationInHours(optOutInterval.toDuration()
                                                           .toPeriod()
                                                           .toStandardHours()
                                                           .getHours());
        }
        optOutRequest.setInventoryIdList(inventoryIds);
        optOutRequest.setQuestions(questionList);

        optOutService.optOut(customerAccount, optOutRequest, userContext.getYukonUser());

        map.addAttribute("result", result);
        return "consumer/optout/optOutResult.jsp";
    }

    @RequestMapping(value = "/consumer/optout/confirmCancel")
    public String confirmCancel(
            @ModelAttribute("customerAccount") CustomerAccount customerAccount,
            Integer eventId, YukonUserContext yukonUserContext, ModelMap model) {
        // Make sure the event is the current user's event
        checkEventAgainstAccount(eventId, customerAccount);

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
            return "/spring/stars/consumer/optout";
        }
        model.addAttribute("optOut", optOut);

        return "consumer/optout/confirmCancel.jsp";
    }

    @RequestMapping(value = "/consumer/optout/cancel", method = RequestMethod.POST)
    public String cancel(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
    		Integer eventId, YukonUserContext userContext, ModelMap map) throws Exception {

        // Log consumer opt out cancel attempt
        OptOutEvent optOutEvent = optOutEventDao.getOptOutEventById(eventId);
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(optOutEvent.getInventoryId());
        accountEventLogService.optOutCancelAtteptedByConsumer(userContext.getYukonUser(), 
                                                              customerAccount.getAccountNumber(),
                                                              lmHardwareBase.getManufacturerSerialNumber(),
                                                              optOutEvent.getStartDate(),
                                                              optOutEvent.getStopDate());
        
    	// Make sure opt outs are enabled for the user
    	LiteYukonUser user = userContext.getYukonUser();
        if (!optOutStatusService.getOptOutEnabled(user)) {
            return "consumer/optout/optOutDisabled.jsp";
        }
    	
    	// Make sure the event is the current user's event
    	this.checkEventAgainstAccount(eventId, customerAccount);
    	
    	optOutService.cancelOptOut(Collections.singletonList(eventId), user);
        
    	return "redirect:/spring/stars/consumer/optout";
    }

    private Date parseDate(String dateStr, YukonUserContext yukonUserContext) {
        try {
            return dateFormattingService.flexibleDateParser(dateStr, yukonUserContext);
        } catch (ParseException e) {
            // caller requires a date so null is treated as an error
        }
        return null;
    }

    private void validateStartDate(Date startDate, Date todayDate, int durationInDays,
                                   YukonUserContext userContext, CustomerAccount customerAccount) 
    throws StartDateException {
        
        LocalDate startLocalDate = new LocalDate(startDate, userContext.getJodaTimeZone());
        String startDateErrorCode = 
            optOutService.checkOptOutStartDate(customerAccount.getAccountId(), startLocalDate, 
                                               userContext, false);

        // Error found while checking the start date
        if (startDateErrorCode != null) {
            throw new StartDateException("yukon.dr.consumer.optout."+startDateErrorCode);
        }
    }

    /**
     * Helper method to make sure the event being updated belongs to the current account
     */
    private void checkEventAgainstAccount(Integer eventId, CustomerAccount customerAccount) {

    	OptOutEvent event = optOutEventDao.getOptOutEventById(eventId);
    	
    	int accountId = customerAccount.getAccountId();
    	if(event.getCustomerAccountId() != accountId) {
    		throw new NotAuthorizedException("The user is not authorized to modify the " +
    				"Opt Out event with id: " + eventId);
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

    private List<Integer> getInventoryIds(YukonUserContext yukonUserContext, String jsonInventoryIds) {
        String unEscaped = StringEscapeUtils.unescapeHtml(jsonInventoryIds);
        List<Integer> inventoryIds = OptOutControllerHelper.toInventoryIdList(unEscaped);

        accountCheckerService.checkInventory(yukonUserContext.getYukonUser(),
                                             inventoryIds.toArray(new Integer[inventoryIds.size()]));
        return inventoryIds;
    }

    private void validateInventoryIds(List<Integer> inventoryIds,
            CustomerAccount customerAccount) {
        // Check that there are opt outs remaining for each inventory being
        // opted out
        for (int inventoryId : inventoryIds) {
            OptOutCountHolder optOutCount = 
                optOutService.getCurrentOptOutCount(inventoryId, customerAccount.getAccountId());
            if(!optOutCount.isOptOutsRemaining()) {
                throw new NotAuthorizedException("There are no remaining opt outs for " +
                        "inventory with id: " + inventoryId);
            }
        }
    }

    @Autowired
    public void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        this.accountEventLogService = accountEventLogService;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }

    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    
    @Autowired
    public void setLmHardwareBaseDao(LMHardwareBaseDao lmHardwareBaseDao) {
        this.lmHardwareBaseDao = lmHardwareBaseDao;
    }
    
    @Autowired
    public void setOptOutService(OptOutService optOutService) {
		this.optOutService = optOutService;
	}
    
    @Autowired
    public void setOptOutEventDao(OptOutEventDao optOutEventDao) {
		this.optOutEventDao = optOutEventDao;
	}
    
    @Autowired
    public void setOptOutStatusService(OptOutStatusService optOutStatusService) {
		this.optOutStatusService = optOutStatusService;
	}
    
}
