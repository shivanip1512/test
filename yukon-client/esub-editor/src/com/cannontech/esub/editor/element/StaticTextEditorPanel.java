package com.cannontech.esub.editor.element;

import java.awt.Font;

import javax.swing.JColorChooser;
import javax.swing.JDialog;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.esub.editor.Util;
import com.cannontech.esub.element.StaticText;

/**
 * Creation date: (1/22/2002 10:23:18 AM)
 * @author: alauinger
 */
public class StaticTextEditorPanel extends DataInputPanel {
	private StaticText staticText;
	private javax.swing.JColorChooser colorChooser;
	private LinkToPanel ivjLinkToPanel = null;
	private javax.swing.JButton ivjColorButton = null;
	private javax.swing.JLabel ivjColorLabel = null;
	private javax.swing.JComboBox ivjFontComboBox = null;
	private javax.swing.JLabel ivjFontLabel = null;
	private javax.swing.JComboBox ivjFontSizeComboBox = null;
	private javax.swing.JLabel ivjFontSizeLabel = null;
	private javax.swing.JPanel ivjJPanel11 = null;
	private javax.swing.JLabel ivjTextLabel = null;
	private javax.swing.JTextField ivjTextTextField = null;
	private static final int[] availableFontSizes = {
		6,8,9,10,11,12,14,18,24,36,48,60,72,84,96
	};

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == StaticTextEditorPanel.this.getColorButton()) 
				connEtoC1(e);
		};
	};
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
/**
 * StaticImageEditorPanel constructor comment.
 */
public StaticTextEditorPanel() {
	super();
	initialize();
}
/**
 * Comment
 */
public void colorButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	JDialog d = JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
		new java.awt.event.ActionListener() { //ok listener
			public void actionPerformed(java.awt.event.ActionEvent e) {
				getColorButton().setBackground(colorChooser.getColor());				
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
 * connEtoC1:  (ColorButton.action.actionPerformed(java.awt.event.ActionEvent) --> StaticTextEditorPanel.colorButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.colorButton_ActionPerformed(arg1);
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
	D0CB838494G88G88GCAE1B7ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBB8DD4D45719A4EB52E463C94CAE5B30E95A43B6EE57EEEC4AB66C095B10DD13E353F463C968D9BDF5D31A45CD523A29EDE86AEE689E1B5E998605E1D08CBF0BCA9492FFC047E19064E7C465E7D408A8C4D0B0C16041BCE0627CE566A198C1763BFF6F3D19F98FA15BAC67FC673D7B7D5E7B5D6FFE773B778D28E0EFFC2641D80010A193313FBA83C24B9B913A672D348D9C17FA40ECC62A3FB2009D68
	3923A6F82681ADF158EC4EC30F16E5C15BF450EC368BE6737F40FBBEDAD6D66A00979DBB698D88EDDC7B1FA9F35B795B1B5C4ED652FE7A26851E5BG32401695E06AA0363F76E6B143F7C1FB9BFB47E0B0A2D4D30EF94E96962B70C90CAF8BE8A5G7BAAB0CF5A4D92C61F86DCACC0AA4026F24AEB811EEC523B3B9C750C2F7F41D834691FF30C395816CABF87CF8515DD421E25684F09A3B2D0D36894BC9B3B2E0D1D9C71FBEDA3024F6777C9624838CD8CCD8C5BC417DB7287ED22C7740ABE496168168449BD52
	6E954644530454A578C48FC207F03F3D05D9C4A7C24F031D458477FAA17615935E7B8124C33C6FBF4EEDE4FD0B3D960BD29FFD4720577764C3344F1B8EA9FDDF77549B462507317E1D5396A6D783ED8FG9D574103A797BE2E9EF1CA8A9B969D17B04D473561B09F5756FCEC3754EAB65783F4D9B97EC5029F07F68CC01C150F7744C1BE5E3E1B0F2274E11BF13B4EF8932CF41C6B2C4AF82D79D7CCBBC96C554E6078A9B882F681BAG82GCBG92GF613187F4D4DC370EC9E16DA7DDE2F5F579C8834F8DC4173
	FE176801F70783A88CF7D19C7187DD8851388E96DAB57A436179A361B10D010E2D9771B8DCFE71E1A4C97B0C199AE3CBDC1BEBCCE1BA520EA8E3AB35E13FDDA942FE4B03F78373B5450F903CCB0527737ECB92D738AD81ED1D0D7B39EF9B6FCB0769FE14D7E5B7EE5568CB5C337B0D0D15348FBD15CADF280F73E6310FE3G7F88C0A240EA000DG96BB777171696A5B79389D62AD6893BC5D934381A1A835CC38BDAEB148FC3DF7FAAEDFEB8115BB32E0B96A17E5556AB9CAF922D253AFA94B8A0C65F66A0FA63B
	6297EAFBD4577610BDBAB63649F3F8132C89499EB937F7917CA2FB645C666673390D85DABAG65EFA940787E2AC8BD9D846F290A5433C656B38D34D4GBA3F0FA31CDF1DC74D66A100A900A5G89G2B81D20F72B93EFB3AF21E6B28CBB53FD47654CC91BCA11F8405F6DFC892FCA362E5A1689606BDE288B11E36996C5761B03973E4FD0F000D3322CF8CC2D6727BC03B6095A118C21467724C8E4C93948935476D954100B700385E0119FC6C8FA1A4DD8A3884C9D4B8A8FDF4864744F561B2E8848570EE2F467E
	3A1764EF846FA5555CD7BFB5E01C846D45555CAF6BE75E0567B988EE5163F0B85DA3389BC270860F5141A23CDF46D773B5796BBE9EFB4E5C3FC45635B64D58A3749A9AC77DB5113167A6BA975570BD78B705FD8BD88338AF8BDFEB6622055848D8603A247EED4F40B13A94463592A035067B3864D6A90D3D96A1A472E86B8CC22008AE456314F767AC5ECB446B3013B9450068F341CB084DF569EC36920723FEB9C68E90BC4ECB2D90A8A199EF1D5DC17120C39D551CDEC36802A4855D43931258ADCA125BB72648
	B7123E0442D2DE080D751C890FB59D4618F78C5674B1BE569F4C662B6229436F3FB6916841AB0545658C59FFEB003F63981F7FBB72F1BC846476FD242D50737355F3077996B2B7730B8FEB180D054E7F7CEC349560F1996476C5520E154779B2690356DA7A6375613744469FFBFC9CE88C2CC07AB17CCFC8BB06FF0034E37885B4F70C7E8B520AD1CAFB99615A45BF69D3457C3FA11C6FC67CBE1FC8961EAC02E82D796B9EBC8E374FAD753ADDE322546D7EDCC46835E3B8F663C9CECD027764E3BC9FCEFE407361
	3E4F7F822D9C4C562C17627E3A5CD8DECB633631D6490734CF29863C16D87D5A6A1F70C9947FF3122F99BE14C1F0872B9E4232652DFE0FBF58B2A1C9FE1FD5A0A33151C717989C75873D222B7CD3613AE093A6A59B1989DBB3A11F466F9525C70E1CAC552C132DA30A624166880D9D7AAA0FD7DC2E30335CF0AB136C6DB04EG40BD35BCB78C9BB11D678616GE1A09EBA95C05BD80B7D7CF0D1014AF7713574DC2644D7E7C69E1EF37133899136E21CDC4E09B778FE1A71EE846D326019D07669010C9CF8FAD4F9
	0962BFB2E94FC12F892F51EB327ECFCCDB905EEF29CE9AB389825699883A3D1046F122E5674358E9757930268E4E96B08E8FBCE36A7859A6798A2EAD740EE2FD75D84F1F9470B3E49C48A5GA4B3DDF97554AFCB895F1372192DD2463DC8F0EF4262FF48446B4773F96A3326ABD426993398B6B07D1B6BA36B6AA736E03DA58DB0B78DFCFE571A54393FB994720F38C9D943FC3CA8D39D4F94F7EFA6BE240F3A03A12831DD62941F2787B2F1DFF15D43504C570F9DBE01226BB86AB43E3536CE12106C7137C26955
	86D35DAAAF774BEEF192F1BF4C1CD11FDB13369799F3F47C5054C047BF542078E171775EB052BEED9D4435345679F4EC42ED1B105C383292A4C8F80189094EACB9DF3EC376C22CA33E011FB98FA19C076B201D861065B01BE97D3B1C5445183794F0FDGF42D7C61B375DAD9827895GA9GB4DF64979E4579F34199027B7B3F279CA4DFFE512440FD7D4D7CA65647550F5F8F15455B862DB373BD5B8D46B48775F171F1455763FB36B9278158997528BAFED726966A462F0B6918BA9E993F8FE5E15F4782FE59F1
	9E3F66ADBBD4718B67E9A87547A0C3487B5A168C1A8BD8BBE9CBA64F85085700EB3271FCA4014EF5GE9GD90DE6F3F9A337513A7536E74D53D3F31CB5B3E6A24FEDED0DCABD545F98DD9B51F355B7C8DC859A6939AAD346FF1B60972970B45E6C0847DB9C50B6B7729A65FB85117607CE045B1C0EE8D33915D1F2FBA25A7191F2B47E36F76F64EB8B475914ADB4897B3F4371A10DC6446F621EB12BF36D0A93343ECC01E79AGDDEF3541AC367633CE72B351219C1CDB1B205D8FA081AC81C884C88658F4124F5B
	1FED554ABB61732617F4143A1B5A7C54027BD1F04AEC5EF70A6BFFF3EB2EAA763A7CEE1FA4475DA3D9F84F71875DE3EE9F477D95410D78032438213814EC7D3A6B55EC6D3A6BBF32356B2EB73367BEBB3C1D2DD71751BAFF578C2973E1FCEDG947776AD0C73C0FB51A99ECFCB9146AD0376CA9977DD02DBF30A66B20A2BC56A7A9C73C64679B28D5C42EA634EB09B199AFC7F173A79BDA27F23226D442F211098A28103B786C67B05D408B4EA538B02D7E436E13E8A32994FE9495FDAA52F135FD694EB2F9355FE
	73BBB35FEF52CF717DA64D0657393D896689E028097B7BF8102F57A940ADEE223E0DE74F4426E8DF33B3E50EFE8C9E4F510E41669C9C7BA17FC4F0C40C0C33361CE219262C28F6C974E237305A0D48CBC2906A75C879BEA2CFE9FA72C24E5CE78C3F856746A9399DA26D9B721CD44D60356891EE78A7E05156B4E33F364D70FC74BC78AA95A08B70A54DB4AF51B97B3BC9DCEF0C8685AF186B8E08A3A82F854BEE1B653253403F84A081A099A02D196F6B1D6731EC075BF72D47CF271862FB7B719C53D92F2A2239
	F1BDD267463E965A07B8F8AEEF61737D0EB9B2BF2FEAD166F8DD0B5E3E92D30963E7E38B5DBFF6487887893E38D54153FD653FC83C613693E8492DBCB7DCA439395FE9B6CFB9B9EE15876396C3BBDE466D74E2DC8234D74A384F3DBC2F2CF672FAA9B0433FA5141CB61BFBCEF31FD78D152857892EDB9C0E33C1372BC5986B20B3C856458A123F307C6253FC2D7C1C68DC856D9400CD277999F06AG2FA317FF6BFE5491D329796DA6352748E8B9C37DE8BF2354A34579D7CC54E64DAC3769817A52B398B7BC2B2E
	6F709C5FG869B76A72147065131DF1FC2EA7AF3E1F45C5FE70CEAFAFA989D7B280A4953313D301F0FED738B0FA04F5F3F277BDDEAA39BD35ED976346DC24E49C166F72BD00396708DC79D5844B7BB8D7CEC53F6164F47B6B31E0F98E84781A41C6571D01803F91F07F60A0C0B357098C1EDBC666F2EAC05E797344781E2G62G92GD683ECGB035F35E0788EF89349B817A5B69B9EC9A1E0FGA48124483C1BEDB19A7E0984EED30F0EED737755AA3EB856B75E6EE2FD565E5376D64E27AF190CDF0F3EC3751E
	9B91CF5BA362B3C7C31E66A6F7904B86026228980413BF3DDAC1B59F627939132C7F4DB0662C8FF90E7B29876755E1FF10A474A4427716871F4727E41E86BBEECB302920F378FBEFA2BE022229F3A487F91D4C3E73DC34FFB25818B39874818F065D2C6DB9C7F317F2DFF7BD2A763E9DD039C5DFD06E69854B515C3B6296DE97ABD57D2778E788FE358ACFF365C6B9D72E83DAE3872F0D9FAA407BC6BC3413G56GEC84301C07734CF9DE3726DB6C68B6F5F10FBF50818501C7D5C65271B54CFE316FDF4FEC65B9
	A9717BDFC5AB6E7E19C16F5C5EF81EFD03E04FE556DD844FEA7FA2BC3E45C0DB86309A60B500F4003C8BFCFCCD39D6BABEFC4550ACC94248B8393D600343978A8A9AEF62F4ECFB8CF30DED5F853D7D74CE12536AAFC44E5DFD846F3C90B9F785E6BEF7DDC0CB3C405705B5A73AA6E94D61F5C0F1A72DC96C70F4F652BA1B2E1947ACFC4D74F7EA2F99B58400E791C0A1EB47F5AA7D237D483805676C8C647A8EE118FC06277838F28B43C3608C7BDB7CD3947F0A975F87115BCDA6C071A74E65F0BCBB79D1FC750E
	DC26872713F09BCF9E4FD351426DFC65A3AC07AFBFB11CF10B9E17ACD711154DE8DC9F3DEBF929D23DD7AEE5FEFB3E135F455C0B547494C6B7F5F17ADDF5DA7245B25DB82DC5374A748F873474B74974674268BDDD14BEAD539F2855523F8468B4F7FE00430379609FBC78FC6E93A7CB656B9D279BC29CFFE4AC2FBEE6ADBDFA34022F4778C975FDCC3DF9376EFD4C52AE9AD3AB58F3FAF0973317652DD359E347979B4D7DB628864496722ADCD911D0308DFB0411EB952C9FA7C4F5BF6CB7DFB7E46874A30D59C7
	9FA97D20BADE7AC42DA36F61CC23C1C7071D49B6FE2454F234D6F9564B4FD981206DA563C364CCAB7F91FFFFF797FE25F657DBF8CEABF96C81647C494F8C9F6858DD466C252B6D0EB518E85E7947DBD89BFD7F216A7D2D1905FC7FDCA8D09BB9373ECC9BB49F4797681FAF7F26C07BFC791D826DBB0EA78A66BE77BDD5207FED6959827537A53A1FBC379B2FAF7BC5B8DF8184G968144DD64FBC9D7617B086CA59A7B48E95798BE5F62DF97C85CFEF5117ED89DC5377FCDC2EB914E2543FEC817DE2C1F635B0B70
	FCC97EGC1D25C05A26C2320FBECDC222C8C5FD3C46AE6F11420E9ADFD4C40EB69440B3C663D472C445A032A77DD66AFBF6E2A3FD49BF41F3D164377D9D3B754C85D7CCE71099CFEE6133A7939E4BD61DD8E6DD5GA9B2EF0A056705CD5DDAF9A16D14960E660ADB780A085F750C6A5C75506D0A585AE267362CBD7C6CBB66592DAD4737C6769B3915081F35DCBD3C6FC9F5DC5FA3B22EC17EFE31C6465DB140E5AD17385D7D65A55AF6152D17487EDE16F5DE627A1E2B65B626AF713A74709E0CCB04F672A53E36
	460B8F43733270E97497A1C0F6857D81B1A85D20D872C5102D5983FB496FC1E5BD5FA9FEFF8EBD78EE379B5205EF0C2F7922D2A42F156C1D7B75E4E105FF88C36F9C7ED49C11FA04B1B9F6FF68D55709264BD08FDC1E7B9E135AEDAE43F51A95F85BGBCG0BG62G92GD6812C835888E06AB51BCBG6CGCE00BE00F100E900A53DFC5C1BF6D54CB1EEF6C1C292D4D0D05FCD3F38DBBFC72D5DFD7B9C754AEE72CD90F2CB4F0DG3B0FFBF5B7396F9E9D8D09F2AE5C30BBCF4635073A7CA1B73E4723B5EBE7B6
	2FD91776C67B0E6F5F19AC36D6G4F6ADE7A1BE25CCE6D0DBC67FD1673454EEB33DFD0EE45026568196012055C3D75C61E958688BE3DB7722C708BA50775C18C74715C3D5E22363F9A7029FDF347A05B6F6DDA7D5E44E4332E685DC99CB64EB55E92DD39E76A34646C0C5F29ABF7D273370EFD0C5F23ABF77740DC7D0C5115B38E4EA5972BAB77C22D165CB2461F24A1C75706FD8FFE176B981A5E982DEE8F2EC9706FD95C02477DB9799CCFB398259F5A0B57DA0865B43A3F3FC26A5764ABB4FEA86ED5025B40F0
	D476D2197E5A6FAF3B7D5A974B346B13EBE55175C920CC3BBE39DE262ECFA86EE699DE6BE310337AE43E9899F7C5465DC5F4E2DC2F5BA50D4B787BC83FB03ECD4486B97EC1522FC9026BE3382F2AF0D7986EEBE55827B064F13F7CEDAE3E8C6FB323EE0F073617929E378F52196C0FEF13BE3A83022A5F0F4BB85CEF3AA746E473BD517471FC6B88444E9C6F61E3850B37CFAC4A3BCB757E2B1CAF3FE65BE279B2ED5018CD37C447716FAD6AB82638776D7C1BC263477C3E78ABF6DE6336493896A347454938F5F5
	DCF6030CEB2867FC1BE45CA5D2ABB9BFB11BFBBE61386FCFF33EA1997775810ECB10F1BB87653B6BFE0E7BF7D297F5C1FBC8461D2D63FC1EFEDEFB8987ACC8DE7FA44CA93EE4AAD74173B0E7F3F3BC6CDBD09268CA6E1FFBBF203155348F3F52BD799BF63EA73F26A14B6F07A26FDE7919B73DDF593B283EABC62E4FF4757677D245D7153BDCD283DFD574513ECE6DD77A5AD54FF557EB6866F501DEDFBB2EC65EF9F926436F243FF6C0317563E9EE2BCF47D6264A1687F8B0F0DBB1D7A37D12B84075AD56710BDA
	DFBC53477D1298254FBE48752D5451271E3BB5403361AA3D7F437AB6C77962F735613ED8F3D07145C09D3715B520DF37F1DB9B18AD6E0B128145967B06E8666B7DF58347751A79FA6F4506C97EFCC9BE9B158F703B2F374B11CC7FD3A3278F8DE867D7AA7364A1C5660EFAAE93A76BFC7930C2B74EF0FA3226CE9E1705C79419578738CC5660DC7D6832A9B242A017E9521151371DD22968396460FA268679F87E002F7AD90ED893D67BA5G4F2A4179540C8F0455FE6BC0E6633C646E2ED74B21A13339E0E8BEF2
	DB433E211403CC633C645E882B7D3AC046B5AF3951305AEF8AE4964FCB6EE307DAAE9EE492EFA3C7637B177668BDAE4D9E3D47FD4B984D7718B11A6F6B7551FC5F2C0F665BB3934D7741CCB41FE1B01AEF4BE0B45F0C865F1D9AFC1FB922792EBB54FC74FFC58FD6CD617FBD7D954A04FDAD94C46FC6DFE3B75F7F2FBFD9BE68ACB0A2E3BB5AB2CE7640F614B909B749A97C9FB838027DGE4374B326D24DECB3DDE6D48F875518CA3B201A02DDF5B11899721A09F24FAB220FEA5DACCEA88FD06EE11ECB68F2C04
	B271378B070436104BEEF849449FB270935F1CB896226C7F97CB6F34ABE655F76969F7EDB668FD97C89FE67F8FBB9279BB4D272B715C36815EB5426FAAF22BF112543BE769768A9E0FA4F82EB57BDC784E256506A4AAF73A4FD6E37DA14E4308147623EA5C6FCFD5B27F8BD0CB878881B912FA1194GG54BCGGD0CB818294G94G88G88GCAE1B7AC81B912FA1194GG54BCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG4B94G
	GGG
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
			ivjColorButton.setPreferredSize(new java.awt.Dimension(35, 22));
			ivjColorButton.setBorder(new javax.swing.border.LineBorder(java.awt.Color.black));
			ivjColorButton.setText("");
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
 * Return the FontSizeComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getFontSizeComboBox() {
	if (ivjFontSizeComboBox == null) {
		try {
			ivjFontSizeComboBox = new javax.swing.JComboBox();
			ivjFontSizeComboBox.setName("FontSizeComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFontSizeComboBox;
}
/**
 * Return the FontSizeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFontSizeLabel() {
	if (ivjFontSizeLabel == null) {
		try {
			ivjFontSizeLabel = new javax.swing.JLabel();
			ivjFontSizeLabel.setName("FontSizeLabel");
			ivjFontSizeLabel.setText("Size:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFontSizeLabel;
}
/**
 * Return the JPanel11 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel11() {
	if (ivjJPanel11 == null) {
		try {
			ivjJPanel11 = new javax.swing.JPanel();
			ivjJPanel11.setName("JPanel11");
			ivjJPanel11.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsFontLabel = new java.awt.GridBagConstraints();
			constraintsFontLabel.gridx = 0; constraintsFontLabel.gridy = 1;
			constraintsFontLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsFontLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel11().add(getFontLabel(), constraintsFontLabel);

			java.awt.GridBagConstraints constraintsFontComboBox = new java.awt.GridBagConstraints();
			constraintsFontComboBox.gridx = 1; constraintsFontComboBox.gridy = 1;
			constraintsFontComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFontComboBox.anchor = java.awt.GridBagConstraints.EAST;
			constraintsFontComboBox.weightx = 1.0;
			constraintsFontComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel11().add(getFontComboBox(), constraintsFontComboBox);

			java.awt.GridBagConstraints constraintsColorLabel = new java.awt.GridBagConstraints();
			constraintsColorLabel.gridx = 0; constraintsColorLabel.gridy = 3;
			constraintsColorLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsColorLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel11().add(getColorLabel(), constraintsColorLabel);

			java.awt.GridBagConstraints constraintsColorButton = new java.awt.GridBagConstraints();
			constraintsColorButton.gridx = 1; constraintsColorButton.gridy = 3;
			constraintsColorButton.fill = java.awt.GridBagConstraints.BOTH;
			constraintsColorButton.anchor = java.awt.GridBagConstraints.EAST;
			constraintsColorButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel11().add(getColorButton(), constraintsColorButton);

			java.awt.GridBagConstraints constraintsFontSizeLabel = new java.awt.GridBagConstraints();
			constraintsFontSizeLabel.gridx = 0; constraintsFontSizeLabel.gridy = 2;
			constraintsFontSizeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsFontSizeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel11().add(getFontSizeLabel(), constraintsFontSizeLabel);

			java.awt.GridBagConstraints constraintsFontSizeComboBox = new java.awt.GridBagConstraints();
			constraintsFontSizeComboBox.gridx = 1; constraintsFontSizeComboBox.gridy = 2;
			constraintsFontSizeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFontSizeComboBox.anchor = java.awt.GridBagConstraints.EAST;
			constraintsFontSizeComboBox.weightx = 1.0;
			constraintsFontSizeComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel11().add(getFontSizeComboBox(), constraintsFontSizeComboBox);

			java.awt.GridBagConstraints constraintsTextTextField = new java.awt.GridBagConstraints();
			constraintsTextTextField.gridx = 1; constraintsTextTextField.gridy = 0;
			constraintsTextTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTextTextField.weightx = 1.0;
			constraintsTextTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel11().add(getTextTextField(), constraintsTextTextField);

			java.awt.GridBagConstraints constraintsTextLabel = new java.awt.GridBagConstraints();
			constraintsTextLabel.gridx = 0; constraintsTextLabel.gridy = 0;
			constraintsTextLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTextLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel11().add(getTextLabel(), constraintsTextLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel11;
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
 * Return the TextLabel property value.
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
 * Return the TextTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getTextTextField() {
	if (ivjTextTextField == null) {
		try {
			ivjTextTextField = new javax.swing.JTextField();
			ivjTextTextField.setName("TextTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTextTextField;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
		
	String link = getLinkToPanel().getLinkTo();
	if(link.length() > 0) {
		staticText.setLinkTo(link);
	}
	staticText.setText( getTextTextField().getText() );
	staticText.setFont( getFontComboBox().getSelectedItem().toString(),
						((Integer) getFontSizeComboBox().getSelectedItem()).intValue() );
	
	staticText.setPaint(colorChooser.getColor());
	
	return staticText;
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
		setName("StaticImageEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(352, 188);

		java.awt.GridBagConstraints constraintsLinkToPanel = new java.awt.GridBagConstraints();
		constraintsLinkToPanel.gridx = 0; constraintsLinkToPanel.gridy = 0;
		constraintsLinkToPanel.gridwidth = 2;
		constraintsLinkToPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsLinkToPanel.anchor = java.awt.GridBagConstraints.NORTH;
		constraintsLinkToPanel.weightx = 1.0;
		constraintsLinkToPanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getLinkToPanel(), constraintsLinkToPanel);

		java.awt.GridBagConstraints constraintsJPanel11 = new java.awt.GridBagConstraints();
		constraintsJPanel11.gridx = 1; constraintsJPanel11.gridy = 1;
		constraintsJPanel11.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel11.weightx = 1.0;
		constraintsJPanel11.weighty = 1.0;
		constraintsJPanel11.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel11(), constraintsJPanel11);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	Font[] fonts = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
	for( int i = 0; i < fonts.length; i++ ) {
		getFontComboBox().addItem(fonts[i].getFontName());
	}

	for( int i = 0; i < availableFontSizes.length; i++ ) {
		getFontSizeComboBox().addItem( new Integer(availableFontSizes[i] ));
	}
	colorChooser = Util.getJColorChooser();
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		StaticImageEditorPanel aStaticImageEditorPanel;
		aStaticImageEditorPanel = new StaticImageEditorPanel();
		frame.setContentPane(aStaticImageEditorPanel);
		frame.setSize(aStaticImageEditorPanel.getSize());
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
public void setValue(Object o) {
	staticText = (StaticText) o;

	getLinkToPanel().setLinkTo(staticText.getLinkTo());
	getTextTextField().setText( staticText.getText());
	
	for( int i = 0; i < getFontComboBox().getItemCount(); i++ ) {
		if( getFontComboBox().getItemAt(i).toString().equalsIgnoreCase(staticText.getFont().getFontName()) ) {
			getFontComboBox().setSelectedIndex(i);
		}
	}

	for( int i = 0; i < getFontSizeComboBox().getItemCount(); i++ ) {
		if( ((Integer) getFontSizeComboBox().getItemAt(i)).intValue() == staticText.getFont().getSize() ) {
			getFontSizeComboBox().setSelectedIndex(i);
		}
	}
	
	java.awt.Color textColor = (java.awt.Color) staticText.getPaint();
		
	getColorButton().setBackground(textColor);	
	colorChooser.setColor(textColor);	
}
}
