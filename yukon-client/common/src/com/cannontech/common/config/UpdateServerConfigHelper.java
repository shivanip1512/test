package com.cannontech.common.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.message.gateway.Authentication;
import com.cannontech.common.rfn.model.GatewayUpdateModel;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
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
            if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER) ||
                    globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER_USER) ||
                    globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER_PASSWORD)) {
                sendNMConfiguration();
            }
        });
    }

    /**
     * Helper method to send NM configuration when the Update Server information is configured in Global
     * Settings
     */
    public void sendNMConfiguration() {
        try {
            Set<RfnGateway> gatewaysToUpdate = rfnGatewayService.getAllGatewaysWithUpdateServer();
            doUpdateGateways(gatewaysToUpdate);
        } catch (NmCommunicationException e) {
            log.error("Failed communication with NM", e);
        }
    }

    /**
     * Helper method to send NM configuration when the NM Gateways being synced do not have any url set
     */
    public void sendNMConfiguration(int paoId) {
        try {
            RfnGateway gateway = rfnGatewayService.getGatewayByPaoIdWithData(paoId);
            Set<RfnGateway> rfnGateways = Collections.singleton(gateway);
            doUpdateGateways(rfnGateways);
        } catch (NmCommunicationException e) {
            log.error("Failed communication with NM", e);
        }
    }

    /**
     * Helper method to send gateway update for a list of gateways.
     */
    private void doUpdateGateways(Set<RfnGateway> gateways) {

        String defaultServer = globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER);
        Authentication defaultAuth = new Authentication();
        defaultAuth.setUsername(globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER_USER));
        defaultAuth.setPassword(globalSettingDao.getString(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER_PASSWORD));
        List<RfnGateway> gatewaysToUpdate = new ArrayList<>();

        try {
            for (RfnGateway gateway : gateways) {
                GatewayUpdateModel updateServer = GatewayUpdateModel.of(gateway);
                String updateServerUrl = updateServer.getUpdateServerUrl();
                boolean isDefault = StringUtils.isEmpty(updateServerUrl) || defaultServer.equals(updateServerUrl);
                if (isDefault) {
                    updateServer.setUseDefault(isDefault);
                    updateServer.setUpdateServerUrl(defaultServer);
                    updateServer.setUpdateServerLogin(defaultAuth);
                    gateway = gateway.withUpdateServer(updateServer);
                    gatewaysToUpdate.add(gateway);
                }
                log.debug("Sending update server request.");
            }
            if (gatewaysToUpdate.size() > 0) {
                rfnGatewayService.updateGateways(gatewaysToUpdate, YukonUserContext.system.getYukonUser());
            }
        } catch (NmCommunicationException e) {
            log.error("Failed communication with NM", e);
        }
    }
}
