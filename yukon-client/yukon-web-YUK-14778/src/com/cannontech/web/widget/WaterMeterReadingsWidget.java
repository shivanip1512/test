package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.web.widget.support.SimpleWidgetInput;

@Controller
@RequestMapping("/waterMeterReadingsWidget/*")
public class WaterMeterReadingsWidget extends MeterReadingsWidget {

    public WaterMeterReadingsWidget() {
    }

    @Autowired
    public WaterMeterReadingsWidget(
            @Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        super(simpleWidgetInput, roleAndPropertyDescriptionService);

        List<BuiltInAttribute> attibutes = new ArrayList<BuiltInAttribute>();
        attibutes.add(BuiltInAttribute.USAGE_WATER);

        this.setAttributesToShow(attibutes);
        this.setPreviousReadingsAttributeToShow(BuiltInAttribute.USAGE_WATER);
    }
}