package com.cannontech.stars.dr.appliance.model;

public class ApplianceCategory {
    private final int applianceCategoryId;
    private final ApplianceType applianceType;
    private final String logoPath;
    
    public ApplianceCategory(int applianceCategoryId, ApplianceType applianceType, String logoPath) {
        this.applianceCategoryId = applianceCategoryId;
        this.applianceType = applianceType;
        this.logoPath = logoPath;
    }

    public int getApplianceCategoryId() {
        return applianceCategoryId;
    }

    public ApplianceType getApplianceType() {
        return applianceType;
    }

    public String getLogoPath() {
        return logoPath;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + applianceCategoryId;
        result = prime * result + ((applianceType == null) ? 0
                : applianceType.hashCode());
        result = prime * result + ((logoPath == null) ? 0 : logoPath.hashCode());
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
        final ApplianceCategory other = (ApplianceCategory) obj;
        if (applianceCategoryId != other.applianceCategoryId)
            return false;
        if (applianceType == null) {
            if (other.applianceType != null)
                return false;
        } else if (!applianceType.equals(other.applianceType))
            return false;
        if (logoPath == null) {
            if (other.logoPath != null)
                return false;
        } else if (!logoPath.equals(other.logoPath))
            return false;
        return true;
    }
    
}
