package com.cannontech.web.stars.dr.operator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.survey.dao.SurveyDao;
import com.cannontech.common.survey.model.Question;
import com.cannontech.common.survey.model.Survey;
import com.cannontech.common.survey.service.SurveyService;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.displayable.dao.DisplayableInventoryDao;
import com.cannontech.stars.dr.displayable.model.DisplayableInventory;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutCountHolder;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.model.OptOutEventDto;
import com.cannontech.stars.dr.optout.model.OptOutLimit;
import com.cannontech.stars.dr.optout.model.OptOutLog;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.stars.dr.optout.service.OptOutSurveyService;
import com.cannontech.stars.dr.optout.util.OptOutUtil;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.consumer.OptOutControllerHelper;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.model.OptOutBackingBean;
import com.cannontech.web.stars.dr.operator.model.SurveyResultValidator;
import com.cannontech.web.stars.dr.operator.model.OptOutBackingBean.SurveyResult;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.stars.dr.operator.validator.OptOutValidator;
import com.cannontech.web.stars.dr.operator.validator.OptOutValidatorFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

@Controller
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT)
@RequestMapping(value = "/operator/program/optOut/*")
public class OperatorOptOutController {
    private AccountEventLogService accountEventLogService;
    private CustomerAccountDao customerAccountDao;
    private DatePropertyEditorFactory datePropertyEditorFactory;
    private DisplayableInventoryDao displayableInventoryDao;
    private OptOutEventDao optOutEventDao;
    private OptOutService optOutService;
    private OptOutValidatorFactory optOutValidatorFactory;
    private LMHardwareBaseDao lmHardwareBaseDao;
    private RolePropertyDao rolePropertyDao;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private DateFormattingService dateFormattingService;
    private InventoryDao inventoryDao;
    private OptOutSurveyService optOutSurveyService;
    private SurveyService surveyService;
    private SurveyDao surveyDao;
    private OptOutControllerHelper helper;

    @RequestMapping
    public String view(YukonUserContext userContext, ModelMap model,
            AccountInfoFragment accountInfoFragment) {
        setupOptOutModelMapBasics(accountInfoFragment, model, userContext);
        LocalDate today = new LocalDate(userContext.getJodaTimeZone());
        OptOutBackingBean optOutBackingBean = new OptOutBackingBean();
        optOutBackingBean.setStartDate(today);
        model.addAttribute("optOutBackingBean", optOutBackingBean);
        return prepareModelForView(userContext, model, accountInfoFragment);
    }

    private String prepareModelForView(YukonUserContext userContext, ModelMap model,
                                       AccountInfoFragment accountInfoFragment) {
        // Get the list of current and scheduled opt outs
        List<OptOutEventDto> currentOptOutList =
            optOutEventDao.getCurrentOptOuts(accountInfoFragment.getAccountId());
        model.addAttribute("currentOptOutList", currentOptOutList);

        // Get the list of completed and canceled opt outs
        List<OptOutEventDto> previousOptOutList =
            optOutEventDao.getOptOutHistoryForAccount(accountInfoFragment.getAccountId(), 6);
        model.addAttribute("previousOptOutList", previousOptOutList);
        Map<Integer, List<MessageSourceResolvable>> previousOptOutDetails =
            getHistoryActionLog(previousOptOutList, userContext);
        model.addAttribute("previousOptOutDetails", previousOptOutDetails);

        // Get the current counts for used opt outs and remaining allowed opt outs for each device
        List<DisplayableInventory> displayableInventories =
            displayableInventoryDao.getDisplayableInventory(accountInfoFragment.getAccountId());
        Map<Integer, OptOutCountHolder> optOutCounts =
            getOptOutCountsForInventories(displayableInventories,
                                          accountInfoFragment.getAccountId());

        boolean allOptedOut = true;
        boolean optOutsAvailable = false;
        for (DisplayableInventory inventory : displayableInventories) {

            if (!inventory.isCurrentlyOptedOut()) {
                allOptedOut = false;
            }
            if (optOutCounts.get(inventory.getInventoryId()).isOptOutsRemaining()) {
                optOutsAvailable = true;
            }
        }
        model.addAttribute("displayableInventories", displayableInventories);
        model.addAttribute("optOutCounts", optOutCounts);
        boolean noOptOutLimits = false;
        if (displayableInventories.size() > 0) {
            // Check the opt out limit from the first device - limits are set by login
            // group and will therefore be the same for every device on an account
            DisplayableInventory inventory = displayableInventories.get(0);
            noOptOutLimits =
                optOutCounts.get(inventory.getInventoryId()).getRemainingOptOuts() == OptOutService.NO_OPT_OUT_LIMIT;
        }
        model.addAttribute("noOptOutLimits", noOptOutLimits);

        OptOutLimit currentOptOutLimit =
            optOutService.getCurrentOptOutLimit(accountInfoFragment.getAccountId());
        int optOutLimit = OptOutService.NO_OPT_OUT_LIMIT;
        if (currentOptOutLimit != null) {
            optOutLimit = currentOptOutLimit.getLimit();
        }

        model.addAttribute("optOutLimit", optOutLimit);
        model.addAttribute("allOptedOut", allOptedOut);
        model.addAttribute("optOutsAvailable", optOutsAvailable);

        return "operator/program/optOut/optOut.jsp";
    }

    @RequestMapping
    public String deviceSelection(
            @ModelAttribute OptOutBackingBean optOutBackingBean,
            BindingResult bindingResult, ModelMap model,
            YukonUserContext userContext, FlashScope flashScope,
            AccountInfoFragment accountInfoFragment)
            throws ServletRequestBindingException {
        setupOptOutModelMapBasics(accountInfoFragment, model, userContext);

        // Validate info entered on first page.
        OptOutValidator optOutValidator =
            optOutValidatorFactory.getOptOutValidator(userContext, true,
                                                      accountInfoFragment);
        optOutValidator.validate(optOutBackingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);

            return prepareModelForView(userContext, model, accountInfoFragment);
        }

        LocalDate today = new LocalDate(userContext.getJodaTimeZone());

        // Check to see if the user can only opt out today.  If so set the
        // start date to today.
        boolean isOptOutTodayOnly =
            rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_OPT_OUT_TODAY_ONLY,
                                          userContext.getYukonUser());
        if (isOptOutTodayOnly) {
            optOutBackingBean.setStartDate(today);
        }

        boolean isSameDay = today.isEqual(optOutBackingBean.getStartDate());
        model.addAttribute("isSameDay", isSameDay);

        int accountId = accountInfoFragment.getAccountId();
        List<DisplayableInventory> displayableInventories =
            displayableInventoryDao.getDisplayableInventory(accountId);
        Map<Integer, OptOutCountHolder> optOutCounts =
            getOptOutCountsForInventories(displayableInventories, accountId);

        model.addAttribute("displayableInventories", displayableInventories);
        model.addAttribute("optOutCounts", optOutCounts);

        return "operator/program/optOut/optOutDeviceSelection.jsp";
    }

    /**
     * This method can be called more than once if the operator has more than
     * one opt out survey to complete. Because of this, it also handles
     * committing an opt out.
     */
    @RequestMapping
    public String optOutQuestions(
            @ModelAttribute OptOutBackingBean optOutBackingBean,
            BindingResult bindingResult, ModelMap model,
            YukonUserContext userContext, FlashScope flashScope,
            AccountInfoFragment accountInfoFragment)
            throws ServletRequestBindingException, CommandCompletionException {
        setupOptOutModelMapBasics(accountInfoFragment, model, userContext);

        Integer[] inventoryIds = optOutBackingBean.getInventoryIds();
        if (inventoryIds == null || inventoryIds.length < 1) {
            flashScope.setMessage(new YukonMessageSourceResolvable("yukon.web.modules.operator.optOut.main.noInventorySelected"),
                                  FlashScopeMessageType.ERROR);

            return deviceSelection(optOutBackingBean, bindingResult, model,
                                   userContext, flashScope, accountInfoFragment);
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

        List<Integer> inventoryIdList = Arrays.asList(inventoryIds);
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

                // Find the oldest incomplete and valid survey and make it the current.
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
            return "operator/program/optOut/optOutSurvey.jsp";
        }

        if (noSurveysToTake && optOutBackingBean.getLegacyQuestions().isEmpty()) {
            // There are no surveys.  Check for legacy questions.
            List<String> questions =
                helper.getConfirmQuestions(messageSourceResolver,
                    userContext, "yukon.dr.operator.optoutconfirm.question.");

            if (!questions.isEmpty()) {
                model.addAttribute("questions", questions);
                setupOptOutModelMapBasics(accountInfoFragment, model, userContext);
                return "operator/program/optOut/optOutQuestions.jsp";
            }
        }

        // At this point, we have verified that no surveys/questions exist
        // or that all have been taken if any did exist.

        // Validate info entered on first page.
        OptOutValidator optOutValidator =
            optOutValidatorFactory.getOptOutValidator(userContext, true, accountInfoFragment);
        optOutValidator.validate(optOutBackingBean, bindingResult);

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
        CustomerAccount customerAccount =
            customerAccountDao.getById(accountInfoFragment.getAccountId());
        for (int inventoryId : optOutBackingBean.getInventoryIds()) {
            LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
            DateTime startDate =
                optOutBackingBean.getStartDate().toDateTimeAtStartOfDay(userContext.getJodaTimeZone());
            accountEventLogService.optOutAttemptedByOperator(userContext.getYukonUser(),
                                                             accountInfoFragment.getAccountNumber(),
                                                             lmHardwareBase.getManufacturerSerialNumber(),
                                                             startDate);
        }

        helper.processOptOut(optOutBackingBean, userContext,
                             customerAccount, surveyIdsByInventoryId);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.optOut.main.success"));
        return "redirect:view";
    }

    @RequestMapping
    public String optOutHistory(ModelMap model,
            AccountInfoFragment accountInfoFragment,
            YukonUserContext yukonUserContext) {

        // Get the list of completed and canceled opt outs
        List<OptOutEventDto> previousOptOutList =
            optOutEventDao.getOptOutHistoryForAccount(accountInfoFragment.getAccountId());
        model.addAttribute("previousOptOutList", previousOptOutList);
        Map<Integer, List<MessageSourceResolvable>> previousOptOutDetails =
            getHistoryActionLog(previousOptOutList, yukonUserContext);
        model.addAttribute("previousOptOutDetails", previousOptOutDetails);

        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);
        return "operator/program/optOut/optOutHistory.jsp";
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

    @RequestMapping
    public String confirmCancelOptOut(int eventId, int inventoryId,
            ModelMap model, YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) {
        helper.checkEventAgainstAccount(eventId, accountInfoFragment.getAccountId());
        HardwareSummary hardware = inventoryDao.findHardwareSummaryById(inventoryId);
        model.addAttribute("hardware", hardware);
        return "operator/program/optOut/confirmCancelOptOut.jsp";
    }

    @RequestMapping
    public String cancelOptOut(Integer eventId, FlashScope flashScope,
            ModelMap model, YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) throws Exception {

        // Log consumer opt out cancel attempt
        OptOutEvent optOutEvent = optOutEventDao.getOptOutEventById(eventId);
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(optOutEvent.getInventoryId());
        accountEventLogService.optOutCancelAttemptedByOperator(userContext.getYukonUser(),
                                                              accountInfoFragment.getAccountNumber(),
                                                              lmHardwareBase.getManufacturerSerialNumber(),
                                                              optOutEvent.getStartDate(),
                                                              optOutEvent.getStopDate());

        // Check that the inventory we're working with belongs to the current account
        helper.checkEventAgainstAccount(eventId, accountInfoFragment.getAccountId());

        LiteYukonUser user = userContext.getYukonUser();
        optOutService.cancelOptOut(Collections.singletonList(eventId), user);

        flashScope.setConfirm(
                       new YukonMessageSourceResolvable(
                              "yukon.web.modules.operator.optOut.main.cancelOptOut.successText"));

        setupOptOutModelMapBasics(accountInfoFragment, model, userContext);
        return closeDialog(model);
    }

    @RequestMapping
    public String confirmDecrementAllowances(int inventoryId, ModelMap model,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) {
        helper.checkInventoryAgainstAccount(inventoryId, accountInfoFragment.getAccountId());
        HardwareSummary hardware = inventoryDao.findHardwareSummaryById(inventoryId);
        model.addAttribute("hardware", hardware);
        return "operator/program/optOut/confirmDecrementAllowances.jsp";
    }

    @RequestMapping
    public String decrementAllowances(Integer inventoryId,
            FlashScope flashScope, ModelMap model,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment)
            throws ServletRequestBindingException {
        // Check that the inventory we're working with belongs to the current account
        CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
        helper.checkInventoryAgainstAccount(inventoryId, accountInfoFragment.getAccountId());

        optOutService.allowAdditionalOptOuts(customerAccount.getAccountId(), inventoryId,
                                             -1, userContext.getYukonUser());

        flashScope.setConfirm(
                       new YukonMessageSourceResolvable(
                               "yukon.web.modules.operator.optOut.main.decrementAllowance.successText"));

        setupOptOutModelMapBasics(accountInfoFragment, model, userContext);
        return closeDialog(model);
    }

    @RequestMapping
    public String confirmAllowAnother(int inventoryId, ModelMap model,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) {
        helper.checkInventoryAgainstAccount(inventoryId, accountInfoFragment.getAccountId());
        HardwareSummary hardware = inventoryDao.findHardwareSummaryById(inventoryId);
        model.addAttribute("hardware", hardware);
        return "operator/program/optOut/confirmAllowAnother.jsp";
    }

    @RequestMapping
    public String allowAnother(Integer inventoryId, FlashScope flashScope,
            ModelMap model, YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment)
            throws ServletRequestBindingException {

        // Log opt out addition attempt
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
        accountEventLogService.optOutLimitIncreaseAttemptedByOperator(userContext.getYukonUser(),
                                                                      accountInfoFragment.getAccountNumber(),
                                                                      lmHardwareBase.getManufacturerSerialNumber());

        // Check that the inventory we're working with belongs to the current account
        CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
        helper.checkInventoryAgainstAccount(inventoryId, accountInfoFragment.getAccountId());

        optOutService.allowAdditionalOptOuts(customerAccount.getAccountId(), inventoryId, 1,
                                             userContext.getYukonUser());

        flashScope.setConfirm(new YukonMessageSourceResolvable(
                              "yukon.web.modules.operator.optOut.main.allowOne.successText"));

        setupOptOutModelMapBasics(accountInfoFragment, model, userContext);
        return closeDialog(model);
    }

    @RequestMapping
    public String confirmResend(int inventoryId, ModelMap model,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) {
        helper.checkInventoryAgainstAccount(inventoryId, accountInfoFragment.getAccountId());
        HardwareSummary hardware = inventoryDao.findHardwareSummaryById(inventoryId);
        model.addAttribute("hardware", hardware);
        return "operator/program/optOut/confirmResend.jsp";
    }

    @RequestMapping
    public String resend(Integer inventoryId, FlashScope flashScope,
            ModelMap model, YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) throws Exception {

        // Log command resend attempt
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
        accountEventLogService.optOutResendAttemptedByOperator(userContext.getYukonUser(),
                                                               accountInfoFragment.getAccountNumber(),
                                                               lmHardwareBase.getManufacturerSerialNumber());

        // Check that the inventory we're working with belongs to the current account
        CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
        helper.checkInventoryAgainstAccount(inventoryId, accountInfoFragment.getAccountId());

        optOutService.resendOptOut(
                inventoryId,
                customerAccount.getAccountId(),
                userContext.getYukonUser());

        flashScope.setConfirm(
                       new YukonMessageSourceResolvable(
                               "yukon.web.modules.operator.optOut.main.resendOptOut.successText"));

        setupOptOutModelMapBasics(accountInfoFragment, model, userContext);
        return closeDialog(model);
    }

    @RequestMapping
    public String confirmResetToLimit(int inventoryId, ModelMap model,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) {

        helper.checkInventoryAgainstAccount(inventoryId, accountInfoFragment.getAccountId());
        HardwareSummary hardware = inventoryDao.findHardwareSummaryById(inventoryId);
        model.addAttribute("hardware", hardware);
        return "operator/program/optOut/confirmResetToLimit.jsp";
    }

    @RequestMapping
    public String resetToLimit(Integer inventoryId, FlashScope flashScope,
            ModelMap model, YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) {

        // Log opt out reset attempt
        LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(inventoryId);
        accountEventLogService.optOutLimitResetAttemptedByOperator(userContext.getYukonUser(),
                                                                   accountInfoFragment.getAccountNumber(),
                                                                   lmHardwareBase.getManufacturerSerialNumber());

        // Check that the inventory we're working with belongs to the current account
        helper.checkInventoryAgainstAccount(inventoryId, accountInfoFragment.getAccountId());

        optOutService.resetOptOutLimitForInventory(inventoryId, accountInfoFragment.getAccountId(),
                                                   userContext.getYukonUser());

        flashScope.setConfirm(
                       new YukonMessageSourceResolvable(
                               "yukon.web.modules.operator.optOut.main.resetToLimit.successText"));

        setupOptOutModelMapBasics(accountInfoFragment, model, userContext);
        return closeDialog(model);
    }

    @RequestMapping(params="cancel")
    public String cancel(ModelMap model,
            AccountInfoFragment accountInfoFragment,
            YukonUserContext userContext, HttpSession session) {

        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);
        return "redirect:view";
    }

    /**
     * This method builds up the basic modelMap entries for an optOutPage
     */
    private void setupOptOutModelMapBasics(AccountInfoFragment accountInfoFragment,
                                           ModelMap model,
                                           YukonUserContext userContext) {

        String optOutPeriodString = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.OPERATOR_OPT_OUT_PERIOD,  userContext.getYukonUser());
        List<Integer> optOutPeriodList = OptOutUtil.parseOptOutPeriodString(optOutPeriodString);
        model.addAttribute("optOutPeriodList", optOutPeriodList);

        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);

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
    public void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        this.accountEventLogService = accountEventLogService;
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
    public void setDatePropertyEditorFactory(DatePropertyEditorFactory datePropertyEditorFactory) {
        this.datePropertyEditorFactory = datePropertyEditorFactory;
    }

    @Autowired
    public void setDisplayableInventoryDao(
            DisplayableInventoryDao displayableInventoryDao) {
        this.displayableInventoryDao = displayableInventoryDao;
    }

    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }

    @Autowired
    public void setLmHardwareBaseDao(LMHardwareBaseDao lmHardwareBaseDao) {
        this.lmHardwareBaseDao = lmHardwareBaseDao;
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

    @Autowired
    public void setOptOutSurveyService(OptOutSurveyService optOutSurveyService) {
        this.optOutSurveyService = optOutSurveyService;
    }

    @Autowired
    public void setSurveyDao(SurveyDao surveyDao) {
        this.surveyDao = surveyDao;
    }

    @Autowired
    public void setHelper(OptOutControllerHelper helper) {
        this.helper = helper;
    }
    
    @Autowired
    public void setSurveyService(SurveyService surveyService) {
        this.surveyService = surveyService;
    }
    
    
}
