/*
 * Created on Jun 30, 2003
 */
package com.cannontech.servlet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.user.YukonGroup;
import com.cannontech.database.db.user.YukonGroupRole;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.roles.YukonGroupRoleDefs;
import com.cannontech.roles.YukonRoleDefs;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.user.UserUtils;
import com.cannontech.util.ServletUtil;
import com.cannontech.util.URLParameters;

/**
 * Sets the values of the properties a user needs
 * @author ryan
 * 
 * 
 * 
 * @param admin_password
 * if the admin_password is not found (null), then the submitter never had a valid
 * db connection, and this parameter should not be checked (ie: we allow the change
 * no matter what). If this param does exist, then the param must be check
 * before changing DB properties AND role properties.
 * 
 * 
 */
public class SetupServlet extends HttpServlet 
{
	static final String LF = System.getProperty("line.separator");
	//jdbc:microsoft:sqlserver://dbserver:1433;SelectMethod=cursor;
	//jdbc:oracle:thin:@10.100.1.76:1521:preprod
	
	static final String FOOTER_TEXT = LF + LF +
		"---------------- Some Sample driver URL stringes ---------" + LF +
		"#---------------------------------------------------------#" + LF +
		"#- OpenSource SqlServer JDBC native driver URL           -#" + LF +
		"#-   (Recommended driver if using SQLServer)             -#" + LF +
		"#---------------------------------------------------------#" + LF +
		"#yukon.url=jdbc:jtds:sqlserver://127.0.0.1:1433;APPNAME=yukon-client;TDS=8.0" + LF +
		"" + LF +
		"#---------------------------------------------------------#" + LF +
		"#- Microsoft SqlServer 2000 native driver URL            -#" + LF +
		"#---------------------------------------------------------#" + LF +
		"#yukon.url=jdbc:microsoft:sqlserver://127.0.0.1:1433;SelectMethod=cursor" + LF +
		"" + LF +	
		"#---------------------------------------------------------#" + LF +
		"#- Oracle 8.x native driver URL                          -#" + LF +
		"#---------------------------------------------------------#" + LF +
		"#yukon.url=jdbc:oracle:thin:@127.0.0.1:1521:yukon" + LF +
		"" + LF +
		"#---------------------------------------------------------#" + LF +
		"#- ODBC SqlServer driver and URL                         -#" + LF +
		"#-   (Only use if no other option is available)          -#" + LF +
		"#---------------------------------------------------------#" + LF +
		"#yukon.url=jdbc:odbc:sqlserver";
			


	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		URLParameters urlParams = new URLParameters();
		
		//HttpSession session = req.getSession(false);
		String retPage = req.getContextPath() + "/setup.jsp";
		
		String adminPword = req.getParameter("admin_password");
		
		if( adminPword != null )
		{
			//validate the password since we have a good DB connection
			LiteYukonUser admin = YukonUserFuncs.getLiteYukonUser( UserUtils.USER_YUKON_ID );
			
			if( !admin.getPassword().equals(adminPword) )
			{
				urlParams.put( "invalid", "true" );
				resp.sendRedirect( retPage + urlParams.toString() );
				return;
			}

		}
		

		
		try
		{
			if( writeDBProperties(req) )
			{
				urlParams.put( "dbprop", "true" );
			
				/*** This seems to cause a NullPointerException in a loader ***/ 
				//remove any cache elements we may have from the prvious DB
				try
				{
					DefaultDatabaseCache.getInstance().releaseAllCache();
		
//					DefaultDatabaseCache.getInstance().getAllYukonUsers();
//					DefaultDatabaseCache.getInstance().getAllYukonRoles();
//					DefaultDatabaseCache.getInstance().getAllYukonRoleProperties();
//					DefaultDatabaseCache.getInstance().getAllYukonGroups();
				}
				catch( Exception ex) {}
			
				//restart tomcat servlets here maybe?
			}
			
		}
		catch( Exception e )
		{
			CTILogger.error( "Unable to write DB properties", e );
			urlParams.put( "dbprop", "false" );			
		}

		
		boolean isValidDBConn = false;
		try
		{
			java.sql.Connection c = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
			isValidDBConn = (c != null);
			PoolManager.getInstance().freeConnection( CtiUtilities.getDatabaseAlias(), c );
		}
		catch( Throwable t ) {}


		//only attempt the following changes if we have a valid DB connection
		if( isValidDBConn )
		{
		
			DBChangeMsg[] dbChangeMsgs = null;
			try
			{
				dbChangeMsgs = writeYukonProperties( req );
				urlParams.put( "yukprop", "true" );				
			}
			catch( Exception e )
			{
				urlParams.put( "yukprop", "false" );				
				CTILogger.error( "Unable to write SERVER properties", e );
			}
	
	
			//try to send out DBChanges if we have any  
			if( dbChangeMsgs != null )
			{
				try
				{
					Multi multi = new Multi();
					for( int i = 0; i < dbChangeMsgs.length; i++ )
					{
						//handle the DBChangeMsg locally
						DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChangeMsgs[i]);
								
						multi.getVector().add( dbChangeMsgs[i] );
					}
			
					//write the DBChangeMessage out to Dispatch since it was a Successfull UPDATE
					writeToDispatch( multi );
					urlParams.put( "disp", "true" );					
				}
				catch( Exception e )
				{
					urlParams.put( "disp", "false" );
					CTILogger.error( "Unable to connect to DISPATCH", e );					
				}				
			}
		}


		resp.sendRedirect( retPage + urlParams.toString() );	
	}


	private DBChangeMsg[] writeYukonProperties( final HttpServletRequest req ) throws Exception
	{
		LiteYukonGroup yukGrp =
			AuthFuncs.getGroup( YukonGroupRoleDefs.GRP_YUKON );
			
		YukonGroup yukGrpPersist = 
				(YukonGroup)LiteFactory.createDBPersistent( yukGrp );
		
		//fill out the DB Persistent with data
		yukGrpPersist = 
			(YukonGroup)Transaction.createTransaction( 
						Transaction.RETRIEVE, yukGrpPersist ).execute();
		
		
		LiteYukonRoleProperty[] props = 
				RoleFuncs.getRoleProperties( YukonRoleDefs.SYSTEM_ROLEID );
				
		for( int i = 0; i < props.length; i++ )
		{
			for( int j = 0; j < yukGrpPersist.getYukonGroupRoles().size(); j++ )
			{
				YukonGroupRole grpRole = 
					(YukonGroupRole)yukGrpPersist.getYukonGroupRoles().get(j);
			
				if( props[i].getRolePropertyID() == grpRole.getRolePropertyID().intValue() )
				{
					String propVal = req.getParameter( props[i].getKeyName() );
					
					//we must have a valid value to assign it here
					if( propVal != null )
						grpRole.setValue( propVal );

					break;
				}

			}
			
		}
		
		//update any changed values in the DB
		yukGrpPersist = 
			(YukonGroup)Transaction.createTransaction(
					Transaction.UPDATE, yukGrpPersist ).execute();
		
		//get the DB change message for this guy
		DBChangeMsg[] dbChangeMsgs =
			DefaultDatabaseCache.getInstance().createDBChangeMessages(
					yukGrpPersist, DBChangeMsg.CHANGE_TYPE_UPDATE );
		
		return dbChangeMsgs;
	}


	private boolean writeDBProperties( final HttpServletRequest req ) throws Exception
	{
		final String dbal = CtiUtilities.getDatabaseAlias();
		boolean hasChanged = false;
		
		String userName = ServletUtil.getParm( req, "db_user_name");
		hasChanged |= !userName.equalsIgnoreCase( PoolManager.getInstance().getProperty(PoolManager.USER) );

		String initConns = ServletUtil.getParm( req, "db_initconns");
		hasChanged |= !initConns.equalsIgnoreCase( PoolManager.getInstance().getProperty(PoolManager.INITCONNS) );
		
		String maxConns = ServletUtil.getParm( req, "max_initconns");
		hasChanged |= !maxConns.equalsIgnoreCase( PoolManager.getInstance().getProperty(PoolManager.MAXCONNS) );

		String pword = ServletUtil.getParm( req, "db_user_password");
		if( pword == null ) //if not present, use the old pwrod
			pword = PoolManager.getInstance().getProperty(PoolManager.PASSWORD);

		hasChanged |= !pword.equals( PoolManager.getInstance().getProperty(PoolManager.PASSWORD) );

		
		String url = ServletUtil.getParm( req, "db_url");
		String port = ServletUtil.getParm( req, "db_port");		
		String driver = ServletUtil.getParm( req, "db_driver");
		String service = ServletUtil.getParm( req, "db_service");
		

		//icky poooh
		if( driver.equalsIgnoreCase("sql") )
		{				
			url = PoolManager.DRV_SQLSERVER + "//" 
					+ url + ":" + port + ";SelectMethod=cursor;";
		}
		else if( driver.equalsIgnoreCase("jtds") )
		{				
			url = PoolManager.DRV_JTDS + "//" 
					+ url + ":" + port + ";APPNAME=yukon-client;TDS=8.0";
		}
		else if( driver.equalsIgnoreCase("oracle") )
		{
			url = PoolManager.DRV_ORACLE + "@" 
					+ url + ":" + port + ":" + service;
		}
		else
			throw new Error("Unknown driver specified");


		hasChanged |= !url.equals( PoolManager.getInstance().getProperty(PoolManager.URL) );


		if( !hasChanged )
			return false;



		FileWriter fw = null;
		try
		{
			File f = new File( PoolManager.getPropertyURL().getFile() );
			fw = new FileWriter(f, false);
			
			//write a timestamp so we keep some sort of record in the file
			fw.write( "#" + new Date().toString() + LF );

			fw.write( dbal + PoolManager.URL + "=" + url + LF );			
			fw.write( dbal + PoolManager.USER + "=" + userName + LF );			
			fw.write( dbal + PoolManager.PASSWORD + "=" + pword + LF );
			fw.write( dbal + PoolManager.INITCONNS + "=" + initConns + LF );
			fw.write( dbal + PoolManager.MAXCONNS + "=" + maxConns + LF );



			//print out some sample driver URL string just in case the user
			// wants to edit the file directly
			fw.write( FOOTER_TEXT );

			fw.flush();
			
			//lets have the pool reset itself
			PoolManager.getInstance().resetPool();			
		}
		finally
		{
			try
			{
				fw.close();
			}
			catch ( IOException io )
			{}
		}
					
	
		return true;  //wrote the file	
	}


	/**
	 * Create and use a dispatch connection, close it when finished.
	 * Dont hang on to it. This seems OK since this servlet is
	 * not used frequently. 
	 * 
	 * @return com.cannontech.message.dispatch.ClientConnection
	 */
	private void writeToDispatch( Multi msg ) throws Exception
	{
		//TODO: We Should at some point create a DispatchServlet that we could use
		String host =
			RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_MACHINE );

		String port =
			RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_PORT );
		
		ClientConnection connToDispatch = new ClientConnection();
		Registration reg = new Registration();
		reg.setAppName("SetupServlet" );
		reg.setAppIsUnique(0);
		reg.setAppKnownPort(0);
		reg.setAppExpirationDelay( 30 );  // 30 sec should be OK

		connToDispatch.setHost(host);
		connToDispatch.setPort( Integer.parseInt(port) );
		connToDispatch.setAutoReconnect(true);
		connToDispatch.setRegistrationMsg(reg);

		//lets send the DB change and then close the connection
		connToDispatch.connectWithoutWait();
		try
		{
			//give 3 seconds to connect
			for( int i = 0; i < 100; i++ )
				if( connToDispatch.isValid() )
					break;
				else
					Thread.sleep( 30 );
		}
		catch( InterruptedException e ) {}


		if( connToDispatch.isValid() )
		{
			connToDispatch.write( msg );
			connToDispatch.disconnect();
		}
		else
		{
			connToDispatch.disconnect();
			throw new IllegalStateException("Unable to connect to Dispatch At: " + host + ":" + port);
		}
		
	}
}
