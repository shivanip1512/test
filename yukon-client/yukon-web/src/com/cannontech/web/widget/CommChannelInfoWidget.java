package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.port.PortBase;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@Controller
@RequestMapping("/commChannelInfoWidget")
public class CommChannelInfoWidget extends AdvancedWidgetControllerBase {

    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    private static final Logger log = YukonLogManager.getLogger(CommChannelInfoWidget.class);

    @Autowired
    public CommChannelInfoWidget(@Qualifier("widgetInput.deviceId")
            SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
    }

    @RequestMapping("render")
    public String render(ModelMap model, HttpServletRequest request, YukonUserContext userContext, FlashScope flash) {
        int deviceId = 0;
        try {
            deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
            try {
                String url = helper.findWebServerUrl(request, userContext, ApiURL.commChannelViewUrl + deviceId);
                ResponseEntity<? extends Object> response =
                        apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.GET, PortBase.class);

                    if (response.getStatusCode() == HttpStatus.OK) {
                        PortBase commChannel = (PortBase) response.getBody();
                        model.addAttribute("commChannel", commChannel);
                    }
            } catch (RestClientException ex) {
                log.error("Error retrieving comm channel: " + ex.getMessage());
            }
        } catch (ServletRequestBindingException e) {
            log.error("Error rendering Comm Channel Information widget", e);
        }
        return "commChannelInfoWidget/render.jsp";
    }
}
