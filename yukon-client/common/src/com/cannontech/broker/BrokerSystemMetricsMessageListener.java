package com.cannontech.broker;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.broker.message.request.BrokerSystemMetricsRequest;
import com.cannontech.broker.model.BrokerSystemMetricsAttribute;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.device.Device;

/**
 * Listens for System Metrics messages from Broker Service, parses them, and does point insertion.
 */
public class BrokerSystemMetricsMessageListener {
    private static final Logger log = YukonLogManager.getLogger(BrokerSystemMetricsMessageListener.class);

    @Autowired private AttributeService attributeService;
    @Autowired private SimplePointAccessDao pointAccessDao;
    
    /** Point for process CPU usage */
    private LitePoint loadAveragePoint;
    /** Point for process memory usage */
    private LitePoint memoryPoint;
    
    /**
     * Processes Broker System Metrics request.
     */
    public void handleSystemMetricsMessage(BrokerSystemMetricsRequest request) {
        log.debug("Received message on yukon.service.obj.common.broker.ArchiveSystemPoint queue.");

        if (request != null && request instanceof BrokerSystemMetricsRequest) {
            log.debug("Processing broker system metrics request");
            processRequest(request);
        }
    }


    public void init() {
        PaoIdentifier paoIdentifier = new PaoIdentifier(Device.SYSTEM_DEVICE_ID, PaoType.SYSTEM);
        BuiltInAttribute loadAverageAttribute = BuiltInAttribute.MESSAGE_BROKER_CPU_UTILIZATION;
        BuiltInAttribute memoryAttribute = BuiltInAttribute.MESSAGE_BROKER_MEMORY_UTILIZATION;

        try {

            loadAveragePoint = attributeService.getPointForAttribute(paoIdentifier,
                                                                     loadAverageAttribute);
        } catch (IllegalUseOfAttribute e) {
            log.error("Attribute: [" + loadAverageAttribute + "] not found for pao type: [SYSTEM]");
        }
        try {
            memoryPoint = attributeService.getPointForAttribute(paoIdentifier, memoryAttribute);
        } catch (IllegalUseOfAttribute e) {
            log.error("Attribute: [" + memoryAttribute + "] not found for pao type: [SYSTEM]");
        }
    }
    
    private void processRequest(BrokerSystemMetricsRequest metrics) {
        BrokerSystemMetricsAttribute logMessage = metrics.getSystemMetricsAttribute();
        Attribute attribute = logMessage.getAttribute();
        double pointValue = logMessage.getPointValue();

        if (attribute == BuiltInAttribute.MESSAGE_BROKER_CPU_UTILIZATION) {
            pointAccessDao.setPointValue(loadAveragePoint, pointValue);
        } else if (attribute == BuiltInAttribute.MESSAGE_BROKER_MEMORY_UTILIZATION) {
            pointAccessDao.setPointValue(memoryPoint, pointValue);
        }
    }

}
