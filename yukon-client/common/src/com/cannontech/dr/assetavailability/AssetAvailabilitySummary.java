package com.cannontech.dr.assetavailability;

/**
 * This has the summary of asset availability. The count of the asset based on
 * there status
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
}
