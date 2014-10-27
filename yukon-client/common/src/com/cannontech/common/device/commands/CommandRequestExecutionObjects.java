package com.cannontech.common.device.commands;

import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;

public final class CommandRequestExecutionObjects<T> {
    private final CommandRequestExecutor<T> commandRequestExecutor;
    private final CommandCompletionCallback<? super T> callback;
    private final CommandRequestExecution execution;

    public CommandRequestExecutionObjects(CommandRequestExecutor<T> commandRequestExecutor,
            CommandCompletionCallback<? super T> callback, CommandRequestExecution execution) {
        this.commandRequestExecutor = commandRequestExecutor;
        this.callback = callback;
        this.execution = execution;
    }

    public CommandRequestExecutor<T> getCommandRequestExecutor() {
        return commandRequestExecutor;
    }

    public CommandCompletionCallback<? super T> getCallback() {
        return callback;
    }

    public CommandRequestExecutionContextId getContextId() {
        return new CommandRequestExecutionContextId(execution.getContextId());
    }

    public CommandRequestExecution getExecution() {
        return execution;
    }
}
