package com.cannontech.web.stars.dr.consumer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

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

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.displayable.model.DisplayableInventory;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutCountHolder;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.model.OptOutEventDto;
import com.cannontech.stars.dr.optout.model.ScheduledOptOutQuestion;
import com.cannontech.stars.dr.optout.service.OptOutRequest;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.stars.dr.optout.service.OptOutStatusService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.OptOutCountDto;

@CheckRole(ResidentialCustomerRole.ROLEID)
@CheckRoleProperty(ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_OPT_OUT)
@Controller
public class OptOutController extends AbstractConsumerController {
	
	private static int MAX_NUMBER_OF_OPT_OUT_HISTORY = 6;
	
    private AuthDao authDao;
    private DateFormattingService dateFormattingService;
    private OptOutService optOutService; 
    private OptOutEventDao optOutEventDao;
    private OptOutStatusService optOutStatusService;
    
    @RequestMapping(value = "/consumer/optout", method = RequestMethod.GET)
    public String view(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            YukonUserContext yukonUserContext, ModelMap map) {
    	
    	LiteYukonUser user = yukonUserContext.getYukonUser();
    	this.checkOptOutsEnabled(user);
    	
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

    	List<OptOutCountDto> optOutCountList = new ArrayList<OptOutCountDto>();
    	
    	for(DisplayableInventory inventory : displayableInventories) {
    		int inventoryId = inventory.getInventoryId();
    		OptOutCountHolder holder = 
    			optOutService.getCurrentOptOutCount(inventoryId, accountId);
    		
    		OptOutCountDto optOutCountDto = new OptOutCountDto();
    		optOutCountDto.setInventory(inventory);
    		optOutCountDto.setUsedOptOuts(holder.getUsedOptOuts());
    		optOutCountDto.setRemainingOptOuts(holder.getRemainingOptOuts());
    		
    		optOutCountList.add(optOutCountDto);
    	}
    	map.addAttribute("optOutCountList", optOutCountList);
    	
    	// Get the list of completed and canceled opt outs
    	List<OptOutEventDto> previousOptOutList = 
    		optOutEventDao.getOptOutHistoryForAccount(accountId, MAX_NUMBER_OF_OPT_OUT_HISTORY);
    	map.addAttribute("previousOptOutList", previousOptOutList);
    	
    	
    	return "consumer/optout/optOut.jsp";
    }

    @RequestMapping(value = "/consumer/optout/view2", method = RequestMethod.POST)
    public String view2(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            YukonUserContext yukonUserContext, String startDate, 
            int durationInDays, ModelMap map) {
        
    	final LiteYukonUser user = yukonUserContext.getYukonUser();
    	this.checkOptOutsEnabled(user);
    	
        final boolean hasDeviceSelection =
            authDao.checkRoleProperty(user, ResidentialCustomerRole.OPT_OUT_DEVICE_SELECTION);
        
        map.addAttribute("durationInDays", durationInDays);
        map.addAttribute("startDate", startDate);
        
        List<DisplayableInventory> displayableInventories =
            displayableInventoryDao.getDisplayableInventory(customerAccount.getAccountId());
        
        List<DisplayableInventory> optOutableInventories = new ArrayList<DisplayableInventory>();
        for(DisplayableInventory inventory : displayableInventories) {
        	// Only add the inventories that have opt outs remaining
        	OptOutCountHolder optOutCount = 
        		optOutService.getCurrentOptOutCount(
        				inventory.getInventoryId(), customerAccount.getAccountId());
        	if(optOutCount.hasOptOutsRemaining()) {
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
        
        map.addAttribute("displayableInventories", optOutableInventories);
        return "consumer/optout/optOutList.jsp";
    }
    
    @RequestMapping("/consumer/optout/confirm")
    public String confirm(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            YukonUserContext yukonUserContext, String startDate, int durationInDays,
            String jsonInventoryIds, ModelMap map) {
        
    	LiteYukonUser user = yukonUserContext.getYukonUser();
    	this.checkOptOutsEnabled(user);
    	
        map.addAttribute("startDate", startDate);
        map.addAttribute("durationInDays", durationInDays);
        
        String escaped = StringEscapeUtils.escapeHtml(jsonInventoryIds);
        map.addAttribute("jsonInventoryIds", escaped);

        List<String> questions = OptOutControllerHelper.getConfirmQuestions(messageSourceResolver, yukonUserContext);
        if (questions.size() == 0) return "redirect:/spring/stars/consumer/optout/update";

        map.addAttribute("questions", questions);
        return "consumer/optout/optOutConfirm.jsp";
    }
    
    @RequestMapping("/consumer/optout/update")
    public String update(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            YukonUserContext yukonUserContext, String startDate, int durationInDays,
            String jsonInventoryIds, HttpServletRequest request, ModelMap map) throws Exception {
        
    	LiteYukonUser user = yukonUserContext.getYukonUser();
    	this.checkOptOutsEnabled(user);
    	
        String unEscaped = StringEscapeUtils.unescapeHtml(jsonInventoryIds);
        List<Integer> inventoryIds = OptOutControllerHelper.toInventoryIdList(unEscaped);
        
        accountCheckerService.checkInventory(yukonUserContext.getYukonUser(),
                                             inventoryIds.toArray(new Integer[inventoryIds.size()]));
        
        // Check that there are opt outs remaining for each inventory being opted out
        for(int inventoryId : inventoryIds) {
        	OptOutCountHolder optOutCount = 
        		optOutService.getCurrentOptOutCount(inventoryId, customerAccount.getAccountId());
        	if(!optOutCount.hasOptOutsRemaining()) {
        		throw new NotAuthorizedException("There are no remaining opt outs for " +
        				"inventory with id: " + inventoryId);
        	}
        }
        
        Date startDateObj = dateFormattingService.flexibleDateParser(startDate, yukonUserContext);
        
        MessageSourceResolvable result = new YukonMessageSourceResolvable(
			"yukon.dr.consumer.optoutresult.success");
        
        // Validate the start date
        final Calendar now = dateFormattingService.getCalendar(yukonUserContext);
        final Date today = TimeUtil.getMidnight(now.getTime());
        boolean isValidStartDate = isValidStartDate(startDateObj, today);
        if (!isValidStartDate) {
        	result = new YukonMessageSourceResolvable(
        			"yukon.dr.consumer.optoutresult.invalidStartDate");
        } else {
        	
        	int hoursRemainingInDay = TimeUtil.getHoursTillMidnight(now.getTime());
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
	        		extraHours = durationInDays * 24;
	        	}
	        	optOutRequest.setDurationInHours(hoursRemainingInDay + extraHours);
	        	optOutRequest.setStartDate(null); // Same day OptOut's have null startDates.
	        } else {
		        optOutRequest.setStartDate(startDateObj);
		        optOutRequest.setDurationInHours(durationInDays * 24);
	        }
	        optOutRequest.setInventoryIdList(inventoryIds);
	        optOutRequest.setQuestions(questionList);

	        optOutService.optOut(customerAccount, optOutRequest, yukonUserContext);
	        
	        
        }
        
        map.addAttribute("result", result);
        return "consumer/optout/optOutResult.jsp";
    }
    
    @RequestMapping(value = "/consumer/optout/cancel", method = RequestMethod.POST)
    public String cancel(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
    		Integer eventId, YukonUserContext yukonUserContext, ModelMap map) throws Exception {

    	// Make sure opt outs are enabled for the user
    	LiteYukonUser user = yukonUserContext.getYukonUser();
    	this.checkOptOutsEnabled(user);
    	
    	// Make sure the event is the current user's event
    	this.checkEventAgainstAccount(eventId, null);
    	
    	optOutService.cancelOptOut(Collections.singletonList(eventId), yukonUserContext);
        
    	return "redirect:/spring/stars/consumer/optout";
    }
    
    private boolean isValidStartDate(Date startDate, Date todayDate) {
        if (startDate == null) return false;
        
        long startTime = startDate.getTime();
        long todayTime = todayDate.getTime();
        
        boolean result = startTime >= todayTime;
        return result;
    }

    /**
     * Helper method to make sure opt outs are enabled for the given user
     */
    private void checkOptOutsEnabled(LiteYukonUser user) {
    	boolean optOutEnabled = optOutStatusService.getOptOutEnabled(user);
    	if(!optOutEnabled) {
    		throw new NotAuthorizedException("Opt Outs are currently disabled.");
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
    
    @Autowired
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
    
    @Autowired
    public void setDateFormattingService(
            DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
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
