package com.cannontech.ejb;

import java.rmi.RemoteException;
import java.util.Observer;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import com.cannontech.yukon.IMACSConnection;
import com.cannontech.yukon.IConnectionBase;
import com.cannontech.message.macs.message.MACSCategoryChange;
import com.cannontech.yukon.connections.ServerMACSConnection;
import com.cannontech.common.util.MessageEventListener;
import com.cannontech.message.macs.message.OverrideRequest;
import com.cannontech.message.macs.message.Schedule;
//import com.cannontech.message.util.ClientConnection;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
/* Add this to SQLStatementHome class */
//public com.cannontech.ejb.SqlStatement create() throws javax.ejb.CreateException, java.rmi.RemoteException;

/**
 * @ejb:bean name="MACSConnection"
 *	jndi-name="jndi/MACSConnectionBean"
 *	type="Stateful" 
**/
public class MACSConnectionBean /*extends ClientConnection*/ implements SessionBean, IMACSConnection
{
	private static ServerMACSConnection macsConn = null;


 	public void ejbActivate() throws EJBException, RemoteException {}
	public void ejbPassivate() throws EJBException, RemoteException{}
	public void ejbRemove() throws EJBException, RemoteException {}
	public void setSessionContext(SessionContext sessCntxt) throws EJBException, RemoteException {}
   public void ejbCreate() throws javax.ejb.CreateException {}


	public static synchronized ServerMACSConnection getMACSConnection()
	{
		if( macsConn == null )
			macsConn = new ServerMACSConnection();
			
		return macsConn;		
	}

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public void addMessageEventListener(MessageEventListener listener) 
	{
		getMACSConnection().addMessageEventListener( listener );
	}

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public void doHandleMessage(Object obj)
	{
		getMACSConnection().doHandleMessage( obj );

	}

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public IConnectionBase getMACSConnBase()
	{
		return getMACSConnection();
	}

	
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public void fireMessageEvent(com.cannontech.common.util.MessageEvent event) 
	{
		//lets try this method!!! MAY NOT WORK FOR JBOSS CLIENT!!!!!!
		getMACSConnection().fireMessageEvent( event );
	}

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public Schedule[] getCategories( String category )
	{
		return getMACSConnection().getCategories( category );
	}

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public java.util.Hashtable getCategoryNames() 
	{
		return getMACSConnection().getCategoryNames();
	}	

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public boolean handleMessage(Object message) 
	{
		return getMACSConnection().handleMessage( message );
	}

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public Schedule[] retrieveSchedules()
	{
		return getMACSConnection().retrieveSchedules();	
	}

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public void sendCreateSchedule(Schedule sched) throws java.io.IOException 
	{
		getMACSConnection().sendCreateSchedule( sched );
	}

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public void sendDeleteSchedule(int scheduleID) throws java.io.IOException
	{
		getMACSConnection().sendDeleteSchedule( scheduleID );
	}

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public void sendEnableDisableSchedule(Schedule sched) throws java.io.IOException
	{
		getMACSConnection().sendEnableDisableSchedule( sched );
	}

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public void sendRetrieveAllSchedules() throws java.io.IOException 
	{
		getMACSConnection().sendRetrieveAllSchedules();
	}

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public void sendRetrieveOneSchedule( int schedId ) throws java.io.IOException 
	{
		getMACSConnection().sendRetrieveOneSchedule( schedId );
	}

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public void sendRetrieveScriptText(String scriptFileName) throws java.io.IOException
	{
		getMACSConnection().sendRetrieveScriptText( scriptFileName );
	}
	
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public void sendScriptFile(com.cannontech.message.macs.message.ScriptFile file) throws java.io.IOException
	{
		getMACSConnection().sendScriptFile( file );
	}

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public void sendStartStopSchedule(Schedule sched, java.util.Date startTime, java.util.Date stopTime, int command ) throws java.io.IOException
	{
		getMACSConnection().sendStartStopSchedule( sched, startTime, stopTime, command );
	}

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
	public void sendUpdateSchedule(Schedule sched ) throws java.io.IOException 
	{
		getMACSConnection().sendUpdateSchedule( sched );
	}

}