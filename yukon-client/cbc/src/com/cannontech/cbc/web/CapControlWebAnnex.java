package com.cannontech.cbc.web;

/**
 * Maintains information from the CBC server and makes
 * it available in a form more useful for clients
 *
 * Creation date: (5/7/20032 2:22:15 PM)
 * @author: Ryan
 */
import java.awt.Color;
import java.util.Vector;

import com.cannontech.cbc.data.CBCClientConnection;
import com.cannontech.cbc.gui.CapBankTableModel;
import com.cannontech.cbc.gui.FeederTableModel;
import com.cannontech.cbc.gui.SubBusTableModel;
import com.cannontech.cbc.messages.CBCCommand;
import com.cannontech.cbc.messages.CBCSubAreaNames;
import com.cannontech.cbc.messages.CBCSubstationBuses;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.common.gui.util.Colors;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.WeakObserver;
import com.cannontech.database.db.state.State;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.util.ServletUtil;

public class CapControlWebAnnex implements java.util.Observer 
{
	public static final String REF_SECONDS_DEF = "60";
	public static final String REF_SECONDS_PEND = "5";


	public static final String CMD_SUB			= "SUB_CNTRL";
	public static final String CMD_FEEDER		= "FEEDER_CNTRL";
	public static final String CMD_CAPBANK		= "CAPBANK_CNTRL";


	//insures only 1 connection for the servlet
	private static CBCClientConnection conn = null;

	//insures only 1 set of these Strings for the servlet
	private static final Vector areaNames = new Vector(32);

	//the user this object was created for
	private String userName = null;


	//used to send commands to the connection
	private CBCCommandExec cbcExecutor = null;


	private SubBusTableModel subBusTableModel = null;
	private FeederTableModel feederTableModel = null;
	private CapBankTableModel capBankTableModel = null;


	//allows us to manage memory a little better 
	//  (GC this once it becomes a weak reference)
	private final WeakObserver thisWeakObsrvr = new WeakObserver(this);
	

	/**
	 * CapControlWebAnnex constructor comment.
	 */
	public CapControlWebAnnex() 
	{
		super();

		//force the connection to connect
		getConnection();

		initialize();
	}


	public synchronized boolean isConnected()
	{
		return getConnection().isConnValid();
	}


	public static String convertColor( Color c )
	{
		return "#" + ServletUtil.getHTMLColor(c);
	}
	
	
	protected synchronized CBCClientConnection getConnection()
	{
		if( conn == null )  
		{
			//first time this app has been hit		
			Registration reg = new Registration();
			reg.setAppName("CBC_WEB_CACHE@" + CtiUtilities.getUserName() );
			reg.setAppIsUnique(0);
			reg.setAppKnownPort(0);
			reg.setAppExpirationDelay( 300 );  // 5 minutes

			//The CBC server does not take this registration for now, dont use it
			//conn = new CBCClientConnection( reg );
			conn = new CBCClientConnection();


			CTILogger.info("Will attempt to connect to CBC Server @" + conn.getHost() + ":" + conn.getPort());
		}
				
		return conn;
	}

	public synchronized SubBusTableModel getSubTableModel()
	{
		if( subBusTableModel == null )
			subBusTableModel = new SubBusTableModel();

		return subBusTableModel;
	}
	
	public synchronized FeederTableModel getFeederTableModel()
	{
		if( feederTableModel == null )
			feederTableModel = new FeederTableModel();

		return feederTableModel;
	}

	public synchronized CapBankTableModel getCapBankTableModel()
	{
		if( capBankTableModel == null )
			capBankTableModel = new CapBankTableModel();
		
		return capBankTableModel;
	}

	protected void finalize() throws Throwable
	{
		getConnection().deleteObserver( getSubTableModel() );
		getConnection().deleteObserver( thisWeakObsrvr );

		super.finalize();
		
		CTILogger.debug("      finalized " + getClass().getName() ); 
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (8/25/00 9:33:17 AM)
	 */
	private void initialize() 
	{
		//dont show the year on timestamp strings
		ModifiedDate.setFormatPattern("MM-dd HH:mm:ss");
		

		//let us observe the connection
		getConnection().addObserver( thisWeakObsrvr );


		//add the table listener to the connection		
		getSubTableModel().setConnection( getConnection() );
		
		getConnection().addObserver( getSubTableModel() );		


		//remember, 2 observers per web client
		CTILogger.debug("   Added new WebDataModel client (Total: " + 
				(getConnection().countObservers()/2) + ")" );
				
		try
		{
			//we must tell our connection we want all the SUBs right away
			// for our SubTableModel
			getConnection().executeCommand( 0, CBCCommand.REQUEST_ALL_SUBS );
		}
		catch( Exception e ) {}

	}


	/**
	 * 
	 * Allows the execution of commands to the cbc server.
	 * @param cmdID_ int : the id of the command from CBCCommand to be executed
	 * @param cmdType_ String : type of command to execute ( CMD_SUB,CMD_FEEDER,CMD_CAPBANK ) 
	 * @param rowID_ int : the row from a types data model to execute 
	 * @param manChange_ Integer : the state index field for a capbank, null if not present
	 */
	public void executeCommand( int cmdID_, String cmdType_, int rowID_, Integer manChange_ )
	{
		if( cbcExecutor == null )		
			cbcExecutor = new CBCCommandExec( this );
			
		if( CMD_SUB.equals(cmdType_) )
			cbcExecutor.execute_SubCmd( cmdID_, rowID_ );
		
		if( CMD_FEEDER.equals(cmdType_) )
			cbcExecutor.execute_FeederCmd( cmdID_, rowID_ );

		if( CMD_CAPBANK.equals(cmdType_) )
			cbcExecutor.execute_CapBankCmd( cmdID_, rowID_, manChange_ );
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (4/12/2002 1:53:27 PM)
	 * @param areaNames com.cannontech.cbc.messages.CBCSubAreaNames
	 */
	private synchronized void updateAreaList(CBCSubAreaNames areaNames_) 
	{
		// remove all the values in the JComboBox except the first one
		getAreaNames().removeAllElements();
		getAreaNames().add( SubBusTableModel.ALL_FILTER );

		// add all area names to the JComboBox	
		for( int i = 0; i < areaNames_.getNumberOfAreas(); i++ )
			getAreaNames().add( areaNames_.getAreaName(i) );
	}

	/**
	 * This method is called whenever the observed object is changed. An
	 * application calls an <tt>Observable</tt> object's
	 * <code>notifyObservers</code> method to have all the object's
	 * observers notified of the change.
	 *
	 * @param   o     the observable object.
	 * @param   arg   an argument passed to the <code>notifyObservers</code>
	 *                 method.
	 */
	public synchronized void update(java.util.Observable o, java.lang.Object arg) 
	{
		CTILogger.debug( getClass() + ": received type: " + arg.getClass());


		if( arg instanceof CBCSubstationBuses )
		{
			//nothing for now, the models will handle this stuff
		}
		else if( arg instanceof CBCSubAreaNames )
		{
			updateAreaList( (CBCSubAreaNames)arg );
		}
		else if( arg instanceof State[] )
		{
			State[] states = (State[])arg;
			Color[][] colors = new Color[states.length][2];
			String[] stateNames = new String[states.length];
			
			for( int i = 0; i < states.length; i++ )
			{
				stateNames[i] = states[i].getText();
				colors[i][0] = Colors.getColor( states[i].getForegroundColor().intValue() );
				colors[i][1] = Colors.getColor( states[i].getBackgroundColor().intValue() );
			}

			CapBankTableModel.setStates( colors, stateNames );			
		}


		//Clear the list table of schedules if the connection isn't good
		if ( !getConnection().isConnValid() )
		{
			getSubTableModel().clear();
			getFeederTableModel().clear();
			getCapBankTableModel().clear();
			
			getAreaNames().clear();
		}

	}

	/**
	 * @return
	 */
	public static Vector getAreaNames()
	{
		return areaNames;
	}

	/**
	 * @return
	 */
	protected String getUserName()
	{
		return userName;
	}

	/**
	 * @param string
	 */
	public void setUserName(String string)
	{
		userName = string;
	}

}
