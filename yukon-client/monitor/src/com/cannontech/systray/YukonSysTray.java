package com.cannontech.systray;

import javax.swing.*;

import com.cannontech.alarms.gui.AlarmHandler;
import com.cannontech.clientutils.AlarmFileWatchDog;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.parametersfile.ParameterNotFoundException;
import com.cannontech.clientutils.parametersfile.ParametersFile;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.version.VersionTools;

import java.awt.event.ActionListener;
import java.util.Vector;

import snoozesoft.systray4j.*;

/**
 * The main application that brings up the sys tray icon for Yukon. All components and icons
 * used are created/found in here. 
 */
public class YukonSysTray implements SysTrayMenuListener, ActionListener, ISystrayDefines
{	
	private AlarmHandler alarmHandler = null;
	private Thread iconCyclerThrd = null;
	private SystrayFlasher flasher = null;

	private SysTrayMenuItem menuItemExit = null;
	private SysTrayMenuItem menuItemAbout = null;

	private SysTrayMenuItem menuItemLogout = null;

	
	private SysTrayMenuItem menuItemTDC = null;
	private SysTrayMenuItem menuItemDBEditor = null;
	private SysTrayMenuItem menuItemTrending = null;
	private SysTrayMenuItem menuItemCommander = null;

	private CheckableMenuItem chkMnuItemMute = null;
	private SysTrayMenuItem menuItemProperties = null;


	private final SysTrayMenu yukonSysTray = 
		new SysTrayMenu( ALL_ICONS[ICO_DISCON], ISystrayDefines.MSG_STARTING );


	public YukonSysTray()
	{
		super();

		// don´t forget to assign listeners to the icons
		for (int i = 0; i < ALL_ICONS.length; i++)
			ALL_ICONS[i].addSysTrayMenuListener(this);

		// create the menu that is for the systray icon
		initComponents();
	}
	
	private SystrayFlasher getSystrayFlasher()
	{
		if( flasher == null )
		{
			flasher = new SystrayFlasher( yukonSysTray );			
					
			getSystrayFlasher().setMuted( getMenuItemMute().getState() );
		}
					
		return flasher;
	}
	
	public synchronized void startCycleImages()
	{
		if( iconCyclerThrd == null
			 || iconCyclerThrd.isInterrupted() )
		{
			iconCyclerThrd = new Thread(
					Thread.currentThread().getThreadGroup(),
					getSystrayFlasher(), 
					"IconCycler" );

			iconCyclerThrd.setDaemon( true );
		}

		iconCyclerThrd.start();
	}

	public void actionPerformed(java.awt.event.ActionEvent e) 
	{
		if( e.getSource() == AlarmFileWatchDog.getInstance() )
		{
			try
			{
				ParametersFile pf = new ParametersFile( CtiUtilities.OUTPUT_FILE_NAME );
				boolean isMuted = 
					Boolean.valueOf(pf.getParameterValue("Mute", "false")).booleanValue();
					
				getMenuItemMute().setState( isMuted );
					
				getSystrayFlasher().setMuted( isMuted );
			}
			catch( ParameterNotFoundException ex)
			{}			

		}

	}
	
	/** 
	 * This method sets the tooltip flyover text AND it sets the corresponding
	 * icon to use.
	 * 
	 * @param str_
	 */
	public synchronized void setTrayText( String str_ )
	{
		yukonSysTray.setToolTip( str_ );
		
		//do not let this guy in my house!
		if( str_ == null )
			return;

		if( ISystrayDefines.MSG_NOT_CONN.equalsIgnoreCase(str_) 
			 || ISystrayDefines.MSG_STARTING.equalsIgnoreCase(str_) )
		{
			stopCycleImages();
			yukonSysTray.setIcon( ALL_ICONS[ICO_DISCON] );
		}
		else if( ISystrayDefines.MSG_STARTING.equalsIgnoreCase(str_) )
		{
			stopCycleImages();
			yukonSysTray.setIcon( ALL_ICONS[ICO_DISCON] );
		}
		else if( str_.indexOf(ISystrayDefines.MSG_ALRM_TOTALS) >=0 )
		{
			yukonSysTray.setIcon( ALL_ICONS[ICO_NO_ALRM] );
		}

	}


	public synchronized void stopCycleImages()
	{
		if( iconCyclerThrd != null )
			iconCyclerThrd.interrupt();

		iconCyclerThrd = null;
	}

	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{}


		System.setProperty("cti.app.name", "Yukon Systray");


		ClientSession session = ClientSession.getInstance(); 
		if( session.establishSession() )
		{
			//System.exit(-1);
			new YukonSysTray();
		}

		//exits gracefully if we are not logged in!
	}

	public AlarmHandler getAlarmHandler()
	{
		if( alarmHandler == null )
			alarmHandler = new AlarmHandler(this);		

		return alarmHandler;
	}
	
	private void exitApp()
	{
		System.exit(0);
	}


	public void menuItemSelected(SysTrayMenuEvent e)
	{
		if( e.getSource() == getMenuItemExit()
			 || e.getSource() == getMenuItemLogout() )
		{
			yukonSysTray.hideIcon();
			getAlarmHandler().getAlarmClient().stop();

			if( e.getSource() == getMenuItemLogout() )
			{
				if( ClientSession.getInstance().establishSession() )
					ClientSession.getInstance().closeSession();
			}


			exitApp();
		}
		else if( e.getSource() == getMenuItemAbout() )
		{
			JOptionPane.showMessageDialog(
				null,
				"Yukon Systray Alerter  (Version : " + VersionTools.getYUKON_VERSION() + ")" );
		}
		else if( e.getSource() == getMenuItemMute() )
		{
			try
			{			
				ParametersFile pf = new ParametersFile( CtiUtilities.OUTPUT_FILE_NAME );
				pf.updateValues( 
					new String[] {"Mute"},
					new String[] {String.valueOf(getMenuItemMute().getState())} );
			}
			catch( ParameterNotFoundException ex )
			{}			
			
		}
		else if( e.getSource() == getMenuItemProperties() )
		{
			//TODO:do the properties action here
		}
		else if( e.getSource() == getMenuItemTDC() )
		{
			try
			{
				Process p = Runtime.getRuntime().exec(
					ISystrayDefines.EXEC_TDC );

				//start logging the stuff
				new SystrayLogger(p, "TDC").start();
			}
			catch( Exception ex )
			{
				CTILogger.error( "Unable to start TDC application", ex );
			}
		}
		else if( e.getSource() == getMenuItemDBEditor() )
		{
			try
			{
				Process p = Runtime.getRuntime().exec(
					ISystrayDefines.EXEC_DBEDITOR );

				//start logging the stuff
				new SystrayLogger(p, "DBEditor").start();
			}
			catch( Exception ex )
			{
				CTILogger.error( "Unable to start DBEditor application", ex );
			}
		}
		else if( e.getSource() == getMenuItemCommander() )
		{
			try
			{
				Process p = Runtime.getRuntime().exec(
					ISystrayDefines.EXEC_COMMANDER );

				//start logging the stuff
				new SystrayLogger(p, "Commander").start();
			}
			catch( Exception ex )
			{
				CTILogger.error( "Unable to start Commander application", ex );
			}
		}
		else if( e.getSource() == getMenuItemTrending() )
		{
			try
			{
				Process p = Runtime.getRuntime().exec(
					ISystrayDefines.EXEC_TRENDING );

				//start logging the stuff
				new SystrayLogger(p, "Trending").start();
			}
			catch( Exception ex )
			{
				CTILogger.error( "Unable to start Trending application", ex );
			}
		}

	}

	public void iconLeftClicked(SysTrayMenuEvent e)
	{
		//bring up TDC when this happens
		//if( isVisible() ) hide();
		//else show();		
	}

	public void iconLeftDoubleClicked(SysTrayMenuEvent e)
	{
//		JOptionPane.showMessageDialog(
//			null,
//			"You may prefer double-clicking the icon.");
	}

	private void initComponents()
	{
		CTILogger.info("Creating systray items...");

		Vector cntrls = new Vector();
		cntrls.add( getMenuItemMute() );
		//cntrls.add( getMenuItemProperties() );

		Vector apps = new Vector();
		apps.add( getMenuItemTDC() );
		apps.add( getMenuItemDBEditor() );
		apps.add( getMenuItemTrending() );
		apps.add( getMenuItemCommander() );

		// create a submenu and insert the previously created items
		SubMenu appSubMenu = new SubMenu("Applications", apps);
		SubMenu cntrlSubMenu = new SubMenu("Controls", cntrls );


		// insert items
		yukonSysTray.addItem( getMenuItemExit() );
		//yukonSysTray.addItem( getMenuItemLogout() );
		yukonSysTray.addSeparator();
		yukonSysTray.addItem( getMenuItemAbout() );
		yukonSysTray.addSeparator();
		yukonSysTray.addItem( cntrlSubMenu );
		yukonSysTray.addItem( appSubMenu );  //top component
		
		
		initConnections();
		

		//start trying to connect for alarms immediately
		getAlarmHandler();		
	}

	private void initConnections()
	{
		//watch a file for changes
		AlarmFileWatchDog.getInstance().addActionListener( this );

		getMenuItemExit().addSysTrayMenuListener(this);
		getMenuItemAbout().addSysTrayMenuListener(this);
		getMenuItemLogout().addSysTrayMenuListener(this);
		
		getMenuItemTDC().addSysTrayMenuListener(this);
		getMenuItemDBEditor().addSysTrayMenuListener(this);
		getMenuItemTrending().addSysTrayMenuListener(this);
		getMenuItemCommander().addSysTrayMenuListener(this);
		
		getMenuItemMute().addSysTrayMenuListener(this);
		getMenuItemProperties().addSysTrayMenuListener(this);
	}

	private CheckableMenuItem getMenuItemMute()
	{
		if( chkMnuItemMute == null )
		{
			chkMnuItemMute = new CheckableMenuItem("Mute Alarms", "mute");

			//get the state of this item from a param file
			try
			{
				ParametersFile pf = new ParametersFile( CtiUtilities.OUTPUT_FILE_NAME );
				chkMnuItemMute.setState(
					Boolean.valueOf(pf.getParameterValue("Mute", "false")).booleanValue() );
			}
			catch( ParameterNotFoundException e )
			{}			
			
		}
		
		return chkMnuItemMute;
	}

	private SysTrayMenuItem getMenuItemProperties()
	{
		if( menuItemProperties == null )
		{
			menuItemProperties = new SysTrayMenuItem("Properties...", "properties");
		}
		
		return menuItemProperties;
	}

	private SysTrayMenuItem getMenuItemExit()
	{
		if( menuItemExit == null )
		{
			menuItemExit = new SysTrayMenuItem("Exit", "exit");
		}
		
		return menuItemExit;
	}

	private SysTrayMenuItem getMenuItemLogout()
	{
		if( menuItemLogout == null )
		{
			menuItemLogout = new SysTrayMenuItem("Logout", "logout");
		}
		
		return menuItemLogout;
	}

	private SysTrayMenuItem getMenuItemAbout()
	{
		if( menuItemAbout == null )
		{
			menuItemAbout = new SysTrayMenuItem("About...", "about");
		}
		
		return menuItemAbout;
	}

	private SysTrayMenuItem getMenuItemDBEditor()
	{
		if( menuItemDBEditor == null )
		{
			menuItemDBEditor = new SysTrayMenuItem("DBEditor", "dbeditor");
		}
		
		return menuItemDBEditor;
	}

	private SysTrayMenuItem getMenuItemTrending()
	{
		if( menuItemTrending == null )
		{
			menuItemTrending = new SysTrayMenuItem("Trending", "trending");
		}
		
		return menuItemTrending;
	}

	private SysTrayMenuItem getMenuItemTDC()
	{
		if( menuItemTDC == null )
		{
			menuItemTDC = new SysTrayMenuItem("TDC", "TDC");
		}
		
		return menuItemTDC;
	}
	
	private SysTrayMenuItem getMenuItemCommander()
	{
		if( menuItemCommander == null )
		{
			menuItemCommander = new SysTrayMenuItem("Commander", "commander");
		}
		
		return menuItemCommander;
	}


}
