package com.cannontech.cbc.gui;

/**
 * This type was created in VisualAge.
 */
import javax.swing.JRootPane;

import com.cannontech.cbc.data.CBCClientConnection;
import com.cannontech.cbc.messages.CBCSubAreaNames;

public class StrategyReceiver implements com.cannontech.tdc.SpecialTDCChild, java.util.Observer
{
	//a band aid to always allow us to insert the last CBCSubAreaNames we received
	private CBCSubAreaNames lastSubAreaMsg = null;
	
	public final static String CBC_NAME = "Cap Bank Control";
	public final static String CBC_VERSION = 
			com.cannontech.common.version.VersionTools.getYUKON_VERSION();

	private ReceiverMainPanel mainPanel = null;
	private CBCClientConnection connectionWrapper = null;
	private javax.swing.JButton[] buttonsArray = null;
	private CapBankActionListener capBankActionListener = null;
	
	private javax.swing.JComboBox comboBox = null;
/**
 * Scheduler constructor comment.
 */
public StrategyReceiver() 
{
	super();
}

public void initChild() {}


/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:41:18 PM)
 */
public void addActionListenerToJComponent( javax.swing.JComponent component )
{
	if( component instanceof javax.swing.JComboBox )
	{
		comboBox = (javax.swing.JComboBox)component;

		getJComboBox().addActionListener( getCapBankActionListener() );

		updateAreaList( lastSubAreaMsg );
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
	try
	{
		if( connectionWrapper != null )
		{
			connectionWrapper.stopInThread();
			connectionWrapper.disconnect();
		}

		if( mainPanel != null )
			mainPanel.destroy();
	}
	catch( java.io.IOException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}

	mainPanel = null;
	connectionWrapper = null;
	buttonsArray = null;
	
	
	System.gc();	
}
/**
 * This method was created in VisualAge.
 */
public static void displayStrategyReceiver( StrategyReceiver strategyReceiver, JRootPane owner) 
{
	ReceiverMainPanel mainPanel = new ReceiverMainPanel( strategyReceiver.getConnectionWrapper() );
		
	owner.setContentPane( mainPanel );

	strategyReceiver.mainPanel = mainPanel;	
	mainPanel.update( strategyReceiver.getConnectionWrapper(), strategyReceiver.getConnectionWrapper() );

	// initDividerPostition is called to set the JSplitPane's divider at the very bottom
	mainPanel.initDividerPosition();
}
/**
 * Insert the method's description here.
 * Creation date: (4/19/2001 10:18:56 AM)
 */
public void executeRefreshButton() 
{

	try
	{
		getConnectionWrapper().executeCommand( 0, com.cannontech.cbc.messages.CBCCommand.REQUEST_ALL_SUBS );
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
						mainPanel.getSubBusTable().getColumnCount(),
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
 * Creation date: (2/2/2001 3:26:23 PM)
 * @return com.cannontech.cbc.gui.CapBankActionListener
 */
private CapBankActionListener getCapBankActionListener() 
{
	if( capBankActionListener == null )
	{
		capBankActionListener = new CapBankActionListener();

		// do this so we can tell the connection what SubBuses we want
		capBankActionListener.setSubBusTableModel( mainPanel.getSubBusTableModel() );
	}
	
	return capBankActionListener;
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 4:11:33 PM)
 */
private CBCClientConnection getConnectionWrapper() 
{
	if( connectionWrapper == null )
	{
		try
		{	
			connectionWrapper = new CBCClientConnection();
			connectionWrapper.addObserver( this );

		 	if( connectionWrapper.isConnValid() )
				com.cannontech.clientutils.CTILogger.info("Retrieving CBC strategies...");
		}
		catch( Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}	
	}
	
	return connectionWrapper;
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
		return mainPanel.getSubBusTable().getFont();
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
 * Creation date: (2/6/2001 4:25:20 PM)
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getJComboBox() {
	return comboBox;
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
	return "Sub Area:";
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:51:13 PM)
 * @return java.awt.Font
 */
public javax.swing.JTable[] getJTables()
{
	if( mainPanel == null )
		return null;
	else
	{
		javax.swing.JTable[] tables =
		{
			mainPanel.getSubBusTable(),
			mainPanel.getCapBankTable(),
			mainPanel.getFeederTable()
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
		//initConnection();
		ReceiverMainPanel mPanel = new ReceiverMainPanel( getConnectionWrapper() );
		
		mainPanel = mPanel;

		javax.swing.JButton[] buttons =
		{
			mainPanel.getEnableAreaButton(),
			mainPanel.getDisableAreaButton(),
			mainPanel.getConfirmAreaButton(),
			mainPanel.getCurrentViewButton()
		};
		
		this.setJButtons( buttons );

		// initDividerPostition is called to set the JSplitPane's divider at the very bottom
		mainPanel.initDividerPosition();
	}
	
	return mainPanel;	
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:51:22 PM)
 */
public String getName()
{
	return CBC_NAME;	
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:51:22 PM)
 */
public String getVersion() 
{
	return CBC_VERSION;
}
/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 */
public static void main(String args[]) 
{
	try
	{
		javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName() );

		javax.swing.JFrame appFrame = new javax.swing.JFrame();
		
		
		StrategyReceiver s = new StrategyReceiver();
		
		
		appFrame.addWindowListener( new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e)
				{
						java.awt.Window win = e.getWindow();
						win.setVisible(false);
						win.dispose();
						System.exit(0);
				}
		} );

		StrategyReceiver.displayStrategyReceiver( s, appFrame.getRootPane() );		

		appFrame.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("CTILogo.gif"));
		appFrame.setSize( 800, 400 );
//		appFrame.setTitle(StrategyReceiver.CBC_NAME);
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
public void removeActionListenerFromJComponent( javax.swing.JComponent component )
{
	if( component instanceof javax.swing.JComboBox )
	{
		//((javax.swing.JComboBox)component).removeActionListener( getCapBankActionListener() );
		getJComboBox().removeActionListener( getCapBankActionListener() );
		getConnectionWrapper().deleteObserver( this );
		
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

	// Set the values for the table model
	mainPanel.getSubBusTableModel().setFontValues( font.getName(), font.getSize() );

		
	for( int i = 0; i < getJTables().length; i++ )
	{
		getJTables()[i].setFont( font );
		getJTables()[i].setRowHeight( font.getSize() + 2 );
		// set the table headers font
		getJTables()[i].getTableHeader().setFont( font );


		getJTables()[i].revalidate();
		getJTables()[i].repaint();
	}

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


	for( int i = 0; i < getJTables().length; i++ )
	{
		getJTables()[i].setIntercellSpacing(new java.awt.Dimension(vLines, hLines));
		getJTables()[i].setShowHorizontalLines( hGridLines );
		getJTables()[i].setShowVerticalLines( vGridLines );

		getJTables()[i].revalidate();
		getJTables()[i].repaint();
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:51:13 PM)
 */
public void setInitialTitle()
{
	// we must have the panel realize its the first time the connection
	//  is being observed
	mainPanel.update( getConnectionWrapper(), null );

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
	// DO NOTHING FOR NOW
}
/**
 * Insert the method's description here.
 * Creation date: (2/1/2001 12:55:52 PM)
 * @param soundToggle boolean
 */
public void setAlarmMute( boolean muted )
{
	((ReceiverMainPanel)getMainJPanel()).setCapBankTableModelSound( muted );
}

public void silenceAlarms() 
{
	((ReceiverMainPanel)getMainJPanel()).silenceAlarms();
}

/**
 * This method was created in VisualAge.
 * @param source Observable
 * @param obj java.lang.Object
 */
public void update(java.util.Observable source, Object obj )
{

	if( source instanceof CBCClientConnection )
	{
		if( obj instanceof CBCSubAreaNames )
		{	
			CBCSubAreaNames areaNames = (CBCSubAreaNames)obj;
			
			lastSubAreaMsg = areaNames;

			updateAreaList( lastSubAreaMsg );
			
		}
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (4/12/2002 1:53:27 PM)
 * @param areaNames com.cannontech.cbc.messages.CBCSubAreaNames
 */
private void updateAreaList(CBCSubAreaNames areaNames) 
{
	if( getJComboBox() == null || areaNames == null )
		return;

	synchronized( getJComboBox() )
	{
		Object obj = getJComboBox().getSelectedItem();

		try
		{
			getJComboBox().removeActionListener( getCapBankActionListener() );
		
			// remove all the values in the JComboBox except the first one
			getJComboBox().removeAllItems();
			getJComboBox().addItem( SubBusTableModel.ALL_FILTER );

			// add all area names to the JComboBox	
			for( int i = 0; i < areaNames.getNumberOfAreas(); i++ )
			{
				getJComboBox().addItem( areaNames.getAreaName(i) );
			}

			if( obj != null )
				getJComboBox().setSelectedItem(obj);
		}
		finally  //we must add our listener back!
		{
			getJComboBox().addActionListener( getCapBankActionListener() );
		}

	}
}

}
