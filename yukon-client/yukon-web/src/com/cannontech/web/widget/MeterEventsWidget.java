package com.cannontech.web.widget;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.meter.service.impl.MeterEventLookupService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class MeterEventsWidget extends AdvancedWidgetControllerBase {
    
    @Autowired RawPointHistoryDao rawPointHistoryDao;
    @Autowired PaoDao paoDao;
    @Autowired MeterEventLookupService meterEventLookupService;
    @Autowired PointDao pointDao;

    public class EventHolder implements Comparable<EventHolder> {
        private PointValueQualityHolder pointValueQualityHolder;
        private String pointName;
        
        private EventHolder(PointValueQualityHolder pvqh, String name) {
            this.pointValueQualityHolder = pvqh;
            this.pointName = name;
        }

        public PointValueQualityHolder getPointValueQualityHolder() {
            return pointValueQualityHolder;
        }

        public String getPointName() {
            return pointName;
        }

        @Override
        public int compareTo(EventHolder o) {
            return pointValueQualityHolder.getPointDataTimeStamp().compareTo(o
                .getPointValueQualityHolder().getPointDataTimeStamp());
        }
    }

    @RequestMapping
    public String render(ModelMap model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        setupModel(deviceId, model);
        return "meterEventsWidget/render.jsp";
    }

    public void setupModel(int deviceId, ModelMap model) {
        YukonPao meter = paoDao.getYukonPao(deviceId);
        List<EventHolder> sortedLimitedAttributeValueMap = getRphSortedLimitedAttributeValueMap(meter);
        model.addAttribute("valueMap", sortedLimitedAttributeValueMap);
        model.addAttribute("deviceId", deviceId);
    }
    
    private List<EventHolder> getRphSortedLimitedAttributeValueMap(YukonPao meter) {
        Set<BuiltInAttribute> availableEventAttributes =
            meterEventLookupService.getAvailableEventAttributes(Collections.singletonList(meter));
        List<EventHolder> events = Lists.newArrayList();

        for (BuiltInAttribute attribute : availableEventAttributes) {
            ListMultimap<PaoIdentifier, PointValueQualityHolder> attributeData =
                rawPointHistoryDao.getLimitedAttributeData(Collections.singleton(meter),
                                                    attribute,
                                                    null,
                                                    null,
                                                    10,
                                                    false,
                                                    Clusivity.INCLUSIVE_INCLUSIVE,
                                                    Order.REVERSE);
            for (Entry<PaoIdentifier, PointValueQualityHolder> entry : attributeData.entries()) {
                String pointName = pointDao.getPointName(entry.getValue().getId());
                events.add(new EventHolder(entry.getValue(), pointName));
            }
        }

        Collections.sort(events, Collections.reverseOrder());
        events = events.subList(0, 10 > events.size() ? events.size() : 10);

        return events;
    }
}