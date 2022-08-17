package com.cannontech.macs.schedule.wizard;

import com.cannontech.clientutils.CTILogger;

/**
 * Insert the type's description here.
 * Creation date: (2/15/2001 12:44:42 PM)
 * @author: 
 */

public class ScriptSchedulePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JComboBox ivjJComboBoxTemplate = null;
	private javax.swing.JLabel ivjJLabelTemplate = null;
	private javax.swing.JPanel ivjJPanelScriptText = null;
	private javax.swing.JScrollPane ivjJScrollPaneScript = null;
	private javax.swing.JButton ivjJButtonCheckScript = null;
	private javax.swing.JLabel ivjJLabelFileName = null;
	private javax.swing.JTextField ivjJTextFieldFileName = null;
	private javax.swing.JTextArea ivjJTextAreaScript = null;
/**
 * ScriptSchedulePanel constructor comment.
 */
public ScriptSchedulePanel() {
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
	if (e.getSource() == getJComboBoxTemplate()) 
		connEtoC1(e);
	if (e.getSource() == getJButtonCheckScript()) 
		connEtoC2(e);
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
	if (e.getSource() == getJTextFieldFileName()) 
		connEtoC3(e);
	if (e.getSource() == getJTextAreaScript()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JComboBoxTemplate.action.actionPerformed(java.awt.event.ActionEvent) --> ScriptSchedulePanel.jComboBoxTemplate_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxTemplate_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonCheckScript.action.actionPerformed(java.awt.event.ActionEvent) --> ScriptSchedulePanel.jButtonCheckScript_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonCheckScript_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JTextFieldFileName.caret.caretUpdate(javax.swing.event.CaretEvent) --> ScriptSchedulePanel.fireInputUpdate()V)
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
 * connEtoC4:  (JTextPaneScript.caret.caretUpdate(javax.swing.event.CaretEvent) --> ScriptSchedulePanel.fireInputUpdate()V)
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
 * Insert the method's description here.
 * Creation date: (2/16/2001 11:23:52 AM)
 * @return java.lang.String[]
 * @param text java.lang.String
 */
private String[] createStringTokens(String text) 
{
	java.util.StringTokenizer tokenizer = new java.util.StringTokenizer( text, " " );
	java.util.ArrayList temp = new java.util.ArrayList(100);

	while( tokenizer.hasMoreTokens() )
	{
		temp.add( tokenizer.nextElement().toString() + " " );
	}

	String[] strings = new String[temp.size()];
	
	return (String[])temp.toArray(strings);
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GA4F44CACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DD8D44735A6EAC2929BB551449F9271A7861598D4525A46741154A65A12BC5B6A2B2F31ADCDCC43EBC942EB73DA5BC71A4B4F8AA8A0C6B4A4C1DD7E9485E1C153DA448464C7C9C8A50ACAAAAA8BFB018D4B6E3AFB11D5513CB3B3774C5E3DFBF7958C7D9E5FF738F74E7C1DF3664C19B3674E2C1039B3C897971FA988F1BAC17E7BDB1CA0AC6990046FB535BD0FB8579769BC1F7C65GEC96B69CCF
	04E7G6435AEC957E588816549108E04F46D93693AF761FD0B703D7240337062239F8972264F1D98643F1F0CDC6CA70926AF1ECD05E7BAC090601682E47C1374FF5759AC999F8538F4791DC0DC3CA0AC25E5421333947866FF32F21360998290F6161419F5B6DB4ECF05F4BE009DE054D9D6B6901EA9143A51958765F22D630304B7FF30B9BE0D7425E89FA1D62E2BEF151F42E4AA0884213477C3F8D6B675F4FD54E16BAD69B0DA2DB62BA4F6F417741B2F989D269227E414440A0A139D8E33DDBA5951AD1A7AAC
	E21351AADA205E38D6C26F2F1313E9DB025082696CF384F7BE0548E8A5242381924FA14DBFCA2B14E91A5093A4C44ECC74C9F35BB9C66B28F3EE1AABE336259DB8CF5AFF1E0E8129379472E38198BF8B369F7641CF2F3143D96214991039734F5756D6644BF59E79B2A412FEC3A13D8CA012637FC8716B88ED9752F50597105F433B115F162B0F8B6B5A8F44EF7241EF558546E77785B73F21AFEDCBFB0F6ADCE09E511BD910378CE08D0050162E5383D481FCC0F57DCC6EDEF8D637CBB5365EDE1B355AEEBFE2B1
	B9BE3319C48B3CD7D4C00E0CBBA1F6589CA6C1E07A7C5B64E28DFA90DA5ABCF519G636D0C007A97F2E236A0C9FB63F59A3C052D5A92EF115B98516666AD3C0D486D47D4CF02601D545B88407011941FE8F46359381BA81E2473A1AF4008F2EEC9C7DA9A93278899E5FF0DCF5220E552B3C57151C6C6C3E23B1B96A663CB85C4468640B78218G86G82811634230C27676E3F110C9BC05F9CD62365E4DF3B5D68100E7419ADA651A14B7AA7E77D49DA8BD83DDF8D3A9E13CB70FE65982DD9742126DC56843F9F3F
	DA16C70CC2AE2C6F77DBFD751D51612D9B69FC8CE751B9115F4146B0016367D37C81851E0D6DDFA91E24EBA1AF9800156F5191FC2A0915CF616D38A83ED001E76D3CC55BA169DA488B86E0631B391B5855D49166A7C095C08B008BE082C03008E39C1DDBFA137328C9B13E2C6EA3F95B60A93AA40731418A36535AA11EB6BA4C46F60B689464B2737308DC5BBD6AAD22733B837AB8AADAC507D1B25B2C503A31D784E5F232B2CB72B613B28E1166B61BFBC5682057AEE03BCB73364053E2F4CA276CA6305A6E92AC
	BFA20F48641271B434888D407BBA1148ABBE055839E5703ED6C4D9D59792DCE5E73A2E399365F2B47F9DF8FE8A4AADDAAAAAEA4D9D048C2363B26A686A94D8E005EEA81F5585F68A60D8B72C0F5DB8C7DFEFC1DD2CCDFBC4A8DEF5C0D39729ADB4B33DEAB12BF57157340F91008F8288870884903E4435794B1496398D52BF210350B318F903C0E8D726899F2C0F113C8F125649BABB14BED2D4E9E257187C9F5FC3F4D88FFCD58234FE09E3F0FB3E1E69669123D3C4EDBC6EGC592CD6E91E1E53FAAA0EB0DF8C9
	34CA3522DD341A6045A96B429D85A9EEBD6D34F19D9AC37144EE5500A2B9B97E3E024D8C8FEDA8359E732750FC23A4B94C6DFD12F8D214A433354BDDFF86254569E1921DB22F8B7692DEA560F18CC0A06735E85796053EB55AECBDFD76E6B213E43DAD236BF3A814DF4647FFEEA2715522F9BA0C265D791509B8F6B78B242D41596AD3036EE3B07AA37B2040BF1BBB89328EA6713C6BB4AF35A7DDF7G2095E0D48F721F20EB5128B7945D75CDDBB66DC3593EDA4FEF8518EE6C586DDB378BF6EB6BF651EEED5DAE
	5B6DDFF77F3EDBCBF7D9DE55EE42232D5F2A188BF53B091D6C30D92DA21D103C0AECABDEEFA6FC182DE6690C5954A5CAA74DD7C47015FB08CE3FC7EDF1883C0777205D6C4FC33B19FFE53A90FEE113261FB569111278AC8B5355E20B5BEEB21A6A8B499C93ADE2AF882A4656E715983E115AF9996FCC203894E1BA29DB78892CFF6D36A3B6D73358EB374002D2EC247C1430C713686834B9FAC5D3611746CB4692E33FD4C24723241AE6571177BD7AFD7BBE52EBB85945DF2A9B3FD02DEA3551F73307761C5EE310
	F5EC0B30B0E1A62377C81FA4592CB55DE2C78F6B72EBA5582B7541D2DCA653DB377DA2233799F4345EACDACC75E60B788FD86B0BBB4040CAA574BFDB43A9292E92E7BFE8245CEA8D493D81311DE60758E03577E1AB50FBB9175713972D6E7EC9F9D9D8435879FE6CFB0DCD51F7B538B27F123E59BA3336084C01C6189BEDGAE8B2EB34B8AC9BE2EB372E2DAE333D85874053C988B191B9FEC48D44C37F1963677DF83CF169F2819FF30977337EF544AEF64798FEB6637717C1D29DA79EE1B78449EE2771D6245BE
	915CCE32D0FEAADEC65FF14FBB2A3C53C6CB1F5BA7DD3A07789E96459A4B705F5A2BEDB71E5ACB56559E5E7E53FB578B4437E4EDB2BB97EAA4ED5A9D66DEF0C90853A047C00E35AAE3A09270C762A5A1708C6F45FD7C07BDE46D575A1D87DAC99B8BF598A3D9CD648310E1E56DB4D965B889ADF7E9BD160B0234E003BD330D15EB33A94BD5A4E139D040AFA3E564F279F6E53937F839DA4037828C58BD4BAD3653B8C18A468ECAB96EA8459D02FFBD0938BF7BACC39943B1B9F7EB5AF68231F27B9997557B567776
	11F68D002F3A08BAFD28C869BBD5BB1D368EB35DB648637A1BA2255DE738B70AB64093E610937630A651053A71BFC504D6322F105172784EABA2319675BE294EE416EC8EF4CDEB7A6904ED36554056669828D78DDF96CF1B45FE81E3ADA7B6203C1AAF623AF72DDE99ABDA32A9BBFE238F590C11E59222104D311736253136FF425B0E06FC06EB5E0838FC8762D6F1DC2B8375B06982597BEA45133A7A4CA5FD12196C040C12115A9C2A202C2D0554B7A5EDCCF0E00C68979748DC0A0074DA00D8G36DFFD116EE3
	C959CCA75883A74E7749ED4A79BE4A497C5C90F8B2DBFAE4BD316903DF62F07C0E27D44173D697E2B6D6B336D449F4C64C1FA2D4FE75D61CD61CABE0D3D2FC38130DD11453BDD651130EEF8CDA1FA54776262DC7F99C1052F5968965719FB625BCE6C9CC9E91924AE3FD522D49E3F9FFB511472D2E738246A1BA12100FC820B12B8F7918770E120FA6403BG827B100F9FA4911BB14895D166E1A935735E2BA05234AB99E98A057E22B8CDE7CA15B469AF415E9D40F289E97AC672D0E812630465CA9B979E7901CF
	9BB762925309891754B66E2D923A57837C52CBE8636C451B95B66E84D89523358BD6AE3EC758C7E807B5CACE676EA37D52B5CA403D4A198D03092B2D3E0471118A8311D7B424137BE1CD81B88850520F344D59F743B8E11D4BCF1CF0752068F275637E6E0B0D0329B706577B59206A8571FA5F720A8D06753B774B4B7A3D774EACF6F7220D68611AFEF5CC6F82452F6BD7477456DE4018DE268B660B8B7B7F15CE5D7F9A17E71F6BD4E9D66F8D2FFABBD4696C4B1E75587C6A321071256B01C275ABAA78DEC9DE4F
	D63461DAF260B26A4C38B262BBD8A09DG90843084A092A096C0FF85F5E75DBE2DF555D3F7FCAD2A6E388C63731175C3134FA22FFAEA79D4DD51124F26136FE94B07D9D581577BB4FB32BC1EADD7F0AC4DC9C323355F2BDE09AABDCE13560242ECEDDA793E0C792BA3CFA97D5560AB723A834F48AB68BBF4E761F82F637C94FBFDD739B9FEAA3C6A2979B1DC55626779EBBEF49377DA327FBD3EC1494F312BEC7D116099F0957939379479893A0A7A3B02FEDFDD8D69282B282B27CA4877BB3441CDB633D592507E
	461712BD014DE16EB2DB9117C4F19DB6878D1830B81E157AE76B204D4C81EC770841F7FC2741209D5FB9EE500E6F1CB2780FDD1EB3780A7FB07A5E60749582FD8CE75F0838CA0E7BEF0EEB623897289FE802340B63661D275F29A19DB6003A5252C676EE2BC999004CEB69BA43B54C93E99E5963B4DDE3E3C576B1AC6FB797EE94EBA4ED29F1588F57A70DB2EEFDCCE83BD19FDAB1C40C7FE15AD78CF0BBAD3BFC5BA6BAC6CE276824460DB855F25E2B52D6C1A31EC682A682469532562748E56AA4DBCD9927FDD5
	1B3FF54D2E41379C4FAE732DFF534B34756F51B2B2971C36BEC70728563179E5D9BC4FED2D9BA4311730A16F35E9FD496868923DF4743B34BE4B73D57F39B2DFFAAC47DEBCE4D44EE5740E1D48E814C2961360BDC4CE07EB480835971424ECEF3F00F66877FB882D9AF6C831BFFA72D4325C7F4AEB38BFDAFD0CFE673B8EFBE9004A6B28633DD97E4630793AAF7ABA1215749D6074F555ED57268F5CFD1B45C22894F01DDA5E00F6307BBA52B92304FE330674E200951C4E7BCB7D519925C1A79B37D5654457F171
	74FF5274E5CE2FD81D4E473E4EEA120DAC0BA90C263629ACD1901E9290371E5A2122C45686435DC9F15300113F47A1AEB91560DE855C76F81459AB2D44BEDB0C17EDFD50C777D724DCAC0F5744828F19002B84E87A0A2D0372390BFE32B76FF498FBC114A76DE207D0BB0254FD66BC568D0572CB815682A4C2DE7E885CDB04DEA5FA6B95ED666398DFCC6AFF52E8EC97AD6ECC16B7321958C626D9EC37C696BB16B77EBC598BF9C705B11E677408570D994178899D01F6BA3A8D6D74D20E5BC37D164C5B52F54537
	A12E05628EC23A116326D3DC9B246D9C77029D5B9B388DF5A67C14D2E7A2G9FF91BDBCF5E3C4D177F5C58C0F4B079F666A7A7F1FC8B456B95F866BF7F024EAD12BE88F90137A3CD233210A6E1A45274C203BFBDEE9A690B26ED25248F53C87577FEBD45CBA3555F7B03CB70FB7FA8480B9A09B44DADC51A9AC7E10CA47F9C1E773183AEF8946ADDD91B078FC7B6659595C79DE65391E3D7A355E2B60FAD6712657A1123F09ECF21EDE60D0635966058E80CBB39F6E15CA9E4769421F144071AE714A21B33635BC6
	4BE714C63B772BCBF95CA96ABC76998E796BC6D399DDD06A9C617D49F8665F317CE39E79E4DE6C911479D7BC72893DBF77289F62112FD354E73C7D30GF90B7DE130E0F9A24F67792B2ABB98CFA67919717D77054565D9E9320D6F74E16369ACE57C4F5D00B6BE905AC83E1360920C35FEF48B3F25A32C04FAD1GF9F2DEAC3CEF955C5FAF3788D5F6E2F7A41B4D52EC361329AFB45DC94E70D5D6883C0C0D4E3BDE2B881BA9F347ACCA479D94779C4004BB518EAD36131836C4BE0A50B624C3248DC677ABF6FFF4
	A729680E12697EC026B58A5A58AC3FB39D7FA9DD7F5E84FCF2G6A7DB69A13AD06F49547CDAC4639E08A4079F9024E71B0C82F840861787394CF62B699F73198B6432FBDF6B35F23C9FB7E7231AFAD5EB78BEF5DD49FB77A4E6D6BFBB49B0329D903519D43DD1E3AD38B695F47A94760158614ED53DDA82B6CD22283C1BC3D0B26C91C1F2567178C765B3EBFA07DF84B72392F358F354CEB693A7CF4A92E4BCB000695GB1G99F703AD3C9BF95DFBEEE8E7558E8E315E45C157E36B0B65BC517B263B597A924771
	FD945F2A403335A4082FA55D1097F1B76EEF4FD45273FF77403A83E08298G8884D8FC8F6ECB4F6FA75F1D7D46E61AED76C658B8D894DBCD465FEB6707F6066F69A43459E15F19A1AC3E6365B8DF5FC1A26EE17A94ADBF033F77BE45B37EFEF3187017BDA6DDF7902095C08288G889A037C4D5EDF4C78A31FF12AA5495851CD3F90A1F364230D9BCD9CBD465B639E714A30B12C7FE572F34D98EF3B31137687F3DB403F180F3DF102EC4F03BF9638BCFE150A72481EB7D5287D694BF1F9BE6491A377DB758D777A
	9C9B7EDE9AEB675F4FAA635299D3B6464779E847A5579F25E8072D998F5949F3F747EE193620FEE51B87B764780CF5AF165B0A101F83FFF8DF66B506FC0610572E57CF91FF3C5D66201BC1660F6BCE635CCD3C97FC9900E600EE00917762DA362F485B0F2FAD42FA336EE5FE7C92F82E38174594180D8DA976E7E315E7FA68BA87F5E36FE5B61624B346266B3E8D7E48E13E1FD8EE213E3DFBE705AE381CFFE48BE917F98DF88A8273D63DC77DF72FD392683F3FEAD07B6F040FEC20C1CB3718EC3754109463B536
	61E6F845F545G6D560EF5735A826FDFG1F1363105E5117B7A13D6E138D72BE7255D27C9EE89A0B3E60CE035A97FCBECE694BFDEED07B02C40F5C79A38DEADFF02A07AF7804C16D8BE2FDB6E79D67857EFED571BE7B5C57397EE85B7AFF9B56BE18ADEB2B749D43692A540E6174D6EA47901D157E4F08DD2D74FDC66C76CA6599B1E60BDF2EA1E393883A3098E08DC08CC062B834439907F385EA07B5ECF01D290B44104869E2897B7F7DE15F3C7E79700D4FA4A7FCC43E7D375BE02B5CCB5AC7FC4AC7E43CA41B
	1DA299AE7DA3A2A307392BDBE2C5E5FC66C7C447ADE2A7C5B39F6BD93A875083DF06F168531E58CAF03510EE6638EFEDC5DF2CED9CBF03DCC766E7A824A3GE2G1247437C86B8GF08C2095408E90G90849086308CE08DC0B400F09F58397BD0262FFDCC76C2270DDFFA1FD4G243CE92402F598D99C1B5171E83D40F95CDFD5602B8D9819CFE0ABF43C7DCB31C3EAB6F6717A630F925E8A010EDACE4B652AA2BF34B4B9ECF651A1DDE6D8FAD2C776DD761D46BD33895A8A3C8F77412FEEA73C79BF2750EC16AC
	6291F9A9116DD25FE934CBAB6F43FD7124A3B48EF7FF3AAE9F209200FDB77834C67D9D249972DCGE36E47FD3A2588FD2BD900DBF3BFFECF88AB423DF066846C67A5F69E8B52A60001895AF65B37AD0FEF90B89FBB4AF02FB9E1506D94FCA288E81722BDE2F89150D664847F7BA2B61E3F2D71BD7FFED7F36379773FB574BC8E4C1B664BF6B91EAB54526FA71D1DCE114FF3DDED8647B5B81BECCEB30983337567679B48DE2593461D6DF1FFCF675E1BC03FB0917D8273D64FBD6E39CF896FECBDFCE16B0D56C32D
	BDAE7ADE0609ACFE9247E5F22A4E37CCDA6BEEAC9331CE5BA67554F95BA4FB1D36CD3AD42734C9322D2EA37A5E85733D05179B41F1DFF05CE85AA6411DB11B24EE0E3F0752C57047C452A16247D33A7AA92EC546BD2040FDA163265611F185163BED8E2C9BD4C7474EEC313074ACDA46EC05E1667218C3E9B45B0D8A3A67F31C1B6E870E127162EB95B39FF25EC3C7B385F67658EC34182F50A3E1A72120350B697F7E53B75A23D4CE545A33D65F301E7A7BB12BF70556EB1968FF0EA1D8A6621D20549AE2BBC6CC
	D46F7936D27C18096ABD5F43C750AECD023C5809E853BAEB54E7915B9E007584A0708134DD416F22ED89FB005F779BA06DC5C3BA71C140BD08FBE6C75150763EA306D86F5B032E4764F43A185E41FAD0AD3F8B94DF7520DAFED15C2EB7C2DEA867B72FCC59FF66A4B60E057233F21277383275BE41C0E45A847944E7E538E40A6B869C710319FDBA5484A8593F2E5FCAFCA22B582F67672F6A415895661AC017EDDD3945FAC3313E6C409E2CBB73F3D217FDC38C5F12C6755EE2360AB56C169527E5BCDD83C7C13F
	0113509FF93C0A1EB7027452C9486BBBF46F405EBFD03C4FBAB27C7E72D3435A873BF7FCDB8D1EEDDDB1095DBB0E02E72CBC061913F1BE3C190BBEC371E41C8F4F945058BF2487GA6CCC619BD762ED2BFC2G9FBE592D93ABA77B7A1650F30D3497B5D97DFD231F6205A96A6F9BEF8D60770DAC489B18027D5363C9328CC5457B6BC543BFE6C94543598753538A6A77C4823FB1D3D0F7974693DC62D470FD27A26E177168DF6B27224FE85CC5E4A62F85F409E378ECD71A9B0FCB1CBC870369F9CF09F812E46E41
	BB73B542699C32C35B83D35157F836CA07E5851C134F1E9078184D2DC11B9282F556CEC57D7ADD8DFEF34D8EC47D7ADBADA95B8CE98B40A80E7F8B4587C2BA8CA0A290477DCF39EA3B38B6502DEB6B82FD695DDF29CCD29FD21FCB2B217842075467521695703B2610977C904AFBA9DDB3D29F86F9BF0C380BBB683E8752AD9C973313FF93FC187F3E40B56D787EB50A0F00F2B1G9953781D2EEB439F5F4F3E29BE069A5FE77A72F919277F5AC5F55BGFCD6CEC31DDA9E6F693F7E57C922CF4CFF7DC37C2D782F
	966803687DDBEF3F7DB6EB7B814232CC5B4CAA72DB909AEBCFB5B82D96D8FA4A6425C716D7E84D5056736F8E315E9FFD56F3CD73F7C7FB9E1DBFE326293F59BE416655B475B75B4335387F8C013C0CA074479CF5749BEF10FA5EFCC57163025473A6318E67CD8864E5CC47791011C746DC02F4G470D4D47B992B21D5F635C657B3CD4834DDB81E5E3263318E8468C7EED3BE0784FCB3DFA43BEEE653C945347975F750E8B7F6EDDF5DC58B0C39D978E58619D971E36836B554EE0F161D6F85AE730FAECEEA569EF
	A5AE9CB0439D97E6FE770325CA3F1B1CC1A4FDCD00F241B370CC7AB3575471DDD2A61C673F1221BEC7CF72D7C2BE1B3FCF136D27AC2F5971B763BB16F35FF1ADD1A1F90DCD2F45FB46A45E4146F8C12D67BD63A7E95A1DDFD3BB7C770CDB875D47606F993B6AFC5FB39ECD7322C05E99B353F587815AE6A27F9B6A067F1EF1DE5DF05EB3E6FA33BB4FFB3EFC14073A9BB0134D17A0F8064DE47AD1CB3F2F4E4E47B291000F8490E601AFGD0B58B7577EF756A7BBD24FE9B4F9F3BCB2D7FA4DF62792B746A7BB0A4
	BFE0967AD1FB3350CF8F1C05713967F6204F3592F0290FE059CDBBB1BED77B887AD607F7123283101EG9072884E2BA472D9C60E3BCDACC7DFEDA92FF70F1E540B05F4666CF4DD71EC6C63CF57F09FDD4BF1591B317EG60589D3BEF5761BEA48CF0440EC74CE67BF24F3BFDE7747CF7129EE5B6BE7FD1D64E73EEDFD78616B3BD4A7C2681391C675D3E37CB305CE248DF819023AA47E4581907B2AC8ECE571902111779F9A84340E014459ADAF6A52423819267E05908FCAC1BBD873F877DB95F7F592062827FE7
	034E9778BF9BB42E405759A0C613351E5F4163F4BE3B8B47EC8C47256660597D25F3F03E1752B31A31F3EE4E06E54C45FA4F6F981A3F7362906BE56E9CDA3CE4811D8B79F35571126FD03CE12EBADED23393F524927202B83FD34A7D751FB84F573E64EDBA273266297D34149C3AAF19277653466B51CFBB88F90173307F3FDCD376AF0448FBFB79D998624B7F4F596C0F6ECA0DFA0C0E176A703ED993148988C1BFA4CFBF543BFA2C5D91FAECB79C5ADC475B5D1FF1EB6D0E4B40F68D0F016F7498369BD1B2147B
	EF2C4D42D2ECB3885ADB7D985A6FEBDEF76E485EAEE3BE76794D5C217449464B12B73479F1E50875968D3A9E535F26FCBA2F662B67559794EF182F1ED7AFF15BDB89F9C173D12FFF14BFB43A1D75FE6763633E66438FF7A96BE549650A65E7154FFA1BF3C83DE6C5FEB73C8F4869F19A75188FDA1F659D2334E5E10CF256637CBE9E794CA577952EF8EF58B97C712E76E16D03713C209C7702211CE7DD0E67193DCDB4CD56EF16FED0BF7CE776C23F56BE54FEBA63FD6BF57A6DB2945EC3D187BE2372488FD5467A
	96F2999D60658EEEC65C28D014CBD45D506E277D412B1E7A5B4E4AD02DFAF76807565F842FFA6A7E22B57BFBA0E3E87D4D722AD722CAA7AE502AD762F57F63667A2B742A276EAFDB559F73434E6570BB268BF0CCCF678B3C5D8B0A77283C611F7FF17943BF7F6F2EC7BD36ACC05DFE097A28CBA09D41F1D393E8DCFC01720C4932842C9B434B95D299D6ADD45E6798CBF1AD0033ACC45CEA0A9B31D0F91F63F0AE36B7EEA16E0D7612A386F2BCF2D9792D1CAF0BDC28BEDFD6FC5DB3BEFA045A03E8C263C27499CC
	65989FE5B4AC3FA18D7EE222050B5CB1D1565E714D37F2867B40A27799ECC66F4EFA753DBD06FFCA2F3E2B46704FE7286F1CB13CD82A3E6BA52FC9B97E56772AC55E6BAD63F3FA7D2D5CD3E8DE243E277027DD1EE337E9B73D9F8465AC0BF06C96A6F80E5DAF93EEE56C8297394FE33076BF4ED15F1510E3E3B95E77A31E4A417B91411C3EB7F2BC69EB4E3D15BB910B97396FC430397399F1076577DD64671BE57A2A4B87973B23571B65F78BB902A55B0E4BB97CBE57A21C2FE35FF7173D3A5D5D67580441FDEB
	1E3343DDF7E58E1E89E6F991548F2B25F121F1B97CB7220106D8006C30F4DDA5C0EB986E2F3F1903F11B01B00C8D3C1E637F2C28BE577FD95193397E4F0A0E4A75FFD674C90FF99486F4AD8B733F57E7B27FF10E77F77E1F66A83F73335F69DE5F18829D053CA668A461DDFAD74F29F7DE36CAC6175E5629F75223341210C4F3CACA1C8EE1939F1786FAC8A672527E0A0417E7A6448B098D42FAF6E828C1C8A4A7FF9A849DBD9854A0A4C8B63B0B7CE06AFA19C1B2EA1B1D97056BC23C0C5DA10BE79347A64AE5E9
	B419E3B7486588A4F1ACE1EC23DCB6D192D2703AE285D01C6621C95F1F7E612FA3FEF15A19A02CEF905EA30661246820E7F2C493397BAC7FC8EB3D10E2A777BD49EF8482BBCE7A50B9254B9631D1486CBCC1FE504BA90926EABC6D6314042D7477E9CA49213F52E3C6E737C4CED59DEA94922D6467E405F7D83645E86DAAE5C7FF8ED11944D537918E696089C06F96CAAFE9250437326549CD85B91F2CDD1CA044578B9BACB623299EB659B6C70310A2F5BBC4E7374DE2F259BF2E228DA57723B89220398FB87B04
	5992FACA9A096EF1BDB4A6ADAA2D9EC468F2882534A431B3A53C0429265B68002EC4C703C4FE9E57664867F9376D10F6BDFF745D56F8A1CEE6128C47C6179D86C0F2982DCE327D878A74AA8AB00BB7D4F27FF24344B9FF7EA7ED48DDD592F6B211C15552AE5156DBDA5D59E9360A3C81109FB43F1DB7CF4A14284A3C639A3F7513D5EF3743F8578A893D86837DDFC67F17137FE594D3C6B1E565A04498416F1FBA5FC515F0134414044F90B24CA575B0D2524FDEDBBE63454A18E8602EDE50511F4181E1E738CB7D
	232F37DDF45C91B2F17B46891177103130931540F657F79AFB4D164B70D2CBED200B60F73ABCF4BFC91EABDB2054C9F952263297BDB93ADFC1CB272834AD0EE2533C54A6112059745CC27FEF9B34D0C82DACDA77FD52A1F9594EB9601C3173F92CD895D6ABF4A908E52425F29360CDEC4257CBEC151AD81ADB6696B791F51ADCB3C15BACC942C6D976E436E99B297FFFBC9E3A910F5A86595B242B556CDF44D51BA6D735BEA60A7662A360DD27BDDFA96F932D9B9D6F73BED15092369E07ADD17FF6D6DC9369B186
	7029CF6179734F0F6F93FC1FE3BF59EB34D8A42325275AEAA2E75A0FDC16C477DD0775CD24FDA71611B3D9DEDA93217BCBC51D7F83D0CB8788CCAE7B30CB9FGG64E3GGD0CB818294G94G88G88GA4F44CACCCAE7B30CB9FGG64E3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG059FGGGG
**end of data**/
}
/**
 * Return the JButtonCheckScript property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonCheckScript() {
	if (ivjJButtonCheckScript == null) {
		try {
			ivjJButtonCheckScript = new javax.swing.JButton();
			ivjJButtonCheckScript.setName("JButtonCheckScript");
			ivjJButtonCheckScript.setToolTipText("Checks syntax of script");
			ivjJButtonCheckScript.setMnemonic('c');
			ivjJButtonCheckScript.setText("Check..");
			// user code begin {1}
			
			//dont show since not implemented
			ivjJButtonCheckScript.setVisible( false );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCheckScript;
}
/**
 * Return the JComboBoxTemplate property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxTemplate() {
	if (ivjJComboBoxTemplate == null) {
		try {
			ivjJComboBoxTemplate = new javax.swing.JComboBox();
			ivjJComboBoxTemplate.setName("JComboBoxTemplate");
			// user code begin {1}

			ivjJComboBoxTemplate.addItem( com.cannontech.common.util.CtiUtilities.STRING_NONE );
			
			//dont show since not implemented
			ivjJComboBoxTemplate.setVisible( false );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxTemplate;
}
/**
 * Return the JLabelFileName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelFileName() {
	if (ivjJLabelFileName == null) {
		try {
			ivjJLabelFileName = new javax.swing.JLabel();
			ivjJLabelFileName.setName("JLabelFileName");
			ivjJLabelFileName.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelFileName.setText("Script Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelFileName;
}
/**
 * Return the JLabelTemplate property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTemplate() {
	if (ivjJLabelTemplate == null) {
		try {
			ivjJLabelTemplate = new javax.swing.JLabel();
			ivjJLabelTemplate.setName("JLabelTemplate");
			ivjJLabelTemplate.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelTemplate.setText("Template:");
			// user code begin {1}
			
			//dont show since not implemented
			ivjJLabelTemplate.setVisible( false );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTemplate;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelScriptText() {
	if (ivjJPanelScriptText == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Script");
			ivjJPanelScriptText = new javax.swing.JPanel();
			ivjJPanelScriptText.setName("JPanelScriptText");
			ivjJPanelScriptText.setBorder(ivjLocalBorder);
			ivjJPanelScriptText.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJScrollPaneScript = new java.awt.GridBagConstraints();
			constraintsJScrollPaneScript.gridx = 1; constraintsJScrollPaneScript.gridy = 2;
			constraintsJScrollPaneScript.gridwidth = 2;
			constraintsJScrollPaneScript.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPaneScript.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJScrollPaneScript.weightx = 1.0;
			constraintsJScrollPaneScript.weighty = 1.0;
			constraintsJScrollPaneScript.ipadx = 300;
			constraintsJScrollPaneScript.ipady = 204;
			constraintsJScrollPaneScript.insets = new java.awt.Insets(4, 11, 8, 15);
			getJPanelScriptText().add(getJScrollPaneScript(), constraintsJScrollPaneScript);

			java.awt.GridBagConstraints constraintsJLabelFileName = new java.awt.GridBagConstraints();
			constraintsJLabelFileName.gridx = 1; constraintsJLabelFileName.gridy = 1;
			constraintsJLabelFileName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelFileName.ipadx = 6;
			constraintsJLabelFileName.ipady = -2;
			constraintsJLabelFileName.insets = new java.awt.Insets(3, 13, 7, 0);
			getJPanelScriptText().add(getJLabelFileName(), constraintsJLabelFileName);

			java.awt.GridBagConstraints constraintsJTextFieldFileName = new java.awt.GridBagConstraints();
			constraintsJTextFieldFileName.gridx = 2; constraintsJTextFieldFileName.gridy = 1;
			constraintsJTextFieldFileName.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldFileName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldFileName.weightx = 1.0;
			constraintsJTextFieldFileName.ipadx = 249;
			constraintsJTextFieldFileName.insets = new java.awt.Insets(0, 1, 4, 15);
			getJPanelScriptText().add(getJTextFieldFileName(), constraintsJTextFieldFileName);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelScriptText;
}
/**
 * Return the JScrollPaneScript property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneScript() {
	if (ivjJScrollPaneScript == null) {
		try {
			ivjJScrollPaneScript = new javax.swing.JScrollPane();
			ivjJScrollPaneScript.setName("JScrollPaneScript");
			getJScrollPaneScript().setViewportView(getJTextAreaScript());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneScript;
}
/**
 * Return the JTextAreaScript property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getJTextAreaScript() {
	if (ivjJTextAreaScript == null) {
		try {
			ivjJTextAreaScript = new javax.swing.JTextArea();
			ivjJTextAreaScript.setName("JTextAreaScript");
			ivjJTextAreaScript.setBounds(0, 0, 333, 230);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextAreaScript;
}
/**
 * Return the JTextFieldFileName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldFileName() {
	if (ivjJTextFieldFileName == null) {
		try {
			ivjJTextFieldFileName = new javax.swing.JTextField();
			ivjJTextFieldFileName.setName("JTextFieldFileName");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldFileName;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object val)
{
	com.cannontech.message.macs.message.Schedule sch = (com.cannontech.message.macs.message.Schedule)val;

	sch.setScriptFileName( getJTextFieldFileName().getText() );
	sch.getNonPersistantData().getScript().setFileName( getJTextFieldFileName().getText() );

	// filter line separators in the script text area
	String text = getJTextAreaScript().getText();

	java.io.BufferedReader rdr = new java.io.BufferedReader(new java.io.StringReader(text));

	String endl = System.getProperty("line.separator");
	StringBuffer buf = new StringBuffer();
	String in;

	try 
	{
		while( (in = rdr.readLine()) != null )
			buf.append(in + endl);
	} 
	catch( java.io.IOException e ) 
	{
		CTILogger.error( e.getMessage(), e );
	}

	sch.getNonPersistantData().getScript().setFileContents( buf.toString() );

	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJComboBoxTemplate().addActionListener(this);
	getJButtonCheckScript().addActionListener(this);
	getJTextFieldFileName().addCaretListener(this);
	getJTextAreaScript().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ScriptSchedulePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(366, 349);

		java.awt.GridBagConstraints constraintsJComboBoxTemplate = new java.awt.GridBagConstraints();
		constraintsJComboBoxTemplate.gridx = 2; constraintsJComboBoxTemplate.gridy = 1;
		constraintsJComboBoxTemplate.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxTemplate.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxTemplate.weightx = 1.0;
		constraintsJComboBoxTemplate.ipadx = 10;
		constraintsJComboBoxTemplate.insets = new java.awt.Insets(17, 1, 6, 30);
		add(getJComboBoxTemplate(), constraintsJComboBoxTemplate);

		java.awt.GridBagConstraints constraintsJLabelTemplate = new java.awt.GridBagConstraints();
		constraintsJLabelTemplate.gridx = 1; constraintsJLabelTemplate.gridy = 1;
		constraintsJLabelTemplate.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelTemplate.ipadx = 4;
		constraintsJLabelTemplate.insets = new java.awt.Insets(19, 7, 8, 1);
		add(getJLabelTemplate(), constraintsJLabelTemplate);

		java.awt.GridBagConstraints constraintsJPanelScriptText = new java.awt.GridBagConstraints();
		constraintsJPanelScriptText.gridx = 1; constraintsJPanelScriptText.gridy = 2;
		constraintsJPanelScriptText.gridwidth = 3;
		constraintsJPanelScriptText.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelScriptText.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelScriptText.weightx = 1.0;
		constraintsJPanelScriptText.weighty = 1.0;
		constraintsJPanelScriptText.ipadx = 348;
		constraintsJPanelScriptText.ipady = 286;
		constraintsJPanelScriptText.insets = new java.awt.Insets(7, 9, 10, 9);
		add(getJPanelScriptText(), constraintsJPanelScriptText);

		java.awt.GridBagConstraints constraintsJButtonCheckScript = new java.awt.GridBagConstraints();
		constraintsJButtonCheckScript.gridx = 3; constraintsJButtonCheckScript.gridy = 1;
		constraintsJButtonCheckScript.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJButtonCheckScript.ipadx = 8;
		constraintsJButtonCheckScript.insets = new java.awt.Insets(15, 30, 6, 9);
		add(getJButtonCheckScript(), constraintsJButtonCheckScript);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	// user code end
}
/**
 * This method was created in VisualAge.
 */

private void initStylesForTextPane(javax.swing.JTextPane textPane)
{
   //Initialize some styles.
/*   javax.swing.text.Style def =
	  javax.swing.text.StyleContext.getDefaultStyleContext().getStyle(javax.swing.text.StyleContext.DEFAULT_STYLE);

	javax.swing.text.Style regular = textPane.addStyle("regular", def);
   javax.swing.text.StyleConstants.setFontSize(regular, 14);
   
	javax.swing.text.Style s = textPane.addStyle(com.cannontech.common.gui.util.Colors.CYAN_STR_ID.toLowerCase(), regular);
	javax.swing.text.StyleConstants.setForeground(s, java.awt.Color.cyan);

   s = textPane.addStyle( com.cannontech.common.gui.util.Colors.BLACK_STR_ID.toLowerCase(), regular);
   javax.swing.text.StyleConstants.setForeground(s, java.awt.Color.black);

   s = textPane.addStyle( com.cannontech.common.gui.util.Colors.RED_STR_ID.toLowerCase(), regular);
   javax.swing.text.StyleConstants.setForeground(s, java.awt.Color.red);
*/



 
	// ADD THE REMAINING COLORS LATER	  
/* 			com.cannontech.common.gui.util.Colors.BLUE_STR_ID,
				com.cannontech.common.gui.util.Colors.GRAY_STR_ID,
				com.cannontech.common.gui.util.Colors.GREEN_STR_ID,
				com.cannontech.common.gui.util.Colors.MAGENTA_STR_ID,
				com.cannontech.common.gui.util.Colors.ORANGE_STR_ID,
				com.cannontech.common.gui.util.Colors.PINK_STR_ID,
				com.cannontech.common.gui.util.Colors.WHITE_STR_ID,
				com.cannontech.common.gui.util.Colors.YELLOW_STR_ID
*/	
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJTextFieldFileName().getText() == null 
		|| getJTextFieldFileName().getText().length() <= 0 
		|| !getJTextFieldFileName().isEnabled() )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}

	if( getJTextAreaScript().getText() == null 
		|| getJTextAreaScript().getText().length() <= 0 
		|| !getJTextAreaScript().isEnabled() )
	{
		setErrorString("The Script text field must be filled in");
		return false;
	}
		
	return true;
}
/**
 * Comment
 */
public void jButtonCheckScript_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	CTILogger.info("Check script is not implemented");
	
	return;
}
/**
 * Comment
 */
public void jComboBoxTemplate_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	CTILogger.info("DO NOTHING FOR TEMPLATE JCOMBOBOX");
	
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ScriptSchedulePanel aScriptSchedulePanel;
		aScriptSchedulePanel = new ScriptSchedulePanel();
		frame.setContentPane(aScriptSchedulePanel);
		frame.setSize(aScriptSchedulePanel.getSize());
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
		CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 11:20:58 AM)
 * @param text java.lang.String
 */
private void setScriptText(String text)
{
	try 
	{
		java.io.BufferedReader rdr =
			new java.io.BufferedReader(new java.io.StringReader(text));

		StringBuffer buf = new StringBuffer();
		String in;

		while ((in = rdr.readLine()) != null)
			buf.append(in + "\n");

		getJTextAreaScript().setText(buf.toString());
		
		//scroll us to the top
		getJTextAreaScript().setCaretPosition( 0 );
	}
	catch (java.io.IOException e) 
	{
		CTILogger.error( e.getMessage(), e );
	}
	 
}
/**
 * Insert the method's description here.
 * Creation date: (3/12/2001 5:13:39 PM)
 * @param file com.cannontech.message.macs.message.ScriptFile
 */
public void setScriptValues(final com.cannontech.message.macs.message.ScriptFile file) 
{
	try
	{
		int i = 0;

		for( i = 0; i < 25; i++ )  // 5 second timeout
		{
			if( this.isDisplayable() )
			{
CTILogger.info("		** TRUE - ScriptEditor isVisible()");
				break;
			}
			else
			{
CTILogger.info("		** Sleeping until ScriptEditor isVisible()");
				Thread.currentThread().sleep(200);
			}
		}

		if( i == 25 )
		{
			CTILogger.info("		** TimeOut occurred while waiting for our ScriptEditor screen to become Visible.");
			return;
		}

	}
	catch( InterruptedException e )
	{
		handleException(e);
	}

	javax.swing.SwingUtilities.invokeLater( new Runnable()
	{
		public void run()
		{
			getJTextFieldFileName().setEnabled(true);
			getJTextFieldFileName().setText( file.getFileName() );

			getJTextAreaScript().setEnabled(true);
			getJTextAreaScript().setText(""); //clear any current text
			setScriptText( file.getFileContents() );
			CTILogger.info("		** Done setting script contents");

		}
	});

	
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) 
{
	com.cannontech.message.macs.message.Schedule sched = (com.cannontech.message.macs.message.Schedule)o;

	javax.swing.SwingUtilities.invokeLater( new Runnable()
	{
		public void run()
		{
			// make the text fields disabled since we havent received the message from the server
			getJTextFieldFileName().setText("(Retrieving serving data...)");
			getJTextFieldFileName().setEnabled(false);

			getJTextAreaScript().setText("(Retrieving serving data...)");
			getJTextAreaScript().setEnabled(false);
		}
			
	});			


	//do not do the following because they are sent to us in a message from the server
	//getJTextFieldFileName().setText( sched.getScriptFileName() );
	//getJTextPaneScript().setText( sched.getNonPersistantData().getScript().getFileContents() );
}
}
