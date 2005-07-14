package com.cannontech.dbeditor.wizard.device;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.IDLCBase;
import com.cannontech.database.data.device.PagingTapTerminal;
import com.cannontech.database.data.device.RemoteBase;
import com.cannontech.database.data.device.TransdataMarkV;
import com.cannontech.database.data.device.TwoWayDevice;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.port.DirectPort;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.device.DeviceVerification;
 
public class DeviceCommChannelPanel extends com.cannontech.common.gui.util.DataInputPanel implements ActionListener, MouseListener, ListSelectionListener {
	private javax.swing.JComboBox ivjPortComboBox = null;
	private javax.swing.JLabel ivjPortLabel = null;
	
	// address attribute for checking against duplicates for ccu's and rtu's
	private int address = 0;
	public static final String TABLE_NAME = "DeviceIDLCRemote";
	private int deviceType = 0;
	
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DeviceCommChannelPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getPortComboBox()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (PortComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceVirtualPortPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
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
 * Comment
 */
public void deviceVirtualPortPanel_MouseClicked(java.awt.event.MouseEvent mouseEvent) {
	fireInputUpdate();
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getPortComboBox() {
	if (ivjPortComboBox == null) {
		try {
			ivjPortComboBox = new javax.swing.JComboBox();
			ivjPortComboBox.setName("PortComboBox");
			ivjPortComboBox.setPreferredSize(new java.awt.Dimension(190, 27));
			ivjPortComboBox.setMinimumSize(new java.awt.Dimension(170, 27));
			// user code begin {1}
         
         com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
         synchronized(cache)
         {
            java.util.List ports = cache.getAllPorts();
            for( int i = 0 ; i < ports.size(); i++ )
               getPortComboBox().addItem( ports.get(i) );
         }
         
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPortComboBox;
}
/**
 * Return the PortSelectionLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPortLabel() {
	if (ivjPortLabel == null) {
		try {
			ivjPortLabel = new javax.swing.JLabel();
			ivjPortLabel.setName("PortLabel");
			ivjPortLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPortLabel.setText("Select a Communication Channel:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPortLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension(350, 200);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val)
{
	Integer portID = new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject) getPortComboBox().getSelectedItem()).getYukonID());
	int devType = com.cannontech.database.data.pao.PAOGroups.getDeviceType( ((DeviceBase) val).getPAOType() );
	
	if( val instanceof PagingTapTerminal )
	{
		 ((PagingTapTerminal) val).getDeviceDirectCommSettings().setPortID(portID);
	}
	else if( val instanceof RemoteBase )
	{
		((RemoteBase) val).getDeviceDirectCommSettings().setPortID(portID);

		//We need to set the Devices baud rate to be the same as the selected routes baud rate
		// This requires a database hit here, could lead to problems in the future!
		try
		{
			DirectPort port = (DirectPort)com.cannontech.database.data.lite.LiteFactory.createDBPersistent( (com.cannontech.database.data.lite.LiteBase)getPortComboBox().getSelectedItem() );			
			com.cannontech.database.Transaction t = com.cannontech.database.Transaction.createTransaction(
						com.cannontech.database.Transaction.RETRIEVE, port );

			port = (DirectPort)t.execute();

			((RemoteBase) val).getDeviceDialupSettings().setBaudRate( port.getPortSettings().getBaudRate() );
		}
		catch( Exception e )
		{
			//no big deal if we fail, the baud rates for the device and port will not be equal
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e ); 
		}

		if (val instanceof IDLCBase)
			 ((IDLCBase) val).getDeviceIDLCRemote().setPostCommWait(new Integer(0));
	}
	else
		throw new Error("What kind of device is this?");

   //default the threshold
	((TwoWayDevice) val).getDeviceTwoWayFlags().setPerformThreshold(new Integer(90));

	//transmitter is a special case

	if (com.cannontech.database.data.pao.DeviceClasses.getClass(((DeviceBase) val).getPAOClass()) 
		     == com.cannontech.database.data.pao.DeviceClasses.TRANSMITTER)
	{
		com.cannontech.database.data.multi.SmartMultiDBPersistent newVal = new com.cannontech.database.data.multi.SmartMultiDBPersistent();
		((DeviceBase) val).setDeviceID( com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID() );
		
		//checks device type and accordingly sets route type
		
		String routeType;

		if( DeviceTypesFuncs.isCCU(devType) || DeviceTypesFuncs.isRepeater(devType) )
			routeType = com.cannontech.database.data.pao.RouteTypes.STRING_CCU;
		else if( DeviceTypesFuncs.isLCU(devType) )
			routeType = com.cannontech.database.data.pao.RouteTypes.STRING_LCU;
		else if ( DeviceTypesFuncs.isTCU(devType) )
			routeType = com.cannontech.database.data.pao.RouteTypes.STRING_TCU;
		else if (devType == PAOGroups.TAPTERMINAL)
			routeType = com.cannontech.database.data.pao.RouteTypes.STRING_TAP_PAGING;
		else if (devType == PAOGroups.WCTP_TERMINAL)
			routeType = com.cannontech.database.data.pao.RouteTypes.STRING_WCTP_TERMINAL_ROUTE;
		else if (devType == PAOGroups.SERIES_5_LMI)
		{
			Integer devID = ((DeviceBase)val).getDevice().getDeviceID();
			((com.cannontech.database.data.device.Series5Base)val).setVerification(
				new DeviceVerification(devID, devID, "N", "N"));
			routeType = com.cannontech.database.data.pao.RouteTypes.STRING_SERIES_5_LMI_ROUTE;
		}
		else if (devType == PAOGroups.RTC)
			routeType = com.cannontech.database.data.pao.RouteTypes.STRING_RTC_ROUTE;
		else
			return val;
		
		//A route is automatically added to each transmitter
		//create new route to be added
		com.cannontech.database.data.route.RouteBase route = com.cannontech.database.data.route.RouteFactory.createRoute(routeType);
		Integer routeID = com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID();
		
		//make sure the name will fit in the DB!!
		route.setRouteName(
			( ((DeviceBase) val).getPAOName().length() <= TextFieldDocument.MAX_ROUTE_NAME_LENGTH 
			  ? ((DeviceBase) val).getPAOName()
			  : ((DeviceBase) val).getPAOName().substring(0, TextFieldDocument.MAX_ROUTE_NAME_LENGTH)) );
		
		//set default values for route tables		
		route.setDeviceID( ((DeviceBase) val).getDevice().getDeviceID() );
		route.setDefaultRoute( com.cannontech.common.util.CtiUtilities.getTrueCharacter().toString() );
		
		if( routeType.equalsIgnoreCase(com.cannontech.database.data.pao.RouteTypes.STRING_CCU) )
		{
			((com.cannontech.database.data.route.CCURoute) route).setCarrierRoute(
				new com.cannontech.database.db.route.CarrierRoute(routeID) );
		}		

		Integer pointID = new Integer( com.cannontech.database.db.point.Point.getNextPointID() );

		//A status point is automatically added to each transmitter
		com.cannontech.database.data.point.PointBase newPoint = PointFactory.createNewPoint(
				pointID,
				com.cannontech.database.data.point.PointTypes.STATUS_POINT,
				"COMM STATUS",
				((DeviceBase) val).getDevice().getDeviceID(),
				new Integer(PointTypes.PT_OFFSET_TRANS_STATUS) );
		
		newPoint.getPoint().setStateGroupID( 
				new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_TWO_STATE_STATUS) );

		((com.cannontech.database.data.point.StatusPoint) newPoint).setPointStatus(
			new com.cannontech.database.db.point.PointStatus(pointID));


		newVal.addDBPersistent( (DBPersistent)val );
		newVal.addDBPersistent(newPoint);
		newVal.addDBPersistent(route);
		newVal.setOwnerDBPersistent( (DBPersistent)val );
		
		//newVal is a vector that contains: Transmitter device, a route & a status point
		//and returned if device is a transmitter
		
		return newVal;
	}
	else if( DeviceTypesFuncs.isMeter(devType) 
				 && devType != DeviceTypes.DR_87 )
	{
		((DeviceBase) val).setDeviceID( 
				com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID() );

		SmartMultiDBPersistent smartDB = null;
      
      //only add a COMM STATUS for an ION meter
      if( DeviceTypesFuncs.isIon(devType) )
      {
         smartDB = new SmartMultiDBPersistent();
         Integer pointID = 
               new Integer( com.cannontech.database.db.point.Point.getNextPointID() );
         
         //A status point is automatically added to each transmitter
         com.cannontech.database.data.point.PointBase newPoint = PointFactory.createNewPoint(
               pointID,
               com.cannontech.database.data.point.PointTypes.STATUS_POINT,
               "COMM STATUS",
               ((DeviceBase) val).getDevice().getDeviceID(),
               new Integer(PointTypes.PT_OFFSET_TRANS_STATUS) );
         
         newPoint.getPoint().setStateGroupID( 
               new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_TWO_STATE_STATUS) );
   
         ((com.cannontech.database.data.point.StatusPoint) newPoint).setPointStatus(
            new com.cannontech.database.db.point.PointStatus(pointID));
         
         smartDB.addDBPersistent( newPoint );
      }
      else
         smartDB = createPoints( (DeviceBase)val );


		smartDB.addDBPersistent( (DeviceBase)val );
		smartDB.setOwnerDBPersistent( (DeviceBase)val );
		
		return smartDB;
	}
	else
		return val;
}

private SmartMultiDBPersistent createPoints( DeviceBase val )
{
	if( val == null )
		return null;

	SmartMultiDBPersistent smartDB = new SmartMultiDBPersistent();
	Integer paoID = val.getPAObjectID();

	if( val instanceof TransdataMarkV )
	{
		//very special case since Transdata devices have unique points compared to
		//other IEDs
		smartDB = TransdataMarkV.createPoints( paoID );
	}
	else
	{
		//majority of the cases are the same
		int[] ids = com.cannontech.database.db.point.Point.getNextPointIDs(4);
		
		//add all ther point to the smart object
		smartDB.addDBPersistent( 
			PointFactory.createAnalogPoint(
				"Total kWh",
				paoID,
				new Integer(ids[0]),
				PointTypes.PT_OFFSET_TOTAL_KWH,
				com.cannontech.database.data.point.PointUnits.UOMID_KWH) );
	
		smartDB.addDBPersistent( 
			PointFactory.createAnalogPoint(
				"Total kVArh",
				paoID,
				new Integer(ids[1]),
				PointTypes.PT_OFFSET_TOTAL_KVARH,
				com.cannontech.database.data.point.PointUnits.UOMID_KVARH) );
					
		smartDB.addDBPersistent( 
			PointFactory.createAnalogPoint(
				"LP kW Demand",
				paoID,
				new Integer(ids[2]),
				PointTypes.PT_OFFSET_LP_KW_DEMAND,
				com.cannontech.database.data.point.PointUnits.UOMID_KW) );
	
		smartDB.addDBPersistent( 
			PointFactory.createAnalogPoint(
				"LP kVAr Demand",
				paoID,
				new Integer(ids[3]),
				PointTypes.PT_OFFSET_KVAR_DEMAND,
				com.cannontech.database.data.point.PointUnits.UOMID_KVAR) );
	}				
	
	return smartDB;	
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
	getPortComboBox().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DeviceVirtualPortPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 200);

		java.awt.GridBagConstraints constraintsPortLabel = new java.awt.GridBagConstraints();
		constraintsPortLabel.gridx = 0; constraintsPortLabel.gridy = 0;
		constraintsPortLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPortLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPortLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getPortLabel(), constraintsPortLabel);

		java.awt.GridBagConstraints constraintsPortComboBox = new java.awt.GridBagConstraints();
		constraintsPortComboBox.gridx = 0; constraintsPortComboBox.gridy = 1;
		constraintsPortComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPortComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPortComboBox.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getPortComboBox(), constraintsPortComboBox);
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
public boolean isDialupPort() 
{
	if ( getPortComboBox().getSelectedItem() == null )
		return false;
	else
		return com.cannontech.database.data.pao.PAOGroups.isDialupPort( ((com.cannontech.database.data.lite.LiteYukonPAObject)getPortComboBox().getSelectedItem()).getType() );
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DeviceCommChannelPanel aDeviceVirtualPortPanel;
		aDeviceVirtualPortPanel = new DeviceCommChannelPanel();
		frame.getContentPane().add("Center", aDeviceVirtualPortPanel);
		frame.setSize(aDeviceVirtualPortPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * This method was created in VisualAge.
 * @param newEvent java.awt.event.MouseEvent
 */
public void mouseClicked(MouseEvent newEvent) {
}
/**
 * This method was created in VisualAge.
 * @param newEvent java.awt.event.MouseEvent
 */
public void mouseEntered(MouseEvent newEvent) {
}
/**
 * This method was created in VisualAge.
 * @param newEvent java.awt.event.MouseEvent
 */
public void mouseExited(MouseEvent newEvent) {
}
/**
 * This method was created in VisualAge.
 * @param newEvent java.awt.event.MouseEvent
 */
public void mousePressed(MouseEvent newEvent) {
	
	
}
/**
 * This method was created in VisualAge.
 * @param newEvent java.awt.event.MouseEvent
 */
public void mouseReleased(MouseEvent newEvent) {
	fireInputUpdate();
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
}

/**
 * This method was created in VisualAge.
 * @param newEvent javax.swing.event.ListSelectionEvent
 */
public void valueChanged(ListSelectionEvent newEvent) {
	
	if( newEvent.getValueIsAdjusting() )
		return;
	
	fireInputUpdate();
}

/**
 * Method to set our address variable
 * 
 */
public void setAddress( int addressvar )
{
	address = addressvar;
}

/**
 * Method to set our device type integer
 */
public void setDeviceType( int deviceTypevar )
{
	deviceType = deviceTypevar;
}

/**
 * Method checks against physical address duplicates for ccu's and rtu's
 *
 */
public boolean isInputValid()
{
	// hit the database to check for dublicate address on non-dial up com channels
	com.cannontech.database.data.lite.LiteYukonPAObject port = ((com.cannontech.database.data.lite.LiteYukonPAObject)getPortComboBox().getSelectedItem());
	int portID = port.getLiteID();
	if((! PAOGroups.isDialupPort(port.getType())) && (com.cannontech.database.data.device.DeviceTypesFuncs.isCCU(deviceType) || com.cannontech.database.data.device.DeviceTypesFuncs.isRTU(deviceType) ))
	{
	
	java.sql.Connection conn = com.cannontech.database.PoolManager.getInstance().getConnection(
												com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		java.util.Vector devices = new java.util.Vector(5);

		String sql = 
				"select y.paoname " +
				"from " + com.cannontech.database.db.pao.YukonPAObject.TABLE_NAME + " y, " + 
				TABLE_NAME + " d, " + "DeviceDirectCommSettings p " +
				"where y.paobjectid= d.deviceid " +
				"and d.address= " + address + " and y.paobjectid= p.deviceid and p.portid= " + portID;
	try
	{
		stmt = conn.createStatement();
		rset = stmt.executeQuery( sql.toString() );
		
		while( rset.next() )
		{
			devices.add( rset.getString(1) );
		}
	}
	catch( java.sql.SQLException e )
	{
		CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try
		{
			if( stmt != null ) stmt.close();
			if( conn != null ) conn.close();
		}
		catch( java.sql.SQLException e2 )
		{
			CTILogger.error( e2.getMessage(), e2 );//something is up
		}
	}
	
	if( devices.size() > 0 )
	{
		setErrorString("Physical address already used by another device");
		return false;
	}
	
	return true;
	}
	return true;
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G89F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8BD0DD6519E888E6B43B2B5354E55CF4A7DACDE9E5EC3469ECA655ADE533ED5226354E6058076D30DB5DB2536824AE339315EEFFAE0FE064F1434BEB00C28CC6A0F001C8B492ECC3A091922AB4A045D5EB8E5C83774039675C1CF3AEDCA2C9767B7E4739074B3DC45D6E1D79B8677F7E47777D5F7B7F8F2432FEC3C12E2B12105C8242FF2F679252748AA1FF73540E4785AEC1CEAFA40E5FC1007D24
	65C2BEBCEF063E9F5E14DE68A64DFFAA02761D502EDC13DE78823CD7100C915FCB709207CE8F740D8D7EBAE9E5BA17CB841D3D347D4D19E2F8168224G8E1F59B252FFE4464371BBB9DEC02E0B10C727F04CD3B3359CF79C5A43G49G29D3EC7CB6F8BEC7A92F6EBE4E47FDF04B86127D5DD7DCA5380EE3CD810D53EC6EGFFB6102F666E1EC6DAB53345FCCE823437G6066734833DDEF4033E7F8F67A7713FA40BBA9E91A2ED972245FBB2FDC128C1F57A74FA913F2F7F7BFFD0EA906951254E15D3006A5CDD6
	E12E6A4775A7E70B68FA04F4C1FB8F453D399FE9EE02776DG19B6BE3F9C71BDCAFA61A8002A08FD3E79CA8F5F477847DFA5B90FE8F9E5F1761928307D25AA11FD2E8BBD3377C52A1387CAD176D950D7BC13DE58G508F6083C884282136302D24851E3D93D61F9E8868DAEFB0F8CA7599FF54FD328A6F5D5D5043F16765C95D70914274FDF036B586BFAB811B577E2967B119243CAAF41FFAFEA3F974EB67DCFBE3486451148E577A99A60B4DB39119B05A7E3834B3E6165A66D25AFFA6CE5A3B12743C029834B7
	BF5C6C4A626B64B8E8374E222E6F25B6D08B6F395CBE987E9B949FF460191D76D1BC3693206F31D9E19B63250217217C5B083BF3A8269C1253FBDD83AA6361029A4D4B56AADCFB2D9A4D4BB60ADF2FC673322FDC70128AFD6EC0FAA1332F075C68379634EF86C881C88348844881F0EB4246CACA5BBF210D8DCB06F5AA2428BE59A0EC6E37CB2B61A907ADC39A54CCCB524077A4C311A6D459A4FC4C0325A80309A573B2286DCF820D37E4CDB6A4CB51B5D8DD8A4816EC18EC4C4FCA776398C3263D23CAC0868201
	A0916BBEDEDA01FEAD19563BC11FE44911912C7F09D254491CB486AB4282704E64725FD4F79D307F8100F1DB8EE6992EF7CEB6A0E0F4F78F697AECA8B80ABBA1A99A4A79AA0D9DAA3CA7EAC2464F14A3EE973473F5314ED74A4A9D6B74AB13483EE4AC889F7B277D981BBD3A3019A747054D74175CC5DA9FBE9B53E690FAF4E6AB23FA346FFC0B2E09FC3176607E71B83E6451E37B52270F9593F1E9647D9F57AF35F5652FC4DDAD824F49GDBEC99BFDE5640EC7614E44A42CA5FB14040E4DFC4620C4F342AFCAE
	CF567EAEED7FD09732F2170F471177DF83980DE6CA632FA117D840E472D3B766A1F9CE562CFEB9A8EBBEF8B1F95F3F3B5156381FCE69761CAC0A8F02DF7701A319B67EC9B77A1EE4D906B291326491593294EDBA527FDF1416991A88C6C20220759BB746DFFD9E7C5A9E6F5A0FBEB129EB1ACC15E3CF617DAC574A2A9CG267B7410E6B17C3EF254552C3C40E3C2E9B996BBF3129A427842EA03CD52733887C389004AB1C4118F02682F374EFA9CB25A837A469A27837A7A034277668F895FEB3CF4BBC97B68E5D7
	2C5C11FCD73F6B4EA0731BAD41086FB11EFE9B0777020A22885EE43C5E5E0871B2DE1142EC7EF69A67FF84F4F68164DF94B6FFDD85760B38420D3DCFD7D5A6D2DB56DA259BF5A3DF8C49900EB1001D3365972AAC0F6A9B6332E47D17ABD1162AA376ED3E0832FC36CA540035506E82983D4864396EA213875B5F5E0FB007472A5326C30AB7E4A99891A58B0CB098E23BE6F599D10B789A449075D72B0EF2B88E6DE1G3FA1F476D009283F564DEEA019DFBC90374ECCB098EFF79A911DB52C9275572F2A1166E350
	17ED88996F29F46600DE5354A7951AFE38FC3ED46554A94325D66D036714E21856A0B8DDD848723EAA64977393C773FC5DD8DD4C77DAE426971E8458ECA27E97DD158E5E43A64BB39B6019C7573B297AA87AA4D53617BD06E5E3CAB782322FE9C61A133C523C65257E6F6D255D27713DD968E6DBF3B963D32652E7C93329F9CC54D1F7D41C6434F75EF39B6939DA610AD5572EA92BF36518CC1E0DD6C42E0B7795666D7A4B33FBD93CABD023EB39209549BB0956729C44EA1C8DD46749962BF18AEC7C5D941F6A40
	B3D99E5505AC53202FB1A47272A58F7A7E9AE85F89F0BFC086C0B6C0651C50F5F7752B649AF54FA0582E21C96A4812C047753B3BBA0F45A65E4E2EAE90310908385FD29DAD070E3925FB9F0AEA33F947174D73C64BB3EA9E537134DAA37C8FADA26CB567A1EEF7F71F05BDCE682774B0BF879DB8D644E92D1993FEF5790C532F6EAFBB6AFABE0EDF25F15A19F30E5A55BE57B49E40D851B01FDE5883B08A9084D8873089A0E3DE487F4A8BEFDECB7E5400C775BE408DC0CC6A0B4E9FB01F55E635B4FEE4435AC5E1
	28F5A96E69G62DA215D9F9639FE230EB8BF3493EC5C3D94179CE6F10D4BDF0FD75BB88157793439BAC277CF1F0846E729995096180CD7F9625784B7FAE25784B7FBE2578437FA76B3BC2C61BC89087EFF742CDCB3EC74442A996A689EF7D4175135CDD3B62952B1C9703EDFDAD5A4C66D70B69C9638FCAA08D47D9C9FF35A527BBAEDBF2ABFA67CE8CB8CDD6D84DCE6941E59705DB5980B33214F3D10DE5884F092604202305D1E972F99BBCE07D7089D5E837175F26CC0EC3D1CB800F94E54C346249CAD7BD387
	BCF6DF44CB86ADB900E2E2E34E517916E4CC4B4BF4F71E4EE7FD71663FFFA01EFE198EDED531B6D32585BDE44119E881FD702CAAEA3084E8A7832481E4AC305C46E2563D7398E7266070874B0D8465C9327D924EF5EBE2EE973487G7CGEBGD2AE091C5823222C30049A12A668BD8F437FC5A966F89178F80E551DB9567DB163236BE391B3DE23F1F1G5ABE9B67EF94F1A4EC63B6FE08386D504E7CD8584457EA626BF4EBCDEC1D2655447635EF572C6CEBBF2809278B16978DD55C23E583FF2D0BC2561D97B0
	B65B7981AB066E6E378C45F7CA1A9E221AE379E1AB0D27B85F37A86A31AC3A66FAE8EF865839A872C67830489B299BEFA3C38967E256E31923C7DD0FADB2DFAB3A9C499BA910B7984D57B4C1339F7A97AFA34E27B975053A3DA51765C256FF6B12FE1449AB44593FFDC9BF727B004B597F4C12FE1451C1BE1F6D6DFBAF0B3D65FCAF0528DFFFA3EE2D390D6FA90BBF07478E11469B1F5D4B647E1FD39E92B3AFD33365B9B9A8F2F24395E15FFFBC06740206BCA59BD06D0FA817B0665DFC8547CFD1DB9D07F79FC0
	6DD5FAC751584F79493832BC5E09FBA381D9B0A68760905F3B7BAA2B2F44388A724007681B6018CAA0942054996D07297D7BE0FC702A48E761E5B55F35004DCB7A0C733E792967717B35G35E347DE997EDFA8BE4501E73E74246DCB1B214F13A0EA4C75F52853C5E8A783EC82588E108510FF1D08893E5A8EF20DBCB12A8707A0E8288EB7E77BBB21FD363B58EF6D95B63BF96B9DE4CB525339716AB84FF54CC60E73E75668A1C78EEC2F477D7921AF91A085A08DA083E0173D3F545AD6363F53BE456A35ACE952
	CFCFC6E2F3E30ABC9FC1E3F067F9B9685C9B391E512F644F266B175BEE4F75781D637CE1E25B66070D7570546479867B70532FG993C6CEB6AFC23233561686B4DDCA69B661D5FA60E6FFBBD66B91F66A9CEFB9DFF5EFF43A1DEB3EFEA94E7541D50D7CFF900D8C6A23FD8B8467761A6E26B675F0A05FEEA6F7E87527F485339076270123F0A5F772D0A444B2C191C3DEC4DAFB4C7E45130FFA5D93C4EE5416F4382B80F3D6FF03CE72B1F249E7D2C40E814847EBFE9B01B3C2BAEFE9ECE2D0B1D076F2B0B1D0737
	542DDC532655453F877BCE1D739E0C79D42085734DDA50E78A40CE00CC006CD5421F5E7D9DFE7A439B77653EF45AB70D359D5E32DB027E717A78FB7DC37D356F66876A716EE2C237ACBD006B8B7C5B7528AFCB8FD2A4430D54230C8CE55AEF31219C7FDEBD3D971727A81A57D1B4173993538B9B92C54D347584623A205DEF636AFBC4EDB51CA86A09875FF77A2A7B3603AE910F23753DA111FFDFCB0C7887137303C7F05DCC4067A48A597E7D61A3709C13E6F8944610259BD43A009CB67420ECD88B8CCBEF4538
	4DFE3D51992BEAD72797F62CDE795C47F8F83E8DF3F1BF0C3DG1088309EE09340F600C781F68364A7C16E86688298G78GA08810G308EA0A5C970BF5B543C827F3C082386E2C88A1A8857CF28A53E0DAC36DC5BC6D63750BBAF506D68C2101F85EEEC41DADF1F1AB2E55B967F2E45ED63864DE15DD4708C416B18A05E63CE691A456A07C9EA9F77435E36A709DA672193B84682E6D8FC7B25972D0155B52F75083AE6CFD26C3A46D95B64C455B545B7C4F2BA534DBA7C644579BEF86465F8F2852FBB8B71741D
	0999B8900DCA53761C0CA3254E38404CC55C439FC19F071249D2A4D539C42F8FC7E020B64DECFEA74DA39E4E9343BDC4F19D9C475629EF0B2F3743ED5756DBFBDBEC5FBE56365C373B5BE27B7649B627EFB35C69B61469B4585B38BD6E2C0DFB4F463DC357C45C1F951F6537716FD23E90BFA0A3C101FF1F72B5CFF1639CF74101FB0F6364B634BD5832DFB7445C19B6EADF0A2A72BBF0BAC6514094EDF91814C7A5A8B9788E5BB8641B595D03DD04083CFA4F890C47CB726A99B0622617FC20DBFD7A255686C82D
	1DDD4D627F8B26FBC40DB0843AE46BBDF50C90F1BFA79DAE0E5886958B435706CBA2F8A1969EFF549EE77CC904F513EFD8B97EB01BDA55335C4E5674B86D0C7D3F49EFBE3C87B61B7A84A9B08DD23E3CFE687D5B9FBF127AD1FF250B3886C9211FB2B7C88A66117B30789C7B4FB0374C1EBBC8851FB977EAF75EAF3E1867A279B0119962A049C7EB027986DBA78F8C112EE27F178B3E1B974995A2DC74AB96299F1171B3C703540005F4C8D3A629DBB57479A58D2F6A01FA49928D7D6B6D477EE3674F474CBCD2B8
	C8DE4440B3A29B549FE59F5EAD710F42E748F3C19D02A59EED00B513BE8ACCEBC11507C86554F97CD0E2DA322FD7F83AE9112AA124540E9FB35A87A453EF21571D98A2799AFE3EA6652CDB153469F66676A7687EF2FBAF509D510297782D207C62AADEFB150AEF143DFC706CE3DB72086B8C59276A126F0CB48941E810BCE7798D59746B2AAF9C7C43C93AD051BC6A92EB7BBCD82E465EBEEE56BB8A3141E6FAB67C0535A5D9A5E760689BB6C8BB9D09F156EB0F7001A08DA0A59B0396FE0E570DC63B6F3A5FD907
	3F7F568B9F38C8AE5FA4AA27A89C34C8611494D05485F8E17EC055D6EC813706241978E982B8EB0862CCF459843C1F5B517179AFBF73BF14C0E4AAE81D09922636CF4BFA203DF7EACA51E4FB8110ABACDFE7AF0FE33CD1E34A4337D41DFD783793E0877DA4AF5051C17FF6523FC771EFA745F4D2CC67D1906EEE3262AF3ABFAC76CC0D477682D43F778CE8507A4993BB6E78E94F6EDD303BB32400FE9EGA538A3235E8E85A6E4A3A97573F54F2F4B3C89F594C4D389633AEE363CAB5ABF44D922792A33D65A335A
	9377DCD339161F2D56C657CA87A5FA1687FC42DAD1EBDC3C50C66267441100242A16244E76EABE4C0F2796ACB9D2939F16F0FDD30C611D2C2FCDC23EE79CF37E97D0CB87880ECEADEC378FGGD0A7GGD0CB818294G94G88G88G89F954AC0ECEADEC378FGGD0A7GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG718FGGGG
**end of data**/
}
}
