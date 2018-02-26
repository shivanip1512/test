package com.cannontech.common.device.commands;

import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.message.porter.message.Request;

/**
 * Command request class for device based commands
 */
public class CommandRequestDevice extends CommandRequestBase {

    private SimpleDevice device;
 
    public CommandRequestDevice(String command, SimpleDevice device) {
        super(command);
        this.device = device;
    }
    
    public CommandRequestDevice(SimpleDevice device) {
        this(null, device);
    }

    public SimpleDevice getDevice() {
        return device;
    }
    
    @Override
    public CommandRequestType getCommandRequestType() {
       return CommandRequestType.DEVICE;
    }
    
    @Override
    public void applyIdsToCommandRequestExecutionResult(CommandRequestExecutionResult result) {
        result.setDeviceId(device.getDeviceId());
    }

    @Override
    public Request generateRequest(ExecutionParameters params, int groupMessageId) {
        Request request = buildRequest(params, groupMessageId);
        request.setDeviceID(device.getDeviceId());
        return request;
    }

    @Override
    public PaoType getPaoType() {
        return device.getDeviceType();
    }
    
    @Override
    public String toString() {
        StandardToStringStyle style = new StandardToStringStyle();
        style.setFieldSeparator(", ");
        style.setUseShortClassName(true);
        ToStringBuilder builder = new ToStringBuilder(this, style);
        builder.append(super.toString());
        builder.append("device", device);
        return builder.toString();
    }
}
