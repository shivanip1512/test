package com.cannontech.loadcontrol.eexchange.views;

/**
 * Insert the type's description here.
 * Creation date: (8/2/2001 11:43:30 AM)
 * @author: 
 */
import com.cannontech.loadcontrol.data.LMProgramEnergyExchange;
import com.cannontech.loadcontrol.eexchange.datamodels.CustomerHistoryTableModel;
import com.cannontech.loadcontrol.eexchange.datamodels.RevisionHistoryRowData;
import com.cannontech.loadcontrol.eexchange.datamodels.CustomerHistoryRowData;


public class JPanelCustomerResponses extends javax.swing.JPanel implements java.awt.event.ActionListener, java.awt.event.MouseListener {
	private JPanelHourOffer ivjJPanelHourOffer = null;
	private javax.swing.JButton ivjJButtonOk = null;
	private javax.swing.JPanel ivjJPanelOk = null;
	private java.awt.FlowLayout ivjJPanelOkFlowLayout = null;
	private javax.swing.JScrollPane ivjJScrollPaneCustomers = null;
	private javax.swing.JTable ivjJTableCustomers = null;
	private RevisionHistoryRowData revisionRowData = null;
	private javax.swing.JLabel ivjJLabelOfferIDLabel = null;
	private javax.swing.JLabel ivjJLabelOfferIDText = null;
/**
 * JPanelCreateOffer constructor comment.
 */
public JPanelCustomerResponses() {
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
	if (e.getSource() == getJButtonOk()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 1:45:36 PM)
 */
private void clearValues() 
{
	getCustomerTableModel().clear();
	getJTableCustomers().clearSelection();
	getJPanelHourOffer().clearTable();	
}
/**
 * connEtoC2:  (JButtonCancel.action.actionPerformed(java.awt.event.ActionEvent) --> JPanelCreateOffer.jButtonCancel_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonOk_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JTableRevisions.mouse.mousePressed(java.awt.event.MouseEvent) --> JPanelViewRevisions.JTableRevisions_MousePressed(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.JTableCustomers_MousePressed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2001 9:39:53 AM)
 */
//Please override me if you want me to do something when my owner
//    frame/dialog is closed!!!
public void exit() {}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 9:53:38 AM)
 * @return com.cannontech.loadcontrol.eexchange.datamodels.CustomerHistoryTableModel
 */
public CustomerHistoryTableModel getCustomerTableModel() 
{
	return (CustomerHistoryTableModel)getJTableCustomers().getModel();
}
/**
 * Return the JButtonCancel property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonOk() {
	if (ivjJButtonOk == null) {
		try {
			ivjJButtonOk = new javax.swing.JButton();
			ivjJButtonOk.setName("JButtonOk");
			ivjJButtonOk.setMnemonic('o');
			ivjJButtonOk.setText("Ok");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonOk;
}
/**
 * Return the JLabelOfferID property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOfferIDLabel() {
	if (ivjJLabelOfferIDLabel == null) {
		try {
			ivjJLabelOfferIDLabel = new javax.swing.JLabel();
			ivjJLabelOfferIDLabel.setName("JLabelOfferIDLabel");
			ivjJLabelOfferIDLabel.setText("OfferID:");
			ivjJLabelOfferIDLabel.setBounds(8, 13, 49, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOfferIDLabel;
}
/**
 * Return the JLabelOffer property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOfferIDText() {
	if (ivjJLabelOfferIDText == null) {
		try {
			ivjJLabelOfferIDText = new javax.swing.JLabel();
			ivjJLabelOfferIDText.setName("JLabelOfferIDText");
			ivjJLabelOfferIDText.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJLabelOfferIDText.setText("----");
			ivjJLabelOfferIDText.setBounds(77, 12, 182, 17);
			ivjJLabelOfferIDText.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOfferIDText;
}
/**
 * Return the JPanelHourOffer property value.
 * @return com.cannontech.loadcontrol.eexchange.views.JPanelHourOffer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private JPanelHourOffer getJPanelHourOffer() {
	if (ivjJPanelHourOffer == null) {
		try {
			ivjJPanelHourOffer = new com.cannontech.loadcontrol.eexchange.views.JPanelHourOffer();
			ivjJPanelHourOffer.setName("JPanelHourOffer");
			ivjJPanelHourOffer.setBounds(5, 193, 537, 287);
			// user code begin {1}

			ivjJPanelHourOffer.getTableModel().setMode(
				com.cannontech.loadcontrol.eexchange.datamodels.HourTableModel.MODE_KWH );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelHourOffer;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelOk() {
	if (ivjJPanelOk == null) {
		try {
			ivjJPanelOk = new javax.swing.JPanel();
			ivjJPanelOk.setName("JPanelOk");
			ivjJPanelOk.setLayout(getJPanelOkFlowLayout());
			ivjJPanelOk.setBounds(1, 479, 546, 34);
			getJPanelOk().add(getJButtonOk(), getJButtonOk().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelOk;
}
/**
 * Return the JPanelOkFlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanelOkFlowLayout() {
	java.awt.FlowLayout ivjJPanelOkFlowLayout = null;
	try {
		/* Create part */
		ivjJPanelOkFlowLayout = new java.awt.FlowLayout();
		ivjJPanelOkFlowLayout.setAlignment(java.awt.FlowLayout.CENTER);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanelOkFlowLayout;
}
/**
 * Return the JScrollPaneRevisions property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneCustomers() {
	if (ivjJScrollPaneCustomers == null) {
		try {
			ivjJScrollPaneCustomers = new javax.swing.JScrollPane();
			ivjJScrollPaneCustomers.setName("JScrollPaneCustomers");
			ivjJScrollPaneCustomers.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneCustomers.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPaneCustomers.setBounds(4, 61, 537, 120);
			getJScrollPaneCustomers().setViewportView(getJTableCustomers());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneCustomers;
}
/**
 * Return the JTableRevisions property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableCustomers() {
	if (ivjJTableCustomers == null) {
		try {
			ivjJTableCustomers = new javax.swing.JTable();
			ivjJTableCustomers.setName("JTableCustomers");
			getJScrollPaneCustomers().setColumnHeaderView(ivjJTableCustomers.getTableHeader());
			getJScrollPaneCustomers().getViewport().setBackingStoreEnabled(true);
			ivjJTableCustomers.setBounds(0, 0, 200, 200);
			// user code begin {1}

			ivjJTableCustomers.setModel( new CustomerHistoryTableModel() );
			ivjJTableCustomers.createDefaultColumnsFromModel();
			ivjJTableCustomers.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjJTableCustomers.setDefaultRenderer( Object.class, new com.cannontech.loadcontrol.gui.LoadControlCellRenderer() );
			
			ivjJTableCustomers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			ivjJTableCustomers.setGridColor( ivjJTableCustomers.getTableHeader().getBackground() );
			

			//set all the column widths
			getJTableCustomers().getColumnModel().getColumn(CustomerHistoryTableModel.CUSTOMER_NAME).setWidth(40);
			getJTableCustomers().getColumnModel().getColumn(CustomerHistoryTableModel.ACCEPT).setWidth(10);
			getJTableCustomers().getColumnModel().getColumn(CustomerHistoryTableModel.TOTAL).setWidth(10);
			getJTableCustomers().getColumnModel().getColumn(CustomerHistoryTableModel.ACCEPT_PERSON).setWidth(40);

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableCustomers;
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2001 2:17:05 PM)
 * @return com.cannontech.loadcontrol.eexchange.datamodels.RevisionHistoryRowData
 */
public com.cannontech.loadcontrol.eexchange.datamodels.RevisionHistoryRowData getRevisionRowData() {
	return revisionRowData;
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 12:10:28 PM)
 * @return com.cannontech.loadcontrol.eexchange.datamodels.CustomerHistoryRowData
 */
public CustomerHistoryRowData getSelectedCustomerHistory() 
{
	int row = getJTableCustomers().getSelectedRow();
	
	if( row >= 0 && row < getCustomerTableModel().getRowCount() )
		return getCustomerTableModel().getRow(row);
	else
		return null;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJButtonOk().addActionListener(this);
	getJTableCustomers().addMouseListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("JPanelCreateOffer");
		setPreferredSize(new java.awt.Dimension(547, 513));
		setLayout(null);
		setSize(547, 513);
		add(getJPanelOk(), getJPanelOk().getName());
		add(getJPanelHourOffer(), getJPanelHourOffer().getName());
		add(getJScrollPaneCustomers(), getJScrollPaneCustomers().getName());
		add(getJLabelOfferIDLabel(), getJLabelOfferIDLabel().getName());
		add(getJLabelOfferIDText(), getJLabelOfferIDText().getName());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	getJPanelHourOffer().getTableModel().setEditable( false );

	// user code end
}
/**
 * Comment
 */
public void jButtonOk_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	exit();

	return;
}
/**
 * Comment
 */
public void JTableCustomers_MousePressed(java.awt.event.MouseEvent mouseEvent) 
{

	if( getSelectedCustomerHistory() != null )
	{
		getJPanelHourOffer().getTableModel().clear();

		getJPanelHourOffer().getTableModel().setRowData(
			null, getSelectedCustomerHistory().getHrCommittedTotal() );
	}
	
	
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
	if (e.getSource() == getJTableCustomers()) 
		connEtoC3(e);
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
 * Insert the method's description here.
 * Creation date: (8/22/2001 2:17:05 PM)
 * @param newRevisionRowData com.cannontech.loadcontrol.eexchange.datamodels.RevisionHistoryRowData
 */
public void setRevisionRowData(com.cannontech.loadcontrol.eexchange.datamodels.RevisionHistoryRowData newRevisionRowData) 
{
	revisionRowData = newRevisionRowData;

	if( getRevisionRowData() != null )
	{
		//set the text fields above
		getJLabelOfferIDText().setText( revisionRowData.getOfferIDString() );

		//go get the data of the customer reply history from the database	
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;

		try
		{	
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
			stmt = conn.createStatement();
			getRevisionRowData().getHistoryProgram().setStatement(stmt);			

			com.cannontech.web.history.HEnergyExchangeCustomer[] custs = getRevisionRowData().getHistoryProgram().getEnergyExchangeCustomers();

			for( int i = 0; i < custs.length; i++ )
			{
				//custs[i].get
				com.cannontech.web.history.HEnergyExchangeCustomerReply reply = custs[i].getEnergyExchangeCustomerReply(
						getRevisionRowData().getOfferID(), (int)getRevisionRowData().getRevisionNumber() );

				//make sure this customer was involved in the notification
				if( reply != null )
				{
					CustomerHistoryRowData cstData = new CustomerHistoryRowData();

					//set our values that do not get displayed in the JTable
					cstData.setOfferID( getRevisionRowData().getOfferID() );
					cstData.setRevisionNumber( (int)getRevisionRowData().getRevisionNumber() );
					cstData.setCustomerID( (int)custs[i].getCustomerId() );
					
					//add our reply to the JTable
					cstData.setCustomerName( custs[i].getCustomerName() );
					cstData.setAccept( reply.getAcceptStatus() );
					cstData.setTotal( new Double(reply.getAmountCommitted()) );
					cstData.setAcceptPerson( reply.getNameOfAcceptPerson() );

					//get all of this replies hourly offers
					com.cannontech.web.history.HEnergyExchangeHourlyCustomer[] hrCust = reply.getEnergyExchangeHourlyCustomers(
							getRevisionRowData().getOfferID(), (int)getRevisionRowData().getRevisionNumber() );

					double[] hrAmount = new double[hrCust.length];
					for( int j = 0; j< hrCust.length; j++ )
						hrAmount[j] = hrCust[j].getAmountCommitted();

					cstData.setHrCommittedTotal( hrAmount );

					getCustomerTableModel().addRow( cstData );
				}				
									
					
			}
			
		}
		catch( java.sql.SQLException e )
		{ 
			e.printStackTrace(System.out); 
		}
		finally
		{
			try 
			{
				if (conn != null) conn.close();
				if (stmt != null) stmt.close();
			}
			catch (java.sql.SQLException se) 
			{
				se.printStackTrace();
			}
		}


	}
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G82F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DDB8DF4D447F546A07E3181DB98459589A13623B6EA22584AA9EEE8C295FAA2ECF2CCDB3B26AE47D6DA1D041E92079CE39FEC1453516A87B0E044DF8CB1189512E3F070229FDC049068EF7533C2C8C25AE00C4162E977C9DA74F65F326FC9DA89813DF7664D5B37CF3BA222269CAE6F4D3DB3F7E66E3D734D1DF98B59F3E8E1CE26E58FA119B9C47BF3B2131083E504BCF26140BC4E1BA0266492431FFC
	201DA421A89B1EF3C036ACBEA5B70F083BB7C379F1A8CBDFCB49FD8F5EF713D356A5257092251FD810591FDC33FC6CFEEA6AF9BF1BE879334AED70FC97A881F878DCAFE27FF265BE0D3FC263F34A3490323A9B6B4C28BC20714A205C86948714584D6AAF0367965A736472B22D5E15398B497AE7BF32ECC7BD861D1C8AFBD85BC66DE9A55F4BDC5703FDBDCD4709EDA6C0F9B190CEBE0BFC7ED185BCAB5A7AFA4E3AE42F4DA578FC32CF95DD3DB6D18C3AFA85DF0FE89B700803CAF9F9D31B6093255AFEC5153DE2
	20DDD47C32CF9195299775EF2C5CCC7591F2824A9BA8EFB09F7BCC0277D420B41DFF76B072ABBCA939CEA0494367F9060E8547FD696677C846CB3E2C9DD1669963E173CB740466995B7F76267D54A7939B5076EBC0366DFACA2E95488E64860A81FA1F4642747A0F61D95D25564ADE2F6C2B767BEBA4F7603C6C96A5F8AFAF870946EB97DDF240CD887377B31547A20CE7ACE26DFEBA6EF64CA689255C77096DCF1255CF1F33EC0AE0135589C516786B4C964957C3B6E1FD132AE8FD3FF0BDBCB6437B7E0C987BDE
	BBC54E4A0950F7720BC7ADEBB4BD99063E0F75212F7F0B464081F84F546203715F20FC3F014F62749665E3F98248527BF8ECDCFA170F25B97BB112D75C9C518EF1A95516C6090D611AE49E4BD3B46EE6C92C4F9CFDACFFC7798B8DFCB696BB65E3B999E47B3CA939AC3EE6BAF05D0EC0B98EA889A895E88DD0368F6A79F80C7DD37D71BFB3465A0400DA536F115CE2003036B39BFE87CFB128868407CFD1851FCB3CA884BCC217A4AAC42B935B00B6688AEB77B59A7BAE6823C1740981C17548BE50AEF8C5D58CA8
	2C4E22061DD8A7A0D22953639521832F1FF03DFF55309B1E122028FF723B85D58C55E072279A50A78342C550888A601D59E5F1935A2B8C665F86F4CD3743A70D286F1C98G78A8AFEF16653EFE3F93E7C212FCE867772A90BBD4F81F6563B6CEBE073C75D05EA6F3BD6E46DD86BDF60F8B07AF8406789A7BCDBEE233D566B1735AA59EB3766DCF10E3AFB6C50C9924AA19454AA5593CF6B6D21DB8AED61E14FFA94ADA324A1157527831A2BEEA9FD97FC77D6F6A3EBAFC9EFDB581469C8F34C43771B79A2DACE6EB
	84C564D15A9A00G935DA10B33F1AEBE4CB1BFC79F7BCFA86F855017A1F31BB59D3EE498FF4ED7B00751B6F35D57BE18ED66B9F0BF9287C41FEA977D224F8DAF0AA60BF7E04CE96B35DB565BAC22FCBF2C6FDAD8D00A4ECFF0609A94D4B560696AD7458ED1D5BD3E1E10BC09762574F779C350407B7A9B874ECF9E04752D57DF6240356112FDBE11BAC9EF2249FFC45726A809DE98F42D5C6FD399FFD99346F71FB824E1C3EA93A6BD8302540FB843F204A4E1AB4EA16071026B912A086A47F5EB2B5AE730D1A170
	B057E9046795BFDF030385FC8D968EFF13AC6D7C5092E98F09FB42EED962E76BE725BF348659181EB997F96CBF2698217195B6565263E8D7ADD5C10493DB3B3B291DD85CFF51023A56C19F59B7D2F28F5C60F1FF3A99659CDB34002F15A50919D337731173F9689771C63F88100CA0F6CE375D71733BCC320B1A9D197C64F93423E4403FF5B768DEF8186701A7205C8CA45DE036CC3C41EC416ABDD3406BD584A0F7828A86D83D64G2B47EC707AD1748B4D692E8B83C250268CC2EC71840E65EBF194F3F16DE484
	F8DEA61623EDCEA8B086A03742FD780F5BF9DE965B371024FD6B77D1734F91050DE321927221F5924F4B1EE845BE53C036CE61F6FF6A3CF1EF28D6945965215B12E6B3214568E3466BEEF9871E5D1E0022BAE0918639FD25969CAF6EDB9ADB5B47BFA8607BF8361A12DB8174020A7C244ABD06312B400B87DA8A14C575CDAEA841B5CA8340469EEDE220DB8EF8C57791342CCD98D4ED948FEC55D4DC076FC7399F565BB1762F57742B2A6CEB6D6B2CB629E80E2E6374510B4C760BF6D7E963DB715D47C8451D5D16
	C8B971349D87AD877A194D6D7DA15B0FBC151B35764B0D1B189D7EDD37439C28138A44563152E0DC47E4A0A537902891684AG3705237093F8FA65FEC53451FF5B8222220C3643F914F1B3E8F628AFAD47B5C9C31E9F9F144E73C6ADE6D3045400A5B846AF5073D8F5004DF15AC0E84F0D9F983D7F327CEE76E9FA8E99E0795DD61D3F0072D79A784CCEFDF49D50B2481C03BCA7B9F1816D34984AAFG2D854A8E022D006CC19E4F6BDB7008B9E64E670073D540A7C89DE1604EF32FD634B76031D67EEEAB5A1062
	B161F6A8ACB06773570261F39FB115D93B12D16DBEB015971A5AB17F6D68589F99CFD8E423BFE89C2DBAED5E8FB0EFDA8CBAD7GB15C79E5A5471D74A04717DB75C6FCD932234432B58A3E9C98E2632B98B264767A396F15F61A1703AC8EA889E88550BA20ED43A939C5435CC7E5EDE76E66A39AFACE3996F80D00573566FDD537691AFB74451BF775C55BF0A4DFDC3F99855B696AE2FE18E4B77A21F71879A1961E89435C8F5FE960395B52E10E7FEFB7E25CEF00F25E4D145CA3C0ACBFCF26FBC595142F683CBF
	1DC41E9F4AB1BA2F64947292203C74A64F7D661DC60CCD07728620BDA3A939A7001CA3DC7EADAAC75CD5C7986E4441F331AE3FD10EF99DEFE70E875499A9DFF3C6281B9E21AE6BE3866D835F37C45095AD175C9A0197A9BFE7B1F766C2745C51FEA1F26E58F4A1F26E58F6E1A763038E6349114BAFDD98BB373CF6A1D2EEF9100EFF796FF6D05D004A8ADD84F6C195B459AF553DA4C20E598207CBED6E3B499785DB34BAF52ADC0B7BA70B373C0A4D1AADD70D70F85BF89663AD6F96449AD055AD6E7335236ECAAE
	01CC3A457CB76156E8BF3275AFBAB116E2C11E84148A1486C4EE73F57F66053BE2F3DDF08CECDE539E5D073FEE0F6C43B75AB1D7D2647E00CBB47BA923FD1FAE8BA10CC3953DE8D2D6A727835B2BC220C79C6567ED9D7BF4D93476FBBB224582735721C2233FCAF51C6929BB9899E7BADC81480AB11354FD7BC3BB47F46BED6E5B9F5523EFAFC1598FB46DB677EDD243D877E08BEFC75B43FD8E5417FC1B6D5519BAFF1072179A78EC8FA78EFE5F3982E4C5F7F89E73DC955A539F9061D0G99FE07E7986731628E
	36C927B8978B6F8B757A52C9BC8FC942105C2FC23EB1816B7D7CA44757BCA89F813A82641F40F016596F7B03088F5D81418BE66B700BAE62FEG5BBACE7136AB21FEBAD0B6708B016C8F70BE6D9F60B8D8365E5A47F89F6F5F21739AA1E25829064926E7E35C9BBC23A7D9CC1EFF944FA5CD4DC2976843F68EBBFD67B170755C1DE63993A21970FD5DD1E35CCF929FE073DC8BCFB2116752FF6DB066527BA632BA953A7C4B16C8723609BCAE76D6186342BDB1948B41095162C226775A33A61A6F9707A8BFFE2279
	FEB133125FAFA602ACEF925F3B32589D941427693CE64A0B07F2224EFB2F066FF10BA7F1DC71BA2363026A0C0C8B434E4858BE41B9B636CFF3C6DB4F4C0ECB6D5C0E2BE1FC6BA6055BADBBA61A9DCB9A50AE7BE24CF62C207CA3B1E6BBAE2B67F6AC83D9FC8C374FCF9B38FD48E46EDB55C174715DB23C67BA225DF3FD95447AB8F4DF7BA81C0C5F0866FDCE88DFCBCDA72D70740903D67DC4E377405995AFCA0FFCFA6264B16B274525C7D95E3BF05078DD276C1D13916FC6E88EB71979EDC4FBAE19DA2079E1CB
	994739E4102D1D4C71276770074470DD47536535758FF8EDC2971C305C9E353C3C25DF121AB5D853F07CC259E6CDD761940E63F7287EABD0D601E6CD61B9FB708FBCE7CFFC72B152BC61DC44BB01B4E7096571A9EC4C29D3C2B9FB82646C2CCF79946FB3FBAA6045D4FAAEBBE5448A34EBD6B6A1B61C8F15BF9FA64775D0C00C724CF2239C475B90563EB4CC0E76BA2535E7F3FB6EC3BE370C6792087403D3D16FBBE22632B9A5E84F360B8524F04646CD4C87B93BCF10F1FDDB53011D5966019A1E2327CD657ED8
	D9CB5769B45853009A27F1FF3F5E9D65DCC0D11D69CBF7709C6C5AB4FE8E18C76FD7F05EF153789ABEC6F33515D0CE57F929E7783A5EBC1D2F6B7ECA3CBBBC26176F50B25EDF3172264A712CBD54B37E75ED9DD79F633DBFE7EB7A77F49F707AC4C89ABDAE16AB94D35E2BC07669FC5FF6D5617CD5GFB068BBF78822349B91D5E15066D21E6E2B6FE0966D171D0FF69F4EE7722BAFA1F8E658C1DB7330E7BA2EF8637F59906B8B92117EBE8996F88D979597A2F8287D1FFF4DF7C47D752C7B49F317C69957D2E2A
	8D4662860A81FA9CE8598C0ECD4FFFE144553C4772ADBCB7B46BCC576633EEC6881B58D9625BD7504605B361DCB2136F73AFFF7EC7F8DE942EEBA707BAGD2B9C00F8E40EC8B48FEB1208EB1AE3D79556C32236C5E3E999F3C47F697475D0E6D752FD32C3FB6531CE3E7D03EB4531CE3CF56771EA048D64F6477E42B2E220F2AE6C1DE8C24824D82CA84CA1D45EDD9F039085C65AC66147D4D10D4C906B4C84BA7CE5D5B37743F5F44F1BD79EF971145D35E4A0CF64F34FA1676BDDBFB2EF196D0BE1BDFDFA74E6F
	5803003FC092D08CD09CD05203FCFEFD170F3179E1B8D42B2A606A25B758FCF2978177C3ECBCA03039CD2999EBEE4B9E0C163F596D680BD58F1AFDF71E72539FB47B6EDFEA396F56014C799047097FF4C46AFF71C32C5F958FC53BC3F92C4558AECD2B37DEFBEE1E1D12FBE0F6F8DB06193595234FD717AB789AAF02B60DC0FE2018596CFE1E61402BE7F81DF860A7812D84DA8714B70763F14CFE74FB933FFF577BEC2C4267C772C7DADC24DEB56E7756BFE6C75C6FB7DCBDEEA91A4366601C93420B972EE7ECE2
	FADF33636F0C86G84BCAEC1E247319AA150A6CB9E5710F6465CC2319B6F52276963EC2843386A15831EE1488FC6376467D397ED0BF71729F3389D9655F1BBEC1B1B12EB852A823A86B4E1AE571F11CF4F0E61776FDA4E175D48732F05F3F97ED552E84E3F66E69A73A7F723B97F4276A179230D667CAB3CFDEA23B97F626D597A7A67EB6895360E5E7A82735808188B2B2889D21D562E6B22CBF58ABDFA1B44AB28D7BFA3B1A856E4CFDF413EBDBE0F6A91A44FB07D54548195FDBDAC0E5F422DC30BE94B29507B
	4C327BFFD6F97C3E7641667F732B514F2AEB2EC6BE2B3EF6B572D9F57D55316F99B32EC67F061D7525719BB673FB1A003ED985F1389EE85F43A93947002A9E6618BA774B4304E2EA84BC2DF377609D9A7ED2C6657D2B1D5167FA33736E3F2F0969446F0CDDB240079775F37E0CCE7417AA7BA91371E6F7220D821E1EDE15D5653F99684475A5095D144D7237857A9D421507755FC858C33156E1F8DFDDFB7F636E0D7B5A871BB3E942B9CF00794EF9186731E29337C33C4E7BBEBD0FAC0572CA1DB76F907252213C
	DE673DFDC83FE7F804735E4F27B99B144B0058BEB731026FF34D400BF9041F17CACB423EE961C13D3C3CDE1287CDE762E7AB7899EC49A37C8C36778C6ADC8F65ED31A93905317CAC15F316E3E9C5AC47525233E6AC2DA3C6AC944F1A317447D9C6797C33E6AC2D8BBB4BAEBFEB4652ACE244520FDD740E9846B387A899E885D09A5006D83E2EBE3CF604C44FEB19C1685A8A889E1F4A7694EBDDB1BC8DC7CD1B617E51F6312E5D69282DEE6E6C28EDEFEDEE2E29EE6F2CEE7E2C7A738E427760613A7F9923FDE3EB
	3B63F2EB0BB3DCC3C7E7CBDD1D3D4E4EFD13DA476D3DED9E37771BF5E6FB7FA64C5EC76A4D763E99A63FDCEF36F7E9183DE7551B6D3DC5EB4F565216467166269A768AE3FCDB906E0ED59B847ABB8940986710DF3B7B7F2D0039155C5D2D08BAA6DA3A72F41EC3E913958F6E00AC17C8564F28F63067A520A0D09C5062F9BC17F82FA97C0C7B93B794353359C893EF3F8A6A2F855A76A8E0B8D073233CFDEA8B366F06BC0765B42F5075AE01BC2857C9BC945E476BE2280F1DD65EC78254DF82349AA8836840FC5E
	3EA49FFDA17A50C0EEADF7F2646BEBF3BE475CE9236E1C7FBC3CFC749E5BBD9F562EED7E5D6FBBD88EBF1D620DFB3EB937FF18727D734D397D7BE7B8660D00ACCD1F6FBBE76FADB6CBDD51E3335CF577582CF5C55EC79D2E517BE80BAB72BE6AF49977D1B61F1F550DF7BE8CB7DE2E6131E00F63383139560C9B1596633AAF2FB5634624301CD62A9D9BB73EDEEB468DBCFB5A74B1ADEA62E39A5147343249BC2603442853F24EBC26774364A74E1947648A13F71FB30FE927A6E76B6EA49E1978B726C3787F9F42
	6E356BE1919FB9DC8D4E168E9F3396435F93C775791C347279A46B73912D6679045F5B4E371A6733BFCC3E5CEA1E0FB74C879B2D66797C05561E4D61DABE723A85C94138E2B943AF687E3EB60EE1870B2B59F844525E638C6F79E76EFFAED67E7F50472BE35EB1C4EF776D26FBEBD7E19DEFBB662F5FD6200F6118566343DC9F5E597F630935B5C10544DF119CA5C0F60D7E4ED27D503F3E1458E95FE3A19687496D9591CC9CA4E75063D6FB03FEC270BA50D22092CBEB1FCAB6EBDFBC4BD5127D897C81155B43C2
	7C9F3E7969AFD63CF2D149A2398E329FF72E8EB1C08F0F229BFF9C217D8A3E1EEC714B109E6197E58749D668A3C7D107A431196C69EE47DF05AA2A682E6647A5C5A5FB694F5E0E772B9E69F82320742A881B271BC9368FFF2FCFF6B131A478FA0E33B36AE99CBE492C3E06C1C9AD1589635DCD470BDAEC3A165DBF5C71E1FED37A62ACE229A76FC832602E97DC1055B9489635B7A0AA3D32648E7AAB2B28224D033888901032C15D7B7A74F132B67C942C8F3AAF38E05676B55B6B819B0381F21C56440D5A2657F0
	57768A81688AEC2A627FBF1083053A6C018F54BF7C2C613DAB961229CD728639CDB6857DAA496D963C9EE988DE6CE0DB39C7C149D6A88ED2774130F6504E5098B64853857565DFADDF74EF956B560232FA12C3FFA187C34A8B55EA6977F60901A909738EEE0DCD1B09A37663F43966AD30E579947CA9A6A8371A664CC57A50ED0FACAF1A7717EFDF26C38FB5D549A166A4E8FA3CC7143D472B3B3BBDBED1D7G9E837587F575D847E62A33ABB8F7EF530B7F5D8591E6A7D95E22A27AEFB17D37847FAD261CE24AA9
	AE81372DA3E37EB14B03FC4EB4ACE9E19746D0905FF684797D6851BB46FB738D1377C57D9E39F9814BDD8EACB05F1BFF2398C369927055857CFC716BA23C6F0AF607536195A4C995243EEA1F9B6FF3EA06D4B1F40770F8B16AD7F89DCD48E45FA946F1DFB73479DFD0CB8788CFAED3E80E94GGC4B8GGD0CB818294G94G88G88G82F954ACCFAED3E80E94GGC4B8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG4894GGGG
	
**end of data**/
}
}
