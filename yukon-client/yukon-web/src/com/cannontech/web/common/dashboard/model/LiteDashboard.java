package com.cannontech.web.common.dashboard.model;

import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * A light class for simple summary of dashboards with basic details. Doesn't contain any information about associated
 * widgets.
 */
public class LiteDashboard implements DashboardBase {
    private int dashboardId;
    private String name;
    private String description;
    private DashboardPageType pageType;
    private LiteYukonUser owner;
    private Visibility visibility;
    private int users;
    
    @Override
    public int getDashboardId() {
        return dashboardId;
    }
    
    @Override
    public void setDashboardId(int dashboardId) {
        this.dashboardId = dashboardId;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public LiteYukonUser getOwner() {
        return owner;
    }

    @Override
    public void setOwner(LiteYukonUser owner) {
        this.owner = owner;
    }

    @Override
    public Visibility getVisibility() {
        return visibility;
    }

    @Override
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
