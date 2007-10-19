package com.cannontech.dbeditor.wizard.copy.device;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.cannontech.common.wizard.CancelInsertException;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapBankController6510;
import com.cannontech.database.data.capcontrol.CapBankController702x;
import com.cannontech.database.data.capcontrol.ICapBankController;
import com.cannontech.database.data.device.CarrierBase;
import com.cannontech.database.data.device.DNPBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.IDLCBase;
import com.cannontech.database.data.device.IEDBase;
import com.cannontech.database.data.device.IEDMeter;
import com.cannontech.database.data.device.Ion7700;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.device.RTCBase;
import com.cannontech.database.data.device.RemoteBase;
import com.cannontech.database.data.device.Repeater900;
import com.cannontech.database.data.device.Repeater902;
import com.cannontech.database.data.device.Series5Base;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.yukon.IDatabaseCache;
 
public class DeviceCopyNameAddressPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ItemListener, javax.swing.event.CaretListener {
	private javax.swing.JTextField ivjAddressTextField = null;
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjNameTextField = null;
	private javax.swing.JLabel ivjPhysicalAddressLabel = null;
	private javax.swing.JCheckBox ivjPointCopyCheckBox = null;
	private javax.swing.JLabel ivjJLabelMeterNumber = null;
	private javax.swing.JPanel ivjJPanelCopyDevice = null;
	private javax.swing.JTextField ivjJTextFieldMeterNumber = null;
   	private int deviceType = 0;
   	private JLabel jLabelRange = null;

	class IvjEventHandler implements java.awt.event.ItemListener, javax.swing.event.CaretListener 
	{
		public void caretUpdate(javax.swing.event.CaretEvent e) 
		{
			if (e.getSource() == DeviceCopyNameAddressPanel.this.getNameTextField()) 
				connEtoC1(e);
			if (e.getSource() == DeviceCopyNameAddressPanel.this.getAddressTextField()) 
				connEtoC2(e);
			if (e.getSource() == DeviceCopyNameAddressPanel.this.getJTextFieldMeterNumber()) 
				connEtoC4(e);
			if (e.getSource() == DeviceCopyNameAddressPanel.this.getJTextFieldPhoneNumber()) 
				connEtoC5(e);
		};
	
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == DeviceCopyNameAddressPanel.this.getPointCopyCheckBox()) 
				connEtoC3(e);
		};
	};

	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjJLabelPhoneNumber = null;
	private javax.swing.JTextField ivjJTextFieldPhoneNumber = null;
	/**
	 * Constructor
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public DeviceCopyNameAddressPanel() {
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
	
	
	/**
	 * connEtoC1:  (NameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceCopyNameAddressPanel.fireInputUpdate()V)
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
	 * connEtoC2:  (AddressTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceCopyNameAddressPanel.fireInputUpdate()V)
	 * @param arg1 javax.swing.event.CaretEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC2(javax.swing.event.CaretEvent arg1) {
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
	 * connEtoC3:  (PointCopyCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> DeviceCopyNameAddressPanel.pointCopyCheckBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
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
 * connEtoC4:  (JTextFieldMeterNumber.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceCopyNameAddressPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC5:  (JTextFieldPhoneNumber.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceCopyNameAddressPanel.fireInputUpdate()V)
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
	 * Creation date: (2/23/00 10:40:51 AM)
	 * @return boolean
	 */
	public boolean copyPointsIsChecked() {
		return getPointCopyCheckBox().isSelected();
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
				ivjAddressTextField.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(-9999999999L, 9999999999L) );
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
	 * Return the JLabelMeterNumber property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getJLabelMeterNumber() {
		if (ivjJLabelMeterNumber == null) {
			try {
				ivjJLabelMeterNumber = new javax.swing.JLabel();
				ivjJLabelMeterNumber.setName("JLabelMeterNumber");
				ivjJLabelMeterNumber.setFont(new java.awt.Font("dialog", 0, 14));
				ivjJLabelMeterNumber.setText("Meter Number:");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJLabelMeterNumber;
	}
/**
 * Return the JLabelPhoneNumber property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPhoneNumber() {
	if (ivjJLabelPhoneNumber == null) {
		try {
			ivjJLabelPhoneNumber = new javax.swing.JLabel();
			ivjJLabelPhoneNumber.setName("JLabelPhoneNumber");
			ivjJLabelPhoneNumber.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelPhoneNumber.setText("Phone Number:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPhoneNumber;
}
	/**
	 * Return the JPanel1 property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getJPanelCopyDevice() {
	if (ivjJPanelCopyDevice == null) {
		try {
			ivjJPanelCopyDevice = new javax.swing.JPanel();
			ivjJPanelCopyDevice.setName("JPanelCopyDevice");
			ivjJPanelCopyDevice.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
			constraintsNameLabel.gridx = 1; constraintsNameLabel.gridy = 1;
			constraintsNameLabel.ipadx = 17;
			constraintsNameLabel.insets = new java.awt.Insets(28, 38, 7, 0);
			getJPanelCopyDevice().add(getNameLabel(), constraintsNameLabel);

			java.awt.GridBagConstraints constraintsPhysicalAddressLabel = new java.awt.GridBagConstraints();
			constraintsPhysicalAddressLabel.gridx = 1; constraintsPhysicalAddressLabel.gridy = 2;
			constraintsPhysicalAddressLabel.ipadx = 48;
			constraintsPhysicalAddressLabel.insets = new java.awt.Insets(7, 38, 5, 0);
			getJPanelCopyDevice().add(getPhysicalAddressLabel(), constraintsPhysicalAddressLabel);

			java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
			constraintsNameTextField.gridx = 2; constraintsNameTextField.gridy = 1;
			constraintsNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNameTextField.weightx = 1.0;
			constraintsNameTextField.ipadx = 186;
			constraintsNameTextField.insets = new java.awt.Insets(26, 0, 5, 18);
			getJPanelCopyDevice().add(getNameTextField(), constraintsNameTextField);

			java.awt.GridBagConstraints constraintsAddressTextField = new java.awt.GridBagConstraints();
			constraintsAddressTextField.gridx = 2; constraintsAddressTextField.gridy = 2;
			constraintsAddressTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsAddressTextField.weightx = 1.0;
			constraintsAddressTextField.ipadx = 186;
			constraintsAddressTextField.insets = new java.awt.Insets(5, 0, 3, 18);
			getJPanelCopyDevice().add(getAddressTextField(), constraintsAddressTextField);

			java.awt.GridBagConstraints constraintsPointCopyCheckBox = new java.awt.GridBagConstraints();
			constraintsPointCopyCheckBox.gridx = 1; constraintsPointCopyCheckBox.gridy = 5;
			constraintsPointCopyCheckBox.ipadx = 12;
			constraintsPointCopyCheckBox.insets = new java.awt.Insets(4, 38, 28, 0);
			getJPanelCopyDevice().add(getPointCopyCheckBox(), constraintsPointCopyCheckBox);

			java.awt.GridBagConstraints constraintsJLabelMeterNumber = new java.awt.GridBagConstraints();
			constraintsJLabelMeterNumber.gridx = 1; constraintsJLabelMeterNumber.gridy = 3;
			constraintsJLabelMeterNumber.ipadx = 11;
			constraintsJLabelMeterNumber.insets = new java.awt.Insets(6, 38, 6, 0);
			getJPanelCopyDevice().add(getJLabelMeterNumber(), constraintsJLabelMeterNumber);

			java.awt.GridBagConstraints constraintsJTextFieldMeterNumber = new java.awt.GridBagConstraints();
			constraintsJTextFieldMeterNumber.gridx = 2; constraintsJTextFieldMeterNumber.gridy = 3;
			constraintsJTextFieldMeterNumber.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldMeterNumber.weightx = 1.0;
			constraintsJTextFieldMeterNumber.ipadx = 186;
			constraintsJTextFieldMeterNumber.insets = new java.awt.Insets(4, 0, 4, 18);
			getJPanelCopyDevice().add(getJTextFieldMeterNumber(), constraintsJTextFieldMeterNumber);

			java.awt.GridBagConstraints constraintsJLabelPhoneNumber = new java.awt.GridBagConstraints();
			constraintsJLabelPhoneNumber.gridx = 1; constraintsJLabelPhoneNumber.gridy = 4;
			constraintsJLabelPhoneNumber.ipadx = 6;
			constraintsJLabelPhoneNumber.insets = new java.awt.Insets(6, 38, 5, 0);
			getJPanelCopyDevice().add(getJLabelPhoneNumber(), constraintsJLabelPhoneNumber);

			java.awt.GridBagConstraints constraintsJTextFieldPhoneNumber = new java.awt.GridBagConstraints();
			constraintsJTextFieldPhoneNumber.gridx = 2; constraintsJTextFieldPhoneNumber.gridy = 4;
			constraintsJTextFieldPhoneNumber.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldPhoneNumber.weightx = 1.0;
			constraintsJTextFieldPhoneNumber.ipadx = 186;
			constraintsJTextFieldPhoneNumber.insets = new java.awt.Insets(4, 0, 3, 18);
			getJPanelCopyDevice().add(getJTextFieldPhoneNumber(), constraintsJTextFieldPhoneNumber);
			// user code begin {1}
				
	         java.awt.GridBagConstraints cpg = new java.awt.GridBagConstraints();
	         cpg.gridx = 1;
	         cpg.gridy = 5;
	         cpg.anchor = java.awt.GridBagConstraints.WEST;
	         cpg.fill = java.awt.GridBagConstraints.HORIZONTAL;
	         cpg.gridwidth = 2;
	         cpg.insets = new java.awt.Insets(26, 48, 10, 15);
	         getJPanelCopyDevice().add(getJLabelRange(), cpg);
				
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelCopyDevice;
}
	/**
	 * Return the JTextFieldMeterNumber property value.
	 * @return javax.swing.JTextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JTextField getJTextFieldMeterNumber() {
		if (ivjJTextFieldMeterNumber == null) {
			try {
				ivjJTextFieldMeterNumber = new javax.swing.JTextField();
				ivjJTextFieldMeterNumber.setName("JTextFieldMeterNumber");
				ivjJTextFieldMeterNumber.setFont(new java.awt.Font("sansserif", 0, 14));
				ivjJTextFieldMeterNumber.setColumns(6);
				// user code begin {1}
				ivjJTextFieldMeterNumber.setDocument(
					new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_METER_NUMBER_LENGTH));
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJTextFieldMeterNumber;
	}
/**
 * Return the JTextFieldPhoneNumber property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldPhoneNumber() {
	if (ivjJTextFieldPhoneNumber == null) {
		try {
			ivjJTextFieldPhoneNumber = new javax.swing.JTextField();
			ivjJTextFieldPhoneNumber.setName("JTextFieldPhoneNumber");
			ivjJTextFieldPhoneNumber.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjJTextFieldPhoneNumber.setColumns(6);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldPhoneNumber;
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
				ivjNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_DEVICE_NAME_LENGTH));
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
				ivjPhysicalAddressLabel.setText("Address:");
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
	 * Return the PointCopyCheckBox property value.
	 * @return javax.swing.JCheckBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JCheckBox getPointCopyCheckBox() {
		if (ivjPointCopyCheckBox == null) {
			try {
				ivjPointCopyCheckBox = new javax.swing.JCheckBox();
				ivjPointCopyCheckBox.setName("PointCopyCheckBox");
				ivjPointCopyCheckBox.setText("Copy Points");
				ivjPointCopyCheckBox.setEnabled(false);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjPointCopyCheckBox;
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
	 * Note: Cap Bank pointID assignment should be handled by utilizing a CommonMulti
	 */
	@SuppressWarnings("unchecked")
    public Object getValue(Object val)
	{
		DeviceBase device = ((DeviceBase) val);
		int previousDeviceID = device.getDevice().getDeviceID().intValue();
		com.cannontech.database.data.route.RouteBase newRoute = null;
	
        PaoDao paoDao = DaoFactory.getPaoDao();   
		device.setDeviceID(paoDao.getNextPaoId());
	
		boolean hasRoute = false;
		boolean hasPoints = false;
		boolean isCapBank = false;
	
		String nameString = getNameTextField().getText();
		device.setPAOName(nameString);
	
		com.cannontech.database.data.multi.MultiDBPersistent objectsToAdd = new com.cannontech.database.data.multi.MultiDBPersistent();
		objectsToAdd.getDBPersistentVector().add(device);
	
		//Search for the correct sub-type and set the address
		if( getAddressTextField().isVisible() )
		{
			
			if (val instanceof IDLCBase)
				((IDLCBase) val).getDeviceIDLCRemote().setAddress(new Integer(getAddressTextField().getText()));
			else if (val instanceof DNPBase)
				((DNPBase) val).getDeviceAddress().setSlaveAddress(new Integer(getAddressTextField().getText()));
			else if (val instanceof CarrierBase)
			{
				 Integer addressHolder = new Integer(getAddressTextField().getText());
				 if(val instanceof Repeater900 || val instanceof Repeater902)
				  	addressHolder = new Integer(addressHolder.intValue() + 4190000);
				 ((CarrierBase) val).getDeviceCarrierSettings().setAddress(addressHolder);
				 
				if( DeviceTypesFuncs.isMCT(getDeviceType()) )
				{
					checkMCTAddresses( new Integer(getAddressTextField().getText()).intValue() );
                    checkMeterNumber(getJTextFieldMeterNumber().getText());
				}
			}
			else if (val instanceof CapBank)
			{
				 ((CapBank) val).setLocation(getAddressTextField().getText());
				 isCapBank = true;
			}
			else if (val instanceof ICapBankController )
				 ((ICapBankController) val).setAddress( new Integer(getAddressTextField().getText()) );
			else if (val instanceof Series5Base)
				 ((Series5Base) val).getSeries5().setSlaveAddress( new Integer(getAddressTextField().getText()) );			
			else if (val instanceof RTCBase)
				((RTCBase) val).getDeviceRTC().setRTCAddress( new Integer( getAddressTextField().getText()));
			else //didn't find it
				throw new Error("Unable to determine device type when attempting to set the address");
		}
	
		//Search for the correct sub-type and set the meter fields
		if( getJTextFieldMeterNumber().isVisible() )
		{
			if( val instanceof MCTBase )
				 ((MCTBase ) val).getDeviceMeterGroup().setMeterNumber( getJTextFieldMeterNumber().getText() );
			else if( val instanceof IEDMeter )
				 ((IEDMeter) val).getDeviceMeterGroup().setMeterNumber( getJTextFieldMeterNumber().getText() );
			else //didn't find it
				throw new Error("Unable to determine device type when attempting to set the Meter Number");
		}
		
		if (com.cannontech.database.data.pao.DeviceClasses.getClass(device.getPAOClass()) == com.cannontech.database.data.pao.DeviceClasses.TRANSMITTER)
		{
			IDatabaseCache cache =
				com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized (cache)
		{
				java.util.List routes = cache.getAllRoutes();
				DBPersistent oldRoute = null;
				String type = null;
	
				for (int i = 0; i < routes.size(); i++)
				{
					oldRoute = com.cannontech.database.data.lite.LiteFactory.createDBPersistent(((com.cannontech.database.data.lite.LiteYukonPAObject) routes.get(i)));
					try
					{
						com.cannontech.database.Transaction t =
							com.cannontech.database.Transaction.createTransaction(com.cannontech.database.Transaction.RETRIEVE, oldRoute);
						t.execute();
					}
					catch (Exception e)
					{
						com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
					}
					
					if (oldRoute instanceof com.cannontech.database.data.route.RouteBase)
					{
						if (((com.cannontech.database.data.route.RouteBase) oldRoute).getDeviceID().intValue()
							== previousDeviceID)
						{
							type = com.cannontech.database.data.pao.PAOGroups.getPAOTypeString( ((com.cannontech.database.data.lite.LiteYukonPAObject) routes.get(i)).getType() );
							newRoute = com.cannontech.database.data.route.RouteFactory.createRoute(type);
	
							hasRoute = true;
							break;
	
						}
					}
				}
				
				if (hasRoute) 
				{
					newRoute.setRouteID(paoDao.getNextPaoId());
					newRoute.setRouteType( type );
					newRoute.setRouteName(nameString);
					newRoute.setDeviceID(
						((com.cannontech.database.data.route.RouteBase) oldRoute).getDeviceID() );
					newRoute.setDefaultRoute(
						((com.cannontech.database.data.route.RouteBase) oldRoute).getDefaultRoute() );
					newRoute.setDeviceID(device.getDevice().getDeviceID());
					
					if( type.equalsIgnoreCase(com.cannontech.database.data.pao.RouteTypes.STRING_CCU) )
					{
						((com.cannontech.database.data.route.CCURoute) newRoute).setCarrierRoute(((com.cannontech.database.data.route.CCURoute) oldRoute).getCarrierRoute());
						((com.cannontech.database.data.route.CCURoute) newRoute).getCarrierRoute().setRouteID(newRoute.getRouteID());
					}
	
					/*//put the route as the second place in our Vector
					objectsToAdd.getDBPersistentVector().insertElementAt( newRoute, 1 );*/
				}
			}
	
		}
	
		if (getPointCopyCheckBox().isSelected())
		{
			java.util.Vector devicePoints = new java.util.Vector();
            
            com.cannontech.database.data.point.PointBase pointBase = null;
            List<LitePoint> points = DaoFactory.getPointDao().getLitePointsByPaObjectId(previousDeviceID);
            for (LitePoint point : points) {
                pointBase = (com.cannontech.database.data.point.PointBase) com.cannontech.database.data.lite.LiteFactory.createDBPersistent(point);
                try
                {
                    com.cannontech.database.Transaction t =
                        com.cannontech.database.Transaction.createTransaction(com.cannontech.database.Transaction.RETRIEVE, pointBase);
                    t.execute();
                    devicePoints.addElement(pointBase);
                }
                catch (com.cannontech.database.TransactionException e)
                {
                    com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
                }
            }	
			
			//int startingPointID = DaoFactory.getPointDao().getNextPointId();
			Integer newDeviceID = device.getDevice().getDeviceID();

			for (int i = 0; i < devicePoints.size(); i++)
			{
				((com.cannontech.database.data.point.PointBase) devicePoints.get(i)).setPointID(DaoFactory.getPointDao().getNextPointId());
				((com.cannontech.database.data.point.PointBase) devicePoints.get(i)).getPoint().setPaoID(newDeviceID);
				objectsToAdd.getDBPersistentVector().add(devicePoints.get(i));
			}
			hasPoints = true;				
		}
		
		//user can input new phone number; otherwise they may later control/scan the wrong device
		if( val instanceof RemoteBase)
		{
			 if(getJTextFieldPhoneNumber().isVisible())
			 {
				  ((RemoteBase)val).getDeviceDialupSettings().setPhoneNumber(getJTextFieldPhoneNumber().getText());
			 }
		}
		
		if (hasPoints || hasRoute || isCapBank)
		{
			if(hasRoute)
				objectsToAdd.getDBPersistentVector().add(newRoute);
			return objectsToAdd;
		}
		else
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
		getPointCopyCheckBox().addItemListener(ivjEventHandler);
		getNameTextField().addCaretListener(ivjEventHandler);
		getAddressTextField().addCaretListener(ivjEventHandler);
		getJTextFieldMeterNumber().addCaretListener(ivjEventHandler);
		getJTextFieldPhoneNumber().addCaretListener(ivjEventHandler);
	}
	
	
	private javax.swing.JLabel getJLabelRange()
	{
	   if( jLabelRange == null )
	   {
	      jLabelRange = new javax.swing.JLabel();
	      jLabelRange.setFont(new java.awt.Font("dialog.bold", 1, 10));
	      
	      jLabelRange.setMaximumSize(new java.awt.Dimension(250, 20));
	      jLabelRange.setPreferredSize(new java.awt.Dimension(220, 20));
	      jLabelRange.setMinimumSize(new java.awt.Dimension(220, 20));            
	   }
	   
	   return jLabelRange;
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
			setLayout(new java.awt.GridLayout());
			setSize(350, 200);
			add(getJPanelCopyDevice(), getJPanelCopyDevice().getName());
			initConnections();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		getJTextFieldPhoneNumber().setVisible(false);
		getJLabelPhoneNumber().setVisible(false);	
		// user code end
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return boolean
	 */
	public boolean isInputValid() {
		if( getNameTextField().getText() == null   ||
				getNameTextField().getText().length() < 1 )
		{
			setErrorString("The Name text field must be filled in");
			return false;
		}
	
	   if( getAddressTextField().isVisible() )
	   {
	   		if( getAddressTextField().getText() == null
	        	|| getAddressTextField().getText().length() < 1 )
	   		{
	   			setErrorString("The Address text field must be filled in");
	   			return false;
	   		}
	   	
			try 
			{
		      	long addy = Long.parseLong(getAddressTextField().getText());
		      	if( !com.cannontech.device.range.DeviceAddressRange.isValidRange( getDeviceType(), addy ) )
		      	{
		        	setErrorString( com.cannontech.device.range.DeviceAddressRange.getRangeMessage( getDeviceType() ) );
		
		         	getJLabelRange().setText( "(" + getErrorString() + ")" );
		         	getJLabelRange().setToolTipText( "(" + getErrorString() + ")" );
		         	return false;
		      	}
		      	else
		         	getJLabelRange().setText( "" );
	   		}
	   		catch( NumberFormatException e )
	   		{} //if this happens, we assume they know what they are 
	   	   	// doing and we accept any string as input	      
		}
	
		return true;
	}
	
	
	/**
	 * Method to handle events for the ItemListener interface.
	 * @param e java.awt.event.ItemEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public void itemStateChanged(java.awt.event.ItemEvent e) {
		// user code begin {1}
		// user code end
		if (e.getSource() == getPointCopyCheckBox()) 
			connEtoC3(e);
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
			DeviceCopyNameAddressPanel aDeviceCopyNameAddressPanel;
			aDeviceCopyNameAddressPanel = new DeviceCopyNameAddressPanel();
			frame.getContentPane().add("Center", aDeviceCopyNameAddressPanel);
			frame.setSize(aDeviceCopyNameAddressPanel.getSize());
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
	public void setValue(Object val ) 
	{
		int deviceClass = -1;
		
		if( val instanceof DeviceBase )
	   {
			deviceClass = com.cannontech.database.data.pao.DeviceClasses.getClass( ((DeviceBase)val).getPAOClass() );
	      setDeviceType( com.cannontech.database.data.pao.PAOGroups.getDeviceType( ((DeviceBase)val).getPAOType() ) );
	   }

		//handle all device Address fields
		boolean showAddress = 
				(val instanceof IEDBase)
				 //|| (val instanceof ICapBankController)
				 || (deviceClass == com.cannontech.database.data.pao.DeviceClasses.GROUP)
				 || (deviceClass == com.cannontech.database.data.pao.DeviceClasses.VIRTUAL)
                 || (deviceClass == com.cannontech.database.data.pao.DeviceClasses.GRID);
	
		getAddressTextField().setVisible( !showAddress );
		getPhysicalAddressLabel().setVisible( !showAddress );
	
		
		//handle all meter fields
		boolean showMeterNumber = (val instanceof MCTBase) || (val instanceof IEDMeter);
		getJTextFieldMeterNumber().setVisible( showMeterNumber );
		getJLabelMeterNumber().setVisible( showMeterNumber );
	
	
		setPanelState( (com.cannontech.database.data.device.DeviceBase)val );
	
	
		int deviceDeviceID = ((com.cannontech.database.data.device.DeviceBase)val).getDevice().getDeviceID().intValue();
	
		
		IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		synchronized(cache)
		{
            List<LitePoint> points = DaoFactory.getPointDao().getLitePointsByPaObjectId(deviceDeviceID);
            if(points.size() > 0) {
                getPointCopyCheckBox().setEnabled(true);
                getPointCopyCheckBox().setSelected(true);                
            }
		}
	}


   private void setPanelState( com.cannontech.database.data.device.DeviceBase val )
   {

      Integer addressHolder;
      
      if( val instanceof CarrierBase )
      {
      	 addressHolder = new Integer(((CarrierBase)val).getDeviceCarrierSettings().getAddress().toString());
         if(val instanceof Repeater900 || val instanceof Repeater902)
         	addressHolder = new Integer(addressHolder.intValue() - 4190000);
         getAddressTextField().setText( addressHolder.toString() );
      }
      if( val instanceof IDLCBase )
         getAddressTextField().setText( ((IDLCBase)val).getDeviceIDLCRemote().getAddress().toString() );
   
   	  if( val instanceof DNPBase )
   	  	 getAddressTextField().setText( ((DNPBase)val).getDeviceAddress().getSlaveAddress().toString() );
   
      if( val instanceof MCTBase )
         getJTextFieldMeterNumber().setText( ((MCTBase)val).getDeviceMeterGroup().getMeterNumber().toString() );
   
      if( val instanceof IEDMeter )
         getJTextFieldMeterNumber().setText( ((IEDMeter)val).getDeviceMeterGroup().getMeterNumber().toString() );

      if( val instanceof Ion7700 )
      {
         getPhysicalAddressLabel().setText("Slave Address:");
         
         getAddressTextField().setText( 
            ((Ion7700)val).getDeviceAddress().getSlaveAddress().toString() );            
      }
      
      if( val instanceof Series5Base )
      {
      	 getAddressTextField().setText( ((Series5Base)val).getSeries5().getSlaveAddress().toString());
      }
      
      if( val instanceof RTCBase )
      {
      	 getAddressTextField().setText( ((RTCBase)val).getDeviceRTC().getRTCAddress().toString());
      }
   
	  //user can input new phone number; otherwise they may later control/scan the wrong device
		if( val instanceof RemoteBase)
		{
			if(((RemoteBase)val).hasPhoneNumber())
			{
				getJTextFieldPhoneNumber().setVisible(true);
			  	getJLabelPhoneNumber().setVisible(true);
			  	getJTextFieldPhoneNumber().setText(((RemoteBase)val).getDeviceDialupSettings().getPhoneNumber());
		 	}
			else
			{
			  	getJTextFieldPhoneNumber().setVisible(false);
			  	getJLabelPhoneNumber().setVisible(false);
			} 
		}
		
       if( val instanceof ICapBankController )
       {
         	getPhysicalAddressLabel().setText(
            (val instanceof CapBankController ? "Serial Number:"
             : ((val instanceof CapBankController6510 || 
             	val instanceof CapBankController702x)? "Master Address:" : "Address:")) );
   
         getAddressTextField().setText( 
            ((ICapBankController)val).getAddress().toString() );            
      }

      if( val instanceof CapBank )
      {
         getPhysicalAddressLabel().setText( "     Street Location:" );

         getAddressTextField().setText( 
            ((CapBank)val).getPAODescription().toString() ); 
            
         getAddressTextField().setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_CAP_BANK_ADDRESS_LENGTH));           
      }


      getNameTextField().setText( val.getPAOName() + "(copy)" );      
   }
   private void setDeviceType( int devType_ )
   {
      deviceType = devType_;
   }
   
   private int getDeviceType()
   {
      return deviceType;
   }
   
   /**
    * Helper method to check that an address for an mct is unique
    * @param address - Address to check
    */
    private void checkMCTAddresses(int address) {

        String[] devices = DeviceCarrierSettings.isAddressUnique(address, null);
        if (devices.length > 0) {
            String devStr = new String();
            for (int i = 0; i < devices.length; i++) {
                devStr += "          " + devices[i] + "\n";
            }

            int res = JOptionPane.showConfirmDialog(this,
                                                    "The address '"
                                                            + address
                                                            + "' is already used by the following devices,\n"
                                                            + "are you sure you want to use it again?\n"
                                                            + devStr,
                                                    "Address Already Used",
                                                    JOptionPane.YES_NO_OPTION,
                                                    JOptionPane.WARNING_MESSAGE);

            if (res == JOptionPane.NO_OPTION) {
                throw new CancelInsertException("Device was not inserted");
            }
        }
    }
   
   /**
     * Helper method to check meternumber uniqueness
     * @param meterNumber - Meternumber to check
     */
   private void checkMeterNumber(String meterNumber) {
        List<String> devices = DeviceMeterGroup.checkMeterNumber(meterNumber, null);

        if (devices.size() > 0) {
            StringBuffer deviceNames = new StringBuffer();
            for (String deviceName : devices) {
                deviceNames.append("          " + deviceName + "\n");
            }

            int response = JOptionPane.showConfirmDialog(this,
                                                         "The meternumber '"
                                                                 + meterNumber
                                                                 + "' is already used by the following devices,\n"
                                                                 + "are you sure you want to use it again?\n"
                                                                 + deviceNames.toString(),
                                                         "Meternumber Already Used",
                                                         JOptionPane.YES_NO_OPTION,
                                                         JOptionPane.WARNING_MESSAGE);

            if (response == javax.swing.JOptionPane.NO_OPTION) {
                throw new com.cannontech.common.wizard.CancelInsertException("Device was not inserted");
            }
        }
    }
}