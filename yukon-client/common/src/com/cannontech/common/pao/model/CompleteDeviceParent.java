package com.cannontech.common.pao.model;

import com.cannontech.common.pao.annotation.YukonPaoField;
import com.cannontech.common.pao.annotation.YukonPaoPart;
import com.google.common.base.Objects;

@YukonPaoPart(idColumnName = "deviceId")
public class CompleteDeviceParent {
    private int parentId = 0;

    @YukonPaoField
    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parentId);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() != CompleteDeviceParent.class) {
            return false;
        }
        CompleteDeviceParent that = (CompleteDeviceParent) other;
        return parentId == that.parentId;
    }

    @Override
    public String toString() {
        return "CompleteDeviceParent [parentId=" + parentId + "]";
    }
}
