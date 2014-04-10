package com.cannontech.web.updater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.TimeSource;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class DefaultDataUpdaterService implements DataUpdaterService {
    private final static Logger log = YukonLogManager.getLogger(DefaultDataUpdaterService.class);

    private TimeSource timeSource;
    private Map<DataType, ? extends UpdateBackingService> backs;
    private Map<DataType, ? extends BulkUpdateBackingService> bulkBacks;

    @Override
    public UpdateResponse getUpdates(Set<String> requests, long afterDate,
                                     YukonUserContext userContext) {
        if (log.isDebugEnabled()) {
            log.debug("getUpdates - getting updates for " + Joiner.on(",").join(requests));
        }
        ListMultimap<DataType, UpdateIdentifier> requestsToProcess = ArrayListMultimap.create();
        for (String request : requests) {
            UpdateIdentifier identifier = new UpdateIdentifier(request);
            requestsToProcess.put(identifier.getType(), identifier);
        }
        Map<String, String> response = new HashMap<>();
        for (DataType type : requestsToProcess.keySet()) {
            List<UpdateIdentifier> updateIdentifiers = requestsToProcess.get(type);
            UpdateBackingService back = backs.get(type);
            BulkUpdateBackingService bulkBack = bulkBacks.get(type);
            if (back != null) {
                for (UpdateIdentifier identifier : updateIdentifiers) {
                    UpdateValue updateValue = getValue(back, identifier, afterDate, userContext, true);
                    addUpdateValueToResponse(response, updateValue);
                }
            } else if (bulkBack != null) {
                List<UpdateValue> updateValues = getValues(bulkBack, updateIdentifiers, afterDate, userContext);
                for (UpdateValue updateValue : updateValues) {
                    addUpdateValueToResponse(response, updateValue);
                }
            }
        }
        log.debug("getUpdates - creating response object");
        return new UpdateResponse(response, timeSource.getCurrentMillis());
    }
    
    @Override
    public UpdateValue getFirstValue(String fullIdentifier, YukonUserContext userContext) {
        UpdateValue updateValue = null;
        UpdateIdentifier identifier = new UpdateIdentifier(fullIdentifier);
        UpdateBackingService back = backs.get(identifier.getType());
        BulkUpdateBackingService bulkBack = bulkBacks.get(identifier.getType());
        if (back != null) {
            updateValue = getValue(back, new UpdateIdentifier(fullIdentifier), 0, userContext, false);
        } else if (bulkBack != null) {
            updateValue = getFirstValue(bulkBack, new UpdateIdentifier(fullIdentifier), userContext);
        }
        return updateValue;
    }
    
    /*
     * Adds update value to the response
     */
    private void addUpdateValueToResponse(Map<String, String> response, UpdateValue updateValue){
        if (updateValue != null) {
            String fullIdentifier = updateValue.getIdentifier().getFullIdentifier();
            String value = updateValue.getValue();
            response.put(fullIdentifier, value);
        }
    }

    /*
     * Returns update value
     */
    private UpdateValue getValue(UpdateBackingService back, UpdateIdentifier identifier,
                                 long afterDate, YukonUserContext userContext, boolean wait) {
        if (wait || back.isValueAvailableImmediately(identifier.getRemainder(),
                                                     afterDate,
                                                     userContext)) {
            String value =
                back.getLatestValue(identifier.getRemainder(), afterDate, userContext);
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

    /*
     * Returns a latest cached value. If the cached value is not found an update values is returned
     * with the value not set.
     */
    private UpdateValue getFirstValue(BulkUpdateBackingService back, UpdateIdentifier identifier,
                                                YukonUserContext userContext) {
        log.debug("getFirstValue - handling " + identifier);
        UpdateValue updateValue;
        Map<UpdateIdentifier, String> cachedValues =
            back.getLatestValues(Lists.newArrayList(identifier), 0, userContext, false);
        String cachedValue = cachedValues.get(identifier);
        if (StringUtils.isNotEmpty(cachedValue)) {
            updateValue = new UpdateValue(identifier, cachedValue);
        } else {
            updateValue = new UpdateValue(identifier);
        }
        log.debug("getFirstValue - done");
        return updateValue;
    }
    
    /*
     * Returns a update values.
     */
    private List<UpdateValue> getValues(BulkUpdateBackingService back, List<UpdateIdentifier> identifiers,
                                            long afterDate, YukonUserContext userContext) {
        if (log.isDebugEnabled()) {
            log.debug("getValues (bulk) - handling " + Joiner.on("; ").join(identifiers));
        }
        List<UpdateValue> updateValues = new ArrayList<>();
        Map<UpdateIdentifier, String> latestValues =
            back.getLatestValues(identifiers, afterDate, userContext, true);
        for (UpdateIdentifier updateIdentifier : latestValues.keySet()) {
            UpdateValue updateValue = new UpdateValue(updateIdentifier, latestValues.get(updateIdentifier));
            updateValues.add(updateValue);
        }
        log.debug("getValues (bulk) - done");
        return updateValues;
    }

    @Required
    public void setTimeSource(TimeSource timeSource) {
        this.timeSource = timeSource;
    }

    public void setBacks(Map<DataType, ? extends UpdateBackingService> backs) {
        this.backs = backs;
    }

    public void setBulkBacks(Map<DataType, ? extends BulkUpdateBackingService> bulkBacks) {
        this.bulkBacks = bulkBacks;
    }
}
