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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.LMDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.LMComparators;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMCurtailCustomer;
import com.cannontech.loadcontrol.data.LMEnergyExchangeCustomer;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramCurtailment;
import com.cannontech.loadcontrol.data.LMProgramEnergyExchange;
import com.cannontech.loadcontrol.data.LMScenarioWrapper;
import com.cannontech.spring.YukonSpringHook;

public class LoadcontrolCache implements java.util.Observer {

    /*TODO: The title of LoadControlCache is now somewhat of a misnomer, since all map management and event handling
     * is taken care of LoadControlClientConnection since it was doing it anyway.  It was easier to move functionality
     * from this class to LoadControlClientConnection rather than vice-versa; this needs to be improved a bit so naming
     * and functionality placement makes more sense.
     */
	//SQL to retrive customer base line point data
	private static String baseLineSql = "SELECT Value FROM RawPointhistory WHERE PointID=? AND Timestamp > ? AND Timestamp <= ?";
		
	private static int startupRefreshRate = 15 * 1000;
	private static int normalRefreshRate = 60 * 5 * 1000; //5 minutes
	private ScheduledExecutor refreshTimer = YukonSpringHook.getGlobalExecutor();
	
	private final LoadControlClientConnection lcConn;
	 
	private String dbAlias = "yukon";

	// key = (Integer) energy company id, value = (long[]) customer id
	private Map<Integer, long[]> energyCompanyCustomer = new HashMap<Integer, long[]>();
	
	// key = (Integer) customer, value = long energy company id
	private Map<Integer, Integer> customerEnergyCompany = new HashMap<Integer, Integer>();

	// key = (Integer) customer, value = Integer baseline pointid
	private Map<Integer, Integer> customerBaseLine = new HashMap<Integer, Integer>();
	
	
	//programs
	private List<LMProgramCurtailment> curtailmentPrograms = new ArrayList<LMProgramCurtailment>();
	private List<LMProgramEnergyExchange> energyExchangePrograms = new ArrayList<LMProgramEnergyExchange>();
	
/**
 * LoadcontrolCache constructor comment.
 */
public LoadcontrolCache(final LoadControlClientConnection lcConn) {
    this.lcConn = lcConn;
    
    Runnable timerTask = new Runnable() {
	    public void run() {
	        refresh();
	    }

	};
    refreshTimer.scheduleWithFixedDelay(timerTask, startupRefreshRate, normalRefreshRate, TimeUnit.MILLISECONDS);
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

	Iterator<LMProgramCurtailment> i = curtailmentPrograms.iterator();
	while( i.hasNext() )
	{
		LMProgramCurtailment p = i.next();

		if (p.getYukonID().longValue() != progID)
			continue;

		// are these for sure all customers???
		// only god or wally knows
		List<LMGroupBase> customers = p.getLoadControlGroupVector();

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
	
	Iterator<LMProgramCurtailment> i = curtailmentPrograms.iterator();
	while( i.hasNext() )
	{
		LMProgramCurtailment p = i.next();

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
	Integer baseLineID = customerBaseLine.get( new Integer((int)customerID));
	
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
		List<Double> values = new ArrayList<Double>(24);
		for( int i = 0; i < 24; i++ )
		{
			if( !rset.next() )
				return null;

			values.add( rset.getDouble(1));	
		}
		
		double[] retVal = new double[values.size()];
		for( int i = 0; i < retVal.length; i++ )
				retVal[i] = values.get(i);

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
	List<LMProgramEnergyExchange> tempProgs = new ArrayList<LMProgramEnergyExchange>();	
	LMProgramEnergyExchange[] retVal= null;

	Iterator<LMProgramEnergyExchange> i = energyExchangePrograms.iterator();
	while( i.hasNext() )
	{
		LMProgramEnergyExchange p = i.next();
		
		Vector<LMEnergyExchangeCustomer> customers = p.getEnergyExchangeCustomers();
		if( customers != null )
		{
			Iterator<LMEnergyExchangeCustomer> cIter = customers.iterator();
			while( cIter.hasNext())
			{
				LMEnergyExchangeCustomer c = cIter.next();
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

public LMProgramBase[] getDirectPrograms() {
	
	Set<LMProgramBase> programSet = new HashSet<LMProgramBase>();
	LMControlArea[] areas = lcConn.getAllLMControlAreas();
	// Get the programs from the control areas to make sure they are up to date
	for(LMControlArea area : areas) {
		Vector<LMProgramBase> programVector = area.getLmProgramVector();
		programSet.addAll(programVector);
	}
	
	return programSet.toArray(new LMProgramBase[]{});
}

/**
 * Returns the available control areas for the given user
 * 
 * @return Iterator
 */
public Iterator<LMControlArea> getAllControlAreas( LiteYukonUser yukUser, boolean overridePaoRestrictions )
{
    if( yukUser == null )
        return lcConn.getControlAreas().values().iterator(); //return all areas

    Iterator<LMControlArea> iter = lcConn.getControlAreas().values().iterator();
    List<LMControlArea> paoList = new ArrayList<LMControlArea>(32);
    while( iter.hasNext() )
    {
        LMControlArea area = iter.next();
        if( YukonSpringHook.getBean(AuthDao.class).userHasAccessPAO(yukUser, area.getYukonID().intValue()) || overridePaoRestrictions )
            paoList.add( area );
    }

    //sort the list base on the names
    Collections.sort( paoList, LMComparators.lmDataNameComp );
    return paoList.iterator();
}

/**
 * 
 * @return LMControlArea
 */
public LMControlArea getControlArea( Integer areaID )
{
	return lcConn.getControlArea( areaID );
}

/**
 * 
 * @return LMProgramBase
 */
public LMProgramBase getProgram( Integer progID )
{
	return lcConn.getProgram( progID );
}

/**
 * Only returns LMGroupBase clases, this excludes customers
 * @return LMGroupBase
 */
public LMGroupBase getGroup( Integer grpID )
{
	return lcConn.getGroup( grpID );
}

/**
 * 
 * @return LMScenarioWrapper
 */
public LMScenarioWrapper getScenario( Integer scenarioID )
{
	LiteYukonPAObject[] scenarios = YukonSpringHook.getBean(LMDao.class).getAllLMScenarios();
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
	List<LMProgramCurtailment> tempProgs = new ArrayList<LMProgramCurtailment>();
	long[] ecCustomers = energyCompanyCustomer.get( new Integer( (int) energyCompanyID) );

	if( ecCustomers != null )
	{
		Iterator<LMProgramCurtailment> pIter = curtailmentPrograms.iterator();
		while( pIter.hasNext() )
		{
			LMProgramCurtailment p = pIter.next();

			List<LMGroupBase> customers = p.getLoadControlGroupVector();
			if( customers != null )
			{
				Iterator<?> cIter = customers.iterator();
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
	List<LMProgramEnergyExchange> tempProgs = new ArrayList<LMProgramEnergyExchange>();
	
	LMProgramEnergyExchange[] retVal= null;

	long[] ecCustomers = energyCompanyCustomer.get( new Integer( (int) energyCompanyID) );

	if( ecCustomers != null )
	{	
		Iterator<LMProgramEnergyExchange> i = energyExchangePrograms.iterator();
		while( i.hasNext() )
		{
			LMProgramEnergyExchange p = i.next();
			
			Vector<LMEnergyExchangeCustomer> customers = p.getEnergyExchangeCustomers();
			if( customers != null )
			{
				Iterator<LMEnergyExchangeCustomer> cIter = customers.iterator();
				while( cIter.hasNext())
				{
					LMEnergyExchangeCustomer c = cIter.next();

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
	
	Iterator<LMProgramEnergyExchange> i = energyExchangePrograms.iterator();
	while( i.hasNext() )
	{
		LMProgramEnergyExchange p = i.next();

		if( p.getYukonID().longValue() == progID )
		{
			Vector<LMEnergyExchangeCustomer> customers = p.getEnergyExchangeCustomers();
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

/**
 * Renew the cache.
 * Creation date: (6/11/2001 3:36:24 PM)
 */
public synchronized void refresh()
{
	CTILogger.debug("Refreshing customer-energycompany mappings");
	
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

	CTILogger.debug("Refreshing customer baselines");
	
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
			customerBaseLine.put(customerID, pointID);			
		}
	}
	catch(java.sql.SQLException e)
	{
		CTILogger.debug("An error occured refreshing customerbaselines");			
	}
	finally
	{
		try {
			if( rset != null ) rset.close();
			if( stmt != null ) stmt.close();
			if( conn != null ) conn.close();
		} catch(java.sql.SQLException e2) {  }
	}
	CTILogger.debug("Loaded " + customerBaseLine.size() + " customer baselines.");
	
	CTILogger.debug("Refreshing control areas");
	
	if( lcConn != null )
	{		
		com.cannontech.loadcontrol.messages.LMCommand c =
			new com.cannontech.loadcontrol.messages.LMCommand();

		c.setCommand( com.cannontech.loadcontrol.messages.LMCommand.RETRIEVE_ALL_CONTROL_AREAS);
		lcConn.write(c);
        lcConn.addObserver( this );
	}
	
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
 * @param   arg   an argument passed to the <code>notifyObservers</code> method.
 */
public synchronized void update(java.util.Observable o, java.lang.Object arg) {}
	/*also, do we need to listen for a LCChangeEvent still and call the local update or something so the browser knows to refresh?*/
}