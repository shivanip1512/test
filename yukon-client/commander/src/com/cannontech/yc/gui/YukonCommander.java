package com.cannontech.yc.gui;

/**
 * This type was created in VisualAge.
 */
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.event.TreeSelectionEvent;

import com.cannontech.database.model.ModelFactory;
import com.cannontech.yc.gui.menu.YCCommandMenu;
import com.cannontech.yc.gui.menu.YCFileMenu;
import com.cannontech.yc.gui.menu.YCHelpMenu;
import com.cannontech.yc.gui.menu.YCViewMenu;

public class YukonCommander extends javax.swing.JFrame implements com.cannontech.database.cache.DBChangeListener, java.awt.event.ActionListener, java.awt.event.FocusListener, java.awt.event.KeyListener, Runnable, javax.swing.event.TreeSelectionListener{
	private static  YC ycClass;
	private static final int treeModels[] =
	{
		ModelFactory.DEVICE,
		ModelFactory.LMGROUPS,
		ModelFactory.CAPBANKCONTROLLER,
		ModelFactory.CICUSTOMER,
		ModelFactory.DEVICE_METERNUMBER,
		ModelFactory.COLLECTIONGROUP,
		ModelFactory.TESTCOLLECTIONGROUP,
		ModelFactory.EDITABLEVERSACOMSERIAL,
		
	};
	//-----------------------------------------
	private static final String YC_TITLE = "Yukon Commander";
	private static final String VERSION = "2.2.7";
	public static final String HELP_FILE = com.cannontech.common.util.CtiUtilities.getHelpDirPath() + "Yukon Commander Help.chm";
	private com.cannontech.message.porter.ClientConnection connToPorter;
	private Thread inThread;
	private com.cannontech.message.dispatch.ClientConnection connToDispatch;
	//-----------------------------------------
	private javax.swing.JPanel ivjJFrameContentPane = null;
	private javax.swing.JPanel ivjRightSidePanel = null;
	private javax.swing.JPanel ivjOutterOutputPanel = null;
	private javax.swing.JPanel ivjOutputPanel = null;
	private javax.swing.JPanel ivjClearPrintLargePanel = null;
	private javax.swing.JScrollPane ivjOutputScrollPane = null;
	private javax.swing.JTextArea ivjOutputTextArea = null;
	private com.cannontech.common.gui.util.TreeViewPanel ivjTreeViewPanel = null;
	private CommandPanel ivjCommandPanel = null;
	private SerialRoutePanel ivjSerialRoutePanel = null;
	private javax.swing.JPanel ivjLeftSidePanel = null;
	private javax.swing.JSplitPane ivjSplitPane = null;
	private static CommandLogPanel ivjCommandLogPanel = null;
	private javax.swing.JLabel ivjCGPMode = null;
	private ClearPrintButtonPanel ivjClearPrintButtons = null;
	private AdvancedOptionsPanel advOptsPanel = null;
	private LocateRouteDialog locRouteDialog = null;
	private YCCommandMenu ivjYCCommandMenu = null;
	private YCFileMenu ivjYCFileMenu = null;
	private YCHelpMenu ivjYCHelpMenu = null;
	private YCViewMenu ivjYCViewMenu = null;
	private javax.swing.JMenuBar ivjYukonCommanderJMenuBar = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public YukonCommander() {
	super();
	ycClass = new YC();
	
	//initialize();
	//initDispatchConnection();
	//initPorterConnection();
	//ycClass.setConnToPorter(connToPorter);
	//ycClass.initCommandFileDirectory();
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 3:21:17 PM)
 */
private void about()
{
	javax.swing.JFrame popupFrame = new javax.swing.JFrame();
	popupFrame.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("CommanderIcon.gif"));
	javax.swing.JOptionPane.showMessageDialog(popupFrame,
	"This is version " + getVersion() + "\nCopyright (C) 1999-2002 Cannon Technologies.",
	"About Yukon Commander",javax.swing.JOptionPane.INFORMATION_MESSAGE);
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed(ActionEvent event)
{
	if(	event.getSource() == getTreeViewPanel().getSortByComboBox())
		sortByComboBoxAction(); 

	/////////////////////////////////////////////////////////////////////////
	// ** YC View Menu - reloadMenu **//
	else if (event.getSource() == getYCViewMenu().reloadMenuItem)
		reloadDevices();

	/////////////////////////////////////////////////////////////////////////
	// ** SerialRoutePanel **//
	else if (event.getSource() == getSerialRoutePanel().getSerialTextField())
		serialNumberAction();

	else if (event.getSource() == getSerialRoutePanel().getRouteComboBox())
	{
		setRoute(getSerialRoutePanel().getRouteComboBox().getSelectedItem());
		com.cannontech.clientutils.CTILogger.info(getRoute());
	}

	/////////////////////////////////////////////////////////////////////////
	// ** CommandPanel - execute **//
	else if( event.getSource() == getCommandPanel().getExecuteButton() ||
			event.getSource() == getYCCommandMenu().executeMenuItem )
	{
		setCommand((String) getCommandPanel().getExecuteCommandComboBoxTextField().getText().trim());
		if( isValidSetup() )
			enter();
	}
	
	/////////////////////////////////////////////////////////////////////////
	// ** CommandPanel - stopButton **//
	// ** YC Command Menu - stopMenu **//
	else if( event.getSource() == getCommandPanel().getStopButton() ||
			event.getSource() == getYCCommandMenu().stopMenuItem )
	{
		if( !connToPorter.isValid() )
		{
			getCommandLogPanel().addLogElement(" ** Warning: Not connected to port control service **");
			return;
		}
		ycClass.stop();
		getOutputTextArea().setCaretPosition( getOutputTextArea().getDocument().getEndPosition().getOffset() - 1 );
	}

	/////////////////////////////////////////////////////////////////////////
	// ** Command Panel ** //
	else if( event.getSource() == getCommandPanel().getAvailableCommandsComboBox())
	{
		if( getCommandPanel().getAvailableCommandsComboBox().getSelectedIndex() > 0) //0 is default "select"
		{
			getCommandPanel().getExecuteCommandComboBoxTextField().setText( ycClass.substituteCommand(getCommandPanel().getAvailableCommandsComboBox().getSelectedItem().toString() ));
			getCommandPanel().getExecuteCommandComboBoxTextField().requestFocus();
		}
	}
	/////////////////////////////////////////////////////////////////////////
	// ** YC Command Menu - locateRoute **//
	else if( event.getSource() == getYCCommandMenu().locateRoute )
	{
		getLocateRouteDialog().getDeviceNameTextField().setText(getTreeViewPanel().getSelectedItem().toString());		
		getLocateRouteDialog().showLocateDialog();
		setRoute(getLocateRouteDialog().getRouteComboBox().getSelectedItem());
	}
	else if ( event.getSource() == getLocateRouteDialog().getLocateButton())
	{
		setCommand("loop locateroute");
		if( isValidSetup() )
		{
			enter();
		}
		// Get routeID from comboBox / set it in the request
		//if( getRoute() != null && getRoute() instanceof com.cannontech.database.data.lite.LiteYukonPAObject)
		//{
			//com.cannontech.database.data.lite.LiteYukonPAObject r = (com.cannontech.database.data.lite.LiteYukonPAObject) getRoute();
			//req.setRouteID(r.getYukonID());
		//}
	}
	else if (event.getSource() == getLocateRouteDialog().getRouteComboBox())
	{
		setRoute(getLocateRouteDialog().getRouteComboBox().getSelectedItem());
	}
	/////////////////////////////////////////////////////////////////////////
	// ** YC File Menu - printMenu **//
	else if( event.getSource() == getYCFileMenu().printMenuItem || 
		event.getSource() == getClearPrintButtons().getPrintButton() )
		print();

	/////////////////////////////////////////////////////////////////////////
	// ** YC File Menu - saveMenu **//
	else if( event.getSource() == getYCFileMenu().saveMenuItem )
		save();

	/////////////////////////////////////////////////////////////////////////
	// ** YC File Menu - findMenu **//
	else if( event.getSource() == getYCViewMenu().findMenuItem )
	{
		String value = javax.swing.JOptionPane.showInputDialog(
			this, "Name of the item: ", "Find",
			javax.swing.JOptionPane.QUESTION_MESSAGE );

		if( value != null )
		{

			boolean found = getTreeViewPanel().searchFirstLevelString(value);

			if( !found )
				javax.swing.JOptionPane.showMessageDialog(
					this, "Unable to find your selected item", "Item Not Found",
					javax.swing.JOptionPane.INFORMATION_MESSAGE );
		}
	}
	/////////////////////////////////////////////////////////////////////////
	// ** YC File Menu - CGPMode **//
	else if( event.getSource() == getYCFileMenu().commandSpecificControl )
	{
		ycClass.setDirectSend( !ycClass.isDirectSend() );
		if( ycClass.isDirectSend() )
		{
			com.cannontech.clientutils.CTILogger.info(" ** CGPMODE IS ON ** ");
			getCGPMode().setText("CGPMODE ON:  Sending \'Execute Command\' string. (CTRL + F5)");
		}
		else
		{
			com.cannontech.clientutils.CTILogger.info(" ** CGPMODE IS OFF ** ");
			getCGPMode().setText("");
		}
	}

	/////////////////////////////////////////////////////////////////////////
	// ** YC View Menu - clearButton **//
	else if( event.getSource() == getClearPrintButtons().getClearButton() ||
			event.getSource() == getYCViewMenu().clearMenuItem )
		getOutputTextArea().setText("");

	/////////////////////////////////////////////////////////////////////////
	// ** YC Help Menu - aboutMenu **//
	else if( event.getSource() == getYCHelpMenu().aboutMenuItem)
		about();

	/////////////////////////////////////////////////////////////////////////
	// ** YC Help Menu - helpTopicMenu **//
	else if( event.getSource() == getYCHelpMenu().helpTopicMenuItem)
		com.cannontech.common.util.CtiUtilities.showHelp( HELP_FILE );

	/////////////////////////////////////////////////////////////////////////
	// ** YC View Menu - deleteSerialNumberMenu **//
	else if( event.getSource() == getYCViewMenu().deleteSerialNumberMenuItem)
		deleteSerialNumber();
	
	/////////////////////////////////////////////////////////////////////////
	// ** YC View Menu - showCommandLogMenu **//
	//else if( event.getSource() == getYCViewMenu().showCommandLogButton )
		//getCommandLogPanel().setVisible( getYCViewMenu().showCommandLogButton.isSelected() );
	/////////////////////////////////////////////////////////////////////////
	// ** YC Command Menu - advancedOptions  **//
	else if( event.getSource() == getYCCommandMenu().advancedOptionsMenuItem )
	{
		advOptsPanel = null;
		getAdvOptsPanel().showAdvancedOptions();
	}
	else if( advOptsPanel != null && event.getSource() == getAdvOptsPanel().getOkButton())
	{
		YCDefaults newDefaults = new YCDefaults(
			(new Integer((String)getAdvOptsPanel().getCommandPriorityTextField().getText())).intValue(),
			getAdvOptsPanel().getQueueCommandCheckBox().isSelected(),
			getAdvOptsPanel().getShowMessageLogCheckBox().isSelected(),
			getAdvOptsPanel().getConfirmCommandCheckBox().isSelected(),
			getAdvOptsPanel().getCommandFileDirectoryTextField().getText().toString());

		ycClass.setYCDefaults(newDefaults);
		ycClass.getYCDefaults().writeDefaultsFile();

		getCommandLogPanel().setVisible( ycClass.getYCDefaults().getShowMessageLog() );
		
		getAdvOptsPanel().setVisible(false);
		getAdvOptsPanel().dispose();
		advOptsPanel = null;
	}
	/////////////////////////////////////////////////////////////////////////
	// ** YC File Menu - exitMenu **//
	else if( event.getSource() == getYCFileMenu().exitMenuItem )
		exit();
}
/**
 * Insert the method's description here.
 * Creation date: (10/15/2001 11:03:05 AM)
 * @param stringObject java.lang.Object
 */
private void appendOutputTextArea(String stringObject)
{
	getOutputTextArea().append( stringObject );
}
/**
 * Insert the method's description here.
 * Creation date: (6/28/2001 9:14:02 AM)
 * @param message java.lang.String
 */
private int areYouSure(String message, int messageType )
{
	javax.swing.JFrame popupFrame = new javax.swing.JFrame();
	popupFrame.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("CommanderIcon.gif"));
	return javax.swing.JOptionPane.showConfirmDialog(popupFrame, message, YC_TITLE, javax.swing.JOptionPane.YES_NO_OPTION, messageType);
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 3:21:58 PM)
 */
private void deleteSerialNumber()
{
	if(getModelType() == ModelFactory.EDITABLEVERSACOMSERIAL)
	{
		if (getTreeItem() == null)
			return;
			
		else
		{
			com.cannontech.common.gui.util.TreeViewPanel t =  getTreeViewPanel();
			if( t.getSelectedNode().getParent() == null)	// we got the parent!
				return;
				
			com.cannontech.database.model.EditableVersacomSerialModel.getSerialNumberVector().remove( getTreeItem() );	// enter serial# into the vector
			
			// refreshing the serial# tree on the treeViewPanel
			((com.cannontech.database.model.EditableVersacomSerialModel) t.getSelectedTreeModel()).update();
			getSerialRoutePanel().getSerialTextField().setText("");
		}
	}	
}
/**
 * Creation date: (3/9/2001 1:59:56 PM)
 * @author Stacey Nebben	Added in version 1.5 
 * @param b boolean
 * Enables/Disables (according to b value) the Route and Serial Number Labels
 *	and Combo Boxes (upper left side).
 */
private void enableSerialAndRoute(boolean b)
{
	getSerialRoutePanel().setObjectsEnabled(b);

	getYCViewMenu().deleteSerialNumberMenuItem.setEnabled(b);
}
/**
 * Handles the case when the user wants to submit the text in the text field
 * 
 * Checks for a connection to port control.
 * Uses MoveToTop thread to control storage in the commandComboBox, items appear
 *	in it only once and most recently used item is stored at the top of the list.
 * Calls substituteCommand to get the command string to be used for the selected command.
 * Determines which type of model 'device' is being used and then calls that type's 
 * handle[Object] function
 */
private void enter()
{
	class MoveToTop implements Runnable
	{
		javax.swing.JComboBox comboBox;
		Object item;
			
		MoveToTop(javax.swing.JComboBox comboBox, Object item)
		{
	   		this.comboBox = comboBox;
		 	this.item = item;
		}

		public void run()
		{			
			java.util.Vector toRemove = new java.util.Vector();
					
			for( int i = 0; i < this.comboBox.getModel().getSize(); i++ )
			{
				if( this.comboBox.getItemAt(i).equals(this.item) )
				{
					toRemove.addElement( this.comboBox.getItemAt(i) );
				}
	 		}
		
			for( int j = 0; j < toRemove.size(); j++ )
			{
				this.comboBox.removeItem( toRemove.elementAt(j) );
			}

			//Insert the item at the top
			comboBox.insertItemAt( item, 0 );
			comboBox.setSelectedIndex(0);

			//Clear the text field
			if( comboBox.getEditor().getEditorComponent() instanceof javax.swing.JTextField )
			{
				//((javax.swing.JTextField) comboBox.getEditor().getEditorComponent()).setText("");
			}
		}
	};
	//---------------------------------------------------------------------------------------	

	MoveToTop runThisLater = new MoveToTop( getCommandPanel().getExecuteCommandComboBox(), getCommand() );
	javax.swing.SwingUtilities.invokeLater( runThisLater );

	ycClass.executeCommand();
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 11:12:24 AM)
 */
private void exit()
{
	try
	{
		if ( connToDispatch!= null && connToDispatch.isValid() )  // free up Dispatchs resources
		{
			com.cannontech.message.dispatch.message.Command command = new com.cannontech.message.dispatch.message.Command();
			command.setPriority(15);
			
			command.setOperation( com.cannontech.message.dispatch.message.Command.CLIENT_APP_SHUTDOWN );

			connToDispatch.write( command );

			connToDispatch.disconnect();
		}
	}
	catch ( java.io.IOException e )
	{
		e.printStackTrace();
	}

	System.exit(0);

}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 3:08:09 PM)
 * @param event java.awt.event.FocusListener
 */
public void focusGained(java.awt.event.FocusEvent event) {}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 3:08:09 PM)
 * @param event java.awt.event.FocusListener
 */
public void focusLost(java.awt.event.FocusEvent event)
{
	if (event.getSource() == getSerialRoutePanel().getSerialTextField())
	{
		serialNumberAction();	
	}
	
}
private AdvancedOptionsPanel getAdvOptsPanel()
{
	if( advOptsPanel == null)
	{
		advOptsPanel  = new AdvancedOptionsPanel(ycClass.getYCDefaults());
		advOptsPanel.getOkButton().addActionListener(this);
		//advOptsPanel.getCancelButton().addActionListener(this);
	}
	return advOptsPanel;
}
/**
 * Return the CGPMode property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCGPMode() {
	if (ivjCGPMode == null) {
		try {
			ivjCGPMode = new javax.swing.JLabel();
			ivjCGPMode.setName("CGPMode");
			ivjCGPMode.setText("");
			ivjCGPMode.setForeground(java.awt.Color.red);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCGPMode;
}
/**
 * Return the ClearPrintButtons property value.
 * @return com.cannontech.yc.gui.ClearPrintButtonPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ClearPrintButtonPanel getClearPrintButtons() {
	if (ivjClearPrintButtons == null) {
		try {
			ivjClearPrintButtons = new com.cannontech.yc.gui.ClearPrintButtonPanel();
			ivjClearPrintButtons.setName("ClearPrintButtons");
			// user code begin {1}
			ivjClearPrintButtons.getClearButton().addActionListener(this);
			ivjClearPrintButtons.getPrintButton().addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjClearPrintButtons;
}
/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getClearPrintLargePanel() {
	if (ivjClearPrintLargePanel == null) {
		try {
			ivjClearPrintLargePanel = new javax.swing.JPanel();
			ivjClearPrintLargePanel.setName("ClearPrintLargePanel");
			ivjClearPrintLargePanel.setLayout(new java.awt.BorderLayout());
			getClearPrintLargePanel().add(getCGPMode(), "West");
			getClearPrintLargePanel().add(getClearPrintButtons(), "East");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjClearPrintLargePanel;
}
/**
 * Insert the method's description here.
 * Creation date: (12/20/2001 5:14:03 PM)
 * @return com.cannontech.message.util.ClientConnection
 */
public com.cannontech.message.util.ClientConnection getClientConnection() {
	return connToDispatch;
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/2001 12:11:11 PM)
 * @return java.lang.String
 */
private String getCommand()
{
	return ycClass.getCommand();
	
}
/**
 * Return the CommandLogPanel property value.
 * @return com.cannontech.yc.gui.CommandLogPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public static CommandLogPanel getCommandLogPanel() {
	if (ivjCommandLogPanel == null) {
		try {
			ivjCommandLogPanel = new com.cannontech.yc.gui.CommandLogPanel();
			ivjCommandLogPanel.setName("CommandLogPanel");
			// user code begin {1}
			ivjCommandLogPanel.setVisible( ycClass.getYCDefaults().getShowMessageLog() );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommandLogPanel;
}
/**
 * Return the CommandPanel1 property value.
 * @return com.cannontech.yc.gui.CommandPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private CommandPanel getCommandPanel() {
	if (ivjCommandPanel == null) {
		try {
			ivjCommandPanel = new com.cannontech.yc.gui.CommandPanel();
			ivjCommandPanel.setName("CommandPanel");
			// user code begin {1}
			ivjCommandPanel.getExecuteButton().addActionListener(this);
			ivjCommandPanel.getStopButton().addActionListener(this);

			ivjCommandPanel.getExecuteCommandComboBoxTextField().addKeyListener(this);
			ivjCommandPanel.getExecuteCommandComboBoxTextField().addActionListener(this);
			//ivjCommandPanel.getExecuteCommandComboBox().addItemListener(this);
			//ivjCommandPanel.getAvailableCommandsComboBox().addItemListener(this);
			ivjCommandPanel.getAvailableCommandsComboBox().addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommandPanel;
}
/**
 * Return the JFrameContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJFrameContentPane() {
	if (ivjJFrameContentPane == null) {
		try {
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setLayout(new java.awt.BorderLayout());
			getJFrameContentPane().add(getSplitPane(), "Center");
			getJFrameContentPane().add(getCommandLogPanel(), "South");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJFrameContentPane;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getLeftSidePanel() {
	if (ivjLeftSidePanel == null) {
		try {
			ivjLeftSidePanel = new javax.swing.JPanel();
			ivjLeftSidePanel.setName("LeftSidePanel");
			ivjLeftSidePanel.setLayout(new java.awt.BorderLayout());
			getLeftSidePanel().add(getSerialRoutePanel(), "North");
			getLeftSidePanel().add(getTreeViewPanel(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLeftSidePanel;
}
private LocateRouteDialog getLocateRouteDialog()
{
	if( locRouteDialog == null)
	{
		locRouteDialog = new LocateRouteDialog();
		if( ycClass.getAllRoutes() != null)
		{
			for ( int i = 0; i < ycClass.getAllRoutes().length; i++)
			{
				locRouteDialog.getRouteComboBox().addItem(ycClass.getAllRoutes()[i]);
			}
			if( locRouteDialog.getRouteComboBox().getItemCount() > 0 )
				locRouteDialog.getRouteComboBox().setSelectedIndex(0);
		}
		locRouteDialog.getLocateButton().addActionListener(this);
		locRouteDialog.getRouteComboBox().addActionListener(this);
	}
	return locRouteDialog;
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2002 2:13:06 PM)
 * @return int
 */
private int getModelType()
{
	return ycClass.getModelType();
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getOutputPanel() {
	if (ivjOutputPanel == null) {
		try {
			ivjOutputPanel = new javax.swing.JPanel();
			ivjOutputPanel.setName("OutputPanel");
			ivjOutputPanel.setLayout(new java.awt.BorderLayout());
			getOutputPanel().add(getOutputScrollPane(), "Center");
			getOutputPanel().add(getClearPrintLargePanel(), "South");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOutputPanel;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getOutputScrollPane() {
	if (ivjOutputScrollPane == null) {
		try {
			ivjOutputScrollPane = new javax.swing.JScrollPane();
			ivjOutputScrollPane.setName("OutputScrollPane");
			ivjOutputScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjOutputScrollPane.setPreferredSize(new java.awt.Dimension(550, 510));
			ivjOutputScrollPane.setDoubleBuffered(true);
			getOutputScrollPane().setViewportView(getOutputTextArea());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOutputScrollPane;
}
/**
 * Return the OutputTextArea property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getOutputTextArea() {
	if (ivjOutputTextArea == null) {
		try {
			ivjOutputTextArea = new javax.swing.JTextArea();
			ivjOutputTextArea.setName("OutputTextArea");
			ivjOutputTextArea.setToolTipText("");
			ivjOutputTextArea.setOpaque(true);
			ivjOutputTextArea.setBounds(0, 0, 15, 19);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOutputTextArea;
}
/**
 * Return the OutterOutputPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getOutterOutputPanel() {
	if (ivjOutterOutputPanel == null) {
		try {
			ivjOutterOutputPanel = new javax.swing.JPanel();
			ivjOutterOutputPanel.setName("OutterOutputPanel");
			ivjOutterOutputPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsOutputPanel = new java.awt.GridBagConstraints();
			constraintsOutputPanel.gridx = 0; constraintsOutputPanel.gridy = 0;
			constraintsOutputPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsOutputPanel.weightx = 1.0;
			constraintsOutputPanel.weighty = 1.0;
			constraintsOutputPanel.insets = new java.awt.Insets(0, 12, 0, 12);
			getOutterOutputPanel().add(getOutputPanel(), constraintsOutputPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOutterOutputPanel;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getRightSidePanel() {
	if (ivjRightSidePanel == null) {
		try {
			ivjRightSidePanel = new javax.swing.JPanel();
			ivjRightSidePanel.setName("RightSidePanel");
			ivjRightSidePanel.setLayout(new java.awt.BorderLayout());
			getRightSidePanel().add(getCommandPanel(), "North");
			getRightSidePanel().add(getOutterOutputPanel(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRightSidePanel;
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2002 1:53:53 PM)
 * @return java.lang.Object
 */
private Object getRoute()
{
	return ycClass.getRoute();
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 4:16:48 PM)
 * @return String
 */
private String getSerialNumber()
{
	return ycClass.getSerialNumber();
}
/**
 * Return the SerialRoutePanel property value.
 * @return com.cannontech.yc.gui.SerialRoutePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SerialRoutePanel getSerialRoutePanel() {
	if (ivjSerialRoutePanel == null) {
		try {
			ivjSerialRoutePanel = new com.cannontech.yc.gui.SerialRoutePanel();
			ivjSerialRoutePanel.setName("SerialRoutePanel");
			// user code begin {1}
			ivjSerialRoutePanel.getSerialTextField().addKeyListener(this);
			ivjSerialRoutePanel.getSerialTextField().addActionListener(this);
			ivjSerialRoutePanel.getSerialTextField().addFocusListener(this);
			ivjSerialRoutePanel.getRouteComboBox().addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSerialRoutePanel;
}
/**
 * Return the JSplitPane1 property value.
 * @return javax.swing.JSplitPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSplitPane getSplitPane() {
	if (ivjSplitPane == null) {
		try {
			ivjSplitPane = new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT);
			ivjSplitPane.setName("SplitPane");
			ivjSplitPane.setDividerSize(5);
			getSplitPane().add(getRightSidePanel(), "right");
			getSplitPane().add(getLeftSidePanel(), "left");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSplitPane;
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 2:19:40 PM)
 * @return java.lang.Object
 */
private Object getTreeItem()
{
	return ycClass.getTreeItem();
}
/**
 * Return the TreeViewPanel property value.
 * @return com.cannontech.common.gui.util.TreeViewPanel
 */
private com.cannontech.common.gui.util.TreeViewPanel getTreeViewPanel() {
	if (ivjTreeViewPanel == null) {
		try {
			ivjTreeViewPanel = new com.cannontech.common.gui.util.TreeViewPanel();
			ivjTreeViewPanel.setName("TreeViewPanel");
			// user code begin {1}
			ivjTreeViewPanel.addTreeSelectionListener(this);
			ivjTreeViewPanel.getSortByComboBox().addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTreeViewPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/2001 2:02:57 PM)
 * @return java.lang.String
 */
private static String getVersion()
{
	return  (com.cannontech.common.version.VersionTools.getYUKON_VERSION() + VERSION);
}
/**
 * Return the YCCommandMenu property value.
 * @return com.cannontech.yc.gui.menu.YCCommandMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.yc.gui.menu.YCCommandMenu getYCCommandMenu() {
	if (ivjYCCommandMenu == null) {
		try {
			ivjYCCommandMenu = new com.cannontech.yc.gui.menu.YCCommandMenu();
			ivjYCCommandMenu.setName("YCCommandMenu");
			ivjYCCommandMenu.setText("Command");
			// user code begin {1}
			javax.swing.JMenuItem item;
			for (int i = 0; i < ivjYCCommandMenu.getItemCount(); i++)
			{
				item = ivjYCCommandMenu.getItem(i);

				if (item != null)
					ivjYCCommandMenu.getItem(i).addActionListener(this);
			}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYCCommandMenu;
}
/**
 * Return the YCFileMenu property value.
 * @return com.cannontech.yc.gui.menu.YCFileMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.yc.gui.menu.YCFileMenu getYCFileMenu() {
    if (ivjYCFileMenu == null) {
        try {
            ivjYCFileMenu = new com.cannontech.yc.gui.menu.YCFileMenu();
            ivjYCFileMenu.setName("YCFileMenu");
            ivjYCFileMenu.setText("File");
            // user code begin {1}
            javax.swing.JMenuItem item;

            for (int i = 0; i < ivjYCFileMenu.getItemCount(); i++) {
                item = ivjYCFileMenu.getItem(i);

                if (item != null)
                    ivjYCFileMenu.getItem(i).addActionListener(this);
            }
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjYCFileMenu;
}
/**
 * Return the YCHelpMenu property value.
 * @return com.cannontech.yc.gui.menu.YCHelpMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.yc.gui.menu.YCHelpMenu getYCHelpMenu() {
	if (ivjYCHelpMenu == null) {
		try {
			ivjYCHelpMenu = new com.cannontech.yc.gui.menu.YCHelpMenu();
			ivjYCHelpMenu.setName("YCHelpMenu");
			ivjYCHelpMenu.setText("Help");
			// user code begin {1}
			javax.swing.JMenuItem item;

			for (int i = 0; i < ivjYCHelpMenu.getItemCount(); i++) {
				item = ivjYCHelpMenu.getItem(i);

				if (item != null)
					ivjYCHelpMenu.getItem(i).addActionListener(this);
			}

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYCHelpMenu;
}
/**
 * Return the YCViewMenu property value.
 * @return com.cannontech.yc.gui.menu.YCViewMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.yc.gui.menu.YCViewMenu getYCViewMenu() {
	if (ivjYCViewMenu == null) {
		try {
			ivjYCViewMenu = new com.cannontech.yc.gui.menu.YCViewMenu();
			ivjYCViewMenu.setName("YCViewMenu");
			ivjYCViewMenu.setText("View");
			// user code begin {1}
			javax.swing.JMenuItem item;

			for (int i = 0; i < ivjYCViewMenu.getItemCount(); i++) {
				item = ivjYCViewMenu.getItem(i);

				if (item != null)
					ivjYCViewMenu.getItem(i).addActionListener(this);
			}

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYCViewMenu;
}
/**
 * Return the YukonCommanderJMenuBar property value.
 * @return javax.swing.JMenuBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuBar getYukonCommanderJMenuBar() {
	if (ivjYukonCommanderJMenuBar == null) {
		try {
			ivjYukonCommanderJMenuBar = new javax.swing.JMenuBar();
			ivjYukonCommanderJMenuBar.setName("YukonCommanderJMenuBar");
			ivjYukonCommanderJMenuBar.add(getYCFileMenu());
			ivjYukonCommanderJMenuBar.add(getYCViewMenu());
			ivjYukonCommanderJMenuBar.add(getYCCommandMenu());
			ivjYukonCommanderJMenuBar.add(getYCHelpMenu());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYukonCommanderJMenuBar;
}
/**
 * Insert the method's description here.
 * Creation date: (12/20/2001 5:12:47 PM)
 * @param msg com.cannontech.message.dispatch.message.DBChangeMsg
 */
public void handleDBChangeMsg(com.cannontech.message.dispatch.message.DBChangeMsg msg, com.cannontech.database.data.lite.LiteBase object)
{
	//com.cannontech.database.cache.DefaultDatabaseCache.getInstance().handleDBChangeMessage((com.cannontech.message.dispatch.message.DBChangeMsg)msg);
	
	java.awt.Frame frame = null;
	java.awt.Cursor savedCursor = null;

	try
	{
		// Cursor set to WAIT during the route combo box update.
		frame = com.cannontech.common.util.CtiUtilities.getParentFrame( getRootPane().getContentPane());
		savedCursor = getRootPane().getCursor();
		getRootPane().setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
		// Refresh the device tree and the route combo box on database changes.
		restoreCurrentTree();
		setRouteModel();
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}
	finally
	{
		getRootPane().setCursor( savedCursor);
	}

}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private static void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Set up a connection to dispatch, for database changes.
 * In the future we could also get point changes and dynamically
 * update a graph.
 * Creation date: (10/31/00 12:18:31 PM)
 */
private void initDispatchConnection() 
{
	String host = null;
	int port;

	try
	{
		java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("config");
		host = bundle.getString("dispatch_machine");
		port = (new Integer(bundle.getString("dispatch_port"))).intValue();
	}
	catch (java.util.MissingResourceException mre)
	{
		host = "127.0.0.1";
		port = 1510;

		com.cannontech.clientutils.CTILogger.info("Missing dispatch_machine and/or dispatch_port from db.properties file");
		com.cannontech.clientutils.CTILogger.info("Defaulted to host = " + host + ", port = " + port);
	}
	catch (NumberFormatException nfe)
	{
		port = 1510;
		com.cannontech.clientutils.CTILogger.info("Number format exception on dispatch_port");
		com.cannontech.clientutils.CTILogger.info("Defaulted to port = " + port);
	}
	
	com.cannontech.clientutils.CTILogger.info("Connecting to Dispatch:  " + host + " port:  " + port);

	connToDispatch = new com.cannontech.message.dispatch.ClientConnection();

	com.cannontech.message.dispatch.message.Registration reg =	new com.cannontech.message.dispatch.message.Registration();
	reg.setAppName(YC_TITLE);
	reg.setAppIsUnique(0);
	reg.setAppKnownPort(0);
	reg.setAppExpirationDelay(1000000);

	connToDispatch.setHost(host);
	connToDispatch.setPort(port);
	connToDispatch.setAutoReconnect(true);
	connToDispatch.setRegistrationMsg(reg);
	try
	{
		connToDispatch.connectWithoutWait();
	}
	catch (java.io.IOException e)
	{
		e.printStackTrace();
	}
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("YukonCommander");
		setSize(841, 647);
		setJMenuBar(getYukonCommanderJMenuBar());
		setContentPane(getJFrameContentPane());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	inThread = new Thread(this);
	inThread.start();

	//--------Setup treeViewPanel and tree models-------------	
	//This model is created in a weird way.  We can do the model creation similar for all
	// the models except for the Device model.  In order to use a different constructor
	// that shows the device types without their points, we have to specifically look for
	// that model type.  Create a new model for that type (false sets showPoints off).
	// For all other 'normal' types, we can follow through with OO and use the same
	// constructor template (no parameters).
	com.cannontech.database.model.LiteBaseTreeModel[] models =
		new com.cannontech.database.model.LiteBaseTreeModel[treeModels.length];
	
	for (int i = 0; i < models.length; i++)
	{
		if( treeModels[i] == ModelFactory.DEVICE)
			models[i] =  new com.cannontech.database.model.DeviceTreeModel(false);
		else if ( treeModels[i] == ModelFactory.LMGROUPS)
			models[i] = new com.cannontech.database.model.LMGroupsModel(false);
		else if ( treeModels[i] == ModelFactory.CAPBANKCONTROLLER )
			models[i] = new com.cannontech.database.model.CapBankControllerModel(false);
		else		
			models[i] = ModelFactory.create(treeModels[i]);
	}

	//serial and route panel visible only when first item in tree is Versacom Serial #
	if (treeModels[0] != ModelFactory.EDITABLEVERSACOMSERIAL)
		enableSerialAndRoute(false);

	//add listeners to treeViewPanel Objects
	getTreeViewPanel().setTreeModels(models);
		
	setRouteModel(); //fill route combo box

	com.cannontech.database.model.EditableVersacomSerialModel.setSerialNumberVector( new Vector());
	
	com.cannontech.database.cache.DefaultDatabaseCache.getInstance().addDBChangeListener(this);
	//DBChangeMessageListener dbChangeMessageListener = new DBChangeMessageListener();
	//dbChangeMessageListener.start();

	addWindowListener(new java.awt.event.WindowAdapter(){
		public void windowClosing(java.awt.event.WindowEvent e){ 
				exit();
		};
	});
	// user code end
}
/**
 * Set up a connection to dispatch, for database changes.
 * In the future we could also get point changes and dynamically
 * update a graph.
 * Creation date: (10/31/00 12:18:31 PM)
 */
private void initPorterConnection() 
{
	String host = null;
	int port;

	try
	{
		java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("config");
		host = bundle.getString("porter_machine");
		port = Integer.parseInt(bundle.getString("porter_port"));
	}
	catch (java.util.MissingResourceException mre)
	{
		host = "127.0.0.1";
		port = 1540;
		com.cannontech.clientutils.CTILogger.info("Missing porter_machine and/or porter_port from db.properties file");
		com.cannontech.clientutils.CTILogger.info("Defaulted to host = " + host + ", port = " + port);
	}
	catch (NumberFormatException nfe)
	{
		port = 1540;
		com.cannontech.clientutils.CTILogger.info("Number format exception on dispatch_port");
		com.cannontech.clientutils.CTILogger.info("Defaulted to port = " + port);
	}
	
	com.cannontech.clientutils.CTILogger.info("Connecting to Porter:  " + host + " port:  " + port);

	connToPorter = new com.cannontech.message.porter.ClientConnection();

	connToPorter.setHost(host);
	connToPorter.setPort(port);
	connToPorter.setAutoReconnect(true);

	try
	{
		connToPorter.connectWithoutWait();
	}
	catch (java.io.IOException e)
	{
		e.printStackTrace();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 3:13:16 PM)
 */
private boolean isValidSetup()
{
	//setCommand((String) getCommandPanel().getExecuteCommandComboBoxTextField().getText().trim());
	if( connToPorter.isValid() )
	{
		if( ycClass.isDirectSend() )
		{
			//bypassing all of these checks, the user MUST Know what they are doing I guess!.
			getCommandPanel().getExecuteCommandComboBoxTextField().requestFocus();
			//enter();
			return true;	
		}
		else if( getTreeItem() == null )
		{
			getCommandLogPanel().addLogElement(" ** Warning: Please make a selection from the tree.");
		}
		else if( getCommand() == null || getCommand().length() == 0 )
		{
			getCommandLogPanel().addLogElement(" ** Warning: No command is specified");
		}
		else if( getTreeViewPanel().getSelectedNode().getParent() == null )
		{
			getCommandLogPanel().addLogElement(" ** Warning: Please select a specific tree item");
		}
		else
		{
			//confirm command execution if needed
			if( ycClass.getYCDefaults().getConfirmCommandExecute())
			{
				String message = "Execute '"+ getCommand() +"' -- Are You Sure?";
				int response = areYouSure(message, javax.swing.JOptionPane.QUESTION_MESSAGE);
				if( response == javax.swing.JOptionPane.OK_OPTION )
				{
					getCommandPanel().getExecuteCommandComboBoxTextField().requestFocus();
					return true;
				}
			}
			else
			{
				//enter();
				getCommandPanel().getExecuteCommandComboBoxTextField().requestFocus();
				return true;
			}
		}
	}
	else
	{
		getCommandLogPanel().addLogElement(" ** Warning: Not connected to port control service **");
	}
	return false;
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.KeyEvent
 */
public void keyPressed(KeyEvent event) {
	
	if( event.getKeyCode() == KeyEvent.VK_ENTER && event.getSource() == getCommandPanel().getExecuteCommandComboBoxTextField())
	{
		setCommand((String) getCommandPanel().getExecuteCommandComboBoxTextField().getText().trim());		
		if( isValidSetup())
		{
			enter();
		}
	}
	
	else if( event.getKeyCode() == KeyEvent.VK_ENTER && event.getSource() == getSerialRoutePanel().getSerialTextField())
	{
		serialNumberAction();
	}
	//else if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
	//{
		//getAdvOptsPanel().setVisible(false);
		//getAdvOptsPanel().dispose();
		//getLocateRouteDialog().dispose();
		//getLocateRouteDialog().setVisible(false);
	//}
	
	
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.KeyEvent
 */
public void keyReleased(KeyEvent event) {
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.KeyEvent
 */
public void keyTyped(KeyEvent event) {
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args)
{
	try
	{
		System.setProperty("cti.app.name", "Commander");
		javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName());

		YukonCommander ycClient;
		ycClient = new YukonCommander();
		
		com.cannontech.common.gui.util.SplashWindow splash = new com.cannontech.common.gui.util.SplashWindow( ycClient, "ctismall.gif", "Loading resources...", new java.awt.Font("dialog", 0, 14), java.awt.Color.black, java.awt.Color.black, 1 );
		ycClient.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("CommanderIcon.gif"));
		ycClient.setTitle(YC_TITLE);
		splash.setDisplayText("Opening connection to database...");

		//Test that a connection can actually be made.
		java.sql.Connection c = null;
		try
		{
			c = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());

			if (c == null)
			{
				splash.setDisplayText("Error connecting to database, closing");
				Thread.sleep(2000);		//let them see
				System.exit(0);
			}
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			splash.setDisplayText("Error connecting to database, closing");
			Thread.sleep(2000);		//let them see
			System.exit(0);
		}
		finally
		{
			if (c != null)
				c.close();
		}

		ycClient.initialize();
		ycClient.initDispatchConnection();
		ycClient.initPorterConnection();
		ycClass.setConnToPorter(ycClient.connToPorter);

		//set the app to start as close to the center as you can....
		//  only works with small gui interfaces.
		java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		ycClient.setLocation((int)(d.width * .07),(int)(d.height * .07));
		ycClient.show();
				
	}
	catch (Throwable exception)
	{
		System.err.println("Exception occurred in main() of javax.swing.JFrame");
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 3:17:39 PM)
 */
private void print()
{
	java.awt.Frame parent = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
	java.awt.PrintJob pj = java.awt.Toolkit.getDefaultToolkit().getPrintJob(parent, "YC Output", null);
	java.awt.Graphics printGraphics = pj.getGraphics();
	getOutputTextArea().printAll(printGraphics);
	printGraphics.dispose();
	pj.end();
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 3:10:42 PM)
 */
private void reloadDevices()
{
	java.awt.Frame frame = null;
	java.awt.Cursor savedCursor = null;
	
	try
	{
		frame = com.cannontech.common.util.CtiUtilities.getParentFrame(getRootPane());
		savedCursor = frame.getCursor();
		frame.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
		
		//load all data from the database to the cache for update to catch anything new.
		com.cannontech.database.cache.DefaultDatabaseCache.getInstance().loadAllCache();
		
		restoreCurrentTree();
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}
	finally
	{
		frame.setCursor( savedCursor );
	}
}
/**
 * Insert the method's description here.
 * Reselects the selected item in the tree, should the tree be refreshed from cache.
 * Creation date: (9/12/2001 8:59:15 AM)
 */
private void restoreCurrentTree()
{
	com.cannontech.common.gui.util.TreeViewPanel tvp = getTreeViewPanel();
	Object item = getTreeItem();
	
	if (tvp != null)
		tvp.refresh();
		
	if (item instanceof com.cannontech.database.data.lite.LiteBase)
	{
		com.cannontech.database.data.lite.LiteBase lb = (com.cannontech.database.data.lite.LiteBase) item;

		tvp.selectLiteBase( 
				new javax.swing.tree.TreePath ( tvp.getTree().getModel().getRoot() ),
				lb );
				//lb.getLiteType() , lb.getLiteID() );

	}
	else if( item instanceof String)
	{
		tvp.selectByString( item.toString());
	}
	else
	{
		tvp.selectObject(null);
		com.cannontech.clientutils.CTILogger.info("WARNING:  nothing reselected in the tree, dbChangeMessageListener is missing an instanceof");
	}
	// ** Add else if... here to include other objects that may be in the treeViewPanel
}
/**
 * Run simply waits for Return messages to appear on our connection and
 * This should be done without using a separate thread but its not.  rev 2 baby
 * adds them to the output pane.
 */
public void run()
{
	class WriteOutput implements java.lang.Runnable
	{
		public String output = null;
		public WriteOutput(String out)
		{
			output = out;
		}
		
		public void run()
		{
			YukonCommander.this.appendOutputTextArea(output);
			//YukonCommander.this.scrollRectToVisible(  new java.awt.Rectangle(0,getOutputTextArea().getHeight()-1,1,1));
		}
	}
	
	try
	{
		for (;;)
		{
			Object in;
			if (connToPorter != null && connToPorter.isValid())
			{
				if ((in = connToPorter.read()) != null)
				{
					if (in instanceof com.cannontech.message.porter.message.Return)
					{
						com.cannontech.message.porter.message.Return ret = (com.cannontech.message.porter.message.Return) in;
						//appendOutputTextArea("\n-> Executing command -> " + ret.getCommandString() +"\n");							
						String output = null;

						java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMM d HH:mm:ss a z");					
						output = "\n["+ format.format(ret.getTimeStamp()) + "]-{" + ret.getUserMessageID() +"} Return from \'" + ret.getCommandString() + "\'\n";
																				
						for (int i = 0; i < ret.getVector().size(); i++)
						{
							Object o = ret.getVector().elementAt(i);
							if (o instanceof com.cannontech.message.dispatch.message.PointData)
							{
								com.cannontech.message.dispatch.message.PointData pd = (com.cannontech.message.dispatch.message.PointData) o;
								if ( pd.getStr().length() > 0 )
									output += pd.getStr() + "\n";
							}
							else
								output += "Warning, was expecting a com.cannontech.message.dispatch.message.PointData but recieved something else.\n";
						}

						output += "->  " + ret.getResultString() + "\n";		
						javax.swing.SwingUtilities.invokeLater( new WriteOutput(output) );

						synchronized ( YukonCommander.class )
						{
							if( ret.getExpectMore() == 0)	//Only send next message when ret expects nothing more
							{
								
								//Break out of this outer loop.
								doneSendMore:
								if( ycClass.sendMore == 0)
								{
									// command finished
								}
								else if ( ycClass.sendMore > 0)
								{
									ycClass.sendMore--;	//decrement the number of messages to send
									if (ycClass.getLoopType() == ycClass.LOOPLOCATE)
									{
								 		if( ycClass.getAllRoutes()[ycClass.sendMore] instanceof com.cannontech.database.data.lite.LiteYukonPAObject)
										{
											com.cannontech.database.data.lite.LiteYukonPAObject rt = (com.cannontech.database.data.lite.LiteYukonPAObject) ycClass.getAllRoutes()[ycClass.sendMore];
											while( rt.getType() == com.cannontech.database.data.pao.PAOGroups.ROUTE_MACRO
												&& ycClass.sendMore > 0)
											{
												ycClass.sendMore--;
												rt = (com.cannontech.database.data.lite.LiteYukonPAObject) ycClass.getAllRoutes()[ycClass.sendMore];
											}
											// Have to check again because last one may be route_ macro
											if(rt.getType() == com.cannontech.database.data.pao.PAOGroups.ROUTE_MACRO)
												break doneSendMore;

											ycClass.loopReq.setRouteID(rt.getYukonID());
											//ycClass.loopReq.setCommandString(getCommand() + " select route id " + r.getYukonID());
										}
									}
									connToPorter.write( ycClass.loopReq);	//do the saved loop request
								}
								else
								{
									output = "Command cancelled\n";
									javax.swing.SwingUtilities.invokeLater( new WriteOutput(output));
								}
							}
						}
//						else
//						{
//							com.cannontech.clientutils.CTILogger.info("** From Porter: " + ret.getResultString() + "\n");
//						}
					}
//					else
					//appendOutputTextArea(" ELSE from Return");	
				}
//				else
//				appendOutputTextArea(" ELSE from connection");			
			}
			else
			{
				Thread.sleep(1000);
			}
		}
	}
	catch (InterruptedException e)
	{
		e.printStackTrace();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 3:19:50 PM)
 */
private void save()
{
	//This will need to be updated someday for a new version of swing
	java.awt.Frame parent = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
	javax.swing.JFileChooser  fileChooser = new javax.swing.JFileChooser();
	
	if( fileChooser.showSaveDialog(parent) == javax.swing.JFileChooser.APPROVE_OPTION )
	{
		java.io.FileWriter fWriter = null;
		try
		{
			fWriter = new java.io.FileWriter( fileChooser.getSelectedFile().getPath(), true );
			java.io.PrintWriter pWriter = new java.io.PrintWriter( fWriter );

			pWriter.print( getOutputTextArea().getText() );
			fWriter.close();
		}
		catch( java.io.IOException e )
		{				
			javax.swing.JOptionPane.showMessageDialog( parent, "An error occured saving to a file", "Error", javax.swing.JOptionPane.ERROR_MESSAGE );
		}
		finally
		{
			try
			{
				if( fWriter != null )
					fWriter.close();
			} 
			catch( java.io.IOException e2 )
			{
				e2.printStackTrace();
			}
		}

	}	 	
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 3:12:12 PM)
 */
private void serialNumberAction()
{
	com.cannontech.common.gui.util.TreeViewPanel t =  getTreeViewPanel();
	String tempSerialNumber = (String) getSerialRoutePanel().getSerialTextField().getText().trim();

	// If invalid serialNumber entered ( null or length 0), set selected tree item to null.
	if(	tempSerialNumber == null || tempSerialNumber.length() == 0 )
	{	
		setSerialNumber(null);
		t.getTree().getSelectionModel().setSelectionPath( null );
		return;
	}
	// Else if not already in serial vector, add it to the serialVector.
	else if(!com.cannontech.database.model.EditableVersacomSerialModel.getSerialNumberVector().contains(tempSerialNumber) )
	{	
		com.cannontech.database.model.EditableVersacomSerialModel.getSerialNumberVector().add(tempSerialNumber);

		// Refresh the tree selection.
		((com.cannontech.database.model.EditableVersacomSerialModel) t.getSelectedTreeModel()).update();

		// Clear serial number text field.
		if( getSerialRoutePanel().getSerialTextField() instanceof javax.swing.JTextField )
			getSerialRoutePanel().getSerialTextField().setText("");

	}

	setSerialNumber( tempSerialNumber );

	updateCurrentSelection( getSerialNumber() );
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/2001 12:07:43 PM)
 * @param newCommand java.lang.String
 */
private void setCommand(String newCommand)
{
	ycClass.setCommand(newCommand);
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2002 2:12:46 PM)
 * @param typeSelected int
 */
private void setModelType(int typeSelected)
{
	ycClass.setModelType( typeSelected);
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2002 4:36:23 PM)
 * @param newRoute java.lang.Object
 */
private void setRoute(Object newRoute)
{
	ycClass.setRoute(newRoute);
}
/**
 * Creation date: (3/9/2001 4:30:36 PM)
 * @author Stacey Nebben	Added for version 1.5
 * -----------------------------------------------------------------------------------------
 * Fills the routeComboBox with all of the created routes.
 * Retrieves the routes from cache memory and then sorts them (alphabetically).
 * Removes all items in the combo box (applicable when new routes exist)
 * Add a default of All Routes as the first item in the combo box
 * Add the database routes to the combo box.
 */
private void setRouteModel()
{
	if( getSerialRoutePanel().getRouteComboBox() == null )
		return; 

	int index = 0;	//Index in the routeComboBox to be selected
	
	// Save the routeComboBox's currently selected item.
	Object saveSelectedRoute = getSerialRoutePanel().getRouteComboBox().getSelectedItem();
	
	// Get an instance of the cache.
	com.cannontech.database.cache.DefaultDatabaseCache cache =
				com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List routes = cache.getAllRoutes();
		java.util.Collections.sort( routes, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );

		getSerialRoutePanel().getRouteComboBox().removeAllItems();	
		getSerialRoutePanel().getRouteComboBox().addItem("All Default Routes");	//default first item

		Object[] tempRouteArray = new Object[routes.size()];

		for( int i = 0; i < routes.size(); i++ )
		{
			getSerialRoutePanel().getRouteComboBox().addItem( routes.get(i));
			tempRouteArray[i] = routes.get(i);
			
			if(saveSelectedRoute != null && routes.get(i).toString() == saveSelectedRoute.toString())
				index = i + 1;
		}
		
		ycClass.setAllRoutes(tempRouteArray);
		
	}

	// Set the newly selected item to the index of the previously selected item.
	//  If the prev item doesn't exist, set selected item to the 0 index (default).
	getSerialRoutePanel().getRouteComboBox().setSelectedIndex( index );
		
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 4:17:54 PM)
 * @param newSerialNumber java.lang.String
 */
private void setSerialNumber(String newSerialNumber)
{
	ycClass.setSerialNumber(newSerialNumber);
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 2:21:41 PM)
 * @param selectedTreeItem java.lang.Object
 */
private void setTreeItem(Object newSelectedItem)
{
	ycClass.setTreeItem(newSelectedItem);
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 3:08:57 PM)
 */
private void sortByComboBoxAction()
{
	////////////////////////////////////////////////////////////////////////////////////////////
	// Serial Number/Route -- enable/disable (with Versacom Serial tree)
	setModelType( treeModels[getTreeViewPanel().getSortByComboBox().getSelectedIndex()] );
	
	if (getModelType() == ModelFactory.EDITABLEVERSACOMSERIAL)
	{
		enableSerialAndRoute(true);
		getSerialRoutePanel().getSerialTextField().requestFocus();
	}
	else
	{
		enableSerialAndRoute(false);
	}
	////////////////////////////////////////////////////////////////////////////////////////////
	// Locate Route Menu Item -- enable
	getYCCommandMenu().locateRoute.setEnabled(false);	//init to false, will change below if valid state.
	if (getModelType() == ModelFactory.DEVICE)
	{
		if( getTreeItem() instanceof com.cannontech.database.data.lite.LiteYukonPAObject)
		{
			com.cannontech.database.data.lite.LiteYukonPAObject lpao = (com.cannontech.database.data.lite.LiteYukonPAObject) getTreeItem();
			if( com.cannontech.database.data.device.DeviceTypesFuncs.isMCT(lpao.getType()) ||
				com.cannontech.database.data.device.DeviceTypesFuncs.isRepeater(lpao.getType()))
			{
				getYCCommandMenu().locateRoute.setEnabled(true);
			}
		}
	}
	////////////////////////////////////////////////////////////////////////////////////////////	
	if (getTreeItem() == null )
	{
		getCommandPanel().getAvailableCommandsComboBox().removeAllItems();
		getCommandPanel().getExecuteCommandComboBoxTextField().setText("");
	}

}
/**
 * Creation date: (3/15/2001 2:28:56 PM)
 * @author Stacey Nebben	Added for version 1.5
 * -----------------------------------------------------------------------------------------
 * Updates the current tree selection to the parameter passed in.
 * Used for synchronization.
 */
private void updateCurrentSelection(Object currentSelection)
{
	com.cannontech.common.gui.util.TreeViewPanel t =  getTreeViewPanel();
		
	javax.swing.tree.TreePath selectedPath = 
				com.cannontech.common.util.CtiUtilities.getTreePath( t.getTree(), currentSelection);
	
	t.getTree().getSelectionModel().setSelectionPath( selectedPath );

}
/**
 * -----------------------------------------------------------------------------------------
 * Method for treeSelectionEvents.
 * If nothing is selected in the tree, we return (do nothing).
 * Gets the appropriate configFile for the object type selected in the treeViewPanel tree.
 * Parses the keys and Values in the configFile and fills the CommandComboBox accordingly.
 * Only updates the command boxes when the selected tree item differs in object type from
 *  the previously selected tree item.
 */
public void valueChanged(TreeSelectionEvent event)
{

	com.cannontech.message.util.ConfigParmsFile cpf = null;
	setModelType( treeModels[getTreeViewPanel().getSortByComboBox().getSelectedIndex()] );
	setTreeItem( getTreeViewPanel().getSelectedItem());

	if (getTreeItem() == null)
		return;

	// If the parent value of the tree is invalid, return.
	if (getTreeViewPanel().getSelectedNode().getParent() == null)
		return;

	// If serial number, use the serial number file string constant.
	if ( getModelType() == ModelFactory.EDITABLEVERSACOMSERIAL)
	{
		setSerialNumber( (String) getTreeItem());
		getSerialRoutePanel().setSerialNumberText( getSerialNumber().toString() );
		cpf = ycClass.getConfigFile( ycClass.SERIALNUMBER_FILENAME );
	}
	else if( getModelType() == ModelFactory.COLLECTIONGROUP ||
			getModelType() == ModelFactory.TESTCOLLECTIONGROUP )
	{
		cpf = ycClass.getConfigFile( ycClass.DEFAULT_FILENAME );
	}
	// Else if a lite object, using the device type selected. 
	else if ( getTreeItem() instanceof com.cannontech.database.data.lite.LiteBase)
	{
		com.cannontech.database.db.DBPersistent dbp = com.cannontech.database.data.lite.LiteFactory.createDBPersistent( (com.cannontech.database.data.lite.LiteBase) getTreeItem());
				
		if (dbp == null)
			return;

		cpf = ycClass.getConfigFile( dbp );

		////////////////////////////////////////////////////////////////////////////////////////////
		// Locate Route Menu Item -- enable
		getYCCommandMenu().locateRoute.setEnabled(false);	//init to false, will change below if valid state.
		if( getTreeItem() instanceof com.cannontech.database.data.lite.LiteYukonPAObject)
		{
			com.cannontech.database.data.lite.LiteYukonPAObject lpao = (com.cannontech.database.data.lite.LiteYukonPAObject) getTreeItem();
			if( com.cannontech.database.data.device.DeviceTypesFuncs.isMCT(lpao.getType()) ||
				com.cannontech.database.data.device.DeviceTypesFuncs.isRepeater(lpao.getType()))
			{
				getYCCommandMenu().locateRoute.setEnabled(true);
			}
		}
	
	}
	else //use the "default" filename
	{
		cpf = ycClass.getConfigFile( ycClass.DEFAULT_FILENAME);
	}

	if (!cpf.exists())
	{
		getCommandLogPanel().addLogElement(" *** The command file: " + cpf+ "     Does not exist. ***");
		getCommandPanel().getAvailableCommandsComboBox().removeAllItems();
		getCommandPanel().getExecuteCommandComboBox().setSelectedItem(""); //clear text field
		return;
	}

	com.cannontech.clientutils.CTILogger.info(" COMMAND FILE: " + cpf);
	
	// Only update command boxes on a change in device type/ file name.
	if( !cpf.toString().equalsIgnoreCase( ycClass.getCommandFile().toString() ) )
	{
		ycClass.setCommandFile( cpf.toString() );
		
		// Collect the config file keys and values (a.k.a. Commands and CommandStrings).		
		String[][] keysAndValues = cpf.getKeysAndValues();

		// Clear out the old, get ready for the new!
		getCommandPanel().getAvailableCommandsComboBox().removeAllItems();
		getCommandPanel().getExecuteCommandComboBox().setSelectedItem("");
			
		// Add the keys to the availableCommandsComboBox, first one is default ("Select A Command")
		getCommandPanel().getAvailableCommandsComboBox().addItem(" <Select A Command>" );
		for (int i = 0; i < keysAndValues[0].length; i++)
			getCommandPanel().getAvailableCommandsComboBox().addItem(keysAndValues[0][i]);
	}
	return;
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G78F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8FDCD4E536142838E11AD2CE06AB259956242CD211D131336EAE3B393B7616DE6E7E5C373659FB547A4AFECB3BD4D617FFA3A6AAAAAAA6959519A6A1A0EEC4EC0522E9CD468A25EEEEB8B0171901E1E69C6E4000286F1C6F3B674E65B2B308456377BB5C7B1D6F4F794E793E6F7C3BF72ED070FACCCEC6E601A0E46488725F7B9902907DEFC1784B725CE78897A69A0C026A6F8D000DC24311EC38CE
	043AFA034118AFBC32B6974AB3212CCFB29837417DE6616932D57F021BA0F4D220EE671CAD3921697C785FC4E7B5AB2FDB1B875775G310043EB1A08745FDEDBA8639749F8020CCC4035E11BCF56964938EAA8B7GC4GC437717671F0DD43A80F2E29165B1D3EA6C6C87B65BB196BF09C55188485D65E37DA3E968BF3B316D911161D4D937B38219C8B004CE7890D475E05EBEDC3077523D6D7E7D92B5969F4B9A53155D6565BDAE6755AEBEACEF9BBDC4E032E4ECE3353A2FAE08EB69C6FC7CC3ED9GA5D0CEE5
	381ACE2491837789G0B95FC06937125F6D89B0053F6626B9F6F544A73BEF9EE2E306AE1E75606A0FCC9F64ECF345D4FD7147739555B599AE4B4232C17C05DAA007CF603318A2089C082584156BE77DB64332EC5C2CEDC4EBA377B0043627942E5919DF0DFD383B5B26E38586A72D884012F2FEBEDE9007904825E6F7C307BF119446E2335569F1FA5AC1D7FF9666AGB2D99ADB1E99594EE5B1335DAF93CE7B4E203497368F5C0B83E91F905434D70CF1E56584209D7F506E4CA572B8ABD4340BBAF02D6730BD10
	877799727E60788406BF234273FD798943E3598DF5A99D34B7CE2E27391832278A79952600F250996AB22B9DFC8E8D8E6DDC3CEC5F75BBB84D8CE5AE9798BEC20567F3D96D24B39285F569GFCFF3DEC46F3FA3A53E07481CC840885C886D88E105EC9FB6C1F5F6E3D44BD56E076C8873CF6879EA85E7765662DF095FD1247FC5459A5191D2D6257E60F5D5C62903B84394D66E614C14B00FE05EC6F37820DCFC52768B1CBF61793C6B7F70A12686962ED5EE85E08EDBCA22BED32F70AC02053AD50383B1BB74355
	E16E123EF2DB4C1268EF416BAB1AF1CD3A4DDF4308B0G5CF3393C5F0272AAF49A0C65G1FB8C98EFAB65E672247A9BAEAEACCAED707575D041C88931DA867DFB15D5184778EA749F8DFAB62D2203CC299675166CD2AF16A6D2DB8FD3327174ED8FCA76AE241C5FB66491334E76A575DAC14BE74D940BD03D06462FB254A25BDBB7731B1F1DE3CFC387364B04E30608AFC3EC26B0A66E151489AC65B754ADA5DE84135BA8D73738168DCA463574E9473BDFB405CA552AE7D52839BCC3478A54E67F98E17DE1EF338
	6AFE1DF3B8739FAEF09ABBC7148617C501996D0F58ADBA25FA51AD0239F3CADDF25D8EB36EB179FC363914BEEFB03C9B4E73C1B8C0DD8A7E9DB31EB933A4F96CADDEC9EC94A5496E347A6B77B2DADD5E9637DF95902D3F1BD16F3AFA1CE8EEE55C07E6BC8B2DAE27D3E40B22F4116B7F414E226890BBE15287DDDE27447187DBF0BFF7083D32AE7834851D1AEE33430BFA057BGF166354803475E89CB0D2A898AF8CE17ABBE8CC29C6C9974E5D2601A2A1C391E37684C15745DA8A4B53F1D9948E668EE2E4FBC62
	66E763345BFF66781C2ADB834F7DA32693E4FCD79643C534E3DF2DCF4277786FC5266FE17C09G31EE5A638BDA311E74083C398F3A9C8EAECAC54637D872F1CD4433DE9154AFAA2C4F95394D31EC5254FDAD4B10575FE9C199BAD43A0E4B7659D7D0064C3FEAB7F71BFDE5DDBD308FEAEABEBB0CBA1A7BD2477210CFEC1F64A61F69F7DB1017F4DC073386632E33A46F8757114F945591A3AC1B71CAD0DF701333DC4E1633FEF9970FA21F29444A7CE3280BBFCBF22A9055FA3B2E2B4B55EAE7A6C366F11928DE8F
	0EFBCCFC992EEDF6CF17F4945642C77278330873C51BA22365F35C18974AAFCABEBBD0FFAE05B23759DFECC5F92CBC2B75ABCEB03C60517A95FF5CC2FEC5BE54C99E32B3E69B7395211C861057E5B016G5483D83AC88EDDED7B04A16C78D17011BDCE3323F14081A6FBEA4D62E7CEAE3FE74D21B387D7EE835B591A0781CD9DG47BD3E8EF109D0DE5CC57627F1AB1E5FB4257CEFD6AE10287C58162174A2767DEE3A7745A1E98C57A68D563D6D36603A772CAD306E6D3185563D67ED9BB99E46D0FBDAD4BF5A96
	DAB70F3785524D7CBC7F28874FF99B9CDE68526896DB05C5922E5D95F924AB2B21EC82F0GC4826881386E9256637AFE760568749EB0FB9CDE6C37EDBD75AB00F229172FD513FC657DCA3F4179F20DF19023B3E44B09FB096B692B372A75F43C177BD5CB603A82006B18DB58FE43B3125DEDB056F6A36E213C824D9AEEGA64B604A6BD72FD357639C5E9654759F8C28473518ECD4577FE2C0BD4AEF025C1F6B32875EA6DD366A01D841F14787C1F5D9F8B717C72CFCED787AAD2164874F2F66E77D63756437AF03
	7A429E0AED2F7C6095A1206E15D7C090382E6DE77D9B201F8DA02C07F42DEFA74DCFBFEB2AE08A7BBC607C16B5D5E66AFA783C92FB7C3AB69662D3BE0F3F6C8EE283F0959DFCFDFE3615ECC0FA8F59004905B82FF21F41F884600C0F4E3CBE248E8D841C1F9F2FA6FE62975CA4A40CF9B6E3ED90F97BFC1C8F1DFCCDEDFA0B61398E0DE8C7FE1221EEB9C0EE2F41D88CD085E06AA59DDAEC47B90226BCE431CBF512E4EE35B12BC18A74EB3B5863C763C1623C9DDAFFF971F663307BF17B31122D7D19DEEEBF56A8
	7897983EDF0567FB6DCA0647328E6AD67512DDF92483E5F2264FE06C8708825083AC82C86DA319586D654290F6254965B60192F4285496676F012DDA7ED6757975FA7E3941BA1E7331FABB4E37649C67A3C741EFE278EA951E73F7BD5BF3D8B6C1DD5CB9A5BE6B4433DC508F319EC093008D203F1FF81BE3FF137396E02D8FD92C2837B02A15C847DE539EDC5F6B5A070E05EFECC73F2345A5C92ECE9CDF71615AD1DE124B4D10F2BC5A0E6B6531DBED92EFAA6367356319F308ED8C4D657C577568836A002FB800
	E400657DF46E32B6E05DAAA8971F871F8140F61E6AA25687321158D70BCB8E0053E9F0F11AB227F1E3C09AEB021C0921ECF40E264CE572C4C770F5F93AA3309D7EDBC7E0BB7CC2C7E8BB1B63889E8365B954B190770F7E44F4E4A448BC66BC79CC66C2722396AAEB31EEBB62D221ACDCA0DCA14395C1799385B73DD0ABCF6CB39C9D02630756CB8903E88C57D7522E5360B57B30B3781A9D689C7AACFD5E497CFAB883CD3DEE11639ABAB1CEF43535F5094A197D2AB3DF419D6DEAF0F559B1B860BECA4B81BC0F27
	8E9E36BBC4B42F643B6C780A63D15F2B718B8A57B13C9C4471AA396E54AE5E670868F04B954C4FF9FF035A4F31DC60FEF2B4DC63GF81BBB7255ED1265B642C5033160A235F9EC133ACD69C55E468657FE25CDF3013ACD145CE691DC17DDA4FF6E592DA8A3073957658599F1FFAEFF9B75CB07F679E186E3B50089GFDF6CB983671952A478EF4CEB78B054770CCB53A9DF67275B83E7A9F38563254CCAEABF3A1E479FC36DDBD2EDE2619825754B062AB35C35DC638023729062B698A721B3ABF5EA304FAA6D086
	5A5DDBD323DAEC6E4338B650B3814B956443E46623BD0906F2BC40A2054619D7AF110632F1B80D999B0946F205C665A624D1F4A578A0G26AB0946C73B4BAE0D4600CD48693C3AD96165CA22235F4CF801F2BC40A2054E319D17480B3221B90DA9858AAF8A0D1DACEEAD9A853CG18G38DD2E5EC6BE2E85F051230876FF176D90D43EE51939C72A29B960429C0709EFCCCEABEC9B797FA92308569CB6E6DEB84440G3561647736F7101FD8F45B8D4C178D6617B705F37D678B777B3D243332049B4A0A0400FE2F
	322D795C7E72AE793E8961B43719FBD83C8F655C51E0EFC7933F23F2C34B3A26C6FBB6B80DE287493AFAB451986BC09AEEA8C7G440CA63BA0ED8864CBA60C8E2E6B39CF554D767A22515A678D9799FE49E86D73065CFCDA5365D0F7E48C510F5D98083EFE4CD074E731F310B4C6CBFFAE43AF9A2325DF3D0968AF013A5A31C47F4D4D0168470C9D0AFEF1810E97BFD6CBFFB743A70D55521F371968AF023A7288229FDD9008FED444D074273233939B2125BF036163A33474CB37907DA428AB99C774BF5AF6F9B1
	0869B27B0D9EF6BF4E571B4CD70F982765F78F43C70D53729B2F681098284B7E8179768FBA51A658209C8E908B10841082306287645FBF5109B9312F4D6DF25402E11D4B43BCFCC09EF73958F9466C04598EBE9BF377BDF0AB49BC263F0BBC8AD785DE4302GF84ECBE1A94EF957D55A7865AD062F3DCA9B3F5C3A07621786280B3F0A5674EDC7A89997C686237FBC53912511DA99E73367165511DA990FF7100C6B21AEB6127C542B18ED4C9BEFB0960D27B97523BBC5FBC0F53FBEFF641F273CB322B4B84F77EF
	A41EF7A9BC6BB779793CC3F57F512611677944085260BCFF3019F82ED2F85ED96067F32F6ABEE17348737C60085260BC4FAFA01EBFD1F80E596267F3366A3EF248DC72F76779737F879A3B8A7DBCD5286E97EC9BF95A3F9DD19ADC5F7F5E99BC26FC44B9F4CC7904B3F0FEE625F3F0FE6699E7607C4C73CEF5FE066F358DFB90E702FDE699CF3A75549E4A8178147D77DAA9FB3E8565A485371B6116C2D9381AF0F1FBAED516B896DE1346DFFA0E00537845A553205CC02818BF70992CF5108CCA945EB2BA7D7B72
	E5557DE447486FD17D0852603170CD9B70D918849B3161EAFCD76FFE4C94887C7ECF5D7E7B9C1F7F7EC82F20745F3987735F96FB379D2225C6FB1FA84C3C9AE579915BCFD8978565D58C675ED39CE2BE29D72B1F573C520B7747F1BFB74250F2084E6B8ED9B116B2411ED6D549717BB7255A67B8182F2A1FE0B01E1CC0717BFBED015A3821BEFC824D355191EA2E519378DCF92C771BEF546F9666CFFDA3137C99ED3FB839DF6284FF2C47F97AEA8F365F2F0C9953239E337A6577833E930030D29EABFB22BC7658
	37E4B90D5F03391503A2BEE725F1FBC4F5DCDA747AB3999B030CDBAB0FE71BB8B0AE65316064D20AB723A7D2AC18D806FEDB8A14D7GE4DFC34F4D44B6EADBF48D359DEF4336CDD0F6G04DFC379A0B74BD9B6C250ED9368D9494F7AF066DC1FC57662733300BEF4DD0B74990448DF36340B2DD213592A740FBB07742301C642B5640B7FC76F1EE063G324163F20B9E2917E359DB8A72D80F76633ED88263248194CE02188B209E60B4009BA0E29251A86C5B950286CFB0781DFCFE66F525DA9FFB66A43F2ECC18
	944C47AE5F4B6253C9DA9F7FC306DFB6C96B639B4A4847CF033A1313C95F3537E969A7CE7653DCBCB9987D8CBB0E37FC3216FE9E4327CF56521FECA37AF9D1A037A812599D67F0CCA55F4DC5AF2FFD62B9F47C6CCE3BE4B7BB6CFD6C350AC6E86834727AA77B035B5BE77A073637AF768736375967875B5BB567835B5B8D675576D6FEA76ABC9F97577B1839D3997715732847F86E738B3353ECD565696732731582F2C90DA29BDDD6C676A96FDADA2F3F6FC5DCB5149B94DC9D4339219C2560124B3436845B2B4B
	55578EAFEFBF58DEBDB208065686FF17F79C388C2EE03AA481663A68DA120BE0A3392CD478DDEDE74FAA2EB3982B2FA35CA606EB0232CF41DDEF534E995BFF9F7635C821B1EF900D6178A621A0CBA01B78335241F974BFA836B07ABA1EC70F07EBB2G7AEF39D3304D2132E2394D32GFC27DD2736494237F8E6655CA0B33B9CEF2DC49B7225D782BB837F5DDEE920CD6E5A2B36378DD3B87DF0386A2610CD7EE93956A6E31BB828DFB805EC032EEDB00FF395DB3EF48A67B18D2E39BA6E1FF29E37590847E25DE0
	9E2BF4EA9EFFFF8C13880DA2288647F120A32A78947F05673549A30A18D09F48E33DDDBD7FFA19FEBFDCA76A08476B5A55EDE265B6CB603AC2C7B96A1C77A87711FD3D41D8FBBD5950AF5E235C33F4BD5950F32C2D9E4A8B8116AAEDBB4B485E26DDCF323B398C4BCCDF89648B34672BFD01253DEBB303798225D3390CCECE8D1423163EC5BBF78979E4553B9A376D25DC724429444F26F264A7994A4BG522792BFAF1493BF79B7D05BC7AB316D91A81F81705DC072FB380D64B791F049B750982FDBC9FEC2B40D
	710515651F205CGE00B26369F5B08DED8B4492F47465EE9863D4EF9AE6FAC9D02E76D9E62E3775B096F84E59E0FB29B39924A39538C4662E91427CFEBA999C0C33233A7A1DE496E282999308D79583FEED7727353E86C7D9DACBF8F65880018E92413563EF7F9F9566D17596F78307BF17F61A936479326E9F307698C3FF01AB6F7F805F2FE96C3DD558F09DFCF191A3E8E707A9F0656735C5F0B3AD08C57A4E83B94C0188EBE9FC0A9C0BD40C9G77F472DDEEBF9F4A27D49E588C4A6B466D8DA51B486941FC2E
	B16C3C44CC574AE682436B27EBE5D3D8CE32C9003A229812CDE60516FED80C1FE6D4CCB07AA77631E785B15A77C94D8C1F9023FD1F7471CAFA1FB4996ACAEFA47AFF9864F3DE5A1E7A73E576AB31DE5EDE7CB1539793EF544A7B178C9FFD23D65E0DD612F7AC54655DC47C565B54745D008F3FA974DE647D2E30071A37AE409872BA35337DF013F65EE798BE61A66D3C9F37533C97C25D2E99B46F5F36075C27B302511F4DF4D04C8CAD7DF98C2F1F21257FE10732CF212EE8A651FF66BDC111450B2A7B316F0DFC
	AEA6E6C4E9F0BFF69C776F015F5FE4936EEA0653816E299C0A9B26DF889E674C3AB0F41CB377C260B8670E8B03631C85978247B9C9978647B9BFE563FAC4AB44E56AB867018B41631C1F6FA55FBDDED96748F27F5ADE2B3AFF2DFC6457797D912541F95EDE01BCAF83FE53E6D28C232BA4B9945E2C3C273E8FFD0EFA25ECE56553CA79294A6FF32EC8E730BCF277FD1FB434719F2771F32BFF0D972B6ECFD9C7FE3DBDA3CA032F6DB7B6BA57DB945C999B1D6B934AD95759E98F047977C03BDF9EED2A7B276CA3AF
	1B5C7611174D936D4433DE61F9EE071F4F8455FDDD4748737C2F9125417DFA73CE34EFF29E6B045DA259B83E48374E0FBFA24A319A4FC77F575E629063AE39D99D4316D78C0E13BF2D2058F385344D1E857E9740A733F80E085318DB990A4649D9EA9ABD4D185B9F906D3398D737CF9B4763788E689BB6CB21E38FC5C7A75361EDB3BAC235558F185373FBD10E47DC9E16BB663E042BC2F9BF855A264F22582A624C00772A78BBDF1FFA6C1683E6ABFF690B47D4F7D5D05CD8FE8B45D40F31B1EDD08E83505DC2B1
	6222CAF53E1D77DF3CAFD75EDBF1CA7FEBD6E37FD4A88B31206FE3157EF6757CB96E358E7518FC0FFA87796B173637C7DDE63F44E177637E5DA9666F1747EA7D3E96062F0D557AFDFFD6566A885469E3C99FBC34CFCDBFFF3641D8B25BEF2B2AE78773BBF3F2F1BC53ECEDCEBA1F614F4C566624272C261C3484F5CBE6937D66CAC11105CDF57FF445486B23173FD79B2F852E03D730BDDAFB2B4158F0AB79A37B2A90F7864A12021B37DF793D5D2DCA9EA1173DA78E656C5B88371561CA20FCC441CDDB2D65835B
	FF1739A34D01323AF3900D6F1AE74D525CEF96322A70BC72DC3EA078EEC3393CD245F2C33751B97E2CEA307EB5D711DECC3C0D67A9D3601A8A007E5C91BD3679457EFA390D209FAC9BBAAB1473AD00B63B745C2740F22D5EFF1E78BCBE2E44DCA657510D2D9E1743011A1A577D6994BECBB968904D1E860F5DA9194C9E2B7CA8156971A339EABDBED14F6719885745FA4AE7BE67536AFAEC138A752B74A40B72AAE4C77ED60928FE26D9FEAEA3B3CB88728D165BB97FA5377B1FE972B3BA2E0AF4C49354C55ECEB9
	3B872BC8CFAF3C1D746CCB2C6DAAA8974C81FBB7075AA6D5D3FE2F9EF02F33F98A0272519AE1654701F07C59779F84BFBF0F64297327758FBF1B71D690FE7A67F0BE964D7167CFD35BD7493FED339EFAD5887C0E2F32E8825913B0F63610FE76DC62B3271B69D3A8B781B866921F153D44E73852F673B9EC1B8865940095F3157797B3F08FE747D1AE6B74C57CADD230DC56C117C564CF5E686CCD283ABC5BB26DB27B7D67307BF15D5F359F79AF0C535A24FE06AF09535A241C7D345F4A21AEAA0E7459CF2B5574
	53811F7723CB49A7E6E66199AF063635GE7GFCG11GB38192G9681AC83C887481F87368E209A4084F09A408D90BE0F56694D0C5DA156C9F6E8D499C765DD49DC1A3F8E46524F734FBD896ED3668533293DEC2F254E53667CC6B1FC7ABCED4EEFEDB7657CF267435E1CCF72FB3FD73BFE0B677BE9AE1F9F0C7ED836FF5367EBED7AA4064F0B575A746D67482697C3DDF8BC496D218C5451DD8376F0AF3EFEA15FDF293ADF3BFF646DFC4908526076FBE9B54521998A6E11EA0AC39147E533B5ABF8FE69552C21
	73CBBB33826717CA33866717AA328267175E4F9A1CDF2AE55FD232421EBE29342BD3F027945CE1B6A662D8F02560CDECDE0827600A630F33F975B05CC999F7C205BBA563CEE7611E8116EDAE8F75B5E7215EEC33BB9C3CAC32B6F6E717A8A972E8E7F3343B4D2AF93B945CA9E5AD2239AF85FBB46F8E72391EE138E2A857AAB81797F18DD0F6A83885DEA5F7F0871D310B5DB8B73DD29E474A49CAF9E377773167F07C607B7A5D6F05C6307786B86F47FA49D7CDD564115C273CABF1A771BB1E3D87D48DE5130253
	B11C834A118A6E75F3431DB30E9988EF8A0067B42A07CDA3188C8279385C771A1F0B5F6C6B96BD123D556C604E44817077DC8EFBEB2F7C1E5E2F597E42E4FA5C1D6433652D41BC045BA336099E0FE8E1EF7589F27B3757E07B1550F61552FE0C97E9DADCDED082873CEDED78E998EABF5DCB4FA4F75DC5717F035E62D0323CCB9D535F73AD5EF39F368954CE1DC7B48BC244DDEC4FF72B3F0DE0829C469C92DC394F5D53CB7E69623B384F1D0AF3380B47A5FCAEFF6C8BB517028475DCFE42FEC7FE7053867C3939
	0C0B78A7BEB3777B51873C20751C787BDCBE47DF1ED34FF1D78227EDCAA09FFA29C56DC31F116BE3609A1FC072FDAC37822EAA5730CC659916FDFD68F8535103F526664603473FB4198E549DEF2EB31D28BB55A8507B1AEF31EFB47231ACCC76CE312718529EE57576CE51099FA55B35FFFFF9E9717E0A725D3C6D1DDE76EE2847AB478CCFFA5539886E3B7E6A7C005F4E79FDD7E53138AF7AF4B765C196A550EF644E9FD6FF07A7F1C3E5E630EFA9E45FAD4783F77B7D67D26FF37237AB2677AA3F6B037A683B49
	7F7D1FDE6577FAF7137F3B250F79898BE02FG54AEA0992F6AD33FF7A8FFF322AD48B7A7CC6696FA661E51CFFCB5AD205C5238D7AB04A15E07506C97FA17040FD9A0521909DCC0736EE35FE5DA886525GE98BC8D77D359B7D283CFB8C4662FB823C5B92G477D50D6A36E4BA3D0EF8168870882083F077C230E1CA13F5FF34897707BBDFCDEB78F726F06825EEF7EE576DBB16CFE5CEF9C4776C34ABD5AF8E09243AF3BC79B8F6C68233D36826A9A92E99D0C7D016847A7F279A743F5E9E22067AB5B45D073DE19
	984C8FFE126D076CFB35737E9B43675F2B1DF718C873AE023A30FB49CE2E6B413D369C4A2B941C44F03977990CC577916E5E9E32276577A9FB44985CCF749883FF6740E78C7C1D038B46505F9B9AE38C769D84B97EE46F884BB6DDF57FC65F487B6EB5A3CA034B7FD526E71AC07696E5CD2E6C27B5F1AB38858C978D65F885D7AED23B647BE83F2609FE993D203A9FA30E3C3C260FA88D2E4FCB58B726A4174B51E4F723AD12EDA17BBC185C662A9E76FD863779AC3EE34C7D151BFAC87726012C78F729D2FA48C6
	E75FEFB09682D4831C84705DCF76232AFF606FA84A190963BE46756C1D71E073555D2F76B19EE46FA03739BC22556372BA95BF6AF1362E3150B67EFE72DDBE95C30D1BB2E05C3BFB4816F39B73DC8F6506165FCF5F322A74E93FE5750E207E961545277D16D546007A6BFC5AEFD93DBE20BE59277D169575177D8DA64393E217AC430A357E35B230EF87AABE4BC1174365A9E3FE266C570858771039CBFE0F7E259E35DEADCA823D1594BABF434F4AD69F45482FAB389DBE0A11D19E7CBB587A4311B0F97D7F8AB9
	12100D5FF0008B3E0EA7892B65179D81015FAA6872889B14E99FE5F161326EFDB5D90F4E484A94320F8AC69EA79F9532B158BDAA6430D878281085F1328F3FAF0BFFF9B288B05A26410EDB5D553FFBD85FDCDF10A9E44200B6110F10530351AF0E117122A0E46E04F8D7E2A32C9FA8460C1B370D4A1F304F1BA9649E960CEDE6075BE6065E6CDBFBA6E1ED8B1B907ED241A18979A66C3B977A6EE5FDBFB489192DDD1230430FEFC5BC2FEDF41BDBC5E83246E276F430914E8A9704355F88EB5ABC5696C6C092B23F
	B97C8D10E5A52CDEE792DE16EC7CA3EDBEC992367907EE409416DC71A1974ECBAF0155E0FF5C275CFDD11DE3CE9FDD9874F7B3918679FB58863FCF49FD23CF0F62FAAF81FC1A01EC5D14A398A3857B5DC9E3275961104C0E0EBA2785FF03F220D7927D364DF494476F22B6F2A52FEBBC3AC65E5754677FGD0CB87881FB16268B69DGG44DDGGD0CB818294G94G88G88G78F854AC1FB16268B69DGG44DDGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GG
	GG81G81GBAGGGF09DGGGG
**end of data**/
}
}
