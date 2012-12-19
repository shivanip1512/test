package com.cannontech.amr.errors.dao;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.user.YukonUserContext;


public interface DeviceErrorTranslatorDao {
    public DeviceErrorDescription translateErrorCode(int error);
    public DeviceErrorDescription translateErrorCode(int error, YukonUserContext userContext);
    public Iterable<DeviceErrorDescription> getAllErrors();
    public Iterable<DeviceErrorDescription> getAllErrors(YukonUserContext userContext);
}