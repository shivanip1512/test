package com.cannontech.dbeditor.wizard.device;

import java.util.List;

import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

/**
 * This type was created in VisualAge.
 */
public class MCTBroadcastListEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemovePanelListener {
	private int rightListItemIndex = getMCTListAddRemovePanel().rightListGetSelectedIndex();
	private boolean rightListDragging = false;
	private com.cannontech.common.gui.util.AddRemovePanel ivjMCTListAddRemovePanel = null;
	
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public MCTBroadcastListEditorPanel() {
	super();
	initialize();
}


/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getMCTListAddRemovePanel()) 
		connEtoC4(newEvent);
	// user code begin {2}
	// user code end
}


/**
 * connEtoC1:  (RepeatersAddRemovePanel.addRemovePanel.rightListMouse_mousePressed(java.util.EventObject) --> RepeaterSetupEditorPanel.repeatersAddRemovePanel_RightListMouse_mousePressed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.MCTAddRemovePanel_RightListMouse_mousePressed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (RepeatersAddRemovePanel.addRemovePanel.rightListMouse_mouseReleased(java.util.EventObject) --> RepeaterSetupEditorPanel.repeatersAddRemovePanel_RightListMouse_mouseReleased(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.MCTAddRemovePanel_RightListMouse_mouseReleased(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (AddRemovePanel.addRemovePanel.addButtonAction_actionPerformed(java.util.EventObject) --> RepeaterSetupEditorPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.util.EventObject arg1) {
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
 * connEtoC5:  (AddRemovePanel.addRemovePanel.removeButtonAction_actionPerformed(java.util.EventObject) --> RepeaterSetupEditorPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.util.EventObject arg1) {
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
 * connEtoC6:  (RepeatersAddRemovePanel.addRemovePanel.rightListMouse_mouseExited(java.util.EventObject) --> RepeaterSetupEditorPanel.repeatersAddRemovePanel_RightListMouse_mouseExited(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.MCTAddRemovePanel_RightListMouse_mouseExited(arg1);
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
	D0CB838494G88G88G33D855ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155DC8FD8D45715A73B26EB931BEAC3DA129ACB14A4C45D2C7DE2931C383B2635D993CDE3D753587DE8C3F76D46E613AC09185ADDCD9EB383039184958DC9CD44847FA5C8C0C1F1C08586D686C18922A12D3B7A1CF9B023434CB87378AB16BD675EF76FBC06B740G5ADD3F6F78667E4EBD771EF36E39675E775EFD8859BB23D312755902101CA2A87F0EA48BC254B9C1586E2FDC4C3004FF1FE792D47FF6
	81ED967E3E568857C460C5D76BCCD9C2CFEA9A140D0973CC95D0FE8FFEEF957E26283099FE0468A78DF80927CB076967B7DE560F09140BEC69F05D841486FD6583F583E589D6DB0E02B7C2F913729BA9D9AF8805EFE01DF70F65287079CABDA370F200F25F44BAF65B8E055F8D58E420F82044B7E85DB4283301E8F73B39D429F7E1CA2410746CF6FD867625EA1F1163CDAABBC939668A5308A38CC2424523F0ADEF3C5AF65C66E9AF30494E827B65823FA4F7F88B5ABA1C85BECF07AC194DCD12D792E549F79699
	B5F6276C71B50AEE4985326B084E054752C8FB0230904A93D7A396FB8C7DD4813FDB003C2B195EBF4DA8D7740A3C9AA9A44C5890D26FA52B293E092B83FACFCA1BE208CE42763FC14681658A216CG22B6C5F79E9FE01368F6FB5C32E4F394F4B9FBC51F3D40AEF5BAEDE0D50325394A67916DB651AF57BB7D72G4328DDAF5818DDAB1318DD7907315F9DEB66190A016A57B0FCB5416DD06E830ADC436CBD3617595BF2FD06D0EB48CF4D8CE16F6CB5544E65EB8276A6BE32A775FD92F7F77AB0F6325E02FE01EA
	01DCC01300E6837D1E44FB5935CF61DAF9D936F85A5BBD6ECA2F374AE577B5F86C128BFE1B4D40D130A649667159850146F44931FC8DFD98ADFCEBE0BCA3D15B3E94D88C4EEE3ACF48FF6189E30A06EDF3DF48512FD05AF8752D00EDD13FC53F351CC03F2543EF142B842278ED840FD761F45C9FA5F105E5475AF9A679BF181FDBB6B1DD6A0DF789D9C5E5FA1306AED1BFAC54C72C23BAC42D8B68C2FDAC4968630500A7G2D854ADABF4FD48854381E797876EB0706733195624D6796DDE7BBAEFBC51FDC5561F4
	59A51F626B3BEDC379DA0B285C83E14BD13F449ED20FD17CE3E6CD3F44476E543B56D3FFCCD89F708B6D7B1FC2769D33FEF0ECEC62E398CD66C45CFABA06860E4FA478A295CE4776A041313C82F865EF4FB3517AEDC7900FFE1B565F485B69A0788295CE5B799DE9874B89402BFEE71E090E2F2193F3EBAC1497824583A581E58BBAD3A1D0B5909D632557BE9F61BCEAD40DAF150D74ED072B54AD7BC42B5BAF0BEE1BF4DE74B9454BAE49AFA8F56E7721DFAF8F101BCE66378D7AB8A939A51FA8BBBDEEE8DDEC17
	A0187C344EAC5FE62C631388375959AEC1876DDE01357B18EFABDCDD10504EF96D103183B5A87FC99F46C427F89EDA048660F72400EBE7BD495FF6789D81C4FDD55805588AA8A7F23F9C76EF016BA988EE49E5B6D7BBED2806686BE1B17ABFA7A260EACC5619668349FA1DA95D20B345G7599F52666941DE9DF2A4E349CE802C9E7F22469CC759BF4267CF7F5268420280DBA5324F460E568CC5D1BF4263CCC288B34A0CBE71A339978DBF426722DD097A8AEDBE71A3CCDE732EF57190C6F69CC31C0FDB950477B
	BAA59F3C5E42623EBA63DE48CDC7B5639EE95F87BA9243954AB59077FFC56CF1GBE91A896E89150AA20345F03ADC035C08E2009C031C00B00D681256DGBED0AD1083E8A2D0AC50A220D5C0E99F829F28964881B491A896E89150AA20340F008FD48BC4F519FBCC5007B77F513F6AB29B979C8F745FG9F2452B1D0E368DF74B97ADF0D635861D811BD4AA61D667C9FAD61F82B4BB86E6359BE9246A146A0469D469A46CEF09D0CC30CDD0CB50CA90CB32D36B09E3570BC458E0CFB3CE21C9B159845B24E0D5168
	CEE3614871F0E3613D20B266D8DC2BB1C7168471B047FC98049D81421C78D9903E8DE82772FB77B80F537E207201F1EE9F8977E218B3B13FFD82F4CC238E5E87E0CEA16BBE505190ED95054077AA578C650A79358C6804EACCC723BB4D53533AF12DF3C01E18G9473914B55577C3982D9432AC43F44D62DB3BED8F0A4FBA0F3533AEE997DA0F5CAEE399A767AEEBB7C70ABEBC6070C36AB6BD92BC7E0EB4DF502637E46828B0E1F6337491BA98EED28D7C7467FBA610B326CF3DE063B8B3827101D6E36007C9422
	0BFF4056492F589A5D03364E879B17832D6436FE5001E3485625FA0F67EA073799D7DCE5FDFB036C6357C27D2C1DAC1FBEF4986F6B0AF9F98EA987782507DBB4FC0E7547A36EE89FDFE876E198275826FD349F49D6FEEFD0FC0CF3FBA367DFA77C8BE057842059C04B0156F1BF3CB3C8C7565646E17AE7FD8D2FE34E1160BE5479E9B8798DA350E32B105199BA4633BB35E37C03CE6D984F6D9CBA0677F5EA45B065FD5E09F6F93A5C2AB9D15C097BAA1B476D16484464A2823D27FD3D996DF03A1D7217CEFB1BA4
	1FF576CA302664E2EC5B485EAD8FFE1766323DCF57A7EC6F1357FB0F3060E236DE6B3EAC6A41C37A4939B41EE3F283FB9F2AD3C9974EB549A5350323AC1E8E372C5CE312FD21027B8D84F3FCB493E577D55ABBF13FEBA78F8A2ABAE459634E9709C5857452A879DABD3EF6493E6F0A58A99608DDF281991102CA422E415F7BF38F9CB81E9B6A19C33EB8380F0B15C10D5707EE3DEC7F797D05B43E368AE9957F2F74BE4874DE14F50722373DC9EA77F4CA2C8BF5314067ECF310A79B960FDB961D90F097ED1EF62F
	478D4D01A4B326C3F63A8AC89FE7AEDF01789B460ED627CF323A3D9D4AAD46BE506B90534B12FE67C874A23F298B280597B53DBCF6450A953D924D4F903D182CFF040EEB70F478250B6D78FF23CF727BC72919CF3B5B0BCDC374D32F5991C6C151A101797B714645E3352B89E632F8AB8C639D8DE1999B2F4395CF0F552E1AEE27FC8B2CD23A89EDD3899B2B6FED1C960ECDD09C6D8C3108DE1C71BE0F6BACACE8325456D3A53A2F86BD629CF29EC794BF90167F47B421C72DEEB11F87F59BFEF68B667748949D7F
	49CEEDAC11D65C0A39BE7EC60EE862D39BF795AF3B7973FE7CAD9CC1922076CDB5BCCC6C33D8023B7E3F188C284E5F78EC6628F47E73A5852A770C4D2BC627774DCC8EB65B504A0CBACBD01B6F333EB2EEB60FDF32981551A35BAEA8E37D62FB3F9EB73B47AB010C4A6A11ECA7941BE71D1DF64BF3C907DF7634CB3E13BE516B88E7BF715C0D877E9CB9A4CCF5837B0966FDAF5D525CB11422B7ADE5A8366AEC3F3A35B9E35C0D9DE12AD06C3D32B97ED6660AF137F67894210CEB43BD44CE5E73CDCF8B8D7826F2
	E4E9C1990BEB3DD16168B8BE69E024EAAAFB52244AE74252F1ECA9C0D339B66CFA3C1F9D4497AF9E2BDD61CC77B199964E3303578FBCBDD63BC6BE2D47E455481F9D3CACC7921BF8F7B7EDAE8FF1A8CAF3AAD37DAE64FDA79C7D46B60F43D41145C56669C5E169B83AF9BC14F264A447386E77E71A1E9D2BCDA3194363E1D4D87B796BDF7EF82CF68DBF0747432A916D57693B4BE53D78BEE5E12E4E3482A8B1173D3BFC228B796C5D2572025662F139682BA060B56F4277BCAF1E48D63D3BC983ACDFB9BFCA791B
	AA3478D361CA795F54644F617C2CCAAD7EC24E1F2249DF4179DB2A34782FF27E5D1AFC61E3464F36E87133B97F9ECD7EBE4E5FDE2D45AF677C6FE8729BB9BF27C60BEF677CFBB579817F7FF8987E9B1A7CEE5EFE29D50BBF097367E97223B95FDCB7F47F2D43703F25491F4B5B5FFACA0B3F0873A3B579711C3F2DDE0B1F48795F56649BBFE17C779A3478B91C9F25494F677C77CFEB71ABB87FBECDFE40BFCF8F43FFF3987EC7E7066657056453BC91531D85D73FF42DC3F25BA4BC5CF0CA6AE167823B3784714E
	0B2E0E40F943473A71BD3ACBF5AE02628F75E83F631D5D03E7A12E72766774248AF8EE1036C95FC94F9131CD2F4F59AE7AFA702007F246BD4D2EBE635E8C7E45737013609A8DC473FBF5B21E439CEA936548C31F4CB9424E4247016CD220A4253D5ABC7A3E1A566BA9E575666651FACB72346AD59DE575F2F753FA253BE93D093B55753619D93D84255EDA25DE6D9EF53DDF70FA51FBE83D38BD341EF1AF2D5732976B757273711FF3EC8A417E819CFB4E484E6AB6E4294F742FD87E32C16B3DBD52D4257DB9FB83
	6F6D69BA1356076DAE85BCE1AFDBE3BE6DD51F0F29747BBDB6A7B9C22A44414FFB556F74A9764B5EF7618A9BAA3FECF55B25EE96CBABFBD1D7BCE32A40CABC4C7A8C4FCE851F1925C34A0E9FD93A4872586C31F86CD2AD0423059FF9B86F143A8466079B7F257EC6E0FE66AE7D4690FEC8D26C4F5E9770435CC7762452737BB5954C6F4D402758539C1B443113157C7B850EAD6258BA0ED92B98D63C1FE14FF24C4531D3960645F0EC894762B956D04D306CCF9976930E55F36CCC8D43A6F06C75DA064D6718474A
	30A40EA5F2AC7FB306BDD347B0BB476ACEB1ECAA4796F3ECB9476A6B99161E4F3067B8D64D31538D8C6B6358739C1B4B3146538CFB95B09ACB260BF85EDC6B9B8E7CAA8577D9E6F335A80BE42BCF7285ED4BE7CFD362B16F00CE79AEE3DA9B66388BD01601A68151B362BFA7E74731EE8CE071C0B4FF1DF52873D7F681BDB3D68F57CB85ACFFD5145640F5DC6E3A787CD938FF8B1DEFC1B7B0ECCECC5FDA21683464513B0472FE3DDE6BBB120919697A6E82BA97228B82F3A26A17FBD2E783D15B2717B25BDF05BA
	F91FB33BB69C2D9DEC57286F56E8DF1DC7D9DF97201FC85E57AF0ED6EA749576DD97775F37B6D861BAEE77D8827B16603819693F84F4B796B27D9FB4D70DCC7FE16E30187EBBF36A4250FF785BA926FFEA99533F9CF417397E4B4AABC6267F10F7D2D47739FD35E168BE5CCD1372ED51C175DA38A0A1D7738C9B526CC29A678B8A0357425F5E40FC908FF8E2A1DB8BAFDE4733FBECADEC02AEC5F79B6C0878B941BE748F6CFD147266BE6C176CFD84F6AFD75C0FFBB5E3114E148BD48A548864ADE2FDC4758D7B0D
	D0CD77905F8855D504732D4F64A2F6D6F3F1D8F2B1DC2E3EB29C39B8AE77DCD8F2CB385C692AF06492385C73E149A5F139C6CBB8F2E987195CCF43125B41651A2A4311AB64F24B42122B66F24DB5614835F0395535614839381CEC8DC7AE024B2D89CBAE124B7D24AE9C39D8AEF766D4B8F273395C52306416F239AF6A4311DB49657EB1AC3935DC6EECC3B8F26907185C723064F2395C3953614895F339AEFB30DC6D2140B97A8B07861F29275F77EDF1605A65857E406F87F790BCC2055335AE260DFDBFB899F8
	C93C7F170E84775FD2BC30CFB928CC65DE9BA4D7902CE7109CDD8F4F9B075F3F8EDCA8697E75CE875B8B4FA9E67B571E9234E9951405921DA92704FD1FF70700771E05D02E873283C5G45GADGDAD9425622B77B346E4B862EC521EE4A825FEED0FF44160E4E0F0F8F128B7623713016DC5F515175F747A0390F024ABBB47B2BB60F2E3F26C1F2417D95EB7617DEB63A7EBE98A4975CDFFDD0FFB4E61ECDA65FF982CFBE4C62634C8D7CAE096D079ABDCE37AC303D503A9B785C47833BC2271BE149FFC24C6671
	118F98A8D65E9F7A5B8ACB3F76379556FE6DEFAB4E748F7D7D50177D213E3D205F7A08647EFE8258978DC431FF6658DC0E59AA993608E3ABB9964FB1290AE1099C7BD70E990FB02C4D42309C0E2D62D8BE471C558C2B6058AB9CEB6658559A06B9B8762E15E1FD9C3B13E313B916DE4730980E7D1AE3F19CEBBD4530A59CFB19E3899CF354B3AC09E33F61D8DAA9432EB4B0EC87477E0DE3059CF31DE6D8B5476EA2776EADD0F6F16CEAABE213219CDB4AF2F90E831FCDAE61650FC9991F3D51724CB62DEF2A4603
	82FD7E68267501397FC3F2FDE6FBA61987F01B6AA7899A9FF8A83C1765ED024677C22745F6C9E06F503617EED06A54489ECBB14F9D139DC32D6109252156625748BAE4BCCA575CE40E3FC570AC95CE57627E927677B9F6GAF62A84BB711C268DC71DDC1BBD7BCC03E1D74FBBAFCB6A9B89F448AB91C97D8392C32540E6E20F53ECF64E55157A68D4AA7F1C41E72C249BFA5044AB95457AF8D70F5600C5EE3C755F891472F1B5578C10EDB4A5478E12E6373496847EEDEDEC64ABD825BEFC8151B388DB5EE3B1244
	69F366EE127BECFAC138DF4F305F916C6B86757368DE127BACC67A1DAD457E13E25049D50E75115CE785ECB14756932C8C304D498C7B9349FD26816D3DC330A70C74BBE20AC5105C7795487EB717CDA6396F0F00E5F26CAF88F696307B396CAE127BE68176379C1BCE305B876872C99D53D96258BA029541E27684173DC1F25F8C28F70857FB1BE00B81FB06E37DA477BD89F63C44E58512671E872C06573B0DE0FB81BBE7E0181E643E4F81FB120F47DF926C9C141799D8ACBCE847F556A576F8BAE0AC9B4DD8EF
	BEFF3732926653BA20BC40CB4D7499A50D056FF7613372D61F588EA1FA56AB59047CB2623FD6A69B8575E783AD81FA95A83D0CBD937A5EC57CCEBC44DBF7E5FDFD5841F232DC466FA3B164157C32CE757B76121B15A783FB21E9B7358F1A4BD65AD9AEDB88B62FABE33EAE3A04FBF0324FE7DF6E194DA7FDCEFB1558D6CFC60E6E6B7325B4C53E381C6D6B7FA0E11BAEA8CFG1ADA4E3E5D6C5E435EFD4CFE68AE21BE422C797711921AF769671653DC3D22BC705C4BB8133D7B08EDE5FDE69F03FB04E3B8F6BFEB
	D53FE344B1776869DE1C72535A54FC76F7G82FC73GBE6A5BEFD073DD837868234F94F9EA5B625D4C36F50B278A2E9F6C8B79371FA20FD11B6214EB56A39F8B35172698720EE3BFFF67289EE24C8CC11488ED98C1EE1BD24E015FFE687436E496C32F8C3ABF9DD96C2D9D25DC11942E9CDDEB73DBC94C9C8FDE03CB885EFDBCF88D3EA431381DG3CD547593D63E48366027A933037840AG0A820A83DAFE022D53B6FD21B04CFDE33347DB8F8B39CB35CD27767DF56B687ED651D3A6969FF3F55F96623E36AAB9
	54FB3FD5A7E8DC24D550EB6C439F931C5AD710027615822F99289BE8B2D08C507C8AE65FACFDBE358FDFD1D64A32E8F310371F4CB8FCA19900B1116E94685F0AC893583F4493DA58D268C3907A50750ADD3FCAE7F66D1875CD217A45D5491F0430AB3B125AE32F8C4C6935DFBDEA24FEBD58AA7098AAD37DEE15EEFE1E6D3F29FD50B1395790FAEF98ED505E9BBEE4503E0F1CED987A9B7D3906505F68BFE9D0FF23CFE36A6E8C4CEDD3E1DC6200620192010CD5AC1E2C460FE9BCE944D20D3D8D7726785720E456
	7F111450369ECF997EEFC81DCC41777317BD326CE94776995E100263A5FB3C8424D8D38A7A083C5C21D5953CA5856356A5359218466A73FFD43FF7493AAFCB4FF2CA701865D651984D2F8AFE6FF2B8937373A5407B002201620016G2D84DA8B14EE5119F201CA019A016CC05DC0130026824D81DAE8E13EFDAA9D5F731D972F8CBED5G20326092877BC475730B6FE6047671740C61FDBCAB031C0BG5FB477F815FB01B9996419C8EB2BDF62E379030CAC0ED97D0D9E3F936FA128AF26A20B0D59265D216C00A8
	2903AD84FD8956AC36F1193A4C50B6FC11B93C8DFF48540E13CB190363C44A540E132B196AB82118B79367C89B783E05576B6058D79C6BA5EDA2762553AEBBB89EC174C23CD6428E997EB522D79741DA946CCE95761502CD4E44F1G139D9E7E9C6A1ECCFC96506AF43914FCC26AB85DB0AC5C9F53080ECE2F2852FB8647D0EF7A37C31DA71B60BA7BB5A1456F93B2799FE630124692BA8F198D7FB24320970CD6A115BA57AA9851C3D6A105B850AA9840395D784759D0A96CA0157E2673ECFF3FFA1E2D3D5D9472
	7D664AEABA3F56D6874F3347493DCEF18D4C219AB6D7D22D87060831336D224BA50B2E2B15EEBB46DBD50FAC85F26213D6EC5F4F6AA8CC4A7B2195753E221279DFD0CB8788D5F382257795GG40D5GGD0CB818294G94G88G88G33D855ACD5F382257795GG40D5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGB196GGGG
**end of data**/
}


/**
 * Return the MCTListAddRemovePanel property value.
 * @return com.cannontech.common.gui.util.AddRemovePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.AddRemovePanel getMCTListAddRemovePanel() {
	if (ivjMCTListAddRemovePanel == null) {
		try {
			ivjMCTListAddRemovePanel = new com.cannontech.common.gui.util.AddRemovePanel();
			ivjMCTListAddRemovePanel.setName("MCTListAddRemovePanel");
			// user code begin {1}
			ivjMCTListAddRemovePanel.setMode(com.cannontech.common.gui.util.AddRemovePanel.TRANSFER_MODE);
			ivjMCTListAddRemovePanel.leftListLabelSetText("Available MCTs");
			ivjMCTListAddRemovePanel.rightListLabelSetText("Assigned MCTs");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMCTListAddRemovePanel;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{

	
	com.cannontech.database.data.device.MCT_Broadcast broadcaster = (com.cannontech.database.data.device.MCT_Broadcast)val;
	broadcaster.getMCTVector().removeAllElements();
	for( int i = 0; i < getMCTListAddRemovePanel().rightListGetModel().getSize(); i++ )
	{

		com.cannontech.database.db.device.MCTBroadcastMapping mappingOfMCTs = new com.cannontech.database.db.device.MCTBroadcastMapping();
		
		mappingOfMCTs.setMctID( new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)getMCTListAddRemovePanel().rightListGetModel().getElementAt(i)).getYukonID()) );
		
		mappingOfMCTs.setMctBroadcastID( broadcaster.getDevice().getDeviceID() );
		
		broadcaster.getMCTVector().addElement(mappingOfMCTs);



	}

	return broadcaster;

}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) 
{
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
	getMCTListAddRemovePanel().addAddRemovePanelListener(this);
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("MCTBroadcastListEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(447, 311);

		java.awt.GridBagConstraints constraintsMCTListAddRemovePanel = new java.awt.GridBagConstraints();
		constraintsMCTListAddRemovePanel.gridx = 1; constraintsMCTListAddRemovePanel.gridy = 1;
		constraintsMCTListAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsMCTListAddRemovePanel.weightx = 1.0;
		constraintsMCTListAddRemovePanel.weighty = 1.0;
		constraintsMCTListAddRemovePanel.ipadx = 185;
		constraintsMCTListAddRemovePanel.ipady = 192;
		constraintsMCTListAddRemovePanel.insets = new java.awt.Insets(4, 5, 4, 6);
		add(getMCTListAddRemovePanel(), constraintsMCTListAddRemovePanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}

	


	// user code begin {2}
/*
	IDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	
	synchronized(cache)
	{
		
	
		java.util.List devicesList = cache.getAllDevices();
		java.util.Vector mctVector = new java.util.Vector();

		for( int j = 0; j < devicesList.size(); j++ )
		{
			com.cannontech.database.data.lite.LiteYukonPAObject tempHolder = (com.cannontech.database.data.lite.LiteYukonPAObject)devicesList.get(j);
			if( com.cannontech.database.data.device.DeviceTypesFuncs.isMCT(tempHolder.getLiteType()))
				mctVector.add(tempHolder);
		}
	}	
	// user code end*/
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
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void leftListListSelection_valueChanged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
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
		MCTBroadcastListEditorPanel aMCTBroadcastListEditorPanel;
		aMCTBroadcastListEditorPanel = new MCTBroadcastListEditorPanel();
		frame.setContentPane(aMCTBroadcastListEditorPanel);
		frame.setSize(aMCTBroadcastListEditorPanel.getSize());
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
 * Comment
 */
public void MCTAddRemovePanel_RightListMouse_mouseExited(java.util.EventObject newEvent) 
{
	rightListItemIndex = -1;
	rightListDragging = false;

	return;
}


/**
 * Comment
 */
public void MCTAddRemovePanel_RightListMouse_mousePressed(java.util.EventObject newEvent) 
{
	rightListItemIndex = getMCTListAddRemovePanel().rightListGetSelectedIndex();
	rightListDragging = true;

	return;
}


/**
 * Comment
 */
public void MCTAddRemovePanel_RightListMouse_mouseReleased(java.util.EventObject newEvent) 
{
	int indexSelected = getMCTListAddRemovePanel().rightListGetSelectedIndex();

	if ( rightListDragging &&  indexSelected != -1 && indexSelected != rightListItemIndex )
	{

		Object itemSelected = new Object();
		java.util.Vector destItems = new java.util.Vector( getMCTListAddRemovePanel().rightListGetModel().getSize() + 1 );

		for( int i = 0; i < getMCTListAddRemovePanel().rightListGetModel().getSize(); i++ )
			destItems.addElement( getMCTListAddRemovePanel().rightListGetModel().getElementAt(i) );

		itemSelected = destItems.elementAt( rightListItemIndex );
		destItems.removeElementAt( rightListItemIndex );
		destItems.insertElementAt( itemSelected, indexSelected );
		getMCTListAddRemovePanel().rightListSetListData(destItems);

		getMCTListAddRemovePanel().revalidate();
		getMCTListAddRemovePanel().repaint();

		// reset the values
		rightListItemIndex = -1;
		fireInputUpdate();
	}

	rightListDragging = false;

	return;
}


/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getMCTListAddRemovePanel()) 
		connEtoC5(newEvent);
	// user code begin {2}
	// user code end
}


/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListListSelection_valueChanged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}


/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseClicked(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}


/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseEntered(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}


/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseExited(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getMCTListAddRemovePanel()) 
		connEtoC6(newEvent);
	// user code begin {2}
	// user code end
}


/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mousePressed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getMCTListAddRemovePanel()) 
		connEtoC1(newEvent);
	// user code begin {2}
	// user code end
}


/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseReleased(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getMCTListAddRemovePanel()) 
		connEtoC2(newEvent);
	// user code begin {2}
	// user code end
}


/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouseMotion_mouseDragged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}


/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) 
{
	
	com.cannontech.database.data.device.MCT_Broadcast broadcaster = (com.cannontech.database.data.device.MCT_Broadcast)val;
	//This check may be necessary if this is acting as a Wizardpanel rather than an EditorPanel
	if( broadcaster == null)
		broadcaster = new com.cannontech.database.data.device.MCT_Broadcast();
	java.util.Vector availableMCTs = null;
	java.util.Vector usedMCTs = null;
	IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		List<LiteYukonPAObject> devices = cache.getAllDevices();
		
		availableMCTs = new java.util.Vector( devices.size() );
		usedMCTs = new java.util.Vector( broadcaster.getMCTVector().size() );

		for (LiteYukonPAObject liteDevice : devices) {

			if( DeviceTypesFuncs.isMCT(liteDevice.getPaoType().getDeviceTypeId()) )
			{
				availableMCTs.add(liteDevice);
				
				for( int j = 0; j < broadcaster.getMCTVector().size(); j++ )
				{				
					com.cannontech.database.db.device.MCTBroadcastMapping mappedMCT = ((com.cannontech.database.db.device.MCTBroadcastMapping)broadcaster.getMCTVector().elementAt(j));

					if( mappedMCT.getMctID().intValue() == liteDevice.getYukonID() )
					{
						availableMCTs.remove(liteDevice);
						usedMCTs.add(liteDevice );
						break;
					}
				}
			}
		}
	}
	
	getMCTListAddRemovePanel().leftListSetListData( availableMCTs );
	getMCTListAddRemovePanel().rightListSetListData( usedMCTs );
	
}	
	
public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getMCTListAddRemovePanel().requestFocus(); 
        } 
    });    
}	
	
}