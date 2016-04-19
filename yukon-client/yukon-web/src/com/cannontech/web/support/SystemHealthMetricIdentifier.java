package com.cannontech.web.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * Unique identifiers for each system health metric. 
 */
public enum SystemHealthMetricIdentifier {
    RFN_METER(SystemHealthMetricType.JMS_QUEUE_EXTENDED, "meterReads", "yukon.qr.obj.amr.rfn.MeterReadingArchiveRequest"),
    RFN_LCR(SystemHealthMetricType.JMS_QUEUE_EXTENDED, "lcrReads", "yukon.qr.obj.dr.rfn.LcrReadingArchiveRequest"),
    RF_GATEWAY_ARCHIVE(SystemHealthMetricType.JMS_QUEUE_EXTENDED, "gatewayArchive", "yukon.qr.obj.common.rfn.GatewayArchiveRequest"),
    RF_DA(SystemHealthMetricType.JMS_QUEUE, "rfDa", "yukon.qr.obj.da.rfn.RfDaArchiveRequest"),
    RF_GATEWAY_DATA_REQUEST(SystemHealthMetricType.JMS_QUEUE, "gatewayDataRequest", "yukon.qr.obj.common.rfn.GatewayDataRequest"),
    RF_GATEWAY_DATA(SystemHealthMetricType.JMS_QUEUE, "gatewayData", "yukon.qr.obj.common.rfn.GatewayData")
    ;
    
    private static final Map<SystemHealthMetricIdentifier, String> queueBeans = ImmutableMap.of(
        RF_DA, "org.apache.activemq:type=Broker,brokerName=ServiceManager,destinationType=Queue,destinationName=yukon.qr.obj.da.rfn.RfDaArchiveRequest",
        RF_GATEWAY_DATA_REQUEST, "org.apache.activemq:type=Broker,brokerName=ServiceManager,destinationType=Queue,destinationName=yukon.qr.obj.common.rfn.GatewayDataRequest",
        RF_GATEWAY_DATA, "org.apache.activemq:type=Broker,brokerName=ServiceManager,destinationType=Queue,destinationName=yukon.qr.obj.common.rfn.GatewayData"
    );
    
    private static final Map<SystemHealthMetricIdentifier, List<String>> extendedQueueBeans = ImmutableMap.of(
        RFN_METER, Lists.newArrayList(
            "com.cannontech.yukon.ServiceManager:name=meterReadingArchiveRequestListener,type=MeterReadingArchiveRequestListener", 
            "org.apache.activemq:type=Broker,brokerName=ServiceManager,destinationType=Queue,destinationName=yukon.qr.obj.amr.rfn.MeterReadingArchiveRequest"),
        RFN_LCR, Lists.newArrayList(
            "com.cannontech.yukon.ServiceManager:name=lcrReadingArchiveRequestListener,type=LcrReadingArchiveRequestListener", 
            "org.apache.activemq:type=Broker,brokerName=ServiceManager,destinationType=Queue,destinationName=yukon.qr.obj.dr.rfn.LcrReadingArchiveRequest"),
        RF_GATEWAY_ARCHIVE, Lists.newArrayList(
            "com.cannontech.yukon.ServiceManager:name=gatewayArchiveRequestListener,type=GatewayArchiveRequestListener", 
            "org.apache.activemq:type=Broker,brokerName=ServiceManager,destinationType=Queue,destinationName=yukon.qr.obj.common.rfn.GatewayArchiveRequest")
    );
    
    public static String getJmsQueueBean(SystemHealthMetricIdentifier metric) {
        return queueBeans.get(metric);
    }
    
    public static List<String> getExtendedJmsQueueBeans(SystemHealthMetricIdentifier metric) {
        return extendedQueueBeans.get(metric);
    }
    
    public static final List<SystemHealthMetricIdentifier> getAllByType(SystemHealthMetricType type) {
        List<SystemHealthMetricIdentifier> metrics = new ArrayList<>();
        for (SystemHealthMetricIdentifier metric : values()) {
            if (metric.getType() == type) {
                metrics.add(metric);
            }
        }
        return metrics;
    }
    
    private SystemHealthMetricType type;
    private String keySuffix;
    private String queueName;
    
    private SystemHealthMetricIdentifier(SystemHealthMetricType type, String keySuffix, String queueName) {
        this.type = type;
        this.keySuffix = keySuffix;
        this.queueName = queueName;
    }
    
    public SystemHealthMetricType getType() {
        return type;
    }
    
    public String getKeySuffix() {
        return keySuffix;
    }
    
    public String getQueueName() {
        return queueName;
    }
}
