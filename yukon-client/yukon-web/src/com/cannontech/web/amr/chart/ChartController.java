package com.cannontech.web.amr.chart;

import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartPeriod;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.model.ConverterType;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.common.chart.service.ChartService;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;

public class ChartController extends MultiActionController {

    private ChartService chartService = null;
    private UnitMeasureDao unitMeasureDao = null;
    private PointDao pointDao = null;

    public ChartController() {
        super();
    }

    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }

    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }

    public void setUnitMeasureDao(UnitMeasureDao unitMeasureDao) {
        this.unitMeasureDao = unitMeasureDao;
    }

    public ModelAndView chart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
    	
        // setup mav with the appropriate jsp, depending on grapth style: LINE
        // or COLUMN (decided in trend.tag)
        String amChartsProduct = ServletRequestUtils.getStringParameter(request,
                                                                        "amChartsProduct");
        ModelAndView mav = new ModelAndView("trendData_" + amChartsProduct + ".jsp");

        // Get point ids from request
        String pointIds = ServletRequestUtils.getRequiredStringParameter(request, "pointIds");
        String[] idStrings = pointIds.split(",");
        int[] ids = new int[idStrings.length];
        for (int i = 0; i < idStrings.length; i++) {
            ids[i] = Integer.valueOf(idStrings[i]);
        }

        // Get period
        String period = ServletRequestUtils.getRequiredStringParameter(request,
                                                                       "period");

        // startDate, endDate
        String endDateParam = ServletRequestUtils.getRequiredStringParameter(request,
                                                                             "endDate");
        Date endDate = new Date(Long.valueOf(endDateParam));

        String startDateParam = ServletRequestUtils.getRequiredStringParameter(request,
                                                                               "startDate");
        Date startDate = new Date(Long.valueOf(startDateParam));

        ChartPeriod chartPeriod = ChartPeriod.valueOf(period);
        ChartInterval chartInterval = chartPeriod.getChartUnit(startDate,
                                                               endDate);

        // Get graph type from request
        String graphTypeString = ServletRequestUtils.getStringParameter(request,
                                                                        "graphType",
                                                                        GraphType.LINE.toString());
        GraphType graphType = GraphType.valueOf(graphTypeString);

        // Get converter type from request
        String converterTypeString = ServletRequestUtils.getStringParameter(request,
                                                                            "converterType",
                                                                            ConverterType.RAW.toString());
        ConverterType converterType = ConverterType.valueOf(converterTypeString);

        // Generate x-axis data
        List<ChartValue<Date>> xAxisData = chartService.getXAxisData(startDate,
                                                               endDate,
                                                               chartInterval,
                                                               userContext);
        mav.addObject("xAxisValues", xAxisData);

        // Generate graph data
        mav.addObject("graphList", chartService.getGraphs(ids,
                                                          startDate,
                                                          endDate,
                                                          chartInterval,
                                                          graphType,
                                                          converterType));

        return mav;
    }

    public ModelAndView settings(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        // setup mav with the appropriate jsp, depending on grapth style: LINE
        // or COLUMN (decided in trend.tag)
        String amChartsProduct = ServletRequestUtils.getStringParameter(request,
                                                                        "amChartsProduct");
        ModelAndView mav = new ModelAndView("trendSettings_" + amChartsProduct + ".jsp");

        // Get converter type from request
        String converterTypeString = ServletRequestUtils.getStringParameter(request,
                                                                            "converterType",
                                                                            ConverterType.RAW.toString());

        ConverterType converterType = ConverterType.valueOf(converterTypeString);

        // Get Point's unit of measure and then get the units for the graph
        String pointIds = ServletRequestUtils.getStringParameter(request, "pointIds");
        
        String[] idStrings = pointIds.split(",");
        int[] ids = new int[idStrings.length];
        for (int i = 0; i < idStrings.length; i++) {
            ids[i] = Integer.valueOf(idStrings[i]);
        }
        
        LitePoint point = pointDao.getLitePoint(ids[0]);
        LiteUnitMeasure unitMeasure = unitMeasureDao.getLiteUnitMeasure(point.getUofmID());
        String units = converterType.getUnits(unitMeasure);
        mav.addObject("units", units);

        // Get graph title from request
        String title = ServletRequestUtils.getStringParameter(request, "title");
        mav.addObject("trendTitle", title);

        // Get period
        String period = ServletRequestUtils.getRequiredStringParameter(request,
                                                                       "period");

        // startDate, endDate
        Date startDate = null;
        Date endDate = null;
        ChartInterval chartInterval = null;

        String endDateParam = ServletRequestUtils.getRequiredStringParameter(request,
                                                                             "endDate");
        endDate = new Date(Long.valueOf(endDateParam));

        String startDateParam = ServletRequestUtils.getRequiredStringParameter(request,
                                                                               "startDate");
        startDate = new Date(Long.valueOf(startDateParam));

        ChartPeriod chartPeriod = ChartPeriod.valueOf(period);
        chartInterval = chartPeriod.getChartUnit(startDate, endDate);

        // Generate x-axis data
        int xAxisDataCount = chartService.getXAxisDataCount(startDate,
                                                               endDate,
                                                               chartInterval);

        int gridFrequency = (int) (xAxisDataCount / (float) 4);
        if (gridFrequency < 1) {
            gridFrequency = 1;
        }

        mav.addObject("gridFrequency", gridFrequency);

        return mav;
    }

}
