package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.chart.model.AttributeGraphType;
import com.cannontech.common.chart.model.ConverterType;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.impl.CachingWidgetParameterGrabber;

/**
 * Widget used to display point data in a trend
 */
@Controller
@RequestMapping("/gasCsrTrendWidget/*")
public class GasCsrTrendWidget extends CsrTrendWidget {

    @Autowired
    @Qualifier("gasCsrTrendWidgetCachingWidgetParameterGrabber")
    private CachingWidgetParameterGrabber cachingWidgetParameterGrabber;

    public GasCsrTrendWidget() {
    }

    @Autowired
    public GasCsrTrendWidget(
            @Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput) {
        super(simpleWidgetInput);

        List<AttributeGraphType> attributeGraphType = new ArrayList<AttributeGraphType>();
        AttributeGraphType attributeGraph = new AttributeGraphType();
        attributeGraph.setAttribute(BuiltInAttribute.USAGE_GAS);
        attributeGraph.setGraphType(GraphType.COLUMN);
        //DELTA_WATER converter behaves similarly as to what a DELTA_GAS Converter would do.
        //This is an intentional line.
        attributeGraph.setConverterType(ConverterType.DELTA_WATER);

        attributeGraphType.add(attributeGraph);

        this.setDefaultAttribute(BuiltInAttribute.USAGE_GAS);
        this.setSupportedAttributeGraphSet(attributeGraphType);
    }
}