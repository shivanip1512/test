package com.cannontech.esub.editor;

import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
/**
 * Creation date: (12/20/2001 5:40:03 PM)
 * @author: 
 */
public class TextPropertiesPanel extends JPanel {
	
	private static int DEFAULT_SELECTED_SIZE = 12;
	
	private Font[] availableFonts = 
		java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
	private int[] availableSizes = { 6, 8, 10, 12, 14, 16, 24, 32, 64 };
	
	private JColorChooser colorChooser = new JColorChooser();
	private JCheckBox ivjBoldCheckBox = null;
	private JButton ivjColorButton = null;
	private JLabel ivjColorLabel = null;
	private JLabel ivjExampleLabel = null;
	private JComboBox ivjFontComboBox = null;
	private JLabel ivjFontLabel = null;
	private JCheckBox ivjItalicCheckBox = null;
	private JPanel ivjJPanel1 = null;
	private JPanel ivjJPanel2 = null;
	private BoxLayout ivjJPanel2BoxLayout = null;
	private JPanel ivjJPanel3 = null;
	private JComboBox ivjSizeComboBox = null;
	private JLabel ivjSizeLabel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == TextPropertiesPanel.this.getColorButton()) 
				connEtoC5(e);
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == TextPropertiesPanel.this.getFontComboBox()) 
				connEtoC1(e);
			if (e.getSource() == TextPropertiesPanel.this.getSizeComboBox()) 
				connEtoC2(e);
			if (e.getSource() == TextPropertiesPanel.this.getItalicCheckBox()) 
				connEtoC3(e);
			if (e.getSource() == TextPropertiesPanel.this.getBoldCheckBox()) 
				connEtoC4(e);
		};
	};
/**
 * TextPropertiesPanel constructor comment.
 */
public TextPropertiesPanel() {
	super();
	initialize();
}
/**
 * TextPropertiesPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public TextPropertiesPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * TextPropertiesPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public TextPropertiesPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * TextPropertiesPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public TextPropertiesPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * connEtoC1:  (FontComboBox.item.itemStateChanged(java.awt.event.ItemEvent) --> TextPropertiesPanel.setExampleFont()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setExampleFont();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (SizeComboBox.item.itemStateChanged(java.awt.event.ItemEvent) --> TextPropertiesPanel.setExampleFont()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setExampleFont();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (ItalicCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> TextPropertiesPanel.setExampleFont()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setExampleFont();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (BoldCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> TextPropertiesPanel.setExampleFont()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setExampleFont();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (ColorButton.action.actionPerformed(java.awt.event.ActionEvent) --> TextPropertiesPanel.selectTextColor()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.selectTextColor();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the BoldCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getBoldCheckBox() {
	if (ivjBoldCheckBox == null) {
		try {
			ivjBoldCheckBox = new javax.swing.JCheckBox();
			ivjBoldCheckBox.setName("BoldCheckBox");
			ivjBoldCheckBox.setText("Bold");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBoldCheckBox;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GCF0F14ABGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DD8D4571536B12D5D0F3A36521624F6572E36258D6D3A3BEEBF3625DB324B571A46EEECCB9B3255C89292C9E2A2C908D2C573789BFE86E5E078D3D1D14070A78CBFE9ACB1C68593CCB121918DB626E1E49EB0BA4C10190720E25DF36E3D674D1B19B720EE2C5FF7FC6F1EFB6EB9675EFB6E39671EF78729F84F422C3474E2C9CA4B12443FD753A4E9C91FA4F93ACF2EA5DC65EF738C12665FDE00C2
	A9441289CF8B5425B4998DA6A935A6874ADDD02EBDE0B414413BD97A737638BF41CBB0B9D077F678C06974F256DDA5B9592C3CF9A49F1E5BG3A8167G0884B0C9A576F2011F8765ED629DA1ADDD12C6D6A24DAB2D659AFC2A20BB84F53DG792B1046E22F9075CBG07B44B6039F2A527CD06E7AE536E4E0E03026E5C97C225641F99533722AC8DFF023ED53C6D24F8D6CA7359C0E4C82DD7DBD0FE6F456143032E5126C12B5369F2AA7260C85370383DE9DC31BBBABA7A64C925576D9A135D0ADD7674DA1D3283
	5AA4B2DDBFE84DE1FCC0D7A8AB8FA36ED72DB8BE0D705E85F06EE1527797DB8F89FDC2AF06CA710B36248553B772E12E674A073D7A1E0BFDB6A3E4B572BFEB4F976DAA205C8740F5793A1DF4D9311AF4F92991E9F362738C15G8763897F1383086F057288402CF852717517C9477E2B0B241473CD6985C1F48C0B673AC547FBF5CC9CBD17391D594855EBB84F29A87711BCC3BBC0BF40A4C09840CEE61B3F1F2A07675173CA27EBF45465BCBAB6F64CE1F31FF459E4873CF7F4C00D403DA78F3A5CB6C96276B750
	5A28238F4112C7FCED8F01776DFD096CA5623D3BA5C529CE4F52695B52DFEFCD0F91BC629E71762D7CD19C37375AF05CD2G0F6D0CG9CFF1A614FBD6A457339FA085982162720AE7ED19A677EED24CBCF66DC4954E2C94F5651A56C3FEA523B9E63BA1CFB4C2B8B9F635357F10C67GFEB1C094C0ACC0B2C0F1820D712C293619467884583B5BE9F51C9ABFBFE6F5AB47466D8E1B6C96E37D11FD3A3156835EEE74265B71F189EF534E514AFFEB569D17156145691684BE9E5D895EF161323F99D476C8C220EDEC
	D36750435644D4821F438C95FF0D61C3B4F8BE37D98C0F6585D0A7BD1EE760749DCD0837BD6EEFA35D8CBF7BF1FF9BD9FE00ECE4B15465BC11E760737B0204FE508165B9G8B81A28162G1281F256509C5FBF55FC036B28D7B33F3C6D6CEB687C4141392DA71C9E456A9C144FD85DF66BF9076C11844D672F61381E77E977C5363E87C146DB32D3F6DB953B4B895C2D23B298130753FC75DAA15238E5D65BE79F15C1406818C4FC97DDB3E37F2C9E6574184D2A48DE8ADEFF4FB53409CB56B3409198602AD90363
	B541FCEEAD3CF72D21312ACCC75C82A8AFD547E57FDF0B6079C7B0EE993CF817FD9055303AAF130D1EEC038DD1DA8674C792E1EC81ACCF6699421F24B57AECBF59E257563B2446DFB7695AA2C275D3E29FFB4A5F96BFE0B2EC001F8D3098A086A061A95ACBD737766B70C3DD26DBAB2823BF4EA2F0589FFCE21F380C94DD9959B32CC7E310B53ACD58BCBE51FF72711F1B01B61C79B478FB00A3CF539CD87FDA49ED731855A313B53E6B86C312ED5E996134FF390EFB0DFCC9F6AADD72186C34410BC75802FCBD57
	EB27C3AE5506AEB2BC7A2DCEB0A40F0A7F78FAA147838F2D55D37DE5D6EFD594377D7C38A21F1295456E9C76367F945345636392BD222F73B2312F7D5047B100D9EADF73A433465EFADC2E0B63E3FD3812045D7E086D4F21C09F71B44D4D658398DB2DD04BF73032375EF8C0EF6EEE9610FFF0DFDD7D094848880267B20EB461BAA07F19A57039EA7DBB2CBE8974ACDF8B7683B082B0EFAD0D43AF190EFA6D7F3FFAFBF5FC1A4970672F378EEE85380DEC16025BF8E61A3E0D6725695B38A9EDFA9BAECF5333E1DE
	3707453F2E8927E6CDD427213F9CF4B91DB2DB18EA9309474A4F76E1BF6CCE3B723E5DB6ACAB276CD7E4C90ADF0B36BD177964C8F8DF3E167C67C495794F6AABDF1222BF4A550D37423ED11BDE7D8C374F034FF87DA75729A69D571A6C10C7E120BADD63CE05639B183F97F8CF86435DF96C82362DFD5305242367DD47DC130DF6C59EED427FCEA93045F40ED81D43322D760275123549BA21B43119E8BA817547712D2E7240014315BA81FF23C7D60ECF425E641011FDED5D19BA8B37953374304C6562F87C8D64
	36105CD75EF5B039A7942B43BE58B9A28FDE3C3D12DB395C43E7F9FF0F399C363F0154B66A6F4FDF9FE5F2BBDD8E177B583822381C0DD6E629CD7C51AB3B07DC6E51C039C7D9750C129DE077D843842068F60976377F4EE2BE9F6C5381B07B9972795FCA47FA7279E2E303768E3E0420AE7599DC9F73D89CC0B63F6099FEEECE04A72FAFEC572B6FFA166A3F28DB3FCD657F1FB354BF95341E77A3B443043EC27EF8DC069090B72DBF4A17A90E4BA8722BBBE3F50CFB6343C59968A39D1A7D0E633F1C293FF697E4
	629EF7D1653FB093138E1784CF6EEB16D81167185BBE8A6181EE60A2FF90EB57668F7AE1FCB057B005CFG5E769FDE43FDD86F54FEE89D72382309728B6150A69AA0DE7029D8A762B8C677E6B3514D017242F54168B62AF451D00E83C8D927C777A38B5135A76599FA120251FD5CC2F48E20198D30B80953A5A4F13A4C67106E9BEA6E21D945C5B15CA88C68DD99F426BAE95266C9EC1E3D3AFE98217BB9F156FC4E7F2C19E2C03ED3008FF90EECFFC716B65EB96A7138866DAC549773FFDF1656C7F35C7FE46541
	F3486E76403953A6CF128D45E6212EF89690E8E18777B43CAE851EED0E5B6C0A4BCD61E46784DB59FD2ECEB80EF40399F62ADB58993BBCA1D12E6627EDB4DE8B1F23BD6A5A1B5A1CCCD4C1E93AB148584409B1C9510C0D697EE7B3B86F6EF662FD70F93243976A50D6D9EE897D51E413E78276670E0E3719B172F607AF64083E0EBDCF391941D1DCB761D00E86088360674488F6FEC45AA440553EC03EE177A56D5A9EG7C9C0088G6EB74D4DE89FB769204578C76FA9E4EB5F4FA9CBE4DB7FE4BEA264AE7F5EDC
	6950758DE9FA7922B985E96951AF70F1CBFC41BBFE299DE1B9265742F2F87FDC4D543F76E470CF49543F1585FA7DDB1CCC63D2E521F61100CBD25B35B5E85B553E18E76885D0DE24F179BB4B2D0C8B177904AAF38E700BFE11E4EEEF54131972A269FAF78B354BDF8F7EFD3D6A7F07346D26560BBE2EA7DD53DADB61F9733B9D2D232B87388F775F62545CD82B56C7C44717847591116B797CADDF6F6FA3D66620ADA7E21F56130FF857D02871916F01F498DF58A154F818758F7682D1CE4BC639ECAF10A8AEDF5E
	F6B339A453864A89EC4F43B12D06729100FE00B100F99BC837B959B366460ECFCE139BBB59FEB3FAAD59C0E7194E8B7E6DE2B6F84FE7F19B824FEABCFF74A0738BEBB7707CD1360AFF0C61B3D33CF8EEEBF60647F285544DCAA1797F5A64AFBFA145D72674BB5FB2EF979D502E492FDC6D570E5B5E308347DB478FD230AC76768A277F5E0E6760C3403377F71463BEFD097C6C486FE81EDDC65CAF97C2B98AA096A08920F8E31E4182503711667B41EC3D3D44F73E03EDA45E7C819F0B7166DB9B43BB825A790F21
	3251379D6F3759C47D8E59C873D8E739B59DDE8DE8672FC34CC63DF9BCFCA658BC0AC30018472EE23DF9DC89BC13B652BCDEE820FDAFE79375E76DAD76E7630C7DB932C9EFCCFB9BC907FED50705AD37264377825A796BB0EF135E18961C5A2EBF267CFC236A5ABD043AE2EC923E096C7E1F7391978F6524CDE4636DB978CD097CED2F4B6ED4A472350F64E01C6DF25B076DCE42BD41F003AEB7CB8470BC55E3ACCE12D2A1AECDA53EFB730267ADEA73747396CDF97AF90B763C69F3F307720265B538FE36F6524F
	8240F1FFE6F1D49716D51C4541E2D6A80726527C2EE2BEB1924A4B8162D57C138C0FFBEEF2AA5F33B95EB5BADDEE8C7950FB3CF8DF1E9A683F038117B1E7DA9999FE74E4BBB7AAE39763F1DFE9819BF30FC776B0A757E5D52C22EE0DD2A26964FD5E310E4A921D3D1FEC4A95B44795D7E70BEA17FDCEDAC3651BA9FEEDAEE25F9621BC82B0EBB30DE57444F47BE5586640F19339DAE3F05B1BE554373D4F98F19DF8DC636EC1595F3EBEEFACD76B3C9E9EC3BC6C32B83B32760A55BDAC87586757D8FBDE972C7DB7
	0D41EC180FE7C433F6BCDBD5DBA8B7CDB7C691BAE344798DD8347C5A547919AA4E913C235579192AE75FB021EC5A826708AD2A2DB76849BE32054B6C5B92CCF6D50BD6F63B5A17530DFA7CC6841F29207C9E7461770A4A6F6490711B27E93BD83CC742F3B9C03C260E4F4552FC0C0BA655F2A4ABDF1648874BC7372973F95CE993CE131F27B7B2DF78ABE8706DB442DDE57EE7F33AA47DB00DF41E3A00FE54E13D6C9A073683AFA15D7C8B348FA603CE7900EB8768FE096B47E57F4B847A74A137F5944C635418BC
	A89D1270AE47DB8EEA9B8E74D1GAB81D281AA20DE482C40F1612749962147FC8D2ECD603E2341350ABD7A1E43FAFB74EC609D8640477B2C53FF7E16C09D3D47E85E89F8BC7AAD367E63209E47F92B0A7FBE4327EA70BCCEFD056131DC19E6B404GF07F7D6084796F949557ECA2DCF7BA697929E22D1EE1000FC8776A961D9ECC4FA4368EE25339BED9AAFE9343A7EA70DC4FCF575359AD956A86B2C8274F30D8A6864A31AAEECD8369992F629C0DC41719C9381FB7921DC9455958DE5F8E65DE95F7979B8B9B14
	959537D19D1FD9AAEE278BF1AB201C2862A20791178A6562AC23B8077E70825DA129855CGG374716810C2555F884CF159D9DEF396D36E356619EE65D5C2FE4DC4C916D4320AD77ABAD9711678AA8A782C886234831CC6E279CCB44B767CABD331AF56F6944771526179B78DC9D92CFBCDB460E1E4B64B2C79DA4F3926A1798F01DFC5521BDB7233D5FA771580B577F5C279EFDF21DCF7DCBBE75286F5E8CEDFD4B2836BECB531E776D7E9A6ADB6A7D8BA4473F6F8AFA87A9C57429A21BBF439BF6C8AB56DF4966
	FDFB6CB045F7FD59B41EDFA8E4778E203C98A0AA1B66F30C6D3931D0CED4F16113E48B79B934AE3EDB04EB7C9C149500900045GD1G31AACD9423D98B657CDCA34B05E21E33915EB16F48E9DEAB9A706B0BA2F8797791F91F7343470AF2088E3D96382CB3813232A47D6FFCC6A9701B63D68D2F69E40566625CBEBB51A54ABD39013AE5C85E6F0D8883C023G6C9478B9395CBF24297C5EB4913F15BA7C327D78A579714B4C737263F39A4676EE93602B73E81EBD25B4775DAAAE271C665E11C7733A3E9EF1D1D0
	0ED3F1E94CA7E1FE3AC71D5BB9751F44375FEDC296BE63723CFD67B23E7E0949505EBB50624CD23A993FA9A89088A601FC3084DC36D23B9167E5CBC379B4BCB97DEBA5DE7A04466968393C2BC3E8EBE3EEF9C8F63BE59B7F94AAADE56DC3197F1E0277B9C65A2B4FB77A6A7797F667125BCD61A459CD0AB150EEA89FCDF6131F8FEB9520C460AB733DF643E5353A308D8494765171D12699576BED36B7B4827D41FC526B01E1C6EB1D7423DD43768C9B500D65137D5C9B10371A89F83B87EE31DD4E451BED47776F
	8C877A4D851AF16178FC068F576079DE786AC55A8B17C2DD79D623B8DF9FADC3BB1802F298409200E5G09G195B0C62DC7C7AD60BB4C3CE294FB5568307970766284B7B67F65C5A9D39FB33E94FD97A03AFCB111FD915966C1BC579B6EECF874573C87D8E06677DDBDD0E7D9B013A59G61G51G31GEB557E5D393511778FBF399CD5946B60887B1AC31D438FACDEB486303CEF090729EFD281449DG3585DE5BBE846F3D853E364E67A80B45023682235F9D3C8206D7B4F8BEF7DFA924BBF83321AE31006C7A
	334E40F87C6B17E8DD77180CAC9E3741F392G4F7DFCAD7ED8DD0B73CC01EB116CA6CB48DC8CB411A63E96313C5C643F961F6844B9432FA6BD56736C5EB0473F3A8F713860FC7105E77285BDA5C745B7770AFCC16F0B7F4DE57C1E46327902914777959889AFB2C97CBB4373C55AF8A6CE74BD951E3C3E37D92F5ED4C875A98DFA7555D04F6318BD13148FB88838BDCC97C9E2799A76AF690DC09C7960F5BBA455469F49A79B2F38676F252E87D725D585317131C2BECF51055E18B1725A9FC44CB8E1A2FDD20268
	43E51F4530D70CDDC5815A4EF9171B9D7A396D5C2B66B4ADC53077824C82888540DC9127D9D0CAB491000F81D88BA019B98D372FBD65E4DF65E67D18C38B0DC0F3446C352FBE333FFD3DF49C5B795EG21BA9B3B272F3D25414776018F345FFACCF7E72593CFFFFD86040E8E33F7ECB90FE59FEA635E4A06C45D3B6CA99F1A53970A36B19A9E4BE1FE44BE385BBB6EF3A7306014A7AA558FDADDF6F0ABF87933767716464AD6CB9D59474289ED9F8E666D8C7A3DAAC5484DAF664F628FF7085C48B155D63B0A75EC
	036CF2559E6FBB7BC90450773B4353685B760A5017377B43BECDBF8F5FC8BB6EC7F351B50877A24D7B6B97BF2918AA787EFB66364A607B4B5BE541F3FBFFAA534F6D3DDF261FD77E30EC7A7BF0F2D9707BF00EB26DFDB83EBF66D6212F1B825B88830882D88110D0CCFB6346B21CD43CDD9D38AF9E378DE3EE91EFD4ABA4BF31BCF8DF57154FFC8FBB259CFD7DF9973873D164CF78AD65B8DF0AEB0CA1B9AEE3BB0E115BBEBC22F0D2014F5D0E766D1007981A1F9FDEE2E7CF2944E828A821B3C5D6811DBDACAAEE
	09194E9EFDAA2E3E044E9E13AAEEE4976216C1B93E04E2C7E391463F49EA39041573CB297C3542E03606EDBE99FB7EFED099416E2E5E8C70F8647A8445120D50B7DBA96D0BBF1D205C4502D2BA5F3F48E8E3213C96A0330CE813D43FD4D1F623FBA86DE35F2A16A40ACFDE988AF27F04C58ADCD61D19E4752972BF5BCC389095572CFEAB0BA923B92B1E54462F2900B715CFBFD7FCDDFD29867740EA20BD82E08318821887908E90853082A081A09520F8BB440EG07GFA8186G9400B95BE99D366F441C4C996B
	05405BC1009449AB36985DD67EBD0C6BBF6513EBDFG7C16EC1FDEFFBE47CF96D34C998374295BA9F736E6D71DB463EF496C0AC3BEA6C28B318F351450375D769DE49B51A5A843816559G8BF6D05EAE26D4BD2BGEE6D8E127DDE377E37D95874F95ACE484AAFA3D955BBC95669B2765BA5AC83046C24B96FAD27B9DF3CD3FFFF8C3EE7CE3DAC2973DDD70B6F625BC9C5CD3039822F79B6287E6E798BF8C745BA2C36BF426ED12D849DD2GAAAA0C06F600EE00018A3201C7ABEA273181BE84DE9360FC87D90EC4
	819E73D4BE0BF6B7CC43C75CA73C4C316C9624302537FC5642926097D3B13DADF1F905D5417704722A1977043DD56C0E9D78723E4BE3625B537EAA761DF7E848A32BFBCFC315C9451D70743ABCF67CE645631152B70267B21A5FE0B9A368CB820EB70BF35B8AA84EB56D6231F0ADBC8F816037D3CE731019E87AG6FG88858897B4BC96BEDAC231F074AE7D3316B6960E851A44DD5ED8B8F517FFACFCFD17FE5E63ABECBF4A598D3EE4B71D8FFFD409B6AFAE35B1FF484FB7EF861C0FD05FA33B49675DB96DB74C
	60FB4F7CDBEC377C265B717332FD927B57375BBF8762E678015D7EB910CD6A3E65003A95EAFFDF74316B76BDB05FFBEEA40F5BE456537B1CE8BB264303378B89389F03B0DB500785ED572E7B2DB1C250C76B3463314AEBEC2F0D5D434703630EB05CDA01636B675BB5415769121A195769FF546847EEBF2E890C5DE2EA74E3371F55E8E3B70EFB20867D73B06CF17DAADD2C0ABB2B629EE2BC91773E5D260C287855CCAF44F74BA810700FB33DA6982EDF601E52604ED25CDA033E863ABC62F2D35B649A0C6D066C
	8E0708DB990D5D892EC79D0F4DCCC77B18D523F77AFE421DD567E23EA9F0FE42CC5A7961733F2EF826F512D369EB8362376CB3365B2DEBEF3FAF3919F5D98D32797AEBE77B7741CA7FF579BA43F7D57A2F4B1F14503A6C053A251534AEFFD6FAEB7EA46E965B9516D13B723DB7960F73FE5D47E20B463D7E7D7DA5439F5C6B5F5F8F4A283FDDD0973E177AFB3ADC2BF77EBE2321F25F0D6C2FA76A022F5B5E3A19576D59BA7DF57BD1DD603A3555692F5B8BF5016BF6249EF5D44F2ABC1490742EFA5C0370F7D6F6
	2B43FE05DD29BF85044EE1EE7FFF5171D903BEBE0B47E02BB11491FB6346ED53649CAC0D15AD964CB908B9A82078369D4658310F6241C58594BBAE5EC73163E61617DD8965E4007CAA0AD3530AE99E2B81F72E0A6254A71AFC62D47EFD194743BEF16A09E2BAAF05D6112C8946F3B91493GD22BC8AFF78945294555B7FBDE5AFAC0D2476B35A66FF8A9053B269D2FD7F038042E570A685BEDCFB559ABE661E86DCCEA5E53A6EFFF5EE647ED15416DAD7FF2208D96DE565AA0479DE7674B59B0AE2155149F780E19
	F2868BD55C22E622CBD2F1199622CBD5F1FFEC24FB90BDB5EADEC23DC351DFC3F6E65D1F6FDDC3EC4B63780A492DDEBCEDF9629EDC341957B96CCE39137F32DD6D47C9E6E36140BF2A0666382354BB2F1D1A7765A537FF0E9F3F2DB2F81FEB59D9F1D98D0F1945373CB2EFBF63B46F56325B5F67AB37D5063823D39EE8579F1787469B5602DB0BB7DCB336530FB742E7EC2705389A0AB7BEB6335F8C54786F3FD798BE2546FF7F4DB0135F4E5CEFB40C6CA7DB4F1EB1CE521F3312DBEC774ECD37637DFA1A6F917B
	7D7B1B4C70F17B7D7BFBCD5D2792202E7BE56A6F04CFFC9781786817EF645C7369A2ED3BD851A6D1BCD34553D4EBB4D45772F73E5F1C733B3FF092BFAB0B7B81771A691CFC30B678B7C9BAA7AB62DE89FDAF6B29757619739EEB71725EE9A15E7DBA3C33E6606D8860BD3F554B7B270D44FBD207F72E9FEF7FFB8DA1357E677B5F9420BE8AE650D4F95F472B2E227E0DB29CC36B245D43576D0F8B48E707552A730AE100182BD91A77DC736D77DF7B7E86B21EAB7676E923667D0E625BAFFB41ED154167F4EEC920
	FF1EDF9298F75C45E23245B06FD13594A7BCDDC477AFD72838DEB596D92962BE47F0B9F5C6C3ED9D61D6B15CA1A88F54D15C793C1ABBD7G476DB192C388919776D828BEC22D7F6DB6EFFD0A1AD3CFD06B3760F1C55876DE7C1CA5ECFBC9911EC360B8B06C446FCC7D141FDB467AD4D1EFB4D86A09C7977E54CFD26A31FE58DCB94D188E54EBFD50C2B3AE88B8485B2F381C0A55F1142489D9BFB0D3CC9ED5CFF934E4F329CF9B762BEB11DDA49D8B54D8FEEDBD2D438DBE79210A86503F61C67C6B1FFCFE6B5385
	ED7AEF285DE61F7C10835A4C1A211DDEEE1B77F9F591FE84D64F851D2E51B11793FFF859F37C4D3E0F0E761CF86B1D13475F69D369E71BB17F51A9635F59226F57933276FEC3451E07585F72507BFE9DAA740CEE70FEBF774E59AF4C240BC879B71D427F695323272993B4B97E1ECA3F3F78C669395DEFAB8EDCEF66624075B6E289249B358452959F88245BFEC0CB47F373CEE6374950E7D32351508E5083B0GB0899082303811F276F30A264B7D734325B777AFFED3E21E66B7A5661973821FB36B67857E5E9C
	189788B56B67856EB68766857ED170F52BBF406178EF3033893F4FF4526A348E336FC77CEFBC7EEFD38C8CDC44B3D216A29978D590C54A44FB9FF0367138258255D4CF309CDC7C25360E0C4796E524CB19A7A4836F4389A99395B9A1E5B1BDCFC8995007C97CDBDD19DA70FCAC7D15D912AF56AD95859E130F4EFD6841080F3A0A5325F490B3A2F33ED993F898E31CF1FBBA9404A3E28D0226CA27245FA2B89F9BAE3A99E8D86822C8D9F87B2A831E7889811E86F60B0AA1144BE082B7ABBEG2BC859624303A2ED
	BDEABDF67E6C65BFDF29ECE9EDEB673E216E3A76EECB4A1D29E9416E369CEA66BE2137596B9B445F63EAC15B8A81FCF8B32D13DFD98E84DBA7C74FAB27C62D8E87984F4523CE9BFE07BBF6D9113DF7A76637A0FF8F5108CAE1476C77BA97B4ED7E8FD0CB878841AD35ED109BGG58D4GGD0CB818294G94G88G88GCF0F14AB41AD35ED109BGG58D4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG4A9BGGGG
**end of data**/
}
/**
 * Return the ColorButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getColorButton() {
	if (ivjColorButton == null) {
		try {
			ivjColorButton = new javax.swing.JButton();
			ivjColorButton.setName("ColorButton");
			ivjColorButton.setPreferredSize(new java.awt.Dimension(60, 20));
			ivjColorButton.setText("");
			ivjColorButton.setMinimumSize(new java.awt.Dimension(35, 20));
			ivjColorButton.setMaximumSize(new java.awt.Dimension(35, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjColorButton;
}
/**
 * Return the ColorLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getColorLabel() {
	if (ivjColorLabel == null) {
		try {
			ivjColorLabel = new javax.swing.JLabel();
			ivjColorLabel.setName("ColorLabel");
			ivjColorLabel.setText("Color:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjColorLabel;
}
/**
 * Return the ExampleLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getExampleLabel() {
	if (ivjExampleLabel == null) {
		try {
			ivjExampleLabel = new javax.swing.JLabel();
			ivjExampleLabel.setName("ExampleLabel");
			ivjExampleLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjExampleLabel.setText("AaBbYyZz 1234");
			ivjExampleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			ivjExampleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExampleLabel;
}
/**
 * Return the FontComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getFontComboBox() {
	if (ivjFontComboBox == null) {
		try {
			ivjFontComboBox = new javax.swing.JComboBox();
			ivjFontComboBox.setName("FontComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFontComboBox;
}
/**
 * Return the FontLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFontLabel() {
	if (ivjFontLabel == null) {
		try {
			ivjFontLabel = new javax.swing.JLabel();
			ivjFontLabel.setName("FontLabel");
			ivjFontLabel.setText("Font:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFontLabel;
}
/**
 * Return the ItalicCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getItalicCheckBox() {
	if (ivjItalicCheckBox == null) {
		try {
			ivjItalicCheckBox = new javax.swing.JCheckBox();
			ivjItalicCheckBox.setName("ItalicCheckBox");
			ivjItalicCheckBox.setText("Italic");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjItalicCheckBox;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsFontLabel = new java.awt.GridBagConstraints();
			constraintsFontLabel.gridx = 0; constraintsFontLabel.gridy = 0;
			constraintsFontLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsFontLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getFontLabel(), constraintsFontLabel);

			java.awt.GridBagConstraints constraintsSizeLabel = new java.awt.GridBagConstraints();
			constraintsSizeLabel.gridx = 2; constraintsSizeLabel.gridy = 0;
			constraintsSizeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsSizeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getSizeLabel(), constraintsSizeLabel);

			java.awt.GridBagConstraints constraintsFontComboBox = new java.awt.GridBagConstraints();
			constraintsFontComboBox.gridx = 1; constraintsFontComboBox.gridy = 0;
			constraintsFontComboBox.anchor = java.awt.GridBagConstraints.EAST;
			constraintsFontComboBox.weightx = 1.0;
			constraintsFontComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getFontComboBox(), constraintsFontComboBox);

			java.awt.GridBagConstraints constraintsSizeComboBox = new java.awt.GridBagConstraints();
			constraintsSizeComboBox.gridx = 3; constraintsSizeComboBox.gridy = 0;
			constraintsSizeComboBox.anchor = java.awt.GridBagConstraints.EAST;
			constraintsSizeComboBox.weightx = 1.0;
			constraintsSizeComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getSizeComboBox(), constraintsSizeComboBox);

			java.awt.GridBagConstraints constraintsColorLabel = new java.awt.GridBagConstraints();
			constraintsColorLabel.gridx = 4; constraintsColorLabel.gridy = 0;
			constraintsColorLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getColorLabel(), constraintsColorLabel);

			java.awt.GridBagConstraints constraintsColorButton = new java.awt.GridBagConstraints();
			constraintsColorButton.gridx = 5; constraintsColorButton.gridy = 0;
			constraintsColorButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getColorButton(), constraintsColorButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}
/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitle("Style");
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setBorder(ivjLocalBorder1);
			ivjJPanel2.setLayout(getJPanel2BoxLayout());
			getJPanel2().add(getItalicCheckBox(), getItalicCheckBox().getName());
			getJPanel2().add(getBoldCheckBox(), getBoldCheckBox().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}
/**
 * Return the JPanel2BoxLayout property value.
 * @return javax.swing.BoxLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.BoxLayout getJPanel2BoxLayout() {
	javax.swing.BoxLayout ivjJPanel2BoxLayout = null;
	try {
		/* Create part */
		ivjJPanel2BoxLayout = new javax.swing.BoxLayout(getJPanel2(), javax.swing.BoxLayout.Y_AXIS);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel2BoxLayout;
}
/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel3() {
	if (ivjJPanel3 == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitle("Example");
			ivjJPanel3 = new javax.swing.JPanel();
			ivjJPanel3.setName("JPanel3");
			ivjJPanel3.setPreferredSize(new java.awt.Dimension(0, 100));
			ivjJPanel3.setBorder(ivjLocalBorder);
			ivjJPanel3.setLayout(new java.awt.BorderLayout());
			ivjJPanel3.setMinimumSize(new java.awt.Dimension(0, 50));
			getJPanel3().add(getExampleLabel(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel3;
}
/**
 * Creation date: (12/20/2001 4:12:16 PM)
 * @return java.awt.Font
 */
public Font getSelectedFont() {
	String fontName = getFontComboBox().getSelectedItem().toString();
	int fontSize = ((Integer) getSizeComboBox().getSelectedItem()).intValue();
	int fontStyle = 0;

	if( getItalicCheckBox().isSelected() )
		fontStyle += Font.ITALIC;

	if( getBoldCheckBox().isSelected() )
		fontStyle += Font.BOLD;

	if( fontStyle == 0 )
		fontStyle = Font.PLAIN;

	return  new Font(fontName, fontStyle, fontSize);
}
/**
 * Return the SizeComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getSizeComboBox() {
	if (ivjSizeComboBox == null) {
		try {
			ivjSizeComboBox = new javax.swing.JComboBox();
			ivjSizeComboBox.setName("SizeComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSizeComboBox;
}
/**
 * Return the SizeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSizeLabel() {
	if (ivjSizeLabel == null) {
		try {
			ivjSizeLabel = new javax.swing.JLabel();
			ivjSizeLabel.setName("SizeLabel");
			ivjSizeLabel.setText("Size:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSizeLabel;
}
/**
 * Creation date: (12/20/2001 5:47:49 PM)
 * @return java.awt.Color
 */
public java.awt.Color getTextColor() {
	return colorChooser.getColor();
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
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
	getFontComboBox().addItemListener(ivjEventHandler);
	getSizeComboBox().addItemListener(ivjEventHandler);
	getItalicCheckBox().addItemListener(ivjEventHandler);
	getBoldCheckBox().addItemListener(ivjEventHandler);
	getColorButton().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("TextPropertiesPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(400, 100);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 0;
		constraintsJPanel1.gridwidth = 2;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsJPanel3 = new java.awt.GridBagConstraints();
		constraintsJPanel3.gridx = 0; constraintsJPanel3.gridy = 1;
		constraintsJPanel3.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel3.weightx = 1.0;
		constraintsJPanel3.weighty = 1.0;
		constraintsJPanel3.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel3(), constraintsJPanel3);

		java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
		constraintsJPanel2.gridx = 1; constraintsJPanel2.gridy = 1;
		constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel2(), constraintsJPanel2);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	for( int i = 0; i < availableFonts.length; i++ ) {
			getFontComboBox().addItem(availableFonts[i].getFontName());
		}

	for( int i = 0; i < availableSizes.length; i++ ) {
		getSizeComboBox().addItem(new Integer(availableSizes[i]));
	}

	getSizeComboBox().setSelectedItem(new Integer(DEFAULT_SELECTED_SIZE));

	getColorButton().setBackground(colorChooser.getColor());
			
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		JFrame frame = new javax.swing.JFrame();
		TextPropertiesPanel aTextPropertiesPanel;
		aTextPropertiesPanel = new TextPropertiesPanel();
		frame.setContentPane(aTextPropertiesPanel);
		frame.setSize(aTextPropertiesPanel.getSize());
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
 * Creation date: (12/20/2001 5:48:53 PM)
 */
private void selectTextColor() {
	
	JDialog d = JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
		new java.awt.event.ActionListener() { //ok listener
			public void actionPerformed(java.awt.event.ActionEvent e) {
				getColorButton().setBackground(getTextColor());
				setExampleFont();
			}
		},
		new java.awt.event.ActionListener() { //cancel listener
			public void actionPerformed(java.awt.event.ActionEvent e) {
			}
		}
			);

	d.show();
	d.dispose();
}
/**
 * Creation date: (12/20/2001 4:08:42 PM)
 */
private void setExampleFont() {	
	getExampleLabel().setFont( getSelectedFont());
	getExampleLabel().setForeground(getTextColor());
}
/**
 * Creation date: (12/20/2001 5:47:49 PM)
 * @param newTextColor java.awt.Color
 */
public void setTextColor(java.awt.Color newTextColor) {
	colorChooser.setColor(newTextColor);
	getColorButton().setBackground(newTextColor);
	setExampleFont();
}
/**
 * Creation date: (12/20/2001 5:59:33 PM)
 * @param f java.awt.Font
 */
public void setTextFont(Font f) {
	
	for( int i = 0; i < getFontComboBox().getItemCount(); i++ ) {
		if( getFontComboBox().getItemAt(i).toString().equalsIgnoreCase(f.getFontName()) ) {
			getFontComboBox().setSelectedIndex(i);
		}
	}

	boolean foundSize = false;
	for( int i = 0; i < getSizeComboBox().getItemCount(); i++ ) {
		if( ((Integer) getSizeComboBox().getItemAt(i)).intValue() == f.getSize() ) {
			getSizeComboBox().setSelectedIndex(i);
			foundSize = true;
		}
	}

	if( !foundSize ) {
		getSizeComboBox().addItem(new Integer(f.getSize()));
	}
		
	if( f.isBold() ) {
		getBoldCheckBox().setSelected(true);
	}

	if( f.isItalic() ) {
		getItalicCheckBox().setSelected(true);
	}
}
}
