package com.cannontech.dbeditor.editor.capsubbus;

import com.cannontech.database.db.*;

import com.cannontech.common.gui.util.DataInputPanel;

/**
 * This type was created in VisualAge.
 */
public class CCSubBusFeederListEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemovePanelListener {
	private int rightListItemIndex = getCCFeederListAddRemovePanel().rightListGetSelectedIndex();
	private boolean rightListDragging = false;
	private com.cannontech.common.gui.util.AddRemovePanel ivjCCFeederListAddRemovePanel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CCSubBusFeederListEditorPanel() {
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
	if (newEvent.getSource() == getCCFeederListAddRemovePanel()) 
		connEtoC4(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void ccFeederBankListAddRemovePanel_RightListMouse_mousePressed(java.util.EventObject newEvent) 
{
	rightListItemIndex = getCCFeederListAddRemovePanel().rightListGetSelectedIndex();
	rightListDragging = true;

	return;
}
/**
 * Comment
 */
public void ccFeederBankListAddRemovePanel_RightListMouse_mouseReleased(java.util.EventObject newEvent) 
{
	int indexSelected = getCCFeederListAddRemovePanel().rightListGetSelectedIndex();

	if ( rightListDragging &&  indexSelected != -1 && indexSelected != rightListItemIndex )
	{

		Object itemSelected = new Object();
		java.util.Vector destItems = new java.util.Vector( getCCFeederListAddRemovePanel().rightListGetModel().getSize() + 1 );

		for( int i = 0; i < getCCFeederListAddRemovePanel().rightListGetModel().getSize(); i++ )
			destItems.addElement( getCCFeederListAddRemovePanel().rightListGetModel().getElementAt(i) );

		itemSelected = destItems.elementAt( rightListItemIndex );
		destItems.removeElementAt( rightListItemIndex );
		destItems.insertElementAt( itemSelected, indexSelected );
		getCCFeederListAddRemovePanel().rightListSetListData(destItems);

		getCCFeederListAddRemovePanel().revalidate();
		getCCFeederListAddRemovePanel().repaint();

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
		this.ccFeederListAddRemovePanel_RightListMouse_mouseExited(arg1);
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
private com.cannontech.common.gui.util.AddRemovePanel getCCFeederListAddRemovePanel() {
	if (ivjCCFeederListAddRemovePanel == null) {
		try {
			ivjCCFeederListAddRemovePanel = new com.cannontech.common.gui.util.AddRemovePanel();
			ivjCCFeederListAddRemovePanel.setName("CCFeederListAddRemovePanel");
			// user code begin {1}
			
			ivjCCFeederListAddRemovePanel.setMode(com.cannontech.common.gui.util.AddRemovePanel.TRANSFER_MODE);
			ivjCCFeederListAddRemovePanel.leftListLabelSetText("Feeders Available");
			ivjCCFeederListAddRemovePanel.rightListLabelSetText("Feeders Assigned");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCCFeederListAddRemovePanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	com.cannontech.database.data.capcontrol.CapControlSubBus ccSubBus = (com.cannontech.database.data.capcontrol.CapControlSubBus) val;
	
	java.util.Vector ccFeederVector = new java.util.Vector( getCCFeederListAddRemovePanel().rightListGetModel().getSize() );
	Integer feederID = null;

	for( int i = 0; i < getCCFeederListAddRemovePanel().rightListGetModel().getSize(); i++ )
	{
		feederID = new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)getCCFeederListAddRemovePanel().rightListGetModel().getElementAt(i)).getYukonID());

		com.cannontech.database.db.capcontrol.CCFeederSubAssignment ccFeederList = new com.cannontech.database.db.capcontrol.CCFeederSubAssignment(
																		  feederID,
																		  ccSubBus.getPAObjectID(),
																		  new Integer(i+1) );
		
		ccFeederVector.addElement(ccFeederList);
	}

//	if( ccStrategyBankListVector.size() > 0 ) Dont know why this was here, anyone???
		ccSubBus.setCcFeederListVector( ccFeederVector );
	
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
	getCCFeederListAddRemovePanel().addAddRemovePanelListener(this);
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

		java.awt.GridBagConstraints constraintsCCFeederListAddRemovePanel = new java.awt.GridBagConstraints();
		constraintsCCFeederListAddRemovePanel.gridx = 1; constraintsCCFeederListAddRemovePanel.gridy = 1;
		constraintsCCFeederListAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsCCFeederListAddRemovePanel.weightx = 1.0;
		constraintsCCFeederListAddRemovePanel.weighty = 1.0;
		constraintsCCFeederListAddRemovePanel.ipadx = 185;
		constraintsCCFeederListAddRemovePanel.ipady = 192;
		constraintsCCFeederListAddRemovePanel.insets = new java.awt.Insets(4, 5, 4, 6);
		add(getCCFeederListAddRemovePanel(), constraintsCCFeederListAddRemovePanel);
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
public void initLeftListFeeders() 
{
	java.util.Vector availableFeeders = new java.util.Vector();

	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	
	synchronized( cache )
	{
		com.cannontech.database.db.capcontrol.CapControlFeeder[] unassignedFeeders = 
						com.cannontech.database.db.capcontrol.CapControlFeeder.getUnassignedFeeders();
		
		java.util.List paoOBjects = cache.getAllYukonPAObjects();
		com.cannontech.database.data.lite.LiteYukonPAObject litePAO = null;

		for(int i = 0; i < paoOBjects.size(); i++)
		{
			litePAO = (com.cannontech.database.data.lite.LiteYukonPAObject)paoOBjects.get(i);
			
			if( litePAO.getType() == com.cannontech.database.data.pao.CapControlTypes.CAP_CONTROL_FEEDER )
			{
				for( int j = 0; j < unassignedFeeders.length; j++ )  // see if this capbank has no strategy
				{				
					com.cannontech.database.db.capcontrol.CapControlFeeder capFeeder = (com.cannontech.database.db.capcontrol.CapControlFeeder)unassignedFeeders[j];

					if( capFeeder.getFeederID().intValue() == litePAO.getYukonID() )
					{
						availableFeeders.addElement(litePAO);
						break;
					}
				}
			}
			
		}
	}

	getCCFeederListAddRemovePanel().leftListSetListData( availableFeeders );	
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
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getCCFeederListAddRemovePanel()) 
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
	if (newEvent.getSource() == getCCFeederListAddRemovePanel()) 
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
	if (newEvent.getSource() == getCCFeederListAddRemovePanel()) 
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
	if (newEvent.getSource() == getCCFeederListAddRemovePanel()) 
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
	com.cannontech.database.data.capcontrol.CapControlSubBus subBus = (com.cannontech.database.data.capcontrol.CapControlSubBus)val;
	java.util.Vector ccFeederListVector = subBus.getCcFeederListVector();
	
	initLeftListFeeders();

	java.util.Vector assignedFeeders = new java.util.Vector();
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List allPAOs = cache.getAllYukonPAObjects();
		com.cannontech.database.data.lite.LiteYukonPAObject litePAO = null;
		int feederID = 0;
		
		for( int i = 0; i < ccFeederListVector.size(); i++)
		{
			for( int j = 0; j < allPAOs.size(); j++)
			{
				litePAO = (com.cannontech.database.data.lite.LiteYukonPAObject)allPAOs.get(j);
				
				if( litePAO.getType() == com.cannontech.database.data.pao.CapControlTypes.CAP_CONTROL_FEEDER )
				{
					feederID = ((com.cannontech.database.db.capcontrol.CCFeederSubAssignment)ccFeederListVector.get(i)).getFeederID().intValue();
					if( feederID == litePAO.getYukonID() )
					{
						assignedFeeders.addElement(litePAO);
						break;
					}
				}
			}
		}
	}

	getCCFeederListAddRemovePanel().rightListSetListData( assignedFeeders );

}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GAEF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DAFDEC944795B741000B5C08D4A8C204BFDCC2A2D7351AB4F5D5543AAAF5C9CB9BC7A5EABE1002DAD7A12AD55146D5D1CB92C71D7B32893131038DF108A1CE0184A72E8B4194570186FFA5F63840459C4725A50135EFED2FFDF7FB5E5D73877E383EB7333337BE6F598E8A16FE6C4EFBB373663DF96F4D1BBDC8556B99AE1B3D0A901B0B68FF27ED04549EA56461EFBC79A227250839454474778660E5
	72D81B931EAB003769AB3945956457CD3B203D8E5A8350FE855EAB49D3CD6B8F43CB92B9AB00B732ECF97B5CF21E70F2B9A5347DE42C941EFBG19C043678E91657FB9D62D5373F4BA074DCE4836BE6C3307546834A6E8FBG99006CBE56BF8F1ECC69A54DCDFA3F2BF7E4109D3FF84BDE0673186664286FE7E3BD7A33165CE75B590F32CE13D2FDCC9A34B7GD0F9873960E90367994FD07FFFFA65D0C32F908E4BE1CD6C9DE8908312A6ABC00928511E1E285A5C5C56F6A95A539AD5BF9045002838A5D56B20FD
	BCC2D88C92EDG654448AEBAAF4C89EDA2A12DEB9AE52F0776E6C001C12F2036EB07772BGCD627A1EFD6B0C2E0FFF62BE127DC458D11EC45FF4096919AD4575DD99FD2E64D53AB72F78F08F8A01D7B518DBFC8C6086848169005728CF9C3A7CB6BCDBFA34B6B9941243AD11C8EBB020FCAC8744A03CB7B783C727F50B3D3292A0046D7BD9D2EF311E3940461D7F426318CDB2CFF29F486A3E17EC7D4EF9FB0905CD36E69E372F9BE43648990C5B0449CECDAAFB6B60CC9F1DA97B8AB14BDE37D4F638ACE4E7BFD6
	E7AF5467F18E45E5B78E61DE7F107A4091F837697E4168BF2374B1931D796BC5CA47F69A70368FF15F706F61EBF1BBD7130A93EECBBB2C4AED31FB02EC8D8341443524D23FDB99E4B2DFB25672B5CA4FB65159DA76D3BA369F85DED3A8371879D72D9F63F78D345783B6838AG3B423945C7GCDE16EE37D177F39C09F73880A569A150290DE040D2D756D0727B826A9C2D7D85504F02F689394C968890AAA517B54795086BDB346B5D05F6F85999F0AE1D191B4C98E436CC2C854C4C5E5FDCE7BDE46BE0AC839DE
	A9A40200D00470F95F77D542B3A8285A65C8C05044F88F467F5007FBB2A278E0C618G5E19DDD24651DEFE50BF82C8B16C10CB67BBAFAA10AE1A1B5D32BC940DF8D19332B90CF63E0B660E15701E9566B61E3C0134AAB93738DE6673BC6B5BEB1A27DD6A45658B4AB80F311FCCE30EBEA7F31F79231F7BCCFB59BD247E310FACFD869114192F244A093173B01D9357455A17277DDFB00E2DF0CE360EBBA6A37C2548F0A421B37B7EFE8277AA8B563189D0E0583871CAAD7359D6C1953917DED4404144C05C62EC1D
	4BE3D57A1ADF54FD67209EAF0C3F0A72C984F2B040839883E4C438ADEBE209FA7239DE1AC7B7AEEB7E3CFBEA168C340BEB81B278DA66DBC7A5F95B0F6714B8A20635F6B1A2068370226A3C93FE1CC30F63BE5998F31A52A39077ED90E82AC1BF6B475894B4CD11FA221AF8C954B4A95C9F67F7D0D9F0B2C762A9034BF27BD1AFF994625E687F099FE326D78E07C53AF946901D3F14462C9894C33068B6B99A5698BDFD9C7DFEC89C57F3460AF1AC0AC604E094738F2BA16E97500691C58A01CBE08AA37543984F8F
	10EA130DB641FEE3ADC400D7B54CE3F374B00F4DA3B73ECEB6DCBBEA37BADBD65D53EE5706D92C248D47E3132DE9518D6B35AF2339C3272B8ECA5B5C067EB2F7A543E36370944E1B8972F2G0F8E7358F0CCA01F679FBDA85A64E010195630F961C4856E11B89C95A1EDE322BBEF58712F93FB93F8BE5D260C7F378934E95014A357A8E85307E33CE65C8E6BF1826D18426CBA29B03B30FEF79A35E501427A91552A5FB59B6F67D5D93F205EAFC7B577FB4B4E7B55E82CDF23467A25EA66FE4F987D8A74FEA44A7A
	39232C9F731912EBD80BD8551D7DD129A12AC91859858D02A592556836307A324636CB1FA3B34A6B48CEA76ED3BE34F782AAC638EFBDD2466B4815C399A47F6E83C96B66E3A3ECED6E1138EF55AE66F5E4E89AE52683EF4D8877019B1366332CC5D565DE099E237CCC1EB27B1EFEA6CF6D06E71F2428DA97A407B13E570DD338DEBCE7F532DEF77C05569D286B7DA037G40626A8527B92EEAC759F96907E790407C7CB3520179A3906896C37208C8AD58G4D562826C189C29D751AC09F9ED16913151098B8B6A8
	0C8854768D9D1844AE768C02C75799FB3631EEAFDB3FC8F7059585476AFC3CCEDCDBF9CEDFD35E37D613B331CA3BD55D1ED6FE501EBA4A6C1CB59A37776483450E6D1FBFDF42F47F91613A6F04BE474738DEEF5ABAE76B2550771BD00D49CA33F1D983A0A74310750C2D45C21654BF20E14678D80E2A6235907E6BD1C4D51D5FFE9D9DDE4C413DAC6B348A61A11C27E5467C573A67185FEDA9603DBA9F5343E367FAEC819DAA46399E5F3337AECC0FEE70D7E1AEC5189E0FD7DD3EB9BD0C7913AAE278FE35036B52
	89FA24983ABC6DBC37B0DDBA46A4AD29A6CC0F5C0397CDFAACD087FD5E649A30FA638DDB62DDEB7DF83C564B9B1FDD7731BBC88E4BE5630977241FD2FA61F862BD6902135F13480D5C62408DDEB797516F8F1B203D8DD084281A001C8D70CE709C16B3F5124CF3AF69021C2C040560259905061E3714E9148B35015E9E19C63B525A00703A2BA816E8074844CC5D53935AEC5C6ED9F7550604767D8963581E7A1C731FABB3F7191DAB27DC7C3B452689FE2E5CC94FFFB219DBDC8DB8BE49ED7BDFFAE6F6C2BBG18
	842C8264G36G0AA639FDC96C6CFC76253E63155B005689E7FDDBE2FDE6586341D976D8189DFFB52F9DEB262C465DBE4BFF97A6EF4D2CF107927D5CD21E4FFEF372823346A54AF3DB4ABB6438B9F9753346A54A8BA448E3F75BA6DA37CC82EF85005176B910168D6D3CA9FE974AF5E23DDDE83473E89B6BA85676BA3F0CBBE1A28A75B5B39921DBA2035FC3D9EC38C8723BD2A9313EAB6DA556F725EA72B223439C66AFA81CDFCB663ECB9DA3D6F72903F44DBF5FDFCE670673D725410AC5294E7B1D360FD85C29
	AE88A1D15737121C24EB46BE9D1A5C56E8583859B557F7406A69E46743EE7ADD2CFE1A1D83B603DEC969CDA6BABB9F56CC716F3E6D404B1C6639A9E0CBEE7FA11B357D07EDD8BF2BF2D4699593EDBCE62BB6F871AC562509A1B407FECF2363B5C16997E76DD19A9D4FF849465FE1CB360F4C56C5B3ECFD4A20BFE8B353CF98742F5A4D74F78D7AE507197EAF03FE5106F75F20B0AEC7B5C246E2E85FBE9B2F45CB21FD846086DC0F311A1C1DC95F9E453BDC1FA204E03917A2E2AF71266058FA879F3B817ABF8A58
	812882DEE38A2FF72E3B8F903C2FA62BD1F8FE794445F3491A941EABAA260961EF2F1A5E57CE5D0A781E19A7FFF0CBE5B05F3E5D46FDFBBB68FCE4913777096BF8BE539A00D6E94228565C7C21A285DA05FEB75DBDF666BFCEF3B20E3F3A081F79CEDAC32F04F696E063A2FE179CFB135FA5336EDDCD5CA967AD6F12795EF76DDB973158A537456FB6195167CA184CEE8717F986785AEDC8CBF1D4196640FD176D6C1CE67C87E670B186B07F44795BE670F13DB107193F5FE966230D5E5147B35D9EBA4AF5ABFAA8
	13843F7B6F2477649CDD27FC7D69719DA6C716BFDF62DE0CF286DC6DF36C19A38155B0269E30CFEF1F03F79504AE32F19FDAB92B16D8186F2D3D49F1CFFE61F1AC77BE4EFCE6F1E2CE7E8D25E7ACCE4C493B6D5CEF3350EE293C2E94E8BE50203D82108958G488794259A5FE56D4749BCB525D70E38A13187CDC7A1536F4A2C5AE5BEB07F78F189770F6C6F2FA56B17BEEBFBA909FFD4ACE1FEF1DCFF96F88FD3BA53EF877DEEF19DF813008C40FAC09EE05B922E5FA42D6730B28EC8DA0B26893D83748B8AD74E
	A70923F1B2A6535769B8C2FFBBE0FF056FDB51F6AE415FF36BE9C94F747AEDA9572B661BF71176A71E359DCE22571925CC1F74257118AE98ACAAE1F65DEAA7068FBDEDFA2F305F7ABCDBF7CBE530BD99F4A42F95069D56354228433AD61BF24CDD0BADF1A47F2E1D6EB0FF57E6BEF5218CF35BC65817FC00F3995C55G754B38BF9DF07E1D791305AFF5847A31D641DF55B4AE7F85E7F2DD1D2E79FF0BABF361B73E9ED95364904E4F6915AE5CAFCD0ED022DE373A50C67463856B2A538F5133BCA876D1B27355DF
	7E4F7CFFGAAD63FE167B9A5F14FBA17B19F75AE0B7BAA33551F76E0FECE837ABA40A6C0BEE08720B4AD37389650886884DC870C8152819900EC40C640964076B4EE5B61D27C3665938675AC443E21D36B82D1BFF0290195C1C2936B7A794A125B78333279ED5CD7C63F2D02ED3C63913DB69CAA435ACC6E6BD3C5E3AFA3E595862DCB75482A04B5A5334595E4697D6CFB0EA653833C64A3A8A158579D2F50EF0CF1154FF1FFA91FDF07FD6556FE72DA79ECBF29AD377613B74B4DFE42E86F14E30C74036D7DC63F
	63866DD303F60A4E0934ABD2C09BB0686F51F5A13DD3C4011C5EC257B5CAE9FE1D7601097629CEBBDF0E7BGAA8F488A9F6BA947BBEC1F948C32361776114230AD06BDBE25EB14A202E95D1F9BB4DCB77BBFBC3156EEF8E67D0138D4051473722E210BCE16BFF2325971545D8EBBF1F611E2E65CAE62C48BF59197B5E097F100F14770C7EEDC948AA8E66FAC4E6AE266B85B39245A7277A8C469F2BD3E16A746D9DEBB6E4BD5200FAD6731D2537E0FB9FC6CD2C88886B5A1B854928E203F350EEBE2BCA7BE520E73
	2B3C0F4EE43CAD6D386EC153187F83D0CB8788343C85E4AD8DGGC8A5GGD0CB818294G94G88G88GAEF954AC343C85E4AD8DGGC8A5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGE78DGGGG
**end of data**/
}
}
