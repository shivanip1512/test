package com.cannontech.dbeditor.wizard.route;

import java.awt.Dimension;

import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.pao.PAOGroups;

/**
 * This type was created in VisualAge.
 */
public class RouteNameDevicePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ItemListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjRouteNameLabel = null;
	private javax.swing.JTextField ivjRouteNameTextField = null;
	private javax.swing.JLabel ivjSignalTransmitterLabel = null;
	private javax.swing.JComboBox ivjSignalTransmitterComboBox = null;
public RouteNameDevicePanel() {
	super();
	initialize();
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean allowRebroadcast() {

	if( getSignalTransmitterComboBox().getSelectedItem() == null )
		return false;
	else
		return DeviceTypesFuncs.allowRebroadcast( ((com.cannontech.database.data.lite.LiteYukonPAObject)getSignalTransmitterComboBox().getSelectedItem()).getType() );
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getRouteNameTextField()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void comboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}
/**
 * connEtoC1:  (RouteNameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> RouteType2Panel.textField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.textField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (SignalTransmitterComboBox.item.itemStateChanged(java.awt.event.ItemEvent) --> RouteType2Panel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ItemEvent arg1) {
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
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension(350, 200 );
}
/**
 * Return the RouteNameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRouteNameLabel() {
	if (ivjRouteNameLabel == null) {
		try {
			ivjRouteNameLabel = new javax.swing.JLabel();
			ivjRouteNameLabel.setName("RouteNameLabel");
			ivjRouteNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRouteNameLabel.setText("Route Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRouteNameLabel;
}
/**
 * Return the RouteNameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getRouteNameTextField() {
	if (ivjRouteNameTextField == null) {
		try {
			ivjRouteNameTextField = new javax.swing.JTextField();
			ivjRouteNameTextField.setName("RouteNameTextField");
			ivjRouteNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			// user code begin {1}
			ivjRouteNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_ROUTE_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRouteNameTextField;
}
/**
 * Return the SignalTransmitterComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getSignalTransmitterComboBox() {
	if (ivjSignalTransmitterComboBox == null) {
		try {
			ivjSignalTransmitterComboBox = new javax.swing.JComboBox();
			ivjSignalTransmitterComboBox.setName("SignalTransmitterComboBox");
			// user code begin {1}
         
         com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
         synchronized(cache)
         {
            java.util.List allDevices = cache.getAllDevices();
            for(int i=0;i<allDevices.size();i++)
            {
               com.cannontech.database.data.lite.LiteYukonPAObject litePAO = 
                        (com.cannontech.database.data.lite.LiteYukonPAObject)allDevices.get(i);
                        
               if( litePAO.getPaoClass() == com.cannontech.database.data.pao.DeviceClasses.TRANSMITTER
                   && !DeviceTypesFuncs.isRepeater(litePAO.getType()) )
               {
                  getSignalTransmitterComboBox().addItem( allDevices.get(i) );
               }

            }
            
         }
         
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSignalTransmitterComboBox;
}
/**
 * Return the SignalTransmitterLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSignalTransmitterLabel() {
	if (ivjSignalTransmitterLabel == null) {
		try {
			ivjSignalTransmitterLabel = new javax.swing.JLabel();
			ivjSignalTransmitterLabel.setName("SignalTransmitterLabel");
			ivjSignalTransmitterLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSignalTransmitterLabel.setText("Signal Transmitter:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSignalTransmitterLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {

		//Determine type of route and its name
	//create that type with the name and return it

	String routeName = getRouteNameTextField().getText().trim();

	Integer deviceID = new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)getSignalTransmitterComboBox().getSelectedItem()).getYukonID());

	int type = ((com.cannontech.database.data.lite.LiteYukonPAObject)getSignalTransmitterComboBox().getSelectedItem()).getType();

	if( DeviceTypesFuncs.isCCU(type) || DeviceTypesFuncs.isRepeater(type) )
	{
		val = com.cannontech.database.data.route.RouteFactory.createRoute( com.cannontech.database.data.pao.RouteTypes.STRING_CCU );
	}
	else if( DeviceTypesFuncs.isTCU(type) )
	{
		val = com.cannontech.database.data.route.RouteFactory.createRoute( com.cannontech.database.data.pao.RouteTypes.STRING_TCU );
	}
	else if( DeviceTypesFuncs.isLCU(type) )
	{
		val = com.cannontech.database.data.route.RouteFactory.createRoute( com.cannontech.database.data.pao.RouteTypes.STRING_LCU );
	}
	else if( type == PAOGroups.TAPTERMINAL )
	{		
		val = com.cannontech.database.data.route.RouteFactory.createRoute( com.cannontech.database.data.pao.RouteTypes.STRING_TAP_PAGING );
	}
	else if( type == PAOGroups.WCTP_TERMINAL )
	{		
		val = com.cannontech.database.data.route.RouteFactory.createRoute( com.cannontech.database.data.pao.RouteTypes.STRING_WCTP_TERMINAL_ROUTE );
	}
	else if( type == PAOGroups.RTC )
	{
		val = com.cannontech.database.data.route.RouteFactory.createRoute( com.cannontech.database.data.pao.RouteTypes.ROUTE_RTC);
	}
	else if( type == PAOGroups.SERIES_5_LMI )
	{
		val = com.cannontech.database.data.route.RouteFactory.createRoute( com.cannontech.database.data.pao.RouteTypes.ROUTE_SERIES_5_LMI);
	}
	else //?
		throw new Error("RouteType2::getValue() - Unknown transmitter type");

	((com.cannontech.database.data.route.RouteBase) val).setDeviceID(deviceID);	
	((com.cannontech.database.data.route.RouteBase) val).setDefaultRoute("Y");

	((com.cannontech.database.data.route.RouteBase) val).setRouteName( routeName );

	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getRouteNameTextField().addCaretListener(this);
	getSignalTransmitterComboBox().addItemListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("RouteType2Panel");
		setLayout(new java.awt.GridBagLayout());
		setSize(374, 211);

		java.awt.GridBagConstraints constraintsRouteNameLabel = new java.awt.GridBagConstraints();
		constraintsRouteNameLabel.gridx = 0; constraintsRouteNameLabel.gridy = 0;
		constraintsRouteNameLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsRouteNameLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getRouteNameLabel(), constraintsRouteNameLabel);

		java.awt.GridBagConstraints constraintsRouteNameTextField = new java.awt.GridBagConstraints();
		constraintsRouteNameTextField.gridx = 1; constraintsRouteNameTextField.gridy = 0;
		constraintsRouteNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsRouteNameTextField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsRouteNameTextField.weightx = 1.0;
		constraintsRouteNameTextField.insets = new java.awt.Insets(5, 5, 5, 5);
		add(getRouteNameTextField(), constraintsRouteNameTextField);

		java.awt.GridBagConstraints constraintsSignalTransmitterLabel = new java.awt.GridBagConstraints();
		constraintsSignalTransmitterLabel.gridx = 0; constraintsSignalTransmitterLabel.gridy = 1;
		constraintsSignalTransmitterLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsSignalTransmitterLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getSignalTransmitterLabel(), constraintsSignalTransmitterLabel);

		java.awt.GridBagConstraints constraintsSignalTransmitterComboBox = new java.awt.GridBagConstraints();
		constraintsSignalTransmitterComboBox.gridx = 1; constraintsSignalTransmitterComboBox.gridy = 1;
		constraintsSignalTransmitterComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsSignalTransmitterComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsSignalTransmitterComboBox.weightx = 1.0;
		constraintsSignalTransmitterComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
		add(getSignalTransmitterComboBox(), constraintsSignalTransmitterComboBox);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{	
	if( getRouteNameTextField().getText().length() <= 0 )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}

	return true;
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getSignalTransmitterComboBox()) 
		connEtoC2(e);
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
		RouteNameDevicePanel aRouteType2Panel;
		aRouteType2Panel = new RouteNameDevicePanel();
		frame.add("Center", aRouteType2Panel);
		frame.setSize(aRouteType2Panel.getSize());
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
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean noRepeaters() {
	boolean noRepeaters = true;

	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List devices = cache.getAllDevices();
		for(int i=0;i<devices.size();i++)
		{
			if( com.cannontech.database.data.device.DeviceTypesFuncs.isRepeater( ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i)).getType()) )
			{
				noRepeaters = false;
				break;
			}
		}
	}
	return noRepeaters;
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) 
{
}
/**
 * Comment
 */
public void textField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {

	fireInputUpdate();
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G56F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBA8BD0DC57F5120CA5ACABE3114893D511E7302238CA4C5834259DB7F6E6B0C3923911A76E94570AC3BB547134CAA3F7C82356F643B417DDF681717F58AC72DA42568FDC3A82D9F1A8C2C68804C1C2B6918B468D9E6B41BE16A7F677AD6F3D958B6253F36EFD776D83F641F2A6B3E566705EBD675EF34FBD675CF34E3DEFC97531C4DB2E251A10DC9B517FFE13CB484EC6C2DEC8F83404635609E9F6E2
	7AFB8B2002385CF9705C8A342E04B4FBB93950DDG6D5D508EEFC9335740FB9539543D77943C4418A791E86F4AB37F3D7ABC3BF279BC79347D2359E2F816826C819CBE8F09B87F4FE79DBAFE1F0E6710EBA1A4E39C7B945FF66A3896E87783EC8558BD4E7ABF814FC2BA735D2DADFA3F1104C4F2682FCEDBD09939A61E9C1CDEB676227E2CA30F669E7062DC3F3EDD2C0F89C3BB99G97EFA56F4DB743332DFF527B7E186CF70D8901009C504431895734B4ABA89E17A207B4313575BABE5CB3C171EC3F9090FDB0
	46B301FC67EF97D0BE04B4C2BB1B627E62F51CEB8F3C27826CB77073B508BFA725597BGBC92DF5F0553ED3A7C43379FA5B94F852CE5B156B7A731F56D16A26BFB467A4B7CDAEA0B97AC2873AC20655DCC33BBG5AGC6G66G5E20BE70535CF76059B12AF54AFE3F9C688886AF79BC4AB559A37A603D3595A8BA6E3AB8A6AB9EC2181D4F5FEE0CA24FEA4046755E71B82613BD6FF21BA7DDFF08E47E59D5CBFE941DE46EE932EC3B49F411F4B32293B6770618F36F3B3954A7174E7DB1B14FFDF023EC35C519BB
	655986CB164E6710E96E9313E86BF7282D2B61BDD7778F06BFC77193A6BC734FA7284FE0BB8C344CC96E9B4325DC163E3C9D243C39AF2A9E3627F5D8AE7A188C433E65327C1C4E9967E3F35A8CD9F2A8FE3B894FE4390B62311D8434F2FF1A1D79574F72F03FEA505E8A3087A095A08BA08720BC40FD6C2C654C677431FEC151AE05A41FC7D4889B7BB34B6B7094431AA27486D4CD880C09C302A28923BED1A5FA1FC3D48723CB463DCAFDFF8C6678C08C080A20C9F2G388BFED19395157531D8AB300FA2D22ADB
	720BB001BFC8B85F4356AAF87A84D59B8CFA84586EC68FC62F3422CDEE89C340919840BB530B430E7AEA027577G8C9BFA789265F7D5D4A0E03436764972E4A868461510BD8154739F515861037738G57F1F5BE628EC2BBCF66FC522D15A6BEDD52980AAFA8B3FC0FB555E0CCF6485CE77EE5187BCCD749B7C86333D7227A8C42B91979CA1F3CFC6F34D21EA897EB273DBEFC077B588C8EB97AFEDB9AABFE7CFB4DE1DD034E743B21GED2501BCDB81128D9D7F4ADA47FC7612200A5CCBBFD24041C4CFC463CC4E
	17EB8AF4FDDA8D59AD8E446D85FE59B25759BDB54326FEDFF4DD2B6BEC971D83795B7EGF3B05D7CDB9E66A3711698503A44209870400B2A53F2725067747DBAAE9BE3ACB64487E1FFF74206D28DFC018D772020E90AB48A99F5C054B4A9600D504BEDB8179A9A8DC6C2831F2B4606EB1427E1FF9B7D1DB65C9BE3F2A0A0D2A399C3F47AD13AB7C51F688721BB65D0C0E37893F6746FC9F1C60F8D75F6ACF6EE893E904699D69BA48BC53886C5720369B1D41139A06E5B645B8E130E9C00439A279F1E1EA05F0353
	47799EBCB17BA0C97D6C24A5DA8E597E0DAECBFA106D0D7D41489EE4B25566C7177D980D91BADEE5327EF3A36AEDF91542FC7E71C264738A70AF1ECA331F1862BE7FB5DAB7703822BBFB276C73B1D59ABA0EAFA8C71B08D3A19142B186302B065E6EAB28DCC69B52F54868DFADC09D7ACC316F50944D038EDE7B35C15B8D20CDB1BDA6CF69B91576FB5968F7D101588A90D6D83F940575E36B4F7F8CF3FD34BA4E9B12DCA1CD4248A9E860244110C615436A37A3F385BA0F8C0557E95D0B282FE295F4855026F21B
	7EF5892F5336CDA6122C2F9F09D9073AD5A6DBD80D58F4240257E936A21C73B1206DD539ADAA8B4D3922C3D565B10926A9DD0FCF961AED4EF05FABBC8C4FF1C9D135DE5814E12E73FD05A8AF66B19D2D6775CF9DBC2FE7423C55DA1ABDDEC37C6FDA2ACD3277E9AC9FE97034D2FE698E4C67E302A2EAAE7A1F25477A1B42ADA16CD227E1833BE8F4F0F5A275B23EB668F65960F88F1E9A8408EE89F47ED927E9FC5FEA8C4EB78C71FAECD755C5DD36FD0F6CA0ED0BD516E875F1FC591B16E7B4266FD734085E677E
	54EE2DFF691779CC87F535DC87DDA1280BC3DC877F7A3ED987A9A126034C90574126DA54012409FE977E9B50E0811D93C240ABFA289EDC4234262F219768CBF4105ED0496CA4D26F53D70F6BE3B17C585C72BAB5BB944917F937D666CED63FFD1B7A2963962B5F8A8D7C77A97E1C894F565EC771586E87DA4AADDEF3ECAB46F86618CE331F83688370G44816C1E667E69BE7CAED923266B057D2684845F4012604DEB2BA2AB0B37FA3B34484663AD61FA98F3AC57C36A745235E7AEEB33F1D3AB469DDD56A66125
	6318DDCE56BBF94CB0F9E2EB6B95B7F7D796A33E5C42E304A34CE341FC37B996BCD1F656D294A396746BF38743911F1CB04EEC6F17228C49C05B8770A2G19013883F09140B343753F3D6842DA7A27BE6716BB8157837136F3F9CEB474F52A760B69795C0AF12EE56D3819E8FA767A5E082EE7D84B28FCC98E6BE76906F6FE165E89FCD80D7C9E0D4F9950BEE0602A9690D7BC8BFA1A6535D66AA266289EE8FBG62G76983427A96D19D916E732E7D99EE1346B0B31EACE9C9F2BAE6A51E91E98FD62F4BC1BE3B2
	669C05B17025A4D2B3462A6B720939C6FB20B8F60D363BB8FA0D762D6268B5DAF2F18543838F73890D539FABDE3D06CBAD0ED6433DC95774546BE514372A0AAAF5D8CC4ABA6D1F3457C814DA6EC3B844697A28A2BB9C05FA1F4B1A5C59EC68782BADE65FAC3F3D54AEA7EE2F34935BFF5FAD43385F82F4B7008FA08EE077ED3E6F06CA560CFB17432B443D1E5258F67928B43ADD86CB312ED06510B2A6AE577DC8294320C5F6B8269CD4936BA352711A20F84595367351710C96EBFC28B416FD198DBE2DB55B602C
	E183D77BEAF7BD69D1EC4078DD1B435A5BA74CC0758AF9F98E7738E70E57587BE1DCB60083700DF3ACAF333872A75398F34761108F628D8445B1D2B7CF6FDE967858AD50BF91A095A08B004C73FCCE9611CE6BE5DCF81FB00A17FA0C36332852CCB3328127A78C629D45006485233B95A1207AA58DDCE0890F79525AE8FD0C0887758A2DBBF209396E1818E76B5BB24F6B92EBE3B4FA62BC0F050F505C14826DF48357F80D47477D735C0F1FAE0B6D0799E5517D7079326871A12BEC7578F020AC167FB079AE37F3
	796A97A02E535AE44B1CF1BF8B386485EE63669BCBF2B416D92D2D9FA8126712606D239E43F2C7469CBF139FD860F53CE59EF956AD2659DBG7A97F96E8E1F6239BB69219D24EF5D5528F5FC167B2CE5E211796C1645C86EFE91F2B71B33FF014F399768E49D6296964C76C2DF7863DC16A799BDF9899D7554C04C74971650D15E17ACE6FA752219EEB30DE7EB7B41C93E361C9F6CA13EBF7F1F18E7141EF5ECCDBE7D59BFF41C5CF06464B33D7FE25CC12266EC6A65EC7DAD24C05F7B3B01874335B6F2DC66BA3E
	57A6891E354761BC66DE0FFD1E23BEDB8D6FF5GD99457F92DCB17EB7B7A68F1025F23A0A4C11FD400633A8EB25633DA9701719BE9677C6AB7C467E7DD05DF5B0688BF965B3E3C22C6DF8B583844AFB86EC7F7BC0E557E995467FBCC72B37C5FD13C5B04E77B2EE01E6F3B9BC0CB5F404F848FD7207D6B6FCA33F7810CGE8G5BG126F62B1E5213C092C119B5DF2308F020E4F94A6587A86973E58F701A77339FF27FCFB97F9EC632B39316A6E743B185DDF541F875C47CDF97F7DAADCDFF5DC1A3D89408D6083
	D88730BD0E2F6FFB650DECFD17BD125621E9425884BDFD73458DC962F4840D89082D6D82B12FADA90E4D1F2ABFB362D67AA39B575EE89EF7C06F1723BF4B6F0EF28E3C9B3F517DA516CDFA5EB9F66DA4BC8362F41DF1AA6D060DD77F368AB9C89595E97C6D463A26463A66F38D06CE93274D5F57DA8E7FA66ADD954DBF3A8CF37A7309CD47755ADD3B464FC8A9C0BBC6E5A1B4BE303F6C7615B8AE7FF17CBC294B728F455C3E4E073FC63A1EFBB577F88CD98E6AB234EDB4FD6FF8B5C70F4DDFEA6772E8C0577504
	A657F57D7CFC647DA7267752795F678E7A736D6D53FF50B9186F26D4444E770FD7C44F77CFD6C44F775F2FD83D5E7FE1C56CBB5B3F2DB05F59323DF7CFAD6630A4304BDE0003GE4D31A3DFA935FF71E4A37895DF7D1765CE50F976BDE7CB2247179BF280A3D568F2B567E1E74F1955E230D4A1AA67B11BF47FFDA05765264A0C5B25CE8956AC8113C939A6B2A63A72A68379CF11C22D91D63A63CB6EB5A44EB33F786915785ED37013BEF1057C3B78C5CD0A39F1BEC604E96713169866EABC5FC6CB31BF8FD7343
	5F1977F3790E37ACBC66AF77153AF836F79A63A3FBC87F56F784795EG7CDCBC374BFF3E76DF709C92EE6A119E43222CD04BG32DF110322224DB0AC3D85567DFD661AB92E6D84FE49712BFF8FE2B21CF6E28ECF073ED9GF97724591DGCDGBDGA3GC100B8001DGA9GFB81B281328148E670A9007A4DDC7E091A06D564570BCA6ADC0AA021FB715C3F64DC53867C7AB72FAEBF1BAF21B636DFEE2FDD5BAF9F2C25F73E60CFF8254FF08F5562D9C99E9FD7C5437F9F2EADB7F03DEA3F2CCA747BA62D0356D3
	9F74003C414D3C9E6B9844FA8C6FE4D9DC2C456BB23DEEFAE81057CD09D156487D0857CD29263A8971A91BA375851BFF117AF1AA6077ED66F1386DD8E43EEF957179F2E24CE7DDE53E72FBA373B11F79A9654D74F2614849D87606C8F2857263C723B761G6C963C46186CA32566D8475CD8273DFC84631694G758ABEE916DE4F8FC04700176D452DB487BAF519986EFE0ABB2763981F734E58FEF1493936DFDCF5C60FD73FF52E0CD7C34E6871EA44E90ED78CA7B8B1D6FBE19F8C9B7D468D5CA786EE1272C45C
	4712C71BB070D3D4AE447708B8A147CFD33926A9EED8474D19F01F683875CE74EDD872042C70319B1D746CA079FC2C3D19761182606A06BE6E23B2CAC141A477B6837709E10BEF362F344FA36DE67BB07F7C9BCCBFFABDF5F4909BD16AA98F78006CB595D32C6EF8EE10579D7D77F2FF7FF7F4D71D778327A23CCF955D996F0E924EBB5E601DFA1A90FE4FAA1CAA0E78A7B58D43BB43A591BCB70D9E5BB2874D31AD9578E65C7BF93E55F717AC29D5E1CC4E9A63183EFFB13852862F8C1AED40F01F162C6CB7DAE2
	6E47FE53761D5FFE879CA469A5E2D395D232C40F5FFD70577F386F6F07D4AB317712DABCFE8EC07D0BBBD774602D237EB30EEED29814A15C8FG3E17642974E1D335991F58C72A472F6327CDD593BD9DBCA6289AF90DFE3ABA031F954F74886A04067B73FC9F498B608FCEC8A5A37B0400778C8B90674378B3135C0E9BE8AD2A8E8272D6D1F9110B4B60D275F85949372E3C781815D83A49E11FACF83A05B188DB3D24D01BD0C4F5C276F94241760B14D141B4EA84CFC0D6E0770631FCDC2C8B6F2D8C21A7438FEC
	A9B9D052EDA1A42C10B334A7E6G17514353B9A1A8B0152874EA78839AD9B9E150569F55CEBD7DC14D0805646A0B1CA28B44928EEA44BEAE78A55F0CC68A0D93060AC4BBE0B43C789A1795BF88D737CCB8CEB266F0FD6529267B3F799F7FCB67088C5548B126CD98FA46AB4A7EB39D6363D2C0B49800EA017D1B86FB6C63DA5627B21C705A15E7FFB58A2E50C52C7E26A67A3F197EBF0B7F1BA92619E21A4F02FE33492AFF4B69E13EE66ABFA0CF991587BD40558DC6541EFF6929DDBFEE4BBE882B6BA6B67A5D8B
	6CD09E6975E148BFAAAA9B136EFF33E8DB563DE826A0FACB98799E8E9B9CF1053DB47AE55DFA37557A132FDBADA42F1758D9B6G7F45108E7ECBA3FEAF31C2B6202EE7B3839536E6B9D6A3C5744E338EAFBDDBB5D245EEAF6BCC57172DF4A4D9614A084DDB691308AEG97E1718CDBC534EB897DDFFE47CA46D115F4EC35211B2361A52B61FF367FFF121E0CA1A961E75F06C5F35D7E4A5D0E186770788496EBF7A6AC2F5BCF52D83A9F700792F85D9BF2630FFAE355C183FE416753845FE4C74003B5512599CD0C
	1C6D5E99C47EAA6F2393992DF990653EE99A73FFD0CB8788F223300E9A91GG44ACGGD0CB818294G94G88G88G56F854ACF223300E9A91GG44ACGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGD491GGGG
**end of data**/
}
}
