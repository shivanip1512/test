package com.cannontech.dbeditor.editor.device.lmprogram;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramBasePanel;
import com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramControlWindowPanel;
import com.cannontech.common.login.ClientSession;
import com.cannontech.roles.application.DBEditorRole;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.UserUtils;
/**
 * This type was created in VisualAge.
 */
public class LMProgramEditor extends com.cannontech.common.editor.PropertyPanel implements com.cannontech.common.editor.IMultiPanelEditor, java.awt.event.ActionListener
{
	private DataInputPanel[] inputPanels;
	private String[] inputPanelTabNames;
	
	private LMProgramBasePanel basePanel;
	private LMProgramControlWindowPanel controlWindowPanel;
	
	private static final int[][] EDITOR_TYPES =
	{
		//LMProgramBasePanel
		{ PAOGroups.LM_CURTAIL_PROGRAM, PAOGroups.LM_DIRECT_PROGRAM, PAOGroups.LM_ENERGY_EXCHANGE_PROGRAM },

		//LMProgramCurtailmentPanel
		{ PAOGroups.LM_CURTAIL_PROGRAM },
		//LMProgramCurtailListPanel
		{ PAOGroups.LM_CURTAIL_PROGRAM },

		//LMProgramDirectPanel
		{ PAOGroups.LM_DIRECT_PROGRAM },
		//LMProgramControlWindowPanel
		{ PAOGroups.LM_DIRECT_PROGRAM },
		//LMProgramListPanel
		{ PAOGroups.LM_DIRECT_PROGRAM },
		//LMProgramDirectCustomerListPanel
		{ PAOGroups.LM_DIRECT_PROGRAM },
		//LMProgramDirectMemberControlPanel
		{ PAOGroups.LM_DIRECT_PROGRAM },
		
		//LMProgramEnergyExchangePanel
		{ PAOGroups.LM_ENERGY_EXCHANGE_PROGRAM},
		//LMProgramEnergyExchangePanel
		{ PAOGroups.LM_ENERGY_EXCHANGE_PROGRAM},
	};
	
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JTabbedPane ivjStateEditorTabbedPane = null;
	
	class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == basePanel.getActionPasser())
			{
				if(basePanel.isTimedOperationalState())
					controlWindowPanel.getWindowChangePasser().doClick();
				else
				{
					controlWindowPanel.getWindowChangePasser().setSelected(true);
					controlWindowPanel.getWindowChangePasser().doClick();
				}
			}
		};
	}
	
public LMProgramEditor() {
	super();
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (3/15/2002 1:17:24 PM)
 * @return Object[]
 * 
 *  This method should return an object array with 2 elements,
 *   Object[0] is a DataInputPanel
 *   Object[1] is a String (Tab Name)
 */
public Object[] createNewPanel(int panelIndex)
{
	Object[] objs = new Object[2];

	switch( panelIndex )
	{
		case 0: 
			basePanel = new com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramBasePanel();
			objs[0] = basePanel;
			objs[1] = "General";
			break;

		case 1:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramCurtailmentPanel();
			objs[1] = "Curtailment";
			break;

		case 2:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramCurtailListPanel();
			objs[1] = "Curtail Customers";
			break;

		case 3:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramDirectPanel();
			objs[1] = "Gears";
			break;

		case 4:
			controlWindowPanel = new com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramControlWindowPanel();
			objs[0] = controlWindowPanel;
			objs[1] = "Control Window";
			break;
			
		case 5:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramListPanel();
			objs[1] = "Groups";
			break;
			
		case 6:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramDirectNotifGroupListPanel();
			objs[1] = "Notification";
			break;
			
		case 7:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramDirectMemberControlPanel();
			objs[1] = "Member Control";
			break;
			
		case 8:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramEnergyExchangePanel();
			objs[1] = "Energy Exchange";
			break;

		case 9:
			objs[0] = new com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramEnergyExchangeCustomerListPanel();
			objs[1] = "Exchange Customers";
			break;
	}
		
	return objs;
}
/**
 * This method was created in VisualAge.
 * @return DataInputPanel[]
 */
public DataInputPanel[] getInputPanels() {
	//At least guarantee a non-null array if not a meaningful one
	if( this.inputPanels == null )
		this.inputPanels = new DataInputPanel[0];
		
	return this.inputPanels;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public java.awt.Dimension getPreferredSize() {
	return new java.awt.Dimension( 400, 450 );
}
/**
 * Return the RouteEditorTabbedPane property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getStateEditorTabbedPane() {
	if (ivjStateEditorTabbedPane == null) {
		try {
			ivjStateEditorTabbedPane = new javax.swing.JTabbedPane();
			ivjStateEditorTabbedPane.setName("StateEditorTabbedPane");
			ivjStateEditorTabbedPane.setPreferredSize(new java.awt.Dimension(400, 350));
			ivjStateEditorTabbedPane.setFont(new java.awt.Font("dialog", 0, 14));
			ivjStateEditorTabbedPane.setBounds(0, 0, 400, 350);
			ivjStateEditorTabbedPane.setMaximumSize(new java.awt.Dimension(400, 350));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateEditorTabbedPane;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String[]
 */
public String[] getTabNames() {
	if( this.inputPanelTabNames == null )
		this.inputPanelTabNames = new String[0];
		
	return this.inputPanelTabNames;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMProgramEditorPanel");
		setPreferredSize(new java.awt.Dimension(400, 350));
		setLayout(null);
		setSize(400, 350);
		setMaximumSize(new java.awt.Dimension(400, 350));
		add(getStateEditorTabbedPane(), getStateEditorTabbedPane().getName());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (3/12/2001 2:49:24 PM)
 * @return boolean
 *
 * We must override isInputValid() because this is a special case since
 * there are 2 panels that, based on state, can make each other invalid!
 * These panels are:
 *		com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramDirectPanel()
 *		com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramListPanel()
 *  
 */
public boolean isInputValid() 
{
	//do what is necessary for Timed operational state
	//checkTimedOpStatus();
	
	boolean retVal = super.isInputValid();
	
	boolean isLatching = false;
	boolean hasLMGroupPoint = false;
	String errTitle = null;

	//be sure we are Ok up to this point
	if( retVal )
	{
		for( int i = 0; i < getInputPanels().length; i++ )
		{
			if( getInputPanels()[i] instanceof com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramDirectPanel )
			{
				isLatching = ((com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramDirectPanel)
					getInputPanels()[i]).hasLatchingGear();
			}
			else if( getInputPanels()[i] instanceof com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramListPanel )
			{
				hasLMGroupPoint = ((com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramListPanel)
					getInputPanels()[i]).hasLMGroupPoint();
					
				errTitle = getTabNames()[i];
			}
			
		}

		if( !isLatching && hasLMGroupPoint )
		{
			setErrorString("The '" + errTitle + "' panel had the following error(s): \n   -> " +
				"LMGroupPoint groups are only allowed if a latching gear is present,\n   -> " +
				"Remove the group from the assigned group list or change the gear type to " + 
				com.cannontech.database.db.device.lm.LMProgramDirectGear.CONTROL_LATCHING );

			retVal = false;
		}		
	}

	return retVal;
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) 
{	
	//Vector to hold the panels temporarily
	java.util.Vector panels = new java.util.Vector( EDITOR_TYPES.length );
	java.util.Vector tabs = new java.util.Vector( EDITOR_TYPES.length );
	
	DataInputPanel tempPanel;
	int type = com.cannontech.database.data.pao.PAOGroups.getDeviceType( ((com.cannontech.database.data.device.lm.LMProgramBase)val).getPAOType() );

 	for( int i = 0; i < EDITOR_TYPES.length; i++ )
 	{
	 	for( int j = 0; j < EDITOR_TYPES[i].length; j++ )
	 	{
		 	if( type == EDITOR_TYPES[i][j] )
			{
				Object[] panelTabs = createNewPanel(i);
				tempPanel = (DataInputPanel)panelTabs[0];
				
				//make sure that this user is allowed to have a member control tab
				boolean allowMemCntrl = ClientSession.getInstance().getRolePropertyValue(
				DBEditorRole.ALLOW_MEMBER_PROGRAMS, "FALSE").trim().equalsIgnoreCase("TRUE");
				if((!allowMemCntrl && ((LiteYukonUser)ClientSession.getInstance().getUser()).getUserID() != UserUtils.USER_ADMIN_ID) && tempPanel instanceof com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramDirectMemberControlPanel)
				{
					i++;
				}
				else
				{
					panels.addElement( tempPanel );
					tabs.addElement( panelTabs[1] );
					break;
				}				
			}
	 	}
 	}
	
	this.inputPanels = new DataInputPanel[panels.size()];
	panels.copyInto( this.inputPanels );

	this.inputPanelTabNames = new String[tabs.size()];
	tabs.copyInto( this.inputPanelTabNames );
	
	//Allow super to do whatever it needs to
	super.setValue( val );
	
	//check for special Timed Operational State case but only if it is a direct program
	if(controlWindowPanel != null)
	{
		controlWindowPanel.setTimedOperationalStateCondition(basePanel.isTimedOperationalState());
		controlWindowPanel.getWindowChangePasser().setSelected(basePanel.isTimedOperationalState());
		basePanel.getActionPasser().addActionListener(ivjEventHandler);
	}
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "LMProgram Editor";
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G4EF854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135998BD0DC55B5EDE3EC2B53A1AD4EE4B41DC19BA3D1A4E0C8C4C2A2B2D8FFF1120E130AADBAB845CEBA23CEE62A5324C3474B728979908941CAB4E978A690378B8811EFEB88886123B201D2C63072E0AF30495BFD1B779EE1B7A136675C7B6E5BE5DD08311A1943FD775CFB4F7F1EF36E06949E8F4B4E3094921211CD0CFF8D9904949EA7E45B0913E582173FBAB107787DAB81B8C4DE7CAC8B46E2D86B
	CBCA4C49A7B78D67423C9E66698909B9EF4377E13221BF7E9D028403728905350F8AAB069767B3D5A2786CE173691E83B09E84E8849C0ECBD7A37FA5BDC586FE54408B483090B2F88F6E3923672801FB9C762482D4823E759E5F7E3D0C73CD4D0D025672B0725A13A7ADF9C8470F260015465984E3ACA6EBB3264311D79213934F6C02359A4021721924EE249546567E0BD37F1CD01C36894965D2DCBA1D1836D13BC3D7D41B1DDEF6CCD01B6CF42B4A14AAB91B1B7BBE6E671F9F319DE90C7E015EDCC60F108818
	3B57A06E0DCE64D9895FE7G068138BE7FB859EA48BBFCF5ADB97312AB734D857409D94375C8D96353E7693974BDC7186D6FA9C59B975D8BB68368875081C281E2G5EE5BE0FACB985E35B385E2EB81D0A2B4D6DBEAB5B550F95BB15613B3999D68C5CF9BA2128F6C238DF1F6C318611E7B1606712EF789C37C9F81D70F1447955A4E5DD2FE5CF901B240457D8365C4BED11F62F4FA61C77215E05F8E75DB7BF8667737EB77167DD932AE4E687619D333D4AD2E450315E6763BDFA9F7ADA6BC2DF7741F70691879C
	FF0D61437C70BC9E33999E67D130D698A1E2E378201025AF6BF612DF5B97548EAB925BAC1E88AEC3C804CF969E97D165F82F62811F8C3093A077FE38DBG9DG43770B5878D3C975570C0DFEC9554F4EB8E4BBD589BF9BD57AD79829C7D725EE1726CB2E89BAA4298EE9DC269AB176BCD40A320F4FBB3719456C84706822AE2ACA3AC3F181F549C9F52AEAFC4FE32507F00FCA596A2043C9010153CD845D2D2507E114A5CD7F175BAE695437032FBFDB0A363CAC8D81C5A0G5F5CAE4FD7204DBD20FF88C018E907
	73E5C82F172AAEAA4375D6140BB36EC154046C3C9FFDD5496EF68CFCEF8160341EBB4172EBA4645BC8C167C7E585FEF4BA9C93A83E24FA455D70F6E16E9C0B943EFEF5D8783AA36FEEE25D5E93545788F3115C47211101B17F83C6936562737DDD43C14E0FC5860F7F9B854E23BCA80F4CEF017EC153D7DD15682BF810BB99E017E9632432E29E33E7A50D0AA87DD405G23F61F45795E38F24C2B74B2F5699D54CDDDF678500C98F924FC2FAFFEA795B3369EE3F8B744FBBB8418E6621FAE471814F4DDF50C4F68
	F4006A3A43B565DB5F4EF8E9B363EE5FD5913CD24AB11FA833906F667E974B8B58BDF039A8935ABCE22C7F0E45AA15299304EED7E6DCBA477F21827DFD11FA0D3B72CAC58E0BF5F9866F9D2FE9D152FE54C1F5B84194F8F5093E96637843DED1139172810735799C0EEBC5CC4E160918AC3FF2A7C978224292AC972E383B43923F16475436353E1864B23DF0A2386CE96C4E98F80D4B5AF8A09F46738A58141753FE89EE8F0F01A5D5C8E7A7504F0ACA4CB99AA5E2E06C64913FFBE6B83FDD11E5EECA53469FD7A2
	ED0DDE1A21101E70C2771AF69B2CAC88D89BB2EC4857C7AB510632DFAE6072FEB9D2C5966B971A1B21B9F0D3D577B2C5E0AF6F8F56309C02B62E0F927D419FBD281F8C7325GAB23048FFE15A77A03500BE1A475976FAC586F44C4F15BA7C779FC10BFA47A03A7CFA14F22E8487D5142F64FD67A673AB6CDD3A69CAC4D9AFAAF2B727791475DD2358F46C9072A695DF009BC42C64B2BD0DE4C43865A488F11E69DEE0D66F275C77B720F9D3EBD517373912F15D3DE34472D5101B57492432F0C8E2C213BBD2206C6
	40DA7E83A2B76F31E21C6BB08F818887C8GC885A06B049D0E1C2AA35729FD5D60D755A5498373AE3528C127B279BDB4664F1C4A967790E316550DC7194F0F824FC6G0E5BFC85F1FDB09FB3F1F74CA1EEA94CD71A3878EB08CB00F972BA115B1DDE3C7BAF1B73D9B64F0F9173B7BC4172727F8348EBFE6EAF72FE5BBC7C6B8637E53EF5613C7D36B5F85EBEE68D1E374B2C07B89EE878F7B1E27DBD6B62F93D4E9AAC2FE7323514DE3C17EE15CED295B25000638A7ADE0EC17F3D4EFCDF8F5FE7G787E6DD7F258
	FD71C23603F741FA5C57F3D56464A85877B8C0BA1EDF4FFB8D1EF322E7B197CDC21384228D386984495A00E79F1893E75D30FFA940AA00A40034750267B357702CD372B81CB3CEA6A1176F4EAFD99C821D618DE2EF2377AD9887F4C964E2C8FC63540E098C6F932BA161DE7F9A32F48317B38A46A400148D78E6AC432BE558E85D15E298DDF4367802F4D932C9333A2D835AB597B6E61575B5B556623A53B5D5BC6F054D7A3F739A77B5842DBDAC4FBD4863325E9863EFAEB3F202FAD53C73E4D8DB75203061D4E9
	85714B5B0EF127ED663253A60D6BACF1B7B7FFB2A34BFD06EB0C1C9DF7B557201572204859BB987D4258449CABC0C72C48591E93A2E7C72C3E1D74AD698D1A33D3875F370C46F21917447AF2768AF853F11E49F302E7BC2C3F960B381CEB058177E5933C21EDB0727546F96B984FD823FC6B97662D233CA5197E6BAB672DE77B1D673ABDD5A1F4CBFFAA1C486B1B962CC72D8FF11D460C31FF280C94FE7E17BDBCAE5ACF7B6262571E2F93975CF76F659E933E434D9E1BB68B97213939E7DEFCF27B25FAC51DDD71
	1070D99FFB07EF03798EG92A7FC36F544BF5E72EFAF31E4AC20D7D19C5727BCAE702DF57A8364B78CF8BDCE5416693A77E09C12AE98D505DFA6D6D6GA97A830EE5DD0711DBBDD73F591BFB59B7BC77708D1F6375F8F48E6D999A476BF10609278C9F660767F772D573CEC640DA7EC6D127A79BD81D06F988C0B8C082C0AAC07AC6E14B683A9AF21DBABD28387B20D848FE691D6B777C5CB77BAD6211BDA25EE3626EA2314BF6E76CDFA0AE7263F9BC5498630E41B206677A6DEEC47D46E0ED8EA08CA096E08BC0
	DA3C50EFF71D156B076150266B5244B46B3C04F2C38EBA6BC3E372673A3D64953A6D82DA87B64D6F270AE1DE3FA9D80F15406E4239CD013EFB026187B7857A6EFC27705D982CA5ED923EBB5A0C1662BA5CFC3AE221F8878DFA604AFEBAFE81FA73C1E94ABC537C814AE456DDFE8D0C35368FB09FB9DC8E5DA1490EAB2CBD9D000D2EA92E7B789CB1E378225F77EBD73F6D5E642BF7A6EF6E3B644175D79A966EFFAEB7846FFF669A0277BF5FEFD83C3F79F143426F56658D7E6FD66E772A9674CD65E628FDG53G
	F3GA11BC5BC9FEFAC65719CA416BF32CF51F6057DDA248B7E162605F55D5BF47D5F188A1A70EDB22E683A62C47A82DF54047E5295B7C39ABDE2935AC8F5CCCD6BFC2B68911BB0074BF41221F97F1D6E953DFB44E651135738D9CF8E73EDA66ED216685D53CC5CCF33C56F1E7B30684DFF59690B532DFE5F431D5FFD4CDE7ACEF9F09B5636AC6C4746166B7B312D053DBB417E03DEB765384E966C171549C90D1A71525B12EF623A35FEC5F3E0F660BDED85FBDB9D851BD7BEACFA5AAEB73E11A6610D4E77FC4EFC
	B8886BE3G3C0F7EE11668E9C3G378AA089A08D579392F378390D4C2765B02FC990FDEEBC1689231F29F097136B77B37C5C6BED3EF3B42BB470DCA788DBD9EA075C27CC15DA0B61E8EDFD1568EFA233C4FFB81AA07A43142C457B430359017DA11E772D1F49DE3CBFD433837BC3F11E6BD3546E53E7F3475731830F4BF5D9B7DA1F797FE3ED9D6EGE6912F10ECCDA585DF65537613671E0D78222350C2AC5DA4E71A62056FA659338E3BBE6DF103747F818851AE11AFD16E20B315644D734723F71E7E7D169706
	34CC1253CD0EE0B88DD015D5A7EA6F10F471D3F3A7596BD69C308878EE122531A1DB533DB26DA30513677167A6CD2776B6110FB51D3C55071C2AE7F407DCFDCE5226F53C97ADFDA44B05BF0A13823EACCB2E29EADE84DBD0871251B60672B233901077B013972958CCAA07B73ED9D15213961BC9AC1DE41F2CC876CEE982CAF3B7592BCF2BD41BD6E43B477D6199C6A8F796A38BBB1DCCA0772E29BEAAEB9B045BEB8AFD517373DB73F664F5C210F9D4D24DF66A3041E66E3037CFCBAA3022EA370EBF72ABEA3939
	763D3F69A71E6EFAFB54C2B28CA55174B99E37CEF2A66109ABFB6103C7BFF34ABE9D24D5A517B62928CE102CB8C0B231E4B2307D6C091A5B567C79B34640F7D4A74739A961E87594D51C55ED13138E97B58900DD017CB113BC6E31856CA970ACFF2BE77B9B6390879DA453D9D3437E56323F6F635FDA0629E5185A774138BB49227F8257BDC2E796BCA04F1BCC9EF43F2D93BC287F6615A76E7AED6B4E17C13BCE124DFEF082A7647BF6FDB2639C276A32085B0E6D8FCD3D85FD6446D071A05D031E79F14E7B5D2A
	7F7A3F03F65DD434603BBBA40977142B12825FC149A398BD2900CFCF923DC065485F4942FD6100D312E5DD12AF36396C58A31E756A545717BCBF0274B53147D8B4FEC79EC139AF781D799FD0CB8788C02FE282218CGGBC9FGGD0CB818294G94G88G88G4EF854ACC02FE282218CGGBC9FGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG5B8CGGGG
**end of data**/
}
}
