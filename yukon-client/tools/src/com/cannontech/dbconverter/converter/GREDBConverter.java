package com.cannontech.dbconverter.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.NativeIntVector;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.device.Series5LMI;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.database.data.device.lm.LMGroupGolay;
import com.cannontech.database.data.device.lm.LMProgramBase;
import com.cannontech.database.data.device.lm.LMProgramDirect;
import com.cannontech.database.data.device.lm.LMScenario;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOGroups;
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
import com.cannontech.database.db.device.lm.LMControlScenarioProgram;
import com.cannontech.database.db.device.lm.LMProgramConstraint;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGroup;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.database.db.state.YukonImage;
import com.cannontech.dbtools.updater.MessageFrameAdaptor;
import com.cannontech.tools.gui.*;

/**
 * Insert the type's description here.
 * 
 * 
 * 1) Create a Control Area for all ----
 *      select * from yukonpaobject where type = 'LM CONTROL AREA' 11
 *      insert into lmcontrolareaprogram select 11, deviceid, 0, 0, 0 from lmprogramdirect
 *
 * 2) Run Group point creation BAT file
 *
 *
 *
 *
 * @author: 
 */
public class GREDBConverter extends MessageFrameAdaptor 
{
	private HashMap stateGrpMap = new HashMap(32);
	private HashMap deviceIDsMap = new HashMap(32);
	private HashMap routeIDsMap = new HashMap(32);
	
	////example KEY<LMProgram> VALUES<LMProgram>
	private HashMap programMap = new HashMap(32);

	//example KEY<SW01> VALUES<SW - Anoka (Grp 1-2),SW - Anoka (Grp 1-3)>
	private HashMap grpStratMap = new HashMap(32);



	private int ROUTE_OFFSET = 500;
	private int START_ROUTE_ID = ROUTE_OFFSET + 1;

	private int GROUPID_OFFSET = 3000;
	private int START_GROUP_ID = GROUPID_OFFSET + 1;

	private int PROGRAMID_OFFSET = 5000;
	private int START_PROGRAM_ID = PROGRAMID_OFFSET + 1;
	
	private int ROUTE_MACRO_OFFSET = 1000;
	private int PORTID_OFFSET = 2000;

	private int START_PTID = 500;
	private int START_STATEGRP_ID = 100;
	private int START_SCENARIO_ID = 100;


	private String analogPointFileName = "cti-analog.txt";
	public static final int ANALOG_PT_TOKEN_COUNT = 11;	

	private String transmitterFileName = "cti-transmitter.txt";
	public static final int TRANSMITTER_TOKEN_COUNT = 17;
	
	private String statusPointFileName = "cti-status.txt";
	public static final int STATUS_PT_TOKEN_COUNT = 8;
	
	private String routeMacroFileName = "cti-users.txt";
	public static final int ROUTE_MACRO_TOKEN_COUNT = 3;

	private String routeEntriesFileName = "cti-usertransmitter.txt";
	public static final int ROUTE_ENTRIES_TOKEN_COUNT = 2;

	private String groupFileName = "cti-code_groups.txt";
	public static final int GROUP_TOKEN_COUNT = 13;

	private String progToGroupsFileName = "cti-programs_groups.txt";
	
	private String progConstTimeFileName = "cti-sttime.txt";
	private String progConstDateFileName = "cti-stdate.txt";
	private String progConstTypeFileName = "cti-sttype.txt";

	
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
//	boolean s = true;
	boolean s = processPortFile();

	if( s ) s = processTransmitterFile();

	if( s ) s = processStateGroupFile();
	if( s ) s = processStatusPoints();	
	if( s ) s = processAnalogPoints();
	
	if( s ) s = processRouteMacro();
	if( s ) s = processLoadGroups();

	//not done
	//if( s ) s = processLoadProgramConstraints();
	

	if( s ) s = processLoadPrograms();
	if( s ) s = processLoadScenarios();


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
 * Creation date: (6/11/2001 11:56:45 AM)
 * @return boolean
 */
public boolean processLoadPrograms() 
{	
	CTILogger.info("Starting Load Program file process...");
	getIMessageFrame().addOutput("Starting Load Program file process (" + progToGroupsFileName + ")...");

	
	String aFileName = getFullFileName(progToGroupsFileName);
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process
	
	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );
		
	for( int i = 0; i < lines.size(); i++ )
	{
		String[] line = lines.get(i).toString().split(",");

		//ignore title line
		if( i <= 2 || line.length <= 9 || line[6].length() <= 0 )
			continue;
		
		CTILogger.info("LOAD_PROGRAM line: " + lines.get(i).toString());


		LMProgramDirect lmProgram =
			(LMProgramDirect)LMFactory.createLoadManagement( DeviceTypes.LM_DIRECT_PROGRAM );

		lmProgram.setName( line[4].trim() );
		lmProgram.getProgram().setControlType( LMProgramBase.OPSTATE_AUTOMATIC );
		
		/* Create a default gear place holder */
		LMProgramDirectGear gear = LMProgramDirectGear.createGearFactory( LMProgramDirectGear.NO_CONTROL );
		gear.setGearName("Dummy Gear");
		lmProgram.getLmProgramDirectGearVector().add( gear );

		//set our unique own deviceID
		lmProgram.setPAObjectID( new Integer(START_PROGRAM_ID++) );

		
		String stratID = lmProgram.getPAOName();
		ArrayList grpList = (ArrayList)grpStratMap.get( stratID );
		for( int j = 0; grpList != null && j < grpList.size(); j++ )
		{
			LMGroupGolay golay = (LMGroupGolay)grpList.get(j);
			if( golay.getRouteID() == null )
				continue; //ignore if we have no route

			LMProgramDirectGroup dirGrp = new LMProgramDirectGroup();
			dirGrp.setLmGroupDeviceID( golay.getPAObjectID() );
			dirGrp.setDeviceID( lmProgram.getPAObjectID() );
			dirGrp.setGroupOrder( new Integer(lmProgram.getLmProgramStorageVector().size()+1) );
			lmProgram.getLmProgramStorageVector().add( dirGrp );
		}
		
		
		programMap.put( lmProgram.getPAOName(), lmProgram );
		multi.getDBPersistentVector().add( lmProgram );
		
		
	}


	//boolean success = true;
	boolean success = writeToSQLDatabase(multi);

	if( success )
	{
		CTILogger.info(" Load Program file was processed and inserted Successfully");
		getIMessageFrame().addOutput("Load Program file was processed and inserted Successfully");
		
		CTILogger.info(" " + multi.getDBPersistentVector().size() + " Load Program were added to the database");
		getIMessageFrame().addOutput(multi.getDBPersistentVector().size() + " Load Program were added to the database");
	}
	else
		getIMessageFrame().addOutput(" Load Program failed adding to the database");


	return success;
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2001 11:56:45 AM)
 * @return boolean
 */
public boolean processLoadScenarios() 
{	
	CTILogger.info("Starting Load Control Scenarios file process...");
	getIMessageFrame().addOutput("Starting Load Control Scenarios file process (" + progToGroupsFileName + ")...");

	//example KEY<SW:0000> VALUES<SW01,SW02...>
	HashMap scenarioMap = new HashMap(128);

	String aFileName = getFullFileName(progToGroupsFileName);
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process
	
	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );
		
	for( int i = 0; i < lines.size(); i++ )
	{
		String[] line = lines.get(i).toString().split(",");

		//ignore title line
		if( i <= 2 || line.length <= 9 || line[6].length() <= 0 )
			continue;
		
		CTILogger.info("LOAD_CONTROL_SCENARIO line: " + lines.get(i).toString());

		String scenName = line[0].trim();

		LMScenario lmScenario = null;
		if( !scenarioMap.containsKey(scenName) )
		{
			lmScenario = (LMScenario)LMFactory.createLoadManagement( PAOGroups.LM_SCENARIO );
			scenarioMap.put( scenName, lmScenario );
			multi.getDBPersistentVector().add( lmScenario );
		}
		else
			lmScenario = (LMScenario)scenarioMap.get( scenName );
		

		
		lmScenario.setScenarioName( scenName );
		LMProgramDirect prog = (LMProgramDirect)programMap.get( line[4].trim() );
		if( prog == null )
			continue;
		
		//set our unique own deviceID
		lmScenario.setScenarioID( new Integer(START_SCENARIO_ID++) );


		LMControlScenarioProgram progScen = new LMControlScenarioProgram();
		progScen.setScenarioID( lmScenario.getScenarioID() );
		progScen.setProgramID( prog.getPAObjectID() );
		progScen.setStartGear( new Integer(1) );
		lmScenario.getAllThePrograms().add( progScen );
	}


	boolean success = writeToSQLDatabase(multi);

	if( success )
	{
		CTILogger.info(" Load Control Scenarios file was processed and inserted Successfully");
		getIMessageFrame().addOutput("Load Control Scenarios file was processed and inserted Successfully");
		
		CTILogger.info(" " + multi.getDBPersistentVector().size() + " Load Control Scenarios were added to the database");
		getIMessageFrame().addOutput(multi.getDBPersistentVector().size() + " Load Control Scenarios were added to the database");
	}
	else
		getIMessageFrame().addOutput(" Load Control Scenarios failed adding to the database");


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
	getIMessageFrame().addOutput("Starting Load Group file process (" + groupFileName + ")...");
	
	String aFileName = getFullFileName(groupFileName);
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process
	
	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );
		
	for( int i = 0; i < lines.size(); i++ )
	{
		String[] line = lines.get(i).toString().split(",");

		//ignore title line
		if( i <= 2 || line.length <= 12 || line[2].length() <= 0 )
			continue;
		
		CTILogger.info("LOAD_GRP (GOLAY) line: " + lines.get(i).toString());


		LMGroupGolay lmGroupGolay = null;
		lmGroupGolay = (LMGroupGolay)LMFactory.createLoadManagement( DeviceTypes.LM_GROUP_GOLAY );
		
		//set our unique own deviceID
		lmGroupGolay.setDeviceID( new Integer(START_GROUP_ID++) );
		
		lmGroupGolay.getLMGroupSASimple().setOperationalAddress( line[2].trim() );
		
		String stratID = line[10].trim();
		if( grpStratMap.containsKey(stratID) )
		{
			ArrayList grpList = (ArrayList)grpStratMap.get(stratID);
			grpList.add( lmGroupGolay );
		}
		else
		{
			ArrayList grpList = new ArrayList(8);
			grpList.add( lmGroupGolay );
			grpStratMap.put( stratID, grpList );
		}
		
		//replace all blanks with a single _
		String name = line[12].trim();
		name = name.replaceAll("\"", ""); //remove double quotes
		name = name.replaceAll("\\.", ""); //remove periods
		
		lmGroupGolay.setPAOName(
			name.replaceAll("([ ])+", " ") + " " +
			lmGroupGolay.getLMGroupSASimple().getOperationalAddress() );


		//lmGroupGolay.getLMGroupSASimple().setNominalTimeout( new Character(tokenizer.nextElement().toString().trim().charAt(0)) );
		//lmGroupGolay.getLMGroupSASimple().setVirtualTimeout( new Integer(Integer.parseInt(tokenizer.nextElement().toString().trim())) );

		multi.getDBPersistentVector().add( lmGroupGolay );
	}



	/* Start reading through our Group to Route mapping file */		
	String prgToGrpFile = getFullFileName(progToGroupsFileName);
	java.util.ArrayList progToGrpLines = readFile(prgToGrpFile);
	for( int i = 0; i < progToGrpLines.size(); i++ )
	{
		String[] line = progToGrpLines.get(i).toString().split(",");

		//ignore title line
		if( i <= 2 || line.length <= 8 || line[4].length() <= 0 )
			continue;

		String key = line[4].trim();
		Integer routeID = 
			new Integer(pInt(line[8].trim()).intValue() + ROUTE_OFFSET);
		
		if( routeIDsMap.get(routeID) != null 
			&& grpStratMap.containsKey(key) )
		{
			ArrayList grpList = (ArrayList)grpStratMap.get(key);
			for( int j = 0; j < grpList.size(); j++ )
				((LMGroupGolay)grpList.get(j)).getLMGroupSASimple().setRouteID(
					new Integer(routeID.intValue()) );				
		}
	}


	for( int i = multi.getDBPersistentVector().size()-1; i >= 0; i-- )
	{
		if( ((LMGroupGolay)multi.getDBPersistentVector().get(i)).getLMGroupSASimple().getRouteID() == null )
		{
			CTILogger.info( "  No route found for Group: " +
				multi.getDBPersistentVector().get(i) +
				", group insertion canceled");

			multi.getDBPersistentVector().remove(i);
		}
	}


	//boolean success = true;
	boolean success = writeToSQLDatabase(multi);

	if( success )
	{
		CTILogger.info(" Load Group file was processed and inserted Successfully");
		getIMessageFrame().addOutput("Load Group file was processed and inserted Successfully");
		
		CTILogger.info(" " + multi.getDBPersistentVector().size() + " Load Groups were added to the database");
		getIMessageFrame().addOutput(multi.getDBPersistentVector().size() + " Load Groups were added to the database");
	}
	else
		getIMessageFrame().addOutput(" Load Groups failed adding to the database");


	return success;
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2001 11:56:45 AM)
 * @return boolean
 */
public boolean processLoadProgramConstraints() 
{	
	CTILogger.info("Starting Load Program Constraints file process...");
	getIMessageFrame().addOutput("Starting Load Program Constraints file process (" + progConstTimeFileName + ")...");

	String aFileName = getFullFileName(progConstTimeFileName);
	java.util.ArrayList lines = readFile(aFileName);

	//example KEY<String strategyID> VALUES<LMProgramConstraint)>
	HashMap constMapByName = new HashMap(64);
	
	//example KEY<String typeID> VALUES<List(LMProgramConstraint)>
	HashMap constMapByType = new HashMap(64);

	if( lines == null )
		return true; //continue the process
	
	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );
		
	for( int i = 0; i < lines.size(); i++ )
	{
		String[] line = lines.get(i).toString().split(",");

		//ignore title line
		if( i <= 2 || line.length <= 10 || line[0].length() <= 0 )
			continue;
		
		CTILogger.info("LOAD_PROG_TIME_CONSTRAINTS line: " + lines.get(i).toString());


		LMProgramConstraint lmProgConst = new LMProgramConstraint();		
		lmProgConst.setConstraintName( line[0].trim() );

		StringBuffer day = new StringBuffer("NNNNNNNN");
		day.setCharAt( 0, line[3].trim().charAt(0) );
		day.setCharAt( 1, line[4].trim().charAt(0) );
		day.setCharAt( 2, line[5].trim().charAt(0) );
		day.setCharAt( 3, line[6].trim().charAt(0) );
		day.setCharAt( 4, line[7].trim().charAt(0) );
		day.setCharAt( 5, line[8].trim().charAt(0) );
		day.setCharAt( 6, line[9].trim().charAt(0) );
		day.setCharAt( 7, line[10].trim().charAt(0) );		
		lmProgConst.setAvailableWeekdays( day.toString() );
		
		//multi.getDBPersistentVector().add( lmProgConst );
		constMapByName.put( lmProgConst.getConstraintName(), lmProgConst );
	}



	/* Start reading through our Constraint to Date mapping file */		
	String constDateFile = getFullFileName(progConstDateFileName);
	java.util.ArrayList dateLines = readFile(constDateFile);
	for( int i = 0; i < dateLines.size(); i++ )
	{
		String[] line = dateLines.get(i).toString().split(",");

		//ignore title line
		if( i <= 2 || line.length <= 3 || line[0].length() <= 0 )
			continue;

		String key = line[0].trim();
		String typeID = line[3].trim();

		if( constMapByName.containsKey(key) ) 
		{
			LMProgramConstraint progConst = 
					(LMProgramConstraint)constMapByName.get( key );

			if( constMapByType.containsKey(typeID) )
			{
				ArrayList typeList = (ArrayList)constMapByType.get(typeID);
				typeList.add( progConst );
			}
			else
			{
				ArrayList typeList = new ArrayList(8);
				typeList.add( progConst );
				constMapByType.put( typeID, progConst );
			}
		}


	}


	/* Start reading through our Group to Route mapping file */		
	String constTypeFile = getFullFileName(progConstTypeFileName);
	java.util.ArrayList typeLines = readFile(constTypeFile);
	for( int i = 0; i < typeLines.size(); i++ )
	{
		String[] line = typeLines.get(i).toString().split(",");

		//ignore title line
		if( i <= 2 || line.length <= 3 || line[0].length() <= 0 )
			continue;

		String typeID = line[0].trim();

		ArrayList typeList = (ArrayList)constMapByType.get(typeID);
		for( int j = 0; j < typeList.size(); j++ )
		{
			LMProgramConstraint constr =
				(LMProgramConstraint)typeList.get(j);


			//convert minutes to seconds
			constr.setMaxActivateTime(
				new Integer(pInt(line[8].trim()).intValue() * 60) );
			
			//convert minutes to seconds
			constr.setMaxDailyOps(
				new Integer(pInt(line[9].trim()).intValue() * 60) );

			/*
				lmProgConst.setMinRestartTime()
				lmProgConst.setMinActivateTime()
	
				lmProgConst.setMaxHoursSeasonal()
				lmProgConst.setMaxHoursMonthly()
				lmProgConst.setMaxHoursDaily()

				lmProgConst.setMaxActivateTime()
	
				lmProgConst.setAvailableSeasons()
			*/
//					line[10].trim().
			
		}
		

	}





	for( int i = multi.getDBPersistentVector().size()-1; i >= 0; i-- )
	{
		if( ((LMGroupGolay)multi.getDBPersistentVector().get(i)).getLMGroupSASimple().getRouteID() == null )
		{
			CTILogger.info( "  No route found for Group: " +
				multi.getDBPersistentVector().get(i) + ", group insertion canceled");

			multi.getDBPersistentVector().remove(i);
		}
	}


	boolean success = writeToSQLDatabase(multi);

	if( success )
	{
		CTILogger.info(" Load Group file was processed and inserted Successfully");
		getIMessageFrame().addOutput("Load Group file was processed and inserted Successfully");
		
		CTILogger.info(" " + multi.getDBPersistentVector().size() + " Load Groups were added to the database");
		getIMessageFrame().addOutput(multi.getDBPersistentVector().size() + " Load Groups were added to the database");
	}
	else
		getIMessageFrame().addOutput(" Load Groups failed adding to the database");


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
		
		CTILogger.info("PORT (Local Serial Port) line: " + lines.get(i).toString());

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
 * Creation date: (4/20/2001 7:01:33 PM)
 * @return boolean
 */
public boolean processRouteMacro() 
{
	CTILogger.info("Starting Route Macro file process...");
	getIMessageFrame().addOutput("Starting Route Macro file process (" + routeMacroFileName + ")...");
	
	String aFileName = getFullFileName(routeMacroFileName);
	
	java.util.ArrayList lines = readFile(aFileName);

	String rtEntriesFile = getFullFileName(routeEntriesFileName);	
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

		CTILogger.info("TRANSMITTER (Series 5 LMI) line: " + lines.get(i).toString());

		Integer deviceID = pInt(tokenizer.nextToken());
				
		String deviceType = DeviceTypes.STRING_SERIES_5_LMI[0];
		Series5LMI device = null;

		device = (Series5LMI)DeviceFactory.createDevice( com.cannontech.database.data.pao.PAOGroups.getDeviceType( deviceType ) );
		device.setDeviceID(deviceID);
		
		//replace all blanks with a single _
		String name = tokenizer.nextToken().trim().replaceAll("([ ])+", " ");
		device.setPAOName( name.substring(2, name.length() - 2) );

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
			routeIDsMap.put( route.getRouteID(), route.getRouteID() );
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