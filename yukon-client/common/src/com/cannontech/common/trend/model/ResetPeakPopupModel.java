package com.cannontech.common.trend.model;

import org.joda.time.DateTime;

public class ResetPeakPopupModel extends ResetPeakModel {

    private Integer trendId;
    private boolean resetPeakForAllTrends;
    private ResetPeakDuration resetPeakDuration;
    
    public ResetPeakPopupModel() {
    }
    
    public ResetPeakPopupModel(Integer trendId) {
        this.trendId = trendId;
        this.resetPeakForAllTrends = false;
        this.resetPeakDuration = ResetPeakDuration.TODAY;
        this.setStartDate(DateTime.now());
    }

    public Integer getTrendId() {
        return trendId;
    }

    public void setTrendId(Integer trendId) {
        this.trendId = trendId;
    }

    public boolean isResetPeakForAllTrends() {
        return resetPeakForAllTrends;
    }

    public void setResetPeakForAllTrends(boolean resetPeakForAllTrends) {
        this.resetPeakForAllTrends = resetPeakForAllTrends;
    }

    public ResetPeakDuration getResetPeakDuration() {
        return resetPeakDuration;
    }

    public void setResetPeakDuration(ResetPeakDuration resetPeakDuration) {
        this.resetPeakDuration = resetPeakDuration;
    }
}