package com.cannontech.stars.util;

import java.io.*;
import java.util.*;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class OptOutEventQueue {
	
	public static class OptOutEvent {
		private long startDateTime = 0;
		private int period = 0;
		private int accountID = 0;
		private String command = null;
		
		/**
		 * Returns the accountID.
		 * @return int
		 */
		public int getAccountID() {
			return accountID;
		}

		/**
		 * Returns the command.
		 * @return String
		 */
		public String getCommand() {
			if (command == null) command = "";
			return command;
		}

		/**
		 * Returns the period.
		 * @return int
		 */
		public int getPeriod() {
			return period;
		}

		/**
		 * Returns the startDateTime.
		 * @return long
		 */
		public long getStartDateTime() {
			return startDateTime;
		}

		/**
		 * Sets the accountID.
		 * @param accountID The accountID to set
		 */
		public void setAccountID(int accountID) {
			this.accountID = accountID;
		}

		/**
		 * Sets the command.
		 * @param command The command to set
		 */
		public void setCommand(String command) {
			this.command = command;
		}

		/**
		 * Sets the period.
		 * @param period The period to set
		 */
		public void setPeriod(int period) {
			this.period = period;
		}

		/**
		 * Sets the startDateTime.
		 * @param startDateTime The startDateTime to set
		 */
		public void setStartDateTime(long startDateTime) {
			this.startDateTime = startDateTime;
		}
	}
	
	private File diskFile = null;
	private ArrayList optOutEvents = new ArrayList();
	
	private boolean reCreateFile = false;
	private ArrayList newEvents = new ArrayList();
	
	public OptOutEventQueue(String fileName) throws IOException {
		diskFile = new File( fileName );
		if (!diskFile.exists()) {
			File dir = new File( diskFile.getParent() );
			if (!dir.exists()) dir.mkdirs();
			diskFile.createNewFile();
		}
	}
	
	public OptOutEvent findEvent(int accountID) {
		for (int i = 0; i < optOutEvents.size(); i++) {
			OptOutEvent e = (OptOutEvent) optOutEvents.get(i);
			if (e.getAccountID() == accountID)
				return e;
		}
		return null;
	}
	
	private void syncToFile() {
		PrintWriter fw = null;
		try {
			if (reCreateFile) {
				fw = new PrintWriter( new FileWriter(diskFile, false) );
				for (int i = 0; i < optOutEvents.size(); i++) {
					OptOutEvent event = (OptOutEvent) optOutEvents.get(i);
					StringBuffer line = new StringBuffer();
					line.append( event.getStartDateTime() )
						.append( " " )
						.append( event.getPeriod() )
						.append( " " )
						.append( event.getAccountID() )
						.append( " \"" )
						.append( event.getCommand() )
						.append( "\"" );
					fw.println( line.toString() );
				}
			}
			else {
				fw = new PrintWriter( new FileWriter(diskFile, true) );
				for (int i = 0; i < newEvents.size(); i++) {
					OptOutEvent event = (OptOutEvent) newEvents.get(i);
					StringBuffer line = new StringBuffer();
					line.append( event.getStartDateTime() )
						.append( " " )
						.append( event.getPeriod() )
						.append( " " )
						.append( event.getAccountID() )
						.append( " \"" )
						.append( event.getCommand() )
						.append( "\"" );
					fw.println( line.toString() );
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (fw != null) fw.close();
		}
		reCreateFile = false;
		newEvents.clear();
	}
	
	public synchronized void addEvent(OptOutEvent event, boolean writeThrough) {
		if (event == null) {
			if (writeThrough) syncToFile();
			return;
		}
		
		OptOutEvent e = findEvent( event.getAccountID() );
		if (e != null) {
			reCreateFile = true;
			e.setStartDateTime( event.getStartDateTime() );
			e.setPeriod( event.getPeriod() );
			e.setCommand( event.getCommand() );
		}
		else {
			optOutEvents.add( event );
			newEvents.add( event );
		}
		
		if (writeThrough) syncToFile();
	}
	
	public synchronized void addEvent(OptOutEvent event) {
		addEvent( event, true );
	}
	
	public synchronized void removeEvent(int accountID) {
		OptOutEvent e = findEvent( accountID );
		if (e != null) {
			optOutEvents.remove( e );
			reCreateFile = true;
			syncToFile();
		}
	}
	
	public synchronized ArrayList consumeEvents(long timeLimit) {
		ArrayList dueEvents = new ArrayList();
		for (int i = optOutEvents.size() - 1; i >= 0; i--) {
			OptOutEvent event = (OptOutEvent) optOutEvents.get(i);
			if (event.getStartDateTime() <= timeLimit) {
				dueEvents.add( event );
				optOutEvents.remove( event );
				reCreateFile = true;
			}
		}
		if (dueEvents.size() > 0) syncToFile();
		return dueEvents;
	}
	
	public synchronized void syncFromFile() {
		optOutEvents.clear();
		newEvents.clear();
		reCreateFile = false;
		
		BufferedReader fr = null;
		try {
			fr = new BufferedReader( new FileReader(diskFile) );
			String line = null;
			while ((line = fr.readLine()) != null) {
				StringTokenizer st = new StringTokenizer( line );
				OptOutEvent event = new OptOutEvent();
				event.setStartDateTime( Long.parseLong(st.nextToken()) );
				event.setPeriod( Integer.parseInt(st.nextToken()) );
				event.setAccountID( Integer.parseInt(st.nextToken()) );
				String cmd = st.nextToken( "\n" );
				event.setCommand( cmd.substring(cmd.indexOf('\"')+1, cmd.lastIndexOf('\"')) );
				optOutEvents.add( event );
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (fr != null) fr.close();
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

}
