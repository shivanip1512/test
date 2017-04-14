package com.cannontech.web.common.dashboard.model;

public class DashboardSetting {
    
    private DashboardPageType pageType;
    private int dashboardId;
    
    public DashboardPageType getPageType() {
        return pageType;
    }
    public void setPageType(DashboardPageType pageType) {
        this.pageType = pageType;
    }
    public int getDashboardId() {
        return dashboardId;
    }
    public void setDashboardId(int dashboardId) {
        this.dashboardId = dashboardId;
    }
}
