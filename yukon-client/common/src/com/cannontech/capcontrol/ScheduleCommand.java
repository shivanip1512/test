package com.cannontech.capcontrol;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.yukon.cbc.CapControlCommand;

public enum ScheduleCommand {
	
	VerifyAll("Verify ALL CapBanks", CapControlCommand.CMD_ALL_BANKS),
	VerifyFailed("Verify Failed CapBanks", CapControlCommand.CMD_FAILED_BANKS),
	VerifyFailedAndQuestionable("Verify Failed and Questionable CapBanks", CapControlCommand.CMD_FQ_BANKS),
	VerifyStandalone("Verify Standalone CapBanks", CapControlCommand.CMD_STANDALONE_VERIFY),
	VerifyQuestionable("Verify Questionable CapBanks", CapControlCommand.CMD_QUESTIONABLE_BANKS),
	VerifyNotOperatedIn("Verify CapBanks that have not operated in", CapControlCommand.CMD_BANKS_NOT_OPERATED_IN),
    ConfirmSub("Confirm Sub", CapControlCommand.CONFIRM_SUB),
    SendTimeSyncs("Send Time Syncs", CapControlCommand.SEND_TIMESYNC);
	
	private static List<ScheduleCommand> verifyList = null;
	
	static {
	    verifyList = new ArrayList<ScheduleCommand>();
        for(ScheduleCommand command : ScheduleCommand.values()){
            if(command.commandName.startsWith("Verify")){
                verifyList.add(command);
            }
        }
	}
	
	private String commandName;
	private int capControlCommand;
	
	private ScheduleCommand(String commandName, int capControlCommand) {
		this.commandName = commandName;
		this.capControlCommand = capControlCommand;
	}
	
	public String getCommandName() {
		return this.commandName;
	}
	
	public int getCapControlCommand(){
	    return capControlCommand;
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
	    return verifyList;
	}
}
