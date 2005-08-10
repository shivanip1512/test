package com.cannontech.dbeditor.editor.capsubbus;

import com.cannontech.common.gui.unchanging.StringRangeDocument;
import com.cannontech.common.login.ClientSession;
import com.cannontech.database.data.capcontrol.CapControlYukonPAOBase;
import com.cannontech.roles.capcontrol.CBCSettingsRole;

/**
 * This type was created in VisualAge.
 */
public class CCSubstationBusBaseEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener
{
	private String originalMapLocID = null;
	private javax.swing.JPanel ivjIdentificationPanel = null;
	private javax.swing.JCheckBox ivjDisableCheckBox = null;
	private javax.swing.JLabel ivjJLabelGeoName = null;
	private javax.swing.JLabel ivjJLabelSubName = null;
	private javax.swing.JTextField ivjJTextFieldGeoName = null;
	private javax.swing.JTextField ivjJTextFieldSubName = null;
	private javax.swing.JLabel ivjJLabelMapLocation = null;
	private javax.swing.JTextField ivjJTextFieldMapLocation = null;
	private javax.swing.JPanel ivjJPanelAvailableDays = null;
	private com.cannontech.common.gui.unchanging.JCheckBoxDayChooser ivjJCheckBoxDayChooser = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CCSubstationBusBaseEditorPanel() {
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

	if (e.getSource() == getJCheckBoxDayChooser()) 
		jCheckBoxDayChooser_Action(e);
	
	// user code end
	if (e.getSource() == getDisableCheckBox()) 
		connEtoC9(e);
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
	if (e.getSource() == getJTextFieldSubName()) 
		connEtoC2(e);
	if (e.getSource() == getJTextFieldGeoName()) 
		connEtoC3(e);
	if (e.getSource() == getJTextFieldMapLocation()) 
		connEtoC17(e);
	// user code begin {2}
	// user code end
}


/**
 * connEtoC17:  (JTextFieldMapLocation.caret.caretUpdate(javax.swing.event.CaretEvent) --> CCSubstationBusBaseEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC17(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC2:  (PasswordTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceTapTerminalEditorPanel.fireInputUpdate()V)
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
 * connEtoC3:  (BankAddressTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceCapBankControllerEditorPanel.fireInputUpdate()V)
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
 * connEtoC9:  (DisableCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> CapControlStrategyBaseEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
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
	D0CB838494G88G88GABD2A3AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DD8D45715241F4D7A35EE57ECE8573436EBD21B10ADCDE957B6EECB36B6EBD2D3E9EB3E1AB6EDEDC2D33725DBEDEDE37733D9B7BE8690D4E4C645080A8A0A8A4A7F3F83AAFFA26870230C0000898F6641CC9CE626B38F9D92FF761CFB5F3D6F4DB0430029597AFD47776E39BF67671EFB6E39673EC1C8BF34A0A9DE17AE8871C902722FAADE9096DE91046FEEBC3F1561B2B2E212854D3FEC009DC2
	3FA9911E39D0375A9E13EC90F69CCE01F2AD14EB07E31277407BAE6137256BAF40CB90BAB1D077541C3ACFCDCD677DC30CCEB2A93FE8CA05E79AC09D60ACGD199C87FB7268C85BF9B4AE94ABBC23CCE90EE6D45B65D8D999A7CFA25DDA754D9G3276E11B4436CC25FEA960304DAAF82E5BCB5B26C01BAD043B0F9A2B15F6838FC5886B3F1B2F5B0E34B463B3306E23FD67A84FACE191D1C40270E0E29B6A2C737A6819E167D8613068F0B89D32B4ECAD348CC9961B6CF497AA0FE15165999F9A9A77980D0D0DDD
	63C39ED914EDCEC7433827C174C867C823CE51A159E13C0DC40EDB8DA90486488165597B91373E81F5D7876F3DG2E7DCC16973757AA3C06DF0F90E2BFFFA828ACB17B298C6B762B322C343E1238A09347AFE8CBD57A95C3598AC0F999EFE33C2C4EE43C1C2B46361987E2124B81CC8798BE759462ADD03E8590FE0071F87AB86331777D270545894FEF558761B172G65ED65811547167ED79277927BC94EC69BB09C843AGADGF600B9G11G7B095DBEFB6884BC6B0764C667581853D16FF2B558AD6EB627C5
	32433B5188B58A6E32B46CF4DB84015A66838D798178E13074202FDDA2D059FA84E6CB11171F92A23E7A7C5624G32ADFA29D07732B2461A032AEC3907D0EF56C654DBAA3C47AB63D27CB8415BB5F8BAD7EF90DB4072EC28DBF3086939B70D71E2CAFCCCB014DC52A58760E55E334D3A4EAC4A03B5CB65056AF84DE154F1B86023G168144828859304632190ED3334AC269389556025BA15A415EDD22DBEE983759AD12DB51F5D95BD43A8E8434DF7D0C7BD13DC414E967E8553FF686544B2A088ADDEDB65507B9
	DB558B257DDED05AF276E45BC863F378EFE42D4C4E26F31840717FC17073B4F8BA3797899E4B91D017FAB8A619368FA3EB4BF5585FC67E1E60679C7637111D27180DC4C0DD6A1118E4BA3F9F4BC19F6902729C0005G51G2BGB6G246630B96E49AA1A66BA6A544CAF6DEB48FE8B1E12D7F60B2D8EF0F70EE129DBF45B44A13B649114B6B9592857A11FFEF9E4FD8F830D7312C3F293B789230BE39298130736A94D5E01ED5C9229B55B46A4A0B066925838466CDD38E6C40FFC45E591E5C9EDC16B9B32D1A7B7
	44EE9891860077019C5457BC62F373613DA507696A692308DBG6545DCAF4FE46F0467C5B0EE49EEB4B65906118D51BD41EC5451E800E78C34B79F853D82D80E3175793BDEE607CD5B3FA4643F5491508E090FBE266CD9477D6D70FDB2FEB160BB81DCGF30F337DD5E86C8D319E10AFFCA2CF489F377D2301EDF8729A5C9B12C602E66DA579758FC5036A7D68F1345DA810AB86E04DF1267B8D59D954A6F163E4D65861868312AC6ACC5036FFBA0CFB0CF4C3F248CD12CBF2D860452358404643DBD47B9CF1F25B
	519DC1BC7A2BC6B0A08F47279C59C171B006565AD93D01540B326C368D0D4BD217A44BB647285ABF6388726271F1059EC5566339A8EBE2EECCF2A6C0FEAE137551A33BB4F6E6F2BA2F0F3B4C380294FBFD086C4BF5503EB7174D7F372AB15E3A454BAF1032DAFF29BA545CE15F197857497671EEC89A8977B83ECA6341D386E51CA4EA77A45E24F58F133AC810E3A540C6001CBCA6FF4629DE8D8FC9C1E86C8841C376143C15CC0BC6283530A5881E5AC6F8CEF05B7ECCCEE05BFEB4A730EDBF11B3356DFEB9A710
	6D523A2F6720FC4E1B8E4DDA78E68E7A47E12743A18D53B0D769A2503879F7E61443663049BDB64B28A4F7595E13E08F4DC31B7EB27141A6F89F4CE3BE73669166B3F35EFBD2D8724E89DD2078EA5E97DAF50B73286D466429BE137294FD945718E41746C0D10D4EF1072C440A443FABF8CF82417D397FFD6C1BFB418CBAEC36C9F68B04B06D3085658F437A178B497FF4EB49FDD73CA1FA8BBDB7C1E105E4B68A9B31769C3E66E595941C491ABA604F9F313925D607EB1C8D18571DD7A2D0BEFE364D1F0F731273
	C36423D4616309ED37889FCDB68FFA1FC62BB4FC3D41694D97496C9652C727649EF13A47A48BE125D03CA9ABFC54136A8F44C8B5656381C7181FBE5AC41749B9CCC67A306626127A6C3EBC3427B5E0E7420918640C934CE71FB906754CE7AB9BD32353EE27CB816AEE1DA0F65E182E315D629374AC1C466BFF9622BE39E96A7A322075144F134751C7FA243F0CCB901A6134DE14A6D8FCF5FC27DFDD37E89FD763B66371ED7034EB76A30AAF488D3C464AF2F18F3A4E472F4A5DACE07CC54724BEA1CA44B1DDEE5B
	98EC5F3841AAE77E08B66D19FFA948017901441320F3GEA97AD3B519FB70A2EC6B8FE3B1D76AEF0FC32B4BAB179EC3D74A40E791DD3ACC7D08C6596G6BC9BA6E4213D4C7345D7EBA564E108FB1F8FE30F6BF606DDA204DA0C0D8FE20F68F5733F6CB207EE5008DFE6D4A8B305D737C6CDF44F1BFA7389258B456A630B3CD1BC11B43587029BB817DA20237C049E194781F757E61840E9B89786882E643DF4955469D759E0FF358C61608B24F03F9DA1FC9F1D29EA61CE041F8605CE7113C4CD62E67A12F980BAB
	E8E53E3FF87234B079ECC1E70B05F50DB749D2B5BB9B61B850826656483714EE1BF4D3E0FA385D2C4DFFC46B2BF45B0268E105A27FBA0D9EE27BDEC924BCBDE244B3ED20FC4EB0E132F0F45CD6B8AE5BB06897E511F886EAD9B4CF3233BDC5112F381065C3DE6B44B5E105F29840BCGFAB6DBC74EE658B68AF0F1GF45D7E29DF3BEE330A40BE816CC54C56634E22CD4C5057A9BADF1A37134E111FD3E3FAFCECD71DC2B7662947045A3B993AC0B91A597A433A30A22A3F08A2D50F1697EF69FA93DFD5F2C9CDF5
	CC3E7550A63F18493729B210FC16E226178D3C1F97F0D13C5FD53D36DFDCB16DE7A8E1FAF9273E941EB35F1158D9B93A1E514D01B187CA985D0D6501788D67F40FB45C4BFCA8F923D26D7ADD92DB99F47DC614D0BDC717782FDFCFBE5A5C4B00DFD342566F2F72F6E8566FE5D8B122E3943CB40F9DCFA06F600F15723C93C81778E3011D3F228AD0EE21944EA4G65GA6GFBA923F168E4487C4FB96F94791FBD6D7E3914B9256A39E3C16964B3884DF5F4DF42F9DAD4CAF39DC99CAF927CB28D1E4EEBDCA71B57
	D5D0D7D7464E817FFD4A1F7E42B2DF1ACB7C4A34DF22513FDF21DFF935DFBF3A760E66E6B25F22B18D23D10DE568FE7748A56DFE37310C1E6D324AE18E4AD9BEB6FFG679CF33C26F2162758DC0463C7C1B986E0B500D081679E00BAGCB851BB7C7C03F6BBBEF411C2EFA66253A38D0F7EFBA6C1D544FDF0733AAFC7BD139AB2B185C73AB58BC3EF90FBC24560762C1288CB40FE73A77871EC79695AB3C4E55A32F384EB3AA992F4FCE22B9BDDED704643533B2103E4AAAD89CE12DE4F652DCCC72D9D00E2AE4B6
	317CA46A14790BCE274DA18B4CD7F873B1D6F33AED23B687435DA638E1271B9C7AA8EEF1C1707368378A821FC71FAB88FC9EDDDEB0F52E650785414E2BB4AFD2C87661A5A05FCAG0A3B5401388DD0B6D4317998380438F2A81BB9EE146050474B9C773B4ED0798C9CC3DBB6D7CD76DF410052C83EB4531CC9303CC2A0B8C07A3F70161E68556311BCE411E39423547DDA5EAD84B83363E9D3218D713DF10B52661C6CECAC65EB62753C0C40EB0284C75496DFA4FAC5D967D53158E8A7B9AF2C00F29C00D04D74DD
	B2B015AEB22AFDF51B2F146B60E9762B33C279169FCE495795853757BDC5016D75C0912E930FF35CBDAC795BE4CED1862FD3BDE62BAC0D218A15739169AF0B6ED1E912CD17137E34AED87F53C5416CDEB99F57E967270C5BD6CD55D47A1C6B271F05CAF9B1BCE37C6AE2213C5E8FC7E9D754EBE91F6234BF29577A2E14D334EF2672AC06E75329C0637D39FE2BE63C12AAB6DEF545D477BF3D27A62FBB2A3B0B459897F8F9390314A7B83DF36D68536C6204F39C1C5F12D3E81F836D5A3D588E38F000880586357D
	2F5C44334608DB9C032968F2C94342FCA3394F6BE0FDD3201C89D08BB0GF04B48E247E72E60B92B95CD4FB6E29BD66EBA70BCC16B5B2CD8CFD7D17D8D51E64733F613B861D14E461F599EA8D6D88D63EFB032357423CBDAFD99EAA0A628D1F5D4DD93AC4E7A94D91B269A9ACF69B97EF10217B5F89AE7E590BC1667C3DDC6AD733BDDA4D68907F2A447F5F809FF0672CA0E1BC7F0F1D0CEBC4DF0D66233B3201C4BF1FFAAE2FE3C166324CA444D0172C20E3BD64568AE66384F69995D980EFBCD4F6846F25CCFAA
	995D759C37231451CDB943F03B2B985DDE0EFB1A0CE70772AD0E5BFC95F1F3213C00632EDFE563C5F2DC1D81F19B21ECB84BF5D5415AE5820E5ADC4920CFAC09A79B23713C5BE6E990C7CD4402294F7D885987F11E6CE7194F0DEA47F9DA8865E80015E7D90E55FB0C1D21A39FFFCCB005F5867C06A056DC25DBF3165A0C21CEBD0B8C743F12C8E92E69E0B4CDD0BF2B8EF17BBA35E7A8346D35BA9A4350FA13CFBD7AB874DDEA7D9D1FFA6437A4C1DB9F65D30FBA3A22742732ADBF4AE45B38BCCA30FF6DCC706F
	A3941956A94FE563C7843968D5C5369FEEC759F03D6756B3FD4AE9284FC1A8FB81665633F9DBCB763B88A8C7F3DCBCD98B31D0DE4FF1E157587CE6B430B5BB1F1C952CD01E853090E089C0AC40C6004CC656AE12342BEB24B92C41C61A23A27927C61ADF226D8E66625DA8768D66B72D4A78DA9C5261FB0226EE0942D728180352BECDE8EF1F22DD0222D3ADCE6FB70EF66E279377A87EB58F479D933D363171B1F249905BC83E2DA17330G5EA39B19AF9E71A21DE127FDFC4C61D15A5DA6BE6990C0CE93EBF71A
	6C61AE37B4A2395D1205DEDDA86D3FCD5A17C35BBA5EFE798466BEC71C8E1936F9082C8FBB54FB81E21B48B7B345CDD34812A42877C8780C04BECBGF6AB3815707ED1451EA94D575FA2F25B9CFEF2FF0278355CF3F01684203887098F9B04321D633E23E7F2AF6038ED44B7C5C1B91A63F424EF9C14571FE33264160612A5C9A3CBEE332FAC35D03E98CFE521B44E909AAD00B7B7B31DFEDF6F2B5355045FB0281F435B9C380AEDE48862221B713B34AFC08481E3929EBFF2F5AA9E93941BCBD0E009C65F4817F0
	BEA63942C75FBD064D444E5C640CC4691DBC25529B29180ADE329F3D54733EBA490272938ABD3A267EEED26EA494503DBB2783FDD57EF93AF747F3FC8141D7EB70F4CF7FFABB7B9E2C896AA24E33B3E41A91630E549670B7GF5G3DGDE007096F626DCD9DCAC0448B3181DAE938460F64D910F4A77070EFB7B46675B494C4FAF7A463F880B9FFCB3BED89EB822C5716F4AF3D0BEC270D43E489A14EF8354253746A4D783F482D8GEE35B279B2CB7229FC18322E17E5F158CA3261CCB8CCD02BE88C8C29ECAFEF
	E7324D05319682ACEED5FDE98C3C47367A7AD6BAC7F1E41FD95F6A7F8D519F89BE71027FB7C46F2531EF0852214EFB012D0B444BE82BC3CEB7B9FC50B877E09F0BA3D7C13B75G4597A1AE3FC8F31D34DF4640647838E800759B0036989F4B701C2D7423367FCF57C22DB5ADAC003E0BG0EA865E5703EF65326CD3F8720FCB83D68FFAF1844A1492E5C4A72F57811B398BBCC3A356575F5FB553E4ACDAA2F937D7A7A57B71E44BD496F86D4295B63594147555CC8723E0F1C586DB336EF9B1AE77EE52EB606D929
	68F043C516077E9FFD207AD4932BDFDE9E28BE8B6A957F07E9BF051F866FB1F8BA241BD9BCB15EE483B3450F21F2CBCB4A2B4A2B4BAA4B2B7233CA0B73585ADAF0D31B2B2E5EDA9BB4D7DDEE22765B22BC4B1FB822508D77C258A714D4B09F29BF2B72F42D63D0F01ECA4ACAA9B3345F19A635DFDC51D17FFE4D30B1649E76088E0FC7F25BC68E67E79567E7151403A8E4CF43B3D50A407E2DBBBB994778934F61BB157F97294C37E4FE7173C2530F5F0CBF92C47EB0C56E95A6B5BECE7C6C2B09F44C654DAA6F2F
	6827AF737F1ED77BBD241F497CB1DA3E3A17DA55713EDFF9B078F8E5E54AB834DF3CC96D17D87AC1F5CF47ACEAD3F5FE39DD9D3FD6BFFD3E523A54FEEF749E8E4C1745A65A1D239A26685C7E20DFEB5B06CF9F5331FD50FFEED35A681C263729F3CB695FB08BD34A78889EC9147D7BC4077ADE26F90FEE8F15837CA0C0E93CF4DFE9A879B3E3705C5AC1E3605CDA0EB1F0AE7804F16AEF13CA0D413FCDBAE554FE1BC477F6DBB77A499618BF8B40EC0079GD1EDECDF7F0E91ED873FEC1D3C271F330CE2EE8F3FE6
	15997D2F558417B53AA674B7304F55E01EEB48A94B4EB19C1F6740EBF03EE4270BA0A9EEC58D6A48ED9B354A3429027FD18DB9D3C8A384CD4F153FA7E7559810AB2E0D1DB5671733B369C60E7B439DF6A64DEAE7B83D404E24CD9CD755C372A9D00EEC67F971B40C3117707286D20E65653034D03606FDBF18BDBF9212468779561046D24DFDAC16CA6C08C92E6DE0F918E736E20CBB2D6F854CB659AEB510280B66921A7A593782F2874BA53477A31DC8A8AF85D855416E44FB86D89C3E9EF0791D6C9E701F87D8
	2E45524946789EE9BB9F4A0BGE2G263767B1BF3531C790F8DEFFA4C8DE1FC4BA1407FDF96CCEEEB5672B5440F0AD1798CED741727F111758B96249DE442D0632F0192D3F7FBAFD921E5D623B133F8881E4275B69125C728445122F1214F57D1F64CC17856354828C82F88166GAC84D88C9083908B3081405095131C83D08DE08298GF0814C6AE2FCBCFEAEEF8ABE14E49BF186EE115E17D1BE1E3FAB70F5F9396BE830B1401BDCG15F68C3DAB8D4BE6F1147779CBF7F0FF626D8E6DCF9E68A65F66009FB0CF
	3814FB03873B499D5E4808C762FE6B635D860EEB75F4BABDB68CC7697E39B9ADF80E63D09A0EBF8F743490001E831EABE7670095DD747C90874F0DDD748E0D3659F757BF171EEE067323192EFFFAD6B8A604BAAB303C81C29D74B51B553302955E8F40E2784D1B6C2C505E93B88FB2C2FC198C6D434C6CFC73EABB493533483EC91CE834BA1D9044A83976BC3D167FC8B315ED83BCD32FB0FF276B8FF5EE4D399268FE5790325FC13FB26D57CA7A55DD191E8FB5DFE13FA3F91678996B957FBC40F202172F786781
	4C7D4C7F04C15DEAAE6F3501FB7B8D4F75FB6C37F0467D28DC7557107F16EEFFF9AF903C395BDF5E9FF2FFBB88F5CB3B193C0772826DB539BDD47F35BD0167219370963FBAAF146774843205AAC3A09A8B14360B0250D88678D5FEF56B201C589B08C6324F9DDA0652A6DFF95675CE26C1FD46D33D784EE3B16AFED99C540BF195FE538DB1366DBDF2386D02060ED1DA7F672B417D1A50975A2FED698B9CA7697BA647C969FD016324FDFD5AB8C9790E298FFD4AA87873DE5EAE07632EF2DC9E9993F1BDB60BEC65
	78A2429762DBA4A4487095042F1B84574B3E1B55602EAA3833FD681BC1E42B13FFCB5154C7721BB63B1D16DBC89B1B83DCB557C79B61516692B5FCDF66B8641B6E67A5A4E5291C9B4B33046067468AB8941516D46751B530361C45BCE63007B93DEC9F2F28E0F14B52DE96F31C3903EDB7C2B97DAA44A7D7D95B3A3BACBEE93A4A5ABA6EE25BDBD08E87083CBA5378440C47D8C5361B892AECBDE953110D6E9F8B4A4F882163B6189251B16A7BF10FE6DBD03E3191757CF73ADC46FF2CA27A00F2FA9F688300FEE3
	31E457F6B20FF61BC3EA243FF09358B7AB03A49EEC023683FDAC667E9131D79714E775B19F342CDF606B78C54DFBDF7F7DBFBBFABE849A672F29B2F5EA5EFFB265B7A1FF9D5AFF3C2FB4681C5613F8F9AE4C6782BE4F0F6771BBF20EBBD6446E2517F05C2289F6AF3D12635E313333DA9C472DFA0B5DCBD777B35CD58323EB6238EF98985DDE0EFB330C5135F39C35F1249B46F1ABFA995DF00E23F1BF52DD4DF187697DB514D38698AE2290F119D04E67386D7CDE3FFA00597A6B657796CF45CF790D88C260F84A
	C87A4D9E18DEBCB5FF0045D3DFA57EAFEA40BF3E78A641C78F784797CDF7D8FC318CF5F00D49FB6E6E3D45B72F911FBA771ABF7D5F927C7CEB7E74653B0CFE8454253E4D680F6827226FFABB5837A21B496F15E73D6DCFDFCF70F35F7627BF2F0A511F8FF542BBAC863F590FFB2C0CC7E54C57896C4C718F44B7DA201D0B37DDCB76BA52D6E07B4127F15BD476035EBC5F7B3FE7485F2E20633DC4565B8298AB729DF61759CD6C6FE5A82F6638DF1075E6980CC94E9CE47799B76CB3394F703FBBB441B8183BCA62
	3CEC5B03B4FA81EF99E4397A584DAA2F3F246B966A668FB23E0E13BC4EB2A82F64380F11B5AA08B01FA26375573DB3B9CF7973DA8B63547B705ACFF8E9813CD9E473D0D161FB97FB112C77B0281F435BFC2B50F7AE481F9AD14EFCB9C5216E0B273A0B0D96296D623B721DA7213F8CF5A0B25F727CED442D07F24A90337545F784012D7BEFEB5EAF5C397F7B4C7007C0A36E2EAA539FB46F6F5D3D7F349F3E2FB4681C5E56337DA21D4FE9EA35AA67EE4D7B67267C7E712FA37353771586DDB7DECCE7A87E6D3BFD
	781B37GF79C75F0F0329F4E4FAAE177AE346FE7D2553E7B0B2638D7292E020E157CDE655F372B7D1E1C18FE3F238635DF17FD3A77B134EF97267C15779BBB55F1DE6E1FBE7DC7F6297DDEAF1B6A1E2D2414F6247D225355FEF3D3275F6F489E355F52E95FBF513E4D992A3C72DEF51C57AA27CF3FBFD36DB73FF0E6746F9CD07BDAAB826F41E1442FDAE02D390618DF7F796D49FB7023B8C50AEF8F574F686E9F46DDB4C4FDBB397B9FD26386BA76170E2AFCFE6376F46DFFE6674B95F835AA503FFF955172E9D0
	C664DAA1C99612152BFF0FDB50F30A2D64C89EFB234C18704B4FA7680444D6E1B3CDD1348A0918E7E89512C89A22D5C8101DAE2FCB707B87236D1CBC37751F78490FA35FE9CA5789BA9850AA519112EEE262010C11FEF75326CD1E3F88F768879EGDF0011367BE86239C7ABFE9573CA37A7C1585CAA6C458D3ACBF2135C0CE4416F7E15BFFE52ACECF1618FBEB0BD8ACCFB48A349A3CF58A51310BEF299FF846C11A5CBBD4B7AF8E4E13789A995614DC6D10B68314A28189A13106840BF53A26C2455F6B8BD9751
	94D08D61BA3EFE10881D26C83E0B700B239472D1F6FDD3FFB47B426A4589022ED958EAF70A16E6F1D8F63ADB05AD3255ADF92CCE3B456BBADBC786CA39493E47CC006176F371D158C27C5A02B3FD5D7B59B95B6336B76B40093A05A252926311C25E4252E8955DC0CAF2374A78E7E71C6E9CDE7740C17958774E6F9950616DB79192D51F62F5494015DBF4F870E70640C1169F872C0A8FD478708B451FFC720DBEB2105AD5968ED115C15722D149B9D6D4BFB2E2F3C8FCG509F8CFF008F0FED8A7D5A6C74BE347B
	42CB1B06E03E1B04043162E272FF89793F947FAFA1189202A9A985A52E7337C65FFF7E75DEE2F029B2B5923E8EF01A8B1BE126641F2EFD61311F552EDB8352B58BC964670420EC035A2AFDFCECC8F2BF987949835B42E3BF0EF361C213E063EE9E9147EC7689D0AA7FFE400BD51914B27B3E09193C4EE7814CD2CB81968716B6CFB2C144EE1DECCB08CEA31586727F2E99EB8AFB6DA6FD77A0C1FC59B7C92CE32AD8CCB4E4B65107756C3F8D56B7B7111D0354962AFCE3CC017BD5B91F8E44EED5A4C27E13FDE4CC
	D1E0B3177127GEF70B6CFEB5A68FC5B48C21A6F8FA5E4A1DD7D5B8DD98D631EAC7CDE4B886E673C649CF50BAEABB434B32421CDF4E5310F2E32DA1B0C81759A9FC42F095CC4E972166A87DBFD988A2F77F2D5E0D0559320747FEFA096155FD655C0B0BEC0835179CC68E4A9DC81CAFFD3B2F529B269B4B2A57D2D735D2F72CDE88A4AF744301C78AF0A3270A7C5B3DB8F082E417FCEFF988BE0F8728610C0BB536F37726E7A7C9D0C0F9E887AEDE26E18F2D7B7667FBB7A4B64EFE9F9819FBE463E81F84CD4A084
	3F3F6F9A936DF6D934DF2FF7D870AE3FE1C216546F12FA5AF0FC8FEB23D4523A375B106FF7B5FD7E8FD0CB8788A26A48F69F9CGG4CD5GGD0CB818294G94G88G88GABD2A3AEA26A48F69F9CGG4CD5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGD99CGGGG
**end of data**/
}

/**
 * Return the DisableCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getDisableCheckBox() {
	if (ivjDisableCheckBox == null) {
		try {
			ivjDisableCheckBox = new javax.swing.JCheckBox();
			ivjDisableCheckBox.setName("DisableCheckBox");
			ivjDisableCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDisableCheckBox.setText("Disable");
			ivjDisableCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDisableCheckBox;
}


/**
 * Return the IdentificationPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getIdentificationPanel() {
	if (ivjIdentificationPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Identification");
			ivjIdentificationPanel = new javax.swing.JPanel();
			ivjIdentificationPanel.setName("IdentificationPanel");
			ivjIdentificationPanel.setBorder(ivjLocalBorder);
			ivjIdentificationPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelSubName = new java.awt.GridBagConstraints();
			constraintsJLabelSubName.gridx = 1; constraintsJLabelSubName.gridy = 1;
			constraintsJLabelSubName.ipadx = 57;
			constraintsJLabelSubName.ipady = 2;
			constraintsJLabelSubName.insets = new java.awt.Insets(2, 25, 5, 2);
			getIdentificationPanel().add(getJLabelSubName(), constraintsJLabelSubName);

			java.awt.GridBagConstraints constraintsJTextFieldSubName = new java.awt.GridBagConstraints();
			constraintsJTextFieldSubName.gridx = 2; constraintsJTextFieldSubName.gridy = 1;
			constraintsJTextFieldSubName.gridwidth = 2;
			constraintsJTextFieldSubName.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldSubName.weightx = 1.0;
			constraintsJTextFieldSubName.ipadx = 66;
			constraintsJTextFieldSubName.insets = new java.awt.Insets(2, 3, 3, 20);
			getIdentificationPanel().add(getJTextFieldSubName(), constraintsJTextFieldSubName);

			java.awt.GridBagConstraints constraintsJLabelGeoName = new java.awt.GridBagConstraints();
			constraintsJLabelGeoName.gridx = 1; constraintsJLabelGeoName.gridy = 2;
			constraintsJLabelGeoName.ipadx = 32;
			constraintsJLabelGeoName.ipady = 2;
			constraintsJLabelGeoName.insets = new java.awt.Insets(5, 25, 2, 2);
			getIdentificationPanel().add(getJLabelGeoName(), constraintsJLabelGeoName);

			java.awt.GridBagConstraints constraintsJTextFieldGeoName = new java.awt.GridBagConstraints();
			constraintsJTextFieldGeoName.gridx = 2; constraintsJTextFieldGeoName.gridy = 2;
			constraintsJTextFieldGeoName.gridwidth = 2;
			constraintsJTextFieldGeoName.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldGeoName.weightx = 1.0;
			constraintsJTextFieldGeoName.ipadx = 165;
			constraintsJTextFieldGeoName.insets = new java.awt.Insets(4, 3, 1, 20);
			getIdentificationPanel().add(getJTextFieldGeoName(), constraintsJTextFieldGeoName);

			java.awt.GridBagConstraints constraintsDisableCheckBox = new java.awt.GridBagConstraints();
			constraintsDisableCheckBox.gridx = 1; constraintsDisableCheckBox.gridy = 3;
			constraintsDisableCheckBox.ipadx = 26;
			constraintsDisableCheckBox.insets = new java.awt.Insets(2, 25, 12, 52);
			getIdentificationPanel().add(getDisableCheckBox(), constraintsDisableCheckBox);

			java.awt.GridBagConstraints constraintsJLabelMapLocation = new java.awt.GridBagConstraints();
			constraintsJLabelMapLocation.gridx = 2; constraintsJLabelMapLocation.gridy = 3;
			constraintsJLabelMapLocation.ipadx = 3;
			constraintsJLabelMapLocation.insets = new java.awt.Insets(7, 3, 11, 1);
			getIdentificationPanel().add(getJLabelMapLocation(), constraintsJLabelMapLocation);

			java.awt.GridBagConstraints constraintsJTextFieldMapLocation = new java.awt.GridBagConstraints();
			constraintsJTextFieldMapLocation.gridx = 3; constraintsJTextFieldMapLocation.gridy = 3;
			constraintsJTextFieldMapLocation.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldMapLocation.weightx = 1.0;
			constraintsJTextFieldMapLocation.ipadx = 83;
			constraintsJTextFieldMapLocation.insets = new java.awt.Insets(7, 1, 10, 20);
			getIdentificationPanel().add(getJTextFieldMapLocation(), constraintsJTextFieldMapLocation);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjIdentificationPanel;
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
 * Return the BankAddressLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelGeoName() {
	if (ivjJLabelGeoName == null) {
		try {
			ivjJLabelGeoName = new javax.swing.JLabel();
			ivjJLabelGeoName.setName("JLabelGeoName");
			ivjJLabelGeoName.setText("Geographical Name:");
			ivjJLabelGeoName.setMaximumSize(new java.awt.Dimension(112, 16));
			ivjJLabelGeoName.setPreferredSize(new java.awt.Dimension(112, 16));
			ivjJLabelGeoName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelGeoName.setMinimumSize(new java.awt.Dimension(112, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelGeoName;
}


/**
 * Return the JLabelMapLocation property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMapLocation() {
	if (ivjJLabelMapLocation == null) {
		try {
			ivjJLabelMapLocation = new javax.swing.JLabel();
			ivjJLabelMapLocation.setName("JLabelMapLocation");
			ivjJLabelMapLocation.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelMapLocation.setText("Map Location ID:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMapLocation;
}


/**
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSubName() {
	if (ivjJLabelSubName == null) {
		try {
			ivjJLabelSubName = new javax.swing.JLabel();
			ivjJLabelSubName.setName("JLabelSubName");
			ivjJLabelSubName.setText("Substation Bus Name:");
			ivjJLabelSubName.setMaximumSize(new java.awt.Dimension(87, 16));
			ivjJLabelSubName.setPreferredSize(new java.awt.Dimension(87, 16));
			ivjJLabelSubName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelSubName.setMinimumSize(new java.awt.Dimension(87, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSubName;
}


/**
 * Return the JPanelAvailableDays property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelAvailableDays() {
	if (ivjJPanelAvailableDays == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Peak Operating Days");
			ivjJPanelAvailableDays = new javax.swing.JPanel();
			ivjJPanelAvailableDays.setName("JPanelAvailableDays");
			ivjJPanelAvailableDays.setPreferredSize(new java.awt.Dimension(250, 218));
			ivjJPanelAvailableDays.setBorder(ivjLocalBorder1);
			ivjJPanelAvailableDays.setLayout(new java.awt.GridBagLayout());
			ivjJPanelAvailableDays.setMinimumSize(new java.awt.Dimension(250, 218));

			java.awt.GridBagConstraints constraintsJCheckBoxDayChooser = new java.awt.GridBagConstraints();
			constraintsJCheckBoxDayChooser.gridx = 1; constraintsJCheckBoxDayChooser.gridy = 1;
			constraintsJCheckBoxDayChooser.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJCheckBoxDayChooser.weightx = 1.0;
			constraintsJCheckBoxDayChooser.weighty = 1.0;
			constraintsJCheckBoxDayChooser.insets = new java.awt.Insets(0, 3, 3, 3);
			getJPanelAvailableDays().add(getJCheckBoxDayChooser(), constraintsJCheckBoxDayChooser);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelAvailableDays;
}

/**
 * Return the BankAddressTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldGeoName() {
	if (ivjJTextFieldGeoName == null) {
		try {
			ivjJTextFieldGeoName = new javax.swing.JTextField();
			ivjJTextFieldGeoName.setName("JTextFieldGeoName");
			ivjJTextFieldGeoName.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjJTextFieldGeoName.setColumns(15);
			ivjJTextFieldGeoName.setPreferredSize(new java.awt.Dimension(33, 20));
			ivjJTextFieldGeoName.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjJTextFieldGeoName.setMinimumSize(new java.awt.Dimension(33, 20));
			ivjJTextFieldGeoName.setDocument( new StringRangeDocument(60) );
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldGeoName;
}


/**
 * Return the JTextFieldMapLocation property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldMapLocation() {
	if (ivjJTextFieldMapLocation == null) {
		try {
			ivjJTextFieldMapLocation = new javax.swing.JTextField();
			ivjJTextFieldMapLocation.setName("JTextFieldMapLocation");
			// user code begin {1}

			ivjJTextFieldMapLocation.setDocument( new StringRangeDocument(64) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldMapLocation;
}


/**
 * Return the NameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldSubName() {
	if (ivjJTextFieldSubName == null) {
		try {
			ivjJTextFieldSubName = new javax.swing.JTextField();
			ivjJTextFieldSubName.setName("JTextFieldSubName");
			ivjJTextFieldSubName.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjJTextFieldSubName.setColumns(15);
			ivjJTextFieldSubName.setPreferredSize(new java.awt.Dimension(132, 20));
			ivjJTextFieldSubName.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjJTextFieldSubName.setMinimumSize(new java.awt.Dimension(132, 20));
			ivjJTextFieldSubName.setDocument( new StringRangeDocument(60) );
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldSubName;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	com.cannontech.database.data.capcontrol.CapControlSubBus subBus = (com.cannontech.database.data.capcontrol.CapControlSubBus)val;

	String subName = getJTextFieldSubName().getText();
	String geoName = getJTextFieldGeoName().getText();
	
	subBus.setName(subName);
	subBus.setGeoAreaName(geoName);
	
	subBus.getCapControlSubstationBus().setDaysOfWeek(
				getJCheckBoxDayChooser().getSelectedDays8Chars() );

	subBus.setDisableFlag(
			getDisableCheckBox().isSelected() 
			? new Character('Y')
			: new Character('N') );

	if( getJTextFieldMapLocation().getText() == null || getJTextFieldMapLocation().getText().length() <= 0 )
		subBus.getCapControlSubstationBus().setMapLocationID( CapControlYukonPAOBase.DEFAULT_MAPLOCATION_ID );
	else
		subBus.getCapControlSubstationBus().setMapLocationID( getJTextFieldMapLocation().getText() );
	
	originalMapLocID = subBus.getCapControlSubstationBus().getMapLocationID();

	return val;
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

	getJCheckBoxDayChooser().addActionListener(this);

	// user code end
	getJTextFieldSubName().addCaretListener(this);
	getJTextFieldGeoName().addCaretListener(this);
	getDisableCheckBox().addActionListener(this);
	getJTextFieldMapLocation().addCaretListener(this);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CapControlStrategyBaseEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(408, 374);

		java.awt.GridBagConstraints constraintsIdentificationPanel = new java.awt.GridBagConstraints();
		constraintsIdentificationPanel.gridx = 1; constraintsIdentificationPanel.gridy = 1;
		constraintsIdentificationPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsIdentificationPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsIdentificationPanel.weightx = 1.0;
		constraintsIdentificationPanel.weighty = 1.0;
		constraintsIdentificationPanel.ipadx = -10;
		constraintsIdentificationPanel.ipady = -9;
		constraintsIdentificationPanel.insets = new java.awt.Insets(6, 8, 1, 8);
		add(getIdentificationPanel(), constraintsIdentificationPanel);

		java.awt.GridBagConstraints constraintsJPanelAvailableDays = new java.awt.GridBagConstraints();
		constraintsJPanelAvailableDays.gridx = 1; constraintsJPanelAvailableDays.gridy = 2;
		constraintsJPanelAvailableDays.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelAvailableDays.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelAvailableDays.weightx = 1.0;
		constraintsJPanelAvailableDays.weighty = 1.0;
		constraintsJPanelAvailableDays.ipadx = 142;
		constraintsJPanelAvailableDays.ipady = -140;
		constraintsJPanelAvailableDays.insets = new java.awt.Insets(2, 8, 183, 8);
		add(getJPanelAvailableDays(), constraintsJPanelAvailableDays);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	boolean amfmInterface = false;
	try
	{	
		amfmInterface = ClientSession.getInstance().getRolePropertyValue(
			CBCSettingsRole.CAP_CONTROL_INTERFACE, "NotFound").trim().equalsIgnoreCase( "AMFM" );
	}
	catch( java.util.MissingResourceException e )
	{}
		
	getJLabelMapLocation().setVisible( amfmInterface );
	getJTextFieldMapLocation().setVisible( amfmInterface );

	// user code end
}

/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 9:11:36 AM)
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJTextFieldSubName().getText() == null
		 || getJTextFieldSubName().getText().length() <= 0 
		 || getJTextFieldGeoName().getText() == null
		 || getJTextFieldGeoName().getText().length() <= 0 )
	{
		setErrorString("The Substatin bus Name text field and Geo Name text field must be filled in");
		return false;
	}
	

	//if we are using MapLocation IDs, we must validate them!
	if( getJTextFieldMapLocation().isVisible()
		 && getJTextFieldMapLocation().getText() != null 
		 && getJTextFieldMapLocation().getText().length() > 0 )	
	{
		String[] mapIDs = null;
		
		if( originalMapLocID != null )
			mapIDs = com.cannontech.database.data.capcontrol.CapControlYukonPAOBase.getAllUsedCapControlMapIDs( originalMapLocID );
		else
			mapIDs = com.cannontech.database.data.capcontrol.CapControlYukonPAOBase.getAllUsedCapControlMapIDs();

		StringBuffer buf = new StringBuffer("The MapLocationID selected is already used, try another\nUsed IDs: ");

		String mapId = getJTextFieldMapLocation().getText();
		for( int i = 0; i < mapIDs.length; i++ )
			if( mapIDs[i].equalsIgnoreCase(mapId) )
			{
				//setErrorString("The MapLocationID selected is already used, try another");
				for( int j = 0; j < mapIDs.length; j ++ )
				{
					if( (j % 20 == 0) && j != 0 )
						buf.append("\n  ");
						
					buf.append( mapIDs[j] + "," );
				}

				setErrorString( buf.toString() );
				return false;
			}
	}

	return true;
}


/**
 * Comment
 */
private void jCheckBoxDayChooser_Action(java.awt.event.ActionEvent e) 
{
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
		CCSubstationBusBaseEditorPanel aCCSubstationBusBaseEditorPanel;
		aCCSubstationBusBaseEditorPanel = new CCSubstationBusBaseEditorPanel();
		frame.setContentPane(aCCSubstationBusBaseEditorPanel);
		frame.setSize(aCCSubstationBusBaseEditorPanel.getSize());
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
	com.cannontech.database.data.capcontrol.CapControlSubBus subBus = (com.cannontech.database.data.capcontrol.CapControlSubBus)val;

	String subName = subBus.getPAOName();
	String geoName = subBus.getGeoAreaName();
	String daysOfWeek = subBus.getCapControlSubstationBus().getDaysOfWeek();
	
	getJTextFieldSubName().setText(subName);
	getJTextFieldGeoName().setText(geoName);

	getJCheckBoxDayChooser().setSelectedCheckBoxes( daysOfWeek );
	
	getDisableCheckBox().setSelected( subBus.getDisableFlag().charValue() == 'Y'
						||  subBus.getDisableFlag().charValue() == 'y' );

	//set our mapLocIDs values for all variables
	originalMapLocID = subBus.getCapControlSubstationBus().getMapLocationID();
	getJTextFieldMapLocation().setText(
		subBus.getCapControlSubstationBus().getMapLocationID().toString() );
}
}