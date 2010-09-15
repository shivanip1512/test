package com.cannontech.web.stars.survey;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ReportConfig {
    private int surveyId;
    private String reportType;
    private Date start;
    private Date end;
    private int questionId;
    private Integer[] answerIds = {};
    private boolean includeOtherAnswers;
    private boolean includeUnanswered;
    private Integer[] programIds = {};
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

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public Integer[] getAnswerIds() {
        return answerIds;
    }

    public void setAnswerIds(Integer[] answerIds) {
        this.answerIds = answerIds;
    }

    public List<Integer> getAnswerIdList() {
        return Arrays.asList(answerIds);
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

    public Integer[] getProgramIds() {
        return programIds;
    }

    public void setProgramIds(Integer[] programIds) {
        this.programIds = programIds;
    }

    public List<Integer> getProgramIdList() {
        return Arrays.asList(programIds);
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
