package com.cannontech.loadcontrol.eexchange.views;

/**
 * Insert the type's description here.
 * Creation date: (8/2/2001 1:50:46 PM)
 * @author: 
 */
import com.cannontech.loadcontrol.eexchange.datamodels.HourTableModel;

public class JPanelHourOffer extends javax.swing.JPanel implements java.awt.event.MouseListener 
{
	private javax.swing.JScrollPane ivjJScrollPaneHour = null;
	private javax.swing.JTable ivjJTableHour = null;
/**
 * JPanelHourOffer constructor comment.
 */
public JPanelHourOffer() {
	super();
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 12:15:57 PM)
 */
public void clearTable() 
{
	getTableModel().clear();	
}
/**
 * connEtoC1:  (JTableHour.mouse.mousePressed(java.awt.event.MouseEvent) --> JPanelHourOffer.jTableHour_MousePressed(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jTableHour_MousePressed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JScrollPaneHour property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneHour() {
	if (ivjJScrollPaneHour == null) {
		try {
			ivjJScrollPaneHour = new javax.swing.JScrollPane();
			ivjJScrollPaneHour.setName("JScrollPaneHour");
			ivjJScrollPaneHour.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPaneHour.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			getJScrollPaneHour().setViewportView(getJTableHour());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneHour;
}
/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JTable getJTableHour() {
	if (ivjJTableHour == null) {
		try {
			ivjJTableHour = new javax.swing.JTable();
			ivjJTableHour.setName("JTableHour");
			getJScrollPaneHour().setColumnHeaderView(ivjJTableHour.getTableHeader());
			ivjJTableHour.setBounds(0, 0, 200, 200);
			// user code begin {1}

			ivjJTableHour.setFont( new java.awt.Font("Monospaced", java.awt.Font.BOLD, 11) );
			ivjJTableHour.getTableHeader().setFont( new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 11) );
			
			ivjJTableHour.setAutoCreateColumnsFromModel(true);
			ivjJTableHour.setModel( new com.cannontech.loadcontrol.eexchange.datamodels.HourTableModel() );
			ivjJTableHour.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjJTableHour.setDefaultRenderer( Object.class, new com.cannontech.loadcontrol.gui.LoadControlCellRenderer() );

			ivjJTableHour.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			ivjJTableHour.setGridColor( ivjJTableHour.getTableHeader().getBackground() );	

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableHour;
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2001 10:43:30 AM)
 * @return java.lang.Double[]
 */
public Integer[] getOfferPrices() 
{
	//Should be 24 Longs inited to null
	Integer[] ret = new Integer[getTableModel().getRowCount()];

	for( int i = 0; i < getTableModel().getRowCount(); i++ )
		ret[i] = new Integer( (int)(getTableModel().getRow(i).getOfferPrice() * 100) );
	
	return ret;
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 2:31:37 PM)
 * @return com.cannontech.loadcontrol.eexchange.datamodels.HourTableModel
 */
public HourTableModel getTableModel() 
{
	return (HourTableModel)getJTableHour().getModel();
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2001 10:43:30 AM)
 * @return java.lang.Double[]
 */
public Double[] getTarget() 
{
	//Should be 24 Doubles inited to null
	Double[] ret = new Double[getTableModel().getRowCount()];

	for( int i = 0; i < getTableModel().getRowCount(); i++ )
		ret[i] = new Double( getTableModel().getRow(i).getTarget() );
	
	return ret;
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
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJTableHour().addMouseListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("JPanelHourOffer");
		setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.X_AXIS));
		setSize(304, 310);
		add(getJScrollPaneHour(), getJScrollPaneHour().getName());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	initJTableCellComponents();
	
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/00 10:52:29 AM)
 */
private void initJTableCellComponents()
{
	// Do any column specific initialization here
	//javax.swing.table.TableColumn nameColumn = getJTableHour().getColumnModel().getColumn(0);
	javax.swing.table.TableColumn offer = getJTableHour().getColumnModel().getColumn(1);
	javax.swing.table.TableColumn target = getJTableHour().getColumnModel().getColumn(2);	

	javax.swing.JTextField f = new javax.swing.JTextField();
	//f.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0.0, 999999, 2) );
	//f.setBackground(getJTableHour().getBackground());
	f.setBackground(java.awt.Color.white);
	f.setForeground(java.awt.Color.red);
	
	javax.swing.JTextField f1 = new javax.swing.JTextField();
	//f1.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0.0, 999999, 5) );
	//f1.setBackground(getJTableHour().getBackground());
	f1.setBackground(java.awt.Color.white);
	f1.setForeground(java.awt.Color.red);
	
//((javax.swing.JTextField)offer.getCellEditor()).setBackground(java.awt.Color.cyan);
	offer.setCellEditor( new javax.swing.DefaultCellEditor(f) );
	target.setCellEditor( new javax.swing.DefaultCellEditor(f1) );

}
/**
 * Comment
 */
public void jTableHour_MousePressed(java.awt.event.MouseEvent event) 
{
	int rowLocation = getJTableHour().rowAtPoint( event.getPoint() );
	int colLocation = getJTableHour().columnAtPoint( event.getPoint() );
	
	getJTableHour().getSelectionModel().setSelectionInterval(
			 		rowLocation, rowLocation );

	getJTableHour().editCellAt(rowLocation, colLocation);

	return;
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseClicked(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseEntered(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseExited(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mousePressed(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTableHour()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseReleased(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD3F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D98DF094559D8F0ACA2BCE2126ED46F15AC811269DC62962CCE819966997D65A5299D4DA19164E50E9BAD25A99E106C246F9F739CBCE0998C93454020D85ABA00D28E9CDA3BA89C9EA82019CF7174B11CF173BCD3261EE77585DCBAEA4187E7F6F6D5B5BEC76029F1019DF5E3E7FFF5F577F7B6D112A3F67FBDD6EAAC2DCDEE27C3D6DA2A43F1A10AFAC18B342E9B9622AD2E279FB99709C794D998F34
	7301D7C67BAD41F2E8F7838A00066DD6E8ABC8DFF02FC1DFE350B9DCEEC2568D62BB6960BE03D687FDBFE0BEE051A0FBFF8534CF5115EF282FB35E6BDD10CF367E78D577B3B80FE5CE0EFDC3ECEC0351D613FBDCC5C338561DF41FB8A68D7D25GBCFC8971D656C2FB5CFFE1683D281228098A322C483A989D2E9145F4F4D81007441AD1C99C536A6BCF7A85D90C37AAA935E3F0D0D4E343B86F6F03BEBA8FA1C7217FA7CA2B6D43358A60F9A5E03DC95FC96935522A52F6C0CC6267FB775563467EA3937710E50F
	4AA54FE6B95F24444E35C84A1CAFB735236C2FD497AF7BD1669B016799D9D53A97F09C508B18847C8DF81564D04791E89B4F6BCDCAA221480D496409F8CCBD2D4444B8BC5757834720854428224688E1FA7EDA702843FEE6839BF77F479E47E4D2709657791240E232613ED36EB28719ECA8B8664E9DE132D8B2121189DBFBCB5635570CCC374969EBF7916B5A0BEED4CA3C8EEBAFFB7830FB23B14FD64B5A87AF202E8B282E2B6059E55887235FCB694396BA334FFACA47FE9AF89BAEF05B086C66FBE9775CCEAA
	5EE8F714C35E2AC6F7C31C6DA192376FC569473967466DFB9927745C38FDAF2EFE3E17C54073A5D615B27BBA92C0FF0DC3FF9EA09FD088D88F588A70495C46AEF63C7E91ED4CAF287A0914940F09AAE1E30F78DF00D6CC6B2A50A6EB3AA0C7453020CA4279382891631D377CA80373534655D15B0F429A6F0B32288A3A2448B03B1090F5D15558BB0D7E67709DD5245C101490E101C41270F95B7C15F8BEC153BB13B1C197B3EFB07ED9BF6AE4D488430CB081BCB339F45158F2884E5FG701BF2D8C367BBA52A90
	B66A6B5B9565C2AA99421310FC99656C8DE16C084173244CE59C2434CD50AFB667A9B93B47B2CF3394456D8B6AB877313B7BB0A6D7A85CE636C4384DB4BFF397B97A70C9C71BC19CD5182DB4ABF65FA924F3623ED83F21AFB20B4FD6A84E7E7471E24539D957A87994736FB6F575C0A76AAA8E7B1D87A8D0380C5FBBDB4DEC760420095CCABBD4B0B0B11611B85BE72A5FE348137527E8FF2542E5D5569F49A26F2B8136C6B5DD63534AA49B18CCEA83180744D1D1561B4524A84760C1B3F86F8450568CBF9DD44C
	B10D141E843FEE82C752CC7A7B8174BDC157D569FCCA9703222ECB72D0065FC15752D26713191040578A87B07EAAE36057667BBD81740928A24BA2D50EB9446077D11F9463E282B65D2424E41D5123C1545585F15C0889C3C1ACF2C605F88A638B2B89168A3B708C2A14G15E308A2F5C97457FD213D969995013E3136A987DEF5127B5E58816EFB87AF5DC9D68E3C62F64A9DF9F7B53BF3124CE7F213995FE3FB0A041C775ECFE303C15758DE0FFD00F23315A6062D6F8863BC4BE07E55000DC9EE6B3F8CA11F47
	9343481B14F81C095214710FBAABD0A762451488E1988357A9D3EEBF6D5CE363058D99B27EA31D3B281FE5E25E320BA843257D3C665B0EB285DA6DC5A64764C5A687F62EA78E232CE9DDB6A20C8A699AED8C6C0597E12C763A114A039631AA2F31723B704CF550778306D52E171FBF43EB2C5C8B79E463D7DF4CDAC36628C60D25E674D2FDBD2F311AA3B4BE83EF2B4A65F9B6EC0D730D1A26C4A51AE28CD910AE2B5E984D5B75B43403122A69ED60D8E9AE37DDDD38DF4CC1865948493F20B9994F6A53A04FG18
	CD6E68365AE49A68F7GD6825681CA681C8D837F02B62124B431067E772B22260931C3A8599AE1CC2F21BEDEF39AF9AD78F85854C3EC4F3F219D21AAC47B9AB8ED1D20BD6B8C6F9C8E93DECFAD2CECB07637666E5B497129CA37D3FDBB6F597D6E4D9A13F9151E117D643D25A51B7BF7163138F7F348DE53FD20E7F20F2E4F4CC32C56290A207C6669769A68A5CA4F536DB55062AEDE8395G2FBC4573F34E944ABB897DDBG0BGABG9BG45A9EE8F37F53DC52ED07F34419DC31505F8F0DA4063F5C8240445A8
	23FFBA6265B10A70787F6BFE3B9C760ECEBFFB2D2D4F467DE1463817EC7D08ED9C33031109AC7EC8AD0359E2E744EA0B49D1D6575C816DD2G7359B7FB78FDE975A87759B93DA8EBB286F7964031B1AE6BDF910C952D500F81A681F9009500750062B1AE6FC7A36FDEC95E54A6C3CA9350DAA1A6B55973860CE7B559C29AD37625E19FGC6CB04119602FEBC4DF37C0B91FA8FB67B87E99FFD0F7517F4D9EB99FC6F6A57GBF183646552E8518ECBFB3153D8618BF65DC83FCF14A398638E36AB9C607B92C15BF67
	AF1A1A3DC6D8B265D4A36C27E7FF7005E7695C9016B42A6CE6C1978C5E6F746709C32DF086AE87065CAA498B83CF996F3468CA539B265D8F356CF7367BE0D4050C095988784C36EF253981F533A44DED3B259BEDFB93740B81D5635CBE5E6E196DDEFCF4BCDB9C5BC6FD25E11C45AB17C9FF12525BADF49647EE6A655FBDA240DBB14EFD2608E4576B9F0933DE37914C199AE40128E857DDB159EB72B25E55260B8994331173DCB8DE9754A1F1066E4BDDFBCDDE36714F3B3259875361729056F2F1E1DCC969045C
	F2894F3F562C592A21DF8778G20DF32569A5F9C43DAE590AE0BB0DDB0A9C64970840E1D9B66E357417BC5G9F508F81DAA7F8EE7D36C86B2F0CB5E012B47C7373912E6325707E77A7266BF47DC4B69DAF27B633F9421E2B3EC769459376DCF52A1B672A72C92823A6F95CBA534363563AC9EEF7BBFB0999BFBCFD1967DBFBE73BFBDD1DF835781A2E41E431BD446DFDBB1C7958E57E1DAF500AB6B4530FCFA869F6E6B04C079BBAFD4678614B5C07E5FAE74C07FEA1E06DE5DED3267F49EB4AA50BEFA76DB9279C
	EB4A0D21B75DC517192E4BBF4C54B585299DE5EC4DEF04791A2D404F7990E96B43D516B9D03789B7A1B550B27E53DDD6BE7A8556EC997E71E9FC5C2FB0ED3CBC0D6F350CE7E7FB6895FE3662078AC87C7E7FE62D17D79BE75AEC347E7081F27033BB4B188DFE1D464403D360B3D35CAE8B4FA12D977AF3CDDAE5B737553CA9EE2B5F6D461C3A426CBFC87BF8E7E07D335DF6FB42F72F2E4D0E4CD863EA66D996E7449EB34E407E0BG3E9C08B300569CFE56A7E7543D9FED7F651D1FECDC4B479E47E2D4833DF38E
	675873D39B256BB9767C24F7F2FF4D815E269CDE7B7D560DB2E91D83359FE092108BD886D8BB076730C257B1F2053AAF24A45BA14945AD6586BB5FBD61CF76DD7A01B26E974B3E3510945E78E757AE626C971B66B0DB74DC475A225081CAE767CBFB70FC35407383F440AD00FCC061F57CFC44FD141D2FA5A6690D3AAEC407698D129FAEAC09E399B2A6AE1613D6C67E826D28286AD2942E98B4B51D90D43F92172263247CFA147BE354AF516757DC47F3594F4E615E0695D53A24487A4C1144987BB875DF2C397D
	504F2534A3536E21F630791F9840BB6FC973AE496B261BFBF87C5BF0BD0FFF4BFB6C71EF014B9A3F9E6F3147BF9C1F616F6F35473F6963BBFA6D710F0FE77671BF3C3E9A4F9D1667CB1D57BECF5EF6CD57E03635451D3D7E5B66F62E7FF63A1D6BFAD7496CF57BD3A5593F6DD514D83F6DB15F780E97F5D3B59772B7A0849886CC4E65FE716512FFB03FF07009165890561F784B014E571F6349FE56F91EAB7F5E304003F6FBDE51F5A50173F37A17BC28AFDDC9D222F14771200CD4E9E8D8E72F9A74059E347138
	B8C8494C9F8A4E61EF0F16F22446D2C75604DB82213626467601E0D320233D7DC4E3E020B1B8F026252539251945227CB16B6F22F5CF3F6D782D9111B757780D4CE8D75CF44070C9F777FFE659C3EBC7202D3B63CC48F997BCA62E6E33568A552FF99CEB056DFD2F3BB7996B7BEE4854C16BC60A0DEFBB73E73D93E537659FFA336B77A75EAB6B7791AF7DBE87FA890DA70D3B46E3DE3CCBA803031AE85A51C6EF05C9EB537C0AA6619D05596CD0994EF19C4E9539015B29D67A9A34E1E14448CC98469515DAAA90
	7D2A120490BD4E28746B29E17F1F73E1ACCD43BC3900C240DA40A6C031B9777252C3334C4DCADA6A872AA0716F4771325DD6BFE5CB9AE7534A5067A4990EA44425CB7473E590DE140798BF4F17DD46DF71DDD94685BEE79F3A5BB753076E73B97B50F21F55077E8FD0CB878854A66CB5768BGG48A0GGD0CB818294G94G88G88GD3F954AC54A66CB5768BGG48A0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGB08CGGGG
	
**end of data**/
}
}
