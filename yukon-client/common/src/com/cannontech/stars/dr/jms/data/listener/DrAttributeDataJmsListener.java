package com.cannontech.stars.dr.jms.data.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.core.dynamic.RichPointDataListener;
import com.cannontech.stars.dr.jms.message.DrAttributeData;
import com.cannontech.stars.dr.jms.message.DrAttributeDataJmsMessage;
import com.cannontech.stars.dr.jms.message.DrJmsMessageType;
import com.cannontech.stars.dr.jms.service.DrJmsMessagingService;
import com.google.common.collect.Sets;

public class DrAttributeDataJmsListener implements RichPointDataListener {
    @Autowired private DrJmsMessagingService drJmsMessagingService;
    @Autowired private PointDao pointDao;
    @Autowired private AttributeService attributeService;

    @Override
    public void pointDataReceived(RichPointData richPointData) {

        PointValueHolder valueHolder = richPointData.getPointValue();
        Set<BuiltInAttribute> attributes = Sets.union(BuiltInAttribute.getVoltageAttributes(), BuiltInAttribute.getRelayDataAttributes());

        PaoPointIdentifier paoPointIdentifier = pointDao.getPaoPointIdentifier(valueHolder.getId());
        Set<BuiltInAttribute> supportedAttributes = attributeService.findAttributesForPoint(paoPointIdentifier.getPaoTypePointIdentifier(),
                                                                                            attributes);

        if (!supportedAttributes.isEmpty()) {
            DrAttributeDataJmsMessage attributeDataJmsMessage = new DrAttributeDataJmsMessage();
            attributeDataJmsMessage.setPaoPointIdentifier(paoPointIdentifier);

            List<DrAttributeData> attributeDataList = new ArrayList<>();
            for (BuiltInAttribute attribute : supportedAttributes) {
                DrAttributeData attributeData = new DrAttributeData();

                attributeData.setAttribute(attribute);
                attributeData.setTimeStamp(valueHolder.getPointDataTimeStamp());
                attributeData.setValue(valueHolder.getValue());

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
