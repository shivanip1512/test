package com.cannontech.dbeditor.wizard.state;

import com.cannontech.common.gui.wizard.state.GroupStateNamePanel;
import com.cannontech.database.db.state.StateGroupUtils;

/**
 * This type was created in VisualAge.
 */
public class StateWizardPanel extends com.cannontech.common.wizard.WizardPanel {
	private GroupStateNamePanel groupStateNamePanel;
    private GroupTypePanel groupTypePanel;
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

public GroupTypePanel getGroupTypePanel() {
    
    if( groupTypePanel == null )
        groupTypePanel = new GroupTypePanel();
        
    return groupTypePanel;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
protected String getHeaderText() {
	return "State Group Setup";
}
/**
 * getNextInputPanel method comment.
 */
protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(com.cannontech.common.gui.util.DataInputPanel currentInputPanel) {

	if( currentInputPanel == null )
	{
		getGroupTypePanel().setFirstFocus();
        return getGroupTypePanel();
	}else if (currentInputPanel == getGroupTypePanel()) 
    {
        getGroupStateNamePanel().setFirstFocus();
        if(getGroupTypePanel().getGroupType() == StateGroupUtils.GROUP_TYPE_ANALOG)
        {
            getGroupStateNamePanel().setTypeAnalog();
            
        }
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
