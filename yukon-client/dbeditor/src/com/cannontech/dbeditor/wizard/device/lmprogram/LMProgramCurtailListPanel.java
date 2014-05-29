package com.cannontech.dbeditor.wizard.device.lmprogram;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.data.customer.CICustomerBase;
import com.cannontech.database.data.device.lm.LMProgramCurtailCustomerList;
import com.cannontech.database.data.device.lm.LMProgramCurtailment;

public class LMProgramCurtailListPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemoveJTablePanelListener 
{
	private AddremoveTableModel tableModel = null;
	private com.cannontech.common.gui.util.AddRemoveJTablePanel ivjAddRemoveJTablePanel = null;


/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMProgramCurtailListPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (AddRemoveJTablePanel.addRemoveJTablePanel.JButtonAddAction_actionPerformed(java.util.EventObject) --> LMProgramCurtailListPanel.fireInputUpdate()V)
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
 * connEtoC2:  (AddRemoveJTablePanel.addRemoveJTablePanel.JButtonRemoveAction_actionPerformed(java.util.EventObject) --> LMProgramCurtailListPanel.fireInputUpdate()V)
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

private void connEtoC3(java.util.EventObject arg1) {
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
 * Return the AddRemoveJTablePanel property value.
 * @return com.cannontech.common.gui.util.AddRemoveJTablePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.AddRemoveJTablePanel getAddRemoveJTablePanel() {
	if (ivjAddRemoveJTablePanel == null) {
		try {
			ivjAddRemoveJTablePanel = new com.cannontech.common.gui.util.AddRemoveJTablePanel();
			ivjAddRemoveJTablePanel.setName("AddRemoveJTablePanel");
			// user code begin {1}

			ivjAddRemoveJTablePanel.setJTableModel( getTableModel() );
			ivjAddRemoveJTablePanel.setMode( com.cannontech.common.gui.util.AddRemoveJTablePanel.MODE_TRANSFER );

			CICustomerBase[] ciCustomers = CICustomerBase.getAllAvailableCICustomers( com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

			LMProgramCurtailCustomerList[] custList 
					= new LMProgramCurtailCustomerList[ ciCustomers.length ];

			for( int i = 0; i < ciCustomers.length; i++ )
			{
				LMProgramCurtailCustomerList localCustomer = new LMProgramCurtailCustomerList();

				localCustomer.getLmProgramCurtailCustomerList().setCustomerID( ciCustomers[i].getCiCustomerBase().getCustomerID() );
				localCustomer.setCustomerName( ciCustomers[i].getCiCustomerBase().getCompanyName() );
				localCustomer.getLmProgramCurtailCustomerList().setRequireAck("N");

				custList[i] = localCustomer;
			}
			
			ivjAddRemoveJTablePanel.setJListData( custList );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddRemoveJTablePanel;
}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 12:36:25 PM)
 * @return AddremoveTableModel
 */
private AddremoveTableModel getTableModel() 
{
	if( tableModel == null )
		tableModel = new AddremoveTableModel();

	return tableModel;
}
/**
 * getValue method comment.
 */
@Override
public Object getValue(Object o) 
{
	LMProgramCurtailment program = (LMProgramCurtailment)o;

	program.getLmProgramStorageVector().removeAllElements();

	for( int i = 0; i < getTableModel().getRowCount(); i++ )
	{
		AddremoveTableModel.RowValue row = (AddremoveTableModel.RowValue)getTableModel().getRowAt(i);
		LMProgramCurtailCustomerList customer = row.customer;//new com.cannontech.database.data.device.lm.LMProgramCurtailCustomerList();

		customer.setDeviceID( program.getPAObjectID() );
		customer.getLmProgramCurtailCustomerList().setCustomerID( row.customer.getLmProgramCurtailCustomerList().getCustomerID() );
		customer.getLmProgramCurtailCustomerList().setRequireAck( (row.reqAck.booleanValue() ? "Y" : "N") );
		customer.getLmProgramCurtailCustomerList().setCustomerOrder( new Integer(i+1) );

		program.getLmProgramStorageVector().add( customer );
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
	getAddRemoveJTablePanel().addAddRemoveJTablePanelListener(this);
	
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
		setSize(416, 383);

		java.awt.GridBagConstraints constraintsAddRemoveJTablePanel = new java.awt.GridBagConstraints();
		constraintsAddRemoveJTablePanel.gridx = 1; constraintsAddRemoveJTablePanel.gridy = 1;
		constraintsAddRemoveJTablePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsAddRemoveJTablePanel.weightx = 1.0;
		constraintsAddRemoveJTablePanel.weighty = 1.0;
		constraintsAddRemoveJTablePanel.insets = new java.awt.Insets(6, 5, 21, 11);
		add(getAddRemoveJTablePanel(), constraintsAddRemoveJTablePanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	initJTableCellComponents();
	
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/00 10:52:29 AM)
 */
private void initJTableCellComponents()
{
	// Do any column specific initialization here
	javax.swing.table.TableColumn nameColumn = getAddRemoveJTablePanel().getJTableAssigned().getColumnModel().getColumn(0);
	javax.swing.table.TableColumn exnotify = getAddRemoveJTablePanel().getJTableAssigned().getColumnModel().getColumn(1);
	nameColumn.setPreferredWidth(80);
	exnotify.setPreferredWidth(20);
	
	// Create and add the column renderers	
	com.cannontech.common.gui.util.CheckBoxTableRenderer bxRender = new com.cannontech.common.gui.util.CheckBoxTableRenderer();
	bxRender.setHorizontalAlignment(javax.swing.JCheckBox.CENTER);

	exnotify.setCellRenderer(bxRender);


	// Create and add the column CellEditors
	javax.swing.JCheckBox chkBox = new javax.swing.JCheckBox();			
	chkBox.setHorizontalAlignment(javax.swing.JCheckBox.CENTER);
	chkBox.setBackground(getAddRemoveJTablePanel().getJTableAssigned().getBackground());
	

	exnotify.setCellEditor( new javax.swing.DefaultCellEditor(chkBox) );		
}
/**
 * Method to handle events for the AddRemoveJTablePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
@Override
public void JButtonAddAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getAddRemoveJTablePanel()) 
		connEtoC1(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemoveJTablePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
@Override
public void JButtonRemoveAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getAddRemoveJTablePanel()) 
		connEtoC2(newEvent);
	// user code begin {2}
	// user code end
}

@Override
public void MouseTableAction_actionPerformed(java.util.EventObject newEvent) {
	if (newEvent.getSource() == getAddRemoveJTablePanel()) 
			connEtoC3(newEvent);
}

/**
 * setValue method comment.
 */
@Override
public void setValue(Object o) 
{
	LMProgramCurtailment program = (LMProgramCurtailment)o;

	for( int i = 0; i < program.getLmProgramStorageVector().size(); i++ )
	{
		//AddremoveTableModel.RowValue row = new AddremoveTableModel.RowValue( (com.cannontech.database.data.device.customer.CustomerBase)program.getLmProgramStorageVector().get(i) );

		((LMProgramCurtailCustomerList)program.getLmProgramStorageVector().get(i)).setDeviceID( program.getPAObjectID() );
		
		getTableModel().addRow( 
			program.getLmProgramStorageVector().get(i),
			((LMProgramCurtailCustomerList)program.getLmProgramStorageVector().get(i)).getLmProgramCurtailCustomerList().getRequireAck() );
	}

}

@Override
public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        @Override
        public void run() 
            { 
            getAddRemoveJTablePanel().requestFocus(); 
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
	D0CB838494G88G88GGF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D98BECDB5599CE216D502826A2E5DA5721A90302A2AD020E9529C251D622EAAB3403EE8A8FCD1956EDEC4BD8592211A19E951CB80D6DA6A9CDE98A59E857302610C217268FE86286C89C676146C9C3EA92835560463E09EFF26D6B5EFB1DC713527D7FB977DC5F38F6C2E13574695C731F47FF7E67796FB529BE10D5D6E22BA624240C983FD3A504E4579272644E876E62348CB14FCEAC3F57G3B49EB
	1D5B21DD8EE37DCB726CD5647CDEA774D7C13FE8F11E7D95F85EC3FADBDA8F40C39ABEB5B07667274BAA6667137F9E6763207D476D15503E8848869A3645A2725F6928B1681B8CBAC7090D1042911CD36F58E7501A205F8FD885D8BB4266EF06F687653C4455E44CBBFFD396A93E7F8D5BCE5C4732A7C763A8DB6BB75ADAF2E74936D16475012352D8338C7A6B81A8FCA9B9BE5B89EDCB7F7868FBC1A552909422D1A52A0B41F04324F4D1D0C38DA1F1C28A0A8DF2A426AA232A90F139FC3D7D6C31BD2E6A02A47B
	A4CD6F972222CC48D498F9FD5B6124FB93528A7D6D9252561FC27E39504F87ECB569B7B6A13D9B1EBF83CCC9DC66F75FE8B1E48A4C5CC936BD92AD5D15C666659213F52D1410B9B37E2C639F54BE552DE807E2982B994B339F817483E2006500D729DF3C7A7E1B50368D6B6DCAA422C45BE2B1379CD2FB1510A843334B85A386EDC08CAAEA0890E67BA147119467198FEC1DF8556B18CE32CFF0BF4899381D947C304766C82113026CC6DB769853453A3104CE986F6F26653DE5EC2E1F4E65BDC42C3C37AED5CA4B
	D270DE7350E1DB31310FF3BC413BE99CED5DC4EDDD8F4FA506FFB03A1B52E7ADF4663377D31F417EB298AB9A673E91F8111F4537FDA529BA66CB290795F9EDB63F4C4EA04B49E7F90172DCA1B31EE566D94A1B111EE32133335CC467E3FF9D0C55C5726C4C3F763BB106EF007EAAC0AEA09FD08C280A423CA87731507BC73F200F758B2A6E0ECBF2C8D489DB3B3F75E5E845A9DD953CD1CD9722C1F1D0D0A5E1D896B5E24CF93D95F5B0BCE75DDB5477034023CB0C0A2A20CBCA94F697A222AE2A9A1BF332F5B74E
	D1C5BA6A17A2A2B00844885F3725F58F6ACF50748FE3A1C19793B358382795EDB2A18C420E3081BCB33D1CE9C7FDF9C07E73G5954430F69FEBD228A7982D209220C47E3FE140464C6D14FF7535C11814FAB22DC471D9E249185E2D8617BBC557A12E59F0F9444638B6AB40F31771AB0CF9FD1384F7CA540FD463373B6F26421B3A9FD067A1442FCA524A447CEB75D934F457A1BCF852EB20E3FA8705C0C471F2E990FD253D6EBBA50D64B00E7B6E0032963E3ED354CE75D02A6F2AFBD2B02030921044659B94B1A
	1D06BEF1DFC62B2234AD301FB3466579D6D340B26F7FAD9363311A72407D4B2E818F261BB75CF89F0993E2D47708B1B19A02874D98EBF0234F9971BA2218EBCED2FA8C623B9D82CAB369AEB74620206B2AB49C5745F3222ECB515144381B7252624331C4EA603C3CEE14D1190478B6677739B1B602CAB4AAD2A319CB0C71FE9A1B22ACC660506DCABC2AB37AE0BB7A7738B8ED64060F5B31G1A9064B86699D6AF2C9630C00A29D284CC0F290AE446B0EEB719758E2291E8D87704211D0D71981CBC48E3306E6277
	483ACF8F59D25DA1ABEE73588AE2AC760AE20998E4E76A7124BE7B87B4C798F40D1D751BD528874CCC8254B68322ACCC9BF58A737BA7BAF12F2A8B102781AD97385F7F4C0363BC37988E5F2E48B2D3272967FBBA2A50AE62053888A99913D80F293BFBBBDECA9A9BB47448466F6BC0BD4A167C67D469DD584CEBC2BF1CC786AC3F40F4D9F84138D7693CCA17B9CFE573E4F56EBC26834727F85F272A6FC663D2C3DC17B0FB8ABAB8EAAC4E2AB8D6433D63F49AFBECD0F92DB6DC017AA29A64E1C023466D7A731D3C
	D64B9C4FA2DBEE7DE75A7A34DBB36EE3ADE1575A6BF92DF62A9BF96640D82E46ED6169345E97ED1A2684A5FAD599FAFC3253EAF7C6FB3A339C5A91C955F4AF8466945779731DF8DE3C4B8C32F1370FB7733BFDB370F56AF9F666532FD4DAFD3AD5E7F7D2860E83CA691E3FE876E16C06C283E2C419904FD0DBD2BDB61CF147F59D2E7AD0280D7A4D2782ED7AC5F5C4D1A3E228FECC189028899ABAB00F1C9D9E8387BB4C6C367E70CBCC8611DA06DD3E750787F9BDF64B1ED663DC1B6ED8C9DAAE6F31252A0FEF58
	355F1623B3DD676B891D4F5EE5AF5D7A5F679CCC7E37DDDC7E5AF81EBD9067325D6CBA3B00ECEC602A44B3F274E9E7F29D3ABC1E28BFD7452F2CC5D9FD76B075413571649AF28B25EF08A75710618ADEC3BE8AE3AD933C26780997ED1B857DDCC0BEE0AB20F2925E91A6396FAD6DBAC19628593C90CBEAD4104F4DC94E3CF66AAEE579546837F61771FCCA78DDF521B9D98F5D13F3E58FA575593A4BD72CEBC836FD52BA66D75311638B6603369496676FF6E7AB793BDD56A44F8B3BE91D7EA8740BG4EA92E633A
	9E4CD13550EF817881B1C0A6E0B5A0FF0A6B797E6EF79752B375A03F528E34EE48196D49F71B2917532EAF27CF6F956B1275D9BCB5F79D2BD787E81E2C1C4E335782986DE8B952BC50BFBF4D6B1E028A3CDBB34C7E9FE89F73B66BC7AB962A73F06DD72B0B3E36A00F2FD213B1DBBE68CDDF97BD6CCDDD9795F8D357C53F756EE6F458437AD644479F774EDFB77D4D1B2AEE5ACF653C6F65DDF4EFCD93B56ADCF8899AE3FF5477129475D39F3CB899BA5AC3321BF798F3BAF4257D1829E3FF65FC6F76D9A9EC4872
	57929AC3EB26D97EAAB1695F2074F596BA4BEB2F1C625FF3B641D863C59ECB8DBD69757FF6CFEA7D1F6E417BDAD362EAD0CC56313B27469CCBC41DD797A3288E23A6236BF5C19D952F3051GDD4F46522D7F24A71D9D192ED517D55747CD3A64447A942ABAA52E43438C6AA543496F6CCC50C98EA09FD0F8115D5DAC877EE092EB2F91F89186B667E2E2109450351D65FCEDB7744F83E681B783F2E7781DF80B379A5AD49912457EF8A50F7D3AD99E5BB71EA226FFE4DA1E77DF33F73BC44C3FF3CDF9B0DF4CF6F2
	DF54C1661CD92E67E31FE17E27F78D2D8504C95D656AD22510DB9875D12B31BB2548495F9D9F1B65F74A3FF6601E7BAE6559CF82FC17F82DB975BA2FB5F3EEDFC9FC99BDA9EB4DAD7E633650A596EBB7DCCA54BD5971E79D0C27DC4EF92E0771ED171036323C5A32875A7B859B3B87587826B963683318AF92634F4F9947733615DA479BAB2C632823C346FAA65B46C3DC36ED9B3309FCF7F35ABA5A73B913A9EC347D0387C95D571FF364FF0EFC2E2B744CE33352A4E4411AB5003DC6FF436709DC43FC680EABEA
	35AF66FB39DFF25D5F2FFA9D4B15C54CE7AEA76750E7A83D7EF2F28E2D5B417D76A40C2D384C6B1637FAB18FEC03FECD8664D6C0B7A08CD81C41736C73BD0DE4011A452F44FC1008E54B55E554FC65DF6E7B6F3D8E6E9FEB6E39052CDD7ACCC9C59A7FD811E1FC93B75AC27FC1CAE7727D288F65AB04316D0B726C7500D6009FA0AF6272F57A0EB079BAC2125E266BC2B0CC5F3038F00312B819A0E392BDC057417BA9613F2277D351B296617FAACFFDC8CC39FED349655A777D6F904FA34F149CCCA3577AC5CC1E
	6A6B92B1DDB8364D41747A945D0EBD3FE0F9FEF347354F336D571407717D28B77D5DFE3AB7755D5E561B3A366A6A1D3FF6BA5B1B7E1B53E02F751B9373291DC3185BFCE017B0E099A08B3066BA6ECF0F757E1B79D38ADF6A880DE2ED01DF36F54E7F27FD69E5FD20EF616F610F7461B700E1855ED2A338BF277F2A8F6D25ABB1CAB46A4CBE5411AA0D06F5B655209776215F4A6288A5B3DFFD70936BFFF1D5ABDF33711C12EC334D060F96D8FC1569EAF5G73F35575702E89688704813300CCC0B6A0973099D088
	A00B736C5500FAC08B4087788C20AF663ABDBDF8985AC1E14C48C298320ACA358BC46342258AD68589D5EC4837F7A83D0E778DAD2C63C3C3745B8B6846BF9DB3EA39B70730A6D3C6C6B45134655121AA1366557A95CD429A1069625CACB14FF36F5021F4F200171C019202FDC3708B23661A750174B2EC8CACACC3FEA0351F7CB2F0251F7CBA105ACFFE9F307A0971EE92409899855D874CF9FFB5E99F1934E23AA7520624109EB669C7E64A8DFA37088CB97D5D99BC57A42585F80DEE21FD4463FB866DGA20795
	152F754D60BB6708A44B46F7E0BAC70A02D9CCFDFCB803E714E202655C9F1BB4BCB77BAFFD23E7G5A1CA7C8192612DD3C3CEB70524D36CC1CF0157E6E56D29B596EA5F626DCAF590E9A7212B22AC0AFA98565CE619FCDD8F8A0833C4C6C7CCEB8FC599AE7CFAF29C9792D9811390445D7761264B82B69C23BBC86F4321447CA3873BF7378583908A04B3AA00F37C5C368EF6EE9DDCC64447DDD383F46679803EC2C2E8B4FBDE6D973BFD0CB87887C03EBA1B88CGG50A0GGD0CB818294G94G88G88GGF9
	54AC7C03EBA1B88CGG50A0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGF28CGGGG
**end of data**/
}
}
