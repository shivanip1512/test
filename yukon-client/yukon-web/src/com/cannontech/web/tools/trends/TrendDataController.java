package com.cannontech.web.tools.trends;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Months;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.gui.util.Colors;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.GraphDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.users.model.PreferenceTrendZoomOption;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.graph.GDSTypesFuncs;
import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.tools.trends.data.RenderType;
import com.cannontech.web.tools.trends.data.TrendType;
import com.cannontech.web.tools.trends.data.TrendType.GraphType;
import com.cannontech.web.tools.trends.data.error.GraphDataError;
import com.cannontech.web.tools.trends.service.TrendDataService;
import com.cannontech.web.user.service.UserPreferenceService;
import com.google.common.collect.ImmutableMap;

@Controller
@CheckRole({ YukonRole.TRENDING, YukonRole.CI_CURTAILMENT })

public class TrendDataController {

    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private GraphDao graphDao;
    @Autowired private TrendDataService trendDataService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private UserPreferenceService userPreferenceService;
    @Autowired private GlobalSettingDao globalSettingDao;
    
    private static final Logger log = YukonLogManager.getLogger(TrendDataController.class);

    @GetMapping("/trends/{id}/data")
    public @ResponseBody Map<String, Object> trend(YukonUserContext userContext, @PathVariable int id) {
        LiteGraphDefinition trend = graphDao.getLiteGraphDefinition(id);
        return getTrendJson(userContext,trend, false);
    }
    
    @GetMapping("/trends/widgetDisplay/{id}/data")
    public @ResponseBody Map<String, Object> getTrendForWidgetDisplay(YukonUserContext userContext, @PathVariable int id) {
        LiteGraphDefinition trend = graphDao.getLiteGraphDefinition(id);
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        if (trend != null) {
            json = getTrendJson(userContext, trend, true);
            Instant lastUpdateTime = new Instant();
            json.put("lastAttemptedRefresh", lastUpdateTime);
            json.put("refreshMillis", trendDataService.getRefreshMilliseconds());
            Instant nextRun = trendDataService.getNextRefreshTime(lastUpdateTime);
            json.put("nextRun", nextRun);
            json.put("updateTooltip", accessor.getMessage("yukon.web.widgets.forceUpdate"));
        } else {
            json.put("errorMessage", accessor.getMessage("yukon.web.modules.dashboard.exception.trendId.notFound"));
        }
        return json;
    }

    /**
     * trend Get the trend data payload for the chart display
     * <p>
     * <ul>
     * <li>step 1 {@link GraphDataSeries} take the series list and begin
     * processing the series
     * <li>step 2 {@link GraphType} determine the graph type to render
     * <li>step 3 {@link RawPointHistoryDao} request a dao for the raw data
     * determined by the graph type
     * <li>step 4 Inject the points into a dataprovider based on GraphType
     * <li>step 5 add to json object
     * <li>step 6 add payload decoration. <li>step 7 return the json
     *     payload to the Response Object to send to client.
     *</ul>
     * @see {@link #graphDataProvider(List)}
     * @see {@link #dateGraphDataProvider(List, DateTime, ReadableInstant)}
     * @see {@link #yesterdayGraphDataProvider(List)}
     * @see {@link #usageGraphDataProvider(List)} 
     * 
     * @param trend - the LiteGraphDefinition object
     * @return {@link ResponseBody} json serialized data.
     */
    private Map<String, Object> getTrendJson(YukonUserContext userContext, LiteGraphDefinition trend, boolean isDisplayedInWidget) {
        
        List<GraphDataSeries> graphDataSeriesList = graphDao.getGraphDataSeries(trend.getGraphDefinitionID());
        List<GraphDataSeries> dateGraphDataSeriesList = new ArrayList<>();
        List<Map<String, Object>> seriesList = new ArrayList<>();
        List<Map<String, Object>> plotLines = new ArrayList<>();
        Map<String, Object> yAxisProperties = new HashMap<>();
        DateTime chartDatePrime = DateTime.now();
        DateTime chartDateLimit = DateTime.now();
        boolean hasCurrentDateBoundary = false;
        boolean showRightAxis = false;
        boolean isTruncated = false;
        /* Calculate earliestStartDate */
        Months months = Months.months(globalSettingDao.getInteger(GlobalSettingType.TRENDS_HISTORICAL_MONTHS));
        if (isDisplayedInWidget) {
            months = Months.ONE;
        }
        DateTime earliestStartDate = new DateTime().withTimeAtStartOfDay().minus(months);
        
        for (GraphDataSeries seriesItem : graphDataSeriesList) {
            TrendType itemType = TrendType.of(seriesItem.getType());
            log.info("TrendType:" + itemType.getGraphType() + " Graph Type:" + GDSTypesFuncs.getType(seriesItem.getType()));
            if (!itemType.isGraphType()) {
                continue;
            }

            Map<String, Object> seriesProperties = new HashMap<>();
            List<PointValueHolder> seriesItemResult = new ArrayList<>();
            List<Object[]> seriesData = new ArrayList<>();

            switch (itemType.getGraphType()) {
            case DATE_TYPE:
            case PEAK_TYPE:
                dateGraphDataSeriesList.add(seriesItem);
                continue;
            case USAGE_TYPE:
                seriesItemResult = getSeriesItemResultForPoint(seriesItem.getPointID(), earliestStartDate);
                seriesData = trendDataService.usageGraphDataProvider(seriesItemResult, earliestStartDate);
                break;
            case YESTERDAY_TYPE:
                seriesItemResult = getSeriesItemResultForPoint(seriesItem.getPointID(), earliestStartDate);
                seriesData = trendDataService.yesterdayGraphDataProvider(seriesItemResult);
                break;
            case BASIC_TYPE:
                seriesItemResult = getSeriesItemResultForPoint(seriesItem.getPointID(), earliestStartDate);
                seriesData = trendDataService.graphDataProvider(seriesItemResult);
                break;
            case MARKER_TYPE:
                seriesProperties.put("threshold-value", seriesItem.getMultiplier());
                Map<String, Object> plotLineProperties = new HashMap<>();
                plotLineProperties.put("color", Colors.colorPaletteToWeb(seriesItem.getColor()));
                plotLineProperties.put("width", 2);
                plotLineProperties.put("value", seriesItem.getMultiplier());
                plotLines.add(plotLineProperties);
                yAxisProperties.put("plotLines", plotLines);
                break;
            }
            if (globalSettingDao.getInteger(GlobalSettingType.TRENDS_READING_PER_POINT) == seriesItemResult.size()) {
                isTruncated = true;
            }
            if (seriesData.isEmpty()) {
                seriesProperties.put("error", graphDataStateMessage(GraphDataError.NO_TREND_DATA_AVAILABLE, userContext));
            }
            seriesProperties.put("data", seriesData);
            if (seriesItem.isRight()) {
                seriesProperties.put("yAxis", 1);
                showRightAxis = true;
            } else {
                seriesProperties.put("yAxis", 0);
            }
            RenderType type = RenderType.getForId(seriesItem.getRenderer());
            if (type.isBar()) {
                seriesProperties.put("type", "column");
            } else if (type.isStep()) {
                seriesProperties.put("step", true);
            }
            if (!seriesItemResult.isEmpty()) {
                if (PointType.getForId(seriesItemResult.get(0).getType()).isStatus()) {
                    seriesProperties.put("dataGrouping", ImmutableMap.of("enabled", false));
                }
            }
            
            // Set the chartDate boundaries, exclude peak/yesterday/specific dates from being used to determine
            if (!seriesItemResult.isEmpty() && 
                    (itemType.getGraphType() == GraphType.BASIC_TYPE || itemType.getGraphType() == GraphType.USAGE_TYPE)) {
                DateTime valueDeltaPrimeDT = new DateTime(seriesItemResult.get(0).getPointDataTimeStamp());
                DateTime valueDeltaLimitDT = new DateTime(seriesItemResult.get(seriesData.size() - 1).getPointDataTimeStamp());
                log.debug("Series Boundaries: " + valueDeltaPrimeDT.toString() + " - " + valueDeltaLimitDT.toString());

                if (hasCurrentDateBoundary) {
                    chartDatePrime = (chartDatePrime.isAfter(valueDeltaPrimeDT)) ? valueDeltaPrimeDT : chartDatePrime;
                    chartDateLimit = (chartDateLimit.isBefore(valueDeltaLimitDT)) ? valueDeltaLimitDT : chartDateLimit;
                } else {
                    chartDatePrime = valueDeltaPrimeDT;
                    chartDateLimit = valueDeltaLimitDT;
                    hasCurrentDateBoundary = true;
                }
            }
            log.debug("Adjusted Boundaries: " + chartDatePrime.toString() + " - " + chartDateLimit.toString());
            
            seriesProperties.put("name", seriesItem.getLabel() + " " + graphTypeLabel(seriesItem.getType(), userContext));
            log.debug("color from series:" + seriesItem.getColor());
            seriesProperties.put("color", Colors.colorPaletteToWeb(seriesItem.getColor()));
            seriesList.add(seriesProperties);
        }
        
        for (GraphDataSeries seriesItem : dateGraphDataSeriesList) {
            Map<String, Object> seriesProperties = new HashMap<>();
            List<PointValueHolder> seriesItemResult = new ArrayList<>();
            List<Object[]> seriesData = new ArrayList<>();
            TrendType itemType = TrendType.of(seriesItem.getType());
            Integer pointId = seriesItem.getPointID();
            DateTime specificDate;
            DateTime endDate;
            if (itemType.getGraphType() == (TrendType.GraphType.PEAK_TYPE)) {
                long millis = Long.valueOf(seriesItem.getMoreData()).longValue();
                Date dateToStartSearch = new Date(millis);
                specificDate = trendDataService.requestPeakDateDataProvider(pointId, dateToStartSearch, userContext);
            } else {
                specificDate = new DateTime(seriesItem.getSpecificDate());
            }
            
            endDate = specificDate.plusDays(1);
            seriesItemResult = trendDataService.datePointHistoryDataProvider(pointId, specificDate, endDate);
            seriesData = trendDataService.dateGraphDataProvider(seriesItemResult, chartDatePrime, chartDateLimit, earliestStartDate);
            
            if (seriesData.isEmpty()) {
                seriesProperties.put("error", graphDataStateMessage(GraphDataError.NO_TREND_DATA_AVAILABLE, userContext));
            }
            seriesProperties.put("data", seriesData);
            if (seriesItem.isRight()) {
                seriesProperties.put("yAxis", 1);
                showRightAxis = true;
            } else {
                seriesProperties.put("yAxis", 0);
            }
            if (!seriesItemResult.isEmpty()) {
                if (PointType.getForId(seriesItemResult.get(0).getType()).isStatus()) {
                    seriesProperties.put("dataGrouping", ImmutableMap.of("enabled", false));
                }
            }
            String reportDate = dateFormattingService.format(specificDate, DateFormatEnum.DATE, userContext);
            seriesProperties.put("name", seriesItem.getLabel() + " " + graphTypeLabel(seriesItem.getType(), userContext) +" " + reportDate);
            seriesProperties.put("color", Colors.colorPaletteToWeb(seriesItem.getColor()));
            seriesList.add(seriesProperties);
        }
        Map<String, Object> json = new HashMap<>();
        json.put("name", trend.getName());
        json.put("series", seriesList);
        if (isTruncated) {
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            String truncateMessage = accessor.getMessage("yukon.web.modules.tools.trends.truncateWarning");
            json.put("truncateMessage", truncateMessage);
        }

        List<Map<String, Object>> yAxis = new ArrayList<>();
        ImmutableMap<String, ImmutableMap<String, String>> labels = ImmutableMap.of("style", ImmutableMap.of("color", "#555"));
        yAxisProperties.put("labels", labels);
        yAxisProperties.put("opposite", false);
        yAxis.add(yAxisProperties);
        if (showRightAxis) {
            addRightAxis(userContext, seriesList, yAxis, labels);
        }
        json.put("yAxis", yAxis);
        return json;
    }

    private List<PointValueHolder> getSeriesItemResultForPoint(Integer pointID, DateTime earliestStartDate) {
        Instant start = earliestStartDate.toInstant();
        Instant end = new DateTime().withTimeAtStartOfDay().plusDays(1).toInstant();
        return trendDataService.rawPointHistoryDataProvider(pointID, start, end);
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
     * Returns context sensitive message for {@link GraphType}
     * <p>
     * @param graphType {@link int} the GraphDataSeries.type value.
     * @param userContext {@link YukonUserContext}
     * @return {@link String}
     */
    private String graphTypeLabel(int graphType, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        GraphType graph = TrendType.of(graphType).getGraphType();
        return accessor.getMessage(graph);
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

    @RequestMapping("/trends/updateZoom")
    public void updateZoom(LiteYukonUser user, HttpServletRequest request) {
        PreferenceTrendZoomOption trendZoom = PreferenceTrendZoomOption.valueOf(request.getParameter("value"));
        userPreferenceService.updatePreferenceZoomType(trendZoom, user);
    }

    @RequestMapping("/trends/getZoom")
    @ResponseBody
    public Map<String, Object> getZoom(LiteYukonUser user, HttpServletRequest request) {
        Map<String, Object> json = new HashMap<>();
        PreferenceTrendZoomOption trendZoom = userPreferenceService.getDefaultZoomType(user);
        json.put("prefZoom", trendZoom.ordinal());
        return json;
    }
}