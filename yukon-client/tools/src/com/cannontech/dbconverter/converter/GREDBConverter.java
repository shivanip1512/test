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
import com.cannontech.database.data.device.RTC;
import com.cannontech.database.data.device.Series5LMI;
import com.cannontech.database.data.device.lm.IGroupRoute;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.device.lm.LMGroupGolay;
import com.cannontech.database.data.device.lm.LMGroupSA205;
import com.cannontech.database.data.device.lm.LMGroupSADigital;
import com.cannontech.database.data.device.lm.LMProgramBase;
import com.cannontech.database.data.device.lm.LMProgramDirect;
import com.cannontech.database.data.device.lm.LMScenario;
import com.cannontech.database.data.device.lm.SmartCycleGear;
import com.cannontech.database.data.device.lm.TimeRefreshGear;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.PortTypes;
import com.cannontech.database.data.pao.RouteTypes;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.data.port.DirectPort;
import com.cannontech.database.data.port.LocalDirectPort;
import com.cannontech.database.data.port.PortFactory;
import com.cannontech.database.data.route.MacroRoute;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.data.route.RouteFactory;
import com.cannontech.database.data.state.GroupState;
import com.cannontech.database.data.state.State;
import com.cannontech.database.db.device.lm.IlmDefines;
import com.cannontech.database.db.device.lm.LMControlScenarioProgram;
import com.cannontech.database.db.device.lm.LMProgramConstraint;
import com.cannontech.database.db.device.lm.LMProgramControlWindow;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGroup;
import com.cannontech.database.db.pao.PAOExclusion;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.database.db.point.PointLimit;
import com.cannontech.database.db.state.YukonImage;
import com.cannontech.dbtools.updater.MessageFrameAdaptor;
import com.cannontech.tools.gui.*;

/**
 * Steps to a successful conversion:
 * 
 * 1) Create a Control Area for all Programs ----
 *      select * from yukonpaobject where type = 'LM CONTROL AREA' (id = 11)
 *      insert into lmcontrolareaprogram select 11, deviceid, 0, 0, 0 from lmprogramdirect
 *
 * 2) Run the following clean up statements:
 * 
 *    delete from LMProgramConstraints where constraintid not in (select constraintid from lmprogram);
 *
 *
 * @author: ryan
 */
public class GREDBConverter extends MessageFrameAdaptor 
{
	/* ________________ PC Maps _________________*/

	private HashMap stateGrpMap = new HashMap(32);
	private HashMap deviceIDsMap = new HashMap(32);
	private HashMap routeIDsMap = new HashMap(32);
	
	//example KEY<SW01> VALUES<SW - Anoka (Grp 1-2),SW - Anoka (Grp 1-3)>
	private HashMap grpStratMap = new HashMap(32);

	//example KEY<SW:0000> VALUES<LMProgramConstraint)>
	private HashMap constByNameMap = new HashMap(64);

	//example KEY<Integer> VALUES<LMProgramControlWindow)>
	private HashMap constToWindowMap = new HashMap(64);

	//example KEY<CONST_NAME> VALUES< int[] )>
	private HashMap gearNameToShedPerc = new HashMap(64);



	/* ________________ Unix Maps _________________*/
	
	//example KEY<TRX_NAME> VALUES< RouteBase >
	private HashMap unxTrxToRtMap = new HashMap(32);

	//example KEY<TRX_NAME> VALUES< int[2] >
	private HashMap unxTrxToGroupIntMap = new HashMap(64);

	//example KEY<cgid> VALUES< LMGroup >
	private HashMap unxGrpNameToGrpMap = new HashMap(32);
	
	//example KEY<PROGRAM_NAME> VALUES< LMProgram >
	private HashMap unxPrgNameMap = new HashMap(64);



	//pao id set
	private int ROUTE_OFFSET = 500;
	private int START_ROUTE_ID = ROUTE_OFFSET + 1;

	private int ROUTE_MACRO_OFFSET = 1000;
	private int START_SCENARIO_ID = 1500;
	private int PORTID_OFFSET = 2000;
	private int UNX_PORTID_OFFSET = 2100;

	private int GROUPID_OFFSET = 3000;
	private int START_GROUP_ID = GROUPID_OFFSET + 1;

	private int PROGRAMID_OFFSET = 10000;
	private int START_PROGRAM_ID = PROGRAMID_OFFSET + 1;

	private int START_RTCID = 12000;
	

	//other id set
	private int START_PTID = 500;
	private int START_STATEGRP_ID = 100;
	private int START_CONSTRAINT_ID = 1;


	//pc prop files
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



	//unix prop files
	private String unxRTCFileName = "xmitters.txt";
	private String unxCgcGroupFileName = "cgc_assoc.txt";
	private String unxCGrpFileName = "cgroup.txt";
	private String unxCGTFileName = "cgt_assoc.txt";

	private String unxConstrFileName = "constraints.txt";
	private String unxLGrpFileName = "lgroup.txt";
	private String unxPrgToGrpFileName = "cg_lg_code.txt"; //"cglg_assoc.txt";
	private String unxScenFileName = "mgroup.txt";	
	private String unxGearFileName = "ctrl_method.txt";

		
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

	if( s ) s = processLoadProgramConstraints();

	if( s ) s = processLoadPrograms();
	

	getIMessageFrame().addOutput("");
	getIMessageFrame().addOutput("FINISHED with PC Database Conversion");

	getIMessageFrame().addOutput("------------------------------------------------");
	getIMessageFrame().addOutput("Starting UNIX Database Conversion....");


	if( s ) s = processUnxRTCFile();
	if( s ) s = processUnxLoadGroups();


	if( s ) s = processUnxPrograms();
	if( s ) s = processUnxLoadScenarios();





	getIMessageFrame().addOutput("FINISHED with UNIX Database Conversion");
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
		if( i <= 2 || tokenizer.countTokens() < 8 )
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
		if( i <= 2 || line.length <= 9 )
			continue;
		
		CTILogger.info("LOAD_PROGRAM line: " + lines.get(i).toString());


		LMProgramDirect lmProgram =
			(LMProgramDirect)LMFactory.createLoadManagement( DeviceTypes.LM_DIRECT_PROGRAM );

		lmProgram.setName( line[4].trim() + " " + line[0].trim() );
		lmProgram.getProgram().setControlType( LMProgramBase.OPSTATE_AUTOMATIC );

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
		
		//replace all blanks with a single _
		String name = line[12].trim();
		name = name.replaceAll("\"", ""); //remove double quotes
		name = name.replaceAll("\\.", ""); //remove periods
		
		lmGroupGolay.setPAOName(
			name.replaceAll("([ ])+", " ") + " " +
			lmGroupGolay.getLMGroupSASimple().getOperationalAddress() );


		lmGroupGolay.getLMGroupSASimple().setNominalTimeout( new Integer(900) );
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

		handleLocalDirectPort( (LocalDirectPort)port );
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
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(lines.get(i).toString(), ",");

		//ignore title line
		if( i == 0 || tokenizer.countTokens() < 20 )
			continue;
		
		CTILogger.info("PORT (Local Serial Port) line: " + lines.get(i).toString());

		//skip 2 tokens
		tokenizer.nextToken();
		tokenizer.nextToken();
			
		Integer portID = new Integer(
				pInt(tokenizer.nextToken()).intValue() 
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
		if( i <= 2 || tokenizer.countTokens() < 3 )
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
				if( j <= 2 || entrTokenizer.countTokens() < 2 )
					continue;
				
				Integer devID = pInt(entrTokenizer.nextToken());
				Integer userID = pInt(entrTokenizer.nextToken());
				
				if( !deviceIDsMap.containsKey(new Integer(devID.intValue() + ROUTE_OFFSET)) )
					continue;

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
		if( i <= 2 || tokenizer.countTokens() < 8 )
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
		if( i <= 2 || tokenizer.countTokens() < 8 )
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
		String[] line = lines.get(i).toString().split(",");

		//ignore title line
		if( i == 0 || line.length <= 20 )
			continue;
			
		CTILogger.info("TRANSMITTER (Series 5 LMI) line: " + lines.get(i).toString());

		Integer deviceID = pInt(line[0].trim());
				
		String deviceType = DeviceTypes.STRING_SERIES_5_LMI[0];
		Series5LMI device = null;

		device = (Series5LMI)DeviceFactory.createDevice( PAOGroups.getDeviceType( deviceType ) );
		device.setDeviceID(deviceID);
		
		//replace all blanks with a single _
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
 * Creation date: (3/31/2001 11:43:17 AM)
 * @return boolean
 */
public boolean processUnxRTCFile() 
{
	CTILogger.info("Starting RTC file process...");
	getIMessageFrame().addOutput("Starting RTC file process (" + unxRTCFileName + ")...");
	
	String aFileName = getFullFileName(unxRTCFileName);
	java.util.ArrayList lines = readFile(aFileName);

	if( lines == null )
		return true; //continue the process

	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );
	
	//example KEY<AGRALITE> VALUES< ArrayList(RTC) >
	HashMap rtcToRtMacroMap = new HashMap(32);

	//example KEY<PortID> VALUES< DirectPort >
	HashMap portIDMap = new HashMap(32);
	


	for( int i = 0; i < lines.size(); i++ )
	{
		String[] line = lines.get(i).toString().split(",");

		//ignore title line
		if( i == 0 || line.length <= 13 )
			continue;
			
		CTILogger.info("RTC line: " + lines.get(i).toString());

		String deviceType = DeviceTypes.STRING_RTC[0];
		String devName = line[0].trim();
		RTC device = (RTC)DeviceFactory.createDevice( PAOGroups.getDeviceType(deviceType) );

		Integer rtcID = new Integer(START_RTCID++);
		device.setDeviceID( rtcID );
		device.setPAOName( devName + " #" + pInt(line[1].trim()) );

		//create a port if we do not already have it created
		Integer portID = new Integer( pInt(line[3].trim()).intValue() + UNX_PORTID_OFFSET );
		if( !portIDMap.containsKey(portID) )
		{
			DirectPort dirPort = createDirectPort( portID );
			multi.getDBPersistentVector().add( dirPort);
			portIDMap.put( portID, dirPort );
		}

		device.getDeviceDirectCommSettings().setPortID( portID );
		device.getDeviceRTC().setRTCAddress( pInt(line[4].trim()) );

		String dis = line[5].trim();
		device.setDisableFlag( new Character(
				(dis.equalsIgnoreCase("ACTIVE") ? 'N' : 'Y')) );

//		device.setCyc( pInt(line[7].trim()) );

		//CycleTime:#,Offset:#,TransmitTime:#,MaxTime:#
		device.getPAOExclusionVector().add(
			PAOExclusion.createExclusTiming(
				rtcID,
				new Integer(300),
				pInt(line[8].trim()),
				new Integer(60) ) );
				

		device.getDeviceRTC().setLBTMode( pInt(line[9].trim()) );


		//12 is repeats
		unxTrxToGroupIntMap.put(
			devName,
			new int[] {
				pInt(line[13].trim()).intValue(),     //space index
				pInt(line[14].trim()).intValue() } ); //mark index


		//create a route
		RouteBase route = null;
		route = RouteFactory.createRoute( RouteTypes.ROUTE_RTC );

		route.setRouteID( new Integer(START_ROUTE_ID++) );
		route.setRouteName( device.getPAOName() + " Rt");
		route.setDeviceID( rtcID );
		route.setDefaultRoute("N");

		if( portID.intValue() > UNX_PORTID_OFFSET )
		{
			String macRtName = line[6].trim();
			addToListMap( rtcToRtMacroMap, macRtName, route );		

			multi.getDBPersistentVector().add( device );
			multi.getDBPersistentVector().add( route );
			
			unxTrxToRtMap.put( devName, route );
		}		
	}


	//add our Macro Routes for Transmitters
	Iterator macIt = rtcToRtMacroMap.keySet().iterator();
	while( macIt.hasNext() )
	{
		String macName = macIt.next().toString();

		MacroRoute macRoute = (MacroRoute)RouteFactory.createRoute(RouteTypes.ROUTE_MACRO);	
		macRoute.setRouteID( new Integer(ROUTE_MACRO_OFFSET++) );
		macRoute.setRouteName( "@" + macName + " #" + macRoute.getRouteID() );
		macRoute.setDeviceID( new Integer(0) );
		macRoute.setDefaultRoute(new String("N"));


		if( !macRoute.getRouteName().toUpperCase().startsWith("@NONE") )
		{
			ArrayList trxList = (ArrayList)rtcToRtMacroMap.get( macName );			
			int order = 1;
			for( int j = 0; j < trxList.size(); j++ )
			{
				RouteBase trxRt = (RouteBase)trxList.get(j);

				com.cannontech.database.db.route.MacroRoute
					macRouteEntry = new com.cannontech.database.db.route.MacroRoute();

				macRouteEntry.setRouteID( macRoute.getRouteID() );
				macRouteEntry.setRouteOrder( new Integer(order++) );
				macRouteEntry.setSingleRouteID( trxRt.getRouteID() );

				macRoute.getMacroRouteVector().add( macRouteEntry );

				//remove the " Rt" from the route name
				// overwrite the route with a MACRO
				unxTrxToRtMap.put( 
					trxRt.getRouteName().substring(0,trxRt.getRouteName().lastIndexOf("#")).trim(),
					macRoute );					
			}
				
			if( macRoute.getMacroRouteVector().size() > 0 )
			{				
				multi.getDBPersistentVector().add( macRoute );
			}
	
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
 * Insert the method's description here.
 * Creation date: (6/11/2001 11:56:45 AM)
 * @return boolean
 */
public boolean processUnxPrograms() 
{	
	CTILogger.info("Starting UNIX Load Program Constraints file process...");
	getIMessageFrame().addOutput("Starting UNIX Load Program Constraints file process (" + unxConstrFileName + ")...");

	String aFileName = getFullFileName(unxConstrFileName);
	java.util.ArrayList lines = readFile(aFileName);
	
	//example KEY<CI_C4> VALUES<LMProgramConstraint>
	HashMap constNameMap = new HashMap(64);

	//example KEY<PROGRAM_NAME> VALUES< ArrayList(GROUP_NAME) >
	HashMap progNmToGrpNmMap = new HashMap(64);

	//example KEY<CMID> VALUES< LMProgramDirectGear >
	HashMap cmidToGearMap = new HashMap(64);



	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );
		
	for( int i = 0; i < lines.size(); i++ )
	{
		String[] line = lines.get(i).toString().split(",");

		//ignore title line
		if( i <= 1 || line.length <= 6 )
			continue;
		
		CTILogger.info("UNX_PROG_CONSTRAINTS line: " + lines.get(i).toString());

		LMProgramConstraint lmProgConst = new LMProgramConstraint();
		String constrName = line[0].trim();		
		lmProgConst.setConstraintName( constrName );
		lmProgConst.setConstraintID( new Integer(START_CONSTRAINT_ID++) );

		lmProgConst.setMaxDailyOps( pInt(line[1].trim()) );
		lmProgConst.setMaxActivateTime( pInt(line[2].trim()) );
		lmProgConst.setMaxHoursDaily( pInt(line[3].trim()) );
		lmProgConst.setMaxHoursSeasonal( pInt(line[4].trim()) );
		lmProgConst.setMinActivateTime( pInt(line[5].trim()) );
		lmProgConst.setMinRestartTime( pInt(line[6].trim()) );

		//need to flip these values
		StringBuffer days = new StringBuffer(line[7].trim().toUpperCase());
		for( int k = 0; k < days.length(); k++  )
		{
			if( days.charAt(k) == 'Y' )
				days.setCharAt( k, 'N');
			else if( days.charAt(k) == 'N' )
				days.setCharAt( k, 'Y');
		}
		lmProgConst.setAvailableWeekdays( days.toString() );



		multi.getDBPersistentVector().add( lmProgConst );
		constByNameMap.put( constrName, lmProgConst );
	}


	/* Start reading through our Program to Group mapping file */		
	String progToGrpFile = getFullFileName(unxPrgToGrpFileName);
	lines = readFile(progToGrpFile );
	for( int i = 0; i < lines.size(); i++ )
	{
		String[] line = lines.get(i).toString().split(",");

		//ignore title line
		if( i <= 1 || line.length <= 2 )
			continue;

		addToListMap(
			progNmToGrpNmMap,
			line[0].trim(),   //program name
			line[1].trim() + " " + line[2].trim() ); //group name
	}

	/* Start reading through our Program to Group mapping file */		
	String gearFile = getFullFileName(unxGearFileName);
	lines = readFile( gearFile );
	for( int i = 0; i < lines.size(); i++ )
	{
		String[] line = lines.get(i).toString().split(",");

		//ignore title line
		if( i <= 1 || line.length <= 5 )
			continue;

		/* Create a default gear */
		LMProgramDirectGear gear = 
			LMProgramDirectGear.createGearFactory(
				(line[5].trim().equalsIgnoreCase("cycl")
				 ? LMProgramDirectGear.CONTROL_SMART_CYCLE
				 : LMProgramDirectGear.CONTROL_TIME_REFRESH) );

		if( gear instanceof TimeRefreshGear )
		{
			gear.setGearName("Refresh");
			((TimeRefreshGear)gear).setShedTime( new Integer(900) ); //15 minutes
			((TimeRefreshGear)gear).setRefreshRate( new Integer(600) ); //10 minutes
			((TimeRefreshGear)gear).setGroupSelectionMethod(
				(line[6].trim().equalsIgnoreCase("least")
				 ? LMProgramDirectGear.SELECTION_LEAST_CONTROL_TIME
				 : LMProgramDirectGear.SELECTION_ALWAYS_FIRST_GROUP) );
		}
		else if( gear instanceof SmartCycleGear )
		{
			gear.setGearName("Cycle");
			((SmartCycleGear)gear).setControlPercent( new Integer(50) ); //50% cycle
			((SmartCycleGear)gear).setCyclePeriodLength( new Integer(30 * 60) ); //30 minutes
			((SmartCycleGear)gear).setStartingPeriodCnt( new Integer(8) ); //8 count
			((SmartCycleGear)gear).setResendRate( new Integer(60 * 60) ); //1 hour
		}
		
		//gear.setDeviceID( lmProgram.getPAObjectID() );
		gear.setMethodStopType( LMProgramDirectGear.STOP_TIME_IN );
		gear.setRampInPercent( new Integer(100) );
		gear.setRampOutPercent( new Integer(100) );
		gear.setRampInInterval( new Integer(300) );
		gear.setRampOutInterval( new Integer(300) );

		cmidToGearMap.put( line[0].trim(), gear );
	}


	/* Start reading through our Constraint to Program mapping file */		
	String progFile = getFullFileName(unxLGrpFileName);
	lines = readFile(progFile);
	for( int i = 0; i < lines.size(); i++ )
	{
		String[] line = lines.get(i).toString().split(",");

		//ignore title line
		if( i <= 1 || line.length <= 6 )
			continue;

		LMProgramDirect lmProgram =
			(LMProgramDirect)LMFactory.createLoadManagement( DeviceTypes.LM_DIRECT_PROGRAM );

		String progName = line[0].trim();
		String gearName = line[1].trim();
		lmProgram.setName( progName + " " +  gearName );
		lmProgram.getProgram().setControlType( LMProgramBase.OPSTATE_AUTOMATIC );

		//set our unique own deviceID
		lmProgram.setPAObjectID( new Integer(START_PROGRAM_ID++) );
		

		LMProgramDirectGear gear = (LMProgramDirectGear)cmidToGearMap.get( gearName );
		try
		{
			//must make a new instance/deep clone of original Gear since we are changing attributes
			// for each program that uses this gear
			LMProgramDirectGear realGear = 
				(LMProgramDirectGear)CtiUtilities.copyObject( gear );
	
			realGear.setDeviceID( lmProgram.getPAObjectID() );
			lmProgram.getLmProgramDirectGearVector().add( realGear );
		}
		catch( Exception ex ) {}		



		String constrName = line[5].trim();
		LMProgramConstraint constraint =
				(LMProgramConstraint)constByNameMap.get( constrName );

		if( constraint != null )
		{
			lmProgram.getProgram().setConstraintID( constraint.getConstraintID() );
		}


		String lgid = progName;
		ArrayList grpNmeList = (ArrayList)progNmToGrpNmMap.get( lgid );
		for( int j = 0; grpNmeList != null && j < grpNmeList.size(); j++ )
		{
			String grpName = (String)grpNmeList.get(j);
			if( grpName == null )
				continue;
			
			LMGroup grp = (LMGroup)unxGrpNameToGrpMap.get( grpName );
			if( grp == null )
				continue;

			LMProgramDirectGroup dirGrp = new LMProgramDirectGroup();
			dirGrp.setLmGroupDeviceID( grp.getPAObjectID() );
			dirGrp.setDeviceID( lmProgram.getPAObjectID() );
			dirGrp.setGroupOrder( new Integer(lmProgram.getLmProgramStorageVector().size()+1) );
			lmProgram.getLmProgramStorageVector().add( dirGrp );
		}

		multi.getDBPersistentVector().add( lmProgram );
		unxPrgNameMap.put( progName, lmProgram );
	}


	//boolean success = true;
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
 * Insert the method's description here.
 * Creation date: (6/11/2001 11:56:45 AM)
 * @return boolean
 */
public boolean processUnxLoadScenarios() 
{	
	CTILogger.info("Starting Unix Load Control Scenarios file process...");
	getIMessageFrame().addOutput("Starting Load Unix Control Scenarios file process (" + unxScenFileName + ")...");
	
	//example KEY<MAP1> VALUES<LMScenario>
	HashMap scenarioMap = new HashMap(128);


	String aFileName = getFullFileName(unxScenFileName);
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
		if( i <= 2 || line.length <= 3 )
			continue;
		
		CTILogger.info("UNIX_SCENARIO line: " + lines.get(i).toString());

		String scenName = line[0].trim();

		LMScenario lmScenario = null;
		if( !scenarioMap.containsKey(scenName) )
		{
			lmScenario = (LMScenario)LMFactory.createLoadManagement( PAOGroups.LM_SCENARIO );
			lmScenario.setScenarioName( scenName );
			scenarioMap.put( scenName, lmScenario );
			multi.getDBPersistentVector().add( lmScenario );
		}
		else
			lmScenario = (LMScenario)scenarioMap.get( scenName );
		

		
		LMProgramDirect prog = (LMProgramDirect)unxPrgNameMap.get( line[1].trim() );
		if( prog == null )
			continue;
		
		//set our unique own deviceID
		lmScenario.setScenarioID( new Integer(START_SCENARIO_ID++) );


		LMControlScenarioProgram progScen = new LMControlScenarioProgram();
		progScen.setScenarioID( lmScenario.getScenarioID() );
		progScen.setProgramID( prog.getPAObjectID() );
		progScen.setStartGear( new Integer(1) );
		progScen.setStartOffset( decodeFunkySeconds(line[2].trim()) );
		progScen.setStopOffset( decodeFunkySeconds(line[3].trim()) );
		lmScenario.getAllThePrograms().add( progScen );
	}

	//try to calculate the start and stop offset
	Iterator it = scenarioMap.values().iterator();	
	while( it.hasNext() )
	{
		LMScenario scen = (LMScenario)it.next();
		
		setScenarioOffset( scen, true );
		setScenarioOffset( scen, false );		
	}




	boolean success = writeToSQLDatabase(multi);

	if( success )
	{
		CTILogger.info(" Unix Load Control Scenarios file was processed and inserted Successfully");
		getIMessageFrame().addOutput("Unix Load Control Scenarios file was processed and inserted Successfully");
		
		CTILogger.info(" " + multi.getDBPersistentVector().size() + " Load Control Scenarios were added to the database");
		getIMessageFrame().addOutput(multi.getDBPersistentVector().size() + " Load Control Scenarios were added to the database");
	}
	else
		getIMessageFrame().addOutput(" Load Control Scenarios failed adding to the database");


	return success;
}

private int setScenarioOffset( LMScenario scen, boolean isStart )
{
	int minVal = -1;
	
	for( int i = 0; i < scen.getAllThePrograms().size(); i++ )
	{
		LMControlScenarioProgram progScen = 
			(LMControlScenarioProgram)scen.getAllThePrograms().get(i);

		int currSecs = 
			(isStart ? progScen.getStartOffset().intValue()
			: progScen.getStopOffset().intValue());
		
		if( minVal < 0 || currSecs < minVal )
			minVal = currSecs;
	}
	
	for( int i = 0; i < scen.getAllThePrograms().size(); i++ )
	{
		LMControlScenarioProgram progScen =
			(LMControlScenarioProgram)scen.getAllThePrograms().get(i);

		if( isStart )
		{
			if( progScen.getStartOffset().intValue() == minVal )
				progScen.setStartOffset( new Integer(0) );
			else
				progScen.setStartOffset( new Integer(
					progScen.getStartOffset().intValue() - minVal) );
		}
		else
		{
			if( progScen.getStopOffset().intValue() == minVal )
				progScen.setStopOffset( new Integer(0) );
			else
				progScen.setStopOffset( new Integer(
					progScen.getStopOffset().intValue() - minVal) );
		}

	}
	
	return minVal;
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2001 11:56:45 AM)
 * @return boolean
 */
public boolean processUnxLoadGroups() 
{	
	CTILogger.info("Starting UNIX Load Group file process...");
	getIMessageFrame().addOutput("Starting UNIX Load Group file process (" + unxCGrpFileName + ")...");

	//create an object to hold all of our DBPersistant objects
	MultiDBPersistent multi = new MultiDBPersistent();
	multi.setCreateNewPAOIDs( false );

	//example KEY<CGID> VALUES< LMGroup >
	HashMap latchingGrpMap = new HashMap(32);
	
	//example KEY<CGID> VALUES< TID >
	HashMap rtToGrpMap = new HashMap(32);
	
	//example KEY<cgid> VALUES< LMGroup >
	HashMap tempGrpNameToGrpMap = new HashMap(32);
	

	/* Start reading through our GroupName to Group mapping file */		
	String grpNameToGrpFile = getFullFileName(unxCGrpFileName);
	java.util.ArrayList grpLines = readFile(grpNameToGrpFile);

	if( grpLines == null )
		return true; //continue the process

	for( int i = 0; i < grpLines.size(); i++ )
	{
		String[] line = grpLines.get(i).toString().split(",");

		//ignore title line
		if( i <= 1 || line.length <= 5 )
			continue;

		CTILogger.info("UNX_LOAD_GRP line: " + grpLines.get(i).toString());

		String cgid = line[0].trim();
		String typeStr = line[2].trim();
		LMGroup lmGroup = null;
		if( typeStr.equalsIgnoreCase("SA205") )
		{
			LMGroupSA205 temp =
				(LMGroupSA205)LMFactory.createLoadManagement( DeviceTypes.LM_GROUP_SA205 );
			
			String lNum = getSAFunction( pInt(line[1].trim()).intValue() );
			temp.getLMGroupSA205105().setLoadNumber( lNum );

			lmGroup = temp;
		}			
		else if( typeStr.equalsIgnoreCase("GOLAY") || typeStr.equalsIgnoreCase("GOLAT") )
		{
			LMGroupGolay temp =
				(LMGroupGolay)LMFactory.createLoadManagement( DeviceTypes.LM_GROUP_GOLAY );

			temp.getLMGroupSASimple().setNominalTimeout(
					new Integer(pInt(line[3].trim()).intValue()) );

			lmGroup = temp;
		}
		else if( typeStr.equalsIgnoreCase("SADIG") || typeStr.equalsIgnoreCase("SALAT") )
		{
			LMGroupSADigital temp =
				(LMGroupSADigital)LMFactory.createLoadManagement( DeviceTypes.LM_GROUP_SADIGITAL );

			temp.getLMGroupSASimple().setNominalTimeout(
					new Integer(pInt(line[3].trim()).intValue()) );

			lmGroup = temp;
		}
		else
		{
			CTILogger.error("  **** Unknown Load Group type found = " + typeStr );
			continue;
		}
		

		if( typeStr.endsWith("LAT") ) //remember these cowboys
			latchingGrpMap.put( cgid, lmGroup );

		tempGrpNameToGrpMap.put( cgid, lmGroup );
	}

	
	String aFileName = getFullFileName(unxCgcGroupFileName);
	java.util.ArrayList lines = readFile(aFileName);
	for( int i = 0; i < lines.size(); i++ )
	{
		String[] line = lines.get(i).toString().split(",");

		//ignore title line
		if( i <= 1 || line.length <= 3 )
			continue;

		String grpCgid = line[0].trim();
		String code = line[1].trim();

		//get the stored Group object
		LMGroup grp = (LMGroup)tempGrpNameToGrpMap.get( grpCgid );
		if( grp == null )
			continue;
		
		try
		{
			//deep copy since we will be storing this object in a List
			LMGroup newGroup = (LMGroup)CtiUtilities.copyObject( grp );
			newGroup.setPAOName( grpCgid + " " + code );

			if( newGroup instanceof LMGroupSA205 )
			{
				((LMGroupSA205)newGroup).getLMGroupSA205105().setOperationalAddress( new Integer(code) );
			}			
			else if( newGroup instanceof LMGroupGolay )
			{
				((LMGroupGolay)newGroup).getLMGroupSASimple().setOperationalAddress( code.toString() );
			}
			else if( newGroup instanceof LMGroupSADigital )
			{
				((LMGroupSADigital)newGroup).getLMGroupSASimple().setOperationalAddress( code.toString() );
			}
			else
				throw new IllegalArgumentException("Unknown Load Group INSTANCE type found = " + newGroup.getClass().getName() );

			
			//set our unique deviceID
			newGroup.setDeviceID( new Integer(START_GROUP_ID++) );

			unxGrpNameToGrpMap.put( newGroup.getPAOName(), newGroup );
			multi.getDBPersistentVector().add( newGroup );
		}
		catch( Exception ex )
		{
			CTILogger.error( "Unable to copy LMGroup", ex );
		}
	}


	String cgtFileName = getFullFileName(unxCGTFileName);
	java.util.ArrayList cgtLines = readFile(cgtFileName);
	for( int i = 0; i < cgtLines.size(); i++ )
	{
		String[] line = cgtLines.get(i).toString().split(",");		

		//ignore title line
		if( i <= 2 || line.length <= 1 )
			continue;

		rtToGrpMap.put( line[1].trim(), line[0].trim() );
	}


	//assign the correct route ID to each LMGroup
	for( int i = 0; i < multi.getDBPersistentVector().size(); i++ )
	{
		LMGroup grp = (LMGroup)multi.getDBPersistentVector().get(i);

		String cgid = 
			grp.getPAOName().substring(0,
			grp.getPAOName().lastIndexOf(" "));

		String tid = (String)rtToGrpMap.get( cgid );
		
		if( tid != null )
		{
			//special case for SADigital
			if( grp instanceof LMGroupSADigital 
				&& unxTrxToGroupIntMap.get(tid) != null)
			{
				int[] indxInts = (int[])unxTrxToGroupIntMap.get(tid);

				((LMGroupSADigital)grp).getLMGroupSASimple().setSpaceIndex( new Integer(indxInts[0]) );
				((LMGroupSADigital)grp).getLMGroupSASimple().setMarkIndex( new Integer(indxInts[1]) );
			}
			
			RouteBase rtBase = (RouteBase)unxTrxToRtMap.get(tid);

			if( rtBase != null )
				((IGroupRoute)grp).setRouteID( rtBase.getRouteID() );
		}
		
		if( latchingGrpMap.containsKey(cgid) )
		{
			grp.setPAOName( "LATCH: " + grp.getPAOName() );
		}



		if( ((IGroupRoute)grp).getRouteID() == null )
		{
			//no route it
			grp.setPAOName( "$_" + grp.getPAOName() );
			
			Integer dummyRtID = new Integer(START_ROUTE_ID-1);
			((IGroupRoute)grp).setRouteID( dummyRtID );			
			CTILogger.error( "  ***  NO Route for: " + grp.toString() +
					", using RouteID = " + dummyRtID );
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

private String getGroupCode( LMGroup grp )
{
	if( grp instanceof LMGroupSA205 )
	{
		return ((LMGroupSA205)grp).getLMGroupSA205105().getOperationalAddress().toString();
	}			
	else if( grp instanceof LMGroupGolay )
	{
		return ((LMGroupGolay)grp).getLMGroupSASimple().getOperationalAddress();
	}
	else if( grp instanceof LMGroupSADigital )
	{
		return ((LMGroupSADigital)grp).getLMGroupSASimple().getOperationalAddress();
	}
	else
		throw new IllegalArgumentException("Unknown Load Group INSTANCE type found = " + grp.getClass().getName() );
}

private String getSAFunction( int lNum )
{
	return
		(lNum==1 ? IlmDefines.SA_LOAD_1 :
		(lNum==2 ? IlmDefines.SA_LOAD_2 :
		(lNum==3 ? IlmDefines.SA_LOAD_3 :
		(lNum==4 ? IlmDefines.SA_LOAD_4 :
		(lNum==5 ? IlmDefines.SA_LOAD_TEST : CtiUtilities.STRING_NONE
		)))));
}

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