package com.cannontech.dbeditor.editor.port;

/**
 * This type was created in VisualAge.
 */

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.util.CtiProperties;
import com.cannontech.database.data.pao.PortTypes;
import com.cannontech.database.data.port.DirectPort;
import com.cannontech.dbeditor.wizard.port.PooledPortListPanel;
 
public class PortEditorPanel extends com.cannontech.common.editor.PropertyPanel implements com.cannontech.common.editor.IMultiPanelEditor
{
	private DataInputPanel[] inputPanels;
	private String[] inputPanelTabNames;

	//The types of ports that each editor panel will instantiated for
	//editorPanels and classToPanelMap must stay in sync!
	private static final int[][]  EDITOR_TYPES =
	{
		{		//0 - PortSettingsEditorPanel
			PortTypes.LOCAL_DIRECT, PortTypes.LOCAL_SHARED, PortTypes.LOCAL_RADIO,
			PortTypes.LOCAL_DIALUP, PortTypes.TSERVER_DIRECT, PortTypes.TSERVER_SHARED,
			PortTypes.TSERVER_RADIO, PortTypes.TSERVER_DIALUP, PortTypes.LOCAL_DIALBACK,
			PortTypes.DIALOUT_POOL
		},
		{		//1	- PortTimingsEditorPanel
			PortTypes.LOCAL_SHARED, PortTypes.LOCAL_RADIO, PortTypes.LOCAL_DIALUP, 
			PortTypes.TSERVER_SHARED, PortTypes.TSERVER_RADIO, PortTypes.TSERVER_DIALUP,
			PortTypes.LOCAL_DIALBACK
		},
		{		//2 - PortModemEditorPanel
			PortTypes.LOCAL_DIALUP, PortTypes.TSERVER_DIALUP, PortTypes.LOCAL_DIALBACK
		},
		{		//3 - PortAlarmEditorPanel
			PortTypes.LOCAL_DIRECT, PortTypes.LOCAL_SHARED, PortTypes.LOCAL_RADIO,
			PortTypes.LOCAL_DIALUP, PortTypes.TSERVER_DIRECT, PortTypes.TSERVER_SHARED,
			PortTypes.TSERVER_RADIO, PortTypes.TSERVER_DIALUP, PortTypes.LOCAL_DIALBACK
		},
		{		//4 - PortSharingEditorPanel
			PortTypes.LOCAL_DIRECT, PortTypes.LOCAL_SHARED, PortTypes.LOCAL_RADIO,
			PortTypes.LOCAL_DIALUP, PortTypes.TSERVER_DIRECT, PortTypes.TSERVER_SHARED,
			PortTypes.TSERVER_RADIO, PortTypes.TSERVER_DIALUP, PortTypes.LOCAL_DIALBACK
		},
		{		//5 - PortPool
			PortTypes.DIALOUT_POOL
		},
		
		{		//6 - PAOExclusionEditorPanel
			PortTypes.LOCAL_DIRECT, PortTypes.LOCAL_SHARED, PortTypes.LOCAL_RADIO,
			PortTypes.LOCAL_DIALUP, PortTypes.TSERVER_DIRECT, PortTypes.TSERVER_SHARED,
			PortTypes.TSERVER_RADIO, PortTypes.TSERVER_DIALUP, PortTypes.LOCAL_DIALBACK,
			PortTypes.DIALOUT_POOL
		}		

	};
	private JTabbedPane ivjPortEditorTabbedPane = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PortEditorPanel() {
	super();
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (3/15/2002 1:17:24 PM)
 * @return Object[]
 * 
 *  This method should return an object array with 2 elements,
 *   Object[0] is a DataInputPanel
 *   Object[1] is a String (Tab Name)
 */
public Object[] createNewPanel(int panelIndex)
{
	Object[] objs = new Object[2];
	
	switch( panelIndex )
	{
		case 0: 
			objs[0] = new com.cannontech.dbeditor.editor.port.PortSettingsEditorPanel();
			objs[1] = "General";
			break;

		case 1:
			objs[0] = new com.cannontech.dbeditor.editor.port.PortTimingsEditorPanel();
			objs[1] = "Timing";
			break;

		case 2:
			objs[0] = new com.cannontech.dbeditor.editor.port.PortModemEditorPanel();
			objs[1] = "Modem";
			break;

		case 3:
			objs[0] = new com.cannontech.dbeditor.editor.port.PortAlarmEditorPanel();
			objs[1] = "Alarms";
			break;

		case 4:
			objs[0] = new com.cannontech.dbeditor.editor.port.PortSharingEditorPanel();
			objs[1] = "Shared";
			break;

		case 5:
			objs[0] = new PooledPortListPanel();
			objs[1] = "Pooled Ports";
			break;

		case 6:
			String showIt = 
					CtiProperties.getInstance().getProperty(CtiProperties.KEY_EDITOR_EXCLUSION, "false");

			if( "TRUE".equalsIgnoreCase(showIt) )
			{
				objs[0] = new com.cannontech.dbeditor.editor.device.PAOExclusionEditorPanel();
				objs[1] = "Exclusion List";
			}
			else
				objs = null;

			break;
	}
		
	return objs;
}
/**
 * This method was created in VisualAge.
 * @return DataInputPanel[]
 */
public DataInputPanel[] getInputPanels() {
	return this.inputPanels;
}
/**
 * Return the PortEditorTabbedPane property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getPortEditorTabbedPane() {
	if (ivjPortEditorTabbedPane == null) {
		try {
			ivjPortEditorTabbedPane = new javax.swing.JTabbedPane();
			ivjPortEditorTabbedPane.setName("PortEditorTabbedPane");
			ivjPortEditorTabbedPane.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPortEditorTabbedPane.setBounds(0, 0, 400, 350);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPortEditorTabbedPane;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public java.awt.Dimension getPreferredSize() {
	return new java.awt.Dimension( 400, 450 );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String[]
 */
protected String[] getTabNames() {

	if( this.inputPanelTabNames == null )
		this.inputPanelTabNames = new String[0];
		
	return this.inputPanelTabNames;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

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
		setName("PortEditorPanel");
		setPreferredSize(new java.awt.Dimension(400, 350));
		setLayout(null);
		setSize(400, 367);
		setMaximumSize(new java.awt.Dimension(10000, 10000));
		add(getPortEditorTabbedPane(), getPortEditorTabbedPane().getName());
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
		JFrame frame = new javax.swing.JFrame();
		PortEditorPanel aPortEditorPanel;
		aPortEditorPanel = new PortEditorPanel();
		frame.setContentPane(aPortEditorPanel);
		frame.setSize(aPortEditorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.editor.PropertyPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {

	//Vector to hold the panels temporarily
	java.util.Vector panels = new java.util.Vector();
	java.util.Vector tabs = new java.util.Vector();
	
	DataInputPanel tempPanel;

	//We must assume that val is an instance of DirectPort	
	DirectPort port = (DirectPort) val;
	int type = com.cannontech.database.data.pao.PAOGroups.getPortType( port.getPAOType() );

 	for( int i = 0; i < EDITOR_TYPES.length; i++ )
 	{
	 	for( int j = 0; j < EDITOR_TYPES[i].length; j++ )
	 	{
		 	if( type == EDITOR_TYPES[i][j] )
			{
				Object[] panelTabs = createNewPanel(i);
				
				//do not add null panels
				if( panelTabs == null )
					continue;
				
				tempPanel = (DataInputPanel)panelTabs[0];
				panels.addElement( tempPanel );
				tabs.addElement( panelTabs[1] );
				break;				
			}
	 	}

 	}

	this.inputPanels = new DataInputPanel[panels.size()];
	panels.copyInto( this.inputPanels );

	this.inputPanelTabNames = new String[tabs.size()];
	tabs.copyInto( this.inputPanelTabNames );
	
	//Allow super to do whatever it needs to
	super.setValue( val );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "Comm Channel Editor";
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD0F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135998BD0DC55B5ED54DA1B69A4E32CE9C6C754E8698C9D931BEA2AF1A6256954CFBA460E46D4D3E5DA5B523156B8CD5BD41D7632408688ABC80892B5CA922241508592B22108B68B48F79329D09AEBC6FD30F7E1616DFBAF6F3DE5F7E183BD675EF75FAE6BAEB6FE181CFD6F1EF3BF67FF4EFDA1152FE49466B9AA8949ABA4565F71BCC26ADEA6C4D3AE19963885B45BC9927E7681BCCB1EBEDAG4F45C0
	3B774B594EF262FA3D8846ABE01CB11B6DFC9E5EF7110A57376E0117B4675C8E34C50E67EB66BF675110B8E7879BFF53D38A4FE7GB281074FED944FDF652932709BAC3C00BC87A1EB7CB8E70367A58B578C63FE00C5G997E787CE2F67205AD4D56784C128C326D4E579CAE5CA7E1CF81E4142F2D311E5564063C8D23F8569F991F38668C0C1782207079E43C3C811E2D7D9323EF0D28C1770824A82AE25211B1B77585CCD5F7EB2AEE363474436F5B8C51AFA9D46E9E43BD5FECABE2FB90D28E63FB994E570D67
	AC027795GEB81388CEF3E56EA71F8FA7A86327AFEA53FAC0D8CD5814E7B09C0DC062521A7F76CE17ABE9CC63D46001681309AE0B340F6002A71EC670B4C4EC7A6DF0727E758ECD703C1D571E8DA1B6C53CF2ABEAA43FBCB8BD0AC5C3BF4C455FD04F0DBBE6529CB414FFC405795371D6FBA2E134C23422ED96FDECF36FC374F31A305CE36E4B6B89A4639AE3A4763BA61E73725BDFBECFC2E5F4DBD7BBFA4716C9597297905A94EDEFDDF2DA3E66D33B8616CF563E86B4D4C56AB61BD4F72830E7F854397CC4471
	5C8747999E4715C00BCC885FB87D0C6045DB301C14B7FAD36AE1D9364731E5420A3109B8AF5CAF1A2298CB55F2363395E090C086D884108930D6963EB1BDD97FFF7AC63F241BED21006C23BA61EB1B424F4113C6CCDD6AD18CD3D2C66810248724E1199A441A73E698F99F1E332E0D796C881C51C5952ACBE6C0D5E0F7A9C8CD2A9BFCCEDF78D91C23D3C69D8C84A99C90540858F7A03C8B1E32E4187F56FC12C963B3B87D7DB06AF2D29A029DE183F867FA1127D067DBC07EEDG85C12107CDEC3FBE2AC3B43734
	F8D5F5A2248D22A4E4B00836FA0F45F6953CB7G703D462691378C46AB6DFDDC110A04FDBA82A3483E24C7C5ECBC540D79F2FDD0587A7727052DBBDC57113A7BFAD35A9AA1A748ED3CBD186C7339ECCF640B0F356E53E9E2E0FDB0F58C1CFF0CFFB56D99791FF17FE7ECDBED0A212D2A9570E100B30A50F1F7241A7BEC1BE4D061256F6860E0549757B81F7BD69473AA1D240A59C1B52A78604530FC646DE8F15CFF7D2A6DDBFD8C2F013F37030399B67EDFD174C949B47540F048248354B4834AE81C7E9EBB4B88
	8DEB71D091E7FD9445FC2206415F6D79B4DA4162C0D1A8E35ADEE251F5662BD426C1E03ADD8DA9A647C726505E93B4EA45CAEC4A49FCDD8EE15C71BA36D25A09B26801A02882C317ECD6500F35B6D10791CC40E1BD4E0267BAC578E4780070491A292B493A8F8FBAD26542E557F5B82AD56ECFCDEA5CA7B9CF4169543C9BACE6AC3C41F94D1FC43DA515D44B874AE6F01FC158FF8CE001AAFC607169BD89F1E6993FDD15E52ECADB47DBE265E893FAB6C4A1BDE1C07759FA7BE52CA209B6E46910539F0D218E6504
	DC40791DF93F164C57A3C0FB202B9A554DA817846A946B859EE0B984F53CCC957D411263A85FC698678290CD58609E17688F16CEE4101CEBDFC85B63D4E9DC772DDA5C86558BC5FF705EAC1E99835AA2CD684EFFAEB157F98CC39D8930B4EB493DFBA651C69C7742CC89BC7D815DB0FBA008A2C2C755B348AF66E18BED6507076DBA1CE17135DA0B671E5B61FD0BB6B79771DAD9580C7A582AA55750B206A7E713EB686547C58DAD871AF9D66466EF9074734DB05E86D02AE7BBEBGBAGFC3A50C3664CD1728935
	2F876C2AAB12BCB0A728AD59BF184D67F1E80D07E78BC59C224F323A71A7E67385F06695G9C7707DE442D0471FA9B37510B38DC9893C3601EB809389A98379AA2376FEC46586F37473B59D83347D79ECF4C4BB86F3366620F677E1B661C71F9647BC4603A3C0A244F5B571354F9BB0B244E5BB712E7B99E76C86CE2847D96B2FFDE4FA62972FABE23395AB1AEB51D7A298E99E8A0B005365FE0207DDEEDC77BAD05778CGBE3F244F4962A52A06CCC2AEB6F15EEC1F484965B0BE84F086C0B3F92F41F34E2AB066
	A2BFB4C9405A00C6C708AF04EBFF6395EBB7427CC7G8AGDF83509192E7961D4235C1A9928806020CC34E5F26D348DF964CDBE34FFDAED4C992F3B1643DE16A43BC466BC7E7F3E27D488DF19EABA701EF009313F8C73C915D4452CFEEEFB5BC959A2E9E17A6A53794B65D9D502AA958149DEAEAE8282BBEFA2C2116673C0CF0623D2E3964F84A3A03F041A4776BE556F36DD78ED87960DBFD62DE3791E8C5E1213F517D87C9C24E8E8C875D214920DB9AB6D952EEE9B99512E52FE596ABDFBF5ED7E46DF5A2AC72
	75DEAF6E9F0171E2004C30485711D7C53E4E3AFEB971AE68CB192FF3860FB85605B94F0F046379BA936EF37CCC57C9F1E6F5A45BF9BA0238B7CED6A6455B43F0E7F643135303F368684BD81F62742B6650115FFD790974FB66508B9356F3596EBAA8E4FB7A2ECCA2FF6F9FE9EBD1C6044B345EFA768F9DA0B517BC35037B454F0B63FEE19C2FC96F970D8D8D68996CD14B6DF738E82F309FAE08380DB084C2CBCBEF5CC1ED3B4DB497D9F2BC9D91F6DB5505F6EB0AE6BB3DG3E28305B5D6FA77ADC7972FD0E3CB4
	324522DC260B2349F72D3BCB703C35005F9C9535654FBB8E43F3C89A372A8A0FA6D6D6GA97A830EE5DD0715DB3F5377696E5C37FE4AF55B4EFB9D2F47CBD89CEC0F72FA1CE76317B3FC41D49C4F637200D744E5B9504CA9D127C9A92B53B05E86D0BA8D398B20836023E9214B93C578CDE65EBABD28EADEA896F2C2FA6772D51C7CF45FA2FE30C3787C6ADB2EA1EBAE7ACB5E4EB4FEE1CEF3FFD8EABDF3878FB0BC176FD2974A379EE8B9G8531EC67CBG8DG5DB1A15F45C5F5DCBEF4870FE9CAA3E32C739242
	8D85E8B80E46644FE5DB581CA85B19585CDA2F45BEDE7B390D8E326F8E974412ED67E6784531E45B5D5CA5ECF7855048B9E13B1BF0CB2BC66EABB9184E5FC102DE8859F7064721B79F14C66DB577166086F65D65E1E0519EAC411C94D082E6C01283D32CBD9D00094AA8177DF2AF31FDB8A3613D561B6EDE7AF900750D698BBD034BDFDC1A3E7FA9AFCD5D7FBCD71A3A7FF929F47E7E2626B47D3D75F0E9623D155B7D07E5E89B9F7841820095GEBGB61C937E7C7D527D5C1FD378725B3ED15A2E322FC526B87F
	8AD7FAD92FF5FD72B726AC975ECD06D553D403383F5D773950DE262AB1A4475D6CC29D690151B113CF35703739B0074B544F503C7FDE4E6AC8AE48357D1C6849E7BAC51FDEBAA3FA6E435DF17FEBCAF83F2D6B0B7745CDDF6899DCB7F71525374FCF4BBE59BE8F16317BA46AB52AD10E4BA943BED8757B8DEA7B412F4A4AEDDC0F512F9A810CFA5EAF2CE2779CBF5C33F96F7AE3E61B8630C1ABG6F05AF6B943D298C3885G99G6BGF2E6C4EF5A0D7F75E175A0FAA77A691C9E24938EB8345F87B9C79D5DDFD7
	0DCDC8D32D68ABEE6D943D9919953D59EFBB13FB335B6674E6AFF6A577E638BECEBF59B5FFEFB65B155C1B09755C7E6BBA4F373E717F7BF92271901C137598A9B4F4D271719E4C73751F5D1F75E1C72503B8FA08F30CE240741042F040E70EC5B4606283004546D9B2036AF82D9C81F6F24D51640F2EBE766B8D8F8D99794459C376207186284E72B975F5C82678B85BC90AB5B5GC44077108203BD8A8DB3AAD3AF29743F0B9FE88C137ABCA20399A6596D45136AC3E6C02E6F160CB193BD6E8DAFA9D070B3B229
	60E4D9D2C66BF959F88339A6F91E0F10DF2688827C6EE27C62AEEEFB17DD37169D5C577B481AFC6268A4A532AA79BA2591A8E6BD24589C5329B1264A3E08764F93EC2322B07A84768679305D0B3678A82CFB906242E6FAA2F265A2D72E2B935CA32213FAB6530489EEFB062FFDCC5261A82A771878D9DC55EBEC5A17DEB6DF7DC95773E79CA44F9292D5DF9851CC62744325D00E428B77DBE614E2937E6912E278D5BD881CD5A7F1A6C876816ECB6FE8386C5BCF7E179D90DFEA12D738AAE1E97DA8D503759E3FBF
	20D0FB8350ABEC3F575E9E67381366D4C4166C6E3D6FEF4360879DA4BF585040FE9B596F917CEDE418C606E9BC824A5DCA667DCB26C7044C4CF9001FB2468F1A5F5D8996B49FF86C0EEB9EEC5D7ABB102E139432CFB4E00472782CD32160B055AF4A3AEC6F4E25B9DFC39BE968AA915C579561BDE16DEC62DDE17B05D5E96F27D58BF96FD537B0792E7057A67417B140DF30D0544BD3CDFFA769FB270120A44B26A4CFF8949F76D1EDD11346EBF77EB15C5F90F3ACA227159CC33E4793567C8FD0CB878860792823
	CE8CGGD49EGGD0CB818294G94G88G88GD0F954AC60792823CE8CGGD49EGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG088CGGGG
**end of data**/
}
}
