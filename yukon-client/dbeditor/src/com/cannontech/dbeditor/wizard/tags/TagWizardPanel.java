package com.cannontech.dbeditor.wizard.tags;

/**
 * Insert the type's description here.
 * Creation date: (1/15/2004 2:31:36 PM)
 * @author: 
 */
public class TagWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{

	private TagsBasePanel tagsWizPanel;
	
	/**
	 * TagsWizardPanel constructor comment.
	 */
	public TagWizardPanel() {
		super();
	}

	public java.awt.Dimension getActualSize() 
	{
		setPreferredSize( new java.awt.Dimension(410, 480) );

		return getPreferredSize();
	}

	protected TagsBasePanel getTagsBasePanel() {
		if( tagsWizPanel == null )
			tagsWizPanel = new TagsBasePanel();
		
		return tagsWizPanel;
	}

	protected String getHeaderText() {
		return "Tags Setup";
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
		return getTagsBasePanel();
	}


	/**
	 * isLastInputPanel method comment.
	 */
	protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) 
	{
		return  ( currentPanel == getTagsBasePanel());
	}


	/**
	 * This method was created in VisualAge.
	 * @param args java.lang.String[]
	 */
	public static void main(String args[]) {
		try
	{
		javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		TagWizardPanel p = new TagWizardPanel();

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