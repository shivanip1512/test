/*
 * Created on Mar 8, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.dbeditor.wizard.device.lmscenario;

import com.cannontech.common.wizard.WizardPanel;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMScenarioWizardPanel extends WizardPanel 
{
	private LMScenarioProgramSettingsPanel lmScenarioProgramSettingsPanel;
	/**
	 * LMGroupWizardPanel constructor comment.
	 */
	public LMScenarioWizardPanel() {
		super();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/2/2004 4:01:28 PM)
	 * @return java.awt.Dimension
	 */
	public java.awt.Dimension getActualSize() 
	{
		setPreferredSize( new java.awt.Dimension(410, 500) );

		return getPreferredSize();
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	protected String getHeaderText() {
		return "LM Control Scenario Setup";
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/2/2004 4:01:28 PM)
	 * @return com.cannontech.dbeditor.wizard.device.lmcontrolarea.LMControlScenarioPanel
	 */
	
	public LMScenarioProgramSettingsPanel getLmScenarioProgramSettingsPanel() 
	{
		if( lmScenarioProgramSettingsPanel == null )
			lmScenarioProgramSettingsPanel = new LMScenarioProgramSettingsPanel();
		
		return lmScenarioProgramSettingsPanel;
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
			return getLmScenarioProgramSettingsPanel();
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
		return (currentPanel == getLmScenarioProgramSettingsPanel() );
	}
	

}


