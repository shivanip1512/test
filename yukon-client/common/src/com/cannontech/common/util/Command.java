package com.cannontech.common.util;

/**
 * Command encapsulates a request as an object letting you parameterize clients with different
 * requests, and queue or log requests.
 */
public abstract class Command {
/**
 * Command constructor comment.
 */
protected Command() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public abstract void execute() throws CommandExecutionException;
}
