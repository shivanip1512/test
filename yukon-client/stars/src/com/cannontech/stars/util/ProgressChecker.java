/*
 * Created on Jan 29, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util;

import java.util.Hashtable;

import com.cannontech.stars.util.task.TimeConsumingTask;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ProgressChecker {
	
	private static long taskID = -1;
	
	private static Hashtable tasks = new Hashtable();
	private static Hashtable threads = new Hashtable();
	
	private static long generateTaskID() {
		if (taskID == -1)
			taskID = System.currentTimeMillis();
		return taskID++;
	}
	
	public static synchronized long addTask(TimeConsumingTask task) {
		long id = generateTaskID();
		tasks.put( new Long(id), task );
		Thread t = new Thread( task );
		threads.put( new Long(id), t );
		t.start();
		
		return id;
	}
	
	public static synchronized TimeConsumingTask getTask(long id) {
		TimeConsumingTask task = (TimeConsumingTask) tasks.get( new Long(id) );
		if (task != null) {
			Thread t = (Thread) threads.get( new Long(id) );
			if (t != null && !t.isAlive()) {
				if (task.getStatus() == TimeConsumingTask.STATUS_CANCELING)
					task.setStatus( TimeConsumingTask.STATUS_CANCELED );
				else if (task.getStatus() != TimeConsumingTask.STATUS_CANCELED
					&& task.getStatus() != TimeConsumingTask.STATUS_FINISHED)
					task.setStatus( TimeConsumingTask.STATUS_ERROR);
			}
		}
		
		return task;
	}
	
	public static synchronized void cancelTask(long id) {
		TimeConsumingTask task = (TimeConsumingTask) tasks.get( new Long(id) );
		if (task != null) {
			Thread t = (Thread) threads.get( new Long(id) );
			if (t != null && t.isAlive())
				task.cancel();
			else
				task.setStatus( TimeConsumingTask.STATUS_CANCELED );
		}
	}
	
	public static synchronized void removeTask(long id) {
		tasks.remove( new Long(id) );
		threads.remove( new Long(id) );
	}

}
