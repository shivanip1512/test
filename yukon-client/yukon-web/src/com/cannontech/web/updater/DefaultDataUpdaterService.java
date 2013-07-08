package com.cannontech.web.updater;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cannontech.common.util.TimeSource;
import com.cannontech.user.YukonUserContext;

public class DefaultDataUpdaterService implements DataUpdaterService {
    private TimeSource timeSource;
    private Map<DataType, ? extends UpdateBackingService> backs;
    
    private Pattern idSplitter = Pattern.compile("^([^/]+)/(.+)$");
    
    @Override
    public UpdateResponse getUpdates(Set<String> requests, long afterDate, YukonUserContext userContext) {
        UpdateResponse response = new UpdateResponse();
        response.asOfTime = timeSource.getCurrentMillis();
        Set<UpdateValue> result = new HashSet<>();
        for (String request : requests) {

            UpdateValue updateValue = getValue(request, afterDate, userContext, true);
            if (updateValue != null) {
                result.add(updateValue);
            }
        }

        response.values = result;
        return response;
    }
    
    protected UpdateValue getValue(String fullIdentifier, long afterDate, YukonUserContext userContext, boolean wait) {
        Matcher m = idSplitter.matcher(fullIdentifier);
        if (m.matches() && m.groupCount() == 2) {
            String typeStr = m.group(1);
            String remainder = m.group(2);
            DataType type = DataType.valueOf(typeStr);
            UpdateBackingService back = backs.get(type);
            
            if (wait || back.isValueAvailableImmediately(remainder, afterDate, userContext)) {

            	String value = back.getLatestValue(remainder, afterDate, userContext);
            	if (value != null) {
            		UpdateValue updateValue = new UpdateValue(fullIdentifier, value);
            		return updateValue;
            	}
            	if (afterDate == 0) {
            	    throw new RuntimeException("A a non-null value was expected from " + back.getClass().getSimpleName() + " for " + fullIdentifier);
            	}
            } else {
                // this must have been the initial call AND a value wasn't immediately available
                return new UpdateValue(fullIdentifier);
            }
        }
        return null;
    }
    
    @Override
    public UpdateValue getFirstValue(String identifier, YukonUserContext userContext) {
        return getValue(identifier, 0 , userContext, false);
    }
    
    public void setBacks(Map<DataType, ? extends UpdateBackingService> backs) {
        this.backs = backs;
    }
    
    public void setTimeSource(TimeSource timeSource) {
        this.timeSource = timeSource;
    }

}
