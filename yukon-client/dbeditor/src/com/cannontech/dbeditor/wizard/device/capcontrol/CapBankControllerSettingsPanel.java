package com.cannontech.dbeditor.wizard.device.capcontrol;

import java.awt.Dimension;

import com.cannontech.database.data.capcontrol.ICapBankController;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceFactory;

/**
 * This type was created in VisualAge.
 * @deprecated use com.cannontech.dbeditor.editor.device.DeviceScanRateEditorPanel instead
 */
 
public class CapBankControllerSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.CaretListener 
{   
	private int cbcType = com.cannontech.database.data.pao.PAOGroups.INVALID;
	
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjNameTextField = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JLabel ivjRouteLabel = null;
	private javax.swing.JLabel ivjSerialNumberLabel = null;
	private javax.swing.JTextField ivjSerialNumberTextField = null;
	private javax.swing.JComboBox ivjRouteComboBox = null;
/**
 * Constructor
 * @deprecated use com.cannontech.dbeditor.editor.device.DeviceScanRateEditorPanel instead
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CapBankControllerSettingsPanel() {
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
	if (e.getSource() == getSerialNumberTextField()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (4/30/2002 1:16:35 PM)
 */
private void checkCBCSerialNumbers( int serialNumber_ )
{
	try
	{
		String[] devices = com.cannontech.database.db.capcontrol.DeviceCBC.isSerialNumberUnique(serialNumber_, null);

		if( devices != null )
		{
			String devStr = new String();
			for( int i = 0; i < devices.length; i++ )
				devStr += "          " + devices[i] + "\n";

			int res = javax.swing.JOptionPane.showConfirmDialog(
							this, 
							"The serial number '" + serialNumber_ + "' is already used by the following devices,\n" + 
							"are you sure you want to use it again?\n" +
							devStr,
							"Serial Number Already Used",
							javax.swing.JOptionPane.YES_NO_OPTION,
							javax.swing.JOptionPane.WARNING_MESSAGE );

			if( res == javax.swing.JOptionPane.NO_OPTION )
			{
				throw new com.cannontech.common.wizard.CancelInsertException("Device was not inserted");
			}
			

		}
		
	}
	catch( java.sql.SQLException sq )
	{
		com.cannontech.clientutils.CTILogger.error( sq.getMessage(), sq );
	}

}
/**
 * connEtoC1:  (NameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceNameAddressPanel.eitherTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
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
 * connEtoC2:  (SerialNumberTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> CapBankControllerSettingsPanel.fireInputUpdate()V)
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
 * Insert the method's description here.
 * Creation date: (4/27/2001 3:51:49 PM)
 * @return int
 */
public int getCbcType() {
	return cbcType;
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
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
			constraintsNameLabel.gridx = 0; constraintsNameLabel.gridy = 0;
			constraintsNameLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNameLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsNameLabel.insets = new java.awt.Insets(5, 0, 5, 0);
			getJPanel1().add(getNameLabel(), constraintsNameLabel);

			java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
			constraintsNameTextField.gridx = 1; constraintsNameTextField.gridy = 0;
			constraintsNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNameTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsNameTextField.insets = new java.awt.Insets(5, 10, 5, 0);
			getJPanel1().add(getNameTextField(), constraintsNameTextField);

			java.awt.GridBagConstraints constraintsSerialNumberLabel = new java.awt.GridBagConstraints();
			constraintsSerialNumberLabel.gridx = 0; constraintsSerialNumberLabel.gridy = 1;
			constraintsSerialNumberLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsSerialNumberLabel.insets = new java.awt.Insets(5, 0, 5, 0);
			getJPanel1().add(getSerialNumberLabel(), constraintsSerialNumberLabel);

			java.awt.GridBagConstraints constraintsRouteLabel = new java.awt.GridBagConstraints();
			constraintsRouteLabel.gridx = 0; constraintsRouteLabel.gridy = 2;
			constraintsRouteLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRouteLabel.insets = new java.awt.Insets(5, 0, 5, 0);
			getJPanel1().add(getRouteLabel(), constraintsRouteLabel);

			java.awt.GridBagConstraints constraintsSerialNumberTextField = new java.awt.GridBagConstraints();
			constraintsSerialNumberTextField.gridx = 1; constraintsSerialNumberTextField.gridy = 1;
			constraintsSerialNumberTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSerialNumberTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsSerialNumberTextField.insets = new java.awt.Insets(5, 10, 5, 0);
			getJPanel1().add(getSerialNumberTextField(), constraintsSerialNumberTextField);

			java.awt.GridBagConstraints constraintsRouteComboBox = new java.awt.GridBagConstraints();
			constraintsRouteComboBox.gridx = 1; constraintsRouteComboBox.gridy = 2;
			constraintsRouteComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsRouteComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRouteComboBox.insets = new java.awt.Insets(5, 10, 5, 0);
			getJPanel1().add(getRouteComboBox(), constraintsRouteComboBox);
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
			ivjNameLabel.setText("Control Name:");
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
			ivjNameTextField.setText("CBC ");
			ivjNameTextField.setColumns(12);
			// user code begin {1}
			ivjNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_DEVICE_NAME_LENGTH));

			// apparently the setDocument clears our text, so lets just set
			// the text again
			ivjNameTextField.setText("CBC ");
			
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
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension( 350, 200);
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getRouteComboBox() {
	if (ivjRouteComboBox == null) {
		try {
			ivjRouteComboBox = new javax.swing.JComboBox();
			ivjRouteComboBox.setName("RouteComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRouteComboBox;
}
/**
 * Return the RouteLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRouteLabel() {
	if (ivjRouteLabel == null) {
		try {
			ivjRouteLabel = new javax.swing.JLabel();
			ivjRouteLabel.setName("RouteLabel");
			ivjRouteLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRouteLabel.setText("Route:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRouteLabel;
}
/**
 * Return the SerialNumberLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSerialNumberLabel() {
	if (ivjSerialNumberLabel == null) {
		try {
			ivjSerialNumberLabel = new javax.swing.JLabel();
			ivjSerialNumberLabel.setName("SerialNumberLabel");
			ivjSerialNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSerialNumberLabel.setText("Serial Number:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSerialNumberLabel;
}
/**
 * Return the SerialNumberTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getSerialNumberTextField() {
	if (ivjSerialNumberTextField == null) {
		try {
			ivjSerialNumberTextField = new javax.swing.JTextField();
			ivjSerialNumberTextField.setName("SerialNumberTextField");
			ivjSerialNumberTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjSerialNumberTextField.setColumns(12);
			// user code begin {1}

			ivjSerialNumberTextField.setDocument(new com.cannontech.common.gui.unchanging.LongRangeDocument(-999999999, 999999999));

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSerialNumberTextField;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val)
{
	DeviceBase newDevice = null;

	com.cannontech.database.data.multi.MultiDBPersistent newVal = new com.cannontech.database.data.multi.MultiDBPersistent();

	if( getCbcType() == com.cannontech.database.data.pao.PAOGroups.CBC_FP_2800 )
		newDevice = DeviceFactory.createDevice(com.cannontech.database.data.pao.PAOGroups.CBC_FP_2800);
	else if( getCbcType() == com.cannontech.database.data.pao.PAOGroups.CAPBANKCONTROLLER )
		newDevice = DeviceFactory.createDevice(com.cannontech.database.data.pao.PAOGroups.CAPBANKCONTROLLER);
   else if( getCbcType() == com.cannontech.database.data.pao.PAOGroups.DNP_CBC_6510 )
      newDevice = DeviceFactory.createDevice(com.cannontech.database.data.pao.PAOGroups.DNP_CBC_6510);
   else
      throw new IllegalStateException("CBC type of: " + getCbcType() + " not found");


   Integer serialNumber = new Integer(getSerialNumberTextField().getText());

   if( newDevice instanceof ICapBankController )
   {
      ICapBankController cntrler = 
            (ICapBankController)newDevice;

		Integer comID = new Integer(0);
		if( getRouteComboBox().getSelectedItem() != null )
			comID = new Integer( 
				((com.cannontech.database.data.lite.LiteYukonPAObject)getRouteComboBox().getSelectedItem()).getYukonID() );
			
   	cntrler.assignAddress( serialNumber );
   
  		cntrler.setCommID( comID );
   	
   	if( newDevice instanceof com.cannontech.database.data.capcontrol.CapBankController6510 )
	   {
	
	      com.cannontech.database.data.capcontrol.CapBankController6510 
	            tempController = (com.cannontech.database.data.capcontrol.CapBankController6510)newDevice;
	
	      Integer slave = null;
	/*            (getJTextFieldSlaveAddress().getText() == null 
	               || getJTextFieldSlaveAddress().getText().length() <= 0)
	            ? new Integer(0)
	            : new Integer(getJTextFieldSlaveAddress().getText());
	*/            
	      Integer postWait = null;
	/*            (getJTextFieldPostCommWait().getText() == null
	               || getJTextFieldPostCommWait().getText().length() <= 0)
	            ? new Integer(0)
	            : new Integer(getJTextFieldPostCommWait().getText());
	*/
	
	      tempController.getDeviceDNP().setSlaveAddress( slave );
	      tempController.getDeviceDNP().setPostCommWait( postWait );            
	   }
   }
   else
      throw new IllegalStateException("CBC class of: " + newDevice.getClass().getName() + " not found");
   
	
	newDevice.setDeviceID( com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID() );
   newDevice.setPAOName(getNameTextField().getText());

	newVal.getDBPersistentVector().add(newDevice);

	//a status point is automatically added to all capbank controllers

	com.cannontech.database.data.point.PointBase newPoint =
		com.cannontech.database.data.point.PointFactory.createPoint(com.cannontech.database.data.point.PointTypes.STATUS_POINT);
	Integer pointID = new Integer( newPoint.getPoint().getNextPointID() );

	//set default for point tables
	newPoint = com.cannontech.database.data.point.PointBase.createNewPoint(		
			pointID,
			com.cannontech.database.data.point.PointTypes.STATUS_POINT,
			"BANK STATUS",
			newDevice.getDevice().getDeviceID(),
			new Integer(1) );

	newPoint.getPoint().setStateGroupID( new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_TWO_STATE_STATUS) );

	((com.cannontech.database.data.point.StatusPoint)newPoint).getPointStatus().setControlOffset(
			new Integer(1) );

	((com.cannontech.database.data.point.StatusPoint)newPoint).getPointStatus().setControlType(
			com.cannontech.database.data.point.PointTypes.getType(
			com.cannontech.database.data.point.PointTypes.CONTROLTYPE_NORMAL) );
	
	((com.cannontech.database.data.point.StatusPoint) newPoint).setPointStatus(
			new com.cannontech.database.db.point.PointStatus(pointID));
	
	newVal.getDBPersistentVector().add(newPoint);


	checkCBCSerialNumbers( serialNumber.intValue() );

	return newVal;
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
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getNameTextField().addCaretListener(this);
	getSerialNumberTextField().addCaretListener(this);
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
		add(getJPanel1(), getJPanel1().getName());
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
	if( getNameTextField().getText() == null   ||
		getNameTextField().getText().length() < 1 )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}
	
	if( getSerialNumberTextField().getText() == null   ||
			getSerialNumberTextField().getText().length() < 1 )
	{
		setErrorString("The Number text field must be filled in");
		return false;
	}
	
	return true;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		CapBankControllerSettingsPanel aCapBankControllerSettingsPanel;
		aCapBankControllerSettingsPanel = new CapBankControllerSettingsPanel();
		frame.setContentPane(aCapBankControllerSettingsPanel);
		frame.setSize(aCapBankControllerSettingsPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/27/2001 3:51:49 PM)
 * @param newCbcType int
 */
public void setCbcType(int newCbcType) {
	cbcType = newCbcType;
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) {
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List allRoutes = cache.getAllRoutes();
		if( getRouteComboBox().getModel().getSize() > 0 )
			getRouteComboBox().removeAllItems();

		for(int i=0;i<allRoutes.size();i++)
		{
			getRouteComboBox().addItem(allRoutes.get(i));
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
	D0CB838494G88G88G7AF854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BBEDF4D45535D142174492A4311407ED7CEC2C69A3ED73DA34E8B10FD73172D6F12DE8DDEAED7A4AEA337A3045A56F2D587216E7E67205A49072018306BAFCD940B49FA4AA06200404C8A0969624B8712964E6664EE412495C491DBBE402C15F5E675CF3664EE4A681DC7CF82C35F36F597B1CFD7659E7FF1DF387D275FADA2149DCC5082910687F5EB191D27AAEA177FDFB3540A54859C54470EFA740
	9612D1D5G4FE420E55D16DDD4CEBAADA5503E8B5A09DF4FAEFA955EAB49A9CBCEA33C4419A783E84F49BFF4CCBE4F6EADFC1EE25AFE6833D2F8EE82C8879CBE5749B87F131FD9F57CCA9D4F41E4A6E40D8B7B3C70D90D0EBB846DB3G69GCBDD2CFF8EBCB7501927379C527BFDB2AF0D2C7B59DF4DE548474013C3331B0D356BCF9BD9ECA283B857768B257A18E4202DG4045DB4860E0B7BCDB4F8C395FF3A843F58E4967D3FC1A6C98289B75DC12D4E71DD33E68F148C070BB0020AA5E1616F649FFCC728D35
	3336D7D64F491A66713983E7A41F6C9D277375DFA8217C896900F60987F1538F238C4B207D9440DA016FEEC5FCB73CBB8146BDFC5DC77E5A2A2F4B7E79E2327E97BE4B66B86BCE713075AE7504571D927CD371B63AC70E8A5C0BFC20558CE697B5G74GE8GA9G2FD15B985CF2G1EED7DDA3BB2BC2C785A7C7EE3DE277A2162143D705E5282949DF7D6F6A82A1390367F368B35B164198C58383A2B9E47F4125E44EDA1636CBD64599F1CB29747504933698D660CC1260B6503E11D30395DF167DEBD98E92B11
	F3FFC40CF32F1921D88AE34C1D7544FEF33E4E27F4A8BCF76B906E75CB5486EA615D245B8743DB288DA498704CEE67507E58CE865A3AA1EE9B76CDDC166E0205243C31BB269E96E437196DDEA6035F9BAD4BB2BA67A2AF1B33D048729845E79A70CC1633940F6D65C05BB71CDD446C4BDB05FE1C846DF400E5GCF816483D479201F0F5B588F3654DF210D1D11D46DD85063F54AAAE1E33D95DB61A907B4D56A7285B44967104FCB2AC76A774A81227789D6208E7AA346DD22366F00B9BE10FD32AAE99E45875C25
	E1D91355G6BB32DE28B76D1E5CA6D718C4BB041301FF03EF3AAAA616915825ABF7CCEC91343BD98FDFE856E49C569BCF08486704E74720DEA545789D8FF9F00DF68E1D3A572BBA52B90ADDADA3A95E5A8686F411510E5BE547397B4F6A4427BA29F57F16AD64495A860438A67F33C324240274363C071A5F50C7B584B07B1D6B7A85CE67EE067B653D1F6B729FD62E4CC1BC168D1182DF815E85F59500ABCD1AE561EF758BE055FB6A831FD6A4AE345F7261C43F20D7CB7093D32D8F12FD2C056CC00D5C2478915
	B6E6334724004C2D74348A86A6BB439AE7F27E3435C45727C5487EB84565813FF2BF57193D55EE68F72DF2CF2E3391BA8772AF3C8EF3B05DDC2C42FCA4DF14FDDA076C17FDCEF88968344BD5E8F33A3F3A94B1E69A457B413F5B41218282BF3B8AFDD052B455539F54E41ED185FD9E1DAB906C7707C3831FEBC1952ED199857F967DEF2BC25F00146D1369A609A1BA7DDB54B7E52FBC8CC237ABC11F4670F7D723FD8F49E3FAEC38379A0B200B12B708F106558C1952C6DC036A99062D47D0C5967951EFD3AFD88D
	BABA84B82CFD7C70CC74F39F9C5D4DFDF04F255B49328BFB4D31F248023BBB4C2B7D4CEF727DE19FE4B2AD5C9ADB76B49AA3F4FC0049FA7AG464D8EDA447C9D22DE1B53294A81D629F05BEF5D067C2CA359C535G9DA35C76EBDEC5BA0FAF3A5137E345C3D5AAF43D41DA0EFBA30F84E5884B9848CE897D15DBAB22E867F5DDB23A550A3A749AE2605A911A8F0F705A7093E8EBGA9A3CC1F2BC618BED83F1F0AFE2DAA54B56A44FECC8F45B467472A735CC1CFDDD073E08415B4B0D6FFD0B354F1AF53D80CBCB4
	1557EBEFBAD0DF9950DE8E702C4A777671B2DE2F258C25115CBB37472DD35729CCB6EBA03C3736E93CDE1B319D673483EDA04077E23E5518B35A828145612169CA576331ED463DE7388F363D82CF17C78DE8DD601CA12E738F37213C184FF4341E5FEB0F707CBE8E73E681F4EB08EFBEDAE510FD8D602BGFF884042E4BB02F95DA12932D6C77F32B439EFD03AA8056A82236048F5B4CA5435A375B83E6E5777E5797E8AA623CCB52F0F5CFF1E57DCF7D49E5567DD794F8BC96B171566D8B5702C4DBB4CA91ADE83
	EBE11D0EFF3F48326633BF9733757DD12C2FA418DDF4B44857B7E20A353ED010476B023E68FAB1A9984EE9E94109790D55D15F22F612950C2E756E2578654168DA2F5141EB3D9C209D3A48F37FCBD598D796C1FB8940AA00BC0012D1282DC639FDBC312D09CCD1DBF5013D2BBE49FBAEA208729AE73B0545BD3DBDF7FBA10FFB04671403C7227550B19A39763E28B69BF7F442383A28F6E254B8E69BFB7755F01FB518D2CB4B499E48AB1D9E596BE4F1B757E10C3B0BC6D95D348C1EB9G4C0F070E22FF20CD2D99
	657EFA3953682FCBB79FB4EF0C632FFBC2CC3E0ED05836EA45796A4E2DA8E7A25052G16G64G6483D40D41BE0E71BD7A1F4AA3D36D91750D9E259DF0A7A0A636C767AF21537F3A463D303CB755DE5847A247B17D251BCAF49EFE2033BA7535BE4425C3FB294065CB084B05F67E98771F3B9D18D3EBAE7176E25AC69FE36DF79C1355F7B86E2B54AAEC0EB3134E71D56BB0363F666A787550066A587550666A5875D0F5759606879E465390276F281E3CDE5ADB9D2BDE5AC1577A68564D14F7A0A0872841E16253
	E93F532AC90C3A89CB87DDCF1544F3E4035E67382634B78AFFFDF13FB5363F9A7229554973E943A51ECF1FF3E15C9B00F6824082E1A77E23C6DB4D546DE0397EFCAA4AAE56DC627172009563B9799C4ECAG4DG5DG5E4F399F7E38FA4AD8F9BCB4C92C7C6E56787B1C35B576BEBF3095EB010092D49DF274DEBE3C55AAE8E10F6F5264E1D4BB6B73B3BADE13D437BC41969E276399AD5E7827374633973627356F99777420701B6F1A0CFB1074F9244E53225AD91F731A60E09F564DDEE9CC89EA04B40F63BEF7
	7571B83D827A658214G3EE61C45EBE6C35F9B45986C0283BA08F74EAFBBC879E59C3B391F0F9D077E49G19G2BG560E73B997F7204C4C5A9AF579F7B823631D7DF2D8E66F65F8397B3C1B5E47DCE6B93ACC60E50A3F4B00E739FBB535E1EC2FGDA7397BC9E4E24F9A78D5A1982D7F69AF14B211DA3F06AE99EB7F3852E560A3806AF21A67F125B6EC1EBFC5BFB579A5B765E37460EB1472D1347188F2D71EC0649371C6684BB48B670A517595E4B57B1AEF0B752FEAB211D8B4074B552476FE357812EB6016F
	7F4FAFECB34610BAE9D4EBE979C075B83BF5D35273517DFD7C2C6DCC60F164F71467A2E8E7812CCC60F9BE74864F739977ACA45D8927E2566539BD874D2B9318BD145C904E73691067591CEF7573B9BB00BEFE83629C7D465A916DF01E0965D4C6CF0D2023BEF793A37D1188BA4A7B2059C8FFA9020EBA5A210FE7EBFBECAFDF5B7A47520977DF5E0DFB66D82E2FA9CFFF1EB93F1B6C39694F45EC0F1AE9DC365D8836FBA35FB70F0B6F257DC69E13BFF1239F0EC3BB85A093E095C09EC069B45EE7087659B70D1D
	5BCE40F3E09ABB1B4D92FDFE6F6A0BA1E3B4A0EFE3BB4F504E545FD3227A209C1BF49959DC056EAB19EBC3D4FB23613D5820FFE31FB2433E339834F1GEB9B971CAD7326218EB3297F2743FB56B49E27761C46BE9AB8EAEDA2FEE349FC9DF7722EC46CBF7BF4074E531FB8B1B6D9C8785E88A1917AA06C56E5DB10486212C94853EEC5599D0AB7B86C8B90A65347B42E201F2DC964B2BDE30A147B856A336B01DEB2BD3B08492674F259DA2747164D88E7200FF3FAD8365074E8593EE11AA8DB26094B16B81D6F5F
	68047357D44046CD3F46F18FDD75B8169B6E17689D22E11D8C1FCD71E986BC0B014F0B9818813472993CFE19EF23799C5A49G69G4BGF281564F60F9608F5686B2C58D5323783BA1D1F88D211D2D2F257F5A3E3DBCDC4CE3CD5683F710A5B3DEB245BBAF154FE4765120BF73FAF6D3BCDB5F273BF0FDFDC09B87C883D8823092E075CC3E3E37375532759DF7FA34B6CD139C8374E603AF6E3CC79E8D2331D0E0B6F54AB9316E68F7725AA19F78174F02B399C077AC7D1CBF8B7BA438F89F672C5836DDC93E63C4
	BDE0CD55AD756375190E7F4FF3E5BADE9CCC85ED64AF98734F492AC7727EBDB85CAF2B11E3579E42BB1A33A0ADE72A7BE3CDF55438093C5542B2BE962C20DFB92660DD8E3B53385DED4C9B4B74352E1E456FBCE6DAE35173857DB3920BDEBA1B53FF99F1E7E21B4D68A76049E25AFD7B0810F36107FB616913C7EDBC2557F5C2205937ABA079608C22FADC3BEAED8D353646667D425652C60D5FFC8F3D72CE4CFBD2CA1F2DFF5F521FCBE76E56F97C7CFF0DBC4A976EB41B6270C85747E64DB65C9DD19FFD60B4BF
	2F64816DF53A9E288FC8789FDB6F8C7CFC2A4F7B6BD26EA7B5777D93697845CB265DF166355D244BFED3F85E3C4175453A6DD82194A00D87799CEF7DAD2C53D33DD723D3361634DE3E16741BE22FA57E7A526B42F3BFE262F231F8729389E96CFD05617D453E293E5BFC95E0F314CA57F38E960B965962575CF75AE2575C775AE2575C0BED131F5BFFE40B7F1D63A11B71BB870B2521FA7443DC584F75G36B9D03F811C184363E8774EDD04465198B17438530D67CD7C1A2A7179DD3B622FF5F857545FE0AF6E42
	F8562FE81AB20C7CB9FEFC976E172678A91261EE5805BAD2BD6E810DF55571B3F7217DF9E597C3D35BBDAA73DC690435AD1A434F8B0DFD91F704617342B1494D0E8C4C76E7493CF64F19434F8BBF24BCCB13320B76G34A6713B42FBDC5CCFFA127899E2452746BA5B76E6C14CBABB7F53FAF3D29273654C2430CF6700CF6BF7A42EB8F7A4B47A33791FF2733B3655C226072C9CD7F3B34725920E6B39196B436DFDADB6FF11A15858AAB37FFDCA524D3C6EF83A011ECB211DFBB3371F357B5E0467F9E9D02FB0B0
	9DABAA35A0C01ED1953F2CEAE38CCB3F6C68F67984DE8B697B79767E3D71F800951E84B1CF778F4A8E2DC7F28B3F7E49E9E30DC23E867965EB135F8332F9C99356287B20EF8740GC082C08AC08640B2009C00BCGB2B73B488AD08BF094208740899082C81A4B577F58017D132CDFB7B46AC42A64C1B752653F3B57A8FF9A704B1ABB397C7A77667A787EB733FEEA7FCB2E275F0140EFFA467C7A5DCFEABD56C40A4B9510051FAF2CAF97382E4099A56041BBA3A6FB27FCEDF5F34FB50E3B792A47313A7AG3DF3
	D9B1B73A5EEE22781C39517576D291C3F20156114C4F9777390C732783FEE97215585A7CE67465D550F78DC0793C6C22FDG2DGBD73387D54B66C1B44FED8048A1B8F136717EE23BC837374732C7ECC1EB7D1B6B66EDF2D46F1F7697D16684F15F147ADA446F139FA3FF57A336416F863FED5B0593E55449847F4722B86FC97B910058D1D765B864CE79E9F18236475DC221FB84FC1C71F1B5DC9BCC96B3106DBD8DCE638E7A86E8460760BB92A1A627B51EBCDD37B511E2658F96CC05344BCD65F94BB0F3D55E4
	4CE38C575204F15A8DF143AE7A3DAFF03D8257C1F9A26EA30FD39B907813D4AE441F10F1C20EBFC3659A25B83B0E7B0781572B63BEEE42D8GCB9ED0D4BE76C293BD7BFB3CDE56F650BE9E9F0486210F81AA2347AF99649E9638DE3197CBFB797E18846E61DE3EBFA6E1BB8DAEA25E5FB53CE74B573FE6FC663A4E41561C6D62FA407BBC06FB44457500F704FAED59BCC9ED59BC351DE6B44736536FB7CF34539FB5473653871B0DF64AE4DD676677564EDB78FD60CABB62C2501EA5F00D623EB7C5601E26354AEA
	E82F9338F59FA12EE4BE645C799C57EB67E377885C77E8CD23C1BBD1600E50FE8B201DAEF0F70831D982775F859CD710426BA1690DD2A26289F5A5062F891505715C1574BB281D83482787F87425707B315F58A36F6CEA5FC61BE577E2A7443DD8F24A4418E73C730AF5E7F7D7CA743D588B56483B38E27A5B0ECC6837D44833B1CA1E536F0465715B39BCEB6248E33C27F39A6E69E82ECC0D16E785091467B93AC725504F1A4A6569091267722130BC778BF9CE24461627F092F9BEB14843781595F2FE83B178D9
	22780D27C65EC3A69B78B1D9779CA6E22D4B6CF8DE0AF12EF6021DA8EE43211A5551E362CC3DA8155FFD1C4523024E7B2336B06F3C0F2E0E776DF64EBBC7703EF4AC4C7B490EB06F462B147B7702F7692D1CF7D9A772E374778AB93D6356496EBD9ABE40F796D73F0828F947E1DC722D1357D1EC5CBD76083A8546AC3922F1FF0C9837924664DE513876C26338F5B026646B130FE371A5D56436458237D064361D827720FDE28E4C369BF3A043954548159BFBA7767B38F0E23F8B0546FE6C7FF77C47161D30A399
	4F13C20D94349FEB3785D4D296E18E7FF67BDB3FDD795C79000594F511ED68516C0651F3C9F6620FG74DFB0F7128DFE854A51F3006FA285817AA88CE8E3DE391BD4394E622F79821A6CEC63A5DCC0A3555DB8D3BD7E122E7E04949850B0CD9D6EA685BE7C2DB529E0E42F64F35733FA6EF088FFE1EDEA6B235604DAA6A0EFA51597395489AE15BF5E3CF7674955CBAC445CC9DE71AA1233D3F2C0955DC5B6E8832A9C98D03C4E107F7D2314D149A8AA86AF26AD406EB531FCDCEC9D5E218A2107C237A5156515F5
	C2FE8E29241E7644GD6A7FAB85B87A495261255AE8DFFBB2E28FB846D063FE8EF7C7B872FFEE2A6A6FD11A3648BE28979B5D2641206BD5EB10DEC9017F181A4968386F239AF60D254E1904E96A59CA709B96A66BF5A10FA6F1FBE26F30407EA64F526CD98DA6F1615617AB6174B63138583D0AD305FA158E31F3A28BE9521F955A71FF8399FCC2103D8069B9A685FC67A77A07EED2418C60AE9BC887ADDCBA67D97CD8F71B5D37B81F9B6D3F950826ABAE19335271FFF740EE7DA572E0155F512C27AD3B3580772
	F0AFF66FBDA3A3F54746145CB938CDFE341690725D98929CF105DD34D84D3D5854E2794D1D96B3A968A2C52CA8827B454A866C1796BEDD4482C591B5BD8CEE2F684296D2E1CD938C92310593AD8B519B0C7B0B08D25AFB937D3B652A350523AA6958EA31489A26C3EB34309AA9537F73118D3FBA34E8E49373E39BD399A00A68E70116EBDAD6914B9957E7CDEFC42E097F3B368D382E127E6D7F0D244D13C9BA41FA887FCE317FCB6377117C6956385FD872EF537FBF53B743F71A7AEF9F2BE99E823C731B7C4E63
	45AA7CD0916F3E6F5C306475EA12F7284D67443B3FE3E31A9C3E7FCE2FC67E815EC7A76AF7677437F20306B17F87D0CB8788875D3FDDD494GGB4B9GGD0CB818294G94G88G88G7AF854AC875D3FDDD494GGB4B9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0E94GGGG
**end of data**/
}
}
