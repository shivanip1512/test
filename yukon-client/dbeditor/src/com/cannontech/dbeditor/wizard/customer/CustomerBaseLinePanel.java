package com.cannontech.dbeditor.wizard.customer;
/**
 * This type was created in VisualAge.
 */
import java.util.Collections;
import java.util.List;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.customer.CICustomerBase;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointTypes;

public class CustomerBaseLinePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JCheckBox ivjJCheckBoxEnableBaseLine = null;
	private javax.swing.JLabel ivjJLabelCalcDays = null;
	private javax.swing.JLabel ivjJLabelLoadPercent = null;
	private javax.swing.JLabel ivjJLabelPerc = null;
	private javax.swing.JLabel ivjJLabelPreviousDays = null;
	private javax.swing.JPanel ivjJPanelBaseLine = null;
	private javax.swing.JTextField ivjJTextFieldLoadPercent = null;
	private javax.swing.JTextField ivjJTextFieldPreviousDays = null;
	private javax.swing.JTextField ivjJTextFieldCalcDays = null;
	private com.cannontech.common.gui.unchanging.JCheckBoxDayChooser ivjJCheckBoxDayChooser = null;
	private javax.swing.JComboBox ivjJComboBoxHoliday = null;
	private javax.swing.JLabel ivjJLabelHoliday = null;
	private javax.swing.JComboBox ivjJComboBoxPoint = null;
	private javax.swing.JLabel ivjJLabelBaseLnPoint = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CustomerBaseLinePanel() {
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
	if (e.getSource() == getJComboBoxHoliday()) 
		connEtoC3(e);
	if (e.getSource() == getJCheckBoxEnableBaseLine()) 
		connEtoC6(e);
	if (e.getSource() == getJCheckBoxDayChooser()) 
		connEtoC5(e);
	if (e.getSource() == getJComboBoxPoint()) 
		connEtoC7(e);
	// user code begin {2}
	// user code end
}

/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTextFieldPreviousDays()) 
		connEtoC4(e);
	if (e.getSource() == getJTextFieldLoadPercent()) 
		connEtoC1(e);
	if (e.getSource() == getJTextFieldCalcDays()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}


/**
 * connEtoC1:  (JTextFieldCompanyName.caret.caretUpdate(javax.swing.event.CaretEvent) --> CICustomerBasePanel.fireInputUpdate()V)
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
 * connEtoC2:  (JTextFieldCalcDays.caret.caretUpdate(javax.swing.event.CaretEvent) --> CustomerBaseLinePanel.fireInputUpdate()V)
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
 * connEtoC3:  (JComboBoxHoliday.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerBaseLinePanel.fireInputUpdate()V)
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
 * connEtoC4:  (JTextField2.caret.caretUpdate(javax.swing.event.CaretEvent) --> CICustomerBasePanel.fireInputUpdate()V)
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
 * connEtoC5:  (JCheckBoxDayChooser.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerBaseLinePanel.jCheckBoxDayChooser_Action(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxDayChooser_Action(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (JCheckBoxEnableBaseLine.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerBaseLinePanel.jCheckBoxEnableBaseLine_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxEnableBaseLine_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (JComboBoxPoint.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerBaseLinePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
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
 * Insert the method's description here.
 * Creation date: (10/18/2001 11:34:59 AM)
 * @param c java.awt.Component
 * @param enabled boolean
 */
private void enableComponent(java.awt.Component c, boolean enabled) 
{

	if( c instanceof java.awt.Container )
	{
		for( int i = 0; i < ((java.awt.Container)c).getComponentCount(); i++ )
			enableComponent( ((java.awt.Container)c).getComponent(i), enabled );
		
	}

	c.setEnabled( enabled );
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GABE746AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DF8D4D71528580D1F2ECBED5028F417255825EB34D90D0A0A959515367815DD2936EB3E0A9215CF290DB6B52C6247CBA60189640FA4C0C2C298C248CCA0BF139F7EC2A089791BC05A4692D2EC691A174CCBB2E5B2B31DF901C9CB691EF37F5E1B491B2481592F7DFAF86F1EF36FB9671EFB6E3D671E7BA6D26101459929E90512141AA1317FCE24CA521AC1C97A790F9B1F64B83314E212F47F9D84
	58A5251CCE07E7915036DCCAB165C91F361B21DC8DE54B73A9263D70DEA03DD53E318FDEA248D98934DF58FF750D19655C1D4365ECA7654555D9704C813083EEGE031047217D797B1FC904AB96C9DA1B5CD1206D2314EB79A0BF478C4D62F89E88E0074B42C73436AD2C6DF86B82C33921E6BD2E95DCD704CA45AFD3079A42BF7656EE5D272A3475332D1160EBF073EB45A56471E9669A1E28813D41E5F814F167E2BE3E7C73C9335A332476375284A48F82DF3D8F13AD42F3F763A6BCD596F2C9D198C285E8945
	5F5C5C415E4E4981254F65D17AE50F621656935DB7B41A89DF509D4AEE9362DEEDC2FBD540FB8B4000096B7F1F59ADCC3F682B4B247577D5C554BF4EC475DEE752745F78218D697353117F96B29E582E904AFDGD4174E9A2E4B6AF42E4BA5AB5615B2D2CCC5G768C0EAF35A15E8165A100E08657710C156BF8792D2FC86E6CC7B2F2A36838A0036A36BCC353714A0D8D694544E776676138A7235CEDA926EA00FEG9F40820092622BCF649C01E77B305A611D1870FA5AFD3EF3EE277FF92FD3F143FBF3B3D098
	6EA5E5446BF7CA9275470D0D55867AF0D836AD549791E85FDE16387F443E74F9694A0AC7B2B28C7A967F784934950C475AEDDA5F0A36235DFECA46B68970588EEDC3714F907C156D9A1E0E15CC70248CB4494C6DFCB9076B52173EC44A2BFFB5ED3B01AEB15F6ECF3BE126BAC41BB5DD280DD3F6210DD782BE81A099A0AB93EC8D501749EDDC1E5BB81B0DFB417F7D9E59FDF1F258A77B55F313AE37D371B3DB27554CE4EBA3206D72665C0E5AE5D923FE0C923E780A21DD921635260DE7D2FB4C4B546CC2E577C6
	143DB8F33AEF6408B1FC184C1578CCBA06A6017FA1412F526169583ECA70D8DE873413BB4057C97D45E4EEAD5A916EA3F1843FEAC7380F145B380FEC825A784E94939D5F058538AEA6C2D94A825EGF600FEGB7403CACBE469339F56FF29E756B4617362D4C5B83CFA5287A65DECFC015BDA34A206CF74943EEA5A031BAF5F9E8576110F6CDE4FE0F000C9E452378E5556575GF7F9C281E78A50BAE772F6E19D3FC228832E898584CC78A44E37AB2FG1EEEB920DE72B9E5D551EAD07A73F968935764C160888C
	F0BEE6213D3612B5F7B13C2F4862367AF6A1628A33417F3339DDD66765437382B83762EEEE6EF40D209A32FF0A7B68AF1A503EEDD0FF9500A3A745349A20AFB7453499A0BE0F4F555FDE66BE59197DB9297A713F9A7AA45957F6D17F6A5B956E134F91D9AA60238162815681A483946627181A7279DEBB560476C19D66B2F7483A15C75BE0FF7478D52C0C7D43A776F1F398FFAA7BAEA2FB679C6566B05F4DE1F3C10F4F507167F3BCBB2C8E9DBF7B9E1C8383E0879F4002FCBE0626DD966A5B3879F2EFFE518F0E
	28B8359125F525FC54C33926F854CE4527F81C7092E03E34BDBFD37373D12F7041EC0247F52F839CB1A07085793BA89EF868E78D2717903A2C2AFE5770242ADCD4D4556599535A9FA23A84C21654G6BEB43DE6C6BB26863CA0035222F317985BAFF6D73FA2FCE7A86F0A6B23F9FA27BFBA2545FA27C65B1AB46EA1682DEFE1214B53AEC3DBC4338E13B390E75F43FF9F3C69961BEF1EBB26E371532F793732B92662B5C27E83DAF13FA1D50AFB7C0B4408A00C4002CC218E7055CAEB61BCF27DF86EB1F49F8067B
	289ECAEE3BAFBDC2B617EBE2B2D1F66E8C6DB3EFC3B67531FFA908BCC76EAFB01EA3318546F3648B85B34F012F95984D81CA7BD681765DFB5D231BD35FAB4075FA446B71A8E4E20BA6920D5DFFBB007DF0F9DC6A4BAE671822DEF43D29409EDA08F3E3CF934E0DAB706EAB646B7675C3FC5D2EF87381E9655F1A520C623D184FDCCCDBDDC867C1C22136EED31D3EDF08F3D5F1AB93E0288E6F24C725789F107D066183A602FB3E73BA36351D9FG9BF63B9437335F2FDCF3F9A7831D72D420FA84969335167CCB77
	BB5B6B72B5B9D89B388ED62BA5C3D25B0154AEFC2D3254541C35989EBD2AC7DDFE2557631B64FC2A862B9AA52A0345FAADCC07BE2F6C6CD77CA340734E2AD0AFB19D1EAE8B046950A13BC76E3C8D9A187CCB0EC9AA9F6221E16FB9EF5061F53B1C72D435CC5C2A16BE40A623DE7F0462A4AA544A57D5A63F1D10EF49820D9237C1DB75BFB39D4615112B2083F43FE35C6B8DA87E3F2F9A2FCF9770B75A5456971963E95036096B3A316C41D0DD3BBC386BF0B9F7C85FD0A1CCE70D7BAC4A9FE33ABFF9489FBA567D
	DE97B87A1D9E6963BCEED10AF0FD5802EB506E9413EDB75F4BFBF7A31D6F65ACE069703A5DF4E9835AC7F6633AD5FD2CD03796356D2679159CC13FBC8BFD7EAC7438D968890674F5702474570E9B51B78A7AD30674AD428E477764619E203CB129405182C77C02B2454F87FB7243E80332FBD2BBF7F46D417D502D0B03A87E64DE63B5796CDE0CFD2E8A7E6776EE13707CC0F952BDA4CEC61EBE3FEB8242C68C6CD81E6AFA0DBECF1537A74504B92DFEF8BA81282F95D76C0627F1B6885A6CC5BECF5BF8AE6B8634
	0B8608E73C1276D03B53FAF1D53C5E95A82BG51FB0D6A850E70FAEB213CB1E2BD6BD1DE4FDC14E232G349599557B0D28B78F68B1G4BD93D4DC57AFA8F0A1CDCDFB158GA02A1856DBDD9C22DF8D2F17378F7C9F4031AFD4EE54BE2CD75344F3D3F58257C9F04F42A015E470737F73F97A9C5FC0C2E01B511E0D30E69F653FFEDFF8DE64D34548D7AA8199A55C8FD76C5547566D0100F744C50E254C272E966977F30A73956D00A74C7E005A8B01D2107B6535A254954F2D8C4DFC6B33C74FC8534F61DDA42B48
	0FAE9D5749F2B3606D00233383DC3BC304BB03AE856387EA07373B75394295396DE9BBA358212204763F33C433C372DB9B52E9CE3060B8377B7C921EA70C96389582377B8447A5830E7667134D381F9965CAC7C887EB47A6DD35132A8B8F5732AA13A5D21F9B7D99B92FE0FCDBDD4A730EC575B8F71DD03E81908DC0F3A0FB2A32189DE38117D84A5717A9D93F3E146ECFB1F5820C6F67F3726DAA7427B90695EC3CD6D56553718DDB5D7998ACA9E8633E7679A5D24BBB65E9C63950285C6A349B7B296D176E5746
	E0D32BFFDBD4D9A24B59BEA97A1784F5ECE53CFFDFBDE5543F21B25E3F36A33752BFAAF35191AED385FE31C2E61455C8E6E2999F0B7E233C5DE640598F70F60F2B7AF66387E83B8587382EDFB95AG4F39EF3DBC8FD6D84D65AE851E89C22E432217DBE821675F968B177BD6752D4A25FE70464EEEF87EFD82935E17A7C4DF7AC14FA8G3A4FFC31CC3F4F2C34D09BE69D04B57BA06FCFEB4D6D7567D06BA9FDFF669A98CA3C8F0BEAF89FCE02EE3E03FCBC0AFB0C7CE019507FC30D372ABF4B53B7687700156B5B
	8C4FED88AB8E32BC7D4170BD60E6892EBD1BGAF1573BD60F7C53BF4FB40CB306A4A1EB108AA4459389875077801156FABC639A4FE10F83C7775D26CFBA1703483B8G06G668999C7764D1AEF6F8A4E10EFAFBABE173C7962F21E4F78641C5A450BF67BCE4C255DEA516E17A4471ED02E650F12F46F6907266715E88E7CA3E47DAFBAC4F36099827F89022F5661298F1655F39FEC835A52C3DCFEE9D5385E660AD0199595C6B94EA3535A5506159DE16D681CBACC62BA32976A165E66E66D0CCB6777E3757A79BD
	D4C1572A79700C2960FBED1CECE6FC63AAF87EFAD799725F88657443A926D20013GD7G02G0B8EF37F1A5AE794E3047AD724GC34BE1D2DB7C7496ED78742CB6DCF1B8349D6DF752A95E6FF50779B8AEB9F2EBBA44CDEB972EC3D625118E5F34F29DAAAA398E03C7EFCD876734F661BADC29B47225B1F70931AF71B3AC53B5EB1247CF2A50F5DF752D69EA1B562EAC2C9CEB286B5941C83A325D05697A0E056B3ACA687A54AD6A3AE9D6DD37986A3A38692CB4FB3C6A999907E5BF34935A96497D64B7E5BC9EAE
	347258755715389675C15989F043AAF249536E235FDDBF0FCFEB976EBF51D6A39F6E6F61360E35727563AF87485DG14132DFC2D7851BE4CA9737D0EA4C6A43E57FD20844F46DE3F8BAC41F1D184B762751324AC45FD27B4F23E7811D263FC715AD263FC710FCAE73ED3F92AB4D2BE195EFF5411F3CB3A8DE2A5G0A0B9338A6012BBF41F18E017BB3B90BDC0132AAF04EDA444D03F234407D228EF14B31EC63637B31FA44A5C3B92F0A636EA5B80CFB708CCDF19575116EC1309D36B7223DDB20B24EC41411A15D
	7EDD4BFE426361BD39E41C8281A5C0B693BC5DB15A2F545D12C10E7E85F9C211F8BE71E2D5A62B5325FABB1A04EE4FD695C906EB89B9B4D25F5E5E40FD3B290A4F3B7B3AF05E3921BC9FE0119803AF49C631CBFC953B672C1AEEEFF6F7D79659274B4B0CFD5AD606F3A9601D740FA861FEDBC7E2FACA53F65BDED519C033301C95E92F4A7EB1E51A5F3712761496293D23AC525C20B65FF3C4EF73FA61AF4F1DB232D18233CD12010DD8DEB504DF0360374AEA44AF7D8865D3F4A4923FFFBC2A6757A8786D1AC4FE
	55866D905AGBF90C6237C7ED22D67F7DC701BEF8957EF9C5A5FE0BC96C454EFF70D1E5FB141AF29EC2678F82981BF9A4F7EAF79B6E479919A4F268A7C987D16C40727F1EED2A57F26EBAD501A0E7233545F9A75FA1D90FADD6AC13DE20E9A5BAD9670AB43E8540F868F200C20A83FC24AD3921F23CAFB0E7045AE0F13ED9AB4DF57CD565AD36912F4AC0D633ECFF08FG6E9909639CE47D3DG38CF24F27B9656E27ECA75FA5D83AE9F464C523A23786D610ECAC90C41DB35387F3865A96FA448F4D7A32F0F556A
	6369A24035818CD4533ED11D7F63BA729E754B93B0A5AE7A149149D1C33E132B63ED63217EEA000DGE62059EA38FD6FFF8169F48562C7FE2E4F773661B71591EEC558B8E4B5687DE38170DDDA2359FDF9CDA47F98694671DED3C37D20D8600384EF2E557054BF9EA0EBA016BB0116D747772BB564AC11G65CD82D7BF4C77304D8217554C6BB96C9CB7514C6B8D885CCF2C3C5ED201EB3171FAF182E7A33129D41FE2AA2A6738FF1A6475AA842E0C6827C2B9CA60BECDE245C5D00E9338C402DB8565F58257FB10
	73DBAFF05B8FF01C3D016F83CE2BA46645046E7D8F15B3FD17F03BC0E5644E786D436D826D331B44FB73203FB18D5C8EBF6C61F6D8AAF03FA736C9063239115F01BF762ABE171277A9FB9ADFF742E515B6D2DF35B506FFB3DBBF94F276456CCDF3F30F5F65BCA70F751159CA776E65A41EC25ED79A795E5DD907FE9B8D65D800D5C22FE0A54FEF475E3FC46A1B770A6137306B875A539218BED21326176F864FEF175B394C96202BCDA4BE366B73D3B8173F1AC6E3EBCAFFFF889D77D50CEDB4FAFC889D75BDEC52
	53FF99C2C79BD9D9FB5A37C78F733EEDF9B4CEF2FF499171BB5F952CCF6B5953B6F9B5F5693144F4BA1E07C8ACD4F4AC45D4FD0C0F7147497C6C0732D3603EAA66E754B1BEA7DC5DF6F8BE8165E40042633027G748328GB1GF1GEB81B681E41DG99A7F85B1B242D834A63GD1A768FD925EE561BD553293749EEA7589FAAF3471843D7721ED9F6CDA9956C754E1AEBE0F3A4CC4C7FD13F5BC7BD9DF264D1D63345FE4BFB320F3C03BD4B319B12C9E6D4B4F2775E52E71755ED9683BA56D9BB9041D4C9F436B
	95C85AB7F4B3FD1B047ED74C5E755F6495C89BE43C9BD5E163257B6EAE9BFAEA4BC932DF8FF74E40131FABB81821CDA940A1C633433B0B79B7655DD425711E5FBC9BEFBD7F79E13CE32671FE7908467B22EDB65E5BF53C170671DE2E634D726E1346B14A06231A4C7BA7E71319296B532AB019CF407B4BA1B20F492823C2C28B2714CCE47449BC7710F81247A9DFA07B6304C71970FADCA3345E438477A84049D33C5E1FAC2163FC34C653FD6860CC3A1B4220D427BB6A6BG99D676CE79159D180D9F7FAE8F5F87
	207D78A94A8F4BB7603D784FDB373A04ADBE50E3EC7FFBEA35BE7C3AE7B67B6F5459BF6AD4287D971D8A377F635D536DDFE2913F69B84573ABEDF88D47E26697AA8F43F3D0FE5D700677BCEC11AF8E3F8E073881F9CC62EBC9C95DDC3F07277160F232FFAEBF95FE0E7896412FBA95FE0EB8DFA74E91C0EBEA667A8F59B1960E01F2BC409A00C400745330361F6679220FD7623ABFE3EEF9406B6B030333DB173E217D335AEF6DFB7FEFED67FBE57C577EDDDAFE17B962BDF753E93AB68F30674A4055D4FC527EBD
	CB726E73013698E095C082C092003905776F294AEA5ABF9C30F6D515C74649EDBF6F9CDE40EBE8BCC8D09FE9EE40F3493057CF8E6D74DC3263B4BFDB14827FA6G9F407C962A9BED77B96B74734CAAEB78F9E6913409E36D280F2F1DF1DD635F2B72F93A9A5AA634E873AA895E1F5D3AF56BB3GD48FD3B936BB5FA78FABEE7EA59E57F12AEB1720693E9494FD28D07285DD7FB5A327C7E56377BD61DF5AD15AEFDE445F9698FF8D495B770F6D8E29E32443E746F6E8BAG0D635F4E4A8FE99B5E370E09EC510EA9
	A6F8B65694C83A331DAE81CC691B1E576CC20EFC9E1EBF2574E7DBF2747DE6C4FABF7A35EEFDFC3825055E0FDA4E707B53F3B70D68CD02DED1E7C4F7887A0B15C674AB027E6CFEA33ACF506F359A5167837D00C4E38DF236A37FA535CE47713DB88153B8EC9E7FAC0B4F63524F5EA7F57E581C46770DF07F0DB9C3FDFD6399AD1EDF33A4B15DC8B615530D6998A9B27D1D63B3532DF884E263B6C263A07F24C275DEFCDDFFBEBA3923BB62DD3365AC7B6DA2FBC65FFFB51572DCEF4127D531B23B5CFDAA12BE11F5
	0CEF1E398FBFBE2D752141427BC0575E07485EC45F3F2FFB6F2F3B7367DF579D15C157ED17BDF23E56E7B74E57DE339B5FC15C304F7C4D7AFCFB64EF56BFEC57FF33CE771DFFEB413964861F08820885D889306EAC5FF30A6B4BA53267986CB7DD4EB14C97632F2ED4AEFFCBFD643E1A9AE67F2DD6D6832E1543DED575CEA0FF0E4FEFA07708DE9FC1D25C5E8634115FB5B62E522A8C3F1F1CBB5D4AA8C153382261B47F2DDB92744D564A3F917B468E0C995E55B7E283AE552D1CA33BA8BD3B3FF91A5F458E3572
	FC41F3A48F9A8365B800552D7C4E7C57D6FE4EDFAF6A96115F4D953525186CG0E36F73BFE71B9550C57057CEEE2B4425D845981288E3F6B66F793C3EDDC2F291B9C97A3F02F56F15CDA016B2B62B8F3BB479DA1F70CB886ED82374E4AF141F69E3B59DAB19E0803729A00A4G73B908EB811AG9CGD7GFCGD1G0B816281D683A4GEC8210BA60AC5C41FD72A0B92099462F00E4099D6218FE196EF9D40F0F3621EEB66053A9F839CF9E1D01D73F5F6BD37C6A9445122FFB190FBD5892594F3F5CB23B1FFF33
	05FC2F897EB9B065E3F7A42BDA481D6668E8C09173696116BC016B8D74FB83AE3CDB21EB57495333452C4E8E23FB5B6ED95B055FD753F66F10F673BA5EDDBEA43A037F1E54C56E8C16F5041F817C74772A9D61E700BF093B0415C02B68643919E78C6FEEE682764D8E19E7D11D61729792FCF4E7387CB29B173F98E869DDDC7E9F3A7572473B289DEE3067022E69F6E179281B7AF6CBD93D656C39A6E23B2110B357FAD6AF19BD4D5D115A7DA5647B16D2D64F4E1E1D915B55055C95DDE6755C6CB98F1E515DC67E
	74E44838ACE3F5D63067DAF8A60635233E5D529AF98EF5344EBE077ADA0D770A17DA276F95AF379A6F952F356A778A0A9BEE45B5F58C56114B225E18407DD5605C04A762DEF6B955F10177933D906FD0D0A0478709DE57896EB2433D2D437D15615E5F0A6BGF4F95CAB3EE37807D60C19C6DDEEB75B4BC99D17871685E10F85C4C717CF56693DD060B43D6FE943B190B184DD56986DD3ED05927D0D1ACBF63B5EA41F18DF040A1EB1FAFF3E32A01B505DAE0F52C1FFBDAD71BC7F4749FE389946BA2F0767F65F2E
	C4DC8514CF7670F964555DF184F56F29F774AE02FDFF747FA063E3B62DCF1F563DD7586E3C6CB3F7D4869D53ECF2F7538963B9A046F9CB8E6246219C94B8B9176775678B5C421B3C6DBA01FB69A0EF3BD1609E95F7E74982F71D44A51D8E106B603825E652964AC1014B4C94F285EE1C44AF6B201CA8F023E46EEF063239D748ED65ED8B852E1B44B9CEA82B8277BE876222209C2355536E34846E35B2FEFF18ACF0C9A4C6B21F077D71BC4755F9495F4201F20B40C5FBB9BF0740BD544D6F48E24E731C6967CE63
	BA24E2F85A6DA5718E4D83FE174C47D82837DC540D2E4C60F5A5FE8E6EB807BE4A72A5BDE41D81B614C7B36953C6E81FACF894640666E8C771CF52305CCFB0F7365C0FBE279D1E232D3810E24275E5AB7CC765BFC47C458E7896GAA6FC31D1A3C1F4F1A934E18C11E7B827D3BC3DBC52E6923F5217DDFCAFCAE887566DF607D7F3AB9347F31DD1ABE164C5B49D147DF20EB24264FBBD5217AACA4FE3C926A2D917A3C5D9A2A4FE35DBA7B344E244F76D96C3385E460F9734FC21F574AC27589907FB67741FC6963
	7A44B8C27579FD0F264FEBB3663F31FD668C7AF476E963C5655FA53EF36C9772375BC2F55CCF6E9707006E96F53A3D21BA166E54F47C2CF7B69DF74E2063A210F1AAC447CD5DA29F5F47FDF8E817B6675E28443FDB65D12EDB440F8BF09E5B4A5BA160F117D7DB6A61FFF26621EDFFD62035CD4AA9B7EE6B04265EB1E8ECE70DE95BF4CCD732777F5A2B7169BEB8B7BE4F95E9FCFED4227109B14F0D4F7F14EAFCAE9D50783C52BAB7BEC10B4667C95DB7B9130E397159E2555A5EDB4673D46C9E6D08C6BB641D9B
	5FED55DADBD38F67CB7F66597BCF2E858709FDDA4A0035DB15529A6D704F09E32A148AE0E319DFCBE0E4DCF1CE3A954BE4C0F1DAD42FC5890E38A71D0AE584647A3DEED54A23DF78D8B05FEB41EFFCD4298602BB17536243742EE89800332CE2D147954BB05644D04EC2FFE56A17F2C5F7FAC9EC397EDAE333E943FD26B4A93DD75AC6E36DDEA99D8366DEA90344533D1289E26D200F75AF0D4D030C409B52CD3242246B3069821BE608659CF0862589AC5641F370F3G5982EB17349DA5D3E04DBA8E9A5F9C4681
	27DD1AAA958A9BD958995F2584G0D69F70B4BE341D47C241BC43F4D2A140DD9FD8BCB6BC339005F84D8C87DCBE0D740269ED6A5B39BADA86669C420791B413A59A14E725DFFBD764BB5CF8D86CC5236DE2998572B0B0A1F045D0A93BFD9E5FFB728DB4AA46307FFAE82CC9FA00F0C00BA65D67A2442511770774781D5F13673009EBA321B7CC42A8E93D7F58EB9B02E22E31C6E1352BD78970E24FCCAF64B1E31BA9A5D1FA663175ABE04EECA2C158A7A9690FD11CB2D60D270755C43874FA7AEB7C9E95D528EB7
	D826DB9E810F6A15B255F13F9298773A1DC1DFEB9BE1E43EEEE64BE5BA30AB915D474E56624507D07AEA705E0FE4EF4C6E06218B7A25BAD2931767DAD14359B1AE7BC114626FD571AFB6F97D9502763EB2357287BDFB2F24C9292C13B87045C11FAAED9B15A7DC6EA9F869A4D3133844FE9534754B1EG7E2A8BB43304E946C9C2C06DBD8F5B97BE7047D708G2D29AA9D20260426F5E30AF7222EFDF49406DCB0G3B827B7D02BD56298D2B139F3CFB777947378E039FF4CA26893B1D7CDBCF7EED40FF6B89261E
	E06A9B4038C9520C7F0553033C4F5B68C1D34CF3F4005AEE98C3753F1FFEF849CFDA12B6C17F3A258C329A40B064E935DE181C98D67CF745AE5C3FB3FA7D07F114FCB8441CB3F1CBD2B80ABE94443703D46E5EB04722EDCA26F90889518744B8E1291CD42DA07FDA675CEBECD59B26D3E3B0E429C14065A9633D27E9F3B0E481C5CDB14458765E5334DD531487135A86701E525421E90A69935CA66EF9EFEA7AE22826E85373925F505ED31A8EEA1A7230987DF59B3D1F2CFAC7FFBF39790356087713ED036C7B08
	41705F427E16644A97GBEF610676D4F366007C1113EE139B8A13B5D2A6C3E5A6EF16277AC6726D4C53BA323F75F81DE0791A96D0FAD28776B3AB67F87D0CB87880023D545B39CGG74D6GGD0CB818294G94G88G88GABE746AE0023D545B39CGG74D6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGED9CGGGG
**end of data**/
}

/**
 * Return the JCheckBoxDayChooser property value.
 * @return com.cannontech.common.gui.unchanging.JCheckBoxDayChooser
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.unchanging.JCheckBoxDayChooser getJCheckBoxDayChooser() {
	if (ivjJCheckBoxDayChooser == null) {
		try {
			ivjJCheckBoxDayChooser = new com.cannontech.common.gui.unchanging.JCheckBoxDayChooser();
			ivjJCheckBoxDayChooser.setName("JCheckBoxDayChooser");
			// user code begin {1}

			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder2 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder2.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder2.setTitle("Days Available");
			ivjJCheckBoxDayChooser.setBorder(ivjLocalBorder2);
			
			ivjJCheckBoxDayChooser.setHolidayVisible( false );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxDayChooser;
}

/**
 * Return the JCheckBoxEnableBaseLine property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxEnableBaseLine() {
	if (ivjJCheckBoxEnableBaseLine == null) {
		try {
			ivjJCheckBoxEnableBaseLine = new javax.swing.JCheckBox();
			ivjJCheckBoxEnableBaseLine.setName("JCheckBoxEnableBaseLine");
			ivjJCheckBoxEnableBaseLine.setMnemonic('e');
			ivjJCheckBoxEnableBaseLine.setText("Enable Base Line");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxEnableBaseLine;
}

/**
 * Return the JComboBoxPoint property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxHoliday() {
	if (ivjJComboBoxHoliday == null) {
		try {
			ivjJComboBoxHoliday = new javax.swing.JComboBox();
			ivjJComboBoxHoliday.setName("JComboBoxHoliday");
			ivjJComboBoxHoliday.setToolTipText("Holiday schedule used to exclude control");
			ivjJComboBoxHoliday.setEnabled(true);
			// user code begin {1}

			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized( cache )
			{
				java.util.List holidaySch = cache.getAllHolidaySchedules();
				for( int i = 0; i < holidaySch.size(); i++ )
					ivjJComboBoxHoliday.addItem( holidaySch.get(i) );
			}
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxHoliday;
}

/**
 * Return the JComboBoxPoint property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxPoint() {
	if (ivjJComboBoxPoint == null) {
		try {
			ivjJComboBoxPoint = new javax.swing.JComboBox();
			ivjJComboBoxPoint.setName("JComboBoxPoint");
			ivjJComboBoxPoint.setToolTipText("Valid point used to store the base line value");
			ivjJComboBoxPoint.setEnabled(true);
			// user code begin {1}
			
			ivjJComboBoxPoint.setToolTipText("Valid point that the base line value is gotten from");
			
			ivjJComboBoxPoint.addItem( LitePoint.NONE_LITE_PT );
			
			DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
			synchronized( cache )
			{
				List points = cache.getAllPoints();
				Collections.sort(points, LiteComparators.liteStringComparator);
				
				for( int i = 0; i < points.size(); i++ )
				{
					LitePoint pt = (LitePoint)points.get(i);

					//the true qualification for this statement should be 
					//  (Would affect performance too much!):
					//CalcBase.updateType = "Historical"
					//CalcComponent.ComponentType = "Function"
					//CalcComponent.FunctionName = "Baseline"
					
					if( pt.getPointType() == PointTypes.CALCULATED_POINT )
						ivjJComboBoxPoint.addItem( pt );
				}
				
			}
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxPoint;
}

/**
 * Return the JLabelBaseLnPoint property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelBaseLnPoint() {
	if (ivjJLabelBaseLnPoint == null) {
		try {
			ivjJLabelBaseLnPoint = new javax.swing.JLabel();
			ivjJLabelBaseLnPoint.setName("JLabelBaseLnPoint");
			ivjJLabelBaseLnPoint.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelBaseLnPoint.setText("Base Line Point:");
			ivjJLabelBaseLnPoint.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelBaseLnPoint;
}

/**
 * Return the JLabelPDA property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCalcDays() {
	if (ivjJLabelCalcDays == null) {
		try {
			ivjJLabelCalcDays = new javax.swing.JLabel();
			ivjJLabelCalcDays.setName("JLabelCalcDays");
			ivjJLabelCalcDays.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelCalcDays.setText("Days in Calculation:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCalcDays;
}

/**
 * Return the JLabelHoliday property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelHoliday() {
	if (ivjJLabelHoliday == null) {
		try {
			ivjJLabelHoliday = new javax.swing.JLabel();
			ivjJLabelHoliday.setName("JLabelHoliday");
			ivjJLabelHoliday.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelHoliday.setText("Holiday:");
			ivjJLabelHoliday.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelHoliday;
}

/**
 * Return the JLabelPrimeContact property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelLoadPercent() {
	if (ivjJLabelLoadPercent == null) {
		try {
			ivjJLabelLoadPercent = new javax.swing.JLabel();
			ivjJLabelLoadPercent.setName("JLabelLoadPercent");
			ivjJLabelLoadPercent.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelLoadPercent.setText("Load Percent:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelLoadPercent;
}

/**
 * Return the JLabelPerc property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPerc() {
	if (ivjJLabelPerc == null) {
		try {
			ivjJLabelPerc = new javax.swing.JLabel();
			ivjJLabelPerc.setName("JLabelPerc");
			ivjJLabelPerc.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelPerc.setText("%");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPerc;
}

/**
 * Return the JLabelNormalStateAndThreshold property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPreviousDays() {
	if (ivjJLabelPreviousDays == null) {
		try {
			ivjJLabelPreviousDays = new javax.swing.JLabel();
			ivjJLabelPreviousDays.setName("JLabelPreviousDays");
			ivjJLabelPreviousDays.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelPreviousDays.setText("Previous Days Used:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPreviousDays;
}

/**
 * Return the JPanelBaseLine property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelBaseLine() {
	if (ivjJPanelBaseLine == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Base Line Properties");
			ivjJPanelBaseLine = new javax.swing.JPanel();
			ivjJPanelBaseLine.setName("JPanelBaseLine");
			ivjJPanelBaseLine.setBorder(ivjLocalBorder);
			ivjJPanelBaseLine.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelCalcDays = new java.awt.GridBagConstraints();
			constraintsJLabelCalcDays.gridx = 1; constraintsJLabelCalcDays.gridy = 3;
			constraintsJLabelCalcDays.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelCalcDays.ipadx = 6;
			constraintsJLabelCalcDays.ipady = -1;
			constraintsJLabelCalcDays.insets = new java.awt.Insets(5, 3, 3, 4);
			getJPanelBaseLine().add(getJLabelCalcDays(), constraintsJLabelCalcDays);

			java.awt.GridBagConstraints constraintsJLabelLoadPercent = new java.awt.GridBagConstraints();
			constraintsJLabelLoadPercent.gridx = 1; constraintsJLabelLoadPercent.gridy = 2;
			constraintsJLabelLoadPercent.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelLoadPercent.ipadx = 11;
			constraintsJLabelLoadPercent.ipady = -1;
			constraintsJLabelLoadPercent.insets = new java.awt.Insets(6, 3, 4, 35);
			getJPanelBaseLine().add(getJLabelLoadPercent(), constraintsJLabelLoadPercent);

			java.awt.GridBagConstraints constraintsJLabelPreviousDays = new java.awt.GridBagConstraints();
			constraintsJLabelPreviousDays.gridx = 1; constraintsJLabelPreviousDays.gridy = 1;
			constraintsJLabelPreviousDays.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelPreviousDays.ipadx = 3;
			constraintsJLabelPreviousDays.ipady = -1;
			constraintsJLabelPreviousDays.insets = new java.awt.Insets(5, 3, 5, 1);
			getJPanelBaseLine().add(getJLabelPreviousDays(), constraintsJLabelPreviousDays);

			java.awt.GridBagConstraints constraintsJComboBoxHoliday = new java.awt.GridBagConstraints();
			constraintsJComboBoxHoliday.gridx = 2; constraintsJComboBoxHoliday.gridy = 4;
			constraintsJComboBoxHoliday.gridwidth = 2;
			constraintsJComboBoxHoliday.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxHoliday.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxHoliday.weightx = 1.0;
			constraintsJComboBoxHoliday.ipadx = 43;
			constraintsJComboBoxHoliday.insets = new java.awt.Insets(2, 2, 3, 57);
			getJPanelBaseLine().add(getJComboBoxHoliday(), constraintsJComboBoxHoliday);

			java.awt.GridBagConstraints constraintsJTextFieldPreviousDays = new java.awt.GridBagConstraints();
			constraintsJTextFieldPreviousDays.gridx = 2; constraintsJTextFieldPreviousDays.gridy = 1;
			constraintsJTextFieldPreviousDays.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldPreviousDays.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldPreviousDays.weightx = 1.0;
			constraintsJTextFieldPreviousDays.ipadx = 52;
			constraintsJTextFieldPreviousDays.insets = new java.awt.Insets(5, 2, 3, 1);
			getJPanelBaseLine().add(getJTextFieldPreviousDays(), constraintsJTextFieldPreviousDays);

			java.awt.GridBagConstraints constraintsJTextFieldLoadPercent = new java.awt.GridBagConstraints();
			constraintsJTextFieldLoadPercent.gridx = 2; constraintsJTextFieldLoadPercent.gridy = 2;
			constraintsJTextFieldLoadPercent.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldLoadPercent.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldLoadPercent.weightx = 1.0;
			constraintsJTextFieldLoadPercent.ipadx = 52;
			constraintsJTextFieldLoadPercent.insets = new java.awt.Insets(4, 2, 4, 1);
			getJPanelBaseLine().add(getJTextFieldLoadPercent(), constraintsJTextFieldLoadPercent);

			java.awt.GridBagConstraints constraintsJLabelPerc = new java.awt.GridBagConstraints();
			constraintsJLabelPerc.gridx = 3; constraintsJLabelPerc.gridy = 2;
			constraintsJLabelPerc.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelPerc.ipadx = 8;
			constraintsJLabelPerc.ipady = -2;
			constraintsJLabelPerc.insets = new java.awt.Insets(5, 2, 6, 147);
			getJPanelBaseLine().add(getJLabelPerc(), constraintsJLabelPerc);

			java.awt.GridBagConstraints constraintsJTextFieldCalcDays = new java.awt.GridBagConstraints();
			constraintsJTextFieldCalcDays.gridx = 2; constraintsJTextFieldCalcDays.gridy = 3;
			constraintsJTextFieldCalcDays.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldCalcDays.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldCalcDays.weightx = 1.0;
			constraintsJTextFieldCalcDays.ipadx = 52;
			constraintsJTextFieldCalcDays.insets = new java.awt.Insets(4, 2, 2, 1);
			getJPanelBaseLine().add(getJTextFieldCalcDays(), constraintsJTextFieldCalcDays);

			java.awt.GridBagConstraints constraintsJLabelHoliday = new java.awt.GridBagConstraints();
			constraintsJLabelHoliday.gridx = 1; constraintsJLabelHoliday.gridy = 4;
			constraintsJLabelHoliday.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelHoliday.ipadx = 11;
			constraintsJLabelHoliday.ipady = -1;
			constraintsJLabelHoliday.insets = new java.awt.Insets(4, 3, 6, 74);
			getJPanelBaseLine().add(getJLabelHoliday(), constraintsJLabelHoliday);

			java.awt.GridBagConstraints constraintsJCheckBoxDayChooser = new java.awt.GridBagConstraints();
			constraintsJCheckBoxDayChooser.gridx = 1; constraintsJCheckBoxDayChooser.gridy = 6;
			constraintsJCheckBoxDayChooser.gridwidth = 3;
			constraintsJCheckBoxDayChooser.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJCheckBoxDayChooser.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxDayChooser.weightx = 1.0;
			constraintsJCheckBoxDayChooser.weighty = 1.0;
			constraintsJCheckBoxDayChooser.ipadx = -372;
			constraintsJCheckBoxDayChooser.ipady = 52;
			constraintsJCheckBoxDayChooser.insets = new java.awt.Insets(9, 3, 48, 7);
			getJPanelBaseLine().add(getJCheckBoxDayChooser(), constraintsJCheckBoxDayChooser);

			java.awt.GridBagConstraints constraintsJLabelBaseLnPoint = new java.awt.GridBagConstraints();
			constraintsJLabelBaseLnPoint.gridx = 1; constraintsJLabelBaseLnPoint.gridy = 5;
			constraintsJLabelBaseLnPoint.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelBaseLnPoint.ipadx = 28;
			constraintsJLabelBaseLnPoint.ipady = -1;
			constraintsJLabelBaseLnPoint.insets = new java.awt.Insets(6, 3, 11, 4);
			getJPanelBaseLine().add(getJLabelBaseLnPoint(), constraintsJLabelBaseLnPoint);

			java.awt.GridBagConstraints constraintsJComboBoxPoint = new java.awt.GridBagConstraints();
			constraintsJComboBoxPoint.gridx = 2; constraintsJComboBoxPoint.gridy = 5;
			constraintsJComboBoxPoint.gridwidth = 2;
			constraintsJComboBoxPoint.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxPoint.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxPoint.weightx = 1.0;
			constraintsJComboBoxPoint.ipadx = 43;
			constraintsJComboBoxPoint.insets = new java.awt.Insets(4, 2, 8, 57);
			getJPanelBaseLine().add(getJComboBoxPoint(), constraintsJComboBoxPoint);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelBaseLine;
}

/**
 * Return the JTextFieldCalcDays property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldCalcDays() {
	if (ivjJTextFieldCalcDays == null) {
		try {
			ivjJTextFieldCalcDays = new javax.swing.JTextField();
			ivjJTextFieldCalcDays.setName("JTextFieldCalcDays");
			ivjJTextFieldCalcDays.setText("5");
			// user code begin {1}

			ivjJTextFieldCalcDays.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument( 0, 9999 ) );
			ivjJTextFieldCalcDays.setText("5");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldCalcDays;
}

/**
 * Return the JTextFieldLoadPercent property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldLoadPercent() {
	if (ivjJTextFieldLoadPercent == null) {
		try {
			ivjJTextFieldLoadPercent = new javax.swing.JTextField();
			ivjJTextFieldLoadPercent.setName("JTextFieldLoadPercent");
			ivjJTextFieldLoadPercent.setText("75");
			// user code begin {1}

			ivjJTextFieldLoadPercent.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument( 0, 100 ) );
			ivjJTextFieldLoadPercent.setText("75");
						
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldLoadPercent;
}

/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldPreviousDays() {
	if (ivjJTextFieldPreviousDays == null) {
		try {
			ivjJTextFieldPreviousDays = new javax.swing.JTextField();
			ivjJTextFieldPreviousDays.setName("JTextFieldPreviousDays");
			ivjJTextFieldPreviousDays.setText("30");
			// user code begin {1}

			ivjJTextFieldPreviousDays.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument( 0, 9999 ) );
			ivjJTextFieldPreviousDays.setText("30");

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldPreviousDays;
}

/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	CICustomerBase customer = (CICustomerBase)o;

	if( getJCheckBoxEnableBaseLine().isSelected() )
	{
		customer.getCustomerBaseLine().setDaysUsed( 
				new Integer( Integer.parseInt(getJTextFieldPreviousDays().getText())) );
		
		customer.getCustomerBaseLine().setPercentWindow( 
				new Integer( Integer.parseInt(getJTextFieldLoadPercent().getText())) );
		
		customer.getCustomerBaseLine().setCalcDays(
				new Integer( Integer.parseInt(getJTextFieldCalcDays().getText())) );
		
		customer.getCustomerBaseLine().setExcludedWeekDays(
				getJCheckBoxDayChooser().getSelectedDays8Chars().substring(0,7) );

		LitePoint pt = (LitePoint)getJComboBoxPoint().getSelectedItem();
		if( !LitePoint.NONE_LITE_PT.equals(pt) )
			customer.getCustomerBaseLinePoint().setPointID( new Integer(pt.getPointID()) );


		if( getJComboBoxHoliday().getSelectedItem() != null )
			customer.getCustomerBaseLine().setHolidaysUsed( 
				new Integer( ((com.cannontech.database.data.lite.LiteHolidaySchedule)getJComboBoxHoliday().getSelectedItem()).getHolidayScheduleID() ) );
		else
			customer.getCustomerBaseLine().setHolidaysUsed( new Integer(0) );
	}
	else
	{
		//Since the base line is not used, set the DBPersistant object to a new
		// dummy like base line so it gets deleted from the database if one already exists
		com.cannontech.database.db.customer.CustomerBaseLine c = new com.cannontech.database.db.customer.CustomerBaseLine();
		c.setCustomerID( customer.getCustomerID() );
		customer.setCustomerBaseLine( c );
	}
	
	return o;
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
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJTextFieldPreviousDays().addCaretListener(this);
	getJTextFieldLoadPercent().addCaretListener(this);
	getJTextFieldCalcDays().addCaretListener(this);
	getJComboBoxHoliday().addActionListener(this);
	getJCheckBoxEnableBaseLine().addActionListener(this);
	getJCheckBoxDayChooser().addActionListener(this);
	getJComboBoxPoint().addActionListener(this);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CustomerBaseLinePanel");
		setToolTipText("");
		setLayout(new java.awt.GridBagLayout());
		setSize(402, 348);

		java.awt.GridBagConstraints constraintsJPanelBaseLine = new java.awt.GridBagConstraints();
		constraintsJPanelBaseLine.gridx = 1; constraintsJPanelBaseLine.gridy = 2;
		constraintsJPanelBaseLine.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelBaseLine.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelBaseLine.weightx = 1.0;
		constraintsJPanelBaseLine.weighty = 1.0;
		constraintsJPanelBaseLine.ipadx = -7;
		constraintsJPanelBaseLine.ipady = -9;
		constraintsJPanelBaseLine.insets = new java.awt.Insets(2, 16, 14, 17);
		add(getJPanelBaseLine(), constraintsJPanelBaseLine);

		java.awt.GridBagConstraints constraintsJCheckBoxEnableBaseLine = new java.awt.GridBagConstraints();
		constraintsJCheckBoxEnableBaseLine.gridx = 1; constraintsJCheckBoxEnableBaseLine.gridy = 1;
		constraintsJCheckBoxEnableBaseLine.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxEnableBaseLine.ipadx = 19;
		constraintsJCheckBoxEnableBaseLine.insets = new java.awt.Insets(13, 16, 1, 245);
		add(getJCheckBoxEnableBaseLine(), constraintsJCheckBoxEnableBaseLine);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	//set everything to disabled
	jCheckBoxEnableBaseLine_ActionPerformed(null) ;

	// user code end
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{

	return true;
}


/**
 * Comment
 */
public void jCheckBoxDayChooser_Action(java.awt.event.ActionEvent e) 
{
	fireInputUpdate();

	return;
}


/**
 * Comment
 */
public void jCheckBoxEnableBaseLine_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	for( int i = 0; i < getJPanelBaseLine().getComponentCount(); i++ )
	{
		java.awt.Component c = getJPanelBaseLine().getComponent(i);
		enableComponent( c, getJCheckBoxEnableBaseLine().isSelected() );		
	}

	fireInputUpdate();
	return;
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		CICustomerBasePanel aCICustomerBasePanel;
		aCICustomerBasePanel = new CICustomerBasePanel();
		frame.setContentPane(aCICustomerBasePanel);
		frame.setSize(aCICustomerBasePanel.getSize());
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
 * setValue method comment.
 */
public void setValue(Object o) 
{
	if( o == null )
		return;
	
	com.cannontech.database.data.customer.CICustomerBase customer = (com.cannontech.database.data.customer.CICustomerBase)o;
	boolean usedBased = false;
	
	Integer temp = customer.getCustomerBaseLine().getDaysUsed();
	if( temp != null )
	{
		getJTextFieldPreviousDays().setText( temp.toString() );
		usedBased = true; temp = null;
	}		
	
	temp = customer.getCustomerBaseLine().getPercentWindow();
	if( temp != null )
	{
		getJTextFieldLoadPercent().setText( temp.toString() );
		usedBased = true; temp = null;
	}
	
	temp = customer.getCustomerBaseLine().getCalcDays();
	if( temp != null )
	{
		getJTextFieldCalcDays().setText( temp.toString() );
		usedBased = true; temp = null;
	}

	String s = customer.getCustomerBaseLine().getExcludedWeekDays();
	if( s != null )
	{
		getJCheckBoxDayChooser().setSelectedCheckBoxes( s + "N" ); //add the holiday column ourself
		usedBased = true;
	}

	Integer holDay = customer.getCustomerBaseLine().getHolidaysUsed();
	if( holDay != null )
		for( int i = 0; i < getJComboBoxHoliday().getItemCount(); i++ )
			if( ((com.cannontech.database.data.lite.LiteHolidaySchedule)getJComboBoxHoliday().getItemAt(i)).getHolidayScheduleID()
				 == holDay.intValue() )
			{
				getJComboBoxHoliday().setSelectedIndex(i);
				break;
			}


	if( customer.getCustomerBaseLinePoint().getPointID() != null )
	{
		for( int i = 0; i < getJComboBoxPoint().getItemCount(); i++ )
		{			
			LitePoint pt = (LitePoint)getJComboBoxPoint().getItemAt(i);

			if( customer.getCustomerBaseLinePoint().getPointID().intValue() 
				 == pt.getPointID() )
			{
				getJComboBoxPoint().setSelectedIndex(i);
				break;
			}
		}
	}
		
			
	if( usedBased )
		getJCheckBoxEnableBaseLine().doClick();
}
}