package com.cannontech.esub.editor.element;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

import javax.swing.JColorChooser;

import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.esub.editor.Util;
import com.cannontech.esub.element.AlarmTextElement;

/**
 * Insert the type's description here.
 * Creation date: (5/8/2003 5:22:35 PM)
 * @author: 
 */
public class AlarmTextElementEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.TreeSelectionListener {
	private javax.swing.JLabel ivjAlarmColorLabel = null;
	private javax.swing.JButton ivjDefaultColorButton = null;
	private javax.swing.JLabel ivjDefaultColorLabel = null;
	private javax.swing.JComboBox ivjDefaultFontComboBox = null;
	private javax.swing.JLabel ivjDefaultFontLabel = null;
	private javax.swing.JTextField ivjDefaultTextTextField = null;
	private LinkToPanel ivjLinkToPanel = null;
	private PointSelectionPanel ivjPointSelectionPanel = null;
	private javax.swing.JPanel ivjTextPanel = null;
	private javax.swing.JLabel ivjTextLabel = null;
	private javax.swing.JComboBox ivjTextSizeComboBox = null;
	private javax.swing.JLabel ivjTextSizeLabel = null;
	private javax.swing.JButton ivjAlarmColorButton = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	
	private JColorChooser colorChooser;
	private static final int[] availableFontSizes = {
		6,8,9,10,11,12,14,18,24,36,48,60,72,84,96
	};
	
	private AlarmTextElement alarmTextElement;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == AlarmTextElementEditorPanel.this.getDefaultColorButton()) 
				connEtoC1();
			if (e.getSource() == AlarmTextElementEditorPanel.this.getAlarmColorButton()) 
				connEtoC2();
		};
	};
/**
 * AlarmTextElementEditorPanel constructor comment.
 */
public AlarmTextElementEditorPanel() {
	super();
	initialize();
}
/**
 * Comment
 */
public void alarmColorButton_ActionEvents() {
		javax.swing.JDialog d = javax.swing.JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
		new java.awt.event.ActionListener() { //ok listener
			public void actionPerformed(java.awt.event.ActionEvent e) {
				getAlarmColorButton().setBackground(colorChooser.getColor());				
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
 * Comment
 */
public void alarmColorButton_ActionEvents1() {
		javax.swing.JDialog d = javax.swing.JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
		new java.awt.event.ActionListener() { //ok listener
			public void actionPerformed(java.awt.event.ActionEvent e) {
				getAlarmColorButton().setBackground(colorChooser.getColor());				
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
 * Comment
 */
public void alarmColorButton_ActionEvents2() {
	javax.swing.JDialog d = JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
		new java.awt.event.ActionListener() { //ok listener
			public void actionPerformed(java.awt.event.ActionEvent e) {
				getAlarmColorButton().setBackground(colorChooser.getColor());				
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
 * connEtoC1:  (DefaultColorButton.action. --> AlarmTextElementEditorPanel.defaultColorButton_ActionEvents()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.defaultColorButton_ActionEvents();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (AlarmColorButton.action. --> AlarmTextElementEditorPanel.alarmColorButton_ActionEvents2()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2() {
	try {
		// user code begin {1}
		// user code end
		this.alarmColorButton_ActionEvents2();
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
public void defaultColorButton_ActionEvents() {
	javax.swing.JDialog d = JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
		new java.awt.event.ActionListener() { //ok listener
			public void actionPerformed(java.awt.event.ActionEvent e) {
				getDefaultColorButton().setBackground(colorChooser.getColor());				
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
 * Comment
 */
public void defaultColorButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	javax.swing.JDialog d = javax.swing.JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
		new java.awt.event.ActionListener() { //ok listener
			public void actionPerformed(java.awt.event.ActionEvent e) {
				getDefaultColorButton().setBackground(colorChooser.getColor());				
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
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getAlarmColorButton() {
	if (ivjAlarmColorButton == null) {
		try {
			ivjAlarmColorButton = new javax.swing.JButton();
			ivjAlarmColorButton.setName("AlarmColorButton");
			ivjAlarmColorButton.setPreferredSize(new java.awt.Dimension(65, 22));
			ivjAlarmColorButton.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAlarmColorButton;
}
/**
 * Return the AlarmColorLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAlarmColorLabel() {
	if (ivjAlarmColorLabel == null) {
		try {
			ivjAlarmColorLabel = new javax.swing.JLabel();
			ivjAlarmColorLabel.setName("AlarmColorLabel");
			ivjAlarmColorLabel.setText("Alarm Color:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAlarmColorLabel;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD5D330AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8DD814D53A5E566D197B646E25DDFA2E994FC6E5C5CFF8577C0B0A924D9FCA3ADAE2EAE1E25215DC7C49F457F65D5B878309850A8206D7C3D2EBE1A3C0652E04287C0C8A0902423551CC78187986A707991A7990F42D6EFB4E795E73FDB3738DBF7568537B4C1C776F3C671C773C67FD4F9992F2F60726A5276488C2F21A207CBB14AC88D9A784E1673D41B39017BE56E094B47FBE8658AAAC79A695
	BE7300D63D4EE04C92FA24F4E897C33B7CED0371C3781EAD9C79F2C3ADFC8950CFAC5032AD6FD5775F4F0A23584FC65AFE38B283BEB78314812E95A0F4AC69FFF2E51E026F0376E665BB016494C1E8FF0C703C5C112741A7A8FC65C02B87C89DC7F8DE295C255043GC7F82260B376B1461B841F1B28F53F2C2CD078AE5D99AA2CFD7E4314CC5217C6BFC263B8A65B2DFC9688775209B08A376DA553D355FC3573284555D5E2911DCE17D316ACD7CAA4CFCFC709E4354BAEF70964103AA427DCD9D963905DDD8DCE
	2B5DA2827EB825B60BCE49A1887154768D9D69D4AF588EED47F80213ADE43E764177AA005671E87F0B19D50AFD41D7C30578875E8FE87F58714C6E58712A7D667A6D9943A7907D4643990ADC8E349B8118AD278E23AD51935016BF3491DEE122411887D0B69171635A893E9E5A6DGFD935146A3FFC39B4D7F7C3D303E23B2E5CBG9B03A6B25BA2A62AB68E5F3BBDE38775196B55E45D5712FEA7990C45G4DG5DGC1GBB292F169F7BBBFC56F448F52E2EAE1733263B3B56E1F51FF6D9A587FC07496F10955C
	B94962F2DB850179E33225D8479E0430C95E3EC8000D6DAB817DA77C5CC302AC172524690CED423C9DA9110A0EB913543125BECE66AD112E6DEA4093B9A24F70EFD3FC7D63AA1E2D558D0AA76DCBC00BFA9C67593C99EDE9CC9DA5E46D2FCA5928E34B48297BD376C5B09B2AA2D4DB589C375412B93E827861GA1G93GE28156C6609C7FF06C60C0F35CG7E6FF60A0E161E0EEE51AD5776589DD6492D4C7526437D4D359EB0398F07AC4766A56C20F60D62469F561D17383002142CA758FC94BE214E8B6B3BB9
	E05F75CF787B46E63E06EDF4CFDCFA022D2111636D946F5060595A3EC57796E95F84DABCG637FD4A4F853133EBE321F624DCF7A7AC8C4BB7A0883E8F3G587A3ED5C762E279D330F7G2EG8C83888508841873942E71F347CA87390F1AB56B4BE4EF2B5E8E1FD21F6C969B1C9ED9F4DA2473225BAEF6B8A40F20705CD1CD663543CBAE086EEF8B74F1D2F2CAEED1363B1C20DD6C124019BC0C676E6A2D0447ADD1EA2B3DCB028E3A3A8554FBDFF5B6993368117F2F5BAA4A124A41680FD4931F38A61E870D2000
	2CF1A4192FBC8B09F32BE17C9911B8D77947894E8A6D1B11B8AF7BEA364167A9F0EE49D1D9E932DB0899227BBA7A68CA2AA78878B7C4623EDCE9C67FB3E5BEA8944F2B5475BF82562719AF5DFC5A577F5E21FAC783FE5A53F8068E3318B532243F0176C350A0F83C775C5DDEFD248DC24EB8047D4966F94489622B89B0AE6199033160991C6B361A8266033522C7C22FEBF20343C8D6F566996F25DAF226C8576004B5C95D12538ADFBC4A1ADBEAB7297EE8F3F1DF31D3BC09CFF560B09E0EF755EEE5F8502175EE
	2477D23AA84BEEFBC70FAC35C832ECF7F62A72BFD2DBBCDE214F230C7557A749D8ABE00C4DG6DFC2C69F5599A3FEAF4392E76F437129D2378E7A7BD07E5609F718C2E3F54CEF22A313C6D22ED157E27B633C29FCA4C9C1AFFE4351B073866C36F23D8A4E347B849F007A8EE810CCD180C63FD065A12FC0B6CE1FD4C67639D4CDE98AAB0DF78CBDDE0DFCEB1697BF23AC95F17B71B7A77556DA6BDDFE5340FCCE40C2EDE2746777718C87C33381CCE09EEC0AEA230DCF8E5AB99075DE9173F32DBBBA53945FEC392
	0466494407CBE98CAB046FD513B1B6766E4558386F46FDC2D4DB2EEE7EB47241721460494C0F43A62B3111593477B859D3CAFEDD676AF14A8CDFC46337027798A9EE7E57EDC43650A459449E87F0BBDC6E5A9ED9F6B90BC5BA2012024FBFBFDA90B0C7AF367A893655D04163E43DBC05C5670B4A845657138517E8DFD45950FB92FD443C7AB9C0BA5A4FE24A14FAB27EE81817B8003513B126BCF81C50B126A8019394BA5852814D91C55665B3EB0EE62EB322D8BDE606CFC68F5725CF0842FEFEF3A20B781074DD
	0F84C7BD89DA27246BF8DE1F5866C3BBAFBAFA54BC603E939F90BBB4710E6103CF6A2F692813A446DD657AFFF772BD011C67CCA7734131A251596D36F74171C08238D2B77EF0D8DBB7C6037D2446CC1FE2B06E82E03257EE10B96B27C49346CE21791B0875E5B9349B819CD3183EE1D318FFB23EBD9D4837815AB9D3D53E72290C4FBA15701D30E01DD54AF15FD0DC9D841ED5E9184B1E4E52562BD64F673A7B056E93C5FF64D45F9C7F0F8DC4EF9C601326629A6655EB4F1F9A0F47E5315394CBD90F19755A3D44
	F0B1756F43274D6E76C03EEF157AF0CDDF2AA736129CCCC1AB6B7268E7C7847F1C124DAE9E6FF53D543FDBDDF510865603DB5471D0F35EAE758AD8A3F7D8F13E04E7B116FCFFC2DB8BC7EE591372C1003929F8164D0979D9F5EE22204EE43A17F25DA31FC53F383B1254BCFAB53E058E26243347DE52A35BC9D2A84A901B3BFBE466B1CCE7A2BD3F092EB94FE23D3C7B98717174E91073GD4G305C7DB7A2666E4D00B3CC43FDB8ACDF3B8F23G1F8810B15DE0E471669D111CD1CAF0D166BC2BE88F7127810396
	7A577D59550A7E187F98A5D47DE8542D558DDBB2D38A2633392B1F2E4E6158978463B4GB60ED39DB80EE06009190E63F84621B70E5499B8576F1DA4F7C68306BF0157608734FBA2AA7E6300FBA2EF863335F8066F1EF86594D91FFA40DF1A01FB62F443D64D1EB887DEA8BABBA1C271F33A4148E21152FE37A18DE3110079C227F5A8B5EB778C2CBDBEBCCD66E8B834C381A2G66G2C66365D7E6500B578713EFEEA7044A15915B593F32C95FEF5703E19EAFED9B153BF57E4F56AEF292FB74EE4F56AC60E3F17
	622D9ABC731DCFA81E34EF82AD1E77BFD2746D5F946D5DE73BCF1B49BD64A7D762539E6EA347FCF1CB4B8E5C775F0A57443E92CFAF64CF1515DFB2279470CEF20DC5FB3604C433BCBD8ABEE32371AE6D717CF4451E24E8DC672EB364FC58771C41D88DE0861081C682CCGD8709C2E773CAF75E22777FA878A1CEA7D426662EC47CF1B43B6BFB95FB9DC7F1C379C9B77BA870EBB6BF99C7718A63A5F20EDFE9E47F860D426E63FB53B6CCE196F35452748B96FF25BBB6DCE44A5D01C45652689AA2B13FE20317B8A
	689C4E756EBE9DB81F7E7434FEBEDDFCDABF1FBEF03A7F5A3072F420FC1B5977AE37EFA4G4325D691DCB83423B8AE698841A5C0FB4373687BF753BDD1B08B4E8A0066D908FF10623BE731B89A4C711F9D53566EFAC0F4114F66D9032B91ABB4FC2C8F5300FD98B57C0369C3DBD7FDC4651F5B3E054E3947A3F928131393D621AD13F3851DFA640C58A5A9FDE78B3345CD8A4FF159D5371F6F6D370B727477B6BD38196F7E5D0A67F644ACBC37CF1F23773550CE0F01750A41790E4C477DD191234ED323723DBD46
	FF0E157B04B3017D7307B37A7EB97C8C59AB9ED70F5BA2797A609DE772B8CD0D828D3254C526C5492F293CAC3A21C673153F074AB3DAA07951E78279B91BF3C307F64E8FF0FF79338367C8564C07C1799E92F00E46B51173210F37A75176F53E16D24DE6EE73F127D5899EAC0F3E0B2E61A6E07CA099F15B69BB97F940A10A184D1FD611F862902F3BFAC0F6578B046FC895467531E0DBB440EA005497D8FCE7FDBF56CB72CC1BDB6C02A9E86916AC42FAAAEBBC0A32D6E877810C8408GD8708266EC7B5B09EC23
	5DF935554572C5061FC36FA9DBE161316EE07869FDA4CF2743EB1114C20EF130F3E841B16DB9D47D1F4C4EE170998C40F8261D517204ABBCEB61B3FDB67AF229DFFED0B0DBDD1B7259014E79F5F4EF544FE667F9AE472737907CCD8D1E1D73AD141F3447826D529C0CFFF369D9197E2241386FC5446DA9A5388AE857F35CAFCAB1CE36F25C56AE149D4DF191EDA89B41F1BB5AD1F65A0B18FBCBD57C9D6CA5286BDE42B55E5F3ECB9BABCA44DE393272245BEE2D95BB9B294730F8B1268A6F2A063F047162D52AB3
	925A31GC9AFE13D5277A956AB618F0D929A079D56FD3B0BEF5D1312BAD7F917182B662D5D3CDEA9BA0AFDDA019EB217603EBA2A4D31096F4CCCE567B5237F2B971D6C654F84AD7DA9AFBA3177E123163E420BCE6628C811E7E31B75890EED432CB041B131A8603BE402B22674D8E50C39BB05AB9FA7AB73DEAC168BC3F8DB55ECA4B6B7D955983F2BE3F1BD0E517733BEE887810445629AAFED2F9ACADFBA1BD0F93FBD0375E3946F33023E6B2C07F656BC0331F09E7A60899A1B2A215D4AF153A5744B3EF9389F
	7FF22E90BE23211D88A03CECB06E822882B08348G41G93G663C0CB21BDB08CC9234B3669B0C45G0DG8E0091G6173593DC54CFCF6AF41E4AE1C8B75D99F229B3F6FFAF9F0F98231D15B0E1E97D80E0CC1D06C76251915B1E1BBD8794EEC9D6EE76B969F1F9DEA3E214DD7746083256FDC9D9A716FEC1FBD057A32056579045E6D16EC125BADD959352830EF81BDA7F61375CE02F5D8BF9F4F0517ED641ECB0678FFE58179BD451FC839AB2C257C23CACD0A6E70857EF306F753C674C1600985582B34939730
	981C4C7B2F247132EAA144D5G565F7F18557E4A5A31BFF921FEFF9B7B694F30D06D0F2D5B552A1F767EFB7DA74A0D9B329CBB33469CA1FBB6D8E3BF43CF2278D08D1E1D9DCB7859918E342CD73056792B1966D3508E8288830882088758708A56A8071B4B04816A3BD6D7F7A3A4098ECD594146D7F274273D0BCF5908B1FA4213778B91373F199C683EAC6BD5366EE54AE7D476CE0AE7638B3AC846578E341BG21G91GB1G892F6278FE51DC4C46C72E7BEAE4D934DC21B709B8B8F2392722C992454616DB0D
	E3DB7F2A3F6FE544854AC3465398DF90673BF61194DF9C673BF647AD38F695C08B0943F8185C545F5C8A0B82759FCE63FD4EA25F7EA7D2FC41A25F7E0F36E17F45C08BDA04F1609B2BFFCEAADBB1FFDB3B086524992F4159719A2B7F581E0D13F04FD63C66EFA33E11A1B482CFBBC02E5216DFD36DE3F6745EA4673D72EAC252D282A76C12434A681B1BB0DFED94BBB4796A7232ACD56E849C27144CE8F7FC1E6DCD835F6FF0553A7A9459393D190ACE92AE3D75BE664C5250D01051E253362AFA698D2237ECC95F
	B69FBADE66B07A61B4F20E282FB6CC1A511EBE1C65C5E3126CDE333EC51B938DD356A36CB53C77F4583474C8059E4F69C7CB7568EBB9FD46C7DAFA7AE2C6AFDB0C744C8ABDF993273B5A75644D1C3E7E9FFA728ECE8F4A51238FDB0CE7C6B0F96ED266694352FC78F4CA3D8518AE17186C3035490FAE8A7F713F4585878E94E19C8A6D555E5FD63C7F37007737410B15B7C065B37844CEC5476C2F35BA32C6EDCDC98E20A3DE11CDDA2C6652ACEF1BD50A39E2C13C4138DBE067F93A007FD69F534331315FF51934
	18ADCBB2B02E6EFA749E41B47F4D643D81ECB147AB6309D7EDB1BC1F1D41F43E5B264E671836414CA79B07580E63080F473559A392DDAC260DBF22FE0F54FCDFD6B550DD4E4F815607F064D676414E1A77AE843ECF493C20FF1F12FDC17F3EEF47057E5F4FBF3E90787D3C7002767D1C1D15EBC432077216980C65GE6G874030A5F8CECE3AC05EEB482F2B7C4F486356CEF21FC3FED1A5E37FF7DF8CBC56500B837F8E6B110B648D2F4385D12C0B68C77C6FAF127512DD5D9449F093AE12B9F25BBB2F480CD541
	BFF911781BC332D1B42BE10AE95D9582630AD802F54DA3E7312E09D902676B9396557F26EA3E1F325CFADF345E52BE145F15B56118F73D0EE39E57260E73A94DF7D35B2D9F73B737348F164B04D8B117F14018C73F0E6F9F89D63C2708FB9D6B62ED924D1316C2FD8AE0DA0A3C19B60CE117166A45E2C1504331D8B70A7A912B01BEA9743A37D16FF8F97E427A7B3185EB766025F89F7036AD407DB0CD88185C835C4E98EEFB8FBFC7D293D0D727E3273EAE1E73B0593FFE04F6D4A520BED3856A13B96EA19E6343
	B92E74906292B8EEE68E62F63D017548C93A8E2D506EFE83634E42CB647E673C782D7F2BBCA01B5D2EEE49ADDFE7D87A8B8EA51E1DA6E1C009833B3F79A4108E08DEDF42301BBA3E15ACF22B58497DF1CE29B63F8E82FBC63F517F9D836B374FC6725B88600D85D88F1075DF8663BE00AA00E6GABC09FC090C098C0A4409C00840035G694BE0CFAE43713FF239281F71AB97FDB4783AC576AE456C3735EB6DAF83FD26E57D5B4F7A7B3398B8EEA7F78C9C375F27FFA3A0C13CED3D5E2D5C49EF6920EFE4B61BC7
	6267C3CEC79647B5F81ADD9EBB392CE2B9C5DDF560BB09CBB4FE1BE1AC56E5D8D3C41D451AA2C8E70C5AFA0140E86009D8265694B14B541A0269ABEDC2FD8B8268535EE3A4810F10286A4BC9743DD738FFC8EF5AEACC9C7B93655E9C329C2B81EF27B12FA05137B67CB70AAFCE742D8D73A4DE1B82ADA4914F11EDB6ED7F421B86E34E1B7D7B1EF2EF56521F5D05BABA18DC18CDCF2ECA61EF8EA8F723D4CF4E2A70778514CB496F4F4E9181659AAA7464C2957E8981659E65FBB9DA07A7AE20DC79A13D7ED6AB7C
	294B83494D4F51134BD3780BF564581EEE34126FBCA7E561D82135505F6A115FDF5AC5077D867D89D78BB0BABBD9BE320C1ECD554B190FA9EF4D94E7D6F0CC4F88DB607874DB5B4071A944261FD75EEF734FAB9F36696715E3EC5A3CD2F987341118508971584C799E67388B9C97C9F5925CD7F62BFC056327D23B883EDEA29DA2BE1A5A55CBF1E685770286F781FF5BE6A3B19606FC4565C65979742E56E6F7B8D87BD54AE3F7C248657391CFED34F70B9A3B5F60388BFCAD3EEF77DF1F5B5A356B43F21CD6B2A4
	25B67C5DD9128B85288D4B4B20B8BCF830C849AF5EBA4BF30965C34D2BFEE5A75F191E76A61EFF686A692FD62E3EAA70BDB0C2D23F5F25791EAF5D7ADC7850AD6D032DDD114DFFBDCBEC5A75E438FBDA30CEC8D80E7552E49B3ED9167F81F17BBA112F1A636E646F22C19C37063F0B06F05C0EFC44A5F1DCBB4DAD2B128C4646A47E7ED401FCE60E1BCAFDB08C5A53B84E140BFC8BB86E54A13425F08562428FA1DFB9472D4CC13E619C574475852D403CD97C141CFDCABC23DB1961F775E52AF85C4A4A3A7E712C
	B62E46032E35AB86F3AECADEF73D69ABA1BFDC593F9C4BE34635E09ED32643CF76A866A8643B89F84CABD99EC37FBEEB25FA5E337DE1A1D7150A6E431D283BCFC737EF4EE5D6F463FD5388BF5D37F7293AB715216E1181F49B7B519D3E52B71FAB4BC7FD918166C18BB1BE73903F526F4E39427B8D6C9127EAFBDC8576152453D71ACFDF42AA10DF457A22EF7B2BFC7BDA589EB897DEC3FD3FGE40AD7E1AE7CF5AE76EFDE25FF3EEB7BF7E87AA7368DDB653BAEB1AEF5EC4F9EC25DA3F4F47B66453EE38B7553FD
	70BBD5370D5B9D9EC077C69D5D3866D19A5DEC4FE61E437876C6B262361D43787695673B5904FCF3D39077AFCD4837B3994F1525AD3C1EDEB558F38547F91EDC33A9FA9C1D3C86DF0DF72057BDAA3D328C69D51C9EA22BF4B11F1F132B756DE0B213FBD4194CAF781928AB03F63EFECD7D1EC6FE5224786097397D1C637B4B4A0A8B8E70F3FCC4AE2F757B6D2B703AEA5FDAFEBF306BAD9C73299BAA7D5F394ED6CE9F75CFC140781868D5B70CG1E50378693D73F6B544ACDG1968C1499DAB534A45014C6AC149
	593C6A0654B586E35E1A4148ED7BC2ABD78CB25503123BB3D7AB578AB2D786A5770ED75DF093E40286A59762555FE8100998C0CE796DE90BFFAE125A6A1F0B98BA7D79FE55694FF73E4C1F6F6BB2FF3E6F73F5F25AFCFF3E22AFF4F225AF7C7942F27D7946647A73E5D5787365D678730D57519761250F7DFFB08AAE7DB7B442D788E990A860BF0FDB58663FD3EBFE3DF0FEF81BA9A7C5C8E9905E3BC2D31386A12D1764AEFD648F15C971D48332DB38EC83AD95622F9D2CB4AEFD4018A22402A0AB1D9A04D4D27F
	003C1B69B1C269C43524EA41731D70031042F513D62AAE0FBE16C8FE8CD17D2E21C84AC29A39C32D04CF72F6CBBE4929891F1B152755827AC28A088C7ADE2AB407EC5D4F4726A8CBE87D797A700D2F68C76DFB637ADF2687FC339C3E0E6D4D116BFC7F66E441E562E33100DF3D8E6FA18BAFFFAE843EC3ED69929D8ED9F4DC2DF1DA49FDEA6DF5D9D25F2296DDA67ABD482390155F4ADFA6F6FF2B1179FFD0CB8788BD55EBB19C97GGB8C6GGD0CB818294G94G88G88GD5D330AEBD55EBB19C97GGB8C6
	GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGD697GGGG
**end of data**/
}
/**
 * Return the DefaultColorButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getDefaultColorButton() {
	if (ivjDefaultColorButton == null) {
		try {
			ivjDefaultColorButton = new javax.swing.JButton();
			ivjDefaultColorButton.setName("DefaultColorButton");
			ivjDefaultColorButton.setPreferredSize(new java.awt.Dimension(65, 22));
			ivjDefaultColorButton.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultColorButton;
}
/**
 * Return the DefaultColorLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDefaultColorLabel() {
	if (ivjDefaultColorLabel == null) {
		try {
			ivjDefaultColorLabel = new javax.swing.JLabel();
			ivjDefaultColorLabel.setName("DefaultColorLabel");
			ivjDefaultColorLabel.setText("Default Color:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultColorLabel;
}
/**
 * Return the DefaultFontComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getDefaultFontComboBox() {
	if (ivjDefaultFontComboBox == null) {
		try {
			ivjDefaultFontComboBox = new javax.swing.JComboBox();
			ivjDefaultFontComboBox.setName("DefaultFontComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultFontComboBox;
}
/**
 * Return the DefaultFontLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDefaultFontLabel() {
	if (ivjDefaultFontLabel == null) {
		try {
			ivjDefaultFontLabel = new javax.swing.JLabel();
			ivjDefaultFontLabel.setName("DefaultFontLabel");
			ivjDefaultFontLabel.setText("Font:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultFontLabel;
}
/**
 * Return the DefaultTextTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getDefaultTextTextField() {
	if (ivjDefaultTextTextField == null) {
		try {
			ivjDefaultTextTextField = new javax.swing.JTextField();
			ivjDefaultTextTextField.setName("DefaultTextTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultTextTextField;
}
/**
 * Return the LinkToPanel property value.
 * @return com.cannontech.esub.editor.element.LinkToPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private LinkToPanel getLinkToPanel() {
	if (ivjLinkToPanel == null) {
		try {
			ivjLinkToPanel = new com.cannontech.esub.editor.element.LinkToPanel();
			ivjLinkToPanel.setName("LinkToPanel");
			ivjLinkToPanel.setPreferredSize(new java.awt.Dimension(405, 33));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLinkToPanel;
}
/**
 * Return the PointSelectionPanel property value.
 * @return com.cannontech.esub.editor.element.PointSelectionPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private PointSelectionPanel getPointSelectionPanel() {
	if (ivjPointSelectionPanel == null) {
		try {
			ivjPointSelectionPanel = new com.cannontech.esub.editor.element.PointSelectionPanel();
			ivjPointSelectionPanel.setName("PointSelectionPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointSelectionPanel;
}
/**
 * Return the DefaultTextLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTextLabel() {
	if (ivjTextLabel == null) {
		try {
			ivjTextLabel = new javax.swing.JLabel();
			ivjTextLabel.setName("TextLabel");
			ivjTextLabel.setText("Text:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTextLabel;
}
/**
 * Return the TextPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getTextPanel() {
	if (ivjTextPanel == null) {
		try {
			ivjTextPanel = new javax.swing.JPanel();
			ivjTextPanel.setName("TextPanel");
			ivjTextPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsDefaultTextTextField = new java.awt.GridBagConstraints();
			constraintsDefaultTextTextField.gridx = 1; constraintsDefaultTextTextField.gridy = 0;
			constraintsDefaultTextTextField.gridwidth = 3;
			constraintsDefaultTextTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDefaultTextTextField.weightx = 1.0;
			constraintsDefaultTextTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getDefaultTextTextField(), constraintsDefaultTextTextField);

			java.awt.GridBagConstraints constraintsTextLabel = new java.awt.GridBagConstraints();
			constraintsTextLabel.gridx = 0; constraintsTextLabel.gridy = 0;
			constraintsTextLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTextLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getTextLabel(), constraintsTextLabel);

			java.awt.GridBagConstraints constraintsDefaultFontLabel = new java.awt.GridBagConstraints();
			constraintsDefaultFontLabel.gridx = 0; constraintsDefaultFontLabel.gridy = 1;
			constraintsDefaultFontLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDefaultFontLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getDefaultFontLabel(), constraintsDefaultFontLabel);

			java.awt.GridBagConstraints constraintsDefaultFontComboBox = new java.awt.GridBagConstraints();
			constraintsDefaultFontComboBox.gridx = 1; constraintsDefaultFontComboBox.gridy = 1;
			constraintsDefaultFontComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDefaultFontComboBox.weightx = 1.0;
			constraintsDefaultFontComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getDefaultFontComboBox(), constraintsDefaultFontComboBox);

			java.awt.GridBagConstraints constraintsTextSizeLabel = new java.awt.GridBagConstraints();
			constraintsTextSizeLabel.gridx = 2; constraintsTextSizeLabel.gridy = 1;
			constraintsTextSizeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getTextSizeLabel(), constraintsTextSizeLabel);

			java.awt.GridBagConstraints constraintsTextSizeComboBox = new java.awt.GridBagConstraints();
			constraintsTextSizeComboBox.gridx = 3; constraintsTextSizeComboBox.gridy = 1;
			constraintsTextSizeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTextSizeComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getTextSizeComboBox(), constraintsTextSizeComboBox);

			java.awt.GridBagConstraints constraintsDefaultColorLabel = new java.awt.GridBagConstraints();
			constraintsDefaultColorLabel.gridx = 0; constraintsDefaultColorLabel.gridy = 2;
			constraintsDefaultColorLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getDefaultColorLabel(), constraintsDefaultColorLabel);

			java.awt.GridBagConstraints constraintsDefaultColorButton = new java.awt.GridBagConstraints();
			constraintsDefaultColorButton.gridx = 1; constraintsDefaultColorButton.gridy = 2;
			constraintsDefaultColorButton.fill = java.awt.GridBagConstraints.BOTH;
			constraintsDefaultColorButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDefaultColorButton.weightx = 1.0;
			constraintsDefaultColorButton.weighty = 1.0;
			constraintsDefaultColorButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getDefaultColorButton(), constraintsDefaultColorButton);

			java.awt.GridBagConstraints constraintsAlarmColorLabel = new java.awt.GridBagConstraints();
			constraintsAlarmColorLabel.gridx = 0; constraintsAlarmColorLabel.gridy = 3;
			constraintsAlarmColorLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAlarmColorLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getAlarmColorLabel(), constraintsAlarmColorLabel);

			java.awt.GridBagConstraints constraintsAlarmColorButton = new java.awt.GridBagConstraints();
			constraintsAlarmColorButton.gridx = 1; constraintsAlarmColorButton.gridy = 3;
			constraintsAlarmColorButton.fill = java.awt.GridBagConstraints.BOTH;
			constraintsAlarmColorButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAlarmColorButton.weightx = 1.0;
			constraintsAlarmColorButton.weighty = 1.0;
			constraintsAlarmColorButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getAlarmColorButton(), constraintsAlarmColorButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTextPanel;
}
/**
 * Return the TextSizeComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getTextSizeComboBox() {
	if (ivjTextSizeComboBox == null) {
		try {
			ivjTextSizeComboBox = new javax.swing.JComboBox();
			ivjTextSizeComboBox.setName("TextSizeComboBox");
			ivjTextSizeComboBox.setPreferredSize(new java.awt.Dimension(100, 23));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTextSizeComboBox;
}
/**
 * Return the TextSizeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTextSizeLabel() {
	if (ivjTextSizeLabel == null) {
		try {
			ivjTextSizeLabel = new javax.swing.JLabel();
			ivjTextSizeLabel.setName("TextSizeLabel");
			ivjTextSizeLabel.setText("Size:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTextSizeLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
	alarmTextElement.setLinkTo( getLinkToPanel().getLinkTo());
	
	alarmTextElement.setText( getDefaultTextTextField().getText());
	alarmTextElement.setFont( new Font( getDefaultFontComboBox().getSelectedItem().toString(), Font.PLAIN, ((Integer) getTextSizeComboBox().getSelectedItem()).intValue() ));
	 
	alarmTextElement.setDefaultTextColor(getDefaultColorButton().getBackground());											
	alarmTextElement.setAlarmTextColor(getAlarmColorButton().getBackground());
	
	LitePoint[] selectedPoints = getPointSelectionPanel().getSelectedPoints();
	alarmTextElement.setPoints(selectedPoints);
	return alarmTextElement;											
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
	getDefaultColorButton().addActionListener(ivjEventHandler);
	getAlarmColorButton().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("AlarmTextElementEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(467, 611);

		java.awt.GridBagConstraints constraintsLinkToPanel = new java.awt.GridBagConstraints();
		constraintsLinkToPanel.gridx = 0; constraintsLinkToPanel.gridy = 0;
		constraintsLinkToPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsLinkToPanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getLinkToPanel(), constraintsLinkToPanel);

		java.awt.GridBagConstraints constraintsTextPanel = new java.awt.GridBagConstraints();
		constraintsTextPanel.gridx = 0; constraintsTextPanel.gridy = 1;
		constraintsTextPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsTextPanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getTextPanel(), constraintsTextPanel);

		java.awt.GridBagConstraints constraintsPointSelectionPanel = new java.awt.GridBagConstraints();
		constraintsPointSelectionPanel.gridx = 0; constraintsPointSelectionPanel.gridy = 2;
		constraintsPointSelectionPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsPointSelectionPanel.weightx = 1.0;
		constraintsPointSelectionPanel.weighty = 1.0;
		constraintsPointSelectionPanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getPointSelectionPanel(), constraintsPointSelectionPanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getPointSelectionPanel().getIvjDevicePointTree().addTreeSelectionListener(this);
	Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
	for( int i = 0; i < fonts.length; i++ ) {
		getDefaultFontComboBox().addItem(fonts[i].getFontName());
	}

	for( int i = 0; i < availableFontSizes.length; i++ ) {
		getTextSizeComboBox().addItem( new Integer(availableFontSizes[i] ));
	}
	colorChooser = Util.getJColorChooser();
	// user code end
}
/**
 * Comment
 */
public void jButton1_ActionEvents() {
		javax.swing.JDialog d = javax.swing.JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
		new java.awt.event.ActionListener() { //ok listener
			public void actionPerformed(java.awt.event.ActionEvent e) {
				getAlarmColorButton().setBackground(colorChooser.getColor());				
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
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) {
	AlarmTextElement elem = (AlarmTextElement) o;
	
	getLinkToPanel().setLinkTo(elem.getLinkTo());
		
	getDefaultTextTextField().setText(elem.getText());
	
	for( int i = 0; i < getDefaultFontComboBox().getItemCount(); i++ ) {
		if( getDefaultFontComboBox().getItemAt(i).toString().equalsIgnoreCase(elem.getFont().getFontName()) ) {
			getDefaultFontComboBox().setSelectedIndex(i);
		}
	}

	for( int i = 0; i < getTextSizeComboBox().getItemCount(); i++ ) {
		if( ((Integer) getTextSizeComboBox().getItemAt(i)).intValue() == elem.getFont().getSize() ) {
			getTextSizeComboBox().setSelectedIndex(i);
		}
	}

	Color textColor = (java.awt.Color) elem.getDefaultTextColor();
	getDefaultColorButton().setBackground(textColor);
	colorChooser.setColor(textColor);
	
	textColor = elem.getAlarmTextColor();
	getAlarmColorButton().setBackground(textColor);
	colorChooser.setColor(textColor);
	
	getPointSelectionPanel().refresh();
	getPointSelectionPanel().selectPoints(elem.getPoints());
	
	alarmTextElement = elem;
}
	/** 
	  * Called whenever the value of the selection changes.
	  * @param e the event that characterizes the change.
	  */
public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
	fireInputUpdate();
}
	/* (non-Javadoc)
	 * @see com.cannontech.common.gui.util.DataInputPanel#isInputValid()
	 */
	public boolean isInputValid() {
		return (getPointSelectionPanel().getSelectedPoint() != null);
	}

}
