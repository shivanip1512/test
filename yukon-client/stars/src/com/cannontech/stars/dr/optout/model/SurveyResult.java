package com.cannontech.stars.dr.optout.model;

import org.joda.time.Instant;

public class SurveyResult {
    private int surveyResultId;
    private int surveyId;
    private Instant whenTaken;
    private Integer accountId;
    private String accountNumber;
    private Integer surveyQuestionAnswerId;
    private String answerKey;
    private String textAnswer;
    private int inventoryId;
    private Instant scheduledDate;
    private Instant startDate;
    private Instant stopDate;

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

    public Instant getWhenTaken() {
        return whenTaken;
    }

    public void setWhenTaken(Instant whenTaken) {
        this.whenTaken = whenTaken;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Integer getSurveyQuestionAnswerId() {
        return surveyQuestionAnswerId;
    }

    public void setSurveyQuestionAnswerId(Integer surveyQuestionAnswerId) {
        this.surveyQuestionAnswerId = surveyQuestionAnswerId;
    }


    public String getAnswerKey() {
        return answerKey;
    }

    public void setAnswerKey(String answerKey) {
        this.answerKey = answerKey;
    }

    public String getTextAnswer() {
        return textAnswer;
    }

    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Instant getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Instant scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getStopDate() {
        return stopDate;
    }

    public void setStopDate(Instant stopDate) {
        this.stopDate = stopDate;
    }
}
