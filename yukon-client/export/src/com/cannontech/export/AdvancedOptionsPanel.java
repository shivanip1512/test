package com.cannontech.export;

/**
 * Insert the type's description here.
 * Creation date: (4/11/2002 3:51:40 PM)
 * @author: 
 */
public class AdvancedOptionsPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JButton ivjCancelButton = null;
	private javax.swing.JButton ivjOkButton = null;
	private javax.swing.JLabel ivjDaysToRetainLabel = null;
	private javax.swing.JLabel ivjRunTimeHourLabel = null;
	private javax.swing.JPanel ivjRunDatePanel = null;
	private javax.swing.JPanel ivjRunTimePanel = null;
	private javax.swing.JLabel ivjStartDateLabel = null;
	private javax.swing.JLabel ivjStopDateLabel = null;
	private com.klg.jclass.field.JCPopupField ivjStartDatePopupField = null;
	private com.klg.jclass.field.JCPopupField ivjStopDatePopupField = null;
	private javax.swing.JPanel ivjAdvancedPanel = null;
	private javax.swing.JTextField ivjDaysToRetainTextBox = null;
	private javax.swing.JPanel ivjOkCancelButtonPanel = null;
	private javax.swing.JTextField ivjRunAtHourTextBox = null;
	private javax.swing.JCheckBox ivjPurgeDataCheckBox = null;
	private javax.swing.JCheckBox ivjAutoEmailCheckBox = null;
	private javax.swing.JComboBox ivjNotificationGroupComboBox = null;
/**
 * DBPurgePanel constructor comment.
 */
public AdvancedOptionsPanel() {
	super();
	initialize();
}
/**
 * DBPurgePanel constructor comment.
 */
public AdvancedOptionsPanel(int format)
{
	super();
	//setFormatType(ExportFormatTypes.getFormatTypeName(format) );
	initialize();
	setPanelsEnabled( format );
}
/**
 * Return the AdvancedPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getAdvancedPanel() {
	if (ivjAdvancedPanel == null) {
		try {
			ivjAdvancedPanel = new javax.swing.JPanel();
			ivjAdvancedPanel.setName("AdvancedPanel");
			ivjAdvancedPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsRunTimePanel = new java.awt.GridBagConstraints();
			constraintsRunTimePanel.gridx = 0; constraintsRunTimePanel.gridy = 0;
			constraintsRunTimePanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsRunTimePanel.weightx = 1.0;
			constraintsRunTimePanel.weighty = 1.0;
			constraintsRunTimePanel.insets = new java.awt.Insets(15, 15, 10, 15);
			getAdvancedPanel().add(getRunTimePanel(), constraintsRunTimePanel);

			java.awt.GridBagConstraints constraintsRunDatePanel = new java.awt.GridBagConstraints();
			constraintsRunDatePanel.gridx = 0; constraintsRunDatePanel.gridy = 1;
			constraintsRunDatePanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsRunDatePanel.weightx = 1.0;
			constraintsRunDatePanel.weighty = 1.0;
			constraintsRunDatePanel.insets = new java.awt.Insets(10, 15, 15, 15);
			getAdvancedPanel().add(getRunDatePanel(), constraintsRunDatePanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdvancedPanel;
}
/**
 * Return the AutoEmailCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBox getAutoEmailCheckBox() {
	if (ivjAutoEmailCheckBox == null) {
		try {
			ivjAutoEmailCheckBox = new javax.swing.JCheckBox();
			ivjAutoEmailCheckBox.setName("AutoEmailCheckBox");
			ivjAutoEmailCheckBox.setText("Auto Email Group:");
			ivjAutoEmailCheckBox.setForeground(new java.awt.Color(102,102,153));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAutoEmailCheckBox;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD4D859ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8DD8D45715980D411498B54420F1D3B4A6CB9B3EEE32217D5C565DE01BEEED63FE255B24F133F4BF36F57B5946E4ED6BFE6B37355B8782413FC8A2CDC8E5AB29A602A290C1E58DC4859486A6BA9544D1C9621B19878C8CB3634C9B99944D1EF36FBB77BD9EEF401FF53E6F785EBD675EFBFE6E39771EF35FC529E8C7F2DEF6CE11A4E567C95A6FD036A4ED36C9D25C196FCDA1DC41C5FB3EE4783D8B
	70063421AD971E45C02BCF316797CA39C385D02E00724205767C3761FD1BF472002F99DEE270E9865AFD0FDFFBE5FCBEBFF90F783C464A2DE7B64373F500EA40F582A4DFC47E974F94EB78A814DF575E9132F3C0AE996B6CECA9B660D7E8750AE57EDC436ADCBDD32251D5A8A7GAC85481475BEB7B0696EBEDC27153BE7A6CBEB3E77C64E9664E5681F205E455BDE541E25529CE60875524AF74A106E98687D489D982CF44BFEFF402FAA6E3ECAA59A8C045443071B9B9D11D02F62107D0AAF43057259DA8AD8DB
	C9CA02B2176587DB6AB5DE098349D2562339B1E5A9F2F3996A5C3AAC09173E1B3B520DFDAFE43645F6D1A8BF894065FC3631FA82B90FF9AE4BFE3762B995D43D81FF58A06F2AB3A4EF05471E1F6AC1DC7789646301779100C48F6951301B74F0DE7DABE92D2BB6E7EB8CBDD6FB3431D3F4BD2225EF653F43FC443F9B473993E8D1008400A781B2GD683ECE73ED97E7E9EF89EF329CD014141007FD8B058687304BE8EF8949F3C03C6AED5431DD65C0110C712387F1DEC293010072020E7346F697EF2D6A2FFC9BD
	7D38242AFBF272ACF4CBFBE1DBCE0956C7F50F2E9B57EBE3B96A65G3C8FE08A40BC0045G19BD24D7787D1A0974324158057C322FA3628A4AA135B16275F91410265F174F0C271F9570F60BEE3A9D37CBCA0D51AE19CF9F48F94D42AE19A9FF48D92B592330D7378B677D4B183C2BFB470E87C2E7AF7AE1867343E6F84F56FC1463DFE278A4831E4F8F9743E3B985E81B7B48EF1D2F138E765C39D2618787ADF5C87A665E1CE09F17A73E4FBC36BF5B036BCE9A60B3GD682C8DEFBFEA9C0BD0043CB637BF76FD7
	5D205FBA8CE34B5B8E6DFE8B1ECAD48D49B6FFD845995BA5073C324B2704A52DCE5CEE145DB52A5D5DECBE3901C70B62D7C2B24EF168DD9ED44011423C4E745DEFE01D1042281D5EC185988C86A56AF7766EED707449E175CC50A32B0ADE03539F590D363CACF7C10F50813CA7FB51668ADB6F02701EG40ED352D82F1AB203CCE58651D32B761598E0E2D78E05D743AD18CB9B4CC7C371563DA22DCD67CEA3392D47C9EF889EB34BF14EF506D529390B217B2BC4E11A690BCAC706F173F41715007514AC42FE4F4
	D9D5C3DED7C4D5BA94D5757AFB75768719AC61D153AFACE9F3FC9F7AD8E13FBD3F8C202E1F74FB26FC1BC1BFFBA0B09089F66248E9F6AA68C21B58213E271F764A9F1D403DB33E1F7C74D5A779E9731647240A97EAAD7D94A1251F7BE7FA3FF92EE531BEF59EFDA71CB7B96F4D905FEFBDC77535E552ED73D8BF8E0D5B7DDAA55AFDA54812BBE04F5FB5C0F67F2F72D2BE5F9A6530C2B36CD488C6CC7168DE466B2E5E935B477EF30F350F7DF60F350F2D5FBB3E8FED5AEB65C31C7666DE5CDB82C3FE03CF3E3D77
	CDB6077DFE0589AD1AC8FC0FFF3593ED6175FB5533DECF2F22F6F82FA8B04E8368DBA3EC8E56433BE300FCE9E8A779522EAB7325744F36DA6EE7C90FD566248C7071DDBC207B9217293882ED2E7814C1B0D4D3A062D7B97E0FECFEEBF8EDFDA95CE7DD772DFD680F834A30D66F8F7BB0083CAC7BA20A506DC9F9935AB8649D042142892B4503BB3A0C71E0A6480731E3094F1EDF8D301E75D7DED70463BFBA3EB901EB9F771B2FD521DC2DD03F9BC075115F4C5E770EE13EEA8E5394707938794538CC2EAC44F1D4
	AEC594E000933ADD68F2EF651BA6DA172697274F2AC43DFC0635A04107FABDA0E2C45CABB86E4B8C7717B021F664515E7BF121B1067504F7DB0EA1C2060F0F5D8A1FF95FFA399A7B4D9D3467978D126EDB2A0CEB5531F0B8607632EDC19B0FB907B1C6310AA75DECD32B6C0DF8ABA32A9797F2D90579920C28AC2414F8CCB90D2DF558D759A04514DFBD0763F0914AD10099C21645D5E3FDE9C955C6F876F8C3E10805BCCA146C791DAA348B6ED19A1AE2377DC6BB25E7ED0FE92714C14D57874DF67AE0BF4A319C
	702B04EC0D55EF986C84F1A6EC493DB00AA44F6A6A755C3735721A6ABC72ED09564669DD6658C7726B6BE911FF6C5A4AE31C7273E82FB2BF0FF172843E16616B8DF89E7BFC0359974B8E202579E9AF5EDD03FA9587E03F8A10DE53BE58E2504B91707AD52153417DB8BF83A1EF2F57CF388F998E22EC36E0717D6CB1B646F550279D00636EF232D8G4A71815AFF2A4EA3AE954ACB856EB0432D047266A061161C3B5DBD84F918F1CBB59C677152ED73305A277234A737D5C58D4B4FC4391A15073536606FDD794C
	07078391B07A3CA05A6D07E21D2B06F2ABC090E0CA10478AFCCEBEB504EBC98FACEC304DF4849537943D04ED67B8296D8A283F96209870F5G4ECB34C6FA59F8539A49E7AA49F35FAEDC8BCE8DB4A14D5798D1D50C9D3186DF03FD670DEBF07AA5AE538AF82E8560F5CA5A2C6A9406E01F86607E791BAE4ADD6B812706C82F8FAEE2DC4D561AFE7932DCA98FC13A5A9272FA9A65DEBB3392DFD31A4E96E86DD3C33426DCBA0BFD2E0072BA0022B06DFD5177A9CFCDFDFC2EE40FBBE019EFE7F51664D40439FFB842
	7AFA10GF9AA67790C13F8A6G7D59B062DEF196197CE1A0C712AA614969BB4E996938FE6C160C74BF0F22233CBF9855FE72A8BA5A28CAEB4FF5FB6EBD52ED5DF3A9126F2BBBE31EA5ECD635BCD5FB2E5E38DD9AF96A3B395C6E3FE6392A30FB34B2BC840EF578F01B61ECE0A11B27B8F77AD432793F309CAB994A0BGB290D85D4EB6DA73D70A3A6BE42CDB9A81FF84F0C4E82EE71F3D359CF66BAD36EB3D69F6FC2DEDF2227CFD913E56E68B3C1D61D5831E7B7850D97271B8202D08509A7C13DA34F56BE51897
	GA3G09GE9G9917E9CD7EEA8D1E610C1BB7F68602F688DEFD0614066B772C7356F27BA52F11AF25FDFD0134E86ACF33B74570259517398F658EE97971066D8C4F750B9CC47DEA0166GD08192G1281968D11FE3F2D29607A9D77F855E32AAA3B7B580ECF4AF5F915A19D0D8B9C576DCB670D3AAD037E320674F5FE4DD02C7D73F48B0EC5C1543CFFFE4270A5D1737E3962846D1F55C0CB0C122F1EEBB35BF6EDD46739F9B8967F200B797E30197FD5062F9DB6734FF1917FD620258E93FF9BEED91A9F1FB63C
	DFBBFB3BFB594473E6769D65417D62655A58B9592FEA2DF332353556B959FF570E1F13659F081D13EDB9E04C4938DFFF63902E4505D7E01F83F0GF8G22D748279FBE70A76E53967EFC9C123426GBB45D1097F750331F53D7B6044E7BF538FE2DC6C8A409EBD087D93BE71A00E179A88B2A4474DB908B602B02ECF65D5B57C978F62BA6DD3FA981A47ED69EC1DCF843DD22ED0AC176EC65CA2A8AF9338A38A45F72B2F10FFBAD95EF274AA449BD7896766E7C2D0CE9438DF5C56998176EDAC9FB515B90F75AD37
	43A34FD4B647FA5C8641B6324B222BE417DF3413DDB2053E17DD6CFB4B08BD3FF904F013984E896528409538E2490CEDEFD5176691E35C7BEECC9E631D3B5C283D38DFFD6BD0EC5F7E5E2109FD7B0707D89E88BE59B99CD4B8EE79A1160B74740495B107FEFC28D060ECE1C7A06C4544154732AF1E4FE773A3448E7B960E60583CFA9E4F9F12C6A896FE51BDB67EFE55CD716B4A919EFFCB57A01E84409C6562B52C5325B40FE38B0A8356EBD081ED0E827CDEABF7DE53E385AE4723AC0FBB9DE1C731AC6662782F
	F4EBF8482CB91E47588F3618E3EC14AD857ADDFC0D62705DB62BBA4F239EGDC57B9A2D6172EF3DD0B61D9F11D67E2DC57EC7BC43A121E68834D5056F91D6B0AE55FF5332E579AD1A7E30641715F6A45B1BFB540F1E957117753AEE37C9B5564CA06A7276FF4DA5157DD27717DDBB72A26FD2FD20C678255D7DF4EDEAFD947B7051FEB713F766C2E5F2E6D4BEFF9A826F3GAD7EF30AF7BF75D0EE107EB9453B892C6EDAA89746C13E8D30034984F108247F2CF0C763705B6A07C7F4597FF5B345E6A5CFBCACB53F
	78536C1DB1E41F9247E5FEBECE4FE17049657C07960A4BB7CF92F256336FAAD06E86D0A711CE1F5928EE02287B319B6BBE8B65CC00B513A81FDB52C6F659FC972CF1F7D19F9B5A49AEA3F7D19F075A31EE9A1417812C384B4AAE2B0FD86138DD9EBBAAC9F4D638358377E60B3C05BB11507B97AE52E55DDD1497DEBBE17C8E3BF8EBC9CE2CF837F6B237E567E45D26E56DEAAE6F7BBC4B8B31ED62E40A719FB9066B4C3249949B786BB0B668127B35C89E435EC008C58700EC02383E834294FFAF2D51A51EDB4BED
	765CE2BB77CD3763712B2460D8E6CDB667C49B98FE55E4F3CEB4CB4C1F35C06B1EC2E3717D8B4639D9B8F7D38E35B373CD1F4247E059947319DDD1932ED3525D767C920023GCEG9F409400A400D400F40065G2B81F22742FA87D081D08F60G70CC25B10BBBD2BE4E18E90792AC288B497C4C0C5B729F47FCF30ECE55776E042931F28429016552D3394DB6897C6B8C1FEE40EB36E473964BD9C0EB3E07E2880F4D4CBF659E1D6762FBE2717FB61B5B9977F0BEDB847EF906DFE340F37E9D8C0F6562F830F1BC
	717F4E189C6946FC7005DBEC77FA7B2D796E93EC9DDA9DEF765DB406DF97EF765D06F6725D02E960D753C85FFFF7D9714F18261D41CF9BEBEF6E2FCB1AF16FDA8B7422FB4187811A81BC77127F3DF2F4423B814723635C8D78CC1C83CC39D717A1C9FBCF05E7BA4073869A673B609846C461D16BD1BE268CF46EE2F8CF725C793C7329BB4A030FC556F1C9687FDE63FBD20CF98FA6EF03ED66142BDFF12B1DF22FE8D35694BB5E2DEA1AB85E2DEB324E65BEEA9A1B4BB5B5D967F236A6E3AE47F1A71BF04F6B0535
	4AA96AF5885CB901F332BE91F7566BD17B847ED3A69762DB95E4C8F88F13EB08611C9A4EEB401D53F07EA674A5D039AF90223661A65C9BFB3CBE1FF64F0155717AA104977698E1B2FA0332C16EC98207F273DC6B67AC1ED989BE3C6EDE4A3F7AEC14FF15FE0166E8FE834E35A014E7G2482AC83D8A968C50CDE10GF19AC0ABC09FC0FC8251136AE379A076E705DFE6402F545E0396F5D3058CCF4564E14EC589B6985EB7E9CF2B9C6EB5E12F2BB632D7CA82596BBF5A48DE4B043E4F319C2864BEFBFE9DC0A700
	8A108810F69F55F90955490472BA001269300782F4830CCC9767E56E0B1A8C5847F8733044C4CF544AF59ACF422B967DF0DE2A60755AB8FC2CECC436C3BBEF09E1DF531A54AE09772F9B5E1B5A6F7CFA58FDC7F9F01F48E767BD73E09CD327131F045C64A70B26539E755D66586BDEC63375F95D0F1A2D4F6BB21B473F273322B956F99E176FB20B438B6E37676F3A1FE46E10C90FBA01CB13C90FE6017B7989F6668565D4017B65A708CB0772727BA91F14DA471E973C5DCAB9742A7BF98E1DBB8362CAGBC7362
	B97467361BB9AF2806364DB3747382678CF38E6D2DC3DBB44B43614E40E9C5057853AE3B8C5F713E79AE1E0BE867892D01C80811B9CD2959E6EA5B8903549808EAED272ADA5BE3AA3654081C66ACC03E6C16ABFE6BEF6AD35C83589272B9160F5B9A0C7938C733C17C8C4A57831295FD1E200F580C74A7B57AF2C1DF55E7C5DFA568F5E1A3DD1A4969E5B3693CE321B893291F49472A931EFDB37959A49F2B4D6DB7FA5E019007EDE76AE315B253BCD67F73B1366BD041257194C79BA8CEFB22FF8B2385028612C6
	9B495DE6EC678884A34193DE45675168BFF63CE9E8EBA0EB7406900E5531089AB8BEA8FBFD0631524EC6EAB076B999D03DBDDEB73B7051928AC40290ED3A8246B1AD7418478CED15B613EC7EF31B95FD29206F100D74CC4D56053308FE2F4D0A3ECB504F4866B1C5FA1D207FEA14FC76D91C2E4E22B97BF483469BAA38B1BFBFB85A025D70311E669EEF2C571BA09E7AC4783DB63E73E619477ABEA70EE7B02474A8211062615732242CD9486BF136BE25205CGDC16E02BAE4BF372F83264CDA04B2E874C32C8AD
	382F8D4AD16FE0E410C94265A8E6B1D3B5542FFF00ECD470A92B6B751B6A16FE0AF57B201E7AG1D759CE925BCA899F04B9F20331EFBDA692C27B0D15C73E1F5DB21FC91A01AF833E7BD4BED12507D57BDB13ED133E943E508EB20F31E9909A45763926116A652BD03774ACA2C7B42550E4DA25E76E9F186A6BDC87D6D74922E6EC16A6F232E6D567D09D9475B864258166DFF8F12BD1759481EA9004B92FC0A5A481E4533491E0D6C1C4D8165BE003859B7EB4F5F34E109775F62A1BD12E6934FD2715DBEE3B669
	66D5F6CB067BDF833E5E4AFEB76E5C15BDE8A8D452307870FE3AE43A53D67090753D44C638238217A0531D0268C3442F57974B1634B27136658A757764C356E72BDC67BF5A75772E0FA5B1BFDA65BF42532F8C1552D5134AE658AD7DF821396CD0CDC5697E8365F47E1ABCE4BC1F2A5B78EE4CBBE5AB9F6271693A077433E34E7B42A9DD0E6B8EDD0E1FFDF2A3F270F6737F2C37BBF262C65A711871F86B2D1D2DF45CE23B041BEE275DF3E3F321B649FCA6331F610FA6194FE43EA156A2BB501ECC223C636E86AB
	7EC5F37818144D0915C3BCA1D9352B576ABBE236DBA44E5CBC96F522B15B15F929DD02C51D64186D54B036CBB35117EA65CC6D395AC0574EA6471C8D5E58F8E65DE23BF7EE7ACC0F0F775758BA17BB576C874FB0FC61DC339F340A6F8FA5C00B1BCBFEE037BC57DEB1175BE56D5CD876FD4FD26E4D8FEBE74E8F47EAF7C19ECF5F5A186D7EFEDC7B3646ECB75312DF37D6BF884FA926363CDD25B29E3FA48BFE7C1C6C64E9F6FF9368CB9F96779EBE5EAB45BEA7F70482C1A5248EF3AC3B912D49D04576F67EDEE7
	F8DF52FA67F368176EA88F1EBF4EEB205CF3654314D3769E215CF30D40FDD322FA4D738837B31B6AB9846E7BEC7FCC0672E201EBEA20FA9982F744CB75CA7F02F06E8BD42FD6603E40E211A9D0CE92386799AE954A0B856EFFD5EA3BD4601630D82F789108439F21393638CD9F43EF9B5ECF365D79716C3D23BCB49BFAE81C6C0F109D1E7510ED1C82D75FC675D2846EAF4499DB1A400544B9C46E978977E3F1F659A9F0A92C5EC2A827895C969755CB9738F52CEDE1B2643B4904BBAE6E6854A753FC6DB80DBA09
	F394BEA535B3167327B117423FAB714ABE6F95169572AF13BC7EF82683471567AB570F5C4C19C68270C720FCA5B9D9DF43F95FBF69547BBE1CFDB3B938396F54E4F3AE74B3DB6C3C6CF74CEE0B204D3349F486F03EE1223C50A843EA83FF0C8BF3676B7CB90FBEB6A78B81DFB21FF8DC39F0B339E76B7C51BC3A671BF5DC4066EED8413FD9D1BC1C6F5F04A9C66E1BCF395E4B615179F0DE17EEF707CAB2C5670F5D83680E0730BB4AB41F7B9562134748343C95E3723EC048FBA560D7E55FB11F37570FB763B43B
	7CCA640C2973A9A7761D556579E74F0D7898491489FDA0505D0855734DBEF63FD36FBB2D6DC66C9F2B6FF5063E3533048973F7232C858BF8FFB41EA58B4C7D2D99774CB04FA4DF19293F7A856631F8C69EBD6EFFFDCE3745AE9771EADDB07138F7C39D8432C5F08C2FEDED6CEFF682A125B7940878BDB48F7632F5F0846A47AF20F950AD6ED225D970FE4D44FB29461B744CB4700E1D833EF0DEFFDFFBC1FF5F7E09A4E42EEB4076B172298F766F07042A3A5C028765569FFD2A775BF3D16F57F961767A1DA66BFD
	E570399B0AA8BC871E240AFB768B464B01173B74BEB6585E0527A1352E6C40FF69FAEA38326978494E63274735E73EC7FFAFE75F03E34B6F6C991FFE95BF23EAB97D99F1CF2774517174F924CFFF37756B3A3D6099E36B2600AF90AA7B20FAFF850383B5659A2F7B9462653464C57279863044630C292AB1EE1E816D96BEBA7EF7AD5E6E7C28BB820B204D329BEAB7EBD41C1E85ED56CC50CE0BB718DF94AC64F3036396B0DC69A85CA55758FA7C2E6A68FADFD246567B0AE22C4767287CBE66E1DA7C40BEA5F3FC
	C9F40B0E27CF49927FFFC77E2D658BB0F8292FC8F9611034D50C1B0DFD904E3ADCF3F87DCF9FDD1FA3655A24FC7E015CA66562D7EE1B1447BE025B2475EAA0988DCA26DF7812F45D0C0339A365732B072A14F32ADD544165EAAB0140EEB54046D17D95E9A0014CEF0E1D5747267F530B291FB59765C8B9A0F61F42654C9BC20BB0C9B11D324568913185236A0812AAED458FB725EA2014FF3AB92CCA5BCEC77C2532DA0A9FEBCA4B2A776D2FA9876CC6761126943F5228D2B17B0A00852534ECF0304A63299A069F
	56FB9BBFA0D8D25E40CF87256C5BC1A97BB2F0D848CB9327965F851636099D82DB2069F4C55449B3428D5741DF2E91A6287B7F0D8DC92F0FDECF329FFBFBF2617DB5119C296004145FA37B02FDB20CAF7BE3CF3B3449459CD389C1DDD5AA34E35BAAE8DB455AFEE817F25CE1D55A2E635D0867540E206CD6204A860F9C9AE0BD20C82597248DBD21DE97E3G1EFB614485E04BCAC87E13DD5A287661DF9860768E032AF76D403BE99A61C3BEC9FE5753B3A748FE5646554EBC4BBFB71EF92E3DBBA76619E7DC8ADF
	1392D34CF772A4F67FB383702BD2A87FF0346D13E25F2F699814FDBE0894870E79BDF85726F1D8D574EF44F96D58FF186AE8C4CE5B5C0EF2779B5A7C9FD0CB8788BE0F4198CB98GGB4C9GGD0CB818294G94G88G88GD4D859ACBE0F4198CB98GGB4C9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0598GGGG
**end of data**/
}
/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getCancelButton() {
	if (ivjCancelButton == null) {
		try {
			ivjCancelButton = new javax.swing.JButton();
			ivjCancelButton.setName("CancelButton");
			ivjCancelButton.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCancelButton;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDaysToRetainLabel() {
	if (ivjDaysToRetainLabel == null) {
		try {
			ivjDaysToRetainLabel = new javax.swing.JLabel();
			ivjDaysToRetainLabel.setName("DaysToRetainLabel");
			ivjDaysToRetainLabel.setText("Days to Retain:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDaysToRetainLabel;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JTextField getDaysToRetainTextBox() {
	if (ivjDaysToRetainTextBox == null) {
		try {
			ivjDaysToRetainTextBox = new javax.swing.JTextField();
			ivjDaysToRetainTextBox.setName("DaysToRetainTextBox");
			ivjDaysToRetainTextBox.setPreferredSize(new java.awt.Dimension(122, 20));
			// user code begin {1}
			ivjDaysToRetainTextBox.setText("90");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDaysToRetainTextBox;
}
/**
 * Return the NotificationGroupComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JComboBox getNotificationGroupComboBox() {
	if (ivjNotificationGroupComboBox == null) {
		try {
			ivjNotificationGroupComboBox = new javax.swing.JComboBox();
			ivjNotificationGroupComboBox.setName("NotificationGroupComboBox");
			// user code begin {1}
			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized( cache )
			{
				java.util.List notifGroups = cache.getAllNotificationGroups();

				for( int i = 0; i < notifGroups.size(); i++ )
					ivjNotificationGroupComboBox.addItem( notifGroups.get(i) );
			}			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNotificationGroupComboBox;
}
/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JButton getOkButton() {
	if (ivjOkButton == null) {
		try {
			ivjOkButton = new javax.swing.JButton();
			ivjOkButton.setName("OkButton");
			ivjOkButton.setPreferredSize(new java.awt.Dimension(73, 25));
			ivjOkButton.setText("OK");
			ivjOkButton.setMaximumSize(new java.awt.Dimension(73, 25));
			ivjOkButton.setMinimumSize(new java.awt.Dimension(73, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOkButton;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getOkCancelButtonPanel() {
	if (ivjOkCancelButtonPanel == null) {
		try {
			ivjOkCancelButtonPanel = new javax.swing.JPanel();
			ivjOkCancelButtonPanel.setName("OkCancelButtonPanel");
			ivjOkCancelButtonPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsCancelButton = new java.awt.GridBagConstraints();
			constraintsCancelButton.gridx = 1; constraintsCancelButton.gridy = 1;
			constraintsCancelButton.insets = new java.awt.Insets(0, 20, 0, 20);
			getOkCancelButtonPanel().add(getCancelButton(), constraintsCancelButton);

			java.awt.GridBagConstraints constraintsOkButton = new java.awt.GridBagConstraints();
			constraintsOkButton.gridx = 2; constraintsOkButton.gridy = 1;
			constraintsOkButton.insets = new java.awt.Insets(0, 20, 0, 20);
			getOkCancelButtonPanel().add(getOkButton(), constraintsOkButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOkCancelButtonPanel;
}
/**
 * Return the PurgeDataCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBox getPurgeDataCheckBox() {
	if (ivjPurgeDataCheckBox == null) {
		try {
			ivjPurgeDataCheckBox = new javax.swing.JCheckBox();
			ivjPurgeDataCheckBox.setName("PurgeDataCheckBox");
			ivjPurgeDataCheckBox.setSelected(true);
			ivjPurgeDataCheckBox.setText("Purge Data");
			ivjPurgeDataCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPurgeDataCheckBox;
}
/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JTextField getRunAtHourTextBox() {
	if (ivjRunAtHourTextBox == null) {
		try {
			ivjRunAtHourTextBox = new javax.swing.JTextField();
			ivjRunAtHourTextBox.setName("RunAtHourTextBox");
			ivjRunAtHourTextBox.setPreferredSize(new java.awt.Dimension(122, 20));
			// user code begin {1}
			ivjRunAtHourTextBox.setText("1");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRunAtHourTextBox;
}
/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getRunDatePanel() {
	if (ivjRunDatePanel == null) {
		try {
			ivjRunDatePanel = new javax.swing.JPanel();
			ivjRunDatePanel.setName("RunDatePanel");
			ivjRunDatePanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsStartDateLabel = new java.awt.GridBagConstraints();
			constraintsStartDateLabel.gridx = 0; constraintsStartDateLabel.gridy = 0;
			constraintsStartDateLabel.gridwidth = 2;
			constraintsStartDateLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStartDateLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getRunDatePanel().add(getStartDateLabel(), constraintsStartDateLabel);

			java.awt.GridBagConstraints constraintsStopDateLabel = new java.awt.GridBagConstraints();
			constraintsStopDateLabel.gridx = 1; constraintsStopDateLabel.gridy = 1;
			constraintsStopDateLabel.gridwidth = 2;
			constraintsStopDateLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStopDateLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getRunDatePanel().add(getStopDateLabel(), constraintsStopDateLabel);

			java.awt.GridBagConstraints constraintsStartDatePopupField = new java.awt.GridBagConstraints();
			constraintsStartDatePopupField.gridx = 2; constraintsStartDatePopupField.gridy = 0;
			constraintsStartDatePopupField.gridwidth = 2;
			constraintsStartDatePopupField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStartDatePopupField.weightx = 1.0;
			constraintsStartDatePopupField.insets = new java.awt.Insets(5, 5, 5, 5);
			getRunDatePanel().add(getStartDatePopupField(), constraintsStartDatePopupField);

			java.awt.GridBagConstraints constraintsStopDatePopupField = new java.awt.GridBagConstraints();
			constraintsStopDatePopupField.gridx = 2; constraintsStopDatePopupField.gridy = 1;
			constraintsStopDatePopupField.gridwidth = 2;
			constraintsStopDatePopupField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStopDatePopupField.weightx = 1.0;
			constraintsStopDatePopupField.insets = new java.awt.Insets(5, 5, 5, 5);
			getRunDatePanel().add(getStopDatePopupField(), constraintsStopDatePopupField);

			java.awt.GridBagConstraints constraintsAutoEmailCheckBox = new java.awt.GridBagConstraints();
			constraintsAutoEmailCheckBox.gridx = 1; constraintsAutoEmailCheckBox.gridy = 2;
			constraintsAutoEmailCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAutoEmailCheckBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getRunDatePanel().add(getAutoEmailCheckBox(), constraintsAutoEmailCheckBox);

			java.awt.GridBagConstraints constraintsNotificationGroupComboBox = new java.awt.GridBagConstraints();
			constraintsNotificationGroupComboBox.gridx = 3; constraintsNotificationGroupComboBox.gridy = 2;
			constraintsNotificationGroupComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNotificationGroupComboBox.weightx = 1.0;
			constraintsNotificationGroupComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getRunDatePanel().add(getNotificationGroupComboBox(), constraintsNotificationGroupComboBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRunDatePanel;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRunTimeHourLabel() {
	if (ivjRunTimeHourLabel == null) {
		try {
			ivjRunTimeHourLabel = new javax.swing.JLabel();
			ivjRunTimeHourLabel.setName("RunTimeHourLabel");
			ivjRunTimeHourLabel.setText("Run at Hour (0-23):");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRunTimeHourLabel;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getRunTimePanel() {
	if (ivjRunTimePanel == null) {
		try {
			ivjRunTimePanel = new javax.swing.JPanel();
			ivjRunTimePanel.setName("RunTimePanel");
			ivjRunTimePanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsDaysToRetainLabel = new java.awt.GridBagConstraints();
			constraintsDaysToRetainLabel.gridx = 0; constraintsDaysToRetainLabel.gridy = 0;
			constraintsDaysToRetainLabel.insets = new java.awt.Insets(8, 5, 8, 52);
			getRunTimePanel().add(getDaysToRetainLabel(), constraintsDaysToRetainLabel);

			java.awt.GridBagConstraints constraintsRunTimeHourLabel = new java.awt.GridBagConstraints();
			constraintsRunTimeHourLabel.gridx = 0; constraintsRunTimeHourLabel.gridy = 1;
			constraintsRunTimeHourLabel.insets = new java.awt.Insets(8, 5, 3, 31);
			getRunTimePanel().add(getRunTimeHourLabel(), constraintsRunTimeHourLabel);

			java.awt.GridBagConstraints constraintsDaysToRetainTextBox = new java.awt.GridBagConstraints();
			constraintsDaysToRetainTextBox.gridx = 1; constraintsDaysToRetainTextBox.gridy = 0;
			constraintsDaysToRetainTextBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDaysToRetainTextBox.weightx = 1.0;
			constraintsDaysToRetainTextBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getRunTimePanel().add(getDaysToRetainTextBox(), constraintsDaysToRetainTextBox);

			java.awt.GridBagConstraints constraintsRunAtHourTextBox = new java.awt.GridBagConstraints();
			constraintsRunAtHourTextBox.gridx = 1; constraintsRunAtHourTextBox.gridy = 1;
			constraintsRunAtHourTextBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsRunAtHourTextBox.weightx = 1.0;
			constraintsRunAtHourTextBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getRunTimePanel().add(getRunAtHourTextBox(), constraintsRunAtHourTextBox);

			java.awt.GridBagConstraints constraintsPurgeDataCheckBox = new java.awt.GridBagConstraints();
			constraintsPurgeDataCheckBox.gridx = 1; constraintsPurgeDataCheckBox.gridy = 2;
			constraintsPurgeDataCheckBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPurgeDataCheckBox.anchor = java.awt.GridBagConstraints.EAST;
			constraintsPurgeDataCheckBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getRunTimePanel().add(getPurgeDataCheckBox(), constraintsPurgeDataCheckBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRunTimePanel;
}
/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStartDateLabel() {
	if (ivjStartDateLabel == null) {
		try {
			ivjStartDateLabel = new javax.swing.JLabel();
			ivjStartDateLabel.setName("StartDateLabel");
			ivjStartDateLabel.setText("Start Date (mm/dd/yyyy):");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartDateLabel;
}
/**
 * Return the JCPopupField1 property value.
 * @return com.klg.jclass.field.JCPopupField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public com.klg.jclass.field.JCPopupField getStartDatePopupField() {
	if (ivjStartDatePopupField == null) {
		try {
			ivjStartDatePopupField = new com.klg.jclass.field.JCPopupField();
			ivjStartDatePopupField.setName("StartDatePopupField");
			// user code begin {1}
			com.klg.jclass.util.value.DateValueModel dateModel = 
						new com.klg.jclass.util.value.DateValueModel();
			dateModel.setValue( com.cannontech.util.ServletUtil.getYesterday());
			ivjStartDatePopupField.setValueModel(dateModel);
			ivjStartDatePopupField.setEnabled( true );
			ivjStartDatePopupField.setToolTipText("Select StartDate from the Calendar");
		// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartDatePopupField;
}

/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStopDateLabel() {
	if (ivjStopDateLabel == null) {
		try {
			ivjStopDateLabel = new javax.swing.JLabel();
			ivjStopDateLabel.setName("StopDateLabel");
			ivjStopDateLabel.setText("Stop Date (mm/dd/yyyy):");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStopDateLabel;
}
/**
 * Return the JCPopupField2 property value.
 * @return com.klg.jclass.field.JCPopupField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public com.klg.jclass.field.JCPopupField getStopDatePopupField() {
	if (ivjStopDatePopupField == null) {
		try {
			ivjStopDatePopupField = new com.klg.jclass.field.JCPopupField();
			ivjStopDatePopupField.setName("StopDatePopupField");
			// user code begin {1}
			com.klg.jclass.util.value.DateValueModel dateModel = 
						new com.klg.jclass.util.value.DateValueModel();
			dateModel.setValue( com.cannontech.util.ServletUtil.getToday());
			ivjStopDatePopupField.setValueModel(dateModel);
			ivjStopDatePopupField.setEnabled( true );
			ivjStopDatePopupField.setToolTipText("Select StopDate from the Calendar");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStopDatePopupField;
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 4:48:50 PM)
 * @return java.lang.Object
 */
public Object getValue(Object object) {
	return null;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("AdvancedOptionsFrame");
		setLayout(new java.awt.GridBagLayout());
		setSize(360, 309);

		java.awt.GridBagConstraints constraintsAdvancedPanel = new java.awt.GridBagConstraints();
		constraintsAdvancedPanel.gridx = 0; constraintsAdvancedPanel.gridy = 0;
		constraintsAdvancedPanel.fill = java.awt.GridBagConstraints.BOTH;
		add(getAdvancedPanel(), constraintsAdvancedPanel);

		java.awt.GridBagConstraints constraintsOkCancelButtonPanel = new java.awt.GridBagConstraints();
		constraintsOkCancelButtonPanel.gridx = 0; constraintsOkCancelButtonPanel.gridy = 1;
constraintsOkCancelButtonPanel.gridheight = 2;
		constraintsOkCancelButtonPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsOkCancelButtonPanel.weightx = 1.0;
		constraintsOkCancelButtonPanel.weighty = 1.0;
		add(getOkCancelButtonPanel(), constraintsOkCancelButtonPanel);
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
public static void main(java.lang.String[] args)
{
	try 
	{
		javax.swing.JFrame frame = new javax.swing.JFrame();
		AdvancedOptionsPanel optionsPanel;
		optionsPanel = new AdvancedOptionsPanel();

		frame.setContentPane(optionsPanel);
		frame.setSize(optionsPanel.getSize());
		
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});

		//set the app to start as close to the center as you can....
		//  only works with small gui interfaces.
		java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((int)(d.width * .3),(int)(d.height * .2));
		
		frame.show();
		
		java.awt.Insets insets = optionsPanel.getInsets();
		//optionsPanel.setSize(optionsPanel.getWidth() + insets.left + insets.right, optionsPanel.getHeight() + insets.top + insets.bottom);
		//optionsPanel.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JFrame");
		exception.printStackTrace(System.out);
	}
}
public void setPanelsEnabled(int format )
{
	boolean value = true;
	switch(format)
	{
		case ExportFormatTypes.DBPURGE_FORMAT:
		{
			getDaysToRetainLabel().setEnabled(value);
			getRunTimeHourLabel().setEnabled(value);
			getRunAtHourTextBox().setEnabled(value);
			getDaysToRetainTextBox().setEnabled(value);
			getPurgeDataCheckBox().setEnabled(value);

			getStartDateLabel().setEnabled(!value);
			getStartDatePopupField().setEnabled(!value);
			getStopDateLabel().setEnabled(!value);
			getStopDatePopupField().setEnabled(!value);
			getNotificationGroupComboBox().setEnabled(!value);
			getAutoEmailCheckBox().setEnabled(!value);

			getDaysToRetainTextBox().requestFocus();
			return;
		}
		case ExportFormatTypes.CSVBILLING_FORMAT:
		{
			getDaysToRetainLabel().setEnabled(!value);
			getRunTimeHourLabel().setEnabled(!value);
			getDaysToRetainTextBox().setEnabled(!value);
			getRunAtHourTextBox().setEnabled(!value);
			getPurgeDataCheckBox().setEnabled(!value);
			
			getStartDateLabel().setEnabled(value);
			getStartDatePopupField().setEnabled(value);
			getStopDateLabel().setEnabled(value);
			getStopDatePopupField().setEnabled(value);
			getNotificationGroupComboBox().setEnabled(value);
			getAutoEmailCheckBox().setEnabled(value);

			getStartDatePopupField().requestFocus();
			return;
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 4:47:40 PM)
 * @param object java.lang.Object
 */
public void setValue(Object object) {}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 4:52:55 PM)
 * @param parent javax.swing.JFrame
 */
public void showAdvancedOptions(javax.swing.JFrame parent)
{
	javax.swing.JDialog dialog = new javax.swing.JDialog(parent);
	dialog.setTitle("Advanced Export Options");
	
	class DialogButtonListener implements java.awt.event.ActionListener
	{
		javax.swing.JDialog dialog;

		public DialogButtonListener(javax.swing.JDialog d)
		{
			dialog = d;
		}
		
		public void actionPerformed(java.awt.event.ActionEvent event )
		{
			if( event.getSource() == getOkButton() )
			{
				com.cannontech.clientutils.CTILogger.info("OK button");
			}
			else if( event.getSource() == getCancelButton() )
			{
				com.cannontech.clientutils.CTILogger.info("Cancel Button");
			}

			dialog.setVisible(false);
			dialog.dispose();
		}
	}
		
	java.awt.event.ActionListener listener = new DialogButtonListener(dialog);
		
	getOkButton().addActionListener(listener);
	getCancelButton().addActionListener(listener);
	
	java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
	dialog.setLocation((int)(d.width * .3),(int)(d.height * .2));

	dialog.setModal(true);	
	dialog.getContentPane().add(this);
	dialog.setSize(360,320);
	dialog.show();

	getOkButton().removeActionListener(listener);
	getCancelButton().removeActionListener(listener);
		
	//if( getButtonPushed() == this.OK )
		//return (com.cannontech.data.graph.GraphDefinition) getValue(null);
	//else
		//return null;
}
}
