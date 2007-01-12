/**
 * 
 */
package com.amdswireless.messages.twoway;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author johng
 *
 */
public class EngineStatus implements Serializable {

	private static final long serialVersionUID = 1L;
	private Hashtable<String,ArrayList> tgbQueues = new Hashtable<String,ArrayList>();
	private Hashtable<Integer,Date> lockedMeters = new Hashtable<Integer,Date>();
	private Hashtable<String,Date> pendingResponses = new Hashtable<String,Date>();
	private Hashtable<String,Integer> enqueuedMeters = new Hashtable<String,Integer>();
	private int enqueuedTxMeterCount = 0;
	private int lockedMeterCount = 0;
	/**
	 * 
	 */
	public EngineStatus() {
		super();
	}
	
	public int getDeepestTgbQueue() {
		Enumeration e = tgbQueues.keys();
		int deepestQueue=0;
		while ( e.hasMoreElements()) {
			String key = (String)e.nextElement();
			ArrayList q = tgbQueues.get(key);
			if ( q.size() > deepestQueue ) {
				deepestQueue = q.size();
			}
		}
		return deepestQueue;
	}

	public int getTgbQueueCount() {
		Enumeration e = tgbQueues.keys();
		int count=0;
		while ( e.hasMoreElements()) {
			String key = (String)e.nextElement();
			ArrayList q = tgbQueues.get(key);
				count += q.size();
		}
		return count;
	}

	public Hashtable<String,ArrayList> getTgbQueues() {
		return this.tgbQueues;
	}
	
	public void addTgbQueue(String tgbId, ArrayList queue) {
		tgbQueues.put(tgbId, queue);
		enqueuedTxMeterCount+=queue.size();
	}
	
	public Hashtable<Integer,Date> getLockedMeters() {
		return this.lockedMeters;
	}
	
	public void setLockedMeters(Hashtable<Integer,Date> t) {
		this.lockedMeters=t;
		this.lockedMeterCount = this.lockedMeters.size();
	}

	public int getEnqueuedTxMeterCount() {
		return enqueuedTxMeterCount;
	}

	public int getLockedMeterCount() {
		return lockedMeterCount;
	}
	
	public Hashtable<String,Date> getPendingResponses() {
		return this.pendingResponses;
	}
	public void addPendingResponse(String s, Date d) {
		this.pendingResponses.put(s,d);
	}
	public void setEnqueuedMeters(Hashtable<String,Integer> s) {
		this.enqueuedMeters = s;
	}
	public void addEnqueuedMeter(String s, Integer i) {
		this.enqueuedMeters.put(s,i);
	}
	public Hashtable<String,Integer> getEnqueuedMeters() {
		return this.enqueuedMeters;
	}
}
