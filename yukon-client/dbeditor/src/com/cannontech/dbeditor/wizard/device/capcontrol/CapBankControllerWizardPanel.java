package com.cannontech.dbeditor.wizard.device.capcontrol;

/**
 * This type was created in VisualAge.
 */
public class CapBankControllerWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{
	private CapBankControllerTypePanel capBankControllerTypePanel;
	private CapBankControllerSettingsPanel capBankControllerSettingsPanel;
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
public CapBankControllerSettingsPanel getCapBankControllerSettingsPanel() {
	if( capBankControllerSettingsPanel == null )
		capBankControllerSettingsPanel = new CapBankControllerSettingsPanel();
		
	return capBankControllerSettingsPanel;
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
		getCapBankControllerSettingsPanel().setValue(null);
		getCapBankControllerSettingsPanel().setCbcType( getCapBankControllerTypePanel().getSelectedType() );
		return getCapBankControllerSettingsPanel();
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
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) {
	return ( currentPanel instanceof CapBankControllerSettingsPanel );
}
}
