package com.cannontech.simulators.service;

import static com.cannontech.common.stream.StreamUtils.not;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.rfn.simulation.service.DataStreamingSimulatorService;
import com.cannontech.common.rfn.simulation.service.NmNetworkSimulatorService;
import com.cannontech.common.rfn.simulation.service.RfnGatewaySimulatorService;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.dr.rfn.service.RfnLcrDataSimulatorService;
import com.cannontech.dr.rfn.service.RfnMeterDataSimulatorService;
import com.cannontech.dr.rfn.service.RfnMeterReadAndControlSimulatorService;
import com.cannontech.simulators.AutoStartableSimulator;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.handler.SimulatorMessageHandler;
import com.cannontech.simulators.ivvc.IvvcSimulatorService;
import com.cannontech.simulators.startup.service.SimulatorStartupSettingsService;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.ImmutableMap;

/**
 * Entry point for the simulators service. This is a testing service that simulates external systems
 * and hardware that
 * Yukon communicates with, most notably Network Manager.
 */
public class SimulatorsService {
    //  The logger creation needs to be delayed in order for setCtiAppName to run before YukonLogManager is initialized.
    //      See https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
    private static class LogHolder {
        static final Logger log = YukonLogManager.getLogger(SimulatorsService.class);
    }

    @Autowired private ConfigurationSource configSource;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
    @Autowired private SimulatorStartupSettingsService simulatorStartupSettingsService;
    @Autowired private RfnGatewaySimulatorService rfnGatewaySimulatorService;
    @Autowired private RfnLcrDataSimulatorService rfnLcrDataSimulatorService;
    @Autowired private DataStreamingSimulatorService dataStreamingSimulatorService;
    @Autowired private NmNetworkSimulatorService nmNetworkSimulatorService;
    @Autowired private RfnMeterDataSimulatorService rfnMeterDataSimulatorService;
    @Autowired private IvvcSimulatorService ivvcSimulatorService;
    @Autowired private RfnMeterReadAndControlSimulatorService rfnMeterReadAndControlSimulatorService;
    @Autowired private Set<SimulatorMessageHandler> messageHandlers;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;

    private SimulatorMessageListener messageListener;
    private ImmutableMap<SimulatorType, AutoStartableSimulator> simulatorTypeToSimulator;
    public static final Duration incomingMessageWait = Duration.standardSeconds(1);

    /**
     * Gets this simulators service as a Spring bean and starts it.
     */
    public static void main(String[] args) {
        try {
            CtiUtilities.setCtiAppName(ApplicationId.SIMULATORS_SERVICE);
            getLogger().info("Starting simulators service from main method");
            YukonSpringHook.setDefaultContext(YukonSpringHook.SIMULATORS_BEAN_FACTORY_KEY);

            SimulatorsService service = YukonSpringHook.getBean(SimulatorsService.class);
            service.start();

            synchronized (service) {
                while (service.isRunning()) {
                    service.wait();
                }
            }
        } catch (Throwable t) {
            getLogger().error("Error in simulators service", t);
            System.exit(1);
        }
    }

    private static Logger getLogger() {
        return LogHolder.log;
    }

    private synchronized void start() {
        var jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.SIMULATORS, incomingMessageWait);
        messageListener = new SimulatorMessageListener(jmsTemplate, messageHandlers);
        messageListener.start();
        autoStartSimulators();
        getLogger().info("Started simulators service.");
    }

    private void autoStartSimulators() {
        if (configSource.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE)) {
            yukonSimulatorSettingsDao.initYukonSimulatorSettings();
            Arrays.stream(SimulatorType.values())
                .filter(not(SimulatorType.SIMULATOR_STARTUP::equals))
                .filter(not(SimulatorType.SMART_NOTIFICATION::equals))
                .filter(simulatorStartupSettingsService::isRunOnStartup)
                .map   (simulatorTypeToSimulator::get)
                .filter(Objects::nonNull)
                .forEach(AutoStartableSimulator::startSimulatorWithCurrentSettings);
        }
    }

    @PostConstruct
    public void init() {
        simulatorTypeToSimulator = new ImmutableMap.Builder<SimulatorType, AutoStartableSimulator>()
            .put(SimulatorType.GATEWAY, rfnGatewaySimulatorService)
            .put(SimulatorType.DATA_STREAMING, dataStreamingSimulatorService)
            .put(SimulatorType.RFN_METER, rfnMeterDataSimulatorService)
            .put(SimulatorType.RFN_NETWORK, nmNetworkSimulatorService)
            .put(SimulatorType.IVVC, ivvcSimulatorService)
            .put(SimulatorType.RFN_LCR, rfnLcrDataSimulatorService)
            .put(SimulatorType.RFN_METER_READ_CONTROL, rfnMeterReadAndControlSimulatorService)
            .build();
    }

    @PreDestroy
    public synchronized void shutdown() {
        try {
            messageListener.stop();
            notify();
            getLogger().info("Stopped simulators service.");
        } catch (Exception e) {
            getLogger().error("Error while stopping simulators service: ", e);
            System.exit(1);
        }
    }

    private boolean isRunning() {
        return messageListener.isActive();
    }
}