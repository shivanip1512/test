package com.cannontech.dbeditor.wizard.capfeeder;

/**
 * This type was created in VisualAge.
 */
public class CCFeederWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{	
	private CCFeederNamePanel ccFeederNamePanel;
	private CCFeederPeakSettingsPanel ccFeederPeakSettingsPanel;
	private CCFeederPointSettingsPanel ccFeederPointSettingsPanel;
	
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
protected CCFeederNamePanel  getCcFeederNamePanel () 
{
	if( ccFeederNamePanel == null )
		ccFeederNamePanel = new CCFeederNamePanel ();
		
	return ccFeederNamePanel ;
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 1:08:37 PM)
 * @return com.cannontech.common.wizard.feeder.CCFeederPeakSettingsPanel
 */
protected CCFeederPeakSettingsPanel getCcFeederPeakSettingsPanel() 
{
	if( ccFeederPeakSettingsPanel == null )
		ccFeederPeakSettingsPanel = new CCFeederPeakSettingsPanel();

	return ccFeederPeakSettingsPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 2:06:50 PM)
 * @return com.cannontech.common.wizard.feeder.CCFeederPointSettingsPanel
 */
protected CCFeederPointSettingsPanel getCcFeederPointSettingsPanel() 
{
	if( ccFeederPointSettingsPanel == null )
		ccFeederPointSettingsPanel = new CCFeederPointSettingsPanel();

	return ccFeederPointSettingsPanel;
}
/**
 * getHeaderText method comment.
 */
protected String getHeaderText() {
	return "Cap Control Feeder";
}
/**
 * getNextInputPanel method comment.
 */
protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(com.cannontech.common.gui.util.DataInputPanel currentInputPanel) 
{
	if( currentInputPanel == null )
	{
		return getCcFeederNamePanel();
	}
	else if( currentInputPanel == getCcFeederNamePanel() )
	{
		return getCcFeederPeakSettingsPanel();
	}
	else if( currentInputPanel == getCcFeederPeakSettingsPanel() )
	{
		return getCcFeederPointSettingsPanel();
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
	return ( currentPanel ==  getCcFeederPointSettingsPanel() );
}
}
