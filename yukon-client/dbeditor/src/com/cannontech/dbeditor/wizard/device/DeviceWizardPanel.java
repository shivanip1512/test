package com.cannontech.dbeditor.wizard.device;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.dbeditor.editor.device.DeviceScanRateEditorPanel;
import com.cannontech.spring.YukonSpringHook;

/**
 * This type was created in VisualAge.
 */

public class DeviceWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{
	private PaoDefinitionDao paoDefinitionDao = YukonSpringHook.getBean("paoDefinitionDao", PaoDefinitionDao.class);
    private DeviceNameAddressPanel deviceNameAddressPanel;
	private DevicePhoneNumberPanel devicePhoneNumberPanel;
	private DeviceRoutePanel deviceRoutePanel;
	private DeviceTypePanel deviceTypePanel;
	private DeviceCommChannelPanel deviceCommChannelPanel;
	private DeviceTapTerminalPanel deviceTapTerminalPanel;
	private DeviceTNPPTerminalPanel deviceTNPPTerminalPanel;
    private DeviceTapVerizonPanel deviceTapVerizonPanel;
	private DeviceIEDNamePanel deviceIEDNamePanel;
	private DeviceBaseNamePanel deviceBaseNamePanel;
    private DeviceGridPanel deviceGridPanel;
	private DeviceMeterNumberPanel deviceMeterNumberPanel;
	private DeviceSixnetWizardPanel deviceSixnetWizardPanel;
	private MCTBroadcastListEditorPanel mctBroadcastListEditorPanel;
	private DeviceScanRateEditorPanel deviceScanRateEditorPanel;
    private RfnMeterPanel rfnMeterPanel;
    private DeviceRDSTerminalPanel rdsTerminalPanel;
    private DeviceTcpTerminalPanel tcpTerminalPanel;

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


public DeviceRDSTerminalPanel getRDSTerminalPanel() {
    if( rdsTerminalPanel == null)
        rdsTerminalPanel = new DeviceRDSTerminalPanel();

    return rdsTerminalPanel;
}

public DeviceTcpTerminalPanel getTcpTerminalPanel() {
    if(tcpTerminalPanel == null)
        tcpTerminalPanel = new DeviceTcpTerminalPanel();
    
    return tcpTerminalPanel;
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

protected DeviceTNPPTerminalPanel getDeviceTNPPTerminalPanel() {
    if( deviceTNPPTerminalPanel == null )
        deviceTNPPTerminalPanel = new DeviceTNPPTerminalPanel();
        
    return deviceTNPPTerminalPanel;
}

protected DeviceTapVerizonPanel getDeviceTapVerizonPanel() {
	if( deviceTapVerizonPanel == null )
		deviceTapVerizonPanel = new DeviceTapVerizonPanel();
		
	return deviceTapVerizonPanel;
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

protected DeviceBaseNamePanel getDeviceBaseNamePanel() 
{
	if( deviceBaseNamePanel == null )
		deviceBaseNamePanel = new DeviceBaseNamePanel();
		
	return deviceBaseNamePanel;
}

protected DeviceGridPanel getDeviceGridPanel()
{
    if( deviceGridPanel == null )
        deviceGridPanel = new DeviceGridPanel();
        
    return deviceGridPanel;    
}

/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.device.DeviceVirtualPortPanel
 */
protected DeviceCommChannelPanel getDeviceCommChannelPanel() {
	if( deviceCommChannelPanel == null )
		deviceCommChannelPanel = new DeviceCommChannelPanel();
		
	return deviceCommChannelPanel;
}

protected RfnMeterPanel getRfnMeterPanel() {
    if( rfnMeterPanel == null ) {
        rfnMeterPanel = new RfnMeterPanel();
    }
    return rfnMeterPanel;    
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
protected DataInputPanel getNextInputPanel(DataInputPanel currentInputPanel)
{
	if (currentInputPanel == null)
	{
		getDeviceTypePanel().setFirstFocus();
        return getDeviceTypePanel();
	}
	else if (currentInputPanel == getDeviceTypePanel())
	{
		int devType = getDeviceTypePanel().getDeviceType();
		
		if( devType == PAOGroups.TAPTERMINAL ||
		    devType == PAOGroups.TNPP_TERMINAL || 
		    devType == PAOGroups.RDS_TERMINAL)
		{
		    getDeviceTapTerminalPanel().setDeviceType(devType);
			getDeviceTapTerminalPanel().setFirstFocus();
            return getDeviceTapTerminalPanel();
		}
		else if( devType == PAOGroups.WCTP_TERMINAL)
        {
            getDeviceTapVerizonPanel().setFirstFocus();
            return getDeviceTapVerizonPanel();
        }
        else if( devType == PAOGroups.SNPP_TERMINAL )
		{
			getDeviceTapVerizonPanel().setFirstFocus();
            getDeviceTapVerizonPanel().setIsSNPP(true);
			return getDeviceTapVerizonPanel();
		}
        else if( devType == PAOGroups.VIRTUAL_SYSTEM || DeviceTypesFuncs.isRfn(devType)) {
            getDeviceBaseNamePanel().setDeviceType(devType);
            getDeviceBaseNamePanel().setFirstFocus();
            return getDeviceBaseNamePanel();
        }
		else if( (DeviceTypesFuncs.isMeter(devType)
					  && !DeviceTypesFuncs.isIon(devType))
					  || devType == PAOGroups.DAVISWEATHER)
		{
			getDeviceIEDNamePanel().setDeviceType(devType);
			getDeviceIEDNamePanel().setFirstFocus();
            return getDeviceIEDNamePanel();
		}
		else if ( devType == PAOGroups.NEUTRAL_MONITOR || devType == PAOGroups.FAULT_CI )
        {
        	getDeviceGridPanel().setDeviceType(devType);
            getDeviceGridPanel().setFirstFocus();
            return getDeviceGridPanel();
        }
		else
		{
			getDeviceNameAddressPanel().setDeviceType( devType );
            getDeviceNameAddressPanel().setFirstFocus();
			return getDeviceNameAddressPanel();
		}
	}
	else if (currentInputPanel == getDeviceBaseNamePanel()) {
	    getDeviceMeterNumberPanel().setFirstFocus();
        return getDeviceMeterNumberPanel();
	}
	else if ((currentInputPanel == getDeviceNameAddressPanel()) || (currentInputPanel == getDeviceIEDNamePanel()))
	{
		int devType = getDeviceTypePanel().getDeviceType();

		if( DeviceTypesFuncs.isMeter(devType) 
			 || DeviceTypesFuncs.isMCT(devType) )
		{
			getDeviceMeterNumberPanel().setValue(null);
			getDeviceMeterNumberPanel().setMCT400Type(devType);
			if( DeviceTypesFuncs.isMCT2XXORMCT310XX(devType) )
			{
				//Append "10" to the address for the desired default meter number.
				getDeviceMeterNumberPanel().setDefaultMeterNumber("10" + getDeviceNameAddressPanel().getAddress());
			}
			
			if(DeviceTypesFuncs.isMCT4XX(devType))
			{
				getDeviceMeterNumberPanel().setMCT400Type(devType);
				getDeviceMeterNumberPanel().setDefaultMeterNumber("");	//Default the meterNumber to nothing
			}
			getDeviceMeterNumberPanel().setFirstFocus();
			return getDeviceMeterNumberPanel();
		}
		else if( devType == PAOGroups.DAVISWEATHER )
		{
			getDeviceScanRatePanel().setDeviceType(getDeviceTypePanel().getDeviceType());
            getDeviceScanRatePanel().setFirstFocus();
			return getDeviceScanRatePanel();			
		}
		else if( com.cannontech.database.data.pao.DeviceTypes.MCTBROADCAST == devType )
		{
			MCTBroadcastListEditorPanel temp = getMCTBroadcastListEditorPanel();
			temp.setValue(null);
            temp.setFirstFocus();
			return temp;
		}
		else if (devType == DeviceTypes.RDS_TERMINAL)
		{
		    return getRDSTerminalPanel();
		}
		else
		{
			getDeviceScanRatePanel().setDeviceType(getDeviceTypePanel().getDeviceType());
            getDeviceScanRatePanel().setFirstFocus();
			return getDeviceScanRatePanel();
		}
	}
	else if (currentInputPanel == getDeviceMeterNumberPanel())
	{
	    int devType = getDeviceTypePanel().getDeviceType();
	    if(DeviceTypesFuncs.isRfn(devType)) {
	        getRfnMeterPanel().setFirstFocus();
	        return getRfnMeterPanel();
	    } else {
	        getDeviceScanRatePanel().setDeviceType(devType);
	        getDeviceScanRatePanel().setFirstFocus();
	        return getDeviceScanRatePanel();
	    }
	}
	else if (currentInputPanel == getDeviceScanRatePanel())
	{
		int devType = getDeviceTypePanel().getDeviceType();

		if( DeviceTypesFuncs.isCarrier(devType) )
		{
			getDeviceRoutePanel().setValue(null);
            getDeviceRoutePanel().setFirstFocus();
			return getDeviceRoutePanel();
		}
		else if( devType == PAOGroups.SIXNET)
		{
			getDeviceSixnetWizardPanel().setFirstFocus();
            return getDeviceSixnetWizardPanel();
		}
		else if( DeviceTypesFuncs.isRTU(devType) || DeviceTypesFuncs.isCCU(devType))
		{
			getDeviceCommChannelPanel().setValue(null);
			getDeviceCommChannelPanel().setAddress(new Integer(getDeviceNameAddressPanel().getAddress()).intValue());
			getDeviceCommChannelPanel().setDeviceType(getDeviceTypePanel().getDeviceType());
            getDeviceCommChannelPanel().setFirstFocus();
			return getDeviceCommChannelPanel();
		}
		else if(paoDefinitionDao.isTagSupported(PaoType.getForId(devType), PaoTag.IPC_METER)) {
		    getTcpTerminalPanel().setFirstFocus();
		    return getTcpTerminalPanel();
		}
		else
			getDeviceCommChannelPanel().setFirstFocus();
            return getDeviceCommChannelPanel();
	}
	else if (currentInputPanel == getDeviceSixnetWizardPanel())
	{
		getDeviceCommChannelPanel().setValue(null);
        getDeviceCommChannelPanel().setFirstFocus();
		return getDeviceCommChannelPanel();
	}
	else if( currentInputPanel == getDeviceTapTerminalPanel())
	{
       int devType = getDeviceTypePanel().getDeviceType();
       
       if (devType == PAOGroups.TAPTERMINAL) {
           getDeviceCommChannelPanel().setValue(null);
           getDeviceCommChannelPanel().setFirstFocus();
           return getDeviceCommChannelPanel();
       }
       if (devType == PAOGroups.TNPP_TERMINAL) {
           getDeviceTNPPTerminalPanel().setFirstFocus();
           return getDeviceTNPPTerminalPanel();
       }
       if (devType == PAOGroups.RDS_TERMINAL) {
           getRDSTerminalPanel().setFirstFocus();
           return getRDSTerminalPanel();
       }
       return null;
	}
	else if (currentInputPanel == getDeviceTapVerizonPanel() ||
	         currentInputPanel == getDeviceTNPPTerminalPanel())
	{
		getDeviceCommChannelPanel().setValue(null);
        getDeviceCommChannelPanel().setFirstFocus();
		return getDeviceCommChannelPanel();
	}
	else if (currentInputPanel == getDeviceCommChannelPanel())
	{
		//To get to this point the device must be a dialup device
		//If it isn't better go find out why we got here!
        getDeviceCommChannelPanel().setFirstFocus();
		return getDevicePhoneNumberPanel();
	}
	
	else if (currentInputPanel == getMCTBroadcastListEditorPanel())
	{
			getDeviceRoutePanel().setValue(null);
            getDeviceRoutePanel().setFirstFocus();
			return getDeviceRoutePanel();
	}
	else if (currentInputPanel == getRDSTerminalPanel())
	{
	    getDeviceCommChannelPanel().setFirstFocus();
        return getDeviceCommChannelPanel();
	}
	else
		throw new Error(getClass() + "::" + "getNextInputPanel() - Could not determine next DataInputPanel");
}


/**
 * isLastInputPanel method comment.
 */
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) 
{
	return  ( currentPanel == getDevicePhoneNumberPanel() 
            || (currentPanel == getDeviceCommChannelPanel() && !getDeviceCommChannelPanel().isDialupPort())
            || currentPanel == getDeviceRoutePanel() 
            || (currentPanel == getDeviceBaseNamePanel() && getDeviceTypePanel().getDeviceType() == PAOGroups.VIRTUAL_SYSTEM)
            || currentPanel == getDeviceGridPanel()
            || currentPanel == getRfnMeterPanel()
            || currentPanel == getTcpTerminalPanel()
            || (currentPanel == getDeviceCommChannelPanel() 
                && 
                (getDeviceTypePanel().getDeviceType() == PAOGroups.ION_7700
                 || getDeviceTypePanel().getDeviceType() == PAOGroups.ION_7330
                 || getDeviceTypePanel().getDeviceType() == PAOGroups.ION_8300) ) );
}


}