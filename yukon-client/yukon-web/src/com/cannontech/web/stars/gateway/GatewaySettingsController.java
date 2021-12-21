package com.cannontech.web.stars.gateway;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.events.loggers.GatewayEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.message.gateway.Authentication;
import com.cannontech.common.rfn.message.gateway.DataType;
import com.cannontech.common.rfn.model.GatewaySettings;
import com.cannontech.common.rfn.model.GatewayUpdateException;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.amr.util.cronExpressionTag.CronException;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.web.stars.gateway.model.GatewaySettingsValidator;

@Controller
@CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.VIEW)
public class GatewaySettingsController {
    
    private static final Logger log = YukonLogManager.getLogger(GatewayListController.class);
    private static final String baseKey = "yukon.web.modules.operator.gateways.";
    
    @Autowired private CronExpressionTagService cronService;
    @Autowired private GatewayEventLogService gatewayEventLogService;
    @Autowired private GatewaySettingsValidator validator;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private ServerDatabaseCache cache;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private GatewayControllerHelper helper;

    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.CREATE)
    @RequestMapping("/gateways/create")
    public String createDialog(ModelMap model) {
        
        model.addAttribute("mode", PageEditMode.CREATE);
        GatewaySettings settings = new GatewaySettings();
        
        settings.setNmPort(RfnGatewayService.GATEWAY_DEFAULT_PORT);
        
        //get all NM IP Address/Port combos
        model.addAttribute("nmIPAddressPorts", helper.getAllGatewayNMIPPorts());
        
        // prefill with the most used nm ip address and port
        GatewayNMIPAddressPort mostUsedNMIPAddressPort = helper.getMostUsedGatewayNMIPPort();
        if (mostUsedNMIPAddressPort != null) {
        	settings.setNmIpAddress(mostUsedNMIPAddressPort.getNmIpAddress());
        	settings.setNmPort(mostUsedNMIPAddressPort.getNmPort());
        }
        
        settings.setUpdateServerUrl(globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER));

        Authentication auth = new Authentication();
        auth.setUsername(globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER_USER));
        auth.setPassword(globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER_PASSWORD));

        settings.setUpdateServerLogin(auth);
        settings.setUseDefaultPort(true);
        settings.setUseDefaultUpdateServer(true);
        settings.setPort(RfnGatewayService.GATEWAY_DEFAULT_PORT);
        
        model.addAttribute("settings", settings);
        
        return "../widget/gatewayInformationWidget/settings.jsp";
    }
    
    /** 
     * Create a gateway, return gateway settings popup when validation or creation fails, 
     * otherwise return success json payload. 
     */
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.CREATE)
    @RequestMapping(value={"/gateways", "/gateways/"}, method=RequestMethod.POST)
    public String create(ModelMap model,
            YukonUserContext userContext,
            HttpServletResponse resp,
            @ModelAttribute("settings") GatewaySettings settings,
            BindingResult result) {
        
        model.addAttribute("nmIPAddressPorts", helper.getAllGatewayNMIPPorts());
        validator.validate(settings, result);
        
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", PageEditMode.CREATE);
            return "../widget/gatewayInformationWidget/settings.jsp";
        }
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        try {
            RfnDevice gateway = rfnGatewayService.createGateway(settings, userContext.getYukonUser());
            log.info("Gateway Created: " + gateway);
            gatewayEventLogService.createdGateway(userContext.getYukonUser(), gateway.getName(), 
                                                  gateway.getRfnIdentifier().getSensorSerialNumber(), 
                                                  settings.getIpAddress(),
                                                  settings.getAdmin().getUsername(), 
                                                  settings.getSuperAdmin().getUsername());
            
            // Success
            model.clear();
            Map<String, Object> json = new HashMap<>();
            json.put("gateway", gateway);

            return JsonUtils.writeResponse(resp, json);
        } catch (NmCommunicationException e) {
            log.error("Failed to create gateway. Settings: " + settings, e);
            gatewayEventLogService.gatewayCreationFailed(userContext.getYukonUser(), settings.getName(),
                settings.getIpAddress(), settings.getAdmin().getUsername(), settings.getSuperAdmin().getUsername());
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", PageEditMode.CREATE);
            String errorMsg = accessor.getMessage(baseKey + "error.comm");
            model.addAttribute("errorMsg", errorMsg);
            
            return "../widget/gatewayInformationWidget/settings.jsp";
        } catch (GatewayUpdateException e) {
            log.error("Failed to create gateway. Settings: " + settings, e);
            gatewayEventLogService.gatewayCreationFailed(userContext.getYukonUser(), settings.getName(),
                settings.getIpAddress(), settings.getAdmin().getUsername(), settings.getSuperAdmin().getUsername());
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", PageEditMode.CREATE);
            String errorMsg = accessor.getMessage(baseKey + "error." + e.getReason().name());
            model.addAttribute("errorMsg", errorMsg);
            
            return "../widget/gatewayInformationWidget/settings.jsp";
        } catch (DeviceCreationException e) {
            log.error("Failed to create gateway. Settings: " + settings, e);
            gatewayEventLogService.gatewayCreationFailed(userContext.getYukonUser(), settings.getName(),
                settings.getIpAddress(), settings.getAdmin().getUsername(), settings.getSuperAdmin().getUsername());
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", PageEditMode.CREATE);
            String errorMsg = accessor.getMessage(e.getMessageSourceResolvable());
            model.addAttribute("errorMsg", errorMsg);
            
            return "../widget/gatewayInformationWidget/settings.jsp";
        }

    }
    
    /** Set schedule popup. 
     * @throws NmCommunicationException */
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.CREATE)
    @RequestMapping("/gateways/{id}/schedule/options")
    public String schedule(ModelMap model, YukonUserContext userContext, @PathVariable int id) 
            throws NmCommunicationException {
        
        RfnGateway gateway = rfnGatewayService.getGatewayByPaoIdWithData(id);
        String schedule = gateway.getData().getCollectionSchedule();
        CronExpressionTagState state = cronService.parse(schedule, userContext);
        model.addAttribute("state", state);
        model.addAttribute("id", id);
        
        return "gateways/schedule.jsp";
    }
    
    /** Set schedule. 
     * @throws ServletRequestBindingException */ 
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.CREATE)
    @RequestMapping("/gateways/{id}/schedule")
    public String schedule(HttpServletResponse resp, HttpServletRequest req, ModelMap model, FlashScope flash,
            YukonUserContext userContext, @PathVariable int id, String uid) throws ServletRequestBindingException {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

        try {
            String cron = cronService.build(uid, req, userContext);
            LiteYukonPAObject pao = cache.getAllPaosMap().get(id);
            rfnGatewayService.setCollectionSchedule(pao.getPaoIdentifier(), cron);
            
            // Success
            model.clear();
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "detail.schedule.update.successful"));
            Map<String, Object> json = new HashMap<>();
            json.put("success", true);

            return JsonUtils.writeResponse(resp, json);

        } catch (CronException | NmCommunicationException e) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("id", id);
            //display what the user entered
            CronExpressionTagState state = new CronExpressionTagState();
            String customExpression = ServletRequestUtils.getRequiredStringParameter(req, uid + "_CRONEXP_CUSTOM_EXPRESSION");
            state.setCustomExpression(customExpression);
            model.addAttribute("state", state);
            Boolean hourlyRandomized = ServletRequestUtils.getBooleanParameter(req, uid + "_HOURLY_RANDOMIZED", false);
            model.addAttribute("hourlyRandomized", hourlyRandomized);
            
            if (e instanceof CronException) {
                model.addAttribute("errorMsg", accessor.getMessage("yukon.common.invalidCron"));
            } else {
                model.addAttribute("errorMsg", accessor.getMessage(baseKey + "error.comm"));
            }
            
            return "gateways/schedule.jsp";
        }
    }
    
    /** Collect data popup. */
    @RequestMapping("/gateways/collect-data/options")
    public String collectData(ModelMap model) {
        
        model.addAttribute("dataTypes", DataType.values());
        
        return "gateways/collect.data.options.jsp";
    }
}