package com.cannontech.stars.database.db.purchasing;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;

public class DeliverySchedule extends DBPersistent {

    private Integer scheduleID; 
    private Integer purchasePlanID;
    private String scheduleName;
    private Integer modelID = CtiUtilities.NONE_ZERO_ID;
    private String styleNumber = "";
    private String orderNumber = "";
    private Double quotedPricePerUnit = new Double(0.00);

    public static final String CONSTRAINT_COLUMNS[] = { "ScheduleID" };

    public static final String SETTER_COLUMNS[] = { "PurchasePlanID", "ScheduleName", "ModelID",
        "StyleNumber", "OrderNumber", "QuotedPricePerUnit"};

    public static final String TABLE_NAME = "DeliverySchedule";


    public DeliverySchedule() {
        super();
    }

    @Override
    public void add() throws java.sql.SQLException 
    {
        Object setValues[] = { getScheduleID(), getPurchasePlanID(), 
                getScheduleName(), getModelID(), getStyleNumber(), getOrderNumber(),
                getQuotedPricePerUnit()};

        if(getScheduleID() == null)
            setScheduleID(getNextScheduleID());

        add( TABLE_NAME, setValues );

    }

    @Override
    public void delete() throws java.sql.SQLException 
    {
        Object constraintValues[] = { getScheduleID() };

        delete( ScheduleTimePeriod.TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }


    @Override
    public void retrieve() throws java.sql.SQLException 
    {
        Object constraintValues[] = { getScheduleID() };

        Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if( results.length == SETTER_COLUMNS.length )
        {
            setPurchasePlanID( (Integer) results[0] );
            setScheduleName( (String) results[1] );
            setModelID( (Integer) results[2] );
            setStyleNumber( (String) results[3]);
            setOrderNumber( (String) results[4] );
            setQuotedPricePerUnit( (Double) results[5] );
        }
        else
            throw new Error( getClass() + "::retrieve - Incorrect number of results" );

    }


    @Override
    public void update() throws java.sql.SQLException 
    {
        Object setValues[] = { getPurchasePlanID(), getScheduleName(), getModelID(), 
                getStyleNumber(), getOrderNumber(), getQuotedPricePerUnit()};

        Object constraintValues[] = { getScheduleID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );

    }

    public static final Integer getNextScheduleID() {
        Integer nextValueId = YukonSpringHook.getNextValueHelper().getNextValue(TABLE_NAME);
        return nextValueId;
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
                    currentSchedule.setStyleNumber( stmt.getRow(i)[4].toString() );
                    currentSchedule.setOrderNumber( stmt.getRow(i)[5].toString() );
                    currentSchedule.setQuotedPricePerUnit( new Double(stmt.getRow(i)[6].toString()));

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

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Double getQuotedPricePerUnit() {
        return quotedPricePerUnit;
    }

    public void setQuotedPricePerUnit(Double quotedPricePerUnit) {
        this.quotedPricePerUnit = quotedPricePerUnit;
    }

    public String getStyleNumber() {
        return styleNumber;
    }

    public void setStyleNumber(String styleNumber) {
        this.styleNumber = styleNumber;
    }


}
