package com.cannontech.dbeditor.editor.user;
/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.text.html.HTMLEditorKit;
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
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.database.db.user.YukonRole;
import com.cannontech.database.db.user.YukonUserRole;
import com.cannontech.user.UserUtils;


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


	private int userID = UserUtils.USER_YUKON_ID;

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
	D0CB838494G88G88GD30C18AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DD8D45715A6A1EAB6A41A8D6D5AFE26EB0DE9C942EE6D56ECED62EECDEB9A5350CD767BF4EBDB5A0FECECEAF7CDBE3EAFFE296DCAF3199F999090858D712702A1099A64C744A41424A283820ECA10A8A8C69FB38FE6E01899E79EB220421EF36FBBEF9E438CC4D36A1743FB775C1F73F34F3D771C736E04656F19E7CEB364B316E6E66A3F775398EB31B1B6D2734315043B5C18EAE43AFFC5GDB58
	597DA6F87A20EE557D2946DC760BC28B14E3209CF7EF2A718DF84FE31772CC8D709205CED2BDE3CF479C7B6D44F43EDBC8F452F9F9FEDD96BCF3GE682AE81E0FDA352FF3C2EC045BF8F659C759DA14D405842A6ECB3AF3DC0072FE9926DE260B98721995B2C2C5B25562F47BE00EB83F0B60B3653601941393B337A8335DD67FD71EC5D0FEB8C59C8CBB7BEC1325A57F4C2BC8B59B22E88A3BBE52EC1BEDA7AFABE36FB7BCB6D12476375A8325DD96A68129DAE456BAFD59F838159DFDD7DA97C35F9FBDC1EE3D2
	C0EE11BC32DBB901BC7FA85D42470331204CEC087B5802FADA8C656781561CA03E7FB33BC665AB2EAF1EA5BFD89615EF2BCD706B340578DEB1B75DD2F092477FA31F876C978F7549GC22632B36F5F34CC2DDE37900789F99A2CA4CF67C912671D0DC8AF8E4A89G4BB47C4B9C1F8465F5GD927C84E0F5EA5B95B2FFD172DFE42E75C9CC54E62D3C2BE5B29101C0D3352ADEFF2FB5BE5C01B1989F589GCB81D68318CE279A0B81F6F2BB7FE36181F856F6A9F55E7EFE2F275667BB66F678CFFA9D329B5E2B2B21
	C645354AF62F5F41183065E753CBA2702359676931F60CA0E4BB47487692DA7F192D683D42F59FAE5B22159F9BDA54B19C27C3324DBB0DFA7B97EE9FB1701E260EAB705F677855BA3C18EFAB47F37AAD29C6C78B6939BD07F8311966335C8A49109E0117B9BFB8EBD85EA2F8C8EE8971A2F47C1A91F51C75C92AF19F40D100F600A0401CCFC8470DC515136938896C4C6F115CE7863AFC12DFB9B660F2BBE43F2AEB13F5A2DDC78251EF6BCD7793FA092F544FD152F7AEC754CBD2FC3DE151A7C29F091F0474A2E8
	1F08CAFB6DA763EDA3C71B432F72B541DA459C9AB57C820E4F5561455C16F0BC168B21AE86C0341752913FC6ED1F260D534B716BF4F8B14E2B9BE9CFE0E740C6G447C4619F1CFDD8E6555G9BG725BD20D0781DAG9CEDB447779715FD4EF554221BDF5177ED43F6F84AC145AFB5F9820A64314BE7A53FCB6AF24B812636A965F350B526DFA5DF5FF620F1DC7648FEC9F1F9BDB03A54AF03B185C41B9A4396EC6317F9ED1B2BDF86827DBEC663DA8DF970F4CB8165D31FC3D264D08BD1EFB320CDDC154E4208B0
	G2E4DB65457FF73FDBB865E6735112E1E5C083875D87FA96965E943D6F81EG63165D5555D6179D59107CC3E4230BAD3970AC067673G8A4F269A634F527AFC251D6C501A7DCF2CE4652508F608D0F2CE581475DC389DBE4547F783BE96A081A0719C1D474D16F6323DCFA35BA4827203FCCD36F60A434690B46C9A8D73A46BAE7DF32CCFE3D8D9683D0C6FEFEBC1AECBBB6CC36D24FB0331D058A41E39E405277DE0C032A3B493226D6B7C0C112F4A9E45AA7BE40F83DE822A8DECB4E5046C335B2B59CE964763
	FED58786945070DBCDDB849E4650DBBB556F6075122278DDDD830AFCC6D6941727A754FF2F89F9890C598A832A2C9599A86BD110319D4029497A28A9CFE7E7B62F37EF405706ABC83557AEFEAEDF0776F16DB47F73B622FF36D8ABBF424B217AC39BE96EF254F6B73317FE3EBD7A44989A61737B572151038F755D27FB7F7D985AFFEDD04F20742924A1667EEB6668367B10B9326DBEEA0EEC3B5FB6CFEC1B5FB3C732CDD137540CEB5CBB6851597AD3E65C7F6CDE0FC7668BCE6B42044F79CA9B4A61723814F3AE
	C70F2C1CF18D4B0CEDE8C71B7DBD5FE3D7407B1AF65A93875F22BD31F878E13674D235A1127FB467218E43518EE12FED9D21BDD17074A3FE5649EE399F94D5679D70A8821F4877EF959FB0F25C4937BC58F7DFF39B683041A53B9D6875B6D9CB6C303C15D27ED71C9C7BFA252BD230B4B0887AAA6513D1DA073575783A3F7060410F8B478781A55DAE3F5C64718D50A07B4F6EAF976BA00FFD5C2C885AADB0C30360FCF27AD418C28E8E897A0F5C980EC03FD5D606FC725F000BCA5243745C2B020FBA27EC6FBB66
	8D56FBF0E763A314C85C2CCA4523C576F7FB7D7D3203B3D1AA8DAAAA87353C7A26D9282824B3461109B62300AD4D84185721793669D8CF7B2EFA3854F95DEEE16ED0D7F01E5B32A5DFE71F0BBBC47CEB02BAD1BF1C91297E32D67FFF916B952D7EFAE6247A1967094FAA6E878464AB83B238D7283E93729079C8162DE1F5E7A577C0487732DAB641532DBBD3847E7D0C486B68238CBCC77A3471EBB3B6B274214418E25DAF14F0CC1F5F558FC7B09E12EA1CBFE4554779F301FF4C89A441B385C058438FED98770E
	0DBE9933DC407E39960ABF4BB45C3B9C77AAB03153CCBE7E49DCFD8E2060751566C87B88026D0258BFAEDF880FFD3E3289471D86785997C84FCFE4684F615AC040EBF7F157D355D9EF26FE0F91B8EFE6A6BC417C829087B964A069FDA093F9C55FD4C52B3AFB743D0F58F8DF3B1E07666466548DF2E3EF735601FB5C88D3D727ED41E7DD72A05AB20F574BB3C8DF8BAE509EFB23C11FE7D8327938E1D3945DA429BA592053CD723D6996B176373531EBBA41D6B96ED0A661948D779D8D1750C939082FD4E34C98A9
	97E167C217768C38CA879497BA5512A271F52B4FC33C487D9DBC03D7F7D23E415108763A6BA244AFG360BA46F39C3FADBD81A5C9071CCC1F0DC94F286AF065B42FB59C8AF8E7071974996D2B23768EC219534AFF9FAE075E86776A623D8A7EAF97EA6B32D93C67EC3D5464D4430CBAED2AC624C417D60F9A82F85B0FD16EA5C85D073997136BFEB52183CBEB8C1CC7E2FB745D767E764D33E1FF9B37DFCDA3F45B755AFC66B37064761F3BE8B792E893A77251F0D77E9C51C3C0A5B4A0A4FC41C1C2E61D7F37CDA
	9DDE444F3D9C0FE553A5704BAF51BC4C580CF611886524CB247BBB3232F5F65162F5F9944DA64A33F12F747ADDF0B4912E0A636CDEBFF72C04BFEF63EBEBAD0CE939CC637AF2227B0103B9117D409BB9117D40BBF2A60EE16E4E0966A78A7EFE2271378B789338D31904AB57F04FE9B82B06FB1D2F5DF6A87BB4DCF7BD0FDF20BCDB43D59CC75CA2A8A7DE26797E951F0794A8DB24D4E309C4787F6178C6A83B81E6EBF8DF634D7867B8E634BA24D9A2C50A016F3AA99A934549C6B6BECE9673913B996DB12815E7
	73729023B3C32E45360A64879F3B5E63D00DCE1C0FF6BED7F9F0E84FE204DB4DF195007B1D061379DCD5822EAC0DF0AFF15C9B002BE3642FED2F4773C271FA5DEDAE9F7A4DEC11045F870EE21AC7EDF3239EED5AAD8DF987008FC7970E95D7CFE77FFAE81F8B388FGECDDC227C20EEF8F62585DFE299F4C6D0CCF363323F66C5BF81C7AA6C07B25G49G8C6A8A6D64B72457208DB41F317B41BFEBEE734B3240BF70B6B2564C0F8F6E93DA65005D6F728987CE3499484E555A34783DBE592F38640070D9F6B769
	FD1619F691E3AF0267F2G5126E61B3ECD325A4664G7F4DC1EDE60DC6EAD38E75D6871DD54FDD501FD539DFBDE220BC5E38BD5721FE83F0044EAA31D7AD2927DC5FDC28DB6120B33762B2667A7819CB6EF3F575F13F4BF1CC6A31711992E7EBD703C56D1FA55359BA3B814734C23993C011095F60BB64CBA4FCE3BE33CD3B9C711BC5F25BF1C32CAC78DCA807786D1CC53E0467B851DC8B75455D081BFBDC6F7362BC7D4C40D8A9BCC57DFF0C29477DEAB7535733C6FDBD727B1ED1DFFFE8CCBD6A68DD353F106D
	5937C9368D4FAEE46E47CEC57D9E636E96B24DD61FB3030134591D960B50FBCF7DEE3D5E5558293ADA35D434B9C67E5A27CD64ABAD6FA65D7FEEAB4F997700AD81147750FC3E3A3FA072383A6FCF4AB672139AFBE83CDF3663F85320BC87E0E18F79E9CF0FD25B65DA5B1ECACE5B8934814A1DF46E7D90BFA32AFBD14C763761F9D66A0D6829B683EB273BFA41BBEA13FA986D19058D371673AF3D45FEBDB75DCF788326637CDCF106675933B93E4D991EE77FF2832D3D4BD037CC53570B85B8D77BDC305681BA81
	9400B80005AEBA5F93371733C9FC36B62F4F868E00DBF7E48B798247EF6DDB481369E4630B9EFF04AD1E1E93B596D86692363DDAFD968E865270A9643B3A83654B6F859B81E883F083CC831853CB72653FD1A264C38329D594496E64D192890701CB880D1B3F10AD37F9A25992FA2379F8FDF9B897CBFB433F358CF07C733D615FDAD237523716E4283376118FB134CDCFBF9E70CB7AC2B41F6F0BC67F138E7E3D2EAF1C7E850EDF5797CE7F45F622EFF2030F69A67A0923FA7A0900CFF607E82EF3C7237F8B3EF6
	AD7D6174FF4D71857D61747BAB097EBE280B6DA77A194719360E72F46FF39B26BE3F7A58145290F63936A03ADF7DEAC1E43F7A7785117D6A578BA64E2F263F99BD3F1A7326BE3FAA5655121DF8C62540DCE4F9E0ED81D8815ABD3426663E391771B595E1BD55BBFA64BAAF7F6226907D519D51E51D3EE3726FF433F6E0AE234B2BA85EFE9C1F70DF5A017325F8FD9CA9F0DF5D01BA02D047290826AAFE5E8EBCE75CF2B7C78BDF7F5DE67275958F595D95F4D959B22DFC0D17133432B1EFAAEC8369056CEF571450
	206FA0C276597C0C5EGF4F33D947B9467A36EB0149BB55C035BC8C7EEAF6960DC876AE409D616F8F915D67EED7BE47CE35FAF36C68D9D135178A25FFD046CAFF25FE483701A6BA3FD9C3C46F58465C68DF7F9D453110FF470C2A55711D67EDFDEDE25158315136A4877C5F594BBA90DAF22A3312E166D0C3E361F5DB9795ADE311367AEE1CD368D79E4017B59CE1E2F686E8E485A9E722B1D399A2EA950628D38B012AA3F027A2FED35CEA2239EB600DEF3215FDBEA39985EDDF0A8BD18C6F1596DB65CEFF8B007
	4960G655B9372743EFB907AE16CBF778A45025F5C06FCF7F97DBC29A1624F7F52E2340550EEB900498FFE2DDF648CC43FBF670F0FDBDB7329DFB934ED84708144287D045C0FEF1BCCEE7384F24F758B392FB012FBE5870FC7C391820FD9C59C79779D6171A872918FE3AC71139CFF989DAF7F96C67DD678057CEB614982A2DFA27AD92F0D173FE8047A9584047C65703486441A90728F0ECEA43FDEF6FC6F043E3EC0C87E98F87F94647F0E0164DF668E131FFF1890726FBA9CAEBF72BF9B46D8G300777873F07
	057EC542091C45D3981A293E708BD9648B6FFA74C1E67DE90E81790B648BAFD1E54FD2C2316C62E7A85EDB580641D104388C2D187257F99467D9E18C755E5EB6723F2E83EE11C2F15E3FE516334F19ABEFF3A9EE79981FF5753E5EF60B2A73358A45F4D7387FDBBC10EA2C81E89BA07A4379943B388137E000683FD43A130D4B9B88A2A2ED204A7403FC0A61138608D68A9FB37FEA2A3184408AA074DE06D322767BF107F62770AA752BB84F6F94C2B981E059D56271C3C6BACA82DC7EA0452E37B11253B6C8E38C
	8F73FBF4D01E8B30E81046E89AA1B99381E78A5298770D907FB5C19A637B3C2D8265D8008541C876157217C8B8A1672542101CED07AD2A2DAD5146FF2C0A4782D04E9ACAB5968FD13CF8328857D38D145B812E8344814483AC85D8813086E083C07EB0689660A8C09B0093A088908BB0EF187C444CBF6DE7D1E2F4C02AC9A06EAC7AA5112FD6736BF99345DE0B06471F0B6A1D3E6D1345234B07A365D237CE5A6FCF91691D607DD6C760A7922C9F26FBF43F66EBA16BDAF8FC7F8A4797DC8B0F6F877348F60A21EE
	66B572A126655FDADE625EDB6C7774CD7793F2B9789E33F61C3C7D9C3FFE1C3C2FE9FB0269BA5856F51277390E0968A7DE0F9613BE4457F57275701874DB9C1FF2BDBCA6FD7FBC2DF7F6A3556838C1747F42EE4D3ECECE5AAF32FDCD677D165F78FC7615F4036C6B953E87255C8857776FB8FE430DF0FDCF9BA6FDDBC660DC9FA1F9CF0F5C1A1DDC3C45FEDF3E69FEC22E4AD11EDB99891737066313C64265FDCA5B0B57C01DED14646DB8CC749782EE99402A51106E570EC6332F7DC7F83EF2DA38FDD5F2FC6E34
	F07BFA22CA3BDF8BF5B153E83F140A227B524E2249FD69ABC5116364412271F172752248F1726DC57AB8D960E694619957837BF53B56AED643F5E8387B781808BB67F2A8CE8D7F8F1CAF44B74AC810708FF03E86B92E1D3ED76BF09DAA6E1BC5689F0248CE2F1F7AA694E1BC556DF23BC5F9A1EF6372C0F8206963B14E234BA76978FEDC43A15F6A3741BC0A5F5640DC6C66F333B4AF1B0F67F6F964BAF19BD8EB5F457D1B2C5B606C3C0DE2407B37A06E83A85BEE53E2BEBC1ED55BDE27FB3FB1A571FD58DA7A9B
	50685D9612A920FB5F10BF753437ED1BCA9AE2CEDF6AA03BE85766745173E8F7B1DA79B1DEDE2015BFBABF95FCA13D106CE7270446587C50835C2FDC86F413B49BB78CA3AE854A265BC95EF54321F9CF553DCF9F1EFA9B78473F810D8FC7C3B29D533DBFBBB27534D7CFA98DB127BE9E9F6442FC9E3E1D6679990EEB0432CF433DD545FF1BG65B99A2E370A56C73CE68FE5C7F0BD2C504A9F72F20AD6FE32EAB219306F97535B4FA72571C572DE62EE40F6ADBF13FFC72AF19FC0A30093E0669D1457985A0E6721
	0261F1031747B72B6F60B19FBFBF6261FD11567611ADE6EA4BA8CFF085D3812CA606FFA76AA81C0027FDB1C2E7E22C9782182762370B35AC0E38F9A072D8AF1D576731E220AF66EE93E20817C5ACB25F09FCCFD88A6D923536EF8E0F67BB2118E93C4CB80CF9092BF83541AE39C5EA651864EF713ADD76A1F55C795C5E8E82EC5046CDBE0CB1A99C782EE1E0E1FCCF62E9836FDB0BFA3A137AD6F33B3C6A1287FDDE3FB273CE7EDD2C8A6D70BA3C8BFD5ED6B511BE936F546BF3234C73C2BC9B94FECF237748187B
	95508FF33C26699077CF27983FA61F72846553691E01129FFE4F60B7E97AFB825F488F3FE700BEE428FED5FE78BD0393E37AE7EE8B3FE7C07D459C998E6866E88B7E3656A38F96EAB71CF15E776D2D8587533D372430827EBB3C1FD14EEA5E207E3E4887193628F79B1D53059E2F2B4FF58F8752844D9D6751DF2B1371B7FAB46E202C4F056D5A13E95892E55CC56AF84953C339B0125348FA2F0D499D69F2E16A0AD0F3A33BAF505D037C9914A3BAF0B02417570676C6560B8354626D8145142B0A916D3FF73882
	1E7A2B6A3ADCE269597A5636263ADA5B25B3F52D27ED36E3352D17EAED67EABB4EE874722AAAA7685FF83A35697C69D3ED11C6907DFB2A18B6C79F497AB9AA3F365DE00C22CB5B8C21434E9961F37435AA7D9C11EE235C7F602BC368F57E9152EB6C8C4AB9854C9873AC03F292403A99F49E540E49A9E44DCCB5964E1CF87F97392A9D257CBB8C34ED19C9715371836F316879A6F5658E89AC3F0D2E0EF5328C77829F0C938B3090E0B9C09240BAG4BDD4053DDC4631E837BA620A1921421141610F16F85143166
	2E48F235G5E91D6A7789AAD47BDAB28516EBF38F7825A98251CD17CB09BF48EFE7388520D057E73A250DE9481A762565F7039DAFED7F8BC1D4271AB6E8A0F27074C94CFA7C31D756F48BFE8677BDEBC14F1EF92380B9C3794F0385F89397614A2ED6D7B35189A356E1DD25C477037BC3066DC437C5A3B10D2543717C50F19AF15CD9EB3BB4BA2474C1E3271B1F320ACF24CBCD4B6BEE69ED147C5FBB8A575EB634614E1DCA93EF51C14BCD28F7F6EAEF473A15F0F12C0B79BB49DDAC648474A3D1BF0157CFB63E1
	A8B75E2D5D7FAD47359AD067DF347BF73E8EBB6F9673A5F0C99C9784DC1CB65E911366475DCD6B72E57E7FE6906F2F695EC74DD36F774E19D29AE29F135E498A5995CFD7887C2EE0F688CF698AF5FFED390067114395C08BF260E1D267F5E3CAC275C5FA797CA5028ED78188BFF10F769DEBBD6032E2D30DA5GB6GF7AC79AB4D23629B28CB410145D852A8FD9708555A2DBC4257490062ED15832EE199EFE991EF2F9EC13EFDFE39DB767BE50778491B4AD7567BB856BC98A7A11678BAE9A23ED681EEADC056BD
	409B006D9EBA8B6B8FB3CDF6B6026BD0BB87396025DDEE495EA7767C7A9172F15C77100FF3E3A45C47C13FAD6423FCEBE4E29F670511F09F6749B1BECE5EE8B49FC7709C9CC5DC37648E04FCD3E7D5C82677CC788DE54C79EEF5754B9E7CD1763E0A0A2A120AA39F6CD7FD05000964DB20497728A9DC3E1DE36E0A7E52B431FC1B4D6172E50C112F569CCDBE753756E37225C940D74ABD931F1D22DF50246FE73A97FC55FBA76EA77689E9047673A7357D3CFB047673A12D5DAC93354B505A4519289D6AC87C7FE0
	1EFE678390AA61E5E606B59670334D5AB4B471BCDE72554AEA630B8F9A8D4C5444B60ABD3A0919F023EDE2E63E8FB7B1A36C51C11F2A9FBCAFE1F816953842C6987D1A023066B1D823DA132530CD3A90C9E119A19F9F184A9E637BBD75752A1792FFF5B6E0E49B1B581B5D00B9A37B7949A4BB70B7C36ACF7A9BD8068FEF4363B2840E837CE18EA8C3EE5946723BDB71E7F181C5F654521997D058B69BD2AA43EF2AE50DD24049B703BF5B1849037F738136D5D43BA5CFCF19B8G7F4C45CE2B3D0C964D27A98D78
	4D637C62A8255AA8F9CFECFE3B28F975E2A3B3B430CC37D7F2B4C8F6F09D1AD80662744B8127576D887A7EF2148FE4994419429B19A698EE27A6BE8ADB0A1B1E46F4DF7001586CB5598D205B201F1571168AB4A855DAB86A1C129FC8497EA6857FE78ADEFF31D6F75B1F14F7FEF27C0DCE83CBD305445949887A9421B04DA0D064528660DA79794B4F4C7FC5CD4ADA6852404C7CC7CDC0B8B7546A54C0FF176C1F1E70255D1B62126FC63EFC289EBE6F598A5B58AD753B5CC370E2659E3C069779E6DE5C02DA200A
	8CF09DBC817C79A7F0D398268A2A52A4AA3D7F1972AFBD7C0773DC22D0D7056D91F387DD4BFAE4EFFFD9EDF7374BA3EB8340C442703B356131CDE9D81B2D417B36B52FFC3D8B8C4F4A0C7D65657CEF857FFB887FD6F0CC8547D49C0259CCE1937E8B2F8F86D1666DC1AEFAFA18E18905EC9AE7A1C6C4EF96FA436678122F2997CBBBF865AE7EF74F2D71D49C846D640CBDFA529EFA630E5CD9158386E6E940E9F57B1CD213EA92B636290BEF8E68093B95169B52BE6F8BEB4AE0078537B30437A3DE541E71C9F6DC
	F6998E495F47C7C09E76DDE0995D7E1EAECEC0E1068B8D9700ACAFE175819B4BD41CF871916DDFE1EF0406EE41B8C02D90AB163D8E60637FC0302D634F1E5AFBFF765304CB56FC83B3403E671445C6E79EC4AF050F900FB3A2623C7D237AB8EF7D1D872346623B62543BB3F1613FB3BCF9007B2F001F99C77E64837BB1900EF63F7ECC3F64F6AB123B2F5663403B76470694B9F45F3475G0E9F20B6EA25286BB800FC776A7A7CBFD0CB878830AD5971C69AGGF8CCGGD0CB818294G94G88G88GD30C18AE
	30AD5971C69AGGF8CCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG009AGGGG
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
			ivjJTableProperties.setPreferredSize(new java.awt.Dimension(115,168));
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
//			combo.addKeyListener(new java.awt.event.KeyAdapter() 
//			{
//				public void keyTyped(java.awt.event.KeyEvent e) 
//				{
//					fireInputUpdate();
//				};
//			});			
//			combo.setHorizontalAlignment( javax.swing.JTextField.CENTER );
//			combo.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(1, 99999) );
			
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
						categoryParent = new DefaultMutableTreeNode(tmpCat);
						root.add( categoryParent );
					}
						
					categoryParent.add( new LiteBaseNode(role) );					
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
	login.getYukonUserRoles().removeAllElements();


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
				YukonUserRole ykUsRole = new YukonUserRole( 
									null,
									login.getUserID(),
									new Integer(role.getRoleID()),
									new Integer(props[j].getRolePropertyID()),
									CtiUtilities.STRING_NONE );

				String s = getJTablePropertyModel().getPropertyChange( props[j] ); 
				if( s != null )
				{
					ykUsRole.setValue( s );
				}
				
				login.getYukonUserRoles().add( ykUsRole );
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
	// user code end
}


private void initConnections()
{
	
	MouseListener tableMl = new MouseAdapter()
	{
		public void mouseClicked(final MouseEvent e) 
		{
			int selRow = getJTableProperties().rowAtPoint( e.getPoint() );
			
			if(selRow != -1) 
			{
				if( e.getClickCount() == 1 ) 
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
		}
	};	
	getJTableProperties().addMouseListener( tableMl );




	// add the mouselistener for the JTree
	MouseListener treeMl = new MouseAdapter()
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

					if( node instanceof LiteBaseNode )
					{
						LiteYukonRole ly =
							(LiteYukonRole)((LiteBaseNode)node).getUserObject();
						
						getJTextPaneDescription().setText(
							ly.getRoleName() + " : " +
							ly.getDescription() );


						getJTablePropertyModel().clear();
						LiteYukonRoleProperty[] props = RoleFuncs.getRoleProperties( ly.getRoleID() );

						//sort by keys
						Arrays.sort( props, LiteComparators.liteStringComparator );
						for( int j = 0; j < props.length; j++ )
							getJTablePropertyModel().addRolePropertyRow( 
									props[j], 
									RoleFuncs.getRolePropertyValue(
											getUserID(),
											props[j].getRolePropertyID(),
											props[j].getDefaultValue()) );

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
	getJTreeRoles().addMouseListener( treeMl );


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
	
		//be sure the rest of the panel knows what user we are dealing with
		setUserID( login.getUserID().intValue() );
		

/* *******

 RoleProperty has the DefaultValue for YukonGroupRole or YukonUserRole.
 YukonGroupRole and YukonUserRole have the exact same row count as RoleProperty 
    if the user has the given YukonRole.
    
*******/ 
		for( int i = 0; i < login.getYukonUserRoles().size(); i++ )
		{
			YukonUserRole dbUserRole = (YukonUserRole)login.getYukonUserRoles().get(i);			

			//set the selected node
			DefaultMutableTreeNode tnode = getJTreeModel().findNode( 
				new TreePath(getJTreeModel().getRoot()),
				new LiteYukonRole(dbUserRole.getRoleID().intValue())  );
				
			if( tnode != null )
				((CheckNode)tnode).setSelected( true );


			LiteYukonRoleProperty[] props = RoleFuncs.getRoleProperties( 
															dbUserRole.getRoleID().intValue() );

			// (none) means we use the default, dont do anything in that case
			if( !CtiUtilities.STRING_NONE.equals(dbUserRole.getValue()) )
			{
				for( int j = 0; j < props.length; j++ )
				{
					if( props[j].getRoleID() == dbUserRole.getRoleID().intValue()
						 && props[j].getRolePropertyID() == dbUserRole.getRolePropertyID().intValue() )
					{
						getJTablePropertyModel().addPropertyValue( 
								props[j],
								dbUserRole.getValue() );
					}

				}
			}


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
	public int getUserID()
	{
		return userID;
	}

	/**
	 * Sets the userID.
	 * @param userID The userID to set
	 */
	private void setUserID(int userID)
	{
		this.userID = userID;
	}

}