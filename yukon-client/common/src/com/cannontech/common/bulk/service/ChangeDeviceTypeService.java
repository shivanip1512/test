package com.cannontech.common.bulk.service;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;

public interface ChangeDeviceTypeService {

    public SimpleDevice changeDeviceType(SimpleDevice device, PaoType newDeviceType);
}
