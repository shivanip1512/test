package com.cannontech.common.gui.util;

/**
 * This type was created in VisualAge.
 */
public class ColorChooserPanel extends javax.swing.JPanel implements java.awt.event.ActionListener {
	private java.awt.Color selectedColor = java.awt.Color.red;
	private ColorLabel ivjblueColorLabel = null;
	private javax.swing.JRadioButton ivjBlueRadioButton = null;
	private javax.swing.ButtonGroup ivjColorButtonGroup = null;
	private ColorLabel ivjgreenColorLabel = null;
	private javax.swing.JRadioButton ivjGreenRadioButton = null;
	private ColorLabel ivjredColorLabel = null;
	private javax.swing.JRadioButton ivjRedRadioButton = null;
	private ColorLabel ivjyellowColorLabel = null;
	private javax.swing.JRadioButton ivjYellowRadioButton = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public ColorChooserPanel() {
	super();
	initialize();
}
/**
 * ColorChooserPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ColorChooserPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * ColorChooserPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ColorChooserPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * ColorChooserPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ColorChooserPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getRedRadioButton()) 
		connEtoC1(e);
	if (e.getSource() == getYellowRadioButton()) 
		connEtoC2(e);
	if (e.getSource() == getGreenRadioButton()) 
		connEtoC3(e);
	if (e.getSource() == getBlueRadioButton()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (RedRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> ColorChooserPanel.RadioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.RadioButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (YellowRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> ColorChooserPanel.RadioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.RadioButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (GreenRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> ColorChooserPanel.RadioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.RadioButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (BlueRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> ColorChooserPanel.RadioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.RadioButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the blueColorLabel property value.
 * @return com.cannontech.common.gui.util.ColorLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ColorLabel getblueColorLabel() {
	if (ivjblueColorLabel == null) {
		try {
			ivjblueColorLabel = new com.cannontech.common.gui.util.ColorLabel();
			ivjblueColorLabel.setName("blueColorLabel");
			ivjblueColorLabel.setPreferredSize(new java.awt.Dimension(30, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjblueColorLabel;
}
/**
 * Return the JRadioButton4 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getBlueRadioButton() {
	if (ivjBlueRadioButton == null) {
		try {
			ivjBlueRadioButton = new javax.swing.JRadioButton();
			ivjBlueRadioButton.setName("BlueRadioButton");
			ivjBlueRadioButton.setPreferredSize(new java.awt.Dimension(23, 20));
			ivjBlueRadioButton.setText("");
			ivjBlueRadioButton.setMinimumSize(new java.awt.Dimension(23, 20));
			// user code begin {1}
			ivjBlueRadioButton.setActionCommand(java.awt.Color.blue.toString());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBlueRadioButton;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G9AEDFEA7GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8BF09447159CA1D724CEF5A7D3A44686BBDCC245A45679F09549F1C1F5079D65CCE2A5E79CA5A697B951594496976CA29512D0B96CB47AA1708ACBC8484288A104G4948C2C8FCD60B50F785088833A0810BA40CC63BA3E9E1F6E7191DC5DF703D6E1E6E996D4ECAB2C52A0E722B19FE7DFE7D3A5F6B77E6E51437F7E17A6614BC04B627A36D5F494D88C59DC7E851703A6FB3DCE6F6E28AB27C5B87
	10032E64264133G66E26D09A959E8FDEAA60CABE01C501C1872813C67225F27AE28061788FAD4180B7B59EFD6CE2E675113CC4F96B27E314F824F6DGD500434F71EC2C7F8DDF01066F54708CB603D4C78E263148059AEE85502C8630G3EB80752DB613915E83EF3512A5145E6FE99392B7D99D9D80EC1A603F99AEF2C7684CBB35CBB302E6332C56359GF39500430BCFC525E5E5702CF35C9E28F7495ECA176A299C88FAAA032AC73259DAE4C9D6DA86E5B9A0AA8E41A7CAC9C456BDB91370A2349846D2AE46
	A50FE2792570DE8750194B702F0EE17C92F8DF8910144B5674DAC31DE633B4788D3466487D0C77A32CE9E3AEDDCBF11E3E2625090D393B087FDF31E2BF77405C8C0039G4BGD683EC8458CD767D17A7BF02E7D31F5AA2FB3D322F496FEF165C4A8559ADCA70EE33410C063BAC3AE4450D905D5BD3F20509BD138165EB7B5CFC54A70BEB58BE4FCB190FB6FE571B314544A77E58110C6C1D5497E5BBF51FD05DB3A36ABE37F362B91C287B9AB26A9E5E7B250CF4935D767ECFB3FAB4B9FE036EC5BB71DE77103D0E
	06F74C0779A9FE0060138DF8FAA65FA6E7860FB741DCEFBEBB9BF34B18AD16B90F2085839ED3BFB8DE78E2C6DCBE35A1B1BF5416CB63D8F66687D467D6EE4B2784DFEA40D3DBDEA7B662F1A34CAD822067AB4906E3B433A0B12594208E2093C0850886D8D4404E58A16B11E91EB10720284DC10F649695C4F9132C79709407D5C5E87385D44167922F8A0AC7681344G52E856DA318F7AA6703DCD4E3E8BF41C95FD22A2289E598752852F280ACA00527C511A03E994114CF6FA3CA2A8707A91137BE7EBAE0EC1A1
	20DE713B85D554A9687C16DA6C133B42D5108882601D7A25208E7BAB8E561F88300E7BE1E6AD167797D101ECE133354B726D203F932F84556D42FEFE0C648E846F853B180F73CEE35C8C984F5B45647CEB6D8E039C3B47054D97149196E3FDE318A7866863F731F37336131D1B0D3F1E0FD6FE6716693941103CCBBBAF9F06460F1748453615FD48F27C06D127266B7344F4A4088F11C3F5E40DE19D298FC17EF6AD260CE36A671975F84F4E413A5CG33BEE43E5ED0DBCC4FEE33909059E93D2840C1935D3A6729
	1D574633B51F26EAFA3670B538495C82109B87308EA0EFB77361DA323ED03E0731D65D07A9634E9079DB9F228E6A2B7F3161FBCA3CAB7AD43B6897FDEEF889E8F33F35E1FDDA9C774B1C670F846F0738EF01C08BF07CBBB69C1B022AAA1E3E20AAF6082A6A718D68736927302EC0304F2F278C264BF28A2FD19E02386774B927F04C38E41FCFA41B46D93479A2923322A4FA416896B968D3A93E348E57B4374591ADE71455E5A09C73D290679FDAA7AC915E43EBD0BCDEB88AB80521285DB81E174885869F55818E
	57BB3D70D4F733389C5A4F6252FD75AB286ADE17695D921D73050C045DB4B612F76BF1C9ED5AF55A5C763DA4F7E878G35F54EB56C573062C5BB9B39689F5B3259FCF3D0D5E55FD9C58E7AE90CACEFA077C6E1E2CAA14031C296A373CF63F9160F3460G9192F5B54C8D97E2FFBCBB16E7D8D3FBA17507FEFEE657E35D8171CED004B40F9363DF385F7F21FEC7485CD5ED8F687C2375F88FA4C3CE1D31876B1CB74E6BC850379AE02326573A076A26F4BD7798DDBC0C576C09C417FD1F512595019F0AA25145F13A
	BA2069B421237B713BF2FCC6C80DF9CB38AB8CD78606601C5BEC678DB565077ECC0DDEADE2356331G5E0B98984781A49671BCFF01550E3DEFFD8D957FF2A4E2BD3C21085AD130D7BFCF8BFF4BEA4774A62C5389F303FB59BE6FE8B05EDFCD01006C72102BD35B03679A0C670D627E23E19BBC7BBDCAC0ED0304B04C76AB3E815B0B6FD68D2D79631D4ABD267EB09CC6446A32472B0DEB9D6F6949F8AF42DA4735B5AEB02CF5BD5449343EF9EE1C55DA09B01FD71C18C24FEB2EBA613C9653FBF9981EB4D64A6E61
	16C9A0073C12BE9C2252AFABDE51DD062D2F940654CA128FAB1B48F4ABFEAFE7EBAD365A60F9D9F0FBE43A3C1BCDA1C25AA3CBB9D5FE157981B99AB59BE36CCFA067C732E93F906F994C08AA266BDFDA2C7BA17659A773974DE952FC7145FB4C97E89F1C55FD4C97D73A4CFC6107F94AD7FB1F714D82DCAC67F30D1871ED60FCEFF13E4C1244144692467722293E6192F6E76E75075672D1A5FA6D3030A43C9E21B5EE9C091B25A534464D61781F92FC1281CF6D3391BC9EDB76C38D361F573E4DB8EECBCB41E6G
	A7009FA09AE0C9A90B174E469AB4C55D5B6603F254A7C89D93AEB2ADC636B561F386F70FB65E5E047DC46E9E446A16CF6C58CF7120B399G9DGBF81D88FB09B3E5CB4258D2D4326B6D0BF3FB09E6AE76701093ED58F1855ECBF8D63AB8A994F8B6123B1115FDCE49A7F678DE1827BC476663C1A296502A587583EACEA45F9B79946E987A1BF83D881FAG0681969CE4FED953F4FACA3F60B06B14DBGF78E6E1516505A012F756F6EBD180F9E8B638B75D16C4109FCF45DD1DDEC5D8987D9BCB45CFFB09BAE0471
	055AE0B9E4E64363A34C0652C34C065FBD208D1BBF1B4A069ED39BD23A198DAA4C539E6A8439ABE3E03C0263B6AB983786461B382DBF8C944333388CE8G1CGAAC0B4C08CC0BCC0F299237D85214535C3DAB9743AGF6GF7B92D938A357B1D52B686E60458BE95E0BD46F134E19C2359E646D7EC402B5ABAC81E67F63BBE37ADB9A163778C6FD9A8F2DFE26C4D70FE309E8B5FC3DA0F5E9C39B7D85FEC5E9B7C21593CB758541CC371A0437845004D2735CC5EBBD8DAA2778ED76D116D3CE1B73753E5B73753E3
	1F5C4EBB76C8F66EA1BE7DC17E7B04BF909083A4995985D550665ED4F7A213FE669341ABA2D64B3FBB3ED523E9D5651623BC5EAE2AAC5EE6156BE7E81E769E834F9565934F9C35771956483E79F62B39EF163762F39310030ACB8CDD7F732D85FCCE4F2EED2A68454B20B4AF92FED5D08644B07F3DCC7869DCA47EDF34C672B1755173770CBE2A66BEFA240B79A841600BE46DFD93BC33BF1A68A3AA6F6EFD233C9AAE6F2BA3CCDE29014F2A3D37433357D45EC71F99659D637236F4B3F92A01EF36763EG1ECBCC
	65DD70637ED592C664200AD055E11C9F5DFE56274681CFA2C09A608B8E9B7B14E707F0AE68D784AF382F43AF3A1025827396A80CF7986823GE2G6281569DE6F532F39B1E3FAC3AA75C61F46E56B53C3F34DF1415F6214F500BC63F077B4191D11264A1FD1A4E7DFE8FBE0B830AA87A8C1CF4EE38886B6B03B249A8144E4D5F0A4F43F5A2F302B95A7C53E5D867D9AC57B0CD6750E7D8E7B348B5F252FA355EE22CD7BBABE83DBA2B025533373DE67373787C77FCE673B1FC7EABAE337995956C0E6BA735D0820C
	57F35C7F6E4438340F2127791861FA7359DDD84CF17FA2E11C9F4633B9EEF19713370063E611BBFF890CD7F05C5A91A6AF1E631CDDCCDEE1A543FDE704492B6238F73A18BCBB47FD5A4D64B9B8EE4E98133710630A3A193C259C774DD1A6EFA547ED9BE37292B82E271B49ABBD427A10A8BF7B8D488AB87F91769B5051DEDCCF72FA9477E2B65BD94563EE96865AC99451DAA401549D18FF71915623275CA1BD850CB782E4D7319EFDB415752D1B4A3F0E240C1926BDFA61F2FFC6D915169F2A8CBDBA742DD4E73D
	42F44E067995D598A7A946F3024FED9088AA61C9671F1CB00FE361A2B24E3FB4E19E5B3BF782FFCA40B80FFDF4D66327EBBB5C4D5656F962A9D476057E085F9FB20F52B5D5E84FC55BD7252BB31E4E27FEDF5E3B8FCD7E5B9B4B955497C1CB26169B5DA00F623E66E55FCD96F05C1FFC8C1740F1BFF2B11C251A652CFBAA365B2F087D2282592943B30A6F5571EA920BA4566C706E6074BB7271F7C295AE4095557837516F37E3EFD8EBB07DD03EDDDBF3DACDF8AD3799695FFEB194G8D061D9A3E220676240CA6
	977DD84239989231479B74924B28DDEF10B81E8174D1G94370A4458B2984755B0DB670FCC3475098B3A2DE814593ABE022D2913581AF6CC37156A7F9B22BF9B70054718CD3FA239609C0CBB81288D41CFF49B025D4C0699474CEDC81F440639E1B6ACA7B9E0A160E3388DE7C98ED88D63B55C068B97F59B760DB19B320F1B5B30E5929BCA0F075A90CD74D5813E6EB82B4F6704FD43188AA85F4287647B796766235FC692C89E9BB42C0962DFA5F855002739B4738E4B25B3E0EE4DF1561BD7B9F02DF06E8464DC
	00F10039GCB81DEBA417A72AF1D29C2D374651D323F9D0AD049D0D2537535A88F765B7216CE1633667E739C6414EEC47C0E386684BD8BE9A769338A2DCA47CF3A3E6FDD426BBB86F38EG95A08AE0A1403213ECFD2B4ED45075353ABDEA132A8A2EC1723514AD6E2AC79C5251385022EB9B353035451F8CBF0FC9005BF0522C37FE4A4B78B22D61FC05A6B84A77E79F632BB22131C764FB5945781CA6B412890E46DED7875E891A1F566F447D3FCF9CAA661FBF6D9EF0877E11352C3A2622383A2A1C6D5DFFA363
	5FFF2058E8535FCFC8AE51F44F551E9BF3D725D399C72CC699CB775C48589CC1C63C463B5A2A5F47748ED91C4F6F105A44143DE49D60F3247F236B4C3E027839AB8C30F3376E6CE3487D6AF0467E88FA6D351A6D353ADE5DF7762B3A6F76D92663BB4AD7F8CD679B99198E9FDD6B51D1365678DA16BB37B975B53E6654651E1916DC4A775D6BBADF05F37A76FC231B5913E6E376BC51255B73EE37AE77192E695B73339E1D6F4951695BF3F90C59D3476DF96706EECF52273A5C9727A5174656FF5DC1BC4EDEB73C
	EF3F63B4B9B38F8B280EC3FFD59DB4B756BAA2FFDFE8F418FFDFB863B07F76526E187C9BD027A372B720EE077177E31A5B175C44F52F8A7B998D3094E0A5C0220D6575350E92C4723AC9CEEFF58F606F9B782FD7D47E7B572548EB7D4925297F66A571927EDD2DCF0696540B65B37CEB1770FE29321FA0A96E4DCB58C70AE7E0D025249A7E2DCB781CCAE2BFC1535E6891D2C7EF04F5651DE27DD22544FA28B20EBB1305F143B00E6638FF73B25EC59CB7B30B71AE6338FC1951E556B15C2F5298DDA1472DF5B1BA
	0963D670FEF3C69D2BABD677E25CCBB0DED74776632B9D0761F9D53825D590783A159532A300F4A832DFD454910AA53FA4EB7B7C9381F17F7C3A63C0A499302B67A1DEAF765D92DDEA27B04063641F720DB50865B4745927A77F5B982A775DBEDCEF9E83DA87008A90853090E099C0BCC0924086GCBBD742DGD600F600DEGBF40AC00F975EC7DD72E14CF32FE2D01A507D291BC78D8EA761F98B55A9F8372D654CFEEBF65FB3A5B4817GBC4953623BB6E6645B84BC598D13735175AD3B99B9FE7E7D66547133
	72A6792D9A4EFD6708DF7BD678231B785B185C5F9F90F91C3EFCB31B635A828EB960415F98E96EDEEC095CA3BEC762289456D285C0FB0AD6097594BDA6EB44B2A37501F86CEF886D83EFE4197701C192F363C0BF3B0175BBEF1258DC8263D8EE4FE3D94C1E648876CC566BEDEA886DF19AC8DCE7B6C26EE8E4BA7E1046F4B4B61A6B182C176BEC8C5531430DF57482DE62BADE99E1BA66C550B1D92F96E350C1777C55DE4C476F8E1A9E3439B53D789EG172B9EC1720C12BFFF68GC25FG4DCFF1248EDC86B271
	5FBCD25C8F886EA58D277DAD05AB72795D651A7A7C16384C73FF19ABBC7FFF6CB24F7FB5AEE37E27B82B8B6F47G6487A727BB4DF157B92E19484438EB9E37BA48716708DD98FFCE448A997E93E2579041B9B5DC0781F7DD43B9DDB886E149033242F8EF3870770AFE0FA45131C0E8BCBE88C96E0FFEE22347AF986C3E4DF15779DEDC988D5F1F4B23467D2138673B4369DE688E27730D055385460CF43466FB5C0DF85C6E17A62979EA2AA65412F7A5D6CB2EEBE435240237D013FB3EDF17FB51DB98D96E510A62
	23651427E4C067994A1A3EADBF4CE23614B6B1DB7EB4205B727220AEB7A1ED7AF28F25B339BDDC6E37BC3A5CE7EE69F23FBDD9BF91A2B71377A9F3395C3F3D0DF84E918EDA74D8A64718628B0733F4BCBB465AFDB5A6996F2BF8101B54B41DFB6EBF330CFC9B00A733F9BAFC9569C63EC260291A96DF5604FB5C8EBC4EA97834EF6D92BB4F299C7728444E330EFBA30B511D66385FE4B13AF20EEBC98F27BB179ECED7BA9ACED7BEEA24237F1F4A93B9091028E35EC26930A77C3F0002F6041F07263F7F79ABB1B7
	6DF9A9A8258DE58C120DECC369C3F82707711F1161D47E81703E4FF95BC8D2C93ADBE3CBFD7D6B29A9A88D98E912EDC3E9B8D3823FC26524C212A5D25801A27F82F750FD1446E5070FB27E1FF194F10EF483D7BA27A1D763B4B8504322215FC6E2DB0D5FE48A736EC47C2E03DA69394EEB553F0D501A7ADA9EBEBF8E40CB2D2CAEFEAA77E39439266F708A12248A526DA61F9B57774DA32A28770CBDF9D8FE0051E8135A5FG64E13BEF99F87E8FD0CB87886D699AD6FC92GG34B5GGD0CB818294G94G88G
	88G9AEDFEA76D699AD6FC92GG34B5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG3692GGGG
**end of data**/
}
/**
 * Return the ColorButtonGroup property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getColorButtonGroup() {
	if (ivjColorButtonGroup == null) {
		try {
			ivjColorButtonGroup = new javax.swing.ButtonGroup();
			// user code begin {1}
			ivjColorButtonGroup.add( getRedRadioButton() );
			ivjColorButtonGroup.add( getYellowRadioButton() );
			ivjColorButtonGroup.add( getGreenRadioButton() );
			ivjColorButtonGroup.add( getBlueRadioButton() );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjColorButtonGroup;
}
/**
 * Return the greenColorLabel property value.
 * @return com.cannontech.common.gui.util.ColorLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ColorLabel getgreenColorLabel() {
	if (ivjgreenColorLabel == null) {
		try {
			ivjgreenColorLabel = new com.cannontech.common.gui.util.ColorLabel();
			ivjgreenColorLabel.setName("greenColorLabel");
			ivjgreenColorLabel.setPreferredSize(new java.awt.Dimension(30, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjgreenColorLabel;
}
/**
 * Return the JRadioButton3 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getGreenRadioButton() {
	if (ivjGreenRadioButton == null) {
		try {
			ivjGreenRadioButton = new javax.swing.JRadioButton();
			ivjGreenRadioButton.setName("GreenRadioButton");
			ivjGreenRadioButton.setPreferredSize(new java.awt.Dimension(23, 20));
			ivjGreenRadioButton.setText("");
			ivjGreenRadioButton.setMinimumSize(new java.awt.Dimension(23, 20));
			// user code begin {1}
			ivjGreenRadioButton.setActionCommand(java.awt.Color.green.toString());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGreenRadioButton;
}
/**
 * Return the redColorLabel property value.
 * @return com.cannontech.common.gui.util.ColorLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ColorLabel getredColorLabel() {
	if (ivjredColorLabel == null) {
		try {
			ivjredColorLabel = new com.cannontech.common.gui.util.ColorLabel();
			ivjredColorLabel.setName("redColorLabel");
			ivjredColorLabel.setPreferredSize(new java.awt.Dimension(30, 20));
			ivjredColorLabel.setMinimumSize(new java.awt.Dimension(1, 1));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjredColorLabel;
}
/**
 * Return the RedRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getRedRadioButton() {
	if (ivjRedRadioButton == null) {
		try {
			ivjRedRadioButton = new javax.swing.JRadioButton();
			ivjRedRadioButton.setName("RedRadioButton");
			ivjRedRadioButton.setPreferredSize(new java.awt.Dimension(23, 20));
			ivjRedRadioButton.setText("");
			ivjRedRadioButton.setMinimumSize(new java.awt.Dimension(23, 20));
			// user code begin {1}
			ivjRedRadioButton.setActionCommand( java.awt.Color.red.toString());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRedRadioButton;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 */
public java.awt.Color getSelectedColor() {

	return selectedColor;
}
/**
 * Return the yellowColorLabel property value.
 * @return com.cannontech.common.gui.util.ColorLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ColorLabel getyellowColorLabel() {
	if (ivjyellowColorLabel == null) {
		try {
			ivjyellowColorLabel = new com.cannontech.common.gui.util.ColorLabel();
			ivjyellowColorLabel.setName("yellowColorLabel");
			ivjyellowColorLabel.setPreferredSize(new java.awt.Dimension(30, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjyellowColorLabel;
}
/**
 * Return the JRadioButton2 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getYellowRadioButton() {
	if (ivjYellowRadioButton == null) {
		try {
			ivjYellowRadioButton = new javax.swing.JRadioButton();
			ivjYellowRadioButton.setName("YellowRadioButton");
			ivjYellowRadioButton.setPreferredSize(new java.awt.Dimension(23, 20));
			ivjYellowRadioButton.setText("");
			ivjYellowRadioButton.setMinimumSize(new java.awt.Dimension(23, 20));
			// user code begin {1}
			ivjYellowRadioButton.setActionCommand( java.awt.Color.yellow.toString());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYellowRadioButton;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getRedRadioButton().addActionListener(this);
	getYellowRadioButton().addActionListener(this);
	getGreenRadioButton().addActionListener(this);
	getBlueRadioButton().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
	setBorder( new javax.swing.border.TitledBorder( new javax.swing.border.LineBorder( java.awt.Color.black ), "Line Color" ) );
		// user code end
		setName("ColorChooserPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(143, 186);

		java.awt.GridBagConstraints constraintsRedRadioButton = new java.awt.GridBagConstraints();
		constraintsRedRadioButton.gridx = 0; constraintsRedRadioButton.gridy = 0;
		constraintsRedRadioButton.insets = new java.awt.Insets(0, 5, 0, 0);
		add(getRedRadioButton(), constraintsRedRadioButton);

		java.awt.GridBagConstraints constraintsredColorLabel = new java.awt.GridBagConstraints();
		constraintsredColorLabel.gridx = 1; constraintsredColorLabel.gridy = 0;
		constraintsredColorLabel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsredColorLabel.insets = new java.awt.Insets(5, 0, 5, 5);
		add(getredColorLabel(), constraintsredColorLabel);

		java.awt.GridBagConstraints constraintsyellowColorLabel = new java.awt.GridBagConstraints();
		constraintsyellowColorLabel.gridx = 1; constraintsyellowColorLabel.gridy = 1;
		constraintsyellowColorLabel.insets = new java.awt.Insets(0, 0, 5, 5);
		add(getyellowColorLabel(), constraintsyellowColorLabel);

		java.awt.GridBagConstraints constraintsgreenColorLabel = new java.awt.GridBagConstraints();
		constraintsgreenColorLabel.gridx = 1; constraintsgreenColorLabel.gridy = 2;
		constraintsgreenColorLabel.insets = new java.awt.Insets(0, 0, 5, 5);
		add(getgreenColorLabel(), constraintsgreenColorLabel);

		java.awt.GridBagConstraints constraintsblueColorLabel = new java.awt.GridBagConstraints();
		constraintsblueColorLabel.gridx = 1; constraintsblueColorLabel.gridy = 3;
		constraintsblueColorLabel.insets = new java.awt.Insets(0, 0, 5, 5);
		add(getblueColorLabel(), constraintsblueColorLabel);

		java.awt.GridBagConstraints constraintsYellowRadioButton = new java.awt.GridBagConstraints();
		constraintsYellowRadioButton.gridx = 0; constraintsYellowRadioButton.gridy = 1;
		constraintsYellowRadioButton.insets = new java.awt.Insets(0, 5, 0, 0);
		add(getYellowRadioButton(), constraintsYellowRadioButton);

		java.awt.GridBagConstraints constraintsGreenRadioButton = new java.awt.GridBagConstraints();
		constraintsGreenRadioButton.gridx = 0; constraintsGreenRadioButton.gridy = 2;
		constraintsGreenRadioButton.insets = new java.awt.Insets(0, 5, 0, 0);
		add(getGreenRadioButton(), constraintsGreenRadioButton);

		java.awt.GridBagConstraints constraintsBlueRadioButton = new java.awt.GridBagConstraints();
		constraintsBlueRadioButton.gridx = 0; constraintsBlueRadioButton.gridy = 3;
		constraintsBlueRadioButton.insets = new java.awt.Insets(0, 5, 0, 0);
		add(getBlueRadioButton(), constraintsBlueRadioButton);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getredColorLabel().setBorder( new javax.swing.border.EtchedBorder() );
	getyellowColorLabel().setBorder( new javax.swing.border.EtchedBorder() );
	getgreenColorLabel().setBorder( new javax.swing.border.EtchedBorder() );
	getblueColorLabel().setBorder( new javax.swing.border.EtchedBorder() );

	getredColorLabel().setColor( java.awt.Color.red);
	getyellowColorLabel().setColor(java.awt.Color.yellow);
	getgreenColorLabel().setColor( java.awt.Color.green );
	getblueColorLabel().setColor( java.awt.Color.blue );
	
	getColorButtonGroup();
	getRedRadioButton().doClick();
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame;
		try {
			Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
			frame = (java.awt.Frame)aFrameClass.newInstance();
		} catch (java.lang.Throwable ivjExc) {
			frame = new java.awt.Frame();
		}
		ColorChooserPanel aColorChooserPanel;
		aColorChooserPanel = new ColorChooserPanel();
		frame.add("Center", aColorChooserPanel);
		frame.setSize(aColorChooserPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Comment
 */
public void RadioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

	if( actionEvent.getActionCommand().equals(java.awt.Color.red.toString() ) )
		selectedColor = java.awt.Color.red;
	else
	if( actionEvent.getActionCommand().equals( java.awt.Color.yellow.toString() ) )
		selectedColor = java.awt.Color.yellow;
	else
	if( actionEvent.getActionCommand().equals( java.awt.Color.green.toString() ) )
		selectedColor = java.awt.Color.green;
	else
	if( actionEvent.getActionCommand().equals( java.awt.Color.blue.toString() ) )
		selectedColor = java.awt.Color.blue;
	else
		return;
}
}
