package com.cannontech.dbeditor.wizard.device.lmscenario;

import com.cannontech.database.cache.functions.DBPersistentFuncs;
import com.cannontech.database.data.device.lm.LMScenario;
import com.cannontech.database.data.device.lm.LMProgramDirect;
import com.cannontech.database.data.lite.LiteLMProgScenario;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.lm.LMControlScenarioProgram;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import java.util.Vector;
import java.util.HashMap;
import javax.swing.JComboBox;

/**
 * Insert the type's description here.
 * Creation date: (3/31/2004 12:15:45 PM)
 * @author: 
 */
public class LMScenarioProgramSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel {
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JScrollPane ivjProgramsScrollPane = null;
	private javax.swing.JTable ivjProgramsTable = null;
	private LMControlScenarioProgramTableModel tableModel = null;
	
	/*This hashmap will simplify referencing program information each time; we
	 won't have to hit the cache or the database to get gears, etc.  This will 
	 be quicker since once the hashmap is populated, the program id and program
	 name can be used as keys to instantly provide the correct direct program*/
	private HashMap allTheKingsPrograms;
	
	

class IvjEventHandler implements java.awt.event.MouseListener {
		public void mouseClicked(java.awt.event.MouseEvent e) {};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() == LMScenarioProgramSettingsPanel.this.getProgramsTable()) 
				connEtoC1(e);
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
	};
/**
 * LMScenarioProgramSettingsPanel constructor comment.
 */
public LMScenarioProgramSettingsPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (ProgramsTable.mouse.mousePressed(java.awt.event.MouseEvent) --> LMScenarioProgramSettingsPanel.programsTable_MousePressed(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.programsTable_MousePressed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (DefaultGearJComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> LMScenarioProgramSettingsPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
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
 * connEtoC3:  (StartDelayJTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMScenarioProgramSettingsPanel.fireInputUpdate()V)
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
 * connEtoC4:  (DurationJTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMScenarioProgramSettingsPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
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
	D0CB838494G88G88GF6F015B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8BD4D4C716E62286A3D1CCB0FE928DA2469FE6750C593893F78F63BAFBB4F1A7A60344ECBCB3E663A66E044C32F318336EEECED2F4C344BFAAAABAFE08DFC0EC1A0FF65BC18579A889EA0F9042A4E665517D00566EFEBD2F9F5298B759FB2BDE3DFEB45DA8B99B4E393CD7372AEE5D3AF53F751A649E0A4B4AB064921211C554BFCB86A17B4B88B99AA31C65386CA1A9C6227B7B84E0BB7936A0931E
	F95057B8AF4518C3BA77E6C33B985A4A1C94639EF85FC95CFBABCF41CB04F5F221EF656CAF6F8C3C4E49C23E4EFA5A6E6D5D8C4F2DGE640B583448D41754774662978G34372A6F889986C21407F04C313DF9BA7C9AF55CE5683B811077939C13583BDF6DDF88B89C138C4F358F31318446EC205C8D33D955F1B7464CA069AF9DB4EC4135F474B9347D044D9D2ABE734933D490C6B2427285BCAB6D37BB4FBBA40F49A1F83D12D7919DDDA6E73B68F4A912EC6AF15D95E42749A95EF1B9C4135B63F708DEC1F6C9
	B6DB6315A6755DAECB1D3260E9929545656D745B852F68863A69F4BF1F53F3C141AE04761007917777FBD106D6F83783F4BD4C77746A16CA1567585BB3486A295BA36EE97143ECAFEB9F8E6EE97330C732468FC57AAB6899613C82E83781B0DE8E5C653C2C9A4AF9F9E7B70E4D9D16E2AC86289D4671F376A07E8634950068E11C47F3A7B80F2D5F3EC05635D79B36C560B1E19863ED5930A00FF38B5A8DFB299E3DD0043A10BD9C56852882F0828C85C8G78A355DF3930881E556DCA0D6471C85EEA1F6F225BA9
	DF111C229B5EEDB668D1F157C507A4BB89E1BAEA5FDB9C069F8E8B077755CF843637AF895729446B4F91C5319832426CED7E0AFC43F215461A61413D65BE02F2336CC339E542FB06AAEB062F2678D69D1E1D55D22A8B586E023EE50FF0B937EE653CB4E646131C529A437AB03C0CDFE4B51823D4BB0A8A7242E4BC2698E52CGBE86E086C09240EAGB2024BF8E1E1797DE45CGB6A1FB85F7D3F73BCF10150B5DAE37D314D5D93F59BB102C43811B772F031E4764B223DCFFC62BFEFAB12CDCD64DB8E158BF0249
	23E2C4D0AEEC6D43F7A32D5DBC223FEEEC554E70ABEA93DDA3C24F56C971019121E71B3A1B1FED9474258230711F64A15E7EE8A81D13945F76E8A81D1F6E61F4FC50178C404E3744043E52BA927CB0009BE0A8C082C092C072C8FE466B0BCA9E500E6C3A73E5F3130AF643D38CA832506075AB0257A1360053935A5D221F28E3169621DC5B7B4C7BA535EF872CF1C9740A322038A4AFD097BCA2A8131F0DF93DE8BB0E11C55A5B6C7208300047C7B85D5F946D0427DB70ABDF781C02A286C7307EF70BD0A76E88AD
	C09188407B5168E0ACCC077D67C4F3D95DA8C1DC973407C4F339341F5C814F4FC139C5374DD66BF2A09B025C4BF5745CBE0CFD31515CA6FF574AF52FF64B9CD23C22BA2C6EA1D4BE4674287931D05D3BC4E95EFB0C4753D776368E528EB8A0DF8FE6EBEBFF709A46879843647B1299F574E958578200150FF199CFBA194FF46F2260973936DD13C1D1C4E7D062EC6C44E20CA5629D512B540ABE516B04973FFA56D30AB7847530C352F4E4BA4523DF2A81C571EB78B9455B999EE868351A77BFCF7B85C511DD6D5D
	0A480337567F371497FF9F1767D7773A2C94771A86FB4C9E85F94BA83E577AD3BBF57A54A8C9373BFD4DE8A92ADE1E2571379846570E62675F36877521CBEB5F226DE07F6FF78F666C104E60FD7126BD03D963C1F4227F9AA772F0DF99DA3B1436E335FD7E8D65A11CAD7DFFGDB63B53A46E079FFD0E0BAF05694D9072BCC61F5385E94DE079BCD836BE833A91C0E323E2FCD183BC8BDDE1D4E3719505FB9A42FD7240627CDA1AC9F7EDDB36E4365F5A9DF3A1C1D2252643AABC20EB38AF577BB6AD397427B72D1
	5C9F769C617E70685D19A469667E30795278E91F9A8AC6B33D2C9C9D74070CA72184EDC9F40B9E90D40D546DD5983E137AEB956FB7D21CB577A91CDB20A65CFEEADB459E295BAF1A68FF3BAC7A7D22336016F0C7B089BD0A091E03698A765561EBE17E29D3677387CE5D0BFDFA72B72F6889B7C6247CE9E1CB21D9758F9F15515C99766A83089A4D7D435BA5584F7D036A84EBA4371B9D8774250EC6D98776656A6497B71A55D9C19DDBEE4E41F3977F52ADC2B8C6AEBF97FBF9CCB56F8869EB915C5D41D87D9B73
	A6F83AF53E0961DFAB8DFF8EABCB51CEEFEB74D715FECCB066B21ACCEF668AC853A73BBC604A51592A751E7D2E3E5E2B08C9B1E2ED2840B38A004D1DD8F0901E77AB26EE0CC1FA6DF93CB60C03798BGD629B47346B0794C206346EAB5D70906CB2438FF83C7B0A9136712D7F274B52453DF91D6FF91D2D57A6471509C3B22826966835E7CB8BF6716B2FD9C28767BA5070B26B82A2C334A753A4DF09B4BB7423343A57BA15FF60A81FEDEB96548AB66C0AADA15796C1367C87F1C2E0ED62DBC4C5674D055EC16EA
	A08D3B8CC7DE2319FE0BCB6CA1DC8E7FDB2F2FBB97EEABB4EC0AA007DAF57FCE1D9C94286998CF93ECD8CB042B279D14C9D3E7374B542D38B059929470013EEE059DB02BCFDF5808F24135939E67F5E8D5A56A65AAE82783ECFEA24548F26211B4D746F50F826E86G53398BF63D4E4582FE8140AA00B4G66C7FE1F0757A203729B1A5CEF9B2DD457FF180BE0720E5FD92572376C39F8D2797D4630B5F45436DCC3F6AC1333B9B6A86F4036BF992281541A35CC2F43C92B8BA26A70BD15C6CCEC288E671DC139A7
	82BEA9166B7048726DBA9D067A9AD27FCE7096DA1C2BB0B23F20364FD5E4F13FC0F8CE7805857DD8B250CC83489E1BE24C87289A4B57D8DCF15F5A33AEB0C06D39F0A32F635A4632BDA9E30379CF943CBF8DB0D707E3F556FC2AD30B470656DFFFC77149E3C36B2F7ACADEFF2D013E4BCF721CE5CCDEE89D1978E45F7CEBC9C81B4DFB265F3C03217EA6E49E533DDB5F66F17BC2A58B187CBD60A3ED364F289AB27D7FC325DE7FB34731DC530C4FF17C9E28474E6D6C46B8EEE76534D60D05F6A24062F13CEE1AED
	C51F3B8A5A6BGF24627980B816CGEE0018717CACF71E8E670F7A1EE5A4E7944C4D591A6F1DC57E66826DA40D3E6F74961DBE5AA517D751F47140990CC9126C6AF4F9B96EB045B9A419A6C08C770DA5F2EE6634044F4DEED94267E6BE4B4075C54092A9F7E335402F282FDB8E7BCB85E03884EA07E4C20AB1F782479D33A24E8AED3B062B2772D1209D2D61C650F1F150DEB0016B674FAAB1F6AF535A0BE99B7DA1EBB7D67E503A70FE90DCD37C5156C0FBBCC01FCBF7EF23E7817E57CF158C230B5A772E320B04
	49F92F8A9ED1253113AC495B200E29D3241AD24D5EBA6B8E043737A6078CD99B06AE426FF9CF1DC379230E26CE60B67568B5FA4FB3B1455885B0E4A21749BDBB5A7FF8E8A7CE6CDF2FA581AED9452FD51F4CBFCD27B67A41C4661FB2B47CDC0A4FFEAA08E7FE6B6CEEFE8F1C8BFD012738ADBDEF0DEC878B2D616DE011957766173AE507982A6BCB2DF9DADF502A9B945103E2D673CBBADF91E428ABC2673FC167333EC8737F59BAF0AD14F6967799505A69345D2B1D71EFB7627ABED96C90E5483AD9E9C3F21FC6
	B999B761592D82792CFD0A6788AFD27F6196FA25EEF0B409F41C5922772D2D009B82907BB4BBA326B373FA502FF6C0F2G6CB579C487091A04F3AB2DFCEEBA0C4F86DC8140E5002EC9FC4DEF4E62DC186872F4FBA8070C3FEEEA6F4BE15CAAEDEC1C150E9582A1E397D0BF10BD99FC48E4BE76F6B356843CEA52ABAF534732F35CC62BE0CE7364873BC36802F177C24646C0BBA18447F474C3EA8B8BA607465CECBB6217CC8E0D39B157F84CDD89FD154FF0BF77840DDDF14F30718CB70F629682EE25B6EEDCA977
	1B299AEED19962F262C076F19CB7A493F115506E0A6376B9872FBCD5597C356E7D42206EE2868BEC0D569FF50DFBF1780D6F469122594504B37970740ABD79DA5EDA6B023A84AFD48BAA4A4C457965E74C05AC570C6B51671A560D260839E6D29C5301E475198ED6496C23348D73894D276202B65BA559653CA8F4B6D2EBE37E3445122D525EBC057B5321965409DAE85FGD02670FB0840F1DE3FA4BE9BCF9A07DC8C7B5DECF5F321A1FA8A63E76E14E08E3C986A9736E63B152F198A7DC763299EDA7535B84A6C
	D583A1A654BD5A3F28CFBF7AA82C95027D69FD7A115F86233E7F78D9FDFF16EEBE5B5B4B47785EBEF8F9AEF1BFEF0E78CD509D2F66776AB36A15FDE475AF7EE4E0E7FE6FD8704C3F33BE481933F94F9C8F4EBBD279A07318DDB553F83884F8C90D6736B6076248D4303F299C377EBC35BFE837EA38194508F3C37B1E06DBEE61769C236112AFA0EEB53457CD65361BE9479CE7F382EFEF25ED3CABE06DA72F8D563E105660EC782F863D46E06EA459D94C32633B502DC857C5BF38FF4C3435FC469EB9563DC5FDE5
	816C298200452667CAF9ECEA831C82908BB09FA0B9014710B74A42471BFF27BEB59D4611E9FC6C384CC3FD56278107DEC539449E1FA4AB3A7973297FB5435C4AE95C1EDF79DA6FE7F2A66699F8DE53CF6727313389CC6B776D73842DE9813FE49A4FFB269CB9894F9661D67F5B92C0C2FC740932524B307456CCBD4F2FAD037DD649E25BBA6A2F56CE8B4D57B22C08CF1F961A2FBDEA61799AF9B64568FC1657E00B8A70FC16C0FB8D4087G3953617CG6C53795EFEFB44CC6ED3FFB5CB3EC6C87A5C3AF2056D2F
	537A433E457EFCBD77CD73FFB60BAC981E1691695E48B9DD7D8D067A4C7F65BE0AD76F896837427950379CA09DE0730C94E3BE00F5865FDF5951E236BFBC1EEAC5919CDD74760CEF8ED5AB084644CB4DFD4E61EFE16E40413A9C021B2962C5C136CBEE1723D755416154AF215FED1C41F5F871B9642D8B0A4A3B12D769BF135B53EABA7782C034B67760794876E7217EEE990CDD290DCFAAC69F55816B30B1A9547FE54E04F7G851F64DDCB6DFDFC029E2AE0CEB340AE355D856F9360E07EC5739F6FDD881FEB9A
	2BE8AE8563174C647C453761FE7A5C3A931ECB7CB71A14CAB33AB41FC40C8F65565345274DE6F3A10B3549B4BF43F817BE13475A4DE52131F67B89FD2CBCDF969AEBD157027D3E3250D8EB6C93EB634BC2E3AD1F4F76F270D4F0AFCA66518176D2E6B9E301E8078FB5CE8EB785676E5A7DC96439E5E60813D69404267F65F8CD2D56ADEF9F47C09D56C70116FF86742E355F028229D9685466BCF9024682DE9BB1572576CDBA01FB46EFBAAE416D3ACB2F211BE0203713597E0796DC1B3D2F37865FE3ACBFFE2EBB
	73C7DD036D7F650248356EEB8561EB5D1585616FFCFEDDB0F08D7ACEC1646FF16F97683F4731F32FB26359944F02BA90408DB084A0F69677F95F969EA654670571F7F54ECE2C357197998ADF7FFF0AA26FD5FC005FF1F80B709BC43B24A8128769F33CC2FF5F21C8BE0AE438DE7A5B8B595559253021AA7E7BA21A3B099D944D6CE152398AF86A2ED0CC3A9B94D3CB5D7566061A6A461BCDB5572FB5B6DE2C3EFE333A6966553A3A5A3ADAB67FBF4E158DB07F72356B8DDFDD3B5A5C074255BAA04B6D7AD725C15B
	1CDC1CFF9F5BAC35725C7755B341F90BAD07C367550343AEB86C843B12BA8F976763548AB06AC3F4BC5460047F25DD8807E3EB1C25B6476269DB1BF9BC5DBFFBAA29FDBDAD838B35F071F46EAC75FB546CE05ED39175C89623B975EC10EF4F358759AF3347B7CA75F90095E8B74E9EB877E5E7FE599CD96F2E196F2FF72DE67ABD8C74253957275E77FCE326F73A9D9DFED1536FF67A5D13619A7CF6496FC2EF2666FBB60C0FED406FBD00B80085G4BG56G2C83581C88B9850095408E608488GC483BC8DB097
	E0F1A2373D6F2C681FA3650AEA41CA8DD09658DDB42BC952E8DCDB89F4769AB96E8F941786B8C7865F4BB4DBE419A55A6EAF3367ED61ED75C5DBFFDBCD32053755FF3069EDD57517B674D31D303FD6ED5CF28D77E78D370252C45C17AE275225615F24FCA17E32088BF27CDB142F9E0AEB657ED2077B330AFB4F06678EDB0654CB3B5B33517C4865F62BF7FBF40C4B8BEA204963BFA90FAE1F20637BC38D07FC337B876178E62245C34ABA436F8FEC8962B96B047D0E7A52711760AC935FA7D9FE196C68EFC75523
	5EF8BD71E6ED2E01989A4847DDF4F283496AC16A819FD8B008A7A3417F97729D5A65C02D1DD150FA08373E7F8801378ED5413FA53CC5665316CC367471C73F18F27A1DE53FEE719B49478DE4AF2614CD22CCA33F6844DBF17527F275E4038FBF21E06257C0B27D741165D7FA5DE2A3496D380EBF9F70AB22331A47BA3FC2F651AF1BA5787D366432606FD2D09FBFEDA419DE7CD19F59413A5D02373304A5981F625EC9C6F59B65173AAB60F7A765972918B4AABBDF5CF66C134F56ACB092C3BD59681684E73D60G
	F3EBA09B14AED974F7C9EEE740F7210A924A6E61DF2F0CC06E0F5A76F13326E6309A0D695B01C951DB56EE29B7C001A81392BAD20181A6ED043326CB10E1A9D1EED070C70A12FCD46BFB6820F27C9FAF6D39E1A0996AA6D1749B83BE853812852F3FC312BD40C1FE88873CCBA3E4FAE229F96C4C7F7A0A928ACED548A1A6B218DA52A9CA1E126A0E8E17D75488007C007C810DBC0EB1050C599198336B33959F3543F957922347EC267FCB697FB27CDFCAB12594D3DA86C2CCA3837E0576872842EDD51812GBF5B
	A8BFF84C26FAB8A9651F5EDF9A7FE6E5DAAA6C2E1EE4518F40A06C1C60282B5D1EF6D19E1EB87640265855A371ACFC28921C6E479D0247656E0517DA9AC98308BF9850D6421DB7D0D3DFFD275CE6FCFB2A51C0B2414C186B8365C57F854AABB32BB3026B23FA0769FB368A877BDA8D0F37055F6B6F99568D5B9671BEB3E69E0B038973C26F9964C2FA8785788F6671583128G8B09C8B9FC13C7F03B21223CDD6DF5E2BEFF31D79103B9E4CFA1527773B1EA279A1368BD6CAD5D1C7F83D0CB8788121A654BD393G
	GC4B2GGD0CB818294G94G88G88GF6F015B0121A654BD393GGC4B2GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0D93GGGG
**end of data**/
}

/**
 * Return the ProgramsScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getProgramsScrollPane() {
	if (ivjProgramsScrollPane == null) {
		try {
			ivjProgramsScrollPane = new javax.swing.JScrollPane();
			ivjProgramsScrollPane.setName("ProgramsScrollPane");
			ivjProgramsScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjProgramsScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			ivjProgramsScrollPane.setPreferredSize(new java.awt.Dimension(200, 180));
			ivjProgramsScrollPane.setFont(new java.awt.Font("dialog", 0, 14));
			ivjProgramsScrollPane.setMinimumSize(new java.awt.Dimension(200, 180));
			getProgramsScrollPane().setViewportView(getProgramsTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjProgramsScrollPane;
}
/**
 * Return the ProgramsTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getProgramsTable() 
{
	if (ivjProgramsTable == null) 
	{
		try 
		{
			ivjProgramsTable = new javax.swing.JTable();
			ivjProgramsTable.setName("ProgramsTable");
			getProgramsScrollPane().setColumnHeaderView(ivjProgramsTable.getTableHeader());
			
			// user code begin {1}
			ivjProgramsTable.setAutoCreateColumnsFromModel(true);
			ivjProgramsTable.setModel( getTableModel() );
			ivjProgramsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjProgramsTable.setPreferredSize(new java.awt.Dimension(385,5000));
			ivjProgramsTable.setBounds(0, 0, 385, 5000);
			ivjProgramsTable.setMaximumSize(new java.awt.Dimension(32767, 32767));
			ivjProgramsTable.setPreferredScrollableViewportSize(new java.awt.Dimension(200, 8000));
			ivjProgramsTable.setGridColor( java.awt.Color.black );
			ivjProgramsTable.getSelectionModel().setSelectionMode( javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
			ivjProgramsTable.setRowHeight(20);
			
			//Do any column specific initialization here
			javax.swing.table.TableColumn nameColumn = getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.PROGRAMNAME_COLUMN);
			javax.swing.table.TableColumn startDelayColumn = getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.STARTDELAY_COLUMN);
			javax.swing.table.TableColumn stopOffsetColumn = getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.STOPOFFSET_COLUMN);
			javax.swing.table.TableColumn startGearColumn = getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.STARTGEAR_COLUMN);
			nameColumn.setPreferredWidth(100);
			startDelayColumn.setPreferredWidth(60);
			stopOffsetColumn.setPreferredWidth(60);
			startGearColumn.setPreferredWidth(100);
	
			// Create and add the column renderers	
			com.cannontech.common.gui.util.ComboBoxTableRenderer comboBxRender = new com.cannontech.common.gui.util.ComboBoxTableRenderer();
			startGearColumn.setCellRenderer(comboBxRender);
			//create the editor for the gear field
			javax.swing.JComboBox combo = new javax.swing.JComboBox();
			combo.setBackground(getProgramsTable().getBackground());
		
			combo.addActionListener( new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e) 
				{
					fireInputUpdate();
				}
			});

			startGearColumn.setCellEditor( new javax.swing.DefaultCellEditor(combo) );
	
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
			startDelayColumn.setCellEditor( ed );
			stopOffsetColumn.setCellEditor( ed );
	
			//create our renderer for the Integer fields
			javax.swing.table.DefaultTableCellRenderer rend = new javax.swing.table.DefaultTableCellRenderer();
			rend.setHorizontalAlignment( field.getHorizontalAlignment() );
			startDelayColumn.setCellRenderer(rend);
			stopOffsetColumn.setCellRenderer(rend);
				
		}	
			
		// user code end
		catch (java.lang.Throwable ivjExc) 
		{
		// user code begin {2}
		// user code end
			handleException(ivjExc);
		}	
	}
	return ivjProgramsTable;
}

private LMControlScenarioProgramTableModel getTableModel() 
{
	if( tableModel == null )
		tableModel = new LMControlScenarioProgramTableModel();
		
	return tableModel;
}

private HashMap getProgramHash()
{
	if(allTheKingsPrograms == null)
		allTheKingsPrograms = new HashMap();
	return allTheKingsPrograms;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) 
{
	
	LMScenario scen = (LMScenario)o;
	
	if(scen == null)
		scen = new LMScenario(); 
		
	Vector assignedPrograms = new Vector();
	
	for(int j = 0; j < getProgramsTable().getRowCount(); j++)
	{
		LMControlScenarioProgram newScenarioProgram = new LMControlScenarioProgram();
				
		//program name needs to be converted to id for storage
		String name = getTableModel().getProgramNameAt(j);
		LMProgramDirect entireProgram = (LMProgramDirect)getProgramHash().get(name);
		newScenarioProgram.setProgramID(entireProgram.getPAObjectID());
		
		newScenarioProgram.setStartDelay(getTableModel().getStartDelayAt(j));
		
		newScenarioProgram.setStopOffset(getTableModel().getStopOffsetAt(j));
		
		newScenarioProgram.setStartGear(((LMProgramDirectGear)getTableModel().getStartGearAt(j)).getGearID());
		
		assignedPrograms.addElement(newScenarioProgram);
	}
		
	scen.setAllThePrograms(assignedPrograms);
	
	return scen;
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
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getProgramsTable().addMouseListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMScenarioProgramSettingsPanel");
		setPreferredSize(new java.awt.Dimension(420, 360));
		setLayout(new java.awt.GridBagLayout());
		setSize(420, 360);
		setMinimumSize(new java.awt.Dimension(420, 360));
		setMaximumSize(new java.awt.Dimension(420, 360));

		java.awt.GridBagConstraints constraintsProgramsScrollPane = new java.awt.GridBagConstraints();
		constraintsProgramsScrollPane.gridx = 1; constraintsProgramsScrollPane.gridy = 1;
		constraintsProgramsScrollPane.fill = java.awt.GridBagConstraints.BOTH;
		constraintsProgramsScrollPane.weightx = 1.0;
		constraintsProgramsScrollPane.weighty = 1.0;
		constraintsProgramsScrollPane.ipadx = 204;
		constraintsProgramsScrollPane.ipady = 114;
		constraintsProgramsScrollPane.insets = new java.awt.Insets(30, 8, 36, 8);
		add(getProgramsScrollPane(), constraintsProgramsScrollPane);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}


public java.util.Vector initProgramList()
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List progs = cache.getAllLoadManagement();
		java.util.Collections.sort( progs, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		java.util.Vector newList = new java.util.Vector();
		
		for( int i = 0; i < progs.size(); i++ )
		{ 
			if( com.cannontech.database.data.device.DeviceTypesFuncs.isLMProgramDirect( ((com.cannontech.database.data.lite.LiteYukonPAObject)progs.get(i)).getType() ))
			{
				newList.addElement( progs.get(i) );
			}

		}

		return newList;
	}
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		LMScenarioProgramSettingsPanel aLMScenarioProgramSettingsPanel;
		aLMScenarioProgramSettingsPanel = new LMScenarioProgramSettingsPanel();
		frame.setContentPane(aLMScenarioProgramSettingsPanel);
		frame.setSize(aLMScenarioProgramSettingsPanel.getSize());
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
 * Comment
 */
public void programsTable_MousePressed(java.awt.event.MouseEvent mouseEvent) {
	return;
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) 
{
	
	LMScenario scen = (LMScenario)o;

	makeTheProgramHash();
	
	Vector assignedPrograms = scen.getAllThePrograms();
	
	for(int j = 0; j < assignedPrograms.size(); j++)
	{
		LMControlScenarioProgram lightProgram = (LMControlScenarioProgram)assignedPrograms.elementAt(j);
		LMProgramDirect heavyProgram = (LMProgramDirect)getProgramHash().get(lightProgram.getProgramID());
		
		//do the gears, man
		Vector theGears = heavyProgram.getLmProgramDirectGearVector();
		LMProgramDirectGear startingGear = null;
		JComboBox gearBox = new JComboBox();
		com.cannontech.common.gui.util.ComboBoxTableRenderer comboBxRender = new com.cannontech.common.gui.util.ComboBoxTableRenderer();
		javax.swing.table.TableColumn startGearColumn = getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.STARTGEAR_COLUMN);
		//populate the gearbox
		for(int x = 0; x < theGears.size(); x++)
		{
			gearBox.addItem((LMProgramDirectGear)theGears.elementAt(x));
			comboBxRender.addItem((LMProgramDirectGear)theGears.elementAt(x));
			if(((LMProgramDirectGear)theGears.elementAt(x)).getGearID().compareTo(lightProgram.getStartGear()) == 0)
				startingGear = (LMProgramDirectGear)theGears.elementAt(x);
		}
		
		//gearBox.setSelectedItem(startingGear);

	  	// Create and add the gear column renderer	
		startGearColumn.setCellRenderer(comboBxRender);
		javax.swing.JComboBox combo = new javax.swing.JComboBox();
		combo.setBackground(getProgramsTable().getBackground());
		
		combo.addActionListener( new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent e) 
			{
				fireInputUpdate();
			}
		});

		//create the editor for the gear field
		startGearColumn.setCellEditor( new javax.swing.DefaultCellEditor(combo) );			
		startGearColumn.setCellRenderer(comboBxRender);
		getProgramsTable().setCellEditor( new javax.swing.DefaultCellEditor(gearBox) );
		getTableModel().addRowValue( heavyProgram.getPAOName(), lightProgram.getStartDelay(), 
			lightProgram.getStopOffset(), startingGear);
	}

	
}

public void makeTheProgramHash()
{
	LMProgramDirect tempProgram = new LMProgramDirect();
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List progs = cache.getAllLoadManagement();
		java.util.Collections.sort( progs, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		
		try
		{
			for( int i = 0; i < progs.size(); i++ )
			{ 
				Integer progID = new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)progs.get(i)).getLiteID());
			
				if( com.cannontech.database.data.device.DeviceTypesFuncs.isLMProgramDirect( ((com.cannontech.database.data.lite.LiteYukonPAObject)progs.get(i)).getType() )
					&& LMProgramDirect.belongsToControlArea(progID))
				{
					tempProgram = (LMProgramDirect)DBPersistentFuncs.retrieveDBPersistent(((com.cannontech.database.data.lite.LiteYukonPAObject)progs.get(i)));
					//make an entry using ID as a key
					getProgramHash().put(progID, tempProgram);
					//make an entry using name as a key
					getProgramHash().put(tempProgram.getPAOName(), tempProgram);
				}
			}
		}
		catch (java.sql.SQLException e2)
		{
			e2.printStackTrace(); //something is up
		}
	}
}
		
}
