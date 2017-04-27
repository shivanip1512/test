package com.cannontech.web.common.dashboard.model;

import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * This class provides a basic API for all features shared between dashboard object types.
 */
public abstract class DashboardBase {
    
    private int dashboardId;
    private String name;
    private String description;
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
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + dashboardId;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((owner == null) ? 0 : owner.hashCode());
        result = prime * result + ((visibility == null) ? 0 : visibility.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DashboardBase other = (DashboardBase) obj;
        if (dashboardId != other.dashboardId) {
            return false;
        }
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (owner == null) {
            if (other.owner != null) {
                return false;
            }
        } else if (!owner.equals(other.owner)) {
            return false;
        }
        if (visibility != other.visibility) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Dashboard [dashboardId=" + dashboardId + ", name=" + name + ", description=" + description
               + ", owner=" + owner + ", visibility=" + visibility + "]";
    }
}
