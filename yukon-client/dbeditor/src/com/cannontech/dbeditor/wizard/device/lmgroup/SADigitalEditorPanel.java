package com.cannontech.dbeditor.wizard.device.lmgroup;

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

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == SADigitalEditorPanel.this.getNominalTimeoutTextField()) 
				connEtoC3(e);
			if (e.getSource() == SADigitalEditorPanel.this.getVirtualTimeoutTextField()) 
				connEtoC4(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == SADigitalEditorPanel.this.getOpAddress1JTextField()) 
				connEtoC1(e);
			if (e.getSource() == SADigitalEditorPanel.this.getOpAddress2JTextField()) 
				connEtoC2(e);
		};
	};
	private javax.swing.JLabel ivjNominalTimeoutJLabel = null;
	private javax.swing.JComboBox ivjNominalTimeoutTextField = null;
	private javax.swing.JLabel ivjVirtualTimeoutJLabel = null;
	private javax.swing.JComboBox ivjVirtualTimeoutTextField = null;
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
 * connEtoC3:  (TimeoutTextField.action.actionPerformed(java.awt.event.ActionEvent) --> SADigitalEditorPanel.fireInputUpdate()V)
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
 * connEtoC4:  (VirtualTimeoutTextField.action.actionPerformed(java.awt.event.ActionEvent) --> SADigitalEditorPanel.fireInputUpdate()V)
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
			constraintsOpAddressJLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsOpAddressJLabel.ipadx = 9;
			constraintsOpAddressJLabel.insets = new java.awt.Insets(72, 49, 68, 4);
			getAddressPanel().add(getOpAddressJLabel(), constraintsOpAddressJLabel);

			java.awt.GridBagConstraints constraintsOpAddress1JTextField = new java.awt.GridBagConstraints();
			constraintsOpAddress1JTextField.gridx = 2; constraintsOpAddress1JTextField.gridy = 1;
			constraintsOpAddress1JTextField.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsOpAddress1JTextField.weightx = 1.0;
			constraintsOpAddress1JTextField.ipadx = 25;
			constraintsOpAddress1JTextField.insets = new java.awt.Insets(68, 5, 66, 2);
			getAddressPanel().add(getOpAddress1JTextField(), constraintsOpAddress1JTextField);

			java.awt.GridBagConstraints constraintsOpAddress2JTextField = new java.awt.GridBagConstraints();
			constraintsOpAddress2JTextField.gridx = 4; constraintsOpAddress2JTextField.gridy = 1;
			constraintsOpAddress2JTextField.anchor = java.awt.GridBagConstraints.NORTHWEST;
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
	D0CB838494G88G88GC0F3D9B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DD8955715245164A54528912DB19A0918AC29B6B50D79E2DA53327D524DCFCDEA3EB52B5B35ADED5C165D455636EE57F65DBABCA091C1450891639FCD54006213DFB584937987C3A3D584127AB73CB7BC86667DB8EF0007226EB9774E1D19B7EF9E282D6173B0F34F39771E1FFB6E4FB9F7104B599D1B1EE24F613814F4CE7DB91242F1D1A7B86E64D3E37B99AE0F37E5F2061FBF81EC622A4F2541
	3388E8AFFC5516194D859B56C239964AC3B3ED19DB61FDB3F735213086DEA270C9835A12AB6F3DB2BC1FD76B981FB7C9B929AA8B1E9BG9A81A7814C6311BFD71D2B6247C1F9037A0E10E2673821CE2CB36FF02E813FDA2D5782B497C01E936BEC2F5E21521F839C56D98A4F951D346EDA2813C1249BDDF1D42DF7F6FCBC3772256D7675484B50BF03EEA7ED9B2DBE7338B8E208D46E118F9A61D959526B7E486973B81C3C576B73AA02335B616A94DC2262139D8362E5DEF6B9DCC23F68949C1247AD7B7A7C9595
	272BEAC5372870D29D295642FB85E9B5117F470757123EC1FEA85BDC08EBBB02B6BB8E6F9DGFE9753615575152A0CB13D71DCE25C0608BAACF0D159D738F49D8A4F4F32478A58FFEDF5165A2E884A5DGD41668EAA64BB20149F224846B6E6832E51681B4F7B17C9F895E8565A10018AEA6633177190C9DD71E66D6F5FEE45F98C14659DDD436C5DD3A0C494F3C14360D784D39869C7BECB77085E88410G2281E683ECA77E1ADB3F9F1ED51DCA0D4F6371F92B7C7EEA49A57F596792A4F82F28G0A0ABBA5B8FD
	320B6328CF3EFE3850C29E864F39C37D91016A564EB19F1AFD6AF1CED14A6C69963A4DFDED27FD095AC732DB57AD3F9B6D7692995BACF8CFD17B25784584AF9970F42C4E92BC16EDC0CB6EE6F66E5840E4E9CE1B41E5979F373FE9A14B646F165BDBC4AAC337284BC2ED3C7B9C5AB88670F3GDEGC88460FAE0EE75B09BF756170EE463A61883321717CE77F57AF9D9296E93A517A02B36BED1BD1C2D2D00366B3869F654AE712546B1DA7A644716F6D99A3F47DE59C36D515A235B05729E9F113752936E9B9B34
	B11CC566042D070EE12A06FF0A60A79B70F4EC0F91BC1663011655EB4B247565D2447BFB4DBEF21D6023FB4DBE12D64AFCA49EE8D912AD130E6F3367F1ED74C3B99AE0A640FC0025G2BG32BCEC0C3FD6FF688667D10BE1FCE95B2E063761A9849519EF7286945E6B945AF8D964BBA5A14029F5FC8DE8574E10F68A195FCE6051A0F88519D7C41F97FA67BD82B8D30056395A30896B488221360A9E8198F87C9C6BF7F443E61CB3FCC0B963F7710A20572074B18DE813FE3E8DFA048E607D2C876D758732669642
	FB2307596ABFAFA0AE964A73B43B2CEDFC8B1E1F00F38BD2C5C52D68C4B1F8F910796842A3300FF18B20FE33571639G2059476667AFBB189F562E7F9AD7785AC716FEC856BF1F3AD6784CFE78A352FF0E9F56C000D6G454F76554EA39D26FE1AFD563EE985CCE62D6C8DEDCBF9DC896311F19373917DBA5D542711CE6D5E41236F4600DEF3G967A196DEB9A7328CFD6738101F961C9999CC8F069A3C16B661F43BDC668973CCA2D60973CAEF88928BEF068DC066E1FDDBE4DF74A899E572B9AF02000067F705C
	A60A07BE0C5E4E68B5044EAB0AACF676A942E9C1D1C42FDBEF7F8911A590329486D4DDBF6FC4DD13C0C76E927861A5266B22F31B8DFE566C73757679DBF186297E9ACF7665BDD03F72929B7FA7CA701CF5D1ABFF0B14F5FACD09F96C7E9E007CF47FB8FBDBF8240678E0309467GDBB753D5BC250DA634A810A99EE0A140AACD7FF5259D966D7EDEB2D29E6F919E467EEFE6FE0C8454B71EBF9F5937DFB9EF6D5B0B4EDB7B768F4F8F6F3BBFBBEF653B14769F67D1C75F0057B097FEF99E57C7274F6B954804541A
	F0743C7C4BD654C3740ACA3B68F28B4AE97132GF1000CBE3D69887AF49E3C1749EC4D9CF80F2D19FBAEBF46A5DC58E6F93E1A7C48D1FB34CC7DB0D65657CCAA532FAE609C93A44183062A7175F9950A7FEF323E2B78C0AA41B5DED630ED7EC9FF154BA58B01C07109D630E63DA8C82EC2A7AC810A037C263BCBFE8F5F4F879D01813019038C08238629F578DA10F77060C7F9D6477C42AED1961A3C7EBE56CDC1DBC19165BF2147443F648B62DFCC79FF63EA007047FD535727683CF9B21C8E7AE89164AE1F6C91
	DCC4G87BF20285C2B8879965817F0147F568FAE937EED222C74715297ADC6A95B0346B851E79240179681A44BECDD3EF49169ECDDD6B71F9A1FA4D1F787DACB007872519C03FF2685E81C3BC1239F9B01AE0EC01F9811CE65BC4F63BA9890AE7589F07C420D6393E1D0BDC3897CDBA6DA9BAF7569E7B38F0FC13CE458F3A87EE22775BC6A6A44FD26D76B3F27738D8E4FD834CFBA6F6710BE7D326801AD9AB7D1B51E7FCE35B11E1F8CFAE06C3F841E49G34EDF7B66AEAB50413956C6319D2966F27C1F987C025
	C27B89AA54A6345ED0992B378C4A2BA356ABAAE775F67441DA84505AE7D5EF25D6AF8668338196186AE575E33D7CA3AC06BE2461BEA638DF4186B1AD0D45A9FF4EB666A3DC01E24B350E78C0BF6D5F55EF0E5F3E6B42FEC781BE261F796C32CE63D922AA9070B9C5F2FCD647F50253388ED25CE467BAF8428C89C0AC6792024CB79EF2222CF83ED6516A784EBAF00C8B0F9768D03123DA4DG19112D3E9AB862B702FB55E85BC41BA88CF04C8ED76B0DB90D799B7759B3A358A1DE557F85039D66BC73D29A156901
	8A0CD32DF2B3CEA224435DA7BA7A94918F723C421325003895C7F39F85B5EBD57D568E309C476F6BC89E864A9DGFEG9AEF7D0C44DBD857863884GBACF7FAA9A6769AA406785ED1935GF43DEBAAC31F384905DD3579F385EF51B1B22DE24C0EB3B69FD779AEF8FC86D7F97DCD4B3C0BED63A6FBC7105AAFAA285BB1587ECCDAB4G556F11B2265F822813B64874F3ED34522FF210596533F2562E85F0B62D5D0992E33B8440A781E4DDE6F6C9A8AF01674D2F76AC7E5DF918714D03BEBBAEB33EBF2C3462BBCE
	63BB6A702D72D573C1A546B91C1038B7629C1EF9195AFA5EE5739CFE348B7DEE916013AE33B93C4D3549B007CF412C613DEED81935B3218B6507B5D8ADFF5F05FC499A4C31BD6DF5B76A3E9A7A4C3DE24BAC82E884683E42F81C92C64C6B548507496BB8EA4CB9925B95BD061CFAA5BC9625B98CCFBD0E551CABB407112E6103847F1C81CF47F6CD9D9B5BA5C02B9C52724A25E67E31C321BC678FD945343F8BEB67B01593CD6D687C5B1B3F032DAF0669DAD1211F58689E77759C639E37EA0846ECBB2E4298DCE5
	F9567BC53656B4DEE579078FC46CBF8A4AD3816682AC84D88910FD8D7625EBEC5C9E962C565E50F10B34706A31AC35452C32DB3361B3E16D4CB6EC3D965A0E6A7D60C62637F40D0D630572DB134193564EAC434CEBD663681636DB0FA3583353D76D8B2AE715B975D663B89F7ADCF80D0D639DA5EC2FCF5274F9AFAC37FCE37A940735B36B13F75D4A261B0EB1990EDEE73EB45803B8BF14232EB33F792300F785ECCDE97109DE0CBDA8EDD6971E61FCB29C393C8CB707601CBE1984FC9477DF6E483168EF5C5631
	68AA37F5AC7AC77770F916B73AA3452AB4A7D2C7766BF1205FCCG0A6B2EC55C8BD0CE3C4E46A3D00F3834A81803D18CF70D60F0AF407D0B62526B0D398C2CEB34F5DAD478FAF6B3C0F96C8A6111EE2A137AB7740F30137471625B9B09DD8381A1C09682BC6D2834DFA8DBB80BF87DD35EA3287CB7F33B4BB254BAF50A2F26D81BB73FAD48351EB7063B2CDF6420DD717CD696454ED05FB8C96EB520BC8AE02AB686DF91193F4F35306D8B96B8B57FA6C676419322358F3608687B81DF1F6C944CFE56AE66EAB4FD
	25ECD2848F1AC50D05C8FB05175DC2181F3AC9FBCA0B545EAFC672E5EA731F05583CC47317580D4CC6CBAD6C314282C77B1BD4EE6C8F77F9BABEC975ECFC526EE063B336984727924A2DG529DECFC6EAA996E7EE5549D11F8FFFE58483BCC53656DE34375B75922BFEA3BEBBDF89E88EA65AF117220466FEC8D2ED392BF686B43856DCB28D3204638F64F063E9781A429BC28BFBFB100F66812F98F8C45E93F606416123657EBD95BC6A81F859882188AB07FCB6C5CB83F92ED2626AB087FD37CA202D7F3B984CF
	F5CF4EE13A6F395316F9942079CEDD5F0B702E5C992ABFBD936DEBA177C9F71A6F01CA89FE661D66FB20F9A759BDD08250760CE26B6263643CE10372CC8DF7320C2D1573B45C539BD93D352399AE2108555BB11A794672FA260F8438A80018513A7C7170BEFF3415BE29643EEB61E833BE59841FBC5A2C4F44E2264F5A3B40B76FE2B2CDABE1B2AD54F0870FB1DC5E5DAC17D0FCB1644C0627790A0A86D9F4D5736EE662B1F4DEBCC07692646D3F1B4D0B67EB10F7BC1493G165C4DF21441FDACDE1D7D688C2EB9
	6AE34BBB784456FD7695F7D39B645874B3BF774D1752A84F54BA46338568B69B620A6B0C718A7A542BF6FA8E20F4E7889D579488C58C7409A1F414775910764F075051C645EAFB2A5B7777B25DD6FFFF8EA7BDF5B06277858BD51DD62A4F608F5E619257FFB30D0EC56A9666C70577307139279CF147215C2A61DAF7A3AE8865E88DA76731F11C2361E2C91E033B97E219FB198F9636E04E2B904A0DG5DGB60D768121CD3D176607B077B3DF23FD7B642D5ED9B09E9BD43EE6FA2346E371AD73B05EA1D81D8752
	B9366E3D33854746AF8BDD02AC8BAE1ADA67F23ECC3EA5A073FDA948138490CDF0DDE5354370E57DB26800B69240DEF57C23603DD4FD27BC9249DA31885E933F4CF8ACAE9A0EC70609C7D6F4A80F3CE81D879D77B4720DD2A1609B2319AFBC5A0E3833D076EB3862B666C7B19A2E35DDDB5FB45C58B266C7EB4730314AA8C1DF59816563G978122B45A5B0406B9411900FBEE8C4D7BD15A0345A30DB176E5AC9F9F936EB3D1AAEE47982B18E06E08BC4CF7DA66B3A5333B15BF51B11BDBF2B3FE31ECCC68182D02
	7272B5EB5624E87D559E9B29BFE31FE977057617FB1F5E9F3541C3E1F90611006EA99F56627A3D67BE3A2724E878DA02AFB26069DE73F28D7BA62A92E8317731D82ACC4273C05AD858D7G0E8234G7881224732586A4D5EA2EE04183C5567EF06C32BE488F528FEEB6BEE6DBB17FFFC132D55F33F79D5EE5EDD4BD3A265CDE347D25FCAD01F592F3CC370D43F67BD285F8A20E50D33E59681B4821C85D046B17DCE7696D27DB045DB25A83C331BE40F19F218505551F81822EBC4A95D87201F18F16C6C94DB0E67
	35CE1FCC8F51644C75F4B9BBF3A5C03DA5GB963ED197963E9FC265E5535051F732E6EB61F730EC31B56716A77FBC406FB72067347D493C850B68AE00BDA0E992F7B89156351B234B39C9AC5CF1F072C3AD45797499E958B75E70FE7B24F6E40D8C53B5DBB514CF78A125A4F556DB9C61A61661152F3760550CB4C7431CE741F46C1FF3760ADD66F09BCAD467DFD09EA3F5563357CF40695BD7BFEB8AB5E0F5FF27E84AD2B4AD7DB368B1EDEE1A0CFCB04564201570B9F526417FCD018D7D22465C1E3870CF95023
	6B8EC44C03F65FCF47E6C8FDAE03D37DEE428F4EF89C7BD971F1B88E66B21171C9721DAB657B53ACB687F64C0A63EA97AFCFF9AF825F39AA3F34897AD92C151FE527FD6E8F6AFABFD5F4A3FADB49CC7B7A73205E57760648FBF499595BAAC11E46894CDF1EED8F1F9B0B5A190FAB93685C0806E76C841A4B26F5AE34196741BC20AF18E01C873D6DA34D83633A3C8C5A2E1A204F032C98FD9ED0793FCD7652DC406F09E1724F5A025F91FF6A035991FE1B2B5AA476E0CED89DBAB1A83D7C8452C3EF040D74381DDB
	4368BA8F6A67E5FE331F232DDAE258BCD813EBC5F781BD72387A712AFA18F11FF085CBF4CD2F2A555FCF985E7F3966F6FCAF93FAE6C83E2DBC68FEF0CC0A1C0329152CF3B0A7A46BBCE00BB47CB7A99DD264EFD2AEC846EFD268FE669C42318D42D846G4C83D8G109843763265523B9C594BAC7631BA179BF3C07895234278AF74C456F531E764EF9F93BDF8375A69D3941F877BE7781FFBF03C941F1FA0557C2A87EDA40B6EEE05D6D5713F76203FC9C297C1D3FF4B3C06FEC567443572BCAE621AD5DAD6D618
	D7DCDED6C04FB9BFA9E75F1C2E861B14CDE43172336B708CF3C377272D22A28955E41522316C7B07353C7EC496BFBF3C0B64A820BC9FE051C416B30F5B4D625EE42D6E2B3B49D9ED12AD339220F51275BC0CBC771E3C0EA57ABEBDDF374B4413B7E297B53F5995A13FC9569EAA7B4C96CD4FC95ADDCE86434D1C44F284BF782BF1BF4C1E32454E56CE73FCDABA092E51C9134C7702638B305F9DDF01B367D7186F6E69BBG4FB63EA77C469D10AD324FAF484AA045122FBC54B97172FEDC04BA209F85E0B2409C00
	67G16822CGC81B8C31B5C091C0A340D9GBF40A8009800F8007913199C2B7A8B0611C3CD301009A473741E010E4DE3057A58BC58BE6C581440589CB9227A6C6AF666338BC106358FB05B57F6B05B9FFF00795235B3D8B78865F1G71DA5D51ED4C6F92343ACF36E15DD5D04E1E82674DA9B76BF78B8E6AFE77AB072EDBD6510D68C6656A75E33F68B74727B0D98FEFE3386094E6735E21486B10BCB472BAF4E508FC77816BC76B20DF4DCBC78D11FB1F2E2E00202DF7230732B5DCD320459790B11FCD7716FF8F
	3BF3B3431CA9D6F16CEFC7EC374BD4D6F3BC64CEEC41147038598A9227306F67371075E46594F36C370B6039874D315FBF6CE231DF96507C8F3298BFFE772D451EF5F9585F22874D7CBFA57844B07E0BF7B37E49C0EB194A78BF56E245FF6ED475BB182961F6D16FD6B22C5AADD56B2F30E8C757092978F90A6A872F67E3CCEEB947E127BC81FEFD3233C7F0AA2D3CDBEBB329G7B5276F23A842934E98538AF633720A2AF091749A7C927212257CD69BF5D9F593F13760F6C5F3F5EEF3D4F7E4F7E70FDF675FE6B
	FD56FE40384F2A317D81DCDF5C3026F5E87532B45CE79AEE53018C9557AE3A14EE8D3F7540BA955FA8A0C3065FF9G659AA0388E95772E8177190AFB7FG4ED1D0395B275D43969E40EF703AC4C92265A2D2C7744214556CD1CEE4947D3CC16E8A8D771966AB760EDB1B5359A336331E53CD24DD5A349B1B533953581C7E8DD9578B271967547F927C51E966B9B56A8C1BD335C00B1F4666543DED37B6275FA7FBD056C3E67EC5841F7B10197FB3ED0C7F9E20599EE27C7DFEAB7E49C067265BB2F326D33B64CF57
	6DC35BFD384D2ADDA554EB81F029750306F6744EB7E173FA629B126895EA68DF5BF0AC3FDAC34EED5150E666F416A31DC27E56E09E1497CCE7B2EF47ADD015E30F617DEB3BEEFFBC73CFDFG0FCF72F41DDA8D6FBF5AFD7BF97F6E3672D06F704839F5890CE712B64EFB7F427263ABB55C6364ACDB9B8BE770D8065B3C0E553B2861AE12F3579CA83F20615EB8C36E9C203CA216794D2AB33AADD35A7477FB4F5CFE3B46FD81BC8E366BBA9DB63CFF276D7673DEF2DBF950B13D1B1CCDD743F8E6BD4C46B91960F2
	21FCD4439D3F44FC24D943CD4EC55CD4A8A7E838D364DC3B884A4BB45C538437824A59B398AE739D565F9E8D9728C41C826549B3D83E09FEEB20E0B8DA6FA3F1864DCD9D7DG6B4E06FA73343ACFD427333A9CBB33FF95BF35D04F6C0BDBF0CFD56010C1E9AF63D5141ABB2B7C4BCD6510CDF7E7C9A043E64E18C37EDEC6A82F95E5BA2F7D6B066375E684DEDB547ED74EB0672B87BA2CEDB4164CE1AE8E46B60E59E86719F09BEDBE2C5B68A1FF285C070F683454CB18476907GD4F472D215A43A3D18DB51786D
	3C44622F4AB8F6DF58FE69EF312B94E7366B57F3711C8887A571B2284ACB1897E4918C136325DCA6C7D053DB5C962A57E7C7F53DDE59B63CDEC95BD8FF711ADE6F3EF3B339D633DECB6274F7AA433D15A36B75C8A513E3D99C4B677F5FB1DD0F7B2A717FE78809C9519F725F2D02033474AE0424702F2CC09B773C0FB96D3D66387EBE3DDA6F738F2DB75AA72DDFF6896B9B3FF6BF0D3FD94AAD6028297B34356E14466BDFEAF4DEBF4D3D5136347E1AEDB7476B7735BA2F7915B75A167E3F9917BEBF8E8770594B
	3974004C3D959E73D75D772F0BE7DF284D31F376A66E0DEEF23EEF625287B0D088621FB3E3E834935AEE545AB61143FCE2FFE9C56A6BF129F6AE8D9A52602609CB4388855A4B341FD488EEC8AF552AE35981EA8317B812A1D47B4612DD2D034FB3DA9DAC312B43AA00C28DDBE540E6EA7DCD81E407D607B9B3F1D60BBA787384145A3496B9C677B755113977B179A8F6FA9CFD7D9E875F29B8707F55F0344A3CB7A0718AC6FCB4E86468CF0A1DCB7F1C5B5C05497FCFA1F814D9CD21095BC43027847AED9FE6BD1A
	392DA1386ABE2FCB9270BBBD05FB2B99EDFFC8623D6EC31445074DDCF6C875CF04C194F7E3334A1061E9F5C2A2634041A1DF615629B7F7C0D238ADA77D6A5F3873D21ECA2848D33814038A77C62B68A115B62937A2F96A3586D658245EF468B8B0527A1069763D074BFF3E6047ED01D46E0DA6EEDB97E0CE8BB289328597FE7C297E75FBBD1761472FFE71968AFCA8C09E6981E5D0823B64F41D423F908BA8022B0A056F81901B9A85533E079A79C0B799G30CB1A97EC26AB1CD2F511280D2BDB8A48371948072D
	9CDA2B4D5F5A3877CFA7164DCB656C755CBA49473B6AF92762131B388C259B4C516D13DCC17F47E42561568EE89FAAC2F75BB5F5D1B9872EFE1A103D41E9516B13565703E785E56E9029098B39C32B612A6966E5E0A548CD8A0E01CF5E23516E58256CFB39E16BD9BB17922A940CCA976F9125C1F82985DB7A5C44D3F7C01106675AE4C507F15403BE4A8F173FB8635FAAD7A4C3E775DCBA79EB8C90A9DB2F75E91F27D3106F1ABDF1E7E6CC6217D1E2BF2A4BFA4ED050F7712FCF2073BC134E0C24096E1870E251
	44477E70B991DDEF2AF03B6918C153C3EE4167B9D4555585211F564107C1BFF43FD36B9E6BB8CCF55E8A0E5FF26235B51D60D135DC2A2728087CAEA63FCB70F7B1419493CCF1898C5B8AEE589FB3BD48F4A6EEC88A6F218F8571ADAF282D9538A663A7EA38BDB7EA5886F58D43B5270EE03706B9A1E25F8977A6C46F524694CBFF42DFFB7157BE52A81F7CBEF0539AE12B03A6CD4AF4CDD02A9235EEC908FE8C3B095E9D94DCB75E9D2C9A1D9571AEBDEB16FAE7B04BFCF790CD3E7FBF8B7860AC16BFDEAAE232BA
	D2BE6F340717A4B8D974D6F9DD185B2B9ED4847D8EEEFC8F769FE0F5D4227A3703BDA8F70F214D7F83D0CB8788BA8453F84799GG28CAGGD0CB818294G94G88G88GC0F3D9B0BA8453F84799GG28CAGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG819AGGGG
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
 * Return the TimeoutTextField property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getNominalTimeoutTextField() {
	if (ivjNominalTimeoutTextField == null) {
		try {
			ivjNominalTimeoutTextField = new javax.swing.JComboBox();
			ivjNominalTimeoutTextField.setName("NominalTimeoutTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNominalTimeoutTextField;
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
			ivjOpAddress1JTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 9) );
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

			java.awt.GridBagConstraints constraintsNominalTimeoutTextField = new java.awt.GridBagConstraints();
			constraintsNominalTimeoutTextField.gridx = 2; constraintsNominalTimeoutTextField.gridy = 1;
			constraintsNominalTimeoutTextField.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsNominalTimeoutTextField.weightx = 1.0;
			constraintsNominalTimeoutTextField.ipadx = -10;
			constraintsNominalTimeoutTextField.insets = new java.awt.Insets(44, 1, 11, 65);
			getTimeoutPanel().add(getNominalTimeoutTextField(), constraintsNominalTimeoutTextField);

			java.awt.GridBagConstraints constraintsNominalTimeoutJLabel = new java.awt.GridBagConstraints();
			constraintsNominalTimeoutJLabel.gridx = 1; constraintsNominalTimeoutJLabel.gridy = 1;
			constraintsNominalTimeoutJLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsNominalTimeoutJLabel.ipadx = 9;
			constraintsNominalTimeoutJLabel.insets = new java.awt.Insets(50, 50, 14, 0);
			getTimeoutPanel().add(getNominalTimeoutJLabel(), constraintsNominalTimeoutJLabel);

			java.awt.GridBagConstraints constraintsVirtualTimeoutJLabel = new java.awt.GridBagConstraints();
			constraintsVirtualTimeoutJLabel.gridx = 1; constraintsVirtualTimeoutJLabel.gridy = 2;
			constraintsVirtualTimeoutJLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsVirtualTimeoutJLabel.ipadx = 18;
			constraintsVirtualTimeoutJLabel.insets = new java.awt.Insets(17, 50, 68, 0);
			getTimeoutPanel().add(getVirtualTimeoutJLabel(), constraintsVirtualTimeoutJLabel);

			java.awt.GridBagConstraints constraintsVirtualTimeoutTextField = new java.awt.GridBagConstraints();
			constraintsVirtualTimeoutTextField.gridx = 2; constraintsVirtualTimeoutTextField.gridy = 2;
			constraintsVirtualTimeoutTextField.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsVirtualTimeoutTextField.weightx = 1.0;
			constraintsVirtualTimeoutTextField.ipadx = -10;
			constraintsVirtualTimeoutTextField.insets = new java.awt.Insets(11, 1, 65, 65);
			getTimeoutPanel().add(getVirtualTimeoutTextField(), constraintsVirtualTimeoutTextField);
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
	return null;
}
/**
 * Return the VirtualTimeoutJLabel property value.
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
 * Return the VirtualTimeoutTextField property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getVirtualTimeoutTextField() {
	if (ivjVirtualTimeoutTextField == null) {
		try {
			ivjVirtualTimeoutTextField = new javax.swing.JComboBox();
			ivjVirtualTimeoutTextField.setName("VirtualTimeoutTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjVirtualTimeoutTextField;
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
	getNominalTimeoutTextField().addActionListener(ivjEventHandler);
	getVirtualTimeoutTextField().addActionListener(ivjEventHandler);
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
		constraintsTimeoutPanel.gridx = 1; constraintsTimeoutPanel.gridy = 2;
		constraintsTimeoutPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsTimeoutPanel.weightx = 1.0;
		constraintsTimeoutPanel.weighty = 1.0;
		constraintsTimeoutPanel.insets = new java.awt.Insets(3, 5, 20, 3);
		add(getTimeoutPanel(), constraintsTimeoutPanel);

		java.awt.GridBagConstraints constraintsAddressPanel = new java.awt.GridBagConstraints();
		constraintsAddressPanel.gridx = 1; constraintsAddressPanel.gridy = 1;
		constraintsAddressPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsAddressPanel.weightx = 1.0;
		constraintsAddressPanel.weighty = 1.0;
		constraintsAddressPanel.insets = new java.awt.Insets(3, 5, 3, 1);
		add(getAddressPanel(), constraintsAddressPanel);
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
public void setValue(Object o) {}
}
