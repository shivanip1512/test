package com.cannontech.dbeditor.wizard.tou;

/**
 * This type was created in VisualAge.
 */
public class TOUScheduleWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{
	private TOUScheduleBasePanel touScheduleBasePanel;
/**
 * TOUScheduleWizardPanel constructor comment.
 */
public TOUScheduleWizardPanel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (9/23/2004 11:11:28 AM)
 * @return java.awt.Dimension
 */
public java.awt.Dimension getActualSize() 
{
	setPreferredSize( new java.awt.Dimension(410, 480) );

	return getPreferredSize();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
protected String getHeaderText() {
	return "TOU Schedule Setup";
}
/**
 * This method was created in VisualAge.
 * @return TOUScheduleBasePanel
 */
public TOUScheduleBasePanel getTOUScheduleBasePanel() {
	
	if( touScheduleBasePanel == null )
		touScheduleBasePanel = new TOUScheduleBasePanel();
		
	return touScheduleBasePanel;
}
/**
 * getNextInputPanel method comment.
 */
protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(com.cannontech.common.gui.util.DataInputPanel currentInputPanel) {

	if( currentInputPanel == null )
	{
		return getTOUScheduleBasePanel();
	}
	else
		throw new Error(getClass() + "::getNextInputPanel - Unable to determine next DataInputPanel");
}
/**
 * isLastInputPanel method comment.
 */
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) {

	return ( (currentPanel == getTOUScheduleBasePanel()) );
}
}
