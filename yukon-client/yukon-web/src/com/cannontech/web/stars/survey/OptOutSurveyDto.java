package com.cannontech.web.stars.survey;

import java.util.Arrays;
import java.util.Date;

import com.cannontech.stars.dr.optout.model.OptOutSurvey;

public class OptOutSurveyDto {
    private int optOutSurveyId;
    private Integer[] loginGroupIds;
    private int surveyId;
    private Date startDate;
    private Date stopDate;
    private boolean specifyStopDate;
    private int energyCompanyId;

    public OptOutSurveyDto() {
    }

    public OptOutSurveyDto(OptOutSurvey optOutSurvey) {
        optOutSurveyId = optOutSurvey.getOptOutSurveyId();
        loginGroupIds = new Integer[optOutSurvey.getLoginGroupIds().size()];
        loginGroupIds = optOutSurvey.getLoginGroupIds().toArray(loginGroupIds);
        surveyId = optOutSurvey.getSurveyId();
        startDate = optOutSurvey.getStartDate();
        stopDate = optOutSurvey.getStopDate();
        specifyStopDate = stopDate != null;
        energyCompanyId = optOutSurvey.getEnergyCompanyId();
    }

    public int getOptOutSurveyId() {
        return optOutSurveyId;
    }

    public void setOptOutSurveyId(int optOutSurveyId) {
        this.optOutSurveyId = optOutSurveyId;
    }

    public int getSurveyId() {
        return surveyId;
    }

    public Integer[] getLoginGroupIds() {
        return loginGroupIds;
    }

    public void setLoginGroupIds(Integer[] loginGroupIds) {
        this.loginGroupIds = loginGroupIds;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
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

    public boolean isSpecifyStopDate() {
        return specifyStopDate;
    }

    public void setSpecifyStopDate(boolean specifyStopDate) {
        this.specifyStopDate = specifyStopDate;
    }

    public int getEnergyCompanyId() {
        return energyCompanyId;
    }

    public void setEnergyCompanyId(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }

    public OptOutSurvey getOptOutSurvey() {
        return new OptOutSurvey(optOutSurveyId, Arrays.asList(loginGroupIds),
                                 surveyId, startDate,
                                 specifyStopDate ? stopDate : null,
                                 energyCompanyId);
    }
}
