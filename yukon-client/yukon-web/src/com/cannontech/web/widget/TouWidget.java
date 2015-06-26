package com.cannontech.web.widget;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.deviceread.service.DeviceReadResult;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeHelper;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetInput;

@Controller
@RequestMapping("/touWidget/*")
public class TouWidget extends AdvancedWidgetControllerBase {

    @Autowired private AttributeService attributeService;
    @Autowired private DeviceAttributeReadService deviceAttributeReadService;
    @Autowired private MeterDao meterDao;
    
    @Autowired
    public TouWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        
        Set<WidgetInput> simpleWidgetInputSet = new HashSet<WidgetInput>();
        simpleWidgetInputSet.add(simpleWidgetInput);
        
        this.setInputs(simpleWidgetInputSet);
        this.setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile("METERING"));
    }

    @RequestMapping("render")
    public String render(ModelMap model, YukonUserContext userContext, Integer deviceId){
        
    	YukonMeter meter = meterDao.getForId(deviceId);
        Set<Attribute> existingTouAttributes = attributeService.getExistingAttributes(meter, AttributeHelper.getTouWidgetAttributes());
        initModel(model, existingTouAttributes, userContext, meter);
      
        return "touWidget/render.jsp";
    }

    @RequestMapping("read")
    public String read(ModelMap model, YukonUserContext userContext, Integer deviceId){

    	YukonMeter meter = meterDao.getForId(deviceId);
        Set<Attribute> attributes = attributeService.getExistingAttributes(meter, AttributeHelper.getTouWidgetAttributes());
        initModel(model, attributes, userContext, meter);
        
		DeviceReadResult result = deviceAttributeReadService.initiateReadAndWait(meter, attributes, DeviceRequestType.TOU_WIDGET_ATTRIBUTE_READ, userContext.getYukonUser());
		model.put("result", result);

        return "common/deviceAttributeReadResult.jsp";
    }
    
	private void initModel(ModelMap model, Set<Attribute> attributes, YukonUserContext userContext, YukonMeter meter) {
		if (attributes.size() > 0) {
			model.put("touAttributesAvailable", true);
			for (Attribute touAttribute : attributes) {
				model.put(touAttribute.getKey(), touAttribute);
			}
		} else {
			model.put("touAttributesAvailable", false);
		}

		boolean readable = deviceAttributeReadService.isReadable(Collections.singleton(meter), attributes, userContext.getYukonUser());
		model.put("readable", readable);
		model.put("meter", meter);
	}

}