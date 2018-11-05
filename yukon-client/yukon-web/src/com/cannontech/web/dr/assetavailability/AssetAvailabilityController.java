package com.cannontech.web.dr.assetavailability;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.widgets.model.AssetAvailabilitySummary;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/assetAvailability/*")
@CheckRoleProperty(YukonRoleProperty.SHOW_ASSET_AVAILABILITY)
public class AssetAvailabilityController {

    private Logger log = YukonLogManager.getLogger(AssetAvailabilityController.class);
    private final static String widgetKey = "yukon.web.widgets.";
    
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DateFormattingService dateFormattingService;

    @GetMapping(value = "updateChart")
    public @ResponseBody Map<String, Object> updateChart(Integer areaOrLMProgramOrScenarioId,
            YukonUserContext userContext) throws Exception {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Map<String, Object> json = new HashMap<>();
        //TODO: replace the call to mockAssetAvailabilitySummary() with service call.
        AssetAvailabilitySummary summary = mockAssetAvailabilitySummary();
        if (summary.getTotalDeviceCount() == 0) {
            String errorMsg = accessor.getMessage(widgetKey + "assetAvailability.noDevicesFound");
            json.put("errorMessage", errorMsg);
        } else {
            json.put("summary",  summary);
        }
        
        //TODO: Add a service call here.
        Instant nextRun = Instant.now().plus(1000000l);
        json.put("lastAttemptedRefresh", nextRun);
        if (nextRun.isAfterNow()) {
            json.put("nextRefresh", nextRun);
            json.put("isRefreshPossible", false);
            String nextRefreshDate = dateFormattingService.format(nextRun, DateFormattingService.DateFormatEnum.DATEHMS_12, userContext);
            json.put("refreshTooltip", accessor.getMessage(widgetKey + "nextRefresh") + nextRefreshDate);
        } else {
            json.put("isRefreshPossible", true);
            json.put("refreshTooltip", accessor.getMessage(widgetKey + "forceUpdate"));
        }
        
        return json;
    }
    
    @GetMapping("forceUpdate")
    public @ResponseBody Map<String, Object> forceUpdate() {
        Map<String, Object> json = new HashMap<>();
        //TODO: Need to add a service call here.
        //dataCollectionWidgetService.collectData();
        json.put("success", true);
        return json;
    }

    // TODO: Delete this once the service call is added.
    private AssetAvailabilitySummary mockAssetAvailabilitySummary() {
        AssetAvailabilitySummary summary = new AssetAvailabilitySummary(Instant.now());
        summary.setActive(10);
        summary.setInactive(5);
        summary.setOptedOut(3);
        summary.setUnavailabile(2);
        summary.calculatePrecentages();
        return summary;
    }
}
