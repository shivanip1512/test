package com.cannontech.dbeditor.wizard.baseline;

import com.cannontech.dbeditor.wizard.baseline.BaselineMainPanel;

/**
 * This type was created in VisualAge.
 */
public class BaselineWizardPanel extends com.cannontech.common.wizard.WizardPanel {
	private BaselineMainPanel baselineMainPanel;
/**
 * RouteWizardPanel constructor comment.
 */
public BaselineWizardPanel() {
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
	return "Baseline Setup";
}
/**
 * This method was created in VisualAge.
 * @return BaselineMainPanel
 */
public BaselineMainPanel getBaselineMainPanel() {
	
	if( baselineMainPanel == null )
		baselineMainPanel = new BaselineMainPanel();
		
	return baselineMainPanel;
}
/**
 * getNextInputPanel method comment.
 */
protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(com.cannontech.common.gui.util.DataInputPanel currentInputPanel) {

	if( currentInputPanel == null )
	{
		return getBaselineMainPanel();
	}
	else
		throw new Error(getClass() + "::getNextInputPanel - Unable to determine next DataInputPanel");
}
/**
 * isLastInputPanel method comment.
 */
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) {

	return ( (currentPanel == getBaselineMainPanel()) );
}
}
