package com.cannontech.dbeditor.wizard.changetype.device;
import java.awt.Dimension;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.device.CarrierBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.device.IDLCBase;
import com.cannontech.database.data.device.IDeviceMeterGroup;
import com.cannontech.database.data.device.RemoteBase;
import com.cannontech.database.data.device.TwoWayDevice;
import com.cannontech.database.data.device.lm.IGroupRoute;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.ICapBankController;

public class DeviceChngTypesPanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.ListSelectionListener {
	
	private DeviceBase currentDevice = null;	
	private DBPersistent extraObj = null;

	private static final int DEVICE_CATEGORIES[] =
   {
      DeviceClasses.CARRIER,
		DeviceClasses.TRANSMITTER,
		DeviceClasses.METER,
		DeviceClasses.RTU,
		DeviceClasses.GROUP
   };
	private static final String DEVICE_TYPES[][] = 
   { { //MCTs
			PAOGroups.STRING_MCT_470[0],
			PAOGroups.STRING_MCT_410IL[0],
			PAOGroups.STRING_MCT_410CL[0],
			PAOGroups.STRING_MCT_370[0],
			PAOGroups.STRING_MCT_360[0],
			PAOGroups.STRING_MCT_318L[0],
			PAOGroups.STRING_MCT_318[0],
         PAOGroups.STRING_MCT_310CT[0],
			PAOGroups.STRING_MCT_310ID[0],
			PAOGroups.STRING_MCT_310IDL[0],
			PAOGroups.STRING_MCT_310IL[0],
         PAOGroups.STRING_MCT_310IM[0],
			PAOGroups.STRING_MCT_310[0],
			PAOGroups.STRING_MCT_250[0],
			PAOGroups.STRING_MCT_248[0],
			PAOGroups.STRING_MCT_240[0],
			PAOGroups.STRING_MCT_213[0],
			PAOGroups.STRING_MCT_210[0],
			PAOGroups.STRING_LMT_2[0],
			PAOGroups.STRING_DCT_501[0] 
      },
      { //Signal Transmitters									
		   PAOGroups.STRING_CCU_710[0],
			PAOGroups.STRING_CCU_711[0],
			PAOGroups.STRING_LCU_415[0],
			PAOGroups.STRING_LCU_LG[0],
			PAOGroups.STRING_TCU_5000[0],
			PAOGroups.STRING_TCU_5500[0] 
      },
      { //Electronic Meters
		   PAOGroups.STRING_ALPHA_POWERPLUS[0],
			PAOGroups.STRING_ALPHA_A1[0],
			PAOGroups.STRING_DR_87[0],
			PAOGroups.STRING_FULCRUM[0],
			PAOGroups.STRING_LANDISGYR_RS4[0],
			PAOGroups.STRING_QUANTUM[0],
			PAOGroups.STRING_TRANSDATA_MARKV[0], 
			PAOGroups.STRING_VECTRON[0]  
      }, 
      { //RTUs
			PAOGroups.STRING_RTU_ILEX[0],
			PAOGroups.STRING_RTU_WELCO[0] 
      },
      { //LMGroups
            PAOGroups.STRING_EMETCON_GROUP[0],
            PAOGroups.STRING_EXPRESSCOMM_GROUP[0],
            PAOGroups.STRING_VERSACOM_GROUP[0]          
      }
   };

	private static final String[] REPEATERS_LIST = 
	{ 
		PAOGroups.STRING_REPEATER[1], PAOGroups.STRING_REPEATER_800[0] 
	};
	
	private static final String[] CBC_LIST =
	{
		PAOGroups.STRING_CAP_BANK_CONTROLLER[0], PAOGroups.STRING_CBC_7010[0],
		PAOGroups.STRING_CBC_EXPRESSCOM[0]	
	};

	private javax.swing.JLabel ivjJLabelDevice = null;
	private javax.swing.JList ivjJListDevices = null;
	private javax.swing.JPanel ivjJPanelNotes = null;
	private javax.swing.JScrollPane ivjJScrollPaneDevList = null;
	private javax.swing.JScrollPane ivjJScrollPaneNotes = null;
	private javax.swing.JTextPane ivjJTextPaneNotes = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DeviceChngTypesPanel() {
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
	D0CB838494G88G88G4BF5EBAEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBB8BF0D357156E1634E44AA4F412F43CA91D906224CEC233E4CBD3CFCBB3598C5D12C0A7EE176C32AD5BB1DBCFC2F6C817B634E53BE4F3AD5B98889FE31B824184A706ACA42EB089C99DE30CE57DACA35B4292460083FE121E24E7BDCB4A53B396893B5DF36EFD7769D9166CC04BFA66705EBD775E733B671EFB4E5503549D18DBD9E62AA3242C12E8FFFFACA3246283C2BE7D0D70F50EEB7950D9C58C
	FFEFG6CA4CD67AB605985FD4118332A1A9C5D39895AC3B8F644D9359B5EEB49711D794D701203CF827A3E1FF7E06D54FC725AB81FAA5AFEAA3C951E5B810681B7832078C3643FAA3CC74397C2FB3B760ED0E6A2A42F9547FC605DE340573532F13276BCF88247EC8857EB7DCBG0FE3CAG56E9E33642184DD43ADBDADB34F1C3DFAEA46B1EEEB4EDC3DE867A9C6493EC6EEC6D59C09E2606A8A76E932761596609065B8371D1F3C0084562B1D58CC44CC13F98145438E29E17BE911420B99091E2E1D13D1A904D
	C1710A9490DBDBED74E90D4442BEC0A7BDC2CC1401986AB0645DC469038E3A3CBF5A562649F3E73410145C777B1C725A5A181C1136343C3EEB210DFBCEA26DC3112D5A3CC268AF81E0BCC7A31C67900CDBC947AEBA03E36F047779G0BF57C677B915F506E2CEA817034F399CF7EB717F17053EF1312DB4265BBF24898E957EC5A1E16F1C9AC34F1AF75112D6D384E4B20EFADC045A9E755E100AE008840BE6A1BBFE8FB9B1E963FEA0D0F0E46E316C422D38EAABD7120A843FBEBAB74E838FEB190D70204B07F93
	3CCDD9646110F7EA226FA1B05D4E916EAF737B9FA14547A36515D9F4DB786CA1531D9A0D42D3E95D18DE0527D02FA500AF8558GD057612CBA8E606B60FA6DBC795EF4FA3940471418A0FB477C89C1D1BB47A4B9A8AA1AFE6FC42652AF9B30F99FDE77BCE61742770CF6D979CD2729AA0BDDD696B619929D4C9EB3BB52F6E13C151C3C8BBBA62F8742328E7443E66A070B603DCC73DD06EF2578EACB9A4FFC7FC96A4F58EE003E99966E37035B398E6E0AF9247AD8D7D69D721FBCE9DAEDE172104E348E0C67EFA9
	4F26CE4633CC1705F8915FE240B3D9EE6D677B5086FD05G4CC7E6D8B0FEED35024F83F4G8C8224G6E84A834F2BF99BAF974B37A3F4760A3EC6E2E765F43D3CC290A600AA5D5A196908784C5927C3218A45A18575B51867E89739A693E8CG8F27989395C11562B120AE0C0A6010C9B62629FDA70ED1C45A6B13C6C5E0B01AA01C6E7176DAF84AC2D2BD1B888A2A189E417A5B5AF1CD2E8883C09188407BFAAB5AABC5E36292F8AF35F2DBBD5A0938B69B6C9F9B374B375BEB60F99AB608A8C3101582A80620DC
	657C9FBD05B1C93CA246D41B1890E3C1F8C9EAFDC527B6276D920A6BB2BFCE7138572CA0F8D247AFBE3513610106514A3CFF996D97D4D5117CE32A6895D5D50A05537317D3D9129336F112E831420A7E91813D72GE66B7AFD54D1EB504F9D0FC74792BEDCB94DCE3752B8DEG63975978197BC1BF1E41A5B66E6F3F98647EEE5B768D52742CA32B3FA3D458191F575BB377EC8725196631644C60B4718279DFCFFC199C23D6CD4B237C8669A3B03B6F33235DED206FB0C01E1D5BBD3F23016D37CEA1A9729D5627
	400A09413417B1B9DF6EA73A4C9B3D69775B7B27137F4F814623702672E036384D12FB8F5DE5493E073EEA493E076E35CC3DC79E34E45BA32CEF01854F6078F84C30673EE5417D9F0847E2A2DD94FD8AE1B95BAFFC2807941354F3D290D2B22F7409C84893F65CBB375398138F6F736DFC2F0CB7723DF27013874993175FB0E5BB77736F6FB0D5B818FF6FF124778A136931CE74A9D196C741D05678D8CCE578C5B4FEE9F8ADFEBEE24DBEF6219577DBD43C2A0DAB32EEA498A765B1D157ED0170AA5AD811C64195
	B1A0E9F9735C08B1EFEE8179B047CE41F396G1B5B77B61E89ACA96D0508EE898695B14953D236B7FC8E142DG66948194BB785EB8EE43FE9E1334CDE10D4BB2DB82FDED5E30D763DA0A9F0F09F064E050BA2D6BF358DE1351B72069467A1B6D281BEC08F72B9D285B83FA2E0C67A143FD0F62DE82836D2B64F9CACF35B15FBF3876632C6B085064E46B57614C4C5FEEF5A25DE140A71CDC77076D46F8ECC9A66381099EFD5A1A7CF5AB66F359EA04GBD384D61B149BC26CAF8D889AA6C194418EA2C8536C84893
	E94DF0727C7BA7D11A77C0FBB9409ADD168B0E497EF45931851EA1C9C9C25E9894D35C1E22836D0267300666F96E3BC6BBBDD172071CF62A68527C3DAB53CE4FF7219C47815F5545E5ABF16CB45889F2F2C8BB42300ADC1E871C654C3F3576C34ECA6E5F0447369DD2E61EB85415BED392DD134F971603754AE82F99DDAC872B527197A8FE0E814FF23392EADFECAF043EBDAE1EEF7C0B8B753A866DD9AE2E57292EED863DBCF1A9266ABA2D6942BD9AD72430946338DFD29CD4A4B4E831B37BAD3A46F3006682G
	06F30DA0EEB934573878996B2372D6F7BB2B1A813A3AB9FE18620721BD8320C047BF974DF696605CEC313DAB873EBA871E71F8A5AB0F4AACB8BE67FA4EEDE633FFF5617A247476F334FDD51BD7CBCECA9B29AFDF0D0F01716BCF237DFC12B166AD819D5682D41CE63A303D7968B8461490A430F064F893E2002C27F307C7785CA034D3G79GC5GABG98CF0B9363D537B720C01C438D8B2153ADA5D5AE530F1338A65DEE41AF4AAC2AB27CE8BF726C26DB3CB72EC200E3B17A8E59A8EF071B65EFB2BC73G0A5D
	B866D736BA4318FCED4CA29CC3E9FC275AC8E3CF8F6BEF032727074779E343D8DF50F8B4A2DC914C42385A5A6AD424E02790F6B38B5238738975C934F32407471DC289F51A8B6DA200629EFEC6265E62F57F7C876691F71EB36B5DC4094FECDA5D235D437426E346CA287B994F52914E338B7A73FA91D793B56A0CB6FC5EC408991E2C5FB6219FE3CCA3B176DF19500F727EE0427C42897DE8237D5AFC265B52C3DC378DCB979079DB6F673CF7D9226934C6FB36A49AC9F5DBF8A333FB583E5FE87714B9B98ECE57
	5AEA70A0E4C76D30D466E75A41BEEE7BFBACE87BA1E82BG337A78FA3E945A131DAE75BCC66F08FD1336778A7A781A7D0F43F81F34E8075974EA8E3DD676B1FDB6742557EC9856CC63FFA487FFF608D17E03B51CFF1B076B639FC1FDD4E84F82A87070187527C9B1FEBAE0313C1E52DB64492C3F8FD17C92CFE67D7D08446B6F655057F20647785253284F9CE8978194832C8658546F2CBA584FE3FE349B2FC5272C3DFD71049B92E459D096B27DDE983931FB163F2B626B3570BB8F1122AF7C3AAC57FA357433F5
	9A541E4531C60AE77A0D7720FEB3212FGE089C08940BA002DDE2E5FB73B1B18FE762024DAD4D588C4E8C641159B104471B49A83A7532DD81ECA3786EF2E7379A91A9F9F77E65E637C83455B3C1977B877DB78BD0E8F7A0A3C5CF74CF6A37FFA9F63572CBDED3E497C19CDD675E28C9804FE95E096C08140E29F37474B5D535E4959D3D35C49FDD4E3142B94682EB7483275ECAE3B7CA3755D063319F6A92178663319F6090CF03BF4C0DF41D9EE17B7C788616775BB067705524D2FC7FFF8D3F93075F971F46EFA
	F45D696C75686FCEE72FC709FB6AFAF413BBF7BD3A43ED2CC719EFB5FA5037B68DC07CG68829882C88CF05F7A1A7BCD66DBD976199D8AD4EB1C5E50291CFFDECFEEDD3F58B37D3D5E6C9E2C877CF1D50D0FA2FD0E7FCA8F2E179ACFD024D63377200DA0FD0D28ECA82F59FBB01E4BE20822D93E7A3C4C7355D9835C6F1E69441AF5255E7EE75ADE27379F365C08EFA03D4F6EFF0BEF0847E73DC7E23A5B6979DAF78E56789C4F5FCB77A22E855ADDBA4E759A37116F9CDF7F97FAF32F675A5E6C3E7B1B5E6C3E3B
	21F76A7B480A3EDC3E4D64D369B9DDG329569B2BB6BF8ED32D247093B389EE4102F654A91DCDB0F5EFE0E36E53D9D9B49358EB867AF93EB3E1813471FF3AFB8B1563C63493DDE6FFA265FFFA7BC34C607FD03BF7BB1DC0707560921D0D25477395DD32D63DCC9CFBCA961254262C134FF5918ED9A5F2D541E8833E1CE81C02356AE027733F0F0C4CD3CD6D971A92BD520FCE125CA12559BDB2C465CF86BF9E79556184D67F98DB4E447B58E45E3EA5EF99C2F50FDE003F70FBE66BF776298950C3278BC7EBE9CBE
	87CD427498FEEDBABD0C30698264E197189EF86E5543BB4F01982FAF55209D7DF105BABD2B836755701A68B00C6FG3886B07B02F62FCD47A46A0CF5D3A174958314EAE3182C57F6CDA5EBE5064DB7405C6A0BE91B9F0477FF07675EB2AE6B4AFEDCDFC376CF0BC1D6476D0EE65602D853B7838D9B4081BA9F6CC1785F5AD359F04C5EFF03DBC24BA1D7ED65B9E47D4377915BBF7D3A0C5BB2D31F210BCC77799753B9FF4B033C16D9FA843F8F48D6F3508218667C96AB3F37AA859ACC169D17083ED6AB76623997
	93479BF899EAF600A39DFEB3A889F2BC7CE6D3C3F3D3433163C7386CF3470D77D2ADDB5E4BF9AFD5BF2465FD5A33605EC60D46B3970DB42A6F5EEFAA4BC1A3224DD507527ACF36B17886556B714B784EF23027EBF88ED688F3CB06F81D763DAD4D64B35E837AA4D596BB1997D3DB565472FAF04FC73CFEFA209679F8209D81487B0873F94E3C0FCC2A6F99B12D3CE7B43FD247EB6DFC1D6662BA24D98C6D55G6452757A5A2AE15A24747B223A4C17B87D47683D1A875A91003CCB3C66FAC6403D158F6D05GABG
	5682D4DCF6D65583B48358G868192GB38166GACGD88C309CE0B540064B7CCCDDB788EEC3860491ADD33753EFB1E8D685C86D3203A6D60A406E6B58796130CED53FD48FCFBC278E8EF3795F8F50339E5A3EE1AE036F52BBD34860D16289D1D12FB2AC3D6956724566A01A1051387B5221DCB4A0BB6C06656D730F088155A704F59D0AEB262BAF558CBD583C1FCDBB6F750CB61B373B76FA6A5959401B55B02FD1DFAB984E2C579BA9FE41F0E63DBE3F16576B0B202FC160F945D76BEE6C3E20E5973D9396B279
	DBA8FE3610497F69BA4EFF8E74913FFEA79B1D0AFF501F2B36FB1B5E59267C19355DF10A1F694F2C6D96C5F9DC49073E8DFE6EE7BF97F267A37FA6CC1F0F3CA4E42F877EC318DC8F7C17103D9E28709B6B81065B6C47F894063DB6280F5B2E634E6B38DA7FE68DF7CE8A2A919D3F573FC543F7094810638F78D12EF10A9B54F0878D38739A6E089FF381D0B992D778DC331F66AB12ACEB799A9DA345A045526D71C7AA2314908CF23769B8141B65409FD9F98EBC9C60F971CC9B62E6C0FBEE002FCFEBG57DC4F39
	59D65768DA82F5047D9689E71E74897D59488B83E3617DF42D1F285DC67BE5A986F5A07D6AC267EF26E742C26033A440FD318149E97B7208617DEFEBEFFE1D3F747F01C77B2E34CECE43FBF15D4D677D42CD654156B49A65BE35DCDF53E699FD1684F93B15366B757613D9FFFF198A105675697E536B66F1BD75967B4DC535721C378D6473GA4GE683AC8872DA629E9B6E0724287D2E0C97F050E635C7110D679B457AF8FBAD6EDB9553A942F335B9741A7D3008F37EAA5AB005DCF5A2339323D5694077EE9FC4
	D3BDDB87DE34CE0A4A46BAA98173306E4D97793715F76B3970FC117F2E71E3EB666F9AA81819705FA5AAEC193FEB14CD687F4016793B46B7A67C2E9131E57E2E4167B3FD1E31E1AEECC85F4D183CFB0D36154DDED72F53ED3F6C6A75597B87AC6E4BDE3B5BEE75397AFA357508E27C340A78BDA24F5347C5E30DD1FF6017E5DC76CC7B16EA762D8E25F3ECAEEBB99979F4C29E1B2EB1788AE8F966BC197F36E08B71BDF14F04BC6E9A60E7072676F916FB7DFD887D21G46BE81D08A308120BA6C2CBA8C5086608B
	6B3FE70B07C96E7C0EA55C6994134975751C77638920BB83A0BFBCD1CE96979E237EB39F7AF00D996E3B943798F01FAB6367490FC239F301952169F301D5216C394073214939401AD076DC60D721493940EFB53AE8937C36040FFDB9047B197DEE5BA34404B03D2BE27ABDCE57358474DB9F66676B7B9E9E9F37C6B8EE788C621A21ED0B709AC8F88BF3A56DDC25E985435727362571BC2D50764333B215D188C443CAFCAC9664F165C5AA47A0508EA2CF9A2B5C9E9E2BE6832EGE0B1C0A9C00544E34F7899F64F
	A329E8E2C66B36B33C66BAA8711AF8F71468B23C6D415AEAC2CDEC0D4BF165703B4D471BB4B822793D6261B125C562B165A1CFE6CC39354C9893D6FA260EA95B4FE446147804799DE7260EA9771FC1DCC8101369EFB646BDC61FCF002CB3242977A2DB575BBC5C67D186063B4343FD9E75E27FC7615901F3F0A04EFF11D42AE49F8BDF8DEAC4ECC0AB32CF6D9B70337B5624C2F668D6F551142F644AFB2D65BF3F2F5CC4AADCE4A3F3FB9729C05FF511CA6A5AAED28EEE1FCAE866C075CC88C0ED5B04FB0B6F5F7B
	7EF34BFEB610ACA79BDDE4AFDE09F9C505E6F5E2903F6B51BEE7F5104D897CC4843FBA83D6C97A28CC2AD7E551CD6AC27D7879D3D29503961EAFA6D5324B0D1C0E625D4051AEA199218EF542CDAAE27861AD29E15D32908B9FE5496389AAEF19E598BD0B9D79A0EFAD15972918F5AA355F5DF1680D6E5222F2E2F210ADF2DC88BA04G04B197592CC694B199094B41D462D487A534E99C2D0FB7EE65C0EE1F2EBEAAEB46282C8B9DCDFDED5636555B9CE02A14C20E5211F883E856C7842D91C181D62262D271C362
	38F2D06F7B5C6B6ADBBFF46E9EB211B2CD490F497F124AD4C2852994A1168C4515D11020A1C3825E2593B25F71D473DD8F7E6E82A5141E2A12834CE4B075E8D80C0F9E3504C210E16B8440FEC0FE3FCE9E4718B34654243E3C2B7B59D77C305EB6D2BE5A5CCC7FBDC67FFD977FBDC6B147A866583BE0C47D7F49E47F4B6CCFA5D16B4DAAF3925DFBF1194D8ED8A975A7AFBEB56F27EDEB5700F68ED2C9BF1482E3D727C7750E0D7AC5658B736F5A7F6A1DA5DF4235C820CBF03A9BC34228A4DF0597F66316C27CDE
	4616173E15DA18582CE36F56F6GEEF813CAAAE99E8452ED1F98314A6E5F7D796A5B5F9BB311CD8E64A4A7A202CB1352CDDE75534D0587242C126A74EA5039605A26G787D3EB4BE00F8566BCD8881747E4DC1C109D28AA85A6B974966109276D386AAB1DDF4DC843634055D8DEE3285020295EF5CD22ACAF627C9FB70F856BA5846A12FACDB36ACC17F341C66481F0C77066BEF294BF977D898E331EDD1AC731B38468B38FB56833EAE4EF3086479BF105C77BB5ED1C116D5C10EDAE2C13C6B693CCAD3CE6D6CBC
	FC8169A779982D13759D3D00F20F98667C9FD0CB878849312302B894GGACB6GGD0CB818294G94G88G88G4BF5EBAE49312302B894GGACB6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGF294GGGG
**end of data**/
}

/**
 * Return the JLabelDevice property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDevice() {
	if (ivjJLabelDevice == null) {
		try {
			ivjJLabelDevice = new javax.swing.JLabel();
			ivjJLabelDevice.setName("JLabelDevice");
			ivjJLabelDevice.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelDevice.setText("Select the new device type:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDevice;
}


/**
 * Return the JListDevices property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getJListDevices() {
	if (ivjJListDevices == null) {
		try {
			ivjJListDevices = new javax.swing.JList();
			ivjJListDevices.setName("JListDevices");
			ivjJListDevices.setBounds(0, 0, 342, 50);
			ivjJListDevices.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJListDevices;
}


/**
 * Return the JPanelNotes property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelNotes() {
	if (ivjJPanelNotes == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitle("Notes");
			ivjJPanelNotes = new javax.swing.JPanel();
			ivjJPanelNotes.setName("JPanelNotes");
			ivjJPanelNotes.setBorder(ivjLocalBorder);
			ivjJPanelNotes.setLayout(new java.awt.BorderLayout());
			getJPanelNotes().add(getJScrollPaneNotes(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelNotes;
}


/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneDevList() {
	if (ivjJScrollPaneDevList == null) {
		try {
			ivjJScrollPaneDevList = new javax.swing.JScrollPane();
			ivjJScrollPaneDevList.setName("JScrollPaneDevList");
			getJScrollPaneDevList().setViewportView(getJListDevices());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneDevList;
}

/**
 * Return the JScrollPaneNotes property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneNotes() {
	if (ivjJScrollPaneNotes == null) {
		try {
			ivjJScrollPaneNotes = new javax.swing.JScrollPane();
			ivjJScrollPaneNotes.setName("JScrollPaneNotes");
			getJScrollPaneNotes().setViewportView(getJTextPaneNotes());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneNotes;
}


/**
 * Return the JTextPaneNotes property value.
 * @return javax.swing.JTextPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextPane getJTextPaneNotes() {
	if (ivjJTextPaneNotes == null) {
		try {
			ivjJTextPaneNotes = new javax.swing.JTextPane();
			ivjJTextPaneNotes.setName("JTextPaneNotes");
			ivjJTextPaneNotes.setBackground(new java.awt.Color(204,204,204));
			ivjJTextPaneNotes.setBounds(0, 0, 183, 196);
			ivjJTextPaneNotes.setEditable(false);
			// user code begin {1}
			
			ivjJTextPaneNotes.setBackground( getJScrollPaneDevList().getBackground() );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextPaneNotes;
}


/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize()
{
	return getPreferredSize();
}


/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize()
{
	return new Dimension(350, 200);
}


/**
 * Insert the method's description here.
 * Creation date: (6/20/2001 11:42:42 AM)
 * @return int
 */
public int getSelectedDeviceType()
{
	int type = PAOGroups.getDeviceType( (String)getJListDevices().getSelectedValue() );
	return type;

}


/**
* This method was created in VisualAge.
* @return java.lang.Object
* @param val java.lang.Object
*/
public Object getValue(Object val)
{
	String type = (String) getJListDevices().getSelectedValue();


	if (val == null)
	{
		return new Integer( PAOGroups.getDeviceType(type) );
	}
	else
	{
		DBPersistent oldDevice = null;
		
		//get a deep copy of val
		try
		{
			oldDevice =
					(DBPersistent)CtiUtilities.copyObject( val );

			Transaction t =
				Transaction.createTransaction(
					Transaction.DELETE_PARTIAL,
					((DBPersistent) val));

			val = t.execute();
		}
		catch( Exception e )
		{
			CTILogger.error( e );
			CTILogger.info(
					"*** An exception occured when trying to change type of " +
					val + ", action aborted.");
			
			return val;
		}



		//create a brand new DeviceBase object
		val = DeviceFactory.createDevice( PAOGroups.getDeviceType(type) );
		
		//set all the device specific stuff here
		((DeviceBase) val).setDevice(
			((DeviceBase) oldDevice).getDevice() );
			
		((DeviceBase) val).setPAOName(
			((DeviceBase) oldDevice).getPAOName() );

		((DeviceBase) val).setDisableFlag(
			((DeviceBase) oldDevice).getPAODisableFlag() );

		((DeviceBase) val).setPAOStatistics(
			((DeviceBase) oldDevice).getPAOStatistics() );

		//remove then add the new elements for PAOExclusion
		((DeviceBase) val).getPAOExclusionVector().removeAllElements();
		((DeviceBase) val).getPAOExclusionVector().addAll(
			((DeviceBase) oldDevice).getPAOExclusionVector() );



		if( val instanceof CarrierBase
			 && oldDevice instanceof CarrierBase )
		{
			((CarrierBase) val).getDeviceCarrierSettings().setAddress(
				((CarrierBase) oldDevice).getDeviceCarrierSettings().getAddress() );

			((CarrierBase) val).getDeviceRoutes().setRouteID(
				((CarrierBase) oldDevice).getDeviceRoutes().getRouteID() );

		}
		else if( val instanceof IGroupRoute
		          && oldDevice instanceof IGroupRoute )
		{
			 ((IGroupRoute) val).setRouteID(
					((IGroupRoute) oldDevice).getRouteID() );
		}
		else if( val instanceof IDLCBase
			 	  && oldDevice instanceof IDLCBase)
		{
			 ((IDLCBase) val).getDeviceIDLCRemote().setAddress(
					((IDLCBase) oldDevice).getDeviceIDLCRemote().getAddress() );
		}
			 
		if( val instanceof RemoteBase
	 	    && oldDevice instanceof RemoteBase )
		{
			 ((RemoteBase) val).getDeviceDirectCommSettings().setPortID(
					((RemoteBase) oldDevice).getDeviceDirectCommSettings().getPortID() );			 
		}

		if( val instanceof IDeviceMeterGroup
	 	    && oldDevice instanceof IDeviceMeterGroup )
		{
			 ((IDeviceMeterGroup) val).setDeviceMeterGroup(
					((IDeviceMeterGroup) oldDevice).getDeviceMeterGroup() );			 			
		}


		if( val instanceof TwoWayDevice
	 	    && oldDevice instanceof TwoWayDevice )
		{
			((TwoWayDevice) val).setDeviceScanRateVector(
					((TwoWayDevice) oldDevice).getDeviceScanRateVector() );
		}
		
		if( val instanceof CapBankController)
		{
			((CapBankController) val).setDeviceCBC(((CapBankController)oldDevice).getDeviceCBC());
		}
		
		//execute the actual command in the database 
		try
		{
			Transaction t2 =
				Transaction.createTransaction(
					Transaction.ADD_PARTIAL,
					((DBPersistent) val));

			val = t2.execute();

		}
		catch (TransactionException e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );

		}


		//execute the actual command in the database to create extra objects 
		try
		{
			if( extraObj != null )  {
				Transaction.createTransaction(
					Transaction.INSERT,
					((DBPersistent) extraObj)).execute();
			
				SmartMultiDBPersistent multi = new SmartMultiDBPersistent();
				multi.addDBPersistent( (DBPersistent)val );
				multi.addDBPersistent( extraObj );
				multi.setOwnerDBPersistent( (DBPersistent)val );
				
				return multi;
			}

		}
		catch (TransactionException e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );

		}

		return val;
	}

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
		setName("DeviceNameAddressPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(355, 371);

		java.awt.GridBagConstraints constraintsJScrollPaneDevList = new java.awt.GridBagConstraints();
		constraintsJScrollPaneDevList.gridx = 1; constraintsJScrollPaneDevList.gridy = 2;
		constraintsJScrollPaneDevList.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneDevList.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollPaneDevList.weightx = 1.0;
		constraintsJScrollPaneDevList.weighty = 1.0;
		constraintsJScrollPaneDevList.ipadx = 170;
		constraintsJScrollPaneDevList.ipady = 309;
		constraintsJScrollPaneDevList.insets = new java.awt.Insets(2, 5, 9, 2);
		add(getJScrollPaneDevList(), constraintsJScrollPaneDevList);

		java.awt.GridBagConstraints constraintsJLabelDevice = new java.awt.GridBagConstraints();
		constraintsJLabelDevice.gridx = 1; constraintsJLabelDevice.gridy = 1;
		constraintsJLabelDevice.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelDevice.ipadx = 5;
		constraintsJLabelDevice.insets = new java.awt.Insets(9, 5, 1, 17);
		add(getJLabelDevice(), constraintsJLabelDevice);

		java.awt.GridBagConstraints constraintsJPanelNotes = new java.awt.GridBagConstraints();
		constraintsJPanelNotes.gridx = 2; constraintsJPanelNotes.gridy = 2;
		constraintsJPanelNotes.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelNotes.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelNotes.weightx = 1.0;
		constraintsJPanelNotes.weighty = 1.0;
		constraintsJPanelNotes.ipadx = 119;
		constraintsJPanelNotes.ipady = 140;
		constraintsJPanelNotes.insets = new java.awt.Insets(2, 2, 154, 3);
		add(getJPanelNotes(), constraintsJPanelNotes);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

      
	getJListDevices().addListSelectionListener( this );
		
	// user code end
}

public static boolean isDeviceTypeChangeable( String devType )
{
   if( PAOGroups.isStringDevice(devType, PAOGroups.STRING_REPEATER_800) 
       || PAOGroups.isStringDevice(devType, PAOGroups.STRING_REPEATER)
       || PAOGroups.isStringDevice(devType, PAOGroups.STRING_CBC_7010)
       || PAOGroups.isStringDevice(devType, PAOGroups.STRING_CBC_EXPRESSCOM)
       || PAOGroups.isStringDevice(devType, PAOGroups.STRING_CAP_BANK_CONTROLLER) )
   {
      return true;
   }
      
   for (int i = 0; i < DEVICE_CATEGORIES.length; i++)
   {
      //only let this device change its type if it is in the DEVICE_TYPES list
      for( int j = 0; j < DEVICE_TYPES[i].length; j++ )
      {
         if( devType.equalsIgnoreCase(DEVICE_TYPES[i][j]) )
         {
            return true;
         }
      }

   }


   return false;   
}


/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	return getJListDevices().getSelectedIndex() >= 0;
}


private void listDeviceType_Selection( javax.swing.event.ListSelectionEvent ev ) {
	
	//clear our text pane every time
	getJTextPaneNotes().setText("");
	
	Object selObj = getJListDevices().getSelectedValue();
	
	if( selObj != null ) {

		if( PAOGroups.getDeviceType(getCurrentDevice().getPAOType())
			 == PAOGroups.getDeviceType(selObj.toString()) ) {
			
			getJTextPaneNotes().setText(
				"Original device type and new device type are the same" );
		}
		else if( PAOGroups.getDeviceType(PAOGroups.STRING_MCT_310IL[0])
			 		 == PAOGroups.getDeviceType(selObj.toString()) ) {

			handleMCT_310IL();
		}
		else if( PAOGroups.getDeviceType(PAOGroups.STRING_MCT_410IL[0])
					 == PAOGroups.getDeviceType(selObj.toString())
				||
				PAOGroups.getDeviceType(PAOGroups.STRING_MCT_410CL[0])
					 == PAOGroups.getDeviceType(selObj.toString()) )
		{

			handleMCT_410IL();
		}
		else if( PAOGroups.getDeviceType(PAOGroups.STRING_MCT_470[0])
					 == PAOGroups.getDeviceType(selObj.toString()) ) {

			handleMCT_470();
		}
		else
			extraObj = null;
	

		fireInputUpdate();
	}
	
}

private void handleMCT_310IL()
{
	
	LitePoint[] ltPoints = PAOFuncs.getLitePointsForPAObject( 
					getCurrentDevice().getPAObjectID().intValue() );
	
	for( int i = 0; i < ltPoints.length; i++ ) {
		LitePoint point = ltPoints[i];	

		if( point.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT
			 && point.getPointOffset() == PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND ){

			getJTextPaneNotes().setText(
				"A Load Profile Demand point will NOT be " +
				"generated for this change type since one already exists." );
			
			extraObj = null;
			return;
		}
	}
	


	getJTextPaneNotes().setText(
		"A Load Profile Demand point will be " +
		"generated for this change type" );

	extraObj = 
         PointFactory.createDmdAccumPoint(
            "kW-LP",
            getCurrentDevice().getPAObjectID(),
            new Integer( PointFuncs.getMaxPointID() + 1 ),
            PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND,
            PointUnits.UOMID_KW,
            .01 );

}

private void handleMCT_410IL()
{
	
	LitePoint[] ltPoints = PAOFuncs.getLitePointsForPAObject( 
					getCurrentDevice().getPAObjectID().intValue() );
	
	for( int i = 0; i < ltPoints.length; i++ ) {
		LitePoint point = ltPoints[i];	

		if( point.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT
			 && point.getPointOffset() == PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND ){

			getJTextPaneNotes().setText(
				"A Load Profile Demand point will NOT be " +
				"generated for this change type since one already exists." );
			
			extraObj = null;
			return;
		}
	}
	
	getJTextPaneNotes().setText(
		"A Load Profile Demand point will be " +
		"generated for this change type" );

	extraObj = 
		 PointFactory.createDmdAccumPoint(
			"kW-LP",
			getCurrentDevice().getPAObjectID(),
			new Integer( PointFuncs.getMaxPointID() + 1 ),
			PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND,
			PointUnits.UOMID_KW,
			.1 );

}

private void handleMCT_470()
{
	LitePoint[] ltPoints = PAOFuncs.getLitePointsForPAObject( 
					getCurrentDevice().getPAObjectID().intValue() );
	/*
	for( int i = 0; i < ltPoints.length; i++ ) {
		LitePoint point = ltPoints[i];	

		if( point.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT
			 && point.getPointOffset() == PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND ){

			getJTextPaneNotes().setText(
				"A Load Profile Demand point will NOT be " +
				"generated for this change type since one already exists." );
			
			extraObj = null;
			return;
		}
	}
	
	getJTextPaneNotes().setText(
		"A Load Profile Demand point will be " +
		"generated for this change type" );

	extraObj = 
		 PointFactory.createDmdAccumPoint(
			"kW-LP",
			getCurrentDevice().getPAObjectID(),
			new Integer( PointFuncs.getMaxPointID() + 1 ),
			PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND,
			PointUnits.UOMID_KW,
			.1 );*/
			
	extraObj = null;
	return;

}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args)
{
	try
	{
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DeviceChngTypesPanel aDeviceTypesPanel;
		aDeviceTypesPanel = new DeviceChngTypesPanel();
		frame.getContentPane().add("Center", aDeviceTypesPanel);
		frame.setSize(aDeviceTypesPanel.getSize());
		frame.setVisible(true);
	}
	catch (Throwable exception)
	{
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/13/2001 12:06:30 PM)
 */
private void setList(int deviceClass, String type)
{
	//sets device types according to class --- repeaters are a unique case
	if( PAOGroups.isStringDevice(type, PAOGroups.STRING_REPEATER_800) 
       || PAOGroups.isStringDevice(type, PAOGroups.STRING_REPEATER)  )
	{
		getJListDevices().setListData( REPEATERS_LIST );
		getJListDevices().setSelectedIndex(0);
	}
	else if(PAOGroups.isStringDevice(type, PAOGroups.STRING_CBC_7010)
		|| PAOGroups.isStringDevice(type, PAOGroups.STRING_CBC_EXPRESSCOM)
		|| PAOGroups.isStringDevice(type, PAOGroups.STRING_CAP_BANK_CONTROLLER)  )
	{
		getJListDevices().setListData( CBC_LIST );
		getJListDevices().setSelectedIndex(0);
	}
	else
	{

		for (int i = 0; i < DEVICE_CATEGORIES.length; i++)
		{
			if( deviceClass == DEVICE_CATEGORIES[i] )
			{
            //only let this device change its type if it is in the DEVICE_TYPES list
            for( int j = 0; j < DEVICE_TYPES[i].length; j++ )
            {
               if( type.equalsIgnoreCase(DEVICE_TYPES[i][j]) )
               {
                   getJListDevices().setListData(DEVICE_TYPES[i]);
                   getJListDevices().setSelectedIndex(0);
                   break;
               }
            }

			}
		}

	}

}


/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) 
{
	
}


public void valueChanged( javax.swing.event.ListSelectionEvent ev )
{
	if( !ev.getValueIsAdjusting() )  // make sure we have the last event in a
	{												// sequence of events.			
	   if( ev.getSource() == getJListDevices() )
	      listDeviceType_Selection( ev );
	}
	
}


	/**
	 * Returns the currentDevice.
	 * @return DeviceBase
	 */
	public DeviceBase getCurrentDevice() {
		return currentDevice;
	}

	/**
	 * Sets the currentDevice.
	 * @param currentDevice The currentDevice to set
	 */
	public void setCurrentDevice(DeviceBase currentDevice) {
		this.currentDevice = currentDevice;
		
  		setList(
				DeviceClasses.getClass( 
						getCurrentDevice().getPAOClass() ),
			   		getCurrentDevice().getPAOType() );		
	}

}