package com.cannontech.amr.deviceDataMonitor.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitorProcessor;
import com.cannontech.amr.deviceDataMonitor.model.ProcessorType;
import com.cannontech.amr.deviceDataMonitor.service.DeviceDataMonitorCalculationService;
import com.cannontech.amr.monitors.MonitorCacheService;
import com.cannontech.amr.monitors.message.DeviceDataMonitorMessage;
import com.cannontech.amr.monitors.message.DeviceDataMonitorStatusRequest;
import com.cannontech.amr.monitors.message.DeviceDataMonitorStatusResponse;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.smartNotification.model.DeviceDataMonitorEventAssembler;
import com.cannontech.common.smartNotification.model.DeviceDataMonitorEventAssembler.MonitorState;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.service.SmartNotificationEventCreationService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.DispatchClientConnection;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.conns.ConnPool;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;

public class DeviceDataMonitorCalculationServiceImpl implements DeviceDataMonitorCalculationService, MessageListener {

    private static final int MINUTES_TO_WAIT_TO_START_CALCULATION = 5;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private Executor executor = Executors.newCachedThreadPool();
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private AttributeService attributeService;
    @Autowired private ConnPool connPool;
    @Autowired private MonitorCacheService monitorCacheService;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private SmartNotificationEventCreationService smartNotificationEventCreationService;
    @Autowired private DbChangeManager dbChangeManager;

    private JmsTemplate jmsTemplate;
    private DispatchClientConnection dispatchConnection;

    // monitors recalculating
    private Map<Integer, DeviceDataMonitor> pending = Collections.synchronizedMap(new HashMap<>());
    // monitorId / violation count
    private Map<Integer, Integer> monitorIdToViolationCount = Collections.synchronizedMap(new HashMap<>());

    private static final Logger log = YukonLogManager.getLogger(DeviceDataMonitorCalculationServiceImpl.class);

    @PostConstruct
    public void init() {

        log.info("Waiting " + MINUTES_TO_WAIT_TO_START_CALCULATION + " minutes to recalcutate");

        scheduledExecutorService.schedule(() -> {
            try {
                // wait for dispatch to be available
                dispatchConnection = connPool.getDefDispatchConn();
                if (!dispatchConnection.isValid()) {
                    log.info("Waiting for dispatch to connect");
                    dispatchConnection.waitForValidConnection();
                }

                monitorCacheService.getEnabledDeviceDataMonitors().forEach(monitor -> {
                    startWork(monitor);
                });

            } catch (Exception e) {
                log.error("Failed to start calculation", e);
            }
        }, MINUTES_TO_WAIT_TO_START_CALCULATION, TimeUnit.MINUTES);
    }

    /**
     * Start recalculating monitor
     */
    private void startWork(DeviceDataMonitor monitor) {

        executor.execute(() -> {
            try {
                log.debug("Starting work " + monitor);
                boolean isWorkingOnObject = pending.containsKey(monitor.getId());
                if (!isWorkingOnObject) {
                    pending.put(monitor.getId(), monitor);
                    try {
                        recalculateViolations(monitor);
                    } catch (Exception e) {
                        // If error occurred during calculation monitor will be removed from pending so that
                        // calculation can re-run in 5 minutes and hopefully succeed on the second run.
                        log.error(monitor + " failed to recalculate violations", e);
                    }
                    Iterator<DeviceDataMonitor> iterator = pending.values().iterator();
                    while (iterator.hasNext()) {
                        DeviceDataMonitor ddm = iterator.next();
                        if (ddm.getId() == monitor.getId()) {
                            iterator.remove();
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                log.error(e);
            }
        });
    }

    private static final class CorrelationIdPostProcessor implements MessagePostProcessor {
        private final String correlationId;

        public CorrelationIdPostProcessor(final String correlationId) {
            this.correlationId = correlationId;
        }

        @Override
        public Message postProcessMessage(final Message msg) throws JMSException {
            msg.setJMSCorrelationID(correlationId);
            return msg;
        }
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            Serializable object;
            try {
                object = objMessage.getObject();
                if (object instanceof DeviceDataMonitorStatusRequest) {
                    Integer monitorId = ((DeviceDataMonitorStatusRequest) object).getMonitorId();
                    boolean isWorkingOnObject = pending.containsKey(monitorId);
                    String correlationId = message.getJMSCorrelationID();
                    DeviceDataMonitorStatusResponse response;
                    if (isWorkingOnObject) {
                        response = new DeviceDataMonitorStatusResponse(isWorkingOnObject, null);
                    } else {
                        Integer violations = monitorIdToViolationCount.get(monitorId);
                        response = new DeviceDataMonitorStatusResponse(isWorkingOnObject, violations);
                    }
                    jmsTemplate.convertAndSend(message.getJMSReplyTo(), response, new CorrelationIdPostProcessor(correlationId));
                } else if (object instanceof DeviceDataMonitorMessage) {
                    DeviceDataMonitorMessage monitorMessage = (DeviceDataMonitorMessage) object;
                    log.debug(monitorMessage);
                    switch (monitorMessage.getAction()) {
                    case CREATE:
                        createViolationGroup(monitorMessage.getUpdatedMonitor());
                        cacheViolationGroupAndDeviceGroup(monitorMessage.getUpdatedMonitor());
                        startWork(monitorMessage.getUpdatedMonitor());
                    case DISABLE:
                        deviceGroupMemberEditorDao.removeAllChildDevices(monitorMessage.getUpdatedMonitor().getViolationGroup());
                        break;
                    case ENABLE:
                    case RECALCULATE:
                        startWork(monitorMessage.getUpdatedMonitor());
                        break;
                    case UPDATE:
                        updateViolationGroup(monitorMessage);
                        cacheViolationGroupAndDeviceGroup(monitorMessage.getUpdatedMonitor());
                        startWork(monitorMessage.getUpdatedMonitor());
                        break;
                    default:
                        break;
                    }
                }
            } catch (JMSException e) {
                log.error("Unable to extract message", e);
            } catch (Exception e) {
                log.error("Unable to process message", e);
            }
        }
    }
    
    private void cacheViolationGroupAndDeviceGroup(DeviceDataMonitor monitor) {
        StoredDeviceGroup violationGroup = deviceGroupEditorDao.getStoredGroup(SystemGroupEnum.DEVICE_DATA,
            monitor.getViolationsDeviceGroupName(), false);
        monitor.setViolationGroup(violationGroup);
        monitor.setGroup(deviceGroupService.findGroupName(monitor.getGroupName()));
    }
    
    /**
     * Updates violation group
     * If the monitor's oldName is not equal to newName, then the device group's name is changed.
     */
    private void updateViolationGroup(DeviceDataMonitorMessage message) {

        String oldName = message.getOldMonitor() == null ? null : message.getOldMonitor().getName();
        String newName = message.getUpdatedMonitor() == null ? null : message.getUpdatedMonitor().getName();
        if (!oldName.equals(newName)) {
            log.debug("monitor name changed from " + oldName + " to " + newName);
            // try to retrieve group by new name (possible it could exist)
            // if does not exist, get old group, give it new name
            try {
                
                StoredDeviceGroup group = deviceGroupEditorDao.getStoredGroup(SystemGroupEnum.DEVICE_DATA, newName, false);
                log.debug("Group "+group.getFullName()+" was already created");
            } catch (NotFoundException e) {
                
                log.debug("Creating group with the name "+newName);
                StoredDeviceGroup group =  deviceGroupEditorDao.getStoredGroup(SystemGroupEnum.DEVICE_DATA,
                    message.getOldMonitor().getName(), false);
                group.setName(newName);
                deviceGroupEditorDao.updateGroup(group);
                dbChangeManager.processDbChange(DbChangeType.UPDATE, DbChangeCategory.DEVICE_DATA_MONITOR, message.getOldMonitor().getId());
            }
        }
    }
    
    /**
     * Creates violation group
     */
    private void createViolationGroup(DeviceDataMonitor monitor) {
        log.debug("Creating new device group " + monitor);
        monitor.setViolationGroup(deviceGroupEditorDao.getStoredGroup(SystemGroupEnum.DEVICE_DATA, monitor.getViolationsDeviceGroupName(), true));
        dbChangeManager.processDbChange(DbChangeType.UPDATE, DbChangeCategory.DEVICE_DATA_MONITOR, monitor.getId());
    }

    /**
     * Recalculates violations for monitor
     * This method does FULL recalculation of violations
     */
    private void recalculateViolations(DeviceDataMonitor monitor) {

        log.info("{} recalculating violations", monitor);

        Multimap<SimpleDevice, Attribute> devicesToEvaluate =
            attributeService.getDevicesInGroupThatSupportAttribute(monitor.getGroup(), monitor.getAttributes(), null);
        
        List<SimpleDevice> devicesInViolationGroup =
                deviceGroupMemberEditorDao.getChildDevices(monitor.getViolationGroup());
        Set<Integer> inViolationGroup =
                devicesInViolationGroup.stream().map(SimpleDevice::getDeviceId).collect(Collectors.toSet());
        
        Set<SimpleDevice> devicesInViolation = new HashSet<>();
        if(!devicesToEvaluate.isEmpty()) {
            devicesInViolation = findViolations(monitor, devicesToEvaluate);  
        }

        log.debug("{} recalculated violations, violations found {}", monitor, devicesInViolation.size());

        Set<Integer> violating = devicesInViolation.stream().map(SimpleDevice::getDeviceId).collect(Collectors.toSet());

        if (!Sets.symmetricDifference(inViolationGroup, violating).isEmpty()) {
            log.debug("{} removing all devices from violation group {}", monitor, monitor.getViolationGroup());

            deviceGroupMemberEditorDao.removeAllChildDevices(monitor.getViolationGroup());
            if (!devicesInViolation.isEmpty()) {
                log.debug("{} adding {} devices to violation group {}", monitor, devicesInViolation.size(),
                    monitor.getViolationGroup());
                deviceGroupMemberEditorDao.addDevices(monitor.getViolationGroup(), devicesInViolation);
            }
        }
        log.debug("{} recaclulation is complete", monitor);

        updateViolationCacheCount(monitor);
        sendSmartNotifications(inViolationGroup, violating, monitor);

    }
    
    @Override
    public void recalculateViolation(DeviceDataMonitor monitor, int deviceId){

        Multimap<SimpleDevice, Attribute> devices = attributeService.getDevicesInGroupThatSupportAttribute(
            monitor.getGroup(), monitor.getAttributes(), Lists.newArrayList(deviceId));
        
        SimpleDevice device = devices.keySet().iterator().next();
        log.debug("{} recalculating violations for {} attributes {}", monitor, device, deviceId);

        boolean foundViolation = !findViolations(monitor, devices).isEmpty();

        boolean inViolationsGroup = deviceGroupService.isDeviceInGroup(monitor.getViolationGroup(), device);
        
        //found violation and device is not in violation group
        if(foundViolation && !inViolationsGroup) {
            //add device to group
            deviceGroupMemberEditorDao.addDevices(monitor.getViolationGroup(), device);
            sendSmartNotificationEvent(monitor, device.getDeviceId(), MonitorState.IN_VIOLATION);
            log.debug("{} adding {} to violation group {}", monitor, device, monitor.getViolationGroup());
        }
        
        // no violation found but device is in violation group
        if (!foundViolation && inViolationsGroup) {
            // remove device from group
            deviceGroupMemberEditorDao.removeDevicesById(monitor.getViolationGroup(), Collections.singleton(device.getDeviceId()));
            sendSmartNotificationEvent(monitor, device.getDeviceId(), MonitorState.OUT_OF_VIOLATION);
            log.debug("{} removing {} form violation group {}", monitor, device, monitor.getViolationGroup());
        }
        
        updateViolationCacheCount(monitor);
        log.debug(" {} recalculation of violation for {} is complete",  monitor, device);
    }
    
    /**
     * Cache violations count to be used when data updaters send request to SM to get the count.
     */
    private void updateViolationCacheCount(DeviceDataMonitor monitor) {
        StoredDeviceGroup violationsGroup = monitor.getViolationGroup();
        int violationsCount = deviceGroupService.getDeviceCount(Collections.singleton(violationsGroup));
        monitorIdToViolationCount.put(monitor.getId(), violationsCount);
    }

    /**
     * Finds violations and returns violating devices
     * This method analyzes the individual point data from dynamicDataCache.
     * If the point data is not available in cache it will ask dispatch for it.
     */
    private Set<SimpleDevice> findViolations(DeviceDataMonitor monitor,
            Multimap<SimpleDevice, Attribute> devicesToAttribute) {

        Set<SimpleDevice> violatingDevices = Sets.newHashSet();
        Multimap<Attribute, SimpleDevice> attributeToDevice = HashMultimap.create();
        Multimaps.invertFrom(devicesToAttribute, attributeToDevice);

        for (Attribute attribute : attributeToDevice.keySet()) {
            // for monitor group and attribute - find device
            Collection<SimpleDevice> devices = attributeToDevice.get(attribute);

            // already in violation
            devices.removeIf(d -> violatingDevices.contains(d));

            if (devices.isEmpty()) {
                continue;
            }
            BiMap<PaoIdentifier, LitePoint> points = attributeService.getPoints(devices, (BuiltInAttribute) attribute);

            Map<Integer, SimpleDevice> pointIdsToPao = points.entrySet().stream().collect(
                Collectors.toMap(p -> p.getValue().getPointID(), p -> new SimpleDevice(p.getKey())));

            if (pointIdsToPao.isEmpty()) {
                continue;
            }

            // Gets point values from cache, if the points values are not available in cache, asks
            // dispatch for the point data
            Set<? extends PointValueQualityHolder> pointValues =
                asyncDynamicDataSource.getPointValues(pointIdsToPao.keySet());
            Map<Integer, Integer> pointIdsToStateGroup =
                points.values().stream().collect(Collectors.toMap(p -> p.getLiteID(), p -> p.getStateGroupID()));

            // find processor for the attribute
            DeviceDataMonitorProcessor processor = monitor.getProcessor((BuiltInAttribute) attribute);

            for (PointValueQualityHolder value : pointValues) {
                SimpleDevice pao = pointIdsToPao.get(value.getId());
                if (violatingDevices.contains(pao)) {
                    continue;
                }
                if (isViolating(processor, pointIdsToStateGroup.get(value.getId()), value)) {
                    log.debug("Found violation for processor:{} point value:{}", processor, value);
                    violatingDevices.add(pao);
                }
            }
        }

        return violatingDevices;
    }

    /**
     * Returns true if violation found
     */
    private boolean isViolating(DeviceDataMonitorProcessor processor, Integer stateGroupId,
            PointValueQualityHolder pointValue) {
        
        if (pointValue != null && pointValue.getPointQuality().isInvalid()) {
            log.debug("Point Quality is invalid - Attribute {} Processor type:{} Point value:{}.", processor.getAttribute(),
                processor.getType(), pointValue);
        }
        
        if (pointValue != null && !pointValue.getPointQuality().isInvalid()) {
            PointType type = PointType.getForId(pointValue.getType());
            if (processor.getType() == ProcessorType.STATE
                && processor.getStateGroup().getStateGroupID() == stateGroupId && type.isStatus()) {
                return processor.getState().getStateRawState() == (int) pointValue.getValue();
            } else if (processor.getType() == ProcessorType.RANGE && type.isValuePoint()) {
                return Range.open(processor.getRangeMin(), processor.getRangeMax()).contains(pointValue.getValue());
            } else if (processor.getType() == ProcessorType.LESS && type.isValuePoint()) {
                return pointValue.getValue() < processor.getProcessorValue();
            } else if (processor.getType() == ProcessorType.GREATER && type.isValuePoint()) {
                return pointValue.getValue() > processor.getProcessorValue();
            }
        }
        return false;
    }

    /**
     * Send smart notification events for devices that are entering or exiting violation. Events will not be sent for
     * devices that were previously in violation and are still in violation.
     */
    private void sendSmartNotifications(Set<Integer> oldViolatingDeviceIds, Set<Integer> newViolatingDeviceIds, DeviceDataMonitor monitor) {
       
        log.debug("Creating smart notification events for monitor: " + monitor);
        
        Instant now = Instant.now();

        Set<Integer> enteringViolationDeviceIds = new HashSet<>(newViolatingDeviceIds);
        enteringViolationDeviceIds.removeAll(oldViolatingDeviceIds);

        Set<Integer> exitingViolationDeviceIds = new HashSet<>(oldViolatingDeviceIds);
        exitingViolationDeviceIds.removeAll(newViolatingDeviceIds);

        List<SmartNotificationEvent> events = Lists.newArrayList(
            Iterables.concat(getEvents(monitor, enteringViolationDeviceIds, MonitorState.IN_VIOLATION, now),
                getEvents(monitor, exitingViolationDeviceIds, MonitorState.OUT_OF_VIOLATION, now)));
        
        log.debug("Sending event=" + events);
        smartNotificationEventCreationService.send(SmartNotificationEventType.DEVICE_DATA_MONITOR, events);
    }
        
    /**
     * Creates smart notification events.
     */
    private List<SmartNotificationEvent> getEvents(DeviceDataMonitor monitor, Set<Integer> ids, MonitorState state,
            Instant now) {
        return ids.stream().map(paoId -> DeviceDataMonitorEventAssembler.assemble(now, monitor.getId(),
            monitor.getName(), state, paoId)).collect(Collectors.toList());
    }
    
    //this method is public because unit tests overrides it
    public void sendSmartNotificationEvent(DeviceDataMonitor monitor, int deviceId, MonitorState state) {
        List<SmartNotificationEvent> events = getEvents(monitor, Sets.newHashSet(deviceId), state, new Instant());
        log.debug("Sending event=" + events);
        smartNotificationEventCreationService.send(SmartNotificationEventType.DEVICE_DATA_MONITOR, events);
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }
}
