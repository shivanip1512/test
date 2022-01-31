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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.MessagePostProcessor;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitorProcessor;
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
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.smartNotification.model.DeviceDataMonitorEventAssembler;
import com.cannontech.common.smartNotification.model.DeviceDataMonitorEventAssembler.MonitorState;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.service.SmartNotificationEventCreationService;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.PointValueQualityTagHolder;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.DispatchClientConnection;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.conns.ConnPool;
import com.google.common.collect.BiMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
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
    @Autowired private PointDao pointDao;
    @Autowired private YukonJmsTemplate jmsTemplate;
    private DispatchClientConnection dispatchConnection;

    // monitors recalculating
    private Map<Integer, DeviceDataMonitor> pending = new ConcurrentHashMap<>();
    // monitorId / violation count
    private Map<Integer, Integer> monitorIdToViolationCount =  new ConcurrentHashMap<>();
    
    static final Logger log = YukonLogManager.getLogger(DeviceDataMonitorCalculationServiceImpl.class);

    @PostConstruct
    public void init() {

        log.info("Waiting " + MINUTES_TO_WAIT_TO_START_CALCULATION + " minutes to recalculate");

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
        cacheViolationGroupAndDeviceGroup(monitor);
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
                        if (ddm.getId().equals(monitor.getId())) {
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
                        startWork(monitorMessage.getUpdatedMonitor());
                    case DISABLE:
                        cacheViolationGroupAndDeviceGroup(monitorMessage.getUpdatedMonitor());
                        deviceGroupMemberEditorDao.removeAllChildDevices(monitorMessage.getUpdatedMonitor().getViolationGroup());
                        break;
                    case ENABLE:
                    case RECALCULATE:
                        startWork(monitorMessage.getUpdatedMonitor());
                        break;
                    case UPDATE:
                        updateViolationGroup(monitorMessage);
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

        Multimap<BuiltInAttribute, SimpleDevice> devicesToEvaluate =
            attributeService.getDevicesInGroupThatSupportAttribute(monitor.getGroup(), monitor.getAttributes(), null);
        
        List<SimpleDevice> devicesInViolationGroup =
                deviceGroupMemberEditorDao.getChildDevices(monitor.getViolationGroup());
        Set<Integer> inViolationGroup =
                devicesInViolationGroup.stream().map(SimpleDevice::getDeviceId).collect(Collectors.toSet());
        
        ViolatingDevices violatingDevices = new ViolatingDevices();
        if(!devicesToEvaluate.isEmpty()) {
            violatingDevices = findViolations(monitor, devicesToEvaluate);  
        }
        Set<SimpleDevice> allViolatingDevices = violatingDevices.getViolatingDevices();
        
        log.debug("{} recalculated violations, violations: {}, alarm-only violations: {}", monitor, 
                  allViolatingDevices.size(), violatingDevices.getAlarmsOnlyViolatingDevices().size());

        Set<Integer> violating = allViolatingDevices
                    .stream()
                    .map(SimpleDevice::getDeviceId)
                    .collect(Collectors.toSet());

        if (!Sets.symmetricDifference(inViolationGroup, violating).isEmpty()) {
            log.debug("{} removing all devices from violation group {}", monitor, monitor.getViolationGroup());

            deviceGroupMemberEditorDao.removeAllChildDevices(monitor.getViolationGroup());
            if (!allViolatingDevices.isEmpty()) {
                log.debug("{} adding {} devices to violation group {}", monitor, allViolatingDevices.size(),
                    monitor.getViolationGroup());
                deviceGroupMemberEditorDao.addDevices(monitor.getViolationGroup(), allViolatingDevices);
            }
        }
        log.debug("{} recaclulation is complete", monitor);

        updateViolationCacheCount(monitor);
        
        Set<Integer> alarmOnlyDeviceIds = violatingDevices.getAlarmsOnlyViolatingDevices()
                .stream()
                .map(SimpleDevice::getDeviceId)
                .collect(Collectors.toSet());
        sendSmartNotifications(inViolationGroup, violating, alarmOnlyDeviceIds, monitor);

    }
    
    /**
     * Check if any attribute, other then an attribute we got the point data for, is violating.
     */
    private boolean isOtherAttributeViolating(DeviceDataMonitor monitor, BuiltInAttribute attributeToExclude,
            SimpleDevice device) {
        boolean foundViolation = false;
        // find all points for monitored attributes, excluding the attribute we received the point data for
        Map<BuiltInAttribute, LitePoint> attributeToPoint = new HashMap<>();
        for (BuiltInAttribute attribute : monitor.getAttributes()) {
            if (attribute != attributeToExclude) {
                LitePoint point = attributeService.findPointForAttribute(device, attribute);
                if(point != null) {
                    attributeToPoint.put(attribute, point);
                }
            }
        }
        Set<Integer> otherPoints = attributeToPoint.values().stream().map(p -> p.getLiteID()).collect(Collectors.toSet());

        Map<Integer, PointValueQualityHolder> pointValues = new HashMap<>();

        if (!otherPoints.isEmpty()) {
            log.debug("{} asking dispatch for point data {}", monitor, otherPoints);
            pointValues.putAll(asyncDynamicDataSource.getPointDataOnce(otherPoints).stream().collect(
                Collectors.toMap(p -> p.getId(), p -> p)));
        }

        for (Map.Entry<BuiltInAttribute, LitePoint> entry : attributeToPoint.entrySet()) {
            int pointId = entry.getValue().getPointID();
            PointValueQualityHolder pointValue = pointValues.get(pointId);
            if (pointValue != null) {
                List<DeviceDataMonitorProcessor> processors = monitor.getProcessors(entry.getKey());
                foundViolation = ViolationHelper.isViolating(processors, entry.getValue().getStateGroupID(), pointValue);
                if (foundViolation) {
                    break;
                }
            }
        }
        return foundViolation;
    }

    @Override
    public void updateViolationsGroupBasedOnNewPointData(DeviceDataMonitor monitor, RichPointData richPointData,
            BuiltInAttribute attribute) {
        SimpleDevice device = new SimpleDevice(richPointData.getPaoPointIdentifier().getPaoIdentifier());
        try {
            log.debug("Started recalculation monitor:{} device:{} point:{} attribute:{}",
                    monitor.getName(), richPointData.getPaoPointIdentifier().getPaoIdentifier().getPaoId(),
                    richPointData.getPointValue(),
                    attribute);
            Boolean addRemoveFromGroup = recalculateViolation(monitor, richPointData, attribute);
            if (addRemoveFromGroup != null) {
                addRemoveFromViolationGroup(monitor, device, addRemoveFromGroup, richPointData.getPointValue());
            }
            updateViolationCacheCount(monitor);
            log.debug("Finished recalculation monitor:{} device:{} point:{} attribute:{}",
                    monitor.getName(), richPointData.getPaoPointIdentifier().getPaoIdentifier().getPaoId(),
                    richPointData.getPointValue(),
                    attribute);
        } catch (Exception e) {
            log.error("Error while updating violation for monitor:" + monitor.getName() + "device:" + device.getDeviceId()
                    + "pointId:" + richPointData.getPointValue().getId(), e);
        }
    }

    private Boolean recalculateViolation(DeviceDataMonitor monitor, RichPointData richPointData, BuiltInAttribute attribute){
        SimpleDevice device = new SimpleDevice(richPointData.getPaoPointIdentifier().getPaoIdentifier());
        LitePoint point = pointDao.getLitePoint(richPointData.getPointValue().getId());
        boolean inViolationsGroup = deviceGroupService.isDeviceInGroup(monitor.getViolationGroup(), device);    
        boolean foundViolation = ViolationHelper.isViolating(monitor.getProcessors(attribute), point.getStateGroupID(), richPointData.getPointValue());

        // no violation found but device is in violation group and this monitor is monitoring for more then 1
        // attribute
        if (!foundViolation && inViolationsGroup && monitor.getAttributes().size() > 1) {
            // check other processors for violation
            // if other violations found, keep the device in the group
            foundViolation = isOtherAttributeViolating(monitor, attribute, device);
        }
        Boolean shouldTheGroupBeModified = ViolationHelper.shouldTheGroupBeModified(inViolationsGroup, foundViolation);
        log.debug("monitor:{} device:{} point data:{} inViolationsGroup:{} violation found:{} shouldTheGroupBeModified:{}",
                monitor.getName(), device.getDeviceId(),
                richPointData.getPointValue(), inViolationsGroup, foundViolation, shouldTheGroupBeModified);
        return shouldTheGroupBeModified;
    }

    private boolean isValidDataForMonitorAlarmOnlySetting(DeviceDataMonitor monitor, PointValueQualityTagHolder pointData) {
        if (monitor.isNotifyOnAlarmOnly()) {
            return pointData.isTagsUnsolicited();
        }
        return true;
    }
    
    /**
     * Adds or removes device from violation group and sends notification
     */
    private void addRemoveFromViolationGroup(DeviceDataMonitor monitor, SimpleDevice device, boolean addToGroup,
            PointValueQualityTagHolder point) {
        // found violation and device is not in violation group
        if (addToGroup) {
            // add device to group
            int rowsAdded = deviceGroupMemberEditorDao.addDevice(monitor.getViolationGroup(), device);
            if (rowsAdded > 0 && isValidDataForMonitorAlarmOnlySetting(monitor, point)) {
                sendSmartNotificationEvent(monitor, device.getDeviceId(), MonitorState.IN_VIOLATION);
                log.debug("{} adding {} ({}) to violation group {}", monitor.getName(), device.getDeviceId(),
                        point, monitor.getViolationGroup());
            } else {
                log.debug("{} adding {} ({}) to violation group {}. Device already in group, no notification.", 
                          monitor.getName(), device.getDeviceId(), point, monitor.getViolationGroup());
            }
        } else {
            // remove device from group
            int rowsDeleted = deviceGroupMemberEditorDao.removeDevices(monitor.getViolationGroup(),
                    Collections.singleton(device));
            if (rowsDeleted > 0 && isValidDataForMonitorAlarmOnlySetting(monitor, point)) {
                sendSmartNotificationEvent(monitor, device.getDeviceId(), MonitorState.OUT_OF_VIOLATION);
                log.debug("{} removing {} ({}) from violation group {}", monitor.getName(), device.getDeviceId(), point,
                        monitor.getViolationGroup());
            } else {
                log.debug("{} removing {} ({}) from violation group {}. Device already removed from group, no notification.",
                        monitor.getName(), device.getDeviceId(), point, monitor.getViolationGroup());
            }
        }
    }

    /**
     * Cache violations count to be used when data updaters send request to SM to get the count.
     */
    public void updateViolationCacheCount(DeviceDataMonitor monitor) {
        StoredDeviceGroup violationsGroup = monitor.getViolationGroup();
        int violationsCount = deviceGroupService.getDeviceCount(Collections.singleton(violationsGroup));
        monitorIdToViolationCount.put(monitor.getId(), violationsCount);
    }

    /**
     * Finds violations and returns violating devices
     * This method analyzes the individual point data from dynamicDataCache.
     * If the point data is not available in cache it will ask dispatch for it.
     */
    private ViolatingDevices findViolations(DeviceDataMonitor monitor,
            Multimap<BuiltInAttribute, SimpleDevice> attributeToDevice) {
      
        Map<BuiltInAttribute, Map<Integer, SimpleDevice>> attributeToPoints = new HashMap<>();
        Set<Integer> allPoints = new HashSet<>();
        Map<Integer, Integer> pointIdsToStateGroup = new HashMap<>();
        
        for (BuiltInAttribute attribute : attributeToDevice.keySet()) {
            // for monitor group and attribute - find device
            Collection<SimpleDevice> devices = attributeToDevice.get(attribute);
            if (devices.isEmpty()) {
                continue;
            }
            BiMap<PaoIdentifier, LitePoint> points = attributeService.getPoints(devices, attribute);
            pointIdsToStateGroup.putAll(points.values().stream().collect(Collectors.toMap(p -> p.getLiteID(), p -> p.getStateGroupID())));
                    
            Map<Integer, SimpleDevice> pointIdsToPao = points.entrySet().stream().collect(
                Collectors.toMap(p -> p.getValue().getPointID(), p -> new SimpleDevice(p.getKey())));
            allPoints.addAll(pointIdsToPao.keySet());

            if (!pointIdsToPao.isEmpty()) {
                attributeToPoints.put(attribute, pointIdsToPao);
            }
        }
        Map<Integer, PointValueQualityTagHolder> pointValues = asyncDynamicDataSource.getPointDataOnce(allPoints).stream()
                .collect(Collectors.toMap(p -> p.getId(), p -> p));

        return ViolationHelper.findViolatingDevices(monitor, attributeToPoints, pointIdsToStateGroup, pointValues);
    }

    /**
     * Send smart notification events for devices that are entering or exiting violation. Events will not be sent for
     * devices that were previously in violation and are still in violation.
     */
    private void sendSmartNotifications(Set<Integer> oldViolatingDeviceIds, Set<Integer> newViolatingDeviceIds,
            Set<Integer> alarmOnlyDeviceIds, DeviceDataMonitor monitor) {
       
        log.debug("Creating smart notification events for monitor: {}", monitor);
        
        Instant now = Instant.now();

        Set<Integer> enteringViolationDeviceIds = new HashSet<>(newViolatingDeviceIds);
        enteringViolationDeviceIds.removeAll(oldViolatingDeviceIds);
        if (monitor.isNotifyOnAlarmOnly()) {
            enteringViolationDeviceIds = enteringViolationDeviceIds.stream()
                    .filter(alarmOnlyDeviceIds::contains)
                    .collect(Collectors.toSet());
        }
        
        Set<Integer> exitingViolationDeviceIds = new HashSet<>(oldViolatingDeviceIds);
        exitingViolationDeviceIds.removeAll(newViolatingDeviceIds);
        if (monitor.isNotifyOnAlarmOnly()) {
            exitingViolationDeviceIds = exitingViolationDeviceIds.stream()
                    .filter(alarmOnlyDeviceIds::contains)
                    .collect(Collectors.toSet());
        }

        List<SmartNotificationEvent> events = Lists.newArrayList(
            Iterables.concat(getEvents(monitor, enteringViolationDeviceIds, MonitorState.IN_VIOLATION, now),
                getEvents(monitor, exitingViolationDeviceIds, MonitorState.OUT_OF_VIOLATION, now)));
        
        log.debug("Sending event={}", events);
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
        //TODO: check the flaggy flag
        smartNotificationEventCreationService.send(SmartNotificationEventType.DEVICE_DATA_MONITOR, events);
    }
}
