package com.cannontech.dbeditor.editor.point;

import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.common.util.CtiUtilities;

/**
 * This type was created in VisualAge.
 */

public class CalcStatusBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, java.awt.event.ItemListener {
	private javax.swing.JComboBox ivjInitialStateComboBox = null;
	private javax.swing.JLabel ivjInitialStateLabel = null;
	private javax.swing.JComboBox ivjStateTableComboBox = null;
	private javax.swing.JLabel ivjStateTableLabel = null;
	private java.util.List allStateGroups = null;
	private javax.swing.JCheckBox ivjArchiveCheckBox = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JComboBox ivjPeriodicRateComboBox = null;
	private javax.swing.JLabel ivjPeriodicRateLabel = null;
	private javax.swing.JComboBox ivjUpdateTypeComboBox = null;
	private javax.swing.JLabel ivjUpdateTypeLabel = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == CalcStatusBasePanel.this.getStateTableComboBox()) 
				connEtoC1(e);
			if (e.getSource() == CalcStatusBasePanel.this.getInitialStateComboBox()) 
				connEtoC3(e);
			if (e.getSource() == CalcStatusBasePanel.this.getArchiveCheckBox()) 
				connEtoC6(e);
			if (e.getSource() == CalcStatusBasePanel.this.getUpdateTypeComboBox()) 
				connEtoC2(e);
			if (e.getSource() == CalcStatusBasePanel.this.getPeriodicRateComboBox()) 
				connEtoC4(e);
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == CalcStatusBasePanel.this.getStateTableComboBox()) 
				connEtoC5(e);
		};
	};
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CalcStatusBasePanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getStateTableComboBox()) 
		connEtoC1(e);
	if (e.getSource() == getInitialStateComboBox()) 
		connEtoC3(e);
	if (e.getSource() == getArchiveCheckBox()) 
		connEtoC6(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (StateTableComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> StatusBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
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
 * connEtoC2:  (UpdateTypeComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> CalcStatusBasePanel.updateTypeComboBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateTypeComboBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (InitialStateComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> StatusBasePanel.fireInputUpdate()V)
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
 * connEtoC4:  (PeriodicRateComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> CalcStatusBasePanel.fireInputUpdate()V)
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
 * connEtoC5:  (StateTableComboBox.item.itemStateChanged(java.awt.event.ItemEvent) --> StatusBasePanel.stateTableComboBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.stateTableComboBox_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (ArchiveCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> StatusBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
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
 * Return the ArchiveCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getArchiveCheckBox() {
	if (ivjArchiveCheckBox == null) {
		try {
			ivjArchiveCheckBox = new javax.swing.JCheckBox();
			ivjArchiveCheckBox.setName("ArchiveCheckBox");
			ivjArchiveCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjArchiveCheckBox.setText("Archive Data");
			ivjArchiveCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			ivjArchiveCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjArchiveCheckBox;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GE00C1EB1GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DD4D45719A4A1292BB4A5DB131A54B444582EC9ECEBDA5B3829E71726E6D75369561EA5A7C9172634F13BF657241E2DEDED4BEE5E7023907F78B3B10DD62AB40506A0A0A4AEC1431FC69493A28808834C1B19878C4C4C9BE75E402898777B6E5FFBB33C81B436A71E73715E7D6E7DFE6FF77F3EF7C729E8EFD236B523C8122C59927B7716D51262CEC9D2695D777E18638E35DA2C12615F9FG7225
	7E4BD970BC8EF5E937DB2CF9D21FBB874AB6A83BBEE9313E8C6F0512431D5E8CAFB16424C05D094E4F35CDAD6715EEAEE7B3A95F5838951E5B816C000B87D85D0A726FEA5C45700B203C1D3DA3D8B3A4A9618436596558E540971D206D6C7054GA6CEE21B2FB56EE675A900DB87F08870EDA7E9DB97BCF309F6B7579DE26DEC37AD12B6BCFEEB46B614E5604FE18523DD4B1E4552CB996808CC69DFEA5F01E7FD7B685091276A2DF04ABE1F6A539467F0054B21385C1A9A28E08F3F6A76E9F5F56D786850E4AD94
	EC1203CA3B6CD3BCD2CF9B6AFD4411C3F882FFA82FA1382FBB50D77361FDA900250D6B7E3DED75CC3739230B24347BEE0829FB69A92AF35BA9DD772D9DAFE4271FC27E4F103EC03A84A82FG20F65D7E72112B332BD976B8234DE2F63D56446DEAF80F5B75328D6506211C8830C8601FA178E4A8BF85307EBDEE6F61BFF1FBFBAEBDACEDF4BC1431A3063DB96FD3BBAB5F576DED33BC12758A093D4F77E07CF820AE91E0A140CA0035GB96D966B2BA4666D5DFF06E723C3EBD63DDE55576877B7F9DC01D32ACB71
	40FBDD9D54B05CE9452986DC12C4633A59D1EE228F0762764818C62036F5CBBC8E971F7E1224E9FFC7E2AB5A3625CFA4E654B09E475BF55B6637235F7A1D6837E1F8C73A1D98B384FF1E60538CF85A6F4BC9DCE13968830BB579836E671E6DDC17362C85D2DE55D7B3B61B68B26F51C5991E53D40778533AAE5447D7FA5147CBGEF81D883108E303B43E22D6F60BEFE207B60F4BEEE05F88B78E4CFC748611783DAD3486DF1A981666BB50DD3795A8CA85DFFDFB59D754B220346BECA7D6AD7CC7D123A68B399BD
	9D549F7E8E5DAFD4766BCD31E4A7F6CC0E0D6D228F6710B131300376E126405FCE70CB8DF85A37FB889E4BAB202E3453E2256D3B7A901F50C95BEF93FC9402DFE240D3BEEB889FAC27C0DD43990B15766F9E9B4E2F0B213C92A08DE0A3402EAE0B358660F8976F63DFF49F1861B8EAB774AF257DD24FCB70D442DAC0EE7585B55967D43A6400DBF6F81420445ABC5C03FEF5C450ADA7635B89B25ED5FCCAC0565C2A8F384BDE8502A9C85BBC56130FED828A296DF4FB95906075CB1C6FF7FA8A616911035A993FCB
	56943D852DFF2287E3E2CC6E820E40G47C3977A2B174C5F43709E5F45FDF5228FF129D05EA07C52FE36G1EA7A13895CFDDDD0B5B09EA48018BBCC6DD4ED2F866F4DB2C89GF9BD966BF2007A337084E86805B58D40F38E46140D0F5B67FBF8FC36EC7B22D47E44C326710910E42331364C969D1F63C4EE9A6025BE0B35982081408E90871084108C1086A07573357CD1C792630B7ACDB50ED05FE85CEE1B0EC3DB79BB5A97B127F7D39FC453B7C435A33EB251036A7A1C50F54BB4E33E50843753701EEDF84F0F
	EA0763AE5704FE0709CE5BCC5A5138D9560FE32F987CDC8FE06B6731B367ECB19DD3384A72D174FEG86006252A309361D550BB215B1452735A8FE45670217A00B61CF75666A63EBD09531FFBB4163FC5B8C83A0A8701F6D4D27F860E19C2D3C7EBED2AFEBDA406D88E9CA0722E9EE5F10CE7FA051A598B11587192D0F8C202D9A5818GB0DF58DAF32E50B0CE5AD4F5B4646F4499000D37A6322FD8826DD70AF83C4D06FB4DF5227CD9D2566B775BFACCFC0E6D2FE6CD18FA0D29B71511F99D786BB2ECFDB84FD9
	D9AC6632185E8330D73499A5ED368E403C8CE0830887D88C10B2407D711FB63B244F93BC2EB93F69F46172E626F3F61F5D6007ECC31D59F809865447EC3CC4830D1DBF59E247FE054DBC76EBEC6631DFE71BBA361BECE631CD6BDAEDE827BA6EB30C1577ECB87FBBD51FCFA183D610C8F46F7DFCA75A61763935EE37EBC851BA5C9795C9AA31137D9CD9E352217F726CFC6E9F5F4F677E120B77CB498389267B47F91FFFB0E358CE63FC422E4F7DD42777499EC071A8DEF0D4339A72E994FF062CDF8C9F4C247E38
	92C25AD25C6EABE4C042826FD01B54F039CC8C2A200FF6A5B028863C0A2BF4C49E13AB64F12D02F4C8C5A329BE0A6FE545EF3CF124586CD0D4BE688EA82DBEFF082D4225E5DDE515ACFE8A253852F122C3ABF8498DFBC4D4652F25C5B553E1C7D5D2ACBF38B545DB01FF28E24332EFE832822DD0BF1D7860A45E832D51EC5BE271FD9B142D213A5EE18D90DD9B834EE1779810AA4E513F022BEAF9FF2D3E2CDD6718B1B9F01A068C155F7F4658F50F1799EAD04DFD1050F54F750C97B3719FBDDC182EEF351E3F1E
	71B2C3D7550A71FDF4E0A1114FF660977CFF115E8ACD62BE509845B65ADB863E682FAAB63E9655517988885D2A4B6DBC7D570B2F2ADA360FFA4ECEF2C5B037AF81D8E9677B286F75E1BD5FC7314DE2336A715065876A4AE5DCDB9EF29699560BB4BB4D4DED9775BF1826BE57B5F5FD6DB47572B47505A3D3573FB3CD7D68B4751F19267EDBB16B291F7F21BF8F77814A79108247D59C04A7158B7C4C59DF90D557A5FBC27AD976BB7D383F71987638941FBCE03EAE7F53G6EEBC7857F4787B6C9F8A625BC69BEE2
	090CBC7D8137970E8438E9E7B95433CD469C2A8636E13EF5A9BCD78250F87E65E15CFB1BA575428E64F1EF9F4F3126814DC600DD8E4A2747C1FDC25BA97D3C5D8AA827BAE235FBE50037CBF7D0FEC5CE5A2E5DE9EC17AA5A4D877CD2009427993FA4BBEFD76432D84B815ADC34DD024B584EA573F629005FE0E8477995A958EE16484D9E9038FB886EE730013DBB0B6709CE6599F34A2E60C2533D98C20FC279FB14687C59BE42B781707395BE06DB6C4633D0E3B028BA5DA4FD41626467F663BE0D62B65ADF04A7
	4C98C12D95B640E19EEB7FEBC7DDB13F4150ACDE9EFC7D30B4B9DFF31424F979D133F91CCCD01DEA336AD20EC338B60BEDEC17DB41B9107A6172B1E3FEFA790EBBB33644704392E67FEA039F72AC0FE4D11D6E2C43BC21D99E5AC9142C988A39ABC21A9B93A932A613295118D37F96B9A3226C3CC11EE3FE37974754F1A85B8142GB45F75B85983235CC4402D842063FED715F15CCBC3743CD9BA4447CAD5BFAC715255CF725C6FAB4A8AE8BFC54D6C5C178B8A9B18EC4B1796C875D7AE1A663EE76D084F289FE2
	5FB806F4DF3A1E3DBFAB8CC0ED3C351F5B38845A2C9D62B62628E6B6968FF35FB48DF03A9A40F906396D778F60F8384A6D8B337B3B79E857C75D32D07D326DDC3F0520DB6AB0372B4AE6342B48CD6D2AF7F37DBD76EB6DBBAAF715105B8EBCE739395CDF5A4C7C19AC64D64A9FADE6322A9B61791177AF92376313B237A385F45CBA426DF83B5F4C0E1691FE766C2EB64EF749E973E24FF7A3EC3E9B091E6FB639F00C4E827C7C91BE5FDD10738D735DE918E1A092E0D59467FB877A9156BFD6FE5D01F2497AA771
	9CDA33023AAF811E9600B5G69G3BC7390C2FB8267D86F1B4BC45B70815A21FDF8FBC5B81DC23FA9EA98C6F892311F9A51AEFFF164CBFC923B45FDEA8703FA0785586BC6D1BD3840FE54983BE72705C4D6A3E686F89073C1172BA234A146E19C9F495D165F0949D0D39A18F7A0D4C4598D4610A60385BB7D4D7F702879F1BF35F2F4AE132923DBC8FFE67A02E33AB213C86A09DE0178F46A1C08F40040F770955E93686C576C92C85C84FC9D2BB6776DF1BFF96CC220B764FFCDFA49D35FB0B1F5B3D54477B6878
	40356950B5096E77D165F5BEB33AAD76EB13774AA43AE81B33D43318B85295ABA658091179E6584657615DAA774DF72FD1579FCC220B76CD27EA56BFEF10DC966A6D91BAFCDA3EB69D6E1DC4976D2F0526BA847A398E4BD5BEB61E9EC25C86A8E77979B868F7E22E134FFFA45DA27139EF0B8B777AEA40BD6476F1DC9E41B955GC9B4D25CC9A5F68E7434E21E836DD14CF3207D4A54797FC1A5D60E14666AFFC876E1C5E0DFA5G45BDBF0C3896A877885C27499D14E1A8CF881CB40A38A4A8AF9338A7BD643B9A
	14B778F9DF7E3437841EB967AD56920096G97C0BCC09240F200D40074733C7DEFC97B5D81180782744C016793BCD35882744CB09120E7825A3E23B7911EC84F7B3464FCE49F9BA1F50A3A19004BC03F1C3D3730E70E41AE2A5BC5221B713BD576943C233FD9DDAD9873757C3B56E67644F519676A7710722A17F61058898615A0193CF1274E6A7EC35BA91964425F133D0A44F3C11B7BF3D91B231A5ADCA546E0E7959F03738313FD3D84F0AB22706CDB55E06CF17140207938D8B208768455D04029C4477E4303
	3BC41D3E9A619E954DE067F8C22F4901A1E5525879E7C2CF6BE2512F9E0CB53E280F3E9D61230342C7F9FE6E2327CCFC348EF0D2B092CF79DD99B0725373F2CDF6A37ECDA147EBB34E07C50CE7A9FB5633E7BBBCDD6C1D725BA49B79D58BFE5591FB14F0140E89414936D0FE0FC7702B917C46A3567424A87A25A67C285F1F99423DD8D814FFCC4A97043CDFB6E13F7A834A209288A8AE7A2986BA9367289D4DB8C72D845EA9C13E6F4FF663BC6DD083A4G5AC93B0BEE4C6534E83C4D83240DC73E2006F0528FE1
	1BEF3AF94EE6AE34DB8C108230D623FA53357821F19CF70381598B215161D71CD2AA21FDF914539E07328DE082E0BE40729017793C87E901506D8DF92995C437CDDE3421E48C666DB15E160EF9BD0B5DA6BB603C4D6BB0CE0DD9F6DAAB097A0288DA31F59475C5D1743C852DDF3593E5C7E525996D9DC730CE4F989B757A721102083AE8392B0E205E466CED245E4F9EA90A2A57333B68271AF346F3D24F98BDA7CD0C71F3D4FD2DD9FD2228FF2E4F2CFE21289FB42DDFB6465743D1326736C0F92D407DA1072F1B
	6982775CB944350D43B89C6738BF79B9EDD8603EF91653A688DC390F53268ADCBC59572D073294663852823256C339C460BED94079D58ADC8D590F45C1F921404DA238E5D0FECA60DA89EE9D1453856ED302DF5E850E3B156C2D6CD0768B5C5CC25EAE9EF0A99770FE6A23BEC96F5761E2F87A1471E2F1CEEEF1FB959FDE6CA9BDD8D9D9DEDCD5D9DDC64F3AC96346336E2197BF97732C3B6BA24B9B3367D6E5BF131768C6CE6CCE23E264D7F9E9B8A3B386BF9763239978513361CF5D7C7E66C228CB3D486FEFD6
	117C27581723E1F5F56F865C2EA6F9280D4CACF4BFDAB51243680BAE717C58408872EC03328BE06292BF6B07DF63B93D45DFD8A03545FD45744EE9DA671D99F3AF31BBC61774333E5D72C89615F9F8144B5CG756593A4FEC70D710DFEF855CA7754347E6E087AFC66C33D7EE60F319E6B3AB30D758FC754230F36B1FAEA5B377F48EDCB7F7692497335241877E93589EA53BC76CCBA38DF325C79C0960D2D4A73FAEC3D5E3BBBF6ECC1E4D516513E7AB9D97FCD4ED0E48A22BE9BA56396F5DA8DF2A9AED84BF1B5
	17B92E241F1FC167895C5DFD9C377AB21F43971C0E3DEE4DA5F3C94E0796EB910076A1628E653418780243E6E6B3FFB6GCDA740CE666BE1F8FF1079134A6F74192FB7AF1179E4B934DF89C0F16DE4FE5A0065ABDC1F274ECE27CF36C11F72AB117AB4C0F907156AC3E51C21F39B60FB2EF09F513BC303AA9C3DC81B0708EE71D01FA85A3CD300EDB458B0527E6F889016C4470585D369487D45F56CB4788C75CA8699CEF87E7C0597DE2032CE76611D48E1B878DD841DE4CFA7C8EDD703EE7691CF236BC48E0BE9
	0B50711E3E42883AC60FFB48075F89984D5F0BF371C6C1E36F0B34FD0C34A9090379BB0E77419726340F5FCB4AE6765963A27BC00B23730A9E935D7D11B29FA6319C876D92044C878B27123905494CE430BCCA66EA03CCBA6E3FFB4EB887675DB5AB43AA190F7B54B8769B0038687C6551A06A397B860B75508D7C7C3ED9FD9D1EDD7248642FA800EC8F28FEA520DD20D872A51065AC9BB474C10F6053266E1B028F6640BA60C8609B627B5C1F392F6DDE747FDCA35D1BA3D7CBC7F31D7FCA56984F8DB457E99578
	D4028F9B70F41DD3C678BA978FF5EBEF60391341904E1DEDB7DA2C43GF1G73G1681245C48FD78B9956F8DCD19D36CD47DEDF0A07498D29B543E2651EB3BAF7E4D4DFCBDD97A48835232DB6CB13FFF2D3D111D69EF224FC3557B891E5A776BB1346F9054F582048192819682AC3F095B7793351C5A07415128E932F318FC8564466107B99D0D8783EA1B7F1C59BD4555CC0FB5B71967AFB682FEEBFCE4DDB114EB22F0D4C63856CC46F1565696EFAE438F7878283AF9D0DEECAA23AC62EC174C5A243067DA9399
	AC4F9BF146CCE76D72EE264F121BA75351B97E9EBC8E32B96B259C3C7F98E30D2F2A2EADAF2E2EA9A3F7A4F18E4E11783F756FCC46F17E1BAFCAA20EFE341547516E876F13DA1E34E76C0F91C707183E33EE5667271A2E9732A94F7317F41D9FD6E722B3253BFFC2277B7A595F4726FB0B11195ACAF9D9AE6BB6E6DF51799659E6421752116516516DAE5897CDF78C960F52FDAEB8BB2AC37BE007855BF71D36A1DB273D5CD78CCF43F62B22837FB62B644786DA3062F46B3B47BB595D353E3DD337EDBBFA2C1349
	78D12EAE63269969D7BD254F323668BE33EF53F94FAA7C683CEF42B0E173CCA59E1DD90E63B301BFCAB1569C18CDCE003C779DA30AD36B1407844D09A0B29379A93A24313A8F023877F653B33D7BA23936C0D7ADF637C143FCC7B0E29DFBFAC49263744706775191337964FA8115917F9715C1EDD6C331F310E3A1739C64C448BCB7FFE3E86A7B4933C3316FA75F96B25ECF2631F0B88F7BAB996611D4G6996B887G145E42571B3DE398FB786B1D49EB4DD15710428786171FB19E5B565C7169FF67D3B00E77D8
	9C2A2629DE644F713B46313FB455CF109437E79CFD94F08F8DEB34A9436F9F477877A80384CDF383FFCB4ED4ADE0D767ADBCDF70AB0233C3B9ACF0F3CE737CC802404D2FA0F985A8AF91381AA04FB52C903843A2E7D274890E3B2B164BAD9538575F64F26B856E578DDCEE3B40BD391F4BF5895C23DDDCEED8603EDF46652E943886119FC995389C129FD98FE5E99647F9C54EE42B4051B3028D4A9E012BA56D62201CA8F0F7895A7933789E6D337938E7DF87651C3F0173BEC08BC08F009FA09EE09E40E20095G
	29G6B8132E6DB2C3BGCA816A815AE70BBD714532D87393A0D9120384E1C0B6FEB7BB1A97BB8E5B72260F43B3F9640E95448FE690A92EB70FFC0B9B9C8CAAA25E877272842EB548CFE86C3E4C39487362B8792CCF6B365B4819D88EC71D09DFA367CE97586F1F4D4F445FA0B1B99F4A0BE673B35BD7ABA24FCF2BC80C26C0FD5AECFEFEBA991C6ECC0A404FEC51E7523CB9966B9B12715C6CA1713D9B7025F338EE5FA2715D89E53B40F512781E0072AC015B57083825D0CE96B82904647A203CF18E5779692E69
	F44E1EC26752840B35B6C2674CBF230C9A40B7A4F039DEB2B6BCD08EA7F0FF6EAD0C6C2B3BCF9096C42778A94F217CB7E019B1F4DA14201FEF789EA3326FFEC2465BD2E817AC74E90B52A779242E4FC3D36A13BB0DBE6B8D7A50987EF2FE6C3172757C69474ABF661B4F590F65CF1E339F4FB71F33D7679B67EC765B4BFC6C67A19863BD225D77856E2C407D1070C4DC375B258D8B7C3F933D90FFDCC1019C7FDFC42FF1026BE13875865CD9067BC5BE0EF7B0F9D8955F59FF13CF7A4E6D71307B15240D5B8743DF
	78A333GF5F47BE5035EB98207FA53BEFD167CDF8A6CB7AE27275A973F857B3BB7455EF653FBBADDD7414C69D25AF53A5260556D27FF7501A4747EDD074EE7EF6D4C659F6E5469CA5E44F9F39F7B78D3CC3E7E546DE3FB5FB3FA3B679B2662FFB002FFCE37CEE759BFF3BA52A57C5C53F5F5FE79C42F6E17874E69FCFEDBB9F3791B7AF53A742BBC67148D6872E7ABBA1F32991D99F4BEDF9CC4DA7A7F003CF49913300B1F1332038129E0327E0D37BE7D64620116228CA923D55AB4CC46CC2B14BD0E03AF0CBF2D
	86DE567260F969C3C97C53242D6CC3DEB15E8890151871388CE2F688B12DE4AC250D9D2C4BFC763E4C8CA98BE4503925D54A428982C4852848CC18DB08C034B983E0946045A85E4652C9D24AE62DB9C5D6D41B0034AD42E90F5DDB7B934BB3DD41CCE9D32B748A4E5B9DCA009CEF94979A43FE9BFECC4AA57FBF862E6620EA10BC32035A850F52A6958D1E465F8384B5455548B76FC1CD5AC92EEC9E40CB4D870E4B41E18DA756375B24AC9F7E0ADDAA20559E59B7F4001E225EA676DA9B6DC4DFF22883FD8B093E
	4825C2F0A97C460EBF7E61441AE519D246B169C50FAA3B0E49CE58BB35CA395AF0C0898E2B9ED7587FCE83E114B34E2FECE5823BD70579E8EC85661E055223613B93362D5DF68C1C978EC887C8CBDC26AAC48BD773309CGD1CA20D543DF652B0192D1F7436F35573E736E4B368C494A0CC457EF8B7BB56AB09189E8F245B150DA7B7EF32B967CDB7D7AF5C0F2CC4AA637BEC1F01E5E6A3D10572184EED9FC7B1EADF3536620DEFEF48F6954FCCD5AB4A8FB5D1E8B7052C2C21F63F3E1C973857166AC88AD0E3218
	D78945ABBE3D2A72767BFF53CB9457C9B5E9AF6DA2A0BDB024285E830D0303EE1FA298C0FF817BBD02BD362908EAD3903EED6709A7DEF0C0FC35C8195E4ACA72370A7C2D463FD584D3C5B0D55550E96B25A97FC5570743E8736F42A27A797712E3244391CBB2160DB498361228C26C0E49610168C2D239931B634BAE61DBAC3DCAAA77103F45572669EB3A2638B349E2E3F55347CF53B7A6EB0A7B2CCDB05D11A52D785D009A7257C111DEC7A8465384943371D6C11D18F7F0ADBCE5D222365A89F67BCCED356B36
	3A2E272D7F275B4A3FE5E12FEC0F54DB1336C7AE6A91552DA6250F1511AD3A111B8CC6667C85743E2E0158266BDD407446E04C7D78E95A9129E986571466404B2E983F51ED3C79CA4CFB97A5F730EFA8F7C4FF23BB73A19A2481BE718E1EC7787CE5BC4B464AF3F6F8E50FC713BD230DBE9766BC1BAEE80A1ED36B7D907987F99BD6C96B648FD16F91834D7F83D0CB8788D80CC2A8E399GG28CDGGD0CB818294G94G88G88GE00C1EB1D80CC2A8E399GG28CDGG8CGGGGGGGGGGGGGG
	GGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG1D99GGGG
**end of data**/
}
/**
 * Return the InitialStateComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getInitialStateComboBox() {
	if (ivjInitialStateComboBox == null) {
		try {
			ivjInitialStateComboBox = new javax.swing.JComboBox();
			ivjInitialStateComboBox.setName("InitialStateComboBox");
			ivjInitialStateComboBox.setPreferredSize(new java.awt.Dimension(75, 24));
			ivjInitialStateComboBox.setMinimumSize(new java.awt.Dimension(75, 24));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjInitialStateComboBox;
}
/**
 * Return the InitialStateLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getInitialStateLabel() {
	if (ivjInitialStateLabel == null) {
		try {
			ivjInitialStateLabel = new javax.swing.JLabel();
			ivjInitialStateLabel.setName("InitialStateLabel");
			ivjInitialStateLabel.setText("Initial State:");
			ivjInitialStateLabel.setMaximumSize(new java.awt.Dimension(73, 16));
			ivjInitialStateLabel.setPreferredSize(new java.awt.Dimension(73, 16));
			ivjInitialStateLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjInitialStateLabel.setMinimumSize(new java.awt.Dimension(73, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjInitialStateLabel;
}
/**
 * Return the PeriodicRateComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getPeriodicRateComboBox() {
	if (ivjPeriodicRateComboBox == null) {
		try {
			ivjPeriodicRateComboBox = new javax.swing.JComboBox();
			ivjPeriodicRateComboBox.setName("PeriodicRateComboBox");
			ivjPeriodicRateComboBox.setFont(new java.awt.Font("dialog", 0, 12));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPeriodicRateComboBox;
}
/**
 * Return the PeriodicRateLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPeriodicRateLabel() {
	if (ivjPeriodicRateLabel == null) {
		try {
			ivjPeriodicRateLabel = new javax.swing.JLabel();
			ivjPeriodicRateLabel.setName("PeriodicRateLabel");
			ivjPeriodicRateLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPeriodicRateLabel.setText("Rate:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPeriodicRateLabel;
}
/**
 * Return the StateTableComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getStateTableComboBox() {
	if (ivjStateTableComboBox == null) {
		try {
			ivjStateTableComboBox = new javax.swing.JComboBox();
			ivjStateTableComboBox.setName("StateTableComboBox");
			ivjStateTableComboBox.setPreferredSize(new java.awt.Dimension(125, 24));
			ivjStateTableComboBox.setMinimumSize(new java.awt.Dimension(125, 24));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateTableComboBox;
}
/**
 * Return the StateTableLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStateTableLabel() {
	if (ivjStateTableLabel == null) {
		try {
			ivjStateTableLabel = new javax.swing.JLabel();
			ivjStateTableLabel.setName("StateTableLabel");
			ivjStateTableLabel.setText("State Group:");
			ivjStateTableLabel.setMaximumSize(new java.awt.Dimension(77, 16));
			ivjStateTableLabel.setPreferredSize(new java.awt.Dimension(77, 16));
			ivjStateTableLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjStateTableLabel.setMinimumSize(new java.awt.Dimension(77, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateTableLabel;
}
/**
 * Return the UpdateTypeComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getUpdateTypeComboBox() {
	if (ivjUpdateTypeComboBox == null) {
		try {
			ivjUpdateTypeComboBox = new javax.swing.JComboBox();
			ivjUpdateTypeComboBox.setName("UpdateTypeComboBox");
			ivjUpdateTypeComboBox.setFont(new java.awt.Font("dialog", 0, 12));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUpdateTypeComboBox;
}
/**
 * Return the UpdateTypeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getUpdateTypeLabel() {
	if (ivjUpdateTypeLabel == null) {
		try {
			ivjUpdateTypeLabel = new javax.swing.JLabel();
			ivjUpdateTypeLabel.setName("UpdateTypeLabel");
			ivjUpdateTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjUpdateTypeLabel.setText("Update Type:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUpdateTypeLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {
	
	com.cannontech.database.data.point.CalcStatusPoint point = (com.cannontech.database.data.point.CalcStatusPoint) val;

	LiteStateGroup stateGroup = (LiteStateGroup) getStateTableComboBox().getSelectedItem();
	com.cannontech.database.data.lite.LiteState initialState = (com.cannontech.database.data.lite.LiteState) getInitialStateComboBox().getSelectedItem();

	point.getPoint().setStateGroupID( new Integer(stateGroup.getStateGroupID()) );
	point.getPointStatus().setInitialState( new Integer(initialState.getStateRawState()) );
	
	if( getArchiveCheckBox().isSelected() )
		point.getPoint().setArchiveType("On Change");
	else
		point.getPoint().setArchiveType("None");
		
	point.getCalcBase().setUpdateType((String) getUpdateTypeComboBox().getSelectedItem());
	point.getCalcBase().setPeriodicRate(CtiUtilities.getIntervalComboBoxSecondsValue(getPeriodicRateComboBox()));


	return point;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getStateTableComboBox().addActionListener(ivjEventHandler);
	getInitialStateComboBox().addActionListener(ivjEventHandler);
	getStateTableComboBox().addItemListener(ivjEventHandler);
	getArchiveCheckBox().addActionListener(ivjEventHandler);
	getUpdateTypeComboBox().addActionListener(ivjEventHandler);
	getPeriodicRateComboBox().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CalcStatusBasePanel");
		setPreferredSize(new java.awt.Dimension(300, 102));
		setLayout(new java.awt.GridBagLayout());
		setSize(427, 141);
		setMinimumSize(new java.awt.Dimension(0, 0));

		java.awt.GridBagConstraints constraintsStateTableLabel = new java.awt.GridBagConstraints();
		constraintsStateTableLabel.gridx = 1; constraintsStateTableLabel.gridy = 1;
		constraintsStateTableLabel.gridwidth = 2;
		constraintsStateTableLabel.ipadx = 22;
		constraintsStateTableLabel.insets = new java.awt.Insets(14, 7, 7, 2);
		add(getStateTableLabel(), constraintsStateTableLabel);

		java.awt.GridBagConstraints constraintsInitialStateLabel = new java.awt.GridBagConstraints();
		constraintsInitialStateLabel.gridx = 1; constraintsInitialStateLabel.gridy = 2;
		constraintsInitialStateLabel.gridwidth = 2;
		constraintsInitialStateLabel.ipadx = 26;
		constraintsInitialStateLabel.insets = new java.awt.Insets(8, 7, 6, 2);
		add(getInitialStateLabel(), constraintsInitialStateLabel);

		java.awt.GridBagConstraints constraintsStateTableComboBox = new java.awt.GridBagConstraints();
		constraintsStateTableComboBox.gridx = 3; constraintsStateTableComboBox.gridy = 1;
		constraintsStateTableComboBox.gridwidth = 3;
		constraintsStateTableComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsStateTableComboBox.weightx = 1.0;
		constraintsStateTableComboBox.ipadx = 85;
		constraintsStateTableComboBox.insets = new java.awt.Insets(10, 2, 3, 107);
		add(getStateTableComboBox(), constraintsStateTableComboBox);

		java.awt.GridBagConstraints constraintsInitialStateComboBox = new java.awt.GridBagConstraints();
		constraintsInitialStateComboBox.gridx = 3; constraintsInitialStateComboBox.gridy = 2;
		constraintsInitialStateComboBox.gridwidth = 3;
		constraintsInitialStateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsInitialStateComboBox.weightx = 1.0;
		constraintsInitialStateComboBox.ipadx = 135;
		constraintsInitialStateComboBox.insets = new java.awt.Insets(4, 2, 2, 107);
		add(getInitialStateComboBox(), constraintsInitialStateComboBox);

		java.awt.GridBagConstraints constraintsArchiveCheckBox = new java.awt.GridBagConstraints();
		constraintsArchiveCheckBox.gridx = 1; constraintsArchiveCheckBox.gridy = 3;
		constraintsArchiveCheckBox.gridwidth = 3;
		constraintsArchiveCheckBox.ipadx = 49;
		constraintsArchiveCheckBox.ipady = -7;
		constraintsArchiveCheckBox.insets = new java.awt.Insets(3, 7, 5, 70);
		add(getArchiveCheckBox(), constraintsArchiveCheckBox);

		java.awt.GridBagConstraints constraintsUpdateTypeLabel = new java.awt.GridBagConstraints();
		constraintsUpdateTypeLabel.gridx = 1; constraintsUpdateTypeLabel.gridy = 4;
		constraintsUpdateTypeLabel.insets = new java.awt.Insets(9, 7, 18, 4);
		add(getUpdateTypeLabel(), constraintsUpdateTypeLabel);

		java.awt.GridBagConstraints constraintsUpdateTypeComboBox = new java.awt.GridBagConstraints();
		constraintsUpdateTypeComboBox.gridx = 2; constraintsUpdateTypeComboBox.gridy = 4;
		constraintsUpdateTypeComboBox.gridwidth = 2;
		constraintsUpdateTypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsUpdateTypeComboBox.weightx = 1.0;
		constraintsUpdateTypeComboBox.ipadx = 4;
		constraintsUpdateTypeComboBox.insets = new java.awt.Insets(6, 4, 15, 2);
		add(getUpdateTypeComboBox(), constraintsUpdateTypeComboBox);

		java.awt.GridBagConstraints constraintsPeriodicRateLabel = new java.awt.GridBagConstraints();
		constraintsPeriodicRateLabel.gridx = 4; constraintsPeriodicRateLabel.gridy = 4;
		constraintsPeriodicRateLabel.insets = new java.awt.Insets(9, 2, 18, 4);
		add(getPeriodicRateLabel(), constraintsPeriodicRateLabel);

		java.awt.GridBagConstraints constraintsPeriodicRateComboBox = new java.awt.GridBagConstraints();
		constraintsPeriodicRateComboBox.gridx = 5; constraintsPeriodicRateComboBox.gridy = 4;
		constraintsPeriodicRateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPeriodicRateComboBox.weightx = 1.0;
		constraintsPeriodicRateComboBox.ipadx = 4;
		constraintsPeriodicRateComboBox.insets = new java.awt.Insets(6, 4, 15, 21);
		add(getPeriodicRateComboBox(), constraintsPeriodicRateComboBox);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	javax.swing.border.TitledBorder border = new javax.swing.border.TitledBorder("Calculation Summary");
	border.setTitleFont(new java.awt.Font("dialog", java.awt.Font.BOLD, 14));
	setBorder(border);
	
	//Load the Update Type combo box with default possible values
	getUpdateTypeComboBox().addItem("On First Change");
	getUpdateTypeComboBox().addItem("On All Change");
	getUpdateTypeComboBox().addItem("On Timer");
	getUpdateTypeComboBox().addItem("On Timer+Change");
	getUpdateTypeComboBox().addItem("Historical");
	
	//Load the Periodic Rate combo box with default possible values
	getPeriodicRateComboBox().addItem("1 second");
	getPeriodicRateComboBox().addItem("2 second");
	getPeriodicRateComboBox().addItem("5 second");
	getPeriodicRateComboBox().addItem("10 second");
	getPeriodicRateComboBox().addItem("15 second");
	getPeriodicRateComboBox().addItem("30 second");
	getPeriodicRateComboBox().addItem("1 minute");	
	getPeriodicRateComboBox().addItem("2 minute");
	getPeriodicRateComboBox().addItem("3 minute");
	getPeriodicRateComboBox().addItem("5 minute");
	getPeriodicRateComboBox().addItem("10 minute");
	getPeriodicRateComboBox().addItem("15 minute");
	getPeriodicRateComboBox().addItem("30 minute");
	getPeriodicRateComboBox().addItem("1 hour");
	getPeriodicRateComboBox().addItem("2 hour");
	getPeriodicRateComboBox().addItem("6 hour");
	getPeriodicRateComboBox().addItem("12 hour");
	getPeriodicRateComboBox().addItem("1 day");
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 9:11:36 AM)
 * @return boolean
 */
public boolean isInputValid() 
{
	return true;
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getStateTableComboBox()) 
		connEtoC5(e);
	// user code begin {2}
	// user code end
}
/**
 * This method was created in VisualAge.
 * @param stateGroupID java.lang.Integer
 */
private void loadStateComboBoxes(int stateGroupID) 
{
	if( getInitialStateComboBox().getItemCount() > 0 )
		getInitialStateComboBox().removeAllItems();

	for(int i=0;i<allStateGroups.size();i++)
	{
		if( ((LiteStateGroup)allStateGroups.get(i)).getStateGroupID() == stateGroupID )
		{
			java.util.List statesList = ((LiteStateGroup)allStateGroups.get(i)).getStatesList();
			for(int j=0;j<statesList.size();j++)
			{
				com.cannontech.database.data.lite.LiteState ls = ((com.cannontech.database.data.lite.LiteState)statesList.get(j));
				getInitialStateComboBox().addItem(ls);
			}

			break;
		}
	}
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		CalcStatusBasePanel aPointStatusBasePanel;
		aPointStatusBasePanel = new CalcStatusBasePanel();
		frame.setContentPane(aPointStatusBasePanel);
		frame.setSize(aPointStatusBasePanel.getSize());
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
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val)
{
	//Assume that defaultObject is an instance of com.cannontech.database.data.point.StatusPoint
	com.cannontech.database.data.point.CalcStatusPoint calcPoint = (com.cannontech.database.data.point.CalcStatusPoint) val;

	int stateGroupID = calcPoint.getPoint().getStateGroupID().intValue();
	
	//Load all the state groups
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		allStateGroups = cache.getAllStateGroups();

		//Load the state table combo box
		for(int i=0;i<allStateGroups.size();i++)
		{
			LiteStateGroup grp = (LiteStateGroup)allStateGroups.get(i);
			
			//only show the editable states
			if( grp.getStateGroupID() > StateGroupUtils.SYSTEM_STATEGROUPID )
			{			
				getStateTableComboBox().addItem( grp );
				if( grp.getStateGroupID() == stateGroupID )
					getStateTableComboBox().setSelectedItem( grp );
			}
			
		}
	}
		
	loadStateComboBoxes(stateGroupID);

	int initialRawState = calcPoint.getPointStatus().getInitialState().intValue();

	//Select the appropriate initial state
	for(int y=0;y<getInitialStateComboBox().getModel().getSize();y++)
	{
		if( ((com.cannontech.database.data.lite.LiteState)getInitialStateComboBox().getItemAt(y)).getStateRawState() == initialRawState  )
		{
			getInitialStateComboBox().setSelectedIndex(y);
			break;
		}
	}

	getArchiveCheckBox().setSelected(calcPoint.getPoint().getArchiveType().equalsIgnoreCase("On Change"));
	
	String updateType = calcPoint.getCalcBase().getUpdateType();
		Integer periodicRate = calcPoint.getCalcBase().getPeriodicRate();

		getPeriodicRateLabel().setEnabled(false);
		getPeriodicRateComboBox().setEnabled(false);

		for(int i=0;i<getUpdateTypeComboBox().getModel().getSize();i++)
		{
			if( ((String)getUpdateTypeComboBox().getItemAt(i)).equalsIgnoreCase(updateType) )
			{
				getUpdateTypeComboBox().setSelectedIndex(i);
				if( getPeriodicRateComboBox().isEnabled() )
					CtiUtilities.setIntervalComboBoxSelectedItem(getPeriodicRateComboBox(),periodicRate.intValue());
				break;
			}
		}	
}
/**
 * Comment
 */
public void stateTableComboBox_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {

	if( itemEvent.getStateChange() == java.awt.event.ItemEvent.SELECTED )
	{
		LiteStateGroup selected = (LiteStateGroup) getStateTableComboBox().getSelectedItem();
		loadStateComboBoxes( selected.getStateGroupID() );
	}
}
/**
 * Comment
 */
public void updateTypeComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

	String val = getUpdateTypeComboBox().getSelectedItem().toString();

	getPeriodicRateLabel().setEnabled(
		"On Timer".equalsIgnoreCase(val) || "On Timer+Change".equalsIgnoreCase(val) );
		
	getPeriodicRateComboBox().setEnabled(
		"On Timer".equalsIgnoreCase(val) || "On Timer+Change".equalsIgnoreCase(val) );

	fireInputUpdate();
	
	return;
}
}
