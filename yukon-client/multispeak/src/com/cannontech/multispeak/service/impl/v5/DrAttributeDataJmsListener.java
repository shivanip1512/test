package com.cannontech.multispeak.service.impl.v5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.core.dynamic.RichPointDataListener;
import com.cannontech.multispeak.service.DrJmsMessageService;
import com.cannontech.stars.dr.jms.message.DrAttributeData;
import com.cannontech.stars.dr.jms.message.DrAttributeDataJmsMessage;
import com.cannontech.stars.dr.jms.message.DrJmsMessage;
import com.cannontech.stars.dr.jms.message.DrJmsMessageType;
import com.google.common.collect.Sets;

public class DrAttributeDataJmsListener implements RichPointDataListener {
    @Autowired private DrJmsMessageService drJmsMessageService;
    @Autowired private AttributeService attributeService;
    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;

    private static Set<BuiltInAttribute> attributes = Sets.union(
            Sets.union(BuiltInAttribute.getVoltageAttributes(), BuiltInAttribute.getRelayDataAttributes()),
            BuiltInAttribute.getItronLcrAttributes());
    private static Set<PaoType> supportedPaoTypes = Sets.union(PaoType.getItronTypes(), PaoType.getTwoWayLcrTypes());

    private static final int POINT_DATA_TO_SEND_AT_ONCE = 100;
    List<DrAttributeDataJmsMessage> relayDataAvailable = Collections.synchronizedList(new ArrayList<>());
    List<DrAttributeDataJmsMessage> voltageDataAvailable = Collections.synchronizedList(new ArrayList<>());
    List<DrAttributeDataJmsMessage> alarmDataAvailable = Collections.synchronizedList(new ArrayList<>());
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

    private static final Logger log = YukonLogManager.getLogger(DrAttributeDataJmsListener.class);

    @PostConstruct
    public void startSchedulers() {
        log.debug("Started schedulers to push data");
        scheduler.scheduleAtFixedRate(() -> {
            sendRelayData();
        }, 10, 10, TimeUnit.SECONDS);

        scheduler.scheduleAtFixedRate(() -> {
            sendVoltageData();
        }, 10, 10, TimeUnit.SECONDS);

        scheduler.scheduleAtFixedRate(() -> {
            sendAlarmData();
        }, 10, 10, TimeUnit.SECONDS);
    }

    @Override
    public void pointDataReceived(RichPointData richPointData) {
        if (supportedPaoTypes.contains(richPointData.getPaoPointIdentifier().getPaoIdentifier().getPaoType())) {
            Set<BuiltInAttribute> supportedAttributes = attributeService.findAttributesForPoint(
                    richPointData.getPaoPointIdentifier().getPaoTypePointIdentifier(),
                    attributes);
            if (!supportedAttributes.isEmpty()) {
                DrAttributeDataJmsMessage attributeDataJmsMessage = new DrAttributeDataJmsMessage();
                attributeDataJmsMessage.setPaoPointIdentifier(richPointData.getPaoPointIdentifier());

                List<DrAttributeData> attributeDataList = new ArrayList<>();
                for (BuiltInAttribute attribute : supportedAttributes) {
                    DrAttributeData attributeData = new DrAttributeData();

                    attributeData.setAttribute(attribute);
                    attributeData.setTimeStamp(richPointData.getPointValue().getPointDataTimeStamp());
                    attributeData.setValue(richPointData.getPointValue().getValue());

                    attributeDataList.add(attributeData);

                }
                attributeDataJmsMessage.setAttributeDataList(attributeDataList);

                setMessageTypeForAttribute(supportedAttributes, attributeDataJmsMessage);

                switch (attributeDataJmsMessage.getMessageType()) {
                case RELAYDATA:
                    relayDataAvailable.add(attributeDataJmsMessage);
                    break;
                case VOLTAGEDATA:
                    voltageDataAvailable.add(attributeDataJmsMessage);
                    break;
                case ALARMANDEVENT:
                    alarmDataAvailable.add(attributeDataJmsMessage);
                    break;
                default:
                    log.debug("Unable to find proper multispeak Dr message type i.e: "
                            + attributeDataJmsMessage.getMessageType());
                    break;
                }
            }
        }
    }

    /**
     * Push data to generate multispeak messages
     */
    private <T extends DrJmsMessage> void sendData(List<T> messageList, Consumer<List<T>> messageConsumer,
            DrJmsMessageType dataType) {
        try {
            if (!messageList.isEmpty()) {
                ArrayList<T> dataToSend = new ArrayList<>();
                synchronized (messageList) {
                    dataToSend.addAll(messageList);
                    messageList.clear();
                }
                log.debug("Sending " + dataType + " data " + dataToSend.size());
                while (!dataToSend.isEmpty()) {
                    ArrayList<T> sendNow;
                    if (dataToSend.size() >= POINT_DATA_TO_SEND_AT_ONCE) {
                        sendNow = new ArrayList<>(dataToSend.subList(0, POINT_DATA_TO_SEND_AT_ONCE));
                        dataToSend.subList(0, POINT_DATA_TO_SEND_AT_ONCE).clear();
                    } else {
                        sendNow = new ArrayList<>(dataToSend.subList(0, dataToSend.size()));
                        dataToSend.subList(0, dataToSend.size()).clear();
                    }
                    new Thread(() -> {
                        messageConsumer.accept(sendNow);
                    }).start();
                }
            }
        } catch (Exception e) {
            log.error("Exception while sending data " + e);
        }
    }

    /**
     * Sending relay data.
     */
    private void sendRelayData() {
        sendData(relayDataAvailable, drJmsMessageService::intervalDataNotification, DrJmsMessageType.RELAYDATA);
    }

    /**
     * Sending voltage data.
     */
    private void sendVoltageData() {
        sendData(voltageDataAvailable, drJmsMessageService::voltageMeterReadingsNotification, DrJmsMessageType.VOLTAGEDATA);
    }

    /**
     * Sending alarm data.
     */
    private void sendAlarmData() {
        sendData(alarmDataAvailable, drJmsMessageService::alarmAndEventNotification, DrJmsMessageType.ALARMANDEVENT);
    }

    /*
     * Set message type based on Attributes.
     */
    private void setMessageTypeForAttribute(Set<BuiltInAttribute> supportedAttributes,
            DrAttributeDataJmsMessage attributeDataJmsMessage) {

        boolean isVoltageAttribute = BuiltInAttribute.getVoltageAttributes().stream()
                .anyMatch(attributeType -> supportedAttributes.contains(attributeType));

        boolean isRelayAttribute = BuiltInAttribute.getRelayDataAttributes().stream()
                .anyMatch(attributeType -> supportedAttributes.contains(attributeType));

        if (isVoltageAttribute) {
            attributeDataJmsMessage.setMessageType(DrJmsMessageType.VOLTAGEDATA);
        } else if (isRelayAttribute) {
            attributeDataJmsMessage.setMessageType(DrJmsMessageType.RELAYDATA);
        } else {
            attributeDataJmsMessage.setMessageType(DrJmsMessageType.ALARMANDEVENT);
        }
    }

}
