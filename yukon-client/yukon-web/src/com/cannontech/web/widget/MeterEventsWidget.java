package com.cannontech.web.widget;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.meter.service.impl.MeterEventLookupService;
import com.cannontech.amr.paoPointValue.model.PaoPointValue;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.core.service.PaoPointValueService;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;

public class MeterEventsWidget extends AdvancedWidgetControllerBase {
    
    @Autowired MeterDao meterDao;
    @Autowired MeterEventLookupService meterEventLookupService;
    @Autowired PaoPointValueService paoPointValueService;

    private static Comparator<PaoPointValue> getDateComparator() {
        Ordering<PaoPointValue> dateOrdering = Ordering.natural().nullsLast()
                .onResultOf(new Function<PaoPointValue, Date>() {
                    @Override
                    public Date apply(PaoPointValue from) {
                        return from.getPointValueHolder().getPointDataTimeStamp();
                    }
                });
        Ordering<PaoPointValue> attributeOrdering = Ordering.natural().nullsLast()
                .onResultOf(new Function<PaoPointValue, String>() {
                    @Override
                    public String apply(PaoPointValue from) {
                        return from.getPointName();
                    }
                });
        Ordering<PaoPointValue> result = dateOrdering.compound(attributeOrdering.reverse());
        return result;
    }

    @RequestMapping
    public String render(ModelMap model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        setupModel(deviceId, model);
        return "meterEventsWidget/render.jsp";
    }

    public void setupModel(int deviceId, ModelMap model) {
        Meter meter = meterDao.getForId(deviceId);
        List<PaoPointValue> sortedLimitedAttributeValueMap = getRphSortedLimitedAttributeValueMap(meter);
        model.addAttribute("valueMap", sortedLimitedAttributeValueMap);
        model.addAttribute("deviceId", deviceId);
        model.addAttribute("meter", meter);
    }
    
    private List<PaoPointValue> getRphSortedLimitedAttributeValueMap(Meter meter) {
        Set<Attribute> availableEventAttributes =
            meterEventLookupService.getAvailableEventAttributes(Collections.singletonList(meter));
        
        List<PaoPointValue> events = paoPointValueService.getPaoPointValuesForMeters(Collections.singletonList(meter),
                                                        availableEventAttributes,
                                                        null,
                                                        null,
                                                        10,
                                                        meter.isDisabled(),
                                                        null,
                                                        null);

        Collections.sort(events, Collections.reverseOrder(getDateComparator()));
        events = events.subList(0, 10 > events.size() ? events.size() : 10);

        return events;
    }
}