package com.cannontech.dbeditor.wizard.config;

import java.awt.Dimension;

import com.cannontech.database.data.device.CarrierBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.IDLCBase;
import com.cannontech.database.data.device.MCT_Broadcast;
import com.cannontech.database.data.device.DNPBase;
import com.cannontech.database.data.device.Repeater900;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.database.data.device.Ion7700;

/**
 * This type was created in VisualAge.
 */

public class Series400ConfigPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjNameTextField = null;
	private javax.swing.JRadioButton ivjMinMaxModeButton = null;
	private javax.swing.JRadioButton ivjpeakModeButton = null;
	private javax.swing.JRadioButton ivjKY2WireButton = null;
	private javax.swing.JRadioButton ivjKYZ3WireButton = null;
	private javax.swing.JLabel ivjMpLabel = null;
	private javax.swing.JPanel ivjMultiplierPanel = null;
	private javax.swing.JLabel ivjTimesLabel = null;
	private javax.swing.JLabel ivjMpLabel2 = null;
	private javax.swing.JLabel ivjEqualsLabel = null;
	private javax.swing.JLabel ivjEqualsLabel2 = null;
	private javax.swing.JLabel ivjEqualsLabel3 = null;
	private javax.swing.JLabel ivjKeLabel = null;
	private javax.swing.JLabel ivjKeLabel2 = null;
	private javax.swing.JLabel ivjKeLabel3 = null;
	private javax.swing.JTextField ivjKeTextField = null;
	private javax.swing.JTextField ivjKeTextField2 = null;
	private javax.swing.JTextField ivjKeTextField3 = null;
	private javax.swing.JLabel ivjKhLabel = null;
	private javax.swing.JLabel ivjKhLabel2 = null;
	private javax.swing.JLabel ivjKhLabel3 = null;
	private javax.swing.JTextField ivjKhTextField = null;
	private javax.swing.JTextField ivjKhTextField2 = null;
	private javax.swing.JTextField ivjKhTextField3 = null;
	private javax.swing.JRadioButton ivjKY2WireButton2 = null;
	private javax.swing.JRadioButton ivjKY2WireButton3 = null;
	private javax.swing.JRadioButton ivjKYZ3WireButton2 = null;
	private javax.swing.JRadioButton ivjKYZ3WireButton3 = null;
	private javax.swing.JLabel ivjMpLabel3 = null;
	private javax.swing.JTextField ivjMpTextField = null;
	private javax.swing.JTextField ivjMpTextField2 = null;
	private javax.swing.JTextField ivjMpTextField3 = null;
	private javax.swing.JPanel ivjMultiplierPanel2 = null;
	private javax.swing.JPanel ivjMultiplierPanel3 = null;
	private javax.swing.JLabel ivjTimesLabel2 = null;
	private javax.swing.JLabel ivjTimesLabel3 = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public Series400ConfigPanel() {
	super();
	initialize();
}
/**
 * Comment
 */
public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G10FE0FAFGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DDC8DD8DC5519C6CDE22A29E2A5EB1AC60BBF3558CD375826AB2D31CFEC63EE5424229B5B2854D2B7EEE99BEBDA69B65B4DBE5EB010C08692FE958DD192E34ADF0090126008868890FE93CC88A1911501394004E1E61839401010385FB967FE675E39F36FA05A74F976F95647AF771E777BB967FC67BBBF771C438859EF06A7EDCA489604CDC9027C5F61CD02105EAB880FAF397FBB08795EB71B8455FF
	3B00B284F3F3A2BC6782AF72D633A9DD3834AF99520BA03DF63E5974AA3CE789D76F2BBE83AF8679E483AFBF72E70F864F67FFF8BEDBE87A597329705C86948E58F22074A3A47F7FBC1FAB63F100ED135F89EDCA9004E8AA73C8D12E8A6FBC42646E02E79450329AA213B11CA773F3A1DD8EB48594DA43E4A36019C2CBB75BD2A54B755C94A144BF32A3A10D6425320F14DC4BF4CB64E73EF0A7F504C9787127F5702C6E989EFC3F5FB5D25AEFF5BADDCEC96C9FAA357509B63B647214CE58AFD8BD36D21BB8EE6F
	97AD16FA7ABCEE9D91EBEDB60F6875F6D81D22837CDECB4A3E3EA8195A95849B248B6A88B6D4CC7C958B698DC4268E4B7FF8DA35DC3E306188A1764EED0665F75431F20756A9654F7D6A1C241A23443EE5B8D5560B86FEB2E02CEEA7DAEABEE35D7ADD4E817B2045F2CA7458C5EFE5F979D18A501A31FADDBD0C751AF5946B55D1C872DD8A6955C06BB87EEA9141936B4D26BC204AFA2C6FFBFF457AF6DF3CCF585077FE42F6037A365533FACA75CAFDCB22BEC9F80D465FD20944D0A470D683458125B618CDA5C0
	EDC0BBE85C573794413336CFBA6A9A99F1B9EB5D6EBA074D536E32098EF837D800A3E3A745FE1747A688AC367FD4D422D39EA4DB03FFDC93E2F5BBA3E0ACAEBE79CF02A4D5A6A46954ED4913BB93FC320DF98DCA5D229A085FE2E81CAC0677CD32AE431F2778C6954E5A5DD90C6D1E5CE8B6B99A514F5D5B30ACED09F788697BEB92366814E5410F8FA6C4B732B244B5AAE5E1BE3EFD1C78B87B9874BF20A620DE20902070E368639BDA8ECC6763E608B70F536AB8B5566736FA243AB13B43A6FAE4DF578C877335
	9EB13D63B356E3FE09B820EE23186F9D5175CBCC441E04254718BFD69FD37C42723E56B06F78E30131310D3761F25AD7121BD89B1AB87ED30A672AF05636DD94A76982604D83E27257533E353EC99BA3E1945F58240D111DC5BCC61AA1C600D87B9E7111B1B69A526B0092DB202F82D583F583B9DA300D1FEAA97B0C7D28C3553ECC77A16995F80ABE49E3EDF6FAA52B33DF6C32FA6C56BE076895E41927A46257BEBF3DE7E97F6E07BC0E09CE51E3156CAEA7D807919702494BE4FEADE590990FC8391D769191B2
	98F18BE877C5A98B1E8E2BD7BA6D36D9A5D111E07CBFCBA4A6462DDDE0918C407B5296622F54E2B24E4D037708967415E312E0C22B59145B0AFE710CE5928802DBF4401CE06FA74530FAA6B1C61FAB96F85C7D3E9B632EBE6D5BC249136F6B469D1D1FDAD98C5537EA636EF7549EFB170A3BE7987FB36FE3DF3E22F9B03FBEFD014426836ABA97A8027B765BE379AC666A2CDE91236C048782C434A91EE6325F9AA7F308B8AEBA25FA51ADBAED7062155B785E7194A57E86DCBCB66E23B8990F0EC200F8B97E40F8
	86434106BA1A117F9065DBA549E36F9B1344D322A4591D030A7EAADA962F5FD067156B9AF31154F59954F18DD09C2FEB57C416AA0E5ADC2E61B1F7A769A1F2BC5EC6675D0DA01F5D066B2FC78A49FA2C0A27D75334426FA954369D117D5B4647D8C09E263FF98C5ED424447CC2557BDBC5D7BE7E2B2FE89E2C6D1BFD46315B61530F5DAE1FFE6C76780247264D27971B0CB76CA3EB955704D3956BEE9F995FE0DD6894E90763AA82DBDB7E3E1354436E34CBE76C36C1D1BAE53FA08AC2C89B09593768985A8D6F0E
	B6DC13CC3C05E3601E8B77884BBEF9CDF7FD34609BD5894B65D8DD53260C012CCC2313248F098EF1849CF554B5661498BEC147E79977327E367D92115DGB6D25B4D26BD6D585FDE1AA4FC6CEF7220F25465F030EA727A47DEC8A77E92C747C41836C807EC95A7F16E39102961F5D99DE34A1C766285B3BC9D2A3E4C7067AE6A17BF6EA2693F435C7E3A0B1B85B2B7B11B4C5F11D6E2536D310F4050C786A779FB6A7B436A6F29EA28A779761A02E7A810095ADBF182BE7C843636BF6037360FE8275F8F453836A7
	F3BD43BE2058B350993E140873D8FB3A7AFB4B665D2F5B060456353336930EEB57157517095DFC404B0FE31B8CCC294740DA2F5755EF27533A6C371CCB6A76E5586B173642F3406E71429A53A67A50777917C8D9493CAF43327F3EEAA1EBD53DEF21FE3A30A99D9C33170EC9F6B219DBA568F36EB109FD58316F1FB79D49322D7A63781D536AA26DB98569B0204547B1564FD4287D34AC76EDC3BFAD177D33A640CF57FDCA720B87BC79847A69EB17B2D4FE026F96D8D28DC2F4717164320945111CEE3F1C04F1A4
	6038364721DD3F661DD066067293017384DBDFBEC66BDAFF022DAF37F37C1794F728F0366E7C0062A49D863C748EDCCBBD78D73A3E0274228E2C57F31753D4756AF0591D922F530D1F123E607258876DCE4442A886DFEDF440E273643B34FD96034DE5C08C4BF592ECAD24B7F6605C78240B2CFB73BFB01B2A00BA015CC07300A20016FF00F24FD339D8C88BA74D26FC209A20DE20D9C091A7D16E0C4B2D532EC6C472A14F4E8F746763889DBCDF462A6432AAE59C1FD15EE6C1195F7478E9BA984E69A40ED26476
	E4ED70AFFFA56D6D6369C7E8FAD2564992CEBAB653FEB8699A03462CBAC55A436E4031EAA578EFADD0AA607927585A067553FBA7C8B987E0318F5357A93758AFE4F7925DB2A76A0600FC9850922055C07127F01C7BEAA9A9A35919E9337611AD9939AC77162649F8A7CC280DF651E1633C245D4426DB348E13EF1A3AB1C9A213G6365EDA3F3C03B5D59EE75A9DC467B835D3BE89FF3C8F63743AEFA58D841F8515E74C0DE8563F53E2643BB90C66B18674ED6793E271379A5841E0C7F25D1B5BFC666A71EC67EA9
	0B1A1FFB1A716BB93FE0DC0F5F8DCFB6173CD9231ECB9CB2BF941EF7F119FFBD28A733147039CC5ABBFAB2F1705C884446071B9C78DD1A5DE5B635F5E17B6F6FA57D0E0E53672D6356D26B04E4319C7358EDF55641B69ACDECBCFE0A76F722BF2B8B4763ADA3F4BF8A522B0156F561F86C5B0B7BCE0B6F3EC3E88BB9223B4F965B39B7A1718C6BEB85E7147138A66A13841667E9A766E9837E22B3843B462976A9E963C7938421941E0C7FFDBFBE993F5F92547CDF7B71C9F99F7053FF4D2566939F6D1475D95D9E
	FD9B6B367151C841717DFD06FB08EB65BAA5F733E7642A570530EF9FCFE0FEFFE9A0D76DF7DF29F782BA204552C23B922B7FA9F732BCFE54F423EFAFB52A77B017EE5F1BE0B6485FA7673B20DB716D7A7BBE1173FF3BA0CFBFFF5EE5D999160DE29956F4E34C3CD277063E6EC92B4D6E42BE4D742F3320FE7AD9E8C30AFDB20EF3FB53D93479D22101BFD4FB22E7EAD0AF64AC46609379A48623A09D8D944773F879A04A8A67D076D086112D02F4875050B914EDF8075B6532C55954AE242301624E619CF0CB40FC
	BE9DB13DEFFCCE3DE7E72C475689CF513EA9FC285D37FA0E62299FEA772D52C6F0FC48855E540738FED8C15BA596529B0132FB4C269220B620219EDCCF1CA8A8972659336AF4395B60434B217A4CE775BB677CFCFB0B8FEE41FE3064075F9222663C3049289FCC753078DFA4BF1BD63ECEF1D63F8F0BC97DD682AF8EA875A378F6812A826A78886BF7CBC1892BDF034DAE55CA1235FF082E0C31F2DDF6F1C20149A44A6A566DD6574D76914B5FA7BF67FD943876E0E5FA28040CC16140DF8A349AE89DD06A47D81E
	DF95CC3B8F5C608B328F7C50283ADCA59F7B1723C6136E7C98E37FC68B519B52702764F468470175E1FA36F13DB63ECB160FB2546B29516A2DD449466A6831983F300B44F27C47AC46F3B87EE50A97FC2260AC765750B10424FB00175C0B75CDBC244DFFC92F12670ADE237C1F2563CCCC2FB67FBFD0BC5D2A4D3FBA83732F875EFAAB665F720EB67FC5FDCA1E11FDC679BFC1472E65FD5A7C1F277806BEED7E655918FFC93F5934269F737F6508405BA2CE751EB5F2657703CA2EE89E2C9FBDDEE83C9F3426D0FF
	BF6859C27D7D20350541770356959A6F873DD4285E8FE2E34016D2B2864C3219CD61C04B0156G2D336198F0D551EE010E81BA63D103EDD0BC6A22A7G925F0FAAB62E6BF071746786D2B1797E6FF3415CBEC26CA3FE2118341764F2D310E11F96939F41A7641044C4E5FCB65D63F0088394E65F0CAE3A46C9964D26DC913FA37F7B8C41CAA0DD4D315EEE7C36EC9251872BCA0C6B343AC43F7D1EAE51EF3F581260FB51F1A5C66D4B4A97B30A75D8404B7C3E9D4B3C18E39B68DAE3A3245387903B1B2E1176C03A
	0AE3AFD22C8D52BD9C7BE1B55A9B625845F1140B62D84CB84AAD6718172E1D3287E1FDB308589FEBD12EED907BFC482E82F8AE02F49450EA20F5C009C3E6D39ED0B9D093D08F10B404BAB7D31D39100EGDA81948714EC073C001A0006006682C558791D067CF0B2AE8DAAE35822C17DEFF4A3A2E5A365516311B2B7493C9E15CC1E6ADDB250A514A873D8D9D3F61132A62978B35DFBA7F6369B70D284761D320D4619022B7787F2025846FD865626BB8E11B6DD81FE0E31E3BBAFBC0C6DAC1CC77F2F4DA0ED5681
	E98750DC20F020A820E82035C09B015207218D07D1674FD4279A52BDC0D3C00B00168245GED844AF3C01F856AF120CEDD86719D49C32F6CD186B8D238DC0E8E39AC08C72B74662A5E576A58AB981EBE2ED8D9C7B2346D2C67E7A342F6B485E9C775DE10DEBC84EBE7A45626D6BA0ECC011FC39D584E3196EC67A55C7FEB32C91BA50E18CD79C055C01DC08E20D9C08B00A2011683450C204E6F284EFAC827BB210F81F5G3901C201A20122015683253BD067D0F638CED9C99E6459A967CD5E170FA86D11A83FBB
	64F2640F4CEC8C50238586B6147AD9F54B0A6D1422EAA7F5BBE21FB74B6DEC94A7B3A123B10144811BF736179A4FF12F14CEBFEF3FD9CA7772E13E6D1CF40B8C5BD3CA77F887863CA2DF9F941626F32C595B61725A4901G0B65B9EEF2DEB260F2CA8E970933C5F44EAE0777EA976E85E6F69399898A9369A2F75A128EC2D248F69379096E7AA0BEB0E9A89E7489919F50FB94EE65DB15651F64A17977GBE844472B8ED8F16C712EC9B1F0B5CCA9E840B047425CDAC8FD61F6D9692837DAE47580853AB673B1776
	AF32BEDB6A46FABFC0B12FC84E33C49B13DBED41731D15DCEE733B7E7EE9BA247867716A697C1324AA3B56BFB1AA7FB05B2107955BAF0D87333DC5E5D74FF61C1B2D35943FDCD793384FFCBB5F470CF74B7B4C235017C7597EBDABC7CA5DF4F55CA293F92F825D36D1D68E126E9DD56A484A71C19651EBBDFB6E40993BC7330FFCBE0B1EEF1C2DD033E4DE6390693F6DEEE5E71A61A1FD84EF9D5262FF9C25382845EFF3EC21F6584EB7436E39CC564874660B3CA14E70046EAD5436DA36E0A7315BB0BAE6F5F855
	F847FEAAAB6F2833FD64D53B347B48442F0ED15CE73E5820478F617C6F16EA773189BF0C731FC95163C7F0FEF9119EFFA967EF4F536E2393FEB4677FDB259EFFAD677FBCDD0F3F01737F21C20F1F6AC17EE379FA7CFC8F763D0AC301B15BFC88E3365243E2368D1E3D9EF65E44E2765E435345AC4E95645D873A73BCCA4C06FB34B17B5C566D81B1DBA173CAB237E9E3368275C277247A472CFC36E1FAA355BFE6E57C0ED3297EB1AB6377536F82B9E6E5ECE44A6C9733D81EB9EFA4ABB1AB4BBECF77CFD4B1AB63
	730FA6ABB1DB415AE4FF26B6A609DF9773B6BBDB2947DF467947CEEBE30E70D7F3FE2FDD0F9F477936CABD7EC64E3FB5CD9BB3041F6EC57E69FDFA7CBD1C5F9F903304DF4579B1E5FA7CB64E3FBAD90F5F6B4518753D939833B772F39E494BE2F69EBC433D6CFC14456C1FAD53452CD99533D1203B522B44EC2CD79B33FBD382E376G2EF17B83E276G6A7D768DCD4C9EE0786018A6E6E55C7C11A6E6E57CEBE32A1815B17BB17F184572F80AD5B1AB4BBEDA2409D9996F5C2C0AD979BC706D9CEDCC923F2E63ED
	7670E1BD3EA0A17F6B595A18236D427957CE68718BB8BF26C90FDF4D79B76C50468C61F7F0FEE1159E5F4679261DFAFC9F675B8346D9421F47792B366A7143014F569D8F13ADF939ADDEBCC376271C62C4BE9EAB16B6428232E0374DEEF53886F717641717646F2FAC447D7C7089759D0E2A2D4506F7B816C8ECFD3FC2FE663D7C3AEC63318F55B6529766A4ECB230A10CB15D54B165EC6CCD9653823DFBAA7F476A65FB1F3C33BCFE150A678EF97FF827D07F7397B63DE510C70DECBBD415C74D22B9C94C666335
	0A2F765B5F567715576A74FAC50FFDC065AEA1301C6B0FB01B7FD52F58FC4F12G4F7DFC8E791D052EBFBDE3A2134DEBD46AF76498F9E7FB418F5760B9508AA837B00EE7E49935F896DBB50EE7E4C7EA092C9B52F3014201744AE6DC5E3EA612E27693F763B9E1944F733B8DF8AE371EE33FA94535F54184E219DB916B6558A07F3B10859CEB4CC3FB519C5B4A4F8913FD08ED4BC02C06E32BAA9073F1EC73AE44A2FDF84EBEFA887D9787D87EA44A7E60907A2FFB927D77EBAA9B8A698820E513B3755FC34D0A7F
	06B279B9AD4FB334924B17FE81314B2751AFCD9C3B450ED888473ED609D89447D627717660583CFD0895DCC4ECE1BAE23D9C4BAAC7EC8147FE104C5B63A27A6F3EF7787D79A9402678F96EBB68BF5F947AEF1F0548C6C2FA85D06C54CC7D975722782FA7877D370167A9D4E1794AAFA17633EC744B9047FEB401D8B847321A91DB433107F67076380CD8AF4F2309E3161D7CBC1AE361BC7E22B816E8666DF1994FEB6737922C60D333297ED35CA75F769E394C5EE5BDAF1F94B6503FC5222785GF6F8DCEE51A3CD
	B2145E2C144FA0C25B08FF180D4E775EB632D15BA7354038F5226FBCFC83F7DA8785BC43383F1D8CC8B6A8C78850DDC0CB0122016200B6822507401A8A289A2893E888E88AA894A89CE889500A902C47836F97862907FCE9059EFCF82C6CCE9B5B6FF9AC609CF1BAE2FA3F781CFA2F54FE3EB37B6F5171B4A6C4FBE63F1462F1A15AB37B06DA6CAB71406B3E8A7737D2F6137C17DE653F8F96AD2757DE9538BF46746EEB50AB7786D9BE75EAA33D5F17862BEF3E215E2BDB0369D59A6AB9027E4DD81321DEEB1E1E
	DE0FAC6FB6545BFED0CFEF16AC3F40D0AFABC3CFEF31AC3F4CD06F098ABD3D5532FC1C211EF9171E5EC6D9BE7D9AA33D0900F36E4F966733BF275E0FE62C477A410707C93C6F39C65BBFEC94AF3FC65BBFDE60F3F1B570422F417EB112C972CF1C656F0FDCB9DDB24B48CF73AB754ADDA34BF79A6ADDF3BAD8FD078C7596590369CD996A056B1693A954D06F29B45DFB9A32FC14215ECD7B7474D64A723106FADFCF57530B176513E79B6965166B696549726506FABFCA56532B17653B8D759E8838077159627C51
	4F2917E879FC7D63F63A9EF24C56760FEFD15CB7DB5BBF8ED830FF4C82DE5CECFEBE12C372EF1B636F0FDEB9AD4DB172D3D215DE39674A720B8C751E498ED65FC8C33D1FCC8453DBEE28175B9CCCEF0D215EA3BB74745649724235C6FA222EDF32E57982C33DA3BB75742AE5798EC33DEF684E9FB6D95EE72817E256531BA74B076B6831F55942D6724E6FC630A5244C3B3D156C99103FDB026FFE7B857AE7A627C050B948780F359B1FEF7D2CFD7A732DDF366B5FCBF93EBD70DE4AEF5B756F252CEFD75FCBE158
	1F5A493AFA9056145DDC6EAF9CBB4B311729CD021D315B24A10EA79D5FAA63CDA24990F173F1D22E890AF54BD83A8ABB0BFF03F31C6C7FC11507DC9E54FD7DB8BD0733BB9CAC7D861531BB3D22447D7156F1D2C63B5B2AAAF781474E72367E815DFFE06FBFD63D3756DE793BDD36AB1A87BB8B4F277B89CBA0E6D7DC0B6763F60A45C0FA9D47FE3A9B4F4CB7A8D8834AB54DC52C2C9165BAB916D40658DD9CAB5D0AD8BC472EA9C22C7ECB087565613D1DB00E7DA48F6542B93677A04A2D67583587795F59F02C08
	6ED934DDE7B675DC07580D997C3E9047FED3416F8BF12C65G3FAF44315D3BD0AE7BFADE0FDDA817FFBD1FAB8EAB31F3DE757E5221AB9FC729072FFC9C4D227BAAEE286FAC6E07D594DBG69880ED5E4713B979CDBDC09F2097390CB63ED174E310527916B655877785D31C80E5DD7496F71FC993175F4CF2683523D9CBB3D8D659C9C0B5807F2CBB916D20AF2ABB8764FF46FA677863329608644F670B82A64D8D9B95A1B453150F2148B65581349A8974B31C3A9A8376E860C23EFDA145809D43D3FE33972F1F4
	720A66416A6C237BCB55B71ACD9DB7229F9E24188D529247763E023E8961D8DE954AC5F36CD2954A45F06C0F59081504A2767484E2D39CABEBC6ECA547FEC5772672EE825D1B90EB6463D19547262AD0EEAE47FE414B924631AE3A07359652719C3BFFA70F5F2FA03634826DF5F0ECA79F0FFAB816EFC639459C3B4A0CF2D1DF41F333433B836FE39C5F0DFBF149B723DC48EE7F7B98371CA0B1474E501E5F7DD96E2B24887AF7A6F2A10FC2C1FD9F63079D0A6D4646E0364D02F297C24F769E307D1E7C4E6C6515
	CDF76FC5FD3F237CE67FFBAF7510BE61E72FE16BF465D35B6B5058338558DBD8944C5EF60DBD37465EACC8FF64674BDFFC207872693C4FF2EF4668EECBA8585E6EE7DBB825583E6D609739B73388EC635F2330F82B4D700F3737BA153C6E4E8816D74AB4F9C55C2C3CB35B0D27955B568341EC27CDE3FB494D6CF74D945B76AE457642DDDF4476B2300DFFBBA71F75E686765D6FE6723BD43C6F2E4D7477653CB3CA191A33E652F7E340E60C4AFF6B837A6EF73A955BFB024604366FEAEDEF8C683B771E1EC95FCD
	8ED36C91BCAFCC5B579E89FA87CE5BF78BB4762A836C7D34F2A6FD37C9E32FA7CC693B2C5D36EC73EF37A74EAA3EF5EC1BC9DF567AF6A8CC5B177FF4CE311DDFBA133E2C35AD05E97B722AF47FFA6476A8F9D54C28AFEB738A895376657D2A716E1B65B369EFDA5B734234FD397324E23BBD650B58DE90266D4BB7668476653BF330AF5F9506FDF9CD0E3FAFA7D4E370FB2F4C24AFAF811BCBD47EDB91266D4B8BD5E3EED855CC7A3256766AB0EDDFCE4919C9DF0ED559A3F8FCC05F7B4344CC7A32B05F5FDE76FC
	2D3D4666197465FC0D3D4A795A3E3CE107FF3BBD2E1AF3BE1A513C2C75ED4DFCEDDF0ED74DB971C15BCD5B1735365B66EB7B7248CE7FFA0C29467BA7F64E24AFEB736A1E2F6D4B8BD463DD650C66E52DED5BFCEDDF3EDFB55EADB0FF915B6E795A3EAC6E763FBB726C07824FF793890BA0F7D24ABE8E4E7F79A74179FF69D5725A59273C5774AB6F7DB665FD0E283C5FB3A070BABC1C09F5B03E6733F2D0F9FFF1A8F839B24F8767578C876777BBD4E59ED1155929AA0FCBD59E37721E69D16A75C04E7475BA68D5
	5E3B2560653AB8961CFF5BF8F07E0393CADEEBFD4A7B2AA34A7B45DA657D36BA657D4123641D7D0E6E538D4B20B3AFFEC1C81204AC768BCAE36C178973498F0BD8A4A16704F3DF07E89DAEBBB1B0C0DF7249DFC0CBC2EA3B5DD956EE75512457A32479DD2AFA6876037F317259AE2FC9585CAC3CC67E26017CCE93591A96ED640708645FA7EC94D25C64F7F1484FECB58B09DE7AC872CA138E31CD489EB8C9FE0B4A8B93DBADEEFAFB21C0EDA427B27223C6E5CDD66F10C4B62A5FED93921D64179405CC46F6D81D
	03E5EC877CDD9FE954CD353D540324EF8AD05EACDADEE22514DB493AFF7B5B3BDA56C6190404C6E12B43E535B5DA7BA51727D9C81106BC22F7486530795CC7EA2821E47A49CF27B0B03703D71FD436145C2F64059E765DBAAFADAE2DB1015CDE964A28A4998ACB39046D68105583D9091EE6097CB2244B3307732EFAC35A3B6A582BBD8942A639122342E5A1496716044D8356913BE392DE58F5BFAF6124C8703F476A748E38BCA3D032FCCD491045B3A83D79617279777C659C4DC0D11504B719ABC135ECD0F40D
	14558E8C581DA2B7GFE8573BB39F9A2D32A1149745D145372644BFD90877502E9243C1C7E3B1F7EDBC17E5DCF117D9459DF814EDDAF847DCF4B77E11DE970C0F93653721066AFED0496141EFE6161BB1E29DE3F8EEA57A8A451DF0D02C6C8D7240E0F0D74091EB90B676FB40745DECF5A48CDC245C76C267938C5D243E6FA8690BBFE40E27A77BBCD89C2E23330191D09C07012038D88DEFA6E51AC18A4171B465DD6C1D967E450421A82221120DB82430A40E6E8CDFEBF11G5B28F4BA7DB7EB465EA2DAB9D477
	D5DE49B7188FDF9758C0C386BA7A23C89CA585A653961976D37CD0A23F5B8FA553D912ECA5D197CD6068E6B58AA34A01FD6406F3FEC16B59F30512E02E60C9C8195A5DB098ED02F15554BAC45E5E03A6EF95AD79FFEFBDDE7AC459AB69FE3ECA1429568FFDC576013FD790B5692245FE281A33598FCD143D680F626F217B23A679690FE687487EFF5D7ECF550D5D19AE7CD4FDAFFB436CD4433B5D71374AF7BFEE537EBEDFC28B69527500775E067781CFB66F930C6FA41E9A31BA9C1255B1DC6B34117B09F51312
	287C3D7F1696E25F0BB2B2D33EAF59C24AFDDE25733FD0CB87889523E3081C9DGGB8E0GGD0CB818294G94G88G88G10FE0FAF9523E3081C9DGGB8E0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG569DGGGG
**end of data**/
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getEqualsLabel() {
	if (ivjEqualsLabel == null) {
		try {
			ivjEqualsLabel = new javax.swing.JLabel();
			ivjEqualsLabel.setName("EqualsLabel");
			ivjEqualsLabel.setText("=");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEqualsLabel;
}
/**
 * Return the EqualsLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getEqualsLabel2() {
	if (ivjEqualsLabel2 == null) {
		try {
			ivjEqualsLabel2 = new javax.swing.JLabel();
			ivjEqualsLabel2.setName("EqualsLabel2");
			ivjEqualsLabel2.setText("=");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEqualsLabel2;
}
/**
 * Return the EqualsLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getEqualsLabel3() {
	if (ivjEqualsLabel3 == null) {
		try {
			ivjEqualsLabel3 = new javax.swing.JLabel();
			ivjEqualsLabel3.setName("EqualsLabel3");
			ivjEqualsLabel3.setText("=");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEqualsLabel3;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getKeLabel() {
	if (ivjKeLabel == null) {
		try {
			ivjKeLabel = new javax.swing.JLabel();
			ivjKeLabel.setName("KeLabel");
			ivjKeLabel.setText("Ke: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKeLabel;
}
/**
 * Return the KeLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getKeLabel2() {
	if (ivjKeLabel2 == null) {
		try {
			ivjKeLabel2 = new javax.swing.JLabel();
			ivjKeLabel2.setName("KeLabel2");
			ivjKeLabel2.setText("Ke: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKeLabel2;
}
/**
 * Return the KeLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getKeLabel3() {
	if (ivjKeLabel3 == null) {
		try {
			ivjKeLabel3 = new javax.swing.JLabel();
			ivjKeLabel3.setName("KeLabel3");
			ivjKeLabel3.setText("Ke: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKeLabel3;
}
/**
 * Return the JTextField12 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getKeTextField() {
	if (ivjKeTextField == null) {
		try {
			ivjKeTextField = new javax.swing.JTextField();
			ivjKeTextField.setName("KeTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKeTextField;
}
/**
 * Return the KeTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getKeTextField2() {
	if (ivjKeTextField2 == null) {
		try {
			ivjKeTextField2 = new javax.swing.JTextField();
			ivjKeTextField2.setName("KeTextField2");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKeTextField2;
}
/**
 * Return the KeTextField3 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getKeTextField3() {
	if (ivjKeTextField3 == null) {
		try {
			ivjKeTextField3 = new javax.swing.JTextField();
			ivjKeTextField3.setName("KeTextField3");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKeTextField3;
}
/**
 * Return the MpLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getKhLabel() {
	if (ivjKhLabel == null) {
		try {
			ivjKhLabel = new javax.swing.JLabel();
			ivjKhLabel.setName("KhLabel");
			ivjKhLabel.setText("Kh:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKhLabel;
}
/**
 * Return the KhLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getKhLabel2() {
	if (ivjKhLabel2 == null) {
		try {
			ivjKhLabel2 = new javax.swing.JLabel();
			ivjKhLabel2.setName("KhLabel2");
			ivjKhLabel2.setText("Kh:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKhLabel2;
}
/**
 * Return the KhLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getKhLabel3() {
	if (ivjKhLabel3 == null) {
		try {
			ivjKhLabel3 = new javax.swing.JLabel();
			ivjKhLabel3.setName("KhLabel3");
			ivjKhLabel3.setText("Kh:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKhLabel3;
}
/**
 * Return the JTextField11 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getKhTextField() {
	if (ivjKhTextField == null) {
		try {
			ivjKhTextField = new javax.swing.JTextField();
			ivjKhTextField.setName("KhTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKhTextField;
}
/**
 * Return the KhTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getKhTextField2() {
	if (ivjKhTextField2 == null) {
		try {
			ivjKhTextField2 = new javax.swing.JTextField();
			ivjKhTextField2.setName("KhTextField2");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKhTextField2;
}
/**
 * Return the KhTextField3 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getKhTextField3() {
	if (ivjKhTextField3 == null) {
		try {
			ivjKhTextField3 = new javax.swing.JTextField();
			ivjKhTextField3.setName("KhTextField3");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKhTextField3;
}
/**
 * Return the KY2WireButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getKY2WireButton() {
	if (ivjKY2WireButton == null) {
		try {
			ivjKY2WireButton = new javax.swing.JRadioButton();
			ivjKY2WireButton.setName("KY2WireButton");
			ivjKY2WireButton.setText("2-Wire (KY)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKY2WireButton;
}
/**
 * Return the KY2WireButton2 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getKY2WireButton2() {
	if (ivjKY2WireButton2 == null) {
		try {
			ivjKY2WireButton2 = new javax.swing.JRadioButton();
			ivjKY2WireButton2.setName("KY2WireButton2");
			ivjKY2WireButton2.setText("2-Wire (KY)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKY2WireButton2;
}
/**
 * Return the KY2WireButton3 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getKY2WireButton3() {
	if (ivjKY2WireButton3 == null) {
		try {
			ivjKY2WireButton3 = new javax.swing.JRadioButton();
			ivjKY2WireButton3.setName("KY2WireButton3");
			ivjKY2WireButton3.setText("2-Wire (KY)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKY2WireButton3;
}
/**
 * Return the KYZ3WireButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getKYZ3WireButton() {
	if (ivjKYZ3WireButton == null) {
		try {
			ivjKYZ3WireButton = new javax.swing.JRadioButton();
			ivjKYZ3WireButton.setName("KYZ3WireButton");
			ivjKYZ3WireButton.setSelected(true);
			ivjKYZ3WireButton.setText("3-Wire (KYZ)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKYZ3WireButton;
}
/**
 * Return the KYZ3WireButton2 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getKYZ3WireButton2() {
	if (ivjKYZ3WireButton2 == null) {
		try {
			ivjKYZ3WireButton2 = new javax.swing.JRadioButton();
			ivjKYZ3WireButton2.setName("KYZ3WireButton2");
			ivjKYZ3WireButton2.setSelected(true);
			ivjKYZ3WireButton2.setText("3-Wire (KYZ)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKYZ3WireButton2;
}
/**
 * Return the KYZ3WireButton3 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getKYZ3WireButton3() {
	if (ivjKYZ3WireButton3 == null) {
		try {
			ivjKYZ3WireButton3 = new javax.swing.JRadioButton();
			ivjKYZ3WireButton3.setName("KYZ3WireButton3");
			ivjKYZ3WireButton3.setSelected(true);
			ivjKYZ3WireButton3.setText("3-Wire (KYZ)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKYZ3WireButton3;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * Return the MinMaxModeButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getMinMaxModeButton() {
	if (ivjMinMaxModeButton == null) {
		try {
			ivjMinMaxModeButton = new javax.swing.JRadioButton();
			ivjMinMaxModeButton.setName("MinMaxModeButton");
			ivjMinMaxModeButton.setText("Min/Max Mode");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMinMaxModeButton;
}
/**
 * Return the MpLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMpLabel() {
	if (ivjMpLabel == null) {
		try {
			ivjMpLabel = new javax.swing.JLabel();
			ivjMpLabel.setName("MpLabel");
			ivjMpLabel.setText("Mp:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMpLabel;
}
/**
 * Return the MpLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMpLabel2() {
	if (ivjMpLabel2 == null) {
		try {
			ivjMpLabel2 = new javax.swing.JLabel();
			ivjMpLabel2.setName("MpLabel2");
			ivjMpLabel2.setText("Mp:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMpLabel2;
}
/**
 * Return the MpLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMpLabel3() {
	if (ivjMpLabel3 == null) {
		try {
			ivjMpLabel3 = new javax.swing.JLabel();
			ivjMpLabel3.setName("MpLabel3");
			ivjMpLabel3.setText("Mp:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMpLabel3;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMpTextField() {
	if (ivjMpTextField == null) {
		try {
			ivjMpTextField = new javax.swing.JTextField();
			ivjMpTextField.setName("MpTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMpTextField;
}
/**
 * Return the MpTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMpTextField2() {
	if (ivjMpTextField2 == null) {
		try {
			ivjMpTextField2 = new javax.swing.JTextField();
			ivjMpTextField2.setName("MpTextField2");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMpTextField2;
}
/**
 * Return the MpTextField3 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMpTextField3() {
	if (ivjMpTextField3 == null) {
		try {
			ivjMpTextField3 = new javax.swing.JTextField();
			ivjMpTextField3.setName("MpTextField3");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMpTextField3;
}
/**
 * Return the MultiplierPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getMultiplierPanel() {
	if (ivjMultiplierPanel == null) {
		try {
			ivjMultiplierPanel = new javax.swing.JPanel();
			ivjMultiplierPanel.setName("MultiplierPanel");
			ivjMultiplierPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsKYZ3WireButton = new java.awt.GridBagConstraints();
			constraintsKYZ3WireButton.gridx = 1; constraintsKYZ3WireButton.gridy = 1;
			constraintsKYZ3WireButton.gridwidth = 4;
			constraintsKYZ3WireButton.ipadx = 20;
			constraintsKYZ3WireButton.insets = new java.awt.Insets(15, 21, 4, 16);
			getMultiplierPanel().add(getKYZ3WireButton(), constraintsKYZ3WireButton);

			java.awt.GridBagConstraints constraintsKY2WireButton = new java.awt.GridBagConstraints();
			constraintsKY2WireButton.gridx = 5; constraintsKY2WireButton.gridy = 1;
			constraintsKY2WireButton.gridwidth = 3;
			constraintsKY2WireButton.ipadx = 30;
			constraintsKY2WireButton.insets = new java.awt.Insets(15, 4, 4, 65);
			getMultiplierPanel().add(getKY2WireButton(), constraintsKY2WireButton);

			java.awt.GridBagConstraints constraintsMpTextField = new java.awt.GridBagConstraints();
			constraintsMpTextField.gridx = 2; constraintsMpTextField.gridy = 2;
			constraintsMpTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMpTextField.weightx = 1.0;
			constraintsMpTextField.ipadx = 39;
			constraintsMpTextField.insets = new java.awt.Insets(4, 1, 22, 4);
			getMultiplierPanel().add(getMpTextField(), constraintsMpTextField);

			java.awt.GridBagConstraints constraintsKhTextField = new java.awt.GridBagConstraints();
			constraintsKhTextField.gridx = 5; constraintsKhTextField.gridy = 2;
			constraintsKhTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsKhTextField.weightx = 1.0;
			constraintsKhTextField.ipadx = 39;
			constraintsKhTextField.insets = new java.awt.Insets(4, 1, 22, 3);
			getMultiplierPanel().add(getKhTextField(), constraintsKhTextField);

			java.awt.GridBagConstraints constraintsKeTextField = new java.awt.GridBagConstraints();
			constraintsKeTextField.gridx = 7; constraintsKeTextField.gridy = 2;
			constraintsKeTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsKeTextField.weightx = 1.0;
			constraintsKeTextField.ipadx = 46;
			constraintsKeTextField.insets = new java.awt.Insets(4, 1, 22, 43);
			getMultiplierPanel().add(getKeTextField(), constraintsKeTextField);

			java.awt.GridBagConstraints constraintsMpLabel = new java.awt.GridBagConstraints();
			constraintsMpLabel.gridx = 1; constraintsMpLabel.gridy = 2;
			constraintsMpLabel.ipadx = 7;
			constraintsMpLabel.insets = new java.awt.Insets(6, 22, 26, 1);
			getMultiplierPanel().add(getMpLabel(), constraintsMpLabel);

			java.awt.GridBagConstraints constraintsTimesLabel = new java.awt.GridBagConstraints();
			constraintsTimesLabel.gridx = 3; constraintsTimesLabel.gridy = 2;
			constraintsTimesLabel.ipadx = 5;
			constraintsTimesLabel.insets = new java.awt.Insets(6, 4, 26, 5);
			getMultiplierPanel().add(getTimesLabel(), constraintsTimesLabel);

			java.awt.GridBagConstraints constraintsKhLabel = new java.awt.GridBagConstraints();
			constraintsKhLabel.gridx = 4; constraintsKhLabel.gridy = 2;
			constraintsKhLabel.ipadx = 10;
			constraintsKhLabel.insets = new java.awt.Insets(6, 5, 26, 0);
			getMultiplierPanel().add(getKhLabel(), constraintsKhLabel);

			java.awt.GridBagConstraints constraintsEqualsLabel = new java.awt.GridBagConstraints();
			constraintsEqualsLabel.gridx = 6; constraintsEqualsLabel.gridy = 2;
			constraintsEqualsLabel.ipadx = 11;
			constraintsEqualsLabel.insets = new java.awt.Insets(6, 3, 26, 26);
			getMultiplierPanel().add(getEqualsLabel(), constraintsEqualsLabel);

			java.awt.GridBagConstraints constraintsKeLabel = new java.awt.GridBagConstraints();
			constraintsKeLabel.gridx = 6; constraintsKeLabel.gridy = 2;
			constraintsKeLabel.ipadx = 6;
			constraintsKeLabel.insets = new java.awt.Insets(6, 20, 26, 0);
			getMultiplierPanel().add(getKeLabel(), constraintsKeLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMultiplierPanel;
}
/**
 * Return the MultiplierPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getMultiplierPanel2() {
	if (ivjMultiplierPanel2 == null) {
		try {
			ivjMultiplierPanel2 = new javax.swing.JPanel();
			ivjMultiplierPanel2.setName("MultiplierPanel2");
			ivjMultiplierPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsKYZ3WireButton2 = new java.awt.GridBagConstraints();
			constraintsKYZ3WireButton2.gridx = 1; constraintsKYZ3WireButton2.gridy = 1;
			constraintsKYZ3WireButton2.gridwidth = 4;
			constraintsKYZ3WireButton2.ipadx = 20;
			constraintsKYZ3WireButton2.insets = new java.awt.Insets(15, 21, 4, 16);
			getMultiplierPanel2().add(getKYZ3WireButton2(), constraintsKYZ3WireButton2);

			java.awt.GridBagConstraints constraintsKY2WireButton2 = new java.awt.GridBagConstraints();
			constraintsKY2WireButton2.gridx = 5; constraintsKY2WireButton2.gridy = 1;
			constraintsKY2WireButton2.gridwidth = 3;
			constraintsKY2WireButton2.ipadx = 30;
			constraintsKY2WireButton2.insets = new java.awt.Insets(15, 4, 4, 65);
			getMultiplierPanel2().add(getKY2WireButton2(), constraintsKY2WireButton2);

			java.awt.GridBagConstraints constraintsMpTextField2 = new java.awt.GridBagConstraints();
			constraintsMpTextField2.gridx = 2; constraintsMpTextField2.gridy = 2;
			constraintsMpTextField2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMpTextField2.weightx = 1.0;
			constraintsMpTextField2.ipadx = 39;
			constraintsMpTextField2.insets = new java.awt.Insets(4, 1, 22, 4);
			getMultiplierPanel2().add(getMpTextField2(), constraintsMpTextField2);

			java.awt.GridBagConstraints constraintsKhTextField2 = new java.awt.GridBagConstraints();
			constraintsKhTextField2.gridx = 5; constraintsKhTextField2.gridy = 2;
			constraintsKhTextField2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsKhTextField2.weightx = 1.0;
			constraintsKhTextField2.ipadx = 39;
			constraintsKhTextField2.insets = new java.awt.Insets(4, 1, 22, 3);
			getMultiplierPanel2().add(getKhTextField2(), constraintsKhTextField2);

			java.awt.GridBagConstraints constraintsKeTextField2 = new java.awt.GridBagConstraints();
			constraintsKeTextField2.gridx = 7; constraintsKeTextField2.gridy = 2;
			constraintsKeTextField2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsKeTextField2.weightx = 1.0;
			constraintsKeTextField2.ipadx = 46;
			constraintsKeTextField2.insets = new java.awt.Insets(4, 1, 22, 43);
			getMultiplierPanel2().add(getKeTextField2(), constraintsKeTextField2);

			java.awt.GridBagConstraints constraintsMpLabel2 = new java.awt.GridBagConstraints();
			constraintsMpLabel2.gridx = 1; constraintsMpLabel2.gridy = 2;
			constraintsMpLabel2.ipadx = 7;
			constraintsMpLabel2.insets = new java.awt.Insets(6, 22, 26, 1);
			getMultiplierPanel2().add(getMpLabel2(), constraintsMpLabel2);

			java.awt.GridBagConstraints constraintsTimesLabel2 = new java.awt.GridBagConstraints();
			constraintsTimesLabel2.gridx = 3; constraintsTimesLabel2.gridy = 2;
			constraintsTimesLabel2.ipadx = 5;
			constraintsTimesLabel2.insets = new java.awt.Insets(6, 4, 26, 5);
			getMultiplierPanel2().add(getTimesLabel2(), constraintsTimesLabel2);

			java.awt.GridBagConstraints constraintsKhLabel2 = new java.awt.GridBagConstraints();
			constraintsKhLabel2.gridx = 4; constraintsKhLabel2.gridy = 2;
			constraintsKhLabel2.ipadx = 10;
			constraintsKhLabel2.insets = new java.awt.Insets(6, 5, 26, 0);
			getMultiplierPanel2().add(getKhLabel2(), constraintsKhLabel2);

			java.awt.GridBagConstraints constraintsEqualsLabel2 = new java.awt.GridBagConstraints();
			constraintsEqualsLabel2.gridx = 6; constraintsEqualsLabel2.gridy = 2;
			constraintsEqualsLabel2.ipadx = 11;
			constraintsEqualsLabel2.insets = new java.awt.Insets(6, 3, 26, 26);
			getMultiplierPanel2().add(getEqualsLabel2(), constraintsEqualsLabel2);

			java.awt.GridBagConstraints constraintsKeLabel2 = new java.awt.GridBagConstraints();
			constraintsKeLabel2.gridx = 6; constraintsKeLabel2.gridy = 2;
			constraintsKeLabel2.ipadx = 6;
			constraintsKeLabel2.insets = new java.awt.Insets(6, 20, 26, 0);
			getMultiplierPanel2().add(getKeLabel2(), constraintsKeLabel2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMultiplierPanel2;
}
/**
 * Return the MultiplierPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getMultiplierPanel3() {
	if (ivjMultiplierPanel3 == null) {
		try {
			ivjMultiplierPanel3 = new javax.swing.JPanel();
			ivjMultiplierPanel3.setName("MultiplierPanel3");
			ivjMultiplierPanel3.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsKYZ3WireButton3 = new java.awt.GridBagConstraints();
			constraintsKYZ3WireButton3.gridx = 1; constraintsKYZ3WireButton3.gridy = 1;
			constraintsKYZ3WireButton3.gridwidth = 4;
			constraintsKYZ3WireButton3.ipadx = 20;
			constraintsKYZ3WireButton3.insets = new java.awt.Insets(15, 21, 4, 16);
			getMultiplierPanel3().add(getKYZ3WireButton3(), constraintsKYZ3WireButton3);

			java.awt.GridBagConstraints constraintsKY2WireButton3 = new java.awt.GridBagConstraints();
			constraintsKY2WireButton3.gridx = 5; constraintsKY2WireButton3.gridy = 1;
			constraintsKY2WireButton3.gridwidth = 3;
			constraintsKY2WireButton3.ipadx = 30;
			constraintsKY2WireButton3.insets = new java.awt.Insets(15, 4, 4, 65);
			getMultiplierPanel3().add(getKY2WireButton3(), constraintsKY2WireButton3);

			java.awt.GridBagConstraints constraintsMpTextField3 = new java.awt.GridBagConstraints();
			constraintsMpTextField3.gridx = 2; constraintsMpTextField3.gridy = 2;
			constraintsMpTextField3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMpTextField3.weightx = 1.0;
			constraintsMpTextField3.ipadx = 39;
			constraintsMpTextField3.insets = new java.awt.Insets(4, 1, 22, 4);
			getMultiplierPanel3().add(getMpTextField3(), constraintsMpTextField3);

			java.awt.GridBagConstraints constraintsKhTextField3 = new java.awt.GridBagConstraints();
			constraintsKhTextField3.gridx = 5; constraintsKhTextField3.gridy = 2;
			constraintsKhTextField3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsKhTextField3.weightx = 1.0;
			constraintsKhTextField3.ipadx = 39;
			constraintsKhTextField3.insets = new java.awt.Insets(4, 1, 22, 3);
			getMultiplierPanel3().add(getKhTextField3(), constraintsKhTextField3);

			java.awt.GridBagConstraints constraintsKeTextField3 = new java.awt.GridBagConstraints();
			constraintsKeTextField3.gridx = 7; constraintsKeTextField3.gridy = 2;
			constraintsKeTextField3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsKeTextField3.weightx = 1.0;
			constraintsKeTextField3.ipadx = 46;
			constraintsKeTextField3.insets = new java.awt.Insets(4, 1, 22, 43);
			getMultiplierPanel3().add(getKeTextField3(), constraintsKeTextField3);

			java.awt.GridBagConstraints constraintsMpLabel3 = new java.awt.GridBagConstraints();
			constraintsMpLabel3.gridx = 1; constraintsMpLabel3.gridy = 2;
			constraintsMpLabel3.ipadx = 7;
			constraintsMpLabel3.insets = new java.awt.Insets(6, 22, 26, 1);
			getMultiplierPanel3().add(getMpLabel3(), constraintsMpLabel3);

			java.awt.GridBagConstraints constraintsTimesLabel3 = new java.awt.GridBagConstraints();
			constraintsTimesLabel3.gridx = 3; constraintsTimesLabel3.gridy = 2;
			constraintsTimesLabel3.ipadx = 5;
			constraintsTimesLabel3.insets = new java.awt.Insets(6, 4, 26, 5);
			getMultiplierPanel3().add(getTimesLabel3(), constraintsTimesLabel3);

			java.awt.GridBagConstraints constraintsKhLabel3 = new java.awt.GridBagConstraints();
			constraintsKhLabel3.gridx = 4; constraintsKhLabel3.gridy = 2;
			constraintsKhLabel3.ipadx = 10;
			constraintsKhLabel3.insets = new java.awt.Insets(6, 5, 26, 0);
			getMultiplierPanel3().add(getKhLabel3(), constraintsKhLabel3);

			java.awt.GridBagConstraints constraintsEqualsLabel3 = new java.awt.GridBagConstraints();
			constraintsEqualsLabel3.gridx = 6; constraintsEqualsLabel3.gridy = 2;
			constraintsEqualsLabel3.ipadx = 11;
			constraintsEqualsLabel3.insets = new java.awt.Insets(6, 3, 26, 26);
			getMultiplierPanel3().add(getEqualsLabel3(), constraintsEqualsLabel3);

			java.awt.GridBagConstraints constraintsKeLabel3 = new java.awt.GridBagConstraints();
			constraintsKeLabel3.gridx = 6; constraintsKeLabel3.gridy = 2;
			constraintsKeLabel3.ipadx = 6;
			constraintsKeLabel3.insets = new java.awt.Insets(6, 20, 26, 0);
			getMultiplierPanel3().add(getKeLabel3(), constraintsKeLabel3);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMultiplierPanel3;
}
/**
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNameLabel() {
	if (ivjNameLabel == null) {
		try {
			ivjNameLabel = new javax.swing.JLabel();
			ivjNameLabel.setName("NameLabel");
			ivjNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjNameLabel.setText("Configuration Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameLabel;
}
/**
 * Return the NameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getNameTextField() {
	if (ivjNameTextField == null) {
		try {
			ivjNameTextField = new javax.swing.JTextField();
			ivjNameTextField.setName("NameTextField");
			ivjNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjNameTextField.setColumns(12);
			// user code begin {1}
			ivjNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_DEVICE_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameTextField;
}
/**
 * Return the peakModeButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getpeakModeButton() {
	if (ivjpeakModeButton == null) {
		try {
			ivjpeakModeButton = new javax.swing.JRadioButton();
			ivjpeakModeButton.setName("peakModeButton");
			ivjpeakModeButton.setSelected(true);
			ivjpeakModeButton.setText("On-Peak/Off-Peak Mode");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjpeakModeButton;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension( 350, 200);
}
/**
 * Return the TimesLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTimesLabel() {
	if (ivjTimesLabel == null) {
		try {
			ivjTimesLabel = new javax.swing.JLabel();
			ivjTimesLabel.setName("TimesLabel");
			ivjTimesLabel.setText("X");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimesLabel;
}
/**
 * Return the TimesLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTimesLabel2() {
	if (ivjTimesLabel2 == null) {
		try {
			ivjTimesLabel2 = new javax.swing.JLabel();
			ivjTimesLabel2.setName("TimesLabel2");
			ivjTimesLabel2.setText("X");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimesLabel2;
}
/**
 * Return the TimesLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTimesLabel3() {
	if (ivjTimesLabel3 == null) {
		try {
			ivjTimesLabel3 = new javax.swing.JLabel();
			ivjTimesLabel3.setName("TimesLabel3");
			ivjTimesLabel3.setText("X");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimesLabel3;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public java.lang.Object getValue(java.lang.Object o) {
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
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("Series300ConfigPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 357);

		java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
		constraintsNameLabel.gridx = 1; constraintsNameLabel.gridy = 1;
		constraintsNameLabel.insets = new java.awt.Insets(17, 11, 10, 3);
		add(getNameLabel(), constraintsNameLabel);

		java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
		constraintsNameTextField.gridx = 2; constraintsNameTextField.gridy = 1;
		constraintsNameTextField.gridwidth = 2;
		constraintsNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsNameTextField.weightx = 1.0;
		constraintsNameTextField.ipadx = 190;
		constraintsNameTextField.insets = new java.awt.Insets(15, 4, 8, 9);
		add(getNameTextField(), constraintsNameTextField);

		java.awt.GridBagConstraints constraintspeakModeButton = new java.awt.GridBagConstraints();
		constraintspeakModeButton.gridx = 1; constraintspeakModeButton.gridy = 2;
		constraintspeakModeButton.gridwidth = 2;
		constraintspeakModeButton.insets = new java.awt.Insets(9, 21, 3, 7);
		add(getpeakModeButton(), constraintspeakModeButton);

		java.awt.GridBagConstraints constraintsMinMaxModeButton = new java.awt.GridBagConstraints();
		constraintsMinMaxModeButton.gridx = 3; constraintsMinMaxModeButton.gridy = 2;
		constraintsMinMaxModeButton.ipadx = 13;
		constraintsMinMaxModeButton.insets = new java.awt.Insets(9, 8, 3, 34);
		add(getMinMaxModeButton(), constraintsMinMaxModeButton);

		java.awt.GridBagConstraints constraintsMultiplierPanel = new java.awt.GridBagConstraints();
		constraintsMultiplierPanel.gridx = 1; constraintsMultiplierPanel.gridy = 3;
		constraintsMultiplierPanel.gridwidth = 3;
		constraintsMultiplierPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsMultiplierPanel.weightx = 1.0;
		constraintsMultiplierPanel.weighty = 1.0;
		constraintsMultiplierPanel.insets = new java.awt.Insets(4, 5, 1, 4);
		add(getMultiplierPanel(), constraintsMultiplierPanel);

		java.awt.GridBagConstraints constraintsMultiplierPanel2 = new java.awt.GridBagConstraints();
		constraintsMultiplierPanel2.gridx = 1; constraintsMultiplierPanel2.gridy = 4;
		constraintsMultiplierPanel2.gridwidth = 3;
		constraintsMultiplierPanel2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsMultiplierPanel2.weightx = 1.0;
		constraintsMultiplierPanel2.weighty = 1.0;
		constraintsMultiplierPanel2.insets = new java.awt.Insets(2, 5, 1, 4);
		add(getMultiplierPanel2(), constraintsMultiplierPanel2);

		java.awt.GridBagConstraints constraintsMultiplierPanel3 = new java.awt.GridBagConstraints();
		constraintsMultiplierPanel3.gridx = 1; constraintsMultiplierPanel3.gridy = 5;
		constraintsMultiplierPanel3.gridwidth = 3;
		constraintsMultiplierPanel3.fill = java.awt.GridBagConstraints.BOTH;
		constraintsMultiplierPanel3.weightx = 1.0;
		constraintsMultiplierPanel3.weighty = 1.0;
		constraintsMultiplierPanel3.insets = new java.awt.Insets(2, 5, 6, 4);
		add(getMultiplierPanel3(), constraintsMultiplierPanel3);
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
		Series300ConfigPanel aSeries300ConfigPanel;
		aSeries300ConfigPanel = new Series300ConfigPanel();
		frame.getContentPane().add("Center", aSeries300ConfigPanel);
		frame.setSize(aSeries300ConfigPanel.getSize());
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
public void setValue(Object val ) {
	return;
}
}
