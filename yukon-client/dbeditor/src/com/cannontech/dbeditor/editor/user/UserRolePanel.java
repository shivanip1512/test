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
	D0CB838494G88G88G82F9ECAEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DF8D4551561D7D03A25AE7623CAF7A962EEDA59B62E3ACDD73A45AED5D47A55F621725954AF362C529615FE4696DB3A3D791D841290C8C28289A59550A0B1BF90EA9A23927263A48C98C90400A0F919F973C3A6B343FBEF12098458F36EFD774E4B64CD82E8647B8E6F5DF36FBDBF771EFB6E39673E89A958B5BFBDB925001064F4227FFBB3191032CEC27E7927FF8AF25C7AF72D96E27877D700AD
	647F9A536019F958EAB9FC2B5512C712CAB221DC8A6561D9D6CB913C671317CB3AEC709205CF8554BD74171C0746667365C14E47C24BB3AFE643B397208CF04DG338FA37F2FDDAC5671EEA8676A6F8849A904D89B314D37B6969B70AB74F625D0D70DF0845BAC3ED82A574F86DCAC40DAGF20435AD06E79615EEDAFD1D5E6E6C17E2C952232F2664A0AF83FD8EA13D6FBC7DD9C63EC987A2159CCFEF00E7C3C71F6B9D3B3F3F52AE79FCFE1FA65B5D150EDE5961517CCA257E882A32D2DF7FA17CEF733BBC3EA3
	12AAF7C8BE591B5D04B22F5C18C96901CE40E3AE459D5B046354816FEE0018A6AE770FF39AF4396674451244BB37C715BB210949CB1A43F2BFFE461591570C747F174E8376BB8C6590G53292A734D6B5669045F4B54A1CC1FB7942ECFD2B357A710097CAADAE04E814E36F07C1BD9085720BC8BE0E18B57736D57381E5D173ECD9227DDC85D9CC54F45ADCC3F55ADE1BD1B0F39B236D3FBFBE588ED262C957882F4838C834C83D88CD0C26D7C67032F433331D7EB7277777BFD0D0140912FC3B966F748DEF82F2F
	079A9DF7C2367B9587A14C161F5CD8E1A20F304F5611F60C40F4BBC5386DADBA71CD72F8EDDFEA3A09EE772DACCFD9235358509A562D61BD9C378729FD1442BB765B04F3C671BF227898831E4DF7A745E3F9BE5425D979B8F767F2D9ECE98BC8DE4D51940B09ACF33FFFB845EDE5B244D8433230B15E70890E71A240AF83C884D88FD05CE635543571B1FEADF4F03CB1EE83BBD3FC1237B3589B10946DC85063F5480ABE563F39B856D81B816B7772F577E36392FB50B8C78977361B0ECBC2ECD5CAD79B9B8FEFDB
	F8DC986F83CAB45E335AC65BC62E184355F42D4CEFE3F318AA704FD3FC1C814F66F60062313C846A7634DBAD2C7D5DF4ED4DE80F24F3BF454FED0F2473F69627938BF559C72D96B63F378F23CF8DC0F99640C20045G89G6B8132EDFC0E15D055B52E238E437C323E7B073641D38EE90A5466D3B549E717CFCA0AC76A754AAA515B548E6138760E6877F73A3E6D4063BD59A7AB1266717B003A54AF03B1292C4D1121AD58C611E9ED1727DF86867D814269368F65637CC92A76E140A1E9F238852BBFB104B6B1A0
	1D840AC0G5E4F5AF03CFECB7DF6853CDBEDFC2C9E3E0C3879D00E97637243212D70BC8A46ADFB6B6B1BBDF694C3D206380DBE3C89B6D7329C699C03B58450F15CEAD9F11C2F51673B39ADB6677C9B29D859E6EA0B88567719DD753C9FE90BABA90F99000F85D8863086A033036F4B27B7F5F39A4798EFB37BC439D03E7156906AB2FAADAA02877A3BCDE32CBF4BB52C535408B29B7FC3D751064BC12FE6GC7871F034CCF4A18ED625E4B2D71FD858CC9F604E70435CD3F04FB0DBCA07B34E6B9A07B9C70226A36
	10FDA9ABEC27CE3F30212D940FFE2B898CC9957862CBDB989EE8982D1E571751FAC95394CFEFD013BBE5CD7378DC617E65D496F504CBD4F5DD5FA4286BB068B887E02150751ECB7986FB33797DFD41C0972EA45DEE5B687E9C8F6D9F97737F6BCC0C5312C4B9091643754319FC6EF275F65763D32F4DD75FB602C7647CFEB6BC6E49C23D12C57981DA4E7B006B591455CD4C5756E7A743D94A6333560F833301054351ED786EE1F39B3EE7585C063FBDBC360DFEEF584CC6D95DC34338567D03BE034DBFB60C7E50
	6E7779E43A70C49742E250673BD08F0F4F231D72B8DC32566979C4861F76815AEE66A6345DBAF837FD407D6360EE6E9F77FC72F532647C1E9433F8EA6EDDCDA98BBFE0F69B7FC158BFB21916DF4635A4FB65FE9828A6FF5027B17CCF28BF5771EAAA459D5B6D433E656DDDB0062D9E596B40A8382D39428E4BDC2B247F331D247C02B4A005AA55C1982FCABA9915CDD85B022F7B4A5EF8631D325107028A27C7115BFC01A0A7326F643EEAE6AF79641DF60D716E00999A04E014726705891460G637F0D4B1F1870
	BFA1EBC38179F31062A09F0769F983CC0EA637EC6FBB628F35785043D18A9592B52BCA7668109527DF69179DD408CAE9D053A5E8245557ADC20D10C1EBF0C75841C9499B14EFD8FDF10EB235G6673AFA4231D2E827BDD8B10F6027B7C63D7301E7BFCFDE3EB72FB3DEC09C15D7089DCBF9B338B8CEB227484BB033B61496AEFB12DDFAA6A33F34C6AD7097AAF18562F95753939E67524136B715655BC74A57245208CA1A0CE69D1F90847F1D737C65451719671E17BD50CA93C067D0E615FA666EB3B09206F6F93
	745FA3998463BCC6137922B889E98694CFBF048738016B39089E45180BA8G79B1EF5105CFGE69F7FED4333794893B2346D427EEB33F81EA2865A4F850857E9A4F56AB19CED37EB93BFCBD7895C5FA86EC59036A41D1FD70E659973990FBF2F197AC00405DD3A8F6C0ABC477DF38A52DD8578B5DDFCBE9EA446D822D1D57DF68F8D2375318DA49B7DA3438513B74293ECD805B31DC38E717939140C32E21C2D2375B13EFB7F5BE4743921052699F82856B4C897CB173F89C2FDABCCF11358BECEFA64C1684FF28F
	0D59FC3CB6F4717D61F22BB1E732F8F3CD4A26A8E3D37BA19B132E8F43E313E0F3E5B05A5F973467C0BD43B565F05CF201DBAAF0E9A7B92EA517639ACE72DC4BD76A71CCEC16EB31530128F485BD15C14D030786C91328C3B066D99E25F19C46945AC91ECFB9B700363E884ACB8192G58592EF0E336BE5EC9002BB8455763CC0BF1BDBAGBF8BA06E943761610DE8F757695174F9DD3AEFAB330388DF46EDF6C17EE11D6F72EFAEA08DD737261865F8E6ECAECDD9FA0A4D475AD361F9716ED3D382AF38B2741C4A
	81237DAFC92C0EEA7FF55D0C062DBB527E6FC84371F483FE381B5B7F6F1237986C7F84D81C64F301E791F1D6CAAA73A1FAB9167225BE0470F82FB57BFAF290333B79D9F2D5BA4E4DC2A8AF81D885308EA06FB4172DAAF55C1CCACBE80C1C4A4357A5D775E99EA3DBF32E27DF33687748F5756B967D5AF32F271FD7747B914D3F441C8E1FD566985EE3CF0FBE43307C480F699ADAFC1A65C732847EA90ADFE5403335E527F8ACA7C1DD77994E3FE4D4FEF0711911BCD71DB1BBA76F9B552FB2223CA1229FDBEFFB4B
	F1CF217E4430BC6B6B4371895B3B3ED0BAE26F7A081D836B60E97B087B2BA516CC1D2E63A3EE0F159624BF8F4A715887E0ADC05ED970A7G9DE739DDDEC9B57367A36DB21AB38F1F0F99CFDFA64A91G5AB1027E4FD2F38CEB32436F71E9E2BD4ECF43BD5C2FF8A00C63383BA84E6ED768A1041D014FD1DFBE9BE8AE92F49353231F191ECDB7BFB3BD17EEFEE6FAB1FD6CF37F9F53231D2918FCCF897916GB05C799C0EDBA1F0098237C66024DC0E5BA0F04F503DA26F1C55D2FE0E630E29082B0532D560DE8FA2
	4E8B652967389DBFB100B1D9AC1417G2C86A09F733A55342E984A8DGBD9F337D0355F9860C6779E800747039C427EB56A6765CE8BC63B1791AF870FCD23A3ECE4D5A181D47F7D27CA35BB65339D4D5D925060B3B31DE772CD6C8CC4E324725FE1970F342090DD9FA1B964D5FD4A3566B0B7B0A4D572B61EE688725FC0F1F7AB15F63D7E4D07F846595GEB45DC7C40B2161FCCBBEF35941F9FB90E4C966BAC516D7EDD0B395D37D8F08D2A7E20E217A3ED5BE6A996F561155D26497DB8AC2C4D07343FA6A9F03E
	0F6CFF1676E7F5517A4B1631F392EAA67A6810A88F52721098FB39B1D770E871B9F4A74262EB0D2E3DFCB89CFC11F05CCB94D783385F8B5CGDDFB8781D7154CF13F2738A240B509F6A13A9E5F835C43A95CA67E24E08C2A797D5EAECFG7DB329B80F77272F61D468ED9CAA7AAD2FB4648F02EC89671156A5157B6EG14E7G4481ACBD4FFCB8536D1EC1246DD424FE98124E00ECA771BDB4B68E723EFB20DC87508D9082185B4363BDCB835AD6FB27DD0133E3FB17A24B8C7F553D181FE716C94F484D32EAD7BC
	81F638E4ED9E6C4D95ED1AE5279E84313A1F5AB709BAFAEA534FD14503463D08C8CC0F8AF89E86E0EDAACD772BEE28770AB67F201935198A75F3A53EBE9EADB52E0FB840AF15426BE11594AD9E00BD915E57CA1177A8A7A8FE0394F90F72E4863FC7496E35DA823D5C37A6D838EFDDA1F005F4FFA3F62B25404E4FABBF7A48984B665DDE1602744DE259F2BB1339569E0EE5198CF72A7CFE578DF5736D5CB6EAFAC64488188E292FFFCF71B80EC8AE9B35B466F75A34CC3D7FAABB77BB039A524CF3409E82F05841
	658D3D4A4FD70B7EE5813145341B5EC9A7F65524F4BB181C430E303C5D4778794A9564BC6321FE1D83F137850D67939C53E7D3D8BC446A9F9ED10FBE817DF2387E659175A86F1B29467A7D8346FA9C2357747ECC374776F25DB6BC96C73C7757C53DEF2F1319CEE77567B2F5B71935431D41465D553213187AFA3652F0DD90FE066D989467CC190F7D2F366358AF0772EA008DB20F531CE2FFA8F00AB3E0A13635C15981B06C64ED9F52F85BB9226D3A9D14AE14D783ECF0F23BFC1A52ADF041DC3BB82E5709B89B
	14FB846EE9170897DCFC1D9D37E0DEE505A8F753B266B8D8B9B163DA766C31GE90F3557EFA1AFFFEA9EE36567535C287B0B1632B15A2C53E5E47E2FA2936512E91252C14E527ECD19FC1EEDEE9EE3FF8343A9FD4F6A5B3C971EA7258B269911F6B02377FBAF406658A539045E966D466E498BEF305F716B6E47FC67EF28AF196FE63EB3D96013A8FE11814F7C59E50D7B33F828AB757071FAF48B7A7D98A84F83388FE0B940DA004C8BBC2E3FF7F3B59967BC52658F58A0G729AC2F5261FB7F8E35F8FBCE861FE
	633E7F7C86091F3EBEB9DA4E2974823313867D59236C26F8265F39FC544F8BF5B3G9681AC85D88530CE6877474D95CCBFB410C6CD136CEE1A0D634AE102AC0C4600006936A9B4E63C5897EDBFBCC7FDC6F1DF64FE682278723E487D70056DFCBF2C033AF9FD5C8F740D580F099776C0EF18E739B76A791C7A3786EFA47F1FB13F670D64EFAF647C4FC25D92AF677F58885B5D538FB1D1FF18272DBF9AFFC789526B690F646F2378C0FFA47F5F6D607CE320EEF5BF0F355617E2FC576BD7E8704B623B4DBBF99CE3
	75011F8518879067E367AA56EF48B9BAAE1C6D647D16C15BC400CCBF2C93BF6B6776E31B752E66B1669B77A90BAEA7C2AD7431826C66E3876FB2A876E778476458C1B0D80D387D507DDDDEA64A58A463B7BDFCCD8C4AFCCD145EFDA7A95D15C4F3EBE6EBA24E4F46BE419F0E93423E748C3D1B5614204C684E9FB446CBF59B7F96B5771797E07476684F5A3BF66BFE6C3718C2505F7FE0F81F22FD5AFDE4FC9FBAEFC2F9B09F71BFDB221F711E5CE2FE46CB5CE21E5BF8E64B58E72F6737C43F8FFEE90B71BE1879
	38778AB186BB8BF392821887908730B4407D5B142D2F906A5FCCFCDB0B4305E7CC7CE2C8637C1D7951F56D4F9F7FBB2301FC3C3F6875EB1A3F9F69F37CF0BE4D357B839449F01372F10C940F4B2D3126BA7E26FC9A9749CE0AE63152ABA19EABA586388F326FC0DB2E3E484B7D34EC95651736CF04EDA03F307D656E18889EBCFEE23ACF2671249B788EDF6431638B6D74DBA9A85FA7F0BE9B9F23D5E28C1EA844B1A9D7F879E9DAEE96E5CF61F872E35FCF37C6A70F4B6353FC4F41F47F850D4F5DA06B30424763
	669A1E231BAFF00F9E62E334D40C4105929C13821517B5DA2E93658D93B24739A3F89616CC940F70DE7A66CE3E17F6835F59AABF57FEF7A346F857F4175665513C72913A6B32F351551DBC573DCC656722EF494807E8D6CBB1C035466F51621DBC8E3582AEC6637C1F29ACA1234E550C89BBD66BE7BB6292E7DC0D732AF4A14DF5D04E8CDAADE5G3B8821772EB4B7207FB343311CD19DFE2E2F6F31CFE79B775866A756A76FA6D14E2BC136A6E68443FB6CFDB739756F269D45E570DC82F589GE4G4681208220
	99209BA0B0A06E218A4AC914F38AA059A0D09F2DC89E1F4668FFF9BB6AB18BE8448ABA41027DE35069D07C81D951069816459A2C5F7B05517D7D4942717D7D79C2FA078DFE3AEBA82067979D05749E4169D4E5312FF88A73842ECD6D702B9E4CFE31586A6F2131E2AB9ED3256B4FE520F342C0B836DA876FAF2490F2E7B20FF1A65BF08F2289B87CF8C0655FEF7C538EE33EE2834E4B47E0B303BCB67C0AC9CC99A772E73503AC263461F310656CD93F0C2323E34A1DC7F93F50A00BA9E743F3215E0F69BD4DB61E
	5E6906E7E4CC3998E8B5024E978957FB65E99CE3C3D6046699D90EEF6B3648DCA14A318CE8A4887DDF56C66B1FA7721EC93A7E59A19833903B2FE0ED6E2D61ED6AC2CC578EF8BA74B6CC57278F0D97BFF3DD4D626761D0E47C5C71BA4D290A7839D9F6CAC12F46CCDB1F6F3BCAA26F67D11E99A16EF35F5EB95E79F4DE486C0E7168387D7E92D1E67D2652FE7121EB5BCF17077837683B295FCC8CC51E77ABA8FEEDA8723CFF3F4C7D6CFA28BBBB44757DAF670D65A9165FE03FF4570D65B796D01F3EE4A8D25F3B
	A9FE79D0243E35AE2E6FAA28EB780467B7FE3283EDEEBE149783A4822C87483EE4356C812883308174G04GE683AC840887D88E10G308EA0ED187B584BC57BC8F49F2BA702434E1AF05F67AF0E6EE3870A477731D70B4DE36AE94523E36A1B0B4DE36A594546181A61EE5369AA62520D616FA846BCAD73E747A41F64B25CAF5D319D67C91CDF7475466A96EC4775065F1EFAA42F67937A29D3A7B47439C66F3F6B5EB54331BDB981536C7C9B32AE5C204D6E65413B6BF1414A9DBC97D8BC4CE30DD525BC5669G
	5C0CE11E47AD93714A12E19EC3B434E35B75D04E3BEC3514DF66B4DA0E0A5FF981AEF419537842D19E0744DD66B49E20ED57C019DC01FD850069120D2EC01733FD9B17337A8A6FF74FB66C6706F28C622EF05EF5C1AEFF9C6056DC613CE302DC7E322B1C46935558369B4A810099D7B90D2F55F2796783EE55D5CE63D735DC7E3C98CE2368903635C23987A09443651F37ABACFFC28967BD9B6A599D6E127C9CBA67DE0FCFEEE23FA4A1BCA63F030E75B2E81B9843636FB7E544A5C1B9F39277CB7B1CC4780C8306
	778764094F9F2C789CF8443A42BA5DE3F82FF7CDBC6F4E89652167B4F6C477EF4F6D985F3F6DA9B177EF2F170C76EFD5A5667E6DD049E87FD60F1FF892977865EE516EDD01BBADF04D14A662CEF99C1ADB605B29DC08374A481063BB28DC0394572D63BEB460CE6B38B3A598CF014AEE3FA262E5FA36F4FA3CDED63653B69E9F0445E2BC5CD4C6CFC0B2485DAFF027455CBE39E32C7D35E0D2346FBE1EAA3D3138E76D387D4C631E3DB46EA9B711478C9AA67138A70E7A650EC911F140F7A87E6C2448B860C83B38
	37053A65423F9CBDFAE3714B699B6CF76BF577E3FAD559D07E6A49117A56D1FC4364C8FD9F947B13956A96CD667A3E3FEDAC7EF9D32265667796A13DBDD3A2F373D594DFBBA5B2B77F6DEDFC0FEA063A58A91C7FBB635E7B185B17ED5CFE6676B50D764B1EFAED76D5B6155B57519A2A575448716E227843D3A3477B6755FC3CEDD097B7156B7B2F35B7E6A7775FE03F672F3B9F536B963A4797CC0B54F78E4517CD0B54F767A12EEF85544D1EA6F208A5E371CF02F61953C74E85BB337D7BCE74339A2610DA7DB4
	BEE6F7390F52B821947A14CF67E75163F2BAEFCBF82E66FBE504085F0F6E60F93C6E69BC36E820F40621BCC760BE36A2AE964A0B27F3797F231D08317C1E615D5ABE717B3174B970B8E48B6B740E617D6123934FBBF1C2F930B9F5EE63F33FCC4C697EA27A9BCCD1BEC44B85B77172F737CD04DCC9530DF781BF1D909EA36F826651783F9674325DA43EED2DC6DC8F14B5216F87B561F9BFE3F87FE575445B40064F0147178E05F53A5D703E7B504473EE18D09EECCE5717F09B0F9173DCC36F0196C1F949CD7CFC
	3694BFF3517D6BFE996F7AFC72E01978359E7A5972D79CF09E773BDE29A82B2EA82B295DC774EF9AB3CBF85EEE8550DB8310B983FCB000F58677431DD1FCF69F7535EEE8270936C16BE81F7D6C2BA89F7B6667723632B174DEB843784D4F53AA6692E8FECFE4B2599713AC3767ACB266766221AF5EE9A58AD96E8D1A4B7D98DDBFC9500E4C64ED872AC74B7DCAF9D86E4D25E349DDBD53A83735F47437CA67CB79F93BF9A6BF03D70A33F477CC7E3D1E33E45477FA49466F6D6616C4FE2F07B443750F16C4FE2FF7
	F4C47F8DA5115F6B717ECC670AB74236F4427AD7F3DBEA04431B97CC29C6B7A53D7D6D5B0C5FAC305C4D05CBA36EF5C67E2602FE354746622BC57C5B3A59B339DD4F2F8A4B52D9FD2DF62DFFAFD6EA14C51F63D21449A9F95570EF0B8E0E08E316816F0419E347102C5F02A26C37AECA5B341B2D16621BC75631755BCE673C826AF0DE986EFD0AEB865C3382779DBA9EBD000B39192F7DDFA85C9F4C91389CFA67998F6565B7733BB469D50C59749C9CBD4EB2FCE9A8A70C6747D9FDEC9EAFC2DBF2F8D4EA34B8B9
	CDFEAF9ED9F57B7DB515A791F82C85E03E63E5719B4E6CDB2C16B20043GBDGB13770F535C7E3F7C39E8D89B3DAEF09EFE3675F4267FAFFCDF82E1FD450C645BCD3C1AAFB3D123D0F594AAE152F1B7BEE616B66149A39EE06C62C0B196A586B6681B5F25DBCB8E25DBC27C6DBB77ADA51C65B569E55E8DBEBC1D6B2EBEC5BE2F37A164AED64C1E18F07D5EEA328977B7BE205B57140F451F3A49D66D1D548E6B1E4EDB41710B8F030BE7517F726261034B61241F2ABEDA48D93A4EDA41D66CF5AC82A668F048286
	3DB05F112EDEA4D72806E9BAA0B6DBE031ED26DE13B1222D20009736C195B816319F99ABA4E7444A7D41578EBD337C2913EAAA49E8A35B1D006914951AB3169D787B907D0F4534122CG7E36G7F0681482E52C73A2A8DF9E59BA9F01E409FB32B1A6CE86459E8D5A305B664D4053755D5D6C9F5D353FB4BC652FC78E7ED48D6D66D15FC2EAA161AFE0B8EC0F2E38F5AA0D58E07B31F4A0BD4AA85157C87B66F7DEB7B6A78D41252CAB6FA7D1223D532EBFE250DE4E9EEC5D65DFE2FA394F877B0A514C93FC3466F
	96D301DC09D09F152D44A5A6046E8BFDF5D64E1A1C5694C2C28A2922ADB5E8D0A9DAB81A5C12822CE425CD43BF5363D776083AC9FF51DE7D61FBC5E7D3C8322EA44EC8D6A8203181932601AAD73602545A134FBD3260E78D6B56C217D612CEFFE9G0C73422D0E877BFBE5E57A225BF6EE1A13F88B4A95406121F31D23118C27546F718E41CBB3753F9C1F25A912CF453F97GCC4BA2B466D5C2704AFFFC247A362F7F618C95BC5CD5A33B5894C157AA176C6F2FEAF4BABDBED99000798272BB85F9ECD39951E6EB
	68CB056DAB7F5C8B7655CCD27B2B2B697FB5747F8378FF8D4554D0CC4D811834F5E44CFF117521906A3CAD2411CD0648C4A3B97AB524EE77584892E1DD49943BE9141924A1FAB32D5C024D7125E05498E7308CA3EB06A47025089637537FCBEFCC155DA1D3E1C994E1D3479476B31229A284931DBBF2ABC83E2BE8CA5E978F86D3C8E6AB9A22B760165AF4A33611CD3D5431619793DE0D64058D097685AF10E28797D192465B914FEABB83129D9DC516C3D27AA88514217AA31265D4DC3D1401C6D2BEEA7D885852
	92D69F30110D1A9BBF2243952B1122B0698E3C8254AB180FA1FF868850FF6CFB13FDD70D5FDE2E1FD694755B4B2937323DE36E2D113F3BFE69884E498A402F3B155FD3369C41C0AE5A376F1D7D12572BC95E3EC61F833F03BFB22449616F8F7FF084692B3C0DDE496A121BD06E8B06BE7F8FD0CB878841429B31209BGGC8D3GGD0CB818294G94G88G88G82F9ECAE41429B31209BGGC8D3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81G
	BAGGG5A9BGGGG
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
		constraintsJPanelLoginDescription.ipadx = 160;
		constraintsJPanelLoginDescription.ipady = 56;
		constraintsJPanelLoginDescription.insets = new java.awt.Insets(6, 8, 14, 4);
		add(getJPanelLoginDescription(), constraintsJPanelLoginDescription);

		java.awt.GridBagConstraints constraintsJPanelDefvalue = new java.awt.GridBagConstraints();
		constraintsJPanelDefvalue.gridx = 2; constraintsJPanelDefvalue.gridy = 2;
		constraintsJPanelDefvalue.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelDefvalue.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelDefvalue.ipadx = 178;
		constraintsJPanelDefvalue.ipady = -1;
		constraintsJPanelDefvalue.insets = new java.awt.Insets(5, 5, 4, 10);
		add(getJPanelDefvalue(), constraintsJPanelDefvalue);

		java.awt.GridBagConstraints constraintsJPanelValue = new java.awt.GridBagConstraints();
		constraintsJPanelValue.gridx = 2; constraintsJPanelValue.gridy = 3;
		constraintsJPanelValue.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelValue.anchor = java.awt.GridBagConstraints.WEST;
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