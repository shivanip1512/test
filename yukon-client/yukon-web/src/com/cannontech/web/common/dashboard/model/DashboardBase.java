package com.cannontech.web.common.dashboard.model;

import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * This class provides a basic API for all features shared between dashboard object types.
 */
public abstract class DashboardBase {
    
    private int dashboardId;
    private String name;
    private String description;
    private DashboardPageType pageType;
    private LiteYukonUser owner;
    private Visibility visibility;
    
    public int getDashboardId() {
        return dashboardId;
    }
    
    public void setDashboardId(int id) {
        dashboardId = id;
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
    
    public DashboardPageType getPageType() {
        return pageType;
    }

    public void setPageType(DashboardPageType pageType) {
        this.pageType = pageType;
    }
    
    @Override
    public String toString() {
        return "Dashboard [dashboardId=" + dashboardId + ", name=" + name + ", description=" + description
               + ", pageType=" + pageType + ", owner=" + owner + ", visibility=" + visibility + "]";
    }
}
