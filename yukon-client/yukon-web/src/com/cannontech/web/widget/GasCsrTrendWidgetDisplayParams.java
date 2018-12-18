package com.cannontech.web.widget;

import java.util.LinkedHashMap;
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
public class GasCsrTrendWidgetDisplayParams implements TrendWidgetDisplayParams {
    
    @Autowired
    @Qualifier("gasCsrTrendWidgetCachingWidgetParameterGrabber")
    private CachingWidgetParameterGrabber cachingWidgetParameterGrabber;
    private Map<String, AttributeGraphType> supportedAttributeGraphMap;
    
    @PostConstruct
    public void init () {
        this.supportedAttributeGraphMap = new LinkedHashMap<>();
        AttributeGraphType attributeGraph = new AttributeGraphType();
        attributeGraph.setAttribute(BuiltInAttribute.USAGE_GAS);
        attributeGraph.setGraphType(GraphType.COLUMN);
        //DELTA_WATER converter behaves similarly as to what a DELTA_GAS Converter would do.
        //This is an intentional line.
        attributeGraph.setConverterType(ConverterType.DELTA_WATER);
        this.supportedAttributeGraphMap.put(attributeGraph.getLabel(), attributeGraph);
    }

    @Override
    public CachingWidgetParameterGrabber getCachingWidgetParameterGrabber() {
        return cachingWidgetParameterGrabber;
    }

    @Override
    public BuiltInAttribute getDefaultAttribute() {
        return BuiltInAttribute.USAGE_GAS;
    }

    @Override
    public TrendWidgetTypeEnum getType() {
        return TrendWidgetTypeEnum.GAS_CSR_TREND_WIDGET;
    }

    @Override
    public Map<String, AttributeGraphType> getSupportedAttributeGraphMap() {
        return this.supportedAttributeGraphMap;
    }
}
