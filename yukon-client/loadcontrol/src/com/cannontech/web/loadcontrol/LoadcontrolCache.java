package com.cannontech.web.loadcontrol;

/**
 * Maintains information from the load control server and makes
 * it available in a form useful for loadcontrol.com
 *
 * Observes com.cannontech.loadcontrol.LoadControlClientConnection for
 * new control area messages.
 *
 * Creation date: (6/11/2001 2:22:15 PM)
 * @author: Aaron Lauinger
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Timer;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.LMFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMCurtailCustomer;
import com.cannontech.loadcontrol.data.LMEnergyExchangeCustomer;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramCurtailment;
import com.cannontech.loadcontrol.data.LMProgramDirect;
import com.cannontech.loadcontrol.data.LMProgramEnergyExchange;
import com.cannontech.loadcontrol.data.LMScenarioWrapper;
import com.cannontech.loadcontrol.events.LCChangeEvent;

public class LoadcontrolCache implements java.util.Observer, java.awt.event.ActionListener {

	//SQL to retrive customer base line point data
	private static String baseLineSql = "SELECT Value FROM RawPointhistory WHERE PointID=? AND Timestamp > ? AND Timestamp <= ?";
		
	private static int startupRefreshRate = 15 * 1000;
	private static int normalRefreshRate = 60 * 5 * 1000; //5 minutes
	private Timer refreshTimer = new javax.swing.Timer(startupRefreshRate, this );
	
	private com.cannontech.loadcontrol.LoadControlClientConnection conn = null;
	 
	private String dbAlias = "yukon";
	/* The following data structures contain information from the loadmanagement
	   server sorted to make it more accessible.  When anew control areas arrives
	   from the connection it is placed in the appropriate slot(s).  This is known
	   to have happened when this update(..) method is called, this observes
	   the loadcontrol connection.
	*/

	// key = Map<Integer, LMControlArea>
	private Hashtable controlAreaMap = new Hashtable();
	//	key = Map<Integer, LMProgramBase>
	private Hashtable programMap = new Hashtable();
	//	key = Map<Integer, LMGroupBase>
	private Hashtable groupMap = new Hashtable();

	// key = (Integer) energy company id, value = (long[]) customer id
	private HashMap energyCompanyCustomer = new HashMap();
	
	// key = (Integer) customer, value = long energy company id
	private HashMap customerEnergyCompany = new HashMap();

	// key = (Integer) customer, value = Integer baseline pointid
	private HashMap customerBaseLine = new HashMap();
	
	
	//programs
	private ArrayList curtailmentPrograms = new ArrayList();
	private ArrayList energyExchangePrograms = new ArrayList();
	private ArrayList directPrograms = new ArrayList();
	
/**
 * LoadcontrolCache constructor comment.
 */
public LoadcontrolCache() {
	super();
	refreshTimer.setRepeats(true);
	refreshTimer.start();
}
	/**
	 * Invoked when an action occurs.
	 */
public void actionPerformed(java.awt.event.ActionEvent e)
{
	refresh();
}
/**
 * Creation date: (6/25/2001 9:16:12 AM)
 * @return java.lang.Integer
 * @param customerID java.lang.Integer
 */
private Integer findEnergyCompany(Integer customerID) {

	java.util.Iterator iter = energyCompanyCustomer.keySet().iterator();
	while(iter.hasNext())
	{
		Integer id = (Integer) iter.next();
		long[] custID = (long[]) energyCompanyCustomer.get(id);

		if( java.util.Arrays.binarySearch(custID, customerID.longValue()) >= 0 )
		{
			return id;
		}
	}

	return null;	
}
/**
 * Creation date: (6/12/2001 9:36:25 AM)
 * @return com.cannontech.loadcontrol.data.LMProgramEnergyExchange[]
 * @param operatorID long
 */
public com.cannontech.loadcontrol.data.LMProgramEnergyExchange[] getActiveEnergyExchangePrograms(long energyCompanyID) {

/*	java.util.ArrayList foundProgs = new java.util.ArrayList();

	// a list of this energy companies customers
	long[] eeCustomers = (long[]) energyCompanyCustomer.get(new Long(energyCompanyID));
	
	for( int i = 0; i < controlAreas.size(); i++ )
	{
		java.util.Vector progVector = ((com.cannontech.loadcontrol.data.LMControlArea) controlAreas.get(i)).getLmProgramVector();
		for( int j = 0; j < progVector.size(); j++ )
		{
			Object prog = progVector.elementAt(j);
				
			if( prog instanceof com.cannontech.loadcontrol.data.LMProgramEnergyExchange )
			{
				com.cannontech.loadcontrol.data.LMProgramEnergyExchange eeProg =
					(com.cannontech.loadcontrol.data.LMProgramEnergyExchange) prog;

				java.util.Vector customers = eeProg.getEnergyExchangeCustomers();
				for( int k = 0; k < customers.size(); k++ )
				{
					com.cannontech.loadcontrol.data.LMEnergyExchangeCustomer eeCust =
						(com.cannontech.loadcontrol.data.LMEnergyExchangeCustomer) customers.elementAt(k);

					long custID = eeCust.getDeviceID().longValue();

					// try to match this customer with customers this
					// energy company
					if( java.util.Arrays.binarySearch(eeCustomers, custID) >= 0 )
					{
						foundProgs.add(eeProg); 
						break;	
					}					
				}				
			}	
		}
		
	}

	com.cannontech.loadcontrol.data.LMProgramEnergyExchange[] retVal =
		new com.cannontech.loadcontrol.data.LMProgramEnergyExchange[foundProgs.size()];

	foundProgs.toArray(retVal);

	return retVal;*/
	return null;
}
/**
 * Creation date: (6/26/2001 3:30:05 PM)
 * @return com.cannontech.loadcontrol.data.LMCurtailCustomer
 * @param progID long
 */
public synchronized LMCurtailCustomer[] getCurtailmentCustomers(long progID) {

	LMCurtailCustomer[] retVal = null;

	Iterator i = curtailmentPrograms.iterator();
	while( i.hasNext() )
	{
		LMProgramCurtailment p = (LMProgramCurtailment) i.next();

		if (p.getYukonID().longValue() != progID)
			continue;

		// are these for sure all customers???
		// only god or wally knows
		Vector customers = p.getLoadControlGroupVector();

		if( customers != null )
		{
			retVal = new LMCurtailCustomer[customers.size()];
			customers.toArray(retVal);				
		}
		else
		{
			retVal = new LMCurtailCustomer[0];
		}			
	}

	return retVal;
}	
/*
	Returns the curtailment id of a program currently set to curtail.	
	returns -1 if the program is inactive.

	Note, currently hits the database since this is unavailable from the server.
*/
public long getCurtailmentID(long progID)
{
	return -1;
}
/**
 * This could be sped up quite a bit.
 * Creation date: (6/26/2001 3:45:59 PM)
 * @return com.cannontech.loadcontrol.data.LMProgramCurtailment
 * @param progID long
 */
public LMProgramCurtailment getCurtailmentProgram(long progID) {
	
	Iterator i = curtailmentPrograms.iterator();
	while( i.hasNext() )
	{
		LMProgramCurtailment p = (LMProgramCurtailment) i.next();

		if( p.getYukonID().longValue() == progID )
		{
			return p;
		}
	}

	return null;
}
/**
 * Returns the current baseline for the given customer.
 * Will be the 24 hourly values or null if not found.
 * Creation date: (11/26/2001 2:39:20 PM)
 * @return double[]
 * @param customerID long
 */
public double[] getCustomerBaseLine(long customerID) {
	java.util.Date start = com.cannontech.util.ServletUtil.getToday();
	java.util.Date end = com.cannontech.util.ServletUtil.getDate(+1);

	return getCustomerBaseLine(customerID, start, end );
}
/**
 * Returns the current baseline for the given customer.
 * Will be the 24 hourly values or null if not found.
 * Creation date: (11/26/2001 2:39:20 PM)
 * @return double[]
 * @param customerID long
 */
public double[] getCustomerBaseLine(long customerID, Date start, Date end) {
	//get point id of baseline point
	Integer baseLineID = (Integer) customerBaseLine.get( new Integer((int)customerID));
	
	if(baseLineID == null) {
		CTILogger.info("No customer baseline ID found for customer id: " + customerID);
		return null;
	}
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;	
	java.sql.ResultSet rset = null;
	
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
		pstmt = conn.prepareStatement(baseLineSql);

		pstmt.setInt(1, baseLineID.intValue());
		pstmt.setTimestamp(2, new java.sql.Timestamp(start.getTime()));
		pstmt.setTimestamp(3, new java.sql.Timestamp(end.getTime()));
		

		rset = pstmt.executeQuery();
	
		// Take the first 24 values, if there are more then
		// the baseline calcuation is screwed up.
		ArrayList values = new ArrayList(24);
		for( int i = 0; i < 24; i++ )
		{
			if( !rset.next() )
				return null;

			values.add( new Double(rset.getDouble(1)));	
		}
		
		double[] retVal = new double[values.size()];
		for( int i = 0; i < retVal.length; i++ )
				retVal[i] = ((Double) values.get(i)).doubleValue();

		return retVal;
	}
	catch(java.sql.SQLException e )
	{
	}
	finally
	{
		try {
			if( rset != null ) rset.close();
			if( pstmt != null ) pstmt.close();
			if( conn != null ) conn.close();			
		}catch(java.sql.SQLException e2) { }
				
	}

	// Something went wrong, lets blow this joint.
	return null;
}
	public LMProgramCurtailment[] getCustomerCurtailmentPrograms(long custID)
	{
		return null;
	}
public LMProgramEnergyExchange[] getCustomerEnergyExchangePrograms(long custID)
{
	ArrayList tempProgs = new ArrayList();	
	LMProgramEnergyExchange[] retVal= null;

	Iterator i = energyExchangePrograms.iterator();
	while( i.hasNext() )
	{
		LMProgramEnergyExchange p = (LMProgramEnergyExchange) i.next();
		
		Vector customers = p.getEnergyExchangeCustomers();
		if( customers != null )
		{
			Iterator cIter = customers.iterator();
			while( cIter.hasNext())
			{
				LMEnergyExchangeCustomer c = (LMEnergyExchangeCustomer) cIter.next();
				if( c.getCustomerID().longValue() == custID )
				{
					tempProgs.add(p);
					break;	
				}
			}
		}
		
	}	

	retVal = new LMProgramEnergyExchange[tempProgs.size()];
	tempProgs.toArray(retVal);
		
	return retVal;
}
/**
 * Creation date: (6/12/2001 9:37:54 AM)
 * @return java.lang.String
 */
public java.lang.String getDbAlias() {
	return dbAlias;
}
/**
 * Creation date: (7/24/2001 11:23:41 AM)
 * @return com.cannontech.loadcontrol.data.LMProgramDirect[]
 */
public LMProgramDirect[] getDirectPrograms() {
	LMProgramDirect[] p = new LMProgramDirect[directPrograms.size()];
	directPrograms.toArray(p);

	return p;
}

/**
 * Creation date: (7/24/2001 11:23:41 AM)
 * @return Enumeration
 */
public Enumeration getAllControlAreas() 
{
	return controlAreaMap.elements();
}

/**
 * 
 * @return LMControlArea
 */
public LMControlArea getControlArea( Integer areaID )
{
	return (LMControlArea)controlAreaMap.get( areaID );
}

/**
 * 
 * @return LMProgramBase
 */
public LMProgramBase getProgram( Integer progID )
{
	return (LMProgramBase)programMap.get( progID );
}

/**
 * Only returns LMGroupBase clases, this excludes customers
 * @return LMGroupBase
 */
public LMGroupBase getGroup( Integer grpID )
{
	return (LMGroupBase)groupMap.get( grpID );
}

/**
 * 
 * @return LMScenarioWrapper
 */
public LMScenarioWrapper getScenario( Integer scenarioID )
{
	LiteYukonPAObject[] scenarios = LMFuncs.getAllLMScenarios();
	for( int i = 0; i < scenarios.length; i++ )
	{
		if( scenarios[i].getYukonID() == scenarioID.intValue() )
		{
			LMScenarioWrapper scenario = new LMScenarioWrapper( scenarios[i] );
			return scenario;
		}
	}
	
	return null;
}

public synchronized LMProgramCurtailment[] getEnergyCompanyCurtailmentPrograms(long energyCompanyID)
{
	LMProgramCurtailment[] retVal = null;
	ArrayList tempProgs = new ArrayList();
	long[] ecCustomers = (long[]) energyCompanyCustomer.get( new Integer( (int) energyCompanyID) );

	if( ecCustomers != null )
	{
		Iterator pIter = curtailmentPrograms.iterator();
		while( pIter.hasNext() )
		{
			LMProgramCurtailment p = (LMProgramCurtailment) pIter.next();

			Vector customers = p.getLoadControlGroupVector();
			if( customers != null )
			{
				Iterator cIter = customers.iterator();
				while( cIter.hasNext() )
				{
					Object o = cIter.next();
					if( o instanceof LMCurtailCustomer )
					{
						LMCurtailCustomer c = (LMCurtailCustomer) o;
						if( java.util.Arrays.binarySearch(ecCustomers,c.getCustomerID().longValue()) >= 0 )
						{
							tempProgs.add(p);
							break;
						}
					}
				}
			}
		}	
	}

	retVal = new LMProgramCurtailment[tempProgs.size()];
	tempProgs.toArray(retVal);
		
	return retVal;
}
public synchronized LMProgramEnergyExchange[] getEnergyCompanyEnergyExchangePrograms(long energyCompanyID)
{
	ArrayList tempProgs = new ArrayList();
	
	LMProgramEnergyExchange[] retVal= null;

	long[] ecCustomers = (long[]) energyCompanyCustomer.get( new Integer( (int) energyCompanyID) );

	if( ecCustomers != null )
	{	
		Iterator i = energyExchangePrograms.iterator();
		while( i.hasNext() )
		{
			LMProgramEnergyExchange p = (LMProgramEnergyExchange) i.next();
			
			Vector customers = p.getEnergyExchangeCustomers();
			if( customers != null )
			{
				Iterator cIter = customers.iterator();
				while( cIter.hasNext())
				{
					LMEnergyExchangeCustomer c = (LMEnergyExchangeCustomer) cIter.next();

					if( java.util.Arrays.binarySearch( ecCustomers, c.getCustomerID().longValue() ) >= 0 )
					{
						tempProgs.add(p);
						break;	
					}
				}
			}
			
		}	
	}

	retVal = new LMProgramEnergyExchange[tempProgs.size()];
	tempProgs.toArray(retVal);
		
	return retVal;
}
/**
 * Creation date: (6/26/2001 3:30:05 PM)
 * @return com.cannontech.loadcontrol.data.LMCurtailCustomer
 * @param progID long
 */
public synchronized LMEnergyExchangeCustomer[] getEnergyExchangeCustomers(long progID) {

	LMEnergyExchangeCustomer[] retVal= null;
	
	Iterator i = energyExchangePrograms.iterator();
	while( i.hasNext() )
	{
		LMProgramEnergyExchange p = (LMProgramEnergyExchange) i.next();

		if( p.getYukonID().longValue() == progID )
		{
			Vector customers = p.getEnergyExchangeCustomers();
			if( customers != null )
			{
				retVal = new LMEnergyExchangeCustomer[customers.size()];
				customers.toArray(retVal);
			}
		}
	}

	if( retVal == null )
		retVal = new LMEnergyExchangeCustomer[0];
		
	return retVal;
}

private void handleGroups( LMProgramBase prog )
{
	for( int j = 0; j < prog.getLoadControlGroupVector().size(); j++ )
	{
		Object grp = prog.getLoadControlGroupVector().get(j);
		if( grp instanceof LMGroupBase )
			groupMap.put( ((LMGroupBase)grp).getYukonID(), grp );
	}

}

/**
 * Creation date: (6/21/2001 3:47:41 PM)
 * @param controlArea com.cannontech.loadcontrol.data.LMControlArea
 */
private synchronized void handleControlArea(LMControlArea controlArea) 
{	
	controlAreaMap.put( controlArea.getYukonID(), controlArea );
	

	java.util.Vector programs = controlArea.getLmProgramVector();

	for( int i = 0; i < programs.size(); i++ )
	{		
		LMProgramBase prog = (LMProgramBase)programs.elementAt(i);
		programMap.put( prog.getYukonID(), prog );
		handleGroups( prog );

		
		if( prog instanceof LMProgramDirect )
		{
			handleDirectProgram( (LMProgramDirect) prog );	
		}
		else
		if( prog instanceof LMProgramCurtailment )
		{
			handleCurtailmentProgram( (LMProgramCurtailment) prog );			
		}
		else
		if( prog instanceof LMProgramEnergyExchange )
		{
			handleEnergyExchangeProgram( (LMProgramEnergyExchange) prog );
		}
		else
		{
			CTILogger.info(getClass() + " - Warning, received unhandled program type");
		}
	}
}
/**
 * Adds or replaces prog in our list of curtailment programs.
 * Note it relies on program implementing .equals() to use the program id
 * Creation date: (6/21/2001 4:31:04 PM)
 * @param prog com.cannontech.loadcontrol.data.LMProgramCurtailment
 */
private void handleCurtailmentProgram(LMProgramCurtailment prog) 
{	
	replaceProgram(prog,curtailmentPrograms);
}
/**
 * Creation date: (6/21/2001 4:30:46 PM)
 * @param prog com.cannontech.loadcontrol.data.LMProgramDirect
 */
private void handleDirectProgram(LMProgramDirect prog) 
{
	replaceProgram(prog,directPrograms);
}


/**
 * Creation date: (6/21/2001 4:31:19 PM)
 * @param prog com.cannontech.loadcontrol.data.LMProgramEnergyExchange
 */
private void handleEnergyExchangeProgram(LMProgramEnergyExchange prog) 
{
	replaceProgram(prog, energyExchangePrograms);	
}
/**
 * Renew the cache.
 * Creation date: (6/11/2001 3:36:24 PM)
 */
public synchronized void refresh()
{

	CTILogger.info("Refreshing customer-energycompany mappings");
	
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
		CTILogger.info("Refreshing customer baselines");
		
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
			CTILogger.info("An error occured refreshing customerbaselines");			
		}
		finally
		{
			try {
				if( rset != null ) rset.close();
				if( stmt != null ) stmt.close();
				if( conn != null ) conn.close();
			} catch(java.sql.SQLException e2) {  }
		}

		CTILogger.info("Loaded " + customerBaseLine.size() + " customer baselines.");
	}
	
	CTILogger.info("Refreshing control areas");
	
	if( conn != null )
	{		
		com.cannontech.loadcontrol.messages.LMCommand c =
			new com.cannontech.loadcontrol.messages.LMCommand();

		c.setCommand( com.cannontech.loadcontrol.messages.LMCommand.RETRIEVE_ALL_CONTROL_AREAS);
		conn.write(c);

		refreshTimer.setDelay(normalRefreshRate);
	}
	
}
/**
 * Replaces a program in l.
 * uses device id to decide sameness.
 * I decided this would be more prudent than
 * relying on the programbase .equals()
 * Creation date: (7/1/2001 6:12:21 PM)
 * @return boolean
 * @param p com.cannontech.loadcontrol.data.LMProgramBase
 * @param l java.util.ArrayList
 */
private boolean replaceProgram(LMProgramBase p, ArrayList l) {
	
	java.util.ListIterator iter = l.listIterator();
	while( iter.hasNext() )
	{
		LMProgramBase cur = (LMProgramBase) iter.next();

		if( cur.getYukonID().equals(p.getYukonID()) )
		{
			iter.remove();
			iter.add(p);
			return true;
		}
	}

	// wasn't already in the list so add it
	l.add(p);
	return false;
}

/**
 * Creation date: (6/12/2001 9:37:54 AM)
 * @param newDbAlias java.lang.String
 */
public void setDbAlias(java.lang.String newDbAlias) {
	dbAlias = newDbAlias;
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

	if( arg instanceof LCChangeEvent )
	{
		// Arg is assumed to be a LCChangeEvent
		LCChangeEvent e = (LCChangeEvent) arg;

		if( conn == null )		
			conn = (com.cannontech.loadcontrol.LoadControlClientConnection) e.target;

		// e.arg is assumed to be a LMControlArea
		if( e.id == LCChangeEvent.DELETE )
		{
			LMControlArea lmArea = (LMControlArea)e.arg;
			controlAreaMap.remove( lmArea.getYukonID() );

			java.util.Vector programs = lmArea.getLmProgramVector();
			for( int i = 0; i < programs.size(); i++ )
			{		
				LMProgramBase prog = (LMProgramBase)programs.elementAt(i);
				programMap.remove( prog.getYukonID() );
				
				for( int j = 0; j < prog.getLoadControlGroupVector().size(); j++ )
				{
					Object grp = prog.getLoadControlGroupVector().get(j);
					if( grp instanceof LMGroupBase )
						groupMap.remove( ((LMGroupBase)grp).getYukonID() );
				}
			}
		}
		else if( e.id == LCChangeEvent.DELETE_ALL ) // remove all areas
		{
			controlAreaMap.clear();				
			programMap.clear();
			groupMap.clear();
		}		
		else
			handleControlArea( (LMControlArea) e.arg );
		
	}	
}

}