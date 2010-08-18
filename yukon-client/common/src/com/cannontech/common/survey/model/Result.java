package com.cannontech.common.survey.model;

import java.util.List;

import org.joda.time.DateTime;

public class Result {
    private int surveyResultId;
    private int surveyId;
    private int accountId;
    private String accountNumber;
    private DateTime whenTaken;
    private List<ResultAnswer> resultAnswers;

    public Result(int surveyId, int accountId, String accountNumber,
            DateTime whenTaken, List<ResultAnswer> resultAnswers) {
        surveyResultId = 0;
        this.surveyId = surveyId;
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.whenTaken = whenTaken;
        this.resultAnswers = resultAnswers;
    }

    public int getSurveyResultId() {
        return surveyResultId;
    }

    public void setSurveyResultId(int surveyResultId) {
        this.surveyResultId = surveyResultId;
    }

    public int getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public DateTime getWhenTaken() {
        return whenTaken;
    }

    public void setWhenTaken(DateTime whenTaken) {
        this.whenTaken = whenTaken;
    }

    public List<ResultAnswer> getResultAnswers() {
        return resultAnswers;
    }

    public void setResultAnswers(List<ResultAnswer> resultAnswers) {
        this.resultAnswers = resultAnswers;
    }
}
