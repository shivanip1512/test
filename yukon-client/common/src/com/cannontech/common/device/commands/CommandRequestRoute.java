package com.cannontech.common.device.commands;

import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.common.pao.PaoType;
import com.cannontech.message.porter.message.Request;

/**
 * Command request class for route based commands
 */
public class CommandRequestRoute extends CommandRequestBase {

    private int routeId;
    
    public CommandRequestRoute(String command, int routeId) {
        super(command);
        this.routeId = routeId;
    }

    public int getRouteId() {
        return routeId;
    }
    
    @Override
    public CommandRequestType getCommandRequestType() {
       return CommandRequestType.ROUTE;
    }

    @Override
    public void applyIdsToCommandRequestExecutionResult(CommandRequestExecutionResult result) {
        result.setRouteId(routeId);
    }
    
    @Override
    public Request generateRequest(ExecutionParameters params, int groupMessageId) {
        Request request = buildRequest(params, groupMessageId);
        request.setRouteID(routeId);
        return request;
    }
    
    @Override
    public PaoType getPaoType() {
        return null;
    }
    
    @Override
    public String toString() {
        StandardToStringStyle style = new StandardToStringStyle();
        style.setFieldSeparator(", ");
        style.setUseShortClassName(true);
        ToStringBuilder builder = new ToStringBuilder(this, style);
        builder.append(super.toString());
        builder.append("routeId", getRouteId());
        return builder.toString();
    }
}