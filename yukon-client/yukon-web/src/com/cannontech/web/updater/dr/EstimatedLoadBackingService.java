package com.cannontech.web.updater.dr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException;
import com.cannontech.dr.estimatedload.service.impl.EstimatedLoadBackingServiceHelper;
import com.cannontech.dr.program.service.impl.EstimatedLoadBackingField;
import com.cannontech.dr.program.service.impl.EstimatedLoadBackingFieldBase;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;


public class EstimatedLoadBackingService implements UpdateBackingService {
    
    private final Logger log = YukonLogManager.getLogger(EstimatedLoadBackingService.class);

    @Autowired private EstimatedLoadBackingServiceHelper backingServiceHelper;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    @Autowired private List<EstimatedLoadBackingFieldBase> handlers;
    private final Map<String, EstimatedLoadBackingField> handlersMap = new HashMap<>();

    private final Map<Integer, String> lastSentValues = new HashMap<>();

    private Pattern idSplitter = Pattern.compile("^([^/]+)/(.+)$");

    /**
     * This method attempts to retrieve the estimated load reduction amounts for a given program by pao id.
     * A gearId can be passed in and will be used to look for reduction amounts within the cache.
     * If gearId is not supplied, then either the default gear or current gear given by the load control server is used. 
     * If the estimated load amount is not found in the cache, or the cached values are too old, they are
     * recalculated.  Otherwise, the cached amount is returned.
     * 
     * @param programId The pao id of the program for which values are being requested.
     * @return The estimated load reduction amounts: connected load, diversified load, max kW savings, now kW savings.
     * @throws EstimatedLoadCalculationException 
     */
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {
        if (log.isDebugEnabled()) {
            log.debug("getLatestValue for: " + identifier);
        }
        Matcher m = idSplitter.matcher(identifier);
        if(m.matches()) {
            int paoId = Integer.parseInt(m.group(1));
            String fieldName = m.group(2);
            EstimatedLoadBackingField handler = handlersMap.get(fieldName);
            Object result = handler.getValue(paoId, userContext);
            String newValue = null;
            if (result != null) {
                if (result instanceof String) {
                    newValue = (String) result;
                } else if (result instanceof MessageSourceResolvable) {
                    MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
                    newValue = messageSourceAccessor.getMessage((MessageSourceResolvable) result);
                }
                return newValue;
//                // Check if this is the first data updater request for this user (afterDate will be zero).
//                if (afterDate == 0) {
//                    lastSentValues.put(paoId, newValue);
//                    return newValue;
//                }
//                // Check if the current value has changed from the last response sent.  If no change, don't send it.
//                if (!lastSentValues.get(paoId).equals(newValue)) {
//                    lastSentValues.put(paoId, newValue);
//                    return newValue;
//                }
            }
        } 
        return null;
    }

    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        Matcher m = idSplitter.matcher(fullIdentifier);
        boolean available = false;
        if(m.matches()) {
            int paoId = Integer.parseInt(m.group(1));
            available = backingServiceHelper.getCache().getIfPresent(paoId) != null ? false : true;
        }
        return available;
    }

    @PostConstruct
    public void init() throws Exception {
        for (EstimatedLoadBackingField handler : this.handlers) {
            this.handlersMap.put(handler.getFieldName(), handler);
        }
    }

}
