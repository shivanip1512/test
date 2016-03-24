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
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.Range;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.PaoPointValueService;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetParameterHelper;
import com.google.common.collect.Sets;

@Controller
@RequestMapping("/meterEventsWidget/*")
public class MeterEventsWidget extends AdvancedWidgetControllerBase {
    
    @Autowired private MeterDao meterDao;
    @Autowired private MeterEventLookupService meterEventLookupService;
    @Autowired private PaoPointValueService paoPointValueService;
    
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
        YukonMeter meter = meterDao.getForId(deviceId);
        List<MeterPointValue> meterPointValues = getMeterPointValues(meter, userContext);
        model.addAttribute("valueMap", meterPointValues);
        model.addAttribute("deviceId", deviceId);
        Instant now = new Instant();
        Instant defaultStartInstant = now.minus(Period.days(30).toDurationTo(now));
        model.addAttribute("defaultStartInstant", defaultStartInstant);
        model.addAttribute("meter", meter);
    }
    
    private List<MeterPointValue> getMeterPointValues(YukonMeter meter, YukonUserContext userContext) {
        Set<Attribute> availableEventAttributes =
            meterEventLookupService.getAvailableEventAttributes(Collections.singletonList(meter));
        availableEventAttributes = Sets.difference(availableEventAttributes, BuiltInAttribute.getRfnEventAnalogTypes());
        
        Range<Instant> range = Range.unbounded();
        List<MeterPointValue> events = paoPointValueService.getMeterPointValues(Collections.singletonList(meter),
                                                                                availableEventAttributes,
                                                                                range,
                                                                                10,
                                                                                meter.isDisabled(),
                                                                                null,
                                                                                userContext);

        Collections.sort(events, Collections.reverseOrder(getDateComparator()));
        events = events.subList(0, 10 > events.size() ? events.size() : 10);

        return events;
    }
}