package com.cannontech.dbeditor.wizard.copy.device;

import com.cannontech.database.db.device.*;
import com.cannontech.database.data.*;
import com.cannontech.database.data.device.*;

/**
 * This type was created in VisualAge.
 */
 import java.awt.Dimension;
 import com.cannontech.database.db.*;
 import com.cannontech.database.data.device.*;
 import com.cannontech.database.data.capcontrol.CapBank;
 import com.cannontech.common.gui.util.DataInputPanel;
 
public class DeviceCopyNameAddressPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ItemListener, javax.swing.event.CaretListener {
	private javax.swing.JTextField ivjAddressTextField = null;
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjNameTextField = null;
	private javax.swing.JLabel ivjPhysicalAddressLabel = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JCheckBox ivjPointCopyCheckBox = null;
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
			constraintsNameLabel.gridx = 1; constraintsNameLabel.gridy = 1;
			constraintsNameLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsNameLabel.ipadx = 5;
			constraintsNameLabel.insets = new java.awt.Insets(55, 58, 7, 5);
			getJPanel1().add(getNameLabel(), constraintsNameLabel);

			java.awt.GridBagConstraints constraintsPhysicalAddressLabel = new java.awt.GridBagConstraints();
			constraintsPhysicalAddressLabel.gridx = 1; constraintsPhysicalAddressLabel.gridy = 2;
			constraintsPhysicalAddressLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPhysicalAddressLabel.ipadx = 36;
			constraintsPhysicalAddressLabel.insets = new java.awt.Insets(7, 58, 7, 5);
			getJPanel1().add(getPhysicalAddressLabel(), constraintsPhysicalAddressLabel);

			java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
			constraintsNameTextField.gridx = 2; constraintsNameTextField.gridy = 1;
			constraintsNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNameTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsNameTextField.weightx = 1.0;
			constraintsNameTextField.ipadx = 168;
			constraintsNameTextField.insets = new java.awt.Insets(53, 5, 5, 18);
			getJPanel1().add(getNameTextField(), constraintsNameTextField);

			java.awt.GridBagConstraints constraintsAddressTextField = new java.awt.GridBagConstraints();
			constraintsAddressTextField.gridx = 2; constraintsAddressTextField.gridy = 2;
			constraintsAddressTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsAddressTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAddressTextField.weightx = 1.0;
			constraintsAddressTextField.ipadx = 168;
			constraintsAddressTextField.insets = new java.awt.Insets(5, 5, 5, 18);
			getJPanel1().add(getAddressTextField(), constraintsAddressTextField);

			java.awt.GridBagConstraints constraintsPointCopyCheckBox = new java.awt.GridBagConstraints();
			constraintsPointCopyCheckBox.gridx = 1; constraintsPointCopyCheckBox.gridy = 3;
			constraintsPointCopyCheckBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPointCopyCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPointCopyCheckBox.insets = new java.awt.Insets(5, 58, 54, 5);
			getJPanel1().add(getPointCopyCheckBox(), constraintsPointCopyCheckBox);
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
			ivjNameLabel.setText("New Device Name:");
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
			ivjPhysicalAddressLabel.setText("New Address:");
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
 */
public Object getValue(Object val)
{
	DeviceBase device = ((DeviceBase) val);
	int previousDeviceID = device.getDevice().getDeviceID().intValue();

	device.setDeviceID( com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID() );

	boolean hasRoute = false;
	boolean hasPoints = false;

	String nameString = getNameTextField().getText();
	device.setPAOName(nameString);

	com.cannontech.database.data.multi.MultiDBPersistent objectsToAdd = new com.cannontech.database.data.multi.MultiDBPersistent();
	objectsToAdd.getDBPersistentVector().add(device);

	//Search for the correct sub-type and set the address
	if (getAddressTextField().isVisible())
	{
		if (val instanceof IDLCBase)
			 ((IDLCBase) val).getDeviceIDLCRemote().setAddress(new Integer(getAddressTextField().getText()));
		else if (val instanceof CarrierBase)
			 ((CarrierBase) val).getDeviceCarrierSettings().setAddress(new Integer(getAddressTextField().getText()));
		else if (val instanceof CapBank)
			 ((CapBank) val).setLocation(getAddressTextField().getText());
		else //didn't find it
			throw new Error("Unable to determine device type when attempting to set the address");
	}

	if (com.cannontech.database.data.pao.DeviceClasses.getClass(device.getPAOClass()) == com.cannontech.database.data.pao.DeviceClasses.TRANSMITTER)
	{
		com.cannontech.database.cache.DefaultDatabaseCache cache =
			com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		synchronized (cache)
	{
			java.util.List routes = cache.getAllRoutes();
			com.cannontech.database.data.route.RouteBase newRoute = null;
			DBPersistent oldRoute = null;
			Integer routeID = null;
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
					e.printStackTrace();
				}
				
				if (oldRoute instanceof com.cannontech.database.data.route.RouteBase)
				{
					if (((com.cannontech.database.data.route.RouteBase) oldRoute).getDeviceID().intValue()
						== previousDeviceID)
					{
						type = com.cannontech.database.data.pao.PAOGroups.getRouteTypeString( ((com.cannontech.database.data.lite.LiteYukonPAObject) routes.get(i)).getType() );
						newRoute = com.cannontech.database.data.route.RouteFactory.createRoute(type);

						routeID = com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID();
						hasRoute = true;
						break;

					}
				}
			}
			
			if (hasRoute) 
			{
				((com.cannontech.database.data.route.RouteBase) newRoute).setRouteID( com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID() );
				((com.cannontech.database.data.route.RouteBase) newRoute).setRouteType( type );
				((com.cannontech.database.data.route.RouteBase) newRoute).setRouteName(nameString);
				((com.cannontech.database.data.route.RouteBase) newRoute).setDeviceID(
					((com.cannontech.database.data.route.RouteBase) oldRoute).getDeviceID() );
				((com.cannontech.database.data.route.RouteBase) newRoute).setDefaultRoute(
					((com.cannontech.database.data.route.RouteBase) oldRoute).getDefaultRoute() );

				if( type.equalsIgnoreCase(com.cannontech.database.data.pao.RouteTypes.STRING_CCU) )
				{
					((com.cannontech.database.data.route.CCURoute) newRoute).setCarrierRoute(((com.cannontech.database.data.route.CCURoute) oldRoute).getCarrierRoute());
					((com.cannontech.database.data.route.CCURoute) newRoute).getCarrierRoute().setRouteID(routeID);
				}

				//put the route in the beginning of our Vector
				objectsToAdd.getDBPersistentVector().insertElementAt( newRoute, 0 );
			}
		}

	}

	if (getPointCopyCheckBox().isSelected())
	{
		java.util.Vector devicePoints = null;
		com.cannontech.database.cache.DefaultDatabaseCache cache =
			com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		synchronized (cache)
	{
			java.util.List allPoints = cache.getAllPoints();
			devicePoints = new java.util.Vector();

			com.cannontech.database.data.point.PointBase pointBase = null;
			com.cannontech.database.data.lite.LitePoint litePoint = null;

			for (int i = 0; i < allPoints.size(); i++)
			{
				litePoint = (com.cannontech.database.data.lite.LitePoint) allPoints.get(i);
				if (litePoint.getPaobjectID() == previousDeviceID)
				{
					pointBase = (com.cannontech.database.data.point.PointBase) com.cannontech.database.data.lite.LiteFactory.createDBPersistent(litePoint);
					try
					{
						com.cannontech.database.Transaction t =
							com.cannontech.database.Transaction.createTransaction(com.cannontech.database.Transaction.RETRIEVE, pointBase);
						t.execute();
						devicePoints.addElement(pointBase);
					}
					catch (com.cannontech.database.TransactionException e)
					{
						e.printStackTrace();
					}
				}
			}

			java.util.Collections.sort(allPoints, com.cannontech.database.data.lite.LiteComparators.litePointIDComparator);
			int startingPointID = ((com.cannontech.database.data.lite.LitePoint) allPoints.get(allPoints.size() - 1)).getPointID() + 1;
			Integer newDeviceID = device.getDevice().getDeviceID();

			for (int i = 0; i < devicePoints.size(); i++)
			{
				((com.cannontech.database.data.point.PointBase) devicePoints.get(i)).setPointID(new Integer(startingPointID + i));
				((com.cannontech.database.data.point.PointBase) devicePoints.get(i)).getPoint().setPaoID(newDeviceID);
				objectsToAdd.getDBPersistentVector().add(devicePoints.get(i));
			}
			hasPoints = true;
		}

	}
	if (hasPoints || hasRoute)
	{
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
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getPointCopyCheckBox().addItemListener(this);
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
public boolean isInputValid() {
	if( getNameTextField().getText() == null   ||
			getNameTextField().getText().length() < 1 )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}

	if( (getAddressTextField().getText() == null 	|| getAddressTextField().getText().length() < 1) &&
			getAddressTextField().isVisible() )
	{
		setErrorString("The Address text field must be filled in");
		return false;
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
		exception.printStackTrace(System.out);
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
		deviceClass = com.cannontech.database.data.pao.DeviceClasses.getClass( ((DeviceBase)val).getPAOClass() );
	
	if( (val instanceof IEDBase)
		 || (val instanceof com.cannontech.database.data.capcontrol.CapBankController)
		 || (deviceClass == com.cannontech.database.data.pao.DeviceClasses.GROUP)
		 || (deviceClass == com.cannontech.database.data.pao.DeviceClasses.VIRTUAL) )
	{
		getAddressTextField().setVisible(false);
		getPhysicalAddressLabel().setVisible(false);
	}
	else if( val instanceof CarrierBase )
		getAddressTextField().setText( ((CarrierBase)val).getDeviceCarrierSettings().getAddress().toString() );
	else if( val instanceof IDLCBase )
		getAddressTextField().setText( ((IDLCBase)val).getDeviceIDLCRemote().getAddress().toString() );
		
	getNameTextField().setText( ((com.cannontech.database.data.device.DeviceBase)val).getPAOName() );
	int deviceDeviceID = ((com.cannontech.database.data.device.DeviceBase)val).getDevice().getDeviceID().intValue();

	
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List allPoints = cache.getAllPoints();
		for(int i=0;i<allPoints.size();i++)
		{
			if( ((com.cannontech.database.data.lite.LitePoint)allPoints.get(i)).getPaobjectID() == deviceDeviceID )
			{
				getPointCopyCheckBox().setEnabled(true);
				getPointCopyCheckBox().setSelected( true );
				break;
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
	D0CB838494G88G88G96F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBA8DD4D467151878C38CC6C86CD6ED3251E8AC1B524426F657EDED8EE55D56B46E093BEBCE6C466D31A927E137264BEE5D56366E49B7838C98B122E00289D6943518509154B4AEA586840988D69690711F674C0399F8B3EF18770061CF765E6F674DE31881B51BF316F3AE6FFD77FE5F3D773B5F3D773B5F7706646DDB10E1316691E249A07C6FA38BA1A9A7887978590A3F9438A8B949C6CCFF7B81F6
	12EFB42443B396E8317312ECB9E4DAC5963497C3BBEFCE126D9DF84FA5B3AB1CF6F809A02782E8077BE697CEAC272CC94849246DA53733617996C0BC607039C9C6794F5D4E6778559CAF40E2A5A439837B7C60F681471D04F6A3408200E59D2C7FEAF8EE251227151F647D2E45ADA01B3E7FBB6BF664E362A92024130DED644FC2720CE5F3A74A320EE673B15150CE84404927913B63B4BCAB9A3BBBCFB9D40F5DA1F93D2AD7179DAEFB3FFBD072BB6D8E55B7E0F74AFDEE07DCDEDEC31F55007A1364112B1CCE3F
	2CE90D12D7D68851DDA8675CE896658BBC21CD5C087B07FDA8FBB93457G249878F905082F0177EBG3ADB4C77635FD57079348DBDC3163D6CCD5B91E13EB1EEB64FE56E60FC67747EBAF38FDD1B1EE6DC03D42065F5A5590A819AG940098007728CF841A5E07E755AD3DDA75F8D4EF154FF7DAF17A4F2BCED9017772F220F0DC136CD07DCEC2583AE70D160451E7A2E0635E3E6BF14CA67147058FA4B4ADA16B3FDEEF4D8CE3137571255645DD4C96AB3A02B6E132AFC4143D3EEB2C0F0E15FD0918E5AF1E2E26
	E504113D6C25A356D44EA73DBBA87BC4B72EF5ADDD6B0370EE617E41700D949FB061193F2E23BE036DE820ED6C963E5176165025A1FDBE49A9EB88EB0739C9D556C6056960D2C2F5593E9FF94FD1184CCCC317BD941FE042B3DD9E27BAE2FBA5500ABDC9B666DF0397B1FEE3209D8F1088308EE0B3C01E977AF9050FFD2769689D7AD8236457CF773A95276CA7EC6CE0735B7014833ADF2A73EA3A64F5482D125FAD5DD2E40D70BE77B723EE37460C0B263E6FG991F4ADE59AF69EE558B5CA1EAF5592F31BEF14D
	BB310FDF26549637C786819E9F91FC67B56742D311B47D224FA969F2308723AFEC46B56913DA01A3B000F7E617AF37223D4E427C5B81FC069D8ADA10DF3D6C07FCD1DE5E202A5D3D3E961C89C974221DBBE86E188A6F0FFA050D97DDC2DC3A1AE4ABD0851F6616DDA6BEB5EE872AAF7987C40CB594E20EAED5054F7C2CCD78CC4D76A7C949CB67427A8CC20B4AFCC5D1C3E3271572C43DD87B077B5AA6095BD2B5FCCC5DF92E786524B2526E117FDB46DA6D3CCCE381F4DD8A30463071C3AD054CE7CFCB1AAC3C74
	029F9CCCF686AD4E744C25F113417559E3683F0F62D300E70EAF49F652A7EC77E4E11BC97F0C3B50774EED771CA1C368F527E36F249F3351540B38AF49FD32D72F11FD325789AF9A27BDF891FD0F47ED07EA0C0923F89F44F9B58416E6603FF891E3D152F53F7BD62FAEB74B3A6E76F686690FD3D9DA6FADDFB0C588D9CBAE629C55FE08F3237FD7AEE20CB8D42FD7260BE58C61742F529815955983CAD72B3DDE1D613F5E0AFE5EAD8F709C71D72DD88475C9CAAF669BE64B2552B61C035F6D81974014C5D67AB0
	FEC7C673CDB6EA879C56BEF3611960933158DFA4E2716060C212F87310B55CDEB277499AEBF68F0F411EE0ACB21D62AF05577DE91AABB8DEE33ADEF89F73A72BDB4657ACAC86AEDEC1FE15A02785C06991B170FE9B52C51E614EDF2DAA8AB329E16BF7AE67605A48BD3DB224E7CCE875067DFEFBF9D7882D155B12518FDFC6DBAA26DC186FC7DBB66D973561D45067D10095BD4C1E1B39BDD83F5D87C4BFA73407819E75076B374E68378E5A29E17AB1FBE55E449AA1DCBD58596B36776AEE4C38128ECE6D6B5529
	95D95D779A4D5D48A3C79375DDEE9A5D87205D8E9050048F7C7DF6D15F4D69DEC0B6ACFAB7E2BD9B2B717ACE8B7AC0617D223EFB7CAA4A5C8434F4DD2CD942E5739ED325E92A43CD37B7EE6F2BD74CBE42F06DD75E04E7075B2F69F590448131B69DD7D0DF5C7FB81A5783C3C63E4B87390DGA9BA1D2FA54F24FBB46016822C85C8237CDE5D0FF500DB17BDF67C572C431EDA6D123C1D323338CB6A136CD23FEE276944DE8774B378F6042F4B4AA33B180EB235BC5B100B0F340A9A6D095CCAAEF7755373C945E8
	2EB5DC4D9C3DE32F35341759322DB7E85361E7EDE99BEF7CA6134D6F5481B13F4550E7FD2F185F4DDC737C727A587CAA7A447C1EBC0073F3C8FED937537FDCCB1CDB402E75C3C263532BC6AA1F9F13E9A992B29B01DFF41F1079570361E4A676093DE463381AF6EDDFF05FCD699B3F8733DAEF86754DADFD2175E89C45E777075623FB52C4BDDAG3428FED11F582FE34ECB06B689A4597281CA819A81FC816113BF3BF21CCCD27F55C10C793D1252BCA641F3BFDCFC9565C2CE666D782BE8A71A1309586F266C8F
	35C3CCE06C5C63C35AEC5C6CF1636CA16D55A16318BF1EEAFDCF6489537216171F2BF6490E6E53EA006F892B524CFBC2F20055F6D983D04F8170B323A50B6BF3E2C0640811DAF30ED8316318F5DB049C218CB07DE286027EDCE21C81575D403C379AE8A9GE4906482D482DC83089A94EBF46C5A47132D91755796359AF0E7A1DFD7076E2D06CDEB8E5C5BDAB40D9B973A968B86432D4521620270EB518275C72DDBD61C4C4EFF16AB6C3CFCD044CFE6513D693AFB5C38D0DDD3C3F4E5BA248E8A9D3206A00EE95D
	1FC690D7896D968337017AC4G5A31C3C257D9E9D81BACB35ADF24ED4C873C5ECC1B28DE46F1F7D25BC582A6636404B2BECBBD0E40FC71236B116B4AD357435715B557435715672E6FE4F860E1BEDD8AFA5375096B4EAB5743551DFB69DC1FFFFB87652DE932C603838B83CEFBCD5FCD42541FD802F1BB6512657B3772BEE7F4357A38614FBFBF129F5E1FCD7586C99775462AA1D1EFAC33E10EA643100781CA0705DFDC30043B77B8BB4C7C619ABCFD43E3FD03650E1B6D3877CC855A820065GEBGD207C54EC8
	3DBEE9DEBF9318A02F3FFAA372BA7F4B0D706B7CEFB730D65254DE3FC38EDD4BDF5C48B7E8416C0425841A1D75F9030E57A5FF27BC4E97ECB7738DDA2471BBEEC672973626C3874CEBDAE6444D17F24DEB10B3B2565E87C35AA7C7C4CDF12A484C6F18416FBF874D7C9AC246370734F5035F3B844FB30AB420766AD0BB0E205F149131C7C5C3FB8140AA0075A3EC2FE2BE7935FE5CFFBA7C1287265B6C139DE44DED9C7BCB0B98DB816DC6G9FC0AC40525BC266B3B528B3735EB22EFFC6FA28CF660F86F5AE990D
	D43794E752334AA82BCF369978BFD0FC3B894F6A16EF521840F6945012C7C5FE6D207975ECD4122DADCA60DEB0F22E53403D560638E550DE95A57C3E33BD325FFA5B437B2D5E9EBEBF8D36CF1C1F6EEB0F64EFCC3FE7691E369EF45BE8687CC93D188719A2F0B7683E5388ED27014B9E92E383866E9F1B4458988377EDA2462E87E0F63DCC44FDFAAA600A6F93FE72624DBD665C05A5FDF97927FE3733013B9C5F8797D21EB83E7DBE113FDE24BC67C2FBA9402A7BC4AD94B8ACEA2104A573C9C3D4FD5873520616
	E3568D77717B577B0335D0BC54C2CC66DE2B10D98974407D082B371A6BEB7457B88B5B4B99FDE48C9D57220818694F0C21233E5F361A69BF9AC3C79B6D6563595CDEB8A466366505F822FC633FA31E8593791C1279333135089C1C791BCC36C685F4BFA8188AF9FF2A6187B63116ADD345DEE04FC4FD82500E85D88A309AA04520D7D0FA56B4D853E96CBCCD4F4ABD021EE8937BBF0E4367D24EABE5EA787AA290819F39467801A1E32B891F41637833569DACA70DD830F652A1D04FCE436FDC491D389A895350EE
	2FDAEAA6991F491F88B1B08621086B38E09A4BC196C3562CFD58DF7662750613E4CDC7990B7789DF5CB4CD6449D7B2D1278E552B33BE2F5058EE0077B6G26DBEB7DC43AE51874223ACD8F518D5A7FE3B16B56BB083AB9D42557635538EEB1B4D7E09CA7C01B61AAE92ED8816F2B8CDD7E35E9B2DD4C7AEC8951A5E7C6D097A663A7B4A7207F974C907EF6135C5BF7A64FBD0EDBF25763587E335002392BE4C6F06D997EA90ABFE942337C791211BFEB0096BFC31C273F67427DB4BB9A628F2092208DA0GB0A7DA
	6CBF79D2A919246EEAD1FD8D30C1A926AD054D6F00755E3E3FFDA7D36429E55FFC02AC1F7EABCB2473E8FCB40B77D57C1952D2C471ECFED73AF0FE1B011677C0126DA4C0A30093E07881B13FF629044D6F0C532DD7693A64F0515BAAB139D6375C9FC4E3B142FC79F87A785A66DC3A28CFE201FFBC409A00648718EEB9B3E9CC59C41FAD8F0C4FD50C77D3CE348356018D52AD3CBA6578CBBA664BC6570066F6C88A3FDD25DD987D274D5B79B863B0EA0C2D580F7726FC0CC1E63425A33857B42AEE3C61758D9837
	8AE0CFFAAF5413E95ED75AE632FB216119625E2827AA9CBDF62658D3DF3CEA7E261EB3FF3FD578E7687C974F64F9FEE6F0CF5DC7F5A4B4DE459F4B23A3E89A6EB3BF4A96BED37015AF111A17FFE5A90AA0A31973BE71E0D0C6CA5716CCE60BEF6D43FC51A1A91AEC70DE50EF567F641B9F053DC7C7D0B84FA87EDCB123086B9A0BE596DF0BE375076069157B8B0D1B48DAC8444587B4498B67BB3F3B63C0C9E1E9C9E15909A350EF7C7CD3BFE1BC1F5702BC978DC2A9C04A0E895B747585E95FED7A3F11B72FBF48
	733F687C7FC0C4AE7BB2B6797BD2537BABE4326FEA1F85180CCD1F2B8C16A75638A25761AF3B4257617F6C8ADF077F58B571BD40CFDD113FBF7D074B7C7D0965B9F7G63EFB9785CDA004DG59B1C93642981163225C87884DF1E1725B99E7A71EDF71EB37AE64BB3AA24F352BEB72EF64DA975E715FD2F5DD75A0FF019F68427552D59FC5B25C6DAE34115F5D6952D9D7713D2F8B7DCF11BBA81A6DE34599E29F2B003969B1629CD056BE66CEAEF88EB8ADF53223G2B49FD99228ECA08916700BF27BC13213D85
	A0EF1638A79D3609BB0F12D9A20F2D3AEE2E1F8BBFC88FDBBFEF3EFE546A1C4562BFFAD6B0472C059C436FDCBAA25C3950DC4E6447E70A3B3B9506CE71D58237E9165843DF98A0064D723AB14EDB25AE3ECB6316267A45CA1F83D197EEF5498E3DC56AB4FC679703D8D715BC14E4BB8B20GCC85188B3094E0A540BA000DG693313EC85G25GB5GEDGAE00E100585942571214A311F4GA4DF946AF0FE490DAE477538B6CCFFC386FC928DDEDBBD9FCC402B512F7AE43FBE403074CB9F37C3CFA0327F868213
	7B6F14G7DEE85FE57B26063F7B15383385F2B9D9D1AEC444903019C83D727B52A1A9B6FF07859A7635EEA420AFB9C57FB5763D84D380B5ED9EC189DDACB3EC7719BE70756120B0D985C8C34EB3122F61EE2B34BCF847C5A58094FE2EC5D270EE0AC24C05F74B828C381AA81DAG14B86187FB870AA770839661C117E27AFCB5532CCFD49C53E58EFF46470D570D0F2BB20FDB417B2D614F64B063180EEF8EC576391DC3137B5C3BC361F3E66150781CF9E8A8FC4E7CE0481CB3992EF488F3CEA744DD1B516FC383
	F7594015D31E083B64F66AAE83DFC975C27CD999858AFCAD552B1F625AB82E5E043B4CF10DC398B7B0E5176A97E3AF8E513327DBD1D87BB26D6376C298997638C1F5F47BA4135E8E839754DB9A46B5B076D21686B84DB51CC779C2D8CA0AFB10FE1AEE060E5ECE36567974B7276CFD3F697D290C4F3FE6791B4FD5863B1319CB77304D6033F8CF44F00FD1DC4E43C9B63CFBE2F61AB59259F7670CCC6E3B0F0D04775DC5A363FDB7FEA43C6FBEBDE276DD266B4ACCF1E7D8723038E75AC5FF07D0896DC68357FFD5
	5CBDDDB3F0AB2B44588483777DFAB1B65140451D97E3D7BFAC6A8969F0F650EFE8B8B0FCC1E0FB90AF42015FA77CBB5535609148E10F08BB16A3D7475E7B6C9DC59FE377998FDD1D6C5EE74C5D0A695E87F3EF9B4890E7E2A67FF5BADFA760FDGCCDEA5894A0BBFBF193C0C8964ADG1EF57C1D71DE0F478DFE9630DD457AB24C794289F6D2BBCD078BB6768D9C465F57E48579BCD7FFF7FCFECE4BB276FB737CF3DE18C0426BA4C3A75B7094DBC84F331A66A7BB8CEEF5B445EC68BBDE1E7663C5E9D612DEC7EC
	4CED6BC8BA7ADE9D49202ED9C75240ED837873CCBC6214F373E4G376DE3F47B5B059F7EE4758FDB35B48276590357E94DF01E42ECA3BB718B9D7F39E6AD596AC3F51A818F22B47A48507481C5EEA0F99DCD7813A5CD171DD5A20FE9BA595D00120E624F000E1E15B4170EB1774786126E459F1612DD0C2CC85E4E23AC297D116AEB29EA274B0E4BCAC05FDC2AAFF2319BDCF23F3563507EF3494B530835163C29281233D6F24016DAC7366AAE3F2C39D4459970FD427CA72B9F17883DAD8D583DE7CC9FA7EB471B
	84C3696E40E3B15BD3365702EB847C64A86D095EEFB7FAB82BDD129FC4497EBA9DFFA82B7A8F9A34A93F558F7F5D276FDC33928B1FE48F39CDB282BE1D58BAA40FDB995049D663442AA1F1ABE0A0B1F9358E556F81658AC31493A4C3067D1167CB3F70973F3EC2E584076AE49F33A68CBD5AA92B1E23D59D9DEE2FECB0G53827B3D86FB6CE38F6933AB90377B5CCBEF5C82D728A1E91E52D27A3F0C7EBF067F4BA8260CE24A0E01FDD34904FF21740018B3759F433151836C3530087ABF3D7E7C932FD424EE0459
	55128C7A1B95D8071CE02FBF75FAEE497E6989DF583BED4E0687F119FC68AD81643B1D31371060EDAA7E8A6AC30ACD9B67D8084D9C6FA1085EE6DEA7C43CC5FB67507F39F7BDEB9C351B0EFD47D0F6EFF8E5A17633594F8F8B71962CDCA7D97C462B7C1E26E1E3096773194361609CF01B94B966237FFF1A7EBE3C261962FE6C482879FEEC73347C0877E3EB6771FB5AF941F3B1BB87A450FD7860FC287F670B3AFFCB436FC96473EC33C7D294DDD23A2B3CCEBC5B1E9E5065609D4A574EA3FFCD7461C4C6DBFE9E
	756EB20D79DFD0CB8788E5D75C35E493GG90B5GGD0CB818294G94G88G88G96F954ACE5D75C35E493GG90B5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG1E93GGGG
**end of data**/
}
}
