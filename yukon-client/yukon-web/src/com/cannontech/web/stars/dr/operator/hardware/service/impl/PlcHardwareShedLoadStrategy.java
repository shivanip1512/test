package com.cannontech.web.stars.dr.operator.hardware.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.porter.message.Request;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareShedLoadStrategy;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareStrategyType;
import com.cannontech.yukon.BasicServerConnection;

public class PlcHardwareShedLoadStrategy implements HardwareShedLoadStrategy {

    private static final Logger log = YukonLogManager.getLogger(PlcHardwareShedLoadStrategy.class);
    private static final String keyBase = "yukon.web.modules.operator.hardware.";

    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoDao paoDao;
    @Autowired private BasicServerConnection porterConnection;

    @Override
    public Map<String, Object> shedLoad(int deviceId, int relayNo, int duration, String serialNo,
            YukonUserContext userContext) {
        Map<String, Object> resultMap = new HashMap<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);

        int routeId = paoDao.getLiteYukonPAO(deviceId).getRouteID();

        Request req = new Request();
        StringBuilder commandString = new StringBuilder("control shed ");
        commandString.append(duration);
        commandString.append("m ");
        commandString.append("relay ");
        commandString.append(relayNo);
        commandString.append(" serial ");
        commandString.append(serialNo);

        req.setCommandString(commandString.toString());
        req.setDeviceID(deviceId);
        req.setRouteID(routeId);

        // run shed load command
        try {
            porterConnection.write(req);
        } catch (Exception e) {
            log.error("Shed Load failed for " + device);
            resultMap.put("success", false);
            resultMap.put("message", accessor.getMessage(keyBase + "error.shedLoadFailed"));
            return resultMap;
        }
        log.debug("Shed load initiated for " + device);
        resultMap.put("success", true);
        resultMap.put("message", accessor.getMessage(keyBase + "shedLoadSuccess"));

        return resultMap;
    }

    @Override
    public HardwareStrategyType getType() {
        return HardwareStrategyType.PLC;
    }

    @Override
    public boolean canHandle(HardwareType type) {
        return type.isTwoWayPlcLcr();
    }

}
