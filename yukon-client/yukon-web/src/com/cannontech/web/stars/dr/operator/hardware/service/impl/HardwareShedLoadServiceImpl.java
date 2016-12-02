package com.cannontech.web.stars.dr.operator.hardware.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandParam;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareShedLoadService;

public class HardwareShedLoadServiceImpl implements HardwareShedLoadService {
    @Autowired private InventoryDao inventoryDao;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private LmHardwareCommandService lmHardwareCommandService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private ServerDatabaseCache dbCache;

    private static final Logger log = YukonLogManager.getLogger(HardwareShedLoadServiceImpl.class);
    private static final String keyBase = "yukon.web.modules.operator.hardware.";

    @Override
    public Map<String, Object> shedLoad(int inventoryId, int relay, int duration,
            YukonUserContext userContext) {
        Map<String, Object> resultMap = new HashMap<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        InventoryIdentifier inventory = inventoryDao.getYukonInventory(inventoryId);
        HardwareType type = inventory.getHardwareType();
        int deviceId = inventoryDao.getDeviceId(inventoryId);
        LiteLmHardwareBase lmhb = (LiteLmHardwareBase) inventoryBaseDao.getByInventoryId(inventoryId);

        int routeId = 0;
        if (!type.isRf()) {
            if (type.isTwoWay()) {
                LiteYukonPAObject twoWayDevice = dbCache.getAllPaosMap().get(deviceId);
                routeId = twoWayDevice.getRouteID();
            } else {
                routeId = lmhb.getRouteID();
            }
        }

        LmHardwareCommand command = new LmHardwareCommand();
        command.setDevice(lmhb);
        command.setUser(userContext.getYukonUser());
        command.setType(LmHardwareCommandType.SHED);
        command.getParams().put(LmHardwareCommandParam.RELAY, relay);
        command.getParams().put(LmHardwareCommandParam.DURATION,
            Duration.standardMinutes(TimeUnit.SECONDS.toMinutes(duration)));
        command.getParams().put(LmHardwareCommandParam.OPTIONAL_ROUTE_ID, routeId);
        command.getParams().put(LmHardwareCommandParam.WAITABLE, true);

        try {
            lmHardwareCommandService.sendShedLoadCommand(command);
        } catch (CommandCompletionException e) {
            log.error("Shed Load failed for " + deviceId);
            resultMap.put("success", false);
            resultMap.put("message", accessor.getMessage(keyBase + "error.shedLoadFailed",e.getMessage()));
            return resultMap;
        }
        log.debug("Shed load initiated for " + deviceId);
        resultMap.put("success", true);
        resultMap.put("message", accessor.getMessage(keyBase + "shedLoadSuccess"));

        return resultMap;
    }
}
