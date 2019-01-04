package com.cannontech.web.widget;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.common.chart.model.AttributeGraphType;
import com.cannontech.common.chart.model.ConverterType;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum CsrTrendWidgetType {

    ELECTRIC_CSR_TREND("electricCsrTrendWidgetCachingWidgetParameterGrabber", BuiltInAttribute.USAGE, BuiltInAttribute.SUM_KWH_PER_INTERVAL,
                     BuiltInAttribute.SUM_KW_LOAD_PROFILE, BuiltInAttribute.DELIVERED_KWH_PER_INTERVAL,
                     BuiltInAttribute.DELIVERED_KW_LOAD_PROFILE, BuiltInAttribute.LOAD_PROFILE,
                     BuiltInAttribute.VOLTAGE_PROFILE, BuiltInAttribute.DEMAND, BuiltInAttribute.USAGE),
    GAS_CSR_TREND("gasCsrTrendWidgetCachingWidgetParameterGrabber", BuiltInAttribute.USAGE_GAS, BuiltInAttribute.USAGE_GAS),
    WATER_CSR_TREND("waterCsrTrendWidgetCachingWidgetParameterGrabber", BuiltInAttribute.USAGE_WATER, BuiltInAttribute.USAGE_WATER);
    
    private static final Map<CsrTrendWidgetType, Map<String, AttributeGraphType>> supportedAttributeGraphMap;

    static {
        Map<String, AttributeGraphType> csrSupportedAttributeGraphMap = new HashMap<>();
        for (BuiltInAttribute attribute : ELECTRIC_CSR_TREND.getBuiltInAttributes()) {
            AttributeGraphType attributeGraph = new AttributeGraphType();
            attributeGraph.setAttribute(attribute);
            attributeGraph.setGraphType((attribute == BuiltInAttribute.USAGE) ? GraphType.COLUMN : GraphType.LINE);
            attributeGraph.setConverterType(
                (attribute == BuiltInAttribute.USAGE) ? ConverterType.NORMALIZED_DELTA : ConverterType.RAW);
            csrSupportedAttributeGraphMap.put(attributeGraph.getLabel(), attributeGraph);
        }
        Map<String, AttributeGraphType> gasCsrSupportedAttributeGraphMap = new HashMap<>();
        for (BuiltInAttribute attribute : GAS_CSR_TREND.getBuiltInAttributes()) {
            AttributeGraphType gasAttributeGraph = new AttributeGraphType();
            gasAttributeGraph.setAttribute(attribute);
            gasAttributeGraph.setGraphType(GraphType.COLUMN);
            // DELTA_WATER converter behaves similarly as to what a DELTA_GAS Converter would do.
            // This is an intentional line.
            gasAttributeGraph.setConverterType(ConverterType.DELTA_WATER);
            gasCsrSupportedAttributeGraphMap.put(gasAttributeGraph.getLabel(), gasAttributeGraph);
        }
        Map<String, AttributeGraphType> waterCsrSupportedAttributeGraphMap = new HashMap<>();
        for (BuiltInAttribute attribute : WATER_CSR_TREND.getBuiltInAttributes()) {
            AttributeGraphType waterAttributeGraph = new AttributeGraphType();
            waterAttributeGraph.setAttribute(attribute);
            waterAttributeGraph.setGraphType(GraphType.COLUMN);
            waterAttributeGraph.setConverterType(ConverterType.DELTA_WATER);
            waterCsrSupportedAttributeGraphMap.put(waterAttributeGraph.getLabel(), waterAttributeGraph);
        }

        Builder<CsrTrendWidgetType, Map<String, AttributeGraphType>> supportedAttributeGraphBuilder =
            new ImmutableMap.Builder<CsrTrendWidgetType, Map<String, AttributeGraphType>>()
                .put(ELECTRIC_CSR_TREND, csrSupportedAttributeGraphMap)
                .put(GAS_CSR_TREND, gasCsrSupportedAttributeGraphMap)
                .put(WATER_CSR_TREND, waterCsrSupportedAttributeGraphMap);
        
        supportedAttributeGraphMap = supportedAttributeGraphBuilder.build();
    }
    
    private String cachingWidgetParameterGrabberBeanRef;
    private BuiltInAttribute defaultAttribute;
    private List<BuiltInAttribute> builtInAttributes;

    private CsrTrendWidgetType(String cachingWidgetParameterGrabberBeanRef, BuiltInAttribute defaultAttribute,
            BuiltInAttribute... builtInAttributes) {
        this.cachingWidgetParameterGrabberBeanRef = cachingWidgetParameterGrabberBeanRef;
        this.defaultAttribute = defaultAttribute;
        this.builtInAttributes = Arrays.asList(builtInAttributes);
    }

    public BuiltInAttribute getDefaultAttribute() {
        return defaultAttribute;
    }

    public Map<String, AttributeGraphType> getSupportedAttributeGraphMap() {
        return supportedAttributeGraphMap.get(this);
    }

    public String getCachingWidgetParameterGrabberBeanRef() {
        return cachingWidgetParameterGrabberBeanRef;
    }

    public List<BuiltInAttribute> getBuiltInAttributes() {
        return builtInAttributes;
    }

}
