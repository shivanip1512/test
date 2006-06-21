package com.cannontech.dbeditor.wizard.customer;
/**
 * This type was created in VisualAge.
 */
import java.util.List;

import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.unchanging.StringRangeDocument;
import com.cannontech.common.gui.util.DataInputPanelListener;
import com.cannontech.common.gui.util.OkCancelDialog;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.customer.CICustomerBase;
import com.cannontech.database.data.customer.Contact;
import com.cannontech.database.data.customer.Customer;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.dbeditor.wizard.contact.QuickContactPanel;
import com.cannontech.yukon.IDatabaseCache;

public class CustomerBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, DataInputPanelListener, CaretListener 
{
	private javax.swing.JButton ivjJButtonNewContact = null;
	private javax.swing.JComboBox ivjJComboBoxPrimaryContact = null;
	private javax.swing.JLabel ivjJLabelPrimaryContact = null;
	private javax.swing.JLabel ivjJLabelTimeZone = null;
	private javax.swing.JComboBox ivjJComboBoxTimeZone = null;
	private javax.swing.JPanel ivjJPanelCustomerInfo = null;
	private CICustomerBasePanel ivjCICustomerPanel = null;

	//manually added GUI components
	private javax.swing.JTextField jTextFieldCustomerNumber = null;
	private javax.swing.JLabel jLabelCustNumber = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CustomerBasePanel() {
	super();
	initialize();
}


/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}

	if (e.getSource() == getJComboBoxTimeZone()) 
		fireInputUpdate();

	// user code end
	if (e.getSource() == getJComboBoxPrimaryContact()) 
		connEtoC7(e);
	if (e.getSource() == getJButtonNewContact()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}

/**
 * connEtoC2:  (JButtonNewContact.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerBasePanel.jButtonNewContact_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonNewContact_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (JComboBoxEnergyCompany.action.actionPerformed(java.awt.event.ActionEvent) --> CICustomerBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G6EE1C7AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BD8DF8D45531D190D20C96EDACD1030D90A5A8E834C12344CA1F71BD3469D7FCE265A9EDD3CBAD2D340DAD76D1CBBFEF32891B709BA0CA10A08B89A1E04C8F024698B1A1BFA490B506883102E433FB132CECF6175D9B327C28EF661CBBF76F5E3D1BBF4CD79F1F13FB4F1C3F19B9F3664C19F36EAA64EC0B4AC8CD4B9104548CC17E77F62AA0ACF588429F328EB5902E68C42BD1D07D5B8E30DEF8EACD
	BABCABA02F6E51D6E336B021AD93524D10EE7CD12BF1933CEF9442FAD34C7092241F3910D7F15958DF8F5C4F4F36D1BFC6162EBF399A1EEB81DAGA7814CB9017D1FBC19AB63A3A03DD6FEC7C8CD930431A7314C789539AAFC7AC9DE2E9D1EF6000B1FE3996F49BCB9BF89F00BGAAG5F7CB9AFEB05E796236E0A4A8A395C09EBE204250F96262D413ED46D93A44AF5974BCF13F0879304C1B8ACB542332A65F477810B333744E2F6B81C8EC9347414D8BBC52BCDF23ACB7AED674DEEEB09D5BCEB3308A516BE0F
	646C955D151535CD35727BC133C7ECB1BBC4FB7BA924FF660ACC56B6748369C58C375308B20B0477B80024D34443E3EB2AE49A43CF478849D3B78765212803535E5C6163C10AB02FDA5E016D5F4C46846B05C1BA91007357EEE898B1FFDA6EE01C98AF3FBFC97CD51B093F68B46C5F8B6909GB18A7E348171F3A03DGA045CCFC3F370B78EE3FF02F30745AC7B2568561BB331373DB566963FB7184732A4DCC97BF5B0E7AE4073C09G51GF3819683E4DADA0DDB589CF84534871EB51DD22D3337576928F1398E
	5A2D6E8F1CD6518E6F151510A363DAC50B53ED95842E67F72FA856210740E47157F1844E5BF10174F2C66B9D42221AC7B3B2F4F80BFB22A8ED1F5CC6234547DB24856576D326AFBD700E75DEC65DE178E406CFD66179783F4E70184E3142FC34121C5B5792AD4D69D3046C72A3E9C69DDAA27E23BA4DAEF29A460ABEDA380C7767230CE3811F843088E0B9C0DED72B312A0BE45CE1FAF3B099B7015E399DE67B513ECE1759AD9D6C33592D22DB167534138349DA8FF83DD9432E4765927326FA0C967E70302EDC96
	46142635F7F1F9383AFCF261FD7FA9E85F933B82F5E32DB20639ECCEC4F771B1B4A8789D8C9F274273313D0D4DADCCA7C2DED1F72B11177F95AB9F564D4B9B15F61E636D2B703C1DB38C0F698448B375349A7978BEDE0076F6AC2423G6281668124G2C36C1B99B0D716D2652A14E23965578723A566D2F40D374CAEEF31343A3199D96711859EDB3F75AC50FA017695D0E34F57A55F33379ED01BE8E098E51ED16ECCE8734EE6E95C119BC3C4C056D6B310CDBE439ED36DE91BA68F589546E186D9B61E9B7FB24
	CFDCD633A47ACA707CAB3723CE1CB59F039621819CEB9B4A6B91E6472B617D040DE4A594A2AE8E52C90ADCD66C58G4FA3205C223D32324EE6C1B24C6EF324230F9B0BF04EC2790EAF408E821CB88DCF009376D663FC00765ED6E33A2355986220F97B27F6524F3AB5B30562A71AF475132D21CE2EEBF9CE2DFEBE4B7A2D833C95E0ACC0B4C0A240A200F4D72B31G208E406A22353D5C9825EA1B68C35A54FDA2AFC8FF08C3DF4F75GF926F71441E065D1A6DADC07DC0F53FA420FD6DCB7573171702F0373E615
	40ED63F0EC045A4E8CD6FE3D8EAEC3FEF23DF16C4239B796649C85B047C53AD333434467942E32B40BBEF24384902DBECD62E58F64A3FF62D951A15509AE51E105970F2C437579D93E795565D4F47F8843233D2D058960D1709F672F67F8E8C3BDDBA97FD316EF16A4372D33CF920F0A12E4F3F47B6A1B99AD9EBFD36E11F9F595A12F8B004794G618C717A077C0D2AF9526CF41E6EF33521851067DB4667B837F220FC71995247578C68FB36A869A21676654726358FB0AED8EFB8EB037E9A734800FD98AE21FD
	DF9F3FD5ECB36A71B6857FBC433B000F8800B98A5F7D86ADCDEABDDDB304FE378DCA534400BE543CE68C210F01668E5709DF94845769C5857ABA7D0782FD1DCEA998D8E717956869AC4FFB29G79F176BBD4F3A0FDA75AF50B53619059C4D42A885C377ED39B72E1F3582463B6EB37A89D351D9785A15B4D7CB436F6AC0671DAFE06ECFAFFBE597402735305B9278A52747C4208DBEA535A5CDCEFFB5CBE1B4EE94AA8C4FD906DE2AF882A56596710B8FE95DB17E43C4740F033F2A52CDBF4388DE458E0936DD6D8
	38DDE647398FE179AC36405C17CA58DF3EAC96FDE1BEEB7616F87AC1E8A5ECC4CAEAB1379EDFF71B5EF86300C9E74BD35CE5F30BCD8ED79F35327B586EB25E7F4FA53B267F061666516C77CD5EEF615633DA3E7B5C1259E62F69C5010DA6857B789C59A8FC364D252161385859689CDD396F67FD77B6F9F95FB05C1D4E03CEEFBDE8F577B9F9700B4DCC19CB7823C5F4F7B95D3D2215D1D1E26E17E492EAD87670E9F80B5370CC5D975F54588FE964C96E73DFBBBB9A3C8F09F32261514DD11C0603FD1264F4FCA8
	7657421E9B7A7C663A7FC25B74299ACD1B4D419BFD9708AD17F5654CEE27E61C50BB7E1753B1C2C59D5AE0713EAD7B7B7D472A45ED6B85EF679B17166EE0F1DFE56AEE342721E0E7A381625C642B0CAF44FC72D5E4072C56E9377325G72F2BDE867CD19B9AA5B1D6466713035CAFE73A0795EC1722387491FB7C87E72C1720B327472BBA00F67CF574D7755FFE2107C54154172391C25DD5938A60BE77AC4589222B3F8C4BCC77B3ADD9BB4F9474C76BE5FFEF14CAE746B6DAABF12634F96692F11970B50F7BC2D
	349FD234C240FD9FEF132F6931E6EC534595919DE3B9EE197739BAEEA981EF98630C0367DCG2E4F3F3B007E2D0E1D94A4895BA8C82738E6B254D906B216F8BBED921789AF77BB255C9CC8AFGD826DB6E218CAAD75157EAEC81F07571F231FD3C5C72BEAC773FCABC32D4412DB5A26E4FA000678C949379A0DB9DCFADC811F47D93046A333C7D36335AD85102BD582E847831E7E9AE3DD824767BEBBC9E274546366A72F8CD5F2D76DDB86E0E5DAB61899357A3B501D36825B10F5B0D3462DEDEC64B63F67B6B6F
	89013109FA966124EDD5EDBF3392ED4EDA27D5EC843529D5DC3BE3B691ED911743178D6A18EC423A7234D5C16490A1739F2F12C3488473AA9EA7BD15C5F2DFF616623157D7E21CCCAF9EEBE10417F47759CA7AA49B86924C1219D9AD2EC33C4DF1EC0F04FEF5D9BF45D80D1DACD68869900088G9E6F79A0FD35AC1FD840AD6E27B9FD13C5BDA7CD5ED6E3A3005DCBFA9C170EFAB2CC73AF0FC3626E8DFC5CB48617E4BBE5E3B55902BB2688D5DFEFC8530B7B062E4BCB8B71F21946F8FD321D3347131673C6279C
	734E4EA07E16C21962F3445FBC031ABF6BB94EDF58B9622FBFE3AFBC073F3AD09CED0952EF943439C069774CF1F53F5967F93F9567295F23ABC65AAF176DA76F216CAEF56917E354FB55F3FDCEF2D950395EFC1E4B3F633CF62EA79423AEDE847C4473B457CB772CD74D75D618DDE6C7B7D8E6E53F374740ED301C7E751E8C32418245CA7E7E864AB19A5A1C8330GA085A07B827551737A203166FA6FG31E6EB56F0E246E597E84FFD6567D82F6E02AF26502EFA37DF880CB570F86C85B6BFC3AE70F8EC06029F
	477061AABC572FB51D24DFB110377AA275FFB4DDCB376B22FF1FE11AB42FE78E28D722C947EA6AF1FD5BD914C736CAB575ABABFD3EA2DF9B1FCDD32F0DF3AF72784D92F88ADF369A395DFA42C236B077CB0A25FED40A6D37C05A8EB096A08AA091A099A07DAB9A6FFBF5ED3B7FF887B36C3E38961745AB99A313E1C1C0BD2D8C8B3E5213E137FD0B3E8C4967156D3AB11D6CFA55D7B45EFF99A12DC2403955EB1A3424CB6B3A231B75E96596C720733F1863C8AB5A11302FE8AC3FDB0638C4C84FD746AD279863D0
	E48BDA1CB68746A3F85E0F0B5177F23AED5DB687617E1361ACCEB78B42F0DCEAC97078D0E609FEFCE8ED09FEFCA827E46018E7DEC93078910FCFDEE06B79A260CF789AFCAD061B42E31610AED6F0D92790D78169E685B73F83F1AEC807FECD63FBD9A762E6C0FA2E028BE33845DF73350363B6F6D29C8E4B51184D78BA505E0C84F89F45CA9F6A385E2546B6893632369EF9E59D133F47A3FA5804C58FC74E7B1D74322093EB43C815C07B6A8969D9F219FA49D93BCF515B3F6E4E5557DB55197CEB86525955A164
	B715ECC2BBDB876993GD288497BA94BC06BC3D8C8205C6518F9E9F0DD6DA85557552ED21CA39EE71F5BA2EA753137B4D74973D934A6C96CC53148FB9DD6DFB23B3B45GFD7E125567F9416A0FAB8D2673DC669FE428E57E3622AF2B591A9225A38B04B8404F55647176F62FD03737D7E9EF66F1354C97C25D9400744B643D64E541E49EDE06EB27D7C9C73074B9C5B7441A350A8C6A9DD6598871BD5035ECBE7F9E885B2D50F75F67686BCBCE273D4D6642F5CEA83E8C6F44B4A26BF29953A934D3F673B9E79F34
	3570B2EC6B6DD334863AA09D8A908B10A8534DE9392B9F5B6EF21BFBC144C7DD22C5083F1C1D57F6D05D82C8D7G3483F881A2AEA75F717AF528DB3E3DA55F9348B2FD87FDEE2E6D144F150C2F4FBF6A545E394175D99853EA4C99C3BA1FEFD04B3F987055GEDE364B87418E03E4D94B607AE0E61BE4CAA859F4B7011AABC77ED76B1BC2693A04FB4166C637396320FE395DCBEDB9367C3FA0902EBCD27F24B945C96369EB5DE8173788A42A5B21C8B52A18A6E3C89F161100ED1F08FB1DC82241394DC0A097AD8
	F085456EFF76195ADF4E3E411406FC695E539947E535EF1C4FDF663C1FBAC5F7F4FAA0AFEA9C6D894BBB7C7CAC745CABAB8F39ED5603666EE626E55C06E530B5846BAF98C7B66C4D8EECB3FBFC2B318820FABC516BAD24BD6C0CDB27884DA107F56F95A5371527350F67F4DE9C2F22D759432EB7D31F71101FB29EF16F1B557B9C5441D9E95C9F6279E77C72513E648B6A7C29FE79C86FBB86F57E93FE79A823B7647A1C371F6CA45E167FA4D6304F2A88FAE72AA21473F4C2FEAE76648BE56D1DB2EFFFE3EBAD36
	9F9E2A48135D35C842B44072D05209CA36FE64FC87647C9DC5E7194F538C698E85675A0B3831108ED7F0BFFC1F74A966BBB45FE2B6A10F4BA01DBB2155D885F08240BB01726FE1799193F87C87E3BB7193F84C0867D7EE521E4DE1DDEC0F52589EBE316D5C89437395647DC6C09F2B84FF1FA0CBD5FEA86701EAD8FDA54AA653DAB7C0198C990CF23F8B000FA5G79B2DE00B67AD27958F3DBD8192EEF3FEFF8C7D07A34258F54A775C550F625FF1FBD10FEDDD07799E640BA2283C310D6E1AC6B236EB86DF724AB
	4906BFFD9C630FDD30EB67E51EE7E566427BFC254CB54C7674BAC4586858AC3CDC8C437D82A0BD0C4ABDE54236A4E0107377CC250FBF637601784B527037C4A5D364A9877A58A81713FD7BF731ED3E3E063D3D931E474CDF68C6588E03797A28738BF0B45A4C5D0A9EBDF4EAE4F73B9E9BE13D159D432D47572CE94C161A42781A152A60E3993ED80567767C1D8E326795109799C6FB21BB2BF03D952EEAB566816C83E886308304DEC5FB2379EF1789036CE95B1C2EE6F0A66D2A2D0A3C07B50F6C4E5B0F0DE4B7
	636E3FCD089F37ACB5D8DCB372AAEEAF9264E708BB1F61B9FF878FA0FFCBA0AF73EA70A5G6AG5A81DCD793FFF995451CBFD4109AC9B2DBFAD8F4171843002B8F0D4E90676DAEBF5FFB6C557EF6AB626AE0FE48B4E637E7DC9DB0F68C9FFFF54058255158A5C2DE51F7E9DEFDECC05F2E5369E60EB4776DFA8C64BF05C139E800E40025G2BA7D23D4C72C01FF0DBB955AB1B48FD42C6F81E184877F63C5E783D7EB6E1B2DBBFA4A89382406773236F8FB41F311D8C41B71FE321DE62C4BE1F194D04775F3C7452
	CBE457E01FF2905716434D66CED0AE3FA3B71EFF288E6F0E87B915135BF861982E6B81A71CCA9F2F3E33D66903CE94A9AF3101792B81A70EF4E6745CA6757A3FC816D766B594E7FD4A22171F87795B84FE7FABD320FFA96F876284F9CDF823DAD066462FD7535C483BFD2AD0776432D43207DAF917DDA34F09EBFC7ED27B431D2B747A667DCC2A919439FC4656G495DA772FE237A55FEE5454A370246E163657E1264E75BAD79B29FF7B5785AF718704E3EC36CB7A9C16086981A229DB5EE1B593E2358D48E7F15
	7976BB8F1A70394B31CBEEEBB3DE0727BDD9076F7D9E557B7B9D835D073AD460FD340FEA9F5CC645D7855F2FBFD8253FDFFF38CABF3674132A016F9EBDDE957C6E51D3D56A3BC75C467E3E91F5BC7DDA58A38154813483582FD5E2606F6D9018FD55312D7556EE0C9760ADDBC93987F8A0B82F2D87863F1B7B5981BC0F6BF442F46F457615FB0087F03CA4270BA16518468114115B565DA37122B25EFEG6706DD6CE2E86E9B4FE57B3ED060AB72DA72173FFF1C7C65B957123FBBB3CD250B2A7777873C1B778D69
	62287641572309863A631D7DBD30E75F233D6183AB51CF9852F9E11BCD320B87594A4577B78B8C343F89799E6DEF9EE17B6FF8C84F83D88C40E38497F335B1FCA13C55D8GD091CE6709074AE94DEC81DCE8B845763FDBCEFB44B961544F832C6CB2C8E7DF876BABG37AFAFFEA088E4FB36D7228E6B58AAAB18AAE7B7982BB2B0D67BF6733B0A035BF53237DBBF9284A5B65895A4B648D6A6CE7F0EDCEC83FD216A6B082775D60A97FA2FA33FEED58B2E433110CE82C881487CBE2CBBG7BG9A81CEG38GC281
	22G62G6682AC84D882A0CCEAB566CC22B96C2EC6BB2C6BCB83D28EG30096CB67358B927634C47C8DB9134D3273435306675815AEAF1BBDD22DBBA47316C9605BCA71FEF8CEE9716B58EEE97D21B5859BD4C6736F3AEB92638220945763B3ABC22E2FFD6B5E5AB38A6CF0B53E343D8A45703DFD982FD2E3F1FA6FD34CE62BE57C5F8CE1C4457BE6E974DBCBE18DF240698281BB04967974D1B447D22A7E55FE83D70FD43E0FE7542C9FA67AD510356531E3F70FA3FE07516CD9A5A5EFCF584FD7771DEAE5B27C4
	E8FD5D8606AF0E507A3ABF5B246CD3300D881A7313F235F7DA046BE12E82D4DDCF7360C1974D03366BE9EE1FF4306FCEA09D8D10F0BD59688F4AC736DF7BEC04754207DD0F4B29FCAF52BF7FFA2D7CAA99BE79FA2D7C9ED26C5E9248EB3901785D150B7D475D4047EF2E7CDCA8BFD36019FE231E3E3CE245FA39F2DE317C2C161FEDB7860BF5EFEB89BED78BDB861F2B25AD7AEB787E1640B53C32C5FF8D2FEED12F61723787ADE8EB3B41DE35AB658EAB38CF955CC72CCD449D37D9259E857F89238B710DA2F628
	789C0C2EFE06EB27F394956ED399A736207DG16FB1C4A396197ADECDFE5335BF95A414A589CE0FE94F9F8980DB617D9C537D7417968BE7BB10E01625BF173C9E7AC9F67887C8EB82CD936736CDA53D1A86868663A335DCE6BFA8F0CEB540DB42FECF6DA1B975CC8736A05E3AC169B89F39020BAF2287B99DA771EE94594BFCF1B33F18D234BEEF308357C6BA9014EB117B23F209D5AF7C5124F7571A6963704F4C4A469F7AD8BE37077E6557B9381F141EF9264EF6BC6358F7E1D715D39F4FFAEBA12FFE71CG4F
	F911F22CB812466A5903F4B6AFCC2631FA2C9E472A8252ADGBD13C9E609AE35CD8A81FCB8C04CE4B91EB2B9D87C62B1E6D367CD561E237C0A61D3A6EB4FD1AC8EBAC7493E2955A85DC47D3F3D57B7CE552A77C74AC7FF4C1E9955BE381E9EE03E5A8260B765A6525DDE06DB8E6955BFA01C194576CD10AED3F0D7F7523E22CA417599686CA8CC413DE125BE22945CB1061B81698485F75F8E444D03F43202FBF687753164874ABAE19F19DF31FF50FA7AFE0503552B0A9A1ADF5192C5FE056DB86A14B5CA3BAE3A
	995E9525DD977FF10C56450B10179CC5B12DD56C1CDE426D887A73704EE3DB7CBC2F6EE618B3B7D3591ECD99D4D62038CCC61B207471F4AE759182F562EF26B9193F19626BA9B7531C6C590CE50B26006D84E81FC27852D7997D108E85081AC263725CC114D37C941F1C12D46F49D3024D557A86ECEF69946DFD2E96064F1C2A3D4F75E4BD5D674A033C102924ABCFB35FEA912417A938747D084B0B863D0DA6DC4CDB08EB0334D5414D59CAFA660DA6FE1EF48CFC2674DB16BF974AAF86483C8556990046DBA85F
	66987DB3254B837A78A64F1438CEFD122BFF86D474092014991B6B9FF73D0159109E1005F48B692637537F0C6548B12C4F779E37F48EE56FC1F1D96D99CBB474013E5CBFE16F417B1FEAA0FD0E3D05764A0E63BEFAFB2D7E740EE176E88E14CDD2681D37431F5EEB5AFD747EF347F0E248DAFA17C39F6BE5FA397EDD417C174CDBDB0D26DBC9A73FBC0638FD10EE3C15F44A49F8606FDE55FB6A7151DF0FDE9D55BE380C7F7835EF0C6AB78DA1760A618C193EDF670EEC5F73679156AB5A9C2CDE5B2D837DDE41C2
	E6D7FBEE557EDE41338C6F3DD57BFB850E4D647306C25E12DBE9FD194A6CF04BB46D6F955C4970BD53343FD7307FD57AA6B6847296CDA3BDCB2BE5EBF60C7A4EDDB943F5GCE0AA15CCDF5E4A3A3E2C89F0FB4845E39BB46F089B17CBCC75E1B570F7E1D3B3FA9FD0C461DBB2E13FFF18546D6324E841CE745E84F33366C8FBC4F5A3B1F6AAD0D6167D92B27437C1F2E2E7760DB7EF66A8936F6D5C0193A69F41E756456CBB94F12267B4E3378F9502BCE6DF9905293B21D4E0B0A1A355F83E0FE3812BF31D0FBEF
	9F73E3147C2475FA6DA7A879DDDB345FD3E07E3C69E447AFE771EAF9ED6A9CEA4C1357E51F3B4BF56FB75059914FDB344217B7F347705AE51F7F48F5A36D7A7EDA9C33710B000F1469B43E6147827D35BD6CA80B4F3977B753B77C98BF1677851B7D3F61DF42523EFCE97368FF43FF5500FDFCB35F70CFD76C157FB77C33985EF49B2C0BG2E5B086FF7DF9D7DEF785B827A78A63F6167BA3139D66BEFF1FCE3ED208DB9D52B7CC658ED72BDC9F8A65EA6FFC7456EC34E2A23B28B811F8210FDBB7843GAD37D36C
	B234C1FB27917549057937539E21668C45D8A2EF27B30CFF6D277D44FC402D1EC1E5276C27B30C3A9934EF18474ADE04F4B8400C99B41F47A13BB20FF1DB07B76F26E31372BD7A0F1DF46E102850F12A19E28D4B955CFF95D28CF65FCC4299B6904E2E60A6EFA19FB2FAA6513AE3230F56B347066A1F706F8B973FCA711105B379BEE169CC9EA3767F8E7136A3D42E6C8E3E37E83CC32F5C06C2AA97FA875FFFC44965146F9559980D2F25B1CA067C5CBB093F1F555298355CC9E374CFD6B69452D1G71CA59276B
	286C3CBB695EE2D67D4077965F2D9F785EE2EF7D407796FFD09F6C5EA227E9CE8369E3E6AC517FE7A752DF8D69F6GD7AC590606B3682B05468E4D76C5AA75BA760F4CB7F40C305E34E15763BEDF5DDB48F7EC2CB656708143A744EAE38DBFD7666DDC48AB3ECB715D1C587F443B381C2265E73C7CCC02E772DDFA3195EBB356DBFA173EFCB36FEEB5665D2DD7AF29D04DEF99142983E8174B5A6F8EEC0F574B5C0075C2647CF07999A3BF9302561B32E5A0794E5329476535FE8B459C96A1AFBFD43EFDBCB212EF
	BBF91B0F9F21B65B203DB0254DDF6E3C346FA95FAD24F693214D65CA3B538E0E34DDF94D2F9D19CE5FB842FA7307DD0F6B7C19BA64FFDF1CF6AE1CE7786AB86DDCB05451DCE8063C58B81A8B49F5A3237B6186F53D1CD97EBAD5B4ABD89C4C62C2BA2AE6E96936B3FC63ACAD5DAFB8096EB6480B1FC57B3413E768BCA26F9E425D3BAF70BC6270DB02C273D1557B427D239FCBF8E1D47B1063584C9FA83BC79D5B1E4FF00D00EBD7E413456E1D5B6FD147369DE534573D2814EBDD0F38A4C8A7AB388B4C3FD8FA0F
	7A1B0AE71A296E6AFB89B7E6A755BD2160FE437C909724C3945CCC96378A07F40C02CBE03FB710F02F7A1B0A6DC66AA3C9A937ED83716B0B33CFE43F9F10978FB6BE1EF06BBFE53F43972F0E331F7D145AEBD14A5D3705F0D17124B31B826C67503E215C99D02FC413CE09572B77BFC7C6565F6F826AE97B4B3CCF2FDECD61487A7BA8201E76AC41245BDFBFEE5D65779055FBD65D684F419D235A875721054CC72BG5E1B6FA33DFAB81F744AF51F92ABF779F8F72B5E7F619CFDB96438C6338F3E779F47EC729C
	60B769BEDA0F47BA7577F94FB97DE3BE296CDCE3B9544B3C1F6A1A4AA8B64DE3C0D714DDCA7C3C62FEDF7C1C37FF434E40EFB0AE2C277DD01DC247354DDA9EB8BDFFEF3E14EFC25A6E77FD93427B3EEBA7755D21747DE2213F1C32F7B13D027C0BCA19C4C360779FCF9B687B0F99CA3947D7784B3342F8A95FFCA45C6F7B66C3763D7678537AG334BF3215CFC050622CF7DE9386A53CBB9E3C81F6DBBE3606D4D5BF2A95F86E54F56FE9B7461966D77439C1F5D2C7D46D5BE7FA954FE2B4A71FFED880C292EBCC4
	71083C59BC263A8F1E0D335577559666DF4AFD350E593E7BEAFCEF7F95FEB2AD53F47B3EA16C6D77625E1E577DB257D7771F25430BE1C4ED76553DF63DFFCC7221D7FDF9F3F77A67ED5B6A4B3B59B03CFB6A71AB7C6F299F4465CAFE3F4964EBB76353617172636D3EF696E1D559D7BDFE084EB06CB81E33A92E7003C33407DFB81B76708579D8B6A781FC96003A840A3D988B8401EC7649C3A33B83F0FA50FA7AF7G22D93D9389C30B9F3892680CA60271B2B6C16B535F4C70939334BE7D1EFC7269A3A1CFF800
	564C59CCCED6C8DFD4F00B585D08098F284F87EF4A7749E81A6A3DACFF74575126D16D036B63FF9F520F2D7F0149A79AE4917BG5953FF6C880C2D0FAF746976029DC30D5F717F7743642C94D86CE67CD14800FEA52170F80FD9B2096C7790CD967E0308A6E73F43E312FAEC9E937D703FA42C7D903A10BF68A9A9A9713805F5CA07CD6C86DD7259B7AB8D3F19EAC893521B04957CC6E113100E57821B048CF6EB30C9B0C8CE1757A5CB9BAD817EB2E31A670C709563A7CD569B4CC905BE5678B175704D7B1FCD7A
	65B10FC1D851A4EC46F5682868E6D76CC4AB7EEC047CDB5D8DC2168BBF69471FDE8582BC6C1161114E5945E6A1272B95FFBB4EA309569A3A3C67110417596FA31562257952C63327C7C21F615DE6A15D013FAAAEEC6059F633233B145F64FB17F111DAE3422190D64A74EEE474E2ABA5CAAB9BE72F5B397D7022F8031056A02C34BB4D56861021535DA4E4C9BDEE51536334DB3D2E772BD9C3197D947ECD076636A86CA333A5681AA8C41F76CE8EDB33F8CD83084C6B96CAD9C9DCF6CB1492565A9E339B3A925DCD
	927ECA3A53DD2064DD761AD47853C31BCE248929B213B88419DE17A42C68B2775A6C6760250E19C7B6B82BA149AFD8AAA30F42A8E9GFE24277E784814272BD216C0E38DC2867B5DA6A0A95BD76A433E5ECE51BDEE46F5DBD705A7DF09943B10DDEAB9CBF21B9D9E0C61C063A68D4F142510DE723DC74A2E1B7E623F98693E2A12300D8F92D4AD6D961D3D25B5DDDDB60728B4GA3864DEFD51A47B2A51AB29B3C573CFC780917BAC1436A84C3EFD9997BDB4E7E6E453F658CD34EB065FBE158D204817FE9733D44
	B3D3CB165808BA644537EC2FB291E89230D38506DD41542D033532D6412E8C509B832257AA230729D7D851DC76F74B30A946DADBE5CABB64F43E0F52D382DF7AD6B21DA5EC3A8A8B446450CFD0187043A9F7AF3B92AB896575783FFFGD3A3026DE12645E4F318ACBD784B9CA6E7173F8DB27508EE180A59722707E40CAAA5E1A7FEACE8BA6FF4081A9A70EEF7DA4CB095CC782D44CAD68E3FAA2C5495E9EA901126870AF4BD7EF9F9F404DB667D768D78DB3E81E7E795022C225FBECA5F73D11AA983D3C8C9A846
	4F2AC53785BAB71502A2DAC1CDF079B069A967370F317A01665C37D4151B552A4CE9D97D6F272EDACE37FE8BA7DA7BC093AD50C57251EFE2ED2E7C77CB57A427BB7E5F2CE65DC1CC060255F7D67D5638EF73ACF4F979F7653B3FD6FFD73E6C0ACDC13FAB8FFD146F91A39F557E3E67E3EB59BDF840AFFD143EB35C3E964F34037D7E4751DE335DAE196D27EB9CD67CAD100367A451778D744F57E27B9EAAA3E74A3F8B3A96697EC2D5677FGD0CB878869835C36D2A1GG9CEFGGD0CB818294G94G88G88G
	6EE1C7AE69835C36D2A1GG9CEFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0CA1GGGG
**end of data**/
}

/**
 * Return the CICustomerPanel2 property value.
 * @return com.cannontech.dbeditor.wizard.device.customer.CICustomerBasePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private CICustomerBasePanel getCICustomerPanel() {
	if (ivjCICustomerPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Commercial / Industrial Customer Information");
			ivjCICustomerPanel = new com.cannontech.dbeditor.wizard.customer.CICustomerBasePanel();
			ivjCICustomerPanel.setName("CICustomerPanel");
			ivjCICustomerPanel.setBorder(ivjLocalBorder);
			// user code begin {1}
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCICustomerPanel;
}

/**
 * Return the JButtonNewContact property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonNewContact() {
	if (ivjJButtonNewContact == null) {
		try {
			ivjJButtonNewContact = new javax.swing.JButton();
			ivjJButtonNewContact.setName("JButtonNewContact");
			ivjJButtonNewContact.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJButtonNewContact.setMnemonic('n');
			ivjJButtonNewContact.setText("New Contact...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonNewContact;
}


/**
 * Return the JComboBoxPrimaryContact property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxPrimaryContact() {
	if (ivjJComboBoxPrimaryContact == null) {
		try {
			ivjJComboBoxPrimaryContact = new javax.swing.JComboBox();
			ivjJComboBoxPrimaryContact.setName("JComboBoxPrimaryContact");
			ivjJComboBoxPrimaryContact.setToolTipText("Who will be the primary contact responsible for this customer");
			ivjJComboBoxPrimaryContact.setEnabled(true);
			// user code begin {1}
			
			refillContactComboBox();
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxPrimaryContact;
}

/**
 * Return the JLabelEnergyCmpy property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPrimaryContact() {
	if (ivjJLabelPrimaryContact == null) {
		try {
			ivjJLabelPrimaryContact = new javax.swing.JLabel();
			ivjJLabelPrimaryContact.setName("JLabelPrimaryContact");
			ivjJLabelPrimaryContact.setToolTipText("Enter the contact in charge of this customer here");
			ivjJLabelPrimaryContact.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelPrimaryContact.setText("Primary Contact:");
			ivjJLabelPrimaryContact.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPrimaryContact;
}


/**
 * Return the JLabelPDA property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTimeZone() {
	if (ivjJLabelTimeZone == null) {
		try {
			ivjJLabelTimeZone = new javax.swing.JLabel();
			ivjJLabelTimeZone.setName("JLabelTimeZone");
			ivjJLabelTimeZone.setToolTipText("Time zone this customer is located in");
			ivjJLabelTimeZone.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelTimeZone.setText("Time Zone:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTimeZone;
}


/**
 * Return the JPanelTrigger property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelCustomerInfo() {
	if (ivjJPanelCustomerInfo == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Customer Information");
			ivjJPanelCustomerInfo = new javax.swing.JPanel();
			ivjJPanelCustomerInfo.setName("JPanelCustomerInfo");
			ivjJPanelCustomerInfo.setBorder(ivjLocalBorder1);
			ivjJPanelCustomerInfo.setLayout(new java.awt.GridBagLayout());
			ivjJPanelCustomerInfo.setFont(new java.awt.Font("dialog", 0, 14));

			java.awt.GridBagConstraints constraintsJLabelPrimaryContact = new java.awt.GridBagConstraints();
			constraintsJLabelPrimaryContact.gridx = 1; constraintsJLabelPrimaryContact.gridy = 1;
			constraintsJLabelPrimaryContact.gridwidth = 2;
			constraintsJLabelPrimaryContact.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelPrimaryContact.ipadx = 7;
			constraintsJLabelPrimaryContact.ipady = -2;
			constraintsJLabelPrimaryContact.insets = new java.awt.Insets(4, 8, 5, 1);
			getJPanelCustomerInfo().add(getJLabelPrimaryContact(), constraintsJLabelPrimaryContact);

			java.awt.GridBagConstraints constraintsJComboBoxPrimaryContact = new java.awt.GridBagConstraints();
			constraintsJComboBoxPrimaryContact.gridx = 3; constraintsJComboBoxPrimaryContact.gridy = 1;
			constraintsJComboBoxPrimaryContact.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxPrimaryContact.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxPrimaryContact.weightx = 1.0;
			//constraintsJComboBoxPrimaryContact.insets = new java.awt.Insets(1, 1, 2, 4);
			constraintsJComboBoxPrimaryContact.insets = new java.awt.Insets(0, 4, 0, 0);
			getJPanelCustomerInfo().add(getJComboBoxPrimaryContact(), constraintsJComboBoxPrimaryContact);

			java.awt.GridBagConstraints constraintsJButtonNewContact = new java.awt.GridBagConstraints();
			constraintsJButtonNewContact.gridx = 4; constraintsJButtonNewContact.gridy = 1;
			constraintsJButtonNewContact.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonNewContact.ipadx = 9;
			constraintsJButtonNewContact.ipady = 4;
			constraintsJButtonNewContact.insets = new java.awt.Insets(0, 4, 1, 11);
			getJPanelCustomerInfo().add(getJButtonNewContact(), constraintsJButtonNewContact);

			java.awt.GridBagConstraints constraintsJLabelTimeZone = new java.awt.GridBagConstraints();
			constraintsJLabelTimeZone.gridx = 1; constraintsJLabelTimeZone.gridy = 2;
			constraintsJLabelTimeZone.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelTimeZone.ipadx = 8;
			constraintsJLabelTimeZone.ipady = -2;
			constraintsJLabelTimeZone.insets = new java.awt.Insets(4, 8, 8, 3);
			getJPanelCustomerInfo().add(getJLabelTimeZone(), constraintsJLabelTimeZone);

			java.awt.GridBagConstraints constraintsJTextFieldTimeZone = new java.awt.GridBagConstraints();
			constraintsJTextFieldTimeZone.gridx = 2; constraintsJTextFieldTimeZone.gridy = 2;
			constraintsJTextFieldTimeZone.gridwidth = 3;
			constraintsJTextFieldTimeZone.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldTimeZone.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldTimeZone.weightx = 1.0;
			constraintsJTextFieldTimeZone.insets = new java.awt.Insets(0, 4, 0, 0);
			getJPanelCustomerInfo().add(getJComboBoxTimeZone(), constraintsJTextFieldTimeZone);
			// user code begin {1}
			
			
			java.awt.GridBagConstraints constraintsJLabelCustNumber = new java.awt.GridBagConstraints();
			constraintsJLabelCustNumber.gridx = 1; constraintsJLabelCustNumber.gridy = 3;
			constraintsJLabelCustNumber.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelCustNumber.ipadx = 8;
			constraintsJLabelCustNumber.ipady = -2;
			constraintsJLabelCustNumber.insets = new java.awt.Insets(4, 8, 8, 3);
			getJPanelCustomerInfo().add(getJLabelCustomerNumber(), constraintsJLabelCustNumber);

			java.awt.GridBagConstraints constraintsJTextFieldCustNumber = new java.awt.GridBagConstraints();
			constraintsJTextFieldCustNumber.gridx = 2; constraintsJTextFieldCustNumber.gridy = 3;
			constraintsJTextFieldCustNumber.gridwidth = 3;
			constraintsJTextFieldCustNumber.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldCustNumber.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldCustNumber.weightx = 1.0;
			constraintsJTextFieldCustNumber.insets = new java.awt.Insets(0, 4, 0, 0);
			getJPanelCustomerInfo().add(getJTextFieldCustomerNumber(), constraintsJTextFieldCustNumber);
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelCustomerInfo;
}

/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxTimeZone() {
	if (ivjJComboBoxTimeZone == null) {
		try {
			ivjJComboBoxTimeZone = new javax.swing.JComboBox();
			ivjJComboBoxTimeZone.setName("JTextFieldTimeZone");
			// user code begin {1}

			//this may change some day in a new release of Java
			if( ivjJComboBoxTimeZone.getEditor().getEditorComponent() instanceof JTextField )
			{			
				((JTextField)ivjJComboBoxTimeZone.getEditor().getEditorComponent()).setDocument( 
					new com.cannontech.common.gui.unchanging.StringRangeDocument(40) );
			}

			ivjJComboBoxTimeZone.setEditable( true );
			
			String[] tz = CtiUtilities.getTimeZones();
			for( int i = 0; i < tz.length; i++ )
				ivjJComboBoxTimeZone.addItem( tz[i] );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxTimeZone;
}

private javax.swing.JTextField getJTextFieldCustomerNumber() {
	if (jTextFieldCustomerNumber == null) {
		jTextFieldCustomerNumber = new javax.swing.JTextField();
		jTextFieldCustomerNumber.setName("jTextFieldCustomerNumber");
		
		jTextFieldCustomerNumber.setDocument( new StringRangeDocument(64) );
	}
	return jTextFieldCustomerNumber;
}

private javax.swing.JLabel getJLabelCustomerNumber() {
	if (jLabelCustNumber== null) {
		jLabelCustNumber = new javax.swing.JLabel();
		jLabelCustNumber.setName("jLabelCustNumber");
		jLabelCustNumber.setToolTipText("A mapping number assigned to the customer");
		jLabelCustNumber.setFont(new java.awt.Font("dialog", 0, 14));
		jLabelCustNumber.setText("Customer Number:");
	}
	return jLabelCustNumber;
}

/**
 * getValue method comment.
 */
public Object getValue(Object o)
{
	Customer customer = (Customer)o;
	if(customer == null)
		customer = new CICustomerBase();
	//set the primary contact here
	if( getJComboBoxPrimaryContact().getSelectedItem() instanceof LiteContact )
	{
		LiteContact cnt = (LiteContact)getJComboBoxPrimaryContact().getSelectedItem();
		customer.getCustomer().setPrimaryContactID( new Integer(cnt.getContactID()) ); 
	}
	else
		customer.getCustomer().setPrimaryContactID( 
				new Integer(CtiUtilities.NONE_ZERO_ID) );


	//get the selected Time Zone if there is one
	if( getJComboBoxTimeZone().getSelectedItem() != null
		 && getJComboBoxTimeZone().getSelectedItem().toString().length() > 0 )
	{
		customer.getCustomer().setTimeZone( getJComboBoxTimeZone().getSelectedItem().toString() );
	}

	//only get the set values for the CICustomer if it is one
	if( customer instanceof CICustomerBase )
		customer = (CICustomerBase)getCICustomerPanel().getValue( customer );

	if( getJTextFieldCustomerNumber().getText() == null
		 || getJTextFieldCustomerNumber().getText().length() <= 0 )
	{
		customer.getCustomer().setCustomerNumber( CtiUtilities.STRING_NONE );
	}
	else
		customer.getCustomer().setCustomerNumber( getJTextFieldCustomerNumber().getText() );



/*FIXFIX
	//WebSettingsDefaults, only used if we do not have a CustomerWebSettings row yet!
	if( customer.getCustomerWebSettings().getLogo() == null )
		customer.getCustomerWebSettings().setLogo( com.cannontech.common.util.CtiUtilities.STRING_NONE );

	if( customer.getCustomerWebSettings().getDatabaseAlias() == null )
		customer.getCustomerWebSettings().setDatabaseAlias( com.cannontech.common.util.CtiUtilities.STRING_NONE );

	String home = getJTextFieldWebHome().getText();
	if (home.length() == 0)
		customer.getCustomerWebSettings().setHomeURL("/default");
	else
		customer.getCustomerWebSettings().setHomeURL(home);
*/

	return customer;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	
	getCICustomerPanel().addDataInputPanelListener( this );
	getJComboBoxTimeZone().addActionListener(this);
	getJTextFieldCustomerNumber().addCaretListener(this);

	// user code end
	getJComboBoxPrimaryContact().addActionListener(this);
	getJButtonNewContact().addActionListener(this);
}

public void inputUpdate( PropertyPanelEvent event )
{
	this.fireInputUpdate();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CICustomerBasePanel");
		setToolTipText("");
		setLayout(new java.awt.GridBagLayout());
		setSize(412, 346);

		java.awt.GridBagConstraints constraintsCICustomerPanel = new java.awt.GridBagConstraints();
		constraintsCICustomerPanel.gridx = 1; constraintsCICustomerPanel.gridy = 1;
		constraintsCICustomerPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsCICustomerPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsCICustomerPanel.weightx = 1.0;
		constraintsCICustomerPanel.weighty = 1.0;
		constraintsCICustomerPanel.ipadx = -15;
		constraintsCICustomerPanel.ipady = -21;
		constraintsCICustomerPanel.insets = new java.awt.Insets(14, 5, 4, 10);
		add(getCICustomerPanel(), constraintsCICustomerPanel);

		java.awt.GridBagConstraints constraintsJPanelCustomerInfo = new java.awt.GridBagConstraints();
		constraintsJPanelCustomerInfo.gridx = 1; constraintsJPanelCustomerInfo.gridy = 2;
		constraintsJPanelCustomerInfo.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanelCustomerInfo.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelCustomerInfo.weightx = 1.0;
		constraintsJPanelCustomerInfo.weighty = 1.0;
		constraintsJPanelCustomerInfo.ipadx = -15;
		constraintsJPanelCustomerInfo.ipady = 3;
		constraintsJPanelCustomerInfo.insets = new java.awt.Insets(4, 5, 95, 10);
		add(getJPanelCustomerInfo(), constraintsJPanelCustomerInfo);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	// user code end
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getCICustomerPanel().isVisible()
		 && !getCICustomerPanel().isInputValid() )
	{
		setErrorString( getCICustomerPanel().getErrorString() );
		return false;
	}


	return true;
}


/**
 * Comment
 */
public void jButtonNewContact_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	QuickContactPanel qPanel = new QuickContactPanel();
	
	OkCancelDialog dialog = new OkCancelDialog(
			CtiUtilities.getParentFrame( this ),
			"New Contact",
			true,
			qPanel );

	dialog.setSize(350, 200);
   dialog.setLocationRelativeTo( this );
   dialog.show();
	
	if( dialog.getButtonPressed() == OkCancelDialog.OK_PRESSED )
	{
		
		Object o = qPanel.getValue(null);
		if( o instanceof Contact )
		{
			Contact cnt = (Contact)o;

			fireInputDataPanelEvent( 
				new PropertyPanelEvent(
							this,
							PropertyPanelEvent.EVENT_DB_INSERT,
							cnt) );	
			
			refillContactComboBox();
			
			//select the newly created contact in our JComboBox, seems reasonable
			for (int j = 0; j < getJComboBoxPrimaryContact().getModel().getSize(); j++)
			{
				if( getJComboBoxPrimaryContact().getItemAt(j) instanceof LiteContact )	
					if( cnt.getContact().getContactID().intValue()
						 == ((LiteContact)getJComboBoxPrimaryContact().getItemAt(j)).getContactID() )
					{
						getJComboBoxPrimaryContact().setSelectedIndex(j);
						break;
					}
			}		
		}

		
	}
	
	dialog.dispose();
	
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (8/9/2001 2:54:35 PM)
 */
private void refillContactComboBox()
{
	getJComboBoxPrimaryContact().removeAllItems();

	//gotta have a NONE
	getJComboBoxPrimaryContact().addItem( CtiUtilities.STRING_NONE );

	IDatabaseCache cache = DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		List contacts = cache.getAllContacts();
		for( int i = 0; i < contacts.size(); i++ )
		{
			getJComboBoxPrimaryContact().addItem( 
				(LiteContact)contacts.get(i) );
		}
	}


}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		CICustomerBasePanel aCICustomerBasePanel;
		aCICustomerBasePanel = new CICustomerBasePanel();
		frame.setContentPane(aCICustomerBasePanel);
		frame.setSize(aCICustomerBasePanel.getSize());
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
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/4/2003 10:51:34 AM)
 * @param type_ int
 */
public void setCustomerType(int type_) 
{
	getCICustomerPanel().setVisible( type_ == CustomerTypes.CUSTOMER_CI );
}


/**
 * setValue method comment.
 */
public void setValue(Object o) 
{
	if( o == null )
		return;

	Customer customer = (Customer)o;

	if( customer.getCustomer().getPrimaryContactID().intValue() 
		 >= CtiUtilities.NONE_ZERO_ID )
	{
		for( int i = 0; i < getJComboBoxPrimaryContact().getItemCount(); i++ )
		{
			if( !(getJComboBoxPrimaryContact().getItemAt(i) instanceof LiteContact) )
				continue;

			LiteContact cnt = (LiteContact)getJComboBoxPrimaryContact().getItemAt(i);
			if( cnt.getContactID() == customer.getCustomer().getPrimaryContactID().intValue() )
			{
				getJComboBoxPrimaryContact().setSelectedIndex( i );
				break;
			}
		}
	}

	//try to find our timezone in the combo box
	getJComboBoxTimeZone().setSelectedIndex(-1);
	for( int i = 0; i < getJComboBoxTimeZone().getItemCount(); i++ ) {
		if( customer.getCustomer().getTimeZone().equalsIgnoreCase(
				getJComboBoxTimeZone().getItemAt(i).toString()) ) {
			getJComboBoxTimeZone().setSelectedIndex( i );
			break;
		}
	}

	//select the unique string they have entered	
	if( getJComboBoxTimeZone().getSelectedIndex() <= -1 )
	{
		getJComboBoxTimeZone().addItem( customer.getCustomer().getTimeZone() );
		getJComboBoxTimeZone().setSelectedItem( customer.getCustomer().getTimeZone() );
	}
	
	if( CtiUtilities.STRING_NONE.equals(customer.getCustomer().getCustomerNumber()) )
		getJTextFieldCustomerNumber().setText("");
	else
		getJTextFieldCustomerNumber().setText(
			customer.getCustomer().getCustomerNumber() );

/*FIXFIX
	String home = customer.getCustomerWebSettings().getHomeURL();
	if( home != null )
		getJTextFieldWebHome().setText(home);
*/

	//only set values for the CICustomer if it is one
	if( customer instanceof CICustomerBase )
		getCICustomerPanel().setValue( customer );

	getCICustomerPanel().setVisible( customer instanceof CICustomerBase );
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getCICustomerPanel().getJTextFieldCompanyName().requestFocus(); 
        } 
    });    
}

public void caretUpdate( CaretEvent e )
{
	if( e.getSource() == getJTextFieldCustomerNumber() )
		fireInputUpdate();
}

}