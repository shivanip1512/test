package com.cannontech.capcontrol;

public enum ScheduleCommand {
	
	VerifyAll("Verify ALL CapBanks"),
	VerifyFailed("Verify Failed CapBanks"),
	VerifyFailedAndQuestionable("Verify Failed and Questionable CapBanks"),
	VerufyStandalone("Verify Standalone CapBanks"),
	VerifyQuestionable("Verify Questionable CapBanks"),
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
			if (commandName.equals(value.commandName)) {
				return value;
			}
		}
		throw new IllegalArgumentException();
	}
		
}
