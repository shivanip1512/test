package com.cannontech.dbeditor.wizard.device.capcontrol;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;
import java.util.ArrayList;

import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.ICapBankController;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.*;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.common.gui.util.TextFieldDocument;
 
public class CapBankCntrlCreationPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener 
{
	private javax.swing.JCheckBox ivjJCheckBoxCreateCBC = null;
	private javax.swing.JComboBox ivjJComboBoxCBCRoute = null;
	private javax.swing.JLabel ivjJLabelCBCRoute = null;
	private javax.swing.JLabel ivjJLabelCBCType = null;
	private javax.swing.JPanel ivjJPanelCBCNew = null;
	private javax.swing.JTextField ivjJTextFieldCBCAddress = null;
	private javax.swing.JComboBox ivjJComboBoxCBCType = null;
	private javax.swing.JComboBox ivjControlDeviceComboBox = null;
	private javax.swing.JLabel ivjControlDeviceLabel = null;
	private javax.swing.JComboBox ivjControlPointComboBox = null;
	private javax.swing.JLabel ivjControlPointLabel = null;
	private javax.swing.JCheckBox ivjJCheckBoxShowAllDevices = null;
	private javax.swing.JPanel ivjJPanelControlDevice = null;
	private javax.swing.JPanel ivjJPanelNewCBC = null;
	private javax.swing.JLabel ivjJLabelCBCSerial = null;
	private javax.swing.JTextField jTextFieldCBCName = null;
	private javax.swing.JLabel jLabelCBCName = null;
	private boolean is7000Series = false;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CapBankCntrlCreationPanel() {
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
   
   if( e.getSource() == getJComboBoxCBCType() )
      jComboBoxCBCType_ActionPerformed(e);
      
	// user code end
	if (e.getSource() == getJCheckBoxCreateCBC()) 
		connEtoC1(e);
	if (e.getSource() == getControlDeviceComboBox()) 
		connEtoC2(e);
	if (e.getSource() == getControlPointComboBox()) 
		connEtoC3(e);
	if (e.getSource() == getJCheckBoxShowAllDevices()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTextFieldCBCAddress()) 
		connEtoC5(e);
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
 * connEtoC1:  (JCheckBoxCreateCBC.action.actionPerformed(java.awt.event.ActionEvent) --> CapBankCntrlCreationPanel.jCheckBoxCreateCBC_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxCreateCBC_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (ControlDeviceComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> CapBankCntrlCreationPanel.controlDeviceComboBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.controlDeviceComboBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (ControlPointComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> CapBankCntrlCreationPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
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
 * connEtoC4:  (JCheckBoxShowAllDevices.action.actionPerformed(java.awt.event.ActionEvent) --> CapBankCntrlCreationPanel.jCheckBoxShowAllDevices_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxShowAllDevices_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JTextFieldCBCAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> CapBankCntrlCreationPanel.fireInputUpdate()V)
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
 * Comment
 */
public void controlDeviceComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getControlPointComboBox().getModel().getSize() > 0 )
		getControlPointComboBox().removeAllItems();

	if( getControlDeviceComboBox().getSelectedItem() != null )
	{
		int deviceID = ((LiteYukonPAObject)getControlDeviceComboBox().getSelectedItem()).getYukonID();
		LitePoint[] litPts = PAOFuncs.getLitePointsForPAObject( deviceID );
		for(int i = 0; i < litPts.length; i++)
		{
			if( litPts[i].getPointType() == PointTypes.STATUS_POINT)
			{
				getControlPointComboBox().addItem( litPts[i] );
			}
		}		
	}

	return;
}
/**
 * Insert the method's description here.
 * Creation date: (11/18/2001 3:41:52 PM)
 * @return com.cannontech.database.data.multi.SmartMultiDBPersistent
 *
 *  This method will create a new CBC and add it to the Multi
 */
private com.cannontech.database.data.multi.SmartMultiDBPersistent createExtraObjects( 
		com.cannontech.database.data.multi.SmartMultiDBPersistent ogMulti, 
      CapBank capBank )
{
	//com.cannontech.database.data.capcontrol.CapBankController newCBC = null;
	com.cannontech.database.data.device.DeviceBase newCBC = null;

   
	if( getJComboBoxCBCType().getSelectedItem().toString().equalsIgnoreCase(com.cannontech.database.data.pao.PAOGroups.STRING_CBC_FP_2800[0]) )
		newCBC = DeviceFactory.createDevice(com.cannontech.database.data.pao.PAOGroups.CBC_FP_2800);
	else if( getJComboBoxCBCType().getSelectedItem().toString().equalsIgnoreCase(com.cannontech.database.data.pao.PAOGroups.STRING_CAP_BANK_CONTROLLER[0]) )
		newCBC = DeviceFactory.createDevice(com.cannontech.database.data.pao.PAOGroups.CAPBANKCONTROLLER);
   	else if( getJComboBoxCBCType().getSelectedItem().toString().equalsIgnoreCase(com.cannontech.database.data.pao.PAOGroups.STRING_DNP_CBC_6510[0]) )
      	newCBC = DeviceFactory.createDevice(com.cannontech.database.data.pao.PAOGroups.DNP_CBC_6510);
   	else if( getJComboBoxCBCType().getSelectedItem().toString().equalsIgnoreCase(com.cannontech.database.data.pao.PAOGroups.STRING_CBC_EXPRESSCOM[0]))
   	  	newCBC = DeviceFactory.createDevice(com.cannontech.database.data.pao.PAOGroups.CBC_EXPRESSCOM);
	else if( getJComboBoxCBCType().getSelectedItem().toString().equalsIgnoreCase(com.cannontech.database.data.pao.PAOGroups.STRING_CBC_7010[0]))
	   	newCBC = DeviceFactory.createDevice(com.cannontech.database.data.pao.PAOGroups.CBC_7010);
	
	//store the SerialNumber
   	Integer serialNumber = new Integer(getJTextFieldCBCAddress().getText());
   
   //routeID or PortID, depends on the CBC type
   Integer comboID = 
      (getJComboBoxCBCRoute().getSelectedItem() instanceof LiteYukonPAObject
      ? new Integer( ((LiteYukonPAObject) getJComboBoxCBCRoute().getSelectedItem()).getYukonID() )
      : new Integer(0) );

   if( newCBC instanceof ICapBankController )
   {
      ICapBankController cntrler = 
            (ICapBankController)newCBC;

   	cntrler.assignAddress(serialNumber);
   
  		cntrler.setCommID( comboID );
   }
   else
      throw new IllegalStateException("CBC class of: " + newCBC.getClass().getName() + " not found");


	newCBC.setDeviceID( com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID() );

  	String cbcName = getJTextFieldCBCName().getText();
	//if no specified name, just use the serial number in the name
   	if(cbcName == null || cbcName.length() <= 0 )
   		newCBC.setPAOName( "CBC " + serialNumber );
   	else
   		newCBC.setPAOName( cbcName );

	//get the pattern used for auto created CBC's
/*
	String name = CTIUtilities.getReflectiveProperty(
							capBank, 
							(CtiProperties.getInstance().getProperty(CtiProperties.KEY_CBC_CREATION_NAME, "CBC %PAOName%")),
							"CBC " + capBank.getPAOName() );
      
	//just in case bankName is too long for our table, use a substring of it
	newCBC.setPAOName(
				(name.length() > 50 
				? name.substring(0, 50) 
				: name) );
*/

	
	//a status point is automatically added to all capbank controllers
   	com.cannontech.database.data.point.PointBase newPoint =
   		CapBankController.createStatusControlPoint( 
   			newCBC.getDevice().getDeviceID().intValue() );

	ogMulti.insertDBPersistentAt( newCBC, 0 );
	ogMulti.insertDBPersistentAt( newPoint, 1 );


	//be sure the serial number is unique
	checkCBCSerialNumbers( serialNumber.intValue() );

	return ogMulti;
}
/**
 * Return the ControlDeviceComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getControlDeviceComboBox() {
	if (ivjControlDeviceComboBox == null) {
		try {
			ivjControlDeviceComboBox = new javax.swing.JComboBox();
			ivjControlDeviceComboBox.setName("ControlDeviceComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlDeviceComboBox;
}
/**
 * Return the ControlDeviceLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getControlDeviceLabel() {
	if (ivjControlDeviceLabel == null) {
		try {
			ivjControlDeviceLabel = new javax.swing.JLabel();
			ivjControlDeviceLabel.setName("ControlDeviceLabel");
			ivjControlDeviceLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjControlDeviceLabel.setText("Device:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlDeviceLabel;
}
/**
 * Return the ControlPointComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getControlPointComboBox() {
	if (ivjControlPointComboBox == null) {
		try {
			ivjControlPointComboBox = new javax.swing.JComboBox();
			ivjControlPointComboBox.setName("ControlPointComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlPointComboBox;
}
/**
 * Return the ControlPointLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getControlPointLabel() {
	if (ivjControlPointLabel == null) {
		try {
			ivjControlPointLabel = new javax.swing.JLabel();
			ivjControlPointLabel.setName("ControlPointLabel");
			ivjControlPointLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjControlPointLabel.setText("Point:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlPointLabel;
}
/**
 * Return the JCheckBoxCreateCBC property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxCreateCBC() {
	if (ivjJCheckBoxCreateCBC == null) {
		try {
			ivjJCheckBoxCreateCBC = new javax.swing.JCheckBox();
			ivjJCheckBoxCreateCBC.setName("JCheckBoxCreateCBC");
			ivjJCheckBoxCreateCBC.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxCreateCBC.setMnemonic('c');
			ivjJCheckBoxCreateCBC.setText("Create new Cap Bank Controller");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxCreateCBC;
}
/**
 * Return the JCheckBoxShowAllDevices property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxShowAllDevices() {
	if (ivjJCheckBoxShowAllDevices == null) {
		try {
			ivjJCheckBoxShowAllDevices = new javax.swing.JCheckBox();
			ivjJCheckBoxShowAllDevices.setName("JCheckBoxShowAllDevices");
			ivjJCheckBoxShowAllDevices.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxShowAllDevices.setMnemonic('s');
			ivjJCheckBoxShowAllDevices.setText("Show All Devices");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxShowAllDevices;
}
/**
 * Return the OperationStateComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxCBCRoute() {
	if (ivjJComboBoxCBCRoute == null) {
		try {
			ivjJComboBoxCBCRoute = new javax.swing.JComboBox();
			ivjJComboBoxCBCRoute.setName("JComboBoxCBCRoute");
			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized( cache )
			{
				java.util.List routes = cache.getAllRoutes();
				
				for( int i = 0; i < routes.size(); i++ )
					ivjJComboBoxCBCRoute.addItem( routes.get(i) );
			}

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxCBCRoute;
}
/**
 * Return the JComboBoxCBCType property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxCBCType() {
	if (ivjJComboBoxCBCType == null) {
		try {
			ivjJComboBoxCBCType = new javax.swing.JComboBox();
			ivjJComboBoxCBCType.setName("JComboBoxCBCType");
			ivjJComboBoxCBCType.addItem( com.cannontech.database.data.pao.PAOGroups.STRING_CAP_BANK_CONTROLLER[0]);
			ivjJComboBoxCBCType.addItem( com.cannontech.database.data.pao.PAOGroups.STRING_CBC_7010[0]);
			//ivjJComboBoxCBCType.addItem( "7020" );
			//ivjJComboBoxCBCType.addItem( "7025" );
			//ivjJComboBoxCBCType.addItem( "7030" );
         	ivjJComboBoxCBCType.addItem( com.cannontech.database.data.pao.PAOGroups.STRING_CBC_EXPRESSCOM[0]);

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxCBCType;
}
/**
 * Return the OperationalStateLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCBCRoute() {
	if (ivjJLabelCBCRoute == null) {
		try {
			ivjJLabelCBCRoute = new javax.swing.JLabel();
			ivjJLabelCBCRoute.setName("JLabelCBCRoute");
			ivjJLabelCBCRoute.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelCBCRoute.setText("Route:");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCBCRoute;
}
/**
 * Return the PhysicalAddressLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCBCSerial() {
	if (ivjJLabelCBCSerial == null) {
		try {
			ivjJLabelCBCSerial = new javax.swing.JLabel();
			ivjJLabelCBCSerial.setName("JLabelCBCSerial");
			ivjJLabelCBCSerial.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelCBCSerial.setText("Serial #:");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCBCSerial;
}
/**
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCBCType() {
	if (ivjJLabelCBCType == null) {
		try {
			ivjJLabelCBCType = new javax.swing.JLabel();
			ivjJLabelCBCType.setName("JLabelCBCType");
			ivjJLabelCBCType.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelCBCType.setText("Type:");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCBCType;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelCBCNew() {
	if (ivjJPanelCBCNew == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitle("New Cap Bank Controller");
			ivjJPanelCBCNew = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints18 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints17 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints20 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints21 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints19 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints22 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints23 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints24 = new java.awt.GridBagConstraints();
			consGridBagConstraints20.insets = new java.awt.Insets(4,0,3,13);
			consGridBagConstraints20.ipady = -2;
			consGridBagConstraints20.ipadx = 171;
			consGridBagConstraints20.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints20.weightx = 1.0;
			consGridBagConstraints20.gridy = 3;
			consGridBagConstraints20.gridx = 1;
			consGridBagConstraints22.insets = new java.awt.Insets(1,2,5,11);
			consGridBagConstraints22.ipady = -2;
			consGridBagConstraints22.ipadx = 171;
			consGridBagConstraints22.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints22.weightx = 1.0;
			consGridBagConstraints22.gridy = 0;
			consGridBagConstraints22.gridx = 1;
			consGridBagConstraints21.insets = new java.awt.Insets(4,8,7,0);
			consGridBagConstraints21.ipadx = 18;
			consGridBagConstraints21.gridy = 3;
			consGridBagConstraints21.gridx = 0;
			consGridBagConstraints23.insets = new java.awt.Insets(6,0,5,13);
			consGridBagConstraints23.ipady = 3;
			consGridBagConstraints23.ipadx = 198;
			consGridBagConstraints23.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints23.weightx = 1.0;
			consGridBagConstraints23.gridy = 1;
			consGridBagConstraints23.gridx = 1;
			consGridBagConstraints18.insets = new java.awt.Insets(5,8,8,0);
			consGridBagConstraints18.ipadx = 8;
			consGridBagConstraints18.gridy = 2;
			consGridBagConstraints18.gridx = 0;
			consGridBagConstraints24.insets = new java.awt.Insets(6,8,9,0);
			consGridBagConstraints24.ipadx = 16;
			consGridBagConstraints24.gridy = 1;
			consGridBagConstraints24.gridx = 0;
			consGridBagConstraints17.insets = new java.awt.Insets(0,8,10,0);
			consGridBagConstraints17.ipadx = 24;
			consGridBagConstraints17.gridy = 0;
			consGridBagConstraints17.gridx = 0;
			consGridBagConstraints19.insets = new java.awt.Insets(5,0,4,13);
			consGridBagConstraints19.ipadx = 66;
			consGridBagConstraints19.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints19.weightx = 1.0;
			consGridBagConstraints19.gridy = 2;
			consGridBagConstraints19.gridx = 1;
			ivjJPanelCBCNew.setName("JPanelCBCNew");
			ivjJPanelCBCNew.setBorder(ivjLocalBorder);
			ivjJPanelCBCNew.setLayout(new java.awt.GridBagLayout());
			ivjJPanelCBCNew.add(getJLabelCBCType(), consGridBagConstraints17);
			ivjJPanelCBCNew.add(getJLabelCBCSerial(), consGridBagConstraints18);
			ivjJPanelCBCNew.add(getJTextFieldCBCAddress(), consGridBagConstraints19);
			ivjJPanelCBCNew.add(getJComboBoxCBCRoute(), consGridBagConstraints20);
			ivjJPanelCBCNew.add(getJLabelCBCRoute(), consGridBagConstraints21);
			ivjJPanelCBCNew.add(getJComboBoxCBCType(), consGridBagConstraints22);
			ivjJPanelCBCNew.add(getJTextFieldCBCName(), consGridBagConstraints23);
			ivjJPanelCBCNew.add(getJLabelCBCName(), consGridBagConstraints24);
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelCBCNew;
}
/**
 * Return the JPanelControlDevice property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelControlDevice() {
	if (ivjJPanelControlDevice == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitle("Control Device");
			ivjJPanelControlDevice = new javax.swing.JPanel();
			ivjJPanelControlDevice.setName("JPanelControlDevice");
			ivjJPanelControlDevice.setBorder(ivjLocalBorder1);
			ivjJPanelControlDevice.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJCheckBoxShowAllDevices = new java.awt.GridBagConstraints();
			constraintsJCheckBoxShowAllDevices.gridx = 1; constraintsJCheckBoxShowAllDevices.gridy = 1;
			constraintsJCheckBoxShowAllDevices.gridwidth = 2;
			constraintsJCheckBoxShowAllDevices.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxShowAllDevices.ipadx = 4;
			constraintsJCheckBoxShowAllDevices.ipady = -5;
			constraintsJCheckBoxShowAllDevices.insets = new java.awt.Insets(5, 10, 3, 188);
			getJPanelControlDevice().add(getJCheckBoxShowAllDevices(), constraintsJCheckBoxShowAllDevices);

			java.awt.GridBagConstraints constraintsControlDeviceLabel = new java.awt.GridBagConstraints();
			constraintsControlDeviceLabel.gridx = 1; constraintsControlDeviceLabel.gridy = 2;
			constraintsControlDeviceLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsControlDeviceLabel.ipadx = 15;
			constraintsControlDeviceLabel.insets = new java.awt.Insets(3, 28, 6, 1);
			getJPanelControlDevice().add(getControlDeviceLabel(), constraintsControlDeviceLabel);

			java.awt.GridBagConstraints constraintsControlPointLabel = new java.awt.GridBagConstraints();
			constraintsControlPointLabel.gridx = 1; constraintsControlPointLabel.gridy = 3;
			constraintsControlPointLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsControlPointLabel.ipadx = 26;
			constraintsControlPointLabel.insets = new java.awt.Insets(3, 28, 12, 1);
			getJPanelControlDevice().add(getControlPointLabel(), constraintsControlPointLabel);

			java.awt.GridBagConstraints constraintsControlPointComboBox = new java.awt.GridBagConstraints();
			constraintsControlPointComboBox.gridx = 2; constraintsControlPointComboBox.gridy = 3;
			constraintsControlPointComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsControlPointComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsControlPointComboBox.weightx = 1.0;
			constraintsControlPointComboBox.ipadx = 81;
			constraintsControlPointComboBox.insets = new java.awt.Insets(3, 1, 8, 35);
			getJPanelControlDevice().add(getControlPointComboBox(), constraintsControlPointComboBox);

			java.awt.GridBagConstraints constraintsControlDeviceComboBox = new java.awt.GridBagConstraints();
			constraintsControlDeviceComboBox.gridx = 2; constraintsControlDeviceComboBox.gridy = 2;
			constraintsControlDeviceComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsControlDeviceComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsControlDeviceComboBox.weightx = 1.0;
			constraintsControlDeviceComboBox.ipadx = 81;
			constraintsControlDeviceComboBox.insets = new java.awt.Insets(3, 1, 2, 35);
			getJPanelControlDevice().add(getControlDeviceComboBox(), constraintsControlDeviceComboBox);
			// user code begin {1}
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelControlDevice;
}
/**
 * Return the JPanelNewCBC property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelNewCBC() {
	if (ivjJPanelNewCBC == null) {
		try {
			ivjJPanelNewCBC = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints26 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints25 = new java.awt.GridBagConstraints();
			consGridBagConstraints25.insets = new java.awt.Insets(3,5,2,59);
			consGridBagConstraints25.ipadx = 41;
			consGridBagConstraints25.gridy = 0;
			consGridBagConstraints25.gridx = 0;
			consGridBagConstraints26.insets = new java.awt.Insets(2,24,9,17);
			consGridBagConstraints26.ipady = -4;
			consGridBagConstraints26.gridy = 1;
			consGridBagConstraints26.gridx = 0;
			ivjJPanelNewCBC.setName("JPanelNewCBC");
			ivjJPanelNewCBC.setLayout(new java.awt.GridBagLayout());

			ivjJPanelNewCBC.add(getJCheckBoxCreateCBC(), consGridBagConstraints25);
			ivjJPanelNewCBC.add(getJPanelCBCNew(), consGridBagConstraints26);
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelNewCBC;
}
/**
 * Return the AddressTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldCBCAddress() {
	if (ivjJTextFieldCBCAddress == null) {
		try {
			ivjJTextFieldCBCAddress = new javax.swing.JTextField();
			ivjJTextFieldCBCAddress.setName("JTextFieldCBCAddress");
			ivjJTextFieldCBCAddress.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjJTextFieldCBCAddress.setColumns(12);
			// user code begin {1}
			
			ivjJTextFieldCBCAddress.setDocument(new com.cannontech.common.gui.unchanging.LongRangeDocument(0, 999999999));
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldCBCAddress;
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
	CapBank capBank = null;
	com.cannontech.database.data.multi.SmartMultiDBPersistent smartDB;
	//oh my, this is fun
	if(val instanceof MultiDBPersistent)
	{
		smartDB = new SmartMultiDBPersistent((MultiDBPersistent) val);
		smartDB.setOwnerDBPersistent((com.cannontech.database.db.DBPersistent)
		((MultiDBPersistent)val).getDBPersistentVector().elementAt(0));
	}
	else
		smartDB = (com.cannontech.database.data.multi.SmartMultiDBPersistent)val;
	
	//get the first instance of Capbank and get out!   
   capBank = (CapBank)smartDB.getFirstObjectOfType( CapBank.class, smartDB );
		
	if( getJCheckBoxCreateCBC().isSelected() )
	{
		smartDB = createExtraObjects( smartDB, capBank );

		//Assign the CapBanks control IDs to that of the CBC status points pointID
		for( int i = 0; i < smartDB.size(); i++ )
		{
			if( smartDB.getDBPersistent(i) instanceof com.cannontech.database.data.point.PointBase )
			{
				capBank.getCapBank().setControlPointID(
						((com.cannontech.database.data.point.PointBase)smartDB.getDBPersistent(i)).getPoint().getPointID() );

				capBank.getCapBank().setControlDeviceID(
						((com.cannontech.database.data.point.PointBase)smartDB.getDBPersistent(i)).getPoint().getPaoID() );
				break;
			}
		}

	}
	else
	{
		capBank.getCapBank().setControlPointID(
				new Integer(((LitePoint) getControlPointComboBox().getSelectedItem()).getPointID()));

		capBank.getCapBank().setControlDeviceID(
				new Integer(((LiteYukonPAObject) getControlDeviceComboBox().getSelectedItem()).getYukonID()));
	}	
	return smartDB;
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
 * Insert the method's description here.
 * Creation date: (12/14/2001 5:55:29 PM)
 */
private void initComboBoxes() 
{
	if( getControlDeviceComboBox().getModel().getSize() > 0 )
		getControlDeviceComboBox().removeAllItems();
	
	if( getControlPointComboBox().getModel().getSize() > 0 )
		getControlPointComboBox().removeAllItems();

	com.cannontech.database.cache.DefaultDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	boolean showAll = getJCheckBoxShowAllDevices().isSelected();

	synchronized(cache)
	{
		java.util.List devices = cache.getAllUnusedCCDevices();
		
		ArrayList lstToAdd = new ArrayList( devices.size() );
		java.util.List points = cache.getAllPoints();

		int deviceID;
		LiteYukonPAObject liteDevice = null;
		LitePoint litePoint = null;

		for( int i = 0; i < points.size(); i++ )
		{
			litePoint = 
					(LitePoint)points.get(i);
			
			liteDevice = PAOFuncs.getLiteYukonPAO( litePoint.getPaobjectID() );
			
			//System device, ignore it
			if(litePoint.getPaobjectID() == 0)
				continue;
						
			if( !showAll && !DeviceTypesFuncs.isCapBankController(liteDevice) )
			{
				continue;
			}
			
			if( litePoint.getPointType() == PointTypes.STATUS_POINT )
			{
				//expensive to call the contains() method, that is why we do this lastly
				if( devices.contains(liteDevice) ) //only add this device if it is not already used
					lstToAdd.add( liteDevice );
			}
			
		}
		
		if( lstToAdd.size() > 0 )
		{
			//getControlDeviceComboBox().addItem(liteDevice);
			java.util.Collections.sort( lstToAdd, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);
			for( int i = 0; i < lstToAdd.size(); i++ )
				getControlDeviceComboBox().addItem( lstToAdd.get(i) );
		}
		
	}

}

private void jComboBoxCBCType_ActionPerformed( java.awt.event.ActionEvent e)
{
   if( getJComboBoxCBCType().getSelectedItem() == null )
      return;
      
   boolean isDNP = getJComboBoxCBCType().getSelectedItem().toString().equals(
         com.cannontech.database.data.pao.DeviceTypes.STRING_DNP_CBC_6510[0] );
   
   getJLabelCBCRoute().setText(
         isDNP 
         ? "Port:"
         : "Route:"  );
   
   getJLabelCBCSerial().setText( 
         isDNP 
         ? "Address:" 
         : "Serial #:"  );


   getJComboBoxCBCRoute().removeAllItems();
   
   com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
   synchronized( cache )
   {
      java.util.List list = 
         (isDNP 
         ? cache.getAllPorts()
         : cache.getAllRoutes());
      
      for( int i = 0; i < list.size(); i++ )
         getJComboBoxCBCRoute().addItem( list.get(i) );
   }
   
   is7000Series = getJComboBoxCBCType().getSelectedItem().toString().equals(
		 com.cannontech.database.data.pao.DeviceTypes.STRING_CBC_7010[0] );
		 
	fireInputUpdate();

}

/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
   
   getJComboBoxCBCType().addActionListener(this);
   
	// user code end
	getJCheckBoxCreateCBC().addActionListener(this);
	getControlDeviceComboBox().addActionListener(this);
	getControlPointComboBox().addActionListener(this);
	getJCheckBoxShowAllDevices().addActionListener(this);
	getJTextFieldCBCAddress().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CapBankCntrlCreationPanel");
		java.awt.GridBagConstraints consGridBagConstraints29 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints30 = new java.awt.GridBagConstraints();
		consGridBagConstraints29.insets = new java.awt.Insets(5,2,24,7);
		consGridBagConstraints29.gridy = 1;
		consGridBagConstraints29.gridx = 0;
		consGridBagConstraints30.insets = new java.awt.Insets(5,2,4,7);
		consGridBagConstraints30.ipady = -14;
		consGridBagConstraints30.ipadx = -10;
		consGridBagConstraints30.gridy = 0;
		consGridBagConstraints30.gridx = 0;
		setLayout(new java.awt.GridBagLayout());
		this.add(getJPanelNewCBC(), consGridBagConstraints29);
		this.add(getJPanelControlDevice(), consGridBagConstraints30);
		setSize(343, 336);

		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	jCheckBoxCreateCBC_ActionPerformed(null);
	initComboBoxes();
	
	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	
	if( getJCheckBoxCreateCBC().isSelected()
		 && (getJTextFieldCBCAddress().getText() == null
		    || getJTextFieldCBCAddress().getText().length() < 1) )
	{
		setErrorString("The CBC Address text field must be filled in");
		return false;
	}

	if( getControlDeviceComboBox().isEnabled()
		 && getControlDeviceComboBox().getItemCount() <= 0 )
	{
		setErrorString("This CapBank must be assigned to a new or existing CBC");
		return false;
	}
	
	if(is7000Series)
	{
		String breakfastSerial = getJTextFieldCBCAddress().getText();
		System.out.println(breakfastSerial.length() + "whoop whoop: " + breakfastSerial.charAt(0));
		
		if(breakfastSerial.length() != 9 || !breakfastSerial.startsWith("7", 0))
		{
			setErrorString("A 7000 series CBC needs a nine digit serial number that begins with a 7");
			return false;
		}
	}
	
	return true;
}
/**
 * Comment
 */
public void jCheckBoxCreateCBC_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	for( int i = 0; i < getJPanelCBCNew().getComponentCount(); i++ )
		((java.awt.Component)getJPanelCBCNew().getComponent(i)).setEnabled( getJCheckBoxCreateCBC().isSelected() );

	getJCheckBoxShowAllDevices().setEnabled( !getJCheckBoxCreateCBC().isSelected() );
	getControlDeviceComboBox().setEnabled( !getJCheckBoxCreateCBC().isSelected() );
	getControlDeviceLabel().setEnabled( !getJCheckBoxCreateCBC().isSelected() );
	getControlPointComboBox().setEnabled( !getJCheckBoxCreateCBC().isSelected() );
	getControlPointLabel().setEnabled( !getJCheckBoxCreateCBC().isSelected() );
	
	fireInputUpdate();
	
	return;
}
/**
 * Comment
 */
public void jCheckBoxShowAllDevices_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	initComboBoxes();
   
   getControlDeviceComboBox().setSelectedIndex( 
         (getControlDeviceComboBox().getItemCount() > 0) ? 0 : -1 );

	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		CapBankNameAddressPanel aCapBankNameAddressPanel;
		aCapBankNameAddressPanel = new CapBankNameAddressPanel();
		frame.setContentPane(aCapBankNameAddressPanel);
		frame.setSize(aCapBankNameAddressPanel.getSize());
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
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) 
{

}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G97F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DD8D457157897CDA49693920D319595A3D9B15204F6E9C23716582F5A38DF6CFE583A0D5B322D5B5ACAB626E1D3533275710F91951598CD958686C58CC1D4D4C4E24099F07851100884DB37E5E09EB0B24C0CB38F9991F54F3D771DBBEF9EB30350F53F70FD07775EB9771EFB7E6E3D677E001013971216141CA388C9E9027CF3BA89FE358AC258732FA6A0AEC00CC99794BF79GBB05B0DDAABC03
	009676E2CCFA3630A9A3933EC361DB73FCCC7A7B703EDB100E581B6045C7BB86207D797BEFBF613F1D6F6F47F6B268F77709ACF8EE8788839CF9A608247DFBA7764A7815B29EA1A9D99056F711B233CE6C17F195705D8C908A9055454A2F0267965A72244A8A395C3569A1C2428F0A123791BE8A1E88E55D2CEE0B7C5488CB12923BC9DBAFD3B9C91DC078DE86C014CF914A2EDE00E7D5F3EF7739CEDBDFE92751EA35D9A5313327F440BCE8F418CACD62CDF3278884FBA7909CB6CBE5254EE8BFEF34766A605322
	F308C649EC33B69B2D22C5F07510363616E7D25E02D08D5F29E6027B261E349F8D5F31G719CFF2716608D705E8E60B2234E9F97D549BA355DDEA2C47E581A3243074EC1E626EB14592DF3F07F6FB376D17FE46492BFA4GED6F7518749200E6GBBC090401FE8DCECBFF0841EB59D124E5657E73356586D67ADA647C51BC93440FBE5A5D0E45C67E2274DE19284667B95A7CA3C48638FD83D9F0F3A9E33C958890C03704F1F91563E50181C6145A6EB434A12432EB3DBAC3D6E3689EBBB3B5CD75BF157BD635433
	6DAF84E55B218F59D2523C349D393AB8B9C16613596BEE3B2217787A887575E1F8CF126303614BA9FEC801E7B19BCDE306FC8782EDE3AF46C65BF61425A9F51610FD2C492B9DE64654A437D8188C960BDA16B7E91BB3AD2C4D2DDC164D949F2540B3D9A6D0BC79DE83342A3E18F496DF599A5207C360BB9AA096A09EA04B9A13FE98204A0AB1764501237799E34DC607F43E5FECB1098E01554D4E5D83CF51A5B90C75D627E434F60A2DC6075958E1911D02DCE6DFAE31C107C73DDC9A7B1D50468551AABA64FE6A
	B076091268F032B2C539BBC999399737187BC4E8204FAEA05F23393B097D0CCE690A5DE414C4F789C6BF1DCBFCF253588A9C01813CB33BFC1CCF6CF58D7417GA6F2BBBCCF79B50A8E98AEAAAB1BEC365EFEFB8B51C4083592BB975131E3A63CC7D85146E78A88EE2FADA63D4C06FCFE1D3BCB41C7EF6EA4629B9D37300F155512F1DAEF4318794FB60C997D3685C2496A862FB1C3E34546E2E522CD5DF7AAA9CFA2977B0E51370D329FEF17E53A7F316235B13511F21F6518FD97EA092F42C12E65G6B390D8B73
	B4ACE64F9B1DA2C669278E88B0516436B81373F735B932BDC95BE9327CDB389EE9F504AE5821DFGB48338GC26CE84B07EB5BFC549F2D6E3EEDB90F3621663F757F288DE6338FB4E41E92EF0AD6C9AF5AC52B89DE1CB2ADDFC35A137BF1170D57A922F8BB74FB9DF4B4A747FF24A1FD53A8C98EF3C73FA4DE96A549EC6DF653CF52361C7D9DF6771001EDD5E9080E368168773CFC0D06749918462DA2F59E2FA253F5344F0A96318F0456597A2D9243576713386F95EF49E34645FC12985DB4DA7A497843720888
	A331235DE16E0310A0C318D0F2037467F365FB95B6DA8E7EA67910G341C9B58B7873458B78B87678ACB7FF6B0595B5CB2F301BED93A417A4E149B6E3E49E4BAD760DDF6BD9DBBE43CB30562EC87C85C7B4EE230DF4495921EE150D6B4C06C8D6C97CB8B889D479E39C368EC968BB3AB3777E2EDB6710FF823DF04A11B8CF20D5C06115ADDAADA2BECCFC60F5692FBDA9463E324035833328EF346F810A795F0058EE6D313035904157BA3AFA7B8A1778128F032F2AE2732DC4C85AC379C70F10AF20224ACF75300
	65B4002FGE813D839199E65AA6B39FC00CF6D87797ABD7931F849789B49C33C651D5D7D6652FE49CCC6F523849D455EAFD137305CB20C4E8F04C7FBBF6610312769DC8E5F91G4B7BB12EFE388DF348605E90A1EE7E87BE736638FEA6DB6ACDF7DCE9A6E08EF96490E9D38F34161B9883278B1573D80D53E96BB45390127D774BC2E55CB15C0642F76159E5F6B825FA9898DC686BB78B093CE40E1551F24E51D917A56B5A836D06GD48C907C10B6C7A17B9A408B00AF84E0717D033A63E45C2041D84A9E4D2223
	4B666893CD072F9BEF9ACB0D83D2A99D23CAEBA839163C9723EF5EAD2B0167F5DD0F5859FB5E66221DC3541D577D2DC6452B49B733334530EC13F347F93B2BE5D9D7BEB7CB283A37BB59DBAEBFE54701640081E67F3081379F061ECFCF097F6B9FB218CD0AAE20CD9220CC098BED72D62652A6A6408782C43850A653AE7CFDB6094951333113AC0174F4CD8419DC078D6CB3CA33B0BDB6F0BD16010C09DC0F66C6EFFA145F42755A3262DDAC06C45AB3D8D2F618981B711DE3C03E862893F08B79DEEDD2478C619B
	F58B6D13DE7F775927AC35CE99B317FBEC83B59659D04ED1870E9C7F75284BCA10B3F590F549582954258A70A600A903BC7E6BC92E5DE9F408D2A97DADDB09B46AAAF58E40A4AA372BA3D4D99116A3AC9E36BE1ABD684E57A2860767EEEC5DE028A032AE9BE46B06B40E3FCC71EB94F8265BCFCE23EE71C0EB3E0D396E370A48FC99895F2BG568388C3B1699A006AA19CFBEE9DBCA10C301628073154E1B5DAAEFBA48718539FA2F60279DC7EBEF10848C167F381F3A5F31D5A8E5706BCF5F7293ED93D01E175F2
	5576D455E371F32E1548C26783051BAAAB9BB02264FCE2EA05B21F089CE26B04B5700C87E0F3448B5ACCF96CC93D03F3411DBA65DC903D63F872D6AFE3900D27BBCC3EEBF75CE3D0895FCF68ACA6735BEC20C5812C820887483E8BE391C073DD7451378FFDBC120FE85C3558F400B340BC2FD367E55C269FDD981BAF4E8E2B2776057D2EB7DFF4DBFC79C29E71E4DF0C4F77660BA940F376DD744511AD389794F997F37DB747284F66E175CAD55F71AAFD188C5BBEC399D26F218C8B8CE3136105E17554B62D3A67
	CD0620A60C4996AE0339FE0CFDECD8BD359D826FF9736B41437B3D7B358556A4F5E651E2E2FEED5C2776AB49AFC201E754BD746B391D1932BC2BG47566E4BE8EE169990133EBFGF1F3CF91DCB5FC3785205E17AA88EE883E03B96EAA4511F6483C44F0F127F16DCB4A2A7B299202C720FB835646BB3C0D11564CE92364CF000D85B1C53E5704AB0A3C2F89DFAD723EA6DCDD3413610107F22788697FD664FF4D789FC55E560C87287EAB766C203C1DCE51C987A712004B34DFC96F89DE560E170CFD22EC3B5DC2
	C85D9639CC2DE4539D63F177D671DE6FF127486BDF2E403CBEB2G737A6F51B5DDBC799E87B1B58E63A2CA2B0C7FF2409BG2E0DE3B1E09FB7BC9658587D1BA3A4EF1888749000A800D5G9B476118FD7B700873EA2D4B4F3C5AD76C5B4FD231F7BF8F9613B50953566F6894553E9CC76D46E86E5921DE927B0859D919C7E8FD49686896074542F4DA1F51FC551FD56CABDE18CFCD9714BEA5B9A273570B79587F3347233FE6B7523DFC78EE87F00DC7FF9D5D62EF3FBBE87CF0FF31368F9B14ED1F60FDF657E77E
	78057A64375603DFB96777C41312DF9454DF8990A773C97049AF24DE496FA4374D04FDE81B4C89E81B13D708ED2A613B8D40BE81ED235B29ECFB4A844F36E62B3EA3A6E00E7BDAB959033198EF597AA5C1A81BC85A78AD5FEBD98665561236813FF7A21B17D9DF7C7AG49FB3A9C46BEF073E53B58A9E4CDA2F523CEE1DD97148F848887D88990BF915BFCC8A2BA30DEFBC99C0095A143BF295D4D71BA658AC9366F728A353FDAA639756A1964AB27BEC377A507A629F3EABD4587CDD267542F94E0CE9D8A344C07
	F08EFA15660396789E6238A72F90DC90FC07F25C4FA8AE8A3E17F31C3E95672F5807F01C786D915F7D7C1DA35E7B7966A35E4773348F7D0F675BBE7455BF19ED3F132F34ED61E4D8A3CDF65B33E932AF5BE6B791DB35CFD61FFF64D2FC40E47579473CC6BC7F8881DA6A9434CF5D9634CF8F4795FF0638A80E8BEFC29FE4BE0C38C58568037D9C7786451541F7B54735E811DF534358EF266CD36ABD9B70918F3BF5DD7630AF3D9753FE3866E1265FB60E0F2678C4851E69FD16624937669168970F20CCFF26794F
	A6784E1C4A6554211C7BB92EDFCFF1705D8240F8FEF0924FE2FBG97B2957B6631F60F7C0C2C0FABABAFB84C2673466EA65A53597852F1B2D32E3FE6AA0EAFC114E7F6E0CC7AE1006AC0DC77388E613AA77C19D9C2D3C02357BD3038166349ED014CE6C3016EF5CF982CFBD81BBFBD05EDC681FDE3A041E51DD62E4349D8F0A51965220CFE4E03CE62D8ABA869F68FBA11F709C77DF99EF4E22383F2FD265BAB87D13744D742844BB74E785C5F2BF8D4DE53494F66D62DD078489FB264FECFF30FE02005BD0AFE4B
	B90B3EDC79A8465CA7B48E77CF0BC92F1A0638860AA37B30E48F0261FED850A617A3EDD5CD9BFDEE6A8FD89B3FA940330C3166267E000D6B3F5412FD7FAE98510326911BA4D2DDDB603DFD9A4E890B691859E795A1E1B2F732F24BA86E7BG313C5C5BD788AF898646F553483D0A39A415908CDFA1657F522A9FC196A59C06BA8434322F2B60FD1C7C4EFC77A31A1B8481BE74AB684F17346863659CF772A07AB87EAB683BBDF41F2CAA88F48488G8881D89A0474C2CAA77B5F6BG17BA1D6D19137DF5C68FEBD4
	1E5791BE789E92F4FFF190A017EB175B9D6E7B6FFA3421DC375C6FF9D906A2EE5467DF4CA77F33451FCF52844C6D9994CEF77B044E5370BDDF768963D77D59C87CB2947C74AAFEED8AFEAC46D653794484F8BBG4345523963D7445653793E2656B36E161DF047DD2F76FE628E75D447DD44F4B6467EEE73664DAC1E66503CAF9A702B26E30C4D9BA23884784EFC8CF179F7B16ECA9E4338F9618A493F0660FBA6C0A4A98B10G107DB8BFEF26E5CA603B69F1F636E201E7A0C0B8AFD3F3255D0FCE046FF6C5BB31
	0F3963AB61317B0BCF37BC577C3605B106B6D446667D1E39EE93F0AC6AE8759C0BA65079F7A54832860065303DBA4CE13302E352B5G55G6DG8141988F01FAA2D7274D525FE7F54A31B41F4E51E40D98920C3AE9CE0E6DEECF69986BF50F3A9E4BDBBACE11F9BFA21865ADC99C5FCB71518ABC4BBD1E643947F22015BC017B4E8B695AA1883E438116812C85C884587BA4663D73BFAC93C6D89F37586CCD1098DB9429AC536F972746F667693B99B847C73EB88FF273F712FC6D5B16BC496237C9FE2EEF51D2BC
	532F122EEF0600B693A09AA096E0BD00B083755B70E189532F56E416EAA4495859C3CF6FD039D633B860C6134513BC37D58CDF0BEDBB033118B3032D45CA60D9BD036D253171674333A30DBF4AB1368D6ADAE6307107FC874CE03E7DEF98FF189C5F22677B8D434E6D308F3DF6403DFE83B4AC61581828BDAB70FE76481582F8DA45818DBF222903CEF73840E4B6DAEC5D85A51A32924D3172E274CF480072EEE2453B273D1E5D93881C417C92A2BF23A7EBD12EAB64D954D9A473FC75AFCA1E5933721313FC70DC
	A373DAB7431D3366D11E3076965CBFCC4FA567849ED77F1E05F135FF5153027E476FA4E9FD3431F7A6635DB3535D467A6B0999CC765769DD088E1B03AE5A186FD38B5077A1D0A78AE0A3C06AD3ECEF1315D92745B27B1FE271D18ECF03DC0645C753415164C56DD0D7FA4A9D9FD3607DF5080DB7F8FC84FF7A3EB2BEBC4F68E4DF8CD4119872D845B799BB482588C61FDE3553CDEF36192D12CC16690B2AF2D4F4BCB4E0746FD53D27664FCF95E06C2267FB8FF91CD5864BB609FA8A4F7FEEB6FA232F0467F05FEF
	7C0479FEDFB57972CDB7543863A4C1676E8FF38A6E37BF30729B8AE8ACBB0BE4DE6F573AF9FDF35F70D859B008F19077940B15CD704C1A455693F23F9E9ABE3E2C9A42FA05D0368A408EB0D12E476227626EE8461759D0B7E216BBFE1641FBA32C475E6272C73412793801BA9C460F16DBF6775E6F662DBB14344B22836C03B1B3C87C65BEE5GFA0D4964901D72FD1C99BBF752381463G481F0326EE5E3FBE3BDD49DBA6B2DAC0DE362AAE910B454926AB6AB8A136D9B58B634815670D3E0E53D76955E771043E
	0953DB6B3C554FFA9A6909F95E6AEB004EE263058B6E583820BB68BD4E1CC62B5389566CD2041A3C4FB8DD07EB727227F1CD7E9D1DFACDCE4E42CB85DCD33F29D32F49C58F3AD62FDE13BF112424DF512B5764B3647ACC0F977590B24231631857741CA2BA32779B0A7759A747F22FF1F4B955B79EE89BACE708A8773D97780F655E77823FDD6EFDAF7065F27FE7B72F147B3E6F77AF654A7BFEAC6719E0A03E32C0ECCC818887D88A907BB466BB6DA74958C6EE9E8F4FF5EACD5D644C015CB616787D40D33EF5ED
	B8B5729D654B27487D278E1BA4597A88FF44DFBDC57CA5596C9449F0FFBDC5EC64B0F777C82C280CB751FDA80B58C55172DD1AB31853EE845D8E4F46B34A931558BF5AE763BE1931125E95036F880065333D4F993E6709BD8D82376F4B8D049E87BC92E723CDFB4EFE884FD663F5B9BBA629244DC12D8A48E6074DAEBA24DB8CCBEF7C493C7E29D160BABEF77620AF9E601986989FBE6D38AEF6CAAD46EEAECBC293996B77FF95469E00EBG924094009000C80065GEB8192G323E866390C0B90081208D408290
	70B554A361DC319FBD64CDC99AA08E231904083C3EA8A940BB7B4140EB6557F0EF73D78788BF7F777DDA4C12C5BCAFCF0F7298A79570337BB96843CC3A7FDF855FADG16B9382F1AD508F1B091F051F3B08EDA8AB18E92B80F7134ECC9C8CC3A9E60DA48E863E055A572A51FEFF679B85F24B39613615BCDF8A6A40520DC0B9B91979E02BA6C3E0D76DB87387DF3316CDFEE239DDA66228EEF503201709D8A90BD97F9EC9AC2BB4482AEEB9E7A203B60205287660E3E527E1BFD25468E29D48477D6D6DE6A37D81A
	64B4C336FF759D341DFE9E36EB3DC34FE560BB98A0FC5EE8ED7756278267FF3495ED30EC9E6A3AA48FF1099C17D10B7A174FC75C23F5D84E42F1A7B4D8AEF4BE2E651F540DF66D497A40D4036F716EB14348635D5386FA879446A91213B0DC080164F2362EAE274847558506EC0E2BF7B65B1CE6F2CEAB5F15B9B3367D044DE32CD7D1B936FD0855F4DCDDBBDF3D8F71D30ADFBFDF3D8F51DD09B11AG3436D09CF716B7901EFC8EE4C3A45E09E8A07319598A66B1DA4C03743A6DE5A8E86DE6744C0B3E7D356362
	487E5AFB517B7C14FBF1787C243D68FDFEAA3E281C1F982E74A269FF5DB0E63671F2A7B86EAA47D5D01E84770559A475F07CB9AA97419BC452A06275D42E810AEB13F1758A5CD599F769A209B9D039476660F371C53A1FE636D8643FCF22E54CD688C1EE0FEBD4C6335D28105B48F1D7F94C9CAF98A9E622C33D6DE3D70FD8CFFD370A553BB5E2BDA52C0136D9AC8E51F9A3BED49D2313A8FED328BAC637EBB1C6D397442477AC4071E4D761587A16E60C752E0E3A9E536BEDBA472DDC2056F7B3452FD92056F78A
	1FBF57814D708C5FF776389F917E0C7C370572B37619617B462C5E730D4AFA6B65F20972B3FB212FFA7BEE0FAD1E0AC62C67BD1E5AC72C2704420598CF1DF41E2DD828362F0562758B55767D2F5BE85FE620C5AEC47B7EF1E8ECF111F5E7EC75EAC7DD0F6975F3BA6F6F8DD36B3B01628B43547ACAF7D05FB2208507213E652D4A76B705B1DFE6BDAB6F2FBD6BABAEA273BC6EFD49658C72731A4FFAD9357E74357B2C771847DD6609F2391972B35CE73D8A0D3F76167A2C77DA25405F57A95E7BAA9F7CDA792187
	5A863BC338F4B759BFB2DBADE62B28E3FF15AA6019ED904D73E341B66B1F45B8992FF55BE02A62BDC77B606D71517FC39B0905EE1D528E3A5F2795BE783697BE50B618CF7F152EF992401F297F00E71F036CEFF9617BB0477D2CG4FC34BB9EE85DD9BCD046FE00E1BC54F754360BB0A63B667E35DE59CB7C057CAB90B200DC57C2E75105B3E8E457B9FEEBFF8DB678CBD78B6565FF16B742662FD704E03EF7B71875A8673E9E3AB5E37AADF04FE0EBBC0F08678EE63385ADC0C8793473D1207F527F01CF387561D
	4DF131BB30EE9847E9EB79BDA00EFB162EBBB74237900EB893EDB7873E8BB9EE8517250C63BE55A02EA59C77779796101CDAA23B9F821EC16C790C781051B7B6F91EFD2753B5068B6ACF64BC2697F85EF3B873B9294F4E815EAE984DB9007A1EC3E4B8DB5FA7F1F9629ABD65F91B765525D0EEA517676B791E72DC3B6C16A7FFC4F9B27C481388ED1C7410E75A6D6176FB2405344764F3515849DC9C131E3398657B5D81CF7996DFA16559BD02EB39F836220772EDGAE0019GD10B712EC9820D29B5703DEEB16E
	672FEAF5735830031F7FBE87728398GFAG26BC077795AAEB31CC986022815682A4826CDF02F2AE2D7334750FE86CD5815D404B5C4E7554650FDF386508633A3820FCA0C0B840CA0078A5A8C717065F1B0808C957GD483348384C420EEA9DD6EF8345E7719982BBB11ECD34A6F766BEEBE5372C747E723554D670B9BEEBE31876E178F5BA39A1F0BFBDAA191F8EE92112BBEB7A99214679EBF4F75FF17B1A7CFFDEEF24B03DE1727BEB77913CC174F021CEEFD3AF70CCE1F1D597CBEA8576713EC7F7A5C4876FF
	8EB4BF5B3FBE2B33FD6943E4EA2FC519323F0EB2CD2B75FFB63534D6AD53339E745F567ABF1BAA2C73F5B645EC7C41CD370D5BF2C7E76374BC54474075A94F73EF632EBC7F7A4C9CA1E6FE60B3E618CC019A14A9004B7422463F0D63B5EA198EFA50F3737D5B38B95F170DD9AEB0B9F7ECEB52BD59E32BA77ADD5B792E37B5EFEC75267BDD5BF983763F478C5503602C705705B4C948E5B7F3B4A47AF4C63B067C9B8A0DFCA542A2BAA4A11BDC4D5054D8AC9A79F20653A1EC7308536F4DBD794B15BFEDF5268869
	7542BEB2BD3093F77320E8A2FF35A57FBB1EBAE10B1D5C41388C78FAA155C99FE9CE6916C5EC92F23ABEA77FFE42A909269A5CEFF5CA42FB740F330F12A31923862333C7A2FB24E71B04D4AB7947C142AEC636982D5DC759666BD99779F7C1C9B56D24F7D1158510F7B71517F0A965DCF63F3463E0FE433A2894A139CEF857E2B31A6A0C1D124DD1AFEC11FA9C223347E6B1396C1FD4D3C61983A43A49ED2F94E077A72EBED136145CA560C2773A3E9A38ED7D363AE468988E61A8ADC9E643D2DE42246BB1BA20A9
	51D1AF11FF04E4F394F25A38DC6950BFDFF87FDA3210A4ABF9C338AB243A6C12105EE56CB3DBEE410B1E0E8ACEC24914C0DA07516AA4FF4B8E12E9D412A109B7D07A780A32A71E7D7D1FE9836E2A121047CC89D50FF60B363E23B5DDDD30B2668C402E407EGE7CF4A142A4A6CF2CDFF2FE175668E08833D1052D7D6C6FF9F233F0F135F47A86698459CBB8E465DA8787DD153DD28B38D9E10E7871507383F348EBCA87D64759573DE2B5A988F5A5589E9740F484189596ED2177A7BBAC447C361CF9C589A9CB715
	7848CEC245C578E639B8C722E1BD5DFE0E3BF922B2659773D3120554FAA11DED47C37012BDF588DE3A65DEAF24C8B6BB0D3BE4F998BACB8305B05DA7E34940287540E68E43A68F0BDC02CD9F9E02847DAE8F8472350D965DC1FF6F9A35D9C92D9CDAF78F37C61E0BCBCA86EE32699FCD349B1324DB78451187A36CA137306F49864F604292EC2AD7ACB9E5A059A619F912A5E13BBC6C49831DA4E43017CAB0B53DBD86AF6FDF72B5CE2611F3B5A831058DFE9AAD29C62F87D50E4D3B2687685DD3AE8FA310BF7521
	CB18AF1F249F3BA54D7AF2CB2AF7FD79E4EA78924A546C42FB2F457714F7EBB7CD5A6B733EEE7C0BEC1FDAF849FD6F155DB519C36F4BD7813E6DA5BC37FDC37711607B4E4A65BE2345A299AD3DB5D693393FF27E16A43A6FB5056A89FFA71611090C36C8CF643E2E28733FD0CB878835189345199BGGA8D6GGD0CB818294G94G88G88G97F954AC35189345199BGGA8D6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG539BG
	GGG
**end of data**/
}
	/**
	 * This method initializes jTextFieldCBCName
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextFieldCBCName() {
		if(jTextFieldCBCName == null) {
			jTextFieldCBCName = new javax.swing.JTextField();
			
			jTextFieldCBCName.setDocument(
				new TextFieldDocument(
					TextFieldDocument.MAX_DEVICE_NAME_LENGTH,
					TextFieldDocument.INVALID_CHARS_PAO) );
		}
		return jTextFieldCBCName;
	}
	/**
	 * This method initializes jLabelCBCName
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelCBCName() {
		if(jLabelCBCName == null) {
			jLabelCBCName = new javax.swing.JLabel();
			jLabelCBCName.setText("Name: ");
			jLabelCBCName.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 14));
		}
		return jLabelCBCName;
	}
}  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"
