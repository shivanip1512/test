package com.cannontech.dbeditor.wizard.device.lmprogram;

/**
 * This type was created in VisualAge.
 */

public class LMProgramListPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemovePanelListener {
	private com.cannontech.common.gui.util.AddRemovePanel ivjAddRemovePanel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMProgramListPanel() {
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
	if (newEvent.getSource() == getAddRemovePanel()) 
		connEtoC1(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (AddRemovePanel.addRemovePanel.addButtonAction_actionPerformed(java.util.EventObject) --> LMProgramListPanel.fireInputUpdate()V)
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
 * connEtoC2:  (AddRemovePanel.addRemovePanel.removeButtonAction_actionPerformed(java.util.EventObject) --> LMProgramListPanel.fireInputUpdate()V)
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
 * Return the AddRemovePanel property value.
 * @return com.cannontech.common.gui.util.AddRemovePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.AddRemovePanel getAddRemovePanel() {
	if (ivjAddRemovePanel == null) {
		try {
			ivjAddRemovePanel = new com.cannontech.common.gui.util.AddRemovePanel();
			ivjAddRemovePanel.setName("AddRemovePanel");
			// user code begin {1}

			ivjAddRemovePanel.setMode( com.cannontech.common.gui.util.AddRemovePanel.TRANSFER_MODE );
			ivjAddRemovePanel.leftListLabelSetText("Available Load Groups");
			ivjAddRemovePanel.rightListLabelSetText("Assigned Load Groups");

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddRemovePanel;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	com.cannontech.database.data.device.lm.LMProgramBase program = (com.cannontech.database.data.device.lm.LMProgramBase)o;
	program.getLmProgramStorageVector().removeAllElements();
	
	for( int i = 0; i < getAddRemovePanel().rightListGetModel().getSize(); i++ )
	{
		com.cannontech.database.db.device.lm.LMProgramDirectGroup group = new com.cannontech.database.db.device.lm.LMProgramDirectGroup();

		group.setDeviceID( program.getPAObjectID() );
		group.setGroupOrder( new Integer(i+1) );
		group.setLmGroupDeviceID( new Integer(
					((com.cannontech.database.data.lite.LiteYukonPAObject)getAddRemovePanel().rightListGetModel().getElementAt(i)).getYukonID() ) );
		
		program.getLmProgramStorageVector().addElement( group );
	}
	
	return o;
	
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
 * Insert the method's description here.
 * Creation date: (3/13/2002 3:15:27 PM)
 * @return boolean
 */
public boolean hasLMGroupPoint() 
{	
	for( int i = 0; i < getAddRemovePanel().rightListGetModel().getSize(); i++ )
	{
		com.cannontech.database.data.lite.LiteYukonPAObject lmGroup = 
					(com.cannontech.database.data.lite.LiteYukonPAObject)getAddRemovePanel().rightListGetModel().getElementAt(i);

		//if our element is a LM_GROUP_POINT object, do not add it to the newList
		if( lmGroup.getType() == com.cannontech.database.data.pao.PAOGroups.LM_GROUP_POINT )
			return true;
	}

	return false;
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getAddRemovePanel().addAddRemovePanelListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("VersacomRelayPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(416, 348);

		java.awt.GridBagConstraints constraintsAddRemovePanel = new java.awt.GridBagConstraints();
		constraintsAddRemovePanel.gridx = 1; constraintsAddRemovePanel.gridy = 1;
		constraintsAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsAddRemovePanel.weightx = 1.0;
		constraintsAddRemovePanel.weighty = 1.0;
		constraintsAddRemovePanel.ipadx = 159;
		constraintsAddRemovePanel.ipady = 174;
		constraintsAddRemovePanel.insets = new java.awt.Insets(25, 2, 38, 4);
		add(getAddRemovePanel(), constraintsAddRemovePanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (3/13/2002 2:17:21 PM)
 */
public void initLeftList( boolean hideLMGroupPoints)
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List groups = cache.getAllLoadManagement();
		java.util.Collections.sort( groups, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		java.util.Vector newList = new java.util.Vector( getAddRemovePanel().leftListGetModel().getSize() );
		
		for( int i = 0; i < groups.size(); i++ )
		{ 
			if( com.cannontech.database.data.device.DeviceTypesFuncs.isLmGroup( ((com.cannontech.database.data.lite.LiteYukonPAObject)groups.get(i)).getType() )
				 &&
				 ( hideLMGroupPoints ? 
					 ((com.cannontech.database.data.lite.LiteYukonPAObject)groups.get(i)).getType() != com.cannontech.database.data.pao.PAOGroups.LM_GROUP_POINT 
					 : true) )
			{
				newList.addElement( groups.get(i) );
			}

		}

		getAddRemovePanel().leftListSetListData( newList );
	}
	
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getAddRemovePanel().rightListGetModel().getSize() <= 0 )
	{
		setErrorString("At least 1 load group must present in this current program.");
		return false;
	}

	
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
		LMProgramBasePanel aLMProgramBasePanel;
		aLMProgramBasePanel = new LMProgramBasePanel();
		frame.setContentPane(aLMProgramBasePanel);
		frame.setSize(aLMProgramBasePanel.getSize());
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
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getAddRemovePanel()) 
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
 * setValue method comment.
 */
public void setValue(Object o) 
{
	com.cannontech.database.data.device.lm.LMProgramDirect dirProg= (com.cannontech.database.data.device.lm.LMProgramDirect)o;


	/**** special case for the LM_GROUP_POINT group type ****/
	boolean isLatching = false;
	for( int i = 0; i < dirProg.getLmProgramDirectGearVector().size(); i++ )
	{
		com.cannontech.database.db.device.lm.LMProgramDirectGear gear = 
			(com.cannontech.database.db.device.lm.LMProgramDirectGear)dirProg.getLmProgramDirectGearVector().get(i);

		//we only can add LM_GROUP_POINTS group types to a program with a LATCHING gear
		if( gear.getControlMethod().equalsIgnoreCase(com.cannontech.database.db.device.lm.LMProgramDirectGear.CONTROL_LATCHING) )
		{
			isLatching = true;
			break;
		}
	}

	initLeftList( !isLatching );
	/**** END of special case for the LM_GROUP_POINT group type ****/


	
	//init storage that will contain all possible items
	java.util.Vector allItems = new java.util.Vector( getAddRemovePanel().leftListGetModel().getSize() );
	for( int i = 0; i < getAddRemovePanel().leftListGetModel().getSize(); i++ )
		allItems.add( getAddRemovePanel().leftListGetModel().getElementAt(i) );

	java.util.Vector usedItems = new java.util.Vector( getAddRemovePanel().leftListGetModel().getSize() );

	for( int i = 0; i < dirProg.getLmProgramStorageVector().size(); i++ )
	{
		com.cannontech.database.db.device.lm.LMProgramDirectGroup group = (com.cannontech.database.db.device.lm.LMProgramDirectGroup)dirProg.getLmProgramStorageVector().get(i);
		
		for( int j = 0; j < allItems.size(); j++ )
		{
			if( ((com.cannontech.database.data.lite.LiteYukonPAObject)allItems.get(j)).getYukonID() ==
				 group.getLmGroupDeviceID().intValue() )
			{
				usedItems.add( allItems.get(j) );
				allItems.removeElementAt(j);				
				break;
			}
			
		}		
	}

	getAddRemovePanel().leftListSetListData( allItems );
	getAddRemovePanel().rightListSetListData( usedItems );		
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G85F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D9FDF0D45595CFB0B899C7E752B6C544CC0D0AB42D282869C8AD35B6459114D62DB06A14F65229D379A3F6780399D179636EEEF6931530C1505A02B5CA506024A98AB35238A96403B1ABC1D3320628D8F8493EECDE72F65F66FD6C971B50F36EFD776D4BE6B7D1C6F6664CFD771CFB6F3967F74FB9773C37E4779B65EE07F3B7A18EB7B1FF9FB888A9EEA224F34797851CD7A0D6F9086D77A650CEF21D
	DC8BED89488EAE2C72B412EB7B6B213F947A8F97D5F97E8A4F3BC8F9FF539B7010C74F56FF90F266E9657E39751CB6381EBA5AEFB236C33B832882F858EE91D17F9163B5133F4E64F3F2B889299E45B18346DE13F7847A7DC0E5C0ABC65978B5503EC0B5AFECBFE20EBB731DF2326501F71C8D380EEDCDCEADA1B6374FEC1B48ED0EADA15415267B44B9C550DF8504463BC8F360A8343E7E0950FFC614F05B089009A891DD9C99EB0BCBA9C18D36854518B4A2364961282A04D4A15C5E5EF7321FBD76C91A5EAFC4
	C419C447D047EA231E2E8976C0FF9B65BD540EFAAB61F99D501396FF510748BFA6D5F9060022923735639D1FE94B50795B483647A2AEEF9EDB0BA5E6638AA9E3EB2971DC5D5F6839DC9DC37CEBC056B8DE65E9866A859A83AA865AC37D6187463B50F68E6B5DCAB82CC4BA2351AEB9281ED402228C4F6D6DA0B1F9274491C58D92424EFC01511AE3BFF3911BD7720D67B1CCAA8E7173DFFEEA9959F86789E7DD8ECCB6D69CF4160FB3ACD60EE7B0E13A5766557D7878CC7F1C297BB4316B2E39DCF139F3682E5CF0
	40D9E32EC3A6B23A8FCE60D9F75333EE02E707699F0CFF1272F59B1F796A866AB358AF825926896E9BC3BB78DE7AEA171046777AF26230382A5359A733BD84656C3D3C684335CBE42653ED6D65954ADFEA63333D1451BDE2FFA54876062BBC4C3F4A93983B8550AF832A84DA87D483D49F01F1916EE3CF997F7A1ABE56AF28FA17A149C1D1A5ECEEF96CD5E845042E8A7E08268B1191F1D0D0A5E1D896B5E20E79D18CB1981EB16FD66A7BA32063A3B1A22A02AEA991D8DD880B3A28EAEC4CDD311DB8C69529B4A0
	05C5D0900E923E6EAFE23B201585CD7FAC9A94F4B1B302496F0F61194404C1D8919600E7064B2F1308178F6C8FG85AD9C3A62385E89D105F481A9C4D1A60CE8GADA1159144391A660E84BC17C4B846EBD3485B0AF2052F130C3FECDB27C79A416D8BEA1247187FC34C4F4D8A77193F8CF11F69E9380934EE78B8274FD03942FC650C129DBB83F4CD5C976B7F2EFD689B467157A55CB75331651269F0D9E775C19A4F2A88F416835DEBE1DC99EFE2BE5BA5E8A27752CFD5F0B0B118C11C6D3351D7EF62096BB25E
	9E4AFB9C56A3D1EE4F8D9F8E5946FD5BB6B19DBF23BAF0FD77A550413079F1826FA3B1A6C6749EB1AAC602702019325B9368F3E63C0EAA561C3BA8BF8A715D8D8125D97C1FA7B08685DDD725E1C39787C4DD17A2210C7CBE2ACBB30623195440F52DCB200DCA9C625B9A7FC882E3E3C409C4C4FAC85694D37EA80DCDD196433069EE4508680C7FC4927DFBC2CC1A396177C9ACFCE202ECE01EE1F5428A61A534C11542F074182A4815D10C5BE72CBA872985F8D86F1C03D60F72980C6F63B1581C3A1E5CFBF63FB3
	579D3278269E677AA80B3DCD51CC8C32BD7DA615FB6F6BE90EB0799A5B6B779B91874CCC8254B427C4D9C832B245747B68942ED5BF89F99A6870A4777B7E73A8673945F478EEC516991C964E9D69C6BC97F15290A1A5E392BBE1E15713FEB9CBB6E8624864FDE944D136653FED1374AE7471DA308F7AC12062C9066546C906859B77EA879F572B32F1675419639886F5E7713E4FD557058C294D50A54C1E028E0E9AB5D89147EAB8A1D6EF2ED12972DA6DAA6AB7B5502F55GB30D1F6BA38D3CD6AB1DA8A755B77E
	BDEFDDFAD8E3FB9B52B2675AF4992F556E1EC61D0BC1D621713338BFED3FAFBAB5CD991168D5E562981B321FBB631D1FFA915AD1C955F4BF84E602E3DEB8057B453B4CE41BF7FB4C476F76D520F78B9073692E145D27DBF5F6A78521CDG39D80C780EE36C86032744309293A902ED506DB2F49D2EF96AB0E7855A740B6A28220645E04B389093A876ED47B101FCBABC8E1EF540BA335587DEE67B976929300B376540A02F45EE58F54C5C533ADB17905F05DD4EDC35F13177F5E709EE5601FA866F749D9ED74D7F
	1E2FE3369F6D6036579AD59E1F416DDA5651BB5BAE15BEDF04E9ACAF0F4632EBCF5D48541C45466C7A1355E48E9A73E5C6F65D789265AFB7326B466FA5F95D388AE4CDB1DEC778891EE791744B01D682BD8C349928B14E7D6D0569C3E41EBA4D8F7123C684F9E0C6C2667552340B65D0337F2BE9B74F21045FCFE9DFB68E2D71193677E675593CABE64DEB4B6A4BD97318BF8D564E1F83BAE71CB44B812994FF0FAB0A739C301756642B203F9E28A64E317D218373D1ED827086BA8294GCAG15822DCCF0FCCFDE
	68188FDF6AB881259BF83D109F3B336FB18B0F639D970763402CF959B8BE1E18B90F5526D3B4A7EE86D9ED92EAFD4A7BA2013C96689FCB729A27AE0577E8506A6FE47DC95E3FA6192B2643715FDE8DF4C7CE9D5FD64D454E729449DF83FDCEF257C0E7C96E9AC8A4BB999F56303F81F1F9184CDDA399A4D70D74BA35F96D2BDE3A3626099AF5AE3C70CC59D37AABA4C72D74893CA419F86DA2F77ADEB0479C57156E77AC0CAF2466FA0FAF48F11EACEF155098AACD323C65307857D0FE390D4F7259DB6D7C1B4DF2
	10B526F8AC5D6E480F7FCAC7EE7C6FF1605D2CA906BAA2E6E3FC1F63B5CB1609BA3FAE06918E337E2273F5C18D09334EE8831D4FE479667F41116F9C9956698EBB566FDB7C52985622D041A906CEC8F58AF139B9466FE79D7AD782D5822DC931FB1A653E5B63D8E70D42CBAF2899080AA3E455F91CBB1960F3DB206F83BA831486AABB4F6F40488E144F4C0CBC97ACCA71382FCD7338DE54CEAC5F384E765CF2495E61B2715EF9C9F5B0BFFCB4467DB0GB6DF1966983FF78EF3BF3DDF6875AF447576760FD4A958
	A5047A6809317BE4CF1C3FA32ECE737B449F47B537C1FF77D41527F50A57140937F9CD39FC599252D7F0A2E7CDD99DF85F59B34562ECECAAD363D4984F55B11DD7A7384E25A05FB8053C7B923BEDEB60D97F54496EGA6FFE6069C7D95F3C5C6FEA0E91763FEBBDDF67917B3640851FEF3BE336D417D5C36ED8FD69079A77F4EDBAFB7CFB31BFA4D36FFF09FE93E62793A4AE954F3CB2AE70EB3F3E5D1814CA985FA456CD74073D1D07A15037B5093336A332F67FBCFDD643C2678B71D4772641E847519696C7C59
	CC79759732736727F16E373BC1163840EB16DBDD1883B6C2FFABD0E3C11527852897A8D840F3EC2173A01927DE89A851BEC842326D5AE27615A5AE6EBB6FAF6B38FFD45EFD83D9F979330E177278C722007945E2335D9458C7794C3ECEB75A3786E4CF825597C2ED86F498282F105B3756594A6CBB9E1474CEDD97C646681B94B7EED0926399B6A650B768BC4019705F662339F8678A71FF93DA0A1BF67DF1BB37EB6F4D57121E471EF56C4BE35752C273BF1442CCCCEF9A5FD647F0FD93DF5DCC9FFA57767CD97C
	52675948A555E1FEA7F2653F576FF1653E57D73BF257D58F38662E1BFE6B4A7FED6989177D5B92732912864CED2D8BA0CE00E420822052855C1FC235EFB1FF4A61CB4703A12CAB708B364E75FF6C4EEF6BFF5D73FF777E420D6F7A438A3C1206F1FD4EBF6746735215A8E51AB5269BB1D22550184E061A7C89B77A2DAC0ED2B67355073E347F57563864CDA74FA959E736E2817351D58BB23E4A30BA64457C3C99785BAF2B729C846A85BA832483958315812DGDA83D48D3485283E284A339728956898D02008E3
	FB4FF67CC6B0A80C1BD988C3D6D1A93A40B4AFDC8A30AAC88831E95F62067C98DF57B0BF46958D749B8BE093C8C64DBA6E16862C471451D1CD344E720E06C60B67577A95CD427A0FE1F18D0A4CF1BDBB766733833C64E3A8A158E70300903266FC644DEF43A75E79EDB86D4D6DA7DFF9E77B4930B7371F0CF96DFEE23E17F8B1C6C2007D10B5CE35F81FDB3CB8DD93F9272520BEE67127683E105FAB22C24E3F0C6EABCEF9C3A62F58467B5C64DD6545F3G134794154F7D2E975FB7C7A5D9E67DC5F40C9401E331
	7038166ED10A8A36FD17DBBC5CB77B2F5CBFF18A5A65CF93372692AFAF6F5A7CF4316A5821F6571FEEF4B9C92D1FF89838FED20B8879091B8268A7AEGB701FFA83D8D84F59629E549044559018B76B85B3A70351C5F04117405AC3E0A97E6475975E1BC1755402FDE48E3654F72BF6770310130A04B3AA0CFF4C60268EFDDC9DD4C6444E5E1DCDF63E3CC216977E15C7738ED4E7F81D0CB8788CD00B613A48CGG30A0GGD0CB818294G94G88G88G85F954ACCD00B613A48CGG30A0GG8CGGGGG
	GGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGDE8CGGGG
**end of data**/
}
}
