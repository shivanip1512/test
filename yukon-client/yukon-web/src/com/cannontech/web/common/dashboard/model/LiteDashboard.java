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
        setVisibility(dashboard.getVisibility());
        if (users != null) {
            this.users = users;
        }
    }
    
    public int getUsers() {
        return users;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + users;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        LiteDashboard other = (LiteDashboard) obj;
        if (users != other.users) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " [users=" + users + "]";
    }
}
