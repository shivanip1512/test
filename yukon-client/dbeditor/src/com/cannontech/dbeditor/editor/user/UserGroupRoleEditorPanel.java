package com.cannontech.dbeditor.editor.user;
/**
 * This type was created in VisualAge.
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableColumn;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.table.CheckBoxColorRenderer;
import com.cannontech.common.gui.util.OkCancelDialog;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.UniqueSet;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.user.YukonGroup;
import com.cannontech.user.UserUtils;

public class UserGroupRoleEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements ActionListener, PopupMenuListener
{
	private UserGroupTableModel tableModel = null;
	private javax.swing.JPanel ivjJPanelDescription = null;
	private javax.swing.JScrollPane ivjJScrollPaneRoleGroup = null;
	private javax.swing.JTable ivjJTableRoleGroup = null;
	private javax.swing.JScrollPane ivjJScrollPaneTextPane = null;
	private javax.swing.JTextPane ivjJTextPaneDescription = null;
	
	private JPopupMenu jPopupMenu = null;
	private JMenuItem jMenuItemRoles = null;
	private JMenuItem jMenuItemConflicts = null;


	/**
	 * Constructor
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public UserGroupRoleEditorPanel() {
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
	D0CB838494G88G88G50042DAEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBB8DD4D4571934EBF3BCA7699EED6D9E1A550DCD5A2CC94D46F6CD4359189E5C1A2CD1DACD57B459DD13C39BCF6A662FB43A2D693A6D9D9894959085E5900461C78501997EB4B800B27C03434F8AA2D8F37441BCE074B1B31DF948905DE43F6F5EF75FBC0799D00F04F3BE5E3B5F7D79FE6EF76F7D3E6F3EA1E9B9CB63F531E9046862097277090E107AFAC28E9CF0A6F35C35C41D1EE87EF2818E1035
	3508F4C35D5A73BAFD8AD95713G65F9D8EE55695361BD15EC68292E011790F4D2206E9B5DBF7B454CF42E8A1C4EEEDAFE39A3911E485CFC40AD87581108745F6AB0A8780DD0CED65E91F431042CCA42B62BB38D9AFCED92EB37G1E5F85D8110CEDBE6E48D26A75D04E8210G4212D95BF0F86E215CFD4DD22D343B32F099593E2EB6B689E9E9466730C3691B351FBD0DE48DD5C49CC97F3495796839B1FAF658B5EE9A961CCE17D3960747CC76A1516E10DD9E1372F03B9CCE59E2694107D592BC635DEE5961F2
	FA1BE9F50F6094A57248FE647F571989F4ECC27A20ECC8C15C319C5459C6A8EF85A0A9DC06D713EA959E975DD8C6221FB0071461CA8A63BDAC45AF0354B096DFF6G474FBB1F28740B047A9DGCC3E08B38DF7A95F04D774D8AC9761FF2B47B5613E6012C42DE80AEC574FF3596483DC3656A3C8FBB91457GECD6714919085F8E654403BAFD41C1AEF35D89AE73402D67097C61C7317BC348DCFB1049EABF6817B93AEAACBE035AE1F8B65A52D228DB83708640CE002CD41D3E96A0135A7F1342C9F8DA0764C657
	78384BE9F53B9BA43B2753E597A5F837D820C641DD9007DD9EBBA14C465F4FAC89428F073E54BB6D9B0149F609F01BDCFE619F08FD49365878A0322DFC6DEC6C18B2466DD43FEC91292837EFD3DBD98C6FBAE5DC06FF1262F7EA70EC6E0BA99E4B06B41D7EF69A5773C0B2674526FF1C24148B313B0370927E4F7D3151E90C079DE9FEDE180EBDC654F141A11D3E9EE0G408D30GE045A12E639821F2B69D37034DF91C0254BBB16496BCF24304C3320B9EC5570567E752F5B0E07DCE5FF3BF2617E5155AB95A7C
	0F57026AE57332665828C3CC9FDB8E797542E83BC252569F1EEE9B496A9C3EC0570A61B01B43B8957F924597EA70ECEEBBA99E4B55D0379800357FAADDDBA41D35572963BCCC71099ABC9BA7AD136F8F86283B8D4066B7A58F775AE8A86F84C8B36874A5GB600B1005B86BE47B5C3663BDCC7BD1A79E5FD3F13FD981E22CF76886DCE2FACB80745FE41639006A451CB14B6CFE723DE076E6877ACDD5F43C023D5F40A9E8137DB98DD989741183C2CCDC47681EC6391E9ED1FE3DC848263EE42470D4CCE0527A4F8
	650BEE3BA00B7E962CFEEDB65A44CD219FC6048160BD4A007A0A4B42FDEEB13CC798382E6A72911712215397E6F03DB4669C0467F9B0EED132D81A9C434806601962B67AD8CE8ABCEB21FD944015A3307F9F616B734381EE07CDC94F1012572E8635C30425194C26A2B2836DF0B99DBF9A70A4CB27B78254E771F33ABCE7609E57C7B0C03E11FFFA869DE1CF14015168F8A0B462959B67EB1B2FD526779F95206DDE8339EE83AC4E623A7FBC4748EC32C1700A5C8A3BBDE0C0225DBF932C6DADA31EB162CD51A9B7
	09EE51E907972FE283DFB56E715B6708CB351D79940F7BD5A39810D7457F3571G434398DAEB6775FFC36B85D976B806A6E431D714E507F3545F7F6FA8AF5EBB36C22FA2EB44B114F5A548988530D91575C4EE2A464EECAE570D89F79F2EA045DE9F2267F28C34CFB84A67E67591745BCA5472FADA7657779EF990F3478169CC5F232587CAA3AE204CE84CC795A96F8BB56FC7B29FA46DC0E0B4AA6714861B7BA3F921EDB7B7AF386D1E488BEE3B25F9B35B66293CE036496AEA73F01D3AA61D9ADB3766617EB76C
	F2BAC53A6054AE4A1A7E308F65F0B89D72A507FDD414FB9D1F0960739CC51BBDC27758BEF89FBB4A774449FC3EA796FC7694093CEA896ABF05FFEFB0B6D23137CDC77DFBA26329A19F5710A809632028C65704D3E678B63AFFABF8EF9C455D3AB58F7B96F6B40E0943B79ADC3EA607971715D51228136FAD9128DCA6766891BDA3AE4F38E8AF3CAE5C94CC4224EC2273E232526AE6FCAFB216161EB54E94A7140CB8BCE23B53BD219CCA05C57DC5E50C1FFF4D7B4A1D7CF439E447485425B151E99D3E71E5B1D329
	2C35D4721F451FAB7C384607DC408F38498E3783C67E32F8A92F643C3C3B7BE946CB4304AC3B1CDD62E4A344B2C0FFCED8F12A43B3F2D72D81235BC28FDF83FCD7D472736B3D63B48E82BBCD4806B3BA1B6F69EB72311E6F694A41556812A43614504E33F11DC46724E9EC3FBA1B455C7360496A85E3307AE8357ED7C16B372B754339416A93F2B81F2B8A5047700AFF199041F54355F1DE1CD27C2F1F949C8C286B9724893FDF373EE09FBCA54DF94570BFBE96FC0D2EB906E7548DF57C3547F691744F5818ECCF
	D9A16018EE0FE39C0EF7BC00155C4212735A5CC296700FF908AB70F46770F8B871967A3BB3CD7C7C9C9C6B7759BC4EC53F176112F390378D984A0C6731C4E70AB68761FEFB23BE58FE059015437629681C40984BD8086392A350B0F21DB79C571E77D62F57B56C20AE2E223F5F9E576EE58C773B63FB6189CB488B7116DD7471B958F99CF9C59FD8C1ABFA7CFEF19D196E53B3D5F0F72AF1129AF41F2B915C70B61846C6F52B6FF70813046785520DDCDF85C63E177FDF0BB63731EAFFEB6C3E903A39E2D4E2E123
	DFB7BE08735958616A582B201E61B2F2B9EE3B0AFBD445156472797E36857732E0790FE1AA34E9F442E11A109D683C8B32C0779EEDAEE737ADC15117BD1767B5AA3BE9FC8E6515GD139DC5ECB95DADB080CEE89FAF6A1EC49D5626D5CC0DB78E19152CB4B0398A30F5B426409839ADB008D98C228D1D8C9FCFE0F954631B523140B8A63791AA15CCF49B25ECB2CDC1B47E31E5F1C443D21874A9240BC0045G2BD45ED6944E9A7BB77BE6087D175C93DF1B73386F1A1DFBAF7DE254FE0F5DD3BFFDBE6F17E88B6C
	E748777B3BA57953E3FE960F7FBD35157AFC960F472B789FD2FC1F864F6274B3940F65B1280B4A677310DC02F6D1D6G71DD81577D5E22A40DDD50BDCD3509670BF05FF4F99C238EA7472D2238E11707BAF0ACEE2824EB2B87461454F1FFFFB2343F794713417DCDFDF1F0FFF3EF714C31524162D07EA863EF254ADF98G431D4A6538C5AA6EC7AAEE190A4B27EBF79514B72AB85205386DD0CEBC46677619EE2ADBA8772838880AF3C3F9110ABB577DA0FCFD2423AD77A8E5C6E3600150880C136209B61EC8ADC1
	1B732965F4DA1EA27CDC902D58D696BC602FB7BB6D0AE131B330034E470BC06035D80EDBCFF14F81AEC947F1B6BA9F0F45E94F4F1FD25CB9B86097926E53D9ECE81312B0651AG9A8547315D7F5A78B99E813A59883083A071B80BED990FBF184433E444A30C0339743A45E1C2CE50F3280B771520FD9840D200C800E8GC6B35C0CFBDAC76F3087FCAD5C6CB157CD135E2C7ED8B25AE487BD861AC46F304761D67CB166E73C5B2D75B35ACEB03EE4F84E87E0ED0C155AB6CB15B6EB607986GDB6BD3B61E130B81
	DCE1A1172D7C9A7AA8744C62AE2645526AF1589B04D19B5594BB1BD6D37B467E578A7959749BBAE6B814D7G2CAD64E7136FB8BF0B17BF79B83105DD8BFA4F905D579A9BDD486CD1DF64BF1B362BE7F1DD9727D98F753EA2445D68527A0F2843B741B6CCC81556AF39239EF50BB6612F5FF0C7BD722B0B53566B3A35752823F3CAFFA65B7AE3DC361D6BD79069392E10F7A8110ACCDB1467D61FD3D77861D8BC537BE873D12D5EFDA66FA4981745223194427D1DBCFA57067433CEF25D2FB751BD9D4A92403C13FC
	BE379599020FCBAD0C0DB7D019206C810B55711EB80C63ED02727BG7AE23E8FFDB16DAC1A8DD83F2F5FE73F176F399FBB63D6F7A27FD94501B96AF594DFD89C18237EAFF5BDD4C1DDF8B1BF7BB615227EF6C259D082EB87208DE08CE0DE89BF3396171411D97C10BE175B86071A24B90618FC8DDD77F70F30FAB7373B157F74B409F8A8B924FF9BDE2264EF15E75824D307CFA65F2899655B8AF57AD25887G6A817AG24D2AE5F161292A69F3A67D6994252B16A79F36150997723F1C3E4321D6861320541D80B
	G16157A4F1FD525217C16F3E6FADFD99AF8CF51CE7151250177942F1A78BDC58C72EE62367AE925D63791000FB279E9C61BC251CFCF27B133A910FEAE456B4D01741FBA4C6927C11D4F4C697FCBA7D16DF823667D3FEDF31F9BCC6B1C7B5C601B2521FD35AD2541FD35F7CB037BEAB125B36786F714064E8D7E29D41B9BE4F67DCC996E5B1BE1AEF6GE415415A85E8AA63B67DF5D39E3369A0765CEC9F95A154445BA21953F71BC34BBAE91E7D0EA94C0C717110CB16DD63B8BE47BFE4467912DDEE0AE438074D
	28A3F01F47E456D441AFB463BEAD09A3944DFC4A3374AC3886F24DAB63FEE64FA06242219C29621E2638CDD05E2A6266752B7EE8B93759E6B366C02E2865EEDA76296557CDF3E5D7FE1AB1F3C68357A813DD26E75EE22039221C6B638F749EE39314372A3829CED5C7951C3F2369D4C7EA7984AD5FD64B4F9E9ED06A67EEFD2F25B482FD6D873B3E1FAB8BED732B4AE63779174AE81E88EC35EF4AADB25C3AB29A9B0E0CF8C5F5EDEDA8CBD1F16D5E9E175701AEAF7307AF766037C4B7C50F6C9806B01EBAC38D02
	2747A5B90627C8F4854E111B2E01D20025955C4F9E9D44FDE08C02504FB011BA2DA7D17A7E95DD97D60002CA5EF775A072B8647250E0114585EF8CF21F5A8E6DEE83C4GC481E01CE62842B6FF5B5FB403CE393F91274096683B83A0DFA9A742984F026FBA286371C9F5836A3F0326AE55B0C10983424CDA9F7FDA9563E39EBC432B389C8FD7CE1763F1B5B6D8D14564D88B4F4DD5AC56E47D8E84090B8A5538E8BB34CDBC2553D781341DE27D187C8BBAE713BFCE8357202FFC4AAF7FFCF8FF8A646FA4DC7EF5F5
	180754F840FDE0EF5493E6BA98C88F0C051017C5B04E32D3DC167A41F2F8EADCDF13E6BCD3FF73053E76C62B6DEAEF6305EE1B2D41FA612A55F649BA58CB381F35F4D21BD72B5EEB8B19D70BBC255C89A94F3E271DBA4643230315B3705056FD217DF2F7D7DFB0AE18FFC1AEF37FA271B41CE12739EF7F62DE74BB6FAA3F586710A53101DA8273795BAE735C626D535C67FF75A23D7F05F2944096009C4ABFA1B4C7CC7FE24E85436DBC0D5F6E9DC7D5A9FA7BE5A277CF333E7F84E9FABDB9B6BF045E6ABFD13E43
	7B449F73757C592144C557EF0508DF682AE032C418F96EF56DA7DC16718A7A0DCAB514814A2AF95C3B2E126B328DF0E155BCFFFB300A6BA322DA7D360CFE5796836504B3BA3D718C9F23C70D952A8167BE4367639DD3A61996BBB3253350D9095F425558F95999CE6BE5BAE6B414F7G24D86ED5778B4EE0090D3FA01D6B235042470FB5506F98206C83D8E0D13FAB31E05BE5D0DEE3613E51D0F9B1BC7B056B533372006C71385C309DCFB1AC3DF1D14E24DD17E70BE536D8025D53274D5AAFBB204C7AF552FE24
	666E4E59349A7E7DD2F3BF0D81EB82E343AE0A2F2A890C8DDF3F48673B9E6A3EDB43739A9B7E2C5D9BD29EBD9D4B7B8552CE28E5BC265586665CFFDC03FBE69B606DG738116822C8408820886588E10D0877687D08DE0833886A0834C838887D8DE47676EC6F5518CF327A4F228F36B91D84E166972C573CCF390D997AAD67AA5DDE71B6A82F579AE45C757856A52DB41F579BE54751C65BE53464A7B331D37E66D975CF6522B305F4A33F7E7BBEB4FF25BF9016E891B4F864A3B0662371E8D1437D37DB6F6BB54
	8D1C6372765FE7BE44FE1F7D165CE79E45C273A52B4E854A5BC871EB4F854A9B256E0D1B202E3A1E4B3BB4FDA67A0B6BC35957CD3AEF2D288F0C4543A8BE32BEB096CFB0707DFDA354D5D955BBCD8B0E294617EC5BD27C4FF68B460A781D08C3109C1F512B4EDEE8689CBDCA470ACCCD22751243A9B632AF82890FA36C749C8D87BAAB2CBCE6683A0838B5D0FE434A65BF5FCF543968533C3FF9F16EF3813F1BD39AEC9FAB406BDC6E4FDB0E1190FB90C4579DF0B4F68FDD970765BEE1D46D73731A5031463F554C
	9EEB7C2AA6F8FC3D35E6FAFC7DC1CD7078FADB0DB63ED6728EB5F8360F423EB920367B9F95B72862E2EB77A838CB8E3BBC266293EA77AA78B69189F2FCF2AD72B5C9F1837CEEC4039BD4F006DA7445C1E488E5F85F23743797A38EC9D23E4322ED9CCE881FD4FD9C2FC59E9DEEC143F7310A9BD46DF713194737DBD4DB7DF5055FBEE3B46FD395F3EF2B5F18D39ACCE6937575929A60CCED60EBF6C387622A205C5620FE43F85EAF7BBA4DFB6F7939570374A550285764A45BB56FBF7F9272136F4DA98DB6274F27
	F35B6ED36774368157CE18DAFE0816172A65E443DC7005747C321F18939AF11AE7AA39F59977A399433E96977573D83E631B743C0AC43BEF60B132F8B11E3705A70BA7661DC5FBE0771A6F2939061D50A7259162A0009EGF7A39F2339AAB83DCF69DADBG6D962BED5F6918CE6FE33CD6A3510D5867E769461966361169120D755BAB74BE15E786B4D7266C7B32678D5A7B3B42A61D9E73F4BD70E4F45FC377DB992B61A266041D6224D17D3A8EE5A94C3303DF609A4DABB11615984B2B0AF81CF5200C47F176A6
	FE9F793D72407B4805BA6DFD626B65017711585FDF3F27A270BE724E7EB5950177113CBF136709D63FBCBF6938DBF9D87B7752719CE9947177B2BC0E1F9435F1FCD64E5ED84EF3603CACEEE2733236499F63F09EB10E3FA3B6564471BC2743F47A2101472EBB1B54B5DBFE2F3EAB73C95EE8C3BB2AEA06798610819E81D88A908190853025197B079F35961250F1938B627DE19363EB1BE1A63EC84BF41F1651FA3BAD342F73EE5B6C3E4E3636603E4E47ED53FD1DBF368577F5746D53FD1DBD6DECDC1435CB98D7
	475D5F0E7EAF4B87F68ACEE1145E3330BDF6EF865FE353C0DE36DF64E6707D229AF0B6G8960910065ADBC5F521A4173ADD1ADFC6DFC119138F6A66FD89BBF4818F96D3C1D9138F6DE39237E5011D0EB074D27ADC3BB1F7AD61D5E50BAF3FC48F4B0CCE5A901362F2838B10AEB825C243A86847AAD2E92E7D0B7116133FCC9FEBCF793897B8DED257091A8E27987A45E6BA17B55F55CCE1D3D681B151638371F080B0569A43B18C13493BD4EEABB0927135ECE6240A0FCEEC5213B95E6F6F97FC2BE278BBCD60355
	4BE49FA61A0DB4536C0512E6BF857AC9F76CA3AFFD6754BBD1EF75FB63482EF612B18218DE51C3A3A8510E5FB3AABFEBEAA1FB684FD7716BFBE04ECB9F71DEF9CA92EDA4ED6482FE3E6B15C53B1547E6C077907D3E590C39CBF31B609D13517EEBECC46F449FE011032CDA921C23E6962855D089F556EBD4A63A7C005FD44AAF0EE2D2C7C9FDE17F315C0EAD91F1A436056C15DC023DC59806255ECE7648E39E51BB66126CBE7739FABAD042A44EA25E2C4741F0192A78A82C89F7AE15699B3EA50FA4EDCDEA81F5
	7ABC44CCDB4A5040243630B70E899EA0A5FA5AE57CC1194BD32056FDA5DBBE7E535674AB31C427880953335B6716492E91E15CA1CD41CB935D3569446D1301DB0F60746267604019B100B3DE2592B0FD731532EFBD75074B14003F2BCCF218AA212BF9D4F40D1B2DA3A390652A8300DEE178236A70584694506620EF61210E577EB484F650C462464B4A687FF27A3F827F17D3CCB94514D700F2E3480CFF0175BEAEB3B59EF59D60741BDAE08665FF7F6015477F23B666FD102E0544530FCFE192D27C2D3AA64607
	C44FC34B3FF5F45F226807F10E5CE8AABE9CF7BF9BBEFE1A7DA0F64FF4C3C0F412BA9DD8CA21CD8F527F076ED9B86C15CE7BE628BCE57B7879DB7405B6473863EB25A16F9F92ED4A39EB8B4CB12E2BC3662FG5EE763675D11BA3CAF8B9523770E8B12A48B528D2B530E71FA4314AC7A6F62B7546178DE5EC629E4F53F28C33E2FEB7A7CBFD0CB8788BE6AB80DBB96GGE8C0GGD0CB818294G94G88G88G50042DAEBE6AB80DBB96GGE8C0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4
	E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGF596GGGG
**end of data**/
}

	public void actionPerformed(ActionEvent e)
	{
		
		if( e.getSource() == getJMenuItemRoles() )
		{
			int row = getJTableRoleGroup().getSelectedRow();
			if( row >= 0 )
				showRoles( row );
		}

		if( e.getSource() == getJMenuItemConflicts() )
		{
			//not sure
			int row = getJTableRoleGroup().getSelectedRow();
			if( row >= 0 )
			{
				StringBuffer buf = new StringBuffer();
				buf.append( "Role Category -> Role Name " + System.getProperty("line.separator") );
				buf.append( "________________________________" + System.getProperty("line.separator") );
				
				SelectableGroupRow rowVal = getTableModel().getRowAt( row );
				Iterator it = rowVal.getConflictIter();
				while( it.hasNext() )
				{
					LiteYukonRole role = (LiteYukonRole)it.next();
					buf.append( role.getCategory() );
					buf.append( " -> " + role.getRoleName() + System.getProperty("line.separator") );					
				}
				
				
				
				JOptionPane.showMessageDialog(
						this, buf.toString(), "Role Conflicts", JOptionPane.WARNING_MESSAGE );
			}
			
		}

	}
	
	/**
	 * Return the ConfigurationPanel property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getJPanelDescription() {
		if (ivjJPanelDescription == null) {
			try {
				com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
				ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
				ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog", 0, 14));
				ivjLocalBorder1.setTitle("Description");
				ivjJPanelDescription = new javax.swing.JPanel();
				ivjJPanelDescription.setName("JPanelDescription");
				ivjJPanelDescription.setBorder(ivjLocalBorder1);
				ivjJPanelDescription.setLayout(new java.awt.BorderLayout());
				getJPanelDescription().add(getJScrollPaneTextPane(), "Center");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJPanelDescription;
	}


	/**
	 * Return the JScrollPaneAlarmStates property value.
	 * @return javax.swing.JScrollPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JScrollPane getJScrollPaneRoleGroup() {
		if (ivjJScrollPaneRoleGroup == null) {
			try {
				com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
				ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
				ivjLocalBorder.setTitleFont(new java.awt.Font("dialog", 0, 14));
				ivjLocalBorder.setTitle("Role Groups");
				ivjJScrollPaneRoleGroup = new javax.swing.JScrollPane();
				ivjJScrollPaneRoleGroup.setName("JScrollPaneRoleGroup");
				ivjJScrollPaneRoleGroup.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				ivjJScrollPaneRoleGroup.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				ivjJScrollPaneRoleGroup.setBorder(ivjLocalBorder);
				getJScrollPaneRoleGroup().setViewportView(getJTableRoleGroup());
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJScrollPaneRoleGroup;
	}

	
	public boolean isShowingUserRoles()
	{
		return true;
	}

	/**
	 * Return the JScrollPaneTextPane property value.
	 * @return javax.swing.JScrollPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JScrollPane getJScrollPaneTextPane() {
		if (ivjJScrollPaneTextPane == null) {
			try {
				ivjJScrollPaneTextPane = new javax.swing.JScrollPane();
				ivjJScrollPaneTextPane.setName("JScrollPaneTextPane");
				getJScrollPaneTextPane().setViewportView(getJTextPaneDescription());
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJScrollPaneTextPane;
	}


	/**
	 * Return the JTableAlarmStates property value.
	 * @return javax.swing.JTable
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JTable getJTableRoleGroup() {
	if (ivjJTableRoleGroup == null) {
		try {
			ivjJTableRoleGroup = new javax.swing.JTable();
			ivjJTableRoleGroup.setName("JTableRoleGroup");
			getJScrollPaneRoleGroup().setColumnHeaderView(ivjJTableRoleGroup.getTableHeader());

			ivjJTableRoleGroup.setBounds(0, 0, 200, 200);
			// user code begin {1}
				
			ivjJTableRoleGroup.setAutoCreateColumnsFromModel(true);
			ivjJTableRoleGroup.setModel( getTableModel() );
			ivjJTableRoleGroup.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjJTableRoleGroup.setShowVerticalLines(false);
			ivjJTableRoleGroup.setShowHorizontalLines(false);
			ivjJTableRoleGroup.setIntercellSpacing( new Dimension(0, 0) );
			
			
			ivjJTableRoleGroup.setDefaultRenderer(
						Object.class, new RolePropertyRenderer() );
			
			ivjJTableRoleGroup.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			ivjJTableRoleGroup.setRowHeight(20);
				
				
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableRoleGroup;
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
				ivjJTextPaneDescription.setBounds(0, 0, 100, 59);
				// user code begin {1}
				
				ivjJTextPaneDescription.setEnabled( false );
				ivjJTextPaneDescription.setBackground( getJPanelDescription().getBackground() );

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
	 * Insert the method's description here.
	 * Creation date: (11/9/00 4:58:59 PM)
	 */
	private UserGroupTableModel getTableModel() 
	{
		if( tableModel == null )
			tableModel = new UserGroupTableModel();
			
		return tableModel;
	}


	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Object
	 * @param val java.lang.Object
	 */
	public Object getValue(Object o) 
	{
		YukonUser login = (YukonUser)o;

		login.getYukonGroups().removeAllElements();

		for( int i = 0; i < getTableModel().getRowCount(); i++ )
		{
			if( getTableModel().getRowAt(i).isSelected().booleanValue() )
			{
				LiteYukonGroup group = getTableModel().getRowAt(i).getLiteYukonGroup(); 
				
				//add a new DBPersistant YukonGroup to our Login
				YukonGroup grp = new YukonGroup( new Integer(group.getGroupID()), group.getGroupName() );
				grp.setGroupDescription( group.getGroupDescription() );
				
				login.getYukonGroups().add( grp );
			}

		}		
	
		return login;
	}


	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(Throwable exception) 
	{	
		/* Uncomment the following lines to print uncaught exceptions to stdout */
		CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
		CTILogger.error( exception.getMessage(), exception );;
	}


	private JPopupMenu getJPopupMenu() 
	{
		if( jPopupMenu == null) 
		{
			try 
			{
				jPopupMenu = new JPopupMenu();
				jPopupMenu.setName("JPopupMenu");

				jPopupMenu.add( getJMenuItemRoles() );
				jPopupMenu.add( getJMenuItemConflicts() );
			}
			catch (java.lang.Throwable ivjExc) 
			{
				handleException(ivjExc);
			}
		}

		return jPopupMenu;
	}

	/**
	 * Returns the jMenuItemRoles property value.
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemRoles() 
	{
		if( jMenuItemRoles == null ) 
		{
			try 
			{
				jMenuItemRoles = new JMenuItem();
				jMenuItemRoles.setName("jMenuItemRoles");
				jMenuItemRoles.setMnemonic('r');
				jMenuItemRoles.setText("Roles...");
			}
			catch (java.lang.Throwable ivjExc) 
			{
				handleException(ivjExc);
			}
		}

		return jMenuItemRoles;
	}

	/**
	 * Returns the jMenuItemRoles property value.
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemConflicts() 
	{
		if( jMenuItemConflicts == null ) 
		{
			try 
			{
				jMenuItemConflicts = new JMenuItem();
				jMenuItemConflicts.setName("jMenuItemConflicts");
				jMenuItemConflicts.setMnemonic('c');
				jMenuItemConflicts.setText("Conflicts...");
			}
			catch (java.lang.Throwable ivjExc) 
			{
				handleException(ivjExc);
			}
		}

		return jMenuItemConflicts;
	}
		
	private void showRoles( int row )
	{
		UserRolePanel rolePanel = new UserRolePanel();
		DBPersistent dbObj = 
				LiteFactory.createDBPersistent( getTableModel().getRowAt(row).getLiteYukonGroup() );

		try
		{
			dbObj = Transaction.createTransaction( 
							Transaction.RETRIEVE, dbObj ).execute();
	
			rolePanel.setValue( dbObj );
			rolePanel.setRoleTabledEnabled( false );
						
			OkCancelDialog diag = new OkCancelDialog(
					CtiUtilities.getParentFrame(UserGroupRoleEditorPanel.this), 
					"Group Roles : " + getTableModel().getRowAt(row).getLiteYukonGroup().getGroupName() + "  (Read-only)", 
					true, rolePanel );
	
			diag.setCancelButtonVisible( false );
			diag.setSize( 520, 610 );
			diag.setLocationRelativeTo( CtiUtilities.getParentFrame(UserGroupRoleEditorPanel.this) );
						
			diag.show();
		}
		catch( TransactionException te )
		{
			CTILogger.error( "Unabel to get the role data from the database", te );					
		}
	
	}
	
	/**
	 * Initializes connections
	 */
	private void initConnections() 
	{

		getJMenuItemRoles().addActionListener( this );
		getJMenuItemConflicts().addActionListener( this );

		getJPopupMenu().addPopupMenuListener( this );



		java.awt.event.MouseListener listener = 
				new com.cannontech.clientutils.popup.PopUpMenuShower( getJPopupMenu() );
		getJTableRoleGroup().addMouseListener( listener );


		//add the mouse listener to our table	
		final MouseAdapter ma = new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e) 
			{
				int row = getJTableRoleGroup().rowAtPoint( e.getPoint() );

				if( row >= 0 && row <= getTableModel().getRowCount() )
				{
					getJTextPaneDescription().setText(
							getTableModel().getRowAt(row).getLiteYukonGroup().getGroupDescription() );

					if( e.getClickCount() == 2 )
					{
						showRoles( row );
					}

				}

			}
			
			public void mousePressed(MouseEvent event) 
			{
				int rLoc = getJTableRoleGroup().rowAtPoint( event.getPoint() );
	
				getJTableRoleGroup().getSelectionModel().setSelectionInterval( rLoc, rLoc );
			}
		
		};		
		getJTableRoleGroup().addMouseListener( ma );
	}


	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("UserGroupRoleEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(384, 366);

		java.awt.GridBagConstraints constraintsJScrollPaneRoleGroup = new java.awt.GridBagConstraints();
		constraintsJScrollPaneRoleGroup.gridx = 0; constraintsJScrollPaneRoleGroup.gridy = 1;
		constraintsJScrollPaneRoleGroup.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneRoleGroup.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollPaneRoleGroup.weightx = 1.0;
		constraintsJScrollPaneRoleGroup.weighty = 1.0;
		constraintsJScrollPaneRoleGroup.ipadx = 343;
		constraintsJScrollPaneRoleGroup.ipady = 172;
		constraintsJScrollPaneRoleGroup.insets = new java.awt.Insets(6, 7, 1, 5);
		add(getJScrollPaneRoleGroup(), constraintsJScrollPaneRoleGroup);

		java.awt.GridBagConstraints constraintsJPanelDescription = new java.awt.GridBagConstraints();
		constraintsJPanelDescription.gridx = 0; constraintsJPanelDescription.gridy = 2;
		constraintsJPanelDescription.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelDescription.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelDescription.weightx = 1.0;
		constraintsJPanelDescription.weighty = 1.0;
		constraintsJPanelDescription.ipadx = 340;
		constraintsJPanelDescription.ipady = 38;
		constraintsJPanelDescription.insets = new java.awt.Insets(4, 7, 2, 5);
		add(getJPanelDescription(), constraintsJPanelDescription);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	
	initConnections();

	initJTableCellComponents();
			
	// user code end
}

	/**
	 * Fired when a check box in the Roles table is clicked
	 * @param e
	 */
	private void checkBoxAction()
	{
		//validate all the selected roles against one another
		UniqueSet[] unqSet = validateRoles();					
					
		for( int i = 0; i < getTableModel().getRowCount(); i++ )
		{
			SelectableGroupRow row = getTableModel().getRowAt(i);
							
			//re-init our conflicts
			row.clearConflicts();

			for( int j = 0; j < unqSet.length; j++ )
			{
				UniqueSet us = unqSet[j];
							
				if( row.getLiteYukonGroup().equals(us.getKey()) )
				{
					row.addConflict( (LiteYukonRole)us.getValue() );
				}
														
			}

		}
					
//		UniqueSet p = (UniqueSet)conflictList.get(i);
//		LiteYukonGroup grp = (LiteYukonGroup)p.getKey();
//		LiteYukonRole r = (LiteYukonRole)p.getValue();
//		CTILogger.info( "  " + r.getRoleName() + "  in  " + grp.getGroupName() );
					
		fireInputUpdate();
		getTableModel().fireTableRowsUpdated( 0, getTableModel().getRowCount()-1 );
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11/15/00 10:52:29 AM)
	 */
	private void initJTableCellComponents() 
	{
		// Do any column specific initialization here
		TableColumn selColumn = getJTableRoleGroup().getColumnModel().getColumn(UserGroupTableModel.COL_SELECTED);
		selColumn.setMaxWidth(30);
	
		// Create and add the column renderers	
		//CheckBoxTableRenderer bxRender = new CheckBoxTableRenderer();
		CheckBoxColorRenderer bxRender = new CheckBoxColorRenderer();
		bxRender.setBackground( getJTableRoleGroup().getBackground() );
		bxRender.setHorizontalAlignment(javax.swing.JCheckBox.CENTER);
		bxRender.setBorderPainted( true );
		
		selColumn.setCellRenderer(bxRender);

	
		// Get the group role data from the cache	
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized( cache )	
		{
			java.util.List yukonGroups = cache.getAllYukonGroups();
		
			for( int i = 0; i < yukonGroups.size(); i++ )
				getTableModel().addRowValue( (LiteYukonGroup)yukonGroups.get(i), new Boolean(false) );
	
			// Create and add the column CellEditors
			javax.swing.JCheckBox chkBox = new javax.swing.JCheckBox();			
			chkBox.setHorizontalAlignment(javax.swing.JCheckBox.CENTER);
			chkBox.setBackground( getJTableRoleGroup().getBackground() );
			chkBox.addActionListener( new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e) 
				{
					checkBoxAction();
				}
			});


			selColumn.setCellEditor( new javax.swing.DefaultCellEditor(chkBox) );
		}

	}
	
	/**
	 * Icky method to below, but what other way? May become a performance nightmare.
	 * 
	 * Returns an arry of UniqueSet instance in the following:
	 *   key<LiteYukonGroup>, value<LiteYukonRole>
	 * 
	 * If no duplicate roles are foud, a zero length array is ruturned
	 */
	private synchronized UniqueSet[] validateRoles()
	{
		//stores the conflicts
		ArrayList conflictList = new ArrayList(64);
		
		//stores all groups we already looked at
		ArrayList allOldGroups = new ArrayList(128);
		
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized( cache )
		{
		
			for( int i = 0; i < getTableModel().getRowCount(); i++ )
			{
				//if the row is selected, add its roles to our ALL_ROWS list
				if( getTableModel().getRowAt(i).isSelected().booleanValue() )
				{
					LiteYukonGroup currGrp = getTableModel().getRowAt(i).getLiteYukonGroup();
					
					Map currRoleMap = (Map)cache.getYukonGroupRolePropertyMap().get( currGrp );

					for( int j = 0; j < allOldGroups.size() && currRoleMap != null; j++ )
					{
						LiteYukonGroup oldGrp = (LiteYukonGroup)allOldGroups.get(j);
						Map oldMap = (Map)cache.getYukonGroupRolePropertyMap().get( oldGrp );
						
						Iterator it = currRoleMap.keySet().iterator();
						while( it.hasNext() )
						{				
							LiteYukonRole currRole = (LiteYukonRole)it.next();
							Object oldRole = ( oldMap == null ? null : oldMap.get( currRole ) ); 

							//if we have an old role, we have the role duplicated in at
							// least 2 Role Groups
							if( oldRole != null )
							{								
								UniqueSet p1 = new UniqueSet(oldGrp, currRole);
								if( !conflictList.contains(p1) )
									conflictList.add( p1 );
								
								UniqueSet p2 = new UniqueSet(currGrp, currRole);
								if( !conflictList.contains(p2) )
									conflictList.add( p2 );
							}
							
						}
									
					}

					//examined this group, put it into the old group mix
					allOldGroups.add( currGrp );
				}
				
			}
/*
			for( int i = 0; i < conflictList.size(); i++ )
			{
				UniqueSet p = (UniqueSet)conflictList.get(i);
				LiteYukonGroup grp = (LiteYukonGroup)p.getKey();
				LiteYukonRole r = (LiteYukonRole)p.getValue();
				CTILogger.info( "  " + r.getRoleName() + "  in  " + grp.getGroupName() );
			}
*/
		}
	
	
		//will return a zero lengh array if none are found
		UniqueSet[] us = new UniqueSet[ conflictList.size() ];
		return (UniqueSet[])conflictList.toArray( us );
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (5/1/2001 9:11:36 AM)
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
		UserGroupRoleEditorPanel aUserGroupRoleEditorPanel;
		aUserGroupRoleEditorPanel = new UserGroupRoleEditorPanel();
		frame.setContentPane(aUserGroupRoleEditorPanel);
		frame.setSize(aUserGroupRoleEditorPanel.getSize());
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
	 * @param val java.lang.Object
	 */
	public void setValue(Object o) 
	{
		if( o == null )
			return;

		YukonUser login = (YukonUser)o;
		
		for( int i = 0; i < login.getYukonGroups().size(); i++ )
		{
			LiteYukonGroup group = 
				(LiteYukonGroup)LiteFactory.createLite( 
						(YukonGroup)login.getYukonGroups().get(i) );

			int indx = getTableModel().indexOf( group ); 

			if( indx >= 0 )
			{
				getTableModel().setValueAt(
						new Boolean(true), indx, UserGroupTableModel.COL_SELECTED );
			}
			
			
		}
		
		checkBoxAction();
		
		//special case if we are the admin
		if( login.getYukonUser().getUserID().intValue() == UserUtils.USER_ADMIN_ID )
		{
			getJTableRoleGroup().setEnabled( false );
		}
		
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (8/25/00 9:45:22 AM)
	 * @param e javax.swing.event.PopupMenuEvent
	 */
	public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) 
	{
	
		if( e.getSource() == getJPopupMenu() )
		{
			int rLoc = getJTableRoleGroup().getSelectedRow();
			SelectableGroupRow sRow = getTableModel().getRowAt( rLoc );

			if( sRow != null )
			{	
				getJMenuItemConflicts().setEnabled( sRow.hasConflict() ); 
			}
		}	
	}


	public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
	public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}


}