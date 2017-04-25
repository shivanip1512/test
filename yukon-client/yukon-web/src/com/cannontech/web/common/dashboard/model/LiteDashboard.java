package com.cannontech.web.common.dashboard.model;

/**
 * A light class for simple summary of dashboards with basic details. Doesn't contain any information about
 * associated
 * widgets.
 */
public class LiteDashboard extends DashboardBase {

    private int users;

    public LiteDashboard(DashboardBase dashboard, Integer users) {
        setDashboardId(dashboard.getDashboardId());
        setDescription(dashboard.getDescription());
        setName(dashboard.getName());
        setOwner(dashboard.getOwner());
        setPageType(dashboard.getPageType());
        setVisibility(dashboard.getVisibility());
        if (users != null) {
            this.users = users;
        }
    }
    
    public int getUsers() {
        return users;
    }

    @Override
    public String toString() {
        return super.toString() + " [users=" + users + "]";
    }
}
