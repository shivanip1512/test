package com.cannontech.web.widget;

import java.util.Map;

import com.cannontech.common.chart.model.AttributeGraphType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.web.widget.support.impl.CachingWidgetParameterGrabber;

public interface TrendWidgetDisplayParams {
    
    enum TrendWidgetTypeEnum {
        WATER_CSR_TREND_WIDGET,
        GAS_CSR_TREND_WIDGET,
        CSR_TREND_WIDGET;
    }
    
    CachingWidgetParameterGrabber getCachingWidgetParameterGrabber();
    
    Map<String, AttributeGraphType> getSupportedAttributeGraphMap();
    
    BuiltInAttribute getDefaultAttribute();
    
    TrendWidgetTypeEnum getType();

}
