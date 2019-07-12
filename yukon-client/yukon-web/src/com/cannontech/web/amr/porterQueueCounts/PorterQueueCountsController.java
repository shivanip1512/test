package com.cannontech.web.amr.porterQueueCounts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.gui.util.Colors;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.users.model.PreferencePorterQueueCountsZoomOption;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.widgets.service.PorterQueueCountsWidgetService;
import com.cannontech.web.tools.trends.TrendUtils;
import com.cannontech.web.tools.trends.data.TrendType.GraphType;
import com.cannontech.web.tools.trends.data.error.GraphDataError;
import com.cannontech.web.user.service.UserPreferenceService;
import com.google.common.collect.ImmutableMap;

/**
 * Controller used to display number of queued entries for ports (refered to as comm channels in the UI)
 * and to handle the updating of the settings used for the graphs that display the queue counts.
 */
@Controller
@RequestMapping("/porterQueueCounts/*")
public class PorterQueueCountsController {
        
    @Autowired private PorterQueueCountsWidgetService porterQueueCountsWidgetService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private UserPreferenceService userPreferenceService;
    
    private final static String widgetKey = "yukon.web.widgets.";
    
    /**
     * Get the data for the graph and build the graph definition.
     */
    @RequestMapping("data")
    public @ResponseBody Map<String, Object> data(YukonUserContext userContext, @RequestParam(value="portIds[]") List<Integer> portIds) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        List<Map<String, Object>> seriesList = new ArrayList<>();
        Map<String, Object> yAxisProperties = new HashMap<>();
        DateTime chartDatePrime = DateTime.now();
        DateTime chartDateLimit = DateTime.now();
        boolean hasCurrentDateBoundary = false;
        boolean showRightAxis = false;
        int colorInt = 0;
        Map<Integer, LiteYukonPAObject> pointIdToPaoMap = porterQueueCountsWidgetService.getPointIdToPaoMap(portIds);
        Map<Integer, List<PointValueHolder>> pointIdToPointValueHolderMap = porterQueueCountsWidgetService.rawPointHistoryDataProvider(pointIdToPaoMap.keySet());
        for (Integer pointId : pointIdToPaoMap.keySet()) {
            Map<String, Object> seriesProperties = new HashMap<>();
            List<PointValueHolder> seriesItemResult = new ArrayList<>();
            List<Object[]> seriesData = new ArrayList<>();
            seriesItemResult = pointIdToPointValueHolderMap.get(pointId);
            seriesData = porterQueueCountsWidgetService.graphDataProvider(seriesItemResult);
            if (seriesData.isEmpty()) {
                seriesProperties.put("error", graphDataStateMessage(GraphDataError.NO_TREND_DATA_AVAILABLE, userContext));
            }
            seriesProperties.put("data", seriesData);
            if (!seriesItemResult.isEmpty()) {
                // Using PORT_QUEUE_COUNT attribute, which is a count with whole numbers only, do not group. (See PorterQueueCountsWidgetServiceImpl) 
                seriesProperties.put("dataGrouping", ImmutableMap.of("enabled", false));

                DateTime valueDeltaPrimeDT = new DateTime(seriesItemResult.get(0).getPointDataTimeStamp());
                DateTime valueDeltaLimitDT = new DateTime(seriesItemResult.get(seriesData.size() - 1).getPointDataTimeStamp());
                if (hasCurrentDateBoundary) {
                    chartDatePrime = (chartDatePrime.isAfter(valueDeltaPrimeDT)) ? valueDeltaPrimeDT : chartDatePrime;
                    chartDateLimit = (chartDateLimit.isBefore(valueDeltaLimitDT)) ? valueDeltaLimitDT : chartDateLimit;
                } else {
                    chartDatePrime = valueDeltaPrimeDT;
                    chartDateLimit = valueDeltaLimitDT;
                    hasCurrentDateBoundary = true;
                }
            }
            seriesProperties.put("name", pointIdToPaoMap.get(pointId).getPaoName());
            String color = Colors.colorPaletteToWeb(colorInt++);
            if (color == "#FFFFFF") { //avoid white points on the graph
                color = Colors.colorPaletteToWeb(colorInt++);
            }
            seriesProperties.put("color", color);
            seriesProperties.put("lineColor", color);
            seriesList.add(seriesProperties);
        }
        Map<String, Object> json = new HashMap<>();
        json.put("name", "Queue Counts");
        json.put("series", seriesList);
        List<Map<String, Object>> yAxis = new ArrayList<>();
        ImmutableMap<String, ImmutableMap<String, String>> labels = ImmutableMap.of("style", ImmutableMap.of("color", "#555"));
        yAxisProperties.put("labels", labels);
        yAxisProperties.put("opposite", false);
        yAxisProperties.put("softMin", 0);
        yAxisProperties.put("showEmpty", false);
        yAxis.add(yAxisProperties);
        if (showRightAxis) {
            addRightAxis(userContext, seriesList, yAxis, labels);
        }
        json.put("yAxis", yAxis);
        Instant lastUpdateTime = new Instant();
        json.put("lastUpdateTime", lastUpdateTime);
        Instant nextRun = porterQueueCountsWidgetService.getNextRefreshTime(lastUpdateTime);
        json.put("nextRefreshDate", nextRun);
        json.put("updateTooltip", accessor.getMessage(widgetKey + "forceUpdate"));
        json.put("refreshMillis",  porterQueueCountsWidgetService.getRefreshMilliseconds());
        json.put("maxNumSelections", GlobalSettingType.PORTER_QUEUE_COUNTS_TREND_MAX_NUM_PORTS);
        json.put("labels",TrendUtils.getLabels(userContext, messageResolver));
        return json;
    }

    /**
    * Returns context sensitive message for {@link GraphDataError}
     * <p>
     * 
     * @param state {@link GraphDataError} is state of the data for being passed back to the client.
     * @param userContext {@link YukonUserContext}
     * @return {@link String}
     */
    private String graphDataStateMessage(GraphDataError state, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        return accessor.getMessage(state);
    }

    /**
     * Determine if we need to render an x-axis for data, 
     * it then updates the seriesList in place instead of passing back.
     *<p>
     * @param userContext {@link GraphType} is the unique identifier for the range of entries.
     * @param seriesList <code> List<Map<String, Object></code> in place referrenced object
     * @param yAxis <code> List<Map<String, Object>></code> in place referrenced object
     * @param labels <code> ImmutableMap<String, ImmutableMap<String, String></code>
     * @return void
     */
    private void addRightAxis(YukonUserContext userContext, List<Map<String, Object>> seriesList, List<Map<String, Object>> yAxis, ImmutableMap<String, ImmutableMap<String, String>> labels) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        String left = accessor.getMessage("yukon.web.modules.tools.trends.axis", accessor.getMessage("yukon.common.left"));
        String right = accessor.getMessage("yukon.web.modules.tools.trends.axis", accessor.getMessage("yukon.common.right"));
        Map<String, Object> secondaryAxis = new HashMap<>();
        secondaryAxis.put("labels", labels);
        secondaryAxis.put("opposite", true);
        yAxis.add(secondaryAxis);
        for (Map<String, Object> serieData : seriesList) {
            boolean isRight = (Integer) serieData.get("yAxis") == 1;
            serieData.put("name", serieData.get("name") + " - " + (isRight ? right : left));
        }
    }

    @RequestMapping(value = "updateZoom")
    public Map<String, Object> updateZoom(LiteYukonUser user, HttpServletRequest request, @RequestParam(value="zoom") String zoom) {
        Map<String, Object> json = new HashMap<>();
        PreferencePorterQueueCountsZoomOption trendZoom = PreferencePorterQueueCountsZoomOption.valueOf(zoom);
        userPreferenceService.updatePorterPreferenceZoomType(trendZoom, user);
        json.put("success", true);
        return json;
    }

    @RequestMapping("getZoom")
    public @ResponseBody Map<String, Object> getZoom(LiteYukonUser user, HttpServletRequest request) {
        Map<String, Object> json = new HashMap<>();
        PreferencePorterQueueCountsZoomOption trendZoom = userPreferenceService.getPorterDefaultZoomType(user);
        json.put("prefZoom", trendZoom.ordinal());
        return json;
    }

}

