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
 * Builds up maps
 * Map<LiteYukonUser,int[]> and
 * 
 * If the user is not found in the Map that is created, the user should
 * then be an owner of all PAOs in the system. (backwards compatability)
 * 
 * @author ryan
 */

public class UserPoaOwnerLoader implements Runnable
{
	private Map allUsersToPAObjects;
	private List allUsers;
	private String dbAlias = null;

	public UserPoaOwnerLoader(Map allUserToPAOs, List allYukUsers, String dbAlias)
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

        HashMap userMap = new HashMap(allUsers.size()*2);

        Iterator i = allUsers.iterator();
        while(i.hasNext()) {
            LiteYukonUser u = (LiteYukonUser) i.next();
            userMap.put(new Integer(u.getUserID()), u);
        }
        
   		String sql1 =
   		    "SELECT UserID, PAOid" +
   		    " FROM " + UserPaoOwner.TABLE_NAME + " order by userid";
   							  
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

    public void populateMap( Map allUsersMap, ResultSet rset )
    	throws SQLException
    {
        Integer lastUserID = null;
        NativeIntVector niv = new NativeIntVector(32);
        Integer userID = null;
        
    	while(rset.next()) 
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
                {
                    allUsersToPAObjects.put(
                            (LiteYukonUser)allUsersMap.get(userID),
                            niv.toArray() );
                }

                niv = new NativeIntVector(32);
                lastUserID = userID;

                niv.add( paoID );
            }
    	}
        
        //get the last element if needed
        if( lastUserID != null )
        {
            allUsersToPAObjects.put(
                    (LiteYukonUser)allUsersMap.get(userID),
                    niv.toArray() );
        }
        
    }
   
   

}
