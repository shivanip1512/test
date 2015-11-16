package com.cannontech.web.updater.dr;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.estimatedload.service.EstimatedLoadBackingServiceHelper;
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
    /* The following regex pattern is used to split the paoId or possibly a second id from the field when receiving a data updater identifier.
     * In order to match, the string has to have the form: VALUE/VALUE
     * For example, it will match: '1234/SCENARIO', storing '1234' in the first group and 'SCENARIO' in the second.
     * In the event that we may have a third value, the match will be 1234/TAG/1234, where the first group is isolated as 
     * numberic, the second group is the IDENTIFIER, and the final third group is the ScenarioID */
    private final static Pattern pattern = Pattern.compile("^(\\d+)/([A-Z_]+)/?(\\d+)?$");

    @Autowired
    public EstimatedLoadBackingService(List<EstimatedLoadBackingFieldBase> handlers) throws Exception {
        Builder<String, EstimatedLoadBackingField> builder = ImmutableMap.builder();
        for (EstimatedLoadBackingField handler : handlers) {
            builder.put(handler.getFieldName(), handler);
        }
        handlersMap = builder.build();
    }

    /**
     * This method attempts to retrieve the estimated load reduction amounts for a given program by pao id.
     * A gearId can be passed in and will be used to look for reduction amounts within the cache.
     * If gearId is not supplied, then either the default gear or current gear given by the load control server is used. 
     * If the estimated load amount is not found in the cache, or the cached values are too old, they are
     * recalculated.  Otherwise, the cached amount is returned.
     * 
     * @param identifier The data updater identifier string in the format: PAOID/FIELD.<br>
     * The choices for field are:  PROGRAM, CONTROL_AREA, SCENARIO.<br>
     * An example identifier for a scenario with paoId 1234 is: 1234/SCENARIO<br>
     * Note: There is a 3rd component to the identifier that selects which backing service to use. By the time
     * execution reaches getLatestValue() this component has already been stripped from the beginning of the string. 
     * See: DefaultDataUpdaterService.getValue()
     * 
     * @return The estimated load reduction amounts: Connected load, Diversified load, kW Savings Max, kW Savings Now.
     */
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {
        Matcher m = pattern.matcher(identifier);
        if (m.matches()) {
            int programId = Integer.parseInt(m.group(1));
            String fieldName = m.group(2);
            EstimatedLoadBackingField handler = handlersMap.get(fieldName);
            String result;
            if (m.group(3) != null) {
                int scenarioId = Integer.parseInt(m.group(3));
                result = handler.getValue(programId, scenarioId, userContext);
            } else {
                result = handler.getValue(programId, userContext);
            }
            if (result != null) {
                return result;
            } else {
                log.error("Problem retrieving estimated load value for pao id: " + programId);
            }
        }
        return null;
    }

    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }

}
