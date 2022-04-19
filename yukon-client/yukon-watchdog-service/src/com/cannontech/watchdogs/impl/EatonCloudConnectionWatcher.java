package com.cannontech.watchdogs.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.dr.eatonCloud.message.EatonCloudHeartbeatRequest;
import com.cannontech.dr.eatonCloud.message.EatonCloudHeartbeatResponse;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.cannontech.watchdog.service.WatchdogWatcherService;

@Service
public class EatonCloudConnectionWatcher extends ServiceStatusWatchdogImpl {

    private static final Logger log = YukonLogManager.getLogger(EatonCloudConnectionWatcher.class);
    
    @Autowired private WatchdogWatcherService watcherService;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    @Autowired private ConfigurationSource configSource;
    private RequestReplyTemplate<EatonCloudHeartbeatResponse> heartbeatTemplate;
    
    public EatonCloudConnectionWatcher(ConfigurationSource configSource) {
        super(configSource);
    }
    
    @Override
    public List<WatchdogWarnings> watch() {
        if (heartbeatTemplate == null) {
            YukonJmsTemplate jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.EATON_CLOUD_HEARTBEAT);
            heartbeatTemplate = new RequestReplyTemplateImpl<>(JmsApiDirectory.EATON_CLOUD_HEARTBEAT.getName(),
                    configSource, jmsTemplate);
        }
        try {
            log.debug("Sending message to verify connection between Eaton Cloud and Service Manager");
            BlockingJmsReplyHandler<EatonCloudHeartbeatResponse> reply = new BlockingJmsReplyHandler<>(
                    EatonCloudHeartbeatResponse.class);
            heartbeatTemplate.send(new EatonCloudHeartbeatRequest(), reply);
            EatonCloudHeartbeatResponse response = reply.waitForCompletion();
            if (response.hasError()) {
                log.error("Communication error between Service Manager and Eaton Cloud:{} status:{}",
                        response.getError(), ServiceStatus.STOPPED);
                return generateWarning(WatchdogWarningType.YUKON_EATON_CLOUD, ServiceStatus.STOPPED);
            }
            log.info("Connection between Eaton Cloud and Service Manager verified status:{}", ServiceStatus.RUNNING);
            return generateWarning(WatchdogWarningType.YUKON_EATON_CLOUD, ServiceStatus.RUNNING);
        } catch (Exception e) {
            log.error("Communication error status:{}", ServiceStatus.STOPPED, e);
            return generateWarning(WatchdogWarningType.YUKON_EATON_CLOUD, ServiceStatus.STOPPED);
        }
    }

    @Override
    public YukonServices getServiceName() {
        return YukonServices.EATON_CLOUD;
    }

    @Override
    public boolean shouldRun() {
        return watcherService.isServiceRequired(getServiceName());
    }
    

    @Override
    public void start() {
        executor.scheduleAtFixedRate(this::doWatchAction, 1, 15, TimeUnit.MINUTES);
    }
}