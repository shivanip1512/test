package com.cannontech.dbeditor.wizard.capsubbus;

import com.cannontech.database.db.device.*;
import com.cannontech.database.data.*;
import com.cannontech.database.data.device.*;

/**
 * This type was created in VisualAge.
 */
 import java.awt.Dimension;
 import com.cannontech.database.db.*;
 import com.cannontech.database.data.device.*;

 import com.cannontech.common.gui.util.DataInputPanel;
 
public class CCSubstationBusPointSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
	private javax.swing.JComboBox ivjCalculatedVarDeviceComboBox = null;
	private javax.swing.JLabel ivjCalculatedVarDeviceLabel = null;
	private javax.swing.JComboBox ivjCalculatedVarPointComboBox = null;
	private javax.swing.JLabel ivjCalculatedVarPointLabel = null;
	private java.util.List points = null;
	private javax.swing.JPanel ivjJPanelCurrentVars = null;
	private javax.swing.JComboBox ivjJComboBoxCalcWattsDevice = null;
	private javax.swing.JComboBox ivjJComboBoxCalcWattsPoint = null;
	private javax.swing.JLabel ivjJLabelCalcWattsDevice = null;
	private javax.swing.JLabel ivjJLabelCalcWattsPoint = null;
	private javax.swing.JPanel ivjJPanelCurrentWatts = null;

	//store this object locally so we dont have to hit the database every time
	// the user clicks a VAR device
	private com.cannontech.common.util.NativeIntVector usedVARPtIDs = new com.cannontech.common.util.NativeIntVector(10);
	
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CCSubstationBusPointSettingsPanel() {
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
	if (e.getSource() == getCalculatedVarDeviceComboBox()) 
		connEtoC1(e);
	if (e.getSource() == getJComboBoxCalcWattsDevice()) 
		connEtoC3(e);
	if (e.getSource() == getJComboBoxCalcWattsPoint()) 
		connEtoC4(e);
	if (e.getSource() == getCalculatedVarPointComboBox()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void calculatedVarDeviceComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	this.fireInputUpdate();

	if(  getCalculatedVarDeviceComboBox().getSelectedItem() != null )
	{
		int deviceID = ((com.cannontech.database.data.lite.LiteYukonPAObject)getCalculatedVarDeviceComboBox().getSelectedItem()).getYukonID();
		
		if( getCalculatedVarPointComboBox().getModel().getSize() > 0 )
			getCalculatedVarPointComboBox().removeAllItems();

		for(int i=0;i<points.size();i++)
		{
			com.cannontech.database.data.lite.LitePoint point = (com.cannontech.database.data.lite.LitePoint)points.get(i);

			if( deviceID == point.getPaobjectID()
				 && (point.getPointType() == com.cannontech.database.data.point.PointTypes.ANALOG_POINT
					  || point.getPointType() == com.cannontech.database.data.point.PointTypes.CALCULATED_POINT)				
				 && !usedVARPtIDs.contains(point.getPointID()) )
			{
				getCalculatedVarPointComboBox().addItem(points.get(i));
			}
			else if( deviceID < point.getPaobjectID() )
				break;
		}
	}
	
	return;
}
/**
 * connEtoC1:  (CalculatedVarDeviceComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> CCSubstationBusPointSettingsPanel.calculatedVarDeviceComboBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.calculatedVarDeviceComboBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (CalculatedVarPointComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> CCSubstationBusPointSettingsPanel.calculatedVarPointComboBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
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
 * connEtoC3:  (JComboBoxCalcWattsDevice.action.actionPerformed(java.awt.event.ActionEvent) --> CCSubstationBusPointSettingsPanel.jComboBoxCalcWattsDevice_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxCalcWattsDevice_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JComboBoxCalcWattsPoint.action.actionPerformed(java.awt.event.ActionEvent) --> CCSubstationBusPointSettingsPanel.fireInputUpdate()V)
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
 * Return the CalculatedVarDeviceComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getCalculatedVarDeviceComboBox() {
	if (ivjCalculatedVarDeviceComboBox == null) {
		try {
			ivjCalculatedVarDeviceComboBox = new javax.swing.JComboBox();
			ivjCalculatedVarDeviceComboBox.setName("CalculatedVarDeviceComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCalculatedVarDeviceComboBox;
}
/**
 * Return the CalculatedVarDeviceLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCalculatedVarDeviceLabel() {
	if (ivjCalculatedVarDeviceLabel == null) {
		try {
			ivjCalculatedVarDeviceLabel = new javax.swing.JLabel();
			ivjCalculatedVarDeviceLabel.setName("CalculatedVarDeviceLabel");
			ivjCalculatedVarDeviceLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjCalculatedVarDeviceLabel.setText("Var Device:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCalculatedVarDeviceLabel;
}
/**
 * Return the CalculatedVarPointComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getCalculatedVarPointComboBox() {
	if (ivjCalculatedVarPointComboBox == null) {
		try {
			ivjCalculatedVarPointComboBox = new javax.swing.JComboBox();
			ivjCalculatedVarPointComboBox.setName("CalculatedVarPointComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCalculatedVarPointComboBox;
}
/**
 * Return the CalculatedVarPointLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCalculatedVarPointLabel() {
	if (ivjCalculatedVarPointLabel == null) {
		try {
			ivjCalculatedVarPointLabel = new javax.swing.JLabel();
			ivjCalculatedVarPointLabel.setName("CalculatedVarPointLabel");
			ivjCalculatedVarPointLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjCalculatedVarPointLabel.setText("Var Point:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCalculatedVarPointLabel;
}
/**
 * Return the JComboBoxCalcWattsDevice property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxCalcWattsDevice() {
	if (ivjJComboBoxCalcWattsDevice == null) {
		try {
			ivjJComboBoxCalcWattsDevice = new javax.swing.JComboBox();
			ivjJComboBoxCalcWattsDevice.setName("JComboBoxCalcWattsDevice");
			// user code begin {1}

			ivjJComboBoxCalcWattsDevice.addItem( com.cannontech.database.data.lite.LiteYukonPAObject.LITEPAOBJECT_NONE );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxCalcWattsDevice;
}
/**
 * Return the JComboBoxCalcWattsPoint property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxCalcWattsPoint() {
	if (ivjJComboBoxCalcWattsPoint == null) {
		try {
			ivjJComboBoxCalcWattsPoint = new javax.swing.JComboBox();
			ivjJComboBoxCalcWattsPoint.setName("JComboBoxCalcWattsPoint");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxCalcWattsPoint;
}
/**
 * Return the JLabelCalcWattsDevice property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCalcWattsDevice() {
	if (ivjJLabelCalcWattsDevice == null) {
		try {
			ivjJLabelCalcWattsDevice = new javax.swing.JLabel();
			ivjJLabelCalcWattsDevice.setName("JLabelCalcWattsDevice");
			ivjJLabelCalcWattsDevice.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelCalcWattsDevice.setText("Watts Device:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCalcWattsDevice;
}
/**
 * Return the JLabelCalcWattsPoint property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCalcWattsPoint() {
	if (ivjJLabelCalcWattsPoint == null) {
		try {
			ivjJLabelCalcWattsPoint = new javax.swing.JLabel();
			ivjJLabelCalcWattsPoint.setName("JLabelCalcWattsPoint");
			ivjJLabelCalcWattsPoint.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelCalcWattsPoint.setText("Watts Point:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCalcWattsPoint;
}
/**
 * Return the JPanelCurrentVars property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelCurrentVars() {
	if (ivjJPanelCurrentVars == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjLocalBorder.setTitle("Current Vars");
			ivjJPanelCurrentVars = new javax.swing.JPanel();
			ivjJPanelCurrentVars.setName("JPanelCurrentVars");
			ivjJPanelCurrentVars.setBorder(ivjLocalBorder);
			ivjJPanelCurrentVars.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsCalculatedVarPointLabel = new java.awt.GridBagConstraints();
			constraintsCalculatedVarPointLabel.gridx = 0; constraintsCalculatedVarPointLabel.gridy = 1;
			constraintsCalculatedVarPointLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCalculatedVarPointLabel.ipadx = 7;
			constraintsCalculatedVarPointLabel.insets = new java.awt.Insets(8, 10, 31, 4);
			getJPanelCurrentVars().add(getCalculatedVarPointLabel(), constraintsCalculatedVarPointLabel);

			java.awt.GridBagConstraints constraintsCalculatedVarDeviceLabel = new java.awt.GridBagConstraints();
			constraintsCalculatedVarDeviceLabel.gridx = 0; constraintsCalculatedVarDeviceLabel.gridy = 0;
			constraintsCalculatedVarDeviceLabel.gridwidth = 2;
			constraintsCalculatedVarDeviceLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCalculatedVarDeviceLabel.ipadx = 6;
			constraintsCalculatedVarDeviceLabel.insets = new java.awt.Insets(25, 10, 9, 220);
			getJPanelCurrentVars().add(getCalculatedVarDeviceLabel(), constraintsCalculatedVarDeviceLabel);

			java.awt.GridBagConstraints constraintsCalculatedVarDeviceComboBox = new java.awt.GridBagConstraints();
			constraintsCalculatedVarDeviceComboBox.gridx = 1; constraintsCalculatedVarDeviceComboBox.gridy = 0;
			constraintsCalculatedVarDeviceComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCalculatedVarDeviceComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCalculatedVarDeviceComboBox.weightx = 1.0;
			constraintsCalculatedVarDeviceComboBox.ipadx = 81;
			constraintsCalculatedVarDeviceComboBox.insets = new java.awt.Insets(25, 25, 5, 42);
			getJPanelCurrentVars().add(getCalculatedVarDeviceComboBox(), constraintsCalculatedVarDeviceComboBox);

			java.awt.GridBagConstraints constraintsCalculatedVarPointComboBox = new java.awt.GridBagConstraints();
			constraintsCalculatedVarPointComboBox.gridx = 1; constraintsCalculatedVarPointComboBox.gridy = 1;
			constraintsCalculatedVarPointComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCalculatedVarPointComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCalculatedVarPointComboBox.weightx = 1.0;
			constraintsCalculatedVarPointComboBox.ipadx = 81;
			constraintsCalculatedVarPointComboBox.insets = new java.awt.Insets(5, 25, 27, 42);
			getJPanelCurrentVars().add(getCalculatedVarPointComboBox(), constraintsCalculatedVarPointComboBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelCurrentVars;
}
/**
 * Return the JPanelCurrentWatts property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelCurrentWatts() {
	if (ivjJPanelCurrentWatts == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjLocalBorder1.setTitle("Current Watts");
			ivjJPanelCurrentWatts = new javax.swing.JPanel();
			ivjJPanelCurrentWatts.setName("JPanelCurrentWatts");
			ivjJPanelCurrentWatts.setBorder(ivjLocalBorder1);
			ivjJPanelCurrentWatts.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelCalcWattsPoint = new java.awt.GridBagConstraints();
			constraintsJLabelCalcWattsPoint.gridx = 0; constraintsJLabelCalcWattsPoint.gridy = 1;
			constraintsJLabelCalcWattsPoint.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelCalcWattsPoint.ipadx = 7;
			constraintsJLabelCalcWattsPoint.insets = new java.awt.Insets(8, 10, 31, 4);
			getJPanelCurrentWatts().add(getJLabelCalcWattsPoint(), constraintsJLabelCalcWattsPoint);

			java.awt.GridBagConstraints constraintsJLabelCalcWattsDevice = new java.awt.GridBagConstraints();
			constraintsJLabelCalcWattsDevice.gridx = 0; constraintsJLabelCalcWattsDevice.gridy = 0;
			constraintsJLabelCalcWattsDevice.gridwidth = 2;
			constraintsJLabelCalcWattsDevice.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelCalcWattsDevice.ipadx = 6;
			constraintsJLabelCalcWattsDevice.insets = new java.awt.Insets(25, 10, 9, 220);
			getJPanelCurrentWatts().add(getJLabelCalcWattsDevice(), constraintsJLabelCalcWattsDevice);

			java.awt.GridBagConstraints constraintsJComboBoxCalcWattsDevice = new java.awt.GridBagConstraints();
			constraintsJComboBoxCalcWattsDevice.gridx = 1; constraintsJComboBoxCalcWattsDevice.gridy = 0;
			constraintsJComboBoxCalcWattsDevice.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxCalcWattsDevice.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxCalcWattsDevice.weightx = 1.0;
			constraintsJComboBoxCalcWattsDevice.ipadx = 81;
			constraintsJComboBoxCalcWattsDevice.insets = new java.awt.Insets(25, 25, 5, 42);
			getJPanelCurrentWatts().add(getJComboBoxCalcWattsDevice(), constraintsJComboBoxCalcWattsDevice);

			java.awt.GridBagConstraints constraintsJComboBoxCalcWattsPoint = new java.awt.GridBagConstraints();
			constraintsJComboBoxCalcWattsPoint.gridx = 1; constraintsJComboBoxCalcWattsPoint.gridy = 1;
			constraintsJComboBoxCalcWattsPoint.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxCalcWattsPoint.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxCalcWattsPoint.weightx = 1.0;
			constraintsJComboBoxCalcWattsPoint.ipadx = 81;
			constraintsJComboBoxCalcWattsPoint.insets = new java.awt.Insets(5, 25, 27, 42);
			getJPanelCurrentWatts().add(getJComboBoxCalcWattsPoint(), constraintsJComboBoxCalcWattsPoint);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelCurrentWatts;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension( 350, 200);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	com.cannontech.database.data.capcontrol.CapControlSubBus ccBus = (com.cannontech.database.data.capcontrol.CapControlSubBus)val;

	ccBus.getCapControlSubstationBus().setCurrentVarLoadPointID( new Integer(((com.cannontech.database.data.lite.LitePoint)getCalculatedVarPointComboBox().getSelectedItem()).getPointID()) );

	if( !((com.cannontech.database.data.lite.LiteYukonPAObject)getJComboBoxCalcWattsDevice().getSelectedItem()).equals( com.cannontech.database.data.lite.LiteYukonPAObject.LITEPAOBJECT_NONE ) )
		ccBus.getCapControlSubstationBus().setCurrentWattLoadPointID( 
					new Integer(((com.cannontech.database.data.lite.LitePoint)getJComboBoxCalcWattsPoint().getSelectedItem()).getPointID()) );
	else
		ccBus.getCapControlSubstationBus().setCurrentWattLoadPointID( 
					new Integer(com.cannontech.database.data.point.PointTypes.SYS_PID_SYSTEM) );
		
	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getCalculatedVarDeviceComboBox().addActionListener(this);
	getJComboBoxCalcWattsDevice().addActionListener(this);
	getJComboBoxCalcWattsPoint().addActionListener(this);
	getCalculatedVarPointComboBox().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CCSubstationBusPointSettingsPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(345, 259);

		java.awt.GridBagConstraints constraintsJPanelCurrentVars = new java.awt.GridBagConstraints();
		constraintsJPanelCurrentVars.gridx = 1; constraintsJPanelCurrentVars.gridy = 1;
		constraintsJPanelCurrentVars.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelCurrentVars.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelCurrentVars.weightx = 1.0;
		constraintsJPanelCurrentVars.weighty = 1.0;
		constraintsJPanelCurrentVars.ipadx = -31;
		constraintsJPanelCurrentVars.ipady = -30;
		constraintsJPanelCurrentVars.insets = new java.awt.Insets(4, 4, 4, 5);
		add(getJPanelCurrentVars(), constraintsJPanelCurrentVars);

		java.awt.GridBagConstraints constraintsJPanelCurrentWatts = new java.awt.GridBagConstraints();
		constraintsJPanelCurrentWatts.gridx = 1; constraintsJPanelCurrentWatts.gridy = 2;
		constraintsJPanelCurrentWatts.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelCurrentWatts.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelCurrentWatts.weightx = 1.0;
		constraintsJPanelCurrentWatts.weighty = 1.0;
		constraintsJPanelCurrentWatts.ipadx = -45;
		constraintsJPanelCurrentWatts.ipady = -30;
		constraintsJPanelCurrentWatts.insets = new java.awt.Insets(5, 4, 32, 5);
		add(getJPanelCurrentWatts(), constraintsJPanelCurrentWatts);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	
	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	try
	{
		if( getCalculatedVarPointComboBox().getSelectedItem() != null
			 && getJComboBoxCalcWattsPoint().getSelectedItem() != null )
		{
			if( ((com.cannontech.database.data.lite.LitePoint)getCalculatedVarPointComboBox().getSelectedItem()).getPointID()
				 == ((com.cannontech.database.data.lite.LitePoint)getJComboBoxCalcWattsPoint().getSelectedItem()).getPointID() )
			{
				setErrorString("The Calc Var Point can not be the same point as the Watt Var Point");
				return false;
			}
		}
	}
	catch( ClassCastException e )
	{
		//strange stuff here, would be the programmers error if this happens
		handleException(e);
		return false;
	}	

	return true;
}
/**
 * Comment
 */
public void jComboBoxCalcWattsDevice_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	this.fireInputUpdate();

	int deviceID = ((com.cannontech.database.data.lite.LiteYukonPAObject)getJComboBoxCalcWattsDevice().getSelectedItem()).getYukonID();
	
	getJComboBoxCalcWattsPoint().removeAllItems();

	for(int i=0;i<points.size();i++)
	{
		if( deviceID == ((com.cannontech.database.data.lite.LitePoint)points.get(i)).getPaobjectID() )
			getJComboBoxCalcWattsPoint().addItem(points.get(i));
		else if( deviceID < ((com.cannontech.database.data.lite.LitePoint)points.get(i)).getPaobjectID() )
			break;
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
		CCSubstationBusPointSettingsPanel aCCSubstationBusPointSettingsPanel;
		aCCSubstationBusPointSettingsPanel = new CCSubstationBusPointSettingsPanel();
		frame.setContentPane(aCCSubstationBusPointSettingsPanel);
		frame.setSize(aCCSubstationBusPointSettingsPanel.getSize());
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
		exception.printStackTrace(System.out);
	}
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
private void setPointComboBoxes(Object val) 
{	
	com.cannontech.database.data.capcontrol.CapControlSubBus ccBus = (com.cannontech.database.data.capcontrol.CapControlSubBus)val;
	com.cannontech.database.data.lite.LitePoint varPoint = null;
	com.cannontech.database.data.lite.LitePoint wattPoint = null;

	//find the var point we have assigned to the sub bus
	for( int i = 0; i < points.size(); i++ )
	{
		if( ccBus.getCapControlSubstationBus().getCurrentVarLoadPointID().intValue()
			 == ((com.cannontech.database.data.lite.LitePoint)points.get(i)).getPointID() )
		{
			varPoint = (com.cannontech.database.data.lite.LitePoint)points.get(i);
		}

		if( ccBus.getCapControlSubstationBus().getCurrentWattLoadPointID().intValue()
			 == ((com.cannontech.database.data.lite.LitePoint)points.get(i)).getPointID() )
		{
			wattPoint = (com.cannontech.database.data.lite.LitePoint)points.get(i);
		}

		if( varPoint != null
			 && 
			 ( ccBus.getCapControlSubstationBus().getCurrentWattLoadPointID().intValue() <=
				com.cannontech.database.data.point.PointTypes.SYS_PID_SYSTEM
				|| (wattPoint != null)) )
			break; //help speed up this loop

	}

	//set the device combo boxes and point combo boxes to the appropriate selections
	com.cannontech.database.cache.DefaultDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List allDevices = cache.getAllDevices();
		java.util.Collections.sort(allDevices);

		for( int i = 0; i < allDevices.size(); i++ )
		{
			com.cannontech.database.data.lite.LiteYukonPAObject device = (com.cannontech.database.data.lite.LiteYukonPAObject)allDevices.get(i);
			
			if( varPoint.getPaobjectID() == device.getYukonID() )
			{
				getCalculatedVarDeviceComboBox().setSelectedItem(device);
				getCalculatedVarPointComboBox().setSelectedItem(varPoint);
			}

			if( wattPoint != null && wattPoint.getPaobjectID() == device.getYukonID() )
			{
				getJComboBoxCalcWattsDevice().setSelectedItem(device);
				getJComboBoxCalcWattsPoint().setSelectedItem(wattPoint);
			}

			if( (wattPoint == null || 
				 (getJComboBoxCalcWattsPoint().getSelectedItem() != null && wattPoint.getPointID() == 
				    ((com.cannontech.database.data.lite.LitePoint)getJComboBoxCalcWattsPoint().getSelectedItem()).getPointID()) )
				 && (getCalculatedVarPointComboBox().getSelectedItem() != null 
					  && varPoint.getPointID() == 
				  	 ((com.cannontech.database.data.lite.LitePoint)getCalculatedVarPointComboBox().getSelectedItem()).getPointID()) )
			{
				break;
			}
			
		}
	}
	
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) 
{
	getCalculatedVarDeviceComboBox().removeAllItems();
	getCalculatedVarPointComboBox().removeAllItems();

	com.cannontech.database.cache.DefaultDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List devices = cache.getAllDevices();
		points = cache.getAllPoints();

		java.util.Collections.sort(devices);
		java.util.Collections.sort(points,com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator);

		int deviceID;
		com.cannontech.database.data.lite.LitePoint litePoint = null;
		com.cannontech.database.data.lite.LiteYukonPAObject liteDevice = null;
		boolean deviceAddedToCalculated;


		if( val == null )
			usedVARPtIDs = com.cannontech.database.db.capcontrol.CapControlSubstationBus.getUsedVARPointIDs(0);
		else
			usedVARPtIDs = com.cannontech.database.db.capcontrol.CapControlSubstationBus.getUsedVARPointIDs(
				((com.cannontech.database.data.capcontrol.CapControlSubBus)val).getCapControlPAOID().intValue() );
			
		for(int i=0;i<devices.size();i++)
		{
			liteDevice = (com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i);
			deviceID = liteDevice.getYukonID();
			deviceAddedToCalculated = false;


			for(int j=0;j<points.size();j++)
			{
				litePoint = (com.cannontech.database.data.lite.LitePoint)points.get(j);
				if( litePoint.getPaobjectID() == deviceID
					 && com.cannontech.database.data.pao.DeviceClasses.isCoreDeviceClass(liteDevice.getPaoClass()) )
				{
					if( (litePoint.getPointType() == com.cannontech.database.data.point.PointTypes.ANALOG_POINT
						 || litePoint.getPointType() == com.cannontech.database.data.point.PointTypes.CALCULATED_POINT)
						 && !usedVARPtIDs.contains(litePoint.getPointID()) )
					{
						getCalculatedVarDeviceComboBox().addItem(liteDevice);						
						getJComboBoxCalcWattsDevice().addItem(liteDevice);
						break;
					}

				}
				else if( litePoint.getPaobjectID() > deviceID )
					break;
			}
		}
	}


	//val will not be null if we are using this panel for an editor
	if( val != null )
		setPointComboBoxes( val );

}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GE0F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BB8DD8D45715287813E8A2C6CC08A109FFC9E8240DD9ED57EC2D2554FCB10D5DF5D3734526EEEB92F76337CB32EE42FE350979F299E0C445008A9185949084D203E834A2E282028410C4A208A4319B5EB08F989C660DB38F9990F54F39775D775E8CEFA03035DF574F43FB771C7BF36E793B675E7B06246C1F959FE3CAA1A4A61EA87FCE4490D2F2161006851967B8AEC00CB4935D3FEC009D44F1A28E
	1EC1C0DB7B3DC8F3B21936A3814A73209CB0A7523C9B5EF7119F652CAC0397BF6304814D32AFB4E468F1E66E6663EC2665ED16C4F83E8F9086B8FCC60BB87E81CB1A02DF2160B9441888D95F0EF5BE31E4A83892A8B7G4C83D85C4E6A2F046796BA72785292255E65E933C874AF72CD5B319FDD1F9C0ABBD85BA66519C51E08595401E3F5D0BE314D94A8AF83404947121F27FF864F3206AB9D1F34C95DC5ED0243A1B9E4312D33285756AF382C00F13AFBAC169EF7E9E9E5E5E30F45AD8B32CDF21C69F1B7C8B6
	075CA84A324D5161EE909C221DF8BAF12CA7AC8934EFC24A219CE7C31C338947DF8265D5GD1AA7E44C5445740FBAB00474667FCBA3FCC19D34B75A7C8600B0E58A4BFF38E323139AE36E9F38E6EF9FB739E2A1F1752D08F51C0CB6B0AB49F82E8GF082848124D33BF8F97707702C304815D2F7376428F0BA4F582D2E4FA52BE8077752D220A8388BE21B64329242F45FEDB9E4404FD0405ADD9FF1BBA613300F399D04DFF8142CD9F84E3459C0A6EB420ACDE1DDCC9691DD1ACC58580B7C0E3D364B5BCE3D473E
	C474E34B932458F80331973DD0E80AD67AC938220DDDF285F53D0E5AC0813C47A8764170EFD07C008E4FEC769A45E3F98A50B6DC61365172BE6725BEEEA6C9BED2EFA80710488AD3131D71E0377B72B21F5ADD081D0D39C565E5B145AF526199AF27A99E4BAB01F62CBB524C6C6B553D6843A1D0DE843082E09DC01CA3521C85F04C41ED6C4334435F51469A8417FC2647E6370AAE425A3E1A7681BCC50F6C92EA9D60FF0EB631D9F059840BDDF49325CED49A4A4062556E3F286D3741981F098E51C57D96FA973A
	C5D9F439D91D3753F6E09D17C829CD36EE918668F6925E2FE94FAE141F6016AFBA2D02ACEAB598FD6B9E1449B52199FA048E601D49A5B9836555847337838C28F298CF7BBBA73AA0DC141656CB52159EE7934E042CF0201C1F2331A3885E439CDC46091908CB16A24D85926FE7491E1D3AFE2AECED483E606A63BE766FCD98274BA4EEB3EF34F01B295A7E88B974C21D214DD0DB11182D84CA3E3E93CD7BC43ED8F92029E504FEBC9414C95E7E47461871979DA356274C645B3F8FF5958E63AF87D82F4A7877FB32
	184D1E915CA2375273AEB0B0512AC91C71796445E4F862BA9B2F701E2E4EE1A92591A7688F2081406364B22C3C281F9FEFBB549CF6AB4F746FB0DFB64665910F41653465BB0C41E4757ADEDC1F44EB22C32E921D22438AAFEE05963D977BD17C37DDD25B3CCD71CE7077CAF0B0370AA769681302AC3BEC169ED964CB2CCACFC84731E0D5F6EA21020F15140E7313FA415F557AA96968ABED1243A1363135DBE92250BF203EAA5A45EEE03AD26AF1488C1F110176FEC56CD3E2C5D686A6C45784FB8F469D16BFAC90
	36619CDC36EEB0858CDDA46EAA7A71ABEA7E03B08B740DF950EAF8C6B939CF769E60BE195BBF1BC4FC1BE7B2DAD3C29E29B255DFE53E51FAD573C946530ECCE35EF7531821605D319477C6B666F043E7B15CAF966E473E83E04CE000702B5CAF26E7A21D479E45A1AAA53B1D09D715FB40BE34FB37F835C704100DC16E1CAA4B097BF67A501A9539B27A54FDA8D73BAEBE863BD02EEFDE64B963B260E78D40262BCC36E5AEA69BD66FC74D3C5EDAA8EFF4E975525C7AFADD17F8BDBB6083816639D93DF5DE750ADA
	F83DB8B9521C81D0A6FB57E3763079DB4CAF0C72490E9EDBD10FEC43E8AD4860884E9E190A1B650C75ED89CA9F83B24F8DBD6DF4AD46F1G56495CEE7EF9BB4F8D032F4CA2EB676E751B8FEF1295FB69516CA6EB9C4F8D5F4D42B1DB01666C613A5D3ADF3FBED5385DD21B0D1A0A221727766B6D0A61166E5F8A4FF61B4BAD570263FB388E176DC7FEF16DD450CAAE115E1C284CB59046DD8250F48D7133EAD3F43CC783BE837065G4CFE47B69F4538C00D2C08BD9AC4D73B646A962D85DD42B521C868150BE88C
	AA2A20643378DE48F5F375F49D4681415E56E307255C8AC9C415F84D56A6C2CEE2114EC81EEFABFCBA2D775F6B2942E61E9B4E59D52E703C6287B3C9592DDDA623DCFDD2D226A9749A534332EB1ABE867E4E9C9B75BF6FECE632791DAA1B645EC8F3CDAF170DD463AB1BG204D6B657B18472D380F6962B32904C9DE02886AE6939C694498AF53AFF1DE9643B89BD4DE5E98B062254043757476257F1B1E16956EE476A4D2AFE109D7814A1B71F5290573D586E34A9E4E575F779B59CF384A5752165172455651AF
	5BFC778E919EADE7D965993CFFE0B9F5947563759E16D3472B785FD37CA69D1E4D6FE63BEA83FDB03FBE1E87CE4941B5E5B5142381927BA34D39GD5G56FE6E3F75D99F13E1726CDA08C7AE07E0EF74DAC0F93E1B0D3202B5CFA91B33118F3A6691AE076CE6DFB98474FB4FBD345FA817BBBC285DBEDFF97A34E37650E1CF67B1953564A9F27742E2D5DADA472DDDD9F38FF768575C557DAC075E844F646B10F351B87BE2AD5F1B97DC677174C635BE1EAEC9BAEA5A66A71E36DEE77C85DC577C77103A577ED3AE
	2E9191C0DB83908D10BCGFBA00086GF9006BA8A87B74F0BA220657A4D5822E865640CA5F5CC515693F0ED297EF8D2B0B60816FF6CC469FDDB012F1B8540D9860B2FE34064BF875G4FF36F3FB4BABE9F9B542E48271CFC433B9D63A16F062A679B1C07BF370C0E07AB035A797250EA48439B7D1C878F50597E2E132E738B203CCC451D9591379E4A64A667754DF64465C239CA453DCBF19853B05EB2DCE07BE877C758B7FF2F3AB9B8F6E973FFF054E36877DB71FE6AB01F181163FF7F901AE33CFF1813E33CFF
	884B59417050077EB4015397648C3D3FD81CE3343F482473F97603A45A375BAD3A2913E2B22750DE13D30941BE638B21DBD4E4310B34B4EFD16A1C15254AA3044738378A512F8CE21CAEC76C68C8D0F4E23D49F344E7BC38D604C2F991408A55D656548EF546364EC05FAC067DA48F57500DC0CF3995E9BE84D085E03D45E357978706DDDF4EFA06D8DF4AF37D6B7933DCE3BDFF1E0B792DDB6AF135093E3AEC4ACDD3E9DA142C1545EE94BB2B73E75ADE96DC9D62A0DB3052760C662FFDF72EBFFBE1BA4D382457
	69472A7F0B97383E82EFF1FDE51F246740D0DE8590F50B6BEBFE4DD07A0A8B982CAFB676CA2F310F29BE7B610D217ACB735BDFF00B3E3F23EAFFEF758F555FA13F7D5DEA43BD33DD6813FAE4C24283D086773671B5249C4AADG81G4181ECADE17E70E4AF2E416DAE219BC45D68945B4824B1587632485BC6C37D844095G54GF40E61F9DE742728AF66B915BDAE8C83106AEB7A6ABC325317CEF3E5BED7FB07FE2EAB215FF5E33479ED9C63AF4FBB55073ACD9C6BFB26DAC37185E3FD4FD4FF6961E72AD5C08B
	9B4B570337EA79FA10B80E7B5D53F97E7DE6C51E315FBC1FE79C9FD7678D9D9F57667933F7A61F2E8BFA79DC869E1D63B419840E73A71FEF4A681973B8DF79F4D27C22F13E7249BF291EB9836DD8A017CFE48D17CF080A2B3841F16BD55C566B9CD7B29E72C05A6752B6FE8FB489F09163393D9DE9455CCD0D3F3893A8AD7D4CE5331E91BA6A2975B21FB6DB9314769B47F31FBEE645BE0BA7009D8234CE60F92567A04FAB439F1DC96A834E996E5357B69DB5F9A6B01905CE50724AB048AB5918E3C5BE66EA20A7
	CFC45C82D13F57C13B5EECE2F989233F62C5C7FD9EA0FAFACA3B1E0E7CAE74EADF6DC5C799E5AA6D595C1E4B63F35B74DC98317F70245FB3081609ECCE834A332179G493D731D4DCCC763681A3688E84BA7F23D7DA69FF1D1D0A613B86E6BC22E4B5CC9BCE6D6746179128C659000A5G6BD45A971416F8872CE1F730F3A2BC33E134DFF974798CEFBBD49EA36B686B06294B4608720C764C156BE3283A81F7D07B2E2B9A26BFFE1F04D08FEDDA818EA87C4870BED72943E4BC062ED74BD1DEF7F0391745A00E5C
	8979401D9C77E3136AD7F7F2198AE5A86FC9D08E43FAG9BD41A0352D2A6038FCFE6E7F1F84E46E89F1D9CAA37443E5ED77A7525ED70412DB0284346289EF28C1E177A5E8D986941C807CC973F2E9909AE1AC08E1D13B5DD8CCC56F4417AEB39B1DCFFFA186453DF68207E322F0F243F30495E3632C4579F136932C1E7A14381EB777CA85BED330E349DDBDB4CA246666513595A92236213A8FE158E4F5607D2ABDF9F56822DEA8ABFFB191B0FF9C7981417812C810886C839AB52DCFC97DF232F9DACA6436446CD
	12339E96F13BEE59E5731BA80E6ECE7CE71BF99CDE748FF3486289FF0871F7F6D1F597738F2B72DC5FF4006215338B3A7F88865A8200D5GD1G646EC8F35A5DFCFECB8F9EE273BBEB35499532AC34F552D3E0BE39E61B582B21B1E9E379564C8E4C81AD120BA699AC87FCD2BD2BB9847DD7818CG845D4D4E6CD83BB77387670EDB8AF83B303BD96E98814FD5CABBE66BD78BC7E26BD15016CCE5360E6594F87F79FB6F3D77B4G632374AB3CF32B541F9B53DB13FA41A25A151C7235EDE93EF55851AB2D446AAC
	B912EEDCC7BD47E275661F5FEBB81EDA0D30734E31FD7AB5BAEBAA13C155D4FE9E7AC803913D851E7B69B8045058CE7FED78F4B08EE4BF953F81F220D9AB7676F2A237370C798F102A977F90F3400F3DAD184A6C0C84E9B94D7A2ECD1B599CC3AE8C3609A754332512A0E6938D70348631738E562EB1E630CDF44570F683C14CA60261992634E3B6713CE9B81B085759C48434DD95245944FAF8FF4C4BA6D2F2D036F5D43B6AE1BB559223BFD316BA184E34CD78BE64A23DA32C73F3E64F6AEC3C3E5B308E9DCA31
	03B9E53EFA46F9C787F1BB2828B622A7CC9B299D2C46638AE25406515FB9BAB45D095BD3A51EA70A5AFB2A6E3D5C7A173C6FB7DECB3E3A2DE330D8991E6FFF3F36B05FF83F76D43E71FE6D2779C31FD7BD136F7FBE7C9F73757761AC56179FC7FB48G7D17GDC86F08284CE63F17EF2C18E21F15EA0461F35F660B98BFE05A37371BFAD74BF576A42613F5DB9DF08770796C91625EE6C1F63AF96223EE449C9118C77F5A14A48E56B6814D9D5855FCA7787F6311D2259DABE3513FFBFB58B6636FA9A5F6B3D16D9
	C8063B23ED3249F6710C921E14731C97BB799DED72BDFC6FD710CF3F5F03F2AB00679E3E4FBCD2407308A040AD3F071F0D7795703D9C194E7BF80C56AD01F283C067746F6A175C2FFE75A7C254F344F6BF6708B492B19E1E6A6367D28153B9DFEB9AB8EE71F43EFF7D27EF745FDDA64F4CB671DC495766B7CCE7B1FE53F4AD56B3DBB317E05CAD8E0EB45783B881A6810482AC82D88E3086E083C0420CC8F39640B1009A004BGF600G0060995CB69796236E1A05AEA53B42D4C4F2D16B842432E127866A92B0C2
	723C6E458B5C9642202F35B3783C77B7F21D265D4B75F187413A8DD06E848838176B741E982E5390402D3C177771D28C57E942FD3C0F849356AD07F28B00733E116A741513C4353BDF17F15DCC3A0F0F69296638087B38ECEA0E7B77437347077743166374EE9F7C27294F494FCE0F63FAAB35373BC5555FAD4713D5DCAD2CC4EE9B1E19AA5FEBF58E1747EE3C4F680EE56E306DFC6F2714F3EE5AAEA5E468BDB907822847727FD06A37E5A13E7B02C718BF07786E8B0A72396E1B0036B8046FDFCF940CEEBFF3F6
	146DEE0E389D1BD7FAA1721FF13F6FFCF3A93E60FE5F79FEDF0DDF470096F2BF1F6F52BE7D781B6EE7F2CF1E491E39B37D6D41DFEE5037ABD16A55AB4FD603F6ACFECCADC5BFD03EE9BA11C77C78BE2CCCF5906E4EDB3A44B639C968D05B6CAD716F9359A5437B4407A546EBD3F149603569F809715AF42AC43FB6A977A0A598F3BAA0DE3528754E2A382FD4DC9D6D93F117ECD639D345B7D03E90DFA362009C5FCC796A25389685770D8E7795DFAFCB502FE14A1D120B3795CB70FB37F61B5D4E4AB6DA4766GB7
	D7656120BC5A1C020EEF370A7BCA55773FDC98DD9C780FE15B9947015CC6ECA79B581191CC1A4963403BB456074E74758BB30D75E1B3FD7DE2AA61FE3188E8E98FF03F8809991DBFBFBA4AF63F9BF1BBB62F813AAE8DBC60BB5F7194BF69815F793EEF62738D865AC6F53E3FAD733A8388E5F2F7AA4F40D0FFF160C6353EDD08D2AFDCF9C69834E3F72B913B36D3DB345B9CE2A57B221B70F34B37E8DE368A5AC60572B34A8587691A7AE024B965C14E731CFC2272B1DF77FEAC7F766FB9BE7FAB0C11DD284D69C3
	5D7B42025BBF760A5BBA8653698BB4EF4D82FD96BF286AB996F1D5D0EED2F129F17CFC3AD545ED24796D82A8C728381F24D33B0172BA951713415BEED0F127E80ED87E9064F28FF15C8E1A3359219C7090372F5898CD66093A779964764B7F87FF05B1461A34B9CD563D2718EE7F589F5D56B118CE7F1B6659D3C01F212A1EEBD2295DC0B9C245FDBF155B488A9597CC73710C07A34D079ED66FF129AD15C339C14535E47236179F66E7DADBED38464B38632C16681EA56AE1EA0BB62C9B8E6F0B553A4F9C0C67F5
	89BF17794FD3288BC60F6A23EBB8F4437AB051EF100AE7C51A4BE629FD44E29D99929F56BE9FBFC7D36E898E458DF5CE263F43C0E8555DA9A14D8EE3A4696A6DA2C9DFFA731377A5726301FA01AABF2B523D79297BC46367E446F07C70EFA70D78D984E3EC776267C223312C3B280FC7C07D95AADF85E4302C47FD2A49BA7134775CF2CE53B5E7F624B9F1B66F635BD46F397D31C21B5B325411483AD3F747033432593E321EDF6D4D4F4F28CD56C03DA6151F2D195E7CFCF1C663C74A9C092CFD79891C2D49DA31
	2BDACDD6F38F62026B90FB33548FEDD17E85B9D61BE017BA0AAC125D1AF3A86B887CBFD6281CB54F6A551FBF14ECBDE1787B88042059ACEF1827BC17CCBC2070700BF39A8F73E2F306628106AFE643ABEDDF2FA76ADCEA4FEB7DBC19BE32FE6C8DDABF250DDABFF1E4A4B2E16D8FB6EB6D4BD2C746C76DA50D0FAB2DDABF2FE50C2C1F3B85EC4B76E727686F5556005CB74E666790765ABF927FE7B48DAE49A93A64BE06253F7A605F06D9D0F76AB9A7A371EF43ACF8E6E9F358E4604956CFFF125088959D9D6C37
	4DAD47AE40C2917EBA09F73BC812BA0FDA3AF9D9FB6D635258D767461AC8DCADB1334DDCAD09439DD9AD09279B36DA928B1BB90FD3B1F01C9C7EA8F60F7BAA39C90D51981B2E43468F030DD3GFB0AD331D9BAEC3CCC92158F1232700BA519EC05C796BD6BAF1549B6AC304B81A8ED67B569FDG3424CF3572F6D6D42A03D036FBA9776959477FED45EF1B5D3144DCCB76E04CE894DDD4342295BFE9D4FE4FD8CD36B8698F04G8F92F353C73CDB6E330B75A4257D827EFE47AD0B568A2EB4E0A4157E3860B01E23
	9E2E915C1DB2EE28CF5513B8877E72126CE4E43B6068B84CB4F80A0ABD26021AA40BB5406FAE4AAF76D22476326B47C9F959F56B964792D3B559EA1784EB355086D6D4CB36481DAE515D899E63F1FEDACEBBCA68C5535CC6256DA2696A74F132C5CD9081D5262FF89E1C32FD7D76EA48E9BCAEF218564490D9245630D6F68AAE98CAF4554A78CBD249152B52466C138F7E72335D17CDA4C619A46AB64963140939DD6836597B60250A3A92557AAEA8324D38EA2AA80C22EA180F7C526B4F4E79CD5906A8682C1A44
	536F6E0125E42D5697BD5D9651B5A1FCC6663660351805F91CB8DD5E739659A5B85C781B8668BC4BE74E1C2432DEB47D5962995FFB7BEB4A3A56D4A67B19122069618ED16ABEDC515E8E9BAF35835098F41F29F60FF50AFC6A6C74CCCB2DFB61BD8BD8D895096DAEAE26FF0F503FC7716F910AB9C2B1C70E025AB610A17F7952BDFC4E54ACE9E1AF5A10875FF6B36AF69F43E2F25AB148C2E2911D8A0AE1A18FCB19346AFE7AB7E7443CE12BDC9F1E8ABD2A5763416FD64573937E76B8AD56B85D22F03A06703867
	4BE90C9FCE63B827D5AA27E978A7637670FC62EFD063277F5FE83C4A0F46793DFF61ADFDAE36F1FC1A5FDCAC77F1160315BC6EFB97A452F3D09970C10F73DC6107A7BEA27E4FF49B3B853BDD966CD7AA9CD6BC5FBD53A70B5A3DE7D789766F66F594A223DD256726DD3AB67F8BD0CB87885B8237126B96GGE0C5GGD0CB818294G94G88G88GE0F954AC5B8237126B96GGE0C5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGA5
	97GGGG
**end of data**/
}
}
