package com.cannontech.dbeditor.editor.device;

import java.util.Vector;

import com.cannontech.common.editor.EditorInputValidationException;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.device.Repeater850;
import com.cannontech.dbeditor.wizard.device.DeviceRDSTerminalPanel;
import com.cannontech.dbeditor.wizard.device.DeviceTNPPTerminalPanel;

/**
 * This type was created in VisualAge.
 */
public class DeviceEditorPanel extends com.cannontech.common.editor.PropertyPanel implements com.cannontech.common.editor.IMultiPanelEditor
{
	private DataInputPanel[] inputPanels;
	private String[] inputPanelTabNames;
	
	private static final PaoType[][] EDITOR_TYPES =
	{
		{   //0 - DeviceBaseEditorPanel
			PaoType.CCU710A, PaoType.CCU711, PaoType.CCU721, PaoType.TCU5000, PaoType.TCU5500, 
			PaoType.LCU415, PaoType.LCU_T3026, PaoType.LCULG, PaoType.LCU_ER, PaoType.ALPHA_A1, 
			PaoType.ALPHA_PPLUS, PaoType.FULCRUM, PaoType.VECTRON, PaoType.QUANTUM,
			PaoType.DAVISWEATHER, PaoType.LANDISGYRS4, PaoType.SIXNET, PaoType.MCT310, 
			PaoType.MCT318, PaoType.MCT310ID, PaoType.MCT310IL, PaoType.MCT318L, 
			PaoType.MCT360, PaoType.MCT370, PaoType.MCT240, PaoType.LMT_2, 
			PaoType.MCT248, PaoType.MCT250, PaoType.MCT210, PaoType.MCT213,
			PaoType.REPEATER, PaoType.REPEATER_902, PaoType.REPEATER_800, PaoType.REPEATER_801, PaoType.REPEATER_850, PaoType.REPEATER_921, PaoType.RTUILEX, PaoType.RTUWELCO, 
			PaoType.DR_87, PaoType.TAPTERMINAL, PaoType.TNPP_TERMINAL, PaoType.WCTP_TERMINAL, PaoType.SNPP_TERMINAL,
			PaoType.VIRTUAL_SYSTEM, PaoType.DCT_501, PaoType.RTU_DNP, PaoType.RTU_DART,
			PaoType.ION_7700, PaoType.ION_7330, PaoType.ION_8300, PaoType.RTU_MODBUS,
			PaoType.MCTBROADCAST, PaoType.MCT310CT, PaoType.MCT310IM, PaoType.MCT310IDL,
			PaoType.TRANSDATA_MARKV, PaoType.SERIES_5_LMI, 
			PaoType.RTC, PaoType.KV, PaoType.KVII, PaoType.RTM, PaoType.MCT410IL, PaoType.MCT410CL,
            PaoType.MCT410FL, PaoType.MCT410GL, PaoType.MCT470, PaoType.MCT430A, PaoType.MCT430S4, PaoType.MCT430SL, PaoType.MCT430A3, 
            PaoType.SENTINEL, PaoType.FOCUS, PaoType.ALPHA_A3, PaoType.FAULT_CI, PaoType.NEUTRAL_MONITOR, PaoType.LCR3102, PaoType.LCR6200_RFN, PaoType.LCR6600_RFN, PaoType.LCR6601S, PaoType.LCR6700_RFN,
            PaoType.RFN410FL, PaoType.RFN410FX, PaoType.RFN410FD, PaoType.RFN420FL, PaoType.RFN420FX, PaoType.RFN420FD, PaoType.RFN420FRX, PaoType.RFN420FRD,
            PaoType.RFN410CL, PaoType.RFN420CL, PaoType.RFN420CD,
            PaoType.RFN430KV, PaoType.RFN430A3D, PaoType.RFN430A3T, PaoType.RFN430A3K, PaoType.RFN430A3R, PaoType.RDS_TERMINAL,
//            PaoType.RFN440_2131T, PaoType.RFN440_2132T, PaoType.RFN440_2133T,
            PaoType.RFN440_2131TD, PaoType.RFN440_2132TD, PaoType.RFN440_2133TD,
            PaoType.MCT420FL, PaoType.MCT420FD, PaoType.MCT420CL, PaoType.MCT420CD,
            PaoType.MCT440_2131B, PaoType.MCT440_2132B, PaoType.MCT440_2133B,  
            PaoType.RFWMETER, PaoType.RFW201, PaoType.RFG201, 
            PaoType.IPC410FL, PaoType.IPC420FD, PaoType.IPC430S4E, PaoType.IPC430SL,
            PaoType.RFN430SL0, PaoType.RFN430SL1, PaoType.RFN430SL2, PaoType.RFN430SL3, PaoType.RFN430SL4,
            PaoType.RFN510FL, PaoType.RFN520FAX, PaoType.RFN520FRX, PaoType.RFN520FAXD, PaoType.RFN520FRXD,
            PaoType.RFN530FAX, PaoType.RFN530FRX, 
            PaoType.RFN530S4X, PaoType.RFN530S4EAX, PaoType.RFN530S4EAXR, PaoType.RFN530S4ERX, PaoType.RFN530S4ERXR,
            PaoType.RFN_1200,
		},
		{ 	//1 - DeviceMeterGroupEditorPanel
			PaoType.ALPHA_A1, PaoType.ALPHA_PPLUS, PaoType.FULCRUM, PaoType.VECTRON, PaoType.QUANTUM, 
			PaoType.SIXNET, PaoType.LANDISGYRS4, PaoType.MCT310, PaoType.MCT318, PaoType.MCT360,
			PaoType.MCT370, PaoType.MCT240, PaoType.LMT_2, PaoType.MCT248, PaoType.MCT250, PaoType.MCT210,
			PaoType.DR_87, PaoType.MCT310ID, PaoType.MCT310IL, PaoType.MCT318L, PaoType.MCT213,
			PaoType.DCT_501, PaoType.MCT310CT, PaoType.MCT310IM, PaoType.ION_7330,
			PaoType.ION_7700, PaoType.ION_8300, PaoType.MCT310IDL, 
			PaoType.TRANSDATA_MARKV, PaoType.KV, PaoType.KVII, PaoType.MCT410IL, PaoType.MCT410CL,
            PaoType.MCT410FL, PaoType.MCT410GL, PaoType.MCT470, PaoType.MCT430A, PaoType.MCT430S4, PaoType.MCT430SL, PaoType.MCT430A3, 
            PaoType.SENTINEL, PaoType.FOCUS, PaoType.ALPHA_A3, PaoType.LCR3102,
            PaoType.RFN410FL, PaoType.RFN410FX, PaoType.RFN410FD, PaoType.RFN420FL, PaoType.RFN420FX, PaoType.RFN420FD, PaoType.RFN420FRX, PaoType.RFN420FRD,
            PaoType.RFN410CL, PaoType.RFN420CL, PaoType.RFN420CD,
            PaoType.RFN430KV, PaoType.RFN430A3D, PaoType.RFN430A3T, PaoType.RFN430A3K, PaoType.RFN430A3R,
//          PaoType.RFN440_2131T, PaoType.RFN440_2132T, PaoType.RFN440_2133T,
          PaoType.RFN440_2131TD, PaoType.RFN440_2132TD, PaoType.RFN440_2133TD,
            PaoType.MCT420FL, PaoType.MCT420FD, PaoType.MCT420CL, PaoType.MCT420CD,
            PaoType.MCT440_2131B, PaoType.MCT440_2132B, PaoType.MCT440_2133B,
            PaoType.RFWMETER, PaoType.RFW201, PaoType.RFG201,
            PaoType.IPC410FL, PaoType.IPC420FD, PaoType.IPC430S4E, PaoType.IPC430SL,
            PaoType.RFN430SL0, PaoType.RFN430SL1, PaoType.RFN430SL2, PaoType.RFN430SL3, PaoType.RFN430SL4,
            PaoType.RFN510FL, PaoType.RFN520FAX, PaoType.RFN520FRX, PaoType.RFN520FAXD, PaoType.RFN520FRXD,
            PaoType.RFN530FAX, PaoType.RFN530FRX, 
            PaoType.RFN530S4X, PaoType.RFN530S4EAX, PaoType.RFN530S4EAXR, PaoType.RFN530S4ERX, PaoType.RFN530S4ERXR
		},
      	{   //2 - CapBankController
         PaoType.CAPBANKCONTROLLER, PaoType.CBC_FP_2800, PaoType.CBC_EXPRESSCOM,
         PaoType.CBC_7010, PaoType.CBC_7020
     	 },
	  	{	//3 - Series5SettingsEditorPanel
		  PaoType.SERIES_5_LMI
	  	},
		{	//4 - DeviceScanRateEditorPanel
			PaoType.CCU710A, PaoType.CCU711, PaoType.CCU721, PaoType.TCU5000, PaoType.TCU5500, 
			PaoType.LCU415, PaoType.LCU_T3026, PaoType.LCULG, PaoType.LCU_ER,
			PaoType.ALPHA_A1, PaoType.ALPHA_PPLUS, PaoType.FULCRUM, PaoType.VECTRON, 
			PaoType.QUANTUM, PaoType.DAVISWEATHER, PaoType.LANDISGYRS4,
			PaoType.MCT310, PaoType.MCT318, PaoType.MCT360, PaoType.MCT370, 
			PaoType.MCT240, PaoType.LMT_2, PaoType.MCT248, PaoType.MCT250, 
			PaoType.MCT210, PaoType.MCT213, PaoType.REPEATER, PaoType.REPEATER_902, PaoType.REPEATER_800, PaoType.REPEATER_801, PaoType.REPEATER_850, PaoType.REPEATER_921,
			PaoType.RTUILEX, PaoType.RTUWELCO, PaoType.DR_87, PaoType.SIXNET, 
			PaoType.MCT310ID, PaoType.MCT310IL, PaoType.MCT318L, PaoType.DCT_501,
         	PaoType.RTU_DNP, PaoType.MCT310CT, PaoType.MCT310IM,
         	PaoType.ION_7700, PaoType.ION_7330, PaoType.ION_8300, PaoType.RTU_DART,
         	PaoType.MCT310IDL, PaoType.TRANSDATA_MARKV, PaoType.SERIES_5_LMI, PaoType.RTU_MODBUS,
         	PaoType.RTC,  PaoType.KV, PaoType.KVII, PaoType.RTM, PaoType.MCT410IL, PaoType.MCT410CL,
            PaoType.MCT410FL, PaoType.MCT410GL, PaoType.MCT470, PaoType.MCT430A, PaoType.MCT430S4, PaoType.MCT430SL, PaoType.MCT430A3,
            PaoType.SENTINEL, PaoType.FOCUS, PaoType.ALPHA_A3, PaoType.LCR3102,
            PaoType.MCT420FL, PaoType.MCT420FD, PaoType.MCT420CL, PaoType.MCT420CD,
            PaoType.MCT440_2131B, PaoType.MCT440_2132B, PaoType.MCT440_2133B,
            PaoType.IPC410FL, PaoType.IPC420FD, PaoType.IPC430S4E, PaoType.IPC430SL,
		},

		{   //5 - TapTerminalPanel
			PaoType.TAPTERMINAL, PaoType.WCTP_TERMINAL, PaoType.SNPP_TERMINAL
		},
		{   //6 - MCTIEDPort
			PaoType.MCT360, PaoType.MCT370
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
			PAOGroups.DCT_501, PAOGroups.RTU_DNP, PAOGroups.RTU_DART,
			PAOGroups.ION_7700, PAOGroups.ION_7330, PAOGroups.ION_8300, PAOGroups.MCT310IDL,
			PAOGroups.MCT410_KWH_ONLY, PAOGroups.TRANSDATA_MARKV,  PAOGroups.KV, PAOGroups.KVII,
			PAOGroups.RTM, PAOGroups.SENTINEL, PAOGroups.ALPHA_A3
		},*/
		{   //7 - CapBank
		    PaoType.CAPBANK
		},
		{   //8 - CapBankSettingsPanel
		    PaoType.CAPBANK
		},
		{	//9 - MCTBroadcastListEditorPanel
			PaoType.MCTBROADCAST
		},
		{	//10 - PAOExclusionEditorPanel
			PaoType.CCU710A, PaoType.CCU711, PaoType.CCU721, PaoType.TCU5000, PaoType.TCU5500, 
			PaoType.LCU415, PaoType.LCU_T3026, PaoType.LCULG, PaoType.LCU_ER, 
			PaoType.TAPTERMINAL, PaoType.TNPP_TERMINAL, PaoType.WCTP_TERMINAL,
			PaoType.SERIES_5_LMI, PaoType.RTC, PaoType.SNPP_TERMINAL, PaoType.RDS_TERMINAL
			
		},
		{	//11 - ExclusionTimingEditorPanel
			PaoType.CCU710A, PaoType.CCU711, PaoType.CCU721, PaoType.TCU5000, PaoType.TCU5500, 
			PaoType.LCU415, PaoType.LCU_T3026, PaoType.LCULG, PaoType.LCU_ER, 
			PaoType.TAPTERMINAL, PaoType.TNPP_TERMINAL, PaoType.WCTP_TERMINAL,
			PaoType.SERIES_5_LMI, PaoType.RTC, PaoType.SNPP_TERMINAL, PaoType.RDS_TERMINAL
		},
		{	//12 - DeviceVerificationAssignmentPanel
			PaoType.RTM
		},
		{
			//13 - DeviceMCT400SeriesOptionPanel
			PaoType.MCT410IL, PaoType.MCT410CL, PaoType.MCT410FL, PaoType.MCT410GL,
			PaoType.MCT420FL
		},
        {
            //14 - TNPPTermOptionPanel
            PaoType.TNPP_TERMINAL
        },
        {
            //15 - RDSTerminalPanel
            PaoType.RDS_TERMINAL
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
@Override
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
					YukonRoleProperty.TRANS_EXCLUSION);
	
			if( "TRUE".equalsIgnoreCase(showIt) )
			{
				objs[0] = new com.cannontech.dbeditor.editor.device.PAOExclusionEditorPanel();
				objs[1] = "Exclusion List";
			} else {
                objs = null;
            }
			
			break;
			
		case 11:
			String showItToo = 
				ClientSession.getInstance().getRolePropertyValue(
					YukonRoleProperty.TRANS_EXCLUSION);
	
			if( "TRUE".equalsIgnoreCase(showItToo) )
			{
				objs[0] = new com.cannontech.dbeditor.editor.device.ExclusionTimingEditorPanel();
				objs[1] = "Exclusion Timing";
			} else {
                objs = null;
            }
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
@Override
public DataInputPanel[] getInputPanels() {
	return inputPanels;
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
public String[] getTabNames() {
	return inputPanelTabNames;
}

@Override
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
@Override
public void setValue(Object val) {

	//Vector to hold the panels temporarily
	Vector<DataInputPanel> panels = new Vector<>();
	Vector<Object> tabs = new Vector<>();
	
	DataInputPanel tempPanel;

	//We must assume that val is an instance of DeviceBase	
	com.cannontech.database.data.device.DeviceBase device = (com.cannontech.database.data.device.DeviceBase) val;
	PaoType type = device.getPaoType();
	
	for( int i = 0; i < EDITOR_TYPES.length; i++ )
 	{
	 	for( int j = 0; j < EDITOR_TYPES[i].length; j++ )
	 	{
		 	if( type == EDITOR_TYPES[i][j] )
			{
				Object[] panelTabs = createNewPanel(i);

				//do not add null panels
				if( panelTabs == null ) {
                    continue;
                }

				tempPanel = (DataInputPanel)panelTabs[0];
				panels.addElement( tempPanel );
				tabs.addElement( panelTabs[1] );
				break;				
			}
	 	}

 	}

	inputPanels = new DataInputPanel[panels.size()];
	panels.copyInto( inputPanels );

	inputPanelTabNames = new String[tabs.size()];
	tabs.copyInto( inputPanelTabNames );
	
	super.setValue(val);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
@Override
public String toString() {
	return "Device Editor";
}

}