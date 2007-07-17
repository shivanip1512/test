package com.cannontech.web.amr.chart;

import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.common.chart.model.ChartPeriod;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.common.chart.service.ChartService;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteUnitMeasure;

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

        ModelAndView mav = new ModelAndView("trendData.jsp");

        // Get point ids from request
        String pointIds = ServletRequestUtils.getRequiredStringParameter(request, "pointIds");
        String[] idStrings = pointIds.split(",");
        int[] ids = new int[idStrings.length];
        for (int i = 0; i < idStrings.length; i++) {
            ids[i] = Integer.valueOf(idStrings[i]);
        }

        // Get period
        String period = ServletRequestUtils.getRequiredStringParameter(request, "period");
        ChartPeriod chartPeriod = ChartPeriod.valueOf(period);

        // Get start date from request - startDateParam is really the stop date
        String startDateParam = ServletRequestUtils.getRequiredStringParameter(request, "startDate");
        Date stopDate = new Date(Long.valueOf(startDateParam));
        Date startDate = chartPeriod.getStartDate(stopDate);

        // Get graph type from request
        String graphTypeString = ServletRequestUtils.getStringParameter(request,
                                                                        "graphType",
                                                                        GraphType.RAW_LINE.toString());
        GraphType graphType = GraphType.valueOf(graphTypeString);

        // Generate x-axis data
        List<ChartValue> xAxisData = chartService.getXAxisData(startDate,
                                                               stopDate,
                                                               chartPeriod.getChartUnit());
        mav.addObject("xAxisValues", xAxisData);

        // Generate graph data
        mav.addObject("graphList", chartService.getGraphs(ids,
                                                          startDate,
                                                          stopDate,
                                                          chartPeriod.getChartUnit(),
                                                          graphType));

        return mav;
    }

    public ModelAndView settings(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("trendSettings.jsp");

        // Get graph type from request
        String graphTypeString = ServletRequestUtils.getStringParameter(request,
                                                                        "graphType",
                                                                        GraphType.RAW_LINE.toString());
        GraphType graphType = GraphType.valueOf(graphTypeString);

        // Get Point's unit of measure and then get the units for the graph
        int pointId = ServletRequestUtils.getIntParameter(request, "pointId");
        LitePoint point = pointDao.getLitePoint(pointId);
        LiteUnitMeasure unitMeasure = unitMeasureDao.getLiteUnitMeasure(point.getUofmID());
        String units = graphType.getUnits(unitMeasure);
        mav.addObject("units", units);

        // Get graph title from request
        String title = ServletRequestUtils.getStringParameter(request, "title");
        mav.addObject("trendTitle", title);

        return mav;
    }

}
