package com.cannontech.logger;

/**
 * Insert the type's description here.
 * Creation date: (5/9/00 2:52:08 PM)
 * @author: 
 * @Version: <version>
 */
import java.util.Enumeration;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.*;
import java.util.Enumeration;
import com.cannontech.message.util.Message;
import com.cannontech.database.SqlStatement;
import com.cannontech.clientutils.CommonUtils;

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
	reg.setAppName("Logger Service");
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
/*	System.out.println("DATABASE CHANGE RECEIVED DB = "+ msg.getDatabase() );
	System.out.println("				     DbType = " + msg.getDBType() );
	System.out.println("					  	 ID = " + msg.getID() );
	System.out.println("	  			   Priority = " + msg.getPriority() );
	System.out.println("	  	  			   Time = " + msg.getTimeStamp() );
*/
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/00 3:02:24 PM)
 * @param mpc com.cannontech.message.dispatch.message.Multi
 */
public void receivedNullMsg()
{
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/00 3:02:24 PM)
 * @param mpc com.cannontech.message.dispatch.message.Multi
 */
public void receivedPointData( PointData point ) 
{	
/*	System.out.println("POINTDATA RECEIVED -- PointID = " + point.getId() );
	System.out.println("					  	Value = " + point.getValue() );
	System.out.println("					   Forced = " + point.getForced() );
	System.out.println("					  	Limit = " + point.getLimit() );
	System.out.println("					   Offset = " + point.getOffset() );
	System.out.println("					 Priority = " + point.getPriority() );
	System.out.println("					  Quality = " + point.getQuality() );
	System.out.println("					  	  Sig = " + ((point.getSig()==null) ? "null" : "NOT NULL") );
	System.out.println("					      Str = " + point.getStr() );
	System.out.println("					  	 Tags = " + point.getTags() );
	System.out.println("					  	 Time = " + point.getTime() );
	System.out.println("					TimeStamp = " + point.getTimeStamp() );
	System.out.println("					  	Type  = " + point.getType() );
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
/*	System.out.println("SIGNAL RECEIVED for ptID = "+ point.getId() );
	System.out.println(" 	  	  classification = "+ point.getClassification());
	System.out.println(" 	  	  		  Action = "+ point.getAction());
	System.out.println(" 	  	  			Flag = "+ point.getFlag());
	System.out.println(" 	  	  		 LogType = "+ point.getLogType());
	System.out.println(" 	  	  		Priority = "+ point.getPriority());
	System.out.println(" 	  	  			 SOE = "+ point.getSoeTag());
	System.out.println(" 	  	  			 Str = "+ point.getStr());
	System.out.println(" 	  	  	   TimeStamp = "+ point.getTimeStamp());
	System.out.println(" 	  	  			User = "+ point.getUser());
*/

	Object[] names = getDeviceNameAndPointName( point.getId() );		
	
	// make sure we have a device name and point name
	if( names[0] != null && names[1] != null )
	{
		outStream.printTextLine(
			CommonUtils.formatString( CommonUtils.formatDate( point.getTimeStamp() ), Logger.TIMESTAMP_LENGTH ) + " " +
			CommonUtils.formatString( names[0].toString(), Logger.DEVICENAME_LENGTH ) + " " +
			CommonUtils.formatString( names[1].toString(), Logger.POINTNAME_LENGTH ) + " " +
			CommonUtils.formatString( point.getDescription(), Logger.DESCRIPTION_LENGTH ) + " " +
			CommonUtils.formatString( point.getAction(), Logger.ACTION_LENGTH ),
			point.getAlarmStateID() );
	}
		
}
}
