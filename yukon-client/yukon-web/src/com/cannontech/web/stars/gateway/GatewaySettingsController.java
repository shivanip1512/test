package com.cannontech.web.stars.gateway;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.message.gateway.DataType;
import com.cannontech.common.rfn.model.GatewaySettings;
import com.cannontech.common.rfn.model.GatewayUpdateException;
import com.cannontech.common.rfn.model.NetworkManagerCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.stars.gateway.model.GatewaySettingsValidator;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Controller
@CheckRole(YukonRole.INVENTORY)
public class GatewaySettingsController {
    
    private static final Logger log = YukonLogManager.getLogger(GatewayListController.class);
    private static final String baseKey = "yukon.web.modules.operator.gateways.";
    
    @Autowired private GatewaySettingsValidator validator;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    @RequestMapping("/gateways/create")
    public String createDialog(ModelMap model) {
        
        model.addAttribute("mode", PageEditMode.CREATE);
        GatewaySettings settings = new GatewaySettings();
        model.addAttribute("settings", settings);
        
        return "gateways/settings.jsp";
    }
    
    /** Create a gateway, return gateway settings popup when validation or creation fails, otherwise return success json payload. */
    @RequestMapping(value={"/gateways", "/gateways/"}, method=RequestMethod.POST)
    public String create(ModelMap model,
            YukonUserContext userContext,
            HttpServletResponse resp,
            @ModelAttribute("settings") GatewaySettings settings,
            BindingResult result) throws JsonGenerationException, JsonMappingException, IOException {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        validator.validate(settings, result);
        
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", PageEditMode.CREATE);
            return "gateways/settings.jsp";
        }
        
        try {
            RfnDevice gateway = rfnGatewayService.createGateway(settings);
            log.info("Gateway Created: " + gateway);
            
            // Success
            model.clear();
            Map<String, Object> json = new HashMap<>();
            json.put("gateway", gateway);
            resp.setContentType("application/json");
            JsonUtils.getWriter().writeValue(resp.getOutputStream(), json);
            return null;
            
        } catch (NetworkManagerCommunicationException|GatewayUpdateException e) {
            
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", PageEditMode.CREATE);
            String errorMsg;
            if (e instanceof NetworkManagerCommunicationException) {
                errorMsg = accessor.getMessage(baseKey + "error.comm");
            } else {
                errorMsg = accessor.getMessage(baseKey + "create.error");
            }
            model.addAttribute("errorMsg", errorMsg);
            
            return "gateways/settings.jsp";
        }
        
    }
    
    /** Test the connection, return result as json. */
    @RequestMapping("/gateways/test-connection")
    public @ResponseBody Map<String, Object> testConnection(String ip, String username, String password) {
        
        Map<String, Object> json = new HashMap<>();
        try {
            boolean success = rfnGatewayService.testConnection(ip, username, password);
            json.put("success", success);
        } catch (NetworkManagerCommunicationException e) {
            json.put("success", false);
        }
        
        return json;
    }
    
    /** Collect data popup. */
    @RequestMapping("/gateways/collect-data/options")
    public String collectData(ModelMap model) {
        
        model.addAttribute("dataTypes", DataType.values());
        
        return "gateways/collect.data.options.jsp";
    }
    
}