package com.cannontech.web.stars.dr.consumer;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.optout.model.ScheduledOptOutQuestion;
import com.cannontech.stars.dr.optout.service.OptOutRequest;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableInventory;

@Controller
public class OptOutController extends AbstractConsumerController {
    private AuthDao authDao;
    private DateFormattingService dateFormattingService;
    private OptOutService optOutService;
    
    @CheckRole(ResidentialCustomerRole.ROLEID)
    @CheckRoleProperty(ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_OPT_OUT)
    @RequestMapping(value = "/consumer/optout", method = RequestMethod.GET)
    public String view(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            YukonUserContext yukonUserContext, ModelMap map) {
    	
        Calendar cal = Calendar.getInstance(yukonUserContext.getTimeZone());
    	Date currentDate = cal.getTime();
    	map.addAttribute("currentDate", currentDate);
    	return "consumer/optout/optOut.jsp";
    }

    @CheckRole(ResidentialCustomerRole.ROLEID)
    @CheckRoleProperty(ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_OPT_OUT)
    @RequestMapping(value = "/consumer/optout/view2", method = RequestMethod.POST)
    public String view2(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            YukonUserContext yukonUserContext, String startDate, 
            int durationInDays, ModelMap map) {
        
        final LiteYukonUser user = yukonUserContext.getYukonUser();
        final boolean hasDeviceSelection =
            authDao.checkRoleProperty(user, ResidentialCustomerRole.OPT_OUT_DEVICE_SELECTION);
        
        map.addAttribute("durationInDays", durationInDays);
        map.addAttribute("startDate", startDate);
        
        List<DisplayableInventory> displayableInventories =
            displayableInventoryDao.getDisplayableInventory(customerAccount);
        
        boolean blanketDevices = (displayableInventories.size() < 2 && !hasDeviceSelection);
        if (blanketDevices) {
            final JSONArray jsonArray = new JSONArray();

            for (final DisplayableInventory inventory : displayableInventories) {
                jsonArray.put(inventory.getInventoryId());
            }
            
            String jsonInventoryIds = StringEscapeUtils.escapeHtml(jsonArray.toString());
            map.addAttribute("jsonInventoryIds", jsonInventoryIds);
            
            return "redirect:/spring/stars/consumer/optout/confirm";
        }
        
        map.addAttribute("displayableInventories", displayableInventories);
        return "consumer/optout/optOutList.jsp";
    }
    
    @CheckRole(ResidentialCustomerRole.ROLEID)
    @CheckRoleProperty(ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_OPT_OUT)
    @RequestMapping("/consumer/optout/confirm")
    public String confirm(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            YukonUserContext yukonUserContext, String startDate, int durationInDays,
            String jsonInventoryIds, ModelMap map) {
        
        map.addAttribute("startDate", startDate);
        map.addAttribute("durationInDays", durationInDays);
        
        String escaped = StringEscapeUtils.escapeHtml(jsonInventoryIds);
        map.addAttribute("jsonInventoryIds", escaped);

        List<String> questions = OptOutControllerHelper.getConfirmQuestions(messageSourceResolver, yukonUserContext);
        if (questions.size() == 0) return "redirect:/spring/stars/consumer/optout/update";

        map.addAttribute("questions", questions);
        return "consumer/optout/optOutConfirm.jsp";
    }
    
    @CheckRole(ResidentialCustomerRole.ROLEID)
    @CheckRoleProperty(ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_OPT_OUT)
    @RequestMapping("/consumer/optout/update")
    public String update(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            YukonUserContext yukonUserContext, String startDate, int durationInDays,
            String jsonInventoryIds, HttpServletRequest request, ModelMap map) throws Exception {
        
        String unEscaped = StringEscapeUtils.unescapeHtml(jsonInventoryIds);
        List<Integer> inventoryIds = OptOutControllerHelper.toInventoryIdList(unEscaped);
        
        accountCheckerService.checkInventory(yukonUserContext.getYukonUser(),
                                             inventoryIds.toArray(new Integer[inventoryIds.size()]));
        
        
        String jsonQuestions = ServletRequestUtils.getStringParameter(request, 
                                                                      "jsonQuestions");
        List<ScheduledOptOutQuestion> questionList = 
            OptOutControllerHelper.toOptOutQuestionList(jsonQuestions);
        
        Date startDateObj = dateFormattingService.flexibleDateParser(startDate, yukonUserContext);

        OptOutRequest optOutRequest = new OptOutRequest();
        optOutRequest.setStartDate(startDateObj);
        optOutRequest.setDurationInHours(durationInDays * 24);
        optOutRequest.setInventoryIdList(inventoryIds);
        optOutRequest.setQuestions(questionList);
        
        MessageSourceResolvable result = optOutService.processOptOutRequest(customerAccount,
                                                                            optOutRequest,
                                                                            yukonUserContext);
        map.addAttribute("result", result);
        return "consumer/optout/optOutResult.jsp";
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
    
}
