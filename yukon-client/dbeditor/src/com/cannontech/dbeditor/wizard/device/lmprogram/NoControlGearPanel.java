package com.cannontech.dbeditor.wizard.device.lmprogram;

/**
 * Insert the type's description here.
 * Creation date: (4/12/2004 2:39:47 PM)
 * @author: jdayton
 */
import com.cannontech.common.util.StringUtils;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
 
public class NoControlGearPanel extends GenericGearPanel 
{
	private javax.swing.JComboBox ivjJComboBoxWhenChange = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldChangeDuration = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldChangePriority = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldChangeTriggerNumber = null;
	private javax.swing.JLabel ivjJLabelChangeDuration = null;
	private javax.swing.JLabel ivjJLabelChangePriority = null;
	private javax.swing.JLabel ivjJLabelChangeTriggerNumber = null;
	private javax.swing.JLabel ivjJLabelChangeTriggerOffset = null;
	private javax.swing.JLabel ivjJLabelMinutesChDur = null;
	private javax.swing.JLabel ivjJLabelWhenChange = null;
	private javax.swing.JPanel ivjJPanelChangeMethod = null;
	private javax.swing.JTextField ivjJTextFieldChangeTriggerOffset = null;
/**
 * NoControlGearPanel constructor comment.
 */
public NoControlGearPanel() {
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
	if (e.getSource() == getJComboBoxWhenChange()) 
		connEtoC1(e);
	// user code end
	
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JComboBoxWhenChange.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.jComboBoxWhenChange_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxWhenChange_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC10:  (JComboBoxHowToStop.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
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
	D0CB838494G88G88G8CEB0CB0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155DC8DD8D457197607E4ED62AA59EAE3DB72BCB6CDD33AA1D9F6F5B7EECAD31B5A2DCDED57B634312DED6D0636E69F53E5F75D566D5AA70799C00C481F8491890E7887A9C59414FF65DFC0D4229384031501394060B0B34EDCF4D4547D4EB977BB774EF0E798CD3BCF56677938773C677B4E77F33EF36EB9675E11E46FD91C14E048A6A4A109A87FAA9388B1F793527E425DBD08651E0CB5924D3F3DC019
	A435B5912EC5D037E0DE2CB103B45424C0398E4A298F459AF7427D8EE27C430AD6388920A7826A1A8FDC5B92DCCF5DED5433051553F2D2611A8654G1895A87AA455FFB0A7D74167C0B9CD392714E0A0E42001725872F2B578C6052F2B01DF9B9AA9CFE3CE3ED29F8978F220EC400B9A55B637B26B9E2A29D24A7D0FC5120DAF979836D3DD1A761166A832D14A55C41EE601B0127236CE9A336E2BA3A7061C6325C39607436910252151D26B20E43549CEF7698D5BAD0B5BDAEA152E5B0624D27B384B6D9CF1DB46
	EBEA4ED8BC3264EE3EB9E417CEC996F73745A1598949EF22BE4C4FCBE16D939287BA393D5F59DE27583360EAA4D977594C00768EB4F1BB431AD4FBE31E5D12D451CC5B9EE37127F2AB20FEB39077E5E8347D01FDB96BEC86DE3753AEBC61FE7CDD8E7A61855D1B187EA7F7D17D51F03F92E8DDB37AD7FF887D6B3D7DBC59B458E8C88F60DFDD8B774B5A227A67BD78E252AE16DFABA7E90EAC063A95C0EB00B6836537469A6B00F6333C1EFD7BDD38B68E4A4D4E71F127235165EA32DB5DE71CD6498E77B5B5D023
	E06725A1275BCA884F5D85F9A5BA76A019DBFD73D64D350B84F3AD6A7CF3C416AB8DC9BA3EAD7D1E49B02A34B1592A7A467D325E25FEC5G3E8CE8B550FA2014B6486DB6746BDB374B27732B837A546D3058AFCC8C3AACEE39E942E637CAEE45BFD7CEB07F74084B5D3DEFB99E1748F2EDDC56AEE9B2EC51094B5A48C30616B69E0F7EB6B5AEDC777381F5CF36CD6D8FCA0B5BE89E666451BC8C077B84A5C7B9EEE2F81C866763A61265ADADEF043A5ED318373DE968C3D76213A463C8332E8F0BDE2AB2441C6276
	441E726F5B86C267AC528EF98AD4815485E4871A8394510E7D1BBBF9B8443C6D5674AD177D4EE48EDCA52F6C36F4B8BC3245B1A475D85CB64B20DD7290056707135476C19F391F3271B484BACEC98E49ED11EDCE8734EE9917A011BC1CE77DE4A665F1CB2C56EC9B17C041380BE03B7113BB60EA07996FFD1755A2CBAA872F5FB4C9E3F95D5283ADC283F03F211D467C99B68F46407D9AA09E2B71840A15F5449A5BBBB0AE1EBBD9F0BD8D09AD59EBEADAECC3548C0B7BA66A9F3FCB6792693A6410DBA417643042
	0DC729136FEED563B26C94B65FE2B89DA34DE038C760B36EE6F29C5A50C6996B6730FA0BAC3BED039332F4C112E51BE3C4158FE736F8FC061F07A8E35CC0F32C9F7C12014204FF3B6E6D50785765F4DE1DF019E94FA9F17AB61BE79781FFF487BEE7737368F3B736837374D73D1827AD5B1FA5A55FEB544DD376DC6D6079195A69BF568E32B6D59D4F6D6A3D4FF19F5B21BFA6834FADDF3FEF9D467B6065F1FF55C863DE817EF68339BAB16E66FBA6BE5E1AAC9E89C758B9B77418E4D5330C73F61240B9769E514F
	310BC4BF47AE1360B9A4913D9C62F5F6C21FAD4E9B8ECDCEDEA3D9EC8CBB9C92B3DA08907E6C7F1519464266304997ED5691C93EE03BA5C1BFF7525CFA090D41B9F09F510939F4E39F66527EDBDFA04B2F646BBE4F96FD2E5630D149211453EAAEF11B5C89B46612DD9A07C0B5BBA79CB2473DECFCAB38B23F0C9974F9DD861A0FD7251B8A1FECC8A6F49E31CFC84237E84BB69AE337ED9C3A0A8ED8E5AD79739C6DDA32886C236B4EC938061F46B5516FF2E051CB74D6EABCEFB21228DD11409F8394FB9A73A65E
	30CBB3DE1504E9F65A6DBC7C22DFFE8CF6C0BFCA57A6A4180E6900BEADFC09B3E6795575A8FE717A8DC66A17DDBB971C26FE7DC02C9D693302E3718C7B918C2807927159FBA6C33B7E35FAAAF47B10D2D9976F3B16AE7F67D6EBB2ED578A385C053E0FA4EA672AC60F47B9E4E30F8525BFEA3DED64C157196C89E49B1A32DEBE37135AC163B1278B57194FD5523ED98E65B5C071423E1C24297935BB69AD388E5B5C9ED89FD9A5AF46F8EF920D95FDEEA9B02E670EEAE337FCDDD14058251C61B14BBF639F3BC737
	D0BB2AG6FBA0336C5A4E5EAE287EBCFF8CC0FC04F22BD1D49C61E6FCA79ECF29266BB4179523C9357C383502E8CB487A8C2519FFDE66A7C4B57BD05D5B4DEAB4E782F07CA989EFB46FFBD34349257C3F1D057FE961F4FB3D328DFD1D00EB90BFE3D163CDD63D73753661005CF61DB6818F53AEDA3B687E28B9986ABEFB60971E75CD556472B204D75C09CEBAD2758E6A8E71FC36C3F98D68465860135D5D04C8C65D101FD6D984566C27929F378BCAB2A227349CAD1BE4A4A6BC579054A7BFD967993EDBB78736E
	158F2DA358730F477761945A17DED1FE0415EFAA32BB482C1D343FDCEEE9D8F2434C459F8724251B466D532C9FD261BE97087377175371E3315BC69CF4D475A26FF7D9DF4C017B88411BDA61435B073C7BD99F2D027BF50277F2C5B29BFFB71D9310B05E77A81FDC0173F6AA147783F5818D3C4757BEFC0E795B9BF4AE9806B9839E1B97DC5290B11F2732EB0F216CF2600F855A88148DF59567D167CB37683C5B49661766D198FF5299C99EF5DA718CE228CA7B5C08B84F75AE07EBAC500673748CE40CBE1E15D8
	56141B606A10EE1846AC57AD25169BF2E98BACFF9DF421DBD4D1F63444D4D1DED64C671545B73473CA55DB250167648B4A1C2CDCD313F613BDCC9F4CB9C47D47EDF83D1C1E45887D30FBF0B99D907C522E56B67315462E0ED3E74F341EB583CF60B6EAABCC7AED184FF52BCD7039A122824F98A6413E259730DF0E8C50FD8E1B7B31A118545DB6EB13E5240B75B21F4F63D8AED0798D97F0BE4FBAC65B5CEF06FDBC10590CEB916FC1BCB708FA7AC952B523C977DCE41D3958E0B773D8051B5539F86E419713384E
	E547D167AA28CFFC1FE23FB81E6DB72EAE431FD238727A5CCAEDFD26A22F56F77A54D3FB8F7848CF7A54A7E96439EF5FBC003EED7EE6B4317FFDD940B31F2E7739CFAE653A7815BC927955E713F85C7F33B8D79BF7EF2967868C0C1A1ACE4DD94D272BD2949FA25F47187F78900DF9EA8F0CB3203A9E0C79BFFF204D530CCF659A928258E56E61768C7478BF7FAAB2293E8500C77760F362EF52FF8F579E4B18320BEDE56B81367086305B6DF4C9EE79A6C759EAC819B31F2CF830F30B688714FB7D3E65783374E1
	16432BFB7833B4C16073993ED60373F114FB8C4751FA286B3A084FD8A90B76E7B414D7814581ED864A6F0DB5D675E2ACB7251711E94E8A4CCED797ECD86C1AEDAC776FCB479F6CBC67ABDBB0DF173E7057E4594371895B8265EBAF4F873BF22D7AEE9E4339FF1BF63033AE28DB863496E8A3D0CADF2C5154077EB5E414F07FE8BAB44A32E5E8142D6850399E1BF4C30569C3007B3638CA7BFC2D00765A017A7B147153277FFC8E83FC91D01426FE398EAF6F4F470A581A290F7767CE01FF0E612917D41C7773EBEC
	1C51F28B54118F301FF73FCD232ABC33B38E849A9B60EDA78C6BF303E330E2B4DBC604CCF5A635C96CA37910D16A9AB2695CC377FCB6F8985FE2CB748B40689861F11AFB0C08FCDF2839AFB876E157A7530F2F4ABF2B8E6E3FA3AB70FEFDA2CBFF3FFEBBCBFF3FBEB3AB78FE7DE3D901776B61D95A7DBA6F77A2366F0C01BCD8833499A8239F6660FE4C7D029D05BC77F5723E95B6704DCEF642A7237E046C403E26E4CFFFAE1819CD57EC03CED9F60E5376915F19CD7BCBF63A9848317CEC9AA3D84E0F4A1CD541
	8B3369FCEF1706994C5760AF31E7CE8378E56E47F5F9A3FB8F650572DC01AD2F25D8B414D78AAC330EE2EB213CD1E01147F00F10F299B1CB99457221DCA2303293CCAF14FB85B66B846E876C17F13D7FF8911DE3E2203C96A8710F5087C0ADC0F62085C0B1C06B01322F449A4B2E205CB3CC2E9B4AAE20F020E82055C071C09983408B548D64828A9FC039578FC56B64AB55CB2F6B7F68BB5FD03B8265F84B9F27566DD7B06A8B5A0BF5F68D7F829DD9FF0A5170F05BF30B74EC5F22DCF778614176A85BF4302D4A
	150E95BAA96E5454ED575C2707B07EB3274C85E5B90147C7E54E746323011D3FCA1057661BAE09E32DB9ECDFBBBC6C1144B86C4C4990D807275B693151D9172F2FFFD0154C46181B9DA6733DCE3C58EBC4C30E2C84A29668BBA03AA765F23DB5D377C84E9A14AB3170BDD28BDCFBFD64DE2D555F077D9B9BEBAE608D33A0EFD19D6D13F10B57B6BEB14EF6031C7769FAB6AE01AF8608E35FE1E3F0BD14B78AF9EF995DC78FBBA13C0CE79E9B1326C198130348F36A047E9E728A9B177D4067820A9D2458A7CF3484
	69DFDCC3EDD172ECB148AC85BA2060AB8755F59657EDF3507936334BB208FB3E16897E260357AF192473F2E77385174D51E6136CD6DFA6510E44770E2C1DB3B687BC8ABC4D234005753DCD7EFA3A5DB6275BA65F647547FBF475A01350B3FEF217DFBBE618F0C7A477590971C1769C61FC7566GFC671434D478A6363FC3794C10659A357A3C174D85FAB67A9940F9F71D4A907AAF0ECA8E4E4A6BF6FAF630F61C63034EA627D72DA666A13677AA526E0D7207F86EB640155757E4EB6BA3147A55225E3DCFCFBECE
	544FCB532B5FA46AF3AF6B351FEA457A97727464CD227E9BD5FA759522FE469EEDFD3B15573BC47DC03D1E7EB0D1EFAD50EBFF1128CF7431AFCAE9BF962E4A5E229A4F6F5683E612F00F7E4BF70A097E796C48042DF4C23659EBEA4CB659AEB5A9D315F286BBD00D7B67DE89770F73180EF0A8C782AD17D07763B5382FD983D846B06CDDD89BBF2D41B65A07310D6D3514F786149781C58385BE23C82EB311694FA878994D3A1D2A5C9347E81B814EE84A4B0F15182A2A0A8917E3CBF3C52E27ACF439D3852A5C7E
	93057EF2ED906422C2AB2C451DA305A526329253112345414EE32E1FF870B31D7813FA983773BA7BEE086F67FE160A7B397CE7BECBDADE0DCF58C77477F3AB0779FAA0F7C45D6747C5EF41730761G678FECE660FD7FCEB13505BE073BC7701C61CE1B761B171874E2C3207D64D2C5EF2CC6FF5D4197957DF70F9F546674D57BC8695890DD41178E53998C8CD127B34C67EBEC3DCA6D489E457C7C42BE443A847635B434BBCCE067AEA35FFA017DBBFB87C3E3EE32A1362D9A31B09B0E3F83C5797A319233B317FD
	FE0F50A15AFB3C9EB15398E21FAEC02CF78CF50C58F76B6B4059D809C1AD3319BEC74347F00DDAD0BD5D59404AB15F75A317AB19D62E402F4C6524E9657C096F358DEC0E099B73BFEB59467078B17F33168555B8A7ED063A012B686F93B58FF6C6744C834A455D379C77EBA61BA7D7DC75777791062F3E6A6F6F0EDA74F7AD54B558714C61B7A63A36FA8A4A4B016200B683E50C479A0B006A003A012CC013C08B00A201E200D6832D835A881462403D6A97775067C920B3BD657017EDD85D967EDE08472130587F
	8CA09F5AACF3A8675E8E7DBDCAAF60F63F3A99D0DE6007F1DF1FAA244F48C828DB813496E8A3D03693EDCFB2CD7BBDD52BB74877D4573331AFAB1CDCFF3BD335239F6EDDCE3D31725CBEED8E04814FA220A805F739F32A6FDC6E65B47F1845826FFA05FF330E9C258C976C91DC3EF5D5D06EF26959E63EAC5EAFAA753206EF8E5CC7684A7DBA4F5F36E860DB21702ED12E9B7CE449B5680FEB3ED891146B003A2F796FC1B32A7DF5D801472B704DD52E0B7D5ADB8A6515FE58DAA84783A53A7DF5446CC17FF3153A
	928DCF835C1B5DFA7EC754235C28D2BF29618B077B27F4659697E8F3E199702C82DA2770EEF4874A0586712DE20AC7F9876DD1F94BBCD365689E40C8166F207BE91B43EEF3C84D7C2BAF51E6CA8D9177191A7BCFCD097B1F12380EA57F87BA7E22D675A9DCF33F2B764F2F7B681FD5871F4F366D8DFC4E1135F77AF30E5DFB754F81776E1DFA8EF8F02F7EB9E049DE6DB9A0470E6C256B1291F896748A3E63826B93D8ADEB13E297EDD6F9D4600D4CAE0A37CBD4A1622718DDB798562BE0DD9A2C8F3F4F5ACB4FD0
	4065D1279BE5FB7632B38B1B5D4E4B17980F4D81FBE1910FAB4CC61B4B22315BAAB0EAB7BFDBECE1EB5B9698F37D9EBCEFDC48B09714E7886C378C1B8B65278456B9C931E5D00E95583DACF6468265CD82DB1507671729B2E2172E333393A84BB2BE53228A83674012C27DF36FFFAC54BF777EF2E170EFA13FDE9868DC1C5B771759F8DEBBC758FCBA8DE395A130CF2421EF5182BB49760AE4A25618BBA13E695807F1A99158338958DE0340B6A5E06C4D826BB3200E8101ED9036C48B2CB29DE5D788ECD1BA765B
	EA01FD5C067D96A73005C4741B40A2055E546B08FDEBAB76DB3F402E32BD058B4AF385D6D40176AD961827825BDBAA3056EA0CD5628D44F2F723EC2E4006F623EFA582DB319BFD2B92D8A6EB2F8B4AAE01C95568EF1840DE4FC3FF9789AC6B925A97A530CF66213F1B85E6E56F86B23C306673A2F6349AEDEE91589D0675C2F9D4E07FD20DB64B82EB4BC31B6788EC2789ED0E9058F0BD5A9CAD3055C9E8738A017D3C9FED4E3EA9623CE72A4D2FB02C88F8DA845FFB7568C72F40A26A457B8701B976E03F4588EC
	C9BD0E1955827BD4813697A7301F97E0FB1B84D6F8925BEB3F0558738568EF3F40727A30BD1740A40605C1F90140DE6EC39D0B85F6B18F6D0B97581DCAECAF71B6E2ABABD1B677B61E81FFD02CFFA66DE17362A870CD8A5E28E2BABF4BB0D971F30B6A7D54B5DE17792E7EF9F5713B341DC8E8E329E847B29978DB27DBEC6EDDC7ED967C1F4872BD4B0EE2F3F16EA42C21A771FC7A353CE06753341DA465CA498CF21470FCFA946EFF73661BEFEE977EDC3B4E7C31B97C7C79981B5FE5601FB109676F7F406654A8
	A8AF93181C06710B93B6BE1B964C4624E9EC4C3893EB24BF8DBBA4EC6C29510F39034D537940DFF4876D79951B274DD09E90587793504670BB789DD5BFD5C7383DAEC3B0FB37CCE3EF8C34C96D7B9D44156BFBBB9D752D947A9E2147C40A5DDFE1759B210E5CC59BE3581CDE8265AA013D4866F4AB14DDF7B136DF8EEA6B56E9ECDD8C6D7CD269FF2E63576CF9908578323B180733AE8779C60FBDAB6200B7DE5879DE857A1BFF8F6DFC24A2181D5B2631339D5A29D5F2C069EBB66F1A819F3807FA9BF7235E3977
	B04E7FF1C80D739B2CFE99542D38A75EB7D55335A8AC46EC37E01CD96C74688B5F1621DCB353C56D0B973A66B1ACFF86645A8C4432D87BE6A88F4CC03F0BAFCDB7BE378471BB9C5A793CCF7F70EF64A3G0F1C01BE5C2E5A6163C3A34EEA0A4C42AA34BFD6587AC1B5460A4C44F696D47B4EF3EF972B315B4646D68970D64D44B61CF9582ED5E08D6CB9978E6588014DE74F1115D00E1D09F179E552F4F99BACAE0933E20D33A1AE89423EB416079900674FC2FF7EF5CF603CB5B2BB5B0157BC8B6D3CD50FB119B1
	8B6DDCD2BFDD5EEA6D0C1A65EB67F2A89FD36E390E4759F3F69560EB045E35CCEF62EC381F0D58D78B50163A59E84B6602696651E0B19B1D2D4E23DC47E9760C16811FA1742EE3FA23203CCCE04DFDE84B7A59985B49BE5FDC2922BFF3D4664F1F75CD177346A0F6E6077173502D424E99AC4FCC00170421CDCFD722CD3DE1981F1FD6CE979F2352AE3D0605796ADD8465C7157B406FC29EA9B611D05F7BFC70FBD56E64E41077B7C74A4ACBCCC7AA707D4D6CD2D5CE4E8ADDEE4F91D5EEF95E07F96F435BEBBA2A
	5ECB65EA5B6FE407EE531C8AD52EA12DA0309CFD05F5342C98F99FCD8BBD4E4F9CD36D7C76F1D55F7B7B02682BA8D374F139F72BD5394FA4049ABB4E7FC6C268361E29D175142407AE672AD5656658C20F7F676BB439CBC217331CD465A28C2146034B266CD57BA33AD0ED67FF7AC3577F4ABECD3ED5844B1BB21F3C998D695BF12E63577BD5BBF79DD475BD11B7CD5E142B7AFAF7072E2F6E102AA339BAD8ACAA14D8F05ECB0CD7737A2C74827D0B1F5EF958FBD8450FABC52A0E1F640522034B7D533B9A5BAE5D
	DFFF3F7EFBB50E29FFD05B692B8A5D47282AD0FC643C6E6A50E3FED8B3175606948F4E7BF0DE68B1BFFFD85571C1FD68B1DFF2CC15DB11F4FFB17F7EF1B5667FDD2936D333A7F47D1F574CF9ED75210F31476AC309FF60777EAF5728363F2119571A035960B7DFE7542BF2738BC2FF2E7C30A0745CA9BF215A5923198FCF855367E767179AD539343E7BFB2EDC6A8B5D56F5CD2A2D4696D5273BB2D8BED4536014E34EFF297243756BC13A3CA47C7FF331B75E0405D6549BA44963A6D9D3F5B74E7B412BD1D7DA32
	8D4450C112C75999F987C93AC18F5B3DAED805BD8774D71EEB64AED3518B25F3FEA5B7596E93472FFD6658AFD67D2447E3A4499DE497BDB639A039599767123545A263CF7E5B48D6977DD9A05D8EF410C48F3BA4F9641BF6290BE48F1F27BF0375481235914F12BDB2F93B0BEABACCBFC9BA5CEE710C4A74DDC5ED97C9F4507F1C00E471EA3B45B1F218FF58DECBFDA0890D8D4CDE7A7A1B003DBB183D3415D2514A0EAF269F585B99374CC88CED64AD3B53E2ED338C49CEF78759AA0F3AA54F2853EE753ACE7206
	D2EE502853DD07911A5BAD5C274E16522D2BB07A2A7709395B57EFEFB39062F513430C132E56CB85877D1A4E8D2AA4F707CC7F338527FB3F281BD9A09F7C56291D7D861220B8C9C31F6CF549BCE020BC1DB5C9DDAEED832B659F3E710DA7FFD4973F81C45AC8927B21A8A84ED0397877FA8FC7ADFCE75B02F50FD23BDCB4BC2CD35F12C97230E55CE63F89B7ADECB4A03ED5F6DB9C1EE127FB9C141A7CBC46AAE1F869473FD136708B3F3D448CD7C5E53207F791089E9E111C63079B0707ED8EC9B4G7D854D3FA3
	1A27BC25FEBCD95E475E6E7C5E1B0310DFAD44B8DED6467E9EE1FF0F523FC798F204A1C70EC2274513207F7C6B3DDE6A738E2F08AE3524033D86DAF73D3C46785AE70D86128843053F960364256F36A0F95DFC7498E5270B655D0F895F6E50794BA8138C7E352389BFA22D01A0970D5B9C2545EA9DFE7889F5694A8716A65EE3DA04FF20C9917A9125C9F61A38F80D981AF427B522D3CA1CB29CA81AB2B52FA91C4AAA53A8BB3D4994C9C1CB592CB2077D5D75E0A1AE72B2E97A6688CFC859AF6282F8C13F5C58B7
	C54260A3C1F90ABEFA1E162B1E627E17F97B7F2B3F6AFC7BABD939866B2F829F89CAA9BE927489DE64A3BFDF2774110AC2079A857A2DB3E6ED6234DEFC04FD3A207AC43FE727BDCB4F6CD282F8615B33B48A583309BEBCC5BE25FDD362E688A071110A0DC50DCDA2D17B1B883B69CA6B2B02A7C1295F2DB4423FEBAD3E277D7D6C260752837E4EFB4DBC653B28F97E3F1FB53631770A73E30DAD73715D7E05D63A518B749B418B63963BDD36582FB6BA2C747702CDB7E5C9FD171F52C65B77A00FD2496B525B285D
	E39A197F85D0CB87888A18GDCEB9AGGFCD0GGD0CB818294G94G88G88G8CEB0CB08A18GDCEB9AGGFCD0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG259AGGGG
**end of data**/
}
/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 12:36:14 PM)
 * @param change java.lang.String
 */
private String getChangeCondition( String change )
{

	if( change.equalsIgnoreCase("After a Duration") )
	{
		return LMProgramDirectGear.CHANGE_DURATION;
	}
	else if( change.equalsIgnoreCase("Priority Change") )
	{
		return LMProgramDirectGear.CHANGE_PRIORITY;
	}
	else if( change.equalsIgnoreCase("Above Trigger") )
	{
		return LMProgramDirectGear.CHANGE_TRIGGER_OFFSET;
	}	
	else
		return LMProgramDirectGear.CHANGE_NONE;

}

/**
 * Return the JComboBoxWhenChange property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxWhenChange() {
	if (ivjJComboBoxWhenChange == null) {
		try {
			ivjJComboBoxWhenChange = new javax.swing.JComboBox();
			ivjJComboBoxWhenChange.setName("JComboBoxWhenChange");
			ivjJComboBoxWhenChange.setPreferredSize(new java.awt.Dimension(195, 23));
			ivjJComboBoxWhenChange.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			// user code begin {1}
			ivjJComboBoxWhenChange.addItem("Manually Only");
			ivjJComboBoxWhenChange.addItem("After a Duration");
			ivjJComboBoxWhenChange.addItem("Priority Change");
			ivjJComboBoxWhenChange.addItem("Above Trigger");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxWhenChange;
}
/**
 * Return the JCSpinFieldChangeDuration property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldChangeDuration() {
	if (ivjJCSpinFieldChangeDuration == null) {
		try {
			ivjJCSpinFieldChangeDuration = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldChangeDuration.setName("JCSpinFieldChangeDuration");
			ivjJCSpinFieldChangeDuration.setPreferredSize(new java.awt.Dimension(35, 20));
			ivjJCSpinFieldChangeDuration.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJCSpinFieldChangeDuration.setMaximumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}
			ivjJCSpinFieldChangeDuration.setDataProperties(
					new com.klg.jclass.field.DataProperties(
						new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(0), new Integer(99999), null, true, 
						null, new Integer(1), "#,##0.###;-#,##0.###", false, 
						false, false, null, new Integer(3)), 
						new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
						new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
						new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldChangeDuration;
}
/**
 * Return the JCSpinFieldChangePriority property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldChangePriority() {
	if (ivjJCSpinFieldChangePriority == null) {
		try {
			ivjJCSpinFieldChangePriority = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldChangePriority.setName("JCSpinFieldChangePriority");
			ivjJCSpinFieldChangePriority.setPreferredSize(new java.awt.Dimension(30, 20));
			ivjJCSpinFieldChangePriority.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJCSpinFieldChangePriority.setMaximumSize(new java.awt.Dimension(40, 30));
			// user code begin {1}
			ivjJCSpinFieldChangePriority.setDataProperties(
					new com.klg.jclass.field.DataProperties(
						new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(0), new Integer(9999), null, true, 
						null, new Integer(1), "#,##0.###;-#,##0.###", false, 
						false, false, null, new Integer(0)), 
						new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
						new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
						new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldChangePriority;
}
/**
 * Return the JCSpinFieldChangeTriggerNumber property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldChangeTriggerNumber() {
	if (ivjJCSpinFieldChangeTriggerNumber == null) {
		try {
			ivjJCSpinFieldChangeTriggerNumber = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldChangeTriggerNumber.setName("JCSpinFieldChangeTriggerNumber");
			ivjJCSpinFieldChangeTriggerNumber.setPreferredSize(new java.awt.Dimension(35, 20));
			ivjJCSpinFieldChangeTriggerNumber.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJCSpinFieldChangeTriggerNumber.setMaximumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}
			ivjJCSpinFieldChangeTriggerNumber.setDataProperties(
					new com.klg.jclass.field.DataProperties(
						new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(1), new Integer(99999), null, true, 
						null, new Integer(1), "#,##0.###;-#,##0.###", false, 
						false, false, null, new Integer(1)), 
						new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
						new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
						new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldChangeTriggerNumber;
}
/**
 * Return the JLabelChangeDuration property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelChangeDuration() {
	if (ivjJLabelChangeDuration == null) {
		try {
			ivjJLabelChangeDuration = new javax.swing.JLabel();
			ivjJLabelChangeDuration.setName("JLabelChangeDuration");
			ivjJLabelChangeDuration.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelChangeDuration.setText("Change Duration:");
			ivjJLabelChangeDuration.setMaximumSize(new java.awt.Dimension(103, 14));
			ivjJLabelChangeDuration.setPreferredSize(new java.awt.Dimension(103, 14));
			ivjJLabelChangeDuration.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelChangeDuration.setMinimumSize(new java.awt.Dimension(103, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelChangeDuration;
}
/**
 * Return the JLabelChangePriority property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelChangePriority() {
	if (ivjJLabelChangePriority == null) {
		try {
			ivjJLabelChangePriority = new javax.swing.JLabel();
			ivjJLabelChangePriority.setName("JLabelChangePriority");
			ivjJLabelChangePriority.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelChangePriority.setText("Change Priority:");
			ivjJLabelChangePriority.setMaximumSize(new java.awt.Dimension(103, 14));
			ivjJLabelChangePriority.setPreferredSize(new java.awt.Dimension(103, 14));
			ivjJLabelChangePriority.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelChangePriority.setMinimumSize(new java.awt.Dimension(103, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelChangePriority;
}
/**
 * Return the JLabelChangeTriggerNumber property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelChangeTriggerNumber() {
	if (ivjJLabelChangeTriggerNumber == null) {
		try {
			ivjJLabelChangeTriggerNumber = new javax.swing.JLabel();
			ivjJLabelChangeTriggerNumber.setName("JLabelChangeTriggerNumber");
			ivjJLabelChangeTriggerNumber.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelChangeTriggerNumber.setText("Trigger Number:");
			ivjJLabelChangeTriggerNumber.setMaximumSize(new java.awt.Dimension(143, 14));
			ivjJLabelChangeTriggerNumber.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjJLabelChangeTriggerNumber.setPreferredSize(new java.awt.Dimension(143, 14));
			ivjJLabelChangeTriggerNumber.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelChangeTriggerNumber.setMinimumSize(new java.awt.Dimension(143, 14));
			ivjJLabelChangeTriggerNumber.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelChangeTriggerNumber;
}
/**
 * Return the JLabelChangeTriggerOffset property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelChangeTriggerOffset() {
	if (ivjJLabelChangeTriggerOffset == null) {
		try {
			ivjJLabelChangeTriggerOffset = new javax.swing.JLabel();
			ivjJLabelChangeTriggerOffset.setName("JLabelChangeTriggerOffset");
			ivjJLabelChangeTriggerOffset.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelChangeTriggerOffset.setText("Trigger Offset:");
			ivjJLabelChangeTriggerOffset.setMaximumSize(new java.awt.Dimension(143, 14));
			ivjJLabelChangeTriggerOffset.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjJLabelChangeTriggerOffset.setPreferredSize(new java.awt.Dimension(143, 14));
			ivjJLabelChangeTriggerOffset.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelChangeTriggerOffset.setMinimumSize(new java.awt.Dimension(143, 14));
			ivjJLabelChangeTriggerOffset.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelChangeTriggerOffset;
}
/**
 * Return the JLabelMinutesChDur property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMinutesChDur() {
	if (ivjJLabelMinutesChDur == null) {
		try {
			ivjJLabelMinutesChDur = new javax.swing.JLabel();
			ivjJLabelMinutesChDur.setName("JLabelMinutesChDur");
			ivjJLabelMinutesChDur.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelMinutesChDur.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelMinutesChDur.setText("(min.)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMinutesChDur;
}
/**
 * Return the JLabelWhenChange property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelWhenChange() {
	if (ivjJLabelWhenChange == null) {
		try {
			ivjJLabelWhenChange = new javax.swing.JLabel();
			ivjJLabelWhenChange.setName("JLabelWhenChange");
			ivjJLabelWhenChange.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelWhenChange.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelWhenChange.setText("When to Change:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelWhenChange;
}
/**
 * Return the JPanelChangeMethod property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelChangeMethod() {
	if (ivjJPanelChangeMethod == null) {
		try {
			ivjJPanelChangeMethod = new javax.swing.JPanel();
			ivjJPanelChangeMethod.setName("JPanelChangeMethod");
			ivjJPanelChangeMethod.setLayout(new java.awt.GridBagLayout());
			ivjJPanelChangeMethod.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJPanelChangeMethod.setMaximumSize(new java.awt.Dimension(335, 88));
			ivjJPanelChangeMethod.setPreferredSize(new java.awt.Dimension(335, 88));
			ivjJPanelChangeMethod.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJPanelChangeMethod.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

			java.awt.GridBagConstraints constraintsJLabelChangeDuration = new java.awt.GridBagConstraints();
			constraintsJLabelChangeDuration.gridx = 1; constraintsJLabelChangeDuration.gridy = 2;
			constraintsJLabelChangeDuration.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelChangeDuration.ipadx = -5;
			constraintsJLabelChangeDuration.ipady = 6;
			constraintsJLabelChangeDuration.insets = new java.awt.Insets(1, 5, 3, 5);
			getJPanelChangeMethod().add(getJLabelChangeDuration(), constraintsJLabelChangeDuration);

			java.awt.GridBagConstraints constraintsJCSpinFieldChangeDuration = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldChangeDuration.gridx = 2; constraintsJCSpinFieldChangeDuration.gridy = 2;
			constraintsJCSpinFieldChangeDuration.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJCSpinFieldChangeDuration.ipadx = 34;
			constraintsJCSpinFieldChangeDuration.ipady = 19;
			constraintsJCSpinFieldChangeDuration.insets = new java.awt.Insets(1, 5, 3, 2);
			getJPanelChangeMethod().add(getJCSpinFieldChangeDuration(), constraintsJCSpinFieldChangeDuration);

			java.awt.GridBagConstraints constraintsJLabelMinutesChDur = new java.awt.GridBagConstraints();
			constraintsJLabelMinutesChDur.gridx = 3; constraintsJLabelMinutesChDur.gridy = 2;
			constraintsJLabelMinutesChDur.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelMinutesChDur.ipadx = 5;
			constraintsJLabelMinutesChDur.ipady = -2;
			constraintsJLabelMinutesChDur.insets = new java.awt.Insets(5, 3, 5, 5);
			getJPanelChangeMethod().add(getJLabelMinutesChDur(), constraintsJLabelMinutesChDur);

			java.awt.GridBagConstraints constraintsJLabelChangePriority = new java.awt.GridBagConstraints();
			constraintsJLabelChangePriority.gridx = 4; constraintsJLabelChangePriority.gridy = 2;
			constraintsJLabelChangePriority.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelChangePriority.ipadx = -13;
			constraintsJLabelChangePriority.ipady = 6;
			constraintsJLabelChangePriority.insets = new java.awt.Insets(1, 6, 3, 3);
			getJPanelChangeMethod().add(getJLabelChangePriority(), constraintsJLabelChangePriority);

			java.awt.GridBagConstraints constraintsJCSpinFieldChangePriority = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldChangePriority.gridx = 5; constraintsJCSpinFieldChangePriority.gridy = 2;
			constraintsJCSpinFieldChangePriority.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJCSpinFieldChangePriority.ipadx = 29;
			constraintsJCSpinFieldChangePriority.ipady = 19;
			constraintsJCSpinFieldChangePriority.insets = new java.awt.Insets(1, 3, 3, 8);
			getJPanelChangeMethod().add(getJCSpinFieldChangePriority(), constraintsJCSpinFieldChangePriority);

			java.awt.GridBagConstraints constraintsJLabelChangeTriggerNumber = new java.awt.GridBagConstraints();
			constraintsJLabelChangeTriggerNumber.gridx = 1; constraintsJLabelChangeTriggerNumber.gridy = 3;
			constraintsJLabelChangeTriggerNumber.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelChangeTriggerNumber.ipadx = -45;
			constraintsJLabelChangeTriggerNumber.ipady = 6;
			constraintsJLabelChangeTriggerNumber.insets = new java.awt.Insets(4, 5, 16, 5);
			getJPanelChangeMethod().add(getJLabelChangeTriggerNumber(), constraintsJLabelChangeTriggerNumber);

			java.awt.GridBagConstraints constraintsJLabelChangeTriggerOffset = new java.awt.GridBagConstraints();
			constraintsJLabelChangeTriggerOffset.gridx = 4; constraintsJLabelChangeTriggerOffset.gridy = 3;
			constraintsJLabelChangeTriggerOffset.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelChangeTriggerOffset.ipadx = -63;
			constraintsJLabelChangeTriggerOffset.insets = new java.awt.Insets(8, 6, 18, 13);
			getJPanelChangeMethod().add(getJLabelChangeTriggerOffset(), constraintsJLabelChangeTriggerOffset);

			java.awt.GridBagConstraints constraintsJTextFieldChangeTriggerOffset = new java.awt.GridBagConstraints();
			constraintsJTextFieldChangeTriggerOffset.gridx = 5; constraintsJTextFieldChangeTriggerOffset.gridy = 3;
			constraintsJTextFieldChangeTriggerOffset.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJTextFieldChangeTriggerOffset.weightx = 1.0;
			constraintsJTextFieldChangeTriggerOffset.ipadx = 26;
			constraintsJTextFieldChangeTriggerOffset.insets = new java.awt.Insets(4, 3, 16, 8);
			getJPanelChangeMethod().add(getJTextFieldChangeTriggerOffset(), constraintsJTextFieldChangeTriggerOffset);

			java.awt.GridBagConstraints constraintsJCSpinFieldChangeTriggerNumber = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldChangeTriggerNumber.gridx = 2; constraintsJCSpinFieldChangeTriggerNumber.gridy = 3;
			constraintsJCSpinFieldChangeTriggerNumber.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJCSpinFieldChangeTriggerNumber.ipadx = 34;
			constraintsJCSpinFieldChangeTriggerNumber.ipady = 19;
			constraintsJCSpinFieldChangeTriggerNumber.insets = new java.awt.Insets(4, 5, 16, 2);
			getJPanelChangeMethod().add(getJCSpinFieldChangeTriggerNumber(), constraintsJCSpinFieldChangeTriggerNumber);

			java.awt.GridBagConstraints constraintsJLabelWhenChange = new java.awt.GridBagConstraints();
			constraintsJLabelWhenChange.gridx = 1; constraintsJLabelWhenChange.gridy = 1;
			constraintsJLabelWhenChange.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelWhenChange.ipadx = 3;
			constraintsJLabelWhenChange.ipady = 4;
			constraintsJLabelWhenChange.insets = new java.awt.Insets(0, 5, 4, 5);
			getJPanelChangeMethod().add(getJLabelWhenChange(), constraintsJLabelWhenChange);

			java.awt.GridBagConstraints constraintsJComboBoxWhenChange = new java.awt.GridBagConstraints();
			constraintsJComboBoxWhenChange.gridx = 2; constraintsJComboBoxWhenChange.gridy = 1;
			constraintsJComboBoxWhenChange.gridwidth = 4;
			constraintsJComboBoxWhenChange.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJComboBoxWhenChange.weightx = 1.0;
			constraintsJComboBoxWhenChange.ipadx = 69;
			constraintsJComboBoxWhenChange.insets = new java.awt.Insets(0, 5, 1, 27);
			getJPanelChangeMethod().add(getJComboBoxWhenChange(), constraintsJComboBoxWhenChange);
			// user code begin {1}
			jComboBoxWhenChange_ActionPerformed(null);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelChangeMethod;
}
/**
 * Return the JTextFieldChangeTriggerOffset property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldChangeTriggerOffset() {
	if (ivjJTextFieldChangeTriggerOffset == null) {
		try {
			ivjJTextFieldChangeTriggerOffset = new javax.swing.JTextField();
			ivjJTextFieldChangeTriggerOffset.setName("JTextFieldChangeTriggerOffset");
			ivjJTextFieldChangeTriggerOffset.setPreferredSize(new java.awt.Dimension(30, 20));
			ivjJTextFieldChangeTriggerOffset.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJTextFieldChangeTriggerOffset.setMaximumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}
			ivjJTextFieldChangeTriggerOffset.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-99999.9999, 99999.9999, 4) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldChangeTriggerOffset;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	LMProgramDirectGear gear = null;
	
	gear = (LMProgramDirectGear)o;
	
	
	gear.setChangeCondition( getChangeCondition(getJComboBoxWhenChange().getSelectedItem().toString()) );
	
	gear.setChangeDuration( new Integer( ((Number)getJCSpinFieldChangeDuration().getValue()).intValue() * 60 ) );
	gear.setChangePriority( new Integer( ((Number)getJCSpinFieldChangePriority().getValue()).intValue() ) );
	gear.setChangeTriggerNumber( new Integer( ((Number)getJCSpinFieldChangeTriggerNumber().getValue()).intValue() ) );
	
	if( getJTextFieldChangeTriggerOffset().getText() == null || getJTextFieldChangeTriggerOffset().getText().length() <= 0 )
		gear.setChangeTriggerOffset( new Double(0.0) );
	else
		gear.setChangeTriggerOffset( Double.valueOf(getJTextFieldChangeTriggerOffset().getText()) );

	com.cannontech.database.data.device.lm.NoControlGear s = (com.cannontech.database.data.device.lm.NoControlGear)gear;
	
	return s;
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

	getJCSpinFieldChangeDuration().addValueListener(this);
	getJCSpinFieldChangePriority().addValueListener(this);
	getJCSpinFieldChangeTriggerNumber().addValueListener(this);
	getJComboBoxWhenChange().addActionListener(this);
	getJComboBoxWhenChange().addActionListener(this);
	getJTextFieldChangeTriggerOffset().addCaretListener(this);
	getJComboBoxWhenChange().addActionListener(this);
	
	// user code end
	
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("NoControlGearPanel");
		setPreferredSize(new java.awt.Dimension(402, 430));
		setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
		setLayout(new java.awt.GridBagLayout());
		setSize(402, 430);

		java.awt.GridBagConstraints constraintsJPanelChangeMethod = new java.awt.GridBagConstraints();
		constraintsJPanelChangeMethod.gridx = 0; constraintsJPanelChangeMethod.gridy = 0;
		constraintsJPanelChangeMethod.gridwidth = 3;
		constraintsJPanelChangeMethod.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJPanelChangeMethod.weightx = 1.0;
		constraintsJPanelChangeMethod.weighty = 1.0;
		constraintsJPanelChangeMethod.insets = new java.awt.Insets(3, 5, 221, 62);
		add(getJPanelChangeMethod(), constraintsJPanelChangeMethod);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getJComboBoxWhenChange().setSelectedItem( LMProgramDirectGear.CHANGE_NONE );
	
	try
	{
		initConnections();
	}
	catch(Exception e)	{ }
	
	// user code end
}
/**
 * Comment
 */
public void jComboBoxWhenChange_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getJLabelChangeDuration().setVisible(false);
	getJCSpinFieldChangeDuration().setVisible(false);
	getJLabelMinutesChDur().setVisible(false);
	
	getJLabelChangePriority().setVisible(false);
	getJCSpinFieldChangePriority().setVisible(false);
	
	getJLabelChangeTriggerNumber().setVisible(false);
	getJCSpinFieldChangeTriggerNumber().setVisible(false);

	getJLabelChangeTriggerOffset().setVisible(false);
	getJTextFieldChangeTriggerOffset().setVisible(false);

	
	if( getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase( LMProgramDirectGear.CHANGE_NONE )
		 || getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase( "Manually Only" ) )
	{
		//None
		return;
	}
	else if( getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase( LMProgramDirectGear.CHANGE_DURATION )
				 || getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase( "After a Duration" ) )
	{
		//Duration
		getJLabelChangeDuration().setVisible(true);
		getJCSpinFieldChangeDuration().setVisible(true);
		getJLabelMinutesChDur().setVisible(true);
	}
	else if( getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase( LMProgramDirectGear.CHANGE_PRIORITY )
				 || getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase( "Priority Change" ) )
	{
		//Priority
		getJLabelChangePriority().setVisible(true);
		getJCSpinFieldChangePriority().setVisible(true);
	}
	else if( getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase( LMProgramDirectGear.CHANGE_TRIGGER_OFFSET )
				 || getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase( "Above Trigger" ) )
	{
		//TriggerOffset
		getJLabelChangeTriggerNumber().setVisible(true);
		getJCSpinFieldChangeTriggerNumber().setVisible(true);

		getJLabelChangeTriggerOffset().setVisible(true);
		getJTextFieldChangeTriggerOffset().setVisible(true);
	}
	else
		throw new Error("Unknown LMProgramDirectGear control condition found, the value = " + getJComboBoxWhenChange().getSelectedItem().toString() );


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
		NoControlGearPanel aNoControlGearPanel;
		aNoControlGearPanel = new NoControlGearPanel();
		frame.setContentPane(aNoControlGearPanel);
		frame.setSize(aNoControlGearPanel.getSize());
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
		System.err.println("Exception occurred in main() of com.cannontech.dbeditor.wizard.device.lmprogram.GenericGearPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 12:36:14 PM)
 * @param change java.lang.String
 */
private void setChangeCondition(String change) 
{
	if( change == null )
		return;

	if( change.equalsIgnoreCase(LMProgramDirectGear.CHANGE_NONE) )
	{
		getJComboBoxWhenChange().setSelectedItem("Manually Only");
	}
	else if( change.equalsIgnoreCase(LMProgramDirectGear.CHANGE_DURATION) )
	{
		getJComboBoxWhenChange().setSelectedItem("After a Duration");
	}
	else if( change.equalsIgnoreCase(LMProgramDirectGear.CHANGE_PRIORITY) )
	{
		getJComboBoxWhenChange().setSelectedItem("Priority Change");
	}
	else if( change.equalsIgnoreCase(LMProgramDirectGear.CHANGE_TRIGGER_OFFSET) )
	{
		getJComboBoxWhenChange().setSelectedItem("Above Trigger");
	}	
	
}
/**
 * setValue method comment.
 */
public void setValue(Object o) 
{
	LMProgramDirectGear gear = null;
	
	if( o == null )
	{
		return;
	}
	else
		gear = (LMProgramDirectGear)o;

	setChangeCondition( gear.getChangeCondition() );
	
	getJCSpinFieldChangeDuration().setValue( new Integer( gear.getChangeDuration().intValue() / 60 ) );
	getJCSpinFieldChangePriority().setValue( gear.getChangePriority() );
	getJCSpinFieldChangeTriggerNumber().setValue( gear.getChangeTriggerNumber() );	
	getJTextFieldChangeTriggerOffset().setText( gear.getChangeTriggerOffset().toString() );

	com.cannontech.database.data.device.lm.NoControlGear s = (com.cannontech.database.data.device.lm.NoControlGear)gear;

		
}
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	//fire this event for all JCSpinFields!!
	this.fireInputUpdate();
}
}
