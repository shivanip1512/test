package com.cannontech.stars.dr.jms.notification.listner;

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
import com.cannontech.stars.dr.jms.notification.DRNotificationMessagingService;
import com.cannontech.stars.dr.jms.notification.message.DRDataMessage;
import com.cannontech.stars.dr.jms.notification.message.DRNotificationDataMessage;
import com.cannontech.stars.dr.jms.notification.message.DRNotificationMessageType;
import com.google.common.collect.Sets;

public class DRNotificationDataListener implements RichPointDataListener {
    @Autowired private DRNotificationMessagingService notificationService;
    @Autowired private PointDao pointDao;
    @Autowired private AttributeService attributeService;

    @Override
    public void pointDataReceived(RichPointData richPointData) {

        PointValueHolder valueHolder = richPointData.getPointValue();
        Set<BuiltInAttribute> notificationSupportedAttributes = Sets.union(BuiltInAttribute.getVoltageAttributes(), BuiltInAttribute.getRelayDataAttributes());

        PaoPointIdentifier paoPointIdentifier = pointDao.getPaoPointIdentifier(valueHolder.getId());
        Set<BuiltInAttribute> supportedAttributes = attributeService.findAttributesForPoint(paoPointIdentifier.getPaoTypePointIdentifier(),
                                                                                              notificationSupportedAttributes);

        if (!supportedAttributes.isEmpty()) {
            DRNotificationDataMessage dataNotificationMessage = new DRNotificationDataMessage();
            dataNotificationMessage.setPaoPointIdentifier(paoPointIdentifier);

            List<DRDataMessage> drDataMessages = new ArrayList<>();
            for (BuiltInAttribute attribute : supportedAttributes) {
                DRDataMessage drDataMessage = new DRDataMessage();

                drDataMessage.setAttribute(attribute);
                drDataMessage.setTimeStamp(valueHolder.getPointDataTimeStamp());
                drDataMessage.setValue(valueHolder.getValue());

                drDataMessages.add(drDataMessage);

            }
            dataNotificationMessage.setDataMessages(drDataMessages);

            Boolean isVoltageAttribute = BuiltInAttribute.getVoltageAttributes()
                                                       .stream()
                                                       .anyMatch(attribute -> supportedAttributes.contains(attribute));
            if (isVoltageAttribute) {
                dataNotificationMessage.setMessageType(DRNotificationMessageType.VOLTAGEDATA);
            } else {
                dataNotificationMessage.setMessageType(DRNotificationMessageType.RELAYDATA);
            }

            notificationService.sendDataMessageNotification(dataNotificationMessage);
        }

    }

}
