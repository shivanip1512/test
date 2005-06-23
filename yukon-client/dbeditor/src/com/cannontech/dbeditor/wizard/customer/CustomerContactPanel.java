package com.cannontech.dbeditor.wizard.customer;
/**
 * This type was created in VisualAge.
 */
import java.util.List;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.CustomerFuncs;
import com.cannontech.database.data.customer.Customer;
import com.cannontech.database.data.lite.LiteContact;

public class CustomerContactPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
	private javax.swing.JButton ivjJButtonAdd = null;
	private javax.swing.JButton ivjJButtonRemove = null;
	private javax.swing.JScrollPane ivjJScrollPaneContactTable = null;
	private javax.swing.JTable ivjJTableContact = null;
	private javax.swing.JLabel ivjJLabelAssignedContacts = null;
	private javax.swing.JComboBox ivjJComboBoxContacts = null;
	private javax.swing.JLabel ivjJLabelAvaillContacts = null;
	
	private static final String STR_NONE_AVAIL = "  (none available)";

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CustomerContactPanel() {
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
	if (e.getSource() == getJButtonAdd()) 
		connEtoC3(e);
	if (e.getSource() == getJButtonRemove()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}


/**
 * connEtoC3:  (JButtonAdd.action.actionPerformed(java.awt.event.ActionEvent) --> LMControlAreaTriggerPanel.jButtonAdd_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonAdd_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (JButtonRemove.action.actionPerformed(java.awt.event.ActionEvent) --> LMControlAreaTriggerPanel.jButtonRemove_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GAF09C4AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DD8D5D5367646A966535758C46FF3FC3E490AE6EC06D71AB4121A8F158AFFCAAA1D5450680D165FFCCD43AB6B754D33BC5C4B85B4FFC8C9401F3A01A9A0DCAE28C5E4AA3F02B1C90288BEE77240BD40554BBD57F38FF0C1DE3D35764F39074B39B878CDFEAD7659EB6F3D76DAEB2F3D76DAFB5F04740F26D892CD690210E891583F4F9261CF29A0BC7733E79E63B86B034DA9026E5F47G6F8BF579
	C9D05A212DF6DDF3CA1AF0CBD6AA542B20BEB63EB9A5833EF78937E69DAD010FA073EC05362DDBED91A34FD3134B67C9A175105E2DD0EE8728851C8BA072C11CFFCAEFA643CF007AF6760D10E89204B18FE11F5E56CC9DFEA36B5784EDDDG8726E11F593D7BD8FB8C6030CFBC149B9E22FD37C21F4D043B9F171532FE176E18AAECD8D4E05A06F36968F3F0CD23E3A73052AAC490C518055B4B6A20BC5EF03573C43B5CEDEB975DEE592DCA6DDDB6C71B64F02A32E26BF38E080A436610FA1D6D122D3D472B4A5D
	12D2D6D6493EAAE100582EB608EE4905ECCDC799A63726927AA0834CB91E6052AF225E2A607B9200BA1D4B714C36630C4F502BD305787BBE8AAAC76CF44A7F0669FEB96CB1F1C9E18FA37D35E4DDF05CD1283BGA8AFD5FD1C17758FF3DEE6B5E2DF6B0C6614D20006999CFFCB93623B20BE86E0628C4E631707B80FAD57E78AF3AA47256E8842633499143795B37CBC064D0ACB4AA2367307BCDC7F74881897209E4083B081E09A40DEE233776766C3D95E26D64A5D5D323B5C6329F0B9143F488E49855FE5E550
	42F06724F6D9F18882354BFBDA8B8C78619093B154A69128EC9784EEC761679E9442DF9F17EAB110ADE2394D9447E8A4C478E5BB70886AAD042CED9AFC63B89CCF71F792FC178ECF57EA8F41E3FD905A629F61FAEE594EF929CF3AD7C8AB7E4614E2404B4479B5262A5FD29EAE7D564F8B55F174E154F1886043G66GAC8358G10BE136B78CF39C7EE2463BA58870ADBF4B57634F9C4C52D68F13A9C1242F4ED6A9BC957C6C047250DFA9C554B54A37AB50A1B51E02817382947CC7619D49F35B37DFA21F35785
	1D3BEB66F05B582E2D210D6C094119F48D4D9A3E1460476B70F4ED23485E427AE4E893A2412F117EEF127E0E48C09B914EA35E9799E8A337B5F19B998FED89GF4FD5B8A51BFB6BD8AFB87A084E08AC094C09CC04223FC0D4B720A7E4EFD5420DBDFBA760D3C5DD0CABED5916B5CDED5F437CB4D2262945BDC12D7E0FDB66621DE5B060CCB4CC75BEB07B9CECBEEC991D5276C866AE23784466425FD36643F0FFD940934B6B93BA518205BA3F03ABB73F7C169923D6AF90FC3D4A5FF8F5A3EA79FED22D7EC860AC0
	G3ECF4DC2FDC5921F1B865F8533382EFEEAC35CD828CF1E4575F2D77ECEA83F826316DCE5E5D54EF6E4C3D47A390D5EFE9147CC037E8D33793EFC2D055BDF5536FF978A161FB534BF0437E7D3DBCA1B93E8FFBFA7F4CB815FB2071F25560BAD235C8F4817913EE176F0BC1D6373D14FE18E02379860281E5FA9A2B68CF20582C44C613A0E4D37D29B2C903D92373A338A980C6470EB1E76FD6AB01EA9D22F64D62BA40F64F64007172D7932431B7D7658A1EB36320A6051BFD50241F8B5FC7C6177A99EE8682D1B
	372FA76D222AAA4E369ED5EA14D455696E740FCFA03CF80738BEAF13F5139DE5DD82B22E855820495ADF30CBE7D77532FC35475304BB065967E5F28E27BE561CE2FD0C2F7F51A60C2D2A347A89D27737BF519838F658F7B4EBF9E31F7C6230B9022D7F4D4F71BFCD294CFED2B4DC72F944B9C01E711A3C7F5C246705777F4772724BA1F3180358754D823585D7E1F0DB76969A5BF2FF2131ADFFDFB8322D5ED6E8E42B346D76C214CD6EF36BEC7F4EC274FF6D325BAD118D288D91E8CC7CDA934A61F4BB558BCEC7
	2724B6BA87A4B8DB9FC39BDECAFCEC84FC47BC46FDE3DF8E770D87869E90223E4DB79945CF936F2FB3D967D25BBDBA576F9BA9CF21B65CD312CB6A86C5D54ABDEE1562A7917F4D70DEB341D5660462585CB395BD2A8A671A43D1A092C1EC34E810148ED969169C39D744DE51A67629B6329036F252DC0D5FF956424293D623903E600AC6765B72GFA754189FE11571CD7C2797B5CBCC94F5FB929DB6E15FE8896A96551F2F9047B6C129254F9932CC5974018395C0F6DAE42F66E4718332E14DDAEEAAE5036F6AE
	5A42620B697A751DCBF3416DDA7BFF87ED2773186DE9E83752359E89428BF414DFC97DBCC6306F8CE8EB96DDBD7E58E3277DBDA8DDBA9FCB7116E2E3BBFA2F987D6AD50D7E0E62E481E388CA135A7DB491E9FA94E7B79CC9F8E8301C352FCF1F33967C8764AB1877C2B986002EF95AC91455E831DA22C82CF81E673493E14CB400D8C6A7AD0A6A04765B28758B037A1CA8FF3FB8566F40BC6C773216DF96E938F7882E981C5C1F4CBCEE7ECB1ABEC79E7B7A00615EC4681AC7698F4E8B4CA73E5B0DF4A783BEFC9E
	37111F946B4F3AF22FD7EEF712F00E2DC3E909FE5FD25C17A5DB206CF0AADE48AD9C120F2FE5E589720A719EC33375784D61AF056171EBB5494AF9A8D159C76C3AC92E041033964C21D2F3EB4DCE29CF60FA783F9AFD0EBDE747E726770268A10A499F2F5343F148DFA9CFBFAF433C49683E201DB0E96B6CF15AFAD4A78616228A7E5D5343EF86C8FE7D920981709C324E67B9F766F534599628FBGC2GE87CAF1E67717F94402DG207B6A3EA47D3ECADBC0630952855C96E34E63154F687D8D577B1F8BBE0772
	265D202673FBF71DE2BCAEF970DE61787719A623BB03109D59267A85D4576A823F4E2D057D2658CB2B595D46A74DDC97D150676D85DC97ABADC63A30C7B727D0D9CAF30F7BE539A9FF496E88CA747BA8AA7E53207B28AA1A6D676840FD541609EB2F82BEA41A6F235F173C2F5BC76740F2C5F7A7F8B33EB7FE345BCC7D96BF6BF7DB385F92389F3FBB8BF5B099E8C68244822C87C8FD1C4F31BD630639FE35EF04DC7FE512B767BE6E0FBB0F6B3E9B9E9F9E0F523CF68231EB4763B42F5D26616FA678909D1E2E71
	07840F75F0E84BFC0247041D6783737631CF8C1DF3CAC01D0E6B99B66E4300FAF440B8EAB3BB9A33789EC74B70593CFD90EF151531739C4D016C5BD5C9FCAF47BD41734DB777207FCEFF92EC9020964081908290869073A4DF133FE698793621EB924C3179F399AA67FF355C1CFEB68D9B97281F75CF8E9DC765CECB66F28BB1DC6EE37BC8EE8B755A98AE632C0CEDBA5BEE101DEED5336B4ECCBCFFE5455969F4EBB1B341354B8A89D6A96ED7D941636887330C636808AC63B8FAD65648B9617C2CE0F1B64D5F5E
	A13E3B856473GD01C430738C128CF50F06B7AC90C8C75A5B15C067F65BA469089D0CFDF8836815084208204AE64FD6EA7FD429732F39F4A840034C5B4DE20FD8EDC3F9C30C65B99DDAC11B616CD8B072E1BBD209E3A50B807D4991E4EF552F0AE1E9B721CE9F3C0FBE03E364D008642FB2C5CCFE8AE5C3D032C0D57ABF909616369495ADED1BF908C72152F45EE097133CB7845794D2CCF35AAD79E51E4F8B1A950466D204F2AC54CC7AF9A2E87EA8B1B7684373B643D46F637F5AF4A6A15FB14F6A95036F66D4D
	545A7CBB39CE153AD18C5AE79F992F0A8A64DC0163F348F85A96ECFC415EE076CBF5145B2A5751D1CDC79FA4876A4885FA9943F4939AD4C7E57B50FF7B347A89D26F5766CB77619A2B326CEAF2FA1AC0DF4254C5783E74DA362075396E43BD6F927B659EF08E2796637E7919CF9FEF3F8AE352811F3B1872C1FD7474BE245D21085DA0E223C7EA9776ADA171F3BF9F9B827DA7834C81088358381847C7197B71ED6CCCE33B82B98D86E7ACDC27F9846DF3328257E2C88627711C1709F723E76A45B6F056DE2F3353
	0D6991A16165FDC287527DFDFAC5274B45FB3035B806EBF5863CFF1BDCA17B7C435978468BDB7C73C38C465F4FDE382E570DE3890DF7A6C099360447C45F346AE322B92CCF3C56DE1AED543EC1EBBF6D513727BE45E22A2738FFFB0B1CFBD5D0EF51F0E596443920BE28617E55427DE00806ABC866E3D7E83897D27858D7B55C29D4BE766D2778BD43527F5547E0E9138E1A505E0CE2305A27199FFCBA703D2F78B21E4F5A590ED1DFD959E945692890BB6B299512B3AE1278F5249D72B40F532B7AB1BE09027A8A
	000427B9DF3E03BC37887F653DC27D188643773B78264FCC29CBA9BF85CB7DFCC5C2EEC1674C9E60F3F6C17B142508EB98502F855A4623A6C130C1C95B7FE9C8BB5A947ACCFFFB4410F664375C2CEFFFF9C8BB6A68909BCFE5DB7C8917ED6362E9026B1193C15FA657B319364652B226A7C7F8753655ACEF6A2C5EAF9846D47EDD085391BD6C9A6471C5D5AC577D9FCC648D866A21G6131BC3EFF23A35318AE59F914DEC8AB27979D4B5FC465EC0E5B28516AF46DB52645F7A9636FBB8F3F4BBC7A8C77A915442E
	3B5D60A95C4E7641E7305FC7447E9720EDEB7DDECF41FB88951CA17505EF9DC03163C97F07ADD586FA551F2B96E68388D1B086A1072D47B2783E403E697C9789CF6BGF21FEDCE2173E59D744FF7A175C673250CB0DFA950BC443EE98C788421CD4F02121CCF20EC962F98E66DE7C03DE75B2EC868F3BB85FE8E6D6D9F6D5BA04DA7A2493E2CFA166693099AFE9E41B76870B44FB8534F5F49AFC1DB5433BC06FD77905A52016740878134GF8GA6G04BF47E35A019C3BF003383DC97654C36060520518D43EC3
	83B77776B9AF056F4108D93F96A2EFFDB7B1685D45F36C6E021517559C0227725DC15E5E5316B52794GB4GF4818C8204AE63724D4FA920726102152BF0A8F5119B9CAE9CDE2A7851F870D259B28739ECD301561CE5C3E30658E541F2443F19F1AD56AE8BFCDB6DA2788D4B825FD693CC7CEDB5E9B9703FDCFBB79872763FE4B95383ABB7AC8F76B626E66B47257E0E765B47CA7B6F020D7B118747D59934133852GCF777F2796AC8F0907585ACE69531FE925DB0E873DD7F0B11AE3D8F974D7B942C704A670AD
	787F51FD7E4AE1C1331F1736F27B59771B7B042A675FCD4C89B2C7A4231D7A3C7FFCCAFFF3F592657DDAB261DDB944667826D05074B1375F7F7D246E3B21FF34EF0F2381BA0773871D036E0B145C60794076DC63FCA0BD57B88F5D13BB72FBCEF6EE70771C3CDC7DFB8E5D579F5B708CBF85EBF689E08CC0A8405467791E1E190B81B73E768F5F4F550ECE8CFF7105DF6573CF4A8BAE6BBDFF476F82FE1D0731EB1B8C21EBB7526778E96477822A6CA1C80AFBA48FF524B8BB3BD45A1561E7671118D06AA0E89A93
	961138A09A64DA7FBC0F934B49993B916A87D6F05CE4AD6EACDD417D03430C67EE08D6F713FA18D6FF4BB4DA9BC2DA2333539D66FF54BB3C91D05943C82C93833C2D50747152E9AD3E56F09535DAFC2D6146111828F6A51CFDABB9CE4DA0E321BE2861D6E6E8F13D061BC4E22295D0CF50F00957F83F0DAB7999FB2288F1752B40F72F6276D91F0F4E4AB0D6GE403A2FBA4C56D27D8728A446CFE400E71CB885089830881088758G30758548D9G0E825482DC8670810C8718829081908D3082605597B89F8585
	F9A37041128432D994D1FF1715E78B3EDF6C369B6F174F699BA758F9D33F076572A7EC64FE2C2343ABE97B32521626616A3C8D325709F7G54B7578CE2CE5B8B0AF2360BAE9AD4D708CA036CF2367733F8F08F599744E94771F83445023A6C129567G262043C672D8D2A5E3714E72940C2571630C9163D5243FD9704710E3E15C44B89ABFE2BD9C3E5F04DC6627097CEC7CB20F645CA48DE7893140735E3B4D7A3CB7924662BDD99C14891AAC33DBF1CFF740C84A73B3E4AF242EEECEC9DF4D635FE6FC9AE171EF
	EB6D0D7857475C0171EF95507CAB14BB844E7F2C6C217347127D54G7DAE2D66BC0E4B989A7F5FF1444FCFC2460D7851C3A0BF63E18E64651DCD1BB6513926FB0C6FDF2E9D734F19F9ED24B9396C3C1C385ABFA76242211E15C867E7B6DA046334330DEED756F626884FA97CDD01D3F4B98748F3E1A3F4F4F7325FAA58036F233B6CB75EC73F309B1FBBE17661674E83F663F367A13B7E5CE17749F614359376FF0B566FD18D572A6166921A083B60F428DD9ABE1A700578DA89A76478C5042FBE02EBE13825BADC
	AB7F6D139DFD81088C5B140FDDE9A73666F439E8FDB56963F403EB5074711F04C727C754717D0A06EB556C3658D28C25AE65356932F2DBF37539263A4A727AEF9BAB4F1D2D2F2FA8BF77EDF97D057256C6ED7CB84B27A30C2FBDFB2E6E62592F1B06D2E87C766B6A6A2AEAB03E61B1EC42C9A39C355DD01C0A5963156BC1F2FFFA11C7F275674D7C1DBCF2B53FCBB8E8419CA2816A29EB601CD843FD039943E4A6537253785BF73754E7559E87EBE0E367E617035139328B2C45705FD1BCAF685803689A59772A13
	FEBADD995946F49CC0C66E84C2F6C6080EFD67940E8DAE3F3582EBB41E08B30F14CF162E8916F3CDA272572F894C176FA37816B50179F20105674B8EE80BD9434F77B74C7CFCAFF81147D1D3ACFE7E9E50FD17D8FE78BC602B9FF48EFA3F3CA5091FC38DA03383E0ACC098C0740B7CEE491E446F71563E486F717E1694F80FF7C7227E9EEEC2D260BD9E0E77374FCF8A3C479BBAFEC3D260BD9E9FCFF5F3EC58BB480D007E7FA32B8EBD820EA67C0F02855C16AA184B9D8ED5C8269762DEC55821D9F59DF1BD713D
	C74A4C3F3F4FEC92126A04E46A0A6B04A47427F50205385BBA418C2E5867E1EA40BD076E516C3DA6FC279DB69AD6955E67D76CD6FEC92E8A3B4815BA6EF28DD9268AC56C5B8A670652EFD53BC4556A74DAC5BEDE158187887E3FF0E8B3223AD399A402D45B066C55476FB9767212B54DDE3310DCA7E461715EA8A96484139C78CA45FE06DCA3EC766093A77EDA8EC476124262D57BDDD23D105EF18EFF4A64D5A5C7B9BF9BC10E8F48CF8F0A7077A2C5352237CBC5113E2897125C7803E9E1A7EDF6096E4EA2FAD0
	FEC17416D8FE99F796D9A664F79761972958B4AA3BE66F786463B3EBA34D0229C6586212C5C78D28C6D66A044DEA17A2F93BE4174367B9F90A90CAA5AF1B98ADA481393D1A78A82C8DA3870D692B3EFF9B3FED7D369A885CFC0AD0C4FAE2B4E353FAB8AA3BC485261214BA95FFGAEAB873436DBBED48FBEF5BA6312C9C8E4C26292EC75F9D460CA915DDE7CC586F0E08D6000B7E904ECF7AE345F75401FAF92C27E212A7091D5998CAD6A14646E22720E8E27DB5288007E007CFE0DBC7631857459693B6303B34B
	B73541FAD78966EE3B1D7CADA6FFCB70EFB1419493CCF189A8B1C19871DFE03B0F98DE1ACA0DC45BC5384C369AD8A9F5559F975E7B427104D7C13A9A41C2FE6C814ACE73777A3A273BCDD2EE8D3FEB7FFB217163F0ADBCE8921CEEF207586DF47543C795B9E5B4BC0DABC8F5AF2E3C8F3F2CBEEDA3A432ED77857A98025D92E0EEF40E6D436C460C681D24B11D7C4D9835D6F0D496658DA7440F8FB591C8EB3E1F53E446A9F23CD3431AF4B2E02EC46F3F723E575F313D7D637DC16F58462F23E76614F5013F5DB9
	F598A50D837C5B6BF83EB8A19FF329E0F75C0D5D224B85995555F237836F3BAB7AD549FF47D3FD9869FBF99F564872E1721B76AB3AB17F8FD0CB8788C21932AAA695GGA8B8GGD0CB818294G94G88G88GAF09C4AEC21932AAA695GGA8B8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGE095GGGG
**end of data**/
}

/**
 * Return the JButtonAdd property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonAdd() {
	if (ivjJButtonAdd == null) {
		try {
			ivjJButtonAdd = new javax.swing.JButton();
			ivjJButtonAdd.setName("JButtonAdd");
			ivjJButtonAdd.setMnemonic('a');
			ivjJButtonAdd.setText("Add");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonAdd;
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
			ivjJButtonRemove.setMnemonic('r');
			ivjJButtonRemove.setText("Remove");
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
 * Return the JComboBoxPoint property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxContacts() {
	if (ivjJComboBoxContacts == null) {
		try {
			ivjJComboBoxContacts = new javax.swing.JComboBox();
			ivjJComboBoxContacts.setName("JComboBoxContacts");
			// user code begin {1}

			ivjJComboBoxContacts.setToolTipText("Contacts that are not already assigned to an existing customer");

			//only make the unassigned contacts available
			LiteContact[] contacts = ContactFuncs.getUnassignedContacts();
			for( int i = 0; i < contacts.length; i++ ) {
				getJComboBoxContacts().addItem( contacts[i] );
			}
			
			setEnableContactList();
			

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxContacts;
}

/**
 * Toggles our list to enabled/disabled and inserts a description string
 *
 */
private void setEnableContactList()
{
	boolean isEnabled = getJComboBoxContacts().getItemCount() > 0;
	getJComboBoxContacts().setEnabled( isEnabled );

	if( isEnabled )
		getJComboBoxContacts().removeItem( STR_NONE_AVAIL );
	else
		getJComboBoxContacts().addItem( STR_NONE_AVAIL );
}

/**
 * Return the JLabelExisTriggers property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAssignedContacts() {
	if (ivjJLabelAssignedContacts == null) {
		try {
			ivjJLabelAssignedContacts = new javax.swing.JLabel();
			ivjJLabelAssignedContacts.setName("JLabelAssignedContacts");
			ivjJLabelAssignedContacts.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJLabelAssignedContacts.setText("Assigned Contacts:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAssignedContacts;
}

/**
 * Return the JLabelPoint property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAvaillContacts() {
	if (ivjJLabelAvaillContacts == null) {
		try {
			ivjJLabelAvaillContacts = new javax.swing.JLabel();
			ivjJLabelAvaillContacts.setName("JLabelAvaillContacts");
			ivjJLabelAvaillContacts.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelAvaillContacts.setText("Available Additional Contacts:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAvaillContacts;
}

/**
 * Return the JScrollPaneTriggerTable property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneContactTable() {
	if (ivjJScrollPaneContactTable == null) {
		try {
			ivjJScrollPaneContactTable = new javax.swing.JScrollPane();
			ivjJScrollPaneContactTable.setName("JScrollPaneContactTable");
			ivjJScrollPaneContactTable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneContactTable.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			getJScrollPaneContactTable().setViewportView(getJTableContact());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneContactTable;
}

/**
 * Return the JTableTrigger property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableContact() {
	if (ivjJTableContact == null) {
		try {
			ivjJTableContact = new javax.swing.JTable();
			ivjJTableContact.setName("JTableContact");
			getJScrollPaneContactTable().setColumnHeaderView(ivjJTableContact.getTableHeader());
			ivjJTableContact.setBounds(0, 0, 200, 200);
			// user code begin {1}

			ivjJTableContact.getSelectionModel().setSelectionMode( javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
			ivjJTableContact.setModel( new CustomerContactTableModel() );

			//do any column specific things here
			javax.swing.table.TableColumn name = ivjJTableContact.getColumnModel().getColumn(CustomerContactTableModel.COLUMN_NAME);
			name.setPreferredWidth(30);
			javax.swing.table.TableColumn login = ivjJTableContact.getColumnModel().getColumn(CustomerContactTableModel.COLUMN_LOGIN);
			login.setPreferredWidth(30);
			javax.swing.table.TableColumn notif = ivjJTableContact.getColumnModel().getColumn(CustomerContactTableModel.COLUMN_NOTIFICATION);
			notif.setPreferredWidth(60);

			
/*			javax.swing.table.TableColumn primeContact = ivjJTableContact.getColumnModel().getColumn(CustomerContactTableModel.COLUMN_PRIME_CONTACT);
			primeContact.setPreferredWidth(40);
			
			// Create and add the column renderers	
			com.cannontech.common.gui.util.CheckBoxTableRenderer cbRender = new com.cannontech.common.gui.util.CheckBoxTableRenderer();
			cbRender.setHorizontalAlignment(javax.swing.JCheckBox.CENTER);
			primeContact.setCellRenderer(cbRender);

			// Create and add the column CellEditors
			javax.swing.JCheckBox chkBox = new javax.swing.JCheckBox();
			chkBox.setHorizontalAlignment(javax.swing.JCheckBox.CENTER);
			chkBox.setBackground(ivjJTableContact.getBackground());
			primeContact.setCellEditor( new javax.swing.DefaultCellEditor(chkBox) );

		
			//javax.swing.ButtonGroup group = new javax.swing.ButtonGroup();
			//group.add(chkBox);
*/			
	
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableContact;
}


/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 5:10:19 PM)
 * @return com.cannontech.dbeditor.wizard.device.lmcontrolarea.TriggerTableModel
 */
private CustomerContactTableModel getJTableModel() 
{
	return (CustomerContactTableModel)getJTableContact().getModel();
}

/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	Customer customer = (Customer)o;

	//create some storage for the IDs
	int[] cstCntIDs = new int[ getJTableModel().getRowCount() ];

	for( int i = 0; i < getJTableModel().getRowCount(); i++ )
	{
		//build a totally new CustomerContact object
		LiteContact cnt = getJTableModel().getRowAt(i);
		cstCntIDs[i] = cnt.getContactID();
	}

	customer.setCustomerContactIDs( cstCntIDs );
		
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
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJButtonAdd().addActionListener(this);
	getJButtonRemove().addActionListener(this);
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CustomerContactPanel");
		setToolTipText("");
		setLayout(new java.awt.GridBagLayout());
		setSize(416, 348);

		java.awt.GridBagConstraints constraintsJScrollPaneContactTable = new java.awt.GridBagConstraints();
		constraintsJScrollPaneContactTable.gridx = 1; constraintsJScrollPaneContactTable.gridy = 4;
		constraintsJScrollPaneContactTable.gridwidth = 3;
		constraintsJScrollPaneContactTable.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneContactTable.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollPaneContactTable.weightx = 1.0;
		constraintsJScrollPaneContactTable.weighty = 1.0;
		constraintsJScrollPaneContactTable.ipadx = 378;
		constraintsJScrollPaneContactTable.ipady = 199;
		constraintsJScrollPaneContactTable.insets = new java.awt.Insets(1, 8, 17, 8);
		add(getJScrollPaneContactTable(), constraintsJScrollPaneContactTable);

		java.awt.GridBagConstraints constraintsJButtonRemove = new java.awt.GridBagConstraints();
		constraintsJButtonRemove.gridx = 3; constraintsJButtonRemove.gridy = 3;
		constraintsJButtonRemove.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJButtonRemove.ipadx = 19;
		constraintsJButtonRemove.insets = new java.awt.Insets(3, 60, 5, 10);
		add(getJButtonRemove(), constraintsJButtonRemove);

		java.awt.GridBagConstraints constraintsJLabelAssignedContacts = new java.awt.GridBagConstraints();
		constraintsJLabelAssignedContacts.gridx = 1; constraintsJLabelAssignedContacts.gridy = 3;
		constraintsJLabelAssignedContacts.gridwidth = 2;
		constraintsJLabelAssignedContacts.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelAssignedContacts.ipadx = 46;
		constraintsJLabelAssignedContacts.ipady = 7;
		constraintsJLabelAssignedContacts.insets = new java.awt.Insets(9, 10, 1, 59);
		add(getJLabelAssignedContacts(), constraintsJLabelAssignedContacts);

		java.awt.GridBagConstraints constraintsJLabelAvaillContacts = new java.awt.GridBagConstraints();
		constraintsJLabelAvaillContacts.gridx = 1; constraintsJLabelAvaillContacts.gridy = 1;
		constraintsJLabelAvaillContacts.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelAvaillContacts.ipadx = 7;
		constraintsJLabelAvaillContacts.ipady = -2;
		constraintsJLabelAvaillContacts.insets = new java.awt.Insets(18, 10, 6, 1);
		add(getJLabelAvaillContacts(), constraintsJLabelAvaillContacts);

		java.awt.GridBagConstraints constraintsJComboBoxContacts = new java.awt.GridBagConstraints();
		constraintsJComboBoxContacts.gridx = 2; constraintsJComboBoxContacts.gridy = 1;
		constraintsJComboBoxContacts.gridwidth = 2;
		constraintsJComboBoxContacts.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxContacts.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxContacts.weightx = 1.0;
		constraintsJComboBoxContacts.ipadx = 140;
		constraintsJComboBoxContacts.insets = new java.awt.Insets(16, 1, 2, 10);
		add(getJComboBoxContacts(), constraintsJComboBoxContacts);

		java.awt.GridBagConstraints constraintsJButtonAdd = new java.awt.GridBagConstraints();
		constraintsJButtonAdd.gridx = 3; constraintsJButtonAdd.gridy = 2;
		constraintsJButtonAdd.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJButtonAdd.ipadx = 43;
		constraintsJButtonAdd.insets = new java.awt.Insets(3, 60, 3, 10);
		add(getJButtonAdd(), constraintsJButtonAdd);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	// user code end
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{

	return true;
}


/**
 * Comment
 */
public void jButtonAdd_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJComboBoxContacts().getSelectedItem() instanceof LiteContact )
	{
		LiteContact selCont = (LiteContact)getJComboBoxContacts().getSelectedItem();

		//add the select Contact to our table
		getJTableModel().addRow( selCont );
		
		//remove it from our list of available Contacts
		getJComboBoxContacts().removeItem( selCont );
		setEnableContactList();		
	}
	
		
	fireInputUpdate();
	return;
}


/**
 * Comment
 */
public void jButtonRemove_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJTableContact().getSelectedRow() >= 0 ) {				
		for( int i = (getJTableContact().getSelectedRows().length-1); i >= 0; i-- ) {
			
			//add the row to our unassigned list of Contacts
			int selRow = getJTableContact().getSelectedRows()[i];
			getJComboBoxContacts().addItem( getJTableModel().getRowAt(selRow) );

			//remove it from our table
			getJTableModel().removeRow( selRow );
			setEnableContactList();
		}
	}

	getJTableContact().clearSelection();
	fireInputUpdate();
	return;
}


/**
 * Comment
 */
public void jComboBoxPoint_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
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
		CustomerContactPanel aCustomerContactPanel;
		aCustomerContactPanel = new CustomerContactPanel();
		frame.setContentPane(aCustomerContactPanel);
		frame.setSize(aCustomerContactPanel.getSize());
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
	if( o == null )
		return;

	Customer customer = (Customer)o;

	for( int i = 0; i < customer.getCustomerContactIDs().length; i++ )
	{
		LiteContact liteContact = ContactFuncs.getContact( customer.getCustomerContactIDs()[i] );
		
		getJTableModel().addRow( liteContact );
	}

}
}