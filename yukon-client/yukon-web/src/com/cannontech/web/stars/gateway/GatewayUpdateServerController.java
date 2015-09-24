package com.cannontech.web.stars.gateway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.GatewayEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.message.gateway.Authentication;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResult;
import com.cannontech.common.rfn.model.GatewaySettings;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;

@Controller
@CheckRole(YukonRole.INVENTORY)
public class GatewayUpdateServerController {

    private static final Logger log = YukonLogManager.getLogger(GatewayUpdateServerController.class);

    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private GatewayEventLogService gatewayEventLogService;
    @Autowired private GlobalSettingDao globalSettingDao;
    private static final String baseKey = "yukon.web.modules.operator.gateways.";

    /** Show Update Server (All) popup. */
    @CheckRoleProperty(YukonRoleProperty.INFRASTRUCTURE_CREATE_AND_UPDATE)
    @RequestMapping("/gateways/update-server/option")
    public String updateServer(ModelMap model) {

        List<RfnGateway> gateways = Lists.newArrayList(rfnGatewayService.getAllGateways());
        List<GatewaySettings> gatewaysSettings = createGatewaySettings(gateways);
        model.addAttribute("gateways", gatewaysSettings);

        return "gateways/updateAllServer.jsp";
    }

    /** Update the update server url */
    @CheckRoleProperty(YukonRoleProperty.INFRASTRUCTURE_CREATE_AND_UPDATE)
    @RequestMapping(value = "/gateways/updateServer")
    public String updateUpdateServer(ModelMap model, YukonUserContext userContext,
            HttpServletResponse resp, @ModelAttribute("settings") GatewaySettings[] settings) {

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        Map<Integer, String> failedUpdate = new HashMap<Integer, String>();

        try {
            for (GatewaySettings setting : settings) {

                RfnGateway gateway = rfnGatewayService.getGatewayByPaoIdWithData(setting.getId());

                RfnGatewayData.Builder builder = new RfnGatewayData.Builder();
                RfnGatewayData data = builder.copyOf(gateway.getData())
                                             .name(setting.getName())
                                             .updateServerUrl(setting.getUpdateServerUrl())
                                             .updateServerLogin(setting.getUpdateServerLogin())
                                             .build();

                gateway.setData(data);

                GatewayUpdateResult updateResult = rfnGatewayService.updateGateway(gateway,
                                                                                   userContext.getYukonUser());

                if (updateResult == GatewayUpdateResult.SUCCESSFUL) {
                    log.info("Gateway update server updated: " + gateway);
                    gatewayEventLogService.updatedGateway(userContext.getYukonUser(),
                                                          gateway.getName(),
                                                          gateway.getRfnIdentifier()
                                                                 .getSensorSerialNumber(),
                                                          setting.getIpAddress(),
                                                          setting.getAdmin().getUsername(),
                                                          setting.getSuperAdmin().getUsername());
                } else {

                    resp.setStatus(HttpStatus.BAD_REQUEST.value());
                    model.addAttribute("mode", PageEditMode.EDIT);
                    String errorMsg = accessor.getMessage(baseKey + "error." + updateResult.name());
                    failedUpdate.put(gateway.getPaoIdentifier().getPaoId(), errorMsg);

                }
            }
        } catch (NmCommunicationException e) {

            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", PageEditMode.EDIT);
            String errorMsg = accessor.getMessage(baseKey + "error.comm");
            model.addAttribute("errorMsg", errorMsg);

            return "gateways/updateAllServer.jsp";
        }
        model.addAttribute("errorMsg", failedUpdate);
        return "gateways/updateAllServer.jsp";

    }

    /* Converts RfnGatway object to GatewaySettings object */
    private List<GatewaySettings> createGatewaySettings(List<RfnGateway> gateways) {

        List<GatewaySettings> gatewaySettings = new ArrayList<GatewaySettings>();

        for (RfnGateway gateway : gateways) {
            GatewaySettings settings = new GatewaySettings();
            settings.setId(gateway.getPaoIdentifier().getPaoId());
            settings.setName(gateway.getName());
            settings.setName(gateway.getRfnIdentifier().getSensorSerialNumber());
            settings.setName(gateway.getData().getReleaseVersion());
            if (gateway.getData().getUpdateServerUrl() == null) {
                settings.setUpdateServerUrl(globalSettingDao.getString(GlobalSettingType.UPDATE_SERVER_URL));
            } else {
                settings.setUpdateServerUrl(gateway.getData().getUpdateServerUrl());
            }

            if (gateway.getData().getUpdateServerLogin() == null) {
                Authentication updateServerAuth = new Authentication();
                updateServerAuth.setUsername(globalSettingDao.getString(GlobalSettingType.UPDATE_SERVER_ADMIN_USER));
                updateServerAuth.setPassword(globalSettingDao.getString(GlobalSettingType.UPDATE_SERVER_ADMIN_PASSWORD));
                settings.setUpdateServerLogin(updateServerAuth);
            } else {
                settings.setUpdateServerLogin(gateway.getData().getUpdateServerLogin());
            }
            gatewaySettings.add(settings);
        }
        return gatewaySettings;
    }
}
