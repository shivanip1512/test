package com.cannontech.dbeditor.wizard.tou;

import com.cannontech.database.db.tou.TOURateOffset;
import com.cannontech.database.data.tou.TOUSchedule;
import com.cannontech.common.gui.util.JTextFieldTimeEntry;
import javax.swing.JTextField;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.gui.util.ComboBoxTableRenderer;

/**
 * This type was created in VisualAge.
 */
public class TOUScheduleBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, java.awt.event.MouseListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjJLabelScheduleName = null;
	private javax.swing.JButton ivjJButtonRemove = null;
	private javax.swing.JButton ivjJButtonCreate = null;
	private javax.swing.JScrollPane ivjJScrollPaneTable = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JTable ivjJTableRateOffsets = null;
	private javax.swing.JTextField ivjJTextFieldTOUScheduleName = null;
	private javax.swing.JPanel ivjJPanelTOU = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == TOUScheduleBasePanel.this.getJButtonCreate()) 
				connEtoC2(e);
			if (e.getSource() == TOUScheduleBasePanel.this.getJButtonRemove()) 
				connEtoC4(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == TOUScheduleBasePanel.this.getJTextFieldTOUScheduleName()) 
				connEtoC5(e);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() == TOUScheduleBasePanel.this.getJTableRateOffsets()) 
				connEtoC1(e);
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
	};
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public TOUScheduleBasePanel() {
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
	if (e.getSource() == getJButtonCreate()) 
		connEtoC2(e);
	if (e.getSource() == getJButtonRemove()) 
		connEtoC4(e);
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
	if (e.getSource() == getJTextFieldTOUScheduleName()) 
		connEtoC5(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JTableRateOffsets.mouse.mousePressed(java.awt.event.MouseEvent) --> TOUScheduleBasePanel.jTableRateOffsets_MousePressed(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jTableRateOffsets_MousePressed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonCreate.action.actionPerformed(java.awt.event.ActionEvent) --> TOUScheduleBasePanel.jButtonCreate_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonCreate_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JButtonRemove.action.actionPerformed(java.awt.event.ActionEvent) --> TOUScheduleBasePanel.jButtonRemove_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonRemove_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JTextFieldTOUScheduleName.caret.caretUpdate(javax.swing.event.CaretEvent) --> TOUScheduleBasePanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(javax.swing.event.CaretEvent arg1) {
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
	D0CB838494G88G88G37F5B7B1GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DF8D455153422258B2D28340DFE6ED70A285846CAAB6E520A7BD1CB3BD8590AAE3638E5DB2C59CADB545825DF33162F3CCCA690E000G9182A41361CFA6A1CC7EA18CA164E77287C3921388C1D17212F9498CCC668DB3AFE4D02AFB4E3D6F5E7766AFBFDA647B8E6F5DF36FBD775CF34EBD775C736EC4484A1B1E1EEA48920454F4C17DF7ACD59012BAG765714B1DC425FB69A855D3F3DGDB05AF
	96A3F296544DECB799B30507448CA84F03F2769913F1A73CEF973EAB36BB61A54EB8D6287B6106B7AF0CBCCE4DF1B64EBAD27E7DDBA6F8EE86188F38A40062B59BE17C54C359AABE834A1B55F704D403A02CA4EDDE65ED9094355DFCF8AE8118AA60BCF28E65287565D046B6CEF876GA0AE844F75043BDB6D15EA3B0BF74C92D67D2C553089475251E730D46D3BDAFDDA04DF90C12489397613702CEA3FB2D05DA78F5A7AC41FCF76A9D21F5B666A15DC9EC58E5806BD9F0A8117CD11076C762E36F7BA7B5C12EB
	48AB5509C129DD74C9DE686FCDC53E332C9904A6708D6594021BE2C5D94D067705G4BD3996F4FEE2AD2F91BF6E516307C3E5A383CD799A84FBD860D7725A97718B25310FE43A1135AEFBA54AF85203CFC65906325AB0D7132A2975BCE0172CC00F99C7F089971CB203C9240E8E4BC1EBC44F86C7E68DF05145E2B06ADF1F84CB1D25E9CC60D4709CB6FB46CA27632480EBA1FGF5B3816682AC83D88D1013EEB26EA1F67A256385702C6DD56A6541C159D76B7757F9DD013332CB72423B5D8EB5AA2EC36A1383AE
	C12036384BDA98039F866569A6E3A40E4E6D3C40ECA731633B02228C9852E34CED4E736D06E6154645F4EDEE5353D1EE46FC145B8DF847FE28BF0A5FCE70C9BABC55551D4496301C31966CFAAD13F377E6460B53B8C348AC899A564560A561C937E179DA4AC34ADA0D97AA63872AD0468756190CB5G5DGFE0029G3357B199BFFB3CECB49937006D87FC2237F32857AF86143AA10F57A585D4D91FB9B4122CE3816DF7E15C7D28DCE61569F5346C51C0CC39AC1BF55E30E89D15C752BACDAEF46CBB630EED4C08
	360D4DDC0797491A484E20BACC6378FE02376A70D4372F12350565CA283B9700365F1F07F8E1BDED3F1E53A9A6789C9D1E52F90C5047F2A5545D8BC0756B28C61FA8EC00B583D08E60847082CC8238F78353F10E3DE80C6B28DD27DF5A77F97B8EF8CAA1A5A03678020A686B134E09810F586B1502025A6657F614EBEFD83F17487A6E03B11AA41F9490950F6C836A622084469424EDD65A37E21B00C4EA3BBC03928CB06897985DD5766D70740AC1659D3FCBD4A42D852DDFEDC719DC934F81C5A0G6FABB790B9
	921FBB8F5E17EEE0323AFA8AF1459BCD46660DCCAE43A73641738C983764355B9D1EBEE4C38CDCE7B63AA19F67F0915A2F8618E98279EEB299978304B633F57AEAB733C74726070542672F4634C784E18B352D6CAD1176380B0CD383789E0089G53816683AC8720ED922CBD232C8F64136C051B6993795457A37F6CFD355A569951060E756828E325294FCD6AF3E3C47DBA5DFB3AEE7D6A5BD03D7C26062C05CC30E1002ACC261B6F1F30D01B45AD15D9E9DBG8CCCF2E91A22ED9F2942BDC83AA67994076417FC
	AEF8892AB672AFD56BB57B6D1739ED4DA3F874E775E0E0C10EFF32EAAB45838D7DEAE075BFA375222284BC3DC30A54A9A90A47B72075FF0E70928CF315C1F52EAF5662DC3BE00EDE009BFC2E6F1D5C2E33C327ACDF9972F7618AD36D79A132EFCF057633B219BD741AB17EDA444BDED2566AD765F60F224351GE90F6C43B719BF6B98E9A35451B17273B2853A1E52D45CFADEF714542D84BE3336026DG38363279BFCEF8CB550D417AFE96FE43F9DBC246C8535978BFF28CEA9B2D5571ED7B1D6A58367DDEF5EC
	5B962BC736DDCFF5AC5B25F57EEA1C23BC6C532D0521EA741FFD324FA71185493B88B40EFE358B676171F914739E570024F4FABE14A0FE5A0AB65DC4FC7494F81F3E157952617D4C179E7870A1E17EA5E74C78AB617EAB06142D54B6CD5BB4DFCAF93AF68A5718641586C1D0757210CF21789B447F2B78E09A413DF578AE6CEBBDCD96DB336C7538446B4142C1F9A8A859487F6D81A99814DC564B62B551A68EABB622885BD92CEB4057FC4B11A35516D8A700424BE1D4AF1D5553F346A5F8A27FDCFE31C0797BDD
	6FBD143F3AA1C511FD7538D1CA05A211350DBE5A25C03F9C980CE63116D40F4A231E72255A881211DC6AE8A21BA5024AE7CAD9981F9D5220FC6D2670C9A90F174FB24667A35EE1D55F30E69ABD125725EA27332F95821342BE707A0A0D7CCF838E42EC48969C06E522522E475A51586D778424961FFF081181A64AD59E9E3AF8374E66BA20362D3FBFA8A97FE03353919E1F65B1D9CDBCFCBDC2D63A51BE87E93178A02F9E57F3A12C739A006EEDEC4FCCB90575EC4FD48303FA596B252E886A96EFC3BF53161F25
	739DFE40E1EEE29EBCE97D621875595BD97D5B85316A8BF97DF3B16BB773717FB6CAFD4161F875F41E7FD303FBDCD03ABAA4C1680D2ABDA3DDE771F34D36083AF322F7C80B4BD755E0DC66554593947FF2EDEC9F79CAAD4690D7B87D57EB578A98DFD31A54274F961126BF609904708B83A4B507B3BFAC07D3837241FC4F84F8CE8320B63565A44EB5168DF833D0961773D80EA7917AACGD82152B910C57DBCEDF744425A4D0572E20064AC5A2E78CDFD3B57F93B9060273E29351B9D566E7E3D2CDD8A60B3F718
	0C45BBE8BB6F8E5AEE4A8EEC77814F6594F15C90413DG9B7F2DC6F6C6BD1B294FC139022E187B98318115FE520E4833FB1783699AF7C2EC3313597C15BAFD1CD89B8C4AFD9EF2F4D275FA30DE3F47D1DCC17D86F842928B42B95EA50518ED9457A32FF836D2512AFE3FF398F3DF11E745861275E2E1F87DB0D952DDF2BD9C6F1A413C6AF988F04EA3210F2579265B72193C2CBB599E7F77C6FD0EEB5E168B060DF1E45333134AE442CECDB64DCB6FB4D05A6B8A986D79BBD9FEEB924725G0E4E67EEBB66B7E265
	727A4884ED83C39E5B10624183202808448F518C9E25D9C0740B340A33D9EE6C0D82DCBFEEA8CFGC8G2067743B48799D5B4E865C8AG3AB6FE5C2ADF9B16DDA62383408DF0E397DB9BEF649D01673876G2E3397F723CCBF0B63273A19313DC665F151F7E788D51F58E36676A6EDF198266D22BA1937CB53CD422BDFB0A482D0D91C33B0D9E46C865F3D1B794B7773747EAE847869G8BF6B3B9BCE1A9056778E387A60BB3C3C4961FA12E217C6F5E4B78DF8A3CE56EE17CBB4C3178EF5E436C2D522A9B7FD345
	ABF47C2EC3EC7C0BC0FB9A9F7F51307DE2519EFAA6DC3507496F71C3681B4639B72B32DB103F0D7A0A086D1867C7CB75FEED7E726E38FEADA3075AC5CECE24DF2BEC4475D489F8E78E73EB5F295F2A73EB9D60C9C45FG6CD64CD7DDF0605CE0DFD24B6FBBF0DC32AF896CACB6B9FFBC79441E9C16979AEAC2F9AAD01E8AB08BE0BE40F24EDB12E3547CE8C3E8047C2829E0BCFC89396C7C7A15F1754B627D0A22F25F56DC6D9CDF159BFD262739C75FE1343776DC1AFBDC47719F923CDB072776A990BC16A7C0DD
	929F7F6E3C48711B4D61E33A4C31F2CE8FC675B3C714A7C57423B6FB79A3742D443F6BCC5CEE2771B5753FBF4D557B5F69E63AE69741F339196D9DFFED4DD0F9C9B1B3BFDFC1723745F9303E81FA72184DBC5D0C71C2884A53G928196822C844830180C0596E6BBE58D317652F05B093711EAF9A7AA1F0B16CFA7574BD17DA2656A3444126B164EDD3165CABD1B403E5D7D4926172D4BC27D51A4F85EEBE1321D160732453DE40E055909E56F271B0FAD2ADF64FC3277067723BCFCE4E6BC946FE5BC3C9B759DE0
	ECBC38237A59A24A5376461269C1EBCE9CDBE5E7AC4A6B9F8EB3DBCC5C4BECEE63E944A5C1F955DEE6DFD39BB07F4A7CE63B6C71A9827319A79BB1F617831E810F0F616A882ECF8E10A48E4DAD4EA0B11AF11F49E85947687E3DA9FE4EE9E2D36C1C53BFB5454EB9CDED9AB91F1A5094AFA7C5795BDF4078AB872038199C574CF10DA4CE3B88E5BF47F9485C26C1B9F19F5339C27C56E2A827GE46FE7788984CFE27F7D54BFD07C5A437A9CA7769B2BCFC65A7AF2CA0C3EF40C3C30B1C64A09466A9FE9E711EDF2
	894D27F6ECA1FA00D8B0C81C8EC63BEA5D4B4A1BC20CBC9E1E7285168FD81C37DEED53204875A55C26879AF2E35BF4E7DFGCE6398CA73B57837DC54875AEC7BFE66E3B737225C67C0F991C09257075006EBECB514B38FC44B6DC08C9C5D2F9EA2BE3B72G5D2FD2B97E7B846F506169BEF6A217FDABEF073AB987589AF83EB97E9AD8569CFB8D7C36996796140782FDD2241D7F3EB91B57E9DE3EC51186D14C344D1FC8FFC58C8CC8D16B647FC8FFDA972F3F21A55EDA22BA3C4B2257E1A957617F664759C36959
	1C1E030A186E328FB25DCDA6323B8165E9G09873598A0723B408228DBFA106AEC257A945E8A57E156DB51BA35822EAA82CF67F2FE2FFEAE65FCADCD087AA6518E7DDDAA0D107A1C92C1F3FA0C3167GEEE14C316FB8249F3B020F7D5A6148FDE1192EFFB23C9B8F055323FA4BBC0D76976265ACD23E4E75A355EE663AEF7039D4C7CF738FBFA0BE2E8C9A2625B21C1D784792F0766F8B8C77B841FDE494049F9958BC5E3762FE60952F4BC3C04FFB885BA9D67DFE1E8D389A00AE15EF4A4F7706714C5E9F908741
	CCBA7DD21F50EC453E9372D95FB950FEA1408A008C2833DA597964D7D7F1BF0B1BD564B29684BC0B1CF60A3D12B73C812DFF5605BA20968AB48416CFCBF06B4FC7D32DB49E19E3E53619466DE3A1601681A4DBC77769467C68B65900AB0C40D3DF73C56253EA72A36F89CCA178767C48FB82D92D6C1EC08F54AD48E77B574EB654193540E4F494B01C17603A212CF01CA1076DF3138A58BC9FC1372C5A65DC5DFB6DE77EAEB85AFE84F15567B0C6D111FE0CC418777C82CD97CB8AE26BF4C58C7CEA40E59E0E2547
	B5443F9DB89CF9A65AC86EE3D49D0EBC935D114B4EC4DDD0B777B0534F63A47E4DA9045822106156901C834ADD9CF73B85F1EEA8CF6038568E22DBA84F66388788EE811417F35C01BCE6832BB8AE0134F39CB199AF9EE13899E6460B1F637EE8E63CCC64380F7398AF899C97623CA4F25CAFB8AF73B92E0A73320463DC1C1783B606DBDBC8F281D0EE67382DC7D9DF0563DE5A427AAE3031BCCBC9CFD8AC0D59943B3DA960F1550983CE6251683EF58F098BD1CFCBEDEC5FDAC86EDCE59601DF8228A9E2394950DB
	ACB71978408C41B9A190737E5D722E8B066EA2EAAFB70AF479AF1E1BB49630B167C2FDF29162CA433E8360DAEFC8236786DA6F8A2B47FD9C77822D7E6BE175486FAB86FD7D4230FA14518935BF1D5B5387595CD6BFBDDB70BE569377EEE1E531FA37CDFD7A0FCCCE956E7F22C10D957A63448A44D35379BF634679639EB12D186970A03131C5D0CE66380F894ED882FE340461DEA0389AA8F7F1DC220559D80863263E4D6CE48A4795933BDB8665D5A54C8FCEEA45EF9A99252CFCA7A9633785DA5E5DBAD6DF05B4
	BE1D2FAA9B739863097D75FD368B36B6FC97079439C38AFABE14B04BG7BC9A97956CE768C3483EBA95B07EFE9C37D7B83D23F9488C8AE7AE9DEEDFFAFE9BF935EE783503DFDD78E5B5B1781AE995B1501BF83E8AFE372FF1B4408FEA8CF6438E492734F0272BC0E5B4EFD4BF20E5B5347F43C0A63A667B29DE714B35C1CF6668B3A4B198E5309BF76C2F99240CC0E5FCA70785D873F91E14E1D623FB1627E44E8C4623DB1F071F5BEFB44B1520568335BF874CFF5788332D686FD52206C7374D15DA59159FE89E0
	FEB95335A18FEDDE01100B3631127534986A1771B6EF1C4775BAA806BC03C303449AE85B39246DAAE8A7D4303637F7206D5064BC2636C41F0B5B4EF08723DD485B1776453735E7683EGED5B816E2DA0E732BC47A8F357438D680300E7D874E7D3AB34B31EBA37ADE4EE9EDF445CFEC446CE027649G9437184874E1001C238C17CDE4DA8865CA0EFB10601CD06E6638D99D2C5DC40EDBC3F089D01EF9144D2FF34479250FB23F45C7357951B13EC246D8867895C7194C2D05B8E737E7406D85D060A4C9637B7AF2
	360E330E010E0ED11BD60E516F1E14BF6B517170B76518469F56CD0772E3E17CED4BC47E9281BF7798DB035F9C710EF6AC2031D7953DC7FDAC72FC5FC0704947A24F77BF3732737DEA286B29E479341F1EC1B9AC0072F20094004C63604FG1C47D98E606E53F89FE2447CED176CF7CA57A42FAE35C56737FEDC79FB843ACF7FE89D5B27677C705B425C5BAC713F0F9ED73F0F2A4F0C4249297824733BF09667B79B6A9681A483E458CD469C00F2BB1B5F0B278B697C7073EE2D2208FDEE726518CD8EBF66EAE8BC
	6829775E5C7A39B55B55F8C1FD7A61B991A0412E79CBEA13D5E67C7D49B5A920F87AC4AFCD90550901F6596B693B2E5A4AF9327E70DBE8221D5972574CB8A7379C70FCA87B14681E4C4E3EC77AE24EFDA56F1BEC46B3EE2F9CA049943A86566531B5D0FC8276B70009G89A7545FD490DA4F933FB79BF073CE30F571605B237925F45D7AD8817DD61F206B021C752A28EDBE41655114975BAFDD266785E8EF2DE27378565FB1A60C3AB544E9FD50063F1550FF6523EDED356FF435DD6AE868ECF93761D22D53F929
	3E4D794E5956CEE6EF5307755F74AAB7FC90771BDED795150FDBFDAE7BE0F26AF4F807A837CB60FC7CE5CF9EBCFD523005FF64F4F840FC700236353472D8E1597132FCB59FF8AA074549C9D5ACCE9E4C090C13370745495F4C090C13D106DA7D33B911F1722930B8B9ADB7B2CECED57BD37E5DC5517672C99133171C1354DE4A71F9124A0176DBE70E4E25184D1139149E68231C545B99BDABCD81DC42C936E71CA1315092A8AFBF49ECEFC66EF8EC4FD4EDB2DA2AB55BAB2F26367747B5EB5650B17E1B44D1B500
	F7D6B39B23793D30DBFCFCFEE1F5743BBA2FFB6CAA5ED152E5FC0965513A3F6601780523474052082D8867B4DB793285F5370FD23359EC7B54B67B4FEBED6ABBC63027125262C2CBE9C53E5AF6FAC7ECFA1D5D9A3DDDF9E32117274EB9C3507ED1DA0F135F455135732209796A1C6F5CA7B8FEE9B16C0F33F6C255D4678B2B357322307A9E9325F970BD0D3F07F247BE5F174DA85B4A230CB777C50D4EF5E2D77B289677517A496FEB750363106B339D513496FC2051AA9913CCE93F17AFE9323CDD54E8540C0986
	5D532F1433FC19F08AE232D3ACA770732B47056833294D6F957BED3DE25053E737576183130F6F349CA06F4D3A88C990E8FE60038A246F873A13G2683C45BC372A9BA076F77EA73F93ECF1B4FC76E1166D30173A949D74F8AF5F815077D1ECA773E44FA7373E82F5C54B1E81C714C19785FD2FEF1A6763714E5E7E2FFCF7C6D19116F307F61CC7CBB6C2F1F515FE127F1527DAE5C8F66020E17G24G18EA40F7563098699367BE0144C8B162238657GFE4B41DFC5A9EC7C5E3371676AB9BB7AEF2982E7713EDE
	2F8CEEF69069B3FC68AC6ACB117D8429FE27BD0BB28A20755226AA7E1633E827DE291F20559C9F4949D4413C1CB5BC37F89EF1BDD076F35CCB675959FCA24735195919BB0163DE8B71EF2BB5EC7F7019713BE0D88C226D8936F38D9DDDAD75354ECB1D759DEDCEE7DDED8744A367EBAFF472B53350FCF4047E4DED9DAD6F3635F605D3683C545A5060E8F030F83FA80F79065540D7F9AD738DCFECA89646F88F304B23F825BA928150B8E5B80F5DAB7456329C62C6F23FEDA6146781AC8120BE2032DF7381373EED
	096F834ACACA8AADE545957931FCCBFCFF734D81AC5177A75D5AD82F1F9D4BD85466E58F52DD89BCE754B1BB4FEABD8C4FF36265689B1F00EC8F48FE885B2FD3AC39E52C6E014F3C270F9FB36FE9B63073D724AF3056513D2F3CAE7277EF0E1C711E15282E9FACE23AF6834D69F56C3E49EBC56C4E6652BA262FB6121B4F2C07189A20261E35F518D98C5ADD4F5A86E85E884A33G6657F35DDE5164ED6A334517F7E9C97931724A32236547504D3377FC2637773C9A1D8939A3692D349C74F67458B8ED6463C14D
	C63E655346720DE9ACEAA3374B98EFAE05392F8248F64059984081F091408FB091A081E0B640C200E5G49G468613B18B408AD055406CCBF8B7FF847BD29377441986C4FA0F055A46210A51EC233DA1569D1D0AD17BC55E9B22F90467485E6FED084CAF7C0A60C38D117985D7854BAFCC043A95FC3E8F3B626F83F3DC236F83CF3848DDEE705FDD577D6A777DA7DD64CE8FB97931F67F6E4A643816E03B9C74E0D6CE4D7BF9B0606E87BF276631483AF7B61A0C5D0D4CE7CB6743F3039F917FBF896A27B5327348
	36EEF2960672BC0E8B11F533824A2B9A5999E56D754F123733B4190C6C1B042A4328BB01E353FD7D287DA2F5CF7B5D98351F9E8A1B586F39BFA1FBC1CDD3244DCCA2F8E7D3244DEC35B01B69063AF9CDBC5670A0DD9E5BD0B72B6A79B68F46A978DBBA0F6875FCC82E7DF7C2C35FGDD2FFF461FCD2BEDCD178F46DBEB90351C06355F56FBD96AD33A4481BE77070BBE1D2C9F9F35DFECD92FA27DAC4DE34B2917B7B3D97F1878716666C8D9AFA2782E66C8D93F436F7774C05D02E616FFECB1FF3A7C636F68EF7D
	DBA247FF0D604BDBA2478F195978B5D0B73305697ABEB93EFF7836BC3AFFF8D40E9DA77EC00E0E937FCD0E9DA7AE1075F1A245BDA5633EB9G7E381B377BB947DD60387FA4B491F75E63D25C9C7F82618B714D928E48703FA1FC8D93DC370ACB5261AE30FBD2B27AB81832DB8E303E2F4A440FF93CDE751E94E96371518498457D057068710BBA3ED7F39C72CD6F3A4D5F3E097475FAFCD2BD7D6B845CA61762D59E757DC55D7B650A1BFF8E3B65260EC1E3011338D52831C05DF9BC570445820D30D1D87739403F
	48837B8AADF0C4ADA967B1CB4FD62D6F3273C8E89F3B16E341FBB9F6B5AFF04BB62D5D2A5A114E4045E5B040F11697F8E03B56AFA9B476FECF21DBD46F0A5EC562740C53A6E34EE9364EEF35E8FA7C2A6EFD2765666B3478260EC1673C31185C910279561CE6E733DF123CEA97145DDC8ECF696EGBD27FBFF6FF3382B23FC8EE32CB4EBF37A336E7D634F7CB78AC69F7B9BB7F58C3AA63E1D073ECC412329405664CA8C1D547AFD456BD9BD2EE30165176D44BE9430038927D94C77EACEB42DAD78E9C6FD7F7DAE
	0D6E53B978773EE27809DA58103C60A660E4D9D20A31BD7D5BE2AF9E798408A73EA224878342366875DB7B5597FE19F84911E5908CAD42DAB7717DADC27AB0EEA2A108E1D23F8C1027886BF996B7F8D5789879A1891BD0C4A9A0EC61E334108DE17935B2FB5ACB77259984A38CC0B7489641083B9C0C93206325418609238938312087A1FF58A71C36A22C25994F919940D23ACA88F716C35104F89BF6D2C3D73A0537C1A5FE1D15D2CBG0E719E02CA99F5EB0C51C678E44D1AB5883CCE86F0707E38976FC60EF0
	7408B681E1D318CAFF722D0A5FAD7A7539E01A3036C558052706CEA9C002CD49051745553F3253A82C776385D47CDE85F28D12C7FAD0396E151CC2D6FF877E9AB928C82EDA9646869561CD720B37A2CC2F94B50BC13702214C8927E074615F83923651EA2F689BA822B16D89221C545A9EA281DC8582703B1D700BD4EC1C4A7647379C5CFBBAE9EE1AE0E894B6F8E55155A876C1345BA22CD75C81A968163D2E107FD48DA114B14CAEA6258139BDFC7AB8D99BE605B953D7C27FBCE5530ACD0D8650DFC0A8A2AD71
	10E463ADDC75EEB1GC3C90196857F3E119CB8406B3EE0D65E7A0F261D978DC22ABAC994FDF6488FB655AF8EFA3C576105EE34C4A9B9E06F906C79027883C1604C9241992B6283586EFA2A78EB8F3D71AE99C06B2A88F9D4145035E8C012870BEA7B7BA1506184C02EC0BE171347B63608B65BC2F73CF97A79B53DE0878EA1ED3038187CDFC27EAF457FCB882604E0CACAC1384942087FA26BC3EC4E44F87892C6755B9AC1034AFF3D72540CDFD5A52F0459B58A6964F7FE2004CC2DD56B50E02F9438AD71EB399B
	27AD1F0CBA72232904102E85966E66705004AFB50CB186E2145255A54696E8A678CFC5B061F7D60B7A2115345BC10C125A92896B89F6F714F59991ED869DBB42F43C16063A24D800FD70E5AFA96EA77F3FB5EED1E2AFABA508FC11F2C5C84D4D65FF224F4D255CFAAA6E375D956D6A5FC2EC0F7C3D664492F2C773ED13316BEDF6F6F92A983F4F47BBDFF68E0ADE2FA2FA2F547ADCF8562C3B2EC8DAFE7E4BA5C8BF485A28156AEF05CA106F4B3ABE7F8FD0CB8788FEDA95D4279CGG8CD3GGD0CB818294G94
	G88G88G37F5B7B1FEDA95D4279CGG8CD3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG619CGGGG
**end of data**/
}
/**
 * Return the JButtonCreate property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonCreate() {
	if (ivjJButtonCreate == null) {
		try {
			ivjJButtonCreate = new javax.swing.JButton();
			ivjJButtonCreate.setName("JButtonCreate");
			ivjJButtonCreate.setMnemonic(67);
			ivjJButtonCreate.setText("Add");
			ivjJButtonCreate.setMaximumSize(new java.awt.Dimension(120, 25));
			ivjJButtonCreate.setActionCommand("Add");
			ivjJButtonCreate.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjJButtonCreate.setMinimumSize(new java.awt.Dimension(120, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCreate;
}
/**
 * Return the JButtonRemove property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonRemove() {
	if (ivjJButtonRemove == null) {
		try {
			ivjJButtonRemove = new javax.swing.JButton();
			ivjJButtonRemove.setName("JButtonRemove");
			ivjJButtonRemove.setMnemonic(86);
			ivjJButtonRemove.setText("Remove");
			ivjJButtonRemove.setMaximumSize(new java.awt.Dimension(120, 25));
			ivjJButtonRemove.setActionCommand("Remove");
			ivjJButtonRemove.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjJButtonRemove.setMinimumSize(new java.awt.Dimension(120, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonRemove;
}
/**
 * Return the StateGroupNameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelScheduleName() {
	if (ivjJLabelScheduleName == null) {
		try {
			ivjJLabelScheduleName = new javax.swing.JLabel();
			ivjJLabelScheduleName.setName("JLabelScheduleName");
			ivjJLabelScheduleName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelScheduleName.setText("Schedule Name:");
			ivjJLabelScheduleName.setMaximumSize(new java.awt.Dimension(103, 19));
			ivjJLabelScheduleName.setMinimumSize(new java.awt.Dimension(103, 19));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelScheduleName;
}
/**
 * Return the JPanelHoliday property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelTOU() {
	if (ivjJPanelTOU == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder.setTitle("Rate Offsets");
			ivjJPanelTOU = new javax.swing.JPanel();
			ivjJPanelTOU.setName("JPanelTOU");
			ivjJPanelTOU.setBorder(ivjLocalBorder);
			ivjJPanelTOU.setLayout(new java.awt.GridBagLayout());
			ivjJPanelTOU.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
			ivjJPanelTOU.setPreferredSize(new java.awt.Dimension(324, 367));
			ivjJPanelTOU.setMinimumSize(new java.awt.Dimension(324, 367));

			java.awt.GridBagConstraints constraintsJButtonRemove = new java.awt.GridBagConstraints();
			constraintsJButtonRemove.gridx = 2; constraintsJButtonRemove.gridy = 1;
			constraintsJButtonRemove.insets = new java.awt.Insets(24, 13, 4, 39);
			getJPanelTOU().add(getJButtonRemove(), constraintsJButtonRemove);

			java.awt.GridBagConstraints constraintsJButtonCreate = new java.awt.GridBagConstraints();
			constraintsJButtonCreate.gridx = 1; constraintsJButtonCreate.gridy = 1;
			constraintsJButtonCreate.insets = new java.awt.Insets(24, 20, 4, 12);
			getJPanelTOU().add(getJButtonCreate(), constraintsJButtonCreate);

			java.awt.GridBagConstraints constraintsJScrollPaneTable = new java.awt.GridBagConstraints();
			constraintsJScrollPaneTable.gridx = 1; constraintsJScrollPaneTable.gridy = 2;
			constraintsJScrollPaneTable.gridwidth = 2;
			constraintsJScrollPaneTable.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPaneTable.weightx = 1.0;
			constraintsJScrollPaneTable.weighty = 1.0;
			constraintsJScrollPaneTable.ipady = 94;
			constraintsJScrollPaneTable.insets = new java.awt.Insets(4, 4, 0, 9);
			getJPanelTOU().add(getJScrollPaneTable(), constraintsJScrollPaneTable);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelTOU;
}
/**
 * Return the JScrollPaneTable property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneTable() {
	if (ivjJScrollPaneTable == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitle("");
			ivjJScrollPaneTable = new javax.swing.JScrollPane();
			ivjJScrollPaneTable.setName("JScrollPaneTable");
			ivjJScrollPaneTable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			ivjJScrollPaneTable.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			ivjJScrollPaneTable.setBorder(ivjLocalBorder1);
			ivjJScrollPaneTable.setPreferredSize(new java.awt.Dimension(311, 305));
			ivjJScrollPaneTable.setMinimumSize(new java.awt.Dimension(311, 305));
			getJScrollPaneTable().setViewportView(getJTableRateOffsets());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneTable;
}
private RateOffsetTableModel getJTableModel() 
{
	if( !(getJTableRateOffsets().getModel() instanceof RateOffsetTableModel) )
		return new RateOffsetTableModel();
	else
		return (RateOffsetTableModel)getJTableRateOffsets().getModel();
}
/**
 * Return the JTableRateOffsets property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableRateOffsets() {
	if (ivjJTableRateOffsets == null) {
		try {
			ivjJTableRateOffsets = new javax.swing.JTable();
			ivjJTableRateOffsets.setName("JTableRateOffsets");
			getJScrollPaneTable().setColumnHeaderView(ivjJTableRateOffsets.getTableHeader());
			ivjJTableRateOffsets.setPreferredSize(new java.awt.Dimension(287,393));
			ivjJTableRateOffsets.setBounds(0, 5, 287, 393);
			// user code begin {1}
			ivjJTableRateOffsets.setAutoCreateColumnsFromModel(true);
			ivjJTableRateOffsets.setModel( getJTableModel() );
			ivjJTableRateOffsets.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
			ivjJTableRateOffsets.setPreferredSize(new java.awt.Dimension(385,5000));
			ivjJTableRateOffsets.setBounds(0, 0, 385, 5000);
			ivjJTableRateOffsets.setMaximumSize(new java.awt.Dimension(32767, 32767));
			ivjJTableRateOffsets.setPreferredScrollableViewportSize(new java.awt.Dimension(200, 8000));
			ivjJTableRateOffsets.setGridColor( java.awt.Color.black );
			ivjJTableRateOffsets.getSelectionModel().setSelectionMode( javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
			ivjJTableRateOffsets.setRowHeight(20);
			
			javax.swing.table.TableColumn switchRateColumn = getJTableRateOffsets().getColumnModel().getColumn(RateOffsetTableModel.SWITCH_RATE_COLUMN);
			javax.swing.table.TableColumn switchOffsetColumn = getJTableRateOffsets().getColumnModel().getColumn(RateOffsetTableModel.SWITCH_OFFSET_COLUMN);
				
			switchRateColumn.setPreferredWidth(20);
			switchOffsetColumn.setPreferredWidth(40);
		
			//create our editors/renderers for the fields
			ComboBoxTableRenderer rateBox = new ComboBoxTableRenderer();
			javax.swing.JComboBox rate = new javax.swing.JComboBox();
			rateBox.addItem("A");
			rateBox.addItem("B");
			rateBox.addItem("C");
			rateBox.addItem("D");
			rate.addItem("A");
			rate.addItem("B");
			rate.addItem("C");
			rate.addItem("D");
			JTextFieldTimeEntry timeField = new JTextFieldTimeEntry();
			rate.addKeyListener(new java.awt.event.KeyAdapter() 
			{
				public void keyTyped(java.awt.event.KeyEvent e) 
				{
					fireInputUpdate();
				};
			});
			timeField.addKeyListener(new java.awt.event.KeyAdapter() 
			{
				public void keyTyped(java.awt.event.KeyEvent e) 
				{
					fireInputUpdate();
				};
			});
		
			timeField.setHorizontalAlignment( javax.swing.JTextField.CENTER );
			javax.swing.DefaultCellEditor ed = new javax.swing.DefaultCellEditor(timeField);
			javax.swing.DefaultCellEditor ed2 = new javax.swing.DefaultCellEditor(rate);
			ed.setClickCountToStart(2);
			ed2.setClickCountToStart(1);
			switchOffsetColumn.setCellEditor( ed );
			switchRateColumn.setCellRenderer(rateBox);
			switchRateColumn.setCellEditor( ed2 );
			
			//create our renderer for the Integer field
			javax.swing.table.DefaultTableCellRenderer rend = new javax.swing.table.DefaultTableCellRenderer();
			rend.setHorizontalAlignment( timeField.getHorizontalAlignment() );
			switchOffsetColumn.setCellRenderer(rend);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableRateOffsets;
}
/**
 * Return the JTextFieldTOUScheduleName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldTOUScheduleName() {
	if (ivjJTextFieldTOUScheduleName == null) {
		try {
			ivjJTextFieldTOUScheduleName = new javax.swing.JTextField();
			ivjJTextFieldTOUScheduleName.setName("JTextFieldTOUScheduleName");
			ivjJTextFieldTOUScheduleName.setHighlighter(new javax.swing.plaf.basic.BasicTextUI.BasicHighlighter());
			ivjJTextFieldTOUScheduleName.setMinimumSize(new java.awt.Dimension(150, 21));
			// user code begin {1}
			ivjJTextFieldTOUScheduleName.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_TOU_SCHEDULE_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldTOUScheduleName;
}
/**
 * getValue method comment.
 */
public Object getValue(Object val) 
{
	/*if( getJTableRateOffsets().isEditing() )
		getJTableRateOffsets().getCellEditor().stopCellEditing();
		
	TOUSchedule tou = null;
	if( val != null )
		tou = (TOUSchedule)val;
	else
		tou = new TOUSchedule(
					com.cannontech.database.db.tou.TOUSchedule.getNextTOUScheduleID() );

	tou.setScheduleName( getJTextFieldTOUScheduleName().getText() )	;

	tou.getRateOffsetsVector().removeAllElements();
	for( int i = 0; i < getJTableModel().getRowCount(); i++ )
	{
		TOURateOffset ro = new TOURateOffset();
		ro.setTOUScheduleID(tou.getScheduleID());
		ro.setSwitchRate(getJTableModel().getSwitchRateAt(i));
		ro.setSwitchOffset(JTextFieldTimeEntry.getTimeTotalSeconds(getJTableModel().getSwitchOffsetAt(i)));
		tou.getRateOffsetsVector().addElement(ro);
	}

	return tou;*/
	return null;
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
	getJTableRateOffsets().addMouseListener(ivjEventHandler);
	getJButtonCreate().addActionListener(ivjEventHandler);
	getJButtonRemove().addActionListener(ivjEventHandler);
	getJTextFieldTOUScheduleName().addCaretListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("TOUScheduleBasePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(374, 418);

		java.awt.GridBagConstraints constraintsJTextFieldTOUScheduleName = new java.awt.GridBagConstraints();
		constraintsJTextFieldTOUScheduleName.gridx = 2; constraintsJTextFieldTOUScheduleName.gridy = 1;
		constraintsJTextFieldTOUScheduleName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldTOUScheduleName.weightx = 1.0;
		constraintsJTextFieldTOUScheduleName.ipadx = 90;
		constraintsJTextFieldTOUScheduleName.insets = new java.awt.Insets(15, 0, 8, 17);
		add(getJTextFieldTOUScheduleName(), constraintsJTextFieldTOUScheduleName);

		java.awt.GridBagConstraints constraintsJLabelScheduleName = new java.awt.GridBagConstraints();
		constraintsJLabelScheduleName.gridx = 1; constraintsJLabelScheduleName.gridy = 1;
		constraintsJLabelScheduleName.ipadx = 6;
		constraintsJLabelScheduleName.insets = new java.awt.Insets(16, 8, 9, 0);
		add(getJLabelScheduleName(), constraintsJLabelScheduleName);

		java.awt.GridBagConstraints constraintsJPanelTOU = new java.awt.GridBagConstraints();
		constraintsJPanelTOU.gridx = 1; constraintsJPanelTOU.gridy = 2;
		constraintsJPanelTOU.gridwidth = 2;
		constraintsJPanelTOU.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelTOU.weightx = 1.0;
		constraintsJPanelTOU.weighty = 1.0;
		constraintsJPanelTOU.ipady = -11;
		constraintsJPanelTOU.insets = new java.awt.Insets(8, 26, 10, 24);
		add(getJPanelTOU(), constraintsJPanelTOU);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 9:11:36 AM)
 * @return boolean
 */
public boolean isInputValid() 
{
	com.cannontech.common.gui.util.OkCancelPanel o
			= new com.cannontech.common.gui.util.OkCancelPanel();
	
	if( getJTextFieldTOUScheduleName().getText() == null
		 || ! (getJTextFieldTOUScheduleName().getText().length() > 0) )
	{
		return false;
	}

	if(getJTableModel().getRowCount() < 1)
	{
		setErrorString("A TOU Schedule must have at least one rate offset defined.");
		return false;
	}
	/*for( int i = 0; i < getJTableModel().getRowCount(); i++ )
	{
		TOURateOffset d = getJTableModel().getRowAt(i);
				
	}*/
			
	return true;

}
/**
 * Comment
 */
public void jButtonCreate_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getJTableModel().addRowValue("A", JTextFieldTimeEntry.setTimeTextForField(new Integer(0)));	
	repaint();
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void jButtonRemove_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJTableRateOffsets().isEditing() )
		getJTableRateOffsets().getCellEditor().stopCellEditing();
	int[] selectedRows = getJTableRateOffsets().getSelectedRows();
	
	for(int u = selectedRows.length - 1; u >= 0; u--)
	{
		getJTableModel().removeRowValue(selectedRows[u]);
	}
	
	repaint();
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void jTableRateOffsets_MousePressed(java.awt.event.MouseEvent event) 
{

	int rowLocation = getJTableRateOffsets().rowAtPoint( event.getPoint() );
	
	getJTableRateOffsets().getSelectionModel().setSelectionInterval(
			 		rowLocation, rowLocation );

	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		TOUScheduleBasePanel aTOUScheduleBasePanel;
		aTOUScheduleBasePanel = new TOUScheduleBasePanel();
		frame.setContentPane(aTOUScheduleBasePanel);
		frame.setSize(aTOUScheduleBasePanel.getSize());
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
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseClicked(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseEntered(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseExited(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mousePressed(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTableRateOffsets()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseReleased(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * setValue method comment.
 */
public void setValue(Object val) 
{
	/*TOUSchedule tou = null;
	
	if( val != null )
	{
		tou = (TOUSchedule)val;

		getJTextFieldTOUScheduleName().setText( tou.getScheduleName() );

		for( int i = 0; i < tou.getRateOffsetsVector().size(); i++ )
		{		
			TOURateOffset ro = (TOURateOffset)tou.getRateOffsetsVector().get(i);
			getJTableModel().addRowValue(ro.getSwitchRate(), JTextFieldTimeEntry.setTimeTextForField(ro.getSwitchOffset()));
		}
	}
	else
	{
		//make sure that afledgling TOU Schedule has at least one RateOffset
		getJTableModel().addRowValue("A", JTextFieldTimeEntry.setTimeTextForField(new Integer(0)));	
	}*/
}
}
