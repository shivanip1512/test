package com.cannontech.dbeditor.editor.device;

import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.pao.PAODefines;
import com.cannontech.database.db.pao.PAOExclusion;
import com.cannontech.common.util.CtiUtilities;
import java.util.Vector;
import java.util.StringTokenizer;
/**
 * Insert the type's description here.
 * Creation date: (4/4/2004 11:31:17 AM)
 * @author: 
 */
public class ExclusionTimingEditorPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JLabel ivjCycleTimeJLabel = null;
	private javax.swing.JTextField ivjCycleTimeJTextField = null;
	private javax.swing.JLabel ivjOffsetJLabel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjJLabelMaxTransmit = null;
	private javax.swing.JLabel ivjJLabelMinutes = null;
	private javax.swing.JLabel ivjJLabelSeconds1 = null;
	private javax.swing.JLabel ivjJLabelSeconds2 = null;
	private javax.swing.JLabel ivjJLabelSeconds3 = null;
	private javax.swing.JTextField ivjJTextFieldMaxTransmit = null;
	private javax.swing.JTextField ivjJTextFieldOffset = null;
	private javax.swing.JTextField ivjJTextFieldTransmitTime = null;
	private javax.swing.JLabel ivjTransmitTimeJLabel = null;
	private javax.swing.JCheckBox ivjJCheckBoxEnable = null;

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ExclusionTimingEditorPanel.this.getJCheckBoxEnable()) 
				connEtoC5(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == ExclusionTimingEditorPanel.this.getCycleTimeJTextField()) 
				connEtoC1(e);
			if (e.getSource() == ExclusionTimingEditorPanel.this.getJTextFieldOffset()) 
				connEtoC2(e);
			if (e.getSource() == ExclusionTimingEditorPanel.this.getJTextFieldTransmitTime()) 
				connEtoC3(e);
			if (e.getSource() == ExclusionTimingEditorPanel.this.getJTextFieldMaxTransmit()) 
				connEtoC4(e);
		};
	};
/**
 * LMIExclusionEditorPanel constructor comment.
 */
public ExclusionTimingEditorPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (CycleTimeJTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMIExclusionEditorPanel.fireInputUpdate()V)
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
 * connEtoC2:  (JTextFieldOffset.caret.caretUpdate(javax.swing.event.CaretEvent) --> ExclusionTimingEditorPanel.fireInputUpdate()V)
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
 * connEtoC3:  (JTextFieldTransmitTime.caret.caretUpdate(javax.swing.event.CaretEvent) --> ExclusionTimingEditorPanel.fireInputUpdate()V)
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
 * connEtoC4:  (JTextFieldMaxTransmit.caret.caretUpdate(javax.swing.event.CaretEvent) --> ExclusionTimingEditorPanel.fireInputUpdate()V)
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
 * connEtoC5:  (JCheckBoxEnable.action.actionPerformed(java.awt.event.ActionEvent) --> ExclusionTimingEditorPanel.jCheckBoxEnable_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxEnable_ActionPerformed(arg1);
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
public void cycleTimeJTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G46E22AB0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E15DDC8BF8D455352695FB39D75A52EBDAD3AFFE0D4835711ADADAD30BD73EDBDAE9BF2C3402C28D125AD881D1A3D2C9B51574F6E712CC5E8FC80004A4B0F98708F9C002E908C2C8F804069ACD0A31C5E5481CE486A6B3634C8919C02AF72D3D4F5E674C64CC9E147A35170F45BE7B5FFB2D3D765AEB3F56B983495B931294EF48A3A4BE09A8FFDE0FA7E4561F8939F5CEB491FFFE6AB5924D1F92202DE4D1
	DDA224B9D03620EA5418C3029BD3A0DF8E790E62D163CEF84EA537B5663D858F815A097EA3A1D1B77C20E362F69E7F043713CC7389E99910E681D5835685948C14C38A5372955C8579AC6599A95EC0C85FA3D8A729A2DF03C7AA75EA206CA8D077F22CF3A82DC8A98F81ECA1D0866026652CEEB32429D43B9BDB1A15FA673E94CAE2FE3C5B1009EDE964F38AD6F897A929095CC98D11C0323737C35A5AFDE9704DFE67D0ED3F5961F0BAE4295FDAEB39A0D9EC3253DD2BA49669322DDFEAE969BA53F95C5BEF9F76
	581C0E6334285B6C106C24E6856A3E3FA2054AA5A4865ADAC831BBAA50DEAEF81E859432026B3FA233D551AF68D2A8093AE3EBC07D63D6A8FABF2A6A3F3227AEE17923A87FC3BA9E48F79E7241C02CEFB517CFCC33EF22E33D36A11BE3D05BBD25FFDFCF637D4BFB0C776F11BD58FE9764ADC0E3827F0A8971A0480781ADF90C777B0DAA5E6F3E2B5FA531975A8C59817A9D79986BEF62CA355F5E5C2B06826A077D7168CBC7214C8264851A8B3488A89228107A7FEE328F52368BF23BF3E84869E8F3390E59AD6E
	B3CE0BE407671696A8D130F724FE275BC28873717A0A9A9DFDB8452C7475EFA45637F789774930F76EA1327C3AA1C927EF612BCC0614D5CCC651AA35EF72AA345B3B15E837D6F8C6BE1C478C3FC471059A1C0D7FDD543FB03F894A42FE466D5C1745F569CA1CC7F29A5A8D49BA3A847F2059E0FD1C69B063F1D597E663B7925046E100AF850A820A854A0F709A1BA3380D2D6460E4B66E843FF3BB4C761E618BAE33DBBEB6EC33DBA437E2EBA9EDA2DB6B916373CC1B0F59A57420F60CA26FBD26EB174850AAC3EF
	843307BDC2358BEBBBACE05B33A3467BC6169843F6BAA7C2A25898A6887C8C4597E8F0B6362B685C427CE2A8ABDF6DB5327AE9A5084FDA4D6A278BB979948F5560CC4EDCAA9F73CB212C66892F110D6F1AC4DCEB67C0BE8CE889D0A4D09CD07E9A2837060F713D24EE0A7328DBB33E0C579E3F83D249AB3B4D1D8E0FECF674CBE74DEE1B7902DD7290254EB0356B859F3EABF4FE77C39BA7A50764B64B30B001F47310844E64E1F5EE085F0AF55C92AD05E5CB028606DC044B3DA9BE97474F6C117F643218E5C92D
	414AEF0EC71F38ECBE8B92C1GBC0F2DC1FB1D246BF897BCDB57F0DB259891DB8C7988E117AC43B6C87FG4EAD59DBDABAEC7D280659BD4AFD747DCA7413E828BFB79276301FFB0D21C075CF42BA85F46A97DE63F2205EA830BB1065A9BEF7B776F19F6D487C8629D955266B23C8F93FD47650DF7A7B68C7346DDE40E520A020F02015C0B1C0F9CFFB0D0DC03DC0B2D05053FCFF7FE64502A94EA76C8B1DABD17ABECF775CA755B2534F55E76CEB3DD20676192CAD34915A906D2545CFA9F9267B13D356BDD5C937
	CFD0A7D3731C2CF95E294C25ACE5EECE56967AE092554F974FD6D266DF1713F10E0643B8AC875A74B47731CE03094D3DE3E60F44E75BDBEE18A812C575B8D677C4824A14AECB8E39C3F2C98E8BBCF894DFBF13102A4E43812718A3BD9447F5399DA60AC7606FA5ECE5B848504EEADEEE2665E6D9F65BAE8C4BD20FA44BF0E8D079EDD4970F4F126FD17A6ACD413E262C05B981D43F1677752504DC4DFC6AF2BAAF8D3BFAF125D0666503747C5181754F2D657E5AE94273698C116F21F935FC4D1E3EA97A44D4895B
	725D0BE22EFB9BDA3FE2ED641440811E707DA1C941D3C579EEDA9E863A2D840A83AADF876773F55C8E73CCFD81782F073E2C0D87E89B7E72532FD39B4CC756A586767168C4FD9FFFA9D15F47E393A776E1122467432CACB9897B689CF1E866C4C6926E877DCE07C32293D33090F6A65F580B7D30B9EC723BB64B20A477582EC8044CDD073E7D815DF3E4F81E350E6F83A3E5FC9FA83FF297D97CE1116EF9B278BF8F9B2256B11F0CD9276E83CC27CCA34EB549AE8D01215A1D438E196139F4BFD3F0CF8245B63EB5
	9FF92B5BC77B6DF433BC5D8B46BCE1136C161AFED88964DA7AAF5BCC2BAF1AAF1B3D351E91B0D9AD9D0F5AF6ACBD0E0FFBCD2F3D7626A940ED2926FF3C7C8F5BB5123BA692FDF86F593D75CC5F3753AFFFAA7A8E585CD22743B54CA521864C4FF3C9C96B58F555215B6D74427DCB9E1D32A6CC0F457123D48F3579378686BC127C69A851C038BDB20BBE71532357EDF6F8C0989A6753516600224BD35BBE7653650C594B5579F4D4B9486DD254BAFADD7DA470EDBD003BAAFA2CCA38FC7D7CE4FACAB4F09D62EA46
	2E370FCCCF1383CC0FC7AEDE3D4E7EB1BDB58EAA7658F20C2D3B275B2DD27F25E3CE6FF1871EFAEA4CF49729E5C937649EF03A07A48BD52356BCA2ABBA34516229A9F15137058F5B7CC4F785160D6BEEA3BB3F5D1D0AFBCA5EFA2F319A6868FAFEFE7BA23DAB71731BF2C8EDF75A6DECBB0432796BF12F8B2B4C536CDFFD6BD99CB1CB14C7CCD21ED8B5F1F97DA46567A6A91FB5C9F978A465D11314E7D4CFDC5EBCC939A5E0B99B0731649CBC37C89F8DCBF05D46017E03B44A6F4C495B7C4A4E1A6D436ADD7C26
	E4BC2F59B5E7EF06FF1C22FF0E181902676DCBC27E2C948CA2DFD6E432F34F82B34AF439EDC3F0D5414B04928FFEA2CD9B8F0E06FEE06C38669970A9A0B6B71E58D18C69046BAE72BE0372FACAF86C388F72AE20B94FB0194B1ED16E1A345E0ED2DE2FF90357588D645A20D7EF3528978665CB82567BFC992F37964A638256EBAB67750A1E057BB7D06F332CDE50332CDE5433D8EFD844116B84B61BE22B61D0FDE3A20FE51D49514640AD1EC65D73A1BDBFBE27444F1F730F71C551BBD59F60566778BCB624EA6F
	E1ED9E0F335FC6C3AC0AAFFCABD5FBF6E458FD29E91042A26711BB61D06E657E34A895F545980C82ABBEF1777EB7487818929BD5FE4DED9F21ABCF2F335DE911CE01CB360B2375D91BB4C2389D7EFAC29BCBDF143D57109E408EE3CA7F67EA6C501AFB5540F47AEA8B46B275E2667DD4495A41E1DB6D30EC43E00FD9B65355DC9B7F6FC8C73BE05B114F71B87855AA3AD6BE8FFE8D548144E2F2E3B4D6076D1E83AC88084D6D901F393D6CF9F6570DF91E4F0727CB50A72639B7A9B6DF32F79B9BA33F0D085BF1DE
	6ED15E6EBD73C86BA7E93A3179D959F90614E8E63F7AE8550EE40B4D1087447AF7241477EF8C6A04C7737E5DD82C573F68E85E3FBBCB2F25FF2CCDC399EF133C60B5B63F405B9C2B50EB5375829F0B7B845FCC4096893E47CAF447C270DDAC67FCA99BE14D5A487962E4AD1FF7A3638B59487B38227CG24535F5D795C492DFB93523FEDAF976FAC8EE8571045D155815710700DEC2C17EE74DFC316E4205F2F85BCF6A3DFC3DECF5B2AD9C35E01D9EBF68C42EEA26E71E9E88B58B7147C33E958AE5DB7880F0945
	E7219DB3DE04F38650D120BEA06F0B3C8DD77A246F9E0EFBA7F87750194E637841AF32BE053D2846F6964373CA208D9A0C4559CFD66338473D48626C0582FF1F622D1BD41C7949E7689A0079B1A86B7B950F49FC36447FBDC263CB3E714DEE3FBC631BB30E2F56AFAF7B71B13F2928AE626B1BE6BA3534E82EA2F44DFA3C9A47937DE376CBBC7E7DEEB672AE01FC94D0ACD05EE618E3C03DC05E4DFCCC3E172E372E7B0EC920C5DD0DB132FE9EAF3DB67B740C637337CF70E6DFBE566FC5453C5F8BB673B1CAA93B
	B69DF60C637357E1032E8E77D6F09DC88C57E151B56A7063C9F5E80E5153A12D146B50ADF4F015DF1B8E9F0F635B6D171F9523672BEF1EAD5477D53E6AA93A9E9C66FBEEC88C7755DBF2900B007C06986E17C599984F676BD13753661089DF0B9E4A40B32B53ED9B34B9B876D30A75BB5DB4404730542C4031471CAC7D58630EAC7D58E3D156447175722CC031C9968BBFCF4F9A31503F3CDFFB0D8C7B8A751DEA48378A4CD30558F948FB8596C46777DC48078BECC6A1E2CBA19F75EBBE66E72A9B518DA11F7732
	57D88FF48A488AB493A894E8A9D0B4D04AABDEE3B950D120F32FF07EFE4A0F67F0AFE041C08BDFE167EDBCCBC73C424E4A3110664742DA8254974B4E450C7FDF2A16C30A727C7722C0047A7167FA65B9450FFF694B016565A97D19288D34872656896A4D5414211D38ED54F971BF34EF05D36CD796710D557377D17E6F2F7C5FB1794F2B5CC95A606F9DA60A59EB5F0B61FC4077E185CA3E184AF8E8C7B675ED0FC77250459ECF4BCA59B372F62293A37F23F9C8A2BCBEF4FFC92AD267386CECEF90B60B2C66EB09
	B7D6356FEC65B9845270D85FB1D15ED5E5871E3756EC7DF96B4846FEFA1C436EFE49FFEE8EE7670BB2F5776A1425A16C06F2DF267C3259BDA80D1B5BB7D2FED696087F8B590166BF3351D82956C687040D3EDF4CED34D4E30BC865F99324093F71359113F724CCAB6F201037D0FC8B132F612BD11E0FC25A2BABEF330F3CC6A1AFDB6C4DD68D5F1872BC8752793A723EDE2E1557A464B58FF3F98BB5FC4B1467A8C8E300D2DED54B18FD676660994DAB72A1B4BFAA5A9BC94771F3392581496D16AC6CD58D69FD95
	574AB9F46DAD02676AD779197E9B9538DF584D234EE158D89237E03D52CA9E5F8806FA8B00A20022DFE57781364FFDEB84675A005B0C213A9E1754CF22A86F3FD5F15EAE481F871A81948234F88BEF736EEA6485C65B507090551069B73A176E83F1301646713A3794523AE62FDF5DEF52F57FBC54330B3A5FEF45F5C2A7D22D58262EA45B273C4BFC816E402C6C74881EF5D9CC1995B0BC75F53CFBEA633C8A1B42F7F59F462C7C23522C2C38F107CF19D68A679F3662FCE1B2B5D1DCB11E43395BFDE46855F9A4
	B3D315E1F34034D3675B5562ACD156A3019FDABCA21E6D9D9ED7F60017A58F0EAB936F84B2289D7D223ECA0C6FA71FE86F1041F14CE7964571BB66FDCDFA654BC5395B26D73ED61437976915470A725F646B15E77C1617976D52ABB70972F9C7744A9BC57977F475BB75DBFE9E59CC4FA867A06F95185C0C58EC484F95586AE6FE3E899558871974EC84791C7F65D8C4B117D7AE304F5073D2B364CF89ECFD851757AB306CE2AEEF3E409E296072968AAC2C8831E5100F945856E2AEEF03405E2E60721AFF47314A
	D2AE2FCBE04F65D3DE483B8476D68117B7A63024D2AEEF19DA0F1E0B23A09FA7307F30A0866B03518444B0A2F1F9B5822BE9C5EC8C72C182FB0A0EC7A86497832D84A2E486EE7D0AAF5FD4E10254A10D18C4582183262783E335550D8D75B52603076A773218C34808B666501CD69BB066109FBF4A3EEFD25272449265CEFD3A12FF77EA013239C0ECBDEDB86FF347C5C5DADACE3AED16E36641AE3ABA33FB433FD726A87CAB0117456A96D3199906D1E3B9D0AB905357DB49630BE1F74EA3DDB30E69FE2B9B553B
	5750EBE0FAFA1594E3A433F32F9AD81B8B2AF81B61D09EED406F361F74F9D70036DCE1E077B4D61E63938F44B58E3F4BD64B0F7914233ECD895AF259279CED34DF61E7FDFB380277AD6E6185447E1D7A005FA1B7A630BE75A96942C7CD447A2D0B86668BA1852AAF8C97CE4597985F033BD43E216DD361E3633CF9A01F685E8F69CA4B6C1DC8D2941B4D811D9976C293E2386E46886CAE9B477A92B9F62508E30B84D6CF679E1EBBCCC99C6B594531BE012DBE4231B982FB5946312500317E266D8DFC46A823FBB8
	B10E9AB30C3C7ECD4D782EC506AD0C59EEB39EDD085D08633C2939C3E74C34631E2458992999F83A0036AB78F9F80EA76A77C1B006197AE70B4F5335B59C6AAF86E2D87CEE4422A19FABF48D2E7055153E46D1F47DED45E43AA6EBF4ADCF765535B9D95515355FCC57588E403B1339CE31F40D9D037C2CE42E5303C53EBA7DFE372AD3F571E4BA25EAF48A7753E969B81D2E5076D7829EA5F4FA0B2E5399A923467C942ED3FC012FCE677628BA8D94CC26D33AC6270E94DF1D7AD27CF52A256DDBGF70976870683
	7B60A7F48F198BF5C3C57DEFDBFC75DDEDD2755DA9CD24AF57137BE117468769BA1C626F036D2D7AE7563F507DA98A6AC78B3DE2831C05F368BC4ACF05B5BD152F670F7CC53B7F645C1AEF606D7A6B5C1D4A563AF3296AFA4EE2CC8F17205CB900078131BB448FF7625AFA56FCF17C5BACG3B5DCE176416C799CA5F3AAAF7186E4AEB7B867E03EB643BE55AFCAC3E5DC863584BD255F1E2789B940F5060EC0F7E11582357C2592934D1A5BE97D7CAFFD7G7925C0D1C031C07969234646F4EE4B2CFC7CE6E542B8
	F22F53558517FA3BA6FC467A771D2AEB7BED40771379DE987E5F7FC596FEEED3FC207708275219BFD81534FB0509622CFFDFAC477E85C3D9B8D08450A620440CD1E3D1866F5F597C9A56BFF407B6D9B677DB695BD45EB9FC412942F8B1E4FDCBA5FA5F1A56E7B0BDBA1434CFC9B959A1BF438F8B02FCA850A28D4E5AF829C92F0D65CA3D35FEF2B845821E1169DBE602FCA35029CC7FB66E316935F1CE29674A54EFE3A66041FEE5E110DF8C34F2DC9B1F94693531C1299797200D1CACD8AB32FC4B1AA15F85F4BE
	4B3F0D26822D2F49CA79ACA51D1BB53E8D465737CB4B37C02937C4C9A38272BDF9C44B972D54A359AC4D4B8E44374526652BD66A35AAE9378E9FDB5F2BABD1BCDBE3ED4D38E186B81F3D5ED0E3BAD03F1710BD0445996979057E59F4E4BC46651B2B70194D3F27B2787CAB3A7B8E5271782678B222BF7FAC0A3E61596A3A9CF736AE0149FC3ADA55797447D3511971997729FC17AB2E352FCC56BD7B55BE2E28D165FEB8A5390C2F39D6653B3D68EF5367AFF52ABE9F572BF2AB27A417715D5E20727DA27FEF53E7
	4981D51FE78E2AF2A327A4177125B529FCF3ACFB8273B5B49FG4683FB953E578E29EDF73F2E4A78D97554E5FC2DD9657B761E2970313D216B774847760062128A92603CGBB40E9107556050BD23F5CEB9E94BC4BCB505FC5DC189DA314324769778A78FD394DEC37DD211F6C74C0C547A0DB83DA68DCE34F6D1A67E515577B778B63571FF5FF57B6D87F134A824779B34A74637C5B4A745F4F6517CD7C5BGD3D9605F86D415E9FF9B40467D3D969C1BE8D82FD2F260CE88F49428B7071F873ED3D6CA68F9C067
	ACF05CB2086F997057F3B2EFBF38BCF0DF3FD6BE79EF6EC24B71FB2D8BCED9F68E21FC0E5FD30E63A5BBDD94D4FE7FC7E36FEE5B20D5E6D5957C7EF21C57F6E90042ACCE341F4EF59774EBC68E0F9D4DA17C5D62EC017D6BBE44C2A03FC4E0BF27D88464E384762BFA44D2364279F0AB475EB8A8E2D682EB23777AB14887896CED0A05C2FE2940728EA1968979E8010D9E627A458A6CDE21CB46B60E9514F1DDCC828B32F3DD9A8596F9096BB2D3E0FBEC63F5D9CE31E02893AA6AB5DD643A048BEC27156B32D4E0
	EB5E643AC48A6C06A3DC17CD823B2D126BF2B417E3270B476BF2BB0DF774C29D0B28E72475DC101FA5304F51BB64DC48AF9458D379DC17E5825B534EF50992D8970D1F546741F9B00FE341A563F5E175D05EB9D1EFBE1567027C8C0125F670B6E68B4C39CB58C5E069D64E3BC9E0F6AB670D93582F0FF05E2E6D9C3B4546F94F8BAC5646F96D825BF61173AE9258DDE31CF73940527E4AF9A337737B50F78F633D39E6875885488EB493A898E8815092208820E820441DF007G2A876AG6A8332820D814D594957
	12BB2BE00B8CF88FD50221F4C1F11B355FC118DBA6F8CF5AB2791A72D18B7DAE9456025ED117729E73F28B7D36023E4D61752E3664882C53536D7458F027E2FB69B1629BDF3899BFA7D26E727753F5E4BE74F341CEFE6F8F5907FAD91DEE5B9527C3B65B71858F1748799E20F3995764C8419733AFF05CA3F3BF9D4B7CD1E3DEBE2F1F5200FA8DC09B2C4E6E862CF3944A3B00D87C237760E4718FED8C4EBF7EB1166F9F7F68EB520FE938683AB68B6A87897D2CCD39BEF6E8335B869D380BA8BC1FED625F74AC96
	BC7B8F795A7A3EB7D5DB4F23EBE2B4540D4D67712BC8EA477282703F820E9D23EB62B948DB8B389D6E36CFE60764896C90DC609F37CA22EB6CFC409794F05D1F31796A3E6E082A7B87F4AEC6C25D8DC24F23F43E676D0235FB97479269FC6F02FC5FAE2E7B2DC7A653BDF5825DE76D72577DC7F47D8D82BCE49757FD21DF9C129EA514B1AFAE560FF9B55135F899480890F2EEAB8D6C437751F5BA31907AD4487B1BC65755D6481FAA647DBD54BED9FF53A7682F37505FE77B8B74FD76AAEDFBA6541FA374992658
	A248AFAD64FDFA205D57B6A7F029D0620B260E09F4CD1EE4FE45C29B7BFD46A6186E9509C5A03B083F5332EDD55B3B41BAD9FB93797151A27F761EFF1B7A9B60FD223D955B547632AFCE565E043EB72E3D43F49E84819EA25A0B49D55BAB796BE46DCDB4764BB56D31F57D614301770FC78FCF3EFF2CB92CFFA6FD6A7078B3697A437AE75268435AB3A943FEF5987DE290763DBED16FE5013DA730ADD4A6E26F5AAC32D560712DE98AFECA4286B91E5C0AFA0DD02C0F5FB3B458FB8A36359577C068B2AC4F623B40
	D63A1F586CF6165FC56B589C30A58AFB6CE9C59DEDAE33C66FF201215EECCCF76210A8736F63FD935DC59BDF2FB1B5B46ED56ABECA6BEA3F2C6F41FF79D543D35BF57CC42F52865D7E953E7575D3E90371C5942AFC8D87CB7D79CE40E6D5DDEA017B2AF3303446D48F2C87447DFE8B3E4AD51EF76FD6651CE81AFA9F3F5AE41A380F6FF41EBC554B5AE85B233691BAA5BBB2BE0BC9654BAA1BBA5F71B215EF16FDFA3631162B363929C21513E71BFA7B77E8E2FB995629732D28D279163FB9F53E57B4713511A353
	6BEF37A61E665144530E94CF3D7D2F3C2672DDAD1DBA5F7D1AB85C4F72274E972209176536CF2F3F8FEA62E36BB571314F17CC3D7D13ADAADF70B45B3FF8D8ED7F49B7D4B93F9B1C1E1C78A32A1C775BD4B9B75926A7E746B1D58EB92E4A39EDECFAF24A703FC5A36C7F47FBFA7F873030073DC012BCEE38400D33E75B97D6BF9E76E1C71E0198BA0951CA5774CE12B4021B035785FD03ADA27ED64FC784FFE62E1B032DC2733700FD61257EC0574443CA1C0BE01068A4E813AD346924DBC2546503AD89CF5F11E0
	A009208A5BA2BBC9A26EF320111BE916GDBA46AC5BFF94023A1EEB1D9FE324767527CB8367E1DB86C9A0EED81B8F01739CFF0E4AA88462B73F46A081CCC52696708A67CA430C5A6496C73C1F84A66DF7931A213CCF24F183D26F1E8A29CD9EBA171C87DFE09E8898FC5B77B655CA453476FFEF8FB537AE5CF1E75A490E3A7A9406BE20F642651D84902DFC1AB7F555489126A421FA06081990651C313A40FBCEA173AC85E40BB7873DC0FACD95AF87C51A3136D7487DDF5787B433AD3E60FD5C6DFBA5CC5929D78
	1FE211ED2C58EEF68C5631206FE16A8B71EDC7293E78AA16003E39D4DF14D2AB2464BE10DDD1F2FA6D4284E2B8C1526CCE33650439DFF63ABBC92AECF5CB9E2B53EE713A0EB0C1A9A3F84222A7C390D7A83A0F1D2D456B37D07A12773659199BB2CF98602260A6F534A61E6CEBC58DCB3B556C0626A4F7270C7F491753DDAE4ABE33DB2E7C49491D678CA4DE69A41A3E526B1209F140BCE4330F42C3871D65F4D0764920AD8EAB7E7E8DB4B379E9460BC48335B7BFD47F653BDE7DB3EDC0E515499EE6CAE02D9B14
	1CC3F5ED8383B607A48400DDC1FC31900FF5EA7D6AEC73FEE97B69D53F3BGFE50C192066A6B693F8D745F8378EF83C59AA852F0G0C3B09CC78473F5C4B7BCC1DC72C98B87C35A7E08465A7DEF8E85E1A56CD51503B93A4097E308E86A1C72D754761218B127BF3E1DFAECE8F0A3A894748052E62C53935DEB187F1FEA6A97E33F54239B0BED7BE4E7F902D9E6FC88857086144DC9D2D5AC07FED1C36F1106B9065ED96FDEA536FD35EBFF31F7E707F30CFFFD27B641A24CF7FD0FA3F6FBB96384B8D91BEE77E21
	B43568EB1A770F276925FF9ACD5D3EDEABB4655FB66C7DC47B4DD96C0D59813FF90E29E25FB424D47BFFF3F6AFFD9756817879EA9E6BCF2847CBCD20F748BDC3E63BDDB65BAF35B9AC78BE7958A87BCD8363DDD80F72BD3C0ED2287C0E379E753E286179BFD0CB87888E2B2962DB9CGG30DAGGD0CB818294G94G88G88G46E22AB08E2B2962DB9CGG30DAGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG159CGGGG
**end of data**/
}
/**
 * Return the CycleTimeJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCycleTimeJLabel() {
	if (ivjCycleTimeJLabel == null) {
		try {
			ivjCycleTimeJLabel = new javax.swing.JLabel();
			ivjCycleTimeJLabel.setName("CycleTimeJLabel");
			ivjCycleTimeJLabel.setText("Cycle Time:");
			ivjCycleTimeJLabel.setMaximumSize(new java.awt.Dimension(147, 14));
			ivjCycleTimeJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjCycleTimeJLabel.setPreferredSize(new java.awt.Dimension(147, 14));
			ivjCycleTimeJLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjCycleTimeJLabel.setMinimumSize(new java.awt.Dimension(147, 14));
			ivjCycleTimeJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCycleTimeJLabel;
}
/**
 * Return the CycleTimeJTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getCycleTimeJTextField() {
	if (ivjCycleTimeJTextField == null) {
		try {
			ivjCycleTimeJTextField = new javax.swing.JTextField();
			ivjCycleTimeJTextField.setName("CycleTimeJTextField");
			ivjCycleTimeJTextField.setPreferredSize(new java.awt.Dimension(71, 20));
			ivjCycleTimeJTextField.setText("");
			ivjCycleTimeJTextField.setMinimumSize(new java.awt.Dimension(71, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCycleTimeJTextField;
}
/**
 * Return the JCheckBoxEnable property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxEnable() {
	if (ivjJCheckBoxEnable == null) {
		try {
			ivjJCheckBoxEnable = new javax.swing.JCheckBox();
			ivjJCheckBoxEnable.setName("JCheckBoxEnable");
			ivjJCheckBoxEnable.setPreferredSize(new java.awt.Dimension(182, 22));
			ivjJCheckBoxEnable.setText("Enable Exclusion Settings");
			ivjJCheckBoxEnable.setMaximumSize(new java.awt.Dimension(182, 22));
			ivjJCheckBoxEnable.setMinimumSize(new java.awt.Dimension(182, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxEnable;
}
/**
 * Return the JLabelMaxTransmit property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMaxTransmit() {
	if (ivjJLabelMaxTransmit == null) {
		try {
			ivjJLabelMaxTransmit = new javax.swing.JLabel();
			ivjJLabelMaxTransmit.setName("JLabelMaxTransmit");
			ivjJLabelMaxTransmit.setText("Max Transmit Time: ");
			ivjJLabelMaxTransmit.setMaximumSize(new java.awt.Dimension(147, 14));
			ivjJLabelMaxTransmit.setPreferredSize(new java.awt.Dimension(147, 14));
			ivjJLabelMaxTransmit.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelMaxTransmit.setMinimumSize(new java.awt.Dimension(147, 14));
			ivjJLabelMaxTransmit.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMaxTransmit;
}
/**
 * Return the JLabelMinutes property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMinutes() {
	if (ivjJLabelMinutes == null) {
		try {
			ivjJLabelMinutes = new javax.swing.JLabel();
			ivjJLabelMinutes.setName("JLabelMinutes");
			ivjJLabelMinutes.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelMinutes.setText("min.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMinutes;
}
/**
 * Return the JLabelSeconds1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSeconds1() {
	if (ivjJLabelSeconds1 == null) {
		try {
			ivjJLabelSeconds1 = new javax.swing.JLabel();
			ivjJLabelSeconds1.setName("JLabelSeconds1");
			ivjJLabelSeconds1.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelSeconds1.setText("sec.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSeconds1;
}
/**
 * Return the JLabelSeconds2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSeconds2() {
	if (ivjJLabelSeconds2 == null) {
		try {
			ivjJLabelSeconds2 = new javax.swing.JLabel();
			ivjJLabelSeconds2.setName("JLabelSeconds2");
			ivjJLabelSeconds2.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelSeconds2.setText("sec.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSeconds2;
}
/**
 * Return the JLabelSeconds3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSeconds3() {
	if (ivjJLabelSeconds3 == null) {
		try {
			ivjJLabelSeconds3 = new javax.swing.JLabel();
			ivjJLabelSeconds3.setName("JLabelSeconds3");
			ivjJLabelSeconds3.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelSeconds3.setText("sec.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSeconds3;
}
/**
 * Return the JTextFieldMaxTransmit property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldMaxTransmit() {
	if (ivjJTextFieldMaxTransmit == null) {
		try {
			ivjJTextFieldMaxTransmit = new javax.swing.JTextField();
			ivjJTextFieldMaxTransmit.setName("JTextFieldMaxTransmit");
			ivjJTextFieldMaxTransmit.setPreferredSize(new java.awt.Dimension(71, 20));
			ivjJTextFieldMaxTransmit.setText("");
			ivjJTextFieldMaxTransmit.setMinimumSize(new java.awt.Dimension(71, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldMaxTransmit;
}
/**
 * Return the JTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldOffset() {
	if (ivjJTextFieldOffset == null) {
		try {
			ivjJTextFieldOffset = new javax.swing.JTextField();
			ivjJTextFieldOffset.setName("JTextFieldOffset");
			ivjJTextFieldOffset.setPreferredSize(new java.awt.Dimension(71, 20));
			ivjJTextFieldOffset.setText("");
			ivjJTextFieldOffset.setMinimumSize(new java.awt.Dimension(71, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldOffset;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldTransmitTime() {
	if (ivjJTextFieldTransmitTime == null) {
		try {
			ivjJTextFieldTransmitTime = new javax.swing.JTextField();
			ivjJTextFieldTransmitTime.setName("JTextFieldTransmitTime");
			ivjJTextFieldTransmitTime.setPreferredSize(new java.awt.Dimension(71, 20));
			ivjJTextFieldTransmitTime.setText("");
			ivjJTextFieldTransmitTime.setMinimumSize(new java.awt.Dimension(71, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldTransmitTime;
}
/**
 * Return the OffsetJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOffsetJLabel() {
	if (ivjOffsetJLabel == null) {
		try {
			ivjOffsetJLabel = new javax.swing.JLabel();
			ivjOffsetJLabel.setName("OffsetJLabel");
			ivjOffsetJLabel.setText("Offset:");
			ivjOffsetJLabel.setMaximumSize(new java.awt.Dimension(147, 14));
			ivjOffsetJLabel.setPreferredSize(new java.awt.Dimension(147, 14));
			ivjOffsetJLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjOffsetJLabel.setMinimumSize(new java.awt.Dimension(147, 14));
			ivjOffsetJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOffsetJLabel;
}
/**
 * Return the ImpliedDurationJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTransmitTimeJLabel() {
	if (ivjTransmitTimeJLabel == null) {
		try {
			ivjTransmitTimeJLabel = new javax.swing.JLabel();
			ivjTransmitTimeJLabel.setName("TransmitTimeJLabel");
			ivjTransmitTimeJLabel.setText("Transmit Time: ");
			ivjTransmitTimeJLabel.setMaximumSize(new java.awt.Dimension(147, 14));
			ivjTransmitTimeJLabel.setPreferredSize(new java.awt.Dimension(147, 14));
			ivjTransmitTimeJLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjTransmitTimeJLabel.setMinimumSize(new java.awt.Dimension(147, 14));
			ivjTransmitTimeJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTransmitTimeJLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
	
	YukonPAObject pao = (YukonPAObject)o;

	//if it isn't enabled, forget it!
	if(getJCheckBoxEnable().isSelected())
	{
		//Add new exclusion timing information to the assigned PAOExclusion Vector
		Integer paoID = pao.getPAObjectID();
		Integer functionID = CtiUtilities.EXCLUSION_TIMING_FUNC_ID;
		String funcName = CtiUtilities.EXCLUSION_TIME_INFO;
	
		//CycleTime:#,Offset:#,TransmitTime:#,MaxTime:#
		StringBuffer exclusionTiming = new StringBuffer();
		exclusionTiming.append("CycleTime:");
		Integer cycleTime = new Integer(getCycleTimeJTextField().getText());
		cycleTime = new Integer(cycleTime.intValue() * 60);
		exclusionTiming.append(cycleTime);
		exclusionTiming.append(",Offset:" + getJTextFieldOffset().getText());
		exclusionTiming.append(",TransmitTime:" + getJTextFieldTransmitTime().getText());
		exclusionTiming.append(",MaxTime:" + getJTextFieldMaxTransmit().getText());
	
		System.out.println(cycleTime);
	
		PAOExclusion paoExcl = new PAOExclusion(
			paoID, functionID, funcName, exclusionTiming.toString()
			);
		
		pao.getPAOExclusionVector().add( paoExcl );

	}  
	return pao;
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
	getCycleTimeJTextField().addCaretListener(ivjEventHandler);
	getJTextFieldOffset().addCaretListener(ivjEventHandler);
	getJTextFieldTransmitTime().addCaretListener(ivjEventHandler);
	getJTextFieldMaxTransmit().addCaretListener(ivjEventHandler);
	getJCheckBoxEnable().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ProximityExclusionEditorPanel");
		setPreferredSize(new java.awt.Dimension(410, 360));
		setLayout(new java.awt.GridBagLayout());
		setSize(410, 360);
		setMinimumSize(new java.awt.Dimension(410, 360));
		setMaximumSize(new java.awt.Dimension(410, 360));

		java.awt.GridBagConstraints constraintsCycleTimeJTextField = new java.awt.GridBagConstraints();
		constraintsCycleTimeJTextField.gridx = 2; constraintsCycleTimeJTextField.gridy = 2;
		constraintsCycleTimeJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsCycleTimeJTextField.weightx = 1.0;
		constraintsCycleTimeJTextField.insets = new java.awt.Insets(14, 13, 17, 4);
		add(getCycleTimeJTextField(), constraintsCycleTimeJTextField);

		java.awt.GridBagConstraints constraintsCycleTimeJLabel = new java.awt.GridBagConstraints();
		constraintsCycleTimeJLabel.gridx = 1; constraintsCycleTimeJLabel.gridy = 2;
		constraintsCycleTimeJLabel.insets = new java.awt.Insets(17, 5, 20, 12);
		add(getCycleTimeJLabel(), constraintsCycleTimeJLabel);

		java.awt.GridBagConstraints constraintsOffsetJLabel = new java.awt.GridBagConstraints();
		constraintsOffsetJLabel.gridx = 1; constraintsOffsetJLabel.gridy = 3;
		constraintsOffsetJLabel.insets = new java.awt.Insets(20, 5, 20, 12);
		add(getOffsetJLabel(), constraintsOffsetJLabel);

		java.awt.GridBagConstraints constraintsTransmitTimeJLabel = new java.awt.GridBagConstraints();
		constraintsTransmitTimeJLabel.gridx = 1; constraintsTransmitTimeJLabel.gridy = 4;
		constraintsTransmitTimeJLabel.insets = new java.awt.Insets(20, 5, 20, 12);
		add(getTransmitTimeJLabel(), constraintsTransmitTimeJLabel);

		java.awt.GridBagConstraints constraintsJTextFieldOffset = new java.awt.GridBagConstraints();
		constraintsJTextFieldOffset.gridx = 2; constraintsJTextFieldOffset.gridy = 3;
		constraintsJTextFieldOffset.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldOffset.weightx = 1.0;
		constraintsJTextFieldOffset.insets = new java.awt.Insets(17, 13, 17, 4);
		add(getJTextFieldOffset(), constraintsJTextFieldOffset);

		java.awt.GridBagConstraints constraintsJTextFieldTransmitTime = new java.awt.GridBagConstraints();
		constraintsJTextFieldTransmitTime.gridx = 2; constraintsJTextFieldTransmitTime.gridy = 4;
		constraintsJTextFieldTransmitTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldTransmitTime.weightx = 1.0;
		constraintsJTextFieldTransmitTime.insets = new java.awt.Insets(17, 13, 17, 4);
		add(getJTextFieldTransmitTime(), constraintsJTextFieldTransmitTime);

		java.awt.GridBagConstraints constraintsJLabelMaxTransmit = new java.awt.GridBagConstraints();
		constraintsJLabelMaxTransmit.gridx = 1; constraintsJLabelMaxTransmit.gridy = 5;
		constraintsJLabelMaxTransmit.insets = new java.awt.Insets(20, 5, 112, 12);
		add(getJLabelMaxTransmit(), constraintsJLabelMaxTransmit);

		java.awt.GridBagConstraints constraintsJTextFieldMaxTransmit = new java.awt.GridBagConstraints();
		constraintsJTextFieldMaxTransmit.gridx = 2; constraintsJTextFieldMaxTransmit.gridy = 5;
		constraintsJTextFieldMaxTransmit.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldMaxTransmit.weightx = 1.0;
		constraintsJTextFieldMaxTransmit.insets = new java.awt.Insets(18, 13, 108, 4);
		add(getJTextFieldMaxTransmit(), constraintsJTextFieldMaxTransmit);

		java.awt.GridBagConstraints constraintsJLabelMinutes = new java.awt.GridBagConstraints();
		constraintsJLabelMinutes.gridx = 3; constraintsJLabelMinutes.gridy = 2;
		constraintsJLabelMinutes.ipadx = 21;
		constraintsJLabelMinutes.insets = new java.awt.Insets(17, 4, 20, 109);
		add(getJLabelMinutes(), constraintsJLabelMinutes);

		java.awt.GridBagConstraints constraintsJLabelSeconds1 = new java.awt.GridBagConstraints();
		constraintsJLabelSeconds1.gridx = 3; constraintsJLabelSeconds1.gridy = 3;
		constraintsJLabelSeconds1.ipadx = 21;
		constraintsJLabelSeconds1.insets = new java.awt.Insets(20, 4, 20, 109);
		add(getJLabelSeconds1(), constraintsJLabelSeconds1);

		java.awt.GridBagConstraints constraintsJLabelSeconds2 = new java.awt.GridBagConstraints();
		constraintsJLabelSeconds2.gridx = 3; constraintsJLabelSeconds2.gridy = 4;
		constraintsJLabelSeconds2.ipadx = 21;
		constraintsJLabelSeconds2.insets = new java.awt.Insets(20, 4, 20, 109);
		add(getJLabelSeconds2(), constraintsJLabelSeconds2);

		java.awt.GridBagConstraints constraintsJLabelSeconds3 = new java.awt.GridBagConstraints();
		constraintsJLabelSeconds3.gridx = 3; constraintsJLabelSeconds3.gridy = 5;
		constraintsJLabelSeconds3.ipadx = 21;
		constraintsJLabelSeconds3.insets = new java.awt.Insets(20, 4, 112, 109);
		add(getJLabelSeconds3(), constraintsJLabelSeconds3);

		java.awt.GridBagConstraints constraintsJCheckBoxEnable = new java.awt.GridBagConstraints();
		constraintsJCheckBoxEnable.gridx = 1; constraintsJCheckBoxEnable.gridy = 1;
		constraintsJCheckBoxEnable.gridwidth = 2;
		constraintsJCheckBoxEnable.insets = new java.awt.Insets(20, 22, 13, 48);
		add(getJCheckBoxEnable(), constraintsJCheckBoxEnable);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getCycleTimeJTextField().setEnabled(false);
	getJTextFieldMaxTransmit().setEnabled(false);
	getJTextFieldTransmitTime().setEnabled(false);
	getJTextFieldOffset().setEnabled(false);
	// user code end
}
public boolean isInputValid() 
{
	if(getCycleTimeJTextField().getText().compareTo("") == 0 && getJCheckBoxEnable().isSelected())
	{
		setErrorString( "No values have been filled in." );
		return false;
	}
	
	if(getJCheckBoxEnable().isSelected())
	{
		
		Integer cycleTime = new Integer(getCycleTimeJTextField().getText());
		cycleTime = new Integer(cycleTime.intValue() * 60);
		Integer offset = new Integer(getJTextFieldOffset().getText());
		Integer transmitTime = new Integer(getJTextFieldTransmitTime().getText());
		Integer maxTime = new Integer(getJTextFieldMaxTransmit().getText());
	
		if(offset.compareTo(cycleTime) > 0 || transmitTime.compareTo(cycleTime) > 0 
			|| maxTime.compareTo(cycleTime) > 0 )
		{
			setErrorString( "Offset and transmit times cannot be greater than the cycle time." );
			return false;
		}
	
	}
	return true;
}
/**
 * Comment
 */
public void jCheckBoxEnable_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	if(getJCheckBoxEnable().isSelected())
	{
		getCycleTimeJTextField().setEnabled(true);
		getJTextFieldMaxTransmit().setEnabled(true);
		getJTextFieldTransmitTime().setEnabled(true);
		getJTextFieldOffset().setEnabled(true);
	}
	else
	{
		getCycleTimeJTextField().setEnabled(false);
		getJTextFieldMaxTransmit().setEnabled(false);
		getJTextFieldTransmitTime().setEnabled(false);
		getJTextFieldOffset().setEnabled(false);
		
		fireInputUpdate();
	}
		
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ExclusionTimingEditorPanel aExclusionTimingEditorPanel;
		aExclusionTimingEditorPanel = new ExclusionTimingEditorPanel();
		frame.setContentPane(aExclusionTimingEditorPanel);
		frame.setSize(aExclusionTimingEditorPanel.getSize());
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
	YukonPAObject pao = (YukonPAObject)o;

	Vector exclusionVector = pao.getPAOExclusionVector();
	PAOExclusion exclusionTimeInfo = null;
	   
	for(int j = 0; j < exclusionVector.size(); j++)
	{
		PAOExclusion temp = (PAOExclusion)exclusionVector.elementAt(j);
		if(temp.getFunctionID().compareTo(CtiUtilities.EXCLUSION_TIMING_FUNC_ID) == 0
			&& temp.getFuncName().compareTo(CtiUtilities.EXCLUSION_TIME_INFO) == 0)
			{
				exclusionTimeInfo = temp;
			}
	}
	
	if(exclusionTimeInfo != null)
	{
		getJCheckBoxEnable().doClick();
		
		//CycleTime:#,Offset:#,TransmitTime:#,MaxTime:#
		StringTokenizer timeInfo = new StringTokenizer(exclusionTimeInfo.getFuncParams());
		Integer cycleTime = new Integer(timeInfo.nextToken("CycleTime:,"));
		String offset = timeInfo.nextToken("Offset:,");
		String transmitTime = timeInfo.nextToken("TransmitTime:,");
		String maxTime = timeInfo.nextToken("MaxTime:,");
	
		cycleTime = new Integer(cycleTime.intValue() / 60);
		getCycleTimeJTextField().setText(cycleTime.toString());
		getJTextFieldOffset().setText(offset);
		getJTextFieldTransmitTime().setText(transmitTime);
		getJTextFieldMaxTransmit().setText(maxTime);
	
	}
}
}
