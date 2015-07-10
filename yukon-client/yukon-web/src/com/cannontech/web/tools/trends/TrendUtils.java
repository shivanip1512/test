package com.cannontech.web.tools.trends;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.fasterxml.jackson.core.JsonProcessingException;

public final class TrendUtils {
    private static final Logger log = YukonLogManager.getLogger(TrendUtils.class);
    
    private TrendUtils() {};
    
    public static String getLabels(YukonUserContext userContext, YukonUserContextMessageSourceResolver messageSourceResolver) {
        
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        json.put("day", accessor.getMessage("yukon.web.modules.tools.trend.day"));
        json.put("week", accessor.getMessage("yukon.web.modules.tools.trend.week"));
        json.put("month", accessor.getMessage("yukon.web.modules.tools.trend.month"));
        json.put("threeMonths", accessor.getMessage("yukon.web.modules.tools.trend.threeMonths"));
        json.put("sixMonths", accessor.getMessage("yukon.web.modules.tools.trend.sixMonths"));
        json.put("ytd", accessor.getMessage("yukon.web.modules.tools.trend.ytd"));
        json.put("year", accessor.getMessage("yukon.web.modules.tools.trend.year"));
        json.put("all", accessor.getMessage("yukon.web.modules.tools.trend.all"));
        
        String jsonString = "";
        try {
            jsonString = JsonUtils.toJson(json);
        } catch (JsonProcessingException e) {
            log.error("Error processing json for trend labels.", e);
        }
        
        return jsonString;
    }
}
