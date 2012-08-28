package com.cannontech.dbeditor.editor.user;

import com.cannontech.common.gui.util.DataInputPanel;

/**
 * This type was created in VisualAge.
 */
public class LoginEditorPanel extends com.cannontech.common.editor.PropertyPanel implements com.cannontech.common.editor.IMultiPanelEditor
{
	private DataInputPanel[] inputPanels;
	private String[] inputPanelTabNames;
	public static final int PANEL_COUNT = 1;

	public LoginEditorPanel() {
		super();
		initialize();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/15/2002 1:17:24 PM)
	 * @return Object[]
	 * 
	 *  This method should return an object array with 2 elements,
	 *   Object[0] is a DataInputPanel
	 *   Object[1] is a String (Tab Name)
	 */
	public Object[] createNewPanel(int panelIndex)
	{
		Object[] objs = new Object[2];
		
		switch( panelIndex )
		{
			case 0: 
				objs[0] = new UserLoginBasePanel();
				objs[1] = "General";
				break;

		}
			
		return objs;
	}
	
	private int getUserRolePanelIndx()
	{
		for( int i = 0 ; i < getTabbedPane().getTabCount(); i++ )
			if( getTabbedPane().getComponentAt(i) instanceof UserRolePanel )
				return i;
			
		return -1;
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
	private void initialize() {
		try {
			setName("LoginEditorPanel");
			setPreferredSize(new java.awt.Dimension(400, 350));
			setLayout(null);
			setSize(400, 350);
			setMaximumSize(new java.awt.Dimension(400, 350));
	
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	
	/**
	 * This method was created in VisualAge.
	 * @param val java.lang.Object
	 */
	public void setValue(Object val) {
		
		//Vector to hold the panels temporarily
		java.util.Vector panels = new java.util.Vector();
		java.util.Vector tabs = new java.util.Vector();
		
		DataInputPanel tempPanel;	

		for( int i = 0; i < PANEL_COUNT; i++ )
		{
			Object[] panelTabs = createNewPanel(i);
			tempPanel = (DataInputPanel)panelTabs[0];
			panels.addElement( tempPanel );
			tabs.addElement( panelTabs[1] );
			
			if(tempPanel instanceof UserGroupRoleEditorPanel) {
				i++;
			}
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
		return "Login Editor";
	}
}
