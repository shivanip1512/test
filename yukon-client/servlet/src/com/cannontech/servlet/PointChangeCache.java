package com.cannontech.servlet;

/**
 * Insert the type's description here.
 * Creation date: (3/31/00 12:52:09 PM)
 * @author: 
 */
public class PointChangeCache  implements Runnable, java.util.Observer {
	public static String SERVLET_CONTEXT_ID = "pointchangecache";
	
	private com.cannontech.message.dispatch.ClientConnection conn = null;
	
	private java.util.Hashtable pointData = new java.util.Hashtable();
	private java.util.Hashtable signalData = new java.util.Hashtable();
	
	private com.cannontech.common.util.LogWriter logger = null;
	private Thread runner = null;

	//Date of the last point change received from dispatch
	private java.util.Date lastChange = null;
/**
 * SingletonVangoghConnection constructor comment.
 */
protected PointChangeCache()
{
	super();
	try
	{
		java.io.FileOutputStream out = new java.io.FileOutputStream("pcache.log");
		java.io.PrintWriter writer = new java.io.PrintWriter(out, true);
		logger = 
			new com.cannontech.common.util.LogWriter(
				"PointChangeCache", 
				com.cannontech.common.util.LogWriter.DEBUG, 
				writer); 

		logger.log("Starting up....", com.cannontech.common.util.LogWriter.INFO);
	}
	catch (java.io.FileNotFoundException e)
	{
		e.printStackTrace();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/7/00 11:16:29 AM)
 */
public synchronized void connect() 
{
	String vgHost = "localhost";
	int vgPort = 1510;

	//figure out where vangogh is
		java.io.InputStream is = getClass().getResourceAsStream("/config.properties");
		java.util.Properties props = new java.util.Properties();
		try
		{
			props.load(is);
		}
		catch (Exception e)
		{
			logger.log("Can't read the properties file. " + "Make sure db.properties is in the CLASSPATH", com.cannontech.common.util.LogWriter.ERROR);
			return;
		}
		vgHost = props.getProperty("vangogh_machine");
		String portStr = props.getProperty("vangogh_port");
		try
		{
			vgPort = Integer.parseInt(portStr);
		}
		catch (NumberFormatException ne)
		{
			logger.log("Unable to determine vangog_port", com.cannontech.common.util.LogWriter.ERROR);
			return;
		}
		
		logger.log("Will attempt to connect to vangogh @" + vgHost + ":" + vgPort, com.cannontech.common.util.LogWriter.INFO);
		conn = new com.cannontech.message.dispatch.ClientConnection();
		conn.addObserver(this);
		conn.setHost(vgHost);
		conn.setPort(vgPort);
		com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
		reg.setAppName("PointChangeCache " + (new java.util.Date()).getTime());
		reg.setAppIsUnique(0);
		reg.setAppKnownPort(0);
		reg.setAppExpirationDelay(5000);
		com.cannontech.message.dispatch.message.PointRegistration pReg = new com.cannontech.message.dispatch.message.PointRegistration();
		pReg.setRegFlags(com.cannontech.message.dispatch.message.PointRegistration.REG_ALL_PTS_MASK |
							com.cannontech.message.dispatch.message.PointRegistration.REG_ALARMS );
		com.cannontech.message.dispatch.message.Multi multi = new com.cannontech.message.dispatch.message.Multi();
		multi.getVector().addElement(reg);
		multi.getVector().addElement(pReg);
		conn.setRegistrationMsg(multi);
		conn.setAutoReconnect(true);
		conn.setTimeToReconnect(30);
logger.log("about to attempt connection....", com.cannontech.common.util.LogWriter.ERROR );
	try
	{
		conn.connectWithoutWait();
	}
	catch( java.io.IOException io )
	{
		io.printStackTrace();
		logger.log("An error occured connecting with van gogh", com.cannontech.common.util.LogWriter.ERROR );
	}
logger.log("returned from connection attempt", com.cannontech.common.util.LogWriter.ERROR );
	if( runner == null )
	{
		runner = new Thread(this);
		runner.start();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/7/00 11:19:05 AM)
 */
public synchronized void disconnect() 
{
	try
	{
		conn.disconnect();
		conn.deleteObservers();
	}
	catch( java.io.IOException io )
	{
		io.printStackTrace();
		logger.log("An error occured connecting with van gogh", com.cannontech.common.util.LogWriter.ERROR );
	}

	//interrupt the runner thread and join with it
	if( runner != null )
	{
		runner.interrupt();

		try
		{
			runner.join();			
		}
		catch( InterruptedException ie )
		{
			ie.printStackTrace();
		}
		
		runner = null;
	}	
}
/**
 * Insert the method's description here.
 * Creation date: (9/13/00 1:44:18 PM)
 * @return java.util.Date
 */
public java.util.Date getLastChange() {
	return lastChange;
}
/**
 * Insert the method's description here.
 * Creation date: (3/31/00 1:03:43 PM)
 * @return com.cannontech.servlet.PointData
 * @param pointId long
 */
public com.cannontech.message.dispatch.message.Signal getSignal(long pointId) {
	return (com.cannontech.message.dispatch.message.Signal) signalData.get( new Long(pointId) );
}
/**
 * Insert the method's description here.
 * Creation date: (3/31/00 1:03:43 PM)
 * @return com.cannontech.servlet.PointData
 * @param pointId long
 */
public String getState(long pointId, double value, String dbAlias) 	
{
	com.cannontech.message.dispatch.message.PointData pData = (com.cannontech.message.dispatch.message.PointData) pointData.get( new Long(pointId) );

	if( pData != null )
	{
		return retrieveState( (int) pData.getId(), pData.getValue(), dbAlias );
	}
	else
	{
		return "-";
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/31/00 1:03:43 PM)
 * @return com.cannontech.servlet.PointData
 * @param pointId long
 */
public com.cannontech.message.dispatch.message.PointData getValue(long pointId) {
	return (com.cannontech.message.dispatch.message.PointData) pointData.get( new Long(pointId) );
}
/**
 * Insert the method's description here.
 * Creation date: (12/27/2000 5:06:48 PM)
 * @param msg com.cannontech.message.util.Message
 */
private void handleMessage(com.cannontech.message.util.Message msg)
{
	if (msg instanceof com.cannontech.message.dispatch.message.Multi)
	{
		java.util.Vector inMessages = ((com.cannontech.message.dispatch.message.Multi) msg).getVector();
		java.util.Iterator iter = inMessages.iterator();
		while( iter.hasNext() )		
			handleMessage( (com.cannontech.message.util.Message) iter.next() );				
	}
	else
	if (msg instanceof com.cannontech.message.dispatch.message.PointData)
	{
		Long id = new Long(((com.cannontech.message.dispatch.message.PointData) msg).getId());		
		pointData.put(id, msg);		
		
		logger.log("Received point data for point id:  " + id, com.cannontech.common.util.LogWriter.INFO);
	}
	else
	if (msg instanceof com.cannontech.message.dispatch.message.Signal)
	{		
		com.cannontech.message.dispatch.message.Signal signal =
			(com.cannontech.message.dispatch.message.Signal) msg;

		Long id = new Long( signal.getId() );

		logger.log("Received signal id:  " + id + " tags:  " + signal.getTags() , com.cannontech.common.util.LogWriter.DEBUG);
		
			
		if( (signal.getTags() & com.cannontech.message.dispatch.message.Signal.MASK_ANY_ALARM) != 0 )
		{						
			signalData.put( id, signal );
			logger.log("Storing signal", com.cannontech.common.util.LogWriter.DEBUG );
		}
		else
		{
			signalData.remove( id );
			logger.log("Removing signal", com.cannontech.common.util.LogWriter.DEBUG);
		
		}		
	}
	else
	{
		logger.log("PointChangeCache received an unknown message of class:  " + msg.getClass(), com.cannontech.common.util.LogWriter.DEBUG );
	}
	
	lastChange = msg.getTimeStamp();
}
/**
 * Insert the method's description here.
 * Creation date: (3/31/00 12:57:17 PM)
 * @param config javax.servlet.ServletConfig
 * @exception javax.servlet.ServletException The exception description.
 */
public void init(javax.servlet.ServletConfig config) throws javax.servlet.ServletException 
{
	synchronized (this)
	{		
		try
		{
			java.io.FileOutputStream out = new java.io.FileOutputStream("pchange.log", true);
			java.io.PrintWriter writer = new java.io.PrintWriter(out, true);
			logger = new com.cannontech.common.util.LogWriter("PointChangeCache", com.cannontech.common.util.LogWriter.DEBUG, writer);

			logger.log("Starting up....", com.cannontech.common.util.LogWriter.INFO );
		}
		catch( java.io.FileNotFoundException e )
		{
			e.printStackTrace();
		}		
	}

	
	String host;
	Integer port;
	
	//figure out where vangogh is
	 java.io.InputStream is = getClass().getResourceAsStream("/config.properties");
	  java.util.Properties props = new java.util.Properties();
	  try
	  {
		 props.load(is);
	  }
	  catch (Exception e)
	  {
		 logger.log("Can't read the properties file. " +
			"Make sure db.properties is in the CLASSPATH",
			com.cannontech.common.util.LogWriter.ERROR);
		 return;
	  }
	 
	
	  host = props.getProperty("vangogh_machine");
	  String portStr = props.getProperty("vangogh_port");
		
		try { 
	    	port = new Integer(portStr);	    
	    }
		catch( NumberFormatException ne) 
		{
		 	logger.log("Unable to determine vangog_port", com.cannontech.common.util.LogWriter.ERROR );
		 	return;
		}
	
	
	logger.log("Will attempt to connect to vangogh @" + host + ":" + port, com.cannontech.common.util.LogWriter.INFO );
	
	conn = new com.cannontech.message.dispatch.ClientConnection();
	conn.addObserver(this);
	conn.setHost(host);
	conn.setPort(port.intValue());

	com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
	reg.setAppName("com.cannontech.servlet.PointData " + (new java.util.Date()).getTime());
	reg.setAppIsUnique(0);
	reg.setAppKnownPort(0);
	reg.setAppExpirationDelay( 1000000 );
	
	com.cannontech.message.dispatch.message.PointRegistration pReg = new com.cannontech.message.dispatch.message.PointRegistration();
	pReg.setRegFlags( com.cannontech.message.dispatch.message.PointRegistration.REG_ALL_PTS_MASK);

	com.cannontech.message.dispatch.message.Multi multi = new com.cannontech.message.dispatch.message.Multi();

	multi.getVector().addElement(reg);
	multi.getVector().addElement(pReg);
	
	conn.setRegistrationMsg(multi);
		
	conn.setAutoReconnect(true);
	conn.setTimeToReconnect(15);

	// Wait until connect() is called to connect
}
/**
 * Insert the method's description here.
 * Creation date: (3/30/00 4:33:36 PM)
 * @param pData com.cannontech.message.dispatch.message.PointData
 */
private void insertValue(com.cannontech.message.dispatch.message.PointData pData) 
{
	Long id = new Long(pData.getId());

	pointData.put(id, pData );
}
/**
 * Creation date: (1/23/2002 4:41:41 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
PointChangeCache pcc = new PointChangeCache();
	while(true) {
		pcc.retrieveState(458, 0.0, "yukon");
	}
		
}
/**
 * Insert the method's description here.
 * Creation date: (1/3/2001 12:47:01 PM)
 * @return java.lang.String
 * @param pointid int
 * @param value double
 */
private synchronized String retrieveState(int pointid, double value, String dbAlias) 
{
	String sql = "SELECT State.Text FROM Point,StateGroup,State WHERE State.StateGroupID=StateGroup.StateGroupID AND StateGroup.StateGroupID=Point.StateGroupID AND State.RawState=" + value + " AND " + "Point.PointID=" + pointid;
	String state = "-";
	
	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;

	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
		stmt = conn.createStatement();
		rset = stmt.executeQuery(sql);

		if( rset.next() )
		{
			state = rset.getString(1);			
		}
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
	}
	finally
	{
		try
		{
			if( stmt != null ) stmt.close();
			if( conn != null ) conn.close();
		} catch( Exception e ) { }
	}

	return state;
}
/**
 * Insert the method's description here.
 * Creation date: (3/30/00 4:27:52 PM)
 */
public void run()
{		
	try
	{
		while(true)
		{
			Object in;
			while( (in = conn.read(0)) != null )
			{
				if( in instanceof com.cannontech.message.util.Message )
				{
					handleMessage( (com.cannontech.message.util.Message) in );
				}
				else
				{
					logger.log("PointChangeCache received an unknown message of class:  " + in.getClass(), com.cannontech.common.util.LogWriter.INFO );
				}
			}
			
			Thread.sleep(1000);

			if( !conn.isValid() )						
				logger.log("Connect to dispatch is down...", com.cannontech.common.util.LogWriter.INFO );			
		}
	}
	catch( InterruptedException ie )
	{
		logger.log("Closing connection to dispatch", com.cannontech.common.util.LogWriter.INFO );		
	}
	finally
	{
		try
		{
			conn.disconnect();
		}
		catch( java.io.IOException ioe )
		{
			logger.log("Error disconnecting with vangogh occured", com.cannontech.common.util.LogWriter.ERROR);
		}

		logger.getPrintWriter().flush();
	}
}
/**
 * Updates from the client connect show up here
 * Creation date: (3/30/00 5:15:08 PM)
 * @param obs java.util.Observable
 * @param val java.lang.Object
 */
public void update(java.util.Observable obs, Object val) 
{
	if( obs instanceof com.cannontech.message.dispatch.ClientConnection )
	{
		if( conn.isValid() )
			logger.log("Connection established to " + conn.getHost() + ":" + conn.getPort(), com.cannontech.common.util.LogWriter.INFO );
		else
			logger.log("Connection to " + conn.getHost() + ":" + conn.getPort() + " is down", com.cannontech.common.util.LogWriter.INFO);
	}
}
}
