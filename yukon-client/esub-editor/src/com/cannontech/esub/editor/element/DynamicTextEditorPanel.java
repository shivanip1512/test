package com.cannontech.esub.editor.element;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.event.TreeSelectionEvent;

import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.esub.*;
import com.cannontech.esub.editor.Util;
import com.cannontech.esub.element.DynamicText;

/**
 * Creation date: (12/18/2001 2:05:01 PM)
 * @author: 
 */
public class DynamicTextEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.TreeSelectionListener {
	
	private static final String ATTRIBUTE_CURRENT_VALUE = "Current Value";
	private static final String ATTRIBUTE_CURRENT_VALUE_AND_UNIT_OF_MEASURE = "Current Value / Unit of Measure";
	private static final String ATTRIBUTE_POINT_NAME = "Point Name";
	private static final String ATTRIBUTE_DEVICE_NAME = "Device Name";
	private static final String ATTRIBUTE_LAST_UPDATE = "Last Update";
	private static final String ATTRIBUTE_LOW_LIMIT = "Low Limit";
	private static final String ATTRIBUTE_HIGH_LIMIT = "High Limit";
	private static final String ATTRIBUTE_LIMIT_DURATION = "Limit Duration";
	private static final String ATTRIBUTE_MULTIPLIER = "Multiplier";
	private static final String ATTRIBUTE_DATA_OFFSET = "Data Offset";
	private static final String ATTRIBUTE_ALARM_TEXT = "Alarm Text";
	private static final String ATTRIBUTE_CURRENT_STATE = "Current State";	
			
	private DynamicText dynamicText;
	private JColorChooser colorChooser;
	private PointSelectionPanel ivjPointSelectionPanel = null;
	private javax.swing.JButton ivjColorButton = null;
	private javax.swing.JLabel ivjColorLabel = null;
	private javax.swing.JComboBox ivjFontComboBox = null;
	private javax.swing.JLabel ivjFontLabel = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private LinkToPanel ivjLinkToPanel = null;
	private javax.swing.JComboBox ivjFontSizeComboBox = null;
	private javax.swing.JLabel ivjFontSizeLabel = null;
	private static final int[] availableFontSizes = {
		6,8,9,10,11,12,14,18,24,36,48,60,72,84,96
	};
	private javax.swing.JComboBox ivjDisplayAttributesComboBox = null;
	private javax.swing.JLabel ivjDisplayAttributesLabel = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == DynamicTextEditorPanel.this.getColorButton()) 
				connEtoC1(e);
		};
	};
/**
 * DynamicTextInputPanel constructor comment.
 */
public DynamicTextEditorPanel() {
	super();
	initialize();
}
/**
 * Comment
 */
public void colorButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	JDialog d = JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
		new java.awt.event.ActionListener() { //ok listener
			public void actionPerformed(java.awt.event.ActionEvent e) {
				getColorButton().setBackground(colorChooser.getColor());				
			}
		},
		new java.awt.event.ActionListener() { //cancel listener
			public void actionPerformed(java.awt.event.ActionEvent e) {
			}
		}
			);

	d.show();
	d.dispose();
}
/**
 * connEtoC1:  (ColorButton.action.actionPerformed(java.awt.event.ActionEvent) --> DynamicTextEditorPanel.colorButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.colorButton_ActionPerformed(arg1);
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
	D0CB838494G88G88G080DA8ADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E145BC8BF0D457F5E40A93D525C4F130AD3BA42181270A2DF14026B64E100438B4A5351AE0875BE40AB3328D35A6A33BCACCDC5C4A74E9755B557F3392C25F8573114C3212C000AC90C8C890B00A2D06B59689363552BEC9EBD67B165DA7349282FA6E673C77F6776D72F119FA66646DBD771E4F3D775C7339D7C4B0B7AC484EB01885A1A3DB607F9D4A9004369302F0F87C3F5EC35CA9A156A4E87E5B86D0
	AACC16E541F7G7A9675461ACA0431FDC6E83B21DDF5BC563485FE178B3F3B36209BFEC41093003EC763FF7BD4F4B9EE89656452766A5A82789681F881B78FA0D9A072D3EA2BB8FE99340B78EF829986C1181FC1463CDBD925416F48E0633C701D8D50E5A0E3B26BEAF97FFA4011B16904564046B64217A9F9D7C7BB9FF761EB73040D7F1CEFA8A432B47C91E2B8EDA27FDA042F5305489462CE9F036F117E0B23C70725F15B30436311BC32B8BCE6937D93C3B6516912A51FCDF40B6322C76E68681D72B846DD43
	F6B1A01F20DD7D8E0F6896EAB3095E7D15C64A937486B98994372A122C55A07C8EG44E5226E4F949E613A4D3BB8CFC8DE38B9A3126E2919CC6712ACD5F72B39A6F3C59661EF2DAB60F47D500E81E03A48F528CBD6B66A726BF2B27688346DGEE853F44CC703321BD9FE0E9B66A58399BF59C3832D058B4E4B394C750B1A91B6936B1DB55B15934B9F3AB3517CDDE3267ADB9A097E08CE08EC0A2C092C08D35534724F7615BBDA477C8636312275B6BBD6EF67A7EA0B9C5B77C6E68009E0E7B23B8AC791C0240EC
	7123CA2B0EBE886BF3026D10G1B5B47825ACE629F9F90E4F907A1DBE7EECB1FABB4EC62BC8AF255399D49A56BF6E48BD9B7AB7C4E60EB4D70A7A9FE16864F766AE75496C8BB8E7AB66662BA8F9421AEFDD9F305125696C32E0EAE71CF6DB48C9879F9B12A3A30B5FE56C756F8BE6017812C86C881B01AE2CD0DA6DC6337257DB7DA6353E07BBE0F43FDF6E2486B704947A7DCEE276863EB3D2FAE5ADA6B81236B3AE5BA36AE897B35FB346E31E65DF5D917E0B6741A58FADCB0296B42E4CFC514BDE38A370DA2E5
	8FBF25E7A24E943A37E3943F40943A37E96538370B214F1C97EBE263F7D690FCA0AF14CFB34547640572F9420CFC6221AF9D006D2FD7A6BEF1B0BF56B483B08FA091A089A085A0BD9F7778143779A64FD13FE6FF99ED4625CA7802EB73B9CEFB7C3243B3AC1EF378DC0EA137689778183CCBE4DD0702680AAF915B9B86993F97BD224FA13BA48FF0F70C0BE0CCFEB6266AD2A9996393E92F5DB5AE0200F12F00FCAD174A616BF678650F3CCE07AC2AA3D8FF63A5E293179D6700A3B0A0E725002C171B7A5CDA785D
	DE00EB750D89020B03F6C2812E4B42CBE570BD8346AD3A410D3B06099A8E5F945A6871ADA446ADAF40B3797AG5ADEEF617D0275B91B2E6D9198ACE4F694A88C353DBE4AF3C1A1464DB51583D1EC1E483E15B322FF56DE0BAAA3738B70A74056B7FF1258688A1857BA0074C2DC63DFDD32B05BBB6E700BE8ED9F7A40D0C4273A62EC6C3F7AC8AC91AFC3D46D953D224789BF7CFC2FDF7619D4BB9C11949BC921F86217FA40D07C8A7EF5DFA943838F2DD5E37F1B345FA14BBE575004AC1E95E5596599D569B7D3DD
	7CC1AE4F4F67DA98A0F3B597451A2CGC70AF02EDF7117EB6C29CF12AECEF86D6424F03B0C2371379F460F95617EBB4D449E66A8ED8FED2B7DEF16635E11B1DFE41FA25BC73199E5FCD1DB08ACE3CF8519D70652BEC05B0B15F97E006A20F7167E6FF4F81E4A3893F3ACD26CE6DA0EEC43DF1275ED78AB323E8D7F359C5DC66F1175EC14754D17C96EA2CDFAB4B67F37B271F7431247A35203271088AC6FFD5DCE66617238640FDD4ED1D1BE6B1A9681D1CCEC77AF28CFDD8B7B15DA047EF0729D7407BB266F91D6
	FCE6524D17629FECB47496B35B9DA8D67DA153695B9364AC71DC3AC71A70488C1FC07DB5477BB3A9AE2DAB1E50B676C8EE49F7FCC216A50F55C1E7E2E31FFE51B7A27946C5E763670E4B8E1BE3D236511D30F5536E9364F713E56F5E23967D1C5DBA2CF27E2CBB04E5DFE41E071B4EB535B05F50B5C5E3BB4CF38E4022E2748DBBAE13FE748D5C8142C45CECAB202F36042C7355ADE64D5AADAFE6351431847914CE1610BD97AFCD08900A09F3B9A3CEE1BC1DAC8B69BB67F0CF28F15AB2198FDF3746AFB1FCF9C0
	FF8F368648993D28702F8B6488A45EB21E4CE696BB88CF2F4FB58EEE1CB8DADE537DDD1D3626F3027E247EDBC43EG0C7697F612DBC4A81F0438D242E724826B3E55C01782E0ACE53CCE15B2DBDAC747BD2054D94D8AEE8945FD888A1E4F417C718FA55A7A50695F26EB3354C64B987F3632503CFAE21A7035835ED9067BF2DFC06B7B3B7DFEE958C5539A3E46A72734764CF0E7267260BB627279A147F60A815C277F19A23A123C072379DABF3C27D3884F635822E1E86D192496E917FAA075BA85DB5D239C77F3
	AEF11258992D6F16EE41758A1461F93EFAD2DBFFAEAF2EB464C7D81B45FCCDD6EB5626B6EFF3A653737E8ED2D368554F43D4F15B68044BB6A13BC85265104197FAA7E4ED4DDCDACFF4A3BA1514E3BDFABD075868A9E88F8284GD8EE7CD51AB39339F100DB85404E51E627769C89E628F9G7AG18AFF933E29F712D376CBDF0AF7EB350C1FD696D7A8B3447EF17F7F19D139E19AB9C397E9F99FA75F4CCF106E1504C56BA462C2E79221C478DB346478D3CFE5D27356D95499591EDFB8D6711E28E356D73B3E46D
	33AAE2CDD595E85BC9D3259A5B06DA9B4A00D1B7898DBC66CDE7B2BF415B57263351CF8898FB1F5CF2AB751E35827376FB2F12FD6C027685G99E08EC02222DB76159B562FA782D16A574AFA54AB29022DC5F2051AE7ED045F464A603C0B55EAEFD0FB2C2DE435DA2E027FEF0AEF5360191D5EC571245D87FD0BABB16F3927A2F4DD4CDB0265350434995D42B03A7A1036BD040E59ED71592DF8B609FD86EC7EC948A9BABA5EE7D6AC601DDC2D2C755F6EADACE70D036F22ADF81FF4C0A436C26E2816EF417DCA3D
	C67C35258A72E2GBB0097E09E40E200B5D538EF76ABFA3EACF85FA2B9B2B517E7B22D8251E3A3704E5A0A7C17DCA95458EC3F6472480A3DFEBAC3621864F30D3ABC089B2638E149C793A6162F2F26BE528CBCDB943E5F3C9AB93FFB702AFEFE7770D57D7CEE495568B54A13D7A365FFCC3F3D54CF7602EE03G8C77D48E4149500ED1F05FB292DCA234D7AA3895A602CB0136D00D36789635D1B3345BGCEA9786CDCFADFD94DFC68EC857FD7B9516ACA64D144F95EDE4E3EA82A0CECBE2EC8383D3C3E0E527D34
	32186E035FAF7A29511128447BDE15AB841D1C7983473848E502FF2CB071B1A7E42927D5B9EBEFB4D5691FB59A6C5899324BC6BE0704EA0CF50FF948FAA7C3FB93C0C98D2EF70728DD0B469A3626C778373FC6FF0D1DBA78G60660460F9DDFCAD32FD3FFDCD5F3EB32F13336617A6FC43E2288D67DD2FD27A5493FCDA96474992727C13524B8E9F549C217455141E75C522FF67FA74BA291546400052BEC05BD34ABE095DC50A0C939EA7F796AC07FC157A00BD1088FF1C017B5DDBCF74757A44915187D9BCAF13
	565610711F50B339GFEA756E02E714B9C62DB5C0EA9E98278465412F19F6422FF2D05F69B4085GEFAD7333CC2FA5B4DF9F71C1FA6C99BD6B950785E79D21FD5C08342BE17CFA00AC406FG682DC319AF1888AD903A46A7462906CC3F877328AF00F14B14313BFBC86ECA27FDD66485894B47D87F2F7FCC6C0FD9EDAB47B51A897FBE176722DDE2E3D95CE84F55460D75F5ECBE567AD8D397G9BB32FDCBBE6201E0D0907EFE2BD5A7AF999635B4AFAE627EB6BD5FBCD2D8F37E996FFFBA4F2F6048696FFCB953C
	1D622D9ABC0B4BCF5233C65A8350373E817D63859A5B3C9670F79644AD2138F950DE24604AC688EEB134D7A9389FF5925CBAE8EFD2F0DFA9C39FDC308DF15F2DC79FFC81F05EED641D4DC2264F5778099C8BFCBD6224C5C9A5FBDDD0BF104B4E46361683D6CB5B4103CDAC77DBB0294D7D5A73EAA266FECB373135DB453F339D55FCADAE67623B1130BD56543E9D6D30F530D66B4708A29D9D3F77391C479D23FD542A19AF7BE12E1153FB3723AFCB25BC93203D82E06DF62CB5823B3056C87C4EDC21EFD6336EBB
	DD323D5210361D4710F7B4F92FE9F3A613394E08B27B21BF669D024BB3E9F3FF32168F427F58604B7A0F8475133DFFCE5076FB027A093EFB026897847513B57ACBCE4F667674CE1CDB7A537385775F57C5FC03DC4D673411FF2BDA2A0565A70CD94C964E18D4DB983AA9DBE07B754A609161965EAFF58EBEDBDBEFAE46A66B8E5CCF171F6C27935AB3G73F6200DD89DED37A2D363B8182C24F2A3DF4BC4C556A17AFE329E5A69G661DF8EE3CB266BDD68577F400603A20EDD7F06FCF63198B6CC43FF2DAA26B38
	8A5A49G1BGCAF6C1DE83508BB088B0FB970E7D939D1B886DA40054DD6C7E21E0B748856883F06F4631BF724C8A59DB42ABF43FD166EC1D3ED068550CA98999CF74EF546141F4F9B9CC17E29D7E594A789B67C905A167C15BC76CB3DF0706592DDFA431EFC87251203EFAB75917D8115EF96F467813CE7F16C0064402593DA7CF357B26919A479C0110B856CD7D2E39B156E4E9C4BE4FF7C60E4B7FCEFDF29F0C9DGC8EAA438EF1675F2DDE7B76A2FE5268662E1CCE2A32B83485A2DE8E431A4C31171AC756766
	A65029897D5C4F7F2C754BA58FE49B10A6D4DEDF933FA3ED8A2D49AF66933EF3G1F5004F508183B873E679C1F075F8281325FA7F9C51FBC45307496106F75CC6E6D3D57465EA65DBF5CB29D0B5703C6F276D7B529EB4C70AE0ADF2341335875DBA5F6A5C3DF6F3BD80F4EAFA039C68234D7822C835888E05E83B55D9EDC4BC7F2DB049B5422F6495B87C931DBD32631793DE43C3DF76C9F67E20CD87A7DFB05E5DFFEB1A352DDDB6F9EE68FCE7EDDEC2B26F8B63F66A2B23FF9503798E08DC0AA00E00DB5D5D9F1
	FEEF52BDA095B518C337ACBB064768ADA4CE0EDC8C2AE812E83239CDA8FBE735061F0FAE40592DFAF7A3AF5298B1E68D5D3BFF23F8599A3AF7129F77EE96742D3722CFFD269C651F5A9BAE7F028E0E49F918468D6F5ED079CBA9FEE6EF287C4EF2149F83FD29G6CBC5FCF63CC3F8D72B39B7A18178361397CDB814CE7E3ECAC17DFG5F25B6D6AFB3DF6318C6DF13E48B579B5F7B9112E14CC6008A5EB6B62BBAB3BDB2E9ADF692E2EC1FE308DC17F3FCFCE3814703B18FC94725G437FEA1CB8D57AF24289983E
	73CCBE62798D90436FAFAD64FC089B8D1671446192CDDF301CE75B681D3E4B6F05B56A46124C9FA473E943D63DB148C84037103BA5ED1EE5EEE66B595E0CF73C2D535A7EBE5EAFAB7D6BC77468E3147E774A757A67AB7D3EFABD7E0B157E5DE6BD7A15CA7FF7F56957B623ADAD908F89EACE472F87ECACD65AA0C3920F531F6AF5B45DA65B105BB1FC31097B11CED19BDFDA2EEC48480C60C7D21B195D25B72BF9BF0B37D5E4BB795EAC6DEC90A2661987DBDA2C16FD7B1AB85D49D215EE262CBE8A5D81C6466F77
	FFDE06391E35A55654C06985FA07477ECBBB960E93384CC74893971F7BCB8568C3EB9F7E2A507B420B996FC4183B33054DF9E90B9ADBE76A9FB7B01E6DE6F59E3565D166514A274FFD6F86CA47FC6C8F72F68A9162B1F85877015707C31FC306EDF70CAAB45EFC220BF2EF4142B46F1B4CA74C49DB2C4B61F6CD53272433B050B34AFCA3CDFFF80C15B43FB765C63BAF7B2240E4149A6F248CFE17DA90799EAA31C07F9E6A51827DFB56E585516F07D694C4FEC77F27826DBBBA5B7737AB485E2402AD99DB2196G
	68823037E23C7D28F03BC0632DCE2CBD619CA577E064AF29E4145FDE94F92E470AEE7C77D7270A08EF9F1240FD0F937E087F20086C17ACF9A912614E9611B57239C647E4B614634F97513F9396C7A81A55CFF3E87D6705F94DE9D56EC0BA3026DA20602EDCA338B4E89B77E16CFE452F5AE42A6677047F4E5B675CBBAA034DB9259CEB505AFD388EAF57625A34A86B3024DC1D7B77B53FFB4A6F7CBADC3823B2186F0CA3FFD24EFD670AC01458C16FA85E5B5F44F32EEDD3CA4E87EB35EC9F3EA9392670BEA7ED9F
	5E835CC747B66E07F386B0309F475EBD0DF1443B7FD663489A123261BD7DC804FBFA1A35B0D9BF15D0D642FE3C7398F5576853A9198C239D1B463776E4C5771AD164D75506B82FF287522F60589DA1210DD1F01F1A71DEE60502CB2EC75ACDEDD87BFCE9AB292BDB8E20DFCAA9A2FEA9D24DC85E59D804C17BF9A0680D3197788C9C08FEEF4164BEDACDF2DDB70C0D81C884D8893096A08DA06BA054A6GD600DE008BGDE0059G71G89G4B81D69FC43D5FA8EB0A22B73F0824CE5567503E73E5D7C6762B8515
	B7762B95746FC7C4700776A9AFFFEB282E24EF07A3A3FED1715F8D15A58A6E343FDF723BC834E6E7E2E1EE64BB0965543F2E07F9269D44BAE1D58756893507746B04ECC12D93DAE0CC6FA135CE98B894DAA75CFDCD7F9D60A16A331DB05EFB886B1DD2EAD773211DF08875F92B9675D993C19FED6D1282E352B57A14342B7AB01B5ABDF5FB778887EE134EF34BF42CD64C1F266F7B6D21B52419629B5BC3EB48EFA87E2C8D7A625B517F5FB72D151F8E781277221FA1C677B3C9CF6F9D1C36DD0787239B1F0EB65F
	3E08F416D1BD3AC1BEDE0EC8B7D12EC79743474F0FC877C8FDB4BD97C7249BB6933A15A17DEBF9BBB5A25D86AACF6850DFFB338E1E1D6D3B3612736E67FE1167C095641CB4F6B09BE03847A92E0B63986DCF2AA37B1FFF293E317FF93EDABF2FDBD79D1E573DD42D1F573DDA2D4D6B98AE351A7810D1701B834A385FA838738A6ECD4A1360BEF6B965B1851FCE75A278D3A29108F8C38D51EB126286B8AED703BB0F3E37062CA9CCF9CC72A1EDE98D09D5A3AE371B7BDEBA466581172A2CC7F58D515165F5E874EE
	D0F0C4EF965B671267AA6E7F3E53E1912267A47B5B5A1AD88CFD2B83E368050E1B4DA7520EB11987C95948651FEFD06577792348EF5D574264B33A990BCA77C26D0DF2297D10CBB13D5F2DC33D67696AAD88117320EF11FF4E07F55D347A3BDA73FB61541D4FD5175FD1996C0CBEB29DFEEE9F1B561EDB065BA061BBEFD207721664455CFE2D021B2D3CAFB59EC65C21D1248DA83878D19CB7DB414D21395E9AE82FD7F03FA947F1E98A6E9F6B9157F584F153B4CE47C1BBC1413DC4F1CB213DD2411D2CC05A558A
	AE3D9EE9AD1D087B248147D9BBB137F56CA2710C5FBF50634F703501C2950F471F6F61668E2DCF1F81DEF11DB7936B1A037EBD56A220D9F683BAE63BB9643AC760772BDE65FED507B6B4CFC964EFB9F8DF145C2946F3466FEF147B5AD49DFEF84706EFC442516037A173D1159F5335E9072A6B77C6113765E8B8EFEDFE2727EB5B51509CEFE1F9707BD95FCED516510C323AF4E4055EBB5BE18C8114B5F6B4F4DD5655A3BFD907DFE68844F0FE38AE7343F47F9A7DBB92D9125CF617177C8DB8DF2FE7F7894A18CD
	6660797DEE37BA3FCFAAD01F048873532E67F298C32002772D8E5B272AA615375C003C57C518AB3E21125FE99A5E841FF5CC654D4ED945E174BF3FB0A02E7AB07A1F1A8C441D2C43F19B945C19BA9C7731007728F7FB516F179C433F9FB9CC7E49AC6FCFD26A63D3477463992399362AB449CADDBACB6179653DEA7FFBE66CDF264B935771B91BCAD35800B4E9D17558542C524C2DC01A462EE8F2F6352AB48FAAF286F5E9D04EC764CF3F396F394F2B75BD71C01758F5B3BE6B1920BCFB85502C39A93A8DC1F9F6
	8A50245F945DD133162E24BB5634237BE6684A9A34F46DC053F7D3F4DF8F2A7D8701C63EA93A07026445G4D7C9B5071586C55094D5E70581C229343DF53096169236163B2464247FDE48E9F77A7F37838260670F1360670F18F6954865F2B889F77E39DFEBF8962477E3F9A6EAD19853EA271B5A15B6F934A42F3416E393FFCA17133DE33C1B01C96F246E8C0BEADE4CF12089D700277F996609E7FA5619A31F95602080D88812A90430036A5F40320E2C550E91A01A7DF5E5F11794A42CC031085D2D8C5F2DA48
	A2E5858873B12119D0119011C2169602F867A92DCF9435C81EEF54D0A83AE58C840D5165CA468881056B3648943210CDDEACBBE4A107BE23410FECF29F825F4376B15122BC8F78AD5208C5860C1F7EC9146834F8499FC8814AA1DBA621C0318C0996A7FB3C941D32102F3EE0FEC1DED1267A7F0CE56FEDCD5735EF0D1B6ECA4F0874B706A93D7C2D31B774EFD9BCB447EDBF81F941893C5F7BE19989B0115E4E4E0EBB5CEE59613E586DF112F73463D332283E5D784B89FFBF0E611D7C5F2514933DBF57507CAFD0
	CB8788C54AA9B3C597GGDCC5GGD0CB818294G94G88G88G080DA8ADC54AA9B3C597GGDCC5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGFF97GGGG
**end of data**/
}
/**
 * Return the ColorButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getColorButton() {
	if (ivjColorButton == null) {
		try {
			ivjColorButton = new javax.swing.JButton();
			ivjColorButton.setName("ColorButton");
			ivjColorButton.setBorder(new javax.swing.border.LineBorder(java.awt.Color.black));
			ivjColorButton.setText("");
			ivjColorButton.setMaximumSize(new java.awt.Dimension(600, 22));
			ivjColorButton.setPreferredSize(new java.awt.Dimension(65, 22));
			ivjColorButton.setMinimumSize(new java.awt.Dimension(10, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjColorButton;
}
/**
 * Return the ColorLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getColorLabel() {
	if (ivjColorLabel == null) {
		try {
			ivjColorLabel = new javax.swing.JLabel();
			ivjColorLabel.setName("ColorLabel");
			ivjColorLabel.setText("Color:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjColorLabel;
}
/**
 * Return the DisplayAttributesComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getDisplayAttributesComboBox() {
	if (ivjDisplayAttributesComboBox == null) {
		try {
			ivjDisplayAttributesComboBox = new javax.swing.JComboBox();
			ivjDisplayAttributesComboBox.setName("DisplayAttributesComboBox");
			ivjDisplayAttributesComboBox.setToolTipText("The attributes of the selected point that will be displayed");
			// user code begin {1}
			ivjDisplayAttributesComboBox.addItem(ATTRIBUTE_CURRENT_VALUE);
			ivjDisplayAttributesComboBox.addItem(ATTRIBUTE_CURRENT_VALUE_AND_UNIT_OF_MEASURE);
			ivjDisplayAttributesComboBox.addItem(ATTRIBUTE_CURRENT_STATE);
			ivjDisplayAttributesComboBox.addItem(ATTRIBUTE_POINT_NAME);
			ivjDisplayAttributesComboBox.addItem(ATTRIBUTE_DEVICE_NAME);
			ivjDisplayAttributesComboBox.addItem(ATTRIBUTE_LAST_UPDATE);
			ivjDisplayAttributesComboBox.addItem(ATTRIBUTE_LOW_LIMIT);
			ivjDisplayAttributesComboBox.addItem(ATTRIBUTE_HIGH_LIMIT);
			ivjDisplayAttributesComboBox.addItem(ATTRIBUTE_LIMIT_DURATION);
			ivjDisplayAttributesComboBox.addItem(ATTRIBUTE_MULTIPLIER);
			ivjDisplayAttributesComboBox.addItem(ATTRIBUTE_DATA_OFFSET);
			ivjDisplayAttributesComboBox.addItem(ATTRIBUTE_ALARM_TEXT);
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDisplayAttributesComboBox;
}

/**
 * Return the DisplayAttributesLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDisplayAttributesLabel() {
	if (ivjDisplayAttributesLabel == null) {
		try {
			ivjDisplayAttributesLabel = new javax.swing.JLabel();
			ivjDisplayAttributesLabel.setName("DisplayAttributesLabel");
			ivjDisplayAttributesLabel.setToolTipText("The attributes of the selected point that will be displayed");
			ivjDisplayAttributesLabel.setText("Attributes:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDisplayAttributesLabel;
}
/**
 * Return the FontComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getFontComboBox() {
	if (ivjFontComboBox == null) {
		try {
			ivjFontComboBox = new javax.swing.JComboBox();
			ivjFontComboBox.setName("FontComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFontComboBox;
}
/**
 * Return the FontLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFontLabel() {
	if (ivjFontLabel == null) {
		try {
			ivjFontLabel = new javax.swing.JLabel();
			ivjFontLabel.setName("FontLabel");
			ivjFontLabel.setText("Font:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFontLabel;
}
/**
 * Return the FontSizeComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getFontSizeComboBox() {
	if (ivjFontSizeComboBox == null) {
		try {
			ivjFontSizeComboBox = new javax.swing.JComboBox();
			ivjFontSizeComboBox.setName("FontSizeComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFontSizeComboBox;
}
/**
 * Return the FontSizeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFontSizeLabel() {
	if (ivjFontSizeLabel == null) {
		try {
			ivjFontSizeLabel = new javax.swing.JLabel();
			ivjFontSizeLabel.setName("FontSizeLabel");
			ivjFontSizeLabel.setText("Size:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFontSizeLabel;
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
			ivjJPanel1.setPreferredSize(new java.awt.Dimension(405, 93));
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsFontLabel = new java.awt.GridBagConstraints();
			constraintsFontLabel.gridx = 0; constraintsFontLabel.gridy = 0;
			constraintsFontLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsFontLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getFontLabel(), constraintsFontLabel);

			java.awt.GridBagConstraints constraintsFontComboBox = new java.awt.GridBagConstraints();
			constraintsFontComboBox.gridx = 1; constraintsFontComboBox.gridy = 0;
			constraintsFontComboBox.gridwidth = 3;
			constraintsFontComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFontComboBox.anchor = java.awt.GridBagConstraints.EAST;
			constraintsFontComboBox.weightx = 1.0;
			constraintsFontComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getFontComboBox(), constraintsFontComboBox);

			java.awt.GridBagConstraints constraintsColorLabel = new java.awt.GridBagConstraints();
			constraintsColorLabel.gridx = 2; constraintsColorLabel.gridy = 1;
			constraintsColorLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsColorLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getColorLabel(), constraintsColorLabel);

			java.awt.GridBagConstraints constraintsColorButton = new java.awt.GridBagConstraints();
			constraintsColorButton.gridx = 3; constraintsColorButton.gridy = 1;
			constraintsColorButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsColorButton.anchor = java.awt.GridBagConstraints.EAST;
			constraintsColorButton.weightx = 1.0;
			constraintsColorButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getColorButton(), constraintsColorButton);

			java.awt.GridBagConstraints constraintsFontSizeLabel = new java.awt.GridBagConstraints();
			constraintsFontSizeLabel.gridx = 0; constraintsFontSizeLabel.gridy = 1;
			constraintsFontSizeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsFontSizeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getFontSizeLabel(), constraintsFontSizeLabel);

			java.awt.GridBagConstraints constraintsFontSizeComboBox = new java.awt.GridBagConstraints();
			constraintsFontSizeComboBox.gridx = 1; constraintsFontSizeComboBox.gridy = 1;
			constraintsFontSizeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFontSizeComboBox.anchor = java.awt.GridBagConstraints.EAST;
			constraintsFontSizeComboBox.weightx = 1.0;
			constraintsFontSizeComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getFontSizeComboBox(), constraintsFontSizeComboBox);

			java.awt.GridBagConstraints constraintsDisplayAttributesLabel = new java.awt.GridBagConstraints();
			constraintsDisplayAttributesLabel.gridx = 0; constraintsDisplayAttributesLabel.gridy = 2;
			constraintsDisplayAttributesLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getDisplayAttributesLabel(), constraintsDisplayAttributesLabel);

			java.awt.GridBagConstraints constraintsDisplayAttributesComboBox = new java.awt.GridBagConstraints();
			constraintsDisplayAttributesComboBox.gridx = 1; constraintsDisplayAttributesComboBox.gridy = 2;
			constraintsDisplayAttributesComboBox.gridwidth = 3;
			constraintsDisplayAttributesComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDisplayAttributesComboBox.weightx = 1.0;
			constraintsDisplayAttributesComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getDisplayAttributesComboBox(), constraintsDisplayAttributesComboBox);
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
 * Return the LinkToPanel property value.
 * @return com.cannontech.esub.editor.element.LinkToPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private LinkToPanel getLinkToPanel() {
	if (ivjLinkToPanel == null) {
		try {
			ivjLinkToPanel = new com.cannontech.esub.editor.element.LinkToPanel();
			ivjLinkToPanel.setName("LinkToPanel");
			ivjLinkToPanel.setPreferredSize(new java.awt.Dimension(405, 33));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLinkToPanel;
}
/**
 * Return the PointSelectionPanel property value.
 * @return com.cannontech.esub.editor.element.PointSelectionPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private PointSelectionPanel getPointSelectionPanel() {
	if (ivjPointSelectionPanel == null) {
		try {
			//ivjPointSelectionPanel = new com.cannontech.esub.editor.element.PointSelectionPanel();
			ivjPointSelectionPanel = com.cannontech.esub.editor.Util.getPointSelectionPanel();
			ivjPointSelectionPanel.setName("PointSelectionPanel");
			ivjPointSelectionPanel.setPreferredSize(new java.awt.Dimension(405, 344));
			ivjPointSelectionPanel.setMinimumSize(new java.awt.Dimension(120, 344));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointSelectionPanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
	dynamicText.setPoint(getPointSelectionPanel().getSelectedPoint());
	dynamicText.setFont( new Font( getFontComboBox().getSelectedItem().toString(), Font.PLAIN, ((Integer) getFontSizeComboBox().getSelectedItem()).intValue() ));
	dynamicText.setPaint(colorChooser.getColor());
	
	String link = getLinkToPanel().getLinkTo();
	if(link.length() > 0 ) {
		dynamicText.setLinkTo(link);
	}
	
	int att = PointAttributes.VALUE;
	String attStr = getDisplayAttributesComboBox().getSelectedItem().toString();
	if( attStr.equals(ATTRIBUTE_CURRENT_VALUE) ) {
		att = PointAttributes.VALUE;
	}
	else
	if( attStr.equals(ATTRIBUTE_CURRENT_VALUE_AND_UNIT_OF_MEASURE) ) {
		att = (PointAttributes.VALUE | PointAttributes.UOFM);
	}
	else
	if( attStr.equals(ATTRIBUTE_DEVICE_NAME) ) {
		att = PointAttributes.PAO;
	}
	else
	if( attStr.equals(ATTRIBUTE_LAST_UPDATE) ) {
		att = PointAttributes.LAST_UPDATE;
	}
	else
	if( attStr.equals(ATTRIBUTE_POINT_NAME) ) {
		att = PointAttributes.NAME;
	}
	else
	if( attStr.equals(ATTRIBUTE_LOW_LIMIT) ) {
		att = PointAttributes.LOW_LIMIT;			
	}
	else
	if( attStr.equals(ATTRIBUTE_HIGH_LIMIT) ) {
		att = PointAttributes.HIGH_LIMIT;
	}
	else
	if( attStr.equals(ATTRIBUTE_LIMIT_DURATION) ) {
		att = PointAttributes.LIMIT_DURATION;
	}
	else
	if( attStr.equals(ATTRIBUTE_MULTIPLIER) ) {
		att = PointAttributes.MULTIPLIER;
	}
	else
	if( attStr.equals(ATTRIBUTE_DATA_OFFSET) ) {
		att = PointAttributes.DATA_OFFSET;
	}		
	else
	if( attStr.equals(ATTRIBUTE_ALARM_TEXT) ) {
		att = PointAttributes.ALARM_TEXT;
	}
	else
	if( attStr.equals(ATTRIBUTE_CURRENT_STATE)) {
		att = PointAttributes.STATE_TEXT;
	}

	dynamicText.setDisplayAttribs(att);
	
	return dynamicText;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getColorButton().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DynamicTextEditorPanel");
		setPreferredSize(new java.awt.Dimension(405, 466));
		setLayout(new java.awt.GridBagLayout());
		setSize(405, 466);
		setMinimumSize(new java.awt.Dimension(405, 466));

		java.awt.GridBagConstraints constraintsPointSelectionPanel = new java.awt.GridBagConstraints();
		constraintsPointSelectionPanel.gridx = 0; constraintsPointSelectionPanel.gridy = 2;
		constraintsPointSelectionPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsPointSelectionPanel.weightx = 1.0;
		constraintsPointSelectionPanel.weighty = 1.0;
		constraintsPointSelectionPanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getPointSelectionPanel(), constraintsPointSelectionPanel);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 1;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(0, 4, 8, 4);
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsLinkToPanel = new java.awt.GridBagConstraints();
		constraintsLinkToPanel.gridx = 0; constraintsLinkToPanel.gridy = 0;
		constraintsLinkToPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsLinkToPanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getLinkToPanel(), constraintsLinkToPanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getPointSelectionPanel().getIvjDevicePointTree().addTreeSelectionListener(this);
	Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
	for( int i = 0; i < fonts.length; i++ ) {
		getFontComboBox().addItem(fonts[i].getFontName());
	}

	for( int i = 0; i < availableFontSizes.length; i++ ) {
		getFontSizeComboBox().addItem( new Integer(availableFontSizes[i] ));
	}
	colorChooser = Util.getJColorChooser();
	// user code end
}
/**
 * Creation date: (12/18/2001 3:46:27 PM)
 * @return boolean
 */
public boolean isInputValid() {
	return (getPointSelectionPanel().getSelectedPoint() != null);
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DynamicTextEditorPanel aDynamicTextEditorPanel;
		aDynamicTextEditorPanel = new DynamicTextEditorPanel();
		frame.setContentPane(aDynamicTextEditorPanel);
		frame.setSize(aDynamicTextEditorPanel.getSize());
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
public void setValue(Object o) {
	dynamicText = (DynamicText) o;

	getPointSelectionPanel().refresh();
	
	if( dynamicText.getPointID() != DynamicText.INVALID_POINT ) {
		LitePoint point = dynamicText.getPoint();
		getPointSelectionPanel().selectPoint(point);
	}
		
	getLinkToPanel().setLinkTo(dynamicText.getLinkTo());

	for( int i = 0; i < getFontComboBox().getItemCount(); i++ ) {
		if( getFontComboBox().getItemAt(i).toString().equalsIgnoreCase(dynamicText.getFont().getFontName()) ) {
			getFontComboBox().setSelectedIndex(i);
		}
	}

	for( int i = 0; i < getFontSizeComboBox().getItemCount(); i++ ) {
		if( ((Integer) getFontSizeComboBox().getItemAt(i)).intValue() == dynamicText.getFont().getSize() ) {
			getFontSizeComboBox().setSelectedIndex(i);
		}
	}

	String attStr = ATTRIBUTE_CURRENT_VALUE;
	int att = dynamicText.getDisplayAttribs();	
	if( (att & PointAttributes.VALUE) != 0 ) {
		if( (att & PointAttributes.UOFM) != 0 ) {
			attStr = ATTRIBUTE_CURRENT_VALUE_AND_UNIT_OF_MEASURE;
		}
		else {
			attStr = ATTRIBUTE_CURRENT_VALUE;
		}		
	}
	else
	if( (att & PointAttributes.NAME) != 0 ) {
		attStr = ATTRIBUTE_POINT_NAME;
	}
	else 
	if( (att & PointAttributes.PAO) != 0 ) {
		attStr = ATTRIBUTE_DEVICE_NAME;
	}
	else
	if( (att & PointAttributes.LAST_UPDATE) != 0 ) {
		attStr = ATTRIBUTE_LAST_UPDATE;	
	}
	else
	if( (att & PointAttributes.LOW_LIMIT) != 0 ) {
		attStr = ATTRIBUTE_LOW_LIMIT;
	}
	else
	if( (att & PointAttributes.HIGH_LIMIT) != 0 ) {
		attStr = ATTRIBUTE_HIGH_LIMIT;
	}
	else
	if( (att & PointAttributes.LIMIT_DURATION) != 0 ) {
		attStr = ATTRIBUTE_LIMIT_DURATION;
	}
	else
	if( (att & PointAttributes.MULTIPLIER) != 0 ) {
		attStr = ATTRIBUTE_MULTIPLIER;
	}
	else
	if( (att & PointAttributes.DATA_OFFSET) != 0 ) {
		attStr = ATTRIBUTE_DATA_OFFSET;
	}
	else
	if( (att & PointAttributes.ALARM_TEXT) != 0 ) {
		attStr = ATTRIBUTE_ALARM_TEXT;
	}
	else
	if( (att & PointAttributes.STATE_TEXT) != 0 ) {
		attStr = ATTRIBUTE_CURRENT_STATE;
	}
		
	for( int i = 0; i < getDisplayAttributesComboBox().getItemCount(); i++ ) {
		if( getDisplayAttributesComboBox().getItemAt(i).equals(attStr) ) {
			getDisplayAttributesComboBox().setSelectedIndex(i);
		}
	}
	
	Color textColor = (java.awt.Color) dynamicText.getPaint();
	getColorButton().setBackground(textColor);
	colorChooser.setColor(textColor);
}

/**
 * Creation date: (12/18/2001 4:16:51 PM)
 * @param evt javax.swing.event.TreeSelectionEvent
 */
public void valueChanged(TreeSelectionEvent evt) {
	fireInputUpdate();
}
}
