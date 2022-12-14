/*
 * Created on Dec 12, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.dbeditor.wizard.config;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

public class ConfigWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{
	private Series200ConfigPanel series200ConfigPanel;
	private Series300ConfigPanel series300ConfigPanel;
	private Series400ConfigPanel series400ConfigPanel;
	private TypeChoicePanel typeChoicePanel;
	
/**
 * ConfigWizardPanel constructor comment.
 */
public ConfigWizardPanel() {
	super();
}

public java.awt.Dimension getActualSize() 
{
	setPreferredSize( new java.awt.Dimension(410, 480) );

	return getPreferredSize();
}

protected TypeChoicePanel getTypeChoicePanel() {
	if( typeChoicePanel == null )
		typeChoicePanel = new TypeChoicePanel();
		
	return typeChoicePanel;
}

protected Series200ConfigPanel getSeries200ConfigPanel() {
	if( series200ConfigPanel == null )
		series200ConfigPanel = new Series200ConfigPanel();
		
	return series200ConfigPanel;
}

/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.config.Series300ConfigPanel
 */
protected Series300ConfigPanel getSeries300ConfigPanel() {
	if( series300ConfigPanel == null )
		series300ConfigPanel = new Series300ConfigPanel();
		
	return series300ConfigPanel;
}

/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.config.Series400ConfigPanel
 */
protected Series400ConfigPanel getSeries400ConfigPanel() {
	if( series400ConfigPanel == null )
		series400ConfigPanel = new Series400ConfigPanel();
		
	return series400ConfigPanel;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */

protected String getHeaderText() {
	return "MCT Config Setup";
}


/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public java.awt.Dimension getMinimumSize() {
	return getPreferredSize();
}


/**
 * getNextInputPanel method comment.
 */
protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(
	com.cannontech.common.gui.util.DataInputPanel currentInputPanel)
{
	if (currentInputPanel == null)
	{
		getTypeChoicePanel().setFirstFocus();
        return getTypeChoicePanel();
	}
	else if (currentInputPanel == getTypeChoicePanel())
	{
		int mctType = getTypeChoicePanel().getSeriesSelectedType();
		
		if( mctType == 3 )
		{
			getSeries300ConfigPanel().setFirstFocus();
            return getSeries300ConfigPanel();
		}
		else if( mctType == 2)
		{
			getSeries200ConfigPanel().setFirstFocus();
            return getSeries200ConfigPanel();
		}
		else if( mctType == 4)
		{
			getSeries400ConfigPanel().setFirstFocus();
            return getSeries400ConfigPanel();
		}
	}

	return getTypeChoicePanel();

}


/**
 * isLastInputPanel method comment.
 */
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) 
{
	return  ( currentPanel == getSeries300ConfigPanel() 
            || currentPanel == getSeries200ConfigPanel()            
            || currentPanel == getSeries400ConfigPanel()); 
}

}