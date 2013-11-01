package com.cannontech.web.widget;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.model.PreviousReadings;
import com.cannontech.common.events.loggers.MeteringEventLogService;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.common.pao.service.LiteYukonPoint;
import com.cannontech.web.common.pao.service.YukonPointHelper;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.google.common.collect.Sets;

/**
 * Widget used to display basic device information
 */
public class MeterReadingsWidget extends WidgetControllerBase {

    @Autowired private AttributeReadingHelper widgetHelper;
    @Autowired private DeviceAttributeReadService deviceAttributeReadService;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private AttributeService attributeService;
    @Autowired private MeteringEventLogService meteringEventLogService;
    @Autowired private PointService pointService;
    @Autowired private YukonPointHelper yukonPointHelper;
    
    private List<? extends Attribute> attributesToShow;
    private Attribute previousReadingsAttributeToShow;
    
    public void setAttributesToShow(List<BuiltInAttribute> attributesToShow) {
        // this setter accepts the enum to make Spring happy
        this.attributesToShow = attributesToShow;
    }
    public void setPreviousReadingsAttributeToShow(BuiltInAttribute previousReadingsAttributeToShow) {
        // this setter accepts the enum to make Spring happy
        this.previousReadingsAttributeToShow = previousReadingsAttributeToShow;
    }
    
    @Override
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response)
            throws ServletRequestBindingException {

        YukonMeter meter = widgetHelper.getMeter(request);
        ModelAndView mav = new ModelAndView("meterReadingsWidget/render.jsp");
        mav.addObject("device", meter);
        mav.addObject("attributes", attributesToShow);
        mav.addObject("previousReadingsAttribute", previousReadingsAttributeToShow);
        
        Set<Attribute> allSupportedAttributes = attributeService.getAvailableAttributes(meter);
        Map<Attribute, Boolean> supportedAttributes = ServletUtil.convertSetToMap(allSupportedAttributes);
        mav.addObject("supportedAttributes", supportedAttributes);
        Set<Attribute> allExistingAttributes = 
                attributeService.getExistingAttributes(meter, Sets.newHashSet(attributesToShow));
        Map<Attribute, Boolean> existingAttributes = ServletUtil.convertSetToMap(allExistingAttributes);
        mav.addObject("existingAttributes", existingAttributes);
        
        // don't attempt unless USAGE is supported and exists
        boolean usageAttributeExists = existingAttributes.containsKey(previousReadingsAttributeToShow);
        if (usageAttributeExists) {
	        LitePoint lp = attributeService.getPointForAttribute(meter, previousReadingsAttributeToShow);
	        PreviousReadings previousReadings = pointService.getPreviousReadings(lp);
	        
	        mav.addObject("previousReadings_All", previousReadings.getPrevious36());
	        mav.addObject("previousReadings_Daily", previousReadings.getPrevious3Months());
	        mav.addObject("previousReadings_CutoffDate", previousReadings.getCutoffDate());
	        mav.addObject("previousReadings_Cutoff", !previousReadings.getPrevious3Months().isEmpty());
	        mav.addObject("previousReadings_OptionValue", "VALUE");
        }
        mav.addObject("usageAttributeExists", usageAttributeExists);

        LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        boolean readable = deviceAttributeReadService.isReadable(Collections.singleton(meter), allExistingAttributes, user);
        mav.addObject("readable", readable);
        
        List<LiteYukonPoint> points = yukonPointHelper.getYukonPoints(meter, null, null);
        mav.addObject("points", points);
        mav.addObject("deviceName", paoLoadingService.getDisplayablePao(meter).getName());
        
        return mav;
    }
    
    public ModelAndView read(HttpServletRequest request, HttpServletResponse response)
    throws ServletRequestBindingException {
        
        YukonMeter meter = widgetHelper.getMeter(request);
        Set<Attribute> allExistingAttributes = 
                attributeService.getExistingAttributes(meter, Sets.newHashSet(attributesToShow));
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        meteringEventLogService.readNowPushedForReadingsWidget(user, meter.getDeviceId());
        
        ModelAndView mav = new ModelAndView("common/deviceAttributeReadResult.jsp");
        widgetHelper.initiateRead(request, meter,allExistingAttributes, mav.getModelMap(), 
                                 DeviceRequestType.METER_READINGS_WIDGET_ATTRIBUTE_READ);

        return mav;
    }

}