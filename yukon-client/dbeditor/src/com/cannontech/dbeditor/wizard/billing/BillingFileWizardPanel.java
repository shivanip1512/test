package com.cannontech.dbeditor.wizard.billing;

/**
 * This type was created in VisualAge.
 */

public class BillingFileWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{
	private BillingFileGenerationPanel billingFileGenerationPanel;

/**
 * BillingFileWizardPanel constructor comment.
 */
public BillingFileWizardPanel() {
	super();
	setFinishedEnabled(false);
}
/**
 * Insert the method's description here.
 * Creation date: (5/4/2001 11:11:28 AM)
 * @return java.awt.Dimension
 */
public java.awt.Dimension getActualSize() 
{
	setPreferredSize( new java.awt.Dimension(410, 500) );

	return getPreferredSize();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.device.DeviceIEDNamePanel
 */
protected BillingFileGenerationPanel getBillingFileGenerationPanel() {
	if( billingFileGenerationPanel == null )
		billingFileGenerationPanel = new BillingFileGenerationPanel();
		
	return billingFileGenerationPanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
protected String getHeaderText() {
	return "Create Billing File";
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
	currentInputPanel = getBillingFileGenerationPanel();
	return currentInputPanel;
}
/**
 * isLastInputPanel method comment.
 */


 
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) 
{
	return true;
}
/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 */
public static void main(String args[]) {
	try
{
	javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
	BillingFileWizardPanel p = new BillingFileWizardPanel();

	javax.swing.JFrame f= new javax.swing.JFrame();

	f.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("dbEditorIcon.gif"));
	f.getContentPane().add( p );
	f.pack();

	f.show();
}
catch( Throwable t)
{
	com.cannontech.clientutils.CTILogger.error( t.getMessage(), t );
}
}
}
