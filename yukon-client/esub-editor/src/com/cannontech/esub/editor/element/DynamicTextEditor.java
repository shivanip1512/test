package com.cannontech.esub.editor.element;

import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.DataInputPanel;

/**
 * Creation date: (12/18/2001 12:54:28 PM)
 * @author: 
 */
public class DynamicTextEditor extends com.cannontech.common.editor.PropertyPanel implements com.cannontech.common.gui.util.DataInputPanelListener {
	private String[] tabs = { "General" };
	private DynamicTextEditorPanel dynamicTextEditorPanel = new DynamicTextEditorPanel();
	private DataInputPanel[] inputPanels = {
		dynamicTextEditorPanel
	};
/**
 * DynamicTextEditor constructor comment.
 */
public DynamicTextEditor() {
	super();
	initialize();
}
/**
 * Creation date: (12/18/2001 4:07:28 PM)
 */
private void checkValidity() 
{
	getPropertyButtonPanel().getOkJButton().setEnabled(isInputValid());
}
	/**
	 * @see PropertyPanel#createNewPanel(int)
	 */
	public Object[] createNewPanel(int arg0) {
		return null;
	}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G240DA8ADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D9FDECD35795E7D2E0D9D490EBC32194CD591ACDEC4AA45AE5EBC6E9972AEE4B5A2EE59B992D84D2D6ED9215B654BF50C6A754DD3FB87E4A87CE6C98873AADECE18D1D9742C834E0A521A10900B50C0C241036F46D0BFDE3BF727C1EE73F10049532F33F6C17E0078F09C8C7775DF33F4E476F1CFB2E033CFFAC2832C8DE04ACD5087F7D4302D038892122EDAFAF933CB3A84F09CCFF07006A51079DD6
	E847E0EC2BBF4F69C6E3818774D56867C2FFBFFCFB50C781379FBE321C3393A6F975E3CD0B1F73396B629CBB6D1F0855C0DB879487DEBED085A2677FBB66677C92685771EFC2968921A596B2E7E554EF6237D858BC1537B5921923465E6463E540A7F3AA00F6DB52FBB2A117863BF87F726773516EE72B25DAF216E9FFC12392DBBB435BE674GB5C4A58AFC900436FBF8B2529B52E3EDA1D953F44D4021E89BCECE0D376130E26809B62C6298560CE0F0E0D613E3CAE8944F9803F488ECC8E57ED654C177C3E883
	7419FCBF2A6D666767CF66230AC75E30E413AFD049649A2ECC4B97709E2CC4D63277AAEAEF32EEA50C178131B3F744441983B06F99BA778791B277D3784E87AA328AD9FA5E92320C7D6F9134E73CCD5A17C596C79513213DAAADCB05730D4AA66A7B46B962BF9546D682958215816D84F258721C87A96636DCFF9B5A3EF123DF0F45F42DAF9EBF2506936774B0D661BB9804914E3B00C3FAA20C9043559351C086F984B55B66E3AA0D03CBC860206842DAE498ADD2D5865D0A4BEB258E3E4799DBDAB72657230868
	9585FE8E50BA20E2202DC03BD3FA9D38F17CD6FA1D856CA4B4D99D199A0F4B896354142206F1026B77F0ECB17DB291DB7755BBDE476C327E3859AE5B3FF1D432E7304B7675DE49EDE776E835276D424EFEA56B59E76CB77B0346321D6030A7CAF0F8993EAD9C230CFF1A72B718788C63AFD25C12FEA90C35B884EE476A048EC3569548FD2CBD238EEB1EBEAC65BA18BC850E053EDDAE11FC72BC70F782A1E71E538F5485B48A94F58A7F629BC7EF933743A65F3235FF1873C18BE9A1A11F5512062C0570C5B92148
	63AACEA2BE672DB9A27B783CF5819ACFA1B863B456F0C2B694DD035D659886A0A5591C4E39FAB2A7016968289243F0C0AC0E443EBDF39EE2DFB9E93C978F4B86CE4FE06303F34416D7650B30A3ECG5FA5CEE2739F531C158B5F05C04CD69BAB89EFAF743DAEE117523986E85F85E0E395D22092A2E24809D9F17EC6C4F2893E8A99F2G473196060FA49F5BCC31486DB22127E47EBE65139869874113A97E8FD1BD63439EE6AB0B71F2BAAE9BC6C2991FB27088B68CC50B24576F2032A46705DF92719837920C35
	02DE6FG8D27741B30F8CC7A8D697A64D4FC14F80E5B69F51AE743B07FD31738837B2364CEACF0891C6E9A93B89D28FD88854A5BB26214D0290B6173A55742D8BBC777CC1FD19E993B433897D4604A9C1BB76716DDF7FDC665ED4CE1F6EF31913B236ABC671BC05D55426EDF34B433F8BBA5A7310830738970988E27D146662E1632E36C8BD2E60C95CA19B176B5E9F18CBDA6E542909B7BB63DDB74E94D04492724869A431A06295029A50855983BC609AD94CDB1AEA96188B6C614EB987CDCCD3075AA0D41D178
	0ED68BACCD7FD9E029655AAAD47A31B363FD36664B2DD2E9B57373566AB41618CC5F29A4B667F5CB3FBE25990C7FDD9A5F1C4F734B63564CF31F3492BCCE62D9BEEF335506C89ED127F0CA378DF2B531F1C209012BC84072BA4F2D196B3C5DA09F29895BEB721C83C0EC6D6B548F99CAAA0E1B0F6C748E0179F1205C9A011B1E2AA6D33CF240746B2A4A4C1F724B5BB6B771A37E6F9406F4CC827A5D14AE47EC8D8B46AEF23D5878899B51CBB56502029A2297D2C44DC56E8A465BCFF967E1417BB6F1771EF31BEB
	53F072D0C69F927AF98D735D1E1A0577567D8E322F3FB64F59DAABF4AF3619F3D5DFB2290794FAADF0FF84CF34234C75EC08DEEA3C228D860793FA9CA78C70996C446A49EB1A0367056EDAD1CF3EF6127840007EFDC005A9B9C2761BF194353B201DD092C9280342F8C658D233931B107B0933C55D76F7330DCAAB9A335AE8D3AD334D165A05B6FA49C96478A570D11D106DD5FB3D49C6D0E342F59C818F8AF9D6BBAA992EF97FE1C71540B592F9F1CE93F50F370E57C6F5691C5ADDF7F3FEE575CD9E2DE3066BD8
	FDE3C9719F207C4BA6BE2BFBFEFDD25476D198FB3ECE5C43AF3808DE6D7B602E5AA774EAF7561A749A56954DC8693443C9E2D3CFA891C5933CD7A88FAAEC1A2C58DD76A07571B06C2982B1DEC5B761AD01FEFE0A77E4AF6115C0FF4BBEF19F2D0C2014FD9EB2FD4B93F7FB075C8E31B3165E53B3183DFE62A2FE18C97537517EACE2716EC1DF57087D6389BC019310F1D89AC781B731577728DDF7032D9074597C5FF45BA86EE775A9F054F6BACF6D91F9B18E7DDC208DC04F38D9ED41E27851E912FBA6208804EB
	E9A40EC328241E2C5D5AAB5636C03F8BE88CE886E88D90EAA0EF77478974393C655D4D50EAF83A790AFCD5EE13270D3681A88DB5D28436F634F7861ABB3ABA0F30D8AC18B647E2176BE056D89CE8E0589F63ED0EFC00E3B93FA775CE05313286214F31CFC87DCDF31290A498BC1DD04227644890358E4BBF5BFB9CA2B6BCA27F38FA491EDD509F860AFA449DB97397711EAD7A4A8AB43464E84637FF4528CFDA62E1F296FA523923905E336C4C5F760AB3ABE05C6FA53C5613DE539E44166FCB8835C14B46BF18B7
	CE8CFE8419476F1BB7CE64BDD4E99EFFEA5EF815E9BD536D3943C2373D4F2DC36AB7FF1F75F70DCF3C5C873C75379FC01B869DD6364F0B9F1AFD6ADEDBA5D9326C33092FAF73AE4C2F1F715077G707D0DA28F6D2D7FAB349765AB7CB5442EEDDA4081D3DCAC0CCBEFD59E43EBFB6E6E7D3B7EAE577D6C0E5731DCFD03E12EF1E18E7FAC658FB4AE4C616EDE017BE198ABEE94B97CB1AF09D73FAF4F5981B48494864A81AA7089DB8EB6107BFA51B767289E9F0242D7B5BD0718FE3F6B3D3B5F85B65B853E0AB7BE
	08CA166D30D4E741C5310F57173C5D50F6007219FE03BE221FF5FF1E3399E8G68B2D09CA8F73F50EFF9C30069C76050E798F2A8CAAB86215CC585CF2759A459313D236467CB11179B8EE7439B6C70CFC8A967472F40ADBFAAC7D2EB16F9081C299A1F41100FAD771078A37538A22B4AB5DABE0D40C4AD426C1A530BD298DAEE7A76755E7BFB6E68BDBD03697F04B77BDB6AE9EF6637D419B773DB6A856F62EF291FFA333F25F6F84DEFA96677A9BF714DBA40CF8950F62057002CFE01274946BFB1BCE54052A0BC
	2E7AF57A6B0BA14E7F17AF3B2E6F79EE7D1B4DFFFC24269D578DC30F117D85BF6CA37EB274B8E5B21E62A3B60292ACEA30291C2F7B686F72F80232996F3ABF3BDCB97EDB4B355CCF6BEF58EFF4B64E773C5FCFEB400909A4CE693F5A6FCE714EA6077524C2504E7EB760BA7E8E34C53FC2D54984EA3839C668DB717236220F873C92124EA2DB9493454E222AE9A5ECC4E76210B1FE81340A6514A3F3663BE64F523D16EC75033F096512C053423B66C2A751A98A7C1CA6616FAFF57CED11589F09492AEA486AE41F
	96A6F960542C0153583B54C976CF0AB9FC107B351348FD4534667FD0CB878800B60E45998AGG0C99GGD0CB818294G94G88G88G240DA8AD00B60E45998AGG0C99GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGD38AGGGG
**end of data**/
}
/**
 * Creation date: (12/18/2001 4:06:27 PM)
 * @return com.cannontech.esub.editor.element.DynamicTextEditorPanel
 */
public DynamicTextEditorPanel getDynamicTextEditorPanel() {
	return dynamicTextEditorPanel;
}
/**
 * This method must be implemented in subclasses.
 * @return com.cannontech.common.gui.util.InputPanel[]
 */
protected com.cannontech.common.gui.util.DataInputPanel[] getInputPanels() {
	return inputPanels;
}
/**
 * Creation date: (12/18/2001 2:35:42 PM)
 * @return java.lang.String[]
 */
public String[] getTabNames() {
	return tabs;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
	return getDynamicTextEditorPanel().getValue(o);
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
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
		setName("DynamicTextEditor");
		setPreferredSize(new java.awt.Dimension(525, 300));
		setLayout(new java.awt.GridBagLayout());
		setSize(520, 400);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getDynamicTextEditorPanel().addDataInputPanelListener(this);
	checkValidity();
	// user code end
}
/**
 * Creation date: (12/18/2001 4:05:26 PM)
 * @param evt PropertyPanelEvent
 */
public void inputUpdate(PropertyPanelEvent evt) {
	checkValidity();
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DynamicTextEditor aDynamicTextEditor;
		aDynamicTextEditor = new DynamicTextEditor();
		frame.setContentPane(aDynamicTextEditor);
		frame.setSize(aDynamicTextEditor.getSize());
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
 * Creation date: (12/18/2001 4:06:27 PM)
 * @param newDynamicTextEditorPanel com.cannontech.esub.editor.element.DynamicTextEditorPanel
 */
public void setDynamicTextEditorPanel(DynamicTextEditorPanel newDynamicTextEditorPanel) {
	dynamicTextEditorPanel = newDynamicTextEditorPanel;
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) 
{
	super.setValue(o);
}
}
