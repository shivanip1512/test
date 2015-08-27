package com.cannontech.amr.deviceDataMonitor.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.springframework.util.CollectionUtils;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitorProcessor;
import com.cannontech.amr.deviceDataMonitor.service.DeviceDataMonitorCalculationService;
import com.cannontech.amr.monitors.DeviceDataMonitorCacheService;
import com.cannontech.amr.monitors.message.DeviceDataMonitorMessage;
import com.cannontech.amr.monitors.message.DeviceDataMonitorStatusRequest;
import com.cannontech.amr.monitors.message.DeviceDataMonitorStatusResponse;
import com.cannontech.amr.worker.ServiceWorker;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigInteger;
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
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;
import com.cannontech.message.dispatch.DispatchClientConnection;
import com.cannontech.yukon.conns.ConnPool;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class DeviceDataMonitorCalculationImpl extends ServiceWorker<DeviceDataMonitorMessage> implements
        DeviceDataMonitorCalculationService, MessageListener  {
    
    @Autowired private AttributeService attributeService;
    @Autowired private ConnPool connPool;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private DeviceDataMonitorCacheService deviceDataMonitorCacheService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DynamicDataSource dynamicDataSource;
    @Autowired private PointDao pointDao;
    private JmsTemplate jmsTemplate;
    
    private static final Logger log = YukonLogManager.getLogger(DeviceDataMonitorCalculationImpl.class);
    private DispatchClientConnection dispatchConnection;
    
    @PostConstruct
    public void init() {
        initServiceWorker();
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
                log.debug("Recieved:" +object.getClass() + "  "+object);
                if (object instanceof DeviceDataMonitorStatusRequest) {
                    Integer monitorId = ((DeviceDataMonitorStatusRequest) object).getMonitorId();
                    boolean isWorkingOnObject = isWorkingOnObject(monitorId);
                    String correlationId = message.getJMSCorrelationID();
                    jmsTemplate.convertAndSend(message.getJMSReplyTo(), new DeviceDataMonitorStatusResponse(
                        isWorkingOnObject), new CorrelationIdPostProcessor(correlationId));
                    log.debug("Sent reply for monitorId=" + monitorId + " isWorkingOnObject=" + isWorkingOnObject);
                } else if (object instanceof DeviceDataMonitorMessage) {
                    addObjectToQueue(((DeviceDataMonitorMessage) object));
                }
            } catch (JMSException e) {
                log.error("Unable to extract message", e);
            }
        }
    }

    @Override
    protected void processWorkerObject(DeviceDataMonitorMessage msg) throws InterruptedException {
        if (msg.isBeforeSave()) {
            recalculateViolatingPaosForMonitorBeforeSave(msg);
        } else {
            recalculateViolatingPaosForMonitor(msg.getUpdatedMonitor());
        }
    }

    @Override
    protected int getWorkerCount() {
        return configurationSource.getInteger(MasterConfigInteger.DEVICE_DATA_MONITOR_WORKER_COUNT, 3);
    }

    @Override
    protected int getQueueSize() {
        return configurationSource.getInteger(MasterConfigInteger.DEVICE_DATA_MONITOR_QUEUE_SIZE, 10);
    }

    @Override
    protected String getWorkerName() {
        return "DeviceDataMonitorWorker";
    }

    @Override
    protected Collection<Class<? extends Exception>> getRecoverableExceptions() {
        /**
         * DynamicDataAccessException is thrown when dispatch is "online" however it is not able to respond to the request for
         *  pointData within the allotted timeout period (30s). Thrown from getPaosWithCurrentPointValuesMatchingAProcessor
         */
        return ImmutableList.of(DynamicDataAccessException.class);
    }
    
    /**
     * Find paos in "violation" (having current point values that match a DeviceDataMonitorProcessor),
     * then add them to the violations device group. This method is only called from a worker thread at ServiceManager
     * startup... and whenever a device is added/removed from one of our monitoring device groups.
     * 
     * There are no smarts in this method (unlike the recalculateViolatingPaosForMonitorBeforeSave method - which is smart about only doing the
     * recalculation when necessary) for conditionally performing the recalculation. This method will do it no matter.
     * 
     * @throws InterruptedException 
     */
    private void recalculateViolatingPaosForMonitor(DeviceDataMonitor monitor) throws InterruptedException {
        Set<PaoIdentifier> paosInViolation = findPaosInViolation(monitor);
        addMonitorViolatingPaos(monitor, paosInViolation);
    }

    
    /**
     * **Conditionally** find paos in "violation" (having current point values that match a DeviceDataMonitorProcessor),
     * then add them to the violations device group. The "violation calculation" is conditional on several factors:
     *
     * (the following logic lives in the "shouldFindViolatingPaosBeforeSave" method)
     * 
     * If either of the following are true then the recalculation will NOT happen:
     * 1) The monitor has no processors
     * 2) The monitor is disabled
     * 
     * If both of the above are false, then we will perform the violation recalculation if ANY of the following are true
     * 
     * 1) The monitor doesn't yet exist in the database
     * 2) The monitoring group has been changed
     * 3) The monitor's processors have changed
     * 4) The monitor is being enabled
     * 
     * @throws InterruptedException 
     */
    private void recalculateViolatingPaosForMonitorBeforeSave(DeviceDataMonitorMessage message) throws InterruptedException {
        
        String basePath = deviceGroupService.getFullPath(SystemGroupEnum.DEVICE_DATA);
        DeviceDataMonitor updatedMonitor = message.getUpdatedMonitor();
        DeviceDataMonitor existingMonitor = message.getOldMonitor();

        // rename or create device groups if need be
        if (existingMonitor == null) {
            deviceGroupEditorDao.getStoredGroup(SystemGroupEnum.DEVICE_DATA, updatedMonitor.getViolationsDeviceGroupName(), true);
            log.info("Created new device group for Device Data Monitor: " + basePath + updatedMonitor.getViolationsDeviceGroupName());

        } else {
            if (shouldUpdateViolationsGroupNameBeforeSave(updatedMonitor, existingMonitor)) {
                // get existing /Monitors/DeviceData/_monitor_name group
                DeviceGroup existingViolationGroup = deviceGroupService.resolveGroupName(SystemGroupEnum.DEVICE_DATA, updatedMonitor.getViolationsDeviceGroupName());
                StoredDeviceGroup existingViolationStoredGroup = deviceGroupEditorDao.getStoredGroup(existingViolationGroup);
                
                // update it's name, then save it
                existingViolationStoredGroup.setName(updatedMonitor.getViolationsDeviceGroupName());
                deviceGroupEditorDao.updateGroup(existingViolationStoredGroup);
                log.info("Updated existing device group (" +  basePath + existingMonitor.getViolationsDeviceGroupName() + ") for Device Data Monitor to: " + basePath + updatedMonitor.getViolationsDeviceGroupName());
            } else {
                log.debug("No updates needed to Device Data Monitor device group: " + basePath + existingMonitor.getViolationsDeviceGroupName());
            }
        }

        Set<PaoIdentifier> paosInViolation = Sets.newHashSet();
        if (shouldFindViolatingPaosBeforeSave(updatedMonitor, existingMonitor)) {
            paosInViolation = findPaosInViolation(updatedMonitor);
        }
        addMonitorViolatingPaos(updatedMonitor, paosInViolation);
    }
    
    /**
     * Adds a Set of violating PaoIdentifiers to our monitor's violation device group (something similar
     * to /Meters/Monitors/DeviceData/_monitor_name. See DeviceDataMonitor.getViolationsDeviceGroupPath)
     */
    private void addMonitorViolatingPaos(DeviceDataMonitor monitor, Set<PaoIdentifier> paosInViolation) {;
        StoredDeviceGroup violationsGroup;
        try {
            violationsGroup = deviceGroupEditorDao.getStoredGroup(SystemGroupEnum.DEVICE_DATA, monitor.getViolationsDeviceGroupName(), false);
        } catch (NotFoundException nfe) {
            /* the user either deleted this monitor or directly deleted this monitor's violations device group
             * between starting this async calculation and right now. Just returning silently.
             */
            return;
        }
        List<SimpleDevice> currentViolationDevices = deviceGroupMemberEditorDao.getChildDevices(violationsGroup);
        String basePath = deviceGroupService.getFullPath(SystemGroupEnum.DEVICE_DATA);
        if (paosInViolation.isEmpty()) {
            if (currentViolationDevices.isEmpty()) {
                // nothing to do here!
                log.debug("No violating devices for monitor " + monitor.getName() + " or devices in our violations group " + basePath + monitor.getViolationsDeviceGroupName());
                return;
            }
            // clear out our violations group
            log.debug("No violating devices for monitor " + monitor.getName() + ", but we have existing devices in our violations group (" + basePath + monitor.getViolationsDeviceGroupName() + " that are being cleared out");
            removeDevicesInGroupForMonitor(violationsGroup, monitor);
        } else {
            if (currentViolationDevices.isEmpty()) {
                // add our violating paos to our violations group
                log.debug("Paos in violation has size " + paosInViolation.size() + " and the current violation device group is empty");
                addViolatingDevicesToGroupForMonitor(violationsGroup, monitor, paosInViolation);
                return;
            }
            // check if our violating devices are the same as the ones in our group
            List<PaoIdentifier> currentViolationPaos = Lists.newArrayList(Lists.transform(currentViolationDevices,
                SimpleDevice.TO_PAO_IDENTIFIER));
            List<PaoIdentifier> paosInViolationList = Lists.newArrayList();
            Collections.sort(currentViolationPaos, PaoIdentifier.COMPARATOR);
            Collections.sort(paosInViolationList, PaoIdentifier.COMPARATOR);

            if (!paosInViolationList.equals(currentViolationPaos)) {
                // clear out our violations group, then
                // add our violating paos to our violations group
                log.debug("Paos currently in violation are different than the paos in the violation device group");
                removeDevicesInGroupForMonitor(violationsGroup, monitor);
                addViolatingDevicesToGroupForMonitor(violationsGroup, monitor, paosInViolation);
            }
        }
    }
    
    
    private void removeDevicesInGroupForMonitor(StoredDeviceGroup group, DeviceDataMonitor monitor) {
        String basePath = deviceGroupService.getFullPath(SystemGroupEnum.DEVICE_DATA);
        deviceGroupMemberEditorDao.removeAllChildDevices(group);
        log.info("Cleared out devices in device data monitor's (" + monitor.getName() + ") violations device group: " + basePath + monitor.getViolationsDeviceGroupName());
    }

    private void addViolatingDevicesToGroupForMonitor(StoredDeviceGroup violationsGroup, DeviceDataMonitor monitor, Set<PaoIdentifier> paosInViolation) {
        String basePath = deviceGroupService.getFullPath(SystemGroupEnum.DEVICE_DATA);
        // add all the devices we found that are in violation to our violations group
        deviceGroupMemberEditorDao.addDevices(violationsGroup, paosInViolation);
        log.info("Added " + paosInViolation.size() + " violating devices to device data monitor's (" + monitor.getName() + ") violations device group: " + basePath + monitor.getViolationsDeviceGroupName());
    }

    /**
     * Find paos having current point values matching a DeviceDataMonitorProcessor
     * @throws InterruptedException 
     */
    private Set<PaoIdentifier> findPaosInViolation(DeviceDataMonitor monitor) throws InterruptedException {
        DeviceGroup monitorGroup = deviceGroupService.findGroupName(monitor.getGroupName());

        if (CollectionUtils.isEmpty(monitor.getProcessors()) || monitorGroup == null || !monitor.isEnabled()){
            // no point in continuing
            return Sets.newHashSet();
        }

        Map<LitePoint, PaoPointIdentifier> litePointsToPaoPointIdentifiers = getPointIdToPaoPointIdentMap(monitor.getProcessorAttributes(), monitorGroup);
        Set<PaoIdentifier> paosInViolation = getPaosWithCurrentPointValuesMatchingAProcessor(monitor, litePointsToPaoPointIdentifiers);
        return paosInViolation;
    }

    /** 
     * Look's at each point's current value and see if it matches any of our processors
     * If we find a match then add that device to our paosInViolation set (which we then return)
    */
    private Set<PaoIdentifier> getPaosWithCurrentPointValuesMatchingAProcessor(DeviceDataMonitor monitor,
                                                                               Map<LitePoint, PaoPointIdentifier> litePointsToPaoPointIdentifiers)
            throws InterruptedException {
        log.debug("Looking for devices in our monitoring group who's current point values match any of our processors...");

        Map<Integer, LitePoint> pointIdToLitePoint = Maps.newHashMapWithExpectedSize(litePointsToPaoPointIdentifiers.keySet().size());
        for (LitePoint litePoint: litePointsToPaoPointIdentifiers.keySet()) {
            pointIdToLitePoint.put(litePoint.getPointID(), litePoint);
        }
        
        // wait for dispatch to be available before asking it for point data
        waitForDispatch();

        Set<? extends PointValueQualityHolder> matchingPointValues = dynamicDataSource.getPointValues(pointIdToLitePoint.keySet());
        
        // pull out the point ids
        Map<Integer, PointValueQualityHolder> pointIdToPVQH = Maps.newHashMap();
        for (PointValueQualityHolder pvqh: matchingPointValues) {
            pointIdToPVQH.put(pvqh.getId(), pvqh);
        }
        
        Set<PaoIdentifier> paosInViolation = Sets.newHashSet();
        for (PointValueQualityHolder pvqh : matchingPointValues) {
            boolean matchFound = false;
            LitePoint litePoint = pointIdToLitePoint.get(pvqh.getId());
            for (DeviceDataMonitorProcessor processor : monitor.getProcessors()) {
                checkWorkerInterrupted();
                
                // check state group
                if (!(litePoint.getStateGroupID() == processor.getStateGroup().getStateGroupID())) {
                    continue;
                }

                // check if our processor matches the point's value
                if (isPointValueMatch(processor, pvqh)) {
                    // sweet - we don't care about the other processors - we're adding this point's device
                    matchFound = true;
                    break;
                }
            }
            if (matchFound) {
                // PHEW - that was hard - add this point's device to our violations group Set
                paosInViolation.add(litePointsToPaoPointIdentifiers.get(litePoint).getPaoIdentifier());
            }
        }
        return paosInViolation;
    }
    

    @Override
    public boolean isPointValueMatch(DeviceDataMonitorProcessor processor, PointValueHolder pointValue) {
        int processorPointValue = processor.getState().getStateRawState();
        // Safety check for points that don't have a previous value yet in the database
        if (pointValue == null) {
            return false;
        }
        int pointValueAsInt = (int) pointValue.getValue();
        return processorPointValue == pointValueAsInt;
    }
    
    /**
     * Returns a map of PointId to PaoPointIdentifier of points that have an existing point
     * for any of our DeviceDataMonitorProcessor Attributes,
     * @throws InterruptedException 
     * 
     */
    private Map<LitePoint, PaoPointIdentifier> getPointIdToPaoPointIdentMap(Set<Attribute> attributes,
                                                                            DeviceGroup monitorGroup) throws InterruptedException {
        Set<PaoPointIdentifier> paoPointIdentToGetAsLitePoints = Sets.newHashSet();
        for (Attribute attribute : attributes) {
            // get all the devices in our monitoring group that support our attribute
            List<SimpleDevice> supportedMonitorGroupDevices = attributeService.getDevicesInGroupThatSupportAttribute(monitorGroup, attribute);

            // from that list get all the devices that have any attributes matching
            List<PaoMultiPointIdentifier> paoPointIdentifiers=
                    attributeService.findPaoMultiPointIdentifiersForAttributes(supportedMonitorGroupDevices, Collections.singleton(attribute));
            for (PaoMultiPointIdentifier paoMultiPointIdentifier : paoPointIdentifiers) {
                for (PaoPointIdentifier paoPointIdentifier : paoMultiPointIdentifier.getPaoPointIdentifiers()) {
                    checkWorkerInterrupted();

                    // this monitor currently only works on status points
                    if (paoPointIdentifier.getPointIdentifier().getPointType() != PointType.Status) {
                        continue;
                    }

                    // this monitor only works on devices
                    if (paoPointIdentifier.getPaoTypePointIdentifier().getPaoType().getPaoCategory() != PaoCategory.DEVICE) {
                        continue;
                    }

                    // add it to our list to get later
                    paoPointIdentToGetAsLitePoints.add(paoPointIdentifier);
                }
            }
        }
        Map<LitePoint, PaoPointIdentifier> litePointsToPaoPointIdentifiers = pointDao.getLitePointsForPaoPointIdentifiers(paoPointIdentToGetAsLitePoints);
        return litePointsToPaoPointIdentifiers;
    }
    
    @Override
    public boolean shouldFindViolatingPaosBeforeSave(DeviceDataMonitor updatedMonitor, DeviceDataMonitor existingMonitor) {
        if (CollectionUtils.isEmpty(updatedMonitor.getProcessors()) || !updatedMonitor.isEnabled()) {
            return false;
        }

        boolean monitorExists = existingMonitor != null;
        boolean monitoringGroupChanged = false;
        boolean processorsChanged = false;
        boolean enablingExistingMonitor = false;

        if (monitorExists) {
            // has the monitoring group been changed?
            monitoringGroupChanged = !existingMonitor.getGroupName().equals(updatedMonitor.getGroupName());
            
            // have any of the processors been changed? (sort both lists then compare equality)
            List<DeviceDataMonitorProcessor> existingProcessors = existingMonitor.getProcessors();
            Collections.sort(existingProcessors, DeviceDataMonitorProcessor.COMPARATOR);
            List<DeviceDataMonitorProcessor> updatedProcessors = updatedMonitor.getProcessors();
            Collections.sort(updatedProcessors, DeviceDataMonitorProcessor.COMPARATOR);
    
            processorsChanged = !existingProcessors.equals(updatedProcessors);
    
            // are we enabling an existing monitor?
            enablingExistingMonitor = !existingMonitor.isEnabled() && updatedMonitor.isEnabled();
        }
        return (!monitorExists || monitoringGroupChanged || processorsChanged || enablingExistingMonitor);
    }
    

    @Override
    public boolean shouldUpdateViolationsGroupNameBeforeSave(DeviceDataMonitor updatedMonitor, DeviceDataMonitor existingMonitor) {
        return !existingMonitor.getViolationsDeviceGroupName().equals(updatedMonitor.getViolationsDeviceGroupName());
    }
    
    private void waitForDispatch() throws InterruptedException {
        if (dispatchConnection == null) {
            dispatchConnection = connPool.getDefDispatchConn();
        }
        dispatchConnection.waitForValidConnection();
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        //jmsTemplate.setExplicitQosEnabled(true);
        //jmsTemplate.setDeliveryPersistent(false);
    }
}
