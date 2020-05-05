package com.cannontech.web.widget;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.port.BaudRate;
import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.device.port.TcpSharedPortDetail;
import com.cannontech.common.device.port.TerminalServerPortDetailBase;
import com.cannontech.common.device.port.UdpPortDetail;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiCommunicationException;
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
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    private static final Logger log = YukonLogManager.getLogger(CommChannelInfoWidget.class);
    private static final String bindingResultKey = "org.springframework.validation.BindingResult.commChannel";
    private static final String baseKey = "yukon.web.modules.dr.setup.";

    @Autowired
    public CommChannelInfoWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput) {
        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
    }

    @RequestMapping("render")
    public String render(ModelMap model, HttpServletRequest request, YukonUserContext userContext, FlashScope flash) {
        int deviceId = 0;
        try {
            deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
            model.addAttribute("mode", PageEditMode.VIEW);
            retrieveCommChannel(userContext, request, deviceId, model);
        } catch (ServletRequestBindingException e) {
            log.error("Error rendering Comm Channel Information widget", e);
        } catch (ApiCommunicationException ex) {
            log.error("Error retrieving comm channel: " + ex.getMessage());
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            String errorMsg = accessor.getMessage("yukon.exception.apiCommunicationException.communicationError");
            model.addAttribute("errorMsg", errorMsg);
        }
        return "commChannelInfoWidget/render.jsp";
    }

    @GetMapping("/{id}/edit")
    public String edit(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash,
            HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.EDIT);
        try {
            retrieveCommChannel(userContext, request, id, model);
        } catch (ApiCommunicationException ex) {
            log.error("Error retrieving comm channel: " + ex.getMessage());
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            String errorMsg = accessor.getMessage("yukon.exception.apiCommunicationException.communicationError");
            model.addAttribute("errorMsg", errorMsg);
        }
        return "commChannelInfoWidget/render.jsp";
    }

    private void retrieveCommChannel(YukonUserContext userContext, HttpServletRequest request, int id, ModelMap model) {
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.commChannelViewUrl + id);
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request, url,
                    HttpMethod.GET, PortBase.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                PortBase commChannel = (PortBase) response.getBody();
                commChannel.setId(id);
                model.addAttribute("commChannel", commChannel);
                model.addAttribute("baudRateList", BaudRate.values());
                if (commChannel instanceof TerminalServerPortDetailBase) {
                    model.addAttribute("isAdditionalConfigSupported", true);
                    model.addAttribute("isPortNumberSupported", true);
                    if (commChannel instanceof UdpPortDetail) {
                        model.addAttribute("isEncyptionSupported", true);
                    }
                    if (commChannel instanceof TcpSharedPortDetail) {
                        model.addAttribute("isIpAddressSupported", true);
                    }
                }
            }
        } catch (RestClientException ex) {
            log.error("Error retrieving comm Channel: " + ex.getMessage());
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            String errorMsg = accessor.getMessage("yukon.exception.restClientCommunicationException.communicationError");
            model.addAttribute("errorMsg", errorMsg);
        }
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("commChannel") PortBase commChannel, BindingResult result, YukonUserContext userContext,
            FlashScope flash, RedirectAttributes redirectAttributes, HttpServletRequest request, ModelMap model,
            HttpServletResponse resp) throws IOException {

        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.commChannelUpdateUrl + commChannel.getId());
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request, url,
                    HttpMethod.POST, Object.class, commChannel);
            if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(commChannel, "commChannel");
                result = helper.populateBindingError(result, error, response);
                if (result.hasErrors()) {
                    resp.setStatus(HttpStatus.BAD_REQUEST.value());
                    model.addAttribute(bindingResultKey, result);
                    model.addAttribute("baudRateList", BaudRate.values());
                    return "commChannelInfoWidget/render.jsp";
                }
            }

            if (response.getStatusCode() == HttpStatus.OK) {
                model.clear();
                Map<String, Object> json = new HashMap<>();
                json.put("success", true);
                resp.setContentType("application/json");
                JsonUtils.getWriter().writeValue(resp.getOutputStream(), json);
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "save.success", commChannel.getName()));
                return null;
            }

        } catch (ApiCommunicationException | RestClientException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable("yukon.exception.apiCommunicationException.communicationError"));
            return "redirect:/device/commChannel/" + commChannel.getId();
        }
        return null;
    }
}
