package com.cannontech.esub.editor;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.loox.jloox.LxView;
/**
 * Creation date: (1/22/2002 11:22:51 AM)
 * @author: 
 */
public class MagneticGridPanel extends JPanel {
	private ButtonGroup ivjDisplayAsButtonGroup = null;
	private JPanel ivjDisplayAsPanel = null;
	private JRadioButton ivjDisplayPointsRadioButton = null;
	private JButton ivjGridColorButton = null;
	private JLabel ivjGridColorLabel = null;
	private JPanel ivjGridSettingsPanel = null;
	private JLabel ivjGridSpacingLabel = null;
	private JTextField ivjGridSpacingTextField = null;
	private JPanel ivjJPanel1 = null;
	private JCheckBox ivjVisibleCheckBox = null;
	private com.loox.jloox.LxView lxView = null;
	private JRadioButton ivjDisplayLinesRadioButton = null;
	private JCheckBox ivjActivatedCheckBox = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == MagneticGridPanel.this.getGridColorButton()) 
				connEtoC2(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == MagneticGridPanel.this.getGridSpacingTextField()) 
				connEtoC4(e);
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == MagneticGridPanel.this.getActivatedCheckBox()) 
				connEtoC1(e);
			if (e.getSource() == MagneticGridPanel.this.getVisibleCheckBox()) 
				connEtoC3(e);
			if (e.getSource() == MagneticGridPanel.this.getDisplayPointsRadioButton()) 
				connEtoC5(e);
			if (e.getSource() == MagneticGridPanel.this.getDisplayLinesRadioButton()) 
				connEtoC6(e);
		};
	};
/**
 * MagneticGridPanel constructor comment.
 */
public MagneticGridPanel() {
	super();
	initialize();
}
/**
 * MagneticGridPanel constructor comment.
 */
public MagneticGridPanel(LxView v) {
	super();
	initialize();
	setLxView(v);
}
/**
 * Comment
 */
public void activatedCheckBox_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
	synchButtons();
}
/**
 * connEtoC1:  (ActivatedCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> MagneticGridPanel.activatedCheckBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.activatedCheckBox_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (GridColorButton.action.actionPerformed(java.awt.event.ActionEvent) --> MagneticGridPanel.gridColorButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.gridColorButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (VisibleCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> MagneticGridPanel.visibleCheckBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.visibleCheckBox_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (GridSpacingTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> MagneticGridPanel.gridSpacingTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.gridSpacingTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (DisplayPointsRadioButton.item.itemStateChanged(java.awt.event.ItemEvent) --> MagneticGridPanel.displayPointsRadioButton_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.displayPointsRadioButton_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (DisplayLinesRadioButton.item.itemStateChanged(java.awt.event.ItemEvent) --> MagneticGridPanel.displayLinesRadioButton_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.displayLinesRadioButton_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (MagneticGridPanel.initialize() --> DisplayAsButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getDisplayAsButtonGroup().add(getDisplayPointsRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM2:  (MagneticGridPanel.initialize() --> DisplayAsButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
	try {
		// user code begin {1}
		// user code end
		getDisplayAsButtonGroup().add(getDisplayLinesRadioButton());
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
public void displayLinesRadioButton_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
	if( getDisplayLinesRadioButton().isSelected() ) {
		lxView.setMagneticGridDisplayPolicy( LxView.GRID_LINES);
	}
}
/**
 * Comment
 */
public void displayPointsRadioButton_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
	if( getDisplayPointsRadioButton().isSelected() ) {
		lxView.setMagneticGridDisplayPolicy( LxView.GRID_POINTS );
	}
}
/**
 * Return the ActivatedCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getActivatedCheckBox() {
	if (ivjActivatedCheckBox == null) {
		try {
			ivjActivatedCheckBox = new javax.swing.JCheckBox();
			ivjActivatedCheckBox.setName("ActivatedCheckBox");
			ivjActivatedCheckBox.setText("Activated");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjActivatedCheckBox;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G37E1B6ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DD8D45715EC57E459DDB6359B53182CE979921B5854F64D2E5D3ADB5A252D5BF59B131A54EEE9CA92CC4C1FB5919312108463E31861E77895869885E1B0082008430F0A087C7B17E217AAB538E193071987CC9CE6A6B38F4106B6FB6E4FF973E6668D83A6164F63FB775CFB4FBD675CF36FBD775C7BC6B0D4C66BD2B586C1C855897C6FE82AA0D40F8902776987079137AECE2B9794FFD5GBB0443
	ADDAF8A6C0DEF927CEDFA0FCE04C04F492243BCE68742570DEA4D80DDDB6F889510E91721A371EAF193D1D5F67E0BB99B47D4B0C9CF86681A41336FF29550F839488DBB30C9C5F8569BC7ECEA0D583EDBDC64A5C315B2840AF6765B643D3007C98DAA6A7331C67DF06B4A9B38D4F2847D85925704C225C5D54564A4B8DFFE911107413ECCDAEE9CBC19FC17BABD63716BFCD422DD491E942C3D71B6059BEF0E52C436A1AE830DA1CCE17D3922D638D22F7F224C13459A517272D6D1CE54CA9CAF66BE90F5DB6E0F1
	0A8EC1D87CAB426B3F6F4E24F400D7C8D747935C518A221FF8C8A7816444A33F0F6436F3FE96DDD9A4A45C73F6EAA8FE4763991FD171BEFE633E3BC9537EB821FFAEB30757DB8B79DA40B1DEEE4BC4DEA29EC7DECE98C859D510DE87703C0CEFAAA6F861892D5E88E0FE82F9BC318FF99C7A649EA1F924C1139F0247B3CFB05E5CCF78F82CAF3A245DC9EDE4C3AE69676510378EE0A3C06613DAFDAD409900B2EA1BCBF2764333FBC46AF5CDCC381C5DEEF70F4366B96732098EF8EFEB039C0EBBAFDADD9E30D5E6
	FF2BF7572B7003F079C9FF5BA340E4FBCFC0FBD9FE7ECEC1122AB5BA9559D67EBCD7A3F19A11CF7AE4CBF812686D5734EF1741BB29C7469843EF27786A849F1E7555890AA769E148B3EEC0BD8F65A1AF7D5ADB0402C633A6C3051745BF5C2BD93F010F118DBEDE180E3F1BCFF4ECFCCA2BEF8168873081ACGD87A946A78591C66F0BABE8B766EF1DA9C97A6C75C960F54B3E9F758C48F5775054C59F42D862C1EE55E7518DE16B5AB7BA87E1F8F296AA5FE19C1937B9453C75CD3BE3D30366F8C59F672D3413611
	A7776183F42C64BC4D7AD0A763E3A85E2440333E6D25F812EE013C45G2C7CE3F4EC25707245B21D97A95E3C5107E7F4DAA91E24ED10D7700CD64F7A77528EB28F46C0BA8EA091A073D998C3GFDGC34FE29FB7669E1A63B89AD074AF2B3BA917CC7E623464311CF5FAA50B53AADE34F86C1691076895F819D7F209DEC77C6AA55371ED05B6CE0BCE51E3116CAEA7D037CC08E0CCDED6A6B5EF87A96391E96E20FDC20486A65C82524D4AAB0227436215FE6F36D9A451D702656F48A3B6F155F291A88281E29D4F
	92FD7D0A4E3953703E68D954554D05843799525A67D0AFD1F905707C8D983768E8EB6B33DB899B964FB534512D9544CE0CD03E95E0A9F9BE8FCFG738B20F30008CDB0DF7E9A66B000484DB8F6DF9AC29B6D4B3DC3287FF9032A0D92E8FF11595B600B01B62A2BA4ED4FGFE89C08CC0BCC08AC079CBDAFDBB40B0408C4092001817F06D6D2DD82120CFF8A5BCAB5BA43C13A7611BBC09ECF4AE5B2CEE7BC4EEA267DC46683ADF7B4A99B95D38974257E53CFB7CF84F66E3DED9AE17BF0B78D3471F2533521D5B7C
	D22CF8CF634F7CB954E37615D4C457F46807E4G639634317F4CB3313157E3710AB85A7E478383C53479AC0E15DD1DCF5A932F0ACE29CFF40BCE9B3CF8392DBF181F659B0723AEF90CBCC271E4DE6E05016215713F485F4170C0C3B92AB17FC91AEF11A40FFDE4D292AF0812E4F70E796ABFC7F9717ACD79DEAE6BEBF4DEB3030CE7G06E4D95D85C50A71546FF2DD19F48F1219020F4B87287F6100728B36203DFE47C0F04BA0CD6C0561FEC8F1244C9A395CD943509C7BB1507E886D39595F483C5BC07BCB1BC3
	D956C6C2F12D406602B4DEBF035BF9B6376FFCEE33C5F21DD7E81D67C10E02C4984FG1781A281D6G448188DBE15E8798580A7A6AA80E1EC7BB7311797384462B1D72BA97BE0BBF677667834C462DBBC20F510FF6280FD1778E75B1FAF5476CE370CFBB5446A04B3BE9875115EB4A299853FF33032CE7D61753A95209C52EA2303D4CCB03C48E3B53AE3DE7370D0952857B9FC4C1085ECA46664BEC4D0477282D380ECD3D036BD8759FEE93E2BF4CD2750797FF2DD633F1AB9BE349DBFD6B986369EF8B49DCA1BA
	4489D0D42FEB52A9B17C1768FA4C715EB40AABAA018D2850E7773A9D16EB5D5E1EC9C9F2B9CFFBDC13EE16FF60528704F6ED50E62B1E48E437B8C01E5ABA5341039DA6B5A275961B2D367FA34BD54BF403F78AB45D50BD62053E32CA2C50713A0BF5E6E607C5C202A91236F5C3DB2B37F8469A159486DCF65046F90B4D6EE2256A25F13B372E11713442F44365EFC279F79F7E631FD37EF795B29FE6C2B78B48CBF64BBFD0DE3A412A2F029FE86B9D972DD7FADC5375F6C91CE8A07FDD10G5FBBEEF10E0936DA52
	E803E5CAEA20433461AC641FA4EF0C59E0F9AC01F4BFBC9BC832BF94CDA2C68B677356D30CCFC2395765F0F9F89FDA68E8ECE00F8151B36A72CC843359CD33E7E5F44C1F70075D81948339D410A4FC9E66FC8ED65DCE793CE8775AE1F57E1C35F9551F6ABCF5F9047328399CAD6B72025BE285839A04197514DDF4586A2D6056C88D74FF36B328D55A99235ACBF243EAB31074073D8A227D33D1A54C9EE53CF61D7ABA65B564A07EFC94EB8BC1FE1E9AEE154755296C7B14FC775B1D620DE3BB107ABC39BE46FD4F
	3A92321E98DF566A6B817ADEC65FB3370864236F499DEC98AB8E3694C2DE744BE41D6B2AB4A8562E6117D9FCB28A1EAC3F22D6197FBC4F4F136B3F6B17DF7BCAE07E55B0756F8ED37FE13FFCDBD07E1B2A79ABDEC17EBF291B3D7EFDE172FF96A6FFFB48FC568FEF9695901FC57CF8D2F4DAC5627C7FC63C067BFDC3E1C05EC50BE35297C748B2901F4F2158B7B0FC0AC15D0750941338739519FEFAF13AC062810CA673F9D6D888CD3747BE815BAC329162F166B5194AB873EA100344241F07E78AG2BFB45C07C
	B31550AE57C5FA31D29706A4C6036AAA09649FAC41B8F5D7925B738CA531BD0DBB09156BFB1514FBCEAE379970444F57825EG508250A0537BF6A916DB786AEC746CC6AC376CD536DF080567BA853D952F11F24DBB31DCA260B381D77B9A1341769A23474A3DA617CB013461F52073BAABA73D2EAC77EF3B30DCAC6063DE7751CB76AB67AC43F2FD49DA7DB040CCB2AB37AAD9D92E319C4BE53E41740174CCEFA84BBDA317135EE0F2E239A8DEAE618DD2AE2B82635F07E41C096236402663F6AD46984F95A84F8D
	EC5EAAD53F177A45EF72984A1B0131578FCA895DA1400F3F09F3D4C431F2FF5C6D753A2CF69A7A62F6FE28C46993B3DCCB89B9F09935FB3C52D958ECCC63D8E9ABA13C125898C7F3FBFF21E40F909C6BBBC90FA964002E15CC086CCCE02A1234C976DCB3EF225F7F47D34AF308187C92CDF68879D7F23957AB640F7A27CD9A464B40E9355832BFAF3DD3F40A6F15B7AD976D62941765536906B9548F284A6BFE6340093936BD686AF55944B3B06D841262BAEA2CC33BB13C05F343ABF5E47E2067C28AAF212D6DDD
	BAC530FACDD9193CDF7A5F42F3157E9CB217AD0074920015B23DED8D3BD569A9BCEA81634197699AC878D98B7535A9DABDDB4F3FC771CA6F1C51B6BB09CD047747517E3FD951457BF76D37EE91B2AD952AE7BCD1AF881A7694566F3694DF7F8BE5473486G46EBC1A9723A9C4AEC14F91DAAA5F3B7717F59DC3A48EFAEAD5F26551F81F0EC4372DF256549460071B8F389F90CD89BAD980BEAC21EC3C5EC63F120EDBE9E279FBED0183376C0A16371891947E4A8E3FEDB2BE7783FB7A2FE9CF051EFA3AFEF987DF9
	EFB629719EA753995E0967G1B81D73D9D576AEE0D1FAF827885G4B36E3BBBF5AC96629F9FAE15C7E5D531D04474F36336176F0F711529E5ABFFDBDD5CD571179291A186DCC479B377BF4DD3D6D0BE92DGCC97153BD097E6C1279F87E03AB824D36AA29A70EBGB683B0DD5C3C2B094BB33F1D944E87A72ABB607959B6D0B87E1E16E590D2F57A56D41441D998581FC486B764B39EEE1BA2F352E75A9F49F3406DE5484382209FA373106E679FA625B2BD16EBD00FEF17911B1A67860A6B70116CEE2E434F34ED
	12E5785FF25996003FF18D6AE9DB5FA9B227FCAE5B9DECABEFB736958D6D4449ED9D4CBB69EB6B336DD184799C39C969BF44A69407749F8A52F46CAC06BFFD7E4333BB495A518578C1G3666FF3B04782368BF1C07158A1882AFD90ECD1612BE86FF182747CAC93B549F96B056BAD399F876B94E5BA6B023F8DF485F99BF6F14913BDA8A38D800B800C40082AD7276D2E958F3641353331CA39F291B4FF930995AE5750EE685566B537AE498D23CA3303356D83A8EBBA0BF156B0661573275D901E776F31E62C9FA
	896489BAECFF38A49C5FB61DBF8F2C5EC7E16BC58654E37DF00D7A3A51109783309EA09140102E531B81865331BF5A0D47437687F3FE589C579B98B566FC1614DE1FFC55E16BC9697E7558F8AC692990D47DAA4530443BC2C99501FBB832A70A823A51G4C87FB3A987D5BD500E3BAFCE3B751E1A224F3B2F47AEA00AEG9BC08440528C5461AD461EF0BA140F34C10F98478FD6A55749466B54654BE16B45E42855DBEC3C3E766E8DDBEFFDC0FBCC579F19505F5D1C0163E3E4E7A02D6ACC7F3A6ABC38036AD584
	243BB2556C2763E2193A7D60FA4BEDC7172BE6BBC3C053110936B32DC1F996E4226DB8AB691D8BC84781A48264E44158836887F0E7216DECB02A6DBF5446DF706643F7D64BF431F757756930B928DE208E97E62969B07F42CEF59D627063BA2C522969F099500C49C29D0E6AF07C2D4FC21BF861BA65F9BD2CBCB9FAB53B7CDAA172D02DC79E6EAA3BBE9E3E91D4AF1007E13D1ACE7756162B6BD47621982FA7EA11D7C96675F27975716A8C2A97486BF23DDABDC3507D39393537A728DEE0FB2B75EAEB5A37683C
	918FF91B75B8FEBAF61233DF74F52893A8201F736B1DA44E677258476CCE44A5D21C55652187226C3E412DB4361182B44B33F5FA06B36F23FE952407E45C0B94B783690559587E013250673CCDE56A673C2DE56A673C9DE5335F45B8D5966A9C1871F70E469FD6GEFEBE51EFFCBE38B8910CE12F1BB33D1B6E38E5A4E0F295F528E69E1G370CFF1479B9FC0FC2762B8CBF14B35B9D88C2A3D41E9A34AB4A7B5A7EF856B6F473201FA7785F3DA074F7D35CF3D23120F2967EDBD82D85BC7F38154650C81913126B
	DC235C9FFFD99E3A6F7F26DC3D6F971693FB753AA6BDD6B130FFEFAFB74AF93ED91EEC9D88BB2CCCB42DAFD9BCE3E210FD5CC76B333CD075D7140732A1A66B3952ECAE6B402847B5B150486746FAB97E97857D43E2D40F53F87BEAC84783A4497D76FCB1995FB9397EB6E06269165CE05BE03C25D50456273EC2DD1F85952175D9DA91DE1F260A507A5CD791DE1F668A222F913B4D6E61A7C8CC0FE5C6254DB4497A7D2951DF3FCDFCCEF41954FC29B3DCD743AABAE3767BDFA5C4E7EE18C6C4AFF5947AAC120537
	F547CE858F3D44EE795A110BFD694E453E0CCA257711A19D8B90170BFD79C70D9ADF091C9FEDDE283E1C2E8C5D171FD62A7765CD15217BB22AB2FCDF5ED6993AAF17D4066F4B2FD54EBEB606F7A97559A46B73128E7DBFE39E6A33380368F3G5263G91F9284FABBA5467E2056E167377D895FD3236CDE54A361B85DCCB6FAFA474568754FB1E27D3C252DB6FC72FC52657D30B7C956487572D8E403566E33C6876F2A53D43F2BB3F592D449F117B631FCDE45FBCAD273FC35357847403DC174999601847629E37
	DB3DFD22576A313BA597F4683AFC720D4A7D34BF7B61DD82D88A30DA419B2371409499EB23307302EE3C60962D02230054BD3D8F7DD1E3812BD34F1F7D85986F7947E672F4D8C668B99E13C5DB9B6A5E912BA3F66749B6F652E9634E88BBBF3251351984277726A2BDB1AB1D36F14DB58965CCBB88EFB3D9485BCA60E5ADC092C0668E469F635F1C0DE54681BF833084A086A0FE8776C7D99131E5FAAB021BB11EC9327C7813243F59A9C4A3479D6C4FD34A47ABB03BE8740B872590DE8BFD7A2EAE8C369596A779
	9D3D135EDA288B388BBFCC717D8ABC0F194AF761AFC35E6AC274D936537DF9ED11CE5FDE0438DEFA8EB6G694BB2EEE9897AB68E99F733894B45483867CCD8EECD910E4D1EDC250CC603CEDFEF704955E588A52320A1BC8F9A18ACE9B2DECF71B6851E1F972462F90184F9F18664E9118679ECAFC65C9B9A6473CCB1727901AEF0CEDEDC2C18CB0AC3717957F4CE0AADE67CE44B785BA9BEC101E7FC16D1BCC98BA5BA3D2D84F97A821D6F56C0FA230CEB29C15E13E55CEDB5483B3694F1BFA844FE9C14F17AA22C
	BBAE632E94E1DDC946DD2A45F6D74B383FB5E339B899D7445AB0029E0DB89E7E23126CA11491E74570EDA473D27DCEB236B6D4D68B6CFBB582D99C48D89AGDA71G64BDC526DB340F4C89D7ADA3E376D16F30CD9CF5CF7E2171C8832478185AA788986FFDDF0F6725D171378A06250F077CEE4B3D137561221D3EF8EF7DC77F21E36B4D845D07506F36A0DF3B0B707D8F7B1471FC4263A1E020811EAC7FA13FFCB23E362732FC465B03FB1137148717880EEF6F8E798DDC7FAE46131BBF8B9AF689B1FF7FA1BF43
	DA1A055F14AD037C04DDB81FB6DE765B571250F8DB9B191EFAACE37DF486E432ED4EC2590CE538166E525335945263G91G0C67699A64F9793D37887D9107D4F9CE98AC51ACAEE33C46147974E9AC3A24E5EDDE4C46B6134BD01F8B32157AA2FDD92CD16A73DF328375DDA7A8731F774BA77CFE532F7E2E1C407E407A4C064FD0FF5BDC2E53771523DD57D661DEEEDC465D3D876DBF2A9C67077768DC998FE9E1379CDF23B8833F5F4163ABFE7758C959403ED68221B7DBFEA030B6B4454AB6426D5966F2BFBE58
	170923635DAB12BBDC222DE5B751EFED891E45D76F46317A8D8DBD9B8647F39AF060DD136B72023602147FEEC99F2799232207D441F7C71E40BAA83391609D341B5DEC6E24BE8D253D304EC77BED93526E2FD0271DB68B6D4B95BE5AFC9E4CC55BF0D448F3E3895A460C0C7BD9A95AC6F4855AC1BA55C1221CAE20E9F227074702D3677BEDC3A0A42A4868EF938FFC66B6E63393267F047DBE7D9724217E8BAA4377EDF5253F7EDBAB8375BF29C3DD77D5222EFF1F093A9E2FC45DFE31030C71D510DE8F108810E3
	821F42A44796E97E99C80F83107BD2640E93398F45722BBA14FACAD4703EDEC58E82C8DFADEFD5C09D56C61BDF9B990A77D0E3953F3918CB3F70FD280E4C4BEE0FB8AAFABC220DFDCEA030F17ACFD417114073A2G56F7E9753E3EDBDE03FD972BA297FE8B02FD37B1E06C2CB7797A0E35372590670584393DFB8E7A5A7BC291361728529E5242B635D57E6D992B825B7B8275D92A81EF86E0ED5CED46B6062B54E5D202BB200D850AB618FE779712FE333A9C1393CEAFEF37DB0EE5AF2C42EF7837D7C640736651
	D178B79EE93DE3E1449DF714C40E74E4C6267526C7DAFBB3A3B5BD6911A3FDA62125D748347675B47575E419C6CECE15FE5C57E7361E3E661DB23418C6CECFCDCD950E182D27CA27BE3EA0240F747CEEC7F1CF7A3835EF6F6F2B014EC89379CB0F3490258AA5115058A8E98F7F7A76907EDA643BB051409F71356A77703D9875439EB2A97D30B441670BC5C23975FBFCFAE33EC0E52E72AC5D71705654A2C15DFFC962F52B5FE14F4DC7F67178F2EC8359D78D81FE9CE086E09140B200556FE03C372D0ACC6C5D6E
	5173A28BC61C33B8ADE374A6DEDB9B204FCEC0CA39DD45EF9677DD5FF74CBF48C09FE7653FFDD9D8F5736329216E58A5F0F904EA76DCD63F0B6219EC3FB593594C10578F60860884D88230329AE57B0BAA42A3B9F1325925EEC932D847698DC3BC08A2175DFCE8322166B108203375F0406A2D3A4EFADB67DD0F6DF3B6D3BFF2CD35EF7CB07C2B943FDE01E73E2CC90F3EEC8264756D45333D7B9A09AE17C1FAB5C0BCC09200218646F88D6A72A7D5E6A14C395E204B5DAFDE959D0AD0BF136F7D6C794A476681F7
	AE09A70C38BCB4704562899FD7E22C208FF89B86D884302C064572D9195FD5E119589A96F3080367E6DE064DD97F3A8767AC6DBE75F9D1B98F9B21CCBDC0B1CFF76D8B1C33FEF80634996ACB889EEF39DC6AAB93F867065369AC21BE1DE7D2E463A5FACAB98F346A4BC25E296967F2DC664F198FF771B177537715B48A6E54E9D2C350D84C6BAE5D675BD770FDCC89468463A02FD2E05FA5E68A781758990CC35F65G8947F1DEB6646078AF7F4697053EDFBC1E0A6BC0A0AF8535DC1EDA9FAF4258B13E47B9D405
	7CAC2E45F56FB192269362EBC97F7ED5AE5163BAF8E7B67105923489D39D3309D6F876831033A0E6931BCA51A6ECF5617D2AE9A893D5673309683AC01BF8A2076C23023E5E92B04E5A1D06B2AC2FD357A913EB7FD112E2F2FCC5BEFB5E4865C801E741FEF65F0D496148C4B9EA7707774F5B214C40FE1F9C367D01F2ECC8A75FB6A83EC86AA7E1C416770EB50F67517B057DAC4048726EEF4BD7667959F9D315411F26FC9E4C3FC3BCC86A86FC29453FBFF834C33957F4F359976D473B006EF4357CE57B51CEBEA1
	430173D1D7C34EEA1D6214C93E6156E70765007C30C2EDF3F33D2971D89D765763B558DFB1B22D2F9C7351CAAF42314B4ADF1347CC527E597A774AF15FD839255DC7AF42BC9BEF076BCDCDE619371F19312D7A8333357540899F7D21C25247A4764E7C295F1AA2847F3F88C04452BFC138775E1F7CA4B1D19036EFFF6BAD720C063F25CBC99972474AEF5F6EAB3F1D7E89737EE3EB55C2932ED5B610E749810C1108B3B81719AFC58966BB62C37A8F6B8FB05BCFBA60730B181C9F57F441730F490230AD85763A9C
	363F4D788F8202CF714127490CCF6677FD3986ED4DD44FE2C36D754A7B03F7F5903FEF90F063GB3G8B81168144822C835888A09C44F5761903E4934F9659537C34202D2DF72CCF3CEA370A641B87FE78C49DB3D6673F9A085DB965F31F079A14779F996E518672BD0507BBF9E7ED080FEBA0EB344D6120A7448C77F883595F581DDEE2EAB0F95162BCEFE3C301FF9E7D1409497ACCA751C5B948528AB08C6086D8A04BA69D625FE82878E2A7EDE3649C0C7C520A04ED3DE78EFD667601B97C6F330C19094CA3AE
	18F6A688FD44CF1869BD93171BA2994EE3A67665310F0DCB2CA81E7B51F16590C7A91A5FCB233F85465E5FD23C5768BFEB9CA03C2F58F9C35B60E76A0D21757E1DC675334E6FB52A5F8179D1636C5F7ABF58987ADB7FC79B155F7A33F9610ECABCEB08863B0ABB08715867F613FD0C5A6F150DCD5A9BA6A53B232DED50AEB9449E6ECE72F9F3C3A56E1F8B9AB0BEBBC9671EB3103E8CB08D405A3ED605F352C2402DE9403BDEBF2842F82BF088E9EC21E5DBA1BDGB0FE884BEE5A0373F44421397AD1B8F7531F53
	61734ABFE6107CD5C0E37DA19CDF562683703CE879A8782B9FC08EF8DCEE51A3DDE3D8BAECB92D7BE9D30C46571B760622812374DDD8077EE764A351AA8DDA46E41B5C6AF727B7897849B44F9EDBE56D7EBC0B78A965D03695E098C08208840886D8893086A09EA089A02791FCB9009600B3GC3G8E0008C6145F5DD2B70B7CBCB8CFA7990F055D2BE27C37D4A979DF847416B54E4EBFDBBFAF525F87643F8FA7AAFDEC73A74F2426896A6BD28C273D36516727B37B3A3F846DAB05485904B673F2895A1723896D
	4BC43F5DD98A69980075F259B223FC9735E93E7655CAFF7A0E6964D41AD2A7554D50C74D336B044970E39D4A508F6597B4A3DFF969A8C3CCB34A501ECE4AA6C1BA2785F0ADD8762ECC14213DE53EB23CCF16AE3EF7FBE5B44437EA54CBE5EDE5F5E2DB125C7EFD6932DF5E02314D27EA147A58887864165975416A7D49EFEC649C869BBEBC17FAF1E6E53D9628F3E6CE753E2CFA6FA6FC3D25732E47467001BC32668F83EFEE0005G51G2B8E63185CFA42A42879B3746BCD51B6E0F1400AA0B23FECC5CD01607B
	C37F115955FF5720DD2D835A19C7107E57BBC8BFCD59ED523800F35837BAC85CE0DCA40B39FCBF0316836FC9D1EED587719D3D30D6B8E0B3BEE031F17C7769B97148B5C9748E089E32D1673FD355C17CAC1BC53230741A8EFA670809406BBE54C1E40089466965979F6D532203652D8F4833DEBBE771DEE1F9AFF606DEF313BA437BBA5B289D0B60238CDEF3F3FEB5DD743E686828D714751051D5A0634EFA87DC4409F3B9198DFCB5689B19C068BA2296F37FE658FA0177E1D93DE74356D342B0344D62DBAF5075
	59FDA4B06E75B2454F9C890CFBFDA26FA5A2A1EF73919C531FD6DD5FD8793B6B2C7763F957E3F2D977507362230172BAA8FE78E8203C2F4AFE4AB8642DBD0A72EE5923EC5F5C2A5377354EBE27B01BD919C15EE59F0E398EBC6F3B99E47C4B5F5E524F3AAFC0C16798BBC74AE45F0337B29E99AE17625C9C47683C10955A7E9333425B7F9BD96A3E7E762CE0DF1FFD6F9D6C6B6B75CADF1F617229FFC93E7D9B124B99E45CA599371352A438777C6622CA3D1E634F784DC7FBE91FCCD15C90476DD760AE613771FA
	B206C164F192FC675F466B49BCB4EAF770F9251516A1FBB2C95647894A235DEDD1705DA363AE49FD711B2A607E9928D2768F1B670F6613B623DAF15EFDC4CF76F221FCAD72ED873382347D95E13F7BD29FB35F3F4EFA2F4F3B9E9BD30B28DF955D9AB8566E2278652D01E32D2694475AAA48ABBF06E34D6477ADD18460979D1BFD2CB15D7FDFDAF0FF0C24A97B0351DF9BF4E7EAEEFAF96CBA6B9525DF1FBECB68EFB2AFBB96284FCA0ADFF9ACD01FBD69284FD8482BEEC3FD5E6D775B351100DF52B697FFA82FD3
	0D6F953C6EEA959A2C5EF2D5F963F8794DAA75787D361A607EFB3BC659FF7CAED9C9F039BBCB024BEDB18717CBB2874F1F4DF9546731B8ED8EF96EEB253F73CA7C1873225BA3FAE1CAE5BF3B41733B724818765A7F40673A3EBC755F21F9B70F74094BFB7EF44FC9A70CEA514BF494DFF3FD3634E95E75585D7058A29617A25EDAAF7B05DDD9A75B68BEDEB82E539B0E4B6B1DC9105BCCD63C7F05694647E96E7CB334E15F63134923F8FF2B6A4637ED58F3A35BE0FD1A7ECE306DE73F2334FD364F3354A8FE7F03
	AE3F8CDFBE1D6B4363724BED7AF8BE99AFDE3FB56A4ED21F8E6FD13C57175EF8FD1E3C21EDB05D9DB5866B335D983C3614E77AE45F2BF8BF13FE6375E03921EDB0197FB5B3D88F5F4F8C1EFB77F662BDDE53F1796EF5A75E0B2A17F1BFCA47F2B3B22EBA834BC59E477BFA86930DB310A8240067BDF751ADAFFB0FA521D2FEEF672ECEBC430BBB9E7E8CAF701E5066630177771EAE7451BE1E013413C3501E6D8ED2CE3B0FB60B17BCAC47D00C6DF8AEC60F60F83EA3837387647C3AE21FDD7D4CEF0D0E04B2CB5A
	673236FF1C212C37826A2C8ED30F754D264E607E5F52995C7F119941657EAEC3D90E45ADA21A9E02447297851DA4243333FFC9505183FEC948124FFFE17211048CFE246975887972295ED96A3AA7DCEDEECBFB7A1EB40D20BDAB24332D4CD9C1CB76A3E7859D5D2E1C9552E0ABB3CDFE92C94BC5A2CF2D77E361CFD4412131B2C04B054167095D377C72974BBF6CB3E8848DB44EE2A750AA8D2C5076C8382DAA84C5A5D66F07A51BA2439CEBDE97D692747CC205299B341DCB223CA6FE245CA68959B4CD0F1F5BE6
	E1B388ABE0DC376ED3E5DCB779261454D077AFAEF7B25B13BA83FF53E493756BD6GFEEDA76E1FE6DA48A53DD0F1768B939607C332B82EF4BBEDA4665EC3E2D1822EDDDBCA88FDAF166119AC6FB57AED67C70ABA7F8FD0CB8788654CFB437CA0GGCCE7GGD0CB818294G94G88G88G37E1B6AC654CFB437CA0GGCCE7GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGB6A1GGGG
**end of data**/
}
/**
 * Return the DisplayAsButtonGroup property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getDisplayAsButtonGroup() {
	if (ivjDisplayAsButtonGroup == null) {
		try {
			ivjDisplayAsButtonGroup = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDisplayAsButtonGroup;
}
/**
 * Return the DisplayAsPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getDisplayAsPanel() {
	if (ivjDisplayAsPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitle("Display As");
			ivjDisplayAsPanel = new javax.swing.JPanel();
			ivjDisplayAsPanel.setName("DisplayAsPanel");
			ivjDisplayAsPanel.setBorder(ivjLocalBorder);
			ivjDisplayAsPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsDisplayPointsRadioButton = new java.awt.GridBagConstraints();
			constraintsDisplayPointsRadioButton.gridx = 0; constraintsDisplayPointsRadioButton.gridy = 0;
			constraintsDisplayPointsRadioButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDisplayPointsRadioButton.weightx = 1.0;
			constraintsDisplayPointsRadioButton.insets = new java.awt.Insets(4, 14, 4, 4);
			getDisplayAsPanel().add(getDisplayPointsRadioButton(), constraintsDisplayPointsRadioButton);

			java.awt.GridBagConstraints constraintsDisplayLinesRadioButton = new java.awt.GridBagConstraints();
			constraintsDisplayLinesRadioButton.gridx = 0; constraintsDisplayLinesRadioButton.gridy = 1;
			constraintsDisplayLinesRadioButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDisplayLinesRadioButton.weightx = 1.0;
			constraintsDisplayLinesRadioButton.insets = new java.awt.Insets(4, 14, 4, 4);
			getDisplayAsPanel().add(getDisplayLinesRadioButton(), constraintsDisplayLinesRadioButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDisplayAsPanel;
}
/**
 * Return the JRadioButton5 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getDisplayLinesRadioButton() {
	if (ivjDisplayLinesRadioButton == null) {
		try {
			ivjDisplayLinesRadioButton = new javax.swing.JRadioButton();
			ivjDisplayLinesRadioButton.setName("DisplayLinesRadioButton");
			ivjDisplayLinesRadioButton.setText("Lines");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDisplayLinesRadioButton;
}
/**
 * Return the DisplayPointsRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getDisplayPointsRadioButton() {
	if (ivjDisplayPointsRadioButton == null) {
		try {
			ivjDisplayPointsRadioButton = new javax.swing.JRadioButton();
			ivjDisplayPointsRadioButton.setName("DisplayPointsRadioButton");
			ivjDisplayPointsRadioButton.setSelected(true);
			ivjDisplayPointsRadioButton.setText("Points");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDisplayPointsRadioButton;
}
/**
 * Return the GridColorButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getGridColorButton() {
	if (ivjGridColorButton == null) {
		try {
			ivjGridColorButton = new javax.swing.JButton();
			ivjGridColorButton.setName("GridColorButton");
			ivjGridColorButton.setPreferredSize(new java.awt.Dimension(33, 19));
			ivjGridColorButton.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGridColorButton;
}
/**
 * Return the GridColorLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getGridColorLabel() {
	if (ivjGridColorLabel == null) {
		try {
			ivjGridColorLabel = new javax.swing.JLabel();
			ivjGridColorLabel.setName("GridColorLabel");
			ivjGridColorLabel.setText("Grid Color:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGridColorLabel;
}
/**
 * Return the GridSettingsPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getGridSettingsPanel() {
	if (ivjGridSettingsPanel == null) {
		try {
			ivjGridSettingsPanel = new javax.swing.JPanel();
			ivjGridSettingsPanel.setName("GridSettingsPanel");
			ivjGridSettingsPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsActivatedCheckBox = new java.awt.GridBagConstraints();
			constraintsActivatedCheckBox.gridx = 0; constraintsActivatedCheckBox.gridy = 0;
			constraintsActivatedCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsActivatedCheckBox.weightx = 1.0;
			constraintsActivatedCheckBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getGridSettingsPanel().add(getActivatedCheckBox(), constraintsActivatedCheckBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGridSettingsPanel;
}
/**
 * Return the GridSpacingLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getGridSpacingLabel() {
	if (ivjGridSpacingLabel == null) {
		try {
			ivjGridSpacingLabel = new javax.swing.JLabel();
			ivjGridSpacingLabel.setName("GridSpacingLabel");
			ivjGridSpacingLabel.setText("Grid Spacing:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGridSpacingLabel;
}
/**
 * Return the GridSpacingTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getGridSpacingTextField() {
	if (ivjGridSpacingTextField == null) {
		try {
			ivjGridSpacingTextField = new javax.swing.JTextField();
			ivjGridSpacingTextField.setName("GridSpacingTextField");
			ivjGridSpacingTextField.setPreferredSize(new java.awt.Dimension(35, 20));
			ivjGridSpacingTextField.setColumns(3);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGridSpacingTextField;
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

			java.awt.GridBagConstraints constraintsGridSpacingLabel = new java.awt.GridBagConstraints();
			constraintsGridSpacingLabel.gridx = 0; constraintsGridSpacingLabel.gridy = 1;
			constraintsGridSpacingLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGridSpacingLabel.insets = new java.awt.Insets(4, 10, 4, 4);
			getJPanel1().add(getGridSpacingLabel(), constraintsGridSpacingLabel);

			java.awt.GridBagConstraints constraintsGridColorLabel = new java.awt.GridBagConstraints();
			constraintsGridColorLabel.gridx = 0; constraintsGridColorLabel.gridy = 2;
			constraintsGridColorLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGridColorLabel.insets = new java.awt.Insets(4, 10, 4, 4);
			getJPanel1().add(getGridColorLabel(), constraintsGridColorLabel);

			java.awt.GridBagConstraints constraintsGridColorButton = new java.awt.GridBagConstraints();
			constraintsGridColorButton.gridx = 1; constraintsGridColorButton.gridy = 2;
			constraintsGridColorButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGridColorButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getGridColorButton(), constraintsGridColorButton);

			java.awt.GridBagConstraints constraintsGridSpacingTextField = new java.awt.GridBagConstraints();
			constraintsGridSpacingTextField.gridx = 1; constraintsGridSpacingTextField.gridy = 1;
			constraintsGridSpacingTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGridSpacingTextField.weightx = 1.0;
			constraintsGridSpacingTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getGridSpacingTextField(), constraintsGridSpacingTextField);

			java.awt.GridBagConstraints constraintsVisibleCheckBox = new java.awt.GridBagConstraints();
			constraintsVisibleCheckBox.gridx = 0; constraintsVisibleCheckBox.gridy = 0;
			constraintsVisibleCheckBox.gridwidth = 2;
			constraintsVisibleCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsVisibleCheckBox.weightx = 1.0;
			constraintsVisibleCheckBox.insets = new java.awt.Insets(4, 10, 4, 4);
			getJPanel1().add(getVisibleCheckBox(), constraintsVisibleCheckBox);
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
 * Return the VisibleCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getVisibleCheckBox() {
	if (ivjVisibleCheckBox == null) {
		try {
			ivjVisibleCheckBox = new javax.swing.JCheckBox();
			ivjVisibleCheckBox.setName("VisibleCheckBox");
			ivjVisibleCheckBox.setSelected(true);
			ivjVisibleCheckBox.setText("Visible");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjVisibleCheckBox;
}
/**
 * Comment
 */
public void gridColorButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	final javax.swing.JColorChooser colorChooser = Util.getJColorChooser();
	JDialog d = javax.swing.JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
		new java.awt.event.ActionListener() { //ok listener
			public void actionPerformed(java.awt.event.ActionEvent e) {
				getGridColorButton().setBackground(colorChooser.getColor());
				lxView.setMagneticGridColor(colorChooser.getColor());
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
public void gridSpacingTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	try {
		int size = Integer.parseInt( getGridSpacingTextField().getText() );
		lxView.setMagneticGridSize( new java.awt.Dimension(size, size) );
	}
	catch( NumberFormatException e ) {
	}
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
	getActivatedCheckBox().addItemListener(ivjEventHandler);
	getGridColorButton().addActionListener(ivjEventHandler);
	getVisibleCheckBox().addItemListener(ivjEventHandler);
	getGridSpacingTextField().addCaretListener(ivjEventHandler);
	getDisplayPointsRadioButton().addItemListener(ivjEventHandler);
	getDisplayLinesRadioButton().addItemListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("MagneticGridPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(342, 266);

		java.awt.GridBagConstraints constraintsGridSettingsPanel = new java.awt.GridBagConstraints();
		constraintsGridSettingsPanel.gridx = 0; constraintsGridSettingsPanel.gridy = 0;
		constraintsGridSettingsPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsGridSettingsPanel.weightx = 1.0;
		constraintsGridSettingsPanel.weighty = 1.0;
		constraintsGridSettingsPanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getGridSettingsPanel(), constraintsGridSettingsPanel);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 1;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsDisplayAsPanel = new java.awt.GridBagConstraints();
		constraintsDisplayAsPanel.gridx = 0; constraintsDisplayAsPanel.gridy = 2;
		constraintsDisplayAsPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsDisplayAsPanel.weightx = 1.0;
		constraintsDisplayAsPanel.weighty = 1.0;
		constraintsDisplayAsPanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getDisplayAsPanel(), constraintsDisplayAsPanel);
		initConnections();
		connEtoM1();
		connEtoM2();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		JFrame frame = new javax.swing.JFrame();
		MagneticGridPanel aMagneticGridPanel;
		aMagneticGridPanel = new MagneticGridPanel();
		frame.setContentPane(aMagneticGridPanel);
		frame.setSize(aMagneticGridPanel.getSize());
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
 * Set the states of all the buttons, fields based
 * on the magnetic grid settings in the view.
 * Creation date: (1/22/2002 11:40:59 AM)
 * @param v com.loox.jloox.LxView
 */
public void setLxView(LxView v) {

	lxView = v;

	getGridColorButton().setBackground(v.getMagneticGridColor());
	getGridSpacingTextField().setText( Integer.toString( v.getMagneticGridHeight()) );

	if( v.getMagneticGridDisplayPolicy() == LxView.GRID_LINES ) {
		getDisplayLinesRadioButton().setSelected(true);
	}
	else
	if( v.getMagneticGridDisplayPolicy() == LxView.GRID_POINTS ) {
		getDisplayPointsRadioButton().setSelected(true);
	}

	getActivatedCheckBox().setSelected(v.isMagneticGridEnabled());
	synchButtons();
}
/**
 * Creation date: (1/22/2002 12:14:55 PM)
 */
private void synchButtons() {
	boolean enable = getActivatedCheckBox().isSelected();

	getVisibleCheckBox().setEnabled(enable);
	getGridSpacingTextField().setEnabled(enable);
	getGridColorButton().setEnabled(enable);
	getDisplayLinesRadioButton().setEnabled(enable);
	getDisplayPointsRadioButton().setEnabled(enable);

	lxView.setMagneticGridEnabled(enable);
}
/**
 * Comment
 */
public void visibleCheckBox_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
	lxView.setMagneticGridVisible( getVisibleCheckBox().isSelected());
}
}
