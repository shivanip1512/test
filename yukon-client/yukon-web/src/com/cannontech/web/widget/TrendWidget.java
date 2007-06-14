package com.cannontech.web.widget;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.chart.model.AttributeGraphType;
import com.cannontech.common.chart.model.ChartPeriod;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display point data in a trend
 */
public class TrendWidget extends WidgetControllerBase {

    private DeviceDao deviceDao = null;
    private AttributeService attributeService = null;
    private Map<String, AttributeGraphType> supportedAttributeGraphMap = null;

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

    @Override
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ModelAndView mav = new ModelAndView();

        // Get lite pao
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        YukonDevice device = deviceDao.getYukonDevice(deviceId);

        // Get the selected attribute
        String selectedAttributeLabel = WidgetParameterHelper.getStringParameter(request,
                                                                                 "selectedAttributeGraph",
                                                                                 "Normalized Usage");
        AttributeGraphType selectedAttributeGraph = supportedAttributeGraphMap.get(selectedAttributeLabel);

        // Get the set of attributes that the trend supports and that exist for
        // the device
        Set<Attribute> existingAtributes = attributeService.getAllExistingAtributes(device);

        Set<AttributeGraphType> availableAttributeGraphs = new HashSet<AttributeGraphType>();
        for (AttributeGraphType agt : supportedAttributeGraphMap.values()) {
            if (existingAtributes.contains(agt.getAttribute())) {
                availableAttributeGraphs.add(agt);
            }
        }

        // Get point based on device and attribute
        LitePoint point = attributeService.getPointForAttribute(device,
                                                                selectedAttributeGraph.getAttribute());

        // Get start date and period
        Calendar startDate = new GregorianCalendar();
        startDate.setTime(new Date());
        String periodString = WidgetParameterHelper.getStringParameter(request, "period", "DAY");
        ChartPeriod period = ChartPeriod.valueOf(periodString);

        mav.addObject("availableAttributeGraphs", availableAttributeGraphs);
        mav.addObject("selectedAttributeGraph", selectedAttributeGraph);
        mav.addObject("title",
                      "Previous " + period.getPeriodLabel() + "'s " + 
                      selectedAttributeGraph.getGraphType().getLabel() + " " + 
                      selectedAttributeGraph.getAttribute().getKey());
        mav.addObject("pointIds", point.getPointID());
        mav.addObject("startDate", startDate.getTimeInMillis());
        mav.addObject("period", periodString);

        return mav;
    }

}
