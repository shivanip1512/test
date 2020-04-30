/**
 * 
 */
package com.cannontech.broker.systemMetrics;


import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.broker.message.request.BrokerSystemMetricsRequest;
import com.cannontech.broker.model.BrokerSystemMetricsAttribute;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.systemMetrics.SystemMetricsBase;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;


/**
 * Spring Bean to report point data for CPU usage and Memory usage for Broker Service.
 * This is different than the SystemMetricsImpl used by other service as Broker Service do not depend on common context.
 * Hence the autowired dependencies have to be removed.
 * 
 * For point data insertion message is send to service manager.
 * A service in service manager does the point insertion.
 */
@ManagedResource
public class BrokerSystemMetricsImpl extends SystemMetricsBase {

    private Logger log = YukonLogManager.getLogger(this.getClass());

    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    private YukonJmsTemplate jmsTemplate;

    public void init() {
        log.debug("BrokerSystemMetricsImpl.init()");
        jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.BROKER_SYSTEM_METRICS);
    }
    
    @Override
    public void insertPointData(Attribute attribute, double value) {
        BrokerSystemMetricsAttribute metricsAttribute = new BrokerSystemMetricsAttribute(attribute,value);
        BrokerSystemMetricsRequest systemMetricRequest = new BrokerSystemMetricsRequest(metricsAttribute);
        jmsTemplate.convertAndSend(systemMetricRequest);
    }
}
