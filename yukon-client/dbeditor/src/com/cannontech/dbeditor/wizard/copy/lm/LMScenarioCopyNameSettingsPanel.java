package com.cannontech.dbeditor.wizard.copy.lm;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.database.data.device.lm.LMScenario;
import com.cannontech.database.db.device.lm.LMControlScenarioProgram;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.database.data.lite.LiteComparators;
import java.util.Collections;

public class LMScenarioCopyNameSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjJLabelName = null;
	private javax.swing.JTextField ivjJTextFieldName = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JCheckBox ivjJCheckBoxCopyLoadPrograms = null;

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == LMScenarioCopyNameSettingsPanel.this.getJCheckBoxCopyLoadPrograms()) 
				connEtoC1(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == LMScenarioCopyNameSettingsPanel.this.getJTextFieldName()) 
				connEtoC2(e);
		};
	};
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMScenarioCopyNameSettingsPanel() {
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
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTextFieldName()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JComboBoxOperationalState.action.actionPerformed(java.awt.event.ActionEvent) --> LMScenarioBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
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
 * connEtoC2:  (JTextFieldName.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMScenarioBasePanel.fireInputUpdate()V)
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
 * connEtoC3:  (JCheckBoxSeasonChooser.JCheckBoxSeasonChooser.JCheckBoxFallAction_actionPerformed(java.util.EventObject) --> LMScenarioBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.util.EventObject arg1) {
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
 * connEtoC4:  (JCheckBoxSeasonChooser.JCheckBoxSeasonChooser.JCheckBoxSpringAction_actionPerformed(java.util.EventObject) --> LMScenarioBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.util.EventObject arg1) {
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
 * connEtoC5:  (JCheckBoxSeasonChooser.JCheckBoxSeasonChooser.JCheckBoxSummerAction_actionPerformed(java.util.EventObject) --> LMScenarioBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.util.EventObject arg1) {
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
 * connEtoC6:  (JCheckBoxSeasonChooser.JCheckBoxSeasonChooser.JCheckBoxWinterAction_actionPerformed(java.util.EventObject) --> LMScenarioBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.util.EventObject arg1) {
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
 * connEtoC8:  (JComboBoxHoliday.action.actionPerformed(java.awt.event.ActionEvent) --> LMScenarioBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
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
	D0CB838494G88G88G9607C7B1GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8BF0D457F5B0459E5C12048C7298FBF0CBB0D6E5408E46EACC8D19482963B1B1E9E982D312B209BCA6AD49901726F4CC9ADA6E2EA4FE96A0C0FC75E11141C652EA7561A7CB8274DDE94582C2D231628251536E1334525BFD2B37CFE805095DF36E676D1334ABA1A6514C51FB771C7BB9675CF34EBD673EA519A76626D92C1904D852887F3BE8A1E4D1A9A179531FB9A2F0CB0F46DB08696FA440FE72
	1DE0AABC5F825A8CD73CA503AC11F6C2FB9D34BB9B62ADB878A0F9D58A5B61A546BAADC073DDBB761B7157F957D8E787EDD7962443F3AFC0B260D20F45DBDA0E617AED85C7B97E8C347772F7840B95D8B80EFDB2F20E1A70F13C5F86F8EE45B93230CF20A01B53EF43989DE0A5C072F156F78BBCF7D16E9E2BA8977D3E1EC0B6FF7F2B56BD3816E9FE819DD9EC6C837E341143D6D4C48AD91353844F2A167E1E4B9E556F70C801009A50E5CF2F435BA5FBFD3A2AB906FC77A54D6B704A77FC9E592178031A5A23C9
	7E0A8A77359676DAAB056496A9A0AB9BA97F97F2F6523989C904F6B89BF1AF65204ECA603D8920B3DB48700FFB2A04AE7A93C872F3D3E34A30BC1B711E1C9D11417B6D4329B3F3F07E27681E60B89B34BBG18FCD3335C8FA91FC78D8E03F4A8D61BC78EC81ACF2D8354F549AF37493A6E8B7404CCB27E2CC04838BAC748B87D8B64A1BDB75EF28A20AAD760FFF89C716D50D6G26658A59AFFDACE46F787CD532256BE56B3E98324F49E5B23F119B11FDCB653254E3549ED58DED0A1C00F5814A81EE83BCG1883
	10C57D20E020G1EB5DDFA1D6A772B011AE030D671EA57D42F2C40FBC585D0B82ED576281A1790EE6BB976A87C88C8BCB1524E9118EC1F92E11B0BDADFA23A7E57545EC64B1638E62675ADBE473A939159ECB6545BC73928372D0047F1E8978CFF1E623D36881E59C0A235B1ECCF875AE61B50F347DE410BBBF59E49A8FD453AA38AAF333F1BE06DBC49F8881F0C7042F4DC6BC49D4F87FC9240DA00CDG99F9711612BC21E3FD606CC4BAF6016DE981C9E99B6C8ACA1ADEBB68D33C3246F57DEC41F83A0E86EC5C
	42C90FE3FAC9B8EB5E23F52FAC092A17F589CFD95DF9CC9F5E3C08DE585A6BE32EBDADEF2CED6CB576F0AB751559F9EC8FD38C7C76AF911FE042333D7DAA6D0F6D25C04B7EB05E427ABB3390BF65C356BF4D1867A645471970EC1E355497311DG3474D37196363F490D98F303501E81B09FE0B940BA00ADG6927459EFFE72078A17D2845343FECECD8BB8CCFB92CEB12AB90522500C73E85E1C66AD2649061FD26EA28572E91639E277E6D01B59A65002CC93ACF8D406C90184018C22C4F57347D58C713A9355D
	6717E181FF1008F91F528E42D311C27A7F863D12AEC7FAB07ADFE9E8937724DBB0A3CCG6F37CF233EDAE9AC3743FB53E9212BBDAE444D0576D2C3AF879B8E4073AA98372CD4D4547BBC4806248D8B9B3567228CAB21FF7BC7F01E81F4FE9CEFD98F201C91FE7ADE0730477ABDAF927B1A17235AA38249E736F5B4FF34BDD652F52A815F89B085E0AEC092C0F23EB83FDF4B69B44D053CA09F785E7971C81BC5DE45BB72E90CB99D5D36477A661A91EBA16E83138F1A7B27195E77185ED3A6D887016D4B674DE85B
	24G7C83202AC06C4D75869B33D9BC2705155E504040E4EFE427D85F2BCEBC0364FBF2C02F1703F2408BAFA1EEA32D4EDD917B6DD68D5B6A20F80CE7F5E0E0A183FF57391F61E18E33B788FAB725CB3A2E793A86F56314B568814ACBE8C4288CF1D97FF0151E29A0238270401075FD67C1139D3AD535FFB0580E9E466D7937745C1E897D938A04BDD83F447CEE25517E5AF1ECC768F1DFF4BC449E6C65F3CCA68EC76C644507DE63E16CA17A9A6EAC11339CB1F01FD25C96605BD6A8647D69710EC92EF5E4523C3C
	BF69B552A631863305DFB6463665FFEF0CEE4B6FB7C637657FEE9C5FD6531A2259AA2325B7A16F6AD040E47B7B1BB0DEFA54C0C0268EE88CA1ACEFFD2F9D657085FC7A27BEEF0F2C37796E4B40D7A15A300F4664A6F83FDDA8E2675007A2F61E3A7F82C97A7D0C2879566C6797DA938B190D3ED598091D0C2783AE74A9D9117D2028BAF5B020B37CD19A6FB9BE14C2F1CDC76F6358B34DF08EF62935EA78C6101FCC105C68F04058A5AA1703BDDAE42DDB557C3277CC1FF4CFF2C8C33A036E0B2306129B70BD5FD6
	D4F459B6362830F77BB45995888E72236BCC7E2D7C92E6B7F06EDCD1989F6D3017CE1F2CF8B1E137FBA068688E7A1F8F42F5430E50906C9ADF3A8E2913DE39142D6B3E7D781FD87E284547383A181F967E236B62213960FAA93AB47C4763E15CBAACAA93C468E2D6F8BA5F1FDE59538F7B03F338D5494B658ABD7A7ECC1E2BD2F6E6ADF623FFEDG3F5B8A10E997E7D61C8B69624C6287F31D2AA8ACB4GADECC73F9F484DB479325D4E6E9E769A74E7A7202F18007E1F9350CF1E981FFEADA61D67364D9998A365
	01C199D2DFBC142F4A43BCFF1D5EFCE0946D16248CC67262194D18B7A926731C617F6CEA74187544D5BC437B0D793FF2F5BB417C164D49E26CE289678CEABEBF24BF182070BB1A830566BB1AA410836FF3C8916C99G332F97BE4F056778EE82E30BF03EA73244FD0E9D5A75G5EA2B6675CA2269F564F1DAD7A25BB20168728F70C6CB74D017D269877AB45866EA90A7BB19C2E0F250A7A6EDA0679FE489B1A9F75CC20E7010357600E51F56F4F2F613CDB811FDEAC6CF5075B1CE35504C22A47C7C39F5F13EF39
	4D678543ADF36F06A7B8C788EAE02F9C967B7A3A9BF9453A0423795EACAC3CC44656D98D7446C62430F5C354AF5B41933DF29318C61DF11C5E7249C3C468618FCE739D5372FDCFDBBF082187DBB1133F3AB8220765154BD299CFCFD7E0FD9F6DAE49C319F4748C7A9C033A8F8BA0C917E894E006406E0C9E946E6472CDA996F7C3898E345F2550DE853081005529CB32C41DCACA62AD65G4C47F6B84DBE96ACE1F96C6C92E117473251A6A699EE394E5F48BF4076E8D4G93FA1CF7301A2F3B7225F9246A4B7BD1
	6F2B266F1BE6DDD44274373AA422470E5685298AG13EFB0DB48E7AB0D37F4148A79DCB5E679E616B27916168A7956E51741F372315B3817AAB37BC4D2726C18BE3132145FB7160E760995B728CFGBE23CC78C455357DA61FE885AB1482BD90250CDC74DA8A0BC73C7D5E35B4910F08285F365FC459CF411C55G9DGC10019469A0357A73CDFEA880FF33FB435F074DD4D6232C83D72C659585A055D25E49763DE2DADE3F7A9BB8C7C990A5FE442333DFD095AB436F71E057A6C2C28AB16E60DDEFF555911EBEE
	9C55E6635E9CB34EB12A1DF1EE64B8E64F274FE48BFFB519FFC5C5A4B7E1FEF959A97C724CB9F147F13B9547F6C27B81C09C40E20055G1BGB24F0BBDF963FA34B8B5F2CFE28529C8BD4C647CBC7B5174734418F12375F36EFCB47DDC3E1595DDBFA2E762F1EBCA8D6A877D47FDDE68E7CE9B6226C1BB4E5045499BFBCCBE50227A823AE17FFFFF834FDFD5737578828277A30A73289AAD0C98EE6F4D58B55B211B51EB362C1B51EB3613B7473FFF483F992B26E3F78533E93C1E8F72AD85E03841A27A0D895A1B
	4F8B3B4E23FEF074C23C258440FDC1609D942FDFE0E7BE1E678C1F6818285647392261CBB85E9D039E31099FCC38C62C3ABB4DB4CF2C31B9F47C0A437B28FEC3A1B9C40D9CCFBDCE7BD77D9009D25FE20EC4C40E7EEFD93BF81F86DD2DABB3F82FF322BD474510B1A18A1E5FA73546369DFFEBF45B8935224D0654C14DA30F360F7B2DC78DDA44EBDD3A6CC7B1F87EC9476B1286B57A6871FFCE47B3DA2C7133DAE359A0535112EC330ECA8D9DBDD603BADA4EF5310ABF5773671618BA7AC69B466A3051FE1E3607
	0D753ED908BE29C8436AA0B8EF78A25A7C1BC6BE1C8E6DD3GEE004E0B6CECE6316465A14C293AA19586765B0232073417635899C5E2EC9274DF8D3099A093E8674A45195EBA0439E133DB6A1295E6A08C7F33D3B8EEE4A5A9E8579BB08E452C6E0828C924E2F3BEB1271C711CD4AE7C79B39A7BD7C3FB2301FB5DA9FCFC0B011BC3E3E053A7507EC460FECEF1C1E8CFB1F07F530338B8E84FB7F01EDEB15F62CFC4FE77DE2178DEC9AAA03F2B90FAAC6D9CF1E6E1B6D3D15128793C35D20F1B6E8A7F46EA5F4947
	87ABC4AE693553EFA750CE82D8DBA16E06429F097CF7D17CBC621E32A46AB7566476272D1BAA185DE4D6C6F21E742AE529EC4DA5C5E24D9620CF2FC4DCF211D93F38B77BD358794368879CE67AFE9EBBA2747A91F46477DD2B19FEEF849DF5D44447B3595EBEADE45B7A76E2227C4D5C185F0FD7F119B673675C3333ACC9BB97725C7EBF3A0F1228E7A135CAA67F6CE2F18E5A2B046D8EB8516EF5D5D55AFDC134D4622D425F90EC7D8C39DBD7C53FB9B96B2370E35619481F70BD8146AC87B84465DD856F3B2C2C
	8FDB7395EAF7A1996F87E46F39CB384677EBC4CDF86A1260EDEB8D56405DD02330BE87EA31CF985E2799FD8E75E29F9D98E7FC1F3B9D617BFE6FF8FC8BFEF67167F2935FC8CB04B5CE43737DED5B36311835F9CC7EB991707BF97B230D2B1C74B81657DEA8C2BFCA3A4472DA0B013FC271ABCDF8664BFDF6614BEB01D6F5D964C8C76E20CD4D0576F20055G9B81522FC0CDFF4578AE5FDEC2A6489F5B55209B8EB5451442B079DEADFA346F3E2F6F90BE13785A823274714E1875F0559566AB3779333CEC96FDB2
	79965CC579E6GAD81E0A540FA00ADG9955C2BE2D5D4E6443523DC657A5CFAF3D959042E1219EC16341413F05971BE5BBD54D5767CFB7FFF6D6C74EB9A65BBF7C1F393E4BF8E63A5592C3B61D0F1DD29D09F5A7A84FD037907137694AD89C61BE7EE2A7B156FDA7DD68B4FB61F3247E1FBA2D9F46D8B7112F17DA93D9F7C65261D4660F6FD01F5535C1194DBBF748ACCF796E3F0CD92FD67179DA78B3DD1EE5E1FCBE530DB33179FF5D13874F00BCE4B30AE5A78408B3F9DE1F2428BDF9F6DB095DD6FAAEDF4878
	6D9E9C4B76242AA8725EE0FA8F5A9F765B5523G2FF17E24EBB0DB355D091DBFFEFCA7FA7ED8F4A7FA6DF1764E785F0BAA6F447EDED4FB477C3D08795AD7FA719C1C8EFBBB9F60AD00F5G1BEA041F2D3A037B0A3FBE986BE38D5E9E4CDF7197873AD87F153B31E5FD6D6E443FD3783B3B1833F5293A2E7AF1FE01DFF19777CBD7039449EB3F3B28A3A87DFAF5561563D75FC51FD264EE0AE639D2A2BDE353EB63AD5935A2FF7AE19762CA20DDED60AEFAC4CE65B6F0739A90B7875A4BEBC58CDE60A346FA5E5F63
	F9F4CB6A0BFA1B588C7EF0232B8F4E54F62947507B1ABEBC83D741FC9B81B26AA0F683D48134837482BCG188910G1084309AE08340D600747AF80B8D60DC3D5827DFF76647628110BC51231B25C92CFEE6FCD4AAB47F04F93C46DC1FC805634C8529F0D05674E106253769DC8FCF76465E7BB93D936F7D73B41F15E14F5A07033C0ED950CB6B7B6E6E10EC5858B7FBB38C1CAB542206FCD87F30B874F671485C649D3A6F53C0361975A2B779C557485CE4159F771265A657BD9365A6A2BF09161BA45533775FC2
	EE42567A5B860C713D2A663B8FBCC9CA0D626B89209791465F0F9A449D51CAC37F3F680B2D47CDFD936B71B7FD51FDE8DB5FD89FCA6D0F6EC33B7B4DBE44F07B7A114F9E30258E23DF26017B1D01BBC667C45C27BE2F5EEB60CF746F6678A699979478537D48579045F5F0DC018977BB0EAB6EC7DBG11C11DE26C39FE3A5FBEC5E16DF25A4797G53B074F10972688BCAA63EEB8DDC046FEAE52FB936B2536634C6A513306F66F01E796E534F92ED50B15043ECE8GFF92454F42B73BA6F796468DC4467E328173
	82732DFA9B7E97E1B664F0B7B85B897B8D723DC08E883F68D7A4CDA77B300835295DB6DE296AA483EBD79B962F36C6CD9D8C06C29A59E7B064229ACA3EF736A2655D67D22CA455C536B30BF111D45CF697C923D661A2A9E0B1E1948FF78F8A867AAB220BB05B1E9162FD6F9B677FE565CFEF05D248F697B9069E58A6EBD4D93297AFF078AF401CE4D790EF7070078730D408BE52C27A30A23BC9E6F7ABFE298BC179D1A336A12413C3EEDC2998BF0194B7C921DE9D4D3D52CDD28378DBB5F2001195A950D34C7624
	1272EB2969C445D24FA5406FC14AAF4E62B0E6B938EC5F69134D6B1726902B136CD6C0DBCE4983F14DC5F6693D1A9C6AD595EFB8F8251ACE34F3881D82135A9418AE4B909F05F5E011E6B05D9FFEF6461E8DFB1C202E30C60AE9CF8CB88E2307372ED752E0A9D9F3697805DF55CE993429397AC7BFE8BCF25BCAACDC48817285599E8E6ACCE146CE22488EA7F02D7F64D7AB667DF355260DB044C952687DBFAC1C9169F5FD505FA5EB0FAFFAB2670338643FC03E02281E904E3CDBA75B3BA53FCF990617FAEA2482
	3F8B82CDA0045FC1E0D15BA8098549E05CB1EBC54913AF7C57E7147148D01D1CE0DB84C30BFBE455DFDC535D6D8B48468430DFB0FD0EB1BD76F10C6AF3A07C75C34DEB36F501FD551394FFC9897DDFCA7F17617FD20AA9251852B25834CDE45C3F5174F098E5BE982622231DE3E441C876BD009D200A6B605168E69534C76A988BC36C4E31260268F4CA5C0B5D71E53F21E7ECE5D262E17A7F5823F1ED0BF0ADEA96BC0FAE92D1C764FFE94E7737BC76E54CFCFF150B5F7939C6FF1F7A56G4AE4EB06BC25D91CD5
	5B87B0E10F157B3479A5C501D32F3FA66045BC28F6D817A3396A32811CBFA47AF0A223FDF7G796EB30D79FFD0CB8788FDE6C5B64292GG54B1GGD0CB818294G94G88G88G9607C7B1FDE6C5B64292GG54B1GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG7C92GGGG
**end of data**/
}
/**
 * Return the JCheckBoxCopyLoadGroups property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxCopyLoadPrograms() {
	if (ivjJCheckBoxCopyLoadPrograms == null) {
		try {
			ivjJCheckBoxCopyLoadPrograms = new javax.swing.JCheckBox();
			ivjJCheckBoxCopyLoadPrograms.setName("JCheckBoxCopyLoadPrograms");
			ivjJCheckBoxCopyLoadPrograms.setSelected(true);
			ivjJCheckBoxCopyLoadPrograms.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxCopyLoadPrograms.setText("Copy Load Programs");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxCopyLoadPrograms;
}

/**
 * Return the SelectLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelName() {
	if (ivjJLabelName == null) {
		try {
			ivjJLabelName = new javax.swing.JLabel();
			ivjJLabelName.setName("JLabelName");
			ivjJLabelName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelName.setText("New Name:");
			ivjJLabelName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelName;
}

/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldName() {
	if (ivjJTextFieldName == null) {
		try {
			ivjJTextFieldName = new javax.swing.JTextField();
			ivjJTextFieldName.setName("JTextFieldName");
			ivjJTextFieldName.setToolTipText("Name of Scenario");
			// user code begin {1}

			ivjJTextFieldName.setDocument(
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
	return ivjJTextFieldName;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	LMScenario scenario = (LMScenario)o;
	int previousScenarioID = scenario.getPAObjectID().intValue();
	
	scenario.setScenarioID( com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID() );

	scenario.setScenarioName( getJTextFieldName().getText() );
	
	if(! getJCheckBoxCopyLoadPrograms().isSelected())
	{
		scenario.getAllThePrograms().removeAllElements();
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
	// user code end
	getJTextFieldName().addCaretListener(ivjEventHandler);
	getJCheckBoxCopyLoadPrograms().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMScenarioCopyNameSettingsPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(369, 392);

		java.awt.GridBagConstraints constraintsJLabelName = new java.awt.GridBagConstraints();
		constraintsJLabelName.gridx = 0; constraintsJLabelName.gridy = 0;
		constraintsJLabelName.ipadx = 10;
		constraintsJLabelName.ipady = -3;
		constraintsJLabelName.insets = new java.awt.Insets(55, 9, 14, 1);
		add(getJLabelName(), constraintsJLabelName);

		java.awt.GridBagConstraints constraintsJTextFieldName = new java.awt.GridBagConstraints();
		constraintsJTextFieldName.gridx = 1; constraintsJTextFieldName.gridy = 0;
		constraintsJTextFieldName.gridwidth = 3;
		constraintsJTextFieldName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldName.weightx = 1.0;
		constraintsJTextFieldName.ipadx = 260;
		constraintsJTextFieldName.insets = new java.awt.Insets(55, 2, 10, 13);
		add(getJTextFieldName(), constraintsJTextFieldName);

		java.awt.GridBagConstraints constraintsJCheckBoxCopyLoadScenarios = new java.awt.GridBagConstraints();
		constraintsJCheckBoxCopyLoadScenarios.gridx = 0; constraintsJCheckBoxCopyLoadScenarios.gridy = 1;
		constraintsJCheckBoxCopyLoadScenarios.gridwidth = 4;
		constraintsJCheckBoxCopyLoadScenarios.ipadx = 18;
		constraintsJCheckBoxCopyLoadScenarios.ipady = -5;
		constraintsJCheckBoxCopyLoadScenarios.insets = new java.awt.Insets(7, 9, 4, 197);
		add(getJCheckBoxCopyLoadPrograms(), constraintsJCheckBoxCopyLoadScenarios);
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
	if( getJTextFieldName().getText() == null || getJTextFieldName().getText().length() <= 0 )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}


	return true;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		LMScenarioCopyNameSettingsPanel aLMScenarioBasePanel;
		aLMScenarioBasePanel = new LMScenarioCopyNameSettingsPanel();
		frame.setContentPane(aLMScenarioBasePanel);
		frame.setSize(aLMScenarioBasePanel.getSize());
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
	LMScenario scenario = (LMScenario)o;

	getJTextFieldName().setText( scenario.getPAOName() + "(copy)");

}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	//fire this event for all JCSpinFields!!
	this.fireInputUpdate();
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) 
{
}
}
