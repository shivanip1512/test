package com.cannontech.dbeditor.wizard.device.capcontrol;

/**
 * This type was created in VisualAge.
 */
public class CapBankWizardPanel extends com.cannontech.common.wizard.WizardPanel {
	
	private CapBankNameAddressPanel capBankNameAddressPanel;
	private CapBankSettingsPanel capBankSettingsPanel;
	private CapBankCntrlCreationPanel capBankCntrlCreationPanel;
/**
 * Insert the method's description here.
 * Creation date: (8/10/00 12:49:14 PM)
 */
public CapBankWizardPanel()
{
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
 */
protected CapBankCntrlCreationPanel getCapBankCntrlCreationPanel() 
{
	if( capBankCntrlCreationPanel == null )
		capBankCntrlCreationPanel = new CapBankCntrlCreationPanel();
		
	return capBankCntrlCreationPanel;
}
/**
 * This method was created in VisualAge.
 */
protected CapBankNameAddressPanel getCapBankNameAddressPanel() 
{
	if( capBankNameAddressPanel == null )
		capBankNameAddressPanel = new CapBankNameAddressPanel();
		
	return capBankNameAddressPanel;
}
/**
 * This method was created in VisualAge.
 */
protected CapBankSettingsPanel getCapBankSettingsPanel() 
{
	if( capBankSettingsPanel == null )
		capBankSettingsPanel = new CapBankSettingsPanel();
		
	return capBankSettingsPanel;
}
/**
 * getHeaderText method comment.
 */
protected String getHeaderText() {
	return "Cap Bank Setup";
}
/**
 * getNextInputPanel method comment.
 */
protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(com.cannontech.common.gui.util.DataInputPanel currentInputPanel) 
{
	if( currentInputPanel == null )
	{
		return getCapBankNameAddressPanel();
	}
	else if( currentInputPanel == getCapBankNameAddressPanel() )
	{
		getCapBankSettingsPanel().setCapBankSelectedType( getCapBankNameAddressPanel().getSelectedCapBankType() );
		getCapBankSettingsPanel().setValue(null);
		
		return getCapBankSettingsPanel();
	}
	else if( !getCapBankNameAddressPanel().isFixedCapBank()
				&& currentInputPanel == getCapBankSettingsPanel() )
	{
		return getCapBankCntrlCreationPanel();
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
	return( (currentPanel == getCapBankSettingsPanel() 
				&& getCapBankNameAddressPanel().isFixedCapBank())
				|| currentPanel == getCapBankCntrlCreationPanel() );
}
}
