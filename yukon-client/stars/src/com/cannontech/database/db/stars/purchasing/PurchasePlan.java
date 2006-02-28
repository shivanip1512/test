package com.cannontech.database.db.stars.purchasing;

import java.util.*;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.DBPersistent;

public class PurchasePlan extends DBPersistent {

    private Integer purchaseID; 
    private Integer energyCompanyID;
    private String planName;
    private String poDesignation = "";
    private String accountingCode = "";
    private Date timePeriod;
    
    private List<DeliverySchedule> deliverySchedules;

    public static final String CONSTRAINT_COLUMNS[] = { "PurchaseID" };

    public static final String SETTER_COLUMNS[] = { "EnergyCompanyID", "PlanName", "PoDesignation", "AccountingCode", "TimePeriod"};

    public static final String TABLE_NAME = "PurchasePlan";
    

public PurchasePlan() {
    super();
}

public void add() throws java.sql.SQLException 
{
    //set the creation date
    if(timePeriod == null)
        timePeriod = new Date();
    
    Object setValues[] = { getPurchaseID(), getEnergyCompanyID(), getPlanName(), getPoDesignation(),
                            getAccountingCode(), getTimePeriod()};

    add( TABLE_NAME, setValues );
    
    for(int i = 0; i < getDeliverySchedules().size(); i++)
    {
        getDeliverySchedules().get(i).setPurchasePlanID(getPurchaseID());
        getDeliverySchedules().get(i).add();
    }
}

public void delete() throws java.sql.SQLException 
{
    Object constraintValues[] = { getPurchaseID() };

    for(int i = 0; i < getDeliverySchedules().size(); i++)
    {
        getDeliverySchedules().get(i).setPurchasePlanID(getPurchaseID());
        getDeliverySchedules().get(i).delete();
    }
        
    
    delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
}


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
    
    setDeliverySchedules(DeliverySchedule.getAllDeliverySchedulesForAPlan(purchaseID));
}


public void update() throws java.sql.SQLException 
{
    Object setValues[] = { getEnergyCompanyID(), getPlanName(), getPoDesignation(),
            getAccountingCode(), getTimePeriod()};
    		
    Object constraintValues[] = { getPurchaseID() };

    update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    
    for(int i = 0; i < getDeliverySchedules().size(); i++)
    {
        getDeliverySchedules().get(i).update();
    }
}

public static Integer getNextPurchaseID()
{
    Integer nextID = new Integer(1);
    
    SqlStatement stmt = new SqlStatement("SELECT MAX(PURCHASEID) + 1 FROM " + TABLE_NAME, CtiUtilities.getDatabaseAlias());
    
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
                
                currentPlan.setDeliverySchedules(DeliverySchedule.getAllDeliverySchedulesForAPlan(currentPlan.getPurchaseID()));
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
                
                currentPlan.setDeliverySchedules(DeliverySchedule.getAllDeliverySchedulesForAPlan(currentPlan.getPurchaseID()));
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

public List<DeliverySchedule> getDeliverySchedules() {
    if(deliverySchedules == null)
        deliverySchedules = new ArrayList<DeliverySchedule>();
    return deliverySchedules;
}

public void setDeliverySchedules(List<DeliverySchedule> deliverySchedules) {
    this.deliverySchedules = deliverySchedules;
}


}
