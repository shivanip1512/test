package com.cannontech.dbeditor.wizard.device.lmprogram;

import com.cannontech.database.data.lite.LiteNotificationGroup;

/**
 * This type was created in VisualAge.
 */

public class LMProgramDirectCustomerListPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemovePanelListener {
	private com.cannontech.common.gui.util.AddRemovePanel ivjAddRemovePanel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMProgramDirectCustomerListPanel() {
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

			ivjAddRemovePanel.leftListLabelSetText("Available");
			ivjAddRemovePanel.rightListLabelSetText("Assigned");
			
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
	com.cannontech.database.data.device.lm.LMProgramDirect program = (com.cannontech.database.data.device.lm.LMProgramDirect)o;
	program.getLmProgramDirectNotifyGroupVector().removeAllElements();
	
	for( int i = 0; i < getAddRemovePanel().rightListGetModel().getSize(); i++ )
	{
		com.cannontech.database.db.device.lm.LMDirectNotificationGroupList group = new com.cannontech.database.db.device.lm.LMDirectNotificationGroupList();

		group.setDeviceID( program.getPAObjectID() );
		group.setCustomerID( new Integer(
					((LiteNotificationGroup)getAddRemovePanel().rightListGetModel().getElementAt(i)).getNotificationGroupID() ) );
		
		program.getLmProgramDirectNotifyGroupVector().addElement( group );
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
		setName("LMProgramDirectCustomerListPanel");
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

	initializeAddPanel();
	
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/2001 4:56:13 PM)
 */
private void initializeAddPanel()
{
	getAddRemovePanel().setMode( com.cannontech.common.gui.util.AddRemovePanel.TRANSFER_MODE );

	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List groups = cache.getAllContactNotificationGroups();
		java.util.Vector lmNotifies = new java.util.Vector( (int)(groups.size() * .75) );

		for( int i = 0; i < groups.size(); i++ )
		{ 
			if(((LiteNotificationGroup)groups.get(i)).getNotificationGroupID() != 1)
				lmNotifies.addElement( groups.get(i) );
		}

		getAddRemovePanel().leftListSetListData(lmNotifies);
	}
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
	com.cannontech.database.data.device.lm.LMProgramDirect program = (com.cannontech.database.data.device.lm.LMProgramDirect)o;
	
	//init storage that will contain all possible items
	java.util.Vector allItems = new java.util.Vector( getAddRemovePanel().leftListGetModel().getSize() );
	for( int i = 0; i < getAddRemovePanel().leftListGetModel().getSize(); i++ )
		allItems.add( getAddRemovePanel().leftListGetModel().getElementAt(i) );

	java.util.Vector usedItems = new java.util.Vector( getAddRemovePanel().leftListGetModel().getSize() );

	for( int i = 0; i < program.getLmProgramDirectNotifyGroupVector().size(); i++ )
	{
		com.cannontech.database.db.device.lm.LMDirectNotificationGroupList aNotificationGroup = (com.cannontech.database.db.device.lm.LMDirectNotificationGroupList)program.getLmProgramDirectNotifyGroupVector().get(i);
		
		for( int j = 0; j < allItems.size(); j++ )
		{
			if( ((LiteNotificationGroup)allItems.get(j)).getNotificationGroupID() ==
				aNotificationGroup.getNotificationGroupID().intValue() )
			{
				usedItems.add( allItems.get(j) );
				allItems.removeElementAt(j);				
				break;
			}
			
		}		
	}

	getAddRemovePanel().leftListSetListData( allItems )	;
	getAddRemovePanel().rightListSetListData( usedItems )	;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G6AF854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D98DEC9C4595F6A089EE9528DB20CA832A8C1848AA9124542DDCC42BF050AA00AB82CD44CFD355E9D3EA51D0CC1B16C02CEAFCE71FDDB04490C38DCD04A189C92A54754F79FCB117603FA3B6B9E3ABB90C2134497AEEFDDEFB6F76323B671F7C103EB73333F7BE6F59A1A2A7FD1A1DB7BFEF661B775E3C5DA3B5EFE617175AEB88A9ADA7462F3514103CBAC2767E647115DC16A15A1CA469779660E532
	7F7DB2A833206D33C5B6E7B5292A2904FA8E54CB965A1CFF07676D6418577FA6BC2451339B5A4637DCF5455CFACE37F0BD95347EFAE39514AF81F2C10665E69175B7B6569A7282C34ED1EAA7E44B887671B76EB0E49E288FG72G2BC7D87FC2A87FC6B5AFEA73987DBE7BFAB659FC5FDE7B0BB8CF521C9C6DE1B6B6E414F56436522AD15495276B44B157C35BDAGEE5EC1ECBBBF0252BBB09EBE9CD4A275C1A19AD5223A989C2D1F14CE8BEA28BEA4CEC8C131DE0E44D4A52C8A1136B6FF5FGFB74C5C5B5BC6D
	1B8A0E8A5130589557F4A5A22AFEC953870428A8433C19D4770F9AAB29AEC28620DEA3214C65467594C0FDA3204414EF2572A1F80E81B2A54E41213DDEE30FC3E7EEA3A58FC79D5B52F010AD313D2F16929CDC93FF2E62F5FADEF73871DC4A46EC4EFDGAF60A4A083108D788735133BDA7E89E547305E25C4A2CA34A3966B14C3EA1F9292E5F8EEEB0396C3F6CC8CAAEA0890E68BE79A8FD82CE7AE30F1DF79426398A7394D5CAED69CDBCE56FF6F283D42021375398D767CB146451A3184A7CC37AD2D6E623119
	F6BBD37747A4D97726450A235CC2F75E3A7D7632F1B6CF6DF8C2F74FB81EF5AEBDEB8FBC179A76416437D37912A4B93361B6AA477A7550C6E4EE9BC3AF7135784B16116AC63FA58FCBED9D76104C56F0D6CEDD0B520AF3674ACCE739391653D4FEE7121C2D25144A313E865ADC911B1359575D9E746925D04F8394GB6824A22B6E79D409565B6763C7B5F97E8E383022AF746A5B9A42A040D3D5B7D9A1462142E8A3DD1CD9722C1F1D0D0A5E1D896B5E274A9F0A38743B346BDC8EDBF88BA0E08603A02AEA9D118
	DD08083A28EA2C4FE36E17310FAA5256009491C1C1A4C6783C3FF6EF07D296B47D23D8C85045C48F567E049B4FE4C2980499E182F8E63C949FC43E8230FF99F05664C1EC45790E0AAA848C88AD0AB29E0F85F0A724A00ABCBFCAE3C796BC67C6B9471B5AD0D62D008FABFC1E6BDBDFC91A27DB8A627285F51A7B582DEE0C5BDE055B4C9F06384DF43FF8B3B9306E83CB1BC14494E6AB19CA2A6F6453B9F1DD2C5E6E9E7A02FEFCA1709AFA190ECFAE098E07D1B2FEBF7562D92D843D8500CDA6478F3756B11B6D94
	B411DBE93F8A86A606920C33F546DBAB8DBE9D665AB3282CACE6F39E08F14EAADA07127AFD597BE2BA6A288E1C3F7C9268E05C7C4C037711B8A1C675EEB1A6C6C3702099EDEBBDE8F3063F0EA866184728BC867E5D858E251972C28F7A20206B2AB49C574563222ECB51F0223D086A52624331C4E8603A1E72609E15C970EF337FE68F7AC6D009C6C5FAC86690237DAF54B7C5D90C40223B14F8D4E7721203E85F636234919BC89BA6C493029C47B843720715428B3887D50A4051E328A279B1745BE2B37FC184C0
	06F95092A833E35C87A7F7F19F5CFD7A86326A449E3B559D327466EEBBB945FC2066D44287591A1EEE33DE7BB3B4C698F24DC1E53F7CF49F14971A3EB0DF387A904E6F823DBD00132738AFE86FE2BB0FB70693F4A9324CA8B639BF692D463392CF45C5885398580E1AFC063D2F24348D9A5C32F6450B5C4AC9B1F11FCA6F47D61EB7CE41FA32GF927983F15AA630775BB63617DF41575DB224E6C474E30628466GD6B9E0B8AE5547F589A32A20037146620C9B1657FD5AD4E94CD128727CEDF18B72D52B591C8D
	009E0D1F7503AF727C6D1A71ECD2F8539BE9F358139ADB5BC2ADF156F517737C2D68B06A3C875A56EA7CAC1E70A65FA19D1A2684A5FAFD99BCFE6BD032ADB059378FBD8F650824EAFAAFB86B9467BC67902E976FB7C3EC5C773FB06F7BA250DB275B1C4C4E5FF5A75B79104E6E29ACA8F3G4C6EBABDBE7467D0681898D1A6C44AE0BDD4BB633A8ED7BFB5189382AD86C4F5C4D1A3E2E85F18B0A1D06E6BFD98D47A07474032761BE7F64F7ED7587AC5FAAA6CB25E37FF1067E7B7EEEFB756D4F06BB262BD3F5DEE
	15AFE7EE5BE93FD3E7BCEF5493FC1F3D5D6958743F2D95EC6FB99E3E7706384DB99A677BFA61E04F6CFD297479A23646E275092654FCB4BB1E48C3736233F3D21627BDC76DEEF5BCB517AC23B963A37154DCF2C98B4FA50B204DBF41F30B49F6BC4F15D0AFGEC8414GF6CC423B42A4373783071B49BC39DBAF780F9A956463B30234E1E3CF9EF63038EA541FBEDC4E63AA61F7560656D49E06A6E76EDDCF2933F1CF4D9AD71FD2DF1AB20E5953E0597CB120E346C933982039793B5D4AC99E8396D0EE0B20CE26
	EC4E5AA94E6D3A8E0CC78DD07783C2008CC08EE095E0639467774CFB076663179ACEC06982D98F44472E543B4D6463BB1E0B63B1FF5638D49E4B26E70EE3796A840D09BB202D8140E447DDA88BC0DD1E66F98F2139E316D9FF1E55CF717AB7DA2C723C2CE96BF7320B4B0BEE315471E565E16CACB35A5367C5D734DB67C5D736DB67C5D7373F4C64B0C772DB91EF3F2EFD6E3C6926F62B3CE9A75D733D2FED23F3EB1A28D163428B4FE87B1D7EAA31481FBE0497A7032F6D241666F2584727ABDD0DA647D36E395E
	6DD7D81CA70BDBFF26BE34EA1A452DD2D37ED7AAAFC81233F8F6C58B7F3E738834F9CFF3DF7AD5C7FA7E9F6F3066FFD3875E4D1A92D703E2AA47FF6A28B55B92DE572B0B912443489B69F8DDD043622CB3F2F4561AED6946D7F526BBC746750699DCB71972AB1BB0BF1505E9A52E83AFE7685D55446F67EC60A49FD08858FC1A5D53AC76FDF792732C91C889C14D711898A4C5F46C180B0F8DC0FD94108948852CBE436F4068CB58BEB3B272D870B5B7777B06335C2F37369053B64A5C0967ABADFD714B8253317C
	126AE0F6D850446DB083761CFF16F35CF892E3BF3DDF6875AFCC6AEDEDC7D4A954A9047D744458FDD2534C5F9B0B4F727B243399675CF74E66EC878C1D63B96554BBBC27DC31FC9971E79C354CA98B83CD7658B966E7571CCB64B8397167AA184EAC975739865AABBFC7590FDDB5C9F360D97F504E6EG56DEBC239D6D95E3C5227D6D1664F6DCEF07A339BDB0239DB95AE30CE7FB3BFF8F5FDB497D39C47E7E413479724067ECCF3AD18E8C6EA23B3F3A3522106AD96E6E1E634C9CA9D881E3D681DEB56AEB6179
	BDD07A5FD2EEC38F4D4A4FAE4C768AAFF25C6B4DDFF49C0B13B5AEEAB367D36367CEAAF71DCF0D1FBD4D5CEE5B21AD67BC4FD956F7E18C284C30B9F7833C00GC087E4E57098FBDBE78319A7DF89A8B1BF84E1B9695AE27B3B56F5F15FFE6F2A607611F7470DA4FF713325AF24310F1C8CE6977798E5D1E09715337DA9BE5C5FE6E82BDEE0F33AGFE40C94054823E3FE7BA8F307D79C2125E216BC2F0143EC9714D8DCA62E4C20C8174CDBA8EF8A37CD77C1E15EC49827C0F65F6BFB17735210A6FEB47AD57116E
	071FAD5D15E6DFEB96983EFCD942270B46CAAA982FEF3808E9C33B121EBFEC3E74F1B6FCC9F530B379EDD77AFBFDD317753D7E47AE6B3CEACB575CF99369CE7F3D2922BB79FB933329FB7BB0368D4139680025009500D517F1FB3A367BEDE6CF9636648B05B12F402F5ABA57BF65CB3F57733E793F05E77A70DDFFD801D752884E4F65D779703CF4A5C605C60E69C30ED4A9BC2A332E7C9B028F6DD696C72818596A837FC97EDF2EFA59DBF69ED3D24FEC3DE123C5C9364A38127BB1BE6F385C6674GBE836800CC
	C0B6A08F309A309E3099D03550662C8338GBD00A1008C48D8E87A2A9F3F918C8AE3C694C217D5D44AAE880D8B17922C8A92D24C7F9B68CB4F71A3FD73F33C210FFEE381EE8253B1A30FFB3C8F73B1E5E4C4934D337CFDDF35A96B558694CD427C0FF17150GB157F36E43BD697681D6728124906C33C1C0881BE3B27A67F857681FFF8FD97D56F6724D7E59F6F2DD3F351D5C501FECA7CC363C9FFDA48C5C8F197DEEB1E51F183215F4CE14FDAC0574D1D37E833AAE14770828104B6F246B1A2432A1C3F6D7126C
	93C37653FEBC875872282272317777637B6608A44B2C7EG6DA3C561D8CCBE5652B5CAB1A1E95D0F1AB2DCB77BDF7D675D4720DC71A4A957D4320D27F7753DF432420966B647EFEEF258C9D9AFF1B2F2FBC999B254CB4AA9013D448164CE611FCC6FGA04FA2E5AC18B0BF5BFFBE594F36AC2A357CCE0C48DE447CABEFD12A1F559D41F3A986F955E26EAB31777FB5070D9D0F88322C8B72F8C7B4047656B92D0B09183867884E2F71BEC62371FD68882EFBACE94C7F81D0CB8788A28AD6B0C58CGG5CA0GGD0
	CB818294G94G88G88G6AF854ACA28AD6B0C58CGG5CA0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGFF8CGGGG
**end of data**/
}
}
