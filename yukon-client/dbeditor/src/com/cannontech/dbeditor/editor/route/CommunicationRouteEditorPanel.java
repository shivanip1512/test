package com.cannontech.dbeditor.editor.route;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.database.data.pao.PAOGroups;

public class CommunicationRouteEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ItemListener, javax.swing.event.CaretListener, com.klg.jclass.util.value.JCValueListener
{
	private com.cannontech.database.data.route.RouteBase objectToEdit = null;
	private javax.swing.JCheckBox ivjDefaultRouteCheckBox = null;
	private javax.swing.JLabel ivjBusNumberLabel = null;
	private com.klg.jclass.field.JCSpinField ivjBusNumberSpinner = null;
	private javax.swing.JLabel ivjRouteNameLabel = null;
	private javax.swing.JTextField ivjRouteNameTextField = null;
	private javax.swing.JComboBox ivjSignalTransmitterComboBox = null;
	private javax.swing.JLabel ivjSignalTransmitterLabel = null;
	private javax.swing.JPanel ivjConfigurationPanel = null;
	private javax.swing.JPanel ivjIdentificationPanel = null;
	private javax.swing.JLabel ivjJLabelPrtNum = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CommunicationRouteEditorPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getRouteNameTextField()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (RouteNameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> CommunicationRouteEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC2:  (SignalTransmitterComboBox.item.itemStateChanged(java.awt.event.ItemEvent) --> CommunicationRouteEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ItemEvent arg1) {
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
 * connEtoC3:  (DefaultRouteCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> CommunicationRouteEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ItemEvent arg1) {
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
 * Return the BussNumberLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getBusNumberLabel() {
	if (ivjBusNumberLabel == null) {
		try {
			ivjBusNumberLabel = new javax.swing.JLabel();
			ivjBusNumberLabel.setName("BusNumberLabel");
			ivjBusNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjBusNumberLabel.setText("Bus Number:");
			ivjBusNumberLabel.setVisible(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBusNumberLabel;
}
/**
 * Return the BusNumberSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getBusNumberSpinner() {
	if (ivjBusNumberSpinner == null) {
		try {
			ivjBusNumberSpinner = new com.klg.jclass.field.JCSpinField();
			ivjBusNumberSpinner.setName("BusNumberSpinner");
			ivjBusNumberSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjBusNumberSpinner.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjBusNumberSpinner.setBackground(java.awt.Color.white);
			ivjBusNumberSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			ivjBusNumberSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1), new Integer(8), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBusNumberSpinner;
}
/**
 * Return the ConfigurationPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getConfigurationPanel() {
	if (ivjConfigurationPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Settings");
			ivjConfigurationPanel = new javax.swing.JPanel();
			ivjConfigurationPanel.setName("ConfigurationPanel");
			ivjConfigurationPanel.setBorder(ivjLocalBorder1);
			ivjConfigurationPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsDefaultRouteCheckBox = new java.awt.GridBagConstraints();
			constraintsDefaultRouteCheckBox.gridx = 1; constraintsDefaultRouteCheckBox.gridy = 1;
			constraintsDefaultRouteCheckBox.gridwidth = 2;
			constraintsDefaultRouteCheckBox.ipadx = 7;
			constraintsDefaultRouteCheckBox.insets = new java.awt.Insets(4, 12, 3, 36);
			getConfigurationPanel().add(getDefaultRouteCheckBox(), constraintsDefaultRouteCheckBox);

			java.awt.GridBagConstraints constraintsBusNumberLabel = new java.awt.GridBagConstraints();
			constraintsBusNumberLabel.gridx = 1; constraintsBusNumberLabel.gridy = 2;
			constraintsBusNumberLabel.insets = new java.awt.Insets(6, 12, 16, 4);
			getConfigurationPanel().add(getBusNumberLabel(), constraintsBusNumberLabel);

			java.awt.GridBagConstraints constraintsBusNumberSpinner = new java.awt.GridBagConstraints();
			constraintsBusNumberSpinner.gridx = 2; constraintsBusNumberSpinner.gridy = 2;
			constraintsBusNumberSpinner.ipadx = 14;
			constraintsBusNumberSpinner.insets = new java.awt.Insets(4, 5, 15, 1);
			getConfigurationPanel().add(getBusNumberSpinner(), constraintsBusNumberSpinner);

			java.awt.GridBagConstraints constraintsJLabelPrtNum = new java.awt.GridBagConstraints();
			constraintsJLabelPrtNum.gridx = 3; constraintsJLabelPrtNum.gridy = 2;
			constraintsJLabelPrtNum.ipadx = 17;
			constraintsJLabelPrtNum.insets = new java.awt.Insets(8, 2, 19, 94);
			getConfigurationPanel().add(getJLabelPrtNum(), constraintsJLabelPrtNum);
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
 * Return the DefaultRouteCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getDefaultRouteCheckBox() {
	if (ivjDefaultRouteCheckBox == null) {
		try {
			ivjDefaultRouteCheckBox = new javax.swing.JCheckBox();
			ivjDefaultRouteCheckBox.setName("DefaultRouteCheckBox");
			ivjDefaultRouteCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDefaultRouteCheckBox.setText("Default Route");
			ivjDefaultRouteCheckBox.setVisible(true);
			ivjDefaultRouteCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultRouteCheckBox;
}
/**
 * Return the IdentificationPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getIdentificationPanel() {
	if (ivjIdentificationPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Identification");
			ivjIdentificationPanel = new javax.swing.JPanel();
			ivjIdentificationPanel.setName("IdentificationPanel");
			ivjIdentificationPanel.setBorder(ivjLocalBorder);
			ivjIdentificationPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsRouteNameLabel = new java.awt.GridBagConstraints();
			constraintsRouteNameLabel.gridx = 1; constraintsRouteNameLabel.gridy = 1;
			constraintsRouteNameLabel.ipadx = 44;
			constraintsRouteNameLabel.ipady = 4;
			constraintsRouteNameLabel.insets = new java.awt.Insets(4, 14, 4, 2);
			getIdentificationPanel().add(getRouteNameLabel(), constraintsRouteNameLabel);

			java.awt.GridBagConstraints constraintsRouteNameTextField = new java.awt.GridBagConstraints();
			constraintsRouteNameTextField.gridx = 2; constraintsRouteNameTextField.gridy = 1;
			constraintsRouteNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsRouteNameTextField.weightx = 1.0;
			constraintsRouteNameTextField.ipadx = 83;
			constraintsRouteNameTextField.insets = new java.awt.Insets(4, 3, 4, 14);
			getIdentificationPanel().add(getRouteNameTextField(), constraintsRouteNameTextField);

			java.awt.GridBagConstraints constraintsSignalTransmitterLabel = new java.awt.GridBagConstraints();
			constraintsSignalTransmitterLabel.gridx = 1; constraintsSignalTransmitterLabel.gridy = 2;
			constraintsSignalTransmitterLabel.ipadx = 11;
			constraintsSignalTransmitterLabel.ipady = 4;
			constraintsSignalTransmitterLabel.insets = new java.awt.Insets(7, 14, 17, 2);
			getIdentificationPanel().add(getSignalTransmitterLabel(), constraintsSignalTransmitterLabel);

			java.awt.GridBagConstraints constraintsSignalTransmitterComboBox = new java.awt.GridBagConstraints();
			constraintsSignalTransmitterComboBox.gridx = 2; constraintsSignalTransmitterComboBox.gridy = 2;
			constraintsSignalTransmitterComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSignalTransmitterComboBox.weightx = 1.0;
			constraintsSignalTransmitterComboBox.ipadx = 65;
			constraintsSignalTransmitterComboBox.insets = new java.awt.Insets(4, 3, 13, 14);
			getIdentificationPanel().add(getSignalTransmitterComboBox(), constraintsSignalTransmitterComboBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjIdentificationPanel;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPrtNum() {
	if (ivjJLabelPrtNum == null) {
		try {
			ivjJLabelPrtNum = new javax.swing.JLabel();
			ivjJLabelPrtNum.setName("JLabelPrtNum");
			ivjJLabelPrtNum.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelPrtNum.setText("(CCU Port #1 - 8)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPrtNum;
}
/**
 * Return the RouteNameLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRouteNameLabel() {
	if (ivjRouteNameLabel == null) {
		try {
			ivjRouteNameLabel = new javax.swing.JLabel();
			ivjRouteNameLabel.setName("RouteNameLabel");
			ivjRouteNameLabel.setText("Route Name:");
			ivjRouteNameLabel.setMaximumSize(new java.awt.Dimension(81, 16));
			ivjRouteNameLabel.setPreferredSize(new java.awt.Dimension(81, 16));
			ivjRouteNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRouteNameLabel.setMinimumSize(new java.awt.Dimension(81, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRouteNameLabel;
}
/**
 * Return the RouteNameTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getRouteNameTextField() {
	if (ivjRouteNameTextField == null) {
		try {
			ivjRouteNameTextField = new javax.swing.JTextField();
			ivjRouteNameTextField.setName("RouteNameTextField");
			ivjRouteNameTextField.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjRouteNameTextField.setColumns(15);
			ivjRouteNameTextField.setPreferredSize(new java.awt.Dimension(132, 20));
			ivjRouteNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjRouteNameTextField.setMinimumSize(new java.awt.Dimension(132, 20));
			// user code begin {1}
			ivjRouteNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_ROUTE_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRouteNameTextField;
}
/**
 * Return the SignalTransmitterComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getSignalTransmitterComboBox() {
	if (ivjSignalTransmitterComboBox == null) {
		try {
			ivjSignalTransmitterComboBox = new javax.swing.JComboBox();
			ivjSignalTransmitterComboBox.setName("SignalTransmitterComboBox");
			ivjSignalTransmitterComboBox.setPreferredSize(new java.awt.Dimension(170, 27));
			ivjSignalTransmitterComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSignalTransmitterComboBox.setMinimumSize(new java.awt.Dimension(150, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSignalTransmitterComboBox;
}
/**
 * Return the SignalTransmitterLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSignalTransmitterLabel() {
	if (ivjSignalTransmitterLabel == null) {
		try {
			ivjSignalTransmitterLabel = new javax.swing.JLabel();
			ivjSignalTransmitterLabel.setName("SignalTransmitterLabel");
			ivjSignalTransmitterLabel.setText("Signal Transmitter:");
			ivjSignalTransmitterLabel.setMaximumSize(new java.awt.Dimension(114, 16));
			ivjSignalTransmitterLabel.setPreferredSize(new java.awt.Dimension(114, 16));
			ivjSignalTransmitterLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSignalTransmitterLabel.setMinimumSize(new java.awt.Dimension(114, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSignalTransmitterLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {
	String routeName = getRouteNameTextField().getText();
	Integer injectorID = new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)getSignalTransmitterComboBox().getSelectedItem()).getYukonID());

	if( routeName != null )
		((com.cannontech.database.data.route.RouteBase) val).setRouteName(routeName);

	if( injectorID != null )
		((com.cannontech.database.data.route.RouteBase) val).setDeviceID(injectorID);

	if( getDefaultRouteCheckBox().isSelected() )
		((com.cannontech.database.data.route.RouteBase)val).setDefaultRoute(com.cannontech.common.util.CtiUtilities.getTrueCharacter().toString());
	else
		((com.cannontech.database.data.route.RouteBase)val).setDefaultRoute(com.cannontech.common.util.CtiUtilities.getFalseCharacter().toString());

	if( val instanceof com.cannontech.database.data.route.CCURoute )
	{
		com.cannontech.database.data.route.CCURoute ccuRoute = (com.cannontech.database.data.route.CCURoute)val;
		
		Object spinVal = getBusNumberSpinner().getValue();
		if ( spinVal instanceof Long )
			ccuRoute.getCarrierRoute().setBusNumber( new Integer(((Long)spinVal).intValue()) );
		else if ( spinVal instanceof Integer )
			ccuRoute.getCarrierRoute().setBusNumber( new Integer(((Integer)spinVal).intValue()) );
	}

	return val;
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	getBusNumberSpinner().addValueListener(this);
	
	// user code end
	getRouteNameTextField().addCaretListener(this);
	getSignalTransmitterComboBox().addItemListener(this);
	getDefaultRouteCheckBox().addItemListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CommunicationRouteEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(398, 387);

		java.awt.GridBagConstraints constraintsIdentificationPanel = new java.awt.GridBagConstraints();
		constraintsIdentificationPanel.gridx = 0; constraintsIdentificationPanel.gridy = 0;
		constraintsIdentificationPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsIdentificationPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsIdentificationPanel.weightx = 1.0;
		constraintsIdentificationPanel.weighty = 1.0;
		constraintsIdentificationPanel.ipadx = -10;
		constraintsIdentificationPanel.ipady = -9;
		constraintsIdentificationPanel.insets = new java.awt.Insets(23, 13, 25, 12);
		add(getIdentificationPanel(), constraintsIdentificationPanel);

		java.awt.GridBagConstraints constraintsConfigurationPanel = new java.awt.GridBagConstraints();
		constraintsConfigurationPanel.gridx = 0; constraintsConfigurationPanel.gridy = 1;
		constraintsConfigurationPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsConfigurationPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsConfigurationPanel.weightx = 1.0;
		constraintsConfigurationPanel.weighty = 1.0;
		constraintsConfigurationPanel.ipadx = -10;
		constraintsConfigurationPanel.ipady = -13;
		constraintsConfigurationPanel.insets = new java.awt.Insets(6, 14, 25, 11);
		add(getConfigurationPanel(), constraintsConfigurationPanel);
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
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getSignalTransmitterComboBox()) 
		connEtoC2(e);
	if (e.getSource() == getDefaultRouteCheckBox()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}
/**
 * This method was created in VisualAge.
 * @param routeType int
 */
private void loadSignalTransmitterComboBox(String routeType) {

	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List devices = cache.getAllDevices();
		if( getSignalTransmitterComboBox().getModel().getSize() > 0 )
			getSignalTransmitterComboBox().removeAllItems();

		if( routeType.equalsIgnoreCase(com.cannontech.database.data.pao.RouteTypes.STRING_CCU) )
		{
			for(int i=0;i<devices.size();i++)
			{
				int type = ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i)).getType();

				if( com.cannontech.database.data.device.DeviceTypesFuncs.isCCU(type)  )
					getSignalTransmitterComboBox().addItem( devices.get(i) );
			}  	//repeaters are not actually signal transmitters, so they should not be in the ComboBox
				//|| com.cannontech.database.data.device.DeviceTypesFuncs.isRepeater(type)
		}
		else if( routeType.equalsIgnoreCase(com.cannontech.database.data.pao.RouteTypes.STRING_TCU) )
		{
			for(int i=0;i<devices.size();i++)
			{
				int type = ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i)).getType();

				if( com.cannontech.database.data.device.DeviceTypesFuncs.isTCU(type) )
					getSignalTransmitterComboBox().addItem( devices.get(i) );
			}
		}
		else if( routeType.equalsIgnoreCase(com.cannontech.database.data.pao.RouteTypes.STRING_LCU) )
		{
			for(int i=0;i<devices.size();i++)
			{
				if( com.cannontech.database.data.device.DeviceTypesFuncs.isLCU( ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i)).getType() ) )
					getSignalTransmitterComboBox().addItem( devices.get(i) );
			}
		}
		else if( routeType.equalsIgnoreCase(com.cannontech.database.data.pao.RouteTypes.STRING_TAP_PAGING) )
		{
			for(int i=0;i<devices.size();i++)
			{
				if( ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i)).getType() == PAOGroups.TAPTERMINAL )
					getSignalTransmitterComboBox().addItem( devices.get(i) );
			}
		}
		else if( routeType.equalsIgnoreCase(com.cannontech.database.data.pao.RouteTypes.STRING_WCTP_TERMINAL_ROUTE) )
		{
			for(int i=0;i<devices.size();i++)
			{
				if( ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i)).getType() == PAOGroups.WCTP_TERMINAL)
					getSignalTransmitterComboBox().addItem( devices.get(i) );
			}
		}
		else if( routeType.equalsIgnoreCase(com.cannontech.database.data.pao.RouteTypes.STRING_VERSACOM) )
		{
			for(int i=0;i<devices.size();i++)
			{
				int type = ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i)).getType();
				if( com.cannontech.database.data.device.DeviceTypesFuncs.isCCU(type)
					 || com.cannontech.database.data.device.DeviceTypesFuncs.isTCU(type)
					 || com.cannontech.database.data.device.DeviceTypesFuncs.isLCU(type) )
					getSignalTransmitterComboBox().addItem( devices.get(i) );
			}
		}
		else if( routeType.equalsIgnoreCase(com.cannontech.database.data.pao.RouteTypes.STRING_SERIES_5_LMI_ROUTE) )
		{
			for(int i=0;i<devices.size();i++)
			{
				if( ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i)).getType() == PAOGroups.SERIES_5_LMI)
					getSignalTransmitterComboBox().addItem( devices.get(i) );
			}
		}
		else if( routeType.equalsIgnoreCase(com.cannontech.database.data.pao.RouteTypes.STRING_RTC_ROUTE) )
		{
			for(int i=0;i<devices.size();i++)
			{
				if( ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i)).getType() == PAOGroups.RTC)
					getSignalTransmitterComboBox().addItem( devices.get(i) );
			}
		}

	}


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
		CommunicationRouteEditorPanel aCommunicationRouteEditorPanel;
		aCommunicationRouteEditorPanel = new CommunicationRouteEditorPanel();
		frame.add("Center", aCommunicationRouteEditorPanel);
		frame.setSize(aCommunicationRouteEditorPanel.getSize());
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
public void setValue(Object val) {
	getBusNumberSpinner().setVisible(false);

	//Assuming val is a good CommunicationRouteBase
	com.cannontech.database.data.route.RouteBase rb = (com.cannontech.database.data.route.RouteBase)val;
	this.objectToEdit = rb;
	
	String routeName = rb.getRouteName();
	int injectorID = rb.getDeviceID().intValue();

	if( routeName != null )
		getRouteNameTextField().setText(routeName);

	loadSignalTransmitterComboBox( rb.getPAOType() );

	for( int i = 0; i < getSignalTransmitterComboBox().getItemCount(); i++ )
	{
		if( ((com.cannontech.database.data.lite.LiteYukonPAObject) getSignalTransmitterComboBox().getItemAt(i)).getYukonID() == injectorID )
		{
			getSignalTransmitterComboBox().setSelectedIndex(i);
			break;
		}
	}

	/*If it is a route that can not have a transmitter device for it, then do not show those values */
	if ( val instanceof com.cannontech.database.data.route.MacroRoute )
	{
		getDefaultRouteCheckBox().setVisible(false);
	}
	else
	{
		getDefaultRouteCheckBox().setVisible(true);
		if( rb.getDefaultRoute() != null )
			com.cannontech.common.util.CtiUtilities.setCheckBoxState(getDefaultRouteCheckBox(),
				new Character(rb.getDefaultRoute().charAt(0)) );
		else
			getDefaultRouteCheckBox().setSelected(true);

		if( val instanceof com.cannontech.database.data.route.CCURoute )
		{
			com.cannontech.database.data.route.CCURoute ccuRoute = (com.cannontech.database.data.route.CCURoute)val;
			if( ccuRoute.getDefaultRoute() != null )
				com.cannontech.common.util.CtiUtilities.setCheckBoxState(getDefaultRouteCheckBox(),
						new Character(ccuRoute.getDefaultRoute().charAt(0)) );
			else
				getDefaultRouteCheckBox().setSelected(true);

			//set all our fields visible that are needed	
			getBusNumberLabel().setVisible(true);
			getBusNumberSpinner().setVisible(true);
				
			if( ccuRoute.getCarrierRoute().getBusNumber() != null )
				getBusNumberSpinner().setValue(ccuRoute.getCarrierRoute().getBusNumber());
			else
				getBusNumberSpinner().setValue(new Integer(1));
		}
		else
		{
			getBusNumberLabel().setVisible(false);
			getBusNumberSpinner().setVisible(false);
			getJLabelPrtNum().setVisible(false);
		}
	}

}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	if (arg1.getSource() == getBusNumberSpinner()) 
		fireInputUpdate();
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) 
{
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GFAF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DD8D447355890E323B15A1004A8094428A1A92F21AF561A463E0FD25F2BE94DD71B4F3C5854B63ED7D3CD1FF94FFE212D29F9F5D8FED5D47CC944080A8A8289C1C07C89D265E7A52228C4D0B172A5AE6C85D6163D6B5D3B30A862BBE7664E5C3B4BAEBF7659AFFC5F615EB9B3E7661CB9BFF36E4CG5932ABBCB14E34051038C4227D9C0EA32471F3C262631FB84AF1C1D2CC92B17C6C8658C82EEC
	CD0067B82C1B911314C6C67725C0F98A1455E7E31236427BE6B22EEF3699DE820C9386F5CFD52C4C9CF81C288F79B849341C65C90567FA00884061B3D642714BBC699AFE2E066790E7A2E4D1AB3639644950F0C720DC87908690554A5A4F0167EABA723D25C734F6CD6343C96CCFF3CC6B309FC31F9CF25B98ED2D764CA44F45453661D8C114CF24890672ACG94BE1E6C6FB88E4F323A0E36E3ADF2E7C10B4561109D2A5452DEA0D9ED2A2C94A832DB15CACB2B644ECE374356E2D1ED3263BC62CE507ABA0BC332
	93E7BB0EB14713C27B84F9207C9E45590E61385361FDAE40E201AFBC0E78F2DBCCD2A3005346E57DDBCE19A6CB634D67C853EB0E788D81E49DE5E3B2C659F4D9C35CFFC95EC67532FCBB4E7FB228CB3B96131485F092209DE09440F6EA8FFF7E70A3F8D6B42BA81C6C28F0BAAB6DD6650CEC156C70DEDA8AB59A6E3C54A2ABD6C2184EC7FA727D70B390B03A10E15331B909B840759FF9FE9AD9787CE9D3321FB9D998D1E88A3F4666E266B5FDCE5858AF879CFB41B5EF7B749E7B92B10EDDBBD20ECF74B37674D7
	73CC4B34FEC807BEF6E1876A7A575486B261BDCE338F06DFC2712A814FECD526F8AC87C35D528EEE9B0D6BB9AF358993C8DAD12D5FF9880D29B0555A998FD63BAFAFD3285D0D3333B11385AFD1949FE140B3DE8ED1BC162321AE2BB3A60959573BBB50F7C7C1F98A40AC0079G31GE98EE86760B6D63EFD7F90ED2C4E222815EE1B5DAAA904513E3B7D83F8CA9ED53154B8DC2A4551A2DD34A8B6CB33DDF2912D0DA983F9EB7622CB4EC05BEF01B1BE139C12C2DD96FA37F4CA2A2438D81B340C0D58C611E8ED03
	2DD30281BA1D04771B1E31991EF60BCB3D6034DAD4C9EF416AB3B3D0A7DD160B50A3F4G6FECDE766D4279B2037CCDGF6B18F93E9FF27A58542C5E9E92DACF7381D8DA889196540F9DECCE3C7903C07BA789CFF1209B8A247A4254B3C1FD7B2B6997AB15BDA10FD0B5243FD6C3747B0BE674B5CE67E3B115B0CF95DD4127F6AA93FB6C3630D4CEC45A97B7A4E5B34CF640B155D479A0761437932FF7F9AB8D60C9D56987143E83BDE68EA62EE54D5A870B79DE03E1863F5BBB2194DD6DADC923752F38A9818E455
	E71C71796CF13E2EA08F09027F99943F8C7A5C620CC9AAF37239ABBB5E6853FEB8FC8FED6EAE50B10C7C7CFF0E4166A8FEA72ECBD21764D04D12D3F2D8614525552D5909362779ED2BACE852A85E89FEDE850E6592788FF622AFDAD4D531B54342DAAF292A4D5126576F24E3395C4DCEBDC4703132F7220CF2B77839E87F51CE74119659611028328409D61FCFFDD432CB1D40F4156CF6288CDF3C8B6D3CC36A51E24421DD18GF5D96CEE0CB7ACDF08322CC59994DBA718G06ACB247097E3BD264BBB46E828E73
	1EB0F8C6B939AFF66F653E18F563C9927DD536495FDA92BA55ECCA3B4EFCA66B3A6E0B9A0F197EF9BFC2E30506F7B1DEE3576C006740198B770579FBE85C02719A811C5739AFCC4F44FA9EEFB4A728126DF6B635E24E1F5C1D06BA122E3BA588539858CE0BF90C583D49276E22B6272CBEEAB74E295D9093B3941C53370E737CF09470938E90FD1D4D6BFB5A3C30F611653CDDBB1403G4295FF6D648A5EEEA114E38136383C5B31F9CB7E8AF385FFB9E11B5BD660D6ED98F9ADAA9837532D52E9E4791F54132275
	D16762F95E78DE1C57D1D08E8718696236700BF5BC4F8B6988A70B1E7AB0E0CEBB4F256555AE5D96B26F61F95E4EAC9C33C40DC9B22BDCE79F6FB12EB595AE175CE2235A57667B75BDC6DBE138B7762C01E72BCDF129B5604C9E2E1BB777A03F388EEAE8ADAF58D41E2A495A88630E8348F5A37ECC5796836FF3G3F9CE08B5445537EFA4AB19FE831A812DAC0FF3365B5771A254B62A9F0F50363975028D2D00535A770B5CF534B6C3CCD0CC7094E3CC619F71167EA13B717EB634E7D4E84D2F6FB3349DF6EBCEA
	438E13534D66B2542D4FE96FBFA745AF7BF2E5B2136FD5A15FE2E8135145657B2117FC8DDDCC3E5EAEAEDFD9854AE7D3254E827CD52F820BD56D96C71BE425B296D83AD5CD409A285764E3E306D670B147C1FFF3441867FA0CE312EE408354F673B1D75D5118EC2D3856631B97DF6D5657EB353B7F5A4D724544DB542E3BD93E18A870E9949FE640B3599E6D6532C5C15D968F4FF17EA99B6326874AA1G11G33819683A474F0FB6E5CFB008C12C35600FFAA8E0B3D5EEB1150ECF8EF964E9344F52D1C1305FC50
	38CE781A19DE6EBB8F19BD5E32177914995D3EFEF4853E7369C347ECB9BBB7034798036917161EEA00F5335AA6592DECDDB9F94B382EF8FAD8FE98864FA8G96F79E6CC2FF4678B127074717DB554678B2EBC331E9ED007812FFC37BE63E217BC23E780E3C1403FC0E013A8800E80005G64A67884C079CD2E236FE67DEDB09DD13BEB102BGF792E2FD156F7AAC6674D7F7280B37865745CDFF3AE833EF772F8B1025D92E14BD5A77FDAC1DE71CDF4FCD1E4FBDD1F1E73CC676237365B54AAF2F47AE866235DDEA
	69C0DE195DCC6E72E7B7F3204F05B7395D0C246B955AC5AC6058F748880A4B6F855D76F25C17B791E705F2EFAF17FB6DAD4405C3B9DA60BE20B80C07981F986E715E4172F86CE3301C13C3F42F3F391CB168984379BE704D4919FD4F488E1C6F7EB05BFF3E7B63EC7F796ECB599B999E7AB0FE75727ADFE48F1C8F7FB25BDFBE3C034A7952879BE85FAE1764228E07090AD637C4FD1F78490B4F420731B6C71B49397255DA1B932ADCD5A46C6E1D3CF47FF6E748FF02287ED1A70BFAF97E13CD570374DBB1C905G
	A7EFF13B88699AC8E7D7EFF96BD9554A23609966D397856559BEB8963BFE130B6B6682280B85C86D0BC94A82B0777118A56F9BF4DDB96199E0DD3164843609769C7FB6614841B86D125DCA0B642BF7F7CE3A285323A3AE5928A256260F522B9625CD6AE7B777D2FAD69708FEECCEA05BE27A5FD4E154FF317031F73BF83C32427CF97A3C67FB8C1443FDF02C3FD73D7A4379E63672FA97371569FD5CD6C2689E6DB2A8A75C067565B6371551DEB9CA49EDB64EC96D5984CFA7C070EDFDFCB6F6E88FFE3F59ADBD70
	51C3C8C2900E71DC8F0F0B2150BE8AE08140325BACBEB2DB7FEEB74644D6455289D3D36F14DA48A2CA6B3E49E9EB21FC95A088A09CA0BA0867E23B56E16EDD0326E0EB55BE3A5817962B3F3D9EF3D850CE2B2D4D2D982A35F93E7716D1565C91B1C9E5A3F4396AC6844A479E23FEE69D41722E5582BF0D62038CF8160FFDCC71D88E03BA728D9E6BE7D03DD8215CABF031A5089B8765A982773703081B8965798277A225DD8A659582770D8BFC6DC83DC75052F6CDD0D66F613E783B5C403E74F62EFFDFFAA757FF
	FCFDB7F760789A1797488798FFE95D485FA860ED0A60F98D13974AF384EEE4BA17ED21402D206D7203A14E84F37BFD234B2853C6405B03F5BD86858752E9CA8F6AA8A458D7276FD3FCD4302FCEA7F838CE67C2DD613D1C2749F4CD8F017294017B65F9AA8F1467885C31FAAE4FFC017B9702382C11B1C9C7C6F2DCF29C62EA216C84E0E33F56A34ED7305DC86ECBC5D73D72DC7C94A9AD7DCC31D9ABADED3554B3D9BC58F5A3C5230F9D496341199B58E77EFDB017GCD77715C56330F67361153A600AF1E767B6D
	3C282158245E27FD675D27673691105B32B1A75E64E34E077A54D108FB692671DB8BFDB6B91E65E62CFE25D7BD5AD19EB156FF52EB2CC7FEF7F851FF65D51FE820E7327DAC1B4B765E4FA2087DFB1F865C97E89845E472E84F3A0BFBC9567DAB13190E4268DA9C85F533C7F13DDD4B257E896577846E2DBC2E734CEFF2FB1DF48B77F21CD09E839089B0CF54C5523A955FE47BB16977337D96D6D7F4AB501A0E740156FA27D6B7A6C01BC88D4F46B895F00C558170467CAF50FEEB32215DE67203BE0C7F1D960F2D
	535DD9EF3BA1914F7DB8D7EF5038F5845E4D7773F83F76A0523648F6F7274325355BC3E340AE00D922DD66F95423D311DAA5C5112C34D72235EF256D67427B8251FE6AB65C0BEC159DAAEB33180E1DB09A5E81BC23E94C3FE09EC066C4CD9E8EE620E984F8DF2B33437B53C45F0B5ECC5AF2285CB6070F5C2DF46CE8E8BFF7B467AF27BB30BC65B4FE2E003689E3F87B5D873D65B9CCE5AE047A32B1DC1E4753871347A813FD0C37BCC1D03E47CB1E8D077C4B13F388479E836DC347F0BF78D317709701EB2E627E
	32D460161845FA76G377B75BD6893CDD06E85888518AE6A3653BA5CEB1C8B38E5G29E3F9DD38E7B8E79C58EF135677C06DFA9FB07A4B8B439AA3D91B3B01FC4968C7EC1EDF6871566D4F59B7A448F9E4AC577F727358C605C53CFDAC5EABE8ADA4E8AFD46F976A8752BB57B757FD23410ED15711509F5F6753F2CEAAC7174DE5433DE44653B2424F66E78A1EF6466179E23B2C58EEG6F96BBEE3D5449AE9B5B58E6F41F4671BD5DB9C2EF3F6E37E7B614F95DC87E6B8E6932EE8C170E65893BEE627A99BB1665
	89F1821FCB7164C19D4F56707A9BFC8DCF05BA6703FC0F2E2688735E79D0DE8E10B28E568A00F20026F1BCFFFBB48F95BA6037D4036C2C05844FEEC849B4BF38F9E77782FE144C574A69BF18CCE60EFCB7AE509E17F39C7309F15AF3E943DE0AE772ADACC6792221EEA1G998F6B9AC0BE00F9BC176F0D3CFCA69F9E1ED4282A25251D6E1EF361AE5A24EE9D0D9F8D4C461E3F055FA04D32C293D0768D72D3312757887DBB81A2GE60DE7FB490CEEC4EE7FEF1750DCCEB7FFBC7BF6D986CF72ADC647FC09FCB410
	AFF1FDF31FCA875AFC003DDA399C5E5FDE35EA557FGE8BEA2A39DBDB6420F48DACBB3FE2B303AF732B6996B444EA52FD772513F6AEDED70815D20D89C2ECE1B8A1F53DEFD7C7968B6FFED444E9BFC8351BD70C5370CF9D32DA63373DBFC0FFCFD313F7AE0F832F8B3330490BE661212FD70F4C85D19E2F75CEC833561C1FEEEF1D1496112A3878E159C4E4FACAE4C6336965EED3C03F4E44DE13FE7F7B4CEFF0B59D824761CF55FDEED5CCF8F1A60D9F490E28E65E94FA11D27282EDD01F9AABAD04C18E1F44AD1
	1D4EFDA15B172E9A02D96E9E974C25CBD2EC2DFB72B38B73B30BCAB04F5CC5E9880D056CE779717EB84EDFA43A17A67FFF24F2DF4B7876C4E2FE6D5D383D8164DF2C49DD72101EF3AF3D76DEB26B7303E3BA6FFFA89A3A4C1F9E5769D6158CC4F79824ADBE4C695E2C5069EE246F71BFD7D61B45AE3799A64A5FBC317ED22A747969B06BFD5FBB207E8EEABCB1BF5E554D7D587E90737FE0F806BE4476CDD91B51D53CCD54C34C576740F3215606797ADF4D03793AB17F0B855A14905D57B3C2182FAF913E3E7192
	7A10D9EA35386DAAF5EB3E294C6A9FC846FEAB5D2E336E4EE69FBFBED71DE62C2BF75A9C8EC9E1F5CD0D382E1C226D6B9495DAB01F6D0C7375D914ADB7047BF42CD7FDD9882BF70A7AF38D7E6803C3384F3FF7D2574F4B67072AFBE66FBF3FE2747734893BCDBCF676737790E667D3C2F4FBE7634B2774716363E87E2338A5D62E0BC3FBB11EFA56636FAA19DEF4D2DD8535A72A9B3475BF7BA676435E8B8C6F17EF8CA7379BAE30B1143BBA86DBDB4F97855E7B39DC64FF6F672BA27FFBBFD25140FB671DC5016F
	1228C5463BA4ACB7B8FE1A6EA103EEE381529F067592207CE11E97180A512E7096DB7F1C600435CD628A9567694501E5DDD6BC78FD37BF9663B9FD332C2AF2A7764F71AB0BD1DF2A6C24C806A387F00E94DBDB3B4A1AEA78248398576CD2ABC5335CAF220F5FB9EC8259C29E667BB7CBF6603AB170DD08861BEA17AA3594C75B5FDD5E477366398F737D1C6F676098890F44A4E5G14BC42770E22F3F95ED98B3860C77879D4CA9E5F1F1975886F63D09E4DE9211C7AE8CCD296405056B49E3FDF29A5C41CE93486
	B85320110A7130350F1F2D16BD4A79BAD14CF1610F729C7976997AFD9E8A39F1A8370D7FAD79981E97AD5734EC185D3B21568148BAC5F6CA0A5A433074C60CE6F3AF1E4538DE8E7DB48184810482C4824C86D8G308C00BC867688D088E086E884308374820C8318729867638FE5F9837021ED46D1C3D5ACB6B4D56DFB6033EEEE93B3212F650F097366EE2E5B5C89DCAF4FD5E25B2BD076G0C1B40F57BFC9557ED846096CD60FD2C2C623ACD1F487B48B3E35BBAA8378384CD9C2EEEA75791E1FFD36278D9ED68
	C4EE4BFD077699ED39435ED6F02D8523C7C1AB262DF8080A6B16164332FEBEB671B352F9934539E983BF3BC889637ABF77B9917ED5DE139DE83EA1AA1C82D3BB57FCCDEAD19BACED026633530163425953034705CB27691D9E7067069E27F65653F49A3FA16456D617A4620F65F41A4055387857AE0B755F699B6CBB2CBC4C5F796B2CC169F67A14995D9FA9DDD358407B99629B8E5A316F570FE93C888E737D2EAD2178F1E13E5F35BF4C61B69886F564F17E9D7FEF39F776BDBE7F8E6956655D59F77C33B466D9
	9F771577F90AF7BE6EABEFD99E1737976A9689F9B37A7C2D6566A7587CB7BE61DF8FF64087795405C0B96289FF36F02A585F9833343673820C319870ABFC6AD2A7C1AC1D640DE3B69CFC865F459A4FC227D6B77A8C2E57B687183545EE3BC18F586A2121230D557F7CECE0BF7B7733037B599BE77D2F3FEF1E6D3F7E7E7E2C7F75F779D9637A2B2D6BE7B1C635C1ACEE946D7EA2F01785EE956D93F117ECD635DD60934F2D5170A7A59C1063571EC33E3AA92E115F43B560AEEB38774FE12CG115BE50553FEF80E
	6E6B596CF66D9EAEEDE3F3C068907331779C72E8F3DA8CFC678A5CE5E197E73A6FAC36DC99144EFFEC8921F4A5130696DBCECE6231E5AFDD2F1AA6797ADABE4537CF7275356FD5F2DFD321EE41A46EEBAFD45DD90C78499D5291731D4516883A6616057B4A7BDD0ABF996EAB6FFEB31737816AE606F3F91F09B30E1F71A41B77C26DE906E763137E74DBE862F4F6288FB2348901778800D9FE69C6B798471B2735D92CBDD7BC59DF6F6C8ECC7466F554066DB607D4457EC20470F35F545B68172913A12F1C4C777B
	0F6750B39FA857CE9667DC39C470D1EAF87FF14E5D7F9EDB788F98A3AACF17698543FBE55E5D9F7B4ADD9DC3BB6726B9FDA36833DD687929BEFE6FC195385567799D07D182B7F69B3F6390A6F05FE863E7C2D182D717436FB81027B84EDE4C475DA2F0AF9773F1B385EE250B0FDBA2F0FFAD61631E943897457D03A601339E6463C68A5C37E93E9E8D658582F73892F14B20CC26F03B4E2B52F5DDECF8FF31726E6BFD7EBFE00C6F18F5193EEFF8BFE83E7BE31F3D2BE3B01DC653EF1ED450E746942E67F91769B9
	AB144B856E60975CEEEA856E09B8FE4FA3CC60BE235FBED1D01EABF01D0779980B846E0B8D080B05F26AD40EFB690438D7A0F0EFD2BBF4C2F91440CD265FD2E1D00E94380F92293DC2F91E40BD18A86E5ACC65E7A4FBEFE3AE24626E836E6591FEE6F71446717CE9B1C9C726713697F392F9DB4277EF1FBB0FB6406AAF74F91FCF3ED2CFC4FFC76837E6BB742582E86702B4B604C3B9D260DE2431E1811497CF93E774EDC3B95B6460FB3639656918A44C21D68BDEEE53381289787C2779BEA73920737AA796E320
	4E7AB467ABCB257737209CA6F0B9B4CE4C01727C27B92F2F8CF87EEEBC037547EBC2847BBBF31D57876898E9004F0860FA0869762F335729ED1C04F68D226D594A7EBA5BFBD15759EBF15E674EEFD39B8E867AF1220FA5173D75DAFD094E951537760B3FC75E6888BDB7E2637FA40E1FB14F9563FFE98AFCCF221B7A57C2E83BB4026B65916ADFDB1E813FF90677F1FD837D663034F434A9325BE165745F24FE57G6D2EBE43F5386844F06ED0784A94847D6CA0463374773A7D5F2118C06F970D017621024F979A
	FC6E81D05F1E8D7573C41B0AC4EFFD589AF5FDCCCB9C087764C1F8A74F32330DB8E1AB37A45DD68E64607FAF70337FDF819FF876BD7919C57C0C0C5106376A34FF7DFC27AF2DFE36F384898F65F13A136DBA5DBD5B06CEB7651ACE5755B654338AC67B138EA2787E3DDD6F2730EB68632FF368F44BDD831DE59515B0C2C6D7A86BF46F8CF8AE67CD7756F51D6E39A11F4BB15A2DAEDD5ED2D56FA7FC40B3B86F71F7F76BF49FD78E4FB67E7316CEBB7B7270F8CF3B2D537EC61C8931BA3C9D4D6B1A8F8F20BBFA1C
	D8AC7AFC5024531D59B01C73E0ED7DC85069FFF48277CF84ED15EC1715026EF61B2A7138B7D11F7B6AA41D2E38E1687C7EA2C5275B19B8147953E9E369719C7BFFA6251B7122DE645BA4D1A56B34E34BCCFAEE69D248267EFCD40C7D65EB11DF193718082906A435537D0F9A12580D9BA99E7C537FAD37D72DDA653ACE7A58628BB095FADA6745593FBEF970F7F3FFF35195CF12EA48B68CF67512C2771AA4AB7E2D05768F812A49EA27EC03CA4057108497FDA43A549E3BD4CB36341E47BF06F5291235026FE239
	D472FEAD0E349F8FD5761F34385AD55CF9BADACB929C782F8B48A6D6ED37B85A7633AD2D23146B380A2B54F0B0C89260F7B36597FBA9903DECFEF1C3766ED30BE74693D3B5D9E317AD56EACB0BAAABB5E4355A2EC82EF659EE75B80F17530ED23A791EF7BCF437DD080F429660AD8F41F4076771B16B162E2B868374A8E4BFED09313DC03430D635DB9498CAD2EAD47CD78C3212A56AC66CD4773D7C5956A6930953044429CF72B8D51254EA693459FBD432DA58AD554B9A4028F8CD24D5D6BA0139CC9F66F81598
	236021178A9FFE66AFDF50B1F4D2156CE23389247B5BA439F3FFC5EB2B4DA1098EE0EA217B9D22FBECD36053E613E77C7B27DEDD558C26E0A6711D050574F7917DDD0C3F0BA82608E20A0AE1FE171381FFFC6BBDDCE6EABF404F864A8FDAC0C1B5A8D1FD7D6D17A67F2AEC79B2102E1AA4523FE884BD24692D58E97C484807F72C8DD9B49A5564C4EB71E03F6BBD22C71430066E6BAD6ABAD09A7F5B2762CDA481FC046DF3027D62E6A5582F42DCA6DE151D54749208FE37B6414BF102C58977841F252504FE460A
	58147ED60768D4B0829683303401B65DC8FFEF9976B4A2558714B6DDC83F1BCDAEF2374E2F94D87348570F6FBC1DEF6499F76332845F085D26C904B5DB3D306BB5BE13D4325E7BAFD4D43216464DCC3C4785CB6AE6F6A1AB53F0A38B30A97CB49D1AD73ADD194C3820E2E3EED555054CBAD9D1B37796E5FE12F9A44F6F3C4585183784BEEFE6BAEF5C6813588EB5139FE96E4E049E71881563515867FECADFAB8328528DA0C16347C40C065BDF8A45CF69EBA55399DD260D861912074077900D8ACB1F622F23F7C7
	028B1E2F5F2CDE51E795670FDE3EGC892330A47DD45F744373EAC5F9D56DBF0EDDA6F1D0646CD5DFACF5A03875CA612D20D89025D59EE2951D63DDA323619AECEF8F5542E12B4FDF5273410C519DAA0EEED5771AD08E73575CECB8BA6DA2B2D9625037600BC34DFA12BDB1536E6BA00CACCD72A2F403034045557EAA1C83563859F4CF8D432D56F3A8E2FD1E895ACC7A310D82EF2529F2193BC086417C893826A04E9A0F5A836E43E8B36D42E152F6B360417AF50BB3FA47492863BAF17F75BF8DFEE453D69816F
	471AE7E8674DB3747BF26C8E45F93A1F9D827828997C6E4A642D1F1040F7A96ABBADF63BEA31F7D4B82CF82F2232C715743BDE175231FF97EF23D5323AAF53116FEB861A7F83D0CB87886FEB1799F49BGG00D1GGD0CB818294G94G88G88GFAF954AC6FEB1799F49BGG00D1GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2E9BGGGG
**end of data**/
}
}
