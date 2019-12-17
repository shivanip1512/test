package com.cannontech.web.dev;

import java.util.Map;

import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.dev.error.RelayStateInjectionException;
import com.cannontech.web.dev.model.RelayStateInjectionParams;
import com.cannontech.web.dev.model.RelayStateInjectionStatus;
import com.cannontech.web.dev.service.RelayStateInjectionService;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.security.annotation.CheckCparm;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/relayStateInjection/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class RelayStateInjectionController {
    private static final Logger log = YukonLogManager.getLogger(RelayStateInjectionController.class);
    
    @Autowired private RelayStateInjectionService relayStateInjectionService;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private DateFormattingService dateFormattingService;
    
    @RequestMapping("relayStateInjection")
    public String relayStateInjection(ModelMap model, RelayStateInjectionParams params) {
        return "relayStateInjection/relayStateInjection.jsp";
    }
    
    @RequestMapping("startInjection")
    public String startInjection(FlashScope flash, RelayStateInjectionParams params) {
        try {
            int deviceCount = relayStateInjectionService.startInjection(params);
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dev.relayStateInjection.started", 
                                                              deviceCount));
        } catch (Exception e) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dev.relayStateInjection.startError", 
                                                            e.getMessage()));
        }
        return "redirect:relayStateInjection";
    }
    
    @RequestMapping("stopInjection") 
    public String stopInjection(FlashScope flash) {
        try {
            relayStateInjectionService.stopInjection();
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dev.relayStateInjection.canceled"));
        } catch (RelayStateInjectionException e) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.dev.relayStateInjection.stopError", 
                                                            e.getMessage()));
        }
        return "redirect:relayStateInjection";
    }
    
    @RequestMapping("injection-status")
    @ResponseBody
    public Map<String, Object> injectionStatus(YukonUserContext userContext) {
        RelayStateInjectionParams params = relayStateInjectionService.getParams();
        RelayStateInjectionStatus status = relayStateInjectionService.getStatus();
        
        Map<String, Object> json = Maps.newHashMapWithExpectedSize(8);
        
        // Overall status
        json.put("status", status);
        
        // Injection parameters
        json.put("group", params.getGroupName());
        json.put("period", params.getPeriod());
        json.put("startAfterLastReading", params.isStartAfterLastReading());
        
        // Start of point data range
        String start = dateFormattingService.format(params.getStart(), DateFormatEnum.DATEHM, userContext);
        json.put("start", start);
        
        // Stop of point data range
        String stop = dateFormattingService.format(params.getStop(), DateFormatEnum.DATEHM, userContext);
        json.put("stop", stop);
        
        // Progress bar
        json.put("deviceCount", params.getDeviceCount());
        json.put("devicesComplete", params.getDevicesComplete());
        
        // Operation start time
        String injectionStart = "--";
        if (params.getInjectionStart() != null) {
            injectionStart = dateFormattingService.format(params.getInjectionStart(), DateFormatEnum.DATEHM, userContext);
        }
        json.put("injectionStart", injectionStart);
        
        // Operation end time
        String injectionEnd = "--";
        if (params.getInjectionEnd() != null) {
            injectionEnd = dateFormattingService.format(params.getInjectionEnd(), DateFormatEnum.DATEHM, userContext);
        }
        json.put("injectionEnd", injectionEnd);
        
        return json;
    }
    
    // Tells Spring how to bind Instants to the model object
    @InitBinder
    public void setupBinder(WebDataBinder webDataBinder, YukonUserContext userContext) {
        datePropertyEditorFactory.setupInstantPropertyEditor(webDataBinder, userContext, BlankMode.CURRENT);
    }
}
