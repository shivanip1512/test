package com.cannontech.analysis.tablemodel;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.survey.model.Question;
import com.cannontech.common.survey.model.Survey;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccountWithNames;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.model.SurveyResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class SurveyResultsDetailModel extends
        SurveyResultsModelBase<SurveyResultsDetailModel.ModelRow> {
    // inputs
    private String accountNumber;
    private String deviceSerialNumber;

    // member variables
    private static String title = "Survey Results Detail Report";

    private OptOutEventDao optOutEventDao;
    private InventoryDao inventoryDao;
    private CustomerAccountDao customerAccountDao;

    public static class ModelRow implements Cloneable {
        public String accountNumber;
        public String serialNumber;
        public String altTrackingNumber;
        public Object reason;
        public Date scheduledDate;
        public Date startDate;
        public Date endDate;
        public String loadProgram;
        public String gear;
    }

    public void doLoadData() {
        if (startDate == null) {
            startDate = new Instant(0).toDate();
        }

        List<SurveyResult> surveyResults =
            optOutSurveyDao.findSurveyResults(surveyId, questionId, answerIds,
                                              includeOtherAnswers, includeUnanswered,
                                              new Instant(startDate), new Instant(endDate),
                                              accountNumber, deviceSerialNumber);

        Survey survey = surveyDao.getSurveyById(surveyId);
        Question question = surveyDao.getQuestionById(questionId);
        String baseKey = survey.getBaseKey(question);
        Multimap<SurveyResult, OptOutEvent> optOutsBySurveyResult =
            optOutEventDao.getOptOutsBySurveyResult(surveyResults);
        List<Integer> inventoryIds = Lists.newArrayList();
        List<Integer> accountIds = Lists.newArrayList();
        for (SurveyResult result : surveyResults) {
            Integer accountId = result.getAccountId();
            if (accountId != null && accountId != 0) {
                accountIds.add(result.getAccountId());
            }
            for (OptOutEvent event : optOutsBySurveyResult.get(result)) {
                inventoryIds.add(event.getInventoryId());
            }
        }

        Map<Integer, HardwareSummary> hardwareSummariesById =
            inventoryDao.findHardwareSummariesById(inventoryIds);
        Map<Integer, CustomerAccountWithNames> accountsByAccountId =
            customerAccountDao.getAccountsWithNamesByAccountId(accountIds);
        for (SurveyResult result : surveyResults) {
            for (OptOutEvent event : optOutsBySurveyResult.get(result)) {
                ModelRow row = new ModelRow();
                row.accountNumber = result.getAccountNumber();
                HardwareSummary hardwareSummary = hardwareSummariesById.get(event.getInventoryId());
                row.serialNumber = hardwareSummary.getSerialNumber();
                row.altTrackingNumber = "";
                CustomerAccountWithNames account = accountsByAccountId.get(result.getAccountId());
                if (account != null) {
                    row.altTrackingNumber = account.getAlternateTrackingNumber();
                }
                row.reason = getReason(result, baseKey);
                row.scheduledDate = event.getScheduledDate().toDate();
                row.startDate = event.getStartDate().toDate();
                row.endDate = event.getStopDate().toDate();

                row.loadProgram = "TODO: program";
                row.gear = "TODO: gear";

                data.add(row);
            }
        }
    }

    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public static void setTitle(String title) {
        SurveyResultsDetailModel.title = title;
    }

    @Autowired
    public void setOptOutEventDao(OptOutEventDao optOutEventDao) {
        this.optOutEventDao = optOutEventDao;
    }

    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }

    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }
}
