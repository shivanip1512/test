package com.cannontech.dbconverter.converter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.NativeIntVector;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.device.MCTIEDBase;
import com.cannontech.database.data.device.Series5LMI;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PortTypes;
import com.cannontech.database.data.pao.RouteTypes;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.data.port.LocalDirectPort;
import com.cannontech.database.data.port.PortFactory;
import com.cannontech.database.data.route.MacroRoute;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.data.route.RouteFactory;
import com.cannontech.database.data.state.GroupState;
import com.cannontech.database.data.state.State;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.database.db.state.YukonImage;
import com.cannontech.dbtools.updater.MessageFrameAdaptor;
import com.cannontech.tools.gui.*;

/**
 * Insert the type's description here.
 * Creation date: (1/10/2001 11:18:45 PM)
 * @author: 
 */
public class GREDBConverter extends MessageFrameAdaptor 
{
	private HashMap stateGrpMap = new HashMap();
	private HashMap deviceIDsMap = new HashMap();

	private int ROUTE_OFFSET = 500;
	private int START_ROUTE_ID = ROUTE_OFFSET + 1;

	private int ROUTE_MACRO_OFFSET = 1000;
	private int PORTID_OFFSET = 2000;

	private int START_PTID = 500;
	private int START_STATEGRP_ID = 100;

	private String analogPointFileName = "cti-analog.txt";
	public static final int ANALOG_PT_TOKEN_COUNT = 11;	

	private String transmitterFileName = "cti-transmitter.txt";
	public static final int TRANSMITTER_TOKEN_COUNT = 17;
	
	private String statusPointFileName = "cti-status.txt";
	public static final int STATUS_PT_TOKEN_COUNT = 8;
	
	private String routeMacroFileName = "cti-users.txt";
	public static final int ROUTE_MACRO_TOKEN_COUNT = 3;

	private String routeEntries = "cti-usertransmitter.txt";
	public static final int ROUTE_ENTRIES_TOKEN_COUNT = 2;

	
	private class GroupStateMod extends GroupState
	{
		public GroupStateMod( Integer ptid )
		{
			super(ptid);
		}

		public int hashCode()
		{
			if( this.getStatesVector().size() == 2 )
			{
				return
					this.getStatesVector().get(0).toString().hashCode()
					| this.getStatesVector().get(1).toString().hashCode();
			}
			else
				return super.hashCode();
		}

		//very custom equals method!!!
		public boolean equals(Object o )
		{
			if( o instanceof GroupState )
			{
				GroupState newGrpSt = (GroupState)o;
				return 
					this.getStatesVector().size() == 2
					&& newGrpSt.getStatesVector().size() == 2
					&& this.getStatesVector().get(0).toString().equalsIgnoreCase(
						newGrpSt.getStatesVector().get(0).toString())
					&& this.getStatesVector().get(1).toString().equalsIgnoreCase(
						newGrpSt.getStatesVector().get(1).toString());
			}
			else if( o instanceof String[] )
			{
				String[] newGrpSt = (String[])o;
				return 
					this.getStatesVector().size() == 2
					&& newGrpSt.length == 2
					&& this.getStatesVector().get(0).toString().equalsIgnoreCase(
						newGrpSt[0])
					&& this.getStatesVector().get(1).toString().equalsIgnoreCase(
						newGrpSt[1]);					
			}
			
			return false;
		}
		
	};
	
	
















	private static final int MAX_DSM2_MACRO_COUNT = 30;
	
	private String stateGroupFileName = "stategrp.txt";
	public static final int STATE_TOKEN_COUNT = 3;
		
	private String portFileName = "port.txt";
	public static final int PORT_TOKEN_COUNT = 12;
	
	private String virtualDeviceFileName = "pseudo.txt";
	public static final int VIRTUALDEV_TOKEN_COUNT = 3;

	private String baseRouteFileName = "route";
	public static final int SINGLE_ROUTE_TOKEN_COUNT = 5;

	private String baseRepeaterFileName = "rptdev";
	public static final int REPEATER_TOKEN_COUNT = 5;

	private String CBCFileName = "cbc.txt";
	public static final int CBC_TOKEN_COUNT = 5;

	private String LMGroupFileName = "lmgroup.txt";
	public static final int LMGROUP_TOKEN_COUNT = 6;
	
	private String MCTFileName = "mct.txt";
	public static final int MCT_TOKEN_COUNT = 7;

	private String RTUFileName = "rtus.txt";
	public static final int RTU_TOKEN_COUNT = 5;

	
	private String AccumPointFileName = "ptaccum.txt";
	public static final int ACCUM_PT_TOKEN_COUNT = 13;	

/**
 * DBConverter constructor comment.
 */
public GREDBConverter() 
{
	super();
}

public String getName()
{
	return "GRE Converter";
}

public String getParamText()
{
	return "Src-Directory:";
}

public void run()
{
	//boolean s = true;
	boolean s = processPortFile();
	if( s ) s = processTransmitterFile();
	if( s ) s = processStateGroupFile();
	if( s ) s = processStatusPoints();	
	if( s ) s = processAnalogPoints();
	
	if( s ) s = processRouteMacro();
	


/*	
	if( s ) s = processTransmitterFile();
	if( s ) s = processVirtualDeviceFile();
	if( s ) s = processSingleRouteFile();

	for (int myPassCount = 1; myPassCount < 4; ++myPassCount)
	{
		if( s ) s = processRepeaterFile(myPassCount);
		if( s ) s = processRptRouteFile(myPassCount);
	}

	if( s ) s = processRouteMacro();
	if( s ) s = processCapBankControllers();
	if( s ) s = processMCTDevices();
	if( s ) s = processRTUDevices();

	if( s ) s = processLoadGroups();
			
	// do the points for devices
	if( s ) s = processStatusPoints();
	if( s ) s = processAnalogPoints();
	if( s ) s = processAccumulatorPoints();
*/

	getIMessageFrame().addOutput("");
	getIMessageFrame().addOutput("");
	getIMessageFrame().addOutput("FINISHED with Database Conversion");

	if( s )
	{
		getIMessageFrame().finish( "SUCCESS: Database Conversion completed");
	}
	else
	{
		getIMessageFrame().finish( "FAILURE: Database Conversion completed");
	}

}


/**
 * Insert the method's description here.
 * Creation date: (12/14/99 10:31:33 AM)
 * @return java.lang.Integer
 */
public static synchronized PtUnitRets[] getAllPointUnitd()
{
	java.sql.Connection conn = com.cannontech.database.PoolManager.getInstance().getConnection( 
						com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
	
	java.sql.PreparedStatement stat = null;
	java.sql.ResultSet rs = null;
	java.util.ArrayList list = new java.util.ArrayList(10);

	try
	{
		stat = conn.prepareStatement(	 "select uomid,UomName from UnitMeasure" );
		
		rs = stat.executeQuery();

		while( rs.next() )
		{
			list.add( new PtUnitRets( rs.getInt("uomid"), rs.getString("UomName") ) );
		}
	}
	catch( Exception e )
	{
		CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try
		{
			if( stat != null )
				stat.close();
			if( rs != null )
				rs.close();
			if( conn != null )
				conn.close();
		}
		catch(java.sql.SQLException e )
		{
			CTILogger.error( e.getMessage(), e );
		}
	}
	return (PtUnitRets[])list.toArray();
}

public String getDefaultValue()
{
	return CtiUtilities.USER_DIR;
}

/**
 * appends a file name with the path
 * Creation date: (4/14/2001 3:13:24 PM)
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
private void handleLocalDirectPort( com.cannontech.database.data.port.LocalDirectPort port )
{
	port.getPortLocalSerial().setPhysicalPort( "Com1" );
	port.getPortSettings().setLineSettings( "8N1" );

	//----- Start of defaults settings for the different instances of ports
	if( port instanceof com.cannontech.database.data.port.LocalDialupPort )
	{
		((com.cannontech.database.data.port.LocalDialupPort)port).getPortDialupModem().setModemType("U.S. Robotics");
		((com.cannontech.database.data.port.LocalDialupPort)port).getPortDialupModem().setInitializationString(
			com.cannontech.common.util.CtiUtilities.STRING_NONE);
		
		((com.cannontech.database.data.port.LocalDialupPort)port).getPortDialupModem().setPrefixNumber("9");
		((com.cannontech.database.data.port.LocalDialupPort)port).getPortDialupModem().setSuffixNumber("9");
		((com.cannontech.database.data.port.LocalDialupPort)port).getPortDialupModem().setPortID( port.getCommPort().getPortID() );
	}
	//----- End of defaults settings for the different instances of ports
	// last item to set for a port
}


/**
 * Insert the method's description here.
 * Creation date: (4/14/2001 5:15:46 PM)
 */
private void handleRouteType(com.cannontech.database.data.route.CCURoute aRoute, java.util.StringTokenizer tokenizer) 
{
	aRoute.setDeviceID( new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim())) );//

	aRoute.setDefaultRoute(tokenizer.nextElement().toString().trim() );//		
	
	aRoute.getCarrierRoute().setCcuFixBits( new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim())) );
	aRoute.getCarrierRoute().setCcuVariableBits( new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim())) );	

	aRoute.getCarrierRoute().setBusNumber( new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim())) );
}


/**
 * Insert the method's description here.
 * Creation date: (4/14/2001 5:15:46 PM)
 */
private void handleRptRouteType(com.cannontech.database.data.route.CCURoute aRoute, java.util.StringTokenizer tokenizer) 
{
	aRoute.setDeviceID( new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim())) );//
	aRoute.setDefaultRoute(tokenizer.nextElement().toString().trim() );//		
	
	aRoute.getCarrierRoute().setCcuFixBits( new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim())) );
	aRoute.getCarrierRoute().setCcuVariableBits( new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim())) );	

	aRoute.getCarrierRoute().setBusNumber( new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim())) );

	// advance token to next because Amp does not exit anymore in the route
	tokenizer.nextElement().toString().trim();
	
}


/**
 * Insert the method's description here.
 * Creation date: (3/31/2001 12:57:46 PM)
 */
private void handleTerminalPort( com.cannontech.database.data.port.LocalDirectPort port, java.util.StringTokenizer tokenizer)
{
	//not sure if this will work every	time???
	port.getPortLocalSerial().setPhysicalPort( tokenizer.nextElement().toString().trim() );
	
	port.setPortName( tokenizer.nextElement().toString().trim() );

	port.getPortSettings().setBaudRate( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim()) ) );
	port.getPortSettings().setLineSettings( "8N1" );
	port.getCommPort().setCommonProtocol( tokenizer.nextElement().toString().trim() );

	// create a new PortTimeing object so we use the next tokens and possibly set
	//  the LocalSharedPorts values correctly
	com.cannontech.database.db.port.PortTiming timing = new com.cannontech.database.db.port.PortTiming();
	timing.setPreTxWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim()) ) );
	timing.setRtsToTxWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim()) ) );
	timing.setPostTxWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim()) ) );
	timing.setReceiveDataWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim()) ) );
	timing.setExtraTimeOut( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim()) ) );
	timing.setPortID( port.getCommPort().getPortID() );

	//----- Start of defaults settings for the different instances of ports
	if( port instanceof com.cannontech.database.data.port.LocalSharedPort )
	{
		((com.cannontech.database.data.port.LocalSharedPort)port).setPortTiming(timing);
	}

	if( port instanceof com.cannontech.database.data.port.LocalDialupPort )
	{
		((com.cannontech.database.data.port.LocalDialupPort)port).getPortDialupModem().setModemType("U.S. Robotics");
		((com.cannontech.database.data.port.LocalDialupPort)port).getPortDialupModem().setInitializationString(
					com.cannontech.common.util.CtiUtilities.STRING_NONE );
		
		((com.cannontech.database.data.port.LocalDialupPort)port).getPortDialupModem().setPrefixNumber("9");
		((com.cannontech.database.data.port.LocalDialupPort)port).getPortDialupModem().setSuffixNumber("9");
		((com.cannontech.database.data.port.LocalDialupPort)port).getPortDialupModem().setPortID( port.getCommPort().getPortID() );
	}

	if( port instanceof com.cannontech.database.data.port.LocalRadioPort )
	{
		((com.cannontech.database.data.port.LocalRadioPort)port).getPortRadioSettings().setPortID( port.getCommPort().getPortID() );
		((com.cannontech.database.data.port.LocalRadioPort)port).getPortRadioSettings().setRadioMasterTail( new Integer(0) );
		((com.cannontech.database.data.port.LocalRadioPort)port).getPortRadioSettings().setReverseRTS( new Integer(0) );
		((com.cannontech.database.data.port.LocalRadioPort)port).getPortRadioSettings().setRtsToTxWaitDiffD( new Integer(0) );
		((com.cannontech.database.data.port.LocalRadioPort)port).getPortRadioSettings().setRtsToTxWaitSameD( new Integer(0) );
	}
	//----- End of defaults settings for the different instances of ports
	
	// last item to set for a port
	port.getPortSettings().setCdWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim()) ) );
}


/**
 * Insert the method's description here.
 * Creation date: (3/31/2001 12:57:46 PM)
 */
private void handleTerminalPort( com.cannontech.database.data.port.TerminalServerDirectPort port, java.util.StringTokenizer tokenizer)
{
	String myTempIpPort = new String(tokenizer.nextElement().toString().trim());
	
	// IP and port is in format --> xx.xx.xx.xx:1000
	java.util.StringTokenizer myIPPorttoken = new java.util.StringTokenizer(myTempIpPort, ":");
	
	port.getPortTerminalServer().setIpAddress( myIPPorttoken.nextElement().toString() );
	
	port.getPortTerminalServer().setSocketPortNumber( new Integer(Integer.parseInt(myIPPorttoken.nextElement().toString())) );
	
	port.setPortName( tokenizer.nextElement().toString().trim() );

	port.getPortSettings().setBaudRate( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim()) ) );
	port.getPortSettings().setLineSettings( "8N1" );
	port.getCommPort().setCommonProtocol( tokenizer.nextElement().toString().trim() );

	// create a new PortTimeing object so we use the next tokens and possibly set
	//  the TerminalServerSharedPort values correctly
	com.cannontech.database.db.port.PortTiming timing = new com.cannontech.database.db.port.PortTiming();
	timing.setPreTxWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim()) ) );
	timing.setRtsToTxWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim()) ) );
	timing.setPostTxWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim()) ) );
	timing.setReceiveDataWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim()) ) );
	timing.setExtraTimeOut( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim()) ) );
	timing.setPortID( port.getCommPort().getPortID() );

	//----- Start of defaults settings for the different instances of ports
	if( port instanceof com.cannontech.database.data.port.TerminalServerSharedPort )
	{
		((com.cannontech.database.data.port.TerminalServerSharedPort)port).setPortTiming(timing);
	}

	if( port instanceof com.cannontech.database.data.port.TerminalServerDialupPort )
	{
		((com.cannontech.database.data.port.TerminalServerDialupPort)port).getPortDialupModem().setModemType("U.S. Robotics");
		((com.cannontech.database.data.port.TerminalServerDialupPort)port).getPortDialupModem().setInitializationString(
					com.cannontech.common.util.CtiUtilities.STRING_NONE);
					
		((com.cannontech.database.data.port.TerminalServerDialupPort)port).getPortDialupModem().setPrefixNumber("9");
		((com.cannontech.database.data.port.TerminalServerDialupPort)port).getPortDialupModem().setSuffixNumber("9");
		((com.cannontech.database.data.port.TerminalServerDialupPort)port).getPortDialupModem().setPortID( port.getCommPort().getPortID() );
	}

	if( port instanceof com.cannontech.database.data.port.TerminalServerRadioPort )
	{
		((com.cannontech.database.data.port.TerminalServerRadioPort)port).getPortRadioSettings().setPortID( port.getCommPort().getPortID() );
		((com.cannontech.database.data.port.TerminalServerRadioPort)port).getPortRadioSettings().setRadioMasterTail( new Integer(0) );
		((com.cannontech.database.data.port.TerminalServerRadioPort)port).getPortRadioSettings().setReverseRTS( new Integer(0) );
		((com.cannontech.database.data.port.TerminalServerRadioPort)port).getPortRadioSettings().setRtsToTxWaitDiffD( new Integer(0) );
		((com.cannontech.database.data.port.TerminalServerRadioPort)port).getPortRadioSettings().setRtsToTxWaitSameD( new Integer(0) );
	}
	//----- End of defaults settings for the different instances of ports

	// last item to set for a port
	port.getPortSettings().setCdWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim()) ) );
}


/**
 * Insert the method's description here.
 * Creation date: (1/10/2001 11:18:55 PM)
 * @param args java.lang.String[]
 *NOTE: THIS METHOD IS NOT USED WHEN GUI IS ACTIVATED
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

		GREDBConverter converter = new GREDBConverter();
		converter.run();

	}
	else
	{
		// default path name
		CTILogger.info("Please enter a directory path");
		CTILogger.info("Syntax: GREDBConverter c:/yukon/client/bin");
	}

	
}


/**
 * Insert the method's description here.
 * Creation date: (5/29/2001 9:13:14 AM)
 * @return boolean
 */
public boolean processAccumulatorPoints() 
{
	CTILogger.info("Starting Accumulator Point file process...");
	getIMessageFrame().addOutput("Starting Accumulator Point file process...");
	
	String aFileName = getFullFileName(AccumPointFileName);
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process

	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();
	
	// if this is not set to false it will create its own PointIDs
	multi.setCreateNewPAOIDs( false );

		
	int addCount = 0;
		
	for( int i = 0; i < lines.size(); i++ )
	{
		CTILogger.info("ACCUM_PT line: " + lines.get(i).toString());
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(lines.get(i).toString(), ",");
		if( tokenizer.countTokens() < ACCUM_PT_TOKEN_COUNT )
		{
			CTILogger.info("** Accumulator Point line #" + i + " has less than " + ACCUM_PT_TOKEN_COUNT + " tokens, EXITING.");
			getIMessageFrame().addOutput("** Accumulator Point line #" + i + " has less than " + ACCUM_PT_TOKEN_COUNT + " tokens, ignoring line.");
			return false;
		}

	    // PointID,PointType,PointName,DeviceId,ReadType,Offset,UOM_Text,Multiplier,Offset,Limits,hi,low,warning,high,low,Archivetype,ArchInterval
		// 4842,DemandAccumulator,MW,100,Demand,1,MW,2.500000,0.000000,None,0,0,,0,0,None,1
	    
		Integer pointID = new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim()) );
		
		String pointType = tokenizer.nextElement().toString().trim();
		
		com.cannontech.database.data.point.AccumulatorPoint accumPoint = null;
			
		//if pointType
		if (pointType.equals(new String("DemandAccumulator")))
		{
			// This is an Demand Accumulator point
			accumPoint = (com.cannontech.database.data.point.AccumulatorPoint)com.cannontech.database.data.point.PointFactory.createPoint(com.cannontech.database.data.point.PointTypes.DEMAND_ACCUMULATOR_POINT);

			// default state group ID
			accumPoint.getPoint().setStateGroupID( new Integer(-2) );
		}
		else
		{
			// This is an Accumulator point
			accumPoint = (com.cannontech.database.data.point.AccumulatorPoint)com.cannontech.database.data.point.PointFactory.createPoint(com.cannontech.database.data.point.PointTypes.PULSE_ACCUMULATOR_POINT);

			// default state group ID
			accumPoint.getPoint().setStateGroupID( new Integer(-2) );
		}
				
		//set our unique deviceID
		accumPoint.setPointID(pointID);
		
	    accumPoint.getPoint().setPointName( tokenizer.nextElement().toString().trim() );

		Integer deviceID = new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim()) );

		accumPoint.getPoint().setPaoID( deviceID );
		
		// advance token but this is not used
		tokenizer.nextElement();	//pseudoflag?
		
		accumPoint.getPoint().setPointOffset(new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim())));	
		
		// set default settings for BASE point
		//accumPoint.getPoint().setPseudoFlag(new Character('N'));
		accumPoint.getPoint().setServiceFlag(new Character('N'));
		accumPoint.getPoint().setAlarmInhibit(new Character('N'));


		// set default settings for point ALARMING
		accumPoint.getPointAlarming().setAlarmStates( PointAlarming.DEFAULT_ALARM_STATES );
		accumPoint.getPointAlarming().setExcludeNotifyStates( PointAlarming.DEFAULT_EXCLUDE_NOTIFY );
		accumPoint.getPointAlarming().setNotifyOnAcknowledge( new String("N") );
		accumPoint.getPointAlarming().setNotificationGroupID(  new Integer(PointAlarming.NONE_NOTIFICATIONID) );

		// set Point Units
		String comparer = new String( tokenizer.nextElement().toString().trim() );

		if( comparer.compareTo( "kVA" ) == 0 )
			accumPoint.getPointUnit().setUomID( new Integer( 2) );
		else if( comparer.compareTo( "kVAr" ) == 0 )
			accumPoint.getPointUnit().setUomID( new Integer( 3 ) );
		else if( comparer.compareTo( "kVAh" ) == 0 )
			accumPoint.getPointUnit().setUomID( new Integer (4) );
		else if( comparer.compareTo( "kVArh" ) == 0 )
			accumPoint.getPointUnit().setUomID( new Integer( 5 ) );
		else if( comparer.compareTo( "kVAr" ) == 0 )
			accumPoint.getPointUnit().setUomID( new Integer( 3 ) );
		else if( comparer.compareTo( "kW" ) == 0 )
			accumPoint.getPointUnit().setUomID( new Integer( 0 ) );
		else if( comparer.compareTo( "MW" ) == 0 )
			accumPoint.getPointUnit().setUomID( new Integer( 20 ) );
		else if( comparer.compareTo( "MWh" ) == 0 )
			accumPoint.getPointUnit().setUomID( new Integer( 21 ) );
		else if( comparer.compareTo( "kQ" ) == 0 )
			accumPoint.getPointUnit().setUomID( new Integer( 7 ) );
		else if( comparer.compareTo( "PF" ) == 0 )
			accumPoint.getPointUnit().setUomID( new Integer( 27 ) );
		else if( comparer.compareTo( "VOLTS" ) == 0 )
			accumPoint.getPointUnit().setUomID( new Integer( 35 ) );
		else 
			accumPoint.getPointUnit().setUomID( new Integer( 1  ) );
			
		accumPoint.getPointUnit().setDecimalPlaces(new Integer(2));
		
		accumPoint.getPointAccumulator().setMultiplier(new Double(tokenizer.nextElement().toString().trim()));
		accumPoint.getPointAccumulator().setDataOffset(new Double(tokenizer.nextElement().toString().trim()));
			
		// make a vector for the point limits
		java.util.Vector pointLimitVector = new java.util.Vector();
	
		int limitCount = 0;
		com.cannontech.database.db.point.PointLimit myPointLimit = null;

		// two set of limits
		for(int count = 0; count < 2; count++)
		{
			String myLimitName = tokenizer.nextElement().toString().trim();

			Double myHighLimit = new Double( tokenizer.nextElement().toString().trim() );
			Double myLowLimit = new Double( tokenizer.nextElement().toString().trim() );

			if (!myLimitName.equals( new String("None") ))
			{
				++limitCount;
				myPointLimit = new com.cannontech.database.db.point.PointLimit();
		
				myPointLimit.setPointID(pointID);
				myPointLimit.setLowLimit(myLowLimit);
				myPointLimit.setHighLimit(myHighLimit);

				myPointLimit.setLimitDuration( new Integer(0) );
				myPointLimit.setLimitNumber( new Integer(limitCount) );
	
 	  			pointLimitVector.addElement( myPointLimit );

	   			myPointLimit = null;
			}
		}

		if (limitCount > 0)
		{
			// stuff the limits into the point
			accumPoint.setPointLimitsVector(pointLimitVector);
		}
	
		//archiving settings
		accumPoint.getPoint().setArchiveType(tokenizer.nextElement().toString().trim());
		accumPoint.getPoint().setArchiveInterval( new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim())) );
			
		multi.getDBPersistentVector().add( accumPoint );
		
		++addCount;
	}
	boolean success = writeToSQLDatabase(multi);

	if( success )
	{
		CTILogger.info(" Accumulator Point file was processed and inserted Successfully");
		getIMessageFrame().addOutput(" Accumulator Point file was processed and inserted Successfully");
	}
	else
		getIMessageFrame().addOutput(" Accumulator Point failed insertion");
		
	return success;
}


/**
 * Insert the method's description here.
 * Creation date: (5/2/2001 7:26:06 PM)
 * @return boolean
 */
public boolean processAnalogPoints() 
{
	CTILogger.info("Starting Analog Point file process...");
	getIMessageFrame().addOutput("Starting Analog Point file process (" + analogPointFileName + ")...");
	
	String aFileName = getFullFileName(analogPointFileName);
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process


	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();

	// if this is not set to false it will create its own PointIDs
	multi.setCreateNewPAOIDs( false );
	
	int addCount = 0;
		
	for( int i = 0; i < lines.size(); i++ )
	{
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(lines.get(i).toString(), ",");

		//ignore title line
		if( i <= 2 || tokenizer.countTokens() < STATUS_PT_TOKEN_COUNT )
			continue;
		
		CTILogger.info("ANALOG_PT line: " + lines.get(i).toString());

		//skip first token
		tokenizer.nextToken();

		AnalogPoint anaPoint = 
			(AnalogPoint)PointFactory.createPoint( PointTypes.ANALOG_POINT );

		anaPoint.setPointID( new Integer(START_PTID) );
		Integer deviceID = pInt(tokenizer.nextToken());
		anaPoint.getPoint().setPaoID( deviceID );

		Integer ptOffset = pInt(tokenizer.nextToken());		
		anaPoint.getPoint().setPointOffset(ptOffset);


		//skip next 2 tokens
		tokenizer.nextToken();tokenizer.nextToken();

		anaPoint.getPointAnalog().setMultiplier( pDbl(tokenizer.nextToken()) );
		anaPoint.getPointAnalog().setDataOffset( pDbl(tokenizer.nextToken()) );

		com.cannontech.database.db.point.PointLimit myPointLimit =
				new com.cannontech.database.db.point.PointLimit();

		myPointLimit.setPointID( anaPoint.getPoint().getPointID() );
		myPointLimit.setHighLimit( pDbl(tokenizer.nextToken()) );
		myPointLimit.setLowLimit( pDbl(tokenizer.nextToken()) );
		myPointLimit.setLimitDuration( new Integer(0) );
		myPointLimit.setLimitNumber( new Integer(1) );


		if( myPointLimit.getLowLimit().doubleValue() != 0.0
			 && myPointLimit.getHighLimit().doubleValue() != 0.0 )
		{
			//if( myPointLimit.getHighLimit().doubleValue() >= CtiUtilities.INVALID_MIN_DOUBLE
			Vector v = new Vector(1);
			v.add( myPointLimit );
			anaPoint.setPointLimitsVector(v);
		}


		anaPoint.getPoint().setPointName( tokenizer.nextToken() );
		
		// set default settings for the point
		anaPoint.getPoint().setServiceFlag(new Character('N'));
		anaPoint.getPoint().setAlarmInhibit(new Character('N'));
		anaPoint.getPointAlarming().setAlarmStates( PointAlarming.DEFAULT_ALARM_STATES );
		anaPoint.getPointAlarming().setExcludeNotifyStates( PointAlarming.DEFAULT_EXCLUDE_NOTIFY );
		anaPoint.getPointAlarming().setNotifyOnAcknowledge( new String("N") );
		anaPoint.getPointAlarming().setNotificationGroupID(  new Integer(PointAlarming.NONE_NOTIFICATIONID) );
		anaPoint.getPoint().setArchiveType("None");
		anaPoint.getPoint().setArchiveInterval( new Integer(0) );
		anaPoint.getPoint().setStateGroupID( new Integer(-1) );

		anaPoint.getPointUnit().setDecimalPlaces(new Integer(2));
		anaPoint.getPointAnalog().setDeadband(new Double(0.0));
		anaPoint.getPointAnalog().setTransducerType( new String("none") );


		if( anaPoint.getPoint().getPointName().toUpperCase().indexOf("VOLTAGE") != -1 )
			anaPoint.getPointUnit().setUomID( new Integer(35) );
		else if( anaPoint.getPoint().getPointName().toUpperCase().indexOf("TEMP") != -1 )
			anaPoint.getPointUnit().setUomID( new Integer(33) );
		else if( anaPoint.getPoint().getPointName().toUpperCase().indexOf("CURRENT") != -1 )
			anaPoint.getPointUnit().setUomID( new Integer(8) );
		else if( anaPoint.getPoint().getPointName().toUpperCase().indexOf("REFERENCE") != -1 )
			anaPoint.getPointUnit().setUomID( new Integer(9) );
		else 
			anaPoint.getPointUnit().setUomID( new Integer(0) );

		if( !anaPoint.getPoint().getPointName().toUpperCase().startsWith("FUTURE SPARE")
			 && deviceIDsMap.get(anaPoint.getPoint().getPaoID()) != null )
		{
			multi.getDBPersistentVector().add( anaPoint );		
			++addCount;
			++START_PTID;
		}

	}

	boolean success = writeToSQLDatabase(multi);

	if( success )
	{
		CTILogger.info(" Analog Point file was processed and inserted Successfully");
		getIMessageFrame().addOutput("Analog Point file was processed and inserted Successfully");
			
		CTILogger.info(" " + addCount + " Analog Points were added to the database");
		getIMessageFrame().addOutput(addCount + " Analog Points were added to the database");
	}
	else
		getIMessageFrame().addOutput(" Analog Points failed adding to the database");
	return success;
}


/**
 * Insert the method's description here.
 * Creation date: (4/21/2001 9:26:01 PM)
 * @return boolean
 */
public boolean processCapBankControllers() 
{
	CTILogger.info("Starting Cap Bank Controller file process...");
	getIMessageFrame().addOutput("Starting Cap Bank Controller file process...");
	
	String aFileName = getFullFileName(CBCFileName);
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process

	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );
	
	int addCount = 0;
		
	for( int i = 0; i < lines.size(); i++ )
	{
		CTILogger.info("CBC line: " + lines.get(i).toString());
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(lines.get(i).toString(), ",");
		if( tokenizer.countTokens() < CBC_TOKEN_COUNT )
		{
			CTILogger.info("** CBC line #" + i + " has less than " + CBC_TOKEN_COUNT + " tokens, EXITING.");
			getIMessageFrame().addOutput("** CBC line #" + i + " has less than " + CBC_TOKEN_COUNT + " tokens, EXITING.");
			return false;
		}
			
		Integer deviceID = new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim()) );
		String deviceType = tokenizer.nextElement().toString().trim();
		
		com.cannontech.database.data.capcontrol.CapControlDeviceBase cbcDevice = null;


		cbcDevice = (com.cannontech.database.data.capcontrol.CapControlDeviceBase)com.cannontech.database.data.device.DeviceFactory.createDevice( com.cannontech.database.data.pao.PAOGroups.getDeviceType( deviceType ) );
		
		//set our unique own deviceID
		cbcDevice.setDeviceID(deviceID);
		
		cbcDevice.setPAOName( tokenizer.nextElement().toString().trim() );
 
	    ((com.cannontech.database.data.capcontrol.CapBankController)cbcDevice).getDeviceCBC().setSerialNumber( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim())) );
	    ((com.cannontech.database.data.capcontrol.CapBankController)cbcDevice).getDeviceCBC().setRouteID( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim())) );
	    	
		multi.getDBPersistentVector().add( cbcDevice );
		
		++addCount;
	}

	boolean success = writeToSQLDatabase(multi);

	if( success )
	{
		CTILogger.info(" CBC file was processed and inserted Successfully");
		getIMessageFrame().addOutput("CBC file was processed and inserted Successfully");
		
		CTILogger.info(" " + addCount + " CBC Devices were added to the database");
		getIMessageFrame().addOutput(addCount + " CBC Devices were added to the database");
	}
	else
		getIMessageFrame().addOutput(" CBC Devices failed addition to the database");
	return success;
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2001 11:56:45 AM)
 * @return boolean
 */
public boolean processLoadGroups() 
{
	CTILogger.info("Starting Load Group file process...");
	getIMessageFrame().addOutput("Starting Load Group file process...");
	
	String aFileName = getFullFileName(LMGroupFileName);
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process
	
	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );
		
	int addCount = 0;
		
	for( int i = 0; i < lines.size(); i++ )
	{
		CTILogger.info("LOAD_GRP line: " + lines.get(i).toString());
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(lines.get(i).toString(), ",");
		if( tokenizer.countTokens() < LMGROUP_TOKEN_COUNT )
		{
			CTILogger.info("** LM Group line #" + i + " has less than " + LMGROUP_TOKEN_COUNT + " tokens, EXITING.");
			getIMessageFrame().addOutput("** LM Group line #" + i + " has less than " + LMGROUP_TOKEN_COUNT + " tokens, EXITING.");
			return false;
		}
			
		// Fields:
		// DeviceId,GroupType,DeviceName,routeID,Relay,AddressUsage,UtilID or GoldAdd,Section or Silver,Class,Division

		Integer deviceID = new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim()) );
		String deviceType = tokenizer.nextElement().toString().trim();
		
		com.cannontech.database.data.device.lm.LMGroup lmGroupDevice = null;

		lmGroupDevice = (com.cannontech.database.data.device.lm.LMGroup)com.cannontech.database.data.device.lm.LMFactory.createLoadManagement(com.cannontech.database.data.pao.PAOGroups.getDeviceType( deviceType ) );
		
		//set our unique own deviceID
		lmGroupDevice.setDeviceID(deviceID);
		
	    lmGroupDevice.setPAOName( tokenizer.nextElement().toString().trim() );
	    
		if (deviceType.equals(new String("EMETCON GROUP")))
		{
			// This is an Emetcon Group
			((com.cannontech.database.data.device.lm.LMGroupEmetcon)lmGroupDevice).getLmGroupEmetcon().setRouteID( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim())) );
			((com.cannontech.database.data.device.lm.LMGroupEmetcon)lmGroupDevice).getLmGroupEmetcon().setRelayUsage( new Character(tokenizer.nextElement().toString().trim().charAt(0)) );
			((com.cannontech.database.data.device.lm.LMGroupEmetcon)lmGroupDevice).getLmGroupEmetcon().setAddressUsage( new Character(tokenizer.nextElement().toString().trim().charAt(0)) );
			((com.cannontech.database.data.device.lm.LMGroupEmetcon)lmGroupDevice).getLmGroupEmetcon().setSilverAddress( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim())) );
			((com.cannontech.database.data.device.lm.LMGroupEmetcon)lmGroupDevice).getLmGroupEmetcon().setGoldAddress( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim())) );
		}
		else if (deviceType.equals(new String("RIPPLE GROUP")))
		{
			// Fields:
			// DeviceId,GroupType,GroupName,routeID,ShedMinutes,ControlMessage,RestoreMessage
			
			// This is an Ripple Group
			((com.cannontech.database.data.device.lm.LMGroupRipple)lmGroupDevice).getLmGroupRipple().setRouteID( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim())) );

			((com.cannontech.database.data.device.lm.LMGroupRipple)lmGroupDevice).getLmGroupRipple().setShedTime( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim())) );

			((com.cannontech.database.data.device.lm.LMGroupRipple)lmGroupDevice).getLmGroupRipple().setControl( tokenizer.nextElement().toString().trim() );
			((com.cannontech.database.data.device.lm.LMGroupRipple)lmGroupDevice).getLmGroupRipple().setRestore( tokenizer.nextElement().toString().trim() );
		}
		else
		{
			// This is a VERSACOM Group
			((com.cannontech.database.data.device.lm.LMGroupVersacom)lmGroupDevice).getLmGroupVersacom().setRouteID( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim())) );

			((com.cannontech.database.data.device.lm.LMGroupVersacom)lmGroupDevice).getLmGroupVersacom().setRelayUsage( tokenizer.nextElement().toString().trim() );

			String aAddressUsage = tokenizer.nextElement().toString().trim();
				
			//((com.cannontech.database.data.device.lm.LMGroupVersacom)lmGroupDevice).getLmGroupVersacom().setAddressUsage( tokenizer.nextElement().toString().trim() );
			((com.cannontech.database.data.device.lm.LMGroupVersacom)lmGroupDevice).getLmGroupVersacom().setUtilityAddress( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim())) );
			((com.cannontech.database.data.device.lm.LMGroupVersacom)lmGroupDevice).getLmGroupVersacom().setSectionAddress( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim())) );
			((com.cannontech.database.data.device.lm.LMGroupVersacom)lmGroupDevice).getLmGroupVersacom().setClassAddress( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim())) );
			((com.cannontech.database.data.device.lm.LMGroupVersacom)lmGroupDevice).getLmGroupVersacom().setDivisionAddress( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim())) );

			if (aAddressUsage.equals(new String("U   X")))
			{
				// serial number group
				((com.cannontech.database.data.device.lm.LMGroupVersacom)lmGroupDevice).getLmGroupVersacom().setAddressUsage( new String("U   "));
				// set the serial number
				((com.cannontech.database.data.device.lm.LMGroupVersacom)lmGroupDevice).getLmGroupVersacom().setSerialAddress( tokenizer.nextElement().toString().trim() );
			}
			else
			{
				((com.cannontech.database.data.device.lm.LMGroupVersacom)lmGroupDevice).getLmGroupVersacom().setAddressUsage(aAddressUsage);			
			}
			
		}
	    	
		multi.getDBPersistentVector().add( lmGroupDevice );
		
		++addCount;
	}

	boolean success = writeToSQLDatabase(multi);

	if( success )
	{
		CTILogger.info(" Load Group file was processed and inserted Successfully");
		getIMessageFrame().addOutput("Load Group file was processed and inserted Successfully");
		
		CTILogger.info(" " + addCount + " Load Groups were added to the database");
		getIMessageFrame().addOutput(addCount + " Load Groups were added to the database");
	}
	else
		getIMessageFrame().addOutput(" Load Groups failed adding to the database");
	return success;
}


/**
 * Insert the method's description here.
 * Creation date: (4/25/2001 7:42:09 PM)
 * @return boolean
 */
public boolean processMCTDevices() 
{
	CTILogger.info("Starting MCT Device file process...");
	getIMessageFrame().addOutput("Starting MCT Device file process...");
	
	String aFileName = getFullFileName(MCTFileName);
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process

	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );
	int addCount = 0;
		
	for( int i = 0; i < lines.size(); i++ )
	{
		CTILogger.info("MCT line: " + lines.get(i).toString());
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(lines.get(i).toString(), ",");
		if( tokenizer.countTokens() < MCT_TOKEN_COUNT )
		{
			CTILogger.info("** MCT line #" + i + " has less than " + MCT_TOKEN_COUNT + " tokens, EXITING.");
			getIMessageFrame().addOutput("** MCT line #" + i + " has less than " + MCT_TOKEN_COUNT + " tokens, EXITING.");
			return false;
		}
			
		Integer deviceID = new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim()) );
		String deviceType = tokenizer.nextElement().toString().trim();
		
		com.cannontech.database.data.device.MCTBase device = null;
				
		//if (deviceType.equals(new String("MCT-213")))
		//{
			// This a temporary work around - no 213 device types
			//device = (com.cannontech.database.data.device.MCTBase)com.cannontech.database.data.device.DeviceFactory.createDevice( com.cannontech.database.data.pao.PAOGroups.MCT210);
		//}
		//else
		//{
		device = (com.cannontech.database.data.device.MCTBase)com.cannontech.database.data.device.DeviceFactory.createDevice( com.cannontech.database.data.pao.PAOGroups.getDeviceType( deviceType ) );
		//}
		
		//set our unique deviceID
		device.setDeviceID(deviceID);
		
		device.setDeviceClass( "CARRIER" );

		device.setDeviceType( deviceType );

	    device.setPAOName( tokenizer.nextElement().toString().trim() );

		// address,routeid,group1,group2,LsInt
		// set the MCT address
		device.getDeviceCarrierSettings().setAddress( new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim() )));

		// set this devices route
		device.getDeviceRoutes().setRouteID(new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim() )));
	    		
		// set group info
		device.getDeviceMeterGroup().setCollectionGroup( tokenizer.nextElement().toString().trim() );

		device.getDeviceMeterGroup().setTestCollectionGroup( tokenizer.nextElement().toString().trim() );

		device.getDeviceMeterGroup().setMeterNumber(new String("0"));

		//added
		device.getDeviceMeterGroup().setBillingGroup( device.getDeviceMeterGroup().getBillingGroup() );
	
		// set LoadProfile Interval
		device.getDeviceLoadProfile().setLoadProfileDemandRate(new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim()) ));

	    
		if (deviceType.equals(new String("MCT-318L")) || 
			deviceType.equals(new String("MCT-310IL"))  ||
			deviceType.equals(new String("MCT-240"))  ||
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

		
		if( deviceType.equals(new String("MCT-360")) || deviceType.equals(new String("MCT-370")) )
		{
			// These devices need some more defaults set
			((MCTIEDBase)device).getDeviceMCTIEDPort().setConnectedIED(new String("Alpha Power Plus"));
			((MCTIEDBase)device).getDeviceMCTIEDPort().setIEDScanRate(new Integer(120));
			((MCTIEDBase)device).getDeviceMCTIEDPort().setDefaultDataClass(new Integer(72));
			((MCTIEDBase)device).getDeviceMCTIEDPort().setDefaultDataOffset(new Integer(0));
			((MCTIEDBase)device).getDeviceMCTIEDPort().setPassword(new String("0000"));
			((MCTIEDBase)device).getDeviceMCTIEDPort().setRealTimeScan(new Character('N'));
		}

		
		multi.getDBPersistentVector().add( device );
		++addCount;
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
 * Insert the method's description here.
 * Creation date: (3/31/2001 11:43:17 AM)
 * @return boolean
 */
public boolean processPortFile() 
{
	CTILogger.info("Starting Port file process...");
	getIMessageFrame().addOutput("Starting Port file process (" + transmitterFileName + ")...");
	
	String aFileName = getFullFileName(transmitterFileName);
	java.util.ArrayList lines = readFile(aFileName);
	NativeIntVector commChannels = new NativeIntVector(16);


	if( lines == null )
		return true; //continue the process

	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );
	
	for( int i = 0; i < lines.size(); i++ )
	{
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(lines.get(i).toString(), ",");

		//ignore title line
		if( i == 0 || tokenizer.countTokens() < TRANSMITTER_TOKEN_COUNT )
			continue;
		
		CTILogger.info("PORT line: " + lines.get(i).toString());

		//skip 2 tokens
		tokenizer.nextToken();
		tokenizer.nextToken();
			
		Integer portID = new Integer(
				pInt(tokenizer.nextToken()).intValue() 
				+ PORTID_OFFSET );

		com.cannontech.database.data.port.DirectPort port = null;
		
		
		//ingore this, we already have it
		if( portID.intValue() == PORTID_OFFSET || commChannels.contains(portID.intValue()) )
			continue;
		
		try
		{
			//this createPort() call actually queries the database for a new unique portID!!
			// let this happen for now, but, a performance issue may occur
			port = PortFactory.createPort( PortTypes.LOCAL_SHARED );
		}
		catch( java.sql.SQLException e )
		{
			CTILogger.error( e.getMessage(), e );
		}

		//set our unique own portID
		port.setPortName( "Port #" + (portID.intValue() - PORTID_OFFSET) );
		port.setPortID(portID);

		handleLocalDirectPort( (LocalDirectPort)port );

		commChannels.add( portID.intValue() );
		multi.getDBPersistentVector().add( port );
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
 * Creation date: (4/16/2001 11:00:11 AM)
 * @return boolean
 * @param aPassCount int
 */
public boolean processRepeaterFile(int aPassCount) 
{
	CTILogger.info("Starting Repeater Pass " + aPassCount + " file process...");
	getIMessageFrame().addOutput("Starting Repeater Pass " + aPassCount + " file process...");
	
	String aFileName = getFullFileName(baseRepeaterFileName) + + aPassCount + ".txt";
	
	java.util.ArrayList lines = readFile(aFileName);
	
	if( lines == null )
		return true; //continue the process

	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );
		
	for( int i = 0; i < lines.size(); i++ )
	{
		CTILogger.info("REPTR line: " + lines.get(i).toString());
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(lines.get(i).toString(), ",");
		if( tokenizer.countTokens() < REPEATER_TOKEN_COUNT )
		{
			CTILogger.info("** Repeater line #" + (i + 1) + " has less than " + REPEATER_TOKEN_COUNT + " tokens, EXITING.");
			getIMessageFrame().addOutput("** Repeater line #" + (i + 1) + " has less than " + REPEATER_TOKEN_COUNT + " tokens, EXITING.");
			return false;
		}
			
		Integer deviceID = new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim()) );
		String deviceType = tokenizer.nextElement().toString().trim();
		
		com.cannontech.database.data.device.Repeater900 device = (com.cannontech.database.data.device.Repeater900)com.cannontech.database.data.device.DeviceFactory.createDevice( com.cannontech.database.data.pao.PAOGroups.REPEATER);

		//set our unique own deviceID
		device.setDeviceID(deviceID);
		
		int deviceInt = com.cannontech.database.data.pao.PAOGroups.getDeviceType( deviceType );
		
		device.setDeviceClass( "CARRIER" );

		device.setDeviceType( deviceType );
		device.setPAOName( tokenizer.nextElement().toString().trim() );

		// set the repeater address
		device.getDeviceCarrierSettings().setAddress( new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim() )));

		// set this devices route
		device.getDeviceRoutes().setRouteID(new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim() )));
		
		multi.getDBPersistentVector().add( device );
	}
	boolean success = writeToSQLDatabase(multi);

	if( success )
	{
		CTILogger.info(" Repeater file " + aPassCount + " processed and inserted Successfully");
		getIMessageFrame().addOutput(" Repeater file " + aPassCount + " processed and inserted Successfully");
	}
	else
		getIMessageFrame().addOutput(" Repeater file failed insertion");
	return success;
}


/**
 * Insert the method's description here.
 * Creation date: (4/20/2001 7:01:33 PM)
 * @return boolean
 */
public boolean processRouteMacro() 
{
	CTILogger.info("Starting Route Macro file process...");
	getIMessageFrame().addOutput("Starting Route Macro file process (" + routeMacroFileName + ")...");
	
	String aFileName = getFullFileName(routeMacroFileName);
	
	java.util.ArrayList lines = readFile(aFileName);

	String rtEntriesFile = getFullFileName(routeEntries);	
	java.util.ArrayList routeEntryLines = readFile(rtEntriesFile);


	if( lines == null )
		return true; //continue the process

	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );	
	int addCount = 0;
		
	for( int i = 0; i < lines.size(); i++ )
	{
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(lines.get(i).toString(), ",");

		//ignore title line
		if( i <= 2 || tokenizer.countTokens() < ROUTE_MACRO_TOKEN_COUNT )
			continue;

		CTILogger.info("ROUTE_MAC line: " + lines.get(i).toString());
		
		//skip token 1
		tokenizer.nextToken();

		Integer tempUserID = pInt(tokenizer.nextToken());
		MacroRoute macRoute = (MacroRoute)RouteFactory.createRoute(RouteTypes.ROUTE_MACRO);

		macRoute.setRouteID( new Integer(ROUTE_MACRO_OFFSET++) );
		macRoute.setRouteName( "@" + tokenizer.nextToken().trim() + " #" + ROUTE_MACRO_OFFSET );
		macRoute.setDeviceID( new Integer(0) );
		macRoute.setDefaultRoute(new String("N"));


		if( !macRoute.getRouteName().toUpperCase().startsWith("@FUTURE") )
		{
			int order = 1;
			for( int j = 0; j < routeEntryLines.size(); j++ )
			{
				StringTokenizer entrTokenizer = new StringTokenizer(routeEntryLines.get(j).toString(), ",");
				//ignore title line
				if( j <= 2 || entrTokenizer.countTokens() < ROUTE_ENTRIES_TOKEN_COUNT )
					continue;
				
				Integer devID = pInt(entrTokenizer.nextToken());
				Integer userID = pInt(entrTokenizer.nextToken());

				if( userID.intValue() == tempUserID.intValue() )
				{
					com.cannontech.database.db.route.MacroRoute
						macRouteEntry = new com.cannontech.database.db.route.MacroRoute();

					macRouteEntry.setRouteID( macRoute.getRouteID() );
					macRouteEntry.setRouteOrder( new Integer(order++) );
					macRouteEntry.setSingleRouteID( 
						new Integer(devID.intValue() + ROUTE_OFFSET) );

					macRoute.getMacroRouteVector().add( macRouteEntry );
				}
			}
			
			if( macRoute.getMacroRouteVector().size() > 0 )
			{
				multi.getDBPersistentVector().add( macRoute );
				++addCount;
			}

		}

	}


	//boolean success = false; //writeToSQLDatabase(multi);
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


/**
 * Insert the method's description here.
 * Creation date: (4/17/2001 12:28:40 PM)
 * @return boolean
 * @param aPassNumber int
 */
public boolean processRptRouteFile(int aPassCount) 
{
	CTILogger.info("Starting Routes with Repeaters Pass " + aPassCount + " file process...");
	getIMessageFrame().addOutput("Starting Routes with Repeaters Pass " + aPassCount + " file process...");
	
	String aFileName = getFullFileName(baseRouteFileName) + + aPassCount + ".txt";
	
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process

	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );

	com.cannontech.database.data.route.RouteBase route = null; 

	int addCount = 0;
		
	for( int i = 0; i < lines.size(); i++ )
	{
		CTILogger.info("RPTR_ROUTE line: " + lines.get(i).toString());		
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(lines.get(i).toString(), ",");
		
		if( tokenizer.countTokens() < (SINGLE_ROUTE_TOKEN_COUNT + 2) )
		{
			CTILogger.info("** Rpt Route line #" + (i+1) + " has less than " + (SINGLE_ROUTE_TOKEN_COUNT + 2) + " tokens, EXITING.");
			getIMessageFrame().addOutput("** Rpt Route line #" + (i+1) + " has less than " + (SINGLE_ROUTE_TOKEN_COUNT + 2) + " tokens, EXITING.");
			return false;
		}
			
		Integer routeID = new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim()) );
		
		String routeType = tokenizer.nextElement().toString().trim();
			
		route = com.cannontech.database.data.route.RouteFactory.createRoute( routeType );
		
		//set our unique own routeID
		route.setRouteID(routeID);

		route.setRouteName(tokenizer.nextElement().toString().trim());		

		// set CCU info
		handleRptRouteType( (( com.cannontech.database.data.route.CCURoute )route ), tokenizer);
		
		// make a vector for the repeaters
		java.util.Vector repeaterVector = new java.util.Vector();
	
		com.cannontech.database.db.route.RepeaterRoute RptRoute = null;

		// set RPT stuff
		for( int count = 0; count < aPassCount; count++ )
		{
			RptRoute = new com.cannontech.database.db.route.RepeaterRoute();
		
   			RptRoute.setRouteID(routeID);
   			
   			// set the DeviceID of the repeater first
   			RptRoute.setDeviceID( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim())) );
   
   			RptRoute.setVariableBits(new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim())) );
   			RptRoute.setRepeaterOrder(new Integer( count + 1 ) );

   			repeaterVector.addElement( RptRoute );
		}

		// stuff the repeaters into the CCU Route
		if( route instanceof com.cannontech.database.data.route.CCURoute)
		{
			((com.cannontech.database.data.route.CCURoute)route).setRepeaterVector(repeaterVector);
		}		
		multi.getDBPersistentVector().add( route );
		++addCount;
	}
	
	boolean success = writeToSQLDatabase(multi);

	if( success )
	{
		CTILogger.info(" Rpt Route file " + aPassCount + " processed and inserted Successfully");
		getIMessageFrame().addOutput("Rpt Route file " + aPassCount + " processed and inserted Successfully");
		
		CTILogger.info(" " + addCount + " Repeater Routes were added to the database");
		getIMessageFrame().addOutput(addCount + " Repeater Routes were added to the database");
	}
	else
		getIMessageFrame().addOutput(" Repeater Routes failed addition to the database");
	return success;

}


/**
 * Insert the method's description here.
 * Creation date: (4/21/2001 9:18:12 PM)
 * @return boolean
 */
public boolean processRTUDevices() 
{
	CTILogger.info("Starting RTU Device file process...");
	getIMessageFrame().addOutput("Starting RTU Device file process...");
	
	String aFileName = getFullFileName(RTUFileName);
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process

	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );	
	int addCount = 0;
		
	for( int i = 0; i < lines.size(); i++ )
	{
		CTILogger.info("RTU line: " + lines.get(i).toString());
		
		// DeviceId,DeviceType,DeviceName,PointID,Address,PostDelay,Phone#
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(lines.get(i).toString(), ",");
		if( tokenizer.countTokens() < RTU_TOKEN_COUNT )
		{
			CTILogger.info("** RTU line #" + i + " has less than " + TRANSMITTER_TOKEN_COUNT + " tokens, EXITING.");
			getIMessageFrame().addOutput("** RTU line #" + i + " has less than " + TRANSMITTER_TOKEN_COUNT + " tokens, EXITING.");
			return false;
		}
			
		Integer deviceID = new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim()) );
		String deviceType = tokenizer.nextElement().toString().trim();
		
		com.cannontech.database.data.device.RTUBase device = null;
		
		device = (com.cannontech.database.data.device.RTUBase)com.cannontech.database.data.device.DeviceFactory.createDevice( com.cannontech.database.data.pao.PAOGroups.getDeviceType( deviceType ) );

		//set our unique own deviceID
		device.setDeviceID(deviceID);

		// set the name of the RTU
		device.setPAOName( tokenizer.nextElement().toString().trim() );
		
		// add more code here
		((com.cannontech.database.data.device.IDLCBase)device).getDeviceDirectCommSettings().setPortID( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim()) ) );
		((com.cannontech.database.data.device.IDLCBase)device).getDeviceIDLCRemote().setAddress( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim()) ) );
		((com.cannontech.database.data.device.IDLCBase)device).getDeviceIDLCRemote().setPostCommWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim()) ) );

		// check if it is dialup
		if( tokenizer.hasMoreTokens() )
		{
			((com.cannontech.database.data.device.IDLCBase)device).getDeviceDialupSettings().setPhoneNumber( tokenizer.nextElement().toString().trim() );
			
			// Default values that do NOT get set by default??
			((com.cannontech.database.data.device.IDLCBase)device).getDeviceDialupSettings().setMaxConnectTime( new Integer(30) );
			((com.cannontech.database.data.device.IDLCBase)device).getDeviceDialupSettings().setMinConnectTime( new Integer(0) );
			((com.cannontech.database.data.device.IDLCBase)device).getDeviceDialupSettings().setLineSettings( "8N1" );		
		}
		multi.getDBPersistentVector().add( device );
	}

	boolean success = writeToSQLDatabase(multi);

	if( success )
	{
		CTILogger.info(" RTU file was processed and inserted Successfully");
		getIMessageFrame().addOutput(" RTU file was processed and inserted Successfully");
	}
	else
		getIMessageFrame().addOutput(" RTU Devices failed addition to the database");
		
	return success;
}


/**
 * Processes routes that do not have repeaters
 * Creation date: (4/14/2001 4:10:27 PM)
 * @return boolean
 */
public boolean processSingleRouteFile()
{
	CTILogger.info("Starting Single Route file process...");
	getIMessageFrame().addOutput("Starting Single Route file process...");
	
	String aFileName = getFullFileName(baseRouteFileName) + "0.txt";
	
	//aFileName.concat(".txt");
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process

	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );
		
	for( int i = 0; i < lines.size(); i++ )
	{
		CTILogger.info("ROUTE line: " + lines.get(i).toString());
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(lines.get(i).toString(), ",");
		if( tokenizer.countTokens() < SINGLE_ROUTE_TOKEN_COUNT )
		{
			CTILogger.info("** Route line #" + (i+1) + " has less than " + SINGLE_ROUTE_TOKEN_COUNT + " tokens, EXITING.");
			getIMessageFrame().addOutput("** Route line #" + (i+1) + " has less than " + SINGLE_ROUTE_TOKEN_COUNT + " tokens, EXITING.");
			return false;
		}
			
		Integer routeID = new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim()) );
		String routeType = tokenizer.nextElement().toString().trim();
		com.cannontech.database.data.route.RouteBase route = null;

		int routeInt = com.cannontech.database.data.pao.PAOGroups.getRouteType( routeType );	
			
		route = com.cannontech.database.data.route.RouteFactory.createRoute( com.cannontech.database.data.pao.PAOGroups.getRouteType( routeType ));

		//set our unique own routeID
		route.setRouteID(routeID);
		route.setRouteName(tokenizer.nextElement().toString().trim());
		route.setRouteType(routeType);
			
		switch(routeInt)
		{
			case com.cannontech.database.data.pao.PAOGroups.ROUTE_CCU:
			// handle CCU
				handleRouteType( ((com.cannontech.database.data.route.CCURoute)route), tokenizer);
				break;
				
			default:
				// private void handleRouteType(com.cannontech.database.data.route.CCURoute aRoute, java.util.StringTokenizer tokenizer) 

				((com.cannontech.database.data.route.RouteBase)route).setDeviceID( new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim())) );
				
				// not sure if this will work every	time???
				( (com.cannontech.database.data.route.RouteBase)route).setDefaultRoute( tokenizer.nextElement().toString().trim() );//guesswork again
				break;
		}
		multi.getDBPersistentVector().add( route );
	}

	boolean success = writeToSQLDatabase(multi);

	if( success )
	{
		CTILogger.info(" Single Route file processed and inserted Successfully");
		getIMessageFrame().addOutput(" Single Route file processed and inserted Successfully");
	}
	else
		getIMessageFrame().addOutput(" Single Route file failed insertion");
	return success;

}


/**
 * Insert the method's description here.
 * Creation date: (3/31/2001 11:43:17 AM)
 * @return boolean
 */
public boolean processStateGroupFile() 
{
	int addCount = 0;
	
	CTILogger.info("Starting StateGroup file process...");
	getIMessageFrame().addOutput("Starting StateGroup file process (" + statusPointFileName + ")...");
	
	String aFileName = getFullFileName(statusPointFileName);
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process
	
	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();

	for( int i = 0; i < lines.size(); i++ )
	{
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(lines.get(i).toString(), ",");

		//ignore title line
		if( i <= 2 || tokenizer.countTokens() < STATUS_PT_TOKEN_COUNT )
			continue;
		
		CTILogger.info("STATE_GROUP line: " + lines.get(i).toString());
		

		//skip first 4 tokens
		tokenizer.nextToken();tokenizer.nextToken();
		tokenizer.nextToken();tokenizer.nextToken();
		
		
		GroupStateMod stateGrpDB = new GroupStateMod( new Integer(START_STATEGRP_ID++) );
		
		stateGrpDB.getStateGroup().setName( "StateGroup #" + stateGrpDB.getStateGroup().getStateGroupID() );
		
		//create StateGroup if needed
		String state0 = tokenizer.nextToken().trim();
		String state1 = tokenizer.nextToken().trim();


		State stateDB0 = new State();
		stateDB0.setState(
			new com.cannontech.database.db.state.State(
				stateGrpDB.getStateGroup().getStateGroupID(),
				new Integer(0),
				state0,
				new Integer(com.cannontech.common.gui.util.Colors.GREEN_ID),
				new Integer(com.cannontech.common.gui.util.Colors.BLACK_ID),
				new Integer(YukonImage.NONE_IMAGE_ID)) );


		State stateDB1 = new State();
		stateDB1.setState(
			new com.cannontech.database.db.state.State(
				stateGrpDB.getStateGroup().getStateGroupID(),
				new Integer(1),
				state1,
				new Integer(com.cannontech.common.gui.util.Colors.RED_ID),
				new Integer(com.cannontech.common.gui.util.Colors.BLACK_ID),
				new Integer(YukonImage.NONE_IMAGE_ID)) );


		stateGrpDB.getStatesVector().add( stateDB0 );
		stateGrpDB.getStatesVector().add( stateDB1 );

		if( stateGrpMap.get(stateGrpDB) == null )
		{
			stateGrpMap.put( stateGrpDB, stateGrpDB);
			multi.getDBPersistentVector().add( stateGrpDB );
			addCount++;
		}
				
	}

	boolean success = writeToSQLDatabase(multi);

	if( success )
	{
		CTILogger.info(" Status Point file was processed and inserted Successfully");
		getIMessageFrame().addOutput(" Status Point file was processed and inserted Successfully");
		
		CTILogger.info(" " + addCount + " Status Points were added to the database");
		getIMessageFrame().addOutput(" " + addCount + " Status Points were added to the database");
	}
	else
		getIMessageFrame().addOutput(" Status Points failed addition to the database");
	return success;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2001 4:59:31 PM)
 * @return boolean
 */
public boolean processStatusPoints()
{
	CTILogger.info("Starting Status Point file process...");
	getIMessageFrame().addOutput("Starting Status Point file process (" + statusPointFileName + ")...");
	
	String aFileName = getFullFileName(statusPointFileName);
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process
	
	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();

	// if this is not set to false it will create its own PointIDs
	multi.setCreateNewPAOIDs( false );
		
	int addCount = 0;
		
	for( int i = 0; i < lines.size(); i++ )
	{
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(lines.get(i).toString(), ",");

		//ignore title line
		if( i <= 2 || tokenizer.countTokens() < STATUS_PT_TOKEN_COUNT )
			continue;
		
		CTILogger.info("STATUS_PT line: " + lines.get(i).toString());

		//skip first token
		tokenizer.nextToken();

		StatusPoint statusPoint = 
			(StatusPoint)PointFactory.createPoint( PointTypes.STATUS_POINT );

		statusPoint.setPointID( new Integer(START_PTID) );
		Integer deviceID = pInt(tokenizer.nextToken());
		statusPoint.getPoint().setPaoID( deviceID );

		Integer ptOffset = pInt(tokenizer.nextToken());		
		statusPoint.getPoint().setPointOffset(ptOffset);
		statusPoint.getPoint().setArchiveType("None");
		statusPoint.getPoint().setArchiveInterval( new Integer(0) );

				
		//skip token
		tokenizer.nextToken();
		
		//create StateGroup if needed
		String state0 = tokenizer.nextToken().trim();
		String state1 = tokenizer.nextToken().trim();


		GroupState grpState = null;
		Iterator it = stateGrpMap.values().iterator();
		while( it.hasNext() )
		{
			Object val = it.next();
			if( val.equals(new String[] {state0, state1}) )
			{
				grpState = (GroupState)val;
				break;
			}
		}

		statusPoint.getPoint().setStateGroupID( grpState.getStateGroup().getStateGroupID() );


		statusPoint.getPoint().setPointName( tokenizer.nextToken().trim() );


		// set default settings for BASE point
		statusPoint.getPoint().setServiceFlag(new Character('N'));
		statusPoint.getPoint().setAlarmInhibit(new Character('N'));

		// set default settings for point ALARMING
		statusPoint.getPointAlarming().setAlarmStates( PointAlarming.DEFAULT_ALARM_STATES );
		statusPoint.getPointAlarming().setExcludeNotifyStates( PointAlarming.DEFAULT_EXCLUDE_NOTIFY );
		statusPoint.getPointAlarming().setNotifyOnAcknowledge( new String("N") );
		statusPoint.getPointAlarming().setNotificationGroupID(  new Integer(PointAlarming.NONE_NOTIFICATIONID) );
	
		statusPoint.getPointStatus().setInitialState(new Integer(1));
		statusPoint.getPointStatus().setControlInhibit(new Character('N'));


		if( !statusPoint.getPoint().getPointName().toUpperCase().startsWith("FUTURE SPARE")
			 && deviceIDsMap.get(statusPoint.getPoint().getPaoID()) != null )
		{
			multi.getDBPersistentVector().add( statusPoint );
			++addCount;
			++START_PTID;
		}
	}

	boolean success = writeToSQLDatabase(multi);

	if( success )
	{
		CTILogger.info(" Status Point file was processed and inserted Successfully");
		getIMessageFrame().addOutput(" Status Point file was processed and inserted Successfully");
		
		CTILogger.info(" " + addCount + " Status Points were added to the database");
		getIMessageFrame().addOutput(" " + addCount + " Status Points were added to the database");
	}
	else
		getIMessageFrame().addOutput(" Status Points failed addition to the database");
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
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(lines.get(i).toString(), ",");

		//ignore title line
		if( i == 0 || tokenizer.countTokens() < TRANSMITTER_TOKEN_COUNT )
			continue;

		CTILogger.info("TRANSMITTER line: " + lines.get(i).toString());

		Integer deviceID = pInt(tokenizer.nextToken());
				
		String deviceType = DeviceTypes.STRING_SERIES_5_LMI[0];
		Series5LMI device = null;

		device = (Series5LMI)DeviceFactory.createDevice( com.cannontech.database.data.pao.PAOGroups.getDeviceType( deviceType ) );
		device.setDeviceID(deviceID);
		
		//replace all blanks with a single _
		device.setPAOName( tokenizer.nextToken().trim().replaceAll("([ ])+", "_") );

		device.getDeviceDirectCommSettings().setPortID( 
			new Integer(pInt(tokenizer.nextToken()).intValue() + PORTID_OFFSET) );

		device.getSeries5().setSlaveAddress( pInt(tokenizer.nextToken()) );
		
		device.getSeries5RTU().setStartCode( pInt(tokenizer.nextToken()) );
		device.getSeries5RTU().setStopCode( pInt(tokenizer.nextToken()) );
		device.getSeries5RTU().setTransmitOffset( pInt(tokenizer.nextToken()) );
		
		String dis = tokenizer.nextToken();
		device.setDisableFlag( new Character(
				(dis.equalsIgnoreCase("N") ? 'Y' : 'N')) );

		device.getSeries5RTU().setSaveHistory( tokenizer.nextToken().toUpperCase().trim() );

		device.getSeries5RTU().setPowerValueMultiplier( pDbl(tokenizer.nextToken()) );
		device.getSeries5RTU().setPowerValueOffset( pDbl(tokenizer.nextToken()) );
		
		//skip the next 4 tokens
		tokenizer.nextToken();tokenizer.nextToken();
		tokenizer.nextToken();tokenizer.nextToken();
		
		
		device.getSeries5RTU().setPowerValueHighLimit( pInt(tokenizer.nextToken()) );
		device.getSeries5RTU().setPowerValueLowLimit( pInt(tokenizer.nextToken()) );
		
		
		
		//create a route
		RouteBase route = null;
		route = RouteFactory.createRoute( RouteTypes.ROUTE_SERIES_5_LMI );

		route.setRouteID( new Integer(START_ROUTE_ID++) );
		route.setRouteName( device.getPAOName() + " Rt");
		route.setDeviceID( deviceID );
		route.setDefaultRoute("N");

		if( device.getDeviceDirectCommSettings().getPortID().intValue() > PORTID_OFFSET )
		{
			multi.getDBPersistentVector().add( device );
			multi.getDBPersistentVector().add( route );
			deviceIDsMap.put( device.getDevice().getDeviceID(), device.getDevice().getDeviceID() );
		}
		
	}

	//boolean success = true; //writeToSQLDatabase(multi);
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
 * Creation date: (4/13/2001 9:03:49 AM)
 * @return boolean
 */
public boolean processVirtualDeviceFile() 
{	
	CTILogger.info("Starting Virtual Device file process...");
	getIMessageFrame().addOutput("Starting Virtual Device file process...");
	
	String aFileName = getFullFileName(virtualDeviceFileName);
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process

	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );
		
	for( int i = 0; i < lines.size(); i++ )
	{
		CTILogger.info("VIRTUAL line: " + lines.get(i).toString());
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(lines.get(i).toString(), ",");
		if( tokenizer.countTokens() < VIRTUALDEV_TOKEN_COUNT )
		{
			CTILogger.info("** Virtual Deivce line #" + i + " has less than " + VIRTUALDEV_TOKEN_COUNT + " tokens, EXITING.");
			getIMessageFrame().addOutput("** Virtual Deivce line #" + i + " has less than " + VIRTUALDEV_TOKEN_COUNT + " tokens, EXITING.");
			return false;
		}
			
		Integer deviceID = new Integer( Integer.parseInt(tokenizer.nextElement().toString().trim()) );
		String deviceType = tokenizer.nextElement().toString().trim();
		
		com.cannontech.database.data.device.DeviceBase device = null;
		
		device = com.cannontech.database.data.device.DeviceFactory.createDevice( com.cannontech.database.data.pao.PAOGroups.getDeviceType( deviceType ) );
		
		//set our unique own deviceID
		device.setDeviceID(deviceID);
		device.setPAOName( tokenizer.nextElement().toString().trim() );
	
		multi.getDBPersistentVector().add( device );
	}

	boolean success = writeToSQLDatabase(multi);

	if( success )
	{
		CTILogger.info(" Virtual Device file processed and inserted Successfully");
		getIMessageFrame().addOutput(" Virtual Device file processed and inserted Successfully");
	}
	else
		getIMessageFrame().addOutput(" Virtual Device file failed insertion");
	return success;
	
}


/**
 * Insert the method's description here.
 * Creation date: (3/31/2001 11:31:42 AM)
 * @return boolean
 */
/* Returns an arraylist of strings that represents each line in the  */
/* file. If a problem occurs, null is returned  */
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