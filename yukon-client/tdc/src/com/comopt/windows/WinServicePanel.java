package com.comopt.windows;

import javax.swing.JOptionPane;

/**
 * Insert the type's description here.
 * Creation date: (10/9/2002 9:47:26 PM)
 * @author: 
 */
public class WinServicePanel extends javax.swing.JPanel implements com.cannontech.tdc.SpecialTDCChild, javax.swing.event.PopupMenuListener, java.awt.event.ActionListener
{
	private javax.swing.JTable ivjJTableServices = null;
	private javax.swing.JScrollPane ivjJScrollPaneTable = null;
   private ServicePopUpMenu servicePopUpMenu = null;
   private javax.swing.JButton[] buttonsArray = new javax.swing.JButton[0];
	private javax.swing.JCheckBox ivjJCheckBoxShowYukon = null;

   private javax.swing.JComboBox comboBox = null;
/**
 * WinServicePanel constructor comment.
 */
public WinServicePanel() {
	super();
	//initialize();
}

public void initChild()
{
	initialize();
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

public boolean needsComboIniting()
{
	return false;
}

public void setRowColors(java.awt.Color[] foreGroundColors, java.awt.Color bgColor ) {}

public void setSound(boolean soundToggle) {}

public void setInitialTitle()
{
   // we must have the panel realize its the first time the connection
   //  is being observed
   java.awt.Frame f = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
   if( f != null )
      f.setTitle("Yukon Servers");
}

public void exportDataSet() {}

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



public void actionPerformed(java.awt.event.ActionEvent event) 
{
   if( event.getSource() == getJCheckBoxShowYukon() )
   {
      getJTableModel().setYukonFilter( getJCheckBoxShowYukon().isSelected() );
   }

   if( event.getSource() == comboBox )
   {
      getJTableModel().setYukonFilter(
			"Yukon Servers Only".equalsIgnoreCase(comboBox.getSelectedItem().toString()) );
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


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G3DE8D5ADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DAFBD0145795E79A13260DE9CD4A9F44E6DA92B5E327E4E2DE9DD3364624E6E29A26E926E634B3A6310D19BA4D23A4120808FAD99E3B3CC1C8A48DE9C8C585A5C423B3A1AB0A4BC3D9D81582728AD37DE0BF61530F5D4D6E0700226DB977716D47328BC91DB07373FE775C57B9671EF36E39F7A5459FC72727180B89C9C9A77C6F0B94C28E6CA66478ED4D0D0256939FEBA1063FCFG05E462DA9A1443
	50362458E4A9A0C3BEAB54A721DE86751D70DDC4D4DF54AE7888330E975A6E7DF749221957693709F5AC34FE0FB5874ABCC08450968262818564B1EB89272F04FA9E7FC6241889515E42BED6E70901FE78AD566FBCAF8F3F0DFDD6D9CBF93B89682BGD620173E9D18B313F2F7332D06577B6F08A6893F5BEB4E45358C738BCC7231513CACA38FD0C52412617F7C934A5A36CBC34786BDA3553E8949ED33F5AB6E8E59F7D999145BA4372C92D23091792AF6DA6998C25640BC0C0767F3EB799A111722493A857F88
	4BC367C636767846G8F092FF5E5D4A4605CDB29CEF15CD2E80F87B07E7272AB0C7C81BCDE2DFACCF13BBCE37EE91CB2BE1BB2851FAA4C7DBA1D5F5E0A73AF04EF93E0D502607F68DE41FF4F5587086979B74C79E178BF708E633B671DG7F6ACBDD999FD01B58F08477B5925A168262G710002F7E3AD87G9FD2DBDCE05B8725FDC0EB700C0CF85CF62F37DEF579CEF9DC328A5FB69B34F0DA3BBC68713988E176F648D995029F0166F7275ADA40BE3A09300F18767BC8CC441B66749032BD72C225391F4F61FD
	B7A09B13EB4ED114EB9E50E3GABGEB81490052CDC22EB7EC07E6134B6156E41FDBD2BBC6873C12CF2B9FD5D4176C637225E64EA4DFA830F1055FFA9C534B62C3C63D2CFD3855EC892117350B8F18EBB6B1FD34ED8A68052D5D92F6ED6F26697BC17DE3935A6123548E6740F78A37D1C6FF1C525798684CAFCED2BA56B7C01BB3D158EDCF1E104119B61F949CF40614A16AC93BF9C9A263E7E5E2705E1E2F43B813G74024D31168A40F140F940A4E05EE6313FB5C7BE7B06F65BE658DBB6762DA36FC3A90FEBBE
	496176EB12FBD06E12FC0AB4204AFE427BA49DC15E87260C5BCE7DE99056B8A93BE51F24A99EB74CAE0D48E0C8FE5647DADB08FDFCB2ED6DD4C6E4D8E044CB443C8535C5D02A12DFBB6BF5C91A9C68415ACBEAD1171725AE1891A6006FF51BD167ADB44EAD016FB8G53556D4711D61E94EB29CD92FA79C96D8EA8DB4130E555E6EBD486118D49B7A1563F7DA8469279326C569AE52F6CF641071F37C59E4D8C686502C767F9BE25230FB4G63FE1D9EFD341051E18E2316C57BE25AAEE91ACF999855648ED95394
	77D0E07C924A0BFF0A7B798977F1BB5AD89B48B58C9857657B72D811C1BE2747F3E9545B09BB4775A451B8BB977AAFCC92E7E3ECAB1E15AB13041D3E59A36C34B1F79129FA21A5241DA256A7B17BCCCE8A763527681C01B5CE3576FCCB3F8F464A2450BE9A08AD97EEF80D54995A185E4D8D287772AD60F70073DB045E35E3E54C5F6AA53FACBC6C0C8FF6CCF6852C0C753DD8975E463E2E8BEDE3E3F521ED6CDA5D4CB6F4F3DDA89BE2EDF36B70EC710C398DB6F9C75D8E6A43EE37CC19560790F6363F59093AD0
	5C0A562D3806E42DC339A2C30E32055A967541F1781E3BC5585258AEE1CB65D76EA14B4F6D8EF91EC5ADEAB42F5F4276B7F1CB401698CFF79EC71D4B2ABC820AEA700C3AB5C60F227E4D69BC3E7C509E3A6FBCBB5A63A5F902770B34E7900CA36A282C4B36C44AC69D7B149158AAF4D81E7F0DE79A733F1264D88B660A35D036A50B1C67AB9F6AB9B889E3B6F32491F99A063E130048E4E1B39F56FFE070D5EEAC8D9ED5E52A5777445AD000FBA8FFBDAAC3A8C6E7EE556548EF5891545645E5E26DBB9BD0A65590
	87E21229CCFADE086784237D1852BE06FFF6270BF377D401B1DF75AE3B1A9AEA7F907149EC5F5236861FD95EA69A0701FEF82B107D4E86E31C327B7D1EC1059E89FCAFB6D6224EE98EF9D13AAC0DD77BA1FF9C3259DA8CB963DFE8AC437E4DDBC54E78D08E6ABA826AD100A57AFA76466936F222B18B4A8B0A4F8F390ECB9E97BAF3B6226CF886F132484DBEB76AE2793A03A1ED1946AA2E033553F4712B9348C7A250F336895E1EEEACB4688272C8B8F207E02784BF23CD294CF6F9FD22A9DD58AE91316F2F53F2
	1B32ED01F8F9F85B745849F218BD39282F66EDAC07C9576987A93D5FC0E7394DD22ADF2CFB21ED55B6F156461CC439EA3647DA1A378B3972CE649A64EA73A8EECD17A976847A1F47278CA9EEC1DBCEE910C55300444E2BEDF40FFBE0CE9540E81FE4A3ED9E5497EF9767CBD5AE5262203EC127FDC1E9D6E232D48198EDD90E710C403E41FA098BC17B26E0EB2C1E32C6FA08FEB31DB33301696A411338AF63FA7DD1DA1F60F397117732B2288DCFF8C6C16951846F5AD5D946B866841A863895A078CCA58F0EE13C
	38GC99B38DB07D79EA413A9B836A6DB0C5DG7D138125C02F857483581AA547B0CE35F48C7AA006211F5200435A9247B0CE35B48C4B03176ABD639D431E315E51CB9864D80CD5F20D3C2D00B9B1F70807B24DEC3274181107BB77959B7A141AD91FC6A8996F874FE3EE2C470CEAE9CC3359CE7A94D73DB464241AE0F123BD4B4A7DB69A4632389111C5F3F92847832C29A6FEFE0D6F91F74D187B6693E7C4EB483B723A4E4F4DE529CC0F0D3CC41F0F003BA6DB5314AD560C00762529A84F5F320D72201E6A4C04
	D4C3495ACBF30C6D98A3AA09317D7414F6647745A963A32634279B46B3591E5DADE4CBFEF6B1D19F7DB26CBBGC9E3B21573F2DE57AE9275D0F786537BD053C7A4E42C8ED882916F9177D01FC4BF69CF933A3FEBAF6ABE926AB10095E9E2BF0FF5FD98FADEE1C5FC4EE776D939CCEB53443EDDBBE1FC63B065FFEE4E8EA3DBF1BA1369F0FAE05F52E05F18AFF7E7FD5B7BAA0B1F89541EB418B3052F4B68DBA9BD42C0E7F17516AC7116B2975A6281AC2EBE6CC03F684CB0D93C00DB815100650035G667B366683
	E4163BDE27476B0404D3B5DCC318FCCFE47FFF7771A7ACC23F0F7C7ADE3274164DA961749B1F4174DAE0E1E54559DD06FC7AC4AB4AF79C5A7A819100A8C08CE005C548773D66AAA6DF13CB516C1AA68D8E53D3DC88572548E381B286ADA65BFD39C236B5B0576BFCFD5D262D26B0E7E2EE856EC5315594F45F5FC9696586BA5B3B1F6F95777D435096E595E74D6F77617A4906F58AB2CD1672CCD62F491C3EBE534992A77A2D935A07819100A84052CC210F3866D95FEE1A46E7F83BC94234C73C63E4873E67E65D
	687DE9A630B596FD27EBB07D189C616FD73FF5043EDFBD6388FD3FFA4EB1737DEA35A37C7D6AA50771FE4576766DD33837EBE0AF93GA5D9101BG0EE7093DD5DB7E4576B6041DB741052B41C3DFE4B4317E19567032F6354E7E0EF32E950DE14023E91E911CDF5065D65CAF4D6325C4FEEFEDC59DC14AB62C312E1C6EED45782B4A97A81965E83F21F13E9F640AGB0DADC9E52A221EE52E9FB72C5AE7707AC61B7C595F8D7AB4B96750FE83DD62FAF5AFBA3B604734EEE274BEEE80D5972BBA67B2B745CBA8F7C
	E859C29F557B11F6AB546F56E9AFD65137AD28AF57E9751F8A3D45E58B5BD96A8CEF8B0FBBC35B7DD34E50F67F2CF366372BBFBAE7760B24D361F9C9B9BD3BDDE61D2677B530274E892F4C6F3A27691D6142853F2C5BFF7169821D667037F97C8ADEA2D94E39AA97FFCF3AAC7BB4E590C2A1CDC66AA5DF1BC7D586A7C8FF8E66C8FF27F67AA920B4C764312F64212F8E4365640A472DCD9FC978586D541E6D00E8289767A26DE8FE63ACF6E144BA9817G58456BD61863FE4861D62708BCBE4F0163DAE8A64D1F96
	7CA2E7BEDDE14C197BE1AC5EF5A2200C4C9532A4D39B734B7826A03B985FC57B455DBFC66F57C4E9A3EE998E94E510756B27340DG135EAF218AFDD1034D0B839A4CF2962A04495E7B69EC32279BE4AF4803B1F9CCF6BCCF4B61FBA205FD3335B2728EC2E9C87D2A8D99E5F5D7D3FB2723416EBC575150FE46692C37371F33BB3B6D3D9DC464A851E3467BFACD5697E16F6B8772F86E4E4B646F6F62BC3C1FFFE8869E1A4F34BB7A4E1C6E8C454547F4BC5EBF45DFFCDDA89A5323290B68FC7FB9C764D6253F7CA9
	E9DC3DB9C568A8186FC94EEFDCDEA0F72DA96B61F979452BE15ED028B5315C79168AE18BE979A29FFF39927319DA28F78254FC31777B76533D770D72BB5D1F2A305CBB3117737D5CD7C6FD974C7F441C92066FB9797C9E119F60FB3AEEC0AE2A1B4B980A7979392B975BD74038F579A26EB51F5D8FE517F411E7C218A6FAFC745484E21B4F630590B0412874F50C4FF52E0F7ED8DDGF7D940F1C08FC0854C81C481E2G4B81EBG710034C2B83381D500DAC09B40D5A87848692E1C018FFE91258738CFE2EFA16C
	2C392DE2269CFC3CB0DC1E5A318F77E9EEE1709D231F52238A036F982FD60ABB46C2E8336E90F70C273EC2B5B0995A3BF7071381B21096B023B38397A116F4CAC37A180ADE64C9BF8B180AF95BE73D186F60FB31A2294A957A4457819D5DC32C5D5F977E1C3852B77BB9F1D3DF687C6587FD5373179F75054EDFA27B0C798B235D5507BEB484FB5623777B19CE6B55E98B681CC86BD6DC5A30CE7F85658B694DB2AEA8688FD03E46A82D07539EB150FAB96D31BEBC6BC0E4B8064458E5FD68FF9794D5E575A7E99F
	458DC71F2E0F27A90F0AD7B2709D275310EF16BF5CDBA17287F507482BFA7127EEEE1383066F57AA3F7BFBC15A3E6FF28D76067518FEDE4E83199782D6G5683520A201D464317AB441BF2F911F82759D9997CCEF3C70A711D45D1997CCE03BE96E83FDA997CCEB3F57C7D15416FB4E2BC534D7AA971219F7872964D1C4332BD5EC2E51983FDF1BDC6A315C83B1B5358B9A16D49A13A0FD2B3E67452715CGDD18B1E17FC7E76764CDD0463CC152B5D2022FF9E5E75C6AC499FD53AB93090A5FC77245CBDC35035A
	693A4B07EC292FACC8B513B48749E0FE6BA0E968FC8E12CEFD53C1D241EF4771B7E171E746FF7CDF136BFAF6A028A97C6C297C2F716CC93C39A46CD91FD04CF4E7FD4F947CBB45F554CDA3504F3FA7E27A72EB189B060B079DA3122AC206F8496EF6E1EC2C1F5064C08EBCFA9D67770BBE3C1135CDDEC73EAF9A467C8FD0CB8788332E7C40E38EGGD8A5GGD0CB818294G94G88G88G3DE8D5AD332E7C40E38EGGD8A5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0
	CB8586GGGG81G81GBAGGG1D8EGGGG
**end of data**/
}

public javax.swing.JButton[] getJButtons() 
{  
   return buttonsArray;
}


/**
 * Return the JCheckBoxShowYukon property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxShowYukon() {
	if (ivjJCheckBoxShowYukon == null) {
		try {
			ivjJCheckBoxShowYukon = new javax.swing.JCheckBox();
			ivjJCheckBoxShowYukon.setName("JCheckBoxShowYukon");
			ivjJCheckBoxShowYukon.setSelected(true);
			ivjJCheckBoxShowYukon.setMnemonic('y');
			ivjJCheckBoxShowYukon.setText("Show Only Yukon Services");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxShowYukon;
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
			getJScrollPaneTable().getViewport().setBackingStoreEnabled(true);
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


private void initConnections()
{

   getJCheckBoxShowYukon().addActionListener( this );
   
   getJTableServices().addMouseListener( new java.awt.event.MouseAdapter()
   {
      public void mousePressed(java.awt.event.MouseEvent e) 
      {
         int rowLocation = getJTableServices().rowAtPoint( e.getPoint() );
         getJTableServices().getSelectionModel().setSelectionInterval(
                     rowLocation, rowLocation );
      }
   });


}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
      
      JNTServices.getInstance().setMachineName(
         com.cannontech.common.util.CtiProperties.getInstance().getProperty(
            com.cannontech.common.util.CtiProperties.KEY_DISPATCH_MACHINE,
            "localhost") );
      
      getServicePopUpMenu().addPropertyChangeListener( "PopUpChange", getJTableModel() );
      
		// user code end
		setName("WinServicePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(366, 287);

		java.awt.GridBagConstraints constraintsJScrollPaneTable = new java.awt.GridBagConstraints();
		constraintsJScrollPaneTable.gridx = 1; constraintsJScrollPaneTable.gridy = 1;
		constraintsJScrollPaneTable.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneTable.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollPaneTable.weightx = 1.0;
		constraintsJScrollPaneTable.weighty = 1.0;
		constraintsJScrollPaneTable.ipadx = 342;
		constraintsJScrollPaneTable.ipady = 235;
		constraintsJScrollPaneTable.insets = new java.awt.Insets(0, 0, 0, 2);
		add(getJScrollPaneTable(), constraintsJScrollPaneTable);

		java.awt.GridBagConstraints constraintsJCheckBoxShowYukon = new java.awt.GridBagConstraints();
		constraintsJCheckBoxShowYukon.gridx = 1; constraintsJCheckBoxShowYukon.gridy = 2;
		constraintsJCheckBoxShowYukon.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxShowYukon.ipadx = 188;
		constraintsJCheckBoxShowYukon.insets = new java.awt.Insets(1, 0, 3, 2);
		add(getJCheckBoxShowYukon(), constraintsJCheckBoxShowYukon);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
   
   initConnections();
   
	// user code end
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
	if( e.getSource() == getServicePopUpMenu() ) 
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

/**
 * Insert the method's description here.
 * Creation date: (8/4/00 9:08:24 AM)
 * @param buttons javax.swing.JButton[]
 */
public void setJButtons(javax.swing.JButton[] buttons) 
{
   buttonsArray = buttons; 
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