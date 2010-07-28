package com.cannontech.common.survey.model;

public class Survey {
    private int surveyId;
    private int energyCompanyId;
    private String surveyName;
    private String surveyKey;

    public int getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }

    public int getEnergyCompanyId() {
        return energyCompanyId;
    }

    public void setEnergyCompanyId(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }

    public String getSurveyKey() {
        return surveyKey;
    }

    public void setSurveyKey(String surveyKey) {
        this.surveyKey = surveyKey;
    }
}
