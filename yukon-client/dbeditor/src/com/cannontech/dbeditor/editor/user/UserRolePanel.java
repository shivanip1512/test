package com.cannontech.dbeditor.editor.user;
/**
 * This type was created in VisualAge.
 */
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.cannontech.common.gui.tree.CTITreeModel;
import com.cannontech.common.gui.tree.CheckNode;
import com.cannontech.common.gui.tree.CheckNodeSelectionListener;
import com.cannontech.common.gui.tree.CheckRenderer;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.user.IYukonRoleContainer;
import com.cannontech.database.data.user.YukonGroup;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.database.db.user.IDefinedYukonRole;
import com.cannontech.database.db.user.YukonGroupRole;
import com.cannontech.database.db.user.YukonUserRole;
import com.cannontech.database.model.DBTreeNode;
import com.cannontech.roles.YukonGroupRoleDefs;
import com.cannontech.user.UserUtils;
import com.cannontech.common.login.ClientSession;


public class UserRolePanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JPanel ivjJPanelLoginDescription = null;
	private javax.swing.JTree ivjJTreeRoles = null;
	private javax.swing.JScrollPane ivjJScrollJTree = null;
	private javax.swing.JScrollPane ivjJScrollPaneDescr = null;
	private javax.swing.JTextPane ivjJTextPaneDescription = null;
	private javax.swing.JPanel ivjJPanelProperties = null;
	private javax.swing.JScrollPane ivjJScrollPaneTable = null;
	private javax.swing.JTable ivjJTableProperties = null;
	private RolePropertyTableModel propertyModel = null;


	private IYukonRoleContainer roleCont = null;

	private CheckNodeSelectionListener nodeListener = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public UserRolePanel() {
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
	D0CB838494G88G88GFBD52DAEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DD8D4571536B1D9B775EB49D73335ED525A446E52446E5A8DED79DA1B4F346ED713502FB6EB9A533AAD5D5A14F6CDCAB7FE093BF137F7780900207CA5E0C5409F948407DF8D8564F7E6E0E054890C08BFDD9918870C8EB313190700B16D1EF36FBBEF9E438C84AD7573785EBD77677C5CF36FBD673CBB325C22D5493A04DC46F449CC79730E0EB1070531DE17E90AF025A93AA42679F388A00BFD52
	0248FA280B6A53A5E5B22FBD854A96A8FB4F69127260BD07FD6AF43E89DE425009B7B2F67D3F1EBCB2B71D74E3C4E7B7AFFF6CC2BABC7782D881A7832CCFC17A0FDE48D770AB213CD7F9C750A5B0B6150AED8CDD799AFCD22AE8E70167A0C29A36F972C201D23F9EF0DBG8AGDF15A65AF641B315F3F7EFC33D526E6A8311EC47B3CD8999C8CBB3BEC12452B7C6F996338DDC91096CDCF293BC1B2CB7C61B07BD637AE13B5B6DF14B5270185EB1A4B91C324727D79E93FE495750F0817E35F8C61D6EF63BDF325A
	5D122BE98F72FC3DAB050F87B2C1F9A3477DFB976AE9893C2F84D83B07787EC1C61342570A9B11AC76514A30FC27278BFE2B5283FCEF35E627452663783F6473007D3CD00E8490B2D5763D33E019FABDAEA18E9372D47613BCC7B3C81EE7CCC86FAA14E50065AA7E01CE442F0672BA004D99A4671963A467407BDFE71674D793760511F3FB0610AFF7EFC0CEEBC7F65AFEEEEFAB0F234DD821CE86D8863086A086E0BB4081EE67FD47CE4033EDC86E700C0FFB5CEDDEEF3B4B61BB6BF1C8AEF8EFE8009A8557AB8D
	FBFC8E4604ADFB3AAAC270235A673E19F60CA0E43B48487656747EA3EB7A662B8949A1E40BFA3EB9A1DF9923E2DFC03631FD2837FEB36A4D826FBAE5DC019F6678B59A3C186FB8EECFD85E88F595192467013D440BA5699116D9EDCF589D0217155F6ECF084894BCC4E686F891BA7615210EB783BE9EA0A5CB17D48C508AB018C5BAFE78D86DFCBAB6031D795CF6D75F4410576E135BA71CAE0764D3F47D720539F49D8AC43FB7965CCF68A532D6BBC7DB1F988C29172D1146045BD9C29FAB3282FA91346B7A4351
	0E4A1AED9BFB55B9EC66EBE2E3161843C4955F4571DBB4F8B1371B785A42F29C54D932F5C922FDB21F733559223DCE9DE7AF472F536045B81F6A24BDE1A354D5646812447C9EA847BDB5824AEBGB6GEC855885101F8B6DF2E90E630ED5FE48F5E4554C2F687B0563EF43D31A12FDF6335BAF5B5D43D23F5D67348F39A4BFD35ABCF69C75BAB4235FBF7175BD8CB4BAA53764334BCE0F9BC6370FCBE0CCFE51A67AF8963671C93C56669C170040381751386B0F674053E5774B973C8E3BAC85DA087A0D4751A6EE
	5A7BE1C498G5EEF67223E6E637B36855E47F2C9D7DD9508DB8F65AD2ADEBACA3361598346AD399A9A8C4EE1E4436E1BA69B3DEA4E0467F69C67ADDDD29C0077EDDA1F2F8C109D9AB23E4CAA1E3F96528E91F63CADECAABDAF588E1DFC7CBA405BGE40095F9F49E3F52B5C03677D6E81BC4C0FE626628D777625C19ED840D5DE62211BC4F3A5B7DA156E7E2D0D9687DEBA751F623C02ECDG71F9247BBF15968B1B44B3172C703C8F8CC8F284E6C234FD3F8C4F98692664168D12D7F2BB60452F5840C74BD28376
	B962D1EDE7994763FE55818664D7719FAF4B92F898C3EB6DD47FF73C5EAE4BBE675004AC75C9326CF40F867AFF0E73621F31957A95D92375A8EBCABE6C33GD579A4EB4D099C0D1DD9BC1E9B93DE9B2EA045DEFB7839EC0076D773E97E8D1D681FADD14B67F8B9D07FC3934D5DDE255DC276528F37C77FE6860D60797D4B505853455477DC4D7B431D83FFE1FA33E9FFEDD1E90839AFA98FEF3B956521ED3726BC346D56174FED1B2D6521ECD354F516639A77CC3AB536EEA9477DEF5863F6CBFC4129DD1870B9DF
	31219CCE37D33E68F40CCAF21F7316445806FC345907799E7B903C2F4D27BDF172886D09C7EFFD092D3F569052FFDA7945CB89A97B053D966C8F6C09022733953806A417B48E0A6A70CC38E54597647B370277A70A36C75C5837345B86BAB4B9A517833DDE3321E29816372C677F0A1323743A7D26FDCA6F1F84FD6979E468BB3056082FE545A7CFB6974F8E82AAC61CBE496C76CE50A0E57DE5D5E29D643066EED95036428CCD02734969D3E191B92891749F7B60D6887A3D12BC6D157E8ADC54129E6E4B3CA978
	68981306EF34FB260CEE5C59788895F6EED6FA7130CA3E910FEFDCF2F0A67476C9D961200DD7AF18056ADABAE37E2392ED2689EC498AB0361F765D6FD7E0BD6D3B4A615261F1390439C3DD6CFEEE4B66DC0DFDAE59AF625F98F80A7A426ED075253F277A4F072CEFD26B0BFBC255DBFFCFFC2EBB09FE00DFFAEFC282778A5557A3CDABBE52F7CEE68755755BDD93815F6B5913FB6069521CA9827F24BE74BA5A2047F36406BA7EC67D1B8CFDA8B126D877EB6DB82657679C07A3988FC9A54E9F6A5746798E609FF3
	822B61998DA06C61BB960CFBE7C61F0CED7ABD760FD26369CA9577AC473D8ACC9CC8A69F7FEC26B687607D792624D07B88C26E8131FF149E880EFD0A2BF15CAE408F9CA0BD37D7EA4F61B63F5FB36C642E2722335FD6EA779801FB2DB28D1EE0FEFE08039C529469FDD7A5720A3E2902D6F477F87999B65B57B672501C5C1C0EC9EE6CB6CF873847DDB0F59D6A965C6F14A65116F98C3E59CC7AF29D20BD768313B64F30EEDFE7421EB03AD92D68E403C6B7F16D59E9E26C8B5DEAEE23GEC1563FE28621AD45C45
	9E424985140B78F483460C21F29143DCE87D6804D3BFA1BB5129364BF63EEE35F908986E6F60993C2600728D57AE233DEE0372AE005C03A46F459A2DAD2C0FB505BCD390AA8E8AB9DB8F865B42BF57A03D2B00779EA4DB182C4A5258C2AFE85F6E9E0555231E5B5509E21DA865326AE4DAA70C7C07974D8B09E1179E24D864B735389F2C04F294C08C40B60024C262EDED753CB139F1EA0E187CF277C278BADAC8BE650F97542FDE6DF735E7A17DACEA3F9F70B8FC30B0603B4A1A77E50533FDDA91A77FAB371507
	8AC51C3CDB457F146323B4F891BFCB9C0F6598282BAA22F958FB8A6DE28514D797116E53EAB2B4F6E175B85D32EA93DF2F413D5263F34251C438F59CB76C71F147CA787371FC5DC641181B54F1DF2F8D6F877EEFEDE8BFB029AE349F18D6B7F78C13DD974ECF947C3D57CD7CED8390387FD4F1BBD51C3F07F06945040B63EB37984A75AA6E7CC59E3FC0F9C045D5DCC25CEDA82FA82679FE114FC3B414B7816CD0713F60784CC33A24BA0001C3043FF1F9A17EB90E992EAEDA212745899ACB97C4E32EB8B9114D0E
	1345FC641CC2FB1CD24BF93CBC4D684C105A386D5BFD60E39B5D8E45684479381B4FCB8E9C5A1FE004DB46F15500FBDD452572392A85DC250EF077F3DC9E60BA9879EB3F6B4773C276F8DCB627977DE6F67B90FE9F78EEA3D35B0CDCC41BF65927BD9340C745E19C6B030BF4762F0776DB308F60F38F0B18D5487115C99CFB44E79F87F36B73CA43AC6588766D38C4FDE5E83F8CA092E0A3C05CE1729BF6B7218DF4778D7B40BF6B3679A4C9609FAE417CD3B7BFBE38CFE8107C43BE27D7B8F0224DC4C6265A466A
	73F8A51F6C147C42E7493F2275D92CC7843F37619981A05AD4F6EB5BC4AAEDE260992B36F930A8D41B1D70CCBFCAE755772FE84F2A4C4F1CCE20BC5E2CBD7728728D60E8602C92FB5597AFD22E4F81F58F9DA55DD68FE22E0F1F3964BEB7B4F47A1C0EF67B28054F14B8DBFB86D2147E1B0F5259BABD00E3261768120E82B415903FD3474817D873770FB04B1241105FACE2ED1D8936924167D4C900DFC78779924ECBC4B38A6A3717701C65A52D4F0B7374E282E3FAF80A7A27E75463FEF510E96B5F18D10F7C16
	A7EA6BCFDE5656230E0EAB7D05EC4F1610EC3B1EDD4BDCDFBD9776FBCC5DB1A15300720C1BF26BD66E4ACE93FA9FB59E546ADD091D9A9A94CBC51BE3642FD92F102F94F10CF47FEB11B306F2AC404EE3B41F2F725CFC08F1B55F1FC63B49CF4ABCCE63FD100D63F5C1F990E06AB879E95FAB223691EADB2F015306F2AC404E63F46EF5230B2A6CC5FB1ACA6059EF3F9E52D36D86564E8FDD876F48E69FE534E7268E5CD94EBF6F8E7BD9975CCF7883AFF33B4FAC8D4E333F4671852541F976BF8F505AAB053A6525
	242F6B4DB8D771D0CEAF03B5845084F095E02ACC7DD655D445667159EC9E2F859CG176648967239AF5D59371027F6130DC7FD63B196FD5F5E30314072B2E15BEB146712C9378E1FC23E14D614EFAB546D84A8B88167B0C097406089126F33CD95C2BEB410B6D9368F0F71A809044340A500464DDF496FDD1DCBB679C4B89F6F7F783710E5A7023F350CF17C4A93415FDA62BB69DBCBA4542517138F611A6153FA813F34BCC0F3E5F9B87A3F656BECCDF9B07DB7B8FEDDF9B07DFB3209FE8C54D5D5907D670A3474
	D7GBE32A2C0F3DDC5B87A5B785A5DD491CC7F950E0F2D8826FF53C07463214EF612683F0A6E0D320EF6E95E6F9DD87C7C6A6796150630CBFFF3F83F7AD6F3E83FFAC9F3E83F7A3E6639732B9FEF8E1FDFFD30D91BDF956B6ADC9B1ED151B0971B81F682246BF5C945FADAD3C7DA8EB33E26C22CA723E3D46A7070AFEEB2514FEA8DAFEBFE6B7C5F698E35E2AEE348A34B1EF19C1F7047DBF13EE40F17A3155CF4AB6A88C21DB1D9B4255CF4AB1EB3AEE90423052FFF78AA797ACDFA32BBC7976619162BE5B7AF2F
	D64B2FADCA6E9D69856CEFDF57E250206FA0C276FB78993D8168EE51D36C733495F13B201CD9C9382EDE52D1DDA5696075EC5449D2352CC3B71F2DD24BFF1BBD9F7F58776E5668A37352381B6FBEC276DFF15FE483703AC555073B096B884A19D504FB22D555D195696057862EA3353C0317D729655B06F9F5D4F537BAFAF0DE9AF723A3312E7A5B422F6DBF364D3F36C75AF86E925624ED5AAB895C0DB61E2F989971CB6A9E62ED4BD4F1E63F5563F7E2A435338A755F64B04CA32396B6C01FADGC71472F6F8F7
	422174280E62320FDAF03F61419CA6037D14EF7FFC1756F7EF2D869C44130EEA0A85BF530DFC8FF9FCBC29A1624F2F28B15A94340BG0881082D96B98351AF56BABBEEFD5DCA7DF6A21D9ADDD2BD0025C67493F2DF6B1DCF6E64A0399DB5813927EA045C6FB1127B79CBBC9E8DC488BCE695F1E4CDCEF0BC0AFCAC05B1D656109C2CF8367CF94554EFED0D10FFA3BC375608FC0968F7EF53EC7907DA285F0E9AA1FFFAAD1CC135221F107F19563964574A0E6F755057D29B10FF905EAF007CCFA4107C9BDCC17273
	8F93C27E92E3307C48078B46D882D0A464E7E84B74E7B6AE3EC564ACDAB02D217842BFCBA7DF386071C71961053D8948DFA8DFF8E52D92675586E259754E9CA55EDBEB43BB16A162B234E2A54E7B217A9D23388EEC107BDFAFF6137FB5G3808BA0A733E15D645BEE42E5C6614DDD2BB1FF5C167904F5F60B89B6BA826FB5B0AF458A9DDD2BEC055A922DF74AE45AEDD00DBF20A683F24BF40E6658D849111B6D0E49AFD17E27868D3C46BC1BEE6BC14D3CE034D1C8EBDBFE2AE3E04A6240CF5BD0774D3FF1A467A
	C18E0E650572B200D527096F16E252DBB46062CFD3BC7BB745A4FB69BBB446B79B316DA01427GA25E21B16EEFA159A391778E0D71DC8B4914DFCFE330D6ECEB05729840127AD0B24537043739ED46001CFFB426A87637D29D7FC9FE67EB931463G589FA806FC4E048A4A07F29D40G0097E0A940C3GEB81B6GEC810887C8E900B88C208A40GE0839883385DC03E63520EB296A6EE87241298628E244FAEF258CA0EFF4E3B11918D334FCA516F2773FE9BD85D90AA3F7A723C7D8AC352BB463FA92C8F41CFA8
	585CC0F76B6E676BA32EA1B8667FA4476FE8880E798B2CE4BB2C917675C672ABCA5E3D33DCC56D9D76F3AD381F10AB351767F89A03654D66784D0D4172FED65DA7E2214E50C472FAF266229F5994AECEB56672F838A9B8CEBD4F71B1CD41F16A8BB9345E37C2DD6B9922EFAC3EB37B6A1B37DFE87BDA4E7B2DBE73616CAB7A8C59D7A54FD3471C8956F7BD47EFB9932C6F279AC95F5B202E2B19640DE839B3BB795C9D76FBE1417D04DCB25FAF57B4874B7B8147C7B7874B7BE6AB493B816ACADBC85EA1A351DF8E
	38D5GD1AD815DEFEC89E7DFB5BC2F3335A5583E1AB8FEFBCB30FDEDD06F5C6E023A41D65AAFDFB205772FDFB64D6FDF3FE68A9DBB6FB24D0E1DF51D21E3675D1D5A58D96052BB710C9B057DFAC0ED172562AE2938377918083B68F448E3AA3E20B3CD41F7C9C8107007BB112FC90E9B20B8DF033BC4F1FEA77A0CA0721847C7FD2B79DD4391274BA54A27F89B279BC286D59F8D1CC727572E613BC545A15F4AF745AE0A69B640DC9C64F333BEA7030F67F23A258EF1C3D8EDFF067BBCF1501635D1DCD8FF8EF139
	D0AEEDD3570F1529367DE94D7BE16B6267359A7F8AB4767586E44A573CFF615D45277D0DC525215C514ED1E3FDF5CEFBF2516E86557285DEDE22167FADE7B178C2FA8159DFDB949AB3F3C60FF25FF695500DD6ED3C2391F1B1D00ED565BD55941877E64D7BF79B975F86E27F8AB4EE3586E43AC7731E5E3A7834CB9615061853E99E9FEC07794CEC27F9FE0E630EC2592262AACC089B04322C627CA6DA9FCB5B499E6A793E3CD6AD3773F20CDAFE46B41FCC58776E747673F9E95CCDAECC5C97C8B5D30EA09E78CD
	6A50A59D85B0GB8BAA857E16B4173D04610596461714D7A8E6C6B626747D2F80FD05B1EBF17CCED99452B2F713B667986FE7FB327F88E1E128CC2E7E22C1F79B1F745EF9C2B199DF19BC164366E4B5566368621AF66F317990817EF9407667BB93EA7BC846DA25536179BE7733D3F1B293CC498B1D7F1932FBB8C5BDDA25D52EE77D9BDAE677034B26E635C5ECE82ECD1477D1591E3D2B8701D37000559BD0927E4EE03ED28A7A3756D60381BCEE9526B71498EA3363BECC2BB9C00F7214F7BCDF369B352285567
	1B924F95718CD1705D8DFF27B6D7348E7AE15EB7961EBB0C9463D71AA9CF10EE223B872366603B873F54E96F8EFC5A9CFC77GFD48C07D3366603B87BDB37A7F0FB9786E817597F3F46F79409CD51C435F5B3A2549E27556B34EFB6961B6F0B0DD07AB0A2B61EFDD9923BC562AC96D9D127AB4CB587B0E8613F2EFC1F95A9EF36B949B4BC1FF2DC3425F6D51381312B6BFD6D014161095E65C88E53CF526C0FE0C64CCE4575F1F11CF52644714952164C63E15CB779162E19C2117685E00DE5EE9BC9CDAAF8ED00B
	E79494D325A8C6347F36319A1E5A6B6B1A7C223E5F58EBB3F734D92E75F5741E37D85A5BFA2F35D9AE36DD6AD3699518EA67685FF53E57FC797CB9DB2891C4FFAF7E62DA516599C9BBC7D56F3F1D1098C617251DC207751D41F3748FA66D9C11EE435C89612BC3687571CE526BD8A7651C72F84C338A4A51G1BBA69BC281BF1BFB58E703BBA675E7FC52EAA328FE9E4F66912AA3AA8FEFA66FCB98B1FEFD2D66E3440729B6A4AD8AF59F0AF3040B8E3G4B81D683C483EC82588630CB2511E9AD1D0306C8DA86D2
	DAC246EF67220C796650F2D5G3EB528CE70155F0FFB164DCC345F68BDBC87ED0CD27AE49F4C861D03EBBB116E98743F9D02F6C4881C08DBBF4667EA35B9B81EDE4171EB4D417174C19345536B202E381B7C039E3E6FAD05B26ECD82E7653807G077B1D106B4BFDC8DB7D26AD26C6290B6A43FD8CFF5F83EB4EF90BDF0597D2AA7761ED61E3661D3679E3E65D0550B1736E8B33E366748B21E3666C8B33E366BCE5DC3407F376F1F55C0397B02E945FBF4E5A5D76D17EADDE6866C53E9FC503EE3628BA5CDBC8BE
	56F69577877E2DB23387FC0E9E320D02FEDC2BFEE57EC53B6F72F5D85FA366CB60FE41F1B640F9FBE83C9F99D4BF2E07566583FCAF916F1F513C9F1957373A9B90B41A96150658476C475283F645539582DFB0159140D33AC259DF63F371BCF2B87DE8C18EBCCCBABCAECCC9A83EC85ADB2847C850E194007093CB8A49BF588C38B844DBF4C925GDD9672D72CC5623B28D3460145D823C574DDE0CCED7713CE3ECEA6E4CF2F64F75E1270669671362B9379767A24914967139C62E7F08ADFEF097C860C334CC2FC
	7D56C0FCC581EEA3C09C561D855E4E52D97893CC11108FD4086BD0BD8739607AA117FD7806F2273310FC1C2E3364635CD3986C63205F9670D13EDAB8370F734B42E09F672999BE4E7E22F0BE0E6079CFC5089B313B7C815FD4F7A5A053778D45AC68FCB7B847A5B77ED03B343A7A949C6E276B4B04FCF9C612EF09AA5FF9E330FC87E65C9F7D08F1EE791EB0864B17BAC33EDF984349275CB9AC541E1D5140D74C59394FCE512F40286D978BFDF64C53CF6C932305341FBF256E672EC25A4F2755F635C6EA172A36
	2BB7D2BB541178BFE2123A6BC128B5AF33E4D8E3FE9F5B27CE03196771E2EF56B6A47E7C51448416E4E6EF0ABD5A4C12F023B533E43E8F1BD9A26C51D3DEC5BFF8DE4270AC5D7F9E7B9323DFD890B6F986B6D129C917599ECD08A43334000F8FCCE54C707D7E658B27DE0A79C93FBF113DE9E67BC7G53A7797849A4B970F7C44A4F7CCDAC550BB764F1998247FE7EC8764B53AE4942F2C7FA7127F2FED9F2345119671759DB9624D4095FD9AB3B6C7EB13E99B4DAD8129B7FC38216AD2ADDF677E825B8G9B3958
	3A36C134E8BECDBA60B707730B236855D1F23E392F64D0773668C416E0E2E9AE0F5DE1328F036BE0E6297218CF720FF9DC0EA9EFCBAB9FA8E592E78AEFE9A641F087D471D1D8BDEEFAAA53B7269EDE1E9117E1427FE44747AAF9CB999A6855960E0EB13B8FC8C9BE330C7F410247F7D42D7BC821FC6CFB1DF9D79318CE9192E7A7F54AAB33B7C76C63CE57B43C9838274D67AD830AA2AF2C1A8AAAC3EF82F9649F3D7C74A3BFEE0A5F8E0319D8B27F8994301499E8F5EEE2FCC8725D37664103FBD644FE8CB97622
	38B4F2AA7888EEBF7E769386AF8E12192AD456751FFC3A6A41AF7D77E54EFA202B4C0A44A4C1574AD149B3DE59B6B262F4CB6AGB0E3B07CC1F5F8EC238FEA13BD7540DB5D4F7FEE88AC4C4092472B2A783F557C5F9A7C371AE32AB9263A8626AD1E4D79A738FE0AE466E6498B39E8C3D37816A5EAF787991650531ED996120868FD6ABCE1A90FB75D4F7FADD8B0EF582BB00827A3D320343DB30F9E5D97736E497CC46DC482CBB12139383CE3F633B2719636E708EF8E68093BE4169918945E9756D442B0AC3883
	817CB062C5ED1F57BE0C4BAE55E1775D60A3A08F25D7D86A08EFF4089310D942955395A04BCBD8FD42425264B13C8C0976AF333C4050D60C83148A31E2596FG3C7C8F8816BD7B6CE97B780BAF2C39E648CDE089306F0DC9E223CB1EC4AF050F9009B3A2623C32BFEB633C1D771E8C9B0B6F6CD37E0F8FDB70EF8F6FBF4BFF078AF8070D7C499FD9B09F936E4EFD5F385D65126D2E9BEDEE875E3FEF1F1625409D54CF1C45717D54C629D47E6F0A334877F5CD1F7F87D0CB8788BEB69EFFD49AGG0CCCGGD0CB
	818294G94G88G88GFBD52DAEBEB69EFFD49AGG0CCCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0E9AGGGG
**end of data**/
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
private javax.swing.JPanel getJPanelProperties() {
	if (ivjJPanelProperties == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog", 0, 14));
			ivjLocalBorder1.setTitle("Properties");
			ivjJPanelProperties = new javax.swing.JPanel();
			ivjJPanelProperties.setName("JPanelProperties");
			ivjJPanelProperties.setBorder(ivjLocalBorder1);
			ivjJPanelProperties.setLayout(new java.awt.BorderLayout());
			getJPanelProperties().add(getJScrollPaneTable(), "Center");
			// user code begin {1}

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelProperties;
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
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneTable() {
	if (ivjJScrollPaneTable == null) {
		try {
			ivjJScrollPaneTable = new javax.swing.JScrollPane();
			ivjJScrollPaneTable.setName("JScrollPaneTable");
			ivjJScrollPaneTable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneTable.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPaneTable().setViewportView(getJTableProperties());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneTable;
}

/**
 * Return the ScrollPaneTable1 property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableProperties() {
	if (ivjJTableProperties == null) {
		try {
			ivjJTableProperties = new javax.swing.JTable();
			ivjJTableProperties.setName("JTableProperties");
			getJScrollPaneTable().setColumnHeaderView(ivjJTableProperties.getTableHeader());
			ivjJTableProperties.setAutoResizeMode(0);
			ivjJTableProperties.setPreferredSize(new java.awt.Dimension(115, 168));
			ivjJTableProperties.setBounds(0, 0, 132, 269);
			// user code begin {1}

			//do this to force the table to layout completely in the ScrollPane
			//  VAJ puts the above setting on automatically
			ivjJTableProperties.setPreferredSize( null );



			ivjJTableProperties.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjJTableProperties.setOpaque(false);
			ivjJTableProperties.setShowVerticalLines(false);
			ivjJTableProperties.setShowHorizontalLines(false);
			
			ivjJTableProperties.setIntercellSpacing(new java.awt.Dimension(0,0));
			ivjJTableProperties.setRowHeight(  (int)(ivjJTableProperties.getFont().getSize() * 1.75) );
			
			ivjJTableProperties.setGridColor( getJTableProperties().getTableHeader().getBackground() );
			ivjJTableProperties.setBackground( getJTableProperties().getTableHeader().getBackground() );
			ivjJTableProperties.createDefaultColumnsFromModel();

			
			ivjJTableProperties.setModel( getJTablePropertyModel() );
			ivjJTableProperties.setDefaultRenderer( Object.class, new RolePropertyRenderer() );
			
			
			
			//create our editor for the Integer fields
			javax.swing.JComboBox combo = new javax.swing.JComboBox();
			combo.setEditable( true );
//			combo.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(1, 99999) );
			
			//try to center our editor text when editing
			if( combo.getEditor().getEditorComponent() instanceof JTextField )
			{
				JTextField txtEditor = (JTextField)combo.getEditor().getEditorComponent();
				txtEditor.setHorizontalAlignment( JTextField.CENTER );
				
				txtEditor.addFocusListener( new FocusListener()
				{
					public void focusGained(FocusEvent e) {}
					public void focusLost(FocusEvent e)
					{
						//if a button on the screen was pressed, save the edited cell
						if( getJTableProperties().isEditing()
							 && !(e.getOppositeComponent() instanceof JWindow) )
							getJTableProperties().getCellEditor().stopCellEditing();
					}

				});						
			}
			
			javax.swing.DefaultCellEditor ed = new javax.swing.DefaultCellEditor(combo);
			ed.setClickCountToStart(1);


			ivjJTableProperties.setDefaultEditor( Object.class, ed );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableProperties;
}


private RolePropertyTableModel getJTablePropertyModel()
{
	if( propertyModel == null )
		propertyModel = new RolePropertyTableModel();
		
	return propertyModel;
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

			ivjJTextPaneDescription.setBackground( this.getBackground() );
			
			

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
			ivjJTreeRoles.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );

			DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
			
			synchronized( cache )
			{
				List roles = cache.getAllYukonRoles();
				Collections.sort( roles, LiteComparators.liteRoleCategoryComparator );
				String tmpCat = null;
				DefaultMutableTreeNode categoryParent = null;
				
				for( int i = 0; i < roles.size(); i++ )
				{
					LiteYukonRole role = (LiteYukonRole)roles.get(i);

					if( !role.getCategory().equalsIgnoreCase(tmpCat) )
					{
						tmpCat = role.getCategory();
						
						if( UserUtils.isReadOnlyCategory(tmpCat) )
						{
							DBTreeNode d = new DBTreeNode( tmpCat + " [SYSTEM]" );
							d.setIsSystemReserved( true );

							categoryParent = d;
						}						
						else
							categoryParent = new DefaultMutableTreeNode(tmpCat);
						
						root.add( categoryParent );						
					}
					
					LiteBaseNode lbNode = new LiteBaseNode(role);	

					//This extra clause is necessary for the RADIUS security interface
					if(((LiteYukonRole)lbNode.getUserObject()).getRoleName().compareTo(UserUtils.CAT_RADIUS) == 0)
					{
						ClientSession session = ClientSession.getInstance();
						if(session.getUser().getUserID() == com.cannontech.user.UserUtils.USER_ADMIN_ID)
							lbNode.setIsSystemReserved(false);
						else
							lbNode.setIsSystemReserved(true);
					}
					//set this to tell the GUI if this node is editable or not		
					else
						lbNode.setIsSystemReserved( 
								UserUtils.isReadOnlyCategory(tmpCat) );
						


					categoryParent.add( lbNode );					
				}
				
			}
			
			//expand the root
			ivjJTreeRoles.expandPath( new TreePath(root.getPath()) );

			ivjJTreeRoles.addMouseListener( getNodeListener() );

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
 * 
 * Returns the correct Role for the specified object
 * 
 */
private void createRoleEntry( LiteYukonRole role_, LiteYukonRoleProperty prop_, IYukonRoleContainer rc_, Hashtable ridMap_ )
{
	String strVal = getJTablePropertyModel().getPropertyChange( prop_ );
	Integer roleEntryID = (Integer) ridMap_.get( new Integer(prop_.getRolePropertyID()) ); 
	IDefinedYukonRole defRole = null;
	
	if( rc_ instanceof YukonUser )
	{
		defRole = new YukonUserRole( 
						roleEntryID,
						rc_.getID(),
						new Integer(role_.getRoleID()),
						new Integer(prop_.getRolePropertyID()),
						CtiUtilities.STRING_NONE );

	}
	else if( rc_ instanceof YukonGroup )
	{
		defRole = new YukonGroupRole( 
						roleEntryID,
						rc_.getID(),
						new Integer(role_.getRoleID()),
						new Integer(prop_.getRolePropertyID()),
						CtiUtilities.STRING_NONE );
						
	}	
	else
		throw new IllegalArgumentException("Unable to create role entry for class : " + rc_.getClass().getName() );



	//process the roles defined value here
	if( strVal != null && defRole != null )
		defRole.setValue( strVal );

	//add the role to our role Vector
	if( defRole != null )
		rc_.getYukonRoles().add( defRole );
}

/**
 * getValue method comment.
 */
public Object getValue(Object obj) 
{
	IYukonRoleContainer roleContainer = null;
	Hashtable roleEntryIDMap = new Hashtable();

	if( obj instanceof IYukonRoleContainer )
	{
		roleContainer = (IYukonRoleContainer)obj;
		
		// Create map: RolePropertyID -> GroupRoleID or UserRoleID
		for (int i = 0; i < roleContainer.getYukonRoles().size(); i++) {
			if (roleContainer.getYukonRoles().get(i) instanceof YukonGroupRole) {
				YukonGroupRole groupRole = (YukonGroupRole) roleContainer.getYukonRoles().get(i);
				roleEntryIDMap.put( groupRole.getRolePropertyID(), groupRole.getGroupRoleID() );
			}
			else if (roleContainer.getYukonRoles().get(i) instanceof YukonUserRole) {
				YukonUserRole userRole = (YukonUserRole) roleContainer.getYukonRoles().get(i);
				roleEntryIDMap.put( userRole.getRolePropertyID(), userRole.getUserRoleID() );
			}
		}
		
		roleContainer.getYukonRoles().removeAllElements();
	}


	DefaultMutableTreeNode
		root = (DefaultMutableTreeNode)getJTreeRoles().getModel().getRoot();

	List allRoleNodes = getJTreeModel().getAllLeafNodes( new TreePath(root) );

	for( int i = 0; i < allRoleNodes.size(); i++  )
	{
		if( allRoleNodes.get(i) instanceof LiteBaseNode
			 && ((LiteBaseNode)allRoleNodes.get(i)).isSelected() )
		{
			LiteBaseNode rNode = 
					(LiteBaseNode)allRoleNodes.get(i);

			LiteYukonRole role = 
				(LiteYukonRole)rNode.getUserObject();



			LiteYukonRoleProperty[] props = RoleFuncs.getRoleProperties( role.getRoleID() );
			for( int j = 0; j < props.length; j++ )
			{
				//modifies o role vector
				createRoleEntry( role, props[j], roleContainer, roleEntryIDMap );
			}


/*
			YukonUserRole userRole = new YukonUserRole();
			userRole.setRoleID( new Integer(role.getRoleID()) );
			
			//use none if they did not choose anything
			userRole.setValue( 
				(rNode.getUserValue() == null 
				? CtiUtilities.STRING_NONE
				:rNode.getUserValue())  );

			login.getYukonUserRoles().add( userRole );
*/
		}
	}
	
	
	return obj;
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
		setName("UserRolePanel");
		setToolTipText("");
		setLayout(new java.awt.GridBagLayout());
		setSize(405, 364);

		java.awt.GridBagConstraints constraintsJScrollJTree = new java.awt.GridBagConstraints();
		constraintsJScrollJTree.gridx = 1; constraintsJScrollJTree.gridy = 1;
		constraintsJScrollJTree.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollJTree.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollJTree.weighty = 1.0;
		constraintsJScrollJTree.ipadx = 180;
		constraintsJScrollJTree.ipady = 209;
		constraintsJScrollJTree.insets = new java.awt.Insets(12, 4, 5, 2);
		add(getJScrollJTree(), constraintsJScrollJTree);

		java.awt.GridBagConstraints constraintsJPanelLoginDescription = new java.awt.GridBagConstraints();
		constraintsJPanelLoginDescription.gridx = 1; constraintsJPanelLoginDescription.gridy = 2;
		constraintsJPanelLoginDescription.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelLoginDescription.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelLoginDescription.ipadx = 180;
		constraintsJPanelLoginDescription.ipady = 56;
		constraintsJPanelLoginDescription.insets = new java.awt.Insets(5, 4, 7, 2);
		add(getJPanelLoginDescription(), constraintsJPanelLoginDescription);

		java.awt.GridBagConstraints constraintsJPanelProperties = new java.awt.GridBagConstraints();
		constraintsJPanelProperties.gridx = 2; constraintsJPanelProperties.gridy = 1;
constraintsJPanelProperties.gridheight = 2;
		constraintsJPanelProperties.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelProperties.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelProperties.weightx = 1.0;
		constraintsJPanelProperties.weighty = 1.0;
		constraintsJPanelProperties.ipadx = 170;
		constraintsJPanelProperties.ipady = 345;
		constraintsJPanelProperties.insets = new java.awt.Insets(12, 3, 7, 4);
		add(getJPanelProperties(), constraintsJPanelProperties);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	initConnections();
	updateSelectionCountNodes();
	// user code end
}


private String getRoleValue( int rolePropID_, String defValue_ )
{
	if( getRoleContainer() instanceof YukonUser )
	{
		return RoleFuncs.getRolePropertyValue(
				getRoleContainer().getID().intValue(),
				rolePropID_,
				defValue_ );
	}
	else if( getRoleContainer() instanceof YukonGroup )
	{
		return RoleFuncs.getRolePropValueGroup(
				getRoleContainer().getID().intValue(),
				rolePropID_,
				defValue_ );
	}
	else if( getRoleContainer() == null )
	{
		return defValue_;
	}
	else
		throw new IllegalArgumentException("Unrecognized role container: " + 
				( getRoleContainer() == null ? "(null)" : getRoleContainer().getClass().getName()) );
	
}

private void initConnections()
{
	
	MouseListener tableMl = new MouseAdapter()
	{
		public void mousePressed(final MouseEvent e) 
		{
			int selRow = getJTableProperties().rowAtPoint( e.getPoint() );
			
			if(selRow != -1) 
			{
				StringBuffer sBuff = new StringBuffer( getJTextPaneDescription().getText() );
				int indx = 
						getJTextPaneDescription().getText().indexOf(							 
									System.getProperty("line.separator") );

				sBuff.replace( 
						(indx >= 0 ? indx : sBuff.length()),
						sBuff.length(),
						System.getProperty("line.separator") +
						getJTablePropertyModel().getRowAt(selRow).getLiteProperty().getKeyName() + " : " +
						getJTablePropertyModel().getRowAt(selRow).getLiteProperty().getDescription() );


				getJTextPaneDescription().setText( sBuff.toString() );
			}
		}
	};	
	getJTableProperties().addMouseListener( tableMl );




	// add the TreeSelectionListener for the JTree
	TreeSelectionListener treeSl = new TreeSelectionListener()
	{
		public void valueChanged(TreeSelectionEvent e) 
		{
			if( getJTableProperties().isEditing() )
				getJTableProperties().getCellEditor().stopCellEditing();
			
			int selRow = getJTreeRoles().getMaxSelectionRow();
			if(selRow != -1) 
			{
				TreeNode node = 
					(TreeNode)getJTreeRoles().getPathForRow( selRow ).getLastPathComponent();

				if( node instanceof LiteBaseNode )
				{
					LiteYukonRole ly =
						(LiteYukonRole)((LiteBaseNode)node).getUserObject();
					
					getJTextPaneDescription().setText(
						//ly.getRoleName() + " : " +
						ly.getDescription() );


					getJTablePropertyModel().clear();
					LiteYukonRoleProperty[] props = RoleFuncs.getRoleProperties( ly.getRoleID() );

					//sort by keys
					Arrays.sort( props, LiteComparators.liteStringComparator );
					for( int j = 0; j < props.length; j++ )
						getJTablePropertyModel().addRolePropertyRow(
								props[j],
								getRoleValue(props[j].getRolePropertyID(), props[j].getDefaultValue()) );
				


					//if we are read only, dont do any enabling/disabling
					if( isReadOnlyTree() )
					{
						//do nothing
					}					
					else if( !((CheckNode)node).isSelected() )
					{
						//always disable the property if the role is NOT selected
						getJTableProperties().setEnabled( false );
					}
					else if( getRoleContainer() instanceof YukonGroup
						  && getRoleContainer().getID().intValue() == YukonGroupRoleDefs.GRP_YUKON )
					{
						//allow the Yukon Group to edit any properties
						getJTableProperties().setEnabled( true );
					}
					else
					{
						//if the ROLE_CATEGORY is SystemReserved, dont allow editing
						getJTableProperties().setEnabled( 
							!((LiteBaseNode)node).isSystemReserved() );
					}


				}
				else
				{
					getJTablePropertyModel().clear();
					getJTextPaneDescription().setText("");  //clear out any text
				}
				
				
				//this must fire here because the NodeCheckBox only fires these events
				if( node instanceof CheckNode && !isReadOnlyTree() )
					getJTableProperties().setEnabled( 
								((CheckNode)node).isSelected() );

				fireInputUpdate();				
			}

		}

	};

	getJTreeRoles().addTreeSelectionListener( treeSl );


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

	private CheckNodeSelectionListener getNodeListener()
	{
		if( nodeListener == null )
			nodeListener = new CheckNodeSelectionListener( getJTreeRoles() );
		
		return nodeListener;
	}


	private boolean isReadOnlyTree()
	{
		MouseListener[] lists = getJTreeRoles().getMouseListeners();
		for( int i = 0; i < lists.length; i++ )
			if( lists[i] == getNodeListener() )
				return false;
				
		return true;
	}
	
	
	public void setRoleTabledEnabled( boolean val_ )
	{
		getJTableProperties().setEnabled( val_ );

		if( val_ )
		{
			getJTreeRoles().addMouseListener( getNodeListener() );
		}
		else
		{
			getJTreeRoles().removeMouseListener( getNodeListener() );
		}

	}


	/**
	 * setValue method comment.
	 */
	public void setValue(Object o) 
	{
		if( o == null )
			return;
		
		IYukonRoleContainer rc = (IYukonRoleContainer)o;

		//be sure the rest of the panel knows what user we are dealing with
		setRoleContainer( rc );


/* *******

 RoleProperty has the DefaultValue for YukonGroupRole or YukonUserRole.
 YukonGroupRole and YukonUserRole have the exact same row count as RoleProperty 
    if the user has the given YukonRole.
    
*******/ 
		for( int i = 0; i < rc.getYukonRoles().size(); i++ )
		{
			IDefinedYukonRole dbDefRole = (IDefinedYukonRole)rc.getYukonRoles().get(i);			

			//set the selected node
			DefaultMutableTreeNode tnode = getJTreeModel().findNode( 
				new TreePath(getJTreeModel().getRoot()),
				new LiteYukonRole(dbDefRole.getRoleID().intValue())  );
				
			if( tnode != null )
				((CheckNode)tnode).setSelected( true );


			LiteYukonRoleProperty[] props = RoleFuncs.getRoleProperties( 
							dbDefRole.getRoleID().intValue() );

			// (none) means we use the default, dont do anything in that case
			if( !CtiUtilities.STRING_NONE.equals(dbDefRole.getValue()) )
			{
				for( int j = 0; j < props.length; j++ )
				{
					if( props[j].getRoleID() == dbDefRole.getRoleID().intValue()
						 && props[j].getRolePropertyID() == dbDefRole.getRolePropertyID().intValue() )
					{
						getJTablePropertyModel().addPropertyValue( 
								props[j],
								dbDefRole.getValue() );
					}

				}
			}


		}
		if(o instanceof YukonUser)
		{
			if(((YukonUser)o).getUserID().intValue() == UserUtils.USER_ADMIN_ID)
				setRoleTabledEnabled(false);
		}

		getJTreeModel().reload();							
		
		updateSelectionCountNodes();
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
					"\t   (" + selected + " Selected)");


			//let the tree repaint itself
			getJTreeModel().nodeChanged( currParent );

			getJTreeRoles().invalidate();
			getJTreeRoles().repaint();
		}

	}
	/**
	 * Returns the userID.
	 * @return Integer
	 */
	private IYukonRoleContainer getRoleContainer()
	{
		return roleCont;
	}

	/**
	 * Sets the userID.
	 * @param userID The userID to set
	 */
	private void setRoleContainer( IYukonRoleContainer rc_ )
	{
		roleCont = rc_;
	}

}