package com.cannontech.systray;

import javax.swing.*;

import com.cannontech.alarms.gui.AlarmHandler;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.version.VersionTools;

import java.util.Vector;

import snoozesoft.systray4j.*;

/**
 * The main application that brings up the sys tray icon for Yukon. All components and icons
 * used are created/found in here. 
 */
public class YukonSysTray implements SysTrayMenuListener
{
	private AlarmHandler alarmHandler = null;
	private Thread iconCyclerThrd = null;
	

	private final SysTrayMenu yukonSysTray = 
			new SysTrayMenu( ALL_ICONS[0], "Starting up Yukon monitor..." );

	// create icons
	static final SysTrayMenuIcon[] ALL_ICONS = 
	{
		// the extension can be omitted
		new SysTrayMenuIcon("d:/systray/systray4j/java/icons/duke"),
		new SysTrayMenuIcon("d:/systray/systray4j/java/icons/duke_up")
	};



	private SysTrayMenuItem menuItemExit = null;
	private SysTrayMenuItem menuItemAbout = null;

	private SysTrayMenuItem menuItemLogin = null;

	
	private SysTrayMenuItem menuItemTDC = null;
	private SysTrayMenuItem menuItemDBEditor = null;
	private SysTrayMenuItem menuItemTrending = null;
	private SysTrayMenuItem menuItemCommander = null;

	private CheckableMenuItem chkMnuItemSilence = null;
	private SysTrayMenuItem menuItemProperties = null;


	public YukonSysTray()
	{
		super();

		// don´t forget to assign listeners to the icons
		for (int i = 0; i < ALL_ICONS.length; i++)
			ALL_ICONS[i].addSysTrayMenuListener(this);

		// create the menu that is for the systray icon
		initComponents();
	}

	public synchronized void startCycleImages()
	{
		if( iconCyclerThrd == null )
		{
			Runnable r = new Runnable()
			{
				public void run()
				{
					try
					{
						while( true )
						{
							for( int i = 0; i < ALL_ICONS.length; i++ )
							{
								yukonSysTray.setIcon( ALL_ICONS[i] );
								
								Thread.currentThread().sleep(1000);
							}

						}
					}
					catch( Exception e ) {}
					finally
					{
						yukonSysTray.setIcon( ALL_ICONS[0] );
						iconCyclerThrd = null;
					}

				}

			};


			iconCyclerThrd = new Thread(r, "IconCycler" );
			iconCyclerThrd.start();
		}
	}

	public void setTrayText( String str_ )
	{
		yukonSysTray.setToolTip( str_ );		
	}


	public synchronized void stopCycleImages()
	{
		if( iconCyclerThrd != null )
			iconCyclerThrd.interrupt();
	}

	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{}

		new YukonSysTray();
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
			getAlarmHandler().getAlarmClient().stop();

			System.exit(0);
		}
		else if( e.getSource() == getMenuItemAbout() )
		{
			JOptionPane.showMessageDialog(
				null,
				"Yukon Systray Alerter  (Version : " + VersionTools.getYUKON_VERSION() + ")" );
		}
		else if( e.getSource() == getMenuItemSilence() )
		{
			//TODO:do the silence action here
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
					"cmd /C java -jar d:/yukon/client/bin/tdc.jar"); // >> d:/a.txt");
			}
			catch( Exception ex )
			{
				CTILogger.error( "Unable to start TDC application", ex );
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
		JOptionPane.showMessageDialog(
			null,
			"You may prefer double-clicking the icon.");
	}

	private void initComponents()
	{
		CTILogger.info("Creating systray items...");

		Vector cntrls = new Vector();
		cntrls.add( getMenuItemSilence() );
		cntrls.add( getMenuItemProperties() );

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
		yukonSysTray.addSeparator();
		yukonSysTray.addItem( getMenuItemAbout() );
		yukonSysTray.addSeparator();
		yukonSysTray.addItem( cntrlSubMenu );
		yukonSysTray.addItem( appSubMenu );		
		yukonSysTray.addItem( getMenuItemLogin() ); //top component
		
		
		initConnections();
		

		//start trying to connect for alarms immediately
		getAlarmHandler();		
	}

	private void initConnections()
	{
		getMenuItemExit().addSysTrayMenuListener(this);
		getMenuItemAbout().addSysTrayMenuListener(this);
		getMenuItemLogin().addSysTrayMenuListener(this);
		
		getMenuItemTDC().addSysTrayMenuListener(this);
		getMenuItemDBEditor().addSysTrayMenuListener(this);
		getMenuItemTrending().addSysTrayMenuListener(this);
		getMenuItemCommander().addSysTrayMenuListener(this);
		
		getMenuItemSilence().addSysTrayMenuListener(this);
		getMenuItemProperties().addSysTrayMenuListener(this);
	}

	private CheckableMenuItem getMenuItemSilence()
	{
		if( chkMnuItemSilence == null )
		{
			chkMnuItemSilence = new CheckableMenuItem("Silence Alarms", "silence");
			chkMnuItemSilence.setState(false);
		}
		
		return chkMnuItemSilence;
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

	private SysTrayMenuItem getMenuItemLogin()
	{
		if( menuItemLogin == null )
		{
			menuItemLogin = new SysTrayMenuItem("Login...", "login");
		}
		
		return menuItemLogin;
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
