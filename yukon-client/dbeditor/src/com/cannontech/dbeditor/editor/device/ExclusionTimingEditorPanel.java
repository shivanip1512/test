package com.cannontech.dbeditor.editor.device;

import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.pao.PAODefines;
import com.cannontech.database.db.pao.PAOExclusion;
import com.cannontech.common.util.CtiUtilities;
import java.util.Vector;
import java.util.StringTokenizer;
/**
 * Insert the type's description here.
 * Creation date: (4/4/2004 11:31:17 AM)
 * @author: 
 */
public class ExclusionTimingEditorPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JLabel ivjCycleTimeJLabel = null;
	private javax.swing.JTextField ivjCycleTimeJTextField = null;
	private javax.swing.JLabel ivjOffsetJLabel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjJLabelMinutes = null;
	private javax.swing.JLabel ivjJLabelSeconds1 = null;
	private javax.swing.JLabel ivjJLabelSeconds2 = null;
	private javax.swing.JTextField ivjJTextFieldOffset = null;
	private javax.swing.JTextField ivjJTextFieldTransmitTime = null;
	private javax.swing.JCheckBox ivjJCheckBoxEnable = null;
	private javax.swing.JLabel ivjTransmitDurationJLabel = null;
	private javax.swing.JPanel ivjJPanelTimeSettings = null;

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ExclusionTimingEditorPanel.this.getJCheckBoxEnable()) 
				connEtoC5(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == ExclusionTimingEditorPanel.this.getCycleTimeJTextField()) 
				connEtoC1(e);
			if (e.getSource() == ExclusionTimingEditorPanel.this.getJTextFieldOffset()) 
				connEtoC2(e);
			if (e.getSource() == ExclusionTimingEditorPanel.this.getJTextFieldTransmitTime()) 
				connEtoC3(e);
		};
	};
/**
 * LMIExclusionEditorPanel constructor comment.
 */
public ExclusionTimingEditorPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (CycleTimeJTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMIExclusionEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC2:  (JTextFieldOffset.caret.caretUpdate(javax.swing.event.CaretEvent) --> ExclusionTimingEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC3:  (JTextFieldTransmitTime.caret.caretUpdate(javax.swing.event.CaretEvent) --> ExclusionTimingEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC4:  (JTextFieldMaxTransmit.caret.caretUpdate(javax.swing.event.CaretEvent) --> ExclusionTimingEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC5:  (JCheckBoxEnable.action.actionPerformed(java.awt.event.ActionEvent) --> ExclusionTimingEditorPanel.jCheckBoxEnable_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxEnable_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
public void cycleTimeJTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	return;
}
public void enableTimeFields(boolean truth)
{
	getCycleTimeJLabel().setEnabled(truth);
	getTransmitDurationJLabel().setEnabled(truth);
	getOffsetJLabel().setEnabled(truth);
	getCycleTimeJTextField().setEnabled(truth);
	getJTextFieldTransmitTime().setEnabled(truth);
	getJTextFieldOffset().setEnabled(truth);
	getJLabelMinutes().setEnabled(truth);
	getJLabelSeconds1().setEnabled(truth);
	getJLabelSeconds2().setEnabled(truth);
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G21BA31B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DDC8FF8D44535CF7BD079BE31469A5F2396ADC55A261A7AF82F3466757135D151C6C1458A9AA4AAD6D05A868C0A026DC6AE3B793379C7B6A1097C0932C850CD0CA1012098D772BFA698B01508711516AC591BE4E1333B6E5EA49B08751DB3F3E76E5D4D5DE40356CFBFCF6E1D5F4C19B9F3664C1919B3F7918AF74FCE5B22AF9404ADE9027C5F5BDB84417809A0642F6F5942B0732FF569026A3FBDC079
	4243B529702C023C4E4214F413706DF2A3241BA1BDABBBA57DA5F85FA6DCD65EF18CDEC234D38AF94F0E9C7B7B446D2CCBE76DE410749B7A9CF8EE85EA834C89948BE4921A745BE5BC9252DB65F724ADFAC1707F864B5C5834DD056B64F29D105783640943B27F50174879F100A5GD5GDE9FC74BF643B313C8F7C96DE1395C292B22058DF73F224F4536D475B30A15F9936527D978B1D104C148ABEA02273573DC7F11DE77E0E52F4D65F23BA431F72052FEC634BBA43737D2FE5845E1C72FD8DB5BF12C3D455F
	6B9C72B95C2E961255E9F309CE2163D614FDC91311548BAA07F482415A1AD1DF11709E8D94F7AB137F7EDC2BACDF5439E8E1658D79A165AF390D4ADDFF1BA27F7C937B8D4937E17D171171C03E88C84782513ED58C37CE31EF3CE3DD0EC1072BDF5DBD39FF7769D97FEAEEE77DAB2F43761D101E86B40B636B89BE9F520B01D65D4E7A7D5E6B2C5F5D97EE91B61DE950670568376EF65A5F5205CA3FFBCE8F9AF690BBBCEFC4DB6A013CE9C0B30196GA5826900F6917BBF14D18E4F06B3D213FBF0506DEA70F89A
	1DF66FB137DDF442FBEDAD644858C7E22F5BEB9784EA634B1AAAB464E1D4F8C720FDA3513EFDA2B01B0C7968E6C1125E5627E974ED7EB2335EA257E13DC369DB649D2837E4E2AF27609D790A50F688EEE8C1FC298A2763FF1C14473471CEDD7A7CBB191E3B37B2D9BAD26788266AA6FD0606ACB3EFBB2C6F79AD15417FDBC5962A632EAC5471DC40630092001201CC713A742AF82663AFB28ECE2663F630BB2F4B66BCB1F446E373CA0DC38E27DD744A3A3EC2BF112E350872FDFF4AFCD4AF518755E3147873C6CD
	3DA4C63F2E6F08277A3047ABFA21EDAF8D59763478713631150F6175E4CE4C0C27E3E860F8B44123D5B89D5BAA02E3BA9672CA6E52255372E2BD6291F75172193C9EAF41E729F0DA4FC6D28F26E3A12F64EEDDBA9D5FE7325157C6C0FA9650FC2045C0C9C046C5D0EE919B633BB37607B90FBAD563CBF97F4DD88CCF51AFF9ED6DAE1FE4F3750AA7EDDE076D0CD37489F21999C6546B19G3E2B487C6E05B65E97DD2257A601E3025AED03A298130F163956180FE53CA2498537A5C283039E0155BB47388D1ECE1B
	CF7A58E337C922D202665FE8C41D8C5BCEC20DD08116DD047ACAA2FE5C8A6F5D0B182EF667A2B68F5271DCAF7BB28B6079819837682C2DEDF6742298B66FA83351671BADE8CBD0FE46E228779E70D3C066FBF569B1C0076F03F985543C84743E044D5BF55D4CBE1BF3FFA6D4ACEB5034CF24317B292DC55D9FEC1F19AD586E82409301F4C0A53F5325DB01CE810D814D82DAG1478BB362EF7B54F8EF39E217C419876857B09FDD1630B170CAF0BBAD027D187933547F407BA2332FE91362C599AD81EBC27368561
	C59AE5B30352791334077E9EE544351C5A4D95DBF16E69C04F5B9F00B1FF0059CECF26194E29C61BCFE43368C3AFCCG512ED892AD7B77AC14D99C96DDD2336891DDF6F87149B6EC4F4AD466D71F1B5B7ED902233FED0289606338A7AB1F62D007FA3632FCBF4937C91257F1E6C892CF0812841B8105BF0248628BF065BE392FD766E1DFBB210F83C0FE5E574C6CED2AF95261F61F9B72F4218710675B1F483EE286141F7BG9B6339F5386F0C67691BC9DA49FF2B2EBBCC9B8817302D40B566432F3C8D036A1D36
	F12E5EA45079ED106DA7136779C95E86102BE4A94CADA067D2567F94A21B1ACF4BDEAF16E89B45755D2A7AB33E62B628ED6832C35B36A1C75B36B3F334EDBBAFE7E25BFDA9C74BF6E95EABB958C77708CBB597766620FF6FF53BDCA219101CC520FB6CF5DD588F074BA1FD6230770B528947F9917C6ED232DFEBC61B0E027768254C370F6CE53E3D747CCF053853A51A7B4319BFFAD7AFAC2336D838CC716DD42652DC1CE322D39C84C5B53907DC92454B497AA463BE6A5F56FDB897F9ADCD233DCE32789D6D82E5
	36BAC4273D2297BC00D4C97E524551F256B6EC73D77AC6C0E515E4BCAA1BB03785DF4B4CEF3EF9449C6274D351BB3E7E53CD2A1ABBA62A7A5D3213E5D5D45E3FE68FFFAD7276B93CE23B4BB344EAC289581A3047BA76154A5069F57B61BCA50D06AD8915A3EE4BA811C3E97E433EBE1FA8FDBDC2D48BCC9F39A5DF844951653539FCD099AA676B116600AC4B63857F8C1265184D4F4479FAC4B94874D2E29D7DCA6DA47469BB04394AF2ACB38CFFF5F6B2B5A12A198C3A0A312F5AC626A64981AA47DF9A293FBB5A
	B4A0761EEBF47BDBDC384B2830916FDDC99F1D22374F6D9D946DC40ACA5B08A40B50C032431361ECE08B279B022A6E88DDB77A3B9A3ADF3A4D043E3C86FCFC9BD04FB236DF7AF1AE66337D123CA9ECF2BB1DF499023C054BC84C2D25D035EE781751F85CD61E6F1EA47F7B13647FF6127CE7A7495F5DBAF17E8713640FCC12FF43A47977054C27FA3EFAAB6E2FFC62E7C3A29CCBF1A0BF90C75959F2EBC1D05EC91BF3C8B9334E590AE78B27EAAFCB71EB723457676F67617E759C2F7F87F998EC9D166B247B09F9
	B62C5363F58C42569FB767F25CD4EFD047CDF39E5425E30C35831EBDC054769FAEFE991E937AB364FD886B0B29E7B1D6BF5685B4CF2EB369C12A9FDA4E5D404AF5C35A83947910D639830D2C5CE2C82F8AD96EC9DEAEB5817634C05684DAEEAC01160BCF40F2BB1BD91CF1BF472A89369CB621172432D84EB113BA86EC7755E86E27C87B4B65FD547260985746FC2C379E704E65EC7E6DB0294FAB8DBE1F3B57C1C28C7298471B54FBAD0A5DE34A02A778A61F548E1BD8BF331387CCA8AB46A0E4D89E6B1B5EF8CF
	989FD32123450E03CDA344E3F4391B5CF6318DCC2D09EFC5CFBA449101696173D6F5ACF9C1DE19BEBB049E6CF27F272974D0FAFA50C0E57ADEAD467234E2463DC4484A7EA1C765106440E007CD3291A72C0EFFBB8D2897ECFB61F29687AEA473C187694207F56995C0B4A6350C442A305DE6404600681C3DE0D04F597900A78289AB181DDB9B50A626382448BADFD8D6C047A8E87DE0FA1C332D1E59714DF3846B97D91A316969F9057A4295F27CF6052247B52D278CBAA05A3F198D2CFF5321CC5C8A563F23E62D
	7EED627D5BFCD17D23ED7E2D11351913886745C456E6D729D61BE309ECAC721A98DFA4E0711C6F41BCB51F7088BD1FDB9EE1328EB79D006754D7D7B687366DBF824FAF371672587B8135AF08DBE989698B2C0F50B16BFCA458972C5806766B84BC6291668B0E67672BFC41C7B07BEC2EFE70767C7C1A0F3AG3FAE270582EC1778F50145003697219E23204EF9C071C0AB0104C7D99B1795CC9AC3EF71CF90C37F4C40625166C7E91FEA9ED5E299ED70FE8A482342E83CF8BA191B539E257162B40EDFCD7019AA1C
	5A494EBCE6A7B110E7FA0C45A2D654874743254782E3FA11C1E94A7707F1FC15C16979C1FC54EE76D9CA181FD2CD0B5ADAE57FC9574D46CC753A3978B1EA3F4970CCDD896BA771CF7F4DFDD649CA966BF595E37DDD1076GCD871A8B948F348A4878B89B37878B34FCF8603805F260CA5C0D6A627A060B53E14CB83EE09DDA9E8F64237D7E1619753B7EF1B60E36460B1341BD0EAFD8863F268C1717B299A2398CC5636E5E4213E16FB83ED70252710FEB595211133B34ED09F9A5D956DC9313F565634CCEEEFE89
	316D3F871F78FBE6938737E16C17790BCE3743A5894CD72C5C06FBBE3757516FF0B1ECB541FA5DDE92F8221839A8F4CC6C75A26D18581BC55AB13103C59347FB5FAD8A95B323315969E4FE9C067EF581D16C729D088DC0FA0CE3B93B901B8D69D80E3DD902D8A224B7F06C29A2440CCF68524DCF30B17FCE9EE2B5106E6458778806FBC4DC2BA8E64E3B58F8AC564B5EEB1E883403C0BBFA67225BC8D33D9BC214F919645DD51CC746446793FD44C160EECA4EFBCAAA92B4E20E47ED0322404E5B858D19F2199649
	5DD44DED7959326D5A36CCB6E9540E4FE79A65721EA7589E6DBB649B02E8C847A1464760963E2EA483163A0A6AED3B7C2C00E77D2AC0DD4AF78845216D770BE2ED7B3D2498670D4FBD646D9503EDF4C671F61E27F850F6C99CC4354867AE42AF593C7D62B89B1FC578E9DEA87E9F95071A87F21CA5C06787383DDCEAE6BA6A82DD8C487A98131F11701C9B24A3DA5F4CC6F5FD87F9FD11252C3ED860DBAC732E141F9B60E9DC2DD5DFC7133A3EC33C3E82932B2FC445D7A53FB743339B48294A237A7879CB38767B
	F97A96129E656D7D40007A76F845BE516B956DB474AD442EC6FB0AA5BEA3825EE72CE6FB45B9BB504FB9ED236EA1F0081DCFE23917F686FC8F84E5CB81BF7CA415055A74FF0D204F6D735AB084F342A3768A95CFA16F5EDD0CF7A6140F81DA8C1484147394EB333984F90151B1B8B4C8A424721DA2FEC902F2913C6C0BC5242C4D9FD43638984B46C039D8DEB6E78F5AF7D014C956CBDA8D1EB1680C436817E2C8725DE5E670BA356AA9C5771B1E9A6F1B681E2C5C00F3B4E78D5D13ED63381560D695CE77EAD1E4
	CE137BEF48B37E01795FFB49584C05F49C47BA5A90DB8A69A40EDDCFB0610F707EC70699EC4C4717F0ACA5076C19A1BD0DE35719D99BB3B9F605053591433165840B0374920E9DA9E3EDA4F26CB5B3EB2362CF8C7BC9A9EB231EE352AE56C697476288B6GE9BF47EE5A435A184631374BD89BCBB9D6C6567CB510CECDE21831002C791036F0EC300055D74331B2324ECD0374AC0EBDBAC23EA100F4BCD0E292FEEF779F38CD11EDA7E207991EAEF1444C0FBC4DE0C2AE0C63D8EA2A2BAA4C875F2AAA23671D59A3
	6A734E612C4A10679D73DA79CCA0BF2352764AF32B3AA7E09F0D8D56563E6FF5589BED7D9DE42652B5633D1DC639EE67DA36E61C5B0976B597528B00162EE5F7DC7E57D8ACA26647F3040E08C64D6F5AD6F6156913E4F9CC6B14F318F4FA50C05BEC5F455A6C007CE96BC8BCF0177ABC0ABA3BDFCF771AB47F278179684F719BC6A5FFC5C0BE4AFB48204E4FADD167230E5E1079E95F966DE3FD53AD1AA7B8FFD195721B3D45F21F126527E5692BC2E74AA9839D73DB4C4A18A79605B366146F61BD8A5F462D6170
	D13B7BF73266DB1E5625DB1FE63678BFD94CE6FB1EE66BFD1521946D9352CB0012000C4930CF833282F583C9C0B313D979C6D29E63078BGCB8452A553B8E149FA60D9CFE30127563372576AA3E57D30FA703DBBD9FBBF06ED239C5690790CD05EF076E7E60DFA12F85FEF6532658B4A37A1F9A16A4A146DA3F712B6C36D8303C96BBB158CBE562BA7D8EBDFA47EFC8CF4BCFDBDDB1FFA5BB096E9F3BA7ADD98216CE6E5AF901FBE9F4A4573323F68413834849BB2EAE3D9FB5146AA1EA167FDDB7384B2274972B1
	5A84F566GDF111CE7FE062EC9DBF8BF8AF33557638AF2F7ADC17988A00A5DC9FCFE8C26B976CB8B13FF55B3CC7EE4CB207CEF16AA72FFDCB6197C6ABE94BE9BA83F65D9C5FE5A7E07C4269A406B1FE5B2ADA16B109352FE0EDD370B49B977D9A667C33B82656C5C2748F95B1E49644CD04939B4C84EB563646CA1EB598640058D2C7DFAD3E89B7A142CF395D076B0AF3F3BA0D05E585794F92795CCA42FA108D0D6A6AF4AE85F90EC9775A176DE5DE48D75C079B1AE57F3A35AB6649B453251D0EE7E8636965D77
	B7759AE93AF63B1E359BAC734A8D54B7A4ED880E895E78BA56EBFE8E7C50F36C0C1F788A2E8BA7EDE747C76D816C743ABD22D79A25A839B51277FE0DBB277A4DAA5D63BDC6565AD34FA9FA23789A028F28F03A777BECA77BA6DC023C0467D83CE205051C8F1F87DF8E64811A8E948D3470F97ECD63EE3CDB1EB026556576F440614E298AA7507E9D5FF5F15F645E1A415655793F3AD1083DB4F9CB287B0B04676978EC121FF13FFB1560F25C2E9C7BE75928CBEF869AG0AG0A820A59487AB7383B02768F0727C1
	12EC3D83649607F58EAFD694988F9C546EFE16B97E7C32B0B3787C92876DAC5DA81F0F49DCD912B559DCD17B20A46095B65139C2FC52266039F22B9557A60D2F8E647C7BF2F11E9F6D301D911D479CAEB84AAAE7587D645C227012C2F25E519134997A6DG4D207805F23CA38D7E3AC04EFBF9ABEF6B0488E7CF3B0FFF93609F9A17F700653DDCD39CD0277AEB81D626F88F16E1B94DC3748BE0D9E479FEF72D245E2F19B751D8D173A6F6FF31E4CCAB3F1B673FB52C156F6479F7B5EA65C7707C55FB357223F83E
	5E2C159F3D096D7DCB5094657EB63461C1A044FE6E6D6A8A73012AB2C158AD503D89B91B497FD17E6F3661661073426C7538DDF8DD5251525AF53A21237D7D6347DA0EF789DAF5A457E955CBE56CF9D3607374719CB6CFCBEE3AC1E8FEA8F94B5E9073F441A6795E7785451FA61E5CAF6FFF6F2ED464DEA20553EF4A37F63F4277EDCBB8FC346CBD16AF239B5A76B5550ACEE28FA8F244B406AF7F0A038A1FE3D778724F5F756565EFBC2448AF3E254831FAF77872CFFFDB61CB495F9D1A2F7A7081E0BCD0A6735D
	F4D8E97B5EF714BA9E98893F0E75358A5F3E4CF078681AF07D3B48A75F553D061BAE4DF59BBC7FD1286B43B3E745DE294B564FF97E73F534DF9E97244B391C774B57F1FE63771F8E58EC1FA79E6A8494F4755375F075CEEC1B3E2FD33D0F6C7C2A3FAB8EBEB7648B117F52B6E87FD7D9C247F97FE4510E732E37E85FD3EC34CC7C6D6EE6CB68EFF7534B545F6E52F13F490AE3538C7E2787E89A50CC20189758BEA02A6CD5016C83B476GAD76FE0CB3632FD5A4567EC8F9683E7E33FC725F38DCD60EE732B3EEC9
	F28FE27D8C3F229C47CBF2FB88C8316F16230E3C0E7E81099615711964B71ECE310F40B4CE7247CC96A709FF0145E636E761DAAE7CD9173E9D288AA8BC1F4FFC45913C16E7F78AFDA16E9448CACCE5A89D42BAD06636BF33FB550C4F0DF27D919C9B9CB68A74FC3D0AE3C293677D8B43CACA99964131F74C0CB776AFECBFDBD107FB2B929D1CDB00CE81C9C05301E6834D870A87CA845AG141322CBB78355G3581F583B901A2D218CD7C68CDF0F5A17775F2E00C980657263E577B39B534ED7C4ABA39ED5CEEA5
	5F934118F60DFA647B08F8AB39ABA43BADD66E9E2B09E36D3ECE374F019E074E772747451543BBDB4CA036921592FC3638066033D3024F962FE43133C58C6419DEE4711332A1F57B92605301E63DC8577FF9AF0E0F3DD03E1C4F357C54C239FC027CCC8A6297B66B528B01AC1B955C8A6F1DC0F695C65B708EEB3561174B4D582C9DBF1A8D78FC20F8D5FEA23CA783258AA9C1ED24855CBFEF077C8A20FA39DC172AFCA05F1ED2B55FG149B830A144B4F8D49F7442C5527D8397CE28DBE241500EFGB2EED172CB
	603D8A28D905D1FB2B2F8BED576F57CDEE571DF55ABE6FE45DF81F7737BAED1FE72BD37BBC0A755521EF6A0779584D4B1D6358279C7310BA917B44E11786B8BECC64C23CCD448699BEC6649AA1D8370CFDCB05FDAAE31756615C04AE8F38791D7D656477C9FD8E2713262FA4E59CAE182ADC9FD1C4C6074726127BFB9C7B140F6DF7D1BC76BBD755FBE9563FFE5F705E3F348D9AFB2EA76BC5B7585BG90452E3A406EEE46B8768841A2A1BD1BE30DE3ACEE9D4331CA325668A01D23E75827827B4EE0BB47F249DA
	D98169CE0E5988E607F484472E9518FCD19C2BB0B17922B966B7B1D996F06C57EE44F28CA9692586065DCF7AFB98525D9C0BA2EB219352E39CFBE11849BC1DE357BA19FC33B876201749B70FE32B3318FC8BB9D61D4D64CB60588795CCBECBAA43724C6365ABE9E2EDD8F939F2ABEB2313E343D6B6C6F60EBDE4E56DFAB8F6381455974731193C3E259CDB456BDB43311677D8FD9BB856C3E4EECECBC96FCAE3589DE6263F9E0E5DE8E17A13B87693B313E5BA47529CCC16D99C7B2B0335BB0FE30787187E04F406
	BDFDC42DBFFAC679E408788337CB227149C1E2830540E38622582FAE30B8F1E7BA635BF0A1B06EEAEC4079CFE3C91F0ECD94CBD2470835622E9150469B02BA165472F9E0DBEFB4E2DBD436EF13B9B083F8E6F259CAB351670353F31C07FE591C9840E13B9046D7473FAFD948797C1921E34FD7117249D0D64775F2BBE95B121112DE1341300FCDCCD7F64028BE2EB4FD19B8F494549398DBDB66560EAD2FA5F3FAB6140F49E0FDDAB334ADC0978D6C36C566518F33BDEC826739E9B8D05F671B94FD3FCA6E06D30D
	A969A6A35783197BD6C837F1ECB3197B9224A3B836B51B69E61E116926ADFBAAF147E05D2CB426845DA9C844EFA4813E09375BC2FCC4E9A66C9FB299F6424AE469810C76733AE6450657D9A7336109640A4CD4EC1836E7A3FEE5966051DC06DB398C89DC063896C506D36FCDA6C34684B2983302675108B9F0DC1FEED546F59D71B50540E34EE272BDC0FCD287243BB93604141B8669C80ED5BAD8BF623258388EB8A69B5709E4CF06FA583FA7A06B0F78B39D6046EC262B5DA45CC77BF64985BCA78744DADA4160
	AD2F5A9DB6273B7F558AF3D505397AG0FD55DFFA15CD8159C17A27B9BB9AE152D342B1FB046D373B6B4DAD3A617BDC94A2A3F10BF01FF59915EC75AE6B12C9C250DAB05F05A20FCA77394BE33E9EABAF11A262613D1132213D979CA3BF78E052F139D436609F572D17B7BEDDD340D5F9428F4628CDFA77D058A5FA2EF78FC3797ABFC4B3326264BFD59D3536513AFA93A4C5C21341BEA8EDFDEF26C1079DE31CECD5E33562949BB3DC411F7156A5B2099D3ECB7E10A6DE61815F64BD55F197C40923E1E92DED578
	E6CDC13F6F14A9FCAF8CCC2D1FAD0E297573D439524FAF5ED05ADDF5A49CF9693F2B662E0E86279933D6C873F9050271659B3E337C2118534D05FAC15FAE248F1033E33B10B60207D03F873CAF9CC537DC6B7BCC401FB3EB2660C84A7E5FC1BFAFD4FE0DEAE631D146A260B4FC9FC44963223413F3674A61033506A7EEB06805D410031E435B05D4BCCC03B8DEAA16814E61A81400CEG9DB02EE595C1F50FCFE586F1940445F1A18C8EF48E3F649CFA9939974824D1E6BC87CA05630486ED1004D4584FD5CAC2B6
	3976B46395E32DA4E450EBCCF8CB75093D157C310DDDB81A590DE32D99941B9BE00BF77C7050130B9FBD69B388696D428E5C621F903D6486C03463175C72BFA754AAE4FA7073FF5CC80256FD64116613C61DE207D05877917E3454A7097686967376C9C291793153FE7C0D5D7EB61BEFC04221FF37C3C8F561BFFCA49450EC274D553F1FDEB43CCB06EECBC3BDD184D95BC05EEDC4DE2C251257326DFF73766DB93AAA56A068DB05AC275BE6EF3575CAEEEF3B10A98DF8C55F005BE977FB6AE8C5C6329B4511B2C0
	F53BF877313315386D67C21F73DFB7A3F7CDEEAB7EA3FFDEE1BFA909BB21CADE425EB4E073C2D32237DD42FF48496DAD65F95FFAC5FA6D1E77DFBA259736481D44D15C6D77C8A0958E857E868CA4B087C94032F8C515D75FD5F54DCF7F7CFF24A205D592F6D315816B7EFE51BD383F212F4F6192F98520BF287EE5DEBD16298CAAD3603F2A6868324DE7E03C1B8543E0D59579DBCD7E9E403F558429A6C87581D0E232B061FF4179FEE23BFBA5EAA4FCA263B0D73642C8C98F2F3DEB4E8AEBF292742ED5C8A3BFAE
	83E51B14D24707864F085ECBE32EF9B9BBEA6565B8969EB489D6EFFA1FED50619C0517E662C57D089F7473A9D3A8CFC9744315FCDA610672EA0D544B63AC8E51BD63CD8761FDA473F57237FC4A0AC1AE6A0BAA31C1FC298E6C05D569C511BC79D1527D1376629B5C27D66DBE95FD5365BEBE5E22F035AD7C66C97A31A2696763A48D5470B7D85F7FD0FA01F23265B848CE68379BE5DF283F055BF4C9DE486F45238A69BDC0347CD43E053B7F90F6EF9560C2D10AFCE7366790FEA0926ACE7D44204D6914EC4EF38D
	AEBB5E2FB70E526F11645FCF9E427AFD2C0C1CC9739EB904F21FD5717CBFD0CB8788E321ACEE949CGGB0D7GGD0CB818294G94G88G88G21BA31B0E321ACEE949CGGB0D7GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGCE9CGGGG
**end of data**/
}
/**
 * Return the CycleTimeJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCycleTimeJLabel() {
	if (ivjCycleTimeJLabel == null) {
		try {
			ivjCycleTimeJLabel = new javax.swing.JLabel();
			ivjCycleTimeJLabel.setName("CycleTimeJLabel");
			ivjCycleTimeJLabel.setText("Cycle Time:");
			ivjCycleTimeJLabel.setMaximumSize(new java.awt.Dimension(147, 14));
			ivjCycleTimeJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjCycleTimeJLabel.setPreferredSize(new java.awt.Dimension(147, 14));
			ivjCycleTimeJLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjCycleTimeJLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjCycleTimeJLabel.setMinimumSize(new java.awt.Dimension(147, 14));
			ivjCycleTimeJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCycleTimeJLabel;
}
/**
 * Return the CycleTimeJTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getCycleTimeJTextField() {
	if (ivjCycleTimeJTextField == null) {
		try {
			ivjCycleTimeJTextField = new javax.swing.JTextField();
			ivjCycleTimeJTextField.setName("CycleTimeJTextField");
			ivjCycleTimeJTextField.setPreferredSize(new java.awt.Dimension(71, 20));
			ivjCycleTimeJTextField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjCycleTimeJTextField.setText("");
			ivjCycleTimeJTextField.setMinimumSize(new java.awt.Dimension(71, 20));
			// user code begin {1}
			ivjCycleTimeJTextField.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(-9999999999L, 9999999999L) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCycleTimeJTextField;
}
/**
 * Return the JCheckBoxEnable property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxEnable() {
	if (ivjJCheckBoxEnable == null) {
		try {
			ivjJCheckBoxEnable = new javax.swing.JCheckBox();
			ivjJCheckBoxEnable.setName("JCheckBoxEnable");
			ivjJCheckBoxEnable.setPreferredSize(new java.awt.Dimension(182, 22));
			ivjJCheckBoxEnable.setText("Enable Exclusion Settings");
			ivjJCheckBoxEnable.setMaximumSize(new java.awt.Dimension(182, 22));
			ivjJCheckBoxEnable.setMinimumSize(new java.awt.Dimension(182, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxEnable;
}
/**
 * Return the JLabelMinutes property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMinutes() {
	if (ivjJLabelMinutes == null) {
		try {
			ivjJLabelMinutes = new javax.swing.JLabel();
			ivjJLabelMinutes.setName("JLabelMinutes");
			ivjJLabelMinutes.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelMinutes.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJLabelMinutes.setText("min.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMinutes;
}
/**
 * Return the JLabelSeconds1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSeconds1() {
	if (ivjJLabelSeconds1 == null) {
		try {
			ivjJLabelSeconds1 = new javax.swing.JLabel();
			ivjJLabelSeconds1.setName("JLabelSeconds1");
			ivjJLabelSeconds1.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelSeconds1.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJLabelSeconds1.setText("sec.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSeconds1;
}
/**
 * Return the JLabelSeconds2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSeconds2() {
	if (ivjJLabelSeconds2 == null) {
		try {
			ivjJLabelSeconds2 = new javax.swing.JLabel();
			ivjJLabelSeconds2.setName("JLabelSeconds2");
			ivjJLabelSeconds2.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelSeconds2.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJLabelSeconds2.setText("sec.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSeconds2;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelTimeSettings() {
	if (ivjJPanelTimeSettings == null) {
		try {
			ivjJPanelTimeSettings = new javax.swing.JPanel();
			ivjJPanelTimeSettings.setName("JPanelTimeSettings");
			ivjJPanelTimeSettings.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsCycleTimeJTextField = new java.awt.GridBagConstraints();
			constraintsCycleTimeJTextField.gridx = 2; constraintsCycleTimeJTextField.gridy = 1;
			constraintsCycleTimeJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCycleTimeJTextField.weightx = 1.0;
			constraintsCycleTimeJTextField.insets = new java.awt.Insets(44, 11, 18, 4);
			getJPanelTimeSettings().add(getCycleTimeJTextField(), constraintsCycleTimeJTextField);

			java.awt.GridBagConstraints constraintsJLabelMinutes = new java.awt.GridBagConstraints();
			constraintsJLabelMinutes.gridx = 3; constraintsJLabelMinutes.gridy = 1;
			constraintsJLabelMinutes.ipadx = 21;
			constraintsJLabelMinutes.insets = new java.awt.Insets(46, 4, 20, 41);
			getJPanelTimeSettings().add(getJLabelMinutes(), constraintsJLabelMinutes);

			java.awt.GridBagConstraints constraintsCycleTimeJLabel = new java.awt.GridBagConstraints();
			constraintsCycleTimeJLabel.gridx = 1; constraintsCycleTimeJLabel.gridy = 1;
			constraintsCycleTimeJLabel.insets = new java.awt.Insets(47, 18, 21, 14);
			getJPanelTimeSettings().add(getCycleTimeJLabel(), constraintsCycleTimeJLabel);

			java.awt.GridBagConstraints constraintsOffsetJLabel = new java.awt.GridBagConstraints();
			constraintsOffsetJLabel.gridx = 1; constraintsOffsetJLabel.gridy = 2;
			constraintsOffsetJLabel.insets = new java.awt.Insets(21, 18, 21, 14);
			getJPanelTimeSettings().add(getOffsetJLabel(), constraintsOffsetJLabel);

			java.awt.GridBagConstraints constraintsJTextFieldOffset = new java.awt.GridBagConstraints();
			constraintsJTextFieldOffset.gridx = 2; constraintsJTextFieldOffset.gridy = 2;
			constraintsJTextFieldOffset.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldOffset.weightx = 1.0;
			constraintsJTextFieldOffset.insets = new java.awt.Insets(18, 11, 18, 4);
			getJPanelTimeSettings().add(getJTextFieldOffset(), constraintsJTextFieldOffset);

			java.awt.GridBagConstraints constraintsJLabelSeconds1 = new java.awt.GridBagConstraints();
			constraintsJLabelSeconds1.gridx = 3; constraintsJLabelSeconds1.gridy = 2;
			constraintsJLabelSeconds1.ipadx = 21;
			constraintsJLabelSeconds1.insets = new java.awt.Insets(20, 4, 20, 42);
			getJPanelTimeSettings().add(getJLabelSeconds1(), constraintsJLabelSeconds1);

			java.awt.GridBagConstraints constraintsJLabelSeconds2 = new java.awt.GridBagConstraints();
			constraintsJLabelSeconds2.gridx = 3; constraintsJLabelSeconds2.gridy = 3;
			constraintsJLabelSeconds2.ipadx = 21;
			constraintsJLabelSeconds2.insets = new java.awt.Insets(20, 4, 125, 42);
			getJPanelTimeSettings().add(getJLabelSeconds2(), constraintsJLabelSeconds2);

			java.awt.GridBagConstraints constraintsJTextFieldTransmitTime = new java.awt.GridBagConstraints();
			constraintsJTextFieldTransmitTime.gridx = 2; constraintsJTextFieldTransmitTime.gridy = 3;
			constraintsJTextFieldTransmitTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldTransmitTime.weightx = 1.0;
			constraintsJTextFieldTransmitTime.insets = new java.awt.Insets(18, 11, 123, 4);
			getJPanelTimeSettings().add(getJTextFieldTransmitTime(), constraintsJTextFieldTransmitTime);

			java.awt.GridBagConstraints constraintsTransmitDurationJLabel = new java.awt.GridBagConstraints();
			constraintsTransmitDurationJLabel.gridx = 1; constraintsTransmitDurationJLabel.gridy = 3;
			constraintsTransmitDurationJLabel.ipadx = 3;
			constraintsTransmitDurationJLabel.insets = new java.awt.Insets(21, 18, 126, 11);
			getJPanelTimeSettings().add(getTransmitDurationJLabel(), constraintsTransmitDurationJLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelTimeSettings;
}
/**
 * Return the JTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldOffset() {
	if (ivjJTextFieldOffset == null) {
		try {
			ivjJTextFieldOffset = new javax.swing.JTextField();
			ivjJTextFieldOffset.setName("JTextFieldOffset");
			ivjJTextFieldOffset.setPreferredSize(new java.awt.Dimension(71, 20));
			ivjJTextFieldOffset.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJTextFieldOffset.setText("");
			ivjJTextFieldOffset.setMinimumSize(new java.awt.Dimension(71, 20));
			// user code begin {1}
			ivjJTextFieldOffset.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(-9999999999L, 9999999999L) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldOffset;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldTransmitTime() {
	if (ivjJTextFieldTransmitTime == null) {
		try {
			ivjJTextFieldTransmitTime = new javax.swing.JTextField();
			ivjJTextFieldTransmitTime.setName("JTextFieldTransmitTime");
			ivjJTextFieldTransmitTime.setPreferredSize(new java.awt.Dimension(71, 20));
			ivjJTextFieldTransmitTime.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJTextFieldTransmitTime.setText("");
			ivjJTextFieldTransmitTime.setMinimumSize(new java.awt.Dimension(71, 20));
			// user code begin {1}
			ivjJTextFieldTransmitTime.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(-9999999999L, 9999999999L) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldTransmitTime;
}
/**
 * Return the OffsetJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOffsetJLabel() {
	if (ivjOffsetJLabel == null) {
		try {
			ivjOffsetJLabel = new javax.swing.JLabel();
			ivjOffsetJLabel.setName("OffsetJLabel");
			ivjOffsetJLabel.setText("Offset:");
			ivjOffsetJLabel.setMaximumSize(new java.awt.Dimension(147, 14));
			ivjOffsetJLabel.setPreferredSize(new java.awt.Dimension(147, 14));
			ivjOffsetJLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjOffsetJLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjOffsetJLabel.setMinimumSize(new java.awt.Dimension(147, 14));
			ivjOffsetJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOffsetJLabel;
}
/**
 * Return the ImpliedDurationJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTransmitDurationJLabel() {
	if (ivjTransmitDurationJLabel == null) {
		try {
			ivjTransmitDurationJLabel = new javax.swing.JLabel();
			ivjTransmitDurationJLabel.setName("TransmitDurationJLabel");
			ivjTransmitDurationJLabel.setText("Transmit Duration: ");
			ivjTransmitDurationJLabel.setMaximumSize(new java.awt.Dimension(147, 14));
			ivjTransmitDurationJLabel.setPreferredSize(new java.awt.Dimension(147, 14));
			ivjTransmitDurationJLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjTransmitDurationJLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjTransmitDurationJLabel.setMinimumSize(new java.awt.Dimension(147, 14));
			ivjTransmitDurationJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTransmitDurationJLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
	
	YukonPAObject pao = (YukonPAObject)o;

	if(getJCheckBoxEnable().isSelected())
	{
		//Add new exclusion timing information to the assigned PAOExclusion Vector
		Integer paoID = pao.getPAObjectID();
		Integer functionID = CtiUtilities.EXCLUSION_TIMING_FUNC_ID;
		String funcName = CtiUtilities.EXCLUSION_TIME_INFO;
	
		//CycleTime:#,Offset:#,TransmitTime:#,MaxTime:#
		StringBuffer exclusionTiming = new StringBuffer();
		exclusionTiming.append("CycleTime:");
		Integer cycleTime = new Integer(getCycleTimeJTextField().getText());
		cycleTime = new Integer(cycleTime.intValue() * 60);
		exclusionTiming.append(cycleTime);
		exclusionTiming.append(",Offset:" + getJTextFieldOffset().getText());
		exclusionTiming.append(",TransmitTime:" + getJTextFieldTransmitTime().getText());
	
		PAOExclusion paoExcl = new PAOExclusion(
			paoID, functionID, funcName, exclusionTiming.toString()
			);
		
		pao.getPAOExclusionVector().add( paoExcl );

	} 
	
	//if it isn't enabled, save the info but don't make it a "real" entry (append a comment symbol)
	else if(getCycleTimeJTextField().getText().compareTo("") != 0 &&
		getJTextFieldOffset().getText().compareTo("") != 0 &&
		getJTextFieldTransmitTime().getText().compareTo("") != 0)
	{
		//Add new exclusion timing information to the assigned PAOExclusion Vector
		Integer paoID = pao.getPAObjectID();
		Integer functionID = CtiUtilities.EXCLUSION_TIMING_FUNC_ID;
		String funcName = CtiUtilities.EXCLUSION_TIME_INFO;
	
	    //CycleTime:#,Offset:#,TransmitTime:#,MaxTime:#
		StringBuffer exclusionTiming = new StringBuffer();
		//appending the "#" to the string so that porter knows not to use it
		exclusionTiming.append("#CycleTime:");
		Integer cycleTime = new Integer(getCycleTimeJTextField().getText());
		cycleTime = new Integer(cycleTime.intValue() * 60);
		exclusionTiming.append(cycleTime);
		exclusionTiming.append(",Offset:" + getJTextFieldOffset().getText());
		exclusionTiming.append(",TransmitTime:" + getJTextFieldTransmitTime().getText());
	
	
		PAOExclusion paoExcl = new PAOExclusion(
			paoID, functionID, funcName, exclusionTiming.toString()
			);
		
		pao.getPAOExclusionVector().add( paoExcl );
	}
	return pao;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getCycleTimeJTextField().addCaretListener(ivjEventHandler);
	getJTextFieldOffset().addCaretListener(ivjEventHandler);
	getJTextFieldTransmitTime().addCaretListener(ivjEventHandler);
	getJCheckBoxEnable().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ProximityExclusionEditorPanel");
		setPreferredSize(new java.awt.Dimension(410, 360));
		setLayout(new java.awt.GridBagLayout());
		setSize(410, 360);
		setMinimumSize(new java.awt.Dimension(410, 360));
		setMaximumSize(new java.awt.Dimension(410, 360));

		java.awt.GridBagConstraints constraintsJCheckBoxEnable = new java.awt.GridBagConstraints();
		constraintsJCheckBoxEnable.gridx = 1; constraintsJCheckBoxEnable.gridy = 1;
		constraintsJCheckBoxEnable.insets = new java.awt.Insets(16, 22, 2, 206);
		add(getJCheckBoxEnable(), constraintsJCheckBoxEnable);

		java.awt.GridBagConstraints constraintsJPanelTimeSettings = new java.awt.GridBagConstraints();
		constraintsJPanelTimeSettings.gridx = 1; constraintsJPanelTimeSettings.gridy = 2;
		constraintsJPanelTimeSettings.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelTimeSettings.weightx = 1.0;
		constraintsJPanelTimeSettings.weighty = 1.0;
		constraintsJPanelTimeSettings.insets = new java.awt.Insets(3, 23, 18, 32);
		add(getJPanelTimeSettings(), constraintsJPanelTimeSettings);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	enableTimeFields(false);
	// user code end
}
public boolean isInputValid() 
{
	if(getCycleTimeJTextField().getText().compareTo("") == 0 && getJCheckBoxEnable().isSelected())
	{
		setErrorString( "Cycle Time has not been filled in." );
		return false;
	}
	
	if(getJTextFieldOffset().getText().compareTo("") == 0 && getJCheckBoxEnable().isSelected())
	{
		setErrorString( "Offset has not been filled in." );
		return false;
	}
	
	if(getJTextFieldTransmitTime().getText().compareTo("") == 0 && getJCheckBoxEnable().isSelected())
	{
		setErrorString( "Transmit Time has not been filled in." );
		return false;
	}
	
	if(getJCheckBoxEnable().isSelected())
	{
		
		Integer cycleTime = new Integer(getCycleTimeJTextField().getText());
		cycleTime = new Integer(cycleTime.intValue() * 60);
		Integer offset = new Integer(getJTextFieldOffset().getText());
		Integer transmitTime = new Integer(getJTextFieldTransmitTime().getText());
	
		if(offset.compareTo(cycleTime) > 0 || transmitTime.compareTo(cycleTime) > 0 )
		{
			setErrorString( "Offset and transmit times cannot be greater than the cycle time." );
			return false;
		}
	
	}
	return true;
}
/**
 * Comment
 */
public void jCheckBoxEnable_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	if(getJCheckBoxEnable().isSelected())
	{
		enableTimeFields(true);
		fireInputUpdate();
	}
	else
	{
		enableTimeFields(false);
		fireInputUpdate();
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
		ExclusionTimingEditorPanel aExclusionTimingEditorPanel;
		aExclusionTimingEditorPanel = new ExclusionTimingEditorPanel();
		frame.setContentPane(aExclusionTimingEditorPanel);
		frame.setSize(aExclusionTimingEditorPanel.getSize());
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
		exception.printStackTrace(System.out);
	}
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) 
{
	YukonPAObject pao = (YukonPAObject)o;

	Vector exclusionVector = pao.getPAOExclusionVector();
	PAOExclusion exclusionTimeInfo = null;
	   
	for(int j = 0; j < exclusionVector.size(); j++)
	{
		PAOExclusion temp = (PAOExclusion)exclusionVector.elementAt(j);
		if(temp.getFunctionID().compareTo(CtiUtilities.EXCLUSION_TIMING_FUNC_ID) == 0
			&& temp.getFuncName().compareTo(CtiUtilities.EXCLUSION_TIME_INFO) == 0)
			{
				exclusionTimeInfo = temp;
			}
	}
	if(exclusionTimeInfo != null)
	{
		StringTokenizer timeInfo = new StringTokenizer(exclusionTimeInfo.getFuncParams());
		
		if(timeInfo.nextToken(" CycleTime:,Offset:,TransmitTime:").compareTo("#") == 0)
		{	
			getJCheckBoxEnable().setSelected(false);
		}
		else
		{
			//start the tokenizer over or we've lost a token
			timeInfo = new StringTokenizer(exclusionTimeInfo.getFuncParams());
			getJCheckBoxEnable().doClick();
		}
		
		//CycleTime:#,Offset:#,TransmitTime:#,MaxTime:#
		Integer cycleTime = new Integer(timeInfo.nextToken("CycleTime:,"));
		String offset = timeInfo.nextToken("Offset:,");
		String transmitTime = timeInfo.nextToken("TransmitTime:,");
	
		cycleTime = new Integer(cycleTime.intValue() / 60);
		getCycleTimeJTextField().setText(cycleTime.toString());
		getJTextFieldOffset().setText(offset);
		getJTextFieldTransmitTime().setText(transmitTime);
	
	}
}
}
