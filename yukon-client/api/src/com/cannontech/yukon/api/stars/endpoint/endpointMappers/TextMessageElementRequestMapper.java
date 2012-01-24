package com.cannontech.yukon.api.stars.endpoint.endpointMappers;

import org.jdom.Element;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.yukon.api.stars.model.TextMessage;
import com.google.common.collect.Sets;

public class TextMessageElementRequestMapper implements ObjectMapper<Element, TextMessage> {

    @Override
    public TextMessage map(Element sendTextMessageRequestElement) throws ObjectMappingException {

        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(sendTextMessageRequestElement);

        TextMessage textMessage = new TextMessage();
        textMessage.setSerialNumbers(Sets.newHashSet(template.evaluateAsStringList("//y:serialNumber")));
        textMessage.setMessage(template.evaluateAsString("//y:message"));
        textMessage.setConfirmationRequired(template.evaluateAsBoolean("//y:confirmationRequired", false));
        textMessage.setDisplayDuration(Duration.standardMinutes(template.evaluateAsLong("//y:displayDurationInMinutes", 1l)));
        textMessage.setStartTime(template.evaluateAsInstant("//y:startTime", new Instant()));
        
        return textMessage ;
    }
}