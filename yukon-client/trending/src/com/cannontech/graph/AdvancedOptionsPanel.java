package com.cannontech.graph;

import com.cannontech.graph.model.TrendProperties;

/**
 * Insert the type's description here.
 * Creation date: (4/11/2002 3:51:40 PM)
 * @author: 
 */
public class AdvancedOptionsPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
	private javax.swing.JDialog dialog = null;
	private TrendProperties trendProperties = null;
	private javax.swing.JButton ivjCancelButton = null;
	private javax.swing.JButton ivjOkButton = null;
	private javax.swing.JPanel ivjOkCancelButtonPanel = null;
	private java.awt.Panel ivjAdvancedOptionsPanel = null;
	private int CANCEL = 0;
	private int OK = 1;
	private int buttonPushed = CANCEL;
	private javax.swing.JPanel ivjDomainAxisPanel = null;
	private javax.swing.JLabel ivjDomainLabel = null;
	private javax.swing.JLabel ivjDomainLabelLoadDuration = null;
	private javax.swing.JTextField ivjDomainLabelLoadDurationTextField = null;
	private javax.swing.JTextField ivjDomainLabelTextField = null;
	private javax.swing.JLabel ivjLeftRangeLabel = null;
	private javax.swing.JTextField ivjLeftRangeTextField = null;
	private javax.swing.JPanel ivjRangeAxisPanel = null;
	private javax.swing.JLabel ivjRightRangeLabel = null;
	private javax.swing.JTextField ivjRightRangeTextField = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	/**
	 * AdvancedOptionsPanel constructor comment.
	 */
	public AdvancedOptionsPanel() {
		super();
		initialize();
	}
	/**
	 * AdvancedOptionsPanel constructor comment.
	 */
	public AdvancedOptionsPanel(com.cannontech.graph.model.TrendProperties props) {
		super();
		initialize();
		setValue(props);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/17/2002 11:49:08 AM)
	 * @param source java.awt.event.ActionEvent
	 */
	public void actionPerformed(java.awt.event.ActionEvent event)
	{
		if( event.getSource() == getOkButton())
		{
			setButtonPushed(OK);
			exit();
		}				
		else if( event.getSource() == getCancelButton())
		{
			setButtonPushed(CANCEL);
			exit();
		}
	
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/3/2002 4:00:35 PM)
	 */
	public void exit() 
	{
		removeAll();
		setVisible(false);
		dialog.dispose();
	}
	/**
	 * Return the Panel1 property value.
	 * @return java.awt.Panel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.Panel getAdvancedOptionsPanel() {
	if (ivjAdvancedOptionsPanel == null) {
		try {
			ivjAdvancedOptionsPanel = new java.awt.Panel();
			ivjAdvancedOptionsPanel.setName("AdvancedOptionsPanel");
			ivjAdvancedOptionsPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsDomainAxisPanel = new java.awt.GridBagConstraints();
			constraintsDomainAxisPanel.gridx = 0; constraintsDomainAxisPanel.gridy = 0;
			constraintsDomainAxisPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsDomainAxisPanel.weightx = 1.0;
			constraintsDomainAxisPanel.weighty = 1.0;
			constraintsDomainAxisPanel.insets = new java.awt.Insets(5, 5, 5, 5);
			getAdvancedOptionsPanel().add(getDomainAxisPanel(), constraintsDomainAxisPanel);

			java.awt.GridBagConstraints constraintsRangeAxisPanel = new java.awt.GridBagConstraints();
			constraintsRangeAxisPanel.gridx = 0; constraintsRangeAxisPanel.gridy = 1;
			constraintsRangeAxisPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsRangeAxisPanel.weightx = 1.0;
			constraintsRangeAxisPanel.weighty = 1.0;
			constraintsRangeAxisPanel.insets = new java.awt.Insets(5, 5, 5, 5);
			getAdvancedOptionsPanel().add(getRangeAxisPanel(), constraintsRangeAxisPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdvancedOptionsPanel;
}
	/**
	 * 
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF6F7B0AFGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DD8D457156E4676337DC8EB927345E6CDE22BF1E94236A6213BEE56B6B4F137A6325B4946A6A41F5B0FA63675CBC9BE535856E45DFE4F8184919584A28AAAC4A02028C4B1A281959891F4841481079F0D8373E0C60619494CC386E212BD775EF76EFB3CF943DFE2793E733DF74FBD675CF34FBD775CF36F3B031039EFFE62C6E326A0ECCC94643F939B85212CC5909EEBF35C0938A5590694C17517
	8F10AEBCB21AG4F65D01766G90DE6FCF0672AAA8C70906141D701EA1846CB1E6F889514EE228DB7461F33F993F1D1D2D58CE92AD4FB32642F39BC0AC6092F699D2DAF61176A30D59B23E984A5B64F7829B0D209725C99A4ED6616748F44B6567425D04661746DC393EG4AD5G8100305D0A4C4DD43BEFD4D648656E3B668B6B1ECBB7EEA5ED2964A344493CE9B96C19A7FC0F9AA2DED8332B981ED5AD837D27FABD0307FBEDEE3747AD093D0E43E2406B71C91515265A16A1DF3F58E2F30B2E99B9C43FC34349
	14D7909A204CF4F9FEEB9576E9E03E903BA0A124AE4BE49D5628F4C9AEFCA3E1EEAE113D1E5A167095C379BAG53F3F54E1190FA767BECDEC7E5E50D7D264D5DAB5A1B3D12536376D3F585266F19F854F795486B5EC3F1B7C9BB1970DE8AE05A037D287EG7BE17D6C47427A1EF26376907D183FC79E3BBDCABF52DA070D3B280FAC3010F1CE58EBC8A9862882388EB083E0BEC08E754DEB2D8760D953A355F98687BD6E9A2F3756E577DD7458C5973CC317FAA499F7D96C75786C02407C6F68F0290EBE88CB760E
	75BD45DF2E8868AF9117978912F4401828533748DFE59823E59931FB153E31FE1DEEA77D927699D2F2814A814CGAE0030FD582F7056E393752B91464E673639DA07FA3CB61FD4BB64F459C51F5C3F3F9A476B1F9EB03E54782972B13B049FD35BE55593470DC9BAF6D9953E5B983E0F59A3EA1FE29756765910ED476E8B9E8F8289F9B4E6D0BFDC87B49BE59FE5781FD1FCC31E02E773231E62C9598AF5CB7350EF2D5B308F66047B05348FBF526D435C278F9A737219BE95795A314D311238E3877C8C0039G0B
	818CGF1G427BB83EEF379E19245F3628461671C6D85E03279810FC36C637DFA2D3365D66F35AFADC22DF10E9FEE2A13A770C61FB124E27DEE8631C6896FDB6B249C13AEDD084C772B31AE7AC690446A752DA0BF3D00486863D824ADDE14900274B66175A3CF61BA4AA942C7E858B3165CDDBBBC88481705E72BE31F9A30DF7F970DE8540EC5550C1F0F3209C416D525436831E974031C59744CDE7AFD143669B41769B5AC9AC91EF0AEE49A4FAC5379DDE7CF2DDF37BE645AEFD9E2EF39B4513B9D2870A7BB93E
	2BBD1D61C1065A4AD85FCB6BED1264F3768CC9E22BA8C9CEF73F42BFC0F5710F19FEFEC11E631D44472220DFB1GEBF87F5635E7287AE776F886063C96B2F2321D3AE91C5DG6F197BF12DFC72A6D9BBAB76231F3EE9C5BFB5EDFDD8A87DD5392E1F52783B1F7927F73FF62EAD23B215B64CB72DD31C77DA28582FBFC71558A2FE65B6624729E3F6FF3C0B58FDAE68928930F2BF5A7D4815BCB65FEAEDFE91E7D8338FC6CC34ABDE46E88B2D21FD2C542A6FE3C72D7ABED6E19D5F074ED875FC08551D33123545B3
	6CD67924593A034EE137DB24CAF396012D71EFDA08AD1CEE27F445E96F9725D667288863DCC0FC6BBA1D03F1E09321GFDE9F8BF7AD24168770528CF366B2EE7F39FBEEC34963071F595A83E44F43A58C1ECAE3A44C1B0D41DE748ADB1FCAB1D5FB2DE0EAFE7BB75E9AFF492FF9C90C7E43A664ECD8209A32EA11177ED31ED8B31314FB988C3C5A62C1C8F7A635579E09868C7F247E8F8468244D3F9A59519E47C47A6B875A476B13F79C3B751EBBD5027969AD28A8A51EFD6F46ED24DD759E16ABCAE97B3BF9F17
	A5DDE9E49C45CF07C4E800CC688B3CAFCFF56D5054354B7DE2753F68A27DF229E2C1D5A16957B04F91E95EC6F15F2138BBE1709AB7615AFBB1CD1D435A7D9F680EA1019905EC6C6694EA572D3CEBC4EEA46017713E57F62BE3D50D5F6F69F552E5C19E0F6FD5129CC5AF1F6C250B5A617EA16761A149C98239CD0279629D1254B9657F50D8C7E2C3CCA16614171348B824954198821CA9C2DD5E6A8E7625BFF7130DC61F536707DC48AE86501E9B3A09DD489AA523B1F7BB2A36D3D4ECCEC8BBD90BE4DFAF525A29
	6F932247AC404F633A8DDFCDD7598972CCD8127BE194D11F42EB714C3765F24935C474ED81E3632BC1394F62A2A51EAE0377182231711565B87FC16D95D7244DFD8C943F21C81B7BD813B077C97D4010627DG57626E6B24DFAB213C7A836C574F2EEDD5752B4563F4CB3CCF831F10796971B97B1DEE44F9A98E32EC9A3058FA768E9D63F5A0B3399876F89477E2A24115C25944F1FF59C4F08EA85FAA46B5A9BA094404593C7CA2ADAF6465EBC9632DA7046F2B2CB52C0D61F15B98EFBD1A8CB03B592E13B18A70
	32C84BA3327C8CA1ADE1937567914F908CC0F98931578789987316C3BFE38112811FDB427286B6BF9F9BA6F1258F029CACB92DDE31D7C8BBC0F85FC8C45EDBC0BF9BE0B10081E0ED89464BEDC9247FB71DFEA709C78E4AD718047148FAG695E7B18501D27537AC35C1FA72B63F144812613811E2B81189FB6A7609EF583604A8F224E179B088DE8CC39E13BE98B9C768F03721515351E00199900450E7B9212E5FE57C10C9D7FCEE5AE0472D2001587F10D8B9461FEB4E2517D02F946F15DFDF52CA55798F7D05E
	6317AA73FE4365E1A3EBB3E39336E9067A192584D733A9D3638FA70C02F0981E2C5EBF261E4409C3023A7E61B175C45F12F8F57DCBE36A090D8AE5FE5637950558378DAB428557CF7607BCB308167B34D6FEE6EF4991ACBDAF4433F12C22F3CB728D09CC6E7CE1F52C2CC8498F99AB0B8FB1F9D572F3E6D50E3CBE3CDD122DBB1667D567A0FFCACE167BA59D626338030CE3841417812CBA04733DADE1FAFB5A5EE9724D1BB29F0B3D77A5527D68A196FBB7F27CFC0A978EABF8B697D67039108AF55E4398132F75
	907B2D0472DA0064A30614BC00B3G5DC7B0C63FFE3DCC18E09FE971F84D104C3AD4DB9C563F6CCD535B6B7FBC89FDAE72499F88CB3E795A46ADA1FC43FB047944EC7939F8F38E45337E3D55CB7A9785F52BG04B2C3CAB6C0A900298C7BF754D64A7AF7566E14EAA449566B2099GF62E5DA98EABE89264186CCC1178159564C8DC56086DA0BC5BEDB7E4AB91119E9F158448961F47AB7A24910625991A6C876FA6EB6DB427CCD9DB424B025799B65EA7B34838AEADE3631D4871A60AB72870329FD03FA76555D0
	5770A17A7D83743C153DAFD23DBF1F70D577D3934F0157EFEB9BECDC7AFBC26F375CBD7A7BAD29C7FF3FB55AB37EFE6B6FFBC26F373E55235EEFB1BFBA5AC762EF840C45F200B8G61A82CE7C75127F6763E4F7CD347B74F42862C4EC3CFE8A4EC7F5D5E50FD5DE81F78DCA745CEF25E9E0FA4F98609FC44275949F8C99EAFC5B2DC161D5888D2B40744C8E5FC0E1D4C8D1758C7518C37CB5C26369BF3FF19BECFA4B6A0FBCA274D659C25DB00D6A0F477335CADFDB311D786B6B19D45FC6E1E9D1863B90E229F1F
	4EA038B0A8C7F05CF90A0B0672DA0EFBFE47576DF358264A2F3F76B648BA1AB8462FAB7BC20FF5CD5F44E3FD3E0F6EF9E00CACA3DEF91C1A7BE85E5D57679739CFD97A52B82E515F6271BB498831F53DF8F3F06ED81B0E79D966B10837G96G87G492DE79CA3B40F6C481E202F9942ABDBF57654F4EDE775F796107DE373C09D29F7558E014F3A597E0F650937B254EDAC175BDF834F8DG0C660E00B617A4B4196530BFAC47DC72C6BA2E1FE7G97A847DC72436B64AC16679F07ED43D2E565B91F53DEEB6BE7
	49A44BC19ECF473CEEF1B966A031D4669AA88B47E1DDBA0E3964722B6AFCAB6F601B9B75722D75D7F7994B0EB3FF339CD7F24940C343C6265B4B4519413A297222488C4C0BE69DC71D3EA952B3C5A847G2C85E0347F96C05A640F10B641CCE84FC0598A607D886716B7FDFA39509753647B17A97331B552C25742D99FE9F3282B94BF67A3ED8E75BB6E837321AE618466D0BF6FA77E630072CC00F000A80015GF1A7B0067F2E0FFC3F8B1597B0CF21F15CE703ADB06B573FE6E87BA5D4A8F9C0E6C5281C60DDBA
	0645955A1CA0310F602BAA34B9C11808B9C18B54C5D660B8C685346DE71FD45AACBD992A7DG730B135A766F20F84BC9ED7BA9E6EC5F81F551A7317DCEF2240579356ABDAE7D7667A409DFFB6CD6837309677AC3476E677BA70E5D2F746B2F53317D416B74EF7B755769BF742B57E9362E9AE8DCA82B04353692575A66AC3E56D6627888F46DD88265159C37056256C2B973E3445DAD6A591270284BCBAA27F716425AF8D4370DC4CDF93AE7A94C86AF5198D8817DB2FF0CF6E99FC53BF8F9FF3FB093DC9414E3B8
	6EDB9497876554AA442519C37998611D2EEF45FC2C4EC1CAC23671D54E1592D5B2B2045FE7842F7FFF656BEEE995DB7FCD70348210BCECC99521694BB249B45E2A607EE26C0D17E1A6504C8548124B91D5CAFCE6FA5CF31C4CA713E790C2E8CD40A95FADE0F58FF6911917ED6EFED12912F3046ABE6D7ACFF4942A41072BB1C778F61B9ECDA9541F2946F32AB78341F6C89CC1BEEBB53303971EB32BD9AE4A6CB0B80AF618D72DEF07F895AC861AE5G7B64F2CC35568E1F5712F82A4E08987E99BA571B8798AE6F
	94DD574D6A331F5532DE29D0476AD78C6B55BBCEE1DED41409EB62AD40C51E42FC411D0979CE5CA94C9766D15A02530614AAG4BE9243DAF8BF38B57E956AF1253919C7BDBFBDA8F3770B43983740E1B14D85E741BD44C1BF29F7D87417452EB9B778B7AE786EBCE4B5F564FA8F9137DA1BC039B6A43EF865EB3287B7D542661D00E8208B9033ABF5D0634EBB96D833B697ADB833E81E02DC1DB9D9AC1DBF98197DE03B206C650D631B5A8E381254D2EB5241481B454A26D0323E82B6B35D335D5AF7944AE1F1BED
	EFA5390B4E3999F38E28E36D7D510C6D4D2D457C7473FA759D22255BF30D214EE68C3572F9E02DE2E7F3AB5A59C863A961AD2B4318D81AB93D9C30F21AFC7EA97331DCA6AD0B0C0F23CE1BBB6E22F829CE1BBB7E004F1399D0371A77F7DE163A7D86930C3FE97C584F466017DD6A3353347B379831BDAD3DD716F74B247D4E746A27A46EAC86FCB440DA0064330614DC00F2008600EEGAF40AC0079G11G4B81D6814481887590A36BB147BD65A991C267387226C8C9F279980C7629EDD08C722A6A959B345407
	4AB33755911B5A6B192DB7F3FC8E454FD06159984C27F3181467C11DF08E476097ED6A766D008F1CD35A8CBB972A7D749CA2EF7EB9ED7BFBA9FE69B9ED7B8BF6E37BB1D0D755006D9F9B191E6F56CC13EFF44AFC2CDFBBC7E9FF9B34BE1FC771918DDA1F7FA10FF7CB20AE37917B3BE0D45D7E8C404FE99C5F67995FDF4CFAFA074B3CCBF5E4B0BF7F2E1F2CE186280F83C8B88F3AGD41DC73FFD54BB61BD383301F16E41A571FDD277F9C5872F7CBE931EF381A2D4F50C6FBD124A486F7BD46F611937FFFF7373
	5B5A864B0D1F206BFA54F966978C370462E2E49C9B1F9F11D0A06FC75E7612EFCF3AB1840661BCACD54DBDB744DE49E26B67BCFF7EB474BE6A7FBE1DF89F3549273F0FCA75856F2352FD7A7B281D3E60734EDC9FD9B77BA16ED9B9DDBE47F5F0DC9115C9F0D71CF649417125D4AF02EF90C9037C9E9055EB18622CB26E048A57A1632AFCA49F05AEBBBCBE642D7111EFF0FDCE17CB3EABC3E91CEE3FA8A9E702D4C7275726523B1963BA78D85EC57324F5B0164997F0BF344AC977C3D0BE43F1DD8338C732DF4098
	10D9C74E93A3219C8D908710E6463AFCDAD78565EE00DBG73F85D22DA2B4CC77CAC7AC2683891A7579139D31B9BCF51B6F0FF245DC3EA6949F3731467462F5B50F6CB4DE827AFBB51F6319C77CE975AAE61A25AE0E78E310F854ADE00B00088DED7C06B22219C87105A84FB0EA62C7B616ED0F31E48516242F4F0FAE099537E53A15B5033E352A464EF1347E2B39F1FBD2382FFAFD23D7FF86476474D6537358DB666CF0E86474D27C655F1136136DDA4B8B30CEFF7937A0B7FA2BF27E942756DBEFF68787820DF
	7F5B57C23F7E372FC77C635FE7FD5C9F6A5B9853EFAF4D791700EEAB394E3F3D007DD843F175E66C47BA0E5BC16F25191A213F4D086B233CDEA84FEA46B3F9DB9149BB64EFC9B424B2FCEEE02B024710AA6F194369B70F9E0F0FF6066D5D8B9D38F71F8732A3815682A434403392777CEFBB03777CA9B710AF37056D794B6159A07331BD3FEFG777C57DBA6BE7B1000E6D60BF276B12FC53B673FBB1D6C235859075956434FBDCE17E50E45DBE02C6B1D224B4E6A6BB2F60E2DB7FBECF653903B38AD4BF839ADAF
	B48D17474E8B5A6A54678561F21F23DB70BCE1D51E3ABED62ECF3E04759D2DFA7C393C5EFCC00F3F8C6A196D135A517626CBEC4C3A61693D444EA399CDF8A7524C3A4446E7BE2115E9587824F56378C4DF1A78CCA696E856DDD246A77932F6FCE69590BEB36487748C0A8D11DCD756CC6667E5620F2ACAD637717C8EB59F37B672BE7BD14698DE0500594B1A2335A769E366E534E7DB35DEFDB12FFFBFCBEBEFD2DF456B6FEF522BEF390C732370A23D7383DA31B308EDB785B27F290D3FFD51A488C1B1D09D7355
	F63603CC82D9B2BE90E46363F4FEFBFDE21F687309F6F6ADD93ED756C4634ECC60198D40F4B9B52C68F255BC1EAE5A792855E5E910AEA574DE5C20AD609C9C9A241AB0BDEAE88CDB8E74AB390DBE3FC0E9A1B00D253D1746B621955671D6BCC33A68403B1595007336E24C7BE98A3923B329FB3296276492EBE924E367B077BA704EDDF8AB1EBF15B8C9BB31D0DE8F10E641760F5E603F7B825CF58B1ECB0D5E40B32C39961491CEE98DD0DE83A034A16D0281BCD34AEE1B6A9956A2F205916FFE7505387BC5A790
	EBAB2C8E4F67DA5A50D60545397AFCFC7248F7717251AE11DC77592D28FB9C475DF1G698A5A51CEFF3262B8F583EECEBB52D6F7A0EDF4BB5AA91A5EEDCF3882F18E207C8A4AB0F4222D4D001BF985E5E4F7212D17DEC1997574B7896B201CEAB52494D81136269B752D32CE55564F126B2172B960F7F3789DBAAEBB2C9AF1919C5711057DDB4DF14FB7A1AE37C3CF87C1E06D450DAA6F853784BE17DE3930971EEEF1B88FBFE39FB6B987C5B77971CF7109E325F9C70F17A05F2B1FABB2923FD4E4F4CDC1C63120
	706D3FB999BE169B36BAA6BAD731F40CDDCB981FEDC23E3D1AB2633BFFC2BEB5F821EDF6DE73G1D5BB3BB3467B80BA8FEF607769C6708937DF09E54891D186BD75C185E79D37DB4793E1CB29F6BD76EGBDA76C54763710623D1D5A7E7E084738DBD0974B7B9BBE20EE5F54E5C831F6CD665C6A2EBA3DBD03EB82DE82B3B4B4F3F4F864FB16796359A6BCA45F3D2DFAFCCBE5FAC3C83ED98746EBEFF5C83E7F35CEEF0EEC6E185E9C291D10CF8D6B3BF00E1C2131B735DB6BB30D941F5D2D75197F6EC01FA9003A
	D95D7C77841D5373755553645B53B53DB972A4DDBF62027A7B8C452F8F6A2F398B7B1BF095761AD7313F755D6A7623819FFBF5B2F3E4CE0E1E5E6BE45E64EB217C692EEABD3EDC193EACA45F352C716CE48A4977E253F8FCD69D3EBD82D9CB22B2585E5365F40BF56C5731826E099F2379178BF8E7DC63E764CE01379921FABF6E3C7DE79CCDFF03B65E9FD07AF4D0759EF96376377D1F37358D793CE6A0788CE759C070994E93B4BF8D03F11FF78D4FB432CD84978E65A80E3BE1427310159CF7A49FF1851FA0AE
	229565C99C375502F4B3B86E218308B3F0DCC7877AE15EF5BE1FBB1531E9D63D47F45C7EF1FA6BEF50C6D4175227E7D56F173BEEFF5B8337358DB626D73B037D3027BB588F9F4DC13FA93D0E7E101A0BBED241F16155C8B70B63B61FC23AB99CB7446F0B2D6638B71A90D7EE433D6FF2FAB6A711AD290079778522161CFFBFD7B7767C40EDD46A0E184877911B4B596FA6F7622D3857BFC7674ACCE8E32E8D4FF37E4E047B7B6536094F5BD6814DDA9BBBBF25EBDC0F327EB1F97579A8AF2DA7D81E766C22200749
	A36785243F95AAF92CDFF791B2D9F60185E51BF4E4EB4F1E56EAF43586697A4F87D01EBD042EEA88E8F48D8B52F5BD999279BDE71322777E5C291C21E8751E9B544E29A4C5F65E2909E40FE71305C1B68963F74F96CFE27C16E9EC9293A4AF2199652D1E040D57EB642576AA727476474C5F2F197261497721F51EC12F478D0EFF58FC365E72C90D3971DC534533CD9681776C0F1B78B9CB2F7E3EBB74DE5CC6D1CC4EF0BE4AE95115036785B3B7ABBCED96FE373C974F5B7EB1C5297F7D812C0F9AD766F3DB941D
	7E38D5F94F5A2E487AA93FBF36FEDCD976B40567F8B37214594763391BFC4E40FB66A6F56E67G3EDB7649643637466C4DE6834F42C971F5DA54FCCB00A7FAD2FCF1E376E6314033EED2FCCF0F490913C5C3CA2EB8993E4AE6B5DF997018A660E36B41FF1902572697CC41EB53BD7941F4F3730369BE3584535D348453459F8C26CBBE98CC77324E7D05DFE78553DDEA8E26EBEFD653317FF19693B0416A957106106877895B79BCEE249F5FE3EF9E2B0CFFEDC13CD1C8E894B6314B880DC28239D150A8A4528B87
	0DC23C64718648AD3E84B578BF953E20CE1B2886C958442E00CA02317902BE4D84D8B2DFB3E5C8811DF7841F0155FC676517A2BEB1E59A85A3286D90191E0943642B9B5514DC71E88CA111E01347507012A43C47BEB865116B3AF9728FA71D229F3AC50FF7ABA5A113FC074A438FD1F9084E229FC2027936102FA81CAA15FEB3110BDCBFFD3BFEF56CD42CBE89396C8EE049176ABB006B3FE18C797BE90B034DC707C3FB87707B1F9113AF84FC14037F9F0CD1727B71D0F7F0DA87EDAE17E4F38D54386D64BECE6D
	08A4AA5F0797FDC6647B11C62EE4F57F7499517B060A677F81D0CB8788AB271F646F98GGD8D0GGD0CB818294G94G88G88GF6F7B0AFAB271F646F98GGD8D0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGA999GGGG
**end of data**/
}

	/**
	 * Returns the buttonPushed.
	 * @return int
	 */
	private int getButtonPushed()
	{
		return buttonPushed;
	}
	/**
	 * Return the JButton2 property value.
	 * @return javax.swing.JButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public javax.swing.JButton getCancelButton() {
		if (ivjCancelButton == null) {
			try {
				ivjCancelButton = new javax.swing.JButton();
				ivjCancelButton.setName("CancelButton");
				ivjCancelButton.setText("Cancel");
				// user code begin {1}
				ivjCancelButton.addActionListener(this);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjCancelButton;
	}
/**
 * Return the DomainAxisPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getDomainAxisPanel() {
	if (ivjDomainAxisPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitle("Domain Axis Properties");
			ivjDomainAxisPanel = new javax.swing.JPanel();
			ivjDomainAxisPanel.setName("DomainAxisPanel");
			ivjDomainAxisPanel.setBorder(ivjLocalBorder);
			ivjDomainAxisPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsDomainLabel = new java.awt.GridBagConstraints();
			constraintsDomainLabel.gridx = 0; constraintsDomainLabel.gridy = 0;
			constraintsDomainLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsDomainLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getDomainAxisPanel().add(getDomainLabel(), constraintsDomainLabel);

			java.awt.GridBagConstraints constraintsDomainLabelTextField = new java.awt.GridBagConstraints();
			constraintsDomainLabelTextField.gridx = 1; constraintsDomainLabelTextField.gridy = 0;
			constraintsDomainLabelTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDomainLabelTextField.weightx = 1.0;
			constraintsDomainLabelTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getDomainAxisPanel().add(getDomainLabelTextField(), constraintsDomainLabelTextField);

			java.awt.GridBagConstraints constraintsDomainLabelLoadDuration = new java.awt.GridBagConstraints();
			constraintsDomainLabelLoadDuration.gridx = 0; constraintsDomainLabelLoadDuration.gridy = 1;
			constraintsDomainLabelLoadDuration.anchor = java.awt.GridBagConstraints.EAST;
			constraintsDomainLabelLoadDuration.insets = new java.awt.Insets(4, 4, 4, 4);
			getDomainAxisPanel().add(getDomainLabelLoadDuration(), constraintsDomainLabelLoadDuration);

			java.awt.GridBagConstraints constraintsDomainLabelLoadDurationTextField = new java.awt.GridBagConstraints();
			constraintsDomainLabelLoadDurationTextField.gridx = 1; constraintsDomainLabelLoadDurationTextField.gridy = 1;
			constraintsDomainLabelLoadDurationTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDomainLabelLoadDurationTextField.weightx = 1.0;
			constraintsDomainLabelLoadDurationTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getDomainAxisPanel().add(getDomainLabelLoadDurationTextField(), constraintsDomainLabelLoadDurationTextField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDomainAxisPanel;
}
/**
 * Return the DomainLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDomainLabel() {
	if (ivjDomainLabel == null) {
		try {
			ivjDomainLabel = new javax.swing.JLabel();
			ivjDomainLabel.setName("DomainLabel");
			ivjDomainLabel.setText("Label:");
			ivjDomainLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDomainLabel;
}

/**
 * Return the DomainLabelLoadDuration property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDomainLabelLoadDuration() {
	if (ivjDomainLabelLoadDuration == null) {
		try {
			ivjDomainLabelLoadDuration = new javax.swing.JLabel();
			ivjDomainLabelLoadDuration.setName("DomainLabelLoadDuration");
			ivjDomainLabelLoadDuration.setText("Load Duration Label:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDomainLabelLoadDuration;
}
/**
 * Return the DomainLabelLoadDurationTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getDomainLabelLoadDurationTextField() {
	if (ivjDomainLabelLoadDurationTextField == null) {
		try {
			ivjDomainLabelLoadDurationTextField = new javax.swing.JTextField();
			ivjDomainLabelLoadDurationTextField.setName("DomainLabelLoadDurationTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDomainLabelLoadDurationTextField;
}
/**
 * Return the DomainLabelTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getDomainLabelTextField() {
	if (ivjDomainLabelTextField == null) {
		try {
			ivjDomainLabelTextField = new javax.swing.JTextField();
			ivjDomainLabelTextField.setName("DomainLabelTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDomainLabelTextField;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsAdvancedOptionsPanel = new java.awt.GridBagConstraints();
			constraintsAdvancedOptionsPanel.gridx = 0; constraintsAdvancedOptionsPanel.gridy = 0;
			constraintsAdvancedOptionsPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsAdvancedOptionsPanel.weightx = 1.0;
			constraintsAdvancedOptionsPanel.weighty = 1.0;
			constraintsAdvancedOptionsPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getAdvancedOptionsPanel(), constraintsAdvancedOptionsPanel);

			java.awt.GridBagConstraints constraintsOkCancelButtonPanel = new java.awt.GridBagConstraints();
			constraintsOkCancelButtonPanel.gridx = 0; constraintsOkCancelButtonPanel.gridy = 1;
			constraintsOkCancelButtonPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsOkCancelButtonPanel.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsOkCancelButtonPanel.weightx = 1.0;
			constraintsOkCancelButtonPanel.weighty = 1.0;
			constraintsOkCancelButtonPanel.insets = new java.awt.Insets(5, 5, 5, 5);
			getJPanel1().add(getOkCancelButtonPanel(), constraintsOkCancelButtonPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}
/**
 * Return the LeftRangeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLeftRangeLabel() {
	if (ivjLeftRangeLabel == null) {
		try {
			ivjLeftRangeLabel = new javax.swing.JLabel();
			ivjLeftRangeLabel.setName("LeftRangeLabel");
			ivjLeftRangeLabel.setText("Left Label:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLeftRangeLabel;
}
/**
 * Return the LeftRangeTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getLeftRangeTextField() {
	if (ivjLeftRangeTextField == null) {
		try {
			ivjLeftRangeTextField = new javax.swing.JTextField();
			ivjLeftRangeTextField.setName("LeftRangeTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLeftRangeTextField;
}
	/**
	 * Return the JButton1 property value.
	 * @return javax.swing.JButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public javax.swing.JButton getOkButton() {
		if (ivjOkButton == null) {
			try {
				ivjOkButton = new javax.swing.JButton();
				ivjOkButton.setName("OkButton");
				ivjOkButton.setPreferredSize(new java.awt.Dimension(73, 25));
				ivjOkButton.setText("OK");
				ivjOkButton.setMaximumSize(new java.awt.Dimension(73, 25));
				ivjOkButton.setMinimumSize(new java.awt.Dimension(73, 25));
				// user code begin {1}
				// This listener is not used because it's calling class implements it instead
				ivjOkButton.addActionListener(this);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjOkButton;
	}
	/**
	 * Return the JPanel1 property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getOkCancelButtonPanel() {
	if (ivjOkCancelButtonPanel == null) {
		try {
			ivjOkCancelButtonPanel = new javax.swing.JPanel();
			ivjOkCancelButtonPanel.setName("OkCancelButtonPanel");
			ivjOkCancelButtonPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsCancelButton = new java.awt.GridBagConstraints();
			constraintsCancelButton.gridx = 1; constraintsCancelButton.gridy = 0;
			constraintsCancelButton.insets = new java.awt.Insets(10, 20, 10, 20);
			getOkCancelButtonPanel().add(getCancelButton(), constraintsCancelButton);

			java.awt.GridBagConstraints constraintsOkButton = new java.awt.GridBagConstraints();
			constraintsOkButton.gridx = 0; constraintsOkButton.gridy = 0;
			constraintsOkButton.insets = new java.awt.Insets(10, 20, 10, 20);
			getOkCancelButtonPanel().add(getOkButton(), constraintsOkButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOkCancelButtonPanel;
}
/**
 * Return the RangeAxisPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getRangeAxisPanel() {
	if (ivjRangeAxisPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitle("Range Axis Properties");
			ivjRangeAxisPanel = new javax.swing.JPanel();
			ivjRangeAxisPanel.setName("RangeAxisPanel");
			ivjRangeAxisPanel.setBorder(ivjLocalBorder1);
			ivjRangeAxisPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsLeftRangeLabel = new java.awt.GridBagConstraints();
			constraintsLeftRangeLabel.gridx = 0; constraintsLeftRangeLabel.gridy = 0;
			constraintsLeftRangeLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsLeftRangeLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getRangeAxisPanel().add(getLeftRangeLabel(), constraintsLeftRangeLabel);

			java.awt.GridBagConstraints constraintsRightRangeLabel = new java.awt.GridBagConstraints();
			constraintsRightRangeLabel.gridx = 0; constraintsRightRangeLabel.gridy = 1;
			constraintsRightRangeLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsRightRangeLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getRangeAxisPanel().add(getRightRangeLabel(), constraintsRightRangeLabel);

			java.awt.GridBagConstraints constraintsLeftRangeTextField = new java.awt.GridBagConstraints();
			constraintsLeftRangeTextField.gridx = 1; constraintsLeftRangeTextField.gridy = 0;
			constraintsLeftRangeTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsLeftRangeTextField.weightx = 1.0;
			constraintsLeftRangeTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getRangeAxisPanel().add(getLeftRangeTextField(), constraintsLeftRangeTextField);

			java.awt.GridBagConstraints constraintsRightRangeTextField = new java.awt.GridBagConstraints();
			constraintsRightRangeTextField.gridx = 1; constraintsRightRangeTextField.gridy = 1;
			constraintsRightRangeTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsRightRangeTextField.weightx = 1.0;
			constraintsRightRangeTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getRangeAxisPanel().add(getRightRangeTextField(), constraintsRightRangeTextField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRangeAxisPanel;
}

/**
 * Return the RightRangeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRightRangeLabel() {
	if (ivjRightRangeLabel == null) {
		try {
			ivjRightRangeLabel = new javax.swing.JLabel();
			ivjRightRangeLabel.setName("RightRangeLabel");
			ivjRightRangeLabel.setText("Right Label:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRightRangeLabel;
}
/**
 * Return the RightRangeTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getRightRangeTextField() {
	if (ivjRightRangeTextField == null) {
		try {
			ivjRightRangeTextField = new javax.swing.JTextField();
			ivjRightRangeTextField.setName("RightRangeTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRightRangeTextField;
}
	/**
	 * @see com.cannontech.common.gui.util.DataInputPanel#getValue(Object)
	 */
	public Object getValue(Object o)
	{
		trendProperties.setDomainLabel( getDomainLabelTextField().getText().toString());
		trendProperties.setDomainLabel_LD( getDomainLabelLoadDurationTextField().getText().toString());
		trendProperties.setRangeLabel( getLeftRangeTextField().getText().toString(), GraphDefines.PRIMARY_AXIS);
		trendProperties.setRangeLabel(getRightRangeTextField().getText().toString(), GraphDefines.SECONDARY_AXIS);

		trendProperties.writeDefaultsFile();
		return trendProperties;
	}
	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {
	
		/* Uncomment the following lines to print uncaught exceptions to stdout */
		 com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
		 exception.printStackTrace(System.out);
	}
	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
	try {
		// user code begin {1}
			//set the app to start as close to the center as you can....
			//  only works with small gui interfaces.
			java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation((int)(d.width * .3),(int)(d.height * .2));
		// user code end
		setName("AdvancedOptionsFrame");
		setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.X_AXIS));
		setSize(404, 283);
		setVisible(true);
		add(getJPanel1(), getJPanel1().getName());
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
	public static void main(java.lang.String[] args)
	{
		try
		{
			AdvancedOptionsPanel aAdvancedOptionsPanel;
			aAdvancedOptionsPanel = new AdvancedOptionsPanel();
			aAdvancedOptionsPanel.showAdvancedOptions(new javax.swing.JFrame());
		}
		catch (Throwable exception)
		{
			System.err.println("Exception occurred in main() of javax.swing.JDialog");
			exception.printStackTrace(System.out);
		}
	}
	/**
	 * Sets the buttonPushed.
	 * @param buttonPushed The buttonPushed to set
	 */
	private void setButtonPushed(int buttonPushed)
	{
		this.buttonPushed = buttonPushed;
	}
	/**
	 * @see com.cannontech.common.gui.util.DataInputPanel#setValue(Object)
	 */
	public void setValue(Object o)
	{
		if ( o == null || !(o instanceof TrendProperties))
			return;
		
		trendProperties = (TrendProperties) o;
		
		getDomainLabelTextField().setText(String.valueOf(trendProperties.getDomainLabel()));
		getDomainLabelLoadDurationTextField().setText(String.valueOf(trendProperties.getDomainLabel_LD()));
		getLeftRangeTextField().setText(String.valueOf(trendProperties.getRangeLabel(GraphDefines.PRIMARY_AXIS)));
		getRightRangeTextField().setText(String.valueOf(trendProperties.getRangeLabel(GraphDefines.SECONDARY_AXIS)));
				
	}
	/**
	 * Show AdvancedOptionsPanel with a JDialog to control the closing time.
	 * @param parent javax.swing.JFrame
	 */
	public TrendProperties showAdvancedOptions(javax.swing.JFrame parent)
	{
		dialog = new javax.swing.JDialog(parent);
		dialog.setTitle("Trending Properties Advanced Options");
		dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setModal(true);
		dialog.setContentPane(getJPanel1());
		dialog.setModal(true);	
		dialog.getContentPane().add(this);
		dialog.setSize(379, 283);
		
		// Add a keyListener to the Escape key.
		javax.swing.KeyStroke ks = javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0, true);
		dialog.getRootPane().getInputMap().put(ks, "CloseAction");
		dialog.getRootPane().getActionMap().put("CloseAction", new javax.swing.AbstractAction()
		{
			public void actionPerformed(java.awt.event.ActionEvent ae)
			{
				setButtonPushed(CANCEL);
				exit();
			}
		});
		
		// Add a window closeing event, even though I think it's already handled by setDefaultCloseOperation(..)
		dialog.addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				setButtonPushed(CANCEL);
				exit();
			};
		});

		dialog.show();
		if( getButtonPushed() == this.OK )
			return (TrendProperties) getValue(null);
		else
			return null;
	}
}
