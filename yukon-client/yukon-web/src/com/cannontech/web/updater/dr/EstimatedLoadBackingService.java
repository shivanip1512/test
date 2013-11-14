package com.cannontech.web.updater.dr;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.estimatedload.service.impl.EstimatedLoadBackingServiceHelper;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;


public class EstimatedLoadBackingService implements UpdateBackingService {
    
    private final Logger log = YukonLogManager.getLogger(EstimatedLoadBackingService.class);

    @Autowired private EstimatedLoadBackingServiceHelper backingServiceHelper;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    private final Map<String, EstimatedLoadBackingField> handlersMap;

    @Autowired
    public EstimatedLoadBackingService(List<EstimatedLoadBackingFieldBase> handlers) throws Exception {
        Builder<String, EstimatedLoadBackingField> builder = ImmutableMap.builder();
        for (EstimatedLoadBackingField handler : handlers) {
            builder.put(handler.getFieldName(), handler);
        }
        handlersMap = builder.build();
    }
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
        Matcher m = idSplitter.matcher(identifier);
        if(m.matches()) {
            int paoId = Integer.parseInt(m.group(1));
            String fieldName = m.group(2);
            EstimatedLoadBackingField handler = handlersMap.get(fieldName);
            String result = handler.getValue(paoId, userContext);
            if (result != null) {
                return result;
            } else {
                log.error("Problem retrieving estimated load value for pao id: " + paoId);
            }

        }
        return null;
    }

    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }

}
