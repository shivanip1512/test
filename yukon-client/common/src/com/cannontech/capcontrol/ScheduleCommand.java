package com.cannontech.capcontrol;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.message.capcontrol.model.CommandType;

public enum ScheduleCommand {
	
	VerifyAll("Verify ALL CapBanks", CommandType.VERIFY_ALL_BANKS.getCommandId()),
	VerifyFailed("Verify Failed CapBanks", CommandType.VERIFY_FAILED_BANKS.getCommandId()),
	VerifyFailedAndQuestionable("Verify Failed and Questionable CapBanks", CommandType.VERIFY_FQ_BANKS.getCommandId()),
	VerifyStandalone("Verify Standalone CapBanks", CommandType.VERIFY_SA_BANKS.getCommandId()),
	VerifyQuestionable("Verify Questionable CapBanks", CommandType.VERIFY_Q_BANKS.getCommandId()),
	VerifyNotOperatedIn("Verify CapBanks that have not operated in 0 min 0 hr 0 day 0 wk", CommandType.VERIFY_INACTIVE_BANKS.getCommandId()),
    ConfirmSub("Confirm Sub", CommandType.CONFIRM_SUBSTATION_BUS.getCommandId()),
    SendTimeSyncs("Send Time Syncs", CommandType.SEND_TIME_SYNC.getCommandId()),
    SendSyncCBCCapbankStates("Sync CapBank States", CommandType.SEND_SYNC_CBC_CAPBANK_STATE.getCommandId()),
    DmvTest("DMV Test", CommandType.SCHEDULE_DMV_TEST.getCommandId());

    private static List<ScheduleCommand> verifyList = null;
    private static List<ScheduleCommand> requiredCommands = null;
	
	static {
	    verifyList = new ArrayList<ScheduleCommand>();
        for(ScheduleCommand command : ScheduleCommand.values()){
            if(command.commandName.startsWith("Verify")){
                verifyList.add(command);
            }
        }
        
        requiredCommands = new ArrayList<ScheduleCommand>();
        for (ScheduleCommand command : ScheduleCommand.values()) {
            if (command != DmvTest) {
                requiredCommands.add(command);
            }
        }
	}
	
	public static final int DEFAULT_INACTIVITY_TIME = -1;
	
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
	
	static public ScheduleCommand getEnum(String value) {
	    if (StringUtils.isNotEmpty(value) && value.contains(VerifyNotOperatedIn.getCommandName().substring(0,41))) {
	        return VerifyNotOperatedIn;
	    }
	    return valueOf(value);
	}
	
    public static List<ScheduleCommand> getRequiredCommands() {
        return requiredCommands;
    }


}
