package com.cannontech.dbconverter.converter;

import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.NativeIntVector;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.device.CCU711;
import com.cannontech.database.db.device.DeviceIDLCRemote;
import com.cannontech.database.data.device.Repeater900;
import com.cannontech.database.data.route.CCURoute;
import com.cannontech.database.data.device.lm.TimeRefreshGear;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.PortTypes;
import com.cannontech.database.data.pao.RouteTypes;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.port.DirectPort;
import com.cannontech.database.data.port.LocalDialupPort;
import com.cannontech.database.data.port.LocalDirectPort;
import com.cannontech.database.data.port.LocalSharedPort;
import com.cannontech.database.data.port.PortFactory;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.data.route.RouteFactory;
import com.cannontech.database.db.pao.PAOExclusion;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.database.db.point.PointLimit;
import com.cannontech.database.db.port.CommPort;
import com.cannontech.dbtools.updater.MessageFrameAdaptor;
import com.cannontech.tools.gui.*;

/**
 * A skeleton app that is created from the following files:
 * GREDBConverter.java
 * DBConverter.java
 *
 * 
 * @author: ryan
 */
public class CentMaineDBConverter extends MessageFrameAdaptor 
{
	/* ______________________________________________________________________________
	 * 
	 * HashMaps used for storing database objects that are needed
	 * for a relationships to other tables (for example, the items
	 * ID in the Hashmap may be needed to form a foreign key relationship
	 * with another table.
	 * ______________________________________________________________________________*/

	private HashMap deviceIDsMap = new HashMap(32);
	private HashMap routeIDsMap = new HashMap(32);
	private HashMap macroRouteIDsMap = new HashMap(32);

	/* ______________________________________________________________________________
	 * 
	 * IDs that need to be unique and are incremented everytime a new
	 * item is added.
	 * ______________________________________________________________________________*/
	private int ROUTE_OFFSET = 500;
	private int START_ROUTE_ID = ROUTE_OFFSET + 1;

	private int PORTID_OFFSET = 2000;

	private int DEMAND_POINT_ID = 16000;
	private int PULSE_POINT_ID = 18000;
	
	private int CCUID_OFFSET = 4000;
	private int RPTID_OFFSET = 5000;
	private int ROUTE_ID = 10000;
	private int MACRO_ROUTE_ID = 12000;
	private int MCT_ID = 14000;
	private int MACRO_ROUTE_ID_OFFSET = 6000;
	private static final int MAX_DSM2_MACRO_COUNT = 30;

	/* ______________________________________________________________________________
	 * 
	 * File names that are read in by this application
	 * ______________________________________________________________________________*/
	 
	private String commListFileName = "comm list.txt";
	private String transmitterFileName = "ccu list by name.txt";
	private String repeaterFileName = "repeaters by name.txt";
	private String routeFileName = "routelist without rptr.txt";
	private String routeMacroFileName = "zonlst.txt";	
	private String mctFileName = "device by id.txt";
	
	private StringTokenizer ts;
	private StringTokenizer ts2;


/**
 * CentMaineDBConverter constructor comment.
 */
public CentMaineDBConverter() 
{
	super();
}

public String getName()
{
	return "Central Maine DB Converter";
}

public String getParamText()
{
	return "Src-Directory:";
}

/**
 * Executes all db inserts that are needed. If a failure occurs,
 * then execution stops.
 */
public void run()
{

	boolean s = processPortFile();

	if( s ) s = processTransmitterFile();

	if( s ) s = processRepeaterFile();

	if( s ) s = processRouteFile();

	if( s ) s = processRouteMacrosFile();
	
	if( s ) s = processMCTFile();
	

	getIMessageFrame().addOutput("");
	getIMessageFrame().addOutput("------------------------------------------------");

	if( s )
	{
		getIMessageFrame().finish( "SUCCESS: Database Conversion completed");
	}
	else
	{
		getIMessageFrame().finish( "FAILURE: Database Conversion completed");
	}

}

public String getDefaultValue()
{
	return CtiUtilities.USER_DIR;
}

/**
 * appends a file name with the path
 * 
 * @return java.lang.String
 * @param aFileName java.lang.String
 */
public String getFullFileName(String aFileName) 
{
	//try to get this value from our System properties
	String propPath = System.getProperty(IRunnableDBTool.PROP_VALUE);
	
	return propPath + aFileName;
}


/**
 * Insert the method's description here.
 * Creation date: (3/31/2001 12:57:46 PM)
 */
private void handleLocalDialupPort( com.cannontech.database.data.port.LocalDirectPort port )
{
	port.getPortLocalSerial().setPhysicalPort( "Com1" );
	port.getPortSettings().setLineSettings( "8N1" );

	//----- Start of defaults settings for the different instances of ports
	if( port instanceof LocalDialupPort )
	{
		((LocalDialupPort)port).getPortDialupModem().setModemType("U.S. Robotics");
		((LocalDialupPort)port).getPortDialupModem().setInitializationString(
			com.cannontech.common.util.CtiUtilities.STRING_NONE);
		
		((LocalDialupPort)port).getPortDialupModem().setPrefixNumber("9");
		((LocalDialupPort)port).getPortDialupModem().setSuffixNumber("9");
		((LocalDialupPort)port).getPortDialupModem().setPortID( port.getCommPort().getPortID() );
	}
	//----- End of defaults settings for the different instances of ports
	// last item to set for a port
}

/**
 * This method executes when the application is run from
 * the command prompt.
 * NOTE: THIS METHOD IS NOT USED WHEN THE GUI IS ACTIVATED
 * 
 * @param args java.lang.String[]
 */
public static void main(String[] args) 
{
	String filePathName;
	if( args.length > 0 )
	{
		filePathName = args[0];
		CTILogger.info("Import File Path:" + filePathName);

		System.setProperty( IRunnableDBTool.PROP_VALUE, 
				filePathName + IRunnableDBTool.FS );

		CentMaineDBConverter converter = new CentMaineDBConverter();
		converter.run();
		
	}
	else
	{
		// default path name
		CTILogger.info("Please enter a directory path for the location of conversion files");
		CTILogger.info("Syntax: CentMaineDBConverter c:/yukon/client/bin");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2001 11:56:45 AM)
 * @return boolean
 */






/**
 * Translates a given string into an Integer of seconds
 * @param string
 * @return
 */
public static Integer decodeStringToSeconds( String string ) 
{
	if( Integer.parseInt(string) >= 2400 )
		string = "2359";
	
	int hour = Integer.parseInt(
		(string.length() <= 2 ? "0" :
		  (string.length() <= 3 ? string.substring(0, 1) :
		   string.substring(0, 2))) ) * 3600;
		   
	int minute = Integer.parseInt(
		string.substring(
			(string.length() <= 1 ? 0 : string.length()-2),
			 string.length()) ) * 60;
	
	return new Integer(hour + minute);
}

/**
 * Valid Formats:
 *   Jan  1 1900 10:25PM
 *   Jan  1 1900  2:18PM
 *   Jan  1 1900  4:00AM
 * @param string
 * @return
 */
public static Integer decodeFunkySeconds( String string ) 
{
	StringBuffer buf = new StringBuffer(string);
	int midLoc = buf.indexOf( ":" );
	
	String h = buf.substring(midLoc-2, midLoc ).toString().trim();
	String m = buf.substring(midLoc+1, midLoc+3 ).toString().trim();
	
	int minute = Integer.parseInt(h) * 3600;
	int hour = Integer.parseInt(m) * 60;
	
	//if PM, add 12 hours worth of seconds
	return new Integer(hour + minute
	 	+ (string.toUpperCase().endsWith("PM") ? 43200 : 0));
}

private LocalSharedPort createDirectPort( Integer portID )
{
	LocalSharedPort port = null;

	try
	{
		//this createPort() call actually queries the database for a new unique portID!!
		// let this happen for now, but, a performance issue may occur
		port = (LocalSharedPort)PortFactory.createPort( PortTypes.LOCAL_SHARED );


		//set our own unique portID
		port.setPortName( "Port #" + (portID.intValue() - PORTID_OFFSET) );
		port.setPortID(portID);

		handleLocalDialupPort( (LocalDirectPort)port );
	}
	catch( java.sql.SQLException e )
	{
		CTILogger.error(e.getMessage(), e);
	}

	
	return port;

}

/**
 * Insert the method's description here.
 * Creation date: (3/31/2001 11:43:17 AM)
 * @return boolean
 */
public boolean processPortFile() 
{
	CTILogger.info("Starting Port file process...");
	getIMessageFrame().addOutput("Starting Port file process (" + commListFileName + ")...");
	
	String aFileName = getFullFileName(commListFileName);
	java.util.ArrayList lines = readFile(aFileName);
	NativeIntVector commChannels = new NativeIntVector(16);


	if( lines == null )
		return true; //continue the process

	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );
	
	for( int i = 0; i < lines.size(); i++ )
	{
		ts = new StringTokenizer(lines.get(i).toString(), "|");
		//ignore title line
		if( i <= 2)
			continue;
			
//		System.out.println("line "+i+":  "+line[i]);
//		while(ts.hasMoreTokens()){
//			System.out.println("line "+i+":  "+ts.nextToken().trim());
//		}
		CTILogger.info("PORT (Local Serial Port) line: " + lines.get(i).toString());
		String portNum = ts.nextToken().trim();
		
		Integer portID = new Integer(
				pInt(portNum.toString()).intValue() 
				+ PORTID_OFFSET );

		//ingore this, we already have it
		if( portID.intValue() == PORTID_OFFSET || commChannels.contains(portID.intValue()) )
			continue;
		ts.nextToken();
		ts.nextToken();

		LocalSharedPort port = (LocalSharedPort)createDirectPort( portID );
		port.getPortLocalSerial().setPhysicalPort( "Com" + ts.nextToken().trim().toString() );
		
		port.getPortSettings().setLineSettings("8N1");
		port.getPortSettings().setBaudRate(new Integer(1200));
		commChannels.add(portID.intValue());
		multi.getDBPersistentVector().add(port);
	}

	//boolean success = true; //writeToSQLDatabase(multi);
	boolean success = writeToSQLDatabase(multi);

	if( success )
	{
		CTILogger.info(" Port file processed and inserted Successfully");
		getIMessageFrame().addOutput(" Port file processed and inserted Successfully");
	}
	else
		getIMessageFrame().addOutput(" Port file failed insertion");

	return success;
}
/**
 * Insert the method's description here.
 * Creation date: (3/31/2001 11:43:17 AM)
 * @return boolean
 */
public boolean processTransmitterFile() 
{
	CTILogger.info("Starting Transmitter file process...");
	getIMessageFrame().addOutput("Starting Transmitter file process (" + transmitterFileName + ")...");
	
	String aFileName = getFullFileName(transmitterFileName);
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process

	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );
		
	for( int i = 0; i < lines.size(); i++ )
	{
		ts = new StringTokenizer(lines.get(i).toString(), "|");
		//ignore title line
		if( i <= 2)
			continue;
			
		CTILogger.info("TRANSMITTER line: " + lines.get(i).toString());
		
		String idNum = ts.nextToken().trim();
		System.out.println("idNum:  "+idNum);
		Integer deviceID = new Integer(pInt((idNum).substring(0,3)).intValue()+CCUID_OFFSET);
		
		if(idNum.equalsIgnoreCase("TESTCCU")){
			deviceID = new Integer(CCUID_OFFSET);
		}else if(idNum.equalsIgnoreCase("TESTCTT")){
			deviceID = new Integer(CCUID_OFFSET+1);
		}
		
		String deviceType = DeviceTypes.STRING_CCU_711[0];
		CCU711 device = null;

		device = (CCU711)DeviceFactory.createDevice( PAOGroups.getDeviceType( deviceType ) );
		device.setDeviceID(deviceID);
		
		//replace all blanks with a single space
		String name = idNum;
		device.setPAOName(name);
		String address = ts.nextToken().trim();
		device.getDeviceDirectCommSettings().setPortID( 
			new Integer(pInt(address.substring(0,2)).intValue() + PORTID_OFFSET) );
		System.out.println("address: "+address.substring(2,4));
		device.assignAddress(pInt(address.substring(2,4)) );
		
		device.getDeviceIDLCRemote().setCcuAmpUseType(DeviceIDLCRemote.AMPUSE_ALTERNATING);
		
//		device.getSeries5RTU().setStartCode( pInt(line[4].trim()) );
//		device.getSeries5RTU().setStopCode( pInt(line[5].trim()) );
//		device.getSeries5RTU().setTransmitOffset( pInt(line[6].trim()) );

		//CycleTime:#,Offset:#,TransmitTime:#,MaxTime:#
//		device.getPAOExclusionVector().add(
//			PAOExclusion.createExclusTiming(
//				device.getPAObjectID(),
//				new Integer(300),
//				device.getSeries5RTU().getTransmitOffset(),
//				new Integer(60) ) );

//		String dis = line[7].trim();
//		device.setDisableFlag( new Character(
//				(dis.equalsIgnoreCase("N") ? 'Y' : 'N')) );
//
//		device.getSeries5RTU().setSaveHistory( line[8].trim().toUpperCase() );
//
//		device.getSeries5RTU().setPowerValueMultiplier( pDbl(line[9].trim()) );
//		device.getSeries5RTU().setPowerValueOffset( pDbl(line[10].trim()) );
//		
//		//15
//		device.getSeries5RTU().setPowerValueHighLimit( pInt(line[15].trim()) );
//		device.getSeries5RTU().setPowerValueLowLimit( pInt(line[16].trim()) );
//		
//		device.getSeries5RTU().setRetries( pInt(line[19].trim()) );
		
		//create a route
//		RouteBase route = null;
//		route = RouteFactory.createRoute( RouteTypes.ROUTE_SERIES_5_LMI );
//
//		route.setRouteID( new Integer(START_ROUTE_ID++) );
//		route.setRouteName( device.getPAOName() + " Rt");
//		route.setDeviceID( deviceID );
//		route.setDefaultRoute("N");

		if( device.getDeviceDirectCommSettings().getPortID().intValue() > PORTID_OFFSET )
		{
			multi.getDBPersistentVector().add( device );
//			multi.getDBPersistentVector().add( route );
			deviceIDsMap.put( device.getDevice().getDeviceID(), device.getDevice().getDeviceID() );
//			routeIDsMap.put( route.getRouteID(), route.getRouteID() );
			
			
			//Create the PowerValue point
//			AnalogPoint pvPoint = (AnalogPoint)PointFactory.createPoint( PointTypes.ANALOG_POINT );
//
//			pvPoint.setPointID( new Integer(START_PTID) );
//			pvPoint.getPoint().setPaoID( deviceID );
//			pvPoint.getPoint().setPointOffset( new Integer(1000) );
//			pvPoint.getPoint().setPointName( "Power Value" );
//
//			pvPoint.getPointAnalog().setMultiplier( device.getSeries5RTU().getPowerValueMultiplier() );
//			pvPoint.getPointAnalog().setDataOffset( device.getSeries5RTU().getPowerValueOffset() );
//
//			PointLimit myPointLimit = new PointLimit();
//			myPointLimit.setPointID( pvPoint.getPoint().getPointID() );
//			myPointLimit.setHighLimit( new Double(device.getSeries5RTU().getPowerValueHighLimit().doubleValue()) );
//			myPointLimit.setLowLimit( new Double(device.getSeries5RTU().getPowerValueLowLimit().doubleValue()) );
//			myPointLimit.setLimitDuration( new Integer(0) );
//			myPointLimit.setLimitNumber( new Integer(1) );
//
//
//			if( myPointLimit.getLowLimit().doubleValue() != 0.0
//				 && myPointLimit.getHighLimit().doubleValue() != 0.0 )
//			{
//				Vector v = new Vector(1);
//				v.add( myPointLimit );
//				pvPoint.setPointLimitsVector(v);
//			}
//
//			// set default settings for the point
//			pvPoint.getPoint().setServiceFlag(new Character('N'));
//			pvPoint.getPoint().setAlarmInhibit(new Character('N'));
//			pvPoint.getPointAlarming().setAlarmStates( PointAlarming.DEFAULT_ALARM_STATES );
//			pvPoint.getPointAlarming().setExcludeNotifyStates( PointAlarming.DEFAULT_EXCLUDE_NOTIFY );
//			pvPoint.getPointAlarming().setNotifyOnAcknowledge( new String("N") );
//			pvPoint.getPointAlarming().setNotificationGroupID(  new Integer(PointAlarming.NONE_NOTIFICATIONID) );
//			pvPoint.getPoint().setArchiveType("None");
//			pvPoint.getPoint().setArchiveInterval( new Integer(0) );
//			pvPoint.getPoint().setStateGroupID( new Integer(-1) );
//			pvPoint.getPointUnit().setDecimalPlaces(new Integer(2));
//			pvPoint.getPointAnalog().setDeadband(new Double(0.0));
//			pvPoint.getPointAnalog().setTransducerType( new String("none") );
//			pvPoint.getPointUnit().setUomID( new Integer(8) );
//
//			multi.getDBPersistentVector().add( pvPoint );		
//			++START_PTID;
		}		
	}


	//boolean success = true;
	boolean success = writeToSQLDatabase(multi);

	if( success )
	{
		CTILogger.info(" Transmitter file processed and inserted Successfully");
		getIMessageFrame().addOutput(" Transmitter file processed and inserted Successfully");
	}
	else
		getIMessageFrame().addOutput(" Transmitter file failed insertion");

	return success;
}

public boolean processRepeaterFile() 
{
	CTILogger.info("Starting Repeater file process...");
	getIMessageFrame().addOutput("Starting Repeater file process (" + repeaterFileName + ")...");
	
	String aFileName = getFullFileName(repeaterFileName);
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process

	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );
		
	for( int i = 0; i < lines.size(); i++ )
	{
		ts = new StringTokenizer(lines.get(i).toString(), "|");
		//ignore title line
		if( i <= 8)
			continue;
			
		CTILogger.info("REPEATER line: " + lines.get(i).toString());
		String idNum = ts.nextToken().trim();
		Integer deviceID = new Integer(pInt((idNum).substring(3,6)).intValue()+RPTID_OFFSET);		
		String deviceType = DeviceTypes.STRING_REPEATER[0];
		Repeater900 device = null;
		
		device = (Repeater900)DeviceFactory.createDevice( PAOGroups.getDeviceType( deviceType ) );
		device.setDeviceID(deviceID);
		device.getDeviceRoutes().setRouteID(new Integer(0));
		//replace all blanks with a single space
		String name = idNum;
		device.setPAOName(name);
		// assign address of zero, manually entered later
		device.assignAddress(new Integer(0));
		multi.getDBPersistentVector().add( device );
	}
	
	boolean success = writeToSQLDatabase(multi);

	if( success )
	{
		CTILogger.info(" Repeater file processed and inserted Successfully");
		getIMessageFrame().addOutput(" Repeater file processed and inserted Successfully");
	}
	else
		getIMessageFrame().addOutput(" Repeater file failed insertion");

	return success;
}

public boolean processRouteFile() 
{
	CTILogger.info("Starting Route file process...");
	getIMessageFrame().addOutput("Starting Route file process (" + routeFileName + ")...");
	
	String aFileName = getFullFileName(routeFileName);
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process

	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );
		
	for( int i = 0; i < lines.size(); i++ )
	{
		ts = new StringTokenizer(lines.get(i).toString(), "|");
		//ignore title line
		if( i <= 7)
			continue;
			
		CTILogger.info("ROUTE line: " + lines.get(i).toString());
		String rteName = ts.nextToken().trim();
		System.out.println("rteName : "+rteName);
		ts.nextToken();
		String crtAdd = ts.nextToken().trim();
		System.out.println("crtAdd : "+ crtAdd);
		String ccuID = ts.nextToken().trim();
		System.out.println("CCU ID:  "+ccuID);
		String routeType = RouteTypes.STRING_CCU;
		CCURoute route = null;
		Integer deviceID = new Integer(pInt((ccuID).substring(0,3)).intValue()+CCUID_OFFSET);
		System.out.println("route line deviceID: "+deviceID);
		route = (CCURoute)com.cannontech.database.data.route.RouteFactory.createRoute( com.cannontech.database.data.pao.PAOGroups.getRouteType( routeType ));
		route.setDeviceID(deviceID);
		
		//replace all blanks with a single space
		String name = rteName;
		route.setRouteName(name);
		route.setRouteID(new Integer(ROUTE_ID));
		ROUTE_ID++;
		Integer busNum = null;
		System.out.println("substring "+crtAdd.substring(4,6));
		if((crtAdd).substring(4,6).equals("00")){
			busNum = new Integer(0);
		}else{
			busNum = new Integer(1);
		}
		route.getCarrierRoute().setBusNumber(busNum);
		route.setDefaultRoute("Y");
		
		multi.getDBPersistentVector().add( route );
		routeIDsMap.put( route.getRouteName(), route.getRouteID() );
	}
	
	boolean success = writeToSQLDatabase(multi);

	if( success )
	{
		CTILogger.info(" Route file processed and inserted Successfully");
		getIMessageFrame().addOutput(" Route file processed and inserted Successfully");
	}
	else
		getIMessageFrame().addOutput(" Route file failed insertion");

	return success;
}

public boolean processRouteMacrosFile() 
{
	CTILogger.info("Starting Route Macro file process...");
	getIMessageFrame().addOutput("Starting Route Macro file process...");
	
	String aFileName = getFullFileName(routeMacroFileName);
	
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process

	//create an object to hold all of our DBPersistant objects
	com.cannontech.database.data.multi.MultiDBPersistent multi = new com.cannontech.database.data.multi.MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );	
	int addCount = 0;
		
	for( int i = 0; i < lines.size(); i++ )
	{
		ts = new StringTokenizer(lines.get(i).toString(), "|");
		//ignore title lines and route list lines

		if( i <= 5 || ts.nextToken().trim() == null)
			continue;
			
		CTILogger.info("MACRO ROUTE line: " + lines.get(i).toString());
		
		String routeType = RouteTypes.STRING_MACRO;

		com.cannontech.database.data.route.MacroRoute route = (com.cannontech.database.data.route.MacroRoute)com.cannontech.database.data.route.RouteFactory.createRoute(routeType);

		//set our unique own routeID
		ts = new StringTokenizer(lines.get(i).toString(), "|");

		String name = "@"+ts.nextToken().trim();
		route.setRouteName(name);
		Integer routeID = new Integer(MACRO_ROUTE_ID);
		route.setRouteID(routeID);
		System.out.println("RouteID " +routeID);
		MACRO_ROUTE_ID++;
		//route.setDeviceID( new Integer(0) );
		route.setDefaultRoute(new String("N"));
		
		// make a vector for the routes in the macro
		java.util.Vector RouteMacroVector = new java.util.Vector();
	
		//com.cannontech.database.db.route.MacroRoute MacroRoute = null;

		// set our list of routes
		for(int count = 0; count < MAX_DSM2_MACRO_COUNT; count++)
		{   
			try{
				ts2 = new StringTokenizer(lines.get(i+1+count).toString(),"|");	
			}catch (Exception e){
				// no more lines
				break;
			}
			for(int j= 0; j<6;j++){
				ts2.nextToken();
			}
			String rteID = ts2.nextToken().trim();
			System.out.println("rteID :"+ rteID);
			
			if (rteID.equalsIgnoreCase(""))
			{
				// no more route ids in list
				break;
			}
			Object myRouteID = routeIDsMap.get(rteID);
			com.cannontech.database.db.route.MacroRoute
				MacroRoute = new com.cannontech.database.db.route.MacroRoute();
		
			MacroRoute.setRouteID(routeID);
			MacroRoute.setSingleRouteID((Integer) myRouteID );
			System.out.println("MyRouteID: "+myRouteID+"|");
			MacroRoute.setRouteOrder(new Integer( count + 1 ) );

			RouteMacroVector.addElement( MacroRoute );
			
		}

		// stuff the repeaters into the CCU Route
		route.setMacroRouteVector(RouteMacroVector);
		
		macroRouteIDsMap.put(route.getRouteName(),route.getRouteID());
		
		multi.getDBPersistentVector().add( route );
		++addCount;
	}


	boolean success = writeToSQLDatabase(multi);

	if( success )
	{
		CTILogger.info(" Route Macro file was processed and inserted Successfully");
		getIMessageFrame().addOutput("Route Macro file was processed and inserted Successfully");
		
		CTILogger.info(" " + addCount + " Macro Routes were added to the database");
		getIMessageFrame().addOutput(addCount + " Macro Routes were added to the database");
	}
	else
		getIMessageFrame().addOutput(" Macro Routes failed addition to the database");	
	return success;

}

public boolean processMCTFile() 
{
	CTILogger.info("Starting MCT Device file process...");
	getIMessageFrame().addOutput("Starting MCT Device file process...");
	
	String aFileName = getFullFileName(mctFileName);
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process

	//create an object to hold all of our DBPersistant objects
	com.cannontech.database.data.multi.MultiDBPersistent multi = new com.cannontech.database.data.multi.MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );
	int addCount = 0;
		
	for( int i = 0; i < lines.size(); i++ )
	{
		CTILogger.info("MCT line: " + lines.get(i).toString());
		
		ts = new StringTokenizer(lines.get(i).toString(), "|");
		Integer deviceID = new Integer(MCT_ID);
		MCT_ID++;
		String deviceType = null;
		String devID = ts.nextToken().trim();
		String dType = ts.nextToken().trim();
		String zone = ts.nextToken().trim();
		String leadMeter = ts.nextToken().trim();
		String leadLoad = ts.nextToken().trim();
		String address = ts.nextToken().trim();
		ts.nextToken();
		ts.nextToken();
		String active = ts.nextToken().trim();
		if(dType.equalsIgnoreCase("MC210I")){
			deviceType = DeviceTypes.STRING_MCT_210[0];
		}else if(dType.equalsIgnoreCase("MC240I") || dType.equalsIgnoreCase("MC240P")){
			deviceType = DeviceTypes.STRING_MCT_240[0];
		}else if(dType.equalsIgnoreCase("MC250P")){
			deviceType = DeviceTypes.STRING_MCT_250[0];
		}else if(dType.equalsIgnoreCase("MC248P")){
			deviceType = DeviceTypes.STRING_MCT_248[0];
		}else if(dType.equalsIgnoreCase("DCT501")){
			deviceType = DeviceTypes.STRING_DCT_501[0];
		}
		if(deviceType == DeviceTypes.STRING_DCT_501[0]){
			// do something
		}else{
			com.cannontech.database.data.device.MCTBase device = null;
					
			device = (com.cannontech.database.data.device.MCTBase)com.cannontech.database.data.device.DeviceFactory.createDevice( com.cannontech.database.data.pao.PAOGroups.getDeviceType( deviceType ) );
			
			//set our unique deviceID
			device.setDeviceID(deviceID);
			
			device.setDeviceClass( "CARRIER" );
	
			device.setDeviceType( deviceType );
	
			device.setPAOName(devID);
			
			deviceIDsMap.put( device.getPAOName(), device.getPAObjectID());
			
			// address,routeid,group1,group2,LsInt
			// set the MCT address
			device.getDeviceCarrierSettings().setAddress(new Integer(address));
	
			// set this devices route
			Object myRouteID = macroRouteIDsMap.get("@"+zone);
			device.getDeviceRoutes().setRouteID((Integer)myRouteID);
		    		
			// set group info
			device.getDeviceMeterGroup().setCollectionGroup(leadMeter);
	
			device.getDeviceMeterGroup().setTestCollectionGroup(leadLoad);

			device.getDeviceMeterGroup().setMeterNumber("10"+address);
	
			//added
			device.getDeviceMeterGroup().setBillingGroup( device.getDeviceMeterGroup().getBillingGroup() );
	
		    
			if (deviceType.equals(new String("MCT-240"))  ||
				deviceType.equals(new String("MCT-248"))  ||
				deviceType.equals(new String("MCT-250")))
			{
				// just guess that these are devices collect load profile
				device.getDeviceLoadProfile().setLoadProfileCollection( new String("YNNN") );
			}
			else
			{
				device.getDeviceLoadProfile().setLoadProfileCollection( new String("NNNN") );
			}
			
			// construct demand accumulator point
			com.cannontech.database.data.point.AccumulatorPoint accumPoint =(com.cannontech.database.data.point.AccumulatorPoint)com.cannontech.database.data.point.PointFactory.createPoint(com.cannontech.database.data.point.PointTypes.DEMAND_ACCUMULATOR_POINT);
			if(devID.substring((devID.length())-2,devID.length()).equalsIgnoreCase("KQ")){
				accumPoint.getPoint().setPointName("KQ");
				accumPoint.getPointUnit().setUomID( new Integer( 7 ) );
			}else{ 
				accumPoint.getPoint().setPointName("KW");
				accumPoint.getPointUnit().setUomID( new Integer( 0 ) );
			}
			accumPoint.getPoint().setPointType("Demand");
			
			// default state group ID
			accumPoint.getPoint().setPointID(new Integer(DEMAND_POINT_ID));
			DEMAND_POINT_ID++;
			accumPoint.getPoint().setStateGroupID( new Integer(-2) );
			accumPoint.getPoint().setPaoID( deviceID );
			accumPoint.getPoint().setArchiveType("On Update");
			accumPoint.getPointAccumulator().setDataOffset(new Double(1));
			if(String.valueOf(dType.charAt(5)).equalsIgnoreCase("I")){
				accumPoint.getPointAccumulator().setMultiplier(new Double(0.01));
			}else accumPoint.getPointAccumulator().setMultiplier(new Double(1.0));
			
			// set default settings for BASE point
			accumPoint.getPoint().setServiceFlag(new Character('N'));
			accumPoint.getPoint().setAlarmInhibit(new Character('N'));
			
			// set default settings for point ALARMING
			accumPoint.getPointAlarming().setAlarmStates( PointAlarming.DEFAULT_ALARM_STATES );
			accumPoint.getPointAlarming().setExcludeNotifyStates( PointAlarming.DEFAULT_EXCLUDE_NOTIFY );
			accumPoint.getPointAlarming().setNotifyOnAcknowledge( new String("N") );
			accumPoint.getPointAlarming().setNotificationGroupID(  new Integer(PointAlarming.NONE_NOTIFICATIONID) );
			accumPoint.getPointUnit().setDecimalPlaces(new Integer(2));
			
			// contruct pulse accumulator point
			com.cannontech.database.data.point.AccumulatorPoint pulseAccumPoint = (com.cannontech.database.data.point.AccumulatorPoint)com.cannontech.database.data.point.PointFactory.createPoint(com.cannontech.database.data.point.PointTypes.PULSE_ACCUMULATOR_POINT);
			if(devID.substring((devID.length())-2,devID.length()).equalsIgnoreCase("KQ")){
				pulseAccumPoint.getPoint().setPointName("KQh");
				pulseAccumPoint.getPointUnit().setUomID( new Integer( 7 ) );
			}else{ 
				pulseAccumPoint.getPoint().setPointName("KWh");
				pulseAccumPoint.getPointUnit().setUomID( new Integer( 0 ) );
			}
			pulseAccumPoint.getPoint().setPointType("Dial Read");
			pulseAccumPoint.getPoint().setPointID(new Integer(DEMAND_POINT_ID));
			DEMAND_POINT_ID++;
			pulseAccumPoint.getPoint().setStateGroupID( new Integer(-2) );
			pulseAccumPoint.getPoint().setPaoID( deviceID );
			pulseAccumPoint.getPoint().setArchiveType("On Update");
			pulseAccumPoint.getPointAccumulator().setDataOffset(new Double(1));
			if(String.valueOf(dType.charAt(5)).equalsIgnoreCase("I")){
				pulseAccumPoint.getPointAccumulator().setMultiplier(new Double(0.01));
			}else pulseAccumPoint.getPointAccumulator().setMultiplier(new Double(1.0));
			
			// set default settings for BASE point
			pulseAccumPoint.getPoint().setServiceFlag(new Character('N'));
			pulseAccumPoint.getPoint().setAlarmInhibit(new Character('N'));
			
			// set default settings for point ALARMING
			pulseAccumPoint.getPointAlarming().setAlarmStates( PointAlarming.DEFAULT_ALARM_STATES );
			pulseAccumPoint.getPointAlarming().setExcludeNotifyStates( PointAlarming.DEFAULT_EXCLUDE_NOTIFY );
			pulseAccumPoint.getPointAlarming().setNotifyOnAcknowledge( new String("N") );
			pulseAccumPoint.getPointAlarming().setNotificationGroupID(  new Integer(PointAlarming.NONE_NOTIFICATIONID) );
			pulseAccumPoint.getPointUnit().setDecimalPlaces(new Integer(2));
			device.getDeviceLoadProfile().setLastIntervalDemandRate(new Integer(300));
			device.getDeviceLoadProfile().setLoadProfileDemandRate(new Integer(300));
			if(active.equalsIgnoreCase("T")){
				device.setDisableFlag(new Character('Y'));
			}
			multi.getDBPersistentVector().add(device);
			multi.getDBPersistentVector().add(accumPoint);
			multi.getDBPersistentVector().add(pulseAccumPoint);
			++addCount;
		}
	}

	boolean success = writeToSQLDatabase(multi);

	if( success )
	{
		CTILogger.info(" MCT Device file was processed and inserted Successfully");
		getIMessageFrame().addOutput("MCT Device file was processed and inserted Successfully");
		
		CTILogger.info(" " + addCount + " MCT Devices were added to the database");
		getIMessageFrame().addOutput(addCount + " MCT Devices were added to the database");
	}
	else
		getIMessageFrame().addOutput(" MCT Devices failed adding to the database");
	return success;
}


/**
 * Parses a given String into an Integer, if any problem occurs,
 * an Integer with the value of 0 (Zero) is returned.
 * @param v
 * @return
 */
private Integer pInt( String v )
{
	try
	{
		if( v == null || (v = v.trim()).length() <= 0 )
			return new Integer(0);
		else
			return new Integer(v);
	}
	catch( NumberFormatException ex )
	{
		return new Integer(0);
	}

}

/**
 * Parses a given String into a Double, if any problem occurs,
 * a Double with the value of 0.0 (Zero) is returned.
 * @param v
 * @return
 */
private Double pDbl( String v )
{
	try
	{
		if( v == null || (v = v.trim()).length() <= 0 )
			return new Double(0);
		else
			return new Double(v);
	}
	catch( NumberFormatException ex )
	{
		return new Double(0);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (3/31/2001 11:31:42 AM)
 * 
 * Returns an arraylist of strings that represents each line in the
 * file. If a problem occurs, null is returned
 */
private java.util.ArrayList readFile(String fileName) 
{
	java.io.File file = new java.io.File(fileName);

	if( file.exists() )
	{
		try
		{
			java.io.RandomAccessFile fileReader = new java.io.RandomAccessFile(file, "r");
			java.util.ArrayList lines = new java.util.ArrayList(500);

			while( fileReader.getFilePointer() < fileReader.length() )
			{
				String line = fileReader.readLine();

				if( line != null && line.length() > 0 )
					lines.add( line );
			}	

			fileReader.close();
			return lines;  //file open/closed and read successfully
		}
		catch( java.io.IOException e)
		{
			CTILogger.error( e.getMessage(), e );
			return null;
		}
	}
	else
	{
		CTILogger.info( "Unable to find file '" + fileName +"'" );
		getIMessageFrame().addOutput( "Unable to find file '" + fileName +"'" );
	}
	return null;
}

/**
 * Adds an item to an ArrayList that is stored in a Hashmap.
 * @param map
 * @param key
 * @param value
 */
private void addToListMap( HashMap map, Object key, Object value )
{
	if( map.containsKey(key) )
	{
		ArrayList typeList = (ArrayList)map.get(key);
		typeList.add( value );
	}
	else
	{
		ArrayList typeList = new ArrayList(8);
		typeList.add( value );
		map.put( key, typeList );
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/31/2001 12:07:17 PM)
 * @param multi MultiDBPersistent
 */
private boolean writeToSQLDatabase(MultiDBPersistent multi) 
{
	//write all the collected data to the SQL database
	try
	{
      multi = (MultiDBPersistent)
   		com.cannontech.database.Transaction.createTransaction(
               com.cannontech.database.Transaction.INSERT, 
               multi).execute();

		return true;
	}
	catch( com.cannontech.database.TransactionException t )
	{
		CTILogger.error( t.getMessage(), t );
		return false;
	}
}

}