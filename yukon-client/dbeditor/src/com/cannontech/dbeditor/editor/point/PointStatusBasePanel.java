package com.cannontech.dbeditor.editor.point;

import java.util.List;

import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

/**
 * This type was created in VisualAge.
 */

public class PointStatusBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, java.awt.event.ItemListener 
{
	private javax.swing.JComboBox ivjInitialStateComboBox = null;
	private javax.swing.JLabel ivjInitialStateLabel = null;
	private javax.swing.JComboBox ivjStateTableComboBox = null;
	private javax.swing.JLabel ivjStateTableLabel = null;
	private javax.swing.JCheckBox ivjArchiveCheckBox = null;
/**
 * Constructor
 */
public PointStatusBasePanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
@Override
public void actionPerformed(java.awt.event.ActionEvent e) {
	if (e.getSource() == getStateTableComboBox()) 
		connEtoC1(e);
	if (e.getSource() == getInitialStateComboBox()) 
		connEtoC3(e);
	if (e.getSource() == getArchiveCheckBox()) 
		connEtoC6(e);
}
/**
 * connEtoC1:  (StateTableComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> StatusBasePanel.fireInputUpdate()V)
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
 * connEtoC3:  (InitialStateComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> StatusBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (StateTableComboBox.item.itemStateChanged(java.awt.event.ItemEvent) --> StatusBasePanel.stateTableComboBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
private void connEtoC5(java.awt.event.ItemEvent arg1) {
	try {
		this.stateTableComboBox_ItemStateChanged(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (ArchiveCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> StatusBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Return the ArchiveCheckBox property value.
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getArchiveCheckBox() {
	if (ivjArchiveCheckBox == null) {
		try {
			ivjArchiveCheckBox = new javax.swing.JCheckBox();
			ivjArchiveCheckBox.setName("ArchiveCheckBox");
			ivjArchiveCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjArchiveCheckBox.setText("Archive Data");
			ivjArchiveCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			ivjArchiveCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjArchiveCheckBox;
}

/**
 * Return the InitialStateComboBox property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getInitialStateComboBox() {
	if (ivjInitialStateComboBox == null) {
		try {
			ivjInitialStateComboBox = new javax.swing.JComboBox();
			ivjInitialStateComboBox.setName("InitialStateComboBox");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInitialStateComboBox;
}

/**
 * Return the InitialStateLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getInitialStateLabel() {
	if (ivjInitialStateLabel == null) {
		try {
			ivjInitialStateLabel = new javax.swing.JLabel();
			ivjInitialStateLabel.setName("InitialStateLabel");
			ivjInitialStateLabel.setText("Initial State:");
			ivjInitialStateLabel.setFont(new java.awt.Font("dialog", 0, 14));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInitialStateLabel;
}

/**
 * Return the StateTableComboBox property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getStateTableComboBox() {
	if (ivjStateTableComboBox == null) {
		try {
			ivjStateTableComboBox = new javax.swing.JComboBox();
			ivjStateTableComboBox.setName("StateTableComboBox");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStateTableComboBox;
}

/**
 * Return the StateTableLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getStateTableLabel() {
	if (ivjStateTableLabel == null) {
		try {
			ivjStateTableLabel = new javax.swing.JLabel();
			ivjStateTableLabel.setName("StateTableLabel");
			ivjStateTableLabel.setText("State Group:");
			ivjStateTableLabel.setFont(new java.awt.Font("dialog", 0, 14));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStateTableLabel;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
@Override
public Object getValue(Object val) {
	//Assume that defaultObject is an instance of com.cannontech.database.data.point.StatusPoint
	com.cannontech.database.data.point.StatusPoint point = (com.cannontech.database.data.point.StatusPoint) val;

	LiteStateGroup stateGroup = (LiteStateGroup) getStateTableComboBox().getSelectedItem();
	com.cannontech.database.data.lite.LiteState initialState = (com.cannontech.database.data.lite.LiteState) getInitialStateComboBox().getSelectedItem();

	point.getPoint().setStateGroupID( new Integer(stateGroup.getStateGroupID()) );
	point.getPointStatus().setInitialState( new Integer(initialState.getStateRawState()) );
	
	if( getArchiveCheckBox().isSelected() )
		point.getPoint().setArchiveType(PointArchiveType.ON_CHANGE);
	else
		point.getPoint().setArchiveType(PointArchiveType.NONE);

	return point;
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
 */
private void initConnections() throws java.lang.Exception {
	getStateTableComboBox().addActionListener(this);
	getInitialStateComboBox().addActionListener(this);
	getStateTableComboBox().addItemListener(this);
	getArchiveCheckBox().addActionListener(this);
}

private void initialize() {
	try {
		setName("StatusBasePanel");
		setLayout(new java.awt.GridBagLayout());

		java.awt.GridBagConstraints constraintsStateTableLabel = new java.awt.GridBagConstraints();
		constraintsStateTableLabel.gridx = 1; constraintsStateTableLabel.gridy = 1;
		constraintsStateTableLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsStateTableLabel.insets = new java.awt.Insets(5,5,5,5);
		add(getStateTableLabel(), constraintsStateTableLabel);

		java.awt.GridBagConstraints constraintsInitialStateLabel = new java.awt.GridBagConstraints();
		constraintsInitialStateLabel.gridx = 1; constraintsInitialStateLabel.gridy = 2;
		constraintsInitialStateLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsInitialStateLabel.insets = new java.awt.Insets(5,5,5,5);
		add(getInitialStateLabel(), constraintsInitialStateLabel);

		java.awt.GridBagConstraints constraintsStateTableComboBox = new java.awt.GridBagConstraints();
		constraintsStateTableComboBox.gridx = 2; constraintsStateTableComboBox.gridy = 1;
		constraintsStateTableComboBox.weightx = 1.0;
		constraintsStateTableComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsStateTableComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsStateTableComboBox.insets = new java.awt.Insets(5,5,5,5);
		add(getStateTableComboBox(), constraintsStateTableComboBox);

		java.awt.GridBagConstraints constraintsInitialStateComboBox = new java.awt.GridBagConstraints();
		constraintsInitialStateComboBox.gridx = 2; constraintsInitialStateComboBox.gridy = 2;
		constraintsInitialStateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsInitialStateComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsInitialStateComboBox.insets = new java.awt.Insets(5,5,5,5);
		add(getInitialStateComboBox(), constraintsInitialStateComboBox);

		java.awt.GridBagConstraints constraintsArchiveCheckBox = new java.awt.GridBagConstraints();
		constraintsArchiveCheckBox.gridx = 1; constraintsArchiveCheckBox.gridy = 3;
		constraintsArchiveCheckBox.gridwidth = 2;
		constraintsArchiveCheckBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsArchiveCheckBox.insets = new java.awt.Insets(5,5,5,5);
		add(getArchiveCheckBox(), constraintsArchiveCheckBox);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	javax.swing.border.TitledBorder border = new javax.swing.border.TitledBorder("Status Summary");
	border.setTitleFont(new java.awt.Font("dialog", java.awt.Font.BOLD, 14));
	setBorder(border);
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
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
@Override
public void itemStateChanged(java.awt.event.ItemEvent e) {
	if (e.getSource() == getStateTableComboBox()) 
		connEtoC5(e);
}
/**
 * This method was created in VisualAge.
 * @param stateGroupID java.lang.Integer
 */
private void loadStateComboBoxes(int stateGroupID) 
{
	if( getInitialStateComboBox().getItemCount() > 0 )
		getInitialStateComboBox().removeAllItems();

	LiteStateGroup stateGroup = YukonSpringHook.getBean(StateGroupDao.class).getStateGroup(stateGroupID);

	List<LiteState> statesList = stateGroup.getStatesList();
	for(int j=0;j<statesList.size();j++)
	{
		LiteState ls = statesList.get(j);
		getInitialStateComboBox().addItem(ls);
	}
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		PointStatusBasePanel aPointStatusBasePanel;
		aPointStatusBasePanel = new PointStatusBasePanel();
		frame.setContentPane(aPointStatusBasePanel);
		frame.setSize(aPointStatusBasePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
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
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
@Override
public void setValue(Object val)
{
	//Assume taht defaultObject is an instance of com.cannontech.database.data.point.StatusPoint
	com.cannontech.database.data.point.StatusPoint point = (com.cannontech.database.data.point.StatusPoint) val;

	int stateGroupID = point.getPoint().getStateGroupID().intValue();
	
	//Load all the state groups
	IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		List<LiteStateGroup> allStateGroups = YukonSpringHook.getBean(StateGroupDao.class).getAllStateGroups();

		//Load the state table combo box
		for (LiteStateGroup grp : allStateGroups)
		{
			getStateTableComboBox().addItem( grp );
			if( grp.getStateGroupID() == stateGroupID )
				getStateTableComboBox().setSelectedItem( grp );
		}
	}
		
	loadStateComboBoxes(stateGroupID);

	int initialRawState = point.getPointStatus().getInitialState().intValue();

	//Select the appropriate initial state
	for(int y=0;y<getInitialStateComboBox().getModel().getSize();y++)
	{
		if( ((com.cannontech.database.data.lite.LiteState)getInitialStateComboBox().getItemAt(y)).getStateRawState() == initialRawState  )
		{
			getInitialStateComboBox().setSelectedIndex(y);
			break;
		}
	}

	getArchiveCheckBox().setSelected(point.getPoint().getArchiveType() == PointArchiveType.ON_CHANGE);
}
/**
 * Comment
 */
public void stateTableComboBox_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {

	if( itemEvent.getStateChange() == java.awt.event.ItemEvent.SELECTED )
	{
		LiteStateGroup selected = (LiteStateGroup) getStateTableComboBox().getSelectedItem();
		loadStateComboBoxes( selected.getStateGroupID() );
	}
}
}
