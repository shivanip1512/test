package com.cannontech.amr.phaseDetect.service.impl;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.phaseDetect.data.DetectedPhase;
import com.cannontech.amr.phaseDetect.data.Phase;
import com.cannontech.amr.phaseDetect.data.PhaseDetectData;
import com.cannontech.amr.phaseDetect.data.PhaseDetectResult;
import com.cannontech.amr.phaseDetect.data.PhaseDetectState;
import com.cannontech.amr.phaseDetect.data.UndefinedPhaseException;
import com.cannontech.amr.phaseDetect.service.PhaseDetectService;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
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
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class PhaseDetectServiceImpl implements PhaseDetectService{

    private CommandRequestDeviceExecutor commandRequestExecutor;
    private RouteBroadcastService routeBroadcastService;
    
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
    private PointDao pointDao;
    private DynamicDataSource dynamicDataSource;
    private DeviceGroupEditorDao deviceGroupEditorDao;
    private DeviceGroupService deviceGroupService;

    @Override
    public CommandResultHolder clearPhaseData(LiteYukonUser user) throws CommandCompletionException {
        CommandRequestDevice request = new CommandRequestDevice();
        request.setCommand("putconfig emetcon phasedetect clear");
        request.setDevice(new SimpleDevice(13108, DeviceTypes.MCTBROADCAST)); /* To be some magic broadcast device in the future */
        CommandResultHolder holder = commandRequestExecutor.execute(request, CommandRequestExecutionType.PHASE_DETECT_CLEAR, user);
        return holder;
    }
    
    @Override
    public void startPhaseDetect(List<Integer> routeIds, LiteYukonUser user, final Phase phase) {
        String delta = phaseDetectData.getDeltaVoltage().toString();
        String interval = phaseDetectData.getIntervalLength().toString();
        String num = phaseDetectData.getNumIntervals().toString();
        String command = "putconfig emetcon phasedetect phase " + phase.name() + " delta " + delta + " interval " + interval + " num " + num;
        
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
        routeBroadcastService.broadcastDetectionCommand(command, routeIds, CommandRequestExecutionType.PHASE_DETECT_COMMAND, callback , user);
        try {
            finishedLatch.await();
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
                String voltageReading = "N/A";
                try {
                    phaseDetected = parsePhase(value);
                    voltageReading = parseVoltageReading(phaseBeingRead, value);
                    
                    if (phaseDetected != phaseBeingRead) {
                        // do something, add to failure? ignore?
                    }
                    
                    StoredDeviceGroup storedDeviceGroup = (StoredDeviceGroup) phaseDetectResult.getPhaseToGroupMap().get(phaseDetected);
                    deviceGroupMemberEditorDao.addDevices(storedDeviceGroup, command.getDevice());
                    /* Map the phase to the reading returned */
                    Map<String, String> phaseToReadingMap = phaseDetectResult.getDeviceReadingsMap().get(command.getDevice().getDeviceId());
                    if(phaseToReadingMap == null){
                        phaseToReadingMap = Maps.newHashMap();
                    }
                    if(phaseDetectData.isReadAfterAll()){
                        phaseToReadingMap.put(phaseDetected.name(), voltageReading);
                    } else {
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
//                if(!phaseDetectData.isReadAfterAll() && StringUtils.isBlank(phaseDetectResult.getErrorMsg())){
//                    phaseDetectState.setPhaseDetectRead(constrainToPhase);
//                }
                if(phaseDetectData.isReadAfterAll() || phaseDetectState.isPhaseDetectComplete()){
                    clearPhaseDetectGroups();
                    proccessReadResults(devices, phaseDetectResult.getPhaseToGroupMap(), user);
                }
            }
            
        };
        
        CommandRequestExecutionIdentifier id = commandRequestExecutor.execute(requests, callback, CommandRequestExecutionType.PHASE_DETECT_READ, user);
        phaseDetectResult.setCommandRequestExecutionIdentifier(id);
        
    }
    
    private void proccessReadResults(List<SimpleDevice> devices, final Map<Phase, StoredDeviceGroup> groups, LiteYukonUser user) {
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
        }
    }
    
    public void clearPhaseDetectGroups(){
        for(DetectedPhase detectedPhase : DetectedPhase.values()){
            try {
                DeviceGroup group = deviceGroupService.resolveGroupName("/System/Meters/Phase Detect/Last Results/" + detectedPhase.name());
                StoredDeviceGroup storedGroup = deviceGroupEditorDao.getStoredGroup(group);
                deviceGroupMemberEditorDao.removeAllChildDevices(storedGroup);
            } catch(NotFoundException e) {} /* ignore if it doesn't exist yet */
        }
    }
    
    private void addToDeviceGroup(SimpleDevice device, DetectedPhase detectedPhase){
        String phaseDetectGroupName = "Phase Detect";
        String lastResultsGroupName = "Last Results";
        String detectedPhaseGroupName = detectedPhase.name();
        
        DeviceGroup systemMetersDeviceGroup = deviceGroupService.resolveGroupName("/System/Meters/");
        StoredDeviceGroup metersGroup = deviceGroupEditorDao.getStoredGroup(systemMetersDeviceGroup);
        
        StoredDeviceGroup phaseGroup = deviceGroupEditorDao.getGroupByName(metersGroup, phaseDetectGroupName, true);
        StoredDeviceGroup lastResultsGroup = deviceGroupEditorDao.getGroupByName(phaseGroup, lastResultsGroupName, true);
        StoredDeviceGroup detectedPhaseGroup = deviceGroupEditorDao.getGroupByName(lastResultsGroup, detectedPhaseGroupName, true);
        deviceGroupMemberEditorDao.addDevices(detectedPhaseGroup, device);
    }
    
    private void generatePhasePointData(SimpleDevice device, DetectedPhase detectedPhase){
        int phasePointId = pointDao.getPointIDByDeviceID_Offset_PointType(device.getPaoIdentifier().getPaoId(), 3000, PointTypes.STATUS_POINT);
        if(phasePointId == 0){
            return;
        }
        LitePoint phasePoint = pointDao.getLitePoint(phasePointId);
        
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
    }

    private Phase parsePhase(String porterResultString) {
        Phase phaseDetected;
        if(porterResultString.contains("Phase: A")){
            phaseDetected = Phase.A;
        } else if(porterResultString.contains("Phase: B")) {
            phaseDetected = Phase.B;
        } else if(porterResultString.contains("Phase: C")) {
            phaseDetected = Phase.C;
        } else {
            throw new UndefinedPhaseException();
        }
        return phaseDetected;
    }
    
    private String parseVoltageReading(Phase phase, String value) {
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
        String color = delta > 0 ? "<font color='green'><b>" : "<font color='red'><b>";
        String voltageReading = "";
        if(!phaseDetectData.isReadAfterAll()){
            voltageReading += "Phase Read: <b>" + phase.name() + "</b> "; 
        }
        voltageReading += "Initial: <b>" + initialVoltage + "</b> Last: <b>" + lastVoltage + "</b> Delta: " + color + deltaString + "</b></font>";
        return voltageReading;
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
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
    
    @Autowired
    @Required
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

}
