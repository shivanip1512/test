package com.cannontech.stars.web.bean;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.db.customer.Address;
import com.cannontech.stars.database.db.hardware.Warehouse;

public class WarehouseAdminBean 
{
    private List<Warehouse> warehouses;    
    private Integer energyCompanyID;
    private Warehouse currentWarehouse;
    private Integer currentWarehouseID;
    private Address currentAddress;
    
    public List<Warehouse> getWarehouses()
    {
        if(energyCompanyID != null)
        {
            warehouses = Warehouse.getAllWarehousesForEnergyCompany(energyCompanyID.intValue());
            return warehouses;
        }
        
        return new ArrayList(0);
    }
    
    public void setWarehouses(List<Warehouse> newHouses)
    {
        warehouses = newHouses;
    }
    
    public Integer getEnergyCompanyID()
    {
        return energyCompanyID;
    }
    
    public void setEnergyCompanyID(Integer newID)
    {
        energyCompanyID = newID;
    }
    
    public Warehouse getCurrentWarehouse()
    {
        if(currentWarehouse != null)
            return currentWarehouse;
        else if(currentWarehouseID.intValue() == 0)
        {
            currentWarehouse = new Warehouse();
            currentWarehouse.setWarehouseName("");
            currentWarehouse.setNotes("");
            return currentWarehouse;
        }
        else
        {        
            Warehouse retrieveWarehouse = new Warehouse();
            retrieveWarehouse.setWarehouseID(currentWarehouseID);
            try
            {
                retrieveWarehouse = (Warehouse)Transaction.createTransaction(Transaction.RETRIEVE, retrieveWarehouse).execute();
                currentAddress = new Address();
                currentAddress.setAddressID(retrieveWarehouse.getAddressID());
                currentAddress = (Address)Transaction.createTransaction(Transaction.RETRIEVE, currentAddress).execute();
            }
            catch(TransactionException e)
            {
                CTILogger.error( "Error retrieving warehouse " + currentWarehouseID + ": " + e.getMessage(), e );
            }
            
            currentWarehouse = retrieveWarehouse;
        }
        
        return currentWarehouse;
    }
    
    public void setCurrentWarehouseID(int newID)
    {
        currentWarehouseID = new Integer(newID);
        currentWarehouse = null;
    }
    
    public Address getCurrentAddress()
    {
        return currentAddress;
    }
}
