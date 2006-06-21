/*
 * Created on Dec 19, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.dbeditor.editor.config;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.database.data.config.ConfigTwoWay;


public class ConfigEditorPanel extends com.cannontech.common.editor.PropertyPanel implements com.cannontech.common.editor.IMultiPanelEditor
{
	private DataInputPanel[] inputPanels;
	private String[] inputPanelTabNames;
	
	
public ConfigEditorPanel() {
	super();
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/2003 1:17:24 PM)
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
		//For 200 series MCTs
		case 0: 
			objs[0] = new com.cannontech.dbeditor.wizard.config.Series200ConfigPanel();
			objs[1] = "General";
			break;
		
		//For 300 series MCTs	
		case 1: 
			objs[0] = new com.cannontech.dbeditor.wizard.config.Series300ConfigPanel();
			objs[1] = "General";
			break;
		
		//For 400 series MCTs	
		case 2: 
			objs[0] = new com.cannontech.dbeditor.wizard.config.Series400ConfigPanel();
			objs[1] = "General";
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
		setName("ConfigEditorPanel");
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
	ConfigTwoWay conMan = (ConfigTwoWay)val;
	
	//Vector to hold the panels temporarily
	java.util.Vector panels = new java.util.Vector();
	java.util.Vector tabs = new java.util.Vector();	
	Object[] panelTabs;
	DataInputPanel tempPanel;
	final int PANEL_COUNT = 1;

	if(conMan.getConfigType().compareTo(ConfigTwoWay.SERIES_200_TYPE) == 0)
		panelTabs = createNewPanel(0);
	else if(conMan.getConfigType().compareTo(ConfigTwoWay.SERIES_400_TYPE) == 0)
		panelTabs = createNewPanel(2);
	else
		panelTabs = createNewPanel(1);

	tempPanel = (DataInputPanel)panelTabs[0];
	panels.addElement( tempPanel );
	tabs.addElement( panelTabs[1] );
	/*
	for( int i = 0; i < PANEL_COUNT; i++ )
	{
		Object[] panelTabs = createNewPanel(i);

		tempPanel = (DataInputPanel)panelTabs[0];
		panels.addElement( tempPanel );
		tabs.addElement( panelTabs[1] );
	}
	*/
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
	return "Config Editor";
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G74F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D8FDECD357954FB4C15146D0D01946D827E5AD20F40B46BED2A92B585ADACC2A56EA25928C360146BEB4A144BA7EC095DD236946410EA1A18990C152B1B59498E4E4291704B473520DC03AB6A5ABA949B2D69061453E361F7D3EF86FB9CEC2A8BB675DFB6D174C8E9F92160E6E3D67FE1CFB3EFE67BE1306570BFC955E86C2AAFC047F5E2AA0E467E1C2421E44BE41AB209EBFF17D0E826DA73BBF5C8D
	EDA14CBDB26D7157137F7687E03C9C46E7E18CC7100364DAFF61A1686411930739DF753FC06716736D5DC2CE15B35E16280136962898F8586E24A85F1BEC647C67B9DFD00597F8D1DC730764914E6B0671A0D091D0E9B43BFE0FA3F9DE281B0F2FAEAEA2BB3FFF520B4628F01DA928A14676F67336192C2A5892C3D97FCC5670BD860C0B01D079CA72D822975A1E41E46C6FE1DDED8BCB1A26EBB68D475BE8C436F533AD2EABF2C41A3442F19AC9A9B494FA0FB106B8639DE7995EAD0EF296A5834E39049C0771D6
	07376FBA4AAE02FE9950DAA026575BA7FB783DAF5FDCC54AB7E815F5F974EA1519BE43F2D62FA5295FD6B5B9BEF8450736AE0439D220E701368355A4BC7ED6205FBB3EA73EBFC15BBBE61F57D5D557FA8D631C92B17F25C728827DD088E6B86F928D6BE60490665F01E4EB0E7B4CC5EC5FC7773C0F592438C3783A6452CA3269DB973CD5B9EC3229B8686DCBB0DBDCCDE4ED42E4BF1CD776F4E2E6AC4E147D9F62163DFD3EDE694BA13BF4FD0B37101F335CA5FBDD82FD7D894757EB20DF41630071BF6D709B13D9
	BE0B4B038E9F4727E0EEE1D2444665DAF117015D4BC87DDF86F25AE12927573BA3496E90D032F7E1F1618DE0FEF582FF90A88ED4GD484349AE82DA2E2235F7766DD464620E45A67D2329221A6E1FB3DFE0494BAE11BD23FE659129626A312A9CBE38A3588DFD36DE045580CFD75FE0C59B048F817EA5414ECD95760F4C925B6B5AD3626493F9F571854199D16D58A82D40308F30F7AA12E09A2D9763F0D08E4536C8AB65F6258F2DC9A019361G68B33B3C3987ED3E83748F289EFF23AA6C305CB96F82B5B52A04
	C2833A1ECC99432889092B682B2FB8395D8A7DBEA0F656693D48AB01711A4CB9BF719FF01D53A707717A12B9A9F263216B08219BD5616B17AE8BDF776DDBC1DA573F1F5357C8C4E3BEEE54E6477CE267CC3C979BB7DD3FFC0F79F73734B1A3237D0149284C786A7355682B33206FD520E9CD5878D0D5B30B59F312C5C514DEB4A140E8A4EBF13636A60038CA4729E677D103EA9168D8BCC68E8676E463B72AE7E22B49619B906F67A1402C8C7F75G4624E45B26BC1632699035EDD90BE567CFBA322C541811CD95
	A12BAD00F82227A15EB36BBB82871CBC50B46ADCBA33054F1FF1E215AAD405CB1F57D31A4D78FF5B037ECE52C91EAB7F5863F7E2DDC9E15E315A76F5299AF5B0E595CC0129CBF6E998474FE4EAB3529260E10DFE9E5AAD1A084974B1911347273ECC1E9EBD614D0505CBD774F94F6A3CB6695918E4F78A6E4DFD77AEA7E7B85F2AF4F85B2E3F86ED4E5ABAA8C19E09F858DC03E75AA0EB81D011AE62611BFB1BDCB94783613C2EA84C2C99FBFF293A9E7DC3EF24A8C095A677050C8DD7D69F18B5B7426D49663FD6
	0D76D4DC38406C7C711596B2579BA2949AB4F5031A76A4D7053F95CEABE88734F719AE5E8A9F0DBA588463006171B79B429FAF6E93EF05A549A232653157723E013A8DDE970D2CBF1ABFA95E8AA7EBD166A3B0D7E2885B355438F12F573274306CC0AE577B97B5EEFFB15E561A3D50C6E553327BA121A6040DDE2A417BA2A6F3B6473C20A2EA72537CDE6B0CECAD5E8A7DF2E3E6EDE6F573870EBD6AEF303AD91161EFF078C7DCFCD6CF2F0F0AF75EA918DBF8C360F4248EE3FE870CEBCC0F7FB8D08F50D520E9D3
	58E1FCDF8739C39D6C873F1A1A248C4DC8F02EFBEBEDA54BC9BE8E567AC4CEC24BEA483B0E4F17024CD2A0467BD994F94F42F8D306775DB87208857549923C1F4A486B017130A5B0F85DA84958F8332B9F9DFDD058DC1B11F17B014AE0B6D46B72E337DD179B3B276AF2E3F7C15DFE4607B35CAF99B13F20EEEEECAF2C4B056D8C3F2EA9180F06C9235484649912275067C5967AAD6D78BC8EFD9B082D5F9575BBF9B22927EC48D59B5729B1014B3F04F565C0C16077596C3D4130669BE94420A8BC1460EAC3868D
	136E946EDD9F97FB17437AB2200DC03B009AD3C2664BB26ED52589D9CD294E8D597D56A470FE8530EEA1D0D98A3F7B8A70D947F57BE13499DA0D261B93523854A6256D36BEF8EAE97828BA55998C36B6F71C8936B01CAACA3B3F553A773E1533EEA01527D83E570F33F675C347F88EFFB6A6F278BD18AB98973A475EB8C1DCB8AB0F29ED29F135CD9A339D208D05BEC8A94AGB7A947584D31GBF2BECDCE0ECF55C41D89887520031E9013193FF9498DB32F29999A8381093E3378C37FB3B539CE353D90CAD066F
	B1A67355381039946657261157A2B7384EC03BFE873E035B20E573D7E64CE39CE2CD494EFFE646BC5E77E825FB7E299973BE57FE265BF3A704EE654F959365093F662D9F0D93CC279E5E8E0E9CA347BF75EA950B0B2FB6E663626D513B0F0B972E386322FE59D1EFC59E79B617DBB0B17BFB660BCDE857D2602F1D90187D3DC327219D11929C2D59134101EBE00A3A4B38CEB567587505587DFD57BEFE1F7BFEFE4F7BD81D3B4DE2F5E2F67DDB60704BA7E6573F7DF111BB01C90FDF1994756F476D683835B05E8E
	34FB4A63BF825483F4F9CA583276F0105C217E8D6B46G00316202CF269F116F6FFB7F19AA911725CFBECA4A663FD2D11DA7AE14A9968F8BF93BF5781843E77A291D285FEA185B88D48E54F05363BF85F4762650EF64F0AB538F4321573625F05CF9518865C6E41A4E3291DC595905E770266C0C5FB41E4897EFF0427B108797479270669C16E219BD0FB661BDB3F50505A11FFB3C8973D656E4DB1694F94AF9F68D41C2AD466CBAAFCEB2B1344855BF9CFF7075BD78C0E57037E5FB7E7A3E2DBDF7FD5F511E3B3E
	3F5CBEF77D7EDDFB7EEFB3DF077B5B0C63CD977AE6986247GDA82D48C34BA93CF8FF73C4162A9C7AC3D930951733A730F08AD640FF7665775D6671D7FC7195F09EF6EB15D36F5954F977C051D68AFDBB79CA663AD6EC49B19F2ACEE33251C7F39CE44D005C69DB6632D684AFF2F122EBB5F6B09AE677BG4E9B1EB47819CFF6617BC60FC6AD1A517F292E7A8C2F5F9A54AD99231D7DB73D6A628750167C1A78AC139C787FFA523B68C79BCAC67B9A3C445BCF7CF10A0A7593DFDA0E5871898390639AD021F503FC
	0CB942C682E3DAEE3BEB4F2EF90DF95FA4DB2679FF585333EB0F7F926A983C65718F5C927E3F70611F674002A1D5D294DBD2123DDA84F1615C24CD3331D8FD894F37449ABE49668EDC42FBA7DCFB7E87D0CB8788481B7C950689GG9C98GGD0CB818294G94G88G88G74F854AC481B7C950689GG9C98GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG4089GGGG
**end of data**/
}
}
