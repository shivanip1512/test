package com.cannontech.dbeditor.wizard.copy.point;

import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.StatusPoint;

/**
 * This type was created in VisualAge.
 */

public class PointCopyOffsetsPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ItemListener {
	private javax.swing.JLabel ivjControlOffsetLabel = null;
	private javax.swing.JLabel ivjPointOffsetLabel = null;
	private javax.swing.JComboBox ivjControlTypeComboBox = null;
	private javax.swing.JLabel ivjControlTypeLabel = null;
	private javax.swing.JCheckBox ivjPhysicalPointOffsetCheckBox = null;
	private com.klg.jclass.field.JCSpinField ivjControlOffsetSpinner = null;
	private com.klg.jclass.field.JCSpinField ivjPointOffsetSpinner = null;
	private java.util.Vector usedPointOffsetsVector = null;
	private javax.swing.JLabel ivjUsedPointOffsetLabel = null;
	private boolean sameDevice = false;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PointCopyOffsetsPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (PhysicalPointOffsetCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> PointStatusPhysicalSettingsPanel.physicalPointOffsetCheckBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.physicalPointOffsetCheckBox_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (ControlTypeComboBox.item.itemStateChanged(java.awt.event.ItemEvent) --> PointStatusPhysicalSettingsPanel.controlTypeComboBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.controlTypeComboBox_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (PointOffsetSpinner.value.valueChanged(com.klg.jclass.util.value.JCValueEvent) --> PointStatusPhysicalSettingsPanel.pointOffsetSpinner_ValueChanged(Lcom.klg.jclass.util.value.JCValueEvent;)V)
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(com.klg.jclass.util.value.JCValueEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.pointOffsetSpinner_ValueChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
public void controlTypeComboBox_ItemStateChanged(java.awt.event.ItemEvent itemEvent) 
{

	boolean value = PointTypes.hasControl(
							getControlTypeComboBox().getSelectedItem().toString() );

	getControlOffsetLabel().setEnabled( value );
	
	getControlOffsetSpinner().setEnabled( value );

	revalidate();
	repaint();
	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GA5F991A9GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BB8DF8D4D715E85132B12B28F4B75B22CBADDA7C0CCA152D6C8735780916AAD55CE51555D4DBC5B71F257D722D6C4AD65C5E498F8DE900G2925B4ADC320C0E8BAC98810A1FF64E772CB7EC80690CC3A65E55ECBE648E4E6726605CCE8F4771CFB5F3D6F4DDFC8D87D3E5D793E13B7775CFB6EBD675CF34FBD674C8B49BF32AC4B121ECF08A50B681F8A8BA1E94D04A4CD2DB946F1BB8A92F20869F394
	E0BF59B018894F6C5793F272DCG644909ECE897C0BBF3B0A1679578FE10FC30B925863E44D9E79574BD17623CF776F57E71BCDF67C55A5E13118B4F3DGD640B9G96A38F644D0CC3BA5E8D6D3D7AF784CBBAA1BDEF60180341C3A67CE6FD5CF1683383B80E60183A0C823DBF89F0AB818860730F303145705CCD391B68366B6356E65FC3961E7929E58F2EE51A1FC322CE3BD2FFDAC982D5C486B9FB359E1E55BDE32335AE7F384D25F9EC7230AD28E813815B6824479670FBFCDAE5E58FBE7AB4C91B8C763827
	039E17646DD3B44D639B8D76C8BE450BF3DA11778B41ECBAAFA12930D6B245BD94C4FD4970FD86E00915737FDCE7354E5F75E7BFC56AFE7CCCDC7E532C0C6F7C23867FDB1ED8173E7EA84E6F217B01F4CEE8A782B059DEA8EC8813CD72797CBECDF139EDD31EDB12AA4FDDB8A65BC399DC366CC2AEDB0B9F572E0376GC0C0607786903F885A4BGD697F219FFFA114B6CF5FF06E41626D9764511F9D3A113F5C721A133356B351CD7298D3EEBC7BBBAFB8C56857082AC86D889308960B5EA7B07ABCE4333FED8EB
	740F0F7BFD7501C003D7D6BB7D3262056F1515502363FA9517DF1589E176DD9BAC0E418F0754E361360D40E43BCA38BDA6255FC7120E7C065AD824EC7248A64BCEFD0E3C6306EC2D47699952D0EF25709D69109F06AF2378C4931E6D7DD728EDE1FBB974E53F49753C649467A577E39FA5EBC7FFE4F9B186AF0147FEED713E49F8D87826418B537105CA5471CA40EF84588A30932060C4C2CE75892EE339227CF6BAEE831BD3FDE0D3134381C9559AA6BDDED9D1F5DD7BB3E653F5ACE0F4FF18B79D534B0AF2739E
	9DDA731318FAD93C76994B4089260F4089C3AFEC6D07632E3D78C434ED6C95FB38061E1565A7589EE6887CB7A8FE15894F76361362E99B7ACE1DCC48E1635FCF4FD66249C89B79B045AFBF99E9A37981EEA3EB214FFAAAC1BFC3BF28C2BF3B905A4BGD683EC82C88348AD02F1C5FC0F3FD8D1B247F354E35ADFC66B367F8E1ECAC8D325B6DFD013FCAE25DFD2BD5230D78992FD4C049DF99B8E233BC94F378B56E8D1FC0AAAE99E3F8FE6174695B026A09B735F767DB8C6D5E82F53B32E4082638142673D5BFE90
	1EDEA928DD8948122698A3D87FC76CE893B725FE1891A6403DAEC2FDDD21BE3C8E3E8F95F1DDE556A0EE9534B78A3D64DCB8G4FCBE05C0A37325261F1A19B12BA4DED74ADFAE61F0271492793F25CEF893FBF40EDF0474F6FA3298F274434C184FBB133271E62C89B2C20F307G1F84303A185F4D0F8487CC7438B67F0E6B4F673C188179B74E5E7743564808B81BF73AC6167EE47AD6EBD13EADA057F6007C37394E1BAED819ADB6C8C105DBDF378A062348468E30318D15F837A8B7951F66D0820ACF06AFC1FD
	6F5BAAF79BF6B96297B653CD7168279A41F00282FF25F2BF43439CE6AB677D6F50FEC953D44F702426707BD374AB1417E0188B8C6A32EA97D156E2105181609432BED7F950E4DF9DFE7F58E44009A7C73753A768FD6C06718B5E667B7FB5BF46EA4BC57B5B34ED7477794DFB978F107ECE76102D611D539A99F7B03F314622G0FF9B2F45BE178A5943F89785F81E035F1398F8786A2465F291D4667291C2EE16667CF39863309272B625BF4EAD5EC1BFE2EAA36CDEF2F1A5DE6F7D64532D95617D10DB27A27FC26
	B310DD0D7E5065777994FA908589E17168F3CE14436373E8D7BD72282275F9EEA940178DED59CBFDEEABFC9F32F1BFB9F50C7BC9397F2FC84F9FB6460C27D66E7FB74BAA9B33578DB643CFB21EF65720BDA8DEE59C9455681F74E98C3F0F7AF39D9F4C2038E372D71176948F02E9DC5CBDB28259C023DBF10DB578C3459ECD9937619F0C1715C637649BD564D3B7241B12CD1A52ECF4E3ECED505F045F0A2CEF3FDDEB3DDDFCDD9C083F603BED11EBF544DB2C2A283F2814595FC152917C82152591A2FE556FF5CE
	8794383D077DFFB699DC518B4D1377B24E7B36222F31FDB05451977000D52945B7A56F24E223FF791C185A0CF9C7EDB7DCE85C36C94D63E583EC6D0D7D781C57CEC4AD79EE3FF9310E392D06621CA17CAEFB308EEDB095ECF3A7C0FE89776F9F2A41FE6E5F75CB2C516F75326383FD09A5F8B6EEE879A6FBAFAEE1B9F7881E2C7F19187D1BC53FF7B2D6FF2A60A3D41B0767DC1918D4A0BC438B6612B24DE32C5A8391FDD4CA913B5DDD7B92BC3D263B0961EFDD0CFD6E96DC447BE8CC4C3F7062AE02B1981B1379
	0915924E99D0BD63F01563E52B578E36E618EB87BB01FF2CB31CAD05383814673C9DA5D6F85EF61F4BF03E47823C4EF09D68C3GC925EC4E4D25FA2D010ECB186063C250DE8C30320C0D5BD6E69E572972F155E7609E87504E30F1EB4F6879FBB90E3BCB5488CA846EDE0A7BBCDC9A0FE772DC25B34FDC5F18795EB3B17D9F42C0B91B5FDB9E19432554633C0900DFDA4E777BBFAF1A630A7AE0506F725090DA5F3B4F5719FDA343FD298E8BC3A39EB588791C2C04787E2F2EC3DEB14656517A9EFE6EADACBBC566
	8CCD32C7732BBCECEB1C22074569EF04B03F95CC28D1DC9D7D9EE50A703A437D9A57574AF2FEB75CFB46DCBBD9BA749D4BCBF1F4132A6BA47B2C211B212E57F2585C0513FC6ED668E7388784EE41D9EED7AF3E0BB9DF2CDA8AD609508DC086A0E9F07186A6B5EAE3EC2E2ECC1C8BE30155E7F9ED243F8B4FC69A34334F411AGACCFCB23318B2EE787DC609CBF3F3F28B41F5F9567D83C37891E4CDFDE1DC0BB1A27D7577749F3208D4F5A1F6A82A25CF6C9CF1D4EEF326393A4F47A6918F51EED1E9FDA361EE37B
	12FB5E581F051E021C567182DDAFDF1E60FA9900B10B4FF33D8C3DE7564B0673CCAFE96739DE8E2BF72E17CF3E04F5417F7525C6F88EFED365B2E4D6A464D4D7F0992A8B4DB2E895CC069595DC06A755CA6E9F6F685A6367F14761F65C67BF5975A6EADEE742FC54EFF7274773D1EBAB587EEE2A0874D16B9A710CEC83BC31F39FD5DE3F5F6423FA41ABG07F03B08B8389E758377085E7EF9FD965793617178E734795408726DBC577F2D83776494349DGC3G9A4092411B376136B52F2650ACB52F13137361EB
	151D67B0AB66C537C15075E5F23A94BB53FD1A5D486FB2AFC467FA2C1E34349BED765085D6CF5AAF701F27F807894FECFC087AB51AA7C35F4E8BFC7DFF8FC4723D28B2FC4D6591EDC617B991C977C6240C91F44C56EB7BF11F282F46239932852760662F2CEC67BECD3FD7DE2FC23BC3BBFE2A126FFDDFB35E61072AA02EG68GF083A4822CG58D845EDE0CDC32C7BAD5C8662DDEEC66D00497977D1F24ECDBF0F5DD6BF5B2AE269E7549BCFBF3A0F537573717732F51A4CEA3E17B36A1D711A90C59749EBF5F5
	ACDE7FF87E84B15D3BA65FB462D13CB2B20D1E2C999BBCDE889672FDF5D673FD7DF88B0D03203D341A6F6121C67C3D057B98FA77916EDF3E5108F129DF750CFAFC9C77AD0AF379D51A74329A4CA3B4B6C906B9B708F9331A62676839CD31F37483CD31F374C3CD33571D2CCD71F2F846DF6DA467AF8500619E9538B4013BC163144C9A08C7EAB82E7992624EC23BD5600EF5A24E8B6D05B55CA6BE55ED05678AE82F87588A10DB1B10F38A2095408D3028160F7DB29B8B6D8D35AC66CE2DE5B1B5464B5997016EA2
	9FBB5C3540E48FFCBE1C0B635C7AF7DC1B63787A31FC627A1A79573598AF9F886385E34DBD0479BA0E5B27BFA36B213B634CFBA7B52857697C0F7FEE9F3509E0D089D2A70301275E77AF5A4BA4C68D26CB9AD7884F2F5F1E582D0FE9527C0DE5C246C555F8C6DBAF066BE8C8EF877467C2D33FDE7BEC0EEF6FF2F3ECFB3F510C7A8B7AA7D51792E953934D07C41F61B5B12CC2B1747C1252EB12BA2AC41D0977D1FA56970FBE21B95E39E1BACA8B535199212324774CFEACA9C2D74997236D0B4D77E555BCDF3918
	AFD44867DB97C13B995A29GBBA3700C7F24963CFBC3223D14362789771BCA7DDE2103A61F2CBBB716EBEE26BE6BB74098FDB500622ED01F7084907F0540FD1F629E4CE263189C6F4BC43D87D4E5C4D1D5C5E6A5BC52DAC7F3D86AC37260FBC19D0FDF5F3904BE53ABCD7BA7E17D9DF45CBFDC62F9FCA2341783EC84F82A0E453E0C7F0726700C0D2852B8ECCBDFC0F1114D75C8DB5949E96B206D8450G16G2C2A67EB5E32E3BD43144BF4C8430A1775ED1C46FA06DE95B375F279920F21BEE729G6A63AE1C4A
	B366417C40340667644170BEF12F327E096283617C6931B8676179167CC89E75913AED66E17F1520A2C74A2857033A4DB9C5C18353EBDD834FB9F6B9E275BBC57F77ABE2753BC5FF2294ABE719917DDB86E351AFEE60F7485FD6231DAC0776EA01CB25380D50CE91384E9AFE57248A5CD085626C0D40EFA347FD1146A95731ADF097CB44BDA5F09F21F141E6E82789DCFF085EFB8E30D98747254B1C36D460465FC15C82E8A781A4BB70BD19EFF792F1AE2AAED96169D3262CA29BF4F846959F7E30F82A7CDCE931
	75CCE9E9914B191EA6661C493AB6BDEE4E1466E0E7BF3309BD97BDFDD80F351F3F44FF7F2806BE2D091F1B326B983F0A588E99292CECD1BDF203B45AC1CFA10B119ABA32F57A64A6DE1788F4601CDB213D93A03F1957DAEEE570DA4B4E2287C8622E6DB15FD329DE73D3CBF1337EFBE5331163650ADA4B75CE3EE6A2742FEF263FD9F71AED85F5798C8C304113752F8B6BC71F8DA92D297FDF437A115F1F241B7B0FF7197B33CC74CC36378639EC4E738F12563B3644FD87A7370549D42ABF4F269CA6B31FBE2A6B
	7DD7A307C84C181A1ECB3D565443EBC16E96DEEB2AF1F05CB2015BE36738AD8277F5094734F92D9C37EF88F148EFEBAB77FD8F53F394D430062C484CAFBFD14D6BD949E25C8B55D8075560E2E6363C2C9B3511560A63659A87374F56687B4C22C316BE67A69803F0CC47EFEDE5F9A17F2D6DA089D6A00F923D05B1DD12FCB272C2578D7598E7284E0EBC0D005FE3FCEF21675A5E86F9B8G632D3F04732635453EEB33E661EDD1DBA4EF1FA90CFF1FBDCA7D459220D9564675B69E42BA6830649A9BD57D13A8889B
	7B816AC7B64238AD0257D7E54E6B0EB83C1AA1379D6256F603D7EBFBA42F8B5F41BB91AEC44F7864B865142D3D147A26E298EFEF67E73644EE76B11BDF7F2E054F9549075C2E47F76D11F5199B17291F867C3AF61E5B64F53D854FFE69C6F4C59710BD2ABF20285AB4435202119E67100EBBFB4FE76F9D52354C1B0E55B636D29F7814C9778C7FAC45EFB361753ADE877743BB20EF68924FB76F6BC5FF308E5AA9G5B81F2BB92F20A81DABB38AE2FF515125B54901C7EC0878456DED36A4764D3BA6F6C7D27971D
	5C5FAD796247C8487B5D38F573218EE68FB37AF35D9307A91E49E7F322FC49503781E09BC0E6E7C24EA10052CEAE5F7DDD45CCBEB407FACD13DCEE7A0B82978E0B7C869A03BC3DBE56E3164D5149569F501F5E4E683344689A9CE63A857A38A57AF3C5DC3A83F6B35DDAFD5CA67D39B5AE5D8609536D0898135B85BE9A60ECD72CDAC87ED02C77A3DA7531C3DD516B517389780591FDC950CE0E40B1DF311579F7F5D2E1F745BDB53C16338E469F21E38849A6460779636FDCA142D68A025CD636357C8DD97864AF
	AD5CB7C57236C36721355B70A1A9AB562533B9AF7F1E883E4ACB8AC9C41C548C8E7FD4216C113C7E5142E2AB04CAE5E70BF8AD676E924E7702EE9EF7958D9A7CDA060C39AF055E081C5B08414E1D03906CCC9127F33CEB50BD6F4298C150B47A3DFE55B66576E83A6E5AE54E436A6E59F4D7F05D783E7D3AB13FDF1A8BDF6CDC5DAFA19DBBBFEBFB5EA4F1FCAD1C1EF6182BFB78865C73CEE9D45078AF237EC5DE4BDCB0FF276EB24A096FDF002EBD3768CFFEFDB050B74A6C72179DC458682FCC5FE7BA6672BE4F
	1D82DB631EBF6B9ACC7E653D716B941F6B0DDD27F828B7F6DD6EAB3D333FBB73E8EF7CF7E73E59EBFEF7066D7B9706F1EF52403EF2FB400782B8G86FA38AFF576613941B7AD237DE813BC0AF592FC3BD2636BD7B8634BDA6B3C7DBB19ADCE9A6778B54DBF0E73F3FC279377CB7387A81261FA1D28A355B36A5658D09DBF6024F914B2C251FABD1246409A4835300767D60F50B8AF895AAB842EF00667E02B846E1A0353E6DE6638935D1C7610405D5D4DE90F8BDC111D67081A40BDC8631BC550CE92385CDECE3B
	DC60FEA2713CF13B40D53D4373466CDE0E8B0A3C31C0609E1C60F923D7604E5338FD8134978B5CFD9437945A2B846E677D085BG6D2D8217B708389D504E6E937AAB926B8A5C9B2FD29C34977671FA6A3DB5E135AB40A7774DDE8FE53619CB6358F5B0B68500B8C157G45G8E0081GAF408200A40015G6B8136GEC83588910F78576628A3765037DC5717C9AA075E4159A34AA311ABC63BF31444C3F9D666B38B2BB7F0C2EDBB653DD879A6DB6F40C4F0707631F21C706EFFF063EB9CC5F23G5B47A29043FD
	FB187E2EC0CBB6FC5C2661BC01EB8B7678039E744C6C6E3852931E930D5173338878DFF20567A41B5F8B4F651E44A32867F297E6F88E32BE064CBC3760308546A060BD0FBEEA5B95D39CCC57FFBFBD03BBG1F594F6F633F9BB1567BE4B7DF6FECFF6C75DE1CE53D567E48757A6939F5827EFABF17772B1743657D4BD1E37DE33DFC7D45F1564FB22D9F199FAE0FDABF1B6E61CA402F85E0B8191E7DD4E8EF6F6739DE024875722F464E754CF2C766FA272EC62E3B097A0D33002F3B4A656E9C8A17DBF19342E30C
	AF8D515CD08AC564065F22FE458BF30444BCBB7B63673CAFDF25B99F0CDDA9461F9C8C37419A6A03D220BF75AA17FFF391173FE0A0DAFE334E91CAE18C82175FB190196B1EFBB5F62E5BC97DDA8F0C9F9A603E6EA19A5BAE4279AC7CF72F07A9EE99604A45F9364A714F73C979766739D40EFDA71E17236F44AAB9761DD8AF1B6FC406EB16D1BF236077864438CB82F7CD60FA681C083B6A11B5374077D33E905F2A60029CBFC4791A2238819DA719F057F45C080CBE89C4F67BD5CEBBA653BD77F83DFADCC947F8
	FC602204BEB44A23A7A019781E96B8641B6D4583A5FCFF32447E2CA8617BD3A1460D48FC1CC56046E4BE0E56AA295D8F5EB06C7E21776617CF5C1861317C5A0159E27972B163FB5138315E616E792D775DEE3E5E76816EAFADBEE3EECF40187B3E4B731B5B594B67AE3E46672E1FB066FED5B566AEFDE76E3951DF878D3AB1F9765CE80D480DE44103DFB3F8E819B466DAB3B8F79E0E5CB468FE58FFA4BEDD191D56560BF43ADF1B68328667CE779FA103EEE55149597641B66C774A3099E5ECB3189E32CEF3BDAC
	3DB61BDDFDEF5A787E59DB461A277DF3611579D169C4AE91F99CF5858CDF905AE36039AB607FE7B7E30E01B6830F29576692BBED6EB6536D841A3C5F4F056EDA2F196EB8505867C437AEACD66BG1A6BF3224BB9ED265300E651ED6818FF79608C77B9AE017B508C77B9C7856E196E68F1E95D5163823DFC5CA382B7599BBD6E87B1FCDDCA182FE338232723471DB8ED9E477E4F7B6E81B43664E7C9D6D0A58722ED287E437F7C0364F79D7969A43D0D6CF2D363E8A3D9D3E8BDA174C19B40AF7D17C6DE66BFFD32
	F7652D6C47C50D3CB64BCF223062BE31E29B3DA21E3AD9DE19713387B252C9A6AC472E4CB61209779E2C2A3255B3604A24EB0B7FC9476FB05B1E30F3770D7B4F7DE2630F7B0399E4D79BF9954310BEC525958CC546B79F74FF5FEBA63B696B0D98AC40D2C17A488AEA53DE2503640F7462AB6CC1CD116BF94E9E84E9693B1BA5781AE3C92B94F4EBF85FD7F510CC9F7E23A1B9403A3D12EF340495CA2AA83F167A6B14D7B4A3827C9E247C5217A545AC87576CFB73E87B565599A43D1964F87D125CAC39A04DE9A3
	3BB5372A845D60C4C2010BF5F4226CA95CD10CF8B2E03A570478A82C4D89D109E0FAAC740944BD29FB1A5389892924040E440850A6C6480DEEC905259435CD43FF1C742B47C55F7B5E50CEBC51724AD0BA3168C2CE10BF126CD0C0832ED449979C712B634001B502835EA5A632FD7C7152FBBE7B7C6F69C48629C60EB01581E94928629FAF299F99717894B181688F26FFDDCC0FE3EC91E38E04BE7AF27BBF3DB08C7B6DA09963252574EF997DFB867F16D1CC9945141D81A52611D9BF117D21A0CA1D2BB1A39156
	0B5BECEB061D52FE786C631F7AD1F55AB6102E19E45117CAC159F946282E4971E1C57DE072BD2F3F3464298F61DE8450A4783C3BC624F10FF79A3E309BA304788236EC26EE6F98323DC16D9D317841G348238599BFE5BDABE7D4ADDF99FA91FCCA73B1AF1E6EF40AD3569DCF510F4971871EB0652DDC8D2C2897B8212CB299AA43BC75451E1BADCA369034D03B089ED613AC7BBC80E66466A919A11C6DEB1E66A415CDB6FE0E6CFDE00CFGBED10210E8C1B4324F6CC1AA2DD60D6C575FE9B06376F19C66489473
	926B35628B8EF44EDDD1C78E31B951E78751FBE9E79E7DFBF05E1601D4AFD35AD7F0C17CF2B8FC9BCB423711FF327E6FF1FA7E7F8D27B533F10A9FF6B43005D1523C6CD034B00EDD9523952989CB9CCDE40A534D32A7A6910E7A332948E12828B35AFB048BCB85E23F5B2CEDB27F76DB1D7F33387FB764F43198496D0A7C6D77D1DAB7D88E78F5AEDE375BB774B6097FFBC45F386475EA12F72C5EA763EF938D531AE25443578F617CC1BEC66F54EBE2C348778D934D7FGD0CB8788B5CF42BD4698GG98C7GG
	D0CB818294G94G88G88GA5F991A9B5CF42BD4698GG98C7GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGG99GGGG
**end of data**/
}
/**
 * Return the ControlOffsetLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getControlOffsetLabel() {
	if (ivjControlOffsetLabel == null) {
		try {
			ivjControlOffsetLabel = new javax.swing.JLabel();
			ivjControlOffsetLabel.setName("ControlOffsetLabel");
			ivjControlOffsetLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjControlOffsetLabel.setText("Control Offset:  ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlOffsetLabel;
}
/**
 * Return the ControlOffsetField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getControlOffsetSpinner() {
	if (ivjControlOffsetSpinner == null) {
		try {
			ivjControlOffsetSpinner = new com.klg.jclass.field.JCSpinField();
			ivjControlOffsetSpinner.setName("ControlOffsetSpinner");
			ivjControlOffsetSpinner.setPreferredSize(new java.awt.Dimension(55, 22));
			ivjControlOffsetSpinner.setBackground(java.awt.Color.white);
			ivjControlOffsetSpinner.setMinimumSize(new java.awt.Dimension(55, 22));
			// user code begin {1}

			ivjControlOffsetSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(
				new com.klg.jclass.field.validate.JCIntegerValidator(null, 
					new Integer(0), new Integer(500), null, true, null, 
					new Integer(1), "#,##0.###;-#,##0.###", false, false, false, 
					null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(
						java.lang.Integer.class, new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
							new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlOffsetSpinner;
}
/**
 * Return the ControlTypeComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getControlTypeComboBox() {
	if (ivjControlTypeComboBox == null) {
		try {
			ivjControlTypeComboBox = new javax.swing.JComboBox();
			ivjControlTypeComboBox.setName("ControlTypeComboBox");
			ivjControlTypeComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlTypeComboBox;
}
/**
 * Return the ControlTypeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getControlTypeLabel() {
	if (ivjControlTypeLabel == null) {
		try {
			ivjControlTypeLabel = new javax.swing.JLabel();
			ivjControlTypeLabel.setName("ControlTypeLabel");
			ivjControlTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjControlTypeLabel.setText("Control Type:  ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlTypeLabel;
}
/**
 * Return the PhysicalPointOffsetCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getPhysicalPointOffsetCheckBox() {
	if (ivjPhysicalPointOffsetCheckBox == null) {
		try {
			ivjPhysicalPointOffsetCheckBox = new javax.swing.JCheckBox();
			ivjPhysicalPointOffsetCheckBox.setName("PhysicalPointOffsetCheckBox");
			ivjPhysicalPointOffsetCheckBox.setSelected(true);
			ivjPhysicalPointOffsetCheckBox.setText("Physical Point Offset");
			ivjPhysicalPointOffsetCheckBox.setActionCommand("PhysicalPointOffsetCheckBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPhysicalPointOffsetCheckBox;
}
/**
 * Return the PointOffsetLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPointOffsetLabel() {
	if (ivjPointOffsetLabel == null) {
		try {
			ivjPointOffsetLabel = new javax.swing.JLabel();
			ivjPointOffsetLabel.setName("PointOffsetLabel");
			ivjPointOffsetLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPointOffsetLabel.setText("Point Offset:  ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointOffsetLabel;
}
/**
 * Return the PointOffsetField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getPointOffsetSpinner() {
	if (ivjPointOffsetSpinner == null) {
		try {
			ivjPointOffsetSpinner = new com.klg.jclass.field.JCSpinField();
			ivjPointOffsetSpinner.setName("PointOffsetSpinner");
			ivjPointOffsetSpinner.setPreferredSize(new java.awt.Dimension(55, 22));
			ivjPointOffsetSpinner.setBackground(java.awt.Color.white);
			ivjPointOffsetSpinner.setMinimumSize(new java.awt.Dimension(55, 22));
			// user code begin {1}
			ivjPointOffsetSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1), new Integer(99999), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointOffsetSpinner;
}
/**
 * Return the InvalidPointOffsetLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getUsedPointOffsetLabel() {
	if (ivjUsedPointOffsetLabel == null) {
		try {
			ivjUsedPointOffsetLabel = new javax.swing.JLabel();
			ivjUsedPointOffsetLabel.setName("UsedPointOffsetLabel");
			ivjUsedPointOffsetLabel.setText("Offset Used");
			ivjUsedPointOffsetLabel.setMaximumSize(new java.awt.Dimension(180, 20));
			ivjUsedPointOffsetLabel.setPreferredSize(new java.awt.Dimension(180, 20));
			ivjUsedPointOffsetLabel.setFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjUsedPointOffsetLabel.setMinimumSize(new java.awt.Dimension(180, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUsedPointOffsetLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	//Assuming val is a real status point
	com.cannontech.database.data.point.StatusPoint point = (com.cannontech.database.data.point.StatusPoint) val;

	Object pointOffsetSpinVal = getPointOffsetSpinner().getValue();
	Integer pointOffset = new Integer( ((Number)pointOffsetSpinVal).intValue() );

	Object controlOffsetSpinVal = getControlOffsetSpinner().getValue();
	Integer controlOffset = new Integer( ((Number)controlOffsetSpinVal).intValue() );
	
	String controlType = (String) getControlTypeComboBox().getSelectedItem();
	point.getPointStatus().setControlType(controlType);

	if ( (getUsedPointOffsetLabel().getText()) == "" )
		point.getPoint().setPointOffset( pointOffset );
	else
		point.getPoint().setPointOffset( null );

	if( PointTypes.hasControl(controlType) )
	{
		point.getPointStatus().setControlOffset(controlOffset);
	}

/*	if (pointOffset.intValue() == 0)
		point.getPoint().setPseudoFlag( new Character('P') );
	else
		point.getPoint().setPseudoFlag( new Character('R') );
*/

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
	// user code end
	getPhysicalPointOffsetCheckBox().addItemListener(this);
	getControlTypeComboBox().addItemListener(this);
	getPointOffsetSpinner().addValueListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PointCopyOffsetsPanel");
		setPreferredSize(new java.awt.Dimension(350, 200));
		setLayout(new java.awt.GridBagLayout());
		setSize(367, 245);

		java.awt.GridBagConstraints constraintsPointOffsetLabel = new java.awt.GridBagConstraints();
		constraintsPointOffsetLabel.gridx = 0; constraintsPointOffsetLabel.gridy = 1;
		constraintsPointOffsetLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPointOffsetLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPointOffsetLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getPointOffsetLabel(), constraintsPointOffsetLabel);

		java.awt.GridBagConstraints constraintsControlOffsetLabel = new java.awt.GridBagConstraints();
		constraintsControlOffsetLabel.gridx = 0; constraintsControlOffsetLabel.gridy = 3;
		constraintsControlOffsetLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsControlOffsetLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsControlOffsetLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getControlOffsetLabel(), constraintsControlOffsetLabel);

		java.awt.GridBagConstraints constraintsPhysicalPointOffsetCheckBox = new java.awt.GridBagConstraints();
		constraintsPhysicalPointOffsetCheckBox.gridx = 0; constraintsPhysicalPointOffsetCheckBox.gridy = 0;
		constraintsPhysicalPointOffsetCheckBox.gridwidth = 2;
		constraintsPhysicalPointOffsetCheckBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPhysicalPointOffsetCheckBox.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getPhysicalPointOffsetCheckBox(), constraintsPhysicalPointOffsetCheckBox);

		java.awt.GridBagConstraints constraintsControlTypeLabel = new java.awt.GridBagConstraints();
		constraintsControlTypeLabel.gridx = 0; constraintsControlTypeLabel.gridy = 2;
		constraintsControlTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsControlTypeLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getControlTypeLabel(), constraintsControlTypeLabel);

		java.awt.GridBagConstraints constraintsControlTypeComboBox = new java.awt.GridBagConstraints();
		constraintsControlTypeComboBox.gridx = 1; constraintsControlTypeComboBox.gridy = 2;
		constraintsControlTypeComboBox.gridwidth = 2;
		constraintsControlTypeComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsControlTypeComboBox.insets = new java.awt.Insets(5, 10, 5, 30);
		add(getControlTypeComboBox(), constraintsControlTypeComboBox);

		java.awt.GridBagConstraints constraintsPointOffsetSpinner = new java.awt.GridBagConstraints();
		constraintsPointOffsetSpinner.gridx = 1; constraintsPointOffsetSpinner.gridy = 1;
		constraintsPointOffsetSpinner.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPointOffsetSpinner.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getPointOffsetSpinner(), constraintsPointOffsetSpinner);

		java.awt.GridBagConstraints constraintsControlOffsetSpinner = new java.awt.GridBagConstraints();
		constraintsControlOffsetSpinner.gridx = 1; constraintsControlOffsetSpinner.gridy = 3;
		constraintsControlOffsetSpinner.anchor = java.awt.GridBagConstraints.WEST;
		constraintsControlOffsetSpinner.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getControlOffsetSpinner(), constraintsControlOffsetSpinner);

		java.awt.GridBagConstraints constraintsUsedPointOffsetLabel = new java.awt.GridBagConstraints();
		constraintsUsedPointOffsetLabel.gridx = 2; constraintsUsedPointOffsetLabel.gridy = 1;
		constraintsUsedPointOffsetLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsUsedPointOffsetLabel.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getUsedPointOffsetLabel(), constraintsUsedPointOffsetLabel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getControlTypeComboBox().addItem( PointTypes.getType(PointTypes.CONTROLTYPE_NONE ) );
	getControlTypeComboBox().addItem( PointTypes.getType(PointTypes.CONTROLTYPE_LATCH ) );
	getControlTypeComboBox().addItem( PointTypes.getType(PointTypes.CONTROLTYPE_NORMAL ));
	getControlTypeComboBox().addItem( PointTypes.getType(PointTypes.CONTROLTYPE_PSEUDO ) );
	getControlTypeComboBox().addItem( PointTypes.getType(PointTypes.CONTROLTYPE_SBO_LATCH ) );
	getControlTypeComboBox().addItem( PointTypes.getType(PointTypes.CONTROLTYPE_SBO_PULSE ) );

	getControlTypeComboBox().setSelectedItem( PointTypes.getType(PointTypes.CONTROLTYPE_NONE ));
	
	// user code end
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getPhysicalPointOffsetCheckBox()) 
		connEtoC1(e);
	if (e.getSource() == getControlTypeComboBox()) 
		connEtoC2(e);
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
		PointCopyOffsetsPanel aPointStatusPhysicalSettingsPanel;
		aPointStatusPhysicalSettingsPanel = new PointCopyOffsetsPanel();
		frame.getContentPane().add("Center", aPointStatusPhysicalSettingsPanel);
		frame.setSize(aPointStatusPhysicalSettingsPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Comment
 */
public void physicalPointOffsetCheckBox_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
	if ( getPhysicalPointOffsetCheckBox().isSelected() )
	{
		getPointOffsetSpinner().setValidator(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1), new Integer(10000), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)));
		getPointOffsetSpinner().setValue( new Integer(1) );
		int temp = 2;
		while( getUsedPointOffsetLabel().getText() != "" )
		{
			getPointOffsetSpinner().setValue(new Integer(temp));
			temp++;
		}
		getPointOffsetLabel().setEnabled(true);
		getPointOffsetSpinner().setEnabled(true);
	}
	else
	if ( !getPhysicalPointOffsetCheckBox().isSelected() )
	{
		getPointOffsetSpinner().setValidator(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(0), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)));
		getPointOffsetSpinner().setValue( new Integer(0) );
		getPointOffsetLabel().setEnabled(false);
		getPointOffsetSpinner().setEnabled(false);
	}

	revalidate();
	repaint();
	return;
}
/**
 * Comment
 */
public void pointOffsetSpinner_ValueChanged(com.klg.jclass.util.value.JCValueEvent arg1)
{
	if (usedPointOffsetsVector != null)
	{
		getUsedPointOffsetLabel().setText("");
		if (usedPointOffsetsVector.size() > 0)
		{
			Object pointOffsetSpinVal = getPointOffsetSpinner().getValue();
			for (int i = 0; i < usedPointOffsetsVector.size(); i++)
			{
				if (pointOffsetSpinVal instanceof Long)
				{
					if (((Long) pointOffsetSpinVal).longValue() != 0
						&& (((Long) pointOffsetSpinVal).longValue()
							== ((com.cannontech.database.data.lite.LitePoint) usedPointOffsetsVector.elementAt(i)).getPointOffset()))
					{
						getUsedPointOffsetLabel().setText(
							"Used by " + ((com.cannontech.database.data.lite.LitePoint) usedPointOffsetsVector.elementAt(i)).getPointName());
						break;
					}
				}
				else if (pointOffsetSpinVal instanceof Integer)
				{
					if (((Integer) pointOffsetSpinVal).intValue() != 0
						&& (((Integer) pointOffsetSpinVal).intValue()
							== ((com.cannontech.database.data.lite.LitePoint) usedPointOffsetsVector.elementAt(i)).getPointOffset()))
					{
						getUsedPointOffsetLabel().setText(
							"Used by " + ((com.cannontech.database.data.lite.LitePoint) usedPointOffsetsVector.elementAt(i)).getPointName());
						break;
					}
				}
			}
			revalidate();
			repaint();
		}
	}
	return;
}

/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) 
{
}

public void setCopyValue(Object val, int newDeviceID) 
{
	PointBase thePoint = (PointBase)val;
	
	if(PointTypes.getType(thePoint.getPoint().getPointType()) == PointTypes.STATUS_POINT)
		thePoint = (StatusPoint)val;
	else
	{
		getControlTypeComboBox().setVisible(false);
		getControlOffsetSpinner().setVisible(false);
		getControlTypeLabel().setVisible(false);
		getControlOffsetLabel().setVisible(false);	
	}
	
	if(PointTypes.getType(thePoint.getPoint().getPointType()) == PointTypes.PULSE_ACCUMULATOR_POINT
			|| PointTypes.getType(thePoint.getPoint().getPointType()) == PointTypes.DEMAND_ACCUMULATOR_POINT)
		thePoint = (AccumulatorPoint)val;
		
	if(PointTypes.getType(thePoint.getPoint().getPointType()) == PointTypes.ANALOG_POINT)
		thePoint = (AnalogPoint)val;
		
	getUsedPointOffsetLabel().setText("");
	usedPointOffsetsVector = new java.util.Vector();
	
	com.cannontech.database.cache.DefaultDatabaseCache cache =
		com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized (cache)
{
		java.util.List points = cache.getAllPoints();
		java.util.Collections.sort(points, com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator);
		com.cannontech.database.data.lite.LitePoint litePoint = null;
		for (int i = 0; i < points.size(); i++)
		{
			litePoint = ((com.cannontech.database.data.lite.LitePoint) points.get(i));
			if (newDeviceID == litePoint.getPaobjectID() && PointTypes.getType(thePoint.getPoint().getPointType()) == litePoint.getPointType())
			{
				usedPointOffsetsVector.addElement(litePoint);
			}
			else if (litePoint.getPaobjectID() > newDeviceID)
			{
				break;
			}
		}
	}

	getPointOffsetSpinner().setValue(thePoint.getPoint().getPointOffset());
	if (usedPointOffsetsVector.size() > 0)
	{
		for (int i = 0; i < usedPointOffsetsVector.size(); i++)
		{
			Object pointOffsetSpinVal = getPointOffsetSpinner().getValue();
			if (pointOffsetSpinVal instanceof Long)
			{
				if (((Long) pointOffsetSpinVal).intValue()
					== ((com.cannontech.database.data.lite.LitePoint) usedPointOffsetsVector.elementAt(i)).getPointOffset())
						getPointOffsetSpinner().setValue(new Integer(((Long) pointOffsetSpinVal).intValue() + 1));
				else
					break;
			}
			else if (pointOffsetSpinVal instanceof Integer)
			{
				if (((Integer) pointOffsetSpinVal).intValue()
					== ((com.cannontech.database.data.lite.LitePoint) usedPointOffsetsVector.elementAt(i)).getPointOffset())
				{
					getPointOffsetSpinner().setValue(new Integer(((Integer) pointOffsetSpinVal).intValue() + 1));
					i = -1;
				}

			}
		}
	}
	revalidate();
	repaint();
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) {
	// user code begin {1}
	// user code end
	if (arg1.getSource() == getPointOffsetSpinner()) 
		connEtoC3(arg1);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}

public void setSameDevice(boolean isIt)
{
	sameDevice = isIt;
}

}
