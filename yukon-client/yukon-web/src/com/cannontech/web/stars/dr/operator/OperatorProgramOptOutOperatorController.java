package com.cannontech.web.stars.dr.operator;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.displayable.dao.DisplayableInventoryDao;
import com.cannontech.stars.dr.displayable.model.DisplayableInventory;
import com.cannontech.stars.dr.optout.dao.OptOutAdditionalDao;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutCountHolder;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.model.OptOutEventDto;
import com.cannontech.stars.dr.optout.model.OptOutLimit;
import com.cannontech.stars.dr.optout.model.ScheduledOptOutQuestion;
import com.cannontech.stars.dr.optout.service.OptOutRequest;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.consumer.OptOutControllerHelper;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;

@Controller
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT)
public class OperatorProgramOptOutOperatorController {
    
    private OptOutService optOutService; 
    private OptOutEventDao optOutEventDao;
    private OptOutAdditionalDao optOutAdditionalDao;
    private CustomerAccountDao customerAccountDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private DisplayableInventoryDao displayableInventoryDao;
    private DateFormattingService dateFormattingService;
    private RolePropertyDao rolePropertyDao;
    protected YukonUserContextMessageSourceResolver messageSourceResolver;

    private static class StartDateException extends Exception {
        private final static long serialVersionUID = 1L;

        private StartDateException(String message) {
            super(message);
        }
    };

    @ModelAttribute("customerAccount")
    public CustomerAccount getCustomerAccount(HttpServletRequest request) throws ServletRequestBindingException {
        
        // Get the account info from the session
        int accountId = ServletRequestUtils.getRequiredIntParameter(request, "accountId");
        CustomerAccount account = customerAccountDao.getById(accountId);
        return account;
    }
    
    @RequestMapping(value = "/operator/program/optOut")
    public String view(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
    				   int accountId,
    				   int energyCompanyId,
                       Integer eventId, 
                       YukonUserContext yukonUserContext, 
                       ModelMap modelMap,
                       AccountInfoFragment accountInfoFragment) {
        
        LiteYukonUser user = yukonUserContext.getYukonUser();        
        Calendar cal = Calendar.getInstance(yukonUserContext.getTimeZone());
        Date currentDate = cal.getTime();
        modelMap.addAttribute("currentDate", currentDate);
        
        //Get the list of current and scheduled opt outs
        List<OptOutEventDto> currentOptOutList =  optOutEventDao.getCurrentOptOuts(accountId);
        modelMap.addAttribute("currentOptOutList", currentOptOutList);
        
        // Get the list of completed and canceled opt outs
        List<OptOutEventDto> previousOptOutList =  optOutEventDao.getOptOutHistoryForAccount(accountId, 6);
        modelMap.addAttribute("previousOptOutList", previousOptOutList);
        
        // Get the current counts for used opt outs and remaining allowed opt outs for each device
        List<DisplayableInventory> displayableInventories = displayableInventoryDao.getDisplayableInventory(customerAccount.getAccountId());
        Map<Integer, OptOutCountHolder> optOutCounts = getOptOutCountsForInventories(displayableInventories, customerAccount.getAccountId());

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
        modelMap.addAttribute("displayableInventories", displayableInventories);
        modelMap.addAttribute("optOutCounts", optOutCounts);
        boolean noOptOutLimits = false;
        if (displayableInventories.size() > 0) {
            // Check the opt out limit from the first device - limits are set by login
            // group and will therefore be the same for every device on an account
            DisplayableInventory inventory = displayableInventories.get(0);
            noOptOutLimits = optOutCounts.get(inventory.getInventoryId()).getRemainingOptOuts() == OptOutService.NO_OPT_OUT_LIMIT;
        }
        modelMap.addAttribute("noOptOutLimits", noOptOutLimits);
        
        OptOutLimit currentOptOutLimit = optOutService.getCurrentOptOutLimit(accountId);
        int optOutLimit = OptOutService.NO_OPT_OUT_LIMIT;
        if(currentOptOutLimit != null) {
            optOutLimit = currentOptOutLimit.getLimit();
        }
        
        modelMap.addAttribute("optOutLimit", optOutLimit);
        modelMap.addAttribute("allOptedOut", allOptedOut);
        modelMap.addAttribute("optOutsAvailable", optOutsAvailable);
        
        List<Integer> optOutPeriodList = optOutService.getAvailableOptOutPeriods(user);
        modelMap.addAttribute("optOutPeriodList", optOutPeriodList);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "operator/program/optOut/optOut.jsp";
    }

    @RequestMapping(value = "/operator/program/optOut/view2")
    public String view2(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
    					int accountId,
    					int energyCompanyId,
            			String startDate, 
            			int durationInDays, 
            			String error, 
            			ModelMap modelMap,
            			YukonUserContext yukonUserContext,
            			AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {

        
    	modelMap.addAttribute("durationInDays", durationInDays);
    	modelMap.addAttribute("startDate", startDate);
    	modelMap.addAttribute("error", error);

        // Validate the start date
        Date startDateObj = parseDate(startDate, yukonUserContext);
        TimeZone userTimeZone = yukonUserContext.getTimeZone();
        final Date today = TimeUtil.getMidnight(new Date(), userTimeZone);
        try {
            validateStartDate(startDateObj, today, yukonUserContext);
        } catch (StartDateException exception) {
            MessageSourceResolvable errorMsg = new YukonMessageSourceResolvable(exception.getMessage());
            modelMap.addAttribute("error", errorMsg);
            return "operator/program/optOut/optOutError.jsp";
        }

        boolean isSameDay = TimeUtil.isSameDay(startDateObj, today, yukonUserContext.getTimeZone());
        modelMap.addAttribute("isSameDay", isSameDay);

        List<DisplayableInventory> displayableInventories = displayableInventoryDao.getDisplayableInventory(customerAccount.getAccountId());
        Map<Integer, OptOutCountHolder> optOutCounts = getOptOutCountsForInventories(displayableInventories, customerAccount.getAccountId());

        modelMap.addAttribute("displayableInventories", displayableInventories);
        modelMap.addAttribute("optOutCounts", optOutCounts);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "operator/program/optOut/optOutList.jsp";
    }

    @RequestMapping(value = "/operator/program/optOut/optOutQuestions")
    public String optOutQuestions(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
						    	  int accountId,
								  int energyCompanyId,
            					  String startDate, 
            					  int durationInDays, 
            					  String jsonInventoryIds, 
            					  ModelMap modelMap,
            					  YukonUserContext yukonUserContext,
            					  AccountInfoFragment accountInfoFragment) {

        
        String unEscaped = StringEscapeUtils.unescapeHtml(jsonInventoryIds);
        List<Integer> inventoryIds = OptOutControllerHelper.toInventoryIdList(unEscaped);
        if (inventoryIds.size() == 0) {
        	
        	modelMap.addAttribute("startDate", startDate);
        	modelMap.addAttribute("duration", durationInDays);
        	modelMap.addAttribute("error", "yukon.dr.operator.optOutlist.noInventorySelected");
            return "operator/program/optOut/view2.jsp";
        }

        List<String> questions = OptOutControllerHelper.getConfirmQuestions(
                messageSourceResolver, 
                yukonUserContext,
                "yukon.dr.operator.optOutconfirm.question.");
        
        if (questions.size() == 0) {
            return "redirect:/spring/stars/operator/program/optOut/update?accountId="+customerAccount.getAccountId()+"&energyCompanyId="+energyCompanyId
            + "&startDate=" + startDate + "&durationInDays=" + durationInDays + "&jsonInventoryIds=" + jsonInventoryIds;
        }
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "operator/program/optOut/confirm";
    }
    
    @RequestMapping("/operator/program/optOut/confirm")
    public String confirm(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
    					  int accountId,
    					  int energyCompanyId,
            			  String startDate, 
            			  int durationInDays,
            			  String jsonInventoryIds, 
            			  ModelMap modelMap,
            			  YukonUserContext yukonUserContext,
            			  AccountInfoFragment accountInfoFragment) {

        
    	modelMap.addAttribute("startDate", startDate);
    	modelMap.addAttribute("durationInDays", durationInDays);
        
        List<String> questions = 
            OptOutControllerHelper.getConfirmQuestions(
                    messageSourceResolver, 
                    yukonUserContext,
                    "yukon.dr.operator.optOutconfirm.question.");
        modelMap.addAttribute("questions", questions);
        
        String escaped = StringEscapeUtils.escapeHtml(jsonInventoryIds);
        modelMap.addAttribute("jsonInventoryIds", escaped);

        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "operator/program/optOut/optOutConfirm.jsp";
    }
    
    @RequestMapping(value = "/operator/program/optOut/optOutHistory", method = RequestMethod.GET)
    public String optOutHistory(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
    							int accountId,
    							int energyCompanyId,
                                ModelMap modelMap,
                                AccountInfoFragment accountInfoFragment) {
        
        
        // Get the list of completed and canceled opt outs
        List<OptOutEventDto> previousOptOutList = optOutEventDao.getOptOutHistoryForAccount(customerAccount.getAccountId());

        modelMap.addAttribute("previousOptOutList", previousOptOutList);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "operator/program/optOut/optOutHistory.jsp";
    }
    
    @RequestMapping("/operator/program/optOut/update")
    public String update(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
    					int accountId,
    					int energyCompanyId,
            			String startDate, 
            			int durationInDays,
            			String jsonInventoryIds, 
            			HttpServletRequest request, 
            			ModelMap modelMap,
            			YukonUserContext yukonUserContext,
            			AccountInfoFragment accountInfoFragment) throws Exception {
        
        String unEscaped = StringEscapeUtils.unescapeHtml(jsonInventoryIds);
        List<Integer> inventoryIds = OptOutControllerHelper.toInventoryIdList(unEscaped);
        
        this.checkInventoryAgainstAccount(inventoryIds, customerAccount);

        Date startDateObj = parseDate(startDate, yukonUserContext);

        MessageSourceResolvable result = 
            new YukonMessageSourceResolvable("yukon.dr.operator.optOutresult.success");
        
        // Validate the start date
        final Date now = new Date();
        TimeZone userTimeZone = yukonUserContext.getTimeZone();
        final Date today = TimeUtil.getMidnight(now, userTimeZone);
        try {
            validateStartDate(startDateObj, today, yukonUserContext);
        } catch (StartDateException exception) {
        	modelMap.addAttribute("startDate", startDate);
        	modelMap.addAttribute("duration", durationInDays);
        	modelMap.addAttribute("error", exception.getMessage());
            result = new YukonMessageSourceResolvable(exception.getMessage());
            modelMap.addAttribute("result", result);
            return "operator/program/optOut/optOut.jsp";
        }

        boolean isSameDay = TimeUtil.isSameDay(startDateObj,
                                               today,
                                               yukonUserContext.getTimeZone());

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
            optOutRequest.setStartDate(startDateObj);
            optOutRequest.setDurationInHours(durationInDays * 24);
        }
        optOutRequest.setInventoryIdList(inventoryIds);
        optOutRequest.setQuestions(questionList);

        LiteYukonUser user = yukonUserContext.getYukonUser();
        optOutService.optOut(customerAccount, optOutRequest, user);
        
        modelMap.addAttribute("result", result);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "redirect:/spring/stars/operator/program/optOut?accountId="+customerAccount.getAccountId();
    }
    
    @RequestMapping(value = "/operator/program/optOut/cancel")
    public String cancel(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
    					int accountId,
    					int energyCompanyId,
                         Integer eventId, 
                         ModelMap modelMap,
                         YukonUserContext yukonUserContext,
                         AccountInfoFragment accountInfoFragment) throws Exception {
        
        // Check that the inventory we're working with belongs to the current account
        this.checkEventAgainstAccount(eventId, customerAccount);
        
        LiteYukonUser user = yukonUserContext.getYukonUser();
        optOutService.cancelOptOut(Collections.singletonList(eventId), user);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "redirect:/spring/stars/operator/program/optOut?accountId="+customerAccount.getAccountId();
    }

    @RequestMapping(value = "/operator/program/optOut/allowAnother")
    public String allowAnother(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
    						   int accountId,
    						   int energyCompanyId,
                               Integer inventoryId,  
                               ModelMap modelMap,
                               AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {
        
        // Check that the inventory we're working with belongs to the current account
        this.checkInventoryAgainstAccount(Collections.singletonList(inventoryId), customerAccount);
        
        optOutAdditionalDao.addAdditonalOptOuts(inventoryId, customerAccount.getAccountId(), 1);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "redirect:/spring/stars/operator/program/optOut?accountId="+customerAccount.getAccountId();
    }
    
    @RequestMapping(value = "/operator/program/optOut/repeat")
    public String repeat(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
                         Integer inventoryId, 
                         int accountId,
						 int energyCompanyId,
                         ModelMap modelMap,
                         YukonUserContext yukonUserContext,
                         AccountInfoFragment accountInfoFragment) throws Exception {
        
        
        // Check that the inventory we're working with belongs to the current account
        this.checkInventoryAgainstAccount(Collections.singletonList(inventoryId), customerAccount);
        
        optOutService.resendOptOut(
                inventoryId, 
                customerAccount.getAccountId(), 
                yukonUserContext.getYukonUser());
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "redirect:/spring/stars/operator/program/optOut?accountId="+customerAccount.getAccountId();
    }
    
    @RequestMapping(value = "/operator/program/optOut/resetToLimit")
    public String resetToLimit(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
    						   int accountId,
    						   int energyCompanyId,
                               Integer inventoryId, 
                               ModelMap modelMap,
                               AccountInfoFragment accountInfoFragment) {

        
        // Check that the inventory we're working with belongs to the current account
        this.checkInventoryAgainstAccount(Collections.singletonList(inventoryId), customerAccount);
        
        optOutService.resetOptOutLimitForInventory(inventoryId, customerAccount.getAccountId());
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "redirect:/spring/stars/operator/program/optOut?accountId="+customerAccount.getAccountId();
    }
    
    /**
     * Helper method to make sure the inventory being used belongs to the current account 
     */
    private void checkInventoryAgainstAccount(List<Integer> inventoryIdList, CustomerAccount customerAccount){
        
        for(Integer inventoryId : inventoryIdList) {
        
            LiteInventoryBase inventory = starsInventoryBaseDao.getByInventoryId(inventoryId);
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
            // caller requires a date so null is treated as an error
        }
        return null;
    }

    private void validateStartDate(Date startDate, Date todayDate,
            YukonUserContext userContext) throws StartDateException {
        // this shouldn't happen unless the user is hacking the UI
        if (startDate == null) throw new RuntimeException("empty start date");

        long startTime = startDate.getTime();
        long todayTime = todayDate.getTime();

        if (startTime < todayTime) {
            throw new StartDateException("yukon.dr.operator.optout.startDateTooEarly");
        }

        Calendar cal = dateFormattingService.getCalendar(userContext);
        cal.setTime(todayDate);
        cal.add(Calendar.YEAR, 1);
        long yearInFuture = cal.getTimeInMillis();
        if (startTime > yearInFuture) {
            throw new StartDateException("yukon.dr.operator.optout.startDateTooLate");
        }
        
        boolean optOutTodayOnly = rolePropertyDao.getPropertyBooleanValue(
                YukonRoleProperty.OPERATOR_OPT_OUT_TODAY_ONLY, userContext.getYukonUser());
        if(optOutTodayOnly) {
            cal.setTime(todayDate);
            cal.add(Calendar.DAY_OF_YEAR, 1);
            long dayInFuture = cal.getTimeInMillis();
            
            if (startTime > dayInFuture) {
                throw new IllegalArgumentException("Start date must be today");
            }
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
    public void setMessageSourceResolver(
            YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
}
