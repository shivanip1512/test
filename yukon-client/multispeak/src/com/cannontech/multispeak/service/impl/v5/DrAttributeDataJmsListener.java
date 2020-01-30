package com.cannontech.multispeak.service.impl.v5;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
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
    private static Set<BuiltInAttribute> attributes = Sets.union(
            Sets.union(BuiltInAttribute.getVoltageAttributes(), BuiltInAttribute.getRelayDataAttributes()),
            BuiltInAttribute.getItronLcrAttributes());
    private static Set<PaoType> supportedPaoTypes = Sets.union(PaoType.getItronTypes(), PaoType.getTwoWayLcrTypes());

    private static final Logger log = YukonLogManager.getLogger(DrAttributeDataJmsListener.class);

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
                    drJmsMessageService.intervalDataNotification(attributeDataJmsMessage);
                    break;
                case VOLTAGEDATA:
                    drJmsMessageService.voltageMeterReadingsNotification(attributeDataJmsMessage);
                    break;
                case ALARMANDEVENT:
                    drJmsMessageService.alarmAndEventNotification(attributeDataJmsMessage);
                    break;
                default:
                    log.debug("Unable to find proper multispeak Dr message type i.e: "
                            + attributeDataJmsMessage.getMessageType());
                    break;
                }
            }
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
