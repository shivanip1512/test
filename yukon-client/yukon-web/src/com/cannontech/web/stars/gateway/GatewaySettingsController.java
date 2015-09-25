package com.cannontech.web.stars.gateway;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigDouble;
import com.cannontech.common.events.loggers.EndpointEventLogService;
import com.cannontech.common.events.loggers.GatewayEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.gateway.Authentication;
import com.cannontech.common.rfn.message.gateway.DataType;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResult;
import com.cannontech.common.rfn.model.GatewaySettings;
import com.cannontech.common.rfn.model.GatewayUpdateException;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.service.NMConfigurationService;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.roleproperties.YukonRole;
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
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.gateway.model.GatewaySettingsValidator;
import com.cannontech.web.stars.gateway.model.Location;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Controller
@CheckRole(YukonRole.INVENTORY)
public class GatewaySettingsController {
    
    private static final Logger log = YukonLogManager.getLogger(GatewayListController.class);
    private static final String baseKey = "yukon.web.modules.operator.gateways.";
    
    @Autowired private ServerDatabaseCache cache;
    @Autowired private CronExpressionTagService cronService;
    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private GatewaySettingsValidator validator;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private GatewayEventLogService gatewayEventLogService;
    @Autowired private EndpointEventLogService endpointEventLogService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private NMConfigurationService nmConfigurationService;
    
    @RequestMapping("/gateways/create")
    public String createDialog(ModelMap model) {
        
        model.addAttribute("mode", PageEditMode.CREATE);
        GatewaySettings settings = new GatewaySettings();
        
        if (nmConfigurationService.isFirmwareUpdateSupported()) {

            settings.setUpdateServerUrl(globalSettingDao.getString(GlobalSettingType.UPDATE_SERVER_URL));

            Authentication auth = new Authentication();
            auth.setUsername(globalSettingDao.getString(GlobalSettingType.UPDATE_SERVER_ADMIN_USER));
            auth.setPassword(globalSettingDao.getString(GlobalSettingType.UPDATE_SERVER_ADMIN_PASSWORD));

            settings.setUpdateServerLogin(auth);
            settings.setUseDefault(true);
        }
        model.addAttribute("settings", settings);
        
        return "gateways/settings.jsp";
    }
    
    /** 
     * Create a gateway, return gateway settings popup when validation or creation fails, 
     * otherwise return success json payload. 
     */
    @CheckRoleProperty(YukonRoleProperty.INFRASTRUCTURE_CREATE_AND_UPDATE)
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
            resp.setContentType("application/json");
            JsonUtils.getWriter().writeValue(resp.getOutputStream(), json);
            return null;
            
        } catch (NmCommunicationException e) {
            
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", PageEditMode.CREATE);
            String errorMsg = accessor.getMessage(baseKey + "error.comm");
            model.addAttribute("errorMsg", errorMsg);
            
            return "gateways/settings.jsp";
        } catch (GatewayUpdateException e) {
            
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", PageEditMode.CREATE);
            String errorMsg = accessor.getMessage(baseKey + "error." + e.getReason().name());
            model.addAttribute("errorMsg", errorMsg);
            
            return "gateways/settings.jsp";
        }
        
    }
    
    @CheckRoleProperty(YukonRoleProperty.INFRASTRUCTURE_CREATE_AND_UPDATE)
    @RequestMapping("/gateways/{id}/edit")
    public String editDialog(ModelMap model, @PathVariable int id) {
        
        model.addAttribute("mode", PageEditMode.EDIT);
        
        try {
            
            RfnGateway gateway = rfnGatewayService.getGatewayByPaoIdWithData(id);
            
            GatewaySettings settings = new GatewaySettings();
            settings.setId(id);
            settings.setName(gateway.getName());
            settings.setIpAddress(gateway.getData().getIpAddress());
            settings.setAdmin(gateway.getData().getAdmin());
            settings.setSuperAdmin(gateway.getData().getSuperAdmin());
            if (gateway.getLocation() != null) {
                settings.setLatitude(gateway.getLocation().getLatitude());
                settings.setLongitude(gateway.getLocation().getLongitude());
            }
            
            
            if(nmConfigurationService.isFirmwareUpdateSupported()) {
            
            settings.setUpdateServerUrl(gateway.getData().getUpdateServerUrl());
            settings.setUpdateServerLogin(gateway.getData().getUpdateServerLogin());
            if(gateway.getData().getUpdateServerUrl().equals(globalSettingDao.getString(GlobalSettingType.UPDATE_SERVER_URL))) {
                settings.setUseDefault(true);
            }
            }
            model.addAttribute("settings", settings);
            
        } catch (NmCommunicationException e) {
            // TODO 
        }
        
        return "gateways/settings.jsp";
    }
    
    /** Update the gateway */
    @CheckRoleProperty(YukonRoleProperty.INFRASTRUCTURE_CREATE_AND_UPDATE)
    @RequestMapping(value="/gateways/{id}", method=RequestMethod.PUT)
    public String update(ModelMap model,
            YukonUserContext userContext,
            HttpServletResponse resp,
            FlashScope flash,
            @PathVariable int id,
            @ModelAttribute("settings") GatewaySettings settings,
            BindingResult result) throws JsonGenerationException, JsonMappingException, IOException {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        validator.validate(settings, result);
        
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", PageEditMode.EDIT);
            return "gateways/settings.jsp";
        }
        
        try {
            
            String updateServerUrl = null;
            Authentication auth = new Authentication();
            
            RfnGateway gateway = rfnGatewayService.getGatewayByPaoIdWithData(id);
            
            gateway.setName(settings.getName());
            if (settings.getLatitude() != null) {
                gateway.setLocation(new PaoLocation(gateway.getPaoIdentifier(), settings.getLatitude(),
                    settings.getLongitude()));
            }
            
            
            if(nmConfigurationService.isFirmwareUpdateSupported()) {
            
            if(settings.isUseDefault()) {
                updateServerUrl = globalSettingDao.getString(GlobalSettingType.UPDATE_SERVER_URL);
                auth.setUsername(globalSettingDao.getString(GlobalSettingType.UPDATE_SERVER_ADMIN_USER));
                auth.setUsername(globalSettingDao.getString(GlobalSettingType.UPDATE_SERVER_ADMIN_PASSWORD));
            } else {
                updateServerUrl = settings.getUpdateServerUrl();
                auth.setUsername(settings.getUpdateServerLogin().getUsername());
                auth.setUsername(settings.getUpdateServerLogin().getPassword());
            }
            }
            RfnGatewayData.Builder builder = new RfnGatewayData.Builder();
            RfnGatewayData data = builder.copyOf(gateway.getData())
           .ipAddress(settings.getIpAddress())
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
                Map<String, Object> json = new HashMap<>();
                json.put("success", true);
                resp.setContentType("application/json");
                JsonUtils.getWriter().writeValue(resp.getOutputStream(), json);
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "detail.update.successful", settings.getName()));
                
                return null;
            } else {
                resp.setStatus(HttpStatus.BAD_REQUEST.value());
                model.addAttribute("mode", PageEditMode.EDIT);
                String errorMsg = accessor.getMessage(baseKey + "error." + updateResult.name());
                model.addAttribute("errorMsg", errorMsg);
                
                return "gateways/settings.jsp";
            }
            
        } catch (NmCommunicationException e) {
            
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", PageEditMode.EDIT);
            String errorMsg = accessor.getMessage(baseKey + "error.comm");
            model.addAttribute("errorMsg", errorMsg);
            
            return "gateways/settings.jsp";
        }
    }
    
    /** Set schedule popup. 
     * @throws NmCommunicationException */
    @CheckRoleProperty(YukonRoleProperty.INFRASTRUCTURE_ADMIN)
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
    
    /** Set schedule. */ 
    @CheckRoleProperty(YukonRoleProperty.INFRASTRUCTURE_ADMIN)
    @RequestMapping("/gateways/{id}/schedule")
    public String schedule(HttpServletResponse resp, HttpServletRequest req, ModelMap model, FlashScope flash,
            YukonUserContext userContext, @PathVariable int id, String uid) 
            throws JsonGenerationException, JsonMappingException, IOException {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        try {
            String cron = cronService.build(uid, req, userContext);
            LiteYukonPAObject pao = cache.getAllPaosMap().get(id);
            rfnGatewayService.setCollectionSchedule(pao.getPaoIdentifier(), cron);
            
            // Success
            model.clear();
            Map<String, Object> json = new HashMap<>();
            json.put("success", true);
            resp.setContentType("application/json");
            JsonUtils.getWriter().writeValue(resp.getOutputStream(), json);
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "detail.schedule.update.successful"));
            
            return null;
            
        } catch (CronException | NmCommunicationException e) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("id", id);
            model.addAttribute("state", new CronExpressionTagState());
            
            if (e instanceof CronException) {
                model.addAttribute("errorMsg", accessor.getMessage("yukon.common.invalidCron"));
            } else {
                model.addAttribute("errorMsg", accessor.getMessage(baseKey + "error.comm"));
            }
            
            return "gateways/schedule.jsp";
        }
    }
    
    /** Set location popup. */
    @RequestMapping("/gateways/{id}/location/options")
    public String location(ModelMap model, @PathVariable int id) {
        
        Location location = new Location();
        location.setPaoId(id);
        
        PaoLocation paoLocation = paoLocationDao.getLocation(id);
        if (paoLocation != null) {
            location.setLatitude(paoLocation.getLatitude());
            location.setLongitude(paoLocation.getLongitude());
        }
        
        model.addAttribute("location", location);
        
        return "gateways/location.jsp";
    }
    
    /** Set location. */ 
    @RequestMapping("/gateways/{id}/location")
    public String location(HttpServletResponse resp, ModelMap model, YukonUserContext userContext, FlashScope flash,
            @PathVariable int id, @ModelAttribute Location location, BindingResult result)
            throws JsonGenerationException, JsonMappingException, IOException {
        
        GatewaySettingsValidator.validateLocation(location.getLatitude(), location.getLongitude(), result);
        
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            return "gateways/location.jsp";
        }
        LiteYukonPAObject pao = cache.getAllPaosMap().get(id);
        PaoLocation paoLocation = new PaoLocation(pao.getPaoIdentifier(), location.getLatitude(), location.getLongitude());
        paoLocationDao.save(new PaoLocation(pao.getPaoIdentifier(), location.getLatitude(), location.getLongitude()));
        endpointEventLogService.locationUpdated(pao.getPaoIdentifier(), paoLocation, userContext.getYukonUser());
        
        // Success
        model.clear();
        Map<String, Object> json = new HashMap<>();
        json.put("success", true);
        resp.setContentType("application/json");
        JsonUtils.getWriter().writeValue(resp.getOutputStream(), json);
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "detail.location.update.successful"));
        
        return null;
    }
    
    /** Collect data popup. */
    @RequestMapping("/gateways/collect-data/options")
    public String collectData(ModelMap model) {
        
        model.addAttribute("dataTypes", DataType.values());
        
        return "gateways/collect.data.options.jsp";
    }
}