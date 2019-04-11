package com.cannontech.core.dynamic.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.Base94;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.core.dynamic.PointDataTracker;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class PointDataTrackerImpl implements PointDataTracker {
    private static final Logger log = YukonLogManager.getLogger(PointDataTrackerImpl.class);
    private static final long MAX_TRACKING_ID = Base94.max(4);
    private static final long DIVISIONS = 6;  //  shared with C++ code 
    private static final long INTERVAL_SIZE = MAX_TRACKING_ID / DIVISIONS;
    
    private AtomicLong trackingId = new AtomicLong();
    private Long offset;

    Map<ApplicationId, Long> applicationOffsets = ImmutableMap.<ApplicationId, Long>builder()
            .put(ApplicationId.SERVICE_MANAGER, 1 * INTERVAL_SIZE)
            .put(ApplicationId.WEBSERVER, 2 * INTERVAL_SIZE)
            .build();

    @PostConstruct
    private void init() {
        var applicationId = BootstrapUtils.getApplicationId();

        offset = applicationOffsets.get(applicationId);
        
        if (offset == null) {
            log.warn("Unhandled application ID " + applicationId + ", defaulting to offset 0");
            offset = 0L;
        } else {
            log.info("Setting start offset to " + Base94.of(offset) + " for " + applicationId);
        }
    }
    
    @Override
    public Optional<String> trackValues(Collection<PointData> messagesToSend) {
        if (messagesToSend.isEmpty()) {
            return Optional.empty();
        }
        
        var messageIds = Maps.toMap(messagesToSend, m -> Base94.of(trackingId.getAndIncrement() % INTERVAL_SIZE + offset));

        messageIds.forEach((message, id) -> message.setTrackingId(id));
        
        return Optional.of(Strings.join(messageIds.values(), ' '));
    }
}
