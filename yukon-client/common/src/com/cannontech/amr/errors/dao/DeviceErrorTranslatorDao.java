package com.cannontech.amr.errors.dao;

import com.cannontech.amr.errors.model.DeviceErrorDescription;


public interface DeviceErrorTranslatorDao {
    public DeviceErrorDescription traslateErrorCode(int error);
}
