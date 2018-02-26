package com.cannontech.common.device.commands;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.common.pao.PaoType;
import com.cannontech.message.porter.message.Request;

public abstract class CommandRequestBase {
    final static Random random = new Random();
    protected String command;

    protected CommandRequestBase(String command) {
        this.command = command;
    }
    
    public String getCommand() {
        return command;
    }
        
    protected Request buildRequest(ExecutionParameters params, int groupMessageId) {
        Request request = new Request();
        request.setGroupMessageID(groupMessageId);
        request.setUserMessageID(random.nextInt());
        request.setPriority(params.getPriority());
        // adds "noqueue" to the command
        if (params.isNoqueue() && !StringUtils.containsIgnoreCase(command, " noqueue")) {
            command += " noqueue";
        }
        request.setCommandString(command);
        return request;
    }
    
    public abstract PaoType getPaoType();
    
    public abstract CommandRequestType getCommandRequestType();

    public abstract Request generateRequest(ExecutionParameters params, int groupMessageId);

    public abstract void applyIdsToCommandRequestExecutionResult(CommandRequestExecutionResult result);
    
    @Override
    public String toString() {
        StandardToStringStyle style = new StandardToStringStyle();
        style.setFieldSeparator(", ");
        style.setUseShortClassName(true);
        ToStringBuilder builder = new ToStringBuilder(this, style);
        builder.append("requestType", getCommandRequestType());
        builder.append("command", command);
        return builder.toString();
    }
}
