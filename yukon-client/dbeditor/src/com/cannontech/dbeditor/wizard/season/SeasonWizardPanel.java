/*
 * Created on May 5, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.dbeditor.wizard.season;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SeasonWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{

	private SeasonBasePanel seasonWizPanel;
	
	/**
	 * SeasonsWizardPanel constructor comment.
	 */
	public SeasonWizardPanel() {
		super();
	}

	public java.awt.Dimension getActualSize() 
	{
		setPreferredSize( new java.awt.Dimension(410, 480) );

		return getPreferredSize();
	}

	protected SeasonBasePanel getSeasonsBasePanel() {
		if( seasonWizPanel == null )
			seasonWizPanel = new SeasonBasePanel();
		
		return seasonWizPanel;
	}

	protected String getHeaderText() {
		return "Seasons Setup";
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
		return getSeasonsBasePanel();
	}


	/**
	 * isLastInputPanel method comment.
	 */
	protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) 
	{
		return  ( currentPanel == getSeasonsBasePanel());
	}


	/**
	 * This method was created in VisualAge.
	 * @param args java.lang.String[]
	 */
	public static void main(String args[]) {
		try
	{
		javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		SeasonWizardPanel p = new SeasonWizardPanel();

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