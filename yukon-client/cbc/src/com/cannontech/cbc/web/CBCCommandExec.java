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
import com.cannontech.clientutils.CTILogger;
import com.cannontech.message.dispatch.message.Multi;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
class CBCCommandExec
{
	private CapControlWebAnnex cbcCache = null;


	/**
	 * 
	 */
	CBCCommandExec( CapControlWebAnnex cbcCache_ )
	{
		super();
		
		cbcCache = cbcCache_;
	}

	public boolean execute_SubCmd( int cmdID_, int rowID_ )
	{
		if( cmdID_ == CBCCommand.CONFIRM_CLOSE 
			 || cmdID_ == CBCCommand.CONFIRM_OPEN )
		{
			executeConfirmSub( rowID_ );
		} 
		else 
		{
			executeCommand( 
				cbcCache.getSubTableModel().getRowAt(rowID_).getCcId().intValue(),
				cmdID_ );			
		}
	  
		return true;
	}

	public boolean execute_FeederCmd( int cmdID_, int rowID_ )
	{
		executeCommand( 
			cbcCache.getFeederTableModel().getRowAt(rowID_).getCcId().intValue(),
			cmdID_ );			

		return true;
	}


	public boolean execute_CapBankCmd( int cmdID_, int rowID_ )
	{
		return true;
	}

	private void executeConfirmSub( int subRowID_ )
	{
		Multi multi = new Multi();
      
		SubBus sub = cbcCache.getSubTableModel().getRowAt( subRowID_ );
      
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
		try
		{
			cbcCache.getConnection().executeCommand( paoID_, cmdOperation_ );
		}
		catch( java.io.IOException ioe )
		{
			CTILogger.error( 
					"A problem occured in the execution of a CBC command with and id = " + cmdOperation_ +
					", paoID = " + paoID_, ioe );
		}
	
	}

}
