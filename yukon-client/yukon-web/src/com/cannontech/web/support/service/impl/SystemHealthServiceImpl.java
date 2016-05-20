package com.cannontech.web.support.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.dev.service.YsmJmxQueryService;
import com.cannontech.web.support.dao.UserSystemMetricDao;
import com.cannontech.web.support.service.SystemHealthService;
import com.cannontech.web.support.systemMetrics.ExtendedQueueData;
import com.cannontech.web.support.systemMetrics.MetricHealthCriteria;
import com.cannontech.web.support.systemMetrics.MetricStatusWithMessages;
import com.cannontech.web.support.systemMetrics.QueueData;
import com.cannontech.web.support.systemMetrics.SystemHealthMetric;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricIdentifier;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class SystemHealthServiceImpl implements SystemHealthService {
    
    private static final Logger log = YukonLogManager.getLogger(SystemHealthServiceImpl.class);
    @Autowired @Qualifier("main") private ScheduledExecutor executor;
    @Autowired List<MetricHealthCriteria> metricHealthCriteria;
    @Autowired private UserSystemMetricDao userSystemMetricDao;
    @Autowired private YsmJmxQueryService jmxQueryService;
    private SystemHealthStatusHelper statusHelper;
    
    @PostConstruct
    private void init() {
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
            metrics.add(metric);
        }
        return metrics;
    }
    
    @Override
    public Multimap<SystemHealthMetricType, SystemHealthMetric> getMetricsByIdentifiers(Collection<SystemHealthMetricIdentifier> metricIds) {
        Multimap<SystemHealthMetricType, SystemHealthMetric> metrics = ArrayListMultimap.create();
        for (SystemHealthMetricIdentifier metricId : metricIds) {
            SystemHealthMetric metric = getMetric(metricId);
            metrics.put(metric.getType(), metric);
        }
        return metrics;
    }
    
    @Override 
    public List<SystemHealthMetric> getAllMetrics() {
        List<SystemHealthMetric> metrics = new ArrayList<>();
        for (SystemHealthMetricIdentifier metricId : SystemHealthMetricIdentifier.values()) {
            SystemHealthMetric metric = getMetric(metricId);
            metrics.add(metric);
        }
        return metrics;
    }
    
    @Override
    public List<SystemHealthMetricIdentifier> getFavorites(LiteYukonUser user) {
        return userSystemMetricDao.getFavorites(user);
    }
    
    @Override
    public void setFavorite(LiteYukonUser user, SystemHealthMetricIdentifier metric, boolean isFavorite) {
        if (isFavorite) {
            userSystemMetricDao.addFavorite(user, metric);
        } else {
            userSystemMetricDao.removeFavorite(user, metric);
        }
    }
    
    private ExtendedQueueData getExtendedQueueData(SystemHealthMetricIdentifier metric) {
        try {
            String serviceBeanName = SystemHealthMetricIdentifier.getExtendedJmsQueueBeans(metric).get(0);
            ObjectName serviceBean = ObjectName.getInstance(serviceBeanName);
            String queueBeanName = SystemHealthMetricIdentifier.getExtendedJmsQueueBeans(metric).get(1);
            ObjectName queueBean = ObjectName.getInstance(queueBeanName);
            
            Integer archived = (Integer) jmxQueryService.get(serviceBean, "ArchivedReadings");
            Integer archiveRequestsProcessed = (Integer) jmxQueryService.get(serviceBean, "ProcessedArchiveRequest");
            
            Long enqueuedCount = (Long) jmxQueryService.get(queueBean, "EnqueueCount");
            Long dequeuedCount = (Long) jmxQueryService.get(queueBean, "DequeueCount");
            Long queueSize = (Long) jmxQueryService.get(queueBean, "QueueSize");
            Double averageEnqueueTime = (Double) jmxQueryService.get(queueBean, "AverageEnqueueTime");
            
            MetricStatusWithMessages status = statusHelper.getStatus(metric);
            
            ExtendedQueueData data = new ExtendedQueueData(metric);
            data.setArchivedReadingsCount(archived);
            data.setArchiveRequestsProcessed(archiveRequestsProcessed);
            data.setEnqueuedCount(enqueuedCount);
            data.setDequeuedCount(dequeuedCount);
            data.setQueueSize(queueSize);
            data.setAverageEnqueueTime(averageEnqueueTime);
            data.setStatus(status);
            
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
            Long enqueueCount = (Long) jmxQueryService.get(bean, "EnqueueCount");
            Long dequeueCount = (Long) jmxQueryService.get(bean, "DequeueCount");
            Long queueSize = (Long) jmxQueryService.get(bean, "QueueSize");
            Double averageEnqueueTime = (Double) jmxQueryService.get(bean, "AverageEnqueueTime");
            
            MetricStatusWithMessages status = statusHelper.getStatus(metric);
            
            QueueData data = new QueueData(metric);
            data.setEnqueuedCount(enqueueCount);
            data.setDequeuedCount(dequeueCount);
            data.setQueueSize(queueSize);
            data.setAverageEnqueueTime(averageEnqueueTime);
            data.setStatus(status);
            
            return data;
        } catch (Exception e) {
            log.error("Couldn't load metric data.", e);
            return null;
        }
    }
    
}
