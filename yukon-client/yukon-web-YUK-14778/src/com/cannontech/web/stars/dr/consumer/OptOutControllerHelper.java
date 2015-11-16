package com.cannontech.web.stars.dr.consumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.survey.model.Result;
import com.cannontech.common.survey.model.ResultAnswer;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.model.ScheduledOptOutQuestion;
import com.cannontech.stars.dr.optout.service.OptOutRequest;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.model.OptOutBackingBean;
import com.cannontech.web.stars.dr.operator.model.OptOutBackingBean.SurveyResult;
import com.cannontech.web.stars.dr.operator.model.OptOutBackingBean.SurveyResultAnswer;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * This class strives to do things that the consumer and operator opt out
 * controllers have in common.
 */
public class OptOutControllerHelper {
    private InventoryBaseDao inventoryBaseDao;
    private OptOutService optOutService;
    private OptOutEventDao optOutEventDao;
    
    private final Logger logger = YukonLogManager.getLogger(OptOutControllerHelper.class);
    
    public List<String> getConfirmQuestions(YukonUserContextMessageSourceResolver messageSourceResolver, 
            YukonUserContext yukonUserContext, String keyPrefix) {
        
        final MessageSourceAccessor messageSourceAccessor = 
            messageSourceResolver.getMessageSourceAccessor(yukonUserContext);
        
        final List<String> questionList = new ArrayList<String>();

        int index = 1;
        boolean done = false;

        while (!done) {
            try {
                String code = keyPrefix + index++;
                String question = StringEscapeUtils.escapeHtml4(messageSourceAccessor.getMessage(code));
                questionList.add(question);
            } catch (NoSuchMessageException e) {
                done = true;
            }
        }
        
        return questionList;
    }

    /**
     * Handle the processing for an opt out.
     */
    public void processOptOut(OptOutBackingBean optOutBackingBean,
            YukonUserContext userContext, CustomerAccount customerAccount,
            Multimap<Integer, Integer> surveyIdsByInventoryId)
            throws CommandCompletionException {
        checkInventoryAgainstAccount(optOutBackingBean.getInventoryIds(),
                                     customerAccount.getAccountId());

        List<ScheduledOptOutQuestion> questionList = optOutBackingBean.getLegacyQuestions();
        List<Result> surveyResults =
            translateSurveyResults(customerAccount.getAccountId(),
                                   customerAccount.getAccountNumber(),
                                   optOutBackingBean);

        OptOutRequest optOutRequest = new OptOutRequest();
        optOutRequest.setInventoryIdList(Arrays.asList(optOutBackingBean.getInventoryIds()));
        optOutRequest.setQuestions(questionList);
        optOutRequest.setSurveyResults(surveyResults);
        optOutRequest.setSurveyIdsByInventoryId(surveyIdsByInventoryId);
        LocalDate today = new LocalDate(userContext.getJodaTimeZone());
        Duration duration =
            optOutService.calculateDuration(optOutBackingBean.getStartDate(),
                                             optOutBackingBean.getDurationInDays(),
                                             userContext);
        logger.info("Start date:" + today.toString("MM/dd/yy") + "  Duration in days:"
                    + optOutBackingBean.getDurationInDays() + " calculated duration in hours:"
                    + duration.toStandardHours().getHours());
 
        optOutRequest.setDurationInHours(duration.toStandardHours().getHours());
        boolean isSameDay = today.isEqual(optOutBackingBean.getStartDate());
        if (isSameDay) {
            optOutRequest.setStartDate(null); // Same day OptOut's have null start dates
        } else {
            DateTime startDateTime =
                optOutBackingBean.getStartDate()
                    .toDateTimeAtStartOfDay(userContext.getJodaTimeZone());
            optOutRequest.setStartDate(startDateTime.toInstant());
        }
        
        optOutService.optOut(customerAccount, optOutRequest, userContext.getYukonUser());
    }
    
    private List<Result> translateSurveyResults(int accountId,
            String accountNumber, OptOutBackingBean optOutBackingBean) {
        List<Result> retVal = Lists.newArrayList();
        ReadableInstant now = new Instant();

        for (SurveyResult surveyResult : optOutBackingBean.getSurveyResults()) {
            List<ResultAnswer> resultAnswers = Lists.newArrayList();
            for (SurveyResultAnswer answer : surveyResult.getAnswers()) {
                Integer surveyQuestionAnswerId = null;
                String dropDownAnswer = answer.getDropDownAnswer();
                if (StringUtils.isNotEmpty(dropDownAnswer) && !"other".equals(dropDownAnswer)
                        && !"pleaseChoose".equals(dropDownAnswer)) {
                    surveyQuestionAnswerId = Integer.parseInt(dropDownAnswer);
                }

                String textAnswer = null;
                if (StringUtils.isEmpty(dropDownAnswer) || "other".equals(dropDownAnswer)) {
                    textAnswer = answer.getTextAnswer();
                }
                resultAnswers.add(new ResultAnswer(answer.getQuestionId(),
                                                   surveyQuestionAnswerId,
                                                   textAnswer));
            }
            Result result = new Result(surveyResult.getSurveyId(), accountId,
                                       accountNumber, now, resultAnswers);
            retVal.add(result);
        }

        return retVal;
    }

    /**
     * Helper method to make sure the event being updated belongs to the current account
     */
    public void checkEventAgainstAccount(int eventId, int accountId) {
        OptOutEvent event = optOutEventDao.getOptOutEventById(eventId);
        if (event.getCustomerAccountId() != accountId) {
            throw new NotAuthorizedException("The Opt Out event with id: " + eventId +
                    " does not belong to the current customer account with id: " + accountId);
        }
    }

    public void checkInventoryAgainstAccount(int inventoryId, int accountId) {
        LiteInventoryBase inventory = inventoryBaseDao.getByInventoryId(inventoryId);
        if (inventory.getAccountID() != accountId) {
            throw new NotAuthorizedException("The Inventory with id: " +
                                             inventoryId +
                                             " does not belong to the current customer account with id: " +
                                             accountId);
        }
    }

    /**
     * Helper method to make sure the inventory being used belongs to the current account
     */
    private void checkInventoryAgainstAccount(Integer[] inventoryIds,
            int accountId) {
        for (Integer inventoryId : inventoryIds) {
            checkInventoryAgainstAccount(inventoryId, accountId);
        }
    }

    @Autowired
    public void setInventoryBaseDao(InventoryBaseDao inventoryBaseDao) {
        this.inventoryBaseDao = inventoryBaseDao;
    }

    @Autowired
    public void setOptOutService(OptOutService optOutService) {
        this.optOutService = optOutService;
    }

    @Autowired
    public void setOptOutEventDao(OptOutEventDao optOutEventDao) {
        this.optOutEventDao = optOutEventDao;
    }
}
