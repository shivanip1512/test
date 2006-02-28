package com.cannontech.database.db.stars.purchasing;

import java.util.*;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.DBPersistent;

public class ScheduleTimePeriod extends DBPersistent {

    private Integer scheduleID; 
    private String timePeriodName;
    private Integer quantity = CtiUtilities.NONE_ZERO_ID;
    private Date predictedShipDate = new Date();
    
    public static final String CONSTRAINT_COLUMNS[] = { "ScheduleID" };

    public static final String SETTER_COLUMNS[] = { "TimePeriodName", "Quantity", "PredictedShipDate"};

    public static final String TABLE_NAME = "ScheduleTimePeriod";
    

public ScheduleTimePeriod() {
    super();
}

public void add() throws java.sql.SQLException 
{
    Object setValues[] = { getScheduleID(), getTimePeriodName(), 
                    getQuantity(), getPredictedShipDate()};

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
        setTimePeriodName( (String) results[0] );
        setQuantity( (Integer) results[1] );
        setPredictedShipDate( (Date) results[2] );
    }
    else
        throw new Error( getClass() + "::retrieve - Incorrect number of results" );
}


public void update() throws java.sql.SQLException 
{
    Object setValues[] = { getTimePeriodName(), getQuantity(), getPredictedShipDate()};
    		
    Object constraintValues[] = { getScheduleID() };

    update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}

/**
 * Sort by predicted ship date so that it is easy to show the most recent
 */
public static List<ScheduleTimePeriod> getAllTimePeriodsForDeliverySchedule(Integer schedID)
{
    List<ScheduleTimePeriod> periods = new ArrayList<ScheduleTimePeriod>();
    
    SqlStatement stmt = new SqlStatement("SELECT * FROM " + TABLE_NAME + " WHERE ENERGYCOMPANYID = " + schedID + " ORDER BY PREDICTEDSHIPDATE DESC", CtiUtilities.getDatabaseAlias());
    
    try
    {
        stmt.execute();
        
        if( stmt.getRowCount() > 0 )
        {
            for( int i = 0; i < stmt.getRowCount(); i++ )
            {
                ScheduleTimePeriod currentPlan = new ScheduleTimePeriod();
                currentPlan.setScheduleID( new Integer(stmt.getRow(i)[0].toString()));
                currentPlan.setTimePeriodName( stmt.getRow(i)[1].toString());
                currentPlan.setQuantity( new Integer(stmt.getRow(i)[2].toString()));
                currentPlan.setPredictedShipDate(new Date(((java.sql.Timestamp)stmt.getRow(i)[3]).getTime()));
                
                periods.add(currentPlan);
            }
        }
    }
    catch( Exception e )
    {
        e.printStackTrace();
    }
    
    return periods;
}

public Integer getQuantity() {
    return quantity;
}

public void setQuantity(Integer quantity) {
    this.quantity = quantity;
}

public Integer getScheduleID() {
    return scheduleID;
}

public void setScheduleID(Integer scheduleID) {
    this.scheduleID = scheduleID;
}

public String getTimePeriodName() {
    return timePeriodName;
}

public void setTimePeriodName(String timePeriodName) {
    this.timePeriodName = timePeriodName;
}

public Date getPredictedShipDate() {
    return predictedShipDate;
}

public void setPredictedShipDate(Date predictedShipDate) {
    this.predictedShipDate = predictedShipDate;
}


}
