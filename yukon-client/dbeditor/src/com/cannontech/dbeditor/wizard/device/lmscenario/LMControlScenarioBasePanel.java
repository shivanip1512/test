package com.cannontech.dbeditor.wizard.device.lmscenario;

import com.cannontech.database.data.device.lm.LMScenario;
import com.cannontech.database.db.device.lm.LMProgram;
import com.cannontech.database.db.device.lm.LMControlScenarioProgram;
/**
 * Insert the type's description here.
 * Creation date: (3/31/2004 9:54:18 AM)
 * @author: 
 */
public class LMControlScenarioBasePanel extends com.cannontech.common.gui.util.DataInputPanel {
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjNameJLabel = null;
	private javax.swing.JTextField ivjNameJTextField = null;
	private com.cannontech.common.gui.util.AddRemovePanel ivjProgramAddRemovePanel = null;

class IvjEventHandler implements com.cannontech.common.gui.util.AddRemovePanelListener, javax.swing.event.CaretListener {
		public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {
			if (newEvent.getSource() == LMControlScenarioBasePanel.this.getProgramAddRemovePanel()) 
				connEtoC5(newEvent);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == LMControlScenarioBasePanel.this.getNameJTextField()) 
				connEtoC1(e);
		};
		public void leftListListSelection_valueChanged(java.util.EventObject newEvent) {};
		public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
			if (newEvent.getSource() == LMControlScenarioBasePanel.this.getProgramAddRemovePanel()) 
				connEtoC6(newEvent);
		};
		public void rightListListSelection_valueChanged(java.util.EventObject newEvent) {};
		public void rightListMouse_mouseClicked(java.util.EventObject newEvent) {};
		public void rightListMouse_mouseEntered(java.util.EventObject newEvent) {};
		public void rightListMouse_mouseExited(java.util.EventObject newEvent) {
			if (newEvent.getSource() == LMControlScenarioBasePanel.this.getProgramAddRemovePanel()) 
				connEtoC2(newEvent);
		};
		public void rightListMouse_mousePressed(java.util.EventObject newEvent) {
			if (newEvent.getSource() == LMControlScenarioBasePanel.this.getProgramAddRemovePanel()) 
				connEtoC3(newEvent);
		};
		public void rightListMouse_mouseReleased(java.util.EventObject newEvent) {
			if (newEvent.getSource() == LMControlScenarioBasePanel.this.getProgramAddRemovePanel()) 
				connEtoC4(newEvent);
		};
		public void rightListMouseMotion_mouseDragged(java.util.EventObject newEvent) {};
	};
/**
 * LMControlScenarioBasePanel constructor comment.
 */
public LMControlScenarioBasePanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (NameJTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMControlScenarioBasePanel.fireInputUpdate()V)
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
 * connEtoC2:  (ProgramAddRemovePanel.addRemovePanel.rightListMouse_mouseExited(java.util.EventObject) --> LMControlScenarioBasePanel.programAddRemovePanel_RightListMouse_mouseExited(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.programAddRemovePanel_RightListMouse_mouseExited(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (ProgramAddRemovePanel.addRemovePanel.rightListMouse_mousePressed(java.util.EventObject) --> LMControlScenarioBasePanel.programAddRemovePanel_RightListMouse_mousePressed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.programAddRemovePanel_RightListMouse_mousePressed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (ProgramAddRemovePanel.addRemovePanel.rightListMouse_mouseReleased(java.util.EventObject) --> LMControlScenarioBasePanel.programAddRemovePanel_RightListMouse_mouseReleased(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.programAddRemovePanel_RightListMouse_mouseReleased(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (ProgramAddRemovePanel.addRemovePanel.addButtonAction_actionPerformed(java.util.EventObject) --> LMControlScenarioBasePanel.fireInputUpdate()V)
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
 * connEtoC6:  (ProgramAddRemovePanel.addRemovePanel.removeButtonAction_actionPerformed(java.util.EventObject) --> LMControlScenarioBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.util.EventObject arg1) {
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
	D0CB838494G88G88G7BD405B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBB8BF0944715A4074FD4C5CEB9AED2A7673894E53023B3F2A257A909AF273A103392F336ABA695DCA10E6E82B92A422530C34ADC8557A9151610D8BE0BBEA00CG9189894992ACC2A001640DAC50879B19A556CAAB31FCA2F6249DC903F6F7D6B33312A0845FFB5D533323552C046433D5751869572F7BFD7A756B57EF87D2D43AB4B7AB3B08102CDC227FB5E69112F80E105FBD750BA7B9EEE4075BC6
	CCFFC7818A89896440F3927A2ADB5C368252F8E3873493205D7C1E5BF6G5E7711C7EEAEEF0017B8FC6EE15F9E7BBF4F4C674BDD1C0F0D36FFF3548E4FBC00C781170AE3F6A27F1D6545BAFEAD607274F7042CECC2D6D31A75EA31895F311351AD0767F3G5AAE24B9D6DE227797C39BE96A70390B515E0467CEAA5DC34EA61D6E6617124816978FE76FC1DE2679B92469E357694FB2320C9AE29B99EEF4433339EBF438E5D08E568E8A21109C52444111DA7F006817B4D9291D106E8A0A3F56AF0ECB03E2EDA028
	8E0AA1C111E42753F5259D0895B95023235A84D56C92C2E2G666C5A0D3A58349D148F68826D8C0A7BD8C37BAD0677E400745DDC1F9F6DE95665DDBC1AC456AD4B0B2BCF191D6951E10F6A1376CCEE6E96BB4EFF255C2E0FDB886DB4GA64B174B39ACCDFB38ACAB02C89B0676A2002586DE89A17EB9E82F865838074B785EBBDCC66FDF3ECD368E34E46747111164B1592A7322B2CED625659E24BEF4349B7D408FFD0BG16G2C84D887C0725D364354F7B33A0E4333F5C0EB1703C1B9549A8E37857C4A9559AF
	86605D69049E9D57AD8E4A0A1F90661FF64DE1A18F07627C293E0940F43BCA38BFA5F7FF1DE8DAC3F62E05EE292F9E49F668F3DC480F6A36B09F6D76DB3A36BEF847F138FE8C3FBD0278D5A6BCDB2B07A8BD36B7C11F37005B591B47E5F165BCC18ACEDF4836D9481278EFE733538B188C9985D1D9180D7F3787ED3C2350EDBB86508C608150G9697F29BD7396BE733F1A76C87A5A484FAA283E1C1515AA2D240AFAA3A2DFB4AE732359530F1421C47B13BA4551B57A8631F4EDB5AA5A369D8F6CAA133472A4228
	DD986F2F44653D31F03AEF6499EBD8CA77C4E6A1DB43ED863E06626DFB23F836364F523D056D9268DBG4068FFC569B76C0D751137A8FE473ED89F198CF19FF1C0DFA2GDB5F47FBB1CE669439EDE5GCDGDDGE100840025C5FC0D475C279EF09FF51956170DBD5E7DB6BC45C9CD91BAC32AA6048645BE08F442C0C0D409CED35F0DF69D18B22E096E6FC1607101989295C11364904CAE84C5F0A6155134F697A20DA252DE0F949401C1B0CC783C1D5D7B609990D42DB76C97B4B1CA417A5D5D689363C29F4C88
	93407B66A234D7850D39AB61BD23085B6A87FD08EB586F363976F33B3C5C3D971E17413945005359A18D229802F2077B686FB4544187746BGD25E0675BBGFB3458ED5BDA4C77699BDE6E0F9DFB1EA10ED7DBAC7D116E55034C37D28E467A23BD02FC5682FEABC071A1D8CBGAF4064A1FE66FEC173197D7100FE1E6D3776D74A676DE89F4AEBC5033A78CCF3B0DECFCD6135D3FF664E3227797B1EB8B43B753D405BECDD9A3C685B0920E7AA401AC3FCEDECBDE54CE771C865DE7A11828EA67A23AB45E8F3BCF8
	860963E2C86B9043E2488FAF2A6EA33BBDBB237EBBA49B3ED5C871984F5A4141D483FF40D34870B007F9B7707ED25AAFE81AA28DC4B431C754B4A9B49C9D7F8E15C51D92AAD5DD57F357D057CD20E34EE1372D68B057B545334F6407AED99E0D04BD3843F4FFFE161E5B55C0FF61B077074A9066E4FE23FD0A36237D5F8EFAE7D8AFAB4039669643D74F194736B950B29E1BC338B7F8DC4D5571BBF5FFE2B43F27B489A0FFAAC086C0CE095B66G7014F0FB5C8F79E618E7AEBA4CAE73631417951FF8FBE3BE40FC
	E7D9EFFC5FFF3A575A77D3FA2DFD7FDB3DB37B763F76DA79B66B7BFEAF6AAACF04CCFB6545DE0C2F03F2A8A4528DEB8CA1AC77FE4303FAC8A1C93BAA7907C52DC73AAB42DD2084FD5EC9E378BDF8FF3404475A098A9EEB0F5D7DC7327256C14B7CAC7149266C0DA54C17B3CB2231164974D29F6EC1B1A08641D06DF2A424B17C0F6879206355ED94D721E8B8367ACFF0EAFD6881BB3EAF09813FE3900203D6CB7FE567D275EDE1DC182CD5A740DA35F4A9EA5B3177A23E56141DBC59D2967F266098129431B3940E
	7049EA7AEA6A987F2A71F44A3FCB11076164EC757B3B4520BC2E0F9326B4EB95E9F8C4F3C92AF6C50E28622DA07EFBF1D252C4BF952FB622C901DAAA51C78337E1BDE615ABEC4575D6F7FCB6AECBBEFF84FDCEB3FDD26DBF78E47AF4A9222AFE868AF1BE71B5BA437677BE72D8632ACF26D3B7781C70D9A8E5B00A2FD5BD53694723DFFAF01D20599651B4C8AB699E3FA550C717288C49CAF05EEAD9EC0A8642ED7E394945F330B9FD7FECC4BC4B4EF969BA461812A3EEDB9D4025A37C1C3FF2957B79B92FA7B36D
	F2A04042A374A59F41585796A9B245B35F91D6E349B37AC3167DDB60497AFFED593F638F3CBFBCEE55DFE2747F4632BF4A7F48C43CFE262753DB006704B89691613A00094CE5718E4F793DFBE37A7A04C0A4FA17687462B999B065C08C5FFC4DBAEE37DD433CE754187F7DEB5B895E89581C6C1CC991F04E30A285A1E54424CE2FC52DAAB757226AC0FF2CDB05613990000D9D359F0267CC051E3AD21C6B2C4CEBD6CBE0ECAA409AFD3E02D2E69BC63755203B876D45G4F15B23A1CB2B35DF7431C6EE69923534A
	98DD7A943A0747B8DD49D1C657F01451ADBCEA267BC861F499BA5D1623D672952B1C6ED2B9237315CF25CBADC7BA37D157BAE560C6A86E5710707CC38E3F2FDFA9B057687C6AE94BB39BE1EBB91B5FDE91DB4778AA2DEBBAG5FDC4177524AEB661C39D5D565C109DEA3F5FF112F1B4FF306532E6F02A76CDBD56B04C4E9127B5C5D6BA8AB5EB3F5346EB7ABCE3CC7265F1BAF522AA03F1234CF501861115B612AFB895C365DC8F77AA4F102F0BB7C75FDF3EDAFAD3FAAFBF79CBBF4697A07CDF6E82ECA4BE5B53C
	A7A35C6EA9953C2ED7B54EF19B8D5C578CDC59B16E4B0FBB3156E3D56B9C248A568EC7A49A8761B2ACE8828DEE4C49591CE7AAF0CE145BFB0C5789EFDC46FD79A83413GD682301AC53FEC576D3896F08515BC3694394C3163D2A53B53046149E27B77E57427B926D87AFA25576CE56B9B9317799ABC316F024E77652FBFC11ABF36D956AE976597E6AF2C5463DDE5F48DAE6CD9116B81E07A9D8DF37D3682CDDD155B46E2CACE29B9260CD4B17D126019D645F5DC94C6B9BEE97E46F43EBB70A10DED7FDF399A2F
	B9EDB474DB83F25B5F6132FFB4567AE0324F1C2B99FBEC227B721C641FB5B55365CF996372F7006C778C798FA8ED8FA67FACF99917FF175BB5A779E74F42741A1F4265CF8559B799727FDC39B8DD7E7965E0042FF52B4AF99D2BF65BEED6F3DEC96AA58BDB4DB71F52EB6CE74C67414AF515F14F034555EC6FA5D5471E871F7FB34621F4402F2D6667417FDCAFB41D875D40D0888DC376E05C13EF20CE10A7686D2F5DC03EB4CFA03C96755DFE0CCD1BE04E9DB5EEDB99C09300370673283DB9EB2D7C62648C3572
	27A3F329F9CF56705A450971390CCBB046A54FE95CD2E3DCE3859F17DA235750EB2235218CF85F8C10F3BC0AE3756BD768D9D0FC1C552FED867EE794DFE742B33F93A89E5B1720AF79B8676F17E365AEB8B135B66508E933F14A34F135B16D2E18F1EC8FD7D6E34EC34FC553B163F4C66F7C6C7C3B6C62E76A4889DED75E6F4331CB211D863086E0B3C011834E84G0F037B4EDFEFDA6592D3FDA7DEA2912DC1B2BD5F894F4FBEE7270D7BC3CCDBF3D859670A7040F943548828678D4D255CEE898E1EB7BCB60AEB
	1F0EED00AD8E3E76EF4CD3374CD9F5331F349A772D317971CB1FB6AE165FB1CBFE77157971CB18B6AE56371BAD79FD204E0FDF6F34F1317CBCB17C58FE6895F02D8350F76FA4777D57EEE25D134766AED98AE1DD0D75BD7CE73C2F49F082CBA10ECB2038C1D92145C406FB21BFFE1D73077D56F54EB57D56F54E577AE72E717F22BFDE9D1455633FC7F3708450AF8900615A46B9AE4D40BDEF60D69B38BF519CFA8334C9AD47BD520B38A2E8D79B383C2B08F3C15BEF60CEF99137885ACB8DDC229BF12950FE3916
	6F21D7E9DCDD876DCC0022D39C3F1E6271EE5AF40A5D6D98FE78B22F6363983E1EB8EE2673E2B6485467E2BC9698BC4C3F8158BE417CBCBE6173881D775F5F4E276B8A8922CA03A95EE2743EDFEA7B09C5AD9ACB3B0457157EDE5E2953DC54647653066CDD2E194ED12F051D745F0AFC71FD364AE76D338E9F6E95D50EA803E22CDF567B0A0D3E6869502909C1D4C32F1350711A208C0B537C3A1D0EE7FD71463BFC71FC1F5928BFEC36519943C62D25B359A8E0E1A3169B8468EF648BCE315CA04B40C7A8BE4104
	E7B9C362A87F36A5917AB2CF71DC35F24CACD73DA16F6BD370519A62B795B37E2C013F2F1A71678C5BDD97106724516E276DBB041F11E2EB1EE15B0BA13F9E64D8DDE4AD0D97EF65900275G063BC6E3438B00EB4F623857A86E96CC78AFD9DC9E318257B32C08C3222208FE769389C92CC37A7BB4167860BDD0476D303C97E3E7C038A3C700FF5DE924BB58E73E13E6GED8E600BCF33B5E172BFBB01777AA1384440F2770445C1E2BF03E3AB2F723113C01FG108C70B24026531C677B34E6878325E0A4C8A5E4
	727553F86501F9FCE7B86DB75C14D6180C21FD0146318420CBB4E8B347116F54DFDF38DD9ED2F771BE17B000F5B806EF4847FDEED999A63CFEF92C5DDC03C8BF436E689B4F7018BA6C6231B75340F9CBB94ED54F632692A4067F7F45747E5551397EAEBA9750FF5F7DD4F9AC29476FGDBC60831AFBE5FDB864F10B8D1C6EFF74204D65BA1854590FEF8D15DF0264ED1D6FF6E2CFE47DBBAE13E63B56DBA91770E372E1E7960967D3981F6977347533EA9B9B9B2F4BABFD0A4FF1BB06C225E4DF24B7374F7BB1C5B
	5140F34BA07D0D4F8F6DFBG0B9B786F0713D53C86177C54934435603C6577F56BBCD559498DCC1E558D513BE702D1033BFC1573341FF55BBA4EA26E4ED533CF214DFE164DF2A6563FE2CABF46D83CFFC77B7FE3CABF4A5B394D5C1F6FB577671A46B35DDE2A643AE53E14C2825FBC99775B4104337AF7A87AF37215C3A469799539EC4D7BEFC7577C9177032CB99B7739516838164B8FB20E2D731BC34544726EC5F7B533770D769D3A4E9BC1E64FB99A7BDC9DB378EF2EAE8B07B29853G30DF6F3B846F6B3318
	FD58FCF269CC73715F72793CCBE0CC0ABE9F36534F316FED5FB35663159B66BDD070157D597C9C0B1DFB75B97D7B3DF331F58EB500FA97B43AED0EC61ECB7F4D4889F876893727D7238189F1AFACAA5A9D06253F2C68712223EF2E5F5B31F3F8BD5DCB2E46587379D7946FED0CBD1F55BEFEBE7B21EFD5A33FA7AF8F62BABB1A5C36CBGBE00C900C40054A663FB2C11BAB24B9D59A307DD10D085CCE93B1E23DD1D5F77045F3371FD137A1D2749F3FF7BFAD63C3A7C2AA636BE9B75E7418F8FD1BC532FCDC67D0A
	5F85BF83708204819682ACF9176BD7A4B918FE38BC2D1AA68C0E50DFA738F27803C1940D07B0534D57EE25DB4A3BCC0E74F72767D625149FA1BB887F5BFCFEBA0E477913E3443047FABB37C7490AE52463A72FE7D5443147DA1DEF35B36A379BD2F2F39F0C7766730C775FE1FA2A5B308353CC7EBD49E8E5BCBF87CBFD8821021595BD4230B1E6B200F29839ABF3FF3D6F63G46CF7C56C4928252DD7AD3D18F900606190DFF59073C75EF0ECC6FF77ABE7D337C31CF1587537F19E07C3BC8EA507AAE721D20757D
	F9E5F0666F04D68563FFA774CA507C1D90DBF73706EB6383BF3A873094A095E015136F1B11D0B9217B46E24FDC748F63DD883FCC55B87F4BF2FCDD3B65593FE73DA1E3EEB9A0EB1A9C4479B9DE10F13DB4B9CC118CB7C46F0C748784C62A6383B22D0308C3944DF24889FA0EEDG3D36B8F9DE19514E73CA72C79EAB3F914133288B5A8100C78112G52G56GEC844884A8E8F65B2A81AEGF8GC6G6E81A4GAC85C8ED6676D346EA626DA5C06AC995B522A218EBB3573478F674E9335BD1223F870B203F67CE
	D83FDF06B4DA6F999AD2C5E33DD42D4040F52ADD32AA616EE43944975B7177FD8D9831F871853CC269E7761B6D331D59E6E8B065G984FD601FDF80E4464F8BE92DF5F97A3336B7B6308355F7CB4B25DEF7EB3E26DB79BA2E63FE1387F0E601E9906F5729AF4EF9838EB866ECDBAA7622ECAFEED44403FC565C27CA59199F27C6F29DC93946755F1B963D15CB59D37F39C578CD49E110DFADC5EB82E4B1094883076DECAA305E0898DFB9C98C7992530E0123B4440215CECCD1F47EBB5E17FDF69615BBF07E7726B
	A4D7D5485E6979E36B97FF7A1364DB9DC559A43B13EC9F21F2F6125C89D4F8B28C2B8977302C656A98390FF3DA369412EF4C5BC985DEB7DE6F5C76DF4B36E5139C1814ADE0A74941D500399546E39BACA0F2A05BF52741CCB90F4E1D233B987F33E9E4BBDECD1DE5C6B7DA15FF493ACFBFEBEBBEB66708DB9F32473DA785DA58993B20A5B6C77CAD4D9153G3F36056F778F82A7636DF70818BDC1A190500440E8EB480F67E85B9DCD0C466ECCFA062A1CC66FE4FD5B68F9F05BB4667FGD0CB87886C64F87E0C92
	GG40B6GGD0CB818294G94G88G88G7BD405B06C64F87E0C92GG40B6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG4692GGGG
**end of data**/
}

/**
 * Return the NameJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNameJLabel() {
	if (ivjNameJLabel == null) {
		try {
			ivjNameJLabel = new javax.swing.JLabel();
			ivjNameJLabel.setName("NameJLabel");
			ivjNameJLabel.setText("Name: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameJLabel;
}

/**
 * Return the NameJTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getNameJTextField() {
	if (ivjNameJTextField == null) {
		try {
			ivjNameJTextField = new javax.swing.JTextField();
			ivjNameJTextField.setName("NameJTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameJTextField;
}

/**
 * Return the ProgramAddRemovePanel property value.
 * @return com.cannontech.common.gui.util.AddRemovePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.AddRemovePanel getProgramAddRemovePanel() {
	if (ivjProgramAddRemovePanel == null) {
		try {
			ivjProgramAddRemovePanel = new com.cannontech.common.gui.util.AddRemovePanel();
			ivjProgramAddRemovePanel.setName("ProgramAddRemovePanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjProgramAddRemovePanel;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
	
	LMScenario scen = (LMScenario)o;
	
	if(scen == null)
		scen = (LMScenario)com.cannontech.database.data.device.lm.LMFactory.createLoadManagement( com.cannontech.database.data.pao.PAOGroups.LM_SCENARIO );
		
	scen.setScenarioName(getNameJTextField().getText());
	
	scen.getAllThePrograms().removeAllElements();
	
	for( int i = 0; i < getProgramAddRemovePanel().rightListGetModel().getSize(); i++ )
	{
		LMControlScenarioProgram prog = new LMControlScenarioProgram();

		prog.setScenarioID( scen.getPAObjectID() );
		prog.setProgramID( new Integer(
						((com.cannontech.database.data.lite.LiteYukonPAObject)getProgramAddRemovePanel().rightListGetModel().getElementAt(i)).getYukonID() ) );
		prog.setDuration(new Integer(0));
		prog.setStartDelay(new Integer(0));
		prog.setStartGear(LMScenario.getDefaultGearID(prog.getProgramID()));
		
		scen.getAllThePrograms().addElement( prog );
	}
	
	return scen;
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
	getNameJTextField().addCaretListener(ivjEventHandler);
	getProgramAddRemovePanel().addAddRemovePanelListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMControlScenarioBasePanel");
		setPreferredSize(new java.awt.Dimension(420, 365));
		setLayout(new java.awt.GridBagLayout());
		setSize(420, 365);
		setMinimumSize(new java.awt.Dimension(420, 365));
		setMaximumSize(new java.awt.Dimension(420, 365));

		java.awt.GridBagConstraints constraintsNameJTextField = new java.awt.GridBagConstraints();
		constraintsNameJTextField.gridx = 2; constraintsNameJTextField.gridy = 1;
		constraintsNameJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsNameJTextField.weightx = 1.0;
		constraintsNameJTextField.ipadx = 240;
		constraintsNameJTextField.insets = new java.awt.Insets(14, 1, 6, 106);
		add(getNameJTextField(), constraintsNameJTextField);

		java.awt.GridBagConstraints constraintsNameJLabel = new java.awt.GridBagConstraints();
		constraintsNameJLabel.gridx = 1; constraintsNameJLabel.gridy = 1;
		constraintsNameJLabel.ipadx = 6;
		constraintsNameJLabel.insets = new java.awt.Insets(19, 24, 7, 1);
		add(getNameJLabel(), constraintsNameJLabel);

		java.awt.GridBagConstraints constraintsProgramAddRemovePanel = new java.awt.GridBagConstraints();
		constraintsProgramAddRemovePanel.gridx = 1; constraintsProgramAddRemovePanel.gridy = 2;
		constraintsProgramAddRemovePanel.gridwidth = 2;
		constraintsProgramAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsProgramAddRemovePanel.weightx = 1.0;
		constraintsProgramAddRemovePanel.weighty = 1.0;
		constraintsProgramAddRemovePanel.ipadx = 11;
		constraintsProgramAddRemovePanel.ipady = 30;
		constraintsProgramAddRemovePanel.insets = new java.awt.Insets(7, 0, 3, 0);
		add(getProgramAddRemovePanel(), constraintsProgramAddRemovePanel);
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
		LMControlScenarioBasePanel aLMControlScenarioBasePanel;
		aLMControlScenarioBasePanel = new LMControlScenarioBasePanel();
		frame.setContentPane(aLMControlScenarioBasePanel);
		frame.setSize(aLMControlScenarioBasePanel.getSize());
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
 * Comment
 */
public void programAddRemovePanel_RightListMouse_mouseExited(java.util.EventObject newEvent) {
	
	return;
}
/**
 * Comment
 */
public void programAddRemovePanel_RightListMouse_mousePressed(java.util.EventObject newEvent) {
	return;
}
/**
 * Comment
 */
public void programAddRemovePanel_RightListMouse_mouseReleased(java.util.EventObject newEvent) {
	return;
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) 
{
	LMScenario scen = (LMScenario)o;
	
	if(scen == null)
		scen = (LMScenario)com.cannontech.database.data.device.lm.LMFactory.createLoadManagement( com.cannontech.database.data.pao.PAOGroups.LM_SCENARIO );
		
	getNameJTextField().setText(scen.getScenarioName());
	
	//begin the fun
	initLeftList();
	
	java.util.Vector allItems = new java.util.Vector( getProgramAddRemovePanel().leftListGetModel().getSize() );
		for( int i = 0; i < getProgramAddRemovePanel().leftListGetModel().getSize(); i++ )
			allItems.add( getProgramAddRemovePanel().leftListGetModel().getElementAt(i) );

		java.util.Vector usedItems = new java.util.Vector( getProgramAddRemovePanel().leftListGetModel().getSize() );

		for( int i = 0; i < scen.getAllThePrograms().size(); i++ )
		{
			LMControlScenarioProgram prog = ((LMControlScenarioProgram)scen.getAllThePrograms().get(i));
		
			for( int j = 0; j < allItems.size(); j++ )
			{
				if( ((com.cannontech.database.data.lite.LiteYukonPAObject)allItems.get(j)).getYukonID() ==
					 prog.getProgramID().intValue() )
				{
					usedItems.add( allItems.get(j) );
					allItems.removeElementAt(j);				
					break;
				}
			
			}		
		}

		getProgramAddRemovePanel().leftListSetListData( allItems );
		getProgramAddRemovePanel().rightListSetListData( usedItems );	
		
}

public void initLeftList()
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List progs = cache.getAllLoadManagement();
		java.util.Collections.sort( progs, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		java.util.Vector newList = new java.util.Vector( getProgramAddRemovePanel().leftListGetModel().getSize() );
		
		for( int i = 0; i < progs.size(); i++ )
		{ 
			if( com.cannontech.database.data.device.DeviceTypesFuncs.isLMProgram( ((com.cannontech.database.data.lite.LiteYukonPAObject)progs.get(i)).getType() ))
			{
				newList.addElement( progs.get(i) );
			}

		}

		getProgramAddRemovePanel().leftListSetListData( newList );
	}
	
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getProgramAddRemovePanel().rightListGetModel().getSize() <= 0 )
	{
		setErrorString("At least 1 load program must present in this scenario.");
		return false;
	}

	
	return true;
}

}
