package com.cannontech.logger;

/**
 * Insert the type's description here.
 * Creation date: (5/9/00 2:52:08 PM)
 * @author: 
 * @Version: <version>
 */
import java.util.Enumeration;

import com.cannontech.clientutils.CommonUtils;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.PointRegistration;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.message.util.Message;

public class LoggerClient extends com.cannontech.clientutils.ClientBase
{
	// points we want to register for
	private Enumeration pointEnum = null;
	private Logger outStream = null;
		
	private ClientConnection connection = null;
	private Thread runningThread = null;
	

	//Default host string
	public static String HOST = "127.0.0.1";
	public static int PORT = 1510;
	public static final String DBALIAS = "yukon";

/**
 * Client constructor comment.
 */
public LoggerClient( Enumeration pointEnum, Logger stream ) 
{
	super();
	this.outStream = stream;
	this.pointEnum = pointEnum;
}
/**
 * This method was created in VisualAge.
 */
public Message buildRegistrationMessage() 
{		
	//First do a registration
	Registration reg = new Registration();
	reg.setAppName( CtiUtilities.getApplicationName() );
	reg.setAppIsUnique(0);
	reg.setAppKnownPort(0);
	reg.setAppExpirationDelay( 1000000 );

	// create our own point registration
	PointRegistration pReg = new PointRegistration();	
	pReg.setRegFlags( outStream.getPointReg() );

	Multi multiReg = new Multi();
	multiReg.getVector().addElement( reg );
	multiReg.getVector().addElement( pReg );

	return multiReg;
}
/**
 * Insert the method's description here.
 * Creation date: (5/15/00 5:08:21 PM)
 * Version: <version>
 * @return java.lang.String
 * @param type int
 */
private String determinePointValue( PointData point ) 
{
	java.text.DecimalFormat formatter = 
				new java.text.DecimalFormat("####.##");

	if( point.getType() == com.cannontech.database.data.point.PointTypes.STATUS_POINT )
	{
		
		return null;
	}
	else
		return formatter.format( point.getValue() );	
}
/**
 * Insert the method's description here.
 * Creation date: (5/16/00 1:29:25 PM)
 * Version: <version>
 * @return java.lang.Object[]
 */
private Object[] getDeviceNameAndPointName( long id )
{
	SqlStatement statement = new SqlStatement(
		"select y.paoname, p.pointname " +
		"from YukonPAObject y, Point p where p.pointid = " + id +
		" and y.PAObjectID = p.PAObjectID", 
		DBALIAS );

	try
	{
		statement.execute();
	}
	catch( com.cannontech.common.util.CommandExecutionException ex )
	{
		return null;
	}

	Object[] values = new Object[2];

	try
	{
		values[0] = statement.getRow(0)[0]; // assign device name
	}
	catch ( NullPointerException ex )
	{
		values[0] = "NO DEVICE NAME FOUND";
	}

	try
	{	
		values[1] = statement.getRow(0)[1]; // assign point name
	}	
	catch ( NullPointerException ex )
	{
		values[1] = "NO POINT NAME FOUND";
	}

	
	return values;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/00 3:02:24 PM)
 * @param mpc com.cannontech.message.dispatch.message.Multi
 */
public void receivedDBChangMsg( DBChangeMsg msg ) 
{
/*	com.cannontech.clientutils.CTILogger.info("DATABASE CHANGE RECEIVED DB = "+ msg.getDatabase() );
	com.cannontech.clientutils.CTILogger.info("				     DbType = " + msg.getDBType() );
	com.cannontech.clientutils.CTILogger.info("					  	 ID = " + msg.getID() );
	com.cannontech.clientutils.CTILogger.info("	  			   Priority = " + msg.getPriority() );
	com.cannontech.clientutils.CTILogger.info("	  	  			   Time = " + msg.getTimeStamp() );
*/
}

/**
 * Insert the method's description here.
 * Creation date: (3/22/00 3:02:24 PM)
 * @param mpc com.cannontech.message.dispatch.message.Multi
 */
public void receivedPointData( PointData point ) 
{	
/*	com.cannontech.clientutils.CTILogger.info("POINTDATA RECEIVED -- PointID = " + point.getId() );
	com.cannontech.clientutils.CTILogger.info("					  	Value = " + point.getValue() );
	com.cannontech.clientutils.CTILogger.info("					   Forced = " + point.getForced() );
	com.cannontech.clientutils.CTILogger.info("					  	Limit = " + point.getLimit() );
	com.cannontech.clientutils.CTILogger.info("					   Offset = " + point.getOffset() );
	com.cannontech.clientutils.CTILogger.info("					 Priority = " + point.getPriority() );
	com.cannontech.clientutils.CTILogger.info("					  Quality = " + point.getQuality() );
	com.cannontech.clientutils.CTILogger.info("					  	  Sig = " + ((point.getSig()==null) ? "null" : "NOT NULL") );
	com.cannontech.clientutils.CTILogger.info("					      Str = " + point.getStr() );
	com.cannontech.clientutils.CTILogger.info("					  	 Tags = " + point.getTags() );
	com.cannontech.clientutils.CTILogger.info("					  	 Time = " + point.getTime() );
	com.cannontech.clientutils.CTILogger.info("					TimeStamp = " + point.getTimeStamp() );
	com.cannontech.clientutils.CTILogger.info("					  	Type  = " + point.getType() );
*/
	//receivedSignal( point.getSig() );

	Object[] names = getDeviceNameAndPointName( point.getId() );		

	outStream.printTextLine(
		CommonUtils.formatDate( point.getPointDataTimeStamp() ) + " " +
		CommonUtils.formatString( names[0].toString(), Logger.DEVICENAME_LENGTH ) + " " +
		CommonUtils.formatString( names[1].toString(), Logger.POINTNAME_LENGTH ) + " " +
		CommonUtils.formatString( determinePointValue( point ), Logger.DESCRIPTION_LENGTH ) + " " +
		CommonUtils.formatString( com.cannontech.database.data.point.PointTypes.getType(point.getType()), Logger.TYPE_LENGTH ),
		0l );
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/00 3:02:24 PM)
 * @param mpc com.cannontech.message.dispatch.message.Multi
 */
public void receivedSignal( Signal point ) 
{
/*	com.cannontech.clientutils.CTILogger.info("SIGNAL RECEIVED for ptID = "+ point.getId() );
	com.cannontech.clientutils.CTILogger.info(" 	  	  classification = "+ point.getClassification());
	com.cannontech.clientutils.CTILogger.info(" 	  	  		  Action = "+ point.getAction());
	com.cannontech.clientutils.CTILogger.info(" 	  	  			Flag = "+ point.getFlag());
	com.cannontech.clientutils.CTILogger.info(" 	  	  		 LogType = "+ point.getLogType());
	com.cannontech.clientutils.CTILogger.info(" 	  	  		Priority = "+ point.getPriority());
	com.cannontech.clientutils.CTILogger.info(" 	  	  			 SOE = "+ point.getSoeTag());
	com.cannontech.clientutils.CTILogger.info(" 	  	  			 Str = "+ point.getStr());
	com.cannontech.clientutils.CTILogger.info(" 	  	  	   TimeStamp = "+ point.getTimeStamp());
	com.cannontech.clientutils.CTILogger.info(" 	  	  			User = "+ point.getUser());
*/

	Object[] names = getDeviceNameAndPointName( point.getPointID() );		
	
	// make sure we have a device name and point name
	if( names[0] != null && names[1] != null )
	{
		outStream.printTextLine(
			CommonUtils.formatString( CommonUtils.formatDate( point.getTimeStamp() ), Logger.TIMESTAMP_LENGTH ) + " " +
			CommonUtils.formatString( names[0].toString(), Logger.DEVICENAME_LENGTH ) + " " +
			CommonUtils.formatString( names[1].toString(), Logger.POINTNAME_LENGTH ) + " " +
			CommonUtils.formatString( point.getDescription(), Logger.DESCRIPTION_LENGTH ) + " " +
			CommonUtils.formatString( point.getAction(), Logger.ACTION_LENGTH ),
			point.getCategoryID() );
	}
		
}
}
