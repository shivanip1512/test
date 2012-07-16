package com.cannontech.stars.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

import com.cannontech.clientutils.CTILogger;
import com.google.common.collect.Lists;

public class OptOutEventQueue {
    
    private boolean reCreateFile = false;
    private ArrayList<OptOutEvent> optOutEvents = Lists.newArrayList();
    private ArrayList<OptOutEvent> newEvents = Lists.newArrayList();
	
    private static final String OPTOUT_EVENT_FILE = "optout_events.txt";
	private static final int PERIOD_REENABLE = -1;
	private static final int PERIOD_REENABLE2 = -2;
	
	// If two events are apart for less than this interval, we consider they are overlapping
	private static final long OVERLAP_INTERVAL = 1000 * 60 * 30;
	
	private File diskFile = null;
	private static OptOutEventQueue instance = null;
	
	private OptOutEventQueue(File file) {
        diskFile = file;
    }
    
    public static OptOutEventQueue getInstance() {
        if (instance == null) {
            File tempDir = new File(StarsUtils.getStarsTempDir());
            if (!tempDir.exists())
                tempDir.mkdirs();
            
            instance = new OptOutEventQueue(new File(tempDir, OPTOUT_EVENT_FILE));
            instance.syncFromFile();
        }
        
        return instance;
    }
	
	public static class OptOutEvent {
	    
		private int energyCompanyID = 0;
		private long startDateTime = 0;
		private int period = 0;
		private int accountID = 0;
		private int inventoryID = 0;
		
		public int getAccountID() {
			return accountID;
		}
		public int getPeriod() {
			return period;
		}
		public long getStartDateTime() {
			return startDateTime;
		}
		public void setAccountID(int accountID) {
			this.accountID = accountID;
		}
		public void setPeriod(int period) {
			this.period = period;
		}
		public void setStartDateTime(long startDateTime) {
			this.startDateTime = startDateTime;
		}
		public int getEnergyCompanyID() {
			return energyCompanyID;
		}
		public void setEnergyCompanyID(int energyCompanyID) {
			this.energyCompanyID = energyCompanyID;
		}
		public int getInventoryID() {
			return inventoryID;
		}
		public void setInventoryID(int i) {
			inventoryID = i;
		}
		public String toString() {
			StringBuffer line = new StringBuffer();
			line.append(getEnergyCompanyID())
				.append(",")
				.append(getStartDateTime())
				.append(",")
				.append(getPeriod())
				.append(",")
				.append(getAccountID())
				.append(",")
				.append(getInventoryID());
			
			return line.toString();
		}
	}
	
	public OptOutEvent[] findOptOutEvents(int accountID) {
		ArrayList<OptOutEvent> eventList = Lists.newArrayList();
		
		for (int i = 0; i < optOutEvents.size(); i++) {
			OptOutEvent e = (OptOutEvent)optOutEvents.get(i);
			if (e.getAccountID()== accountID && e.getPeriod()>= 0)
				eventList.add(e);
		}
		
		OptOutEvent[] events = new OptOutEvent[ eventList.size()];
		eventList.toArray(events);
		return events;
	}
	
	public OptOutEvent findOptOutEvent(int inventoryID) {
		for (int i = 0; i < optOutEvents.size(); i++) {
			OptOutEvent e = (OptOutEvent)optOutEvents.get(i);
			if (e.getInventoryID()== inventoryID && e.getPeriod()>= 0) {
				return e;
			}
		}
		
		return null;
	}
	
	public OptOutEvent[] findReenableEvents(int accountID) {
		ArrayList<OptOutEvent> eventList = Lists.newArrayList();
		
		for (int i = 0; i < optOutEvents.size(); i++) {
			OptOutEvent e = (OptOutEvent)optOutEvents.get(i);
			if (e.getAccountID()== accountID
				&& (e.getPeriod()== PERIOD_REENABLE || e.getPeriod()== PERIOD_REENABLE2)) {
				eventList.add(e);
			}
		}
		
		OptOutEvent[] events = new OptOutEvent[ eventList.size()];
		eventList.toArray(events);
		return events;
	}
	
	public OptOutEvent findReenableEvent(int inventoryID) {
		for (int i = 0; i < optOutEvents.size(); i++) {
			OptOutEvent e = (OptOutEvent)optOutEvents.get(i);
			if (e.getInventoryID()== inventoryID
				&& (e.getPeriod()== PERIOD_REENABLE || e.getPeriod()== PERIOD_REENABLE2)) {
				return e;
			}
		}
		
		return null;
	}
	
	public OptOutEvent[] getOptOutEvents(int energyCompanyID) {
		ArrayList<OptOutEvent> eventList = Lists.newArrayList();
		for (int i = 0; i < optOutEvents.size(); i++) {
			OptOutEvent e = (OptOutEvent)optOutEvents.get(i);
			if (e.getEnergyCompanyID()== energyCompanyID && e.getPeriod()> 0) {
				eventList.add(e);
			}
		}
		
		OptOutEvent[] events = new OptOutEvent[ eventList.size()];
		eventList.toArray(events);
		return events;
	}
	
	private void syncToFile() {
		PrintWriter fw = null;
		ArrayList<OptOutEvent> events = null;
		
		try {
			if (reCreateFile) {
				fw = new PrintWriter(new FileWriter(diskFile, false));
				events = optOutEvents;
			}
			else {
				if (newEvents.size()== 0)return;
				fw = new PrintWriter(new FileWriter(diskFile, true));
				events = newEvents;
			}
			
			for (int i = 0; i < events.size(); i++) {
				OptOutEvent event = (OptOutEvent)events.get(i);
				fw.println(event.toString());
			}
		}
		catch (Exception e) {
			CTILogger.error(e.getMessage(), e);
		}
		finally {
			if (fw != null)fw.close();
		}
		
		reCreateFile = false;
		newEvents.clear();
	}
	
	/**
	 * Add an opt out or reenable event to the event queue. There can be at most one
	 * scheduled opt out event and one reenable event in the queue for each hardware
	 * and each account at the same time (the account event and hardware event won't
	 * interfere with each other). The new event will replace the old one if necessary.
	 * 
	 * @param event The opt out event to add to the queue
	 * @param writeThrough Controls whether to write to the disk file immediately
	 * 
	 * To increase efficiency, this functions can be called consecutively with
	 * writeThrough set to false, followed by a call with writeThrough set to true,
	 * and event set to null.
	 */
	public synchronized void addEvent(OptOutEvent event, boolean writeThrough) {
		if (event == null) {
			if (writeThrough)syncToFile();
			return;
		}
		
		OptOutEvent e1 = null;
		OptOutEvent[] events = findOptOutEvents(event.getAccountID());
		for (int i = 0; i < events.length; i++) {
			if (events[i].getInventoryID()== event.getInventoryID()) {
				e1 = events[i];
				break;
			}
		}
		
		OptOutEvent e2 = null;
		events = findReenableEvents(event.getAccountID());
		for (int i = 0; i < events.length; i++) {
			if (events[i].getInventoryID()== event.getInventoryID()) {
				e2 = events[i];
				break;
			}
		}
		
		if (event.getPeriod()>= 0) {	// This is an opt out event
			if (e1 != null) {
				/* Replace any existing scheduled opt out event */
				optOutEvents.remove(e1);
				reCreateFile = true;
			}
			optOutEvents.add(event);
			newEvents.add(event);
			
			if (e2 != null && e2.getStartDateTime()> event.getStartDateTime()- OVERLAP_INTERVAL) {
				/* If an existing reenable event overlaps with this opt out event,
				 * change the period of the reenable event to PERIOD_REENABLE2,
				 * which means it won't cause a command being sent, and is left there
				 * just to keep record of the program status.
				 */
				e2.setPeriod(PERIOD_REENABLE2);
				reCreateFile = true;
			}
		}
		else {		// This is a reenable event
			if (e2 != null) {
				/* Replace any existing reenable event */
				optOutEvents.remove(e2);
				reCreateFile = true;
			}
			optOutEvents.add(event);
			newEvents.add(event);
			
			if (e1 != null && event.getStartDateTime()> e1.getStartDateTime()- OVERLAP_INTERVAL) {
				/* If an existing scheduled opt out event overlaps with this reenable event ... */
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date(e1.getStartDateTime()));
				cal.add(Calendar.DATE, e1.getPeriod());
				
				if (cal.getTimeInMillis()< event.getStartDateTime()+ OVERLAP_INTERVAL) {
					/* If the extension of the opt out event is shorter than this reenable event, remove it */
					optOutEvents.remove(e1);
					reCreateFile = true;
				}
				else {
					/* Otherwise set the period of the reenable event to PERIOD_REENABLE2 */
					event.setPeriod(PERIOD_REENABLE2);
				}
			}
		}
		
		if (writeThrough) syncToFile();
	}
	
	public synchronized void addEvent(OptOutEvent event) {
		addEvent(event, true);
	}
	
	public synchronized void removeEvent(OptOutEvent event) {
		reCreateFile = optOutEvents.remove(event);
		if (reCreateFile) {
			if (event.getPeriod()>= 0) {
				// When removing an opt out event, we need to check to see if there is a
				// corresponding reenable event with period = PERIOD_REENABLE2.
				// If there is, change its period to PERIOD_REENABLE.
				OptOutEvent[] events = findReenableEvents(event.getAccountID());
				for (int i = 0; i < events.length; i++) {
					if (events[i].getInventoryID()== event.getInventoryID()&& events[i].getPeriod()== PERIOD_REENABLE2) {
						events[i].setPeriod(PERIOD_REENABLE);
						break;
					}
				}
			}
			syncToFile();
		}
	}
	
	public synchronized void removeEvents(int accountID) {
		for (int i = optOutEvents.size()- 1; i >= 0; i--) {
			OptOutEvent e = (OptOutEvent)optOutEvents.get(i);
			if (e.getAccountID()== accountID) {
				optOutEvents.remove(i);
				reCreateFile = true;
			}
		}
		if (reCreateFile) syncToFile();
	}
	
	/**
	 * Return all the events in the queue that are due in ascending order of
	 * their start time. An event is said due if it's earlier than a given
	 * period of time from now.
	 */
	public synchronized OptOutEvent[] getDueEvents(int energyCompanyID, long timeLimit) {
		TreeMap<Long, OptOutEvent> eventTree = new TreeMap<Long, OptOutEvent>();
		long now = new Date().getTime();
		
		for (int i = optOutEvents.size()- 1; i >= 0; i--) {
			OptOutEvent event = (OptOutEvent)optOutEvents.get(i);
			if (event.getEnergyCompanyID()== energyCompanyID &&
				event.getStartDateTime()- now <= timeLimit) {
				if (event.getPeriod()!= PERIOD_REENABLE2) {
					eventTree.put(new Long(event.getStartDateTime()), event);
				}
				optOutEvents.remove(i);
				reCreateFile = true;
			}
		}
		
		if (reCreateFile) syncToFile();
		
		OptOutEvent[] dueEvents = new OptOutEvent[ eventTree.size()];
		eventTree.values().toArray(dueEvents);
		return dueEvents;
	}
	
	public synchronized void syncFromFile() {
		optOutEvents.clear();
		newEvents.clear();
		reCreateFile = false;
		
		try {
			if (!diskFile.exists())diskFile.createNewFile();
		} catch (IOException e) {
			CTILogger.error("Failed to create the opt out event file:");
			CTILogger.error(e.getMessage(), e);
			return;
		}
		
		String[] lines = StarsUtils.readFile(diskFile, false);
		if (lines != null) {
			for (int i = 0; i < lines.length; i++) {
				String[] fields = StarsUtils.splitString(lines[i], ",");
				
				try {
					OptOutEvent event = new OptOutEvent();
					event.setEnergyCompanyID(Integer.parseInt(fields[0]));
					event.setStartDateTime(Long.parseLong(fields[1]));
					event.setPeriod(Integer.parseInt(fields[2]));
					event.setAccountID(Integer.parseInt(fields[3]));
					event.setInventoryID(Integer.parseInt(fields[4]));
					optOutEvents.add(event);
				}
				catch (NumberFormatException e) {
					CTILogger.error(e.getMessage(), e);
				}
			}
		}
	}

}