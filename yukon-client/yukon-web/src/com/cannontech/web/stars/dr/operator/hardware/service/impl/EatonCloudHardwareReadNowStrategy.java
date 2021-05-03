package com.cannontech.web.stars.dr.operator.hardware.service.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.Range;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.itron.service.ItronCommunicationException;
import com.cannontech.dr.pxmw.service.v1.PxMWDataReadService;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareReadNowStrategy;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareStrategyType;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class EatonCloudHardwareReadNowStrategy implements HardwareReadNowStrategy {
    private static final Logger log = YukonLogManager.getLogger(EatonCloudHardwareReadNowStrategy.class);

    @Autowired private PxMWDataReadService readService;
    @Autowired private IDatabaseCache cache;

    @Override
    public Map<String, Object> readNow(int deviceId, YukonUserContext userContext) {
        LiteYukonPAObject device = cache.getAllPaosMap().get(deviceId);
        Map<String, Object> json = Maps.newHashMapWithExpectedSize(2);
        try {
            DateTime start = new DateTime();
            DateTime end = start.minusDays(7);
            Range<Instant> range = new Range<Instant>(end.toInstant(), false, start.toInstant(), true);

            Set<Integer> deviceIds = new HashSet<>();
            deviceIds.add(deviceId);
            // Read Device.
            Multimap<PaoIdentifier, PointData> devicesToPointValues = readService.collectDataForRead(deviceIds, range);
            json.put("success", true);
            json.put("message", devicesToPointValues);
        } catch (ItronCommunicationException e) {
            log.debug("Read now failed for " + device);
            json.put("success", false);
            json.put("message", e.getMessage());
        }

        return json;
    }

    @Override
    public HardwareStrategyType getType() {
        return HardwareStrategyType.EATON_CLOUD;
    }

    @Override
    public boolean canHandle(HardwareType type) {
        return type.isEatonCloud();
    }

}
