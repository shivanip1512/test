package com.cannontech.loadcontrol.gui.manualentry;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.table.TableColumn;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.CheckBoxTableRenderer;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.message.server.ServerResponseMsg;

/**
 * Insert the type's description here.
 * Creation date: (6/1/2004 12:44:50 PM)
 * @author: 
 */
public class ConstraintResponsePanel extends DataInputPanel
{
	private javax.swing.JScrollPane ivjJScrollPaneTable = null;
	private javax.swing.JTable ivjJTableConstraints = null;
	
	private ConstraintTableModel constTableModel = null;

/**
 * ConstraintOverridePanel constructor comment.
 */
public ConstraintResponsePanel() {
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
	D0CB838494G88G88GC7E841B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E1359A8DF09455B9DAD42652A93458E225ADAAD52C31039515EAEDD3EB27DAD131C6254234F106D1460245160E0C4334AFF78987C8B04029D0C1838688A139904098C3E2F279B341C8B2648C8789C9766EB6F7CB76F60F3D8D39G55FE5FFB7B76B647DD22D698BE5E3E6F7DFD7F5F775EC11C6F4C484D36B989494EA5461F773389096DA6C45E3176F90EDBD360F4904B1F9DG9B499F3BF3205D82E3331A
	1C0EFC72D4519A6817C0FFD23D5371A6FCEFA68B5BBDBB61A345B9A5B0764413332B46BE676AAEFE0E037677752E07F68340FE40F9GE69460798D3DDB8CFC8C7A9B0CEF04EC9BA1EDBBF08E09EE3160979B73AAF730F6811D535B3B4D981F8AFD1C138EED460E781EEBA9F5D7D4D59A7D1EA933480ABF74587270AC4B7E9C8AF632350DC6DBC096D3C15849EFB736C0DB5DB1987C502F06DDFEC1D1D4C5977DA1172C8A81BFFCEB2A6C8A8EC92E30208C89328808112A2A86D5096A1AA0A9FA7BD9D153240058A1
	A822BCE7A7523FBB3A066ECD488568B3DA9F4F2B66BC8D4EA2D9B7362724F525C16336F763342ED8DCE01F77AE6EFD9515BD2E6B017ED4GF666E33D7C4C7CC227E3F2A162BE50F0EE8BFC7783448AB9ADB5FBB8AD5E73F7131E26037657D25012D948E8D8D59827A574F401FDAB3503B2AF6A32F2175351839081188AB087A09360ADEAFFAFF495C36B76698DEAB82CAA6EC824DE8EE81F2881D1066F2AAA98B1F047C43F2A8588B1ECACDA12049E8ECBF70D362F38CD9CA75CA6520F7D0CF82FEA3067A661ED4E
	133DB6321B6D615C9D670D71757089646B9060BBGC2GE9GB3GB2F6F33E8E9DBFB89EDF2DE0C71AA2481DC33E08206975C3129C90B5033FEF750E45DFB2E06B2E7B526B18DCE69D344AE5519DE9B6C79239AC1AA55A969972D8E1118BBBBBB365594EBD976B037A759E34435B289D9641F736E1238CFFB745DF3060190D57D2BC76A7415872BD5CEE3D9BB88FEDB9B7107C724B1272B06D7E33B6CF9123C7AECA546D4D5D98DB2681FE8E40BC0045GB9FB1D0E8200C3FB39FEBFBFDE76856D364322DB363652
	7B8634E28C82C4AB0489C1710BDD02A689BED90C92E30E5B0B347BC62DEB267E6407B3BE9295D193F4C9D5E0F7A1AC02A1C5591CF66FC61C2309F454A305C5B8A09CA1FC5F635E4D48379055BF0D8484DD0C4FE063FDDE1465D9218BF6048D60FB42DE14F9BD0DD99E780EGB0D9E576A06EC168E719F2F9463B895A0F4130C5392A2AD172A39902B6424F4FBC0131C4BC8BF132D10C08CAGBE2246584293EB63F299D0CD1A1F25F8741186A0BCEA621FBF311161E18F2B1479785F6838206B1A649B5245CED157
	A5A598DF7FB225A5BA4A7D22447071D3E8E3AB002F7545CE4736E24EDF5A494D967E5AD4F5F0A862C14D99F27AAB0D337BE1FECBB14F07F7E8189F65E2EE27AFFA391DB6665DC6CA1E7CBC291DA2CCAEE676B92BB851576E23FB464FE8523CDF526F3F884846396C0C5ECBF2065DE8195CBF6CC3394F07F317822C5F4765FE6F498266EF75C2D4649E562E014644C05C4A585C5B3BD35B585C6E64B6762B6E64B676C07758B6B42FBB198D3131A73AB13728430A45A697F4EF22BE2CA8A2A55ADCC2D83D712287E5
	A1A912FEDC8A84C53DD3BAA7C2BC5B0736F5027AE0A1FCD76E6336B47CAE372542F337100C3E1336E479EC5ACFC6EC1377B1DD4E5C9737A5C6531FFAD06622AC06C1D08D6A1022B37CDF28FF9BF8A33EBCFEAA795C27CF21BD8E0AA3463C0527D6930CA37210E872B6DBF895E52CC9E1D095BA2CD173597A2CB55F83C09F5607CB20DD8540563E1D0375EA0A324A301D61FE244DD962F494815415F05BB95E3B5562330651B428324CD4E06A2631AF9FF5A91E9992A1A423D3FFEC7273F15F260431AE03B7B65E59
	073C4916F860ADC15EA2E62D077902612E2238A69050BB39BC7FFE12EF2DD573E7A42FCB902617B07D4DAEC94CDD69826EBB9F70D9A66F0F76D96315BB9AD57D92CD8D06CE7E3197E5CF6B5B5342D9A1660A8E03FDD7D59D31542BF29F6E0B73D714705A315A0732AEA9F5BA9A81FACA79F97F6D3F58E6AE6FDF876D0024C521668908B1AE33097D48BB66A2834DEB3483D6D9E4E48DA435E9843D1449E0E2E922AC7E6EC3BAE682FE2EC91BDD58E81185541310FA0320A9CE4F6D021D5930513FD34865B6CCF88C
	BC5317D8634CAB0D57B6D9259757B92C1619CD653522B43146390762F3DC09B5CE3D0F57B8CE980B39F84EDD62C73E96C1FF290B73F54417E7612BC38577B0F9FA5907FE28EAD2D0D2B8EE9545C1B5CD83934BDB93290ED7411E79E5CE87435DCAF5F9887AEDE5BC4F5C65C3BF0F187D8C5A1FE476DBFC636588DC7BD5F385BBC398778C7BD75CFF03A937E5FE54C74C6C3FC47BA3465E1B490F7BD6D35B9DD107C058B97BD1DEF70D0AE333014F85GCB8DFE193F5DBE0CF1E2G0AB6F0334E0868A7D9F46D7D7D
	FCED9B747B8152GE6GE4G30B31FF3E2FCBA526907AB270C7EC9838D0B21AF792CE79715337A4383ED281C475067DC480B696BAEE1D82F2A2AD7B19E36B1C6183FAF2236007C2615F3FFDFC7E36BDC684F87D8D24E73CFECB73FAB265FF483E9CBCBCBFAEF4D7284ED64G13C36101382FDE003BA2BB33D960E77643783483085385E702CE9F0389AEE85978CD2346513777926B7802D163C8EF0D5DBA3E4EE79D4F352CE73CBD3C0B73366A6159C43ED3C9F9A74FB2F84AB9485A3A975232E7CF5EE1E7F28FB6
	EDA7C9E3ECDC13B04E6430177AB71EDFFD104BFEF198E52FC3FF92404C035C571E3DA8960D87AC66FCC6F5794B03AC66E41B782BA87EC18B1E45227CFE7E261089E31507F8AC3AD1C41B1B8E7D39G738116G2CF94F69A8FC0F472633017DE41CFB12C70D34C131A6DBCAF8465F4742D73B4B7E464175B6671E1F1239D73AEC2F2650DB65FBCCDFDE23DD30A4AD9BDB46DFC590791B88E3B3819E844882D881303E0273772BC089632FA9A069EEDD977CA11A79B8F3DD12B89CC72343B35EDCFE4EDB816CF58820
	25A29E97FBAAD265912F023A08D4A466919F4527D5A466119742BC0FCC063195955CF6A67423240C37864B779B7D1722469F9D3F4BAE6999CCAF69E26ABB409DE272BB40AF4464F700DF0BE35F81FEAF263E83BCAADA6FG4C2EAE04B06EF85FF7BAF400E9G69G996FF31B6A995849ECAA09BDB5412500D64182A6F9B63FB6181A57666078EF8D6DC12C4FFC2A2E2BE15C1F63BF8D223EF4B5C2118CF7B208B202F2A2243329863EBF08F1CB9687A81A558FCD2CD6813EC8A52FA90E4B08F3C23F5A447D1C62BA
	209FB0F15706F96DB131125BEC2F0235469C339F227DCC333FBC1C4A2EF04D57E33BEB15D4E77CBFB547E85B1D90CA2D4F2BC3636BF3EA08566020874FC8C4E438EBC3348E9C98080A265D7CA814EF62DA239DEAD4420B812BB5FA7CF85F02CB16AE798519A52AFAC16BD0E549BFC2AABFC09D8DD37D16822C2C64B50AA2230D072060BC27AA7A45AB0931761BD467B5G97BE60EB9F12D7D3BB5468A38E2B2932E4DE57E4403C85G4EAA385FD5B1FDFA2AE8ED9DAE98C326F546DCF64662967465A334F20A5FA6
	5117D99DF52DE22D232643DA2CEDB3204D2C62F4CE99AC0756124ADD16CC6E6AEABA66E9EDF03775F5B69CEBEFEB2BF79F6BF3379DF71F68A4BCBF4D98365EEFAA5749A96FB7CB8D5A49E156263FF5D9B62361FA39E28C9ADA5A0F351EECBF6A99CDC5E75F5126264626C61E9B5618392192760F9C66F562FD6BB0E7A77B4D43CF13B87D39E3C81760326111F4D9E4A5A42BDFBA427C2EB86BB02FDFD64AF8CE9674D78264D7F3B9BE92C6FDB708788E4EE5B3AC725C3D6D569B4936F70AED9BD34826285A30016A
	F8BD390170BA7B74799477DA2AF9C66B638A3F1BCF2C6634DEF986E94D007E820065553C5629B90DF35754C0EDD34363F42B348F5AAE6134D155E08920EAB4D28332C3D3A360BEA38CCBDF8B0C9832A1BCDEDD53D8B3BADE31F55B46DD77F6C21F2D7B142E6B2F79E2B1D007F92C261029FDCC2CC92C9307A8FEEACDE21D78AF195709B3E0AC67C3AE3B48A07AE7887A93GE681E4GE482AC85A035CE4716DAAE53EFCBC5E34814D9DA5CEF99FFCFA9E34965D0ED2ADA6A952A6B165AC47E6C747785CFEDA2FF13
	4EF07E7AE16C8113667A533827D99FB0D59BB13675B466FAFC4F1384D9BAC71FDEBAE122925CCEEDAFE3F39E9D17A5856291FD59A77CCEAED3DFAA2AF3BA6A6AF8DECC23F153837DD09D4FFBDA18183ABEEF794EC9198F3F8EE0E7EC3F24E7B0991E9FCC1D8FAF9F9CBF9FDEBD183C3E19B2F8F1FD733D4164754D74C1EBFD43F0579B7B22CD9E9542663EB78F22CE595B40A702A284E96D477437CC61F50DEE6A0FE8F11D2D35FCFF674C25575F2D17748C96673F91C6196A18AA1AD57AB645EA006B28FDCF83B9
	4C2C63B9617BF2AE1F8BEDE69D7EFF03AB28CC58BBC90E4A7373963753D1825086A083CC82E0736DD8E698E76BB27E9F85C59CAE60CF9EAE2421E8279B0CC45ED9D2D08EFF8F613D1F651F473AC73D437E204B46E3C0A2EF69EE96D7663A6379076515E2153FE5ACF573378C2F1A7816B1E554DB4672C862DB862E0F0F6FBC137816B1FA3D67CC62DB86DF4FE270BF29CC18FDFEB4320B240831F043B882F2EA771D967D3AC7881AF6D226FE593BB87B7FA50F1C58G4C26AFA339BAF14683F7C117244AB498C6B5
	12B7CAB53F3B7E3D67667D39ABEAA72BDB49568140F40A9A8D1EE220D15079AF134DE4ED84DFBA718712D6129325CDEED49F1145B6629CB806AF75D1DD8C38F9D80E6A6475B6BC298CCB173296A19A52B1CC9CEEA3B98A7E06CAB631E1D9D002E5ACC69F0E61AF27596EFEEA9B48D5B6503B19520B3B384CDDB65F7B5A2E9DC7964F3593DBB3D9077FB724D97003D4DB49DABD24095110AA87E2115ABA3A511AE1B490FC1448016D5EB259C7E6DD9E88E6A65103319FCE4ADB1257ECA3A42611B2BAD30789AEF3C6
	20A1A4E8F01428356A781B302A951AE3173D2D6FFE6423B7FBECA45BE0728C790C58E3911D2C9E96B14641712F51CD11E9D7B3502DBF336C219B96D6AFDF8A0B1AC9AEFD7D06237363330E8E05FD22F6E57AB55BDF1D1AF5B5D296C136223837C387FEB5C1098E28DA98B6AFC8600D8F19A43A3E7B507EEBEEF965A4A5B13ED4A76FB0E54052322028064B5C838310A24D8DC0B3307DF6F3FB1C63CA1833A9B66575A3CF7E5B871654C86C617D7B693F65745F8378EFB94514D3CC7981D04FF2B2661F4471986719
	1A9F6D3C0A3642512B870430A40F40C7A398309A0CA17EBFB19EEB76FEEE0DB5AB2F78A475BD20493887B4A77E4E71AC7D0D2E9A705EE6DE0FEC6DA99D433FBB4302AC434DEC502D8450576BC7F4B17E1671DC8F6E9F65F30CC163CD3C8769BEEDD973BFD0CB878878D41489928FGG20A6GGD0CB818294G94G88G88GC7E841B078D41489928FGG20A6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGCC8FGGGG
**end of data**/
}


/**
 * Return the JScrollPaneTable property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneTable() {
	if (ivjJScrollPaneTable == null) {
		try {
			ivjJScrollPaneTable = new javax.swing.JScrollPane();
			ivjJScrollPaneTable.setName("JScrollPaneTable");
			ivjJScrollPaneTable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneTable.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			getJScrollPaneTable().setViewportView(getJTableConstraints());
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

private ConstraintTableModel getTableModelCons()
{
	if( constTableModel == null )
		constTableModel = new ConstraintTableModel();
	
	return constTableModel;
}

/**
 * Return the JTableConstraints property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableConstraints() {
	if (ivjJTableConstraints == null) {
		try {
			ivjJTableConstraints = new javax.swing.JTable();
			ivjJTableConstraints.setName("JTableConstraints");
			getJScrollPaneTable().setColumnHeaderView(ivjJTableConstraints.getTableHeader());
			ivjJTableConstraints.setBounds(0, 0, 200, 200);
			// user code begin {1}

			ivjJTableConstraints.setModel( getTableModelCons() );
			ivjJTableConstraints.setFont( new Font("Dialog", Font.PLAIN, 12) );
			ivjJTableConstraints.setAutoResizeMode( javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS );
			ivjJTableConstraints.setShowGrid( false );
			ivjJTableConstraints.setIntercellSpacing( new Dimension(0,0) );
			ivjJTableConstraints.setDefaultRenderer(
				Object.class,
				new MultiLineConstraintRenderer() );

			//ivjJTableConstraints.sizeColumnsToFit( true );
			ivjJTableConstraints.setBackground( Color.BLACK );
			
			ivjJTableConstraints.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);					
			ivjJTableConstraints.setAutoCreateColumnsFromModel( true );
			ivjJTableConstraints.createDefaultColumnsFromModel();
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableConstraints;
}


/**
 * Insert the method's description here.
 * Creation date: (11/15/00 10:52:29 AM)
 */ 
private void initJTableCellComponents() 
{
	// Do any column specific initialization here
    getJTableConstraints().getColumnModel().getColumn(ConstraintTableModel.COL_OVERRIDE).setMaxWidth(50);
    getJTableConstraints().getColumnModel().getColumn(ConstraintTableModel.COL_PROGRAM_NAME).setPreferredWidth(150);
    getJTableConstraints().getColumnModel().getColumn(ConstraintTableModel.COL_VIOLATION).setPreferredWidth(600);

    
    TableColumn overColumn = getJTableConstraints().getColumnModel().getColumn(ConstraintTableModel.COL_OVERRIDE);

	// Create and add the column renderers	
	CheckBoxTableRenderer bxRender = new CheckBoxTableRenderer();
	bxRender.setVerticalAlignment(JCheckBox.NORTH);
	bxRender.setHorizontalAlignment(JCheckBox.CENTER);
	bxRender.setBackGroundColor( Color.BLACK );

	overColumn.setCellRenderer(bxRender);

	// Create and add the column CellEditors
	JCheckBox chkBox = new JCheckBox();
	chkBox.setVerticalAlignment(JCheckBox.NORTH);
	chkBox.setHorizontalAlignment(JCheckBox.CENTER);
	chkBox.setBackground( Color.BLACK );
	
	overColumn.setCellEditor( new javax.swing.DefaultCellEditor(chkBox) );
}


/**
 * Returns the programs to override
 * 
 * @return java.lang.Object
 * @param obj java.lang.Object
 */
public Object getValue(Object obj)
{
	ArrayList progList = new ArrayList( getTableModelCons().getRowCount() );
	for( int i = 0; i < getTableModelCons().getRowCount(); i++ )
	{
		ResponseProg prg = getTableModelCons().getRowAt( i );
		if( prg.getOverride().booleanValue() )
			progList.add( prg );
	}

	//ResponseProg[] progArr = new ResponseProg[ progList.size() ];
	return (ResponseProg[])progList.toArray( new ResponseProg[progList.size()] );
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception)
{
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	CTILogger.error("--------- UNCAUGHT EXCEPTION ---------", exception );
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ConstraintOverridePanel");
		setLayout(new java.awt.BorderLayout());
		setSize(414, 231);
		add(getJScrollPaneTable(), "Center");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
    
	initJTableCellComponents();

	// user code end
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 1:08:46 PM)
 * @param obj java.lang.Object
 */
public void setValue(Object obj) 
{
	ResponseProg[] respProgs = (ResponseProg[])obj;
	
	for( int i = 0; i < respProgs.length; i++ )
	{
		//we only care about the violators!!!!
		if( respProgs[i].getStatus() != ServerResponseMsg.STATUS_OK )
			getTableModelCons().addRow( respProgs[i] );
	}

}
}