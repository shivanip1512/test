/*
 * Created on Mar 2, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.dbeditor.editor.device.lmconstraint;

import com.cannontech.common.gui.util.DataInputPanel;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMConstraintEditorPanel extends com.cannontech.common.editor.PropertyPanel implements com.cannontech.common.editor.IMultiPanelEditor
{
	
	private DataInputPanel[] inputPanels;
	private String[] inputPanelTabNames;
	
	public LMConstraintEditorPanel() {
		super();
		initialize();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/2/2004 4:17:24 PM)
	 * @return Object[]
	 * 
	 *  This method should return an object array with 2 elements,
	 *   Object[0] is a DataInputPanel
	 *   Object[1] is a String (Tab Name)
	 */
	public Object[] createNewPanel(int panelIndex)
	{
		Object[] objs = new Object[2];
	
		objs[0] = new com.cannontech.dbeditor.wizard.device.lmconstraint.LMProgramConstraintPanel();
		objs[1] = "General";
		
		return objs;
	}
	/**
	 * This method was created in VisualAge.
	 * @return DataInputPanel[]
	 */
	public DataInputPanel[] getInputPanels() {
		//At least guarantee a non-null array if not a meaningful one
		if( this.inputPanels == null )
			this.inputPanels = new DataInputPanel[0];
		
		return this.inputPanels;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.awt.Dimension
	 */
	public java.awt.Dimension getPreferredSize() {
		return new java.awt.Dimension( 400, 450 );
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String[]
	 */
	public String[] getTabNames() {
		if( this.inputPanelTabNames == null )
			this.inputPanelTabNames = new String[0];
		
		return this.inputPanelTabNames;
	}
	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {

		/* Uncomment the following lines to print uncaught exceptions to stdout */
		com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("ProgramConstraintEditorPanel");
			setPreferredSize(new java.awt.Dimension(400, 350));
			setLayout(null);
			setSize(400, 350);
			setMaximumSize(new java.awt.Dimension(400, 350));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		// user code end
	}
	/**
	 * This method was created in VisualAge.
	 * @param val java.lang.Object
	 */
	public void setValue(Object val) 
	{	
		//Vector to hold the panels temporarily
		java.util.Vector panels = new java.util.Vector();
		java.util.Vector tabs = new java.util.Vector();
		DataInputPanel tempPanel;
		final int PANEL_COUNT = 1;

		for( int i = 0; i < PANEL_COUNT; i++ )
		{
			Object[] panelTabs = createNewPanel(i);

			tempPanel = (DataInputPanel)panelTabs[0];
			panels.addElement( tempPanel );
			tabs.addElement( panelTabs[1] );
		}
	
		this.inputPanels = new DataInputPanel[panels.size()];
		panels.copyInto( this.inputPanels );

		this.inputPanelTabNames = new String[tabs.size()];
		tabs.copyInto( this.inputPanelTabNames );
	
		//Allow super to do whatever it needs to
		super.setValue( val );
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	public String toString() {
		return "Program Constraint Editor";
	}
	
}
