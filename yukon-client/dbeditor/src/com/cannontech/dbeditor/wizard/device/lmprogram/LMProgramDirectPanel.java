package com.cannontech.dbeditor.wizard.device.lmprogram;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.database.data.device.lm.LMProgramDirect;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.database.db.device.lm.IlmDefines;

public class LMProgramDirectPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.DataInputPanelListener, java.awt.event.ActionListener, java.awt.event.ItemListener {
	private javax.swing.JButton ivjJButtonCreate = null;
	private javax.swing.JButton ivjJButtonDelete = null;
	private DirectModifyGearPanel ivjDirectModifyGearPanel = null;
	private javax.swing.JComboBox ivjJComboBoxGear = null;
	private javax.swing.JLabel ivjJLabelGear = null;
	private javax.swing.JPanel ivjJPanelButtons = null;
	private java.awt.FlowLayout ivjJPanelButtonsFlowLayout = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == LMProgramDirectPanel.this.getJButtonDelete()) 
				connEtoC4(e);
			if (e.getSource() == LMProgramDirectPanel.this.getJButtonCreate()) 
				connEtoC5(e);
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == LMProgramDirectPanel.this.getJComboBoxGear()) 
				connEtoC1(e);
		};
	};
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMProgramDirectPanel() {
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
	if (e.getSource() == getJButtonDelete()) 
		connEtoC4(e);
	if (e.getSource() == getJButtonCreate()) 
		connEtoC5(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JComboBoxGear.item.itemStateChanged(java.awt.event.ItemEvent) --> LMProgramDirectPanel.jComboBoxGear_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxGear_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JButtonDelete.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramDirectPanel.jButtonDelete_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonDelete_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JButtonCreate.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramDirectPanel.jButtonCreate_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC5FFEFB0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBB8DD4D5D5564CDAB4399AD61F2D4FA61A4FEF0D29B3C3258D25A696B3E17903292909C979139A2596B6CE794DB0B3CC9E9ED0A8BFE2EAD1DA121A0A628394955194FCCF8325FA8A6293503870AE70647D795E851E19533777B9775CF7F95C8768AC473536771DFD76B9E71FFD7659FF77C2B6EE9D121A245BC8C8D2AA117F9DCEA2A445C0C8640D7F5B40F1E961A169C4756F8B0075642D4BA9704C01
	3E62F7C2523349B31752201D8F6D1895A1699F436F8D24634C602F60C700F5A468FBCE7CF4C76F6B045672F5BE206D1F76E540B38B2090F0B5GC342F17D87766548F8AF3433645F88C9BAC25A9EC71A93069C95BECE26BB86FDA6000CA7106609FDDB647EF040A1CDB4BC67BF4EE8D741F38D656E2E5292192E613EB0326A794D3ACCDCCBB5BF071AA758D82F7C4CA54328A012493C2EB3383E293D75F833532EEF969C8E27C3921B5B7416A651E2151CEEFD17758741ED51DB44CEEB33283759DDEEE72BDB3017
	16D6FDE3E2BF0DD63758AC198407E8A3E4995D03E8C82373439E20AD0DC25C82834AAD9FFE9B819AC671FD4C4ABCA673B928BD0CAC9A1695F09F9123987F73C77976117D6048641051B87FBCFAAEB8EE8B34EBG98AF5B751C171923B9AF41A710B6EDCCC8FAAEC049980EBFDB06F893345BG0246F09E3F5A45F9B45F98CB921A0E6B568560B1F48C63ADEA0C0F474824C65DA7D4F7F618717C93F15DA7C1EF814CGDE00D0004DD4E7DF2D5D834F72A64960345B1D0EF2176B244D627E46E991ED703B3494FAE4
	5CF931596936904274722AA1DF039F8E61CFF657C98436370B046B5108730F91C9AA5625EA6CED741C4FF49364B9E21FD449FFAC4A6D1C916516GF89C077CB07CE50A2F996B433333FA0D6A8236ED50B7F3AC1733B90B73D215B214E4971569BE5060A57459033A628846C3D5040F97A66377AE210C3D009F84B082E082C09CC04AB8AE633C0B877A12F1A55C8337C330D5F7B4398437F43243EA3308EED9560B7775A6EBADE0637EF44B6318DC428E284FE87E28931AF2199F36C317BB0E4923E41CCFAEEC6D5D
	7AC0EB1B4775540DAC658C9368DD310DE3E7182C60F5742E7C284233333D075253B6742D84E0744FD07A1A277CF5E4AA455B1E725711F3E5DCC70221EF99GBB5F4175E89F2B1E06BB8A9084908A90819083306CE9FE46524502FE5EA3136AFC5958FD660F60A9FAA537D06970C80223D92C955CD62149A6FA08CCF3480C3CB5F59BF7145E6FE6D863EB51A13A85496AF4406C02DD84E572B09A23F9BD5238C55ADBE3350B30005DC5783CD5E6F4FCB641A3DDF0D984C974D1307E9AB36AC427D08BB34284F85611
	A82F1FD11B1B863FF7C4F2D9CDE9C05C0F509E944965B2437CA1BC4F02F20B3652D223359959905C57390EDE24F7B68C68CDFF88C9CF78833F1BEF1B398E9AB3C7127CB947B5F590E1709F19BE0D7E233F8EB653396783BE91E04BB35C27AEB71865B5EF65EE7883726B0FC37E599ABA23D9564BFFE70DD48D9C4E4F64FD788A6E6F986C4B8C60FD064BFC6D25DC260BA7850F48356FBBB7A80EE8711DG237D208EFD0B58A9BAA42368929D967861114FBE2BEE0DCFAFDB1C0A4EEC24F834D386D09C0F027F34EE
	BD43439CEAAD677D5BE83FA0C9EEEBD307A4D60B12E4F5347A466F213CF83A19C00F3C5727AE60DE87429E43GA2143D0E2A5B20522FAA27333D43D503B7C7565393549FCF837AB8657C5FBB01B1D6DA94EF2715E15B57BF700479B64F8B676C1FEDFE783657680FCE31B5A6AAB6ED0DACE3061FC5713B01D79300D7114345B2FF1E78583E565B5CEF1EDCBD56C88E2067DA637B434786725B7A40BABD32DEDB27C755EB6B74387A5EF5F6FC3D164E323E68FA5C17334B213A83536B519EB6BB9D8E11DEC4E588E1
	B1725BB5388F2B43AADD34DADAC5295A7A03C8C8F4946AF2A83539C3607768A8EEA73B36F3BB39630707C954771FE846D3213FA951E50C176D63F81F1DE4BC3D50007AA05AC4BB884A606CF0C88C9FC36D390C77A4D35C47793F42313B4F1C6C10A42743883D1218AF503D685943A43ADB1CEE3BE859FDD5689474C2172427E721AF275D2770F7DE6E3EFD47F335027A7C2B6A193FAF771B32AA701CC773EA738EB0FD5AC0E62F6F4627819D609D61134DFC2BFC96F3BE7F5170G63936206A667C92777EBD1F067
	DBA5512E477F2AA5185C50A6B8DAFB32D9897DFDB2291E767BCA7F997D39D426C49E0B8E4836F7C5AD6AC79968CD8D00EDBC373D264B584FED2F6CE08CCE1B0D29B6744D9C0FFA7BCB63C615AE860FE7F9EC16527FEC9F7DEF876CE7FCFCF5A59B6F20F82DC30450890D7FD971BA0FFF2EFC6857D7AB58BAFCF1552EB2CC42EDAA3F4170D197346F44248B68AB5A15791F3F70BE4178084D496EF03800F33A5CD6BB38D9F404F2BEDE2BD76763B94F026D05FD1861E983E07A70F89D6ED56BAC0D4F619CA965BCDF
	8F06B10381A265F9921FE5B2E1F4518A5DC0E807BD67230BFECECD7763C9CE17BDA1A4FDB7C04584C697BC0151CD1B00F43FD7F26C8285B709626601E13EAB05678E5FE42B6B8496CF1126BD226BCDE4736F18681FD3EDB3633CC64057CC643AE62CD57B79F20F4759EC25A12DFCDE6F562AED9543A5542E05E70B556D017C4AA2FA79193FD70B3CE24CAB2365F3FBF4AF16EF7CE378D334B24143A8C3973D9FB5CE83045D9520B686451457DA45AE4265F073343A4E9039EE27AEBD009C5A647D8FD049A188F2F8
	4653032518BBEA55CC1AA9137A568E2B3EC332E2F0ADC86053DC9DF2F10455A322E87C03EBC7CF64F50781C9285BE4D2C8FA8EC0A1G4B017615E7483228GDC90G3BFF55675477AF82700BGD2A6F31D9DD43E9F1E37EEDB395C3357212C7E9D3B4FE4BEF4C3194C6334470612E3BFFD20D9B79930EE3DAEE7B213F559E41F4CF306FF2B4B87E0327833A20B902019B0194BE2EB1E16AC92211F6D65EC07EAAF37659B587A3FB949574F0E06BB994D573F2BCD3DFED0B40BEB432279D9E49870FE5E227F104FE1
	F872D1647D76DD862F716CD75B0028C5DF8634811151321D0F7637816F57215E46831ECC61B6607A4575AA9BF09EEE9D308496DB316566E4E61B6576DEF3AA374D0467CC06FA145FC618339020822091A0C8D96389F31F351AD35EDEEAB593153A4710A9F2DEBA451793C4436F79GABD5B8D617C824F7B265F9D617D823605768901F2B42B37D381752E37B98748DF91E4770874A7D6BAEA9D33B67A439FEEDB62E3C47384F7D5AC63FF1CCE756D5FF426D93AA06D76F6902783834D40E3B882FC59BF4EA5F57B0
	1569ED90BC87CD653553FB4E6119631D9DB11557949EECC07F14BDADA4BD9F40885088908CB084E042B4FEEEC91734ECF777F38BE438FDF9AA1345DFEFD30629A77B12E15C346E63583EEB735264B9922771F3FC3447DC7D63E1DC1FBC94CF57BA47D65BE66DF36456C066B5320D4781D55379993DFC99F141509EB41D1F4715CB192A3BEAF2DA9D12F2CF536BB0E6F23A2D2DD68747E5D3DC3353CD13A506BB5BCB9EF7BEC09EE78E1047DD296F3DB65192B04FE3F50479540F8E077DC581B0DC57A14445C0FB19
	022BA9C1DC468BA169BBDE60380EA3082B00F62302BB389FF19234C3DED0EAC6BA0CE307C0BB92A086E02552370E76212F59B80345E945B3F85F20A44C2911B6D0DE2C861C172F519FFA3646C83AC6FFEA81FCCCFF73FA044FA87D140F5651B372F8C48F3D2C985148FDCB25CDC4A3EF7ED6300B0467D2D74B5748B427A42721D8516DF773F234F51B86CACC2FEF6852647D19E770B8E93009561C209D869025487B3F4E7957B0E221AFFE8613A71929AD570D9A785D00BB86E0D275B1FD9C5690D8779FE950567D
	70863CF39EE7073BD974576F318DB9CA1F4F722173C6914AB98F9DAF896ED631477D18CC47333EC063E7B6843AC36CFC74A75567F3D0512D7ABCFF1BE581F9F8E51904E8480D4D37245BFCC54AFC635BFAFB6FB1C4E3BE467F424B68E33DCAFB89EDDFD756FBD70F72F1394596518D19AEAB4D10781928AB998528BF2361F754CC9E0B5DFB88ED1ACD386E6CG63B7F89652A59F66FE30905A95GAE0060D94C9FB2FDFD3C8B637596C87FC0FC55AE3119F8DF44316DA5FCEC9C50A7G64G3E84407CA2DFB36488
	0E0501D6FB071DF248782BABC57E2200EE1AC2DB351F528ADEBFDA8B35CD2B000E4C66346BCF635E590DE12E5DF72E89C7B04FBAD3A5B409B68C9AB97E26190ED147134A18FFEE437B4C52DB705256166B58AD3F5615737E4ABD6AD8B7FFB61311891EAC26882DD347949639BF921EB3E7733B9A24633A30ECB6BB7744592A58AB866CF4CCF7FDE0F1D776B93C7BA5B1AC6ECAD5701BA93ECA05E77158D0930F579B21EFC28C37730F5318E6779C3867F3B86E6F94E702F623027B7EBC62EC508ED6F0EDC791978A
	6D518A6E7ADA6EDF22955C0394978FED729247AD21380D504ED7F06EBC4ECB1902FBB20F73D22360BE5E4357E8D4F031ED9C978C381817705B05F6D4FF79BCED07F26169903BF2157C40E805DC9CDF9A6DBEF0E4FFFE6E010345F9ACBF9852254E8FCA566E8D189F644CE5E713AFBFD3D2B74B725EFB183FFF37C05F60397C6E94B5E23C2858FAE4243474EB3755F2D2E82D22B71159FB4F61B4F9FC4CDCEE6F9F22F3E64402CEG9C0B65B5D66FCE1E4B0F786DD0D295F4C2731B01C5B5BBF5B53172378F313EFC
	E6A66472EC4D7BCA781A2321BFBE163E27ACD16BB84AF2220E90BD1EA76D7FF337FE342F688FFD7D5B0F287B115F75496A7E8B5D7AD1C63B6471ECEFD33F64FBCB1C9ACEECE376857C9E2278E536A7337C8C1D7DA9892A7BCE47F461972DBEDD583BFFCBE0DDA84C4FAD4CE3E39EEB730DE9CF6A0F7E487E3CA500BF27960849BAE88F0F29873E4CF563570DE7496DFEFF22ED2958DAB36B149A4B4BDC17E69D27B516D740C683943E426F5183B44E280036C5415DA3A04E8B6D9085375A426FDB3802DBCAEDB819
	87F34E63766DA3EACB7221DD21603E20B82C8DE25D0F6142CCE619CE4BA7D684407797E43FEB6A6D1DE7FFE341C04074A41A6E4D6E90A14933B6B3FF75AA45CD81304CD37C55F92C53CA90A4B1DDDBE87569DA67F9ED3FF7AF3541924C91244C73535140BEBF145A6791C09B8190B78FF18357667439077035A8AF510165E60BBC576435BC2F4A1F4F577E5BDAEDDF3EBE9DE90F81DD85G437D8F3563F7A37F7339FDCEEA47BDB33E74FBFA63ABE2BE527C294D58876FEA8806B121G5B65339D813F718C7E31FA
	75EAE6DBE6542B6DFC762FB669502EE85996320069DF46827FBA503B6D382FB240B7AC6039E53838971E3542551E95DAC01A5CCE1768162EB3AC2D764B7AD9F478763E23B9F11B633C37BC0E45981BCA6837AE8BD80C112460BF277881AABC73053FD3FC61A068CBD84073729DF6BC73060590CB838C848883088608DB48E579B7311074D1B729F13A2AA091312952E6363FDF165C5E77C563BF60BEE774D30F10081F2FC88AD49BCFD84874A167D576ACFC71D30AE77B9B6B407DD5C01F85E0G40900051G53DE
	657BABEF49E77BC3F5A817A421390D3EA1601B43223D8F0D41B65B5BDDFB54FBDBA42F3FCAFE26AD6AE9AF5938B9F5FC5C9620A97423B3C25B8CE0D36159591E225FB185AD72BF73EFA9BEE4117F194FBD4E4FBC947A9297F11B7118AE303D1ACCFDCF63E2D8FFB127FFC8681923C4885CD60482DDB8C0ACC07CE2D66BE63662A331AFDB41EBG88A9CB40E7ADE136025A38A53E7DB0BE5EAB5536F59FD19BDB827446A51CE753B91AD7285F97A8736CF42B7AD87DDD697B4BB9FC6E326CA24CBE1A70B3A8396F72
	D12C138508BFCA0A72F3779F4CA3723808EB3EF10E357D994774BE7A32BA564A5D1B102495EBA5DC5E2EF3ADE17AB1E8094F9EE68C9E194C56DC38167AB6F7074878B9687171234B6B8FBF0C7609BCAA8F43AE321536C12F897F3772C4CF9C5FBFAD6549FBDA1C4177346551E144B8F7C55276GF7B9DA5ECB4EEB3EBD15401E58DDEE6A4059581DBD557EA589E05F61461E01FDFD57F49522319A21D59913500EF3AA3587E676653E556D988363B795D64186819ED6AE2A0150514A6E6CE6F4D3727D5D217ABD72
	706DFE2353FF9FB1690E2EA1FFDFE88FDCCBAA36EB5712CA6D5AF554B2FB6F5F43D45883FF8F731DDD7DBD8CBBF7E7A71E0D89F44286B090E0B8C044EB5CFEAF30EFA354FEEB586ED316D62CE561971392DFBF4A91F82F139DFDFFE7B95301350CA6A7188FBB4E4F71AFB9703CA4270BA299EE1E83E564363636C90CD446AFF650DCDEEC21E8963B8F2679C88C6CAB6EB59E4F3FC15F1FA7C0BBAD0E634E9AF95C3FD14145537C42826DA085F7A993F103209D2660E2337858E98A4ECA6BD2DBDE8FC9AFFE1D638C
	59B48701F6636BBCBFB8FB422777B52A5F8B0E5F79BB7017BB3A8633D9BAAC6B49F66FA781EDDB20DC7340A1B012C772E4BF3BC860FED682D90DF81D67128776F7FB6F456A8C27ED4E2EAA59154A39616EA614B50E09FB1D6706D5961CF34B9BF0A6GD5G7D333FABCF303DBC02A5C9F9DF53FFD06D4B541FFD31FB773B9B6863AD30F69040F000C80019G4B5E60F7AF715AEE92B84EE69B25774FADD89D921BB77DA6468509B0C74E521074E3GB5GEDGC1G03G42GA281E2G628148B250F5005DGA5G
	D5G0D4B146FBB71B30100BC48059D9F934A9D6C6A8CEC877E5559379D383B13FECF827737663ACB2EEF07F45277D6ADAD9ED131B777F5E6AB38CA0F49693122F7E167B1EA8F7A699627C3E2314C94EA83A458DB50B29E4B7C5350BDCFDD7AAFBCCF96E3B599EFA59F8BD565E3282F6130C6262AFF83C92E8F9C9BEE29C75EA2E1CC34421BAB93775B64F4539781ACB63CBB1347062B00AEE3B944E5G66656CFD141CC7E67545F72A0AAF970C8DDE6E4BA3C36177C3BAC2B669B85FC1855A31E1A835ED4301BEFC
	B96739AEDBBB37F7D31B978BF4F14B394DFBC3383D3CEE55ED0E5B57545B386CB7C352F73C29D50B0E22762264CD7F5CE08A459B5F744F8D4EDB384DB2C15F68B7153C79C660BBB176C65FF7A26A0636EF1CF42327EF1CFAC35BB74E3A21760D8C3775E6E03EF65E6C1B2F021B5AFC9D3C59132F521B5AFC155D6C4957291B280F2DE0E34C8A5D9985F7C941FDC767C45CC52BC5EAD37097A8DF082F90F1C10E2F27FCF5D11CD946B52AF017E41CF8936F9FEC394D293C933FFA135A942B4D465A8ECAE3F50079D1
	646121BCDADD020AEF2F02C33E192DA94050CA3EE32F9830514DF71C864335FB1B85E2E6E76B367C5C427C5C22E2A587FAGC3F47977D3BBFD73FC1B196BBF0F3F8FBAF4A80F69696FB3795F6E643C9512EEFA0B7B56251F214D572A537ABEF62B31CAB67124EC1AE4DF7B57CCDEDF9F70967735DFE6619AD1500E85D879562D7A5A853BFC7BCC4F736D73C8F67FEA5C7ABD3EB1230F77C7B6ACC6B35238A1AD9E7CE8BC0F5BC2A8AE9F5A4762398D1BE4A10AFD19217AED365C79386D5AFFE00DB119FD59DAD33C
	56F7B04F75B94E7F3B98B6AE314FF1BD6CB635458FD2BD33457B5B6821946F0D773751FB33380D8E063EE5715CFF55B7617D6EEC95DC4CEFDDEB625FA5D42D60B443A8CD1BC233153EAB4077298DC0433E17085A003E5E6A30D99D22017D05183247DFE77974A4CC753BA86B4E1F6719BB3A86134F00A614EB3152BEE401D002949592427ECE7B7E7AEA90600895A45563A69F763C0B65773EBCF74477460DBA222BA46F37D11BDCC9D23B5038FBDDF0EACDG83BC57880D5558BF5EE2FD92F91F5525A432069521
	74FA3DB2GA365F33D365CE41D42D6A5356D0BBA8F14A63FB6ACD9C7D200A7666BAAC98ABAACE04D4DD8CC86DF078C921D3CFF340D194A5C2099C9A65A226F89E59A5425646EB492C945F7DC2540C7E6B7F7B0712103EFCCDBD86BC9A66FD712CFB0322D965D3428A2DA70E39D792F84CF13B5AE7C628B63AFE052C39F299E693ACD2CA29BDB4E6357789EC9341473B202C7A21B68072585E85D8BAA84CF1B04AE7EE895C9F1605FB3128FD937CDF03496305A4DD13A532472C64AB7D5E360F7836597E751AB33EC
	F8FA5D17DF1C090BC8A63A53E42D4DA9D8CE8B4D10C5D412B5D21BDB7434B9ED962F6BC4991DA82D0B3F4CCB0669B6AB5B474D6AEBA090D118EE777EEFC866324C53A0EC2F1B94D0CA8C64758A054550A638E1A951DDA9615FE7BA5DBB143E1FFDAE6D1C7E75478DBA12A4EF920FE03D57853A53A2582D366B28C45479524359842A84C11043039F9183E739FE1C71AEE5817DFFCFA93C7F613F575185FCC3A5321509920696340ACEFBC1F9CB8B9889E58210ABCC7F19B2BD52687DE8BE745E37694C1C55CD2087
	C612ECAFAC247F97517F77637FC594D3C4B1C57BC138ABC92F7F7C7B3DFC4FD4F9147B00472F07ACDB10DED9B1E5683CE3AB63E1F727C9AA7D46978EA15BC775ED073DC9F47FFC447D1F258FDA748BBCA3972A0A97677D442B5C02A4F658C95528C34AA5DE5B6D26705B04B7C597605A5391F2CDB46FA775FB2F043BB282FE5F60FDC77E46699D7F77DECB2E209AC683BE6E9D9E3F6F3F320F842E0BD65B851BCD92EC6D658E8B56C8CFDE17C4DFDDEE69951C5F43E964CE56777695647B2AEA4C7F83D0CB8788DF
	623D258998GGACC3GGD0CB818294G94G88G88GC5FFEFB0DF623D258998GGACC3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGC398GGGG
**end of data**/
}
/**
 * Return the DirectModifyGearPanel property value.
 * @return com.cannontech.dbeditor.wizard.device.lmprogram.DirectModifyGearPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private DirectModifyGearPanel getDirectModifyGearPanel() {
	if (ivjDirectModifyGearPanel == null) {
		try {
			ivjDirectModifyGearPanel = new com.cannontech.dbeditor.wizard.device.lmprogram.DirectModifyGearPanel();
			ivjDirectModifyGearPanel.setName("DirectModifyGearPanel");
			ivjDirectModifyGearPanel.setPreferredSize(new java.awt.Dimension(336, 266));
			ivjDirectModifyGearPanel.setMinimumSize(new java.awt.Dimension(0, 0));
			ivjDirectModifyGearPanel.setMaximumSize(new java.awt.Dimension(336, 266));
			// user code begin {1}

			ivjDirectModifyGearPanel.setVisible( false );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDirectModifyGearPanel;
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
			ivjJButtonCreate.setMnemonic('C');
			ivjJButtonCreate.setText("Create...");
			// user code begin {1}

			ivjJButtonCreate.setMnemonic('a');
			ivjJButtonCreate.setText("Add");
			
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
 * Return the JButtonDelete property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonDelete() {
	if (ivjJButtonDelete == null) {
		try {
			ivjJButtonDelete = new javax.swing.JButton();
			ivjJButtonDelete.setName("JButtonDelete");
			ivjJButtonDelete.setMnemonic('d');
			ivjJButtonDelete.setText("Delete");
			ivjJButtonDelete.setMaximumSize(new java.awt.Dimension(81, 25));
			ivjJButtonDelete.setPreferredSize(new java.awt.Dimension(81, 25));
			ivjJButtonDelete.setEnabled(true);
			ivjJButtonDelete.setMinimumSize(new java.awt.Dimension(81, 25));
			// user code begin {1}

			ivjJButtonDelete.setMnemonic('r');
			ivjJButtonDelete.setText("Remove");

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonDelete;
}
/**
 * Return the JComboBoxGear property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxGear() {
	if (ivjJComboBoxGear == null) {
		try {
			ivjJComboBoxGear = new javax.swing.JComboBox();
			ivjJComboBoxGear.setName("JComboBoxGear");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxGear;
}
/**
 * Return the JLabelGear property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelGear() {
	if (ivjJLabelGear == null) {
		try {
			ivjJLabelGear = new javax.swing.JLabel();
			ivjJLabelGear.setName("JLabelGear");
			ivjJLabelGear.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelGear.setText("Gear:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelGear;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelButtons() {
	if (ivjJPanelButtons == null) {
		try {
			ivjJPanelButtons = new javax.swing.JPanel();
			ivjJPanelButtons.setName("JPanelButtons");
			ivjJPanelButtons.setPreferredSize(new java.awt.Dimension(336, 29));
			ivjJPanelButtons.setLayout(getJPanelButtonsFlowLayout());
			ivjJPanelButtons.setMinimumSize(new java.awt.Dimension(336, 29));
			getJPanelButtons().add(getJButtonDelete(), getJButtonDelete().getName());
			getJPanelButtons().add(getJButtonCreate(), getJButtonCreate().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelButtons;
}
/**
 * Return the JPanelButtonsFlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanelButtonsFlowLayout() {
	java.awt.FlowLayout ivjJPanelButtonsFlowLayout = null;
	try {
		/* Create part */
		ivjJPanelButtonsFlowLayout = new java.awt.FlowLayout();
		ivjJPanelButtonsFlowLayout.setVgap(4);
		ivjJPanelButtonsFlowLayout.setHgap(10);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanelButtonsFlowLayout;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	//this stores any changes that are made to the current Gear
	if( getJComboBoxGear().getSelectedItem() != null )
	{
		int index = getJComboBoxGear().getSelectedIndex();
		getJComboBoxGear().insertItemAt(getDirectModifyGearPanel().getValue( getJComboBoxGear().getSelectedItem() ), index);
		getJComboBoxGear().removeItemAt(index+1);
	}
		 
		
	LMProgramDirect program = (LMProgramDirect)o;

	program.getLmProgramDirectGearVector().removeAllElements();
	for( int i = 0; i < getJComboBoxGear().getItemCount(); i++ )
	{
		com.cannontech.database.db.device.lm.LMProgramDirectGear gear = (com.cannontech.database.db.device.lm.LMProgramDirectGear)getJComboBoxGear().getItemAt(i);
		gear.setGearNumber( new Integer(i+1) );

		program.getLmProgramDirectGearVector().add( gear );
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
 * Insert the method's description here.
 * Creation date: (3/13/2002 10:24:00 AM)
 * @return boolean
 */
public boolean hasLatchingGear() 
{
	for( int i = 0; i < getJComboBoxGear().getItemCount(); i++ )
	{
		if( getDirectModifyGearPanel().getGearType().equalsIgnoreCase(LMProgramDirectGear.CONTROL_LATCHING)
			 ||
			 ((LMProgramDirectGear)getJComboBoxGear().getItemAt(i)).getControlMethod().equalsIgnoreCase(
			  	 			LMProgramDirectGear.CONTROL_LATCHING) )
		{
			return true;
		}
	}

	return false;	
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	getDirectModifyGearPanel().addDataInputPanelListener( this );

	// user code end
	getJButtonDelete().addActionListener(ivjEventHandler);
	getJButtonCreate().addActionListener(ivjEventHandler);
	getJComboBoxGear().addItemListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMProgramDirectPanel");
		setPreferredSize(new java.awt.Dimension(392, 354));
		setLayout(new java.awt.GridBagLayout());
		setSize(392, 354);
		setMinimumSize(new java.awt.Dimension(0, 0));
		setMaximumSize(new java.awt.Dimension(392, 354));

		java.awt.GridBagConstraints constraintsJPanelButtons = new java.awt.GridBagConstraints();
		constraintsJPanelButtons.gridx = 1; constraintsJPanelButtons.gridy = 3;
		constraintsJPanelButtons.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelButtons.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelButtons.weightx = 1.0;
		constraintsJPanelButtons.weighty = 1.0;
		constraintsJPanelButtons.insets = new java.awt.Insets(3, 52, 17, 4);
		add(getJPanelButtons(), constraintsJPanelButtons);

		java.awt.GridBagConstraints constraintsJLabelGear = new java.awt.GridBagConstraints();
		constraintsJLabelGear.gridx = 1; constraintsJLabelGear.gridy = 1;
		constraintsJLabelGear.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelGear.ipadx = 6;
		constraintsJLabelGear.ipady = 1;
		constraintsJLabelGear.insets = new java.awt.Insets(14, 11, 1, 339);
		add(getJLabelGear(), constraintsJLabelGear);

		java.awt.GridBagConstraints constraintsJComboBoxGear = new java.awt.GridBagConstraints();
		constraintsJComboBoxGear.gridx = 1; constraintsJComboBoxGear.gridy = 1;
		constraintsJComboBoxGear.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxGear.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxGear.weightx = 1.0;
		constraintsJComboBoxGear.ipadx = 100;
		constraintsJComboBoxGear.ipady = -3;
		constraintsJComboBoxGear.insets = new java.awt.Insets(13, 52, 2, 114);
		add(getJComboBoxGear(), constraintsJComboBoxGear);

		java.awt.GridBagConstraints constraintsDirectModifyGearPanel = new java.awt.GridBagConstraints();
		constraintsDirectModifyGearPanel.gridx = 1; constraintsDirectModifyGearPanel.gridy = 2;
		constraintsDirectModifyGearPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDirectModifyGearPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDirectModifyGearPanel.weightx = 1.0;
		constraintsDirectModifyGearPanel.weighty = 1.0;
		constraintsDirectModifyGearPanel.ipadx = 336;
		constraintsDirectModifyGearPanel.ipady = 266;
		constraintsDirectModifyGearPanel.insets = new java.awt.Insets(2, 52, 2, 4);
		add(getDirectModifyGearPanel(), constraintsDirectModifyGearPanel);
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
public void inputUpdate(PropertyPanelEvent event)
{
	
	if( getJComboBoxGear().getSelectedItem() != null 
		 && !getDirectModifyGearPanel().getGearType().equalsIgnoreCase(
			 ((LMProgramDirectGear)getJComboBoxGear().getSelectedItem()).getControlMethod()) )
	{
		int index = getJComboBoxGear().getSelectedIndex();
		getJComboBoxGear().insertItemAt(getDirectModifyGearPanel().getValue( getJComboBoxGear().getSelectedItem() ), index);
		getJComboBoxGear().removeItemAt(index+1);
	}

	fireInputUpdate();
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJComboBoxGear().getSelectedItem() != null
		 && !getDirectModifyGearPanel().isInputValid() )
	{
		setErrorString( getDirectModifyGearPanel().getErrorString() );
		return false;
	}

	if( getJComboBoxGear().getItemCount() <= 0 )
	{
		setErrorString("Every Direct Program must contain 1 or more gears");
		return false;
	}

	if( getJComboBoxGear().getItemCount() > 1 
		 && hasLatchingGear() )
	{
		setErrorString("A latching gear can not exist alongside other gear types.");
		return false;
	}

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
	if (e.getSource() == getJComboBoxGear()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void jButtonCreate_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	//if there were any changes to the current selected gear, we must store them
	if( getJComboBoxGear().getSelectedItem() != null )
		getDirectModifyGearPanel().getValue( getJComboBoxGear().getSelectedItem() );


	//only allow the user to define at most MAX_GEAR_COUNT Gears
	if( getJComboBoxGear().getItemCount() >= IlmDefines.MAX_GEAR_COUNT )
	{
		javax.swing.JOptionPane.showMessageDialog(
			this, 
			"A Direct Program is only allowed " + IlmDefines.MAX_GEAR_COUNT + 
			" or less gears to be defined for it.",
			"Gear Limit Exceeded",
			javax.swing.JOptionPane.INFORMATION_MESSAGE );

		return;
	}
	else if( hasLatchingGear() )
	{
		javax.swing.JOptionPane.showMessageDialog(
			this, 
			"Only 1 latching gear is allowed for a Direct Program.",
			"Latching Gear Limit Exceeded",
			javax.swing.JOptionPane.INFORMATION_MESSAGE );

		return;
	}


	DirectModifyGearPanel p = new DirectModifyGearPanel();
	com.cannontech.common.gui.util.OkCancelDialog d = new com.cannontech.common.gui.util.OkCancelDialog(
		com.cannontech.common.util.CtiUtilities.getParentFrame(this), "Gear Creation", true, p );

	d.setSize( 400, 385 );
	//d.pack();
	d.setLocationRelativeTo( this );
	d.show();

	if( d.getButtonPressed() == d.OK_PRESSED )
	{
		LMProgramDirectGear g = (LMProgramDirectGear)p.getValue(null);

		if( getJComboBoxGear().getItemCount() >= 1 
			 && g.getControlMethod().equalsIgnoreCase(LMProgramDirectGear.CONTROL_LATCHING) )
		{
			javax.swing.JOptionPane.showMessageDialog(
				this, 
				"A latching gear can not exist alongside other gear types.",
				"Latching Gear Exception",
				javax.swing.JOptionPane.INFORMATION_MESSAGE );
		}
		else
		{
			//set the gear number for the newly created gear
			g.setGearNumber( new Integer(getJComboBoxGear().getItemCount()+1) );
			
			getJComboBoxGear().addItem( g );
			getJComboBoxGear().setSelectedItem( g );

			fireInputUpdate();

			//we may need to show/hide the DirectModifyGearPanel panel
			getDirectModifyGearPanel().setVisible( getJComboBoxGear().getItemCount() > 0 );
		}
	}	


	d.dispose();	
	return;





	
/*	final javax.swing.JDialog dialog = new javax.swing.JDialog( com.cannontech.common.util.CtiUtilities.getParentFrame(this) );
	
	DirectGearPanel panel = new DirectGearPanel()
	{
		public void disposePanel()
		{
			dialog.dispose();
		}
	};

	dialog.setTitle("Create Gear");
	dialog.setModal(true);
	dialog.setContentPane( panel );
	panel.setUsedGearNames( getJTableModel().getAllGearNames() );
	//dialog.setSize(460, 400);
	dialog.pack();
	dialog.setLocationRelativeTo(this);
	dialog.show();

	if( panel.getButtonPressed() == DirectGearPanel.PRESSED_OK )
	{
		//add the newly created gear to the JTable
		getJTableModel().addRow( panel.getNewGear() );

		//tell the listeners our panel has changed
		fireInputUpdate();

		//only allow up to MAX_GEAR_COUNT gears per program
		getJButtonCreate().setEnabled( getJTableModel().getRowCount() < MAX_GEAR_COUNT );		
	}

	return;
*/
}
/**
 * Comment
 */
public void jButtonDelete_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJComboBoxGear().getSelectedItem() != null )
	{
		getJComboBoxGear().removeItemAt(
				getJComboBoxGear().getSelectedIndex() );

		//tell the listeners our panel has changed
		fireInputUpdate();

		//we may need to show/hide the DirectModifyGearPanel panel
		getDirectModifyGearPanel().setVisible( getJComboBoxGear().getItemCount() > 0 );
	}
	else
		javax.swing.JOptionPane.showMessageDialog(
			this,
			"A gear must be selected in the Gear drop down box.",
			"Unable to Delete Gear",
			javax.swing.JOptionPane.INFORMATION_MESSAGE );
	

	return;
}
/**
 * Comment
 */
public void jComboBoxGear_ItemStateChanged(java.awt.event.ItemEvent itemEvent) 
{
	if( itemEvent != null )
	{
		if( itemEvent.getStateChange() == itemEvent.DESELECTED )
			getDirectModifyGearPanel().getValue( itemEvent.getItem() );


		if( itemEvent.getStateChange() == itemEvent.SELECTED )
		{
			getDirectModifyGearPanel().setValue( itemEvent.getItem() );
		}
		
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
		LMProgramBasePanel aLMProgramBasePanel;
		aLMProgramBasePanel = new LMProgramBasePanel();
		frame.setContentPane(aLMProgramBasePanel);
		frame.setSize(aLMProgramBasePanel.getSize());
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
	LMProgramDirect program = (LMProgramDirect)o;

	for( int i = 0; i < program.getLmProgramDirectGearVector().size(); i++ )
	{
		getJComboBoxGear().addItem(
				(com.cannontech.database.db.device.lm.LMProgramDirectGear)program.getLmProgramDirectGearVector().get(i) );
	}


	if( getJComboBoxGear().getItemCount() > 0 )
		getJComboBoxGear().setSelectedIndex( 0 );

	//we may need to show/hide the DirectModifyGearPanel panel
	getDirectModifyGearPanel().setVisible( getJComboBoxGear().getItemCount() > 0 );
}
}
