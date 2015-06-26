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
@RequestMapping("/polyphaseMeterReadingsWidget/*")
public class PolyphaseMeterReadingsWidget extends MeterReadingsWidget {
    
    public PolyphaseMeterReadingsWidget() {
    }
    
    @Autowired
    public PolyphaseMeterReadingsWidget(@Qualifier("widgetInput.deviceId")
            SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        super(simpleWidgetInput, roleAndPropertyDescriptionService);

        List<BuiltInAttribute> attibutes = new ArrayList<BuiltInAttribute>();
        attibutes.add(BuiltInAttribute.USAGE);
        attibutes.add(BuiltInAttribute.PEAK_DEMAND);
        attibutes.add(BuiltInAttribute.DEMAND);
        attibutes.add(BuiltInAttribute.VOLTAGE_PHASE_A);
        attibutes.add(BuiltInAttribute.VOLTAGE_PHASE_B);
        attibutes.add(BuiltInAttribute.VOLTAGE_PHASE_C);

        super.setAttributesToShow(attibutes);
    }
}