package com.cannontech.dbeditor.wizard.billing;

/**
 * This type was created in VisualAge.
 */

public class BillingFileWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{
	private BillingFileGenerationPanel billingFileGenerationPanel;

/**
 * BillingFileWizardPanel constructor comment.
 */
public BillingFileWizardPanel() {
	super();
	setFinishedEnabled(false);
}
/**
 * Insert the method's description here.
 * Creation date: (5/4/2001 11:11:28 AM)
 * @return java.awt.Dimension
 */
public java.awt.Dimension getActualSize() 
{
	setPreferredSize( new java.awt.Dimension(410, 500) );

	return getPreferredSize();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.device.DeviceIEDNamePanel
 */
protected BillingFileGenerationPanel getBillingFileGenerationPanel() {
	if( billingFileGenerationPanel == null )
		billingFileGenerationPanel = new BillingFileGenerationPanel();
		
	return billingFileGenerationPanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
protected String getHeaderText() {
	return "Create Billing File";
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
	currentInputPanel = getBillingFileGenerationPanel();
	return currentInputPanel;
	/*
	
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
	else
		throw new Error(getClass() + "::" + "getNextInputPanel() - Could not determine next DataInputPanel");
*/
		
}
/**
 * isLastInputPanel method comment.
 */


 
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) 
{
	return true;
}
/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 */
public static void main(String args[]) {
	try
{
	javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
	BillingFileWizardPanel p = new BillingFileWizardPanel();

	javax.swing.JFrame f= new javax.swing.JFrame();

	f.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("dbEditorIcon.gif"));
	f.getContentPane().add( p );
	f.pack();

	f.show();
}
catch( Throwable t)
{
	com.cannontech.clientutils.CTILogger.error( t.getMessage(), t );
}
}
}
