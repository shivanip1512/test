package com.cannontech.dbeditor.wizard.device.lmprogram;

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
	private LMProgramDirectNotificationPanel lmProgramDirectNotificationPanel;
	private LMProgramDirectCustomerListPanel lmProgramDirectCustomerListPanel;
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
	if( lmProgramBasePanel == null )
		lmProgramBasePanel = new LMProgramBasePanel();
		
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
	if( lmProgramDirectPanel == null )
		lmProgramDirectPanel = new LMProgramDirectPanel();

	return lmProgramDirectPanel;
}

public LMProgramDirectNotificationPanel getLmProgramDirectNotificationPanel() 
{
	if( lmProgramDirectNotificationPanel == null )
		lmProgramDirectNotificationPanel = new LMProgramDirectNotificationPanel();

	return lmProgramDirectNotificationPanel;
}

public LMProgramDirectCustomerListPanel getLmProgramDirectCustomerListPanel() 
{
	if( lmProgramDirectCustomerListPanel == null )
		lmProgramDirectCustomerListPanel = new LMProgramDirectCustomerListPanel();

	return lmProgramDirectCustomerListPanel;
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
		return getLmProgramTypePanel();
	}
	else if( currentInputPanel == getLmProgramTypePanel() )
	{
		return getLmProgramBasePanel();
	}
	else if( currentInputPanel == getLmProgramBasePanel() )
	{
		if( getLmProgramTypePanel().getLMSelectedType() == com.cannontech.database.data.pao.PAOGroups.LM_CURTAIL_PROGRAM )
			return getLmProgramCurtailmentPanel();
		else if( getLmProgramTypePanel().getLMSelectedType() == com.cannontech.database.data.pao.PAOGroups.LM_DIRECT_PROGRAM )
			return getLmProgramDirectPanel();
		else if( getLmProgramTypePanel().getLMSelectedType() == com.cannontech.database.data.pao.PAOGroups.LM_ENERGY_EXCHANGE_PROGRAM )
			return getLmProgramEnergyExchangePanel();		
	}
	//Curtailment program begin
	else if( currentInputPanel == getLmProgramCurtailmentPanel() )
	{
		return getLmProgramCurtailListPanel();
	}
	// Direct program begin
	else if( currentInputPanel == getLmProgramDirectPanel() )
	{
		return getLMProgramControlWindowPanel();
	}
	else if( currentInputPanel == getLMProgramControlWindowPanel() )
	{		
		getLmProgramListPanel().initLeftList( !getLmProgramDirectPanel().hasLatchingGear() );
		return getLmProgramListPanel();
	}
	else if( currentInputPanel == getLmProgramListPanel() )
	{
		return getLmProgramDirectNotificationPanel();
	}
	else if( currentInputPanel == getLmProgramDirectNotificationPanel() )
	{
		return getLmProgramDirectCustomerListPanel();
	}
	//EExchange program begin
	else if( currentInputPanel == getLmProgramEnergyExchangePanel() )
	{
		return getLmProgramEnergyExchangeCustomerListPanel();
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
	//we dont use the getters for each panel here since this call creates new instances of each
	return ( currentPanel == lmProgramDirectCustomerListPanel
				|| currentPanel == lmProgramCurtailListPanel
				|| currentPanel == lmProgramEnergyExchangeCustomerListPanel );
}
}
