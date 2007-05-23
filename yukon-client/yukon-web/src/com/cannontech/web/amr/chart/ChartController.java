package com.cannontech.web.amr.chart;

import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.common.chart.model.ChartPeriod;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.service.ChartService;

public class ChartController extends MultiActionController {

    private ChartService chartService = null;

    public ChartController() {
        super();
    }

    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }

    public ModelAndView chart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("trendData.jsp");

        String pointIds = request.getParameter("pointIds");
        String[] idStrings = pointIds.split(",");
        int[] ids = new int[idStrings.length];
        for (int i = 0; i < idStrings.length; i++) {
            ids[i] = Integer.valueOf(idStrings[i]);
        }

        String period = request.getParameter("period");
        String startDateParam = request.getParameter("startDate");

        ChartPeriod chartPeriod = ChartPeriod.valueOf(period);

        // startDateParam is really the stop date
        Date stopDate = new Date(Long.valueOf(startDateParam));
        Date startDate = chartPeriod.getStartDate(stopDate);

        List<ChartValue> xAxisData = chartService.getXAxisData(startDate,
                                                               stopDate,
                                                               chartPeriod.getChartUnit());
        mav.addObject("xAxisValues", xAxisData);
        mav.addObject("graphList", chartService.getGraphs(ids,
                                                          startDate,
                                                          stopDate,
                                                          chartPeriod.getChartUnit()));

        return mav;
    }

    public ModelAndView settings(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("trendSettings.jsp");

        String title = request.getParameter("title");
        String unitOfMeasure = request.getParameter("unitOfMeasure");
        mav.addObject("trendTitle", title);
        mav.addObject("unitOfMeasure", unitOfMeasure);

        return mav;
    }

}
