package com.cannontech.dbeditor.wizard.season;

/**
 * This type was created in VisualAge.
 */
public class SeasonScheduleWizardPanel extends com.cannontech.common.wizard.WizardPanel {
	private SeasonScheduleBasePanel seasonScheduleBasePanel;
/**
 * RouteWizardPanel constructor comment.
 */
public SeasonScheduleWizardPanel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 11:11:28 AM)
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
	return "Season Schedule Setup";
}
/**
 * This method was created in VisualAge.
 * @return SeasonScheduleBasePanel
 */
public SeasonScheduleBasePanel getSeasonScheduleBasePanel() {
	
	if( seasonScheduleBasePanel == null )
		seasonScheduleBasePanel = new SeasonScheduleBasePanel();
		
	return seasonScheduleBasePanel;
}
/**
 * getNextInputPanel method comment.
 */
protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(com.cannontech.common.gui.util.DataInputPanel currentInputPanel) {

	if( currentInputPanel == null )
	{
		return getSeasonScheduleBasePanel();
	}
	else
		throw new Error(getClass() + "::getNextInputPanel - Unable to determine next DataInputPanel");
}
/**
 * isLastInputPanel method comment.
 */
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) {

	return ( (currentPanel == getSeasonScheduleBasePanel()) );
}
}
