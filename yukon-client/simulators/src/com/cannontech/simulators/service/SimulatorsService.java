package com.cannontech.simulators.service;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.dr.rfn.model.SimulatorSettings;
import com.cannontech.dr.rfn.service.impl.RfnMeterDataSimulatorServiceImpl;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.handler.SimulatorMessageHandler;
import com.cannontech.simulators.startup.service.SimulatorStartupSettingsService;
import com.cannontech.spring.YukonSpringHook;

/**
 * Entry point for the simulators service. This is a testing service that simulates external systems and hardware that 
 * Yukon communicates with, most notably Network Manager.
 */
public class SimulatorsService {
    private static final Logger log = YukonLogManager.getLogger(SimulatorsService.class);
    private static final int incomingMessageWaitMillis = 1000;
    
    @Autowired private ConfigurationSource configSource;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
    @Autowired private SimulatorStartupSettingsService simulatorStartupSettingsService;
    @Autowired private RfnMeterDataSimulatorServiceImpl rfnMeterDataSimulatorServiceImpl;
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private Set<SimulatorMessageHandler> messageHandlers;
    private SimulatorMessageListener messageListener;
    private JmsTemplate jmsTemplate;
    
    /**
     * Gets this simulators service as a Spring bean and starts it.
     */
    public static void main(String[] args) {
        try {
            System.setProperty("cti.app.name", "SimulatorsService");
            log.info("Starting simulators service from main method");
            YukonSpringHook.setDefaultContext(YukonSpringHook.SIMULATORS_BEAN_FACTORY_KEY);
            
            SimulatorsService service = YukonSpringHook.getBean(SimulatorsService.class);
            service.start();
            
            synchronized (service) {
                while (service.isRunning()) {
                    service.wait();
                }
            }
        } catch (Throwable t) {
            log.error("Error in simulators service", t);
            System.exit(1);
        }
    }
    
    private synchronized void start() {
        messageListener = new SimulatorMessageListener(jmsTemplate, messageHandlers);
        messageListener.start();
        autoStartSimulators();
        log.info("Started simulators service.");
    }
    
    private void autoStartSimulators() {
        if (configSource.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE)) {
            yukonSimulatorSettingsDao.initYukonSimulatorSettings();
            for (SimulatorType simType : SimulatorType.values()) {
                if (simType == SimulatorType.RFN_METER) {
                    SimulatorSettings settings = rfnMeterDataSimulatorServiceImpl.getCurrentSettings();
                    if (simulatorStartupSettingsService.getRunOnStartup(simType)) {
                        rfnMeterDataSimulatorServiceImpl.startSimulator(settings);
                    }
                }
            }
        }
    }
    
    @PostConstruct
    public void init() {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setReceiveTimeout(incomingMessageWaitMillis); //TODO: does this need to be set?
    }
    
    @PreDestroy
    public synchronized void shutdown() {
        try {
            messageListener.stop();
            notify();
            log.info("Stopped simulators service.");
        } catch (Exception e) {
            log.error("Error while stopping simulators service: ", e);
            System.exit(1);
        }
    }
    
    private boolean isRunning() {
        return messageListener.isActive();
    }
}