package com.cannontech.web.stars.dr.operator;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
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
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.consumer.OptOutControllerHelper;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.model.OptOutBackingBean;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.stars.dr.operator.validator.OptOutValidator;
import com.cannontech.web.stars.dr.operator.validator.OptOutValidatorFactory;

@Controller
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT)
public class OperatorProgramOptOutOperatorController {
    
    private CustomerAccountDao customerAccountDao;
    private DatePropertyEditorFactory datePropertyEditorFactory;
    private DisplayableInventoryDao displayableInventoryDao;
    private OptOutAdditionalDao optOutAdditionalDao;
    private OptOutEventDao optOutEventDao;
    private OptOutService optOutService; 
    private OptOutValidatorFactory optOutValidatorFactory;
    private RolePropertyDao rolePropertyDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    protected YukonUserContextMessageSourceResolver messageSourceResolver;

    @RequestMapping(value = "/operator/program/optOut")
    public String view(YukonUserContext yukonUserContext, 
                        ModelMap modelMap,
                        AccountInfoFragment accountInfoFragment) {
        
        LocalDate today = new LocalDate();
        OptOutBackingBean optOutBackingBean = new OptOutBackingBean();
        optOutBackingBean.setStartDate(today);
        modelMap.addAttribute("optOutBackingBean", optOutBackingBean);
        
        //Get the list of current and scheduled opt outs
        List<OptOutEventDto> currentOptOutList = 
            optOutEventDao.getCurrentOptOuts(accountInfoFragment.getAccountId());
        modelMap.addAttribute("currentOptOutList", currentOptOutList);
        
        // Get the list of completed and canceled opt outs
        List<OptOutEventDto> previousOptOutList = 
            optOutEventDao.getOptOutHistoryForAccount(accountInfoFragment.getAccountId(), 6);
        modelMap.addAttribute("previousOptOutList", previousOptOutList);
        
        // Get the current counts for used opt outs and remaining allowed opt outs for each device
        List<DisplayableInventory> displayableInventories = 
            displayableInventoryDao.getDisplayableInventory(accountInfoFragment.getAccountId());
        Map<Integer, OptOutCountHolder> optOutCounts = 
            getOptOutCountsForInventories(displayableInventories, 
                                          accountInfoFragment.getAccountId());

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
            noOptOutLimits = 
                optOutCounts.get(inventory.getInventoryId()).getRemainingOptOuts() == OptOutService.NO_OPT_OUT_LIMIT;
        }
        modelMap.addAttribute("noOptOutLimits", noOptOutLimits);
        
        OptOutLimit currentOptOutLimit = 
            optOutService.getCurrentOptOutLimit(accountInfoFragment.getAccountId());
        int optOutLimit = OptOutService.NO_OPT_OUT_LIMIT;
        if(currentOptOutLimit != null) {
            optOutLimit = currentOptOutLimit.getLimit();
        }
        
        modelMap.addAttribute("optOutLimit", optOutLimit);
        modelMap.addAttribute("allOptedOut", allOptedOut);
        modelMap.addAttribute("optOutsAvailable", optOutsAvailable);
        
        setupOptOutModelMapBasics(accountInfoFragment, modelMap, yukonUserContext);
        return "operator/program/optOut/optOut.jsp";
    }

    @RequestMapping(value = "/operator/program/optOut/view2")
    public String view2(@ModelAttribute("optOutBackingBean") OptOutBackingBean optOutBackingBean,
                         BindingResult bindingResult,
                         ModelMap modelMap,
                         YukonUserContext userContext,
                         FlashScope flashScope,
                         AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {

        // Check to see if the user can only opt out today.  If so set the start date to today.
        boolean isOptOutTodayOnly = 
            rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_OPT_OUT_TODAY_ONLY, 
                                          userContext.getYukonUser());
        if (isOptOutTodayOnly) {
            optOutBackingBean.setStartDate(new LocalDate(userContext.getJodaTimeZone()));
        }
        
        // Validate the optOutBackingBean
        OptOutValidator optOutValidator = optOutValidatorFactory.getOptOutValidator(userContext);
        optOutValidator.validate(optOutBackingBean, bindingResult);
        setupOptOutModelMapBasics(accountInfoFragment, modelMap, userContext);

        if (bindingResult.hasErrors()) {
            
            List<MessageSourceResolvable> messages = 
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return "operator/program/optOut/optOut.jsp";
        } 

    	final LocalDate today = new LocalDate();
    	boolean isSameDay = today.isEqual(optOutBackingBean.getStartDate());
        modelMap.addAttribute("isSameDay", isSameDay);

        List<DisplayableInventory> displayableInventories = displayableInventoryDao.getDisplayableInventory(accountInfoFragment.getAccountId());
        Map<Integer, OptOutCountHolder> optOutCounts = getOptOutCountsForInventories(displayableInventories, accountInfoFragment.getAccountId());

        modelMap.addAttribute("displayableInventories", displayableInventories);
        modelMap.addAttribute("optOutCounts", optOutCounts);
        
        setupOptOutModelMapBasics(accountInfoFragment, modelMap, userContext);
        return "operator/program/optOut/optOutList.jsp";
    }

    @RequestMapping(value = "/operator/program/optOut/optOutQuestions")
    public String optOutQuestions(@ModelAttribute("optOutBackingBean") OptOutBackingBean optOutBackingBean,
                                   BindingResult bindingResult,
                                   HttpServletRequest request, 
                                   String jsonInventoryIds, 
                                   ModelMap modelMap,
                                   YukonUserContext userContext,
                                   FlashScope flashScope,
                                   AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException, CommandCompletionException {
        setupOptOutModelMapBasics(accountInfoFragment, modelMap, userContext);
        
        String unEscaped = StringEscapeUtils.unescapeHtml(jsonInventoryIds);
        List<Integer> inventoryIds = OptOutControllerHelper.toInventoryIdList(unEscaped);
        if (inventoryIds.size() == 0) {
        	
            flashScope.setMessage(new YukonMessageSourceResolvable("yukon.web.modules.operator.optOut.noInventorySelected"), FlashScopeMessageType.ERROR);
            return "redirect:/spring/stars/operator/program/optOut";

        }

        List<String> questions = OptOutControllerHelper.getConfirmQuestions(
                messageSourceResolver, 
                userContext,
                "yukon.dr.operator.optOutconfirm.question.");

        modelMap.addAttribute("jsonInventoryIds", jsonInventoryIds);
        setupOptOutModelMapBasics(accountInfoFragment, modelMap, userContext);
        if (questions.size() == 0) {

            CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
            processOptOut(optOutBackingBean, bindingResult, request, modelMap,
                          userContext, flashScope, accountInfoFragment,
                          inventoryIds, customerAccount);

            return view(userContext, modelMap, accountInfoFragment);
        }
        
        return "operator/program/optOut/confirm";
    }
    
    @RequestMapping("/operator/program/optOut/confirm")
    public String confirm(@ModelAttribute("optOutBackingBean") OptOutBackingBean optOutBackingBean,
                           String jsonInventoryIds, 
                           ModelMap modelMap,
                           YukonUserContext yukonUserContext,
                           AccountInfoFragment accountInfoFragment) {

        List<String> questions = 
            OptOutControllerHelper.getConfirmQuestions(
                    messageSourceResolver, 
                    yukonUserContext,
                    "yukon.dr.operator.optOutconfirm.question.");
        modelMap.addAttribute("questions", questions);
        
        String escaped = StringEscapeUtils.escapeHtml(jsonInventoryIds);
        modelMap.addAttribute("jsonInventoryIds", escaped);

        setupOptOutModelMapBasics(accountInfoFragment, modelMap, yukonUserContext);
        return "operator/program/optOut/optOutConfirm.jsp";
    }
    
    @RequestMapping(value = "/operator/program/optOut/optOutHistory", method = RequestMethod.GET)
    public String optOutHistory(ModelMap modelMap,
                                 AccountInfoFragment accountInfoFragment) {
        
        // Get the list of completed and canceled opt outs
        List<OptOutEventDto> previousOptOutList = optOutEventDao.getOptOutHistoryForAccount(accountInfoFragment.getAccountId());

        modelMap.addAttribute("previousOptOutList", previousOptOutList);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "operator/program/optOut/optOutHistory.jsp";
    }
    
    @RequestMapping("/operator/program/optOut/update")
    public String update(@ModelAttribute("optOutBackingBean") OptOutBackingBean optOutBackingBean,
                          BindingResult bindingResult,
                          String jsonInventoryIds, 
                          HttpServletRequest request, 
                          ModelMap modelMap,
                          YukonUserContext userContext,
                          FlashScope flashScope,
                          AccountInfoFragment accountInfoFragment) throws Exception {

        String unEscaped = StringEscapeUtils.unescapeHtml(jsonInventoryIds);
        List<Integer> inventoryIds = OptOutControllerHelper.toInventoryIdList(unEscaped);

        bindingResult = new BeanPropertyBindingResult(optOutBackingBean, "optOutBackingBean");
        
        CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
        
        processOptOut(optOutBackingBean, bindingResult, request, modelMap,
                      userContext, flashScope, accountInfoFragment,
                      inventoryIds, customerAccount);
        
        if (bindingResult.hasErrors()) {
            
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            
            return "operator/program/optOut/optOut.jsp";
        }
       
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.optOut.success"));
        return "redirect:/spring/stars/operator/program/optOut";
    }

    /**
     * This method handles the processing for an opt out and also handles
     * validation.
     */
    private void processOptOut(OptOutBackingBean optOutBackingBean,
                                BindingResult bindingResult,
                                HttpServletRequest request, ModelMap modelMap,
                                YukonUserContext userContext,
                                FlashScope flashScope,
                                AccountInfoFragment accountInfoFragment,
                                List<Integer> inventoryIds,
                                CustomerAccount customerAccount) throws ServletRequestBindingException, CommandCompletionException {
        this.checkInventoryAgainstAccount(inventoryIds, customerAccount);
        
        // validate/update
        OptOutValidator optOutValidator = optOutValidatorFactory.getOptOutValidator(userContext);
        optOutValidator.validate(optOutBackingBean, bindingResult);
        if (!bindingResult.hasErrors()) {
            String jsonQuestions = ServletRequestUtils.getStringParameter(request, "jsonQuestions");
            List<ScheduledOptOutQuestion> questionList = OptOutControllerHelper.toOptOutQuestionList(jsonQuestions);

            OptOutRequest optOutRequest = new OptOutRequest();
            optOutRequest.setInventoryIdList(inventoryIds);
            optOutRequest.setQuestions(questionList);
            
            LocalDate today = new LocalDate(userContext.getJodaTimeZone());
            boolean isSameDay = today.isEqual(optOutBackingBean.getStartDate());
            
            if (isSameDay) {
                int extraHours = 0;
                // If durationInDays is 1 that means the rest of today only
                if (optOutBackingBean.getDurationInDays() > 1) {
                    // Today counts as the first day
                    extraHours = (optOutBackingBean.getDurationInDays() - 1) * 24;
                }

                Date now = new Date();
                int hoursRemainingInDay = TimeUtil.getHoursTillMidnight(now, userContext.getTimeZone());
                optOutRequest.setDurationInHours(hoursRemainingInDay + extraHours);
                optOutRequest.setStartDate(null); // Same day OptOut's have null start dates
            } else {
                DateTime startDateTime = optOutBackingBean.getStartDate().toDateTimeAtStartOfDay(userContext.getJodaTimeZone());
                optOutRequest.setStartDate(startDateTime.toDate());
                optOutRequest.setDurationInHours(optOutBackingBean.getDurationInDays() * 24);
            }

            LiteYukonUser user = userContext.getYukonUser();
            optOutService.optOut(customerAccount, optOutRequest, user);
        }

        setupOptOutModelMapBasics(accountInfoFragment, modelMap, userContext);

        if (bindingResult.hasErrors()) {
            
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            
        }
    }
    
    @RequestMapping(value = "/operator/program/optOut/cancel")
    public String cancel(Integer eventId, 
                          ModelMap modelMap,
                          YukonUserContext yukonUserContext,
                          AccountInfoFragment accountInfoFragment) throws Exception {
        
        // Check that the inventory we're working with belongs to the current account
        CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
        this.checkEventAgainstAccount(eventId, customerAccount);
        
        LiteYukonUser user = yukonUserContext.getYukonUser();
        optOutService.cancelOptOut(Collections.singletonList(eventId), user);
        
        setupOptOutModelMapBasics(accountInfoFragment, modelMap, yukonUserContext);
        return "redirect:/spring/stars/operator/program/optOut";
    }

    @RequestMapping(value = "/operator/program/optOut/allowAnother")
    public String allowAnother(Integer inventoryId,  
                                ModelMap modelMap,
                                YukonUserContext userContext,
                                AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {
        
        // Check that the inventory we're working with belongs to the current account
        CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
        this.checkInventoryAgainstAccount(Collections.singletonList(inventoryId), customerAccount);
        
        optOutAdditionalDao.addAdditonalOptOuts(inventoryId, customerAccount.getAccountId(), 1);
        
        setupOptOutModelMapBasics(accountInfoFragment, modelMap, userContext);
        return "redirect:/spring/stars/operator/program/optOut";
    }
    
    @RequestMapping(value = "/operator/program/optOut/repeat")
    public String repeat(Integer inventoryId, 
                          ModelMap modelMap,
                          YukonUserContext yukonUserContext,
                          AccountInfoFragment accountInfoFragment) throws Exception {
        
        
        // Check that the inventory we're working with belongs to the current account
        CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
        this.checkInventoryAgainstAccount(Collections.singletonList(inventoryId), customerAccount);
        
        optOutService.resendOptOut(
                inventoryId, 
                customerAccount.getAccountId(), 
                yukonUserContext.getYukonUser());
        
        setupOptOutModelMapBasics(accountInfoFragment, modelMap, yukonUserContext);
        return "redirect:/spring/stars/operator/program/optOut";
    }
    
    @RequestMapping(value = "/operator/program/optOut/resetToLimit")
    public String resetToLimit(Integer inventoryId, 
                                ModelMap modelMap,
                                YukonUserContext userContext,
                                AccountInfoFragment accountInfoFragment) {

        
        // Check that the inventory we're working with belongs to the current account
        CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
        checkInventoryAgainstAccount(Collections.singletonList(inventoryId), customerAccount);
        
        optOutService.resetOptOutLimitForInventory(inventoryId, customerAccount.getAccountId());
        
        setupOptOutModelMapBasics(accountInfoFragment, modelMap, userContext);
        return "redirect:/spring/stars/operator/program/optOut";
    }
    
    /**
     * This method builds up the basic modelMap entries for an optOutPage
     */
    private void setupOptOutModelMapBasics(AccountInfoFragment accountInfoFragment,
                                           ModelMap modelMap,
                                           YukonUserContext userContext) {
        
        List<Integer> optOutPeriodList = optOutService.getAvailableOptOutPeriods(userContext.getYukonUser());
        modelMap.addAttribute("optOutPeriodList", optOutPeriodList);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        
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

    /* INIT BINDER */
    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        binder.registerCustomEditor(LocalDate.class, 
                                    "startDate", 
                                    datePropertyEditorFactory.getLocalDatePropertyEditor(DateFormatEnum.DATE, userContext));
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
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setStarsInventoryBaseDao(StarsInventoryBaseDao starsInventoryBaseDao) {
        this.starsInventoryBaseDao = starsInventoryBaseDao;
    }
    
    @Autowired
    public void setDatePropertyEditorFactory(DatePropertyEditorFactory datePropertyEditorFactory) {
        this.datePropertyEditorFactory = datePropertyEditorFactory;
    }
    
    @Autowired
    public void setDisplayableInventoryDao(
            DisplayableInventoryDao displayableInventoryDao) {
        this.displayableInventoryDao = displayableInventoryDao;
    }
    
    @Autowired
    public void setMessageSourceResolver(
            YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setOptOutValidatorFactory(OptOutValidatorFactory optOutValidatorFactory) {
        this.optOutValidatorFactory = optOutValidatorFactory;
    }
}
