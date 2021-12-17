package com.cannontech.common.rfn.simulation.service.impl;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.model.RfnDeviceDeleteConfirmationReply;
import com.cannontech.common.rfn.model.RfnDeviceDeleteConfirmationReplyType;
import com.cannontech.common.rfn.model.RfnDeviceDeleteInitialReply;
import com.cannontech.common.rfn.model.RfnDeviceDeleteInitialReplyType;
import com.cannontech.common.rfn.model.RfnDeviceDeleteRequest;
import com.cannontech.common.rfn.simulation.SimulatedRfnDeviceDeletionSettings;
import com.cannontech.common.rfn.simulation.service.RfnDeviceDeletionSimulatorService;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;

public class RfnDeviceDeletionSimulatorServiceImpl implements RfnDeviceDeletionSimulatorService {

    private static final Logger log = YukonLogManager.getLogger(RfnDeviceDeletionSimulatorServiceImpl.class);
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    @Autowired private YukonJmsTemplate jmsTemplate;

    private YukonJmsTemplate rfnDeviceDeletionTemplate;
    private YukonJmsTemplate rfnDeviceDeletionConfirmTemplate;
    private SimulatedRfnDeviceDeletionSettings deletionSettings;

    private boolean rfnDeviceDeletionReplyActive;
    private boolean rfnDeviceDeletionReplyStopping;
    public static final Duration incomingMessageWait = Duration.standardSeconds(1);

    @PostConstruct
    public void init() {
        rfnDeviceDeletionTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.RFN_DEVICE_DELETE, incomingMessageWait);
        rfnDeviceDeletionConfirmTemplate = jmsTemplateFactory.createResponseTemplate(JmsApiDirectory.RFN_DEVICE_DELETE);
    }

    @Override
    public void startSimulatorWithCurrentSettings() {
        startRfnDeviceDeletionReply(getDeletionSettings());
    }

    @Override
    public boolean startRfnDeviceDeletionReply(SimulatedRfnDeviceDeletionSettings settings) {
        if (rfnDeviceDeletionReplyActive) {
            return false;
        } else {
            saveDeletionSettings(settings);
            deletionSettings = settings;

            Thread rfnDeviceDeleteThread = getRfnDeviceDeletionThread(settings);
            rfnDeviceDeleteThread.start();
            rfnDeviceDeletionReplyActive = true;
            return true;
        }

    }

    public void saveDeletionSettings(SimulatedRfnDeviceDeletionSettings settings) {
        log.debug("Save RFN_DELETE_DEVICE settings to YukonSimulatorSettings table");
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_DEVICE_DELETION_SIMULATOR_INITIAL_REPLY,
                settings.getInitialReply());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.RFN_DEVICE_DELETION_SIMULATOR_CONFIRMATION_REPLY,
                settings.getConfirmationReply());
    }

    @Override
    public void stopRfnDeviceDeletionReply() {
        if (rfnDeviceDeletionReplyActive) {
            rfnDeviceDeletionReplyStopping = true;
            deletionSettings = null;
        }
    }

    @Override
    public boolean isRfnDeviceDeletionReplyActive() {
        return rfnDeviceDeletionReplyActive;
    }

    @Override
    public boolean isRfnDeviceDeletionReplyStopping() {
        return rfnDeviceDeletionReplyStopping;
    }

    @Override
    public SimulatedRfnDeviceDeletionSettings getDeletionSettings() {
        if (deletionSettings == null) {
            log.debug("Getting RFN_DEVICE_DELETE SimulatorSettings from database.");
            SimulatedRfnDeviceDeletionSettings simulatorSettings = new SimulatedRfnDeviceDeletionSettings();
            simulatorSettings.setInitialReply(RfnDeviceDeleteInitialReplyType.valueOf(yukonSimulatorSettingsDao
                    .getStringValue(YukonSimulatorSettingsKey.RFN_DEVICE_DELETION_SIMULATOR_INITIAL_REPLY)));
            simulatorSettings.setConfirmationReply(RfnDeviceDeleteConfirmationReplyType.valueOf(yukonSimulatorSettingsDao
                    .getStringValue(YukonSimulatorSettingsKey.RFN_DEVICE_DELETION_SIMULATOR_CONFIRMATION_REPLY)));
            deletionSettings = simulatorSettings;
        }
        return deletionSettings;
    }

    private RfnDeviceDeleteInitialReply setUpDeviceDeleteInitialResponse(SimulatedRfnDeviceDeletionSettings settings) {
        RfnDeviceDeleteInitialReply response = new RfnDeviceDeleteInitialReply();
        response.setReplyType(settings.getInitialReply());
        return response;
    }

    private RfnDeviceDeleteConfirmationReply setUpDeviceDeleteConfirmationResponse(SimulatedRfnDeviceDeletionSettings settings,
            ObjectMessage requestMessage) throws JMSException {
        RfnDeviceDeleteConfirmationReply response = new RfnDeviceDeleteConfirmationReply();

        response.setReplyType(settings.getConfirmationReply());
        RfnDeviceDeleteRequest request = (RfnDeviceDeleteRequest) requestMessage.getObject();
        response.setRfnIdentifier(request.getRfnIdentifier());
        return response;
    }

    private Thread getRfnDeviceDeletionThread(SimulatedRfnDeviceDeletionSettings settings) {
        Thread deviceDeleteRunner = new Thread() {
            public void run() {
                while (!rfnDeviceDeletionReplyStopping) {
                    try {
                        Object message = rfnDeviceDeletionTemplate.receive();
                        if (message != null && message instanceof ObjectMessage) {
                            ObjectMessage requestMessage = (ObjectMessage) message;
                            RfnDeviceDeleteInitialReply initialResponse = setUpDeviceDeleteInitialResponse(settings);
                            RfnDeviceDeleteConfirmationReply confirmationResponse = setUpDeviceDeleteConfirmationResponse(
                                    settings, requestMessage);
                            jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), initialResponse);
                            if (settings.getInitialReply() == RfnDeviceDeleteInitialReplyType.OK) {
                                rfnDeviceDeletionConfirmTemplate.convertAndSend(confirmationResponse);
                            }
                        }
                    } catch (Exception e) {
                        log.error("Error occurred while deleting the RF device", e);
                    }
                }
                log.info("RF device deletion thread shutting down.");
                rfnDeviceDeletionReplyActive = false;
                rfnDeviceDeletionReplyStopping = false;
            }
        };
        return deviceDeleteRunner;
    }
}
