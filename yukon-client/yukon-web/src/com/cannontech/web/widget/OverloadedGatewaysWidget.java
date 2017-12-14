package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.web.rfn.dataStreaming.DataStreamingConfigException;
import com.cannontech.web.rfn.dataStreaming.service.DataStreamingService;
import com.cannontech.web.security.annotation.CheckCparm;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;

@Controller
@RequestMapping("/overloadedGatewaysWidget/*")
@CheckCparm(MasterConfigBoolean.RF_DATA_STREAMING_ENABLED)
public class OverloadedGatewaysWidget extends AdvancedWidgetControllerBase {

    @Autowired private DataStreamingService dataStreamingService;
    
    @Autowired
    public OverloadedGatewaysWidget(RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        setLazyLoad(true);
        setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile(MasterConfigBoolean.RF_DATA_STREAMING_ENABLED.name()));
    }
    
    @RequestMapping("render")
    public String render(ModelMap model) {

        List<RfnGateway> overloadedGateways = new ArrayList<>();
        try {
            overloadedGateways = dataStreamingService.getOverloadedGateways();
        } catch (DataStreamingConfigException e) {}
        
        model.addAttribute("overloadedGateways", overloadedGateways);
        
        return "overloadedGatewaysWidget/render.jsp";
    }
}

