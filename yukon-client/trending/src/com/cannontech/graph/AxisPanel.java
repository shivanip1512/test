package com.cannontech.graph;

/**
 * Insert the type's description here.
 * Creation date: (1/4/2002 3:55:34 PM)
 * @author: 
 */

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class AxisPanel extends javax.swing.JPanel implements java.awt.event.ItemListener {
	private javax.swing.JPanel ivjLeftAxisPanel = null;
	private javax.swing.JPanel ivjRightAxisPanel = null;
	private javax.swing.JLabel ivjLeftMaxLabel = null;
	private javax.swing.JLabel ivjLeftMinLabel = null;
	private javax.swing.JLabel ivjRightMaxLabel = null;
	private javax.swing.JLabel ivjRightMinLabel = null;
	private javax.swing.JTextField ivjLeftMaxTextField = null;
	private javax.swing.JTextField ivjLeftMinTextField = null;
	private javax.swing.JTextField ivjRightMaxTextField = null;
	private javax.swing.JTextField ivjRightMinTextField = null;
	private javax.swing.JCheckBox ivjLeftAutoScalingCheckBox = null;
	private javax.swing.JCheckBox ivjRightAutoScalingCheckBox = null;
/**
 * AxisPanel constructor comment.
 */
public AxisPanel() {
	super();
	initialize();
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GA2DA47ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBCEBD0D4D71AEE0D261C091BA1B3E416E449ACC9588D29610729B83B4C14BB4BECB93BA6F19213188DDB436E188A2930AEEB4846C948441ADCE89E1A002232D912102465A1CFF9098A884AD3E9E8848521CDB0F2018B34B65DED77A5B4083A5FB967FE67DE3AEF831AF22D7AF26F799E67FB1C6F3C3E3EC4487AB2A4A5B1A9CB9092D384655F71C4C188E895041F0CCE9A90377973B87ECE7E9D8458A7
	6CEAC90667D6202D9E0EB3E48A379C3B203D835A66EF628C7F8B6F8704158EF3933C7851138F3444077625AC2E671291752451760B0569705C8310G381CAF628C13DF907D5B8A3395FC933477A86F84921384A11E72341932B578F005AFDAF94EFFC9F8D20AF294FAAD3489CFAFBC473ED47B5CCD2DDBD5DF2B34076E8F9436BC73EEEA865125699FE103A29B2BBC8561D6AAF15AA03C31AB9F1E8D3DD7A7CE0EBA264AC7C53B5DE117255149728917681C2C2FEF76D85D3D22DD32D90C4436AB26DDD4CE900C50
	E6F63C10512068893C9AA8C4BE364D2F9D41C626BF52285A91F4E6D31273A0693B3CA8DD119320DD8B40F4FEDB04BA57812EA61760EEF4905EB8E8A7C3BBA797EDE9ACC4DBAC579F9322C6BECD5B6B4796CFAE33A1A4D735A53029F057A7F40C7F63A09927E82089F9F106AC00DAG8B008760B31ADB214692F8B60F482D0E29A9073D5969EC310D393A9DE3128D5EA1F4A33202BBAF0DBADCE302407247E1AA533187A1A0EFE16E2863BDA860F8071FFFC86817B353D2F4FC8BFC793D54D0250F08BC55B766572A
	BC62D79460F7G2467479972815AG4672512F6D46A3CB79E5029CF159C5DB5F7408D3F449AD53D65B1864D27C2BA9DA4CBFBDE0F2AD37AD476292F6C49B1775CFFD161626931775E1C25A0AFC960F20FCB5AECC77F53F3AA372FD47034E59FC1207EF51BC5C846F09CA0EB27C7B94EFACD071AC473FC77124DD893440824CDB4B9E74411CFC1F90DF19236B03679F7725A594B0FBB28FF90F2DD8C0560D8640DBG1CG2B8142GB6GC49D42712DBBD8314C3C6D550CAD137D6F039F43D37248AE51E4F74B22FD
	D49A90DDD6F144A6398505677D0344761185F2C2AE493BD150F1C632CBAED136BA6C503BB8A5C1A239994F6E5CFD0447A5D1EA3FF5CA8285D3CE817B5D178B7D88B651ADDFF00E093224F2B07AA739A4165F0A8350A3F4G6FC505A4667750B5AB817C4F02B60B55FD07884E866D15059817405C7D706C024416EC7575ED56D1E206681AC57D77651135C47AD6324BED12D3320F410BDB21BD1037DB0D4B38035B7C1794CF66C8AB986E6678C77276B1BC74210DB2521F20F4D116DD5611E9D96A13E459EA1FD065
	1F24363897CCBF37204CF13ABF8501DFEB81A239FF757987B47E199D0E2B534EFEB2F2CA1CFEC45759AD709ED708FB5D3B9DE46F4BAC42BC5DEE41BCED4BF8D0C8FF79D35DBCA5D0D44472332948FB2EA51AC81F2A0E6FF7D8949D37B377EFEFEDF9146AB05C057E893038A7507DEB927CD88990DA04F11F4EB7327956A23AA51CE1BDAE98B1E9CC4DB246FB3540FF0E398A74F34CD3201FE3B78B964F21FB8A74F208517E3C006CAD0E993BA6A7FFD4301F4EE13BDD22C6F391011DA5367713D8D86DD6F950BAB6
	A149FD56B9C990E20AC8EEBDCC6760FAF85F4CF3E9A68FF3A9FF6E81A164729FD27576B34FDFE7259595335CE8A8D6F309597463C3A46612CD1A02C035BA266DB243BFC8673702D75617FB8B75F96FAFA479F8D51AD5789EA8CC9548BAE21B1638EFEB450FC80CDD56A998AAB2E115735C93C55A735C9058C74EFEAB60998840E4F31A083F7CA04573252A0458938EFC11GD14518AF7B0A889D672912A82D8E1B0D051D0F471F0AB24978C95726A5D80649C46E62BE24946F77228DA87EB0FAC6B1714726D98362
	0B09BF6B78790E6C918C77AC453D89C14AB0601E5B1D29BDFB3639B3F44FA284EACA5818F514F86FD76325245FC940FBCA507755455AB52A596DF60CDA69F6200C435B45A45E743CFAC57CD674143BE7A0276B6BBBB5E754834524DF423F26844F0B274AC82CB7C0BB9AE08757F73C44B7CF9ACABE046738556506F34E186441183514905F497E232071DCD62D0DC54856C43F31C8AFE5B1B016FA47A23C0C5851G785ED2346D6FCB76E9E281E7C858EEA7E024501E2B25861637CA5BD11A02F9AB603A17DD6CFD
	2E99ABD557D9CF296F1A4B4EAFCF51F82DA9E56717940E7F954507E8706CDC53CE71243D8EE859E5384FBEFA1878B58F6DB5E568D7F2D906462FDE0755AEF31F1EACA3F34F6132CED86D087BDB0A0393B4DD0C58DED5C147B8987ADC8B40F069B41F36C0BB2E8C7716DF5038E69E0EB3149DC65C860A6B00360D633A4AACBA6335DCA0FD939D0B6FC1E35FC9874BFF7FF4962B478F13F17170764FE8FBD611BDA01CAECE25B9BC6B1806206FA4F68B170AF18D8B00F69840E60018436C9C4074BEB9C3560BF1B830
	41F46BF3CA23C2D4B9114DABC559B6E8DBGBCGC1G91GCCE7D89F99EF33B4AEEB57C0C61B28A532672D9313DAA2DBDB5FA973DEDB09BD468A1853G0C670F33FABCE6208F83305CDCD50C77CA8F6056D620CF4FDFA6B94A5712F2F1C62E2FBF63320E19157088ECBD79AD4DB5A29FDB016B497B34CFE3A55C8B81FAABF1CF739C427BE7785F5CA7142D78ACCD6FAE9C591F14B6D94972E5CD253A9604417D1369B415224E0DC0972A88EE26B4CB538709DF95B01493D1CAFFE2811D2C9D0D02167E3B85F4E2EF
	1AC1CB4FA85352C90C0A95F9665B3385685B4EE70385734FF76B7AC6577AAA6613CDF9C6980E240696C4AB7779F23AA610FC8A2D42F8FE5CC762392B9A469720369A6F7E55F3485BDB0D3C3DB7896FEAE807GC4D4639C3E57E72DDB8A185C03F7A87762ED4B31B57437F45F5FDC4D5650C40E7F970A0F5660D97EA616E07E46824DFC845756CB95A47757C2FB93C08C40CE001C9A08DF8D2E351565154292F73DFE07538C87CE1B669A427CBBDBFAE77771DF27E11E2C7B65CF0420FB1ECE7D48CF1E18EB14BCD1
	1E3DC9C7D249137997DFC97C8B86DA84C0B4408E00DDC7A1C70E22FF1E72B266DF7B18D5EE16E5F1F4126E64685C00D51AD151E461E23E9DB92CDD0BEB203F8E00A1G67515B3B072C5461E76379831A53C1C759F8FE4471C1949F214133F14E21F8520E82DA43B14C6B63F35AF188817CBA000DG51G71477C695FC067C972F1EF7DAFD2FC7EF1EF7D83B7D1FF9B50420E237E17C819CB4979E84DFBDA49F7594F16B7470CF7D5874B21718A7FF72E298A7DBB173BC27F4EB5DB31781D6B4FAA7C5F39D6D7E86F
	DCEC8E9C2BA6EBF2FAAD1CF1G4CG43GF2AD667F471539AC7FF5F23F9DAEE12D8EDA2511D17FFB157EFD952A162E6D6C2EA2E7639107ACBB26C87F085FDBC546CBF6B8A91261322AC80CDCE4EFE72C8A7E53AADAFB0173GC133335B853AA78400DF2135F81E7B53F1BC63ED64B8CF9D4145C1BB398EF14FD0DCB634EBB9AE66BC1E854DF5184781FDA416CEE887820483ECG584A69BF25741D504E29077987B084B0DF0F745CBEFF39982E74630D8F7442ED506159235844F41C702BA3CD191B5E78942F76EE
	BF72643C40E2F3166E294160573AFA0CD7489C0FB547FDBA6F9B6B56F99C27B84E7745CD0CF566897463191BA416B55036G48G81GEBB9BD0A52B7C3BB9EA033A14ED089505B0074211BA48EC4E6B973F86DA2FC32060676B09D53D4073FF8F903DE6C35E3230DF38A4F7513557E67DBFB7552736DECB53D1F42BC691FF5CA8C57D7CD6FC86363EE094FEBCBF5A647195C3D8E3715DC1A59995BF6B8154ED997ADB032B37FBDC7715C6C04182F8608845882C06EC50C273096F962G1F5C88F7A80026C646437A
	7EE19DD98FC41BF542CE561E0B10C30DC476F1BAA7AD70EEEBC45ED76B48180FBA261C8EBB7076382C70DFFA39E7B26FD3192D709E87E0BCC9F16733F5622D0D758141EAA23F75517B45346C689B85EB6C932D135268559607C76159E6CAD7F83A2DF633B8A25998FECF96470B9E8D7E5FDC19AAFFBF0C51E92BE49BE3B44FA7192A8C2705D2FBDF6A531E21A7CF32D885401351BF35EA69F18ABD73945273BA7568791CFE731C16DEFB0A5107B97D39EBFAF479940E01B437E03CACB8DEB7687CDF8DFC4127F07C
	1F1E4358EFBA4546BF861E8927583D1971586611A7330965D191BC9B1A988F9B4323B7179E43089A42436E013E0348F85E7EAC83F9F090397C410FD51AB2101C16543CCFA347870C530FF56C5348EA69AC2699373C471478565F04B1B77A0C9921CFF2FA3028C71F6774BF1A3C470C5083B83DC7179E5A04775B31E3781BD28460621B70FE7B4667647E267B5B6E3435FCDA365A6A6B7B2D32CDEAD11686653E9BFE8CEB00C54DF867EABACEF48CC35B8390500C3AFBEA71EE9286382D4D283BEAB8C7503DDB3708
	936CFA4DF47DC5AD5EF333DBD057EFE81F3D501E84D88170A59DA7389F8A78EF6BA9B6F63F2A4B03A72AA8EF4545253C357DECFF7B794B4DE65319335D702E5CCDC2E634353A6C8F537C566A02DA583E912EBC730F9EC90DE9A15F87BCDFA7705CF965FC012EFE4D62D6FE61EC57591E4133825E0FB6DE525E230552AD3A3F17C7DD7AA035281569B6372AB5825C738D4249014F845DDAA8DF7BD80CBF6CA7F1A3FB540AD62CD35CB82D7D4DBEF8EFD21A3FBBDA0422BBCAE3C3D3D361AE259EBB6E279ECB27A053
	FF4E0A7A335B70DE7FD5A762FA5BB0E78A0AF2747B62D312497E7C9C5684D6737ED2DC3B1478C6366965CB3C12AF01F3397A79F25EF42623DF13AE4A1D682DB94C6F846837329D7555DD473C1DEC473C1D38CEEB23500EG585C0EF3A4F09E67E7AC600C27310F68F91CE716535847559B04B7G5AE1G111C376D9646AB0A6386F98CB34FA06EA591F19D9CD7550931D941F16CB7D1020BB8036721AB47163A6F471FD9F84EE2F21EA5653E70EAB339A7171453C2FA875CF7697D759F68DAE46C702E1FEC247832
	8E6F7A496963B80635C08B6EC0FFAF545ED95DE7748E6502EFDB0E79D5D7C76C97CC5E7E36D0FC3A495B5FE7791A1C8D34F993560BB2CE50739534F71A707E78CEF5A9BC8744ABCAE50814D19CAEFA0384E42F4B6114DC72AC4352DF1014B56C792F356BE87C03EF25228D5EBE64F4321CA96A747E4DE32E012C9DC30077G84832C8358883035936DBBD0DF3408FDECA721F7DC17E8354BAC4EFB7BC91CF7F4AE4C55DDDD2C1D5365FBD7E07644B412FD369268E600B100F90060AE34E528FE496FE25ABD0BFC97
	B3E0559B7F750ABD1B61995325B73FAEF76A49A5A83C69E6D8D34CFAF23FBC07F2B5C06FD0F8061427931EABF565BEF421DC10069E2E3CC742F30B2E5C3BF3F7B6AFD22E5F195C495B16E37366F7F41D0EB5FB4F27980A5FE1761ECFD35779F7E95D10975D38FEBCB4FFE76BE55812F27A6B652BCB4AE9E1E3B72E173F27FBCAF437373F31949F5B6D6D6F359B686F8E20D94E223F26DBC47F7A338BFD5A2C34E34E7A4EAB65AE2F1F3F8AFFFA8F64EF0FDE1C22C4BD399A0537831EC33AF24738BE27063ED2F98F
	02E7382E5C3D7C3B3CC88DFD0B729E8B4F1DDEF21F8BE47F0EBCC04EEFD6BB5CD824D67605130035160D74AC1D598BF738DE0C63EF0E8BDC67F31A774163F73F1E6A7CFF50F12ACE7529C3737EC26D5D573D6D2E6AE07B407E93641D57F85936085F201D489258B7B0D638414E518BC09FB05AA7589EE3B8A570FE7E6A8439BB686EED3015F4429935E7640AB4AA770B93DC66FB8D7E6BC9BFE8D83A1E7463867D7A6D038D3E755B1FB66857EFC39BFC6B3707689D2392F23C2D976B02AFDF20B5A0E8CFF2DC7DG
	568A65DE5CD7639A7D7B74F623FE7D7D3DC67D7A7B870D0BFFD717F652DFFD1E59B7E5C51BB7F01B1F33224D1BB96E1F697959F88E4E6D679077EDA74AF6F05CAB1DA85B4FF10567D0B61463DE203888E8EF613841BC020B0536F09EF1CFD0DC963473B9EECFBE6A28643819EB28E385479DF4210EC00EDBE9427E42B86EF59376370E639E1B457E127BF09DFB753ABA3737E95E5D576F7EBCDFF3D7F5B01F9366F16D4E66BE3FFEC375F33B66FD66465D77B9602E6AE0BE8F5F42F91C5F07E39FFA8B733A1663D2
	844C11B60EF3737C8F6438ADBC7FC3B82EDD44DC0A6738B7C514CD6EC7DC6F887FDD016302783C9B66386ECE4CEB19635E98463CDE4DF19DA2769748F1899D58DF8C47BDEA427E92B82E017717FE81F18F8FE3FF599CF70877B7F981EB2E3FBFC66A9BB22934411355106CCDA4F7983D112E1DEE09FC5FA70D31FAECFFBF568666F9BFF19748F70AB2AC062C0F7B1BC99F6197E80DE520ED113145739DFEC33486E48864A978108B6A9910757D6B96356F572DCB75ED50G767DBF8AED0726EF66473F1BC8BECBF6
	32B10DA97567BF18301EA28C40D8D09B5EECD5ED585E39148DA90B5810B5205A4074751EC3FD46810CEF6CB9F20EF33A24F14965124658672B8AFFA27DBD3186F81BB83FBDCF3F16FE8FDD23ED40370273C666AD9C3B7D6D2AEF8579685B5A815FB334418BB68CAC9C3BA89F5FC22E21EFB1C0E338CE3A76A7C0BBF990635BF4DA35E12789ED289C5437C19B5F364105B6D8863DED58B40BB60CF1FD9B4F881C7E7DDB185F9ECEFF27C3356791975A33DA471EC48D6865F37020F7BED7F42AFD7FA74F25B0BFFDAF
	164F6BFDFC350B7CDBAD6E4B79AED55F44886A0B7623EF315C0D770DAD1FABBB87F1FC5959A35392E74831208D53E655064BA25A50E1D9BA4706AC8B636934F85BF0105BB0EFC19B58F74281244DED886AD6EDD8B70CB6EC74E303B68E51DEB6447B5850534163E041F97682DBA3DD5312EF8D98F32129073C33BA562B69D8874F79598F057CD71ECEC5FD5E76E5DEE4B68FDFDCD80BE7FDEE6F9584DC5F52BB099F63224D2D2E777B4E2B7409F33E74D3FDAA5D1CC76E7FF6E946486B54EDD6D8E1489F35945554
	14992BAB0B85C14FBFA67FF09E793BC51D9A37397D74029237727B40C0BEFEAF9DF4D13FF64EEC9C69D7F57C5DB5DF9FD6DDD0693326657840749F6EC47D1B97557F54005A7FEB33DA7D8C97F28B471E757B228B7B5D759566632B038277BB278B69CD1C1EFCD15531CA546AE0B2C1A3A86361B25FFC25314B649B17B5DF2B7468616547A5C0C4DDEB3FDAACAE6BAE297D576A68FF6D9B15DEF59B7ADF9FC67D310B6ACF9DD67B9F6F506AE7F75E9DF45E9B3F0EB3B4G8C83F8G56G0482C4GEC82588A10G10
	B984E7CF00DAGB340900093E065905E97D775105FB57D5504159F995522300067672F73357524A068AFFCE8716FCB18DC02D9AB9789B2DB16A5770ACBAB978BB2BB17A5F730CBAB17F98962F1E9B9F28F0FE865EAC1463CAC398796545906C1C6DE165C36E12D5CEA1089DEC20E0D5FAE517FFDFF2F38F48DA3DB542FE1FCA17A56B072C47D9AC631282DE1B0DCB91D6F9310BF964EF70463AEF2DCAD6D1360862DE372A4471F24F691FC07C494A23E0D5AB5C3F19685E75260AE6237BEA279CE87DC1EF438D036
	CFA4EB5A3855E6E36DC14AE3353BA51947E3085AE8F50A9A3BC50EA3F6333B090B2E73EBAF31FDD42993D05C4685383D5D3EFCD95D3EFC5BDD3EFCEF3BFC79EA3BFC799A3BFC79560D78727DE24417AFDC7465FBD27465CB5461CBDD40474E9BA27D9BB225E6C707D4793BADCF060A47A195587FD760C45361A4B8577C171062F6891FAF7A7B7FBFEDFB3C7D116051CDC9C222C978482192BC64CFDB53E4E1B7791448C87E7CC196524957AFC6657397D9C86E365A6B49C3744043D0D9DEA98BA9D5157479A17DE2
	65F665405C3D5CDC934D232D5F9E29B73C7618A1C9C8B68929ECDE1904E4B2B9CCC28A1DBBA641G730A9A1C2C8577B561A61D594B413A047D3E1BCE73FD7F7ACA7865362CA4A18914CFCACCDB4A8C99862A0F54D34B965133FF99BC5F9D3B9CADCB7088F866AC3E257D6DB4EAD52C6E5FBD9258B44C56532D435E3F1DFE50C272B8C704FDD244FD31276530603F1E5CB7A55AEC32E83B5AEC9FA3356516D9D9D26B26492D24FFB772A8C4C67B281558FDC5A373FFD0CB8788BC3DE872EB96GG38C3GGD0CB81
	8294G94G88G88GA2DA47ACBC3DE872EB96GG38C3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2596GGGG
**end of data**/
}
/**
 * Return the JCheckBox1 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBox getLeftAutoScalingCheckBox() {
	if (ivjLeftAutoScalingCheckBox == null) {
		try {
			ivjLeftAutoScalingCheckBox = new javax.swing.JCheckBox();
			ivjLeftAutoScalingCheckBox.setName("LeftAutoScalingCheckBox");
			ivjLeftAutoScalingCheckBox.setSelected(true);
			ivjLeftAutoScalingCheckBox.setText("Auto Scaling");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLeftAutoScalingCheckBox;
}
/**
 * Return the LeftAxisPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getLeftAxisPanel() {
	if (ivjLeftAxisPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitle("Left Axis");
			ivjLeftAxisPanel = new javax.swing.JPanel();
			ivjLeftAxisPanel.setName("LeftAxisPanel");
			ivjLeftAxisPanel.setBorder(ivjLocalBorder);
			ivjLeftAxisPanel.setLayout(new java.awt.GridBagLayout());
			ivjLeftAxisPanel.setAlignmentY(java.awt.Component.CENTER_ALIGNMENT);
			ivjLeftAxisPanel.setComponentOrientation(java.awt.ComponentOrientation.UNKNOWN);

			java.awt.GridBagConstraints constraintsLeftAutoScalingCheckBox = new java.awt.GridBagConstraints();
			constraintsLeftAutoScalingCheckBox.gridx = 0; constraintsLeftAutoScalingCheckBox.gridy = 0;
			constraintsLeftAutoScalingCheckBox.gridwidth = 2;
			constraintsLeftAutoScalingCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsLeftAutoScalingCheckBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getLeftAxisPanel().add(getLeftAutoScalingCheckBox(), constraintsLeftAutoScalingCheckBox);

			java.awt.GridBagConstraints constraintsLeftMinLabel = new java.awt.GridBagConstraints();
			constraintsLeftMinLabel.gridx = 0; constraintsLeftMinLabel.gridy = 1;
			constraintsLeftMinLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getLeftAxisPanel().add(getLeftMinLabel(), constraintsLeftMinLabel);

			java.awt.GridBagConstraints constraintsLeftMaxLabel = new java.awt.GridBagConstraints();
			constraintsLeftMaxLabel.gridx = 0; constraintsLeftMaxLabel.gridy = 2;
			constraintsLeftMaxLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getLeftAxisPanel().add(getLeftMaxLabel(), constraintsLeftMaxLabel);

			java.awt.GridBagConstraints constraintsLeftMinTextField = new java.awt.GridBagConstraints();
			constraintsLeftMinTextField.gridx = 1; constraintsLeftMinTextField.gridy = 1;
			constraintsLeftMinTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsLeftMinTextField.weightx = 1.0;
			constraintsLeftMinTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getLeftAxisPanel().add(getLeftMinTextField(), constraintsLeftMinTextField);

			java.awt.GridBagConstraints constraintsLeftMaxTextField = new java.awt.GridBagConstraints();
			constraintsLeftMaxTextField.gridx = 1; constraintsLeftMaxTextField.gridy = 2;
			constraintsLeftMaxTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsLeftMaxTextField.weightx = 1.0;
			constraintsLeftMaxTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getLeftAxisPanel().add(getLeftMaxTextField(), constraintsLeftMaxTextField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLeftAxisPanel;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLeftMaxLabel() {
	if (ivjLeftMaxLabel == null) {
		try {
			ivjLeftMaxLabel = new javax.swing.JLabel();
			ivjLeftMaxLabel.setName("LeftMaxLabel");
			ivjLeftMaxLabel.setText("Max:");
			ivjLeftMaxLabel.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLeftMaxLabel;
}
/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JTextField getLeftMaxTextField() {
	if (ivjLeftMaxTextField == null) {
		try {
			ivjLeftMaxTextField = new javax.swing.JTextField();
			ivjLeftMaxTextField.setName("LeftMaxTextField");
			ivjLeftMaxTextField.setPreferredSize(new java.awt.Dimension(44, 20));
			ivjLeftMaxTextField.setText("100.0");
			ivjLeftMaxTextField.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLeftMaxTextField;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLeftMinLabel() {
	if (ivjLeftMinLabel == null) {
		try {
			ivjLeftMinLabel = new javax.swing.JLabel();
			ivjLeftMinLabel.setName("LeftMinLabel");
			ivjLeftMinLabel.setText("Min:");
			ivjLeftMinLabel.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLeftMinLabel;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JTextField getLeftMinTextField() {
	if (ivjLeftMinTextField == null) {
		try {
			ivjLeftMinTextField = new javax.swing.JTextField();
			ivjLeftMinTextField.setName("LeftMinTextField");
			ivjLeftMinTextField.setPreferredSize(new java.awt.Dimension(44, 20));
			ivjLeftMinTextField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjLeftMinTextField.setText("0.0");
			ivjLeftMinTextField.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLeftMinTextField;
}
/**
 * Return the JCheckBox2 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBox getRightAutoScalingCheckBox() {
	if (ivjRightAutoScalingCheckBox == null) {
		try {
			ivjRightAutoScalingCheckBox = new javax.swing.JCheckBox();
			ivjRightAutoScalingCheckBox.setName("RightAutoScalingCheckBox");
			ivjRightAutoScalingCheckBox.setSelected(true);
			ivjRightAutoScalingCheckBox.setText("Auto Scaling");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRightAutoScalingCheckBox;
}
/**
 * Return the RightAxisPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getRightAxisPanel() {
	if (ivjRightAxisPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitle("Right Axis");
			ivjRightAxisPanel = new javax.swing.JPanel();
			ivjRightAxisPanel.setName("RightAxisPanel");
			ivjRightAxisPanel.setAlignmentX(java.awt.Component.RIGHT_ALIGNMENT);
			ivjRightAxisPanel.setLayout(new java.awt.GridBagLayout());
			ivjRightAxisPanel.setAlignmentY(java.awt.Component.CENTER_ALIGNMENT);
			ivjRightAxisPanel.setBorder(ivjLocalBorder1);

			java.awt.GridBagConstraints constraintsRightAutoScalingCheckBox = new java.awt.GridBagConstraints();
			constraintsRightAutoScalingCheckBox.gridx = 0; constraintsRightAutoScalingCheckBox.gridy = 0;
			constraintsRightAutoScalingCheckBox.gridwidth = 0;
			constraintsRightAutoScalingCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRightAutoScalingCheckBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getRightAxisPanel().add(getRightAutoScalingCheckBox(), constraintsRightAutoScalingCheckBox);

			java.awt.GridBagConstraints constraintsRightMinLabel = new java.awt.GridBagConstraints();
			constraintsRightMinLabel.gridx = 0; constraintsRightMinLabel.gridy = 1;
			constraintsRightMinLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getRightAxisPanel().add(getRightMinLabel(), constraintsRightMinLabel);

			java.awt.GridBagConstraints constraintsRightMaxLabel = new java.awt.GridBagConstraints();
			constraintsRightMaxLabel.gridx = 0; constraintsRightMaxLabel.gridy = 2;
			constraintsRightMaxLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getRightAxisPanel().add(getRightMaxLabel(), constraintsRightMaxLabel);

			java.awt.GridBagConstraints constraintsRightMinTextField = new java.awt.GridBagConstraints();
			constraintsRightMinTextField.gridx = 1; constraintsRightMinTextField.gridy = 1;
			constraintsRightMinTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRightMinTextField.weightx = 1.0;
			constraintsRightMinTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getRightAxisPanel().add(getRightMinTextField(), constraintsRightMinTextField);

			java.awt.GridBagConstraints constraintsRightMaxTextField = new java.awt.GridBagConstraints();
			constraintsRightMaxTextField.gridx = 1; constraintsRightMaxTextField.gridy = 2;
			constraintsRightMaxTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRightMaxTextField.weightx = 1.0;
			constraintsRightMaxTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getRightAxisPanel().add(getRightMaxTextField(), constraintsRightMaxTextField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRightAxisPanel;
}
/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRightMaxLabel() {
	if (ivjRightMaxLabel == null) {
		try {
			ivjRightMaxLabel = new javax.swing.JLabel();
			ivjRightMaxLabel.setName("RightMaxLabel");
			ivjRightMaxLabel.setText("Max:");
			ivjRightMaxLabel.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRightMaxLabel;
}
/**
 * Return the JTextField4 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JTextField getRightMaxTextField() {
	if (ivjRightMaxTextField == null) {
		try {
			ivjRightMaxTextField = new javax.swing.JTextField();
			ivjRightMaxTextField.setName("RightMaxTextField");
			ivjRightMaxTextField.setPreferredSize(new java.awt.Dimension(44, 20));
			ivjRightMaxTextField.setText("100.0");
			ivjRightMaxTextField.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRightMaxTextField;
}
/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRightMinLabel() {
	if (ivjRightMinLabel == null) {
		try {
			ivjRightMinLabel = new javax.swing.JLabel();
			ivjRightMinLabel.setName("RightMinLabel");
			ivjRightMinLabel.setText("Min:");
			ivjRightMinLabel.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRightMinLabel;
}
/**
 * Return the JTextField3 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JTextField getRightMinTextField() {
	if (ivjRightMinTextField == null) {
		try {
			ivjRightMinTextField = new javax.swing.JTextField();
			ivjRightMinTextField.setName("RightMinTextField");
			ivjRightMinTextField.setPreferredSize(new java.awt.Dimension(44, 20));
			ivjRightMinTextField.setText("0.0");
			ivjRightMinTextField.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRightMinTextField;
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
private void initConnections() throws java.lang.Exception
{
	getLeftAutoScalingCheckBox().addItemListener(this);
	getRightAutoScalingCheckBox().addItemListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("AxisPanel");
		setLayout(new java.awt.GridLayout());
		setSize(387, 152);
		add(getLeftAxisPanel(), getLeftAxisPanel().getName());
		add(getRightAxisPanel(), getRightAxisPanel().getName());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	try
	{
		initConnections();
	}
	catch (Exception e)
	{
		System.out.println(" Error initializing Event listeners");
		e.printStackTrace();
	}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (1/7/2002 11:05:58 AM)
 * @param event java.awt.event.ItemEvent
 */
public void itemStateChanged(ItemEvent event)
{
	if (event.getSource() == getLeftAutoScalingCheckBox() )
	{
		boolean enable = !getLeftAutoScalingCheckBox().isSelected();
		getLeftMinLabel().setEnabled(enable);
		getLeftMinTextField().setEnabled(enable);
		getLeftMaxLabel().setEnabled(enable);
		getLeftMaxTextField().setEnabled(enable);

	}
	
	else if (event.getSource() == getRightAutoScalingCheckBox())
	{
		boolean enable = !getRightAutoScalingCheckBox().isSelected();
		getRightMinLabel().setEnabled(enable);
		getRightMinTextField().setEnabled(enable);
		getRightMaxLabel().setEnabled(enable);
		getRightMaxTextField().setEnabled(enable);
	}
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		AxisPanel aAxisPanel;
		aAxisPanel = new AxisPanel();
		frame.setContentPane(aAxisPanel);
		frame.setSize(aAxisPanel.getSize());
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
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}
