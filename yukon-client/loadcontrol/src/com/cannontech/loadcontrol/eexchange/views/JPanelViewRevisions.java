package com.cannontech.loadcontrol.eexchange.views;

/**
 * Insert the type's description here.
 * Creation date: (8/2/2001 11:43:30 AM)
 * @author: 
 */
import com.cannontech.loadcontrol.data.LMProgramEnergyExchange;
import com.cannontech.loadcontrol.eexchange.datamodels.RevisionHistoryRowData;
import com.cannontech.loadcontrol.eexchange.datamodels.RevisionHistoryTableModel;

public class JPanelViewRevisions extends javax.swing.JPanel implements java.awt.event.ActionListener, java.awt.event.MouseListener {
	private com.cannontech.web.history.EnergyExchangeHistory historyData = null;
	private LMProgramEnergyExchange[] eExchangePrgs = null;
	private JPanelHourOffer ivjJPanelHourOffer = null;
	private javax.swing.JButton ivjJButtonOk = null;
	private javax.swing.JComboBox ivjJComboBoxOfferID = null;
	private javax.swing.JLabel ivjJLabelOfferID = null;
	private javax.swing.JScrollPane ivjJScrollPaneRevisions = null;
	private javax.swing.JTable ivjJTableRevisions = null;
	private javax.swing.JPanel ivjJPanelOk = null;
	private java.awt.FlowLayout ivjJPanelOkFlowLayout = null;
	private javax.swing.JButton ivjJButtonCustomers = null;
/**
 * JPanelCreateOffer constructor comment.
 */
public JPanelViewRevisions() {
	super();
	initialize();
}
/**
 * JPanelCreateOffer constructor comment.
 * @param layout java.awt.LayoutManager
 */
public JPanelViewRevisions(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * JPanelCreateOffer constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public JPanelViewRevisions(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * JPanelCreateOffer constructor comment.
 * @param isDoubleBuffered boolean
 */
public JPanelViewRevisions(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJButtonOk()) 
		connEtoC2(e);
	if (e.getSource() == getJComboBoxOfferID()) 
		connEtoC1(e);
	if (e.getSource() == getJButtonCustomers()) 
		connEtoC5(e);
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 1:45:36 PM)
 */
private void clearValues() 
{
	getRevisionTableModel().clear();
	getJTableRevisions().clearSelection();
	getJPanelHourOffer().clearTable();	
}
/**
 * connEtoC1:  (JButtonConfirm.action.actionPerformed(java.awt.event.ActionEvent) --> JPanelCreateOffer.jButtonConfirm_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxOfferID_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonCancel.action.actionPerformed(java.awt.event.ActionEvent) --> JPanelCreateOffer.jButtonCancel_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonOk_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JTableRevisions.mouse.mousePressed(java.awt.event.MouseEvent) --> JPanelViewRevisions.JTableRevisions_MousePressed(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.JTableRevisions_MousePressed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JTableRevisions.mouse.mouseClicked(java.awt.event.MouseEvent) --> JPanelViewRevisions.jTableRevisions_MouseClicked(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jTableRevisions_MouseClicked(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JButtonCustomers.action.actionPerformed(java.awt.event.ActionEvent) --> JPanelViewRevisions.jButtonCustomers_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonCustomers_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2001 9:39:53 AM)
 */
//Please override me if you want me to do something when my owner
//    frame/dialog is closed!!!
public void exit() {}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 10:26:38 AM)
 * @return com.cannontech.web.history.EnergyExchangeHistory
 */
private com.cannontech.web.history.EnergyExchangeHistory getHistoryData() 
{
	if( historyData == null )
	{
		historyData = new com.cannontech.web.history.EnergyExchangeHistory(
					com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
	}

	
	return historyData;
}
/**
 * Return the JButtonCustomers property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonCustomers() {
	if (ivjJButtonCustomers == null) {
		try {
			ivjJButtonCustomers = new javax.swing.JButton();
			ivjJButtonCustomers.setName("JButtonCustomers");
			ivjJButtonCustomers.setToolTipText("View All Customer Responses");
			ivjJButtonCustomers.setText("Customers...");
			ivjJButtonCustomers.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCustomers;
}
/**
 * Return the JButtonCancel property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonOk() {
	if (ivjJButtonOk == null) {
		try {
			ivjJButtonOk = new javax.swing.JButton();
			ivjJButtonOk.setName("JButtonOk");
			ivjJButtonOk.setMnemonic('o');
			ivjJButtonOk.setText("Ok");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonOk;
}
/**
 * Return the JComboBoxOfferID property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxOfferID() {
	if (ivjJComboBoxOfferID == null) {
		try {
			ivjJComboBoxOfferID = new javax.swing.JComboBox();
			ivjJComboBoxOfferID.setName("JComboBoxOfferID");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxOfferID;
}
/**
 * Return the JLabelOfferID property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOfferID() {
	if (ivjJLabelOfferID == null) {
		try {
			ivjJLabelOfferID = new javax.swing.JLabel();
			ivjJLabelOfferID.setName("JLabelOfferID");
			ivjJLabelOfferID.setText("OfferID:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOfferID;
}
/**
 * Return the JPanelHourOffer property value.
 * @return com.cannontech.loadcontrol.eexchange.views.JPanelHourOffer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private JPanelHourOffer getJPanelHourOffer() {
	if (ivjJPanelHourOffer == null) {
		try {
			ivjJPanelHourOffer = new com.cannontech.loadcontrol.eexchange.views.JPanelHourOffer();
			ivjJPanelHourOffer.setName("JPanelHourOffer");
			// user code begin {1}

			ivjJPanelHourOffer.getTableModel().setMode(
				com.cannontech.loadcontrol.eexchange.datamodels.HourTableModel.MODE_MW );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelHourOffer;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelOk() {
	if (ivjJPanelOk == null) {
		try {
			ivjJPanelOk = new javax.swing.JPanel();
			ivjJPanelOk.setName("JPanelOk");
			ivjJPanelOk.setLayout(getJPanelOkFlowLayout());
			getJPanelOk().add(getJButtonOk(), getJButtonOk().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelOk;
}
/**
 * Return the JPanelOkFlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanelOkFlowLayout() {
	java.awt.FlowLayout ivjJPanelOkFlowLayout = null;
	try {
		/* Create part */
		ivjJPanelOkFlowLayout = new java.awt.FlowLayout();
		ivjJPanelOkFlowLayout.setAlignment(java.awt.FlowLayout.CENTER);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanelOkFlowLayout;
}
/**
 * Return the JScrollPaneRevisions property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneRevisions() {
	if (ivjJScrollPaneRevisions == null) {
		try {
			ivjJScrollPaneRevisions = new javax.swing.JScrollPane();
			ivjJScrollPaneRevisions.setName("JScrollPaneRevisions");
			ivjJScrollPaneRevisions.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneRevisions.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			getJScrollPaneRevisions().setViewportView(getJTableRevisions());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneRevisions;
}
/**
 * Return the JTableRevisions property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableRevisions() {
	if (ivjJTableRevisions == null) {
		try {
			ivjJTableRevisions = new javax.swing.JTable();
			ivjJTableRevisions.setName("JTableRevisions");
			getJScrollPaneRevisions().setColumnHeaderView(ivjJTableRevisions.getTableHeader());
			ivjJTableRevisions.setBounds(0, 0, 200, 200);
			// user code begin {1}

			ivjJTableRevisions.setModel( new RevisionHistoryTableModel() );
			ivjJTableRevisions.createDefaultColumnsFromModel();
			ivjJTableRevisions.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjJTableRevisions.setDefaultRenderer( Object.class, new com.cannontech.loadcontrol.gui.LoadControlCellRenderer() );
			
			ivjJTableRevisions.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			ivjJTableRevisions.setGridColor( ivjJTableRevisions.getTableHeader().getBackground() );
			

			//set all the column widths
			getJTableRevisions().getColumnModel().getColumn(RevisionHistoryTableModel.OFFER_ID).setWidth(10);
			getJTableRevisions().getColumnModel().getColumn(RevisionHistoryTableModel.PROGRAM_NAME).setWidth(20);
			getJTableRevisions().getColumnModel().getColumn(RevisionHistoryTableModel.NOTIFY_TIME).setWidth(25);
			getJTableRevisions().getColumnModel().getColumn(RevisionHistoryTableModel.EXPIRE_TIME).setWidth(25);
			getJTableRevisions().getColumnModel().getColumn(RevisionHistoryTableModel.AMOUNT_COMMITTED).setWidth(10);
			getJTableRevisions().getColumnModel().getColumn(RevisionHistoryTableModel.AMOUNT_REQUESTED).setWidth(10);

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableRevisions;
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 9:53:38 AM)
 * @return com.cannontech.loadcontrol.eexchange.datamodels.RevisionHistoryTableModel
 */
public RevisionHistoryTableModel getRevisionTableModel() 
{
	return (RevisionHistoryTableModel)getJTableRevisions().getModel();
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 12:31:24 PM)
 * @return com.cannontech.web.history.HEnergyExchangeProgramOffer
 */
public com.cannontech.web.history.HEnergyExchangeProgramOffer getSelectedHistoryOffer() 
{
	if( getJComboBoxOfferID().getSelectedIndex() >= 0 )
	{
		if( getJComboBoxOfferID().getSelectedItem() instanceof com.cannontech.web.history.HEnergyExchangeProgramOffer )
			return (com.cannontech.web.history.HEnergyExchangeProgramOffer)getJComboBoxOfferID().getSelectedItem();
		else
		{
			com.cannontech.clientutils.CTILogger.info("*** Found an object of type : " + getJComboBoxOfferID().getSelectedItem().getClass().getName());
			com.cannontech.clientutils.CTILogger.info("    in the JComboBox of " + this.getClass().getName());
			com.cannontech.clientutils.CTILogger.info("    when expecting object of type com.cannontech.web.history.HEnergyExchangeProgramOffer only!!!");
		}

	}

	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 12:10:28 PM)
 * @return com.cannontech.loadcontrol.eexchange.datamodels.RevisionHistoryRowData
 */
public RevisionHistoryRowData getSelectedRevision() 
{
	int row = getJTableRevisions().getSelectedRow();
	
	if( row >= 0 && row < getRevisionTableModel().getRowCount() )
		return getRevisionTableModel().getRow(row);
	else
		return null;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJButtonOk().addActionListener(this);
	getJComboBoxOfferID().addActionListener(this);
	getJTableRevisions().addMouseListener(this);
	getJButtonCustomers().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("JPanelCreateOffer");
		setPreferredSize(new java.awt.Dimension(547, 513));
		setLayout(new java.awt.GridBagLayout());
		setSize(547, 513);

		java.awt.GridBagConstraints constraintsJPanelOk = new java.awt.GridBagConstraints();
		constraintsJPanelOk.gridx = 1; constraintsJPanelOk.gridy = 3;
		constraintsJPanelOk.gridwidth = 3;
		constraintsJPanelOk.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelOk.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelOk.weightx = 1.0;
		constraintsJPanelOk.weighty = 1.0;
		constraintsJPanelOk.ipadx = 485;
		constraintsJPanelOk.ipady = -1;
		constraintsJPanelOk.insets = new java.awt.Insets(292, 1, 0, 0);
		add(getJPanelOk(), constraintsJPanelOk);

		java.awt.GridBagConstraints constraintsJPanelHourOffer = new java.awt.GridBagConstraints();
		constraintsJPanelHourOffer.gridx = 1; constraintsJPanelHourOffer.gridy = 3;
		constraintsJPanelHourOffer.gridwidth = 3;
		constraintsJPanelHourOffer.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelHourOffer.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelHourOffer.weightx = 1.0;
		constraintsJPanelHourOffer.weighty = 1.0;
		constraintsJPanelHourOffer.ipadx = 515;
		constraintsJPanelHourOffer.ipady = 261;
		constraintsJPanelHourOffer.insets = new java.awt.Insets(6, 5, 33, 5);
		add(getJPanelHourOffer(), constraintsJPanelHourOffer);

		java.awt.GridBagConstraints constraintsJScrollPaneRevisions = new java.awt.GridBagConstraints();
		constraintsJScrollPaneRevisions.gridx = 1; constraintsJScrollPaneRevisions.gridy = 2;
		constraintsJScrollPaneRevisions.gridwidth = 3;
		constraintsJScrollPaneRevisions.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneRevisions.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollPaneRevisions.weightx = 1.0;
		constraintsJScrollPaneRevisions.weighty = 1.0;
		constraintsJScrollPaneRevisions.ipadx = 515;
		constraintsJScrollPaneRevisions.ipady = 94;
		constraintsJScrollPaneRevisions.insets = new java.awt.Insets(7, 4, 6, 6);
		add(getJScrollPaneRevisions(), constraintsJScrollPaneRevisions);

		java.awt.GridBagConstraints constraintsJLabelOfferID = new java.awt.GridBagConstraints();
		constraintsJLabelOfferID.gridx = 1; constraintsJLabelOfferID.gridy = 1;
		constraintsJLabelOfferID.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelOfferID.ipadx = 6;
		constraintsJLabelOfferID.insets = new java.awt.Insets(28, 4, 12, 4);
		add(getJLabelOfferID(), constraintsJLabelOfferID);

		java.awt.GridBagConstraints constraintsJComboBoxOfferID = new java.awt.GridBagConstraints();
		constraintsJComboBoxOfferID.gridx = 2; constraintsJComboBoxOfferID.gridy = 1;
		constraintsJComboBoxOfferID.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxOfferID.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxOfferID.weightx = 1.0;
		constraintsJComboBoxOfferID.ipadx = 19;
		constraintsJComboBoxOfferID.insets = new java.awt.Insets(24, 5, 7, 110);
		add(getJComboBoxOfferID(), constraintsJComboBoxOfferID);

		java.awt.GridBagConstraints constraintsJButtonCustomers = new java.awt.GridBagConstraints();
		constraintsJButtonCustomers.gridx = 3; constraintsJButtonCustomers.gridy = 1;
		constraintsJButtonCustomers.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJButtonCustomers.ipadx = 6;
		constraintsJButtonCustomers.insets = new java.awt.Insets(23, 111, 6, 6);
		add(getJButtonCustomers(), constraintsJButtonCustomers);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	initOfferComboBox();
	getJPanelHourOffer().getTableModel().setEditable( false );

	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (8/9/2001 4:34:29 PM)
 */
private void initOfferComboBox() 
{
	try 
	{
		java.util.GregorianCalendar nowCal = new java.util.GregorianCalendar();
		nowCal.setTime( new java.util.Date() );

		com.cannontech.web.history.HEnergyExchangeProgramOffer[] offers = 
			getHistoryData().getEnergyExchangeProgramOffers();


		for( int i = 0; i < offers.length; i++ )
			getJComboBoxOfferID().addItem( offers[i] );		
	}
	catch (Exception e) 
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally 
	{
		getHistoryData().gc();
		setHistoryData(null);
	}


}
/**
 * Comment
 */
public void jButtonCustomers_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getSelectedRevision() == null )
		return;
		
	java.awt.Cursor savedCursor = com.cannontech.common.util.CtiUtilities.getParentFrame(this).getCursor();
	com.cannontech.common.util.CtiUtilities.getParentFrame(this).setCursor( new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR) );
	final javax.swing.JDialog dialog = new javax.swing.JDialog( com.cannontech.common.util.CtiUtilities.getParentFrame(this) );
	
	try
	{						
		JPanelCustomerResponses panel = new JPanelCustomerResponses()
		{
			public void exit()
			{
				dialog.dispose();
			}

		};

		panel.setRevisionRowData( getSelectedRevision() );
		
		dialog.setContentPane(panel);
		dialog.setTitle( "Customer Responses for OfferID : " + getSelectedRevision().getOfferIDString() );
		dialog.setModal(true);
		dialog.setSize(560, 530);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
	}
	finally
	{
		com.cannontech.common.util.CtiUtilities.getParentFrame(this).setCursor( savedCursor );
	}

	dialog.show();

	return;
}
/**
 * Comment
 */
public void jButtonOk_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	exit();

	return;
}
/**
 * Comment
 */
public void jComboBoxOfferID_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	//always clear the table
	clearValues();

	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;

	java.awt.Frame frame = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
	java.awt.Cursor savedCursor = frame != null ? frame.getCursor() : null;
	if( frame != null )
		frame.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

	try
	{	
		com.cannontech.web.history.HEnergyExchangeProgramOffer offer =
					(com.cannontech.web.history.HEnergyExchangeProgramOffer)getJComboBoxOfferID().getSelectedItem();

		java.util.GregorianCalendar nowCal = new java.util.GregorianCalendar();
		nowCal.setTime( new java.util.Date() );

		java.util.GregorianCalendar offerCal = new java.util.GregorianCalendar();
		offerCal.setTime(offer.getOfferDate());

		//be sure the current offers date is less than todays date by at least 1 day
		//  we will not allow todays offers to carry on!
		//if( offerCal.get(java.util.Calendar.YEAR) > nowCal.get(java.util.Calendar.YEAR) ||
			 //offerCal.get(java.util.Calendar.YEAR) == nowCal.get(java.util.Calendar.YEAR) 
			 //&& offerCal.get(java.util.Calendar.DAY_OF_YEAR) >= nowCal.get(java.util.Calendar.DAY_OF_YEAR))
			//return;


		conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
		stmt = conn.createStatement();
		offer.setStatement(stmt);

		com.cannontech.web.history.HEnergyExchangeProgram program = offer.getEnergyExchangeProgram();
		program.setStatement(stmt);
		
		com.cannontech.web.history.HEnergyExchangeOfferRevision[] revisions = offer.getEnergyExchangeOfferRevisions();

		for(int j = (revisions.length-1); j >= 0; j--) 
		{
			double amountRequested = revisions[j].getAmountRequested();
			double amountCommitted = revisions[j].getAmountCommitted();

			RevisionHistoryRowData row = new RevisionHistoryRowData();
			revisions[j].setStatement(stmt);
			

			//since most of the getters for revisions and programs query the database,
			// we must do all the getting and store the values here, could be performance hindering??
			row.setProgramName( program.getProgramName() );
			row.setAmountCommitted( new Double(revisions[j].getAmountCommitted()) );
			row.setAmountRequested( new Double(revisions[j].getAmountRequested()) );
			row.setExpireTime( revisions[j].getExpirationDateTime() );
			row.setNotifyTime( revisions[j].getNotificationDateTime() );
			row.setOfferID( (int)revisions[j].getOfferId() );
			row.setRevisionNumber( revisions[j].getRevisionNumber() );
			row.setHistoryProgram( program );

			//set our price amounts and amount offered for each revision
			com.cannontech.web.history.HEnergyExchangeHourlyOffer hrOffers[] = revisions[j].getEnergyExchangeHourlyOffers();
			double[] price = new double[hrOffers.length];
			double[] amount = new double[hrOffers.length];
			for( int y = 0; y < hrOffers.length; y++ )
			{
				price[y] = ((com.cannontech.web.history.HEnergyExchangeHourlyOffer)hrOffers[y]).getPrice() * .01;
				amount[y] = ((com.cannontech.web.history.HEnergyExchangeHourlyOffer)hrOffers[y]).getAmountRequested();
			}

			//set the price and amount with their returned values
			row.setPrice(price);
			row.setHrAmountRequested(amount);

			//add our row to the table	
			getRevisionTableModel().addRow( row );
		}
	
	}
	catch( java.sql.SQLException e )
	{ 
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e ); 
	}
	finally
	{
		if( frame != null )
			com.cannontech.common.util.CtiUtilities.getParentFrame(this).setCursor( savedCursor );

		try 
		{
			if (conn != null) conn.close();
			if (stmt != null) stmt.close();
		}
		catch (java.sql.SQLException se) 
		{
			com.cannontech.clientutils.CTILogger.error( se.getMessage(), se );
		}
	}


	return;
}
/**
 * Comment
 */
public void jTableRevisions_MouseClicked(java.awt.event.MouseEvent mouseEvent)
{
	if( mouseEvent.getClickCount() == 2 )
	{
		getJButtonCustomers().doClick();
	}
	
	return;
}
/**
 * Comment
 */
public void JTableRevisions_MousePressed(java.awt.event.MouseEvent mouseEvent) 
{
	getJButtonCustomers().setEnabled((getJTableRevisions().getSelectedRow() >= 0));

	if( getSelectedRevision() != null )
	{
		getJPanelHourOffer().getTableModel().setRowData( 
			getSelectedRevision().getPrice(), getSelectedRevision().getHrAmountRequested() );
	}
	
	
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		JPanelCreateOffer aJPanelCreateOffer;
		aJPanelCreateOffer = new JPanelCreateOffer();
		frame.setContentPane(aJPanelCreateOffer);
		frame.setSize(aJPanelCreateOffer.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseClicked(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTableRevisions()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseEntered(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseExited(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mousePressed(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTableRevisions()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseReleased(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 11:51:18 AM)
 * @param newHistoryData com.cannontech.web.history.EnergyExchangeHistory
 */
private void setHistoryData(com.cannontech.web.history.EnergyExchangeHistory newHistoryData) {
	historyData = newHistoryData;
}
/**
 * Insert the method's description here.
 * Creation date: (8/9/2001 5:10:21 PM)
 * @param offerid long
 */
public void setSelectedOfferID(long offerid) 
{

	for( int i = 0; i < getJComboBoxOfferID().getItemCount(); i++ )
	{							
		if( offerid == 
			 ((com.cannontech.web.history.HEnergyExchangeProgramOffer)getJComboBoxOfferID().getItemAt(i)).getOfferId() )
		{
			getJComboBoxOfferID().setSelectedIndex(i);
			break;
		}
	}

}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G51F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DD4DC651528181098A309B1E2CC9A34314BB932EB3A46B6F5639A592CC6431E522EB659EEECE9E55575204DE953F3F01BF6BFE6889003799112E07E9CCDB4BF7C87AC84E393E05090A7C2E1C0F4316105F94040B0EFB26F71179058FB3F1FB7EF06998851F04E4DFB5F7DFE6E7D6E3D5F7D795E04643C933DB95514C3C86AE6427F4AD3611F0F8819396607F384AECC0ECBA7063FF781F61222DDE9
	700C043E1845F169BB483434CCE8AF01F67BF762527740FBB639ADEDE9853C04209381FDDFC7BE3DEDE2BA2F6C94F4B2E8FB38BA8B1EB8A986F0785CA8A37DB955399C9F4F7182D2CD042C6B44B131557BB92E825A8DG73GE2BA597815705CC2A95FDAD94147354D09A69B1FB9E2FA9357B12CA940524566DA7953C29ECACD6EC2DA4FD1BEF14E8834178160664D646BE6DC7BD4C3EF57DFBA143E428E496DD65C1A5C51DDA84BC39D5D123BCBAE9CF04A03EAE565398649AD3BDA2051A88FB8D52762D63B3BF1
	6D83B519F4ADC20A213D1162160FA03DD8F8DF8530DE47579244D7B8E13FG5DCE31470F0F1C62FBE83D7290593456ED5E9EE20FE1CE36379827EF0F8966BFE43CCD75F1BC9B651E84FD19BDF1697B814EG348304816C23F6D07256F1F8D6DD54EA143EBE45DD6571D43B9C5E730AC3F641FBE5A574F0DC235C21F89D04B0DD7FE7F5FE90FEA682B66F3F27BC0F49A426D468BD3671FBE47D431F19B20248E4FDCC31A92A0749E2E90FCFA60C76FBB521E8A774785B25BF6DAF0811F672B4453CB9886DE54F9DB3
	A571F5D28C340F76222EFFBB063A4E0577D4EE9F8C3F0962DD86BC3351FF26B6036D9168CB6C9536513ACD70E2CBDBC0F61458024AA1AA2E4AE4F5B19E5ADC013C7C085ADD040B515C2A737294454798700C174FA89E5B2B21AF3FAFAE1D59D769AEBC3311500E85D885309EE093C02E9B4639050DDDFE2B689AED2CC172EA557DCE17C37692B637B4FBB7BC65A14DAB5539D5CDF2F748AD1257A9DDF449AA61E3CEE5238CAE7A4D2B22365F81B4BE155D32D7526008426AD21F2C49DE150DB9173D9347F8E55AEB
	F77649C0204FC3443A0D5959289BC9551ABD8EC913FDA3D87FD7592813812985D60485601D4925FDB74A4B867BF7GE83A9C1E236BFDA6FB41F5D4D65A9425375FE3471D10D5EE1473C96ABBA260BD5AADE45C3687F119CADC3AC5916BE4653CE5D8476A6CC076A56F30B8E3A9E368174B94E1B32F358A1B313E79G49FF6EDCD01BC130AB4CD6DCCA6059B191DC9379E26DD5A32DA14ED219927CACCD5DD7BC9F1206799B2E3FCD5755B339282BF94073D20084DD461F66D8184DD6CB2AAC2C345E8B86A6BBFC92
	E7FCBECEF2B8CF98DBB7F37E37687B78B75A7FB22C3D439317DE81508E906191327CAB917BB4874C8F457B1EA94B32470F06D8FF6B8473B6CF01861359473BB0CE498332DB334A9E596D00971577D9F7A1BDFE0EBB95FD4EB90A7740392F0103266A7806DDF8B6A5CD73BAAF76EBF2132CE9CEF7172F3F1552D27BAFFAFCAEC3503A308BF9D786615C6B639D3B704CF4A8EE37CC1527CF617D1D744C4AAE398F182ED17A5D9A433BF6235D774A435CE7F8F6E3A2B4A0397A517F303CE12904F274F81DFDE0926842
	883904673921A657A0232520EF4CFF1E05674B9EF1B6878F0933F97872FDE4650577CD41E2CB5483D65359CB5C1FDF721DCD461334A7B86FDD54F7F03C4AF8ADADC23932742586BD1FDC5F59C965444E03F9AF2EB582B4A281E2AE097370429E6C97BE079F049A4565E26254651C103B8375A2DF6A1741D523F37BCC175D5A5C37827ADA389CD97FAFF3D10EAE03DF9C71229CCD29A2B7DC897C2C83C83944E4D961E532E0631E54472D06F6A2GD15938B32AF15CCC1398B78B70CBGD673F1E91AF15CF93398E7
	5358B8074646C57B0D5B11A6463D8CF8528F3E3A1F0DEB6FE763186CFFF78C6D016617BD5200B4D4288E02CD0BE412650F0F52980067B336DF6409F79620CE12203D89A0E7C0584ECF5E94F9623C5EE812785D3DA1F3617C8146C743004FF65AF60ABC717CBE249989FD5183C25FDF3DED0CD5D52A2AF4B8E918643A5A3257E8DB8C37ED6F9B706CF4FAD52D8E8E7F1050EB4EDE649763A8C7733CA2AFB50B4BE69950CD86889F247B9D4DB170FE98F0B6G8F00192EB7B775847A86EAF8056C51A0FBBB95EF1F6C
	B80A12AD148635C26A078A2BE8F7AD3E9F93FA58E845B357D35D2FE90A3B3E77C2D540923650EBFCF42C05497E7E6CB31C3F78FFD8C0CE0DE51B026568915B8F186287F9ADB468137D48BF261B0F3E7A078CA6075F68F2B8B39417BEB4A4647053B70CF2D8BA447202F5C3C28EAD26EFA607EDC327D18E1015DDD42A15A16A886A2CD39487576543A63107E4606F70B01C836A83DFECB47A40B6400781AC81D8BEAC763157DCG4FBE25DF158B693F8DDED9D5476F61BC7609AD70BDBC73CEA57AB36AAE74D26C42
	F963AA017C7B16C176B97F83E641FF827015F9D97076FCA0EFB5AEE7C76F35712656A1EFBD41F8932BDC93EFA753F47BG3E06AE8B7BB8FABA701C20FDAC55F90FCE7BE67631784CE95FB92969D7B5258FA266D46D0367C8FA6D356AB2BB876BAE7B7244644B63F3C6D613BCD108FBA7D7D8CD72260ECF20784386BC1355BD54D7E27BAC74AD3BA27268757B518EADA3F16927G6CGAE00D9G31A34267AD5CDBCAA629D36A5CD0BE38A5D713DF62A16A05FDA8F74895F83BE59F4A11668AC464E1A52901B5686A
	917F3DA78534593C5363669D8CE8E7DE751F47F4383D89779FA466B02D22EC69B94DAA8A4CD55016AC30E699G0BCD4F0C0A58E43BAAE25055CF0CB1E8457693262DC17CA0C2B867EF49D51F9F6C566FAA269D44981A84FDE4946AC700B200D6G8DA0EAD468A8F15F47136908DA1EDD29815CD948A5EA82F3BEDD263F3FCEDD18CD1369E279E8B0DDF4397685570570400449F94BD6F10714B0AAEA049F0D23F9ED3CBEB5A92F19E341F86D3992A2D7E109993519EE55E8B316B1E6B3B6F8360F891BB958A476B2
	B4A67219BEBB1E512550DE8D10B8A676B8E23E3EBD4E9CB7AFF00FA9E3C14FD45A7551BBBFEEDEA03D9DE17E7318ACAE1CC6D96039B98A7D2C667F994D775A21BD22636EBACE73B1E8AF51F16D7908DB8D6D44B0A12F278AD17EE9B741F981A8833083F8GE65DA4467C0C0E417CA966A696FB63EF6239F13F983338209D1E38C66019AD8B02B302BDC4FF1A018FFF59BDCAE9852BADB3A620330D042E07513650673CE940B1DF71DF7BC35723497B0357233F5D9F3C9ECD593F1361E18D63AD1568A787A62ED7B3
	8F842BD78FD07E1F5E3D1D2E8D590ACA1D17D552A45E7712360B8429DBBF177AE43E77EC521D3A050F2955141A933A3DD90BB2392C13EF92E76F97D7E95C3C9962A0007DE6E183BF9E8D34E99774055D4C74B76F6671FAE6FEBB5702B9C88C742F82D88F3091A06796612F3FF3E05218DABBB4C1CC0DBC98DA07F79F8C2E43C5875196D4255F5BA1876AE94941DC3D4F97996AB4398FC54A467CBD1D2FC95EAEF91C1E1751792CAF547C478E063285262FFC13D1DFA57A595836F5223B7132DB466B002D3741EF3D
	D222471BDA3421A0BE3C29438B15BBD61D3A2DC42BA2361F3DC558CAC9A35ACA983423G165EA2EC65D053C4FC2E8E42A74B59FEC57D7E7ADBD84E162A63DF2178178DF8164B29F6711DE6A37435060B3C76E133F13FE53A9C86520C788FF539BACEA33FAB427D79CA606DA4F82684743175DE2DC1BB70F8E5280B3C322349F99965E53D95F9CB37223C3260BD77D641D76CF13C9BF2C943CA3F86BC53F16F66099899816DE80055G6BEFB55E8BFCFF907DD727D76A8375B4F9648E12B08D678E660B3927205DG
	60810884D8BACD50349EC4BE588DC2FDAF439DFFFB3B0EBB8B164EEEF8D85F87F6BC2F868B508B93567F73FD3867F3B669A22C478AC21F1D95625D4E3900FA11753DE723FD8135824B931E9CB366893953D91E508ACF57F4D14BD81A0D35CCB89F93B3DD58DCDDD1204D2D186E53D94274D0B657B8CA6FCE2687FEF7E923780D53833F3BA4DD955FDD52A2A00709907177C51A7B46C3BBD147950DA1EEA33453E6885C3CB19127F74C90BE50E2896D430ED8027B3082CB70B8D4EA19B88EFDE48965FB98FF6FEF95
	7C154D9032FDC095328D87DC548CFFD9464E88A55B8728FFD8B9A3D0368FD1FC420CC05916B78A59A6C21FF52610D9E51360A9E6266069A5BB514F6F86437B30BD54B701EF83980D39B714865B73951A8B4643FE13F4B97C1A62B6C01B5CA6F01F1E91324952F1EDA308EB033687A07CB67CB61FFF046867F466F18BBC5D7220C52F4C2D4EBE590D477C6807E56579168FCBCA0F317AADFA5078BD3D620D7220774F542F5F466CE0BDFF2E18FE086B35AECF78691CD9107F4F925F53CB5A312653638FB2D2D97929
	576928163AEC54CB3258B3A7AF134F8F1FA5E24F63F44D9550FE96A0F91628B707BE9075E66C779690DB58E7C16FBC936DA7CCE937B3BE0F5E6E2BB713205EE4B497658B1A8E68DFF8BB6256649BFD904A521CC6C8A1BCD97F9F7D7A711C9DA2467E2282E3BF727B295FFC492F9FE5F4124FE7FBDB733E585B26B5B144754849107739C9FCCF1933793DEE4BA1521E3BA90349FDF3F69919526F9874A041E47327B1D1331D19AD7471089775B1846DC8001859C2473F6B8CD1A7528842567B22D96421ABE76B758C
	4DCDD3201DF50740EDAA9636DEFC0770832D23787DA5CCEFDF24ED7C86405A2FDC1D4AD945F5266E8F4C23D321B155EF136C4C7E114617BE378CC93233036598B9D48F3F84083AC3447BCF6837DD8D8286BB77B63CEEA4B1F76078877D62ECA09C0E44B12736DAA79833D958BD078D30EE9A4CBB447BF2617D8837C9263395B4EFF481BEB2D268F117C6FFEF846DF89D6751747AB5D2686E41C65CCB719C51FE043671BB8BEBFF5CA8640EE33EEDBF4CE834B49A3F0BFE5BB4D89E7757A621979B6C239DA08CA09A
	E0659C7D1B46E94C81B5C5F1591D9EBB2817B07DAA78739CEEA32F1F76577D62BC226B75EFE7A652EBC600DED127C22F284BC460C3789AC6CB9D41B9329BEFFB9CBC671DB9A2FCE6729C610FFF7CFF467831E3413BA611438772C066B29DE64D7579E3D6C34E213E788C605B660A1C6952915CDC0B5443AB46DA07D3D33C34E484E403D77148DEED18E169D7C42E4F7A3C6B7BDD525757B9EF7E1467319C29B41FFEE71D9BD88B1D227808390135503F6AB1F69E74254C95775AF185E8DBEDF746258F81C48244G
	2C86C83AD3487251A345E4129A5C2EF8EC10203A8CA9A55B5F7D795777FB2DA7B2C4ACDB76687DE4793457D3C35D8B275C496CA1F79EFB3EECBFC471ECFF4D74DE7AAC74B9GA2G22811681444F937BB39D4DE77BC3F32852B429231BFE15941B431F697950D8E0F13FCEA65ADB623CD0F96FB115568E7382F5F7026249DD013A7BA72F50DD9674F96E927E2D2DB9987DE76F62F238ABD43DBFD8B43E4E1C2D5F5FEF02F9B9736352CF81B44CE75FCE190F48AE1948C7085F0588DF518DF3C766B39F016DC8F8DF
	83AF7F8BFF0C0F7037D1576744F7D41D3F3CB1FC9ED10EF0DBD87D35B12F316425854DEBD23EAEB2AD1C4FFF6FB95F67A3F63C3EA9639D3AAE64BBC47CEDB8BD9EC7384F9C42AB95CE7BD7D902767E876FA556352F278A1F97387F0D1C2675EE9F6D43C01B6D695FD57C4D4DGB89DE70764E265F33564EDD0DC4E0EE16E9FDF2331903F970E5CAD7CFA3E06F65C2DF81D17A1879BBFD35CA71C2571913FED2C38DB68EB3AB6193E44EF05701DC4015E23FC7ABA8A6F9990B09E5375F2EFA93D97707F9E496FFF86
	1B04EFAF0B92797083CD0179701CD4E3BE3B36A9B09F46793E7EAD7640FC58FF7EC77640FCD84CE7FAFC952FB2B96F770DE0FF2764D2718E06790685F855456B5E97725EA7A16285F800F3101A56DF6C11BBB43B54254F191F0FF475DA190591F107C8F3FD275B29B9A51773B27D69C3938CF4F73133FB920FAE0F81550677B5F9B73EBECD3A21B4587E5794043E3378E9C170BB0BF585416FACFED1B0715D780B8521FF4B75EA017137DCCC6FFFD7063AF1011DC6G44822C84F8B6CA4403AB056F919A8F024402
	DAC7975E7B62AFC9B5C17FEFC52177AA97CD7E7BD3F7917E6E652282EE308F5797F82D887525A99E0AE43861A214115759552D31219CBFC66BA1175CC951ACE75ECBC44E3CA1CA440CA255E7EB950677A73CB75E6EFEFEC3E930BD2B34D6BCF38F442EFBC46D302ED948218DF0AC267E20C84454D840A55DA36A515293FE5F3B591D43A7AEE5504643A577F7FFAA92B56E7E8522462DAB46B56D50F6810483DCDB5C59F01A792B7ABF935DC60F15E3BCDE88EBAC87D80FB4GB26F0DCB375CAB6CF4FB71D192BA47
	E58CD3DB75CACE3746563D3D82E3C2992C51G20814C820886D88E908F10883091A0EBA14491008AG9BC0BB0087A09CA0EA2160613112E3937040AFEBFCCC90610FB7EB98E48DA5FF2161CE3930253651DED7D3E53B50D453D8EF33D5D7B5DE2832FDD175E513BE7F02766789661F2DEF2C7B2A7EF33B7F8ACD97BE2F2D355602G26261757AAFD67E4E442EF97217D61C3E521FD448F4AA677910F17515F4B4159368FFB78F722D5E51847A91D1D2A2C7B22274BF66838BA35C1D11D98F9D8BED0C226123F4502
	0ED7AE7465834FAEE47940D613FECF586CDFCF527FF6426B495FB4CFC6EB42FAF2212F1EE4F2DFB16E7E7E5AFC4EB357B94FDCEC1C173CF0727B831643BFC6FDGD994185BBFC971D90B82F37B7AE26107F221EFE411B8D7A74AC35B4B4972496D253ABCF8CCB9DBBEBE261CAF8F9ED31A4A0DB105616E2B884DD7CC4564FCFD3FA2B8DFBF2C984F570A0A60FC45D50C676B298A745FDD60EFDA75F171BA6ECB9D77933AA6623EF0BA34EE9D3F1672057833B292947867A9DF0394574AF13FB6603E643817AA70AC
	4216A1D597F313AB70FCF4BADDAE565EC047B85DF0B4F5F97C1E726874C8863E7FC747A15FD373D54DD88208BA66AF784EF381FAB71A380855954C467F854BFA7E9EEFF8EFAE3E7139007B0652E07E292498F58D69F7179BF3B8427E5FC3B6D91CFD928EDC6C2BE4336AA5EF0D7F46D1B57BBF5646DE30661808290E24F7D3BD57114D03E8B0C39EC2700A4AF4C8A3267ADE0D6C435B81CB154BE591DFADAD0D326A0108A72B9A4952BFE596969682356DBA35BAEA860983251566973EEBB611B4A0450EC59DC9C3
	5B860ADEC6598C478269D25121E93881F255CBE41436E890604FBDBA967FD27963305F58B47E4C50C8A67F86DB298FF817132003F5EC961F06005FE4D8EDFCEC4CF8F717F2EBEE48EFBF490B197FA45101F7F79F6CC63BBC857856E8617B86F6E18D9C2A96EB6A13DCAE2804FB2B5C8E2C4B2A07B559D78B646D4675D5B106F7F21F3A9B796EB14C79FFD0CB87882E7BE9653094GGA8BBGGD0CB818294G94G88G88G51F854AC2E7BE9653094GGA8BBGG8CGGGGGGGGGGGGGGGGG
	E2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6A94GGGG
**end of data**/
}
}
