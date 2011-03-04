package com.cannontech.dbeditor.wizard.device.lmcontrolarea;

/**
 * Insert the type's description here.
 * Creation date: (12/6/00 3:21:12 PM)
 * @author: 
 */
public class LMControlAreaWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{
	private LMControlAreaBasePanel lmControlAreaBasePanel;
	private LMControlAreaTriggerPanel lmControlAreaTriggerPanel;
	private LMControlAreaProgramPanel lmControlAreaProgramPanel;
/**
 * LMGroupWizardPanel constructor comment.
 */
public LMControlAreaWizardPanel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (5/4/2001 11:11:28 AM)
 * @return java.awt.Dimension
 */
public java.awt.Dimension getActualSize() 
{
	setPreferredSize( new java.awt.Dimension(410, 595) );

	return getPreferredSize();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
protected String getHeaderText() {
	return "LM Control Area Setup";
}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2001 2:12:25 PM)
 * @return com.cannontech.dbeditor.wizard.device.lmcontrolarea.LMControlAreaBasePanel
 */
public LMControlAreaBasePanel getLmControlAreaBasePanel() 
{
	if( lmControlAreaBasePanel == null )
		lmControlAreaBasePanel = new LMControlAreaBasePanel();
		
	return lmControlAreaBasePanel;
}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2001 2:55:50 PM)
 * @return com.cannontech.dbeditor.wizard.device.lmcontrolarea.LMControlAreaProgramPanel
 */
public LMControlAreaProgramPanel getLMControlAreaProgramPanel() 
{
	if( lmControlAreaProgramPanel == null )
		lmControlAreaProgramPanel = new LMControlAreaProgramPanel();
		
	return lmControlAreaProgramPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (3/21/2001 10:15:19 AM)
 * @return com.cannontech.dbeditor.wizard.device.lmcontrolarea.LMControlAreaTriggerPanel
 */
public LMControlAreaTriggerPanel getLmControlAreaTriggerPanel() 
{
	if( lmControlAreaTriggerPanel == null )
		lmControlAreaTriggerPanel = new LMControlAreaTriggerPanel();
		
	return lmControlAreaTriggerPanel;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public java.awt.Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.common.gui.util.InputPanel
 * @param currentInputPanel com.cannontech.common.gui.util.InputPanel
 */
protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(com.cannontech.common.gui.util.DataInputPanel currentInputPanel) 
{
	if( currentInputPanel == null )
	{
		getLmControlAreaBasePanel().setFirstFocus();
        return getLmControlAreaBasePanel();
	}
	else if( currentInputPanel == getLmControlAreaBasePanel() )
	{
        getLmControlAreaTriggerPanel().setFirstFocus();
        return getLmControlAreaTriggerPanel();
	}
	else if( currentInputPanel == getLmControlAreaTriggerPanel() )
	{
        getLMControlAreaProgramPanel().setFirstFocus();
        return getLMControlAreaProgramPanel();
	}

	return null;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param currentPanel com.cannontech.common.gui.util.DataInputPanel
 */
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) 
{
	return (currentPanel == getLMControlAreaProgramPanel() );
}
}
