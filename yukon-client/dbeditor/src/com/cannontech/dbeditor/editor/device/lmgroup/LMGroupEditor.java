package com.cannontech.dbeditor.editor.device.lmgroup;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.ClientRights;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.roles.application.DBEditorRole;

/**
 * This type was created in VisualAge.
 */
public class LMGroupEditor extends com.cannontech.common.editor.PropertyPanel implements com.cannontech.common.editor.IMultiPanelEditor
{
	private DataInputPanel[] inputPanels;
	private String[] inputPanelTabNames;
	
	//hex value representing some privileges of the user on this machine
	public static final long SPECIAL_RIPPLE = Long.parseLong( 
		ClientSession.getInstance().getRolePropertyValue(
		DBEditorRole.OPTIONAL_PRODUCT_DEV, "0"), 16 );
		
	private static final int[][] EDITOR_TYPES =
	{
		//com.cannontech.dbeditor.editor.device.lmgroup.LMGroupBasePanel
		{ PAOGroups.LM_GROUP_EMETCON, PAOGroups.LM_GROUP_RIPPLE, 
		  PAOGroups.LM_GROUP_VERSACOM, PAOGroups.MACRO_GROUP, PAOGroups.LM_GROUP_MCT,
		  PAOGroups.LM_GROUP_POINT, PAOGroups.LM_GROUP_EXPRESSCOMM, 
		  PAOGroups.LM_GROUP_SA305, PAOGroups.LM_GROUP_SA205,
		  PAOGroups.LM_GROUP_SADIGITAL, PAOGroups.LM_GROUP_GOLAY,PAOGroups.LM_GROUP_INTEGRATION,
		  PaoType.LM_GROUP_DIGI_SEP.getDeviceTypeId()},

		//com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupEmetconPanel
		{ PAOGroups.LM_GROUP_EMETCON },
		
		//com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupVersacomEditorPanel
		{ PAOGroups.LM_GROUP_VERSACOM },

		//com.cannontech.dbeditor.wizard.device.lmgroup.RippleMessageShedPanel
		{ PAOGroups.LM_GROUP_RIPPLE },

		//LMGroupPointEditorPanel
		{ PAOGroups.LM_GROUP_POINT },

		//com.cannontech.dbeditor.wizard.device.lmgroup.GroupMacroLoadGroupsPanel
		{ PAOGroups.MACRO_GROUP },

		//com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupExpressComEditorPanel
		{ PAOGroups.LM_GROUP_EXPRESSCOMM, PAOGroups.LM_GROUP_INTEGRATION },

		//com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupMCTEditorPanel
		{ PAOGroups.LM_GROUP_MCT },
		
		//com.cannontech.dbeditor.wizard.device.lmgroup.SA305EditorPanel
		{ PAOGroups.LM_GROUP_SA305 },
		
		//com.cannontech.dbeditor.wizard.device.lmgroup.SA205EditorPanel
		{ PAOGroups.LM_GROUP_SA205 },
		
		//com.cannontech.dbeditor.wizard.device.lmgroup.SADigitalEditorPanel
		{ PAOGroups.LM_GROUP_SADIGITAL },
		
		//com.cannontech.dbeditor.wizard.device.lmgroup.GolayEditorPanel
		{ PAOGroups.LM_GROUP_GOLAY },
		
		//com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupXMLEditorPanel
		{ PAOGroups.LM_GROUP_INTEGRATION },
		
		{ PaoType.LM_GROUP_DIGI_SEP.getDeviceTypeId() }
	};

	
	private javax.swing.JTabbedPane ivjStateEditorTabbedPane = null;
public LMGroupEditor() {
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
			objs[0] = new com.cannontech.dbeditor.editor.device.lmgroup.LMGroupBasePanel(false);
			objs[1] = "General";
			break;

		case 1:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupEmetconPanel();
			objs[1] = "Addressing";
			break;

		case 2:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupVersacomEditorPanel();
			objs[1] = "Addressing";
			break;

		case 3:
			if((SPECIAL_RIPPLE & ClientRights.SHOW_SPECIAL_RIPPLE) != 0)
				objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.SpecialRippleMessagePanel();
			else
				objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.RippleMessageShedPanel();
			objs[1] = "Message";
			break;

		case 4:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupPointEditorPanel();
			objs[1] = "Point Group";
			break;
			
		case 5:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.GroupMacroLoadGroupsPanel();
			objs[1] = "Macro Group";
			break;

		case 6:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupExpressComEditorPanel();
			objs[1] = "Addressing";
			break;

		case 7:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupMCTEditorPanel();
			objs[1] = "Addressing";
			break;
			
		case 8:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.SA305EditorPanel();
			objs[1] = "Settings";
			break;
			
		case 9:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.SA205EditorPanel();
			objs[1] = "Settings";
			break;
			
		case 10:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.SADigitalEditorPanel();
			objs[1] = "Settings";
			break;
			
		case 11:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.GolayEditorPanel();
			objs[1] = "Settings";
			break;
			
		case 12:
            objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupXMLEditorPanel();
            objs[1] = "Integration Settings";
            break;
            
		case 13:
            objs[0] = new com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupDigiSepPanel();
            objs[1] = "Addressing";
            break;
	}
		
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
 * Return the RouteEditorTabbedPane property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getStateEditorTabbedPane() {
	if (ivjStateEditorTabbedPane == null) {
		try {
			ivjStateEditorTabbedPane = new javax.swing.JTabbedPane();
			ivjStateEditorTabbedPane.setName("StateEditorTabbedPane");
			ivjStateEditorTabbedPane.setPreferredSize(new java.awt.Dimension(400, 350));
			ivjStateEditorTabbedPane.setFont(new java.awt.Font("dialog", 0, 14));
			ivjStateEditorTabbedPane.setBounds(0, 0, 400, 350);
			ivjStateEditorTabbedPane.setMaximumSize(new java.awt.Dimension(400, 350));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateEditorTabbedPane;
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
		setName("RouteEditorPanel");
		setPreferredSize(new java.awt.Dimension(400, 350));
		setLayout(null);
		setSize(400, 350);
		setMaximumSize(new java.awt.Dimension(400, 350));
		add(getStateEditorTabbedPane(), getStateEditorTabbedPane().getName());
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
	java.util.Vector panels = new java.util.Vector( EDITOR_TYPES.length );
	java.util.Vector tabs = new java.util.Vector( EDITOR_TYPES.length );
	
	DataInputPanel tempPanel;
	int type = com.cannontech.database.data.pao.PAOGroups.getDeviceType( 
				((com.cannontech.database.data.device.lm.LMGroup)val).getPAOType() );

 	for( int i = 0; i < EDITOR_TYPES.length; i++ )
 	{
	 	for( int j = 0; j < EDITOR_TYPES[i].length; j++ )
	 	{
		 	if( type == EDITOR_TYPES[i][j] )
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
	return "LMGroup Editor";
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G88F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13599EDEC9447158A12C68D0D9C61C894251573D927F2922A504AED9315DAB4A20DD1CA9384E9E86ACA28628729109AD5A9311471999F36316170C1824118434689CE4C59C022B806B6B5F69C7C91E2E1572589EDD87B666CBD6F6DAE3BFBDF46013EB73333F73E5C1910A6165E6D4EFBB373BE673DB7EB327BE0DEC519EBB7A1E595447EFB270C904FC1C266ED735EA4F073E8111B247C9D8258C51A7B37
	42B387E805730B5C9E721777AAE1FCB70CA7679539DF01F7AFF9713D42D761A58B1F9C202D101EEF1E1B4FC315024FB6B67EC9DF8DBCF7G64838E1F4FD3643F3AEF2F0DAF3671824ADC04AC8F621CCDFD75B62E8346C3G39G7941647CAA4679064E8EFBFC6156BC727C2FDEF75562BEA9FB8A281C60EBDB6D270FBCD8765484725A53DFE32F894078F6GD43E1C2C3E02FB1F9A1A1A787B3896760FCB2A2A29969D1F745300ECE906BFC02372B875AB6189C30B681D1D039FFE00AF6FB36A50A46E7D1F7ECA36
	97A1FBE13C1E61BEB9017CF2613DGE085G5765EF2F1F32E5BDFF79C1D23AD6AD5F19C5971F4CF5B8A3A7F5490DEC59360F597D4FD5E85F85C04B87F898208400040A5CBE007D4C5FDBAA5F00E75718552D05431A5A256B271500712196208A3CF7F68245461D23631A91A0047BF4FFFFEB86F966823E2E653A57F11B641F907EADB8F7AFD97F5000EBDB861B2C4FEFF7F50438AD06C2C91BF05EE3D9F96B215971B71B773FC8AA6FCDB7EA659599F8AFDB5362DAE06F33B805774A907A7A89666BC2F8AF336300
	631FE6781A29A41E47224470B82E875A3CA9919B67F788D98637AEA11EE303996D3038284B35E10A4BD0BA15140547C569F6BCD3C714A2F78F4085G8BA097E0A9408AC544C6F7D55B970C0DA14930CEC7E4A5C08D425716D63D8CCF9A378C29CFB5ADC99D272312A1CBE38AB509BD27229AE59F1B352E3A9AE3F69CF8FCC0D5EAC8162C29303B942696B5CCBE47DB3D8B679814D1C764B08586E11D08FD77D7FB6129C82675CFBDA0D9B4B903531B2A5116D1E994F6048D601D5B252596ED3E8174AF8528898BBB
	FC0F6DB7C08D15AAF038B5EDAA220F20A664C298FD15E0E75B876F9DGFC2F669D084B03F121334F1A6A3A14FDFA64F194DFB29262EC2CB901F933B8ACFC7D27734257BD35771056B57D99FD0D30B14CFDCC5474187FBD5B93656263099367B32CAF8EE70E7F6B854EE3DEC69E65DF417EBB9CDFAD5A093EBA827A76G84D4E1E34FF69F0F595312C9C514FEE4C00051C05262FC2E3B8673AA0DD2556A21BAD58370E25AB1D2DBD3150C5F206644161761F50877EE88B053416F2B411814AC4B1047A2969D2616A5
	2B93C97AA1464B0C0C694923A2F89D2941FC2245A05E1D79C7EB6A58B9D0D54A04F6165874B6962BD421E1903ADB0B2896473FDD0B7E1E22897B2C3CDB6BE6312EC4705C71FA36D45A0EBA98F298CC01C71714289847B97D229EA34CG8E6B72B2F82ED4C5CC461AC4CC9E1E3E03AC7F345915A997AE3E2747F5C463B1F5CACB46A417690D9D19E5EFE7E746461BDC560E9A8FBC4FE9E0D33B1CCAF0FAF88C94FBF01F8B303F8EF013A6E220E0473E14F3E6BB3FDBD394EECA47465F5909FB1B74D204C2FA4283BD
	6058ADEFE7DD9AED5436A127676FC49BAAA939004BFB651396B2D72F5059B9E4E8BAB52C84D38466727EE08B4BA1E863BCCD7487F744D03FF5B05E8CD0298B9F2C2E957DC16ED49EA9396B552C3D0ECF67366F5113BE704D977D414BF548F38150F2F5E13BA6CFEA2E6BB2CDEDDCE6E956567BB71ED49FF15CDACFB5BC0332E1DAFDF0086242C63F73203C1807ED341D9FDEF26AF03EAD5743FAB27F7C9A5EB7683373912F15FB62E80F9774749AFA1061ABAF2557506F47C48D5D8B3419CBA2B78FFBB14ECBE0DC
	8A305BA8F237828C82A80630835DF502DC23767501DF8DD5D206E79DEAD1036A4A79B93447876BAA44B94418E5F56365813465CD4073EEG0EBB9CC5DCA10C0B9DDCCD9CF11BE1DCE98A5C518462DAE15CE30A5C3EBF0EE77F3CB3EEE66399E7FCFFACD3DE7EFFGF94D4E7D0FFE65BCD26B86376585EF763CBD6E4D1C37E5EF663C2DF9F7F1BC6C115A4588FA4CBBF7DE3F6A4D14574B996D68G1ECB5D20C1EAC0869A162751776BCC74DFAF73FD9E3C97G7079A723EEF6DE92106D60ECDBB8EFE9D46464FA98
	3F85B089B0E371DE03671C9F46B0978521C9825106F5BACE74882EED0A0B356BE17EE65C8F706DGC3914133A701EB43D2DC8EC742CCC2AEDF5DB40BC3183742195B1158834FE1CB923998925F988DE0A2437344EA48E8BC3506EC0EF0B96B23409760CC946F0B53F84334ED34AF6A0327CAE33E1094157CD24C7277C03B26E2E3F6642D7676D65F09375BDBF85E4B0B255E71BA2A5F49D8FB9096C6F9DC66594F073F59E46704F5D1F147DB87B4CFCC58F02291AF35CE5E1647427EC8B46C1746AC1638BBBB4FC6
	94E550F60D1D33FD31CAFB2FB3B111334F321CB38F460B811646C44E0E3FA6F2F6413DCB48603C010CB93BE46438EBE50C4B3CA9164C5979F02763BC7B62026711F8117BE29CF131786E347372885C1F7D7064747BE651B11E31C6A5694F4C22233C074AD3693509D4FAC54AFA2E5B634DC237524773097223F73356237CB8572958FE8E0DB611435FFAF19B0F0BA5754938D8907BA2F141FD77E6658161BB1C9C771BB1B8881D1D7D3362135B6F16382833DB9342E7794751E727E0BC8220A4044F1E78A4B55EBC
	CB8E394A326835A0417549C9245F35969EC0FEABGDF12903565672F3E894FD1A9E4D795FE18D8D98124688FB816F59DF6EEFDB67A656E5C1B3E643A6658752E637538081D81B24D6BF11903FF1C61EBD2707CCC0E4444195C8B3419E9D1279FE9E0F59A4625G3BAFC31D869884183CACECD9393F1DDC23CE0FE87AA0948BA5A53DF37D4E463F5C3708DFEC93713E6C27F712429B7FDA36BDCBDC4CDC6671F03B7D5CB8524470DC3FA19F6AD78C340DGB5B3C56E43G9DGC3B3C23F217D2DDCBF8C07AE4B1246
	A7D967A5149B15E9AC0946644FF5DB646896003D62B3337B2905B04E1B4954E355336F8E4BE652FD574C704BE752FD7740F1613BE22035FFA6FCE76227C03BC6BEDB5F1CAD5EC103FEB8329F0D0520B79F11A61CB5379EC0191C3A4B0F01CDDBFCG73112C4A16ACA972B4EBCF07E122BA41F5DF99A3CE8C2FCEF97F78AB6FCDBEFFE64C2F1587577F51066C7D4F2A064C7D4F138D197B1F279B666EEF7E50107D5E3A31A1755E4A7DFEF3A37AA69762E0A940D3G9B81CABF93719CBD584863B9C3AC3F9F18205D
	9A7BDAE4897E7F76E55775226F5A5F18E49F5ECD46B44B5242383F53777950DF1626B3A447D9BE3411A1CFCCDAFC2A0D1F76E18ED7E81021F97F7D0338685D77DE91BD792842FAF2981FF2F0CAC8746EC38E6E5BD322F7372E085E3C71C4B2CE5FCCF9DFF67C6B0F59D5DFAB8FEE43C50D597D3824715AFE3C2B115DBB417EA3891D5AF770C66C1735E05024CE3C5C5F68F1F0FD6610E64A189DF8CF7BA4779958BC770A68E9438A5E1102F0C767F366B39FAE87FAB1G6F236707C4CFDB8AB84FD548F3GC3GBA
	GDF77E366539C985FFED574398D07510F1C4F0062A3576EE7783AF0CBF25D45D0E37A3ADE9076C8E3GF21FB651586A0325475EEA917D4DFDA1519F2E3CAA7A433521397B432A29747E9057A769A727666E8F43D3697D21D84F7549F9A329CF7331AFE2879E178F042E37BE737FE1F94F74G330267C805E9103A4F73693A656935851F766CF691D79FF1CFD2BC70FD24A2A6872C49388E527F97A0473CC42E205C99C786291D650FDF5E71769F0B1F9DB54B093B0F6C43F09A26862BCEB450A3D962D3F3AF2952
	B59908006FA3DBCD7628B02D04C287496E60B97C5CE4DAB450A57231E911BD034829ADE249CA5B99491C3470DC1C9CA4DBD57CA8CE6AB8D9115409B6DE84CF228E242C6BA24A4B4CC2C0DEAF1397F771BB3BF8FF363379D07F0642F2626AA5550AA685FA25F1A84DFD244A1AB428B929A901387E5EBF58C6156C36031DCEB9EC375FD19F15750F4069F5041E0AFFF7E16D465ADE883238C15A58CC8BA6781D99016EC949GD654683370A33FE69CF6E85FE830DEDB7541AB97DC244CD692CD6F0E6B96F187610A2B
	A46005C7BFF3CA3585529A12EA86B5A38C127952A493A40701FF51E36D375D3765E346A03954A28739A9E1E95B845542EDDD41202CD2E78330ABECFF40599E677853665445EF5D533F6625B108039ED29EEEEFE73F47586FF17CBD46B047986658F1B06EE6B267DFBABDAEF4E64183726CE472207B7D3D60C16B374FBDF667B327B6EF826DFAC9857B6084CE70A4E71D0D044728F1E341ED873667165C0CBE52B1D462386F0E786CB8677DEE4B5554FB508BB76C4DFA6FAE1D4FFBCA4F0274FBD0BD7B5EB1887849
	852297D07A0F126CFD61F0D8D294CBD2263A54G7608279396CD791EBE007B1BE20ECD343F278F205C2114B57F83D0CB87885DC78A5D048CGGB89FGGD0CB818294G94G88G88G88F954AC5DC78A5D048CGGB89FGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG3E8CGGGG
**end of data**/
}
}
