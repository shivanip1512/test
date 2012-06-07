package com.cannontech.stars.database.db.purchasing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;

public class PurchasePlan extends DBPersistent {

    private Integer purchaseID; 
    private Integer energyCompanyID;
    private String planName;
    private String poDesignation = "";
    private String accountingCode = "";
    private Date timePeriod;

    public static final String CONSTRAINT_COLUMNS[] = { "PurchaseID" };

    public static final String SETTER_COLUMNS[] = { "EnergyCompanyID", "PlanName", "PoDesignation", "AccountingCode", "TimePeriod"};

    public static final String TABLE_NAME = "PurchasePlan";


    public PurchasePlan() {
        super();
    }

    @Override
    public void add() throws java.sql.SQLException 
    {
        //set the creation date
        if(timePeriod == null)
            timePeriod = new Date();

        Object setValues[] = { getPurchaseID(), getEnergyCompanyID(), getPlanName(), getPoDesignation(),
                getAccountingCode(), getTimePeriod()};

        add( TABLE_NAME, setValues );

    }

    @Override
    public void delete() throws java.sql.SQLException 
    {
        Object constraintValues[] = { getPurchaseID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    @Override
    public void retrieve() throws java.sql.SQLException 
    {
        Object constraintValues[] = { getPurchaseID() };

        Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if( results.length == SETTER_COLUMNS.length )
        {
            setEnergyCompanyID( (Integer) results[0] );
            setPlanName( (String) results[1] );
            setPoDesignation( (String) results[2] );
            setAccountingCode( (String) results[3] );
            setTimePeriod( (Date) results[4] );
        }
        else
            throw new Error( getClass() + "::retrieve - Incorrect number of results" );

    }


    @Override
    public void update() throws java.sql.SQLException 
    {
        Object setValues[] = { getEnergyCompanyID(), getPlanName(), getPoDesignation(),
                getAccountingCode(), getTimePeriod()};

        Object constraintValues[] = { getPurchaseID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );

    }

    public static Integer getNextPurchaseID() {
        Integer nextValueId = YukonSpringHook.getNextValueHelper().getNextValue(TABLE_NAME);
        return nextValueId;
    }

    /**
     * Sort by date so that it is easy to show the most recently created plan
     */
    public static List<PurchasePlan> getAllPurchasePlans(Integer energyCompanyID)
    {
        List<PurchasePlan> plans = new ArrayList<PurchasePlan>();

        SqlStatement stmt = new SqlStatement("SELECT * FROM " + TABLE_NAME + " WHERE ENERGYCOMPANYID = " + energyCompanyID + " ORDER BY TIMEPERIOD DESC", CtiUtilities.getDatabaseAlias());

        try
        {
            stmt.execute();

            if( stmt.getRowCount() > 0 )
            {
                for( int i = 0; i < stmt.getRowCount(); i++ )
                {
                    PurchasePlan currentPlan = new PurchasePlan();
                    currentPlan.setPurchaseID( new Integer(stmt.getRow(i)[0].toString()));
                    currentPlan.setEnergyCompanyID( new Integer(stmt.getRow(i)[1].toString()));
                    currentPlan.setPlanName( stmt.getRow(i)[2].toString());
                    currentPlan.setPoDesignation( stmt.getRow(i)[3].toString());
                    currentPlan.setAccountingCode( stmt.getRow(i)[4].toString());
                    currentPlan.setTimePeriod(new Date(((java.sql.Timestamp)stmt.getRow(i)[5]).getTime()));

                    plans.add(currentPlan);
                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return plans;
    }

    /**
     * Sort by date so that it is easy to show the most recently created plan
     */
    public static List<PurchasePlan> getAllPurchasePlansForAllMembers(Integer ecID, List<LiteStarsEnergyCompany> members)
    {
        List<PurchasePlan> plans = new ArrayList<PurchasePlan>();

        StringBuffer orQuery = new StringBuffer();

        for(int j = 0; j < members.size(); j++)
        {
            orQuery.append(" OR ENERGYCOMPANYID = " + members.get(j).getLiteID());
        }

        SqlStatement stmt = new SqlStatement("SELECT * FROM " + TABLE_NAME + " WHERE ENERGYCOMPANYID = " + ecID + orQuery.toString() + " ORDER BY TIMEPERIOD DESC", CtiUtilities.getDatabaseAlias());

        try
        {
            stmt.execute();

            if( stmt.getRowCount() > 0 )
            {
                for( int i = 0; i < stmt.getRowCount(); i++ )
                {
                    PurchasePlan currentPlan = new PurchasePlan();
                    currentPlan.setPurchaseID( new Integer(stmt.getRow(i)[0].toString()));
                    currentPlan.setEnergyCompanyID( new Integer(stmt.getRow(i)[1].toString()));
                    currentPlan.setPlanName( stmt.getRow(i)[2].toString());
                    currentPlan.setPoDesignation( stmt.getRow(i)[3].toString());
                    currentPlan.setAccountingCode( stmt.getRow(i)[4].toString());
                    currentPlan.setTimePeriod(new Date(((java.sql.Timestamp)stmt.getRow(i)[5]).getTime()));

                    plans.add(currentPlan);
                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return plans;
    }



    public String getAccountingCode() {
        return accountingCode;
    }

    public void setAccountingCode(String accountingCode) {
        this.accountingCode = accountingCode;
    }

    public Integer getEnergyCompanyID() {
        return energyCompanyID;
    }

    public void setEnergyCompanyID(Integer energyCompanyID) {
        this.energyCompanyID = energyCompanyID;
    }

    public String getPoDesignation() 
    {
        return poDesignation;
    }

    public void setPoDesignation(String poDesignation) 
    {
        this.poDesignation = poDesignation;
    }

    public Integer getPurchaseID() {
        return purchaseID;
    }

    public void setPurchaseID(Integer purchaseID) {
        this.purchaseID = purchaseID;
    }

    public Date getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(Date timePeriod) {
        this.timePeriod = timePeriod;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

}
