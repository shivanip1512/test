package com.cannontech.web.updater.deviceConfig;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao.ConfigState;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastActionStatus;
import com.cannontech.common.device.config.model.DeviceConfigState;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;

public class DeviceConfigBackingService implements UpdateBackingService {
    
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    private final static String baseKey = "yukon.web.modules.tools.configs.summary.status.";
        
    private enum RequestType {
        STATUS_TEXT,
        IN_PROGRESS
        ;
    }
    
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {
        String[] idParts = StringUtils.split(identifier, "/");
        String deviceId = idParts[0];
        RequestType type = RequestType.valueOf(idParts[1]);
        if (type == RequestType.STATUS_TEXT) {
            
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            
            DeviceConfigState configState = deviceConfigurationDao.getDeviceConfigStatesByDeviceId(Integer.valueOf(deviceId));
            configState.setState(ConfigState.IN_SYNC);
            
            if (configState == null
                    || (configState.getState() == ConfigState.UNASSIGNED || configState.getState() == ConfigState.UNKNOWN)) {
                // "Current Configuration: None" should display. No Status row, no Actions row, but with the Change Configuration row
                // still below.
                return null;
            } else if (configState.getStatus() == LastActionStatus.IN_PROGRESS) {
                return accessor.getMessage(baseKey + "IN_PROGRESS");
                // disable buttons
                // display status "In Progress" -  we can end up in this state for a short while
            } else if (configState.getState() == ConfigState.IN_SYNC) {
                // display status "in sync"
                return accessor.getMessage(baseKey + "IN_SYNC");
            } else if (configState.getState() == ConfigState.OUT_OF_SYNC || configState.getState() == ConfigState.UNREAD) {
                // display status "needs upload"
                return accessor.getMessage(baseKey + "NEEDS_UPLOAD");
            } else if (configState.getState() == ConfigState.UNCONFIRMED) {
                // display status "needs validation"
                return accessor.getMessage(baseKey + "NEEDS_VALIDATION");
            }
        } else if (type == RequestType.IN_PROGRESS) {
            DeviceConfigState configState = deviceConfigurationDao.getDeviceConfigStatesByDeviceId(Integer.valueOf(deviceId));
            if (configState != null) {
                return Boolean.toString(configState.getStatus() == LastActionStatus.IN_PROGRESS);
            }

        }
        return null;
    }

    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }
}