package com.cannontech.dbeditor.editor.device;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.login.ClientSession;
import com.cannontech.database.data.pao.PAOGroups;
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
			PAOGroups.CCU710A, PAOGroups.CCU711, PAOGroups.TCU5000, PAOGroups.TCU5500, 
			PAOGroups.LCU415, PAOGroups.LCU_T3026, PAOGroups.LCULG, PAOGroups.LCU_ER, PAOGroups.ALPHA_A1, 
			PAOGroups.ALPHA_PPLUS, PAOGroups.FULCRUM, PAOGroups.VECTRON, PAOGroups.QUANTUM,
			PAOGroups.DAVISWEATHER, PAOGroups.LANDISGYRS4, PAOGroups.SIXNET, PAOGroups.MCT310, 
			PAOGroups.MCT318, PAOGroups.MCT310ID, PAOGroups.MCT310IL, PAOGroups.MCT318L, 
			PAOGroups.MCT360, PAOGroups.MCT370, PAOGroups.MCT240, PAOGroups.LMT_2, 
			PAOGroups.MCT248, PAOGroups.MCT250, PAOGroups.MCT210, PAOGroups.MCT213,
			PAOGroups.REPEATER, PAOGroups.REPEATER_800, PAOGroups.RTUILEX, PAOGroups.RTUWELCO, 
			PAOGroups.DR_87, PAOGroups.TAPTERMINAL, PAOGroups.WCTP_TERMINAL, PAOGroups.SNPP_TERMINAL,
			PAOGroups.VIRTUAL_SYSTEM, PAOGroups.DCT_501, PAOGroups.RTU_DNP, PAOGroups.RTU_DART,
			PAOGroups.ION_7700, PAOGroups.ION_7330, PAOGroups.ION_8300, PAOGroups.RTU_MODBUS,
			PAOGroups.MCTBROADCAST, PAOGroups.MCT310CT, PAOGroups.MCT310IM, PAOGroups.MCT310IDL,
			PAOGroups.TRANSDATA_MARKV, PAOGroups.SERIES_5_LMI, 
			PAOGroups.RTC, PAOGroups.KV, PAOGroups.KVII, PAOGroups.RTM, PAOGroups.MCT410IL, PAOGroups.MCT410CL,
			PAOGroups.MCT470, PAOGroups.MCT430A, PAOGroups.MCT430S, PAOGroups.SENTINEL, PAOGroups.ALPHA_A3
		},
		{ 	//1 - DeviceMeterGroupEditorPanel
			PAOGroups.ALPHA_A1, PAOGroups.ALPHA_PPLUS, PAOGroups.FULCRUM, PAOGroups.VECTRON, PAOGroups.QUANTUM, 
			PAOGroups.SIXNET, PAOGroups.LANDISGYRS4, PAOGroups.MCT310, PAOGroups.MCT318, PAOGroups.MCT360,
			PAOGroups.MCT370, PAOGroups.MCT240, PAOGroups.LMT_2, PAOGroups.MCT248, PAOGroups.MCT250, PAOGroups.MCT210,
			PAOGroups.DR_87, PAOGroups.MCT310ID, PAOGroups.MCT310IL, PAOGroups.MCT318L, PAOGroups.MCT213,
			PAOGroups.DCT_501, PAOGroups.MCT310CT, PAOGroups.MCT310IM, PAOGroups.ION_7330,
			PAOGroups.ION_7700, PAOGroups.ION_8300, PAOGroups.MCT310IDL, 
			PAOGroups.TRANSDATA_MARKV, PAOGroups.KV, PAOGroups.KVII, PAOGroups.MCT410IL, PAOGroups.MCT410CL,
			PAOGroups.MCT470, PAOGroups.MCT430A, PAOGroups.MCT430S, PAOGroups.SENTINEL, PAOGroups.ALPHA_A3
		},
      	{   //2 - CapBankController
         PAOGroups.CAPBANKCONTROLLER, PAOGroups.CBC_FP_2800, PAOGroups.DNP_CBC_6510, PAOGroups.CBC_EXPRESSCOM,
         PAOGroups.CBC_7010, PAOGroups.CBC_7020
     	 },
	  	{	//3 - Series5SettingsEditorPanel
		  PAOGroups.SERIES_5_LMI
	  	},
		{	//4 - DeviceScanRateEditorPanel
			PAOGroups.CCU710A, PAOGroups.CCU711, PAOGroups.TCU5000, PAOGroups.TCU5500, 
			PAOGroups.LCU415, PAOGroups.LCU_T3026, PAOGroups.LCULG, PAOGroups.LCU_ER,
			PAOGroups.ALPHA_A1, PAOGroups.ALPHA_PPLUS, PAOGroups.FULCRUM, PAOGroups.VECTRON, 
			PAOGroups.QUANTUM, PAOGroups.DAVISWEATHER, PAOGroups.LANDISGYRS4,
			PAOGroups.MCT310, PAOGroups.MCT318, PAOGroups.MCT360, PAOGroups.MCT370, 
			PAOGroups.MCT240, PAOGroups.LMT_2, PAOGroups.MCT248, PAOGroups.MCT250, 
			PAOGroups.MCT210, PAOGroups.MCT213, PAOGroups.REPEATER, PAOGroups.REPEATER_800, 
			PAOGroups.RTUILEX, PAOGroups.RTUWELCO, PAOGroups.DR_87, PAOGroups.SIXNET, 
			PAOGroups.MCT310ID, PAOGroups.MCT310IL, PAOGroups.MCT318L, PAOGroups.DCT_501,
         	PAOGroups.DNP_CBC_6510, PAOGroups.RTU_DNP, PAOGroups.MCT310CT, PAOGroups.MCT310IM,
         	PAOGroups.ION_7700, PAOGroups.ION_7330, PAOGroups.ION_8300, PAOGroups.RTU_DART,
         	PAOGroups.MCT310IDL, PAOGroups.TRANSDATA_MARKV, PAOGroups.SERIES_5_LMI, PAOGroups.RTU_MODBUS,
         	PAOGroups.RTC,  PAOGroups.KV, PAOGroups.KVII, PAOGroups.RTM, PAOGroups.MCT410IL, PAOGroups.MCT410CL,
			PAOGroups.MCT470, PAOGroups.MCT430A, PAOGroups.MCT430S, PAOGroups.SENTINEL, PAOGroups.ALPHA_A3
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
			PAOGroups.CCU710A, PAOGroups.CCU711, PAOGroups.TCU5000, PAOGroups.TCU5500, 
			PAOGroups.LCU415, PAOGroups.LCU_T3026, PAOGroups.LCULG, PAOGroups.LCU_ER, 
			PAOGroups.REPEATER, PAOGroups.REPEATER_800, PAOGroups.TAPTERMINAL, PAOGroups.WCTP_TERMINAL,
			PAOGroups.SERIES_5_LMI, PAOGroups.RTC, PAOGroups.SNPP_TERMINAL
			
		},
		{	//11 - ExclusionTimingEditorPanel
			PAOGroups.CCU710A, PAOGroups.CCU711, PAOGroups.TCU5000, PAOGroups.TCU5500, 
			PAOGroups.LCU415, PAOGroups.LCU_T3026, PAOGroups.LCULG, PAOGroups.LCU_ER, 
			PAOGroups.REPEATER, PAOGroups.REPEATER_800, PAOGroups.TAPTERMINAL, PAOGroups.WCTP_TERMINAL,
			PAOGroups.SERIES_5_LMI, PAOGroups.RTC, PAOGroups.SNPP_TERMINAL
		},
		{	//12 - DeviceVerificationAssignmentPanel
			PAOGroups.RTM
		},
		{
			//13 - DeviceMCT400SeriesOptionPanel
			PAOGroups.MCT410IL, PAOGroups.MCT410CL
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


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GCCF161ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D9FFF0D4459D4799F518A94368B4BA54319D2A3426537C0195AD2D60E07DD5B59D2883D3DB291369D09BE750B2AD9DD36B584DA50183928C90C9880989D1A810F0F789E90BC7DA13A346840E90F2A184CCB06FF21B64483BFB673B3D6422D67B7D6E3EFD77F25C85E4B4B35FFBEF3F5F5D7D6E7747FE763BAF242CE6E1D13E2D0C107CA2E27C7DBB1F103EEAC2CA5BF747A5EFC12747CEACFF7B00F610
	A3278A61198932B26A311712CF5B3053B2E89F74FB6C65703E0B4CE99B5E83AFE9749C83D9C77E1BF74D2EE730DE6A594C5B4B9D45705C8E3482F878AC6EC27DEB9C9586FF1D411714EFA3E4B56F3341D1E570FA20BD8E1489342CAB51FF8B57FC137B18519E3CB503ECFCEA372D8467314CA9296A24985BEABCEB49F772F3CF222EBD4EE2E3CC9C5A0B015078827262E74D70EC6919987B6F0896F20CA8613096E6F4E45CC183C126690EG1D8C0ED0373B1DBF5F674C9EA5CC5540A91CD7F15A79BC04B4C0FB83
	673D96C3DD19703E8268D9A0E147FF7E51E22CF360533B49229F398A5FC8E3C733D72CFF501B30A3FB2421F08F7779BD8D685B85A0DB86348AE8A3D059E90F3D99E8AF0F755D7507615966E7A734D0C88B37C5A29EB5201F5682D405F7379BA4862F170EE8FA0090914F9AE7C30A754CC6E25C11AFBDCE78A473280CEDD66F9DE46DFDEE5B6694BED91BD9E3731E963E881CCE78C4689ECD2B7B4653B3F3EF266E73442ABB634773EDC5A9F4AFD9D3E1DBE04C13E5513D76B446BAB209314E0677FCA38F847F934E
	2F6DCE70C59E5AB89F5BAE10E5F44B5C9858AE5762AD1CCFCA9B0F277443021F39ECF95DE28D25E792EB91F971A05FCFAD409FG0AG4D854A84FA946859B3B2B7DE29F7DEE3EE74A8BA734402EA006AC40CDD5C309B1EB44EF425AB9CE5CAF80476ABFAD0712BB4CA0CBEBFE940357BE70CFB08676C8868780006292E30209606591590E5D40F0ABECFB46C40BEBA65523EE0080202D00448F9D7B66C02272AC45939C8C0E1B451C348FF55003E1CD47AE1C618G5E05DFF2795E4F837BCBFBBC765A9E690721A3
	B85FA92A43EEF63B3D1AB6910B7421A5A45203317A9F5F5B4D706E8592F32DF3A1EF8934334DF9B20E6C344C539E9C4165AB7A345C9B13130819B9BDB256AF8F48D83717FC0BB42CF9B7E52C1148D991630A3349B9BF074F096B92ED7B6440175CFFD7A3DC6F4CBD3E6FAB57D1E0F9977E6DED42D835023D03C01F1D15BEDEF924D6642CC709D21925E7F4C8B09AC8F8DC74FD4C0138CAA7E9183553888D8760A5EA64C836E3CBA2FFC7B5B3371E617C88647B89C8302849FF5601B929B026877DB1C6FD1431E0F8
	ACA14F613A22B1FFA431D5242EBFB890CF34A948F7337FFA47CE3E8F42E14A97ED8EB164AF73DC25AA8D4122CFE831B0937C0DCE0C77841DB676CA1EF3934FF5B5067BCE1CE50B15EDE8039E8C01ABF06B123C33184799CEF996A3E5G8F4F64ECF8661C15B9B975364C49F7BE390D2C7828ACA596AE785ED1DBEB2F480B3E5EC4CE0AB53D68CA3D76BF72BDE37023E22D8F6FC23FDEF1281AF97043F21C0B010E39C08BFBE59ECCB46E316CB5A381CEE82AAA5CE97AF920299463C2BF0ED100A85C5427CC5FFD54
	34B3C956EF78D148471A500F2A858F441ABF7F7090192DD6F03BFBF4ADC2F5B6ADED91B5426B9CC7F00E253D32C6B8FC946D5BGED3B8FB049A763704B92D9A3E4CCE4101C3B37242DF50E790C9A41E729F51E59DCA07C77178A54F9A74832FC52F73FEB3262DDDBB42A0D84B9549AF65FDBEE0D1360BDD03E951E23C1BD4A3AE0A345250F161563FA910B8D360191852E6433F805AF01A32BFDD71E47623C7C5BFB680FDCDF72B9DA544A315B17FC0E3A0E4AF3545E67312BFD921F1BAAB157D7C1FBBDD061B90F
	3D8A2885E8701C74C3FB45D1F215732F8B622A079555B7E3E39B363FDAD1A0762251FE3D22C86EC55CFF7C6C2864B99F871D7300846F15C664AD06764F4FC95C4EEF45FD3D5EECEF636D52FE591E7B5E35E0AE0E3F9E7C95BA322EC9C741F54C3F5D5C0F47AB53E33227B2B5A6F7D62646646E4A9D028FF3D8AB94A96F2F1C9D33AFD526426C82AEEBF4ED62F9BD2D459854FB7D9837CE17444FE6E8FB0166GE5740B3AC0E0433DD3089923D05000AADF040E103967F16C76A6B9F6A37437836FA0D0A750B81050
	393489EBAAAB96826A78E9GD10498985E52EA4570D567056E57D0470047AE76C444A56FB88701B7BCA057B6D6371FD8F0AB688FB9E213A10762E79C385C6E6E182AFA8D138D4CDA4A739767DAB0A0B16B85BE7FAA5487C4AEC84C0A9F101815757D7944BB471D9233F27A7659AAAE08BCED3910402C9C3857881D4DCDD2E79C640BAFA04F5FD41614034B617E6840FD446599B36498F74468047C7199F2DC6F3E822BBCEF063C48B2DE587674FEE95BEBCF5FC9547B8F264563BC43262A0B621977C09DD9F458DE
	A0E2D0EF2F16B1382CCCAAF1C7F48A92456DBEE98D3561FB56AA4F0B3E0B5277DD7CCEB98F5A0B0016DD1478B0F9855EDE5B1E3F71BA47AD7F5263840EAFED44752F3EA8F0BC5F64BF4C79B9963E486575E6AE2F87D94F07925FEB2BF09FAC0176AA20DCA0B28867A7D04B204477BFD73A48D5703DCF0BF8810CD48BFC887B5EED3A3EFB6C431BE51EAC79696DE4694D39795B5264C94F20480F08713C657EBA4E97765DD70D76AD8459B2209C20BC2062A10F7D1DA1E95F713D8D42BE840BB6461411F1FEE2CB63
	7A03F4AA41C6B09236CD37DAEDBBB6B4734C708E25BB1B17735C9B9ECA0E5D631C2F8EA5476ECC4C7C56823235C3B2D79FEAA4E69EBDE1F97F136B2B3ED7DC19375B9A3FCE9DA2AE072A521FF12E2A54E75C3F2AD21FF1ADD5331FE16DD5696F9D272A2C778E11D7FFFD8BF12E759254C1C0F300B200B2AF491C7AF5D51D4829947974FEE00C1E5078ED1F1975DFF5FADB9F293E7AB7026CEA2CAB7D9AE3DA8867B76FE15598AF26C5B8533857D7230F746058B893DD8D7EEF2B91A7D5BA4A5922463A05E36723E0
	576ACB326EAAE6326E4A3DA4F37111D8A27F32AD6F67E2DFFFAE063FD69D42B779B56963E32F39FAFCF654703BG7835EFBAC285EFF78D56C65A68E8141AF93037265464F5C5FB34E890AF94222E3907573223F0C76A7C0863880FCDB13CD7G097A68EDA66B1AF940DB883482A887088C832E8F63B7685B71D326D11F9106F99A26D335F80CBA14A96668G85876B82C1C555466A9AEADD8D350D4D07A4AEAF1C32FEBBBE3675C04AFBADD2E0D860DF5CF886F26B0C73380B4973F871302CCBEEE03357A5773164
	3A84F5A6643FE73357A56531643AC40E97F62D7C50EAD7699D25B6094149F6659A76EC9CCE7EDE38329E6DBA28F86C6D0A4401678E574333DF39EC1C1A229E65C8GCCF93F95DCFEEBB6F23A1BD94F16G4C97D7E63F1308B5BC05430CDC5AD73FBF1DEEC02113906FB37E4BF04F6FD3464CB12B6BF1AE93A745128C59F3756843E098D2D2D1031F702B2E8FBA064784AED09E57F930CE743D60DD663CC54043F80A5ACDB9D01C1867FCB04046853FAADE1260F7D244A7E80B7F35B4F7BD8F6333DEA2C5D11D144C
	4855473E7B4F97321F6F0F9610CDDDE48FEE8F9F55798AE920DDE17273E58759925102A084FE97A90C72C7D114CD2B54CB4AC6FB7173C51451C01B34BD4A481BDE54640C31206A6CD422638C7774F1AFA98C6307D632D308D5A5BC66940EB89E474F2B79ED43BC29501584563B0B2F97E7F118336CFA700D7D7BCE2EDBDAC0EC9DE42B2AA9018EE5844253C53630F11DC647B5B5900F3C574AA732CF21973182AA0069761A6623310EBEC09EF3519371EF4FAB49AD69GE745F56264BD99F4F018BD82A746959DD4
	D13D0B6107E3CDFF47145D7096BB700B8F4A87EDA45FB072E372B93145A30CEC9A052B18BA8DAF829922A85944E0353A920E0EEAFA88D6D61B34B2A9B295B83E79246B369F3CFA11ABC88CE524C6389206BA472896F2360D0E8643541CG7C8A53D71B53E39FC7D21F1D71DB5FBC39666FFE4803F6D290F23978EFA37FED425FC64EE9641C46A6F06E86B26BDF32BCAEED6649836BF9032F87436F680088325F3C7464DD4F35ECD88F56F510A27E818302D01A68559D8B7929FEF356ED555BB2F23E01B10AE02A44
	F15E2DF109C5073E30E2D15ECD1B53E26CG95182052E4ACF2FAB1E3B2017FA815D8744529A3336001AF2428AAD3540936F0G3141B34DE822BEEA76627CD15947909A75339757FD59B2667FD0CB87884EE2D79D5A8BGGFC9DGGD0CB818294G94G88G88GCCF161AC4EE2D79D5A8BGGFC9DGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG948CGGGG
**end of data**/
}
}