package com.cannontech.web.common.dashboard.model;

import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * This interface provides a basic API for all features shared between dashboard object types.
 */
public interface DashboardBase {
    
    public int getDashboardId();
    
    public void setDashboardId(int dashboardId);
    
    public String getName();
    
    public void setName(String name);
    
    public String getDescription();
    
    public void setDescription(String description);

    public LiteYukonUser getOwner();

    public void setOwner(LiteYukonUser owner);

    public Visibility getVisibility();

    public void setVisibility(Visibility visibility);
}
