package com.cannontech.web.updater.deviceConfigSummary;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.device.config.dao.DeviceConfigSummaryDao;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryDetail;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastActionStatus;
import com.cannontech.web.updater.UpdateBackingService;

public class DeviceConfigSummaryBackingService implements UpdateBackingService {
    
    @Autowired private DeviceConfigSummaryDao deviceConfigSummaryDao;

    private enum RequestType {
        IS_IN_PROGRESS;
    }
    
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {
        String[] idParts = StringUtils.split(identifier, "/");
        String deviceId = idParts[0];
        RequestType type = RequestType.valueOf(idParts[1]);
        if (type == RequestType.IS_IN_PROGRESS) {
            DeviceConfigSummaryDetail detail = deviceConfigSummaryDao.getSummaryForDevice(Integer.valueOf(deviceId));
            return Boolean.toString(detail.getStatus() == LastActionStatus.IN_PROGRESS);
        }
        return null;
    }

    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }
}