/**
 * Class TrendDataController
 * <p>
 * TrendData is the Controller responsible for providing the consumable
 * Trend Data as JSON serialized nodes 
 * There are no presentations or JSTL transforms or views, and thus remains
 * headless.
 * 
 * <p>
 * The chart types provided are:
 * <ul>
 * <li> Graph @see com.cannontech.web.tools.trends.data.GraphType#BASIC_GRAPH_TYPE
 * <li> Date Graph @see com.cannontech.web.tools.trends.data.GraphType#DATE_GRAPH_TYPE
 * <li> Peak Graph @see com.cannontech.web.tools.trends.data.GraphType#PEAK_GRAPH_TYPE
 * <li> Usage Graph @see com.cannontech.web.tools.trends.data.GraphType#USAGE_GRAPH_TYPE
 * <li> Yesterday Graph @see com.cannontech.web.tools.trends.data.GraphType#YESTERDAY_GRAPH_TYPE
 * <li> Marker @see com.cannontech.web.tools.trends.data.GraphType#MARKER_TYPE
 * </ul>
 *
 * <p>
 * The main REST method is /trends and requires for completion {id} 
 * the point id associated.
 * 
 * 
 * @author      Thomas Red-Cloud
 * @email       ThomasRedCloud@Eaton.com
 * @version     %I%, %G%
 * @since       1.0
 */
package com.cannontech.web.tools.trends;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.tools.trends.data.GraphType;
import com.cannontech.web.tools.trends.data.RenderType;
import com.cannontech.web.tools.trends.data.error.GraphDataError;
import com.cannontech.web.tools.trends.service.TrendDataService;
import com.google.common.collect.ImmutableMap;


@Controller
// @RestController when we get spring 4
@CheckRole(YukonRole.TRENDING)
public class TrendDataController {
    /**
     * The {@link GraphDao} graphDao is a spring injected provider for Graph Series
     */
    @Autowired 
    private GraphDao graphDao;
   
    /**
     * The {@link TrendDataService} is a spring injected provider for Trend Data Service
     */
    @Autowired 
    private TrendDataService trendDataService;
    
    /**
     * The {@link YukonUserContextMessageSourceResolver} messageResolver is a spring injected provider for the Context Mapping
     */
    @Autowired 
    private YukonUserContextMessageSourceResolver messageResolver;
    
    /**
     * The {@link DurationFormattingService} durationFormatting is a spring injected provider for the time encoding mapping
     */
    @Autowired 
    private DurationFormattingService durationFormatting;
    /**
     * The {@link Logger} log is the journal utility for benchmarking and debugging this class. 
     */
    private static final Logger log = YukonLogManager.getLogger(TrendDataController.class);
     /**
     * trend
     * Get the trend data payload for the chart display
     * <p>
     * <ul>
     * <li>step 1 {@link GraphDataSeries} take the series list and begin processing the series
     * <li>step 2 {@link GraphType} determine the graph type to render
     * <li>step 3 {@link RawPointHistoryDao} request a dao for the raw data determined by the graph type
     * <li>step 4 Inject the points into a dataprovider based on GraphType 
     * @see {@link #graphDataProvider(List)}
     * @see {@link #dateGraphDataProvider(List, DateTime, ReadableInstant)}
     * @see {@link #yesterdayGraphDataProvider(List)}
     * @see {@link #usageGraphDataProvider(List)}
     * <li>step 5 add to json object
     * <li>step 6 add payload decoration.
     * <li>step 7 return the json payload to the Response Object to send to client.
     * </ul>
     * 
     * @param id
     * @return {@link ResponseBody} json serialized data.
     */
    @RequestMapping("/trends/{id}/data")
    public @ResponseBody Map<String, Object> trend(YukonUserContext userContext, @PathVariable int id) {
        LiteGraphDefinition trend = graphDao.getLiteGraphDefinition(id);
        List<GraphDataSeries> graphDataSeriesList = graphDao.getGraphDataSeries(trend.getGraphDefinitionID());
        List<GraphDataSeries> dateGraphDataSeriesList = new ArrayList<>();
        List<Map<String, Object>> seriesList = new ArrayList<>();
        List<Map<String, Object>> plotLines = new ArrayList<>();
        Map<String, Object> yAxisProperties = new HashMap<>();
        DateTime chartDatePrime = DateTime.now();
        DateTime chartDateLimit = DateTime.now();
        boolean hasCurrentDateBoundary = false;
        boolean showRightAxis = false;
        for (GraphDataSeries seriesItem : graphDataSeriesList) {
            log.debug("Graph Type:" + seriesItem.getType() + ":" + graphTypeLabel(seriesItem.getType(), userContext));
            Map<String, Object> seriesProperties = new HashMap<>();
            List<PointValueHolder> seriesItemResult = new ArrayList<>();
            List<Object[]> seriesData = new ArrayList<>();
            GraphType itemType = GraphType.getByType(seriesItem.getType());
            switch (itemType) {
            case DATE_GRAPH_TYPE:
            case DATE_TYPE:
            case PEAK_GRAPH_TYPE:
            case PEAK_TYPE:
                dateGraphDataSeriesList.add(seriesItem);
                continue;
            case USAGE_TYPE:
            case USAGE_GRAPH_TYPE:
                seriesItemResult = trendDataService.rawPointHistoryDataProvider(seriesItem.getPointID());
                seriesData = trendDataService.usageGraphDataProvider(seriesItemResult);
                break;
            case YESTERDAY_GRAPH_TYPE:
            case YESTERDAY_TYPE:
                seriesItemResult = trendDataService.rawPointHistoryDataProvider(seriesItem.getPointID());
                seriesData = trendDataService.yesterdayGraphDataProvider(seriesItemResult);
                break;
            case GRAPH_TYPE:
            case BASIC_GRAPH_TYPE:
                seriesItemResult = trendDataService.rawPointHistoryDataProvider(seriesItem.getPointID());
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
            if (!seriesItemResult.isEmpty()) {
                DateTime valueDeltaPrimeDT = new DateTime(seriesItemResult.get(0).getPointDataTimeStamp());
                DateTime valueDeltaLimitDT = new DateTime(seriesItemResult.get(seriesData.size() - 1).getPointDataTimeStamp());

                if (hasCurrentDateBoundary) {

                    chartDatePrime = (chartDatePrime.isAfter(valueDeltaLimitDT)) ? valueDeltaPrimeDT : chartDatePrime;
                    chartDateLimit = (chartDateLimit.isBefore(valueDeltaLimitDT)) ? valueDeltaLimitDT : chartDateLimit;

                } else {
                    chartDatePrime = new DateTime(valueDeltaPrimeDT);
                    chartDateLimit = new DateTime(valueDeltaLimitDT);
                    hasCurrentDateBoundary = true;
                }
            }
            seriesProperties.put("name", seriesItem.getLabel() + " " + graphTypeLabel(seriesItem.getType(), userContext));
            log.debug("color from series:" + seriesItem.getColor());
            seriesProperties.put("color", Colors.colorPaletteToWeb(seriesItem.getColor()));
            seriesList.add(seriesProperties);
        }
        for (GraphDataSeries seriesItem : dateGraphDataSeriesList) {
            Map<String, Object> seriesProperties = new HashMap<>();
            List<PointValueHolder> seriesItemResult = new ArrayList<>();
            List<Object[]> seriesData = new ArrayList<>();
            GraphType itemType = GraphType.getByType(seriesItem.getType());
            DateTime specificDate;
            DateTime endDate;
            if (itemType.equals(GraphType.PEAK_GRAPH_TYPE)) {
                long ts = Long.valueOf(seriesItem.getMoreData()).longValue();
                DateTime dateToStartSearch = new DateTime(ts);
                specificDate = new DateTime(trendDataService.requestPeakDateDataProvider(seriesItem.getPointID(), dateToStartSearch), 
                userContext.getJodaTimeZone()).withTimeAtStartOfDay();
                log.debug("specificDate:" + specificDate.toString());
                endDate = specificDate.plusDays(1);
                log.debug("endDate:" + endDate.toString());
                log.debug("pointID:" + seriesItem.getPointID());
                seriesItemResult = trendDataService.datePointHistoryDataProvider(seriesItem.getPointID(), specificDate, endDate);
                log.debug("Data size:" + seriesItemResult.size());
                seriesData = trendDataService.dateGraphDataProvider(seriesItemResult, chartDatePrime, chartDateLimit);
            } else {
                specificDate = new DateTime(seriesItem.getSpecificDate());
                endDate = specificDate.plusDays(1);
                seriesItemResult = trendDataService.datePointHistoryDataProvider(seriesItem.getPointID(), specificDate, endDate);
                seriesData = trendDataService.dateGraphDataProvider(seriesItemResult, chartDatePrime, chartDateLimit);
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
            if (!seriesItemResult.isEmpty()) {
                if (PointType.getForId(seriesItemResult.get(0).getType()).isStatus()) {
                    seriesProperties.put("dataGrouping", ImmutableMap.of("enabled", false));
                }
            }
            DateFormat df = new SimpleDateFormat(" [MM/dd/yyyy] ");
            String reportDate = df.format(specificDate.toDate());
            seriesProperties.put("name", seriesItem.getLabel() + " " + graphTypeLabel(seriesItem.getType(), userContext) + reportDate);

            seriesProperties.put("color", Colors.colorPaletteToWeb(seriesItem.getColor()));
            seriesList.add(seriesProperties);
        }
        Map<String, Object> json = new HashMap<>();
        json.put("name", trend.getName());
        json.put("series", seriesList);
        List<Map<String, Object>> yAxis = new ArrayList<>();
        ImmutableMap<String, ImmutableMap<String, String>> labels = ImmutableMap.of("style", ImmutableMap.of("color", "#555"));
        yAxisProperties.put("labels", labels);
        yAxis.add(yAxisProperties);
        if (showRightAxis) {
            addRightAxis(userContext, seriesList, yAxis, labels);
        }
        json.put("yAxis", yAxis);
        return json;
    }
    
    /**
     * graphDataStateMessage
     * This data provider a {@link GraphDataError} to render the message, and the YukonUserContext instance of interface
     * and grabs the message stored in the context mapping. 
     * The call returns a String of data.
     * <p>
     * @param state {@link GraphDataError} is state of the data for being passed back to the client.
     * @param userContext{@link YukonUserContext}
     * @return {@link String}
     */
    private String graphDataStateMessage(GraphDataError state, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        return accessor.getMessage(state);
    }

    /**
     * graphTypeLabel
     * This method takes the integer form of the graphType that is native to the DataSource, match
     * it to the GraphType enumerator and pass back the associate context stored for localization.
     * The call returns a String Identifier
     * <p>
     * @param graphType {@link int} is the unique identifier for the range of entries.
     * @param userContext{@link YukonUserContext}
     * 
     * @return {@link String}
     */
    private String graphTypeLabel(int graphType, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        GraphType graph = GraphType.getByType(graphType);
        return accessor.getMessage(graph);
    }

    /**
     * addRightAxis
     * right access determine if we need to arnder a x-axis for data, it then updates the seriesList in place instead 
     * of passsing back. 
     * <p>
     * @param userContext {@link GraphType} is the unique identifier for the range of entries.
     * @param  seriesList <code> List<Map<String, Object></code> in place referrenced object 
     * @param  yAxis <code> List<Map<String, Object>></code> in place referrenced object
     * @param labels <code> ImmutableMap<String, ImmutableMap<String, String></code>
     * @return void
     */
    private void addRightAxis(YukonUserContext userContext, List<Map<String, Object>> seriesList, List<Map<String, Object>> yAxis, ImmutableMap<String, ImmutableMap<String, String>> labels) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        String left = accessor.getMessage("yukon.web.modules.tools.trend.axis", accessor.getMessage("yukon.common.left"));
        String right = accessor.getMessage("yukon.web.modules.tools.trend.axis", accessor.getMessage("yukon.common.right"));
        Map<String, Object> secondaryAxis = new HashMap<>();
        secondaryAxis.put("labels", labels);
        secondaryAxis.put("opposite", true);
        yAxis.add(secondaryAxis);
        for (Map<String, Object> serieData : seriesList) {
            boolean isRight = (Integer) serieData.get("yAxis") == 1;
            serieData.put("name", serieData.get("name") + " - " + (isRight ? right : left));
        }
    }

}