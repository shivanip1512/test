package com.cannontech.web.stars.dr.operator.hardware.service.impl;

import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.util.Range;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommunicationExceptionV1;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudDataReadService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareReadNowStrategy;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareStrategyType;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Maps;

public class EatonCloudHardwareReadNowStrategy implements HardwareReadNowStrategy {
    private static final Logger log = YukonLogManager.getLogger(EatonCloudHardwareReadNowStrategy.class);
    private static final String keyBase = "yukon.web.modules.operator.hardware.";

    @Autowired private EatonCloudDataReadService readService;
    @Autowired private IDatabaseCache cache;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    @Override
    public Map<String, Object> readNow(int deviceId, YukonUserContext userContext) {
        LiteYukonPAObject device = cache.getAllPaosMap().get(deviceId);
        final MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Map<String, Object> json = Maps.newHashMapWithExpectedSize(2);
        try {
            DateTime start = new DateTime();
            DateTime end = start.minusDays(7);
            Range<Instant> range = new Range<Instant>(end.toInstant(), false, start.toInstant(), true);

            // Read Device.
            readService.collectDataForRead(deviceId, range);
            json.put("success", true);
            json.put("message", accessor.getMessage(keyBase + "readNowSuccess"));
        } catch (EatonCloudCommunicationExceptionV1 e) {
            log.debug("Read now failed for " + device);
            json.put("success", false);
            json.put("message", accessor.getMessage(keyBase + "error.readNowFailed", e.getDisplayMessage()));
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
