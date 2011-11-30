package com.cannontech.web.widget;

import java.util.Collections;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.rfn.dao.RfnMeterDao;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;
import com.google.common.collect.ListMultimap;

public class RfnMeterEventsWidget extends AdvancedWidgetControllerBase {
    
    @Autowired RawPointHistoryDao rawPointHistoryDao;
    @Autowired RfnMeterDao rfnMeterDao;
    
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
        return "rfnMeterEventsWidget/render.jsp";
    }

    public void setupModel(int deviceId, ModelMap model) {
        RfnMeter meter = rfnMeterDao.getForId(deviceId);
        
        SortedMap<PointValueQualityHolder, BuiltInAttribute> sortedLimitedAttributeValueMap =
            getRphSortedLimitedAttributeValueMap(meter);
        model.addAttribute("valueMap", sortedLimitedAttributeValueMap);
        model.addAttribute("deviceId", deviceId);
    }
    
    private SortedMap<PointValueQualityHolder, BuiltInAttribute> getRphSortedLimitedAttributeValueMap(YukonPao meter) {
        SortedMap<PointValueQualityHolder, BuiltInAttribute> sortedAttributeValueMap =
            new TreeMap<PointValueQualityHolder, BuiltInAttribute>(comparator);

        for (BuiltInAttribute attribute : BuiltInAttribute.getRfnEventStatusTypes()) {
            ListMultimap<PaoIdentifier, PointValueQualityHolder> attributeData =
                rawPointHistoryDao.getLimitedAttributeData(Collections.singleton(meter),
                                                    attribute,
                                                    null,
                                                    null,
                                                    10,
                                                    false,
                                                    Clusivity.INCLUSIVE_INCLUSIVE,
                                                    Order.FORWARD);
            for (Entry<PaoIdentifier, PointValueQualityHolder> entry : attributeData.entries()) {
                sortedAttributeValueMap.put(entry.getValue(), attribute);
            }
        }
        
        SortedMap<PointValueQualityHolder, BuiltInAttribute> limitedSortedAttributeValueMap =
            getLimitedSortedAttributeValueMap(sortedAttributeValueMap);
        return limitedSortedAttributeValueMap;
    }
    
    private SortedMap<PointValueQualityHolder, BuiltInAttribute> getLimitedSortedAttributeValueMap(SortedMap<PointValueQualityHolder, BuiltInAttribute> sortedAttributeValueMap) {
        int keyCounter = 0;
        SortedMap<PointValueQualityHolder, BuiltInAttribute> limitedSortedAttributeValueMap =
            new TreeMap<PointValueQualityHolder, BuiltInAttribute>(comparator);
        for (Entry<PointValueQualityHolder, BuiltInAttribute> entry : sortedAttributeValueMap.entrySet()) {
            if (keyCounter == 10) {
                break;
            }
            limitedSortedAttributeValueMap.put(entry.getKey(), entry.getValue());
            keyCounter++;
        }
        
        return limitedSortedAttributeValueMap;
    }
}