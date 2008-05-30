/*
 * Created on Jun 30, 2003
 */
package com.cannontech.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.jdbc.support.JdbcUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.PoolManager;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DefaultDatabaseCache;
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
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		URLParameters urlParams = new URLParameters();
		
		//HttpSession session = req.getSession(false);
		String retPage = req.getContextPath() + "/setup.jsp";
		
		String adminPword = req.getParameter("admin_password");
		
		if( adminPword != null )
		{
			//validate the password since we have a good DB connection
			LiteYukonUser admin = DaoFactory.getYukonUserDao().getLiteYukonUser( UserUtils.USER_YUKON_ID );
			
			if( DaoFactory.getAuthDao().login(admin.getUsername(), adminPword) == null )
			{
				urlParams.put( "invalid", "true" );
				resp.sendRedirect( retPage + urlParams.toString() );
				return;
			}

		}
		
		boolean isValidDBConn = false;
		try
		{
			java.sql.Connection c = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
			isValidDBConn = (c != null);
            JdbcUtils.closeConnection(c);
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
						//handle the DBChangeMsg locally (nulls out our roleproperties, forcing us to reload completely)
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
			DaoFactory.getRoleDao().getGroup( YukonGroupRoleDefs.GRP_YUKON );
			
		YukonGroup yukGrpPersist = 
				(YukonGroup)LiteFactory.createDBPersistent( yukGrp );
		
		//fill out the DB Persistent with data
		yukGrpPersist = 
			(YukonGroup)Transaction.createTransaction( 
						Transaction.RETRIEVE, yukGrpPersist ).execute();
		
		
		LiteYukonRoleProperty[] props = 
				DaoFactory.getRoleDao().getRoleProperties( YukonRoleDefs.SYSTEM_ROLEID );
				
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


	/**
	 * Create and use a dispatch connection, close it when finished.
	 * Dont hang on to it. This seems OK since this servlet is
	 * not used frequently. 
	 * 
	 * @return com.cannontech.message.dispatch.ClientConnection
	 */
	private void writeToDispatch( Multi msg ) throws Exception
	{
		//We Should at some point create a DispatchServlet that we could use
		String host =
			DaoFactory.getRoleDao().getGlobalPropertyValue( SystemRole.DISPATCH_MACHINE );

		String port =
			DaoFactory.getRoleDao().getGlobalPropertyValue( SystemRole.DISPATCH_PORT );
		
		ClientConnection connToDispatch = new ClientConnection();
		Registration reg = new Registration();
		reg.setAppName( CtiUtilities.getApplicationName() );
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
