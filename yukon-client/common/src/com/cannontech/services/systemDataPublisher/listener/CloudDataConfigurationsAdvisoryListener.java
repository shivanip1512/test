package com.cannontech.services.systemDataPublisher.listener;

import javax.annotation.PostConstruct;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.DataStructure;
import org.apache.activemq.command.RemoveInfo;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.services.systemDataPublisher.yaml.YamlConfigManager;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfigurations;

@Service
public class CloudDataConfigurationsAdvisoryListener {

    private static final Logger log = YukonLogManager.getLogger(CloudDataConfigurationsAdvisoryListener.class);

    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private YamlConfigManager yamlConfigManager;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;

    private YukonJmsTemplate jmsTemplate;
    private MappingJackson2MessageConverter converter;
    private static int previousConsumerCount = 0;

    @PostConstruct
    private void init() {
        converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.CLOUD_DATA_CONFIGURATIONS, converter);
    }

    public Runnable advisoryListener() {
        return new Runnable() {
            public void run() {
                try {
                    Connection connection = connectionFactory.createConnection();
                    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                    Topic topic = session.createTopic(JmsApiDirectory.CLOUD_DATA_CONFIGURATIONS.getQueueName());
                    Topic advisoryTopic = AdvisorySupport.getConsumerAdvisoryTopic(topic);

                    MessageListener messageListener = new MessageListener() {
                        @Override
                        public void onMessage(Message message) {
                            try {
                                int consumerCount = Integer.parseInt(message.getStringProperty("consumerCount"));
                                if (previousConsumerCount == 0) {
                                    previousConsumerCount = consumerCount;
                                }
                                if (consumerCount != previousConsumerCount) {
                                    ActiveMQMessage activeMQMessage = (ActiveMQMessage) message;
                                    DataStructure dataStructure = (DataStructure) activeMQMessage.getDataStructure();
                                    String currentDateTime = TimeUtil.getCurrentDateTime();
                                    // If a new consumer subscribes to the topic, consumerCount will be greater than
                                    // previousConsumerCount.log the information of newly connected consumer and publish the data
                                    // to the topic.
                                    if (consumerCount > previousConsumerCount) {
                                        if (dataStructure.getDataStructureType() == ConsumerInfo.DATA_STRUCTURE_TYPE) {
                                            ConsumerInfo consumerInfo = (ConsumerInfo) dataStructure;
                                            log.debug("Consumer with client ID: {} connected at: {}.", consumerInfo.getClientId(),
                                                    currentDateTime);
                                            previousConsumerCount++;
                                            publishCloudDataConfigurations(consumerInfo.getClientId());
                                        }
                                    } else {
                                        // If a consumer unsubscribes to the topic, consumerCount will be less than
                                        // previousConsumerCount.
                                        if (dataStructure.getDataStructureType() == RemoveInfo.DATA_STRUCTURE_TYPE) {
                                            RemoveInfo removeInfo = (RemoveInfo) dataStructure;
                                            log.debug("Consumer with object ID: {} disconnected at: {}.",
                                                    removeInfo.getObjectId(), currentDateTime);
                                            previousConsumerCount--;
                                        }
                                    }
                                }
                            } catch (JMSException e) {
                                log.error("Error occurred while receiving advisory messages: ", e);
                            }
                        }

                        private void publishCloudDataConfigurations(String clientId) {
                            CloudDataConfigurations configurations = yamlConfigManager.getCloudDataConfigurations();
                            configurations.setClientId(clientId);
                            jmsTemplate.convertAndSend(configurations);
                        }
                    };

                    SimpleMessageListenerContainer cointeContainer = new SimpleMessageListenerContainer();
                    cointeContainer.setDestination(advisoryTopic);
                    cointeContainer.setConnectionFactory(connectionFactory);
                    cointeContainer.setPubSubDomain(true);
                    cointeContainer.setConcurrentConsumers(1);
                    cointeContainer.setMessageListener(messageListener);
                    cointeContainer.start();
                } catch (Exception e) {
                    log.error("Error occurred while initilizing advisory message listener : ", e);
                }
            }
        };
    }
}
