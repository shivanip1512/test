package com.cannontech.web.widget;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.chart.model.AttributeGraphType;
import com.cannontech.common.chart.model.ChartPeriod;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.widget.support.CachingWidgetParameterHelper;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display point data in a trend
 */
public class TrendWidget extends WidgetControllerBase {

    private DeviceDao deviceDao = null;
    private AttributeService attributeService = null;
    private Map<String, AttributeGraphType> supportedAttributeGraphMap = null;
    private DateFormattingService dateFormattingService = null;

    public void setDeviceDao(DeviceDao paoDao) {
        this.deviceDao = paoDao;
    }

    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    public void setSupportedAttributeGraphSet(Set<AttributeGraphType> supportedAttributeGraphSet) {
        supportedAttributeGraphMap = new HashMap<String, AttributeGraphType>();
        for (AttributeGraphType agt : supportedAttributeGraphSet) {
            supportedAttributeGraphMap.put(agt.getLabel(), agt);
        }
    }

    @Required
    public DateFormattingService getDateFormattingService() {
        return dateFormattingService;
    }

    public void setDateFormattingService(
            DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }

    public ModelAndView render(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView();
        
        CachingWidgetParameterHelper cachingWidgetParameterHelper = new CachingWidgetParameterHelper(request, "trendWidget");

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        // Get lite pao
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        YukonDevice device = deviceDao.getYukonDevice(deviceId);

        // Get the selected attribute graph
        String selectedAttributeLabel = cachingWidgetParameterHelper.getStringParameter("selectedAttributeLabel", null, deviceId);
        
        AttributeGraphType selectedAttributeGraph = supportedAttributeGraphMap.get(selectedAttributeLabel);

        // Get the set of attributes that the trend supports and that exist for
        // the device
        Set<Attribute> existingAttributes = attributeService.getAllExistingAttributes(device);

        boolean graphSelected = selectedAttributeGraph != null;
        Set<AttributeGraphType> availableAttributeGraphs = new HashSet<AttributeGraphType>();
        for (AttributeGraphType agt : supportedAttributeGraphMap.values()) {
            if (existingAttributes.contains(agt.getAttribute())) {
                availableAttributeGraphs.add(agt);

                if (selectedAttributeGraph == null || (!graphSelected && agt.getAttribute() == BuiltInAttribute.ENERGY)) {
                    selectedAttributeGraph = agt;
                }

            }
        }

        if (selectedAttributeGraph != null) {
            // Get point based on device and attribute
            LitePoint point = attributeService.getPointForAttribute(device,
                                                                    selectedAttributeGraph.getAttribute());
            int pointId = point.getPointID();

            // default start date (one year ago from today)
            Calendar startDateCal = dateFormattingService.getCalendar(userContext);
            Date startDate = new Date();
            startDateCal.setTime(startDate);

            // graph type (line/column)
            String defaultGraphType = selectedAttributeGraph.getGraphType().toString();
            String graphTypeString = cachingWidgetParameterHelper.getStringParameter("graphType", defaultGraphType, deviceId);



            GraphType graphType = null;
            if (graphTypeString.equals("LINE")) {
                graphType = GraphType.LINE;
            } else if (graphTypeString.equals("COLUMN")) {
                graphType = GraphType.COLUMN;
            }

            // period
            String periodString = cachingWidgetParameterHelper.getStringParameter("period", "YEAR", deviceId);
            
            
            ChartPeriod period = ChartPeriod.valueOf(periodString);

            
            
            // default end date (today)
            Calendar endDateCal = dateFormattingService.getCalendar(userContext);
            Date endDate = new Date();
            endDateCal.setTime(endDate);

            // adjust startDateCal for period selected
            if (periodString.equals("YEAR")) {
                startDateCal.add(Calendar.YEAR, -1);
            } else if (periodString.equals("THREEMONTH")) {
                startDateCal.add(Calendar.MONTH, -3);
            } else if (periodString.equals("MONTH")) {
                startDateCal.add(Calendar.MONTH, -1);
            } else if (periodString.equals("WEEK")) {
                startDateCal.add(Calendar.DATE, -7);
            } else if (periodString.equals("DAY")) {
                startDateCal.add(Calendar.DATE, -1);
            }

            // init start end date as strings
            String startDateStr = dateFormattingService.formatDate(startDateCal.getTime(),
                                                                   DateFormattingService.DateFormatEnum.DATE,
                                                                   userContext);
            String endDateStr = dateFormattingService.formatDate(endDateCal.getTime(),
                                                                 DateFormattingService.DateFormatEnum.DATE,
                                                                 userContext);

            // if no period, re adjust
            if (periodString.equals("NOPERIOD")) {

                String startDateParam = cachingWidgetParameterHelper.getStringParameter("startDateParam", "", deviceId);
                String endDateParam = cachingWidgetParameterHelper.getStringParameter("endDateParam", "", deviceId);

                if (!startDateParam.equals("")) {

                    startDate = dateFormattingService.flexibleDateParser(startDateParam,
                                                                         DateFormattingService.DateOnlyMode.START_OF_DAY,
                                                                         userContext);
                    startDateCal.setTime(startDate);
                    startDateStr = dateFormattingService.formatDate(startDateCal.getTime(),
                                                                    DateFormattingService.DateFormatEnum.DATE,
                                                                    userContext);
                }

                if (!endDateParam.equals("")) {

                    endDate = dateFormattingService.flexibleDateParser(endDateParam,
                                                                       DateFormattingService.DateOnlyMode.START_OF_DAY,
                                                                       userContext);
                    endDateCal.setTime(endDate);
                    endDateStr = dateFormattingService.formatDate(endDateCal.getTime(),
                                                                  DateFormattingService.DateFormatEnum.DATE,
                                                                  userContext);
                }
            }

            // set mav
            mav.addObject("availableAttributeGraphs", availableAttributeGraphs);
            mav.addObject("graphType", graphType);
            mav.addObject("selectedAttributeGraph", selectedAttributeGraph);

            mav.addObject("startDateParam", startDateStr);
            mav.addObject("endDateParam", endDateStr);

            if (!periodString.equals("NOPERIOD")) {
                mav.addObject("title",
                              "Previous " + period.getPeriodLabel() + "'s " + selectedAttributeGraph.getConverterType()
                                                                                                    .getLabel() + " " + selectedAttributeGraph.getAttribute()
                                                                                                                                              .getKey());
            } else {
                mav.addObject("title",
                              selectedAttributeGraph.getConverterType()
                                                    .getLabel() + " " + selectedAttributeGraph.getAttribute()
                                                                                              .getKey() + ": " + startDateStr + " - " + endDateStr);
            }

            mav.addObject("deviceId", deviceId);
            mav.addObject("pointIds", pointId);
            mav.addObject("startDateMillis", startDateCal.getTimeInMillis());
            mav.addObject("endDateMillis", endDateCal.getTimeInMillis());
            mav.addObject("startDate", startDateCal.getTime());
            mav.addObject("stopDate", endDateCal.getTime());
            mav.addObject("period", periodString);

        }

        return mav;
    }

}