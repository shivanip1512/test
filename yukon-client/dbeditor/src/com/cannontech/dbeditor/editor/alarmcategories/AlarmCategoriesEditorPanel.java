package com.cannontech.dbeditor.editor.alarmcategories;

import com.cannontech.database.db.*;
import com.cannontech.common.gui.util.DataInputPanel;

/**
 * This type was created in VisualAge.
 */
public class AlarmCategoriesEditorPanel extends com.cannontech.common.editor.PropertyPanel implements com.cannontech.common.editor.IMultiPanelEditor {
	private DataInputPanel[] inputPanels;
	private String[] inputPanelTabNames;
	private javax.swing.JTabbedPane ivjStateEditorTabbedPane = null;
public AlarmCategoriesEditorPanel() {
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
			objs[0] = new AlarmCategoriesGeneralEditor();
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

	com.cannontech.database.db.notification.AlarmCategory stateGroup = (com.cannontech.database.db.notification.AlarmCategory) val;

 	//for(int i=0;i<editorPanels.length;i++)
 	//{
 	//}
	Object[] panelTabs = createNewPanel(0);
	tempPanel = (DataInputPanel)panelTabs[0];
	panels.addElement( tempPanel );
	tabs.addElement( panelTabs[1] );
	
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
	return "Alarm Category Editor";
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G97F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13599EDF0D45595C64436B08AEDEC990A8EEA34E85391ABD5C629C3B33645CA2D2DD0E89B9D5AE1BACC2BCEFEB09D30F63C5910A5C0C8A2208222066FD0639294A64BA6DAC2B611C0AAA9C953CC2515175D3B3BEF73BE16775EE6F7C3001EF36F3BEFB76BEE90AB4C1C5CF74F39771E4FFB4E398B5972564C0AB25796C24AAA087D2F290C103A378819745422FA019BC70B5DA4635FCE0017C94F36B5B0CE
	815A026B0A5D35647476CA189742BCB92E587D9AFCEFA65BEB96EC070FBCFCE6826D56276AF70F4DE7E7C070D94B66437B2BE02C861885B89CD7D2647F557DDBED7CC29BAF204CC54813A1DC731D7DBBEC1C97665DG05GF3C369756B98676BFDDEFBFEF66ACC3272A77BDD9B711C0CB385EC8D733DAD76D8C76EADDB9EC6DE3FE4F2629E8B66C5G28FCB9295E57016B3B87439F84F4559310B4CD57AC9A08F8E8D036F443A3A9122186A40B06F5C326264F570A08B687514E16F5CB9AD588B997C1DEEF56D732
	3389290779338C672DC17E0570BD8FE091G576D7D7DAD366C7D976EA5F316E865B5F9F4EB1439CE7DF2DA3702788BEB37B1BFC8C134F781506682AC84A885280D963B9B81DEE77E0F847E8AE36B0055262B2A2E3546E247152071F7BDC89578864D86AC9BF70686F4A3C88877F1497E069C720C85FC5F9F2EFA9F37492C77043F0B4E5CC5CA6EBF65DA1B43A6A5339ADDA7225C966722E91BF05E3B6B73719E979D9D0F23F97F0BE472AE1C2817D76460BDE77181D701FD4E2C8C5ECB23686BA2666B8570DDE647
	8147BF4070BB8653F89E1B9FB0BC4E9B00B6E5D044C6FF351025EB4DF4D2FB28AB279D2695373AD68EF2992A1434ACBCAE22A13CE3DE407782A8G93G8A816683ACD2C4EC5C97B87899E323DBB22C63F1D989D203703D51602BB05224E5C81D1AE9C9DA0076C906AC8DA854A4769AB308328F0C5A17E4B19BG9E9FD20D9A12A56B9A1CAE2954220649570C8F3E0CEB8C4A283D32CA01019AA3625CAF87B74328C826754FD890AEE5FA8527CF8D22AD0724BEB8918E00EFEE17EF045166ABC17FAA35583DC395F6
	2822F85EA9EA40FD76793AF4FDB09E6BC5CD0822222FFC6CEEB7427789GFE562D91444D067982671CB63AA9639C3F9CC071A5A3A56E46F7EBB00F162842574F778BDF7BB75EC99A961F4C69EB04D5AA77F12D169D73735819A8971F7BEB7AF36CAFD1F3477F558267711F1CBC4A3F00732B9DDFD5C551D7DE5037974052040DAF51BA9E3347A5130AA8BDEDC00051E05A62FC2D95423CCA0728E679E90CEAC178B06D983990DA170E5F106E4456F8060FC13C37C100198E7E06904624E4D906BC9037E88F35ACD9
	8B2769B7B1DEE6FCA0163EAA025757C318CF7484443B337E1B21CD6C9EE89AE5C2BBDBEC7AADACD629C2D5903ACD0FEB96475F91C6FF8F5214FDD73E95F633D8D762F86FF8FD1BADEDC09D8CD985D36055A5259A4671BF6AC5FDC6188CB82C530F4238D493B11958ADE2F24F70EDE47EA7FBDD39F26134BB7DAE2F4EE32ACBCF4724ADE3A4376CF733BBE363CDAE2B372A9646B3BA58B4232A7298E89F44F394B8FF9C40B4DD4440DEF9DB46BD331D5F26AB8AB725E363CDD1BC5B246763945293DE68D30E5D36C5
	B7E5517AEC9BF27A4E688646BB1D8B383C17BEBEC0466A9DFC3EEEC30FD143CA596D015D9FBC4CF2885AF836AE7A036B1BD03F95B0A731E277561870414FB70A7E20E0F0A6D9F647763C3DCFE30C5B3EB71676C15DF522BF38C9C11E85C0AB0C895B4D08E6663AD6535483B2CB33365E2786B3FD44F15D036BE18C4906E9F542A5CA8A9B758D223C1807ED341D9F66BBF5F82EAD5742D8BA7FAC036F1531517908574A554C9E156733EBE8399771DB4FE75750C9CD22065683ED72F9111B972A986725B02FB20A5D
	FBGDAG4E820C98428E4F8C3EC72ED07BBA412F06A6A9BD23AE352DFB01D24E6F21BD1F26D408FB08B6E1F5E3897379B460B9870063E69CC55C821817B838D4B3620889B1E08A5C74964435403C5794397D25A63C7B91E73E4E0B73494EFCEAD32E3C7C7FG729A1D7B6F7942F9E4568DEE4B45EA7E3CDD22664E5B3FD3F3676D956A4B9C8FE7E4F6B102DE2A0E1D57D7293972FAB923BDDF0F77B2E65090B5A083754843687B95A67AEF6D81569F4077BCG3E7E7E23EEF6DFD2106D60DDE0613AFA1F48498DB0
	7783A481A6DB3C5760B9673E84662290B4C9A0DACF0C864838B86E1D52AC76BE8B6B89606AG0E811C0B8B1E773560DED5CA4AEADCE592F279AE3F4F629056ADF256FAD32F4058E3C9A297C3629B20C1CCE4F81FD88D71F9B3EB889962F2B640F88C20FF885F0FABACE27B95FA3123F5B0EAB4D1971506A40F1430BCFEE857B4EC4C6A8FB7B6B6543DF72471G4FFBB39319EFBE6F7A261C3587E146900F4B5976B86F065DF6CE687609B75F8A2055A5048D433B76120C3CAD8F281E78106A1186AC1638FD3E0F62
	0A52E53B464E5953FC1576D97D891133FF444E1F8273D9G7393A2E7A777091CDDF457F452B56ED44E1C3D2C77DD575284177945C4BAE74F02B79D6779C0336069CD963BE3C944ADEF5E12F5DF3E8F6FE98F469E233F5A12C947F8469A15261F98C5C7F9F716E75207C751ABB276F35D9E5FABF4FB7171D9C4795E513C75E8EE126BD4E20F5DFD3B491E2F7CF9AD0F0B4AC4BAAEA2CD1FA5AE386F5E2EFCC3788E97A7BDE682AE024FF7F2D4FCF27B99DED1E737241CBADB0DBE6B02F990E0A4A5FC76330FB3632D
	F67ACED7D99E3D8AD2DC1F19296C37568B896437887025A9D1DB5E093F8DE31F94352B8A3FCC2C2CGD274879C4B3A8EBB377679BE5F1BBB78B9775DF2557BF8BD76B523BDEB07F9BDAEF370ED8C3FA3834F6F64C267CE5683ED72302853EF27D81D06F955852853GADGE781129704AD07620D648AF53AD70FF5C131D0B252BB576F61664F77DB448F560AF81F73506DE46E4455E59B724445648BBC9E0A6CF1C56FEE06677ABDB20C7A15GED95400E11E277E10093G67C604FE0F449B38FE988E2D16A585A2
	2C73924A7549B411C6E372673AA53CC2B78B4E7A524868FEEA864CE70F646C3158DDF8F4A45BF7AE76FB4413A3593E1BD4ADFCD78234E397056FFEFB81AD45F568CA6C4D976F2041C93832278722501B77CAE1E74FCB891449293B7C9A58B4F79273112C4916ACA972B0EBCFFBE02196663A9FEBA6CE8CF7E6FC7F4277C577A61F3EB3FF3C26BC387E07D3797B1F66D46E7E27B5153B7F79B0B5F6FFF3BA157F5D5A174AFC37F23FBFBF023EA904B8188F309CE095C055A5914F4BD33BF8BC67086576E01836696C
	57A2CB707F71F0FEDD1F983E72EFCCCB0671EDB220DB162E6279827F74B07A4B52E38CE9770843E8A3C38EC7AC3ED47408431843959AE2E85EFF5F68953DFB7DA551132FBE4CFAF218F7B938877D22F7BF676016360B5EFD42A527F72FCE47E9E5CD7AFBEA75350F59FB2EA98FEE43BF0D647763DFC62E6CC777C5766E847B7726E21463B6DC44FED98F05CC6A44CB6D45DA8757E9F66B260C590177348FF21F014D8BAF091E3676883E11C270C667EB769D41B5CF823D84007751CF78C5CFDBF599FAC600E3G67
	G468170FDE59D38EFA64C0BAE0BBEF77CA574237D96B8DCC72E5C4F70FD8F152777FD5B3FABFBDF8788DB3FAB8839CF8F6FEA28032D078E9F907D4DCA3F688F17DE967D612E76317B435E766C7E90772769935B476E8F6736E77707E2BF576769B5E9FDDC9BBF0B9DF8DC2E76DFEDFD667F27557A1A9F1895BDC7AACC03EC7AB41F569BFF352468937F9697F1F592F70462056FA49589B9E8C512B1107E3FGD34C730479AF674CA09BC7796307379D797D42E77A4CF2626EA45BB01CFA28412A938D7AA5CB7C54
	5CC15645F49908006FA4EBCCB6D418D6CA21DDE4CB688C7E5CE4DAB458AA7231E911D73A10534138A5AB87CFC8E64442FB515CC556E87823B85944490A24058F72A2580CBA103256F3A8AFB38B81F9B7B3F971940FF34A6607EB766EBC39FCEEB9F1F510750AAE85BB24G1466CE324E0A98540C68CAB0997B5BB1F6D0A56BE43153A90763DEF754C7E5BD3DF0FB9D21871337CC5E38E2E38784D952A08759CA8B96F81C95413608E4GABEAF4DA78A33FEE6CF1E8635F34767D7443574E3AC8192DA41A5E1D0CD9
	449D02A72E12028F9E7D4CA96BAD105610B4B3249BAAC8D617A519A0B98CBCDFFB2C71663BDF78B7E3105EEA113738A9E16B41B05555032D21102CD167G30AB9C7F06F3BC2E71E42D59141C7A4A4945AF8DC09C78C9395A58487E9EE2FF5F453F07986690439CFA970CDBCA467C17CDCF8A1DD97000BCB5CC9EF43F2783BCE87D7A3947EE7FCDCB693320DD8729E0BFB80193EA532BBE0A2B835418D8F4739B9B8A16CDC29F45B0D412F8EEF5F2F41C73FE7740654CF7502A6B3766FDF7D7CD305FBF93325FC1A7
	597FBD1F85FCF202688526EFFB07646F8BFBD4C9D1ACC999EC550258A39ECFD9B423AF590E671BE20DCD347B126DA8F7B4E34F7FGD0CB87885DC2F24D0B8CGGC89FGGD0CB818294G94G88G88G97F954AC5DC2F24D0B8CGGC89FGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG458CGGGG
**end of data**/
}
}
