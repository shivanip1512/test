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
	
	private static long generateTaskID() {
		if (taskID == -1)
			taskID = System.currentTimeMillis();
		return taskID++;
	}
	
	public static synchronized long addTask(TimeConsumingTask task) {
		long id = generateTaskID();
		tasks.put( new Long(id), task );
		new Thread(task).start();
		
		return id;
	}
	
	public static synchronized TimeConsumingTask getTask(long id) {
		return (TimeConsumingTask)tasks.get( new Long(id) );
	}
	
	public static synchronized void cancelTask(long id) {
		TimeConsumingTask task = (TimeConsumingTask) tasks.get( new Long(id) );
		if (task != null) task.cancel();
	}
	
	public static synchronized void removeTask(long id) {
		tasks.remove( new Long(id) );
	}

}
