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
	private javax.swing.JLabel ivjStartDateLabel = null;
	private javax.swing.JLabel ivjStopDateLabel = null;
	private javax.swing.JPanel ivjAdvancedPanel = null;
	private javax.swing.JTextField ivjDaysToRetainTextBox = null;
	private javax.swing.JPanel ivjOkCancelButtonPanel = null;
	private javax.swing.JTextField ivjRunAtHourTextBox = null;
	private javax.swing.JCheckBox ivjPurgeDataCheckBox = null;
	private javax.swing.JCheckBox ivjAutoEmailCheckBox = null;
	private javax.swing.JComboBox ivjNotificationGroupComboBox = null;
	private javax.swing.JLabel ivjDelimiterLabel = null;
	private javax.swing.JTextField ivjDelimiterTextBox = null;
	private com.cannontech.common.gui.util.DateComboBox ivjStartDateComboBox = null;
	private com.cannontech.common.gui.util.DateComboBox ivjStopDateComboBox = null;
	private javax.swing.JCheckBox ivjHeadingsCheckBox = null;
	private javax.swing.JPanel ivjCSVAdvOptions = null;
	private javax.swing.JPanel ivjDBPurgeAdvOptions = null;
	private javax.swing.JButton ivjBrowseButton = null;
	private javax.swing.JLabel ivjFileDirectoryExportLabel = null;
	private javax.swing.JTextField ivjFileDirectoryTextField = null;
	private javax.swing.JPanel ivjInputFilePanel = null;
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
	this();
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

			java.awt.GridBagConstraints constraintsDBPurgeAdvOptions = new java.awt.GridBagConstraints();
			constraintsDBPurgeAdvOptions.gridx = 0; constraintsDBPurgeAdvOptions.gridy = 0;
			constraintsDBPurgeAdvOptions.fill = java.awt.GridBagConstraints.BOTH;
			constraintsDBPurgeAdvOptions.weightx = 1.0;
			constraintsDBPurgeAdvOptions.weighty = 1.0;
			constraintsDBPurgeAdvOptions.insets = new java.awt.Insets(15, 15, 10, 15);
			getAdvancedPanel().add(getDBPurgeAdvOptions(), constraintsDBPurgeAdvOptions);

			java.awt.GridBagConstraints constraintsCSVAdvOptions = new java.awt.GridBagConstraints();
			constraintsCSVAdvOptions.gridx = 0; constraintsCSVAdvOptions.gridy = 1;
			constraintsCSVAdvOptions.fill = java.awt.GridBagConstraints.BOTH;
			constraintsCSVAdvOptions.weightx = 1.0;
			constraintsCSVAdvOptions.weighty = 1.0;
			constraintsCSVAdvOptions.insets = new java.awt.Insets(10, 15, 15, 15);
			getAdvancedPanel().add(getCSVAdvOptions(), constraintsCSVAdvOptions);
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
			ivjAutoEmailCheckBox.setEnabled(false);
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
 * Return the BrowseButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getBrowseButton() {
	if (ivjBrowseButton == null) {
		try {
			ivjBrowseButton = new javax.swing.JButton();
			ivjBrowseButton.setName("BrowseButton");
			ivjBrowseButton.setFont(new java.awt.Font("dialog", 0, 12));
			ivjBrowseButton.setText("Browse");
			ivjBrowseButton.setMargin(new java.awt.Insets(0, 2, 0, 2));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBrowseButton;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF9EED1ADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8BF4D457F172B171A18DCDF0233432C3FAE49B7B601AB6A4A52D1A121A24A42189E9F003DDDA63D6C9E9D19345A535F20A9DCE7C10D69F73B182C4AD9989ADC8A081C27F3FB088FDD7E211E474C1B6F22C1576C9DAD86DAE3BCF305858694C3DEF6EFB7B762D04F0094E993DF7E76E5C19BBF76EEF6EFDABE56547266EC84E12249D29127AD737C31286A6A4E96BB30DCB89674B5EE211F4FFC7G76
	C9D7FA90998534440F36D8B225A72EE4C0BA9A524BE736D85E007783D2CE456E7760A5021C4DA3121479A74A09596564E7131CF416BEFDE9B7BCDF8708815CAA000C9C145FFEA9DB4547836EF5759DE1C7B2684572BCF9B1DB07376770FCD16AF364CD4CB3FAA9D725EF82FC92C0B9605B5E544AFC0DE9772946FAB5BD72E02C3461FBBFCD5B0332F46593ACD5F9572ACFC9722721A1D224041CE2F81E693BB6F9F65CBBDDBA6E70F83C9EC59E1FAA1503BE2FDFE9ECECEB691B71CF4AFD8E0F6C2EBF047A7D70E2
	866305BFC8F3DD1E59F3C61595FDADD6DA7D588B91F539FD086B90FBC853E564415A1DFD39D8762719ED11AF9E68590063FA2EE9A91FC34F734E9B8E4F386C6C75A9AE2FA720537719CB24EFB414171507381F58D14EBAC8EF86581ECB75F82B086AB17C6117250DE3FB5277C62847F01E5AF6F9DABD5A96B4A567B09FF9AA875BF99550628112G328FEF3154838C839CE23EB911FD921E674714D66F743457F35E67EBF1BB7D973DCE598D6FD023B1C545DD12473DFE27A4F17F5BF03144C49F825F61D05F537C
	6532C47E327C5293520012121EEAD23768E793539628E5449C566A466B55F9886B9587788DG09G167CAD16C20036FC2A570A6C2A396A5585ED6777B85C7DB3E3BE07DFE919F1391D32DF2D5F67AE4DD6BFB360FCDF1AB79F374B32AA3DDDD67D796B6969A6F6D9356C253451FCEE0FE03EE697AE7B390832E372435B0315170FFEB8F4917DF0B93C6FD0FD1463C799DE32EAF85EBF92185FE2BA93E80A157CF678F52A035D72303435FC2FE99D025F3224EF34F2FD36DA0DEDDB100763CE6E9170D300BE00A900
	05GCB81620ED07B7EAA3B6C8E7D36CF57361CF7E54E7F42D38EAAFEC717A720E00F9DF278DD0EB1379C1054BC5F48C15D47C2783E457A53B8486814BD325F01FD9CCAF7CC4B60C8811E677BB97BB00FDFE6548157348C8226FD92157B231C8370F4BB824A204F69D0E4AD8727BF1F03363C6198029221GF84FB80AB67F999B6FB6C27D13G382D7A8EA34E8EF467D1324B501B7B61F9819CDBF64338699AC7B59C7EDBA43F6F900EA5728D59233449BE5963041700CA3BFC68B54DAE93DE2173FB8C0FFD249594
	8F887C7821FD9C8FE5682DCC74EB0C6ED0943FEBECC6117BE5C5F1F9A6B57EG53A590527D82125A4773514702D02F45G3122FE5B8F9D50554F6E75DE1B718DE04B29F67AB99BE7D7427B3A23B4D70EDC4439F36BD17253970749CF5B76BCAE6DFEF60F291FA2E496F07FACAEB0763589D626A663C57B70BC7B3D91D03F594716740B1FD4C64AACB4EE772FDB516EFDD0DFB740228232FBDD2E15773796C7C0269E566B0796131D1A17713C2773A27BD8F51E390FB566197BD8F35E6CBEE44BB373A1CE6B4D4339
	45FB53237349013C7D2C8FFBBCB2D3DA30C8FC0EFFF1GED617238144BAE67242C743BBE10211D8B503712FB50371642FBDC8179524D827225428F9E11E25FCFC8B31B4F020F2727E7967276ADAC54FC096BB4F898EDAE3B65E9B0D42BF74623F07C95563FD53CBA3EDC4CB74F5B1F0F7EF8CD3E25663B1C1FA661B8621E11C55DD6B8F6210D7D2EE9E8AA6C306AFA50522FDF8F3601FE38F6DCG4F980094D65E097AACEC7F5075CD870EFD5CEFDEBA02FA2D007CEBGB695125F7CA8BFC757DFD507E9753A5D5C
	7C22DDD6DBB3319D656BB3B288408EFDC154653B567D865A10DAAFCE7F21956B65560D85C905D82F1C9EDAF361DC41F1658C578546B9E0213977E226FE8D5B9610CC57A4887547F85B590F99672D6282AC578D785B47286E5DC774E35579C040BB6EE253025A9E8FB5629A45ECBDB94EA635524999D7690C6242015C21C0FF714DA8ECC9A971B525279FE5E2D90B0F511A7261A1EC0735100E8758A6F4F979C838AF7D7A48CEF8CE387C81D88BB965A05953F2946D02F3140A2635DB255ECE3171C9263E4E464D63
	2A2F9FB75A49D308FAB483FE60B869765B237BF4F602F5A6CC4913500A244F6902946E5BEA3A22A015FCDB2231B150EFDC7BCC9D5746535B474347D63E46F97DB25AEB71F13E46C995789C06DF2A437335CFAC33AF26630016DBC4F331739856AB8A520B0B28DE5FA958232BD71F5765D1C41D820558BF3DFE57244BC338A04341AA1B8DD8FCBE7BBD564631D066CAG0E1B9CE4EB81C8EFAD2279A76BB262320AE11FD6CC38FC066B0334A2F0CF8CFD52B984E598F165AA0E4BF87A934BB01B27D255A737156B98
	36CBD02427D97A164AFBC0FA24BF0D7970AD6F8C985DF2826D96A746399850F78540C60044E23ED660FD72ABB7F1AC1900018D2619FE1FBCAE45B35E0F8709578E69D100A800D80055A7E80CF4D5E0FB539849FBAA69737B05B89674DEEBC51A3BE5C6D1F06D08B978987C76E57D98DCFC126BE40767A8G4FB3FD44ACCF901E0BG38FF4677535EF5A960B61D24FAD50C623A1A0DB5D79DB79C250E1B30DD6D743B1CAD0EC9BB33929FD30A87B2D47E2CD3B4268C8DE019F6C8BB81EE1F2239AFF81C76294B1FF8
	D8AA09FABD5DEC3F3DFA6017E9512738FF441D524603F25827F219CB87C9E69250CBCA90778F03D986FFA803FF257064F44B101E0E63C7112427570650D15F9FA46B69D3A1F434D1194A4F6B767DE3D4376D5FDFA6593F36BDE2ACC1A9D177296AB3AE65F85A6A6E7B52395DDFBA112D37FB30B4F0939C2B3131DB979B78B46B2758F75617104D8FF7204DB3CEEF31D8816A91D85E470F50185FF71A72C66D433C8BA19D8B90F71A7A7AFF040D09F38167CB3ACB3E5383736563E3ED71A06A3F7EB49FEBF788FC85
	43EF5261390F0F8C100FA7G4DDECAE3F0FE915AFA85245781EC86588E10DB8636AB23B1793F0FE18CE756FD630057E707452BDB37256175FBF2706E76765FCCA7DFDA7975C7251887D6256D0A60CB76B26EC3EE755917FCBC8D1F3CFEDFAD467AAD81DA9C40A600A4000CF27011F22ADF637192DE3FF627CBB92FA80E71A9B663D36506DC72CD8D0D839C2F5BE7AF6B6BD68D65594A35F1FE24BC527C79874CCFFD65467973CB8C3F205CB8FF96F6507C9983346D65642BFFF244E85B518ACD26D291C9FE92777D
	8A237CD799FEC905D17E837BC87E8A20E5D5127C52C1C978F11D6EFDEC60134EE5F3771B8F6F298C6E9745C51177E4E5C566FB321AA273BDD9D3516CFB32562248FB326EA27D1E0C7B753693B8968721AD228162G568144D712CFEFACBA4AFD5A441F5BE113566AE5D19C05647FF5F1643AFE2BF86E584F770BF1DDBC6605B9FA9A4BA77CB34558DE0A5747109C77DCB15A8816F1D38A4F2A62B795337D00BC4150FC5D76B89B6737C33DB22BE8AD7778A8622C102E963842B1DA5F8DD711FF46F4A0EE9924578A
	5CE38C3799792A89F73263137A924A5027576A52DCC66BA716E1DC5F256A5E398D3EF9046CD2D2CDF6E9AAA03B74097A3E320F2D0FA19DA7F0297B91378E52098237E8DFA41D1177EE6B91D72DDF77BE9ED146ECF11739A0F45DAB1D0C6C5B69A767766D4C13EC9F88BEB9F04BA7F35C4113ECAFB2B19110C59FFA73E42640F5857A3C8197EEDC79DAF65565B456BF7CAC5817DD03ED73234B98FF58DDC3EB61D523616B6F9F0D527AF524062F3F95F8AE8440BD4A86D6CE53D85BAC3620F5C00A8A314033926020
	1ADEDB232D95389E6F84F02CD24F0FE0E92EC6AE843DE09C57F52D7DC3A1B43E5EFE3643385EC6BDF3EBE10FD5CBEB722CFA33BCB6200FG70FA7FD42C5B15DADE6FC5700C2D657BB2DE6FCB85F3551B6A4C761E403B2E16579B53713546FAFF5402731CFE374171FFBB096D5FFB0D630A6BD0F65DFE7DDAB8D155AB8BE81C1E13EBC67755D1DBBFBA8A8FF52D73162C0F91E4FE38A6ADC5B2DF6BAC2863FDA7DAFD96569CCF63F3742FC6E9FD3786E85B6BE86D5B6A20FDC2F5BD2DFD2FBBB02F9B528BG16G64
	73FEA2E5C85A1F99EEE5BD1E3386ABB45DFF3C1B56E939CFFDD1AA7C47D5E98591F45FD64FF536B5E87B195B856D6AFEE6CFA72D67239AC84FB35528E79C245783A4B4D01D6C7514D7EA243C8BBBB1EFBD247BG269AE9EF377FA859A58AF0F10DD4463BC749AEDB35B2D85E6226AD16E60061A6B33BECBDE70663F679C925A4D15CF0EFBF4E53A6FB986EC428AB0FC76D273D092F0976089FF5684FE4176C7DE544356F32A675FC38C933E91458A37665E259481BF106567B4FD7611853FC065689CF1D44F54210
	632A3A2A47A530574FD68A00EC05B5FEBFACD9BC13B4DE6FF05C5DBEE76FDD7259664D4757327F4EFCFC600CF1FF74A2430F1EB16E0FEEBA48CF5CC0DBFF065A6207D774FDF36BC31B53084FA8377AAD5E864DEF9963F7EF5760B825GFEB140CA00F5G71G5BGF61F053D88C0B5009D40899084D884908B3082E08D40463354E65F28B8B1CB1B29818A364073BBF87C0C5BF2FF275E1609D01EF4CE1B47334EC55AAF2CE07D30789C3759AE017F9B063769705C16AD8C0F69A9202DBBC76B09376B75724B1B37
	D85A1AB519434D1164175B58F8554C659C9478D6060F5561397C4F3231G539B0096F51E64E79F3DBB9FBCFE17FC6F4F1B0F57EBBB9B07CADA0C3E1BDA40463896236FFED60CF1EDC0DB56C2758D6CB7133F3B155B595A9AEEEF6E2F3F2E45392B9A68FDGD3GD1G312D64FFA72B663CA7509E1C651E40E0AE6935DA27438675BD811E5B81B25BB49A177B2FD538BE8A040CC7BFF3C8425EC93A770F9C77FE8F7A0777D486EF0BBC9CFE547A0FD59F13A274FBB0F9B7CCB33DE3D765F1E540B1A9F8641A48EBDF
	F74D5CEB5F199A73FD5D07B5617B3A289A73FD5D83B57AFD9D47FD2686673CC9982B06C53E4582770E40FD011509384BAE27B2A570DFE4FAA15EA623C042BF42743A49F043AA6E899D6E9D9577D48D7A92D4F94A6BA75E95B5B8B7CE385CEE754E834B637240F2DE58632F180EAE1FC327775F889C6A4D77DDA758FA2690FC383E0D76E24BEC089B0134CF600A3AE9FF965DC67D76AF2A317FED0634259D668600E60011F6222FE1745B10DE82308AE09340F6C137D5C772CBAC4F8C5F2C430F286F1B5B4273DA05
	8CC7C4992991702F695EF729CF33BDDE3A3061D4BD59B037036C75138632E133400DB4118D1D9D24DF2D8D6F472D0474FA00C4005D1DB04782748138819681AC83D853C9BCDD0CA79E52128D6C8E508CB08A9085908B309AA09EC06AA21E684ED8F8E2D9283B3BD333994AE4632DAAA7D2BFDFB38B8D81756F1BA38F56EBE547DCF7A11EE63A6ED3F1FB8DF4EA23833348B9B88B0D5AFE4F9CF9F6C5E877D0DD13F19B213E6F523DC79F3D77637A1F5ED3996A99A5DB7797029FD5F7119F7FECBF79F1F3974D35E9
	F511476FBDF566B14883F566B1481C3A596F9E9D290B9423647A3959FEE2B16836DC681C1FC175D8ADF0D199D40FF5827778C14465F6C3FD3B8917FA83F1B6C80FF6533E3839D39FA76131004B1DA296G7996812C8658G00F1BC4E7734ADBCF6721C0D78923BF98C41F2E10BA57B826763B104335D73091D1483EF5B85ADF6B2FC4198C3F05533580963D6E040FBC9D6E07DEDF70C694EB43FF584E3C417E658B5B71BF7464F481CA6D79DB0708EC06336F803AA6F830A4AFBDEC1CE1548E94399A817C5EC705E
	C36B14BCFE8DB9E15D4E6291AFD76B6391CE55868BAFD03C4251E3C6DFA268BF9B5753D728740D02AE8F1B5193857DC03B1EAE59B93D58CE711E87C5CC680C1D3771GBC276CBCCE4B736CEA24BCD1F65E1EB170DC2E66616D99E83A531890429A605DE8575AB351EEEC4F3C0B48572FC0F7813B4AEAE3F25AD3D777B01A5727A32934377C589E67E794EF7B34436556358727FF238A57F7BDDE45B5619AE797DCBA7D5E999F2C28473C7AFC5F1B41B6EF135D2EE997AC29C35AFC276DF5BDADC437A75F5B2757BB
	2C5C07FBB2F53A931953EE0EA04DA6BB1C587DC9F5CE7BDC914AE457EABADCEED9BD3067EDFC3F4D68C358AE5B6C648313D74C683BAF925DDDEB749124DB85FD741C1EDEFD1153C7843D2A536803C87789FAC32D197C85027E6396BDBDC6ADFF23203FD2E1C6CF94748FDB4C7417FAA86E7969C6E35C9369D9C0AF6E21F1EC1C2DC195E8E69ED37A28810B623E1DFCE0B65FCEB1C0B31409F0D07567019E23EF5F1F0B7E6B734B93325FAFBB7935BDA92687E5ADE1E37DA83C3B8138AEFF5A286972C799336912BA
	07AE1B42F4D9F5905785530E20EBFAE61AE942755840660D84F8CF92B64A3D41723ABC063C55ECBE3176EE311474D27C6F371DF46FFA98F00BFB69EEC72A9577D24678DCA91FF89A9B5B1571A9595922CEC3EA2C6E3B1D94B3DC5BCBB143DF327225BE18D7G4A7BC86E2FED34A73781AE2A0F620E9DDDD4C6DC9F1521F4E15E2D104EF89B7C7D6D79469DBD67A4E17317A6A25C1DE0238357213C06E20E75EF13DEB1BDA28E79B65968D8F12EF9D9B88AF538E43713739EF5D2BCF619A8EF49B06162C5F9E7078E
	1917A7C6C74E5B5D0E3C3806493EC4767C2BFAEA47BE40AD3CC4F2F6B410BDD7DFA2FB16B5E05EED104E6C073E55CFE5D4B5D21BB483AE58CFE5FC58C8E52C6827B21EE4F993A0AD8DC05B8E4C37CD9EEC16045FFC5DC636AD9FA019FFFC05F0D38277D8AD55BBF600ECB6690EE4B39A51B96F9F1D2372B60A722EF6D2F91903047355D2BE5BA049383F3BC23A73BB157A390417195DCAF7C623041C0ACA4245895CD7DBC876D6017BE6A3610A0748763FE5FDE79452C10045C36667935C4EAB4FEB6F09AD1298CF
	32B28E4353A35F3452552D52B6D8F1F9708301623A2A92EBE54D89BA43083D290F71E66FFCA5629D4DE5C3FC0F35EAC8BBFF6132FF51266911552169714A4C1D6841796ABAB53EEF5B6F040F6FD7BE55B9D71CB1E9A8F4EF4879BEBFA75FE1C31A732D1B134F88BCFE69E26D1AF959985774B3FC6EE5E3DC73D71D54DF0B013668B26D39ADDDF7970F5DFF17FC97664D476B15404775307A7E02613707557703AE2AEF46B04C3B43D45F1A9AB3796B0755B84DF0783EDF7D2E2A470CEF1B1AFF77BB11780A1C44E7
	556529D65FED91799E99A63E91DD9E1F7A3EA0A2DFEFBB724598684B5574EA7539C1C7D7639975F76777BF18134F5C6F539A6E4E8F9EE373D142BBC6BFD84E70C96F987D2026017CC0FAF70B4579AE79C1C3635D79EF67DD72C54D1B0F57AB2F095D27FF57D85F638C3F61DDE3FD3FAC6665F82035DD217ABEEDB3133F6C0ABA86DF0964CF0FDDB16DAFEA7E4D917916551A7697B57F6E91D81B81D40F987DF04939596CE4037CA3A3E6FE68956328CFC7DF203E4740F339A95F0C291E2B553C9BC6A255AF2FF5B6
	BD93A27255D64E4637BDA25FDF361871E53E274E1D6FC5627BCE185F556B725ACD7838FF3D44578D6FF17FCA9778D799BE2843F33F7B9443E3FAB150125E23B3724DEC5DB47C1BAD9665B794072BE8BDA5C5BEF36D73FBFD32DF3945316CCB9BA9CF4239FC75815CE339BCEE17C7EE65DFC1EA6DEF1344FBD0773E43F66FE31CEF7E8EE43C58255569FFF46FD1DD77DE76C377D4860FDF5AEBA876997D9B0AE9FE2506E21F318237320772ED9538033D14EF3B401DE57BA87B7BDBACD36F936EFE660B37A13DC860
	92BA09B7C660620609B7C160DE903C5B856E2C0DF8B3C689B74176DB4310F68B5C1799AE8A520B85AE2B03F81788DCC2B56292A13D5BC16376138DDADBFFD5775E54F06F5BFD60F7A02320C92BD3096E7DEB0D77DE765F5FD3993CCD1F33113FDA9D544EF5DD5476A5827775AB146F36409D9A21FC8B856EE936675C88698401AB2C23FCC98277E74C3F5A46E03E9CA31C056DEBFD10DEA0F0FFFECE783F40ED6A20FC898277C0A775096D82F7DFAD75094CF1F126D8CB65658A5CAE466B06748201DB447245C0FA
	19407DC2702E94386AD642D91C043BD8C93881017B7B96AAEF294015895E958277825BA3CBB26C0FE5F166F90672554B546FDEE67359C2C8AF87D8AF7029ECDF11003C93DBAC4593047F4C3C67GAC175E972AB2E6775725731621BFBFCE113433C9B360F373FFF6623BB83762532FCA5B4AE24278ED324B61F6FD40A26D7CC69B5FF38F32F90947B457564C67AC4886F6C4B82462C7A634F52DFACF32DBAB3B24F7BE6792463227A60C7152C1E764582D074DA38A70C4CDD04C74614E50387297AFE83A3D5AB9D7
	DCD92F5FF2D5B73A6739DA279B177FE3B6972D837C46890A9DA7DF54648D5A66934736CC064A4B1E8C3B374B662A000C5FC74BCE2EC3F4BB451F8AA749865FE98F3541319ECD27D39D24D375E438CF5377B022FDD027C9DD5B076974EAB5462B263CFE5787DE0F62F01FF73BA6BD789506EA231C9A0AB9CECD120DEC3D1ABEBDDD73714598A8A3C6274F72C923AF5E68534A2E9C190FAF9A4B0E8B2B6BF33521F6FD68EDCDD6D39D49DA93412E3BF432B6C21E0DBAD909E1323E5D413E3F767A65C93FF74663A41F
	FB010D7F5BA03FE50A5A7B50B99CBBE48FDEE8F22A79AA4F115D4B27684C70FB67C8473629F09DD38CBA8ECFF19D699E39FB4A687F29621CA088B40E7BB243AD42749435772ACB1A1DDE2E95F60A20035E7FB6C21E44A92DCFC8AE231D0EF61A1F11B4B2BDF6C37EEC975969B1C37B7DEB3F26D706E83FB6575C7DE25845752276F36B746265DDE8257202A66599471AC5EAF9E46B5830721C15D45E72886569E135213C8DAEE3BFC99E546A5EA1F40D0FD07757F4E5A7CE0556BDA92CCD129A436FA86CEA22B3C1
	69AABF534E06E749D5FE47C8BD53BEA35A602A399E693AF69806BC6E2B5A19F654D5239E975AD1A69E5F36397CB0E6FA7D375A590FF1696EA2CC347F2FB10FB87521F26EEB47F5C50B5FFBB3A06B3F0D286FB21E33E2DD96DD25F352528663B9AD52E3857D21EE633793C8DFF9F536F300E3C35A3BF2D9FB0FAB15C43D031D334454EB4ACB2C1565A74C3EFB20B31D674B34F2F7D6E8657E43A7AC77F425D656D3FC0E724F487CFCE424034EC7564EDA7F3EAA2D0C721AA368CE3AE34DFE7CCF1F7C85CADB5BFB86
	5AF17009DCDE20DAFBFF3E865F39BE6EBA52C73AB61BBE3F2E555E1756EB3A3DFCB65FE82BD62F5B6BAF2EA82FACE1D0D3F5C24D5BF58E65CCB85C81D9487F15B8672AB715CFB67DEE03A6B337D3DF8E277F34D1235F283D13766372CF0A7A7BA2480FEC9376D3FB6AFB79DBBA7DE613DF95AA3FC148DFB62B7D7B4FEAEDF63B15F862E735590DF39A4F97AA09A75BBD9B4FC34D9A4FCDA1274FCDF35CB7DB112E1E3FB6D11CF7A150D73B4579EB934D175B5C627C1565AD1F06F5964068B465BDA94E90035373BD
	135D5C26696A6D22F2D608326586BA33CE9038755DF406EA75083B2F9D7A7864B0605D1E596FE7F23E6BF5FA3EA860093EA3BE29D94F378CF8D65D91DFD9083C754033790E784E0670ED831E5D5EBB613BAFA43EEC851E6ABB62FBB8A43EEC831E11BB623B9DA24F87BC8B6E082F2169AE4FD16E12AFEA5EFC6AB94A996C83B15E30F39406DF6A8DBBC7917DEB855032FC642FE5A1F178A06097796E44CE0A6939E72C4A3B52248C4EF7B5641CEC2D1AEF137A5C9A116F59EEBD1F65BA4F172DBECB6059FCBD1417
	47C202EC0E9C384E6D41F19FB11CBB843730AEBC5F6F570567FB23B9BCDFEEF3F83E8E1372AE1814B7E8126FDD13FCB12D61793E549A1E6FAB1561797E22B2BC5F67CD4A0B89A9CF0DE732F9C90DE76A5E3F5674BB08E75ED399EABB351834D3CB383DBE43660405000B39CEF1362DA26E39D4606EE373C49224ADFE427D57F144E5C33AD860860A0837DA609259FC32G525182F77B9449DDA6F0AF1C92F1C501DBF3GF116G6C9182F4F747F19C7B3C9A5FE21F48F0FCEEF00F0627CFE454754623DD217B3DF4
	F6DF056FB55245FD68420079DEC33FBF2B07BC88F42F4F9EB06EB5E29A58B7B5DE0F42771FFF46ECB78C791C815AFF2695056AF33DD35367DA916993ED220FF13F38CC278F7BDD13B0FDD2AE046A13F38175D98B79B684E84D92ED5364A71DA27996A5DCFE2AB43BBD3295237C678F609EF05A61E73FE146657DA50BC17275C6FDB72DCB8A155956F77F5C2D3DDF6C11C4797FF69C3F678DD9CBF6C0E50B0FBA418F3C13C7CB2C301E2C28946B49A5C5A4EF44D49E2D1DFC3D1A3CE786B5F93ED37313F72C0464C5
	ABE46FA436F7636341DBA1F340BA4893EFE27770F16411A2BDDF9270E44C5C895F4992BDDFAE70144F4127766D6E70B1651D6E70B1A52EA8BC5FB70A42731DAF894F57D1224F47FF97FD4F85BCF0DB7EF3A9B56017768A1BF7310F5462EFD4B5267C5BE3A9491225CBCA639F6DF5C9967C722ECBCAE59F66F5C9A90A579774C9063F40F569E3A38E7A241446FF8EC11112FBAF08BC98025BCBA030FBF530B3243CAC95A450F9FF789E677CE77F699F173F5F1615ACA5035AD3B257B375A60EE6CCD3BC166F0AD0A2
	E2B3C2720814A26D450FA92C0A574ABF27E8D4248317E6BCD607E2458FA82C456527AB2BCEGB6392ADC11F6D4AB52CE76850595BF21D024ECF6FD9E933235F8FA3A4C69AC3B85FF4870865E1CB7256C43BB7FD6F6695F4A2E7483EE47EDA8D95C538744DE58734ECCFB2CF447DE548DBB469975199A02CD8D41E628752F7C04F96E8556AF3D9E3A375F71789B77E7FE2EEAA6D94A681052E09F6E1BF2C07B329F2334CB3B4618E34AFE482BC819F664AD835EB2465BE4171247830AF4C8430FA31ED37BFD0EF199
	323C66F4782F3192D02502AB52EB937E49B1A6G9A77CA4795904BD2C8BEE617F6AAD3788BC8B865C0DBEAC577617772AA2109F712D7A7A69E72313F7F2766B87C3BED8EC55A5D6E117D13372C984C835FAB9EEF9C39B5F34D6B9999F73BE40FB2B26E75CC38A6C7F82E1E1969B1599FA8D550B2E9BC3697527312D510A2367E1E1019605B0F547C473AFF998A24C8E9DDD28ECE3C7D321F1D4549CE6CE66A0FF6F7C82F797047BEB1D48DC3D600BDD2834AADB738C85644A57CA15B00A2BB4F53A99F7844C15EF0
	F8B3394C6688CCA9986887EF30F8706745257D1C6CF6F8A64B78119FEF529D671B195E6C62BE68FB00690B25140AD28E7C755EE3C73AB74725C8499D52CE3757616CF0E09034CBFACD19724B01A92F5B99741D6385E55C4436E5E784D05CA1D1FD2CECA9C6CD05525702DFDA34A7E1CF8798B068174AD8CE7435D2114359BA657003A8595F25604F25FB7D0502F65FE165788FBA5F98C916F6281544A6288F7A94504A6F7084A63C7EE95040EA5000C82220524F7FDD79971EFC65DDD610462AC879DAD718143D53
	E567A7A6DC9ED994G76036273C47118275410E7FF7041035D4F3EBA866D5DA625CC1717337F956CFFA57E2FE0188A06292884A3EE15E67DB352034C714EA85CC944AC084DDC5A81AD257C734F7F6E51674FECCD045AF528238C98BBD34B45FD7C0165DF485B959D7F99EC8B9F3A84158BA354344BFD8BDE5A589AA608F8FBF016CED79B66D508ED8CF78FC41FE544E6763FF55E15C72EF6465B09827125BBD4537E881A723B6EA7FE2B3F6B3E71D34FC73C6BDE7EB1DF8735FDEC7CBD13CB6C6ED2946017FCCC77
	639E6DB9ADC57EED027EE9075B2DB85C574EFB1C78BB85AD3794D97BAE75F2AF169F20BCAA1153FE530BFADF55717C9FD0CB8788EA095BEE56A0GGFCE6GGD0CB818294G94G88G88GF9EED1ADEA095BEE56A0GGFCE6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG90A1GGGG
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
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getCSVAdvOptions() {
	if (ivjCSVAdvOptions == null) {
		try {
			ivjCSVAdvOptions = new javax.swing.JPanel();
			ivjCSVAdvOptions.setName("CSVAdvOptions");
			ivjCSVAdvOptions.setBorder(new javax.swing.border.EtchedBorder());
			ivjCSVAdvOptions.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsStartDateLabel = new java.awt.GridBagConstraints();
			constraintsStartDateLabel.gridx = 0; constraintsStartDateLabel.gridy = 0;
			constraintsStartDateLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsStartDateLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getCSVAdvOptions().add(getStartDateLabel(), constraintsStartDateLabel);

			java.awt.GridBagConstraints constraintsStopDateLabel = new java.awt.GridBagConstraints();
			constraintsStopDateLabel.gridx = 0; constraintsStopDateLabel.gridy = 1;
			constraintsStopDateLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsStopDateLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getCSVAdvOptions().add(getStopDateLabel(), constraintsStopDateLabel);

			java.awt.GridBagConstraints constraintsAutoEmailCheckBox = new java.awt.GridBagConstraints();
			constraintsAutoEmailCheckBox.gridx = 0; constraintsAutoEmailCheckBox.gridy = 5;
			constraintsAutoEmailCheckBox.anchor = java.awt.GridBagConstraints.EAST;
			constraintsAutoEmailCheckBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getCSVAdvOptions().add(getAutoEmailCheckBox(), constraintsAutoEmailCheckBox);

			java.awt.GridBagConstraints constraintsNotificationGroupComboBox = new java.awt.GridBagConstraints();
			constraintsNotificationGroupComboBox.gridx = 1; constraintsNotificationGroupComboBox.gridy = 5;
			constraintsNotificationGroupComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNotificationGroupComboBox.weightx = 1.0;
			constraintsNotificationGroupComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getCSVAdvOptions().add(getNotificationGroupComboBox(), constraintsNotificationGroupComboBox);

			java.awt.GridBagConstraints constraintsDelimiterTextBox = new java.awt.GridBagConstraints();
			constraintsDelimiterTextBox.gridx = 1; constraintsDelimiterTextBox.gridy = 2;
			constraintsDelimiterTextBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDelimiterTextBox.weightx = 1.0;
			constraintsDelimiterTextBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getCSVAdvOptions().add(getDelimiterTextBox(), constraintsDelimiterTextBox);

			java.awt.GridBagConstraints constraintsDelimiterLabel = new java.awt.GridBagConstraints();
			constraintsDelimiterLabel.gridx = 0; constraintsDelimiterLabel.gridy = 2;
			constraintsDelimiterLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsDelimiterLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getCSVAdvOptions().add(getDelimiterLabel(), constraintsDelimiterLabel);

			java.awt.GridBagConstraints constraintsStartDateComboBox = new java.awt.GridBagConstraints();
			constraintsStartDateComboBox.gridx = 1; constraintsStartDateComboBox.gridy = 0;
			constraintsStartDateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStartDateComboBox.weightx = 1.0;
			constraintsStartDateComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getCSVAdvOptions().add(getStartDateComboBox(), constraintsStartDateComboBox);

			java.awt.GridBagConstraints constraintsStopDateComboBox = new java.awt.GridBagConstraints();
			constraintsStopDateComboBox.gridx = 1; constraintsStopDateComboBox.gridy = 1;
			constraintsStopDateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStopDateComboBox.weightx = 1.0;
			constraintsStopDateComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getCSVAdvOptions().add(getStopDateComboBox(), constraintsStopDateComboBox);

			java.awt.GridBagConstraints constraintsHeadingsCheckBox = new java.awt.GridBagConstraints();
			constraintsHeadingsCheckBox.gridx = 0; constraintsHeadingsCheckBox.gridy = 4;
			constraintsHeadingsCheckBox.anchor = java.awt.GridBagConstraints.EAST;
			constraintsHeadingsCheckBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getCSVAdvOptions().add(getHeadingsCheckBox(), constraintsHeadingsCheckBox);

			java.awt.GridBagConstraints constraintsInputFilePanel = new java.awt.GridBagConstraints();
			constraintsInputFilePanel.gridx = 0; constraintsInputFilePanel.gridy = 3;
			constraintsInputFilePanel.gridwidth = 2;
			constraintsInputFilePanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsInputFilePanel.weightx = 1.0;
			constraintsInputFilePanel.weighty = 1.0;
			constraintsInputFilePanel.insets = new java.awt.Insets(5, 5, 5, 5);
			getCSVAdvOptions().add(getInputFilePanel(), constraintsInputFilePanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCSVAdvOptions;
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
			ivjDaysToRetainTextBox.setPreferredSize(new java.awt.Dimension(50, 20));
			ivjDaysToRetainTextBox.setText("30");
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
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getDBPurgeAdvOptions() {
	if (ivjDBPurgeAdvOptions == null) {
		try {
			ivjDBPurgeAdvOptions = new javax.swing.JPanel();
			ivjDBPurgeAdvOptions.setName("DBPurgeAdvOptions");
			ivjDBPurgeAdvOptions.setBorder(new javax.swing.border.EtchedBorder());
			ivjDBPurgeAdvOptions.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsDaysToRetainLabel = new java.awt.GridBagConstraints();
			constraintsDaysToRetainLabel.gridx = 0; constraintsDaysToRetainLabel.gridy = 0;
			constraintsDaysToRetainLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsDaysToRetainLabel.insets = new java.awt.Insets(5, 60, 5, 5);
			getDBPurgeAdvOptions().add(getDaysToRetainLabel(), constraintsDaysToRetainLabel);

			java.awt.GridBagConstraints constraintsRunTimeHourLabel = new java.awt.GridBagConstraints();
			constraintsRunTimeHourLabel.gridx = 0; constraintsRunTimeHourLabel.gridy = 1;
			constraintsRunTimeHourLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsRunTimeHourLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getDBPurgeAdvOptions().add(getRunTimeHourLabel(), constraintsRunTimeHourLabel);

			java.awt.GridBagConstraints constraintsDaysToRetainTextBox = new java.awt.GridBagConstraints();
			constraintsDaysToRetainTextBox.gridx = 1; constraintsDaysToRetainTextBox.gridy = 0;
			constraintsDaysToRetainTextBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDaysToRetainTextBox.weightx = 1.0;
			constraintsDaysToRetainTextBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getDBPurgeAdvOptions().add(getDaysToRetainTextBox(), constraintsDaysToRetainTextBox);

			java.awt.GridBagConstraints constraintsRunAtHourTextBox = new java.awt.GridBagConstraints();
			constraintsRunAtHourTextBox.gridx = 1; constraintsRunAtHourTextBox.gridy = 1;
			constraintsRunAtHourTextBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRunAtHourTextBox.weightx = 1.0;
			constraintsRunAtHourTextBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getDBPurgeAdvOptions().add(getRunAtHourTextBox(), constraintsRunAtHourTextBox);

			java.awt.GridBagConstraints constraintsPurgeDataCheckBox = new java.awt.GridBagConstraints();
			constraintsPurgeDataCheckBox.gridx = 0; constraintsPurgeDataCheckBox.gridy = 2;
			constraintsPurgeDataCheckBox.anchor = java.awt.GridBagConstraints.EAST;
			constraintsPurgeDataCheckBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getDBPurgeAdvOptions().add(getPurgeDataCheckBox(), constraintsPurgeDataCheckBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDBPurgeAdvOptions;
}
/**
 * Return the DelimiterLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDelimiterLabel() {
	if (ivjDelimiterLabel == null) {
		try {
			ivjDelimiterLabel = new javax.swing.JLabel();
			ivjDelimiterLabel.setName("DelimiterLabel");
			ivjDelimiterLabel.setText("Delimiter:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDelimiterLabel;
}
/**
 * Return the DelimiterTextBox property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JTextField getDelimiterTextBox() {
	if (ivjDelimiterTextBox == null) {
		try {
			ivjDelimiterTextBox = new javax.swing.JTextField();
			ivjDelimiterTextBox.setName("DelimiterTextBox");
			ivjDelimiterTextBox.setPreferredSize(new java.awt.Dimension(50, 20));
			ivjDelimiterTextBox.setText("|");
			ivjDelimiterTextBox.setMinimumSize(new java.awt.Dimension(30, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDelimiterTextBox;
}
/**
 * Return the FileDirectoryExportLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFileDirectoryExportLabel() {
	if (ivjFileDirectoryExportLabel == null) {
		try {
			ivjFileDirectoryExportLabel = new javax.swing.JLabel();
			ivjFileDirectoryExportLabel.setName("FileDirectoryExportLabel");
			ivjFileDirectoryExportLabel.setText("Energy File:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFileDirectoryExportLabel;
}
/**
 * Return the FileDirectoryTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JTextField getFileDirectoryTextField() {
	if (ivjFileDirectoryTextField == null) {
		try {
			ivjFileDirectoryTextField = new javax.swing.JTextField();
			ivjFileDirectoryTextField.setName("FileDirectoryTextField");
			ivjFileDirectoryTextField.setFont(new java.awt.Font("dialog", 0, 12));
			ivjFileDirectoryTextField.setText("c:\\yukon\\client\\config\\EnergyNumbers.txt");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFileDirectoryTextField;
}
/**
 * Return the HeadingsCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBox getHeadingsCheckBox() {
	if (ivjHeadingsCheckBox == null) {
		try {
			ivjHeadingsCheckBox = new javax.swing.JCheckBox();
			ivjHeadingsCheckBox.setName("HeadingsCheckBox");
			ivjHeadingsCheckBox.setText("Column Headings");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHeadingsCheckBox;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getInputFilePanel() {
	if (ivjInputFilePanel == null) {
		try {
			ivjInputFilePanel = new javax.swing.JPanel();
			ivjInputFilePanel.setName("InputFilePanel");
			ivjInputFilePanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsFileDirectoryExportLabel = new java.awt.GridBagConstraints();
			constraintsFileDirectoryExportLabel.gridx = 0; constraintsFileDirectoryExportLabel.gridy = 0;
			constraintsFileDirectoryExportLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getInputFilePanel().add(getFileDirectoryExportLabel(), constraintsFileDirectoryExportLabel);

			java.awt.GridBagConstraints constraintsFileDirectoryTextField = new java.awt.GridBagConstraints();
			constraintsFileDirectoryTextField.gridx = 1; constraintsFileDirectoryTextField.gridy = 0;
			constraintsFileDirectoryTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFileDirectoryTextField.weightx = 1.0;
			constraintsFileDirectoryTextField.insets = new java.awt.Insets(5, 5, 5, 5);
			getInputFilePanel().add(getFileDirectoryTextField(), constraintsFileDirectoryTextField);

			java.awt.GridBagConstraints constraintsBrowseButton = new java.awt.GridBagConstraints();
			constraintsBrowseButton.gridx = 2; constraintsBrowseButton.gridy = 0;
			constraintsBrowseButton.insets = new java.awt.Insets(5, 5, 5, 5);
			getInputFilePanel().add(getBrowseButton(), constraintsBrowseButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjInputFilePanel;
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
			ivjNotificationGroupComboBox.setEnabled(false);
			// user code begin {1}
			
			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized( cache )
			{
				java.util.List notifGroups = cache.getAllContactNotificationGroups();

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
			ivjRunAtHourTextBox.setPreferredSize(new java.awt.Dimension(50, 20));
			ivjRunAtHourTextBox.setText("4");
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
 * Return the StartDateComboBox property value.
 * @return com.cannontech.common.gui.util.DateComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public com.cannontech.common.gui.util.DateComboBox getStartDateComboBox() {
	if (ivjStartDateComboBox == null) {
		try {
			ivjStartDateComboBox = new com.cannontech.common.gui.util.DateComboBox();
			ivjStartDateComboBox.setName("StartDateComboBox");
			// user code begin {1}
			ivjStartDateComboBox.setSelectedDate(com.cannontech.util.ServletUtil.getYesterday());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartDateComboBox;
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
 * Return the StopDateComboBox property value.
 * @return com.cannontech.common.gui.util.DateComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public com.cannontech.common.gui.util.DateComboBox getStopDateComboBox() {
	if (ivjStopDateComboBox == null) {
		try {
			ivjStopDateComboBox = new com.cannontech.common.gui.util.DateComboBox();
			ivjStopDateComboBox.setName("StopDateComboBox");
			// user code begin {1}
			ivjStopDateComboBox.setSelectedDate(com.cannontech.util.ServletUtil.getToday());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStopDateComboBox;
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
		setSize(358, 407);

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
			getDBPurgeAdvOptions().setVisible(value);
			getCSVAdvOptions().setVisible(!value);
			
			getDaysToRetainLabel().setEnabled(value);
			getRunTimeHourLabel().setEnabled(value);
			getRunAtHourTextBox().setEnabled(value);
			getDaysToRetainTextBox().setEnabled(value);
			getPurgeDataCheckBox().setEnabled(value);

			getStartDateLabel().setEnabled(!value);
			getStartDateComboBox().setEnabled(!value);
			getStopDateLabel().setEnabled(!value);
			getStopDateComboBox().setEnabled(!value);
			getDelimiterLabel().setEnabled(!value);
			getDelimiterTextBox().setEnabled(!value);
			getFileDirectoryExportLabel().setEnabled(!value);
			getFileDirectoryTextField().setEnabled(!value);
			getBrowseButton().setEnabled(!value);
			//getNotificationGroupComboBox().setEnabled(!value);
			//getAutoEmailCheckBox().setEnabled(!value);

			getDaysToRetainTextBox().requestFocus();

			return;
		}
		case ExportFormatTypes.CSVBILLING_FORMAT:
		{
			getDBPurgeAdvOptions().setVisible(!value);
			getCSVAdvOptions().setVisible(value);
			
			getDaysToRetainLabel().setEnabled(!value);
			getRunTimeHourLabel().setEnabled(!value);
			getDaysToRetainTextBox().setEnabled(!value);
			getRunAtHourTextBox().setEnabled(!value);
			getPurgeDataCheckBox().setEnabled(!value);
			
			getStartDateLabel().setEnabled(value);
			getStartDateComboBox().setEnabled(value);
			getStopDateLabel().setEnabled(value);
			getStopDateComboBox().setEnabled(value);
			getDelimiterLabel().setEnabled(value);
			getDelimiterTextBox().setEnabled(value);
			getFileDirectoryExportLabel().setEnabled(value);
			getFileDirectoryTextField().setEnabled(value);
			getBrowseButton().setEnabled(value);
			//getNotificationGroupComboBox().setEnabled(value);
			//getAutoEmailCheckBox().setEnabled(value);

			getStartDateComboBox().requestFocus();
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
	dialog.setSize(360, 300);
	dialog.show();

	getOkButton().removeActionListener(listener);
	getCancelButton().removeActionListener(listener);
		
	//if( getButtonPushed() == this.OK )
		//return (com.cannontech.data.graph.GraphDefinition) getValue(null);
	//else
		//return null;
}
}
