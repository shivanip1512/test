package com.cannontech.dbeditor.wizard.point;

import java.util.List;

import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.spring.YukonSpringHook;

/**
 * This type was created in VisualAge.
 */

public class PointStatusSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ItemListener {
	private javax.swing.JComboBox ivjStateTableComboBox = null;
	private javax.swing.JLabel ivjStateTableLabel = null;
	private javax.swing.JComboBox ivjJComboBoxInitialState = null;
	private javax.swing.JLabel ivjJLabelInitialState = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PointStatusSettingsPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (StateTableComboBox.item.itemStateChanged(java.awt.event.ItemEvent) --> PointStatusSettingsPanel.stateTableComboBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.stateTableComboBox_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the AlarmStateComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxInitialState() {
	if (ivjJComboBoxInitialState == null) {
		try {
			ivjJComboBoxInitialState = new javax.swing.JComboBox();
			ivjJComboBoxInitialState.setName("JComboBoxInitialState");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxInitialState;
}
/**
 * Return the AlarmStateLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelInitialState() {
	if (ivjJLabelInitialState == null) {
		try {
			ivjJLabelInitialState = new javax.swing.JLabel();
			ivjJLabelInitialState.setName("JLabelInitialState");
			ivjJLabelInitialState.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelInitialState.setText("Initial State:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelInitialState;
}
/**
 * Return the StateTableComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getStateTableComboBox() {
	if (ivjStateTableComboBox == null) {
		try {
			ivjStateTableComboBox = new javax.swing.JComboBox();
			ivjStateTableComboBox.setName("StateTableComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateTableComboBox;
}
/**
 * Return the StateTableLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStateTableLabel() {
	if (ivjStateTableLabel == null) {
		try {
			ivjStateTableLabel = new javax.swing.JLabel();
			ivjStateTableLabel.setName("StateTableLabel");
			ivjStateTableLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjStateTableLabel.setText("State Group:  ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
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
	
	//Assuming commonObject is a StatusPoint
	com.cannontech.database.data.point.StatusPoint point = (com.cannontech.database.data.point.StatusPoint) val;

	point.getPoint().setStateGroupID( new Integer(((com.cannontech.database.data.lite.LiteStateGroup)getStateTableComboBox().getSelectedItem()).getStateGroupID()) );
	point.getPointStatus().setInitialState( new Integer(((com.cannontech.database.data.lite.LiteState)getJComboBoxInitialState().getSelectedItem()).getStateRawState()) );

	return val;
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
	// user code end
	getStateTableComboBox().addItemListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PointStatusSettings");
		setPreferredSize(new java.awt.Dimension(350, 200));
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 200);

		java.awt.GridBagConstraints constraintsStateTableLabel = new java.awt.GridBagConstraints();
		constraintsStateTableLabel.gridx = 1; constraintsStateTableLabel.gridy = 1;
		constraintsStateTableLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsStateTableLabel.insets = new java.awt.Insets(51, 14, 5, 0);
		add(getStateTableLabel(), constraintsStateTableLabel);

		java.awt.GridBagConstraints constraintsJLabelInitialState = new java.awt.GridBagConstraints();
		constraintsJLabelInitialState.gridx = 1; constraintsJLabelInitialState.gridy = 2;
		constraintsJLabelInitialState.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelInitialState.insets = new java.awt.Insets(4, 14, 102, 0);
		add(getJLabelInitialState(), constraintsJLabelInitialState);

		java.awt.GridBagConstraints constraintsStateTableComboBox = new java.awt.GridBagConstraints();
		constraintsStateTableComboBox.gridx = 2; constraintsStateTableComboBox.gridy = 1;
		constraintsStateTableComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsStateTableComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsStateTableComboBox.weightx = 1.0;
		constraintsStateTableComboBox.ipadx = 105;
		constraintsStateTableComboBox.insets = new java.awt.Insets(48, 1, 4, 19);
		add(getStateTableComboBox(), constraintsStateTableComboBox);

		java.awt.GridBagConstraints constraintsJComboBoxInitialState = new java.awt.GridBagConstraints();
		constraintsJComboBoxInitialState.gridx = 2; constraintsJComboBoxInitialState.gridy = 2;
		constraintsJComboBoxInitialState.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxInitialState.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxInitialState.weightx = 1.0;
		constraintsJComboBoxInitialState.ipadx = 105;
		constraintsJComboBoxInitialState.insets = new java.awt.Insets(6, 1, 96, 19);
		add(getJComboBoxInitialState(), constraintsJComboBoxInitialState);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
@Override
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getStateTableComboBox()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		PointStatusSettingsPanel aPointStatusSettingsPanel;
		aPointStatusSettingsPanel = new PointStatusSettingsPanel();
		frame.getContentPane().add("Center", aPointStatusSettingsPanel);
		frame.setSize(aPointStatusSettingsPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * This method was created in VisualAge.
 */
private void setInitialComboBoxes() 
{
	int stateGroupID = ((com.cannontech.database.data.lite.LiteStateGroup)getStateTableComboBox().getSelectedItem()).getStateGroupID();

	//Empty out Combo Boxes first
	if( getJComboBoxInitialState().getItemCount() > 0 )
		getJComboBoxInitialState().removeAllItems();
	
	//Load all the states for the stategroup
	List<LiteState> states = YukonSpringHook.getBean(StateGroupDao.class).getLiteStates(stateGroupID);
	for (LiteState liteState : states) {
		getJComboBoxInitialState().addItem(liteState);
	}

}
	
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
@Override
public void setValue(Object val) {
	//Load all the state groups
	if( getStateTableComboBox().getModel().getSize() > 0 )
		getStateTableComboBox().removeAllItems();


	List<LiteStateGroup> allStateGroups = YukonSpringHook.getBean(StateGroupDao.class).getAllStateGroups();
	for (LiteStateGroup grp : allStateGroups)
	{
		getStateTableComboBox().addItem( grp );
	}

	setInitialComboBoxes();
}

@Override
public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        @Override
        public void run() 
            { 
            getStateTableComboBox().requestFocus(); 
        } 
    });    
}

/**
 * Comment
 */
public void stateTableComboBox_ItemStateChanged(java.awt.event.ItemEvent itemEvent) throws java.sql.SQLException{

	if( itemEvent.getStateChange() == java.awt.event.ItemEvent.SELECTED )
		setInitialComboBoxes();
		
}
}
