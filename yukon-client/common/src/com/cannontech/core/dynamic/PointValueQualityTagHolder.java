package com.cannontech.core.dynamic;

public interface PointValueQualityTagHolder extends PointValueQualityHolder {
    public long getTags();
    public boolean isTagsOldTimestamp();
    public boolean isTagsUnsolicited();
}
