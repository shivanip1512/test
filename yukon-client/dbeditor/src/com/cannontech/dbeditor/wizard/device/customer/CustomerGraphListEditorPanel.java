package com.cannontech.dbeditor.wizard.device.customer;

import com.cannontech.database.db.*;

import com.cannontech.common.gui.util.DataInputPanel;

/**
 * This type was created in VisualAge.
 */
public class CustomerGraphListEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemovePanelListener {
	private int rightListItemIndex = getGraphListAddRemovePanel().rightListGetSelectedIndex();
	private boolean rightListDragging = false;
	private com.cannontech.common.gui.util.AddRemovePanel ivjGraphListAddRemovePanel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CustomerGraphListEditorPanel() {
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
	if (newEvent.getSource() == getGraphListAddRemovePanel()) 
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
		this.graphAddRemovePanel_RightListMouse_mousePressed(arg1);
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
		this.graphAddRemovePanel_RightListMouse_mouseReleased(arg1);
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
		this.graphAddRemovePanel_RightListMouse_mouseExited(arg1);
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
private com.cannontech.common.gui.util.AddRemovePanel getGraphListAddRemovePanel() {
	if (ivjGraphListAddRemovePanel == null) {
		try {
			ivjGraphListAddRemovePanel = new com.cannontech.common.gui.util.AddRemovePanel();
			ivjGraphListAddRemovePanel.setName("GraphListAddRemovePanel");
			// user code begin {1}
			
			ivjGraphListAddRemovePanel.setMode(com.cannontech.common.gui.util.AddRemovePanel.TRANSFER_MODE);
			ivjGraphListAddRemovePanel.leftListLabelSetText("Available Trends");
			ivjGraphListAddRemovePanel.rightListLabelSetText("Assigned Trends");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGraphListAddRemovePanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	com.cannontech.database.data.customer.CICustomerBase customer = (com.cannontech.database.data.customer.CICustomerBase)val;
	customer.getGraphVector().removeAllElements();

	for( int i = 0; i < getGraphListAddRemovePanel().rightListGetModel().getSize(); i++ )
	{
		com.cannontech.database.db.graph.GraphCustomerList graph = new com.cannontech.database.db.graph.GraphCustomerList();

		graph.setGraphDefinitionID( new Integer( 
				 ((com.cannontech.database.data.lite.LiteGraphDefinition)getGraphListAddRemovePanel().rightListGetModel().getElementAt(i)).getGraphDefinitionID()) );
		
		graph.setCustomerID( customer.getCustomerID() );
		graph.setCustomerOrder( new Integer(i+1) );
		
		customer.getGraphVector().addElement( graph );
	}
	
	return val;
}
/**
 * Comment
 */
public void graphAddRemovePanel_RightListMouse_mouseExited(java.util.EventObject newEvent) 
{
	rightListItemIndex = -1;
	rightListDragging = false;

	return;
}
/**
 * Comment
 */
public void graphAddRemovePanel_RightListMouse_mousePressed(java.util.EventObject newEvent) 
{
	rightListItemIndex = getGraphListAddRemovePanel().rightListGetSelectedIndex();
	rightListDragging = true;

	return;
}
/**
 * Comment
 */
public void graphAddRemovePanel_RightListMouse_mouseReleased(java.util.EventObject newEvent) 
{
	int indexSelected = getGraphListAddRemovePanel().rightListGetSelectedIndex();

	if ( rightListDragging &&  indexSelected != -1 && indexSelected != rightListItemIndex )
	{

		Object itemSelected = new Object();
		java.util.Vector destItems = new java.util.Vector( getGraphListAddRemovePanel().rightListGetModel().getSize() + 1 );

		for( int i = 0; i < getGraphListAddRemovePanel().rightListGetModel().getSize(); i++ )
			destItems.addElement( getGraphListAddRemovePanel().rightListGetModel().getElementAt(i) );

		itemSelected = destItems.elementAt( rightListItemIndex );
		destItems.removeElementAt( rightListItemIndex );
		destItems.insertElementAt( itemSelected, indexSelected );
		getGraphListAddRemovePanel().rightListSetListData(destItems);

		getGraphListAddRemovePanel().revalidate();
		getGraphListAddRemovePanel().repaint();

		// reset the values
		rightListItemIndex = -1;
		fireInputUpdate();
	}

	rightListDragging = false;

	return;
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
	getGraphListAddRemovePanel().addAddRemovePanelListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CustomerGraphListEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(447, 311);

		java.awt.GridBagConstraints constraintsGraphListAddRemovePanel = new java.awt.GridBagConstraints();
		constraintsGraphListAddRemovePanel.gridx = 1; constraintsGraphListAddRemovePanel.gridy = 1;
		constraintsGraphListAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsGraphListAddRemovePanel.weightx = 1.0;
		constraintsGraphListAddRemovePanel.weighty = 1.0;
		constraintsGraphListAddRemovePanel.ipadx = 185;
		constraintsGraphListAddRemovePanel.ipady = 192;
		constraintsGraphListAddRemovePanel.insets = new java.awt.Insets(4, 5, 4, 6);
		add(getGraphListAddRemovePanel(), constraintsGraphListAddRemovePanel);
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
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getGraphListAddRemovePanel()) 
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
	if (newEvent.getSource() == getGraphListAddRemovePanel()) 
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
	if (newEvent.getSource() == getGraphListAddRemovePanel()) 
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
	if (newEvent.getSource() == getGraphListAddRemovePanel()) 
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
	
	java.util.Vector availableGraphs = null;
	java.util.Vector usedGraphs = null;
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	
	synchronized( cache )
	{
		java.util.List graphs = cache.getAllGraphDefinitions();
		com.cannontech.database.data.lite.LiteGraphDefinition liteGraph = null;
		availableGraphs = new java.util.Vector( graphs.size() );
		usedGraphs = new java.util.Vector( customer.getGraphVector().size() );

		for(int i=0;i<graphs.size();i++)
		{
			liteGraph = (com.cannontech.database.data.lite.LiteGraphDefinition)graphs.get(i);
			availableGraphs.add( graphs.get(i) );
			
			for( int j = 0; j < customer.getGraphVector().size(); j++ )
			{				
				com.cannontech.database.db.graph.GraphCustomerList gListValue = ((com.cannontech.database.db.graph.GraphCustomerList)customer.getGraphVector().elementAt(j));

				if( gListValue.getGraphDefinitionID().intValue() == liteGraph.getGraphDefinitionID() )
				{
					availableGraphs.remove( graphs.get(i) );
					usedGraphs.add( graphs.get(i) );
					break;
				}
			}
			
		}
	}

	getGraphListAddRemovePanel().leftListSetListData( availableGraphs );
	getGraphListAddRemovePanel().rightListSetListData( usedGraphs );
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GADF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DA8BF0D455998EEFD4BA25B3B8C311CEA30293EBDAD0F10A8E1D52C8E7D03156B10CBA451994EDCB5B5426D3EA23B275E4738289728A98A5A109BC8C9AA3088F32593CB61B97C459409A434888176CCDF2135D3D4B5D3BF9C8147E7FF95C3D595CCDGB5B39F771E7FBF677C0F731F7F7C67AEA43FB8BEBB4316CFC8C6B661FF9FE740BF8789691C74577F88DA1C1C14C3CCFF25009D2469E096BCE783
	AF6106241C6D64646B1B203D905AAD50FE8D5EF711F3EF4C5D8FAFB1648401F7F6CA467E7165AC7ED84849256DF5FB736079AAA081E8785CA0237C977796F07AEACE97483091323E9B7B6CAEAC64346350F683968216F5337E3F0767E6AAF91A7DB86FF76EC771E443C3EF593662BC26B9850E7430319E7EACA10BB3B676202C1A42BCBEE69634D7825078CC32F820851E8EF7FFCFCD176A2F68128281B5204BDD3D95034AD712662D704A83CA17DC51958E692ADF566C76867E562CC9415EB6A524B7FA95DD555C
	D2C0768D75222C69051B685C0454C1BBCBC15AFBF6143F9C5A4900D4037EBC2537407B854010A2EC2EFE4B41ED6A3C3C18ACFDA21039AD064D3395E66BB2A5E2731C700B392F53757967F1DC078D40AB68CB4AA9873881C140EC40DE9A974FFF7CB6BC6BAF6A8D2A5F2F866A03C1274F2BFD22FAE59F3C5B6D40613453F2172AF989E1EB6FAAAC3750E7BC30F1272FF99C73C942B191870927EFA7EB6FB9E94B3570495A04A33604BE660B95FD911FB059B745141D52B7BACEC74B7E1C18E5AF1C2EE6E6DB48DE3A
	6630ED831FE7D3FFC47671FEDC6B5B68DA1741FB860F8FC67FB9250F1868ACE6BF24F4EC4F82DEDA3F080D4ED705AEEDD973487623ED16FE181BD4EF7378188EBEDF34AEFDD5B877DC9F13394550E50852171968CC170DD497ECBF89BC07BFA90745D73A9D77F0BC341783128129003CC0D2CE99409190B1D6FB7C3D2B0CB13724694E3062734A9AE1E3530F6F0127BC24EBD2EBA024CB01AE39C352946922CF8E915EA72392FDF0F154381CCA0C7DAE1051AC87E4CD5295B5G33CBFED91735906B332DF2877651
	E44A75A8FE99847803C44C7BDA65AE741F9452BF8BFAA5DD0E74E07C62CADC13812983E60489601D796520837DF58E6C5781D38DBF4C23731D14B54896F6FB1B2A7607039E3404A487504F8BE86E188B6FCB8242472555C8ABD013F20E28E21E47AAF71A66F1A9DD283E248D0BBDF6079D7334CB95B173DC2708995756C524FC4D894B1821312232D8192AC66F1DFB681C2897EBD759BB2FF19FCF84179A5903CC0667BB1711E9FAE77E7DE18D2ED5A248DE85D8EF78B84FDE42E256A905E4912527B488B0599B71
	B853B3D8154FF55E4CE327086F97467F0672C9907685408D9882448705AFEDD551F60A3936CCE00F10B5F15E5DBBC6867AA27BAAE488DDA652E39758016714BCA087F4179C1483DEF889F15E66AA1C036F63EE5598330D5203306F9BE023058C7A6EAA5C1B122EEB4A4530AE1F11F5DD8974C478C5D4D6A8FCB198C999C25601AA34CB9D04FDEF74BFDC05FB26CB8D84E43AF846904EFF076ED959A77BC16986B59C50997D2883633EDF9E66B963C38796C68312AF0C79075591CBA474E1D0D37C90921842C879A5
	5C4F77959618FC348A569B6BA1823C7CCBE2EF8E6E97FB7340D7371295678F5A2C4E16390BDCB67D925B9FB3AFC576A65369D035356E65B4F7F0FAA8135296513AEB3CBA066D0BD535B8E78248DA8EC83EA476C5C2B572C56E619B22C1757918DB8DFF5FDC339D57C73E9416A1E5E312BBE978F0FE4D4EA8DE8777A763AF2CC1FF7ACC79F13E067E3C27CA540C2920CF96504AB46653910D7904759BF108FE6BB5560F042C7AB5D60BFE1E906B67637D16074C7D365608FE79BA6BD72E7379F4F33FB5C63FE45EAF
	1577BB9EE67DD83C641E47BA442A6E6C89AB95E1DD412CAE6930D102E15DD4DB6EAF5A4467189596B5642992DC271550DE8B589096F17558D6D1C34E690FA7A9373D9133EE4E9BE03A9D990844D54994D1C36E2CC719C160458D0898A82EB51FE37521105A2550A3142F5F6FEA4DF147E80F573E824FEEC58B692D10980644DABFD50B7A62994B493C66D8CBEB9134F58E48DD8DE0FBAA2B543C27328659D96900279B40E23C41510839436BBDAD7B558119FA30821A4E302EC379C183753CC49FEED96BD6B53F6C
	AD6B1386A46A7B0AC6CCE027AE76C1C49FB656EC65611DCCFF192E8AAB884A8EF7089AF1412EBA2E536ADF4CA30EAB3BECD6B57B4CEDC5365EC16667590391FF0F5C1D13197A65CB394C76C58EE17BDA6813BFA46C4A2BEE99EB17C65F2F43B4A6ABD0ADE435011C29062CB555759632141EDE9D5345A7EAB8A41F77633FEECD8E05A676DFF738951EBD18EF6AC7CDFA7E74B81336D94EDA05EE67F172D10D50FFB9683E41507FA7B54E2B537FB44429B41E81CCFF55FD629A74B7A69D47G267F7F8C7D4B0713F2
	3C43C27F076B6A2ECE7F46A1C50F29BD53FD0D3F659AF4679346541C5593E5C551F7297943117AED4970587B943BE3BC407255F074BD6891CACF9E0E3E87FDD6A26EC14F822F65ABD1975F6A44BC10886DD50094C0BA2060B2645A4BA2CFC9F5474884770ED6483BDAC0721D99D5C8087A3F9E6542594F5BFB6B511F746CA7222EDAD7956D874F65513687235AECDC5A18F195D16DB9D16358DAF6E4CDFCF60CDEE4F6F6B8CA45F709444B626C9829C45FBE8B6D0D007C91615B179B709CAB07F68B6082E084908F
	D88EC899917EDDD4DFBD11FFE968F85486203540F95E90DDFF997E3853F1FDFE3CEF4C38E8BF122F2D46695557A7EF7218F17B225A8516729CB557A76F4418F151720ED84A4B2A3DBEF9BB470C0B165792A50F5DDDEBE9EDF281F8BA00518A0B69FD965ACB3E96F71D87CB301ECEB65A0F53B656CA2C7DC5C92CBBDF7257E3F34A35022FFDCC99195FF2FE845B9BBFF3463E8B5D65343E8B7D52E9FD977A15F38723439C66AFA4027FDB6778F725C71CD6F725A2EA6F03FB36513961388D514D0A05A7677DC55FCD
	AC6ECC1FCAFE197BEA97F95D311977E95455068F8C9F57170E771DAF5DE2AD59B910C6F3D25EB76C9C48B068AFD0FA01094E4E074915625B6E81604D7CC664264D2E587E5F6E32767F9E975648A1B52CF54951BEAEF295983CC896EB55E5BF3A035F4368F8DD52FA64B1EB74AE9D4FF83146FF640A350E4C576BC6797AC303FEE73519FE542007EA4C74778DFAD52D197E11C16FA8423B2DCF9AD643BAA11DD7503F7EA2D1EF2702EF3300D68E283B426AEEF6A65DB5087735EECD72033AE702F29779A08E479EAB
	96E363217FD2C0B2E083A0BFCE54B933BC787B04F18F9DFDF4715C52D9AA7248C81C48934FD792A356DEB63D4F2C7C2E3F990D4D9F37FE2FB2D8DC5FDCA462FAB5581CB5C9787A6885BC1B6979CF8BB4E9D0375B1BB5456B14FA5A684A31737EE91A0FF13CE312B86F376C43B9F5E84F82AC1CA46E0AC30744DDB17176F924AD6E2465DDB145733EED79A436EFD3A6C56EAE896197F3194C3345C2E66164249C4FE4244DA84EB74D01EB2E5A58994D78770D62E37CE36E09707FBE0A0F7ADE49B4730BCA4CFC7451
	BBFCBC336D610342360D8FA7905F3D15B16F4173A7735F8D78535D311F9C3871255C72A9A8C7ADF50D33E619D1C807B972E05CEE5EAE017790881D14A1E26816B1757355455E9D57B9EE5DB50FE3F977E996B3D32273711FA9BDEECAF4BE5E314F782D8DF8E9D3C4CD1953043940B3B5A9A788188908872CG2C1DAAF276D32EA3E402FA5223865BA0297BCC47A0336F4B626B7BDD69B739A2BE165E3F00AC1B7EC24616987111B615454576E96C395E331F5219FDBFEEC67B6A00F78E9087188BC8842C1CA66CAB
	EDACE776615736FADD173AFA699792E1DC07A28FC64818C80B69B8C2B6917117D6EBC5FBF29A7EDE7B582744306B0FF9422E42BBEFA12EA7DE20BF6EDA59D5B81D7FCEB4BD32275777ED4CE5FEFD864BBF9EC32926775776FD7FF9764877AA03FFFFEE0ADDA76CE9322E938A1B2C6B34B71B462F435EEE0A7D4D7AE813791BB50B297527B037AD04F5D981F896108E481BA1626957CDEF32F8320825C6EF8F56A9780B19AE64AFE80EED6B9D4D937F4EF6F7B3FE433B286A3A6A477985FDD9B32E172E86A911572C
	4D68A37A4102F56574871AB1EEFDF2B7A533D8FD748B73EF7C5B67155AC4CE09DE333299ACC6BF189109D5662B1B5AB1BF7782FDAAA0813082108CC8851019D0F3G4AGF500CE008F90871883D888D886D8BDD378F64FA97CF655A17571AC44BE10D36F82119F3854411A24200B39FDE927E27B783F27A676B1F153EF27609B4FF010570559EE2C4B546E6E10EC2C65AB6E6D862DB564D6C38A56134C977FBACD8CFD663B8F46328322648414906C430EC76AB1467C34BD368D3737CFEC4362F66BB83937FDEC1C
	5C5FEE9DA7AB5A4DF1426FB96D38C7FA40771DC63F078C5AD90376A81D93E91FABDE3D57202F21FAA13DC5C601027E872A57A025F5F2DA0A09F616537E540E6BGA6772A1A987B37F63C3FF6ABBE9FEBBFC77BA881D896439F7F26BAAAC14924F73AC1C33D597F51C95B0F8B17788F129D5248B6D15ED5345249D2860E59B31F39AD53C632DAC98EF3EEAB49C28F3512EC6A40D612894E9D429F30D1A99410435E59BEBBFC453C4F522795D87E5604C83D016DAFF2E374BE2BBE046B62GFA670DE22F7C60603B63
	4458993F647369122F3FBE6045F8F38E6BF2A4A7BA8F617CA15107B3992F75906A5DE79A73FFD0CB8788BB9325BCC48DGGA8A5GGD0CB818294G94G88G88GADF954ACBB9325BCC48DGGA8A5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGFE8DGGGG
**end of data**/
}
}
