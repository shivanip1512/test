package com.cannontech.web.stars.dr.operator;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.displayable.dao.DisplayableInventoryDao;
import com.cannontech.stars.dr.displayable.model.DisplayableInventory;
import com.cannontech.stars.dr.optout.dao.OptOutAdditionalDao;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.model.OptOutEventDto;
import com.cannontech.stars.dr.optout.model.ScheduledOptOutQuestion;
import com.cannontech.stars.dr.optout.service.OptOutRequest;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.consumer.OptOutControllerHelper;

@Controller
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT)
public class OptOutOperatorController {
	
    private OptOutService optOutService; 
    private OptOutEventDao optOutEventDao;
    private OptOutAdditionalDao optOutAdditionalDao;
    private CustomerAccountDao customerAccountDao;
	private StarsInventoryBaseDao starsInventoryBaseDao;
	private DisplayableInventoryDao displayableInventoryDao;
	private DateFormattingService dateFormattingService;
	private ECMappingDao ecMappingDao;
	protected YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @ModelAttribute("customerAccount")
    public CustomerAccount getCustomerAccount(HttpServletRequest request) {
    	
    	// Get the account info from the session
    	HttpSession session = request.getSession();
    	StarsCustAccountInformation accountInfo = 
            (StarsCustAccountInformation) session.getAttribute(ServletUtils.TRANSIENT_ATT_CUSTOMER_ACCOUNT_INFO);
    	
        int accountId = accountInfo.getStarsCustomerAccount().getAccountID();
		CustomerAccount account = customerAccountDao.getById(accountId);
        return account;
    }
    
    @RequestMapping(value = "/operator/optout", method = RequestMethod.GET)
    public String view(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
    		Integer eventId, YukonUserContext yukonUserContext, ModelMap map) {
    	
    	Calendar cal = Calendar.getInstance(yukonUserContext.getTimeZone());
    	Date currentDate = cal.getTime();
    	map.addAttribute("currentDate", currentDate);
    	
    	int accountId = customerAccount.getAccountId();
    	
    	//Get the list of current and scheduled opt outs
    	List<OptOutEventDto> currentOptOutList = 
    	    optOutEventDao.getCurrentOptOuts(accountId);
    	map.addAttribute("currentOptOutList", currentOptOutList);
    	
    	// Get the list of completed and canceled opt outs
        List<OptOutEventDto> previousOptOutList = 
            optOutEventDao.getOptOutHistoryForAccount(accountId, 6);
        map.addAttribute("previousOptOutList", previousOptOutList);
    	
        // Get the current counts for used opt outs and remaining allowed opt outs for each device
    	List<DisplayableInventory> displayableInventories =
            displayableInventoryDao.getDisplayableInventory(customerAccount.getAccountId());

    	boolean allOptedOut = true;
        boolean optOutsAvailable = false;
    	for(DisplayableInventory inventory : displayableInventories) {
    		if (!inventory.isCurrentlyOptedOut()) {
				allOptedOut = false;
			}
            if (inventory.isOptOutsRemaining()) {
                optOutsAvailable = true;
            }
    	}
    	map.addAttribute("displayableInventories", displayableInventories);
    	boolean noOptOutLimits = false;
    	if (displayableInventories.size() > 0) {
    		// Check the opt out limit from the first device - limits are set by login
    		// group and will therefore be the same for every device on an account
    	    DisplayableInventory inventory = displayableInventories.get(0);
			noOptOutLimits = inventory.getRemainingOptOuts() == OptOutService.NO_OPT_OUT_LIMIT;
    	}
    	map.addAttribute("noOptOutLimits", noOptOutLimits);
    	map.addAttribute("allOptedOut", allOptedOut);
        map.addAttribute("optOutsAvailable", optOutsAvailable);
    	
    	// The following is a hack and will be fixed by YUK-7269
    	LiteStarsEnergyCompany energyCompany = ecMappingDao.getCustomerAccountEC(customerAccount);
    	YukonSelectionList optOutPeriodList = 
    		energyCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD);
    	int maxOptOutDays = optOutPeriodList.getYukonListEntries().size();
    	map.addAttribute("maxOptOutDays", maxOptOutDays);
    	// end hack

    	return "operator/optout/optOut.jsp";
    }
    
    @RequestMapping(value = "/operator/optout/optout2", method = RequestMethod.POST)
    public String optout2(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
    		YukonUserContext yukonUserContext, String startDate, 
            int durationInDays, ModelMap map) {
    	
    	return "redirect:/operator/Consumer/OptOut2.jsp?startDate=" + startDate
				+ "&duration=" + durationInDays;
	}

    @RequestMapping(value = "/operator/optout/view2", method = RequestMethod.GET)
    public String view2(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            YukonUserContext yukonUserContext, String startDate, 
            int durationInDays, ModelMap map) {

        map.addAttribute("durationInDays", durationInDays);
        map.addAttribute("startDate", startDate);

        // Validate the start date
        Date startDateObj = parseDate(startDate, yukonUserContext);
        final Calendar now = dateFormattingService.getCalendar(yukonUserContext);
        TimeZone userTimeZone = yukonUserContext.getTimeZone();
        final Date today = TimeUtil.getMidnight(now.getTime(), userTimeZone);
        boolean isValidStartDate = isValidStartDate(startDateObj, today);
        if (!isValidStartDate) {
            MessageSourceResolvable error = new YukonMessageSourceResolvable(
                    "yukon.dr.operator.optoutError.invalidStartDate");
            map.addAttribute("error", error);
            return "operator/optout/optOutError.jsp";
        }

        boolean isSameDay = DateUtils.isSameDay(startDateObj, today);
        map.addAttribute("isSameDay", isSameDay);

        List<DisplayableInventory> displayableInventories =
            displayableInventoryDao.getDisplayableInventory(customerAccount.getAccountId());

        map.addAttribute("displayableInventories", displayableInventories);
        return "operator/optout/optOutList.jsp";
    }

    @RequestMapping(value = "/operator/optout/optoutQuestions", method = RequestMethod.POST)
    public String optoutQuestions(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
    		YukonUserContext yukonUserContext, String startDate, 
            int durationInDays, String jsonInventoryIds, ModelMap map) {
    	
    	List<String> questions = OptOutControllerHelper.getConfirmQuestions(
    			messageSourceResolver, 
    			yukonUserContext,
    			"yukon.dr.operator.optoutconfirm.question.");
    	if (questions.size() == 0) {
    		return "redirect:/spring/stars/consumer/optout/doOptOut?startDate=" + startDate
				+ "&duration=" + durationInDays + "&inventoryIds=" + jsonInventoryIds;
    	}
    	
		return "redirect:/operator/Consumer/OptOutQuestions.jsp?startDate=" + startDate
				+ "&duration=" + durationInDays + "&inventoryIds=" + jsonInventoryIds;
	}
    
    @RequestMapping("/operator/optout/confirm")
    public String confirm(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            YukonUserContext yukonUserContext, String startDate, int durationInDays,
            String jsonInventoryIds, ModelMap map) {
        
        map.addAttribute("startDate", startDate);
        map.addAttribute("durationInDays", durationInDays);
        
        List<String> questions = 
        	OptOutControllerHelper.getConfirmQuestions(
        			messageSourceResolver, 
        			yukonUserContext,
        			"yukon.dr.operator.optoutconfirm.question.");
        map.addAttribute("questions", questions);
        
        String escaped = StringEscapeUtils.escapeHtml(jsonInventoryIds);
        map.addAttribute("jsonInventoryIds", escaped);

        return "operator/optout/optOutConfirm.jsp";
    }
    
    @RequestMapping("/operator/optout/update")
    public String update(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            YukonUserContext yukonUserContext, String startDate, int durationInDays,
            String jsonInventoryIds, HttpServletRequest request, ModelMap map) throws Exception {
        
        String unEscaped = StringEscapeUtils.unescapeHtml(jsonInventoryIds);
        List<Integer> inventoryIds = OptOutControllerHelper.toInventoryIdList(unEscaped);
        
        this.checkInventoryAgainstAccount(inventoryIds, customerAccount);

        Date startDateObj = parseDate(startDate, yukonUserContext);

        MessageSourceResolvable result = new YukonMessageSourceResolvable(
			"yukon.dr.operator.optoutresult.success");
        
        // Validate the start date
        final Calendar now = dateFormattingService.getCalendar(yukonUserContext);
        TimeZone userTimeZone = yukonUserContext.getTimeZone();
		final Date today = TimeUtil.getMidnight(now.getTime(), userTimeZone);
        boolean isValidStartDate = isValidStartDate(startDateObj, today);
        if (!isValidStartDate) {
        	result = new YukonMessageSourceResolvable(
        			"yukon.dr.operator.optoutresult.invalidStartDate");
        } else {
        	
        	int hoursRemainingInDay = TimeUtil.getHoursTillMidnight(now.getTime(), userTimeZone);
            boolean isSameDay = DateUtils.isSameDay(startDateObj, today);
        	
        	String jsonQuestions = ServletRequestUtils.getStringParameter(
													        			request, 
													        			"jsonQuestions");
        	List<ScheduledOptOutQuestion> questionList = 
        		OptOutControllerHelper.toOptOutQuestionList(jsonQuestions);
        
	        OptOutRequest optOutRequest = new OptOutRequest();
	        if (isSameDay) {
	        	int extraHours = 0;
	        	// If durationInDays is 1 that means the rest of today only
	        	if(durationInDays > 1) {
	        		// Today counts as the first day
		        	extraHours = (durationInDays - 1) * 24;
	        	}
	        	optOutRequest.setDurationInHours(hoursRemainingInDay + extraHours);
	        	optOutRequest.setStartDate(null); // Same day OptOut's have null startDates.
	        } else {
		        optOutRequest.setStartDate(startDateObj);
		        optOutRequest.setDurationInHours(durationInDays * 24);
	        }
	        optOutRequest.setInventoryIdList(inventoryIds);
	        optOutRequest.setQuestions(questionList);
	        
	        LiteYukonUser user = yukonUserContext.getYukonUser();
	        optOutService.optOut(customerAccount, optOutRequest, user);
	        
        }
        
        map.addAttribute("result", result);
        return "redirect:/operator/Consumer/OptOut.jsp";
    }
    
    @RequestMapping(value = "/operator/optout/cancel", method = RequestMethod.POST)
    public String cancel(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
    		Integer eventId, YukonUserContext yukonUserContext) throws Exception {
    	
    	// Check that the inventory we're working with belongs to the current account
    	this.checkEventAgainstAccount(eventId, customerAccount);
    	
		LiteYukonUser user = yukonUserContext.getYukonUser();
		optOutService.cancelOptOut(Collections.singletonList(eventId), user);
    	
    	return "redirect:/operator/Consumer/OptOut.jsp";
    }

	@RequestMapping(value = "/operator/optOut/allowAnother", method = RequestMethod.POST)
    public String allowAnother(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
    		Integer inventoryId, YukonUserContext yukonUserContext) {
    	
    	// Check that the inventory we're working with belongs to the current account
    	this.checkInventoryAgainstAccount(Collections.singletonList(inventoryId), customerAccount);
    	
    	optOutAdditionalDao.addAdditonalOptOuts(
    			inventoryId, customerAccount.getAccountId(), 1);
    	
    	return "redirect:/operator/Consumer/OptOut.jsp";
    }
    
    @RequestMapping(value = "/operator/optout/repeat", method = RequestMethod.POST)
    public String repeat(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
    		Integer inventoryId, YukonUserContext yukonUserContext) throws Exception {
    	
    	// Check that the inventory we're working with belongs to the current account
    	this.checkInventoryAgainstAccount(Collections.singletonList(inventoryId), customerAccount);
    	
		optOutService.resendOptOut(
				inventoryId, 
				customerAccount.getAccountId(), 
				yukonUserContext.getYukonUser());
    	
    	return "redirect:/operator/Consumer/OptOut.jsp";
    }
    
    @RequestMapping(value = "/operator/optOut/resetToLimit", method = RequestMethod.POST)
    public String resetToLimit(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
    		Integer inventoryId, YukonUserContext yukonUserContext) {
    	
    	// Check that the inventory we're working with belongs to the current account
    	this.checkInventoryAgainstAccount(Collections.singletonList(inventoryId), customerAccount);
    	
    	optOutService.resetOptOutLimitForInventory(inventoryId, customerAccount.getAccountId());
    	
    	return "redirect:/operator/Consumer/OptOut.jsp";
    }
    
    /**
     * Helper method to make sure the inventory being used belongs to the current account 
     */
    private void checkInventoryAgainstAccount(List<Integer> inventoryIdList, CustomerAccount customerAccount){
    	
    	for(Integer inventoryId : inventoryIdList) {
    	
	    	LiteInventoryBase inventory = starsInventoryBaseDao.getById(inventoryId);
	    	int accountId = customerAccount.getAccountId();
			if(inventory.getAccountID() != accountId) {
	    		throw new NotAuthorizedException("The Inventory with id: " + inventoryId + 
	    				" does not belong to the current customer account with id: " + accountId);
	    	}
    	}
    }
    
    /**
     * Helper method to make sure the event being updated belongs to the current account
     */
    private void checkEventAgainstAccount(Integer eventId,
			CustomerAccount customerAccount) {

    	OptOutEvent event = optOutEventDao.getOptOutEventById(eventId);
    	
    	int accountId = customerAccount.getAccountId();
    	if(event.getCustomerAccountId() != accountId) {
    		throw new NotAuthorizedException("The Opt Out event with id: " + eventId + 
    				" does not belong to the current customer account with id: " + accountId);
    	}
	}

    private Date parseDate(String dateStr, YukonUserContext yukonUserContext) {
        try {
            return dateFormattingService.flexibleDateParser(dateStr,
                                                            yukonUserContext);
        } catch (ParseException e) {
        }
        return null;
    }

    private boolean isValidStartDate(Date startDate, Date todayDate) {
        if (startDate == null) return false;
        
        long startTime = startDate.getTime();
        long todayTime = todayDate.getTime();
        
        boolean result = startTime >= todayTime;
        return result;
    }

    @Autowired
    public void setOptOutService(OptOutService optOutService) {
		this.optOutService = optOutService;
	}
    
    @Autowired
    public void setOptOutAdditionalDao(OptOutAdditionalDao optOutAdditionalDao) {
		this.optOutAdditionalDao = optOutAdditionalDao;
	}
    
    @Autowired
    public void setOptOutEventDao(OptOutEventDao optOutEventDao) {
		this.optOutEventDao = optOutEventDao;
	}
    
    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
		this.customerAccountDao = customerAccountDao;
	}
    
    @Autowired
    public void setStarsInventoryBaseDao(
			StarsInventoryBaseDao starsInventoryBaseDao) {
		this.starsInventoryBaseDao = starsInventoryBaseDao;
	}
    
    @Autowired
    public void setDisplayableInventoryDao(
			DisplayableInventoryDao displayableInventoryDao) {
		this.displayableInventoryDao = displayableInventoryDao;
	}
    
    @Autowired
    public void setDateFormattingService(
			DateFormattingService dateFormattingService) {
		this.dateFormattingService = dateFormattingService;
	}
    
    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
		this.ecMappingDao = ecMappingDao;
	}
    
    @Autowired
    public void setMessageSourceResolver(
			YukonUserContextMessageSourceResolver messageSourceResolver) {
		this.messageSourceResolver = messageSourceResolver;
	}
    
}
