package com.cannontech.dbeditor.wizard.device.lmcontrolarea;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;
import java.util.Vector;
import com.cannontech.common.gui.util.OkCancelDialog;
import com.cannontech.common.util.CtiUtilities;
import javax.swing.AbstractAction;
import com.cannontech.common.gui.util.TreeFindPanel;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import java.awt.event.InputEvent;

import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.lm.LMProgram;

public class LMControlAreaProgramPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
	private javax.swing.JButton ivjJButtonAdd = null;
	private javax.swing.JButton ivjJButtonRemove = null;
	private javax.swing.JComboBox ivjJComboBoxLMProgram = null;
	private javax.swing.JLabel ivjJLabelPriority = null;
	private javax.swing.JLabel ivjJLabelProgram = null;
	private javax.swing.JLabel ivjJLabelStopOrder = null;
	private javax.swing.JPanel ivjJPanelLMProgram = null;
	private javax.swing.JScrollPane ivjJScrollPaneJTable = null;
	private javax.swing.JLabel ivjJLabelSelectedPrograms = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldPriority = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldStopOrder = null;
	private javax.swing.JTable ivjJTableProgram = null;
	private static OkCancelDialog dialog = null;
	private static final TreeFindPanel FND_PANEL = new TreeFindPanel();
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjJLabelSearchInstructions = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == LMControlAreaProgramPanel.this.getJButtonAdd()) 
				connEtoC1(e);
			if (e.getSource() == LMControlAreaProgramPanel.this.getJButtonRemove()) 
				connEtoC2(e);
		};
	};
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMControlAreaProgramPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJButtonAdd()) 
		connEtoC1(e);
	if (e.getSource() == getJButtonRemove()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JButtonAdd.action.actionPerformed(java.awt.event.ActionEvent) --> LMControlAreaProgramPanel.jButtonAdd_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonAdd_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonRemove.action.actionPerformed(java.awt.event.ActionEvent) --> LMControlAreaProgramPanel.jButtonRemove_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonRemove_ActionPerformed(arg1);
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
	D0CB838494G88G88G4AD499B1GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DF41447F1B84106BE73D25BA5F9A4A1AD414411ED1A10943F1014B4B4A12F58418DE9F1C2EC1A28B1C95456F1718BC9951B542B5389F4BA7DA2B082A4F100GE10BC382010504C03FD8A20710A5856404976B136E13F470696E387BCEF78289BA33FF5F27531D845874056751FEBB3B33BBBBBBBBBB333BE732F5773C34944BD6C2D252887F77F68AA19BDD04BC1B16B0A2F07E941B15187E6D8148
	A3DF30A3F29A144DBEEF336612DF5726C3FEB6645DCDB66BF6784EA78F684B9B60A3CEBFF3216C7C4A32BD1377332DCA74331966FFF3BD9352EC00B900DB8A10EE417E73EE94F0FCA2603279B7C20A0510B534CEFDC701896F3230FA8BA1DD86B01A0AF54AEE9472F2876431CE9526292C2E9BD29B656E7E6AAADE6F724389E443B3ED16AC6C4B543E00651CB609278E72FDAA08D4E25D5E896929762B83277BFCC34EBE456B75F9F535EF50696ED55D9E5D97F006BD5715005B69D607BDFD2AD39B6A038A811F26
	84D4253A5AF53E11E56BA15B9E708D841421F6452BEA245B0AE39968C8277DC01F10DFC7F1BF6EC0794D056F45GAB2DE2BC5F4BBA45791DFDB501A4BEDA9FF7BC25E9EC9C2EB4E3BCDD3F7DFE6A46B4EC7F77B7B2B95DAC48AF87E03CFC66066025EE1360256682560DC07EA10084093734A3FE996457G2C5FA4F82CBDA8F86C3E7195125CFB4D12930747744D0C37724D860F657697B66D20BA74591328879A14BD8430GE085C092C0FA3A4D3A0B6A6E652AB7A12D6F559BFDC3C3BEEF3D5F5F20398367FDEE
	D5036F6AEAA861388E354F97F0934274D36BA80B410FGC77AF85DC4E0E33BC404BEAD6C7882517581CBDA0C31ADFE2E5DD2495BE8C9B746B61A0EF26B6CC4398D42B752A1BD437F0962171970EC2EFECAF581732B212C4AA6645C1DADF8F1D967135C0A20E5F38CDE66FCF350B25746F8D8ECB3F8E1B2FE2086E51C88780DGDBB3EC56B2G970016A1E47CD8553129E45C8A6BA160D5344ED02FDF89688DA10F66D683DC56FF3CB1192CE38123735CB19D13CB42B1739C2D7D3BC0CC392CCD38E419194164B1AF
	43108B6BFBFE5C3E17E5CC540DECB907DF24EBE5F5861B43D4097FAA452FB361595C56D3BC66B7C0D9379D6C9B2DBFCC5756D2FB340E5CC7712B6C51BA125EAEF4E49D1435E45A2CECFECBEA51CEAE047C8A00C400E40002AC1B359220A5CB4C71ABD5C7EEF39D351B661751BEF5728DC855089ED0DA3DC1DD717629971500C7695554A061F57E61A44A35F79C5D37687A6E03BE5ED13DEAC051BDBEAF342E8C2920CCC1D6E76549BC2C830693CB3BBCC3AAF4B064A7225D55A773A15514207EFB3FDB51D52386AB
	DFFB92F5E2D83988ADC283389E32D0DE9F25B6F7903E27E789D93DFB9AF1EBA13FC14A65D27596C8FF874A2DEA5555CD1EBEE4C3890C889DFD2F93E95233ED56C559E2DD3E5CAD742FA96BEFC959F357E26A9F5D13F218AE69B9517A67266D4E857C529C31277ED7C7779D2E078976B6FB3C6EAEB265D99F564E8F5AC7EAD4BE4D744D647C0DB3282BEBE1DC49G8539C2560FD4BB188EB6A8C1D5E85D05GA80C6AB6A44F6A7EE58D6EA96A306A551BD43F6AF543C7104F79A7EAEC06BE767B242E4C25F834CF0D
	20B0C1091FDF1347705006D93BC5799334DC51750027B7242B1D2A2EFB3C83867D17A9AF41F126AF48477A3FFDB856F298E38BC0379CEB4D29FC13DE39FC3E2BA1FF972E982E1FDF257B30867527670A79FF6A827AD889B27F8D1AB74A5F3970C1668E5B1D5AA677FC20BE22F5A3F69F236DB8F6E1A799EE86452D829E1365F8335B3BCD75BE084E466765A06DA3B5CA0FBFACE03A602C0D2F4B47EBE36BF2CDEDECDD2E2F1DDCD7DBEBE369AAABEB2F45B17A42DE136E7F3E966D9F3833DE15AEC0C9C218EF7CF2
	970E4363756817BC6E81D56F74DCD7416749C39D7E9435318E782E4C9336B13CCF5846126B0F13E56F3BE27ACFF3BEF755B2AB0F6F4DF906EDE4BCDDBC0DEBCA5554A190D423AF645539AFC86DB74787D3A94E5275C924ADBD579052F558575C6EB2058E444912F6B5506F8B8C296E52AB4A3062D4423A13CE04331E96B76377A1476143279DF13D7832AB326D776B239AF545EF35665045C365DC3772492FCA7F464CE707BA649BD66F992B2C79BB6536025B348A157A29B0AF2B81DE4A93B6ED4DE9AC97B60D9B
	6EC61F26B155013276AD28971F685CEA1AEB6B96969FE64B72E7631633FE3EFDA697F5D83D96D24155C02379BBF5C4788BE736C415DDD4341061072CBEE307D4B359DB06373AE36BD4069BED6CD559FE36FB93C1FF0235495640A2855B7487BCC330BD6186426358D5B74CF16C9C9887463CEBA0FD89004DFBEB88E596BFC20438B41F6E876DA25E35C23E9060D4BEEBEFB41F4906559BFBD7544B5DEA33168234ECE575E6EEE575D6ED45FADF12B1679109DBC1F1BF8443F73FD5785267F34D7133BB680E39DE91
	36EEE36D17EE0B0EB17A7B315D9640F7EF933AB253ED5E7F6A03C1DF1F073AF8FCBE5ED64DEB1961CE29991076FB82C108B75CEAC44CE9030A3C228F4851FCDE1EFC2B16CC74E91BE944AE5C0B46B0D56FAEDFA33821AD20960D5254DD7428E1A264B0F6569CF7AF4DF94FE20FA3870D0FFF16C98E9BB2DE5844F87AE4B546D2314E927AA8134E0110479952BD68ECAABA58FCFFC8B71F95D40F2673712D5CA662707EDB283B56B7C0AFGAA81D8CC7093999338GB79D002D2F87F7185757D2402F83C85FAEF572
	5D2338075C316D91F2FF356CA424F7ED95254C676757F19ED7FEE1BEB9F52BBA66B9424C1CA6CB61F6A66B266D064C9F5A7D4C2685GCC960F3DABE4719054D931DD48624B195132C0FF0A9488D9E4F5FCB0D9D4151EB2E4F1D7E6171FBB9CB52F43E5095D7157E181DF0785516B705CA06ACE93603B8B44BA7C3A1AE7DA079D20790AF7G2C22D8DBD77BD3197D63F9DFFF1A30FFC47873B3BCA8C38D5A1C89B08FE089409A59472F7B27BCBFE80ECCF2FEF0E6B4BA96CFAEB0FC554C9D937DD696ABBFCF5705E3
	870B156D921FC471F5A6BC53910D62B15F83E54BF788BFB329BD3A7F521D637BEC0A4AB33ADFCF20AB0E4A8FC651B11D49695CA1EC84EAC644998C038FD7DD4DFD83A24EC9970F197717E9BB196EAE00F449CEF11E774D9D691C1FD5BBC51C7BEFD7F00FC85FE53316G1C8268869885188330F41718376601D876F37C3C45B31EC68C45E431705D3B136157A650C54BF06D2E71F4EC5C7F11A9463DE11798F711063832C298F32198637C01AC137E377BBCDEDD6A7E05C15C6BFD814F0047ABF0DD9457678BD0A7
	1947221EC9E2D1CF1CD85493A79675CC910BFA627977ACEE3CC87787970C2F8700610E5DC45CF4484FAD94BA1DF88B576CB248A782EC84A8A8823994097217E9F9CF9173A1F0FF188E5F8BE4F960565D4401093C9F1147FE1107D3C531667E413B6A835710AD2AAD34E17628FAC5947F749BB9D45E41209A244A0C3BAEAF7B393E0D4408FD5A14A115883FFD773BB6DE27D977B5D648B57C4BC3853157B05D4C190E3EB4A6F4F4F911584B077CE803B6C2FEEB314DDAD6AC647DCFBB2257C19314F597B339E94593
	EDA053171FDF092F1BAFDF092D1B9B2E601A887AC201BEB5DA7FDE3BD2A04B0C555E2A2BC3A8966ECBDFC5FADD89C0BC98CDEF3FDAA04B6251EF3D9ACF47194CDF9CA77323D2DFDE4E0C1651B4104BECAE1B0438B25A2B61BE90117983B4BFA27B9B9A45B52FA9A33E909807525DB8CF53C7056DDD8A6D2E865888103B1B59E02683DF8A234E7503AF8F6CF77A55BEE2F5A06D391B02560F758196GAC87D837DB789D8B681953394EBE888AB45419F3ACDA91BC052CA863F15122AC3B1C09F34EB44DF51E07933C
	6CB75B712C761CCB69D535CE95A3B3554DEB84857D232440D4C789748D6231EEA0C427AA48761EFDE45C9D1D030DBB8269AC07702D361418FD2B853C4E8AD95E5C962BFC2DC3687CABE36679DC8FF86B9EE38E8B76445B77DF21EB28EC8F5B5FF3A4BE659A623BCCF8366F87E9FDDA8F4A1277883B79A33AF76AFB412F59ABF0C90574DC96728BA52E35C85857A5921716A5E85377895C62ECC1DBA8F1BBF284ED69BE6137AF9C95F7F39140AD5AA77429222750ECD350E32C2EFEA760F1B7A883AE2A1D4C2EEC98
	9567E7C97B04DD59BBC6FD21921B358A203DC41C47C48E0838E661E37309EBDAA066FDE2E257FB1641929E571498FEE5137D05CD2CCF7FCD51678AA8A77B917757B74D730B3A75DD8BA1CE6C16162F9AD70E7A0EF657A8375CB217A33F76D4F3F96538F21451C1CE4F46761D7DE2EC9B3F3308E8CF75443DABED594F6DA8CF6BD2E626A4BF778C0F59861A0BC8CCDBEE2CCEA27C0EBF92E153976C97323FEC23675D105FG10F9C04C67AF7B636C91F4653176DACBC4FBA58704CFB75AA6F0178F88BDCA27721F8D
	798409EB8EA3EEA56493A5EEF9C4681B7520D8E77F63C73D6A023C8E7090C08240F200C4GD2AA6AED3E06759C102FABE5E790BD10CE83188730CC569B717BE348985B395DBD3C0B73A072BA67853F177374210331FC06B9B17B4E394DFE051E1A7D871C2BAC7EEDB77595CD13CF8A691A9F7202B576FA7A5617223CB76FC45C37F08D158A5B7E1DECBCD352E1632DBC04EFA906F1EBA3F38EE17D1E2226C9F83391431FC108G8DC291576BD907581B8E7CE67D9F23BC7D947B07B2565FA7F5233F721C497ACBA3
	421FE1309EE8903671BE2CEFB2BB12A2657F6589315AD44068165DA55DEF6E180E6D859B28BD4CFD136D85A9927FDB0AAFB46119CDAE9D93B639944AE63DA9E20A51902E6175104FFC8B62A900D3G1781A2EF099863C98D4FF9A70D233AFCFE97B8E91A4965E7638B5D3C3BBB70FF5CACEC5F622FBDC116BC6008FBDEB56BAD7E3E0627EBDE1F1102A99BDFCE984737964A12818A4BC007GDAGFA4A4478FE291531716151DA3D2EABFD037454CE8C8E8F528CB4BAC27CAE0908316950564C32716BF9EED93C7D
	FD9535270B4A226F581F2778E5E551F76C3DB6F1473E8A79BFAC6CD4DB09D936F38EF3B970F45961097DB33A7B5A4CF42BF93DA41EEE0CC147E3051B68C7767A8254A1E6FE6489698B66BA4187GF083C41C2C8D6E4F05A77A1F413020FB48497C4F84C817BAD92C4656768B11495636D853E2FD2F865AA4A7DB5BD4F6CEA6474D120FC3C3F836FE8E34385757600B982EA5AF7F56F16A13525DCB9671B2EDB31E5D9A37B0825FF2A8C752F46ABEBF3DB560E5D9EA3629BD8F84627A0820CB3E14CFF96974FB3CE7
	BD2A661695D87993BB370EAF170DB35F7374B5336F10FE04492D7C0870CD17CC70DD313CC916FF7CF82C72EED91ED4982BDC1365EF0EEBFF9A6FFF31ACCF4808D53E620858C35695E2FAD0B948577B3C3079FC30AA63CFF14F87579EE13A351E278B0F4ECCE1EDFC770F66B6F2BF55E2C90953C6E9B9232DACB7FC415D14AFC27D9F710F6DB71F26EF94D99F7F1EA96CD261130F12269FB8AC7B6274B1485BDEE26AE3147CA0150DFFD1B69DFF60A0095F6F169B0408B57B6A18711DFA5378F60EFD50778ED36FD1
	6F5C53BE188D3EBF94BF4E1F950A9D67BF920AFD86B5A7B4799D723CD07CBB644F074CF748EC8F79498D546575B00F19953046G1AG3AAB447EF1FCF8AF217BC70C3D2359BD00678C782AC8977D9704630FF5F7F86A37C825E134C53DBEB0C5C3583E3C078F63FC011D20C806BB96C69985BC8303BA2B4A71A729EF2D297D944DFC6BB055C19D46B527C2785BFFE1C35CC2482F1638F5B66103A7D5083D687B74D1ED55D1F9BEC773DDB2BFE8B36B90567B7075743EF1FDFC58EFAA5858D59A4FC4007F19C705BC
	AE0E0938F82E44FDE5CC48A8C162665038A799726947846E3085F10510AF1338C7AC02366A1808297FF4D378GB381376C1808413E1E013E59ED5D47F5F9F4CDED203BB70B439E3BA96E62120F09D8EF41B0FD6FD389BE92C0CF25687F53E1995383EED1250865DE8C4BD83DD234119721317AF1609F209DE0220DDBFFA6960E593D9035F5BCF655F61118B1265845F95C9A3AA646B2F8DC70765FB2761CA7F1AB0F8B5C9A095BDDA4F039A704DCEF1EB8E0166BD5ED40F9258F2D0C339FF7E3E4C0EE4DA2F6D6CA
	848FADA7C45BBF4E9038E9A7044F3D174AFD8964D71F90B6641970DB10DED42ECC3C6585A4F8A3FEB5200FB0ACFD9140EDD351ADA257F4EEE4FF3CB640921D8371DD683D227669DD4AG91362D05200F35817848FD9BFCCBG97C08FC084E09640BC0045GAB815681A483E4D6592C8E00F200A6006EAAB10E4411C3130C039F76D00398D04CF7957FF9A33ECDFC654654B671D57A5ECC85DB56B5626767302F5F20779F7D7DC1D55ADE6BE82E4435865BFDC18F9E8A32BD79E384E34FE1903427CF515861C80392
	E877E91E3E919E0FBECE7575B000D6A5FC19F9B61465A038EF57E16DCD2494316CD2EAB771CEEAE11570F1DF33CD6563EEA6060F3B9E682CA78D9F37G3E1F34C01BA952CF5DC1FDD8FA58EA78306C4CB3AF43FC66D98234F8BF5282696513E2ACFB77235D6E07F1B01EBFC6F5DA0772E9A7C57C7DD18B6A9C637F1F472662FF3278FB51C9E69B7208607F0BED637B7FB63519CB215E8A49E313E57C794306D4031F47ADD371E3B34BB30A1FE4686307DC667C9D6B14712B35BA56B9CEE0CA3A68FBC0C677A425F3
	D45F5E7ED8D9AD5EDB97D07B5CD29DFD9E3007623B2A234F831E9896763C874A16D70BFD7A33613BBB477862DD52A55DB19D9B577DF40FA92D099E6F47A83E32A6FA3C5BA3E23CF5D03620C60C777AB56CBF7DD4ECF9974640B33A8D63E26FF2DE2F09275DF1693EF75CCC2771FA53F8BABBAEDDC91119AE0157DB4A53D5F1697AA91F6B620CAFB9AE5D3A8C244B2C0DCD670801E7767560CD7C163EB75B2A04FFCA4F4B712D25C751BC5769F329CE28689D606F9FC97CBD200FCC3D87E8A436DF4C5EE10C770B43
	A436DFBCC64CFEB143FD045A0B81583B3AE53D9992771E444D22EDA26E12472D8FCA7CA314AF44372858210CD3A8DFE10A6B66384F18F06F09BBF5027B988C993698C17BF9C26D24C75378999D2D63714236A6653108726871ABA63E1F12B8641B5953919B5E7B1A5CAB2769E640F93139232B35315E75FEE7E34785172B213E637DFA57257A77BAA57D3F5ACECCC25FF22123758F975A3A46375079FEDBF3F3D3B3986BBB738D4B68EF7B187D3EF2A34E7D835D7D787D03743FAAEB05FF7A3A9DED01867969GF3
	EB453E6646E962E3BAB7063F8B742AE107FC5CF496B625523D7520F45A5EB2C7857CD7093E8E231D0147137CFBDF36514E9E4BED34D3EE34F39617G3F3FDF161FC567D473F855C676AB93A26210EFD29FF2B1703E32D6448DBF23F7FB09105FD0AB6CDBB1BE8194EF41CD5F0B066F7D3941537F8FFDBC98B146B45B74DD943977FD1F3827FD30B9FD1846B564344DDAF0DA4CF372B5919F16C81CFD3F08CBF5097B51AE91174E1438B30502F63644A59D97346BA42E30D850A6CB5CD8313CFBBBA3F0B5C50236CB
	62322D02F6D062FCD6C12BCB5C9875AD17C3FE2D442DAB11E78B9277DA0628D7D5A7F0176D22DED31DBC8F27FB566C3A58FBD6C28CBC739902197437EAF5513E44A8452F280B76A552B205AF319A4A2A6A05DF7A431BE87FF58C33F10D9361773FC2576ADC2817A06B7E36B0CD54A5E26D3F0647535CEEA4DCC303227BFCDA17475F857B9E2FD30CDBA877F5F382D35DFD09788153B5756C2EED9FAFFB89723FA2663B36AA2AC3490037B6881BD83F4D686FE2616D1C47C75F6D0973514A86434FE67D651D407E6A
	G6F127D395FB07A1BDDFCA731D1F4FF238DCCAF0C7E5CD4D7E782FE366CEF46F6233F872C37934B446BEFF983BBBB90F7B779E4F6C96CB9FC224068733512497AB40FAFD61F2B20CF0C6F8ECA9DFAF60751F650BED55B316EE2C55BEBCDED7333AF3A8E13GCF9A45BADC19A156E1E6233CBF476541ED64C7CC5F764CFBEF13CB6EE99FCC4674A729FC0E1FAF3CDDBFA13ECF73B4EE77C2B79CC65B7F32EBEF6C365D5034EFG9AAF670D73395FE334F32844E8A737784E5AF16DB75AF9AD4DE847E23D33F60AB7
	9B6D54279B5FEE1B5166E8C66CB5B2037AE26C3D590B725C229CF42C89408DB09DE0C123B8E7335945B96472C671FE67EDFB747B1D07D34C6FEFBC766877BBC8EF14FF5A9E7DFEE7BC7D33766877BB021E7F3E4FFE2731AD7B7FD914246F054E967E022469A4355E6D5649A676C2AE98A0B9D2742D5457CF9CBED61D7A62232996E2EDA51BD86C53CA2C98403412B49A5F3412D408FDA2FEBE8664F9A700A5F80D5CA4964EF59DG1E0C25CBAC4AB303E3F3F412D79F8CFA863C2A5BA11E5B6964F0E92366693B6A
	5087D5070EBE37C3713A9D7E009A8CBA6AB55D71162353217B9CC17A866F10EC1918DA3640183246295337BFFB7CE7ABFFF4B118CAB635129DF892542986E840283A71F1AB7FA574D9E27363EBE77C419E8CB8C8133420BE2229AE32353F83FFC19554D5F73D88C5015FED74178AC770B87CC80B929C24BC573808550B3F59A6DBD83126F8870E30383406CAAD253E07720CB314827C66D3FE319527ECA57F6FF3766FB937EEC9AA311CA5991ACFF11FD57AF4DF20155874C1904820CFF3C77CE76AE8C36974509C
	955F8A4D6D12434741BAF191C8262FC6BEB3ABABA96BAC08AD92A0C7E8CDBC78F24A9A6E46C1A5GDD2901D69DFF036E8B1448327B0A75834F3E337D32052470C1629458A3FE1D89CC6A938E59F99638561F7F455373DFB8357EA5A0B9CB52686FA42063DC23D6DBE828D78DBC307063C57659098FA2DFFE948F1DDCD013CD7D4A10C79B010FA6EA94A41E0534B4CBFF96A38AEC9051FB0378CB92604691A58AD1A4C7647C2B274BBF7E782BFF20A3B2C8F5321B4D9D109E99D0FDC3C76A7B7BA1EA128D40C4C273
	C532F92C630C2A33A5727036F34F3D5E8B0A57C4D2074A4B695F8A7A77A87E2D20188A0A29B88A33391ECC7AAF3ABC9241B197C464C2307005F0C6AEBC7A98295EA117C88A45E6CD549BC467B1E9E2F57CF8C38A9DF3BB704FAE7CD3C82B95533FFB6E0E6703864FE91C339A3E988E62750827F748999A9EF2AA3D3A937F0FB2EC66B405AE8D3614F8FBA99612768712CFAFD15A54E1B5A0EA426A4D23588E153D4E46F3FC9759BE8E5790723AB535CDA8320B640EAB7E1DBA42717963700C832E7B148DBFAC01AD
	5C10B918E543EBDB1D14F701AD73730291DF4891D63CBA9AB1456D46C49F74849D7C7F0E63407FBD0ECE52F9BB55BA59AE0DA4BE57F008B79DD0D2E0AAD1823A39A8073DD106DE998A1419BE74F0F869D2F30402C0959ED43D0EA0B46A472BECC3D54C1669B6D425671E2B4A5B062A600E0BFB8B3ACBE9B1F9A6F1F8CE1D12678F0153538627F8F6A6767FBFC3CE1B8DCEB3B82778EF731F9F27AE03539493271B4619BCAC41B045AA31F693961C1E836D51AEC4AFAEE0C7486FD038B7616875C5441B1CC3374C6F
	FE126FBF93776D501A736C8C602573513FAD7C9C7D3DD5E99B7817ED62AE73D7F63C670BF71F5AB924E81A2EE8D76B3DEE3CDBED9851D5630D49931958FED05461052C6CCB19487795934D7F81D0CB8788591AB137339AGGA4CDGGD0CB818294G94G88G88G4AD499B1591AB137339AGGA4CDGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6D9AGGGG
**end of data**/
}
/**
 * Return the JButtonAdd property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonAdd() {
	if (ivjJButtonAdd == null) {
		try {
			ivjJButtonAdd = new javax.swing.JButton();
			ivjJButtonAdd.setName("JButtonAdd");
			ivjJButtonAdd.setMnemonic('a');
			ivjJButtonAdd.setText("Add");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonAdd;
}
/**
 * Return the JButtonRemove property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonRemove() {
	if (ivjJButtonRemove == null) {
		try {
			ivjJButtonRemove = new javax.swing.JButton();
			ivjJButtonRemove.setName("JButtonRemove");
			ivjJButtonRemove.setMnemonic('r');
			ivjJButtonRemove.setText("Remove");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonRemove;
}
/**
 * Return the JComboBoxLMProgram property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxLMProgram() {
	if (ivjJComboBoxLMProgram == null) {
		try {
			ivjJComboBoxLMProgram = new javax.swing.JComboBox();
			ivjJComboBoxLMProgram.setName("JComboBoxLMProgram");
			ivjJComboBoxLMProgram.setToolTipText("The program you want to add to this control area");
			// user code begin {1}

			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized( cache )
			{
				java.util.List l = cache.getAllLMPrograms();
				Vector unassignedPrgIDs = LMProgram.getUnassignedPrograms();

				// fills our JComboBox with LiteDevices!!
				for( int i = 0; i < l.size(); i++ )
				{
					LiteYukonPAObject lite = (LiteYukonPAObject)l.get(i);
					 
					if( unassignedPrgIDs.contains( new Integer(lite.getYukonID()) ) )
						ivjJComboBoxLMProgram.addItem( lite );
				}
			}
			
			if( ivjJComboBoxLMProgram.getItemCount() <= 0 )
				ivjJComboBoxLMProgram.addItem( "  (No Unassigned Programs Available)" );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxLMProgram;
}
/**
 * Return the JCSpinFieldUserOrder1 property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldPriority() {
	if (ivjJCSpinFieldPriority == null) {
		try {
			ivjJCSpinFieldPriority = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldPriority.setName("JCSpinFieldPriority");
			ivjJCSpinFieldPriority.setToolTipText("Search priority");
			// user code begin {1}

			ivjJCSpinFieldPriority.setDataProperties(
				new com.klg.jclass.field.DataProperties(
					new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(1), new Integer(Integer.MAX_VALUE), null, true, null, 
						new Integer(1), "###0.###;-###0.###", false/*allow_null*/,
						false, false, null, new Integer(1)/*default value*/), 
						new com.klg.jclass.util.value.MutableValueModel(
							java.lang.Integer.class, new Integer(0)), 
							new com.klg.jclass.field.JCInvalidInfo(true, 2, 
							new java.awt.Color(0, 0, 0, 255), 
							new java.awt.Color(255, 255, 255, 255))));

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldPriority;
}
/**
 * Return the JCSpinFieldUserOrder2 property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldStopOrder() {
	if (ivjJCSpinFieldStopOrder == null) {
		try {
			ivjJCSpinFieldStopOrder = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldStopOrder.setName("JCSpinFieldStopOrder");
			ivjJCSpinFieldStopOrder.setToolTipText("Order number used when stopping");
			// user code begin {1}

			ivjJCSpinFieldStopOrder.setDataProperties(
				new com.klg.jclass.field.DataProperties(
					new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(1), new Integer(Integer.MAX_VALUE), null, true, null, 
						new Integer(1), "###0.###;-###0.###", false/*allow_null*/,
						false, false, null, new Integer(1)/*default value*/), 
						new com.klg.jclass.util.value.MutableValueModel(
							java.lang.Integer.class, new Integer(0)), 
							new com.klg.jclass.field.JCInvalidInfo(true, 2, 
							new java.awt.Color(0, 0, 0, 255), 
							new java.awt.Color(255, 255, 255, 255))));

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldStopOrder;
}
/**
 * Return the JLabelPriority property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPriority() {
	if (ivjJLabelPriority == null) {
		try {
			ivjJLabelPriority = new javax.swing.JLabel();
			ivjJLabelPriority.setName("JLabelPriority");
			ivjJLabelPriority.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelPriority.setText("Assigned Start Priority:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPriority;
}
/**
 * Return the JLabelProgram property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelProgram() {
	if (ivjJLabelProgram == null) {
		try {
			ivjJLabelProgram = new javax.swing.JLabel();
			ivjJLabelProgram.setName("JLabelProgram");
			ivjJLabelProgram.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelProgram.setText("Program:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelProgram;
}
/**
 * Return the JLabelSearchInstructions property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSearchInstructions() {
	if (ivjJLabelSearchInstructions == null) {
		try {
			ivjJLabelSearchInstructions = new javax.swing.JLabel();
			ivjJLabelSearchInstructions.setName("JLabelSearchInstructions");
			ivjJLabelSearchInstructions.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJLabelSearchInstructions.setText("(Click the table and press Alt + S to search)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSearchInstructions;
}
/**
 * Return the JLabelSelectedPrograms property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSelectedPrograms() {
	if (ivjJLabelSelectedPrograms == null) {
		try {
			ivjJLabelSelectedPrograms = new javax.swing.JLabel();
			ivjJLabelSelectedPrograms.setName("JLabelSelectedPrograms");
			ivjJLabelSelectedPrograms.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJLabelSelectedPrograms.setText("Assigned Programs");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSelectedPrograms;
}
/**
 * Return the JLabelStopOrder property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStopOrder() {
	if (ivjJLabelStopOrder == null) {
		try {
			ivjJLabelStopOrder = new javax.swing.JLabel();
			ivjJLabelStopOrder.setName("JLabelStopOrder");
			ivjJLabelStopOrder.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelStopOrder.setText("Assigned Stop Priority:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStopOrder;
}
/**
 * Return the JPanelLMProgram property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelLMProgram() {
	if (ivjJPanelLMProgram == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Program Assignment");
			ivjJPanelLMProgram = new javax.swing.JPanel();
			ivjJPanelLMProgram.setName("JPanelLMProgram");
			ivjJPanelLMProgram.setBorder(ivjLocalBorder);
			ivjJPanelLMProgram.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJComboBoxLMProgram = new java.awt.GridBagConstraints();
			constraintsJComboBoxLMProgram.gridx = 2; constraintsJComboBoxLMProgram.gridy = 1;
			constraintsJComboBoxLMProgram.gridwidth = 3;
			constraintsJComboBoxLMProgram.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxLMProgram.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxLMProgram.weightx = 1.0;
			constraintsJComboBoxLMProgram.ipadx = 122;
			constraintsJComboBoxLMProgram.insets = new java.awt.Insets(5, 1, 3, 28);
			getJPanelLMProgram().add(getJComboBoxLMProgram(), constraintsJComboBoxLMProgram);

			java.awt.GridBagConstraints constraintsJLabelProgram = new java.awt.GridBagConstraints();
			constraintsJLabelProgram.gridx = 1; constraintsJLabelProgram.gridy = 1;
			constraintsJLabelProgram.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelProgram.ipadx = 10;
			constraintsJLabelProgram.insets = new java.awt.Insets(5, 17, 6, 1);
			getJPanelLMProgram().add(getJLabelProgram(), constraintsJLabelProgram);

			java.awt.GridBagConstraints constraintsJButtonAdd = new java.awt.GridBagConstraints();
			constraintsJButtonAdd.gridx = 4; constraintsJButtonAdd.gridy = 3;
			constraintsJButtonAdd.anchor = java.awt.GridBagConstraints.SOUTHEAST;
			constraintsJButtonAdd.ipadx = 11;
			constraintsJButtonAdd.insets = new java.awt.Insets(2, 20, 9, 26);
			getJPanelLMProgram().add(getJButtonAdd(), constraintsJButtonAdd);

			java.awt.GridBagConstraints constraintsJLabelStopOrder = new java.awt.GridBagConstraints();
			constraintsJLabelStopOrder.gridx = 1; constraintsJLabelStopOrder.gridy = 3;
			constraintsJLabelStopOrder.gridwidth = 2;
			constraintsJLabelStopOrder.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelStopOrder.ipadx = 3;
			constraintsJLabelStopOrder.insets = new java.awt.Insets(7, 17, 10, 0);
			getJPanelLMProgram().add(getJLabelStopOrder(), constraintsJLabelStopOrder);

			java.awt.GridBagConstraints constraintsJLabelPriority = new java.awt.GridBagConstraints();
			constraintsJLabelPriority.gridx = 1; constraintsJLabelPriority.gridy = 2;
			constraintsJLabelPriority.gridwidth = 2;
			constraintsJLabelPriority.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelPriority.ipadx = 2;
			constraintsJLabelPriority.insets = new java.awt.Insets(6, 17, 1, 0);
			getJPanelLMProgram().add(getJLabelPriority(), constraintsJLabelPriority);

			java.awt.GridBagConstraints constraintsJCSpinFieldPriority = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldPriority.gridx = 3; constraintsJCSpinFieldPriority.gridy = 2;
			constraintsJCSpinFieldPriority.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCSpinFieldPriority.ipadx = 54;
			constraintsJCSpinFieldPriority.ipady = 19;
			constraintsJCSpinFieldPriority.insets = new java.awt.Insets(4, 1, 2, 25);
			getJPanelLMProgram().add(getJCSpinFieldPriority(), constraintsJCSpinFieldPriority);

			java.awt.GridBagConstraints constraintsJCSpinFieldStopOrder = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldStopOrder.gridx = 3; constraintsJCSpinFieldStopOrder.gridy = 3;
			constraintsJCSpinFieldStopOrder.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCSpinFieldStopOrder.ipadx = 54;
			constraintsJCSpinFieldStopOrder.ipady = 19;
			constraintsJCSpinFieldStopOrder.insets = new java.awt.Insets(5, 1, 11, 25);
			getJPanelLMProgram().add(getJCSpinFieldStopOrder(), constraintsJCSpinFieldStopOrder);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelLMProgram;
}
/**
 * Return the JScrollPaneJTable property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneJTable() {
	if (ivjJScrollPaneJTable == null) {
		try {
			ivjJScrollPaneJTable = new javax.swing.JScrollPane();
			ivjJScrollPaneJTable.setName("JScrollPaneJTable");
			ivjJScrollPaneJTable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneJTable.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			getJScrollPaneJTable().setViewportView(getJTableProgram());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneJTable;
}
/**
 * Insert the method's description here.
 * Creation date: (3/21/2001 5:55:39 PM)
 * @return com.cannontech.dbeditor.wizard.device.lmcontrolarea.ControlAreaProgramTableModel
 */
private ControlAreaProgramTableModel getJTableModel() 
{
	return (ControlAreaProgramTableModel)getJTableProgram().getModel();
}
/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableProgram() {
	if (ivjJTableProgram == null) {
		try {
			ivjJTableProgram = new javax.swing.JTable();
			ivjJTableProgram.setName("JTableProgram");
			getJScrollPaneJTable().setColumnHeaderView(ivjJTableProgram.getTableHeader());
			getJScrollPaneJTable().getViewport().setBackingStoreEnabled(true);
			ivjJTableProgram.setToolTipText("Press Alt + S to bring up a search box");
			ivjJTableProgram.setBounds(0, 0, 200, 200);
			// user code begin {1}

			ivjJTableProgram.getSelectionModel().setSelectionMode( javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
			ivjJTableProgram.setModel( new ControlAreaProgramTableModel() );

			//create our editor for the Integer fields
			javax.swing.JTextField field = new javax.swing.JTextField();
			field.addKeyListener(new java.awt.event.KeyAdapter() 
			{
				public void keyTyped(java.awt.event.KeyEvent e) 
				{
					fireInputUpdate();
				};
			});
			
			field.setHorizontalAlignment( javax.swing.JTextField.CENTER );
			field.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(1, 99999) );
			javax.swing.DefaultCellEditor ed = new javax.swing.DefaultCellEditor(field);
			ed.setClickCountToStart(1);

			//create our renderer for the Integer fields
			javax.swing.table.DefaultTableCellRenderer rend =
						new javax.swing.table.DefaultTableCellRenderer();
			rend.setHorizontalAlignment( field.getHorizontalAlignment() );

			
			ivjJTableProgram.setDefaultEditor( Integer.class, ed );
			ivjJTableProgram.setDefaultRenderer( Integer.class, rend );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableProgram;
}
/**
 * Insert the method's description here.
 * Creation date: (1/11/2002 4:44:33 PM)
 * @return java.lang.Integer
 */

//as of 2.41 we do not use incrementing priorities; user changes priorities manually
/*private Integer getNextStartOrder() 
{
	int j = 1;
	for( int i = 0; i < getJTableModel().getRowCount(); i++ )
	{

		if( j == getJTableModel().getRowAt(i).getProgramList().getDefaultPriority().intValue() )
		{
			j++;
			i = -1; //we must start i to -1 because we will increment to zero
						// in the loop above
			continue;
		}

	}
	
	return new Integer(j);
}*/
/**
 * Insert the method's description here.
 * Creation date: (1/11/2002 4:44:33 PM)
 * @return java.lang.Integer
 */
private Integer getNextStopOrder() 
{
	int j = 1;
	for( int i = 0; i < getJTableModel().getRowCount(); i++ )
	{

		if( j == getJTableModel().getRowAt(i).getProgramList().getStopPriority().intValue() )
		{
			j++;
			i = -1; //we must start i to -1 because we will increment to zero
						// in the loop above
			continue;
		}

	}
	
	return new Integer(j);
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return null;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o)
{
	if( getJTableProgram().isEditing() )
		getJTableProgram().getDefaultEditor(Integer.class).stopCellEditing();

	com.cannontech.database.data.device.lm.LMControlArea controlArea = (com.cannontech.database.data.device.lm.LMControlArea) o;

	controlArea.getLmControlAreaProgramVector().removeAllElements();

	for (int i = 0; i < getJTableModel().getRowCount(); i++)
	{
		ControlAreaProgramTableModel.ProgramRow row = getJTableModel().getRowAt(i);

		row.getProgramList().setDeviceID(controlArea.getPAObjectID());

		controlArea.getLmControlAreaProgramVector().add(row.getProgramList());
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
	
	dialog = new OkCancelDialog(
		CtiUtilities.getParentFrame(this),
		"Search",
		true, FND_PANEL );
	
	final AbstractAction searchAction = new AbstractAction()
	{
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
			if( !dialog.isShowing() )
			{
				dialog.setSize(250, 120);
				dialog.setLocationRelativeTo( LMControlAreaProgramPanel.this );
				dialog.show();
		
				if( dialog.getButtonPressed() == OkCancelDialog.OK_PRESSED )
				{
					Object value = FND_PANEL.getValue(null);
					boolean found = false;
							
					if( value != null )
					{
						int numberOfRows = getJTableModel().getRowCount();
						for(int j = 0; j < numberOfRows; j++)
						{
							String programName = ((String)getJTableModel().getValueAt(j, 0));
							if(programName.compareTo(value.toString()) == 0)
							{
								getJTableProgram().setRowSelectionInterval(j, j);
								getJTableProgram().scrollRectToVisible( new java.awt.Rectangle(
								0,
								getJTableProgram().getRowHeight() * (j+1) - getJTableProgram().getRowHeight(),  //just an estimate that works!!
								100,
								100) );	
								found = true;
								break;
							}
							//in case they don't know the full name and just entered a partial
							if(programName.indexOf(value.toString()) > -1 && programName.indexOf(value.toString()) < 2)
							{
								getJTableProgram().setRowSelectionInterval(j, j);
								getJTableProgram().scrollRectToVisible( new java.awt.Rectangle(
								0,
								getJTableProgram().getRowHeight() * (j+1) - getJTableProgram().getRowHeight(),  //just an estimate that works!!
								100,
								100) );	
								found = true;
								break;
							}
						}
							
						if( !found )
							javax.swing.JOptionPane.showMessageDialog(
								LMControlAreaProgramPanel.this, "Unable to find your selected item", "Item Not Found",
								javax.swing.JOptionPane.INFORMATION_MESSAGE );
					}
				}
		
				dialog.setVisible(false);
			}
		}
	};

	//do the secret magic key combo: ALT + S
	KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK, true);
	getJTableProgram().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(stroke, "FindAction");
	getJTableProgram().getActionMap().put("FindAction", searchAction);
	
	// user code end
	getJButtonAdd().addActionListener(ivjEventHandler);
	getJButtonRemove().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMControlAreaProgramPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(389, 359);

		java.awt.GridBagConstraints constraintsJScrollPaneJTable = new java.awt.GridBagConstraints();
		constraintsJScrollPaneJTable.gridx = 1; constraintsJScrollPaneJTable.gridy = 3;
		constraintsJScrollPaneJTable.gridwidth = 2;
		constraintsJScrollPaneJTable.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneJTable.weightx = 1.0;
		constraintsJScrollPaneJTable.weighty = 1.0;
		constraintsJScrollPaneJTable.ipadx = 341;
		constraintsJScrollPaneJTable.ipady = 158;
		constraintsJScrollPaneJTable.insets = new java.awt.Insets(2, 10, 0, 16);
		add(getJScrollPaneJTable(), constraintsJScrollPaneJTable);

		java.awt.GridBagConstraints constraintsJButtonRemove = new java.awt.GridBagConstraints();
		constraintsJButtonRemove.gridx = 2; constraintsJButtonRemove.gridy = 2;
		constraintsJButtonRemove.ipadx = 4;
		constraintsJButtonRemove.insets = new java.awt.Insets(8, 64, 1, 16);
		add(getJButtonRemove(), constraintsJButtonRemove);

		java.awt.GridBagConstraints constraintsJPanelLMProgram = new java.awt.GridBagConstraints();
		constraintsJPanelLMProgram.gridx = 1; constraintsJPanelLMProgram.gridy = 1;
		constraintsJPanelLMProgram.gridwidth = 2;
		constraintsJPanelLMProgram.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelLMProgram.weightx = 1.0;
		constraintsJPanelLMProgram.weighty = 1.0;
		constraintsJPanelLMProgram.ipady = 3;
		constraintsJPanelLMProgram.insets = new java.awt.Insets(15, 10, 8, 16);
		add(getJPanelLMProgram(), constraintsJPanelLMProgram);

		java.awt.GridBagConstraints constraintsJLabelSelectedPrograms = new java.awt.GridBagConstraints();
		constraintsJLabelSelectedPrograms.gridx = 1; constraintsJLabelSelectedPrograms.gridy = 2;
		constraintsJLabelSelectedPrograms.ipadx = 15;
		constraintsJLabelSelectedPrograms.ipady = 8;
		constraintsJLabelSelectedPrograms.insets = new java.awt.Insets(8, 10, 2, 63);
		add(getJLabelSelectedPrograms(), constraintsJLabelSelectedPrograms);

		java.awt.GridBagConstraints constraintsJLabelSearchInstructions = new java.awt.GridBagConstraints();
		constraintsJLabelSearchInstructions.gridx = 1; constraintsJLabelSearchInstructions.gridy = 4;
		constraintsJLabelSearchInstructions.gridwidth = 2;
		constraintsJLabelSearchInstructions.ipadx = 108;
		constraintsJLabelSearchInstructions.ipady = 3;
		constraintsJLabelSearchInstructions.insets = new java.awt.Insets(1, 10, 5, 59);
		add(getJLabelSearchInstructions(), constraintsJLabelSearchInstructions);
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
 * Comment
 */
public void jButtonAdd_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJComboBoxLMProgram().getSelectedItem() == null
	    || !(getJComboBoxLMProgram().getSelectedItem() instanceof LiteBase) )
	{
		return;
	}

	if( getJTableProgram().isEditing() )
		getJTableProgram().getDefaultEditor(Integer.class).stopCellEditing();

	com.cannontech.database.db.device.lm.LMControlAreaProgram programList = new com.cannontech.database.db.device.lm.LMControlAreaProgram();
	programList.setStartPriority( new Integer( ((Number)getJCSpinFieldPriority().getValue()).intValue() ) );
	programList.setStopPriority( new Integer( ((Number)getJCSpinFieldStopOrder().getValue()).intValue() ) );

	// this is set to the LMPrograms deviceID
	programList.setLmProgramDeviceID( new Integer( ((com.cannontech.database.data.lite.LiteYukonPAObject)getJComboBoxLMProgram().getSelectedItem()).getYukonID() ) );
	
 	/*if( getJTableModel().addRow( programList, (com.cannontech.database.data.lite.LiteYukonPAObject)getJComboBoxLMProgram().getSelectedItem() ) )
 	{
	 	//as of 2.41 we no longer increment priorities
	 	//user manually changes them; otherwise, they remain at the same value
	 	getJCSpinFieldStopOrder().setValue( getNextStopOrder() );
	 	getJCSpinFieldPriority().setValue( getNextStartOrder() );
	}
 	else*/
	if( ! getJTableModel().addRow( programList, (com.cannontech.database.data.lite.LiteYukonPAObject)getJComboBoxLMProgram().getSelectedItem() ) )
 		javax.swing.JOptionPane.showMessageDialog( this, "That Program is already in the list.", "Duplicate Program", javax.swing.JOptionPane.INFORMATION_MESSAGE );
 	
	//autoscroll to show new additions
	getJTableProgram().scrollRectToVisible( new java.awt.Rectangle(
		0,
		getJTableProgram().getRowHeight() * (getJTableProgram().getRowCount() - 4 )- getJTableProgram().getRowHeight(),  //just an estimate that works!!
		100,
		100) );	
 	
	//remove it from the combo box to avoid confusion
	getJComboBoxLMProgram().removeItem(getJComboBoxLMProgram().getSelectedItem());	
 	
 	fireInputUpdate();
 	
 	return;
}
/**
 * Comment
 */
public void jButtonRemove_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJTableProgram().isEditing() )
		getJTableProgram().getDefaultEditor(Integer.class).stopCellEditing();
	
	if( getJTableProgram().getSelectedRow() >= 0 )
	{
		LiteYukonPAObject[] lite = new LiteYukonPAObject[getJTableProgram().getSelectedRows().length];
		int[] selRows = getJTableProgram().getSelectedRows();
		
		for( int i = (selRows.length-1); i >= 0; i-- )
		{
			lite[i] = getJTableModel().removeRow( selRows[i] );
	
			boolean alreadyFound = false;
			for( int j = 0; j < getJComboBoxLMProgram().getItemCount(); j++ )
			{
				if( getJComboBoxLMProgram().getItemAt(j).equals(lite[i]) )
				{
					alreadyFound = true;
					break;
				}				
			}
				
			if( !alreadyFound )
				getJComboBoxLMProgram().addItem( lite[i] );
		}		
	}

	getJTableProgram().clearSelection();
	fireInputUpdate();
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		LMControlAreaProgramPanel aLMControlAreaProgramPanel;
		aLMControlAreaProgramPanel = new LMControlAreaProgramPanel();
		frame.setContentPane(aLMControlAreaProgramPanel);
		frame.setSize(aLMControlAreaProgramPanel.getSize());
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
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * setValue method comment.
 */
public void setValue(Object o) 
{
	if( o == null )
		return;

	com.cannontech.database.data.device.lm.LMControlArea controlArea = (com.cannontech.database.data.device.lm.LMControlArea)o;

	for( int i = 0; i < controlArea.getLmControlAreaProgramVector().size(); i++ )
	{
		com.cannontech.database.db.device.lm.LMControlAreaProgram programList = (com.cannontech.database.db.device.lm.LMControlAreaProgram)controlArea.getLmControlAreaProgramVector().elementAt(i);
		com.cannontech.database.data.lite.LiteYukonPAObject liteDevice = null;

		//find the LMProgram that this list item points at
		liteDevice = PAOFuncs.getLiteYukonPAO( programList.getLmProgramDeviceID().intValue() );
		
		if( liteDevice == null )
			throw new RuntimeException("Unable to find the LMProgram with deviceID " 
				+ programList.getLmProgramDeviceID().intValue() + 
				" for the LMControlAreaList in LMControlArea '" 
				+ controlArea.getPAOName() + "'" );
			

		getJTableModel().addRow( programList, liteDevice );
	}


	//getJCSpinFieldStopOrder().setValue( new Integer(getJTableModel().getRowCount()+1) );
}
}
