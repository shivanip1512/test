package com.cannontech.stars.dr.optout.model;

import java.util.Collection;
import java.util.Date;

import com.google.common.collect.Lists;

public class OptOutSurvey {
    private int optOutSurveyId;
    private Collection<Integer> programIds = Lists.newArrayList();
    private int surveyId;
    private String surveyName;
    private Date startDate;
    private Date stopDate;
    private int energyCompanyId;

    public OptOutSurvey() {
    }

    public OptOutSurvey(int optOutSurveyId, Collection<Integer> programIds,
            int surveyId, Date startDate, Date stopDate, int energyCompanyId) {
        this.optOutSurveyId = optOutSurveyId;
        this.programIds = programIds;
        this.surveyId = surveyId;
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.energyCompanyId = energyCompanyId;
    }

    public int getOptOutSurveyId() {
        return optOutSurveyId;
    }

    public void setOptOutSurveyId(int optOutSurveyId) {
        this.optOutSurveyId = optOutSurveyId;
    }

    public Collection<Integer> getProgramIds() {
        return programIds;
    }

    public void addProgramId(Integer programId) {
        programIds.add(programId);
    }

    public void setProgramIds(Collection<Integer> programIds) {
        this.programIds = programIds;
    }

    public int getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
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

    public int getEnergyCompanyId() {
        return energyCompanyId;
    }

    public void setEnergyCompanyId(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + optOutSurveyId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OptOutSurvey other = (OptOutSurvey) obj;
        if (optOutSurveyId != other.optOutSurveyId)
            return false;
        return true;
    }
}
