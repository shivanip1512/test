package com.cannontech.stars.database.db.purchasing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;

public class ScheduleTimePeriod extends DBPersistent {

    private Integer timePeriodID;
    private Integer scheduleID; 
    private String timePeriodName;
    private Integer quantity = CtiUtilities.NONE_ZERO_ID;
    private Date predictedShipDate = new Date();

    public static final String CONSTRAINT_COLUMNS[] = { "TimePeriodID" };

    public static final String SETTER_COLUMNS[] = { "ScheduleID", "TimePeriodName", "Quantity", "PredictedShipDate"};

    public static final String TABLE_NAME = "ScheduleTimePeriod";


    public ScheduleTimePeriod() {
        super();
    }

    @Override
    public void add() throws java.sql.SQLException 
    {
        Object setValues[] = { getTimePeriodID(), getScheduleID(), getTimePeriodName(), 
                getQuantity(), getPredictedShipDate()};

        add( TABLE_NAME, setValues );
    }

    @Override
    public void delete() throws java.sql.SQLException 
    {
        Object constraintValues[] = { getTimePeriodID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }


    @Override
    public void retrieve() throws java.sql.SQLException 
    {
        Object constraintValues[] = { getTimePeriodID() };

        Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if( results.length == SETTER_COLUMNS.length )
        {
            setScheduleID( (Integer) results[0] );
            setTimePeriodName( (String) results[1] );
            setQuantity( (Integer) results[2] );
            setPredictedShipDate( (Date) results[3] );
        }
        else
            throw new Error( getClass() + "::retrieve - Incorrect number of results" );
    }


    @Override
    public void update() throws java.sql.SQLException 
    {
        Object setValues[] = { getScheduleID(), getTimePeriodName(), getQuantity(), getPredictedShipDate()};

        Object constraintValues[] = { getTimePeriodID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public static Integer getNextTimePeriodID() {
        Integer nextValueId = YukonSpringHook.getNextValueHelper().getNextValue(TABLE_NAME);
        return nextValueId;
    }

    /**
     * Sort by predicted ship date so that it is easy to show the most recent
     */
    public static List<ScheduleTimePeriod> getAllTimePeriodsForDeliverySchedule(Integer schedID)
    {
        List<ScheduleTimePeriod> periods = new ArrayList<ScheduleTimePeriod>();

        SqlStatement stmt = new SqlStatement("SELECT * FROM " + TABLE_NAME + " WHERE SCHEDULEID = " + schedID + " ORDER BY PREDICTEDSHIPDATE DESC", CtiUtilities.getDatabaseAlias());

        try
        {
            stmt.execute();

            if( stmt.getRowCount() > 0 )
            {
                for( int i = 0; i < stmt.getRowCount(); i++ )
                {
                    ScheduleTimePeriod currentPeriod = new ScheduleTimePeriod();
                    currentPeriod.setTimePeriodID( new Integer(stmt.getRow(i)[0].toString()));
                    currentPeriod.setScheduleID( new Integer(stmt.getRow(i)[1].toString()));
                    currentPeriod.setTimePeriodName( stmt.getRow(i)[2].toString());
                    currentPeriod.setQuantity( new Integer(stmt.getRow(i)[3].toString()));
                    currentPeriod.setPredictedShipDate(new Date(((java.sql.Timestamp)stmt.getRow(i)[4]).getTime()));

                    periods.add(currentPeriod);
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

    public Integer getTimePeriodID() {
        return timePeriodID;
    }

    public void setTimePeriodID(Integer timePeriodID) {
        this.timePeriodID = timePeriodID;
    }


}
