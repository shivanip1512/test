package com.cannontech.common.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.message.gateway.Authentication;
import com.cannontech.common.rfn.model.GatewayUpdateModel;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;

public class UpdateServerConfigHelper {
    private final static Logger log = YukonLogManager.getLogger(UpdateServerConfigHelper.class);
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private GlobalSettingDao globalSettingDao;

    @PostConstruct
    public void init() {
        asyncDynamicDataSource.addDatabaseChangeEventListener(event -> {

            if ((event.getChangeCategory() == DbChangeCategory.GLOBAL_SETTING)
                && (event.getPrimaryKey() == globalSettingDao.getSetting(
                    GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER).getId()
                    || event.getPrimaryKey() == globalSettingDao.getSetting(
                        GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER_USER).getId()
                    || event.getPrimaryKey() == globalSettingDao.getSetting(
                        GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER_PASSWORD).getId())) {
                sendNMConfiguration();
            }
        });
    }

    /**
     * Helper method to send NM configuration when the Update Server information is configured in Global
     * Settings
     */
    public void sendNMConfiguration() {
        String defaultServer = globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER);
        Authentication defaultAuth = new Authentication();
        defaultAuth.setUsername(globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER_USER));
        defaultAuth.setPassword(globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER_PASSWORD));
        List<GatewayUpdateModel> updateServerInfos = Collections.emptyList();
        try {
            updateServerInfos = rfnGatewayService.getAllGatewaysWithUpdateServer().stream().map(gateway -> {
                GatewayUpdateModel updateServer = GatewayUpdateModel.of(gateway);
                String updateServerUrl = updateServer.getUpdateServerUrl();
                boolean isDefault = StringUtils.isEmpty(updateServerUrl) || defaultServer.equals(updateServerUrl);
                updateServer.setUseDefault(isDefault);

                return updateServer;
            }).collect(Collectors.toList());

            List<RfnGateway> gateways = new ArrayList<>();

            for (GatewayUpdateModel updateServerInfo : updateServerInfos) {
                if (updateServerInfo.isUseDefault()) {
                    updateServerInfo.setUpdateServerUrl(defaultServer);
                    updateServerInfo.setUpdateServerLogin(defaultAuth);
                    RfnGateway gateway = rfnGatewayService.getGatewayByPaoIdWithData(updateServerInfo.getId());
                    gateway = gateway.withUpdateServer(updateServerInfo);
                    gateways.add(gateway);
                }
            }
            log.debug("Sending update server request.");
            if (gateways.size() > 0) {
                rfnGatewayService.updateGateways(gateways, YukonUserContext.system.getYukonUser());
            }
        } catch (NmCommunicationException e) {
            log.error("Failed communication with NM", e);
        }
    }

    /**
     * Helper method to send NM configuration when the NM Gateways being synced do not have any url set
     */
    public void sendNMConfiguration(int paoId) {
        String defaultServer = globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER);
        Authentication defaultAuth = new Authentication();
        defaultAuth.setUsername(globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER_USER));
        defaultAuth.setPassword(globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER_PASSWORD));
        try {
            List<RfnGateway> gateways = new ArrayList<>();
            RfnGateway gateway = rfnGatewayService.getGatewayByPaoIdWithData(paoId);
            GatewayUpdateModel updateServer = GatewayUpdateModel.of(gateway);
            String updateServerUrl = updateServer.getUpdateServerUrl();
            boolean isDefault = StringUtils.isEmpty(updateServerUrl) || defaultServer.equals(updateServerUrl);
            if (isDefault) {
                updateServer.setUseDefault(isDefault);
                updateServer.setUpdateServerUrl(defaultServer);
                updateServer.setUpdateServerLogin(defaultAuth);
                gateway = gateway.withUpdateServer(updateServer);
                gateways.add(gateway);
            }
            log.debug("Sending update server request.");
            rfnGatewayService.updateGateways(gateways, YukonUserContext.system.getYukonUser());
        } catch (NmCommunicationException e) {
            log.error("Failed communication with NM", e);
        }
    }
}
