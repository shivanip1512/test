package com.cannontech.clientutils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import org.apache.log4j.helpers.FileWatchdog;

import com.cannontech.common.util.CtiUtilities;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AlarmFileWatchDog extends FileWatchdog
{
	private static AlarmFileWatchDog singleton = null;


	private Vector actListeners = null;

	/**
	 * 
	 */
	private AlarmFileWatchDog()
	{
		super( CtiUtilities.OUTPUT_FILE_NAME );
		
		setDelay( 2000 ); //2 seconds should be good
		
		setName( CtiUtilities.OUTPUT_FILE_NAME ); //thread name
	}

	public static synchronized AlarmFileWatchDog getInstance()
	{
		if( singleton == null )
		{
			singleton = new AlarmFileWatchDog();
			singleton.start();
		}

		return singleton;
	}

	protected Vector getActListeners()
	{
		if( actListeners == null )
			actListeners = new Vector(16);
		
		return actListeners;
	}

	public synchronized void doOnChange()
	{
		for( int i = getActListeners().size()-1; i >= 0; i-- )
		{
			((ActionListener) getActListeners().get(i)).actionPerformed(
				new ActionEvent(
						this,
						0,
						"FileWatchChange") );
		}		
	}

	public synchronized void addActionListener( ActionListener list_ )
	{
		getActListeners().add( list_ );
	}

	public synchronized void removeActionListener( ActionListener list_ )
	{
		getActListeners().remove( list_ );
	}

}
