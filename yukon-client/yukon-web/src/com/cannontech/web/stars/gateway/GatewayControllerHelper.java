package com.cannontech.web.stars.gateway;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class GatewayControllerHelper {
    
    private static final String baseKey = "yukon.web.modules.operator.gateways.";
    
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    public void addText(ModelMap model, YukonUserContext userContext) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        Map<String, String> text = new HashMap<>();
        text.put("connect.pending", accessor.getMessage(baseKey + "connect.pending"));
        text.put("connect.success", accessor.getMessage(baseKey + "connect.success"));
        text.put("connect.failure", accessor.getMessage(baseKey + "connect.failure"));
        text.put("login.successful", accessor.getMessage(baseKey + "login.successful"));
        text.put("login.failed", accessor.getMessage(baseKey + "login.failed"));
        text.put("disconnect.pending", accessor.getMessage(baseKey + "disconnect.pending"));
        text.put("disconnect.success", accessor.getMessage(baseKey + "disconnect.success"));
        text.put("disconnect.failure", accessor.getMessage(baseKey + "disconnect.failure"));
        text.put("collect.data.pending", accessor.getMessage(baseKey + "collect.data.pending"));
        text.put("collect.data.success", accessor.getMessage(baseKey + "collect.data.success"));
        text.put("collect.data.failure", accessor.getMessage(baseKey + "collect.data.failure"));
        text.put("collect.data.title", accessor.getMessage(baseKey + "collect.data.title"));
        text.put("cert.update.more", accessor.getMessage(baseKey + "cert.update.more"));
        text.put("cert.update.label", accessor.getMessage(baseKey + "cert.update.label"));
        text.put("complete", accessor.getMessage("yukon.common.complete"));
        
        model.addAttribute("text", text);
        
    }
    
}