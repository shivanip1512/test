package com.cannontech.web.widget.gatewayInfo;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.GatewayEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.message.gateway.Authentication;
import com.cannontech.common.rfn.message.gateway.GatewayConfigResult;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResult;
import com.cannontech.common.rfn.model.GatewayConfiguration;
import com.cannontech.common.rfn.model.GatewaySettings;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.model.TimeoutExecutionException;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.gateway.model.GatewaySettingsValidator;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;

/**
 * Widget used to display basic gateway information
 */
@Controller
@RequestMapping("/gatewayInformationWidget/*")
public class GatewayInformationWidget extends AdvancedWidgetControllerBase {
    
    private static final Logger log = YukonLogManager.getLogger(GatewayInformationWidget.class);
    private static final String baseKey = "yukon.web.modules.operator.gateways.";
    
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private GatewaySettingsValidator validator;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private GatewayEventLogService gatewayEventLogService;
    
    @Autowired
    public GatewayInformationWidget(@Qualifier("widgetInput.deviceId")
            SimpleWidgetInput simpleWidgetInput,
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
            if (gateway.isDataStreamingSupported()) {
                model.addAttribute("streamingCapacity", BuiltInAttribute.DATA_STREAMING_LOAD);
            }

        } catch (NmCommunicationException e) {
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            String errorMsg = accessor.getMessage(baseKey + "error.comm");
            model.addAttribute("errorMsg", errorMsg);
        }
        
        return "gatewayInformationWidget/render.jsp";
    }
    
    @CheckRoleProperty(YukonRoleProperty.INFRASTRUCTURE_CREATE_AND_UPDATE)
    @RequestMapping(value="edit", method=RequestMethod.GET)
    public String editDialog(ModelMap model, int deviceId, YukonUserContext userContext) {
        
        model.addAttribute("mode", PageEditMode.EDIT);
        model.addAttribute("deviceId", deviceId);

        try {
            
            RfnGateway gateway = rfnGatewayService.getGatewayByPaoIdWithData(deviceId);
            model.addAttribute("isVirtualGateway", gateway.getPaoIdentifier().getPaoType() == PaoType.VIRTUAL_GATEWAY);
            GatewaySettings settings = rfnGatewayService.gatewayAsSettings(gateway);
            model.addAttribute("settings", settings);
            
        } catch (NmCommunicationException e) {
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            String errorMsg = accessor.getMessage(baseKey + "error.comm");
            model.addAttribute("errorMsg", errorMsg);
        }
        
        return "gatewayInformationWidget/settings.jsp";
    }
    
    @CheckRoleProperty(YukonRoleProperty.INFRASTRUCTURE_CREATE_AND_UPDATE)
    @RequestMapping(value="configure", method=RequestMethod.GET)
    public String configureDialog(ModelMap model, int deviceId, YukonUserContext userContext) {
        
        model.addAttribute("mode", PageEditMode.EDIT);
        model.addAttribute("deviceId", deviceId);

        try {
            
            RfnGateway gateway = rfnGatewayService.getGatewayByPaoIdWithData(deviceId);
            GatewayConfiguration configuration = rfnGatewayService.gatewayAsConfiguration(gateway);
            model.addAttribute("configuration", configuration);
            model.addAttribute("gateway", gateway);
            
        } catch (NmCommunicationException e) {
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            String errorMsg = accessor.getMessage(baseKey + "error.comm");
            model.addAttribute("errorMsg", errorMsg);
        }
        
        return "gatewayInformationWidget/configuration.jsp";
    }
    
    /** Configure the gateway */
    @CheckRoleProperty(YukonRoleProperty.INFRASTRUCTURE_CREATE_AND_UPDATE)
    @RequestMapping(value="configure", method=RequestMethod.POST)
    public String configure(ModelMap model, YukonUserContext userContext, HttpServletResponse resp, FlashScope flash,
            int deviceId, @ModelAttribute("configuration") GatewayConfiguration configuration, BindingResult result) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

        model.addAttribute("deviceId", deviceId);
        
        if (result.hasErrors()) {
            returnConfigurationErrorModel(null, model, resp);
        }
        
        try {
            
            RfnGateway gateway = rfnGatewayService.getGatewayByPaoIdWithData(deviceId);

            //validate settings.getIpv6Prefix().size() = 16
            if (gateway.getData().getIpv6Prefix() == null
                || !gateway.getData().getIpv6Prefix().equals(configuration.getIpv6Prefix())) {
                try {
                    GatewayConfigResult configResult = rfnGatewayService.updateIpv6Prefix(gateway, configuration.getIpv6Prefix());
                    if (configResult != GatewayConfigResult.SUCCESSFUL) {
                        String errorMsg = accessor.getMessage(baseKey + "error." + configResult.name());
                        returnConfigurationErrorModel(errorMsg, model, resp);
                    } else {
                        gatewayEventLogService.updatedIpv6Prefix(configuration.getIpv6Prefix());
                        // Success
                        model.clear();
                        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "detail.update.successful", gateway.getName()));
                        Map<String, Object> json = new HashMap<>();
                        json.put("success", true);

                        return JsonUtils.writeResponse(resp, json);
                    }
                } catch (DuplicateException e) {
                    String errorMsg = accessor.getMessage(baseKey + "error." + GatewayConfigResult.FAILED_DUPLICATE_CONFIG);
                    returnConfigurationErrorModel(errorMsg, model, resp);
                } catch (IllegalArgumentException e) {
                    String errorMsg = accessor.getMessage(baseKey + "exception.illegalargument");
                    returnConfigurationErrorModel(errorMsg, model, resp); 
                }
            }

            
        } catch (NmCommunicationException e) {
            String errorMsg = accessor.getMessage(baseKey + "error.comm");
            returnConfigurationErrorModel(errorMsg, model, resp);
        }
        return "gatewayInformationWidget/configuration.jsp";
    }
    
    private String returnConfigurationErrorModel(String errorMsg, ModelMap model, HttpServletResponse resp) {
        resp.setStatus(HttpStatus.BAD_REQUEST.value());
        model.addAttribute("mode", PageEditMode.EDIT);
        if (errorMsg != null) {
            model.addAttribute("errorMsg", errorMsg);
        }
        return "gatewayInformationWidget/configuration.jsp";
    }
    
    /** Update the gateway */
    @CheckRoleProperty(YukonRoleProperty.INFRASTRUCTURE_CREATE_AND_UPDATE)
    @RequestMapping(value="edit", method=RequestMethod.PUT)
    public String update(ModelMap model,
            YukonUserContext userContext,
            HttpServletResponse resp,
            FlashScope flash,
            int deviceId,
            @ModelAttribute("settings") GatewaySettings settings,
            BindingResult result) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

        validator.validate(settings, result);
        model.addAttribute("deviceId", deviceId);
        
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", PageEditMode.EDIT);
            return "gatewayInformationWidget/settings.jsp";
        }
        
        try {
            
            String port = String.valueOf(settings.getPort());
            String updateServerUrl = null;
            Authentication auth = new Authentication();
            RfnGateway gateway = rfnGatewayService.getGatewayByPaoIdWithData(deviceId);
            
            gateway.setName(settings.getName());
            
            if (settings.isUseDefaultUpdateServer()) {
                updateServerUrl = globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER);
                auth.setUsername(globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER_USER));
                auth.setPassword(globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER_PASSWORD));
            } else {
                updateServerUrl = settings.getUpdateServerUrl();
                auth.setUsername(settings.getUpdateServerLogin().getUsername());
                auth.setPassword(settings.getUpdateServerLogin().getPassword());
            }

            if (settings.isUseDefaultPort()) {
                port = null;
            }

            RfnGatewayData.Builder builder = new RfnGatewayData.Builder();
            RfnGatewayData data = builder.copyOf(gateway.getData())
           .ipAddress(settings.getIpAddress())
           .port(port)
           .name(settings.getName())
           .admin(settings.getAdmin())
           .superAdmin(settings.getSuperAdmin())
           .updateServerUrl(updateServerUrl)
           .updateServerLogin(auth)
           .build();
            
            gateway.setData(data);
            
            GatewayUpdateResult updateResult = rfnGatewayService.updateGateway(gateway, userContext.getYukonUser());
            
            if (updateResult == GatewayUpdateResult.SUCCESSFUL) {
                log.info("Gateway updated: " + gateway);
                gatewayEventLogService.updatedGateway(userContext.getYukonUser(), gateway.getName(), 
                                                      gateway.getRfnIdentifier().getSensorSerialNumber(), 
                                                      settings.getIpAddress(),
                                                      settings.getAdmin().getUsername(), 
                                                      settings.getSuperAdmin().getUsername());
                // Success
                model.clear();
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "detail.update.successful", settings.getName()));
                Map<String, Object> json = new HashMap<>();
                json.put("success", true);

                return JsonUtils.writeResponse(resp, json);

            } else {
                resp.setStatus(HttpStatus.BAD_REQUEST.value());
                model.addAttribute("mode", PageEditMode.EDIT);
                String errorMsg = accessor.getMessage(baseKey + "error." + updateResult.name());
                model.addAttribute("errorMsg", errorMsg);
                
                return "gatewayInformationWidget/settings.jsp";
            }
            
        } catch (NmCommunicationException e) {
            
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", PageEditMode.EDIT);
            String errorMsg = accessor.getMessage(baseKey + "error.comm");
            model.addAttribute("errorMsg", errorMsg);
            
            return "gatewayInformationWidget/settings.jsp";
        }
    }
    
    /** Test the connection, return result as json. */
    @RequestMapping("test-connection")
    public @ResponseBody Map<String, Object> testConnection(YukonUserContext userContext, 
            int id, String ip, String username, String password) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        Map<String, Object> json = new HashMap<>();
        try {
            boolean success = false;
            if (ip == null || username == null || password == null) {
                success = rfnGatewayService.testConnection(id);
            } else {
                success = rfnGatewayService.testConnection(id, ip, username, password);
            }
            json.put("success", success);
        } catch (NmCommunicationException e) {
            json.put("success", false);
            if (e.getCause() instanceof TimeoutExecutionException) {
                json.put("message", accessor.getMessage(baseKey + "login.failed.timeout"));
            }
        }
        
        return json;
    }

}