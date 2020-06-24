package com.cannontech.common.device.model;

import com.cannontech.common.device.dao.DevicePointDao.SortBy;

public enum DevicePointsSortBy {

    pointName(SortBy.POINT_NAME),
    offset(SortBy.POINT_OFFSET),
    deviceName(SortBy.DEVICE_NAME),
    pointType(SortBy.POINT_TYPE);


    private DevicePointsSortBy(SortBy value) {
        this.value = value;
    }

    private final SortBy value;

    public SortBy getValue() {
        return value;
    }
}
