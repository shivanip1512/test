package com.cannontech.web.widget;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.deviceread.dao.MeterReadService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.model.PreviousReadings;
import com.cannontech.common.device.service.PointService;
import com.cannontech.common.events.loggers.MeteringEventLogService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display basic device information
 */
public class MeterReadingsWidget extends WidgetControllerBase {

    private MeterDao meterDao;
    private MeterReadService meterReadService;
    private AttributeService attributeService;
    private MeteringEventLogService meteringEventLogService;
    private PointService pointService;
    private List<? extends Attribute> attributesToShow;
    
    public void setAttributesToShow(List<BuiltInAttribute> attributesToShow) {
        // this setter accepts the enum to make Spring happy
        this.attributesToShow = attributesToShow;
    }
    
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        Meter meter = getMeter(request);
        ModelAndView mav = new ModelAndView("meterReadingsWidget/render.jsp");
        mav.addObject("device", meter);
        mav.addObject("attributes", attributesToShow);
        Set<Attribute> allSupportedAttributes = attributeService.getAvailableAttributes(meter);
        Map<Attribute, Boolean> supportedAttributes = ServletUtil.convertSetToMap(allSupportedAttributes);
        mav.addObject("supportedAttributes", supportedAttributes);
        Set<Attribute> allExistingAttributes = attributeService.getAllExistingAttributes(meter);
        Map<Attribute, Boolean> existingAttributes = ServletUtil.convertSetToMap(allExistingAttributes);
        mav.addObject("existingAttributes", existingAttributes);
        
        // don't attempt unless USAGE is supported and exists
        boolean usageAttributeExists = existingAttributes.containsKey(BuiltInAttribute.USAGE);
        if (usageAttributeExists) {
	        LitePoint lp = attributeService.getPointForAttribute(meter, BuiltInAttribute.USAGE);
	        PreviousReadings previousReadings = pointService.fillInPreviousReadings(lp);
	        
	        mav.addObject("previousReadings_All", previousReadings.getPrevious36());
	        mav.addObject("previousReadings_Daily", previousReadings.getPrevious3Months());
	        mav.addObject("previousReadings_CutoffDate", previousReadings.getCutoffDate());
	        mav.addObject("previousReadings_Cutoff", !previousReadings.getPrevious3Months().isEmpty());
	        mav.addObject("previousReadings_OptionValue", "VALUE");
        }
        mav.addObject("usageAttributeExists", usageAttributeExists);
        
        allExistingAttributes.retainAll(attributesToShow);
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        boolean readable = meterReadService.isReadable(meter, allExistingAttributes, user);
        mav.addObject("readable", readable);
        
        return mav;
    }
    
    public ModelAndView read(HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        
        Meter meter = getMeter(request);
        ModelAndView mav = new ModelAndView("common/meterReadingsResult.jsp");
        
        Set<Attribute> allExistingAttributes = attributeService.getAllExistingAttributes(meter);
        
        // allExisting is a copy...
        allExistingAttributes.retainAll(attributesToShow);
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        meteringEventLogService.readNowPushedForReadingsWidget(user, meter.getDeviceId());
        CommandResultHolder result = meterReadService.readMeter(meter, allExistingAttributes, CommandRequestExecutionType.METER_READINGS_WIDGET_ATTRIBUTE_READ, user);
        
        mav.addObject("result", result);
        
        boolean readable = meterReadService.isReadable(meter, allExistingAttributes, user);
        mav.addObject("readable", readable);

        return mav;
    }

    private Meter getMeter(HttpServletRequest request) throws ServletRequestBindingException {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        Meter meter = meterDao.getForId(deviceId);
        return meter;
    }

    @Autowired
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
    
    @Autowired
    public void setMeterReadService(MeterReadService meterReadService) {
        this.meterReadService = meterReadService;
    }

    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
	
    @Autowired
    public void setMeteringEventLogService(
            MeteringEventLogService meteringEventLogService) {
        this.meteringEventLogService = meteringEventLogService;
    }
    
    @Autowired
    public void setPointService(PointService pointService) {
        this.pointService = pointService;
    }
}