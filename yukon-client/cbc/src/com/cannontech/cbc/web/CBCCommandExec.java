package com.cannontech.cbc.web;

import com.cannontech.database.data.point.PointQualities;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.yukon.cbc.CBCCommand;
import com.cannontech.yukon.cbc.CBCTempMoveCapBank;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;

/**
 * @author rneuharth
 *
 * Creates and executes a given CBC command
 * 
 */
public class CBCCommandExec
{
	private CapControlCache cbcCache = null;
	private String userName = null;


	/**
	 * 
	 */
	public CBCCommandExec( CapControlCache _cbcCache, String _userName )
	{
		super();
		
		cbcCache = _cbcCache;
		userName = _userName;
	}

	public boolean execute_SubCmd( int _cmdID, int _paoID )
	{
		if( _cmdID == CBCCommand.CONFIRM_CLOSE 
			 || _cmdID == CBCCommand.CONFIRM_OPEN )
		{
			_executeConfirmSub( _paoID );
		} 
		else 
		{
			_executeCommand( 
				_paoID,
				_cmdID );			
		}
	  
		return true;
	}

	public boolean execute_FeederCmd( int _cmdID, int _paoID )
	{
		_executeCommand( 
			_paoID,
			_cmdID );			

		return true;
	}


	public boolean execute_CapBankCmd( int _cmdID, int _paoID, int[] _optionalParams )
	{
		if( _cmdID == CBCCommand.CONFIRM_CLOSE 
			 || _cmdID == CBCCommand.CONFIRM_OPEN )
		{
			CapBankDevice bank = (CapBankDevice)cbcCache.getCapBankDevice( new Integer(_paoID) );
			
			if( CapBankDevice.isInAnyOpenState(bank) )
			{
				_executeCommand( bank.getControlDeviceID().intValue(), CBCCommand.CONFIRM_OPEN );
			}
			else if( CapBankDevice.isInAnyCloseState(bank) )
			{
				_executeCommand( bank.getControlDeviceID().intValue(), CBCCommand.CONFIRM_CLOSE );
			}
		}
		else if( _cmdID == CBCCommand.CLOSE_CAPBANK 
			 	 || _cmdID == CBCCommand.OPEN_CAPBANK
				 || _cmdID == CBCCommand.BANK_DISABLE_OVUV 
				 || _cmdID == CBCCommand.BANK_ENABLE_OVUV )
		{
			CapBankDevice bank = (CapBankDevice)cbcCache.getCapBankDevice( new Integer(_paoID) );
			
			_executeCommand( 
				bank.getControlDeviceID().intValue(),
				_cmdID );			
		}
		else if( _cmdID == CBCCommand.CMD_MANUAL_ENTRY
					&& _optionalParams.length >= 1 )
		{
			// Build up the manaual change message here
			//   params[0] = new state ID
			CapBankDevice bank = (CapBankDevice)cbcCache.getCapBankDevice( new Integer(_paoID) );
			
			// Send new point Here
			PointData pt = new PointData();

			pt.setId( bank.getStatusPointID().intValue() );
			pt.setQuality( PointQualities.MANUAL_QUALITY );
			pt.setStr("Manual change occured using CBC Web Client");
			pt.setTime( new java.util.Date() );
			pt.setTimeStamp( new java.util.Date() );
			pt.setType( PointTypes.STATUS_POINT );
			pt.setUserName( _getUserName() );

			//the actual new value for the selected state 
			pt.setValue( (double)_optionalParams[0] );

			cbcCache.getConnection().write( pt );
		}
		else if( _cmdID == CBCCommand.CMD_BANK_TEMP_MOVE
				 && _optionalParams.length >= 3 )
		{
			// Build up  the move message here
			//   params[0] = original feeder ID
			//   params[1] = new feeder ID
			//   params[2] = order of control
			CBCTempMoveCapBank msg = new CBCTempMoveCapBank(
				_optionalParams[0],
				_optionalParams[1],
				_paoID,
				_optionalParams[2],
				false );
			
			cbcCache.getConnection().write( msg );
		}
		else
		{
			//be sure we are not sending any commands that would not matter
			// to the capbank
			if( _isValidBankCmd(_paoID, _cmdID) )
				_executeCommand( 
					_paoID,
					_cmdID );
		}
							

		return true;
	}
	
	private boolean _isValidBankCmd( int _paoID, int _cmdID )
	{
		CapBankDevice bank =
			(CapBankDevice)cbcCache.getCapBankDevice( new Integer(_paoID) );

		switch( _cmdID )
		{
			case CBCCommand.RETURN_BANK_TO_FEEDER:
				return bank.isBankMoved();			

			case CBCCommand.ENABLE_CAPBANK:
				return bank.getCcDisableFlag().booleanValue();

			case CBCCommand.DISABLE_CAPBANK:
				return !bank.getCcDisableFlag().booleanValue();
		}		

		return true;		
	}

	private String _getUserName()
	{
		return userName;
	}

	private void _executeConfirmSub( int _paoID )
	{
		Multi multi = new Multi();
      
		SubBus sub = cbcCache.getSubBus( new Integer(_paoID) );
      
		for( int i = 0; i < sub.getCcFeeders().size(); i++ )
		{
			Feeder feeder = (Feeder)sub.getCcFeeders().get(i);
	
			//do not confirm disabled feeders
			if( feeder.getCcDisableFlag().booleanValue() )
				continue;

   			
			for( int j = 0; j < feeder.getCcCapBanks().size(); j++ )
			{
				CapBankDevice bank = (CapBankDevice)feeder.getCcCapBanks().get(j);

				//do not confirm disabled banks
				if( bank.getCcDisableFlag().booleanValue() )
				{
					continue;
				}
				else if( CapBankDevice.isInAnyCloseState(bank) )
				{
					multi.getVector().add( new CBCCommand(
								CBCCommand.CONFIRM_CLOSE, 
								 bank.getControlDeviceID().intValue()) );
				}
				else if( CapBankDevice.isInAnyOpenState(bank) )
				{
					multi.getVector().add( new CBCCommand(
								CBCCommand.CONFIRM_OPEN, 
								 bank.getControlDeviceID().intValue()) );
				}
			}			
		}
	
		if( multi.getVector().size() > 0 )
			cbcCache.getConnection().write( multi );		
	}


	/**
	 * Used to send a command to the CBC server.
	 * @param int, int
	 */
	private void _executeCommand(int _paoID, int _cmdOperation )
	{
		CBCCommand cmd = new CBCCommand();
		cmd.setDeviceID( _paoID );
		cmd.setCommand( _cmdOperation );
		cmd.setUserName( _getUserName() );
		
		cbcCache.getConnection().sendCommand( cmd );
	}

}
