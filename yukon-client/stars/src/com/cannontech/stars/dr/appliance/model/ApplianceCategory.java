package com.cannontech.stars.dr.appliance.model;

public class ApplianceCategory {
    private final int applianceCategoryId;
    private final String categoryLabel;
    private final ApplianceTypeEnum applianceTypeEnum;
    private final String logoPath;

    public ApplianceCategory(int applianceCategoryId,
                             String categoryLabel,
                             ApplianceTypeEnum applianceType, 
                             String logoPath) {
        this.applianceCategoryId = applianceCategoryId;
        this.categoryLabel = categoryLabel;
        this.applianceTypeEnum = applianceType;
        this.logoPath = logoPath;
    }

    public int getApplianceCategoryId() {
        return applianceCategoryId;
    }

    public String getCategoryLabel() {
        return categoryLabel;
    }

    public ApplianceTypeEnum getApplianceTypeEnum() {
        return applianceTypeEnum;
    }

    public String getLogoPath() {
        return logoPath;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + applianceCategoryId;
        result = prime * result + ((applianceTypeEnum == null) ? 0
                : applianceTypeEnum.hashCode());
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
        if (!categoryLabel.equalsIgnoreCase(other.categoryLabel)){
            return false;
        }
        if (applianceTypeEnum == null) {
            if (other.applianceTypeEnum != null)
                return false;
        } else if (!applianceTypeEnum.equals(other.applianceTypeEnum))
            return false;
        if (logoPath == null) {
            if (other.logoPath != null)
                return false;
        } else if (!logoPath.equals(other.logoPath))
            return false;
        return true;
    }
    
}
