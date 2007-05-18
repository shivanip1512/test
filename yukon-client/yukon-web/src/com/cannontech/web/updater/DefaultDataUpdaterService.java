package com.cannontech.web.updater;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cannontech.common.util.TimeSource;
import com.cannontech.database.data.lite.LiteYukonUser;

public class DefaultDataUpdaterService implements DataUpdaterService {
    private TimeSource timeSource;
    private Map<DataType, ? extends UpdateBackingService> backs;
    
    private Pattern idSplitter = Pattern.compile("^([^/]+)/(.+)$");
    
    public UpdateResponse getUpdates(Set<String> requests, long afterDate, LiteYukonUser user) {
        UpdateResponse response = new UpdateResponse();
        response.asOfTime = timeSource.getCurrentMillis();
        Set<UpdateValue> result = new HashSet<UpdateValue>();
        for (String request : requests) {

            UpdateValue updateValue = getValue(request, afterDate, user);
            if (updateValue != null) {
                result.add(updateValue);
            }
        }

        response.values = result;
        return response;
    }
    
    protected UpdateValue getValue(String fullIdentifier, long afterDate, LiteYukonUser user) {
        Matcher m = idSplitter.matcher(fullIdentifier);
        if (m.matches() && m.groupCount() == 2) {
            String typeStr = m.group(1);
            String remainder = m.group(2);
            DataType type = DataType.valueOf(typeStr);
        
            UpdateBackingService back = backs.get(type);
            String value = back.getLatestValue(remainder, afterDate, user);
            if (value != null) {
                UpdateValue updateValue = new UpdateValue(fullIdentifier, value);
                return updateValue;
            }
        }
        return null;
    }
    
    public UpdateValue getFirstValue(String identifier, LiteYukonUser user) {
        return getValue(identifier, 0 , user);
    }
    
    public void setBacks(Map<DataType, ? extends UpdateBackingService> backs) {
        this.backs = backs;
    }
    
    public void setTimeSource(TimeSource timeSource) {
        this.timeSource = timeSource;
    }

}
