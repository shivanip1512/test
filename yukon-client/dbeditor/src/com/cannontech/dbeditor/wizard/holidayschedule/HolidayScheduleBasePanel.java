package com.cannontech.dbeditor.wizard.holidayschedule;

/**
 * This type was created in VisualAge.
 */
public class HolidayScheduleBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, java.awt.event.MouseListener, javax.swing.event.CaretListener {
	private javax.swing.JDialog holidayCreationDialog = null;
	private javax.swing.JLabel ivjJLabelAssignedHolidays = null;
	private javax.swing.JLabel ivjJLabelScheduleName = null;
	private javax.swing.JPanel ivjJPanelHoliday = null;
	private javax.swing.JTable ivjJTableHolidays = null;
	private javax.swing.JTextField ivjJTextFieldHolidayScName = null;
	private javax.swing.JButton ivjJButtonRemove = null;
	private javax.swing.JButton ivjJButtonCreate = null;
	private javax.swing.JButton ivjJButtonEdit = null;
	private javax.swing.JScrollPane ivjJScrollPaneTable = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public HolidayScheduleBasePanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJButtonCreate()) 
		connEtoC2(e);
	if (e.getSource() == getJButtonEdit()) 
		connEtoC3(e);
	if (e.getSource() == getJButtonRemove()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTextFieldHolidayScName()) 
		connEtoC5(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JTableHolidays.mouse.mousePressed(java.awt.event.MouseEvent) --> HolidayScheduleBasePanel.jTableHolidays_MousePressed(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jTableHolidays_MousePressed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonCreate.action.actionPerformed(java.awt.event.ActionEvent) --> HolidayScheduleBasePanel.jButtonCreate_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonCreate_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JButtonEdit.action.actionPerformed(java.awt.event.ActionEvent) --> HolidayScheduleBasePanel.jButtonEdit_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonEdit_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JButtonRemove.action.actionPerformed(java.awt.event.ActionEvent) --> HolidayScheduleBasePanel.jButtonRemove_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonRemove_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JTextFieldHolidayScName.caret.caretUpdate(javax.swing.event.CaretEvent) --> HolidayScheduleBasePanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
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
 * Creation date: (1/22/2002 3:05:37 PM)
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getHolidayCreationDialog() 
{
	if( holidayCreationDialog == null )
	{
		holidayCreationDialog = new javax.swing.JDialog( 
			com.cannontech.common.util.CtiUtilities.getParentFrame(this),
			true );

		HolidayDateCreationPanel panel = new HolidayDateCreationPanel()
		{
			public void disposePanel()
			{
				holidayCreationDialog.setVisible(false);
				super.disposePanel();
			}

		};

		holidayCreationDialog.setContentPane( panel );
	}
		
	return holidayCreationDialog;
}
/**
 * Return the JButtonCreate property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonCreate() {
	if (ivjJButtonCreate == null) {
		try {
			ivjJButtonCreate = new javax.swing.JButton();
			ivjJButtonCreate.setName("JButtonCreate");
			ivjJButtonCreate.setMnemonic(67);
			ivjJButtonCreate.setText("Create...");
			ivjJButtonCreate.setMaximumSize(new java.awt.Dimension(81, 25));
			ivjJButtonCreate.setActionCommand("Create...");
			ivjJButtonCreate.setMinimumSize(new java.awt.Dimension(81, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCreate;
}
/**
 * Return the JButtonEdit property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonEdit() {
	if (ivjJButtonEdit == null) {
		try {
			ivjJButtonEdit = new javax.swing.JButton();
			ivjJButtonEdit.setName("JButtonEdit");
			ivjJButtonEdit.setMnemonic(68);
			ivjJButtonEdit.setText("Edit...");
			ivjJButtonEdit.setMaximumSize(new java.awt.Dimension(65, 25));
			ivjJButtonEdit.setActionCommand("Edit...");
			ivjJButtonEdit.setMinimumSize(new java.awt.Dimension(65, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonEdit;
}
/**
 * Return the JButtonRemove property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonRemove() {
	if (ivjJButtonRemove == null) {
		try {
			ivjJButtonRemove = new javax.swing.JButton();
			ivjJButtonRemove.setName("JButtonRemove");
			ivjJButtonRemove.setMnemonic(86);
			ivjJButtonRemove.setText("Remove");
			ivjJButtonRemove.setMaximumSize(new java.awt.Dimension(81, 25));
			ivjJButtonRemove.setActionCommand("Remove");
			ivjJButtonRemove.setMinimumSize(new java.awt.Dimension(81, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonRemove;
}
/**
 * Return the JLabelAssignedHolidays property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAssignedHolidays() {
	if (ivjJLabelAssignedHolidays == null) {
		try {
			ivjJLabelAssignedHolidays = new javax.swing.JLabel();
			ivjJLabelAssignedHolidays.setName("JLabelAssignedHolidays");
			ivjJLabelAssignedHolidays.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelAssignedHolidays.setText("Assigned Holidays:");
			ivjJLabelAssignedHolidays.setMaximumSize(new java.awt.Dimension(106, 16));
			ivjJLabelAssignedHolidays.setMinimumSize(new java.awt.Dimension(106, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAssignedHolidays;
}
/**
 * Return the StateGroupNameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelScheduleName() {
	if (ivjJLabelScheduleName == null) {
		try {
			ivjJLabelScheduleName = new javax.swing.JLabel();
			ivjJLabelScheduleName.setName("JLabelScheduleName");
			ivjJLabelScheduleName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelScheduleName.setText("Schedule Name:");
			ivjJLabelScheduleName.setMaximumSize(new java.awt.Dimension(103, 19));
			ivjJLabelScheduleName.setMinimumSize(new java.awt.Dimension(103, 19));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelScheduleName;
}
/**
 * Return the JPanelHoliday property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelHoliday() {
	if (ivjJPanelHoliday == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog", 0, 14));
			ivjLocalBorder.setTitlePosition(2);
			ivjLocalBorder.setTitleJustification(4);
			ivjLocalBorder.setTitle("Holiday Dates");
			ivjJPanelHoliday = new javax.swing.JPanel();
			ivjJPanelHoliday.setName("JPanelHoliday");
			ivjJPanelHoliday.setBorder(ivjLocalBorder);
			ivjJPanelHoliday.setLayout(new java.awt.GridBagLayout());
			ivjJPanelHoliday.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
			ivjJPanelHoliday.setMinimumSize(new java.awt.Dimension(350, 330));

			java.awt.GridBagConstraints constraintsJLabelAssignedHolidays = new java.awt.GridBagConstraints();
			constraintsJLabelAssignedHolidays.gridx = 0; constraintsJLabelAssignedHolidays.gridy = 1;
			constraintsJLabelAssignedHolidays.gridwidth = 2;
			constraintsJLabelAssignedHolidays.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelAssignedHolidays.ipadx = 20;
			constraintsJLabelAssignedHolidays.ipady = 1;
			constraintsJLabelAssignedHolidays.insets = new java.awt.Insets(8, 10, 0, 63);
			getJPanelHoliday().add(getJLabelAssignedHolidays(), constraintsJLabelAssignedHolidays);

			java.awt.GridBagConstraints constraintsJButtonRemove = new java.awt.GridBagConstraints();
			constraintsJButtonRemove.gridx = 2; constraintsJButtonRemove.gridy = 0;
			constraintsJButtonRemove.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonRemove.ipadx = 4;
			constraintsJButtonRemove.insets = new java.awt.Insets(3, 7, 8, 40);
			getJPanelHoliday().add(getJButtonRemove(), constraintsJButtonRemove);

			java.awt.GridBagConstraints constraintsJButtonEdit = new java.awt.GridBagConstraints();
			constraintsJButtonEdit.gridx = 1; constraintsJButtonEdit.gridy = 0;
			constraintsJButtonEdit.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonEdit.ipadx = 20;
			constraintsJButtonEdit.insets = new java.awt.Insets(3, 4, 8, 6);
			getJPanelHoliday().add(getJButtonEdit(), constraintsJButtonEdit);

			java.awt.GridBagConstraints constraintsJButtonCreate = new java.awt.GridBagConstraints();
			constraintsJButtonCreate.gridx = 0; constraintsJButtonCreate.gridy = 0;
			constraintsJButtonCreate.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonCreate.ipadx = 4;
			constraintsJButtonCreate.insets = new java.awt.Insets(3, 16, 8, 3);
			getJPanelHoliday().add(getJButtonCreate(), constraintsJButtonCreate);

			java.awt.GridBagConstraints constraintsJScrollPaneTable = new java.awt.GridBagConstraints();
			constraintsJScrollPaneTable.gridx = 0; constraintsJScrollPaneTable.gridy = 2;
			constraintsJScrollPaneTable.gridwidth = 3;
			constraintsJScrollPaneTable.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPaneTable.weightx = 1.0;
			constraintsJScrollPaneTable.weighty = 1.0;
			constraintsJScrollPaneTable.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelHoliday().add(getJScrollPaneTable(), constraintsJScrollPaneTable);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelHoliday;
}
/**
 * Return the JScrollPaneTable property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneTable() {
	if (ivjJScrollPaneTable == null) {
		try {
			ivjJScrollPaneTable = new javax.swing.JScrollPane();
			ivjJScrollPaneTable.setName("JScrollPaneTable");
			ivjJScrollPaneTable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneTable.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPaneTable().setViewportView(getJTableHolidays());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneTable;
}
/**
 * Return the JTableHolidays property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableHolidays() {
	if (ivjJTableHolidays == null) {
		try {
			ivjJTableHolidays = new javax.swing.JTable();
			ivjJTableHolidays.setName("JTableHolidays");
			getJScrollPaneTable().setColumnHeaderView(ivjJTableHolidays.getTableHeader());
			getJScrollPaneTable().getViewport().setBackingStoreEnabled(true);
			ivjJTableHolidays.setBounds(0, 0, 200, 200);
			// user code begin {1}

			ivjJTableHolidays.setModel( getJTableModel() );
			ivjJTableHolidays.setDefaultRenderer( Object.class, new HolidayScheduleCellRenderer() );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableHolidays;
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2002 3:57:28 PM)
 * @return com.cannontech.dbeditor.wizard.holidayschedule.HolidayDatesTableModel
 */
private HolidayDatesTableModel getJTableModel() 
{
	if( !(getJTableHolidays().getModel() instanceof HolidayDatesTableModel) )
		return new HolidayDatesTableModel();
	else
		return (HolidayDatesTableModel)getJTableHolidays().getModel();
}
/**
 * Return the StateGroupNameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldHolidayScName() {
	if (ivjJTextFieldHolidayScName == null) {
		try {
			ivjJTextFieldHolidayScName = new javax.swing.JTextField();
			ivjJTextFieldHolidayScName.setName("JTextFieldHolidayScName");
			ivjJTextFieldHolidayScName.setHighlighter(new javax.swing.plaf.basic.BasicTextUI.BasicHighlighter());
			ivjJTextFieldHolidayScName.setMinimumSize(new java.awt.Dimension(150, 21));
			// user code begin {1}
			ivjJTextFieldHolidayScName.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_HOLIDAY_SCHEDULE_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldHolidayScName;
}
/**
 * getValue method comment.
 */
public Object getValue(Object val) 
{
	com.cannontech.database.data.holiday.HolidaySchedule hSched = null;
	if( val != null )
		hSched = (com.cannontech.database.data.holiday.HolidaySchedule)val;
	else
		hSched = new com.cannontech.database.data.holiday.HolidaySchedule(
					com.cannontech.database.db.holiday.HolidaySchedule.getNextHolidayScheduleID() );

	hSched.setHolidayScheduleName( getJTextFieldHolidayScName().getText() )	;

	hSched.getHolidayDatesVector().removeAllElements();
	for( int i = 0; i < getJTableModel().getRowCount(); i++ )
	{
		com.cannontech.database.db.holiday.DateOfHoliday d = getJTableModel().getRowAt(i);
		d.setHolidayScheduleID( hSched.getHolidayScheduleID() );
		hSched.getHolidayDatesVector().add( getJTableModel().getRowAt(i) );
	}


	return hSched;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

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
	getJTableHolidays().addMouseListener(this);
	getJButtonCreate().addActionListener(this);
	getJButtonEdit().addActionListener(this);
	getJButtonRemove().addActionListener(this);
	getJTextFieldHolidayScName().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("HolidayScheduleBasePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(374, 444);

		java.awt.GridBagConstraints constraintsJTextFieldHolidayScName = new java.awt.GridBagConstraints();
		constraintsJTextFieldHolidayScName.gridx = 2; constraintsJTextFieldHolidayScName.gridy = 1;
		constraintsJTextFieldHolidayScName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldHolidayScName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldHolidayScName.weightx = 1.0;
		constraintsJTextFieldHolidayScName.ipadx = 79;
		constraintsJTextFieldHolidayScName.insets = new java.awt.Insets(15, 0, 3, 17);
		add(getJTextFieldHolidayScName(), constraintsJTextFieldHolidayScName);

		java.awt.GridBagConstraints constraintsJLabelScheduleName = new java.awt.GridBagConstraints();
		constraintsJLabelScheduleName.gridx = 1; constraintsJLabelScheduleName.gridy = 1;
		constraintsJLabelScheduleName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelScheduleName.ipadx = 6;
		constraintsJLabelScheduleName.insets = new java.awt.Insets(16, 8, 4, 0);
		add(getJLabelScheduleName(), constraintsJLabelScheduleName);

		java.awt.GridBagConstraints constraintsJPanelHoliday = new java.awt.GridBagConstraints();
		constraintsJPanelHoliday.gridx = 1; constraintsJPanelHoliday.gridy = 2;
		constraintsJPanelHoliday.gridwidth = 2;
		constraintsJPanelHoliday.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelHoliday.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelHoliday.weightx = 1.0;
		constraintsJPanelHoliday.weighty = 1.0;
		constraintsJPanelHoliday.ipadx = 351;
		constraintsJPanelHoliday.ipady = 330;
		constraintsJPanelHoliday.insets = new java.awt.Insets(4, 5, 12, 7);
		add(getJPanelHoliday(), constraintsJPanelHoliday);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 9:11:36 AM)
 * @return boolean
 */
public boolean isInputValid() 
{
	com.cannontech.common.gui.util.OkCancelPanel o
			= new com.cannontech.common.gui.util.OkCancelPanel();
	
	if( getJTextFieldHolidayScName().getText() != null
		 && getJTextFieldHolidayScName().getText().length() > 0 )
	{
		return true;
	}
	else
		return false;

}
/**
 * Comment
 */
public void jButtonCreate_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	HolidayDateCreationPanel panel = 
				(HolidayDateCreationPanel)getHolidayCreationDialog().getContentPane();

	getHolidayCreationDialog().setTitle("Holiday Date Creation");
	panel.resetValues();

	getHolidayCreationDialog().pack();
	getHolidayCreationDialog().setLocationRelativeTo(this);
	getHolidayCreationDialog().show();

	if( panel.getResponse() == HolidayDateCreationPanel.PRESSED_OK )
	{
		getJTableModel().addRow( panel.getDateOfHoliday() );

		fireInputUpdate();
	}

	return;
}
/**
 * Comment
 */
public void jButtonEdit_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	int row = getJTableHolidays().getSelectedRow();

	if( row < 0 || row >= getJTableModel().getRowCount() )
		return;

	HolidayDateCreationPanel panel = 
				(HolidayDateCreationPanel)getHolidayCreationDialog().getContentPane();

	getHolidayCreationDialog().setContentPane( panel );
	getHolidayCreationDialog().setTitle("Edit Holiday Date");
	panel.setDateOfHoliday( 
			getJTableModel().getRowAt(row) );
	
	getHolidayCreationDialog().pack();
	getHolidayCreationDialog().setLocationRelativeTo(this);
	getHolidayCreationDialog().show();

	
	if( panel.getResponse() == HolidayDateCreationPanel.PRESSED_OK )
	{
		getJTableModel().setDateOfHolidayRow( panel.getDateOfHoliday(), row );

		fireInputUpdate();
	}

	return;
}
/**
 * Comment
 */
public void jButtonRemove_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	int row = getJTableHolidays().getSelectedRow();

	if( row >= 0 && row < getJTableModel().getRowCount() )
	{
		getJTableModel().removeRow(row);
		fireInputUpdate();
	}

	return;
}
/**
 * Comment
 */
public void jTableHolidays_MousePressed(java.awt.event.MouseEvent event) 
{

	int rowLocation = getJTableHolidays().rowAtPoint( event.getPoint() );
	
	getJTableHolidays().getSelectionModel().setSelectionInterval(
			 		rowLocation, rowLocation );

	//If there was a double click open a new edit window
	if (event.getClickCount() == 2)
	{
		//if( event.isShiftDown() )
			//showDebugInfo();
		//else
			getJButtonEdit().doClick();
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
		HolidayScheduleBasePanel aHolidayScheduleBasePanel;
		aHolidayScheduleBasePanel = new HolidayScheduleBasePanel();
		frame.setContentPane(aHolidayScheduleBasePanel);
		frame.setSize(aHolidayScheduleBasePanel.getSize());
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
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
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
	if (e.getSource() == getJTableHolidays()) 
		connEtoC1(e);
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
 * setValue method comment.
 */
public void setValue(Object val) 
{
	com.cannontech.database.data.holiday.HolidaySchedule hSched = null;
	
	if( val != null )
	{
		hSched = (com.cannontech.database.data.holiday.HolidaySchedule)val;

		getJTextFieldHolidayScName().setText( hSched.getHolidayScheduleName() );

		for( int i = 0; i < hSched.getHolidayDatesVector().size(); i++ )
			getJTableModel().addRow( 
				(com.cannontech.database.db.holiday.DateOfHoliday)hSched.getHolidayDatesVector().get(i) );

	}


}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GB1F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E145BC8DD8D45731A412280DC954E06A8F0D18E0CAD2521A04FE75FB352DA124B52DEDED1FE9EC62571AD75B703DE79A5B5A56B4B6DEC0FECCD0099245849591A3A82E7C0B8415BF8D689AA920414697DD588B3B386C2E3BF7E1B1D45F4CB9F74E3D3B6C827ABD5F737B06FB4F1CB3E7664C19B3E7661C3BCA193BA2129392B3A5A9A1D9D27F9DCE1024B823A4BD1BF9AA0FF0A1F2EC0A247B379B60DDE973
	276B60B9916AECCF4626E4C85F29C903F2A414AB3F961B328D5E37C831B563CE40CB90BE17216E0227709B437379757B44A71515B7652743F3A3C094607039CAC67E7B7233D47CC295CF1010A8C94B3B30CDDDFE360A2B04F2B3C084C0CC97EF3F901E6B99673B2AAA55F697A7C5C82BFE38AFF18376236B1320301B53B62B4F9C699B892B3B115795A6A7520C0372FCG9CFC12F475FC83BC2B1B2FF49F6BF4749ABACDF63B432E481D96C33F751A49E5B6D89CB62B59B4606E3448E60FCD2E2AEA6408D695D1E7
	F24B4DA63BECD3AC4867697CB456AF7489E5490A38FB4A1177DCA8AF82089778168A44B7407BC5G45CA63BD3A2FDA9DCF7B975F10E2DE34A7ED8AB25E89D6BE4E982BB65EB04F5FD237333959150DF330926AB2FBE2D372810CGB60089G9FB01B7870037D702C6DD06A9D3D3D8EFB2D53D9E7B33B4EB84C328D5E2B2A20C64535481D8E17D912783CDF4DAF8CA04FF04069EE5CB49D57C9D4A959C0F44B23525227CFA72686504952282244489E2E0BF9BD1ACEB86F39C1F9AF6D7135D1DF5E1FCAFA5E11F7BB
	121283700EF921A0F1255A4F3AAB9A6F32ABB857EB198D6441FB02EA9F9C3F1E5900D7076776BA09354772B828DBF105EC23FDA349E2DCB7DD4AA8B686544354585A44E69B1741E27317657B0CE7180D73FCC748321861E3F4F8AECBBB43E3F9B154D5764626F07B4A5F096BB79C4AF381968144832459E3D372GAA6DE4E30E8F8E0D52461ACDAE254EE33519E51744E973333742D376AAAED3135D2D186C1D72F9134BEA6A3049EEC9EDD31C0DBA6870212BE0365F89BCBE166D324B24D89DF6685D54ABAB324B
	4D5B9C4FFE975B38E4D65BE66D1501C12FD322FEBF4E5E82CF1B492D1CF31ACD0A2C356075E733F1CE7ACC6721C768G5E39DE4E6DC2FD3543781DGA1C28FBFE47D1D16DD60AD2A2A0C8E47950F338DC7A2AD32231EFF4AFCC7983CC75BC9477F48C1DC26A3B62550C17DA46C582C6B27415A09621BDC83344612AB50C757B848E6FE5FCEB65330E136D478422900B603E0F6F0DB99F4782F1DCD2CCF140B173FDC5EBE4A75DB63883C36C67695F3C64DA369A67B5FA8666AFB3BF12EA2C14679G4B040E2BF764
	F01BC57FCED6FA568586A61BB50DF3B997D6E42A7AC4B912D579570BF13C446AD7C35FB94E5814C6GA740D4A76972730A76A074B7BB26603A6CE7BC7C7BFF67FF098757D949CE5C2764BE592EB448CE59EE069737DAF7E4A772D357F117C3509CE7F8A72C7BFAD8E8EE01BF31935726C9D1DC568E0FA2374A0AE235F7EB75E798AF37274329398C6255369347686807F5AF5ADF5809EB2653E1374BEC728409DA7F99DB3332CD6E85216B9D9E3B427126DDE877D7648155E7483BB0A86AB359BC68FFF88CB14704
	FAF43A2C3DE0926842243C2B381EFFA7E2A00479B05F988B2D06E77AD5DA1B7DFBE8ED66DD1BA54D3F34B7B1505EB2F5F6C3226DAADFC7A1D7353549E5BA1F93D8760B4CF728F8B717750802B2850BE1781AD81D0B7DC5G1F980005D7E9CDAC4941FA72BB6AE228F758ECDC25C2574F6D4E4039112FFAE4F0576860CE8B7DAD5C3D592F6E3C2ACBDE3FF8B76A5226730D11AE5465264394ABAE87F956G643838BEAF3A38BEF83B762354EEA514535C907738F9BB0BDB5FAE7B98351B8378853AF671BE6D9609F6
	C58A2CDDC5571F22EF97FADC74877885G718AEF1767616D387ED3AFE18C92A85E6C76D88D9E450A9E5D2440A2F1FA94DDDC59DC10267651602178313E9067E9904AE1G519E32291FED2078B16CCA04346C117703464BF12AEC719E4D26F246D07CD81707BC737BE0AF6FA39BB81B2B5F43EA5DEEC72715ED1F6A7C3D152B37B90ECB5A73B6BC3B2CAE375284CE41CBF33DFE8F4A0B7B2B0AD6630D4A43696AD80D40B794002F27FF146A5753BC40AF83D8579F1B128510447A5CFB74A0BCFB9D9E37ECE0FF1BDD
	325BAD1B73FBCCFDA60329DFB1B07FE4B803F5A77035C04CCD6BC7C76059434CCEDDA16ECBE7741D98037672D141F909624507375428B2AE7C7AF4297A461644C0717B38CDBB920B7A395E5B7BB57D8FBE1512346273B7D339AE6EB8CA3A0804B6CB8194AF62A716E76A7A4A84DCB540E5AF69A17EE8897ACF36B08D7C51AC3B3A9C2E5E213A28E555248C2E0B539E34211EBA0F22B86C7598A24917EA7D3A7157062E9FD087177F44B112FF9E48364ACB72CF7286123FF0007232DC29CA63FF42ECD5EE12BB5952
	9B02FF8D746D9DA07E9D0783711FA378D7666B465FA277BA7AEEED7CCF9CA77EF150779A41FFCD211EFF59B59E73DD3CC67317D903B1F32749A5AB8676170720EC6A3C86F7BFEC06AA57FA2CD5A70F4B9ED730192FB719F9911592F542E301F3857EF910731A962305DE9B9A2F717C60DB4C5FCC3D467303D401FF1661676870FC6CCD8C0F6585D0D778854534BF5B0B6BF8A214238162G16822C81489EA4BFB3E9CF29B4C24E5084FE53E5B759DAFD02G0A5D73D00F30EF2B6553F9289B36EFCB240738EAFFBD
	D48E7A0E3D4D2F4C69FEBE04EE27DF596BC7476725678B9CB7737D3AE92C2ABA45BC8E77F1CE375E47CD9C64F6B98F1E0BG78DEB03E144E986287E9AFD8D2063A4E799778EA0066FF112E8F6D43FD498265D000F00039GCB81D683E4DFA7FD3F10F7F4A4FDB39BEBF354832E917667FA7FD8CA68E76C51DB536B14A1F47EFA2D3E9EC82F1BDA378756ABDF3DD047F59BFAD42F5B366BFC4DF961B961BA6976876528DB5CAFA32F53FE7A2F137A7DF45E2612C40CD1826D27E9B738FC79B7B4FFDE6879DBAA9F5F
	69E33726178BC3687C75E23961CB47473240CBE38939C139465ADB14E1437111E4D89ED006E907C906B5C206270774B5BA991E99C2E770ABDF8E89E49FFB59B9E320F5877948C92BECB3F3D9CB8F26293C3C508F4F49EDAC6E1A8365B801FBEABF626221AC5DC1E37A8E73F5F9D0EE8430887C738C0F71E0689DFCADF37C59C24AED312D3F9D85033CGED9B8360B88F0B6011BA4A7EB7CA01F36964GB86EB7DE5A9BBC67FBE5EF601C6F553D01F33E57763E4B7150077EA4086A7F3CF7781C706FFB836504BB58
	181E5F3A09758DE11C1BB9B28C32553AD7157724G3961A726DED9554796694D6A75EA1B930A233ED858D677099D01ED2B3553851991C670E2ADC439493EA26FA03F9DDE027631944A2BG526FA47B382FF438F3523CBB036D51C6F6EED8F9A75F0B93847E9C43B76870FC0F7ED5991D0BB7C3DD4C1D349F9C5997FCDEEB76851E57137BF01D3B9D9ED7276CBFF7E776E509BAED67E8D264DED4331A47B2FA45646A16074C7DFFB3FADE970C3EEBDFB07B60F3785D237AB9AC91F3782702A07B32AE7F1AF794678E
	7DFE56981A3B83CC274DD036G040C2139DBB8645CEAAA54C50F61F3B6DFFDAE9EB3723A0F07B6EB7C5A71FDBD31G63470CD0082BG9AGAE828C06527C3D1DBFE27CF4429BB0FE623A3A7318DED72542277C586BBF36F060B9A7144B9827BE17040EBC369550C63A4B379D67DDF1DC4F3BCC70FE6830BF6FCC204FD77B28D61F4DF70D4C5B8CED3C81F97F278F6FF2413B72207FFA1C20230F007718007D0DA94073A21BE94061D1A09E389B6DE6FA8145A18B00E619CA17F6B70FC738CDBE590F365665B27502
	A936BA65CEE98D23FDFEBF51DA20BC88908EB08FE0695D14BBC8925E77E93B1BB86DE19E0D5AE00EF04AE86A10EDF48C449D9E5563DA64ABC025477547F217448AFD6E52B0164BED99CB767F041BF4B5883809E3FD7592B9B6186F4AA8E577E4E37D6FC85EE77885E37D6FC89EAE21BB12A5D0D7BD0E7667592534BFC70CA319DAF0AA55397CCC773E3CEC34E74C37821C475F7F8FF8BC6393BB474138170C53743C825E257131A91963B59C576DCC664372477B5F85BD41700D637D6F020A4B69AE4882F58B4713
	4EFF4F626622AF01AF7A9261CAAA90578EE50B4045B25CA0144384AE3A8FF1D1D00E9338F57DB407CB84EEF199BB0F3AA7B625669E61678A026FD36F96845E273695840EBFF6948C9FFF6CAD88363FF079DE77128E5A6EA119CB87048E846E5981524120400DA9A79DCC9438174BC9871182975ECFBAD8A5F0738F935F348904DBDFC9FC3385AE3C127896895CE3DE625BA0F029834437DD606EBFCCFC23846EAC0B1767C1F93140998F916DEA01FB251CE89B6F85BC332358820ABBAE82AE6CDE72FB4517FDE2F5
	BCDD292A7A58E5B557193A0D5C0B323DF7AD23DF88F44B81565CCB391A77C34A55229F1DAE99C3CE87BC7BDC56D61218F99FDF8B1577E939DA9464EAFC3FBFF3A0CD25D320BE7ABE44FDF1C06F77506F8CA4719C0457BF61D37F2EEAB7DA7D2FFD6AD15E579275756F7A1C8921BEBED2697958FE3417463666C7D1126D1BC7021E6B2ED24714F53F3AA71E5FA365FD694DD4B55E698A926F305D000F7FC88545BB4D7753BC463178F59C142385EE07023879D00E9738FE05ECE04D7D64FF371662BD0AF1A215F3D8
	994F71F9B92AC46FA3315DED7011BEBCEE654EF0B8607B73BCF1C6ED01B18C8204834C83D8BA117433044512EB201CB109F08FE7D11ED9A6F03F59CE6B35DD601E95BE50A6F055FD246F10C9244F596CFCF89114D781E4CC06B88920E1324841D97D65497CCE836FBF702E844F53F97D61A1B1AC76C56F54973EE7CC56702BA60DEE5EB80F66A1BC74F3223F1F54672F01F251C040631897195F6A354B9084DBBBA7CEC6FDEDE336BD96A0E4B245CCB915E80F8AEC963C4DE72C4DE2F8DFA65A243422BC3DA62F35
	57535BEA3DA6CB3C6DFF31362B61FD5D8354F63E9757A6BFF847905CE4B7C31937FF210F7AAE0276038FA06E037E06E146C2B9DB128A0DC0F391E00F0AF7BE407DDF0298776AF2A62B556EA76B5F197D44C17BC5G9C17457C7DF834C9013B48F069E131A9D9E104DB41F0C5D02E963868F2EAE78923317C31FC3831A4790DA5846842423431C441FB017A4EF9BC47763C98404F97FCFF4F667589009582D7417637B5D0CE1BC2386798AE9B4A798277CBAF35EB1BC2B26F9C98CE66E4BF19ADC8B7C513F96294CD
	7F1C47DCA6DFB860232610CDF49DE4F113355BE2830064CB4D872E9FA41F31985A2E184257D059037C9E114B57F9E8A47912F4E0845A4B8FF27930CE017719BE72E5B01B8D817C0487E94DAF98F2AFB03AF57C0B1B2663B126FBBF0B779F74BF2B98E47838877D4FAA1277D34C30886A4A3ECCE75C71ACA71A8A65980005G4B8156CD0DCD4919CAF162378B0A24917261B60753880124CD977A29394301DB7BE66D19D45A2FE37E6DE1E96E5DEFA484BBB7AE1B4A673CCDFD46376DE1F8BE3EAF8AF1FC21D09781
	90873094E0A5C05AB49ADF527EC2BEBE3CAB2BD594D327055D4052604EDB65FE8D0D89A59F5B318A7D583227F17EC56A33811E6DG36E91ADF67367C329BF34CBE5925D8BBCDB6FE58D5E7F2B5C32A58B9205A58DFD83C007731A153E88D14A9B8A60B43E53D6630ABC3A949BE1BD98CD18BB0FF9A2D017B94DA835274217BCC0224FD5B023CB221CDBE40FBEA39FA3AE6CFDC166FDFC23AD3BE773DF4D6779F25947FB5CE2778AF234CBF7E1B14200F5F6A4A7C63BF24576A15B27F784F177E51B27F780F68393C
	2F9F4533048E070BA5A55CF7249CA35F9182F2CE85D88430FCBABFEB66EDFE14C5ED56CC574E9BB2E770B6DC5F696F63F88B81D7B9C378FB36D7A8D08E19C1F3505AB712EFCF16B4FF948DF473E7F0BF046545707EBD08F927A4EA678C78FD9F0FBCEB5DEEEB37DDB6EBF361F396213F0B15343AF734BA347DC0B47C7EDA50FC73AAFE1F22BB21E596404FB6624B7D4FB6D0C74BE6107D9AAAC0F42962B02D7108FE7D374915EF9F8E787D8E422A996AB9CF387AFD6A58BDD2CDB8FEE37DADBCDED3E51BD7016349
	65929B8C06DC354D56F22D4D2CBE3CFB314B7DB962823A418A4B9ABFC24C2FACAE4CA9A9AF2036EF75797737CB55CF1A247D8B0463FC6F2D10A49A6BAB69644F323FB6C3EAF8710D845A7F7C479A2A0EF1F13816DB45772CC965FD7E7530B616D6776847F2E800693E12E4FB7F08564FF786D046DC34GB6CAEAF334CAEB63AD9F06D7C5398F2F974A7D7B3BB93DF9BE525E27D6EBB238EFC20664CA9CEFE58575733323DABFDFE6F1CDAEDF8D393CFE55B12D7EA16F68751AB8206F6B664699D1233DBFDB2B71D7
	C6451F6F6B0F9E24F33C742FC0DE7195BA8B7849D51CE07FBC556034193A8C9DA63735332A2A8E9FF850F92EA90F3DB76A22AB78474E8A269958F7D250EF98C0B44050712CBC9EFC0C3F2B535E57B7E8E37CED792E60E3ACAEAF02C1969728B152GFEE62F3E5F29FBFFE77F6DBF9B4C3B2DBCF87C71077D414F61D66F8FFC8E77567E4067F06B8E8CFF4F17FEA078371FDB8E683F7D647153AF9B502747413CAF83D8779064B4G058FD16CB46BG2EEF7C8AFDE85CF4425C0D77D5786539C27CC38B030F751E42
	113FD78FAB446F61BA9C30B575E27F041FDE087325B81C8C49F1B38BD1C7AE34E85ED4454FAEC4BB3549DD8C4D4F814E31183E9146557E901D8D7C330535037220403D5EC2678A93856E3795F42E90A9F02F265339425C07E87F5B20605D27FEB96A76D1437993ADEDCD753546CB2D75ADE70D463A5A16CB3546CFEBAF340AB5532D148EC35FF83625691FE7BFE98B54830F4B3E2750F710E9B3C73B0ED79E67EBF74EC9ED5D3DBA6C7DEAA0603E4AF00CFCD58D70774EA4DF756A8E4CD9077F2E334D2A5864BAB5
	FCD34753F78C6544B91EB3136E95DF5F06BC62319C81369AC1E726FF4A227C2788F01688EADF341DFD2F8B6539G0BG78F8DF7ED892FA0FA943B8B2082FAAA9AEBBDCD6D9DAD1F698DD963D97104FFB2BD16B2738AFA7F8BF256873CACB0B8AEEEEDEF61F1204BEF609F34778884A6FFEF28AF1B933A0961BC5EB36626081F81EB77528199CEE548E97DB3500ECF6B91C101A8CF0AC7B42D80DE1FE7A19BEDE4B183EBB11F2827F793EB8CB7D7D4CACAD0661721CB562A6908E7839GCB81D68124BD8C71B6C099
	C0A340C5G85E082C084C08C408200A5GABG24C7E8ACBF2FA898E6AC6A61B7F3C2AE13955D10EA439D0611ECB87311C05F51B8C724737F360767659B8E211D95BD621F2FEFE5786AC77C737519863257C6280B9663FD25A1387F0CEF9859FF7E2101FDE38CFE2FED40295E7D7F2581F3A3C7D717DB96FE7A4D068C01EBF2B7BB5CD6BC0D63F6ECE33FE76902B54AF31DBB195DE5C44626E4C7122F3B5A62FBA6B81679CDA35437C7D20E34331565C8D08E91380D6C6CA79A4AF3A3A9EFFAABFD243CC98F4B01EE
	F524F63E14BEBBB6054E7A55EF97957D9CE6C1FD616C614FDEB9DD59106F3FC6E7B3F5A352795B8C277B02515500ECEDFE72D98248BB88B8EED31D4C57CD1C6DEFEBB6068F1F6DEFEBFF5EC6369685F5690F5219569BD937F626153A7D564E34A219AFF6BE6AAF77938CBF78283F5C955B456FD221EE0510BB4F67DC26792BDCD7E6756905678400082FEABA64B67F6F2770DD44CE5C755137DA27B08E325AE19918EC56EB6C337DD6E8E86F66FE6D1BE7A4514F6553FB25A0BE892222D3607B4FF674481DCA1B29
	DB50F49A032F6B9E63486B5AED8C9C978D980746C5570D016322D023BEAE623871C65C233B410F360BF677895C8501FB00750938CF2DE645A2705318DC08EF1411A161E7B2397A992EDD45C56AF097D45CE3C674CDB0E40B43C534DFB7B27FE33559F879A95646EA87D7A57471ADA62355E95249BDCF60D0EE7E7D5A7CAD9B982D4DEA176B79AF9E059DEF414F485577EC5D7BEC436D4FBB3EFBDBF970584155A6C9941BFC5202F13CCFECF2929CFCFE2E996C5D511DDB188361C9F11908EB261C53E863DBF02F48
	25CFE1F270DB18AAB52F1EFBDEEB77D245F0B9DFD1A9B0B8D220767F72279A5D527451505DDC6E7DC8A73E2B5F8A3038B8662B5C3FF05CB206DB68031B4C624DF840C9D194674F594AFEE38A65A201FB13619A20EC96380F37A14E8B6589D164272CD91ACD39F46FEFEE3B7D761519F57BF9BC3EDD9B53533A776A6D371F77395B4A034F69C7A2E70C90737CD696650CB18277C487650C8B846E4A914A99178A5C67DE4A99D7C6D1EEE47A9077B8F55FE12E1563333D9BB4BC39D6F53DBC463EBFD4B0E342B5AC51
	3D40B34CFEEB9E0BCDB1BEC6F15A8F36EE2036946409764B36D23EB8A85AEF5E36195A1F723895EB1735137F04CD25A95CC6B95984C1737936E42291FDFBD83BF0E893759845FEE6264FB8A8AF9238FD6C9EDFFA9C62124789778B264FC2A8D78ADC985327994A4E47A9EE74F8C73A9726B3F7FC06835D1C473538B16EF1FE174A72D566B35622782A0EA3370E56DF7276FF5D0AFD16887A7AEDAC3E91FD67F6EBFD7FE29B4682396AD1FECE03C9115DF4163D522A357BE356E8E4A891BEFB4B950DF6F68746B339
	F4ED10C377866437ABECDA5B4BC706753F954024027C7605DE0D2E55BB9A3A1B735B03E81E927FBFD9BEEE5B8718685724E4C5DAAFCE695DAEE973D0FE35777D724568CB8D1909D2E21314E2E18B26C9CA6E479565457F36G7D53BF00F61320EDE28145323E522A245FBC1214A82D83C29EE0B5C96BB0CA82FA976FA78982AC568BCB04D485A4392FCA5719A9313E95A9D5BD9177415732D212CA0B01CD4A905ADAC5CA61A74C3EDD7A10225F4B9F4AD634C9D2750867031BC49B3C61FBD034D927875E66465A35
	EB9108A2619440670202FE21174AA8DC52869FD3FCEED6796F967E6A3CBBC9CAE91236E34658AA3BD8GAD1B71C7846A7F7EF0D2DA6FF4C0F60E5FDA00EE5D6C116CD686EC32D14A6CEA41DFBC3B9559DCCB2139DB115EE33FA0BC0427C3079ACDEE0B026170C7C6E91D9D7F1F8AE9B32F36196C5D07F81C7E911B20045A4BCC8B6881A410F78B1397FBB108DE36FCFB535E5D27164FCD1292CFCAEF5B9CA673C9D3A7C470CD52FA4562125D96074D6CF59E2FE19D25755321CB92F4770198BE8E5600276BC268AB
	5E2FCC58903F61E4A24481AE6990EB098932C134B057DBCCAEE0A53B1A947CFFB79C2EBCD1F747CE65439FFF3C6DE22214208E9227E02B57893651E56A355A866005AFFCB6B96F431A0004416E469FDC02E4B9FE12D115E0E0F8607922A90F7D6D1F0C01C62AC83B38AA0174D0376C68BDD45B5585413268G748A5D6F905DE39B03DF1B4D5EC96F1DFAE1ED8758C1031454DBD4447E96333FA5783718E10A99263884143BD29A761FFF3D17464C0CC7ACE31CFE43C918C16525571EFF78656A15ABE0F4A725E476
	DBC618048C2D55A71E5E8E59F5F774149D6F04AD3B87674809266245FEF7FA95E923EFF8AB96903A296EGA5DACE6442B298B6F30889A2B6EB28ADA1BADB4CA816F631263B595F3C1B568FD2FD48E87345300AE1D89BFCF6991D0CDC22747FAF99EB54F2A54838F968AEA5940E4E36A7C0093C9DE61F4759B470B34C029B7AB34C55F7E5853DF31E73247A3B0CA77D4FB0573733C9FDAAB6A57BA9BA173B70A9FE2B91AC67EF6DB559EC0A49F62556EE467C3FEEC01135BB190D6D583F1B5A28153C6E3DF6143BC7
	C773BFD0CB8788EBC92C23B19BGG6CCCGGD0CB818294G94G88G88GB1F954ACEBC92C23B19BGG6CCCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGEB9BGGGG
**end of data**/
}
}
