package com.cannontech.dbeditor.editor.point;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.common.util.CtiUtilities;

public class PointAlarmOptionsEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
	private PointAlarmOptionsEditorTableModel tableModel = null;
	private javax.swing.JPanel ivjConfigurationPanel = null;
	private javax.swing.JScrollPane ivjJScrollPaneAlarmStates = null;
	private javax.swing.JTable ivjJTableAlarmStates = null;
	public static final String[] STATUS_ALARM_STATES =
	{
		"Non-updated",
		"Abnormal",
		"Uncommanded State Change",
		"Command Failure"
	};
	public static final String[] OTHER_ALARM_STATES =
	{
		"Non-updated",
		"Rate Of Change",
		"Limit Set 1",
		"Limit Set 2"
	};
	private javax.swing.JCheckBox ivjJCheckBoxNotifyWhenAck = null;
	private javax.swing.JLabel ivjJLabelEmail = null;
	private javax.swing.JComboBox ivjJComboBoxRecipient = null;
	private javax.swing.JCheckBox ivjJCheckBoxDisableAllAlarms = null;
	private javax.swing.JComboBox ivjJComboBoxGroup = null;
	private javax.swing.JLabel ivjJLabelGroup = null;
	private javax.swing.JButton ivjNewEmailButton = null;
	
	
	
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PointAlarmOptionsEditorPanel() {
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
	if (e.getSource() == getJCheckBoxDisableAllAlarms()) 
		connEtoC1(e);
	if (e.getSource() == getJCheckBoxNotifyWhenAck()) 
		connEtoC4(e);
	if (e.getSource() == getJComboBoxRecipient()) 
		connEtoC3(e);
	if (e.getSource() == getNewEmailButton()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (AlarmInhibitCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> PointAlarmOptionsEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
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
 * connEtoC2:  (NewEmailButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointAlarmOptionsEditorPanel.newEmailButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.newEmailButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JComboBox1.action.actionPerformed(java.awt.event.ActionEvent) --> PointAlarmOptionsEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
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
 * connEtoC4:  (JCheckBoxNotifyWhenAck.action.actionPerformed(java.awt.event.ActionEvent) --> PointAlarmOptionsEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
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
 * Return the ConfigurationPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getConfigurationPanel() {
	if (ivjConfigurationPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjLocalBorder.setTitle("Notification");
			ivjConfigurationPanel = new javax.swing.JPanel();
			ivjConfigurationPanel.setName("ConfigurationPanel");
			ivjConfigurationPanel.setBorder(ivjLocalBorder);
			ivjConfigurationPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJCheckBoxNotifyWhenAck = new java.awt.GridBagConstraints();
			constraintsJCheckBoxNotifyWhenAck.gridx = 1; constraintsJCheckBoxNotifyWhenAck.gridy = 3;
			constraintsJCheckBoxNotifyWhenAck.gridwidth = 2;
			constraintsJCheckBoxNotifyWhenAck.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxNotifyWhenAck.ipadx = 38;
			constraintsJCheckBoxNotifyWhenAck.ipady = -5;
			constraintsJCheckBoxNotifyWhenAck.insets = new java.awt.Insets(3, 30, 3, 15);
			getConfigurationPanel().add(getJCheckBoxNotifyWhenAck(), constraintsJCheckBoxNotifyWhenAck);

			java.awt.GridBagConstraints constraintsJLabelGroup = new java.awt.GridBagConstraints();
			constraintsJLabelGroup.gridx = 1; constraintsJLabelGroup.gridy = 1;
			constraintsJLabelGroup.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelGroup.ipadx = 19;
			constraintsJLabelGroup.ipady = -1;
			constraintsJLabelGroup.insets = new java.awt.Insets(4, 32, 5, 3);
			getConfigurationPanel().add(getJLabelGroup(), constraintsJLabelGroup);

			java.awt.GridBagConstraints constraintsJComboBoxGroup = new java.awt.GridBagConstraints();
			constraintsJComboBoxGroup.gridx = 2; constraintsJComboBoxGroup.gridy = 1;
			constraintsJComboBoxGroup.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxGroup.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxGroup.weightx = 1.0;
			constraintsJComboBoxGroup.ipadx = 57;
			constraintsJComboBoxGroup.insets = new java.awt.Insets(2, 3, 2, 5);
			getConfigurationPanel().add(getJComboBoxGroup(), constraintsJComboBoxGroup);

			java.awt.GridBagConstraints constraintsJLabelEmail = new java.awt.GridBagConstraints();
			constraintsJLabelEmail.gridx = 1; constraintsJLabelEmail.gridy = 2;
			constraintsJLabelEmail.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelEmail.ipadx = 25;
			constraintsJLabelEmail.insets = new java.awt.Insets(3, 32, 6, 3);
			getConfigurationPanel().add(getJLabelEmail(), constraintsJLabelEmail);

			java.awt.GridBagConstraints constraintsJComboBoxRecipient = new java.awt.GridBagConstraints();
			constraintsJComboBoxRecipient.gridx = 2; constraintsJComboBoxRecipient.gridy = 2;
			constraintsJComboBoxRecipient.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxRecipient.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxRecipient.weightx = 1.0;
			constraintsJComboBoxRecipient.ipadx = 59;
			constraintsJComboBoxRecipient.insets = new java.awt.Insets(2, 3, 3, 5);
			getConfigurationPanel().add(getJComboBoxRecipient(), constraintsJComboBoxRecipient);

			java.awt.GridBagConstraints constraintsNewEmailButton = new java.awt.GridBagConstraints();
			constraintsNewEmailButton.gridx = 3; constraintsNewEmailButton.gridy = 2;
			constraintsNewEmailButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getConfigurationPanel().add(getNewEmailButton(), constraintsNewEmailButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConfigurationPanel;
}
/**
 * Return the AlarmInhibitCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxDisableAllAlarms() {
	if (ivjJCheckBoxDisableAllAlarms == null) {
		try {
			ivjJCheckBoxDisableAllAlarms = new javax.swing.JCheckBox();
			ivjJCheckBoxDisableAllAlarms.setName("JCheckBoxDisableAllAlarms");
			ivjJCheckBoxDisableAllAlarms.setText("Disable All Alarms");
			ivjJCheckBoxDisableAllAlarms.setMaximumSize(new java.awt.Dimension(104, 26));
			ivjJCheckBoxDisableAllAlarms.setActionCommand("Alarm Inhibit");
			ivjJCheckBoxDisableAllAlarms.setBorderPainted(false);
			ivjJCheckBoxDisableAllAlarms.setPreferredSize(new java.awt.Dimension(104, 26));
			ivjJCheckBoxDisableAllAlarms.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxDisableAllAlarms.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjJCheckBoxDisableAllAlarms.setMinimumSize(new java.awt.Dimension(104, 26));
			ivjJCheckBoxDisableAllAlarms.setHorizontalAlignment(2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxDisableAllAlarms;
}
/**
 * Return the JCheckBoxNotifyWhenAck property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxNotifyWhenAck() {
	if (ivjJCheckBoxNotifyWhenAck == null) {
		try {
			ivjJCheckBoxNotifyWhenAck = new javax.swing.JCheckBox();
			ivjJCheckBoxNotifyWhenAck.setName("JCheckBoxNotifyWhenAck");
			ivjJCheckBoxNotifyWhenAck.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxNotifyWhenAck.setText("Notify When Acknowledged");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxNotifyWhenAck;
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxGroup() {
	if (ivjJComboBoxGroup == null) {
		try {
			ivjJComboBoxGroup = new javax.swing.JComboBox();
			ivjJComboBoxGroup.setName("JComboBoxGroup");
			ivjJComboBoxGroup.setEnabled(true);
			// user code begin {1}

			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized( cache )
			{
				java.util.List notifGroups = cache.getAllNotificationGroups();

				for( int i = 0; i < notifGroups.size(); i++ )
					ivjJComboBoxGroup.addItem( notifGroups.get(i) );
			}
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxGroup;
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxRecipient() {
	if (ivjJComboBoxRecipient == null) {
		try {
			ivjJComboBoxRecipient = new javax.swing.JComboBox();
			ivjJComboBoxRecipient.setName("JComboBoxRecipient");
			// user code begin {1}

			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized( cache )
			{
				java.util.List recipients = cache.getAllNotificationRecipients();

				for( int i = 0; i < recipients.size(); i++ )
				{
					com.cannontech.database.data.lite.LiteNotificationRecipient rec = (com.cannontech.database.data.lite.LiteNotificationRecipient)recipients.get(i);

					getJComboBoxRecipient().addItem(rec);
				}

			}
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxRecipient;
}
/**
 * Return the JLabelEmail property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelEmail() {
	if (ivjJLabelEmail == null) {
		try {
			ivjJLabelEmail = new javax.swing.JLabel();
			ivjJLabelEmail.setName("JLabelEmail");
			ivjJLabelEmail.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelEmail.setText("Email:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelEmail;
}
/**
 * Return the JLabelType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelGroup() {
	if (ivjJLabelGroup == null) {
		try {
			ivjJLabelGroup = new javax.swing.JLabel();
			ivjJLabelGroup.setName("JLabelGroup");
			ivjJLabelGroup.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelGroup.setText("Group:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelGroup;
}
/**
 * Return the JScrollPaneAlarmStates property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneAlarmStates() {
	if (ivjJScrollPaneAlarmStates == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitlePosition(2);
			ivjLocalBorder1.setTitle("Alarming");
			ivjJScrollPaneAlarmStates = new javax.swing.JScrollPane();
			ivjJScrollPaneAlarmStates.setName("JScrollPaneAlarmStates");
			ivjJScrollPaneAlarmStates.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneAlarmStates.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneAlarmStates.setBorder(ivjLocalBorder1);
			getJScrollPaneAlarmStates().setViewportView(getJTableAlarmStates());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneAlarmStates;
}
/**
 * Return the JTableAlarmStates property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableAlarmStates() {
	if (ivjJTableAlarmStates == null) {
		try {
			ivjJTableAlarmStates = new javax.swing.JTable();
			ivjJTableAlarmStates.setName("JTableAlarmStates");
			getJScrollPaneAlarmStates().setColumnHeaderView(ivjJTableAlarmStates.getTableHeader());
			getJScrollPaneAlarmStates().getViewport().setBackingStoreEnabled(true);
			ivjJTableAlarmStates.setBounds(0, 0, 200, 200);
			// user code begin {1}
			
			ivjJTableAlarmStates.setAutoCreateColumnsFromModel(true);
			ivjJTableAlarmStates.setModel( getTableModel() );
			ivjJTableAlarmStates.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjJTableAlarmStates.setGridColor( java.awt.Color.black );
			//ivjJTableAlarmStates.setDefaultRenderer( Object.class, new ReceiverCellRenderer() );
			ivjJTableAlarmStates.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			ivjJTableAlarmStates.setRowHeight(20);
			
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableAlarmStates;
}
/**
 * Return the NewEmailButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getNewEmailButton() {
	if (ivjNewEmailButton == null) {
		try {
			ivjNewEmailButton = new javax.swing.JButton();
			ivjNewEmailButton.setName("NewEmailButton");
			ivjNewEmailButton.setFont(new java.awt.Font("dialog", 0, 12));
			ivjNewEmailButton.setText("Create new...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNewEmailButton;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 4:58:59 PM)
 * @return com.cannontech.dbeditor.editor.point.PointAlarmOptionsEditorTableModel
 */
private PointAlarmOptionsEditorTableModel getTableModel() 
{
	if( tableModel == null )
		tableModel = new PointAlarmOptionsEditorTableModel();
		
	return tableModel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {
	//Consider commonObject an instance of com.cannontech.database.data.point.PointBase
	com.cannontech.database.data.point.PointBase point = (com.cannontech.database.data.point.PointBase) val;

	Character alarmInhibit;
	if( getJCheckBoxDisableAllAlarms().isSelected() )
		alarmInhibit = new Character('Y');
	else
		alarmInhibit = new Character('N');

	point.getPoint().setAlarmInhibit( alarmInhibit );

	// Set all the values for the PointAlarming structure
	String alarmStates = new String();
	String excludeNotifyState = new String();

	int i = 0;
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )	
	{
		java.util.List liteAlarmStates = cache.getAllAlarmCategories();
		
		for( i = 0; i < getJTableAlarmStates().getRowCount(); i++ )
		{
			int alarmStateID = com.cannontech.database.db.point.PointAlarming.NONE_NOTIFICATIONID;
			
			for( int j = 0; j < liteAlarmStates.size(); j++ )
			{
				if( ((com.cannontech.database.data.lite.LiteAlarmCategory)liteAlarmStates.get(j)).getCategoryName() == getTableModel().getGenerateAt(i) )
				{
					alarmStateID = ((com.cannontech.database.data.lite.LiteAlarmCategory)liteAlarmStates.get(j)).getAlarmStateID();
					break;
				}
			}
				
			char generate = (char)alarmStateID;
			boolean notify = getTableModel().getDisableAt(i);

			alarmStates += generate;
			excludeNotifyState += (notify == true ? "Y" : "N");
		}
	}
	
	// fill in the rest of the alarmStates and excludeNotifyState so we have 32 chars
	alarmStates += com.cannontech.database.db.point.PointAlarming.DEFAULT_ALARM_STATES.substring(i);
	excludeNotifyState += com.cannontech.database.db.point.PointAlarming.DEFAULT_EXCLUDE_NOTIFY.substring(i);
	
	point.getPointAlarming().setAlarmStates(alarmStates);
	point.getPointAlarming().setExcludeNotifyStates(excludeNotifyState);
		
	if( getJCheckBoxNotifyWhenAck().isSelected() )
		point.getPointAlarming().setNotifyOnAcknowledge("Y");
	else
		point.getPointAlarming().setNotifyOnAcknowledge("N");

	// get the selected notificationRecipient from its combo box and insert its id
	com.cannontech.database.data.lite.LiteNotificationRecipient rec = (com.cannontech.database.data.lite.LiteNotificationRecipient)getJComboBoxRecipient().getSelectedItem();
	point.getPointAlarming().setRecipientID( new Integer(rec.getRecipientID()) );

	// get the selected notificationGroup from its combo box and insert its id
	com.cannontech.database.data.lite.LiteNotificationGroup grp = (com.cannontech.database.data.lite.LiteNotificationGroup)getJComboBoxGroup().getSelectedItem();
	point.getPointAlarming().setNotificationGroupID( new Integer(grp.getNotificationGroupID()) );
	
	return point;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	//getAlarmClassSpinner().addValueListener(this);
	// user code end
	getJCheckBoxDisableAllAlarms().addActionListener(this);
	getJCheckBoxNotifyWhenAck().addActionListener(this);
	getJComboBoxRecipient().addActionListener(this);
	getNewEmailButton().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PointAlarmOptionsEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(384, 363);

		java.awt.GridBagConstraints constraintsConfigurationPanel = new java.awt.GridBagConstraints();
		constraintsConfigurationPanel.gridx = 1; constraintsConfigurationPanel.gridy = 1;
		constraintsConfigurationPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsConfigurationPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsConfigurationPanel.weightx = 1.0;
		constraintsConfigurationPanel.weighty = 1.0;
		constraintsConfigurationPanel.ipadx = -10;
		constraintsConfigurationPanel.ipady = 1;
		constraintsConfigurationPanel.insets = new java.awt.Insets(9, 9, 4, 10);
		add(getConfigurationPanel(), constraintsConfigurationPanel);

		java.awt.GridBagConstraints constraintsJScrollPaneAlarmStates = new java.awt.GridBagConstraints();
		constraintsJScrollPaneAlarmStates.gridx = 1; constraintsJScrollPaneAlarmStates.gridy = 2;
		constraintsJScrollPaneAlarmStates.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneAlarmStates.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollPaneAlarmStates.weightx = 1.0;
		constraintsJScrollPaneAlarmStates.weighty = 1.0;
		constraintsJScrollPaneAlarmStates.ipadx = 336;
		constraintsJScrollPaneAlarmStates.ipady = 143;
		constraintsJScrollPaneAlarmStates.insets = new java.awt.Insets(4, 9, 2, 10);
		add(getJScrollPaneAlarmStates(), constraintsJScrollPaneAlarmStates);

		java.awt.GridBagConstraints constraintsJCheckBoxDisableAllAlarms = new java.awt.GridBagConstraints();
		constraintsJCheckBoxDisableAllAlarms.gridx = 1; constraintsJCheckBoxDisableAllAlarms.gridy = 3;
		constraintsJCheckBoxDisableAllAlarms.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxDisableAllAlarms.ipadx = 62;
		constraintsJCheckBoxDisableAllAlarms.insets = new java.awt.Insets(3, 9, 13, 209);
		add(getJCheckBoxDisableAllAlarms(), constraintsJCheckBoxDisableAllAlarms);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	initJTableCellComponents();
	
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/00 10:52:29 AM)
 */
private void initJTableCellComponents() 
{
	// Do any column specific initialization here
	javax.swing.table.TableColumn nameColumn = getJTableAlarmStates().getColumnModel().getColumn(PointAlarmOptionsEditorTableModel.CONDITION_COLUMN);
	javax.swing.table.TableColumn generateColumn = getJTableAlarmStates().getColumnModel().getColumn(PointAlarmOptionsEditorTableModel.CATEGORY_COLUMN);
	javax.swing.table.TableColumn notifyColumn = getJTableAlarmStates().getColumnModel().getColumn(PointAlarmOptionsEditorTableModel.EXNOTIFY_COLUMN);
	nameColumn.setPreferredWidth(120);
	generateColumn.setPreferredWidth(120);
	notifyColumn.setPreferredWidth(50);
	
	//Create new TableHeaderRenderers		
// DOES NOT WORK IN IBM's JRE1.3 DECAUSE THE getHeaderRenderer() IS NULL
/*	nameColumn.setHeaderRenderer( new javax.swing.table.DefaultTableCellRenderer.UIResource() );
	generateColumn.setHeaderRenderer( new javax.swing.table.DefaultTableCellRenderer.UIResource() );
	notifyColumn.setHeaderRenderer( new javax.swing.table.DefaultTableCellRenderer.UIResource() );

	//Assign the tableHeaderRenderers som toolTips
	((javax.swing.JComponent)nameColumn.getHeaderRenderer()).setToolTipText("Alarm Name");
	((javax.swing.JComponent)generateColumn.getHeaderRenderer()).setToolTipText("What group the alarm belongs to");
	((javax.swing.JComponent)notifyColumn.getHeaderRenderer()).setToolTipText("Click to enable/disable notification");
*/

	// Create and add the column renderers	
	com.cannontech.common.gui.util.CheckBoxTableRenderer bxRender = new com.cannontech.common.gui.util.CheckBoxTableRenderer();
	bxRender.setHorizontalAlignment(javax.swing.JCheckBox.CENTER);
	com.cannontech.common.gui.util.ComboBoxTableRenderer comboBxRender = new com.cannontech.common.gui.util.ComboBoxTableRenderer();

	// Get the alarm data from the cache	
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )	
	{
		java.util.List allAlarmStates = cache.getAllAlarmCategories();
	
		for( int i = 0; i < allAlarmStates.size(); i++ )
			comboBxRender.addItem( ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get(i)).getCategoryName() );

		generateColumn.setCellRenderer(comboBxRender);
		notifyColumn.setCellRenderer(bxRender);


		// Create and add the column CellEditors
		javax.swing.JCheckBox chkBox = new javax.swing.JCheckBox();			
		chkBox.setHorizontalAlignment(javax.swing.JCheckBox.CENTER);
		chkBox.setBackground(getJTableAlarmStates().getBackground());
		javax.swing.JComboBox combo = new javax.swing.JComboBox();
		combo.setBackground(getJTableAlarmStates().getBackground());
		combo.addActionListener( new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent e) 
			{
				fireInputUpdate();
			}
		});

		for( int i = 0; i < allAlarmStates.size(); i++ )
			combo.addItem( ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get(i)).getCategoryName() );

		generateColumn.setCellEditor( new javax.swing.DefaultCellEditor(combo) );
		notifyColumn.setCellEditor( new javax.swing.DefaultCellEditor(chkBox) );
	}
		
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 9:11:36 AM)
 * @return boolean
 */
public boolean isInputValid() 
{
	return true;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame;
		try {
			Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
			frame = (java.awt.Frame)aFrameClass.newInstance();
		} catch (java.lang.Throwable ivjExc) {
			frame = new java.awt.Frame();
		}
		PointAlarmOptionsEditorPanel aPointAlarmOptionsEditorPanel;
		aPointAlarmOptionsEditorPanel = new PointAlarmOptionsEditorPanel();
		frame.add("Center", aPointAlarmOptionsEditorPanel);
		frame.setSize(aPointAlarmOptionsEditorPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Comment
 */
public void newEmailButton_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{

	com.cannontech.dbeditor.wizard.notification.recipients.NameEmailPanel nameEmailPanel =
		new com.cannontech.dbeditor.wizard.notification.recipients.NameEmailPanel();

	int userResponse =
		javax.swing.JOptionPane.showInternalOptionDialog(
			com.cannontech.common.util.CtiUtilities.getDesktopPane(this),
			nameEmailPanel,
			"Create New Email Recipient",
			javax.swing.JOptionPane.OK_CANCEL_OPTION,
			javax.swing.JOptionPane.PLAIN_MESSAGE,
			null,
			null,
			null);

	if (userResponse == javax.swing.JOptionPane.OK_OPTION)
	{
		com.cannontech.database.data.notification.NotificationRecipient notifRecipient =
			(com.cannontech.database.data.notification.NotificationRecipient) nameEmailPanel.getValue(null);
		try
		{
			com.cannontech.database.Transaction t =
				com.cannontech.database.Transaction.createTransaction(com.cannontech.database.Transaction.INSERT, notifRecipient);
			t.execute();

			com.cannontech.message.dispatch.message.DBChangeMsg[] dbChange =
				com.cannontech.database.cache.DefaultDatabaseCache.getInstance().createDBChangeMessages(
							notifRecipient, com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_ADD );

			for (int i = 0; i < dbChange.length; i++)
			{
				//handle the DBChangeMsg locally
				com.cannontech.database.cache.DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);

				//connToVanGogh.write(dbChange[i]);
			}

			refillRecipientComboBox();

			for (int j = 0; j < getJComboBoxRecipient().getModel().getSize(); j++)
			{
				if (notifRecipient.getNotificationRecipient().getRecipientID().intValue()
					== ((com.cannontech.database.data.lite.LiteNotificationRecipient) getJComboBoxRecipient().getItemAt(j)).getRecipientID())
				{

					getJComboBoxRecipient().setSelectedIndex(j);
				}
			}
		}
		catch (com.cannontech.database.TransactionException e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );

		}
	}

}
/**
 * Insert the method's description here.
 * Creation date: (8/9/2001 2:54:35 PM)
 */
public void refillRecipientComboBox()
{
	getJComboBoxRecipient().removeAllItems();

	com.cannontech.database.cache.DefaultDatabaseCache cache =
		com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized (cache)
{
		
	
	java.util.List recipients = cache.getAllNotificationRecipients();
	java.util.Collections.sort( recipients, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );

		for (int i = 0; i < recipients.size(); i++)
		{
			com.cannontech.database.data.lite.LiteNotificationRecipient rec =
				(com.cannontech.database.data.lite.LiteNotificationRecipient) recipients.get(i);

			getJComboBoxRecipient().addItem(rec);
		}

	}
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) 
{
	
	
	
	//Consider defaultObject an instance of com.cannontech.database.data.point.PointBase
	com.cannontech.database.data.point.PointBase point = (com.cannontech.database.data.point.PointBase) val;
   int ptType = com.cannontech.database.data.point.PointTypes.getType( point.getPoint().getPointType() );
	
	Character alarmInhibit = point.getPoint().getAlarmInhibit();

	if( alarmInhibit != null )
		CtiUtilities.setCheckBoxState( getJCheckBoxDisableAllAlarms(), alarmInhibit );
		
   //be sure we have a 32 character string
	String alarmStates =
      ( point.getPointAlarming().getAlarmStates().length() != point.getPointAlarming().ALARM_STATE_COUNT
        ? point.getPointAlarming().DEFAULT_ALARM_STATES
        : point.getPointAlarming().getAlarmStates() );
        
	String excludeNotifyStates = point.getPointAlarming().getExcludeNotifyStates();

	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )	
	{
		java.util.List allAlarmStates = cache.getAllAlarmCategories();
		java.util.List allStateGroups = cache.getAllStateGroups();
		String generate = new String();

		if( allAlarmStates.size() <= 0 )
			throw new ArrayIndexOutOfBoundsException("No AlarmStates exist, unable to create alarms, occured in " + this.getClass() );
	   
      
		if( ptType == com.cannontech.database.data.point.PointTypes.STATUS_POINT )
		{
			String[] stateNames = null;

			// get all the states the status point may have
			for( int i = 0; i < allStateGroups.size(); i++ )
			{			
            com.cannontech.database.data.lite.LiteStateGroup stateGroup = 
                  (com.cannontech.database.data.lite.LiteStateGroup)allStateGroups.get(i);

				if( point.getPoint().getStateGroupID().intValue() == stateGroup.getStateGroupID() )
				{
					stateNames = new String[stateGroup.getStatesList().size()];

					for( int j = 0; j < stateGroup.getStatesList().size(); j++ )
						stateNames[j] = stateGroup.getStatesList().get(j).toString();
						
					break; // we have all the states, get out
				}
			}
		
			// insert all the predefined states into the JTable
			int i = 0;
			for( i = 0; i < STATUS_ALARM_STATES.length; i++ )
			{
				if( ((int)(alarmStates.charAt(i))-1) < allAlarmStates.size() )
					generate = ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get( (int)(alarmStates.charAt(i))-1 )).getCategoryName();
				else
					generate = ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get(0)).getCategoryName();
						
				boolean notify = ( Character.toUpperCase(excludeNotifyStates.charAt(i)) == 'Y' ? true : false );
				
				getTableModel().addRowValue( STATUS_ALARM_STATES[i], generate, notify );
			}
			
			for( int j = 0; j < stateNames.length; j++, i++ )
			{
				if( i >= alarmStates.length() )
					throw new ArrayIndexOutOfBoundsException("Trying to get alarmStates["+i+"] while alarmStates.length()==" + alarmStates.length() + ", to many states for Status point " + point.getPoint().getPointName() + " defined.");
						
				if( ((int)(alarmStates.charAt(i))-1) < allAlarmStates.size() )
					generate = ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get( (int)(alarmStates.charAt(i))-1 )).getCategoryName();
				else
					generate = ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get(0)).getCategoryName();
						
				boolean notify = ( Character.toUpperCase(excludeNotifyStates.charAt(i)) == 'Y' ? true : false );
				
				getTableModel().addRowValue( stateNames[j], generate, notify );
			}		
			
		}
		else
		{  
			// All other point types are processed here
			// insert all the predefined states into the JTable
			for( int i = 0; i < OTHER_ALARM_STATES.length; i++ )
			{
				if( ((int)(alarmStates.charAt(i))-1) < allAlarmStates.size() )
					generate = ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get( (int)(alarmStates.charAt(i))-1 )).getCategoryName();
				else
					generate = ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get(0)).getCategoryName();
						
				boolean notify = ( Character.toUpperCase(excludeNotifyStates.charAt(i)) == 'Y' ? true : false );
				
				getTableModel().addRowValue( OTHER_ALARM_STATES[i], generate, notify );
			}		
		}


		// assign the correct recipient to the getJComboBoxRecipient() component
		java.util.List recipients = cache.getAllNotificationRecipients();
		for( int i = 0; i < recipients.size(); i++ )
		{
			com.cannontech.database.data.lite.LiteNotificationRecipient rec = (com.cannontech.database.data.lite.LiteNotificationRecipient)recipients.get(i);

			if( rec.getRecipientID() == point.getPointAlarming().getRecipientID().intValue() )
			{
				getJComboBoxRecipient().setSelectedItem( rec );
				break;
			}
		}
	
		// assign the correct notificationGroup to the getJComboBoxGroup() component
		java.util.List notifGroups = cache.getAllNotificationGroups();
		for( int i = 0; i < notifGroups.size(); i++ )
		{
			com.cannontech.database.data.lite.LiteNotificationGroup grp = (com.cannontech.database.data.lite.LiteNotificationGroup)notifGroups.get(i);

			if( grp.getNotificationGroupID() == point.getPointAlarming().getNotificationGroupID().intValue() )
			{
				getJComboBoxGroup().setSelectedItem( grp );
				break;
			}
		}
		
	}

		
	Character excludeNotify = new Character(point.getPointAlarming().getNotifyOnAcknowledge().charAt(0));
	if( alarmInhibit != null )
		CtiUtilities.setCheckBoxState( getJCheckBoxNotifyWhenAck(), excludeNotify );

	getTableModel().fireTableDataChanged();
	
	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G6CF854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DD8D45715C6C9C292E3B1B1ADD1CC0892EB37FC894D3A293B716BDAEAF6F56336E6EBB6E957ED59AECDE957346C566D1A162D17615788718F8594F5D4CC302588A88D0C207C8BC810882B58E4A38F66818F9E73A6B3EFE0C023FB4E3D6F3E7798E6C0ED7C36FE5F613EFB6E4F7939671EFB6E4FC8328E0527A4DA3288C9CCA15A3F779289E9BDCF48330FBCE0673899E2F4AAB17DBB8C3013D4DEC8
	06B4844AB6BD9B1D3A03BCDF1E8E79884887FDB1BAB59B3EF713974A575740C7GBA11D05630EAF55854F43E370753C92379583A8CC85F86D88AB8CCB709C8BF21AEC7432F55709C92AD044474E11D6C7ABC8DD78E79F600B00028BED6FFB524DBA965FBED65DA3D0F660613CDFF772EA5937BB1754921201F35ED55D2ABF9B6F1D3BF522A2E4F505A84C1FE85G8A1FC4FEA91E013432FD287FCC2FB2DC54ABB89C0AC395FB870AC43B24AA2EA227A2B9D41B2D9D139AD9F08DDFF02A1262F0B75062F641A14A04
	B88710C6F0FDBA6D936401FC8245DDABC73A4B60FBADC02C0E2F2DC0FC35941D5A856014382CD56FD6EA32F4DDFB16A43C6AC85A9EC056E0094998A599324E737CB2AD170E4B71EC547FC6A85BB1981D1A8F5084B0G908C301F5A43313D3F013426C72DD3060795C70D53D9AB5BDD6FABF6D106EF1B8DCAB45CC531D7F1018532B1EF2CAF74434FD4405AF55EF6BB261325A77978C7DEDCC2B67C79F9CB1A9F1DECD8DAEC899FE43AD8BEE8680451FEB8A06D5786A75A67C45A17091976467B142494BF34173DF2
	5C32D16B078C99340B07F02CFFC6ED408A5F091AFDB07C1B942F1A704CD66F21F84C87C1D95C90370D2E37B9AF2D4973490E12D63FFA880D2E31344A0C873B6C4B4B73546EC2E4C6F32B4E4BB7A8BE5204E73CB4D1BC66D7C3D961F0F4AA332F52DC1C3BA1100F84D8853081A081A04781759C5C4646761E38C59BEB97DCEA2DC7126D220B30362559FBA1953D2ACBE8F638D54151ABDE92DC1250A30BEE2255B91D0DBA6819502E1A5AFEAF50B8A7BAC417007395FA9706C5D5F439D91D666C1DD847A55252CEE9
	D8848243CE427B7DA0FBB7243260D67F47E997D45128414A2FE423CEC604CB50A3F4G5FCCAFD777213EDAC1FEBB002A6BE1BD6D6F3C6882FFE13335AA4A104759091210D58E54F384759D41709D6E60BA7EF0BF62521568D42B427B5916334B54CF3D540B6C8B2EB1BE477E339C7D73A9055B4CCF3B384D54E7BECD8ADFE971EBB35437AA4CD6D445F76EEC21FDA2DFACFFFFF957ED4E63296014B2F17EB19A611FA90DA41FBC536F1ABC9C2BB0203F9C60B5DD470DB9D6E6333502DB64D6FA418586A65A8D0DB3
	BED7D46C50741926594E89DD06B534AC9E7A4DF1C627D6838CB8398E5BAA3A34F6A7CCF25F0AAC7BEF218E2351CFE9206CDBB5FC4AAD523895DE182EAAF3F1FD92C7C407DAAFBAC5079DBE5CDAD9DDAE525566EF1F2237E926F8A74C77BA18E0EE9DFFA1976724202AAE2947230A9D222ACA0EFE237CB225657674B88DD741E9FD1C0B32A923B05F757A3D39B8D7FA9507C3240326B7514AC5BAD7C5D99C86266B940FC3E57821FDE86FC3621866AB14FD98880D8832877D8E0B9B2204EDA803CB9A86D3C057C5B6
	B9F19E3FD01FE352D18860B07ED98D6986A71F1323C7791C4C9FFF0A2C3CFA4C62EFCD89FD3A5ED279891B9B6D1F98F31271543D5FBF6FFD54E7E8F8B763B5639A7A6A29A298B69FAC8731CFA7508A82887B044F079F6C47F26EF33409D02748B2D32B2E6FEF65213D3B45CFBCA238EAF4EE67F59D7EE35EAE1F32CB1ABED9797772D01F3249AF363AD01F7FDD41E344E5404FDA000D1FB01D963A34D80F567B3A0D578B05FC9440BA173FFA771E6675F63823D38BG1A5C7E6AB5D772FA41D09E8E30D22BC7D4D6
	0F69B66DAA46957E6247FE0FD464D1A57452028A934069D1292ED92CF82CA1DD6B23D565B16187CD287FA0480781ACD3393D3C1C49E342F9C361A4E6713E00716FDA0D37F85530976BAC9E93DEBE04B40BBD600BBCFCEC2F9CB02FCBB5EE3752AB51A5D19B177403E6FBE2384C03EFC15AA7395CEAB3CCF8AF9F435D8711DFDCB3B53496C33CE34B50E46D843A3381488862171F4DB2713E4A43563E0D10A6517E9E361522BF20C6D64412F65155273806C5FB4120B0A2948923EA9175BDC5B53438813F0F7331D9
	F5FC9763D3245AE78BF94171CBBC36DB343B1A6B6D1979247266EE0B3FD8BBF87B81CB53885327F344506B7567D213B6FE7C2BB4A663CFF499D7C01DCDA3DC46199EDF998BC7E12FB34AE57C60741D4A4868BEF31A5335C31FE1231C6E4B23E63A6BC7995D841D6E1E4ABF0E2E2B12535D6105B96465F46DE5E63AB33C0C6ED2AF277B1DBB264B46B4F1DF95248EF134E1D81064DA0F2AAA0E2BB5BE7D34866E68B48E3F36BE7E2641F7CF3042EB44A26B3C137785ACD6FE114E53D8AF0B1553F47C4B941FE042B3
	DDDD26F84CE70DC527FA47F8FC773A95570A8D105F8C30E39C7C91C0934040B81F1FCB8E1EA453444F4D60EFDC8EC16E1830B0EAF37223C328F7D84B34FC77A154A3DD4BF4BD945AFC7590B4BED17670F1FFB11AEDD23BA21F7CAA1FF6EC8C4FDC5A4FFDA68E143748BD8A6B3C4D56D2B7A0768E55AADEEDBF7D333368FB76200E4639AF7C34516C8BD7ECAF35EC8B608B2B2FB15A5D570CB9DB286F0FC30E20FF8F073295G6B816281322EC7279683F4DE677A7F49212A6974CF6D2AD32983DC932CDFF53E7106
	2E2F78BB54732F275533FAFDE2BB960B3C55EC0EC5E6DFE77EB58A52D5GCC47FF69C95768EC384EE353E5276F0C4F68C96DFC794C7A541FBD744B016CC1996ED18CFBC87772731542CFB92F5E4ABB63753EC96D8E7A646DFEF95D5E116B1FD7660A88BF8BDAD04675FA9D7AE1FB3C9A3A66AF037CEA9DF7709C62B6C2BE79861769A56AAF8AA05F84A06B78D7A99E7D693C9BAC96E2787FED1AEE2F047DE0AA5F70FF56E106029B7E74B5B2AD0D94D37D69E8786E4758DC7B19B570DE6297D67FFB09842B7F3D04
	6570CE0607BE4CA78B3C7C2D43D36FB5F69D763757B8C0F9DF33F7BB6D5B6D965DF472E30027157DC8C5FFB5E94F51A68C0B1A5C30AF31ED556AB428CADD092E63CDE77D69F839B69EEB35B446B4BE1B60BB7DE6F4EA5E4D09E3D68C79FA00AEGD9ABE3FEAFBB9F576799008B83D8863096E063CD6E6F169E1EF63DE970CE315E4CBF92F88C1FBA62FF8C3FF88463D93762F1750A3E63744C119C3D4C702C4D2AB80CAAE5F53ECA5B2B022BDF1CB44EDF236DD9D92076FFFBA410AD3071FA7734F93CCE693E6067
	47F37C7B82D31CDF5B4C7DC0468C9E677F350A73389E729F8128B378BC7E1A476C13E64FE063962E254BE6CC1E3B0C3FFF1B405FC95D1EB23DD31D372E8E585F3315667ECA757E424B4C7DED505A47EBE972BD017A6BEA407D33AC0CA99E957C73BD288325012FCDB910AF866882106FE1EB94536FD7C6F1ED6FF3894320568E2758CB3AE7E25B32F37A5D82540F81A0003782D44F64F1664213987B430877C97D9EFE84C84F6AD9F975A5BCDBEC6968F541BE96CB684E38C3059D021B57D9FB9E670C9E1554CBEE
	5C185648B22D0B7641565968B273BA3BF4A613E1BD24F1B3391F5FDB0DF6C0E64177AC0E3BC6F1F910BF2563EA33915784F93B0E7BAE45F9E731381261B6D35CBC40C56975E651BB1FD510DF2F6396A4A2AE8E7289BA6ECBDD74DCB188F49644F1396308BB9544F44FF06563FCED6A8C627E61D0FE60797DCE3E7F79DD186F5FC71766CF6D23ED790166A53347F34DE6FB0CG9E178799B638B6A8D07C7E1E8767DFCC9053E7260EEF247864FB8DBC0B5FDF2473957385D0B66FDE2E33D7E86CC46E036F7BB8AE4B
	0138FC481F52F1E38E2E47EA9D176F6538909D77D59AB344C0BEDE473D120338647BA1B63D1F63EC393C2D95F00C47AFB4707BADA760165E4F67D0C9F71E59C7612648E6BB67126C35C2FFAB1D114CCFFD1F46A658BE66FE6E27B29B314F1CE0181FGCD41BC86773E43E37048A573C96B0C73FE4FA3E2BACBAD5D41EC8C02020D98FCA944600C660767B84D15D03EB998F18F1CB36F9FF19E3EEAE1F1A4ABFFE1C2B95A87EC8DCD65EFCCA8C7FE93134C65071A4C65A92676CC36170EF15992DEDACA643F2888F8
	56D27D00F64722256D170E127C87FF152645348D87085F354170B804470AAB693CC57AE18FF05D2F217B6B75105F88107CA077EB3F2B4633C995A6D95E03F8BF7B455F92427D55A35568AF07852FB46C996E1046C5927AA07675E7544FF443372C77F3A59B65E29BF0DC6A8587DEB0337AAE6ADBB640F71CDE7F5FE97D9E858F7F5A8589361CF61D6E769CACF33A44BE5165926D14B26F2B00596FC3707D906FEBBE3DCF68D39CAA2B93C5652F0472A63D4EF30929D496D73F84C1B513F54F6F8CD9D7F6D1D9A507
	0F2C2FD15F6605FE02743E0A47710CEBC0F1C963C0D3G77AD75BB70BC0B2CC33E480571FABF63EB3641B4ADC50802B688C7B53BD8855F1F72EFBABFBF26EBFE4E6C68544259FC4EFEE190F16D10376B383DB21F474133F98CD065413564B548A7GE4BD8CF38F2089E060E1DE27155641B34EA040C5G2CFA181DE562B9253607D03B399D4FE6FDCC5A937841A15D296276AC2D4D0016B2DA2F6B34D208F157E006ED8174BA95AD3E0F48B4555FCDB6FAA65A0EE584F5988B7C44BF4C477B370E09F3249C2737B6
	56A10EDB996B14GE3ED1D931D7ABE247F35E54B96565FE96FD47D25187A227B56B9C6FF082F067C2856B7637D1BE593650B25EBC2AB546B1A43655B18B3D13E019306FC9F64F2FE0267CC9E4BB49FFE42208E429E0D0728B9EC6D6367D7BB4942C9E7E45381EB7765BBEC97FB5B6D585A7DFD3AD62DB0714F70F1943F5A04E76B656EC63EDE2E0732724F71B3397345B83742A03F9CE09DC09CC0FAC8F4EAFE880F0156659713E976C81D0A339502A4599456B0793A4F5D599B082F2771F5E959DFADA24B6FFBB3
	B15039D7F9889B77AEAD0D6BBCCA71CC3E9F1720FC41D09681309AA086E093C046DCAEDF4E31C2A69F5E9055282A50BBC0CF7F39F017A4F154C0E3E04EE4FB311C4BE6053ECE81B44DB5EC7023390162312AAAFA6FB45777FD44B90A1FB15777FD4437AA797B0890A85BB41747A55F7A507CCEE8477C4396BE663EF43B9E51566EC70C38044D2543383D536662FB590788BDEB366A67497592080C977E85A545278A2D253FBF2E0DCF782819F679DB6F793D53C38855E8C6EA690A7B0FEA6B5BC3E5866DEFE7E31C
	FD046EB42C4D0E8129C7D20FE8753E71FB231E377CD6F8E46D4AAA0DF61FCBBC625B2E911CCE41913BA448CA7F11C2EBF121356454F13E76AE2CB25A3EBDFEABB48F513A304FA046BF5657CAFCF225696DDFB238DD67FDF98129FF754DC46EEBFD75962B696B54234618458DA624B11EBC4D98AF3078046FB1E76B671FB2348982D881306ED1F6A7415ABDB3B8F9EF7A62A0EF9777A85B1B12F930964FE36D181F7D1DBC5DBAE206C2E8DBBD0F79FD4CF742779B300670725DA4126DC779DE33CDD1253E314B8322
	2326F70857B9C8633B16D621C7144F39940FD3435F68F81B3655CECBD911D6A67698ED681509060F7BF817394DC531D7F2CA9892E9654F5D44BE5BA65C33G025E717C20429CC3F7EBFA891A476F006615793BFB8A554B23C77D350F04145947629A4356529D37E727FFD30BEDA75BDE7CD9D63E294E304376C6034E1A1CDB21D3FA1C476CB3F3797D6DCA60BB302DDBDB701B79479FB69A5FBFB5FD67B6FE16EFDD7C2F2B25F71586DBB7BA0B831F8DFCD86C7FECC0A876FFB650DFBC75792CD29C78AD48C83179
	AD88DB7762DB5066B663BCFE8C56FB00D6006E47781A17DC02630F2F51A62FF78D76FEBCFB4497E8AA277F9FA501E5FD33E47AF7EB16D23CC36FD1E0DE8DE37F9C1FDE0A6325AACE0A546E5BCBD1C7AE29FFC0E5D579FDFBA95A1BAC76D1B45BD3EC20FB296BA0D778E3FC1FD1DDC16F9DA03FC1474D2738F848A7FF1E631EC9617B11024F73FDC4DD95461ADEBD7FBE4D076A79D7AABF8B3B427E835B6ECFBF939A01769B4CBEF2DA092E57452547A03DA48CEA119DFBA8432D21857C43051EC131D76D947A7576
	1677D1F7ABC00E57BE4FED2A6F54EF83758348F6176294DD6A98435257B0DADFE75AF0ED598C7DE4FD81EC95209D408EF09DA084A082E0B940BA00B80084009D219023821C82E88278A8147311F2767894FCE807C15440DD3833A7BC4E9AEC61F116937ADA9A4A4F14FEF4G7B1B7A4DCB27244AE22D369CEBBE7339966EB3E3C279B9C72CC44F189E07F5962069F1FE1E7546A00F4F3B81977AB83F2FAC9B62E7C66B9E67FD748EE15D74795129D60072797E7CF17C5940EB423EF6C274B37D3EGE77AF485E5BC
	645A7819FE7BFC4E57CBE5DC36E09D7766A82F37F23E7EF6B2579C7FEF86FC460229775CEC8CB7378476B7DBDA2677B7696769BB9E70931DE3CE6D5EA373BC46BECADF1FDB547D5A2E73BBF4DC333BDDF1CBF8C24456481F17636F88C640E825DEC1E687D935022BDD11255EB16D7C65ED6AD77020463A0047D817AB502E0D73975F167C2C48CD5B62625F2D377DCA456438EEF5850F4F42973038EEB924EB9730FBD1961F3D18B2DDFCE65E8747C25B4D8B0C78ACA3ACBA75A98B449109BCBEE8BA076BD1CBA73B
	81B0DD95E8F7GB72A4CF7GB9E10C17CAC85B4378BCCCFB9F4B74B50DCDF9CD6FEF3F0F6B1364GF58B32B4CEAFACBA2022231F15DFEC8BBC7ED75A269F7F1EB67F6B4DC05B6475E6384D7FFA63EEB32FB78C67EDC3BD76038F69526BFD2A632E68381934CF44DD166C6A000E8F26FCA13EC9C4029CBF0772B5CAF1DD9A6ED1936E0A068BEDC37BG11413CF85B05ED748CC512E56D5E1156119CE0523ABE16D09EA527E0627B4BBA6E8A61EB0F33E53A7D7AC0183F7B729B533673FDEF405AFD15368B8A1B5AA7
	F08805FAEC2F5CC57DE5E4186F7907C0714B43FC4FBFFEA2F17F3A8A4A8A96F27F747341BBBB37C99C3A33F6365BEE4764FA157A7C598BFD650D217850053E72CAC3DC5E88A8CBFF024BFBF0425B9A7989267799DABA6F897FE7E8BB493A322978DE8A6DD6BC614FAEFEBDEAEE37CE2B932B251B7D50E3315993F43F117124798EF0B14565BFE91C0F6CA647AA70BD01E969AAB25DB894DDEA385859DCD753FA35236E620556565A1A0BD7EBDAAF57DC69505BBFD8F1F20A76CD97AEB67F61C2DB273F9E181E63AB
	F84CD08DFCF91F64EBE1393EE6C6BD49576DDFA5E35DB848137068541C705BDD371BBE409C73630357822C5B54E3B39E1E2F626F070A43B98FA9E7110781484FG8885E06FB8D66E46F3E749A1CB8E310E7D12C4577BB7E99C9385F5D7077358D916681D8964634339EDFDBCC874F16FB7FD47CBF7FF3F37F5706E537867A1C32657CD5F4A505D279DFCD7E930B1AD33717B5D84FD1C33F2787D6E0E27B84E134377CDF9BA6EC7E59CA76B38FE9A8F2E007CBA9D37474BE9446A38684B1C46E69DF734CBFFA73608
	476142BB99C40F9968324A70F95ECC834F17D5CDEF3F28383375ACF9529BC4DF703F1ED551F61D407340226957346B0B78FA2624505F29AD72756FDE0A8FDB646B5F93D338FFDF8AE5990B799CFC21EAAA3E1D0B831DC90FD13F903458177ECC0A8FD96CCBFF5BD9CEBF8C4AC8840FDFFF5802F10F0A3BA1BCCFA2BC567DEF6AC36CD04F29571DA525703A041F9F8DDDA4FA1B0F29BDC6C07D2888FED6B687DD37F677B3BB67F64EA4FD6F36E2204FF7A15D225FED7D3AEC3A3BADF3FF9BA3A65EED112723D35D44
	FC37F5E39447C4F420C736EBB2BDB64AFDF28654E738B3F4CE64C13E60E9AE67D7BA8DB9EDDD37935B7B4A5985FD1EA4667B5ACE2AD7BB602FBF4D67DBF9B262C2A09F2963E6D15CCA482FFF1A5B17070A2A45FC266F34143B6F832DF7158613799F2A385F0951E53EF95610B358743D536F7B454FD6665F5CD59A5ABD4CE5C2384DDE15F0CE84BC97AE6AD1E47B11C2EB89BB04E66DBB3A0D766937F42EEC1CDF5FA3E05B40B1CFC40F51F764A83D87F6F9C4D6C6C4236C794B37C777E81F51F648665FE74D2273
	A4F989FCAF61BE6BC2F2A62F4B8F8B747A83497CCC24DA2FBFBFF9320FEB44E3B46DED5F0F75FDF4BB3431838481C4G2CDA42FD41A2496035B07930FFB9EBE083AC9BA3436B5FCC46F90FEF9AD9CC387D8CBFCF0ADD425FA0D51D71FD03B4B7517C06C8B9637B86895B9B65CF1D71FD03B4317D374F783EC1626D190DE71D395DBD917B7F9DFEF0DEGE1A25FA0A9AA59253DD634564832153DD7F43B48F6DDC94DF4B79CB3F25216742F0B13ACA4391924325301E6120CDB7CE612C2CFG1AC9122AB83D784BE8
	0B99D41243AE292CF8CBE52D699DF2A823B29EB059D512D907BF2C962DB0AEC5C5C5C0F857641B421AB95FF9B572EAFD1605D80076004808250CE29CC3496D830048A5F3429D287B93F2032A0C16C24F1993467D45274ADEDF7B3DCB6EA4125ACCF27199C5076822A7AA229DDFE9EBBF57EEA4DB697F6B008F07C0DEB7CDD25C6A18AC36122C3E0B78B3C537AA5AEB78D90DDBA5FB682F0ACE60716609A641BD2022FACF3712E4877E301C6CE245326068BF418EEECED3A992EB28B72043C4005F5D14DF6C25C86F
	E5778B5B0F9DEE09DD1EC4AC0D64ADD9916C0DC22F2A381A49D6F540A53A874049F81DE72BE9C769F44544DB3DA468EE3FAEBE8ADB54A9FAD51D69A16F425919F1190DB0DEDE97B9C1EB622B11A23D063DEEC0F081A951552C628F6595D73EDEF64FC1751D3FBF177D1105A4EAC2226A37F91DAAC96D9306A5F98CBE18B7210332DD85EEDD02430DBF42824E2CBE1C71A21DC05123EB0A9F7B52AF7FC08998CDD5F20829921A1E6897156193B5FDFD308B53BBG3DC27787746E31CE11CF1DDD5E39FBDADE595283
	F6D0CF12060B0B695F927A37947F16D0CC8945141402F26349147FFC4B3DDCE6EABC7A544261AFEA0491D43F7B461AC57FD4993F9124EBA4A97487D7B088BB0CDAED1E619E51F5DF64E387364D0BF988474809266245FEF7FBFD6C9CF37CD1AABA14176970A3F66BA49BC3EC66E4E3C174CE9832FABAE4184B22D577523F39372DGEC351F36BD20731D6F55B9C53709DB026DBA279C4B83B40B09FF46ED467F977F55DA3E5060BF598C933048B19EDAEE55319693B6D9A59954B1CA3D749097B8269772B67820F7
	69B63F431698C06C6409E2F32BCBE527200CFDECF3F746335C7B27E7E3B546982195D9893328648923614F467EC478EFB678CFB5711F2412F43A5261B5779FE1A1182B40BF276F8E7B9F606F3A595B1D63B74DEF1EB65F1B93704DD3F914765B7AA8636D8E3B6BBC59CE7F5F9340C7C471FB12D997707EA8507DEB4730A04B2AA08F55B86CF897DBBB466EE1D85B72F66C5F4D6BE8052C6CECBB72BDE8EA73FFD0CB8788B22EE550BE9AGGF4CBGGD0CB818294G94G88G88G6CF854ACB22EE550BE9AGG
	F4CBGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGF89AGGGG
**end of data**/
}
}
