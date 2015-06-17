package com.cannontech.database.db.season;

import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Insert the type's description here.
 * Creation date: (6/22/2004 10:35:21 AM)
 * @author: 
 */
public class SeasonSchedule extends com.cannontech.database.db.DBPersistent
{
	private Integer scheduleID;
	private String scheduleName;


	public static final String SETTER_COLUMNS[] = 
	{ 
		"ScheduleName"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "ScheduleID" };

	public static final String TABLE_NAME = "SeasonSchedule";
/**
 * SeasonSchedule constructor comment.
 */
public SeasonSchedule() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 12:08:19 PM)
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getScheduleId(), getScheduleName()};

	add( TABLE_NAME, addValues );
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 12:08:42 PM)
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getScheduleId());
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 1:31:31 PM)
 */
public Integer getScheduleId()
{
	return scheduleID;
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 1:34:49 PM)
 */
public String getScheduleName() {
	return scheduleName;}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public final static Integer getNextSeasonScheduleID()
{
	IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List seasonSchedules = cache.getAllSeasonSchedules();
		java.util.Collections.sort(seasonSchedules);

		int counter = 1;
		int currentID;
		 														
		for(int i=0;i<seasonSchedules.size();i++)
		{
			currentID = ((com.cannontech.database.data.lite.LiteSeasonSchedule)seasonSchedules.get(i)).getScheduleID();

			if( currentID > counter )
				break;
			else
				counter = currentID + 1;
		}		
		
		return new Integer( counter );
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 12:08:08 PM)
 */
public void retrieve() throws java.sql.SQLException
{
	Object constraintValues[] = { getScheduleId()};

	Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

	if (results.length == SETTER_COLUMNS.length)
	{
		setScheduleName((String) results[0]);		
	}

}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 1:39:07 PM)
 * @param id java.lang.Integer
 */
public void setScheduleID(Integer id) {
	scheduleID = id;
	}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 1:38:37 PM)
 * @param name java.lang.String
 */
public void setScheduleName(String name) {
	scheduleName = name;
	
	}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 12:08:59 PM)
 */
public void update() throws java.sql.SQLException
{
	Object setValues[] = { getScheduleName()};
	Object constraintValues[] = { getScheduleId()};

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
}

/**
 * This method returns all SeasonSchedule currently
 * in the database with all their attributes populated
 *
 */
@SuppressWarnings("unchecked")
public static SeasonSchedule[] getAllCBCSchedules() {
    java.sql.Connection conn = null;
    java.sql.PreparedStatement pstmt = null;
    java.sql.ResultSet rset = null;
    Vector vect = new Vector(32);

   //Get all the data from the database                
   String sql = "select " + 
       "ScheduleID, ScheduleName" +
       " from " + TABLE_NAME + " order by ScheduleName";

    try {       
        conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

        if( conn == null ) {
            throw new IllegalStateException("Error getting database connection.");
        }
        else {
            pstmt = conn.prepareStatement(sql.toString());          
            rset = pstmt.executeQuery();

            while( rset.next() ) {
                Integer scheduleId = new Integer (rset.getInt(1));
                if(scheduleId != 0) {  // ignore the 'Empty Schedule' schedule for capcontrol purposes
                    SeasonSchedule cbcSS = new SeasonSchedule();
    
                    cbcSS.setScheduleID( scheduleId );
                    cbcSS.setScheduleName( rset.getString(2) );
                    vect.add( cbcSS );  
                }
            }
        }       
    }
    catch( java.sql.SQLException e ) {
        CTILogger.error( e.getMessage(), e );
    }
    finally {
        SqlUtils.close(rset, pstmt, conn );
    }


    SeasonSchedule[] strats = new SeasonSchedule[vect.size()];
    return (SeasonSchedule[])vect.toArray( strats );
}

}
