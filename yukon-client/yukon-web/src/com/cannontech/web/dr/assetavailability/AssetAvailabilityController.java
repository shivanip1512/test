package com.cannontech.web.dr.assetavailability;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.widgets.model.AssetAvailabilityWidgetSummary;
import com.cannontech.web.common.widgets.service.AssetAvailabilityWidgetService;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/assetAvailability/*")
@CheckRoleProperty(YukonRoleProperty.SHOW_ASSET_AVAILABILITY)
public class AssetAvailabilityController {

    private final static String widgetKey = "yukon.web.widgets.";
    
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private AssetAvailabilityWidgetService assetAvailabilityWidgetService;

    @GetMapping(value = "updateChart")
    public @ResponseBody Map<String, Object> updateChart(Integer controlAreaOrProgramOrScenarioId, YukonUserContext userContext) throws Exception {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Map<String, Object> json = new HashMap<>();

        Instant lastUpdateTime = new Instant();
        AssetAvailabilityWidgetSummary summary = assetAvailabilityWidgetService.getAssetAvailabilitySummary(controlAreaOrProgramOrScenarioId, lastUpdateTime);

        if (summary.getTotalDeviceCount() == 0) {
            String errorMsg = accessor.getMessage(widgetKey + "assetAvailabilityWidget.noDevicesFound");
            json.put("errorMessage", errorMsg);
        } else {
            json.put("summary",  summary);
        }

        json.put("lastAttemptedRefresh", lastUpdateTime);
        Instant nextRun = assetAvailabilityWidgetService.getNextRefreshTime(lastUpdateTime);
        json.put("refreshMillis", assetAvailabilityWidgetService.getRefreshMilliseconds());
        String nextRefreshDate = dateFormattingService.format(nextRun, DateFormattingService.DateFormatEnum.DATEHMS_12, userContext);
        json.put("refreshTooltip", accessor.getMessage(widgetKey + "nextRefresh") + nextRefreshDate);
        json.put("updateTooltip", accessor.getMessage(widgetKey + "forceUpdate"));

        return json;
    }

}
