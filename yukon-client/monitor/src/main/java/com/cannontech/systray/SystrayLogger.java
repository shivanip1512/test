package com.cannontech.systray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;

/**
 * @author rneuharth
 *
 * Acts as a way to log the console output for each systray application
 * 
 */
public class SystrayLogger extends Thread
{
	private Logger sysLogger = null;
	private Process proc = null;


	/**
	 * 
	 */
	public SystrayLogger( Process p, String app_ )
	{
		super();
		setName(app_);
		
		try
		{
			sysLogger = Logger.getLogger( getName() );
			
			synchronized( sysLogger )
			{
				if( sysLogger.getAppender(getName()) == null )
				{
					RollingFileAppender rfa = new RollingFileAppender(
							new PatternLayout(),   //no layout needed
							CtiUtilities.getClientLogDir() +
							"systray_"+ getName() + ".log",
							true); //append to the file
					
					rfa.setName( getName() );
					rfa.setMaxBackupIndex(3); //number of backups
					rfa.setMaxFileSize("6MB"); //how large should each backup be
					
					sysLogger.addAppender( rfa );
				}
			}

		}
		catch( IOException e )
		{}
		

		proc = p;		
	}


	public void run()
	{
		try
		{
			InputStreamReader isr = new InputStreamReader(proc.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			
			String line = null;

			sysLogger.info( getProcID() +
					"------------------------ <SYSTRAY Logging Started for " + getName() + "> -------------------------" );

			while ( (line = br.readLine()) != null )
				sysLogger.info( getProcID() + line );

			sysLogger.info( getProcID() +		
					"------------------------ <SYSTRAY Logging Finished for " + getName() + "> ------------------------" );

			sysLogger.info(getProcID() + "");
			
			
		}
		catch( Exception e )
		{
			//maybe not needed
			CTILogger.warn( "Spawned process died for procid = " + getProcID(), e );
		}

	}

	private String getProcID()
	{
		return "0x" + Integer.toHexString(proc.hashCode()) + " : ";		
	}
	
}
