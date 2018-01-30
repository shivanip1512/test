package com.cannontech.web.common.widgets.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigInteger;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.infrastructure.dao.InfrastructureWarningsDao;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningSummary;
import com.cannontech.infrastructure.model.InfrastructureWarningsRefreshRequest;
import com.cannontech.infrastructure.model.InfrastructureWarningsRequest;
import com.cannontech.web.common.widgets.service.InfrastructureWarningsWidgetService;

public class InfrastructureWarningsWidgetServiceImpl implements InfrastructureWarningsWidgetService, MessageListener {

    private static final Logger log = YukonLogManager.getLogger(InfrastructureWarningsWidgetServiceImpl.class);
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private PersistedSystemValueDao persistedSystemValueDao;
    @Autowired private InfrastructureWarningsDao infrastructureWarningsDao;
    private JmsTemplate jmsTemplate;
    private static List<InfrastructureWarning> cachedWarnings;
    private static InfrastructureWarningSummary cachedSummary;
    private boolean refreshWarnings;
    private boolean refreshSummary;
    private Instant nextRunTime;
    private Instant lastRunTime;
    private static int minimumTimeBetweenRuns = 5;
        
    @PostConstruct
    public void init() {
        minimumTimeBetweenRuns = configurationSource.getInteger(
            MasterConfigInteger.INFRASTRUCTURE_WARNING_MINIMUM_TIME_BETWEEN_RUNS, minimumTimeBetweenRuns);
        lastRunTime = persistedSystemValueDao.getInstantValue(PersistedSystemValueKey.INFRASTRUCTURE_WARNINGS_LAST_RUN_TIME);
        if (lastRunTime == null) {
            lastRunTime = Instant.now();
        }
        nextRunTime = lastRunTime.toDateTime().plusMinutes(minimumTimeBetweenRuns).toInstant();
        refreshWarnings = true;
        refreshSummary = true;
    }
    
    @Override
    public Instant getRunTime(boolean nextTime) {
        if (nextTime) {
            return nextRunTime;
        } else {
            return lastRunTime;
        }
    }

    @Override
    public InfrastructureWarningSummary getWarningsSummary() {
        if(refreshSummary) {
            refreshSummary = false;
            cachedSummary = infrastructureWarningsDao.getWarningsSummary();
            cachedSummary.setLastRun(lastRunTime);
            log.debug("Summary is refreshed." );
        }
        return cachedSummary;
    }

    @Override
    public List<InfrastructureWarning> getWarnings() {
        if(refreshWarnings) {
            refreshWarnings = false;
            cachedWarnings = infrastructureWarningsDao.getWarnings();
            log.debug("Warnings are refreshed." );
        }
        return cachedWarnings;
    }
    
    @Override
    public void initiateRecalculation() {
        log.info("Manually initiating a recalculation of infrastructure warnings.");
        jmsTemplate.convertAndSend(JmsApiDirectory.INFRASTRUCTURE_WARNINGS.getQueue().getName(),
            new InfrastructureWarningsRequest());
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                if (objMessage.getObject() instanceof InfrastructureWarningsRefreshRequest) {
                    InfrastructureWarningsRefreshRequest request =
                        (InfrastructureWarningsRefreshRequest) objMessage.getObject();
                    nextRunTime = request.getNextRunTime();
                    lastRunTime = request.getLastRunTime();
                    refreshWarnings = true;
                    refreshSummary = true;
                    log.debug("Recieved Cache Refresh Request (InfrastructureWarningsRefreshRequest)" );
                    log.debug("lastRunTime=" + lastRunTime.toDateTime().toString("MM/dd/yyyy HH:mm:ss"));
                    log.debug("nextRunTime=" + nextRunTime.toDateTime().toString("MM/dd/yyyy HH:mm:ss"));
                }
            } catch (Exception e) {
                log.error("Unable to process message", e);
            }
        }
    }
        
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setDeliveryPersistent(false);
    }  
}
