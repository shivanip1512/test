package com.cannontech.yukon.api.stars.endpoint.endpointMappers;

import org.jdom.Element;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.yukon.api.stars.model.DeviceTextMessage;
import com.google.common.collect.Sets;

public class TextMessageElementRequestMapper implements ObjectMapper<Element, DeviceTextMessage> {

    @Override
    public DeviceTextMessage map(Element sendTextMessageRequestElement) throws ObjectMappingException {

        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(sendTextMessageRequestElement);

        DeviceTextMessage deviceTextMessage = new DeviceTextMessage();
        deviceTextMessage.setMessageId(template.evaluateAsLong("//y:messageId"));
        deviceTextMessage.setSerialNumbers(Sets.newHashSet(template.evaluateAsStringList("//y:serialNumber")));
        deviceTextMessage.setMessage(template.evaluateAsString("//y:message"));
        deviceTextMessage.setConfirmationRequired(template.evaluateAsBoolean("//y:confirmationRequired"));
        deviceTextMessage.setDisplayDuration(Duration.standardMinutes(template.evaluateAsLong("//y:displayDurationInMinutes", 1l)));
        deviceTextMessage.setStartTime(template.evaluateAsInstant("//y:startTime", new Instant()));
        
        return deviceTextMessage ;
    }
}