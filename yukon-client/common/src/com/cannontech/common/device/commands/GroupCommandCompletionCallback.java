package com.cannontech.common.device.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.CancelStatus;
import com.cannontech.common.util.Completable;
import com.cannontech.common.util.MapList;
import com.cannontech.core.dynamic.PointValueHolder;

public class GroupCommandCompletionCallback implements
        CommandCompletionCallback<CommandRequestDevice>, Completable, CancelStatus, MultipleDeviceResultHolder {
    
    private Map<YukonDevice,DeviceErrorDescription> errors = new ConcurrentHashMap<YukonDevice, DeviceErrorDescription>(100, .75f, 1);
    private Map<YukonDevice,String> resultStrings = new ConcurrentHashMap<YukonDevice, String>(100, .75f, 1);
    private Map<YukonDevice, Object> allDevices = new ConcurrentHashMap<YukonDevice, Object>(100, .75f, 1);
    private Object PRESENT = new Object(); // used as the value for the allDevices map
    private MapList<YukonDevice,PointValueHolder> receivedValues = new MapList<YukonDevice, PointValueHolder>();
    private boolean complete = false;
    private boolean canceled = false;
    
    @Override
    public void receivedIntermediateError(CommandRequestDevice command, DeviceErrorDescription error) {
        // ignore
    }

    @Override
    public void receivedIntermediateResultString(CommandRequestDevice command, String value) {
        // ignore
    }

    @Override
    public void receivedLastError(CommandRequestDevice command, DeviceErrorDescription error) {
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
    
    public Set<YukonDevice> getAllDevices() {
        return Collections.unmodifiableSet(allDevices.keySet());
    }
    
    @Override
    public Set<YukonDevice> getSuccessfulDevices() {
        return Collections.unmodifiableSet(resultStrings.keySet());
    }
    
    @Override
    public Set<YukonDevice> getFailedDevices() {
        return Collections.unmodifiableSet(errors.keySet());
    }
    
    public void handleSuccess(YukonDevice device) {
        // ignore
    }
    
    public void handleFailure(YukonDevice device) {
        // ignore
    }

    public boolean isSuccessful(YukonDevice device) {
        return resultStrings.containsKey(device);
    }
    
    public boolean isUnsuccessful(YukonDevice device) {
        return !errors.containsKey(device);
    }
    
    @Override
    public List<PointValueHolder> getValues(YukonDevice device) {
        return Collections.unmodifiableList(receivedValues.get(device));
    }
    
    public Map<YukonDevice, List<PointValueHolder>> getValues() {
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
        complete();
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

    public Map<YukonDevice, DeviceErrorDescription> getErrors() {
        return errors;
    }

    public Map<YukonDevice, String> getResultStrings() {
        return resultStrings;
    }

}
