package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.cannontech.common.chart.model.AttributeGraphType;
import com.cannontech.common.chart.model.ConverterType;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.web.widget.support.impl.CachingWidgetParameterGrabber;

@Component
public class CsrTrendWidgetDisplayParams implements TrendWidgetDisplayParams {
    
    @Autowired
    @Qualifier("csrTrendWidgetCachingWidgetParameterGrabber")
    private CachingWidgetParameterGrabber cachingWidgetParameterGrabber;
    
    private Map<String, AttributeGraphType> supportedAttributeGraphMap = new LinkedHashMap<>();
    
    @PostConstruct
    public void init () {
        List<BuiltInAttribute> buildInAttributes = new ArrayList<BuiltInAttribute>();
        buildInAttributes.add(BuiltInAttribute.SUM_KWH_PER_INTERVAL);
        buildInAttributes.add(BuiltInAttribute.SUM_KW_LOAD_PROFILE);
        buildInAttributes.add(BuiltInAttribute.DELIVERED_KWH_PER_INTERVAL);
        buildInAttributes.add(BuiltInAttribute.DELIVERED_KW_LOAD_PROFILE);
        buildInAttributes.add(BuiltInAttribute.LOAD_PROFILE);
        buildInAttributes.add(BuiltInAttribute.VOLTAGE_PROFILE);
        buildInAttributes.add(BuiltInAttribute.DEMAND);
        buildInAttributes.add(BuiltInAttribute.USAGE);

        for (BuiltInAttribute attribute : buildInAttributes) {
            AttributeGraphType attributeGraph = new AttributeGraphType();
            attributeGraph.setAttribute(attribute);
            attributeGraph.setGraphType((attribute == BuiltInAttribute.USAGE) ? GraphType.COLUMN
                    : GraphType.LINE);
            attributeGraph.setConverterType((attribute == BuiltInAttribute.USAGE)
                    ? ConverterType.NORMALIZED_DELTA : ConverterType.RAW);
            this.supportedAttributeGraphMap.put(attributeGraph.getLabel(), attributeGraph);
        }
    }

    @Override
    public CachingWidgetParameterGrabber getCachingWidgetParameterGrabber() {
        return cachingWidgetParameterGrabber;
    }

    @Override
    public BuiltInAttribute getDefaultAttribute() {
        return BuiltInAttribute.USAGE;
    }

    @Override
    public TrendWidgetTypeEnum getType() {
        return TrendWidgetTypeEnum.CSR_TREND_WIDGET;
    }

    @Override
    public Map<String, AttributeGraphType> getSupportedAttributeGraphMap() {
        return this.supportedAttributeGraphMap;
    }

}
