package com.cannontech.jmx;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.login.ClientSession;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.spring.YukonSpringHook;

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
   		
        //MBeanServer server = MBeanUtil.getInstance();
		
        // NOTE:
        // I've commented out all references to JMX and MBeans. Until we have
        // time to do this right, they aren't really serving any purpose.
        // --Tom
        
		JdbcOperations template = JdbcTemplateHelper.getYukonTemplate();
		template.query(sql, new ResultSetExtractor() {
		    public Object extractData(ResultSet rset) throws SQLException, DataAccessException {
		        int cnt = 0;
                while( rset.next() ) {
                    if (startService(rset.getString(2), rset.getString(3))) {
                        cnt++;
                    }
                }
                
                //make the people aware
                if( cnt == 0 ) {
                    CTILogger.info( " Started ZERO(0) YukonService's from the Database" );
                }   
		        return null;
		    }
		});
			
            
            // start the http adapter
//            try {
//                XSLTProcessor outputProcessor = new XSLTProcessor();
//                ObjectName xsltProcessorName = new ObjectName("server:name=XsltProcessor");
//                HttpAdaptor httpAdaptor = new HttpAdaptor();
//                ObjectName httpName = new ObjectName("server:name=HttpAdaptor");
//                server.registerMBean(httpAdaptor, httpName);
//                httpAdaptor.setPort(8081);
//                final String name = XSLTProcessor.class.getName();
//                httpAdaptor.setProcessorClass(name);
//                httpAdaptor.setProcessorNameString("server:name=XsltProcessor");
//                httpAdaptor.start();
//            } catch (Exception e) {
//                CTILogger.error("Unable to start Http JMX Adapter", e);
//            }
         
				
	}

	private boolean startService(String displayName, String classOrBeanName) throws SQLException {
        try
        {
            //ObjectName name = new ObjectName( ":name=" + rset.getString(2).trim() );
            String className = classOrBeanName.trim();
            String beanPrefix = "bean:";
            Object service = null;
            if (className.startsWith(beanPrefix)) {
                String beanName = className.substring(beanPrefix.length());
                YukonSpringHook.setDefaultContext(YukonSpringHook.SERVICES_BEAN_FACTORY_KEY);
                ApplicationContext context = YukonSpringHook.getContext();
                service = context.getBean(beanName);
                //server.registerMBean(bean, name);
            } else {
                Class clazz = Class.forName(className);
                service = clazz.newInstance();
                //server.createMBean( className, name, null);
            }
            
            //better have a start() method defined in the class!
            service.getClass().getMethod("start").invoke(service);
            //server.invoke(name, "start", null, null);
            
            CTILogger.info( 
                " SUCCESSFUL start of the " + displayName + 
                " service from the Database" );
            
            return true;
        }
        catch( Exception ex )
        {
            CTILogger.warn( "Unable to start the YukonService : " + displayName, ex ); 
        }
        return false;
    }

    public static void main(String[] args)
	{
		try
		{
			System.setProperty("cti.app.name", "BulkImporter");
            
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
