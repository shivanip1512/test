package com.cannontech.common.bulk.collection.device.model;

import static com.cannontech.common.device.commands.CommandRequestExecutionStatus.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.CollectionUtils;

import com.cannontech.common.bulk.collection.DeviceMemoryCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.service.CollectionActionLogDetailService;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.user.YukonUserContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CollectionActionResult {
    private int cacheKey;
    private CollectionAction action;
    private DeviceGroupMemberEditorDao editorDao;
    private CollectionActionLogDetailService logService;
    private CollectionActionInputs inputs;
    private Instant startTime;
    private Instant stopTime;
    private CommandRequestExecutionStatus status;
    private CommandRequestExecution execution;
    // used by demand reset
    private CommandRequestExecution verificationExecution;
    // example: porter is down
    private String executionExceptionText;
    private String infoText;
    private CollectionActionCounts counts;
    private Map<CollectionActionDetail, CollectionActionDetailGroup> details = new HashMap<>();
    @JsonIgnore
    private List<CollectionActionCancellationCallback> cancelationCallbacks =
        Collections.synchronizedList(new ArrayList<CollectionActionCancellationCallback>());
    private boolean isCanceled;
    private YukonUserContext context;
    private DeviceMemoryCollectionProducer producer;
    private boolean loadedFromDatabase;

    public CollectionActionResult() {
    }

    public CollectionActionResult(CollectionAction action, List<? extends YukonPao> allDevices,
            LinkedHashMap<String, String> inputs, CommandRequestExecution execution,
            DeviceGroupMemberEditorDao editorDao, TemporaryDeviceGroupService tempGroupService,
            DeviceGroupCollectionHelper groupHelper, 
            DeviceMemoryCollectionProducer producer, 
            CollectionActionLogDetailService logService,
            YukonUserContext context) {        
        if(action == CollectionAction.MASS_DELETE && producer != null) {
            // deleted devices - in order for the devices to be visible the "in memory" collection
            // is created
            DeviceCollection allDeviceCollection = producer.createDeviceCollection(allDevices);
            this.inputs = new CollectionActionInputs(allDeviceCollection, null);
        } else {
            StoredDeviceGroup tempGroup = tempGroupService.createTempGroup();
            editorDao.addDevices(tempGroup, allDevices);
            DeviceCollection allDeviceCollection = groupHelper.buildDeviceCollection(tempGroup);
            this.inputs = new CollectionActionInputs(allDeviceCollection, inputs);
        }
        this.setStartTime(new Instant());
        this.editorDao = editorDao;
        this.execution = execution;
        this.action = action;
        this.logService = logService;
        this.context = context;
        this.producer = producer;
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
            status = STARTED;
            startTime = new Instant();
        }
        
        logService.loadPointNames(this);
    }

    public CollectionActionResult(CollectionAction action, List<? extends YukonPao> allDevices,
            LinkedHashMap<String, String> inputs, DeviceGroupMemberEditorDao editorDao,
            TemporaryDeviceGroupService tempGroupService, DeviceGroupCollectionHelper groupHelper,
            DeviceMemoryCollectionProducer producer, 
            CollectionActionLogDetailService logService, YukonUserContext context) {
        this(action, allDevices, inputs, null, editorDao, tempGroupService, groupHelper, producer, logService, context);
    }


    public boolean isCancelable() {
        return status == STARTED && !cancelationCallbacks.isEmpty();
    }
    
    public boolean isTerminatable() {
        if(status == CANCELING && !cancelationCallbacks.isEmpty()) {
            // in the process of canceling
            return false;
        }
        return !isComplete();
    }
    
    public boolean isCancelActionAllowed() {
        return status == STARTED && action.isCancelable();
    }
    
    public boolean isComplete() {
        return status == COMPLETE || status == CANCELLED || status == FAILED;
    }

    public CollectionActionDetailGroup getDetail(CollectionActionDetail detail) {
        return details.get(detail);
    }

    public DeviceCollection getDeviceCollection(CollectionActionDetail detail) {
        return details.get(detail) != null ? details.get(detail).getDevices() : null;
    }
    
    public boolean containsDevice(CollectionActionDetail detail, SimpleDevice device) {
        return getDeviceCollection(detail).getDeviceList().contains(device);
    }

    public CommandRequestExecution getExecution() {
        return execution;
    }

    public void addDevicesToGroup(CollectionActionDetail detail, List<? extends YukonPao> paos,
            List<CollectionActionLogDetail> log) {
        if (!CollectionUtils.isEmpty(paos)) {
            appendToLogWithoutAddingToGroup(log);
            // "in memory" collection is used for deleted devices
            if(detail == CollectionActionDetail.SUCCESS && action == CollectionAction.MASS_DELETE) {
                //SUCCESS for MASS_DELETE means that device no longer exists
                if(details.get(detail).getGroup() != null) {
                    details.put(detail, new CollectionActionDetailGroup(producer.createDeviceCollection(paos), null));
                } else {
                    // after creating a collection the collection can't be modified
                    DeviceCollection collection = details.get(detail).getDevices();
                    List<YukonPao> newList = new ArrayList<>(collection.getDeviceList());
                    newList.addAll(paos);
                    // each time the device is deleted, the new collection is created
                    details.put(detail, new CollectionActionDetailGroup(producer.createDeviceCollection(newList), null));
                    // the original collection is removed from cache
                    producer.invalidate(collection);
                }
            } else {
                editorDao.addDevices(details.get(detail).getGroup(), paos);
            }
        }
    }
    
    /**
     * Returns the list of devices that can be marked as canceled
     */
    public List<SimpleDevice> getCancelableDevices() {
        List<SimpleDevice> devices = Collections.synchronizedList(new ArrayList<SimpleDevice>());
        if (isCanceled) {
            devices.addAll(inputs.getCollection().getDeviceList());
            details.values().forEach(d -> {
                devices.removeAll(d.getDevices().getDeviceList());
            });
        }
        return devices;
    }

    public void addDeviceToGroup(CollectionActionDetail detail, YukonPao pao, CollectionActionLogDetail log) {
        addDevicesToGroup(detail, Lists.newArrayList(pao), Lists.newArrayList(log));
    }

    public void appendToLogWithoutAddingToGroup(List<CollectionActionLogDetail> log) {
        if (!CollectionUtils.isEmpty(log)) {
            logService.appendToLog(this, log);
        }
    }
    
    public void appendToLogWithoutAddingToGroup(CollectionActionLogDetail log) {
        appendToLogWithoutAddingToGroup(Lists.newArrayList(log));
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
        logService.appendToLog(this, new CollectionActionLogDetail(executionExceptionText));
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
    
    public boolean hasCancelationCallbacks() {
        return !cancelationCallbacks.isEmpty();
    }

    public List<CollectionActionCancellationCallback> getCancellationCallbacks(StrategyType type) {
        return cancelationCallbacks.stream().filter(c -> c.getStrategy() == type).collect(Collectors.toList());
    }

    public void addCancellationCallback(CollectionActionCancellationCallback cancelationCallback) {
        cancelationCallbacks.add(cancelationCallback);
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
    
    public String getInputString() {
        
        if (inputs.getInputs() == null) {
            return "";
        }
        String retVal = "";
        for (String key : inputs.getInputs().keySet()) {
            retVal += key + ": " + inputs.getInputs().get(key) + ",";
        }
        return StringUtils.isNotEmpty(retVal) ? StringUtils.abbreviate(retVal.substring(0, retVal.length() - 1), 2000) : "";
    }
    
    public String getResultStatsString(MessageSourceAccessor accessor) {
        String retValue = "";
        for(CollectionActionDetail key : details.keySet()){
            retValue += accessor.getMessage(key) + ":" + details.get(key).getDevices().getDeviceCount() + ",";
        }
        return StringUtils.isNotEmpty(retValue) ? retValue.substring(0, retValue.length() - 1) : "";
    }
    
    public YukonUserContext getContext() {
        return context;
    }
    
    public boolean hasLogFile() {
        return logService == null ? false : logService.hasLog(cacheKey);
    }
    
    public File getLogFile() {
        try {
            return logService == null ? null : logService.getLog(cacheKey);
        } catch (FileNotFoundException e) {
            return null;
        }
    }
    
    public CommandRequestExecution getVerificationExecution() {
        return verificationExecution;
    }

    public void setVerificationExecution(CommandRequestExecution verificationExecution) {
        this.verificationExecution = verificationExecution;
    }
    
    public String getInfoText() {
        return infoText;
    }

    public void setInfoText(String infoText) {
        this.infoText = infoText;
        logService.appendToLog(this, new CollectionActionLogDetail(infoText));
    }
    
    public void setLoadedFromDatabase(boolean isLoadedFromDatabase) {
        this.loadedFromDatabase = isLoadedFromDatabase;
    }
    
    public boolean isLoadedFromDatabase() {
        return loadedFromDatabase;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter df = null;
        if (context != null) {
            df = DateTimeFormat.forPattern("MMM dd YYYY HH:mm:ss")
                    .withZone(context.getJodaTimeZone());
        } else {
            df = DateTimeFormat.forPattern("MMM dd YYYY HH:mm:ss");
        }
        sb.append(getCacheKey());
        sb.append(" [");
        sb.append(getStatus());
        sb.append(" ");
        sb.append(getAction());
        sb.append("(");
        sb.append(getAction().getProcess());
        sb.append(") ");
        sb.append("devices:");
        sb.append(getInputs().getCollection().getDeviceCount());
        if (getInputs() != null && getInputs().getInputs() != null) {
            sb.append(" ");
            sb.append(getInputs().getInputs());
        }
        sb.append("]");
        sb.append(" Loaded from db:");
        sb.append(loadedFromDatabase);
        sb.append(" Start:" + startTime.toString(df));
        sb.append( stopTime == null ? "" : " Stop:" + startTime.toString(df));
        if (execution != null) {
            sb.append(" ");
            sb.append("[");
            sb.append("creId:");
            sb.append(execution.getId());
            sb.append(" type:");
            sb.append(execution.getCommandRequestExecutionType());
            sb.append("]");
        }
        if (verificationExecution != null) {
            sb.append(" ");
            sb.append("[verification ");
            sb.append("creId:");
            sb.append(verificationExecution.getId());
            sb.append(" type:");
            sb.append(verificationExecution.getCommandRequestExecutionType());
            sb.append("]");
        }
        sb.append(" isCancelable:");
        sb.append(isCancelable());
        sb.append(" isTerminatable:");
        sb.append(isTerminatable());
        sb.append(" Progress:");
        sb.append(getCounts().getPercentCompleted());
        sb.append("%");
        sb.append(" [");
        getCounts().getPercentages().keySet().forEach(detail -> {
            sb.append(" ");
            sb.append(detail);
            sb.append(" ");
            sb.append(getDeviceCollection(detail).getDeviceCount());
            sb.append(" ");
            sb.append("(");
            sb.append(getCounts().getPercentages().get(detail));
            sb.append("%");
            sb.append(")");
        });
        sb.append("]");
        if (!cancelationCallbacks.isEmpty()) {
            sb.append(" callbacks:");
            sb.append(cancelationCallbacks.size());
        }
        if(executionExceptionText != null) {
            sb.append(" ");
            sb.append(executionExceptionText);
        }
        return sb.toString();
    }
}
