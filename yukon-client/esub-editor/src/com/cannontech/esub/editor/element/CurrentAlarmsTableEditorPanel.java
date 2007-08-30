package com.cannontech.esub.editor.element;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.cannontech.common.gui.tree.CheckNode;
import com.cannontech.common.gui.tree.CheckNodeSelectionListener;
import com.cannontech.common.gui.tree.CheckRenderer;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.model.AlarmCategoryCheckBoxTreeModel;
import com.cannontech.database.model.DeviceCheckBoxTreeModel;


/**
 * Insert the type's description here.
 * Creation date: (12/23/2002 11:36:50 AM)
 * @author: 
 */
public class CurrentAlarmsTableEditorPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private com.cannontech.esub.element.CurrentAlarmsTable alarmsTable;
	
	private JLabel ivjDeviceListLabel = null;
    private JLabel ivjAlarmCategoryListLabel = null;
	private JComboBox ivjJComboBox1 = null;
	private JPanel ivjJPanel1 = null;
    private JTree selectionJTreeDevices = null;
    private JTree selectionJTreeAlarms = null;
    private JScrollPane ivjJScrollPaneDevices = null;
    private JScrollPane ivjJScrollPaneAlarms = null;
    private CheckNodeSelectionListener deviceNodeListener = null;
    private CheckNodeSelectionListener alarmNodeListener = null;
    
    
    
    
/**
 * CurrentAlarmsTableEditorPanel constructor comment.
 */
public CurrentAlarmsTableEditorPanel() {
	super();
	initialize();
}

/**
 * Return the AlarmCategoriesLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDeviceListLabel() {
	if (ivjDeviceListLabel == null) {
		try {
            ivjDeviceListLabel = new javax.swing.JLabel();
            ivjDeviceListLabel.setName("DeviceListLabel");
            ivjDeviceListLabel.setText("Devices:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeviceListLabel;
}


/**
 * Return the AlarmCategoriesLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAlarmCategoryListLabel() {
    if (ivjAlarmCategoryListLabel == null) {
        try {
            ivjAlarmCategoryListLabel = new javax.swing.JLabel();
            ivjAlarmCategoryListLabel.setName("AlarmCategoryListLabel");
            ivjAlarmCategoryListLabel.setText("Alarm Categories:");
            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjAlarmCategoryListLabel;
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G9BEC17ADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8BF0D457F55431B33823A458D6E35925AD29158EA6CAA32744D17D0931CBEA26468D2D71044940D42EF1C749283614B22D2623F12F76235D9582FD010515E0019592E8D9C97C748101BE2B1F3D410C11315C42C4CF5AA7E9E1353B5EFD7A59A4745CFB5FF97B34FA3BB290E9664C5BFB6E6F1CF34FBD1FFB2FC86521D54682DDA5A185C6A27FB5979062ECA6A4B77BFB3508A3E539A6227ABBCCD164
	4BA283FCCB21EE30AF57D4CA3EB6E30632834A69BD3926FD703B022C79E259997891E71EA1283B161A3AB271BC8F931C47444A4F1FA8066FEE00BA40F9GD24A687CEF1C304A789014F74B3FA9946888F11753B6AFD92CAA7CD639DDE3B97FEEE2ED5EBDD1A557A7C11936C905EFC6F9F4CC8B236E21369639FC7D11E432639FF66AF65139D463A3D8ABF85F8B7257CE9EE302501350A73D94FF65567845516024EBD488840281C99C1DF009112991176875C94130CB740B13E2C0EAEB6B1E8A0761C727DF88CFC6
	8605913F5843DADC9182221F10348ACA7FBB96B39B1B90894A1C56FF5AF5C126A579D6B249FC6A7D02F8346648B4D6D6C6E94DCAF942303E120E3D2F3ED86EB78865A4GBE67EF6BF1CE731EDC5372BD9417E726EDBB607790C0E88F5252DE0B348CFD7994499BF169CA625032F18F27E1471EA8AD1E56C95DFE2687FBF4F4AD9B77027E81CCGA4812481EC8430B17D5BC4CE4037F3C46A8ECECE86831D21D0175F9B7ECD50AB7A61F7DB9B5448380F4551E058CB885731DFD91C9A74A0EC5FBBDF3F22BA71A9C1
	1DC87B78C9A2C98E1DD1033735EF6E526549E3946F0B724679FAC6CF79F2823E9FE098E096A085A0E39F72F5206074E2FC8D009E0583027F6A54C8C888CBDDD3BE3FD78C4B7CD557A762CF8BF83F7A3B6E4765327A34DAAEDB7F26C1E752104B5655153A4D32BC32D5F261F3FB634EDDECDD389EECDFDB299E7AADD48F6D703BC056D10E1FE6F8C90567BA7EBE53DBDAFE906A322C2837C33B11870F611B243429D1130714D7EAF41E7D1C9E6F7E58357D06115A169500CF83D88F3095A09F40FAA05764BC006BAB
	96B4FCC53D3D22DADB5E7720EEAFFC45D9A9AC8C84A2129098952F89E19F359691A23729E5340F4C6B67E47BE99466689383E2D810FC41G0CAECC0A20C8915E662C2E0C36890B2CF650B7A9428413A10263366BAA606B97A252A7A12FA00951963C3EC7C7E5B9ADDC0391E1G78FD7BG15F9C191359F9E78BD8140E575DCA1452D037296C5AE6BF465707D9094DB74373539FD23148CA1BC0773BF272736C41C86A36996C3E2408BBFA2F25D4BFACBD4AEE3C10566D7991E6E11EEA0BC2260DF5717F1BC0C2116
	B2563F496A85C98A7BC626A4712AA8C93E40F8347FB6C6CBE45E760B90F90F1B280EE583DF86DB2E2952067C0D99AAD47CF90241DBD3A1E667E5B9FD496CEC9D346F3021BF8CDB28FF745AD0CF5F9DC2BDF56FFA02B85FF4E96AA90595B62E1F2BED31FB6DB69BB3BA0741B224515FEB535EF7F78B32CD521CC37F87981F82173B036D79CDC0F7B6C0F1954A7DBB86BB5FEFDDC2C4449D76D198D6CC74C6350C37FD5C98DF477E5C282DE329C6ED9DDBE3CC2CC3BFB4EA69902F7BDBA375AD411900CAA7FFE2ACE7
	FBB890909951CA9742630DF78729ACFC811F74294FBBAECAD7FDDF08306F2A28EED9599EF4406F16AA5425192328CB0EAF7E042C7B2DC5531F25FC37CE37220A6BC2EAD5D417B8CDAF95D2194BB1CCF7F0AAA0F17CCF597E167132FD7931C93B6D0BA62A0F3744B9395D4B26C2C26D08FFCAD4F8CB97F6D29907FD1330D4F4434AB15F23756A18EFBD50C763C3C3B558DCG5E7701DD5570DDA43C12F568C6912531917A3A812ED7238E351B772B762E2CBC5DC13F1FAF0532C6A7AC25F4CD454F27C4B04DF4168F
	953E1AAC65B1F557E49EF9FD2B057268D75985291A7258D0047197759B9CD73C0B625E03059CAFC4BF7C1BD2F54C6A0D9C56DCCF8A9955FC9DB7D6477A30C7F752F133811F2F703E562236DB1D11C8F05447DC043CB6CF3451F8C5AB4E9DE58E4EB5BE65F3CDC9BEEA54858976CEE8CA62D26771E5BE33FBF42C520398DF6EED206B508F65E1005903C84BE851C23D1AA8AA026F18AF9C013848AB4E22BC83C5D4AE54DF49E80C63CE296534AEF3CFDCB92DB8A86B7D41D8B9EDA9E67E8B701B955AFED9D4261293
	441C601E47E195111E47F76A391E4B65BF5DE9C4BDA7E8A78B1C31F1D05641289D4EBF3850A673F8670F193CCA8F4546C1498CEFBF949B87D5B5E09C5488F5490750AF7FA3532BBCA81B6D48D7E371AE95DFD7023E002470342D186E55E058B76E8BA06EDF998EA2EEE63C38EF33BA6938D698538940F1EF1F2438ABD01E3023AFDA51C0EDC112D2FE1215D7A965A38DDAFE8401763BFFDFF5BE619C776B33380C5E58C557E3D6A97F1C15676471ABC8033310696EDCF08A045DF8184A2B570936EEBD70399540G
	F86BE14EB35F0B4F4CD09BB286019D381A2BA1F114949F21FD7F7BA4761D0576C9GE9G9B813281781C7FE9207CFF4036E613ECDFDFEBD05B5741A3BCB61905EF92G572500937346D440EDBD02F637E91846434CAE5C94268517B083D9EEDF58676D9246BD0CB33E7F1FE53AC17BD79E457D3F1D0D690132976076D174D93347B03FCC7B2BEF924F32864D3CBCF3F02FAE79A817CB4651685EAD057C124FE9F7611CBB205E6920384B2E4A18F526E78BAE78727AE5736A69DE9FA76A7A9F4E2B27749A756A7A6D
	736A290CCE4B7DB9EF2F55A0EF792F2DA47E9FD547BDF3109C1C279479BB54F800F44CCC69385C7F6338D5AD77D9D7E48614212D0D2FAC67DF5FE81675FD1383E57EEB664B4DB539A6BBC0CB8D6EB9E901CD7AEA7B6801FB6C777CDD7763366E5913147EABB55C5695A87817987E3A8A4F7576D70A5ECEC05D469A34018F17303307E339A68F009760B640CA000CE3E8932F6FEEA40B64F00341108782C93FAA3D607C55386EAD4FFE59047A317639EF130C2FEFAB5899C7BFB69E1373EC791B63BA40701C3F2AB2
	4ADFD5AD2CB140F5G89E0B9402ADA64AFF5371373C70324CEC992C6A71847C5662E794419A81A9A9A4E1B39D14D5BDA98EFC3ED54A6EF2D0D673F46D8EC18D34B57482860BFE7F8C3DD944F57AE0F69AAADDB216EF69D6A6A8BA70922C7BFD57D4EF1A63269778BFC8E4B49251C032F4B23A5717313A7CA3473133FA8514ECF3EDB12B8BF79FEC97C7CE4ED09BABF61FAB5D2CE6DDBA62CC5BE007DF82E698CC07FF154292E52A3DC27B474298792166EA0BB5110F07E1A32783CBA4B96BF87B9DBC663421120A4
	85A7697808BFDFC657CB8A06981263BA4A280CA00C1910F8D3995FDBC66D24DF9CE3E89E379CE236538BFC4D9E47D8E6301762D6C2B9DD413D5107714D6663281FFE934B43CEC0FEF182F1B38CF79D4A37955C7B0509F409760D45B9B4F00975F51FA9519CC68D5C5D44BEDCFEB74B632F6167650B2F619765AC5E87598F4E05C40E3BD34EE24E31310828684A0365258AEEA0F2A5987151840547B5679A8B199E0459818F0F252E34E0DC3392643696A08740D01FEB2A2A47F868D66F42986C01DE6C57816D06G
	1681A4835098F5C7BD73ABFDEE39CDDA7D42B5C19FE41465398E5AEC8658A317336A23FE0A5371361B6EAB1638F68B12B88E5136987188A3221F573FFA066A79876009C602DD4159A650EDA72563E913BA5E386064B48E431757C74C6A7A97647A6DCE0C6BDC2D18A364812E71A446F5AF96D15F7715723DC11F64973B1864F95CB15D0A396C44C90CBB569450F9D6C1B983E093G0FBDEF68E2E34FAC28ABF601EDF1A10DEBFA510737GAE64C29ACF1F5ACF96441E3D7E600CC7DECEB9777CF5AF46012BDDC80F
	3D0F0E198965BC0052863C873175E31EDA8738C3CC7690AF917C4B3924059BEE2077DE49149C590F3FD50CFE3C6AFB5FA26E1FEFABB8CA347DF8EA03FCC752900DE1B37E3A55A047F9E3F162BC269B1C66CC934A5B5908B1746FFA5577C5AF146C55450BA33C0DFC5E879B2373AF07981A4FBF6E37E94F0F1AA84BF80D99E9D85F08766DCC6BE2B1D0FE637C3D437BF5AD5A6FE0CC19777BF251FEEA30B6013E337863200E6A0233A9B6262CE57816265818B23DG75518DF52B1BD017E7F53167D4452740BF8274
	1FC27DAA31E0CCBEFC8AF5F158CD47CB01F2BA4006D3A83F745EFB0B2DFFF20F7D7E6B2E7B49678FFCAF1D0A155F1FB1FC7629D8799D6BC37965C15D7553482F3DDFBD7FBA40EFBE1D585FF1DD7F597F294FFACA1FB46AF03E5876D972F8BBCE471E7508B6EA4F6B4E40DA82CCGAC83C886C883D8873099A08B001C05FC9C40895081B08860851885C8BA0BF150767D4761FBCD38A9E78D7C9C12C5C2001493E4968C0585FE5642E5700EC9EB8DD61D0DAF87CEFFF9B53573EB215DA600ACGF28EEC68B92447F5
	E0517B461E5984770DBFB0A7520D0EF38B69236731FA322E0266373E005F9700580F5DCC91BCAFFA147909C168BBF18EF54021A30AACCE2AFEBFDB3074317D4F16F48E7EB661B19D3ECD104E713789C970DD25121F6C078C6807B24E211DE8E7771AB9D0B6B7435AB6234CAA675DF537G5E539C9DEF38B9DEBE3619599D29B9B69F7B9706DF5E9C1B0F0D39B19FDB89F524C5B93F6B0B2E53D32A5F4E3E25DF331EA51D036BE9B30B893CADDC9E9CF70161EE4BB83E871B28DB12E3556983B5A44E7E072D768138
	520FC6EE0A235220B02E741933450F231759960F239F36E967C25F32AD4C0512ED5A395013B6F5AEA46716B63A6663E0330614F6298A6EB38577B49B1362BE75F92589857F83C69745770BF4C2447F0851B543F0C3F87F2B42FDA663DE3251989DD81E8806316FFA9BBDB39E73797D3C3C01357185A0B5D06471BA23519792D4F47F33027BCCD94BFC96BF2540DA2EEED15EE7D447DF0B43555AB9F8ED35F68EFE32BA71BD74596AF8B9BA276F6F4D18E32EED413C6569D64A07C4C3EF787278F32B1DA8FB75859D
	7A58AD50273495638F039E63347ED634AB83FA5AF69914D3G52DB95FFEC0835799BDAD567BC2D716C4AB35CCE35465ED3BC4F7024AD761E22DBF92FD18AF5D29B665675ACD65A8265EC8557FB0762CA4F671A9C6791F7C8C079741FC75AFF61DE18033F43F0A950A6C3E967F7AFFD8E7ED042B96EB78767F13AF02C1870FBE11F60E75B0F632BE6F7C571387D641C3342CC7710840A4C7567FF68F3BD3265BC1569B633B5411C1C2FD7F47796E7EE3A47FE96FD3CFE768B095E8F3D48747B4C05D8FDFC15615D97
	E27571A3BD6A63A054E5DCC0BDEBE6BE392A9D7206F644259AD177865BD127FEEC1EFF3F75B2AB53B387DE9EB0AF7D7D56C842B96E77FE0B6BCF17E561194BE71658B317597658B3179BF7961E39C8F730DFD2BBBFF3C905EFC6BB6F474FDC4E89F8663229BD7E190B1EE81F39ECEF0F66A17CECE4DFCF6C59891DAB279D4FCE5E6A09BDBB2175060B18E3EF38213E4B3157E7E93E494C3BD12133DE145F0FDE0C669D291063F39D326B71CD90BD7B606B77F7FAFC93444B2F33F2347E937D523F6789E84E7107FA
	4F43F56132E121AE0886659D70C5FE873A9C3EABE559713B4E670D58E6AD60B7GE48118AF651A6AAE212FFBC4F9C75BFF894F12D65EC13F388CF099CA5B776E6099D14EA5740B7EE15A362E8372BC00218EECBBAEE0EC9E829C371DE1FA3CA3476E17FBF05E55CA9FEBAF62B2BB708E4A6320BCD8BBF3CDE7G06GC2G8F822C6C44B8247BC89D091FC772032EE89A49F97C0681F9CC07F132BA1106B78D48233D8BF9549BE95BA1A807A82E8B5B3EE544362B3A706E3424B0715DE9E7E1623B53C8E1623B531D
	66F8F727DC9F3F1D70CDEFACEC69423D7217F76E4D5F245FE33F9DF75D0F7B23CC26EF59DD3167A83FE4783C2E58F31470B02E33219BF268EEA526915473EF84FCE6F7E23B407BDD6D5122FBC7023E723B3F0467C6E677427EDC37BFF1440F312FBB964FF7C407F63EF34B31B05F89B934731DE907BA5F617CBC36A06E7DEA6B7E1DFB6C770B3B6E4775E2066D5DAAF72C3EACE378BAF72C3ED80C28AFE720AE450D7A72EF463B1D1F7B2D6FDB707CA6BFBA56B0D178FACF75FBF6F8694F829ED952B9B84FDF9396
	1E85AC9754E7819CF71379GF3CF2E495A03316219DE0C959DBDA82F5F9923B27A3A6AF709F16965D53724F3F03FD8F20C7244FD7D9F754C4FB72E327FCF627946073D09728D7E3FC81B4D0F0300537E1D98A3E1D202D7BE2E811674E7CE1FEE533F7D14DEC78C8324109B0581E220BBFB009859469FA0FAB08A3374313BC18DB0DAAD3D64720DCC3A2626A7DD4208447EF147B5989682913FA0D12BF3953273G75E7748F7C1E71FFC94518CF74FBAF03818BE3CBF10014B16C47E2A4B8959EE5B71A9E32EF9E2E
	EBAA60750B17693BFCD26E21FCB4780540F8831F623C07144EEB7E21B877B979BD51FB6489914F1B33AA469304A3A1091438FD119044D3F67E8856AE01819627FD23E224ADC25B12855CD32CCE69FF742AC85F3B5A05G9F41AEDFC74E592520DD9EDBA2E5736AFCB038FC96FF7C0E7AACBE6F217C02F86FAED37A38DFC86B0BBD0B8F337729D900B774E39C74CA91FD68916FBC6D6A246077CB027FD6E740CB4F563A66A4B1FA7EB233130E9F41B6F2A52F7B7DCECA77CDD51F7F87D0CB87887E82ECC70A92GG
	D4B7GGD0CB818294G94G88G88G9BEC17AD7E82ECC70A92GGD4B7GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG4492GGGG
**end of data**/
}


/**
 * Return the JScrollPaneAlarms property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneAlarms() {
    if ( ivjJScrollPaneAlarms == null ) 
    {
        try 
        {
            ivjJScrollPaneAlarms = new javax.swing.JScrollPane();
            ivjJScrollPaneAlarms.setName( "JScrollPaneAlarms" );
            getJScrollPaneAlarms().setViewportView(getJTreeAlarms());
            ivjJScrollPaneAlarms.setPreferredSize(new Dimension (100, 250));
            // user code begin {1}
            // user code end
        } catch ( java.lang.Throwable ivjExc ) 
        {
            // user code begin {2}
            // user code end
            handleException( ivjExc );
        }
    }
    return ivjJScrollPaneAlarms;
}


/**
 * Return the JScrollPaneDevices property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneDevices() {
    if ( ivjJScrollPaneDevices == null ) 
    {
        try 
        {
            ivjJScrollPaneDevices = new javax.swing.JScrollPane();
            ivjJScrollPaneDevices.setName( "JScrollPaneDevices" );
            getJScrollPaneDevices().setViewportView(getJTreeDevices());
            ivjJScrollPaneDevices.setPreferredSize(new Dimension (100, 250));
            // user code begin {1}
            // user code end
        } catch ( java.lang.Throwable ivjExc ) 
        {
            // user code begin {2}
            // user code end
            handleException( ivjExc );
        }
    }
    return ivjJScrollPaneDevices;
}


/**
 * This method was created in VisualAge.
 * @return CTITreeMode
 */
private DeviceCheckBoxTreeModel getDeviceJTreeModel() 
{
    return (DeviceCheckBoxTreeModel)getJTreeDevices().getModel();
}

/**
 * This method was created in VisualAge.
 * @return CTITreeMode
 */
private AlarmCategoryCheckBoxTreeModel getAlarmJTreeModel() 
{
    return (AlarmCategoryCheckBoxTreeModel)getJTreeAlarms().getModel();
}

/**
 * Return the JTreeAlarms property value.
 * @return javax.swing.JTree
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTree getJTreeAlarms() 
{
    if (selectionJTreeAlarms == null) {
        try {
            selectionJTreeAlarms = new javax.swing.JTree();
            selectionJTreeAlarms.setName("JTreeNodes");
            selectionJTreeAlarms.setBounds(0, 0, 300, 400);
            // user code begin {1}
            
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Alarm Categories");
            selectionJTreeAlarms.setModel( new AlarmCategoryCheckBoxTreeModel() );
            selectionJTreeAlarms.setCellRenderer( new CheckRenderer() );
            selectionJTreeAlarms.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
            getAlarmJTreeModel().update();
            
            //expand the root
            selectionJTreeAlarms.expandPath( new TreePath(root.getPath()) );
            
            selectionJTreeAlarms.addMouseListener( getAlarmNodeListener() );
            
            selectionJTreeAlarms.addMouseListener( new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    alarmValueChanged( null );
                }
            });
            
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return selectionJTreeAlarms;
}

/**
 * Return the JTree1 property value.
 * @return javax.swing.JTree
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTree getJTreeDevices() {
    if (selectionJTreeDevices == null) {
        try {
            selectionJTreeDevices = new javax.swing.JTree();
            selectionJTreeDevices.setName("JTreeNodes");
            selectionJTreeDevices.setBounds(0, 0, 300, 400);
            // user code begin {1}
            
            selectionJTreeDevices.setModel(new DeviceCheckBoxTreeModel(true));
            selectionJTreeDevices.setCellRenderer( new CheckRenderer() );
            selectionJTreeDevices.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
            getDeviceJTreeModel().update();
            
            // Load the child nodes on expansion
            selectionJTreeDevices.addTreeWillExpandListener(new TreeWillExpandListener() {
                public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                    getDeviceJTreeModel().treePathWillExpand(event.getPath());
                }
                public void treeWillCollapse(TreeExpansionEvent event)
                throws ExpandVetoException {}
            });

            selectionJTreeDevices.addMouseListener( getDeviceNodeListener());
            
            selectionJTreeDevices.addMouseListener( new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    deviceValueChanged( null );
                }
            });

            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return selectionJTreeDevices;
}
/**
 * Return the CheckNodeSelectionListener property value.
 * @return com.cannontech.common.gui.tree.CheckNodeSelectionListener
 */
private CheckNodeSelectionListener getDeviceNodeListener()
{
    if( deviceNodeListener == null )
    {
        deviceNodeListener = new CheckNodeSelectionListener( getJTreeDevices(), true);
    }
    return deviceNodeListener;
}

/**
 * Return the CheckNodeSelectionListener property value.
 * @return com.cannontech.common.gui.tree.CheckNodeSelectionListener
 */
private CheckNodeSelectionListener getAlarmNodeListener()
{
    if( alarmNodeListener == null )
    {
        alarmNodeListener = new CheckNodeSelectionListener( getJTreeAlarms() );
    }
    return alarmNodeListener;
}

/**
 * This method checks for extra work to do like checking or unchecking parents after a tree
 * selection and then updates the panel.
 */
public void deviceValueChanged(TreeSelectionEvent e) 
{
    int selRow = getJTreeDevices().getMaxSelectionRow();
    if( selRow != -1) 
    {
        CheckNode node = ( CheckNode )getJTreeDevices().getPathForRow( selRow ).getLastPathComponent();
        if( !node.isSelected( )) // we are doing an uncheck
        {
            CheckNode parent = (CheckNode)node.getParent();
            
            // only uncheck parents if they infact checked currently
            if( parent != null  && parent.isSelected() )  
            {
                //uncheck our parents until we hit the root
                while( node.getParent() != null ) 
                {
                    getDeviceNodeListener().uncheckParent(node);
                    if( parent.getLevel() == 0 )
                    {
                        break;
                    }
                    node = (CheckNode)node.getParent();
                }
            }
            
        }else if ( (CheckNode)node.getParent() != null ) // we don't care if the root got clicked
        {
            //  Here we check to see if we need to set our parent as checked and if we do, we continue to check are 
            //  parent's parent until we either find an unchecked child or we get to the root, confusing as hell.
            
            boolean cont = true;
            while(cont)
            {
                cont = checkParent(node);
                node = (CheckNode)node.getParent();
            }
            
        }
        
    }
    
    fireInputUpdate();
}

/**
 * This methdod looks at all the siblings of "node" to see whether we need to set node's parent as checked,
 * returning true if we another round of parent checking is needed, false if our parent is actually the root.
 * Return the ret property value.
 * @return boolean ret
 */
private boolean checkParent(CheckNode node)
{
    boolean ret = true;
    
    //  since we're doing a set checked on this guy, see if all our siblings are also checked, if so check the parent
    int children = node.getSiblingCount();
    CheckNode parent = (CheckNode)node.getParent();
    CheckNode check = (CheckNode)parent.getFirstChild();
    
    for ( int i = 0; i < children; i++ )
    {
        if ( !check.isSelected() )
        {
            // at least one of our siblings isn't checked so we don't care anymore
            return false;
        }else 
        {
            if ( check.getNextSibling() == null )
            {
                // we are the last node and we are checked so now we can set the parent as checked
                parent.setSelected( true );
                if( parent.getLevel() == 0 )                
                {
                    // the parent is the root and we are done
                    return false;
                }else
                {
                    // we've set our parent and we can return for more checking fun
                    break;
                }
                
            }
        }
        check = (CheckNode) check.getNextSibling();
        
    }
    
    return ret;
}

public void alarmValueChanged(TreeSelectionEvent e) 
{
    int selRow = getJTreeAlarms().getMaxSelectionRow();
    if( selRow != -1 ) 
    {
        TreeNode node = ( TreeNode )getJTreeAlarms().getPathForRow( selRow ).getLastPathComponent();
    }
    fireInputUpdate();
}

/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitle("Display Alarms ");
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setBorder(ivjLocalBorder);
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());
            
            java.awt.GridBagConstraints constraintsDeviceListLabel = new java.awt.GridBagConstraints();
            constraintsDeviceListLabel.gridx = 0; constraintsDeviceListLabel.gridy = 0;
            constraintsDeviceListLabel.gridwidth = 1;
            constraintsDeviceListLabel.fill = java.awt.GridBagConstraints.BOTH;
            constraintsDeviceListLabel.weightx = 1.0;
            constraintsDeviceListLabel.weighty = 1.0;
            constraintsDeviceListLabel.insets = new java.awt.Insets(5, 5, 0, 5);
            ivjJPanel1.add(getDeviceListLabel(), constraintsDeviceListLabel);
            
            java.awt.GridBagConstraints constraintsAlarmCategoryListLabel = new java.awt.GridBagConstraints();
            constraintsAlarmCategoryListLabel.gridx = 1; constraintsAlarmCategoryListLabel.gridy = 0;
            constraintsAlarmCategoryListLabel.gridwidth = 1;
            constraintsAlarmCategoryListLabel.fill = java.awt.GridBagConstraints.BOTH;
            constraintsAlarmCategoryListLabel.weightx = 1.0;
            constraintsAlarmCategoryListLabel.weighty = 1.0;
            constraintsAlarmCategoryListLabel.insets = new java.awt.Insets(5, 5, 0, 5);
            ivjJPanel1.add(getAlarmCategoryListLabel(), constraintsAlarmCategoryListLabel);

            java.awt.GridBagConstraints constraintsJScrollPaneDevices = new java.awt.GridBagConstraints();
            constraintsJScrollPaneDevices.gridx = 0; constraintsJScrollPaneDevices.gridy = 1;
            constraintsJScrollPaneDevices.gridwidth = 1;
            constraintsJScrollPaneDevices.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJScrollPaneDevices.weightx = 1.0;
            constraintsJScrollPaneDevices.weighty = 1.0;
            constraintsJScrollPaneDevices.insets = new java.awt.Insets(0, 5, 5, 5);
            ivjJPanel1.add(getJScrollPaneDevices(), constraintsJScrollPaneDevices);
            
            java.awt.GridBagConstraints constraintsJScrollPaneAlarms = new java.awt.GridBagConstraints();
            constraintsJScrollPaneAlarms.gridx = 1; constraintsJScrollPaneAlarms.gridy = 1;
            constraintsJScrollPaneAlarms.gridwidth = 1;
            constraintsJScrollPaneAlarms.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJScrollPaneAlarms.weightx = 1.0;
            constraintsJScrollPaneAlarms.weighty = 1.0;
            constraintsJScrollPaneAlarms.insets = new java.awt.Insets(0, 5, 5, 5);
            ivjJPanel1.add(getJScrollPaneAlarms(), constraintsJScrollPaneAlarms);
            
			//getJPanel1().add(getJComboBox1(), getJComboBox1().getName());
            ivjJPanel1.setPreferredSize(new Dimension ( 300, 400 ));
            
            
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
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
    // create our arrays of selected devices and points
    
    CheckNode deviceRoot = (CheckNode)getJTreeDevices().getModel().getRoot();
    CheckNode alarmRoot = (CheckNode)getJTreeAlarms().getModel().getRoot();
    Enumeration deviceChildren = deviceRoot.children();
    Enumeration alarmChildren = alarmRoot.children();
    
    ArrayList<Integer> pointids = new ArrayList<Integer>();
    ArrayList<Integer> deviceids = new ArrayList<Integer>();
    ArrayList<Integer> alarmcatids = new ArrayList<Integer>();
    
    while( deviceChildren.hasMoreElements())
    {
        CheckNode currentDeviceNode = (CheckNode)deviceChildren.nextElement();
        if(currentDeviceNode.isSelected())
        {
            LiteYukonPAObject device = (LiteYukonPAObject) currentDeviceNode.getUserObject();
            deviceids.add(device.getYukonID());
            
        }else
        {
            Enumeration categories = currentDeviceNode.children();
            while( categories.hasMoreElements() )
            {
                CheckNode category = (CheckNode)categories.nextElement();
                Enumeration points = category.children();
                while(points.hasMoreElements())
                {
                    CheckNode currentPointNode = (CheckNode)points.nextElement();
                    if(currentPointNode.isSelected())
                    {
                        LitePoint point = (LitePoint) currentPointNode.getUserObject();
                        
                        pointids.add( point.getLiteID());
                        
                    }
                }
            }
        }
    }
    
    while( alarmChildren.hasMoreElements())
    {
        CheckNode currentAlarmNode = (CheckNode)alarmChildren.nextElement();
        if(currentAlarmNode.isSelected())
        {
            LiteAlarmCategory alarmcat = (LiteAlarmCategory) currentAlarmNode.getUserObject();
            alarmcatids.add(alarmcat.getLiteID());
        }
    }
    
    //  we have to do this since we need to set an array of actual primitive ints
    int[] pointarray = new int[pointids.size()];
    int[] alarmarray = new int[alarmcatids.size()];
    int[] devicearray = new int[deviceids.size()];
    
    for(int j = 0; j < pointids.size(); j++)
    {
        pointarray[j] = pointids.get(j);
    }
    
    for(int j = 0; j < alarmcatids.size(); j++)
    {
        alarmarray[j] = alarmcatids.get(j);
    }
    
    for(int j = 0; j < deviceids.size(); j++)
    {
        devicearray[j] = deviceids.get(j);
    }
    
    alarmsTable.setAlarmCategoryIds(alarmarray);
    alarmsTable.setPointIds(pointarray);
    alarmsTable.setDeviceIds(devicearray);
    
	return alarmsTable;
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
		// user code end
		setName("CurrentAlarmsTableEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		//setSize(386, 256);
        setSize(300, 400);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 0;
		constraintsJPanel1.gridwidth = 2;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel1(), constraintsJPanel1);
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
		CurrentAlarmsTableEditorPanel aCurrentAlarmsTableEditorPanel;
		aCurrentAlarmsTableEditorPanel = new CurrentAlarmsTableEditorPanel();
		frame.setContentPane(aCurrentAlarmsTableEditorPanel);
		frame.setSize(aCurrentAlarmsTableEditorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
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
	alarmsTable = (com.cannontech.esub.element.CurrentAlarmsTable) o;
	int[] deviceids = alarmsTable.getDeviceIds();
    int[] pointids = alarmsTable.getPointIds();
    int[] alarmcatids = alarmsTable.getAlarmCategoryIds();
    
    for( int i = 0; i < alarmcatids.length; i++ )
    {
        CheckNode currentAlarmNode = (CheckNode) getAlarmJTreeModel().getAlarmCategorybyID(alarmcatids[i]);
        if(currentAlarmNode != null)
        {
            currentAlarmNode.setSelected(true);
        }
        
    }
    
    for( int i = 0; i < pointids.length; i++ )
    {
        // Find the device for this point and load the device's tree node.
        int deviceId = DaoFactory.getPointDao().getLitePoint(pointids[i]).getPaobjectID();
        CheckNode currentDeviceNode = (CheckNode) getDeviceJTreeModel().getDevicebyID(deviceId);
        // Only load children if not already loaded
        if(currentDeviceNode != null) {
            if (currentDeviceNode.getChildCount() == 0) {
                getDeviceJTreeModel().treePathWillExpand(new TreePath(currentDeviceNode.getPath()));
            }
            
            CheckNode currentPointNode = (CheckNode) getDeviceJTreeModel().getPointbyID(pointids[i]);
            if(currentPointNode != null)
            {
                currentPointNode.setSelected(true);
                getJTreeDevices().expandPath(new TreePath(((CheckNode)currentPointNode.getParent()).getPath()));
            }
        }
        
    }
    
    for( int i = 0; i < deviceids.length; i++ )
    {
        CheckNode currentDeviceNode = (CheckNode) getDeviceJTreeModel().getDevicebyID(deviceids[i]);
        if( currentDeviceNode != null)
        {
        	// Load the device's child nodes
            getDeviceJTreeModel().treePathWillExpand(new TreePath(currentDeviceNode.getPath()));
            currentDeviceNode.setSelected(true);
            getJTreeDevices().expandPath(new TreePath(((CheckNode)currentDeviceNode.getFirstChild()).getPath()));
        }
        
    }
}
}
