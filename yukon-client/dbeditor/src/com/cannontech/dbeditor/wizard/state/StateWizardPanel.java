package com.cannontech.dbeditor.wizard.state;

/**
 * This type was created in VisualAge.
 */
public class StateWizardPanel extends com.cannontech.common.wizard.WizardPanel {
	private GroupStateNamePanel groupStateNamePanel;
/**
 * RouteWizardPanel constructor comment.
 */
public StateWizardPanel() {
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
 * @return com.cannontech.dbeditor.wizard.route.RepeaterSelectPanel
 */
public GroupStateNamePanel getGroupStateNamePanel() {
	
	if( groupStateNamePanel == null )
		groupStateNamePanel = new GroupStateNamePanel();
		
	return groupStateNamePanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
protected String getHeaderText() {
	return "State Setup";
}
/**
 * getNextInputPanel method comment.
 */
protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(com.cannontech.common.gui.util.DataInputPanel currentInputPanel) {

	if( currentInputPanel == null )
	{
		return getGroupStateNamePanel();
	}
	else
		throw new Error(getClass() + "::getNextInputPanel - Unable to determine next DataInputPanel");
}
/**
 * isLastInputPanel method comment.
 */
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) {

	return ( (currentPanel == getGroupStateNamePanel()) );
}

}
