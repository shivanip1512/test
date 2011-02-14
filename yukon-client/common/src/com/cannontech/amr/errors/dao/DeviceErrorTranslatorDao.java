package com.cannontech.amr.errors.dao;

import com.cannontech.amr.errors.model.DeviceErrorDescription;


public interface DeviceErrorTranslatorDao {
    public DeviceErrorDescription translateErrorCode(int error);
    public Iterable<DeviceErrorDescription> getAllErrors();
}