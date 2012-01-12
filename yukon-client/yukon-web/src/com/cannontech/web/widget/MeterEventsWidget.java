package com.cannontech.web.widget;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.meter.service.impl.MeterEventLookupService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.MapUtil;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;
import com.google.common.collect.ListMultimap;

public class MeterEventsWidget extends AdvancedWidgetControllerBase {
    
    @Autowired RawPointHistoryDao rawPointHistoryDao;
    @Autowired PaoDao paoDao;
    @Autowired MeterEventLookupService meterEventLookupService;
    @Autowired PointDao pointDao;
    
    private Comparator<PointValueQualityHolder> comparator =
        new Comparator<PointValueQualityHolder>() {
            public int compare(PointValueQualityHolder o1, PointValueQualityHolder o2) {
                return o2.getPointDataTimeStamp().compareTo(o1.getPointDataTimeStamp());
            }
        };

    @RequestMapping
    public String render(ModelMap model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        setupModel(deviceId, model);
        return "meterEventsWidget/render.jsp";
    }

    public void setupModel(int deviceId, ModelMap model) {
        YukonPao meter = paoDao.getYukonPao(deviceId);
        
        SortedMap<PointValueQualityHolder, String> sortedLimitedAttributeValueMap =
            getRphSortedLimitedAttributeValueMap(meter);
        model.addAttribute("valueMap", sortedLimitedAttributeValueMap);
        model.addAttribute("deviceId", deviceId);
    }
    
    private SortedMap<PointValueQualityHolder, String> getRphSortedLimitedAttributeValueMap(YukonPao meter) {
        SortedMap<PointValueQualityHolder, String> sortedPointValueMap =
            new TreeMap<PointValueQualityHolder, String>(comparator);

        Set<BuiltInAttribute> availableEventAttributes =
            meterEventLookupService.getAvailableEventAttributes(Collections.singletonList(meter));

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
                sortedPointValueMap.put(entry.getValue(), pointName);
            }
        }
        
        SortedMap<PointValueQualityHolder, String> limitedSortedValueToPointNameMap =
                MapUtil.putFirstEntries(10, sortedPointValueMap, new TreeMap<PointValueQualityHolder, String>(comparator));

        return limitedSortedValueToPointNameMap;
    }
}