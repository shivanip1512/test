package com.cannontech.dbeditor.wizard.route;

import java.awt.Dimension;
import java.util.List;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

/**
 * This type was created in VisualAge.
 */
public class RouteMacroCommunicationRoutesPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemovePanelListener {
	private com.cannontech.common.gui.util.AddRemovePanel ivjRoutesAddRemovePanel = null;
	private int rightListItemIndex = getRoutesAddRemovePanel().rightListGetSelectedIndex();
	private boolean rightListDragging = false;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public RouteMacroCommunicationRoutesPanel() {
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
	if (newEvent.getSource() == getRoutesAddRemovePanel()) 
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
 * Return the AddRemovePanel1 property value.
 * @return com.cannontech.common.gui.util.AddRemovePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.AddRemovePanel getRoutesAddRemovePanel() {
	if (ivjRoutesAddRemovePanel == null) {
		try {
			ivjRoutesAddRemovePanel = new com.cannontech.common.gui.util.AddRemovePanel();
			ivjRoutesAddRemovePanel.setName("RoutesAddRemovePanel");
			// user code begin {1}

			ivjRoutesAddRemovePanel.setMode(ivjRoutesAddRemovePanel.COPY_MODE );
			ivjRoutesAddRemovePanel.leftListRemoveAll();
			ivjRoutesAddRemovePanel.rightListRemoveAll();
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRoutesAddRemovePanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {

	Integer routeID = ((com.cannontech.database.data.route.RouteBase) val).getRouteID();
	
	java.util.Vector macroRouteVector = new java.util.Vector();

	for( int i = 0; i < getRoutesAddRemovePanel().rightListGetModel().getSize(); i++ )
	{
		com.cannontech.database.db.route.MacroRoute mRoute = new com.cannontech.database.db.route.MacroRoute();
		mRoute.setRouteID(routeID);
		mRoute.setSingleRouteID( new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)
									getRoutesAddRemovePanel().rightListGetModel().getElementAt(i)).getYukonID()) );
		mRoute.setRouteOrder( new Integer(i+1) );

		macroRouteVector.addElement( mRoute );	
	}

	((com.cannontech.database.data.route.MacroRoute) val).setMacroRouteVector( macroRouteVector );
	
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
	getRoutesAddRemovePanel().addAddRemovePanelListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("RouteMacroCommunicationRoutes");
		setLayout(new java.awt.GridBagLayout());
		setSize(374, 228);

		java.awt.GridBagConstraints constraintsRoutesAddRemovePanel = new java.awt.GridBagConstraints();
		constraintsRoutesAddRemovePanel.gridx = 0; constraintsRoutesAddRemovePanel.gridy = 0;
		constraintsRoutesAddRemovePanel.gridwidth = 2;
constraintsRoutesAddRemovePanel.gridheight = 2;
		constraintsRoutesAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
		add(getRoutesAddRemovePanel(), constraintsRoutesAddRemovePanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	java.util.Vector availableRoutes = null;
	synchronized(cache)
	{
		List<LiteYukonPAObject> allRoutes = cache.getAllRoutes();
		availableRoutes = new java.util.Vector();
		for (LiteYukonPAObject liteRoute : allRoutes) {
			if( liteRoute.getPaoType() != PaoType.ROUTE_MACRO) {
				availableRoutes.add(liteRoute);
			}
		}
	}


	getRoutesAddRemovePanel().leftListSetListData(availableRoutes);
	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() {
	if( getRoutesAddRemovePanel().rightListGetModel().getSize() < 1 )
	{
		setErrorString("One or more routes should be selected for this route Macro");
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
		RouteMacroCommunicationRoutesPanel aRouteMacroCommunicationRoutesPanel;
		aRouteMacroCommunicationRoutesPanel = new RouteMacroCommunicationRoutesPanel();
		frame.add("Center", aRouteMacroCommunicationRoutesPanel);
		frame.setSize(aRouteMacroCommunicationRoutesPanel.getSize());
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
	if (newEvent.getSource() == getRoutesAddRemovePanel()) 
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
	if (newEvent.getSource() == getRoutesAddRemovePanel()) 
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
	if (newEvent.getSource() == getRoutesAddRemovePanel()) 
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
	if (newEvent.getSource() == getRoutesAddRemovePanel()) 
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
	rightListItemIndex = getRoutesAddRemovePanel().rightListGetSelectedIndex();
	rightListDragging = true;

	return;
}
/**
 * Comment
 */
public void routesAddRemovePanel_RightListMouse_mouseReleased(java.util.EventObject newEvent) {
	int indexSelected = getRoutesAddRemovePanel().rightListGetSelectedIndex();

	if ( rightListDragging &&  indexSelected != -1 && indexSelected != rightListItemIndex )
	{

		Object itemSelected = new Object();
		java.util.Vector destItems = new java.util.Vector( getRoutesAddRemovePanel().rightListGetModel().getSize() + 1 );

		for( int i = 0; i < getRoutesAddRemovePanel().rightListGetModel().getSize(); i++ )
			destItems.addElement( getRoutesAddRemovePanel().rightListGetModel().getElementAt(i) );

		itemSelected = destItems.elementAt( rightListItemIndex );
		destItems.removeElementAt( rightListItemIndex );
		destItems.insertElementAt( itemSelected, indexSelected );
		getRoutesAddRemovePanel().rightListSetListData(destItems);

		getRoutesAddRemovePanel().revalidate();
		getRoutesAddRemovePanel().repaint();

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
public void setValue(Object val) {
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getRoutesAddRemovePanel().requestFocus(); 
        } 
    });    
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GE6F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E1359AEDECD4D536FCD742A61A7003A0BF2AD6C3A26B47EEB7A19B92305184D7F645DD0CEC36BF3A8B592D1B2A68323B28443D1DCE27957AC92B22D6C57990B635D220508E6036258D964ADACBEDEBE445571957691B3E19B7FD6FCDBF20ADFB4E7DF873BAFD53C203CD8E6F5DF36FBD5F675CF35FC0AABECA494BF1D5901213C7785F299CC2169EA4646E43729C01CB1253BD447677B1C099492DF14373CE
	18BB33B85DD3CADEBAD8G63D498E75C116EF9875E4B497603D58760A5819FB74C7D567DC25974FC9E7BC6704927632C930570AC86D889B8FCEE1711BF295D4B719BB8DEC00E0B102DFD38667D5ACA0E2B03F1BBC08AC0DA9FDB3F811EEFD24E8B3CF5FC5D153BD24876DFFD6AAAC2BAB61A822A82ECEFBBFF6EA38F676C88A02F665AC23EA799466BGD079DC12595E8C4FB36D8301AFFCDA285AA70543DA5814FD7D55434AB5C977D76BDA54143D5E8EFCDC12FC3AF6CE8B0522E145A7190A9626E823DD8A4B2A
	590FFC165496D03AC09346C4C15C9B0548FBB50CB782E4D978A70A905F8C6FD7GCCC568FB7653B3DC1F1E6B8F13794F05F3CB92683BC4E1FA26A9B1FD17C6DF4BFF177AA6279BFD308D66AA02691E43GEDGAA40920077E9CC94F47D9B1E0D3DA62A25059BA311A6552FDF527C328A6FDEAF4CF0DC076C53F4BFA14C6F97EA2B9C64198E583E1EDB5E47EC3272380801D59D8F100C1FDFF465BB58A4E365D1D7EA1059E2CDB0E693467B2E043CB30213E3F4B26FEEE267EDAE54F2739CF827BDFB44350D53F18F
	44F81F98C0DF6B54578760BD074787430FD17C088D4F6255CD71B8CE06396C81919BBD45C216B677F2D2DA5366E807E5690D2EF61549502F464BD2DF0C34172A0C67EECB1673941FE643B3D91E223260F8934C550552BDAC3EDA82183FABE03C9AE0A3C096C0C1B85DF3G20AEACE26C6B2E4FEFB2465AA55DEC0AAA2ADF56895B3B2B7BEDF84AA326AE35068DD38A7B64AEC9D724DED5B688DFD35E0DB6681D34EFAF0DFD9F70B8AF07E51DA6ABD017C232A96B86DB73F1B796BB1FAE5359CEA5A48303D004883A
	C73A4B6129CA0679CD44AF19F2EC851B2F69C61F8CC9DDC0918840BB330B5E0F766A817DA3GC9169DD6D1FA97E59D2A05575B26E98351C8A7EAC2B606514EABE96DD88A6F2B424246E185F195DA3A27CA93F436F46F3151E9D6FCA83E240F0A9C9BA9429A5D200918F931C744CCF3517D246A598B0EB10320EAACD666EB7139B31752C43958F8D7D1CF5C5E864DB907E6830C477EA9BCF2EF937DE24BD799C17455AA10FDBDC016E5638FFB763118ED128CD9C469D7BA84186C0FD91C49791F628AEECF14AF0F4B
	7F262547A5BACFA2D003815A81C6GD2A242164FD0BD1D765FBEDB7E157208273F7BB671E0B67BAA006714BCA4074DE6B9A2077D70E270392EG7263F95C27D9FB2ED0FC84727E9CA41AE161FB83181B12E96ACAAF9C3117E553D4420158FC1072B2223D11D849903C86832823B68CF9EF2D9F8AE04E7834F0D8264E33367079911A3332AA07C068F3DAB4ECB27CC4BF467D00BC4AEB465CFEEC0A06A4B50A750775900FC8E847082E04A0A43004112AC14C6735D68F04309E7C0D3D9001390AC1111B437BC5EE9E
	38F68FD9F775204B69ECD9F6FF334B9CE47915BC984BCDA61321B84BBECAEB87479BCC56107181BC276DE62C5CF8CBC53AAB015F9A00CD03A2B77E2E603C28BFBCA94EE92A4ACCEB597CCF41D27411BC981521ECE3213BE8597179601E3839AEEED3B67FF290ED2A5AEA648A9DEDBA37D8740C59A00F9BF007F5E657B11D59052D3BD8A256E569EC9DB11C56D5140AF51D86DB2772F5EB8C7B3A8CEBDD054956D5191C1EE9DF17DAA656ED626B32793A3AA8DB477C10FF95FB91273EB390D52A23260215DDB2A1D9
	A2D15356DF9EB9D940E95C9995FD64B775682775B04E845891953175CC1168A3178E24104C7B5ECB58B7178EB159CE8C45E2EB5FBC51C7DE8DA1CF9366668F0998D0876CE7D923E1E8BE054690775F7B2ABD7698EE1F3A8B1EFD0AEE182DD09CC6042F8F29A8AF1E339C4D7B0E87E9BF023AAE833E9B81D8DE6D725A732AE0181D178D706C844825B4771434E07D707BBB6410B6A4D38BD64330A9EA1A500250C03DAA51C73B2C77E9FAC8769F8ECAC39235FDF58B96312FFA039051C7AC1FBDFEE48F13DF26DEE1
	CD4161A3DD22CF3C373C014B34612165644C0DF217D35F1EDC7201CB9DE6F6DEBA9C3377584FBC39595F3F1E4FF4B7CB046E1930E66F08506B8FA5DFCE55CB276F33D00D71BADBAAF835831FE40BD7EAE92383AFA550EF36A906F9C90B9A7255907E5B2E4B06B1337DDA3F38GCFFA21B29AA7D13D5AB18D55B6C73227516ECC7E3CB2A17FBA10FD07A53FDA56F4F372F7C01CCA53A94064771E3CF8AB72DBD493AB40647F35A57F5151F4CF7F2810FFB90D271B103FE5C4B193CA4FE4CFADEA3D9559B945E9CC4F
	7B0A6A1371F72A1451D89F18B6BA35A7E4F70DA7D94D9A0D3F8F7D0E62B70F465F073E2B977721AC18EB3BA67A639743D88B9E017186002DGBB81AA2F27FB4EDC97356AA175B819617E518A35D78FCB6A65C98D852FCF7E9072059E000F7BC3E8D35A83D8F6F834B85E8EBD57A76BEE460D593E35D376D5470D17456DE37E6CF24FFCFECC76B3BBBF1A3C627B44A3574579F111FE1F48427198541AB1E15B36881EE5C7E15C866087C882C885D88730F5CC5877155059196CCB2327933A8D3F7CA51C6967627BB0
	4B9E57CAE6E74764A97B626D689EF75AF72EF4F67C3E1E326F4338F125A33F5DE53363776E14FD717CCEB8722BAF1F9D3F73D3764573EB0B63476E30AD34BF71435C88G43FDD2073895B0CE9B97F71E276B312F5EEC0DFFCF4758AF3131D49FFF771B8DEC9EF73E1372D83BADBC925DC9D9EE7CB31C78CE74C65879CE64561C6FC43B34B206879A76AFA5E2FE0FB67D1D69BD4D694E748155614937CBA8EDB8F18D1A2C58FC72393F18EF91073B53FF25104C75AFA7D945EF72B5AD26F62EA696C75E693E776DF0
	708FBB8756521AD4B8414E019C8B3F1E62775A706CFC783AD0FC5FBDGF3499322B6F5C5925B7FFF91E77B7BA258A79BDAD4774971B68EC676DAF331AA56EA4AA1B4873F0F517D26248764A9BE3AC6773339C47B67C5927911597A51923B2D0FD978EB25F67CF18B5FD4E6471F307011F2BB3E5642DFB909F7DCD59A05EE816A458D342FF1D2745C59E0DBB7602AG9AEE305E1B1DC90F8E631D2DCF17C2A06E65086CA3A712687746BA31B785562781EC82588ED011A4FA1D2EA0FEA369F068CFD88D795EABEA48
	D8122891CF204BF91C7D46765EDDF8BB72BA91B09E119F15870B693BCF0A185EGBA3B6788BB577C0067B2BD7BE9FFA68D1BDE6FF9DD71B7C901B66AB5F656EF213598771F19A34E7A32D3C8530471920054B9622EB8F2C85C95D7BD301C34A5DDF43CABE6F69EF32D1943F2B6F3CE6C6E32B27ADABE63F935CE702C1C8B773139087BC9DD050D867A7B499CF6BE3379351366B176B10EE273AFCF1AC7F9DFF0596777575B6751C6C5FCBF536D2903C2371DCF2DA46AAF6A935E03D74CE5BA2D634F762E7D644062
	57732B66A11F2837D978E76E543AA56AD19E07C25803709657F95F3CD84DE2B1F44F145E7966E26F61D96E4B3A65FD2C66EEE1B1B3AF3E96E7D3FC523C78DA7C4EA99137CBE0EE5BBC51CFDE56318EF44ECF77C4G1281D2G5681E44C97757A5060D1B2C3AF5929C55A20202B36A31069672F1B5DEFCB0F650B78C87B653DE47542D7F3F6A7080FED73195FCB9730E7D667FE0AE77A7D59C07D9AE06E8AC09240B200D5G0FAF907A05872B18FEADFE45ECB4CD4957CF3F1088653A94F9B80646A24A726A812F78
	6E3399E8FDC4E991D2C0E2FF258B71775A8F2F93CB2FBF968A3DAA9F3C1BB4BF77EA4E7E84FAF5AEE47A24AE0C65F4D6F0E7BE336B73270895C32F585EBFB87563575913BFAA8F7E8DDACF5CA378F4679EC1511DFBB4CD1F3E879B5693FF371E506D5F2DD9CC550EE1ED4BG3F6CG28DC146EB98A503CC844D32171890BA707D8EA718730C741DF4DCC417FA5A3312E7FB0E67E2D6DDF86FE436B55CCD38BA1FD014FB351DF269621C85E2F1AE8A37A4102AD6578D29363D6157BA81A777B276D397B8368E7AE1A
	7E77A49663CFFFE77F3F81254BBFF609DA943FBE1953DB3AA896634C461B47312E2F877CD6000264F44FBE0093GEDGBFG18G49GA9G2B81B6GE482EC87F05F81F7E6002ABB04CFFC57711BD7179464558BD3DD5329D7G498FEA6A98DDD250B55CAEE747927B26F9ECE65FDC9A235FDC41261D23915ECBF60CE1AF277575993295835DE325962E55E8578C85FBD056E70D1CC69A8D20CBED2E38530DD3DC8F60CE3BC4DCFEF103D8F2BFBFF6B0113E90059720C5E15F0DBA250035A7FBBC312EFF9B1FD957
	1D634EF1681A189A0779934EF1D8B4E10FC3FE071A409C8C000FFA2CF56FD838EFADDCA5250938EE45EF76DB78CFA6F6F17C17B2B2947843D42EE10A6B61384FEC38EFB96E58847A8BD46E57F4317754845E0D7B94D5E56353F40D928677D976E820B2AA9149A6F70B05C339D96FA89DAA24FB416512CAC5E7784A112298DE08CE587F9B7A69679D70DC758249B3F4D2A2DA4E6AD6AAC06650F1EF6E167BF2DD445DCABC4CA12D440DD6EDA5F954682DA4979CB292A1D37ED88E9F39E14F619D8B76BA7E86061034
	18677062789CFE2D86FD399170590BC59EDE2879EC1A383C9C12D45514540146309FE334E9147D76456BDC8D52B7449ABE4966BC47D06E20ED4F7F81D0CB8788F539EFB9DA8DGG3CA5GGD0CB818294G94G88G88GE6F954ACF539EFB9DA8DGG3CA5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG148DGGGG
**end of data**/
}
}
