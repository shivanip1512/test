package com.cannontech.dbeditor.editor.route;

import java.awt.Dimension;

/**
 * This type was created in VisualAge.
 */
public class MacroRouteEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemovePanelListener, javax.swing.event.CaretListener {
	private com.cannontech.common.gui.util.AddRemovePanel ivjRoutesAddRemovePanel = null;
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjNameTextField = null;
	private int rightListItemIndex = getRoutesAddRemovePanel().rightListGetSelectedIndex();
	private boolean rightListDragging = false;
public MacroRouteEditorPanel() {
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
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getNameTextField()) 
		connEtoC6(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (RoutesAddRemovePanel.addRemovePanel.addButtonAction_actionPerformed(java.util.EventObject) --> RouteMacroCommunicationRoutesPanel.fireInputUpdate()V)
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
 * connEtoC2:  (RoutesAddRemovePanel.addRemovePanel.removeButtonAction_actionPerformed(java.util.EventObject) --> RouteMacroCommunicationRoutesPanel.fireInputUpdate()V)
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
 * connEtoC3:  (RoutesAddRemovePanel.addRemovePanel.rightListMouse_mouseExited(java.util.EventObject) --> RouteMacroCommunicationRoutesPanel.routesAddRemovePanel_RightListMouse_mouseExited(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.util.EventObject arg1) {
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
 * connEtoC4:  (RoutesAddRemovePanel.addRemovePanel.rightListMouse_mousePressed(java.util.EventObject) --> RouteMacroCommunicationRoutesPanel.routesAddRemovePanel_RightListMouse_mousePressed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.util.EventObject arg1) {
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
 * connEtoC5:  (RoutesAddRemovePanel.addRemovePanel.rightListMouse_mouseReleased(java.util.EventObject) --> RouteMacroCommunicationRoutesPanel.routesAddRemovePanel_RightListMouse_mouseReleased(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.util.EventObject arg1) {
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
 * connEtoC6:  (NameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> MacroRouteEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(javax.swing.event.CaretEvent arg1) {
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
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNameLabel() {
	if (ivjNameLabel == null) {
		try {
			ivjNameLabel = new javax.swing.JLabel();
			ivjNameLabel.setName("NameLabel");
			ivjNameLabel.setText("Route Macro Name:");
			ivjNameLabel.setMaximumSize(new java.awt.Dimension(125, 16));
			ivjNameLabel.setPreferredSize(new java.awt.Dimension(125, 16));
			ivjNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjNameLabel.setMinimumSize(new java.awt.Dimension(125, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameLabel;
}
/**
 * Return the NameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getNameTextField() {
	if (ivjNameTextField == null) {
		try {
			ivjNameTextField = new javax.swing.JTextField();
			ivjNameTextField.setName("NameTextField");
			ivjNameTextField.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjNameTextField.setColumns(20);
			ivjNameTextField.setPreferredSize(new java.awt.Dimension(132, 20));
			ivjNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjNameTextField.setMinimumSize(new java.awt.Dimension(132, 20));
			// user code begin {1}
			ivjNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_ROUTE_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameTextField;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension(350, 200);
}
/**
 * Return the RoutesAddRemovePanel property value.
 * @return com.cannontech.common.gui.util.AddRemovePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.AddRemovePanel getRoutesAddRemovePanel() {
	if (ivjRoutesAddRemovePanel == null) {
		try {
			ivjRoutesAddRemovePanel = new com.cannontech.common.gui.util.AddRemovePanel();
			ivjRoutesAddRemovePanel.setName("RoutesAddRemovePanel");
			// user code begin {1}

			ivjRoutesAddRemovePanel.setMode( com.cannontech.common.gui.util.AddRemovePanel.COPY_MODE );
			
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
public Object getValue(Object val) 
{
	com.cannontech.database.data.route.MacroRoute macroRoute = ((com.cannontech.database.data.route.MacroRoute)val);
	macroRoute.setRouteName(getNameTextField().getText());
	
	Integer routeID = macroRoute.getRouteID();
	java.util.Vector macroRouteVector = macroRoute.getMacroRouteVector();
	macroRouteVector.removeAllElements();

	javax.swing.ListModel rightListModel = getRoutesAddRemovePanel().rightListGetModel();
	for( int i = 0; i < rightListModel.getSize(); i++ )
	{
		com.cannontech.database.db.route.MacroRoute mRoute = new com.cannontech.database.db.route.MacroRoute();
		mRoute.setRouteID(routeID);
		mRoute.setSingleRouteID( new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)rightListModel.getElementAt(i)).getYukonID()) );
		mRoute.setRouteOrder( new Integer(i+1) );

		macroRouteVector.addElement( mRoute );	
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
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getRoutesAddRemovePanel().addAddRemovePanelListener(this);
	getNameTextField().addCaretListener(this);
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
		setSize(487, 354);

		java.awt.GridBagConstraints constraintsRoutesAddRemovePanel = new java.awt.GridBagConstraints();
		constraintsRoutesAddRemovePanel.gridx = 1; constraintsRoutesAddRemovePanel.gridy = 2;
		constraintsRoutesAddRemovePanel.gridwidth = 2;
		constraintsRoutesAddRemovePanel.insets = new java.awt.Insets(25, 0, 0, 0);
		add(getRoutesAddRemovePanel(), constraintsRoutesAddRemovePanel);

		java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
		constraintsNameLabel.gridx = 1; constraintsNameLabel.gridy = 0;
		constraintsNameLabel.anchor = java.awt.GridBagConstraints.WEST;
		add(getNameLabel(), constraintsNameLabel);

		java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
		constraintsNameTextField.gridx = 2; constraintsNameTextField.gridy = 0;
		constraintsNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsNameTextField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsNameTextField.insets = new java.awt.Insets(0, 20, 0, 0);
		add(getNameTextField(), constraintsNameTextField);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
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
		java.awt.Frame frame = new java.awt.Frame();
		MacroRouteEditorPanel aMacroRouteEditorPanel;
		aMacroRouteEditorPanel = new MacroRouteEditorPanel();
		frame.add("Center", aMacroRouteEditorPanel);
		frame.setSize(aMacroRouteEditorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.ibm.uvm.abt.edit.DeletedClassView");
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
		connEtoC3(newEvent);
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
		connEtoC4(newEvent);
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
		connEtoC5(newEvent);
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
public void setValue(Object val ) {

	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	java.util.Vector availableRoutes = null;
	java.util.Vector assignedRoutes = null;
	synchronized(cache)
	{
		java.util.Vector macroRoutesVector = ((com.cannontech.database.data.route.MacroRoute)val).getMacroRouteVector();
		java.util.List allRoutes = cache.getAllRoutes();

		assignedRoutes = new java.util.Vector();
		int singleRouteID;
		for(int i=0;i<macroRoutesVector.size();i++)
		{
			singleRouteID = ((com.cannontech.database.db.route.MacroRoute)macroRoutesVector.get(i)).getSingleRouteID().intValue();
			for(int j=0;j<allRoutes.size();j++)
			{
				if( ((com.cannontech.database.data.lite.LiteYukonPAObject)allRoutes.get(j)).getYukonID() == singleRouteID )
				{
					assignedRoutes.addElement(allRoutes.get(j));
					break;
				}
			}
		}

		availableRoutes = new java.util.Vector();
		for(int i=0;i<allRoutes.size();i++)
		{
			if( ((com.cannontech.database.data.lite.LiteYukonPAObject)allRoutes.get(i)).getType() != com.cannontech.database.data.pao.RouteTypes.ROUTE_MACRO )
				availableRoutes.addElement(allRoutes.get(i));
		}		
	}

	com.cannontech.common.gui.util.AddRemovePanel routesPanel = getRoutesAddRemovePanel();
	routesPanel.leftListRemoveAll();
	routesPanel.rightListRemoveAll();

	routesPanel.leftListSetListData(availableRoutes);
	routesPanel.rightListSetListData(assignedRoutes);

	getNameTextField().setText( ((com.cannontech.database.data.route.MacroRoute)val).getRouteName() );
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GDBF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DA8BD4D5571946280498B498690C314E1424C9CB27CC6BD45BBAD3579A4318D5CDED6324265A9926C3262E29B34BF61C8EEB16CDDC4D667252C4827290D050AB62EBA48808082899DE92C0929190C9A5558377G976E3D67FA4E39F0C114797F3D4FBE77F0B9979F135C357EFB4E7E7763FF6CFF7F7B5B679C12F5A8A2A95E12C5C8FC9251FE676289D9DAC1484BD5BF7B0C73024468E4E2789D865ACB
	9A5392619A86F58D4FC5A727133E49DDD0FE914A4B422313F360BE138CCE1253F093C04E6692C2160788F5B34B391141652450723AEBFB607ABED0A470709AA7227CB72F65EA7C759A1FD33C85E475E31B1D5772B5DE95145B004201A27BD97B55F05DCDA54F2D2E525A75AC0CA0F12F9E372462B806B1B9D987D85F866DEAA55F0E5FB600320E366F517ACCC0F985909A1FC0CEB51D036B453661018F7BA4E7C91F60F2C9AED56C9BAC91EDF6D512CBE449230A55559F8BFD32540E7717A93FCDF0098EB2B808E3
	BB5BF7513188A905F29C6539BCA8AF8A6E57G45687C33A3482F32033DC003F6EE63256397B59B3A6FFE1B4C5964CAC88BE0E3101D5996E97759986EF9ABA50F4E471FE6225F37C05D2E216864FC209A205BC0C1C087E89C7CF9467F403536D72D171CCE49D56BF657B9EC724712CDF440FDF5B554E83CF631CF12ED043039BE55DEEC224FCC447A1DFF64FE4CA711E7783CC735FF0D44AC3FE2C9B171C9CCE429E57190734532A11FCF18ECB9206C8DC3D363F22A6C9B44A8DB8D169212CCE42FF86324E50BB64E
	8E036C9343B8577FC2E3A0976E633578E07CED146FB070D90C2A140F6589280B9D6631517DBE5725B5F189C9AFEBB575436268DACB0303695063705765AFE95C05B8184CD4DD17D5149FE560B3DDAED0BE16B7C05DC5E7F4B20B2F08ECDC3311D0DE839483948714650ACEAE85EAF071987BD54669070C31B6C1D66BBCF607CD14896B9B11398F2E22D71505E617228A2EBE31CB106DC22FC3D40856669B1968035EA97D3EC5E33F8FE4FCA43AC4D9D06D128BC6971C22AA4A8AEB737D4C3D58C696E9ED275DA902
	G271B70F17FB6B39367C6D0546BEE1B200A3E962C7E0719B8A7A3C2970C888340BD734B66FD68AF9B58BF81942A7B21AE8B473BA24A10AE2A2BDBA5E958636EC4CBC80C8B7D7C944D9D91F03F5245FD3CB187F9D6A9BA39CA62630CE5E59846E93077217A02BC465758D68F6665D609474CEF3AF94CB4243EC40A5FE8B10D99A42F44E2A5CC72DFBB3F25E322DE2CBC6E69766B5BAA192F2147A1A6E351B499891F53786F6BF3D51707F33592F45F8894277BF8C5161545EC1D2008BCCA2F4A90E0224D67F12667
	7211DC4D1F28DF1226BF7A7EBD5D16E85AE60FBBBA39822887E88ED09450C6B777E96D486D994679FCFD5BC3E51949C97D9CE5B1DFFEB79B77AFF1C4F4298D22DBF45960C6516AD6E523DCEDFD77CBFA1FE84AF7C3BE28078528687C355938E685D5156D3D3081F7082AEAF78D786AB7D0D90A27576DCBA5DC5626EC34D59A05FC20370F49463554A739DCA21DD43D0BD67F8B3A16C5076884256BA50FCBE57CAD7BF0BD8C0BE3DAAE593A8F8152086070E0DEE2F8E219007EF44BF6A7048A26B6F27B8E2E730876
	DC030FF6427CA3AE2A003A26BBFC4D0E9E61EB36E87CF9327A56D10B591E33782586CB649D366ED65D712DD926534FF34CF57FE71AD3B43E42F4F5AA85F0252006429BCC559E173D0F26C64AD674B5F3F7BF0E9B837236836D12791A39190B75BCAFE90B25DEF2B818EBF51FB76525639C09F7BCA224F3CC00D7F4BFDE4D4B702B6B52FC4A6AEF6421CF9D065C39C1C61F6E986178518A65AA209E1979F5194C7C425A3DBC4A5B65AB2CDD05E256EE1E17378B515AC5E86D36A946F65746F83B86D5132B32F6D12A
	31DD5EB8EF1768E16D72BD2C1D43E3EC7775FB3C5D9A6047G6D74CC550F4DD74AAD44B2E6D8F540E3AF7128F65C99849596155B23D2004A70693B9D3B34B12AC6B88E2D6C4279F4C3B984A8E20447605F27F29C9ABE9CC1E23F3ABFA056DEB942F40B99714520F5B647212F9CC019792351494523BCD6DE4FB36E05350AA27559E92CE973BCEB3FB1C699AFF87F3BF06D374B0A5A8CC9444BE3A2F4BF6A0B7B344656F04B6151BD1A2DB5A0578B44565FFFF499575F32D1365FEE056BCE2084BAE64868E54CB3B6
	DB3B6814C6C46A4192A856F9D495A08C8D68DB823D3409F23FA4BBC55B09A1E1C4203EAF390C496EEA6F90C47EC9FD4E561C4CE07A0BF4D69828B8F1320B634C97B2EBB41D56FFEB8939B819E9B1437DA1E98516ACAF7373C52F4F5F935FC9CE58764737D3186D7F2E5B9E82EDD6FB39DD573CCD536D12697DE31846E4AD77F2D9BBC0CE69981735FF2C46C416FDE0D0ED35AB6A4712C791EFB9717F32572E861441FC77E2EAB3DC6981CC291DB2622D7640A3361A8EF981DD2E4548789857FD90740E50F5FFF32C
	7661F4EF13C5C5F9705CB7FF5872A87A73D1E7B00069DFB74E755F8C3A270FF37D25FBF58F27FFBB2CB1E1A683187E5515D79EC5FFFD544086B07DB75C637AB7016EC1C0F16348DF5C12E54CAF63EC0D466A761D3B0FE70ABEC19655927A2F2DA69467ADD1C6819414D044D1D20F35D476C98D7B24F5781F83AF5E75E157363B53F1ACBB9F7D854D13363B6CFC1422733FC7799386BE336DA265E3B99C6AF65C65183E33G73CF4F8464A420B020C82035C0DBA7F8FE4CBAF006BC604C548C79DEF6890E0EA9E0C7
	4B092F9EC0BF81BE514ABFB900BE267804F08CF8FC545F8F0977265AEE75AB33FEE72775AB71ABB77875E3B154157860BDEBEAEC31BD6B788D7ECC25679E5F3356BB513761D00E82DAF30F7B76BB87F17F0C01728E20747B3067G3581B900426EF37FF667DFFA10FFE94CF482923209CD00B76A7DB1226E0F6D0F69475FBF500F4B6E1B757BEB6F63497B6134FE877D4AEBCD65850DBD1E3C85537A794B0BB51557B47EF8723A267573173743D45E4F6EBD1E3CAD537A794FDF7A2459BAB8FAA21F2F83C33A2A2E
	EE6904F3D023DDF458D85C3F5602F1CF1F4FCCF2CCF62F510849D62515DB908F1BE132D0CDF654248FA394FB5ECAE14F9A0ED19C388D6AC89060CC4ACB380E3CA2A8DF8C62E752E5B4075D06F29050E21D3F0A7291472E88E2B815716B3A7C4F6C5847DFB7A38519545F8E50074938B9CDC64A8C63F33F7B7382BDCBE07961330240E7D6DB017919F528407C4C6AAE584B78B0067189972F77964CFC268DAAB0BB5396D09B566DCB23E383DAD0E842C2502F557DCA4DA6A6E75BCF842728591FC97EB83ADBEBF3D9
	156A4BF49F975F1869B96DC61379E1FBE10D8B63E2EB905B8B63F5FEB365479978EC0FFC5D491F4513D9514936D9BCBF2FBA98587FFFF7505C7F6B8E62DAD1A40F5CA77A7BF84341DC3D4E17491BD551096E504E4B343FAA48836234B90A257DD9DD207E7FF6B050BCB2DF9F779AFDDD2E7337CF611F51795F9FB372ABF47E643811FFD667275EB772AB75395C58623FDE26647CD9D367B2C6ABEF07EB6293D36BF221DC6C47E37BEE189571DA8D5475G29C021C0D1CF7079DCFC6801F8663257944FB05BAEF560
	739507B0868894724EEC0C1D4E8E7D3D9248598234877856598CAB32DC7BD723189F7BE54189D35161967BC8569C6C3B7FBA6F9B846D4301D6GED840A1B4DB166CF2E20CD6DA6181755D777203FF17EDB05DE51214FC3FBCF2A465793BA46BE453513AEA32E6D1943F48D1A43F3F07D8D1E034367701C5A68A27ADCDEB55CEFF64EB42F7FDFE2B27E738B1541EC7EEF3A4FED81FBF76AFEB8B6063C74395149C5F3B9CF223CAAA8F76ABCEF9B7286216C55F9A9B95C0721BAEF53996445C0B9EE2E9E135640B966
	ABD6739C731255BC47FF53BAF30EDFEE8D149BD89E1C6860F9F0D7F0F4F2CDB00F5F325BD330C209B02AD6D7FFA45BEDF542C0ABDD8D8CAB2C217BB9767786F30C7CDBBA66B2A82F850A8D669842FB0CE308282FADA12DC1D7CC1F6B44F616DBF68433351E7F248FC3C48206E0B2EB2EF3195DD09F76A4FD57F95D985F389EDE31B0DC446AA3265423CF70FD342F7E75A975286FEB89467A44AEE3BD7A28C06B4FEC7B51D1EE5B4E9FC59247F74F87FCE635D933E927F6ED6BBAC20A663D1D427C7EDF7D39449423
	51254E6C7F1E0B63330A9070EF88727EF023C19BFFF14874BD12BFEFCF526E23204DEA20ECCD470DA13E7D116512CFA54CDFCE41EBF7FA1C9D76F1118CBE05F28EDDC159A7603E62A91E2FAE517791FD124363F4A9DA3BCE3AE670B9C4145EAEF488F5F64BE23FA84B220D0ECA34760BE97BD5F03FD6EFFF309F1FF57743D90D35F91E2E39B838A773385D87F3385D9573265B1DE4308929895A74G9D516A5C737C6D7EE4AF355B6E72331BD4206C89E89FB20F6747A7261D679FAE3705BDE63FF50F5C0FE120B5
	1D389ED74C7347C6BF267C55737C31518EBDA72C073A52277999F72490E3B39C4A4B0056824582115068647CD01E5B1A8F1712876C371D123B95121F43102E18FD172FBF5EBB7A17D3785ADB71B7AF1015413F0B8FF4B6A98D55561CF65D5AF94400A5F69721FDC1D03794E8B550E620EDC0097339FD8B8F97B37B70930CDAD5957A866913E2EEDC17DD9C7531F163E7367D40E5361F654EE7FA944FB7BFF754G3F532FEE904A937EBC1A971E3E411F3107C17DA12AAF64F5627B1971564E47EFECEACF915D1FEF
	6E617E4C7F6617C943265F459F89604FBD8B34F77D8BFCF9FA6B504E94B6E69C3ACADBD7F9E347606A92C72DFCBBA9E930030B7085760972320AF395D5E7AAAB4E95DB4BCBCFF2BDA2C60D5F5CD43DFB4E74DD94D238A6BFD23B2EFA7208A6F7F88C3681D2DE4A6D9CAE716964ECBB94D82732B365CC99562F7D0C2F5FDB7B0E7A77EB04A4F522D091DCF09E126D7D05455652E2EBD945C9FDEE2E66703909DD7028F3B32B42F77F62D91F9E57BC8F23BF0B3FD7BB319F3B7F29617E779DDFBC164B697CA2E530F5
	B9D99898D385971AE32A50C2F3CC75EC614C6762A505015F75BEDFE8FC574B720ADA0958A1779958B3013A019CC0C14F701CF27AC8A121B945A41FDC368D6039913FC0D1397C0C2240366695BD783B1542A2FC1F55AB292A644471B97FC4914E17AA39A91371BEA8C29F5187782C2946BFD304F16A907BA91BE15DFF2238E4A95895758C473FF5924758ABF51EE708636975BAAF251F6369D81D3765A64753F1BA6F5CDE0E279B4274F3CA37F15F68863EA3EC66E7BFAC3F3CF65318DF52179C3670FD503FFD10B6
	DEE818AF4F31394D29C27C319A78B1C0E4E1F4F2AED0B1D083D0B71083A888E8B150B220B5C01B0136816D84CAFF964E9A4F72D888BE05B923CB9852F6CE76259F0D86E0EAG1C8604AC58B1A434983BF71E7A9446695447BA7F41299946EA13A537A82BE30CCB5FF6EB6B35B34768CF95468BF9F6E6FFB2F993150163F2F6650363B234123EFB05F86A9CF3EB4FB696D66279DB6A6FD7C4BD7E7F24B2DD67B5ABED12E247E7A2ACA7463990B32AA008654B5FD463CCB38CF7074662D230A5920871DE223138914A
	31BA2F0246621EC5B0170BB84EFC7BA64719CD0B4C713591E776AC7261CC643B9779B0961331A90362C96007AC62317DD9B772D6C079EC82BF2376D25EFA60DD3070397E7285227B7C44392301669832C78B6C8B6C4DD727B02077B9D595F81E4A2B9EBCCF972A4C73C7ED55747C51D8E51EBFDA2B0C7903712ED5E16E9C00986F565BF5693CCFF55ECDBAA672EE58ED6A204E9728DE48EF92D1A0678FD03DC6A92FDB63B98C3CCFB55E1DAA0CB5B0F9D012F95F11AAFA8631BB9C2CBCCE5B58DD90FA3ABFA6290E
	F637E050FB0E4E73697D44F90C95FDDFE1CBCE2BFB6AFC969D97C2D9F0584769A7899D5050B54062E0B807478646906339F3F8ECA00F457B5B885DB4FCB0B4B493BEA8BF8B882512631B3E8FFD7DF60A8F5FAF315E57EF734DC27FFE8C975940A6E940G0A98A6AA6E6118E86BA2F3CC44E49CEF705D37DC76493B5B72B0FA32B32FF0EC0F4F3FB4EC98BF5F1B6A6373305172E0D90FB18FDA43E17F8E1FB98F32F9E96A19BED79F7798670AFD8B7F4BBDF3A13822FECD1294192469B6B453600A9DB9D31D704B2F
	A6D8C8E2B3C9E60B2D19A4620AE9A6C9F4C1B51384D8ECDEB7197A4B0351B226FB24F6414FB7C55DEA4832908B8CB8A8329112C65197F40C2C49F75EF9C739C36E93044C980EB1B0D26A1439FC6579337F3A7E97DDCA82C9EEA6F9F8B46FG3C0B91AB5A708D0076C9EAA3596D16E0FB4283B4A82D50CB12220EB944D612555F0E1FD9A92AE82B65EBC1D1C9B6FDE5FF9ADF471EEE9214C195F34E05D61268420FE7C9862BF6882E0153ECE1DC20DA47575E26D6237B89681B69FB712F0F127903342307DB362CCC
	A016C6722EC392EC0DC29F2449E632DB9D14C5E5D0F2583C6E7F2D21836D9A45194293E4828CF7C0B79F0DAD41872B3A52435EBF8BCD5D1A5A886E724A64B4ED09BBCF095E42D6BFA848A0CA141BD57C98D8120B743AD98755E3BF7EA82747C262B5A35175A9DE37CA127B85275DB126125D7A09024E4BFB4001446CD2706398D04E6A279C2F52E514ACDAD77A25EF3C7587AA4357D5A50718B7216B6981D1F21E2E6D6F373BC4FDGF0AD8CDF208F0FEDCA7C5AE4F897E6373C71CEAF04C283C9F01616527FB27A
	DF0E7FE514D3C6B9E565605F6DE4461FFF3D175BCC63C7DFA29881A50DB0096ABF7CFA5D8B7FF8F17BB6302E11A451EF8CE09E52FD2DBE71B8FBC5B9B86ACB856F0547BE0D536446E8716238E9ECF87E4B62B7D396409C156C254F612D7463C4ABBEDB2F56DBA08A39E7D2CA1A9616485DBDBD3E101D8A534D72A31652E9538C7A1F7D488E43DEB934EF1EEE671129F66A3F29FA930E09CFCE9AB1710E3939814F5CA14F31DC3874B9FFCCFCA99DAD5C8C7C3867B8961D1F7681890CD3BA1C0243218A0E61DA178D
	B1CB5D987B6ED37BFEBD9D47D7F89B2DD2FB5E140EFA8F997A7C9FD0CB8788D457FAB7F993GG44B4GGD0CB818294G94G88G88GDBF954ACD457FAB7F993GG44B4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG3393GGGG
**end of data**/
}
}
