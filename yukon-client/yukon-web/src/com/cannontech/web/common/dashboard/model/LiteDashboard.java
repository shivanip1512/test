package com.cannontech.web.common.dashboard.model;

import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * A light class for simple summary of dashboards with basic details. Doesn't contain any information about associated
 * widgets.
 */
public class LiteDashboard {
    private int dashboardId;
    private String name;
    private String description;
    private DashboardPageType pageType;
    private LiteYukonUser owner;
    private Visibility visibility;
    private int users;
    
    public int getDashboardId() {
        return dashboardId;
    }
    
    public void setDashboardId(int dashboardId) {
        this.dashboardId = dashboardId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public LiteYukonUser getOwner() {
        return owner;
    }

    public void setOwner(LiteYukonUser owner) {
        this.owner = owner;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public int getUsers() {
        return users;
    }

    public void setUsers(int users) {
        this.users = users;
    }

    public DashboardPageType getPageType() {
        return pageType;
    }

    public void setPageType(DashboardPageType pageType) {
        this.pageType = pageType;
    }
    
}
