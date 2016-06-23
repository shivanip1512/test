package com.cannontech.web.stars.dr.operator.hardware.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareShedLoadService;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareShedLoadStrategy;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareStrategyType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class HardwareShedLoadServiceImpl implements HardwareShedLoadService {
    @Autowired
    private InventoryDao inventoryDao;
    @Autowired
    private PaoDao paoDao;
    @Autowired
    private InventoryBaseDao inventoryBaseDao;

    private ImmutableMap<HardwareStrategyType, HardwareShedLoadStrategy> strategies = ImmutableMap.of();

    @Autowired
    public void setStrategies(List<HardwareShedLoadStrategy> strategyList) {
        Builder<HardwareStrategyType, HardwareShedLoadStrategy> builder = ImmutableMap.builder();
        for (HardwareShedLoadStrategy strategy : strategyList) {
            builder.put(strategy.getType(), strategy);
        }
        strategies = builder.build();
    }

    @Override
    public Map<String, Object> shedLoad(int inventoryId, int relay, int duration,
            YukonUserContext userContext) {
        InventoryIdentifier inventory = inventoryDao.getYukonInventory(inventoryId);
        HardwareType type = inventory.getHardwareType();
        int deviceId = inventoryDao.getDeviceId(inventoryId);

        LiteLmHardwareBase lmhb = (LiteLmHardwareBase) inventoryBaseDao.getByInventoryId(inventoryId);
        String serialNo = lmhb.getManufacturerSerialNumber();

        HardwareStrategyType strategy = getStrategy(type);
        final HardwareShedLoadStrategy impl = strategies.get(strategy);
        return impl.shedLoad(deviceId, relay, duration, serialNo, userContext);

    }

    private HardwareStrategyType getStrategy(HardwareType type) throws IllegalArgumentException {
        for (HardwareStrategyType strategyType : strategies.keySet()) {
            HardwareShedLoadStrategy deviceShedLoadStrategy = strategies.get(strategyType);
            if (deviceShedLoadStrategy.canHandle(type)) {
                return strategyType;
            }
        }
        throw new IllegalArgumentException("No device shed load strategy found for hardware type " + type);
    }

}
