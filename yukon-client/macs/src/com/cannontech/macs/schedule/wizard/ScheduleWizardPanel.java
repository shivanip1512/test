package com.cannontech.macs.schedule.wizard;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.message.macs.message.Schedule;

public class ScheduleWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{
	private ScheduleBasePanel scheduleBasePanel;
	private SimpleSchedulePanel simpleSchedulePanel;
	private ScriptSchedulePanel scriptSchedulePanel;

/**
 * RouteWizardPanel constructor comment.
 */
public ScheduleWizardPanel() {
	super();
}
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
 * @return java.lang.String
 */
protected String getHeaderText() {
	return "Schedule Setup";
}
/**
 * getNextInputPanel method comment.
 */
protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(com.cannontech.common.gui.util.DataInputPanel currentInputPanel) 
{
	if( currentInputPanel == null )
	{
		return getScheduleBasePanel();
	}
	else if( currentInputPanel == getScheduleBasePanel() )
	{
		if( getScheduleBasePanel().getScheduleType().equalsIgnoreCase( Schedule.SCRIPT_TYPE ) )		
			return getScriptSchedulePanel();
		else
			return getSimpleSchedulePanel();
	}
	else
		throw new Error(getClass() + "::getNextInputPanel - Unable to determine next DataInputPanel");

}
/**
 * Insert the method's description here.
 * Creation date: (2/15/2001 10:38:48 AM)
 * @return com.cannontech.macs.gui.schedule.ScheduleBasePanel
 */
public ScheduleBasePanel getScheduleBasePanel() 
{
	if( scheduleBasePanel == null )
		scheduleBasePanel = new ScheduleBasePanel();
		
	return scheduleBasePanel;
}
/**
 * Insert the method's description here.
 * Creation date: (2/15/2001 12:48:03 PM)
 * @return com.cannontech.macs.gui.schedule.ScriptSchedulePanel
 */
public ScriptSchedulePanel getScriptSchedulePanel() 
{
	if( scriptSchedulePanel == null )
		scriptSchedulePanel = new ScriptSchedulePanel();
		
	return scriptSchedulePanel;
}
/**
 * Insert the method's description here.
 * Creation date: (2/15/2001 12:48:03 PM)
 * @return com.cannontech.macs.gui.schedule.SimpleSchedulePanel
 */
public SimpleSchedulePanel getSimpleSchedulePanel() 
{
	if( simpleSchedulePanel == null )
		simpleSchedulePanel = new SimpleSchedulePanel();
		
	return simpleSchedulePanel;
}
/**
 * isLastInputPanel method comment.
 */
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) {

	return ( currentPanel == getScriptSchedulePanel() ||
				currentPanel == getSimpleSchedulePanel() );
}
}
