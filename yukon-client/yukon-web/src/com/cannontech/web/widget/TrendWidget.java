package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
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
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;
import com.cannontech.web.widget.support.impl.CachingWidgetParameterGrabber;

/**
 * Widget used to display point data in a trend
 */
public class TrendWidget extends WidgetControllerBase {

    // injected dependencies
    private DeviceDao deviceDao = null;
    private AttributeService attributeService = null;
    private Map<String, AttributeGraphType> supportedAttributeGraphMap = null;
    private DateFormattingService dateFormattingService = null;
    private CachingWidgetParameterGrabber cachingWidgetParameterGrabber = null;
    

    /*
     * (non-Javadoc)
     * @see com.cannontech.web.widget.support.WidgetController#render(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView render(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("trendWidget/render.jsp");
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        // DEVICE
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        YukonDevice device = deviceDao.getYukonDevice(deviceId);

        // ATTRIBUTE, ATTRIBUTE GRAPH TYPE
        // - if selected attribute does not exist, choose first valid
        String defaultAttribute = WidgetParameterHelper.getStringParameter(request, "defaultAttribute", "ENERGY");
        String attributeStr = cachingWidgetParameterGrabber.getCachedStringParameter(request, "attribute", defaultAttribute);
        BuiltInAttribute attribute = BuiltInAttribute.valueOf(attributeStr);
        
        List<AttributeGraphType> availableAttributeGraphs = new ArrayList<AttributeGraphType>();
        Set<Attribute> existingAttributes = attributeService.getAllExistingAttributes(device);
        
        AttributeGraphType attributeGraphType = null;
        
        for (AttributeGraphType agt : supportedAttributeGraphMap.values()) {
            if (existingAttributes.contains(agt.getAttribute())) {
                availableAttributeGraphs.add(agt);
                if (agt.getAttribute() == attribute){
                    attributeGraphType = agt;
                }
            }
        }
        
        if (attributeGraphType == null) {
            attributeGraphType = availableAttributeGraphs.get(0);
            attribute = attributeGraphType.getAttribute();
        }
        
        // GET PERIOD
        String period = cachingWidgetParameterGrabber.getCachedStringParameter(request, "period", "YEAR");
        ChartPeriod chartPeriod = ChartPeriod.valueOf(period);

        // SET START/END DATE
        Date startDate = new Date();
        Date stopDate = new Date();

        if (period.equalsIgnoreCase("YEAR")) { 
            startDate = DateUtils.addYears(startDate, -1); 
        }
        else if (period.equalsIgnoreCase("THREEMONTH")) {
            startDate = DateUtils.addMonths(startDate, -3);
        }
        else if (period.equalsIgnoreCase("MONTH")) {
            startDate = DateUtils.addMonths(startDate, -1);
        }
        else if (period.equalsIgnoreCase("WEEK")) {
            startDate = DateUtils.addWeeks(startDate, -1);
        }
        else if (period.equalsIgnoreCase("DAY")) {
            startDate = DateUtils.addDays(startDate, -1);
        }

        if (period.equalsIgnoreCase("NOPERIOD")) {
            
            String startDateParam = cachingWidgetParameterGrabber.getCachedStringParameter(request, "startDateParam", null);
            String stopDateParam = cachingWidgetParameterGrabber.getCachedStringParameter(request, "stopDateParam", null);
        
            if (startDateParam != null) {
                startDate = dateFormattingService.flexibleDateParser(startDateParam, DateFormattingService.DateOnlyMode.START_OF_DAY, userContext);
            }
            if (stopDateParam != null) {
                stopDate = dateFormattingService.flexibleDateParser(stopDateParam, DateFormattingService.DateOnlyMode.START_OF_DAY, userContext);
            }
            
        }
        else {
            cachingWidgetParameterGrabber.removeFromCache("startDateParam");
            cachingWidgetParameterGrabber.removeFromCache("stopDateParam");
        }

        // GET DATES STRINGS
        String startDateStr = dateFormattingService.formatDate(startDate, DateFormattingService.DateFormatEnum.DATE, userContext);
        String stopDateStr = dateFormattingService.formatDate(stopDate, DateFormattingService.DateFormatEnum.DATE, userContext);
        
        // CHART SYTLE (LINE/COLUMN)
        String defaultGraphType = attributeGraphType.getGraphType().toString();
        String graphTypeString = cachingWidgetParameterGrabber.getCachedStringParameter(request, "graphType", defaultGraphType);
        GraphType graphType = GraphType.valueOf(graphTypeString);

        // TABULAR DATA LINK REQUIREMENTS 
        // - point id
        // - start/stop date as seconds
        // - view controller method name
        LitePoint point = attributeService.getPointForAttribute(device, attribute);
        int pointId = point.getPointID();
        
        Long startDateMillis = startDate.getTime();
        Long stopDateMillis = stopDate.getTime();
        
        String tabularDataViewer = WidgetParameterHelper.getRequiredStringParameter(request, "tabularDataViewer");


        // SET MAV
        mav.addObject("attributeGraphType", attributeGraphType);
        mav.addObject("availableAttributeGraphs", availableAttributeGraphs);
        mav.addObject("period", period);
        mav.addObject("startDateStr", startDateStr);
        mav.addObject("stopDateStr", stopDateStr);
        mav.addObject("graphType", graphType);

        if (!period.equals("NOPERIOD")) {
            mav.addObject("title",
                          "Previous " + chartPeriod.getPeriodLabel() + "'s " + 
                          attributeGraphType.getConverterType().getLabel() + " " + attribute.getKey());
        } else {
            mav.addObject("title",
                          attributeGraphType.getConverterType().getLabel() + " " + attribute.getKey() + 
                          ": " + startDateStr + " - " + stopDateStr);
        }
        
        mav.addObject("pointId", pointId);
        mav.addObject("startDateMillis", startDateMillis);
        mav.addObject("stopDateMillis", stopDateMillis);
        mav.addObject("tabularDataViewer", tabularDataViewer);
        

        return mav;
    }

    @Required
    public void setDeviceDao(DeviceDao paoDao) {
        this.deviceDao = paoDao;
    }

    @Required
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @Required
    public void setSupportedAttributeGraphSet(Set<AttributeGraphType> supportedAttributeGraphSet) {
        supportedAttributeGraphMap = new HashMap<String, AttributeGraphType>();
        for (AttributeGraphType agt : supportedAttributeGraphSet) {
            supportedAttributeGraphMap.put(agt.getLabel(), agt);
        }
    }

    @Required
    public void setDateFormattingService(
            DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    @Required
    public void setCachingWidgetParameterGrabber(
            CachingWidgetParameterGrabber cachingWidgetParameterGrabber) {
        this.cachingWidgetParameterGrabber = cachingWidgetParameterGrabber;
    }

}