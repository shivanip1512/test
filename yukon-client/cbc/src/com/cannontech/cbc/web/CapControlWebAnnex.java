package com.cannontech.cbc.web;

/**
 * Maintains information from the CBC server and makes
 * it available in a form more useful for clients
 *
 * Creation date: (5/7/20032 2:22:15 PM)
 * @author: Ryan
 */
import java.awt.Color;

import com.cannontech.cbc.data.CBCClientConnection;
import com.cannontech.cbc.gui.CapBankTableModel;
import com.cannontech.cbc.gui.FeederTableModel;
import com.cannontech.cbc.gui.SubBusTableModel;
import com.cannontech.cbc.messages.CBCCommand;
import com.cannontech.cbc.messages.CBCSubstationBuses;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.WeakObserver;
import com.cannontech.database.db.state.State;
import com.cannontech.util.ServletUtil;

public class CapControlWebAnnex implements java.util.Observer 
{
	public static final String REF_SECONDS_DEF = "60";
	public static final String REF_SECONDS_PEND = "5";


	public static final String CMD_SUB			= "SUB_CNTRL";
	public static final String CMD_FEEDER		= "FEEDER_CNTRL";
	public static final String CMD_CAPBANK		= "CAPBANK_CNTRL";


	//the user this object was created for
	private String userName = null;

	private SubBusTableModel subBusTableModel = null;
	private FeederTableModel feederTableModel = null;
	private CapBankTableModel capBankTableModel = null;


	//what our current refresh rate is
	private String refreshRate = CapControlWebAnnex.REF_SECONDS_DEF;


	//allows us to manage memory a little better 
	//  (GC this once it becomes a weak reference)
	private final WeakObserver thisWeakObsrvr = new WeakObserver(this);
	

	//just a ref to a real connection
	private CBCClientConnection conn = null;
	
	/**
	 * CapControlWebAnnex constructor comment.
	 */
	public CapControlWebAnnex() 
	{
		super();
	}

	public boolean isConnected()
	{
		return getConnection().isValid();
	}

	public CBCClientConnection getConnection()
	{
		if( conn == null )
			throw new IllegalStateException("The CBC Connection should NEVER be (null)");

		return conn;
	}

	public synchronized boolean hasValidConn()
	{
		return conn != null;
	}

	public void setConnection( CBCClientConnection conn_ )
	{
		//only init the conn the annex after we set the conn
		if( conn == null )
		{
			conn = conn_;
			initialize();
		}
		else			
			conn = conn_;
	}
	
	public static String convertColor( Color c )
	{
		return "#" + ServletUtil.getHTMLColor(c);
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
		{
			capBankTableModel = new CapBankTableModel();
			
			//the browser cant flash and blink! 
			capBankTableModel.toggleAlarms( false );
		}
		
		
		return capBankTableModel;
	}

	protected void finalize() throws Throwable
	{
		getConnection().removeMessageListener( getSubTableModel() );
		getConnection().deleteObserver( thisWeakObsrvr );

		super.finalize();
		
		//remember, 2 observers per web client
		CTILogger.debug("  Finalized - CapControlAnnex (Total: " + 
				(getConnection().countObservers()/2) + ")" );
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (8/25/00 9:33:17 AM)
	 */
	private void initialize() 
	{
		//add the table listener to the connection		
		getSubTableModel().setConnection( getConnection() );
		
		//let us observe the connection
		getConnection().addObserver( thisWeakObsrvr );

		//let us observe the SubTableModel
		getConnection().addMessageListener( getSubTableModel() );		


		//remember, 2 observers per web client
		CTILogger.debug("   Added new WebDataModel client (Total: " + 
				(getConnection().countObservers()/2) + ")" );
				
		try
		{
			//we must tell our connection we want all the SUBs right away
			// for our SubTableModel
			getConnection().executeCommand( 0, CBCCommand.REQUEST_ALL_SUBS );
			
			//FIXME: since this is asynchronous, the page is returned before we have any SubBuses, thus
			// causing a blank page to be displayed. This is a hack around it.
			Thread.currentThread().sleep( 3500 );
		}
		catch( Exception e ) {}

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
		else if( arg instanceof State[] )
		{
			//nothing for now, a singleton (servlet) will handle this stuff
		}


		//Clear the list table of schedules if the connection isn't good
		if ( !getConnection().isValid() )
		{
			getSubTableModel().clear();
			getFeederTableModel().clear();
			getCapBankTableModel().clear();
		}

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



	/**
	 * @return
	 */
	public String getRefreshRate()
	{
		return refreshRate;
	}

	/**
	 * @param string
	 */
	public void setRefreshRate(String string)
	{
		refreshRate = string;
	}
}
