package com.cannontech.web.widget;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.Instant;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.meter.service.impl.MeterEventLookupService;
import com.cannontech.amr.paoPointValue.model.MeterPointValue;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.Range;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.PaoPointValueService;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@Controller
@RequestMapping("/meterEventsWidget/*")
public class MeterEventsWidget extends AdvancedWidgetControllerBase {
    
    @Autowired private MeterDao meterDao;
    @Autowired private MeterEventLookupService meterEventLookupService;
    @Autowired private PaoPointValueService paoPointValueService;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    
    @Autowired
    public MeterEventsWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        
        String checkRole = YukonRole.METERING.name();
        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
        setLazyLoad(true);
        setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile(checkRole));
    }

    private static Comparator<MeterPointValue> getDateComparator() {
        return MeterPointValue.getDateOrdering().compound(MeterPointValue.getPointNameOrdering().reverse());
    }

    @RequestMapping("render")
    public String render(ModelMap model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        setupModel(deviceId, model, YukonUserContextUtils.getYukonUserContext(request));
        return "meterEventsWidget/render.jsp";
    }

    public void setupModel(int deviceId, ModelMap model, YukonUserContext userContext) {
        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        boolean isDisabled = true;
        if (device.getPaoIdentifier().getPaoType().isRfMeter()) {
            YukonMeter meter = meterDao.getForId(deviceId);
            isDisabled = meter.isDisabled();
        } else if (device.getPaoIdentifier().getPaoType().isRfRelay()) {
            isDisabled = false;
        }
        model.addAttribute("isDisabled", isDisabled);
        
        List<MeterPointValue> meterPointValues = getMeterPointValues(device, userContext, isDisabled);
 
        model.addAttribute("valueMap", meterPointValues);
        model.addAttribute("deviceId", deviceId);
        Instant now = new Instant();
        Instant defaultStartInstant = now.minus(Period.days(30).toDurationTo(now));
        model.addAttribute("defaultStartInstant", defaultStartInstant);
    }
   
    private List<MeterPointValue> getMeterPointValues(RfnDevice device, YukonUserContext userContext, boolean isDisabled) {
        Set<Attribute> availableEventAttributes =
            meterEventLookupService.getAvailableEventAttributes(Collections.singletonList(device));
        
        Range<Instant> range = Range.unbounded();
        List<MeterPointValue> events = paoPointValueService.getMeterPointValues(Collections.singletonList(device),
                                                                                availableEventAttributes,
                                                                                range,
                                                                                10,
                                                                                isDisabled,
                                                                                null,
                                                                                userContext);

        Collections.sort(events, Collections.reverseOrder(getDateComparator()));
        events = events.subList(0, 10 > events.size() ? events.size() : 10);

        return events;
    }
}