/*
 * Created on Oct 7, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.cannontech.clientutils.CTILogger;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SwitchCommandQueue {
	
	private static SwitchCommandQueue instance = null;
	
	public static class SwitchCommand {
		private int energyCompanyID = 0;
		private int accountID = 0;
		private int inventoryID = 0;
		private String commandType = null;
		private String infoString = null;
		
		public String getCommandType() {
			return commandType;
		}

		public int getEnergyCompanyID() {
			return energyCompanyID;
		}

		public int getInventoryID() {
			return inventoryID;
		}

		public void setCommandType(String string) {
			commandType = string;
		}

		public void setEnergyCompanyID(int i) {
			energyCompanyID = i;
		}

		public void setInventoryID(int i) {
			inventoryID = i;
		}

		/**
		 * @return
		 */
		public int getAccountID() {
			return accountID;
		}

		/**
		 * @param i
		 */
		public void setAccountID(int i) {
			accountID = i;
		}

		/**
		 * @return
		 */
		public String getInfoString() {
			return infoString;
		}

		/**
		 * @param string
		 */
		public void setInfoString(String string) {
			infoString = string;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			StringBuffer line = new StringBuffer();
			line.append( getEnergyCompanyID() )
				.append( " " )
				.append( getAccountID() )
				.append( " " )
				.append( getInventoryID() )
				.append( " " )
				.append( getCommandType() );
			if (getInfoString() != null)
				line.append( " " ).append( getInfoString() );
			
			return line.toString();
		}

	}
	
	public static final String SWITCH_COMMAND_ENABLE = "Enable";
	public static final String SWITCH_COMMAND_DISABLE = "Disable";
	public static final String SWITCH_COMMAND_CONFIGURE = "Configure";
	
	private File diskFile = null;
	private ArrayList switchCommands = new ArrayList();
	
	private boolean reCreateFile = false;
	private ArrayList newCommands = new ArrayList();
	
	public static SwitchCommandQueue getInstance() throws IOException {
		if (instance == null) {
			File tempDir = new File( ServerUtils.getStarsTempDir() );
			if (!tempDir.exists())
				tempDir.mkdirs();
			
			instance = new SwitchCommandQueue( new File(tempDir, ServerUtils.SWITCH_COMMAND_FILE) );
		}
		
		return instance;
	}
	
	private SwitchCommandQueue(File file) {
		diskFile = file;
	}
	
	public synchronized void syncFromFile() {
		switchCommands.clear();
		newCommands.clear();
		reCreateFile = false;
		BufferedReader fr = null;
		
		try {
			fr = new BufferedReader( new FileReader(diskFile) );
			String line = null;
			while ((line = fr.readLine()) != null) {
				StringTokenizer st = new StringTokenizer( line );
				SwitchCommand cmd = new SwitchCommand();
				cmd.setEnergyCompanyID( Integer.parseInt(st.nextToken()) );
				cmd.setAccountID( Integer.parseInt(st.nextToken()) );
				cmd.setInventoryID( Integer.parseInt(st.nextToken()) );
				cmd.setCommandType( st.nextToken() );
				if (st.hasMoreTokens())
					cmd.setInfoString( st.nextToken() );
				switchCommands.add( cmd );
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		finally {
			try {
				if (fr != null) fr.close();
			}
			catch (IOException e) {
				CTILogger.error( e.getMessage(), e );
			}
		}
	}
	
	private void syncToFile() {
		PrintWriter fw = null;
		ArrayList cmdsToWrite = null;
		
		try {
			if (reCreateFile) {
				fw = new PrintWriter( new FileWriter(diskFile, false) );
				cmdsToWrite = switchCommands;
			}
			else {
				if (newCommands.size() == 0) return;
				fw = new PrintWriter( new FileWriter(diskFile, true) );
				cmdsToWrite = newCommands;
			}
			
			for (int i = 0; i < cmdsToWrite.size(); i++) {
				SwitchCommand cmd = (SwitchCommand) cmdsToWrite.get(i);
				fw.println( cmd.toString() );
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		finally {
			if (fw != null) fw.close();
		}
		
		reCreateFile = false;
		newCommands.clear();
	}
	
	public synchronized SwitchCommand getCommand(int invID, String cmdType) {
		for (int i = 0; i < switchCommands.size(); i++) {
			SwitchCommand cmd = (SwitchCommand) switchCommands.get(i);
			if (cmd.getInventoryID() == invID && cmd.getCommandType().equalsIgnoreCase(cmdType))
				return cmd;
		}
		
		return null;
	}
	
	public synchronized void addCommand(SwitchCommand cmd, boolean writeThrough) {
		if (cmd != null && getCommand(cmd.getInventoryID(), cmd.getCommandType()) == null) {
			switchCommands.add( cmd );
			newCommands.add( cmd );
		}
		if (writeThrough) syncToFile();
	}
	
	public synchronized SwitchCommand[] getCommands(int energyCompanyID) {
		ArrayList cmdList = new ArrayList();
		Iterator it = switchCommands.iterator();
		while (it.hasNext()) {
			SwitchCommand cmd = (SwitchCommand) it.next();
			if (cmd.getEnergyCompanyID() == energyCompanyID) {
				cmdList.add( cmd );
				it.remove();
				reCreateFile = true;
			}
		}
		
		if (reCreateFile) syncToFile();
		
		SwitchCommand[] commands = new SwitchCommand[ cmdList.size() ];
		cmdList.toArray( commands );
		return commands;
	}
	
	public synchronized void clearCommands(int energyCompanyID) {
		Iterator it = switchCommands.iterator();
		while (it.hasNext()) {
			SwitchCommand cmd = (SwitchCommand) it.next();
			if (cmd.getEnergyCompanyID() == energyCompanyID) {
				it.remove();
				reCreateFile = true;
			}
		}
		
		if (reCreateFile) syncToFile();
	}

}
