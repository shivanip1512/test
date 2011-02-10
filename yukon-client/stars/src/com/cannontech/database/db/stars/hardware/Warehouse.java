package com.cannontech.database.db.stars.hardware;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;

public class Warehouse extends DBPersistent {
    private Integer warehouseID;
    private String warehouseName;
    private Integer addressID;
    private String notes = "";
    private Integer energyCompanyID;

    public static final String CONSTRAINT_COLUMNS[] = { "WarehouseID" };

    public static final String SETTER_COLUMNS[] = { "WarehouseName","AddressID", "Notes", "EnergyCompanyID" };

    public static final String TABLE_NAME = "Warehouse";
    
    private Integer inventoryID;
    public static final String MAPPING_TABLE_NAME = "INVENTORYTOWAREHOUSEMAPPING";


public Warehouse() {
    super();
}

public static Integer getNextWarehouseID() {
    int nextValueId = YukonSpringHook.getNextValueHelper().getNextValue(TABLE_NAME);
    return nextValueId;
}

public void add() throws java.sql.SQLException 
{
    if(getWarehouseID() == null)
        setWarehouseID(getNextWarehouseID());
    
    Object setValues[] = { getWarehouseID(), getWarehouseName(), 
        getAddressID(), getNotes(), getEnergyCompanyID() };

    add( TABLE_NAME, setValues );
}

public void addPartial() throws java.sql.SQLException 
{
    Object setValues[] = { getWarehouseID(), getInventoryID() };
    
    add( MAPPING_TABLE_NAME, setValues );
}

public void delete() throws java.sql.SQLException 
{
    Object constraintValues[] = { getWarehouseID() };
    
    delete( MAPPING_TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
}

public void deletePartial() throws java.sql.SQLException 
{
    Object constraintValues[] = { getWarehouseID() };

    delete( MAPPING_TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
}

public Integer getWarehouseID() 
{
    return warehouseID;
}

public String getWarehouseName()
{
    return warehouseName;
}

public Integer getAddressID() 
{
    return addressID;
}

public String getNotes()
{
    return notes;
}

public Integer getEnergyCompanyID() 
{
    return energyCompanyID;
}

public void retrieve() throws java.sql.SQLException 
{
    Object constraintValues[] = { getWarehouseID() };

    Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

    if( results.length == SETTER_COLUMNS.length )
    {
        setWarehouseName( (String) results[0] );
        setAddressID( (Integer) results[1] );
        setNotes( (String) results[2] );
        setEnergyCompanyID( (Integer) results[3] );
    }
    else
        throw new Error( getClass() + "::retrieve - Incorrect number of results" );
}

public void setWarehouseID(Integer newID) 
{
    warehouseID = newID;
}

public void setWarehouseName(String newValue)
{
    warehouseName = newValue;
}

public void setAddressID(Integer newAddressID) 
{
    addressID = newAddressID;
}

public void setNotes(String newValue)
{
    notes = newValue;
}

public void setEnergyCompanyID(Integer newID) 
{
    energyCompanyID = newID;
}

public void update() throws java.sql.SQLException 
{
    Object setValues[] = { getWarehouseName(), 
            getAddressID(), getNotes(), getEnergyCompanyID() };
    
    Object constraintValues[] = { getWarehouseID() };

    update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}

public static List<Warehouse> getAllWarehousesForEnergyCompany(int ecID)
{
    List<Warehouse> warehouses = new ArrayList<Warehouse>();
    
    SqlStatement stmt = new SqlStatement("SELECT * FROM " + TABLE_NAME + " WHERE ENERGYCOMPANYID = " + ecID, CtiUtilities.getDatabaseAlias());
    
    try
    {
        stmt.execute();
        
        if( stmt.getRowCount() > 0 )
        {
            for( int i = 0; i < stmt.getRowCount(); i++ )
            {
                Warehouse myHouse = new Warehouse();
                myHouse.setWarehouseID(new Integer(stmt.getRow(i)[0].toString()));
                myHouse.setWarehouseName(stmt.getRow(i)[1].toString());
                myHouse.setAddressID(new Integer(stmt.getRow(i)[2].toString()));
                myHouse.setNotes(stmt.getRow(i)[3].toString());
                myHouse.setEnergyCompanyID(new Integer(stmt.getRow(i)[4].toString()));
                
                warehouses.add(myHouse);
            }
        }
    }
    catch( Exception e )
    {
        e.printStackTrace();
    }
    
    return warehouses;
}

public static List<Integer> getAllInventoryInAWarehouse(int warehouseID)
{
    List<Integer> inventory = new ArrayList<Integer>();
    
    SqlStatement stmt = new SqlStatement("SELECT INVENTORYID FROM " + MAPPING_TABLE_NAME + " WHERE WAREHOUSEID = " + warehouseID, CtiUtilities.getDatabaseAlias());
    
    try
    {
        stmt.execute();
        
        if( stmt.getRowCount() > 0 )
        {
            for( int i = 0; i < stmt.getRowCount(); i++ )
            {
                inventory.add((new Integer(stmt.getRow(i)[0].toString())));
            }
        }
    }
    catch( Exception e )
    {
        e.printStackTrace();
    }
    
    return inventory;
}

public static Integer getWarehouseIDFromInventoryID(int invenID)
{
    Integer warehouseID = new Integer(0);
    
    SqlStatement stmt = new SqlStatement("SELECT WAREHOUSEID FROM " + MAPPING_TABLE_NAME + " WHERE INVENTORYID = " + invenID, CtiUtilities.getDatabaseAlias());
    
    try
    {
        stmt.execute();
        
        if( stmt.getRowCount() > 0 )
        {
            for( int i = 0; i < stmt.getRowCount(); i++ )
            {
                warehouseID = (new Integer(stmt.getRow(i)[0].toString()));
            }
        }
    }
    catch( Exception e )
    {
        e.printStackTrace();
    }
    
    return warehouseID;
}

public static String getWarehouseNameFromInventoryID(int invenID)
{
    String name = "";
    
    SqlStatement stmt = new SqlStatement("SELECT W.WAREHOUSENAME FROM " + TABLE_NAME + " W, " + MAPPING_TABLE_NAME + " IWP WHERE IWP.WAREHOUSEID = W.WAREHOUSEID AND IWP.INVENTORYID = " + invenID, CtiUtilities.getDatabaseAlias());
    
    try
    {
        stmt.execute();
        
        if( stmt.getRowCount() > 0 )
        {
            for( int i = 0; i < stmt.getRowCount(); i++ )
            {
                name = stmt.getRow(i)[0].toString();
            }
        }
    }
    catch( Exception e )
    {
        e.printStackTrace();
    }
    
    return name;
}

public static boolean moveInventoryToAnotherWarehouse(int invenID, int newWarehouseID)
{
    boolean success = false;
    SqlStatement stmt;
    
    if(newWarehouseID == 0)
        stmt = new SqlStatement("DELETE FROM " + MAPPING_TABLE_NAME + " WHERE INVENTORYID = " + invenID, CtiUtilities.getDatabaseAlias());
    else 
    {
        Integer oldWarehouseID = getWarehouseIDFromInventoryID(invenID);
        if(oldWarehouseID == 0)
            stmt = new SqlStatement("INSERT INTO " + MAPPING_TABLE_NAME + " VALUES ( " + newWarehouseID + ", " + invenID + " ) ", CtiUtilities.getDatabaseAlias());
        else if(oldWarehouseID.intValue() != newWarehouseID)
        {
            stmt = new SqlStatement("UPDATE " + MAPPING_TABLE_NAME + " SET WAREHOUSEID = " + newWarehouseID + " WHERE INVENTORYID = " + invenID, CtiUtilities.getDatabaseAlias());
        }
        else 
            return false;
    }
        
        
    try
    {
        stmt.execute();
        success = true;
    }
    catch( Exception e )
    {
        e.printStackTrace();
        success = false;
    }
    
    return success;
}

public static Integer getEnergyCompanyIDFromWarehouseID(Integer houseID)
{
    Integer energyCompanyID = new Integer(0);
    
    SqlStatement stmt = new SqlStatement("SELECT ENERGYCOMPANYID FROM " + TABLE_NAME + " WHERE WAREHOUSEID = " + houseID.toString(), CtiUtilities.getDatabaseAlias());
    
    try
    {
        stmt.execute();
        
        if( stmt.getRowCount() > 0 )
        {
            for( int i = 0; i < stmt.getRowCount(); i++ )
            {
                energyCompanyID = (new Integer(stmt.getRow(i)[0].toString()));
            }
        }
    }
    catch( Exception e )
    {
        e.printStackTrace();
    }
    
    return energyCompanyID;
}

public Integer getInventoryID() {
    return inventoryID;
}

public void setInventoryID(Integer inventoryID) {
    this.inventoryID = inventoryID;
}

public static Warehouse getWarehouseFromInventoryID(int invenID)
{
    Warehouse myHouse = new Warehouse();
    myHouse.setWarehouseID(new Integer(0));
    myHouse.setWarehouseName(CtiUtilities.STRING_NONE);
    
    SqlStatement stmt = new SqlStatement("SELECT * FROM " + TABLE_NAME + " WHERE WAREHOUSEID IN (SELECT WAREHOUSEID FROM " + MAPPING_TABLE_NAME + " WHERE INVENTORYID = " + invenID + ")", CtiUtilities.getDatabaseAlias());
    
    try
    {
        stmt.execute();
        
        if( stmt.getRowCount() > 0 )
        {
            myHouse.setWarehouseID(new Integer(stmt.getRow(0)[0].toString()));
            myHouse.setWarehouseName(stmt.getRow(0)[1].toString());
            myHouse.setAddressID(new Integer(stmt.getRow(0)[2].toString()));
            myHouse.setNotes(stmt.getRow(0)[3].toString());
            myHouse.setEnergyCompanyID(new Integer(stmt.getRow(0)[4].toString()));
        }
    }
    catch( Exception e )
    {
        e.printStackTrace();
    }
    
    return myHouse;
}

public static List<Integer> getAllInventoryNotInAWarehouse()
{
    List<Integer> inventory = new ArrayList<Integer>();
    
    SqlStatement stmt = new SqlStatement("SELECT INVENTORYID FROM " + LMHardwareBase.TABLE_NAME + " WHERE INVENTORYID NOT IN (SELECT INVENTORYID FROM " + MAPPING_TABLE_NAME + ")", CtiUtilities.getDatabaseAlias());
    
    try
    {
        stmt.execute();
        
        if( stmt.getRowCount() > 0 )
        {
            for( int i = 0; i < stmt.getRowCount(); i++ )
            {
                inventory.add((new Integer(stmt.getRow(i)[0].toString())));
            }
        }
    }
    catch( Exception e )
    {
        e.printStackTrace();
    }
    
    return inventory;
}
}
