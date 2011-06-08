package com.cannontech.common.device.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.CancelStatus;
import com.cannontech.common.util.Completable;
import com.cannontech.common.util.ExceptionStatus;
import com.cannontech.common.util.MapList;
import com.cannontech.core.dynamic.PointValueHolder;

public class GroupCommandCompletionCallback implements
        CommandCompletionCallback<CommandRequestDevice>, Completable, CancelStatus, ExceptionStatus, MultipleDeviceResultHolder {
    
    private Map<SimpleDevice,SpecificDeviceErrorDescription> errors = new ConcurrentHashMap<SimpleDevice, SpecificDeviceErrorDescription>(100, .75f, 1);
    private Map<SimpleDevice,String> resultStrings = new ConcurrentHashMap<SimpleDevice, String>(100, .75f, 1);
    private Map<SimpleDevice, Object> allDevices = new ConcurrentHashMap<SimpleDevice, Object>(100, .75f, 1);
    private Object PRESENT = new Object(); // used as the value for the allDevices map
    private MapList<SimpleDevice,PointValueHolder> receivedValues = new MapList<SimpleDevice, PointValueHolder>();
    private boolean complete = false;
    private boolean canceled = false;
    private boolean processingErrorOccured = false;
    private String processingErrorReason = "";
    
    @Override
    public void receivedIntermediateError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
        // ignore
    }

    @Override
    public void receivedIntermediateResultString(CommandRequestDevice command, String value) {
        // ignore
    }

    @Override
    public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
        errors.put(command.getDevice(),error);
        allDevices.put(command.getDevice(), PRESENT);
        handleFailure(command.getDevice());
    }

    @Override
    public void receivedLastResultString(CommandRequestDevice command, String value) {
        resultStrings.put(command.getDevice(), value);
        allDevices.put(command.getDevice(), PRESENT);
        handleSuccess(command.getDevice());
    }

    @Override
    public void receivedValue(CommandRequestDevice command, PointValueHolder value) {
        receivedValues.add(command.getDevice(), value);
    }
    
    public Set<SimpleDevice> getAllDevices() {
        return Collections.unmodifiableSet(allDevices.keySet());
    }
    
    @Override
    public Set<SimpleDevice> getSuccessfulDevices() {
        return Collections.unmodifiableSet(resultStrings.keySet());
    }
    
    @Override
    public Set<SimpleDevice> getFailedDevices() {
        return Collections.unmodifiableSet(errors.keySet());
    }
    
    public void handleSuccess(SimpleDevice device) {
        // ignore
    }
    
    public void handleFailure(SimpleDevice device) {
        // ignore
    }

    public boolean isSuccessful(SimpleDevice device) {
        return resultStrings.containsKey(device);
    }
    
    public boolean isUnsuccessful(SimpleDevice device) {
        return !errors.containsKey(device);
    }
    
    @Override
    public List<PointValueHolder> getValues(SimpleDevice device) {
        return Collections.unmodifiableList(receivedValues.get(device));
    }
    
    public Map<SimpleDevice, List<PointValueHolder>> getValues() {
        return receivedValues.values();
    }

    @Override
    final public void complete() {
        complete = true;
        doComplete();
    }
    
    @Override
    final public void cancel() {
        canceled = true;
    }
    
    @Override
    final public void processingExceptionOccured(String reason) {
    	processingErrorReason = reason;
    	processingErrorOccured = true;
    }
    
    @Override
    public boolean isComplete() {
        return complete;
    }
    
    @Override
    public boolean isCanceled() {
        return canceled;
    };

    protected void doComplete() {
        // noop
    }
    
    @Override
    public boolean isExceptionOccured() {
    	return processingErrorOccured;
    }
    
    @Override
    public String getExceptionReason() {
    	return processingErrorReason;
    }

    public Map<SimpleDevice, SpecificDeviceErrorDescription> getErrors() {
        return errors;
    }

    public Map<SimpleDevice, String> getResultStrings() {
        return resultStrings;
    }

}
