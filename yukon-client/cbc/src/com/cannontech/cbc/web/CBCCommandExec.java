/*
 * Created on May 19, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.cbc.web;

import com.cannontech.cbc.data.CapBankDevice;
import com.cannontech.cbc.data.Feeder;
import com.cannontech.cbc.data.SubBus;
import com.cannontech.cbc.messages.CBCCommand;
import com.cannontech.database.data.point.PointQualities;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.PointData;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CBCCommandExec
{
	private CapControlWebAnnex cbcCache = null;


	/**
	 * 
	 */
	public CBCCommandExec( CapControlWebAnnex cbcCache_ )
	{
		super();
		
		cbcCache = cbcCache_;
	}

	public boolean execute_SubCmd( int cmdID_, int paoID_ )
	{
		if( cmdID_ == CBCCommand.CONFIRM_CLOSE 
			 || cmdID_ == CBCCommand.CONFIRM_OPEN )
		{
			executeConfirmSub( paoID_ );
		} 
		else 
		{
			executeCommand( 
				paoID_,
				cmdID_ );			
		}
	  
		return true;
	}

	public boolean execute_FeederCmd( int cmdID_, int paoID_ )
	{
		executeCommand( 
			paoID_,
			cmdID_ );			

		return true;
	}


	public boolean execute_CapBankCmd( int cmdID_, int paoID_, Integer manChange_ )
	{
		if( cmdID_ == CBCCommand.CONFIRM_CLOSE 
			 || cmdID_ == CBCCommand.CONFIRM_OPEN )
		{
			CapBankDevice bank = (CapBankDevice)cbcCache.getCapBankTableModel().getCapbank( paoID_ );
			
			if( CapBankDevice.isInAnyOpenState(bank) )
			{
				executeCommand( bank.getControlDeviceID().intValue(), CBCCommand.CONFIRM_OPEN );
			}
			else if( CapBankDevice.isInAnyCloseState(bank) )
			{
				executeCommand( bank.getControlDeviceID().intValue(), CBCCommand.CONFIRM_CLOSE );
			}
		}
		else if( cmdID_ == CBCCommand.CLOSE_CAPBANK 
			 	 || cmdID_ == CBCCommand.OPEN_CAPBANK )
		{
			CapBankDevice bank = (CapBankDevice)cbcCache.getCapBankTableModel().getCapbank( paoID_ );
			
			executeCommand( 
				bank.getControlDeviceID().intValue(),
				cmdID_ );			
		}
		else if( cmdID_ == CBCCommand.CMD_MANUAL_ENTRY )
		{
			CapBankDevice bank = (CapBankDevice)cbcCache.getCapBankTableModel().getCapbank( paoID_ );
			
			// Send new point Here
			PointData pt = new PointData();

			pt.setId( bank.getStatusPointID().intValue() );
			pt.setQuality( PointQualities.MANUAL_QUALITY );
			pt.setStr("Manual change occured using CBC Web Client");
			pt.setTime( new java.util.Date() );
			pt.setTimeStamp( new java.util.Date() );
			pt.setType( PointTypes.STATUS_POINT );
			pt.setUserName( cbcCache.getYukonUser().getUsername() );

			//the actual new value for the selected state 
			pt.setValue( (double)manChange_.intValue() );

			cbcCache.getConnection().write( pt );
		}
		else
			executeCommand( 
				paoID_,
				cmdID_ );			

		return true;
	}

	private void executeConfirmSub( int paoID_ )
	{
		Multi multi = new Multi();
      
		SubBus sub = cbcCache.getSubTableModel().getSubBus( paoID_ );
      
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
	private void executeCommand(int paoID_, int cmdOperation_ )
	{
		CBCCommand cmd = new CBCCommand();
		cmd.setDeviceID( paoID_ );
		cmd.setCommand( cmdOperation_ );
		cmd.setUserName( cbcCache.getYukonUser().getUsername() );
		
		cbcCache.getConnection().sendCommand( cmd );
	}

}
