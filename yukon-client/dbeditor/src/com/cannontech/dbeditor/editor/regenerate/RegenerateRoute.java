package com.cannontech.dbeditor.editor.regenerate;
/**
 * @author rneuharth
 * Jun 27, 2002 at 9:10:34 AM
 *
 * A undefined generated comment
 */
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.springframework.jdbc.support.JdbcUtils;

import com.cannontech.database.SqlUtils;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.route.CCURoute;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.route.CarrierRoute;
import com.cannontech.database.db.route.RepeaterRoute;
import com.cannontech.database.db.route.Route;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
public class RegenerateRoute
{
        public static final int LARGE_VALID_FIXED = 31;
        public static final int SMALL_VALID_FIXED = 0;
        public static final int LARGE_VALID_VARIABLE = 7;
        public static final int SMALL_VALID_VARIABLE = 0;
        
        private Vector currentMatriciesVector = new Vector(5);
        
        /**
         * Constructor for RegenerateRoute.
         */
        public RegenerateRoute()
        {
                super();
                initialize();
        }
        
        private void initialize() {
            buildMatrices();
        }
        
        /**
        * This method returns a vector of carrier routes that have repeaters associated with them
        * Uses the cache to retrieve all of the routes
        * Creation date: (5/28/2002 10:26:44 AM)
        */
        @SuppressWarnings("unchecked")
        public static final Vector getAllCarrierRoutes()
        {
                Vector realRoutes = new Vector();
                List routes;
                IDatabaseCache cache = DefaultDatabaseCache.getInstance();
                synchronized (cache)
                {
                        routes = cache.getAllRoutes();
                        DBPersistent rt = null;
                        //retrieves the repeater vector for each CCU route
                        try
                        {
                                for (int i = 0; i < routes.size(); i++)
                                {
                                        rt = LiteFactory.createDBPersistent((LiteBase) routes.get(i));
                                        if (rt instanceof CCURoute)
                                        {
                                                rt = Transaction.createTransaction(Transaction.RETRIEVE, rt).execute();
                                                if (((CCURoute) rt).getRepeaterVector().size() > 0)
                                                        realRoutes.add(rt);
                                        }
                                }
                                return realRoutes; //returns routes that have repeaters
                        }
                        catch (TransactionException t)
                        {
                                CTILogger.error( t.getMessage(), t );
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
     * If 'existingRoute' is true, it recalculates only for the existing route.  This is used when editing
     * an existing route.  The algorithm attempts to use the existing fixed bit.
     * @param routes java.util.Vector
     * @param all java.lang.boolean
     * @param routeToBeEdited DBPersistent
     * @param existingRoute java.lang.boolean
     * Creation date: (5/28/2002 12:57:11 PM)
     */
    @SuppressWarnings({ "unchecked", "unchecked" })
    public static final Vector resetRptSettings(java.util.Vector routes, boolean all, CCURoute routeToBeEdited, boolean existingRoute)
    {
        //      Initialize usedBitMatrix (of size 32 by 8) to all -1's.
        //      Indexed by CCUFixedBit, VariableBit.  Set to -1 if no routes have a claim.
        int[][] usedBitMatrix = new int[LARGE_VALID_FIXED + 1][LARGE_VALID_VARIABLE];
        for (int i=0; i <= LARGE_VALID_FIXED; i++) {
            for (int j=0; j < LARGE_VALID_VARIABLE; j++) {
                usedBitMatrix[i][j] = -1;
            }
        }

        CCURoute rt;
        Integer PaobjectId;
        Integer CcuFixedBits;
        Integer CcuVariableBits;
        String reset;
        String userLock;
        Vector changingRoutes = new Vector();  // Vector of routes that are to be modified.

        // Setup usedBitMatrix to know which fixed bits have already been used by placing the paoID of the route at the index
        // Only routes that not changing are going in this matrix, ie: routes that are locked, routes that are not locked but also not set to be reset
        // and "all" was not specified,  If "all" was specified, locked routes will still be put in this usedBitMatrix.
        for (int i = 0; i < routes.size(); i++)
        {
            rt = (CCURoute) routes.get(i);
            boolean currentIsntRouteToBeEdited = (existingRoute && rt.getPAObjectID().intValue() !=  routeToBeEdited.getRouteID().intValue());
            
            
            if (routeToBeEdited == null || !existingRoute || currentIsntRouteToBeEdited) {
                // Each route takes one or more slots in the matrix.  The CcuFixBits define the first offset, CcuVariableBits defines the first
                // slot in the matrix consumed by this route.  If there is only one repeater that is it, if more then more slots are used.

                CcuFixedBits = rt.getCarrierRoute().getCcuFixBits();               // The fixed bit value for this route.
                CcuVariableBits = rt.getCarrierRoute().getCcuVariableBits();       // The first variable bit used by this route is stored here
                reset = rt.getCarrierRoute().getResetRptSettings();                // Did the user request a reset on this route?
                userLock = rt.getCarrierRoute().getUserLocked();                   // Did the user forbid a reset on this route?
                PaobjectId = rt.getPAObjectID();


                // if locked, or (if not locked and not reset and not all).
                if (userLock.equals("Y") || (userLock.equals("N") && reset.equals("N") && !all)) {
                    // Mark each slot in the matrix as belonging to this route.

                    // First mark the CCU Variable bit
                    usedBitMatrix[CcuFixedBits.intValue()][CcuVariableBits.intValue()] = PaobjectId.intValue();

                    // Now mark out the repeater variable bits.  The last repeater is always a 7 and does not mark the matrix.
                    Vector rptVector = rt.getRepeaterVector();
                    for (int j=0; j < rptVector.size()-1; j++ ) {
                        com.cannontech.database.db.route.RepeaterRoute rpt = ((com.cannontech.database.db.route.RepeaterRoute) rptVector.get(j));
                        usedBitMatrix[CcuFixedBits.intValue()][rpt.getVariableBits().intValue()] = PaobjectId.intValue();
                    }
                } else {

                    if (routeToBeEdited == null)  // This must be the "all" case down here...  Add the route to the list if it is not locked and set for reset, or all is set.
                        changingRoutes.add(rt);
                }
            }
        }

        //if we are only setting a new route or adjusting an existing route it will be the only route changed.
        if (routeToBeEdited != null) {
                changingRoutes.add(routeToBeEdited);
        }
        
        //*****************************************************************************************************************
        // matrix is marked for all routes which are NOT moving.  The changingRoutes vector contains all the routes that are expected to move.
        //*****************************************************************************************************************

        CCURoute ccuRoute;
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

            ccuRoute = ((CCURoute) changingRoutes.get(k));
            repeatVector = ccuRoute.getRepeaterVector();
            int numRpts = repeatVector.size();
            int row = LARGE_VALID_FIXED;
            newCcuVariableBits = LARGE_VALID_VARIABLE;

            // An offset must be found with a run longer than numRpts.
            int offset;     // Start of the open slot.
            int run;        // Length of the open slot.

            if ( ccuRoute.getCarrierRoute().getUserLocked().equals("Y") ) {      // This route would prefer to stay where it is.
                boolean itfits = true;
                int fixbit = ccuRoute.getCarrierRoute().getCcuFixBits().intValue();
                int varbit = ccuRoute.getCarrierRoute().getCcuVariableBits().intValue();

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
            } else if (numRpts == 0) {
                row = LARGE_VALID_FIXED;
                newCcuVariableBits = LARGE_VALID_VARIABLE;
            }else {
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

            ccuRoute.getCarrierRoute().setCcuFixBits(new Integer(newCcuFixedBit));
            ccuRoute.getCarrierRoute().setCcuVariableBits(new Integer(newCcuVariableBits));
            if (newCcuFixedBit != LARGE_VALID_FIXED) {
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
                        if(ccuRoute.getPAObjectID() == null)
                        {
                            nextAvailableID = DaoFactory.getPaoDao().getNextPaoId();
                            ccuRoute.setRouteID(new Integer(nextAvailableID));
                        }
                        usedBitMatrix[newCcuFixedBit][q] = ccuRoute.getPAObjectID().intValue();
                    }
                }

            }

            ccuRoute.getCarrierRoute().setResetRptSettings("N");
        }

        return changingRoutes; //returns the routes whose settings have changed
    }

    /**
     * Insert the method's description here.
     * Creation date: (6/3/2002 1:47:36 PM)
     * @param routes java.util.Vector
     */
    @SuppressWarnings("unchecked")
    public final static void updateRouteRoles(Vector routes){
            try{
                    for (int i = 0; i < routes.size(); i++){
                        routes.set( i, Transaction.createTransaction(Transaction.UPDATE, ((DBPersistent) routes.get(i)) ).execute() );
                    }
            }
            catch (TransactionException t){
                    CTILogger.error( t.getMessage(), t );
            }
    }
    
    //*******************************************************************
    //*                              New and improved methods start here.                                    *
    //*******************************************************************
    
    /**
     * This method takes a vector of routes that will be changing, and returns a Vector of 31 by 7 matracies that hold
     * values (routeId's) at the index of all roles used by routes that will not change. ie: Routes that are user locked or 
     * not in the list of routes to be changed.
     * @param changingRoutes java.util.Vector
     * @author ASolberg
     */
    public Vector getCurrentRouteMatrixVector( Vector changingRoutes ) {
        
        if(changingRoutes != null) {
            Enumeration enummers = changingRoutes.elements();
            
            while(enummers.hasMoreElements()) {
                CCURoute route = (CCURoute)enummers.nextElement();
                String userLocked = route.getCarrierRoute().getUserLocked();
                if(userLocked.equalsIgnoreCase("N")) {
                    int maskedFixedBit = route.getCarrierRoute().getCcuFixBits().intValue();
                    int variableBit = route.getCarrierRoute().getCcuVariableBits().intValue(); 
                    int matrixNumber = maskedFixedBit / 32;
                    int realFixedBit = maskedFixedBit % 32;
                    int[][] matrix = (int[][])currentMatriciesVector.get(matrixNumber);
                    matrix[realFixedBit][variableBit] = -1;
                    Vector rptVector = route.getRepeaterVector();
                    for (int i=0; i < rptVector.size()-1; i++ ) {
                        RepeaterRoute rpt = ((RepeaterRoute) rptVector.get(i));
                        int rptVariableBits = rpt.getVariableBits().intValue();
                        matrix[realFixedBit][rptVariableBits] = -1;
                    }
                }
            }
        }
        
        return currentMatriciesVector;
    }
    
    public void removeChanginRoutes(Vector changingRoutes) {
        if(changingRoutes != null) {
            Enumeration enummers = changingRoutes.elements();
            
            while(enummers.hasMoreElements()) {
                CCURoute route = (CCURoute)enummers.nextElement();
                String userLocked = route.getCarrierRoute().getUserLocked();
                int maskedFixedBit = route.getCarrierRoute().getCcuFixBits().intValue();
                int variableBit = route.getCarrierRoute().getCcuVariableBits().intValue(); 
                int matrixNumber = maskedFixedBit / 32;
                int realFixedBit = maskedFixedBit % 32;
                int[][] matrix = (int[][])currentMatriciesVector.get(matrixNumber);
                if(variableBit < 7) {
                    matrix[realFixedBit][variableBit] = -1;
                    Vector rptVector = route.getRepeaterVector();
                    for (int i=0; i < rptVector.size()-1; i++ ) {
                        RepeaterRoute rpt = ((RepeaterRoute) rptVector.get(i));
                        int rptVariableBits = rpt.getVariableBits().intValue();
                        if(rptVariableBits < 7) {
                            matrix[realFixedBit][rptVariableBits] = -1;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * This method builds multiple matricies of all the route roles used by all routes and returns them in a vector.
     * @param changingRoutes java.util.Vector
     * @author ASolberg
     */
    private Vector buildMatrices() {
        
        for(int k = 0; k < 5; k++ ) {
            
            //Initialize usedBitMatrix (of size 32 by 8) to all -1's.
            int[][] usedBitMatrix = new int[LARGE_VALID_FIXED + 1][LARGE_VALID_VARIABLE];
            for (int i=0; i <= LARGE_VALID_FIXED; i++) {
                for (int j=0; j < LARGE_VALID_VARIABLE; j++) {
                    usedBitMatrix[i][j] = -1;
                }
            }
            currentMatriciesVector.add(k, usedBitMatrix);
        }
        
        Vector carrierRoute = getAllCarrierRoutes();
        Enumeration routeEnum = carrierRoute.elements();
        
        while(routeEnum.hasMoreElements()) {
            CCURoute route = (CCURoute)routeEnum.nextElement();
            int maskedFixedBit = route.getCarrierRoute().getCcuFixBits().intValue();
            int variableBit = route.getCarrierRoute().getCcuVariableBits().intValue(); 
            int matrixNumber = maskedFixedBit / 32;
            int realFixedBit = maskedFixedBit % 32;
            int[][] matrix = (int[][])currentMatriciesVector.get(matrixNumber);
            if(variableBit == 7){
                System.out.println("fixed:" +maskedFixedBit);
            }
            matrix[realFixedBit][variableBit] = route.getRouteID().intValue();
            
            Vector rptVector = route.getRepeaterVector();
            for (int i=0; i < rptVector.size()-1; i++ ) {
                RepeaterRoute rpt = ((RepeaterRoute) rptVector.get(i));
                int rptVariableBits = rpt.getVariableBits().intValue();
                matrix[realFixedBit][rptVariableBits] = route.getRouteID().intValue();
            }
        }
        
        return currentMatriciesVector;
    }
    
    private boolean checkRangeForBaddies(int fixed, int rangeStart, int rangeEnd, Vector baddies){
        boolean ok = true;
        for( int k = 0; k < 5; k++ ){
          int[][]otherMatrix = (int[][])currentMatriciesVector.get(k);
          for( int m = rangeStart; m <= rangeEnd; m++){
              int otherID = otherMatrix[fixed%32][m];
              if(!(otherID == -1)){
                  LiteYukonPAObject liteYukker = DaoFactory.getPaoDao().getLiteYukonPAO(otherID);
                  DBPersistent heavyRoute = LiteFactory.createDBPersistent(liteYukker);
                  java.sql.Connection conn = null;
                  try {
                      conn = com.cannontech.database.PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
                      heavyRoute.setDbConnection(conn);
                      heavyRoute.retrieve();
                  } catch (SQLException e) {
                      CTILogger.error(e);
                  } finally {
                	  SqlUtils.close(conn);
                  }
                  int deviceID = ((RouteBase)heavyRoute).getDeviceID().intValue();
                  LiteYukonPAObject liteCCUYuk = DaoFactory.getPaoDao().getLiteYukonPAO(deviceID);
                  if(baddies.contains(liteCCUYuk)){
                      return false;
                  }
              }
          }
      }
        
        return ok;
    }
    
    private boolean checkRangeForAnything(int fixed, int rangeStart, int rangeEnd){
        boolean ok = true;
        for( int k = 0; k < 5; k++ ){
          int[][]otherMatrix = (int[][])currentMatriciesVector.get(k);
          for( int m = rangeStart; m <= rangeEnd; m++){
              int otherID = otherMatrix[fixed%32][m];
              if(!(otherID == -1)){
                      return false;
              }
          }
      }
        
        return ok;
    }
    
    /**
     * This method finds the first slot big enough to fit the specified route starting in the first matrix and returns a 
     * RouteRole object that holds the fixbit and variable bit starting location that was found to fit and also a vector
     * of all the paoId's of ccu's that were also using the same fixed/variable bit combos being suggested.
     * @param route com.cannontech.database.db.route.Route
     * @author ASolberg
     */
    public RouteRole assignRouteLocation( CCURoute route, RouteRole oldRole, Vector blackList ) {
        
        RouteRole routeRole = new RouteRole();
        Vector duplicates = new Vector();
        // adjust the spots i start looking at
        int startingPointFixed;
        int startingPointVar;
        if(oldRole == null){
           startingPointFixed = 0;
           startingPointVar = 0;
           // try for a completly open slot
           
           Vector rptVector = route.getRepeaterVector();
           int size = rptVector.size();
           boolean success = false;
           int successFixedBit = -1;
           int runStartPoint = -1;
           for(int i = startingPointFixed; i <= 31; i++) {
               int run = 0;
               int matrixNumber = 0;
               int[][] matrix = (int[][])currentMatriciesVector.get(matrixNumber);
               for(int j = startingPointVar; j < 7; j++) {
                   if(matrix[i][j] == -1 ) {
                       run++;
                       if(run == size) {
                           // check other matricies
                           boolean ok = checkRangeForAnything(i, j - size + 1, j);
                           if(ok){
                               success = true;
                               runStartPoint = j - size + 1;
                               successFixedBit = i;
                               break;
                           }else{
                               run = 0;
                           }
                       }
                   }else{
                       run = 0;
                       if( (j + 1) + size > 7){
                           //don't continue looking on this fixed bit if there isn't enough room to fit the route anyway.
                           break;
                       }
                   }
               }
               if(success) {
                   routeRole.setFixedBit(successFixedBit);
                   routeRole.setVarbit(runStartPoint);
                   routeRole.setDuplicates(duplicates);
                   return routeRole;
               }
               startingPointVar = 0;
           }
           // we failed to find an unused slot, reset the starting spots to find a suitable slot to reuse.
           startingPointFixed = 0;
           startingPointVar = 0;
           
        }else{
            startingPointFixed = oldRole.getFixedBit();
            startingPointVar = oldRole.getVarbit();
        
            if(startingPointFixed == 154){
                startingPointFixed = 0;
            }
            if(startingPointVar == 6){
                startingPointVar = 0;
                startingPointFixed++;
            }else{
                startingPointVar++;
                if(startingPointVar + route.getRepeaterVector().size() > 7){
                    startingPointVar = 0;
                    startingPointFixed++;
                }
            }
        }
        // find suitable slot
        Vector rptVector = route.getRepeaterVector();
        int size = rptVector.size();
        boolean success = false;
        int successFixedBit = -1;
        int runStartPoint = -1;
        for(int i = startingPointFixed; i <= 155; i++) {
            int run = 0;
            int matrixNumber = i / 32;
            int[][] matrix = (int[][])currentMatriciesVector.get(matrixNumber);
            for(int j = startingPointVar; j < 7; j++) {
                if(matrix[i % 32][j] == -1 ) {
                    run++;
                    if(run >= size) {
                        
                        if(blackList == null){
                            success = true;
                            runStartPoint = j - size + 1;
                            successFixedBit = i;
                            break;
                        }else{
                            // verify this range isn't used by a ccu i don't like
                            boolean ok = checkRangeForBaddies( i, j-size+1, j, blackList );
                            if(ok){
                                success = true;
                                runStartPoint = j - size + 1;
                                successFixedBit = i;
                                break;
                            }
                        }
                    }
                }else{
                    run = 0;
                    if( (j + 1) + size > 7){
                        //don't continue looking on this fixed bit if there isn't enough room to fit the route anyway.
                        break;
                    }
                }
            }
            if(success) {
                routeRole.setFixedBit(successFixedBit);
                routeRole.setVarbit(runStartPoint);
                break;
            }
            startingPointVar = 0;
        }
        // look for other ccu's that may use these bits combos
        if(success) {
            for(int i = 0; i < currentMatriciesVector.size(); i++) {
                int[][] matrix = (int[][])currentMatriciesVector.get(i);
                for(int j = runStartPoint; j < (runStartPoint + size); j++) {
                    if(matrix[successFixedBit % 32][j] > -1) {
                        LiteYukonPAObject liteYuk = DaoFactory.getPaoDao().getLiteYukonPAO(matrix[successFixedBit % 32][j]);
                        DBPersistent heavyRoute = LiteFactory.createDBPersistent(liteYuk);
                        java.sql.Connection conn = null;
                        try {
                            conn = com.cannontech.database.PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
                            heavyRoute.setDbConnection(conn);
                            heavyRoute.retrieve();
                        } catch (SQLException e) {
                            CTILogger.error(e);
                        } finally {
                        	SqlUtils.close(conn);
                        }
                        int deviceID = ((RouteBase)heavyRoute).getDeviceID().intValue();
                        LiteYukonPAObject liteCCUYuk = DaoFactory.getPaoDao().getLiteYukonPAO(deviceID);
                        if(!duplicates.contains(liteCCUYuk)){
                            duplicates.add(liteCCUYuk);
                        }
                    }
                }
            }
        }
        routeRole.setDuplicates(duplicates);
        return routeRole;
    }
    
    public Vector findDuplicatesForBits(int fixed, int variable) {
        
        Vector dups = new Vector();
        
        for(int i = 0; i < 5; i++) {
            int[][] matrix = (int[][])currentMatriciesVector.get(i);
            Integer id = new Integer(matrix[fixed][variable]);
            if (id.intValue() > -1 ) {
                dups.add(id);
            }
        }
        if(dups.size() > 1) {
            return dups;
        }else {
            return null;
        }
    }
    
    public Vector findConflicts(int me, int fixed_, int variable_, int[] rptVariables_) {
        Vector conflicts = new Vector();
        Vector ids = new Vector();
        int maskedFixed = fixed_ % 32;
        for(int i = 0; i < 5; i++) {
            int[][] matrix = (int[][])currentMatriciesVector.get(i);
            Integer id = new Integer(matrix[maskedFixed][variable_]);
            if (id.intValue() > -1 && (!ids.contains(id)) && (id.intValue() != me)) {
                ids.add(id);
            }
            for(int j = 0; j < rptVariables_.length; j++) {
                Integer rptId = new Integer(matrix[maskedFixed][rptVariables_[j]]);
                if (rptId.intValue() > -1 && (!ids.contains(rptId)) && (rptId.intValue() != me)) {
                    ids.add(rptId);
                }
            }
        }
        
        for(int i = 0; i < ids.size(); i++) {
            LiteYukonPAObject liteYuk = DaoFactory.getPaoDao().getLiteYukonPAO(((Integer)ids.get(i)).intValue());
            DBPersistent heavyRoute = LiteFactory.createDBPersistent(liteYuk);
            java.sql.Connection conn = null;
            try {
                conn = com.cannontech.database.PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
                heavyRoute.setDbConnection(conn);
                heavyRoute.retrieve();
            } catch (SQLException e) {
                CTILogger.error(e);
            } finally {
            	SqlUtils.close(conn);
            }
            int deviceID = ((RouteBase)heavyRoute).getDeviceID().intValue();
            LiteYukonPAObject liteCCUYuk = DaoFactory.getPaoDao().getLiteYukonPAO(deviceID);
            if(!conflicts.contains(liteCCUYuk)){
                conflicts.add(liteCCUYuk);
            }
        }
        
        return conflicts;
    }
    
    public int findFixedBit(int maskedFixed, Vector variables) {
        int unMaskedFixed = -1;
        int run = 0;
        boolean success = false;
        for (int i = 0; i < 5; i++) {
            int[][] matrix = (int[][]) currentMatriciesVector.get(i);
            for(int j = 0; j < variables.size(); j++) {
                if(matrix[maskedFixed][((Integer)variables.get(j)).intValue()] == -1) {
                    run++;
                    if(run >= variables.size()) {
                        success = true;
                        break;
                    }
                }
            }
            if(success) {
                unMaskedFixed = (i * 32) + maskedFixed;
                break;
            }
        }
        
        return unMaskedFixed;
    }
}
