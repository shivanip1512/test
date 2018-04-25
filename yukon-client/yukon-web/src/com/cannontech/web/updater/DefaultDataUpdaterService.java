package com.cannontech.web.updater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.joda.time.Interval;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.capcontrol.exception.CacheManagementException;
import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;

public class DefaultDataUpdaterService implements DataUpdaterService {
    private final static Logger log = YukonLogManager.getLogger(DefaultDataUpdaterService.class);

    private Map<DataType, ? extends UpdateBackingService> backs;
    private Map<DataType, ? extends BulkUpdateBackingService> bulkBacks;

    @Override
    public UpdateResponse getUpdates(Set<String> requests, long afterDate, YukonUserContext userContext) {
        if (log.isDebugEnabled()) {
            log.debug("getUpdates - getting updates for " + Joiner.on(",").join(requests));
        }

        // Collect requests by type.
        ListMultimap<DataType, UpdateIdentifier> requestsToProcess = ArrayListMultimap.create();
        for (String request : requests) {
            UpdateIdentifier identifier = new UpdateIdentifier(request);
            requestsToProcess.put(identifier.getType(), identifier);
        }

        Map<String,Long> timesMap = new HashMap<>();

        // Handle requests by type.
        Map<String, String> response = new HashMap<>();
        for (DataType type : requestsToProcess.keySet()) {
            List<UpdateIdentifier> identifiers = requestsToProcess.get(type);
            UpdateBackingService back = backs.get(type);
            BulkUpdateBackingService bulkBack = bulkBacks.get(type);
            if (back != null) {
                if (log.isDebugEnabled()) {
                    log.debug("handling values of type " + type + ": " + Joiner.on("; ").join(identifiers));
                }
                for (UpdateIdentifier identifier : identifiers) {
                    String updaterType = null;
                    Instant start = null;
                    if (log.isDebugEnabled()) {
                        updaterType =  identifier.getRemainder();
                        int lastIndex = updaterType.lastIndexOf("/");
                        updaterType = updaterType.substring(lastIndex > 0 ? lastIndex + 1 : 0 );
                        start = Instant.now();
                    }
                    UpdateValue updateValue = null;
                    try {
                        updateValue = getValue(back, identifier, afterDate, userContext, true);
                    } catch (CacheManagementException e) {
                        log.debug("Unable to get the identifierValue");
                    }
                    if (log.isDebugEnabled()) {
                        long time = new Interval(start, Instant.now()).toDurationMillis();
                        Long timeForUpdater = timesMap.get(updaterType);
                        timeForUpdater = timeForUpdater != null? timeForUpdater + time: time;
                        timesMap.put(updaterType, timeForUpdater);
                    }
                    if (updateValue != null) {
                        response.put(identifier.getFullIdentifier(), updateValue.getValue());
                    }
                }
            } else if (bulkBack != null) {
                if (log.isDebugEnabled()) {
                    log.debug("handling bulked values of type " + type + ": " + Joiner.on("; ").join(identifiers));
                }
                List<UpdateValue> updateValues = getValues(bulkBack, identifiers, afterDate, userContext, true);
                for (UpdateValue updateValue : updateValues) {
                    if (updateValue != null) {
                        response.put(updateValue.getIdentifier().getFullIdentifier(), updateValue.getValue());
                    }
                }
            } else {
                log.error("could not find handler for data type " + type);
            }
            log.debug("done handling values of type " + type);
        }

        if (log.isDebugEnabled()) {
            for (Entry<String, Long> e : timesMap.entrySet()) {
                log.debug(e.getKey() +": " + e.getValue() + "ms" );
            }
            log.debug("getUpdates - creating response object");
        }

        return new UpdateResponse(response, System.currentTimeMillis());
    }

    @Override
    public UpdateValue getFirstValue(String fullIdentifier, YukonUserContext userContext) {
        
        UpdateIdentifier identifier = new UpdateIdentifier(fullIdentifier);
        if (log.isTraceEnabled()) {
            log.trace("getFirstValue - handling " + identifier);
        }
        
        UpdateValue updateValue = null;
        UpdateBackingService back = backs.get(identifier.getType());
        BulkUpdateBackingService bulkBack = bulkBacks.get(identifier.getType());
        if (back != null) {
            updateValue = getValue(back, identifier, 0, userContext, false);
        } else if (bulkBack != null) {
            List<UpdateValue> updateValues = getValues(bulkBack, ImmutableList.of(identifier), 0, userContext, false);
            if (updateValues.isEmpty()) {
                updateValue = new UpdateValue(identifier);
            } else {
                updateValue = updateValues.get(0);
            }
        } else {
            log.error("could not find handler for data type " + identifier.getType());
        }
        log.trace("getFirstValue - done");
        return updateValue;
    }

    private UpdateValue getValue(UpdateBackingService back, UpdateIdentifier identifier, long afterDate,
            YukonUserContext userContext, boolean canWait) {
        if (canWait || back.isValueAvailableImmediately(identifier.getRemainder(), afterDate, userContext)) {
            String value = back.getLatestValue(identifier.getRemainder(), afterDate, userContext);
            if (value != null) {
                return new UpdateValue(identifier, value);
            }
            if (afterDate == 0) {
                throw new RuntimeException("A a non-null value was expected for afterDate");
            }
        } else {
            // this must have been the initial call AND a value wasn't immediately available
            return new UpdateValue(identifier);
        }

        return null;
    }

    private List<UpdateValue> getValues(BulkUpdateBackingService bulkBack, List<UpdateIdentifier> identifiers,
            long afterDate, YukonUserContext userContext, boolean canWait) {
        List<UpdateValue> updateValues = new ArrayList<>();
        Map<UpdateIdentifier, String> latestValues =
            bulkBack.getLatestValues(identifiers, afterDate, userContext, canWait);
        for (UpdateIdentifier updateIdentifier : latestValues.keySet()) {
            UpdateValue updateValue = new UpdateValue(updateIdentifier, latestValues.get(updateIdentifier));
            updateValues.add(updateValue);
        }
        return updateValues;
    }

    public void setBacks(Map<DataType, ? extends UpdateBackingService> backs) {
        this.backs = backs;
    }

    public void setBulkBacks(Map<DataType, ? extends BulkUpdateBackingService> bulkBacks) {
        this.bulkBacks = bulkBacks;
    }
}
