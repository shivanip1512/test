package com.cannontech.dbeditor.wizard.device;
/**
 * This type was created in VisualAge.
 */
import com.cannontech.common.wizard.WizardPanel;
import com.cannontech.common.gui.util.DataInputPanelListener;
import com.cannontech.common.gui.util.DataInputPanelEvent;

public class DeviceWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{
	private DeviceNameAddressPanel deviceNameAddressPanel;
	private DevicePhoneNumberPanel devicePhoneNumberPanel;
	private DeviceRoutePanel deviceRoutePanel;
	private DeviceTypePanel deviceTypePanel;
	private DeviceVirtualPortPanel deviceVirtualPortPanel;
	private DeviceTapTerminalPanel deviceTapTerminalPanel;
	private DeviceIEDNamePanel deviceIEDNamePanel;
	private DeviceVirtualNamePanel deviceVirtualNamePanel;
	private DeviceMeterNumberPanel deviceMeterNumberPanel;
	private DeviceSixnetWizardPanel deviceSixnetWizardPanel;
	private MCTBroadcastListEditorPanel mctBroadcastListEditorPanel;
	private com.cannontech.dbeditor.editor.device.DeviceScanRateEditorPanel deviceScanRateEditorPanel;

/**
 * DeviceWizardPanel constructor comment.
 */
public DeviceWizardPanel() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (5/4/2001 11:11:28 AM)
 * @return java.awt.Dimension
 */
public java.awt.Dimension getActualSize() 
{
	setPreferredSize( new java.awt.Dimension(410, 480) );

	return getPreferredSize();
}


/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.device.DeviceIEDNamePanel
 */
protected DeviceIEDNamePanel getDeviceIEDNamePanel() {
	if( deviceIEDNamePanel == null )
		deviceIEDNamePanel = new DeviceIEDNamePanel();
		
	return deviceIEDNamePanel;
}


/**
 * Insert the method's description here.
 * Creation date: (7/13/2001 11:36:51 AM)
 * @return com.cannontech.dbeditor.wizard.device.DeviceMeterNumberPanel
 */
public DeviceMeterNumberPanel getDeviceMeterNumberPanel() {
	if( deviceMeterNumberPanel == null )
		deviceMeterNumberPanel = new DeviceMeterNumberPanel();
		
	return deviceMeterNumberPanel;
}


/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.device.DeviceNameAddressPanel
 */
protected DeviceNameAddressPanel getDeviceNameAddressPanel() {
	if( deviceNameAddressPanel == null )
		deviceNameAddressPanel = new DeviceNameAddressPanel();
		
	return deviceNameAddressPanel;
}


/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.device.DevicePhoneNumberPanel
 */
protected DevicePhoneNumberPanel getDevicePhoneNumberPanel() {
	if( devicePhoneNumberPanel == null )
		devicePhoneNumberPanel = new DevicePhoneNumberPanel();
		
	return devicePhoneNumberPanel;
}


/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.device.DeviceRoutePanel
 */
protected DeviceRoutePanel getDeviceRoutePanel() {
	if( deviceRoutePanel == null )
		deviceRoutePanel = new DeviceRoutePanel();
		
	return deviceRoutePanel;
}


/**
 * Insert the method's description here.
 * Creation date: (7/30/2001 10:54:57 AM)
 * @return com.cannontech.dbeditor.editor.device.DeviceScanRateEditorPanel
 */
public com.cannontech.dbeditor.editor.device.DeviceScanRateEditorPanel getDeviceScanRatePanel()
{
	if (deviceScanRateEditorPanel == null)
		deviceScanRateEditorPanel = new com.cannontech.dbeditor.editor.device.DeviceScanRateEditorPanel();

	return deviceScanRateEditorPanel;
}


/**
 * Insert the method's description here.
 * Creation date: (7/27/2001 12:00:43 PM)
 * @return com.cannontech.dbeditor.wizard.device.DeviceSixnetWizardPanel
 */
public DeviceSixnetWizardPanel getDeviceSixnetWizardPanel()
{
	if (deviceSixnetWizardPanel == null)
	{
		deviceSixnetWizardPanel = new DeviceSixnetWizardPanel();
	}
	return deviceSixnetWizardPanel;
}


/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.device.DeviceNameAddressPanel
 */
protected DeviceTapTerminalPanel getDeviceTapTerminalPanel() {
	if( deviceTapTerminalPanel == null )
		deviceTapTerminalPanel = new DeviceTapTerminalPanel();
		
	return deviceTapTerminalPanel;
}


/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.device.DeviceTypePanel
 */
public DeviceTypePanel getDeviceTypePanel() {
	if( this.deviceTypePanel == null )
		this.deviceTypePanel = new DeviceTypePanel();
		
	return deviceTypePanel;
}


/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.device.DeviceNameAddressPanel
 */
protected DeviceVirtualNamePanel getDeviceVirtualNamePanel() 
{
	if( deviceVirtualNamePanel == null )
		deviceVirtualNamePanel = new DeviceVirtualNamePanel();
		
	return deviceVirtualNamePanel;
}


/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.device.DeviceVirtualPortPanel
 */
protected DeviceVirtualPortPanel getDeviceVirtualPortPanel() {
	if( deviceVirtualPortPanel == null )
		deviceVirtualPortPanel = new DeviceVirtualPortPanel();
		
	return deviceVirtualPortPanel;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
protected String getHeaderText() {
	return "Device Setup";
}


/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.device.DeviceNameAddressPanel
 */
protected MCTBroadcastListEditorPanel getMCTBroadcastListEditorPanel() {
	if( mctBroadcastListEditorPanel == null )
		mctBroadcastListEditorPanel = new MCTBroadcastListEditorPanel();
		
	return mctBroadcastListEditorPanel;
}


/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public java.awt.Dimension getMinimumSize() {
	return getPreferredSize();
}


/**
 * getNextInputPanel method comment.
 */
protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(
	com.cannontech.common.gui.util.DataInputPanel currentInputPanel)
{
	if (currentInputPanel == null)
	{
		return getDeviceTypePanel();
	}
	else if (currentInputPanel == getDeviceTypePanel())
	{
		int devType = getDeviceTypePanel().getDeviceType();
		
		if( devType == com.cannontech.database.data.pao.PAOGroups.TAPTERMINAL
			 || devType == com.cannontech.database.data.pao.PAOGroups.WCTP_TERMINAL )
		{
			return getDeviceTapTerminalPanel();
		}
		else if( com.cannontech.database.data.device.DeviceTypesFuncs.isMeter(devType)
				    || devType == com.cannontech.database.data.pao.PAOGroups.DAVISWEATHER)
			return getDeviceIEDNamePanel();
		else if( devType == com.cannontech.database.data.pao.PAOGroups.VIRTUAL_SYSTEM )
			return getDeviceVirtualNamePanel();
		else
		{
			getDeviceNameAddressPanel().setDeviceType( devType );
			return getDeviceNameAddressPanel();
		}
	}
	else if ((currentInputPanel == getDeviceNameAddressPanel()) || (currentInputPanel == getDeviceIEDNamePanel()))
	{
		int devType = getDeviceTypePanel().getDeviceType();

		if( com.cannontech.database.data.device.DeviceTypesFuncs.isMeter(devType) 
			 || com.cannontech.database.data.device.DeviceTypesFuncs.isMCT(devType)
			 || devType == com.cannontech.database.data.pao.PAOGroups.DAVISWEATHER)
		{
			getDeviceMeterNumberPanel().setValue(null);
			if( com.cannontech.database.data.device.DeviceTypesFuncs.isMCTiORMCT2XX(devType) )
			{
				getDeviceMeterNumberPanel().setDefaultMeterNumber(getDeviceNameAddressPanel().getAddress());
			}
			return getDeviceMeterNumberPanel();
		}
		else if( com.cannontech.database.data.pao.DeviceTypes.MCTBROADCAST == devType )
		{
			MCTBroadcastListEditorPanel temp = getMCTBroadcastListEditorPanel();
			temp.setValue(null);
			return temp;
		}
		else
		{
			getDeviceScanRatePanel().setDeviceType(getDeviceTypePanel().getDeviceType());
			return getDeviceScanRatePanel();
		}
	}

	else if (currentInputPanel == getDeviceMeterNumberPanel())
	{

		getDeviceScanRatePanel().setDeviceType(getDeviceTypePanel().getDeviceType());
		return getDeviceScanRatePanel();
	}
	else if (currentInputPanel == getDeviceScanRatePanel())
	{
		int devType = getDeviceTypePanel().getDeviceType();

		if( com.cannontech.database.data.device.DeviceTypesFuncs.isCarrier(devType) )
		{
			getDeviceRoutePanel().setValue(null);
			return getDeviceRoutePanel();
		}
		else if( devType == com.cannontech.database.data.pao.PAOGroups.SIXNET)
		{
			return getDeviceSixnetWizardPanel();
		}
		else
		{
			getDeviceVirtualPortPanel().setValue(null);
			return getDeviceVirtualPortPanel();
		}
	}
	else if (currentInputPanel == getDeviceSixnetWizardPanel())
	{
		getDeviceVirtualPortPanel().setValue(null);
		return getDeviceVirtualPortPanel();
	}
	else if (currentInputPanel == getDeviceTapTerminalPanel())
	{
		getDeviceVirtualPortPanel().setValue(null);
		return getDeviceVirtualPortPanel();
	}
	else if (currentInputPanel == getDeviceVirtualPortPanel())
	{
		//To get to this point the device must be a dialup device
		//If it isn't better go find out why we got here!
		return getDevicePhoneNumberPanel();
	}
	
	else if (currentInputPanel == getMCTBroadcastListEditorPanel())
	{
			getDeviceRoutePanel().setValue(null);
			return getDeviceRoutePanel();
	}
	else
		throw new Error(getClass() + "::" + "getNextInputPanel() - Could not determine next DataInputPanel");
}


/**
 * isLastInputPanel method comment.
 */
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) 
{
	return  ( currentPanel == getDevicePhoneNumberPanel() ||
			  (currentPanel == getDeviceVirtualPortPanel() && !getDeviceVirtualPortPanel().isDialupPort()) ||
			  currentPanel == getDeviceRoutePanel() ||
			  currentPanel == getDeviceVirtualNamePanel() );
}


/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 */
public static void main(String args[]) {
	try
{
	javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
	DeviceWizardPanel p = new DeviceWizardPanel();

	javax.swing.JFrame f= new javax.swing.JFrame();

	f.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("dbEditorIcon.gif"));
	f.getContentPane().add( p );
	f.pack();

	f.show();
}
catch( Throwable t)
{
	t.printStackTrace();
}
}
}