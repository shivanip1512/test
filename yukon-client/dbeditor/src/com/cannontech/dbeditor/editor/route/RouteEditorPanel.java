package com.cannontech.dbeditor.editor.route;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.database.data.pao.RouteTypes;
import com.cannontech.database.data.route.RouteBase;

/**
 * This type was created in VisualAge.
 */
public class RouteEditorPanel extends com.cannontech.common.editor.PropertyPanel implements com.cannontech.common.editor.IMultiPanelEditor
{
	private DataInputPanel[] inputPanels;
	private String[] inputPanelTabNames;

	private static final String [][] EDITOR_TYPES =
	{
		{  //0 - CommunicationRouteEditorPanel
			RouteTypes.STRING_CCU, RouteTypes.STRING_TCU, RouteTypes.STRING_LCU, 
			RouteTypes.STRING_TAP_PAGING, RouteTypes.STRING_WCTP_TERMINAL_ROUTE,
			RouteTypes.STRING_VERSACOM, RouteTypes.STRING_SERIES_5_LMI_ROUTE,
			RouteTypes.STRING_RTC_ROUTE, RouteTypes.STRING_SNPP_TERMINAL_ROUTE
		},
		{  //1 - RepeaterSetupEditorPanel
			RouteTypes.STRING_CCU
		},
		{  //2 - MacroRouteEditorPanel
			RouteTypes.STRING_MACRO
		},
		{  //3 - VersacomAddressingEditorPanel
			RouteTypes.STRING_VERSACOM
		},
		{  //4 - VersacomCCUSettingsEditorPanel
			RouteTypes.STRING_VERSACOM
		}
	};
	private javax.swing.JTabbedPane ivjRouteEditorTabbedPane = null;
public RouteEditorPanel() {
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
			objs[0] = new com.cannontech.dbeditor.editor.route.CommunicationRouteEditorPanel();
			objs[1] = "General";
			break;

		case 1:
			objs[0] = new com.cannontech.dbeditor.editor.route.RepeaterSetupEditorPanel();
			objs[1] = "Repeater Setup";
			break;

		case 2:
			objs[0] = new com.cannontech.dbeditor.editor.route.MacroRouteEditorPanel();
			objs[1] = "Macro Route";
			break;

		case 3:
			objs[0] = new com.cannontech.dbeditor.editor.route.VersacomAddressingEditorPanel();
			objs[1] = "Addressing";
			break;

		case 4:
			objs[0] = new com.cannontech.dbeditor.editor.route.VersacomCCUSettingsEditorPanel();
			objs[1] = "CCU Settings";
			break;
	}
		
	return objs;
}
/**
 * This method was created in VisualAge.
 * @return DataInputPanel[]
 */
public DataInputPanel[] getInputPanels() {
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
 * Return the RouteEditorTabbedPane property value.
 * @return javax.swing.JTabbedPane
 */
private javax.swing.JTabbedPane getRouteEditorTabbedPane() {
	if (ivjRouteEditorTabbedPane == null) {
		try {
			ivjRouteEditorTabbedPane = new javax.swing.JTabbedPane();
			ivjRouteEditorTabbedPane.setName("RouteEditorTabbedPane");
			ivjRouteEditorTabbedPane.setPreferredSize(new java.awt.Dimension(400, 350));
			ivjRouteEditorTabbedPane.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRouteEditorTabbedPane.setBounds(0, 0, 400, 350);
			ivjRouteEditorTabbedPane.setMaximumSize(new java.awt.Dimension(400, 350));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRouteEditorTabbedPane;
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
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("RouteEditorPanel");
		setPreferredSize(new java.awt.Dimension(400, 350));
		setLayout(null);
		setSize(400, 350);
		setMaximumSize(new java.awt.Dimension(400, 350));
		add(getRouteEditorTabbedPane(), getRouteEditorTabbedPane().getName());
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

	//We must assume that val is an instance of RouteBase	
	RouteBase route = (RouteBase) val;
	String type = route.getPAOType();

 	for( int i = 0; i < EDITOR_TYPES.length; i++ )
 	{
	 	for( int j = 0; j < EDITOR_TYPES[i].length; j++ )
	 	{
		 	if( type.equalsIgnoreCase(EDITOR_TYPES[i][j]) )
			{
				Object[] panelTabs = createNewPanel(i);

				tempPanel = (DataInputPanel)panelTabs[0];
				panels.addElement( tempPanel );
				tabs.addElement( panelTabs[1] );
				break;				
			}
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
	return "Route Editor";
}
}
