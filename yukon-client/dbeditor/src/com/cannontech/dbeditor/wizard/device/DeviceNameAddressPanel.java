package com.cannontech.dbeditor.wizard.device;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.gui.unchanging.LongRangeDocument;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.wizard.CancelInsertException;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.device.CCU721;
import com.cannontech.database.data.device.CarrierBase;
import com.cannontech.database.data.device.DNPBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.IDLCBase;
import com.cannontech.database.data.device.Ion7700;
import com.cannontech.database.data.device.RTCBase;
import com.cannontech.database.data.device.RTM;
import com.cannontech.database.data.device.Repeater900;
import com.cannontech.database.data.device.Repeater921;
import com.cannontech.database.data.device.Series5Base;
import com.cannontech.database.data.device.XmlTransmitter;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.dbeditor.DatabaseEditorOptionPane;
import com.cannontech.device.range.PlcAddressRangeService;
import com.cannontech.device.range.IntegerRange;
import com.cannontech.spring.YukonSpringHook;

/**
 * This type was created in VisualAge.
 */

public class DeviceNameAddressPanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.CaretListener {
	private DeviceBase deviceBase = null;
//	private int deviceType = PAOGroups.INVALID;
	private javax.swing.JTextField ivjAddressTextField = null;
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjNameTextField = null;
	private javax.swing.JLabel ivjPhysicalAddressLabel = null;
	private javax.swing.JLabel ivjJLabelErrorMessage = null;
	private javax.swing.JPanel ivjJPanelNameAddy = null;
	private javax.swing.JPanel ivjJPanel1 = null;
    
    private JCheckBox createPointsCheck = null;
    
    private PlcAddressRangeService plcAddressRangeService = 
        YukonSpringHook.getBean("plcAddressRangeService", PlcAddressRangeService.class);

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DeviceNameAddressPanel() {
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
	if (e.getSource() == getNameTextField()) 
		connEtoC1(e);
	if (e.getSource() == getAddressTextField()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}

    private void checkPaoAddresses(int address) {

        String[] devices = DeviceCarrierSettings.isAddressUnique(address, null);

        if (devices.length > 0) {
            
            String message = "The address '"
                + address
                + "' is already used by the following devices,\n"
                + "are you sure you want to use it again?\n";
            
            int res = DatabaseEditorOptionPane.showAlreadyUsedConfirmDialog(this,
                                                                            message,
                                                                            "Address Already Used",
                                                                            devices);
            if (res == JOptionPane.NO_OPTION) {
                throw new CancelInsertException("Device was not inserted");
            }

        }
    }


/**
 * connEtoC1: (NameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) -->
 * DeviceNameAddressPanel.eitherTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.eitherTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (AddressTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceNameAddressPanel.eitherTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.eitherTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
public void eitherTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	fireInputUpdate();
}

/**
 * Insert the method's description here.
 * Creation date: (7/27/2001 10:04:55 AM)
 * @return java.lang.String
 */
public String getAddress()
{
    return getAddressTextField().getText();
}


/**
 * Return the AddressTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getAddressTextField() {
	if (ivjAddressTextField == null) {
		try {
			ivjAddressTextField = new javax.swing.JTextField();
			ivjAddressTextField.setName("AddressTextField");
			ivjAddressTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjAddressTextField.setColumns(6);
			// user code begin {1}

			ivjAddressTextField.setDocument( new LongRangeDocument(-999999999L, 999999999L) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddressTextField;
}

/**
 * Return the JLabelRange property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelErrorMessage() {
	if (ivjJLabelErrorMessage == null) {
		try {
			ivjJLabelErrorMessage = new javax.swing.JLabel();
			ivjJLabelErrorMessage.setName("JLabelRange");
			ivjJLabelErrorMessage.setOpaque(false);
			ivjJLabelErrorMessage.setText("..RANGE TEXT..");
			ivjJLabelErrorMessage.setVisible(true);
			ivjJLabelErrorMessage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabelErrorMessage.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJLabelErrorMessage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}

			ivjJLabelErrorMessage.setVisible( false );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelErrorMessage;
}

/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setPreferredSize(new java.awt.Dimension(342, 27));
			ivjJPanel1.setLayout(new java.awt.FlowLayout());
			ivjJPanel1.setMaximumSize(new java.awt.Dimension(342, 27));
			ivjJPanel1.setMinimumSize(new java.awt.Dimension(342, 27));
			getJPanel1().add(getJLabelErrorMessage(), getJLabelErrorMessage().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}

/**
 * Return the JPanelNameAddy property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelNameAddy() {
	if (ivjJPanelNameAddy == null) {
		try {
			ivjJPanelNameAddy = new javax.swing.JPanel();
			ivjJPanelNameAddy.setName("JPanelNameAddy");
			ivjJPanelNameAddy.setLayout(new GridBagLayout());

			GridBagConstraints constraintsNameLabel = new GridBagConstraints();
			constraintsNameLabel.gridx = 1; 
            constraintsNameLabel.gridy = 1;
			constraintsNameLabel.anchor = GridBagConstraints.WEST;
			constraintsNameLabel.insets = new Insets(15, 10, 0, 0);
			getJPanelNameAddy().add(getNameLabel(), constraintsNameLabel);

			GridBagConstraints constraintsPhysicalAddressLabel = new GridBagConstraints();
			constraintsPhysicalAddressLabel.gridx = 1; 
            constraintsPhysicalAddressLabel.gridy = 2;
			constraintsPhysicalAddressLabel.anchor = GridBagConstraints.WEST;
			constraintsPhysicalAddressLabel.insets = new Insets(10, 10, 0, 0);
			getJPanelNameAddy().add(getPhysicalAddressLabel(), constraintsPhysicalAddressLabel);

			// Add create points check box
			GridBagConstraints constraintsCreatePoints = new GridBagConstraints();
            constraintsCreatePoints.gridx = 1; 
            constraintsCreatePoints.gridy = 3;
            constraintsCreatePoints.anchor = GridBagConstraints.WEST;
            constraintsCreatePoints.insets = new Insets(10, 10, 0, 0);
            constraintsCreatePoints.weighty = 1.0;
            this.createPointsCheck = new JCheckBox("Create default points");
            this.createPointsCheck.setVisible(false);
			getJPanelNameAddy().add(this.createPointsCheck, constraintsCreatePoints);
            
			GridBagConstraints constraintsNameTextField = new GridBagConstraints();
			constraintsNameTextField.gridx = 2; 
            constraintsNameTextField.gridy = 1;
			constraintsNameTextField.fill = GridBagConstraints.HORIZONTAL;
			constraintsNameTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsNameTextField.weightx = 1.0;
			constraintsNameTextField.insets = new Insets(15, 0, 0, 20);
			getJPanelNameAddy().add(getNameTextField(), constraintsNameTextField);

			GridBagConstraints constraintsAddressTextField = new GridBagConstraints();
			constraintsAddressTextField.gridx = 2; 
            constraintsAddressTextField.gridy = 2;
			constraintsAddressTextField.fill = GridBagConstraints.HORIZONTAL;
			constraintsAddressTextField.anchor = GridBagConstraints.WEST;
			constraintsAddressTextField.weightx = 1.0;
			constraintsAddressTextField.insets = new Insets(10, 0, 0, 20);
			getJPanelNameAddy().add(getAddressTextField(), constraintsAddressTextField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelNameAddy;
}

/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}


/**
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNameLabel() {
	if (ivjNameLabel == null) {
		try {
			ivjNameLabel = new javax.swing.JLabel();
			ivjNameLabel.setName("NameLabel");
			ivjNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjNameLabel.setText("Device Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameLabel;
}


/**
 * Return the NameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getNameTextField() {
	if (ivjNameTextField == null) {
		try {
			ivjNameTextField = new javax.swing.JTextField();
			ivjNameTextField.setName("NameTextField");
			ivjNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjNameTextField.setColumns(12);
			// user code begin {1}
			ivjNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_DEVICE_NAME_LENGTH, TextFieldDocument.INVALID_CHARS_PAO));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameTextField;
}


/**
 * Return the PhysicalAddressLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPhysicalAddressLabel() {
	if (ivjPhysicalAddressLabel == null) {
		try {
			ivjPhysicalAddressLabel = new javax.swing.JLabel();
			ivjPhysicalAddressLabel.setName("PhysicalAddressLabel");
			ivjPhysicalAddressLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPhysicalAddressLabel.setText("Physical Address:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPhysicalAddressLabel;
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
	//The name is easy, the address is more difficult
	//since at this point all devices have a physical
	//address but it is tedious to tell what type
	//of device it is - CommLine, Carrier, Repeater, MCT Broadcast group, etc

	deviceBase = (DeviceBase)val;
	
	String nameString = getNameTextField().getText();
	deviceBase.setPAOName( nameString );

	//Search for the correct sub-type and set the address

	Integer address = new Integer( getAddressTextField().getText() );
	
	if( val instanceof IDLCBase ) {
		((IDLCBase)deviceBase).getDeviceIDLCRemote().setAddress( address );	
	} else if( val instanceof CCU721 ) {
		((CCU721)deviceBase).getDeviceAddress().setSlaveAddress( address );
	} else if( val instanceof Ion7700 ) {
		((Ion7700)val).getDeviceAddress().setSlaveAddress( address );
	} else if( val instanceof DNPBase ) {
		((DNPBase)val).getDeviceAddress().setSlaveAddress( address );
	} else if( val instanceof RTCBase ) {
   		((RTCBase)val).getDeviceRTC().setRTCAddress( address );
   	} else if( val instanceof RTM ) {
   		((RTM)val).getDeviceIED().setSlaveAddress(address.toString());
   		((RTM)val).getDeviceIED().setPassword(CtiUtilities.STRING_NONE);
   	} else if( val instanceof Series5Base ) {
   		((Series5Base)val).getSeries5().setSlaveAddress( address );
   	} else if( val instanceof CarrierBase ) {
   		CarrierBase carrierBase = (CarrierBase)val;
		if( carrierBase instanceof Repeater900 ) {
			//special case, we must add ADDRESS_OFFSET to every address for Repeater900
			carrierBase.getDeviceCarrierSettings().setAddress( 
                  new Integer(address.intValue() + Repeater900.ADDRESS_OFFSET) );
		}else if( val instanceof Repeater921 ) {
            //special case, we must add ADDRESS_OFFSET to every address for Repeater921
            carrierBase.getDeviceCarrierSettings().setAddress( 
                  new Integer(address.intValue() + Repeater921.ADDRESS_OFFSET) );
        }
		else {
			carrierBase.getDeviceCarrierSettings().setAddress( address );
		}
	} else if (val instanceof XmlTransmitter) {
        //This is empty because nothing special needs to be done, 
    	//but we don't want to throw an error since it is valid
    } else  { //didn't find it
		throw new Error("Unable to determine device type when attempting to set the address");
	}

	int deviceType = PAOGroups.getDeviceType(deviceBase.getPAOType());
	if (DeviceTypesFuncs.isMCT(deviceType) || DeviceTypesFuncs.isRepeater(deviceType) || DeviceTypesFuncs.isTwoWayLcr(deviceType)) {

        // Check for unique address
	    checkPaoAddresses(address.intValue());
        
        if (this.createPointsCheck.isSelected()) {
            PaoDao paoDao = (PaoDao) YukonSpringHook.getBean("paoDao");
            deviceBase.setDeviceID(paoDao.getNextPaoId());

            PaoDefinitionService paoDefinitionService = (PaoDefinitionService) YukonSpringHook.getBean("paoDefinitionService");
            DeviceDao deviceDao = (DeviceDao) YukonSpringHook.getBean("deviceDao");
            SimpleDevice yukonDevice = deviceDao.getYukonDeviceForDevice(deviceBase);
            List<PointBase> defaultPoints = paoDefinitionService.createDefaultPointsForPao(yukonDevice);

            SmartMultiDBPersistent persistant = new SmartMultiDBPersistent();
            persistant.addOwnerDBPersistent(deviceBase);
            for (PointBase point : defaultPoints) {
                persistant.addDBPersistent(point);
            }
            return persistant;
        }
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
	// user code end
	getNameTextField().addCaretListener(this);
	getAddressTextField().addCaretListener(this);
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DeviceNameAddressPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 357);

		GridBagConstraints constraintsJPanelNameAddy = new GridBagConstraints();
		constraintsJPanelNameAddy.gridx = 1; 
        constraintsJPanelNameAddy.gridy = 1;
		constraintsJPanelNameAddy.fill = GridBagConstraints.HORIZONTAL;
		constraintsJPanelNameAddy.anchor = GridBagConstraints.WEST;
		constraintsJPanelNameAddy.weightx = 1.0;
		constraintsJPanelNameAddy.insets = new Insets(0, 0, 0, 0);
		add(getJPanelNameAddy(), constraintsJPanelNameAddy);

		GridBagConstraints constraintsJPanel1 = new GridBagConstraints();
		constraintsJPanel1.gridx = 1; 
        constraintsJPanel1.gridy = 2;
		constraintsJPanel1.fill = GridBagConstraints.HORIZONTAL;
		constraintsJPanel1.anchor = GridBagConstraints.WEST;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.insets = new Insets(2, 4, 202, 4);
		add(getJPanel1(), constraintsJPanel1);
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
	String deviceName = getNameTextField().getText();
	if( StringUtils.isBlank(deviceName)) {
		setErrorString("The Name text field must be filled in");
		return false;
	}

	if( StringUtils.isBlank(getAddress())) {
		setErrorString("The Address text field must be filled in");
		return false;
	}

	int address = Integer.parseInt( getAddress());
	int deviceType = PAOGroups.getDeviceType(deviceBase.getPAOType());
	PaoType paoType = PaoType.getForId(deviceType);
    IntegerRange range = plcAddressRangeService.getAddressRangeForDevice(paoType);
	if (!range.isWithinRange(address)) {
		setErrorString("Invalid address. Device address range: " + range);
		getJLabelErrorMessage().setText( "(" + getErrorString() + ")" );
		getJLabelErrorMessage().setToolTipText( "(" + getErrorString() + ")" );
		getJLabelErrorMessage().setVisible( true );
		return false;
	}

	if( !isUniquePao(deviceName, deviceBase.getPAOCategory(), deviceBase.getPAOClass())) {
		setErrorString("Name '" + deviceName + "' is already in use.");
     	getJLabelErrorMessage().setText( "(" + getErrorString() + ")" );
     	getJLabelErrorMessage().setToolTipText( "(" + getErrorString() + ")" );
     	getJLabelErrorMessage().setVisible( true );
		return false;
	}

	getJLabelErrorMessage().setText( "" );
   	getJLabelErrorMessage().setToolTipText( "" );
    getJLabelErrorMessage().setVisible( false );
	return true;
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DeviceNameAddressPanel aDeviceNameAddressPanel;
		aDeviceNameAddressPanel = new DeviceNameAddressPanel();
		frame.getContentPane().add("Center", aDeviceNameAddressPanel);
		frame.setSize(aDeviceNameAddressPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/30/2002 10:02:36 AM)
 * @param newDeviceType int
 */
public void setDeviceType(int deviceType) 
{
	deviceBase = DeviceFactory.createDevice(deviceType);
   if( DeviceTypesFuncs.hasMasterAddress(deviceType) )
      getPhysicalAddressLabel().setText("Master Address:");
   else if( DeviceTypesFuncs.hasSlaveAddress(deviceType) || DeviceTypes.CCU721 == deviceType )
      getPhysicalAddressLabel().setText("Slave Address:");
   else if( deviceType == DeviceTypes.MCTBROADCAST )
      getPhysicalAddressLabel().setText("Lead Meter Address:");
   else if( deviceType == DeviceTypes.SERIES_5_LMI )
	  getPhysicalAddressLabel().setText("Address:");
   else if(DeviceTypesFuncs.isTwoWayLcr(deviceType))
		  getPhysicalAddressLabel().setText("Serial Number:");
   else
      getPhysicalAddressLabel().setText("Physical Address:");
   
   if (DeviceTypesFuncs.isMCT(deviceType) || DeviceTypesFuncs.isRepeater(deviceType)
            || DeviceTypesFuncs.isCCU(deviceType) || DeviceTypesFuncs.isTwoWayLcr(deviceType)) {
        this.createPointsCheck.setVisible(true);
        this.createPointsCheck.setSelected(true);
    } else {
        this.createPointsCheck.setVisible(false);
        this.createPointsCheck.setSelected(false);
    }
   
}


/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) {
	return;
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getNameTextField().requestFocus(); 
        } 
    });    
}

}