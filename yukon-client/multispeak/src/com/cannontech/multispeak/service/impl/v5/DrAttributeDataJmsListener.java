package com.cannontech.multispeak.service.impl.v5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
    private static final Logger log = YukonLogManager.getLogger(DrAttributeDataJmsListener.class);

    @PostConstruct
    public void startSchedulers() {
        log.debug("Started schedulers to push data");
        executor.scheduleAtFixedRate(() -> {
            sendRelayData();
        }, 10, 10, TimeUnit.SECONDS);

        executor.scheduleAtFixedRate(() -> {
            sendVoltageData();
        }, 10, 10, TimeUnit.SECONDS);

        executor.scheduleAtFixedRate(() -> {
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
    private void sendRelayData() {
        if (!relayDataAvailable.isEmpty()) {
            ArrayList<DrAttributeDataJmsMessage> relayDataToSend;
            synchronized (relayDataAvailable) {
                if (relayDataAvailable.size() >= POINT_DATA_TO_SEND_AT_ONCE) {
                    relayDataToSend = new ArrayList<>(relayDataAvailable.subList(0, POINT_DATA_TO_SEND_AT_ONCE));
                    relayDataAvailable.subList(0, POINT_DATA_TO_SEND_AT_ONCE).clear();
                } else {
                    relayDataToSend = new ArrayList<>(relayDataAvailable.subList(0, relayDataAvailable.size()));
                    relayDataAvailable.subList(0, relayDataAvailable.size()).clear();
                }
            }
            log.debug("Sending relay data " + relayDataToSend.size());
            new Thread(() -> {
                drJmsMessageService.intervalDataNotification(relayDataToSend);
            }).start();
        }
    }

    /**
     * Push voltage data to generate multispeak messages
     */
    private void sendVoltageData() {
        if (!voltageDataAvailable.isEmpty()) {
            ArrayList<DrAttributeDataJmsMessage> voltageDataToSend;
            synchronized (voltageDataAvailable) {
                if (voltageDataAvailable.size() >= POINT_DATA_TO_SEND_AT_ONCE) {
                    voltageDataToSend = new ArrayList<>(voltageDataAvailable.subList(0, POINT_DATA_TO_SEND_AT_ONCE));
                    voltageDataAvailable.subList(0, POINT_DATA_TO_SEND_AT_ONCE).clear();
                } else {
                    voltageDataToSend = new ArrayList<>(voltageDataAvailable.subList(0, voltageDataAvailable.size()));
                    voltageDataAvailable.subList(0, voltageDataAvailable.size()).clear();
                }
            }
            log.debug("Sending voltage data " + voltageDataToSend.size());
            new Thread(() -> {
                drJmsMessageService.voltageMeterReadingsNotification(voltageDataToSend);
            }).start();
        }
    }

    /**
     * Push alarm data to generate multispeak messages
     */
    private void sendAlarmData() {
        if (!alarmDataAvailable.isEmpty()) {
            ArrayList<DrAttributeDataJmsMessage> alarmDataToSend;
            synchronized (alarmDataAvailable) {
                if (alarmDataAvailable.size() >= POINT_DATA_TO_SEND_AT_ONCE) {
                    alarmDataToSend = new ArrayList<>(alarmDataAvailable.subList(0, POINT_DATA_TO_SEND_AT_ONCE));
                    alarmDataAvailable.subList(0, POINT_DATA_TO_SEND_AT_ONCE).clear();
                } else {
                    alarmDataToSend = new ArrayList<>(alarmDataAvailable.subList(0, alarmDataAvailable.size()));
                    alarmDataAvailable.subList(0, alarmDataAvailable.size()).clear();
                }
            }
            log.debug("Sending alarm data " + alarmDataToSend.size());
            new Thread(() -> {
                drJmsMessageService.alarmAndEventNotification(alarmDataToSend);
            }).start();
        }
    }

    /*
     * Set message type based on Attributes.
     */
    private void setMessageTypeForAttribute(Set<BuiltInAttribute> supportedAttributes,
            DrAttributeDataJmsMessage attributeDataJmsMessage) {

        Boolean isVoltageAttribute = BuiltInAttribute.getVoltageAttributes().stream()
                .anyMatch(attributeType -> supportedAttributes.contains(attributeType));

        Boolean isRelayAttribute = BuiltInAttribute.getRelayDataAttributes().stream()
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
