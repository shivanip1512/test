package com.cannontech.systray;

import snoozesoft.systray4j.SysTrayMenu;

import com.cannontech.common.util.CtiUtilities;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SystrayFlasher implements Runnable
{
	private boolean muted = false;

	private java.applet.AudioClip alarmSound = null;

	private SysTrayMenu yukonSysTray = null;
	
	/**
	 * 
	 */
	public SystrayFlasher( SysTrayMenu sysTray_ )
	{
		super();
		
		yukonSysTray = sysTray_;
	}

	public void run()
	{
		try
		{
			while( true )
			{
				for( int i = ISystrayDefines.ICO_ANIME_START; i < ISystrayDefines.ALL_ICONS.length; i++ )
				{
					yukonSysTray.setIcon( ISystrayDefines.ALL_ICONS[i] );
								
					//we cant be muted, only play the sound once
					if( i == ISystrayDefines.ICO_ANIME_START && !isMuted() )
						getAlarmSound().play();
								
					Thread.currentThread().sleep(1300);
				}

			}
		}
		catch( Exception e ) {}
		finally
		{
			yukonSysTray.setIcon( ISystrayDefines.ALL_ICONS[ISystrayDefines.ICO_NO_ALRM] );

			//interrupt ourself
//			if( !Thread.currentThread().isInterrupted() )
//				Thread.currentThread().interrupt();
		}

	}



	/**
	 * Insert the method's description here.
	 * Creation date: (4/4/00 5:01:45 PM)
	 * Version: <version>
	 */
	private java.applet.AudioClip getAlarmSound() 
	{	
		if( alarmSound == null )
		{
			alarmSound = java.applet.Applet.newAudioClip( CtiUtilities.ALARM_AU );

		}
	
		return alarmSound;
	}

	/**
	 * @return
	 */
	public boolean isMuted()
	{
		return muted;
	}

	/**
	 * @param b
	 */
	public void setMuted(boolean b)
	{
		muted = b;
	}

}
