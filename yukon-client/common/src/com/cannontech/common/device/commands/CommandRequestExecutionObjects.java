package com.cannontech.common.device.commands;

public final class CommandRequestExecutionObjects<T> {
	private final CommandRequestExecutor<T> commandRequestExecutor;
	private final CommandCompletionCallback<? super T> callback;
	private final CommandRequestExecutionContextId contextId;
	
	public CommandRequestExecutionObjects(CommandRequestExecutor<T> commandRequestExecutor, CommandCompletionCallback<? super T> callback,  CommandRequestExecutionContextId contextId) {
		this.commandRequestExecutor = commandRequestExecutor;
		this.callback = callback;
		this.contextId = contextId;
	}

	public CommandRequestExecutor<T> getCommandRequestExecutor() {
		return commandRequestExecutor;
	}
	
	public CommandCompletionCallback<? super T> getCallback() {
		return callback;
	}
	
	public CommandRequestExecutionContextId getContextId() {
		return contextId;
	}
}
