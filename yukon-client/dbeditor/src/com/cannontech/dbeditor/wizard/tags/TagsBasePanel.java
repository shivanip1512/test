package com.cannontech.dbeditor.wizard.tags;

import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.DataInputPanelListener;
import com.cannontech.database.db.tags.Tag;

/**
 * Insert the type's description here.
 * Creation date: (1/15/2004 2:31:36 PM)
 * @author: 
 */
public class TagsBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements DataInputPanelListener{
	private javax.swing.JTextField ivjLevelTextField = null;
	private javax.swing.JLabel ivjTagLevelLabel = null;
	private javax.swing.JLabel ivjTagNameLabel = null;
	private javax.swing.JComboBox ivjColorComboBox = null;
	private javax.swing.JLabel ivjColorLabel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JButton ivjImageButton = null;
	private javax.swing.JLabel ivjImageLabel = null;
	private javax.swing.JCheckBox ivjInhibitCheckBox = null;
	private javax.swing.JTextField ivjNameTexField = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == TagsBasePanel.this.getImageButton()) 
				connEtoC4(e);
			if (e.getSource() == TagsBasePanel.this.getInhibitCheckBox()) 
				connEtoC5(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == TagsBasePanel.this.getNameTextField()) 
				connEtoC1(e);
			if (e.getSource() == TagsBasePanel.this.getLevelTextField()) 
				connEtoC2(e);
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == TagsBasePanel.this.getColorComboBox()) 
				connEtoC3(e);
		};
	};
/**
 * TagsBasePanel constructor comment.
 */
public TagsBasePanel() {
	super();
	initialize();
}
/**
 * Comment
 */
public void colorComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}
/**
 * connEtoC1:  (NameTexField.caret.caretUpdate(javax.swing.event.CaretEvent) --> TagsBasePanel.fireInputUpdate()V)
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
 * connEtoC2:  (LevelTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> TagsBasePanel.fireInputUpdate()V)
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
 * connEtoC3:  (ColorComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> TagsBasePanel.colorComboBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.colorComboBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (ColorComboBox.item.itemStateChanged(java.awt.event.ItemEvent) --> TagsBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ItemEvent arg1) {
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
 * connEtoC4:  (ImageButton.action.actionPerformed(java.awt.event.ActionEvent) --> TagsBasePanel.imageButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.imageButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (InhibitCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> TagsBasePanel.fireInputUpdate()V)
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GA60AAFB0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8DD8D4571516352DED120D5918AE6D5A2DB5B61F09EE16DA5A300D5D355DF457A45AB52D36EE63E6C96AB66E2EA6EEEB3E726D3A49E5G419FFE7C0B9AFF98E0069F05817C09BA2002A0B082BA0191513251877398A68EB31319078C4654BD677E3CF9B33C81314977056FBB735EBD675E73F74F3D6F5C739EE4C77E0C0CD4438EC2D2B3887FBB1ECA0869ACA1DB3EE27F2540E59963B209662FGE0
	AB595D108E575DC0F3C8F119F964231BD950AE0776122BF1193B60FEBB69290C3742CD8CB9FE207DD7623C7431656CB18AB919347D1F873640B587208AE5834CB0227C347C5D9C9F02F68E3FC7C8B59062A840BE730AF7EB70ABF93FD22055A294E21F83797BB8FDBA609281D2G3694323EA638EE245AFD5EFA02776BF9E8B6D9775CDEC3B64A527097B0190F1D4D2FC672A8F5C49AE94EC59E35769B2E53FD3EC1CB1F64757A3C0A5CB7E0F1764ACE37628BD8065D3724005322C82E20556A10DC17E95BAEF9E5
	4F6EA254F7F9F19665C5C8B2701FC5F1769274D18F5CABG7797891DDF482E65BACD3BB11BA4BF1A93D367D5C5CC57AD26304E35C395E973CD483FB9FF8B9F57866D49G4C1EE7BEBCBED1FB02E72520CC4DA14C1E877305BD44AC6CF90C4E7989E85B8186D47CA055E392346381924C424ED3A5424E6E0F1EA2A93D278D39B16CDCE8E6762DB5076D1C7165EB06BDB4563ED50071D2DE8CF2811CGD3GE683AC8458CBE37C3A71A0DC6BFB15865F60204FDB6F771F75B8836DBE276C01FB2B95A89C57A1777982
	CEC2D89C7FCBF1390EBE82120BA3E3980159F61908381B5B71A4D11423068C9D5B92176DB72C63BC32CA4236F517205FD65378A8057BD4EA837809625F2478699ABC1B6FAE0A4776A2208D148ABFF76788DD5A52E7123C6ABAC3260EAE71BFBAE6C8AFE3BA6CAB8B6B42FCFC2788FD5C82F8A740ED00E9G09GCB4B040F1FB09E994F47B6083300D772F48E757A2500F2F6486DF14A816E6B767C31FC2D87EC5C6FA7BC0E79E576916D9C2D784E99DD3F2C18EDB22C657E48BA98768B13BDAD266C520323E3A3C7
	1D43F7681A28BD4866B0CD451BA83ECD03E7F3BB172EADEC77GED81G6B7FB26DEFBA447AEFD6793CCE710D9ABC639324F8ECBB013690004D6FFEB36E276565F119ADG5781C2G71G09G8B4B459C3FE12C3C4BF5E4574CAF9B7BE4415BF015C3CAC032F9030A646D133B2400DB6A7548C1427BBCD500FE6D0D98B71F2E6FBE1051ACFB65002438FDDE60AE8D4A90CCC156674785DB31CFC026D407FBD0868103FEA278AEAE40F56111024AA53FD3D264F08FC6DFD600B1F1D36A820E40G6EF7D7203F5651FD
	FB95581FDEA1FCF51E62FA202DD4883F588B3741359502DB76D82D0D6EBED4C38A0C08987DCD09892ED3207FFAG3F85F8D746E54E85B0D641DA82282A067D32DA2C59573AC5ECB6E67F95A9DFF6DAB7B669DABD4C626C68616858CCAFC519BD003F8DB09DE0BE408A0075G7B0E403394206708F8E67714CC956B21D2BFF685208D629E75274F796AB0EEDA55586391E6E97A23DF509F68977DB57EA72AEE1B47611B21394F66572D9ADC6EB863F3885BC345BD2E390DDC0F68FEAC9E12683EFB9B7C988F10F4C4
	4444FD05C636D67061A9D64745G84366C8CC7886BBB25887512EF4ADE25D1764BDEA75C84F9ECBED834B13CEE7AFDEACCBFC271380FB6C0E087D57CDF94EDE5F86021DD05027EA825CB0A92F0778EA9F2272CA8EE2FABBCFE8E55A59831C587392D5FAFC5DB17000D2BGD2D4DB0F1A36EB623F4D673BB164F7604A666BE88E4D97369C0D4BB49D9571E64C471C31C5ED9F246DB0FD5E016E3B182B09824A8B4755731F0A0C340858FDB59F12EAA27673CC6E670DAA7D8DCAF702DE7783A4822CGC8BFA67CB012
	5F2D99AF78EE3C8BBD765EC59FA6634F288C641F21410F3756F2A62047F6D2E20E9D639633FE0C9FB56B473855BCF68C1FB56B45B023594CE823EF582BD993974C38FF77793CDE19AECCF588E1B973EB8E34436DF5AB175DCE172CF43AEF49F0DEB906313D39041E29603E6E18583F070B447EED3A75B8D9F0ED0FEE7E977F2D930678E3AC9E679E8B6F5FCC270B453856E40FBC880EEA708DF995063FC41FBF9C9FCC2338E566FE9CDBFA811E0A8EB9546416BD4E72BE5882948B7DE5CF3D528F241BD248929C86
	DFD968C4D89A10FA8EEF4B0C9595270D11071772FEF7C036F97DC302C1D9D7D9950B136D64573B86284CB6606481214A27A9351A49DCDFBA0D4AEC70F9FC8148C1FAFDE7FD21F2094E1905DD6CF2205F9798141DD42EC59AD63850FACA0EAD35CF4B73DAFD943336585CEAC03F43CC3F97CFCE257A5986A517FCF6C8D1FC5ECFC8BBF71863C4F5BB42F45B37CD7E44E2A4FCA00C91A2BCC6922D2ECFB0C64616DA4DE5FE497460A7B8879A19FF4C94F05DD66439F462572D4803967C69D440160681496B9A2D1C8D
	6893F2071079C56F8D668F6F003B572DB48C48FDB77E38D5B3366023BCD7D8D3067B57A258571281D20E09DC6105E2240BDC01A7C460908F5BFA015673AE6E2B27CAF7E8764A3CF7D99DA9C7253B4721BF22CB1F85D7C6FF3BCC0F1E24527FDC179E667F776350DF9F073E776058746618F466473FAD4943E71C7C61108CC7A9CC36DA6591F19EAA5996C56B12BCC361F3566292FC36FBB4F99A43AFA855FF667CC3A966E6B7D47E4F15EEA0F8DEE2BC59B3B2C1C21E7EGAC134088A61E3C1E7723FCEDBDEF9158
	01353FAD47616CF3DC54248A5FC5DA641687FD0F637803C6D16BBB81EDBB007FB863B177B873876B771DA251EF7789B8CF1D08554FE5927D6C50E7GE04A8956EF6189ED3FC3E651AF6FE4DCE6A9C04BC9BDFE2F297DA683FDBA407C28FE6BCFE23F4292D1B32BD4F1A7A96EC5C8363E14AE6A926DF95A1A25B3D82D1BBBA054D999FF07B53ADED3F88879AA001FEC956B2E31CC1B235787033EBEB7BDAE73397FED19B62FE0381432CDF005C59ED4EC103005C47C3CD9063A62F91A23F98C4CB9F40A0C2E8F1C23
	D5C5F184EA98263B094357GC77A96884186B56D6AF24B43C4786163A6ED9DF4FEEE31E1F38CBF44F37B13B4FEF0FE791A0155AA97158A3F27G1D615A4B842E2AC64460D72DD83F522B0B76D1E3AC2EA137E5C8F16381DFD2A43A6BE9EB226546AC6E07019AD17BDCDF086BE9863413G16G30BACC27F18B6F3B8AF046DA3167FCDC23DD735D356C3CB4892EECAF1FD40431B34147A71F1BE74A3631390C5A30053FE7EE2F93EB7449192476CE26EEBDF6CA6ED64334DA66677935E1FFE73D51EE5887406CDBD1
	A46C4BBA85F971A9E15FABFF5033AFF4CA78E52AC90C3B9FF04F2863EEE5EB472DBF454619CE8B3F7C4EF4982E93FF220938F53661AB10FBFF400B7A1A43AC74BF813A79CF8B7DB759357A4FBE4D74DF226A7F349967F502F9G17392BD848DC897C761D91B2CBA52DCC4799A6737699A1738F45776AB3DE63BE2C5D2796A41BE36ED3D34F30B819F5A6FA1F5AD001EBE68160171E917B5448412D1AFD2A83F686700387F3D7FE0EBC047A433308378F9DC23974D9C4C4CD264102362F821E69F560930023G0EBA
	A1E35E21F1EB5567C2E3542ADFA81DC84DD92993E7790E32090C1B220E2BB6C60F1BDE972E15A5540D2E1B31BAF0BCDD074F54C53FC3F802625755C53FC3B09409F788267A384C8575C2FEFED1347C1E3311B2C3D1EDB62EE254B8CB349DD16358FAB41762B3156E451A2D4DEABD2F1EA1581E1BD8A37671443322BE7B55CA9C3B9A5AD98D3037825482DC87388DB023C144C0EA395E33AAB286E2BD2842B5B5E667AB77681F35637AE7C1C364B8E6771EBB426E658DE20E3EE23AB79DE60D9AF7A02A1D5728B7C7
	391DFB7467086D202A2E5F4E964F4552C6216BD5733D69BAB0EADC343F2E6B6A6A726C5557D51C41382E75F66157102A6B7ED16FG6FCE57C32346C56B1A202B6B692ED83A72139B57751A043AE2CE3430D1447EBFD7A10E1C837AB991676F17E39DDC6C27F61F5B2B903117EE2E40BC5E97F03B5CDE014B2338BEDF009648982E55923BF657E1512F5DF5DB74EBF76FDB462ECF77DBE25576D8ADF9954D670CE0DFADG43394B844E2E62DAE8BE6604F6C8455DA8C05CF4E8A71E93737B17C5781EE4B9345782E4
	B541F392208E2087C081188AB03BC974CF207D97B43133C8F293BBEBE05ECF1A59B941584C4E952CFFD611F8F7B1964C5648C25908C3FDE2754F627A6951166B0CB3B56945E865DD69064F03DC425E23081AF046987D11F7F68CDA1A8EAE3A063C1F76FB766DDC9AA741209C249BB1E65D1C762A32136854EB3134C5C46D290B3E9F41BE6794DFC335EA735F540C758C2EEB9E6DBB7E5E24B2769AF822D2FF8DA4D4229F023E21C01F9C9D67CFD56ED6E961A78B26F7E886BF0F53710A94F049235649BF52710C96
	EB7C124AD8EB0979E80FC96B23432A0F8E5C994BC78E9D9FB1FE0BA3789DD579FDAFDB0F5FG67F3BBA63F074CDAFEC7D4FECD7631741B9A131F23D84B6F184A4FA9E9794D62631378F5913FA643F5DD33368673D29566CFA1357D8AED0F9031174B75B96A9C1D73BA79664B4E7EEF51FD6C98F47CA19138C9F49FFB8FF077258A1D2F99F1BF76C8A33EA1985B53027DEE9A35B515AC40D581B436B05D186CEF8F63DA6D8FC803B0651DFE390F1C3061583882B1F69674CF82D88E1082305BA64E86EF525A1DC3F2
	E1A835C93D3207615FB90FF8ED851961FF15154D7A53C2AF9D207ABC2D89E5C5D60085ED1B8957D4D4E512531CFDD8FB2015D24E1061FF521CAD706A53DB507EA39747504AEF441815991B851EE7A76C0C746FC55AB352A49BBBA34D3209B3544F9CFA74A4151E512BC7DF2452C7F46949B671BCDAD2A31EC76BD45C31BB8257F8DE6096539CEA9A34E729B8CB0E681720627E4FAEF024D560BE22F9029D5A57D55C7B55021F22627E2EDA0C1D24625A6858A550DE2B623EC1F169ED702CEB9335136A6B9179BA1E
	9C2D5666005BF996C202C6AE4BD92653E7B22E115AB6D1A3F90E56C294E85F0F38B6718EADD4AC6ABFF39F1BC95AA61D517D8EAE59D1EC185F4657E7DB78FC192856FFB6948A197B5A618C580E3823055AF94198F95E4072B3C66F0F20639E8A47DE8DBDBE020E7A7EA0E27CF391748C4DF8E65B73E6E15B7A679308673B95B13F711B5E4EECDA402FF5AF3FC37CF3AE98185FFF5B3F1B68660AB47A197DC6D3965F67D25AC57DEB53A5012B3AA0F0353D82B7206292D45C8C40ED390838D7EB9AF574545AAA5EF3
	5373890CDB8D3013538870F8A015790171EB3BB39E3F8C8DDF4745C8FE83507E087B152D75E7BB30D62D40D623DC44EFD50FE119024BBA17B3162CCC8D9F04559A5DF1EE1200C7813FE77C947BD87C049F04AF1E0952FD79C5966B625D7EF652E9C25D7BFDDE45E8C77E3368DADB897D5682B0FB3ACF066D79F975D872B7EA7C26E70F93E4E05D69F7EF3D7596135FA7E12EB7G8702DB200364297738DDDE2C8793264F4712B84B796D62D971BD696D08B138495BFDC1B7FB1943463D200E1BEE97F971B7C755B4
	4683D63BB8DD007B45C2BB2BDD242A78E60ADF2A41331A469223788EB699E80D6F0933D20D95574EECE8BF833082E09D400E8EF816F60833132B9A5FE50ED9A3F0787C745926B94AB07BB6975E5BF709BF4C94FBC362771FA0C9DFD8139A2B6E5D58417684A73FAE7F57F7A81E5977139A34EF9A5092G16822C86A01D3036BB05FDFFDDDD4E6C4392FD3D22C8FD83347AAF0C4302FC980DC984334D9CD1B3A86F64FB933FBABAE3FD4715FBC9BBEE00773B4D2FD3E30E2B6F0D484FF83FA4FEDD94F35CF7A346A5
	73FE6B78B54BB1FADCBEE1679DFA0E26FFEB4F0C46417EC257624F300444676D65ADE25E764DF914B47EE2CDEAD10CF95B4765FA9C1A7795210AB4BD59CC4E79B3E1B97F74FBED5DB56FEBBB8D29B1644C627C93B4F2983FF9F5A4062D0C3E2CBEEC631CE63CE7EB3D233A902EDEF9582896081BE0511796BA5D124767AAACB7D6159B2B0F2AB5629943DADDCFECBA9433C63C126B18422FF3376D6572F75A427227C9283376EDC7A77E427187BFA8D5021636F3CD8E5EF755DD75E5F152085B3A788ED9F37FF363
	2771FDD8E40C3E7E294A606F7E2C314F2E2DD67D336BFBD67D7A4DE56B585FDEDD33467E762A5F2A7D760A6DD37FE643DC296AD2DCE69B009FE0B2C07CA531C72D33E24C611745237727F3CE17ACA6D4487FC5CDECDBDF2A997F9B64D7EB708C516B03E342A0729778B5B5B8DF0A4FCF118C771B9A74D1406D9AD0D8D70ECF21F93DC76E27E816B7571AC49E1EF8C9645273BAC49E3ED0456D6A94F978D295D7CC73B5D3D7DC6651AE013B6DC0DC8B343BD55C4B8EB15629623AFBD539AA6EEA3BAAD745BD5D2E4A
	D5F10B5431264B82D7DEA9461ED0F18FD42AE7164B62795A500A3949B4E8A7GAC85D88DC03AE18CC0B9C09D00834083B099A09EE0AE408200A5GAB81D23AC59C6CBE0DB9F417744168374A0064078D9A8C810955B8199E0F3791F5DE7E740CB9968F08267330D7DC6C7DC06ED360A42B0EFF187AE54B95B86FDC913A2CEDBEB406AE76004FAF8714910625DFDE089832450E4DF53671E373FFED749DBD4414E3444FEBBC067334065B5F9F1455B510F9BECF455902A29BE339181B46E18B586238A2F24A5C0B68
	A316D3DABA474BE935901DD3CE861E59A41C73EEA727AEC566341DB41627C23F69D7C48E7930A3B2E79F360775C9F10C274FD8B96EA21031156B4364FF0E4674F2402FD46D7F5AFBE1F9BFED3F1B1CBE163C9DD7D97CE3CE4D643DC1659981DFFED5487BC1C7D85E2C4A7164E97378E8F9BD40F3BB27717D39B5F60C3D5FBAFE0C39DA75773F4156517BDF20D5FF7F8B35EA77BF067B3895EDF041BA6DD67B452938ABAA6E0B14A762AE3B1D4A000AFF006A05789699858A7C43D42FE10A6B6638F88D6E8A47FD3D
	955789188CC795B1F6E6AB0DCD3747435A0F51BEEEAFAC9B559FF3280EEE3F2451BBC14505750EEF4779D21F0BEC59F35AB75AF790767DB064C96EDB74331BCE6868F531F838F209286B65310E3B4D3158585BDDB8165DAF6F8E7371BBA64667FF2E047924DF8B733950BEB1BEA55743FCB635047998ABA746270266FD6CFFD4CF568D43444CDDC3B29412C14BF88A59444B06C623B198A039AAD39B1D3D641BC72CE93FFAB44DC052EDE4830BE69BC9471034118C9A31B61286519C427F925A40D793D618F3029F
	12BF90835CED6258E2DC73AAF6B1E1FFC849D03198C7380E69B850A7BB42489FFF73582F973D5495CCA39BECE48F6E051DF20006026C44579A7C7F0E1A48C6BF16087173F2D0B6C8AF99C1E544A337119D7D9D78BDDFD0111D75A248028A59C93FCC29448F13AADB246000020B39260D24FB71BF2448B6C676C8DED7A50B389AEAF1EABD1DA63A479050F7BB559739D8D4AE5B1F4EB5971CDF1914C68CCDE41347A7B91B24BEF8225948C6E5A0A08787FC9EE748FF260EB24A9A962FA353005DDE55FCB45602B581
	D5699B212F5F1F3DAA3B89DC958A10CA5A9337FE0B5A4359B0A085C0149C30A9789FDF3E00C925FD6E00D23C38F9D70F0124F2A3F18AB6067C8A5950AF8D3ABDA3F053C8C30ACECE36825A86A4EF903FEC814D0CD11A8912AA40727033D50FBC7E5FD7290070D00564B3D74250CA176C9B2C2C6F6FF7FBE515817895586FD759E39FCBD41FED2107F61EDF76D6AF44C1A3C99B2C2A223F557477B07ED6D3CCB545D49F86672EA5E37EC553C342E69ABC6A3A406937B4418CAA3FDC736C4C97EB572E866B1AC8867D
	EA86A6A1AF5C6B4250E02F9C78425CC776EF1E16FC9F4E119FC3A504FC370654F897B5957A0E8796A3E4CCC6FCAFE0B5AAE4B3B6E8D184DB1934D40FF7B9F4659ABBE57C5457EA243CB2C745A2E2B70F8EAAC4672AD30B2DED346B8E7A3BEB420E42D1FB68583D2AFD6630FDA8B70F5B1873595334BCF2A60426191FBDCD0F04B55D4CB5FD87357E6CE9EA8DEB7AD32EA97EE570DAC6599DEDADA36573DBE256B262A5DE4310223FF74BED247FCF8A7874DEF1163856D0C1E21FC9BA87A50FC711BCB76A3DCEBC1F
	1C9DD16470D9FBC7A3728F0ABE1C48E8FB9BD16F8FB4E37E9FD0CB878824B63E0A1796GGCCC2GGD0CB818294G94G88G88GA60AAFB024B63E0A1796GGCCC2GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG5196GGGG
**end of data**/
}
/**
 * Return the ColorComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getColorComboBox() {
	if (ivjColorComboBox == null) {
		try {
			ivjColorComboBox = new javax.swing.JComboBox();
			ivjColorComboBox.setName("ColorComboBox");
			// user code begin {1}
			ivjColorComboBox.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjColorComboBox;
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
			ivjColorLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjColorLabel.setText("Color: ");
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
 * Return the ImageButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getImageButton() {
	if (ivjImageButton == null) {
		try {
			ivjImageButton = new javax.swing.JButton();
			ivjImageButton.setName("ImageButton");
			ivjImageButton.setText("Image");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjImageButton;
}
/**
 * Return the ImageLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getImageLabel() {
	if (ivjImageLabel == null) {
		try {
			ivjImageLabel = new javax.swing.JLabel();
			ivjImageLabel.setName("ImageLabel");
			ivjImageLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjImageLabel.setText("Image Select: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjImageLabel;
}
/**
 * Return the InhibitCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getInhibitCheckBox() {
	if (ivjInhibitCheckBox == null) {
		try {
			ivjInhibitCheckBox = new javax.swing.JCheckBox();
			ivjInhibitCheckBox.setName("InhibitCheckBox");
			ivjInhibitCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjInhibitCheckBox.setText("Inhibit   ");
			ivjInhibitCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			ivjInhibitCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjInhibitCheckBox;
}
/**
 * Return the LevelTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getLevelTextField() {
	if (ivjLevelTextField == null) {
		try {
			ivjLevelTextField = new javax.swing.JTextField();
			ivjLevelTextField.setName("LevelTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLevelTextField;
}
/**
 * Return the NameJTexField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getNameTextField() {
	if (ivjNameTexField == null) {
		try {
			ivjNameTexField = new javax.swing.JTextField();
			ivjNameTexField.setName("NameTexField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameTexField;
}
/**
 * Return the TagLevelLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTagLevelLabel() {
	if (ivjTagLevelLabel == null) {
		try {
			ivjTagLevelLabel = new javax.swing.JLabel();
			ivjTagLevelLabel.setName("TagLevelLabel");
			ivjTagLevelLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjTagLevelLabel.setText("Tag Level: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTagLevelLabel;
}
/**
 * Return the TagNameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTagNameLabel() {
	if (ivjTagNameLabel == null) {
		try {
			ivjTagNameLabel = new javax.swing.JLabel();
			ivjTagNameLabel.setName("TagNameLabel");
			ivjTagNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjTagNameLabel.setText("Tag Name: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTagNameLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) 
{
	
	com.cannontech.database.db.tags.Tag aTag = (com.cannontech.database.db.tags.Tag)o;
	
	if(o == null)
		aTag = new Tag();
	
	aTag.setTagName(getNameTextField().getText());
	aTag.setTagLevel(new Integer(getLevelTextField().getText()));
	if(getInhibitCheckBox().isSelected())
		aTag.setInhibit(new Character('Y'));
	else
		aTag.setInhibit(new Character('N'));
		
	aTag.setColorID(new Integer(getColorComboBox().getSelectedIndex()));	

   	Integer yukImgId = 
				(getImageButton().getClientProperty("LiteYukonImage") == null)
				? new Integer(com.cannontech.database.db.state.YukonImage.NONE_IMAGE_ID)
				: new Integer( ((LiteYukonImage)getImageButton().getClientProperty("LiteYukonImage")).getImageID() );
				
	aTag.setImageID(yukImgId);
      
   	return aTag;
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
 * Comment
 */
public void imageButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	javax.swing.JButton button = (javax.swing.JButton)actionEvent.getSource();
	
	java.awt.Image[] images = null;  
   	LiteYukonImage[] yukonImages = null;
   	com.cannontech.database.cache.DefaultDatabaseCache cache = 
	com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
    
   	synchronized( cache )
   	{
		java.util.List imgList = cache.getAllYukonImages();
	  	yukonImages = new  LiteYukonImage[ imgList.size() ];

	  	for( int i = 0; i < imgList.size(); i++ )
			if( ((LiteYukonImage)imgList.get(i)).getImageValue() != null )
				yukonImages[i] = (LiteYukonImage)imgList.get(i);
   	}

   	final javax.swing.JDialog d = new javax.swing.JDialog();

   	com.cannontech.dbeditor.wizard.state.YukonImagePanel yPanel =
		new com.cannontech.dbeditor.wizard.state.YukonImagePanel( yukonImages )
   	{
	  	public void disposePanel()
	  	{
			d.setVisible(false);
	  	}
   	};
  
  	yPanel.addDataInputPanelListener( this );
	
	//get our selected image id with the JButton
   	LiteYukonImage liteImg = (LiteYukonImage)button.getClientProperty("LiteYukonImage");
   	if( liteImg != null )
		yPanel.setSelectedLiteYukonImage( liteImg );
         
   	d.setModal( true );      
   	d.setTitle("Image Selection");
   	d.getContentPane().add( yPanel );
   	d.setSize(800, 600);

   	//set the location of the dialog to the center of the screen
   	d.setLocation( (getToolkit().getScreenSize().width - d.getSize().width) / 2,
				  (getToolkit().getScreenSize().height - d.getSize().height) / 2);
   	d.show();   

   	if( yPanel.getReturnResult() == yPanel.OK_OPTION )
   	{
		setImageButton( button, yPanel.getSelectedImageIcon(), yPanel.getSelectedLiteImage() );
   	}

	fireInputUpdate();
	return;
}

private void setImageButton(javax.swing.JButton button, javax.swing.ImageIcon img, LiteYukonImage liteYuk )
{
   if( img == null || liteYuk == null )
   {
	  button.setText("Image...");
	  button.setIcon( null );
      
	  liteYuk = LiteYukonImage.NONE_IMAGE;
   }
	else
   {
	  //strange, this will preserve the size of the button
	  int width = (int)button.getPreferredSize().getWidth() - 12;
      
	  //strange, this will preserve the size of the button
	  int height = (int)button.getPreferredSize().getHeight() - 9;
   
      
	  //javax.swing.ImageIcon ico = new javax.swing.ImageIcon(
	  img.setImage(
		 img.getImage().getScaledInstance( 
			   width,
			   height,
			   java.awt.Image.SCALE_AREA_AVERAGING ) );
   
	  button.setText(null);
	  button.setIcon( img );
   }

   //store our selected image id with the JButton
   button.putClientProperty(
		 "LiteYukonImage",
		 (LiteYukonImage)liteYuk );

}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getNameTextField().addCaretListener(ivjEventHandler);
	getLevelTextField().addCaretListener(ivjEventHandler);
	getImageButton().addActionListener(ivjEventHandler);
	getColorComboBox().addItemListener(ivjEventHandler);
	getInhibitCheckBox().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		getColorComboBox().addItem(com.cannontech.common.gui.util.Colors.GREEN_STR_ID);
		getColorComboBox().addItem(com.cannontech.common.gui.util.Colors.RED_STR_ID);
		getColorComboBox().addItem(com.cannontech.common.gui.util.Colors.WHITE_STR_ID);
		getColorComboBox().addItem(com.cannontech.common.gui.util.Colors.YELLOW_STR_ID);
		getColorComboBox().addItem(com.cannontech.common.gui.util.Colors.BLUE_STR_ID);
		getColorComboBox().addItem(com.cannontech.common.gui.util.Colors.CYAN_STR_ID);
		getColorComboBox().addItem(com.cannontech.common.gui.util.Colors.BLACK_STR_ID);
		getColorComboBox().addItem(com.cannontech.common.gui.util.Colors.ORANGE_STR_ID);
		getColorComboBox().addItem(com.cannontech.common.gui.util.Colors.MAGENTA_STR_ID);
		getColorComboBox().addItem(com.cannontech.common.gui.util.Colors.GRAY_STR_ID);
		getColorComboBox().addItem(com.cannontech.common.gui.util.Colors.PINK_STR_ID);
		// user code end
		setName("TagWizardPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 357);

		java.awt.GridBagConstraints constraintsTagNameLabel = new java.awt.GridBagConstraints();
		constraintsTagNameLabel.gridx = 1; constraintsTagNameLabel.gridy = 1;
		constraintsTagNameLabel.ipadx = 6;
		constraintsTagNameLabel.ipady = 3;
		constraintsTagNameLabel.insets = new java.awt.Insets(14, 16, 8, 1);
		add(getTagNameLabel(), constraintsTagNameLabel);

		java.awt.GridBagConstraints constraintsNameTexField = new java.awt.GridBagConstraints();
		constraintsNameTexField.gridx = 2; constraintsNameTexField.gridy = 1;
		constraintsNameTexField.gridwidth = 3;
		constraintsNameTexField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsNameTexField.weightx = 1.0;
		constraintsNameTexField.ipadx = 198;
		constraintsNameTexField.ipady = 4;
		constraintsNameTexField.insets = new java.awt.Insets(14, 1, 6, 51);
		add(getNameTextField(), constraintsNameTexField);

		java.awt.GridBagConstraints constraintsTagLevelLabel = new java.awt.GridBagConstraints();
		constraintsTagLevelLabel.gridx = 1; constraintsTagLevelLabel.gridy = 2;
		constraintsTagLevelLabel.ipadx = 8;
		constraintsTagLevelLabel.ipady = 3;
		constraintsTagLevelLabel.insets = new java.awt.Insets(7, 16, 12, 1);
		add(getTagLevelLabel(), constraintsTagLevelLabel);

		java.awt.GridBagConstraints constraintsLevelTextField = new java.awt.GridBagConstraints();
		constraintsLevelTextField.gridx = 2; constraintsLevelTextField.gridy = 2;
		constraintsLevelTextField.gridwidth = 2;
		constraintsLevelTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsLevelTextField.weightx = 1.0;
		constraintsLevelTextField.ipadx = 61;
		constraintsLevelTextField.ipady = 4;
		constraintsLevelTextField.insets = new java.awt.Insets(7, 1, 10, 38);
		add(getLevelTextField(), constraintsLevelTextField);

		java.awt.GridBagConstraints constraintsInhibitCheckBox = new java.awt.GridBagConstraints();
		constraintsInhibitCheckBox.gridx = 4; constraintsInhibitCheckBox.gridy = 2;
		constraintsInhibitCheckBox.ipadx = 23;
		constraintsInhibitCheckBox.ipady = -5;
		constraintsInhibitCheckBox.insets = new java.awt.Insets(7, 1, 12, 52);
		add(getInhibitCheckBox(), constraintsInhibitCheckBox);

		java.awt.GridBagConstraints constraintsColorLabel = new java.awt.GridBagConstraints();
		constraintsColorLabel.gridx = 1; constraintsColorLabel.gridy = 3;
		constraintsColorLabel.ipadx = 12;
		constraintsColorLabel.ipady = -5;
		constraintsColorLabel.insets = new java.awt.Insets(12, 16, 11, 26);
		add(getColorLabel(), constraintsColorLabel);

		java.awt.GridBagConstraints constraintsColorComboBox = new java.awt.GridBagConstraints();
		constraintsColorComboBox.gridx = 1; constraintsColorComboBox.gridy = 3;
		constraintsColorComboBox.gridwidth = 3;
		constraintsColorComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsColorComboBox.weightx = 1.0;
		constraintsColorComboBox.ipadx = 4;
		constraintsColorComboBox.insets = new java.awt.Insets(10, 68, 4, 2);
		add(getColorComboBox(), constraintsColorComboBox);

		java.awt.GridBagConstraints constraintsImageLabel = new java.awt.GridBagConstraints();
		constraintsImageLabel.gridx = 1; constraintsImageLabel.gridy = 4;
		constraintsImageLabel.gridwidth = 2;
		constraintsImageLabel.ipadx = 9;
		constraintsImageLabel.ipady = 1;
		constraintsImageLabel.insets = new java.awt.Insets(5, 16, 210, 0);
		add(getImageLabel(), constraintsImageLabel);

		java.awt.GridBagConstraints constraintsImageButton = new java.awt.GridBagConstraints();
		constraintsImageButton.gridx = 3; constraintsImageButton.gridy = 4;
		constraintsImageButton.ipadx = 16;
		constraintsImageButton.insets = new java.awt.Insets(5, 0, 205, 1);
		add(getImageButton(), constraintsImageButton);
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
		TagsBasePanel aTagsBasePanel;
		aTagsBasePanel = new TagsBasePanel();
		frame.setContentPane(aTagsBasePanel);
		frame.setSize(aTagsBasePanel.getSize());
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
	Tag youAreIt;
	
	if( o != null )
		youAreIt = (Tag)o;
	else
		youAreIt = new Tag();
	
	String name = youAreIt.getTagName();
	if( name != null )
	{
		getNameTextField().setText(name);
	}
	
	Integer temp = youAreIt.getTagLevel();
	if( temp != null )
	{
		getLevelTextField().setText( temp.toString() );
		temp = null;
	}		
	
	Character elChar = youAreIt.getInhibit();
	
	if( elChar != null )
	{
		if(elChar.compareTo(new Character('Y')) == 0)
		getInhibitCheckBox().setSelected(true);
		temp = null;
	}
	
	temp = youAreIt.getColorID();
	if( temp != null )
	{
		getColorComboBox().setSelectedIndex(temp.intValue() );
	}

	//grab that image for the button
	if(youAreIt.getImageID() != null)
	{
		int yukImgID = youAreIt.getImageID().intValue();
		if( yukImgID > com.cannontech.database.db.state.YukonImage.NONE_IMAGE_ID )
		{
			com.cannontech.database.cache.DefaultDatabaseCache cache = 
			com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
          
			LiteYukonImage liteYukImg = null;
			synchronized( cache )
			{
				java.util.List imgList = cache.getAllYukonImages();
       
				for( int j = 0; j < imgList.size(); j++ )
					if( ((LiteYukonImage)imgList.get(j)).getImageID() == yukImgID )
					{
						liteYukImg = (LiteYukonImage)imgList.get(j);
						break;
					}
			}
         
			//be sure we have found a matching LiteYukonImage
			if( liteYukImg != null )
			{
				setImageButton( 
					getImageButton(),
					new javax.swing.ImageIcon(liteYukImg.getImageValue()),
					liteYukImg );
			}
		}
	}
}
	



public void inputUpdate(PropertyPanelEvent event) {
	// Auto-generated method stub

}

}
