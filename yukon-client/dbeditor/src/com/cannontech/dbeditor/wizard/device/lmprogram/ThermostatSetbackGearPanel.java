package com.cannontech.dbeditor.wizard.device.lmprogram;

/**
 * Insert the type's description here.
 * Creation date: (8/1/2002 4:37:34 PM)
 * @author: 
 */
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.util.StringUtils;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
 
public class ThermostatSetbackGearPanel extends GenericGearPanel implements com.cannontech.common.gui.util.DataInputPanelListener {
	private javax.swing.JComboBox ivjJComboBoxHowToStop = null;
	private javax.swing.JComboBox ivjJComboBoxWhenChange = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldChangeDuration = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldChangePriority = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldChangeTriggerNumber = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldPercentReduction = null;
	private javax.swing.JLabel ivjJLabelChangeDuration = null;
	private javax.swing.JLabel ivjJLabelChangePriority = null;
	private javax.swing.JLabel ivjJLabelChangeTriggerNumber = null;
	private javax.swing.JLabel ivjJLabelChangeTriggerOffset = null;
	private javax.swing.JLabel ivjJLabelHowToStop = null;
	private javax.swing.JLabel ivjJLabelMinutesChDur = null;
	private javax.swing.JLabel ivjJLabelPercentReduction = null;
	private javax.swing.JLabel ivjJLabelWhenChange = null;
	private javax.swing.JPanel ivjJPanelChangeMethod = null;
	private javax.swing.JTextField ivjJTextFieldChangeTriggerOffset = null;
	private com.cannontech.dbeditor.wizard.device.lmprogram.LMExpressStatEditorPanel statEditorPanel = null;
/**
 * ThermostatPreOperateGearPanel constructor comment.
 */
public ThermostatSetbackGearPanel() {
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
	if (e.getSource() == getJComboBoxHowToStop()) 
		connEtoC10(e);
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
	D0CB838494G88G88G6ED885ADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155DC8FDCD4D53E676D3ABDFBEBE9C509C59B2D1A1454D232C6CA9BA9953564DA6106BDF7E3BF0F35762D356C4B374B6EF3374BBFE59494D09373CFD32102314300D0ACD902B088CA4546041810F3E1AEB323434CB8F311815DFA3FF34EBD675E996E8C236D3ECF4F4F67673D67FBFE3F737BF37E5CF3FF678E5C36B7227233F336F1DCF6BEA77DFBAF1B6362CFF1DCF9EDD181455A2FB1E8B8453FB701CA
	389943F9F06D053A0A5586CDB1D7348B8958219C7D1CC133836E37F2253B749F41CDGBD85D037FC59F7678657B372A1553381176F668A613A994885D8B8D07AB5C87FA3DC190447C3F933F40FA8BB0763A22EC5BC19274B94F8793504EF0AF4AD1F0EF852395DD2FDBA6008A78B28F03A5CA6B172BB0D8DD2396F3AF0EE6D937BF30A10AEC57B146C126C8C692A656E41014865B4973A61FA386B1C6543C16730EE10F7B81C8ED198346A4C830259A6BA5D3AD15BB86FB66B4C42855B2020338F3B5CCE0B1B9FEE
	ECB4DA85773053A372E217DB7844A538F9D1B8A6706EAE5EA158C1997265353E822C076312C1B7317B29224312DD616742397459BB835A5DBA0358EB1DA15BFD6999EBCE59F5286DED590512DCB4542783911DFD59D4E79F702D423C152791EFB85C47GA5DDC7ED7960G35257762ECEE5AE17D06AD81EC51DECFECE83FDE36A561736A8DBB711848EEC27DBA856A220192005201389B8C9AAD50AEBC9693BEBC885766813145B9BC6CF4B43BDCC76DE677C7CE33E0077B46C6281130CF05C1275B4CF1E43C6D6A
	2BD23107D2438D3EE3CD9E9FA7B9BABEE2BE3D19CB7861741CFC955F62D6D464F4CAED18EE10FDA3FEC5B7A33F42G0F840A83CA81DA83D498CE7DDAFF6450E4FEF54018F2BBF8FB7748000BF70BC7C7ECF63360167CFBBDBB18FFEAC464AAAEDB0E44A57A10B2AE2B969C4F59209217D55155B99561A49ECD61F2DC08EEBE20EED378447E40F3BC9C0D43DB71B89461BEDB9A23841F0771E485CE46F8B546D1B98D6A8E5FC847ED6FE66AC3E75E35DCF14D89D59FA216B465C45DC86C093F513FEF273720F5A683
	F06EA603268CC88F5485E4878A3B09766F5F0ED407B8EE3B94FDCBE4B7B63D86D741AB3A798E87AC950EC12107F75B78813B6061A41E2DCD4876819F39B2BC1F86C147B14101D6961B53812D7343828CA48F61F9332984713885DCEB348D8B20E0584551F6AB1B3642554EFB444FDCE6D81BE48ED2DF5304E2F9016F0196218138DFFE930AF9ADDE332260BE810844AA2195E15BE69AB4D5B3E9DCE21ACA61FA828636E0EFECEC358DA2B3F8779855FFEFB3DACB048B02C3EC95DC02438CB79E29AE3ED9A347E548
	49EC7E9146519CE98143BD8CCFEAAEA1B834210CB22DCF41753CA83AED83A32250AD08224DE11165D331AD9E1F69676108EF8B8CE80CB501DF3DC0D666DF7F512D8A7FBA1D4EF3A3AEA36AB9A9CEDE3C4EDE827E70197459B87BB4FAD6464F24637465DEBACEDB0BE6F1D5ABBAD447A9220C19E4FC2E1B69BF576E46ED4ABA6ACF77CAB271B355675A57A12263E326A3779F5CBEA292777D47D05C4BA38C1AA6A0D3840DFB44D1AD19EFC7F90FC0E758A7EE68B141AC0FB242BB2BA570980BEAD19FE3F734280F31
	181660E3A82EC5ED8C113A45AD6859629CF5A846E4E2CBA91E438E07000DE6A29C59FB3CECC43130B9EC62C91B59A2085D36F18166DD849ADB9DF88EFA61FEDA849DCB23EF5331D4BEFEA317F826DC75F996B137A5A7A3020C0B2C88F9AC911B9EEEC5B1976C42B0842A45B96290897E981E5F92AE2DAF8F9854F99FB22071F8CE981378169A56F3E89D310F884C37D8FE930A315BB68CDD05A62C347F1B1E2D5C7F154DB2E850DE518E57B0A0A27B7045BDF01DE4F7A50D21B36D484688100D81CA1AC547501136
	1D0A39AB8D1E96275DCE3A0275D15531E254274279918116E6B439CFB03F8E9DAB752B6B11FCA4756F9FC3BE5A956B422AD948C7935B7321678641C6B1769B18DC0F67516770C7454A7DEB5916B1557E447DF7B369473A1B7D1FE1619D28DDA360661B296F8B0FA9572DE60F47B9E8430F8829EF5E7736F1D73A6745CFA35B2032A3701E738FF8ADC471705ECC771CB5DBD05F44C2B999A81D59A734CF9CEBE75BB742F548667640DE49ACF8E90CDD6DA8D66899A641F4EFD72B0CDDE23ABEE06C322418955E629F
	3B670EA3BBAAGBFFC8B356D576DA50A5841BE949E599668D9EA4F2C0EDCB27625722D9D79F46CF3F46D7CB35B9BF5C13BE6A0AF50B4C9FF54AD9357EC32871A0B6395F78B5983EDE278820C27AAF032B7FA8F63283C966AFAA36933FA798964D78214D3A229DF35470B94FEF5B9ED8E1179747CF1B4FF1DEE1B456620D88646E0970E97B4723C5B1E035ADD85EDE681912CFC93428AEF057734DBA9160A3186A8F7B1ECDFA1424CD076B22C9D3F60C6C2B96ED67AEC1B3F85DED239B4A82F855A76BD0326814874
	BDDA7FG2E8F03F294D0A2D0BA90F79B2D7FE0CBAFB7797387350F5AD55D3FAAF0EA036F3331A7A49D3952D8D52B5B24384F175A25E5927F95A7D0DFFBD9F9A5AE0FC96D423A055F99DDEEE1C8F0434AC69E9DDC5FEDA82E865CCF65F02F87A27C6FEFC2730B375BAC8EB46BFAA96F3D382F2260BE0E717E5A177794654D45FD188177D90C77F061FABCBF471CA3B0203CD1082F3B50679D9F30CEA0D39409A3D9036E9DC5B19802B5859E315DAEE110B35E0EE44F94D159C460CF85DA8B348D6A6AEE27BA671FC5
	4F61631D7C00E0EFF70E9A1D5D2253A545254D06566C6330D39FF09EF5FA65EA297E126BB5A65BA53887413FCF85730874D0A0BCB3DD3BF09B5DAE1B234DA6584D9398895F12F12C8B2F03ADD6D8A7040F84516A047756D46C4B4BF82C52B8C45ECE760909F0A575AFD4A86BF7FF1F5437FE1F56179FD4136FE5753F601475F6C9BE8A2E64D93B750C329F62247AB4382E814A1B0DF2BDC91FF2ECCC6D5C24052BC398551E65AF70BAFED4543542AB03833D9CD45469EB2B34F5077415E47D0D9AD52E3F8D9B5F8D
	387E364FA67D5EA7DD6D7527325F407AB80EA538188D491BD04E096907FEF4B99D90FBDD27214DF82639336358479F99BEB682CF60B6DEAF542A37E17C24CBEE020C439A93FA87444FC2AAG8FB9374DFC1437F412D10D1FEFE3F86CA37F42E753675B3CC2347E26C0B983E85DEC3ACF736E27B91518BB2E65BA430E2B66F7520DB5B95B661018B44C111FCD4BBF2F5EC0F4FE27086A9421BEE68E42EC1B15FD0E7C6D077FF4C8972E3F45273EC412176B1F7429C7766E7311FF5527BEDFA1CFFC7B71BE6A5B3A9F
	C7F2769FB6844CDD2D15FCAA1BCB2EDC5E296CB5FBEDB9A46E2F8C15A9636E55F9C6E1A1E8ECA4B313783F2F38C0525BB51746766FED4A5CD942161A1CCD8174CF1574C64F15E39B8631A57AAD76DD6A7A694AA16DEB76D6A29B507A7C62DC1ADFBBFD90E1E88D525EC1772CDF3E3B1FD36C294E59AD3A3303686DC2B704D69054B0DBCEB8296D32EC6A5F94564EAB158162C2F6DBD86E4499A407621EF0879D0B7B3A51D8544E03679AD057BC9A2F1F1CD64E53621B35B9598162E51DC762A44E735FA77D520874
	C5831EB40F6EA7B45D6F403507BFABE5BE8CF85F08DF56G6CF2BBE143AE0E91946F1A25E7671705171B33A2FB20DDC5483F0CF9E48F144D70F20CE7AAF032B73A3B1066C657C11DA91A6E0D5E6CC171CD02F2BAD096D0711D701C846A3C137A76E9371E1BA45FE3F43ABA6125532EC8C5907F1AB6DFD9CE6E618DF4DC47AD3A090B3FAAABBB5038B65DC97AA7CC3AC6641C4AC6D762DF5CC964DFAC5425GE582955CE5506C862A3B0B7AF7F3F795718FF5CF33A87203D63C93274E755804D199C68FE7625B41E2
	25EF6D50DE9F106BAEA907FF57447D9431297294DAD3A220BE8EA885A883A8EFBE3567B75D1366CF8D5EA0795337AB29DD65732575F43EECCB677C09F691B953C17FFD1C89F8C509FF2A0A9CFE8E839E6BD7178465B4BF0C6858435E855628344745405E8628A246FF0F59F5C6E95BE1286F82B247285B24A4AF70CCD370C52948103933922F99F1B1E46E6CE078CB98DF2340491CFA876328DCFEB74C1F3B691CEA7F9C39C27A72BD63BEAE403AG5DF89C1634CF864E42DB151137B0197F44F99016F7A14B05D4
	77DBA39ABBA8C7E2038D69B8FE0D6D86C607054469CD348CCAB1BB2838DFD0984A9E7DCA0968C879276AA07E9F6C891C5F2A69D14FEF3D57231E5F7A20A7F8FE4B5093B83F5559234CEF11FE7F6CB46A1B48FB8C1AF820D5C019C0F9BF20737A391E37B8BC2FD556980359A234B8F1C6DC247A9FBC99585724131367511FBC095EDB861C22689CC66DD37C1913283F60A5G0384CBBB09E2842F3CD61130CA784FCF62B3D4E1884364BD35996FC33460177E87745DF5F4GE12DD06EE3D8B246DCD08EE358DF8669
	BB6EB496171AD3017DECB8255E279F1ED26F5356D34173621D278275B9312F31123E532FE2B66F3FC8FD4BE458790B540F3CD80AFD6BA065B3B26CC90CD9216CE5D86DBB881B8265C8064D41F10905F28A43FEDF0230F4A8E7B2EC0A05693D17E253314DC6A85B99D60777D2E1D00EE458E31B29ECAC43DE68476F8B7799B4E577D16CFE3C4F2E02F2AB435CF80F558BE5BB43FEEBC2588CA84FE158360109317AGE34940134678CE9EC0589AA867AD20581D8728FDE58BE87E62F97CEC36C2F9AAD0AC62854A84
	AA0BB3E81A002CC061C009C02B62285CABD8EE9D14F77F5020B98C5487F489A892A881E89550BA205D8B21FEA1152B690ED5D9FB10DEF4C5FA1438F5C16067CF26CA5DF289C33ED0FBE9DD19623EA9EE72671AB54E7F99E9D2351D66D2367A61414EB1B629E0B4F703563D6D9CF96E513AA2457D16C96C5E4C1177B05FF53D7FF460B540FCFA7235EE78B44E21429AE59CF38984F31F46F93C21A10F4056545153458C6B70F4B9BDB67484A56F493D45A80FBF64F408A43F63406BDC9F0C8D6BC21AEB39F6C0BD97
	F4879E6391409743F81F9FC06D09E0B849AD1475232916359071378E368609CF2E9F25018CA23417C0FB10B58B497B8C3AA77A87719A50F4BF6C5B01887649C5EA1397B022F76A45E0FA737D744E395FD7EFBC14EBFC744EBFC8F5A45DCF7D4EB8686BF75B979CC7F3919D6F2031B54CFBED43A343B8CBC75A61711A1889ED2CE36DAC9E8C1C53FB9E2F179571300F0F277C1BCBFD7B2F3C947118205E9ECF7D3F45B2D95C15B1700FFBA434F3CE1AD3C4679D15589F5810797A73A35C9F51409F476CBB119D581F
	7FF98F2F75403B06597A734D1375D5B0DB4B9EB0E8F8691E687FF33FFA6CDF7B8267C701DF7FG35F5D9113ADF2B7133218F782C0C776AB3E81E8DB85D78C1CBF21ED1AC57968EFCB1C099C0D98F105CB411AB60A766CA5F607DF325450B8C1A72C5E42DA3F246815FFEBE0B675EE160E9DFC45BF69CD04F6FDE051FB1DE601B360846B97140E4E3E203A25689A037FC119C670CC5723BB651EDF320FDA0495C10BCE86B8879E20154AF3804760B0A8408AF93EBC7A079D45C4EC7B687ECD7BCADD66022753DC77D
	75F439EDCE37CD9CA3756F76286A21CCCC4F70111DFE6D98E1A3E8915C9F0F8C8F603D93617B4098006F93E9091578C60A50F965F1A3ACG8A356A3C7D463DEAB67A99A065E37159A451FF52AAB888AB295B61594AA9725CF23514FB56997DF347E89CE6AE22396315457E39E3D41F371856DBCDEA72E52CFED91FDAFD952B7F2AD42D7DA6D6FF1ED713B7327A748A35FAAB2BFF2EDFD9FFE9B1291F436A9F2BD6539F4F6AFBACEA6D27307A551F796766D1FD7AE21AB70F9964D81F1E9BC06FB9BEF973B6182C95
	EF19611D55E9F92BCA2B2F525654D6864BD9BFB82065A64FD92B4993FBFE0B320AD2AE699705B41734FB7EF42E75592C6C3739GB95A45E41DE9CD10F3FE2D0EEBF2C81BC3C332FD3BAE3E616F23FCB6D0D38F8E569E2A14F807AEDE19AF19C7085E2BAD325EA587C3E90B485DEF1565E68D8633372696BA242ED20A5D9BE7E539B5A5214B2DF648F2DECB28E30048EEF349FD775EF9391DA4AEF47DF829126492B66F8DAC5750G5D23E73A3F6711F52708F29BB1C5C15A782B5E270D5F0C48F23F7BA208DC9D15
	A3B92464B374FB3ECBB0666292E85E7B05BD40227ACD02E544269B91ED7646C623CD348BC725472E14C3AFB651B312B589B4977E2189FFD774A03C67G999F24F9FB83CFF96D8FD2DE8F0FF867C0B981A875C1BA3FCFF8E57FCE9C787A737B5683D7FE2E15F9A470DCF10CF1AC9602111ECB2CE1BE971730B3149FD16CB6934526B2AC3F1F4AA6B2ECE1A965ABFF08E251B8064816BE066D2824D862C334BF77D5781C2B4967986CA9C9E4CB3EE0BA92E9FBFF2E26D89F432E31D0AC1CE1FBFA6859CCCAA24DC576
	0D233DCD5E43860D962895288F4885B495A892A896A889E89550DA2002A570CE8C2487EA82B282D91750DC4D3B829A17014EB92403C21C48F273641B8A72EE3A6C4CE467G171628650BD7CDAA37572FCC64361B2E6CDC65BEBCD7A216781F2BAC46781CA57E67AA2DA6FA2E928BF55B1652BC42F17E4A743F00671FF7293F7ECC0CCFD96A2FFF0C277AE7C05DDA267F2C51BF975B1B846FC1C952D9C912FAFEA19C70E83F3A84A8AFCFD26B1B6DA554478C29BECB41D778880C39C75464667874CD9D70348375C9
	3C2EC7821DCFEC6C7777E98A70C6C87CB1AAF2089281CF752BFB914A6BD4EDDBDCCAEDABFED47A8D4623B2DF835CF7BE2AA6F7B7BF616C8478C409F72AF40D74130D05F2129F168665B5C05CE37E791D5D157EBA3681CF0544F7D83AF6BD665B1E994ADEBFEC9A14230062A668F86D8B6AFF32D437CA41138977F949EA7E676A29DC19D4DF2560EB02FB232A5C759665D830824FA52099926F1C64C0E3612D9EAA97AF7124A8F853D564246F6746836719C647A74FB3050D2B67542F9A1F18D37F6E38FACE7D3AF1
	E5CE1DE0B70D23670485564FDE46F78B43CEB1AC8A37093013B633E8E5789D58AE04378BC8A17B06975BB50A31DE7A9D3A82BBA5E18B47513BB838ECF53A296C62F17C1EED335B256FDCB10F4D816FEFAC9EC958C61B0BD7587DB843105D68DDB017CB5CDA04E56DB60750C23E62E7FD99EF62587D430A7B6326FF7E1955607F010E17F859275FA96E3F1A30E67C63F5C77CD3F5103C7DB43C4FDA8BF32E70F11A4B7F994634D056B36C68E7B8CF83E5A3433CD578FC844AD398560277E391D00EE6D8F5A5BD9BC8
	E058FC3C7FABFC4220A9FF02E2D5A5747C2601E1A9A6EADFA743F67034BD93433231ECAC141399361859320AE17B75343DCC061D57D3FF73FECC312775D4C7B1432A7A292C09E1237DD4D6E4D8DABF0DD598437ED4C8E3954E3067CF33D8B12CDE47E245306ABE9A2B4294E69F5E4FEA21FC18E17730D8F5B16C8FA65A1E19E17732D845B16CA7BC15CDE6586B835437D58C3BB8C0FDFB11E197F0FB6B202CFD12E2CF14D0FF6B98164F7A231DE131ECEC74B14C75B76AEF8C43AED6A0AC914A698C7B30125A3C16
	E1D7E32CE019C1D3360CE2AFD7D21B4B99F60427B6B7B06C5FF8EAF3A7436ACB284DA606CD9B22B60B8C7BD98D35B909E13775CF3479AF789DA18DF856B23EEFB3BF8AFEC231E5ACAE3B99367AA46DB7BB43DE5753B9934630BD965ADEB8432C6C6CB11AE1ABF750763298F627057ADB381C453E1E3627E55823982B03F2AB435EF9176A68E258DFFB28FDD18C3B24236D45B16CB1B66E131753BC70EC237AF94DE23CBE15BDE550D4BDC5F9579AFD4FADDAAE21351A541D7ACCBDF7FE9E2FE93D50061935B33DAA
	F01EBF9A2FF7B300B71271FFEE724DE73B7098CF067A5427E80E3A2D726B1CDB64BDED507C7E55D7DFADE27EAC6154737B4F61B5359878F7BFCD4F11BE4363379D4AC6067D370946AF6CE9EA230E7FBA6795B1500E812E87180DE5A56AB157E3FB6201BF095913016D490472BA06FD22E7FD7C8C4DE38C7D9D75A93177D77AE076EE18445EDEE8935977A7082B9427FE2A4F4C74DD7DA5477ABE9B4F5B99D097798C35F1BFBECFC906F2AA438A70DA4D25427CC925316D548533D5B3092D07211D97247EA7BAFA31
	0EF6400D29F49C7E2AB470384D590A3FA7G5EE929544EE7599808E7F696879D831BA631B3835AF9DF9A8352F844FD1D8978BA26B70B27FAABD650B85F7F159C67B97879E204BA538A6A5BB5A5E8EF8F1BDB5BB84CB35E0E52AE7474174DCD2C8B59B7ED8555758A99EBD0CEE2180E0CB5A82FDBC17DFEEC527939A1085FD5BFB5E86679744FAC7C7CEAG3C752754076BAB367A7850CCD7B5C9A626025A6F7AA93555D2C9E395495A092A74DD677E4049315B014FF31301B71535E165E93B5C3394DB0031AAA8B7
	B06C8B9CABB314DD4F52384C9E1AEC5C860BCB943473ED080BFC5E3C3E96690881BC7ED96ACF6B970147EDBF1EF399401B496C0CE1B1591DC66D7CE5507E5B64E7E7FB1A2F1DFDD02E67146762DBFA71BEGF0B10D6AED416BD19414E39856E6213624B1DB3E8AFAC61D4F850FD95ECAF99D1566EE99DE53815F3D126AFD9A6BED073211E1F36A29ADD3D652587E2C5EF72C3CFA818D83E26751F73F4EB78C31ABC92ECA436C9C404F7984401319CDF3F5542635AB5939F85035102EAF9A692AFD4ED7EF9D14EF13
	6E8367271FB6EA39D04FD942943971612AE067957AC34A730AFB46E43986D368F253957B1237ABAF6F1C65BE7C7CA377AB3E145BC9A88EDDFF56D7325C4326A0E79D076A25338E42DB48079E537D59321D9DB9323E93417455791E49AC4E1365DE57079AA7694CCE9F3A2D3F4A1775587BC3175B31C1167B2F4250635FD8A04B251E8EDD6E260D32DC256E7246CD52A639BFDEA8145B99A88DDD7F0D1BE53959C1470D5EE75C3CEC8ABD26CB3748F69E5DAE6BB372130C1BC3323EADA11DBD939D7867F3745CBB68
	D9E91D948B427B9066D59C85693A517F7493E08FBE87137C38E1072C630FE7C251C1647E38D3161BF3196B441EDDF29CCF6C165BF1D5046E63520AD0FCA43CDFD6069E7331BD32BDF5FC6876FC45079E73F9FBE59D87C36AD7A23773CDD9AEEC6872E2FE78ADB966E75E165B896F8FDD7F374AE5B9EFE568F3ECE9C8718FFC6E7A78BE59765F6F17ED382BBA74757A758AD92E54927AF3255F927A587920D236735CC1D95FDF036973337345AAD94ED9FFF94F154575215B3AC9A75B2A2B16F51E5785998F357AFA
	6537A7893A2F572FFFC3BF7D62485FCEF35BC4F88A443C4465FB5CDC69C45D4D572EFCB666CC6B369CAE2703DBEF45678B9DDC7EA8BA28703AE047F58F5075A2372D5DB92A951DDA74BBE4AD4CBE516D34B70A5C2EE3EE6788G3C0B9F3409E3DA7673E26D3EC6D0D8649360C7EE2FDF1D72739ECFAE373E035B09F2A75D029B7FA4C6B03772A27D9BBEED1C4605FE5B0F5E89BA38BC8F3E64FB44B13B5049ED9B7A947DAD8B0FA8181B698F82BCA2373D93E92AC65FD8D437739E2B088E005E6F64729C682F8DF1
	25245A4EBBAC556417B76FA367386C66A69CB97CF084FB37E2FBD1ABBA564A5645DB763DF9BCA3BE174BE963B65A1D3C390D9F941D6E8ECEA3DA5D02476A341B3D2EA3242102D1549D68391B8B4D6DE26EA3E7F5687D1599FD4EFB6B3422970B5AE01F64F5F3551893ED59F50C83FDD66786D5023BC3C4FFB94469AEE7F57F32D75C7F64319DFDB9DC366464F96ECBEE3D57A502D5EE5E6199F23A0741822D1F85340AB5243B61F17D0DF77E71F35C10ACAAF2EF101001E835C5F08ED7B78F8D599C82EBG62874D
	6FE14DA39E1D9FCF29773A6D47D73CBAG7D5D4A658E6B75787F9A7CFFAD7A3F86A3B598292905A0E6F2C17F7957FBBD486B0DA299A4E04F96EC8F6AE6DD9B7414785CCB0FFF7F5F8FE72E816F5A38FC7C97AEA05845B29779D8712A189B77EC8ACF7FAE6A8B979A92345D75C37C304DBE86B72DF8E970A27CF5AF5304BC6F40E7EB69978EB5667E476C5C9CAE8F6691B9EB04410B8E8CE1703A49344A054903475DC609BE00A11807155199FB93AE6D12568D740EDC67430336E3AE15D25E04510C500209439241
	0538F2B37E3F6432030E2436E2596DC8A13AA9730D50FEAF73A2DF72945994488B7415E2AF1348117CF67BC8A01E4FFDCA955FBC3FEBE53FCBA55FBFC43AC52E18FC582A255F8B4352393EE2586650D54AF5749BDF285BA2FDCB2BA563D30910EFF191023E17C5CBB291EF74892EC525740D8AD53357E72053C4F3F6GAFD0073FBDC1A257C7629BBAA8BAFDA7C31EC4797F3F7A6BB35F7EC23665F0417BEB2F0F84220DBE92E873D661A3BFDD25740D0A42009C85741ECAC7ED5E24DEFC03FD324BBE2187D01EE4
	D3C1GAFFCFB96C501762C6FE2DD61D3FAFBC25CF283C8FC23E2B3AE47A60FD36BEF9431A799CF36D40FFEAA17C73EE32F7CCA7937AB323E33A3605F18312F163E73DB6D7F37AB7E5501F32C00272D265FF4BEBB7C97AE70EF543B07F93BDD646D671A9DE674FB7523E322A07F5EF61A83356F21BCD2A5293B41016CBE2B1079DFD0CB87883E82850EBF9DGG60D9GGD0CB818294G94G88G88G6ED885AD3E82850EBF9DGG60D9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4
	E1F4E1D0CB8586GGGG81G81GBAGGGF99DGGGG
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
 * Return the JComboBoxHowToStop property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxHowToStop() {
	if (ivjJComboBoxHowToStop == null) {
		try {
			ivjJComboBoxHowToStop = new javax.swing.JComboBox();
			ivjJComboBoxHowToStop.setName("JComboBoxHowToStop");
			ivjJComboBoxHowToStop.setPreferredSize(new java.awt.Dimension(150, 23));
			ivjJComboBoxHowToStop.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			// user code begin {1}
			ivjJComboBoxHowToStop.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.STOP_TIME_IN ) );
			ivjJComboBoxHowToStop.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.STOP_RESTORE ) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxHowToStop;
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
			ivjJComboBoxWhenChange.setPreferredSize(new java.awt.Dimension(205, 23));
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
 * Return the JCSpinFieldPercentReduction property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldPercentReduction() {
	if (ivjJCSpinFieldPercentReduction == null) {
		try {
			ivjJCSpinFieldPercentReduction = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldPercentReduction.setName("JCSpinFieldPercentReduction");
			ivjJCSpinFieldPercentReduction.setPreferredSize(new java.awt.Dimension(88, 20));
			ivjJCSpinFieldPercentReduction.setMaximumSize(new java.awt.Dimension(90, 20));
			ivjJCSpinFieldPercentReduction.setMinimumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}
			ivjJCSpinFieldPercentReduction.setDataProperties(
					new com.klg.jclass.field.DataProperties(
						new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(0), new Integer(100), null, true, 
						null, new Integer(1), "#,##0.###;-#,##0.###", false, 
						false, false, null, new Integer(100)), 
						new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
						new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
						new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));

			ivjJCSpinFieldPercentReduction.setValue( new Integer(100) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldPercentReduction;
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
 * Return the JLabelHowToStop property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelHowToStop() {
	if (ivjJLabelHowToStop == null) {
		try {
			ivjJLabelHowToStop = new javax.swing.JLabel();
			ivjJLabelHowToStop.setName("JLabelHowToStop");
			ivjJLabelHowToStop.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelHowToStop.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelHowToStop.setText("How to Stop Control:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelHowToStop;
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
 * Return the JLabelPercentReduction property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPercentReduction() {
	if (ivjJLabelPercentReduction == null) {
		try {
			ivjJLabelPercentReduction = new javax.swing.JLabel();
			ivjJLabelPercentReduction.setName("JLabelPercentReduction");
			ivjJLabelPercentReduction.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelPercentReduction.setText("Group Capacity Reduction %:");
			ivjJLabelPercentReduction.setMaximumSize(new java.awt.Dimension(112, 14));
			ivjJLabelPercentReduction.setPreferredSize(new java.awt.Dimension(112, 14));
			ivjJLabelPercentReduction.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelPercentReduction.setMinimumSize(new java.awt.Dimension(112, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPercentReduction;
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
			//ivjJPanelChangeMethod.setBorder(new com.ibm.uvm.abt.edit.DeletedClassView());
			ivjJPanelChangeMethod.setLayout(new java.awt.GridBagLayout());
			ivjJPanelChangeMethod.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
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
			constraintsJCSpinFieldChangeDuration.insets = new java.awt.Insets(1, 5, 3, 3);
			getJPanelChangeMethod().add(getJCSpinFieldChangeDuration(), constraintsJCSpinFieldChangeDuration);

			java.awt.GridBagConstraints constraintsJLabelMinutesChDur = new java.awt.GridBagConstraints();
			constraintsJLabelMinutesChDur.gridx = 3; constraintsJLabelMinutesChDur.gridy = 2;
			constraintsJLabelMinutesChDur.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelMinutesChDur.ipadx = 5;
			constraintsJLabelMinutesChDur.ipady = -2;
			constraintsJLabelMinutesChDur.insets = new java.awt.Insets(5, 4, 5, 4);
			getJPanelChangeMethod().add(getJLabelMinutesChDur(), constraintsJLabelMinutesChDur);

			java.awt.GridBagConstraints constraintsJLabelChangePriority = new java.awt.GridBagConstraints();
			constraintsJLabelChangePriority.gridx = 4; constraintsJLabelChangePriority.gridy = 2;
			constraintsJLabelChangePriority.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelChangePriority.ipadx = -13;
			constraintsJLabelChangePriority.ipady = 6;
			constraintsJLabelChangePriority.insets = new java.awt.Insets(1, 4, 3, 2);
			getJPanelChangeMethod().add(getJLabelChangePriority(), constraintsJLabelChangePriority);

			java.awt.GridBagConstraints constraintsJCSpinFieldChangePriority = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldChangePriority.gridx = 5; constraintsJCSpinFieldChangePriority.gridy = 2;
			constraintsJCSpinFieldChangePriority.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJCSpinFieldChangePriority.ipadx = 29;
			constraintsJCSpinFieldChangePriority.ipady = 19;
			constraintsJCSpinFieldChangePriority.insets = new java.awt.Insets(1, 3, 3, 10);
			getJPanelChangeMethod().add(getJCSpinFieldChangePriority(), constraintsJCSpinFieldChangePriority);

			java.awt.GridBagConstraints constraintsJLabelChangeTriggerNumber = new java.awt.GridBagConstraints();
			constraintsJLabelChangeTriggerNumber.gridx = 1; constraintsJLabelChangeTriggerNumber.gridy = 3;
			constraintsJLabelChangeTriggerNumber.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelChangeTriggerNumber.ipadx = -45;
			constraintsJLabelChangeTriggerNumber.ipady = 6;
			constraintsJLabelChangeTriggerNumber.insets = new java.awt.Insets(4, 5, 21, 5);
			getJPanelChangeMethod().add(getJLabelChangeTriggerNumber(), constraintsJLabelChangeTriggerNumber);

			java.awt.GridBagConstraints constraintsJLabelChangeTriggerOffset = new java.awt.GridBagConstraints();
			constraintsJLabelChangeTriggerOffset.gridx = 4; constraintsJLabelChangeTriggerOffset.gridy = 3;
			constraintsJLabelChangeTriggerOffset.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelChangeTriggerOffset.ipadx = -63;
			constraintsJLabelChangeTriggerOffset.insets = new java.awt.Insets(8, 4, 23, 12);
			getJPanelChangeMethod().add(getJLabelChangeTriggerOffset(), constraintsJLabelChangeTriggerOffset);

			java.awt.GridBagConstraints constraintsJTextFieldChangeTriggerOffset = new java.awt.GridBagConstraints();
			constraintsJTextFieldChangeTriggerOffset.gridx = 5; constraintsJTextFieldChangeTriggerOffset.gridy = 3;
			constraintsJTextFieldChangeTriggerOffset.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJTextFieldChangeTriggerOffset.weightx = 1.0;
			constraintsJTextFieldChangeTriggerOffset.ipadx = 26;
			constraintsJTextFieldChangeTriggerOffset.insets = new java.awt.Insets(4, 3, 21, 10);
			getJPanelChangeMethod().add(getJTextFieldChangeTriggerOffset(), constraintsJTextFieldChangeTriggerOffset);

			java.awt.GridBagConstraints constraintsJCSpinFieldChangeTriggerNumber = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldChangeTriggerNumber.gridx = 2; constraintsJCSpinFieldChangeTriggerNumber.gridy = 3;
			constraintsJCSpinFieldChangeTriggerNumber.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJCSpinFieldChangeTriggerNumber.ipadx = 34;
			constraintsJCSpinFieldChangeTriggerNumber.ipady = 19;
			constraintsJCSpinFieldChangeTriggerNumber.insets = new java.awt.Insets(4, 5, 21, 3);
			getJPanelChangeMethod().add(getJCSpinFieldChangeTriggerNumber(), constraintsJCSpinFieldChangeTriggerNumber);

			java.awt.GridBagConstraints constraintsJLabelWhenChange = new java.awt.GridBagConstraints();
			constraintsJLabelWhenChange.gridx = 1; constraintsJLabelWhenChange.gridy = 1;
			constraintsJLabelWhenChange.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelWhenChange.ipadx = 3;
			constraintsJLabelWhenChange.ipady = 4;
			constraintsJLabelWhenChange.insets = new java.awt.Insets(4, 5, 4, 5);
			getJPanelChangeMethod().add(getJLabelWhenChange(), constraintsJLabelWhenChange);

			java.awt.GridBagConstraints constraintsJComboBoxWhenChange = new java.awt.GridBagConstraints();
			constraintsJComboBoxWhenChange.gridx = 2; constraintsJComboBoxWhenChange.gridy = 1;
			constraintsJComboBoxWhenChange.gridwidth = 4;
			constraintsJComboBoxWhenChange.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJComboBoxWhenChange.weightx = 1.0;
			constraintsJComboBoxWhenChange.ipadx = 79;
			constraintsJComboBoxWhenChange.insets = new java.awt.Insets(4, 5, 1, 17);
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
 * Insert the method's description here.
 * Creation date: (7/15/2002 1:48:13 PM)
 * @return com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupExpressStatEditorPanel
 */
public com.cannontech.dbeditor.wizard.device.lmprogram.LMExpressStatEditorPanel getStatEditorPanel() {
	if(statEditorPanel == null)
		statEditorPanel = new com.cannontech.dbeditor.wizard.device.lmprogram.LMExpressStatEditorPanel();
	
	return statEditorPanel;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	LMProgramDirectGear gear = null;
	
	gear = (LMProgramDirectGear)o;
	
	if( getJComboBoxHowToStop().getSelectedItem() != null )
	{
		gear.setMethodStopType( 
			com.cannontech.common.util.StringUtils.removeChars( ' ', getJComboBoxHowToStop().getSelectedItem().toString() ) );
	}

	gear.setPercentReduction( new Integer( ((Number)getJCSpinFieldPercentReduction().getValue()).intValue() ) );
	
	gear.setChangeCondition( getChangeCondition(getJComboBoxWhenChange().getSelectedItem().toString()) );
	
	gear.setChangeDuration( new Integer( ((Number)getJCSpinFieldChangeDuration().getValue()).intValue() * 60 ) );
	gear.setChangePriority( new Integer( ((Number)getJCSpinFieldChangePriority().getValue()).intValue() ) );
	gear.setChangeTriggerNumber( new Integer( ((Number)getJCSpinFieldChangeTriggerNumber().getValue()).intValue() ) );
	
	if( getJTextFieldChangeTriggerOffset().getText() == null || getJTextFieldChangeTriggerOffset().getText().length() <= 0 )
		gear.setChangeTriggerOffset( new Double(0.0) );
	else
		gear.setChangeTriggerOffset( Double.valueOf(getJTextFieldChangeTriggerOffset().getText()) );

	com.cannontech.database.data.device.lm.ThermostatSetbackGear ts = (com.cannontech.database.data.device.lm.ThermostatSetbackGear)gear;

	if(getStatEditorPanel().getJCheckBoxDeltaB().isSelected())
		ts.setValueB(Integer.valueOf(getStatEditorPanel().getJTextFieldDeltaB().getText()));
	if(getStatEditorPanel().getJCheckBoxDeltaD().isSelected())
		ts.setValueD(Integer.valueOf(getStatEditorPanel().getJTextFieldDeltaD().getText()));
	if(getStatEditorPanel().getJCheckBoxDeltaF().isSelected())
		ts.setValueF(Integer.valueOf(getStatEditorPanel().getJTextFieldDeltaF().getText()));
	if(getStatEditorPanel().getJCheckBoxRand().isSelected())
		ts.setRandom(Integer.valueOf(getStatEditorPanel().getJTextFieldRand().getText()));
	if(getStatEditorPanel().getJCheckBoxMax().isSelected())
		ts.setMaxValue(Integer.valueOf(getStatEditorPanel().getJTextFieldMax().getText()));
	if(getStatEditorPanel().getJCheckBoxMin().isSelected())
		ts.setMinValue(Integer.valueOf(getStatEditorPanel().getJTextFieldMin().getText()));
	if(getStatEditorPanel().getJCheckBoxTa().isSelected())
		ts.setValueTa(Integer.valueOf(getStatEditorPanel().getJTextFieldTa().getText()));
	if(getStatEditorPanel().getJCheckBoxTb().isSelected())
		ts.setValueTb(Integer.valueOf(getStatEditorPanel().getJTextFieldTb().getText()));
	if(getStatEditorPanel().getJCheckBoxTc().isSelected())
		ts.setValueTc(Integer.valueOf(getStatEditorPanel().getJTextFieldTc().getText()));
	if(getStatEditorPanel().getJCheckBoxTd().isSelected())
		ts.setValueTd(Integer.valueOf(getStatEditorPanel().getJTextFieldTd().getText()));
	if(getStatEditorPanel().getJCheckBoxTe().isSelected())
		ts.setValueTe(Integer.valueOf(getStatEditorPanel().getJTextFieldTe().getText()));
	if(getStatEditorPanel().getJCheckBoxTf().isSelected())
		ts.setValueTf(Integer.valueOf(getStatEditorPanel().getJTextFieldTf().getText()));

	if(getStatEditorPanel().isAbsolute)
		ts.getSettings().setCharAt(0, 'A');
	else
		ts.getSettings().setCharAt(0, 'D');

	if(getStatEditorPanel().isCelsius)
		ts.getSettings().setCharAt(1, 'C');
	else
		ts.getSettings().setCharAt(1, 'F');

	if(getStatEditorPanel().getJCheckBoxHeatMode().isSelected())
		ts.getSettings().setCharAt(2, 'H');
	else
		ts.getSettings().setCharAt(2, '-');

	if(getStatEditorPanel().getJCheckBoxCoolMode().isSelected())
		//I for "icy goodness"
		ts.getSettings().setCharAt(3, 'I');
	else
		ts.getSettings().setCharAt(3, '-');

				
	return ts;
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
 * Insert the method's description here.
 * Creation date: (7/15/2002 2:01:07 PM)
 * @return java.awt.GridBagConstraints
 */
private java.awt.GridBagConstraints holderConstraints() {
	/* This is annoying, but a call to this method must always be in the 
	initialize of ThermostatPreOperateGear to bring in the proper constraints 
	for the ExpressStat panel.
	*/
	java.awt.GridBagConstraints constraintsLMGroupExpressStatEditor = new java.awt.GridBagConstraints();
			constraintsLMGroupExpressStatEditor.gridx = 1; constraintsLMGroupExpressStatEditor.gridy = 1;
			constraintsLMGroupExpressStatEditor.gridwidth = 2;
			constraintsLMGroupExpressStatEditor.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsLMGroupExpressStatEditor.weightx = 1.0;
			constraintsLMGroupExpressStatEditor.weighty = 1.0;
			constraintsLMGroupExpressStatEditor.ipadx = -17;
			constraintsLMGroupExpressStatEditor.insets = new java.awt.Insets(4, 3, 5, 34);
	return constraintsLMGroupExpressStatEditor;
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
	getJCSpinFieldPercentReduction().addValueListener(this);
	getJComboBoxWhenChange().addActionListener(this);
	getJTextFieldChangeTriggerOffset().addCaretListener(this);
	getJComboBoxWhenChange().addActionListener(this);
	getJComboBoxHowToStop().addActionListener(this);
	getJTextFieldChangeTriggerOffset().addCaretListener(this);
	getStatEditorPanel().addDataInputPanelListener(this);
	
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
		setName("ThermostatPreOperateGearPanel");
		setPreferredSize(new java.awt.Dimension(402, 430));
		setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
		setLayout(new java.awt.GridBagLayout());
		setSize(402, 430);

		java.awt.GridBagConstraints constraintsJLabelHowToStop = new java.awt.GridBagConstraints();
		constraintsJLabelHowToStop.gridx = 1; constraintsJLabelHowToStop.gridy = 1;
		constraintsJLabelHowToStop.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJLabelHowToStop.ipadx = 4;
		constraintsJLabelHowToStop.insets = new java.awt.Insets(280, 10, 10, 61);
		add(getJLabelHowToStop(), constraintsJLabelHowToStop);

		java.awt.GridBagConstraints constraintsJComboBoxHowToStop = new java.awt.GridBagConstraints();
		constraintsJComboBoxHowToStop.gridx = 2; constraintsJComboBoxHowToStop.gridy = 1;
		constraintsJComboBoxHowToStop.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJComboBoxHowToStop.weightx = 1.0;
		constraintsJComboBoxHowToStop.ipadx = 24;
		constraintsJComboBoxHowToStop.insets = new java.awt.Insets(280, 13, 3, 52);
		add(getJComboBoxHowToStop(), constraintsJComboBoxHowToStop);

		java.awt.GridBagConstraints constraintsJLabelPercentReduction = new java.awt.GridBagConstraints();
		constraintsJLabelPercentReduction.gridx = 1; constraintsJLabelPercentReduction.gridy = 2;
		constraintsJLabelPercentReduction.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJLabelPercentReduction.ipadx = 53;
		constraintsJLabelPercentReduction.ipady = 3;
		constraintsJLabelPercentReduction.insets = new java.awt.Insets(4, 10, 5, 12);
		add(getJLabelPercentReduction(), constraintsJLabelPercentReduction);

		java.awt.GridBagConstraints constraintsJCSpinFieldPercentReduction = new java.awt.GridBagConstraints();
		constraintsJCSpinFieldPercentReduction.gridx = 2; constraintsJCSpinFieldPercentReduction.gridy = 2;
		constraintsJCSpinFieldPercentReduction.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJCSpinFieldPercentReduction.ipadx = 48;
		constraintsJCSpinFieldPercentReduction.insets = new java.awt.Insets(4, 13, 2, 114);
		add(getJCSpinFieldPercentReduction(), constraintsJCSpinFieldPercentReduction);

		java.awt.GridBagConstraints constraintsJPanelChangeMethod = new java.awt.GridBagConstraints();
		constraintsJPanelChangeMethod.gridx = 1; constraintsJPanelChangeMethod.gridy = 3;
		constraintsJPanelChangeMethod.gridwidth = 2;
		constraintsJPanelChangeMethod.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJPanelChangeMethod.weightx = 1.0;
		constraintsJPanelChangeMethod.weighty = 1.0;
		constraintsJPanelChangeMethod.insets = new java.awt.Insets(3, 5, 18, 62);
		add(getJPanelChangeMethod(), constraintsJPanelChangeMethod);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	add(getStatEditorPanel().getJPanelData(), holderConstraints());
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
		ThermostatSetbackGearPanel aThermostatSetbackGearPanel;
		aThermostatSetbackGearPanel = new ThermostatSetbackGearPanel();
		frame.setContentPane(aThermostatSetbackGearPanel);
		frame.setSize(aThermostatSetbackGearPanel.getSize());
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
 * Insert the method's description here.
 * Creation date: (7/15/2002 2:54:59 PM)
 * @param newStatEditorPanel javax.swing.JPanel
 */
public void setStatEditorPanel(com.cannontech.dbeditor.wizard.device.lmprogram.LMExpressStatEditorPanel newStatEditorPanel) {
	statEditorPanel = newStatEditorPanel;
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

	getJComboBoxHowToStop().setSelectedItem( StringUtils.addCharBetweenWords( ' ', gear.getMethodStopType() ) );

	getJCSpinFieldPercentReduction().setValue( gear.getPercentReduction() );
	
	setChangeCondition( gear.getChangeCondition() );
	
	getJCSpinFieldChangeDuration().setValue( new Integer( gear.getChangeDuration().intValue() / 60 ) );
	getJCSpinFieldChangePriority().setValue( gear.getChangePriority() );
	getJCSpinFieldChangeTriggerNumber().setValue( gear.getChangeTriggerNumber() );	
	getJTextFieldChangeTriggerOffset().setText( gear.getChangeTriggerOffset().toString() );

	com.cannontech.database.data.device.lm.ThermostatSetbackGear ts = (com.cannontech.database.data.device.lm.ThermostatSetbackGear)gear;
				
		
	if(ts.getValueB().intValue() != 0)
	{
		getStatEditorPanel().getJCheckBoxDeltaB().setSelected(true);
		getStatEditorPanel().getJTextFieldDeltaB().setText(ts.getValueB().toString());
	}
	if(ts.getValueD().intValue() != 0)
	{
		getStatEditorPanel().getJCheckBoxDeltaD().setSelected(true);
	getStatEditorPanel().getJTextFieldDeltaD().setText(ts.getValueD().toString());
	}
	
	if(ts.getValueF().intValue() != 0)
	{
		getStatEditorPanel().getJCheckBoxDeltaF().setSelected(true);
		getStatEditorPanel().getJTextFieldDeltaF().setText(ts.getValueF().toString());
	}
		if(ts.getRandom().intValue() != 0)
	{
		getStatEditorPanel().getJCheckBoxRand().setSelected(true);
		getStatEditorPanel().getJTextFieldRand().setText(ts.getRandom().toString());
	}
	if(ts.getMaxValue().intValue() != 0)
	{
		getStatEditorPanel().getJCheckBoxMax().setSelected(true);
		getStatEditorPanel().getJTextFieldMax().setText(ts.getMaxValue().toString());
	}

	if(ts.getMinValue().intValue() != 0)
	{
		getStatEditorPanel().getJCheckBoxMin().setSelected(true);
		getStatEditorPanel().getJTextFieldMin().setText(ts.getMinValue().toString());
	}
		
	if(ts.getValueTa().intValue() != 0)
	{
		getStatEditorPanel().getJCheckBoxTa().setSelected(true);
		getStatEditorPanel().getJTextFieldTa().setText(ts.getValueTa().toString());
	}
	
	if(ts.getValueTb().intValue() != 0)
	{
		getStatEditorPanel().getJCheckBoxTb().setSelected(true);
		getStatEditorPanel().getJTextFieldTb().setText(ts.getValueTb().toString());
	}
	
	if(ts.getValueTc().intValue() != 0)
	{
		getStatEditorPanel().getJCheckBoxTc().setSelected(true);
		getStatEditorPanel().getJTextFieldTc().setText(ts.getValueTc().toString());
	}

	if(ts.getValueTd().intValue() != 0)
	{
		getStatEditorPanel().getJCheckBoxTd().setSelected(true);
		getStatEditorPanel().getJTextFieldTd().setText(ts.getValueTd().toString());
	}

	if(ts.getValueTe().intValue() != 0)
	{
		getStatEditorPanel().getJCheckBoxTe().setSelected(true);
		getStatEditorPanel().getJTextFieldTe().setText(ts.getValueTe().toString());
	}

	if(ts.getValueTf().intValue() != 0)
	{
		getStatEditorPanel().getJCheckBoxTf().setSelected(true);
		getStatEditorPanel().getJTextFieldTf().setText(ts.getValueTf().toString());
	}
		
	if(ts.getSettings().charAt(0) == 'A')
	{
		getStatEditorPanel().getJButtonDeltasAbsolute().setText("Absolutes");
		getStatEditorPanel().getJCheckBoxDeltaB().setText("Abs B:");
		getStatEditorPanel().getJCheckBoxDeltaD().setText("Abs D:");
		getStatEditorPanel().getJCheckBoxDeltaF().setText("Abs F:");
		getStatEditorPanel().isAbsolute = true;
	}
	else
	{
		getStatEditorPanel().getJButtonDeltasAbsolute().setText("Deltas");
		getStatEditorPanel().getJCheckBoxDeltaB().setText("Delta B:");
		getStatEditorPanel().getJCheckBoxDeltaD().setText("Delta D:");
		getStatEditorPanel().getJCheckBoxDeltaF().setText("Delta F:");
		getStatEditorPanel().isAbsolute = false;
	}
		
	if(ts.getSettings().charAt(1) == 'C')
	{
		getStatEditorPanel().getJButtonFahrenheitCelsius().setText("Celsius");
		getStatEditorPanel().isCelsius = true;
	}

	else
	{
		getStatEditorPanel().getJButtonFahrenheitCelsius().setText("Fahrenheit");
		getStatEditorPanel().isCelsius = false;
	}
		
	getStatEditorPanel().getJCheckBoxHeatMode().setSelected(ts.getSettings().charAt(2) == 'H');

	//I for "icy goodness"
	getStatEditorPanel().getJCheckBoxCoolMode().setSelected(ts.getSettings().charAt(3) == 'I');

}

public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	//fire this event for all JCSpinFields!!
	this.fireInputUpdate();
}
/* (non-Javadoc)
 * @see com.cannontech.common.gui.util.DataInputPanelListener#inputUpdate(com.cannontech.common.editor.PropertyPanelEvent)
 */
public void inputUpdate(PropertyPanelEvent event) {
	fireInputUpdate();	
}
}
