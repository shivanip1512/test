package com.cannontech.dbeditor.editor.route;

/**
 * This type was created in VisualAge.
 */

import java.awt.GridBagConstraints;
import java.util.List;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

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
public CommunicationRouteEditorPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	if (e.getSource() == getRouteNameTextField()) 
		connEtoC1(e);
}
/**
 * connEtoC1:  (RouteNameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> CommunicationRouteEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (SignalTransmitterComboBox.item.itemStateChanged(java.awt.event.ItemEvent) --> CommunicationRouteEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
private void connEtoC2(java.awt.event.ItemEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (DefaultRouteCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> CommunicationRouteEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
private void connEtoC3(java.awt.event.ItemEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Return the BussNumberLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getBusNumberLabel() {
	if (ivjBusNumberLabel == null) {
		try {
			ivjBusNumberLabel = new javax.swing.JLabel();
			ivjBusNumberLabel.setName("BusNumberLabel");
			ivjBusNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjBusNumberLabel.setText("Bus Number:");
			ivjBusNumberLabel.setVisible(true);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjBusNumberLabel;
}
/**
 * Return the BusNumberSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
private com.klg.jclass.field.JCSpinField getBusNumberSpinner() {
	if (ivjBusNumberSpinner == null) {
		try {
			ivjBusNumberSpinner = new com.klg.jclass.field.JCSpinField();
			ivjBusNumberSpinner.setName("BusNumberSpinner");
			ivjBusNumberSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjBusNumberSpinner.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjBusNumberSpinner.setBackground(java.awt.Color.white);
			ivjBusNumberSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
			ivjBusNumberSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1), new Integer(8), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjBusNumberSpinner;
}
/**
 * Return the ConfigurationPanel property value.
 * @return javax.swing.JPanel
 */
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
		} catch (java.lang.Throwable ivjExc) {
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
            constraintsRouteNameLabel.gridx = 0;
            constraintsRouteNameLabel.gridy = 0;
            constraintsRouteNameLabel.anchor = GridBagConstraints.NORTHWEST;
            constraintsRouteNameLabel.fill = GridBagConstraints.HORIZONTAL;
            constraintsRouteNameLabel.insets = new java.awt.Insets(5, 5, 5, 5);
            getIdentificationPanel().add(getRouteNameLabel(), constraintsRouteNameLabel);

            java.awt.GridBagConstraints constraintsRouteNameTextField = new java.awt.GridBagConstraints();
            constraintsRouteNameTextField.gridx = 0;
            constraintsRouteNameTextField.gridy = 1;
            constraintsRouteNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsRouteNameTextField.weightx = 1.0;
            constraintsRouteNameTextField.insets = new java.awt.Insets(5, 5, 5, 5);
            getIdentificationPanel().add(getRouteNameTextField(), constraintsRouteNameTextField);

            java.awt.GridBagConstraints constraintsSignalTransmitterLabel = new java.awt.GridBagConstraints();
            constraintsSignalTransmitterLabel.gridx = 0;
            constraintsSignalTransmitterLabel.gridy = 2;
            constraintsSignalTransmitterLabel.anchor = GridBagConstraints.NORTHWEST;
            constraintsSignalTransmitterLabel.fill = GridBagConstraints.HORIZONTAL;
            constraintsSignalTransmitterLabel.insets = new java.awt.Insets(5, 5, 5, 5);
            getIdentificationPanel().add(getSignalTransmitterLabel(), constraintsSignalTransmitterLabel);

            java.awt.GridBagConstraints constraintsSignalTransmitterComboBox = new java.awt.GridBagConstraints();
            constraintsSignalTransmitterComboBox.gridx = 0;
            constraintsSignalTransmitterComboBox.gridy = 3;
            constraintsSignalTransmitterComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsSignalTransmitterComboBox.weightx = 1.0;
            constraintsSignalTransmitterComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
            getIdentificationPanel().add(getSignalTransmitterComboBox(), constraintsSignalTransmitterComboBox);

        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjIdentificationPanel;
}


/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelPrtNum() {
	if (ivjJLabelPrtNum == null) {
		try {
			ivjJLabelPrtNum = new javax.swing.JLabel();
			ivjJLabelPrtNum.setName("JLabelPrtNum");
			ivjJLabelPrtNum.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelPrtNum.setText("(CCU Port #1 - 8)");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelPrtNum;
}
/**
 * Return the RouteNameLabel1 property value.
 * @return javax.swing.JLabel
 */
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRouteNameLabel;
}
/**
 * Return the RouteNameTextField1 property value.
 * @return javax.swing.JTextField
 */
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
			ivjRouteNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_ROUTE_NAME_LENGTH));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRouteNameTextField;
}
/**
 * Return the SignalTransmitterComboBox1 property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getSignalTransmitterComboBox() {
	if (ivjSignalTransmitterComboBox == null) {
		try {
			ivjSignalTransmitterComboBox = new javax.swing.JComboBox();
			ivjSignalTransmitterComboBox.setName("SignalTransmitterComboBox");
			ivjSignalTransmitterComboBox.setPreferredSize(new java.awt.Dimension(170, 20));
			ivjSignalTransmitterComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSignalTransmitterComboBox.setMinimumSize(new java.awt.Dimension(150, 20));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSignalTransmitterComboBox;
}
/**
 * Return the SignalTransmitterLabel1 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getSignalTransmitterLabel() {
	if (ivjSignalTransmitterLabel == null) {
		try {
			ivjSignalTransmitterLabel = new javax.swing.JLabel();
			ivjSignalTransmitterLabel.setName("SignalTransmitterLabel");
			ivjSignalTransmitterLabel.setText("Signal Transmitter:");
			ivjSignalTransmitterLabel.setMaximumSize(new java.awt.Dimension(114, 20));
			ivjSignalTransmitterLabel.setPreferredSize(new java.awt.Dimension(114, 20));
			ivjSignalTransmitterLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSignalTransmitterLabel.setMinimumSize(new java.awt.Dimension(114, 20));
		} catch (java.lang.Throwable ivjExc) {
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
private void initConnections() throws java.lang.Exception {
	getBusNumberSpinner().addValueListener(this);
	
	// user code end
	getRouteNameTextField().addCaretListener(this);
	getSignalTransmitterComboBox().addItemListener(this);
	getDefaultRouteCheckBox().addItemListener(this);
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
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
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	if (e.getSource() == getSignalTransmitterComboBox()) 
		connEtoC2(e);
	if (e.getSource() == getDefaultRouteCheckBox()) 
		connEtoC3(e);
}
/**
 * This method was created in VisualAge.
 * @param routeType int
 */
private void loadSignalTransmitterComboBox(String routeType) {

	PaoType routePaoType = PaoType.getForDbString(routeType);
	IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		List<LiteYukonPAObject> devices = cache.getAllDevices();
		if( getSignalTransmitterComboBox().getModel().getSize() > 0 )
			getSignalTransmitterComboBox().removeAllItems();

		if (routePaoType == PaoType.ROUTE_CCU )
		{
			for (LiteYukonPAObject liteDevice : devices) {
				if (DeviceTypesFuncs.isCCU(liteDevice.getPaoType().getDeviceTypeId())) {
					getSignalTransmitterComboBox().addItem(liteDevice);
				}
			}  	//repeaters are not actually signal transmitters, so they should not be in the ComboBox
				//|| com.cannontech.database.data.device.DeviceTypesFuncs.isRepeater(type)
		}
		else if (routePaoType == PaoType.ROUTE_TCU)
		{
			for (LiteYukonPAObject liteDevice : devices) {
				if (DeviceTypesFuncs.isTCU(liteDevice.getPaoType().getDeviceTypeId())) {
					getSignalTransmitterComboBox().addItem(liteDevice);
				}
			}
		}
		else if (routePaoType == PaoType.ROUTE_LCU)
		{
			for (LiteYukonPAObject liteDevice : devices) {
				if (DeviceTypesFuncs.isLCU( liteDevice.getPaoType().getDeviceTypeId())) {
					getSignalTransmitterComboBox().addItem(liteDevice);
				}
			}
		}
		else if (routePaoType == PaoType.ROUTE_TAP_PAGING)
		{
			for (LiteYukonPAObject liteDevice : devices) {
				if (liteDevice.getPaoType() == PaoType.TAPTERMINAL) {
					getSignalTransmitterComboBox().addItem(liteDevice);
				}
			}
		}
		else if (routePaoType == PaoType.ROUTE_WCTP_TERMINAL)
		{
			for (LiteYukonPAObject liteDevice : devices) {
				if (liteDevice.getPaoType() == PaoType.WCTP_TERMINAL) {
					getSignalTransmitterComboBox().addItem(liteDevice);
				}
			}
		}
		else if (routePaoType == PaoType.ROUTE_SNPP_TERMINAL)
		{
			for (LiteYukonPAObject liteDevice : devices) {
				if (liteDevice.getPaoType() == PaoType.SNPP_TERMINAL) {
					getSignalTransmitterComboBox().addItem(liteDevice);
				}
			}
		}
		else if (routePaoType == PaoType.ROUTE_TNPP_TERMINAL)
        {
			for (LiteYukonPAObject liteDevice : devices) {
                if (liteDevice.getPaoType() == PaoType.TNPP_TERMINAL) {
                    getSignalTransmitterComboBox().addItem(liteDevice);
                }
            }
        }
		else if (routePaoType == PaoType.ROUTE_RDS_TERMINAL)
		{
			for (LiteYukonPAObject liteDevice : devices) {
		        if (liteDevice.getPaoType() == PaoType.RDS_TERMINAL) {
		            getSignalTransmitterComboBox().addItem(liteDevice);
		        }
		    }
		}
		else if (routePaoType == PaoType.ROUTE_VERSACOM)
		{
			for (LiteYukonPAObject liteDevice : devices) {
				int type = liteDevice.getPaoType().getDeviceTypeId();
				if( DeviceTypesFuncs.isCCU(type)
					 || DeviceTypesFuncs.isTCU(type)
					 || DeviceTypesFuncs.isLCU(type)) {
					getSignalTransmitterComboBox().addItem(liteDevice);
				}
			}
		}
		else if (routePaoType == PaoType.ROUTE_SERIES_5_LMI)
		{
			for (LiteYukonPAObject liteDevice : devices) {
				if (liteDevice.getPaoType() == PaoType.SERIES_5_LMI) {
					getSignalTransmitterComboBox().addItem(liteDevice);
				}
			}
		}
		else if (routePaoType == PaoType.ROUTE_RTC)
		{
			for (LiteYukonPAObject liteDevice : devices) {
				if (liteDevice.getPaoType() == PaoType.RTC) {
					getSignalTransmitterComboBox().addItem(liteDevice);
				}
			}
		}
		else if (routePaoType == PaoType.ROUTE_INTEGRATION)
		{
			for (LiteYukonPAObject liteDevice : devices) {
				if (liteDevice.getPaoType() == PaoType.INTEGRATION_TRANSMITTER) {
					getSignalTransmitterComboBox().addItem(liteDevice);
				}
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
}
