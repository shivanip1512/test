package com.cannontech.dbeditor.wizard.device.lmgroup;

import java.awt.Dimension;
import com.cannontech.database.data.pao.YukonPAObject;

/**
 * This type was created in VisualAge.
 */
public class GroupMacroLoadGroupsPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemovePanelListener {
	private int rightListItemIndex = getLoadGroupsAddRemovePanel().rightListGetSelectedIndex();
	private boolean rightListDragging = false;
	private com.cannontech.common.gui.util.AddRemovePanel ivjLoadGroupsAddRemovePanel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public GroupMacroLoadGroupsPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getLoadGroupsAddRemovePanel()) 
		connEtoC1(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (AddRemovePanel1.addRemovePanel.addButtonAction_actionPerformed(java.util.EventObject) --> RouteMacroCommunicationRoutesPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.util.EventObject arg1) {
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
 * connEtoC2:  (AddRemovePanel1.addRemovePanel.removeButtonAction_actionPerformed(java.util.EventObject) --> RouteMacroCommunicationRoutesPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.util.EventObject arg1) {
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
 * connEtoC3:  (RoutesAddRemovePanel.addRemovePanel.rightListMouse_mousePressed(java.util.EventObject) --> RouteMacroCommunicationRoutesPanel.routesAddRemovePanel_RightListMouse_mousePressed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.routesAddRemovePanel_RightListMouse_mousePressed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (RoutesAddRemovePanel.addRemovePanel.rightListMouse_mouseReleased(java.util.EventObject) --> RouteMacroCommunicationRoutesPanel.routesAddRemovePanel_RightListMouse_mouseReleased(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.routesAddRemovePanel_RightListMouse_mouseReleased(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (RoutesAddRemovePanel.addRemovePanel.rightListMouse_mouseExited(java.util.EventObject) --> RouteMacroCommunicationRoutesPanel.routesAddRemovePanel_RightListMouse_mouseExited(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.routesAddRemovePanel_RightListMouse_mouseExited(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the AddRemovePanel1 property value.
 * @return com.cannontech.common.gui.util.AddRemovePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.AddRemovePanel getLoadGroupsAddRemovePanel() {
	if (ivjLoadGroupsAddRemovePanel == null) {
		try {
			ivjLoadGroupsAddRemovePanel = new com.cannontech.common.gui.util.AddRemovePanel();
			ivjLoadGroupsAddRemovePanel.setName("LoadGroupsAddRemovePanel");
			// user code begin {1}

			ivjLoadGroupsAddRemovePanel.setMode(ivjLoadGroupsAddRemovePanel.TRANSFER_MODE );
			ivjLoadGroupsAddRemovePanel.leftListRemoveAll();
			ivjLoadGroupsAddRemovePanel.rightListRemoveAll();


			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			java.util.Vector availableDevices = null;
			synchronized(cache)
			{
				java.util.List allDevices = cache.getAllLoadManagement();
				java.util.Collections.sort( allDevices, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
				
				availableDevices = new java.util.Vector();
				for(int i=0;i<allDevices.size();i++)
					if( com.cannontech.database.data.device.DeviceTypesFuncs.isLmGroup( ((com.cannontech.database.data.lite.LiteYukonPAObject)allDevices.get(i)).getType())
						 && ((com.cannontech.database.data.lite.LiteYukonPAObject)allDevices.get(i)).getType() != com.cannontech.database.data.pao.PAOGroups.MACRO_GROUP )
						availableDevices.add(allDevices.get(i));
			}


			getLoadGroupsAddRemovePanel().leftListSetListData(availableDevices);
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLoadGroupsAddRemovePanel;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension(350, 200);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	
	YukonPAObject macro = null;
	
	if( val instanceof com.cannontech.database.data.multi.MultiDBPersistent )
	{
		macro = (YukonPAObject)
				com.cannontech.database.data.multi.MultiDBPersistent.getFirstObjectOfType(
		YukonPAObject.class,
				(com.cannontech.database.data.multi.MultiDBPersistent)val );
	}
	else if( val instanceof com.cannontech.database.data.multi.SmartMultiDBPersistent )
		macro = (YukonPAObject)
				((com.cannontech.database.data.multi.SmartMultiDBPersistent)val).getOwnerDBPersistent();
	
	
	if( val instanceof YukonPAObject || macro != null )
	{
		if( macro == null )
			macro = (YukonPAObject) val;
		
		Integer ownerID = macro.getPAObjectID();
	
		java.util.Vector macroGroupVector = new java.util.Vector();

		for( int i = 0; i < getLoadGroupsAddRemovePanel().rightListGetModel().getSize(); i++ )
		{
			com.cannontech.database.db.macro.GenericMacro mGroup = new com.cannontech.database.db.macro.GenericMacro();
			mGroup.setOwnerID(ownerID);
			mGroup.setChildID( new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)
									getLoadGroupsAddRemovePanel().rightListGetModel().getElementAt(i)).getYukonID()) );
			mGroup.setChildOrder(new Integer(i+1) );
			mGroup.setMacroType(com.cannontech.database.db.macro.MacroTypes.GROUP);
			macroGroupVector.addElement( mGroup );	
		}

		((com.cannontech.database.data.device.lm.MacroGroup) macro).setMacroGroupVector( macroGroupVector );
	}
	return val;
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
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getLoadGroupsAddRemovePanel().addAddRemovePanelListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("GroupMacroLoadGroupsPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(374, 228);

		java.awt.GridBagConstraints constraintsLoadGroupsAddRemovePanel = new java.awt.GridBagConstraints();
		constraintsLoadGroupsAddRemovePanel.gridx = 0; constraintsLoadGroupsAddRemovePanel.gridy = 0;
		constraintsLoadGroupsAddRemovePanel.gridwidth = 2;
constraintsLoadGroupsAddRemovePanel.gridheight = 2;
		constraintsLoadGroupsAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
		add(getLoadGroupsAddRemovePanel(), constraintsLoadGroupsAddRemovePanel);
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
	if( getLoadGroupsAddRemovePanel().rightListGetModel().getSize() < 1 )
	{
		setErrorString("There needs to be at least 1 load group in this group macro");
		return false;
	}
	else
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
		java.awt.Frame frame;
		try {
			Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
			frame = (java.awt.Frame)aFrameClass.newInstance();
		} catch (java.lang.Throwable ivjExc) {
			frame = new java.awt.Frame();
		}
		GroupMacroLoadGroupsPanel aGroupMacroLoadGroupsPanel;
		aGroupMacroLoadGroupsPanel = new GroupMacroLoadGroupsPanel();
		frame.add("Center", aGroupMacroLoadGroupsPanel);
		frame.setSize(aGroupMacroLoadGroupsPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getLoadGroupsAddRemovePanel()) 
		connEtoC2(newEvent);
	// user code begin {2}
	// user code end
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
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseClicked(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseEntered(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseExited(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getLoadGroupsAddRemovePanel()) 
		connEtoC5(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mousePressed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getLoadGroupsAddRemovePanel()) 
		connEtoC3(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseReleased(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getLoadGroupsAddRemovePanel()) 
		connEtoC4(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouseMotion_mouseDragged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void routesAddRemovePanel_RightListMouse_mouseExited(java.util.EventObject newEvent) {
	rightListItemIndex = -1;
	rightListDragging = false;

	return;
}
/**
 * Comment
 */
public void routesAddRemovePanel_RightListMouse_mousePressed(java.util.EventObject newEvent) {
	rightListItemIndex = getLoadGroupsAddRemovePanel().rightListGetSelectedIndex();
	rightListDragging = true;

	return;
}
/**
 * Comment
 */
public void routesAddRemovePanel_RightListMouse_mouseReleased(java.util.EventObject newEvent) {
	int indexSelected = getLoadGroupsAddRemovePanel().rightListGetSelectedIndex();

	if ( rightListDragging &&  indexSelected != -1 && indexSelected != rightListItemIndex )
	{

		Object itemSelected = new Object();
		java.util.Vector destItems = new java.util.Vector( getLoadGroupsAddRemovePanel().rightListGetModel().getSize() + 1 );

		for( int i = 0; i < getLoadGroupsAddRemovePanel().rightListGetModel().getSize(); i++ )
			destItems.addElement( getLoadGroupsAddRemovePanel().rightListGetModel().getElementAt(i) );

		itemSelected = destItems.elementAt( rightListItemIndex );
		destItems.removeElementAt( rightListItemIndex );
		destItems.insertElementAt( itemSelected, indexSelected );
		getLoadGroupsAddRemovePanel().rightListSetListData(destItems);

		getLoadGroupsAddRemovePanel().revalidate();
		getLoadGroupsAddRemovePanel().repaint();

		// reset the values
		rightListItemIndex = -1;
		fireInputUpdate();
	}

	rightListDragging = false;

	return;
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) 
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	java.util.Vector availableGroups = null;
	java.util.Vector assignedGroups = null;

	synchronized(cache)
	{
		java.util.Vector macroGroupsVector = ((com.cannontech.database.data.device.lm.MacroGroup)val).getMacroGroupVector();
		java.util.List allDevices = cache.getAllLoadManagement();
		java.util.Collections.sort( allDevices, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );

		assignedGroups = new java.util.Vector();
		int childID;
		for(int i=0;i<macroGroupsVector.size();i++)
		{
			childID = ((com.cannontech.database.db.macro.GenericMacro)macroGroupsVector.get(i)).getChildID().intValue();
			for(int j=0;j<allDevices.size();j++)
			{
				if( ((com.cannontech.database.data.lite.LiteYukonPAObject)allDevices.get(j)).getYukonID() == childID )
				{
					assignedGroups.addElement(allDevices.get(j));
					break;
				}
			}
		}

		availableGroups = new java.util.Vector();
		for(int i=0;i<allDevices.size();i++)
		{
			if( com.cannontech.database.data.device.DeviceTypesFuncs.isLmGroup( 
				((com.cannontech.database.data.lite.LiteYukonPAObject)allDevices.get(i)).getType()) )
			{
				availableGroups.addElement(allDevices.get(i));
			}
		}		
	}

	com.cannontech.common.gui.util.AddRemovePanel groupsPanel = getLoadGroupsAddRemovePanel();

	groupsPanel.leftListSetListData(availableGroups);
	groupsPanel.rightListSetListData(assignedGroups);
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC3F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E1359AEBD0DC55193CC9A6F56C1436E9E4A6280945912BB1C9C3B5ED63B69D3526E36A444734CC07564C480F68E426E90D1A9F0705ACC493C38C9A54D8E383F9D4443CAC30AC304BAE8404186D8691C7476432FB018B3BFB173BF7F964E17AFD675CF377325C0584ABB3DF4EBD5FF9FC6F6FFC67ECC869FB99BB722D2504646FA05ADFF5BEA1E995047CAD5D768F0ECB91ADB6E278FB9FE0AF099DAE0476E6
	98F3AC3058CA48E7EF97C3FFA974F366DBEC7B61FB9F69FD27329CBE1250A9043152B79E7C6D64F41AEBB81DA25A2F2E5E856DEBG190043F62B08744FD517E978759A1EC33E1510CD3DB8E7207A00062B013E97A083E0E5AF1B3F9E5A1D14729CFB0DB66F2B6FE7102D3FBBE25D0D7B987664D0594756FA3576A039A7FFDB9F527A8165935724C2FFADG8ADFC06EAC76C06B708E76B57865D015DF88076530AA7A7B2BC624CB0292288A084312DF2C8A067A94B996315B4FE0F3D670ABF207AC84E8AF6A9542E2
	D06DC7BAFF2AAE267B421E50A7926282F5C8BB9B7A9BG72F47C878E44FB607BABGD56272569FF1E872F4DF3E072CFCAADC30A7093C8BA5A667CAA9AEEFDA6C25223728ED6654228D3640D8690045F6942083A0883090609D6A938BEB7E8D2D2BC7F54B21109CF6C5A2CD4100F2D68E08C178365BE1C44379C43F2C8488E1F67FC9F52589BF1381DB77539BDE47F412F90A7BC0166F8E1233225DDAE422131C4C63562583CC97EB8662BAE134F313524E99986F2363E9FFCE0C34174E158BF61850DE7944B16B96
	ED1F4241B86D5303E86B27298F1443F73E669F8C7FD70A9FB560193F8ED0BC76D3E1EC73A0770D6E57B8AF9D050BC949098ED3BDAC32382C5EA06321BF18484B926AF7E9C1C673D51D173BA9FE25814FF8B9C171585F88E3B5A10B0D7957CDF5983F69504F8658G1087D09C36584A81EA425C471E287968BAFD4CABA8EAD3CC8A86C4053035B7553E892DB82AAAC2DBB82A8AE13F58A5A81250939423C41B7363DA54C14F38F56954777DC0630C989695C11564B06CAE04C4D5D422EC4E325A3DB8C79169E82794
	9201C0A8C2783EF7576E03B6A8C4554FA281C19563B358782ADA3449305085BB4286704D7472G55D7B7489F81C85175F05C0E7B350B8AE48B3B3DC31687E311CE1404EC88231EDF2039A38D3E3342DC472B6B91D7AADBEC15B25F274F7E3AE19F0F64C77685E50C47581F6BB0C7BBE56EB34FF7F31F716CDEC6AA1F784454E7282F484CD7D26444584923FBA2DF2C9F296B3E41B8CE8427EC9EF371DCB17FDB53A818E40C69372291ED15893C2C8358246B78B67BC16633CDC2D464DEFACE81879383F10DB3BE17
	B9CAB5FDA23D9D9A7FBBF5B9D650716D30F7F984F2B0008A109E514FD0C7F712755FC676713A74D29A097B3F7AFF2241F436288E4FA9F1D88C2B9EB1A20683709155461654A1BDAD0EFBE5FD4DB20A0FC05C3BA15022BA7E2EBA0CCDC1D59529A7260A67C5D51542FD71719514D6B4569309278CCE6B7EBA14D19E013857677F3A8EE346AF0743A2B51E3EC49BFF10462C9894C34034DB0E05D506FF4801FEBFA80EE9B963D18796C543C2B00679075590778A284708A20540A5B0051123C3984F2F551499F4348E
	6C0D3550F6E8CB06F8EC0E9C6231D9FE69D63276C205556CECD9344CE30D8C31F819BD940FCD46531AFAF35EADB4F7E878E88145B9D4747B24C50C9E97DFBBF14F25C0AB9BE043900F0B06869C6739C78B88379C8CB2356A7A7E2031846DA38E45C4C85918645AF59D1EECFCBDE12CCB53A79B37B722BE03067C38C8C1FD66B9F83D18877C904015ABCC27238A53891B373C1E4F5B2430F95BCD67DDEA60733CD1B62FBF4A66E5C70D731A9A793C92154DBB2AEA7B2946F92FB8793C8D5A3CBCED5E69981B477C25
	688256A1E6B5E7DFCC2A0A2992E6F5C105C00944D4C3EDB92B26D85BE3E10C57107750BAE19D74F3G364638DF7DE1B72FA15386B3C86E6DEFA72D19F78DB35E0E8F477D6A60ACDEC3E63A10E68446D206398F2CEAB41EE32EE8D476CB7488556C97F09A7D0E61A467AB5076CACAD4ED0344B04AEDADBB11DFBCE3B534D6F36C25BE0D3226815D75GAC26828EE3CC950E303352812D97007978E8FDB3660EC040A70664E111EA308A3ACDB1D5057203BA6A8501B6DED16915151098B8BAA08C8BD477D54D18404E
	758C00C79F53ED366E586B0CFF11DA059584C70FF5719A7136FDCE0D2775BFDBCC9C5776D94DEA7654BD6FDA7BC7181EEF9E096B7B4AFD36024DDF3FDC44E47F23AEFB8E4CA99D65F2399ADAA74A25506FE90846E85D5E40E9F5G1D59BA2DD79ADDA634243EFE35C30A2AE765D8D43C9042FF3D0A980DCE2D3F3606CF2005C4230AD15738DDAF78A6593543F45BBA54BB63FF3011733F86F85F2A737FF8E35375716F83BF95A6930071EF7FCF7B0D702F6F1ADCG467FA9A7673FF24CE28B0CF17EC913737A78EF
	9E1554245CB35E176EEE3B915E359DA7D13DD6D32456A45E27524762B55D3DE3936BBBF64FB8C44F5AF5E309F7218FA9FE63D862DDE815FE975A84E32D17F8EDDC6D46DC10857D0781F281368114DD063B47E51E2BDEF01EA2D35CBD5AA077AAE1A1F8FEDCB1416FGAE248B673F56DF6EC21D5273DF57437E7AC4BDF4DE9EAFFBA4214F56D5CCD8D71550CFCBD8476C59D5B87579B15E4E6C7CC8F77037092C4B7C7CD8EEC75DEE027EF60052ABDC37BF7460D9D6897DD6000BGD7GB2G56G64DE617A2DEB2A
	1FCA3F54FBBAE5B760DA61CCF7A756E03ABEF2272947672754A339EA366E4E066951DBBDE15DBF937AE52674069A27C76F5A04F509740E1B52DB621A9E3D3BA72CCB24571AC00F5DDF17507A64A20C29G8CF7450E38C5503F77AA3F6F9C23B575C63DFF0A7631DEE27D8774FB5F462B496F085385C6E323CEE332BB62F4014506531D7CBEF446EDFE9FBA6BB63F8F1DF76FE5F8584378CA42473FF4CFFEDF723B4D6ECB6FD2799FF9F38F5D9BCE5CA88DD6ACBE3531E755B708493D69D3A1A4EA3A5BC7BE2D5F29
	4DE9D6E57789DD4737BAA6FB6B5BE6E2DBF68ED450FC3F6B9BF68E646B78AA0AAFB3605979305A4E5FF64BE1AC759B1E1B16F8126B7F8E0F397E33BCD8A7C76518629793F53C42D3260F4533D81BAA06D09D5ADD0C2ED78525CF1CE0235F5075ECAC597A751EE4F6E43A5E5FE05475C99D1FBB8EFFCA47E7B69A7127F57C13AEA37EE39D7F739A3C5F8605B1289688693E067A7D3DFE0F5D8C3AAD84DCA500739A2B3D591934FC846FEC3D0A9082F64FC7C4BFB91DC25F5D6DFCED864CDF893091E0ABC0E98A2FF5
	1EB902F362B75171E7974BA3598E1EC72E2470BC71C19D51FD6D34617B1776EF7BEEB4F57EF872BB25417C7A6FB55C2F57034C05B3382ECFDC4433191E7F34C693C6D47CDDC28AB489FD9D54F26C3C2F2FAD5656BBE67073FE309677D4213F90E0698CFEDF9CBD4C6F0BD9F7ACA69DA96D2677455C4E1356B5B3D85C664E085FDFB2E3AF95B11A3F30F31A87E6DAEC1DB391772C3D5430875A7C11FCF6C63371376A0C63687F68CB717176F163486FF3D6637855F16328235D5AFAA65B2395DC366D0FE612602A5A
	24F7617419CC2635DA6B6DBAC44A973CDCD4B98B69BC6670F07B4C1C183BF8CE5A2141AED8037006A67341D9713C45FC686589757375795E4E5A692DEB3A61F5AC6F56537C2A4ECA4C47AD941FB2ABB19F8F55F23FDD88E3DBE6711AB23D85633CF336459681C88548GD88B10B31B67EC3979B819221E6C14A39D10540306E3104977AB7B74FEDBFA30087B474A7BEFA359F3DF4CFFB509FFEC194D6CDEB20735F91D07A81E4977D1AB4A6704312FGD2G9681E4812C1B4365DB5DDC4964EB8EC82ACBD585FFBF
	FDA56142F5C962C89C0D091445952967EFBFCF43DE6F513D88A9A6713F123978FBED5FB9224B7517DDDC2E83F75DC2BCCF3D18FFA809DC1DF319BCCB6746E3BAEFE0FB91532B5BCEF49F6AB0FC0F54FE77F976666F140676865D123CCE48EAB12F936EEBB12F5332DBA62F435636A4FF37FE284578EE4DFC6A9BAF66369C304BB6008373AC3663G1EF95C1FBEBB73AF66CFA63E549C6843BA85FFB5D3B97D1A566432B636CE7DDBDBEBAB3E6375482AAA07F0FF0E7F34956D254A910A54EA56D65491FD34E0D3B5
	7C972D6837C1311722351AFF5C9D6EA248274E1B3C76E7BE7E5817467F9BD03278FDAB4FC50973D3357D526645FD1C6978FD9F667507813F8920B855E2BB88F09A208360A2008A108A1081108D309EA097E0ABC061FC0B2D8C20F2BE37C956B9FC776A9286346C0521AEAB54AA00548EEAEA98C51050B41ADE66FA135B66FB5E29ED73A3AFFDF7851DF60EC534FA7296AF56F3F2EFEFD454FDA043DB22635A22DEB9AAE19D4A6A2C47E91EF102AC9F9770FB5DD394578D38BAAB774BF967094E77497FD6A413973C
	7093A8D1585BD12750272F795817DCD607EFEAD91BFD66FE586E1B6807DE1F399FF6790CFE285D23FC9803FDE023EEFD5EC59D77050E93691E087BDC8A287DBAFE10720578D69189F27C9065EB04623AB55C30817705063B6CC3FB01487D324257267870FE5CAB85032CBF1B4E1142E0BEDD9F73A90FD2C4B070FD130EC33ED96DA89C5EC557024905A0E51D618F0C6E0E63B96B047D3F2177AAFC50E6BDC7F6C4953207171CD5ED14015C61D37602E7EEAF301242B6E2E386E9A3052855B63203AA3D0D9400C1C6
	A3E4429F0B61E3570CB13CEDCE1969EFE088A98B3498DE10984341A3E84B8D005F3C0047611A439FCE621767C3C2B0288A41C1D7B800BE5AB4260A71BCBDF484770F72B95AA09B9BBD02FC8F98567C8FD0CB8788A3CA8E0FEE8DGG3CA5GGD0CB818294G94G88G88GC3F954ACA3CA8E0FEE8DGG3CA5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG288DGGGG
**end of data**/
}
}
