package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.amr.rfn.dataStreaming.model.DataStreamingConfigException;
import com.cannontech.amr.rfn.dataStreaming.service.DataStreamingService;
import com.cannontech.common.config.MasterConfigLicenseKey;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.web.security.annotation.CheckCparmLicense;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;

@Controller
@RequestMapping("/overloadedGatewaysWidget/*")
@CheckCparmLicense(license = MasterConfigLicenseKey.RF_DATA_STREAMING_ENABLED)
public class OverloadedGatewaysWidget extends AdvancedWidgetControllerBase {

    @Autowired private DataStreamingService dataStreamingService;
    
    @Autowired
    public OverloadedGatewaysWidget(RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        setLazyLoad(true);
        setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile(MasterConfigLicenseKey.RF_DATA_STREAMING_ENABLED.name()));
    }
    
    @RequestMapping(value = "render", method = RequestMethod.GET)
    public String render(ModelMap model) {

        List<RfnGateway> overloadedGateways = new ArrayList<>();
        try {
            overloadedGateways = dataStreamingService.getOverloadedGateways();
        } catch (DataStreamingConfigException e) {}
        
        model.addAttribute("overloadedGateways", overloadedGateways);
        
        return "overloadedGatewaysWidget/render.jsp";
    }
}

