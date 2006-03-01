package com.cannontech.database.db.stars.purchasing;

import java.util.*;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.DBPersistent;

public class DeliverySchedule extends DBPersistent {

    private Integer scheduleID; 
    private Integer purchasePlanID;
    private String scheduleName;
    private Integer modelID = CtiUtilities.NONE_ZERO_ID;
    
    private List<ScheduleTimePeriod> timePeriods;

    public static final String CONSTRAINT_COLUMNS[] = { "ScheduleID" };

    public static final String SETTER_COLUMNS[] = { "PurchasePlanID", "ScheduleName", "ModelID"};

    public static final String TABLE_NAME = "DeliverySchedule";
    

public DeliverySchedule() {
    super();
}

public void add() throws java.sql.SQLException 
{
    Object setValues[] = { getScheduleID(), getPurchasePlanID(), 
            getScheduleName(), getModelID(), };

    if(getScheduleID() == null)
        setScheduleID(getNextScheduleID());
    
    add( TABLE_NAME, setValues );

}

public void delete() throws java.sql.SQLException 
{
    Object constraintValues[] = { getScheduleID() };

    delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
}


public void retrieve() throws java.sql.SQLException 
{
    Object constraintValues[] = { getScheduleID() };

    Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

    if( results.length == SETTER_COLUMNS.length )
    {
        setPurchasePlanID( (Integer) results[0] );
        setScheduleName( (String) results[1] );
        setModelID( (Integer) results[2] );
    }
    else
        throw new Error( getClass() + "::retrieve - Incorrect number of results" );
    
}


public void update() throws java.sql.SQLException 
{
    Object setValues[] = { getPurchasePlanID(), getScheduleName(), getModelID()};
    		
    Object constraintValues[] = { getScheduleID() };

    update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    
}

public static Integer getNextScheduleID()
{
    Integer nextID = new Integer(0);
    
    SqlStatement stmt = new SqlStatement("SELECT MAX(SCHEDULEID) + 1 FROM " + TABLE_NAME, CtiUtilities.getDatabaseAlias());
    
    try
    {
        stmt.execute();
        
        if( stmt.getRowCount() > 0 )
        {
            nextID = new Integer(stmt.getRow(0)[0].toString());
        }
    }
    catch( Exception e )
    {
        e.printStackTrace();
    }
    
    return nextID;
}

public static List<DeliverySchedule> getAllDeliverySchedulesForAPlan(Integer purchasePlanID)
{
    List<DeliverySchedule> schedules = new ArrayList<DeliverySchedule>();
    
    SqlStatement stmt = new SqlStatement("SELECT * FROM " + TABLE_NAME + " WHERE PURCHASEPLANID = " + purchasePlanID, CtiUtilities.getDatabaseAlias());
    
    try
    {
        stmt.execute();
        
        if( stmt.getRowCount() > 0 )
        {
            for( int i = 0; i < stmt.getRowCount(); i++ )
            {
                DeliverySchedule currentSchedule = new DeliverySchedule();
                currentSchedule.setScheduleID( new Integer(stmt.getRow(i)[0].toString()));
                currentSchedule.setPurchasePlanID( new Integer(stmt.getRow(i)[1].toString()));
                currentSchedule.setScheduleName( stmt.getRow(i)[2].toString());
                currentSchedule.setModelID( new Integer(stmt.getRow(i)[3].toString()));
                
                schedules.add(currentSchedule);
            }
        }
    }
    catch( Exception e )
    {
        e.printStackTrace();
    }
    
    return schedules;
}

public Integer getPurchasePlanID() {
    return purchasePlanID;
}

public void setPurchasePlanID(Integer purchasePlanID) {
    this.purchasePlanID = purchasePlanID;
}

public Integer getModelID() {
    return modelID;
}

public void setModelID(Integer modelID) {
    this.modelID = modelID;
}

public Integer getScheduleID() {
    return scheduleID;
}

public void setScheduleID(Integer scheduleID) {
    this.scheduleID = scheduleID;
}

public String getScheduleName() {
    return scheduleName;
}

public void setScheduleName(String scheduleName) {
    this.scheduleName = scheduleName;
}


}
