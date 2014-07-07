package com.cannontech.systray;

import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JOptionPane;

import snoozesoft.systray4j.CheckableMenuItem;
import snoozesoft.systray4j.SubMenu;
import snoozesoft.systray4j.SysTrayMenu;
import snoozesoft.systray4j.SysTrayMenuEvent;
import snoozesoft.systray4j.SysTrayMenuItem;
import snoozesoft.systray4j.SysTrayMenuListener;

import com.cannontech.alarms.gui.AlarmHandler;
import com.cannontech.clientutils.AlarmFileWatchDog;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.parametersfile.ParameterNotFoundException;
import com.cannontech.clientutils.parametersfile.ParametersFile;
import com.cannontech.common.login.ClientStartupHelper;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.version.VersionTools;

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

	private CheckableMenuItem chkMnuItemMute = null;
	private SysTrayMenuItem menuItemProperties = null;


	private final SysTrayMenu yukonSysTray = 
		new SysTrayMenu( ALL_ICONS[ICO_DISCON], ISystrayDefines.MSG_STARTING );


	public YukonSysTray()
	{
		super();

		// don't forget to assign listeners to the icons
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

        if( !iconCyclerThrd.isAlive() )
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
	        ClientStartupHelper clientStartupHelper = new ClientStartupHelper();
	        clientStartupHelper.setAppName("Yukon Systray");

	        clientStartupHelper.doStartup();
	        clientStartupHelper.hideSplash();
	        
	        new YukonSysTray();
		}
		catch (Exception e)
		{
			e.printStackTrace( System.err );
			System.exit(-1);		
		}

	}

	public AlarmHandler getAlarmHandler()
	{
		if( alarmHandler == null )
			alarmHandler = new AlarmHandler(this);		

		return alarmHandler;
	}
	
	public void menuItemSelected(SysTrayMenuEvent e)
	{
		if( e.getSource() == getMenuItemExit() )
		{
			yukonSysTray.hideIcon();

			// Exiting application
	        System.exit(0);
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
			//do the properties action here
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

		// create a submenu and insert the previously created items
		SubMenu cntrlSubMenu = new SubMenu("Controls", cntrls );

		// insert items
		yukonSysTray.addItem( getMenuItemExit() );
		yukonSysTray.addSeparator();
		yukonSysTray.addItem( getMenuItemAbout() );
		yukonSysTray.addSeparator();
		yukonSysTray.addItem( cntrlSubMenu );
		
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

	private SysTrayMenuItem getMenuItemAbout()
	{
		if( menuItemAbout == null )
		{
			menuItemAbout = new SysTrayMenuItem("About...", "about");
		}
		
		return menuItemAbout;
	}
}
