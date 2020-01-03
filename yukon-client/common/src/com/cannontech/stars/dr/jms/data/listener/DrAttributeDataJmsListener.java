package com.cannontech.stars.dr.jms.data.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.core.dynamic.RichPointDataListener;
import com.cannontech.stars.dr.jms.message.DrAttributeData;
import com.cannontech.stars.dr.jms.message.DrAttributeDataJmsMessage;
import com.cannontech.stars.dr.jms.message.DrJmsMessageType;
import com.cannontech.stars.dr.jms.service.DrJmsMessagingService;
import com.google.common.collect.Sets;

public class DrAttributeDataJmsListener implements RichPointDataListener {
    @Autowired private DrJmsMessagingService drJmsMessagingService;
    @Autowired private AttributeService attributeService;

    @Override
    public void pointDataReceived(RichPointData richPointData) {

        Set<BuiltInAttribute> attributes = Sets.union(BuiltInAttribute.getVoltageAttributes(), BuiltInAttribute.getRelayDataAttributes());

        Set<BuiltInAttribute> supportedAttributes = attributeService.findAttributesForPoint(richPointData.getPaoPointIdentifier().getPaoTypePointIdentifier(),
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

            Boolean isVoltageAttribute = BuiltInAttribute.getVoltageAttributes()
                                                       .stream()
                                                       .anyMatch(attribute -> supportedAttributes.contains(attribute));
            if (isVoltageAttribute) {
                attributeDataJmsMessage.setMessageType(DrJmsMessageType.VOLTAGEDATA);
            } else {
                attributeDataJmsMessage.setMessageType(DrJmsMessageType.RELAYDATA);
            }

            drJmsMessagingService.publishAttributeDataMessageNotice(attributeDataJmsMessage);
        }

    }

}
