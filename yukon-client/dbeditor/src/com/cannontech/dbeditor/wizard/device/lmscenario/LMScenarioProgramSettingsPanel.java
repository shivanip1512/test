package com.cannontech.dbeditor.wizard.device.lmscenario;

import com.cannontech.database.cache.functions.DBPersistentFuncs;
import com.cannontech.database.data.device.lm.LMScenario;
import com.cannontech.database.data.device.lm.LMProgramDirect;
//import com.cannontech.database.data.lite.LiteLMProgScenario;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.lm.LMControlScenarioProgram;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.database.data.lite.LiteGear;
import java.util.Vector;
import javax.swing.JComboBox;
import com.cannontech.common.gui.util.ComboBoxTableEditor;
import java.awt.Component;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.database.db.device.lm.LMControlAreaProgram;
import com.cannontech.common.gui.util.JTextFieldTimeEntry;
import com.cannontech.common.gui.util.OkCancelDialog;
import com.cannontech.common.util.CtiUtilities;
import javax.swing.AbstractAction;
import com.cannontech.common.gui.util.TreeFindPanel;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import java.awt.event.InputEvent;

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
	private javax.swing.JList ivjAvailableList = null;
	private javax.swing.JButton ivjJButtonAdd = null;
	private javax.swing.JButton ivjJButtonRemove = null;
	private javax.swing.JScrollPane ivjJScrollPaneAvailable = null;
	private javax.swing.JLabel ivjNameJLabel = null;
	private javax.swing.JTextField ivjNameJTextField = null;
	private Vector allGears = null;
	private static OkCancelDialog dialog = null;
	private static final TreeFindPanel FND_PANEL = new TreeFindPanel();

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == LMScenarioProgramSettingsPanel.this.getJButtonAdd()) 
				connEtoC2(e);
			if (e.getSource() == LMScenarioProgramSettingsPanel.this.getJButtonRemove()) 
				connEtoC3(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == LMScenarioProgramSettingsPanel.this.getNameJTextField()) 
				connEtoC4(e);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() == LMScenarioProgramSettingsPanel.this.getProgramsTable()) 
				connEtoC1(e);
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
	};
	private javax.swing.JLabel ivjJLabelSearchInstructions = null;
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
		this.jButtonAdd_ActionPerformed(arg1);
		// user code begin {2}
		fireInputUpdate();
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JButtonRemove.action.actionPerformed(java.awt.event.ActionEvent) --> LMScenarioProgramSettingsPanel.jButtonRemove_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonRemove_ActionPerformed(arg1);
		// user code begin {2}
		fireInputUpdate();
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
 * Return the AvailableList property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getAvailableList() {
	if (ivjAvailableList == null) {
		try {
			ivjAvailableList = new javax.swing.JList();
			ivjAvailableList.setName("AvailableList");
			ivjAvailableList.setBounds(0, 0, 160, 120);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAvailableList;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G96E699B1GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DF8D4453591D002249894B400C2042891A32284094AFB4516362834257D30D29F1F8F5B346535349FED7D7AF82DCFA71B8DEEC282798342A34AA2A159C8DCC20812C60A89C9B041G7912948209A4B73BB749127D636E4D2F286F1C19BBF76FEEF67343D73F564F13FB674C1C19B3E74E1CF3665CD9C87601482484DDB6A189C9C4796FC482A1AB9B8959D21B3F04638C578DFA22796FA0C086F973
	B0A2F320EE4B378C7AF4726EDE83140B207CFA1CC13F975E3308A42D7E84DE020CF3AF54756C1873675147118DFC1C37E879CF8929705C89D08C38E600486BB87ECEDD0E029F0472CE659DA1C1C708ACE19B33A5C7030FD75A55C2DD9BC00E875B946AF2157A150043B66B6139D9E2ED8934C9265C5DDADE2634EB1B99CDDEFB7E3CAE8D4752744F214343E827A8CFA3798995C4A219F6626F70BC59505BFD4A62F218AD025369F24A222547EC6D942DB659A51987EC4302E4B5DB45FE1BC5B45B9D9E0B6894A41B
	2B3C3C7EDC13725EA0393AA54151A44A324D596DE9901C229D7AFD0D4E2759E220E341FC209CA2A3EE2D85E5D8866F8DGBDB21F530F52CEAABC4F6A0DA61B96D5861D53EA194DE5336C1DD3737F7EA4B1228F7BEF5425AAF4A6A8F7GB0DE666AB8AF9B7BB8AFEFDF4636597D86FD91C0F5BF4747D3FC9B14E50030FE4E635F8AB80FAD1F2FA05BBA2F6BF68561B12A1F7136365F4B637AADAFEF5FC775082420AE9886E0DC008AGAB409400A8007DD4FF1FB13C874F4ACE394A65F0381C15EE77E93BD5BA6732
	0AF6F8AFAF079A8557A8DADC121590262317ADC581786130F240D7BF91585CAE912ED3B10DCB08ACF76B12824CAD7645865DBA250F78816F5C0C03A8378AAB4ACD8F6F89CA3F8CFF0E622D9ABCDB2B9FD3DD40F28854458FF2B9376C643C546B971274E39E5DDB81F80978D60F2EF6087150B16465054978659DA863B040C7832C8258G308DA0E7184BB8A97978D8B22E03BDA1B985FBD3DF27DB106453FDB63BD51494D93768C613F5A0E0F4DD9326E3F209BE2EDD230D4B2400F25998FDC9D7BA4C64D1BF6C15
	8B9BFBC150316D43A3F5E3273A06B3691E8899E6EB18286267D1FC38864F56769045E3B9926A741F9B742CFD4B954477FC6E2FA3A245CF795CDFC7FEFD196BC8A45469EF9874ECFD875350D676C0F98A403DG31G6B813682683F60EB5C10FCF41C7B28C1333E0CB6B9E58FBC45C1D9926A1C9ED9F0DA440BE07404CE3B68A1CA1B4C9414EB278F5DDE3A3FADB046A722D314845966F2C26F02C384E57230B646148CECA3093436596690E1G071B70FECDA9D938A602C77E3B5BAA482237852BAFCEC119748B97
	21C768G5E1B3FC0F939294D4D03770AAF382C9E58093888A8AFD56572C8CAA6BCBF8565966D656555B68B32A1C8C3DCC74FD85361399A5A5B3F04BD7315C13F66AB3EBFFF57427530BA6DD1D274627500FAC875AF046914B544DF8F1BE87FE1000F81D88B30A5047B598D1696F862F8C81373D5E03D8C8448EFC05D7E520BE7E37C1E0E112C410DF70C44A078A44DBB13FBD6BA6AEE6AAD863D89207696AE7BDF269819CE1E96BCA257428B92A810E8752E84EB7B739D68E344FE51A9D70BEE51E905970F22833F
	5E116C554FAE172ABBDBA99E6DD595A810C7457FE9C78643C39FDAED6775EF50FAC116A5DBE71FACF2272E56EFCFC5DEBCBE2650234CF5F7864E358366B888902A4E750E54AC0D1E553BDC3DFD6EE65CC10A3EBEC97D723D50BE76963E7E77DC4638EB03DA3E1F163D750517DBA630DE584F44ED7469890D91CCA7C69F636A955C835CEEA6A9F8D657CB6B7413A0C6GE88638B1094FFFAB654D1FEEA2630F4DDB7215963F7E137F01E3B05D181496DC37272585566DBB52826B766C3451F5773E34C03A4B6A2252
	F00E2E8127E6AFBC1C06765162F2BAC53AA1D5924262675FB563BCECCE1BFC49E66D9665A65B3088715EA45469295486C741FB5CA4EEB3878EF11B19B77CB0D955DE9FB03E0AF820D717BE1969EB5EE42F4DE4BCAD5E09FBCC340B8E90D4152B4FA9B37CD2EA5F953CA711624A3267A12DC98950BDF44F95B9DCFD9E51CC7FB6C82247A3DACD5704FE41AC8C48E63A8E66F3D8D7032F0546775FBFE59CBD54AFF2EB3BEFBF276D38BEE84F9F95DEACACA60C4F7A7373A91FE7CF7749B278E72B35C82082B733C703
	A8F539A447C8C6ABE97578B93D268E50DE695733BF271A2E1155E3C461B56BFA141657C651616A973FDEF659989365380471FB7B2EC14A6FF908BC4EB643DEBAE3936D56A28BB8G59CC7F32580272BAE8768C4070CA07D5D8BBFEEE3BEC12D867F477718E0107634CAF54E6224ED603AE37G38A7F33FD0348B6B39DFD01CDF154BEEE75B8D6A56CF26FB491A2D599F21135939FB27DA3FEC0C7A7F9C23FE07982C1E7139AF9D6D30C73C5EA7C27807DA7E29B844E33874CC3F3A0B023D4F9B9B1A52F740532E71
	F98C3FBFA370BEBE1801FE2ED76D7FF046F602B19E6B13591D2582766916EC8E88915009AB79056F6B3479055CA96007E19EED70F483B05A3926F760B956613D7AD66CEF41951E0B88857AC80055CA1F64D6A69F562E3315378B07F2B4401ADBD9BB43ED5AF6475AF83B8640775C66ED976A536E97EA3BF5005F8CE0184A5AD5CFD5E2492958EE3E1ADBB82A6216D35CCF41D95D2A67E726F3695ADC0955E38DE8F79122157ED7CE75BFCBFE11057DEE847C1629DCFF97E4E8631ACA0F47E53151D0DED9637319DA
	1B4CF0CD19A970043D6201F325D59C64FAF2B993F945D8DFC1ABEB7D48113F1111E7179A1A1D61E1E355G5D254D2EAAB8EE54022AD529AE6B22CD9CA0DC8EDF1C5166D7D66E3A2C5B91C48EA4D419FF28D78E91DBDE5E4EF81AD30EE766C0F9A38BE5525C5DE7B37749B6BCD48832C00D82D3AC1607B91108F24131BBC2F93E658DFA460B0072D200B5G6C6C37061E89F15CCD004B19E650B3DD2F4E536AFA836281A2GE2G186DBB5A7ABEBCA7646FD43977A61651BDF6F32E0D49FBE1D605425F5AA58B4949
	2F4A83660AC2F7D56BD6CFE3F25EB24DAB6F520467375782B0B9CCEF65F2A83B9D76B3G33DD4EC22D6D0A3E1D45506BEE67B278C3EB89BCA76E1F389C7EDC74D7F85E3463A47C8CFF250D739F8F3C65CE677C5F559A087F66695CDE1519CEFA473FA9CF48461F5549476F013EA35571AF183463EF184E46A7E1DCFEFF69C45BB2C1F72948EEF5E1A65B6BFEFED14D3715E86D522ACDADC16DD2F698538BD3183FDD92F663DE2A86FCCB9837CB6BB3B3B4F62991AC01606C864F23465CD9B8B770B1CA7936AC9C17
	7A98424F2D737720DC6C50E7A8C0A4C09C4086F50C346CB173E6B50323644DCEA6729C54B6E5CE295F701E510C70DE8AD02B45519C51A4EA8F5A3E41F2C43BD47C1DACF7244133F557D3BC16E3204EB4031F1FFEF045BF8796B543770C384A2F4C68B60E20FB472F9C6FC747F4615A67B95CE6EAD4273C7CACB5AD4C267D700A56263DBE036922698E037E649DBC07FDA38F579B73624DF7F05B790C8967988765F5G1B6F60B91FB777229FA561305FGCA819AGE400F00058F03E16ED5901FC0C6FDA86F3B05E
	7C81134F4C561B136B0291F47EF2DD9F9EC82E3B1A768516AB339804FF0768ADB2A8E3ED8967BA5051F6F33CBAC75079731AB753170E7170FCAB676164CC4E435C4E1B63A1FA841D592FAC4F8CA42F43265CA0FA484F860C575BCCBC368A1B49756963FD08DB8D6575B3396EBC150D3A48EDCD034B66148937B3DD3BB1F6F5C936EE1B1363FAA94E621268C11B619E5C93BC8770681E40B900E5FB8267G1E5AB3FAFE6B59BD41F284AC9755C4E313F8189F39937C90452D7A8CF159D0B629387AF64455C3592A62
	9C9D088B05F2240A9B56A3AE964AEB6F64EB9ECA636ACDD0A6F7C1CC83D08A508C6086883D0B370B206DB02E41189A6365183BD8BC3C8E1EDB55F6A9D75CFEEBBF9A64E84622710772C49E1027E0F4642EC0FAD8B821311186633EB4253CCBF9A6135173E7C9FE6537E97B67285E38E179C58FB5D098EDAAF53F12F713G399BBC8693FE16CFEACDD65A54482E2AE36A9E692EF9BB709EE932C8F0BA45D0D65D53EF51BD0DFC244F6276780EC3E80FE5A80781C44D626BF4CB9E6653D6C2F95D2C11F20E576036E9
	5ED3E70FECEB8C0423FE70E8A10EDDBA1B79418495DFC671959ABC730F4F1A783752FA28DBBA1B6F6B641C60FBB2BDA7701E5C13036B6AF175C99651FF5F3D1D132356F93DC71DACBAF0C9D81BFCCAAF8BD237B8E25F3ECF69D9DDB07A8FF3026DED365EEBFC563BC455E1F7115616E65F2D44DA4A73645DA3E54D7A3B27CD5B5FF1353F9F7A783286055E2ABC8703767723CEEDFF25EAFF53CD23FDAB8C8B509F13C775BEDC57C135DCCB4BC36AF85792D15EEEC96C92A5C9343274A559F0B76A45E4EA87A361BD
	66EE9E8B3F74995AEE3BB0646A83A36F3E075A412BBEB98740D583345C43F8E27B6971815C73DDF0100265E8F20B96D29F013416F6CE9B876D5782EC81C8053A2288BE66148E2485C21B234FC1B9E47C5DCBED6DACE89729361D2927ED05C13F368FD39B3C965AEDD05B76B6E3EC4EB3235A0D4D6A8DFF45F33AB7A3497173AA533CF8F69ED165E9EC4418D9E3A5AA7B851B9D63BB455E1C43397AE662B86DBB67D2F8DD3D5009B998065F3B9DE544F0CD22A0D9FA70B32454C7555F4362465201C0F123FE8E7816
	B97C1CC38EEA4FB945F314F316DA7F3994283EE78E37DD292D7CDB488D400583C44D997B1BCA9C34D96B57EE9314DF7343B19B1518073648B0577F7BF29A45674C757F3EBC7B907F3EEC023A30395C67AE22B172D625BD43BDCBF129738C7A3CF93C1D69B0620A21DC2B62DEAFC4DC9B14C3D55C7EF47AFD8A4AF1AA6E4B826E7F574E63B27AG53874A1EBD25F97F1EE9A25F11A68AEC0CCDDF6B98EC4ED3E98C3D9566CB6E65F2F8015058884AF9AA2EB8014B26D8457D321553062838D3F6CEBBCB45AD30F35A
	C895375044F1DB81D7F19FDE49B299093A6F16FF068527B8E0D4938155B60768440F6226521262A263718F0B8B5919BBF2C0FB66AECB399A744CBD65BE2623914AB326A4D439B7F52C43A70646814B4BBF11EC5653C2F7BD350EACB6102E9A143E575D47E303482BD447679B74C60032797C5B50E0BE4F4D45BC38105407C8816FBEEDEA3E2CEB184F781167FBF3811B373C3C1D0DB92D1D0F39946AB74FC75C0AF66D5EC6197DD6474E8B2C7E3FFC6A51072272FA6B77F7E86B115F3AC4ED7DB91F7AA48DBD1B5B
	8B07795CDEFFE1A931AF6F88FA2F2BF8811BD333722CC888CD38F745735B591A573E6BDDF317FEBCEB4E68FA8FF969DE3DBC9EBAA6CB7BG4FD306AD606B171D0F6B379A4A9B81DEDB4073D4FF6C8AF23E2716DB49FB9E64673F54C8FE1EFFC262386AC82E6FE63AD7EEC0B9D245CD2538D8A8EFD4F12F32BD85E57D7D9CB7D406B893142BD55C0A8C4435C039C7458D8C70BDF5C345C59F20FE924A7177F31B161C075FA3362965ACDA46EF812CBC6F50C46D8E76B5B15B96BB61B1A672AD1E694553F978AE7449
	2EC651832183E6BFF85D06BC5481596532B75B5C683989532B43073DFA757161403147A56AE3329742FEDF48E30F2C778E90ED5CC563857A6948A68E38DD122C21AFFE8F699B0036CD25AFC98F9E37FDCAFDD598340DGE0F1566C829E9F2C865C8600EDGB90B407EAC62FD4EAF42F56DF2B9E556CFAC3575ADD0EFD55B7C0CE09B9984206C47FC1CFF6CA2EC7FC9C27598B2FE0B70F89062B620C1582D5801E8F83F2C3C3371F73561782F835E9045EDFEEF01F73CA76D230D476FD0A486992F8C7AACD45ED9FF
	CF19C66B0F734D1F55D15E7E9057826529892CBFE673FE582A75B369F3EBF57C5C655F37B50A6D87F714FFEE77B36A7722G3FAA0A1F4B9C46A3703CA8DC9B79A58810904F3AC5C99EE2D87AA5D259C383D7A7FA5F1045DE875A515EED0A72BFCF1AA8FECB147FF972712B7CBC398D6A5A9E603923990528032B203C9160B5005487A14E8728FD104F6DC9E3B199A38F596CF2574321542EC96D30794DE83F397B144F3E45FDD36C530BC95CED46205F445A9EE46BF3C3F9EEF8F3EA82BE597C8E9E417945C05D9A
	00ADG7A07E00F8194BF4467770631084D8F1727D21685CB8F7DB248A7075B5F0B4603921B5B7581BE37EA682B85407E10570606407B2C078245532154D7C5BF649FCF5FC971F18F7947537F174F6369B5D0D7944DED7C4903DA5906C733312214E7DC74C8FB4E68D6C8DA3A35CA3BCD4A7335GF44C4E5434627D7DFED0E41BC530B373F8DA109ADCF61BE5C8318F9D5477619EB6BC2C5A30D6DC0B9E17E49B86BBB612125B1627A96D47G35AA6D36D6BC5BF63AA41AF4E0E75BDDED5CEE06C23BC8000DGDB9F
	E65F9A99DDF8D9F0DB7CB07545064586FD76E25E7E87993EF6B61E7A668A282F87E036E84640D836B419F86D5B8D208BDF4CEC91162396FB77A49B731B87821F21DF227EFEA934DF29723730836763F3E38879237677E9DCCE254F2D05F9A4E89CF53C644407A7A012420792037D61FDAF6D2E740341E9CB0AA186ABABAEAEAED4475DFD54CB6BA9B00EB2EE5987C59FA0A90B2D1E21E7B4344F1B9773D8780F857E31F0C601B616BDEC720F05D10E5E7ACBA67FD8B851A7961EE6720F05B9BD1B4B9B473DF379A6
	99A107B3209826F7ABA51BE0FF3748F88C7EAFC1B99C206D89BDEB717FD8FF79A78861366C55D4EE4BF29FD9C42ADFB26A8E1140364C780862F39E717A1C9B6425C45627335C4BE309FD3CBCB25A199F11007CEE7D185547A20B0A4D94703313121BFA2CG032900FE8DAC63D9987BC267B551A2B78B5DAACD270946ECBC5F475C1DD2E7236B00F77300CD5BB03D1651848D1D5D4CB69DEEA7045BA93366FD45552F7F2C3E6EEB9D034D7F1E4260795B7905017337D105013F29ACAE9C7DDE659305416FD5BED528
	3DD749565DF98A57A69AF4EFB540E600578152E3381F7C64BDBC68612D7C11BE32465A0D79E33C09AF73715FBB92FC2E470E0CFDFF3F7C88665EBADD326CF2E07F9CDFF18457CBF639A91261EA0E200CA4DBF70F4C1AAA78CF0F202E5B45AE0AE66729D0FA46AA02F9554670B356527DB43F84659095171C4B4FF133D41CDD4DAF45293852B46ACF9E053344235CB74F48C7FFD22A16A3E839C1AD9B73EFC657303F716B73DFEFEA0C711E51146FF2746C6C863EC29E657278BE4B81C3B9CA451588DCEECBD55C8F
	68991BAC0177A59CF7E1187EC6824A25AAAEF61853D6AC617EF0209573705A5B9E1A7C2E79E2CDE3F3DDD5E5FDFBD3D563057A7A53150D6D15CD6D67EBEA2AEB2A151CBE3DFF930C3E76C2E35D158B671BFDFAB8DF835D72186CC49B0F49420127B5CBF83E615FD2B0D69E579D2CE61BEC97CF53480679452F5A38DFA40F713C46F7CF505F64C139812067B1FE66CA28A0047B02188CA3891EBBA9AE85CF7041870A3FBFBD60256B9DF8571F0E7990AB58E6D7B7B891F07947CA8B837B0D603E04DE13D37CCE6810
	F7BC6950F878E43A3B2AC09B2306403CE7BDB63A6EB39B56BF0CB1DAB434DD8D309900AC05B88F2088208220992087A084A082A086E09540FA00F800EDG290FF31B376E847A50E067BAA539C88D1FA4E83F31771F8AEE7B3EB8B5366D3B6D943D2308B6EB482DFCC71BFE0AFE376F6A72082A0D8DBF152E626ABC8DAE0F8DBDAA1377020160B1F09C3501C618E75163FCCFFDEF7F58717D4F281D4420318568D8BCDE124B63710847D9BC3E941E2B9FE75F45D96CBC39E02C58197F16017E8E94E837BE6E0D1D75
	CF78474E1F26850E1DAD549627C37B5CA7783CC29DA8877523CF3D4D036B443E2FBC172F0D8BEB1FE0BC5BD55AEB87FD4F85A1545E05C0FD185AA63E43B77F52F883F51E4D3BCE98EB5EDA704F072C04B132FC667D8B49171F3FD09B3A865A2DD779699A7665E75A97DEFEDE989E0B1F9D1AF57067C73F4C2097097637B0FF9BF167469F5217853A1F706918F47EF7F7985D94CAD734ECFCFE30E2997FEDE3F9995A5166E57E7912AA0A6FD8661FAFD9FB0267CB6CD037EE9977C5AFD17FDF9AEB506FCB64BEF0A3
	0B89GE7C9607B727661607BFF6670587BFF6EF06058A7F2F8E46C7340F060586711E1ED6C237C9EE59877D9B7582D96355DF295F7D945BDCD7BC45CA51BD56ED1712BA8DF082F95F1C00E7FAE65EB0062DA945C8B9A5CE58537EE98ED98CC3947255EB3FA116AEB174DEEE7651F52B6B6A718B4D59E2FD09EEDEEC143774FD59C724DF4BD66CB226E1587769B49987E66F8F1A1EB7BBF7B711E49C462097ACB15171BD4FD39124B63019E500348D87E9DE1E1AE77699BE239CFFF0A66F352179B74A6008A000979
	D7FAE4D7665B10601D6FFF9F9E4FFCD91C72C2A15A7AC05FC550BAB25EC9813FEB56321C479D47745E711E6D18589934B9494B77B3EFF97B299A9EC7BF1A3C43ABD4A46CBE512A2CB42AABF61BD32CE23F6AA5FCDF4E2371D4B8709E3D1C6F5FDE9B62D6C2F95DF29E0BD7E0CAD831AFF51A779F1F787A4F1C3F79DA47E0EBADD0FDD34E5CF4BBB3FC6EE01A974F373342D7C76E4D59F56718F4016D7AC314EE63727159752D4B39DDFF3F007E3B86CF7A5B758FA9BE7DC9FF3B7E4DFCEE57F321AE64C92E83C985
	23719D7FE4301C6893742E110C21BC6AAC61715573D47736811DFE85774B13ECC93CAD613A3C9F3BD274755FF3393E162E607A1AABA22E964AEDABB82F478B3CBAFAD2737E1D1BBA6FCDCCDFDF79DA47D0662C1E31FBD6F03BB300E12EA273F65BF86D8EDB0BDFD13906C1DF916ADA9C97C72E45E318CED16EC09DD73F4D2D811A0DG2F8364469974A763B8CFF7E7FBF9CA97474B93332D178B78FD0036B81E831DBAA2877AFB224DE1BED5B0FA8E74B7857EB9504A84ED7D5BC1F3204AEF1EC75D8301007DFBBE
	B56F3E8A03457C16A4F9A412B95277D44E7869CBB16D5559BA222BA35BFB282129A3C983E809865D4085049789D11E6B047A6EC0A51994505F7798AB6D3271083149A83B0C1D18B4B476390D0251C3AFE099BBDD03E619E4D6FABC36EEA7B661BF2613C9B63B92E66C12DC8E954D47786ACD84DEBAD0010B4ECBD921B07EADB02FB41FF57D4E7D9F7CF26DAB97BD09E4FB9D590761F813A8510C26E8455B2E4ABF7DF006A43B716AB59ED06A085EC39FC99EF988CEA1A43B2B91FF1E6811C5EBA54F61016F5DCDFF
	B6F394F384C7EB85CF0F0CF161C775C46F44FF2402E432EA3B606CBE4A92279F218CC9C2A572CD88B5EF40EF966597FBB12B3DE4BD336B70413371F109C4F7062458DD02750CE00163EC9DC916FBC03EBDAE3BF5507DB16B48205E3250C3F77B556963E44D7835DDE53AF7703E30344DE9E7F4048CCA64A8ED09278F335A42DA55A3C8B014A85549780FEE3824BC356E16F7647C6FFF3237CDC79214C92268D3875DB2594EE2C798FE976D94A7EDBE83FC4BAF7F7639057FF1F26B96A0BAC31268CFE4E068F4EF2B
	73FD0ECED13AADE6765BBBE6ED1A0E1C39F1DAF4D953E5182FA4B8BD78BBA5685C68B7B7DE2532E83E6B3962598F7F798AE551CBAA1383ECB10074E83768F29C2D6C6A02B8C46DGD6863AFFDB6D9E5B187D5AE48E4E5CFD7645B7BBC1132AC9222338187EBDC67F16605FE394F30CE20E154072ECA5237E67DFBF4867CC550F9632D1D7B8FAFB176030590760251A26FD8691FFE8D095BAB2DEC7E371CD7D474B93FF3EA8D1C774300F595984349A8F982055925B5609F0B6218A491591ED0B0E8E16B2C2CD913B
	F3243EA1BA937F6C41BFFBE933FD74EF6E04E70FD46FF8E70DAF87FD67FA04C9A8CF21C0FEF3D41E910BA1AFDA63CE2C6506014C03894C524C2B03E229BB8FB22F44FF6CECBA824E2654BB9B346E868592FC791649AEEE850DF839355CA813A3D5F61B2557A87708C6EAAD0C0253EAF47B1BDDE6EB555EA629D24091F51A9175815A7C0BEAC025D7E6F872CCD7E632C34333117615724F6059981067DAAF4FEFA9520CC03EE15F96E066595669B07775BB4CC227EC46FFB40E19B53BC01716B98EE530849DC93AC2
	323A705A73F931DF14F8CB70D599945BA8329F88E0AA321E6C75411D6EF3DA6D62F9EE366BC93ACF75276210024F7241B38E94CBCF59F003414FE03EDB349A79176EFFE535E9E4CBF0D6E57FE3B5472872EFB4B97ABC3231D3343BA0DA017DA3982B4049C8AE3B31D292859A4808C63B8D230042FF6549DE4124BE7B1EDD7815768E5536DBBF8EFAD737785B4A3F9F70ED7FBBD4FF4A27F9D4405F7BED1EEBBF130F190DE05F1A1B9C025DAE8B765ECA27953FBB1F9E12C56F374E3F64E37F9E5EC629E4F53A8264
	7B1A06667F81D0CB8788E40978AE449CGG64D2GGD0CB818294G94G88G88G96E699B1E40978AE449CGG64D2GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG7E9CGGGG
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
			ivjJButtonAdd.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJButtonAdd.setText("Assign to Scenario");
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
			ivjJButtonRemove.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJButtonRemove.setText("Remove from Scenario");
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
			ivjJLabelSearchInstructions.setText("Click the table and press Alt + S to search");
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
 * Return the JScrollPaneAvailable property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneAvailable() {
	if (ivjJScrollPaneAvailable == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder1.setTitle("Available Programs (must belong to a Control Area to be listed)");
			ivjJScrollPaneAvailable = new javax.swing.JScrollPane();
			ivjJScrollPaneAvailable.setName("JScrollPaneAvailable");
			ivjJScrollPaneAvailable.setPreferredSize(new java.awt.Dimension(404, 130));
			ivjJScrollPaneAvailable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneAvailable.setBorder(ivjLocalBorder1);
			ivjJScrollPaneAvailable.setMinimumSize(new java.awt.Dimension(404, 130));
			getJScrollPaneAvailable().setViewportView(getAvailableList());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneAvailable;
}
/**
 * Return the NameJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNameJLabel() {
	if (ivjNameJLabel == null) {
		try {
			ivjNameJLabel = new javax.swing.JLabel();
			ivjNameJLabel.setName("NameJLabel");
			ivjNameJLabel.setFont(new java.awt.Font("Arial", 1, 12));
			ivjNameJLabel.setText("Scenario Name: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameJLabel;
}
/**
 * Return the NameJTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getNameJTextField() {
	if (ivjNameJTextField == null) {
		try {
			ivjNameJTextField = new javax.swing.JTextField();
			ivjNameJTextField.setName("NameJTextField");
			// user code begin {1}
			ivjNameJTextField.setDocument(
					new TextFieldDocument(
						TextFieldDocument.MAX_DEVICE_NAME_LENGTH,
						TextFieldDocument.INVALID_CHARS_PAO) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameJTextField;
}
/**
 * Return the ProgramsScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getProgramsScrollPane() {
	if (ivjProgramsScrollPane == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder.setTitle("Assigned Programs");
			ivjProgramsScrollPane = new javax.swing.JScrollPane();
			ivjProgramsScrollPane.setName("ProgramsScrollPane");
			ivjProgramsScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjProgramsScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			ivjProgramsScrollPane.setBorder(ivjLocalBorder);
			ivjProgramsScrollPane.setPreferredSize(new java.awt.Dimension(404, 155));
			ivjProgramsScrollPane.setFont(new java.awt.Font("dialog", 0, 14));
			ivjProgramsScrollPane.setMinimumSize(new java.awt.Dimension(404, 155));
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
			javax.swing.table.TableColumn nameColumn = getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.PROGRAMLITEPAO_COLUMN);
			javax.swing.table.TableColumn startOffsetColumn = getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.STARTOFFSET_COLUMN);
			javax.swing.table.TableColumn stopOffsetColumn = getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.STOPOFFSET_COLUMN);
			javax.swing.table.TableColumn startGearColumn = getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.STARTGEAR_COLUMN);
			nameColumn.setPreferredWidth(100);
			startOffsetColumn.setPreferredWidth(60);
			startGearColumn.setPreferredWidth(100);
	
			//create our editor for the time fields
			JTextFieldTimeEntry field = new JTextFieldTimeEntry();
			field.addKeyListener(new java.awt.event.KeyAdapter() 
			{
				public void keyTyped(java.awt.event.KeyEvent e) 
				{
					fireInputUpdate();
				};
			});
		
			field.setHorizontalAlignment( javax.swing.JTextField.CENTER );
			javax.swing.DefaultCellEditor ed = new javax.swing.DefaultCellEditor(field);
			ed.setClickCountToStart(1);
			startOffsetColumn.setCellEditor( ed );
			stopOffsetColumn.setCellEditor( ed );
			
			//create our renderer for the Integer fields
			javax.swing.table.DefaultTableCellRenderer rend = new javax.swing.table.DefaultTableCellRenderer();
			rend.setHorizontalAlignment( field.getHorizontalAlignment() );
			startOffsetColumn.setCellRenderer(rend);
			stopOffsetColumn.setCellRenderer(rend);
			
			
			//for the gears, just use a default renderer
			startGearColumn.setCellRenderer(rend);
			
			//create the editor for the gear field
			javax.swing.JComboBox combo = new javax.swing.JComboBox();
			combo.setBackground(getProgramsTable().getBackground());
		
			combo.addActionListener( new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e) 
				{
					//save the edited cell
					if( getProgramsTable().isEditing() )
						getProgramsTable().getCellEditor().stopCellEditing();
					fireInputUpdate();
				}
			});
			combo.addMouseListener( new java.awt.event.MouseListener()
			{
				public void mouseClicked(java.awt.event.MouseEvent e) {}
				public void mouseEntered(java.awt.event.MouseEvent e) 
				{
					//need to populate the combo editor and renderer to the program's gears
					userWantsTheirGears();
				}
				public void mousePressed(java.awt.event.MouseEvent e) {}
				public void mouseReleased(java.awt.event.MouseEvent e) {}
				public void mouseExited(java.awt.event.MouseEvent e) {}
			});
			startGearColumn.setCellEditor( new ComboBoxTableEditor(combo) );
			
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
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) 
{
	//make sure cells get saved even though they might be currently being edited
	if( getProgramsTable().isEditing() )
		getProgramsTable().getCellEditor().stopCellEditing();
	
	LMScenario scen = (LMScenario)o;
	
	if(scen == null)
		scen = (LMScenario)com.cannontech.database.data.device.lm.LMFactory.createLoadManagement( com.cannontech.database.data.pao.PAOGroups.LM_SCENARIO );
		
	scen.setScenarioName(getNameJTextField().getText());
		
	Vector assignedPrograms = new Vector();
	
	for(int j = 0; j < getProgramsTable().getRowCount(); j++)
	{
		LMControlScenarioProgram newScenarioProgram = new LMControlScenarioProgram();
						
		//program name needs to be converted to id for storage
		LiteYukonPAObject thePAO = getTableModel().getProgramLitePAOAt(j);

		newScenarioProgram.setProgramID(new Integer(thePAO.getLiteID()));
		
		newScenarioProgram.setStartOffset(JTextFieldTimeEntry.getTimeTotalSeconds(getTableModel().getStartOffsetAt(j)));
		
		newScenarioProgram.setStopOffset(JTextFieldTimeEntry.getTimeTotalSeconds(getTableModel().getStopOffsetAt(j)));
		
		newScenarioProgram.setStartGear(new Integer(((LiteGear)getTableModel().getStartGearAt(j)).getGearNumber()));
		
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
				dialog.setLocationRelativeTo( LMScenarioProgramSettingsPanel.this );
				dialog.show();
		
				if( dialog.getButtonPressed() == OkCancelDialog.OK_PRESSED )
				{
					Object value = FND_PANEL.getValue(null);
					boolean found = false;
							
					if( value != null )
					{
						int numberOfRows = getTableModel().getRowCount();
						for(int j = 0; j < numberOfRows; j++)
						{
							String programName = ((LiteBase)getTableModel().getValueAt(j, 0)).toString();
							if(programName.compareTo(value.toString()) == 0)
							{
								getProgramsTable().setRowSelectionInterval(j, j);
								getProgramsTable().scrollRectToVisible( new java.awt.Rectangle(
								0,
								getProgramsTable().getRowHeight() * (j+1) - getProgramsTable().getRowHeight(),  //just an estimate that works!!
								100,
								100) );	
								found = true;
								break;
							}
							//in case they don't know the full name and just entered a partial
							if(programName.indexOf(value.toString()) > -1 && programName.indexOf(value.toString()) < 2)
							{
								getProgramsTable().setRowSelectionInterval(j, j);
								getProgramsTable().scrollRectToVisible( new java.awt.Rectangle(
								0,
								getProgramsTable().getRowHeight() * (j+1) - getProgramsTable().getRowHeight(),  //just an estimate that works!!
								100,
								100) );	
								found = true;
								break;
							}
						}
							
						if( !found )
							javax.swing.JOptionPane.showMessageDialog(
								LMScenarioProgramSettingsPanel.this, "Unable to find your selected item", "Item Not Found",
								javax.swing.JOptionPane.INFORMATION_MESSAGE );
					}
				}
				dialog.setVisible(false);
			}
		}
	};

	//do the secret magic key combo: ALT + S
	KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK, true);
	getProgramsTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(stroke, "FindAction");
	getProgramsTable().getActionMap().put("FindAction", searchAction);
	
	// user code end
	getProgramsTable().addMouseListener(ivjEventHandler);
	getJButtonAdd().addActionListener(ivjEventHandler);
	getJButtonRemove().addActionListener(ivjEventHandler);
	getNameJTextField().addCaretListener(ivjEventHandler);
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
		setSize(420, 374);
		setMinimumSize(new java.awt.Dimension(420, 360));
		setMaximumSize(new java.awt.Dimension(420, 360));

		java.awt.GridBagConstraints constraintsProgramsScrollPane = new java.awt.GridBagConstraints();
		constraintsProgramsScrollPane.gridx = 1; constraintsProgramsScrollPane.gridy = 4;
		constraintsProgramsScrollPane.gridwidth = 3;
		constraintsProgramsScrollPane.fill = java.awt.GridBagConstraints.BOTH;
		constraintsProgramsScrollPane.weightx = 1.0;
		constraintsProgramsScrollPane.weighty = 1.0;
		constraintsProgramsScrollPane.insets = new java.awt.Insets(1, 8, 1, 8);
		add(getProgramsScrollPane(), constraintsProgramsScrollPane);

		java.awt.GridBagConstraints constraintsJButtonAdd = new java.awt.GridBagConstraints();
		constraintsJButtonAdd.gridx = 1; constraintsJButtonAdd.gridy = 3;
		constraintsJButtonAdd.gridwidth = 2;
		constraintsJButtonAdd.ipadx = 44;
		constraintsJButtonAdd.insets = new java.awt.Insets(2, 12, 0, 7);
		add(getJButtonAdd(), constraintsJButtonAdd);

		java.awt.GridBagConstraints constraintsJButtonRemove = new java.awt.GridBagConstraints();
		constraintsJButtonRemove.gridx = 3; constraintsJButtonRemove.gridy = 3;
		constraintsJButtonRemove.ipadx = 20;
		constraintsJButtonRemove.insets = new java.awt.Insets(2, 7, 0, 24);
		add(getJButtonRemove(), constraintsJButtonRemove);

		java.awt.GridBagConstraints constraintsJScrollPaneAvailable = new java.awt.GridBagConstraints();
		constraintsJScrollPaneAvailable.gridx = 1; constraintsJScrollPaneAvailable.gridy = 2;
		constraintsJScrollPaneAvailable.gridwidth = 3;
		constraintsJScrollPaneAvailable.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneAvailable.weightx = 1.0;
		constraintsJScrollPaneAvailable.weighty = 1.0;
		constraintsJScrollPaneAvailable.insets = new java.awt.Insets(4, 8, 2, 8);
		add(getJScrollPaneAvailable(), constraintsJScrollPaneAvailable);

		java.awt.GridBagConstraints constraintsNameJTextField = new java.awt.GridBagConstraints();
		constraintsNameJTextField.gridx = 2; constraintsNameJTextField.gridy = 1;
		constraintsNameJTextField.gridwidth = 2;
		constraintsNameJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsNameJTextField.weightx = 1.0;
		constraintsNameJTextField.ipadx = 195;
		constraintsNameJTextField.insets = new java.awt.Insets(7, 3, 4, 98);
		add(getNameJTextField(), constraintsNameJTextField);

		java.awt.GridBagConstraints constraintsNameJLabel = new java.awt.GridBagConstraints();
		constraintsNameJLabel.gridx = 1; constraintsNameJLabel.gridy = 1;
		constraintsNameJLabel.ipadx = 9;
		constraintsNameJLabel.insets = new java.awt.Insets(11, 16, 6, 2);
		add(getNameJLabel(), constraintsNameJLabel);

		java.awt.GridBagConstraints constraintsJLabelSearchInstructions = new java.awt.GridBagConstraints();
		constraintsJLabelSearchInstructions.gridx = 1; constraintsJLabelSearchInstructions.gridy = 5;
		constraintsJLabelSearchInstructions.gridwidth = 3;
		constraintsJLabelSearchInstructions.ipadx = 50;
		constraintsJLabelSearchInstructions.ipady = 3;
		constraintsJLabelSearchInstructions.insets = new java.awt.Insets(2, 12, 7, 152);
		add(getJLabelSearchInstructions(), constraintsJLabelSearchInstructions);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
public boolean isInputValid() 
{
	if( getNameJTextField().getText() == null || getNameJTextField().getText().length() <= 0 )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}


	return true;
}
/**
 * Comment
 */
public void jButtonAdd_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	Object[] availablePrograms = getAvailableList().getSelectedValues();

	//also need to update the available programs list
	Vector allAvailable = new Vector( getAvailableList().getModel().getSize());
	for( int i = 0; i < getAvailableList().getModel().getSize(); i++ )
		allAvailable.add( getAvailableList().getModel().getElementAt(i) );
		
	for(int h = 0; h < availablePrograms.length; h++)
	{
		Integer programID = new Integer(((LiteYukonPAObject)availablePrograms[h]).getLiteID());
		LiteYukonPAObject thePAO = PAOFuncs.getLiteYukonPAO(programID.intValue());
		
		//do the gears, man
		LiteGear startingGear = null;
		for(int d = 0; d < allGears.size(); d++)
		{
			if( ((LiteGear)allGears.elementAt(d)).getOwnerID() == programID.intValue() )
			{
				startingGear = (LiteGear)allGears.elementAt(d);
				break;
			}
		}
	
		//add the new row
		getTableModel().addRowValue( thePAO, "0:00", "0:00",
			startingGear);
		//autoscroll to show new additions
		getProgramsTable().scrollRectToVisible( new java.awt.Rectangle(
			0,
			getProgramsTable().getRowHeight() * (getProgramsTable().getRowCount() - 4 )- getProgramsTable().getRowHeight(),  //just an estimate that works!!
			100,
			100) );	
		
		//update the available programs list
		for(int y = 0; y < allAvailable.size(); y++)
		{
			if(programID.intValue() == (((LiteYukonPAObject)allAvailable.elementAt(y)).getLiteID()))
				allAvailable.removeElementAt(y);
		}
	}
	//update the available programs list
	getAvailableList().setListData(allAvailable);
	
	repaint();
	return;
}
/**
 * Comment
 */
public void jButtonRemove_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	int[] selectedRows = getProgramsTable().getSelectedRows();
	Vector allAvailable = new Vector( getAvailableList().getModel().getSize() + selectedRows.length);
	for( int i = 0; i < getAvailableList().getModel().getSize(); i++ )
		allAvailable.add( getAvailableList().getModel().getElementAt(i) );
	
	for(int u = selectedRows.length - 1; u >= 0; u--)
	{
		LiteYukonPAObject thePAO = getTableModel().getProgramLitePAOAt(selectedRows[u]);
				
		allAvailable.addElement(thePAO);
		getTableModel().removeRowValue(selectedRows[u]);
	}
	
	getAvailableList().setListData(allAvailable);
	repaint();
		
	return;
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
public void populateAvailableList()
{
	Vector availablePrograms = new java.util.Vector();
	
	if(allGears == null)
		allGears = new Vector();
	
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List progs = cache.getAllLoadManagement();
		java.util.Collections.sort( progs, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		allGears.addAll(cache.getAllGears());
		Vector programsInAControlArea = LMControlAreaProgram.getAllProgramsInControlAreas();
		try
		{
			for( int i = 0; i < progs.size(); i++ )
			{ 
				Integer progID = new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)progs.get(i)).getLiteID());
				
				for( int j = 0; j < programsInAControlArea.size(); j++)
				{
					if(progID.compareTo((Integer)programsInAControlArea.elementAt(j)) == 0)
					{
						if( com.cannontech.database.data.device.DeviceTypesFuncs.isLMProgramDirect( ((com.cannontech.database.data.lite.LiteYukonPAObject)progs.get(i)).getType() ))
						{
							availablePrograms.addElement(((com.cannontech.database.data.lite.LiteYukonPAObject)progs.get(i)));
							programsInAControlArea.removeElementAt(j);
						}
					}
				}				
			}
		}
		catch (Exception e2)
		{
			e2.printStackTrace(); //something is up
		}
	}
	getAvailableList().setListData(availablePrograms);

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

	if(scen == null)
		scen = (LMScenario)com.cannontech.database.data.device.lm.LMFactory.createLoadManagement( com.cannontech.database.data.pao.PAOGroups.LM_SCENARIO );
		
	getNameJTextField().setText(scen.getScenarioName());
	
	populateAvailableList();
	
	Vector assignedPrograms = scen.getAllThePrograms();
	
	//also need to update the available programs list
	Vector allAvailable = new Vector( getAvailableList().getModel().getSize());
	for( int i = 0; i < getAvailableList().getModel().getSize(); i++ )
		allAvailable.add( getAvailableList().getModel().getElementAt(i) );
	
	for(int j = 0; j < assignedPrograms.size(); j++)
	{
		LMControlScenarioProgram lightProgram = (LMControlScenarioProgram)assignedPrograms.elementAt(j);
		Integer progID = lightProgram.getProgramID();
		LiteYukonPAObject thePAO = PAOFuncs.getLiteYukonPAO(progID.intValue());
		
		//do the gears, man
		LiteGear startingGear = null;

		//find the start gear
		for(int x = 0; x < allGears.size(); x++)
		{
			if( ((LiteGear)allGears.elementAt(x)).getOwnerID() == progID.intValue() && ((LiteGear)allGears.elementAt(x)).getGearNumber() == lightProgram.getStartGear().intValue() )
			{
				startingGear = (LiteGear)allGears.elementAt(x);
				break;
			}
		}
		
		//add the new row
		getTableModel().addRowValue( thePAO, JTextFieldTimeEntry.setTimeTextForField(lightProgram.getStartOffset()), JTextFieldTimeEntry.setTimeTextForField(lightProgram.getStopOffset()),
			startingGear);
			
		
		//make sure that the available programs list is not showing these assigned programs
		for(int y = 0; y < allAvailable.size(); y++)
		{
			if(progID.intValue() == (((LiteYukonPAObject)allAvailable.elementAt(y)).getLiteID()))
				allAvailable.removeElementAt(y);
		}
	}
	//update the available programs list
	getAvailableList().setListData(allAvailable);
		
}
//This is what tries to fill the StartGearColumn combo boxes with the correct gears
public void userWantsTheirGears()
{
	int currentRow = getProgramsTable().getSelectedRow();
	javax.swing.table.TableColumn startGearColumn = getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.STARTGEAR_COLUMN);

	LMControlScenarioProgramTableModel scenModel = (LMControlScenarioProgramTableModel) getTableModel();

	LiteYukonPAObject thePAO = getTableModel().getProgramLitePAOAt(currentRow);
		
	Vector specificGears = new Vector();
	//find the appropriate gears
	for(int x = 0; x < allGears.size(); x++)
	{
		if( ((LiteGear)allGears.elementAt(x)).getOwnerID() == thePAO.getLiteID() )
			specificGears.addElement(allGears.elementAt(x));
	}
	
	startGearColumn.getCellEditor().getTableCellEditorComponent(
				getProgramsTable(), specificGears, true, 
				currentRow, LMControlScenarioProgramTableModel.STARTGEAR_COLUMN);
}
}
