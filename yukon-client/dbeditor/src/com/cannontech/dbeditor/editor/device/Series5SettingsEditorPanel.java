package com.cannontech.dbeditor.editor.device;

import com.cannontech.database.data.device.Series5Base;
/**
 * Insert the type's description here.
 * Creation date: (5/3/2004 11:06:58 AM)
 * @author: 
 */
public class Series5SettingsEditorPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JCheckBox ivjJCheckBoxSaveHistory = null;
	private javax.swing.JLabel ivjJLabelHighLimit = null;
	private javax.swing.JLabel ivjJLabelLowLimit = null;
	private javax.swing.JLabel ivjJLabelMinutes = null;
	private javax.swing.JLabel ivjJLabelMultiplier = null;
	private javax.swing.JLabel ivjJLabelOffset = null;
	private javax.swing.JLabel ivjJLabelSeconds = null;
	private javax.swing.JLabel ivjJLabelTickTime = null;
	private javax.swing.JLabel ivjJLabelTransmitOffset = null;
	private javax.swing.JPanel ivjJPanelPowerValue = null;
	private javax.swing.JTextField ivjJTextFieldHighLimit = null;
	private javax.swing.JTextField ivjJTextFieldLowLimit = null;
	private com.klg.jclass.field.JCSpinField ivjTickTimeSpinField = null;
	private com.klg.jclass.field.JCSpinField ivjMultiplierSpinField = null;
	private com.klg.jclass.field.JCSpinField ivjOffsetSpinField = null;
	private com.klg.jclass.field.JCSpinField ivjTransmitOffsetSpinField = null;
	private javax.swing.JLabel ivjJLabelStartCode = null;
	private javax.swing.JLabel ivjJLabelStopCode = null;
	private javax.swing.JTextField ivjJTextFieldStartCode = null;
	private javax.swing.JTextField ivjJTextFieldStopCode = null;

class IvjEventHandler implements javax.swing.event.CaretListener {
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == Series5SettingsEditorPanel.this.getJTextFieldHighLimit()) 
				connEtoC1(e);
			if (e.getSource() == Series5SettingsEditorPanel.this.getJTextFieldLowLimit()) 
				connEtoC2(e);
			if (e.getSource() == Series5SettingsEditorPanel.this.getJTextFieldStartCode()) 
				connEtoC3(e);
			if (e.getSource() == Series5SettingsEditorPanel.this.getJTextFieldStopCode()) 
				connEtoC4(e);
		};
	};
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
/**
 * Series5SettingsEditorPanel constructor comment.
 */
public Series5SettingsEditorPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (JTextFieldHighLimit.caret.caretUpdate(javax.swing.event.CaretEvent) --> Series5SettingsEditorPanel.fireInputUpdate()V)
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
 * connEtoC2:  (JTextFieldLowLimit.caret.caretUpdate(javax.swing.event.CaretEvent) --> Series5SettingsEditorPanel.fireInputUpdate()V)
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
 * connEtoC3:  (JTextFieldStartCode.caret.caretUpdate(javax.swing.event.CaretEvent) --> Series5SettingsEditorPanel.fireInputUpdate()V)
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
 * connEtoC4:  (JTextFieldStopCode.caret.caretUpdate(javax.swing.event.CaretEvent) --> Series5SettingsEditorPanel.fireInputUpdate()V)
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G61CA24B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DD8D45715760B693ADB3735DFED1774333B5618ACEDCCBE7A15ECEDCBF6EDC37B1946B426B5D5931B182DEDE857B4F4EB36261FED9E7FA8A803E282020AC094E49CFE04B00E9899C08403CA0490A2EA9C18870C8EB3634CC3B0B53A675C7B4EFDEF06B703B1CD7265785EBD671E1FFB6F39BF673CBBC8F93B6726A5A567C9D2D21A247E5710A4C9A3E7A469C7AF473DC8B8A93FBEC3527D3787A0D7
	FA26B2951EA6204DF255E71824275BB321DC86E577C0FD46EB703ECDDA5D5EF882DE426849835A6F631FF8B8321EF9C924A7031507FD59705C82D08E38CE0028FC54FF53172F62FDD05E223EA3A4A5CB5239822CF3561E2F432FD26BD5832D95C05A01F53E6AABD269738107F5626039340057CD0067C6E65D1DB62BDA6F5C972325F5BF59153C99F5696493346F603CEE75D9AC5D4BBAA2C5FA3D3A8D1E0DDD17075F9C740EDA869D9E0F57234803A39667006CF4A9DE3FC5FDB8652B2EC159E66B167DAEB9D05F
	ADAB0A4BB39CB84628DD8E0F6C0676B07B2759B319EC309F4AA30508DBEF47BEAB06F7ABC0D7A13561094D0D2A0D33AFC7CB2B6F4E8D5B0685055C762505DA9B16F5E724CDABC2798F33B1C1BE93145B8138ADD9D7481678A232A55000F5B7C0B9EFE7FD06F9A761EDD64437C2799C003213ECBC320FEC6C7B5BF724758376641CB0B64E5C49ED0B5D293371D9FBDA81739BCB8A0EFDA25072F655E71483F4823881E68294B2FF7DED52FEF85A8714966F682857E377791A5DCE7FC92FD3F6433B4D869495F7C69E
	747A1D1244FD72CE3B59409E02793B027D9101376DDD09FCE8411987A4C5E9C8CEB3E8DB6C1345498BD599CBF6E9ED4B5C0D7D66EE46FEDB8B78A4D5AE47DFE3784E5D9A1E0F55DF19AFE0F984E8CBF6D3BF77EDA1DBBAD367C9261A16648C83DB22FEE8CD2EAE66B63497EB3670BE4ECC43BED6GBF8BA09AE09140EAGE98F7571416403D375F1874C813F47616E9E9B70B97CCA73184B6D147DEADFBF620B5457C6407916FFE4BE5EAF518775E3346A5B4D067D32AAFADFF2519E5E9FF5FB34FE613ACDD74269
	6E59B359B73608B1FC1E4D1511BDFC8CD3847E8F8CBF214373313D4670D81E813435G3C7E83ECEEF515047A4877995EDD926AA3CDD6721199C0DB8B4047775EF4DC9B3BCAE15EGCC87188390873082E0EDA90D710D642ADB1CC7DD3A71653C8DA92F42D31ED07C0E8ECFC0F1F88665DE075F6598F04B81C92D5314027DBA9044F70C4D6FC150F1DC7648FE076272FAC03AE3D486E78A70BA27D2F2310EDFE6549E57288C8AC6FD92496DCD59067D6188A86F781C8EC556EAF07A05946C132B0EDE108882603D31
	8C7BEB8EDBF333613D3C0C7A6A6799081B866528B26A1715A9DB617996B8376C3659DADD03E80643FF0DFC545FECC2BF037A66576BB396G1877527C7CFD9F79E16B667BA57313F6C3BFE4FE3A177BD47C5ED0BF3C49642F85FC76BE108D50390F765554663E90B9E883FB6EB576D1216F7570F4EC0BB687F7CE5211261BEBD34D61F0349475497B5D3E1179B434EB86C074BE6A7B3C54E26E134D0E00CCDEF85A8F8EA4BB351160F5F353F00F112F4A9E25D576499EA73C84D49F48CF5B28796710D7784EAE0647
	752A859CA8A070E5E9399C8FB2745ECEF4B323BB94456F9A98D3E45A59853D0E599288DA8A83EADBDBB6E1DB97C19BD7GA40836BE1436CD67E71DDE6F65B1DF8F4EA055DF7731FDF983544FAB2771FF21814FD9D6D17E93ABEB741B8D21E377F181F5852F51D1FFF79DA93AF72EA3560AF300564DB4954FE9BFE0340BE0578C00980095227DED56BE833E3F171DDC477BCC07DE7ED4736223G770D1874703E7D1DF4E35FFEA85D58379FCE0F6C3B0F269B79AE277DAC9D5B689D7768664272F4DC9F873D9E0F4C
	A624E01178F97977BD588E1747253C6BF28E4BCA376B839962008A7469EF33B5D802773C8ADAB347CBE94DAC7B609B5222770B8C4FD7D1779CCEF6D7F0DF1CD6212D195C269599B847E437BC8A9D55629D73A89C7F8CDB5FD5FCA005617E623B023C65A7FA208F5BDC325BD9E39E04192FD8583FFCD3A93F64386A18308446212BACEC9CACADC8BD062F9545878E3CD99C7E14EF9EF279658E0FEF0C04D574D6D42BBE33CD1A37459F2A7F53B02006745BA6353F76D3D0DFAB5A3F27BCD47F41CFC1BF1EE779FAFC
	B58BFDE5AE78D0AC40128ADA0F4FE6A21D56E3F553E9713A5D5C4D01D63D1F79F0F31E4EAF57D47078F60B20BFBB85BD2BA5B23DA1AC1D5BF9FCA32EFF81794A188C47AE5CB05E122F5159E9635690DA2F43BD261D49FAB7E641532D5BEBB83EE313717CB93D8977174BC2FE7726F4894FD6DCA61F6FB18E1469733BC6E1EB464DD30D635F3C220F63A720ED9873AF046792G4E7BF64D8EF8C60A17A7AAD156CBD60A7757G6F860022CAAE2F2F12778D2F377090550B07722A4AF0757E77A8555BG65BCF338FA
	77B6D13DBA2853E9D04FED46FABF9171FC1540BD47F0AF42867135D40AD3CE1A747988E7204EF02DC308B12B67C3F3E87CF6DFB64ADD8378F5E6725D47B6694F9276C040BB68E247E7F5FC2FE76957C10E7BCC56A6F842D489C0AC6714A748C77EA18BED4573350AD647793E4AA35264F8018F969D55DA4659846D713640913F9D5C2CC5EC933DAEF9DC22FE7830CD1F53084B29C84E8A538F1987F87B4B8FE87D90752CBD0D5BF4178D6735D1EEE61099E9999EF3D94694979E649D0A032D895C31F86EA31A1D31
	D05648814AF1BC263075814A8B81168270F8EB5721EC35EE82600AAD7599FC3EBE30C3BFDF7BGBF8DE0BEGDF6F6EBC04BE7191D7B8354F63AB3672B18AD94E289F67EDEBD275AEF9E01E54F8F313E15EE5C6CEDE72C28B6F3F55962D9F677C38BAAD860037EF78A8354FDCD51F612C22764DBBEF543EB9D554AF05CD449783388441D7E88D6543F3EAF1B571BDAE786AG672EA63EDF649B714D2F2673423B357AB934E8F5F958B93430DA4D47D4074E21AFEC41F1CFG7C06EA1AC34959393AB9F4863C566199
	0695D21C4932D378DA2816E33353E8AD14E8EFD91203361BEAA0EE83E885B887F03D06F49C5EBCE5DE6558C4043C4A7D13F294F3EA3473F9CC4D64980F67907EF98C7BBA3E0667908A84FE9E43CB359A1E0F4D4F599C40F2A350B28F52793A6CD0287EE58703F5A60614B9DF6DA4BECBC8B92FAE18CF4D3D1697517C56CD971BCDBBBB703DE64D18FE2FB157F11F690227330E721C3F5E11295AF33D0E627F12DC149F8B6525G89G2975759965G2DGA375B4EE7F3459E86D8B9E37F08B1F96CB723E98BDFAFB
	FD7837C9FC21FDB823BE180F37FB65F9EA775CFA9A47BDCD37E7C355A43ED09BD6985AF046CAB6A48A9B1E3CCD9BFEB5258D65EF9859303900ECE8FA0346FFE0AB62A620BC638D9A6B67B7E3FE1D5601AE2F4B23C834864C5E02679F2F5FB56C72906EAE069B747AD91044F14BF24247EFCF679847EF4F659847EF4F67C44ECD3C1893AE3E63F904F83647C5C17B96GF05C978208DB8A6504B7E8BC3EB406B1CAF68378BFC0BB0093E09A40DC00F80055G12954EC0GD600AEAB71C6B3DE5C97C6GB783A09AE0
	B1C082C066A11089508EB0F2081F3770AC45F9D7AA0BF4E305BAC257B0B12EBA9A6A8FD7CF8F6DAACF3C7AAC52714D8D234BE9004FD67B825F315D125548B7334774ED490DE0D71A6E7D35DBE8876940E70E7A5C2E23ED8E43E3141B40FDACCBFDAFE0BE94CC5F496ABC72EA8E733BC0C08E30458DCFD0AA6DF9E53BE41083B8659815A50A737A8FEDD46B9CD33CADB5922D69FF2C40355AE0CDE787B3BED70B2E52DC1DF1084EE5669C16C703728A0044C364BF3F5BC16BC126ED723895812EBA842F6674F2434F
	51033946F374D0AE76F340BB669F14C367E1D3EE3E20E92BFF07A20FE23728F19663D79C7EE1F952BC6EE27C1C960E7F3D5CF0F31D77797C237ABE2F95FE794CF96A23D603BE6A831CBB844F6575B569659D94725E35123CE98672E683AE5AD0DE36DDAF2FCE485BDAC072628C64AD855C9A43B19C5E0A671689D13E4C4A57043E272F605A6CF6DC730E41A26EBC4CE2BF91172E8719A640D981BA8FF3795CC73FB50E73E3486F98056E6D764903D2D3A30B8783443BG6AAF82D88DA0812D38114E7B5F9A41781C
	FBF317F7DC7633901C532A0BF07CFADC0317712BC5374F65E10798E16FBDF9A87BC427E3C0F6D3AD22BDBC30D950CE3ABCB07C5A38B76F4953787C8ECFE054251C9E9A8A488A271B1BF10D8926E85AF91DBBCAB5795DB20C11D3137FA79F135FB2A28FDEEE76CEF4BB2E4A6D2EG84FE57D45E099C0DD741AD8C0EC1AAEFFA53969D4D6BE3A415B676967633AE932562DF3F980CBF2866C6BA8B7531410CC6FE4E0BEB2458419EC4DF2A525789FA0F4D081EF90468779B7297897A004DC8FF352027994AEF957427
	0264779D6174E9027E1A5508FF36201FAFB022C79F91673A2B7A33D19C60179E51664D1AA31367960F8362585A3A7E880F83F2857EB106AFFBD3437378201661315C8534D5EF5239A22990576D11237599530F926E6C87F456089238E753D97C85E52909F0AF1DA75EBC017BFAA171168BDC0C0DF8A7844EE3A55E198217DAC03CF3856E0F8D441BA8F09585441BEAA7DCEF8371668B5C4AFC629D31D38C3B43CFF7AA2E83AE56CEF92D1A0BC1F10B45B12E58EC477DAEE733E338132DC6FC1F9B70E72A7CEB6D34
	4F4DE2B24B1AC1AFC0CFB3453D93FBA9E732605EF9D26734E643FBA02BFBAA125D4DFC1CE7B5EBF1EF423376B42EF3C900F4AE81FAEA8B627E9C507B954E41A71279391A534D0A1E0E63DEA16969970268E86FC3C17C330268E9BAFE5E36C7DF2736EDF8B4C6F2BFD89D760ECBE78BEF13CFFDE6AEAF12D2BF2CD173C1B75E58AB6972C1175D4316CB03F81A368C61B282070C965D224B7B630EC24A81C53750787DF1A84C99052DE21CEFC2702DEE219873F3B642653792EE0D2857A5F0F709FAB3842ED1609635
	129DEF76969A5BC1AB315A065CA2BAAF2D9772CED9C9DE75B1424DAE90792EE36457D6F6561A8B65D801FB2584F1AB203CD6605CE5B4A7CCED345E7CB027981E9721FC9DA08AA096E089409A008DGC547296E4FD95DC6A81F83388EB067B84F65E21EB68E5ED7093A561CE9A163CEB2717DFA1B360E45365DDA9CE08497F5B6C69948613674CF328577A6BAEF6B4F719F67BBA04E399CF5EE99FD33647BE2FEA16A757965A1596F171D7C531AD4570E63D3437CEC8334A5934A8B986E4A872D91F42629F289A600
	E7A640F615B6371D2F7BC9C27F7F25E35B61D0649A9D9BE55A396EAD6CBB5BBAF817BA6AB3B8EE5785441921EC6DA0FB9696CEE50F5EA6E7C730BD939D1ABDDC47CAB6E72683FE16507BAF4C6F63203CD868ADAC18CA6FC61D5EF5A1FAB3CF046AADE2BA72GDFFC02741EE1F32B934AFDA7C86FE843D4FA33F4FA271F88569BB5C9EFB253B19F70B1A7E82E947BEF6F2E1565B67906BFB29FBFCB8C84F0DFDB25EB9347DFE678849D1E6F2DDF91FB6BBA6C5337A8E733B38F5766B8A82FGC884486C047381C0EB
	A765F09237E2BEA2E23E2E476B6B04604F2DCB297076BD91383DBBE8BF4820BDAC76FB5F1496FEB6B1A95CB70D3ECE759B137A1C7BE49143737645EC4776C583AD9EA081C0BA89E7A2G73C9EA1FF51B19378FBF3F589545B1B8423E6CD06370E30B0646G0637EDC6E12436B5017C1E13C6F90D8F8D79C6543A57CF06DBBBE7590C78E629756707657B2D213E05EA7DA5E1797E55D09F41EA60DBE758BE3BA1DF66A9B5E7F4AA1C3ED70B0C782A557A2D6A33CF474F793A2DC6FCEE355E34307A6EAAB0621B2D56
	0FB660636B76692B98878FF87DAC614063E0578D0AE56300EF990069F4FDC659E9AE03733DDABAB9FEBED0CAFC0D27F97C5C834F91150F2FFBF714C5DA77D2A46D5E8C3B97893C33CF73F58F4B51707E42AB2F3C7284G37A337DC0BAF5BDD43A31DAE88E3D59B571C58A2E81D5EF1CED2E98917CC02F6F24C2D38FCEE176C67348F8FE48B1ABED65E13959CFB5628326A46330371B59C7F75917CC6B7A9269777AD96598C693562BE00A9B42643BCEA6CE9717D6D2C91FD3120BF3747083ECA50DF7E50081EA868
	0BF6044634C8CF6DA2FA5324189B6979C0B7F761EF8196E328C3FE7641AEF8FA6471E28AFBAC2D50E88FDE44AC2F371A0BEB2BABE83D1CBB2E7F0EE75DE4897B9D4F5745FDEB3A7AD4B2F7CA3BA51E37E4770958FF09C7A763607C41EC1C03E629FA1F4B26F53A683E3B25566509C925E17446297A325F5662288D3D99E9DC6636E22D5DF71E3F15F6F33EB776E8FCDFA93C95BE2336F2D96F15E8ED3CDE2A493DFB472D5B93DB26713D1C9F012F2EFECAFB163F2E59E35A27497D6EAD4965FC8B4BB53E379ABE1E
	BDCFD7E8767CF53FA63777166472B5F78453E06A7B955D7B9C7F5F7B9E6464757E41CFD487BFCB6C4D8B1F87BF10E71C87BF18E77C2D6AD0DE643B06763C70F78D5B7374F78D79D9682715387634427C3B88B09DA08AE0415BF48E7AC59ECEG3C29BE798CF44CB90CF9F83C1D2E107E6FEE8F5F56C55B273E537E48F63CB3B360D5946FA84AA77C635BF13C942F0FA155EF005B310F7C30DBA93C2A0AFFEABB7A29DB9EE2E89EDB7F1B48F3ADFE1B626DAF36D13EA97F8C61E633FAE6AC8B5CCB0237DD607E5DC6
	752284AE73B0551BAFF0924B9B2C03F2EAB76164A291678B5C03B642DD94389F9712FDB18257E7A3F9718277DD413BD4608E9411FD45BD047B3C50EB9638EF31BC5CF5A84F9538CBD6620D9538C72D443BC860E28A281E699D42656E20FAC56FD08E6F78CD7ABD069BF0735F215C4BC31BB00638253BC5BDAE45ADB73353954F33FC61A665D9D63DC3F93D8BA528A72F975A87505ACB7A5FA925B8679C60E677D22E66EB25A2CF57CBB29EE5F5D35F857B816AGEEED9F247550F1GCBDCFE3309EC6CFC17F42EBC
	CB38698217DFC238B8012B3EC138F58277279D04B37791EEDC643E1CFD1447FC3CDF3F6F1B3E1A1FCCF1DF687AB427CFBD5776055E5F6936205CD500DF5FC7EB41661D15706CF5DC1AFCB38D10DDFE2FCF76AB57B816FD12D157182FD661D9B57FBD68CF00BEG9F40F400B9GB1G71GABG9281B24FC25F83D483348274G0CGDCBFCBF6BC372BA2029DEA9218ADCCFE077EEE41AFAA432FCD3F2C1CFAED7ACDA53B6387EBCA4FB51F7A3D70FFAA59FD85F63A25FA0915A6016B88F4F983AE7C4E484F6D7FB1
	E5FEC61FAF1A294B9761784DBA9B1A2F789EDB4366GBE7AACFD9F9BB3611DD2859A43FD72AAFE9AD663066C22296206C87A17010E036ABB177D4BDACD766E4311E4E7CCA1FBB5482E14743930570A025BA155E93A2ECF590E34883A56022EDC751D4BDBE27BA8F933D0F96BA70D4B0BECDE267643FBBF1D63CB6BB57B17C65417B1053E46FE2D2F386C36B7B45977C46C9BFD9EAE4DC0F6FBBF77A3ED9C96B7605C98717ADD9F407A6CF0E300C6DECD6D7DD5832DC3BD223D2E864D267F36CEB5DEFA3BC2ED1A56
	2F0D9717BD532A49AE5FB1156C2C8832E776EB6F7CCCF87266D4792928FE239CC77F14FC3B8C46B9D71A4D78E27BEFAD4F3E381FFE4F78B3365F2D6A8F4D77BD4D70897D2179BEE7094877812D6F9C65B77BCBEFAFCF693ACD3EF99F190F376BE8995A9FF7AE343D6D8C3F78DCE8FB1F907B7EB220D94FD3FB0F1B0C744FB94F7BBF66FC385C50B3E70D7862557AAB42726DACB162DB2B5617AE04632B3FE144172756AF8F4B37E1C7247EED843E2E8BC67EFBBDE26E5289BC93A1FCFCFFBFF4881B41773EFB8EE0
	00EA38B7C344F082E24153831764C12547B1ACF87AACA8CB44B47C6820525E37E0FC023FF5F2B95C2E8F58157BEE2868996674872A426F698FD6CD3D277FE715F13C91DFB5B95EF8244AB85EF83CCA9FEF2831C9952E0543F096699375D68A5CFB82778C130938F7DDCEE5C46057B03B905FAE23C2427F0E59B54EF0FDF4BFD107FBCF452D2B42F389B49916ED62FD398A7753A1175B4D4BFFE1F5DC9EB826087EC82EC69BDDBE074E6E8C01C33B797AFB17CD926FF107711D6F95E51172ADF555C1F908E79BB599
	16B735775F6E555E334B357743951A1E04C3372E6742FECD46E7AAB599FFEE40F33C7ED7825D78AF053481CB6731369E15EF3ED9E713855F79BD3DC55BB633BEF43BBCF28B7F5535C477C26FE3B14ACC18B373AFD0FC343E83F18B213C64822DC7ABD80E093FBF27FB3FD4724967BF6E7894F49CAB535AF4CA773E3C7413571D78096A60E3FA41C4F1708AB14E8F1BA88EDEA3F0AF1D25FAF56F0BFBB07DD42FC9606ACA285EAC017BF289551BA3F01DB7285EDA013B7F8655DBAFF05B347C45C542FDC55C530992
	38AF09FA8B846E0C388B3461A24597BF3C09EB1602A1F91B1745D77C8C789473F7138362B787554DB911C6F5A51ABF7F08BF57D15F97E08AC0BD47BDE57AB831C92703473F5A99B1C7971B0C76FF1CF8218FE4E785492EBD2849DED072F1629E27A374EC7B0EAE4E093F71F162841FC30B57383C7D91634F0DD358FA5D919A677C97732399001F6D20EF725F1CD2C7A41B97CD5271B76603CBG3F42C13145CAF66660BE363683937EC1FB817AE5F9099DB6FBF7093938867E2F2D907D3BC137F76D566DC14DC734
	F7D9379F4DB82A3D5F572469AD7D88FB5DCF6D1A0CBFB4EBB22AEEC9867FBBD24B4BDF43997302149670CBDBA75737FFFE6572856F3766A5CB499DD27A88BB8FF4C8E963F8301840BF6F01C7A99B7066885E8E36792F3EFA5016722B3BD31225D4E06407218EA995CFB4406F67F2D260B04424D0B0058336ABF0C53A012DD1E5CFAEA5435BFE9DC7F6840E8302838FA649AAD73AC11D499C8DBA0E0C88D2A9144FD124CD784930983F995A94A9F5546531A812092E709673AF16C8894803C04946BB39456A25DCAC
	62AD5CE25C73ED450A140597EF55125017225A14B929274AC59DBC12617447CF1825C1D81AA09A562456CD92EDCCD6A795E9C9892345241327F1662B98BC861985B6D35502073FCD82FBD8075D88BE348FD3362E01D14B5855D10BE3C0E1FF224B42FA4F6DD0700C4D0F69927FAFA9D9CABBABEDE31784CF41F15DCFB5658EA917E14F487CE6BF6656BA255702F04DE39E27DB46DB7A0A3435933D374A6D708CD7F19507BBA5D3D0753764EBE8F6C9272A10703CBAA3B1CF06855287C3476E109733308346953FFE
	9733EF5CECDCBB3D63DAA9DB7B440D45CC55C70A21BFB6872DBDBF7AFA7DEF16BC539BC81152BB2402A14060CF30B102111D78AB8F75CF6734C99BFD7873A73C688593AE409EE981651A9B3AA0EF688C7E4CBC2048CEBB45C681C55A4E5B0FF9712AF6C7E00475B5F4C12A873AC7EBDB121D2DD4928B01413EED4CBE6432882EED5F4FF9FD4F09B58BD32464B6E9135B6BF036B986413DBB240D4A08DF8E0CF85D4E895FD1AEA8F3BCD31D7C29A02ED0B4979BE7411410B072724457E6EECE585C86EEB361172AD8
	CD4C62D9C48DE74B08438F2AE4FF0702FF24486BAF93344F6CD276BEF67C35F349D2D2F02394A91D47B8E285C39F3034015D4AD3AFBCB26F694644354054A625311F0C02EA13D66B545868006C7F6C02AF6F4C1A3D7AF3E8198F1B455C9BA677C6855DF1486B9F856145A1EDA312B0517225C72A3F7C0DBF1FE5A6EA2C0A341B0F8D30D68D4B5E51AA7B50904485C24061899F085FA944E39DCBC81D2D93DF5CFE6249D78640F3DA2514516AEA76EF8D7B37967F2DE1189A0629290561C914A27E97CA1F20B6B3F7
	E305DA7495C2278FB9C6DDEED86152DB41E13D43930837CF0805G631D82F561A8915883BAECCED05D6D6A7294DC1730DADD5AD60AA3AC167BF5F502B712F2756EC145CD7D370F75F766043D73304C257EADC3D768370F55E676B79CAF41197C92FDF328B6235561729A5D238E37DBF138AF5BBDCE4CF1B4DF633F9D613C3FB6237CG55D1091C36560CF6DF52717CBFD0CB878831709E493E9BGG10D2GGD0CB818294G94G88G88G61CA24B031709E493E9BGG10D2GG8CGGGGGGGGGGG
	GGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG789BGGGG
**end of data**/
}
/**
 * Return the JCheckBoxSaveHistory property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxSaveHistory() {
	if (ivjJCheckBoxSaveHistory == null) {
		try {
			ivjJCheckBoxSaveHistory = new javax.swing.JCheckBox();
			ivjJCheckBoxSaveHistory.setName("JCheckBoxSaveHistory");
			ivjJCheckBoxSaveHistory.setText("Save History");
			ivjJCheckBoxSaveHistory.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSaveHistory;
}
/**
 * Return the JLabelHighLimit property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelHighLimit() {
	if (ivjJLabelHighLimit == null) {
		try {
			ivjJLabelHighLimit = new javax.swing.JLabel();
			ivjJLabelHighLimit.setName("JLabelHighLimit");
			ivjJLabelHighLimit.setText("High Limit: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelHighLimit;
}
/**
 * Return the JLabelLowLimit property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelLowLimit() {
	if (ivjJLabelLowLimit == null) {
		try {
			ivjJLabelLowLimit = new javax.swing.JLabel();
			ivjJLabelLowLimit.setName("JLabelLowLimit");
			ivjJLabelLowLimit.setText("Low Limit: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelLowLimit;
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
 * Return the JLabelMultiplier property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMultiplier() {
	if (ivjJLabelMultiplier == null) {
		try {
			ivjJLabelMultiplier = new javax.swing.JLabel();
			ivjJLabelMultiplier.setName("JLabelMultiplier");
			ivjJLabelMultiplier.setText("Multiplier: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMultiplier;
}
/**
 * Return the JLabelOffset property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOffset() {
	if (ivjJLabelOffset == null) {
		try {
			ivjJLabelOffset = new javax.swing.JLabel();
			ivjJLabelOffset.setName("JLabelOffset");
			ivjJLabelOffset.setText("Offset: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOffset;
}
/**
 * Return the JLabelSeconds property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSeconds() {
	if (ivjJLabelSeconds == null) {
		try {
			ivjJLabelSeconds = new javax.swing.JLabel();
			ivjJLabelSeconds.setName("JLabelSeconds");
			ivjJLabelSeconds.setText("sec.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSeconds;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStartCode() {
	if (ivjJLabelStartCode == null) {
		try {
			ivjJLabelStartCode = new javax.swing.JLabel();
			ivjJLabelStartCode.setName("JLabelStartCode");
			ivjJLabelStartCode.setText("Start Code: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStartCode;
}
/**
 * Return the JLabelStopCode property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStopCode() {
	if (ivjJLabelStopCode == null) {
		try {
			ivjJLabelStopCode = new javax.swing.JLabel();
			ivjJLabelStopCode.setName("JLabelStopCode");
			ivjJLabelStopCode.setText("Stop Code: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStopCode;
}
/**
 * Return the JLabelTickTime property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTickTime() {
	if (ivjJLabelTickTime == null) {
		try {
			ivjJLabelTickTime = new javax.swing.JLabel();
			ivjJLabelTickTime.setName("JLabelTickTime");
			ivjJLabelTickTime.setText("Tick Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTickTime;
}
/**
 * Return the JLabelTransmitOffset property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTransmitOffset() {
	if (ivjJLabelTransmitOffset == null) {
		try {
			ivjJLabelTransmitOffset = new javax.swing.JLabel();
			ivjJLabelTransmitOffset.setName("JLabelTransmitOffset");
			ivjJLabelTransmitOffset.setText("Transmit Offset:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTransmitOffset;
}
/**
 * Return the JPanelPowerValue property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelPowerValue() {
	if (ivjJPanelPowerValue == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder.setTitle("Power Value");
			ivjJPanelPowerValue = new javax.swing.JPanel();
			ivjJPanelPowerValue.setName("JPanelPowerValue");
			ivjJPanelPowerValue.setBorder(ivjLocalBorder);
			ivjJPanelPowerValue.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelHighLimit = new java.awt.GridBagConstraints();
			constraintsJLabelHighLimit.gridx = 1; constraintsJLabelHighLimit.gridy = 1;
			constraintsJLabelHighLimit.ipadx = 4;
			constraintsJLabelHighLimit.insets = new java.awt.Insets(34, 25, 13, 3);
			getJPanelPowerValue().add(getJLabelHighLimit(), constraintsJLabelHighLimit);

			java.awt.GridBagConstraints constraintsJLabelLowLimit = new java.awt.GridBagConstraints();
			constraintsJLabelLowLimit.gridx = 1; constraintsJLabelLowLimit.gridy = 2;
			constraintsJLabelLowLimit.ipadx = 5;
			constraintsJLabelLowLimit.insets = new java.awt.Insets(15, 25, 45, 3);
			getJPanelPowerValue().add(getJLabelLowLimit(), constraintsJLabelLowLimit);

			java.awt.GridBagConstraints constraintsJLabelMultiplier = new java.awt.GridBagConstraints();
			constraintsJLabelMultiplier.gridx = 3; constraintsJLabelMultiplier.gridy = 1;
			constraintsJLabelMultiplier.ipadx = 8;
			constraintsJLabelMultiplier.insets = new java.awt.Insets(34, 21, 13, 2);
			getJPanelPowerValue().add(getJLabelMultiplier(), constraintsJLabelMultiplier);

			java.awt.GridBagConstraints constraintsJLabelOffset = new java.awt.GridBagConstraints();
			constraintsJLabelOffset.gridx = 3; constraintsJLabelOffset.gridy = 2;
			constraintsJLabelOffset.ipadx = 25;
			constraintsJLabelOffset.insets = new java.awt.Insets(15, 21, 45, 2);
			getJPanelPowerValue().add(getJLabelOffset(), constraintsJLabelOffset);

			java.awt.GridBagConstraints constraintsJTextFieldHighLimit = new java.awt.GridBagConstraints();
			constraintsJTextFieldHighLimit.gridx = 2; constraintsJTextFieldHighLimit.gridy = 1;
			constraintsJTextFieldHighLimit.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldHighLimit.weightx = 1.0;
			constraintsJTextFieldHighLimit.insets = new java.awt.Insets(30, 3, 11, 21);
			getJPanelPowerValue().add(getJTextFieldHighLimit(), constraintsJTextFieldHighLimit);

			java.awt.GridBagConstraints constraintsJTextFieldLowLimit = new java.awt.GridBagConstraints();
			constraintsJTextFieldLowLimit.gridx = 2; constraintsJTextFieldLowLimit.gridy = 2;
			constraintsJTextFieldLowLimit.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldLowLimit.weightx = 1.0;
			constraintsJTextFieldLowLimit.insets = new java.awt.Insets(12, 3, 42, 21);
			getJPanelPowerValue().add(getJTextFieldLowLimit(), constraintsJTextFieldLowLimit);

			java.awt.GridBagConstraints constraintsMultiplierSpinField = new java.awt.GridBagConstraints();
			constraintsMultiplierSpinField.gridx = 4; constraintsMultiplierSpinField.gridy = 1;
			constraintsMultiplierSpinField.insets = new java.awt.Insets(30, 3, 11, 32);
			getJPanelPowerValue().add(getMultiplierSpinField(), constraintsMultiplierSpinField);

			java.awt.GridBagConstraints constraintsOffsetSpinField = new java.awt.GridBagConstraints();
			constraintsOffsetSpinField.gridx = 4; constraintsOffsetSpinField.gridy = 2;
			constraintsOffsetSpinField.insets = new java.awt.Insets(12, 3, 42, 32);
			getJPanelPowerValue().add(getOffsetSpinField(), constraintsOffsetSpinField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelPowerValue;
}
/**
 * Return the JTextFieldHighLimit property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldHighLimit() {
	if (ivjJTextFieldHighLimit == null) {
		try {
			ivjJTextFieldHighLimit = new javax.swing.JTextField();
			ivjJTextFieldHighLimit.setName("JTextFieldHighLimit");
			ivjJTextFieldHighLimit.setPreferredSize(new java.awt.Dimension(58, 20));
			ivjJTextFieldHighLimit.setMinimumSize(new java.awt.Dimension(58, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldHighLimit;
}
/**
 * Return the JTextFieldLowLimit property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldLowLimit() {
	if (ivjJTextFieldLowLimit == null) {
		try {
			ivjJTextFieldLowLimit = new javax.swing.JTextField();
			ivjJTextFieldLowLimit.setName("JTextFieldLowLimit");
			ivjJTextFieldLowLimit.setPreferredSize(new java.awt.Dimension(58, 20));
			ivjJTextFieldLowLimit.setMinimumSize(new java.awt.Dimension(58, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldLowLimit;
}
/**
 * Return the JTextFieldStartCode property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldStartCode() {
	if (ivjJTextFieldStartCode == null) {
		try {
			ivjJTextFieldStartCode = new javax.swing.JTextField();
			ivjJTextFieldStartCode.setName("JTextFieldStartCode");
			ivjJTextFieldStartCode.setPreferredSize(new java.awt.Dimension(45, 20));
			ivjJTextFieldStartCode.setMinimumSize(new java.awt.Dimension(45, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldStartCode;
}
/**
 * Return the JTextFieldStopCode property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldStopCode() {
	if (ivjJTextFieldStopCode == null) {
		try {
			ivjJTextFieldStopCode = new javax.swing.JTextField();
			ivjJTextFieldStopCode.setName("JTextFieldStopCode");
			ivjJTextFieldStopCode.setPreferredSize(new java.awt.Dimension(45, 20));
			ivjJTextFieldStopCode.setMinimumSize(new java.awt.Dimension(45, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldStopCode;
}
/**
 * Return the TickTimeSpinField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getTickTimeSpinField() {
	if (ivjTickTimeSpinField == null) {
		try {
			ivjTickTimeSpinField = new com.klg.jclass.field.JCSpinField();
			ivjTickTimeSpinField.setName("TickTimeSpinField");
			ivjTickTimeSpinField.setMinimumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTickTimeSpinField;
}
/**
 * Return the MultiplierSpinField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getMultiplierSpinField() {
	if (ivjMultiplierSpinField == null) {
		try {
			ivjMultiplierSpinField = new com.klg.jclass.field.JCSpinField();
			ivjMultiplierSpinField.setName("MultiplierSpinField");
			ivjMultiplierSpinField.setMinimumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMultiplierSpinField;
}
/**
 * Return the OffsetSpinField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getOffsetSpinField() {
	if (ivjOffsetSpinField == null) {
		try {
			ivjOffsetSpinField = new com.klg.jclass.field.JCSpinField();
			ivjOffsetSpinField.setName("OffsetSpinField");
			ivjOffsetSpinField.setMinimumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOffsetSpinField;
}
/**
 * Return the TransmitOffsetSpinField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getTransmitOffsetSpinField() {
	if (ivjTransmitOffsetSpinField == null) {
		try {
			ivjTransmitOffsetSpinField = new com.klg.jclass.field.JCSpinField();
			ivjTransmitOffsetSpinField.setName("TransmitOffsetSpinField");
			ivjTransmitOffsetSpinField.setMinimumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTransmitOffsetSpinField;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
	
	Series5Base fiver = (Series5Base)o ;
	
	fiver.getSeries5RTU().setTickTime((Integer)getTickTimeSpinField().getValue());
	
	fiver.getSeries5RTU().setTransmitOffset((Integer)getTransmitOffsetSpinField().getValue());
	
	fiver.getSeries5RTU().setPowerValueHighLimit(new Integer(getJTextFieldHighLimit().getText()));
	
	fiver.getSeries5RTU().setPowerValueLowLimit(new Integer(getJTextFieldLowLimit().getText()));
	
	fiver.getSeries5RTU().setPowerValueMultiplier(new Double(getMultiplierSpinField().getValue().toString()));
	
	fiver.getSeries5RTU().setPowerValueOffset(new Double(getOffsetSpinField().getValue().toString()));
	
	fiver.getSeries5RTU().setStartCode(new Integer(getJTextFieldStartCode().getText()));
	
	fiver.getSeries5RTU().setStopCode(new Integer(getJTextFieldStopCode().getText()));
	
	return fiver;
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
	getJTextFieldHighLimit().addCaretListener(ivjEventHandler);
	getJTextFieldLowLimit().addCaretListener(ivjEventHandler);
	getJTextFieldStartCode().addCaretListener(ivjEventHandler);
	getJTextFieldStopCode().addCaretListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("Series5SettingsEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 360);

		java.awt.GridBagConstraints constraintsJPanelPowerValue = new java.awt.GridBagConstraints();
		constraintsJPanelPowerValue.gridx = 1; constraintsJPanelPowerValue.gridy = 3;
		constraintsJPanelPowerValue.gridwidth = 4;
		constraintsJPanelPowerValue.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelPowerValue.weightx = 1.0;
		constraintsJPanelPowerValue.weighty = 1.0;
		constraintsJPanelPowerValue.ipadx = -10;
		constraintsJPanelPowerValue.ipady = -24;
		constraintsJPanelPowerValue.insets = new java.awt.Insets(15, 5, 6, 5);
		add(getJPanelPowerValue(), constraintsJPanelPowerValue);

		java.awt.GridBagConstraints constraintsTickTimeSpinField = new java.awt.GridBagConstraints();
		constraintsTickTimeSpinField.gridx = 2; constraintsTickTimeSpinField.gridy = 1;
		constraintsTickTimeSpinField.insets = new java.awt.Insets(30, 2, 3, 8);
		add(getTickTimeSpinField(), constraintsTickTimeSpinField);

		java.awt.GridBagConstraints constraintsJLabelTickTime = new java.awt.GridBagConstraints();
		constraintsJLabelTickTime.gridx = 1; constraintsJLabelTickTime.gridy = 1;
		constraintsJLabelTickTime.ipadx = 25;
		constraintsJLabelTickTime.insets = new java.awt.Insets(35, 17, 4, 18);
		add(getJLabelTickTime(), constraintsJLabelTickTime);

		java.awt.GridBagConstraints constraintsJLabelMinutes = new java.awt.GridBagConstraints();
		constraintsJLabelMinutes.gridx = 3; constraintsJLabelMinutes.gridy = 1;
		constraintsJLabelMinutes.ipadx = 21;
		constraintsJLabelMinutes.insets = new java.awt.Insets(35, 3, 4, 3);
		add(getJLabelMinutes(), constraintsJLabelMinutes);

		java.awt.GridBagConstraints constraintsJLabelTransmitOffset = new java.awt.GridBagConstraints();
		constraintsJLabelTransmitOffset.gridx = 1; constraintsJLabelTransmitOffset.gridy = 2;
		constraintsJLabelTransmitOffset.ipadx = 8;
		constraintsJLabelTransmitOffset.insets = new java.awt.Insets(7, 17, 16, 1);
		add(getJLabelTransmitOffset(), constraintsJLabelTransmitOffset);

		java.awt.GridBagConstraints constraintsTransmitOffsetSpinField = new java.awt.GridBagConstraints();
		constraintsTransmitOffsetSpinField.gridx = 2; constraintsTransmitOffsetSpinField.gridy = 2;
		constraintsTransmitOffsetSpinField.insets = new java.awt.Insets(3, 2, 14, 8);
		add(getTransmitOffsetSpinField(), constraintsTransmitOffsetSpinField);

		java.awt.GridBagConstraints constraintsJLabelSeconds = new java.awt.GridBagConstraints();
		constraintsJLabelSeconds.gridx = 3; constraintsJLabelSeconds.gridy = 2;
		constraintsJLabelSeconds.ipadx = 21;
		constraintsJLabelSeconds.insets = new java.awt.Insets(7, 3, 16, 3);
		add(getJLabelSeconds(), constraintsJLabelSeconds);

		java.awt.GridBagConstraints constraintsJCheckBoxSaveHistory = new java.awt.GridBagConstraints();
		constraintsJCheckBoxSaveHistory.gridx = 4; constraintsJCheckBoxSaveHistory.gridy = 1;
constraintsJCheckBoxSaveHistory.gridheight = 2;
		constraintsJCheckBoxSaveHistory.ipadx = 1;
		constraintsJCheckBoxSaveHistory.insets = new java.awt.Insets(44, 3, 24, 31);
		add(getJCheckBoxSaveHistory(), constraintsJCheckBoxSaveHistory);

		java.awt.GridBagConstraints constraintsJLabelStartCode = new java.awt.GridBagConstraints();
		constraintsJLabelStartCode.gridx = 1; constraintsJLabelStartCode.gridy = 4;
		constraintsJLabelStartCode.ipadx = 8;
		constraintsJLabelStartCode.insets = new java.awt.Insets(10, 17, 8, 27);
		add(getJLabelStartCode(), constraintsJLabelStartCode);

		java.awt.GridBagConstraints constraintsJLabelStopCode = new java.awt.GridBagConstraints();
		constraintsJLabelStopCode.gridx = 1; constraintsJLabelStopCode.gridy = 5;
		constraintsJLabelStopCode.ipadx = 10;
		constraintsJLabelStopCode.insets = new java.awt.Insets(9, 17, 59, 27);
		add(getJLabelStopCode(), constraintsJLabelStopCode);

		java.awt.GridBagConstraints constraintsJTextFieldStartCode = new java.awt.GridBagConstraints();
		constraintsJTextFieldStartCode.gridx = 2; constraintsJTextFieldStartCode.gridy = 4;
		constraintsJTextFieldStartCode.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldStartCode.weightx = 1.0;
		constraintsJTextFieldStartCode.insets = new java.awt.Insets(6, 2, 6, 3);
		add(getJTextFieldStartCode(), constraintsJTextFieldStartCode);

		java.awt.GridBagConstraints constraintsJTextFieldStopCode = new java.awt.GridBagConstraints();
		constraintsJTextFieldStopCode.gridx = 2; constraintsJTextFieldStopCode.gridy = 5;
		constraintsJTextFieldStopCode.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldStopCode.weightx = 1.0;
		constraintsJTextFieldStopCode.insets = new java.awt.Insets(7, 2, 55, 3);
		add(getJTextFieldStopCode(), constraintsJTextFieldStopCode);
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
		Series5SettingsEditorPanel aSeries5SettingsEditorPanel;
		aSeries5SettingsEditorPanel = new Series5SettingsEditorPanel();
		frame.setContentPane(aSeries5SettingsEditorPanel);
		frame.setSize(aSeries5SettingsEditorPanel.getSize());
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
public void setValue(Object o) {

	Series5Base fiver = (Series5Base)o ;
	
	getTickTimeSpinField().setValue(fiver.getSeries5RTU().getTickTime());
	
	getTransmitOffsetSpinField().setValue(fiver.getSeries5RTU().getTransmitOffset());
	
	getJTextFieldHighLimit().setText(fiver.getSeries5RTU().getPowerValueHighLimit().toString());
	
	getJTextFieldLowLimit().setText(fiver.getSeries5RTU().getPowerValueLowLimit().toString());
	
	getMultiplierSpinField().setValue(fiver.getSeries5RTU().getPowerValueMultiplier());
	
	getOffsetSpinField().setValue(fiver.getSeries5RTU().getPowerValueOffset());
	
	getJTextFieldStartCode().setText(fiver.getSeries5RTU().getStartCode().toString());
	
	getJTextFieldStopCode().setText(fiver.getSeries5RTU().getStopCode().toString());
	
}
}
