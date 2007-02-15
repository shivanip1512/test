package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.NativeIntVector;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.user.UserPaoOwner;

/**
 * Builds up a map of:
 * Map<LiteYukonUser,int[]<paoID>> and
 * 
 * Notice: The int[] of PAOIds must always be sorted
 * 
 * If the user is not found in the Map that is created, the user should
 * then be an owner of all PAOs in the system. (backwards compatability)
 * 
 * @author ryan
 */

public class UserPoaOwnerLoader implements Runnable
{
	private Map<LiteYukonUser, int[]> allUsersToPAObjects;
	private List<LiteYukonUser> allUsers;
	private String dbAlias = null;

	public UserPoaOwnerLoader(Map<LiteYukonUser, int[]> allUserToPAOs, List<LiteYukonUser> allYukUsers, String dbAlias)
    {
        if( allYukUsers == null )
            throw new IllegalArgumentException("The List of YukonUsers must be populated");

        this.allUsersToPAObjects = allUserToPAOs;
		this.allUsers = allYukUsers;
        
		this.dbAlias = dbAlias;      	
   	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
   		long timerStart = System.currentTimeMillis();

        HashMap<Integer, LiteYukonUser> userMap = new HashMap<Integer, LiteYukonUser>(allUsers.size()*2);

        Iterator<LiteYukonUser> i = allUsers.iterator();
        while(i.hasNext()) {
            LiteYukonUser u = i.next();
            userMap.put(new Integer(u.getUserID()), u);
        }
        
        //paoIDs returned must always be sorted
   		String sql1 =
   		    "SELECT UserID, PAOid" +
   		    " FROM " + UserPaoOwner.TABLE_NAME + " order by userid, paoid";
   							  
      	Connection conn = null;
      	Statement stmt = null;
      	ResultSet rset = null;
      	try
        {
        	conn = PoolManager.getInstance().getConnection(dbAlias);
         	stmt = conn.createStatement();

         	rset = stmt.executeQuery(sql1);
   			populateMap( userMap, rset );
      	}
      	catch(SQLException e )
        {
         	CTILogger.error( e.getMessage(), e );
      	}
      	finally
        {
         	try
            {
            	if( stmt != null )
               		stmt.close();
            	if( conn != null )
               	conn.close();
         	}
         	catch( java.sql.SQLException e )
            {
            	CTILogger.error( e.getMessage(), e );
         	}
      
         	CTILogger.info(
    	        (System.currentTimeMillis() - timerStart)*.001 + 
    	        " Secs for UserPaoOwnerLoader (" + allUsersToPAObjects.size() + " loaded)" );   
      }
   
   }

    public void populateMap( Map<Integer, LiteYukonUser> allUsersMap, ResultSet rset )
    	throws SQLException
    {
        Integer lastUserID = null;
        NativeIntVector niv = new NativeIntVector(32);
        Integer userID = null;
        
    	while( rset.next() )
        {
    		userID = new Integer(rset.getInt(1));
    		int paoID = rset.getInt(2);

            if( userID.equals(lastUserID) )
            {
                niv.add( paoID );
            } 	
            else
            {
                if( lastUserID != null )
                    allUsersToPAObjects.put(
                            allUsersMap.get(lastUserID),
                            niv.toArray() );

                niv = new NativeIntVector(32);
                lastUserID = userID;

                niv.add( paoID );
            }
    	}
        
        //add the last set of paoIDs to the array
        if( niv.size() > 0 )
        	allUsersToPAObjects.put(
                allUsersMap.get(userID),
                niv.toArray() );
        
    }
   
   

}
