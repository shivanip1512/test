package com.cannontech.dbeditor.wizard.device.lmgroup;


import com.cannontech.database.data.device.lm.LMGroupGolay;
/**
 * Insert the type's description here.
 * Creation date: (2/19/2004 4:54:55 PM)
 * @author: jdayton
 */
public class GolayEditorPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JPanel ivjAddressPanel = null;
	private javax.swing.JPanel ivjTimeoutPanel = null;
	private javax.swing.JLabel ivjHyphen1 = null;
	private javax.swing.JLabel ivjHyphen11 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjOpAddressJLabel = null;
	private javax.swing.JTextField ivjOpAddressJTextField1 = null;
	private javax.swing.JTextField ivjOpAddressJTextField2 = null;
	private javax.swing.JTextField ivjOpAddressJTextField3 = null;
	private javax.swing.JLabel ivjNominalTimeoutJLabel = null;
	private javax.swing.JComboBox ivjNominalTimeoutJComboBox = null;
	private javax.swing.JLabel ivjVirtualTimeoutJLabel = null;
	private javax.swing.JComboBox ivjVirtualTimeoutJComboBox = null;

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == GolayEditorPanel.this.getVirtualTimeoutJComboBox()) 
				connEtoC4(e);
			if (e.getSource() == GolayEditorPanel.this.getNominalTimeoutJComboBox()) 
				connEtoC5(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == GolayEditorPanel.this.getOpAddressJTextField1()) 
				connEtoC1(e);
			if (e.getSource() == GolayEditorPanel.this.getOpAddressJTextField2()) 
				connEtoC2();
			if (e.getSource() == GolayEditorPanel.this.getOpAddressJTextField3()) 
				connEtoC3(e);
		};
	};
/**
 * GolayEditorPanel constructor comment.
 */
public GolayEditorPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (JTextField1.caret.caretUpdate(javax.swing.event.CaretEvent) --> GolayEditorPanel.fireInputUpdate()V)
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
 * connEtoC2:  (JTextField11.caret. --> GolayEditorPanel.fireInputUpdate()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2() {
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
 * connEtoC3:  (JTextField12.caret.caretUpdate(javax.swing.event.CaretEvent) --> GolayEditorPanel.fireInputUpdate()V)
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
 * connEtoC4:  (TimeoutTextField.action.actionPerformed(java.awt.event.ActionEvent) --> GolayEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
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
 * connEtoC5:  (JCheckBoxRelay1.action.actionPerformed(java.awt.event.ActionEvent) --> GolayEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
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
 * Return the AddressPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getAddressPanel() {
	if (ivjAddressPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Addressing");
			ivjAddressPanel = new javax.swing.JPanel();
			ivjAddressPanel.setName("AddressPanel");
			ivjAddressPanel.setPreferredSize(new java.awt.Dimension(346, 160));
			ivjAddressPanel.setBorder(ivjLocalBorder);
			ivjAddressPanel.setLayout(new java.awt.GridBagLayout());
			ivjAddressPanel.setMinimumSize(new java.awt.Dimension(346, 160));

			java.awt.GridBagConstraints constraintsOpAddressJLabel = new java.awt.GridBagConstraints();
			constraintsOpAddressJLabel.gridx = 1; constraintsOpAddressJLabel.gridy = 1;
			constraintsOpAddressJLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsOpAddressJLabel.ipadx = 9;
			constraintsOpAddressJLabel.insets = new java.awt.Insets(85, 35, 63, 4);
			getAddressPanel().add(getOpAddressJLabel(), constraintsOpAddressJLabel);

			java.awt.GridBagConstraints constraintsOpAddressJTextField1 = new java.awt.GridBagConstraints();
			constraintsOpAddressJTextField1.gridx = 2; constraintsOpAddressJTextField1.gridy = 1;
			constraintsOpAddressJTextField1.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsOpAddressJTextField1.weightx = 1.0;
			constraintsOpAddressJTextField1.ipadx = 20;
			constraintsOpAddressJTextField1.insets = new java.awt.Insets(81, 5, 61, 1);
			getAddressPanel().add(getOpAddressJTextField1(), constraintsOpAddressJTextField1);

			java.awt.GridBagConstraints constraintsOpAddressJTextField2 = new java.awt.GridBagConstraints();
			constraintsOpAddressJTextField2.gridx = 4; constraintsOpAddressJTextField2.gridy = 1;
			constraintsOpAddressJTextField2.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsOpAddressJTextField2.weightx = 1.0;
			constraintsOpAddressJTextField2.ipadx = 20;
			constraintsOpAddressJTextField2.insets = new java.awt.Insets(81, 1, 61, 1);
			getAddressPanel().add(getOpAddressJTextField2(), constraintsOpAddressJTextField2);

			java.awt.GridBagConstraints constraintsOpAddressJTextField3 = new java.awt.GridBagConstraints();
			constraintsOpAddressJTextField3.gridx = 6; constraintsOpAddressJTextField3.gridy = 1;
			constraintsOpAddressJTextField3.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsOpAddressJTextField3.weightx = 1.0;
			constraintsOpAddressJTextField3.ipadx = 20;
			constraintsOpAddressJTextField3.insets = new java.awt.Insets(81, 1, 61, 82);
			getAddressPanel().add(getOpAddressJTextField3(), constraintsOpAddressJTextField3);

			java.awt.GridBagConstraints constraintsHyphen11 = new java.awt.GridBagConstraints();
			constraintsHyphen11.gridx = 5; constraintsHyphen11.gridy = 1;
			constraintsHyphen11.insets = new java.awt.Insets(82, 1, 66, 1);
			getAddressPanel().add(getHyphen11(), constraintsHyphen11);

			java.awt.GridBagConstraints constraintsHyphen1 = new java.awt.GridBagConstraints();
			constraintsHyphen1.gridx = 3; constraintsHyphen1.gridy = 1;
			constraintsHyphen1.insets = new java.awt.Insets(82, 1, 66, 1);
			getAddressPanel().add(getHyphen1(), constraintsHyphen1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddressPanel;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G06F9D9B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DD8D4473566BD5B7A9A4BA3A9C9E91EE92CE22A919352D0A5D193CC08A555B47E201204A8C9E8E27AB41FEDEC9FDFBE7BF979738FD0100DB1C6A3AA9AECF085C54485955865E7ADAA518D2252979776820B4B6E3AFB91B4B6F9674C5C39776E6E5DDD71FDA6DF8E774E191973B3F3664C19B3F765B4BBC7E72525EBB8AEAD0B937FBB1A46F106AFB96E3F0A7E321061E29E894966947F6D8248676E
	B3E642B39E6A0AD604E467F15B2E2E03F29214635E8B497E985E8B381DD7130C706207CFB454957EB5BCADB01F55650C4FDAD23E39B3871E9B81128117B9BAA43BE5B4723FFF67D691DF8C650D62BBC2DABA47657D925B2C2D5E2A4007096DD260198A90CD5ACC5A39DD2CEF03B236F142F378AFE95BD170DCCF247B012ED2EC57FE5FF8EE656FBECD4FC5DE8A7A8CD609FD8B4708CFEEAC99088C6E098D86F8D637DC6DBE51696853F61A6DF607DD60BBFB34168E5EE2959CAE6D00752659E551DA786B56CEDEEB
	6B6BF6B97A1DBADD2343E63ED1CF1A34186D3CADF48C4AFE20FA9D218B32C3F97BD8440D2B4671CA0072B2004C31CC7E393955EC9C2E0E671247EE74AB7F1531D46E61E3E57943AECECCAF0FC07A8B49FCE03FB828DF85C0E5792C1049B29841E419D501ED23209C8F10AC616FBF0A78D424B7AEA43BF89C13717867CC4636EF26F0299DA752B7791131E69C152DE71CACE3667B6732BEA1B6F3EF854EFB84544583A481F00F06E4978254GFCCAEC3561485F61D95BA1689DFDFD8EFB2D53D9E73338CEBBAC3C8D
	5EF5BA2891F16778CE074B42F1549E8FD517284843206DD1CFDBC4203ADD60187DC41EFB1C93047274AC955D22976CC83709B406BDAA6BB667D19C375A63B8EE2361BD0D6840F194FF1E60B53F1271F42EA692DB4032816A12FF454639EDA31325B9F38C17D7D6133ED6C51670670F245F9ACFE5889BAF4BC247182F44B10E85FC9240CA001C89A159A5G4D935898079EB99CEC0C0DE07FAE3B59F63E3F43E9F689F57DD61B05F709E31D34B350D82B816D7747A1772363B27E30F20E121EBA29BAAEC9630B52FB
	A608F3B4C19E974AFBCF21BF5E23A7785A46C6E98ECF12B591BD014EE10604B792FC1C82CF67F6AED9DBD8CE003A4A47C232E97BB515089F7598ED1FAD5151907CB4851E5289A774311C84F5B593C332697CE66850AFC6C0B996A091E085C0DEA44CB1C0CDA41B63570F9C3C4DF5542218DF5AB72CE28BBC79C141E5B65A5D02595E49371ADDD6F3070DF7F3E21B07ABF0DCBBBC7A0DA16B3B93F8B472F65EE5962C8EBBD0B77771E0CCEE5AE6E2C5BE36F1712456E46D6301C11F13E3F41F2A40F5E1B33B052F1C
	9633404BADE87D548A34096B66D620888460BDB4124C877139D7607DD6A49B2B52AA44A5C0F919B4AE95C7B743739FE05C3CCD27B3D8BBD18C336B8633514663C5E8F213C2328500D8007247C33223GCA1E88499E86308A1FD1EC4D3E5F46EC5310BB09ABD9F0C255B691B21F24F6367DC9EF5B3CC8F89AGEF81988E9081908710821079EB585B818C3FE6FB6F135521DE34D1A6141558FE143ABD2BC13928D720B6283B32BCCCEC4F4648F30DA77A4816AF56ED64284F9B4A5A4F8D52A65B2B3CC9FC627A4BD2
	6029BD7C0278DD8B0C63B000513FE6B6F16DE8A1DDABF5E6B74FD647D99798B6EF11AD043635D7A28F7EBAEF978C3C1337DB6045AD5AE6FF65FAF95DF4B9A41B7E0660510F6A41305D927E1E4AFC0A879A4AD5486A7F03541B8541E56D689778733CA0D86D5DF27F7B08ACEE8F976D96F59DCD761E9850F18EC032246B36E3858A7BEFF6B82E76BBCD383245F524A37142CAE81F739433377A23987B15CA653324AC57AF2AE89B423CEE94E98DED6FD8B1E49E99C3E8CBF964D4C2F04CB1FF1EA56257CB755BC8FD
	B348AEG0C820883D8A60D43AF0E367969BF9439834BB8057070266F3D8E6E94280D646A7C5B38C627EE635BF46AB6DE288BEC4345BAB59B26F587F52823E3402ED89365BA745F1D8E3B1DA78BD36A4251D87EFD936AE135DB058BD6CBB7AF1C375E64A1360FC65B3EC276888D3C97C7B37FBD3007796F221B9338582FBFD10D7F4247D5268F0B96E32EE859FFD3190ED461DA63EDFC9F8C145E51EF97A83E0A6CBFA25E1DC1F0B33E6C463E4527CCB0068DD65EE6A9AB69848FA0E849DF3A6995771A2F1B87356E
	8198AAAD19872D9EEB6B71F5FF610183A78A3DCF9EA5DDD697EF34BB7B99097D2D7BCBB94A730D01135EBC99535B26D5A6526AAD6C7126F5682EA9F008725C5864A0BCB196F0748B926792B3B181ADFD34702EAE072B0F379066DA7300A0F22EA555C3E4FDD854B76ADBA761FD56D9EB3138F8377BFB19B84AFB4D670D7EF89F3A53793B7F2CDFFD8E5F6DF9AC3EF21D70BE6368334259627B1C4EF2CE547F1DA2AAC32B55A574FF3FB29CE6F1487C9364CC81FEA49EA0B91A6D4DD32A301E6D4DE2G22F758EC54
	55C11D61B7641CFEDC2370CD2951B47F32D12AFFB6C87D9F0354EFB991383EAEC83DBDC87DC3C16AE786297F335FFABA0E9155F9382770577AF9B83AE0F0738F7E86BBFFD4EF762AEBB55B7A65F34DD35538175A94F19145BFFADC5D47C79E47D8682AC4BF6A789A8E4FA714A65D13224CC853693276C19809011E18877B32D019879B893AE14EECB6BC1381E85F8B5D280B37F90D1C0C7D4FD6329CD9AA0E8B60CAA7D39A3649F4BCE83B4D47D83BC4A82FC05C9435F63324F6C5D0DFBD459F3D6FF42CDDBB3491
	G42D469692BD83B8428DF86109363592EB9865B8DC879320392AE0460DE07G678719AC8FF0BACF196B33384BD477EA045098CABFA246BBBF322E86694681BEA1062D3B3F1FD0464435EE372353CA0E27625C3FFAC2390FD35C62939B60892B5CAD98A1C09AE47613F282E545732B0896EDE0629747B95F73B81DD2F664508F900FE2F26861885D84A62817421CD6ABBF4031F178D703B2E7B8ED533E74ECBF6330D454FF5D5372B844AFBF17C5E57A398E73C0EAF94FCEA224363B5F2A6D972CF8D0B68BE6624E
	0879F1B437B8151C9510D65353AC07F86F9EDCA743209C8E9085C0739903A44F01ED638147BD8367D93216276DD22E65CA40DF81988EC07D7512CA3409A1EECC6218476D5FCC67484B9333F19CD3D0A3721D7D78982E7A3B352AF94D911B7253431F2163977B0CBC0E217756E6C5GD07D2A0EB17DF22602EED319FE7A8EB57DEECDE57A0DB936C6520F65FED6CA34C221DD3CC46BBB139AADEE9A9B6371BA56CF833866E92C1F36D159EFB860A38166CCE3B2EC2ABA844F216FFCCCDEDB95631B82B437CFE7FC0B
	AF2B71B5CDE7FC675FB1DFB1F7FBC8698FE213777A7587BD53693C5D1A6E6D8F1655228D0783BEF2BA7387DF1F4CD77803F330824D76EE58892473D18D4A8FBEDFAC6757A0DF6273B936476F57236E31C0B389E0A5C04E33A159A54FB29E61B5C1F330750381F23031BE794C66E765B37515E7FD4F57B45F381A2CCF61D91AEF4C12709B0A90BFC20127F3FB5F9EB63723200EFB0E1DF53F2D7466EFF94EABCF64D5267DC67A7453FA15A33C7A51353C37F8BB73D50A252F534961AB5DD36B0A14FB6A3467E89EA3
	991EAB1FE3F7A29F6CE27BCDCEAC4B95FEE7C07A86A8DF81988488830881C8GC80DE57376EE0D1A9F771CB7FFCEDC4E6F503128BDF6E7E3F856271F7798EE1D61590F6AFD330369DDBA034D6307F7A843BADDB099FAD4E518ECE2B28C13E4F85C07566D49B0AD288CB1B354EC295B76293AAD411CF6B86A9C03E2FC46672B59529C2019B20359925F4874D9A569632A3AB3FD0679747356277CF935B17D7AB2132179F9E64F4F55A3EEB814431EE736EB2845FCAA73EBAD8E2BDD6018CF7BD3AD6EC38E17355BEA
	E7383F92DC2743C592B094D726771F9B2A542B6706CE6855F3C386FD607CE7335EDF6E0866AA971178A382740B8120388F495ACE02724A6759FCE495A1EE6B8B904F3E40F0F984077BA76EC9947733BD4A9CA336D70EF5698B3EBEF5A8C0F9C4F970704ED1E67DBF68A36CA0B4DF5A32090C2B5B4D3B09B34268CD2CFBD77808D3491F1DB177719CBBA37F06642B31CD3D605017C96B66037DDB55570D626E7BA699D70CC71BDEE0B161CF6BC8CC8865F000A8E98ED2F7853A8F0CD799EFB1AFEE70EF032186F59B
	FC5000366FF6743BBAF9EFBBFB5830D52A133D35D1607BF0D844739F69AF18DD5D3C0F1DCEA27DE91D3F7E138D7EEC190E7960B165189F126C653B0EC0E31424B2C614DE19CEC96F30C4EF2AA9903D95FE697D4103DEB947FC4F4379EC3EB36358FC3B9BF03E2B21EC82304531796EED8C44FBF81CBF5E932A143C0FC83AD8AE8722972EC20F4EC5DCBD46B803D2B91E14EFC87C5EAFC43F87A7EEC7BFBA4897D1272CDD4A3DA092E8A7GAC95F950757164G0EC3174B5C87D3FB5E49F7F2C924EF7EEE5637894A
	6DG37GC681CCFB1145421F7E9347CC4C491175C471BBC8DCA6262988DE340BA226FB514C106CCA0066190A788F5E05191E7A53B8AFDBCF623C19B44E4B15705B88BED20127715F4F495AA56D202E7A374C4FEE5A4D7C6CF88977E0874325CA38F9A606EB0AE7F6B0A61F499E8E38C80058F8D95684F8DF9A2FA67B3B442ED645D39957CB7815843F75A599CFE53FC170248CF561AFB1196EE9E4B22D12F05FDCE638665F315CCB59950F1893CFA3BADD234BEA29B3F7B7936B20EB608B3267A06FD13FE3EB6062
	AE723D8F141781E44EE2778D037B5859BD7251B1DC7330132A5F7BA41B7625EF1FC54720E616FCE6597A7E39AC4A73015D0C67AD280F1E053897F6AB4FDBE8BFF353E98CC16B7F62D10F7E88E385397E73BD4AFA14F73AC77F0B9E75B8C6E5E2FF2A5B2C3DCC37D53322B85BEF8E787F960B69B41BBE87DF5946255656E509F6C47610B62893E6337999F19AF1A320BCCA42C5B891978B6584893747A5592004DBC4EC3069E5086DDFE6B6182FA7BE9B4A21GD1GF3GD6GE47E1E35A9A4ED367E1E6656B0EF06
	B931CAA837C9ED9E293BA2527130D7110EBF5F94A57272D79F7A32DAFC788C61659DB3843BA7473E016E873DE30EC854175B375B9024CF068286214FC8003D62FC0F06777D62BB25B729A3903D75DE74D6FA51DB770AB73D77CC016865FA51533C62C92FD8C10F5A4853E49FAB87FC4BAB4CEE4E17A24E8265C18957E0E0B6B7CA424556B31B0B15F02FD9194D654DE136323C81F339C5D0EE82688198A955FDC86AB0179AB90766CAE7CBF55FB5843B7BC6FA4A7248B93E36D5A462FA662859D6F8D09E5EF622F6
	2F1D212069EBD3610D0166AC438B164D21F37611481B1B9B127D666A552BE59B387FF2B0FAE98AFAB9F3BD6995AA6851B1A88D980F2A815DFFDEA63E3EE4AE5DFF52A47CC2022FD460693E7475AE762D26816A464FE5E7361B0D68EFF26605E4978354G34818C82044DE3E738538D25DC107C034961EC06605826B8D2D17D9E5AFDE75F5F4DD84B7CFA74B30FF1B1BFDC1E66AF5FBCFE9E352DF87119777BED84CF754BB522FE29D027198FFE9420854082F0EBBE536F0106922A9F2646EB85415C59C3326ECCB9
	CC044BE88C32E81CB42988573C5345F771AE97EF21577EDC7CFCF21FC47613D0F89FB51F45DB0F1C46982F43612201BC0953A61FE6F1DACEC2C8F6914095GA10166D9E93F29CE5F58F01E13759B89EDB1B60C00E70C580F5A2A4EB5945B1F83FDD392282D625C25A648B6A44628A61C8388B42DFD7DFDC4DB2A6B1A2FD0574C85703E00497CF8A11E17649B5FE6F387EF936914FC2C712813EF37E97DFF6E76D3AF5E69C6377829BFAC1EEBBA099F3C616C616DE50CE667DA99A75E69356A1531C57582BAF6D796
	30DCF24100DA3DA0553FDE2BD6BF821EBBB97A5D59BA0E7D376224AF8EE23785785DFBFD8BA7597D1BB94C6E37CF9C4B9996ADCF5B63476E1397D0FBAFDDA847EA535AA7262B7126FC1E7892CB7E6B671D6324391E136F5F365FA37BD68F70959632796EAD75356DFBCA190D0EDEC8EDBB861E3397527C87ED63B030B6A98B299D2F02E75EA25A065A71227AE0F62C0C8D0A21EF75A2EA47D8EE017755224F2572E774227C6D006FD944645F3F9EFF37606706DE9C13DBA7B5BEED28E1537A034711061FAF8DC49A
	DB7AB4BEEDA88DEAD3C9D6EF1B42F188DB44ECEEE601DA7D78C56AB6C767355194F85EFB3F4292755773F7496FC90AF7FE57D03F059B8A08797D3B4A037A7AFFEF741F4779F123FA9EA734D1BD1778E0E3606F4CFE51687FBB33F10D4A6F4C68DE25EB45391D86F31988308AA0AF9156CDA25B278EB76D6648BE2532C755DB3AB10F04DFCE8B0C7F9623FFDDF79803FFEF3D4F08774D9D8EC1F074A1FD06AFB162FC898EA7C10AB9DAA30E114B5A5DA35026A27E28916D4D46F791B43537448B9C477C4C3FCE9742
	534E8F94CA17EF86D895F67C503A78F0F9C5C9E1D9C569FE6A9BC78FA86F5EAAB7FC61776E2DA6117AC41378CC81E9E89C3478B47BD6FEA45445A532F377748D98635C563D3449AA5878BA62696839F85F99F6A73DAA111D457F64C0BE652F42599E4072AA3BAF7D50496230DB000BFE1565746B2E31337532D7998D67B5ECDB749A78B3G53EB373B2F30F5FD61A216687B6ACB7218DF2F3B1DB197732FDDFE722F44E7D159FF22E77A5BDEE37A1C9AE038888937390E6112DEE33909D7AEAB67B46F210F52D94C
	61BD271A5745FD6EF5F93F236B66D0BB6E8FA640BB8142GA281E28192811681F0C9907F8114G98G5AGECG43GC281A2GE21258BA7BF18B1E53DA4D3D3EDFCCGD2CC46104566B2537B8CBA4671C6F90C1BCA030E71310A7D54A6A614B21B1C8DB2E43E41466BCF25EC3C2A5F10725AA4E7A6C0F9A4C0449B4C2EFEE6E0F6B58DF0ABA59ACB8C4C2ECA96B39A3975641BF4A85F8288DBBCD43B7A6FD332DDEDEC16F5EEED389D1D29DC85E48FC69B08DC4CE47DDB814325ACE6F3D1576A5F0799DB037B30F3
	2D64BB9A70BD269BCEB1AFFE21155CBBF5F539F949D7363766C9B8233B456136E2BE1D5A5754E62431FD891C9F16B039F634FD9140C6DADC8EA76F92444F274857DE629E17F2BA58F928ED095AD9794FC17BFD66D5267D3EB8037D04A57E73BECA9831047DF608A7FEEA54926F73251D6047AF71BEDF7E0F0359DFB454EDCDE6F9846E5A1D7DEEEB639D76EB9CF2BF2AD78A713537123D75DDCE70A3123D753DFE0D699B86F529123E776B5578373FC9475F79266FBC08776B83EA7D060B6D437D767B38CE2DDF24
	58BED6259F3569492761A15A3E7EB26628D4ED99A20BD330164FF674721D02495CAD75E9E8773FAECF3787DF172D6D6A31C5FB3BEFECE1EED70FAD3A5B15318545595A51DFF6030FEE135ADD13F097A55CF5C293F1972C9621C7425FA2F2A13E09C7068C7FEFC42E8102EB93F1BFD260AE0A38116D68DBC0659E07F4FFFDDFBBFE2F59E5355944FC83E9E335032B114663A1A22355E9D6487D08043BA84D779325F766BF26876D276EBF7ECA7AA52858119AACFD13790F35E4BFDA752677FA4AA378F5EFF92F271F
	6B59FA52C05D60DBECBDBDEC38B3BFB0718E7B258C399F556B9E3227A63D652D6F0F89FE290F3E8575CC5F15D05776B653F7EB2F9A7FE9D0BF87A065EDBA8F29EFFB7F86AEDD2356AFA7A5A43B9020BC0536EFCA117BD19B0BED46F7698CC337AA316E374D9A0E7E2E45EA36D9EF124FD34FC3C3FBB73D570FAD48A575B62B1D5753DFE0F2AC37DDC7E262F660A724307C74E892930E00722894266FD57CE900280320F87F1B636E1FB737B86FBE0FBFDE13F57AC071FE6B5A5D677D40DD65C167F482097D4743FC
	46C8733C51406EA662A4DC9719FB4D9F60BC7B87061BB5405A15CAB8ADB9A38C4332047BE99DEBB7CA426516A1EE9914B35FE138CF09ACDB21DC728E332FDC03BC66DB946F0F686F7E7847FC8FBCFED4AF6B942AF87F247E6E73BEF4D7F950B9BDC84E8915B01FCD52BC07911C894A0204DB5A4BECE404041BF189F109D0DEA561BE51A0AE6FDD700B6FB25C3784D78265E689B7CF4368DD11F0168A44C5C1B961DD16B73CF886638881D3828D8EF29624B946EB447FAD05F6AB2536D38632D8DB0E1D1F1EA639DF
	DA9F2B47B8C100400956A511FCAD4D016EB58CA517BF526BAE33E8E9C8F681274C657FEB4013570673B22F378682714A76436BA311FE69D26FFB037ABACFDE87CCB22F476A8271DA9B04D7358F2F22B2757928A67E22895A1B163279B824771D0F162F6479E86875145B5DAA57CD6C455C5FF5B864D9BB4D36DA1B355B0E79B8095F0B3D6C5CBDFCA94BE32F6E8D242B725ED0CD57682572FCD29992AFE11C8D012675A628EA36E12E191DDC199C9FDCE2F244CAFA17EABC75FAD0110FFBC093D82FA7B40C5E8AC9
	2F77B44174CA8B20D761FBA159399EFAFDD391DC2F87AB243C867427FA687EA96B91B60049EB0FDC845AC3716EDAB80858F6179416417F657B25F97F677FF2527B302F657731D7647702CE197EA67D6D52276D13FA313D7297A46771AFCB593A357A7AB3267AF3922F8A0B4CEB43655B6DCB5B4F548C0D5794DE66B52962F67B52FF67A92A418407194865DC165B45ED124642C88EB749578F6BB25E9E1B114EE59A39B57430E764B27144E6643248014E48E540E1EF90A3BF7C481DA5F43FF4DF633EC52988B64D
	AF761C8736D24422FF4913301B9458AA10F233EFF62AB66455C511DF9BB469DCBA885A43D349328670E8C8E49B8DA4AEC99411DA3A88E9843B46238D1662D4FBE4F87420EDDC5C6718F236F674E97B2F77E94D9D02967F85A82D49E5363BEDE6814F4DB46C6668FFE969DC56A52E208B2F154E40915C45DA72C6AE1FE04F71744BD24CF9B5F39FFB606A7A6D969B0FDF098A5C66E69C0B03B6333D7BA0E5D1554C65F9B47F87FF83455D58ACB2E4F85A1CD4119162B827D360B608F732D0A5F09F1DF50A7FA20B59
	D6A8D6688A85AE6DG05B5A6EB9FE918AF5E29950A97E258A8DF3CA313F1B0D0399E0B6D45DF9EF9E77662D6F786374648FD528518733C0B9CD5F88BFEFEAC7EFBAD8D5CFAA7FEF70EF718E0FDEE7248F28BB7ECB0B61A2EF3783BCC3740DBEA59A1488D22530141047F41A6333B07CC820CCD269D46CDD6BA2D1638876A67C03E82A29F7652CA3D8A26EE5A3B6BD4CACC86175E40ED30B94C1686F32760F0993975C28F8CC90F43E699F41E2CA104568DC81F368239CFA5F5D1B9AD7AC1C9482B038F0F4CDD1A5B
	GD6B56802B089DB22CB57CAADAC7A9E338BD871AE23007360F095C9F577FCA66CFB39716376F4AE4DD3A91815AEF31F55F683DE8CB0160EEEE22D5B21C8939D52B2C72351B600BE42EB4BDF9A73FA750AE5C02C014BA23FC982117264D6E77A7BBAF8578FA39F58119D16FCAFCA6CC4F5996575825AAF7E868B0897FA694C2AA45135BFFD297401899FDEA2224BDD85EEA71DB368FA301BF7749D2C6D6A020329C420EA508964F7C864310D562B4D66417BBEBA35E0F587D814014B68ABADA5FF4B485FC378370C
	E04A88266C90CC5B8AAE60FF5E7503CCE7E206243087EDE8905F8A87A5FF017FF882DE7C63C67D15042DD37842F381309F7B18A6E237795A9822BF13E69ACB3B704FDE7C330FF4AAA6FF3F98321E586B0017FEC7E47D2AG0A4536DBA46CE6910BDE32165E8F6D7FCEF9BF147A039C7F3F37DBA17E5EEE05777D5008D328F08C6093D6307CFD2271G67BF67F93E4FEC33C164F13556EE417CE75D8D01176FF5FFF28A693BD99B31D27CF72CCE215C3D0ABE7F87D0CB8788C464F15D9A9BGGECD1GGD0CB8182
	94G94G88G88G06F9D9B0C464F15D9A9BGGECD1GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGD49BGGGG
**end of data**/
}
/**
 * Return the Hyphen1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getHyphen1() {
	if (ivjHyphen1 == null) {
		try {
			ivjHyphen1 = new javax.swing.JLabel();
			ivjHyphen1.setName("Hyphen1");
			ivjHyphen1.setText("-");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHyphen1;
}
/**
 * Return the Hyphen11 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getHyphen11() {
	if (ivjHyphen11 == null) {
		try {
			ivjHyphen11 = new javax.swing.JLabel();
			ivjHyphen11.setName("Hyphen11");
			ivjHyphen11.setText("-");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHyphen11;
}
/**
 * Return the NominalTimeoutJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNominalTimeoutJLabel() {
	if (ivjNominalTimeoutJLabel == null) {
		try {
			ivjNominalTimeoutJLabel = new javax.swing.JLabel();
			ivjNominalTimeoutJLabel.setName("NominalTimeoutJLabel");
			ivjNominalTimeoutJLabel.setText("Nominal Timeout: ");
			ivjNominalTimeoutJLabel.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
			ivjNominalTimeoutJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNominalTimeoutJLabel;
}
/**
 * Return the NominalTimeoutJComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getNominalTimeoutJComboBox() {
	if (ivjNominalTimeoutJComboBox == null) {
		try {
			ivjNominalTimeoutJComboBox = new javax.swing.JComboBox();
			ivjNominalTimeoutJComboBox.setName("NominalTimeoutJComboBox");
			// user code begin {1}
			ivjNominalTimeoutJComboBox.addItem("7.5 minutes");
			ivjNominalTimeoutJComboBox.addItem("15 minutes");
			ivjNominalTimeoutJComboBox.addItem("30 minutes");
			ivjNominalTimeoutJComboBox.addItem("60 minutes");
			ivjNominalTimeoutJComboBox.addItem("2 hours");
			ivjNominalTimeoutJComboBox.addItem("4 hours");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNominalTimeoutJComboBox;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOpAddressJLabel() {
	if (ivjOpAddressJLabel == null) {
		try {
			ivjOpAddressJLabel = new javax.swing.JLabel();
			ivjOpAddressJLabel.setName("OpAddressJLabel");
			ivjOpAddressJLabel.setText("Function Code: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpAddressJLabel;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOpAddressJTextField1() {
	if (ivjOpAddressJTextField1 == null) {
		try {
			ivjOpAddressJTextField1 = new javax.swing.JTextField();
			ivjOpAddressJTextField1.setName("OpAddressJTextField1");
			// user code begin {1}
			ivjOpAddressJTextField1.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 99) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpAddressJTextField1;
}
/**
 * Return the JTextField11 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOpAddressJTextField2() {
	if (ivjOpAddressJTextField2 == null) {
		try {
			ivjOpAddressJTextField2 = new javax.swing.JTextField();
			ivjOpAddressJTextField2.setName("OpAddressJTextField2");
			// user code begin {1}
			ivjOpAddressJTextField2.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 99) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpAddressJTextField2;
}
/**
 * Return the JTextField12 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOpAddressJTextField3() {
	if (ivjOpAddressJTextField3 == null) {
		try {
			ivjOpAddressJTextField3 = new javax.swing.JTextField();
			ivjOpAddressJTextField3.setName("OpAddressJTextField3");
			// user code begin {1}
			ivjOpAddressJTextField3.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 99) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpAddressJTextField3;
}
/**
 * Return the TimeoutPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getTimeoutPanel() {
	if (ivjTimeoutPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Timing");
			ivjTimeoutPanel = new javax.swing.JPanel();
			ivjTimeoutPanel.setName("TimeoutPanel");
			ivjTimeoutPanel.setPreferredSize(new java.awt.Dimension(346, 196));
			ivjTimeoutPanel.setBorder(ivjLocalBorder1);
			ivjTimeoutPanel.setLayout(new java.awt.GridBagLayout());
			ivjTimeoutPanel.setMinimumSize(new java.awt.Dimension(346, 196));

			java.awt.GridBagConstraints constraintsVirtualTimeoutJComboBox = new java.awt.GridBagConstraints();
			constraintsVirtualTimeoutJComboBox.gridx = 1; constraintsVirtualTimeoutJComboBox.gridy = 1;
			constraintsVirtualTimeoutJComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsVirtualTimeoutJComboBox.weightx = 1.0;
			constraintsVirtualTimeoutJComboBox.ipadx = -16;
			constraintsVirtualTimeoutJComboBox.insets = new java.awt.Insets(16, 1, 56, 84);
			getTimeoutPanel().add(getVirtualTimeoutJComboBox(), constraintsVirtualTimeoutJComboBox);

			java.awt.GridBagConstraints constraintsVirtualTimeoutJLabel = new java.awt.GridBagConstraints();
			constraintsVirtualTimeoutJLabel.gridx = 0; constraintsVirtualTimeoutJLabel.gridy = 1;
			constraintsVirtualTimeoutJLabel.ipadx = 18;
			constraintsVirtualTimeoutJLabel.insets = new java.awt.Insets(22, 40, 59, 0);
			getTimeoutPanel().add(getVirtualTimeoutJLabel(), constraintsVirtualTimeoutJLabel);

			java.awt.GridBagConstraints constraintsNominalTimeoutJComboBox = new java.awt.GridBagConstraints();
			constraintsNominalTimeoutJComboBox.gridx = 1; constraintsNominalTimeoutJComboBox.gridy = 0;
			constraintsNominalTimeoutJComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNominalTimeoutJComboBox.weightx = 1.0;
			constraintsNominalTimeoutJComboBox.ipadx = -16;
			constraintsNominalTimeoutJComboBox.insets = new java.awt.Insets(59, 1, 15, 84);
			getTimeoutPanel().add(getNominalTimeoutJComboBox(), constraintsNominalTimeoutJComboBox);

			java.awt.GridBagConstraints constraintsNominalTimeoutJLabel = new java.awt.GridBagConstraints();
			constraintsNominalTimeoutJLabel.gridx = 0; constraintsNominalTimeoutJLabel.gridy = 0;
			constraintsNominalTimeoutJLabel.ipadx = 6;
			constraintsNominalTimeoutJLabel.insets = new java.awt.Insets(65, 40, 18, 3);
			getTimeoutPanel().add(getNominalTimeoutJLabel(), constraintsNominalTimeoutJLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimeoutPanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
	
	LMGroupGolay golay = null;
	
		if( o instanceof com.cannontech.database.data.multi.MultiDBPersistent )
		{
			golay = (LMGroupGolay)
					com.cannontech.database.data.multi.MultiDBPersistent.getFirstObjectOfType(
					LMGroupGolay.class,
					(com.cannontech.database.data.multi.MultiDBPersistent)o );
		}
		else if( o instanceof com.cannontech.database.data.multi.SmartMultiDBPersistent )
			golay = (LMGroupGolay)
						((com.cannontech.database.data.multi.SmartMultiDBPersistent)o).getOwnerDBPersistent();
	
	
		if( o instanceof LMGroupGolay || golay != null )
		{
			if( golay == null )
				golay = (LMGroupGolay) o;
			
			
			//some annoying checks to verify that the user hasn't messed up the six digit address
			StringBuffer opAddress = new StringBuffer();
			if(getOpAddressJTextField1().getText().length() < 2)
				opAddress.append("0");
			opAddress.append(getOpAddressJTextField1().getText());
			if(getOpAddressJTextField2().getText().length() < 2)
				opAddress.append("0");
			opAddress.append(getOpAddressJTextField2().getText());
			if(getOpAddressJTextField3().getText().length() < 2)
				opAddress.append("0");
			opAddress.append(getOpAddressJTextField3().getText());
			golay.getLMGroupSASimple().setOperationalAddress(opAddress.toString());
			
			golay.getLMGroupSASimple().setNominalTimeout(com.cannontech.common.util.CtiUtilities.getIntervalSecondsValueFromDecimal((String)getNominalTimeoutJComboBox().getSelectedItem()));
		
			golay.getLMGroupSASimple().setVirtualTimeout(com.cannontech.common.util.CtiUtilities.getIntervalSecondsValueFromDecimal((String)getVirtualTimeoutJComboBox().getSelectedItem()));
			
		}
	return golay;
}
/**
 * Return the TimeoutJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getVirtualTimeoutJLabel() {
	if (ivjVirtualTimeoutJLabel == null) {
		try {
			ivjVirtualTimeoutJLabel = new javax.swing.JLabel();
			ivjVirtualTimeoutJLabel.setName("VirtualTimeoutJLabel");
			ivjVirtualTimeoutJLabel.setText("Virtual Timeout: ");
			ivjVirtualTimeoutJLabel.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
			ivjVirtualTimeoutJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjVirtualTimeoutJLabel;
}
/**
 * Return the TimeoutTextField property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getVirtualTimeoutJComboBox() {
	if (ivjVirtualTimeoutJComboBox == null) {
		try {
			ivjVirtualTimeoutJComboBox = new javax.swing.JComboBox();
			ivjVirtualTimeoutJComboBox.setName("VirtualTimeoutJComboBox");
			// user code begin {1}
			ivjVirtualTimeoutJComboBox.addItem("7.5 minutes");
			ivjVirtualTimeoutJComboBox.addItem("15 minutes");
			ivjVirtualTimeoutJComboBox.addItem("30 minutes");
			ivjVirtualTimeoutJComboBox.addItem("60 minutes");
			ivjVirtualTimeoutJComboBox.addItem("2 hours");
			ivjVirtualTimeoutJComboBox.addItem("4 hours");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjVirtualTimeoutJComboBox;
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
	getOpAddressJTextField1().addCaretListener(ivjEventHandler);
	getOpAddressJTextField2().addCaretListener(ivjEventHandler);
	getOpAddressJTextField3().addCaretListener(ivjEventHandler);
	getVirtualTimeoutJComboBox().addActionListener(ivjEventHandler);
	getNominalTimeoutJComboBox().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("GolayEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 360);

		java.awt.GridBagConstraints constraintsAddressPanel = new java.awt.GridBagConstraints();
		constraintsAddressPanel.gridx = 1; constraintsAddressPanel.gridy = 1;
		constraintsAddressPanel.fill = java.awt.GridBagConstraints.VERTICAL;
		constraintsAddressPanel.weightx = 1.0;
		constraintsAddressPanel.weighty = 1.0;
		constraintsAddressPanel.insets = new java.awt.Insets(1, 2, 1, 2);
		add(getAddressPanel(), constraintsAddressPanel);

		java.awt.GridBagConstraints constraintsTimeoutPanel = new java.awt.GridBagConstraints();
		constraintsTimeoutPanel.gridx = 1; constraintsTimeoutPanel.gridy = 2;
		constraintsTimeoutPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsTimeoutPanel.weightx = 1.0;
		constraintsTimeoutPanel.weighty = 1.0;
		constraintsTimeoutPanel.ipadx = -1;
		constraintsTimeoutPanel.insets = new java.awt.Insets(1, 2, 1, 3);
		add(getTimeoutPanel(), constraintsTimeoutPanel);
		initConnections();
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
		javax.swing.JFrame frame = new javax.swing.JFrame();
		GolayEditorPanel aGolayEditorPanel;
		aGolayEditorPanel = new GolayEditorPanel();
		frame.setContentPane(aGolayEditorPanel);
		frame.setSize(aGolayEditorPanel.getSize());
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
	
	if(o instanceof LMGroupGolay)
	{
		LMGroupGolay golay = (LMGroupGolay) o;
		
		StringBuffer address = new StringBuffer(golay.getLMGroupSASimple().getOperationalAddress());
		getOpAddressJTextField1().setText(address.substring(0,2));
		getOpAddressJTextField2().setText(address.substring(2,4));
		getOpAddressJTextField3().setText(address.substring(4,6));
		
		com.cannontech.common.util.CtiUtilities.setIntervalComboBoxSelectedItem( 
			getNominalTimeoutJComboBox(), golay.getLMGroupSASimple().getNominalTimeout().intValue() );
	
		com.cannontech.common.util.CtiUtilities.setIntervalComboBoxSelectedItem( 
			getVirtualTimeoutJComboBox(), golay.getLMGroupSASimple().getVirtualTimeout().intValue() );
		
	}

}

public boolean isInputValid() 
{

		return true;
}
}
