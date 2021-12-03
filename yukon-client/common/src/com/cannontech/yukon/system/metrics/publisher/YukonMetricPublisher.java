package com.cannontech.yukon.system.metrics.publisher;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class YukonMetricPublisher {

    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    private YukonJmsTemplate jmsTemplate;
    private MappingJackson2MessageConverter converter;

    @PostConstruct
    public void init() {
        converter = new MappingJackson2MessageConverter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        converter.setObjectMapper(mapper);
        converter.setTargetType(MessageType.TEXT);
        jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.YUKON_METRIC, converter);
    }

    /**
     * Publish the date to the topic
     */
    public void publish(YukonMetric yukonMetric) {
        jmsTemplate.convertAndSend(yukonMetric);
    }

}
