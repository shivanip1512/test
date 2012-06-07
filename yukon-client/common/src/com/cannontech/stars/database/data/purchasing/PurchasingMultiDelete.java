package com.cannontech.stars.database.data.purchasing;

import java.util.List;

import com.cannontech.database.db.DBPersistent;
import com.cannontech.stars.database.db.purchasing.DeliverySchedule;
import com.cannontech.stars.database.db.purchasing.Invoice;
import com.cannontech.stars.database.db.purchasing.PurchasePlan;
import com.cannontech.stars.database.db.purchasing.Shipment;

public class PurchasingMultiDelete extends DBPersistent 
{
    private PurchasePlan purchasePlan;
    private List<DeliverySchedule> schedules;
    private List<Shipment> shipments;
    private List<Invoice> invoices;
    
    public static final String CONSTRAINT_COLUMNS[] = { "PurchaseID" };

    public static final String TABLE_NAME = "PurchasePlan";
    

public PurchasingMultiDelete() {
    super();
}

public void add() throws java.sql.SQLException 
{
    throw new Error( getClass() + "::add - Method not supported." );
}

public void delete() throws java.sql.SQLException 
{
    for(int j = 0; j < shipments.size(); j++)
    {
        shipments.get(j).setDbConnection(getDbConnection());
        shipments.get(j).delete();
    }
    
    for(int i = 0; i < schedules.size(); i++)
    {
        schedules.get(i).setDbConnection(getDbConnection());
        schedules.get(i).delete();
    }
    
    for(int j = 0; j < invoices.size(); j++)
    {
        invoices.get(j).setDbConnection(getDbConnection());
        invoices.get(j).delete();
    }
    
    purchasePlan.setDbConnection(getDbConnection());
    purchasePlan.delete();
}

public void retrieve() throws java.sql.SQLException 
{
    throw new Error( getClass() + "::retrieve - Method not supported." );
}


public void update() throws java.sql.SQLException 
{
    throw new Error( getClass() + "::update - Method not supported." );
}

public List<Invoice> getInvoices() {
    return invoices;
}

public void setInvoices(List<Invoice> invoices) {
    this.invoices = invoices;
}

public PurchasePlan getPurchasePlan() {
    return purchasePlan;
}

public void setPurchasePlan(PurchasePlan purchasePlan) {
    this.purchasePlan = purchasePlan;
}

public List<DeliverySchedule> getSchedules() {
    return schedules;
}

public void setSchedules(List<DeliverySchedule> schedules) {
    this.schedules = schedules;
}

public List<Shipment> getShipments() {
    return shipments;
}

public void setShipments(List<Shipment> shipments) {
    this.shipments = shipments;
}


}
