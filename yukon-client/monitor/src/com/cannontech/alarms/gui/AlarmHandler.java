package com.cannontech.alarms.gui;

import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.systray.ISystrayDefines;
import com.cannontech.systray.YukonSysTray;

/**
 * @author rneuharth
 *
 * Listens to Alarm changes in the alarm connection. Changes the state of
 * the systray by the alarms/Signals it receives.
 */
public class AlarmHandler implements Observer, MessageListener
{
	private AlarmClient alarmClient = null;
	private YukonSysTray yukonSysTray = null;

	//contains Signal instances
	private Vector signalVector = null;

	/**
	 * 
	 */
	public AlarmHandler( YukonSysTray sysTrya_ )
	{
		super();
		
		if( sysTrya_ == null )
			throw new IllegalArgumentException("Constructor param can not be null");

		yukonSysTray = sysTrya_;

		//start trying to connect immediately
		getAlarmClient().startConnection();
	}


	public AlarmClient getAlarmClient()
	{
		if( alarmClient == null )
		{
			alarmClient = new AlarmClient(this);
			alarmClient.addMessageListener( this );
		}		

		return alarmClient;
	}

	public Vector getSignalVector()
	{
		if( signalVector == null )
			signalVector = new Vector(32);

		return signalVector;
	}

	public synchronized void handleSignal( Signal sig )
	{

		boolean foundSig = false;
		int prevAlrmCnt = getSignalVector().size();//alarmCount;
		boolean addAlarm = TagUtils.isAlarmUnacked(sig.getTags());

		for( int i = 0; i < getSignalVector().size(); i++ )
		{
			Signal storedSig = (Signal)getSignalVector().get(i);

			if( storedSig.equals(sig) ) //update the sig value
			{
				if( addAlarm )  //update the underlying signal
				{
					getSignalVector().setElementAt( sig, i );
				}
				else  //remove the alarm
				{
					getSignalVector().removeElementAt( i );
				}

				foundSig = true;
				break;
			}
		}

		//we may need to add this new signal to the list
		if( !foundSig && addAlarm )
			getSignalVector().add( sig );

		//set the color of our item
		if( getSignalVector().size() <= 0 )
		{
			//stop waving the Duke
			yukonSysTray.stopCycleImages();
		}
		else
		{
			//wave the Duke
			yukonSysTray.startCycleImages();
		}


		//update the text
		yukonSysTray.setTrayText( ISystrayDefines.MSG_ALRM_TOTALS + getSignalVector().size() );
	}
	
	
	public void update( Observable src, Object val )
	{
		if( val instanceof ClientConnection )
		{
			if( !getAlarmClient().connected() )
				yukonSysTray.setTrayText( ISystrayDefines.MSG_NOT_CONN );
			else
				//alarmMenu.setToolTip( "Connected to Server" );
				yukonSysTray.setTrayText( ISystrayDefines.MSG_ALRM_TOTALS +  
							getSignalVector().size() );
		}		
	}
	
	public void messageReceived( MessageEvent e )
	{
		Message in = e.getMessage();

		if( in instanceof Signal )
		{
			handleSignal( (Signal)in );
			
			yukonSysTray.setTrayText( ISystrayDefines.MSG_ALRM_TOTALS +  
						getSignalVector().size() );			
		}

	}
	
}
