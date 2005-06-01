package com.cannontech.dbeditor.wizard.device.lmprogram;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.device.lm.LMProgramDirect;
/**
 * Insert the type's description here.
 * Creation date: (3/11/2004 2:55:06 PM)
 * @author: 
 */
public class LMProgramDirectNotificationPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener {
	private javax.swing.JLabel ivjJLabelMsgFooter = null;
	private javax.swing.JLabel ivjJLabelMsgHeader = null;
	private javax.swing.JLabel ivjJLabelSubject = null;
	private javax.swing.JPanel ivjJPanelMessage = null;
	private javax.swing.JScrollPane ivjJScrollPaneMsgFooter = null;
	private javax.swing.JScrollPane ivjJScrollPaneMsgHeader = null;
	private javax.swing.JTextField ivjJTextFieldSubject = null;
	private javax.swing.JTextPane ivjJTextPaneMsgFooter = null;
	private javax.swing.JTextPane ivjJTextPaneMsgHeader = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldMinNotifyTime = null;
	private javax.swing.JLabel ivjJLabelMinNotifyTime = null;
	private javax.swing.JLabel ivjJLabelMinutesMNT = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();

class IvjEventHandler implements javax.swing.event.CaretListener {
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == LMProgramDirectNotificationPanel.this.getJTextFieldSubject()) 
				connEtoC1(e);
			if (e.getSource() == LMProgramDirectNotificationPanel.this.getJTextPaneMsgHeader()) 
				connEtoC2(e);
			if (e.getSource() == LMProgramDirectNotificationPanel.this.getJTextPaneMsgFooter()) 
				connEtoC3(e);
		};
	};
/**
 * LMProgramDirectNotificationPanel constructor comment.
 */
public LMProgramDirectNotificationPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (JTextFieldSubject.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMProgramDirectNotificationPanel.fireInputUpdate()V)
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
 * connEtoC2:  (JTextPaneMsgHeader.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMProgramDirectNotificationPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC3:  (JTextPaneMsgFooter.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMProgramDirectNotificationPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(javax.swing.event.CaretEvent arg1) {
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
	D0CB838494G88G88G1D0632B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DF8D4453558071657C795DFC3CB1FE9BF8AE8D183860DB5A82D81230200869BA5EA44288891A2CDDF71137AE83D79B39B487F8FA6A1A6CB424FAE46A5FFA80DD1898901680281D2C8DB5AE6133DC9EE58BFF6EFC8C2313EB3B3774C5E5D6CEE821F76AB5FF738F74E4C19B9674C19B3674C5C0DD0D8B1B3ADA939D090125284655F07C90260689004B52F5CF6B362F4F3B569026A5FAE00BC61773A
	D4F89E023A08B7B469394209BA8D140DD0CECD54243F836F8542693A10E37062E71C49D057DDFE5F2B0147095D0E632453720DF919704C8668G1CCC461FCB463FAD2FD8414F00F23672CEA0A9D99002EEA7ED36390AD578545BD93B81F8DE81B8F487E933B82FCC290F859CE93399C0F387EB2B05E786656E7A46C3CA3B73B74F95B6BFD61E1CC546D2750FB0C9211D03FD8837D1C52488416DE7615954F1217F13DE3B5550EB32596CB6D96C9DB018FBC433A45B1D06E1693249E9B61845CBD22FE830D89DCEFB
	3F53E4EDECB41E68E02F2D12D36C15CF5AE529CF6AB549125D56E1320996D07B1DC41E9A17060E85724078B3A9EE2E0B68308B5E9DGD36EC419D6E5B5A9BC87DD18AB444D4E73AB535ABB19AC1A9037CCE1E78E240407107EB750B9A2F44721BC890071D2170F3CEC8DC1DEBA6D24ED43BCCD3A9140BC8F71BB9D84FF854AC1GA17310470F77A10FDD7F38CF5852531C1C63074748F90C3704F9EE9EB393DB53CA289D8DE491DB504D07F1813A81A6834C81088458C96D37AAF3BFBC1BFB64963B55EA37B5BB9C
	C7ACE667893BD93440FBE3A354A83853E22F5DE99684E62387DD55BE78C1081D6FE91F8418EC6784342910537385D97EB0B94D07ECE1CFE9139315BED26FF24B56F49751DB19CC74369B5E899D610761F594BFC905E7F3B51F5A82A94F003A2DF7211E3B321197E36AACA13736A5B95D87AFB3169CCA6E8EE5BC0C043AF9E1BABE12C3F4BC877091GB1G09G398BB469B58BD047564C7A71F45C8EEB42E9B3D94E8C75B8CCCE794810E4B10BCEC55709F901F46D8B985D6F2F1A0E69E5EE3DFA0EE26FB962D3AF
	31F377A59B97B0FD1897387542467EB05F5F5813970C350DECBE07CB69DA1931004DE18A47BFC971F3D5F8B637FF26F8DA063A323BB5692C7DCDF4EDCD3A5B5BC6EE217899F7FB5B48AE875AC8A854955E23C9E7739B17CBFC65881403GC2GA281568288E150AE8C67B8BC73408457D107EAFE996D452C9D7094C7E427295D6612CD36DE712C49A919FAAC22CBD05ADC4EA23C75F850FDC557F7AF0CF1CC3409CE45B1023F944118DC2C4D142CBC5246A9525ACE49AA42GD607007DCE4BAA00274564127F68B0
	1BE4515D02554F48A2B6F149F496FA048E48DC0791FD99294FED00770EB054D59675FDF3219C4175D2101D8F4F4F4138C5CBE3E32B54CB58B0B9C751C6AB691A0D06765D77EA52936E453539298BED30B5EB1ED07DD433CF9B24EB72E74C1E267D4C5B868D346FF04047GEC79996E2FE12EAE95BD995B3BCF4247552C9B825D8A8D9BE3254798A9FE5668550E1126BC193E7FD1C064AB3ECF135EG50F99F6A7CE4361659629113CBC46B7B428906A31A5DB3405AFE1EC376967112E813DBC507E8B3430BCB197B
	53B999EE3B6C33F31B6922F86227DA40F0DC9C7F371CBC0607BE54D60E75FD345EA44BCE29E7C8964F0832AC597A5D74B64A0B4B43853A94D93FACA432DAC04649GC1DC56B7F28AD476E5345BAF8CB9BA494AD16C74A433C9E89FF19F4E7F63F692EF2D656598DAF657F75B55F377F581994FED9F235F4898E8E7EC0C698E0CD1D2945BE1781FD0FCEEB84403G5D61A877BEC717D77B2F13B7B646E1BA061A1F2FF38CE6939BF27D5B742FF3FD5B749B393EED7AF73901EDB6AD4F174D323A4CBCA223FD5826DA
	83F9F9449F765AEDB611AEC4CEA230F8F9D3A711C332C972B9495CAF4AE7244BA26CC9614416FB294F1D826F416168A7072B50CF6E3EFC3B907177B21F71540CDBBFCA5E9C2E44338B5DFE1271141FCF6CC13408D6D0D40BFD48A6B3FCB1DDBB8A5E15C2F16259AF892D6E33CE50E11BA4DA4C90328C02AC553D306EE5837D1FEDA53AC153A5530841B58C8AB35059B034105A2364D52FFD7F7DCF3463C57855FD00EF37B90630CB7DD9FD0DE2BF20FB7DBF5D3C1076A7DC7D47C5932873DF46CCAD72F24B27E3F9
	E9335B65FFA1AFF538AFBCD0C466D387735C84503990FDE5D0BE29C7DF29EC88ADF60B0519A25405AFA4F6E6128BD536E3D948F250ECDE7F5DF16A977A2DE7FCDCD710CB560CF8F1C804D007B86B4F45D10CD78A723D6A4E1AACC36EB8E8DA41F652274A4FB37CFF947A36617FACA43E7D826F7F3B05EF8BA41EE1FD32B597EAA2FDBA1C129536C532F1A93934182F4E25057BB569A46FBE8C4FCEGC65B7309961E63E62A8F107E229D18F7DB00FEB2409C254F387B19FED83BD997311D914A66877C35BB6B44F6
	13219C6C235DA25A6EB24F0F8FF05C4D94778CB8606BD2B16EBF112B4E774D2E861F3E040E3F0875DF3848BB9FCA5CC163C24037AEC23B4BAFD26F514DAE173DD7223AD166EEF1115A4FB05CA3C55B6089D66E025C48AC0E607C2FA8A23C12F8D5C1ABF3F867FB9F8BE3636F23740481C32016E13A42BA6DAD90B29F8793EA61EE782CA48E8B2807AF5B54E7840BF274495B7D6821CB11FFC4250728DF3526B11EFE58C872BEDFE79F3D14C9C37F10E49812A592981BE493DD4E4CDC58D94293547F921E42976119
	C1EF893173C4A8EBFE2EC95782307C65F7F4CFA7639E821C036069DAFC7314FAAD4E85FC9440E6G66374C9709CDDC250BD2F49E294FE7F3646509D00F338A8EAB632E18BFCBE87AEA1B4FF30CA9B905491ADFB07D557C422D47205575E9F38118FC4BAF22FCD720CD58AFD03E0AAA35FC8900AFFC90621A87D1AFDFB9114E8838498FA2DDE525B75DA2280BFB907755F3F56A35909127733B96B6BF286C2B915EEBA13A184C5FEE409F0A4035F0ECC71EEAAD1C866BB3597A410B21FD3F3EA30579AB257C5B9DE9
	682F84746FF92504F7A374B9GB089E086C0B89FE352BB631EB79C9D89F05E904DF37798C5260488F7BEA4AC869DAF764C0FD8EE3D1A5A26EEB14B2DB3B8FEAD45372A70ECEEA40AA765EE280BDC0CF1E84AC56F33835D924F71DA3D4A0CAEEF8C1D412BBC60C5476CF80F2E8C57294A6C9B9B5DB18E5B8F4ACB557B4124A5AC2718834F70A5F87ED7FBDA237093358473627E1D24FF4DC31A745DGCDGDDGD7GE6GACFA88676D09F7FC79B04FF97367405C399653452AEB54618B636AB076A1DFBA9C7C47CE
	7FBAA4CECD59D35FADD76BF073C34C9FE9A3A1070DC49D1E2DC29D9AA351A62EF7DE1BBC5F9BC367AD4F64C8CFBA46673A8ADFFC86C35BD04E671FAA114F2511B857BFADA3B861E128FF9867353AB8CB3566BB6C12CD666B7D19E2920F581DD23FE4C35C9A0A6B35BBE96240F0A5257EF39AED296F1CE6EF296F1C263AB4F01E5ED06AAF67E1B975C63AAFE9C13EA6G065BD0CEF09D244CF19734B4DF0172CC0E1B3A0B60A2219C7BB04E6D73A5A49ECC04F266A31A749AG63A3D8370156915DDBGB795A00457
	59CB7C6550249FDFF884B21651C71B9A950E0DB1456F98636547440FE778402BF35EF7E93BE5BBF268FC38DC220BAEF0928DA8F56B65A241C73EF852E495854CBD9AAFE6A8ED0E4A7616DA3EA6FF23AF763DA6E910212C35D2B29FA43ED9718846980EA66217D39F55241781B4BC0A7A7E60B42EA56323E3F5578DB8598B2F1CEF6E74EF3BA3BBFD5B6EBFF712B563328FB9FBC5EF7B3CFEE7B12FF3FB40F6D934923530B6B7D2FA5964049C5A1BFEBA25E7F57E68FF3C535F9AE0BA57B855BA2F63BA6FBF7A2EEF
	1D1F69F5C226C5BDA15EDB7D319C6372A90F226E87AB096EA3209C831048F57F17AA543DE669D85D17812E468B4F78DC6441E7BD9F3BDC0BE337AE45312D3B68FD941403GC2166258FF2B4431A3FD0C9D833804253E66BD240C6C77A33C9CCA4B231C376482B2C78E2758A7BAA193E5C79D4221E574EE2C007AD1F84FDC0631EAD285710B965328FD889CE894EDF74943CFDF815C9C0070E50C9FB6A78B0609FD77C1FA86D3F146A1768AA10F915AEFE91136984A55GC6G8B405447F04C593B88AD90CA56A1AB
	65107177G75E31B211D309C5B4ECAA5723055F5C2F439CC7D6EB55AE0A7E3FD56F246A159E8C8F1C23251B4F1141C3833B6DF58092F7B4CE86A81FA4986E667BAF13213558D588B54F5EED2A567DE17250EE72B17335824FBB9463BADB6F53D2C544F60754FDBFD5507AC6776D70AB6908138E80078656EB95F8A6F39ABFC45047154176CDE41E23FAC0EFF03620F2B70ACA63CC47124EC013A189538EF1CB80DFBCE6BCA44ED51617E5245F1A57BF1FF393292F97FF3F920FB30E9AB475AB463F18DDD0BF3D6FA
	5FC545D3FC58CA6F3BA8C7A55EC5C5C21D6EF16449D9053CCF6538D2BD5F9BB96EA867BD72F1643DCA9B087718477D717EA2DD4B890FFB733E116237BE6E4D3BEB97721E7984640FCFA0CFC31548FBB4479D663C47F35C854EFBE69444F3D164BB07DB2A84EE7FAF14EB61E993073544AD9ACC433221952C57C6AE15F47587EB2A35759F5468D96EB5F3D81DFB9D5AE6701BFB1923147CDBF98625D5A9EB3E365BA326A783B6B69EF3CA66A326FEA375A04C8F86D7E8143EC323508FBED6C1F491CFE4D825C9AFDE
	05B96148DEBC978839ED16E01CF444673783F11D7A641AD50C1F4ED56E1CD01B581A46461C2B45B127C17D52D5847734D63D7E084ED6A533D81555EBF6296B495E743E202EEF7428A77C7EA1C5DDEF7128A7BA3A2050B359166FC15936AE8F95AC7756787DAEA277C9A6D3037254C5D78ADB3F5B1046667C01BD6EB9EFAB19481C33797A6787FB8555D94985CB3FE13017C42D06BE62ADE183F53BCE5C435E4D56A832D81E4473385F747909F92867E4F417ED98374FF992E379C20E0BFD122F5BD2347D42DFA2EE
	381060F4D0EE62389D548FF5C3D966387F5B0FEBE48647FD3B8157C87CAFF1FDEFEAA27CEE05F2F134A67D90C0974088C0D0B436799DED939A4D4E8C497986B92B0802F2A2EFF32529DB69072E07E8CF3FD02C7497A8DEC5C09EFC553131FEC047423B266DDEED48BCE4CDE09C6C4FD70CCC79E07BBB757B39CFC1AC8A40766D9CBD6E5B4781F79E608AC0B040A2GC6F7046ABF964A71CF217EAF16631C94BE0DFADBDBC93F3D6265CD34CC4EDCD979D265F877F404F6A2327A5767CD630E616B7EF8224074F18B
	5D033A00D773532847DA2E47E9001B8390893096A0F5B56A51C675389B4AB52BD10F0DBC2FBB3F9A7534FE97515BA4DE7EB5AD87F3BD0E6E1A48FDA7213FF6BD7E77044638D6DDB2FD7C186E07B3014F10552847F3DC0FB100CBG480C01F892409803FA8C22FA34C0F9A4867713A77E225E67F27F27B81978DBDFBEF7CE8C53CBE80C7799633C3D245F3500971E417306382A7760F956B4B876949C109DCE3BC3F44A238CCBEFD770DE3862EA3F97E2B145A7DA32DFEA1FE1B1C592479F2378EA951E459AD195F8
	AFFD886A021F413313B57B089E05E7E12D83B4G9881ACGD31EC559743B497DDF40B332CE3B43884926C5F53441641B97B0167285EC1E9627635E98F67F9DC2788D09C97E6E83021FD56EDD14E7441315944F643BC86DA4816AB44F01FDG3482F481B81EC379AE6E2EE6721169E916E5D36FG3D95C16148C5059BCD129965BB1AECC2B779B95F6BE7068FBC237B4A2656C90852AEC2F9C67B25AB71200BD75AEDD51E3931E36994BFC477545DD0DF930BF9530D74CE2E476E24C9BA4B6BEEAD44B5F59E5A49G
	21G11316C9C0E513D2F9B1B8F366A10AE965AEE8650BD8FFB75730CEE4A7324CD7EFB2D8166BCC501F4655989B483GC58A6E8A3C533301375EFA0B4994CA7775A84047BE0F3CBDB5C8F3C19AF5A8B7648246C5B1A6C2BF66861D57EF5AC1E2B55509813F57E675BF7CE8ECBD3BEBE6752B69778FCA2E0824CA5FD5F60FBAC6266410759FFB6700C4E71AB518A3DA343E6A4BF87D42C1DF75B5EBB09F58C65C124247EB3A8A41EFECD8774141EAEDDD0DDE09738E6AB1DEEEDD0371F22F5EBBDE568B6AF8F73A5E
	BBDE2E7728FFD46F9DAFE7F8445B3F55FB474BB7A974CC1646EA37ACC279C4E4E1FB4594FE7EFD85E41986908A9085103886EDE7F7FDBE5A07476C6358FFFD5FBDF60EBE40583507E06C06FA5463B9BD464B39AF209EEF9A2347F39EFADABCC60FC99E753F9923C7D7123AFE4FFEEFBDA2BD1365E103DB16E595931105697194BF3BB98E321C873882908C3068855463671FF86BD1B1F7E56C78836E318FDE059E3F64FA0C65FA3CF79CBD2E9FC70FA57B836B7173F174282DF54B7205F6A23230BDE9A525E3FB4F
	DBFBC87266B3EE001D67B3686B8B6A28BACD7D1C666E3D049FFECE4A428925EE61DE42B37916C9B2D9244B744A7F8CB4347533BD24D5CB46E66FA7D46FCFD7FC935F46F96EDF3F7AC647E0722F5F677F5CFB53BE5F675E1B77793E33F9F3DF606F5012F57E3FC34B50293FC3E3737EEC9D191B42B8704FG1DG83GD762B08E79012ED220F1080F986428391F1C3B13AF15E57EFD325E3F2C5769477F3E79BBFA723DC70FDD166DD652BF7F5EC7CF66CB36BBA8D2B93F2767C4CE29FFC0E6CD957CADF4DDDA44BE
	0AE6394A9F6899418C10ABB48E73171959183FAC62386F537CBF73C5584FDEC45C9E5A2E864A07B96EBB1C5648F1B514B6984AA19CB7FB9B41C5C0B90A63B45B11B676C55C932D8D6EF5EBA9547ADF37758D75555A7A1AC3FA96C32FAC44733A6A17207F17707C63675BC8FCBC21EFCEBAA559A29E21D19773EBC5C5685702DF625F1B6C235FE7C3B981A075E57C2E422243983E8CF01DAF63787D15FB5463CBBDD64350A52B4154A39B484FFD9A9BCF8ED9ACC6A5E4D37CE969FE5C5F273E0C639E5FCF7A0F04F2
	AC40E6008A2AA3C120778274DF62273EF0CC2F379DA4A5963F3F180971FB591D3305565509C9D5026F78BDF3AD0BDB4F2FF567D931E78F2430BE27FF681E2BB3CD01662A7EA0192B9ABD13EF70E3BCDB8ADA0B67CD333508DB41F1090308CBFD857366C67D5566AC2C2F7965285326D7F00F0AAD775E2348CF575CFBCCF605779EF50BC77D618A6FBD6AFA0F1849D9613DC795A874CC16739E3248405714D782677BCC16E1FEDE9A4CE539F54CF92927AC2B355E32E4F96C2799E34ECBBDE579E84CF92927AC5F72
	10A59A780A9FC7966623DE2A736FA7DF2D9B5FCF7EAF7D8D22887E2DF354215CD53ED1C73FA1686BF3095C9FEF2D4B6538F6D7075DA5115D1759EEC7B649DFE53B5D52A9B9C8F42444906F7C0158B74B116E4B8E14A32589F837267C568DE4A7D0244CD36EBACD7A97705C47574BBC9BB98F6A83DF44F22607284FAC03F63AF5982FB5E513B6B2B02444580DEEFEC2827213BE8EBFDD6B5CE790EC2CE3DE7C7C1572E306F68E4E4F7D5BBC79796BC7EEFEAA360F17C38662E7A60C015F42B1FB7AE561F86BBCE41D
	2F6FDBDE9C172E5C072E720452A2C237F45D444E5BE257616F2AD6537DA0F11D77D9CF9C45EFDD67FD56A36D43FDC233DE13EED94FFFF3F155FE0D512D3BC63A0A7D57F63635086EC3D16B3D65FD1862E357FB4BFBF2BF4A9B8FF546F8BC5B7A3C0958CBA814D7GA482E8DE05D883208160B840F9G8740940019GE1GCB81E28192G048D30CFEC4078EC63C1126F7ABB63D3AE3AE81066B4316F0D14EFE6BED66BC137014DFF13726C58606F3CE72E475919D9E9B722BC277A25FBFDD0CDB7D3E99726BC177A
	253BA7601D2DAFE0B1160B46003150AF491F98EE0462B6836E9C6F7F4AD51F85323EFE68237F9FFB74BFB6A6419C6D42A76E7759449C94DFD412CDE2ED0FD8228D9C132E528CF9133D3F32DADBD32D2D2D53A36DA3C75D7DD49EF7777303ED936927017753CCC2EE65BB23887AAD06E433C8B631057D22104BDCCB63425C0D602BB7E2AC3B111EBBB7C139E3A37F0E0B38FBDC272AF77B3EEFBE1F1B72AF98E349FE37CC4BD56F1D7B3F79312D5F68984C277C3D49FFEC5257B4FEECE2EF721D438DB50D4D61AEB7
	794E61A6B529F3B865FB2FA632177603CF6B626D3E43F1FF62381BE81F84F7CEB24B839CBF1D72C57047C5B2A04F97A9DF4394572560E62AF0FFD2F037B611788AC49E30736FB16FE8227103E4313072FC5AC632C1384575F18F65D1F218D4FC07F39C611B2D259BE8FEF09E5611052F2FB7BFA538C9D01E46F10FF6E07E984CF18FEA11B6026376C8C89B4DF1B7C8C89B47F1199710B6B581F127B3113618636E49C45A5D8918671976E68A7CEC072A1B614BC6325CF8D437621F0E535FEE48A46DA4FEC940BB01
	EE6ADF0EC35F1D8998FBA56FCB4336827AABB389DB153EFE7431E77CF68F5561E4201F467B986D700C5F3ECDD8D64E8F57F46079E184340F86588CD0789A6C472F611DDDBC55EDA7143BDF43FC7D0EACF79FBF12787DA9544F810884D88B103A897BB8C8F53C9B4AB51B300F15596EBE9E4B44BE3A21DE868882888308666D6F4EF34B7D42DE72B7C3FC7878E6D80C96F071350A0BD7F4763DC2B76D26CFA73ABF303CAB759466DD1BB7E15ED5F7AA70393B79D460F3776F1F724E3BAEFA54AFBD65AF6FE2723C39
	43ADCF09F4F572BCA421BC553FC2F9FEADF94B334B031F2AC1EFF9C6BD6A7FB868AD4FF39E755F9E8CAC4F599237BCED19D7A7CFE2A64AE3667214E7851627A34BDB1ED7BD6A3F1565AD4F820F7A302C40F9F14629EB0B4F17CA57C677FA665552313F5B33246DAE68A064B5A14D659472471E61B45F784C6A103F3796A68B496D425B835411358BE94344530DB8C06B303DA47D4DF5D1A0BF7B74AA89646FGE578A91161977252FB9E25GF5C98DG6700579C4EEBBB5DEC62AE55B7263CBCBBA5D9C885C65966
	5BAE24129D9478F5B23ED3E073A5DC8B78516F9100E45EB728B0698BD9B824FCF42AB58D08A633565E276D9DF24AA649C2CEFF35A6A83920F3512935420B36C7545A686F9CC533AC3C2D33CAB603DE96CA591729DA592EED261FD5EB691FB639E432B45261682F7A944BA16F9A90A94B435C9F7E4941F5AB561CF52588EF378BA544431F911D7482C1B4138F63153FC451A6E4B848AFA948B739A02F0BBE52DC7228C5B48A05FD2749AFC8DD32E8EE46A3F317AC949949C8874871670163A65700CC42170F0CC22A
	0D7C4D8CA11FD5DBCC367E836C1E62A32A34246643F4C26876817C96D0FEC9AF865ECB4183B9FBF6FD36B6BCC5C8EE9336D96CA6F31B2997B232F6A1C39EF00A2E813B45BC627814F52499460F6ED3203B1DDCFCA22C019C48F026AF0C7CE8EAD6FCD6DBB264DACE61GEDC9B6B483EFE1EE99B0B9E1A85159AE133F81E2F76E66F557154BFBD79EFB67FC32102488C92620E044A1B305F1F3A2A29B5A00EB795957164DFA2EA9B181C85A04B47A3BB398B8575D6A641035C7F45E90B27D5D6DC1F17FC578F29075
	5049FD878C214FE415AC237052CA1D9662B3A0F034397A6CCEAB8C2A7512982BB863066FAD2B19FE7B1BFF260C3BC9E52102CD91109E68976D56834DFDFD1030708EE03E207BF7F977240D412BCD7E484DC51FBD75D68F58D72B10E22D29217F57527F6B487F3594D3CBB135F5B0E909C240FF5E75A3A3C4E66D883777FEE5322FA3CF0A2577FF4D5D34B98D86A836E82CF990F4892D5CC91A1317F22EDBD25AC5AB2B687FFB2F0D536A910F15D9222C4E8CD9C8D73EE217850D720DC2A3A059E5B4F9E357290DBE
	C5CA72A3D2EAC011389931881CB5A3DDFCCD32FEB8726F277F437F06BC357D9B72E49CD16EF374DF293F1B5BF2FD0E5F6F43D33730732562AD5E5F4D3DCE4FECBA816F5802E7EAC77564438AFF775FE72CA60BC5B6D9AEB45B4C64AE7C4828AC2A6EE77524FF9736D1AAD9DD52FE4277200A667F81D0CB87888F24748D239BGG18D0GGD0CB818294G94G88G88G1D0632B08F24748D239BGG18D0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81
	G81GBAGGG5D9BGGGG
**end of data**/
}
/**
 * Return the JCSpinFieldMinNotifyTime property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldMinNotifyTime() {
	if (ivjJCSpinFieldMinNotifyTime == null) {
		try {
			ivjJCSpinFieldMinNotifyTime = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldMinNotifyTime.setName("JCSpinFieldMinNotifyTime");
			ivjJCSpinFieldMinNotifyTime.setToolTipText("Minutes ahead of curtailment a customer must be notified");
			// user code begin {1}
			ivjJCSpinFieldMinNotifyTime.setDataProperties(
								new com.klg.jclass.field.DataProperties(
									new com.klg.jclass.field.validate.JCIntegerValidator(
									null, new Integer(1), new Integer(99999), null, true, 
									null, new Integer(1), "#,##0.###;-#,##0.###", false, 
									false, false, null, new Integer(120)), 
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
	return ivjJCSpinFieldMinNotifyTime;
}
/**
 * Return the JLabelMinNotifyTime property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMinNotifyTime() {
	if (ivjJLabelMinNotifyTime == null) {
		try {
			ivjJLabelMinNotifyTime = new javax.swing.JLabel();
			ivjJLabelMinNotifyTime.setName("JLabelMinNotifyTime");
			ivjJLabelMinNotifyTime.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelMinNotifyTime.setText("Notify to Action Interval:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMinNotifyTime;
}
/**
 * Return the JLabelMinutesMNT property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMinutesMNT() {
	if (ivjJLabelMinutesMNT == null) {
		try {
			ivjJLabelMinutesMNT = new javax.swing.JLabel();
			ivjJLabelMinutesMNT.setName("JLabelMinutesMNT");
			ivjJLabelMinutesMNT.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelMinutesMNT.setText("(min.)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMinutesMNT;
}
/**
 * Return the JLabelMsgFooter property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMsgFooter() {
	if (ivjJLabelMsgFooter == null) {
		try {
			ivjJLabelMsgFooter = new javax.swing.JLabel();
			ivjJLabelMsgFooter.setName("JLabelMsgFooter");
			ivjJLabelMsgFooter.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelMsgFooter.setText("Footer:");
			ivjJLabelMsgFooter.setBounds(9, 116, 88, 16);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMsgFooter;
}
/**
 * Return the JLabelMsgHeader property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMsgHeader() {
	if (ivjJLabelMsgHeader == null) {
		try {
			ivjJLabelMsgHeader = new javax.swing.JLabel();
			ivjJLabelMsgHeader.setName("JLabelMsgHeader");
			ivjJLabelMsgHeader.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelMsgHeader.setText("Header:");
			ivjJLabelMsgHeader.setBounds(9, 60, 109, 16);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMsgHeader;
}
/**
 * Return the JLabelSubject property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSubject() {
	if (ivjJLabelSubject == null) {
		try {
			ivjJLabelSubject = new javax.swing.JLabel();
			ivjJLabelSubject.setName("JLabelSubject");
			ivjJLabelSubject.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelSubject.setText("Subject:");
			ivjJLabelSubject.setBounds(9, 25, 56, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSubject;
}
/**
 * Return the JPanelMessage property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelMessage() {
	if (ivjJPanelMessage == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Message");
			ivjJPanelMessage = new javax.swing.JPanel();
			ivjJPanelMessage.setName("JPanelMessage");
			ivjJPanelMessage.setPreferredSize(new java.awt.Dimension(343, 308));
			ivjJPanelMessage.setBorder(ivjLocalBorder);
			ivjJPanelMessage.setLayout(null);
			ivjJPanelMessage.setMinimumSize(new java.awt.Dimension(335, 300));
			getJPanelMessage().add(getJLabelSubject(), getJLabelSubject().getName());
			getJPanelMessage().add(getJTextFieldSubject(), getJTextFieldSubject().getName());
			getJPanelMessage().add(getJScrollPaneMsgHeader(), getJScrollPaneMsgHeader().getName());
			getJPanelMessage().add(getJScrollPaneMsgFooter(), getJScrollPaneMsgFooter().getName());
			getJPanelMessage().add(getJLabelMsgHeader(), getJLabelMsgHeader().getName());
			getJPanelMessage().add(getJLabelMsgFooter(), getJLabelMsgFooter().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelMessage;
}
/**
 * Return the JScrollPaneMsgFooter property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneMsgFooter() {
	if (ivjJScrollPaneMsgFooter == null) {
		try {
			ivjJScrollPaneMsgFooter = new javax.swing.JScrollPane();
			ivjJScrollPaneMsgFooter.setName("JScrollPaneMsgFooter");
			ivjJScrollPaneMsgFooter.setBounds(9, 134, 322, 35);
			getJScrollPaneMsgFooter().setViewportView(getJTextPaneMsgFooter());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneMsgFooter;
}
/**
 * Return the JScrollPaneMsgHeader property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneMsgHeader() {
	if (ivjJScrollPaneMsgHeader == null) {
		try {
			ivjJScrollPaneMsgHeader = new javax.swing.JScrollPane();
			ivjJScrollPaneMsgHeader.setName("JScrollPaneMsgHeader");
			ivjJScrollPaneMsgHeader.setBounds(9, 77, 322, 35);
			getJScrollPaneMsgHeader().setViewportView(getJTextPaneMsgHeader());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneMsgHeader;
}
/**
 * Return the JTextFieldSubject property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldSubject() {
	if (ivjJTextFieldSubject == null) {
		try {
			ivjJTextFieldSubject = new javax.swing.JTextField();
			ivjJTextFieldSubject.setName("JTextFieldSubject");
			ivjJTextFieldSubject.setPreferredSize(new java.awt.Dimension(265, 20));
			ivjJTextFieldSubject.setBounds(71, 25, 260, 20);
			ivjJTextFieldSubject.setMinimumSize(new java.awt.Dimension(265, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldSubject;
}
/**
 * Return the JTextPaneMsgFooter property value.
 * @return javax.swing.JTextPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextPane getJTextPaneMsgFooter() {
	if (ivjJTextPaneMsgFooter == null) {
		try {
			ivjJTextPaneMsgFooter = new javax.swing.JTextPane();
			ivjJTextPaneMsgFooter.setName("JTextPaneMsgFooter");
			ivjJTextPaneMsgFooter.setPreferredSize(new java.awt.Dimension(185, 43));
			ivjJTextPaneMsgFooter.setBounds(0, 0, 185, 43);
			ivjJTextPaneMsgFooter.setMinimumSize(new java.awt.Dimension(185, 43));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextPaneMsgFooter;
}
/**
 * Return the JTextPaneMsgHeader property value.
 * @return javax.swing.JTextPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextPane getJTextPaneMsgHeader() {
	if (ivjJTextPaneMsgHeader == null) {
		try {
			ivjJTextPaneMsgHeader = new javax.swing.JTextPane();
			ivjJTextPaneMsgHeader.setName("JTextPaneMsgHeader");
			ivjJTextPaneMsgHeader.setPreferredSize(new java.awt.Dimension(185, 43));
			ivjJTextPaneMsgHeader.setBounds(0, 0, 185, 43);
			ivjJTextPaneMsgHeader.setMinimumSize(new java.awt.Dimension(185, 43));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextPaneMsgHeader;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) 
{
	LMProgramDirect program = (LMProgramDirect)o;

	if( getJTextFieldSubject().getText() == null || getJTextFieldSubject().getText().length() <= 0)
		program.getDirectProgram().setHeading( CtiUtilities.STRING_NONE );
	else
		program.getDirectProgram().setHeading( getJTextFieldSubject().getText() );

	if( getJTextPaneMsgHeader().getText() == null 
		 || getJTextPaneMsgHeader().getText().length() <= 0 )
		program.getDirectProgram().setMessageHeader(CtiUtilities.STRING_NONE);
	else
		program.getDirectProgram().setMessageHeader( getJTextPaneMsgHeader().getText() );

	if( getJTextPaneMsgFooter().getText() == null 
		 || getJTextPaneMsgFooter().getText().length() <= 0 )
		program.getDirectProgram().setMessageFooter(CtiUtilities.STRING_NONE);
	else
		program.getDirectProgram().setMessageFooter( getJTextPaneMsgFooter().getText() );

	program.getDirectProgram().setNotifyActiveOffset( new Integer( ((Number)getJCSpinFieldMinNotifyTime().getValue()).intValue() * 60 ) );

	return o;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

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
	getJCSpinFieldMinNotifyTime().addValueListener( this );
	// user code end
	getJTextFieldSubject().addCaretListener(ivjEventHandler);
	getJTextPaneMsgHeader().addCaretListener(ivjEventHandler);
	getJTextPaneMsgFooter().addCaretListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMProgramDirectNotificationPanel");
		setPreferredSize(new java.awt.Dimension(350, 360));
		setLayout(new java.awt.GridBagLayout());
		setSize(412, 360);
		setMinimumSize(new java.awt.Dimension(350, 360));

		java.awt.GridBagConstraints constraintsJPanelMessage = new java.awt.GridBagConstraints();
		constraintsJPanelMessage.gridx = 1; constraintsJPanelMessage.gridy = 1;
		constraintsJPanelMessage.gridwidth = 3;
		constraintsJPanelMessage.fill = java.awt.GridBagConstraints.VERTICAL;
		constraintsJPanelMessage.weightx = 1.0;
		constraintsJPanelMessage.weighty = 1.0;
		constraintsJPanelMessage.ipadx = 8;
		constraintsJPanelMessage.ipady = -80;
		constraintsJPanelMessage.insets = new java.awt.Insets(2, 3, 8, 66);
		add(getJPanelMessage(), constraintsJPanelMessage);

		java.awt.GridBagConstraints constraintsJCSpinFieldMinNotifyTime = new java.awt.GridBagConstraints();
		constraintsJCSpinFieldMinNotifyTime.gridx = 2; constraintsJCSpinFieldMinNotifyTime.gridy = 2;
		constraintsJCSpinFieldMinNotifyTime.ipadx = 39;
		constraintsJCSpinFieldMinNotifyTime.ipady = 19;
		constraintsJCSpinFieldMinNotifyTime.insets = new java.awt.Insets(8, 5, 102, 10);
		add(getJCSpinFieldMinNotifyTime(), constraintsJCSpinFieldMinNotifyTime);

		java.awt.GridBagConstraints constraintsJLabelMinutesMNT = new java.awt.GridBagConstraints();
		constraintsJLabelMinutesMNT.gridx = 3; constraintsJLabelMinutesMNT.gridy = 2;
		constraintsJLabelMinutesMNT.insets = new java.awt.Insets(11, 11, 103, 129);
		add(getJLabelMinutesMNT(), constraintsJLabelMinutesMNT);

		java.awt.GridBagConstraints constraintsJLabelMinNotifyTime = new java.awt.GridBagConstraints();
		constraintsJLabelMinNotifyTime.gridx = 1; constraintsJLabelMinNotifyTime.gridy = 2;
		constraintsJLabelMinNotifyTime.ipadx = 7;
		constraintsJLabelMinNotifyTime.insets = new java.awt.Insets(9, 25, 102, 5);
		add(getJLabelMinNotifyTime(), constraintsJLabelMinNotifyTime);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
public boolean isInputValid() 
{
	return true;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		LMProgramDirectNotificationPanel aLMProgramDirectNotificationPanel;
		aLMProgramDirectNotificationPanel = new LMProgramDirectNotificationPanel();
		frame.setContentPane(aLMProgramDirectNotificationPanel);
		frame.setSize(aLMProgramDirectNotificationPanel.getSize());
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
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) 
{
	LMProgramDirect program = (LMProgramDirect)o;

	if(program.getDirectProgram().getHeading().compareTo(CtiUtilities.STRING_NONE) != 0)
		getJTextFieldSubject().setText( program.getDirectProgram().getHeading() );
	
	if(program.getDirectProgram().getMessageHeader().compareTo(CtiUtilities.STRING_NONE) != 0)
		getJTextPaneMsgHeader().setText( program.getDirectProgram().getMessageHeader() );
		
	if(program.getDirectProgram().getMessageFooter().compareTo(CtiUtilities.STRING_NONE) != 0)
		getJTextPaneMsgFooter().setText( program.getDirectProgram().getMessageFooter() );
		
	getJCSpinFieldMinNotifyTime().setValue( new Integer(program.getDirectProgram().getNotifyActiveOffset().intValue() / 60) );
	
}
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	//fire this event for all JCSpinFields!!
	this.fireInputUpdate();
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) 
{
}
}
