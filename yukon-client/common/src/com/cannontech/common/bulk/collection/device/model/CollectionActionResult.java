package com.cannontech.common.bulk.collection.device.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.pao.YukonPao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Sets;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CollectionActionResult {
    private int cacheKey;
    private CollectionAction action;
    private DeviceGroupMemberEditorDao editorDao;
    private CollectionActionInputs inputs;
    private Instant startTime;
    private Instant stopTime;
    private CommandRequestExecutionStatus status;
    private CommandRequestExecution execution;
    // example: porter is down
    private String executionExceptionText;
    private CollectionActionCounts counts;
    private Map<CollectionActionDetail, CollectionActionDetailGroup> details = new HashMap<>();
    @JsonIgnore
    private CommandCompletionCallback<?> cancelationCallback;
    private boolean isCanceled;

    public CollectionActionResult(CollectionAction action, List<? extends YukonPao> allDevices,
            LinkedHashMap<String, String> inputs, CommandRequestExecution execution,
            DeviceGroupMemberEditorDao editorDao, TemporaryDeviceGroupService tempGroupService,
            DeviceGroupCollectionHelper groupHelper) {
        StoredDeviceGroup tempGroup = tempGroupService.createTempGroup();
        editorDao.addDevices(tempGroup, allDevices);
        DeviceCollection allDeviceCollection = groupHelper.buildDeviceCollection(tempGroup);
        this.inputs = new CollectionActionInputs(allDeviceCollection, inputs);
        this.setStartTime(new Instant());
        this.editorDao = editorDao;
        this.execution = execution;
        this.action = action;
        counts = new CollectionActionCounts(this);
        action.getDetails().forEach(detail -> {
            StoredDeviceGroup group = tempGroupService.createTempGroup();
            DeviceCollection devices = groupHelper.buildDeviceCollection(group);
            details.put(detail, new CollectionActionDetailGroup(devices, group));
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

    public CollectionActionResult(CollectionAction action, List<? extends YukonPao> allDevices,
            LinkedHashMap<String, String> inputs, DeviceGroupMemberEditorDao editorDao,
            TemporaryDeviceGroupService tempGroupService, DeviceGroupCollectionHelper groupHelper) {
        this(action, allDevices, inputs, null, editorDao, tempGroupService, groupHelper);
    }
    
    public boolean isCancelable() {
        return action.isCancelable() && status != null && status == CommandRequestExecutionStatus.STARTED;
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

    public void addDevicesToGroup(CollectionActionDetail detail, Set<? extends YukonPao> paos) {
        if (!paos.isEmpty()) {
            editorDao.addDevices(details.get(detail).getGroup(), paos);
        }
    }

    public void addDeviceToGroup(CollectionActionDetail detail, YukonPao pao) {
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

    public void setExecutionExceptionText(String executionExceptionText) {
        this.executionExceptionText = executionExceptionText;
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

    public CommandCompletionCallback<?> getCancelationCallback() {
        return cancelationCallback;
    }

    public void setCancelationCallback(CommandCompletionCallback<?> cancelationCallback) {
        this.cancelationCallback = cancelationCallback;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean isCanceled) {
        this.isCanceled = isCanceled;
    }

    // used by JS
    public Map<CollectionActionDetail, CollectionActionDetailGroup> getDetails() {
        return details;
    }

    public void log(Logger log) {
        if (log.isDebugEnabled()) {
            DateTimeFormatter df = DateTimeFormat.forPattern("MMM dd YYYY HH:mm:ss");
            df.withZone(DateTimeZone.getDefault());
            log.debug("Key=" + getCacheKey() + "----------------------------------------------------------------------");
            log.debug("Start Time:" + startTime.toString(df));
            log.debug(stopTime == null ? "" : "Stop Time:" + startTime.toString(df));
            if (execution != null) {
                log.debug("creId:" + execution.getId() + " Type:" + execution.getCommandRequestExecutionType());
            }
            log.debug(stopTime == null ? "" : "Stop Time:" +stopTime.toString(df));
            log.debug("---Inputs---");
            log.debug("Action:" + getAction());
            if (getInputs().getInputs() != null) {
                getInputs().getInputs().forEach((k, v) -> log.debug(k + ": " + v));
            }
            log.debug("Process:" + getAction().getProcess());
            log.debug("Devices:" + getInputs().getCollection().getDeviceCount());

            log.debug("---Results---");
            log.debug("Cancel:" + isCancelable());
            log.debug("Progress=" + getCounts().getPercentProgress() + "%");

            getAction().getDetails().forEach(detail -> {
                log.debug("------" + detail + "    device count=" + getDeviceCollection(detail).getDeviceCount() + "   "
                    + getCounts().getPercentage(detail) + "%");
            });

            if (cancelationCallback != null) {
                log.debug("cancelationCallback:" + cancelationCallback);
            }
            log.debug("status=" + getStatus() + "----------------------------------------------------------------------");
        }
    }
}
