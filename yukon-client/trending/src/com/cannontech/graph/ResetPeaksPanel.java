package com.cannontech.graph;

import java.awt.event.ItemEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;

import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.functions.GraphFuncs;
import com.cannontech.database.db.graph.GDSTypesFuncs;
import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.util.ServletUtil;

/**
 * Insert the type's description here.
 * Creation date: (2/17/2004 9:42:47 AM)
 * @author: 
 */
public class ResetPeaksPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, java.awt.event.ItemListener {
	private javax.swing.JDialog dialog = null;
	private javax.swing.JButton ivjCancelButton = null;
	private javax.swing.JRadioButton ivjFirstDateOfMonthRadioButton = null;
	private javax.swing.JRadioButton ivjFirstDateOfYearRadioButton = null;
	private javax.swing.JButton ivjResetPeaksButton = null;
	private com.cannontech.common.gui.util.DateComboBox ivjSelectedDateComboBox = null;
	private javax.swing.JRadioButton ivjSelectedDateRadioButton = null;
	private javax.swing.JRadioButton ivjTodayRadioButton = null;
	private javax.swing.JPanel ivjDateSelectionPanel = null;
	private javax.swing.JPanel ivjOkResetButtonPanel = null;
	private ButtonGroup buttonGroup = null;
	private GraphDataSeries [] gds = null;
	private boolean updated = false;
	private Date resetPeakDate = null;
	private final String TODAY = "Today";
	private final String FIRST_OF_MONTH = "FirstOfMonth";
	private final String FIRST_OF_YEAR = "FirstOfYear";
	private final String OTHER_DATE = "OtherDate";
	private java.awt.Panel ivjPanel1 = null;
/**
 * ResetPeaksPanel constructor comment.
 */
public ResetPeaksPanel() {
	super();
	initialize();
}
/**
 * ResetPeaksPanel constructor comment.
 */
public ResetPeaksPanel(GraphDataSeries[] gds_)
{
	this();
	setGDSArray(gds_);
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/2002 11:49:08 AM)
 * @param source java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent event)
{
	if( event.getSource() == getResetPeaksButton())
	{
		// Default the initial reset value for today selection.
		GregorianCalendar tempCal = new GregorianCalendar();
		tempCal.setTime(ServletUtil.getToday());
		
		if( getButtonGroup().getSelection().getActionCommand().equalsIgnoreCase(FIRST_OF_MONTH))
		{
			tempCal.set(Calendar.DATE, 1);
		}
		else if( getButtonGroup().getSelection().getActionCommand().equalsIgnoreCase(FIRST_OF_YEAR))
		{
			tempCal.set(Calendar.DATE, 1);
			tempCal.set(Calendar.MONTH, 0);
		}
		else if( getButtonGroup().getSelection().getActionCommand().equalsIgnoreCase(OTHER_DATE))
		{
			GregorianCalendar selectedCal = new GregorianCalendar();
			selectedCal.setTime(getSelectedDateComboBox().getSelectedDate());
			tempCal = (GregorianCalendar)selectedCal.clone();
		}
		else
		{
			//TODAY...nothing more required
		}
		tempCal.set(Calendar.HOUR, 0);
		tempCal.set(Calendar.MINUTE, 0);
		tempCal.set(Calendar.SECOND, 0);
		tempCal.set(Calendar.MILLISECOND, 0);
		setResetPeakDate(tempCal.getTime());
		

		try
		{
			if( getGDSArray() == null)
			{
				//Load all Peak type GDS
				List allGDS = GraphFuncs.getAllGraphDataSeries(GDSTypesFuncs.PEAK_TYPE);
				GraphDataSeries [] gdsArray = new GraphDataSeries[allGDS.size()];
				allGDS.toArray(gdsArray);
				setGDSArray(gdsArray);
			}

			for (int i = 0; i < getGDSArray().length; i++)
			{
				GraphDataSeries gds = getGDSArray()[i];

				//Verify that only peak GDS may be updated!
				if( GDSTypesFuncs.isPeakType(gds.getType().intValue()))
				{
					gds.setMoreData(String.valueOf(getResetPeakDate().getTime()));
					System.out.println(" MORE DATA RESET TO: " + getResetPeakDate());
					Transaction t = Transaction.createTransaction(Transaction.UPDATE, gds);
					gds = (GraphDataSeries)t.execute();
				}				
			}
		}
		catch (TransactionException e)
		{
			e.printStackTrace();
		}
		setUpdated(true);
		exit();
	}				
	else if( event.getSource() == getCancelButton())
	{
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD4DAD2B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBB8BF0D35715CE88545B120CE9188921E46BA410BADD27CB3BE4820DF7566D123493182C3BA59327419DB3A5A9C9592C3725DD5A25596B1F6270F50213C0F140CD00F5D2CAA0505428867C2335313094CB36659F761374AC8B4BFA3274A4CB0E93765CFB5FF9FA161EEC4354754C613DFBBE771E5F3D775C7B8429B812DEDAD8D4C1C8E1A9D17E7ED8C8485173046C795A259FA32E39A54FC0B4FF3F85
	F80D4C0F1440538E344573738C6564343B8C5ADEE82F1D17E7F88B5E77130F5CA6A33CA49B87E84F7FFC4F116947F9C946F1DEE16D77EBF643F3AF001F0E8D105FC247EF28B92060D7C3FB2F72CE213088F8AFD31EFC610086FF74B2676BD31EBB98CFFFCDA57683ED4A13833079F22C4FD719F6730DB5CA3BEFD1BA5976C4F5519EBA1626FF0426D6AEBB2CBC2B48CA6608E262B873BEBCEBADA36E8BCEE97424D37079A41FACBA07CF3A8302FF58E8EC970322EC91051120C57009DEC2F2DB290E86C79913A7A4
	9D5ADC1F6F6D29D546DBBC12CE726FBF14D41FCA9357234E9453C796A78C85A65A778266DFAA17826D55GFC4CA7EBF04C33D7728C2B2ED05CC10172FA617DB600E5D7D017737F073A58BF79C7325D51DA34AF09AE5B2FA8BA3445F4C9DF333778A00B75C418462B8FE8A9G0B81D6836482EC87B844F26C8FA16A43FA075CA80D0ECA3EFA3F3F416B8ADC11DC22975E0DC620A838F651A985DC0470BC5A65B821238FC239F9EA8E45625EC5B06E196D8F90D92EAFAA5531ED55D36F979DD57A28B147EC63F63D99
	21F65981AF83AC84D88E3096E033996D3A9EBAB313DDAD10AB811F6035069CFEA1A0B704BCDE1798D06CDBD8B31DFDFA4065165E309C77CB46992DDFB67E132968959D3FEC4CB8DDC4AC5C9F87ACB13F70313F1FF46C9ACBE2BC585C3350BC3C1D6521895E8B159C65783B98BEDD0367B9FE0461E9FBA550AABE463C356FC59BCCA5CBC97969AB3AB6AC79F6C3D164E32ECF5A4771317DE9142E9FD9004F85A8GA8EB4FB3D483B481585BB13ECF04BE18E55EDAB43165327342EF42D30C488121459714851FD36C
	94829E41619503C461D998263ABB2648ADE2734989E37CC5740981C176C8BE68DD9895A111021CE7E978B54A93909955669995E100D1BF41FE6F8B6F0727D7884A9DFE17A00BB18ECE7FEA187AB2ACF4C20F50814D0CF66A73F7581A95017745G5CD70B7FC4F1DB21DDE2C53F9C9BFF9D1E2D10582257E8EC72B8299AC2A00A639F095035C48C0BBE39C9740BBE973C84955A71482BB13F8CC92A4EA7981E4E11C6D0BC2862BF0C3C46715007564BC8B7B23AA04B810FA3A40BD6D116BDBEF7CC3E0169921CB27D
	02045B7605DA1AE387402ED3GF52AFD6BA27BB57619A4E9A464375148A9FEFA112D33B66077DAF14F3BD320FBE01A957374A5BB66E9531E956444D32D3AF94A6622156767DAEB7CDC3B0775999B6318E03F41F9BF9BC8D34665E3DC1813B10A15A7777B4B67295F374238E5B618F7B6743BA1DA4567DB039094F106198390B151954BB24E3BA31ABC470AA774F36C55897D9C5BB7B1FD8E3DB52117C31CF6F8026EAD5238CF13136FCE3C4E66304FA7B225D59142EB0A17EC54979E1FC76E72385C22EC75CC08D0
	FB58E8EE1DE3F3308E5EEDB64C2571F7B1170ECE5CCB32870E6B6EE7CBD6DCAA4A3471D8E65BE23944F53A7BCF546722D79C85C7B5CAA11F4C71DFE673DB41AB6B4B22DAFD5E3BEAE9BE0E08D1056FCB3534988C8B5E10285A36D258C5FD9C700CC2286804D56A3A42F35A3AAE9F7423B5E0D5C71E61ACG17BDB1C97D9CDFD0711C7963C536FFG6FA0C02483F3662D7387B5F3D5C916C6496B652ED7E3D2F2211C46D09C8B09309453495C2A5A316B426BF134CE45A6CE5FFF015A64552C83E99D54A60BDA6351
	FD02635C8CC71D7A1DE25CF72F14EB6BD0D77002EE7CA8EC6860F15B52913FE7F96BE83FE51DF906831DE87B9D97346BD4FDB0A8B9BDECCBD0E2F1371156A7FA352B13EDE8A75DA14F49106C210B38A043DC7107E46EF3DECF0E1CABD35605931DD8CFDAFAE89CDC501E84D82C6AF27EE2E29EB5DC5C894FA1CFA088F510CB0C20BF1BAFD23F507DC9C1E35D762156CF59799FA675D3E62712671D71FE7AC6BD55E3A3608BD45D7E7562EB9ABFC10D895B319B22087A0C5595733CD65A213AD24CEB026B227FDCFC
	5DD3522554C870BC5115D87B707AE6AD73D7DDD7FC5D73A443DB3A626B1E4E9E2CFB860136368B77610C86EAD735BD4FD0E3C73BF6566F5158E511BCBED9356911FABAB72500476D71A1AE0B612042E60B955F4BBE47E25C8CFD7681F05CD70D9417826DF49577F6B7452D0276FABB6EC7CF74507520C0EDEFE06D12EEEC777560DEC2F97E5AFB8A9FA3240ED17CD76EFF2F6A4B079AE80CA2EA7B6B2C9DD5465CCF7E7E1C0165F3D48AC1GA63B293F9E1E325ED5834E82606A66FE60F25FB2D630F80C7AA59F4C
	9AF340837F3289DC5243768236B62F85191CEEDCA33FBECE5722A1A888E10B327AC5A75948F84FF663F84D506E831884D88610854047FB26096A49E678E9BCEF1776CEB9EF77721A26891E76DE9C7374A02D1759BAF2CD888BA705F159E87CCB4063EA905CA6E6B5DFAF1EE539C4FD636F457562D5A34D678CE8E7836476621E96B90E674F4C871692D30AC977AC1CEFBBD3D4504BFDD651971B6B8B617CE96923F7916517096A4F726EE370C495CFB66AB876E4C7DBEB1B392B0D601A13BE2EDDF3EAF656A4DDF3
	96AB63E7A84F4A50A1E51D6C69C63B7301F6201F62D23BABB432D43F670061A4BCB97D1BD368F4FD22FBC80C3EF58A1D6A7AE81116FE38C7CB27BA7FDE1167F62DBB067E5D31AE03F89FB6A63DEB986C67765C26BCD7B69EA24D0FBED34CE37FCCF5C5E26CD98AF15BFF56DB260C1F550FB13F4BC6E33E8D5A3B2F66990EDE4575A0BFE18D1D8978DA79A94B215A2BFC2DACD4718B983ED90367EB68BEA35E0F5800367AAA2E21373452DC2F9C00B5944082B08C108A30FCG57540F9ACF11994EFCB649EF024253
	2BB90EF01F7FDB2FB6174A173ED304FA455B35FAC029798762772F97CD541F5B81DFB108BAF5DC7E9DBCBB05EB0AB617DC9ED98AB0F5GE989C8FEB1A0C7B916D5AE4A5AB850FDF377845FFA85F3E755B76FA32B977C2AF0D792BB4E8EF27DED4A33396110264E3735D1FF5F8634F40035G9B818AG4A8434ED5965935C5FD47DFAD9961C432C02C0E7F7FA4471981AAEF85C366D3D7136D58A31BD661410ECBF5E55C1F323C960B9D32A625FE4F83B864FF3699E164B346D85DA0E0039BC0F5E1A62BD0E667D8D
	63DC1C1F266EF9A767F48C9E174F37A6BFDFADEA55BFDF5D552AFF3E3A27F57A73550A5664672B4CD66D790A6755381928323526E3496686E44FE5D8324C0EEBD071598437AA731F263DDAFD7814D1E83FB051750E1E4DBC0257B341B68DABB07A5CFC6C5BAC14FE56816B87008C108A304C0179BCF1659D1E4FBA39FC898EFB0D923B8D12F13C7E3664FEF6354DFC07B45AC6EBEC07A44B52A86D9F7141B61AAB3264E7C80E0B345178C0C9B8ACF3D6857FD99B7BAEA08EB1B42F777675D15CEA30EB3D83EB40BB
	7A3186ACF060BCF82D031DD71DF0AEF3A22E1261D2203DDC455DEF3B191C25FD4E44D3270E11F5D3E3144E0227D87555C2CB72F85DE919B9DE4BAC6C1C84FE36C57DA2475DEBE1357A50909CFC116F81CB390AEB89DA242007E6A22FF30E77A5561B677B300E7BAE7803561B79705C86C06B71BA9765D9515FB4030DDA588DB2D5GEFA86D33700E7BG57E3D383BDB74351912634F25ED63E01F07A0A1D14EE9EE1A77A069024A852F96D5956212DBDD3DDDC57CCF8E683F09E77009ECFAEBC378270B5DA6C437D
	3EC44CB3540A78BD6421BE2C4BE491EB13E799EFA6345782EC84B842F40533B5413F020BFAB8DEEF763553965FA7B76D46FD32729F3EC41A1E7ED5613BC4FF1F5CA672BCEE9A0A6D77DB0B77A9B5564F5E23B62AB5D664E4F09C569B237132264EDA5001E77149A1346556B6EA4BAAE82F8758BC04F6FFF9GEB329DAA2F095D271FF56799CCGAEB74E19113E1B7B86913DC939EF5C309C5F1F4D7DD47FC9F7FC8DE8E778D4F7FC8D38C94D0945C05B6E469AF011154E013E61BCC384E09940AA006F826483EC83
	A8736499AA814E8218G8681E400D400A5G19G599EDC6F6FEDFF0FA42F4D14C392DB748382BF1FF3BFFC21C36B078D505F96CFEC2D5B61C9D63F7C02453C7C9A37F7170AAFB1D37CA98D1E7BA11571533685E82B2EE15C178F44476140C8EC4C93A349464FE6F9D4B792DFBF2DE3F85BC8FC7D54AEE07DB48C3475A3B8FEC7BFD1FDF1D5737E633E392F1FC87F5C57CFAB2C497709CC6B4C7B4443D67DFD7DD1EB623E1EED555F579F37EA77F5655B061546428DF9E9D779F2D4DC370AFB0A75C9F1DD9E17BC2C
	621FE5FAD1FC33C887C47CA62657B84359955C739ADC37027B09156EDDE0723094C05982AB3DB79A72F83D3C3D0D71F8FC30EB287E78A553516397B4FA3F2C62285E3CC6390F2D13273CF906A6AF56AD1F6B2238E1E8CF2AB8EF8F56B269DE4C499DED3436AB00766E51BC43A9G8BC0E49469BB4D141E8E6DEC00FCG6203BD520774BB5A6273EA37A2BF1B3C21E35131CFA56167E3FCA5E18C6A03DD334CCD9A53E225AFBDBA774D2383682FBA9F7A6BFF1C68AF17EA6F638243C1BBC745FD0F6136C03BCCC21C
	BD69B7A5AAF7B3F3EC15C60E0FB116F40C5954F7FAD09C773E1F2C6920F96910826C83A42F3BF2549A269A6C2D831884C88320752FF2BF5825F3BF5805F299922F57326139C11163755A3C9E2C5736CA09FE427D8E6BB5624FB3D478E375DA35BF36A7F2BD1C5D342634C9AEA15AAE38BC92AF49B8AD7FED7AFBA0DE4709AEDA53E9D8947952410AB81EC6E954A1B5C891CE7F60B01D17FF26777D14E89E3AA279646144FEFEE27E4DD43EEED188E859086C2776DFB4EB6F384E7A39EF86614969FF08685133D47A
	53A1ADBDC721170FA17DB93F1E7CD1153EBF2CC72F81BA0F61E635B6B50D71983A60999963679E9E4357DD984334B17D98EAEB6E654033FAAC964375E371B1746D25B10CFDA35346703F6B69DE5BC87793AF47F3BB0A9DDABBF2957DC8G6DFC28C70FDE91407AFAFB87FE1328865CE0G6F057FF9A73DAB1B5577A51BC7760A8DEC66709A352E933F1DAD89E00D3A21130E13836DADGA4086367F5E1ADF7GF016A05639476CD85F2786310FB55D14B71B72D0F915F7D58F5644E5720D567CDF6CA0843FEF3E59
	F01868566AD372170F99B6E34DDEA7633D7727FF567E86ACEB5F1922E4F7EB9119674B92B9F6E65838E6EFB16F5B9BC1FBF2E50C473C4B27E79B8F6D64A5286F5EB076D99EC23FD50EA12ED945DDD679D2C29823FFF7E00CD6D31C4A7B9BA74628BA0CB1EAF6D25EC1E8C7G5242485B6842FEB342B79A23422ED80C76D98F6A4788432358EBF460F8792A8E1B55BCB9B00E7B574B9DB31DD9AC63D367B6175B5DB9135C6138B6173BB4231C963CB0B62F411B59BCC9990FBFA3FD4C708B47634FC8BF684418AD81
	5A8E555E9FF55D5C596E3FEED2AE6D066F2939DD3FE75FCA87A37176FE44705EC83C3D7F520D76C60036B102762E69510EDF974DB358225357A5DC6E93B3159B0E631DD45AE9BAFD70B3684F3B68FA3B9C6859G39G85G6593F8267CC1470C3FAB3C941966F705E391BDFF1E1AD06EA0A6927562F27B435345418EF25E893DBC7F6D181EDC0A423BB869F8E65D71B2947E2C24F239537EAEB4F93C1E3FC939A34E1B4B4FAC364E65CC446767E38C3FF1A2BEBF5B1C181FDB00E67A84733359259DFFA56057FEB2
	1B7C3C28EBEF2EA23BD5278FAE77C20F1E9C19D43E43CEA64A3DCD683A1B3D1FFEE374783C9E9FECAE6C970C844F94FDEC3F2F86595AC9342B0D2D597C3DCB731E5FB977F7G3F7C9B0CF126A7E6D32D667D316E399F7B07F3BA860FE98E5BAB1BA7F99EF35CD38CB7B88577C5D687CD82AEED924F134FB25CF2E82FD2F1F596BCE3E6CF62DA786B2E6477A8A5F67D6FD0BB6D7A5F214A6D537F167400BD59F7AA2EDFEA84F5AE7F94F5FEA102BAD72A38774308732A383F73D3DC9634F3D4DC4D987637D9455DBD
	26FEE3D1F1C3E1642B7B8CF15FD147B0FD0673695B4ED80E2D533C5B1CF31FEF23F3BA8637F9401518EFAED7E23EF5BA50DF4BD4FFBD62C0FFE528B8520BFC5BD51C500BFCA557717C9E6C247520CC0FB07064756667691513D2EF8A47693A2DFCB3E457E59CDF9959934363F519F20EFB1065FFD0B9CB735F7BE41B71CCE4D3477E29195E0D491064FC5C0758BA424F16DD96BCDB3A2E4FFCBF90811E05G6FAAFA27DF0F6DBF3C6F67FAE3FD13A8761D19246FD7B4FDE7834F86CD5FDBB4FD737E6A43585FB61D
	7E0A637AABCB192AEBE5CA7CB9B85DCF79C59F3DB4F5916E3FC77C6AD9837839CDE57DB11BD60E218E35A9B31F459746794B12926F2F472F467A7E21EADFDF123ECB27695B1F50778B83313E1FF4E05F13C97AAE54697B8D851F165077EEA156F724977BDE2653F7718CFD2FCC084B53BCDF83A1111F27D2AD6A6F03D2E6FB26C3BDBFF050F75E4F69A876336D067BE9F7467A8928E7579337687743E5EEF7915526A77D14FFC87086C5351FCD6A3958F58B665AB6B1B64E3EF124AFD369EF39E3747FD54F1E9BD4
	3AF1B8C6FFD07DDD556EDB115E6F21F43E5E5EEE5156E8CD40E33FF5B6B561C7D12D1C97E4D2E6A5B7BE25D6DF8CB2993312FBFE4A59A08BE4F2E6A5D7B12E155B8CB25BE7A5D7B225D65FBDAF4FD0B5EFB6F2DF1B72FB2233A053BC039C5FAF16DB92772487AD09FB5215E8A2DFFBB4116F0EF0A25F1D61C43EDF0FA57295859279FEB71E48F7EABC116F10B3112F4A194877D8EFA25F933DDABE7E7FCA3F9FDE8B0D4C7FA02541GF9FB5A5FE4BE7E62D7AE5D3B4C393E08943610DDD20C90217F256995105F27
	4A37300FC27961B346629F5DDFDCC4CADA0801FFA4EBA1A574CBD78BA9E59F42DAC831AC79D98F25DA880E114FC8913C15E8C19FAB13FD6C22350A5D34D659A423CCCA597539CC7660B5F8953DCA1349FEF61D4D9AD56621AAF673AD1357B5D8E9280A5EF34BE41726D3199878FD6D0D68B567D87A092FC2011D7C5E723D6B5A5FB6EE1FFFB0696FE4F367AB674B79713FED1CE479DE3DA04F5034G6B7B75917A3B55E43F5F320E8ADE2FACF8C76AFDAE7ADB2E0628AC466A79DB22347FA072A8C4CECB0DD23D2F
	E9E47E9FD0CB8788DFA05636AA94GGCCBDGGD0CB818294G94G88G88GD4DAD2B0DFA05636AA94GGCCBDGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGE494GGGG
**end of data**/
}
	/**
	 * @return
	 */
	public ButtonGroup getButtonGroup()
	{
		if( buttonGroup == null)
			buttonGroup = new javax.swing.ButtonGroup();
		return buttonGroup;
	}
/**
 * Return the CancelButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getCancelButton() {
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
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getDateSelectionPanel() {
	if (ivjDateSelectionPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitle("Reset Peaks To:");
			ivjDateSelectionPanel = new javax.swing.JPanel();
			ivjDateSelectionPanel.setName("DateSelectionPanel");
			ivjDateSelectionPanel.setBorder(ivjLocalBorder);
			ivjDateSelectionPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsTodayRadioButton = new java.awt.GridBagConstraints();
			constraintsTodayRadioButton.gridx = 0; constraintsTodayRadioButton.gridy = 0;
			constraintsTodayRadioButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTodayRadioButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTodayRadioButton.weightx = 1.0;
			constraintsTodayRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getDateSelectionPanel().add(getTodayRadioButton(), constraintsTodayRadioButton);

			java.awt.GridBagConstraints constraintsSelectedDateRadioButton = new java.awt.GridBagConstraints();
			constraintsSelectedDateRadioButton.gridx = 0; constraintsSelectedDateRadioButton.gridy = 3;
			constraintsSelectedDateRadioButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSelectedDateRadioButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsSelectedDateRadioButton.weightx = 1.0;
			constraintsSelectedDateRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getDateSelectionPanel().add(getSelectedDateRadioButton(), constraintsSelectedDateRadioButton);

			java.awt.GridBagConstraints constraintsSelectedDateComboBox = new java.awt.GridBagConstraints();
			constraintsSelectedDateComboBox.gridx = 1; constraintsSelectedDateComboBox.gridy = 3;
			constraintsSelectedDateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSelectedDateComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsSelectedDateComboBox.weightx = 1.0;
			constraintsSelectedDateComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getDateSelectionPanel().add(getSelectedDateComboBox(), constraintsSelectedDateComboBox);

			java.awt.GridBagConstraints constraintsFirstDateOfMonthRadioButton = new java.awt.GridBagConstraints();
			constraintsFirstDateOfMonthRadioButton.gridx = 0; constraintsFirstDateOfMonthRadioButton.gridy = 1;
			constraintsFirstDateOfMonthRadioButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFirstDateOfMonthRadioButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsFirstDateOfMonthRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getDateSelectionPanel().add(getFirstDateOfMonthRadioButton(), constraintsFirstDateOfMonthRadioButton);

			java.awt.GridBagConstraints constraintsFirstDateOfYearRadioButton = new java.awt.GridBagConstraints();
			constraintsFirstDateOfYearRadioButton.gridx = 0; constraintsFirstDateOfYearRadioButton.gridy = 2;
			constraintsFirstDateOfYearRadioButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFirstDateOfYearRadioButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsFirstDateOfYearRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getDateSelectionPanel().add(getFirstDateOfYearRadioButton(), constraintsFirstDateOfYearRadioButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDateSelectionPanel;
}
/**
 * Return the FirstDateOfMonthRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getFirstDateOfMonthRadioButton() {
	if (ivjFirstDateOfMonthRadioButton == null) {
		try {
			ivjFirstDateOfMonthRadioButton = new javax.swing.JRadioButton();
			ivjFirstDateOfMonthRadioButton.setName("FirstDateOfMonthRadioButton");
			ivjFirstDateOfMonthRadioButton.setText("First Date Of Month");
			// user code begin {1}
			ivjFirstDateOfMonthRadioButton.setActionCommand(FIRST_OF_MONTH);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFirstDateOfMonthRadioButton;
}
/**
 * Return the FirstDateOfYearRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getFirstDateOfYearRadioButton() {
	if (ivjFirstDateOfYearRadioButton == null) {
		try {
			ivjFirstDateOfYearRadioButton = new javax.swing.JRadioButton();
			ivjFirstDateOfYearRadioButton.setName("FirstDateOfYearRadioButton");
			ivjFirstDateOfYearRadioButton.setText("First Date of Year");
			// user code begin {1}
			ivjFirstDateOfYearRadioButton.setActionCommand(FIRST_OF_YEAR);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFirstDateOfYearRadioButton;
}
	/**
	 * @return
	 */
	public GraphDataSeries[] getGDSArray()
	{
		return gds;
	}
/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getOkResetButtonPanel() {
	if (ivjOkResetButtonPanel == null) {
		try {
			ivjOkResetButtonPanel = new javax.swing.JPanel();
			ivjOkResetButtonPanel.setName("OkResetButtonPanel");
			ivjOkResetButtonPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsResetPeaksButton = new java.awt.GridBagConstraints();
			constraintsResetPeaksButton.gridx = 0; constraintsResetPeaksButton.gridy = 0;
			constraintsResetPeaksButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getOkResetButtonPanel().add(getResetPeaksButton(), constraintsResetPeaksButton);

			java.awt.GridBagConstraints constraintsCancelButton = new java.awt.GridBagConstraints();
			constraintsCancelButton.gridx = 1; constraintsCancelButton.gridy = 0;
			constraintsCancelButton.insets = new java.awt.Insets(10, 20, 10, 20);
			getOkResetButtonPanel().add(getCancelButton(), constraintsCancelButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOkResetButtonPanel;
}
/**
 * Return the Panel1 property value.
 * @return java.awt.Panel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Panel getPanel1() {
	if (ivjPanel1 == null) {
		try {
			ivjPanel1 = new java.awt.Panel();
			ivjPanel1.setName("Panel1");
			ivjPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsDateSelectionPanel = new java.awt.GridBagConstraints();
			constraintsDateSelectionPanel.gridx = 0; constraintsDateSelectionPanel.gridy = 0;
			constraintsDateSelectionPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsDateSelectionPanel.weightx = 1.0;
			constraintsDateSelectionPanel.weighty = 1.0;
			constraintsDateSelectionPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getPanel1().add(getDateSelectionPanel(), constraintsDateSelectionPanel);

			java.awt.GridBagConstraints constraintsOkResetButtonPanel = new java.awt.GridBagConstraints();
			constraintsOkResetButtonPanel.gridx = 0; constraintsOkResetButtonPanel.gridy = 1;
			constraintsOkResetButtonPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsOkResetButtonPanel.weightx = 1.0;
			constraintsOkResetButtonPanel.weighty = 1.0;
			constraintsOkResetButtonPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getPanel1().add(getOkResetButtonPanel(), constraintsOkResetButtonPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPanel1;
}
	/**
	 * @return
	 */
	public Date getResetPeakDate()
	{
		return resetPeakDate;
	}
/**
 * Return the ResetPeaksButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getResetPeaksButton() {
	if (ivjResetPeaksButton == null) {
		try {
			ivjResetPeaksButton = new javax.swing.JButton();
			ivjResetPeaksButton.setName("ResetPeaksButton");
			ivjResetPeaksButton.setText("Reset Peaks");
			// user code begin {1}
			ivjResetPeaksButton.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjResetPeaksButton;
}
/**
 * Return the SelectedDateComboBox property value.
 * @return com.cannontech.common.gui.util.DateComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.DateComboBox getSelectedDateComboBox() {
	if (ivjSelectedDateComboBox == null) {
		try {
			ivjSelectedDateComboBox = new com.cannontech.common.gui.util.DateComboBox();
			ivjSelectedDateComboBox.setName("SelectedDateComboBox");
			ivjSelectedDateComboBox.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSelectedDateComboBox;
}
/**
 * Return the SelectedDateRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getSelectedDateRadioButton() {
	if (ivjSelectedDateRadioButton == null) {
		try {
			ivjSelectedDateRadioButton = new javax.swing.JRadioButton();
			ivjSelectedDateRadioButton.setName("SelectedDateRadioButton");
			ivjSelectedDateRadioButton.setText("Selected Date");
			// user code begin {1}
			ivjSelectedDateRadioButton.setActionCommand(OTHER_DATE);
			ivjSelectedDateRadioButton.addItemListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSelectedDateRadioButton;
}
/**
 * Return the TodayRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getTodayRadioButton() {
	if (ivjTodayRadioButton == null) {
		try {
			ivjTodayRadioButton = new javax.swing.JRadioButton();
			ivjTodayRadioButton.setName("TodayRadioButton");
			ivjTodayRadioButton.setSelected(true);
			ivjTodayRadioButton.setText("Today");
			// user code begin {1}
			ivjTodayRadioButton.setActionCommand(TODAY);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTodayRadioButton;
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
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		getButtonGroup().add(getTodayRadioButton());
		getButtonGroup().add(getFirstDateOfMonthRadioButton());
		getButtonGroup().add(getFirstDateOfYearRadioButton());
		getButtonGroup().add(getSelectedDateRadioButton());

		// user code end
		setName("ResetPeaksPanel");
		setLayout(new java.awt.GridBagLayout());
		setComponentOrientation(java.awt.ComponentOrientation.UNKNOWN);
		setSize(349, 242);

		java.awt.GridBagConstraints constraintsPanel1 = new java.awt.GridBagConstraints();
		constraintsPanel1.gridx = 1; constraintsPanel1.gridy = 1;
		constraintsPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsPanel1.weightx = 1.0;
		constraintsPanel1.weighty = 1.0;
		constraintsPanel1.ipadx = 5;
		constraintsPanel1.insets = new java.awt.Insets(5, 5, 5, 5);
		add(getPanel1(), constraintsPanel1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
	/**
	 * @return
	 */
	public boolean isUpdated()
	{
		return updated;
	}
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent event)
	{
		if( event.getSource() == getSelectedDateRadioButton())
		{
			getSelectedDateComboBox().setEnabled(getSelectedDateRadioButton().isSelected());
		}
		
	}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ResetPeaksPanel aResetPeaksPanel;
		aResetPeaksPanel = new ResetPeaksPanel();
		frame.setContentPane(aResetPeaksPanel);
		frame.setSize(aResetPeaksPanel.getSize());
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
	 * @param serieses
	 */
	public void setGDSArray(GraphDataSeries[] gds_)
	{
		gds = gds_;
	}
	/**
	 * @param date
	 */
	public void setResetPeakDate(Date date)
	{
		resetPeakDate = date;
	}
	/**
	 * @param b
	 */
	public void setUpdated(boolean b)
	{
		updated = b;
	}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(java.lang.Object o) {}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/11/2002 4:52:55 PM)
	 * @param parent javax.swing.JFrame
	 */
	public boolean showResetDialog(JFrame parent)
	{
		dialog = new javax.swing.JDialog(parent);
		dialog.setTitle("Trending Properties Advanced Options");
		dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setModal(true);
		dialog.setContentPane(getPanel1());
		dialog.setModal(true);	
		dialog.getContentPane().add(this);
		dialog.setSize(379, 283);
				
		dialog.addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				exit();
			};
		});


		javax.swing.KeyStroke ks = javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0, true);
		getRootPane().getInputMap().put(ks, "CloseAction");
		getRootPane().getActionMap().put("CloseAction", new javax.swing.AbstractAction()
		{
			public void actionPerformed(java.awt.event.ActionEvent ae)
			{
				dialog.dispose();
				setVisible(false);
			}
		});
		
		//set the app to start as close to the center as you can....
		//  only works with small gui interfaces.
		java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setLocation((int)(d.width * .3),(int)(d.height * .2));
		dialog.setModal(true);
		dialog.show();
		return updated;
		//this.toFront();
	}
}
