package com.cannontech.core.authorization.support;

import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * Implementation of CommandDeviceCheck. Checks that a given device has the same
 * category and paoClass as the implementation
 */
public class DeviceCheckCategoryClass implements DeviceCheck {

    private Integer category = null;
    private Integer paoClass = null;

    public boolean checkDevice(LiteYukonPAObject device) {
        if(device == null){
            return false;
        }
        return device.getCategory() == this.category && device.getPaoClass() == paoClass;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Integer getPaoClass() {
        return paoClass;
    }

    public void setPaoClass(Integer paoClass) {
        this.paoClass = paoClass;
    }

}
