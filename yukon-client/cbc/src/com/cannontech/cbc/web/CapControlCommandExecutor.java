package com.cannontech.cbc.web;

import java.util.Date;

import com.cannontech.capcontrol.CapBankOperationalState;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointQualities;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.yukon.cbc.CapControlCommand;
import com.cannontech.yukon.cbc.TempMoveCapBank;
import com.cannontech.yukon.cbc.CCVerifySubBus;
import com.cannontech.yukon.cbc.CCVerifySubStation;
import com.cannontech.yukon.cbc.CapBankDevice;

/**
 * @author rneuharth
 *
 * Creates and executes a given CBC command
 * 
 */
public class CapControlCommandExecutor
{
    public static final int defaultOperationalState = -1;
	private final CapControlCache capControlCache;
	private final LiteYukonUser user;
	
	public CapControlCommandExecutor(final CapControlCache capControlCache, LiteYukonUser user) {	
	    this.capControlCache = capControlCache;
	    this.user = user;
	}
	
	public void execute(CapControlType controlType, int cmdId, int paoId) throws UnsupportedOperationException {
	    execute(controlType, cmdId, paoId, null, null);
	}
	
    public void execute(CapControlType controlType, int cmdId, int paoId, 
    		float[] optParams, String operationalState) throws UnsupportedOperationException {
    
        switch (controlType) {
            case AREA :
            case SPECIAL_AREA : {
                executeSubAreaCommand(cmdId, paoId);
                break;
            }
            case SUBSTATION : {
                executeSubStationCommand(cmdId, paoId);
                break;
            }
            case SUBBUS : {
                executeSubBusCommand(cmdId, paoId);
                break;
            }
            case FEEDER : {
                executeFeederCommand(cmdId, paoId);
                break;
            }
            case CAPBANK : {
                int operationalStateValue = getOperationalState(operationalState);
                executeCapBankCommand(cmdId, paoId, optParams, operationalStateValue);
                break;
            }
            case CBC : {
                executeCBCCommand(paoId, optParams);
                break;
            }
            default : throw new UnsupportedOperationException("Unsupported ControlType: " + controlType +
                                                              "cannot execute command: " + cmdId +
                                                              " for pao: " + paoId );
        }
    }
    

    private int getOperationalState(String value) {
        if (value == null) return defaultOperationalState;
        try {
            int operationalState = CapBankOperationalState.valueOf(value).ordinal();
            return operationalState;
        } catch (IllegalArgumentException e) {
            return defaultOperationalState;
        }
    }
    
    public void executeSubAreaCommand(int cmdId, int paoId) {
        CapControlCommand cmd = new CapControlCommand (cmdId, paoId);    
        capControlCache.getConnection().write(cmd);
    }
    
    public void executeSubStationCommand(int cmdId, int paoId) {
        if( cmdId == CapControlCommand.CONFIRM_CLOSE || cmdId == CapControlCommand.CONFIRM_OPEN ) {
            executeConfirmSubstation( paoId );
        }
        if ((cmdId == CapControlCommand.CMD_ALL_BANKS) ||
            (cmdId == CapControlCommand.CMD_FQ_BANKS) ||
            (cmdId == CapControlCommand.CMD_FAILED_BANKS) ||
            (cmdId == CapControlCommand.CMD_QUESTIONABLE_BANKS) ||
            (cmdId == CapControlCommand.CMD_DISABLE_VERIFY)   ||
            (cmdId == CapControlCommand.CMD_STANDALONE_VERIFY)) {
            executeVerifySubstation (paoId, cmdId);
        } else {
            executeCommand( paoId, cmdId );           
        }
    }

    public void executeSubBusCommand(int cmdId, int paoId) {
		if (cmdId == CapControlCommand.CONFIRM_CLOSE || cmdId == CapControlCommand.CONFIRM_OPEN ) {
			executeConfirmSub( paoId );
		}
		if ((cmdId == CapControlCommand.CMD_ALL_BANKS) ||
			(cmdId == CapControlCommand.CMD_FQ_BANKS) ||
			(cmdId == CapControlCommand.CMD_FAILED_BANKS) ||
			(cmdId == CapControlCommand.CMD_QUESTIONABLE_BANKS) ||
			(cmdId == CapControlCommand.CMD_DISABLE_VERIFY)	||
            (cmdId == CapControlCommand.CMD_STANDALONE_VERIFY))
		{
			executeVerifySub (paoId, cmdId);
		}
		else 
		{
			executeCommand( 
				paoId,
				cmdId );			
		}
	}
    
    private void executeVerifySubstation(int paoId, int cmdId) {
        int action = 0;
        if (cmdId == CapControlCommand.CMD_DISABLE_VERIFY)
            action = 1;
        int strat = cmdId - CapControlCommand.VERIFY_OFFSET;  
        CCVerifySubStation msg = new CCVerifySubStation (action, paoId, strat, CCVerifySubStation.DEFAULT_CB_INACT_TIME);
        capControlCache.getConnection().write(msg);
    }

	private void executeVerifySub(int paoId, int cmdId) {
		int action = 0;
		if (cmdId == CapControlCommand.CMD_DISABLE_VERIFY)
			action = 1;
		int strat = cmdId - CapControlCommand.VERIFY_OFFSET;	
		CCVerifySubBus msg = new CCVerifySubBus (action, paoId, strat, CCVerifySubBus.DEFAULT_CB_INACT_TIME);
		capControlCache.getConnection().write(msg);
	}

	public void executeFeederCommand( int cmdId, int paoId) {
		executeCommand( 
			paoId,
			cmdId );			
	}

	public void executeCBCCommand(int paoId, float[] optParams) {
       //TODO validate optionalParams is not null
        
        // Build up the manaual change message here, params[0] = new state ID
        CapBankDevice bank = capControlCache.getCapBankDevice(paoId);
        
        final Date now = new Date();
        
        // Send new point Here
        PointData pt = new PointData();
        pt.setId( bank.getStatusPointID().intValue() );
        pt.setQuality( PointQualities.MANUAL_QUALITY );
        pt.setStr("Manual change occurred using CBC Web Client");
        pt.setTime(now);
        pt.setTimeStamp(now);
        pt.setType( PointTypes.STATUS_POINT );
        pt.setUserName( getUserName() );

        // the actual new value for the selected state 
        pt.setValue( optParams[0] );

        capControlCache.getConnection().write( pt );
	    
	}
	
	public void executeCapBankCommand(int _cmdID, int _paoID, float[] _optionalParams) {
	    executeCapBankCommand(_cmdID, _paoID, _optionalParams, defaultOperationalState);
	}
	
	public void executeCapBankCommand(int cmdId, int paoId, float[] optionalParams, int operationalState) {
	    switch (cmdId) {
	        case CapControlCommand.CONFIRM_CLOSE : {
	            executeCapBankCmdConfirm(paoId, defaultOperationalState);
	            break;
	        }
	        case CapControlCommand.CONFIRM_OPEN : {
	            executeCapBankCmdConfirm(paoId, defaultOperationalState);
                break;
	        }
	        case CapControlCommand.CLOSE_CAPBANK : {
	            executeCapBankCmdByCmdId(paoId, cmdId, defaultOperationalState);
	            break;
	        }
	        case CapControlCommand.OPEN_CAPBANK : {
	            executeCapBankCmdByCmdId(paoId, cmdId, defaultOperationalState);
                break;
	        }
	        case CapControlCommand.BANK_DISABLE_OVUV : {
	            executeCapBankCmdByCmdId(paoId, cmdId, defaultOperationalState);
                break;
	        }
	        case CapControlCommand.BANK_ENABLE_OVUV : {
	            executeCapBankCmdByCmdId(paoId, cmdId, defaultOperationalState);
                break;
	        }
	        case CapControlCommand.SCAN_2WAY_DEV : {
	            executeCapBankCmdByCmdId(paoId, cmdId, defaultOperationalState);
                break;
	        }
	        case CapControlCommand.CMD_MANUAL_ENTRY : {
	            if (optionalParams.length == 0) {
	                executeCapBankDefault(paoId, cmdId, defaultOperationalState);
	                break;
	            }
	            executeCBCCommand(paoId, optionalParams);
	            break;
	        }
	        case CapControlCommand.RESET_OPCOUNT : {
	            executeCapBankCmdResetOpCount(paoId, optionalParams);
	            break;
	        }
	        case CapControlCommand.CMD_BANK_TEMP_MOVE : {
	            if (optionalParams.length < 5) {
	                executeCapBankDefault(paoId, cmdId, defaultOperationalState);
                    break;
	            }
	            executeCapBankCmdTempMove(paoId, optionalParams);
	            break;
	        }
	        case CapControlCommand.DISABLE_CAPBANK : {
	            executeCapBankDefault(paoId, cmdId, operationalState);
                break;
	        }
	        case CapControlCommand.ENABLE_CAPBANK : {
	            executeCapBankDefault(paoId, cmdId, operationalState);
                break;
	        }
	        case CapControlCommand.OPERATIONAL_STATECHANGE : {
	            executeCapBankDefault(paoId, cmdId, operationalState);
	            break;
	        }
	        case CapControlCommand.RETURN_BANK_TO_FEEDER : {
	        	executeCapBankDefault(paoId, cmdId, operationalState);
	        	break;
	        }
	        case CapControlCommand.SEND_TIMESYNC : {
	            executeCapBankDefault(paoId, cmdId, operationalState);
	            break;
	        }
	        case CapControlCommand.FLIP_7010_CAPBANK : {
	            executeCapBankDefault(paoId, cmdId, operationalState);
                break;
            }
	        default: {
	        	throw new UnsupportedOperationException("Cap Bank Command, " + cmdId + ", not supported." );
	        }
	    }
	}
	
	private void executeCapBankDefault(final int paoId, final int cmdId, final int operationalState) {
	    boolean isValidCommand = isValidBankCmd(paoId, cmdId);
	    if (isValidCommand) executeCommand(paoId, cmdId, operationalState);
	}
	
	private void executeCapBankCmdTempMove(final int paoId, final float[] optionalParams) {
	    //TODO: Validate Optional parameters is not null
		TempMoveCapBank msg = new TempMoveCapBank(
	                                                    (int)optionalParams[0], // original feeder ID
	                                                    (int)optionalParams[1], // new feeder ID
	                                                    paoId,
	                                                    optionalParams[2], // order of control
	                                                    optionalParams[3],
	                                                    optionalParams[4],
	                                                    false );
	    capControlCache.getConnection().write( msg );
	}
	
	private void executeCapBankCmdResetOpCount(final int paoId, final float[] optionalParams) {
        
		//TODO: Validate optionalParams is not null
		
		// Build up the reset opcount message here
        CapBankDevice bank = capControlCache.getCapBankDevice(paoId);

        final Date now = new Date();
        
        // Send new point Here
        PointData pt = new PointData();
        pt.setId( bank.getOperationAnalogPointID().intValue() );
        pt.setQuality( PointQualities.MANUAL_QUALITY );
        pt.setStr("Capacitor Bank OP_COUNT change from CBC Client");
        pt.setTime(now);
        pt.setTimeStamp(now);
        pt.setType( PointTypes.ANALOG_POINT );
        pt.setUserName( getUserName() );

        //the actual new value for the selected state
        float pointValue = (optionalParams.length > 0) ? optionalParams[0] : 0.0f;
        pt.setValue(pointValue);
        
        capControlCache.getConnection().write( pt );    
	}
	
	private void executeCapBankCmdByCmdId(final int paoId, final int cmdId, final int operationalState) {
	    CapBankDevice bank = capControlCache.getCapBankDevice(paoId);
	    if (bank == null) return;
        int deviceId = bank.getControlDeviceID(); 
        executeCommand(deviceId, cmdId, operationalState); 
	}
	
	private void executeCapBankCmdConfirm(final int paoId, final int operationalState) {
	    CapBankDevice bank = capControlCache.getCapBankDevice(paoId);
	    int deviceId = bank.getControlDeviceID();
	    
	    if (CapBankDevice.isInAnyOpenState(bank)) {
	        executeCommand(deviceId, CapControlCommand.CONFIRM_OPEN, operationalState);
	        return;
	    }
	    
	    if (CapBankDevice.isInAnyCloseState(bank)) {
	        executeCommand(deviceId, CapControlCommand.CONFIRM_CLOSE, operationalState);
	        return;
	    }
	}
	
	private boolean isValidBankCmd(int paoId, int cmdId) {
		CapBankDevice bank =
			capControlCache.getCapBankDevice( new Integer(paoId) );

		switch( cmdId )
		{
			case CapControlCommand.RETURN_BANK_TO_FEEDER:
				return bank.isBankMoved();			

			case CapControlCommand.ENABLE_CAPBANK:
				return bank.getCcDisableFlag().booleanValue();

			case CapControlCommand.DISABLE_CAPBANK:
				return !bank.getCcDisableFlag().booleanValue();
		}		

		return true;		
	}

	private String getUserName() {
		String username = user.getUsername();
		return username;
	}

	private void executeConfirmSub(int paoId) {
		Multi<CapControlCommand> multi = new Multi<CapControlCommand>();
		CapControlCommand command = new CapControlCommand (CapControlCommand.CONFIRM_SUB, paoId);
		command.setUserName(getUserName());
		multi.getVector().add(command);
		if (multi.getVector().size() > 0) {
			capControlCache.getConnection().write(multi);
		}	
	}
	
    private void executeConfirmSubstation(int paoId) {
        Multi<CapControlCommand> multi = new Multi<CapControlCommand>();
        CapControlCommand command = new CapControlCommand (CapControlCommand.CONFIRM_SUBSTATION, paoId);
        command.setUserName(getUserName());
        multi.getVector().add(command);
        if (multi.getVector().size() > 0) {
            capControlCache.getConnection().write(multi);
        }
    }

    public void executeCommand(int paoId, int cmdOperation) {
        executeCommand(paoId, cmdOperation, CapControlCommandExecutor.defaultOperationalState);
    }
    
	public void executeCommand(int paoId, int cmdOperation, int operationalState) {
		CapControlCommand cmd = new CapControlCommand();
		cmd.setDeviceID( paoId );
		cmd.setCommand( cmdOperation );
		cmd.setUserName( getUserName() );
		cmd.setToken(operationalState);
		
		capControlCache.getConnection().sendCommand( cmd );
	}

}
