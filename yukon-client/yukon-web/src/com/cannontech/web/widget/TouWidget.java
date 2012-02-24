package com.cannontech.web.widget;

import java.util.Collections;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeHelper;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.google.common.collect.Sets;

public class TouWidget extends WidgetControllerBase {

    @Autowired private AttributeService attributeService;
    @Autowired private DeviceAttributeReadService deviceAttributeReadService;
    @Autowired private AttributeReadingWidgetHelper widgetHelper;

    public ModelAndView render(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("touWidget/render.jsp");
        Meter meter = widgetHelper.getMeter(request);
        LiteYukonUser user = ServletUtil.getYukonUser(request);

        // Finds the existing attributes for the supplied meter
        Set<Attribute> allExistingAttributes = attributeService.getAllExistingAttributes(meter);
        Set<Attribute> existingTouAttributes =
            Sets.intersection(allExistingAttributes,AttributeHelper.getTouAttributes());

        boolean readable = deviceAttributeReadService.isReadable(Collections.singleton(meter), existingTouAttributes, user);

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

    public ModelAndView read(HttpServletRequest request, HttpServletResponse response)
    throws ServletRequestBindingException {
        Meter meter = widgetHelper.getMeter(request);
        
        // Finds the existing attributes for the supplied meter
        Set<Attribute> allExistingAttributes = attributeService.getAllExistingAttributes(meter);
        Set<Attribute> existingTouAttributes = Sets.intersection(allExistingAttributes, AttributeHelper.getTouAttributes());
        
        ModelAndView mav = widgetHelper.initiateRead(request, 
                                 meter, 
                                 existingTouAttributes, 
                                 "common/deviceAttributeReadResult.jsp", 
                                 DeviceRequestType.TOU_WIDGET_ATTRIBUTE_READ);
        
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

}