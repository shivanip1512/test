package com.cannontech.services.systemDataPublisher.listener;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
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
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.services.systemDataPublisher.service.impl.SystemDataServiceInitializer;

@Service
public class CloudDataConfigurationsAdvisoryListener {

    private static final Logger log = YukonLogManager.getLogger(CloudDataConfigurationsAdvisoryListener.class);

    @Autowired ConnectionFactory connectionFactory;
    @Autowired SystemDataServiceInitializer serviceInitializer;
    private static String CLOUD_DATA_CONFIGURATIONS_TOPIC = "com.eaton.eas.cloud.CloudDataConfigurations";
    // Service Manager also consumes message from the same topic. So setting previousConsumerCount to 1.
    private static int previousConsumerCount = 0;

    public Runnable listen() {
        return new Runnable() {
            public void run() {
                try {
                    // Added sleep of 1 minutes, for all the consumer services to start.
                    Thread.sleep(60000);
                    Connection connection = connectionFactory.createConnection();
                    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                    Topic topic = session.createTopic(CLOUD_DATA_CONFIGURATIONS_TOPIC);
                    Topic advisoryTopic = AdvisorySupport.getConsumerAdvisoryTopic(topic);
                    MessageConsumer advisoryConsumer = session.createConsumer(advisoryTopic);
                    advisoryConsumer.setMessageListener(new MessageListener() {

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
                                    String currentDateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
                                    // If a new consumer subscribes to the topic, consumerCount will be greater than
                                    // previousConsumerCount.log the information of newly connected consumer and publish the data
                                    // to the topic.
                                    if (consumerCount > previousConsumerCount) {
                                        if (dataStructure.getDataStructureType() == ConsumerInfo.DATA_STRUCTURE_TYPE) {
                                            ConsumerInfo consumerInfo = (ConsumerInfo) dataStructure;
                                            log.debug("Consumer with client ID : " + consumerInfo.getClientId()
                                                    + " connected at" + currentDateTime);
                                            previousConsumerCount = consumerCount;
                                            serviceInitializer.publishCloudDataConfigurations();
                                        }
                                    } else {
                                        // If a consumer unsubscribes to the topic, consumerCount will be less than
                                        // previousConsumerCount.
                                        if (dataStructure.getDataStructureType() == RemoveInfo.DATA_STRUCTURE_TYPE) {
                                            RemoveInfo removeInfo = (RemoveInfo) dataStructure;
                                            log.debug("Consumer with object ID : " + removeInfo.getObjectId() + " disconnected at"
                                                    + currentDateTime);
                                            previousConsumerCount = previousConsumerCount - consumerCount;
                                        }
                                    }
                                }
                            } catch (JMSException e) {
                                log.error("Error occured while receiving advisory messages: " + e);
                            }
                        }
                    });
                    connection.start();
                } catch (Exception e) {
                    log.error("Error occured while initilizing advisory message listener : " + e);
                }
            }
        };
    }
}
