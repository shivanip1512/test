package com.cannontech.dbconverter.converter;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.device.MCTIEDBase;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.dbtools.updater.MessageFrameAdaptor;
import com.cannontech.tools.gui.*;

/**
 * Insert the type's description here.
 * Creation date: (1/10/2001 11:18:45 PM)
 * @author: 
 */
public class DBConverter extends MessageFrameAdaptor 
{
	private static final int MAX_DSM2_MACRO_COUNT = 30;
	
	private String stateGroupFileName = "stategrp.txt";
	public static final int STATE_TOKEN_COUNT = 3;
		
	private String portFileName = "port.txt";
	public static final int PORT_TOKEN_COUNT = 12;
	
	private String transmitterFileName = "trxmiter.txt";
	public static final int TRANSMITTER_TOKEN_COUNT = 6;

	private String virtualDeviceFileName = "pseudo.txt";
	public static final int VIRTUALDEV_TOKEN_COUNT = 3;

	private String baseRouteFileName = "route";
	public static final int SINGLE_ROUTE_TOKEN_COUNT = 5;

	private String baseRepeaterFileName = "rptdev";
	public static final int REPEATER_TOKEN_COUNT = 5;

	private String RouteMacroFileName = "routemac.txt";
	public static final int ROUTE_MACRO_TOKEN_COUNT = 4;

	private String CBCFileName = "cbc.txt";
	public static final int CBC_TOKEN_COUNT = 5;

	private String LMGroupFileName = "lmgroup.txt";
	public static final int LMGROUP_TOKEN_COUNT = 6;
	
	private String MCTFileName = "mct.txt";
	public static final int MCT_TOKEN_COUNT = 7;

	private String RTUFileName = "rtus.txt";
	public static final int RTU_TOKEN_COUNT = 5;

	private String StatusPointFileName = "ptstatus.txt";
	public static final int STATUS_PT_TOKEN_COUNT = 13;

	private String AnalogPointFileName = "ptanalog.txt";
	public static final int ANALOG_PT_TOKEN_COUNT = 13;	
	
	private String AccumPointFileName = "ptaccum.txt";
	public static final int ACCUM_PT_TOKEN_COUNT = 13;	

/**
 * DBConverter constructor comment.
 */
public DBConverter() 
{
	super();
}

public String getName()
{
	return "DSM2/Converter";
}

public String getParamText()
{
	return "Src-Directory:";
}


public void run()
{
	boolean s = processStateGroupFile();
	if( s ) s = processPortFile();
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
private void handleLocalDirectPort( com.cannontech.database.data.port.LocalDirectPort port, java.util.StringTokenizer tokenizer)
{
	//not sure if this will work every	time???
	port.getPortLocalSerial().setPhysicalPort( tokenizer.nextElement().toString() );
		
	port.setPortName( tokenizer.nextElement().toString() );
	
	port.getPortSettings().setBaudRate( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
	port.getPortSettings().setLineSettings( "8N1" );
	port.getCommPort().setCommonProtocol( tokenizer.nextElement().toString() );

	// create a new PortTimeing object so we use the next tokens and possibly set
	//  the LocalSharedPorts values correctly
	com.cannontech.database.db.port.PortTiming timing = new com.cannontech.database.db.port.PortTiming();
	timing.setPreTxWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
	timing.setRtsToTxWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
	timing.setPostTxWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
	timing.setReceiveDataWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
	timing.setExtraTimeOut( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
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
			com.cannontech.common.util.CtiUtilities.STRING_NONE);
		
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
	port.getPortSettings().setCdWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
}


/**
 * Insert the method's description here.
 * Creation date: (4/14/2001 5:15:46 PM)
 */
private void handleRouteType(com.cannontech.database.data.route.CCURoute aRoute, java.util.StringTokenizer tokenizer) 
{
	aRoute.setDeviceID( new Integer( Integer.parseInt(tokenizer.nextElement().toString())) );//

	aRoute.setDefaultRoute(tokenizer.nextElement().toString() );//		
	
	aRoute.getCarrierRoute().setCcuFixBits( new Integer( Integer.parseInt(tokenizer.nextElement().toString())) );
	aRoute.getCarrierRoute().setCcuVariableBits( new Integer( Integer.parseInt(tokenizer.nextElement().toString())) );	

	aRoute.getCarrierRoute().setBusNumber( new Integer( Integer.parseInt(tokenizer.nextElement().toString())) );
}


/**
 * Insert the method's description here.
 * Creation date: (4/14/2001 5:15:46 PM)
 */
private void handleRptRouteType(com.cannontech.database.data.route.CCURoute aRoute, java.util.StringTokenizer tokenizer) 
{
	aRoute.setDeviceID( new Integer( Integer.parseInt(tokenizer.nextElement().toString())) );//
	aRoute.setDefaultRoute(tokenizer.nextElement().toString() );//		
	
	aRoute.getCarrierRoute().setCcuFixBits( new Integer( Integer.parseInt(tokenizer.nextElement().toString())) );
	aRoute.getCarrierRoute().setCcuVariableBits( new Integer( Integer.parseInt(tokenizer.nextElement().toString())) );	

	aRoute.getCarrierRoute().setBusNumber( new Integer( Integer.parseInt(tokenizer.nextElement().toString())) );

	// advance token to next because Amp does not exit anymore in the route
	tokenizer.nextElement().toString();
	
}


/**
 * Insert the method's description here.
 * Creation date: (3/31/2001 12:57:46 PM)
 */
private void handleTerminalPort( com.cannontech.database.data.port.LocalDirectPort port, java.util.StringTokenizer tokenizer)
{
	//not sure if this will work every	time???
	port.getPortLocalSerial().setPhysicalPort( tokenizer.nextElement().toString() );
	
	port.setPortName( tokenizer.nextElement().toString() );

	port.getPortSettings().setBaudRate( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
	port.getPortSettings().setLineSettings( "8N1" );
	port.getCommPort().setCommonProtocol( tokenizer.nextElement().toString() );

	// create a new PortTimeing object so we use the next tokens and possibly set
	//  the LocalSharedPorts values correctly
	com.cannontech.database.db.port.PortTiming timing = new com.cannontech.database.db.port.PortTiming();
	timing.setPreTxWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
	timing.setRtsToTxWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
	timing.setPostTxWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
	timing.setReceiveDataWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
	timing.setExtraTimeOut( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
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
	port.getPortSettings().setCdWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
}


/**
 * Insert the method's description here.
 * Creation date: (3/31/2001 12:57:46 PM)
 */
private void handleTerminalPort( com.cannontech.database.data.port.TerminalServerDirectPort port, java.util.StringTokenizer tokenizer)
{
	String myTempIpPort = new String(tokenizer.nextElement().toString());
	
	// IP and port is in format --> xx.xx.xx.xx:1000
	java.util.StringTokenizer myIPPorttoken = new java.util.StringTokenizer(myTempIpPort, ":");
	
	port.getPortTerminalServer().setIpAddress( myIPPorttoken.nextElement().toString() );
	
	port.getPortTerminalServer().setSocketPortNumber( new Integer(Integer.parseInt(myIPPorttoken.nextElement().toString())) );
	
	port.setPortName( tokenizer.nextElement().toString() );

	port.getPortSettings().setBaudRate( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
	port.getPortSettings().setLineSettings( "8N1" );
	port.getCommPort().setCommonProtocol( tokenizer.nextElement().toString() );

	// create a new PortTimeing object so we use the next tokens and possibly set
	//  the TerminalServerSharedPort values correctly
	com.cannontech.database.db.port.PortTiming timing = new com.cannontech.database.db.port.PortTiming();
	timing.setPreTxWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
	timing.setRtsToTxWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
	timing.setPostTxWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
	timing.setReceiveDataWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
	timing.setExtraTimeOut( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
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
	port.getPortSettings().setCdWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
}


/**
 * Insert the method's description here.
 * Creation date: (4/1/2001 4:09:36 PM)
 * @param device com.cannontech.database.data.device.CCUBase
 * @param tokenizer java.util.StringTokenizer
 */
private void handleTransmitter(com.cannontech.database.data.device.TwoWayDevice device, java.util.StringTokenizer tokenizer) 
{

	device.setPAOName( tokenizer.nextElement().toString() );
	
	if( device instanceof com.cannontech.database.data.device.IDLCBase )
	{
		/* For CCU710A, CCU711, TCU5000, TCU5500, LCU415, LCULG */
		((com.cannontech.database.data.device.IDLCBase)device).getDeviceDirectCommSettings().setPortID( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
		((com.cannontech.database.data.device.IDLCBase)device).getDeviceIDLCRemote().setAddress( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
		((com.cannontech.database.data.device.IDLCBase)device).getDeviceIDLCRemote().setPostCommWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );

		if( device instanceof com.cannontech.database.data.device.CCU710A )
		{
			// set to amp for for 710 or 700
			((com.cannontech.database.data.device.IDLCBase)device).getDeviceIDLCRemote().setCcuAmpUseType(new String ("Amp 1"));
		}

		
		if( tokenizer.hasMoreTokens() )
		{
			((com.cannontech.database.data.device.IDLCBase)device).getDeviceDialupSettings().setPhoneNumber( tokenizer.nextElement().toString() );
			
			// Default values that do NOT get set by default??
			((com.cannontech.database.data.device.IDLCBase)device).getDeviceDialupSettings().setMaxConnectTime( new Integer(30) );
			((com.cannontech.database.data.device.IDLCBase)device).getDeviceDialupSettings().setMinConnectTime( new Integer(0) );
			((com.cannontech.database.data.device.IDLCBase)device).getDeviceDialupSettings().setLineSettings( "8N1" );		
		}
	}
	else if( device instanceof com.cannontech.database.data.device.PagingTapTerminal )
	{
		/* For TAPTERMINAL */
		((com.cannontech.database.data.device.PagingTapTerminal)device).getDeviceDirectCommSettings().setPortID( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );

		// Actually the Address for tap terms are the Pager Phone Numbers
		((com.cannontech.database.data.device.PagingTapTerminal)device).getDeviceTapPagingSettings().setPagerNumber( tokenizer.nextElement().toString() );

		
		//just remove the transmitter PostCommWait token and do nothing with it
		tokenizer.nextElement();
	
		if( tokenizer.hasMoreTokens() )
		{
			((com.cannontech.database.data.device.IEDBase)device).getDeviceDialupSettings().setPhoneNumber( tokenizer.nextElement().toString() );
			
			// Default values that do NOT get set by default??
			((com.cannontech.database.data.device.PagingTapTerminal)device).getDeviceDialupSettings().setMaxConnectTime( new Integer(30) );
			((com.cannontech.database.data.device.PagingTapTerminal)device).getDeviceDialupSettings().setMinConnectTime( new Integer(0) );
			((com.cannontech.database.data.device.PagingTapTerminal)device).getDeviceDialupSettings().setLineSettings( "8N1" );		
		}
		
		// set some Tap term defaults  --> getDeviceIED()
		((com.cannontech.database.data.device.IEDBase)device).getDeviceIED().setPassword( "None" );
		((com.cannontech.database.data.device.IEDBase)device).getDeviceIED().setSlaveAddress( "Master" );
	}

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
	}
	else
	{
		// default path name
		filePathName = new String("/yukon/client/export/");
	}
	
	CTILogger.info("Import File Path:" + filePathName);

	System.setProperty( IRunnableDBTool.PROP_VALUE, 
			filePathName + IRunnableDBTool.FS );

	DBConverter converter = new DBConverter();
	converter.run();
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
	com.cannontech.database.data.multi.MultiDBPersistent multi = new com.cannontech.database.data.multi.MultiDBPersistent();
	
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
	    
		Integer pointID = new Integer( Integer.parseInt(tokenizer.nextElement().toString()) );
		
		String pointType = tokenizer.nextElement().toString();
		
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
		
	    accumPoint.getPoint().setPointName( tokenizer.nextElement().toString() );

		Integer deviceID = new Integer(Integer.parseInt(tokenizer.nextElement().toString()));

		accumPoint.getPoint().setPaoID(deviceID);
		
		// advance token but this is not used
		tokenizer.nextElement();	//pseudoflag?
		
		accumPoint.getPoint().setPointOffset(new Integer( Integer.parseInt(tokenizer.nextElement().toString())));	
		
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
		String comparer = new String( tokenizer.nextElement().toString() );

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
		
		accumPoint.getPointAccumulator().setMultiplier(new Double(tokenizer.nextElement().toString()));
		accumPoint.getPointAccumulator().setDataOffset(new Double(tokenizer.nextElement().toString()));
			
		// make a vector for the point limits
		java.util.Vector pointLimitVector = new java.util.Vector();
	
		int limitCount = 0;
		com.cannontech.database.db.point.PointLimit myPointLimit = null;

		// two set of limits
		for(int count = 0; count < 2; count++)
		{
			String myLimitName = tokenizer.nextElement().toString();

			Double myHighLimit = new Double( tokenizer.nextElement().toString() );
			Double myLowLimit = new Double( tokenizer.nextElement().toString() );

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
		accumPoint.getPoint().setArchiveType(tokenizer.nextElement().toString());
		accumPoint.getPoint().setArchiveInterval( new Integer( Integer.parseInt(tokenizer.nextElement().toString())) );
			
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
	getIMessageFrame().addOutput("Starting Analog Point file process...");
	
	String aFileName = getFullFileName(AnalogPointFileName);
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process


	//create an object to hold all of our DBPersistant objects
	com.cannontech.database.data.multi.MultiDBPersistent multi = new com.cannontech.database.data.multi.MultiDBPersistent();

	// if this is not set to false it will create its own PointIDs
	multi.setCreateNewPAOIDs( false );
	
	int addCount = 0;
		
	for( int i = 0; i < lines.size(); i++ )
	{
		CTILogger.info("ANALOG_PT line: " + lines.get(i).toString());
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(lines.get(i).toString(), ",");
		if( tokenizer.countTokens() < ANALOG_PT_TOKEN_COUNT )
		{
			CTILogger.info("** Analog Point line #" + i + " has less than " + ANALOG_PT_TOKEN_COUNT + " tokens, EXITING.");
			getIMessageFrame().addOutput("** Analog Point line #" + i + " has less than " + ANALOG_PT_TOKEN_COUNT + " tokens, EXITING.");
			return false;
		}

		// PointID,PointType,PointName,DeviceId,Offset,UOM_Text,Multiplier,Offset,DeadBand,Limits,hi,low,warning,high,low,Archivetype,ArchInterval	
		//2541,Analog,5285_05_KVAR_T,126,Y,0,KVAR  ,1,0,-1,,0,0,,0,0,none,1
		Integer pointID = new Integer( Integer.parseInt(tokenizer.nextElement().toString()) );
		
		String pointType = tokenizer.nextElement().toString();

		com.cannontech.database.data.point.ScalarPoint analogPoint = null;
			
		//if pointType
		if (pointType.equals(new String("CalcAnalog")))
		{
			// This is a Caclulated Analog point
			analogPoint = new com.cannontech.database.data.point.CalculatedPoint();
		
			// default state group ID
			analogPoint.getPoint().setStateGroupID( new Integer(-3) );
		}
		else
		{
			analogPoint = new com.cannontech.database.data.point.AnalogPoint();
			
			// default state group ID
			analogPoint.getPoint().setStateGroupID( new Integer(-1) );
		}
				
		//set our unique deviceID
		analogPoint.setPointID(pointID);
		
		analogPoint.getPoint().setPointType(pointType);
	    analogPoint.getPoint().setPointName( tokenizer.nextElement().toString() );

		Integer deviceID = new Integer( Integer.parseInt(tokenizer.nextElement().toString()) );
		
		analogPoint.getPoint().setPaoID( deviceID );

		Character wastedToken = new Character(tokenizer.nextElement().toString().charAt(0));
		//analogPoint.getPoint().setPseudoFlag(new Character(tokenizer.nextElement().toString().charAt(0)));
		analogPoint.getPoint().setPointOffset(new Integer( Integer.parseInt(tokenizer.nextElement().toString())));	

		// set default settings for BASE point
		analogPoint.getPoint().setServiceFlag(new Character('N'));
		analogPoint.getPoint().setAlarmInhibit(new Character('N'));

		// set default settings for point ALARMING
		analogPoint.getPointAlarming().setAlarmStates( PointAlarming.DEFAULT_ALARM_STATES );
		analogPoint.getPointAlarming().setExcludeNotifyStates( PointAlarming.DEFAULT_EXCLUDE_NOTIFY );
		analogPoint.getPointAlarming().setNotifyOnAcknowledge( new String("N") );
		analogPoint.getPointAlarming().setNotificationGroupID(  new Integer(PointAlarming.NONE_NOTIFICATIONID) );

		// set Point Units
		analogPoint.getPointUnit().setDecimalPlaces(new Integer(2));

		String comparer = new String( tokenizer.nextElement().toString() );

		if( comparer.compareTo( "kVA" ) == 0 )
			analogPoint.getPointUnit().setUomID( new Integer( 2) );
		else if( comparer.compareTo( "kVAr" ) == 0 )
			analogPoint.getPointUnit().setUomID( new Integer( 3 ) );
		else if( comparer.compareTo( "kVAh" ) == 0 )
			analogPoint.getPointUnit().setUomID( new Integer (4) );
		else if( comparer.compareTo( "kVArh" ) == 0 )
			analogPoint.getPointUnit().setUomID( new Integer( 5 ) );
		else if( comparer.compareTo( "kVAr" ) == 0 )
			analogPoint.getPointUnit().setUomID( new Integer( 3 ) );
		else if( comparer.compareTo( "kWh" ) == 0 )
			analogPoint.getPointUnit().setUomID( new Integer( 1 ) );
		else if( comparer.compareTo( "MW" ) == 0 )
			analogPoint.getPointUnit().setUomID( new Integer( 20 ) );
		else if( comparer.compareTo( "MWh" ) == 0 )
			analogPoint.getPointUnit().setUomID( new Integer( 21 ) );
		else if( comparer.compareTo( "kQ" ) == 0 )
			analogPoint.getPointUnit().setUomID( new Integer( 7 ) );
		else if( comparer.compareTo( "PF" ) == 0 )
			analogPoint.getPointUnit().setUomID( new Integer( 27 ) );
		else if( comparer.compareTo( "VOLTS" ) == 0 )
			analogPoint.getPointUnit().setUomID( new Integer( 35 ) );
		else 
			analogPoint.getPointUnit().setUomID( new Integer( 0  ) );
			
		if (pointType.equals(new String("CalcAnalog")))
		{
			// move the token up 3
			tokenizer.nextElement().toString();
			tokenizer.nextElement().toString();
			tokenizer.nextElement().toString();
			
			((com.cannontech.database.data.point.CalculatedPoint)analogPoint).getCalcBase().setUpdateType(new String("On All Change") );
			((com.cannontech.database.data.point.CalculatedPoint)analogPoint).getCalcBase().setPeriodicRate(new Integer(1) );
		}
		else
		{
			// analog points have these values
			((com.cannontech.database.data.point.AnalogPoint)analogPoint).getPointAnalog().setMultiplier(new Double(tokenizer.nextElement().toString()));
			((com.cannontech.database.data.point.AnalogPoint)analogPoint).getPointAnalog().setDataOffset(new Double(tokenizer.nextElement().toString()));
			((com.cannontech.database.data.point.AnalogPoint)analogPoint).getPointAnalog().setDeadband(new Double(tokenizer.nextElement().toString()));
			((com.cannontech.database.data.point.AnalogPoint)analogPoint).getPointAnalog().setTransducerType( new String("none") );
		}
			
		// make a vector for the repeaters
		java.util.Vector pointLimitVector = new java.util.Vector();
	
		
		int limitCount = 0;
		com.cannontech.database.db.point.PointLimit myPointLimit = null;

		// set RPT stuff
		for(int count = 0; count < 2; count++)
		{

			String myLimitName = tokenizer.nextElement().toString();

			//private Integer limitNumber = null;
			Double myHighLimit = new Double( tokenizer.nextElement().toString() );
			Double myLowLimit = new Double( tokenizer.nextElement().toString() );

			if (!myLimitName.equals( new String("None") ))
			{
				++limitCount;
				myPointLimit = new com.cannontech.database.db.point.PointLimit();
		
				myPointLimit.setPointID(pointID);
//				myPointLimit.setLimitName(myLimitName);
				myPointLimit.setLowLimit(myLowLimit);
				myPointLimit.setHighLimit(myHighLimit);

//				myPointLimit.setDataFilterType(new String("None"));
				myPointLimit.setLimitDuration( new Integer(0) );
				myPointLimit.setLimitNumber( new Integer(limitCount) );
	
 	  			pointLimitVector.addElement( myPointLimit );

	   			myPointLimit = null;
			}
		}

		if (limitCount > 0)
		{
			// stuff the repeaters into the CCU Route
			analogPoint.setPointLimitsVector(pointLimitVector);
		}
		
		//archiving settings
		analogPoint.getPoint().setArchiveType(tokenizer.nextElement().toString());
		analogPoint.getPoint().setArchiveInterval( new Integer( Integer.parseInt(tokenizer.nextElement().toString())) );
			
		multi.getDBPersistentVector().add( analogPoint );
		
		++addCount;
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
	com.cannontech.database.data.multi.MultiDBPersistent multi = new com.cannontech.database.data.multi.MultiDBPersistent();
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
			
		Integer deviceID = new Integer( Integer.parseInt(tokenizer.nextElement().toString()) );
		String deviceType = tokenizer.nextElement().toString();
		
		com.cannontech.database.data.capcontrol.CapControlDeviceBase cbcDevice = null;


		cbcDevice = (com.cannontech.database.data.capcontrol.CapControlDeviceBase)com.cannontech.database.data.device.DeviceFactory.createDevice( com.cannontech.database.data.pao.PAOGroups.getDeviceType( deviceType ) );
		
		//set our unique own deviceID
		cbcDevice.setDeviceID(deviceID);
		
		cbcDevice.setPAOName( tokenizer.nextElement().toString() );
 
	    ((com.cannontech.database.data.capcontrol.CapBankController)cbcDevice).getDeviceCBC().setSerialNumber( new Integer(Integer.parseInt(tokenizer.nextElement().toString())) );
	    ((com.cannontech.database.data.capcontrol.CapBankController)cbcDevice).getDeviceCBC().setRouteID( new Integer(Integer.parseInt(tokenizer.nextElement().toString())) );
	    	
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
	com.cannontech.database.data.multi.MultiDBPersistent multi = new com.cannontech.database.data.multi.MultiDBPersistent();
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

		Integer deviceID = new Integer( Integer.parseInt(tokenizer.nextElement().toString()) );
		String deviceType = tokenizer.nextElement().toString();
		
		com.cannontech.database.data.device.lm.LMGroup lmGroupDevice = null;

		lmGroupDevice = (com.cannontech.database.data.device.lm.LMGroup)com.cannontech.database.data.device.lm.LMFactory.createLoadManagement(com.cannontech.database.data.pao.PAOGroups.getDeviceType( deviceType ) );
		
		//set our unique own deviceID
		lmGroupDevice.setDeviceID(deviceID);
		
	    lmGroupDevice.setPAOName( tokenizer.nextElement().toString() );
	    
		if (deviceType.equals(new String("EMETCON GROUP")))
		{
			// This is an Emetcon Group
			((com.cannontech.database.data.device.lm.LMGroupEmetcon)lmGroupDevice).getLmGroupEmetcon().setRouteID( new Integer(Integer.parseInt(tokenizer.nextElement().toString())) );
			((com.cannontech.database.data.device.lm.LMGroupEmetcon)lmGroupDevice).getLmGroupEmetcon().setRelayUsage( new Character(tokenizer.nextElement().toString().charAt(0)) );
			((com.cannontech.database.data.device.lm.LMGroupEmetcon)lmGroupDevice).getLmGroupEmetcon().setAddressUsage( new Character(tokenizer.nextElement().toString().charAt(0)) );
			((com.cannontech.database.data.device.lm.LMGroupEmetcon)lmGroupDevice).getLmGroupEmetcon().setSilverAddress( new Integer(Integer.parseInt(tokenizer.nextElement().toString())) );
			((com.cannontech.database.data.device.lm.LMGroupEmetcon)lmGroupDevice).getLmGroupEmetcon().setGoldAddress( new Integer(Integer.parseInt(tokenizer.nextElement().toString())) );
		}
		else if (deviceType.equals(new String("RIPPLE GROUP")))
		{
			// Fields:
			// DeviceId,GroupType,GroupName,routeID,ShedMinutes,ControlMessage,RestoreMessage
			
			// This is an Ripple Group
			((com.cannontech.database.data.device.lm.LMGroupRipple)lmGroupDevice).getLmGroupRipple().setRouteID( new Integer(Integer.parseInt(tokenizer.nextElement().toString())) );

			((com.cannontech.database.data.device.lm.LMGroupRipple)lmGroupDevice).getLmGroupRipple().setShedTime( new Integer(Integer.parseInt(tokenizer.nextElement().toString())) );

			((com.cannontech.database.data.device.lm.LMGroupRipple)lmGroupDevice).getLmGroupRipple().setControl( tokenizer.nextElement().toString() );
			((com.cannontech.database.data.device.lm.LMGroupRipple)lmGroupDevice).getLmGroupRipple().setRestore( tokenizer.nextElement().toString() );
		}
		else
		{
			// This is a VERSACOM Group
			((com.cannontech.database.data.device.lm.LMGroupVersacom)lmGroupDevice).getLmGroupVersacom().setRouteID( new Integer(Integer.parseInt(tokenizer.nextElement().toString())) );

			((com.cannontech.database.data.device.lm.LMGroupVersacom)lmGroupDevice).getLmGroupVersacom().setRelayUsage( tokenizer.nextElement().toString() );

			String aAddressUsage = tokenizer.nextElement().toString();
				
			//((com.cannontech.database.data.device.lm.LMGroupVersacom)lmGroupDevice).getLmGroupVersacom().setAddressUsage( tokenizer.nextElement().toString() );
			((com.cannontech.database.data.device.lm.LMGroupVersacom)lmGroupDevice).getLmGroupVersacom().setUtilityAddress( new Integer(Integer.parseInt(tokenizer.nextElement().toString())) );
			((com.cannontech.database.data.device.lm.LMGroupVersacom)lmGroupDevice).getLmGroupVersacom().setSectionAddress( new Integer(Integer.parseInt(tokenizer.nextElement().toString())) );
			((com.cannontech.database.data.device.lm.LMGroupVersacom)lmGroupDevice).getLmGroupVersacom().setClassAddress( new Integer(Integer.parseInt(tokenizer.nextElement().toString())) );
			((com.cannontech.database.data.device.lm.LMGroupVersacom)lmGroupDevice).getLmGroupVersacom().setDivisionAddress( new Integer(Integer.parseInt(tokenizer.nextElement().toString())) );

			if (aAddressUsage.equals(new String("U   X")))
			{
				// serial number group
				((com.cannontech.database.data.device.lm.LMGroupVersacom)lmGroupDevice).getLmGroupVersacom().setAddressUsage( new String("U   "));
				// set the serial number
				((com.cannontech.database.data.device.lm.LMGroupVersacom)lmGroupDevice).getLmGroupVersacom().setSerialAddress( tokenizer.nextElement().toString() );
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
	com.cannontech.database.data.multi.MultiDBPersistent multi = new com.cannontech.database.data.multi.MultiDBPersistent();
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
			
		Integer deviceID = new Integer( Integer.parseInt(tokenizer.nextElement().toString()) );
		String deviceType = tokenizer.nextElement().toString();
		
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

	    device.setPAOName( tokenizer.nextElement().toString() );

		// address,routeid,group1,group2,LsInt
		// set the MCT address
		device.getDeviceCarrierSettings().setAddress( new Integer( Integer.parseInt(tokenizer.nextElement().toString() )));

		// set this devices route
		device.getDeviceRoutes().setRouteID(new Integer( Integer.parseInt(tokenizer.nextElement().toString() )));
	    		
		// set group info
		device.getDeviceMeterGroup().setCollectionGroup( tokenizer.nextElement().toString() );

		device.getDeviceMeterGroup().setTestCollectionGroup( tokenizer.nextElement().toString() );

		device.getDeviceMeterGroup().setMeterNumber(new String("0"));

		//added
		device.getDeviceMeterGroup().setBillingGroup( device.getDeviceMeterGroup().getBillingGroup() );
	
		// set LoadProfile Interval
		device.getDeviceLoadProfile().setLoadProfileDemandRate(new Integer( Integer.parseInt(tokenizer.nextElement().toString()) ));

	    
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
	getIMessageFrame().addOutput("Starting Port file process...");
	
	String aFileName = getFullFileName(portFileName);
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process

	//create an object to hold all of our DBPersistant objects
	com.cannontech.database.data.multi.MultiDBPersistent multi = new com.cannontech.database.data.multi.MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );
	
	for( int i = 0; i < lines.size(); i++ )
	{
		CTILogger.info("PORT line: " + lines.get(i).toString());
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(lines.get(i).toString(), ",");
		if( tokenizer.countTokens() < PORT_TOKEN_COUNT )
		{
			CTILogger.info("** Port line #" + i + " has less than " + PORT_TOKEN_COUNT + " tokens, EXITING.");
			getIMessageFrame().addOutput("** Port line #" + i + " has less than " + PORT_TOKEN_COUNT + " tokens, EXITING.");
			return false;
		}
			
		Integer portID = new Integer( Integer.parseInt(tokenizer.nextElement().toString()) );
		com.cannontech.database.data.port.DirectPort port = null;
		
		try
		{
			//this createPort() call actually queries the database for a new unique portID!!
			// let this happen for now, but, a performance issue may occur
			port = com.cannontech.database.data.port.PortFactory.createPort( com.cannontech.database.data.pao.PAOGroups.getPortType( tokenizer.nextElement().toString()) );
		}
		catch( java.sql.SQLException e )
		{
			CTILogger.error( e.getMessage(), e );
		}

		//set our unique own portID
		port.setPortID(portID);

		int portType = com.cannontech.database.data.pao.PAOGroups.getPortType(port.getPAOType() );//guesswork here...

		if( portType == com.cannontech.database.data.pao.PAOGroups.LOCAL_DIALUP
			 || portType == com.cannontech.database.data.pao.PAOGroups.LOCAL_DIRECT
			 || portType == com.cannontech.database.data.pao.PAOGroups.LOCAL_SHARED
			 || portType == com.cannontech.database.data.pao.PAOGroups.LOCAL_RADIO )
		{
			handleLocalDirectPort( (com.cannontech.database.data.port.LocalDirectPort)port, tokenizer);
		}
		else if( portType == com.cannontech.database.data.pao.PAOGroups.TSERVER_DIALUP
			 		|| portType == com.cannontech.database.data.pao.PAOGroups.TSERVER_DIRECT
			 		|| portType == com.cannontech.database.data.pao.PAOGroups.TSERVER_RADIO
					|| portType == com.cannontech.database.data.pao.PAOGroups.TSERVER_SHARED )
		{
			handleTerminalPort( (com.cannontech.database.data.port.TerminalServerDirectPort)port, tokenizer);
		}
		else
		{
			CTILogger.info("** Unsupported port type " + port.getCommPort().getSharedPortType() + " found at line #" + i + ", EXITING.");
			getIMessageFrame().addOutput("** Unsupported port type " + port.getCommPort().getSharedPortType() + " found at line #" + i + ", EXITING.");
			return false;
		}


		multi.getDBPersistentVector().add( port );
	}

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
	com.cannontech.database.data.multi.MultiDBPersistent multi = new com.cannontech.database.data.multi.MultiDBPersistent();
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
			
		Integer deviceID = new Integer( Integer.parseInt(tokenizer.nextElement().toString()) );
		String deviceType = tokenizer.nextElement().toString();
		
		com.cannontech.database.data.device.Repeater900 device = (com.cannontech.database.data.device.Repeater900)com.cannontech.database.data.device.DeviceFactory.createDevice( com.cannontech.database.data.pao.PAOGroups.REPEATER);

		//set our unique own deviceID
		device.setDeviceID(deviceID);
		
		int deviceInt = com.cannontech.database.data.pao.PAOGroups.getDeviceType( deviceType );
		
		device.setDeviceClass( "CARRIER" );

		device.setDeviceType( deviceType );
		device.setPAOName( tokenizer.nextElement().toString() );

		// set the repeater address
		device.getDeviceCarrierSettings().setAddress( new Integer( Integer.parseInt(tokenizer.nextElement().toString() )));

		// set this devices route
		device.getDeviceRoutes().setRouteID(new Integer( Integer.parseInt(tokenizer.nextElement().toString() )));
		
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
	getIMessageFrame().addOutput("Starting Route Macro file process...");
	
	String aFileName = getFullFileName(RouteMacroFileName);
	
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process

	//create an object to hold all of our DBPersistant objects
	com.cannontech.database.data.multi.MultiDBPersistent multi = new com.cannontech.database.data.multi.MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );	
	int addCount = 0;
		
	for( int i = 0; i < lines.size(); i++ )
	{
		CTILogger.info("ROUTE_MAC line: " + lines.get(i).toString());
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(lines.get(i).toString(), ",");
		
		if( tokenizer.countTokens() < ROUTE_MACRO_TOKEN_COUNT)
		{
			CTILogger.info("** Route Macro line #" + (i+1) + " has less than " + ROUTE_MACRO_TOKEN_COUNT + " tokens, EXITING.");
			getIMessageFrame().addOutput("** Route Macro line #" + (i+1) + " has less than " + ROUTE_MACRO_TOKEN_COUNT + " tokens, EXITING.");
			return false;
		}
			
		Integer routeID = new Integer( Integer.parseInt(tokenizer.nextElement().toString()) );
		String routeType = tokenizer.nextElement().toString();

		com.cannontech.database.data.route.MacroRoute route = (com.cannontech.database.data.route.MacroRoute)com.cannontech.database.data.route.RouteFactory.createRoute(routeType);

		//set our unique own routeID
		route.setRouteID(routeID);
		
		route.setRouteName(tokenizer.nextElement().toString());

		route.setDeviceID( new Integer(0) );
		route.setDefaultRoute(new String("N"));
		
		// make a vector for the routes in the macro
		java.util.Vector RouteMacroVector = new java.util.Vector();
	
		com.cannontech.database.db.route.MacroRoute MacroRoute = null;

		// set our list of routes
		for(int count = 0; count < MAX_DSM2_MACRO_COUNT; count++)
		{
   	
   		 	Integer myRouteID = new Integer(Integer.parseInt(tokenizer.nextElement().toString())); 
   			if (myRouteID.intValue() == 0)
   			{
	   			// no more route ids in list when ID is 0
	   			break;
   			}
   			
			MacroRoute = new com.cannontech.database.db.route.MacroRoute();
		
   			MacroRoute.setRouteID(routeID);
   			MacroRoute.setSingleRouteID( myRouteID );
   			MacroRoute.setRouteOrder(new Integer( count + 1 ) );

   			RouteMacroVector.addElement( MacroRoute );
		}

		// stuff the repeaters into the CCU Route
		route.setMacroRouteVector(RouteMacroVector);
		
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
	com.cannontech.database.data.multi.MultiDBPersistent multi = new com.cannontech.database.data.multi.MultiDBPersistent();
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
			
		Integer routeID = new Integer( Integer.parseInt(tokenizer.nextElement().toString()) );
		
		String routeType = tokenizer.nextElement().toString();
			
		route = com.cannontech.database.data.route.RouteFactory.createRoute( routeType );
		
		//set our unique own routeID
		route.setRouteID(routeID);

		route.setRouteName(tokenizer.nextElement().toString());		

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
   			RptRoute.setDeviceID( new Integer(Integer.parseInt(tokenizer.nextElement().toString())) );
   
   			RptRoute.setVariableBits(new Integer(Integer.parseInt(tokenizer.nextElement().toString())) );
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
	com.cannontech.database.data.multi.MultiDBPersistent multi = new com.cannontech.database.data.multi.MultiDBPersistent();
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
			
		Integer deviceID = new Integer( Integer.parseInt(tokenizer.nextElement().toString()) );
		String deviceType = tokenizer.nextElement().toString();
		
		com.cannontech.database.data.device.RTUBase device = null;
		
		device = (com.cannontech.database.data.device.RTUBase)com.cannontech.database.data.device.DeviceFactory.createDevice( com.cannontech.database.data.pao.PAOGroups.getDeviceType( deviceType ) );

		//set our unique own deviceID
		device.setDeviceID(deviceID);

		// set the name of the RTU
		device.setPAOName( tokenizer.nextElement().toString() );
		
		// add more code here
		((com.cannontech.database.data.device.IDLCBase)device).getDeviceDirectCommSettings().setPortID( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
		((com.cannontech.database.data.device.IDLCBase)device).getDeviceIDLCRemote().setAddress( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );
		((com.cannontech.database.data.device.IDLCBase)device).getDeviceIDLCRemote().setPostCommWait( new Integer(Integer.parseInt(tokenizer.nextElement().toString()) ) );

		// check if it is dialup
		if( tokenizer.hasMoreTokens() )
		{
			((com.cannontech.database.data.device.IDLCBase)device).getDeviceDialupSettings().setPhoneNumber( tokenizer.nextElement().toString() );
			
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
	com.cannontech.database.data.multi.MultiDBPersistent multi = new com.cannontech.database.data.multi.MultiDBPersistent();
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
			
		Integer routeID = new Integer( Integer.parseInt(tokenizer.nextElement().toString()) );
		String routeType = tokenizer.nextElement().toString();
		com.cannontech.database.data.route.RouteBase route = null;

		int routeInt = com.cannontech.database.data.pao.PAOGroups.getRouteType( routeType );	
			
		route = com.cannontech.database.data.route.RouteFactory.createRoute( com.cannontech.database.data.pao.PAOGroups.getRouteType( routeType ));

		//set our unique own routeID
		route.setRouteID(routeID);
		route.setRouteName(tokenizer.nextElement().toString());
		route.setRouteType(routeType);
			
		switch(routeInt)
		{
			case com.cannontech.database.data.pao.PAOGroups.ROUTE_CCU:
			// handle CCU
				handleRouteType( ((com.cannontech.database.data.route.CCURoute)route), tokenizer);
				break;
				
			default:
				// private void handleRouteType(com.cannontech.database.data.route.CCURoute aRoute, java.util.StringTokenizer tokenizer) 

				((com.cannontech.database.data.route.RouteBase)route).setDeviceID( new Integer( Integer.parseInt(tokenizer.nextElement().toString())) );
				
				// not sure if this will work every	time???
				( (com.cannontech.database.data.route.RouteBase)route).setDefaultRoute( tokenizer.nextElement().toString() );//guesswork again
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
	CTILogger.info("Starting StateGroup file process...");
	getIMessageFrame().addOutput("Starting StateGroup file process...");
	
	String aFileName = getFullFileName(stateGroupFileName);
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process

	//create an object to hold all of our DBPersistant objects
	com.cannontech.database.data.multi.MultiDBPersistent multi = new com.cannontech.database.data.multi.MultiDBPersistent();
	
	for( int i = 0; i < lines.size(); i++ )
	{
		CTILogger.info("STATE_GRP line: " + lines.get(i).toString());
		int stateCount = 0;
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(lines.get(i).toString(), ",");
		if( tokenizer.countTokens() < STATE_TOKEN_COUNT )
		{
			CTILogger.info("** State line #" + i + " has less than " + STATE_TOKEN_COUNT + " tokens, EXITING.");
			getIMessageFrame().addOutput("** State line #" + i + " has less than " + STATE_TOKEN_COUNT + " tokens, EXITING.");
			return false;
		}
		
		com.cannontech.database.db.state.StateGroup group = new com.cannontech.database.db.state.StateGroup();
		
		//if we are at the beginning of the line, we must create a new StateGroup for the States
		group.setStateGroupID( new Integer( Integer.parseInt(tokenizer.nextElement().toString()) ) );
		group.setName( tokenizer.nextElement().toString() );
		stateCount = Integer.parseInt(tokenizer.nextElement().toString());
		multi.getDBPersistentVector().add(group);
		
		for( int j = 0; j < stateCount; j++)
		{
         com.cannontech.database.data.state.State state = 
                  new com.cannontech.database.data.state.State();
         
			state.setState( new com.cannontech.database.db.state.State(
               group.getStateGroupID(),
               new Integer( Integer.parseInt(tokenizer.nextElement().toString()) ),
   			   tokenizer.nextElement().toString(),
   			   new Integer( Integer.parseInt(tokenizer.nextElement().toString()) ),
   			   new Integer( Integer.parseInt(tokenizer.nextElement().toString()) ) ) );

         
			multi.getDBPersistentVector().add( state );
		}
	}

	boolean success = writeToSQLDatabase(multi);

	if( success )
	{
		CTILogger.info(" StateGroup file processed and inserted Successfully");
		getIMessageFrame().addOutput(" StateGroup file processed and inserted Successfully");
	}
	else
		getIMessageFrame().addOutput(" StateGroup file failed insertion");	
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
	getIMessageFrame().addOutput("Starting Status Point file process...");
	
	String aFileName = getFullFileName(StatusPointFileName);
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process
	
	//create an object to hold all of our DBPersistant objects
	com.cannontech.database.data.multi.MultiDBPersistent multi = new com.cannontech.database.data.multi.MultiDBPersistent();

	// if this is not set to false it will create its own PointIDs
	multi.setCreateNewPAOIDs( false );
		
	int addCount = 0;
		
	for( int i = 0; i < lines.size(); i++ )
	{
		CTILogger.info("STATUS_PT line: " + lines.get(i).toString());
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(lines.get(i).toString(), ",");
		if( tokenizer.countTokens() < STATUS_PT_TOKEN_COUNT )
		{
			CTILogger.info("** Status Point line #" + i + " has less than " + STATUS_PT_TOKEN_COUNT + " tokens, EXITING.");
			getIMessageFrame().addOutput("** Status Point line #" + i + " has less than " + STATUS_PT_TOKEN_COUNT + " tokens, EXITING.");
			return false;
		}
		
		// PtID,type,name,devID,Pseudo,offset,StateGrpID,CtrlType,Ctrloffset,time1,time2,archive,ArchInterval
		// 3230,Status,COMM STATUS,1,Y,0,5,None,0,0,0,On Change,0

		Integer pointID = new Integer( Integer.parseInt(tokenizer.nextElement().toString()) );
		
		String pointType = tokenizer.nextElement().toString();
		
		com.cannontech.database.data.point.StatusPoint statusPoint = new com.cannontech.database.data.point.StatusPoint();
				
		//set our unique deviceID
		statusPoint.setPointID(pointID);
		
		statusPoint.getPoint().setPointType(pointType);
	    statusPoint.getPoint().setPointName( tokenizer.nextElement().toString() );

		Integer deviceID = new Integer( Integer.parseInt(tokenizer.nextElement().toString()) );

		statusPoint.getPoint().setPaoID( deviceID );

		Character wastedToken = new Character(tokenizer.nextElement().toString().charAt(0));
		//statusPoint.getPoint().setPseudoFlag(new Character(tokenizer.nextElement().toString().charAt(0)));
		statusPoint.getPoint().setPointOffset(new Integer( Integer.parseInt(tokenizer.nextElement().toString())));	

		// state group next
		statusPoint.getPoint().setStateGroupID(new Integer( Integer.parseInt(tokenizer.nextElement().toString())));
		
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

		// Control point settings	
		statusPoint.getPointStatus().setControlType(tokenizer.nextElement().toString());
		
		Integer controlOffset = new Integer( Integer.parseInt(tokenizer.nextElement().toString()));
		Integer closeTime1 = new Integer( Integer.parseInt(tokenizer.nextElement().toString()));	
		Integer closeTime2 = new Integer( Integer.parseInt(tokenizer.nextElement().toString()));
		
		if (!statusPoint.getPointStatus().getControlType().equals(new String("None")) )
		{
			// there is control
			statusPoint.getPointStatus().setControlOffset(controlOffset);
			
			statusPoint.getPointStatus().setCloseTime1(closeTime1);
			statusPoint.getPointStatus().setCloseTime2(closeTime2);
		}
		
		//archiving settings
		statusPoint.getPoint().setArchiveType(tokenizer.nextElement().toString());
		statusPoint.getPoint().setArchiveInterval( new Integer( Integer.parseInt(tokenizer.nextElement().toString())) );
			
		multi.getDBPersistentVector().add( statusPoint );
		
		++addCount;
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
	getIMessageFrame().addOutput("Starting Transmitter file process...");
	
	String aFileName = getFullFileName(transmitterFileName);
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process

	//create an object to hold all of our DBPersistant objects
	com.cannontech.database.data.multi.MultiDBPersistent multi = new com.cannontech.database.data.multi.MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );
		
	for( int i = 0; i < lines.size(); i++ )
	{
		CTILogger.info("TRANSMITTER line: " + lines.get(i).toString());
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(lines.get(i).toString(), ",");
		if( tokenizer.countTokens() < TRANSMITTER_TOKEN_COUNT )
		{
			CTILogger.info("** Transmitter line #" + i + " has less than " + TRANSMITTER_TOKEN_COUNT + " tokens, EXITING.");
			getIMessageFrame().addOutput("** Transmitter line #" + i + " has less than " + TRANSMITTER_TOKEN_COUNT + " tokens, EXITING.");
			return false;
		}
			
		Integer deviceID = new Integer( Integer.parseInt(tokenizer.nextElement().toString()) );
		String deviceType = tokenizer.nextElement().toString();
		com.cannontech.database.data.device.DeviceBase device = null;

		device = com.cannontech.database.data.device.DeviceFactory.createDevice( com.cannontech.database.data.pao.PAOGroups.getDeviceType( deviceType ) );
		
		
		//set our unique own deviceID
		device.setDeviceID(deviceID);

		handleTransmitter( (com.cannontech.database.data.device.TwoWayDevice)device, tokenizer);

		multi.getDBPersistentVector().add( device );
	}

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
	com.cannontech.database.data.multi.MultiDBPersistent multi = new com.cannontech.database.data.multi.MultiDBPersistent();
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
			
		Integer deviceID = new Integer( Integer.parseInt(tokenizer.nextElement().toString()) );
		String deviceType = tokenizer.nextElement().toString();
		
		com.cannontech.database.data.device.DeviceBase device = null;
		
		device = com.cannontech.database.data.device.DeviceFactory.createDevice( com.cannontech.database.data.pao.PAOGroups.getDeviceType( deviceType ) );
		
		//set our unique own deviceID
		device.setDeviceID(deviceID);
		device.setPAOName( tokenizer.nextElement().toString() );
	
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
 * @param multi com.cannontech.database.data.multi.MultiDBPersistent
 */
private boolean writeToSQLDatabase(com.cannontech.database.data.multi.MultiDBPersistent multi) 
{
	//write all the collected data to the SQL database
	try
	{
      multi = (com.cannontech.database.data.multi.MultiDBPersistent)
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