package com.cannontech.stars.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import com.cannontech.clientutils.CTILogger;
import com.google.common.collect.Lists;

public class SwitchCommandQueue {
	
    private static final String SWITCH_COMMAND_FILE = "switch_commands.txt";
	public static final String SWITCH_COMMAND_ENABLE = "Enable";
    public static final String SWITCH_COMMAND_DISABLE = "Disable";
    public static final String SWITCH_COMMAND_CONFIGURE = "Configure";
    public static final String GROUP_ADDRESSING = "Group Addressing";
    public static final String HARDWARE_ADDRESSING = "Hardware Addressing";
    
    private File diskFile = null;
    private boolean reCreateFile = false;
    private ArrayList<SwitchCommand> switchCommands = Lists.newArrayList();
    private ArrayList<SwitchCommand> newCommands = Lists.newArrayList();
    
    private static SwitchCommandQueue instance = null;
    
    private SwitchCommandQueue(File file) {
        diskFile = file;
    }
    
    public static SwitchCommandQueue getInstance() {
        if (instance == null) {
            File tempDir = new File(StarsUtils.getStarsTempDir());
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }
            
            instance = new SwitchCommandQueue(new File(tempDir, SWITCH_COMMAND_FILE));
            instance.syncFromFile();
        }
        
        return instance;
    }
	
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
		public int getAccountID() {
			return accountID;
		}
		public void setAccountID(int i) {
			accountID = i;
		}
		public String getInfoString() {
			return infoString;
		}
		public void setInfoString(String string) {
			infoString = string;
		}

		public String toString() {
			StringBuffer line = new StringBuffer();
			line.append(getEnergyCompanyID())
				.append(",")
				.append(getAccountID())
				.append(",")
				.append(getInventoryID())
				.append(",")
				.append(getCommandType());
			if (getInfoString() != null)
				line.append(",\"").append(getInfoString()).append("\"");
			
			return line.toString();
		}
	}
	
	public synchronized void syncFromFile() {
		switchCommands.clear();
		newCommands.clear();
		reCreateFile = false;
		
		try {
			if (!diskFile.exists()) diskFile.createNewFile();
		}
		catch (IOException e) {
			CTILogger.error("Failed to create the switch command file:");
			CTILogger.error(e.getMessage(), e);
			return;
		}
		
		String[] lines = StarsUtils.readFile(diskFile, false);
		if (lines != null) {
			for (int i = 0; i < lines.length; i++) {
				String[] fields = StarsUtils.splitString(lines[i], ",");
				
				try {
					SwitchCommand cmd = new SwitchCommand();
					cmd.setEnergyCompanyID(Integer.parseInt(fields[0]));
					cmd.setAccountID(Integer.parseInt(fields[1]));
					cmd.setInventoryID(Integer.parseInt(fields[2]));
					cmd.setCommandType(fields[3]);
					if (fields.length > 4)
						cmd.setInfoString(fields[4]);
					switchCommands.add(cmd);
				}
				catch (NumberFormatException e) {
					CTILogger.error(e.getMessage(), e);
				}
			}
		}
	}
	
	private void syncToFile() {
		PrintWriter fw = null;
		ArrayList<SwitchCommand> cmdsToWrite = null;
		
		try {
			if (reCreateFile) {
				fw = new PrintWriter(new FileWriter(diskFile, false));
				cmdsToWrite = switchCommands;
			}
			else {
				if (newCommands.size() == 0) return;
				fw = new PrintWriter(new FileWriter(diskFile, true));
				cmdsToWrite = newCommands;
			}
			
			for (int i = 0; i < cmdsToWrite.size(); i++) {
				SwitchCommand cmd = (SwitchCommand) cmdsToWrite.get(i);
				fw.println(cmd.toString());
			}
		}
		catch (Exception e) {
			CTILogger.error(e.getMessage(), e);
		}
		finally {
			if (fw != null) fw.close();
		}
		
		reCreateFile = false;
		newCommands.clear();
	}
	
	public synchronized SwitchCommand getCommand(int invID, boolean remove) {
		for (int i = 0; i < switchCommands.size(); i++) {
			SwitchCommand cmd = (SwitchCommand) switchCommands.get(i);
			if (cmd.getInventoryID() == invID) {
				if (remove) switchCommands.remove(i);
				return cmd;
			} 
		}
		
		return null;
	}
	
	public synchronized void addCommand(SwitchCommand cmd, boolean writeThrough) {
		if (cmd != null && getCommand(cmd.getInventoryID(), false) == null) {
			switchCommands.add(cmd);
			newCommands.add(cmd);
		}
		if (writeThrough) syncToFile();
	}
	
	public synchronized void removeCommand(int invID) {
		for (int i = 0; i < switchCommands.size(); i++) {
			if (((SwitchCommand) switchCommands.get(i)).getInventoryID() == invID) {
				switchCommands.remove(i);
				return;
			}
		}
	}
	
	public synchronized SwitchCommand[] getCommands(int energyCompanyID, boolean remove) {
		ArrayList<SwitchCommand> cmdList = Lists.newArrayList();
		Iterator<SwitchCommand> it = switchCommands.iterator();
		while (it.hasNext()) {
			SwitchCommand cmd = it.next();
			if (cmd.getEnergyCompanyID() == energyCompanyID) {
				cmdList.add(cmd);
				if (remove) it.remove();
				reCreateFile = true;
			}
		}
		
		if (reCreateFile) syncToFile();
		
		SwitchCommand[] commands = new SwitchCommand[ cmdList.size() ];
		cmdList.toArray(commands);
		return commands;
	}
	
	public synchronized void clearCommands(int energyCompanyID) {
		Iterator<SwitchCommand> it = switchCommands.iterator();
		while (it.hasNext()) {
			SwitchCommand cmd = it.next();
			if (cmd.getEnergyCompanyID() == energyCompanyID) {
				it.remove();
				reCreateFile = true;
			}
		}
		
		if (reCreateFile) syncToFile();
	}

}