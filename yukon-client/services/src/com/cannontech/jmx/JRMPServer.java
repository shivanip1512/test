package com.cannontech.jmx;

import java.sql.*;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.util.MBeanUtil;

/**
 * @author rneuharth
 *
 * Independent coupled implementation of a JMX server.
 * 
 * Currently dynamically loads classes from:
 *   MX4J 1.1.1
 * 
 */
public class JRMPServer
{
	// Create a MBeanServer
	
	// The domain for all CTI services in the JMX domain
	//public static final String CTI_DOMAIN = "CTI-WServer:name=";


	/**
	 * Default constructor for the server
	 */
	public JRMPServer()
	{
		super();
	}
	
	/**
	 * Start CTI MBean services here
	 */
	private void loadCustomServices()
	{
		String sql = 
			"SELECT ServiceID, ServiceName, ServiceClass, ParamNames, ParamValues " + 
			"FROM YukonServices " +
			"WHERE ServiceID > 0";
   		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
        MBeanServer server = MBeanUtil.getInstance();
		
		try 
		{
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sql);
			
			int cnt = 0;
			while( rset.next() ) 
			{
				try
				{
					ObjectName name = new ObjectName( ":name=" + rset.getString(2).trim() );
					server.createMBean( rset.getString(3).trim(), name, null);
					
					//better have a start() method defined in the class!
					server.invoke(name, "start", null, null);
					
					CTILogger.info( 
						" SUCCESSFUL start of the " + rset.getString(2).trim() + 
						" service from the Database" );

					cnt++;
				}
				catch( Exception ex )
				{
					CTILogger.warn( "Unable to start the YukonService : " + rset.getString(2).trim(), ex ); 
				}
			}
			
			//make the people aware
			if( cnt == 0 )
				CTILogger.info( " Started ZERO(0) YukonService's from the Database" );		
         
		}
		catch(SQLException e ) 
		{
			CTILogger.error( e.getMessage(), e );
		}
		finally 
		{
				try {
					if( stmt != null )
						stmt.close();
					if( conn != null )
						conn.close();
				}
				catch( java.sql.SQLException e ) {
					CTILogger.error( e.getMessage(), e );
				}
		}
				
//		ObjectName webGName = new ObjectName("CTI-Server:name=WebGraph");
//		server.createMBean("com.cannontech.jmx.services.DynamicWebGraph", webGName, null);
//		server.invoke(webGName, "start", null, null); 		
	}

	public static void main(String[] args)
	{
		try
		{
			System.setProperty("cti.app.name", "MBeanServer");
            
			//Assume the default server login operation
            ClientSession session = ClientSession.getInstance(); 
            if(!session.establishDefServerSession())
                System.exit(-1);          
                
            if(session == null) 
                System.exit(-1);

			JRMPServer thisServer = new JRMPServer();
			
			thisServer.loadCustomServices();

		}
		catch( Throwable t )
		{
			CTILogger.error( "Problem with loading services", t);
		}
		
	}
	

}
