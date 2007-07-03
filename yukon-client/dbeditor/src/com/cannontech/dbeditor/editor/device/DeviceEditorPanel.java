package com.cannontech.dbeditor.editor.device;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.login.ClientSession;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.dbeditor.DatabaseEditor;
import com.cannontech.dbeditor.editor.device.configuration.DeviceConfigurationComboPanel;
import com.cannontech.roles.application.DBEditorRole;

/**
 * This type was created in VisualAge.
 */
public class DeviceEditorPanel extends com.cannontech.common.editor.PropertyPanel implements com.cannontech.common.editor.IMultiPanelEditor
{
	private DataInputPanel[] inputPanels;
	private String[] inputPanelTabNames;
	
	private static final int[][] EDITOR_TYPES =
	{
		{   //0 - DeviceBaseEditorPanel
			PAOGroups.CCU710A, PAOGroups.CCU711, PAOGroups.CCU721, PAOGroups.TCU5000, PAOGroups.TCU5500, 
			PAOGroups.LCU415, PAOGroups.LCU_T3026, PAOGroups.LCULG, PAOGroups.LCU_ER, PAOGroups.ALPHA_A1, 
			PAOGroups.ALPHA_PPLUS, PAOGroups.FULCRUM, PAOGroups.VECTRON, PAOGroups.QUANTUM,
			PAOGroups.DAVISWEATHER, PAOGroups.LANDISGYRS4, PAOGroups.SIXNET, PAOGroups.MCT310, 
			PAOGroups.MCT318, PAOGroups.MCT310ID, PAOGroups.MCT310IL, PAOGroups.MCT318L, 
			PAOGroups.MCT360, PAOGroups.MCT370, PAOGroups.MCT240, PAOGroups.LMT_2, 
			PAOGroups.MCT248, PAOGroups.MCT250, PAOGroups.MCT210, PAOGroups.MCT213,
			PAOGroups.REPEATER, PAOGroups.REPEATER_902, PAOGroups.REPEATER_800, PAOGroups.REPEATER_801, PAOGroups.REPEATER_921, PAOGroups.RTUILEX, PAOGroups.RTUWELCO, 
			PAOGroups.DR_87, PAOGroups.TAPTERMINAL, PAOGroups.WCTP_TERMINAL, PAOGroups.SNPP_TERMINAL,
			PAOGroups.VIRTUAL_SYSTEM, PAOGroups.DCT_501, PAOGroups.RTU_DNP, PAOGroups.RTU_DART,
			PAOGroups.ION_7700, PAOGroups.ION_7330, PAOGroups.ION_8300, PAOGroups.RTU_MODBUS,
			PAOGroups.MCTBROADCAST, PAOGroups.MCT310CT, PAOGroups.MCT310IM, PAOGroups.MCT310IDL,
			PAOGroups.TRANSDATA_MARKV, PAOGroups.SERIES_5_LMI, 
			PAOGroups.RTC, PAOGroups.KV, PAOGroups.KVII, PAOGroups.RTM, PAOGroups.MCT410IL, PAOGroups.MCT410CL,
            PAOGroups.MCT410FL, PAOGroups.MCT410GL, PAOGroups.MCT470, PAOGroups.MCT430A, PAOGroups.MCT430S4, PAOGroups.MCT430SN, 
            PAOGroups.SENTINEL, PAOGroups.ALPHA_A3, PAOGroups.FAULT_CI, PAOGroups.NEUTRAL_CS
		},
		{ 	//1 - DeviceMeterGroupEditorPanel
			PAOGroups.ALPHA_A1, PAOGroups.ALPHA_PPLUS, PAOGroups.FULCRUM, PAOGroups.VECTRON, PAOGroups.QUANTUM, 
			PAOGroups.SIXNET, PAOGroups.LANDISGYRS4, PAOGroups.MCT310, PAOGroups.MCT318, PAOGroups.MCT360,
			PAOGroups.MCT370, PAOGroups.MCT240, PAOGroups.LMT_2, PAOGroups.MCT248, PAOGroups.MCT250, PAOGroups.MCT210,
			PAOGroups.DR_87, PAOGroups.MCT310ID, PAOGroups.MCT310IL, PAOGroups.MCT318L, PAOGroups.MCT213,
			PAOGroups.DCT_501, PAOGroups.MCT310CT, PAOGroups.MCT310IM, PAOGroups.ION_7330,
			PAOGroups.ION_7700, PAOGroups.ION_8300, PAOGroups.MCT310IDL, 
			PAOGroups.TRANSDATA_MARKV, PAOGroups.KV, PAOGroups.KVII, PAOGroups.MCT410IL, PAOGroups.MCT410CL,
            PAOGroups.MCT410FL, PAOGroups.MCT410GL, PAOGroups.MCT470, PAOGroups.MCT430A, PAOGroups.MCT430S4, PAOGroups.MCT430SN, 
            PAOGroups.SENTINEL, PAOGroups.ALPHA_A3
		},
      	{   //2 - CapBankController
         PAOGroups.CAPBANKCONTROLLER, PAOGroups.CBC_FP_2800, PAOGroups.DNP_CBC_6510, PAOGroups.CBC_EXPRESSCOM,
         PAOGroups.CBC_7010, PAOGroups.CBC_7020
     	 },
	  	{	//3 - Series5SettingsEditorPanel
		  PAOGroups.SERIES_5_LMI
	  	},
		{	//4 - DeviceScanRateEditorPanel
			PAOGroups.CCU710A, PAOGroups.CCU711, PAOGroups.CCU721, PAOGroups.TCU5000, PAOGroups.TCU5500, 
			PAOGroups.LCU415, PAOGroups.LCU_T3026, PAOGroups.LCULG, PAOGroups.LCU_ER,
			PAOGroups.ALPHA_A1, PAOGroups.ALPHA_PPLUS, PAOGroups.FULCRUM, PAOGroups.VECTRON, 
			PAOGroups.QUANTUM, PAOGroups.DAVISWEATHER, PAOGroups.LANDISGYRS4,
			PAOGroups.MCT310, PAOGroups.MCT318, PAOGroups.MCT360, PAOGroups.MCT370, 
			PAOGroups.MCT240, PAOGroups.LMT_2, PAOGroups.MCT248, PAOGroups.MCT250, 
			PAOGroups.MCT210, PAOGroups.MCT213, PAOGroups.REPEATER, PAOGroups.REPEATER_902, PAOGroups.REPEATER_800, PAOGroups.REPEATER_801, PAOGroups.REPEATER_921,
			PAOGroups.RTUILEX, PAOGroups.RTUWELCO, PAOGroups.DR_87, PAOGroups.SIXNET, 
			PAOGroups.MCT310ID, PAOGroups.MCT310IL, PAOGroups.MCT318L, PAOGroups.DCT_501,
         	PAOGroups.DNP_CBC_6510, PAOGroups.RTU_DNP, PAOGroups.MCT310CT, PAOGroups.MCT310IM,
         	PAOGroups.ION_7700, PAOGroups.ION_7330, PAOGroups.ION_8300, PAOGroups.RTU_DART,
         	PAOGroups.MCT310IDL, PAOGroups.TRANSDATA_MARKV, PAOGroups.SERIES_5_LMI, PAOGroups.RTU_MODBUS,
         	PAOGroups.RTC,  PAOGroups.KV, PAOGroups.KVII, PAOGroups.RTM, PAOGroups.MCT410IL, PAOGroups.MCT410CL,
            PAOGroups.MCT410FL, PAOGroups.MCT410GL, PAOGroups.MCT470, PAOGroups.MCT430A, PAOGroups.MCT430S4, 
            PAOGroups.SENTINEL, PAOGroups.ALPHA_A3
		},

		{   //5 - TapTerminalPanel
			PAOGroups.TAPTERMINAL, PAOGroups.WCTP_TERMINAL, PAOGroups.SNPP_TERMINAL
		},
		{   //6 - MCTIEDPort
			PAOGroups.MCT360, PAOGroups.MCT370
		},
		/*
		 * Until the background functionality is there, no point in showing this panel
		 *{   //6 - Alarm
			PAOGroups.CCU710A, PAOGroups.CCU711, PAOGroups.TCU5000, PAOGroups.TCU5500, 
			PAOGroups.LCU415, PAOGroups.LCU_T3026, PAOGroups.LCULG, PAOGroups.LCU_ER,
			PAOGroups.ALPHA_A1, PAOGroups.ALPHA_PPLUS, PAOGroups.FULCRUM, 
			PAOGroups.VECTRON, PAOGroups.QUANTUM, PAOGroups.DAVISWEATHER, PAOGroups.LANDISGYRS4,
			PAOGroups.MCT310, PAOGroups.MCT318, PAOGroups.MCT360, PAOGroups.MCT370, 
			PAOGroups.MCT240, PAOGroups.LMT_2, PAOGroups.MCT248, PAOGroups.MCT250, 
			PAOGroups.MCT210, PAOGroups.REPEATER, PAOGroups.REPEATER_800, PAOGroups.RTUILEX, 
			PAOGroups.RTUWELCO, PAOGroups.TAPTERMINAL, PAOGroups.WCTP_TERMINAL, PAOGroups.SNPP_TERMINAL, 
			PAOGroups.MCT213, PAOGroups.SIXNET, PAOGroups.MCT310CT, PAOGroups.MCT310IM,
			PAOGroups.DR_87, PAOGroups.MCT310ID, PAOGroups.MCT310IL, PAOGroups.MCT318L,
			PAOGroups.DCT_501, PAOGroups.DNP_CBC_6510, PAOGroups.RTU_DNP, PAOGroups.RTU_DART,
			PAOGroups.ION_7700, PAOGroups.ION_7330, PAOGroups.ION_8300, PAOGroups.MCT310IDL,
			PAOGroups.MCT410_KWH_ONLY, PAOGroups.TRANSDATA_MARKV,  PAOGroups.KV, PAOGroups.KVII,
			PAOGroups.RTM, PAOGroups.SENTINEL, PAOGroups.ALPHA_A3
		},*/
		{   //7 - CapBank
			PAOGroups.CAPBANK
		},
		{   //8 - CapBankSettingsPanel
			PAOGroups.CAPBANK
		},
		{	//9 - MCTBroadcastListEditorPanel
			PAOGroups.MCTBROADCAST
		},
		{	//10 - PAOExclusionEditorPanel
			PAOGroups.CCU710A, PAOGroups.CCU711, PAOGroups.CCU721, PAOGroups.TCU5000, PAOGroups.TCU5500, 
			PAOGroups.LCU415, PAOGroups.LCU_T3026, PAOGroups.LCULG, PAOGroups.LCU_ER, 
			PAOGroups.REPEATER, PAOGroups.REPEATER_902, PAOGroups.REPEATER_800, PAOGroups.REPEATER_801, PAOGroups.REPEATER_921, PAOGroups.TAPTERMINAL, PAOGroups.WCTP_TERMINAL,
			PAOGroups.SERIES_5_LMI, PAOGroups.RTC, PAOGroups.SNPP_TERMINAL
			
		},
		{	//11 - ExclusionTimingEditorPanel
			PAOGroups.CCU710A, PAOGroups.CCU711, PAOGroups.CCU721, PAOGroups.TCU5000, PAOGroups.TCU5500, 
			PAOGroups.LCU415, PAOGroups.LCU_T3026, PAOGroups.LCULG, PAOGroups.LCU_ER, 
			PAOGroups.REPEATER, PAOGroups.REPEATER_902, PAOGroups.REPEATER_800, PAOGroups.REPEATER_801, PAOGroups.REPEATER_921, PAOGroups.TAPTERMINAL, PAOGroups.WCTP_TERMINAL,
			PAOGroups.SERIES_5_LMI, PAOGroups.RTC, PAOGroups.SNPP_TERMINAL
		},
		{	//12 - DeviceVerificationAssignmentPanel
			PAOGroups.RTM
		},
		{
			//13 - DeviceMCT400SeriesOptionPanel
			PAOGroups.MCT410IL, PAOGroups.MCT410CL, PAOGroups.MCT410FL, PAOGroups.MCT410GL
		}

};
	private javax.swing.JTabbedPane ivjDeviceEditorTabbedPane = null;

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
			objs[0] = new com.cannontech.dbeditor.editor.device.DeviceBaseEditorPanel();
			objs[1] = "General";
			break;

		case 1:
			objs[0] = new com.cannontech.dbeditor.editor.device.DeviceMeterGroupEditorPanel();
			objs[1] = "Metering";
			break;

      	case 2:
         	objs[0] = new com.cannontech.dbeditor.editor.device.capcontrol.DeviceCapBankControllerEditorPanel();
         	objs[1] = "General";
        	break;
         
		case 3:
			objs[0] = new com.cannontech.dbeditor.editor.device.Series5SettingsEditorPanel();
			objs[1] = "Series 5 Settings";
			break;
		
		case 4:
			objs[0] = new com.cannontech.dbeditor.editor.device.DeviceScanRateEditorPanel();
			objs[1] = "Scan Rate";
			break;

/*		case 4:
			objs[0] = new com.cannontech.dbeditor.editor.device.DeviceStatisticsEditorPanel();
			objs[1] = "Statistics";
			break;
*/
		case 5:
			objs[0] = new com.cannontech.dbeditor.editor.device.DeviceTapTerminalEditorPanel();
			objs[1] = "Paging";
			break;
			
		case 6:
			objs[0] = new com.cannontech.dbeditor.editor.device.DeviceMCTIEDPortEditorPanel();
			objs[1] = "IED Port";
			break;
/*
		case 6:
			objs[0] = new com.cannontech.dbeditor.editor.device.DeviceAlarmEditorPanel();
			objs[1] = "Alarm";
			break;
*/
		case 7:
			objs[0] = new com.cannontech.dbeditor.editor.device.capcontrol.DeviceCapBankEditorPanel();
			objs[1] = "General";
			break;
	
		case 8:
			objs[0] = new com.cannontech.dbeditor.editor.device.capcontrol.CapBankInfoPanel();
			objs[1] = "Information";
			break;

		case 9:
			objs[0] = new com.cannontech.dbeditor.wizard.device.MCTBroadcastListEditorPanel();
			objs[1] = "MCT Assignment";
			break;

		case 10:
			String showIt = 
				ClientSession.getInstance().getRolePropertyValue(
					DBEditorRole.TRANS_EXCLUSION, "false");
	
			if( "TRUE".equalsIgnoreCase(showIt) )
			{
				objs[0] = new com.cannontech.dbeditor.editor.device.PAOExclusionEditorPanel();
				objs[1] = "Exclusion List";
			}
			else
				objs = null;
			
			break;
			
		case 11:
			String showItToo = 
				ClientSession.getInstance().getRolePropertyValue(
					DBEditorRole.TRANS_EXCLUSION, "false");
	
			if( "TRUE".equalsIgnoreCase(showItToo) )
			{
				objs[0] = new com.cannontech.dbeditor.editor.device.ExclusionTimingEditorPanel();
				objs[1] = "Exclusion Timing";
			}
			else
				objs = null;
			break;
		
		case 12:
			objs[0] = new com.cannontech.dbeditor.editor.device.DeviceVerificationAssignmentPanel();
			objs[1] = "Verification";
			break;
			
		case 13:
			objs[0] = new com.cannontech.dbeditor.editor.device.DeviceMCT400SeriesOptionsPanel();
			objs[1] = "MCT Settings";
			break;
	}
		
	return objs;
}


/**
 * Return the DeviceEditorTabbedPane property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getDeviceEditorTabbedPane() {
	if (ivjDeviceEditorTabbedPane == null) {
		try {
			ivjDeviceEditorTabbedPane = new javax.swing.JTabbedPane();
			ivjDeviceEditorTabbedPane.setName("DeviceEditorTabbedPane");
			ivjDeviceEditorTabbedPane.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDeviceEditorTabbedPane.setBounds(0, 0, 400, 350);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeviceEditorTabbedPane;
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
 * This method was created in VisualAge.
 * @return java.lang.String[]
 */
public String[] getTabNames() {
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
		setName("DeviceEditorPanel");
		setLayout(null);
		setSize(400, 350);
		add(getDeviceEditorTabbedPane(), getDeviceEditorTabbedPane().getName());
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
public void setValue(Object val) {

	//Vector to hold the panels temporarily
	java.util.Vector panels = new java.util.Vector();
	java.util.Vector tabs = new java.util.Vector();
	
	DataInputPanel tempPanel;

	//We must assume that val is an instance of DeviceBase	
	com.cannontech.database.data.device.DeviceBase device = (com.cannontech.database.data.device.DeviceBase) val;
	int type = com.cannontech.database.data.pao.PAOGroups.getDeviceType( device.getPAOType() );
	
	//com.cannontech.database.data.pao.YukonPAObject device = (com.cannontech.database.data.pao.YukonPAObject)val;
	//int type = DeviceTypes.getType( device.get.getType() );

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

    // Add the device configuration tab if this user can see device configuration
    if(DatabaseEditor.showDeviceConfiguration()){
     	panels.addElement( new DeviceConfigurationComboPanel() );
     	tabs.addElement( "Device Configuration" );
    }
	this.inputPanels = new DataInputPanel[panels.size()];
	panels.copyInto( this.inputPanels );

	this.inputPanelTabNames = new String[tabs.size()];
	tabs.copyInto( this.inputPanelTabNames );
	
	super.setValue(val);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "Device Editor";
}

}