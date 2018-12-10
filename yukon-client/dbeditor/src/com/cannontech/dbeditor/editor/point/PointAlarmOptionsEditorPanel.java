package com.cannontech.dbeditor.editor.point;

import java.util.List;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.clientutils.tags.IAlarmDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.core.dao.AlarmCatDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.database.db.point.PointAlarming.AlarmNotificationTypes;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

public class PointAlarmOptionsEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
	private PointAlarmOptionsEditorTableModel tableModel = null;
	private javax.swing.JPanel ivjConfigurationPanel = null;
	private javax.swing.JScrollPane ivjJScrollPaneAlarmStates = null;
	private javax.swing.JTable ivjJTableAlarmStates = null;
	public static final LiteContact NONE_LITE_CONTACT =
			new LiteContact( CtiUtilities.NONE_ZERO_ID, 
					null, CtiUtilities.STRING_NONE );
	private javax.swing.JCheckBox ivjJCheckBoxNotifyWhenAck = null;
	private javax.swing.JCheckBox ivjJCheckBoxDisableAllAlarms = null;
	private javax.swing.JComboBox ivjJComboBoxGroup = null;
	private javax.swing.JLabel ivjJLabelGroup = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JCheckBox ivjJCheckBoxNotifyOnClear = null;
	private javax.swing.JLabel ivjJLabelNotifyOn = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		@Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == PointAlarmOptionsEditorPanel.this.getJCheckBoxDisableAllAlarms()) 
				connEtoC1(e);
			if (e.getSource() == PointAlarmOptionsEditorPanel.this.getJCheckBoxNotifyWhenAck()) 
				connEtoC4(e);
			if (e.getSource() == PointAlarmOptionsEditorPanel.this.getJCheckBoxNotifyOnClear()) 
				connEtoC5(e);
		}
	}
/**
 * Constructor
 */
public PointAlarmOptionsEditorPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
@Override
public void actionPerformed(java.awt.event.ActionEvent e) {
	if (e.getSource() == getJCheckBoxDisableAllAlarms()) 
		connEtoC1(e);
	if (e.getSource() == getJCheckBoxNotifyWhenAck()) 
		connEtoC4(e);
	if (e.getSource() == getJComboBoxGroup()) 
		fireInputUpdate();
}
/**
 * connEtoC1:  (AlarmInhibitCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> PointAlarmOptionsEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoC4:  (JCheckBoxNotifyWhenAck.action.actionPerformed(java.awt.event.ActionEvent) --> PointAlarmOptionsEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoC5:  (JCheckBoxNotifyOnClear.action.actionPerformed(java.awt.event.ActionEvent) --> PointAlarmOptionsEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * Return the ConfigurationPanel property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getConfigurationPanel() {
	if (ivjConfigurationPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog", 0, 14));
			ivjLocalBorder.setTitle("Notification");
			ivjConfigurationPanel = new javax.swing.JPanel();
			ivjConfigurationPanel.setName("ConfigurationPanel");
			ivjConfigurationPanel.setBorder(ivjLocalBorder);
			ivjConfigurationPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJCheckBoxNotifyWhenAck = new java.awt.GridBagConstraints();
			constraintsJCheckBoxNotifyWhenAck.gridx = 2; constraintsJCheckBoxNotifyWhenAck.gridy = 3;
			constraintsJCheckBoxNotifyWhenAck.ipadx = 2;
			constraintsJCheckBoxNotifyWhenAck.ipady = -2;
			constraintsJCheckBoxNotifyWhenAck.insets = new java.awt.Insets(3, 2, 14, 1);
			getConfigurationPanel().add(getJCheckBoxNotifyWhenAck(), constraintsJCheckBoxNotifyWhenAck);

			java.awt.GridBagConstraints constraintsJLabelGroup = new java.awt.GridBagConstraints();
			constraintsJLabelGroup.gridx = 1; constraintsJLabelGroup.gridy = 1;
			constraintsJLabelGroup.ipadx = 14;
			constraintsJLabelGroup.ipady = -1;
			constraintsJLabelGroup.insets = new java.awt.Insets(27, 15, 5, 10);
			getConfigurationPanel().add(getJLabelGroup(), constraintsJLabelGroup);

			java.awt.GridBagConstraints constraintsJComboBoxGroup = new java.awt.GridBagConstraints();
			constraintsJComboBoxGroup.gridx = 2; constraintsJComboBoxGroup.gridy = 1;
			constraintsJComboBoxGroup.gridwidth = 2;
			constraintsJComboBoxGroup.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxGroup.weightx = 1.0;
			constraintsJComboBoxGroup.ipadx = -21;
			constraintsJComboBoxGroup.insets = new java.awt.Insets(25, 2, 2, 13);
			getConfigurationPanel().add(getJComboBoxGroup(), constraintsJComboBoxGroup);

			java.awt.GridBagConstraints constraintsJLabelNotifyOn = new java.awt.GridBagConstraints();
			constraintsJLabelNotifyOn.gridx = 1; constraintsJLabelNotifyOn.gridy = 3;
			constraintsJLabelNotifyOn.ipady = 4;
			constraintsJLabelNotifyOn.insets = new java.awt.Insets(2, 15, 14, 2);
			getConfigurationPanel().add(getJLabelNotifyOn(), constraintsJLabelNotifyOn);

			java.awt.GridBagConstraints constraintsJCheckBoxNotifyOnClear = new java.awt.GridBagConstraints();
			constraintsJCheckBoxNotifyOnClear.gridx = 3; constraintsJCheckBoxNotifyOnClear.gridy = 3;
			constraintsJCheckBoxNotifyOnClear.gridwidth = 2;
			constraintsJCheckBoxNotifyOnClear.ipadx = 52;
			constraintsJCheckBoxNotifyOnClear.ipady = -2;
			constraintsJCheckBoxNotifyOnClear.insets = new java.awt.Insets(3, 2, 14, 70);
			getConfigurationPanel().add(getJCheckBoxNotifyOnClear(), constraintsJCheckBoxNotifyOnClear);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjConfigurationPanel;
}

/**
 * Return the AlarmInhibitCheckBox property value.
 * @return javax.swing.JCheckBox
 */
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxDisableAllAlarms;
}

/**
 * Return the JCheckBoxNotifyOnClear property value.
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getJCheckBoxNotifyOnClear() {
	if (ivjJCheckBoxNotifyOnClear == null) {
		try {
			ivjJCheckBoxNotifyOnClear = new javax.swing.JCheckBox();
			ivjJCheckBoxNotifyOnClear.setName("JCheckBoxNotifyOnClear");
			ivjJCheckBoxNotifyOnClear.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJCheckBoxNotifyOnClear.setText("Clear");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxNotifyOnClear;
}

/**
 * Return the JCheckBoxNotifyWhenAck property value.
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getJCheckBoxNotifyWhenAck() {
	if (ivjJCheckBoxNotifyWhenAck == null) {
		try {
			ivjJCheckBoxNotifyWhenAck = new javax.swing.JCheckBox();
			ivjJCheckBoxNotifyWhenAck.setName("JCheckBoxNotifyWhenAck");
			ivjJCheckBoxNotifyWhenAck.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJCheckBoxNotifyWhenAck.setText("Acknowledge");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxNotifyWhenAck;
}

/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getJComboBoxGroup() {
	if (ivjJComboBoxGroup == null) {
		try {
			ivjJComboBoxGroup = new javax.swing.JComboBox();
			ivjJComboBoxGroup.setName("JComboBoxGroup");
			ivjJComboBoxGroup.setEnabled(true);

			IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized( cache )
			{
				java.util.List notifGroups = cache.getAllContactNotificationGroupsWithNone();

				for( int i = 0; i < notifGroups.size(); i++ )
					ivjJComboBoxGroup.addItem( notifGroups.get(i) );
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxGroup;
}

/**
 * Return the JLabelType property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelGroup() {
	if (ivjJLabelGroup == null) {
		try {
			ivjJLabelGroup = new javax.swing.JLabel();
			ivjJLabelGroup.setName("JLabelGroup");
			ivjJLabelGroup.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelGroup.setText("Group:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelGroup;
}

/**
 * Return the JLabelNotifyOn property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelNotifyOn() {
	if (ivjJLabelNotifyOn == null) {
		try {
			ivjJLabelNotifyOn = new javax.swing.JLabel();
			ivjJLabelNotifyOn.setName("JLabelNotifyOn");
			ivjJLabelNotifyOn.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelNotifyOn.setText("Notify On: ");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelNotifyOn;
}
/**
 * Return the JScrollPaneAlarmStates property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getJScrollPaneAlarmStates() {
	if (ivjJScrollPaneAlarmStates == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog", 0, 14));
			ivjLocalBorder1.setTitle("Alarming");
			ivjJScrollPaneAlarmStates = new javax.swing.JScrollPane();
			ivjJScrollPaneAlarmStates.setName("JScrollPaneAlarmStates");
			ivjJScrollPaneAlarmStates.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneAlarmStates.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneAlarmStates.setBorder(ivjLocalBorder1);
			getJScrollPaneAlarmStates().setViewportView(getJTableAlarmStates());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneAlarmStates;
}
/**
 * Return the JTableAlarmStates property value.
 * @return javax.swing.JTable
 */
private javax.swing.JTable getJTableAlarmStates() {
	if (ivjJTableAlarmStates == null) {
		try {
			ivjJTableAlarmStates = new javax.swing.JTable();
			ivjJTableAlarmStates.setName("JTableAlarmStates");
			getJScrollPaneAlarmStates().setColumnHeaderView(ivjJTableAlarmStates.getTableHeader());
			getJScrollPaneAlarmStates().getViewport().setBackingStoreEnabled(true);
			ivjJTableAlarmStates.setBounds(0, 0, 200, 200);
			ivjJTableAlarmStates.setAutoCreateColumnsFromModel(true);
			ivjJTableAlarmStates.setModel( getTableModel() );
			ivjJTableAlarmStates.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjJTableAlarmStates.setGridColor( java.awt.Color.black );
			ivjJTableAlarmStates.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			ivjJTableAlarmStates.setRowHeight(20);
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJTableAlarmStates;
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
@Override
public Object getValue(Object val) 
{
	//Consider commonObject an instance of com.cannontech.database.data.point.PointBase
	com.cannontech.database.data.point.PointBase point = 
			(com.cannontech.database.data.point.PointBase) val;

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
	List<LiteAlarmCategory> liteAlarmStates = YukonSpringHook.getBean(AlarmCatDao.class).getAlarmCategories();
	
	for( i = 0; i < getJTableAlarmStates().getRowCount(); i++ )
	{
		int alarmStateID = com.cannontech.database.db.point.PointAlarming.NONE_NOTIFICATIONID;
		
		for( int j = 0; j < liteAlarmStates.size(); j++ )
		{
			if( ((com.cannontech.database.data.lite.LiteAlarmCategory)liteAlarmStates.get(j)).getCategoryName() == getTableModel().getGenerateAt(i) )
			{
				alarmStateID = ((com.cannontech.database.data.lite.LiteAlarmCategory)liteAlarmStates.get(j)).getAlarmCategoryId();
				break;
			}
		}
			
		char generate = (char)alarmStateID;
		String notify = getTableModel().getExcludeNotifyAt(i);

		alarmStates += generate;
		
		if(notify.equals(AlarmNotificationTypes.EXCLUDE_NOTIFY.getDbString()))
			excludeNotifyState += 'E';
		else if(notify.equals(AlarmNotificationTypes.AUTO_ACK.getDbString()))
			excludeNotifyState += 'A';
		else if(notify.equals(AlarmNotificationTypes.BOTH_OPTIONS.getDbString()))
				excludeNotifyState += 'B';
		else
			excludeNotifyState += 'N';
		
	}
	
	// fill in the rest of the alarmStates and excludeNotifyState so we have 32 chars
	alarmStates += com.cannontech.database.db.point.PointAlarming.DEFAULT_ALARM_STATES.substring(i);
	excludeNotifyState += com.cannontech.database.db.point.PointAlarming.DEFAULT_EXCLUDE_NOTIFY.substring(i);
	
	point.getPointAlarming().setAlarmStates(alarmStates);
	point.getPointAlarming().setExcludeNotifyStates(excludeNotifyState);
	
	//notify on acknowledge	
	if( getJCheckBoxNotifyWhenAck().isSelected() && ! getJCheckBoxNotifyOnClear().isSelected())
		point.getPointAlarming().setNotifyOnAcknowledge("A");
	//notify on clear
	else if ( ! getJCheckBoxNotifyWhenAck().isSelected() && getJCheckBoxNotifyOnClear().isSelected())
		point.getPointAlarming().setNotifyOnAcknowledge("C");
	//notify on both
	else if ( getJCheckBoxNotifyWhenAck().isSelected() && getJCheckBoxNotifyOnClear().isSelected())
		point.getPointAlarming().setNotifyOnAcknowledge("B");
	else
	point.getPointAlarming().setNotifyOnAcknowledge("N");

	// get the selected notificationGroup from its combo box and insert its id
	LiteNotificationGroup grp = 
		(LiteNotificationGroup)getJComboBoxGroup().getSelectedItem();

    if(grp != null){
        point.getPointAlarming().setNotificationGroupID( new Integer(grp.getNotificationGroupID()) );
    }
	
	return point;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );
}
/**
 * Initializes connections
 */
private void initConnections() throws java.lang.Exception {

	getJComboBoxGroup().addActionListener(this);
	
	getJCheckBoxDisableAllAlarms().addActionListener(ivjEventHandler);
	getJCheckBoxNotifyWhenAck().addActionListener(ivjEventHandler);
	getJCheckBoxNotifyOnClear().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("PointAlarmOptionsEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(384, 363);

		java.awt.GridBagConstraints constraintsConfigurationPanel = new java.awt.GridBagConstraints();
		constraintsConfigurationPanel.gridx = 1; constraintsConfigurationPanel.gridy = 1;
		constraintsConfigurationPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsConfigurationPanel.weightx = 1.0;
		constraintsConfigurationPanel.weighty = 1.0;
		constraintsConfigurationPanel.ipadx = -10;
		constraintsConfigurationPanel.ipady = -29;
		constraintsConfigurationPanel.insets = new java.awt.Insets(9, 9, 0, 10);
		add(getConfigurationPanel(), constraintsConfigurationPanel);

		java.awt.GridBagConstraints constraintsJScrollPaneAlarmStates = new java.awt.GridBagConstraints();
		constraintsJScrollPaneAlarmStates.gridx = 1; constraintsJScrollPaneAlarmStates.gridy = 2;
		constraintsJScrollPaneAlarmStates.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneAlarmStates.weightx = 1.0;
		constraintsJScrollPaneAlarmStates.weighty = 1.0;
		constraintsJScrollPaneAlarmStates.ipadx = 336;
		constraintsJScrollPaneAlarmStates.ipady = 138;
		constraintsJScrollPaneAlarmStates.insets = new java.awt.Insets(1, 9, 2, 10);
		add(getJScrollPaneAlarmStates(), constraintsJScrollPaneAlarmStates);

		java.awt.GridBagConstraints constraintsJCheckBoxDisableAllAlarms = new java.awt.GridBagConstraints();
		constraintsJCheckBoxDisableAllAlarms.gridx = 1; constraintsJCheckBoxDisableAllAlarms.gridy = 3;
		constraintsJCheckBoxDisableAllAlarms.ipadx = 62;
		constraintsJCheckBoxDisableAllAlarms.insets = new java.awt.Insets(3, 9, 13, 209);
		add(getJCheckBoxDisableAllAlarms(), constraintsJCheckBoxDisableAllAlarms);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}

	initJTableCellComponents();
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
	javax.swing.table.TableColumn notifyColumn = getJTableAlarmStates().getColumnModel().getColumn(PointAlarmOptionsEditorTableModel.NOTIFY_COLUMN);
	nameColumn.setPreferredWidth(120);
	generateColumn.setPreferredWidth(120);
	notifyColumn.setPreferredWidth(160);
	
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
	com.cannontech.common.gui.util.ComboBoxTableRenderer comboBxRender = new com.cannontech.common.gui.util.ComboBoxTableRenderer();
	com.cannontech.common.gui.util.ComboBoxTableRenderer notifyComboBxRender = new com.cannontech.common.gui.util.ComboBoxTableRenderer();

    // Exclude notify
    notifyComboBxRender.addItem(AlarmNotificationTypes.NONE.getDbString());
    notifyComboBxRender.addItem(AlarmNotificationTypes.EXCLUDE_NOTIFY.getDbString());
    notifyComboBxRender.addItem(AlarmNotificationTypes.AUTO_ACK.getDbString());
    notifyComboBxRender.addItem(AlarmNotificationTypes.BOTH_OPTIONS.getDbString());
    notifyColumn.setCellRenderer(notifyComboBxRender);

	List<LiteAlarmCategory> allAlarmStates = YukonSpringHook.getBean(AlarmCatDao.class).getAlarmCategories();

	for( int i = 0; i < allAlarmStates.size(); i++ )
		comboBxRender.addItem( ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get(i)).getCategoryName() );

	generateColumn.setCellRenderer(comboBxRender);

	// Create and add the column CellEditors
	javax.swing.JComboBox combo = new javax.swing.JComboBox();
	javax.swing.JComboBox notifyCombo = new javax.swing.JComboBox();
	combo.setBackground(getJTableAlarmStates().getBackground());
	notifyCombo.setBackground(getJTableAlarmStates().getBackground());
	combo.addActionListener( new java.awt.event.ActionListener()
	{
		@Override
        public void actionPerformed(java.awt.event.ActionEvent e) 
		{
			fireInputUpdate();
		}
	});
	
	notifyCombo.addActionListener( new java.awt.event.ActionListener()
	{
		@Override
        public void actionPerformed(java.awt.event.ActionEvent e) 
		{
			fireInputUpdate();
		}
	});

	for( int i = 0; i < allAlarmStates.size(); i++ )
		combo.addItem( ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get(i)).getCategoryName() );

	generateColumn.setCellEditor( new javax.swing.DefaultCellEditor(combo) );
	
	notifyCombo.addItem(AlarmNotificationTypes.NONE.getDbString());
	notifyCombo.addItem(AlarmNotificationTypes.EXCLUDE_NOTIFY.getDbString());
	notifyCombo.addItem(AlarmNotificationTypes.AUTO_ACK.getDbString());
	notifyCombo.addItem(AlarmNotificationTypes.BOTH_OPTIONS.getDbString());
	notifyColumn.setCellEditor( new javax.swing.DefaultCellEditor(notifyCombo) );
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 9:11:36 AM)
 * @return boolean
 */
@Override
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
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );
	}
}

/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
@Override
public void setValue(Object val) 
{
	
	//Consider defaultObject an instance of com.cannontech.database.data.point.PointBase
	com.cannontech.database.data.point.PointBase point = (com.cannontech.database.data.point.PointBase) val;
	PointType ptType = point.getPoint().getPointTypeEnum();
	
	Character alarmInhibit = point.getPoint().getAlarmInhibit();

	if( alarmInhibit != null )
		SwingUtil.setCheckBoxState( getJCheckBoxDisableAllAlarms(), alarmInhibit );
		
    //be sure we have a 32 character string
	String alarmStates =
      ( point.getPointAlarming().getAlarmStates().length() != PointAlarming.ALARM_STATE_COUNT
        ? PointAlarming.DEFAULT_ALARM_STATES
        : point.getPointAlarming().getAlarmStates() );
        
	String excludeNotifyStates = point.getPointAlarming().getExcludeNotifyStates();

	IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )	
	{
		List<LiteAlarmCategory> allAlarmStates = YukonSpringHook.getBean(AlarmCatDao.class).getAlarmCategories();
		String generate = new String();

		if( allAlarmStates.size() <= 0 )
			throw new ArrayIndexOutOfBoundsException("No AlarmStates exist, unable to create alarms, occurred in " + this.getClass() );
	   
      
		if( ptType == PointType.Status
			|| ptType == PointType.CalcStatus)
		{
			String[] stateNames = null;

			LiteStateGroup stateGroup = YukonSpringHook.getBean(StateGroupDao.class).getStateGroup(point.getPoint().getStateGroupID());

			stateNames = new String[stateGroup.getStatesList().size()];

			for( int j = 0; j < stateGroup.getStatesList().size(); j++ )
				stateNames[j] = stateGroup.getStatesList().get(j).toString();
		
			// insert all the predefined states into the JTable
			int i = 0;
			for( i = 0; i < IAlarmDefs.STATUS_ALARM_STATES.length; i++ )
			{
				if( ((alarmStates.charAt(i))-1) < allAlarmStates.size() )
					generate = ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get( (alarmStates.charAt(i))-1 )).getCategoryName();
				else
					generate = ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get(0)).getCategoryName();
						
				String notifyString = new String();
				if ( Character.toUpperCase(excludeNotifyStates.charAt(i)) == 'E' ||
						Character.toUpperCase(excludeNotifyStates.charAt(i)) == 'Y')
					notifyString = AlarmNotificationTypes.EXCLUDE_NOTIFY.getDbString();
				else if ( Character.toUpperCase(excludeNotifyStates.charAt(i)) == 'A' )
				    notifyString = AlarmNotificationTypes.AUTO_ACK.getDbString();
				else if ( Character.toUpperCase(excludeNotifyStates.charAt(i)) == 'B' )
				notifyString = AlarmNotificationTypes.BOTH_OPTIONS.getDbString();
				else
				    notifyString = AlarmNotificationTypes.NONE.getDbString();
				
				getTableModel().addRowValue( IAlarmDefs.STATUS_ALARM_STATES[i], generate, notifyString );
			}
			
			for( int j = 0; j < stateNames.length; j++, i++ )
			{
				if( i >= alarmStates.length() )
					throw new ArrayIndexOutOfBoundsException("Trying to get alarmStates["+i+"] while alarmStates.length()==" + alarmStates.length() + ", to many states for Status point " + point.getPoint().getPointName() + " defined.");
						
				if( ((alarmStates.charAt(i))-1) < allAlarmStates.size() )
					generate = ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get( (alarmStates.charAt(i))-1 )).getCategoryName();
				else
					generate = ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get(0)).getCategoryName();
						
				String notifyString = new String();
				if ( Character.toUpperCase(excludeNotifyStates.charAt(i)) == 'E' ||
						Character.toUpperCase(excludeNotifyStates.charAt(i)) == 'Y')
				    notifyString = AlarmNotificationTypes.EXCLUDE_NOTIFY.getDbString();
				else if ( Character.toUpperCase(excludeNotifyStates.charAt(i)) == 'A' )
				    notifyString = AlarmNotificationTypes.AUTO_ACK.getDbString();
				else if ( Character.toUpperCase(excludeNotifyStates.charAt(i)) == 'B' )
				    notifyString = AlarmNotificationTypes.BOTH_OPTIONS.getDbString();
				else
				    notifyString = AlarmNotificationTypes.NONE.getDbString();
				
				getTableModel().addRowValue( stateNames[j], generate, notifyString );
			}		
			
		}
		else
		{  
			// All other point types are processed here
			// insert all the predefined states into the JTable
			for( int i = 0; i < IAlarmDefs.OTHER_ALARM_STATES.length; i++ )
			{
				if( ((alarmStates.charAt(i))-1) < allAlarmStates.size() )
					generate = ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get( (alarmStates.charAt(i))-1 )).getCategoryName();
				else
					generate = ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get(0)).getCategoryName();
						
				String notifyString = new String();
				if ( Character.toUpperCase(excludeNotifyStates.charAt(i)) == 'E' ||
						Character.toUpperCase(excludeNotifyStates.charAt(i)) == 'Y')
					notifyString = AlarmNotificationTypes.EXCLUDE_NOTIFY.getDbString();
				else if ( Character.toUpperCase(excludeNotifyStates.charAt(i)) == 'A' )
				    notifyString = AlarmNotificationTypes.AUTO_ACK.getDbString();
				else if ( Character.toUpperCase(excludeNotifyStates.charAt(i)) == 'B' )
				    notifyString = AlarmNotificationTypes.BOTH_OPTIONS.getDbString();
				else
				notifyString = AlarmNotificationTypes.NONE.getDbString();
				
				getTableModel().addRowValue( IAlarmDefs.OTHER_ALARM_STATES[i], generate, notifyString );
			}		
		}
	
		// assign the correct notificationGroup to the getJComboBoxGroup() component
		java.util.List notifGroups = cache.getAllContactNotificationGroups();
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

	if( alarmInhibit != null )
	{
		switch(point.getPointAlarming().getNotifyOnAcknowledge().charAt(0))
		{
			case 'A': 
				getJCheckBoxNotifyWhenAck().setSelected(true);
				getJCheckBoxNotifyOnClear().setSelected(false);
				break;
			case 'C':
				getJCheckBoxNotifyWhenAck().setSelected(false);
				getJCheckBoxNotifyOnClear().setSelected(true);
				break;
			case 'B':
				getJCheckBoxNotifyWhenAck().setSelected(true);
				getJCheckBoxNotifyOnClear().setSelected(true);
				break;
			case 'N':
			default:
				getJCheckBoxNotifyWhenAck().setSelected(false);
				getJCheckBoxNotifyOnClear().setSelected(false);
		}
	}
	getTableModel().fireTableDataChanged();
	
	return;
}
}
