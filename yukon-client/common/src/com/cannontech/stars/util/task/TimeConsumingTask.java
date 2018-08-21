/*
 * Created on Jan 28, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class TimeConsumingTask implements Runnable {
	
	public static final int STATUS_NOT_INIT = -1;
	public static final int STATUS_RUNNING = 0;
	public static final int STATUS_FINISHED = 1;
	public static final int STATUS_CANCELING = 2;
	public static final int STATUS_CANCELED = 3;
	public static final int STATUS_ERROR = 4;
	
	protected int status = STATUS_NOT_INIT;
	protected boolean isCanceled = false;
	protected String errorMsg = null;
	
	/**
	 * Return the current status of the task, the return value
	 * could be one of the constants defined above.
	 */
	public int getStatus() {
		return status;
	}
	
	/**
	 * Set the current status of the task.
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	
	/**
	 * Cancel the task. It's up for the implementation to determine
	 * when is the proper time to terminate the execution.
	 */
	public void cancel() {
		if (status == STATUS_RUNNING) {
			isCanceled = true;
			status = STATUS_CANCELING;
		}
	}
	
	/**
	 * Return a description of the current progress of the task.
	 */
	public String getProgressMsg() {
		return null;
	}
	
	/**
	 * If there is an error occurred, return a description of it.
	 * Otherwise return null.
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

}
