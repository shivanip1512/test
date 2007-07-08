package com.cannontech.web.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.deviceread.dao.MeterReadService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display basic device information
 */
public class MeterReadingsWidget extends WidgetControllerBase {

    private MeterDao meterDao;
    private MeterReadService meterReadService;
    private AttributeService attributeService;
    private List<? extends Attribute> attributesToShow;

    public void setMeterReadService(MeterReadService meterReadService) {
        this.meterReadService = meterReadService;
    }
    
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
        Set<Attribute> allSupportedAtributes = attributeService.getAvailableAttributes(meter);
        Map<Attribute, Boolean> supportedAttributes = convertSetToMap(allSupportedAtributes);
        mav.addObject("supportedAttributes", supportedAttributes);
        Set<Attribute> allExistingAtributes = attributeService.getAllExistingAtributes(meter);
        Map<Attribute, Boolean> existingAttributes = convertSetToMap(allExistingAtributes);
        mav.addObject("existingAttributes", existingAttributes);
        
        return mav;
    }

    public ModelAndView read(HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        Meter meter = getMeter(request);
        ModelAndView mav = new ModelAndView("common/meterReadingsResult.jsp");
        
        Set<Attribute> allExistingAtributes = attributeService.getAllExistingAtributes(meter);
        
        // allExisting is a copy...
        allExistingAtributes.retainAll(attributesToShow);
        CommandResultHolder result = meterReadService.readMeter(meter, allExistingAtributes);
        
        mav.addObject("errorsExist", result.isErrorsExist());
        
        mav.addObject("result", result);
        
        return mav;
    }

    private Meter getMeter(HttpServletRequest request) throws ServletRequestBindingException {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        Meter meter = meterDao.getForId(deviceId);
        return meter;
    }
    
    private Map<Attribute, Boolean> convertSetToMap(Set<Attribute> allExistingAtributes) {
        Map<Attribute, Boolean> existingMap = new HashMap<Attribute, Boolean>();
        
        // convert to a map of true's because JSP EL can use this to check "contains"
        for (Attribute attribute : allExistingAtributes) {
            existingMap.put(attribute, Boolean.TRUE);
        }
        
        return existingMap;
    }
    
    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
    
    @Required
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
}
