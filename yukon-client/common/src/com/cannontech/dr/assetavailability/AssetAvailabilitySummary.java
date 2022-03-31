package com.cannontech.dr.assetavailability;

import java.util.Objects;

/**
 * A summary for a set of inventory, containing the number of inventory in each asset availability state.
 */
public class AssetAvailabilitySummary {
    private Integer unavailableSize = 0;
    private Integer inactiveSize = 0;
    private Integer activeSize = 0;
    private Integer optedOutSize = 0;
    private Integer totalSize = 0;

    public Integer getUnavailableSize() {
        return unavailableSize;
    }

    public void setUnavailableSize(Integer unavailableSize) {
        this.unavailableSize = unavailableSize;
    }

    public Integer getInactiveSize() {
        return inactiveSize;
    }

    public void setInactiveSize(Integer inactiveSize) {
        this.inactiveSize = inactiveSize;
    }

    public Integer getActiveSize() {
        return activeSize;
    }

    public void setActiveSize(Integer activeSize) {
        this.activeSize = activeSize;
    }

    public Integer getOptedOutSize() {
        return optedOutSize;
    }

    public void setOptedOutSize(Integer optedOutSize) {
        this.optedOutSize = optedOutSize;
    }

    public Integer getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Integer totalSize) {
        this.totalSize = totalSize;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(activeSize, inactiveSize, optedOutSize, totalSize, unavailableSize);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AssetAvailabilitySummary other = (AssetAvailabilitySummary) obj;
        return Objects.equals(activeSize, other.activeSize) && Objects.equals(inactiveSize, other.inactiveSize)
                && Objects.equals(optedOutSize, other.optedOutSize) && Objects.equals(totalSize, other.totalSize)
                && Objects.equals(unavailableSize, other.unavailableSize);
    }
}
