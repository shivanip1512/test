package com.cannontech.jmx;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.management.Attribute;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.naming.Context;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;

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
	private MBeanServer server = MBeanServerFactory.createMBeanServer();
	
	// The domain for all CTI services in the JMX domain
	public static final String CTI_DOMAIN = "CTI-Server:name=";


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
					ObjectName name = new ObjectName( CTI_DOMAIN + rset.getString(2).trim() );
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

	/**
	 * Necessary Services for monitoring & managing MBeans
	 * @throws Exception
	 */
	private void loadJMXServices() throws Exception //throws a lot of things, just force a catch for all
	{
		ObjectName naming = new ObjectName("Naming:type=registry");
		//	Create and register the MBean in the MBeanServer
		server.createMBean("mx4j.tools.naming.NamingService", naming, null);			
		server.invoke(naming, "start", null, null);
		
		
		
		ObjectName adaptName = new ObjectName("Adaptor:protocol=JRMP");
		server.createMBean("mx4j.adaptor.rmi.jrmp.JRMPAdaptor", adaptName, null);		
		server.setAttribute( adaptName, new Attribute("JNDIName", "jrmp") );		

		server.invoke(adaptName, "start", null, null ); 
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


			//RMI specific setters
			System.setProperty( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory" );
			System.setProperty( Context.PROVIDER_URL, "rmi://localhost:1099" );

			JRMPServer thisServer = new JRMPServer();
			
			//
			// If management and monitoring is needed, load the JMX services
			// 
			thisServer.loadJMXServices();
			
			
			
			thisServer.loadCustomServices();

		}
		catch( Throwable t )
		{
			CTILogger.error( "Problem with loading services", t);
		}
		
	}
	

}
