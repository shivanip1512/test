package com.cannontech.amr.deviceDataMonitor.service.impl;

import java.io.Serializable;
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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitorProcessor;
import com.cannontech.amr.deviceDataMonitor.service.DeviceDataMonitorCalculationService;
import com.cannontech.amr.monitors.DeviceDataMonitorCacheService;
import com.cannontech.amr.monitors.message.DeviceDataMonitorMessage;
import com.cannontech.amr.monitors.message.DeviceDataMonitorStatusRequest;
import com.cannontech.amr.monitors.message.DeviceDataMonitorStatusResponse;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.database.data.point.PointType;
import com.cannontech.message.dispatch.DispatchClientConnection;
import com.cannontech.yukon.conns.ConnPool;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;

public class DeviceDataMonitorCalculationServiceImpl implements DeviceDataMonitorCalculationService, MessageListener {

    private static final int MINUTES_TO_WAIT_TO_START_CALCULATION = 5;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private Executor executor = Executors.newCachedThreadPool();
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private AttributeService attributeService;
    @Autowired private ConnPool connPool;
    @Autowired private DeviceDataMonitorCacheService deviceDataMonitorCacheService;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private PointDao pointDao;

    private JmsTemplate jmsTemplate;
    private DispatchClientConnection dispatchConnection;

    // monitors recalculating
    private Map<Integer, DeviceDataMonitor> pending = Collections.synchronizedMap(new HashMap<>());

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

                deviceDataMonitorCacheService.getAllEnabledMonitors().forEach(monitor -> {
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
        log.debug("Starting work " + monitor);
        executor.execute(() -> {
            try {
                boolean isWorkingOnObject = pending.containsKey(monitor.getId());
                if (!isWorkingOnObject) {
                    pending.put(monitor.getId(), monitor);
                    recalculateViolations(monitor);
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
                log.error(monitor, e);
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
                    jmsTemplate.convertAndSend(message.getJMSReplyTo(),
                        new DeviceDataMonitorStatusResponse(isWorkingOnObject),
                        new CorrelationIdPostProcessor(correlationId));
                } else if (object instanceof DeviceDataMonitorMessage) {
                    DeviceDataMonitorMessage monitorMessage = (DeviceDataMonitorMessage) object;
                    log.debug(monitorMessage);
                    switch (monitorMessage.getAction()) {
                    case CREATE:
                        createViolationGroup(monitorMessage.getUpdatedMonitor());
                        startWork(monitorMessage.getUpdatedMonitor());
                    case DISABLE:
                        clearViolationGroup(monitorMessage.getUpdatedMonitor());
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
    
    /**
     * Removes all devices from violation group
     */
    private void clearViolationGroup(DeviceDataMonitor monitor) {
        StoredDeviceGroup violationsGroup = deviceGroupEditorDao.getStoredGroup(SystemGroupEnum.DEVICE_DATA,
            monitor.getViolationsDeviceGroupName(), false);
        deviceGroupMemberEditorDao.removeAllChildDevices(violationsGroup);
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
            }
        }
    }
    
    /**
     * Creates violation group
     */
    private void createViolationGroup(DeviceDataMonitor monitor) {

        log.debug("Creating new device group " + monitor);
        deviceGroupEditorDao.getStoredGroup(SystemGroupEnum.DEVICE_DATA, monitor.getViolationsDeviceGroupName(), true);
    }

    /**
     * Recalculates violations for monitor
     * This method does FULL recalculation of violations
     */
    private void recalculateViolations(DeviceDataMonitor monitor) throws InterruptedException {

        log.info("Recalculating violations " + monitor);
        
        DeviceGroup monitorGroup = deviceGroupService.findGroupName(monitor.getGroupName());

        Set<SimpleDevice> devicesInGroupAndSubgroups = new HashSet<SimpleDevice>();
        monitor.getProcessorAttributes().forEach(attribute -> {
            devicesInGroupAndSubgroups.addAll(attributeService.getDevicesInGroupThatSupportAttribute(monitorGroup, attribute));
        });

        Set<PaoIdentifier> devicesInViolation =
            findViolations(devicesInGroupAndSubgroups, monitor.getProcessorAttributes(), monitor.getProcessors());
        log.debug("Found violations " + devicesInViolation.size() + " " + monitor);

        StoredDeviceGroup violationsGroup = deviceGroupEditorDao.getStoredGroup(SystemGroupEnum.DEVICE_DATA,
            monitor.getViolationsDeviceGroupName(), false);
        List<SimpleDevice> devicesInViolationGroup = deviceGroupMemberEditorDao.getChildDevices(violationsGroup);

        Set<Integer> inViolationGroup =
            devicesInViolationGroup.stream().map(SimpleDevice::getDeviceId).collect(Collectors.toSet());
        Set<Integer> violating = devicesInViolation.stream().map(PaoIdentifier::getPaoId).collect(Collectors.toSet());

        if (!Sets.symmetricDifference(inViolationGroup, violating).isEmpty()) {
            log.debug("Removing all devices from violation group " + monitor);
            deviceGroupMemberEditorDao.removeAllChildDevices(violationsGroup);
            if (!devicesInViolation.isEmpty()) {
                log.debug("Adding devices " + devicesInViolation.size() + " to violation group " + monitor);
                deviceGroupMemberEditorDao.addDevices(violationsGroup, devicesInViolation);
            }
        }
        log.info("Recalculation done for " + monitor);
    }

    /**
     * Finds violations and returns violating devices
     * This method analyzes the individual point data from dynamicDataCache.
     * If the point data is not available in cache it will ask dispatch for it.
     */
    private Set<PaoIdentifier> findViolations(Set<SimpleDevice> devicesInGroup, Set<Attribute> attributes,
            List<DeviceDataMonitorProcessor> processors) throws InterruptedException {

        List<PaoMultiPointIdentifier> identifiers =
            attributeService.findPaoMultiPointIdentifiersForAttributes(devicesInGroup, attributes);

        Set<PaoPointIdentifier> paoPointIdentifiers = new HashSet<>();

        identifiers.forEach(identifier -> {
            paoPointIdentifiers.addAll(identifier.getPaoPointIdentifiers().stream().filter(
                (x) -> x.getPointIdentifier().getPointType() == PointType.Status
                    && x.getPaoTypePointIdentifier().getPaoType().getPaoCategory() == PaoCategory.DEVICE).collect(
                        Collectors.toSet()));
        });

        Map<PaoPointIdentifier, PointInfo> paoToPoint = pointDao.getPointInfoById(paoPointIdentifiers);

        Map<Integer, PointInfo> points =
            paoToPoint.entrySet().stream().collect(Collectors.toMap(e -> e.getValue().getPointId(), e -> e.getValue()));

        //Gets point values from cache, if the points values are not available in cache, asks dispatch for the point data   
        Set<? extends PointValueQualityHolder> pointValues = asyncDynamicDataSource.getPointValues(points.keySet());

        Map<PointInfo, PaoPointIdentifier> pointToPao = HashBiMap.create(paoToPoint).inverse();

        Set<PaoIdentifier> violatingDevices = Sets.newHashSet();
        pointValues.forEach(value -> {
            PointInfo pointInfo = points.get(value.getId());
            processors.forEach(processor -> {
                if (pointInfo.getStateGroupId() == processor.getStateGroup().getStateGroupID()
                    && isPointValueMatch(processor, value)) {
                    violatingDevices.add(pointToPao.get(pointInfo).getPaoIdentifier());
                }
            });
        });
        return violatingDevices;
    }

    @Override
    public boolean isPointValueMatch(DeviceDataMonitorProcessor processor, PointValueHolder pointValue) {

        int processorPointValue = processor.getState().getStateRawState();
        if (pointValue == null) {
            return false;
        }
        int pointValueAsInt = (int) pointValue.getValue();
        return processorPointValue == pointValueAsInt;
    }

    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }
}
