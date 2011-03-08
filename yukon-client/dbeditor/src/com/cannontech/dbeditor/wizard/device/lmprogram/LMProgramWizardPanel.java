package com.cannontech.dbeditor.wizard.device.lmprogram;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.pao.PaoType;

/**
 * Insert the type's description here.
 * Creation date: (12/6/00 3:21:12 PM)
 * @author: 
 */
public class LMProgramWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{
	private LMProgramTypePanel lmProgramTypePanel;
	private LMProgramBasePanel lmProgramBasePanel;
	private LMProgramControlWindowPanel lmProgramControlWindowPanel;
	private LMProgramListPanel lmProgramListPanel;
	private LMProgramCurtailListPanel lmProgramCurtailListPanel;
	private LMProgramDirectPanel lmProgramDirectPanel;
	private LMProgramEnergyExchangePanel lmProgramEnergyExchangePanel;
	private LMProgramEnergyExchangeCustomerListPanel lmProgramEnergyExchangeCustomerListPanel;
	
	private LMProgramCurtailmentPanel lmProgramCurtailmentPanel;
	private LMProgramDirectNotifGroupListPanel lmProgramDirectNotifGroupListPanel;
/**
 * LMGroupWizardPanel constructor comment.
 */
public LMProgramWizardPanel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (5/4/2001 11:11:28 AM)
 * @return java.awt.Dimension
 */
public java.awt.Dimension getActualSize() 
{
	setPreferredSize( new java.awt.Dimension(430, 500) );

	return getPreferredSize();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
protected String getHeaderText() {
	return "LM Program Setup";
}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2001 10:09:07 AM)
 * @return com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramBasePanel
 */
public LMProgramBasePanel getLmProgramBasePanel() 
{
	if( lmProgramBasePanel == null ) {
		PaoType programType = getLmProgramTypePanel().getLMSelectedType();
		lmProgramBasePanel = new LMProgramBasePanel(programType);
	}
		
	return lmProgramBasePanel;
}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2001 10:09:07 AM)
 * @return LMProgramControlWindowPanel
 */
public LMProgramControlWindowPanel getLMProgramControlWindowPanel() 
{
	if( lmProgramControlWindowPanel == null )
		lmProgramControlWindowPanel = new LMProgramControlWindowPanel();
		
	return lmProgramControlWindowPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 1:00:22 PM)
 * @return com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramCurtailListPanel
 */
public LMProgramCurtailListPanel getLmProgramCurtailListPanel() 
{
	if( lmProgramCurtailListPanel == null )
		lmProgramCurtailListPanel = new LMProgramCurtailListPanel();

	return lmProgramCurtailListPanel;	
}
/**
 * Insert the method's description here.
 * Creation date: (5/4/2001 12:02:36 PM)
 * @return com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramCurtailmentPanel
 */
public LMProgramCurtailmentPanel getLmProgramCurtailmentPanel() 
{
	if( lmProgramCurtailmentPanel == null )
		lmProgramCurtailmentPanel = new LMProgramCurtailmentPanel();

	return lmProgramCurtailmentPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (5/14/2001 10:47:12 AM)
 * @return com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramDirectPanel
 */
public LMProgramDirectPanel getLmProgramDirectPanel() 
{
	if( lmProgramDirectPanel == null ) {
		PaoType programType = getLmProgramTypePanel().getLMSelectedType();
		lmProgramDirectPanel = new LMProgramDirectPanel(programType);
	}

	return lmProgramDirectPanel;
}

public LMProgramDirectNotifGroupListPanel getLmProgramDirectCustomerListPanel() 
{
	if( lmProgramDirectNotifGroupListPanel == null )
		lmProgramDirectNotifGroupListPanel = new LMProgramDirectNotifGroupListPanel();

	return lmProgramDirectNotifGroupListPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/2001 2:30:31 PM)
 * @return com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramEnergyExchangeCustomerListPanel
 */
public LMProgramEnergyExchangeCustomerListPanel getLmProgramEnergyExchangeCustomerListPanel() 
{
	if( lmProgramEnergyExchangeCustomerListPanel == null )
		lmProgramEnergyExchangeCustomerListPanel = new LMProgramEnergyExchangeCustomerListPanel();
		
	return lmProgramEnergyExchangeCustomerListPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/2001 12:25:07 PM)
 * @return com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramEnergyExchangePanel
 */
public LMProgramEnergyExchangePanel getLmProgramEnergyExchangePanel() 
{
	if( lmProgramEnergyExchangePanel == null )
		lmProgramEnergyExchangePanel = new LMProgramEnergyExchangePanel();
		
	return lmProgramEnergyExchangePanel;
}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2001 11:00:32 AM)
 * @return com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramListPanel
 */
public LMProgramListPanel getLmProgramListPanel() 
{
	if( lmProgramListPanel == null )
		lmProgramListPanel = new LMProgramListPanel();
		
	return lmProgramListPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2001 10:29:08 AM)
 * @return com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramTypePanel
 */
public LMProgramTypePanel getLmProgramTypePanel() 
{
	if( lmProgramTypePanel == null )
		lmProgramTypePanel = new LMProgramTypePanel();
		
	return lmProgramTypePanel;
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
         getLmProgramTypePanel().setFirstFocus();
         return getLmProgramTypePanel();
    }
    else
	if( currentInputPanel == getLmProgramTypePanel() )
	{
        getLmProgramBasePanel().setIsAWizardOp(true);
        getLmProgramBasePanel().getJLabelActualProgType().setText(getLmProgramTypePanel().getLMSelectedType().getPaoTypeName());
        getLmProgramBasePanel().setTriggerThresholdVisible(true);
        getLmProgramBasePanel().setFirstFocus();
        return getLmProgramBasePanel();
    } else if( currentInputPanel == getLmProgramBasePanel() )
	{
            getLmProgramDirectPanel().setFirstFocus();
            return getLmProgramDirectPanel();
	}
	// Direct program begin
	else if( currentInputPanel == getLmProgramDirectPanel() )
	{
		getLMProgramControlWindowPanel().setTimedOperationalStateCondition(getLmProgramBasePanel().isTimedOperationalState());
		getLMProgramControlWindowPanel().getWindowChangePasser().setSelected(getLmProgramBasePanel().isTimedOperationalState());
        getLMProgramControlWindowPanel().setFirstFocus();
		return getLMProgramControlWindowPanel();
	}
	else if( currentInputPanel == getLMProgramControlWindowPanel() )
	{		
		getLmProgramListPanel().initLeftList(!getLmProgramDirectPanel().hasLatchingGear(),
		                                     getLmProgramTypePanel().getLMSelectedType());
        getLmProgramListPanel().setFirstFocus();
		return getLmProgramListPanel();
	}
	else if( currentInputPanel == getLmProgramListPanel() )
	{
	    getLmProgramDirectCustomerListPanel().setFirstFocus();
        return getLmProgramDirectCustomerListPanel();
	}
	return null;
}


protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) 
{
	//we dont use the getters for each panel here since this call creates new instances of each
	return ( currentPanel == lmProgramDirectNotifGroupListPanel);
//				|| currentPanel == lmProgramCurtailListPanel
//				|| currentPanel == lmProgramEnergyExchangeCustomerListPanel );
}

@Override
protected boolean isBackButtonSupported(DataInputPanel inputPanel) {
      if (inputPanel instanceof LMProgramBasePanel) {
            return false;
      }
      return super.isBackButtonSupported(inputPanel);
}

}
