package com.cannontech.web.support.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.management.ObjectName;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.dev.service.SystemHealthMetricSimulatorService;
import com.cannontech.web.support.service.SystemHealthService;
import com.cannontech.web.support.systemMetrics.ExtendedQueueData;
import com.cannontech.web.support.systemMetrics.MetricStatusWithMessages;
import com.cannontech.web.support.systemMetrics.QueueData;
import com.cannontech.web.support.systemMetrics.SystemHealthMetric;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricIdentifier;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricType;
import com.cannontech.web.support.systemMetrics.criteria.MetricHealthCriteria;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class SystemHealthServiceImpl implements SystemHealthService {
    
    private static final Logger log = YukonLogManager.getLogger(SystemHealthServiceImpl.class);
    @Autowired @Qualifier("main") private ScheduledExecutor executor;
    @Autowired List<MetricHealthCriteria> metricHealthCriteria;
    @Autowired private SystemHealthMetricSimulatorService metricSimulator;
    @Autowired private YsmJmxQueryService jmxQueryService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    private SystemHealthStatusHelper statusHelper;
    
    @PostConstruct
    private void init() {
        // Set the default message for healthy metrics
        String message = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system)
                                              .getMessage("yukon.web.modules.support.systemHealth.criteria.default");
        MetricStatusWithMessages.setDefaultMessage(message);
        
        // Init status helper and start periodic status calculation
        statusHelper = new SystemHealthStatusHelper(this, executor, metricHealthCriteria);
    }
    
    @Override
    public SystemHealthMetric getMetric(SystemHealthMetricIdentifier metric) {
        switch (metric.getType()) {
            case JMS_QUEUE:
                return getQueueData(metric);
            case JMS_QUEUE_EXTENDED:
                return getExtendedQueueData(metric);
            default:
                throw new IllegalArgumentException("Metric " + metric + "has unsupported type: " + metric.getType());
        }
    }
    
    @Override
    public List<SystemHealthMetric> getMetricsByType(SystemHealthMetricType type) {
        List<SystemHealthMetricIdentifier> metricIds = SystemHealthMetricIdentifier.getAllByType(type);
        List<SystemHealthMetric> metrics = new ArrayList<>();
        for (SystemHealthMetricIdentifier metricId : metricIds) {
            SystemHealthMetric metric = getMetric(metricId);
            if (metric != null) {
                metrics.add(metric);
            }
        }
        return metrics;
    }
    
    @Override
    public Multimap<SystemHealthMetricType, SystemHealthMetric> getMetricsByIdentifiers(Collection<SystemHealthMetricIdentifier> metricIds) {
        Multimap<SystemHealthMetricType, SystemHealthMetric> metrics = ArrayListMultimap.create();
        for (SystemHealthMetricIdentifier metricId : metricIds) {
            SystemHealthMetric metric = getMetric(metricId);
            if (metric != null) {
                metrics.put(metric.getType(), metric);
            }
        }
        return metrics;
    }
    
    @Override 
    public List<SystemHealthMetric> getAllMetrics() {
        List<SystemHealthMetric> metrics = new ArrayList<>();
        for (SystemHealthMetricIdentifier metricId : SystemHealthMetricIdentifier.values()) {
            SystemHealthMetric metric = getMetric(metricId);
            if (metric != null) {
                metrics.add(metric);
            }
        }
        return metrics;
    }
    
    @Override
    public Collection<MetricHealthCriteria> getPertinentCriteria(SystemHealthMetricIdentifier metricId) {
        return statusHelper.getPertinentCriteria(metricId);
    }
    
    private ExtendedQueueData getExtendedQueueData(SystemHealthMetricIdentifier metric) {
        try {
            String serviceBeanName = SystemHealthMetricIdentifier.getExtendedJmsQueueBeans(metric).get(0);
            ObjectName serviceBean = ObjectName.getInstance(serviceBeanName);
            String queueBeanName = SystemHealthMetricIdentifier.getExtendedJmsQueueBeans(metric).get(1);
            ObjectName queueBean = ObjectName.getInstance(queueBeanName);
            
            Integer archived = jmxQueryService.getTypedValue(serviceBean, "ArchivedReadings", 0, Integer.class, BeanTypeForJMXConnector.SERVICE);
            Integer archiveRequestsProcessed = jmxQueryService.getTypedValue(serviceBean, "ProcessedArchiveRequest", 0, Integer.class, BeanTypeForJMXConnector.SERVICE);
            
            Long enqueuedCount = jmxQueryService.getTypedValue(queueBean, "EnqueueCount", 0L, Long.class, BeanTypeForJMXConnector.QUEUE);
            Long dequeuedCount = jmxQueryService.getTypedValue(queueBean, "DequeueCount", 0L, Long.class, BeanTypeForJMXConnector.QUEUE);
            Long queueSize = jmxQueryService.getTypedValue(queueBean, "QueueSize", 0L, Long.class, BeanTypeForJMXConnector.QUEUE);
            Double averageEnqueueTime = jmxQueryService.getTypedValue(queueBean, "AverageEnqueueTime", 0.0, Double.class, BeanTypeForJMXConnector.QUEUE);
            
            MetricStatusWithMessages status = statusHelper.getStatus(metric);
            
            ExtendedQueueData data = new ExtendedQueueData(metric);
            data.setArchivedReadingsCount(archived);
            data.setArchiveRequestsProcessed(archiveRequestsProcessed);
            data.setEnqueuedCount(enqueuedCount);
            data.setDequeuedCount(dequeuedCount);
            data.setQueueSize(queueSize);
            data.setAverageEnqueueTime(averageEnqueueTime);
            data.setStatus(status);
            
            log.debug("Loaded data for metric " + metric + ": " + data.toString());
            
            if (metricSimulator.isActive()) {
                metricSimulator.applyFakeMetrics(data);
                MetricStatusWithMessages fakeStatus = statusHelper.calculateStatus(data);
                data.setStatus(fakeStatus);
            }
            
            log.debug("Applied fake data to metric " + metric + ": " + data.toString());
            
            return data;
        } catch (Exception e) {
            log.error("Couldn't load metric data.", e);
            return null;
        }
    }
    
    private QueueData getQueueData(SystemHealthMetricIdentifier metric) {
        
        try {
            String beanName = SystemHealthMetricIdentifier.getJmsQueueBean(metric);
            ObjectName bean = ObjectName.getInstance(beanName);
            Long enqueueCount = jmxQueryService.getTypedValue(bean, "EnqueueCount", 0L, Long.class, BeanTypeForJMXConnector.QUEUE);
            Long dequeueCount = jmxQueryService.getTypedValue(bean, "DequeueCount", 0L, Long.class, BeanTypeForJMXConnector.QUEUE);
            Long queueSize = jmxQueryService.getTypedValue(bean, "QueueSize", 0L, Long.class, BeanTypeForJMXConnector.QUEUE);
            Double averageEnqueueTime = jmxQueryService.getTypedValue(bean, "AverageEnqueueTime", 0.0, Double.class, BeanTypeForJMXConnector.QUEUE);
            
            MetricStatusWithMessages status = statusHelper.getStatus(metric);
            
            QueueData data = new QueueData(metric);
            data.setEnqueuedCount(enqueueCount);
            data.setDequeuedCount(dequeueCount);
            data.setQueueSize(queueSize);
            data.setAverageEnqueueTime(averageEnqueueTime);
            data.setStatus(status);
            
            log.debug("Loaded data for metric " + metric + ": " + data.toString());
            
            if (metricSimulator.isActive()) {
                metricSimulator.applyFakeMetrics(data);
                MetricStatusWithMessages fakeStatus = statusHelper.calculateStatus(data);
                data.setStatus(fakeStatus);
            }
            
            log.debug("Applied fake data to metric " + metric + ": " + data.toString());
            
            return data;
        } catch (Exception e) {
            log.error("Couldn't load metric data.", e);
            return null;
        }
    }
    
}
