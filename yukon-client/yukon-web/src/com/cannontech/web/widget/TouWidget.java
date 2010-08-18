package com.cannontech.web.widget;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.deviceread.dao.PlcDeviceAttributeReadService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeHelper;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;
import com.google.common.collect.Sets;

/**
 * Widget used to display basic device information
 */
public class TouWidget extends WidgetControllerBase {

    private AttributeService attributeService;
    private MeterDao meterDao;
    private PlcDeviceAttributeReadService plcDeviceAttributeReadService;

    /**
     * This method renders the default deviceGroupWidget
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView render(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("touWidget/render.jsp");
        Meter meter = getMeter(request);
        LiteYukonUser user = ServletUtil.getYukonUser(request);

        // Finds the existing attributes for the supplied meter
        Set<Attribute> allExistingAttributes = attributeService.getAllExistingAttributes(meter);
        Set<Attribute> existingTouAttributes =
            Sets.intersection(allExistingAttributes,AttributeHelper.getTouAttributes());

        boolean readable = plcDeviceAttributeReadService.isReadable(meter, existingTouAttributes, user);

        // Add objects to mav.
        mav.addObject("meter", meter);
        mav.addObject("readable", readable);
        
        if (existingTouAttributes.size() > 0) {
            mav.addObject("touAttributesAvailable", true);
            for (Attribute touAttribute : existingTouAttributes) {
                mav.addObject(touAttribute.getKey(), touAttribute);
            }
        } else {
            mav.addObject("touAttributesAvailable", false);
        }
        
        return mav;
    }

    /**
     * This method gets the attributes that we are using and sends them to 
     * porter to fetch the most recent data of those attributes so the AttributeValue
     * tags can update their values.
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView read(HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        
        ModelAndView mav = new ModelAndView("common/meterReadingsResult.jsp");
        Meter meter = getMeter(request);
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        // Finds the existing attributes for the supplied meter
        Set<Attribute> allExistingAttributes = attributeService.getAllExistingAttributes(meter);
        Set<Attribute> existingTouAttributes =
            Sets.intersection(allExistingAttributes,AttributeHelper.getTouAttributes());

        // Reads all the meters in the existing set
        CommandResultHolder result = plcDeviceAttributeReadService.readMeter(meter, existingTouAttributes, DeviceRequestType.TOU_WIDGET_ATTRIBUTE_READ, user);

        mav.addObject("result", result);
        
        boolean readable = plcDeviceAttributeReadService.isReadable(meter, existingTouAttributes, user);
        mav.addObject("readable", readable);

        if (existingTouAttributes.size() > 0) {
            mav.addObject("touAttributesAvailable", true);
            for (Attribute touAttribute : existingTouAttributes) {
                mav.addObject(touAttribute.getKey(), touAttribute);
            }
        } else {
            mav.addObject("touAttributesAvailable", false);
        }

        return mav;
    }
    
    /**
     * @param request
     * @return
     * @throws ServletRequestBindingException
     */
    private Meter getMeter(HttpServletRequest request) throws ServletRequestBindingException {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        Meter meter = meterDao.getForId(deviceId);
        return meter;
    }

    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @Autowired
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }

    @Autowired
    public void setPlcDeviceAttributeReadService(PlcDeviceAttributeReadService plcDeviceAttributeReadService) {
        this.plcDeviceAttributeReadService = plcDeviceAttributeReadService;
    }

}

