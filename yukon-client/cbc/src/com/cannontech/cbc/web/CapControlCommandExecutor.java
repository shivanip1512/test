package com.cannontech.cbc.web;

import java.util.Date;
import java.util.List;

import com.cannontech.capcontrol.CapBankOperationalState;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.cbc.CCVerifySubBus;
import com.cannontech.yukon.cbc.CCVerifySubStation;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.CapControlCommand;
import com.cannontech.yukon.cbc.DynamicCommand;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.cbc.TempMoveCapBank;
import com.cannontech.yukon.cbc.DynamicCommand.CommandType;
import com.cannontech.yukon.cbc.DynamicCommand.Parameter;

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
	
	public void executeDeltaUpdate(int bankId, int pointId, double delta, boolean staticDelta) {
	    DynamicCommand command = new DynamicCommand(CommandType.DELTA);
	    
	    command.addParameter(Parameter.DEVICE_ID, bankId);
	    command.addParameter(Parameter.POINT_ID, pointId);
	    command.addParameter(Parameter.POINT_RESPONSE_DELTA, delta);
	    command.addParameter(Parameter.POINT_RESPONSE_STATIC_DELTA, staticDelta?1:0);
	    
	    capControlCache.getConnection().sendCommand(command);
	}
	
	public void execute(CapControlType controlType, int cmdId, int paoId, LiteYukonUser user) throws UnsupportedOperationException {
	    execute(controlType, cmdId, paoId, CCVerifySubBus.DEFAULT_CB_INACT_TIME, user);
	}
	
	public void execute(CapControlType controlType, int cmdId, int paoId, float[] optParams, 
	                    String operationalState, LiteYukonUser user) throws UnsupportedOperationException {
	    execute(controlType, cmdId, paoId, CCVerifySubBus.DEFAULT_CB_INACT_TIME, optParams, operationalState, user);
	}
	
	public void execute(CapControlType controlType, int cmdId, int paoId, long inactiveTime, LiteYukonUser user) throws UnsupportedOperationException {
	    execute(controlType, cmdId, paoId, inactiveTime, null, null, user);
	}
	
    public void execute(CapControlType controlType, int cmdId, int paoId, long inactiveTime, 
    		float[] optParams, String operationalState, LiteYukonUser user) throws UnsupportedOperationException {
        
        RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao", RolePropertyDao.class);
        
        switch (controlType) {
            case AREA :
            case SPECIAL_AREA : {
                rolePropertyDao.verifyProperty(YukonRoleProperty.ALLOW_AREA_CONTROLS, user);
                executeSubAreaCommand(cmdId, paoId);
                break;
            }
            case SUBSTATION : {
                rolePropertyDao.verifyProperty(YukonRoleProperty.ALLOW_SUBSTATION_CONTROLS, user);
                executeSubStationCommand(cmdId, paoId);
                break;
            }
            case SUBBUS : {
                rolePropertyDao.verifyProperty(YukonRoleProperty.ALLOW_SUBBUS_CONTROLS, user);
                executeSubBusCommand(cmdId, paoId, inactiveTime);
                break;
            }
            case FEEDER : {
                rolePropertyDao.verifyProperty(YukonRoleProperty.ALLOW_FEEDER_CONTROLS, user);
                executeFeederCommand(cmdId, paoId);
                break;
            }
            case CAPBANK : {
                rolePropertyDao.verifyProperty(YukonRoleProperty.ALLOW_CAPBANK_CONTROLS, user);
                int operationalStateValue = getOperationalState(operationalState);
                executeCapBankCommand(cmdId, paoId, optParams, operationalStateValue);
                break;
            }
            case CBC : {
                rolePropertyDao.verifyProperty(YukonRoleProperty.ALLOW_CAPBANK_CONTROLS, user);
                executeCBCCommand(paoId, optParams);
                break;
            }
            case LTC:
            case GO_REGULATOR:
            case PO_REGULATOR: {
                rolePropertyDao.verifyProperty(YukonRoleProperty.ALLOW_SUBBUS_CONTROLS, user);
                executeCommand(paoId, cmdId);
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
    
    private void executeSubAreaCommand(int cmdId, int paoId) {
		if (cmdId == CapControlCommand.CMD_EMERGENCY_DISABLE_VERIFY){
			executeVerifyStopArea (paoId, cmdId);
		} else {
	        CapControlCommand cmd = new CapControlCommand (cmdId, paoId);
	        cmd.setUserName(getUserName());
	        capControlCache.getConnection().write(cmd);	
		}
    }
    
    private void executeSubStationCommand(int cmdId, int paoId) {
        if( cmdId == CapControlCommand.CONFIRM_SUBSTATION) {
            executeConfirmSubstation( paoId );
        } else if ((cmdId == CapControlCommand.CMD_ALL_BANKS) ||
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

    private void executeSubBusCommand(int cmdId, int paoId, long inactiveTime) {
		if (cmdId == CapControlCommand.CONFIRM_CLOSE || cmdId == CapControlCommand.CONFIRM_OPEN ) {
			executeConfirmSub( paoId );
		}
		if ((cmdId == CapControlCommand.CMD_ALL_BANKS) ||
			(cmdId == CapControlCommand.CMD_FQ_BANKS) ||
			(cmdId == CapControlCommand.CMD_FAILED_BANKS) ||
			(cmdId == CapControlCommand.CMD_QUESTIONABLE_BANKS) ||
			(cmdId == CapControlCommand.CMD_BANKS_NOT_OPERATED_IN) ||
			(cmdId == CapControlCommand.CMD_DISABLE_VERIFY)	||
			(cmdId == CapControlCommand.CMD_EMERGENCY_DISABLE_VERIFY)	||
            (cmdId == CapControlCommand.CMD_STANDALONE_VERIFY))
		{
			executeVerifySub (paoId, cmdId, inactiveTime);
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
        CCVerifySubStation msg = new CCVerifySubStation (action, paoId, strat, CCVerifySubStation.DEFAULT_CB_INACT_TIME, false);
        capControlCache.getConnection().write(msg);
    }

	private void executeVerifyStopArea(int paoId, int cmdId) {
		if (cmdId == CapControlCommand.CMD_EMERGENCY_DISABLE_VERIFY){
			int action = 2;
	        int strat = cmdId - CapControlCommand.VERIFY_OFFSET;
	        List<SubBus> subBusList = capControlCache.getSubBusesByArea(paoId);
	        Multi<CCVerifySubBus> multi = new Multi<CCVerifySubBus>();
			
	        for (final SubBus bus : subBusList) {
	        	multi.getVector().add(new CCVerifySubBus (action, bus.getCcId(), strat, CCVerifySubBus.DEFAULT_CB_INACT_TIME, false));
	        }
			capControlCache.getConnection().write(multi);
		}
	}
	
	private void executeVerifySub(int paoId, int cmdId, long inactiveTime) {
		int action = 0;
		if (cmdId == CapControlCommand.CMD_DISABLE_VERIFY)
			action = 1;
		if (cmdId == CapControlCommand.CMD_EMERGENCY_DISABLE_VERIFY)
			action = 2;
		int strat = cmdId - CapControlCommand.VERIFY_OFFSET;
		CCVerifySubBus msg = new CCVerifySubBus (action, paoId, strat, inactiveTime, false);
		capControlCache.getConnection().write(msg);
	}

	private void executeFeederCommand( int cmdId, int paoId) {
		if (cmdId == CapControlCommand.CONFIRM_CLOSE || cmdId == CapControlCommand.CONFIRM_OPEN ) {
			executeConfirmFeeder( paoId );
		}
		else{
			executeCommand( 
					paoId,
					cmdId );			
		}
	}

	public void executeCBCCommand(int paoId, float[] optParams) {
        // Build up the manual change message here, params[0] = new state ID
        CapBankDevice bank = capControlCache.getCapBankDevice(paoId);
        
        final Date now = new Date();
        
        // Send new point Here
        PointData pt = new PointData();
        pt.setId( bank.getStatusPointID().intValue() );
        pt.setPointQuality( PointQuality.Manual);
        pt.setStr("Manual change occurred using CBC Web Client");
        pt.setTime(now);
        pt.setTimeStamp(now);
        pt.setType( PointTypes.STATUS_POINT );
        pt.setUserName( getUserName() );

        // the actual new value for the selected state 
        pt.setValue( optParams[0] );

        capControlCache.getConnection().write( pt );
	    
	}
	
	private void executeCapBankCommand(int cmdId, int paoId, float[] optionalParams, int operationalState) {
	    switch (cmdId) {
	        case CapControlCommand.CONFIRM_CLOSE :
	        case CapControlCommand.CONFIRM_OPEN : {
	            executeCapBankCmdConfirm(paoId, defaultOperationalState);
                break;
	        }
	        case CapControlCommand.BANK_ENABLE_TEMPCONTROL:
	        case CapControlCommand.BANK_DISABLE_TEMPCONTROL:
            case CapControlCommand.BANK_ENABLE_VARCONTROL:
            case CapControlCommand.BANK_DISABLE_VARCONTROL:	            
            case CapControlCommand.BANK_ENABLE_TIMECONTROL:
            case CapControlCommand.BANK_DISABLE_TIMECONTROL:	        
            case CapControlCommand.CLOSE_CAPBANK :
	        case CapControlCommand.OPEN_CAPBANK :
	        case CapControlCommand.BANK_DISABLE_OVUV :
	        case CapControlCommand.BANK_ENABLE_OVUV :
	        case CapControlCommand.SCAN_2WAY_DEV : 
	        case CapControlCommand.SYNC_CBC_CAPBANK_STATE: {
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
	        case CapControlCommand.DISABLE_CAPBANK :
	        case CapControlCommand.ENABLE_CAPBANK :
	        case CapControlCommand.OPERATIONAL_STATECHANGE :
	        case CapControlCommand.RETURN_BANK_TO_FEEDER :
	        case CapControlCommand.SEND_TIMESYNC :
	        case CapControlCommand.FLIP_7010_CAPBANK :  {
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
		TempMoveCapBank msg = new TempMoveCapBank(
	                                                    (int)optionalParams[0], // original feeder ID
	                                                    (int)optionalParams[1], // new feeder ID
	                                                    paoId,
	                                                    optionalParams[2], // order of control
	                                                    optionalParams[3],
	                                                    optionalParams[4],
	                                                    optionalParams[5] > 0 ? true : false);
	    capControlCache.getConnection().write( msg );
	}
	
	private void executeCapBankCmdResetOpCount(final int paoId, final float[] optionalParams) {
        
		// Build up the reset opcount message here
        CapBankDevice bank = capControlCache.getCapBankDevice(paoId);

        final Date now = new Date();
        
        // Send new point Here
        PointData pt = new PointData();
        pt.setId( bank.getOperationAnalogPointID().intValue() );
        pt.setPointQuality( PointQuality.Manual);
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

	private void executeConfirmFeeder(int paoId) {
		Multi<CapControlCommand> multi = new Multi<CapControlCommand>();
		CapControlCommand command = new CapControlCommand (CapControlCommand.CONFIRM_FEEDER, paoId);
		command.setUserName(getUserName());
		multi.getVector().add(command);
		if (multi.getVector().size() > 0) {
			capControlCache.getConnection().write(multi);
		}	
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
        CapControlCommand command = new CapControlCommand (CapControlCommand.CONFIRM_AREA, paoId);
        command.setUserName(getUserName());
        multi.getVector().add(command);
        if (multi.getVector().size() > 0) {
            capControlCache.getConnection().write(multi);
        }
    }

    public void executeCommand(int paoId, int cmdOperation) {
        executeCommand(paoId, cmdOperation, CapControlCommandExecutor.defaultOperationalState);
    }
    
    private void executeCommand(int paoId, int cmdOperation, int operationalState) {
		CapControlCommand cmd = new CapControlCommand();
		cmd.setDeviceID(paoId);
		cmd.setCommand(cmdOperation);
		cmd.setUserName(getUserName());
		cmd.setToken(operationalState);
		
		capControlCache.getConnection().sendCommand( cmd );
	}
}
