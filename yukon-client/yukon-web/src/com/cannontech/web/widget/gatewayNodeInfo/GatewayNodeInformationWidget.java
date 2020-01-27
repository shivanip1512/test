package com.cannontech.web.widget.gatewayNodeInfo;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;

/**
 * Widget used to display Gateway Node Information
 */
@Controller
@RequestMapping("/gatewayNodeInformationWidget/*")
public class GatewayNodeInformationWidget extends AdvancedWidgetControllerBase {

    private static final String baseKey = "yukon.web.modules.operator.gateways.";

    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private RfnDeviceDao rfnDeviceDao;

    @Autowired
    public GatewayNodeInformationWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {

        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
        setLazyLoad(true);
    }

    @RequestMapping("render")
    public String render(ModelMap model, int deviceId, YukonUserContext userContext) {

        try {
            RfnGateway gateway = rfnGatewayService.getGatewayByPaoIdWithData(deviceId);
            model.addAttribute("gateway", gateway);
            
            //check for wifi meters
            List<RfnDevice> wiFiMeters = rfnDeviceDao.getDevicesForGateways(Arrays.asList(deviceId), PaoType.getWifiTypes());
            model.addAttribute("wifiCount", wiFiMeters.size());
        } catch (NmCommunicationException e) {
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            String errorMsg = accessor.getMessage(baseKey + "error.comm");
            model.addAttribute("errorMsg", errorMsg);
        }

        return "gatewayNodeInformationWidget/render.jsp";
    }

}