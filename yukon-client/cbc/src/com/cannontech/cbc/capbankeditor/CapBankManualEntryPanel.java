package com.cannontech.cbc.capbankeditor;

/**
 * Insert the type's description here.
 * Creation date: (12/14/00 4:00:59 PM)
 * @author: 
 */
import com.cannontech.cbc.gui.CapBankTableModel;
import com.cannontech.cbc.data.CBCClientConnection;

public class CapBankManualEntryPanel extends javax.swing.JPanel implements java.awt.event.ActionListener, java.util.Observer 
{
	private CBCClientConnection connectionWrapper = null;
	private javax.swing.JLabel ivjJLabelCapBank = null;
	private javax.swing.JLabel ivjJLabelCapBankName = null;
	private javax.swing.JLabel ivjJLabelState = null;
	private javax.swing.JComboBox ivjJComboBoxState = null;
	private javax.swing.JButton ivjJButtonDismiss = null;
	private javax.swing.JButton ivjJButtonUpdate = null;
	private ObservableCapBankRow observableCapBankRow = null;
	private com.cannontech.cbc.data.CapBankDevice capBankDevice = null;
/**
 * CapBankEntryPanel constructor comment.
 */
public CapBankManualEntryPanel() {
	super();
	initialize();
}
/**
 * CapBankEntryPanel constructor comment.
 */
public CapBankManualEntryPanel( CBCClientConnection conn ) 
{
	super();

	connectionWrapper = conn;
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJButtonUpdate()) 
		connEtoC1(e);
	if (e.getSource() == getJButtonDismiss()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JButtonUpdate.action.actionPerformed(java.awt.event.ActionEvent) --> CapBankEntryPanel.jButtonUpdate_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonUpdate_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonDismiss.action.actionPerformed(java.awt.event.ActionEvent) --> CapBankEntryPanel.jButtonDismiss_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonDismiss_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/00 5:11:18 PM)
 */
private void destroyObservers() 
{
	if( observableCapBankRow != null )
		observableCapBankRow.deleteObserver( this );	
}
/**
 * Insert the method's description here.
 * Creation date: (12/14/00 4:50:21 PM)
 */
/* THIS METHOD IS MEANT TO BE OVERRIDEN. WILL MOSTLY BE USED TO DESTROY 
	THIS PANEL AND ITS JFRAME OR JDIALOG */
protected void disposePanel() 
{
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G3BF24CACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DDB8DD4D55715B6A949105455E0EBBAA6CDD6EDE23A6804E44C44CEDC09B3C3DBB3139FE75534E6A62BCDE71109C9DC09E91D0953329A1B1C779ECFC1C0D1C0910100C14504A0C4C4C49091B4E0B0E1A2D512E864725E851E5C77633D97F900606CFD7E5E65719E96EB4735B6771EEF1F334F597B6CFD4EBE67BEC94EBBF31DB6FB8EA1B6A7617F8E5888E92FA1E45F01879385967FF93A64633F9220CD
	6405CFD3603990F8853725BB33484546D4A8AF01F24A2D694E2D701ECDAEB516D742CB14FED20077662F1646CC5D4F4F06C5BF6BE879216A8CF8EEGDA8A58EA20A3DFE07FCFD667F1BC8F4A9B78BB124DCE48EADAE724AE4F024770FACF4073F92065DFE21D6555851CDF8F65CE2039C08B3EE4F51740B30D0E6E263ADADE6F6C2C39E475D31B6C195817C53E2072332CED87FF9613BB28A19CE41F8DE554F78C749DF6793D95AE4567737BCC5555DF616AF1C1B150237886D43747746BF5F54DCA60A8940F79CC
	FD38C37129DA62D99C6F5C3AD4AA0B10D8A8371FC3AC6F905AA8855E8B002A4F09B1BF13D94F47B4FBE0AEC93AB7A56A18634E3131AEB8979AF3679BDBB25DDF217C4C7D993C5DEA6017834674F9277A703475B929788695AD2495A1CC2FBF6E97FA45FFA5740AB9007DA6C1B99947F1DE609F57A2DE8665A3C0DD67053E8D3B053EDD23FFCB12FB2A6D9B23689BB84F741CF3BE246F93AFED495CC6FD6E2BD1741BE540CB864A68CEF7D682F5G850036D3DF5FB6729EBC1BFA4CE63F576B77B58582C7B537FE52
	6FD6B5F82F2B838E47BED3DDFE5DCD8873677B6A4AA30CC7D0EC77C4DFC6E23A1DA1427F92BE7BBEB14D776C4E883AADF8B65BFE9717B13FBB245B4AEE345BCC6AA7CB615D4665B27C8E0AD7AAA11C4DFB9145315C8E3C790A30F35786B1167614BBC9D6D50DFDDD04314C79717B761C9EB606721E50D8180DEB475046DD001BC0B3016681AD86DA5EA3EC6C9A59F7B59B37013F69BEC5BBB55893D0F4736820C7F32BBA37F5767E29EC9D09D83B12E937E3F6095FE71D23679F5E9F51AE4F47675B090B59A34F95
	328B6B7B0B28FD573AA67B4686B90749B4D65ADDEC8E9D923753D8B9EB41595C5ECA6BE3D9835ED2A0D67F1FE87DEAB72B6F14F21E22F82B85E7F27E1762D8BE8B3CC4A0B63F7FB60E6BEA298AB18A548594G0A83CAGCAD4459CBFB83277CF0C238E4B7C3236FD23DB602986CDDDE973992662F32927955D2374E82AC1F81D40A85A35E7C23BC19A5FAE6863A355276A0A69717BC03A62D541198CD6E7FCF4935651D54A6D74F8D568409BA0C26E4D2359E8BB45B07F98F0AB269A2A41785F98C51F38241C8609
	A0G5E33FA51DE0B687A1D847A2FD1052D9C8431CE60EB3D42AEE917B74373E3F0EED52B2BEB7138F0980ABEACFC741B073260B9836A177625BB17G857BC5FC7E3ACB78E1CB66FD247C596A08FE08D46AE1BED56F8977436FD279EE40E3006681AD7608BD3974D0976FDB4A617DCFA7E6846158A747E0836D43E1411DBC4E2E250FF5E1E5E6774BB6745D65209739106EAC3EA0EC5FF239187964D145D00597FE2A0383296E50CC303ACDE338472817D41F592286D41F9BDE8C6E832DE3E9A17F6C75CB5F790462
	38DEB5038399926F9C5B44F010E175F641BFCB790AE96A1E1EC1D3BD251A26475797EA6F26E3B1A6AC058657F55C0E3A56020E9DC05DD257574732ADFE566E778F8C86BAB102383FDE247B328975E7DE90737F56814C51664BF2EAAD16C37C3883DD96DBE33DEB19332957689FCC6843F19D65077AF83C96F5976BA64316D2ECA96814AC75BDD35BE529F7BD47919ACB20D6440373AF508773052471683E7C4AF8E4DFFEFDBC32AF3FB1BE352F3EB99E49D7994FFE85F5748F79AC3E3F6E8A2E07AE3F4F2752G14
	CD884BC5FF5D09FAF8FC9E730C475D271A27BCA3AA6CD7836843BF27EBEEB13CD78F08B5F268DD31C6160EFC0FA41E5F98B11F1AF3DF05FD66G735F398321B5120DA915E0CC291A6A85C3B57B87FDA643B750751C630603E2339CFF0DED4BCE9C9DB4CD3F0FED9A658A552502BDBAD43D572FFBD5F759856512D2218C1995F4AEAA1AA87B983E6FA97E60034345134E8665972CE24FB70549EB0FAE70501E53FBAA899B633DF9F7D8475862B13C9E43386E0364F227BB4AAA6E7F306FDF3C7B3A5BB27CDC7267DA
	D40C3544765D6BEF53E8033DE64B32BD62A1877AF3A27879B2201583E20F381572459E41B742E63F2631D084DE07C6636CD00EA5F6D2B4F6DE5FA079EFC7644F01A7634F280F44CF10FCDBC3A4FE22A646F9238DF396C33DB828C2AA081B58476AB04F67EE31ED8E631DD63441D01EB84B168ECF4D327FB1BC469EB946EF35631EB7A065FF433E16E03E47E432B589CECC38B66A9E2F0293251B7CDEA2F63F75DEE2990C9F6FB01261196195E76B3FAF4075F3D28C81365CCB737A03625E22944A75C0E73DCC4EDD
	DEE6F3D66F1FE53DCADF3A3355972A576F63E7FE9F56FBD55EAB6C15186D90E278E7A0C51CF1CEE6D96FC15C466E086BA6D2B2171F618FBF7B3D1C02F24B812F770B39CBF5D87310A643703BBCB47566737008433A26B2EC11E3BDBCFBBD3A8167C0379A94F379E3870E95F3F38E7379387F7D86B2792CF10CDED008B42FF90806C5273F990E872D608E4DF24BB96DD10708304358F16B5D4A220D857674A8F668607A87ACF668FEE9CBA63B77282997F61F8FFC06BDA23195922BED90D8F1C078489BFB500F687D
	8B86F830429802BD362E6E04653E683FE97E06753B82627E2468A07AF49C146301920158D96E7D0362AC779CE0D997E5DC76D9632E75A24B858370E4EB5CB78FFE884F69AFE9E2DE5EBA03367CF396DCB6A777E49F61E3DC72601D247E4A9FEC116EFFE2B72E3347DCE4F311F0B1B4A7B39F78B4FDE1DA9E3F737A0D34C5B254295405AD6EEF0CE40BFEDD4C491E120610AE57369ECB9FBB7321B556921336C70D354BBA53A3CE8F0F352756627CA7G1E280BD8EBC859E409354F403B95DF9F2CF8A2FEDECBF130
	350D17FF136294EB9B91E727F529E807E5A0F395D02A8167A720A306686312732AF7B74702D35C5D9C2A1F4E9DCC37A1F271C553EAE74AF68D8D53E997AB5B7DEE52FD4DDDC6683CB35F18FC76E177A95F267E3558E077A919923F0F62ABACB87347D20AE3B943043336A97A2FBA985E7FF2F3E21FE4B052793AF1D23B2230F2E9D8BB665F9BCF617CC7D8F3184793F11F7BC8DFAA6F2BFED05CF31CCB433D4804F29CD0BC50E22095C0A917521DE517046FBC6E0C34CECF741DE80BF4684C4C747C1FEB340FE352
	BDED38FDDAAFCDEC47747E4FC621F777A521F77CFA4466C0B9DE6A38E3AD4E2508418E3F47E79291FFBFDD0B3904DF7774F9FC82DBC6B117DF2707A206EDCC0DFEDE5B129A793CD6109A793C36A3F56A3B0777D2231D6758BD41E7F44FDA887AAD83E2584FA536C2E21D8D82DBA3318DB508E58D25BBCB0784765A8144DA215CBDA4FC7D2E03385F45C0F9AE50C220E5C02B31CDD054391F56A907F2BB1086B4B34872A34CFDD81D778EF6074D65EAAEB346A277CFDD8B306FC8B86B6B58243E52E5AC31BA533953
	4A0C0297D2F9CFEE59C86750B0D40386C80BE2AA1C770A19CBA21C1FBFD13CAA91E7157E03E93C4EB1535FDCA5F5D854979E8B8920F3A257FBD9847D797DECDAF47F7425C576CFB38DF5B47C033ACB8D77415134BC498BC5FC1B29FAD18D1E2F53762622772913FC786B34BD63C5EBBFBBAD1A1FB39B1557D9ED74213451AB0D61B6DA81F6D943ED13B59C4DC68F2C47F5BEA84B8F517230749135E9039C73B11F1BAF94ACBF7750785A8439797DC46671947B8E88B9E99758G0D39A6D040E197FA6C2C4175C4D3
	067D03A0AFEE986B555708F3C9A9146B015CC041E116633071BCB4047E5B2BABDEB04B29006AA25A887B4EA05ABE8775D782E5GDE8E54BEA272446CD61C1B936DCA0F2A71130D342B36A3DD72CE191081F2FC53E614F982F6029E7FD1FF1031B8AF6F5826F0F9937C79619334BFEBDEA9F92B07B704F8BCCF936754978EDAF33C85A3CC7F67C6C48ED85698093FD272DF3DE26513D146AF9B957C3F599529FD7D28DCB7695E5981E537448A7A918BC2B9D6E2E37DE20D1CAD31379AC55B6592DBF9DE34CD165849
	EE51B66532487F5DB562BBEFA7E0B3AF0BB92F6A46FDDB6E7918385655FD24FB5CC7153EF66AC1EC6FCB289177250BAF0B3346AF284CB5D04E9983FF989377EF41DD627C14707DBBC97B0C7D913FCDA7F59658DB46D86C740F05F25B67657929CC5E514E837E73E3081D2E355A97677B9628D081CF460F1D40C79F022308057F6884BE0E77B13B153FF2825FE9E94FF4FBFA2750ED4D5371C47B613BD13F3B170EB31DDA79B36B71FC32F4FFDEA6BF5F75C6B95FD10FE77A675509FCE34638B8AB1E17B94842F1B1
	07FD5A764832C4E4717C250408F366AAA96F05DDC2DE5995914B2FD29F757AD4C850BC2E4095FADFC07D71B520EED9EF75E13CA7B1E121DFF085FF6F71A61A1B644D407A337BDBA25844EA57F5FCACC849509E29105BB283E46472F7567FB3544F7F857B879E6B2FA8BB541F3BFB2A7E56DD253F408C663BB6A2567C8BB55776FDB9F80D6D9E1AF6BBF626C8205FFAAF4FE0E78A1B44FFC871D88B4E5601A4398E4C86DE728C11475AF2509F4F5EGFB82D09CD0BC5093C04BEF90F9EDCDBA5EFDCE193BF77A836D
	1094E816B4136977C16D35FDF77F51BA91EF8B9E3D07AC3C79C5DB343B18649B78F7779B59B37EA77994E77A4D4EC57DDA0167860A851A8B34GE8490DC23F7174F2269FDE9BB51926626A27B7D2C2B93CA48A4138C1B25D8AA71CA712F87F2B79B375EB137383564EBDA12FA86075AA7933A5EA3BDDE4AADBF6C5ED7772AEEBBB0D571B411F33A33463FBF1BFBEF78F6E66F3B2F748FAF7D13B3EA46A5DC5BC173908BFAF7FB61F4B786917D699D9F7265BEDD1E42C66EDC9CCE80FF8070E0B5075CC7CE3EB428E
	1CE65E478B9942F78A6E7F8EE9797797ED6FC669231A4B0E31745139F8DBA653FF45F92A3FEBB76FE39E3A9437695F9D883DBFE6F9FF3926ABC2BF570BD89FEF7FC57BE0B131BEA7FA1E3FA9A7F21E3FB5A772B934B0E76A6F06BBF322FFB7AC4F31FEB7E4B1ED6663DE3D88666CB920B5C0D9B741B973A6914F0F666CA0B41EA34472B1F79F1EB37017A526687F6E5C683A4E4B3D7A6FD19E4845BB6B9EBF241DDE14AF7007F3F13ECCFF00028CDB180BB652BDFD7DA62B4A71FF4CC51F5654DE8A33FC2E1A6681
	C7C02F0E1BC40E772B43A26FF3CB6CE823487B82929BED94ED93A596D3A25AAE1358CBA522ED12449A0840AAEF96589FF689F9B3A466489672E6CB4C1DAD5A4E3DD96CB13336A3368A4A99FFA5666853AD6F437334F2E1720DBD009D3ABF206A66B0C369D79DBE77B3B1ED65B63BFD4B4EE8B2E026CFC0CA74E94F85B890F6AAFD52F79EA944FC219C46518A248145G4D811A8F349868B92015C0A931694E8220CA209620AE20FE204BC0F131C28FBDEF4F94FA70041ABA1C2ED86F03AE67C777392F65DF5D67E6
	6653EFA760AB1D4381FE4E1D15CF6F18FAFB8DD57A76377333A456E6F4788D8F1E0F597A76F19D66F13DFE1F49F2AD057A5BBC50EDFE2C48353E05CBAE4F35BEEF1CAA57F204114835445A1B944B72D67A7D0C76B70F7A68CA4013E5FFBFAA8E75372664EA7DB927682F6C16F4679EDE07496BA4D74BCD1D961955370464A1568A65AF09B5B75D351B7E468B706EDB447817170446BF1AFD357E2CBA040FBF8EE45EE0E3E799BE175B311DDCC7D9D808B5ECBB2E0978DB890F22F9C668E736D3D051574778CF97C6
	77371F95DE5D5FFED998F90DFB21F0729A77F2E164B5EEE521F50DE358FF95227EFD90A7DD325EEFA57639445E24B291BB63F11B7D9237952D67F82B0A9D4A5F1694613806A85645318C8B7639584B0AB0E6C065FE3F3C035DD2C4634223E92C1CCF6BF8FC90C2529E6F946198BD8145B26ED209613819BF5C391B9011CFBDF39877A51FBAD4AC3F959D0760AB5B6106B973776DA8AF2EACAF2E2A5EA35A7EC39911F2BC6F076484CB26A7E78926FFE4F26E342A1171FF7FC1289FEFE5281F5FEDAE095CCF93B807
	865DD471EEF85BB83CDEA46C7F8D1CBB09C8426B44E912356C1E86DE1A5137B4C3A79B2544B6EA5624CB7B6A9CAF5E6B301314B6321639D99BC9C1DFE9A3CE6ACAED4481EE96441FEB62917193A01C262D46C5B2CE6C9C4589C0C75FEA12D4FEFDD4877D52ABA6F8D9470E597BE0841993F4FA7CFB7BDFDE729F278D87D95BC6366112F8CA55E9A429EE3CF165BF79BCCE5282F8DB073FC40261997461B44CE1CDEDA7B93D1F61CF918CD3F5B70998B5CC12CB3F8C6E9DB4BD5A5ED64568B7B12E8E3513949F7EB8
	15ECE6ECCD71756DE581FB086AE8EB6A2613C4D3E098EFB69DAFCA2910D2329F5B3833644472058EE2BFCE56EBFE45FDDCF1417653C6524CFEDDB57A7D1ABB98E8BCC285258E614C636A6BGF15B257A28ECC5A7C43F9C74C0706E1919ABB20F03D103BA59CBEB62E93FC256F0B777ABBAF4256AEDA67E58562F17CA5E8DC5662EFF7DE86BD9BB31F1A5F102568783A60CCAD7FC86FEB0059194070DC03024200AEFBDD9F97B8FFE7F859594EAEA12F7184920695EBE556F5D5B545B6B7129D2G588F4497CA71D8
	27A22C4E66602C5C934F3E5D83735DC29C5E4ACA7A370A7E7D907FD6D1240AA2D59F0291D711A97F05730354ADB7184CC9244763B4D79C0719B2FF717A13777C32FE55CA506EB8F1528F31E06C2CD02DCF863DBD2AFEF3426D056933133E0EF391C01790F25776AADE0FB68CAFADB46A03089785E5B42C67130D6FBBA91ABA492B904D186C9E88E7D166E67AB7F75A4AE3AB96185B30C3FCA918B8521D2111EE657D62821FA1D15C12F0996AC1EE44715B220CBFC51A08AD75EC9CD86B7FC3314A09D360608A6E64
	E753BDD72C675F641B6CD14F505D3731B329F9DB78777F97BBD0557900AF39CD64217BBAF0910F168B1F72AA1AE6AA5AC0134F0DF97151E1D38D1D3FD6F420FCC35461CC7EBB018E9C7785CB1B7F83D0CB878890AB5D4D2D93GGF4B6GGD0CB818294G94G88G88G3BF24CAC90AB5D4D2D93GGF4B6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6793GGGG
**end of data**/
}
/**
 * Insert the method's description here.
 * Creation date: (12/14/00 4:50:21 PM)
 * @return com.cannontech.cbc.CapBankDevice
 */
public com.cannontech.cbc.data.CapBankDevice getCapBankDevice() {
	return capBankDevice;
}
/**
 * Insert the method's description here.
 * Creation date: (1/8/2001 3:45:56 PM)
 * @return com.cannontech.cbc.CBCClientConnection
 */
public com.cannontech.cbc.data.CBCClientConnection getConnectionWrapper() {
	return connectionWrapper;
}
/**
 * Return the JButtonDismiss property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonDismiss() {
	if (ivjJButtonDismiss == null) {
		try {
			ivjJButtonDismiss = new javax.swing.JButton();
			ivjJButtonDismiss.setName("JButtonDismiss");
			ivjJButtonDismiss.setMnemonic('c');
			ivjJButtonDismiss.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonDismiss;
}
/**
 * Return the JButtonUpdate property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonUpdate() {
	if (ivjJButtonUpdate == null) {
		try {
			ivjJButtonUpdate = new javax.swing.JButton();
			ivjJButtonUpdate.setName("JButtonUpdate");
			ivjJButtonUpdate.setMnemonic('u');
			ivjJButtonUpdate.setText("Update");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonUpdate;
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxState() {
	if (ivjJComboBoxState == null) {
		try {
			ivjJComboBoxState = new javax.swing.JComboBox();
			ivjJComboBoxState.setName("JComboBoxState");
			// user code begin {1}

			for( int i = 0; i < CapBankTableModel.getStateNames().length; i++ )
				ivjJComboBoxState.addItem( CapBankTableModel.getStateNames()[i] );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxState;
}
/**
 * Return the JLabelCapBank property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCapBank() {
	if (ivjJLabelCapBank == null) {
		try {
			ivjJLabelCapBank = new javax.swing.JLabel();
			ivjJLabelCapBank.setName("JLabelCapBank");
			ivjJLabelCapBank.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelCapBank.setText("CapBank:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCapBank;
}
/**
 * Return the JLabelCapBankName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCapBankName() {
	if (ivjJLabelCapBankName == null) {
		try {
			ivjJLabelCapBankName = new javax.swing.JLabel();
			ivjJLabelCapBankName.setName("JLabelCapBankName");
			ivjJLabelCapBankName.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJLabelCapBankName.setText("JLabel3");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCapBankName;
}
/**
 * Return the JLabelState property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelState() {
	if (ivjJLabelState == null) {
		try {
			ivjJLabelState = new javax.swing.JLabel();
			ivjJLabelState.setName("JLabelState");
			ivjJLabelState.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelState.setText("State:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelState;
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
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJButtonUpdate().addActionListener(this);
	getJButtonDismiss().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CapBankEntryPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(349, 196);

		java.awt.GridBagConstraints constraintsJLabelCapBank = new java.awt.GridBagConstraints();
		constraintsJLabelCapBank.gridx = 1; constraintsJLabelCapBank.gridy = 1;
		constraintsJLabelCapBank.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelCapBank.ipadx = 2;
		constraintsJLabelCapBank.ipady = 5;
		constraintsJLabelCapBank.insets = new java.awt.Insets(37, 11, 10, 2);
		add(getJLabelCapBank(), constraintsJLabelCapBank);

		java.awt.GridBagConstraints constraintsJLabelState = new java.awt.GridBagConstraints();
		constraintsJLabelState.gridx = 1; constraintsJLabelState.gridy = 2;
		constraintsJLabelState.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelState.ipadx = 20;
		constraintsJLabelState.ipady = 2;
		constraintsJLabelState.insets = new java.awt.Insets(12, 11, 23, 9);
		add(getJLabelState(), constraintsJLabelState);

		java.awt.GridBagConstraints constraintsJComboBoxState = new java.awt.GridBagConstraints();
		constraintsJComboBoxState.gridx = 2; constraintsJComboBoxState.gridy = 2;
		constraintsJComboBoxState.gridwidth = 2;
		constraintsJComboBoxState.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxState.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxState.weightx = 1.0;
		constraintsJComboBoxState.ipadx = 45;
		constraintsJComboBoxState.insets = new java.awt.Insets(11, 3, 22, 98);
		add(getJComboBoxState(), constraintsJComboBoxState);

		java.awt.GridBagConstraints constraintsJLabelCapBankName = new java.awt.GridBagConstraints();
		constraintsJLabelCapBankName.gridx = 2; constraintsJLabelCapBankName.gridy = 1;
		constraintsJLabelCapBankName.gridwidth = 2;
		constraintsJLabelCapBankName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelCapBankName.ipadx = 192;
		constraintsJLabelCapBankName.ipady = 8;
		constraintsJLabelCapBankName.insets = new java.awt.Insets(37, 3, 10, 23);
		add(getJLabelCapBankName(), constraintsJLabelCapBankName);

		java.awt.GridBagConstraints constraintsJButtonUpdate = new java.awt.GridBagConstraints();
		constraintsJButtonUpdate.gridx = 2; constraintsJButtonUpdate.gridy = 3;
		constraintsJButtonUpdate.anchor = java.awt.GridBagConstraints.SOUTHEAST;
		constraintsJButtonUpdate.ipadx = 10;
		constraintsJButtonUpdate.insets = new java.awt.Insets(23, 78, 21, 4);
		add(getJButtonUpdate(), constraintsJButtonUpdate);

		java.awt.GridBagConstraints constraintsJButtonDismiss = new java.awt.GridBagConstraints();
		constraintsJButtonDismiss.gridx = 3; constraintsJButtonDismiss.gridy = 3;
		constraintsJButtonDismiss.anchor = java.awt.GridBagConstraints.SOUTHEAST;
		constraintsJButtonDismiss.ipadx = 4;
		constraintsJButtonDismiss.insets = new java.awt.Insets(23, 5, 21, 15);
		add(getJButtonDismiss(), constraintsJButtonDismiss);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	if( observableCapBankRow != null )
		observableCapBankRow.addObserver(this);
	
	// user code end
}
/**
 * Comment
 */
public void jButtonDismiss_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	destroyObservers();
	disposePanel();
	
	return;
}
/**
 * Comment
 */
public void jButtonUpdate_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	try
	{
		// Send new point Here
		com.cannontech.message.dispatch.message.PointData pt = new com.cannontech.message.dispatch.message.PointData();

		pt.setId( getCapBankDevice().getStatusPointID().intValue() );
		pt.setValue( (double)getJComboBoxState().getSelectedIndex() );
		pt.setQuality( com.cannontech.database.data.point.PointQualities.MANUAL_QUALITY );
		pt.setStr("Manual change occured from " + com.cannontech.common.util.CtiUtilities.getUserName() + " using CBC Client");
		pt.setTime( new java.util.Date() );
		pt.setTimeStamp( new java.util.Date() );
		pt.setType( com.cannontech.database.data.point.PointTypes.STATUS_POINT );
		pt.setUserName( com.cannontech.common.util.CtiUtilities.getUserName() );

		if( getConnectionWrapper() != null )
		{
			getConnectionWrapper().write( pt );
		}
		else
		{
			com.cannontech.common.util.MessageEvent msgEvent = new com.cannontech.common.util.MessageEvent( this, "Unable to send Manual Point Entry, no connection found." );
			msgEvent.setMessageType( com.cannontech.common.util.MessageEvent.INFORMATION_MESSAGE );
			getConnectionWrapper().fireMessageEvent(msgEvent);
		}
			
	}
	finally
	{	
		destroyObservers();
		disposePanel();
	}
	
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		CapBankManualEntryPanel aCapBankManualEntryPanel;
		aCapBankManualEntryPanel = new CapBankManualEntryPanel();
		frame.setContentPane(aCapBankManualEntryPanel);
		frame.setSize(aCapBankManualEntryPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (12/14/00 4:50:21 PM)
 * @param newCapBankDevice com.cannontech.cbc.CapBankDevice
 */
public void setCapBankDevice(com.cannontech.cbc.data.CapBankDevice newCapBankDevice) 
{
	capBankDevice = newCapBankDevice;

	if( getCapBankDevice() != null )
	{
		getJLabelCapBankName().setText( getCapBankDevice().getCcName() );

		try
		{
			getJComboBoxState().setSelectedItem( CapBankTableModel.getStateNames()[newCapBankDevice.getControlStatus().intValue()] );
		}
		catch( ArrayIndexOutOfBoundsException e)
		{
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/8/2001 3:45:56 PM)
 * @param newConnectionWrapper com.cannontech.cbc.CBCClientConnection
 */
public void setConnectionWrapper(com.cannontech.cbc.data.CBCClientConnection newConnectionWrapper) {
	connectionWrapper = newConnectionWrapper;
}
/**
 * Insert the method's description here.
 * Creation date: (7/28/00 2:55:31 PM)
 * @param obsRow com.cannontech.tdc.ObservableRow
 */
public void setObservableCapBankRow(ObservableCapBankRow obsRow) 
{
	observableCapBankRow = obsRow;

	if( observableCapBankRow != null )
		observableCapBankRow.addObserver( this );
}
/**
 * Insert the method's description here.
 * Creation date: (12/10/00 5:06:20 PM)
 */
public void update( java.util.Observable originator, Object newValue ) 
{
	if( newValue instanceof ObservedCapBankChanged )
	{
		ObservedCapBankChanged capBank = (ObservedCapBankChanged)newValue;

		// make sure we are looking at the capbank that was changed		
		if( capBank.getCapBankDevice().getCcId().intValue() == capBankDevice.getCcId().intValue() )
		{
			getJLabelCapBankName().setText( capBank.getCapBankDevice().getCcName() );
			getJComboBoxState().setSelectedItem( capBank.getCapBankDevice().getOperationalState() );
			setCapBankDevice( capBank.getCapBankDevice() );

			this.revalidate();
			this.repaint();
		}
	}		
}
}
