package com.cannontech.tdc.windows;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import com.cannontech.common.login.ClientSession;
import com.cannontech.roles.yukon.SystemRole;

/**
 * Insert the type's description here.
 * Creation date: (10/9/2002 9:47:26 PM)
 * @author: 
 */
public class WinServicePanel extends javax.swing.JPanel implements com.cannontech.tdc.SpecialTDCChild, java.awt.event.ActionListener, javax.swing.event.PopupMenuListener 
{
	private javax.swing.JTable ivjJTableServices = null;
	private javax.swing.JScrollPane ivjJScrollPaneTable = null;
   private ServicePopUpMenu servicePopUpMenu = null;
   private javax.swing.JButton[] buttonsArray = new javax.swing.JButton[0];
   private javax.swing.JComboBox comboBox = null;

	private JButton jButtonStartAll = null;
	private JButton jButtonStopAll = null;
	

/**
 * WinServicePanel constructor comment.
 */
public WinServicePanel() {
	super();
	//initialize();
}

public void actionPerformed(java.awt.event.ActionEvent event) 
{

   if( event.getSource() == comboBox )
   {
      getJTableModel().setYukonFilter(
			"Yukon Servers Only".equalsIgnoreCase(comboBox.getSelectedItem().toString()) );
			
		getJButtonStartAll().setEnabled(
			"Yukon Servers Only".equalsIgnoreCase(comboBox.getSelectedItem().toString()) );			
		getJButtonStopAll().setEnabled(
			"Yukon Servers Only".equalsIgnoreCase(comboBox.getSelectedItem().toString()) );			
   }
	else if( event.getSource() == getJButtonStartAll() )
	{
		
		for( int i = 0; i < getJTableModel().getRowCount(); i++ )
		{
			NTService serv = getJTableModel().getRowAt(i);
			
			if( getJTableModel().isServiceIdle(serv) )
			{
				getServicePopUpMenu().setNtServices( serv, true );
				getServicePopUpMenu().execute_Start( event );
			}
				
		}
		
	}
	else if( event.getSource() == getJButtonStopAll() )
	{

		for( int i = 0; i < getJTableModel().getRowCount(); i++ )
		{
			NTService serv = getJTableModel().getRowAt(i);
			
			if( getJTableModel().isServiceIdle(serv) )
			{
				getServicePopUpMenu().setNtServices( serv, true );
				getServicePopUpMenu().execute_Stop( event );
			}
				
		}
	}

}


public void addActionListenerToJComponent( javax.swing.JComponent component ) 
{
   if( component instanceof javax.swing.JComboBox )
   {
      comboBox = (javax.swing.JComboBox)component;

		comboBox.removeAllItems();
      comboBox.addItem("Yukon Servers Only");
      comboBox.addItem("All Servers");

      comboBox.addActionListener( this );
   }

}


public void destroy() 
{
//   mainPanel = null;
//   connectionWrapper = null;
   buttonsArray = null;
      
   System.gc();   
}


public void executeRefreshButton() 
{
   getJTableModel().initTable();
}


public void exportDataSet() {}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G72F6D7ADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DAFDD0D45795F7D213F19AB313F4728735CECB93F56814B624B1D352528CEDCCEB925B100EC96C44A62621C926CDB2CEC2B22635B3170595E2D0A1E0DC909179D091A478B1E15DB1C6BECCC457C881919011476EE3F972F8EFF97B00DD490CBD675EF75FBE56DDE8F4424CEF6EBB673EFB6FB9671EFB4E39EFA1C53B636D1936A2C2B26D4478EB4CA444DD812DB3F18967A5ECCD49A1163FBD00826277
	E4C31B8CFD258DA9B979243EB79768D5C027FC121233831E8B49115E74F2F80831CE8A743DDB156E1EFD1D1F4BFC1D9CCA0F7666C13B9510863C8C4049ED387EF7FA9D865F8174D6639911E9A3E485FDE750653070679B6FA5996D7CFCFC67073D25C6FF9E50D5009E001C9F1E7383AA5D6DAE27C1775F93CFB21E5CE75B02EBD96667D8E50CDDEF34656481EA08ACB2B2D0866D7176D15F890FBAD62F058445653AA8A91D22B6A9F944F6C191E5C2868BD02EBBDC39F48CA17B01E6B2BC33653831463D2371E45D
	7DA5B1E5D8D24056DED99016A16E7A05CD410FF06EBFD01B6238DC20CF8218FCDB376D374A87D07DFA7D1424F85529408D12B2B93775F1B9D3E01E6305487B7EB11CBF8368BC202B8A397C1F6D637277CCBFC0D21EF95336AD067C738A195C8905E17973348B1B0A29CF38945C57F4684BAD82FF841C848C826681F6D2DFFCD7BEGED53105E220E0D29CA135F5FACFB35F32AD714615965029E0357A1FAD44DCB887337BBDDF5D1646108AB1A69EBE17F38C838FFA4F6ACA309735E32592368367C599ADB22B1C7
	EAD1D8B72657F3AA6A358E789B81C55BD3F26AGEE003C1D6B757958113974EAD3F4D1D38439F3E248AFE8FA7304A4FBC54D506F0F3D3369978DEC5C8B5FF89C33CB42912BDD56BEF456169345AEEB939AEC8B36B3FB44EF8F5B052DDD5497EB6D546DB76E87A2FDBB7A6196977A61EAF84EB4FC1471F7D07E7E0F43FCF6AEFEC47D96E9A774AD7E187BED4FD62E03BBFB9149BF640E2AC35C6F1AECE487134731A3F2EFFFB10EF12695780300A060EEC092E095E05D8E3E3F5F9D7B647F745BF64B5E3231AD4A47
	500AC1DD935A1400AEA89E31DB50A4E1C8968344F8472D206CC3B3467D171EA78F2C712528081A20CB2A82338BE3A2B8D2003D53279460BB1AC8FB3B24B19196987393BE6F10D2882DAC84748BFE2F200B61B7D87FD585EDB9A9F4430CB081BCB7B850667751B8C760399440EC751206BC9D68058EEE173FAB9FC1FB969CDB14DD2ED64903E288DA082F7F120A31C41C94953DD5740B0A979E82C65F2B6A87E13B8C2B264CEFD0BE1E119690BCE0725FD68B989F6630DA19777F0B768B3A2EC9C3933A58A96A3A24
	784263EDFE14A5B063788588536D0C9FFDAC9E74CA862CB4753B53DFE8514F2D2A23937EAE5CB943CE35B44E2E05678C874F0D05AE4C150EE26E27EF75F0BFED5D321454BDFBA62A1FA20E96B37FF497C71E35DDF44E709ACB0F75FC43F39F89C7F174B39A0EAD0F5E729AD93374B13B7FE69C6D2E03ACF783120A395DFF6FAFE7672DD988087C041D57E047C4EF584B583BA96331FD6C317168BE7644F8F49FFBFAFCF69FFAFEBC1A8F313E9747B13728D30A45A7DF997F081EE1C5912950669042F27BDBDDE88B
	C911740B1257A76A1D52B591EA14E2742DC76899DC894FEBCDDF1A2A643ED4F56D47A475CAF554FC96373455F634046D6F691230AFB11952B53439A80BE3E02896F5C251997F9F74FC9BFCA33E347B233F5B6DC7FF9C95C3BC8E79B7910CA37204E86A16A4FC08B65624B158AABC30C67D575CE72D7FC6C0BE2C9597C29BDF42EB1ECB9A5AB93288E3BE739E15BF995EDD85C8AF61BE731C3F58F2D68DE7E9D1E519195E5C13474673F18F45718991C2B19E6633269ECF527D3276F59BBA317EBF0F23CE32A58EEC
	ACC11D524C3A907384633DC6F93DF018DE4E66F977DC3E35DEDD706B692CE87B07B839136DDB574E481CD52B53B88C7C79BB396E67B4EB1CEA8A84D40FC4D30231976F54204DE98DF9D51894027581289FFDAE5799CB4D18C8E3993E9F3713570CEBAE232D5300FE9DC0CA79FA9B2378CAE6E0B33443129600DA47AB86394DF28228BB66A0034DEB3343D6DB242EBB945517E92CAAE5B628AB0D344578844AF19A787D26EC37858AAC3600BA92D22E8FF60A4B5320E7B15FB568233A1D7BAE61316F419BEA9BFFE9
	B8DE4EAF3DB1F6329A46BD00760AAB0D2CED3AA9BF21B43236F971B22FED12212FF4974F353EC954EB9E50F76F62FABD21EF3168552ECA0AEE6AB4B90167CF55A41F24F05EB465C195CD83924BD7AB699E47431C4B810C37349FF9EB00DE3F0B67170E81BC5FC5E51C6E27F403C927DF1EABB760581B4D9BEC0D7F4C3946EC79E5AEB09B4913389FC11376D3BAE44C8D75CB5FA66A3BA1F5820C1DDA0676FA4F0CDFDDC04B00FB8189E52C86E0676D41A90C9343D02C41B16B740B9E32F8B70ECD6967E3F301AE85
	9C8774833E8630B59DA7B0BE1D6974E8903B70FC52C043E267DD835658193E1B2D19DF8E77B9G732932CB7C9EF99CF8FAB9176950A0563AE68C289726F4176BCBCD72B68BBEB75510450161CB39467844F29E876E25F326833D91D03407672360DEFEF7CCDC360838671D0DFA77DD57F558D6370779C37B1E7099AE073BA3DB73EF7DFC4D05503FE28F7272AF97C56C7567B6C26A21E57D4DB37A714C5790EB3FBE239F657D6B0C714BE6745BAD6319EECFD5F35DB6BE15C064070F453C5767D5B01D9A0C5659D5
	C91CDFF4EFE2F6771D5AC522465E708E937EFD617101DC437FC7AA386D63BF2535A9504900348AFE862BAE5D5C1D7170CD0E9B7D4663D86C9B6EC779DF2FE031AF53642B141FE161B37FDDE47AAF29CC4971D67218D891C45FDF89F4BAE0A32028AAA567A8203D4A7C3EB059C0663827F529FEB7940B32658A4174FB2D7F666E523F4D61FE327CD7771164BB5E4F7CB0061FF82B0CEFDCC61B56D1C979CC3F07C3285FF268CB83E4G722AD3F24A814EEA2E5FE7D3F5CC3FD3DEC9EF52F541B3C2B3B0D72EDB9227
	42EC8CBCCC37A78738EEEE18EB30FAE6EC562BE3653322C65C0B855511792C0C72632AA3735932CFF9BECB003E3C3D5CD765FEE27A510465B97E522D5EB166765BC73E55B5583E5486E35FC18E87235FC11C4168F7109341596FA0DF84E35FC14E852DF79066D77F3E06F14E8FFB31901084D881D833177B548B418A66D3D17C6994DCCADAD47A55C2676BBF9A0A2D6B6321393FF57CA904F562102A6B6A984E4F792BC338DF3A6A27CC467BCB88ED84E54D084EDE655F95C298A7E5F11832D99D73B20D1D992017
	E39F2FED5E71A12F8E68AE13F7EE0457BBA37B38FF16B4526F5BA6DDC56955A61D78692D78904EBB371FBEFECBEB4CD58BB13BE5CD475E3B4D53F36FDD61B42D7B4166DDA13F48F80EE9DAFB8E8F87C453C7F6CD671B3C36C03B9A1070B2426A1B8D83783B4424286912C710D992EC9634F6D516BCA1625C0FFBD4F88557B98858300F57C51BC7501FC720483D26AA7A0DA309B1F6AF5D5FA64057C02F27BC6FC86BAC366179A74BC08A0CC983D49AF4BABC7F8CEA0767B3F9DD38358D471D211519F1C58D705FB7
	AEB4DAEBB4E78D789D54067D507AEB38AE2F788EC1EBA9876AAD55C0FD77290E2E3616A677154E160E73EEF7F3D34715A67745265ECE42F3CE7C14756E645C5C98736E342006795EE223DDF2FB252143E7A3C7E6116174790E363E73DFF5C513E2B79D8FB5AC61FF6BBF0F46DBDD033FA5DD3791D36E1773F82EAC7D698FC86B736FE7D646105BF10049BBF2A0DCAF2EF90857D4D727E35CE7690E30DA6A2EC6FEA70FBF40EB29D71D181F56G3D9E10DB4BEB0BC0963EDB8E7471DA9E971326EB216D962E9AD584
	26DCD5231191186D1A6A87978EB1AE7DCAE0687874A56B5E64AF5AE34B0C21E3FF2D21E3ED643DB6A39B7DF5B1701381EB8199005CBA2887GC78127817DG9D3090908FD88ED889D883F883C08EF2DDD658EAE65145388450C02F896CDE49E252779AE72B090A8E462A9B7A68B77B7D87A3EBBE8F659FBD98D9733D6164B55FC968DBE24A9D4C42B94DDC484CEC44186BD91857705B19A44852B57A392393DED4FC4C06746AE63CEB4F2E0E25BFE439B360C267072E0A9E3DCB7019E3F26D31E365B67B5C3152E1
	0F1E674A6CB76639CAFB74BCD7E33766B9462B37639970411E75186F9DB1F93DA64FC967C45EC5492B0F187C93D4AE641F96F1C14EEF25F2CDD1DE0F41EB3370FA8D5ED7F6B4AC288C21180F6D34E33E9C16E419519769BB1282615F34C7BF15D1728B9639851307F2337C7C13C61E1FF36BF97E9DBCC6CC1F14AC4FEFBB3F7D7A7143DB7E8EBE9B58F701D4B3E754014EA7810300F900F8FC26716C55C67EFDAE351E5F114B1C11F764FBB22DF75CF6E7649D994FD8387FB6E7649DF96678654E48BBB29F4FEC73
	4F9971E183481577496C358E5BE31B1346DBF89757E33C9C4AF39ABC9667053DF944BC63540D993FB438A54C67EECC587FBB148CF5C01B78A63187B4320DFF6A28EF234E38EE7208AB6B157B33ECA43B0DECE207330DE46389EBA3F6FAG5BC8969C4EA07E08467FEC2CE1713D663AB53EEF385D91B3778EB6B0DB688D11715D67C5DD13003F2A01473ADFFA305E099523BA4784D9062AE734C971E23CEA8E69E278CEA0FBF17EGFF4768E4FD81AF4AFD55B266FFD0CB8788A0BA9D6F8B8DGGF4A2GGD0CB81
	8294G94G88G88G72F6D7ADA0BA9D6F8B8DGGF4A2GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGC58DGGGG
**end of data**/
}

public javax.swing.JButton[] getJButtons() 
{  
   return buttonsArray;
}


// Overide this method so TDC knows what the JLabel should display
// for the JCombo box that normally displays "Display:"
public String getJComboLabel()
{
   return "Sort By:";
}


/**
 * Return the JScrollPaneServices property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneTable() {
	if (ivjJScrollPaneTable == null) {
		try {
			ivjJScrollPaneTable = new javax.swing.JScrollPane();
			ivjJScrollPaneTable.setName("JScrollPaneTable");
			ivjJScrollPaneTable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneTable.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPaneTable().setViewportView(getJTableServices());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneTable;
}


/**
 * Insert the method's description here.
 * Creation date: (10/9/2002 10:07:00 PM)
 * @return com.comopt.windows.ServiceTableModel
 */
private ServiceTableModel getJTableModel() 
{
	return (ServiceTableModel)
		((com.cannontech.common.gui.util.SortTableModelWrapper)
				getJTableServices().getModel()).getRealDataModel();
}


/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:51:13 PM)
 * @return JTable[]
 */
public javax.swing.JTable[] getJTables()
{
   javax.swing.JTable[] tables =
   {
      getJTableServices()
   };
   
   return tables;
}


/**
 * Return the JTableServices property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableServices() {
	if (ivjJTableServices == null) {
		try {
			ivjJTableServices = new javax.swing.JTable();
			ivjJTableServices.setName("JTableServices");
			getJScrollPaneTable().setColumnHeaderView(ivjJTableServices.getTableHeader());
			ivjJTableServices.setBounds(0, 0, 200, 200);
			// user code begin {1}

			ivjJTableServices.createDefaultColumnsFromModel();
         
         try
         {
   			ivjJTableServices.setModel( 
   				new com.cannontech.common.gui.util.SortTableModelWrapper(
   					new ServiceTableModel()) );
				
				getJTableModel().initTable();
         }
         catch( Exception e )
         {
            com.cannontech.clientutils.CTILogger.error(
               "Exception occured when opening the Service Manager." + System.getProperty("line.separator") +
               "Be sure you have access and user rights to operate the Yukon Server services",
               e );
 				
 				JOptionPane.showMessageDialog( this, "Be sure you have access and user rights to operate the Yukon Server services" );
         }

			
			ivjJTableServices.setDefaultRenderer( 
					Object.class, 
					new ServiceRenderer() );

			setTableHeaderListener();

         // init the popup box connections
         java.awt.event.MouseListener subListener = new com.cannontech.clientutils.popup.PopUpMenuShower( getServicePopUpMenu() );
         ivjJTableServices.addMouseListener( subListener );
         getServicePopUpMenu().addPopupMenuListener( this );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableServices;
}


/**
 * This method was created in VisualAge.
 * @param event java.awt.event.ActionEvent
 */
public javax.swing.JPanel getMainJPanel()
{
   return this;
}


/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:51:22 PM)
 */
public String getName()
{
   return "Windows Services";
}


private ServicePopUpMenu getServicePopUpMenu()
{
   if( servicePopUpMenu == null )
      servicePopUpMenu = new ServicePopUpMenu();
      
   return servicePopUpMenu;
}


/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:51:22 PM)
 */
public String getVersion() 
{
   return "1.0.0";
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}


public void initChild()
{
	initialize();
}


private void initConnections()
{

   getJTableServices().addMouseListener( new java.awt.event.MouseAdapter()
   {
      public void mousePressed(java.awt.event.MouseEvent e) 
      {
         int rowLocation = getJTableServices().rowAtPoint( e.getPoint() );
         getJTableServices().getSelectionModel().setSelectionInterval(
                     rowLocation, rowLocation );
      }
   });


	getJButtonStartAll().addActionListener( this );
	getJButtonStopAll().addActionListener( this );
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
      
      JNTServices.getInstance().setMachineName(
         ClientSession.getInstance().getRolePropertyValue(
            SystemRole.DISPATCH_MACHINE, "localhost") );
      
      getServicePopUpMenu().addPropertyChangeListener( "PopUpChange", getJTableModel() );
      
		//add any JButtons here
		javax.swing.JButton[] buttons =
		{
			getJButtonStartAll(),
			getJButtonStopAll()		
		};
		setJButtons( buttons );
      
		// user code end
		setName("WinServicePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(366, 287);

		java.awt.GridBagConstraints constraintsJScrollPaneTable = new java.awt.GridBagConstraints();
		constraintsJScrollPaneTable.gridx = 0; constraintsJScrollPaneTable.gridy = 0;
		constraintsJScrollPaneTable.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneTable.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollPaneTable.weightx = 1.0;
		constraintsJScrollPaneTable.weighty = 1.0;
		constraintsJScrollPaneTable.ipadx = 342;
		constraintsJScrollPaneTable.ipady = 235;
		constraintsJScrollPaneTable.insets = new java.awt.Insets(0, 0, 0, 2);
		add(getJScrollPaneTable(), constraintsJScrollPaneTable);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
   
   initConnections();        
   
	// user code end
}


/**
 * Insert the method's description here.
 * Creation date: (8/4/00 9:01:12 AM)
 * @return javax.swing.JButton
 */
public JButton getJButtonStartAll() 
{
	if( jButtonStartAll == null )
	{
		jButtonStartAll = new JButton("Start All");
		jButtonStartAll.setActionCommand("StartAll");
		jButtonStartAll.setMnemonic('t');
		jButtonStartAll.setPreferredSize( new java.awt.Dimension(80,23) );
		jButtonStartAll.setToolTipText("Starts all available services");
	}

	return jButtonStartAll;
}

/**
 * Insert the method's description here.
 * Creation date: (8/4/00 9:01:12 AM)
 * @return javax.swing.JButton
 */
public JButton getJButtonStopAll() 
{
	if( jButtonStopAll == null )
	{
		jButtonStopAll= new JButton("Stop All");
		jButtonStopAll.setActionCommand("StopAll");
		jButtonStopAll.setMnemonic('s');
		jButtonStopAll.setPreferredSize( new java.awt.Dimension(80,23) );
		jButtonStopAll.setToolTipText("Stops all available services");
	}

	return jButtonStopAll;
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		WinServicePanel aWinServicePanel;
		aWinServicePanel = new WinServicePanel();
		frame.setContentPane(aWinServicePanel);
		frame.setSize(aWinServicePanel.getSize());
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
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


public boolean needsComboIniting()
{
	return false;
}


/**
 * Method to handle events for the PopupMenuListener interface.
 * @param e javax.swing.event.PopupMenuEvent
 */
public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {
}


/**
 * Method to handle events for the PopupMenuListener interface.
 * @param e javax.swing.event.PopupMenuEvent
 */
public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {
}


/**
 * Method to handle events for the PopupMenuListener interface.
 * @param e javax.swing.event.PopupMenuEvent
 */
public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) 
{
	if( e.getSource() == getServicePopUpMenu()
		 && getJTableServices().getSelectedRow() >= 0 ) 
	{      
      NTService service = getJTableModel().getRowAt( getJTableServices().getSelectedRow() );
   
      if( service != null )
      {
         getServicePopUpMenu().setNtServices( 
                  service,
                  getJTableModel().isServiceIdle(service) );
      }
      
   }

}


public void print() {}


public void printPreview() {}


public void removeActionListenerFromJComponent( javax.swing.JComponent component ) 
{
   if( component instanceof javax.swing.JComboBox )
   {
      comboBox.removeActionListener( this );
      comboBox = null;
   }

}


/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:52:15 PM)
 * @param hgridLines boolean
 */
public void setGridLines(boolean hGridLines, boolean vGridLines ) 
{
   int vLines = ((vGridLines == true) ? 1 : 0);
   int hLines = ((hGridLines == true) ? 1 : 0);

   for( int i = 0; i < getJTables().length; i++ )
   {
      getJTables()[i].setIntercellSpacing(new java.awt.Dimension(vLines, hLines));
      getJTables()[i].setShowHorizontalLines( hGridLines );
      getJTables()[i].setShowVerticalLines( vGridLines );

      getJTables()[i].revalidate();
      getJTables()[i].repaint();
   }
   
}


public void setInitialTitle()
{
   // we must have the panel realize its the first time the connection
   //  is being observed
   java.awt.Frame f = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
   if( f != null )
      f.setTitle("Yukon Servers");
}


/**
 * Insert the method's description here.
 * Creation date: (8/4/00 9:08:24 AM)
 * @param buttons javax.swing.JButton[]
 */
public void setJButtons(javax.swing.JButton[] buttons) 
{
   buttonsArray = buttons; 
}


public void setRowColors(java.awt.Color[] foreGroundColors, java.awt.Color bgColor ) {}
public void setAlarmMute( boolean muteToggle ) {}
public void silenceAlarms() {}


public void setTableFont(java.awt.Font font ) 
{
   super.setFont( font );

   // Set the values for the table model
   //get.getSubBusTableModel().setFontValues( font.getName(), font.getSize() );
      
   for( int i = 0; i < getJTables().length; i++ )
   {
      getJTables()[i].setFont( font );
      getJTables()[i].setRowHeight( font.getSize() + 3 );
      // set the table headers font
      getJTables()[i].getTableHeader().setFont( font );


      getJTables()[i].revalidate();
      getJTables()[i].repaint();
   }

}


/**
 * Return the JScrollPaneServices property value.
 * @return javax.swing.JScrollPane
 */
private void setTableHeaderListener() 
{
	javax.swing.table.JTableHeader hdr = 
			(javax.swing.table.JTableHeader)getJTableServices().getTableHeader();

/*	hdr.setToolTipText("Dbl Click on a column header to sort");

	// The actual listener is defined here
	hdr.addMouseListener( new java.awt.event.MouseAdapter() 
	{
		public void mouseClicked(java.awt.event.MouseEvent e)
		{
			if( e.getClickCount() == 2 )
			{
				int vc = getJTableServices().getColumnModel().getColumnIndexAtX( e.getX() );
				int mc = getJTableServices().convertColumnIndexToModel( vc );

				java.awt.Frame owner = com.cannontech.common.util.CtiUtilities.getParentFrame( WinServicePanel.this );
				
				java.awt.Cursor original = owner.getCursor();	
				owner.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
				
				try
				{
					((com.cannontech.common.gui.util.SortTableModelWrapper)
									getJTableServices().getModel()).sort( mc );

					getJTableServices().repaint();
				}
				finally
				{
					owner.setCursor( original );
				}
				
			}
			
		};
		
	});	
*/
	
}
}