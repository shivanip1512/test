package com.cannontech.dbeditor.editor.port;

/**
 * This type was created in VisualAge.
 */

import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.port.DirectPort;
import com.cannontech.dbeditor.wizard.port.PooledPortListPanel;
 
public class PortEditorPanel extends com.cannontech.common.editor.PropertyPanel implements com.cannontech.common.editor.IMultiPanelEditor
{
	private DataInputPanel[] inputPanels;
	private String[] inputPanelTabNames;

	//The types of ports that each editor panel will instantiated for
	//editorPanels and classToPanelMap must stay in sync!
	private static final PaoType[][]  EDITOR_TYPES =
	{
		{		//0 - PortSettingsEditorPanel
		    PaoType.LOCAL_DIRECT, PaoType.LOCAL_SHARED, PaoType.LOCAL_RADIO,
		    PaoType.LOCAL_DIALUP, PaoType.TSERVER_DIRECT, PaoType.TSERVER_SHARED,
		    PaoType.TSERVER_RADIO, PaoType.TSERVER_DIALUP, PaoType.LOCAL_DIALBACK,
		    PaoType.DIALOUT_POOL, PaoType.TCPPORT, PaoType.UDPPORT
		},
		{		//1	- PortTimingsEditorPanel
		    PaoType.LOCAL_SHARED, PaoType.LOCAL_RADIO, PaoType.LOCAL_DIALUP, 
		    PaoType.TSERVER_SHARED, PaoType.TSERVER_RADIO, PaoType.TSERVER_DIALUP,
		    PaoType.LOCAL_DIALBACK, PaoType.TCPPORT, PaoType.UDPPORT
		},
		{		//2 - PortModemEditorPanel
		    PaoType.LOCAL_DIALUP, PaoType.TSERVER_DIALUP, PaoType.LOCAL_DIALBACK
		},
		//Until the background functionality is there, hide this panel.
		/*{		//3 - PortAlarmEditorPanel
			PortTypes.LOCAL_DIRECT, PortTypes.LOCAL_SHARED, PortTypes.LOCAL_RADIO,
			PortTypes.LOCAL_DIALUP, PortTypes.TSERVER_DIRECT, PortTypes.TSERVER_SHARED,
			PortTypes.TSERVER_RADIO, PortTypes.TSERVER_DIALUP, PortTypes.LOCAL_DIALBACK
		},*/
		{		//3 - PortSharingEditorPanel
		    PaoType.LOCAL_DIRECT, PaoType.LOCAL_SHARED, PaoType.LOCAL_RADIO,
		    PaoType.LOCAL_DIALUP, PaoType.TSERVER_DIRECT, PaoType.TSERVER_SHARED,
		    PaoType.TSERVER_RADIO, PaoType.TSERVER_DIALUP, PaoType.LOCAL_DIALBACK,
		    PaoType.UDPPORT
		},
		{		//4 - PortPool
		    PaoType.DIALOUT_POOL
		}
		
	};
	private JTabbedPane ivjPortEditorTabbedPane = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PortEditorPanel() {
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
@Override
public Object[] createNewPanel(int panelIndex)
{
	Object[] objs = new Object[2];
	
	switch( panelIndex )
	{
		case 0: 
			objs[0] = new com.cannontech.dbeditor.editor.port.PortSettingsEditorPanel();
			objs[1] = "General";
			break;

		case 1:
			objs[0] = new com.cannontech.dbeditor.editor.port.PortTimingsEditorPanel();
			objs[1] = "Timing";
			break;

		case 2:
			objs[0] = new com.cannontech.dbeditor.editor.port.PortModemEditorPanel();
			objs[1] = "Modem";
			break;
/*
		case 3:
			objs[0] = new com.cannontech.dbeditor.editor.port.PortAlarmEditorPanel();
			objs[1] = "Alarms";
			break;
*/
		case 3:
			objs[0] = new com.cannontech.dbeditor.editor.port.PortSharingEditorPanel();
			objs[1] = "Shared";
			break;

		case 4:
			objs[0] = new PooledPortListPanel();
			objs[1] = "Pooled Ports";
			break;

		case 6:
			boolean showIt = false;
			try
			{
			    Boolean.parseBoolean(ClientSession.getInstance().getRolePropertyValue(YukonRoleProperty.TRANS_EXCLUSION));
			}
			catch (Exception e)
			{
			    /* Leave showIt false */
			}

			if(showIt)
			{
				objs[0] = new com.cannontech.dbeditor.editor.device.PAOExclusionEditorPanel();
				objs[1] = "Exclusion List";
			}
			else
				objs = null;

			break;
	}
		
	return objs;
}
/**
 * This method was created in VisualAge.
 * @return DataInputPanel[]
 */
@Override
public DataInputPanel[] getInputPanels() {
	return this.inputPanels;
}
/**
 * Return the PortEditorTabbedPane property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getPortEditorTabbedPane() {
	if (ivjPortEditorTabbedPane == null) {
		try {
			ivjPortEditorTabbedPane = new javax.swing.JTabbedPane();
			ivjPortEditorTabbedPane.setName("PortEditorTabbedPane");
			ivjPortEditorTabbedPane.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPortEditorTabbedPane.setBounds(0, 0, 400, 350);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPortEditorTabbedPane;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
@Override
public java.awt.Dimension getPreferredSize() {
	return new java.awt.Dimension( 400, 450 );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String[]
 */
@Override
protected String[] getTabNames() {

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
		setName("PortEditorPanel");
		setPreferredSize(new java.awt.Dimension(400, 350));
		setLayout(null);
		setSize(400, 367);
		setMaximumSize(new java.awt.Dimension(10000, 10000));
		add(getPortEditorTabbedPane(), getPortEditorTabbedPane().getName());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		JFrame frame = new javax.swing.JFrame();
		PortEditorPanel aPortEditorPanel;
		aPortEditorPanel = new PortEditorPanel();
		frame.setContentPane(aPortEditorPanel);
		frame.setSize(aPortEditorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
            public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.editor.PropertyPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
@Override
public void setValue(Object val) {

	//Vector to hold the panels temporarily
	Vector<DataInputPanel> panels = new Vector<DataInputPanel>();
	Vector<Object> tabs = new Vector<Object>();
	
	DataInputPanel tempPanel;

	//We must assume that val is an instance of DirectPort	
	DirectPort port = (DirectPort) val;
	PaoType type = port.getPaoType();

 	for( int i = 0; i < EDITOR_TYPES.length; i++ )
 	{
	 	for( int j = 0; j < EDITOR_TYPES[i].length; j++ )
	 	{
		 	if( type == EDITOR_TYPES[i][j] )
			{
				Object[] panelTabs = createNewPanel(i);
				
				//do not add null panels
				if( panelTabs == null )
					continue;
				
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
@Override
public String toString() {
	return "Comm Channel Editor";
}

}
