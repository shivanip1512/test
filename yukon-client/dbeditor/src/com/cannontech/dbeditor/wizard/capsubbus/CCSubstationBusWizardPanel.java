package com.cannontech.dbeditor.wizard.capsubbus;

/**
 * This type was created in VisualAge.
 */
public class CCSubstationBusWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{	
	private CCSubstationBusNamePanel ccSubstationBusNamePanel;
	private CCSubstationBusPeakSettingsPanel ccSubstationBusPeakSettingsPanel;
	private CCSubstationBusPointSettingsPanel ccSubstationBusPointSettingsPanel;
	private CCSubstationBusMiscSettingsPanel ccSubstationBusMiscSettingsPanel;

	//defined in another package
	private com.cannontech.dbeditor.editor.capsubbus.CCSubBusFeederListEditorPanel ccSubBusFeederListEditorPanel;
/**
 * Insert the method's description here.
 * Creation date: (5/4/2001 11:11:28 AM)
 * @return java.awt.Dimension
 */
public java.awt.Dimension getActualSize() 
{
	setPreferredSize( new java.awt.Dimension(430, 480) );

	return getPreferredSize();
}
/**
 * This method was created in VisualAge.
 */
protected com.cannontech.dbeditor.editor.capsubbus.CCSubBusFeederListEditorPanel getCCSubBusFeederListEditorPanel() 
{
	if( ccSubBusFeederListEditorPanel == null )
	{
		ccSubBusFeederListEditorPanel = new com.cannontech.dbeditor.editor.capsubbus.CCSubBusFeederListEditorPanel();
		ccSubBusFeederListEditorPanel.initLeftListFeeders();
	}
		
	return ccSubBusFeederListEditorPanel;
}
/**
 * This method was created in VisualAge.
 */
protected CCSubstationBusMiscSettingsPanel getCCSubstationBusMiscSettingsPanel() 
{
	if( ccSubstationBusMiscSettingsPanel == null )
		ccSubstationBusMiscSettingsPanel = new CCSubstationBusMiscSettingsPanel();
		
	return ccSubstationBusMiscSettingsPanel;
}
/**
 * This method was created in VisualAge.
 */
protected CCSubstationBusNamePanel getCCSubstationBusNamePanel() 
{
	if( ccSubstationBusNamePanel == null )
		ccSubstationBusNamePanel= new CCSubstationBusNamePanel();
		
	return ccSubstationBusNamePanel;
}
/**
 * This method was created in VisualAge.
 */
protected CCSubstationBusPeakSettingsPanel getCCSubstationBusPeakSettingsPanel() 
{
	if( ccSubstationBusPeakSettingsPanel== null )
		ccSubstationBusPeakSettingsPanel = new CCSubstationBusPeakSettingsPanel();
		
	return ccSubstationBusPeakSettingsPanel;
}
/**
 * This method was created in VisualAge.
 */
protected CCSubstationBusPointSettingsPanel getCCSubstationBusPointSettingsPanel() 
{
	if( ccSubstationBusPointSettingsPanel == null )
		ccSubstationBusPointSettingsPanel = new CCSubstationBusPointSettingsPanel();
		
	return ccSubstationBusPointSettingsPanel;
}
/**
 * getHeaderText method comment.
 */
protected String getHeaderText() {
	return "Cap Control Substation Bus Setup";
}
/**
 * getNextInputPanel method comment.
 */
protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(com.cannontech.common.gui.util.DataInputPanel currentInputPanel) 
{
	if( currentInputPanel == null )
	{
		return getCCSubstationBusNamePanel();
	}
	else if( currentInputPanel == getCCSubstationBusNamePanel() )
	{
		getCCSubstationBusPointSettingsPanel().setValue(null);
		return getCCSubstationBusPointSettingsPanel();
	}
	else if( currentInputPanel == getCCSubstationBusPointSettingsPanel() )
	{
		return getCCSubstationBusPeakSettingsPanel();
	}
	else if( currentInputPanel == getCCSubstationBusPeakSettingsPanel() )
	{
		return getCCSubstationBusMiscSettingsPanel();
	}
	else if( currentInputPanel == getCCSubstationBusMiscSettingsPanel() )
	{
		return getCCSubBusFeederListEditorPanel();
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
	return ( currentPanel ==  getCCSubBusFeederListEditorPanel() );
}
}
