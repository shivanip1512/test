package com.cannontech.dbeditor.editor.capfeeder;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.data.capcontrol.CapControlFeeder;
import com.cannontech.database.db.capcontrol.CapBank;

public class CCFeederCapBankListEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemovePanelListener {
	private int rightListItemIndex = getCCCapBankListAddRemovePanel().rightListGetSelectedIndex();
	private boolean rightListDragging = false;
	private com.cannontech.common.gui.util.AddRemovePanel ivjCCCapBankListAddRemovePanel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CCFeederCapBankListEditorPanel() {
	super();
	initialize();
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getCCCapBankListAddRemovePanel()) 
		connEtoC4(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void ccFeederBankListAddRemovePanel_RightListMouse_mouseExited(java.util.EventObject newEvent) 
{
	rightListItemIndex = -1;
	rightListDragging = false;

	return;
}
/**
 * Comment
 */
public void ccFeederBankListAddRemovePanel_RightListMouse_mousePressed(java.util.EventObject newEvent) 
{
	rightListItemIndex = getCCCapBankListAddRemovePanel().rightListGetSelectedIndex();
	rightListDragging = true;

	return;
}
/**
 * Comment
 */
public void ccFeederBankListAddRemovePanel_RightListMouse_mouseReleased(java.util.EventObject newEvent) 
{
	int indexSelected = getCCCapBankListAddRemovePanel().rightListGetSelectedIndex();

	if ( rightListDragging &&  indexSelected != -1 && indexSelected != rightListItemIndex )
	{

		Object itemSelected = new Object();
		java.util.Vector destItems = new java.util.Vector( getCCCapBankListAddRemovePanel().rightListGetModel().getSize() + 1 );

		for( int i = 0; i < getCCCapBankListAddRemovePanel().rightListGetModel().getSize(); i++ )
			destItems.addElement( getCCCapBankListAddRemovePanel().rightListGetModel().getElementAt(i) );

		itemSelected = destItems.elementAt( rightListItemIndex );
		destItems.removeElementAt( rightListItemIndex );
		destItems.insertElementAt( itemSelected, indexSelected );
		getCCCapBankListAddRemovePanel().rightListSetListData(destItems);

		getCCCapBankListAddRemovePanel().revalidate();
		getCCCapBankListAddRemovePanel().repaint();

		// reset the values
		rightListItemIndex = -1;
		fireInputUpdate();
	}

	rightListDragging = false;

	return;
}
/**
 * Comment
 */
public void ccFeederListAddRemovePanel_RightListMouse_mouseExited(java.util.EventObject newEvent) 
{
	rightListItemIndex = -1;
	rightListDragging = false;

	return;
}
/**
 * connEtoC1:  (RepeatersAddRemovePanel.addRemovePanel.rightListMouse_mousePressed(java.util.EventObject) --> RepeaterSetupEditorPanel.repeatersAddRemovePanel_RightListMouse_mousePressed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.ccFeederBankListAddRemovePanel_RightListMouse_mousePressed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (RepeatersAddRemovePanel.addRemovePanel.rightListMouse_mouseReleased(java.util.EventObject) --> RepeaterSetupEditorPanel.repeatersAddRemovePanel_RightListMouse_mouseReleased(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.ccFeederBankListAddRemovePanel_RightListMouse_mouseReleased(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (AddRemovePanel.addRemovePanel.addButtonAction_actionPerformed(java.util.EventObject) --> RepeaterSetupEditorPanel.fireInputUpdate()V)
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
 * connEtoC5:  (AddRemovePanel.addRemovePanel.removeButtonAction_actionPerformed(java.util.EventObject) --> RepeaterSetupEditorPanel.fireInputUpdate()V)
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
 * connEtoC6:  (RepeatersAddRemovePanel.addRemovePanel.rightListMouse_mouseExited(java.util.EventObject) --> RepeaterSetupEditorPanel.repeatersAddRemovePanel_RightListMouse_mouseExited(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.ccFeederBankListAddRemovePanel_RightListMouse_mouseExited(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the AddRemovePanel property value.
 * @return com.cannontech.common.gui.util.AddRemovePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.AddRemovePanel getCCCapBankListAddRemovePanel() {
	if (ivjCCCapBankListAddRemovePanel == null) {
		try {
			ivjCCCapBankListAddRemovePanel = new com.cannontech.common.gui.util.AddRemovePanel();
			ivjCCCapBankListAddRemovePanel.setName("CCCapBankListAddRemovePanel");
			// user code begin {1}
			
			ivjCCCapBankListAddRemovePanel.setMode(com.cannontech.common.gui.util.AddRemovePanel.TRANSFER_MODE);
			ivjCCCapBankListAddRemovePanel.leftListLabelSetText("Banks Available");
			ivjCCCapBankListAddRemovePanel.rightListLabelSetText("Banks Assigned");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCCCapBankListAddRemovePanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	CapControlFeeder ccFeeder = (com.cannontech.database.data.capcontrol.CapControlFeeder)val;
	
	java.util.Vector ccBankVector = new java.util.Vector( getCCCapBankListAddRemovePanel().rightListGetModel().getSize() );
	Integer deviceID = null;

	for( int i = 0; i < getCCCapBankListAddRemovePanel().rightListGetModel().getSize(); i++ )
	{
		deviceID = new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)getCCCapBankListAddRemovePanel().rightListGetModel().getElementAt(i)).getYukonID());

		com.cannontech.database.db.capcontrol.CCFeederBankList ccBankList = new com.cannontech.database.db.capcontrol.CCFeederBankList(
																		  ccFeeder.getCapControlPAOID(),
																		  deviceID,
																		  new Integer(i+1) );
		
		ccBankVector.addElement(ccBankList);
	}

//	if( ccStrategyBankListVector.size() > 0 ) Dont know why this was here, anyone???
	ccFeeder.setCcBankListVector( ccBankVector );
	
	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) 
{
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getCCCapBankListAddRemovePanel().addAddRemovePanelListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CCSubBusFeederListEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(447, 311);

		java.awt.GridBagConstraints constraintsCCCapBankListAddRemovePanel = new java.awt.GridBagConstraints();
		constraintsCCCapBankListAddRemovePanel.gridx = 1; constraintsCCCapBankListAddRemovePanel.gridy = 1;
		constraintsCCCapBankListAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsCCCapBankListAddRemovePanel.weightx = 1.0;
		constraintsCCCapBankListAddRemovePanel.weighty = 1.0;
		constraintsCCCapBankListAddRemovePanel.ipadx = 185;
		constraintsCCCapBankListAddRemovePanel.ipady = 192;
		constraintsCCCapBankListAddRemovePanel.insets = new java.awt.Insets(4, 5, 4, 6);
		add(getCCCapBankListAddRemovePanel(), constraintsCCCapBankListAddRemovePanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/00 5:00:18 PM)
 */
public void initLeftListBanks() 
{
	java.util.Vector availableBanks = new java.util.Vector();

	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	
	synchronized( cache )
	{
		CapBank[] unassignedBanks = 
				CapBank.getUnassignedCapBanksList();
		
		java.util.List paoOBjects = cache.getAllYukonPAObjects();
		com.cannontech.database.data.lite.LiteYukonPAObject litePAO = null;

		for(int i = 0; i < paoOBjects.size(); i++)
		{
			litePAO = (com.cannontech.database.data.lite.LiteYukonPAObject)paoOBjects.get(i);
			
			if( litePAO.getType() == com.cannontech.database.data.pao.PAOGroups.CAPBANK )
			{
				for( int j = 0; j < unassignedBanks.length; j++ )  // see if this capbank has no strategy
				{				
					CapBank capBank = unassignedBanks[j];

					if( capBank.getDeviceID().intValue() == litePAO.getYukonID() )
					{
						availableBanks.addElement(litePAO);
						break;
					}
				}
			}
			
		}
	}

	getCCCapBankListAddRemovePanel().leftListSetListData( availableBanks );
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 9:11:36 AM)
 * @return boolean
 */
public boolean isInputValid() 
{
	return true;
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void leftListListSelection_valueChanged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		CCFeederCapBankListEditorPanel aCCFeederCapBankListEditorPanel;
		aCCFeederCapBankListEditorPanel = new CCFeederCapBankListEditorPanel();
		frame.setContentPane(aCCFeederCapBankListEditorPanel);
		frame.setSize(aCCFeederCapBankListEditorPanel.getSize());
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
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getCCCapBankListAddRemovePanel()) 
		connEtoC5(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void repeatersAddRemovePanel_RightListMouse_mouseExited(java.util.EventObject newEvent) {
	rightListItemIndex = -1;
	rightListDragging = false;

	return;
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListListSelection_valueChanged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseClicked(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseEntered(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseExited(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getCCCapBankListAddRemovePanel()) 
		connEtoC6(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mousePressed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getCCCapBankListAddRemovePanel()) 
		connEtoC1(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseReleased(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getCCCapBankListAddRemovePanel()) 
		connEtoC2(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouseMotion_mouseDragged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) 
{
	CapControlFeeder ccFeeder = (com.cannontech.database.data.capcontrol.CapControlFeeder)val;
	java.util.Vector ccBankListVector = ccFeeder.getCcBankListVector();
	
	initLeftListBanks();
	java.util.Vector assignedBanks = new java.util.Vector();

	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List allPAOs = cache.getAllYukonPAObjects();
		com.cannontech.database.data.lite.LiteYukonPAObject litePAO = null;
		int bankID = 0;
		
		for( int i = 0; i < ccBankListVector.size(); i++)
		{
			for( int j = 0; j < allPAOs.size(); j++)
			{
				litePAO = (com.cannontech.database.data.lite.LiteYukonPAObject)allPAOs.get(j);
				
				if( litePAO.getType() == com.cannontech.database.data.pao.PAOGroups.CAPBANK )
				{
					bankID = ((com.cannontech.database.db.capcontrol.CCFeederBankList)ccBankListVector.get(i)).getDeviceID().intValue();
					if( bankID == litePAO.getYukonID() )
					{
						assignedBanks.addElement(litePAO);
						break;
					}
				}
			}
		}
	}

	getCCCapBankListAddRemovePanel().rightListSetListData( assignedBanks );
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GA6F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DA8DF0D455958E72AB63CC31C3E79C5419945126E3C6210D8E532695D3F428E0457AC75B4C88ADE3E30B15CEF1C421531B4D9F0810DFC9C02CA88192C9578428068DA849A6CAA04896E20CD3277812FD3BF9495BFD1B775E66075F1EF3FF5E3EEC5EA618414CFC3CF74F39771EFB4EBD675CF35FC24A5ECE4E4BF61591121DC7785F075904D46EA5647668F7778ADA121C11CFECFF6FG0A49FAA3971E
	3300376C460C7CA2C214CD501E87ED955A6570DECA721474BD7012C04EEC60ADBA1FD7B8361C6FEC97F28AE87B0C7FF5F8EE8324G8D1F6BE4149F74D7F07AB2CE9748F69132BA00FDEE706F6234FAE87BG490034G6B3F8C1E1B2964291EFA5E6F2B1B13493AC7763B3662BC36B985EA02EC2C0FBFF713FB335787D1565DF41DB8E686349781D0799C72F2FFB3BC0F7A7A029FF5EBA1F737948EEBE1D36E6EF14BFE4554F420C482326C17F50F476B7D043EF925C813946EEBD38C3305F672C9E1D9B5FBD04E5F
	7C1B683CB0A7340902341736216C0550DE8E4832686905C8EF06772FG26A274BD367FA8572773623DA46D19F0CEE182FDEFD2181EE9CACC5F5951D78BF6503DF92C9C77E0AD704AFAB3722BGEDG95F09360CD6A93CFEFFF8F1E0DDD26D78B0534F0E3A452247A75531ADFD6615D63818E271D153BB55DCF885B77857E9A07750C85B66E01EFBC0E59A4651070015433F7114C9F1FF295B858A4B32556B52F1759A23DB7E693A6FBFDC259193DA3FDF4246CAF08DD763CE9DACE1E036C34272ADDEB79BC39FDB1
	59077BF02F953A5715701E4D7D03518DCA9F3251193FEE20BE036D9940DB53A7FC23F31BD8CBDB6E9CD2D45766E807DBB29ADDBE152D21C70DDFCBED914EBDDBE5B237D8EB79085253ECF436168C3AC6ECBF893C7AD0C6BE732F95BBB0FEEF0376C240F2C096E0D3B8A33F92D09F96BE76F6797B5768E3BEC9B71B220A8AC1C6585895655B61A98F193A549AB6CCA95CADF7C83AA2F52932C1F81FD565E8032E91631E233E5F8DB2BE154332AE190A9606592510EC4A3A417A3CD8DE0CFDF419F25B15108C82C291
	A266FD393C941E2AE4181FC77C12A947FAB07EBF4AF1CF86248E1891A600F7E61746FDE82FCE50BF82C8326CF0218267BBA56B10ADBC1EB6CD6B0BC65AD19332BC0CF6AE14B1F74C067754303071317DC8AB53B272EBB4B14F020A925BBC4DCAB7AEDF5207C50CE90518230FEB42E77E52A9FC26F96B1D246629130EBE03D0B566ABD33478589926F3623AD8BB3B30739B4631930EEB4EF147E46C3CAEB2F2925019FD6F2E423DCA05F5AC81E4D9B6DED3311B79EC13E44842CB4F6860E032BFE6F1364E8F0A4A78
	1AB7F35FF90B478B63B7D3BE09C08E8678GC3006408306512D1FA0A39360C231B10B5FE5EDDB5CA865AA56F9AE408350C370ED2727B9DF8CE4983F258EC16A3F2588FAF8667BD3F836760F19C502CB1AFD2FA84625E8B01E6D87417F6E0ECCA2629ABDDD1D3BEA71B26928E4678FF27320CE8D7A416B2042C5C1D2817B688F1EF755F3C93E326DB8B07E53AF956904E7F14462C2C4AA1D834D70B06CDC6BF3D8F7D3ECF9E66B963BF7B30A89A1054A8669FD6C34C17500691DD8901CBE08AA3B57D984F7B648A1B
	0D1640FEE3ADC400D756AFE2F3F00F084D4A8B5F077AEB2F4B69EC39654EE617594FE2E5C6FFACB6591A1A76BB2F7D844D9D1CEE64D05AF2AF7A0B57FBAE5A5594B5D8BD93D748D831313A9A67CD81F96900A77BC5ECAC5E0FFC11FFF8D0F8B5D5E52635EC3E202A8877C86E0F4A1036B1511D326C38302AA40E5741ED4A780B2A50262AADC75E2623CD070BC44D388656138B34AA1D5975124E6C427AB5950BFED9BA6BC78C27FEDBCAC43FF60375D3F93FF4435E6F892B5FAE1375BBEC32FED3CCFB3FDBCBAD39
	3C9F0932FEEDD1560F79CC41F92CC51C6A4EE0D4F1C7CD85B33BE4C230C422262D3E1CC54F881CA3B5EA5511BD38CF2B213D91D0B6A0FC6B712D220E1C5D17CCD65E31B3F15DBC406B660118EF6D1EAC6AC8778114B98BF8498342879A2A6DE7D923E1E85D8ABDC6797E7D295A6EFBBC562BDF03E7C0518D3395124310956B5538DEBCE7B919579DF3E9BD023A26015CB5G96D77D0ABD2EAA8759F97995BCA3G66671B0BDBB0FF787DE76510B6A0D38B3A215994B5CDA8C128231E176843A76B81CD8F497E2ADE
	E9C02236F737E092BB53558B9EDDED6D59626A9236FE196E8AAB8A2A2ABBC41DB83774B8DF5332FB661023D7CBDDCEF57B0C4237DCB386191D678F466CFD69C779B9EB7E372100695ED7ACF45F88FD8E8F893DFEDDFCE234DEBAFD1F00EACC566192A1CB85B973ACD93716B4BA48D202BDA6E60C53DA54104F0770DF1FAE9B467876EBE9E9479C5C4D320E38C2B50E187F7C59B166EFF39450D05D417458E8691189BA148D8BBD3ACB1B2ECD0F33602F52D80AB0BD1EAE7BFCE2FAD873A7D444727D4525C217B650
	E30A254B4DEF9C3FB6DDDA0694B321A6CC0F474F1F1B189EFC6E44DA301A63FBF27CFDABFDB8D66FAD9F9EDD7B717B09C27359F07CDD49C3696B06636FCA99BD622E14FBA1A33F6702281DFFD603F9E2993433G64E2C6FE85209E50F9D164314B6F9DA2635CCDDAA1AF6BE1C9BDB7225860396B5081148B7581EFFFF8GEDCB6B83A2EA2F1422F8BB1897C76ABEAB2E4D465DBFEA1CBB2E1D96B70E6DEBC76E78E74B48DDE6E74B8F95716DE259C5F136B451FBE86E250C7CDD00431704ED5FF463B957866D9EC0
	9260B640A2C0A600DC9676FD704031716CCBFD27DD73826D841C775E789A4D32C7FF71446CF8F55438F8BB6E3E6CB44ED3B2B1F9A7C60D7BE7DC3B5ED11E2BF4E2720AC60D0B1767F314774884652D98D7DECF1CBCF63FCD2535CB9270E683986D46A05296E27B32388FFD20E04D3D4EEA9F27ED2C2578FD2967FA5C8B57DD9E6B6E396A3A48C8F477E43171F0CD627B5223B54E7725A7EA1C6FCB3F29A9E6F418437E95457AC6D2B376FD6A799A277B54DBD407DFEEAF24F34399EC50E0454214737EE83EC19C6E
	D51FC9A1196BDFCA1EAD5A4C7B34181A7730E563FB14313E856E3A1268FC381F66245AAB6C9C4836688FD07AD19B1D1D8FA73609EF3FA70017FAC564263D6E44762FF1BB5B7F209BEBE8C30B6A5DF23C0D0F38ABACDEAC0B351AF2884D416FEAF43CA969C1F9549E3552710C17E87CD9F722FDE436CEA9365B7A5F963D3F44CE2F3368C7CA6D740396BD3B4CCE7F40227FC2467B2FAA8DEBD11310CBD7513E3F356E39C5502E8278G7E2B2CAEE7E75202C13C4F85F4A9844BBD97113BC9E7920ECD8E0A310B217F
	8A40FAC085706A13C44D6359013AFB3D366F6EA30FAF1EDF9ED2C4AEC9CE9239A29D27667E76106D7D33ED57A33E93014988FE2BB218EF3B82423757024ED513043D6B3E46731956G34CA1386CD0F67D3DD71B7C941B63AFB6C4CBF99584447FFBDC91C79C3F44EDB20BD9F30E41238CF8E6D937749543B66103624D30E7749156D87DDABA7716F60B7446EB7A951D78B184C0703C266F1608F5D0034F541B25B9C386F1A0B1D530C3F2F474E4798407C9363F70E6063FA2F665879B3C77051C68778F8265B523D
	C2370DCBD308FA5F1104F765C5DC27557C696B58C3AAE7EEA870CDC6B919CA7398FB169307DDB026967086EFB7433B81C2A7E58B9F5A9418586FAC5BA7B86E4CB79E47F26F49A075194971B9799C254F1B9C1F132FD8FEBB9FF8E5D3C4DD79EEAD6603A1E84F8624829683D68348D41137DFF25712F1EA4AF6AD528609DD359D05CC3F2541097D7E74E001700F341F4CA58B273D123DA501FF14CDE5FEF118BF335A77D0BA536F3EBA544F8F3CA440BCC0BAE0B9A04B526F587BB5CCBF7CA857E81AD2F78F7D0AA2
	146BD06441989913695BF49CA11B0878DB7B3193ED63D47CCD77857C644875DA753A50EB575D371266E7DE495E13C02F635318BE332645E2BA2BF7E381332BAFC8AC9F7A5276BEA5706D67595B3FD599ECCF8E54A62E958E56BA578A75354E355AC735E357E21F54A67E36FD3A567EED1B79D42E87F35B9258175500CD53612E86281DAE7C69F7356FB2FFF2702596FF90EB957CE54D9472FFDE17D857076A467FBD6ED7F5781D2FCBB3CDAD04738B7A13F538DF269621C4DE3756210D68878C5615531F2DC33FD5
	65GA5B3DFFD6C3F767F87D0B4679D1748A971FB56B61D79E86774182FB2DB7D20817373CDC0CF81AC832C862C87944D48482F8454835AGFE40A540AC40BC40C24092C0A6E06D8CE15B03C7707BD607544B33907B0ECE2D8BC4FE60D2836B1202A6667A3D6EC9EC63D24F78B65E65215FD741B66D4391DE9B3E63415ACC8B848C595A4BFD1EA20B56EA78B4C3411A1259E2ABDE4DF93F051E3D0974GAFB989A5847B3A53AE852DB1BFEDC82CC3C64378BAACEDF07613479BC67B49538D4EFE1259E07793FE57E9
	409889026DBB2DFE4FD934AFAD5A1F691CC87BC2711BBD967D2FF4DDC8BFA123C06B77E03A2EC1CA6B64340DB65A171C16FD947781D46E51F4B1B67FA85EE1830A2A3276EB340F9206ED316CD1F8945728C4A45B3A4BAC9A2E1B7DBF1E397AD9F8263EC0728C1D940A724E5DCAA7DBB9F048137307BBF2DCA4371564B3633612DC34D0AB492386ECA5B9E05CA17C219B9705827259BB0B336A2B76B8DBBF354271B7A9C451CC96DF15B363636CC5937765EB20DF1AA9E265E373DFE378583910242A262476B5067D
	68EFCD43269C4B096BCC1C5F90FDB81371B618386EDE5B187F83D0CB8788B375D066B88DGGCCA5GGD0CB818294G94G88G88GA6F954ACB375D066B88DGGCCA5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGF28DGGGG
**end of data**/
}
}
