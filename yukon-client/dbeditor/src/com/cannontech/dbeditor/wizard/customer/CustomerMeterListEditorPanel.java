package com.cannontech.dbeditor.wizard.customer;

import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.yukon.IDatabaseCache;

/**
 * This type was created in VisualAge.
 */
public class CustomerMeterListEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemovePanelListener {
	private int rightListItemIndex = getMeterListAddRemovePanel().rightListGetSelectedIndex();
	private boolean rightListDragging = false;
	private com.cannontech.common.gui.util.AddRemovePanel ivjMeterListAddRemovePanel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CustomerMeterListEditorPanel() {
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
	if (newEvent.getSource() == getMeterListAddRemovePanel()) 
		connEtoC4(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void ccStrategyBankListAddRemovePanel_RightListMouse_mouseExited(java.util.EventObject newEvent) {
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
		this.meterAddRemovePanel_RightListMouse_mousePressed(arg1);
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
		this.meterAddRemovePanel_RightListMouse_mouseReleased(arg1);
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
		this.meterAddRemovePanel_RightListMouse_mouseExited(arg1);
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
private com.cannontech.common.gui.util.AddRemovePanel getMeterListAddRemovePanel() {
	if (ivjMeterListAddRemovePanel == null) {
		try {
			ivjMeterListAddRemovePanel = new com.cannontech.common.gui.util.AddRemovePanel();
			ivjMeterListAddRemovePanel.setName("MeterListAddRemovePanel");
			// user code begin {1}
			
			ivjMeterListAddRemovePanel.setMode(com.cannontech.common.gui.util.AddRemovePanel.TRANSFER_MODE);
			ivjMeterListAddRemovePanel.leftListLabelSetText("Unowned Meters");
			ivjMeterListAddRemovePanel.rightListLabelSetText("Owned Meters");			
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMeterListAddRemovePanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	com.cannontech.database.data.customer.CICustomerBase customer = (com.cannontech.database.data.customer.CICustomerBase)val;
	customer.getDeviceVector().removeAllElements();

	for( int i = 0; i < getMeterListAddRemovePanel().rightListGetModel().getSize(); i++ )
	{	
		com.cannontech.database.db.customer.DeviceCustomerList deviceCustomerItem = new com.cannontech.database.db.customer.DeviceCustomerList();
		
		deviceCustomerItem.setCustomerID( customer.getCustomerID());
		deviceCustomerItem.setDeviceID(new Integer( 
				 ((com.cannontech.database.data.lite.LiteYukonPAObject)getMeterListAddRemovePanel().rightListGetModel().getElementAt(i)).getYukonID()) );
		
		customer.getDeviceVector().addElement( deviceCustomerItem);
	}
	
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
	getMeterListAddRemovePanel().addAddRemovePanelListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CustomerMeterListEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(447, 311);

		java.awt.GridBagConstraints constraintsMeterListAddRemovePanel = new java.awt.GridBagConstraints();
		constraintsMeterListAddRemovePanel.gridx = 1; constraintsMeterListAddRemovePanel.gridy = 1;
		constraintsMeterListAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsMeterListAddRemovePanel.weightx = 1.0;
		constraintsMeterListAddRemovePanel.weighty = 1.0;
		constraintsMeterListAddRemovePanel.ipadx = 185;
		constraintsMeterListAddRemovePanel.ipady = 192;
		constraintsMeterListAddRemovePanel.insets = new java.awt.Insets(4, 5, 4, 6);
		add(getMeterListAddRemovePanel(), constraintsMeterListAddRemovePanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	
	// user code end
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
		CustomerGraphListEditorPanel aCustomerGraphListEditorPanel;
		aCustomerGraphListEditorPanel = new CustomerGraphListEditorPanel();
		frame.setContentPane(aCustomerGraphListEditorPanel);
		frame.setSize(aCustomerGraphListEditorPanel.getSize());
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
 * Comment
 */
public void meterAddRemovePanel_RightListMouse_mouseExited(java.util.EventObject newEvent) 
{
	rightListItemIndex = -1;
	rightListDragging = false;

	return;
}
/**
 * Comment
 */
public void meterAddRemovePanel_RightListMouse_mousePressed(java.util.EventObject newEvent) 
{
	rightListItemIndex = getMeterListAddRemovePanel().rightListGetSelectedIndex();
	rightListDragging = true;

	return;
}
/**
 * Comment
 */
public void meterAddRemovePanel_RightListMouse_mouseReleased(java.util.EventObject newEvent) 
{
	int indexSelected = getMeterListAddRemovePanel().rightListGetSelectedIndex();

	if ( rightListDragging &&  indexSelected != -1 && indexSelected != rightListItemIndex )
	{

		Object itemSelected = new Object();
		java.util.Vector destItems = new java.util.Vector( getMeterListAddRemovePanel().rightListGetModel().getSize() + 1 );

		for( int i = 0; i < getMeterListAddRemovePanel().rightListGetModel().getSize(); i++ )
			destItems.addElement( getMeterListAddRemovePanel().rightListGetModel().getElementAt(i) );

		itemSelected = destItems.elementAt( rightListItemIndex );
		destItems.removeElementAt( rightListItemIndex );
		destItems.insertElementAt( itemSelected, indexSelected );
		getMeterListAddRemovePanel().rightListSetListData(destItems);

		getMeterListAddRemovePanel().revalidate();
		getMeterListAddRemovePanel().repaint();

		// reset the values
		rightListItemIndex = -1;
		fireInputUpdate();
	}

	rightListDragging = false;

	return;
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getMeterListAddRemovePanel()) 
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
	if (newEvent.getSource() == getMeterListAddRemovePanel()) 
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
	if (newEvent.getSource() == getMeterListAddRemovePanel()) 
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
	if (newEvent.getSource() == getMeterListAddRemovePanel()) 
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
	com.cannontech.database.data.customer.CICustomerBase customer = (com.cannontech.database.data.customer.CICustomerBase)val;
	
	java.util.Vector availableMeters = null;
	java.util.Vector usedMeters = null;
	IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	
	synchronized( cache )
	{
		java.util.List devices = cache.getAllDevices();
		com.cannontech.database.data.lite.LiteYukonPAObject liteDevice = null;
		
		availableMeters = new java.util.Vector( devices.size() );
		usedMeters = new java.util.Vector( customer.getDeviceVector().size() );

		for(int i=0;i<devices.size();i++)
		{
			liteDevice = (com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i);

			if( DeviceTypesFuncs.isMeter(liteDevice.getPaoType().getDeviceTypeId()) )
			{
				availableMeters.add( devices.get(i) );
				
				for( int j = 0; j < customer.getDeviceVector().size(); j++ )
				{				
					com.cannontech.database.db.customer.DeviceCustomerList deviceCustomerValue = ((com.cannontech.database.db.customer.DeviceCustomerList)customer.getDeviceVector().elementAt(j));
					if( deviceCustomerValue.getDeviceID().intValue() == liteDevice.getYukonID() )
					{
						availableMeters.remove( devices.get(i) );
						usedMeters.add( devices.get(i) );
						break;
					}
				}
			}
				
		}
	}

	getMeterListAddRemovePanel().leftListSetListData( availableMeters );
	getMeterListAddRemovePanel().rightListSetListData( usedMeters );
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G99F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DAFBD0D46795C7C54554A62631D3EACDC222B134F592531A09532193CBEDEB9AE71A24E44C9FCE07CCB293E7CAD37FE0A6A65A69477252080A014802E0CCFD84ADA5BE12084B72D8969390C828D2C2D2D33D309738303BF739FB17C74458F33E475D4BF29714094E7C3877BB5F63BC3EF34EF73E3B1242F79232B3EC0504E4E4937E6FEC867CB9CA48774FBC74D7C10B1113F308695F3B00FD645FC733
	6039887A9297A46764137A629D50DE8E6DA6E89741FB8139DC92FF98DE227089C05F03DF3E37E3EABE1F6F97FCF2E97BDEFB9EBC77G9201064FF49979BFEE37F37A86CE9748309132399747BCE3AF6534AAE837831683567432714F40F3A765BC57D14547DD3EBB01247F7EBD5BEEDC4734264049BEB63703BF8F11C7B33677A12F57291CB8E7A1345781D079CC32604BA6F8BA5B877BEAFBD4DFE50F64772BFEDD6E692F9CD13E16B4CF25C79ED6FA644A1ED0D0D7FD3266F03879DB1B2C4BDA2B92544FFB94DD
	555AA53F6C9D6DC7DEAEEA7BCCC0BD34339424A51DC37EC950CE81249974B98E24B7417BD54028A2F42EF94F49F56A3A76A8D93D511F39B70A4E0B94266B9AA52C7362509B3945F4FFFED0047B108EFD7601641C8AC0BBA0GD884B8C07D623E7DA760696A565D2A4F277ADD01C003572335299E598B6F8E8774F05AC539C755BC04303D7F3B3D42C21E2940666DAE3E55F94CA609E7049F2C3C3802EC7AF90BAD5742A61B92CF5A928718AD568E04ED42F8FF9C15F76A40C4BF1D487BF3E2663DFC1E1A19ED41FB
	757347ED69FC1D9D03E15ED5033857A769DE1743FB86778FC6F7D07A38094EFCB6197A8C3697C25F16C1619BDDFB04AC2DD9CBC87E29D6CBBB44A73BEC9DDEA603579BA94BEB14E73C17715CE54812DD0D74B5A6BA13E5819D0F6D97204F69CB4EE17E15D00CB11CG6DA4C08AA08D1067CF4EA987B87D4247DE597F7ECD7AD83B24698DA1456B11B54266A6946D0727BC2AEBD233BF28CB7E9E39D35294295BAB87899F7363A234C17704F90FD05F6F819E1F48FED913F4C575436A128FA3B74846BCD1348F47E8
	B26D6DD0FCB2B0708508D877492282341F94547F9370C83A9C9E417AFFD704FBB2ACF5420A30G3C7338BB0B763A8C7A6B00D8438E8DF6DC2FC556A0DBB89C2D2ABA988AF420A6A4450FF67E0A660EF8F8DF6597B63E278AE9F6B5B96724AA5699333FE5DA27D169C171A5EDCC4458C78E4C530D2A7019D73B044FB46EFE08D4BCFF4152E7282F284CD7E25548582927EB22DC2C3D7EDC17457CC655BA0EEE950C470B16BCB23F05757798FB15C1FDFBA5483D9E30593071EA7BA166338DD2D096DE7A19868EA6FB
	4296E7F27E4DD148E55E49FD270C478B6B5FD50D7DA4GF181E8870C8292824216F7B9A27594EB6D1AC68741EBFADBA6CE6201F64C3E899EC2166964A8A0BFA946F3CA9E167DFA239C107D9EF889723E470AF18D9E473D2AB16789CA8FC05C3BA15002867D1745981B122EEBCAF7C817AF493A2E787B427D3F213C02216EC0B8E588DE9B0AD1AFF584625E987FECB146CC0F6A774BF4730CA93C7FDE9A3332D7760150EEB5645799FD59D9747BC1F90C670C074FE2E1B4ACF9C318FFD89D31CAC29B86B44587AE01
	A90CD48CE1BC6B45F6130D5643FEE3BDC4202FF0C8446648E1911BC73EFE002C3DF254E6F53644BF54E85307D8EC458D05E3134994DFE5AD7BFDB4F7F0FAB013529AB3B0E7CFD5473038F05033A091F8A581D206C4DC141C43FE11FBF8C038D52F17195530F72EA39F77C79E8A491032B1493598B65C63F8AB222F135B13756FF720BD3D267C38D423F5ED35289953C01EAC2015EB4C26639A33899B3753A946ED5658B892349A771CB12EA34846F979382420F95C129AB12ED0E763AAF43E1EEE9E57DDAB462570
	F1E9FCDCD5080DE37E12FB856B902B3A33AF24D406F485333A24C32084C23A29367C93BD9FF00D05A1D1C33ED9067B348E5A1BG69A161D74F6E96B564624184123A2CA4EA5D1CB74CE4BBB99C762BC3F3C48D996BC41E81680B9996BEF03743FC0E3902C135C721C7A85F3F7AEA335FB15A796AB761592BE8C13D99924328586B16EA14974FD8CE66B507C27D8FF5DD8CFCB7GD8CCBDD9E60E292C91F6D6BA61598EE0BE7ECC75F94C9D9E4FC559278E4B54021550EC8869BA149F54D12FC87451AEEB3D2A6613
	BD65835230C4EDDFF99E9358E75D836051470DBDDBF77CADA63FCCF70595846547BBC50D78E0C1BD17E943A3CB0873C6014D2AE60F5BDBE66B9FE1F6DEB4923677784FF2B2537E37AD1769FE5AA9F45F84E38AC705DEBFF4B6CD56CB236FB3D00D715AE670EA85BE31862F6E9A1785AF252FDF47F45126060272959F7EED5764E0F0FA7BFDEFE7B3BCE975651A3068150BD3AC5AEA39EAB51A1D7B492FEB047CC9A0FB3AA17F095A061B137FA27829B41582CC7EF76C97EEC1FEE351A994E072FF27D648DFB1161C
	63999372F755575F1C7C67C795BD2A74CC765563CD37A0BBDFB02A642C1EF8E952DDEA69D83876DBB5B6398EE4F70CEFD83E9A0B3C8745D1FA4AD864BDA83FCC5C03DE063E262FC5DDDCD00BF9E0A5345783D281DB8176EB10EB2F09BC75476AB3E41AFBC7B364DD4DAFF9AFCDA8A4F8EEDA60C43EF0767376F71DE8CFFA7693D1D7295511F66838B6D177C0C41B4D3BBEE9DEE5C4FBF144BC36171DD9531F9D93B7191D9D2F15096F92AB2F0933E3A73DE33E8C6D6D0042F1E15B21BABC47AA205D84388A9887A4
	G12G2963423EC5B5B55359173ACE076A86DA931C676E487A4B30C715F3E6F67CF8523CC8BB126BD673B2EAE646EF6F24F987A35AF6CBFECF4D105F46E9791D3464F7F76D4C785DBFE9DEA43F2688FE6C6E7A862DCD2EC21F8EE0340D8768FD965A2B2E0B3BCEEC995653A9C67BCE5A46DA09357717D95D793E8D04F93EFF5BF8E0FC3158A82D0DFE97BADCEBFD97BADEEBFD97FA3FF69F23439A66AFA4227FEC6D54F7253ADA2B3BD2991577297DFB695AF05C86E930E261497BDE515FA696F726CFA51F4CF5AD
	A0ABE87D06E34E6B2A7B83430F3617CD751DEF6B75E86743F51A13723EE167C006C11FC769F6931D1D8FFB4F09EF3BC720AF6E9B111B2E55C5377F6CBAEB7B4755E10D9CD4C3DA0F9CE9633B6A6CC6DFB80BB56B328F4D416FE1F43EAEE9FD7224BD3A1F4EE7FD5166A756C55BC7E6EB55E9367507863D2A46CCBFE550C92D19FE5A202F1FC07F48206FAB413B2DD79AD3C3BAA1DDB7503E65A5225ECE835BE6812D82D0FF03555D6CCCFAEC846FEB3D1A6483F1AF85649E72C18C4EFD7500181BG63D783D2G69
	004298D1675CFF85FF1FB06E21930FAE1EDB8A8E0ABCB29EA3E2F84EB9E2785AC2537B5B165F0C3EADB09E15371587736B769261579BC0672CD94256272E6259CC4FFFDA20C9A33A43710926F89A243ED63AF36C3C0FAF594167BBE709737E37F4CD9D5A8B814BE7093B6268B1F1D7DC39E289E90DE9313CAB26F61C36A54DE2F11BBAABFCF7C98C3D114BF896168A1E253313F3BAE6A32D21345034866E39EAE3E7B46B57A774237FE36E89772F18500F725E48B4773FB0219FED74CFBE1F697674D1215B762793
	097771F3D16F41CBE7735F8D78333D73B0B9F247365C0AB94867444146A976ACB382DBE10DBC1877B6EF9F0277A0B01D15A1FC2833E4E63F1FF8E6B86F5EDB1E4772EE7C816AB3F3A2737183149EB3A7B29FEFB07CF6A174ED19A3EA4AD19766020E58641CGA08E10GD88B5894ABF2F6CB7DC9B2CDBD592186DAA129FBCD47A0532F38F4E63FAB7DAAD778476ADFBCC8564CFBBDE3D7947F58924B7CA2FFAEFBEE6EB8CC69CC3FDDEE542F9E7AAE83E2G71001500F5F305FE7754D7B07D70EB1BCB57251EFE7A
	05C4A8572948A3E1B2A652F768BCC20C9FE64916BAAB5A8BF37177DAB7DED139DEAF66893DCAFF7AA3523871750C43D174AA1D47FFA71A970E694D835BF319DD6FC351398FBDECFAFF2E64766759BF5FD69EECCFE639225789F1AE6BBA61CE17F51D3658B5F59D3654957D1B75B217791BB573A93985F35BF2581735001781DB81F97305BF1DEA788773A78BDFBA6F6943BA85FFB153857FC2F7F4DD8F38277F1D6D089B3F61F52B3A2E7AF0FDC1BF61467D5255GA5721A550DB6229FAC58D0CE7F500DFE6B15FB
	A919796A9F7EEB7E0DBFFF493BB611D3A2772CFCBE73518F6607FD1559EAC79B6667FE2047829281EB81A900B4G09031A83D08E2887F4813C009840E240F2409A4006B8A3D6AF60372BCEE900E7A176011CDA97087C40258656A485CD4C757335C437315EB23D0D2F3750EF27E01B0E31G2F8BE737E0DD26767686E5E3AF6737649B3466E03B9AD4301EE43670FFCA8CF90ADB0FC653833C648214906C43CE0754E74CA9ED1B626E52B63D8E95ED56FEF23AED321F1CED3376131AB6331F705FCF5BB0C67A4076
	DD4638CF8C5A97862D152E09344F950F5EEF50AFD239105EA4A3C3C16F22F20DD0DA9727FDE522FD41E95DED388F20F23F2A0939FDEDF8FF6DD53CDE569E24E394BFEC0BE10FG15D189C8A639C3868D65E67FC7E75B610B70DC7997129D5448DED15ED5B65345D2074FB8B2DFDA16E9A3D94DA407993719E421051AC9B6B5E0B34984630E628F58A894B248E16FAC4E0E5FB047595639F64B5F1A90E98BD8FC11BBA2636C52B15C97A7503B6E903192FF74DFD37858A51F64756A12F7506577203FB50C69F2B8A7
	F69D437503E28C6FE4FDD70E215C8326B97F87D0CB8788C594405FB18DGGA8A5GGD0CB818294G94G88G88G99F954ACC594405FB18DGGA8A5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGEB8DGGGG
**end of data**/
}
}
