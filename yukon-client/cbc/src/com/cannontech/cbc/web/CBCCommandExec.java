package com.cannontech.cbc.web;

import com.cannontech.database.data.point.PointQualities;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.yukon.cbc.CBCCommand;
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
	public CBCCommandExec( CapControlCache cbcCache_, String userName_ )
	{
		super();
		
		cbcCache = cbcCache_;
		userName = userName_;
	}

	public boolean execute_SubCmd( int cmdID_, int paoID_ )
	{
		if( cmdID_ == CBCCommand.CONFIRM_CLOSE 
			 || cmdID_ == CBCCommand.CONFIRM_OPEN )
		{
			_executeConfirmSub( paoID_ );
		} 
		else 
		{
			_executeCommand( 
				paoID_,
				cmdID_ );			
		}
	  
		return true;
	}

	public boolean execute_FeederCmd( int cmdID_, int paoID_ )
	{
		_executeCommand( 
			paoID_,
			cmdID_ );			

		return true;
	}


	public boolean execute_CapBankCmd( int cmdID_, int paoID_, int manChange_ )
	{
		if( cmdID_ == CBCCommand.CONFIRM_CLOSE 
			 || cmdID_ == CBCCommand.CONFIRM_OPEN )
		{
			CapBankDevice bank = (CapBankDevice)cbcCache.getCapBankDevice( new Integer(paoID_) );
			
			if( CapBankDevice.isInAnyOpenState(bank) )
			{
				_executeCommand( bank.getControlDeviceID().intValue(), CBCCommand.CONFIRM_OPEN );
			}
			else if( CapBankDevice.isInAnyCloseState(bank) )
			{
				_executeCommand( bank.getControlDeviceID().intValue(), CBCCommand.CONFIRM_CLOSE );
			}
		}
		else if( cmdID_ == CBCCommand.CLOSE_CAPBANK 
			 	 || cmdID_ == CBCCommand.OPEN_CAPBANK
				 || cmdID_ == CBCCommand.BANK_DISABLE_OVUV 
				 || cmdID_ == CBCCommand.BANK_ENABLE_OVUV )
		{
			CapBankDevice bank = (CapBankDevice)cbcCache.getCapBankDevice( new Integer(paoID_) );
			
			_executeCommand( 
				bank.getControlDeviceID().intValue(),
				cmdID_ );			
		}
		else if( cmdID_ == CBCCommand.CMD_MANUAL_ENTRY )
		{
			CapBankDevice bank = (CapBankDevice)cbcCache.getCapBankDevice( new Integer(paoID_) );
			
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
			pt.setValue( (double)manChange_ );

			cbcCache.getConnection().write( pt );
		}
		else
			_executeCommand( 
				paoID_,
				cmdID_ );			

		return true;
	}
	
	private String _getUserName()
	{
		return userName;
	}

	private void _executeConfirmSub( int paoID_ )
	{
		Multi multi = new Multi();
      
		SubBus sub = cbcCache.getSubBus( new Integer(paoID_) );
      
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
	private void _executeCommand(int paoID_, int cmdOperation_ )
	{
		CBCCommand cmd = new CBCCommand();
		cmd.setDeviceID( paoID_ );
		cmd.setCommand( cmdOperation_ );
		cmd.setUserName( _getUserName() );
		
		cbcCache.getConnection().sendCommand( cmd );
	}

}
