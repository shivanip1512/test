package com.cannontech.common.bulk.collection.device.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;

import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.pao.YukonPao;
import com.google.common.collect.Sets;

public class CollectionActionResult {

    private CollectionAction action;
    private DeviceGroupMemberEditorDao editorDao;
    private CollectionActionInputs inputs;
    private Instant startTime;
    private Instant stopTime;
    private CommandRequestExecutionStatus status;
    private CommandRequestExecution execution;
    //example: porter is down
    private String executionExceptionText;
    private CollectionActionCounts counts;
    private Map<CollectionActionDetail, CollectionActionDetailGroup> details = new HashMap<>();
    private int cacheKey;

    public CollectionActionResult(CollectionAction action, CollectionActionInputs inputs,
            CommandRequestExecution execution, DeviceGroupMemberEditorDao editorDao,
            TemporaryDeviceGroupService tempGroupService, DeviceGroupCollectionHelper groupHelper) {
        init(action, inputs, execution, editorDao, tempGroupService, groupHelper);
    }
    
    public CollectionActionResult(CollectionAction action, Set<YukonPao> allDevices,
            LinkedHashMap<String, String> inputs, CommandRequestExecution execution,
            DeviceGroupMemberEditorDao editorDao, TemporaryDeviceGroupService tempGroupService,
            DeviceGroupCollectionHelper groupHelper) {
        StoredDeviceGroup tempGroup = tempGroupService.createTempGroup();
        editorDao.addDevices(tempGroup, allDevices);
        DeviceCollection allDeviceCollection = groupHelper.buildDeviceCollection(tempGroup);
        init(action, new CollectionActionInputs(allDeviceCollection, inputs), execution, editorDao, tempGroupService,
            groupHelper);
    }

    private void init(CollectionAction action, CollectionActionInputs inputs, CommandRequestExecution execution,
            DeviceGroupMemberEditorDao editorDao, TemporaryDeviceGroupService tempGroupService,
            DeviceGroupCollectionHelper groupHelper) {
        this.setStartTime(new Instant());
        this.editorDao = editorDao;
        this.inputs = inputs;
        this.execution = execution;
        this.action = action;
        counts = new CollectionActionCounts(this);
        action.getDetails().forEach(detail -> {
            StoredDeviceGroup tempGroup = tempGroupService.createTempGroup();
            DeviceCollection devices = groupHelper.buildDeviceCollection(tempGroup);
            details.put(detail, new CollectionActionDetailGroup(devices, tempGroup));
        });
        
        if (execution != null) {
            status = execution.getCommandRequestExecutionStatus();
            startTime = new Instant(execution.getStartTime());
            stopTime = execution.getStopTime() == null ? null : new Instant(execution.getStopTime());
        } else {
            status = CommandRequestExecutionStatus.STARTED;
            startTime = new Instant();
        }
    }
    
    public CollectionActionDetailGroup getDetail(CollectionActionDetail detail) {
        return details.get(detail);
    }
    
    public DeviceCollection getDeviceCollection(CollectionActionDetail detail) {
        return details.get(detail) != null ? details.get(detail).getDevices() : null;
    }

    public CommandRequestExecution getExecution() {
        return execution;
    }
    
    public void addDevicesToGroup(CollectionActionDetail detail, Set<YukonPao> paos) {
        if (!paos.isEmpty()) {
            editorDao.addDevices(details.get(detail).getGroup(), paos);
        }
    }
    
    public void addDevicesToGroup(CollectionActionDetail detail, YukonPao pao) {
        addDevicesToGroup(detail, Sets.newHashSet(pao));
    }

    public void setExecution(CommandRequestExecution execution) {
        this.execution = execution;
    }
    
    public CollectionActionCounts getCounts() {
        return counts;
    }
    
    public boolean isFailed() {
        return StringUtils.isNotEmpty(executionExceptionText);
    }

    public String getExecutionExceptionText() {
        return executionExceptionText;
    }

    public CollectionActionInputs getInputs() {
        return inputs;
    }

    public void setInputs(CollectionActionInputs inputs) {
        this.inputs = inputs;
    }

    public CollectionAction getAction() {
        return action;
    }

    public int getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(int cacheKey) {
        this.cacheKey = cacheKey;
    }

    public Instant getStopTime() {
        return stopTime;
    }

    public void setStopTime(Instant stopTime) {
        this.stopTime = stopTime;
    }

    public CommandRequestExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(CommandRequestExecutionStatus status) {
        this.status = status;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Map<CollectionActionDetail, CollectionActionDetailGroup> getDetails() {
        return details;
    }

    public void setDetails(Map<CollectionActionDetail, CollectionActionDetailGroup> details) {
        this.details = details;
    }
}
