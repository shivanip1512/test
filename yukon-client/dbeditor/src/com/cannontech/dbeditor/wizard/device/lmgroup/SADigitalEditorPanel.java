package com.cannontech.dbeditor.wizard.device.lmgroup;

import com.cannontech.database.data.device.lm.LMGroupSADigital;
/**
 * Insert the type's description here.
 * Creation date: (2/25/2004 10:52:28 AM)
 * @author: 
 */
public class SADigitalEditorPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JPanel ivjAddressPanel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjHyphen1 = null;
	private javax.swing.JTextField ivjOpAddress1JTextField = null;
	private javax.swing.JTextField ivjOpAddress2JTextField = null;
	private javax.swing.JLabel ivjOpAddressJLabel = null;
	private javax.swing.JPanel ivjTimeoutPanel = null;
	private javax.swing.JLabel ivjNominalTimeoutJLabel = null;
	private javax.swing.JLabel ivjJLabelMarkIndex = null;
	private javax.swing.JLabel ivjJLabelSpaceIndex = null;
	private javax.swing.JPanel ivjJPanelIndexing = null;
	private javax.swing.JTextField ivjJTextFieldMarkIndex = null;
	private javax.swing.JTextField ivjJTextFieldSpaceIndex = null;

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == SADigitalEditorPanel.this.getNominalTimeoutJComboBox()) 
				connEtoC3(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == SADigitalEditorPanel.this.getOpAddress1JTextField()) 
				connEtoC1(e);
			if (e.getSource() == SADigitalEditorPanel.this.getOpAddress2JTextField()) 
				connEtoC2(e);
			if (e.getSource() == SADigitalEditorPanel.this.getJTextFieldMarkIndex()) 
				connEtoC4(e);
			if (e.getSource() == SADigitalEditorPanel.this.getJTextFieldSpaceIndex()) 
				connEtoC5(e);
		};
	};
	private javax.swing.JComboBox ivjNominalTimeoutJComboBox = null;
/**
 * SADigitalEditorPanel constructor comment.
 */
public SADigitalEditorPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (OpAddress1JTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> SADigitalEditorPanel.fireInputUpdate()V)
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
 * connEtoC2:  (OpAddress2JTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> SADigitalEditorPanel.fireInputUpdate()V)
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
 * connEtoC3:  (TimeoutJComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> SADigitalEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
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
 * connEtoC4:  (VirtualTimeoutJComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> SADigitalEditorPanel.fireInputUpdate()V)
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
 * connEtoC4:  (JTextFieldMarkIndex.caret.caretUpdate(javax.swing.event.CaretEvent) --> SADigitalEditorPanel.fireInputUpdate()V)
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
 * connEtoC5:  (JTextFieldSpaceIndex.caret.caretUpdate(javax.swing.event.CaretEvent) --> SADigitalEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(javax.swing.event.CaretEvent arg1) {
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
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Addressing");
			ivjAddressPanel = new javax.swing.JPanel();
			ivjAddressPanel.setName("AddressPanel");
			ivjAddressPanel.setPreferredSize(new java.awt.Dimension(344, 154));
			ivjAddressPanel.setBorder(ivjLocalBorder1);
			ivjAddressPanel.setLayout(new java.awt.GridBagLayout());
			ivjAddressPanel.setMinimumSize(new java.awt.Dimension(344, 154));

			java.awt.GridBagConstraints constraintsOpAddressJLabel = new java.awt.GridBagConstraints();
			constraintsOpAddressJLabel.gridx = 1; constraintsOpAddressJLabel.gridy = 1;
			constraintsOpAddressJLabel.ipadx = 9;
			constraintsOpAddressJLabel.insets = new java.awt.Insets(72, 49, 68, 4);
			getAddressPanel().add(getOpAddressJLabel(), constraintsOpAddressJLabel);

			java.awt.GridBagConstraints constraintsOpAddress1JTextField = new java.awt.GridBagConstraints();
			constraintsOpAddress1JTextField.gridx = 2; constraintsOpAddress1JTextField.gridy = 1;
			constraintsOpAddress1JTextField.weightx = 1.0;
			constraintsOpAddress1JTextField.ipadx = 25;
			constraintsOpAddress1JTextField.insets = new java.awt.Insets(68, 5, 66, 2);
			getAddressPanel().add(getOpAddress1JTextField(), constraintsOpAddress1JTextField);

			java.awt.GridBagConstraints constraintsOpAddress2JTextField = new java.awt.GridBagConstraints();
			constraintsOpAddress2JTextField.gridx = 4; constraintsOpAddress2JTextField.gridy = 1;
			constraintsOpAddress2JTextField.weightx = 1.0;
			constraintsOpAddress2JTextField.ipadx = 25;
			constraintsOpAddress2JTextField.insets = new java.awt.Insets(68, 2, 66, 85);
			getAddressPanel().add(getOpAddress2JTextField(), constraintsOpAddress2JTextField);

			java.awt.GridBagConstraints constraintsHyphen1 = new java.awt.GridBagConstraints();
			constraintsHyphen1.gridx = 3; constraintsHyphen1.gridy = 1;
			constraintsHyphen1.insets = new java.awt.Insets(69, 2, 71, 1);
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
	D0CB838494G88G88G580232B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8BF8D44555747F284D7F95AD2D78577A519691EB44E0030D8A9AB4E81407D1A38D15C724D12326B63CA4D6C43EFA49433B89F9818102C1D8A90F84436689A411D7C8C0820BD00898AD7EE6133DC9D6B63B6B6E8D899470BFE7664E5C3B3BF773EA71AF9FA7774E19B9F34E1CF366F5E66E8AF91BC7252DC84E930495E90272EF6F8AC16879CA9076EC7A479E064B8F96C5C1736FC300D5C2D1F9AABC
	8BA1EFC22AC14C959A2B8C105E876978F7C4F1BD3C2F91D2570FBC8BAF81784810973DFB76555E79940A0C0FC85267B3B261198DF088F0D600D000DC613B0CFC859F8469EC659DE1C5B2703A9B4BDC591B2F41A7A9650EC15EF9007C31D866360C0DCAFEB86030CC8CBC936E26E585A813CE247BE1CD15D26E7CCF030524691B1233101726FE864DE3A96DD065E994FEC39411A25CD9FD8CF5F67AC27B01D6E727295562F0B89D32545AE13236C8D61B6CF41B3AED17ADEE2B49AADD3435CAA6FBE73B5B59652A29
	B9DBDBEFEB3749967B91D26C3445A15913087C23779AC85DA0BF24075C0338772BD0E7D570FE9A20639E5606673276A9B20E3890AC445D31AAE09BA26F2132A75C2336A16CECC94A2DA1D87F6344A6C8D78869E6GAA4B4A15CC165890A64B51F2AC1BB7CE940B816A47B1FCD685624FC3DA8698B60E4938FF871331691F8F8ACBDBEA13F38248B8E69C15ADEA1CAAE3682213E99B085F0499507606FB01AF40A1GAB40D000B1G8544DF1D62CEF8563648F54E4ECE27235665BAEC373ACFBA2D129D5EEBEAA0C7
	411D11DA1DEE2BA0D01F3CEBEF310EBC8C426F7576C7845A364F8566C3A1E76E95E4F9EFF21ACE5B42E69A132315BA626FD55BD678DB545B4F08EDD3619D6950FE94FFBB415BB5F8EAABA341E3FAB864AD7FAD53F3D3B613451CBADA48AD2BCB96F5E43975312A640ED0AA4310D0D5962A631BC89F8C817CD400B80025G7963C531EABC5371C43122AF9DB7C09FF0BBAC7633DDADAE0BDBBE5CE533DBA537226BE9993D69DA8FA85D73832623FA892E505AA876F707F575929B3CA339F1BC5507FD3C2A974ABBF7
	E5A05E43467B7BC6B63761C3242F0C9ACFED18427111849F2A41D35B1A899E5391103775BED12465FFC07AD650FD347CCADE4FCF88BED80327752CA1F8CCCF053C625F09A235EFF1A60E0D43A19D829089908B309CA0BF8C4A05B19B279ACA7A590FCEEB6CCBE9A7995641D36A115D168607C736B8DA25F396374D52E217BC02D266C9836A35450B6EA9523FDB0147A712C3F2DBE41B538135DBBAA5F0A68FAD93E3D805E55C9249ED34F5CA402053A530FAE38DEB50FE960F7C194BEA11A535844D0FB720CFDC34
	1C039A2182F83F92067AFA040C39E6F86F88E33A7AA28BF191101E457572F57AEAF81EG67166CB5B57536D69443623E44FC7456AAECC3A214375FAF0A09G7687E0AC85109FE47DF4C19373477A2CF1C2714CDADDFFA4735B035437B2A7787A63DD04CFA5609B81E40091GE1GB19358BC3B3D2AD927CE14CBFE505FE7D14EECC5765E7A943609482614273CEAF5F961D8189520DFE6ABFE5ED73F4FD67C981F54AED332513713201D3993C531F4A2334DED69C66A3307AD9E09F969A9B7B818E4D5ADC54B7EBC
	8367A0692264106BA5176430420BC77111DFE5242B7E5B66643EB50660F1BC2B838773F07C580CD5948FF5E8FB834B3F0F64DBE459EDEB6912253312AC5B9C6DAA7DC3C4960F57D069D15ABAAD875BFA8C5A588C6062ED2DDB39C66307E62773C2172B91FB18624FBB483CBD944A0F1C487CA1022C77A2F8FABAC92B79E74A1B7A3047E0G7929FEF2613A70E0FEC4F97C2882962A829BD75394FCBA4F3F05644701DC060740FFG1AG06BC4474302122A9G7D3FC3D64A231C70702DFF653F09877511BD1901FD
	FCFF263E0F57E56A7BF8C3E66FBE7C77CCBD9F26F9CD1958C6E737C353A73E4A44F1345569F0C824E3F292012E2B97B4E2BBEC8E1B7C394D5AAE49E7ED17A558EBBC043EBD130C5541709E7E909BD33B7F4646542D176F96A23E5E283B8E3B754E0F13739E267ED87830BA26D2197EB78B7B1AE417BAC1D1F54EAE07CC71D6B28FA8F8CF8A419D3BACA3ED61A9D72D556A16BC1E32630D204D23B649EEADEE0521C0B6113FF496AA7C46F25152E372F4034ECC44A026BA4CBD022FC546DD3B8E9875368345EDB637
	5460F0F531EA0A4E9515D27EB7FF63433F7CFB62DFC6795FF755C378637C6A6C12D55E96E2CE93FD1C165CEDCEF727E4A58218AC5D32423D16E48F02FD39C079AF30DDA67CBFF5F65AE01D797D0B214831AC196AC1557EC90B7BC28374351E6BEA068A057F4F3BE59F7EE7DD16D6697A8BD0C967276539E4FE023EF49EC0FE184DCFF1D9184F66A7E5922EF35A6D343BC35E2C0731AF3FD01527691F439E267100EC1E1FDE5DFB7E6EBE72ADFD645F58C77E440079341DCFE7676238A7FD5BA541B297A7609352A5
	36D64DDE6D13F74EE26FD257403159B85E5BB5F3B745BF1B23BF8E4D4C41797A822FFFF64EFB822EE5E91DF45C8C35E01DAE372D9316BA3898D162A687D6EA63A646F0D14498CBB3BCE5GCA5B110BED55F3G63A42CE3FA850B2B8C831AB1G11CABD42A4E5ACA565EE2CE4656C109EBAA9D0395A5D2C5C98C8078344CE52AB173607155B7808A8D682B4BE22D7EEAAAFB79C7203F54ACDFD844BA57278CA894725105857EC1808FF104A760DA7F33571A12B27D2F7CEC148FCD41163D15F7DF459EA2C779060CF
	BF4A7A4659DC6D5A2D5663F1365A48F6C631FF72AA6DFCC3F1622A8CF8C2BF744824EFB39F4ADA053262FEC7C1ABFEF04FC77B857F7D9BB5A9DB9A57F513FE5F682C03AD57B1F043BABE9D1F33C95D82534355235A98D3F8CED1724AGFA30AA6D9F2A51033C68E49A156997B598B7500B153592A1CD6DDDB6D317EC430D15C536108107381FC0E3D141E4CD0BF5C5BE4AE2CEB1E4AF309C52F99130FF85207B5FA5E47F0BE56B81F785C099CFAEE97BF39860E381044922C84753AFAB51A786B8F0AABA0FACDACD
	ED64B3D6B2BD0EDEF3C8619BF56FE8E15FF7993AF130201C3C643C49D4FF7BA62BFA2C1A35AF4D8CC05B77C0A5EBDF901409184C5A77F839DE7B16CEE6FA79E6B7234BFCCC946B9FE3F4D9DE635C95400F81187A98534BF33BE14A9386BE2B32F804E98F639B8BF566BD4E785EFB4DD7DEB364399EE7FCEF59B398FBD01E0BB94F21D0DFB867D930D10FE75263ECAD79F939365FC74495866C7706C8EA1F0D113E7DFEED9E7AEA95604D112C5F872DDA256977E72027D99C6DB06A73757AEAECB30C6FCA3AE6B572
	A5633B40763EE756226C4DD067950091GA1GD11C476455FD46660E7474921B9B679767CA0CD4638142937E31819A07BAC6662C3CA7E89CCA6478C602AF566029ED6610FE0B69FA488BFE026D3D5E2E70651F7924B74FC21FB42593ABFD69CCBEE9338F9D6D335B8AB732B1C963D2B5B56AFA0A4E1FBD555A79537AA45DD7876173C996ABBFF4090DCFE1CF3298D270BA2CBF915206A922E8845887508CF085E05494E6375CB5FA6335375D828D56EA3C016AA25CCF977D536174BEF598B1451B0E36FBD9B9EB77
	2CA94C0E2E5D0313611A9F1D2F8C39D3756C58EEAF5037A3683345F95859232C03EE4C5133E3A15439EFAA33E3FD35C11127F1AAEBCF651E413567309F1DEFFB067934074A79CE111E1CE3206C84AE67AB57181C51DC4E3786A9E71A5F79072F1C06E9FA36BF331149E01C46FCBEE1BD621AA05DB10D79F7679A0C8332316F34536640FD8B4DFBBF8F5731CEB7ACBB9D8C371960DA1DEE92B42038AF57860EE7D8566A47B35A576A47B3BA57769E336BDA9BA85EC163EB11E4AD52836D9B86C0F13713B8C1A82427
	CEE37678140CFF0910CE1DAE0A9B27B37C17840FF3EC2CB27FD0FCDC36B6BE06B4DEE371B4DA076F985CDFD046D8AF9EE9BEE5D284B56EBA98780054B1EDDD8E512F47A3F9484005ABBAA56FB5F92D20937B795452A989ECDFDDDA192E14B9A2BB6B4A84564F1710FE21534FB5672721247F629A35EABADBA70EF0223EDD108E8298456DF06492737B89BAFA0D5241A9B15DF501FDF162BAFDDFFCF49D76810F334B5DAA797A5B136B72F91EBA32B748D2A72AC5599792FA5962EE177C7C758F041E6685227F633A
	C0BECDF53E44CB67655CDF5EADE7BA0A5351C7128E0E56F757EEEDFD38AE2176F9A20759477094334FB9F226F98852CDG2E2718FD9A2A996F20277C790C84DC288F1E723E30C74B3B12732E5849462BC84EFB49D5320700F4DE946CA122986F572FB15E75D17E3C1BGE70F5263ED5A2B653D1B6B71730D2C3EA1BA750DGDC308F1E5AED7EFADCBB757074E2123E44794DDD09E3255DF249598503EA61535826B778FEBF9C6A0C81D88E107BB41DC7E8DF9A5F0DB6E8F3DBBA418D4E3A24D6A17599241D1045E8
	DDD0FEA8409800C8007827593ABCFC9F6ACC893B113EC771B1842F44A4B5780F3231BE5ACB49CE95D8B23D5C4FEDEC867E4DG72B32A9E0260FD64B3BEF398D9DB9ADC648C75993A36CC677835849F254153B567EDE48C40F48264191FE56376F932EE8B05F494477DEC9B9B4B63B8EEDD85ABD79A4DF09B7633F275517CDCA50735E7846002814223D5792361BDBEDA2FBD53C99FD89ACD65CE637819841F791C0A276DB9DF4576B7C6489B7A9C1329190CFF8910CE1D41F0F32EB1B973B9AEB495F19D109EB203
	49DE35D33B8E98817860992A2C61B37C7D164A58FE8DE5091E61EB8B196093E7785A626D2B4C96065FC3BF7ABD13E945F7A8D3B42493B92E444DE497E2984EF691F127A15D41F10E0D0C768A471DA8E13443B96E3B6D0CB6AE066D0B5F4FE477DF12GD7BC13751132E62F7D816EF2EBEABEF15B2C07AD6DE6526368385212E9D0682DB35938B21C54B99252E1GD1B3597E32E7BB0B27047CE634E09EF2D8774ECEDCE3D1F27CCC2AEF439F547DE5642213E914673CAC46339E722F7C81F119595A3DAD763D6712
	69DA0E669F704A4779G36681AFC17D7BE4ABB490BFE34D7BE6A28CC2127EDFBEA9BEB5B7227C2857B7D25016FA3A9EDCAD01E05B1DB04D1B76DCF23B60A2AE6FD4D78BC335B5F099FD4C27A9847EDB800B8BB2407F05C4D87194D47F05CDD240FA7C1BAF3967375EC9746EE0D105E87F09E608A4FABA0F943E751B8A746B0C3F95EA867E04FB0990FEC052FEF7EBE4EE36220F9E84FAA754E575384B6EF3CDC0D36F13925B6496D162C7408CDB04CC6FD7D1068AB9A64098598C2F0F7EF2B6F05AF2B170199E81A
	8156AA7617613DC2F927BC8E1271F4AA3C474CE6BCCA777746A35D0707B0471BC75E9C1587357B0BE42DE1847C3EB94C970EE6137E8FE9AB47251B199F85F1DC50C966C7A19CF7FCBD732365F3182D22C96CB8F72EA8D64EE538398407B16DC60EEB299A28CDD91D7D357BAA611F8366414E207BDBF60D505D0B5F8CA77DD68619074CE5B6DDFCEDA0FE9382F491F329CDB1AFE6AE1DBBD6F07ED3D2F15D820B96DBE7D7A761CEF97F114CE7F1D0BEF1AE3357A9325E4B0F05B1BD1661E625937BC35A4AF11F2FE4
	368E0AE5766A3C06E340ACC8AF8548FF81EC71824B3BCC72AA15F3065300F7713CF72F7A5AG697B23DF2C9F79E8F133E275766B7957FCF97457066C9E042FDD7D6D1C7FDDE0BB1711F9FAB848BA7285E667236E0158F96A8B2A1D513631AF785AF948C5FDBB5FCA667E84A81F84C0F1BFA7F33FF19E58F99E13272524AFF934B2B54D731647BECF15077288A66B069E408F1D47ECED4C9C5CDDC653A0695A87CCC757E1AD64EEC32826CD94FF016043B5F83A9E7A9F3E9E1A0ABA7DA30B6D5C3181778643A19D82
	9089908B309CA0BF0E45FAAE66178AFD44B29B1DAEB3EC1E6D1A508BED5F4BD9033B63B9D9E46B0E3007468A93EED834A25099D5F19C6DCFE66599B1E38B4153766DA94076DD013CDB814281E2G92G0497D97B9E49AF266D436334DAD93634F61013BA56B8BCBCD3513831227E331C0C0DF9D00F71C5360F6AF0635E2D4569A61BF93AF77B4E4D76DFE6A8E78598859076A20D95D1BA2359FF4FF778G230BFA116E79626139D42123FDE26C415E7AC40A8F643EA40ADBDF22FD8253D5AFD1BF197C6E3B6FD2B9
	AC6B757BE8B4D9BF54C37953AFB119C31AB0E642EF2C9CB7DBDAA43B52572F9664E973B437E9E8FE5EF62F7CF25F7C1F34227F9C3B646A109CE54A597DA917571956CBD48F23DEE267C0A74A75724360B975A56C5EDFEC9184B6967DE85BA6F8BA24EEA3BF6C2A074D2F83AF119616D7979B4BCB0B18AF0E6A561E9FD5E518821F9B3F2C2C85156708343F891B89BF98F38476EF51C1FF9C74CBA2632E2D826F83AFE632BE30711EBB047A67972D78DBG3ECD8A3F601755FDC56C391294DA6718EDEA3B33777727
	5DFAB2533A26ECD76BDA1A93F8BED915CB66EE10A771E566AFDFE57B778DB91B79F8FEBC6D9B25702C0F276715344CDEB3ABF3BE1E7683991EC1CA995A8FFEFD322FFEA0EA7A41A8208D0BD77B4154F85A8FEC508F287C17483AB08670F171CC7E4FAA709B9A6F9BCA6AF984ADB3EAD71EFF9952B1280FBE335957C7318D192FB09FFDFD072A5FE72FED8EEC2B32D2B4D6C551G7DAB2BC8FDBF32D365B5FEF3FFF85132EDBB7D6DF8F5A733117195EA47AAF81ADF2131F0CA37B05DFF0CFB0FFFA3E4FD055A768A
	BC07ABF454360E15FD59D6BB6F8783ED78AB2AED2361FDBE58F5A29F632A8F20EEF4EEF8A96DFB64D01ED73EFA830B667F358547BDEAD5DF5A7D27F2F91ECAC7ED7FFA3D2F6D310D312F30716BDC17DE7EA26E9B9D9F29765A6A6E259FD7745D0F376FD27D204124567B57027E54CB696CA5AA5D2112FFCD1E5BCAD5F9A6152975BEDD5F1FFA69DA238307A3657DDB4D7B484C6BF1C759FB9DF77FF565C157A9A19B821FD17CEE037E9945448D7A67E50FEE687D7E6F148D016F7FBE33C1FB7F172E33EEA4F3EF6A
	2B3096822887E882303F4A56D8675F4749973FB271DFDF9D3136639989FED9A2B37E878B823775E8C15F5F231CAA407BD5ADCED9F6F6E27D8C7FD9815ACBF63A88D2B907ACC09D39ED6D9DB2AD2A601B8BC88CC1EAA3E86A1B46CA55B70F3A0D3D78E6E5E53131ECF7E591DDFF5F65E65F818501CEA2DEE57148C99968787D3AC355E8136D52E1B26A52F861C2B7BB27DF7AAA0BD10E2AC1BE252F0962B100665778BD2183EC7D5F83385057581973FD87D84CB161B5D6C79CA9EBCC00B19C60B4C07F66985617FF
	34DB6D4BE377287A6AF676C7DF4A39E0DB00F3C1B232D259EFF03176F7A430767C973F3BB0AA0145E71F7D07F67D16FBDBFEB25B9B797633D9894AB7FD893E77FFC612BB0679FF92454ABFB11F7E55969C104FD93E713F0D874853EE27CBF24B17A8165C94D57A4A2CAA1C4F9A219E9740880090008800D9G09G426B221887D08CD08F5084E0879882B09CE08C400457199C6E2DC53D48218437C987F3DB68B9BD35CDD80DEA1B0F33FB35CDB958E66FDE4517C7E6B3DF0E8299D27F4CF49F1F4DF43F6F4F7C9C
	70841615A1BD8CE04C1F19BFFEEDE67E980E743C0E3B4C4C9F0B93D99DB3C9D9AB242FG0CC89C28BFBE7F316A0F4B7629ED2E2E6ACF1B29DCE9C62C977DA9A41135614E1DCC8F71005BB81F35E17ECE260746792C8D5B52316CB0C80F81881F4F6AB0716FF4E3G17390055F1B1031F85ACE0F50CA1E52FC0FA84C04802016A610383182275FFF8041F1BF21EB7F6B15C72854C37EEA98FBC8E0FAC6FFB9C3E1374C789464F46CBAE655CFAECB9399F5256661178F87F5B72DC0EEB701CF6FAECF85E2D5CE1F377
	95C328DC2897D3FB38CF3ACDBEE9CA17C468CEAF6CDF0C4F3A10FD53B9E7BF5A27E721EFCC66E5029F3A50B7A6731B9AE67B61101734105F35BAB038D85235C152BDB8E0BA65AE60C1143FE911EFFB3FA2F86BA25F763E4A67A0976445ACE26D7D31CB0FFF55E2A52E33585F8E4AF7E165FAF44DCAF9D9070E0E3F53C9BF20FE37E08FEE0EF547CED8199C07F161D44BB7D22B5CE8E967B4376F453A785A098E6DCADE70DEDC8761F7CEB60B5DF619DC83BF8B859D6DB4BF31AAF0FFDAD255F7FFDADE253F2EC92E
	76DF5708557A6B1A2CEA6D3A0662D6D763F8558EF3C593AF371E633E60380D24CE44FDEE334A9D9C3F25BAC3419F1310A1439792393A892EC9416D52603ED0F06555B8A6C013BB1C7CFE585EEA7CBE224DE637537447240C4D81C3845747A1A2234DE55148FD04633E603EB2BAFBF0E3C8E81FF47AE3481F88DD100E8F6A414845EC8C19CE664B50453EFD6A77849F3E5837CF1DBF41633A10D77C866BD312F9F0E301E710F4A18326236DEAB809728FFF43373DE788FE649B3E6D1D4B578D411017390435B74328
	475F8579C381EEDDC26D9032C43587253BE767607CE2E21FF47AFE7186210BD852BF3F08D9427C6297E47D103044D7CF2389BEE9092F1EBECAE7FA9212C4511A446F3D8E72379A8E8F126E1F8326236D4ADF0972CFCD72ED6F07849F13645B5E31FC9D9587F975EF72EF660FE879873FC9759E2EBC235F8CB4377C2CCBCB972F14DB2ABCB37F62CFC76FC2C610FB31B6075D66106A68AF9F886C6CFA25071CE981ED55DF58B9653DE44DEC06F473DF781DD13CBA4E46914DFBC84D750FE3BC71BD707872205A26D6
	4DFB62016B4FBB6D607564C1EDBA1A6CCB6DE8E3EEE771DBFE9E4DF13FA2EB63D910CE6038148AD6AE096384F267217EADD1BC7F96434DBB0EB89724073E456FAD1CD0F5B9DB735EF2627A6B751F5F830FD3A755B6B5E95EDFB4DFFF5E4B2EAB8FEA538F499EF3B858F39437738D84978269A80E1BE364F799B9EE95D95315AE954546257C8E4CC764BEA02407F05CB88362C6C0BAF8A9731BDF272B3A8C563C1725DFFF3D9E7F9EF8ECDA2936E90766BDB4637A731EF6DDF9D01B6EA7FB79B030E7A437333B070C
	AD10CE603867FB785842F1F7F7B15A7A37793D58FCC65B44F15D790C567AB6BB4F5962C1E711B1ACF954C962CA746C2C064CF3E1D0AE0217FDA2BB0D1595D8ACE696EEF714D84CC4972E75E5587C503CF77028CD2067BF1B3E9D4899F7334FBD35C5A0439AC1FB8F657EF2EFDE1BB6283C5E2F680D1758872F65EF7B5EF9D91F232F2392B2269B1609E25EB22623B70F7B6B68647B2A0E2A0C5EF23B8A543CDF9AB11EFFD1F24B36D60B3D56EEEBF7E00C1D73FB504863C94B78BDAE63C0749AB1577BBE0D8C7530
	BB99D40642EAB4A8EC60EC17212996BB9E2031C88E13232E1A49B104375B78113E0E2A4878958A6542F959C8033F0EEE5E286A21718859B3C2B5340EEFC85FC8847A253C0E7F6E715663839F2876DF50732F6824709DD15CA1E86F984DA07D2B9270075EE167F81BB62B7CEE4A9F485926AFBF57BB749E4F8A2E0B69DBD5DD0C4E465FEB740AEF22FE8B37543AED967B16E2E3997C2FAC6234A74859BEFDF76CD05FCFE94E09E79C6FEF1D347CB3C6AC2F7D3A77AC7EE547BD9ED35D11CF9B0F1C613C5C1AB34EAF
	2B87C6BBFB17CAFB3BE1E0ED5FDD2A523675740336DC257DCAF35EB1F12FDACFC67E406A0945E38A017ECE678326A741F1C2668BE99E37305ABFBEDDFB536C67C33E2E4FCB96129B0477BAC8AC25C1C86B4620CC8F7EFC9A0621BEG5A9CCE5BC082A7F197ABEAD2DE3EA3A5D9C885C29AC8EA90D2B19A8474EEDACF0A6CF411DA8EAB8E180C830767DB619ACA28540D29E4258CBEBF73AA034D6864E536ABCA41D5E921CE99D66B8ECE0125316B7C94E00BCE99FF0ACD9A0A54G9437C1DFE97262412E72246814
	41FF63A1F50ED364E9BAE4EA00B2DE1D96AC30838F76ECAD1D262E0B1DA6CB0BEC425F91B5B53AAD8E0F5DA2E3F40F8688857AEFC53210762530268DEF9EFCAA41084ECACA8D42AA02BDA3516F4AB022EE9656FB618EF7B92CF6893F91130555E634FD095D62E8AF21ACBEB68B39DE45CFC817D06C75E605A1435362A40B78818CB5AED948D0EEEBC116AC2CBD65D2FE534FE2B7AA99B5C6D9D831CB965EEB34F512C22B141BB0C665AA8C96B0306F05E4A1932FCF98C99AF332480D89168655E5F9F51CA7EE5F7D
	EA543CF31E94613D86E1830E71E7A5B78933CAD67C9CD1798D4023C23A8B3FC7457BC860599E72C87348176C202D3C36B3787BAD9ED9325632G2E879AC3D505872DA547AC1E8EE29650D62A83B4292AE1C5EDB3D1860E4DABC03EB5C4BE24B2F12AB58F67EC7B70F87C0494A1792810E1F7DA2CC7AD2D32535DA0244B9D20248E275D5A63BAF808D4E468E61F4E25C2F5853C3958B8934E1FDC488BBD3F9C161510F594BC2C47AD141012B80519F889EBDD07458D2CA4F7030C16F13A37723C9FEC1237BF7D497A
	7349428A6FC6012D5AAC1DB67BA5F82987DDBA5B099FEF07A48D5072A1845D48F4945AA34F19BFED745CFD0B92213223C29A79BD8390A9D7AD75E9D7E70B643EA164168FD60E087BB1CA6C4266321A53E574E87C2D89285C6853E61645C5B75DBC2D74163B17FDC9C4D7C9E5E1B33599101634CB4E4E125A36B61BC36295FC5C63026ABF6055E39913CF1955BDBFDDFBFC663BAD60D175C2CAE7E9A979DBC67E16635FB202A9A31832F2B05BA22157FF3E79BD2C4D448DC9E2977AD08F3E6D6C61E386AE8270568B
	AECC0EF16CA7829DD9F1FCBAC2309F7AB9A1E2377AFB93228B39CDB175917EA941BF2504281C7C5DBD6096A155DE1F169CD0DB0253D909A23769BFD966A3BD9A6928AC197F7F529DD252A7D40D6221875E22C0C9367DA74BFCC61559281159406E3396FD273D07317407B9816F51AECFB51078EAEE1A41679E061144474C006FG2067BD2EF2DC7885BA43BB5BE9315BE1B7F22156E145733C4317E4C93D67342D826B7730B2CAA64DAB2EC0393F51507C9FD0CB87883F156DE0B89EGG20DDGGD0CB818294G
	94G88G88G580232B03F156DE0B89EGG20DDGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGF29EGGGG
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
 * Return the JLabelMarkIndex property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMarkIndex() {
	if (ivjJLabelMarkIndex == null) {
		try {
			ivjJLabelMarkIndex = new javax.swing.JLabel();
			ivjJLabelMarkIndex.setName("JLabelMarkIndex");
			ivjJLabelMarkIndex.setText("Mark Index: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMarkIndex;
}
/**
 * Return the JLabelSpaceIndex property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSpaceIndex() {
	if (ivjJLabelSpaceIndex == null) {
		try {
			ivjJLabelSpaceIndex = new javax.swing.JLabel();
			ivjJLabelSpaceIndex.setName("JLabelSpaceIndex");
			ivjJLabelSpaceIndex.setText("Space Index: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSpaceIndex;
}
/**
 * Return the JPanelIndexing property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelIndexing() {
	if (ivjJPanelIndexing == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder2;
			ivjLocalBorder2 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder2.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder2.setTitle("Indexing");
			ivjJPanelIndexing = new javax.swing.JPanel();
			ivjJPanelIndexing.setName("JPanelIndexing");
			ivjJPanelIndexing.setBorder(ivjLocalBorder2);
			ivjJPanelIndexing.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJTextFieldMarkIndex = new java.awt.GridBagConstraints();
			constraintsJTextFieldMarkIndex.gridx = 2; constraintsJTextFieldMarkIndex.gridy = 1;
			constraintsJTextFieldMarkIndex.weightx = 1.0;
			constraintsJTextFieldMarkIndex.insets = new java.awt.Insets(34, 8, 3, 145);
			getJPanelIndexing().add(getJTextFieldMarkIndex(), constraintsJTextFieldMarkIndex);

			java.awt.GridBagConstraints constraintsJTextFieldSpaceIndex = new java.awt.GridBagConstraints();
			constraintsJTextFieldSpaceIndex.gridx = 2; constraintsJTextFieldSpaceIndex.gridy = 2;
			constraintsJTextFieldSpaceIndex.weightx = 1.0;
			constraintsJTextFieldSpaceIndex.insets = new java.awt.Insets(4, 8, 30, 145);
			getJPanelIndexing().add(getJTextFieldSpaceIndex(), constraintsJTextFieldSpaceIndex);

			java.awt.GridBagConstraints constraintsJLabelMarkIndex = new java.awt.GridBagConstraints();
			constraintsJLabelMarkIndex.gridx = 1; constraintsJLabelMarkIndex.gridy = 1;
			constraintsJLabelMarkIndex.ipadx = 19;
			constraintsJLabelMarkIndex.insets = new java.awt.Insets(37, 54, 6, 8);
			getJPanelIndexing().add(getJLabelMarkIndex(), constraintsJLabelMarkIndex);

			java.awt.GridBagConstraints constraintsJLabelSpaceIndex = new java.awt.GridBagConstraints();
			constraintsJLabelSpaceIndex.gridx = 1; constraintsJLabelSpaceIndex.gridy = 2;
			constraintsJLabelSpaceIndex.ipadx = 12;
			constraintsJLabelSpaceIndex.insets = new java.awt.Insets(8, 54, 32, 8);
			getJPanelIndexing().add(getJLabelSpaceIndex(), constraintsJLabelSpaceIndex);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelIndexing;
}
/**
 * Return the JTextFieldMarkIndex property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldMarkIndex() {
	if (ivjJTextFieldMarkIndex == null) {
		try {
			ivjJTextFieldMarkIndex = new javax.swing.JTextField();
			ivjJTextFieldMarkIndex.setName("JTextFieldMarkIndex");
			ivjJTextFieldMarkIndex.setPreferredSize(new java.awt.Dimension(39, 20));
			ivjJTextFieldMarkIndex.setMinimumSize(new java.awt.Dimension(39, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldMarkIndex;
}
/**
 * Return the JTextFieldSpaceIndex property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldSpaceIndex() {
	if (ivjJTextFieldSpaceIndex == null) {
		try {
			ivjJTextFieldSpaceIndex = new javax.swing.JTextField();
			ivjJTextFieldSpaceIndex.setName("JTextFieldSpaceIndex");
			ivjJTextFieldSpaceIndex.setPreferredSize(new java.awt.Dimension(39, 20));
			ivjJTextFieldSpaceIndex.setMinimumSize(new java.awt.Dimension(39, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldSpaceIndex;
}
/**
 * Return the TimeoutJComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getNominalTimeoutJComboBox() {
	if (ivjNominalTimeoutJComboBox == null) {
		try {
			ivjNominalTimeoutJComboBox = new javax.swing.JComboBox();
			ivjNominalTimeoutJComboBox.setName("NominalTimeoutJComboBox");
			ivjNominalTimeoutJComboBox.setPreferredSize(new java.awt.Dimension(109, 23));
			ivjNominalTimeoutJComboBox.setMinimumSize(new java.awt.Dimension(109, 23));
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
 * Return the TimeoutJLabel property value.
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
 * Return the OpAddress1JTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOpAddress1JTextField() {
	if (ivjOpAddress1JTextField == null) {
		try {
			ivjOpAddress1JTextField = new javax.swing.JTextField();
			ivjOpAddress1JTextField.setName("OpAddress1JTextField");
			ivjOpAddress1JTextField.setPreferredSize(new java.awt.Dimension(29, 20));
			// user code begin {1}
			ivjOpAddress1JTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 99) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpAddress1JTextField;
}
/**
 * Return the OpAddress2JTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOpAddress2JTextField() {
	if (ivjOpAddress2JTextField == null) {
		try {
			ivjOpAddress2JTextField = new javax.swing.JTextField();
			ivjOpAddress2JTextField.setName("OpAddress2JTextField");
			ivjOpAddress2JTextField.setPreferredSize(new java.awt.Dimension(29, 20));
			// user code begin {1}
			ivjOpAddress2JTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 9) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpAddress2JTextField;
}
/**
 * Return the OpAddressJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOpAddressJLabel() {
	if (ivjOpAddressJLabel == null) {
		try {
			ivjOpAddressJLabel = new javax.swing.JLabel();
			ivjOpAddressJLabel.setName("OpAddressJLabel");
			ivjOpAddressJLabel.setText("Operational Address: ");
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
 * Return the TimeoutPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getTimeoutPanel() {
	if (ivjTimeoutPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Timing");
			ivjTimeoutPanel = new javax.swing.JPanel();
			ivjTimeoutPanel.setName("TimeoutPanel");
			ivjTimeoutPanel.setPreferredSize(new java.awt.Dimension(342, 177));
			ivjTimeoutPanel.setBorder(ivjLocalBorder);
			ivjTimeoutPanel.setLayout(new java.awt.GridBagLayout());
			ivjTimeoutPanel.setMinimumSize(new java.awt.Dimension(342, 177));

			java.awt.GridBagConstraints constraintsNominalTimeoutJComboBox = new java.awt.GridBagConstraints();
			constraintsNominalTimeoutJComboBox.gridx = 2; constraintsNominalTimeoutJComboBox.gridy = 1;
			constraintsNominalTimeoutJComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNominalTimeoutJComboBox.weightx = 1.0;
			constraintsNominalTimeoutJComboBox.insets = new java.awt.Insets(40, 3, 38, 75);
			getTimeoutPanel().add(getNominalTimeoutJComboBox(), constraintsNominalTimeoutJComboBox);

			java.awt.GridBagConstraints constraintsNominalTimeoutJLabel = new java.awt.GridBagConstraints();
			constraintsNominalTimeoutJLabel.gridx = 1; constraintsNominalTimeoutJLabel.gridy = 1;
			constraintsNominalTimeoutJLabel.ipadx = 5;
			constraintsNominalTimeoutJLabel.insets = new java.awt.Insets(46, 47, 41, 2);
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
public Object getValue(Object o) 
{
	
	LMGroupSADigital digital = null;
	
	if( o instanceof com.cannontech.database.data.multi.MultiDBPersistent )
	{
		digital = (LMGroupSADigital)
				com.cannontech.database.data.multi.MultiDBPersistent.getFirstObjectOfType(
				LMGroupSADigital.class,
				(com.cannontech.database.data.multi.MultiDBPersistent)o );
	}
	else if( o instanceof com.cannontech.database.data.multi.SmartMultiDBPersistent )
		digital = (LMGroupSADigital)
				((com.cannontech.database.data.multi.SmartMultiDBPersistent)o).getOwnerDBPersistent();
	
	
	if( o instanceof LMGroupSADigital || digital != null )
	{
		if( digital == null )
			digital = (LMGroupSADigital) o;
		
		//some annoying but necessary verification of the address string
		StringBuffer opAddress = new StringBuffer();
		if(getOpAddress1JTextField().getText().length() < 2)
			opAddress.append("0");	
		opAddress.append(getOpAddress1JTextField().getText());
		opAddress.append("-");
		opAddress.append(getOpAddress2JTextField().getText());
		digital.getLMGroupSASimple().setOperationalAddress(opAddress.toString());
			
		digital.getLMGroupSASimple().setNominalTimeout(com.cannontech.common.util.CtiUtilities.getIntervalSecondsValueFromDecimal((String)getNominalTimeoutJComboBox().getSelectedItem()));
		
		if(getJTextFieldMarkIndex().getText().compareTo("") != 0)
			digital.getLMGroupSASimple().setMarkIndex(new Integer(getJTextFieldMarkIndex().getText()));
			
		if(getJTextFieldSpaceIndex().getText().compareTo("") != 0)
			digital.getLMGroupSASimple().setSpaceIndex(new Integer(getJTextFieldSpaceIndex().getText()));
			
	}
	return digital;
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
	getOpAddress1JTextField().addCaretListener(ivjEventHandler);
	getOpAddress2JTextField().addCaretListener(ivjEventHandler);
	getNominalTimeoutJComboBox().addActionListener(ivjEventHandler);
	getJTextFieldMarkIndex().addCaretListener(ivjEventHandler);
	getJTextFieldSpaceIndex().addCaretListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SADigitalEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 360);

		java.awt.GridBagConstraints constraintsTimeoutPanel = new java.awt.GridBagConstraints();
		constraintsTimeoutPanel.gridx = 1; constraintsTimeoutPanel.gridy = 3;
		constraintsTimeoutPanel.fill = java.awt.GridBagConstraints.NONE;
		constraintsTimeoutPanel.weightx = 1.0;
		constraintsTimeoutPanel.weighty = 1.0;
		constraintsTimeoutPanel.ipady = -76;
		constraintsTimeoutPanel.insets = new java.awt.Insets(4, 5, 20, 3);
		add(getTimeoutPanel(), constraintsTimeoutPanel);

		java.awt.GridBagConstraints constraintsAddressPanel = new java.awt.GridBagConstraints();
		constraintsAddressPanel.gridx = 1; constraintsAddressPanel.gridy = 1;
		constraintsAddressPanel.fill = java.awt.GridBagConstraints.NONE;
		constraintsAddressPanel.weightx = 1.0;
		constraintsAddressPanel.weighty = 1.0;
		constraintsAddressPanel.ipady = -32;
		constraintsAddressPanel.insets = new java.awt.Insets(3, 5, 3, 1);
		add(getAddressPanel(), constraintsAddressPanel);

		java.awt.GridBagConstraints constraintsJPanelIndexing = new java.awt.GridBagConstraints();
		constraintsJPanelIndexing.gridx = 1; constraintsJPanelIndexing.gridy = 2;
		constraintsJPanelIndexing.fill = java.awt.GridBagConstraints.NONE;
		constraintsJPanelIndexing.weightx = 1.0;
		constraintsJPanelIndexing.weighty = 1.0;
		constraintsJPanelIndexing.ipadx = -10;
		constraintsJPanelIndexing.ipady = -36;
		constraintsJPanelIndexing.insets = new java.awt.Insets(3, 5, 3, 3);
		add(getJPanelIndexing(), constraintsJPanelIndexing);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
public boolean isInputValid() 
{
	
	return true;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SADigitalEditorPanel aSADigitalEditorPanel;
		aSADigitalEditorPanel = new SADigitalEditorPanel();
		frame.setContentPane(aSADigitalEditorPanel);
		frame.setSize(aSADigitalEditorPanel.getSize());
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
	
	if(o instanceof LMGroupSADigital)
	{
		LMGroupSADigital digital = (LMGroupSADigital) o;
		
		StringBuffer address = new StringBuffer(digital.getLMGroupSASimple().getOperationalAddress());
		getOpAddress1JTextField().setText(address.substring(0,2));
		//skip that hyphen at position 2
		getOpAddress2JTextField().setText(address.substring(3,4));

		
		com.cannontech.common.util.CtiUtilities.setIntervalComboBoxSelectedItem( 
			getNominalTimeoutJComboBox(), digital.getLMGroupSASimple().getNominalTimeout().intValue() );
			
		if(digital.getLMGroupSASimple().getMarkIndex().intValue() != 0)
			getJTextFieldMarkIndex().setText(digital.getLMGroupSASimple().getMarkIndex().toString());
		
		if(digital.getLMGroupSASimple().getSpaceIndex().intValue() != 0)
			getJTextFieldSpaceIndex().setText(digital.getLMGroupSASimple().getSpaceIndex().toString());
				
	}
}
}
