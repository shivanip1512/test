package com.cannontech.web.stars.survey;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

public class ReportConfig {
    private int surveyId;
    private String reportType;
    private Date startDate;
    private Date stopDate;
    private int questionId;
    private List<Integer> answerIds = Lists.newArrayList();
    private boolean includeOtherAnswers;
    private boolean includeUnanswered;
    private List<Integer> programIds = Lists.newArrayList();
    private String accountNumber;
    private String deviceSerialNumber;

    public int getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStopDate() {
        return stopDate;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    // Spring can handle creating integer arrays more easily than lists.
    public Integer[] getAnswerId() {
        return answerIds.toArray(new Integer[answerIds.size()]);
    }

    public void setAnswerId(Integer[] answerIds) {
        this.answerIds = Arrays.asList(answerIds);
    }

    public List<Integer> getAnswerIds() {
        return answerIds;
    }

    public void setAnswerIds(List<Integer> answerIds) {
        this.answerIds = answerIds;
    }

    public boolean isIncludeOtherAnswers() {
        return includeOtherAnswers;
    }

    public void setIncludeOtherAnswers(boolean includeOtherAnswers) {
        this.includeOtherAnswers = includeOtherAnswers;
    }

    public boolean isIncludeUnanswered() {
        return includeUnanswered;
    }

    public void setIncludeUnanswered(boolean includeUnanswered) {
        this.includeUnanswered = includeUnanswered;
    }

    public Integer[] getProgramId() {
        return programIds.toArray(new Integer[programIds.size()]);
    }

    public void setProgramId(Integer[] programId) {
        this.programIds = Arrays.asList(programId);
    }

    public List<Integer> getProgramIds() {
        return programIds;
    }

    public void setProgramIds(List<Integer> programIds) {
        this.programIds = programIds;
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
}
