package com.cannontech.dbeditor.editor.user;
/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.common.gui.tree.CTITreeModel;
import com.cannontech.common.gui.tree.CheckNode;
import com.cannontech.common.gui.tree.CheckNodeSelectionListener;
import com.cannontech.common.gui.tree.CheckRenderer;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.database.db.user.YukonUserRole;
import com.cannontech.user.UserUtils;


public class UserRolePanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.CaretListener 
{
	private javax.swing.JPanel ivjJPanelDefvalue = null;
	private javax.swing.JPanel ivjJPanelLoginDescription = null;
	private javax.swing.JPanel ivjJPanelValue = null;
	private javax.swing.JTextField ivjJTextFieldDefaultValue = null;
	private javax.swing.JTextField ivjJTextFieldValue = null;
	private javax.swing.JTree ivjJTreeRoles = null;
	private javax.swing.JScrollPane ivjJScrollJTree = null;
	private javax.swing.JScrollPane ivjJScrollPaneDescr = null;
	private javax.swing.JTextPane ivjJTextPaneDescription = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public UserRolePanel() {
	super();
	initialize();
}


/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTextFieldValue()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}


/**
 * connEtoC1:  (JTextFieldFirstName.caret.caretUpdate(javax.swing.event.CaretEvent) --> CustomerContactBasePanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		

		int selRow = -1;
		if( (selRow = getJTreeRoles().getLeadSelectionRow()) >= 0 )
		{		
			TreeNode node = 
				(TreeNode)getJTreeRoles().getPathForRow( selRow ).getLastPathComponent();
	
			if( node instanceof RoleNode )
			{
				LiteYukonRole ly =
					(LiteYukonRole)((RoleNode)node).getUserObject();
				

				((RoleNode)node).setUserValue(
					getJTextFieldValue().getText() );
			}
		}
				
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (JTextFieldLastName.caret.caretUpdate(javax.swing.event.CaretEvent) --> CustomerContactBasePanel.fireInputUpdate()V)
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
 * connEtoC3:  (JTextFieldPhone1.caret.caretUpdate(javax.swing.event.CaretEvent) --> CustomerContactBasePanel.fireInputUpdate()V)
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
	D0CB838494G88G88GB2F2DAAEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8BD4D4573536CD8DAF0F36B625DD364B2CD2E372CC434B733576BD5B5A9753A6AB265AD55B252D2FB1A9AA09A6B11198B44458F8984099FE8A7295790CFF0C040FE0E2A80DA0E098C0A5C28CB62CBE86668223435C714EC586C3425B7B1CFB4EDC069990D342DA1BFB4FBE1F3D77B97B6C33773E87C8C6E1E8FC0CA1031018F8227D3C93C3C857A5C25A5ED10EF25C6C279A0DC477F3GA00D145747
	41F3BE5419CBAD46D4B22B50846545D06EBCEEB1E643FBBA79F6E1C8B33C842063003A447A543BA626E39B62F40C347C1A1A8C4F5DGCBG9781D076F4A350CF9B4C5170A6A86F565E91E28C04EC24ED9EBB1F23432BDA3B4570DC0170E7ECD3BC18275517C13991A098A0744F2C6DA2F8A6D06E3EDAF5DAEB57794D79A46A57C78DA9C8CBB7BE071CF05637DAFB1A49C3F4A2E2490578EA4437DE6BFB2FC79EA86931BA1D32D315FA7ACBEC5D124D2E4ACA097698F4CBCAD55507703BC56E33BB4FDA5DD22B55A9
	B942421167974E1B68F8A09314CF2FC5DC4F851C27C8A81B20ECDE4B797EDDCA3546D7483579A47C5EFD81799ED94B788DDD6B65FBCD573A3856F5B87E070349DA3FF0284F819C1329346D1DA94BF4C9F6B0F18813671EC1AECF48BAAE4F4CE624379C4A91GBB847EFC8B62B356B79A0B816A57F3B97FF60C4B597149FF1360BB2F466E89A0E767FAA65F9D6B3DF236362D0B5BC7756DF58F6A4C92280BG0886480BE8B4D683F4826C27FA7E60501B702C6DD66B64018159D96BF21DF558147332CDF240FBD595
	54E838CBD20F2C5888E13A9CF93E588FBFC2BFA3466AB182136DA342F5AF6C52C3E4646DEB3171FEE4DB382A4890240DB1A7C2370691B8EFFF247A31985E31DF92G43BFC37166E73CF8365EF60A47F28D544DFB064FF347EE4ECBCB5CDC12DA5EECB07A61E576AFEB8C26E7B59E1E75724266F855CD1C63964077834CG188D3088E07533FC0E378ED5CCB6479650B345E9F5348DF63B2C0AFAF6506E30C90AB6579F8FCEB4577E007533CF399F1B1779957AB5DA7363A63F7332E6FE29A1CA1B0F648D5EF9E134
	678624DD3CE13CEE6C96EB381D6E151A8DEC8DE385BE0E6E15D69D1E2DEDB0ED0F65AE28FB940035FF0436AF5A68BB4EB20ABF3D51F71C3696BECEA35405813075F50F20CDCDFD8E76A8C0BDC0A7408840EC003067789A57F8CAEFF19F356A5617750D9B4E022764D1952B4569D62D4E9E6932D531DB3B9D121BE8EDD206F1DE3B47745BBB0C3A5783B45E171C12E2D56D3293C6378EC820CCEE56E67FF09A36D1A4DA5BEE9F100040000B70F18F8D274353E1F52B9F3AECD6D57236E075A706D1A7EED8AF4308B0
	G3C6FF88E674BF8816D5CF2F85F789C1F2B50CF91D773FC233163F9BEAF778F6F05E7B3A83764282A2A3777A09BD6E5186B68D397B210BE345F88507FC223B192C05D447768CB9DDC976BD37E1D942F327855C5040D1B18DE1197FDF57185CA2388700DG7DGC1G73DF646732FC21030F71BC236DCFBF11AF64EF32BD04320C5F0BC102865A3B2489761F719676E92CCF194D7F6ED1546125A0D784C05CCBFC8D568E1B19EE62594B357122820AA4593CAB425ABEFD934F9A690664D46BA5176434410BDB5305
	E7EEA6F87534D796BA748245235D2A83C5F28B7C4BB753989E4650EBBD2FFF0D56DBD5D531F78F2AD21B242AF6E71F37FF4CA7480BFB0CC9F4EB32269214B58FE42C842897325E75C93ACE5FDAE4795A202B9DF7122637BF2567F38734F73D44579F4CBFBCC3C4B911163D75F7B773355B2D351B0ACD3DB5DB7D60989A3E6B7B4F2171B815ABC614FFC74B8B041CD7DABA087F3D754F63C121B47E5972F1E0BA7069C8E09D3EE3443F8E5FB562DF07E70DCC2C2333C77C69A82B7B7E886EF5F948295379F9A3E88F
	FBE427D3229BCFF4A14C87FD299D6530BB6D6AC7F6DB1F243659EFCA00580C3ADBC0ED6EEAD82FCDAFF17BB8F4085B4743B79FA0CB7E6F30411FBFB57B3EBAC37DE626379D1B3D761171F45F273817A407B4G93D5A78FBAD54DB72476DC433BE3A96E7CA1A776ADEAEA07B9BCE7179CB67402AD7545BD304D55927A1B1DA4C5D72DB72C1E9277904CD789DD0C12BA2CED40576366375EFA4FBCBEA8A86E35AB1245699A64039C3FFC3C0C69CBBAF92FC9E534DBE1050640992574F9E19AB9B849687F70531BFE68
	DF1254E1177485F0D14167E1E66A8D46C7DD3F54F36D2C6CE9F0220523A394DB29DA1530C72B24744A4A00E423CC14D807D40D03DADABDE5964A858FEAF53F0F9EDC36BA86255B96DF44D1FEB500597C3A9854D38F68EFB0403C4D5C669F7B8C6B394D578E36BA5961E0DB8C6A72A2F17F18AFE6687644624DAC86B7C19D2B7F015FFA1B28BF586A2F5EA36A6F75DB9FAC6A8FFF602FBEB4124B11B11A0A36C43ABEA8018B08CB5AAC8DF3BFEEF42FCF9D1DEF619F9E9AC51F4221BB6F98BE0B785F5BF984ED7FB5
	B13E196CA2686731B119ADDAE045B1DD0AFDG5C83BC4035DC440AC1FDAEE2A1700FF90BA8FC3E5CE8E47A712B960C4D47C6485076E56CEFB84F7394E6A81F8668F8190D9172326643ED41F627AE70D83AD460DAA82E9C8E1397E3F93CF2BED51F4FB89C297A35018875DBB49B3845B70E7B0A814775GBEE88BDF0F6F913DAFD16BF64BBDF66AC6EBF3EB09515BC7063B901388CF50E1B744F4B649435727BD86F9C5BFDBC3EBF37C60093F1171F1C383CDB3F0D72DEE08EE16F6398EDC7DC6D862BAF1FCDC36CB
	C3501F659EBE3A4867EB4E96FEBEFCFACE1FB3D93C275C1094E0EED6EAF392251B1B0D1756453131D70B31ABDF013807622E34F21CC3607EA4F073846E638FB8EE4DABBC57725DAA0C097D65DAFA68C414748D5ACB86D5BB868DD655CA8D02BE4FD2F58347C41FA2A30A67D32E8F232E37C05986B08240E23B93671335798E815C72A83E9F3F5F245F0FF1DBA14683E85D4AF5786E7328F7D334E85A3ABEFAFCAF53839FDB46F5F6EEFA0DC6F779C3F3C97568DE033F9CCF501EBC03EDABDB0F602D5EF5D972265B
	B032F5DD1C16D3B929577FA561E58175FF35B646262D3E7A5F970773E95A86F3330D6B7FEFE352F47AFF89B44E6A6CF320BB426A038C314C06E865E0CA175A90427D3D4E0BD349C114ED6331643CF8DC1BFAA8F781F8GE681AC903C2547CE1AD3E970CC10D3F9EACAFC2D5846FD64FF34CE25DF04687767A9750B967D3ABE18CA3F64D7F93FB3B7301F79D5EF2CD229FBEFFCF5FC8C236550689E6AFC1565C79284FE8B45FBF4F8363706A89E4BA1D097AD68170F4B8FF6C60F2569097697A71F99572F4427BC47
	279F5BEFC70A70CC2176C437BD2B2A3C7E89BB3B56E50DB93B22D99C389A1E1B22393D7AC313C99BB76EB52E0F5BF75171215C8160828886D8G309CA072B52E175D317E6C79D83D8CE44C3D71B123D9E5C2BED237B79A4D5B79788F47266876E42BECF72AE2BFBA62708C1795BB38F19CF71D62FAE40586A1AC86F6D3DBDE86E33608F143628347CCBF0E779FB37DB45EFF4C74F07C44F17F637101E2AA46DF14602F8B006186DBB9CE9538ED8297A4F0C39FF05C9C013BCC4F0A85D0DEAAF03F203815D05EA8F0
	6FF990177CFA2371706BDC0FDF98C61F2C914ADDGF7G040A3AE8DA37884AEBG488EF6FE303ACF077571FCA040717059250D6B2FCD239FBC23711DDB224173C9715ABE755746DFBC1ECF71CFE46D21EB69F6CBEE2A38F89AEBF59B54CC62A7163DE09D10880F937A4FA7E8ED9AD4392EDC6C57D70E67785F2F3AEFC339597C0CBF3C031F714FDB28FD02328A903C034F7713CD935949F950EE510E31732865DE0D0175BE4968DF6F530C38875D722052A3796AF60EB1C754F9F736C51586F0DAD81B83343FEAD5
	A03E776DDFC47B333AC07D4B0D9367A44E1850C6FBC4390E1607455CCB353B850D86270D9B916ADF5FC577DEBA84875FA09C17C477D9B960DE9538E00A2BGDCE98C4725503D178D38BA516E6B9477BE609EB7F01D983801BE28AA4B0EF63B8B6DB3D93E833F1F3E02ECE9EDD65EC03B65308E4B03401B6DAF74DB618D3D6DCE855CE900163F30F5E5327D67900E5D2BD887E0CA5ADCD28F29F903FE7373703EE150FE89C0B8G01BA739B5C5FB3D623EEB53575A890BBB635AB124470779C417CBC53CC9AA357CB
	6E9E456EE241A5EB73C877EE5126DE6A551CA0D677071EA4D1C723B6AD0EAA3E2917A7688DF696AD0467D2G56A66E143F73AA9C6A23C41BD6622FCD72CE10EDA75F9F05597A7DF19A70ADBB3D7B21EBE7A0FF609FCDB8A76ACE5F6FA87D94FF47CE5F6FA811967E9DA5846A22F7F25B3A25095B56CEE251F0A71A91978465B9G4C177D6D47FADFB675FBE6830E6F4F17DDGFD7039D8FBFA3FCD17F70D7185B06DD1D5753EE2371D357635D00DE276A5ED48240D9D89E3B07BF24F907DEE94E3B156GF444F03E
	BCC7F99C95F67FDC52B223496F37677076F203AB0671939263654B56466328CFBC1C668A28B799707E41839EFD9C02F33741407C9ED67F6418FA5C7BE8FF3D75A9437AFA64771DD8FD7D19B175B8C747347ECC36E5C738ECBB16ADA00E1F1C8E78DD3D5540E4D23567C677A1B25B3CBE0E4DFBDFC3BE71EB53590EC27DA7BCD6355D64B1E118014F7D43A7F16EA3203C83A0A3562279E3552778B9D09C4B5BE6BA312D8D4AA3GA122ED9061ED43C45B06CCBAAE14F7GE444F17DFB1B1EAF45D02E9138ABC90833
	C159A3F02B13396E4E819C5BCF36A64C1FEC92E5A7ADE3AE031537D9EE65EC1E88F06C094E74B4322769735218A88F1F970F3A1350EC1E200DA91E71486C5C59E664CB2249C89BD1E97FAB4DFC1DEDD0E6FE6D8DF41B3433691DBDC760F959FA55EF6623895468E277D5B8845B2DFDC2EEF5E82A5F35194DCA73209EAC82BE38CDE178DC0AFFD407E7366C5EA1FECFE48554D56E627CFFA78D6D6DECA8AF84D88E908190E734984DGCC2E51BDE5E412B820DDF635006361503948CC3E991E5B7BEE7F08116F6305
	BF7BA1D9B4B3BAA6D02E275248562DDDFB4ED08ED1BC136FF8BA4AF787540582BC8A3086A09240E46272BD3E271849078BD62B2A561EFE1A856342E1E24A0B4603D83B13F0F3A25972CC63F51E2DD1C5852EC5194962F38E7D0D62EBF4F836F60F1D6467D0AB54ADB471FDDAFBCACFBFA741E2ACCE70522CC988C47FDF283DE9CF70257FADE60792FC696FF7F27AAA542DCE60743FC674742B93AD4656C4AFCDDBE2A07ABF25B64C1368CB7FD7949F1468CB3FB913531F8DF5BBG346FB75968D7F54B8AF5BA19DF
	E54B667EC3D79258BD0005GCB810AC5AE4C15B45E9F3BB309778B073651G66E458A7492C5F0CE45C7F1B136BA7D86FF8EDCF47EA5082FD3AG8E713903F789845B89BF0C0FDC027E3A4FD7874D7E9CEDC69E6BA43CCB437744104477C45E0377123C42A81A537237A716A633390FCA761E5BDE5B66EAC67B2EAA03929BB7F4C86F271CCEFCBBE04E2DA8050DD72DBD5B6FBB245931ECF45135F7336E7D01214FEB57A782C6631753CAC37B7E1996B8363ABF4DFFEC951666BF2770233409E31E1F25857E8E7BC8
	1A7EBB2CF6B7AE83FDA29556E49640C20025GEBD238FD3B3877A021764D0FEDEB3075E1EC07B7F5D4CE7FE4FAE0D95FCD1F7CFE4F59F47CEE50AD2B2ABC0063F3FCE3BA2E17AA3BA812614E27639CA9763EFE15B55570ED69F856BA24DE0AE63E4B0C1B5CF709CE61B668DD8C5BC863EEDE2E2365AED1DEF6F2BAF48369F975AFE2DAE8F0FF06495EC17D4699C0B7E4B77765CA8A9037884A4B852E27104FD1241803EFB8F1CEEA767072F7E939D3148FB8A7639F7BFE3EBDDAB5A90D4FF30F0249BE0F7A4BB300
	5710BDFCBEBEF3725C58A2017B9F054F519AB1878FE7621C9427727293345CAA4A9FE4CE170D71523CBAEDB4F4F78C3379D96A823A7381D83C730BC47471EE699BD63BDDF5C8E769294B62141559BC479C1E4A63147B9311CECE1A45D886508840687FC79277C33BGB7BB0D53FF36E4BF999767B2A2AC4C55E22DD713C44C1946E99DC946B1CDFB614C86B88DD0C8887D5E09ED59CF6499FFB8162B5904A6CDBBE357A66B4F587ABFC6479CA28162473DECCF046E751E31CB3E36DE3B2F1C13E30667EA280B8248
	C907F9G28876884F0818425F37B3CBE3D0884089BG49A6015AE845EAF72AEC7C26FDA847BC98E3099867F846098946E9D5E4172428438C4BFC8D56AFB6B3303DCF4C1C5C5E27E752EF47E0275B07DDDADEAFA713666FFBFB5D12B8D772B3D3854E62EE155DF64CBAB15F6AFB0609FCAB6ED345EB4FF010B9AA5D6BDB19B2AC464D86C26E0D61BE4E17DB708C2209AF7CE86F6677A6548AFD7EA0837AE14E7BF48677A965D363FD4A19A2EF551241FCCA9BBE81B0D74E7AB5940C77A9BB8B7439AC74A9E7C11FF9
	DABFA6774D4249640E57BD97C35F9599DE39A3603D96E43ECE385C2B2E609C6B3294B43F47F2EB3FF67A6668100FC898A3AE134B7FF3B2DE7E5F0B1CDCDEA6133F921E0DG781D003579B7B1F6D7A6731FBD708C565AB0D9D7A913794FDCD67A4D9D7AAE4A744A3ABC5357FFEEFD1366B2057FDCAF75DA879DAAD3EDED3D37E77AFE97C7FED6E7F21BFBF776E4716926CC0B1FEFFB211376AB70A933FE7FCB7B1932EE6DBC4D03F6DADCCF6DE6D916EF3CFF08622B33FC637D30C4EEE79B21AEAC0B4B7B13245B3B
	FF7F2B5B6C37E34A7D18DC5F25B6BDB55BD75E39941F176DAB6F09E4AEEF915485E7737C464CDC5439C8A8A767008D86E8846884D08102G66GACGD88A908E9085E05AE7B16681948354G346F63B636BC7BB889ECE335442C57D893F127A9A7300DFDB3E7F29BDB11635F27FEB7E73CCF7DDE0EFF1F3ABEC76FD3EB77FE34F1957131CB0B99F2B0EF4A6C59F92B535A277B2EF3F19F2E1308DF347D466AAE6F437D06F7BE6DD6077DA63DE254868D1DFDD3BB133BB16D4D6FEE35635F31797B9E8E56DD738B9E
	2C6039397EFD5C57F8689477F5C28137FA9F4F2BEEBB457D1554DC6EC3FC120FED9B215C85604965ED3FDC407D0DD9226D0282FAB79D4A61G5139D375B76E4654324637C166FC276E67635BE54A8B143BGBC7B39BC5B8917E79660166E67BC1E0D617210BCBE4632817A37A1D0EE85684F63E37C564977498C40AD4A63E3E44B627B0198A3C9215F8F72E18FGF464CFD5CED321D74E72AC4E238B46E15FDC172427D0DDF1581DD29D7B4B8F42FD79C86AD34E0736CB72C53C13C8FDF9A8EF4C67764C0CE9C14D
	569C573D7FA8F17A738E4B3EG9A5FCF764AB4DF77DE1EBC7D341B261506F6AFB1B730DD7CC16E64F67107397E6D62025C71F671A7397E6D624FF3475B45A539282BFDE04FBBC43B47846E0A407D1A0E09380F6CB635DF60D7D03E905FA8A1C10E7F8365EB0862BAB45C13BA5C958D971E0BFE98085CAFAB3C6FB3399813765A9D8ED6FE0E3631BB411D9673719265516E326A78FEC5602E0835DDD0B151399C15BF5EBFE17D961E3ABDFF69171376736FAF3DCE7B198AEE51DFAA607E52E6EA4F4B8AFC7D07E80A
	2FAE70759F3E14AF7CA5288BAB6076654E025B73DF8E97D27F2550177EDB941FD768CB7F3F8A047F82F541051C3ED11E08FEE4E1209C7BD3748C09B37B6658B7D0FC0659B747AE4B7CECB98CF5C1E6CE1F445C5EFA6F1E341F7F75FE1F765BE43E3575A68778FA1F2167DA46815F79EE24F873815F797E5DG1F6FE2281BF5004B3B4AF9FBFE6E7A5B6C376E7A6D6957A8BD1B2346499BC47164202F3CE98A17B7996ADC87393CEFE7CDC4FFA5345BF8F06CDAE877CB280F29E2AA681CCC7DDC760D34107A18BB20
	0F69900FA99B9263F9DBF8B25F60049959E4677A6395BC9FF77A90BF6B773E0D38CEA82B82F74FFEFA47824AF38EF17E37D2DF0A3D3F21FB3FB3FF7A4F4750AF00461B05DE19AAF46F0B8B261F762AE925415634C066EBBFDF2C69FA994F3915227CA2ADC7097220BC9DFCA1BD2F6CDF1F969AE3F37A7F20FEFB46E108B38FF39DEF9B20FFF78C657643DC5E8627F75DAF685E7FB8B07DBA90798550782A6215696B3A77ECE57AE917CDAB8D36266733380EF709F51EC9F141D08E85E0B6712FF8FDC4332F879371
	1B1DD39AB20B3FF6C3BBDBF45086F1355CF73058DCD6ECAE2FBCCE34BB01CFD7707C5BC298EFA940C6G53910B3168883743475F76EF33EB292DED04F66D226D427D63ED361D7EAF9AF6176614EC1EC06EBB0E686F522CF5E3CE006669C4C6125DB8E4B93A8217BEC7B79B7A623729A5021741987FFCFF0D6E1F1550AEDC343DBAB01E6F60E3DE3EEDD993711DFAD44F77BDD9636FGAD4C62F1726123BCF60E2B64316D69237C9EDCF56538FBF0B17AFBEC4ECA5FFBF0B82637BE3452779EDC73187E3F2F743D87
	477BB3193FD36C5525EF6EBF60DF17EAA118F200AA15EB2A24353FA46B6F9E309C4C55CF46FC1F997BB78974B69C1B0B8FDD7C4E1A6BA8576BADA5DEDE5E9F38D53DE66D3F142567056116E5A1CF3DD6075B7B37B98915FABFE6BE50DEFCF4E29F12757B3B8B7B2D8850B6827051BEF5EC7F1A691AA79F3398F1DD986E88459D865C86013BCE672386F0DD47785EFF62B3EE8FD4010B9AC55CECA8079D635F442CC751E753F2E9B43CE478BCCF0A974F43CBEDEE02DC28CBB63B1BAAADAECE1D6C4090D2535F3914
	1FA5C0E3B9G339D611FF15B3189F02448E2B48354GF49571FD15C458B79E3B0A8333318E923E97E69471356EA9732E75A31F210E0AF5260C14F4BB2CBD57182E6C9A657BA624086F1B77C6FD774D7018FD619A1DF85F4C9D755DB70F0C59B7ABC7836D9B46733BE43CEEA50D6AF5EBB9709ADEB431EE31B5BDB84AF56491218F47C6390E20DC6C7FF3FD39F691908DFB114443BA3A9532C7CC1905467661B7AA2AE2575F9BEBA0F196320B65BAACA48E93969692CF739996922B4AAE0FCBA797669F625D5749E7
	D442B88D901BAC30585624556C9A53D60C2012643AC1B7D0B433BF52D5C84A181D7B588FCEBD3B7C694B6ED8324BC276758226CDD2E86ED732615FD7E87FEC659CC9F0615DFC7C9FG403B1BBE625D6A30C3EAA1993D17700F015D2AE42B65D9E537CAB2DB10D2A9FEF5AEED343A7B296A7D350544B9715F42103D2C5AE1F57615329473DF69844454F6218ED261F0BA53A93FB8CA0998A57D67FB0E9CE80AD894CB8C67C822C3365A4ED9FBD4D931108435DF115C7D324366F11D29218319685DEACCAE4642F07B
	0578A8EC89EEB14174B54FBD41A99BD34E998871A8241436D421C109E8E12B6B37AAC0CAD2ACAA7E1B9BD9B9AC6A3ED4209E7D4D7B591D8692238909AB1260F1294015E2F53A716F5F0183338F873CCA8CD4722DA74A3E7D40763F53013CDDD5D248268C3A1676C972C0E9EDEF2F5DA90981E07EE078FCB1BC36A971E93357734D4C26D5BB3BE13D6BC96CC0D9997DDDCEFF1F445F6594D3CEB165A7E192A34904BF3E759E2A07A9AAD392319DF019CB4E41CA29FFFA710939CFD6C7EE82694E11F87A5789B05929
	5ED69786873AA5E5E658377313C2427F95574205AA41475D55EB9D30BB0661251E1EB99E4467FBD4122473CCD41222FDCE54749E9B99FD34AB06E2134629C91CA04512FA3418032D70E58FAD26515F99D31E9D6C154586C4CE70E57FD8D10ABCFE19A581180D7DA2F8AA7140C26E9EFB94445C177D1554EFD48C9A08699CAE0A4355EF35E88B5AC2123A29E1431B8F8E1524FA95177685ABE0688193315F0B6FC1BC2BEDF3D9FB50D0A4582C4AB5BA8272D076B1C968D57A3AA9811598BEBE77B11025A52CBE59C2
	9255FE3C8C07BBD6A5595E21DB71D31ED641EC8C59896022BF6C5E4871D17D9D4A682FE6873CC3E9ABE5E7072754FBEF04FD2BACEA40B5D9887895257CFB23277EAD9278CEF95B005561D02D0EEB35CE9B5EAFBFBB2CCA5EFB04EFB56078EE5EC62BE4F5158D4877D5DD1F7F87D0CB87889DBE2A481B9BGG08D2GGD0CB818294G94G88G88GB2F2DAAE9DBE2A481B9BGG08D2GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG55
	9BGGGG
**end of data**/
}

/**
 * Return the JPanelDefvalue property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelDefvalue() {
	if (ivjJPanelDefvalue == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog", 0, 14));
			ivjLocalBorder1.setTitle("Default Value");
			ivjJPanelDefvalue = new javax.swing.JPanel();
			ivjJPanelDefvalue.setName("JPanelDefvalue");
			ivjJPanelDefvalue.setBorder(ivjLocalBorder1);
			ivjJPanelDefvalue.setLayout(new java.awt.BorderLayout());
			getJPanelDefvalue().add(getJTextFieldDefaultValue(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelDefvalue;
}

/**
 * Return the JPanelTrigger property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelLoginDescription() {
	if (ivjJPanelLoginDescription == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Description");
			ivjJPanelLoginDescription = new javax.swing.JPanel();
			ivjJPanelLoginDescription.setName("JPanelLoginDescription");
			ivjJPanelLoginDescription.setBorder(ivjLocalBorder);
			ivjJPanelLoginDescription.setLayout(new java.awt.BorderLayout());
			getJPanelLoginDescription().add(getJScrollPaneDescr(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelLoginDescription;
}

/**
 * Return the JPanelValue property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelValue() {
	if (ivjJPanelValue == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder2;
			ivjLocalBorder2 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder2.setTitleFont(new java.awt.Font("dialog", 0, 14));
			ivjLocalBorder2.setTitle("Custom Value");
			ivjJPanelValue = new javax.swing.JPanel();
			ivjJPanelValue.setName("JPanelValue");
			ivjJPanelValue.setBorder(ivjLocalBorder2);
			ivjJPanelValue.setLayout(new java.awt.BorderLayout());
			getJPanelValue().add(getJTextFieldValue(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelValue;
}

/**
 * Return the JScrollJTree property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollJTree() {
	if (ivjJScrollJTree == null) {
		try {
			ivjJScrollJTree = new javax.swing.JScrollPane();
			ivjJScrollJTree.setName("JScrollJTree");
			getJScrollJTree().setViewportView(getJTreeRoles());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollJTree;
}

/**
 * Return the JScrollPaneDescr property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneDescr() {
	if (ivjJScrollPaneDescr == null) {
		try {
			ivjJScrollPaneDescr = new javax.swing.JScrollPane();
			ivjJScrollPaneDescr.setName("JScrollPaneDescr");
			getJScrollPaneDescr().setViewportView(getJTextPaneDescription());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneDescr;
}


/**
 * Return the JTextFieldFirstName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldDefaultValue() {
	if (ivjJTextFieldDefaultValue == null) {
		try {
			ivjJTextFieldDefaultValue = new javax.swing.JTextField();
			ivjJTextFieldDefaultValue.setName("JTextFieldDefaultValue");
			ivjJTextFieldDefaultValue.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldDefaultValue;
}


/**
 * Return the JTextFieldValue property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldValue() {
	if (ivjJTextFieldValue == null) {
		try {
			ivjJTextFieldValue = new javax.swing.JTextField();
			ivjJTextFieldValue.setName("JTextFieldValue");
			ivjJTextFieldValue.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldValue;
}


/**
 * Return the JTextPaneDescription property value.
 * @return javax.swing.JTextPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextPane getJTextPaneDescription() {
	if (ivjJTextPaneDescription == null) {
		try {
			ivjJTextPaneDescription = new javax.swing.JTextPane();
			ivjJTextPaneDescription.setName("JTextPaneDescription");
			ivjJTextPaneDescription.setDisabledTextColor(java.awt.Color.black);
			ivjJTextPaneDescription.setBounds(0, 0, 224, 60);
			ivjJTextPaneDescription.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextPaneDescription;
}


/**
 * This method was created in VisualAge.
 * @return CTITreeMode
 */
private CTITreeModel getJTreeModel() 
{
	return (CTITreeModel)getJTreeRoles().getModel();
}


/**
 * Return the JTree1 property value.
 * @return javax.swing.JTree
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTree getJTreeRoles() {
	if (ivjJTreeRoles == null) {
		try {
			ivjJTreeRoles = new javax.swing.JTree();
			ivjJTreeRoles.setName("JTreeRoles");
			ivjJTreeRoles.setBounds(0, 0, 165, 243);
			// user code begin {1}
			
			
			DefaultMutableTreeNode root = 
				new DefaultMutableTreeNode("Role Categories");

			ivjJTreeRoles.setModel( new CTITreeModel(root) );			
			ivjJTreeRoles.setCellRenderer( new CheckRenderer() );
			//ivjJTreeRoles.setRootVisible( false );

			DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
			
			synchronized( cache )
			{
				List roles = cache.getAllYukonRoles();
				Collections.sort( roles, LiteComparators.liteRoleCategoryComparator );
				String tmpCat = null;
				DefaultMutableTreeNode currParent = null;
				
				for( int i = 0; i < roles.size(); i++ )
				{
					LiteYukonRole role = (LiteYukonRole)roles.get(i);

					if( !role.getCategory().equalsIgnoreCase(tmpCat) )
					{
						tmpCat = role.getCategory();
						currParent = new DefaultMutableTreeNode(tmpCat);
						root.add( currParent );
					}
						
					currParent.add( new RoleNode(role) );
				}
				
			}
			
			//expand the root
			ivjJTreeRoles.expandPath( new TreePath(root.getPath()) );

			ivjJTreeRoles.addMouseListener(
				new CheckNodeSelectionListener(ivjJTreeRoles) );
			
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTreeRoles;
}


/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	YukonUser login = (YukonUser)o;

	DefaultMutableTreeNode
		root = (DefaultMutableTreeNode)getJTreeRoles().getModel().getRoot();

	List allRoleNodes = getJTreeModel().getAllLeafNodes( new TreePath(root) );

	for( int j = 0; j < allRoleNodes.size(); j++  )
	{
		if( allRoleNodes.get(j) instanceof RoleNode
			 && ((RoleNode)allRoleNodes.get(j)).isSelected() )
		{
			RoleNode rNode = 
					(RoleNode)allRoleNodes.get(j);

			LiteYukonRole role = 
				(LiteYukonRole)rNode.getUserObject();

			YukonUserRole userRole = new YukonUserRole();
			userRole.setRoleID( new Integer(role.getRoleID()) );
			
			//use none if they did not choose anything
			userRole.setValue( 
				(rNode.getUserValue() == null 
				? CtiUtilities.STRING_NONE
				:rNode.getUserValue())  );

			login.getYukonUserRoles().add( userRole );
		}
	}
	
	
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

	// add the mouselistener for the JTree
	MouseListener ml = new MouseAdapter()
	{
		public void mouseClicked(final MouseEvent e) 
		{
			int selRow = getJTreeRoles().getRowForLocation(e.getX(), e.getY());
			
			if(selRow != -1) 
			{
				if( e.getClickCount() == 1 ) 
				{
					TreeNode node = 
						(TreeNode)getJTreeRoles().getPathForRow( selRow ).getLastPathComponent();

					if( node instanceof RoleNode )
					{
						LiteYukonRole ly =
							(LiteYukonRole)((RoleNode)node).getUserObject();
						
						getJTextPaneDescription().setText(
							ly.getDescription() );

						getJTextFieldDefaultValue().setText(
							ly.getDefaultValue() );

						getJTextFieldValue().setText(
							( CtiUtilities.STRING_NONE.equals( ((RoleNode)node).getUserValue() )
							  ? null
							  : ((RoleNode)node).getUserValue() ) );
					}
					else
					{
						getJTextPaneDescription().setText("");
						getJTextFieldDefaultValue().setText("");
						getJTextFieldValue().setText("");						
					}


					//we may have added a new node to our selectin group
					SwingUtilities.invokeLater( new Runnable(){
						public void run() {					
							updateSelectionCountNodes();
						}
					});


					fireInputUpdate();
				}
				else if(e.getClickCount() == 2) 
				{
					//executeEditButton_ActionPerformed( new ActionEvent(e.getSource(), e.getID(), "MouseDBLClicked") );
				}
			}
		

		}
	};
	
	getJTreeRoles().addMouseListener( ml );
	
	
	// user code end
	getJTextFieldValue().addCaretListener(this);
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("UserRolePanel");
		setToolTipText("");
		setLayout(new java.awt.GridBagLayout());
		setSize(411, 371);

		java.awt.GridBagConstraints constraintsJScrollJTree = new java.awt.GridBagConstraints();
		constraintsJScrollJTree.gridx = 1; constraintsJScrollJTree.gridy = 1;
		constraintsJScrollJTree.gridwidth = 2;
		constraintsJScrollJTree.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollJTree.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollJTree.weightx = 1.0;
		constraintsJScrollJTree.weighty = 1.0;
		constraintsJScrollJTree.ipadx = 375;
		constraintsJScrollJTree.ipady = 209;
		constraintsJScrollJTree.insets = new java.awt.Insets(12, 4, 4, 10);
		add(getJScrollJTree(), constraintsJScrollJTree);

		java.awt.GridBagConstraints constraintsJPanelLoginDescription = new java.awt.GridBagConstraints();
		constraintsJPanelLoginDescription.gridx = 1; constraintsJPanelLoginDescription.gridy = 2;
constraintsJPanelLoginDescription.gridheight = 2;
		constraintsJPanelLoginDescription.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelLoginDescription.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelLoginDescription.weightx = 1.0;
		constraintsJPanelLoginDescription.weighty = 1.0;
		constraintsJPanelLoginDescription.ipadx = 160;
		constraintsJPanelLoginDescription.ipady = 56;
		constraintsJPanelLoginDescription.insets = new java.awt.Insets(6, 8, 14, 4);
		add(getJPanelLoginDescription(), constraintsJPanelLoginDescription);

		java.awt.GridBagConstraints constraintsJPanelDefvalue = new java.awt.GridBagConstraints();
		constraintsJPanelDefvalue.gridx = 2; constraintsJPanelDefvalue.gridy = 2;
		constraintsJPanelDefvalue.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelDefvalue.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelDefvalue.weightx = 1.0;
		constraintsJPanelDefvalue.weighty = 1.0;
		constraintsJPanelDefvalue.ipadx = 178;
		constraintsJPanelDefvalue.ipady = -1;
		constraintsJPanelDefvalue.insets = new java.awt.Insets(5, 5, 4, 10);
		add(getJPanelDefvalue(), constraintsJPanelDefvalue);

		java.awt.GridBagConstraints constraintsJPanelValue = new java.awt.GridBagConstraints();
		constraintsJPanelValue.gridx = 2; constraintsJPanelValue.gridy = 3;
		constraintsJPanelValue.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelValue.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelValue.weightx = 1.0;
		constraintsJPanelValue.weighty = 1.0;
		constraintsJPanelValue.ipadx = 178;
		constraintsJPanelValue.ipady = -1;
		constraintsJPanelValue.insets = new java.awt.Insets(5, 5, 14, 10);
		add(getJPanelValue(), constraintsJPanelValue);
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
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		UserLoginBasePanel aUserLoginBasePanel;
		aUserLoginBasePanel = new UserLoginBasePanel();
		frame.setContentPane(aUserLoginBasePanel);
		frame.setSize(aUserLoginBasePanel.getSize());
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
	 * setValue method comment.
	 */
	public void setValue(Object o) 
	{
		if( o == null )
			return;
	
		YukonUser login = (YukonUser)o;
	
		DefaultMutableTreeNode
			root = (DefaultMutableTreeNode)getJTreeRoles().getModel().getRoot();

		List allRoleNodes = getJTreeModel().getAllLeafNodes( new TreePath(root) );
					
			
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		synchronized( cache )
		{
			List roles = (List)cache.getAllYukonUserRoleMap().get( 
									LiteFactory.createLite(login) );
			
			for( int i = 0; roles != null && i < roles.size(); i++ )
			{
				Pair rolePair = (Pair)roles.get(i);
				
				for( int j = 0; j < allRoleNodes.size(); j++  )
				{
					if( allRoleNodes.get(j) instanceof RoleNode )
					{
						RoleNode rNode = 
								(RoleNode)allRoleNodes.get(j);
	
						LiteYukonRole role = 
							(LiteYukonRole)rNode.getUserObject();
	
						if( rolePair.getFirst().equals(role) )
						{
							rNode.setUserObject( (LiteYukonRole)rolePair.getFirst() );
							
							rNode.setUserValue( 
								( CtiUtilities.STRING_NONE.equals(rolePair.getSecond().toString())
								? null
								: rolePair.getSecond().toString()) );
								
							rNode.setSelected( true );

							break;							
						}
					}
							
				}			

			}
			
			getJTreeModel().reload();							
			
			updateSelectionCountNodes();
		}
			
			
	}
	
	
	private void updateSelectionCountNodes()
	{
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)getJTreeModel().getRoot();
				
		for( int i = 0; i < root.getChildCount(); i++ )
		{
			DefaultMutableTreeNode currParent= (DefaultMutableTreeNode)root.getChildAt(i);

			int selected = 0;
			for( int j = 0; j < currParent.getChildCount(); j++ )
				if( currParent.getChildAt(j) instanceof CheckNode )
					if( ((CheckNode)currParent.getChildAt(j)).isSelected() )
						selected++;
				

			int endIndx = currParent.getUserObject().toString().indexOf("\t");

			currParent.setUserObject(
					currParent.getUserObject().toString().substring(
						0,
						(endIndx >= 0 ? endIndx : currParent.getUserObject().toString().length()) ) +
					"\t   (" + selected + " Roles Selected)");


			//let the tree repaint itself
			getJTreeModel().nodeChanged( currParent );

			getJTreeRoles().invalidate();
			getJTreeRoles().repaint();
		}

	}

	
}