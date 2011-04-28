package com.cannontech.amr.phaseDetect.service.impl;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.phaseDetect.data.DetectedPhase;
import com.cannontech.amr.phaseDetect.data.PhaseDetectData;
import com.cannontech.amr.phaseDetect.data.PhaseDetectResult;
import com.cannontech.amr.phaseDetect.data.PhaseDetectState;
import com.cannontech.amr.phaseDetect.data.PhaseDetectVoltageReading;
import com.cannontech.amr.phaseDetect.data.UndefinedPhaseException;
import com.cannontech.amr.phaseDetect.service.PhaseDetectService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.device.groups.dao.DeviceGroupPermission;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.common.device.service.RouteBroadcastService;
import com.cannontech.common.device.service.RouteBroadcastService.CompletionCallback;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.enums.Phase;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class PhaseDetectServiceImpl implements PhaseDetectService{

    private CommandRequestDeviceExecutor commandRequestExecutor;
    private RouteBroadcastService routeBroadcastService;
    private Logger log = YukonLogManager.getLogger(PhaseDetectService.class);
    
    /*
     *  Internal state for the one global phase detection process 
     *  that is allowed to happen at any given time.
     */
    private PhaseDetectData phaseDetectData = null;
    private PhaseDetectState phaseDetectState = null;
    private PhaseDetectResult phaseDetectResult = null;
    
    private TemporaryDeviceGroupService temporaryDeviceGroupService;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private DeviceGroupProviderDao deviceGroupProviderDao;
    private StateDao stateDao;
    private DynamicDataSource dynamicDataSource;
    private DeviceGroupEditorDao deviceGroupEditorDao;
    private DeviceGroupService deviceGroupService;
    private RecentResultsCache<PhaseDetectResult> phaseDetectResultsCache;
    private AttributeService attributeService;
    
    private static final String SYSTEM_METERS_GROUP = "/System/Meters/";
    private static final String PHASE_DETECT_GROUP = "Phase Detect";
    private static final String LAST_RESULTS_GROUP = "Last Results";
    private static final int DEFAULT_TIMEOUT = 60; /* Seconds */

    @Override
    public void clearPhaseData(LiteYukonUser user) {
        /* mct410base comes from the paoDefinition.xml file and represents all mct 410s. */
        /* In the future it would be retrieved from some enum builts on the paoDefinition.xml file. */
        String command = "putconfig emetcon phasedetect clear broadcast MCT_410_BASE"; 
        
        final CountDownLatch finishedLatch = new CountDownLatch(1);
        
        CompletionCallback callback = new CompletionCallback() {
            @Override
            public void success() {
                phaseDetectResult.setErrorMsg(null);
                finishedLatch.countDown();
            }
            @Override
            public void failure(String errorReason) {
                phaseDetectResult.setErrorMsg(errorReason);
                finishedLatch.countDown();
            }
        };
        routeBroadcastService.broadcastCommand(command, phaseDetectData.getBroadcastRoutes(), DeviceRequestType.PHASE_DETECT_CLEAR, callback , user);
        try {
            boolean finishedBeforeTimeout = finishedLatch.await(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            if(!finishedBeforeTimeout){
                callback.failure("Timeout waiting for clear command.");
            }
        } catch(InterruptedException e) {
            // do nothing
        } 
    }
    
    @Override
    public void startPhaseDetect(LiteYukonUser user, final Phase phase) {
        String delta = phaseDetectData.getDeltaVoltage().toString();
        String interval = phaseDetectData.getIntervalLength().toString();
        String num = phaseDetectData.getNumIntervals().toString();
        String command = "putconfig emetcon phasedetect broadcast MCT_410_BASE phase " + phase.name() + " delta " + delta + " interval " + interval + " num " + num;
        
        final CountDownLatch finishedLatch = new CountDownLatch(1);
        
        CompletionCallback callback = new CompletionCallback() {
            @Override
            public void success() {
                phaseDetectState.setPhaseDetectSent(phase);
                phaseDetectResult.setErrorMsg(null);
                finishedLatch.countDown();
            }
            @Override
            public void failure(String errorReason) {
                phaseDetectResult.setErrorMsg(errorReason);
                finishedLatch.countDown();
            }
        };
        routeBroadcastService.broadcastCommand(command, phaseDetectData.getBroadcastRoutes(), DeviceRequestType.PHASE_DETECT_COMMAND, callback , user);
        try {
            boolean finishedBeforeTimeout = finishedLatch.await(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            if(!finishedBeforeTimeout){
                callback.failure("Timeout waiting for send command.");
            }
        } catch(InterruptedException e) {
            // do nothing
        } 
    }
    
    @Override
    public void readPhaseDetect(final List<SimpleDevice> devices, final Phase phaseBeingRead, final LiteYukonUser user) {
        final String command = "getconfig emetcon phasedetect read ";
        phaseDetectResult.setErrorMsg(null);
        ObjectMapper<YukonDevice, CommandRequestDevice> objectMapper = new ObjectMapper<YukonDevice, CommandRequestDevice>() {
            public CommandRequestDevice map(YukonDevice from) throws ObjectMappingException {
                CommandRequestDevice request = new CommandRequestDevice();
                request.setDevice(new SimpleDevice(from.getPaoIdentifier()));
                request.setCommand(command);
                return request;
            }
        };
        
        List<CommandRequestDevice> requests = new MappingList<YukonDevice, CommandRequestDevice>(devices, objectMapper);
        
        CommandCompletionCallbackAdapter<CommandRequestDevice> callback = new CommandCompletionCallbackAdapter<CommandRequestDevice>() {
            
            @Override
            public void receivedLastResultString(CommandRequestDevice command, String value) {
                Phase phaseDetected = null;
                try {
                    phaseDetected = parsePhase(value);
                    PhaseDetectVoltageReading voltageReading = parseVoltageReading(value);
                    
                    if (phaseDetected != phaseBeingRead) {
                        // do something, add to failure? ignore?
                    }
                    
                    StoredDeviceGroup storedDeviceGroup = phaseDetectResult.getPhaseToGroupMap().get(phaseDetected);
                    
                    deviceGroupMemberEditorDao.addDevices(storedDeviceGroup, command.getDevice());
                    /* Map the phase to the reading returned */
                    Map<String, PhaseDetectVoltageReading> phaseToReadingMap = phaseDetectResult.getDeviceReadingsMap().get(command.getDevice().getDeviceId());
                    if(phaseToReadingMap == null){
                        phaseToReadingMap = Maps.newHashMap();
                    }
                    if(!phaseDetectData.isReadAfterAll()){
                        phaseToReadingMap.put(phaseBeingRead.name(), voltageReading);
                    }
                    /* Map the device to the reading map */
                    phaseDetectResult.getDeviceReadingsMap().put(command.getDevice().getDeviceId(), phaseToReadingMap);
                } catch (UndefinedPhaseException e) {
                    // ignore
                }
            }
            
            @Override
            public void receivedLastError(CommandRequestDevice command, DeviceErrorDescription error) {
                phaseDetectResult.getFailureGroupMap().put(command.getDevice(), error.getPorter());
            }
            
            @Override
            public void processingExceptionOccured(String reason) {
                phaseDetectResult.setErrorMsg(reason);
            }
            
            @Override
            public void complete() {
                if(phaseDetectData.isReadAfterAll() || phaseDetectState.isPhaseDetectComplete()){
                    clearPhaseDetectGroups();
                    proccessReadResults(devices, phaseDetectResult.getPhaseToGroupMap(), user);
                }
            }
            
        };
        
        CommandRequestExecutionIdentifier id = commandRequestExecutor.execute(requests, callback, DeviceRequestType.PHASE_DETECT_READ, user);
        phaseDetectResult.setCommandRequestExecutionIdentifier(id);
        phaseDetectResult.setCallback(callback);
    }
    
    public void cancelReadPhaseDetect(LiteYukonUser user){
        try{
            commandRequestExecutor.cancelExecution(phaseDetectResult.getCallback(), user);
        } catch (Exception e){
            log.warn("Unable to cancel phase detect read.", e);
        }
    }
    
    public void clearPhaseDetectGroups() {
        for(DetectedPhase detectedPhase : DetectedPhase.values()){
            try {
                DeviceGroup group = deviceGroupService.resolveGroupName(SYSTEM_METERS_GROUP + PHASE_DETECT_GROUP +"/"+ LAST_RESULTS_GROUP +"/" + detectedPhase.name());
                StoredDeviceGroup storedGroup = deviceGroupEditorDao.getStoredGroup(group);
                deviceGroupMemberEditorDao.removeAllChildDevices(storedGroup);
            } catch(NotFoundException e) {} /* ignore if it doesn't exist yet */
        }
    }
    
    public static Phase parsePhase(String porterResultString) {
        Phase phaseDetected;
        if(porterResultString.contains("Phase = A")){
            phaseDetected = Phase.A;
        } else if(porterResultString.contains("Phase = B")) {
            phaseDetected = Phase.B;
        } else if(porterResultString.contains("Phase = C")) {
            phaseDetected = Phase.C;
        } else {
            throw new UndefinedPhaseException();
        }
        return phaseDetected;
    }
    
    public static PhaseDetectVoltageReading parseVoltageReading(String value) {
        String[] words = value.split(" ");
        String initialVoltage = "";
        String lastVoltage = "";
        for (int i = 0; i < words.length; i++) {
            if (words[i].equalsIgnoreCase("Voltage:")) {
                if (words[i - 2].contains("First")) {
                    initialVoltage = words[i + 1];
                } else if (words[i - 2].equalsIgnoreCase("Last")) {
                    lastVoltage = words[i + 1];
                }
            }
        }
        float initial = Float.parseFloat(initialVoltage);
        float last = Float.parseFloat(lastVoltage);
        float delta = last - initial;
        DecimalFormat formatter = new DecimalFormat("##.##");
        String deltaString = formatter.format(delta);
        initialVoltage = formatter.format(initial);
        lastVoltage = formatter.format(last);
        PhaseDetectVoltageReading voltageReading = new PhaseDetectVoltageReading();
        voltageReading.setInitial(Double.valueOf(initialVoltage));
        voltageReading.setLast(Double.valueOf(lastVoltage));
        voltageReading.setDelta(Double.valueOf(deltaString));
        return voltageReading;
    }
    
    @Override
    public String cacheResults(){
        return phaseDetectResultsCache.addResult(phaseDetectResult);
    }
    
    @Override
    public String getLastCachedResultKey(){
        List<String> keys = phaseDetectResultsCache.getCompletedKeys();
        if(keys != null && !keys.isEmpty()){
            return keys.get(keys.size()-1);
        } else { 
            return null;
        }
    }
    
    @Override
    public void cancelTest(LiteYukonUser user){
        cancelReadPhaseDetect(user);
        phaseDetectData = null;
        phaseDetectResult = null;
        phaseDetectState = null;
    }
    
    @Override
    public void initializeResult(){
        phaseDetectResult = new PhaseDetectResult();
        Map<Phase, StoredDeviceGroup> phaseToGroupMap = phaseDetectResult.getPhaseToGroupMap();
        for(Phase phase : Phase.values()){
            StoredDeviceGroup group = temporaryDeviceGroupService.createTempGroup(null);
            phaseToGroupMap.put(phase, group);
        }
    }
    
    /* HELPERS */
    private void setupPhaseDetectGroups() {
        DeviceGroup systemMetersDeviceGroup = deviceGroupService.resolveGroupName(SYSTEM_METERS_GROUP);
        StoredDeviceGroup metersGroup = deviceGroupEditorDao.getStoredGroup(systemMetersDeviceGroup);
        
        /* Setup the 'Phase Detect' group if it doesn't exist. */
        StoredDeviceGroup phaseGroup = null;
        try{
            phaseGroup = deviceGroupEditorDao.getGroupByName(metersGroup, PHASE_DETECT_GROUP, false);
        } catch (NotFoundException nfe) {
            phaseGroup = deviceGroupEditorDao.addGroup(metersGroup, DeviceGroupType.STATIC, PHASE_DETECT_GROUP, DeviceGroupPermission.NOEDIT_NOMOD);
        }
        
        /* Setup the 'Last Results' group if it doesn't exist. */
        StoredDeviceGroup lastResultsGroup = null;
        try{
            lastResultsGroup = deviceGroupEditorDao.getGroupByName(phaseGroup, LAST_RESULTS_GROUP, false);
        } catch (NotFoundException nfe) {
            lastResultsGroup = deviceGroupEditorDao.addGroup(phaseGroup, DeviceGroupType.STATIC, LAST_RESULTS_GROUP, DeviceGroupPermission.NOEDIT_NOMOD);
        }
        
        /* Setup Detected Phase Groups */
        setupDetectedPhaseGroup(lastResultsGroup, DetectedPhase.A);
        setupDetectedPhaseGroup(lastResultsGroup, DetectedPhase.B);
        setupDetectedPhaseGroup(lastResultsGroup, DetectedPhase.C);
        setupDetectedPhaseGroup(lastResultsGroup, DetectedPhase.AB);
        setupDetectedPhaseGroup(lastResultsGroup, DetectedPhase.AC);
        setupDetectedPhaseGroup(lastResultsGroup, DetectedPhase.BC);
        setupDetectedPhaseGroup(lastResultsGroup, DetectedPhase.ABC);
        setupDetectedPhaseGroup(lastResultsGroup, DetectedPhase.UNKNOWN);
    }
    
    private void setupDetectedPhaseGroup(StoredDeviceGroup parent, DetectedPhase detectedPhase) {
        try{
            deviceGroupEditorDao.getGroupByName(parent, detectedPhase.name(), false);
        } catch (NotFoundException nfe) {
            deviceGroupEditorDao.addGroup(parent, DeviceGroupType.STATIC, detectedPhase.name(), DeviceGroupPermission.NOEDIT_NOMOD);
        }
    }
    
    private StoredDeviceGroup retrieveGroup(StoredDeviceGroup parent, String groupName) {
        StoredDeviceGroup group = deviceGroupEditorDao.getGroupByName(parent, groupName, false);
        return group;
    }
    
    private void generatePhasePointData(SimpleDevice device, DetectedPhase detectedPhase) {
        try {
            LitePoint phasePoint = attributeService.getPointForAttribute(device, BuiltInAttribute.PHASE);
            
            LiteStateGroup phaseStateGroup = stateDao.getLiteStateGroup("PhaseStatus");
            LiteState liteState = new LiteState(0);
            for(LiteState state : phaseStateGroup.getStatesList()){
                if(state.getStateText().equalsIgnoreCase(detectedPhase.name())){
                    liteState = state;
                    break;
                }
            }
            PointData pointData = new PointData();
            pointData.setId(phasePoint.getLiteID());
            pointData.setType(phasePoint.getPointType());
            pointData.setPointQuality(PointQuality.Normal);
            pointData.setTime(new Date());
            pointData.setValue(liteState.getStateRawState());
            pointData.setTags(PointData.TAG_POINT_MUST_ARCHIVE);
            pointData.setMillis(0);
            dynamicDataSource.putValue(pointData);
        } catch (IllegalUseOfAttribute e) {
            log.warn("No Phase point found for device with id: " + device.getDeviceId());
        }
    }
    
    private void addToDeviceGroup(SimpleDevice device, DetectedPhase detectedPhase) {
        String detectedPhaseGroupName = detectedPhase.name();
        
        DeviceGroup systemMetersDeviceGroup = deviceGroupService.resolveGroupName(SYSTEM_METERS_GROUP);
        StoredDeviceGroup metersGroup = deviceGroupEditorDao.getStoredGroup(systemMetersDeviceGroup);
        StoredDeviceGroup phaseGroup = retrieveGroup(metersGroup, PHASE_DETECT_GROUP);
        StoredDeviceGroup lastResultsGroup = retrieveGroup(phaseGroup, LAST_RESULTS_GROUP);
        StoredDeviceGroup detectedPhaseGroup = retrieveGroup(lastResultsGroup, detectedPhaseGroupName);
        
        deviceGroupMemberEditorDao.addDevices(detectedPhaseGroup, device);
    }
    
    private void proccessReadResults(List<SimpleDevice> devices, final Map<Phase, StoredDeviceGroup> groups, LiteYukonUser user) {
        setupPhaseDetectGroups();
        phaseDetectResult.setInputDeviceList(devices);
        for (SimpleDevice device : devices) {
            if(phaseDetectResult.getFailureGroupMap().containsKey(device)){
                continue; /* skip devices that failed: don't add them to the unknown group or generate point data */
            }
            Set<Phase> devicePhases = Sets.newHashSet();
            for (Map.Entry<Phase, StoredDeviceGroup> phaseGroupEntry : groups.entrySet()) {
                if (deviceGroupProviderDao.isDeviceInGroup(phaseGroupEntry.getValue(), device)) {
                    devicePhases.add(phaseGroupEntry.getKey());
                }
            }
            DetectedPhase detectedPhase = DetectedPhase.getPhase(devicePhases);
            generatePhasePointData(device, detectedPhase);
            addToDeviceGroup(device, detectedPhase);
            phaseDetectResult.handleDetectResult(device, detectedPhase);
        }
    }
    
    /* DEPENDANCIES */
    @Autowired
    public void setCommandRequestExecutor(CommandRequestDeviceExecutor commandRequestExecutor) {
        this.commandRequestExecutor = commandRequestExecutor;
    }
    
    @Autowired
    public void setRouteBroadcastService(RouteBroadcastService routeBroadcastService) {
        this.routeBroadcastService = routeBroadcastService;
    }
    
    @Autowired
    public void setTemporaryDeviceGroupService(TemporaryDeviceGroupService temporaryDeviceGroupService) {
        this.temporaryDeviceGroupService = temporaryDeviceGroupService;
    }
    
    @Autowired
    public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }
    
    @Autowired
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
    
    @Autowired
    public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
    
    @Autowired
    public void setDeviceGroupProviderDao(DeviceGroupProviderDao deviceGroupProviderDao) {
        this.deviceGroupProviderDao = deviceGroupProviderDao;
    }
    
    @Autowired
    public void setStateDao(StateDao stateDao) {
        this.stateDao = stateDao;
    }
    
    @Autowired
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }
    
    @Override
    public void setPhaseDetectData(PhaseDetectData data) {
        this.phaseDetectData = data;
    }

    @Override
    public PhaseDetectData getPhaseDetectData() {
        return phaseDetectData;
    }

    @Override
    public PhaseDetectState getPhaseDetectState() {
        return phaseDetectState;
    }

    @Override
    public PhaseDetectResult getPhaseDetectResult() {
        return phaseDetectResult;
    }

    @Override
    public void setPhaseDetectState(PhaseDetectState state) {
        this.phaseDetectState = state;
    }
    
    @Override
    public void setPhaseDetectResult(PhaseDetectResult result) {
        this.phaseDetectResult = result;
    }
    
    @Required
    public void setPhaseDetectResultsCache(RecentResultsCache<PhaseDetectResult> phaseDetectResultsCache) {
        this.phaseDetectResultsCache = phaseDetectResultsCache;
    }

    @Autowired
    public void setAttributeService(AttributeService attributeService){
        this.attributeService = attributeService;
    }
}
