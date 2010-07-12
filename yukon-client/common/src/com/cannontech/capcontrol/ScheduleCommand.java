package com.cannontech.capcontrol;

import java.util.ArrayList;
import java.util.List;

public enum ScheduleCommand {
	
	VerifyAll("Verify ALL CapBanks"),
	VerifyFailed("Verify Failed CapBanks"),
	VerifyFailedAndQuestionable("Verify Failed and Questionable CapBanks"),
	VerifyStandalone("Verify Standalone CapBanks"),
	VerifyQuestionable("Verify Questionable CapBanks"),
	VerifyNotOperatedIn("Verify CapBanks that have not operated in"),
    ConfirmSub("Confirm Sub"),
    SendTimeSyncs("Send Time Syncs");
	
	private String commandName;
	
	private ScheduleCommand(String commandName) {
		this.commandName = commandName;
	}
	
	public String getCommandName() {
		return this.commandName;
	}
		
	static public ScheduleCommand getScheduleCommand(String commandName) {
		for (ScheduleCommand value : ScheduleCommand.values()) {
			//startsWith is necessary for VerifyNotOperatedIn.  The end of that command
			//string includes several time variables, so it can't be matched exactly.
			if (commandName.startsWith(value.commandName)) {
				return value;
			}
		}
		throw new IllegalArgumentException();
	}
	
	//returns a list of all verification schedule commands
	static public List<ScheduleCommand> getVerifyCommandsList(){
	    List<ScheduleCommand> verifyList = new ArrayList<ScheduleCommand>();
	    for(ScheduleCommand command : ScheduleCommand.values()){
	        if(command.commandName.startsWith("Verify")){
	            verifyList.add(command);
	        }
	    }
	    return verifyList;
	}
}
