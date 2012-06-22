package com.cannontech.dbeditor.editor.device;

import java.util.Vector;

import com.cannontech.common.editor.EditorInputValidationException;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.login.ClientSession;
import com.cannontech.database.data.device.Repeater850;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.dbeditor.wizard.device.DeviceRDSTerminalPanel;
import com.cannontech.dbeditor.wizard.device.DeviceTNPPTerminalPanel;
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
			PAOGroups.REPEATER, PAOGroups.REPEATER_902, PAOGroups.REPEATER_800, PAOGroups.REPEATER_801, PAOGroups.REPEATER_850, PAOGroups.REPEATER_921, PAOGroups.RTUILEX, PAOGroups.RTUWELCO, 
			PAOGroups.DR_87, PAOGroups.TAPTERMINAL, PAOGroups.TNPP_TERMINAL, PAOGroups.WCTP_TERMINAL, PAOGroups.SNPP_TERMINAL,
			PAOGroups.VIRTUAL_SYSTEM, PAOGroups.DCT_501, PAOGroups.RTU_DNP, PAOGroups.RTU_DART,
			PAOGroups.ION_7700, PAOGroups.ION_7330, PAOGroups.ION_8300, PAOGroups.RTU_MODBUS,
			PAOGroups.MCTBROADCAST, PAOGroups.MCT310CT, PAOGroups.MCT310IM, PAOGroups.MCT310IDL,
			PAOGroups.TRANSDATA_MARKV, PAOGroups.SERIES_5_LMI, 
			PAOGroups.RTC, PAOGroups.KV, PAOGroups.KVII, PAOGroups.RTM, PAOGroups.MCT410IL, PAOGroups.MCT410CL,
            PAOGroups.MCT410FL, PAOGroups.MCT410GL, PAOGroups.MCT470, PAOGroups.MCT430A, PAOGroups.MCT430S4, PAOGroups.MCT430SL, PAOGroups.MCT430A3, 
            PAOGroups.SENTINEL, PAOGroups.FOCUS, PAOGroups.ALPHA_A3, PAOGroups.FAULT_CI, PAOGroups.NEUTRAL_MONITOR, PAOGroups.LCR3102, PAOGroups.LCR6200_RFN, PAOGroups.LCR6600_RFN,
            PAOGroups.RFN410FL, PAOGroups.RFN410FX, PAOGroups.RFN410FD, PAOGroups.RFN420FL, PAOGroups.RFN420FX, PAOGroups.RFN420FD,
            PAOGroups.RFN420CL, PAOGroups.RFN420CD,
            PAOGroups.RFN430KV, PAOGroups.RFN430A3, PAOGroups.RDS_TERMINAL, 
            PAOGroups.MCT420FL, PAOGroups.MCT420FD, PAOGroups.MCT420CL, PAOGroups.MCT420CD,
            PAOGroups.RFWMETER,
            PAOGroups.IPC410FL, PAOGroups.IPC420FD, PAOGroups.IPC430S4E, PAOGroups.IPC430SL,
            PAOGroups.RFN420ELO, PAOGroups.RFN430ELO
		},
		{ 	//1 - DeviceMeterGroupEditorPanel
			PAOGroups.ALPHA_A1, PAOGroups.ALPHA_PPLUS, PAOGroups.FULCRUM, PAOGroups.VECTRON, PAOGroups.QUANTUM, 
			PAOGroups.SIXNET, PAOGroups.LANDISGYRS4, PAOGroups.MCT310, PAOGroups.MCT318, PAOGroups.MCT360,
			PAOGroups.MCT370, PAOGroups.MCT240, PAOGroups.LMT_2, PAOGroups.MCT248, PAOGroups.MCT250, PAOGroups.MCT210,
			PAOGroups.DR_87, PAOGroups.MCT310ID, PAOGroups.MCT310IL, PAOGroups.MCT318L, PAOGroups.MCT213,
			PAOGroups.DCT_501, PAOGroups.MCT310CT, PAOGroups.MCT310IM, PAOGroups.ION_7330,
			PAOGroups.ION_7700, PAOGroups.ION_8300, PAOGroups.MCT310IDL, 
			PAOGroups.TRANSDATA_MARKV, PAOGroups.KV, PAOGroups.KVII, PAOGroups.MCT410IL, PAOGroups.MCT410CL,
            PAOGroups.MCT410FL, PAOGroups.MCT410GL, PAOGroups.MCT470, PAOGroups.MCT430A, PAOGroups.MCT430S4, PAOGroups.MCT430SL, PAOGroups.MCT430A3, 
            PAOGroups.SENTINEL, PAOGroups.FOCUS, PAOGroups.ALPHA_A3, PAOGroups.LCR3102,
            PAOGroups.RFN410FL, PAOGroups.RFN410FX, PAOGroups.RFN410FD, PAOGroups.RFN420FL, PAOGroups.RFN420FX, PAOGroups.RFN420FD,
            PAOGroups.RFN420CL, PAOGroups.RFN420CD,
            PAOGroups.RFN430KV, PAOGroups.RFN430A3, 
            PAOGroups.MCT420FL, PAOGroups.MCT420FD, PAOGroups.MCT420CL, PAOGroups.MCT420CD,
            PAOGroups.RFWMETER,
            PAOGroups.IPC410FL, PAOGroups.IPC420FD, PAOGroups.IPC430S4E, PAOGroups.IPC430SL,
            PAOGroups.RFN420ELO, PAOGroups.RFN430ELO
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
			PAOGroups.MCT210, PAOGroups.MCT213, PAOGroups.REPEATER, PAOGroups.REPEATER_902, PAOGroups.REPEATER_800, PAOGroups.REPEATER_801, PAOGroups.REPEATER_850, PAOGroups.REPEATER_921,
			PAOGroups.RTUILEX, PAOGroups.RTUWELCO, PAOGroups.DR_87, PAOGroups.SIXNET, 
			PAOGroups.MCT310ID, PAOGroups.MCT310IL, PAOGroups.MCT318L, PAOGroups.DCT_501,
         	PAOGroups.DNP_CBC_6510, PAOGroups.RTU_DNP, PAOGroups.MCT310CT, PAOGroups.MCT310IM,
         	PAOGroups.ION_7700, PAOGroups.ION_7330, PAOGroups.ION_8300, PAOGroups.RTU_DART,
         	PAOGroups.MCT310IDL, PAOGroups.TRANSDATA_MARKV, PAOGroups.SERIES_5_LMI, PAOGroups.RTU_MODBUS,
         	PAOGroups.RTC,  PAOGroups.KV, PAOGroups.KVII, PAOGroups.RTM, PAOGroups.MCT410IL, PAOGroups.MCT410CL,
            PAOGroups.MCT410FL, PAOGroups.MCT410GL, PAOGroups.MCT470, PAOGroups.MCT430A, PAOGroups.MCT430S4, PAOGroups.MCT430SL, PAOGroups.MCT430A3,
            PAOGroups.SENTINEL, PAOGroups.FOCUS, PAOGroups.ALPHA_A3, PAOGroups.LCR3102,
            PAOGroups.MCT420FL, PAOGroups.MCT420FD, PAOGroups.MCT420CL, PAOGroups.MCT420CD,
            PAOGroups.IPC410FL, PAOGroups.IPC420FD, PAOGroups.IPC430S4E, PAOGroups.IPC430SL,
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
			PAOGroups.TAPTERMINAL, PAOGroups.TNPP_TERMINAL, PAOGroups.WCTP_TERMINAL,
			PAOGroups.SERIES_5_LMI, PAOGroups.RTC, PAOGroups.SNPP_TERMINAL, PAOGroups.RDS_TERMINAL
			
		},
		{	//11 - ExclusionTimingEditorPanel
			PAOGroups.CCU710A, PAOGroups.CCU711, PAOGroups.CCU721, PAOGroups.TCU5000, PAOGroups.TCU5500, 
			PAOGroups.LCU415, PAOGroups.LCU_T3026, PAOGroups.LCULG, PAOGroups.LCU_ER, 
			PAOGroups.TAPTERMINAL, PAOGroups.TNPP_TERMINAL, PAOGroups.WCTP_TERMINAL,
			PAOGroups.SERIES_5_LMI, PAOGroups.RTC, PAOGroups.SNPP_TERMINAL, PAOGroups.RDS_TERMINAL
		},
		{	//12 - DeviceVerificationAssignmentPanel
			PAOGroups.RTM
		},
		{
			//13 - DeviceMCT400SeriesOptionPanel
			PAOGroups.MCT410IL, PAOGroups.MCT410CL, PAOGroups.MCT410FL, PAOGroups.MCT410GL,
			PAOGroups.MCT420FL
		},
        {
            //14 - TNPPTermOptionPanel
            PAOGroups.TNPP_TERMINAL
        },
        {
            //15 - RDSTerminalPanel
            PAOGroups.RDS_TERMINAL
        }

};

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
         
		case 3:
			objs[0] = new com.cannontech.dbeditor.editor.device.Series5SettingsEditorPanel();
			objs[1] = "Series 5 Settings";
			break;
		
		case 4:
			objs[0] = new com.cannontech.dbeditor.editor.device.DeviceScanRateEditorPanel();
			objs[1] = "Scan Rate";
			break;

		case 5:
			objs[0] = new com.cannontech.dbeditor.editor.device.DeviceTapTerminalEditorPanel();
			objs[1] = "Paging";
			break;
			
		case 6:
			objs[0] = new com.cannontech.dbeditor.editor.device.DeviceMCTIEDPortEditorPanel();
			objs[1] = "IED Port";
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
			
		case 14:
		    objs[0] = new DeviceTNPPTerminalPanel();
	        objs[1] = "TNPP Settings";
	        break;
        
        case 15:
            objs[0] = new DeviceRDSTerminalPanel();
            objs[1] = "RDS Settings";
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
 * This method was created in VisualAge.
 * @return java.lang.String[]
 */
public String[] getTabNames() {
	return this.inputPanelTabNames;
}

public Object getValue(Object o) {
    try {
        o = super.getValue(o);
    } catch (EditorInputValidationException e) {  /* Ignore */  }
    
    if(o instanceof Repeater850)
    {
            javax.swing.JOptionPane.showMessageDialog( this, 
                                                       "It is recommended that this repeater has no more than 10 devices connected.", 
                                                       "Warning", 
                                                       javax.swing.JOptionPane.WARNING_MESSAGE );
    }
    
    return o;
}


/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {

	//Vector to hold the panels temporarily
	Vector<DataInputPanel> panels = new Vector<DataInputPanel>();
	Vector<Object> tabs = new Vector<Object>();
	
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