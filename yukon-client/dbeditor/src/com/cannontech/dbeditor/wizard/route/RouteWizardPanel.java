package com.cannontech.dbeditor.wizard.route;

/**
 * This type was created in VisualAge.
 */
public class RouteWizardPanel extends com.cannontech.common.wizard.WizardPanel {
	private RepeaterSelectPanel repeaterSelectPanel;
	private RouteMacroCommunicationRoutesPanel routeMacroCommunicationRoutesPanel;
	private RouteMacroNamePanel routeMacroNamePanel;
	private RouteRepeaterQuestionPanel routeRepeaterQuestionPanel;
	private RouteNameDevicePanel routeNameDevicePanel;
	private RouteTypePanel routeTypePanel;
/**
 * RouteWizardPanel constructor comment.
 */
public RouteWizardPanel() {
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
	return "Route Setup";
}
/**
 * getNextInputPanel method comment.
 */
protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(com.cannontech.common.gui.util.DataInputPanel currentInputPanel) {

	if( currentInputPanel == null )
	{
		return getRouteTypePanel();
	}
	else
	if( currentInputPanel == getRouteTypePanel() )
	{
		if( ((RouteTypePanel) currentInputPanel).isRouteMacro() )
		{
			return getRouteMacroNamePanel();
		}
		else
		{
			getRouteType2Panel().setValue(null);
			return getRouteType2Panel();
		}
	}
	else
	if( currentInputPanel == getRouteType2Panel() )
	{
		return getRouteRepeaterQuestionPanel();
	}
	else
	if( currentInputPanel == getRouteRepeaterQuestionPanel() )
	{
		getRepeaterSelectPanel().setValue(null);
		return getRepeaterSelectPanel();
	}
	else
	if( currentInputPanel == getRouteMacroNamePanel() )
	{
		return getRouteMacroCommunicationRoutesPanel();
	}
	else
		throw new Error(getClass() + "::getNextInputPanel - Unable to determine next DataInputPanel");
		
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.route.RepeaterSelectPanel
 */
protected RepeaterSelectPanel getRepeaterSelectPanel() {
	
	if( repeaterSelectPanel == null )
		repeaterSelectPanel = new RepeaterSelectPanel();
		
	return repeaterSelectPanel;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.route.RouteMacroCommunicationRoutesPanel
 */
protected RouteMacroCommunicationRoutesPanel getRouteMacroCommunicationRoutesPanel() {
	
	if( routeMacroCommunicationRoutesPanel == null )
		routeMacroCommunicationRoutesPanel = new RouteMacroCommunicationRoutesPanel();
		
	return routeMacroCommunicationRoutesPanel;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.route.RouteMacroNamePanel
 */
protected RouteMacroNamePanel getRouteMacroNamePanel() {

	if( routeMacroNamePanel == null )
		routeMacroNamePanel = new RouteMacroNamePanel();
		
	return routeMacroNamePanel;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.route.RouteRepeaterQuestionPanel
 */
protected RouteRepeaterQuestionPanel getRouteRepeaterQuestionPanel() {
	
	if( routeRepeaterQuestionPanel == null )
		routeRepeaterQuestionPanel = new RouteRepeaterQuestionPanel();
		
	return routeRepeaterQuestionPanel;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.route.RouteType2Panel
 */
protected RouteNameDevicePanel getRouteType2Panel() {
	
	if( routeNameDevicePanel == null )
		routeNameDevicePanel = new RouteNameDevicePanel();
		
	return routeNameDevicePanel;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.route.RouteTypePanel
 */
protected RouteTypePanel getRouteTypePanel() {
	
	if( routeTypePanel == null )
		routeTypePanel = new RouteTypePanel();
		
	return routeTypePanel;
}
/**
 * isLastInputPanel method comment.
 */
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) {

	return (	( currentPanel == getRouteType2Panel() && !(((RouteNameDevicePanel) currentPanel).allowRebroadcast()) ) ||
						( currentPanel == getRouteRepeaterQuestionPanel() && !((RouteRepeaterQuestionPanel) currentPanel).isYesSelected() ) ||
						( currentPanel == getRepeaterSelectPanel() ) ||
						( currentPanel == getRouteMacroCommunicationRoutesPanel() ) ||
						( currentPanel == getRouteType2Panel() && getRouteType2Panel().noRepeaters() )		);
}

}
