package com.cannontech.dbeditor.wizard.holidayschedule;

/**
 * This type was created in VisualAge.
 */
public class HolidayScheduleWizardPanel extends com.cannontech.common.wizard.WizardPanel {
	private HolidayScheduleBasePanel holidayScheduleBasePanel;
/**
 * RouteWizardPanel constructor comment.
 */
public HolidayScheduleWizardPanel() {
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
 * @return java.lang.String
 */
protected String getHeaderText() {
	return "Holiday Schedule Setup";
}
/**
 * This method was created in VisualAge.
 * @return HolidayScheduleBasePanel
 */
public HolidayScheduleBasePanel getHolidayScheduleBasePanel() {
	
	if( holidayScheduleBasePanel == null )
		holidayScheduleBasePanel = new HolidayScheduleBasePanel();
		
	return holidayScheduleBasePanel;
}
/**
 * getNextInputPanel method comment.
 */
protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(com.cannontech.common.gui.util.DataInputPanel currentInputPanel) {

	if( currentInputPanel == null )
	{
		return getHolidayScheduleBasePanel();
	}
	else
		throw new Error(getClass() + "::getNextInputPanel - Unable to determine next DataInputPanel");
}
/**
 * isLastInputPanel method comment.
 */
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) {

	return ( (currentPanel == getHolidayScheduleBasePanel()) );
}
}
