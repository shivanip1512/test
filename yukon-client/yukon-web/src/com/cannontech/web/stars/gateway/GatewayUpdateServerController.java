package com.cannontech.web.stars.gateway;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.GatewayEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.dao.impl.GatewayDataException;
import com.cannontech.common.rfn.message.gateway.Authentication;
import com.cannontech.common.rfn.model.GatewayUpdateModel;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayFirmwareUpgradeService;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.util.LazyList;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.ImmutableMap;

@Controller
@CheckRole(YukonRole.INVENTORY)
public class GatewayUpdateServerController {

    private static final Logger log = YukonLogManager.getLogger(GatewayUpdateServerController.class);

    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private RfnGatewayFirmwareUpgradeService rfnFirmwareUpgradeService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private GatewayEventLogService gatewayEventLogService;
    @Autowired private GlobalSettingDao globalSettingDao;
    private static final String baseKey = "yukon.web.modules.operator.gateways.";

    public static class GatewayUpdateModelList {

        private List<GatewayUpdateModel> list = LazyList.ofInstance(GatewayUpdateModel.class);

        public GatewayUpdateModelList() {}

        public GatewayUpdateModelList(List<GatewayUpdateModel> list) {
            setList(list);
        }

        public List<GatewayUpdateModel> getList() {
            return list;
        }

        public void setList(List<GatewayUpdateModel> list) {
            this.list = list;
        }
    }

    @CheckRoleProperty(YukonRoleProperty.INFRASTRUCTURE_CREATE_AND_UPDATE)
    @RequestMapping("/gateways/update-servers")
    public String editUpdateServers(ModelMap model, YukonUserContext userContext) {

        String defaultServer = globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER);

        model.addAttribute("mode", PageEditMode.EDIT);

        List<GatewayUpdateModel> allGateways = Collections.emptyList();
        try {
            allGateways = rfnGatewayService.getAllGatewaysWithUpdateServer()
                .stream()
                .sorted()
                .map(new Function<RfnGateway, GatewayUpdateModel>() {

                        @Override
                        public GatewayUpdateModel apply(RfnGateway gateway) {
                            GatewayUpdateModel updateServer = GatewayUpdateModel.of(gateway);
                            String updateServerUrl = updateServer.getUpdateServerUrl();
                            boolean isDefault = StringUtils.isEmpty(updateServerUrl) || 
                                        defaultServer.equals(updateServerUrl);
                            updateServer.setUseDefault(isDefault);

                            return updateServer;
                        }
                    }).collect(Collectors.toList());
        } catch (NmCommunicationException e) {
            log.error("Failed communication with NM", e);
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

            String errorMsg = accessor.getMessage(baseKey + "error.comm");
            model.addAttribute("errorMsg", errorMsg);
        }


        model.addAttribute("allSettings", new GatewayUpdateModelList(allGateways));

        String defaultUpdateServer = globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER);

        model.addAttribute("defaultUpdateServer", defaultUpdateServer);

        return "gateways/update-servers.jsp";
    }

    @CheckRoleProperty(YukonRoleProperty.INFRASTRUCTURE_CREATE_AND_UPDATE)
    @RequestMapping(value="/gateways/update-servers", method=RequestMethod.POST)
    public String saveUpdateServers(
        ModelMap model,
        HttpServletResponse resp,
        @ModelAttribute("allSettings") GatewayUpdateModelList allSettings,
        YukonUserContext userContext ) {

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

        String defaultServer = globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER);
        Authentication defaultAuth = new Authentication();
        defaultAuth.setUsername(globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER_USER));
        defaultAuth.setPassword(globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER_PASSWORD));

        try {
            List<GatewayUpdateModel> updateServerInfos = allSettings.getList();

            List<RfnGateway> gateways = new ArrayList<>();

            for (GatewayUpdateModel updateServerInfo : updateServerInfos) {
                if (updateServerInfo.isUseDefault()) {
                    updateServerInfo.setUpdateServerUrl(defaultServer);
                    updateServerInfo.setUpdateServerLogin(defaultAuth);
                }

                RfnGateway gateway = rfnGatewayService.getGatewayByPaoIdWithData(updateServerInfo.getId());
                gateway = gateway.withUpdateServer(updateServerInfo);
                gateways.add(gateway);
            }

            rfnGatewayService.updateGateways(gateways, userContext.getYukonUser());

        } catch (NmCommunicationException e) {

            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", PageEditMode.EDIT);
            String errorMsg = accessor.getMessage(baseKey + "error.comm");
            model.addAttribute("errorMsg", errorMsg);

            return "gateways/update-servers.jsp";
        }

        // Success
        model.clear();
        String message = accessor.getMessage(baseKey + "updateServer.success");
        Map<String, Object> json = new HashMap<>();
        json.put("success", true);
        json.put("message", message);
        return JsonUtils.writeResponse(resp, json);
    }

    @CheckRoleProperty(YukonRoleProperty.INFRASTRUCTURE_CREATE_AND_UPDATE)
    @RequestMapping(value = "/gateways/firmware-upgrade", method=RequestMethod.GET)
    public String firmwareUpgrade(ModelMap model, YukonUserContext userContext) {

        try {
            List<GatewayUpdateModel> gateways =
                rfnGatewayService.getAllGatewaysWithUpdateServer().stream()
                    .sorted()
                    .map(new Function<RfnGateway, GatewayUpdateModel>() {

                        @Override
                        public GatewayUpdateModel apply(RfnGateway gateway) {
                            return GatewayUpdateModel.of(gateway);
                        }
                    }).collect(Collectors.toList());

            GatewayUpdateModelList springGateways = new GatewayUpdateModelList(gateways);
            model.addAttribute("gateways", springGateways);

        } catch (NmCommunicationException e) {

            log.error("Failed communication with NM", e);
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

            String errorMsg = accessor.getMessage(baseKey + "error.comm");

            model.addAttribute("errorMsg", errorMsg);
        }

        return "gateways/firmware-upgrade.jsp";
    }

    @CheckRoleProperty(YukonRoleProperty.INFRASTRUCTURE_CREATE_AND_UPDATE)
    @RequestMapping(value = "/gateways/firmware-upgrade", method=RequestMethod.POST)
    public String sendFirmwareUpgrade(
        HttpServletResponse resp,
        GatewayUpdateModelList modelList,
        ModelMap model,
        YukonUserContext userContext) {

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

        List<GatewayUpdateModel> allGatewayModels = modelList.getList();

        Set<Integer> idsToUpdate = 
            allGatewayModels.stream()
           .filter(new Predicate<GatewayUpdateModel>() {
                @Override
                public boolean test(GatewayUpdateModel updateModel) {
                    return updateModel.isSendNow();
                }
            }).map(new Function<GatewayUpdateModel, Integer> () {

                @Override
                public Integer apply(GatewayUpdateModel updateModel) {
                    return updateModel.getId();
                }
            })
            .collect(Collectors.toSet());

        Set<RfnGateway> gateways = rfnGatewayService.getGatewaysByPaoIds(idsToUpdate);

        try {
            int updateId = rfnFirmwareUpgradeService.sendFirmwareUpgrade(gateways);
            gatewayEventLogService.sentFirmwareUpdate(userContext.getYukonUser(), idsToUpdate.size());

            String message = accessor.getMessage(baseKey + "firmwareUpdate.success");
            Map<String, Object> json = ImmutableMap.of(
                "success", true,
                "message", message,
                "updateId", updateId);
            return JsonUtils.writeResponse(resp, json);

        } catch (GatewayDataException e) {

            log.error("Unable to update gateways", e);
            resp.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

            String errorMsg = accessor.getMessage(baseKey + "error.comm");
            model.addAttribute("errorMsg", errorMsg);

            model.addAttribute("gateways", modelList);

            return "gateways/firmware-upgrade.jsp";
        }
    }

}
