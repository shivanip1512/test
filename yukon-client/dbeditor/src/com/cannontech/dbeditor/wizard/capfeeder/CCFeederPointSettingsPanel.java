package com.cannontech.dbeditor.wizard.capfeeder;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.database.data.capcontrol.CapControlFeeder;
 
public class CCFeederPointSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
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
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CCFeederPointSettingsPanel() {
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
	if (e.getSource() == getCalculatedVarPointComboBox()) 
		connEtoC2(e);
	if (e.getSource() == getJComboBoxCalcWattsPoint()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void calculatedVarDeviceComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	this.fireInputUpdate();

	int deviceID = ((com.cannontech.database.data.lite.LiteYukonPAObject)getCalculatedVarDeviceComboBox().getSelectedItem()).getYukonID();
	
	if( getCalculatedVarPointComboBox().getModel().getSize() > 0 )
		getCalculatedVarPointComboBox().removeAllItems();

	for(int i=0;i<points.size();i++)
	{
		if( deviceID == ((com.cannontech.database.data.lite.LitePoint)points.get(i)).getPaobjectID() )
			getCalculatedVarPointComboBox().addItem(points.get(i));
		else if( deviceID < ((com.cannontech.database.data.lite.LitePoint)points.get(i)).getPaobjectID() )
			break;
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
 * connEtoC2:  (CalculatedVarPointComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> CCFeederPointSettingsPanel.fireInputUpdate()V)
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
 * connEtoC4:  (JComboBoxCalcWattsPoint.action.actionPerformed(java.awt.event.ActionEvent) --> CCFeederPointSettingsPanel.fireInputUpdate()V)
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
	CapControlFeeder ccFeeder = (CapControlFeeder)val;

	ccFeeder.getCapControlFeeder().setCurrentVarLoadPointID( new Integer(((com.cannontech.database.data.lite.LitePoint)getCalculatedVarPointComboBox().getSelectedItem()).getPointID()) );

	if( !((com.cannontech.database.data.lite.LiteYukonPAObject)getJComboBoxCalcWattsDevice().getSelectedItem()).equals( com.cannontech.database.data.lite.LiteYukonPAObject.LITEPAOBJECT_NONE ) )
		ccFeeder.getCapControlFeeder().setCurrentWattLoadPointID( 
					new Integer(((com.cannontech.database.data.lite.LitePoint)getJComboBoxCalcWattsPoint().getSelectedItem()).getPointID()) );
	else
		ccFeeder.getCapControlFeeder().setCurrentWattLoadPointID( 
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
 * Insert the method's description here.
 * Creation date: (11/15/2001 2:33:55 PM)
 */
private void initComboBoxes() 
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
		
		for(int i=0;i<devices.size();i++)
		{
			liteDevice = (com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i);
			deviceID = liteDevice.getYukonID();
			deviceAddedToCalculated = false;
			
			for(int j=0;j<points.size();j++)
			{
				litePoint = (com.cannontech.database.data.lite.LitePoint)points.get(j);
				if( litePoint.getPaobjectID() == deviceID )
				{
					if( litePoint.getPointType() == com.cannontech.database.data.point.PointTypes.ANALOG_POINT
						 || litePoint.getPointType() == com.cannontech.database.data.point.PointTypes.CALCULATED_POINT )
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
	getCalculatedVarPointComboBox().addActionListener(this);
	getJComboBoxCalcWattsPoint().addActionListener(this);
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

	initComboBoxes();
	
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
		CCFeederPointSettingsPanel aCCFeederPointSettingsPanel;
		aCCFeederPointSettingsPanel = new CCFeederPointSettingsPanel();
		frame.setContentPane(aCCFeederPointSettingsPanel);
		frame.setSize(aCCFeederPointSettingsPanel.getSize());
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
public void setValue(Object val ) 
{	
	//val will not be null if we are using this panel for an editor
	if( val != null )
	{
		CapControlFeeder ccFeeder = (CapControlFeeder)val;
		com.cannontech.database.data.lite.LitePoint varPoint = null;
		com.cannontech.database.data.lite.LitePoint wattPoint = null;

		//find the var point we have assigned to the sub bus
		for( int i = 0; i < points.size(); i++ )
		{
			if( ccFeeder.getCapControlFeeder().getCurrentVarLoadPointID().intValue()
				 == ((com.cannontech.database.data.lite.LitePoint)points.get(i)).getPointID() )
			{
				varPoint = (com.cannontech.database.data.lite.LitePoint)points.get(i);
			}

			if( ccFeeder.getCapControlFeeder().getCurrentWattLoadPointID().intValue()
				 == ((com.cannontech.database.data.lite.LitePoint)points.get(i)).getPointID() )
			{
				wattPoint = (com.cannontech.database.data.lite.LitePoint)points.get(i);
			}

			if( varPoint != null
				 && 
				 ( ccFeeder.getCapControlFeeder().getCurrentWattLoadPointID().intValue() <=
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
	
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G6FF854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BB8BF4D4C516B004FF04A081A2C48D0ACE540C62486E32331CB31841999C33ABAA2E6CEE9CB3E798656CC1A747C587B5C7AA3FCEC2928890C8070F045F88CE8C89C64C04007901846290A5E0C0443CCE3FF4BAF43A1B57AFC9F7786D3DD52F5EFB69FE1D90D6664CF23829D7372AEE5D3A3F3AD56FB5495F9E151E1C12CFC8F2BAD17EFD16CCC8E2BDA13FACFDF083478D93E3CDC477EF8740BA32481B
	86E59834C5BC94EB4AA3E7CEE5C2FD9654AB6E0BB5954073867211F5CC95BC841927955A2E9D7C6365016759F10E4F13C16B9F1BF220DC83908DB8ACD708B87FD113D94147A9F88E49A904A434E31FD653D685D78175A600C800596D2C7FC2A833684CA3ABAB14FE97A6C511953F7EB0A5976968E8F258EBE3E39B153208BC163C4206F30D25FC621890284F8340452712974ED4C3D955F449F6244D55D552A6B81DAE27AC36F514745A7B84498A98F73BA8DAC52932322E6E98FDEAF2591D72E9D1166DCE1B27C9
	F00A8EF7874E711CA913520475C0BD096246ED44F96740F39C40D2957F55A644D75BE3CDADGEEBBDF6B618F2B143534DCF90CA43D64CCDD9BE42DE36CEC0D336D5ADA433B5F4DA824FA914E237C17C3DBDEE72CE9B740F1008E00B1GDB28BDF41C7B884A9A0BDC676A6AF2B9EB5C6EDA07D57A52E5959D70DCD989AD8A6EEB314DA5D989E1BA1FE8AAB660E7A0E0636E9E72B8A61368CF387EE33EFE1044BFF1B2A543C0A6715125A9D11DCC96F3BBB51930397FBD685CCBBA7B5BE77F393FA17A39170FF22526
	9B4CBD6745FDA94B95BA6412B6F769A554757B54860A60B9D9318F064F24B6A06B704CD643E8FF2C07C05B32CB5CB6DA56F0DE9A5326133C32C6C3B9C4445624B4BA988FD607BFAFCF51B9439CEC4EAC151767A9BEC607E73CFCC371D8DF88ED45DD31A6E6DF1D9750F743209E8330GA09EA089406C04FECEEEE30F1F5BFF13B656A4C8F2ED375D818EC658584E731B20943D32A4B4B8BD3260EC934F8A12DD30B8C48FD17AF84E238CAC7D46F9296D37419CDF08CED192E43B4B895405AED196A58F6BB37C7CBA
	6CA3093435595EA54284DDEE42690EBD0F8157A1F864B3EE2BA00BDA8F56BE69BC4A24C7B88B9401GBCB339CC7B8E6555886B3782482A9CF23EC5FAA7C58922C5E5E5234BF5295B5D0CABA18B1CA86757E96C98834FD1CEAE6349979117690AB59539B81D3AEF576B68545B5B10FDC172F19FCB5B0471395C45ED660D96EEB375398F106297CF985A8C35959733152BAEFF5FD9C3E9A2DF2CBEF5E34B907DF8A0A8F77577BFB647E3BF689C29FEF5A65F1CD65455AC18FFBEC002AA636B5F96B11B2D95BCA23752
	D3929818E855A44E78FCE6D39E14383F26ABF4B354B5AC21EDABD1FFEE083FGEEB71761F71B746BCBD7468C340682057E4D475B1E00B932EEC2CE0371410149CA3E007B1358A3BA65FA51ADBA2D7060D15A2EDC4079947FEDF729E306D33C9B7C3D8E9C4C2362C7DFC01F94E4D932DB3AE5116F30EA7BC4BA17275B6256C2851FEB6A85DC1F2B977CDD6DBF63827ACA1B4B6994295254A1CA7B5D54D7C5075885CC57393A1DB2435F7F9D5A7BA55127440A9FFF0709D00F6068463843720659C2B62EC132F701A9
	E068A2C9EE7463348C33CEC69100437CE79114CB5D5CA7FBF7F11F5C5DB7134C7FFECF0A511E9271C0FDCA7DE5669BAD17B51FE4BCC5DCB4667D9E9AB3943CA715625E580139DBDD5D69EE8B04B0A689CFE092237A45D181E9FBE14ED000D91739DFEC7E9E5BF96CD19C224E65F0B0712AF2DF5F0AF66F91AFF70B9032B1481DD4E5D9583A5E2F6D2CA2D7563E2395656A504547D089651AD340F345F9404FE200951719EC4BA5A69B56EF115AEF895493A52DDF1EC75FAF444C7BD981FF95A052437A45776BF73C
	1077CB82FC3E9C580F59C3467718DF98651136EEFBC937EC47E8AD4860886EEED917B3BAB3B2959A65B24F8D272FC679BB209E0273495CEE1E4B6539E17825A812F07F6620F9709C197136C4566C26E8844F8DA7DAF0CEF3F72CE9EFB757EDE42BFEFF2A71F8DCEDF6EAAA0ADECE8AFA3BE238A6E1B5146DF649A3B70063FB398E4F8A48AF6E1D8ADA49A5BEAD48D156DA85734AGB9BDD446A4DF47FB8C6097832C84E0769BD9F0G6382B53292D6B409D23BCB6A922DFBBB059E21C46815CBE88CAA29214DC771
	F99F574D6543A7B08E880E36EE87EC65D6C8A26A459EFB1B88B90945D56B72FEDF63C734B1B855C37B4E725C703E8D558A4FF10FCEA7D5B7B62498656AE356EECBE96ED176569ECD9FD7FFEACADDFE713D8CA61BF7D459AC04BE24174BE60149DFB69550F621171FDF9E36FE81E5A7DFC99DAC729B08209E364021AE0C717293B367458D73C42B3C44FFEA242704DE2E277C427F1B1E966CDB4F6CC924DE42922F3DA8EF465765C24E578A1833584B79FA7730110C2CDE3E57FAB27CF3FF2FD74BB9C23D0179BF4B
	0957E463FCD1DE1693272B78C20A1F2343B37E22D62BBA0436E29F4F63BE30621E908675980085G71GC9GDB7B387F7DC678048C12A7B7C0BC111C0263F43F8DD07139F08B6AG76AC259EE1C1BE681EC5F8BEF02840DF8E95FD7D575E5CE7140BB5840C7B40DF1EFE6318BEED0EADBCA622763DA51EDE58ECAAABCFF0EBD5764C2F3275FBE6D89F4B0167C039900045499B041F2D937AF8BC3CF6CC9F8F672DBD10129DA49E1A2FB07EAA2EE87ED72C1E15970A98633D50968E90833090E0B9C066D55063D52E
	231DED07875391B568E6D79D600E439ED6671FFB28B2DDF50B3A48B58F260B462B7D47B1994F6DB51231957AFA2FF2993F275EDF04DD653E73C440FCB74767138163CA7C6A8B7D78E4BCECB8286AD96521377056F89899B04E1F8773B5A39E4A0EF09ECA211D1D4F4651FD3A836AD7D51CAD8BF1D1D01FFB0D731A1F0D38F828A729386D9437D809CB8C77606ADBBD5F446BF8CE3A96983B34757F7C1667501F17520374E1BE7121B5F87EDFE2B54E7F4B2D4679FF25F59D43838D7DEDGEF2F358EFCBEB8E1B5BA
	9FEC236BF9FA53DACA5B6391BD54C9B199D35ADE13B79203F342DF05AED1114586A295E4A9FD0E4A2E3AB242E35C7F6CC33FB208F13A9CEFAC55BF6AE46BF51E63BD67453D22996A8E00106B5CAEC248C0F7E4115783754DE258A99B6E013321BD8EE0A94E833075860FDD8956C17717235E817617674560FA7E8F51D84F2F08181FFADC5DD21B682F4BD7C5335A26C54986D96CC23133BEFF2063E5C13209813670B69D4F5A020DDFA5863397265303E63DCEBFD17DBF31176B2B6A8657573BB47710219E8AB06B
	8657978948AD74B06FC6203E585C7F5DEF6E832A4F1A8F8EC4EFD1D0FA0F966A69152B748E9E99085E522074E6E6601957A178DC5DB21C110651BDC4BD5B2E04F1F9002BGE89C467692668F0F77629E5CAE89DDA06A53EE310DD48F4731573378589868BF9FA08100C0DB51F01EBF2E789C7545BC272EDB42B0G293A262F0E32757E6DB45765EB1D102DDF6B98209BB9DCDB5F6C6141723C23BE546D02617EF722DFD3FC42F07FBB51173C7CCEB4895ADAFF44770399046F878BFE447D6E9BDBF03F39E8B3769B
	2B4DB8BEF65A860E0F12AD183DB3793C552B17CF7E88B87F0D50E4D2B5A218FCCAAC385E26917E72B9C4710E917E72A11B39FC46C05B7291DCBE8599DCBECDA19C377FA047C52938AF0FF0DCA260984D048C7E7E26FAA464ACA3393D1535E26E2646DFBCE1D4D6FEA1592D35022D11DAAF73693D19194A7859A339CF1F4DC41A4B20CEC6451A4C23F8DE697D934FABE39E1CCE9A071DB4BCE7A7B49FC8A99D45E456BCCA4BAB23A12FE4F34E4A62F306C17B42D1741EBDCBFFD62177EDA9ACAFE16DE92B756D284F
	DDC45FFE30DFBB727BC43F719D7D5AD1C65B1471ECED4F6C61EBCBFAA61AB87E61AFC16F9072C633B515ABE55359DDE4773877B2180E9E20FB1A835A060D66FAABAF246F9C21BED745BD31196BB2FEB40F19A7FDF8BFD4BC866CGC086089C435BDAE85B8228AF9D436EF970EE06353D6C5567B3FC6CC0F9CC312EBDF2103EEC0E373DC33D175567CA035D2FD60CC5197C14548FC20F3F8FC2C803B1E600DD8ABF4570FC3F520749B8470BB29E86787031DC6E27EA90B7976AF1AA6E5FEAD53F9A4BE5DAEEC1F9D7
	0F03B886908290BD0E37555036B828AF9F476E52707E0B3519B68F14DBA22DB58ADD7F36E8BFDC08C19FA5EF9EF08E1E177A5F6D9B6941C807CC97A599C351C54EF8582F46EB3AA89F2F690251BBF6F0B0FAFA289E5FDF374D81742CC706C22F550FBFD9C70F49745581731560FE31629647FD1CB954F1ECEF591B0531F958F83637A42B78B20A8F5561597EF0A1136F8F915016B41E5F3DFCEA473C23B5946288C0B8C08CC09C4032D03EC7E7F61412C1F263E6173B91B6F107EE5BE56BFBA86B565EE9BF194163
	701C1F5DC7660EFAA7B9585DC5D2A8730F2DF730F2D973AE0AD76EAEBAF1FD0D505681908AB08BE0AE4022BB787A1ABA0A597A0EDA6DF20DAC8BED9D749617AF6E2CDD6C5550183431FCEBDEB666009617C4138C16833E205E55AC857AC9G6593E2CD47A7303BC0B66EE4E1E06EE8A96463DAA7305C518BE528B20E597A2FB78F4556E741583993182DE3BD8E1EFF31EA552A27G989F1567701DD91D7E5E173E75E894AC22C349A9DF236B298B3C9B261D94B914EDB16E235EE331FE0F1C5AECB81F5A0D307B4A
	D01FFE0FDE2248A0E9823F4FFCD6B0EA4F1B98EB5ACE67A1B44FE47F92BF8F449132FBA2FE3B130D2921E2EF3F4D617636751199247E25F712F78531370E094AB79493351CE6D9E7D286DB63AEEF20CDD4FA39EE93A7B21B488C0333E7983B6FE0635AEB82ED62DA8D9FD79E46EC22914AD6E59C33095F578EE69369BA1B70425850B04DA6226079477DECA2FFA74A7684552EFAD9CE35445AFFD535B1301DE91B70F348998B72FDA2481DBB6B3372CA01E19FBA15E2875D96FFBD633AE342389D3C12EA54BEBFEC
	28F6B08C0FG44E88CEB3FDF9E385D5C03B5966FCA3234674FF44F164C9F72FD3D71DEF265364E41E26591FB70735AD13B71F96D245D783C7615FD607B2AF376606F33DB6D7A7759AC56AF6BC1FBD88C7ACF84481F8467B7002AC9BC4E3FE15FC9E81CB70871C72DB63CE7412FE8E4BE7FF31D41573A24F370EFEFFE5B096F7FACAED9F6F5A1FD0EFF2D9375A53B5C9449F04BBBD1C6125D56A1332E8A7E4DCEFA0E905BA91A6D650F67706F1FDAE0EDE11378D96F35ED7B48E06FD81B6D32C32CD54213F21F73CE
	8EFF473AF092BF7B8D276F38481D10FF81145E494F19638AF99EF19CF0436E64F7634F9772335C5CBBB98DF64ECD04FA66E4483BA65F2CDFF23F3A022FC679BDE2FB10FBC49A09988F3F7071FB290A491CAF1305635C1379797559EF755FCB66CD5F1142F3A5FF1B0F1E4CE27C1C49DA2CE73676288F636EB2402705031D81D483B483B8G06810483C4834C87D882309C00CC813B84588DD08150B8C54DC1A4545DD921D3492EB095F1C954BA81299C582901CA82C6C81E57D93C5C96DA01D644943E6E073DDC27
	0B26F0FDC4D6E35F4C29208B000A29DC278F54F01DB681EE4CD4CE63759A2E5379D3B90DAD3558F7A5547326413A278DD527632E90556EE2D45DD4CF63F3EECA63B86FB4AE1B441E60FE78471E417D10507BCA917C275967D66EBE53FBF13FF53537FBC4555FD777662938865809BCF63CB3E531751FF2864BE3E7CFB3FA47725420637C5FCF31F16F53F1F15386BE13F3C800FEAC7FFF0B7A6D0AE97E67027719BFC7781F8BC61B396EF3204D9D414F2FE1816F06EEEE8F3A6796473DB464F1ECDDB20DB10BA37C
	57FB0D62938256EB5A4C573B9C5A1A6E626BDD68534FBF67AE659BEB250C3FAB5899BC5B229F172874CB1B4ECA7374E06FA3BE3B0A6AE0F677F36F9E924477E1E7BA81616E1425D3EC131B851BBAA656975CA71E768D6E934F7B0C7726A53E403D69E51F715E74BB1FFEEFE238E5BE0CB9B608D7ADEA3F57D55CB99577A6250938EF6CD639C3452F24FCA17E380893F27C7B142FDE0AEBD1F029FD9A6E1C024B6CC33F06A5F738A4BEB6378F3FD7EB37BB9C2C3E0E7631BB414DD5F9EC6AC39E6DEEC147779695F7
	CE55F707775662C04F20630C6340E3F4DC31019D99C175F49E8772E8CCEF1E6E6F9705945FBA5D5FAF6E2B66FE610036C55339DFBCD2F3EB7E7C4F37B86E4DA10FE36B1AC8772572997E6B0D20786A997E6B5DD14B575B88ED33E77075BE5E4F1F73A3195C77AAE5D5E430B830A5CDBF2EC969E7D5CA2F41B876EEF57E06DCEA0B8E3BD32CE3DFE493FEEF39B1977DAB94464E0A64F714D9E6FAEF897538C84E73FB189A716F1B8B3567BB4C377F4C71605FE00E3E4D5A1AC6681EF3863C7F7CE1665EF3DB67E0BA
	FD054651A5204FE52A1EF794A0AE896AB9F7F35C2882FEBFEDD6F1BBE98E5581F52F0AFB25195A8D54A3D5DC7D99BEB6DA457D126608AB211EF78F476D2EC65CEE28D75C436DEBD30DA6736D3A67686A5BAF7FA77F86F3CC295556F40F6E79435A5BBFF775ED1D0369F4A13511FA50E7332A673296EAB7D0772A3829AD5CC6C2D45C811A0FAF067AD29557C03F21DE89754CFBB96E510BFCEC7E3D7CCE2BB89777F899CF1C47DC744CB26BDEEA0BB42ED961592D76ADB227733E045F4BCC3B063AE06DEF7B689E8E
	E4980D1CBE24318C1ED728B4AA8A300F8C098F9B7F6CF59C4F6E496EAE98681ECC7F8E83412CFB2704ED3B23E2CDEBF57DB6103FD87A73F30A4A38947AD5C5F1FE5EEE6E4F4F1BB7B4FE3EBDB398BF7C5BC9A3FE9CB0C7EEBFFEDE769A4B7AAD6A63DE689F2272F528BAD056FFCE56E47D0A5F5A5E22EB1B8B639728B40E3774DF5B8BA95A5AFE52B294D997695E49507DB9C0568DE97D79791E5AA4198931F1A6672775E2FFFE5ECE5578F96A62D0E46D4FCF55CCCD560C766FD7EB32CAAA7890CA2758DB24FE40
	0B725F3B53EA979CAEDB094565306EACAEAA037F657B143B6628DE7D7DC3456A4F8CFF5F00F0FCA64B9BAEA8653C513B949EA667EABCECAC58B9908FB0FDA91BDE997B0FC8CAF9DE3FCE2373C2735068FC3ADE2333E623C62720FAA8B2E1637FD0200D5F53B2B4BE3695EAFCDC7EC023F37862506804633505F2BE2BA9C29B8B87394F1E496FA1BE4A7EB389FEC753A4395C22A47B98167EEAC3B9C79CABC2012B771C2CC9E97B32886FAC6DCE3B8CBC597B68CF8ACEC3C7270D7DA679E75DDF43C69173BAC977C8
	E42D3A0E86FAF8C9687924B275F7772726103486E2E20739861206A7328612CE8FEC8DA4958EF3DEB751FE0005BFEAAD74DCA6B4BA8641EE51E153874126A9001452D4EC118E1BAE139C650324A27CE2C9A62B21A822F77D15B249468AFBB9G35DC5E133E8F001134D4BB67322A529D04125BCF39CF4D7C7455385F1C752492D383A94418F1DA1428E8C5ABFE52287C9E719849F2539F78G9EA46621C53AC776B944C6125F7EB57E7E46A30B569A2EB4E0E4A37DF140FE3CC75DFFDC70F448F820BE54C8521C78
	4BC9321EB5BB8427EDBF5360A1AA76641AD6AA909AEB005F8D14DF24D222D25970AFEB776CB831F4EEAAC9B9C6D6BBDC0275185086D654C032648EC974F40047F85D1FD7D3C21974BAAB1BCADBA2DB546563E2CB1AA1822ACCDF725E9D1A3BAC779844E62FC476531E98A2CB549E563A8EC10229C429C146DF023A245DEA5B708F64BF7D6B978597D2C83232C8546DDA2FDBA626F6214B6E7041C3BDF5A52A758DD0E507F155D4D198A547E0BD72FF3E7E74FD7FD515389C089DA369743BFBE0A9CF6B75576EAE0B
	A80D0A1932ADBBBCE1BCF26C4665F24AD932A4B8BD78DB89A0DE6437E65E2432DEB2796952A98F3DFB1E322E8D1549F626A4983A5FA63A3A765734374341CBA5G9A83725BD4725827442F4FFA6F240DA7DEDCE5818B2BA729DD252574EF997DFBG7F16D1CC9945149DG35A512817F7937FB791A29D9524AE634A1AFBE953056DCBF43E2F2DA97E0A129085E880AE1A18FEB5BE8576D746F4EA1730623F67B71344F2BFABDC650550A67E77E7DF1DA2AF11A25F09ACFF85C7367B4B98827E91C53FA15D3B37E59
	FAFBF87E6C6FD063077F5FE83CBE084679FB7FFDB77439584A11662039D87CC3AC87CBFC487FDD502F68793D7861D8537107F92ED0607B18843F53BD5DA5B89C326038D46334627DEE2DCF9635771EF1BE246F61FD14C65676823DB76D540D79DFD0CB8788422FB8085F96GG98C5GGD0CB818294G94G88G88G6FF854AC422FB8085F96GG98C5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG9997GGGG
**end of data**/
}
}
