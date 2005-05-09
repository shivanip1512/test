package com.cannontech.dbeditor.wizard.device.lmgroup;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.device.lm.LMGroupSA205;
import com.cannontech.database.db.device.lm.IlmDefines;
/**
 * Insert the type's description here.
 * Creation date: (2/24/2004 5:55:33 PM)
 * @author: 
 */
public class SA205EditorPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JPanel ivjAddressPanel = null;
	private javax.swing.JLabel ivjJLabelOpAddress = null;
	private javax.swing.JTextField ivjJTextFieldOpAddress = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == SA205EditorPanel.this.getRelayCombosJComboBox()) 
				connEtoC2(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == SA205EditorPanel.this.getJTextFieldOpAddress()) 
				connEtoC1(e);
		};
	};
	private javax.swing.JComboBox ivjRelayCombosJComboBox = null;
	private javax.swing.JLabel ivjRelayJLabel = null;
	private javax.swing.JPanel ivjRelayPanel = null;
/**
 * SA205EditorPanel constructor comment.
 */
public SA205EditorPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (JTextFieldOpAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> SA205EditorPanel.fireInputUpdate()V)
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
 * connEtoC2:  (JComboBox1.action.actionPerformed(java.awt.event.ActionEvent) --> SA205EditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
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
			ivjAddressPanel.setBorder(ivjLocalBorder);
			ivjAddressPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelOpAddress = new java.awt.GridBagConstraints();
			constraintsJLabelOpAddress.gridx = 1; constraintsJLabelOpAddress.gridy = 1;
			constraintsJLabelOpAddress.ipadx = 9;
			constraintsJLabelOpAddress.insets = new java.awt.Insets(73, 25, 65, 4);
			getAddressPanel().add(getJLabelOpAddress(), constraintsJLabelOpAddress);

			java.awt.GridBagConstraints constraintsJTextFieldOpAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldOpAddress.gridx = 2; constraintsJTextFieldOpAddress.gridy = 1;
			constraintsJTextFieldOpAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldOpAddress.weightx = 1.0;
			constraintsJTextFieldOpAddress.ipadx = 107;
			constraintsJTextFieldOpAddress.insets = new java.awt.Insets(69, 5, 63, 63);
			getAddressPanel().add(getJTextFieldOpAddress(), constraintsJTextFieldOpAddress);
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
	D0CB838494G88G88G59DBD9B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBB8BD494D716FE1098A5FEA6A4B1334906A4EE56CCC88E3323B3A6E34EF166B03B4964E74E1A0D3BF1F24C045938331AE1F75CF31C2CB3F9B420A82898B42220A18E929C3181A346340872D1C1D191BA0808C251DDF437D4F7355585B4C8123D773DFA55C5538D62464949256A5D773B3FF75F3D37CB3275131854A453D6C212D209765F97C904A4D6907234FB70B90EDB529219CE8C7FFD8A30057C38
	BF851E4B21AF717E4874CC1264C803769AE8C74F0CCC5F8E6FD9E42DA36A883C0459478EFD267FFAF856487B7C66B45FE79DED3F33BD831E1BF040654874FCG6FE55C7F8F5B33B5FCAD34B7E96F88C9A6C2CADAF14CCACB3681BFD79B1782FD59G0B69182D5939DA3F9F5AB8E686BCE73732318B60391ED2B749F2C49B57B6F38ED9754B9C53C65C4B30BE075CABEC6E896D19C79E2702C8A6732AEA60D95E506D283049EE33CD70F8E40FAA5A1CE6FB27E8F72932E26EF38D880A5DEC97FBDDB651AC399D0A5C
	6335D81AAA8F16141D24C39A840FA845DDC15A330E2651F501F6E81737A1EE6AD11457AAE8E7C0BB3F0D537F7A46F20D3E686EB9E459631B4252BF2E0D51BD3BADC07F8C79A91375AA2EFFACBBC31B17G7D3900E334CC4A66344C3A4AE959D306E397C1FBB9G3946717FC1715950AE81283D46E9BC769927316566CF486A4E8A5366B0B4F6DCE334CD3E9620316D2CA5F587351941BA54FBBC74AD87D88310579E19FE822083A0075AEAD16DDF60D95929D64BEE376C2974FA2BA43BF2CE360B923CDBAC502361
	9AC51B2C5889E17618FD34A884BD9C7C6DC3ED910171F609F07B09EDFC06286AA1D3EA885E663E314B94252D91539E60EDF1BB4A6DC32A5B05700E73F0BE43EFA827726C8860192EE6527158AE023E999DDC4EAD1BB8AD75A90F114C529326F5A1E8197573B2532A8EC6C306902005493879AC4A7888602DG92C08440EC00F8014B3837767351E4DC87762FF88429292753ABA8EAD50FCB320B0AA66B6A6C11E49D8A583C0BE31E4764B267F3230E167E68F8C839AC1D13EFDA2649E335C1AEEC6FA9E1774E6E9C
	EE9B1BF49D5ECF4FC4D1A753E1320E0F2178F2831E6936106231DD8FFDF1GEC7C5543085FEA8B3691A745975A02ED64F7E55CC64A21AF860069F7D5BD7A4594BB1C9D00A3G8DGDE00A80098BB57714FEA0BEF719CB598744B66CE2A7B981E22DFD504BA0FCF95BCB631D9D0DCC227A47A08B6E6DA9D4A35F3483CFB697936419E27C50F28882ACB76406A02DB84E37231B18F55ED41B10AC8FB2DAE37889B383D042FBB3BAE8B1E1260D33F725A85D58C0CE07D31F5E8933DC2B32C888B40FB229D65D5CCFD6E
	C2F8DFEA67327A74BC628E09116975A2174BE7E7BE0267D9B0EED132D8EADCB6A4C3D07A390D2616631C8E983FD46467727D96EEFFB59B1FA6C5EFD40434BF84EB9733A529AB587EB2693A51001F5B456F50870E360CE27FC843A60D1E311C1B40190B9BF50FD46D19BC467591181C27DFC0DBDD8CFCA582E4BA382CBB4F64B19B2C92FCA2373A8B8A980CE88FC81E0DEDBF0BF70A58ABFA549A51ABFA6C706253F45EF5F6FD409E3BE45DD6A40AC77FD48D866353714A59AD8C8FEB982D1B778F50FEC1D595D7E7
	0FAAB6092A6A72B88273A7D0DAFCC3DC1FCF63F5E683BD2F40638900861D57747AAC03DD554BF2F70F570AA7C6334FC769BDEC0771038E2E7F074AB0260A515BCF50F6205FDCB61A6E4682384FF07BB87D1D6E91ECC3EC8F5BE1641D7BC90673D05C8220A9C16777FFA8ADC9A15679AE006D3169F00B36FE6A9D580359421F6A435BF26A3950361CFEAE34ADEFB9B7322D6EB8974AD6D9DF5EB964D16E7398ECFF6FB974FFB65963916981542790968B3FEFC5BEDC9E17FA49E5F708EA13EBC0849F6AC49BBEC5FD
	EC863C67B939EF6C7BB3770D7983CFC28C34A3E47CB46B6F0E187CCEE6FFD14E00EFE4B4951C47B3A5CA229B84D5AD77F8D4AD96247EDB437B12A92E7CG4AAC2F700C95E4F84AA5CA768B5ECA3BDD91FD3EA29B1CF855CC7F324B23703A50AB784D3EBE9019196A43DC0D3DA771F5DF5E018395F94191FCD117CB916BBC5E9E3E443E66FDA5046DFD76240B6D8DF7EA27DCA57BCB0B84AAC4B3FBB408CA172C38C5BB5D57AC74295A261534FB0C3B16926EDF63AF22FCE2C1EE8B8116B8394F19F39E7B394F519C
	EB35ACC9CC3550D76EC23DD515EFB568A251497235CDFA3FA7ECBF5B673E8B19E8E3620D9E91C281F4EAE745FEFE1FDF78A8282FD910FA82F1426C8B18CCCA86FF48708FB4045679438D68833B75759FEDD8CB703EE7EBB29B0D93F0CD2F62F24375018EDE4BAB67E79B734A88608DF350F8F8AE81E0F3D336A3AF41720F38CEE33A43BC67CC0471E900AB3C4E5630DFE772E063ECDF70F10B203D9CA02DFB68385AEE9A7369F9E0310ED3A96EADF0C613D2F8FCFBAE5318435AFD25A14F90BDBB5DDA8E529D9C77
	7F211146BF00DF544D6DA3672271CE2A7479E41B0B06DD1A8EFEF951F83E986E350B9B60896668039C40AE7A399E5F380834E2DC2621B5DDBC357F98999EE7B25172AB3F3A0F1A3ED52E06503096CC21DAF7BF4DAE310FF0B9FCFD4A188BAF58DCE0CA8FA307840D7F94A9A007BC48B399CD8FDAB03F89154F5BA811E6C70F4B5C233AB0G94D4011EBBEA8604654CFBF624E97C55C8BCB7BE1F0B76BA886DE800D8G96273FF81847697100DB03636919CA7C0B71CC9DF2C72637810C83E03E24648B3409B1FAAC
	CD66717BBEE2BA8AF299DC0E0FE51D5076FD791947C879376BC2666B13B7EFB1C53B35FA0ABBA047654B8BD357GB07E223E607C25F9A06671F07EDEEFB4726787FC8CC03C0773772C65A0BC476E9E753C7C2071CCACDC36B76C19D862E1342F70841F09DFD9D10FE9F2E4FA2E4C4FC4C363964319E884AB94BC8E70CA7A5D5D184C7C0F56B6B525F27FC338BF5E590C3C17401A75GF600C1000774BD6EEE9AB53FBE699FA13F7EECC7F02E3AC08E44EE0B646139G4BA5FFCEEDB4C18E4EB1DF217855F2F00EE9
	4D653A4D70C28E6965F158EB070377DF6C9D3AE722B7D4AC792BE1734CC16D2DB7064EE3763C37B0171FD70379DBAC01BB1E797754DDC67FDEF40345528D70345F60752E4F76F31FBBF80367015ECB387EDCE8AF82D88E102200EF872881F0AADCEFCB1AC2793221FA8B674882B98613C50B6576E458B5ECDE308CA7AB21E46810F2C24B1047AA5A9D394691CA06B13066DC054BB02D094B70E5054BB02E85F1A93E48742DBEAE2FB26BC643D9EA10DD9ED5BFC73F36623DADABAE074B43F16FD11CCDD6E8C04AF0
	FB1A43474A7B1AC3474A454D21E365326611733EE34D61E2E91623D5D03F1F8F7C9D81E0381C9C4435C1DB75717371C2AE62E6C1BBCE477DB345210FC7BF48F02D39465C8A47871F5B389038DB853607EF489E2172267FCF1E330B2E7962471B29DCFDBE51C78FG5E1ADA5FFB6AB692A21FB8AF38C50D1EAC32760B755A18132ADCDD2A5B6C6F77E50736D9C3AD7D582EB40D5797FC7CAE2EBC06FE0B28116959GA5AA17F7517E116A0B356AF0D9EB7500CB61EDF07C2550B6F857A534FD1F5C235844E0BB1BF6
	A9DB6F8BF808BAD5F423D83478174ED78585F22C6079FFCB67333EF0737F7ED2B8DBE6B21FEEB14A9C6FE2A64FC3CEAE4FB6154BF3E7A34AB39A5A31G713ABCB7B60DA44F25E165B92F856F993F5EFE16367BF55A3E5A01FEC0927A659EF098EDBDC80334037B25C4D8B78DF0A5GB5BD4CBFB1FB79E19F4603DD0A6086D61B3C220D9C69453987F3785C59B0FEBE40920055G593DBC9EF95002BCEA19A235AF067F57AE0CF79BC5200605005A392A5AE564FD9C2C935D9B60F7CEEF38BBF89E354D853D41F770
	C20ADF549BFC871FBE466F6084682B6D63FE27FABF77BB31FDDC9FDFBA0DB42D81FC26BFC0C73EBF9CCD5328FE0F7803E9FA10626B7D41B465B5F21ABA20AF5E4FE94AEF62B495767378B7FA877F5D498F3838FE2E2B520EA1F7BAC6FB964BE945E52F929C75D4734C9E176E603521657D5C9EB76C44B57386408F83B48C70DA033F00678731CFBCC66A479D8F79DB59B2EB0149B920559A8682B12197728336E7CB8E5F738568A7B7919719E30CE751B6DEB7313B17754F9F520FE78FC2D5C37FEF0774A33D4F8F
	191F1BEB6CCFB54CE73C3D341773366625B8A24DBB9076F7405A1BDA0DD9FB7ADF5BC99A9E59174AF4F4197A4B5950B77FA6575B64D644AD45360EDB5746F539F5105B57CF0F519AA534476938FF243818C1168FB35C39E37C4E4131E33DD3460DB2C70B0374BDEE2796B85A1D37EA90E5503A3FE614B5128D309066AC86582675AD0777B7B59DB119B6D05FDA7EB5586C57DC4E76EB08D321BDD9473D5B2E1F6D2F39CC23E97E3E865A395FF05CA394077505C3BA2E20F12C75DADCF324FE5CEF286C0F0FF90F51
	F4E45487137D512611E41FCC82F5E07A5B9A50687CA6A07BC1F8BF2B0DE1B4FFFFD84EB49A30F9F3EEF35EBBE31E47FC6ED26A5BA6FF43FCEE120E7F8D45C79B704C3FE66E647EB5867AD23E6571F8F7AB5EF9CEE8C7GBC84B097E0B1C06237BCBE2F38DCC2C649E72C32379E829F4990AEB37E2E665C5EEF35BFDB477D585C67FEC066C72ECC8AD743C999476CAEDFFBE63E3A136299FFFF3A027CD9214F8B908D908B3090E049B84EDF6465A2469F161BAAD5D530B9E9A50BB3074525G9A8386F667FBF7E17C
	51A9ABB468E27147143DBC06D8816B270C0FCCBF81E09D4F6AB6EC5EA32D4363169F3772F952F89637C440F316B60F597A6E3651EC5D88F1B0B7FEBC33F5ECAF01775FFD7861072FGB0BADEAA43F875CC3D50A9CAFA75DB0B2D3637E04DAFC4F5DC2B752EBEE63C0792B4BA53A6C426FFC2679312C678FF0947076340DFCC406F038E5B082E73F7B2384EF31FFA1C543C39B2694FE1F45EB10169BAEEC260DE1E2FBCE5E234BB1C4375720D134BF779842697B2916E2C09AC4FE273EEDE9B2E176FDD63730AA6B2
	3D544033C51B477472C17BE8FAB15E81DE189BB1B12017D9703EF208DE1EB909322531A44D3AFDFA724D7A1553693C1FA9CF5349573B03F502B44E1EB8D61DBCE547F7F6FEFF3DB3707E1F06770FF7FE173F7705760D45F7F48FF676555670B95B4D5650B95B785650F5033BDAC77E0DEDFAEB785F586EEFB57E4646FC57CD9175B99F743798E0B5C0C6C4E4FADE8477DB79D776906A37C2782C13F687660C7855054A77DF57961E57CDED23FF2B313DED83BDD72AAA3BF1FD0E5F55067AD2E5AFC5EA7518B61411
	62F2B8D5B6D4439752F8D1923BA81A5DCD4E827E2DCBB97026C670587F798DF86F5CD27D5D6AD2A5310A1EF8961B4795705AFBEC844F875E38CC6B1A50DE8330F5924FBD5EEE65F7E391606C13F87D2A608A0F6F9F1A445730DE215FA8C0FB85C0CA642D1E2D44636CECEDF2902257AD3A4254AD6821E6B494D7F0BEF2A3F97E725A95E3CDBB737B5BCC7C2E8F36F1EB24765DC7E4F0CD3B5209BEAB9A70F1G0B81D6G103BA05785A882B881E08510GA2GE6814482AC84D88410G307AAEEE0BB7ED1807B48B
	57077FF283C8ADE1238629882C3E47E47F04136BBEE3B2785649DC766FBBB95F0313396CFFF895474E05764BG8913397E1E3B46753786F0076E66EB185A397E243B799AA56DB8F6B63497GAC3EFB2C7A132FE30B2DBFF7B727F1455DDC8E934570E7EC0AB87A993BD7243F2561596877EAB51AD9A22D01F6F579C47DACBFACE66A38BADF036CF3E1EDC77BA622E03418ABFFCA281A71602873F68735593CF9F45E09A9371643D92770EF596A6839F3CE890EE19BA9DE1D929C433EF5196BF99C74A5CC6139CCC2
	6B6D455E7FFD1B7376DF393D187D856ABBEA2786737BBA45DB2786735BFA05735B81FD71D3B93F07AAC26DDF98257D969FB5DC8F6C4C67F6236DB27BE9F46CA5E14EAB5C9EE7409DDD683CAE5AD42B605067FC658CEF5BED4E51EDDBF4063EBF3A1D436F8F2FB3747D516BB45E9F1A6FF16299F4000FE951470D53F117F55CA43AA662AE396C2AD347CF25F4A13ED6448DB9FEA6252B0F62DA78BDED40DD56F0FFC363CDE059A96B3FC744B8717B03AE17A4E95FAF50B1AE8F9CD7DD9ECFD29ADDDE41C077B3BA6E
	32EEEF314E5BBB4F4F0DBAAF74F97EBD1D5790420EC201BD0A1F673D548F7B23026D5BCC7191D141767D132B5C3EE7C05F2AA8EE5F8B2F5D5E39FC75B6672DEB3F3D73BC1B5EA5AD7784737BB4455B6F896677F0BB67578BFD0B6F617CBE3BBB547EC726B17957CF9B2E87761B49422C0D5426A417C72CE6DF4B92DEC3FA1946BF9DB057BF0D570BD4BF6222209DB30D6FFD211568F2E8B13C3FF3794E47779F7C9576F875CA0027B78D6F9DD76E7C5E7DF7F48F265333B4BE0C85FDAE50759CD7416B02AF68388D
	AE44E5CF0FCCAF194EF1DFDEC5DC8D34DB26F3FB58FFADA023C30677672F5EF9F97D4BDFE10F9F3487F87A11613D3C7D4E6F7D559D5D0369F4910DC96D20CFD557F359A75C9E46CD67350EF78B70AED4B1EDBAA553F8BCE3860E7DDF6AB7964138257A58297ED4BE16709A01570D32E37D1D95F85729F07933BEDACE536A337FD4B1161A55E2C3ED9C7DE4835014C582B55CAC7229ABB45DDFD27BEE03F7678CCEF7435561F4FF6FC0006E77F68F25FB1EB9C0F77DA7A3511DC40656151D063AB262E74DE0B9EC12
	3E6F9F8F8576F57A715F42F9443EBC7D83A66425F0CFA584CC521E223CD2787F9066D3AC5F0B69B366FBC7B66C8F7B8D536C996C2E182FBD93A02AE07BBB8E931D16CDDF86E8B9F075D6E9E1F3CBADB8174513EFDBD0660930D746CC1E7FD4156DA761734086C5760A0A5A4F30748BCFAD6EF95702746A35935625757D5602F5907C1698C8F28D50CF8F1BE02047417EDD6227FD8DA0105815A45527104DBA9FF5B458DA567B39A5795D4713CDA4250E2CE541E79DC94188320E2452G330EA4C3706947FFBCF21C
	30048E1FA7FDB748B7D4811504250278D364FE9D4BFF6CC4A3CA5531A78DD89350739916AEDC1DEEF3CF2F5BACF42AE67C1700E62BA2F8FC1220E2FC4CD8A16C3FA493C9EDA5D9DDD8F3BD8F213642C70AF5E48B45B60A6C0BG4C8F6B4976A1382A9E0FDD927157FD15FCD40F12A816840F2318EDF1341EE48E99FED66CC7325756EB9BF2BC9BCE3B28D46038FBD532C12BGC317CA36DD70EA7FB2C71072348ECB1ECA56523224CFA5D974A58FAB17AE8F9DE84983F1EC9CE2E63FF8346C5FDFFE3B5917CC5656
	119DDD00E992952AE4510E9F07E87FBA6794D96F452F0270A3F2501D0FBED2FDEA3F8492585A5508DF8D7BD451DE494D87765F46584772D0F12D60F3D2D10384D2BCA01DGEBC9159D14C1FA2A013EACCA9F4EB26B3332FE3AF96F27E79266A79353A932C112857BA9418676DBC7562BCEE05CA9CBF63F7778893AD0DA9FDA8A96E8D3E0399C1DDDE44E0C65F41D48EE7F43D19B17EFBCE50218D9A145F4A4BAA333BE42DE6D949458CAD46AD414362C646BFD63F72B852F1C5E5EE6A2C9C319829DF489EE17548F
	AFB5A0CB59C1EDF38BB4D95A229F92B48D73A960C77D3715AFBE76D6F9628AD86C94C925DFA982C91901D167FB5C1D229299FB5F2E7468E5D311E2AF324BD7DE2F221562D71930F8DE904F3CCBA75DFC6F0BA577BD79C1ABA5BDB0D5A51FB01D4154E207A83B0BAB3B3AA0345597B86A7742723B7465F10CB9E84CC77E195B4E3C71E1A7D8D48DC9F61714503F25746FC17CDBCAB12594D3FA905416C8C67CAF385F4FF926E6C89B59E8C3FEFC5B669F62B3F6E8FE200EE2B306199BE2B78F379BC4E7695A435647
	78E7A77E492113F6513FF9E3269DE76D89223950BF44F7E19A0F61CA893BFB76FDEB3CFBD6CF4A88FB779CFAC07BE67D0160DA63538348DA8460E39E6077449F7B71A28ED7FFE8F28B12248AD2F725470E35082AFED58C54517FE1G577771B1DA27760D50G52FD5DB0677FGD0CB878897123BAEA396GG98BFGGD0CB818294G94G88G88G59DBD9B097123BAEA396GG98BFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGDD
	96GGGG
**end of data**/
}
/**
 * Return the JLabelOpAddress property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOpAddress() {
	if (ivjJLabelOpAddress == null) {
		try {
			ivjJLabelOpAddress = new javax.swing.JLabel();
			ivjJLabelOpAddress.setName("JLabelOpAddress");
			ivjJLabelOpAddress.setText("Operational Address: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOpAddress;
}
/**
 * Return the JTextFieldOpAddress property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldOpAddress() {
	if (ivjJTextFieldOpAddress == null) {
		try {
			ivjJTextFieldOpAddress = new javax.swing.JTextField();
			ivjJTextFieldOpAddress.setName("JTextFieldOpAddress");
			// user code begin {1}
			ivjJTextFieldOpAddress.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 999999) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldOpAddress;
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getRelayCombosJComboBox() {
	if (ivjRelayCombosJComboBox == null) {
		try {
			ivjRelayCombosJComboBox = new javax.swing.JComboBox();
			ivjRelayCombosJComboBox.setName("RelayCombosJComboBox");
			// user code begin {1}
			ivjRelayCombosJComboBox.addItem( CtiUtilities.STRING_NONE );
			ivjRelayCombosJComboBox.addItem( IlmDefines.SA_LOAD_1 );
			ivjRelayCombosJComboBox.addItem( IlmDefines.SA_LOAD_2 );
			ivjRelayCombosJComboBox.addItem( IlmDefines.SA_LOAD_3 );
			ivjRelayCombosJComboBox.addItem( IlmDefines.SA_LOAD_4 );
			ivjRelayCombosJComboBox.addItem( IlmDefines.SA_LOAD_12 );
			ivjRelayCombosJComboBox.addItem( IlmDefines.SA_LOAD_123 );
			ivjRelayCombosJComboBox.addItem( IlmDefines.SA_LOAD_1234 );
			ivjRelayCombosJComboBox.addItem( IlmDefines.SA_LOAD_TEST );
			ivjRelayCombosJComboBox.addItem( IlmDefines.SA_MEMORY_ERASE );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRelayCombosJComboBox;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRelayJLabel() {
	if (ivjRelayJLabel == null) {
		try {
			ivjRelayJLabel = new javax.swing.JLabel();
			ivjRelayJLabel.setName("RelayJLabel");
			ivjRelayJLabel.setText("Relay Combination: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRelayJLabel;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getRelayPanel() {
	if (ivjRelayPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Relays");
			ivjRelayPanel = new javax.swing.JPanel();
			ivjRelayPanel.setName("RelayPanel");
			ivjRelayPanel.setBorder(ivjLocalBorder1);
			ivjRelayPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsRelayCombosJComboBox = new java.awt.GridBagConstraints();
			constraintsRelayCombosJComboBox.gridx = 1; constraintsRelayCombosJComboBox.gridy = 0;
			constraintsRelayCombosJComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsRelayCombosJComboBox.weightx = 1.0;
			constraintsRelayCombosJComboBox.ipadx = 37;
			constraintsRelayCombosJComboBox.insets = new java.awt.Insets(43, 8, 65, 28);
			getRelayPanel().add(getRelayCombosJComboBox(), constraintsRelayCombosJComboBox);

			java.awt.GridBagConstraints constraintsRelayJLabel = new java.awt.GridBagConstraints();
			constraintsRelayJLabel.gridx = 0; constraintsRelayJLabel.gridy = 0;
			constraintsRelayJLabel.ipadx = 2;
			constraintsRelayJLabel.insets = new java.awt.Insets(48, 22, 69, 7);
			getRelayPanel().add(getRelayJLabel(), constraintsRelayJLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRelayPanel;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) 
{
	LMGroupSA205 twoOhFive = null;
	
	if( o instanceof com.cannontech.database.data.multi.MultiDBPersistent )
	{
		twoOhFive = (LMGroupSA205)
			com.cannontech.database.data.multi.MultiDBPersistent.getFirstObjectOfType(
			LMGroupSA205.class,
			(com.cannontech.database.data.multi.MultiDBPersistent)o );
	}
	else if( o instanceof com.cannontech.database.data.multi.SmartMultiDBPersistent )
		twoOhFive = (LMGroupSA205)
			((com.cannontech.database.data.multi.SmartMultiDBPersistent)o).getOwnerDBPersistent();
	
	
	if( o instanceof LMGroupSA205 || twoOhFive != null )
	{
		if( twoOhFive == null )
		twoOhFive = (LMGroupSA205) o;
			
		twoOhFive.getLMGroupSA205105().setOperationalAddress(new Integer(getJTextFieldOpAddress().getText()));
			
		twoOhFive.getLMGroupSA205105().setLoadNumber((String)getRelayCombosJComboBox().getSelectedItem());
		
		
	}
	return o;
	
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
	getJTextFieldOpAddress().addCaretListener(ivjEventHandler);
	getRelayCombosJComboBox().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SA205EditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 360);

		java.awt.GridBagConstraints constraintsAddressPanel = new java.awt.GridBagConstraints();
		constraintsAddressPanel.gridx = 1; constraintsAddressPanel.gridy = 1;
		constraintsAddressPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsAddressPanel.weightx = 1.0;
		constraintsAddressPanel.weighty = 1.0;
		constraintsAddressPanel.ipadx = -9;
		constraintsAddressPanel.ipady = -26;
		constraintsAddressPanel.insets = new java.awt.Insets(5, 5, 1, 4);
		add(getAddressPanel(), constraintsAddressPanel);

		java.awt.GridBagConstraints constraintsRelayPanel = new java.awt.GridBagConstraints();
		constraintsRelayPanel.gridx = 1; constraintsRelayPanel.gridy = 2;
		constraintsRelayPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsRelayPanel.weightx = 1.0;
		constraintsRelayPanel.weighty = 1.0;
		constraintsRelayPanel.ipadx = -10;
		constraintsRelayPanel.ipady = -26;
		constraintsRelayPanel.insets = new java.awt.Insets(2, 5, 69, 4);
		add(getRelayPanel(), constraintsRelayPanel);
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
		SA205EditorPanel aSA205EditorPanel;
		aSA205EditorPanel = new SA205EditorPanel();
		frame.setContentPane(aSA205EditorPanel);
		frame.setSize(aSA205EditorPanel.getSize());
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
	if(o instanceof LMGroupSA205)
	{
		LMGroupSA205 twoOhFive = (LMGroupSA205) o;
		
		getJTextFieldOpAddress().setText(twoOhFive.getLMGroupSA205105().getOperationalAddress().toString());
		
		getRelayCombosJComboBox().setSelectedItem(twoOhFive.getLMGroupSA205105().getLoadNumber() );
	}
}

public boolean isInputValid() 
{
	String utilityAddress = getJTextFieldOpAddress().getText();
	if(utilityAddress.compareTo("") == 0)
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}

	return true;
}
}
