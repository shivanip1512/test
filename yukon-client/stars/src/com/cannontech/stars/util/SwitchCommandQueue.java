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

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SwitchCommandQueue {
	
	public static class SwitchCommand {
		private int energyCompanyID = 0;
		private int inventoryID = 0;
		private String serialNumber = null;
		private String commandType = null;
		
		public String getCommandType() {
			return commandType;
		}

		public int getEnergyCompanyID() {
			return energyCompanyID;
		}

		public int getInventoryID() {
			return inventoryID;
		}

		public String getSerialNumber() {
			return serialNumber;
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

		public void setSerialNumber(String string) {
			serialNumber = string;
		}
	}
	
	public static final String SWITCH_COMMAND_ENABLE = "Enable";
	public static final String SWITCH_COMMAND_DISABLE = "Disable";
	public static final String SWITCH_COMMAND_CONFIGURE = "Configure";
	
	private File diskFile = null;
	private ArrayList switchCommands = new ArrayList();
	
	private boolean reCreateFile = false;
	private ArrayList newCommands = new ArrayList();
	
	public SwitchCommandQueue(String fileName) throws IOException {
		diskFile = new File( fileName );
		if (!diskFile.exists()) {
			File dir = diskFile.getParentFile();
			if (dir != null && !dir.exists()) dir.mkdirs();
			diskFile.createNewFile();
		}
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
				cmd.setInventoryID( Integer.parseInt(st.nextToken()) );
				cmd.setSerialNumber( st.nextToken() );
				cmd.setCommandType( st.nextToken() );
				switchCommands.add( cmd );
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
	
	private void syncToFile() {
		PrintWriter fw = null;
		
		try {
			if (reCreateFile) {
				fw = new PrintWriter( new FileWriter(diskFile, false) );
				for (int i = 0; i < switchCommands.size(); i++) {
					SwitchCommand cmd = (SwitchCommand) switchCommands.get(i);
					StringBuffer line = new StringBuffer();
					line.append( cmd.getEnergyCompanyID() )
						.append( " " )
						.append( cmd.getInventoryID() )
						.append( " " )
						.append( cmd.getSerialNumber() )
						.append( " " )
						.append( cmd.getCommandType() );
					fw.println( line.toString() );
				}
			}
			else {
				if (newCommands.size() == 0) return;
				
				fw = new PrintWriter( new FileWriter(diskFile, true) );
				for (int i = 0; i < newCommands.size(); i++) {
					SwitchCommand cmd = (SwitchCommand) newCommands.get(i);
					StringBuffer line = new StringBuffer();
					line.append( cmd.getEnergyCompanyID() )
						.append( " " )
						.append( cmd.getInventoryID() )
						.append( " " )
						.append( cmd.getSerialNumber() )
						.append( " " )
						.append( cmd.getCommandType() );
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
