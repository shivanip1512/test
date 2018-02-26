package com.cannontech.common.device.commands;

import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.message.porter.message.Request;

public class CommandRequestRouteAndDevice extends CommandRequestBase {
    private SimpleDevice device;
    private int routeId;
    
    public CommandRequestRouteAndDevice(String command, SimpleDevice device, int routeId) {
        super(command);
        this.device = device;
        this.routeId = routeId;
    }

    public SimpleDevice getDevice() {
        return device;
    }

    public int getRouteId() {
        return routeId;
    }

    @Override
    public CommandRequestType getCommandRequestType() {
        return CommandRequestType.DEVICE_ROUTE;
    }

    @Override
    public Request generateRequest(ExecutionParameters params, int groupMessageId) {
        Request request = buildRequest(params, groupMessageId);
        request.setRouteID(routeId);
        request.setDeviceID(device.getDeviceId());
        return request;
    }

    @Override
    public void applyIdsToCommandRequestExecutionResult(CommandRequestExecutionResult result) {
        result.setDeviceId(device.getDeviceId());
        result.setRouteId(routeId);
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
        builder.append("device", getDevice());
        builder.append("routeId", getRouteId());
        return builder.toString();
    }
}