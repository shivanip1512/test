package com.cannontech.database.data.route;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.route.RepeaterRoute;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

@SuppressWarnings("deprecation")
public class RouteUsageHelper {
    
    public static final int LARGE_VALID_FIXED = 31;
    public static final int SMALL_VALID_FIXED = 0;
    public static final int LARGE_VALID_VARIABLE = 7;
    public static final int SMALL_VALID_VARIABLE = 0;

    private Vector<int[][]> currentMatriciesVector = new Vector<int[][]>(5);

    public RouteUsageHelper() {
        super();
        buildMatrices();
    }
    
    public Vector<int[][]> getRouteUsage() {
        return currentMatriciesVector;
    }

    /**
     * This method returns a vector of carrier routes that have repeaters associated with them
     * Uses the cache to retrieve all of the routes
     */
    public static final List<CCURoute> getAllCarrierRoutes() {
        List<CCURoute> realRoutes = Lists.newArrayList();
        List<LiteYukonPAObject> routes;
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            routes = cache.getAllRoutes();
            DBPersistent rt = null;
            // retrieves the repeater vector for each CCU route
            try {
                for (int i = 0; i < routes.size(); i++) {
                    rt = LiteFactory.createDBPersistent((LiteBase) routes.get(i));
                    if (rt instanceof CCURoute) {
                        rt = Transaction.createTransaction(Transaction.RETRIEVE, rt).execute();
                        CCURoute ccuRoute = (CCURoute) rt;
                        if (ccuRoute.getRepeaterVector().size() > 0) {
                            realRoutes.add(ccuRoute);
                        }
                    }
                }
                return realRoutes; // returns routes that have repeaters
            } catch (TransactionException t) {
                CTILogger.error(t.getMessage(), t);
                return null;
            }
        }
    }

    public void removeChanginRoutes(Vector<CCURoute> changingRoutes) {
        
        if (changingRoutes != null) {
            
            Iterator<CCURoute> iter = changingRoutes.iterator();
            
            while (iter.hasNext()) {
                CCURoute route = iter.next();
                
                int maskedFixedBit = route.getCarrierRoute().getCcuFixBits();
                int variableBit = route.getCarrierRoute().getCcuVariableBits(); 
                int matrixNumber = maskedFixedBit / 32;
                int realFixedBit = maskedFixedBit % 32;
                int[][] matrix = currentMatriciesVector.get(matrixNumber);
                
                if (variableBit < 7) {
                    
                    matrix[realFixedBit][variableBit] = -1;
                    Vector<RepeaterRoute> rptVector = route.getRepeaterVector();
                    
                    for (int i=0; i < rptVector.size()-1; i++ ) {
                        
                        RepeaterRoute rpt = rptVector.get(i);
                        int rptVariableBits = rpt.getVariableBits();
                        
                        if (rptVariableBits < 7) {
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
    private Vector<int[][]> buildMatrices() {
        
        for (int k = 0; k < 5; k++ ) {
            
            //Initialize usedBitMatrix (of size 32 by 7) to all -1's.
            int[][] usedBitMatrix = new int[LARGE_VALID_FIXED + 1][LARGE_VALID_VARIABLE];
            for (int i=0; i <= LARGE_VALID_FIXED; i++) {
                for (int j=0; j < LARGE_VALID_VARIABLE; j++) {
                    usedBitMatrix[i][j] = -1;
                }
            }
            currentMatriciesVector.add(k, usedBitMatrix);
        }
        
        Iterator<CCURoute> iter = getAllCarrierRoutes().iterator();
        
        while (iter.hasNext()) {
            CCURoute route = (CCURoute)iter.next();
            
            int maskedFixedBit = route.getCarrierRoute().getCcuFixBits();
            int variableBit = route.getCarrierRoute().getCcuVariableBits();
            int matrixNumber = maskedFixedBit / 32;
            int realFixedBit = maskedFixedBit % 32;
            int[][] matrix = currentMatriciesVector.get(matrixNumber);
            
            if (variableBit < 7) {
                matrix[realFixedBit][variableBit] = route.getRouteID();
                
                Vector<RepeaterRoute> rptVector = route.getRepeaterVector();
                
                for (int i=0; i < rptVector.size() - 1; i++ ) {
                    
                    RepeaterRoute rpt = rptVector.get(i);
                    int rptVariableBits = rpt.getVariableBits();
                    matrix[realFixedBit][rptVariableBits] = route.getRouteID();
                    
                }
            }
        }
        
        return currentMatriciesVector;
    }
    
    private boolean isRangeInUse(int fixed, int rangeStart, int rangeEnd, Vector<LiteYukonPAObject> avoidCCUs) {
        
        for (int k = 0; k < 5; k++) {
        
            int[][] otherMatrix =  currentMatriciesVector.get(k);
            
            for (int m = rangeStart; m <= rangeEnd; m++) {
                
                int otherID = otherMatrix[fixed % 32][m];
            
                if (!(otherID == -1)) {
                    
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
                    
                    int deviceID = ((RouteBase) heavyRoute).getDeviceID();
                    LiteYukonPAObject liteCCUYuk = DaoFactory.getPaoDao().getLiteYukonPAO(deviceID);
                    
                    if (avoidCCUs.contains(liteCCUYuk)) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    private boolean isRangeAvailable(int fixed, int rangeStart, int rangeEnd) {

        for (int k = 0; k < 5; k++) {

            int[][] otherMatrix = currentMatriciesVector.get(k);
            
            for (int m = rangeStart; m <= rangeEnd; m++) {
                int otherID = otherMatrix[fixed % 32][m];
                if (!(otherID == -1)) {
                    return false;
                }
            }
            
        }

        return true;
    }
    
    /**
     * This method finds the first slot big enough to fit the specified route starting in the first matrix and returns a 
     * RouteRole object that holds the fixbit and variable bit starting location that was found to fit and also a vector
     * of all the paoId's of ccu's that were also using the same fixed/variable bit combos being suggested.
     */
    public RouteRole assignRouteLocation(CCURoute route, RouteRole oldRole, Vector<LiteYukonPAObject> avoidCCUs) {
        
        RouteRole routeRole = new RouteRole();
        Vector<LiteYukonPAObject> duplicates = new Vector<LiteYukonPAObject>();
        
        // adjust the spots i start looking at
        int startingPointFixed;
        int startingPointVar;
        
        if (oldRole == null) {
           startingPointFixed = 0;
           startingPointVar = 0;
           // try for a completly open slot
           
           int size = route.getRepeaterVector().size();
           boolean success = false;
           int successFixedBit = -1;
           int runStartPoint = -1;
           
           for (int i = startingPointFixed; i <= 31; i++) {
               
               int run = 0;
               int matrixNumber = 0;
               int[][] matrix = currentMatriciesVector.get(matrixNumber);
               
               for (int j = startingPointVar; j < 7; j++) {
                   
                   if (matrix[i][j] == -1 ) {
                       
                       run++;
                       
                       if (run == size) {
                           // check other matricies
                           int rangeStart = j - size + 1;
                           
                           if (isRangeAvailable(i, rangeStart, j)){
                               
                               success = true;
                               runStartPoint = rangeStart;
                               successFixedBit = i;
                               break;
                               
                           } else {
                               run = 0;
                           }
                       }
                   } else {
                       run = 0;
                       if ( (j + 1) + size > 7 ){
                           //don't continue looking on this fixed bit if there isn't enough room to fit the route anyway.
                           break;
                       }
                   }
               }
               if (success) {
                   
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
           
        } else {
            
            startingPointFixed = oldRole.getFixedBit();
            startingPointVar = oldRole.getVarbit();
        
            if (startingPointFixed == 154) {
                startingPointFixed = 0;
            }
            
            if (startingPointVar == 6) {
                
                startingPointVar = 0;
                startingPointFixed++;
                
            } else {
                
                startingPointVar++;
                if(startingPointVar + route.getRepeaterVector().size() > 7){
                    startingPointVar = 0;
                    startingPointFixed++;
                }
                
            }
        }
        
        // find suitable slot
        int size = route.getRepeaterVector().size();
        boolean success = false;
        int successFixedBit = -1;
        int runStartPoint = -1;
        
        for (int i = startingPointFixed; i <= 155; i++) {
            
            int run = 0;
            int matrixNumber = i / 32;
            int[][] matrix = currentMatriciesVector.get(matrixNumber);
            
            for (int j = startingPointVar; j < 7; j++) {
                
                if (matrix[i % 32][j] == -1 ) {
                    
                    run++;
                    if (run >= size) {
                        
                        if (avoidCCUs == null) {
                            
                            success = true;
                            runStartPoint = j - size + 1;
                            successFixedBit = i;
                            break;
                            
                        } else {

                            // verify this range isn't used by a ccu i don't like
                            if (isRangeInUse( i, runStartPoint, j, avoidCCUs )) {
                                
                                success = true;
                                runStartPoint = j - size + 1;
                                successFixedBit = i;
                                break;
                                
                            }
                        }
                    }
                } else {
                    
                    run = 0;
                    if ( (j + 1) + size > 7) {
                        //don't continue looking on this fixed bit if there isn't enough room to fit the route anyway.
                        break;
                    }
                }
            }
            if (success) {
                routeRole.setFixedBit(successFixedBit);
                routeRole.setVarbit(runStartPoint);
                break;
            }
            startingPointVar = 0;
        }
        // look for other ccu's that may use these bits combos
        if (success) {
            
            for (int i = 0; i < currentMatriciesVector.size(); i++) {
                
                int[][] matrix = currentMatriciesVector.get(i);
                
                for (int j = runStartPoint; j < (runStartPoint + size); j++) {
                    
                    if (matrix[successFixedBit % 32][j] > -1) {
                        
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
                        
                        int deviceID = ((RouteBase)heavyRoute).getDeviceID();
                        LiteYukonPAObject liteCCUYuk = DaoFactory.getPaoDao().getLiteYukonPAO(deviceID);
                        
                        if (!duplicates.contains(liteCCUYuk)){
                            duplicates.add(liteCCUYuk);
                        }
                    }
                }
            }
        }
        
        routeRole.setDuplicates(duplicates);
        return routeRole;
    }
    
    public Vector<Integer> findDuplicatesForBits(int fixed, int variable) {
        
        Vector<Integer> dups = new Vector<Integer>();
        
        for (int i = 0; i < 5; i++) {
            
            int[][] matrix = currentMatriciesVector.get(i);
            Integer id = new Integer(matrix[fixed][variable]);
            
            if (id > -1 ) {
                dups.add(id);
            }
        }
        
        return dups.size() > 1 ? dups : null;
    }
    
    public Vector<LiteYukonPAObject> findConflicts(int me, int fixed, int variable, int[] rptVariables) {
        
        Vector<LiteYukonPAObject> conflicts = new Vector<LiteYukonPAObject>();
        Vector<Integer> ids = new Vector<Integer>();
        
        int maskedFixed = fixed % 32;
        
        for (int i = 0; i < 5; i++) {
            
            int[][] matrix = currentMatriciesVector.get(i);
            Integer id = new Integer(matrix[maskedFixed][variable]);
            
            if (id > -1 && (!ids.contains(id)) && (id != me)) {
                ids.add(id);
            }
            
            for (int j = 0; j < rptVariables.length; j++) {
                
                Integer rptId = matrix[maskedFixed][rptVariables[j]];
                
                if (rptId > -1 && (!ids.contains(rptId)) && (rptId != me)) {
                    ids.add(rptId);
                }
                
            }
        }
        
        for (int i = 0; i < ids.size(); i++) {
            
            LiteYukonPAObject liteYuk = DaoFactory.getPaoDao().getLiteYukonPAO(((Integer)ids.get(i)));
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
            
            int deviceID = ((RouteBase)heavyRoute).getDeviceID();
            LiteYukonPAObject liteCCUYuk = DaoFactory.getPaoDao().getLiteYukonPAO(deviceID);
            
            if (!conflicts.contains(liteCCUYuk)){
                conflicts.add(liteCCUYuk);
            }
        }
        
        return conflicts;
    }
    
    public int findFixedBit(int maskedFixed, Vector<Integer> variables) {
        
        int unMaskedFixed = -1;
        int run = 0;
        boolean success = false;
        
        for (int i = 0; i < 5; i++) {
            
            int[][] matrix = currentMatriciesVector.get(i);
            
            for(int j = 0; j < variables.size(); j++) {
                
                if (matrix[maskedFixed][variables.get(j)] == -1) {
                    run++;
                    if (run >= variables.size()) {
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