
package com.cannontech.web.stars.dr.operator;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.Period;
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

import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.StringUtils;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
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
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutCountHolder;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.model.OptOutEventDto;
import com.cannontech.stars.dr.optout.model.OptOutLimit;
import com.cannontech.stars.dr.optout.model.OptOutLog;
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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

@Controller
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT)
@RequestMapping(value = "/operator/program/optOut/*")
public class OperatorProgramOptOutOperatorController {
    
    private CustomerAccountDao customerAccountDao;
    private DatePropertyEditorFactory datePropertyEditorFactory;
    private DisplayableInventoryDao displayableInventoryDao;
    private OptOutEventDao optOutEventDao;
    private OptOutService optOutService; 
    private OptOutValidatorFactory optOutValidatorFactory;
    private RolePropertyDao rolePropertyDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private DateFormattingService dateFormattingService;
    private InventoryDao inventoryDao;

    @RequestMapping
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
        Map<Integer, List<MessageSourceResolvable>> previousOptOutDetails =
            getHistoryActionLog(previousOptOutList, yukonUserContext);
        modelMap.addAttribute("previousOptOutDetails", previousOptOutDetails);
        
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

    @RequestMapping
    public String deviceSelection(
                       @ModelAttribute("optOutBackingBean") OptOutBackingBean optOutBackingBean,
                       BindingResult bindingResult,
                       ModelMap modelMap,
                       YukonUserContext userContext,
                       FlashScope flashScope,
                       AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {

        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,
                                       userContext.getYukonUser());
        
        // Check to see if the user can only opt out today.  If so set the start date to today.
        boolean isOptOutTodayOnly = 
            rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_OPT_OUT_TODAY_ONLY, 
                                          userContext.getYukonUser());
        if (isOptOutTodayOnly) {
            optOutBackingBean.setStartDate(new LocalDate(userContext.getJodaTimeZone()));
        }
        
        // Validate the optOutBackingBean
        OptOutValidator optOutValidator = 
            optOutValidatorFactory.getOptOutValidator(userContext, accountInfoFragment);
        optOutValidator.validate(optOutBackingBean, bindingResult);
        setupOptOutModelMapBasics(accountInfoFragment, modelMap, userContext);

        if (bindingResult.hasErrors()) {
            
            List<MessageSourceResolvable> messages = 
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            
            return view(userContext, modelMap, accountInfoFragment);
        } 

    	final LocalDate today = new LocalDate();
    	boolean isSameDay = today.isEqual(optOutBackingBean.getStartDate());
        modelMap.addAttribute("isSameDay", isSameDay);

        List<DisplayableInventory> displayableInventories = displayableInventoryDao.getDisplayableInventory(accountInfoFragment.getAccountId());
        Map<Integer, OptOutCountHolder> optOutCounts = getOptOutCountsForInventories(displayableInventories, accountInfoFragment.getAccountId());

        modelMap.addAttribute("displayableInventories", displayableInventories);
        modelMap.addAttribute("optOutCounts", optOutCounts);
        
        List<String> questions = 
            OptOutControllerHelper.getConfirmQuestions(messageSourceResolver, 
                                                       userContext,
                                                       "yukon.dr.operator.optoutconfirm.question.");
        modelMap.addAttribute("optOutQuestionsExist", !questions.isEmpty());
        
        setupOptOutModelMapBasics(accountInfoFragment, modelMap, userContext);
        return "operator/program/optOut/optOutList.jsp";
    }

    @RequestMapping
    public String optOutQuestions(@ModelAttribute("optOutBackingBean") OptOutBackingBean optOutBackingBean,
                                   BindingResult bindingResult,
                                   HttpServletRequest request,
                                   ModelMap modelMap,
                                   YukonUserContext userContext,
                                   FlashScope flashScope,
                                   AccountInfoFragment accountInfoFragment)
            throws ServletRequestBindingException, CommandCompletionException {

        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,
                                       userContext.getYukonUser());
        
        String[] inventoryIdsArr = ServletRequestUtils.getStringParameters(request, "inventoryIds");
        
        if (inventoryIdsArr.length < 1) {
            flashScope.setMessage(new YukonMessageSourceResolvable("yukon.web.modules.operator.optOut.main.noInventorySelected"), 
                                  FlashScopeMessageType.ERROR);
            setupOptOutModelMapBasics(accountInfoFragment, modelMap, userContext);
            
            return deviceSelection(optOutBackingBean,
                                   bindingResult,
                                   modelMap,
                                   userContext,
                                   flashScope,
                                   accountInfoFragment);
            
        }
        
        // get the list of inventory ids
        int[] inventoryIdsIntArr = StringUtils.toIntArray(inventoryIdsArr);
        List<Integer> inventoryIds = Lists.newArrayList();
        for (int inventoryId : inventoryIdsIntArr) {
            inventoryIds.add(inventoryId);
        }
        
        List<String> questions = OptOutControllerHelper.getConfirmQuestions(
                messageSourceResolver, 
                userContext,
                "yukon.dr.operator.optoutconfirm.question.");

        modelMap.addAttribute("inventoryIds", inventoryIds);
        setupOptOutModelMapBasics(accountInfoFragment, modelMap, userContext);
        if (questions.size() == 0) {

            CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
            processOptOut(optOutBackingBean, bindingResult, request, modelMap,
                          userContext, flashScope, accountInfoFragment,
                          inventoryIds, customerAccount);
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.optOut.main.success"));
            return "redirect:view";
        }
        
        return confirm(optOutBackingBean, inventoryIds, modelMap, 
                        userContext, accountInfoFragment);
    }
    
    @RequestMapping
    public String confirm(@ModelAttribute("optOutBackingBean") OptOutBackingBean optOutBackingBean,
                           List<Integer> inventoryIds, 
                           ModelMap modelMap,
                           YukonUserContext yukonUserContext,
                           AccountInfoFragment accountInfoFragment) {

        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,
                                       yukonUserContext.getYukonUser());
                
        List<String> questions = 
            OptOutControllerHelper.getConfirmQuestions(messageSourceResolver, 
                                                       yukonUserContext,
                                                       "yukon.dr.operator.optoutconfirm.question.");

        modelMap.addAttribute("questions", questions);
        modelMap.addAttribute("inventoryIds", inventoryIds);

        setupOptOutModelMapBasics(accountInfoFragment, modelMap, yukonUserContext);
        return "operator/program/optOut/optOutConfirm.jsp";
    }
    
    @RequestMapping
    public String optOutHistory(ModelMap modelMap,
                                 AccountInfoFragment accountInfoFragment,
                                 YukonUserContext yukonUserContext) {
        
        // Get the list of completed and canceled opt outs
        List<OptOutEventDto> previousOptOutList =
            optOutEventDao.getOptOutHistoryForAccount(accountInfoFragment.getAccountId());
        modelMap.addAttribute("previousOptOutList", previousOptOutList);
        Map<Integer, List<MessageSourceResolvable>> previousOptOutDetails =
            getHistoryActionLog(previousOptOutList, yukonUserContext);
        modelMap.addAttribute("previousOptOutDetails", previousOptOutDetails);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "operator/program/optOut/optOutHistory.jsp";
    }
    
    @RequestMapping
    public String update(@ModelAttribute("optOutBackingBean") OptOutBackingBean optOutBackingBean,
                          BindingResult bindingResult,
                          HttpServletRequest request, 
                          ModelMap modelMap,
                          YukonUserContext userContext,
                          FlashScope flashScope,
                          AccountInfoFragment accountInfoFragment) throws Exception {

        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,
                                       userContext.getYukonUser());

        // get the list of inventory ids
        String inventoryIdsStr = 
            ServletRequestUtils.getRequiredStringParameter(request, "inventoryIds");
        inventoryIdsStr = inventoryIdsStr.substring(1, inventoryIdsStr.length()-1);
        List<Integer> inventoryIds = Lists.newArrayList(); 
        for (String inventoryIdStr : inventoryIdsStr.split(",")) {
            inventoryIds.add(Integer.parseInt(inventoryIdStr.trim()));
        }
        
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
       
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.optOut.main.success"));
        return "redirect:view";
    }

    private Map<Integer, List<MessageSourceResolvable>> getHistoryActionLog(
            List<OptOutEventDto> previousOptOutList,
            YukonUserContext userContext) {
        Multimap<OptOutEventDto, OptOutLog> previousOptOutDetails =
            optOutEventDao.getOptOutEventDetails(previousOptOutList);
        Map<Integer, List<MessageSourceResolvable>> retVal = Maps.newHashMap();
        for (OptOutEventDto event : previousOptOutDetails.keySet()) {
            List<MessageSourceResolvable> actionMessages = Lists.newArrayList();
            for (OptOutLog actionLog : previousOptOutDetails.get(event)) {
                MessageSourceAccessor messageSourceAccessor =
                    messageSourceResolver.getMessageSourceAccessor(userContext);
                String actionStr = messageSourceAccessor.getMessage(actionLog.getAction().getFormatKey());
                String logDateStr =
                    dateFormattingService.format(actionLog.getLogDate(),
                                                 DateFormatEnum.DATEHM, userContext);
                MessageSourceResolvable msg =
                    new YukonMessageSourceResolvable("yukon.web.modules.operator.optOut.historyAction",
                                                     actionStr,
                                                     actionLog.getUsername(),
                                                     logDateStr);
                actionMessages.add(msg);
            }
            retVal.put(event.getEventId(), actionMessages);
        }
        return retVal;
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
        OptOutValidator optOutValidator = 
            optOutValidatorFactory.getOptOutValidator(userContext, accountInfoFragment);
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
                Period optOutPeriod = Period.days(optOutBackingBean.getDurationInDays());
                Interval optOutInterval = new Interval(startDateTime, optOutPeriod);
                optOutRequest.setStartDate(startDateTime.toInstant());
                optOutRequest.setDurationInHours(optOutInterval.toDuration()
                                                               .toPeriod()
                                                               .toStandardHours()
                                                               .getHours());
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

    @RequestMapping
    public String confirmCancelOptOut(int eventId, int inventoryId,
            ModelMap model, YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) {
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,
                                       userContext.getYukonUser());

        CustomerAccount customerAccount =
            customerAccountDao.getById(accountInfoFragment.getAccountId());
        checkEventAgainstAccount(eventId, customerAccount);
        HardwareSummary hardware = inventoryDao.findHardwareSummaryById(inventoryId);
        model.addAttribute("hardware", hardware);
        return "operator/program/optOut/confirmCancelOptOut.jsp";
    }

    @RequestMapping
    public String cancelOptOut(Integer eventId,
                                FlashScope flashScope,
                                ModelMap modelMap,
                                YukonUserContext yukonUserContext,
                                AccountInfoFragment accountInfoFragment) throws Exception {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,
                                       yukonUserContext.getYukonUser());
        
        // Check that the inventory we're working with belongs to the current account
        CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
        this.checkEventAgainstAccount(eventId, customerAccount);
        
        LiteYukonUser user = yukonUserContext.getYukonUser();
        optOutService.cancelOptOut(Collections.singletonList(eventId), user);
        
        flashScope.setConfirm(
                       new YukonMessageSourceResolvable(
                              "yukon.web.modules.operator.optOut.main.cancelOptOut.successText"));
        
        setupOptOutModelMapBasics(accountInfoFragment, modelMap, yukonUserContext);
        return closeDialog(modelMap);
    }

    @RequestMapping
    public String confirmDecrementAllowances(int inventoryId, ModelMap model,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) {
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,
                                       userContext.getYukonUser());

        CustomerAccount customerAccount =
            customerAccountDao.getById(accountInfoFragment.getAccountId());
        checkInventoryAgainstAccount(Collections.singletonList(inventoryId),
                                     customerAccount);
        HardwareSummary hardware = inventoryDao.findHardwareSummaryById(inventoryId);
        model.addAttribute("hardware", hardware);
        return "operator/program/optOut/confirmDecrementAllowances.jsp";
    }

    @RequestMapping
    public String decrementAllowances(Integer inventoryId,
            FlashScope flashScope, ModelMap modelMap,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment)
            throws ServletRequestBindingException {
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,
                                       userContext.getYukonUser());

        // Check that the inventory we're working with belongs to the current account
        CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
        checkInventoryAgainstAccount(Collections.singletonList(inventoryId), customerAccount);

        optOutService.allowAdditionalOptOuts(customerAccount.getAccountId(), inventoryId, 
                                             -1, userContext.getYukonUser());

        flashScope.setConfirm(
                       new YukonMessageSourceResolvable(
                               "yukon.web.modules.operator.optOut.main.decrementAllowance.successText"));

        setupOptOutModelMapBasics(accountInfoFragment, modelMap, userContext);
        return closeDialog(modelMap);
    }

    @RequestMapping
    public String confirmAllowAnother(int inventoryId, ModelMap model,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) {
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,
                                       userContext.getYukonUser());
        
        CustomerAccount customerAccount =
            customerAccountDao.getById(accountInfoFragment.getAccountId());
        checkInventoryAgainstAccount(Collections.singletonList(inventoryId),
                                     customerAccount);
        HardwareSummary hardware = inventoryDao.findHardwareSummaryById(inventoryId);
        model.addAttribute("hardware", hardware);
        return "operator/program/optOut/confirmAllowAnother.jsp";
    }
    
    @RequestMapping
    public String allowAnother(Integer inventoryId,  
            FlashScope flashScope,
            ModelMap modelMap,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,
                                       userContext.getYukonUser());
        
        // Check that the inventory we're working with belongs to the current account
        CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
        this.checkInventoryAgainstAccount(Collections.singletonList(inventoryId), customerAccount);
        
        
        optOutService.allowAdditionalOptOuts(customerAccount.getAccountId(), inventoryId, 1, 
                                             userContext.getYukonUser());
        
        flashScope.setConfirm(
                              new YukonMessageSourceResolvable(
                              "yukon.web.modules.operator.optOut.main.allowOne.successText"));
        
        setupOptOutModelMapBasics(accountInfoFragment, modelMap, userContext);
        return closeDialog(modelMap);
    }
    
    @RequestMapping
    public String confirmResend(int inventoryId, ModelMap model,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) {
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,
                                       userContext.getYukonUser());

        CustomerAccount customerAccount =
            customerAccountDao.getById(accountInfoFragment.getAccountId());
        checkInventoryAgainstAccount(Collections.singletonList(inventoryId),
                                     customerAccount);
        HardwareSummary hardware = inventoryDao.findHardwareSummaryById(inventoryId);
        model.addAttribute("hardware", hardware);
        return "operator/program/optOut/confirmResend.jsp";
    }

    @RequestMapping
    public String resend(Integer inventoryId,
                          FlashScope flashScope,
                          ModelMap modelMap,
                          YukonUserContext yukonUserContext,
                          AccountInfoFragment accountInfoFragment) throws Exception {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,
                                       yukonUserContext.getYukonUser());
        
        // Check that the inventory we're working with belongs to the current account
        CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
        this.checkInventoryAgainstAccount(Collections.singletonList(inventoryId), customerAccount);
        
        optOutService.resendOptOut(
                inventoryId, 
                customerAccount.getAccountId(), 
                yukonUserContext.getYukonUser());
        
        flashScope.setConfirm(
                       new YukonMessageSourceResolvable(
                               "yukon.web.modules.operator.optOut.main.resendOptOut.successText"));
               
        setupOptOutModelMapBasics(accountInfoFragment, modelMap, yukonUserContext);
        return closeDialog(modelMap);
    }

    @RequestMapping
    public String confirmResetToLimit(int inventoryId, ModelMap model,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) {
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,
                                       userContext.getYukonUser());

        CustomerAccount customerAccount =
            customerAccountDao.getById(accountInfoFragment.getAccountId());
        checkInventoryAgainstAccount(Collections.singletonList(inventoryId),
                                     customerAccount);
        HardwareSummary hardware = inventoryDao.findHardwareSummaryById(inventoryId);
        model.addAttribute("hardware", hardware);
        return "operator/program/optOut/confirmResetToLimit.jsp";
    }

    @RequestMapping
    public String resetToLimit(Integer inventoryId, 
                                FlashScope flashScope,
                                ModelMap modelMap,
                                YukonUserContext userContext,
                                AccountInfoFragment accountInfoFragment) {

        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,
                                       userContext.getYukonUser());
        
        // Check that the inventory we're working with belongs to the current account
        CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
        checkInventoryAgainstAccount(Collections.singletonList(inventoryId), customerAccount);
        
        optOutService.resetOptOutLimitForInventory(inventoryId, customerAccount.getAccountId(),
                                                   userContext.getYukonUser());
        
        flashScope.setConfirm(
                       new YukonMessageSourceResolvable(
                               "yukon.web.modules.operator.optOut.main.resetToLimit.successText"));
               
        setupOptOutModelMapBasics(accountInfoFragment, modelMap, userContext);
        return closeDialog(modelMap);
    }

    @RequestMapping(params="cancel")
    public String cancel(ModelMap modelMap,
                          AccountInfoFragment accountInfoFragment,
                          YukonUserContext userContext,
                          HttpSession session) {
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "redirect:view";
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

    private String closeDialog(ModelMap model) {
        model.addAttribute("popupId", "confirmDialog");
        return "closePopup.jsp";
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

    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }

    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }
}
