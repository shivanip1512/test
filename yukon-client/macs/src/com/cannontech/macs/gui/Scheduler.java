package com.cannontech.macs.gui;

/**
 * This type was created in VisualAge.
 */

import java.awt.event.ActionEvent;

import javax.swing.JRootPane;

import com.cannontech.message.macs.message.MACSCategoryChange;
import com.cannontech.yukon.IMACSConnection;
import com.cannontech.yukon.concrete.ResourceFactory;

public class Scheduler implements java.awt.event.ActionListener, com.cannontech.tdc.SpecialTDCChild, java.util.Observer
{
	//an int read in as a CParm used to turn on/off features
	private static int userRightsInt = 0;
	
	private ScheduleActionListener scheduleActionListener= null;
	private javax.swing.JComboBox comboBox = null;
	public static final String MACS_NAME = "MACS Scheduler";
	public static final String MACS_VERSION = 
			com.cannontech.common.version.VersionTools.getYUKON_VERSION();

	private SchedulerMainPanel mainPanel = null;
	private javax.swing.JButton[] buttonsArray = null;

/**
 * Scheduler constructor comment.
 */
public Scheduler() 
{
	super();
	initialize();
}

public void initChild() {}

/**
 * This method was created in VisualAge.
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed(ActionEvent event) {

	
	
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:41:18 PM)
 */
// Takes a JComponent and adds an ActionListener to it
public void addActionListenerToJComponent( javax.swing.JComponent component )
{
	if( component instanceof javax.swing.JComboBox )
	{
		comboBox = (javax.swing.JComboBox)component;
		getComboBox().addActionListener( getScheduleActionListener() );

		getIMACSConnection().getMACSConnBase().addObserver( this );
	}
	
}

public boolean needsComboIniting()
{
	return true;
}

/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:51:22 PM)
 */
public void destroy() 
{
/*	try
	{
		if( connection != null )
			connection.disconnect();
	}
	catch( java.io.IOException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
*/
	mainPanel = null;
//	connection = null;
	buttonsArray = null;
	
	System.gc();	
}
/**
 * This method was created in VisualAge.
 * @param scheduler com.cannontech.macs.gui.Scheduler
 * @param pane RootPane
 */
public static void displayScheduler( Scheduler scheduler, final JRootPane owner) 
{
	final SchedulerMainPanel mainPanel = 
			new SchedulerMainPanel( false );
	
	javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();

	
	owner.setJMenuBar( menuBar );
	owner.setContentPane( mainPanel );
	scheduler.mainPanel = mainPanel;
	
	javax.swing.SwingUtilities.invokeLater( new Runnable()
	{
		public void run()
		{
			java.awt.Frame f = com.cannontech.common.util.CtiUtilities.getParentFrame(mainPanel);
	
			if( f != null )
				f.setTitle( mainPanel.getConnectionState() );
		}
			
	});			

}
/**
 * Insert the method's description here.
 * Creation date: (4/19/2001 10:18:56 AM)
 */
public void executeRefreshButton() 
{
	try
	{
		getIMACSConnection().sendRetrieveAllSchedules();
	}
	catch( java.io.IOException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}

}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:50:51 PM)
 */
public void exportDataSet() 
{
	java.awt.Frame f = com.cannontech.common.util.CtiUtilities.getParentFrame( mainPanel );

	com.cannontech.clientutils.commonutils.ModifiedDate date = new com.cannontech.clientutils.commonutils.ModifiedDate();
	
		
	if (f != null)
	{

		com.cannontech.tdc.exportdata.ExportOptionDialog exportDisplay = 
					new com.cannontech.tdc.exportdata.ExportOptionDialog(
							f,
							mainPanel.getScheduleTable().getColumnCount(),
							getName(),
							date.getTimeString(),
							date.getDateString(),
							mainPanel.createPrintableText() );							

		exportDisplay.setModal(true);
		exportDisplay.setLocationRelativeTo( f );
		exportDisplay.show();

		f.repaint();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/23/2001 4:17:59 PM)
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getComboBox() {
	return comboBox;
}

/**
 * Insert the method's description here.
 * Creation date: (8/7/00 4:11:33 PM)
 */
private IMACSConnection getIMACSConnection() 
{
	return (IMACSConnection)ResourceFactory.getIYukon();
}

/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:51:13 PM)
 * @return java.awt.Font
 */
public java.awt.Font getFont() 
{
	if( mainPanel == null )
		return null;
	else	
		return mainPanel.getScheduleTable().getFont();
}
/**
 * Insert the method's description here.
 * Creation date: (8/4/00 9:00:06 AM)
 * @return javax.swing.JButton[]
 */
public javax.swing.JButton[] getJButtons() 
{	
	return buttonsArray;
}
/**
 * Insert the method's description here.
 * Creation date: (8/14/00 9:56:13 AM)
 * @return java.lang.String
 */

// Overide this method so TDC knows what the JLabel should display
// for the JCombo box that normally displays "Display:"
public String getJComboLabel()
{
	return "Category";
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:51:13 PM)
 * @return java.swing.JTable[]
 */
public javax.swing.JTable[] getJTables()
{
	if( mainPanel == null )
		return null;
	else
	{
		javax.swing.JTable[] tables =
		{
			mainPanel.getScheduleTable(),
		};
		
		return tables;
	}
	
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.ActionEvent
 */
public javax.swing.JPanel getMainJPanel()
{
	if( mainPanel == null )
	{
		final SchedulerMainPanel mainPanel = 
				new SchedulerMainPanel( true );
		
		this.mainPanel = mainPanel;

		javax.swing.JButton[] buttons =
		{
			mainPanel.getEditViewButton(),
			mainPanel.getEnableDisableButton(),
			mainPanel.getStartStopButton(),
			mainPanel.getCreateScheduleButton(),
			mainPanel.getDeleteScheduleButton()
		};
		
		setJButtons( buttons );
		
		javax.swing.SwingUtilities.invokeLater( new Runnable()
		{
			public void run()
			{
				java.awt.Frame f = com.cannontech.common.util.CtiUtilities.getParentFrame(mainPanel);
				
				if( f != null )
					f.setTitle( mainPanel.getConnectionState() );
			}
				
		});			

	}
	
	return mainPanel;	
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:51:22 PM)
 */
public String getName()
{
	return MACS_NAME;	
}
/**
 * Insert the method's description here.
 * Creation date: (2/23/2001 4:22:16 PM)
 */
private ScheduleActionListener getScheduleActionListener() 
{
	if( scheduleActionListener == null )
	{
		scheduleActionListener = new ScheduleActionListener();

		// do this so we can tell the connection what Schedules we want
		scheduleActionListener.setScheduleTableModel( mainPanel.getScheduleTableModel() );
	}

	return scheduleActionListener;
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:51:22 PM)
 */
public String getVersion() 
{
	return MACS_VERSION;
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 4:11:33 PM)
 */
private void initialize()
{
	
   try
   {
		//hex value representing the privelages of the user on this machine
		userRightsInt = Integer.parseInt( com.cannontech.common.util.CtiProperties.getInstance().getProperty(
				com.cannontech.common.util.CtiProperties.KEY_MACS_EDIT, "0"), 16 );
	}
   catch (java.util.MissingResourceException e)
   {
	  com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
   }

}

/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 11:32:59 AM)
 * @return boolean
 */
public static boolean isCreateable() 
{
	return com.cannontech.common.util.CtiProperties.isCreateable(userRightsInt);	
}
/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 11:32:59 AM)
 * @return boolean
 */
public static boolean isEnableable() 
{
	return com.cannontech.common.util.CtiProperties.isEnableable(userRightsInt);
}
/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 11:32:59 AM)
 * @return boolean
 */
public static boolean isStartable() 
{
	return com.cannontech.common.util.CtiProperties.isStartable(userRightsInt);
}
/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 */
public static void main(String args[]) {

	try
	{
		javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName() );

		javax.swing.JFrame appFrame = new javax.swing.JFrame();
		appFrame.setTitle("MACS Scheduler");
		appFrame.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("CTILogo.gif"));
		appFrame.setSize( 800, 400 );
		
		final Scheduler s = new Scheduler();		
		//com.cannontech.macs.MACSClientConnection conn = s.getConnection();
	
		appFrame.addWindowListener( new java.awt.event.WindowAdapter() 
		{
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				java.awt.Window win = e.getWindow();
				win.setVisible(false);
				s.destroy();
				win.dispose();				
				System.exit(0);
			}
		});


		Scheduler.displayScheduler( s, appFrame.getRootPane() );
		appFrame.setVisible(true);		
	}
	catch( Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
} 
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:51:22 PM)
 */
public void print() {}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:51:22 PM)
 */
public void printPreview() {}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:41:18 PM)
 */
// Takes a JComponent and an removes the specified ActionListener
public void removeActionListenerFromJComponent( javax.swing.JComponent component )
{
	if( component instanceof javax.swing.JComboBox )
	{
		//((javax.swing.JComboBox)component).removeActionListener( getCapBankActionListener() );
		getComboBox().removeActionListener( getScheduleActionListener() );
		
		getIMACSConnection().getMACSConnBase().deleteObserver( this );
		
		comboBox = null;
	}

}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:51:13 PM)
 */
public void setTableFont(java.awt.Font font ) 
{
	if( mainPanel == null )
		return;
			
	mainPanel.getScheduleTable().setFont( font );
	mainPanel.getScheduleTable().setRowHeight( font.getSize() + 2 );

	// set the table headers font
	mainPanel.getScheduleTable().getTableHeader().setFont( font );

	// Set the values for the table model
	((ScheduleTableModel)mainPanel.getScheduleTable().getModel()).setModelFont( font.getName(), font.getSize() );
	
	mainPanel.getScheduleTable().revalidate();
	mainPanel.getScheduleTable().repaint();	
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:52:15 PM)
 * @param hgridLines boolean
 */
public void setGridLines(boolean hGridLines, boolean vGridLines ) 
{
	if( mainPanel == null )
		return;

	int vLines = ((vGridLines == true) ? 1 : 0);
	int hLines = ((hGridLines == true) ? 1 : 0);
	
	mainPanel.getScheduleTable().setIntercellSpacing(new java.awt.Dimension(vLines, hLines));
	mainPanel.getScheduleTable().setShowHorizontalLines( hGridLines );
	mainPanel.getScheduleTable().setShowVerticalLines( vGridLines );
	
	mainPanel.getScheduleTable().revalidate();
	mainPanel.getScheduleTable().repaint();	
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:51:13 PM)
 */
public void setInitialTitle()
{
	// we must have the panel realize its the first time the connection
	//  is being observed
	mainPanel.update( null, getIMACSConnection() );

}
/**
 * Insert the method's description here.
 * Creation date: (8/4/00 9:08:24 AM)
 * @param buttons javax.swing.JButton[]
 */
public void setJButtons(javax.swing.JButton[] buttons) 
{
	buttonsArray = buttons;	
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:51:13 PM)
 */
public void setRowColors(java.awt.Color[] foreGroundColors, java.awt.Color bgColor )
{
	mainPanel.getScheduleTableModel().setBackGroundColor( bgColor );
	mainPanel.getScheduleTableModel().setCellColors( foreGroundColors );
}
/**
 * Insert the method's description here.
 * Creation date: (2/1/2001 1:04:57 PM)
 * @param soundToggle boolean
 */
public void setSound(boolean soundToggle) 
{
	// if we want the sound to be toggled, implement this method	
}
/**
 * This method was created in VisualAge.
 * @param source Observable
 * @param obj java.lang.Object
 */
public void update(java.util.Observable source, Object obj )
{
	if( source instanceof IMACSConnection
		 && obj instanceof MACSCategoryChange )
	{
		final MACSCategoryChange msg = (MACSCategoryChange)obj;
			
		if( getComboBox() != null )
		{				
			if( msg.id == MACSCategoryChange.INSERT )
			{
				javax.swing.SwingUtilities.invokeLater( new Runnable()
				{
					public void run()
					{
						getComboBox().addItem( msg.arg.toString() );
					}
				});

			}
			else if( msg.id == MACSCategoryChange.DELETE )
			{
				javax.swing.SwingUtilities.invokeLater( new Runnable()
				{
					public void run()
					{
						getComboBox().removeItem( msg.arg.toString() );
					}
				});

			}
			else if( msg.id == MACSCategoryChange.DELETE_ALL ) // remove all items
			{
				javax.swing.SwingUtilities.invokeLater( new Runnable()
				{
					public void run()
					{
						getComboBox().setSelectedIndex(0);
						
						for( int i = 1; i < getComboBox().getItemCount(); i++ )
						{
							getComboBox().removeItemAt(1);
						}
					}
				});

			}	
		}
	}
		
}
}
