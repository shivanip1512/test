package com.cannontech.dbeditor.editor.regenerate;
/**
 * @author rneuharth
 * Jun 27, 2002 at 9:10:34 AM
 * 
 * A undefined generated comment
 */
import java.util.List;
import java.util.Vector;

import com.cannontech.database.data.route.CCURoute;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.route.CarrierRoute;
import com.cannontech.database.db.route.RepeaterRoute;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.core.dao.DaoFactory;
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
		IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
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
						rt = com.cannontech.database.Transaction.createTransaction(com.cannontech.database.Transaction.RETRIEVE, rt).execute();
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
     * If 'existingRoute is true, it recalculates only for the existing route.  This is used when editing
     * an existing route.  The algorithm attempts to use the existing fixed bit.
     * Creation date: (5/28/2002 12:57:11 PM)
     */
    public static final Vector resetRptSettings(java.util.Vector routes, boolean all, DBPersistent newRoute, boolean existingRoute)
    {

        // Indexed by CCUFixedBit, VariableBit.  Set to 0 if no routes have a claim.
        int[][] usedBitMatrix = new int[LARGE_VALID_FIXED][LARGE_VALID_VARIABLE];
        for (int i=0; i < LARGE_VALID_FIXED; i++) {
            for (int j=0; j < LARGE_VALID_VARIABLE; j++) {
                usedBitMatrix[i][j] = -1;
            }
        }

        DBPersistent rt;
        Integer PaobjectId;
        Integer CcuFixedBits;
        Integer CcuVariableBits;
        String reset;
        String userLock;
        Vector changingRoutes = new Vector();  // Vector of routes that are to be modified.

        //setting up matrix to know which fixed bits have already been used
        for (int i = 0; i < routes.size(); i++)
        {
            rt = (DBPersistent) routes.get(i);

            if (newRoute == null || !existingRoute || (existingRoute && ((RouteBase) rt).getPAObjectID().intValue() != ((RouteBase) newRoute).getRouteID().intValue())) {
                // Each route takes one or more slots in the matrix.  The CcuFixBits define the first offset, CcuVariableBits defines the first
                // slot in the matrix consumed by this route.  If there is only one repeater that is it, if more then more slots are used.

                CcuFixedBits = ((CCURoute) rt).getCarrierRoute().getCcuFixBits();               // The fixed bit value for this route.
                CcuVariableBits = ((CCURoute) rt).getCarrierRoute().getCcuVariableBits();       // The first variable bit used by this route is stored here
                reset = ((CCURoute) rt).getCarrierRoute().getResetRptSettings();                // Did the user request a reset on this route?
                userLock = ((CCURoute) rt).getCarrierRoute().getUserLocked();                   // Did the user forbid a reset on this route?
                PaobjectId = ((CCURoute) rt).getPAObjectID();


                // if locked, or (if not locked and not reset and not all).
                if (userLock.equals("Y") || (userLock.equals("N") && reset.equals("N") && !all)) {
                    // Mark each slot in the matrix as belonging to this route.

                    // First mark the CCU Variable bits
                    usedBitMatrix[CcuFixedBits.intValue()][CcuVariableBits.intValue()] = PaobjectId.intValue();

                    // Now mark out the repeater variable bits.  The last repeater is always a 7 and does not mark the matrix.
                    Vector rptVector = ((CCURoute) rt).getRepeaterVector();
                    for (int j=0; j < rptVector.size()-1; j++ ) {
                        com.cannontech.database.db.route.RepeaterRoute rpt = ((com.cannontech.database.db.route.RepeaterRoute) rptVector.get(j));
                        usedBitMatrix[CcuFixedBits.intValue()][rpt.getVariableBits().intValue()] = PaobjectId.intValue();
                    }
                } else {

                    if (newRoute == null)  // This must be the "all" case down here...  Add the route to the list if it is not locked and set for reset, or all is set.
                        changingRoutes.add(rt);
                }

            }
        }

        //if we are only setting a new route or adjusting an existing route it will be the only route changed.
        if (newRoute != null)
                changingRoutes.add(newRoute);

        // matrix is marked for all routes which are NOT moving.  The changingRoutes vector contains all the routes that are expected to move.
        // An enhancement could be made here if the changing routes were ordered by the number of repeaters.  Best fit probably has the largest
        // repeater count being inserted first... A later improvement?

        CCURoute ccu;
        int newCcuFixedBit = SMALL_VALID_FIXED;         // This is the first available fixed-bit slot that will fit my needs (if fail 31)
        int newCcuVariableBits = SMALL_VALID_VARIABLE;  // This if the first available var-bit offset that will fit my needs (if fail 7)
        int newRptVariableBits;                         // Used only for clarity.
        Vector repeatVector = null;

        //holds the used fixed bits, is null if no routes are using it, is -1 if two
        //routes are already using this fixed bit
        java.util.ArrayList usedFixedBits = new java.util.ArrayList(LARGE_VALID_FIXED + 1);

        //holds the value of the variable bit of the first CCU route that uses the fixed bit
        //the fixed bit is used as the index
        java.util.ArrayList varBits = new java.util.ArrayList(LARGE_VALID_FIXED + 1);


        for (int k = 0; k < changingRoutes.size(); k++) {

            ccu = ((CCURoute) changingRoutes.get(k));
            repeatVector = ccu.getRepeaterVector();
            int numRpts = repeatVector.size();
            int row = LARGE_VALID_FIXED;
            newCcuVariableBits = LARGE_VALID_VARIABLE;

            // An offset must be found with a run longer than numRpts.
            int offset;     // Start of the open slot.
            int run;        // Length of the open slot.

            if ( ccu.getCarrierRoute().getUserLocked().equals("Y") ) {      // This route would prefer to stay where it is.
                boolean itfits = true;
                int fixbit = ccu.getCarrierRoute().getCcuFixBits().intValue();
                int varbit = ccu.getCarrierRoute().getCcuVariableBits().intValue();

                if (usedBitMatrix[fixbit][varbit] != -1) {
                    itfits = false;
                } else {
                    for (int q = 0; q < numRpts-1; q++) {
                        com.cannontech.database.db.route.RepeaterRoute rpt = ((com.cannontech.database.db.route.RepeaterRoute) repeatVector.get(q));
                        if (usedBitMatrix[fixbit][rpt.getVariableBits().intValue()] != -1) {
                            itfits = false;
                            break;
                        }
                    }
                }

                if (itfits) {
                    row = fixbit;
                    newCcuVariableBits = varbit;
                }
            } else {
                // Go find out where the route should be placed.
                // This block could be a method
                // ##################################
                for (int fixed = 0; fixed < LARGE_VALID_FIXED; fixed++) {
                    offset = -1;
                    run = 0;
                    for (int variable = 0; variable < LARGE_VALID_VARIABLE; variable++) {

                        if (offset == -1 && usedBitMatrix[fixed][variable] == -1) {
                            offset = variable;
                            run = 1;
                        } else if (offset != -1) {
                            if (usedBitMatrix[fixed][variable] == -1) {
                                run++;  // The run of unused slots beginning at offset continues...
                            } else { // Any previously found offset and run are not sufficient for numRpts (If they were, the break below would have popped us out of the loops)
                                offset = -1;
                                run = 0;
                            }
                        }
                        if (run >= numRpts) break;
                    }
                    if (run >= numRpts) {
                        // Got it!
                        row = fixed;
                        newCcuVariableBits = offset;
                        break; // The outer for loop.
                    }
                }
            }

            newCcuFixedBit = row;
            // return newCcuFixedBit, newCcuVariableBits.
            // #####################################

            if (newCcuFixedBit != LARGE_VALID_FIXED) {
                ccu.getCarrierRoute().setCcuFixBits(new Integer(newCcuFixedBit));
                ccu.getCarrierRoute().setCcuVariableBits(new Integer(newCcuVariableBits));
                newRptVariableBits = newCcuVariableBits;

                for (int j = 0; j < repeatVector.size(); j++) {
                    com.cannontech.database.db.route.RepeaterRoute rpt = ((com.cannontech.database.db.route.RepeaterRoute) repeatVector.get(j));
                    if (newRptVariableBits + 1 <= LARGE_VALID_VARIABLE) newRptVariableBits++;
                    if (j+1 == repeatVector.size()) newRptVariableBits = LARGE_VALID_VARIABLE;  // Last rpt is always LARGE_VALID_VARIABLE!
                    rpt.setVariableBits(new Integer(newRptVariableBits));
                }

                // Mark the matrix as being owned by this paoid.
                for (int q = newCcuVariableBits; q < newCcuVariableBits + repeatVector.size(); q++) {
                    if (newCcuFixedBit < LARGE_VALID_FIXED && q < LARGE_VALID_VARIABLE) {
                        int nextAvailableID = 0;
                        if(ccu.getPAObjectID() == null)
                        {
                            nextAvailableID = DaoFactory.getPaoDao().getNextPaoId();
                            ccu.setRouteID(new Integer(nextAvailableID));
                        }
                        usedBitMatrix[newCcuFixedBit][q] = ccu.getPAObjectID().intValue();
                    }
                }

            }

            ccu.getCarrierRoute().setResetRptSettings("N");
        }

        return changingRoutes; //returns the routes whose settings have changed
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
            routes.set( i, 
				com.cannontech.database.Transaction.createTransaction(
                  com.cannontech.database.Transaction.UPDATE, 
                  ((DBPersistent) routes.get(i)) ).execute() );
			}
		}
		catch (com.cannontech.database.TransactionException t)
		{
			com.cannontech.clientutils.CTILogger.error( t.getMessage(), t );
		}
	}
}
