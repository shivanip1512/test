package com.cannontech.dbeditor.editor.capfeeder;

import com.cannontech.database.db.*;
import com.cannontech.common.gui.util.DataInputPanel;
/**
 * This type was created in VisualAge.
 */
public class CCFeederEditor extends com.cannontech.common.editor.PropertyPanel implements com.cannontech.common.editor.IMultiPanelEditor
{
	private DataInputPanel[] inputPanels;
	private String[] inputPanelTabNames;
	
	private javax.swing.JTabbedPane ivjStateEditorTabbedPane = null;
public CCFeederEditor() {
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
			objs[0] = new com.cannontech.dbeditor.wizard.capfeeder.CCFeederNamePanel();
			objs[1] = "General";
			break;

		case 1:
			objs[0] = new com.cannontech.dbeditor.wizard.capfeeder.CCFeederPeakSettingsPanel();
			objs[1] = "Peaks";
			break;

		case 2:
			objs[0] = new com.cannontech.dbeditor.wizard.capfeeder.CCFeederPointSettingsPanel();
			objs[1] = "Points";
			break;

		case 3:
			objs[0] = new CCFeederCapBankListEditorPanel();
			objs[1] = "CapBanks";
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
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
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
	java.util.Vector panels = new java.util.Vector();
	java.util.Vector tabs = new java.util.Vector();	
	DataInputPanel tempPanel;
	final int PANEL_COUNT = 4;
	
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
	return "Feeder Editor";
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G4DF854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135998BF0D4553536EAE3252DCEE3A76350A9DA440011A498A0C0A2B49327D86A08B3D035C687BBEAE9C7DB6C50A9BAF43C59104D0289AC10A08A54FCB1894BA610E0128D55AC0911C02A991226DB314ACB76ED3270F65F7A5EDD32C12ABD675EF7F7B75B5DA0D619B9396F1EF3BF67FF4EDDC8796B330A8BCD650494969363DFDBA1A1252F93F27D252527854EBAA74FCC227ED5816CA44FFC308546CA20
	756467192DA46432403C95669BD76419DF016F5DE441C056EB7011601E99C07BD7568F7FB47DBD9FD709FBCA587CD1F3A90C5B815A81076335F3707E674DFB8D7C0786DEC02109107EBBF1CDE949FE833792562C832884FC6B1D1175DB594D57B95A45D9B74FA29BFF7E06298C4F09BAD3C03231B75B98ABC9FA6139D43C6BD8C92931E783506A8107429791677B2DB09E9F38B07656286A330FCAFE3F6A27726838DDF6FB292A81A66011E5372CB99C5D5D3D6C6B9DC6A9E067A615D858B9044C067978DC440DBA
	712ECA78EE8798G60F27C6D0D63861F2ECF53C957537E229D8964C81B4B79DFB3B7A24735A7B7173C4AF47E63EA542DF59E688A60A40082B083A08DE09F33751C2AC6981DA334DB7579D43FB390B82138353F2BEED901EF0783A8866E0CBC2AEAEEC238BDBBCBECF178198E783E3E2B5E47F5125AA2EC1BF6E68EA938673429A40ECE8AD21BCD797338AE8A66C5F4426F7EEE423BB74F1B6AFBD36F7EA7093E3BB9D9ADAA0EF3F7565A861375AEFECE6DDD113B076EC2DBBF4CEC5D835F05069FF07C138C1F9205
	67FE68E5F81C27822DB4CD7806EB3B6025FF4B2D44FA24BF2E9ED2721C26C09A67A1A9AD428B770B54DA0C27AC402F86D88FC06E061882B88EB0F037700DBFD4B5FDC15F9810B4FAA268D54069895F1BDA3D87C6B9C4B5294F2FD349BFAA8FCB1AD79AD1E41D98EBB22B117711A97B96B11F9D05BB5E157D32A6D12F6A0753A51FCCE5CD67EB16D76F44B51A4C28C3DE1F8C977882C41C7B336A5DB0AA12CE7F91F0CBD40E2C6074872BD1179725E1B8918E00EF2E1735F52873G481F8410925643299ABC6F342C
	79E5456168D7558B414090CAC2565F0D361A4DE2BB8D3E738178D98F9DC41CEDBE645979621CEBEAF6C51D53639DC576A5EDD2444663CE4C192E7942563FF789DB77145DC1ECEBCF4535B5821D4FEDBCE3FE2C4F7F061D09FC71396AF445596F1A9F5F7F2F96789DC9F16FA87A8A4E5F9E36555B75E82BAC60FBB54006300EF3EAAA394F1E10F4D9F8697B9AB8186C0EE81C2FDDD00BF9D53EA87BE90F9C107DEE78508D9FD9D23BB5623F9EB56CDB4B99BEG7E5E8D8E260771775522CFCA14EA5E11A01587E5CA
	3D7E31887D81F6179E9C89C4C2C55C3526967309BA817E9EDE7FE86DAE9687FE3F4C188EEFB1680FB1DF15955987CCF72BC1BF6578A76B505E9764C9A3D61E2EB3B3DFD70298F73C16E5C8AF238C1A57872A4050A5CABA7A713292D10B91AC00431A5C8563D03A7049099A61133517EEA3ABBE2EB3454B05A9F77418325239CF2DCC0F78A4676917876373DE40E246406B1C5776D2AB0CE7D450A9AF278392C48F7701D0831E339E4E5F8CD01EA1FC4075462BD1F1E6983FDBD5942E4A300E5F294733F57913208C
	6989837AF4D8EF7D753BE2E843068EB9FD289EF528C465824E6FE7E79B48F4FD0243B1202981D92313CC9002B58BF5DFE7C159D147368C519F14F8D0BEB74CAF83A4E7889BBCD4A67A03648B33483A1F3C16304FC94B603ADF1D913101F5D87487F90DF827B593F2FF26505D037551394E296B6A281725D9C36E4F9A22ED44F15FEA588623472B69348F02A8A4F4F4C383720BF958C09B792151A26AF0EBA6676BE4E6A47FFC885F014C297908574ACF19BE2E4D0C2D2157B1FCF2E6EC8DAD73089ABA9BE81685A2
	B73FF4887DDC01F99240AC00EC00C7GB6AD90FA585958C22ED07B7A402E1ADFD28627843528C10DC5BC8E0D792A46E2910768332CEEB8194D4B6F49B3B783F0DC110E389E183B4238F594F117E11E9C4695DEC4DCB64CD75FA3F27BB59E0C7D2742739B595C12A5663BBD7172727F83F857545CEF7B4A6F082E9BDC1796DB623C3D4396BFEFD75862676DFD361D9C8FE7C4F7B102DEE31BBE2F9F32454B6BC50C6632E0DC86B459A3EB1001863D1750766EAC345FA74C76B678EE87606BDF534DACDEA6A15BC12C
	AE44F55F57C5CECE05F5ABGB682D89672DE03671C4C894CC59EE81200354100BCCAB6333DFF26E26FB84CAF834C844881A8D8A86E5CFB91777A2410579774B18EB9FF4FB07F6AD9847D60A23136FD328246C1AA09DC8C09EFC4F6E3A2C33ED98D4971C457104B0BB81F29B066G2CD904EF45FD98DA060EB6E915B07A65094A7352C549AECDD0FB8F34EBFEEC4C6ADB1B1BED15AD471A9BF85E1BB5917D3EEB5F56963776A06CDD4C7D52E60CB95F2EB1F242E2DD3C6F5CC01B39D868F02C3A0EC465ED6F084F9E
	3C6833CBA314A5EE07633D2022749B26B1F276CBACD6712CB50BC54EBECC717C526CBCF3AD4071EC1133C387C54ECE1BF3AB697F466938B9FB5D50D153D0B667B9149D4959A9702663F79E880ABB33007EECB662CE874BE362A5975E4EF698B97DBFD368684FD823A27474A9F464372AA81A7E6494FAF154FEAE5B2ABAA15B66D529C4D95811309E35AE61B2390CF1E03806147F7BAFA55CAFBA0FC57C62F77297718BEE3BC3168342F638B8E457A7A0909C0ED3D37C136BEF23C754591B16881BE5F4214DD642FC
	9D4026A542E68F1E0D76B76B2DD5264284F2D917F2F976AF0DFDEB9DFE936F9BG3C32D45416F3AD07E09C164E9BD5058793ABAB00947D8147322E43482D397A17FBF32F7A127B36502B5D476B71EDAC86E6AC657538B00C1F4770A9D1F89E13ADD44464EC20D91609BA7DD19B2B53B0CF821885108D70884046E5C217F3DB1A49956A74109A6807E221C425F7AEDFF5704B7D96715392616FD9CBEFA759573FD078F2823F3064F0FF28B74675C3B58C4F65FB3E9D65F3810D8224GE4G6483946488791EEF31F1
	79509D1C14CA23632C7392428DFB6509889A13BF176D850F10ED831C35A5F7EABF359766365CF8BDD6AD7B5D21ABB756F607993EBFB756F60B3A046DDCC04B499536ABF72026388C1F9F2BCB646FA041A9885977C74EC3EFBEA40D05779CFD93F98A57DD9E86862D63CD4CC7DE3F17FAA545FB093527033050BF46E53F93D334614399D15F9D742B6ECD7EB7E64EFC2DF7F079475A9277BF7E36787D8FED0B5F7FDCEA1B3E3F79E6DB62F76B8DED516FD6EE772ACE34CD653DD07BG4E81D00024FB05BF3F52DE4D
	7DB90EAF3F639E133BD576EB9195773F581ED856420EAB7F46342D835FA6A3AA252A8F4F97F8EB875A0B2A8106B4FA448E5411669D9B27FC296891BBB007AB320721F97FED9D933D7B6CFBC5CF7E58FE561343FCE59827D7085E3DA00CCB5EA3FAF732DC74660D4E081F36C4FD67F4FD7DBE3B76EB3D036B3021B3319D1BBB2FEC470ECE766E847D8FCD86E40EFB2B937BE555635165303FF4F7DA4338BEFDC0553D189DF8CF5B41ED86BA2FDCAEFA5ABF6E47B712875E68FC0D65G2E69873A8B0077515F2990BD
	ED9260E68264G94A09D006F4BE5B65D3FA24FDC3FC27439D9D8A60CFEE6757ECAF265FE066FFB4E9959B7D6D19D3B2F97182D2FF6C36ED3472AED153075C8EB0368EFB2AAC4FFB834C27407CFD4CC5F9FDA7744760738BFC2BF31E77A7E106E096D8F45FEAE0F35BBA24F61373F089E38DFAE2C385A7A4C7F7FEA0D2B87AECBFB0E946B9A59753F77B83F776843E99F77141B08290F1847E58C78BED2BC61F55371D0G387F8860A67D937299729DF7261132A976386F36E34F7C6209E13D08187B482B68CE0332
	462A136C6E112878293917EC8D28DEA0823E0FEC5159D02C53C9C56EA7651EB378F313CEE537D364E31D120AFE3C29A9C83DCA53C9C99F2798971D7DE40B9FFF94A73BB8D9117CE3CD3C88F6228C2450F98E79E5EAA1406FEE46AF1EE28F1F32FB590E3A2AD3CFE59791D3AF592628123BD79A05525CC73652F1CD5647D5459D8A3C5D458E32CC20E7E127D384476D8B0B0F425A07A0FA43CCDF887DE8C6597A32DEF03210C61A58CA8A8B6C61956E6EF1C9032BE42D0F620F7C2AD69B26DD73D7FA7001F7DF7950
	C48A8DA1D17566D0009233871E384AA4FCF06FE7C659C601DBCD726B9ED57381E715B11C89D278827B8F6EEF3EE56E0B9F308BA2DBA9F91D2B9236B60D492A2F496971F87DF278G50AB9CFFA0FCBC2E31472C59953A3962545A17C6408FFAC8112F39197DBD427E9E453FC7986688439CB98A4A5DC0267D97CB8F89191973GBFBB98BFE8FEFBAFD8107E6A397BEFFF6C7806E7C13ADED24CFEF082A3D8A32B5E8B7AC6E46D7A34DB8E3C1C3C6EC634D1GDDA50467EE8FCD75F35E6FB6FC9E7D8E5AF45D5E046F
	6E24FC5ED34E4C0FFD87BDFE963D679140EF4C973D40613307C9623EF050A7A98A15148BCE3F9BFB449313D40E74A53FBE0B676BE20DC164345F1EC53E4FC76D79AFD0CB87887DE5553C188CGGB49FGGD0CB818294G94G88G88G4DF854AC7DE5553C188CGGB49FGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG528CGGGG
**end of data**/
}
}
