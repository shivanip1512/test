package com.cannontech.dbconverter.converter;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.cannontech.database.data.port.PortFactory;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.data.route.RouteFactory;
import com.cannontech.database.db.device.lm.LMProgramConstraint;
import com.cannontech.database.db.device.lm.LMProgramControlWindow;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGroup;
import com.cannontech.database.db.pao.PAOExclusion;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.database.db.point.PointLimit;
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
	 * ID in the Hashmap may be needed to forma a foreign key relationship
	 * with another table.
	 * ______________________________________________________________________________*/
	private HashMap stateGrpMap = new HashMap(32);
	private HashMap deviceIDsMap = new HashMap(32);
	private HashMap routeIDsMap = new HashMap(32);
	
	//example KEY<String> VALUES< ArrayList(LMGroups) >
	private HashMap grpStratMap = new HashMap(32);

	//example KEY<String> VALUES<LMProgramConstraint>
	private HashMap constByNameMap = new HashMap(64);

	//example KEY<Integer> VALUES<LMProgramControlWindow>
	private HashMap constToWindowMap = new HashMap(64);

	//example KEY<String> VALUES< int[] )>
	private HashMap gearNameToShedPerc = new HashMap(64);



	/* ______________________________________________________________________________
	 * 
	 * IDs that need to be unique and are incremented everytime a new
	 * item is added.
	 * ______________________________________________________________________________*/
	private int ROUTE_OFFSET = 500;
	private int START_ROUTE_ID = ROUTE_OFFSET + 1;

	private int PORTID_OFFSET = 2000;

	private int GROUPID_OFFSET = 3000;
	private int START_GROUP_ID = GROUPID_OFFSET + 1;

	private int PROGRAMID_OFFSET = 10000;
	private int START_PROGRAM_ID = PROGRAMID_OFFSET + 1;

	private int START_PTID = 500;
	private int START_CONSTRAINT_ID = 1;


	/* ______________________________________________________________________________
	 * 
	 * File names that are read in by this application
	 * ______________________________________________________________________________*/
	private String analogPointFileName = "cti-analog.txt";
	private String transmitterFileName = "cti-transmitter.txt";	
	private String statusPointFileName = "cti-status.txt";
	private String routeMacroFileName = "cti-users.txt";
	private String routeEntriesFileName = "cti-usertransmitter.txt";
	private String groupFileName = "cti-code_groups.txt";
	private String progToGroupsFileName = "cti-programs_groups.txt";
	private String progConstTimeFileName = "cti-sttime.txt";
	private String progConstDateFileName = "cti-stdate.txt";
	private String progConstTypeFileName = "cti-sttype.txt";


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
 * Executs all db inserts that are needed. If a failure occurs,
 * then execution stops.
 */
public void run()
{

	boolean s = processPortFile();

	if( s ) s = processTransmitterFile();

	if( s ) s = processLoadGroups();

	if( s ) s = processLoadProgramConstraints();

	if( s ) s = processLoadPrograms();
	

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
		if( i <= 2 || line.length <= 9 )
			continue;
		
		CTILogger.info("LOAD_PROGRAM line: " + lines.get(i).toString());


		LMProgramDirect lmProgram =
			(LMProgramDirect)LMFactory.createLoadManagement( DeviceTypes.LM_DIRECT_PROGRAM );

		lmProgram.setName( line[4].trim() + " " + line[0].trim() );
		lmProgram.getProgram().setControlType( LMProgramBase.OPSTATE_MANUALONLY );

		//set our unique own deviceID
		lmProgram.setPAObjectID( new Integer(START_PROGRAM_ID++) );

		
		/* Create a default gear */
		TimeRefreshGear gear = (TimeRefreshGear)LMProgramDirectGear.createGearFactory(
									LMProgramDirectGear.CONTROL_TIME_REFRESH );
		gear.setGearName("Refresh");
		gear.setDeviceID( lmProgram.getPAObjectID() );
		gear.setShedTime( new Integer(900) ); //15 minutes
		gear.setRefreshRate( new Integer(600) ); //10 minutes
		gear.setMethodStopType( LMProgramDirectGear.STOP_TIME_IN );
		gear.setGroupSelectionMethod( LMProgramDirectGear.SELECTION_LAST_CONTROLLED );
		gear.setRampInInterval( new Integer(300) );
		gear.setRampOutInterval( new Integer(300) );




		//assign all the Groups to this Program
		String stratID = line[4].trim(); //lmProgram.getPAOName();
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


		String constrName = line[0].trim();
		LMProgramConstraint constraint = (LMProgramConstraint)constByNameMap.get( constrName );
		if( constraint != null )
		{
			lmProgram.getProgram().setConstraintID( constraint.getConstraintID() );

			//store ramp % using the original constraint name 
			int[] rampPerc = (int[])gearNameToShedPerc.get( constrName );
			gear.setRampInPercent( new Integer(rampPerc[0]) );
			gear.setRampOutPercent( new Integer(rampPerc[1]) );



			LMProgramControlWindow lmWindow = 
				(LMProgramControlWindow)constToWindowMap.get(constraint.getConstraintID());

			if( lmWindow != null )
			{
				try
				{
					//must make a new instance/deep clone of original Window
					LMProgramControlWindow realWindow = 
						(LMProgramControlWindow)CtiUtilities.copyObject( lmWindow );
	
					realWindow.setDeviceID( lmProgram.getPAObjectID() );
					
					lmProgram.getLmProgramControlWindowVector().add( realWindow );
				}
				catch( Exception ex ) {}
			}		
		}
		

		lmProgram.getLmProgramDirectGearVector().add( gear );
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
		
		String name = line[12].trim();
		name = name.replaceAll("\"", ""); //remove double quotes
		name = name.replaceAll("\\.", ""); //remove periods
		
		lmGroupGolay.setPAOName(
			name.replaceAll("([ ])+", " ") + " " +
			lmGroupGolay.getLMGroupSASimple().getOperationalAddress() );


		lmGroupGolay.getLMGroupSASimple().setNominalTimeout( new Integer(900) );

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
	
	//example KEY<typeID> VALUES< List(LMProgramConstraint) >
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
		if( i <= 2 || line.length <= 10 || line[0].trim().length() <= 0 )
			continue;
		
		if( line[3].trim().charAt(0) == 'Y' || line[3].trim().charAt(0) == 'N' )
		{
			CTILogger.info("LOAD_PROG_TIME_CONSTRAINTS line: " + lines.get(i).toString());
	
	
			LMProgramConstraint lmProgConst = new LMProgramConstraint();
			String constrName = line[0].trim();		
			lmProgConst.setConstraintName( constrName + " " + line[11].trim() );
			lmProgConst.setConstraintID( new Integer(START_CONSTRAINT_ID++) );
	
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

			//all constraints have their own time window
			LMProgramControlWindow lmWindow = new LMProgramControlWindow();
			lmWindow.setWindowNumber( new Integer(1) );
			lmWindow.setAvailableStartTime( decodeStringToSeconds(line[1].trim()) );
			lmWindow.setAvailableStopTime( decodeStringToSeconds(line[2].trim()) );			
			constToWindowMap.put( lmProgConst.getConstraintID(), lmWindow );

			multi.getDBPersistentVector().add( lmProgConst );
			constByNameMap.put( constrName, lmProgConst );
		}
	}



	/* Start reading through our Constraint to Date mapping file */		
	String constDateFile = getFullFileName(progConstDateFileName);
	lines = readFile(constDateFile);
	for( int i = 0; i < lines.size(); i++ )
	{
		String[] line = lines.get(i).toString().split(",");

		//ignore title line
		if( i <= 2 || line.length <= 3 || line[0].length() <= 0 )
			continue;

		String key = line[0].trim();
		String typeID = line[3].trim();

		if( constByNameMap.containsKey(key) ) 
		{
			LMProgramConstraint progConst = 
					(LMProgramConstraint)constByNameMap.get( key );

			if( constMapByType.containsKey(typeID) )
			{
				ArrayList typeList = (ArrayList)constMapByType.get(typeID);
				typeList.add( progConst );
			}
			else
			{
				ArrayList typeList = new ArrayList(8);
				typeList.add( progConst );
				constMapByType.put( typeID, typeList );
			}
		}


	}


	/* Start reading through our ConstraintType to ConstraintValue mapping file */		
	String constTypeFile = getFullFileName(progConstTypeFileName);
	java.util.ArrayList typeLines = readFile(constTypeFile);
	for( int i = 0; i < typeLines.size(); i++ )
	{
		String[] line = typeLines.get(i).toString().split(",");

		//ignore title line
		if( i <= 2 || line.length < 9 || line[0].length() <= 0 )
			continue;

		String typeID = line[0].trim();


		ArrayList typeList = (ArrayList)constMapByType.get(typeID);
		for( int j = 0; typeList != null && j < typeList.size(); j++ )
		{
			LMProgramConstraint constr =
				(LMProgramConstraint)typeList.get(j);

			//store shed % using the original gear name 
			gearNameToShedPerc.put( 
				constr.getConstraintName().substring(0,constr.getConstraintName().indexOf(' ')).trim(),
				new int[] 
				{ pInt(line[1].trim()).intValue(), pInt(line[4].trim()).intValue() } );


			//convert minutes to hours
			constr.setMinRestartTime(
				new Integer(pInt(line[7].trim()).intValue() / 60) );

			//convert minutes to hours
			constr.setMaxHoursDaily(
				new Integer(pInt(line[8].trim()).intValue() / 60) );
			
			if( line.length >= 10 )
				constr.setMaxDailyOps(
					new Integer(pInt(line[9].trim()).intValue()) );

			
			/*
				lmProgConst.setMinActivateTime()
	
				lmProgConst.setMaxHoursSeasonal()
				lmProgConst.setMaxHoursMonthly()
				lmProgConst.setMaxActivateTime()

				lmProgConst.setMaxActivateTime()
	
				lmProgConst.setAvailableSeasons()
			*/			
		}


	}


	boolean success = writeToSQLDatabase(multi);

	if( success )
	{
		CTILogger.info(" Load Program Constraints file was processed and inserted Successfully");
		getIMessageFrame().addOutput("Load Program Constraints file was processed and inserted Successfully");
		
		CTILogger.info(" " + multi.getDBPersistentVector().size() + " Load Program Constraints were added to the database");
		getIMessageFrame().addOutput(multi.getDBPersistentVector().size() + " Load Program Constraints were added to the database");
	}
	else
		getIMessageFrame().addOutput(" Load Program Constraints failed adding to the database");


	return success;
}

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

private DirectPort createDirectPort( Integer portID )
{
	DirectPort port = null;

	try
	{
		//this createPort() call actually queries the database for a new unique portID!!
		// let this happen for now, but, a performance issue may occur
		port = PortFactory.createPort( PortTypes.LOCAL_SHARED );


		//set our unique own portID
		port.setPortName( "Port #" + (portID.intValue() - PORTID_OFFSET) );
		port.setPortID(portID);

		handleLocalDialupPort( (LocalDirectPort)port );
	}
	catch( java.sql.SQLException e )
	{
		CTILogger.error( e.getMessage(), e );
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
		String[] line = lines.get(i).toString().split(",");

		//ignore title line
		if( i == 0 || line.length <= 20 )
			continue;
		
		CTILogger.info("PORT (Local Serial Port) line: " + lines.get(i).toString());

			
		Integer portID = new Integer(
				pInt(line[3].toString()).intValue() 
				+ PORTID_OFFSET );

		//ingore this, we already have it
		if( portID.intValue() == PORTID_OFFSET || commChannels.contains(portID.intValue()) )
			continue;


		DirectPort port = createDirectPort( portID );
		

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
		String[] line = lines.get(i).toString().split(",");

		//ignore title line
		if( i == 0 || line.length <= 20 )
			continue;
			
		CTILogger.info("TRANSMITTER line: " + lines.get(i).toString());

		Integer deviceID = pInt(line[0].trim());
				
		String deviceType = DeviceTypes.STRING_SERIES_5_LMI[0];
		Series5LMI device = null;

		device = (Series5LMI)DeviceFactory.createDevice( PAOGroups.getDeviceType( deviceType ) );
		device.setDeviceID(deviceID);
		
		//replace all blanks with a single space
		String name = line[1].trim().replaceAll("([ ])+", " ");
		device.setPAOName( name.substring(2, name.length() - 2) );

		device.getDeviceDirectCommSettings().setPortID( 
			new Integer(pInt(line[2].trim()).intValue() + PORTID_OFFSET) );

		device.getSeries5().setSlaveAddress( pInt(line[3].trim()) );
		
		device.getSeries5RTU().setStartCode( pInt(line[4].trim()) );
		device.getSeries5RTU().setStopCode( pInt(line[5].trim()) );
		device.getSeries5RTU().setTransmitOffset( pInt(line[6].trim()) );

		//CycleTime:#,Offset:#,TransmitTime:#,MaxTime:#
		device.getPAOExclusionVector().add(
			PAOExclusion.createExclusTiming(
				device.getPAObjectID(),
				new Integer(300),
				device.getSeries5RTU().getTransmitOffset(),
				new Integer(60) ) );


		String dis = line[7].trim();
		device.setDisableFlag( new Character(
				(dis.equalsIgnoreCase("N") ? 'Y' : 'N')) );

		device.getSeries5RTU().setSaveHistory( line[8].trim().toUpperCase() );

		device.getSeries5RTU().setPowerValueMultiplier( pDbl(line[9].trim()) );
		device.getSeries5RTU().setPowerValueOffset( pDbl(line[10].trim()) );
		
		//15
		device.getSeries5RTU().setPowerValueHighLimit( pInt(line[15].trim()) );
		device.getSeries5RTU().setPowerValueLowLimit( pInt(line[16].trim()) );
		
		device.getSeries5RTU().setRetries( pInt(line[19].trim()) );
		
		
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
			
			
			//Create the PowerValue point
			AnalogPoint pvPoint = 
				(AnalogPoint)PointFactory.createPoint( PointTypes.ANALOG_POINT );

			pvPoint.setPointID( new Integer(START_PTID) );
			pvPoint.getPoint().setPaoID( deviceID );
			pvPoint.getPoint().setPointOffset( new Integer(1000) );
			pvPoint.getPoint().setPointName( "Power Value" );

			pvPoint.getPointAnalog().setMultiplier( device.getSeries5RTU().getPowerValueMultiplier() );
			pvPoint.getPointAnalog().setDataOffset( device.getSeries5RTU().getPowerValueOffset() );

			PointLimit myPointLimit = new PointLimit();
			myPointLimit.setPointID( pvPoint.getPoint().getPointID() );
			myPointLimit.setHighLimit( new Double(device.getSeries5RTU().getPowerValueHighLimit().doubleValue()) );
			myPointLimit.setLowLimit( new Double(device.getSeries5RTU().getPowerValueLowLimit().doubleValue()) );
			myPointLimit.setLimitDuration( new Integer(0) );
			myPointLimit.setLimitNumber( new Integer(1) );


			if( myPointLimit.getLowLimit().doubleValue() != 0.0
				 && myPointLimit.getHighLimit().doubleValue() != 0.0 )
			{
				Vector v = new Vector(1);
				v.add( myPointLimit );
				pvPoint.setPointLimitsVector(v);
			}

			// set default settings for the point
			pvPoint.getPoint().setServiceFlag(new Character('N'));
			pvPoint.getPoint().setAlarmInhibit(new Character('N'));
			pvPoint.getPointAlarming().setAlarmStates( PointAlarming.DEFAULT_ALARM_STATES );
			pvPoint.getPointAlarming().setExcludeNotifyStates( PointAlarming.DEFAULT_EXCLUDE_NOTIFY );
			pvPoint.getPointAlarming().setNotifyOnAcknowledge( new String("N") );
			pvPoint.getPointAlarming().setNotificationGroupID(  new Integer(PointAlarming.NONE_NOTIFICATIONID) );
			pvPoint.getPoint().setArchiveType("None");
			pvPoint.getPoint().setArchiveInterval( new Integer(0) );
			pvPoint.getPoint().setStateGroupID( new Integer(-1) );
			pvPoint.getPointUnit().setDecimalPlaces(new Integer(2));
			pvPoint.getPointAnalog().setDeadband(new Double(0.0));
			pvPoint.getPointAnalog().setTransducerType( new String("none") );
			pvPoint.getPointUnit().setUomID( new Integer(8) );

			multi.getDBPersistentVector().add( pvPoint );		
			++START_PTID;
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