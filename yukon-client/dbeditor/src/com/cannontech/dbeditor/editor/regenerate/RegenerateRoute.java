package com.cannontech.dbeditor.editor.regenerate;
/**
 * @author rneuharth
 * Jun 27, 2002 at 9:10:34 AM
 * 
 * A undefined generated comment
 */
import com.cannontech.database.db.route.*;
import java.util.*;
import com.cannontech.database.data.route.*;
import com.cannontech.database.data.*;
import com.cannontech.database.db.*;
public class RegenerateRoute
{
	public static final int LARGE_VALID_FIXED = 31;
	public static final int SMALL_VALID_FIXED = 0;
	public static final int LARGE_VALID_VARIABLE = 7;
	public static final int SMALL_VALID_VARIABLE = 0;
	/**
	 * Constructor for RegenerateRoute.
	 */
	public RegenerateRoute()
	{
		super();
	}
	/**
	* This method returns a vector of carrier routes that have repeaters associated with them
	* Uses the cache to retrieve all of the routes
	* Creation date: (5/28/2002 10:26:44 AM)
	*/
	public static final java.util.Vector getAllCarrierRoutes()
	{
		Vector realRoutes = new Vector();
		List routes;
		com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		synchronized (cache)
		{
			routes = cache.getAllRoutes();
			DBPersistent rt = null;
			//retrieves the repeater vector for each CCU route
			try
			{
				for (int i = 0; i < routes.size(); i++)
				{
					rt = com.cannontech.database.data.lite.LiteFactory.createDBPersistent((com.cannontech.database.data.lite.LiteBase) routes.get(i));
					if (rt instanceof CCURoute)
					{
						com.cannontech.database.Transaction.createTransaction(com.cannontech.database.Transaction.RETRIEVE, rt).execute();
						if (((CCURoute) rt).getRepeaterVector().size() > 0)
							realRoutes.add(rt);
					}
				}
				return realRoutes; //returns routes that have repeaters
			}
			catch (com.cannontech.database.TransactionException t)
			{
				com.cannontech.clientutils.CTILogger.error( t.getMessage(), t );
				return null;
			}
		}
	}
	/**
	 * Returns the next available fixed bit
	 * Creation date: (5/28/2002 4:56:35 PM)
	 */
	public static final int getNextFixedBit(java.util.ArrayList rpts, int numRpts)
	{
		//first check to see if there exists and unused fixed bit
		for (int i = 0; i < LARGE_VALID_FIXED; i++)
		{
			if (rpts.get(i) == null)
				return i;
		}
		//if gets here all fixed bits have been used then we find a fixed bit that only has
		//one other route using it 
		for (int i = 0; i < LARGE_VALID_FIXED; i++)
		{
			if (((Integer) rpts.get(i)).intValue() == -1)
				continue;
			else
			{
				if (LARGE_VALID_VARIABLE - ((Integer) rpts.get(i)).intValue() >= numRpts && ((Integer) rpts.get(i)).intValue() >= 0)
					return i;
				else
					continue;
			}
		}
		return LARGE_VALID_FIXED;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/28/2002 10:52:29 AM)
	 * @param args java.lang.String[]
	 */
	public static void main(String[] args)
	{
		java.util.Vector routes;
		routes = getAllCarrierRoutes();
		resetRptSettings(routes, false, null, false);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/31/2002 10:49:57 AM)
	 */
	public static final int numCarrierRoutesReset(java.util.Vector routes)
	{
		String userLocked;
		String reset;
		int total = 0;
		for (int i = 0; i < routes.size(); i++)
		{
			userLocked = ((CCURoute) routes.get(i)).getCarrierRoute().getUserLocked();
			reset = ((CCURoute) routes.get(i)).getCarrierRoute().getResetRptSettings();
			if (userLocked.equals("Y"))
				continue;
			else if (reset.equals("Y"))
				total++;
		}
		return total;
	}
	/**
	 * This method recalculates the fixed and variable bits for carrier routes.
	 * If 'all' is true it recalculates for all of the routes.
	 * If 'newRoute' is not null, then it recalculates just for that new route that is passed in.
	 * Creation date: (5/28/2002 12:57:11 PM)
	 */
	public static final Vector resetRptSettings(java.util.Vector routes, boolean all, DBPersistent newRoute, boolean existingRoute)
	{
		//holds the used fixed bits, is null if no routes are using it, is -1 if two
		//routes are already using this fixed bit
		java.util.ArrayList usedFixedBits = new java.util.ArrayList(LARGE_VALID_FIXED + 1);
		//holds the value of the variable bit of the first CCU route that
		//uses the fixed bit as the index 
		java.util.ArrayList varBits = new java.util.ArrayList(LARGE_VALID_FIXED + 1);
		DBPersistent rt;
		Integer CcuFixedBits;
		Integer CcuVariableBits;
		String reset;
		String userLock;
		Vector changingRoutes = new Vector();
		//set fixed bits that have not been used to null in the array. Each index in 
		//the array represents the fixed bit  
		for (int i = 0; i < LARGE_VALID_FIXED + 1; i++)
		{
			usedFixedBits.add(i, null);
			varBits.add(i, null);
		}
		//setting up array to know which fixed bits have already been used
		//the value in the array gives the number of repeaters associated with that particular
		//fixed bit
		for (int i = 0; i < routes.size(); i++)
		{
			rt = (DBPersistent) routes.get(i);
			if (newRoute == null || !existingRoute || (existingRoute && ((RouteBase) rt).getPAObjectID().intValue() != ((RouteBase) newRoute).getRouteID().intValue()))
			{
				CcuFixedBits = ((CCURoute) rt).getCarrierRoute().getCcuFixBits();
				CcuVariableBits = ((CCURoute) rt).getCarrierRoute().getCcuVariableBits();
				reset = ((CCURoute) rt).getCarrierRoute().getResetRptSettings();
				userLock = ((CCURoute) rt).getCarrierRoute().getUserLocked();
				if (userLock.equals("Y") || (userLock.equals("N") && reset.equals("N") && !all))
				{
					if (userLock.equals("Y"))
					{
						usedFixedBits.set(CcuFixedBits.intValue(), new Integer(-1));
					}
					else if (usedFixedBits.get(CcuFixedBits.intValue()) == null)
					{
						usedFixedBits.set(CcuFixedBits.intValue(), new Integer(((CCURoute) rt).getRepeaterVector().size()));
						varBits.set(CcuFixedBits.intValue(), ((CarrierRoute) ((CCURoute) rt).getCarrierRoute()).getCcuVariableBits());
					}
					else
					{
						usedFixedBits.set(CcuFixedBits.intValue(), new Integer(-1));
					}
				}
				else
				{
					if (newRoute == null)
						changingRoutes.add(rt);
				}
			}
		}
		//if we are only setting a new route it will be the only route changed
		if (newRoute != null)
			changingRoutes.add(newRoute);
		//now use the next available fixed bit if there is one for each route that needs to be
		//recalculated
		CCURoute ccu;
		int nextFixedBit = SMALL_VALID_FIXED;
		int variable1 = SMALL_VALID_VARIABLE;
		int variable2 = SMALL_VALID_VARIABLE;
		int numRepeat = 0;
		Vector repeatVector = null;
		if (newRoute != null)
		{
			nextFixedBit = ((CCURoute) newRoute).getCarrierRoute().getCcuFixBits().intValue();
			repeatVector = ((CCURoute) newRoute).getRepeaterVector();
			if (usedFixedBits.get(nextFixedBit) != null)
			{
				numRepeat = ((Integer) usedFixedBits.get(nextFixedBit)).intValue();
			}
		}
		if ((newRoute != null && existingRoute)
			&& repeatVector.size() + numRepeat <= LARGE_VALID_VARIABLE
			&& nextFixedBit != LARGE_VALID_FIXED
			&& ((CCURoute) newRoute).getCarrierRoute().getUserLocked().equalsIgnoreCase("N"))
		{
			variable1 = ((CCURoute) newRoute).getCarrierRoute().getCcuVariableBits().intValue();
			variable2 = SMALL_VALID_VARIABLE;
			if (variable1 > 0)
			{
				variable1 = RegenerateRoute.LARGE_VALID_VARIABLE - ((CCURoute) newRoute).getRepeaterVector().size();
				variable2 = variable1 + 1;
			}
			else
			{
				variable2++;
			}
			for (int i = 0; i < ((CCURoute) newRoute).getRepeaterVector().size(); i++)
			{
				if (i + 1 >= ((CCURoute) newRoute).getRepeaterVector().size())
				{
					variable2 = RegenerateRoute.LARGE_VALID_VARIABLE;
				}
				((RepeaterRoute) ((CCURoute) newRoute).getRepeaterVector().get(i)).setVariableBits(new Integer(variable2));
				variable2++;
			}
			((CCURoute) newRoute).getCarrierRoute().setCcuVariableBits(new Integer(variable1));
		}
		else
		{
			for (int k = 0; k < changingRoutes.size(); k++)
			{
				ccu = ((CCURoute) changingRoutes.get(k));
				repeatVector = ccu.getRepeaterVector();
				nextFixedBit = getNextFixedBit(usedFixedBits, repeatVector.size());
				if (nextFixedBit == LARGE_VALID_FIXED)
				{
					variable1 = LARGE_VALID_VARIABLE;
					variable2 = LARGE_VALID_VARIABLE;
				}
				else if (usedFixedBits.get(nextFixedBit) == null)
				{
					variable1 = LARGE_VALID_VARIABLE - repeatVector.size();
					variable2 = variable1 + 1;
				}
				else
				{
					if (((Integer) varBits.get(nextFixedBit)).intValue() == SMALL_VALID_VARIABLE)
						variable1 = LARGE_VALID_VARIABLE - repeatVector.size();
					else
						variable1 = SMALL_VALID_VARIABLE;
					variable2 = variable1 + 1;
				}
				ccu.getCarrierRoute().setCcuFixBits(new Integer(nextFixedBit));
				ccu.getCarrierRoute().setCcuVariableBits(new Integer(variable1));
				for (int j = 0; j < repeatVector.size(); j++)
				{
					com.cannontech.database.db.route.RepeaterRoute rpt = ((com.cannontech.database.db.route.RepeaterRoute) repeatVector.get(j));
					if (j + 1 == repeatVector.size() || nextFixedBit == LARGE_VALID_FIXED)
						variable2 = LARGE_VALID_VARIABLE;
					rpt.setVariableBits(new Integer(variable2));
					variable2++;
				}
				if (usedFixedBits.get(nextFixedBit) == null)
					usedFixedBits.set(nextFixedBit, new Integer(repeatVector.size()));
				else
					usedFixedBits.set(nextFixedBit, new Integer(-1));
				varBits.set(nextFixedBit, ((CarrierRoute) ccu.getCarrierRoute()).getCcuVariableBits());
				ccu.getCarrierRoute().setResetRptSettings("N");
			}
		}
		return changingRoutes;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/3/2002 1:47:36 PM)
	 * @param routes java.util.Vector
	 */
	public final static void updateRouteRoles(Vector routes)
	{
		try
		{
			for (int i = 0; i < routes.size(); i++)
			{
				com.cannontech.database.Transaction.createTransaction(com.cannontech.database.Transaction.UPDATE, ((DBPersistent) routes.get(i))).execute();
			}
		}
		catch (com.cannontech.database.TransactionException t)
		{
			com.cannontech.clientutils.CTILogger.error( t.getMessage(), t );
		}
	}
}
