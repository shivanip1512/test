package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.deviceread.service.DeviceReadResult;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.model.PreviousReadings;
import com.cannontech.common.events.loggers.MeteringEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.common.pao.service.LiteYukonPoint;
import com.cannontech.web.common.pao.service.YukonPointHelper;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.google.common.collect.Sets;

/**
 * Widget used to display basic device information
 */
@Controller
@RequestMapping("/meterReadingsWidget/*")
public class MeterReadingsWidget extends AdvancedWidgetControllerBase {

    @Autowired private DeviceAttributeReadService deviceAttributeReadService;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private AttributeService attributeService;
    @Autowired private MeteringEventLogService meteringEventLogService;
    @Autowired private PointService pointService;
    @Autowired private YukonPointHelper yukonPointHelper;
    @Autowired private MeterDao meterDao;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    
    private List<? extends Attribute> attributesToShow;
    private Attribute previousReadingsAttributeToShow;
    
    public MeterReadingsWidget() {
    }
    
    @Autowired
    public MeterReadingsWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        
        SimpleWidgetInput simpleWidget = new SimpleWidgetInput();
        simpleWidget.setName("startOffset");
        simpleWidget.setDescription("the number of prior days of point history to display");
        simpleWidget.setRequired(false);
        
        addInput(simpleWidgetInput);
        addInput(simpleWidget);
       
        List<BuiltInAttribute> attibutes = new ArrayList<BuiltInAttribute>();
        attibutes.add(BuiltInAttribute.USAGE);
        attibutes.add(BuiltInAttribute.PEAK_DEMAND);
        attibutes.add(BuiltInAttribute.DEMAND);
        attibutes.add(BuiltInAttribute.VOLTAGE);
        
        String checkRole = YukonRole.METERING.name();
        setIdentityPath("common/deviceIdentity.jsp");
        setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile(checkRole));
        setAttributesToShow(attibutes);
        setPreviousReadingsAttributeToShow(BuiltInAttribute.USAGE);
    }
    
    public void setAttributesToShow(List<BuiltInAttribute> attributesToShow) {
        // this setter accepts the enum to make Spring happy
        this.attributesToShow = attributesToShow;
    }
    public void setPreviousReadingsAttributeToShow(BuiltInAttribute previousReadingsAttributeToShow) {
        // this setter accepts the enum to make Spring happy
        this.previousReadingsAttributeToShow = previousReadingsAttributeToShow;
    }
    
    @RequestMapping("render")
    public String render(ModelMap model, YukonUserContext userContext, Integer deviceId) {

        YukonMeter meter = meterDao.getForId(deviceId);
        model.addAttribute("device", meter);
        model.addAttribute("attributes", attributesToShow);
        model.addAttribute("previousReadingsAttribute", previousReadingsAttributeToShow);
        
        Set<Attribute> allSupportedAttributes = attributeService.getAvailableAttributes(meter);
        Map<Attribute, Boolean> supportedAttributes = ServletUtil.convertSetToMap(allSupportedAttributes);
        model.addAttribute("supportedAttributes", supportedAttributes);
        Set<Attribute> allExistingAttributes = 
                attributeService.getExistingAttributes(meter, Sets.newHashSet(attributesToShow));
        Map<Attribute, Boolean> existingAttributes = ServletUtil.convertSetToMap(allExistingAttributes);
        model.addAttribute("existingAttributes", existingAttributes);
        
        // don't attempt unless USAGE is supported and exists
        boolean usageAttributeExists = existingAttributes.containsKey(previousReadingsAttributeToShow);
        if (usageAttributeExists) {
	        LitePoint lp = attributeService.getPointForAttribute(meter, previousReadingsAttributeToShow);
	        PreviousReadings previousReadings = pointService.getPreviousReadings(lp);
	        
	        model.addAttribute("previousReadings_All", previousReadings.getPrevious36());
	        model.addAttribute("previousReadings_Daily", previousReadings.getPrevious3Months());
	        model.addAttribute("previousReadings_CutoffDate", previousReadings.getCutoffDate());
	        model.addAttribute("previousReadings_Cutoff", !previousReadings.getPrevious3Months().isEmpty());
	        model.addAttribute("previousReadings_OptionValue", "VALUE");
        }
        model.addAttribute("usageAttributeExists", usageAttributeExists);
        
		boolean readable = deviceAttributeReadService.isReadable(Collections.singleton(meter), allExistingAttributes,
				userContext.getYukonUser());
        model.addAttribute("readable", readable);
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        List<LiteYukonPoint> points = yukonPointHelper.getYukonPoints(meter, accessor);
        model.addAttribute("points", points);
        model.addAttribute("deviceName", paoLoadingService.getDisplayablePao(meter).getName());
        
        return "meterReadingsWidget/render.jsp";
    }
    
    @RequestMapping(value = "read", method = RequestMethod.POST)
	public String read(ModelMap model, YukonUserContext userContext, Integer deviceId) {

		YukonMeter meter = meterDao.getForId(deviceId);
		Set<Attribute> attributes = attributeService.getExistingAttributes(meter, Sets.newHashSet(attributesToShow));

		meteringEventLogService.readNowPushedForReadingsWidget(userContext.getYukonUser(), meter.getMeterNumber());

		DeviceReadResult result = deviceAttributeReadService.initiateReadAndWait(meter, attributes,
				DeviceRequestType.METER_READINGS_WIDGET_ATTRIBUTE_READ, userContext.getYukonUser());

		model.addAttribute("result", result);

		return "common/deviceAttributeReadResult.jsp";
	}

}