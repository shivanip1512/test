package com.cannontech.tdc.editdisplay;

/**
 * Insert the type's description here.
 * Creation date: (1/27/00 3:47:03 PM)
 * @author: 
 */
import java.awt.Cursor;

import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.createdisplay.ColumnData;
import com.cannontech.tdc.createdisplay.CreateTopPanel;
import com.cannontech.tdc.createdisplay.TemplatePanel;
import com.cannontech.tdc.logbox.MessageBoxFrame;
import com.cannontech.tdc.utils.DataBaseInteraction;
import com.cannontech.tdc.utils.TDCDefines;

public class EditDisplayDialog extends javax.swing.JDialog 
{
	private String displayName = null;
	private java.util.Vector displayNumbers = null;
	private long currentDisplayNumber = 0;
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private javax.swing.JLabel ivjJLabelDisplay = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private javax.swing.JOptionPane warningMsg = null;
	private CreateTopPanel ivjTopPanel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JComboBox ivjJComboBoxCurrentDisplay = null;
	private com.cannontech.tdc.addpoints.AddPointsCenterPanel ivjAddPointsPanel = null;
	private TemplatePanel ivjTemplatePanel = null;
	private com.cannontech.common.gui.util.OkCancelPanel ivjOkCancelPanel = null;

class IvjEventHandler implements com.cannontech.common.gui.util.OkCancelPanelListener, java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == EditDisplayDialog.this.getJComboBoxCurrentDisplay()) 
				connEtoC3(e);
		};
		public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
			if (newEvent.getSource() == EditDisplayDialog.this.getOkCancelPanel()) 
				connEtoC4(newEvent);
		};
		public void JButtonOkAction_actionPerformed(java.util.EventObject newEvent) {
			if (newEvent.getSource() == EditDisplayDialog.this.getOkCancelPanel()) 
				connEtoC5(newEvent);
		};
	};

/**
 * EditDisplayDialog constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 */
public EditDisplayDialog(java.awt.Frame owner, String currentDisplay ) 
{
	super(owner);

	displayName = currentDisplay;
	initialize();
}

/**
 * connEtoC3:  (JComboBoxCurrentDisplay.action.actionPerformed(java.awt.event.ActionEvent) --> EditDisplayDialog.jComboBoxCurrentDisplay_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxCurrentDisplay_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (OkCancelPanel.okCancelPanel.JButtonCancelAction_actionPerformed(java.util.EventObject) --> EditDisplayDialog.okCancelPanel_JButtonCancelAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.okCancelPanel_JButtonCancelAction_actionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (OkCancelPanel.okCancelPanel.JButtonOkAction_actionPerformed(java.util.EventObject) --> EditDisplayDialog.okCancelPanel_JButtonOkAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.okCancelPanel_JButtonOkAction_actionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Remove all the columns from the database
 */
private void copyPoints( long displayNumber ) 
{
	// only call this method if invisible
	if( this.isVisible() )
		return;
		
	String query = new String
		("insert into display2waydata (displaynum, ordering, pointid)" +
		 " select ? " +
		 ", ordering, pointid from display2waydata " +
		 "where displaynum = ?");
	Object[] objs = new Object[2];
	objs[0] = new Long(currentDisplayNumber);
	objs[1] = new Long(displayNumber);
	DataBaseInteraction.updateDataBase( query, objs );
		

	return;	
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/00 5:32:21 PM)
 */
public String createCopy() 
{
	boolean invalidName = true;
	String newName = null;
	
	// only call this method if invisible
	if( this.isVisible() )
		return null;
		
	javax.swing.JOptionPane box = new javax.swing.JOptionPane();

	while ( invalidName )
	{
		newName = box.showInputDialog(this,
			"Copy Name",
			"Copy",
			javax.swing.JOptionPane.PLAIN_MESSAGE);
		
		if( newName != null )
		{
			if( newName.equalsIgnoreCase("") || newName.equals( getTopPanel().getName() ) )
			{
				invalidName = true;
				com.cannontech.clientutils.CTILogger.info("Error");
				TDCMainFrame.messageLog.addMessage("Copy of display " + getTopPanel().getName() + " was not created due to an error with the new name", MessageBoxFrame.ERROR_MSG );
			}
			else
				invalidName = false;
		}
		else
			return null;
	}

	long oldDisplayNumber = currentDisplayNumber;
	currentDisplayNumber = TDCDefines.createValidDisplayNumber();
	
	Cursor original = getCursor();
	try
	{
		setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );		

		// specific calls to make the display
		createDisplay( newName );
		insertCreatedColumns();
		copyPoints( oldDisplayNumber );

		TDCMainFrame.messageLog.addMessage("Copy of display " + getTopPanel().getName() + " created successfully", MessageBoxFrame.INFORMATION_MSG );
	}
	finally
	{	
		setCursor( original );
	}

	this.dispose();
	return newName;
}
/**
 * Remove all the columns from the database
 */
private void createDisplay( String newName ) 
{
	// only call this method if invisible
	if( this.isVisible() )
		return;

	String displayName = new String( newName );
	String displayType = new String( getTopPanel().getType().toString() );
	String displayTitle = new String( getTopPanel().getTitle() );
	String displayDescr = new String( getTopPanel().getDescription() );
		
	String query = new String
		("insert into display (displaynum, name, type, title, description) values (?, ?, ?, ?, ?)");
	Object[] objs = new Object[5];
	objs[0] = new Long(currentDisplayNumber);
	objs[1] = displayName;
	objs[2] = displayType;
	objs[3] = displayTitle;
	objs[4] = displayDescr;
	DataBaseInteraction.updateDataBase( query, objs );
	

	return;	
}
/**
 * Return the AddPointsPanel property value.
 * @return com.cannontech.tdc.addpoints.AddPointsCenterPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.tdc.addpoints.AddPointsCenterPanel getAddPointsPanel() {
	if (ivjAddPointsPanel == null) {
		try {
			ivjAddPointsPanel = new com.cannontech.tdc.addpoints.AddPointsCenterPanel();
			ivjAddPointsPanel.setName("AddPointsPanel");
			ivjAddPointsPanel.setPreferredSize(new java.awt.Dimension(611, 305));
			ivjAddPointsPanel.setMinimumSize(new java.awt.Dimension(611, 305));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddPointsPanel;
}

/**
 * Removes any resources used by this Dialog
 */
public void dispose()
{
	getAddPointsPanel().dispose();
	super.dispose();
}

/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxCurrentDisplay() {
	if (ivjJComboBoxCurrentDisplay == null) {
		try {
			ivjJComboBoxCurrentDisplay = new javax.swing.JComboBox();
			ivjJComboBoxCurrentDisplay.setName("JComboBoxCurrentDisplay");
			ivjJComboBoxCurrentDisplay.setBackground(java.awt.Color.white);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxCurrentDisplay;
}
/**
 * Return the JDialogContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJDialogContentPane() {
	if (ivjJDialogContentPane == null) {
		try {
			ivjJDialogContentPane = new javax.swing.JPanel();
			ivjJDialogContentPane.setName("JDialogContentPane");
			ivjJDialogContentPane.setPreferredSize(new java.awt.Dimension(623, 570));
			ivjJDialogContentPane.setLayout(new java.awt.GridBagLayout());
			ivjJDialogContentPane.setMaximumSize(new java.awt.Dimension(623, 570));
			ivjJDialogContentPane.setMinimumSize(new java.awt.Dimension(623, 570));

			java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
			constraintsJPanel2.gridx = 1; constraintsJPanel2.gridy = 1;
			constraintsJPanel2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPanel2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJPanel2.weightx = 0.0;
			constraintsJPanel2.weighty = 0.0;
			constraintsJPanel2.insets = new java.awt.Insets(5, 6, 2, 6);
			getJDialogContentPane().add(getJPanel2(), constraintsJPanel2);

			java.awt.GridBagConstraints constraintsTopPanel = new java.awt.GridBagConstraints();
			constraintsTopPanel.gridx = 1; constraintsTopPanel.gridy = 2;
			constraintsTopPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsTopPanel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTopPanel.weightx = 0.0;
			constraintsTopPanel.weighty = 0.0;
			constraintsTopPanel.insets = new java.awt.Insets(3, 0, 0, 0);
			getJDialogContentPane().add(getTopPanel(), constraintsTopPanel);

			java.awt.GridBagConstraints constraintsTemplatePanel = new java.awt.GridBagConstraints();
			constraintsTemplatePanel.gridx = 1; constraintsTemplatePanel.gridy = 3;
			constraintsTemplatePanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTemplatePanel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTemplatePanel.weightx = 0.0;
			constraintsTemplatePanel.weighty = 0.0;
			constraintsTemplatePanel.insets = new java.awt.Insets(3, 0, 2, 0);
			getJDialogContentPane().add(getTemplatePanel(), constraintsTemplatePanel);

			java.awt.GridBagConstraints constraintsAddPointsPanel = new java.awt.GridBagConstraints();
			constraintsAddPointsPanel.gridx = 1; constraintsAddPointsPanel.gridy = 4;
			constraintsAddPointsPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsAddPointsPanel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAddPointsPanel.weightx = 1.0;
			constraintsAddPointsPanel.weighty = 1.0;
			constraintsAddPointsPanel.insets = new java.awt.Insets(2, 0, 0, 0);
			getJDialogContentPane().add(getAddPointsPanel(), constraintsAddPointsPanel);

			java.awt.GridBagConstraints constraintsOkCancelPanel = new java.awt.GridBagConstraints();
			constraintsOkCancelPanel.gridx = 1; constraintsOkCancelPanel.gridy = 5;
			constraintsOkCancelPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOkCancelPanel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsOkCancelPanel.weightx = 0.0;
			constraintsOkCancelPanel.weighty = 0.0;
			constraintsOkCancelPanel.insets = new java.awt.Insets(0, 6, 0, 6);
			getJDialogContentPane().add(getOkCancelPanel(), constraintsOkCancelPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane;
}
/**
 * Return the JLabelDisplay property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDisplay() {
	if (ivjJLabelDisplay == null) {
		try {
			ivjJLabelDisplay = new javax.swing.JLabel();
			ivjJLabelDisplay.setName("JLabelDisplay");
			ivjJLabelDisplay.setText("Display");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDisplay;
}
/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setPreferredSize(new java.awt.Dimension(611, 25));
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());
			ivjJPanel2.setMinimumSize(new java.awt.Dimension(611, 25));

			java.awt.GridBagConstraints constraintsJLabelDisplay = new java.awt.GridBagConstraints();
			constraintsJLabelDisplay.gridx = 1; constraintsJLabelDisplay.gridy = 1;
			constraintsJLabelDisplay.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJLabelDisplay.ipadx = 4;
			constraintsJLabelDisplay.ipady = 3;
			constraintsJLabelDisplay.insets = new java.awt.Insets(2, 400, 4, 3);
			getJPanel2().add(getJLabelDisplay(), constraintsJLabelDisplay);

			java.awt.GridBagConstraints constraintsJComboBoxCurrentDisplay = new java.awt.GridBagConstraints();
			constraintsJComboBoxCurrentDisplay.gridx = 2; constraintsJComboBoxCurrentDisplay.gridy = 1;
			constraintsJComboBoxCurrentDisplay.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxCurrentDisplay.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJComboBoxCurrentDisplay.weightx = 1.0;
			constraintsJComboBoxCurrentDisplay.ipadx = 34;
			constraintsJComboBoxCurrentDisplay.ipady = -1;
			constraintsJComboBoxCurrentDisplay.insets = new java.awt.Insets(1, 3, 0, 0);
			getJPanel2().add(getJComboBoxCurrentDisplay(), constraintsJComboBoxCurrentDisplay);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}
/**
 * Return the OkCancelPanel property value.
 * @return com.cannontech.tdc.utils.OkCancelPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.OkCancelPanel getOkCancelPanel() {
	if (ivjOkCancelPanel == null) {
		try {
			ivjOkCancelPanel = new com.cannontech.common.gui.util.OkCancelPanel();
			ivjOkCancelPanel.setName("OkCancelPanel");
			ivjOkCancelPanel.setPreferredSize(new java.awt.Dimension(611, 37));
			ivjOkCancelPanel.setMinimumSize(new java.awt.Dimension(611, 37));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOkCancelPanel;
}
/**
 * Return the TemplatePanel property value.
 * @return com.cannontech.tdc.createdisplay.TemplatePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.tdc.createdisplay.TemplatePanel getTemplatePanel() {
	if (ivjTemplatePanel == null) {
		try {
			ivjTemplatePanel = new com.cannontech.tdc.createdisplay.TemplatePanel();
			ivjTemplatePanel.setName("TemplatePanel");
			ivjTemplatePanel.setPreferredSize(new java.awt.Dimension(611, 94));
			ivjTemplatePanel.setMinimumSize(new java.awt.Dimension(611, 94));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTemplatePanel;
}
/**
 * Return the TopPanel property value.
 * @return com.cannontech.tdc.createdisplay.CreateTopPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.tdc.createdisplay.CreateTopPanel getTopPanel() {
	if (ivjTopPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjLocalBorder.setTitle("Display");
			ivjTopPanel = new com.cannontech.tdc.createdisplay.CreateTopPanel();
			ivjTopPanel.setName("TopPanel");
			ivjTopPanel.setPreferredSize(new java.awt.Dimension(611, 110));
			ivjTopPanel.setBorder(ivjLocalBorder);
			ivjTopPanel.setMinimumSize(new java.awt.Dimension(611, 110));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTopPanel;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION IN EditDisplayDialog()---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;

	TDCMainFrame.messageLog.addMessage(exception.toString() + " in : " + this.getClass(), MessageBoxFrame.ERROR_MSG );	
}
/**
 * This method was created in VisualAge.
 */
private boolean initComboCurrentDisplay( String displayName ) 
{

	if( displayNumbers == null )
		displayNumbers = new java.util.Vector( 20 );
	
	// Init our Display Name Combo Box
	String query = new String
		("select name, displaynum, title from display " +
		 " where displaynum >= ?");
	Object[] objs = new Object[1];
	objs[0] = new Integer(com.cannontech.tdc.data.Display.BEGINNING_USER_DISPLAY_NUMBER-1); // subtract 1 so we get the PreDefined Display(displaynum=99)
	Object[][] values = DataBaseInteraction.queryResults( query, objs );
		
	if ( values.length > 0 )
	{
		for( int i = 0; i < values.length; i++ )
		{
			getJComboBoxCurrentDisplay().addItem( values[i][0] );			
			displayNumbers.addElement( values[i][1] );
		}
		
		//init our first display
		getJComboBoxCurrentDisplay().setSelectedItem( displayName );
		currentDisplayNumber = Long.parseLong( displayNumbers.elementAt( getJComboBoxCurrentDisplay().getSelectedIndex() ).toString() );

		// init our sub components with data
		Long displayNumber = new Long( currentDisplayNumber );
		getTopPanel().editInitialize( displayNumber );		
		getTemplatePanel().editInitialize( displayNumber );
		getAddPointsPanel().jDisplayChanged_ActionPerformed( displayNumber.longValue() );
		
		

		getJComboBoxCurrentDisplay().revalidate();
		getJComboBoxCurrentDisplay().repaint();		
		return true;
	}

	return false; // no rows found in the display table

}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	if( displayName != null )
		initComboCurrentDisplay( displayName );

	// user code end
	getJComboBoxCurrentDisplay().addActionListener(ivjEventHandler);
	getOkCancelPanel().addOkCancelPanelListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() 
{
	try 
	{
		// user code begin {1}
		
		setResizable( true );

		// user code end
		setName("EditDisplayDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Edit Display");
		setContentPane(getJDialogContentPane());
		initConnections();
	}
	catch (java.lang.Throwable ivjExc) 
	{
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
private void insertCreatedColumns()
{

	for ( int i = 0; i < getTemplatePanel().getJTableColumnCount(); i++ )
	{			
		String query = new String
			("insert into displaycolumns (displaynum, title, typenum, ordering, width) values (?, ?, ?, ?, ?)");
		Object[] objs = new Object[5];
		objs[0] = new Long(currentDisplayNumber);
		objs[1] = getTemplatePanel().columnFieldData( i, ColumnData.COLUMN_TITLE );
		objs[2] = getTemplatePanel().columnFieldData( i, ColumnData.COLUMN_TYPE_NUMBER );
		objs[3] = getTemplatePanel().columnFieldData( i, ColumnData.COLUMN_NUMBER );
		objs[4] = getTemplatePanel().columnFieldData( i, ColumnData.COLUMN_WIDTH );
		DataBaseInteraction.updateDataBase( query, objs );

	}
			
//			TDCMainFrame.messageLog.addMessage("Display " + displayName + " added to the display tables", MessageBoxFrame.INFORMATION_MSG );
	this.setVisible( false );
	
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/00 2:10:23 PM)
 * Version: <version>
 */
private void insertDataSet() 
{
	// Lets delete the current display Numbers points just in case
	// ordering was the only thing that got changed
	String query = new String
		("delete from display2waydata where DisplayNum = ?");
	Object[] objs = new Object[1];
	objs[0] = new Long(currentDisplayNumber);
	DataBaseInteraction.updateDataBase( query, objs );
		
	for ( int i = 0; i < getAddPointsPanel().getRightTablePointCount(); i++ )
	{

		query = new String
			("insert into display2waydata (displaynum, ordering, pointid) values (?, ?, ?)");
		objs = new Object[3];
		objs[0] = new Long(currentDisplayNumber);
		objs[1] = String.valueOf(i + 1);
		objs[2] = new Long(getAddPointsPanel().getRightTablePointID( i ));
		DataBaseInteraction.updateDataBase( query, objs );
			
	}

	TDCMainFrame.messageLog.addMessage("New data set for " + getTopPanel().getName() + " has been created", MessageBoxFrame.INFORMATION_MSG );
	this.setVisible( false );
	
	return;
}
/**
 * Comment
 */
public void jButtonCancel_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	this.dispose();
	return;
}
/**
 * Comment
 */
public void jButtonOk_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{	
		
	if ( !getTopPanel().isColumnDataNULL() )
	{

		Cursor original = getCursor();		
		setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );		

		try
		{
			updateDisplayTable();
			
			removeAllColumnsFromDB();

			insertCreatedColumns();
			insertDataSet();

			TDCMainFrame.messageLog.addMessage("Display " + getTopPanel().getName() + " edited successfully", MessageBoxFrame.INFORMATION_MSG );

			this.setVisible( false );
		}
		finally
		{	
			setCursor( original );					
		}
	}
	else
		warningMsg.showMessageDialog(this, 
			"Make sure all fields are completely filled in.", "Message Box", warningMsg.WARNING_MESSAGE);
	
	return;
}
/**
 * Comment
 */
public void jComboBoxCurrentDisplay_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	
	if ( getJComboBoxCurrentDisplay().getItemCount() > 0 )
	{
		currentDisplayNumber = 
				Long.parseLong( displayNumbers.elementAt( getJComboBoxCurrentDisplay().getSelectedIndex() ).toString() );
				
		getAddPointsPanel().jDisplayChanged_ActionPerformed( currentDisplayNumber );
		getTopPanel().editInitialize( new Long( currentDisplayNumber ) );
		getTemplatePanel().editInitialize( new Long( currentDisplayNumber ) );		
	}

	return;
}
/**
 * Comment
 */
public void okCancelPanel_JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) 
{
	this.dispose();
	return;
}
/**
 * Comment
 */
public void okCancelPanel_JButtonOkAction_actionPerformed(java.util.EventObject newEvent) 
{
	if ( !getTopPanel().isColumnDataNULL() ) /*&& 
		  !getBottomPanel().isColumnDataNULL() &&
		  getBottomPanel().columnCount() > 0 )*/
	{

		Cursor original = getCursor();		
		setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );		

		try
		{
			updateDisplayTable();
			
			removeAllColumnsFromDB();

			insertCreatedColumns();
			insertDataSet();

			TDCMainFrame.messageLog.addMessage("Display " + getTopPanel().getName() + " edited successfully", MessageBoxFrame.INFORMATION_MSG );

			this.setVisible( false );
		}
		finally
		{	
			setCursor( original );					
		}
	}
	else
		warningMsg.showMessageDialog(this, 
			"Make sure all fields are completely filled in.", "Message Box", warningMsg.WARNING_MESSAGE);

	return;
}
/**
 * Remove all the columns from the database
 */
private void removeAllColumnsFromDB() 
{

	String query = new String("delete from displaycolumns" +
		 " where displaynum = ?");
	Object[] objs = new Object[1];
	objs[0] = new Long(currentDisplayNumber);
	DataBaseInteraction.updateDataBase( query, objs );

	return;	
}
/**
 * Remove all the columns from the database
 */
private void updateDisplayTable() 
{
	String displayName = new String( getTopPanel().getName() );
	String displayType = new String( getTopPanel().getType().toString() );
	String displayTitle = new String( getTopPanel().getTitle() );
	String displayDescr = new String( getTopPanel().getDescription() );
		
	String query = new String
		("update display set name = ? " +
		 ", type = ? " +
		 ", title = ? " +
		 ", description = ? " +
		 " where displaynum = ?");
	Object[] objs = new Object[5];
	objs[0] = displayName;
	objs[1] = displayType;
	objs[2] = displayTitle;
	objs[3] = displayDescr;
	objs[4] = new Long(currentDisplayNumber);	
	DataBaseInteraction.updateDataBase( query, objs );	

	return;	
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GE7F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBB8BF494D516D1BE22848C98A482EAA048624A2863B0C7C659DDA6B23BB0030ABB384691DDBCB2A3BB9B67303B0C07D14E4E6B4EDFFE91D0C1D003029086C30084E244A1A1A1A61011AC0490A512D43A2BBFC9F5D7D3DD1D6EFCA0FB6FFB752AAA1D6EA4B0B2B9DC2A5EFD1F7B6E675DF75FFD55E44BC70999966B96C2AC99C47BBBEAA1A4D4C5482F47FE554CF1B1E2F2A6B17D6D84F81B5C95CA07E7
	9C5465BD101C19CB761F4A0672F4A83B6ECB4EFC8F5EB7131536B94761A58A1DB828FB3F7D45338353F9371E534922658FEC6B603981E0A6607039CAC47A45362D9AFE110667E03192126240B6F5369D9A2E844AF5G71GB39D2C7DBCF866D04AA3CACB34F6CD6392492A1F6F312E47F1CCE3F248F3323E155AB30FBCE4C9F5A22DCEBACF6C53836559G48FC9AE9B9FB841E476A5A1DFF36491E821B60754ADED534398AD43B2DC0343BD53B5B6F13044E525213D028E405CA37A049CEC2DCAE9C7B2736ECBA96
	A105D0DEC5F16F07105EACF81F8F30D4473FCC71A5EE6097406566BCFE39671846C3E377C3E478F35E340DD1F80CF1B35EE63A8D9E6383EFE4ED237AE8B80FF2DF8EF559ED4919BBG4AGDAGE2G36D3BB782EFE9FBC4BDB558A5963113D65BE5F89492E7CC5360B923C1716C20D06BBAB5AE445CE8853F52FEDFF04798C842C5F18EB6E47E4B273B0577B2C33B348529F1D36E6C510495219055604B6A60B075B8C19B05A7F9C157662363EF659177685E2262D0E1453B2A2501E7DEC3EF539B64EEA936D3D6D
	286B31D457DB615D22598743A7D03CE442B39B4D23F8AC77C05D32F6EE9B0D9B78DCEA53A7115C033591651010DCEE2D14589C1A2470391C6F4431C7C90CE616BE97016293CDF8B617D769DC30BC876AF6F812B319FDB5DF40B59B8365A9G338196812482E4FB211D175B580C7A83C33431BAC1D1CF845C12DDD4886B5BFC7EDDF80AA1D591EA3CFED5705A4486C1F18B2D1268A7DA9B57F91CDBEB1FFE9EEA7BB6207115689595C1F54BDE98DD70082A2878D91B0E73EFE39BC5243575EE0F8884BCBE42473DFA
	FEB36AC6702B67FDF6C1950D962CFE64F954C90750GA342G704E64925B0872BA867C578358F5B9D89AF03C53226295255252DAD9EE8F786A1193B25B0BF2FE117A0E903C47F9390CC7FD033855D8AF73F1CA9AB6194629F45BF07A02524957D8BC757F3BE4EEB33FEB64B6D3397EBE327F59D391ED06564B1A4D4861EB679E3627F8392F7FB4549856F7179CF98DDD8FB09A477B51C87B1E465F206B6A75EFD1D743E16E09G73F4997B9B72184D1E907CA23752B38A9818E8B7A44E66F9112E1B8CED5EB97A7C
	1D941F82E392DFF266DE9F175DEBFD78B2777B7EE41759E92611E62273FD50E0B2F2DF40FDC96C903DEA2568933DF6F871EBF53E8B480F36EE9D325E270362FD304EABE0E179F5FC4F85DC0B022AAA6E5600AA1E93D5556DF59A7543A9ADFF2055E73888CEEB748564CD8E42BA575B47DD40B5E2133DDE11AACB6F22550F27EBD414C48FCC3AC28EF8D506CFE8C4BBEF97BBB59FB12591831F8EC18A203FE1F1424342DA64C1F1FB408450E51169BEDC3F2B74B887218EF0986F0460996B63EBB17889DF0B3B3A26
	12F94D3B2D117612047BAA2D29BE361E48E5E3AD32B90D79A6725C6F203EC2437B59DC037BD02E9142954D7E8FB461D8F9C023882076B2377F0D5FE2BD77B11A61D748124444294BF94D3739289771F2C084570C4E6C34AE3B340B1B426A9AB4B9327A3597D10E12498F96AAA847C91DBC963443FCFAG26DCE632DC2A4902353B54455B75C0B99EE00E425A2D731B5B6D6956477333F6BDFE56EE11560E496A6CE5641946FFEDC20790AA70874186CBCBCF69B2E3B15EA5FB36361672D49E4BADF0209C9B216C83
	9825F2FDBF331E47F2716D09E4D9527BD16355C4154DE53EEA68BBEF980F65BA3FC31A6901644CAD812E2311974D7BC9395FAF5B5CF4AB5364DB5CE4368706ABEDFA8B1E8E3762D7EBE0410638AE4A1AF03E3857E9E8ED6FFFC277EDFB01EE8B406AG62FFDD3445EC2F001B87309CA00D0E372F6B902EE7EAAC856CD1A7AA8ED97108763DA85D82A12896D05FD1D0CE2BCF62FBBE574529EAB86A10B60898DA6593F228A220A868E6188DB717078DD89BFD44AF729BF89CB7EDF319B65FC58FCEA247FAB7DBA345
	5523B6FEE0AD6BE0BAF0F5983A68F9A4B3AD75521BD9CCAE37F4F1394C06B629G4C87B80E18FD402EE0F2E693C08CC0FC104B66F7DD55682F5AABB01A116A84D0DF41299381D5153D8C45265F9CD1EC8155AD95D04E4E343641E24CE772CA5FF9BAFC4C66210CD99BF1D0949A63332E1B73B98B663FDC6761516E4AE8BC1CE93F0E799F5877D53479C79DAF7A5C599EE833E33C3FA394B9AEA98C43D7C2194541471CF4AD0742E375AA0AF70542E375279CBCD68FC15D52900F5D321B51964ABA4186GD400D800
	D9G0BBA799ADE50F4188C929B574099D4718A5239BE1B9F0FD13FC33A30DFE9651B3FC3F9527D0A70D8E0E1E77839E7C5E7DF5E533B7A16D93F677A757BB02C3CB72C9F5325D35A9E590F72552C6D17AF854DEB252C0B2D95BBBCC3G4C576EA84E56460D6B62BE75CA1559274E5DF8483AB60ACFDD204DEFC517310E776B6763C7DAF0DF585B1D1CD989508490821882B087E0E9B757513ACBDF8E26A3EAFF75F285602AE1BF2B880FBBF4190E693ABEDDCC6E57AFDC97EB3AA369E2EAE8BF091E53403563076D
	5D3C6C18EE2EB843F503317F169ED8A3BDDCB7779C617BE0D98F5F871DC1DC8BA1A847814C6C61B1EFC3777571ED6F57AF1C6FF9BDFD7B3133D4975D23D320EE8DG43BDC3F139D70007AB9CD767C0DCAD14DB2E70391EF0E28CA86965DADAC65BE465A50E70F349DF8BC82BAF5F2FFD6FB44CE7A7E657EFB7C70F5F37B6C70E5FBFEC0E9C3F6FEAFE1B61E18C73A91E576FEF9EB83EAFEA0E945FFFC0673D705D0DF4EC3FDF745305D6A92802D6772A7A8E099067FF8D87FD0DE708113AF234B6A7D53962303EB6
	7EA7FFAB09682768BAE0B67EEE886D837B47DE61B61EF795EDFCBE1417A2CE37131D455172CB6B2EA6E766DD6D3F8FB1BF74F9AB46D2C5D0DF8760820881C83C4A7D4F4F1B875DA3CE068658A36636C4577393AD11753C308563D43F9CD0ECE238AE9737EC556B8CCFD7230A9E94BBEB73BC6D2F8A0AD36CE78B4BE8FFD6972D7FFF34C43397265379FDF4DAC4F8EC7EE3A76A8B7703F9D7393EF6D21F349A4A393D3076FB393E120EF0FD15757657CDBD60DCE1F896873CC0476B69E5F100C547AF2778D1A6BC0B
	8FBCC11EEF0E073A553DBC069A53ED6621D867ED1C9DED5AAEBA0400844BCC760BE7FCDA8A8B470841B1E759793E5894437357BF8CA25F8EC57000284E79C49B11EE4236AB6C5C1727C05BD400F5005F8FD0FB931F0B5301E328EED542F5B37BA64C779FF093FDCEB3B270AC271D04AAF08B41F56E95393F5A9E6232DCFD9313593A1B8D596541FB514D11E499CF57D2754DCCE699BA7EDE0AEFB26119ACBF3D4A63D19F54AD39197B71929AA7148C03B5B40C638EF6F1FFEF9F4657D2F9EB7435D05D9AF9ADFC5D
	9A596755378E6C731ADA2359B01359E3CEAE33594318EC968CB3E434945ED782248FB7F0CCB69328BDED9D4EE433D647A7D1FC09894FE4360762315C88F5F307F379BCC6776F3C91491947C6F0DCCC104B2CCE47ED226D9221BCE784DFBBAF87094E47EFCD6F7E60773DEF750F89E2EFA88DE66FCF503303E9DBA83860765A65A018BEE621D5FFC1E54D393AB367CEB61F793A392296DED8EC1A98B45FCF153CF5B4E2FE87E151882657E55AF36EAD1FE8BEEC03182D7559BA9274B01266C444ADE1F3CC022024G
	1E2CFED11F7A37B59BB46A7F37CFBD4E755F7B74AFF4186BF14EC7357E0C2FDF6C66B1771ADF4CA4520FBF0F1A47C898297123BD6B9ABEA13BEEFBB30B0D7374C533FCF2A76D34DA220C33CA6BCFEEB1E2F7E60F07681EDC84783ADB380DE6D33B35C3B92463EE8EF1DB1E2263DE2DC6DCAA1453C7F1DB1EF495F1DB21DC24636E233846D1AC7F44F07B2F225DE11BC8F633B58A7E5AEC7BA42511B100ED7E7536FD2698F3C02D022D5D2948812F1D6FA9CE6AC3C34047F00014D1082B6E2A9CE03C8CEDCC8E5321
	4F1CD14C57E25DA2F8BF894FBF421F9603BB396E72EE65BA591745F5D72463A6E6F35DB56A38C7A92E874A71BAAEBF036BF3662DDCCF1303E84BF3213C98E0B9G39ADB9F347ED3C7EFEDA0F792A9240B581445C4672022C7E33A179979C93E970B20E3F78563EB6307C567EB641E8D48D09C6CE184E57C6300DC8B9FD2667470AF00D7A9451A1AA0AE8BF676E423D1D697A956A5327431C9F3E0D6BFAE2F0A0DD27E974392E17C23F15G1FE8F3CA9FED44BD0C7E0EED589E029137A7602154996DAFA86DDCE83F
	E3B44F15BC540638EAA85BC7733DDAF57F891E8DC21B9671E296DBD6E8488B48BAC50628C86DE4D81AD956E4FBD93C3EBB661FE6F19FB77B31E9E44E48BFD8226595060FE6FA1D2EBDD754FFC2710C17ED9246DBCB20EEA54016D80835G4AG9AE3B9EF0F3BF10E983943744BD5D53039E8161687748DEEB1E820B118E3E39F7360CC357B44365D516483A31C023D0BE5446A85272E13954E68B6F1117ACB8966980A65F1E1F9965EFFC8C2279CD0C1E7E3E8DC2567ED26C33B39G4B81D647329CB033253BC203
	5912999AE1DCE90CE1CBB1E342ED29ACA332ADED2F41B947C27B04B1FC4E4FB81047D335C22BA8E999E3FD1C07CE6177AF27A26715894BEDBFF7553C974E9F4378DA81CF9697BF052121B6DEFE973655534F9532A4AB85C117DB95BF227504E4937E3772F8FF9C0F35B3AF935D8EDFDE476DF0478313C965F3FF30FC92458E570CE176D7BB5658A3D7342D49E2323138104EF0280B83E03AEB4C623A1BBB1669EE89BCD7E87D496D5866416C2D91E87575ADCFE5D3FD8821B0FD7C067A68EA98279E00619A290F0E
	01F26C6DDCC752A5DC2B6C24FD0847DCF44F2D17FD944BF0EB3721AE6BC58F68C795E905BE079FFE0C73A9375B6BE4B7AC9D2D12557DDBB9766B1358C2FB25392DB3C5E67E665C4E720EEB1077B81E471FD8E2EE33B70E35F14133C7EF5390B23709575A2440B3B50E5B4AC8453015C090756F9503F93A3DD43AE1F9634783FB0F9CBC38BF6F7051FCB2B43BD9F91C0DFF3F5F3021C52AC16B5410E8B1BFB23B8D672F1FD318AB556A9EEFC37B06E52742494F5DC58F2167202157497C5114G56270F037D9C2092
	2091C09A477D5C71760F8975F391FC5CC93BD36471B4277731947DFC16AF8D7EAD48A10946B6322A4A9E9C1F63CBA434B7D576D1A443FDA961FAD35CCE174A1AEA788A097AC151C1515AD9B8E848F6E2E0B0599EAA4E574EDE5F68E77FD1A013F96378D97DA04DBD6B39A79C243474AB45EDBFA1B8EB2937E5E70129C1FE96D8BD0E67B31E26E3960ECF4E2C86E8994F736221CF395F18B5E392290DB99DB1C6DFD6FF489A9A4F567B1471065F1899F8A36B5AEC6FCA27E1FB09DD06FC522E8EC5BEDA8E4E91A587
	C73D37F69E6A65390E1471BCDE98CB6F1FF26F003D95208E40855083908FB093E0AE40E20095GA49E76E1003DG47GEA81DAG54F8EE2727AF67136831062698EA2C0A60C6F35578FE700A4177C7D9F98370FD68707E3C622305794CAEE232F43B00B94C0B67B97585D93A3E63393EDF27ED8BA7003E81DAA6F0FD37351B778954E134B3D9E802266F89063EB768BC6C68B5F878B0E3A8BC30FE470951AFBDB814FECC67773ED5CC225FC9506F31C47DCB4B8ADA641B8261F93EDB0AF8DEF66984AE2765C7D1CE
	DB6E841BG283C132F35113B0B06CE2F4FD643E8BDDD426FB91A6E6434D82EA59E4A33G666B34FE7F5D5181E889F63B0F6ED5607F7836D5A1627D9E630E519B9B6274166A74FED4CCF91B883C81D4CE64F6BBBD905DDFBE90985CDF4E8E50EF84404F55F77A345C6BCF829873498E07DF547D72BF86F2F5DC0D3FCE763BF1A7E071F6F3707A3E8575DCE73F99575C0F6547AA68F70CCD9343733635946F1A981E37FDD6775DAA5425CC643E67E38B75D98930578178GC6814C84181F40755291ACA403646C4126
	EBC1F112A9F548785B5C9B093F148436F6D3937A1FA9994DE95D51ED617E6E41ED61116E48FB671C6E7EFB67BFF4C75EBB1F68B66F1D5A9DF7B72E8DA77850C63D5D13BA6E9B9D772FF4CC44DDF05BD5170ECF2173C2FC350884B97EA5BA2FA045B5EA38D7CC38EFB45C0AEE34E7E059A5AB3C6FEB5DF82767F0CB92AB7F97ED637602F96B72F81D4E516D93CC73FED347FD235B63482C6B3363786B6C7764B577E3F65E160D76CC6E8A37FFBF452F3BAB5C7E5768FB54D6286B390B5BFF0F956DFF991457G64CD
	CA4EAC81288530CF62767F7BABF8E795EDBFBDAB4B7476GAF92E0BB9595534E2A65F80BB80FA198B3F6D2DF1BCF04726CC9116E939ACBF04E8BA60573A8D07C12C961BC7E76A867F19954D5CE566FF8CBB87D19137B2F373900DBBCB9927D7728AFD9BEB91CBE7B1EFB6564F07A776BBEE68D5435CC61746F506FD366CF69CFBF85F029D3A2515FF3046A786EF07A05943F6E6EF07ABFA956F58CF5BDF7F37AB5788918260B332677977E86797DD5B71486334FBFD9227B4AC3164877C0A5164877C0FF368CFC37
	DDE5097E6D6ADFAC66EFD735FCA80DF71701AED65E4D731C377772FC689ADDC73EECC3AF1D26F7EB760D5751769BCA0349E604B53A0EE22D036FE7932C11773349567E7BD922B572FEB6435A67ACC8F511D4446F4DEA6F617A391346D1ADD0D6F55C0B65DCE789BAAEE39B3FCBDBF18F57632592C3F7CE537B4A23B7DE0FB9A5B712866379BFCB383C5668F2A8556FE6F26F6538537ABDE34B3D7AEF4ACC3E67D3537BC3FF83BF34600652E0BC7FFD084BC65565303C184BE6140EFBBA48E513224B6633A306BC0E
	185E7F2978464BA665065250F24F3D982329304859F9B38F0B046517E774DECBFEF90E69DE0A6EFD7732B3B47242F2143F24EBFAB5605393F90E7287749C54AAAB74D21F65CF6787F84E2F905AD58344G44A7323BA4B637D565034D4DFCEFB68B7A4ECBE439EFACAFC9B476E8B637C3825D8FG1F1A48F32ADBBE27F716D313B3F7CD65F47FEF501CFB1609EE9D7433CFB5F26E212961F4D53A9E0783BEEEAA277BCBEA2FF3213CC0277BE671E0F4F3CCF4D7C13F6CE9863FBB260553DDCDEDFDAF600B2671BCEA
	857DFDAAFBF7505F22B29D0D8E641101F3CD878F721CCBEA00FF275B8263A6CC63E776D7BF405CCF24B33BB360265FC81616566317B1A7B4D3504E69C59DBCAF35F89ABF276FABC79A59C990978314GDCDBCEADBE4B607351EC034FFFB9BA94BED93FDF65987D8E04864B45FDDEF42DB9672235469CAF2CB3E80D9CB4670C34D83F2E75C63F670B87636DF0E1F17EB56645B798F3DC345120F56710E8B19F775F01414ED9F5C9113ED9A59D03758B7F0615757B1C76131206769DC1CC927FDE7D9EEAF371C961B1
	7F5FD1FCE2D2F84CDFD84EE37ED9D017BBBDB913FDCBB9EFB3AA456D155CF8B2233F1CA4FC1F2A23362EC25B5869FCCFFA240C6EC9D81E4E77246965C46767872677A365B7FECFBAF3C3E9E85F49F56062225F49D549B48F4CFCD6AF154FBC10432269FC7FD8D8567F1B3AB7F28D3BCCF947304B7B4A76045BA50E3F77E3BB7D622E20D5166C9F43A207FF300658EF78DF8B5C8F84E63DC6B2D4328E8F58F95ABD26DFA19B7ADEC9DA6EFBEFD86E6D0783D612DDC5B29D0264F389B5A4037ECE20162CED2501AF9E
	43A51564525FD59C003E87E85FAFEA095566D749F683EFC3BC2BBD6793ECA2B449318BCABB9D61B239CA2C97C90EC3F136D282AA942BAE82D9DA426A74DA72166A2247FF585DD5721EB1F49DA681340AAFC278E3DC7A75080F7E81E39BF546EAE832E6D947615234D712522CA43D06E432E03F0624E3448E8CD23EEAC89A847BECACCD6078E70151B675778265E31FFFEED6F36596AB314200AE110D10914414949DE3B7C1F9A09BD95AC8199A24D1ECCE1FB6D85A5627075554A3A3C29BA8D9CB816401CAAA4962
	4A6663C2F4E4095378336A63FA7B4D167F25C5E80325CB7DE65B3F47A5F38F4B4141FB30B626D9ABE4FD9FCB7F5954625FACFA29411FC6B2EB48B687E04E098A3DF3946D783D3776A36BAA12C3B368F8658CEA7653C706DF6D14400E37B84E620F8D7D2AE8AF6727F1B05CF718F561C6FE20DA703BD4BCA5024926FB716760E4932B16842F73G3B5AE4F6E7A9EF21BCEC50ECE53371F3A3FD144D0FEF5C3D735472B9E944DAC55E12E441DEA558D4D929A1B92ACB917DAED836A15F71B2BA907BC68DB74FF498EE
	3B4EBEB2DBD08F762ECF3ABDF4F76C7A956B2BC014A1059C20ADF1C19468AD6C95AEC181D222D2236237413232CB2F3B69C37553A73FFA2F49CAAC9A1328041DA11F0ACB5F6316BA6105FD49CB55339B0AACE32A2FAF94C6C19570237E6A350553DEB836B295862B529C86CCA957E875F54053AAAAA3E7CD78E0ED7C3251B8E39F324BC74E01933A570F3F5802417342F866D57A548B6ED8D8B8617EB73E25D3B73A2A64A343C3B8C559F3205C6100DDCA9FGB48643FF208F0FED8A425AEC8A0DFB675433FFEC85
	8B2BA4E91E42C27A7FC17A7FA17C7FA0459C24180307C0EDAB4900FF6175A14EB3B5CBDA580FB6944237FCBC3C32FB3B7CDE73B7102BC7EC0D7A0D287D81968B049EB06E6DD88EB60EA6FCE683FE7183BC673AAC7499097EBD50B90FA0C92AA03517FB6D78ED5009CED5B4F2D493BAF1FCBFEF23D5EA3F016E44F937197A7CBFD0CB87888275858F0E98GG20C6GGD0CB818294G94G88G88GE7F954AC8275858F0E98GG20C6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1
	F4E1D0CB8586GGGG81G81GBAGGG4898GGGG
**end of data**/
}
}
