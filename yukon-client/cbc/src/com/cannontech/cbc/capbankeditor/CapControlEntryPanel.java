package com.cannontech.cbc.capbankeditor;
/**
 * Insert the type's description here.
 * Creation date: (12/14/00 4:00:59 PM)
 * @author: 
 */
import java.awt.Dimension;

import com.cannontech.cbc.gui.CapBankTableModel;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.unchanging.LongRangeDocument;
import com.cannontech.common.util.MessageEvent;
import com.cannontech.database.data.point.PointQualities;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.util.Message;
import com.cannontech.yukon.cbc.CBCClientConnection;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubBus;

public class CapControlEntryPanel extends javax.swing.JPanel implements java.awt.event.ActionListener 
{
	private CBCClientConnection connectionWrapper = null;
	private javax.swing.JLabel ivjJLabelCapBank = null;
	private javax.swing.JLabel ivjJLabelCapBankName = null;
	private javax.swing.JLabel ivjJLabelState = null;
	private javax.swing.JComboBox ivjJComboBoxState = null;
	private javax.swing.JButton ivjJButtonDismiss = null;
	private javax.swing.JButton ivjJButtonUpdate = null;
	private StreamableCapObject capObject = null;
	private javax.swing.JPanel ivjJPanel1 = null;


	private javax.swing.JLabel jLabelOpCount = null;
	private javax.swing.JTextField jTextFieldOpCount = null;
	private javax.swing.JCheckBox jCheckBoxOpCount = null;

/**
 * CapBankEntryPanel constructor comment.
 */
public CapControlEntryPanel( CBCClientConnection conn ) 
{
	super();

	connectionWrapper = conn;
	initialize();
}


/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e) 
{

	if (e.getSource() == getJButtonUpdate()) 
		connEtoC1(e);
	if (e.getSource() == getJButtonDismiss()) 
		connEtoC2(e);

	if( e.getSource() == getJCheckBoxOpCount() )
	{
		getJTextFieldOpCount().setEnabled( getJCheckBoxOpCount().isSelected() );
//		getJLabelOpCount().setEnabled( getJCheckBoxOpCount().isSelected() );
		
//		getJLabelState().setEnabled( !getJCheckBoxOpCount().isSelected() );
//		getJComboBoxState().setEnabled( !getJCheckBoxOpCount().isSelected() );
	}

}


/**
 * connEtoC1:  (JButtonUpdate.action.actionPerformed(java.awt.event.ActionEvent) --> CapBankEntryPanel.jButtonUpdate_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonUpdate_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (JButtonDismiss.action.actionPerformed(java.awt.event.ActionEvent) --> CapBankEntryPanel.jButtonDismiss_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonDismiss_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (12/14/00 4:50:21 PM)
 */
/* THIS METHOD IS MEANT TO BE OVERRIDEN. WILL MOSTLY BE USED TO DESTROY 
	THIS PANEL AND ITS JFRAME OR JDIALOG */
protected void disposePanel() 
{
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G0BF2EBADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DDB8DF4D5551546B558B21565428EED991AAED127CE2C19B1D646D2451126D8350DB6DDC2F514E56394DB3A0C16F63003B30CBDF979858264CF89FFB5008829A1F9AFBF82AF81C972724B8B89AFFFAF92A4B76F5DA4CF6E7B713D9B13D0BA1D3D4F4FFDB7AF77A5C0E5D8EBF36F794EB97B6CFD4E5E7B6CF36E8BA9FCE7C9C62A291010548C427F9DCBA5E4F9ADA1C92EFB3EAC30ECF3C296517D5B8F14
	C7361FCD07E7915425DCCA484AA5EFE4E7C339944A8B069332F642FB81F9B35B79813CC499478CF573FE665BB073B8661DE21CACDAFE69E88EBC378319816B86DAE226639FAD627884143773F724D493A15D96ECF3EFEF118EDF4B5B9D013ADAA0DB8536A9AC5B4B6BE3GDB8AC4GAF2CE0EDCB613919CAF73375B8EFB7F05B9232610707CD5BF0AC9DFFC173F95FF87EACA6775109C8A31529482336735248A9175FDB6612FCBE3FCF15DD23E52EA1979483C3126F126C76287E20555AA0856A215868D303131D
	12CFD60AABD15E1F74E6D3DE042C837EF194EB6D43B9F2437B95200515C2661FEC2B65B2AD3C3404A45F35BB2A4C6BAB192C05D5E119C72F74E42E2CC27EA74BF2F83FEEA84F87E27A3CD3F56A1A75E917FCE31292568A4622BA5CDCA6744A2E96FA7D3A9347BD8DE5A7D0C0439FBC0778BCA8AF81DAD6AD74BDF9D8686B7C53FD4479542B269DD174CD2AE6FAEE2C8E6B3B465713390B5A5C72A2341B2A8FE0DCA085E881D0BCD092509EEA6B63BB7F884F3AA135416F757AFDF501C03D628E367B5D32826FD6AB
	54F04CA13B7CC1B7A14C1EFF5ADBEAA00F20F59FCC35E5A426DB9F91769767381B34DCF94D14E1205B5255A526CD1CC76E3130EE76E3B8EF0FD0BBB143FBAA674B709FD2FC018EE76B5EC1F1AC47E13B63E21E1D5B05AC767445A457E2B7E5994832A8214E249EE7B24CBF9E16054D71D7F7639CAF83FC95D08AD07A09042CF701ECA7449CFFEFE765ECF35C8A769674C9CA5758D0C08A2A75E39E45AD8779DCB7144DB457C6447A1D3B66FEECDEE2AB75EB346601B3067332A656EC9ABC4166E362C4F8DE5858DF
	0ABA76229353EDE33B3606AE6A93F1A7589A26E93817624BF5B8DB5B5729EFE1B9916A4C5604AC567E0F8E4497DAD97B8C0DCFB545E3F5B863331C724772F228AB2EC948E26B7BFBB3465539D0DE82348CA889E8BDD0CEAD342B95EB1CD3D4FE15FE5429DBDF567731223761A9CF28C1295597D2A51FCB6E15029EE9C811C30437F93A8867F5E8CA3FE728FF3BE00CE659A787A555637781F749AB03B105D81BB5C5F958A6A8535AEE0FD706813C81A2782EADAA0027A205541E00DBD265F08BD61FD204B6711954
	8B9C01813C87EAF13EDE25717BB43C8F540A39AA2DC0ECA9149335F99959158F4FB3E05C32E2355ABCAE94C38ACE8A9B7DFEDFAEBC13217D5C1389D9252792321EBBA57C73B52730C35B36EF125255ED06F608B477C3E6D3B19FC65AE192653F927035C0692741B7CF0BBD39274F494756781CE463DF0B4F88AA0D6847467051B152F4F88677336B99A3AB224C66FDF1A55A2E8D749A841AF3DA4C7D1D3B0A19CD56CBA1D9D8E1C7908CC8F607D702353DFDB76EB172E732CF354981596706179037013BF7EF8E5B
	6730DF331DB80AE33CEAG838AE97837F767B19CF8682DDD54BFC46BA5D58DFA0646D439CBD6D50FEFA45CBF014A921A928AC3DC5718AA54F59168988F34C25375EDF3014E4E6CFE7F2531C0B7FA903757BB683E3C8A5A27E86B6FB907B9DAEE1DA807E8B9DC7F5F1DCE5DDCE33B6BD93319E3F461B97D98E91FA37F7098E6876AAE62A643EAA8E6861D1C1A7E89D416549BA0C7D816974E89FF483881E3B0DB58EC09EE4B3996E3DBFE5BE2EC4BFBADB35B6A3B96A3DBE5F56FDBD0C77F38CFE77BE68B46C3175F
	67132983EADD884BC5DF6BC6BDBCBE0F5A67F10F48EA1767328C7BD59D5AF0FA9F2E599CF8DFD8A7E26478C191A35F3DFC87D9F17110E1BE35681BB6D38A37DFD29F0E11CCA6CB857A142C48DE1828867F18CFE578B19A4FB99ECA2358EDE9DF433EC75A6A47D4556FE31BC629C4F5A9E30FCEB9B86C8FFAE57711CF244F24B2E9DCAD23EBD1D6C72B9B713D24786851D345534E86251F6859DE2C0B60E70F4E3026243744CC180CF7957D3DDEC61BA7647504C21F3B101C6F35CAE961768FFB63275F785C67B272
	DC72574E2810F5FF6A6D1F7F1CC693763AE7166D91BF2EC6FBAE86BB2F8232570BBD62018A2C97FB845F889B7C0A42DC916A626B291FB98BF53E632EE767756DDA3D5930FEA3BCD97D7D0675B98D223E225F283E38C148392492F316107C69188C29A0EEE2E764C91E4F7DE3E5FEC4DD2F240C0573447BAA3742D3516DFFBC9728B2767158AA5C73AEE97C632AB28966FB0CA70BC97192728C84BDDE8997AA2872FB09E5E57AFB09AA109F6FB01C70D49A44597A413D98BF277990E0B69B4D6B9D625EE2AE740981
	DA4E79ECEAE0F34E5AD93AC43B85D00E35055BBDE163E77EC6EC3727CF1C3F4BB52C02E24F00C0B569620C531E2B3F87A9FE78CFE9C6F19349594878AB0D11E73F12E348F7BE60B10DE26D5E2B5667A1F521105F652129B7DF0755557A184A30672B3740F358938C41B950ADCF0835CC2EC6D9B1B767B0DF0FFB5FBFC9261FB59A698505C8739A4629DBF47B9B60F85082665020EDB93D9EF91C08F978F3137EEEE5790E8A5356A873904F75CF524D036A6B49E4779EA3CEB16F39CDE04B94DB27E1AD9A66699758
	1CA6E1A37FD102F6C46FDF5041A74AC2633047DA2DED3A7B22836559DC7FE5CD627E643796346975D04EEE867EC06CAC77D507B84B9D87AC50AC7C6E16FC3D5F45821E8434810845387FF1D440735AC31AD817B77BF0AE7F1A004B56644E0253DC4644FFDECCEA7FD2E0F87FB3EF47BED3F68BDB8BF3CBF8CDCE3FBE19163233175FF995770A393882ED16360839F82840E8AED2DA441A146CBF9956657A6231E6E3FD95FADFDB11ECB14CD1E8EE524AFD39B55257361D2079AA6036D661EBF3BE4853791A832CDB
	720DC044937ED3F6AC0D45B6DE2EB816A1E29B91E727B6AB4E0393F82AC073017E81E825B646F70F4DFAF753B8B1435D0D623C16BB18352DA297FF751A7AED547A797A2F25DFCE1B68F738BC32DFF1DB783CD355B67D6C436ED306E96E5852466ED336EA38CA71D19D4E6CF1A375D9AC4F033A94ED7C98C764781DE7260E291CB1BADFFFEBDA3FBFC41497C474E3763D238B575FA066B00BA7626E7D673648BD8E4F073140F37999915392723339BC2B4E083B10A5B538DF655893320EG1D869AG1A8394833442
	AE6CAB7B38D1AC1FEADF5182F978DC4D66623DE9F732D7B707D5537AC54EE132FDEABF2677EF8AD0EF3C0B5FE897FAA757A2B6508EB9C03B50D1BE0EED041FF67ABDBE15889F4DBF017906BF68997178843613E2AEFF109E1C986630C6BF5375DB0D4FF4972D46E7BA59BA737D04579A6D4C476E927E066EEBD7C03FC5C08CCB6F97D81C065D2CE1ABB46C945D47D6C1F91D06C9E644F24EA6E43DFBD67803D201F129854AEE20ABC00B0016E97517E9FDA214D300F2BA9232CABBD88CE775EFDA74778A11340C73
	B42AEB61E3995539B94E4658D9B15398DB49747BAAC19817235D0109FB01FD345D13EF6F20EB958A49A16A88B6C915F85DAF55370841D97A2C641509B837BC63584C5BB42A7E860BE63BFB9AC5BEE16F9079442ABC0CCDF3219C833434C34C656379117E1188F5499DECAEB6F4CC1FABE6C75F2A09EE337757985B6CF7EA50D7C27E3120CB0E344BFF2DA9526A42D120D5153D28B2EB73B86D2FCA4191F91ADDBFCD7B333AE87D1F2D09E67BECBEAF746A6733C233055F95C45AC2F6E7C2565ECE1E1FF49A5B134D
	GF78226C460CC3617EBF1AF19504A3F2265C9A2E23ADC37DD532F51676601061D8196D31F4D037C7FDE221D9528FFFE9D18341B8496C331BAD0B64DA4F46EA947F824C8137EB160372A935BA9657AFDE18EE031C04B395CCC1E7B475187060312972630ABA03BC87CB96CBBE196FD77C2598C548994GDAF0CE642285AD380EEDF6E9C8D67869C9DB836540D62D2ECB05AC1363F97948338DF612A1FF3DFF02D5713A22463CC8FED37C6405D1DCC716F1DB78B9773E8AFD0E38719C5B7B76BAC48E796200D13DD9
	2BAF4854575B9C2CDE516A7FE950287F9C07703B6246C83FDB68885BC52CA3DACE5212CF3F95B9A23F7DF4D1BC4991796D6779BC11A32F053A16AE9117DF247BFA9C14D7E858E13A672D0272DA8D1BD7A06277FA8DABAA94FD8B3B85B6D0A87A9E5130C7A8D685E53B06FDDC0758A0142FE858C7C5E20C859AF619E2CB211C58AD72F64BA0669DDADE034939555A9C74386B2591BB35E0967F3EDCAE6E04133BC57CDBD10E73D05483EB885452A36E98A75E93E744383B97937B1CB3065F5F133BABCC03BDECCD66
	76047377755A99F113D90C39826AB776506FF4963D8D20AD7EADB4A8C3FB207583D36A51066138252B3FF5CABD4A7B30C9DF3FE2CAFD062EBF536DC70704EE1BFE94CB14FFB99175378527FB19CEEE7E1C68BDC8329BFAB37999F6B84A9916FA9C537F9795A2CF0C69953963DB97345C31D72C61083247181770EC3E0735E50A33F4CE1F6037F6D070337589BBB9C6773AABD0DE20E19FB589FBDA5AA77C6DAC3577F4A770F28A2C1FE2F8AF03F7AE8CCB4E1BE95F0FC648B392CBB7405898EFDC5798C65F940C3E
	E5301877BA554D6B13A17176386CCE1C179C6A4B2F607CBBC5AC36DF46BBB295B66B85CE7C2DCF258DADE795EDFFFBC1518C72B01EF554479F875A5B8FEBC0F974B692CD0ED505984F3D520447BB6625977E2CDF8A6DD78AFD0E778BD9466A50BE83C1F9D88E86E5B77BBC405B5FCA63069B5A860018FC1F964DA49F3F53AF2263FBFC91635FC363CB8C700A6D97EB7F35E967445908455EAAB34616D57DAC7626EA78C90AA76BF09613FFD0AEFE2B1382F51D9F09734509A654E7A914130056811173E02FC03567
	45F943F7926F2DE7BCD3F57B83F6C84C94DD7A4F74CB35DC5FEFA63E17A5624852076EA44B3E70C6EA34FB344E734C66837C195BF31062CC3F44E654EF8954AD87CA865A8814B3GE70381215F03A7CB19FEF865D7272A12EB145EA68A657002AF8CE36241F4FB25C22F5B7181B63E1DBF87872279626E8B7AFEAAEFB70FBFE322766B481CE9AE6323767B55203E5FCA5E6EB97ECCB168476C78EC6374BCCDEE545BBF39GFBAFD06D852E43856AA3CD36D9FC84654A606F5D50E79468A0AFDF01777329616F2685
	6401B61A3B696F81353A8D135B43F57CDECD549DAC40676131435C26160C6B6F4D0EEFB996755EAC096B33D6BC3FF810737871F9BD0F5C457BCD29D1F86C7D187F2E7063705E4DF8BCF1C13FC7963F1FEE38C7EE3CD0EE9A60FD6768F8AC0C6F4DE47AB98B29FE2E4304454018B6224DCBD79D5EB9786471E26DCE51668173459F1D9CB9D6DDDADCD18D9BB76B17F8A65C2F23686A7B7D5A9E6E77D0675574E376E845F0A2FE0B25FBCFAC3F1EBD6AEA090D71728D9D0345137A2668675456A663F3EAC713715DCA
	CF534C5F4BAFB4C57FDE6EEE52FFAFE7717051F64C5F1602BDA581EDG4A3E88E7100BA2967E3669G213150A08EB63AC7701C0C3F28D245782B1B236B3A26F9765FE13D5C0C5FEA067C603EDE64AF70D71AF13DD4FF00028CDB5F0CF394740C0C2A2CA947FF570C7B2AA28FD31865E326CA440E03DEAD97C50ED6FDD91C9786B4EC71E511370DEA186502683BCC43CA6C22EF2206FD49AE7ABE27E1431982BBB2A8B0A289FE939A16E2957C66EBD8FB0D683BE8D0642E1595D36ED5597923C9710F5BF9D866396B
	AD0D62AC30EED01CBF3E5BC83F5BCA106FGF5823DC3670B90EC4B7E2D7F5088E37E7D1D8EAC311875D20E08D9FB6F7DBA31BD7BC66AC1E29C77C6A5962B16C96118958BB10B595F3D1DB84EFA284B9992B6774516776159ABFDB27D4B9B001DC1FFC08E2A138C25DFE739AD5FFD0EE8B670D44B21E8BC40F25BE06ABA06BE11DDEA37B4227942A38E5C4BCAC10E96A085E8AE50A220F8CD367C56A3B3484696017AC4D07208EF400F37C77709275BE77709E75B69B7CD30656E49G3FC7DA53CE6FF50707C33266
	FBAF3567EAD8EB28539F72607D930B473BAAB1479E767BD416FB16D3FFD889FAA58D091C79D2B74E9F5B336F3FBC5B1E2DA762027EAE36E7238D943A183D526F95F43C0F288F9583DE6A92635D5A939E6F8E7BEC63E54CB05EG3C1770B60C5F1FB3E762179141EFD4470FFE4B0377731CB7133F047A6CBC4097EA727F2CB7AC3F3BE63671E61A2F1570FED3AABBFF33FDE941343B195908657BC71B689D0CAB729C7081455738A24F810FB60AF340BA28333B45B96029BE54A79E4A09C06B0132E55897002A00DA
	00860082C0730016GAD85FA82E88DD08A9099CE48AA9C96FE332923E4863F6197A8E147517CB83E93F55376BB666E62AE379377AE7CAD17C7D2BC17694FG3A2021EF04557F40915D6F1EF24C6EF7BFF5986FC5EB9C537722179C46FB51AF9D7A3D08594D3A8B0408BC2964B26E2FD37224A6F052A3875C2013FF64C0E931393458D28513B7BDAE0B3CEB6B45707B23D21877BE7B5572E6FD7FFD887BC60F7FED4361F13EBD9A9E6772097D4663544104AAB00C058F437B2E76904D4EAB1B426F353A77479AEFFC
	6E77628D9D035BC5DFF49BDC57B73B8D7E264F58867F33EF3A8DEE6AB336C113D3EF038C4BF2E2CC9A813FF3EA6DF2B42CDF43721C1BB95667F12B239A3E53390563ADB28EA870FDCE14EB1CE2CE0E9D50E17D9CBB6C447D8BD49E75EB5FA0CB1DF40F72A88A7FADA7ED637141F6264DC7B515519310F4F2DBB58C65E6FFE3F6D3D1BB00F12F128C15E432B3A23CB4609CAB21A0491FFEAA293B755F1E0D3BE8ABB491D3AB499C255236120CF1D4FB82FF50CFFF5C967A147CAFB1C5A985498E0DEFAB95BB7933CA
	EB5A4F6FCAB311F4E04A1631152463DAG6FA09BA38D16110E006AE67067AE15E4738F89D6101CFEEC0017ACF661D989E3ED1B62ED0F5DD17D0B4497FBC3E9A433156C420D3ECB8E5238A83B711B9E7F8303A632B9005FED709A8A848951C7C6C81DD4E4BBA99CF6608F5FC22A6C2E9391B7241237686FD04A47D40FD25EA205C6D534509ABBC977611FC210FCD62DC83E11F296FEEB28B629F503B4FC20B3901037004A0BDC4AB4AE858F6FB8343FED6D32B4E2EAA2DB943F64EE12DC30B134124D6AE8D08E0D7A
	9577C460435314D176B85AB16694E940EE0F26BEAADB568DFE2489FDE9629B73372D5B5684CBB391A46534A55EAF16E9AD5C8D23D2900612032DAA7EE907BF782ED6F7539F54771EEA5EB9E0A229DCC9DC66ED9381D807E1496BD1A66145C6E3A8B500BC95248DCA3E907EEC87A4AB0E10CCD4E983147D5D13662F5C73DF9F518142DDD5728E1BCA68DABEA27B3D65F543439E1F2CB100F985767BB4765826AC22CD7E44EDEF352D7E7D9058010D24F94DE67A3F057EDF017FDBA8E221082582A6F7BD1971DFE47D
	0450199A0FE673387CE5F090115467DFFD724E97EA572700F6CDA4037E9C8896A1B75C6A6C18F7C88EFEA16EAB7B36AECC3E8557A80026B201FC5FE26C5183367045C67FBAC4512D532C8A519D534D836182ED113074B6EDDAC47F5FF34DAAE32FFD346FBB1A240742126E6663E2284C51D00C8938C1622E3A55D07E54A872270BA94EE4C1130D0C2D7EBF94B3CFDD82D481E36AA1A24EFEA5FF515FF9ED3C39A86A3D19D9613FB1D5A2FFEF1651C1FFEB8FF80CA2F2441E33C7C974B3DB17D7D294D5D2AE55795C
	F8FE2B1FD465703D47168E649F92EDF8A55F43BAD06ECFF4FD7E8FD0CB878874A5B3605195GG64BCGGD0CB818294G94G88G88G0BF2EBAD74A5B3605195GG64BCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8B96GGGG
**end of data**/
}

/**
 * Insert the method's description here.
 * Creation date: (12/14/00 4:50:21 PM)
 * @return com.cannontech.cbc.CapBankDevice
 */
public StreamableCapObject getCapObject() {
	return capObject;
}


/**
 * Insert the method's description here.
 * Creation date: (1/8/2001 3:45:56 PM)
 * @return com.cannontech.cbc.CBCClientConnection
 */
public CBCClientConnection getConnectionWrapper() {
	return connectionWrapper;
}


/**
 * Return the JButtonDismiss property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonDismiss() {
	if (ivjJButtonDismiss == null) {
		try {
			ivjJButtonDismiss = new javax.swing.JButton();
			ivjJButtonDismiss.setName("JButtonDismiss");
			ivjJButtonDismiss.setMnemonic('c');
			ivjJButtonDismiss.setText("Cancel");
			ivjJButtonDismiss.setMaximumSize(new java.awt.Dimension(75, 25));
			ivjJButtonDismiss.setPreferredSize(new java.awt.Dimension(75, 25));
			ivjJButtonDismiss.setMinimumSize(new java.awt.Dimension(75, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonDismiss;
}

/**
 * Return the JButtonUpdate property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonUpdate() {
	if (ivjJButtonUpdate == null) {
		try {
			ivjJButtonUpdate = new javax.swing.JButton();
			ivjJButtonUpdate.setName("JButtonUpdate");
			ivjJButtonUpdate.setMnemonic('u');
			ivjJButtonUpdate.setText("Update");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonUpdate;
}


/**
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getJTextFieldOpCount() 
{
	if (jTextFieldOpCount == null) 
	{
		try 
		{
			jTextFieldOpCount = new javax.swing.JTextField();
			jTextFieldOpCount.setName("JTextFieldOpCount");
			jTextFieldOpCount.setMinimumSize( new Dimension(50, 20) );
			jTextFieldOpCount.setMaximumSize( new Dimension(50, 20) );
			jTextFieldOpCount.setPreferredSize( new Dimension(50, 20) );
			
			jTextFieldOpCount.setEnabled( false );			
			jTextFieldOpCount.setDocument( new LongRangeDocument(0, 999999) );			
		}
		catch (java.lang.Throwable ivjExc)
		{
			handleException(ivjExc);
		}
	}
	
	return jTextFieldOpCount;
}

/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxState() {
	if (ivjJComboBoxState == null) {
		try {
			ivjJComboBoxState = new javax.swing.JComboBox();
			ivjJComboBoxState.setName("JComboBoxState");
			// user code begin {1}

			for( int i = 0; i < CapBankTableModel.getStateNames().length; i++ )
				ivjJComboBoxState.addItem( CapBankTableModel.getStateNames()[i] );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxState;
}


/**
 * Return the JLabelCapBank property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCapBank() {
	if (ivjJLabelCapBank == null) {
		try {
			ivjJLabelCapBank = new javax.swing.JLabel();
			ivjJLabelCapBank.setName("JLabelCapBank");
			ivjJLabelCapBank.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelCapBank.setText("CapBank:");
			// user code begin {1}
			
			ivjJLabelCapBank.setText("Name:");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCapBank;
}

/**
 * Return the JLabelCapBankName property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JCheckBox getJCheckBoxOpCount() 
{
	if( jCheckBoxOpCount == null ) 
	{
		try 
		{
			jCheckBoxOpCount = new javax.swing.JCheckBox();
			jCheckBoxOpCount.setName("JLabelOpCount");
			jCheckBoxOpCount.setFont(new java.awt.Font("dialog", 0, 14));
			jCheckBoxOpCount.setText("Change Daily Operation Count");
			jCheckBoxOpCount.setToolTipText("Check this option to set the daily operations count");
		}
		catch (java.lang.Throwable ivjExc)
		{
			handleException(ivjExc);
		}
	}
	return jCheckBoxOpCount;
}


/**
 * Return the JLabelCapBankName property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelOpCount() 
{
	if( jLabelOpCount == null ) 
	{
		try 
		{
			jLabelOpCount = new javax.swing.JLabel();
			jLabelOpCount.setName("JLabelOpCount");
			jLabelOpCount.setFont(new java.awt.Font("dialog", 0, 14));
			jLabelOpCount.setText("Op Count:");
			
			jLabelOpCount.setEnabled( false );
			jLabelOpCount.setVisible( false );
		}
		catch (java.lang.Throwable ivjExc)
		{
			handleException(ivjExc);
		}
	}
	return jLabelOpCount;
}

/**
 * Return the JLabelCapBankName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCapBankName() {
	if (ivjJLabelCapBankName == null) {
		try {
			ivjJLabelCapBankName = new javax.swing.JLabel();
			ivjJLabelCapBankName.setName("JLabelCapBankName");
			ivjJLabelCapBankName.setFont(new java.awt.Font("dialog", 1, 14));
			ivjJLabelCapBankName.setText("JLabel3");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCapBankName;
}


/**
 * Return the JLabelState property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelState() {
	if (ivjJLabelState == null) {
		try {
			ivjJLabelState = new javax.swing.JLabel();
			ivjJLabelState.setName("JLabelState");
			ivjJLabelState.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelState.setText("State:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelState;
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
			ivjJPanel1.setLayout(new java.awt.FlowLayout());
			getJPanel1().add(getJButtonUpdate(), getJButtonUpdate().getName());
			getJPanel1().add(getJButtonDismiss(), getJButtonDismiss().getName());
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
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	CTILogger.error( exception.getMessage(), exception );;
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception 
{
	getJCheckBoxOpCount().addActionListener( this );
	
	
	getJButtonUpdate().addActionListener(this);
	getJButtonDismiss().addActionListener(this);
}


/**
 * Initialize the class.
 */
private void initialize() 
{
	try 
	{
		setName("CapBankEntryPanel");
		setLayout(new java.awt.GridBagLayout());
		setPreferredSize( new Dimension(280, 200) );

		java.awt.GridBagConstraints constraintsJLabelCapBank = new java.awt.GridBagConstraints();
		constraintsJLabelCapBank.gridx = 1; constraintsJLabelCapBank.gridy = 1;
		constraintsJLabelCapBank.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelCapBank.insets = new java.awt.Insets(11, 11, 5, 2);
		add(getJLabelCapBank(), constraintsJLabelCapBank);

		java.awt.GridBagConstraints constraintsJLabelCapBankName = new java.awt.GridBagConstraints();
		constraintsJLabelCapBankName.gridx = 2; constraintsJLabelCapBankName.gridy = 1;
		constraintsJLabelCapBankName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelCapBankName.insets = new java.awt.Insets(11, 3, 5, 5);
		add(getJLabelCapBankName(), constraintsJLabelCapBankName);


		java.awt.GridBagConstraints constraintsJLabelState = new java.awt.GridBagConstraints();
		constraintsJLabelState.gridx = 1; constraintsJLabelState.gridy = 2;
		constraintsJLabelState.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelState.insets = new java.awt.Insets(5, 11, 5, 2);
		add(getJLabelState(), constraintsJLabelState);

		java.awt.GridBagConstraints constraintsJComboBoxState = new java.awt.GridBagConstraints();
		constraintsJComboBoxState.gridx = 2; constraintsJComboBoxState.gridy = 2;
		constraintsJComboBoxState.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxState.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxState.weightx = 1.0;
		constraintsJComboBoxState.insets = new java.awt.Insets(11, 3, 5, 5);
		add(getJComboBoxState(), constraintsJComboBoxState);


		java.awt.GridBagConstraints conOpCntBox = new java.awt.GridBagConstraints();
		conOpCntBox.gridx = 1; conOpCntBox.gridy = 3;
		conOpCntBox.gridwidth = 2;
		conOpCntBox.fill = java.awt.GridBagConstraints.BOTH;
		conOpCntBox.anchor = java.awt.GridBagConstraints.WEST;
		conOpCntBox.insets = new java.awt.Insets(5, 11, 2, 2);
		add(getJCheckBoxOpCount(), conOpCntBox );


		java.awt.GridBagConstraints conOpCntLabel = new java.awt.GridBagConstraints();
		conOpCntLabel.gridx = 1; conOpCntLabel.gridy = 4;
		conOpCntLabel.anchor = java.awt.GridBagConstraints.WEST;
		conOpCntLabel.insets = new java.awt.Insets(5, 11, 10, 2);
		add(getJLabelOpCount(), conOpCntLabel );

		java.awt.GridBagConstraints conOpCntText = new java.awt.GridBagConstraints();
		conOpCntText.gridx = 2; conOpCntText.gridy = 4;
		conOpCntText.anchor = java.awt.GridBagConstraints.WEST;
		conOpCntText.insets = new java.awt.Insets(5, 3, 10, 5);
		add(getJTextFieldOpCount(), conOpCntText );



		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 1; constraintsJPanel1.gridy = 5;
		constraintsJPanel1.gridwidth = 2;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.anchor = java.awt.GridBagConstraints.SOUTHWEST;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(10, 0, 6, 0);
		add(getJPanel1(), constraintsJPanel1);
		
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * Comment
 */
public void jButtonDismiss_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	disposePanel();
	
	return;
}

private PointData createPointData( int pointID_, String msg_, double value_, int ptType_ )
{
	PointData pt = null;
	
	if( getCapObject() instanceof CapBankDevice )
	{
		pt = new PointData();

		pt.setId( pointID_ );
		pt.setValue( value_ );
		pt.setQuality( PointQualities.MANUAL_QUALITY );
		pt.setStr( msg_ );
		pt.setTime( new java.util.Date() );
		pt.setTimeStamp( new java.util.Date() );
		pt.setType( ptType_ );
	}
		
	return pt;
}

/**
 * Comment
 */
public void jButtonUpdate_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	try
	{
		Message msg = null;
		Message opCntMsg = null;

		if( getJComboBoxState().isEnabled() )
		{
			msg = createPointData(
						((CapBankDevice)getCapObject()).getStatusPointID().intValue(),
						"Capacitor Bank manual change from CBC Client",
						(double)getJComboBoxState().getSelectedIndex(),
						PointTypes.STATUS_POINT );
		}
		
		
		if( getJCheckBoxOpCount().isSelected() )
		{
			//if we have a valid NON-ZERO value for the new opcount, send it
			//  in a PointData message			
			if( getJTextFieldOpCount().getText() != null
				 && getJTextFieldOpCount().getText().length() > 0 )
			{
				opCntMsg = 
					createPointData(
							((CapBankDevice)getCapObject()).getOperationAnalogPointID().intValue(),
							"Capacitor Bank OP_COUNT change from CBC Client",
							new Double(getJTextFieldOpCount().getText()).doubleValue(),
							PointTypes.ANALOG_POINT );
			}

		}


		if( getConnectionWrapper() != null )
		{
			getConnectionWrapper().write( msg );
			
			if( opCntMsg!= null )
				getConnectionWrapper().write( opCntMsg );
		}
		else
		{
			MessageEvent msgEvent = new MessageEvent( this, "Unable to send message to CapControl Server, no connection found." );
			msgEvent.setMessageType( MessageEvent.INFORMATION_MESSAGE );
			getConnectionWrapper().fireMsgEventGUI(msgEvent);
		}
			
	}
	finally
	{	
		disposePanel();
	}
	
	return;
}

/**
 * Insert the method's description here.
 * Creation date: (12/14/00 4:50:21 PM)
 * @param newCapBankDevice com.cannontech.cbc.CapBankDevice
 */
public void setCapObject(StreamableCapObject newCapObj_ ) 
{
	capObject = newCapObj_;

	if( getCapObject() != null )
	{
		getJLabelCapBankName().setText( getCapObject().getCcName() );

		try
		{
			if( getCapObject() instanceof CapBankDevice )
			{
				CapBankDevice capBank = (CapBankDevice)getCapObject();
				//getJComboBoxState().setSelectedItem( capBank.getOperationalState() );

				getJTextFieldOpCount().setText( 
					String.valueOf(capBank.getCurrentDailyOperations()) );
				
				getJComboBoxState().setSelectedItem( 
					CapBankTableModel.getStateNames()[capBank.getControlStatus().intValue()] );				
			}

			if( getCapObject() instanceof SubBus )
			{
				SubBus subBus = (SubBus)getCapObject();

			}

		}
		catch( ArrayIndexOutOfBoundsException e)
		{
		}
	}
	
	
}


/**
 * Insert the method's description here.
 * Creation date: (1/8/2001 3:45:56 PM)
 * @param newConnectionWrapper com.cannontech.cbc.CBCClientConnection
 */
public void setConnectionWrapper(com.cannontech.yukon.cbc.CBCClientConnection newConnectionWrapper) {
	connectionWrapper = newConnectionWrapper;
}

}