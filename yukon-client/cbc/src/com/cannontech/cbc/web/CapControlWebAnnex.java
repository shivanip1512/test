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

import javax.swing.Timer;

import com.cannontech.cbc.data.CBCClientConnection;
import com.cannontech.cbc.data.SubBus;
import com.cannontech.cbc.gui.CapBankTableModel;
import com.cannontech.cbc.gui.FeederTableModel;
import com.cannontech.cbc.gui.SubBusTableModel;
import com.cannontech.cbc.messages.CBCSubAreaNames;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.common.gui.util.Colors;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.state.State;
import com.cannontech.message.dispatch.message.Registration;

public class CapControlWebAnnex implements java.util.Observer, java.awt.event.ActionListener 
{
	public static final String REFRESH_SECONDS = "60";

	public static final int CMD_SUB			= 1;
	public static final int CMD_FEEDER		= 2;
	public static final int CMD_CAPBANK		= 3;



	private static int startupRefreshRate = 15 * 1000;
	private static int normalRefreshRate = 60 * 5 * 1000; //5 minutes

	//insure only 1 connection for the servlet
	private static CBCClientConnection conn = null;

	private Timer refreshTimer = new javax.swing.Timer(startupRefreshRate, this );
	private CBCCommandExec cbcExecutor = null;

	//contains Strings of all distinct areas
	private Vector areaNames = null;


	private SubBusTableModel subBusTableModel = null;
	private FeederTableModel feederTableModel = null;
	private CapBankTableModel capBankTableModel = null;


	/**
	 * CapControlCache constructor comment.
	 */
	public CapControlWebAnnex() 
	{
		super();
		refreshTimer.setRepeats(true);
		refreshTimer.start();
		
		//force the connection to connect
		getConnection();
		
		initialize();
	}


	public synchronized boolean isConnected()
	{
		return getConnection().isConnValid();
	}


	public static synchronized String convertColor( Color c )
	{
		String r = Integer.toHexString(c.getRed());
		String g = Integer.toHexString(c.getGreen());
		String b = Integer.toHexString(c.getBlue());

		return
				"#" +
				(r.length() <= 1 ? "0"+r : r) + 
				(g.length() <= 1 ? "0"+g : g) +
				(b.length() <= 1 ? "0"+b : b);
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(java.awt.event.ActionEvent e)
	{
		refresh();
		
	}


	protected synchronized CBCClientConnection getConnection()
	{
		if( conn == null )
		{
			Registration reg = new Registration();
			reg.setAppName("CBC_WEB_CACHE@" + CtiUtilities.getUserName() );
			reg.setAppIsUnique(0);
			reg.setAppKnownPort(0);
			reg.setAppExpirationDelay( 300 );  // 5 minutes

			//TODO:The CBC server does not take this registration for now, dont use it
			//conn = new CBCClientConnection( reg );
			conn = new CBCClientConnection();
			
			CTILogger.info("Will attempt to connect to CBC Server @" + conn.getHost() + ":" + conn.getPort());

			//let us observe the connection
			conn.addObserver( this );
			
			//add the table listener to the connection
			getSubTableModel().setConnection( getConnection() );
			getConnection().addObserver( getSubTableModel() );			
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

	/**
	 * Insert the method's description here.
	 * Creation date: (8/25/00 9:33:17 AM)
	 */
	private void initialize() 
	{
		//dont show the year on timestamp strings
		ModifiedDate.setFormatPattern("MM-dd HH:mm:ss");
	}


	public void executeCommand( int cmdID_, int cmdType_, int rowID_ )
	{
		if( cbcExecutor == null )		
			cbcExecutor = new CBCCommandExec( this );
			
		if( cmdType_== CMD_SUB )
			cbcExecutor.execute_SubCmd( cmdID_, rowID_ );
		
		if( cmdType_== CMD_FEEDER )
			cbcExecutor.execute_FeederCmd( cmdID_, rowID_ );

		if( cmdType_== CMD_CAPBANK )
			cbcExecutor.execute_CapBankCmd( cmdID_, rowID_ );
	}
	
	/**
	 * Renew the cache.
	 * Creation date: (6/11/2001 3:36:24 PM)
	 */
	private synchronized void refresh()
	{
/*	
		com.cannontech.clientutils.CTILogger.info("Refreshing customer-energycompany mappings");
		
		// Update energy company - customer mapping from db
		energyCompanyCustomer.clear();
		customerEnergyCompany.clear();
		
		long[] ids = com.cannontech.database.db.company.EnergyCompany.getAllEnergyCompanyIDs();
	
		for( int i = 0; i < ids.length; i++ )
		{
			long[] custIDs = com.cannontech.database.db.web.EnergyCompanyCustomerList.getCustomerIDs(ids[i], dbAlias);
	
			energyCompanyCustomer.put( new Integer( (int) ids[i]), custIDs );
	
			for( int j = 0; j < custIDs.length; j++ )
			{
				customerEnergyCompany.put( new Integer( (int) custIDs[j] ), new Integer( (int) ids[i] ) );
			}
		}
	
		
		{
			com.cannontech.clientutils.CTILogger.info("Refreshing customer baselines");
			
			java.sql.Connection conn = null;
			java.sql.Statement stmt = null;
			java.sql.ResultSet rset = null;
		
			try
			{		
				conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
				stmt = conn.createStatement();
				rset = stmt.executeQuery("SELECT CustomerID,PointID FROM CustomerBaseLinePoint");
		
				while( rset.next() )
				{
					int customerID = rset.getInt(1);
					int pointID = rset.getInt(2);
					customerBaseLine.put(new Integer(customerID), new Integer(pointID));			
				}
			}
			catch(java.sql.SQLException e)
			{
				com.cannontech.clientutils.CTILogger.info("An error occured refreshing customerbaselines");			
			}
			finally
			{
				try {
					if( rset != null ) rset.close();
					if( stmt != null ) stmt.close();
					if( conn != null ) conn.close();
				} catch(java.sql.SQLException e2) {  }
			}
	
			com.cannontech.clientutils.CTILogger.info("Loaded " + customerBaseLine.size() + " customer baselines.");
		}
		
		com.cannontech.clientutils.CTILogger.info("Refreshing control areas");
		
		if( conn != null )
		{		
			com.cannontech.loadcontrol.messages.LMCommand c =
				new com.cannontech.loadcontrol.messages.LMCommand();
	
			c.setCommand( com.cannontech.loadcontrol.messages.LMCommand.RETRIEVE_ALL_CONTROL_AREAS);
			conn.write(c);
	
			refreshTimer.setDelay(normalRefreshRate);
		}
*/		
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/12/2002 1:53:27 PM)
	 * @param areaNames com.cannontech.cbc.messages.CBCSubAreaNames
	 */
	private void updateAreaList(CBCSubAreaNames areaNames_) 
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
		CTILogger.info( getClass() + ": received type: " + arg.getClass());


		if( arg instanceof SubBus[] )
		{
			//nothing for now
		}
		else if( arg instanceof CBCSubAreaNames )
		{
			updateAreaList( (CBCSubAreaNames)arg );
		}
		else if( arg instanceof State[] )
		{
			//handleStates( (State[])arg );
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

	}

	/**
	 * @return
	 */
	public Vector getAreaNames()
	{
		if( areaNames == null )
			areaNames = new Vector(32);

		return areaNames;
	}

}
