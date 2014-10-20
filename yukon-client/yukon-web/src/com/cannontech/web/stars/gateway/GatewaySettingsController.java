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

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.rfn.model.GatewaySettings;
import com.cannontech.common.rfn.model.GatewayUpdateException;
import com.cannontech.common.rfn.model.NetworkManagerCommunicationException;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
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
        
        model.addAttribute("mode", "CREATE");
        model.addAttribute("settings", new GatewaySettings());
        
        return "gateways/settings.jsp";
    }
    
    @RequestMapping(value = {"/gateways", "/gateways/"}, method = RequestMethod.POST)
    public String create(ModelMap model,
            YukonUserContext userContext,
            HttpServletResponse resp,
            @ModelAttribute("settings") GatewaySettings settings,
            BindingResult result) throws JsonGenerationException, JsonMappingException, IOException {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        validator.validate(settings, result);
        
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            return "gateways/settings.jsp";
        }
        
        try {
            PaoIdentifier gateway = rfnGatewayService.createGateway(settings);
            log.info("Gateway Created: " + gateway);
            
            // Success
            model.clear();
            Map<String, Object> json = new HashMap<>();
            
            resp.setContentType("application/json");
            JsonUtils.getWriter().writeValue(resp.getOutputStream(), json);
            return null;
            
        } catch (NetworkManagerCommunicationException|GatewayUpdateException e) {
            
            return "gateways/settings.jsp";
        }
        
    }
    
}