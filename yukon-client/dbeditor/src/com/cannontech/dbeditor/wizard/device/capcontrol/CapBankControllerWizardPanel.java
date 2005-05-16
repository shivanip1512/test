package com.cannontech.dbeditor.wizard.device.capcontrol;

import com.cannontech.dbeditor.editor.device.DeviceScanRateEditorPanel;
import com.cannontech.dbeditor.editor.device.capcontrol.DeviceCapBankControllerEditorPanel;

/**
 * This type was created in VisualAge.
 */
public class CapBankControllerWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{
	private CapBankControllerTypePanel capBankControllerTypePanel;
   	private DeviceCapBankControllerEditorPanel deviceCapBankControllerEditorPanel;
   	private DeviceScanRateEditorPanel deviceScanRateEditorPanel;
   	private CapBankControllerSpecialTypePanel capBankControllerSpecialTypePanel;

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
    */
   public DeviceCapBankControllerEditorPanel getDeviceCapBankControllerEditorPanel() {
   	if( deviceCapBankControllerEditorPanel == null )
   		deviceCapBankControllerEditorPanel = new DeviceCapBankControllerEditorPanel();
   		
   	return deviceCapBankControllerEditorPanel;
   }
   
   /**
    * This method was created in VisualAge.
    */
   public DeviceScanRateEditorPanel getDeviceScanRateEditorPanel() 
   {
      if( deviceScanRateEditorPanel == null )
         deviceScanRateEditorPanel = new DeviceScanRateEditorPanel();
         
      return deviceScanRateEditorPanel;
   }
   
   /**
    * Insert the method's description here.
    * Creation date: (4/27/2001 3:37:51 PM)
    * @return com.cannontech.dbeditor.wizard.device.capcontrol.CapBankControllerTypePanel
    */
   public CapBankControllerTypePanel getCapBankControllerTypePanel() 
   {
   	if( capBankControllerTypePanel == null )
   		capBankControllerTypePanel = new CapBankControllerTypePanel();
   
   	return capBankControllerTypePanel;
   }
   
   public CapBankControllerSpecialTypePanel getCapBankControllerSpecialTypePanel() 
   {
		if( capBankControllerSpecialTypePanel == null )
			capBankControllerSpecialTypePanel = new CapBankControllerSpecialTypePanel();
   
		return capBankControllerSpecialTypePanel;
   }
   /**
    * getHeaderText method comment.
    */
   protected String getHeaderText() {
   	return "Cap Bank Controller Setup";
   }
   /**
    * getNextInputPanel method comment.
    */
   protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(com.cannontech.common.gui.util.DataInputPanel currentInputPanel) {
   
   	if( currentInputPanel == null )
   	{
   		getCapBankControllerTypePanel().setValue(null);
   		return getCapBankControllerTypePanel();
   	}
   	else if( currentInputPanel == getCapBankControllerTypePanel() )
   	{
   		getDeviceCapBankControllerEditorPanel().setCbcType( getCapBankControllerTypePanel().getSelectedType() );
   		if(getCapBankControllerTypePanel().getSelectedType() == com.cannontech.database.data.pao.DeviceTypes.CBC_7010)
   			return getCapBankControllerSpecialTypePanel();
   		else
   			return getDeviceCapBankControllerEditorPanel();
   	}
	else if( currentInputPanel == getCapBankControllerSpecialTypePanel() )
	{
		getDeviceCapBankControllerEditorPanel().setCbcType( getCapBankControllerSpecialTypePanel().getSelectedType() );
		return getDeviceCapBankControllerEditorPanel();
	}
    else if( currentInputPanel == getDeviceCapBankControllerEditorPanel()
                && getDeviceCapBankControllerEditorPanel().getCbcType() == com.cannontech.database.data.pao.DeviceTypes.DNP_CBC_6510 )
    {
    	getDeviceScanRateEditorPanel().setDeviceType( getCapBankControllerTypePanel().getSelectedType() );
        return getDeviceScanRateEditorPanel();
    }
   	else
   	{
   		System.err.println( getClass() + "::getNextInputPanel() - currentInputPanel was not recognized.");
   		return null;
   	}
   }

   /**
    * isLastInputPanel method comment.
    */
   protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) 
   {
   	return ( 
         (getCapBankControllerTypePanel().getSelectedType() == com.cannontech.database.data.pao.DeviceTypes.DNP_CBC_6510
         && currentPanel == getDeviceScanRateEditorPanel())
         ||
         (getCapBankControllerTypePanel().getSelectedType() != com.cannontech.database.data.pao.DeviceTypes.DNP_CBC_6510
         && currentPanel == getDeviceCapBankControllerEditorPanel()) );
   }

}
