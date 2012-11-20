package com.cannontech.web.widget;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.meter.service.impl.MeterEventLookupService;
import com.cannontech.amr.paoPointValue.model.MeterPointValue;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.util.Range;
import com.cannontech.core.service.PaoPointValueService;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class MeterEventsWidget extends AdvancedWidgetControllerBase {
    
    @Autowired MeterDao meterDao;
    @Autowired MeterEventLookupService meterEventLookupService;
    @Autowired PaoPointValueService paoPointValueService;

    private static Comparator<MeterPointValue> getDateComparator() {
        return MeterPointValue.getDateOrdering().compound(MeterPointValue.getPointNameOrdering().reverse());
    }

    @RequestMapping
    public String render(ModelMap model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        setupModel(deviceId, model, YukonUserContextUtils.getYukonUserContext(request));
        return "meterEventsWidget/render.jsp";
    }

    public void setupModel(int deviceId, ModelMap model, YukonUserContext userContext) {
        Meter meter = meterDao.getForId(deviceId);
        List<MeterPointValue> meterPointValues = getMeterPointValues(meter, userContext);
        model.addAttribute("valueMap", meterPointValues);
        model.addAttribute("deviceId", deviceId);
        model.addAttribute("meter", meter);
    }
    
    private List<MeterPointValue> getMeterPointValues(Meter meter, YukonUserContext userContext) {
        Set<Attribute> availableEventAttributes =
            meterEventLookupService.getAvailableEventAttributes(Collections.singletonList(meter));
        
        List<MeterPointValue> events = paoPointValueService.getMeterPointValues(Collections.singletonList(meter),
                                                                                availableEventAttributes,
                                                                                new Range<Instant>(),
                                                                                10,
                                                                                meter.isDisabled(),
                                                                                null,
                                                                                userContext);

        Collections.sort(events, Collections.reverseOrder(getDateComparator()));
        events = events.subList(0, 10 > events.size() ? events.size() : 10);

        return events;
    }
}