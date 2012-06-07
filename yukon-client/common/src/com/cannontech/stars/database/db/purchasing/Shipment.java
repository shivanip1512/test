package com.cannontech.stars.database.db.purchasing;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;

public class Shipment extends DBPersistent {

    private Integer shipmentID;
    private String shipmentNumber;
    private Integer warehouseID = new Integer(0);
    private String serialNumberStart = "";
    private String serialNumberEnd = "";
    private Date shipDate = new Date();
    private Double actualPricePerUnit = new Double(0.00);
    private Double salesTotal = new Double(0.00);
    private Double salesTax = new Double(0.00);
    private Double otherCharges = new Double(0.00);
    private Double shippingCharges = new Double(0.00);
    private Double amountPaid = new Double(0.00);
    private Date orderedDate = new Date();
    private Date receivedDate = new Date();

    public static final String CONSTRAINT_COLUMNS[] = { "ShipmentID" };

    public static final String SETTER_COLUMNS[] = { "ShipmentNumber", "WarehouseID", "SerialNumberStart", "SerialNumberEnd", 
        "ShipDate", "ActualPricePerUnit", "SalesTotal", "SalesTax", "OtherCharges", "ShippingCharges", "AmountPaid",
        "OrderedDate", "ReceivedDate"};

    public static final String TABLE_NAME = "Shipment";

    private Integer scheduleID;
    public static final String SCHED_MAPPING_TABLE_NAME = "ScheduleShipmentMapping";
    public static final String INVOICE_MAPPING_TABLE_NAME = "InvoiceShipmentMapping";


    public Shipment() {
        super();
    }

    @Override
    public void add() throws java.sql.SQLException 
    {
        Object setValues[] = { getShipmentID(), getShipmentNumber(), getWarehouseID(), getSerialNumberStart(), getSerialNumberEnd(),
                getShipDate(), getActualPricePerUnit(), getSalesTotal(), getSalesTax(), getOtherCharges(), getShippingCharges(), 
                getAmountPaid(), getOrderedDate(), getReceivedDate()};

        add( TABLE_NAME, setValues );
        addPartial();
    }

    @Override
    public void addPartial() throws java.sql.SQLException 
    {
        Object setValues[] = { getScheduleID(), getShipmentID() };

        add( SCHED_MAPPING_TABLE_NAME, setValues );
    }

    @Override
    public void delete() throws java.sql.SQLException 
    {
        Object constraintValues[] = { getShipmentID() };

        delete( INVOICE_MAPPING_TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        delete( SCHED_MAPPING_TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    @Override
    public void deletePartial() throws SQLException
    {
        Object constraintValues[] = { getShipmentID() };

        delete( INVOICE_MAPPING_TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }


    @Override
    public void retrieve() throws java.sql.SQLException 
    {
        Object constraintValues[] = { getShipmentID() };

        Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if( results.length == SETTER_COLUMNS.length )
        {
            setShipmentNumber( (String) results[0] );
            setWarehouseID( (Integer) results[1] );
            setSerialNumberStart( (String) results[2] );
            setSerialNumberEnd( (String) results[3] );
            setShipDate( (Date) results[4] );
            setActualPricePerUnit( (Double) results[5] );
            setSalesTotal( (Double) results[6] );
            setSalesTax( (Double) results[7] );
            setOtherCharges( (Double) results[8] );
            setShippingCharges( (Double) results[9] );
            setAmountPaid( (Double) results[10] );
            setOrderedDate( (Date) results[11] );
            setReceivedDate( (Date) results[12] );
        }
        else
            throw new Error( getClass() + "::retrieve - Incorrect number of results" );
    }


    @Override
    public void update() throws java.sql.SQLException 
    {
        Object setValues[] = { getShipmentNumber(), getWarehouseID(), getSerialNumberStart(), getSerialNumberEnd(),
                getShipDate(), getActualPricePerUnit(), getSalesTotal(), getSalesTax(), getOtherCharges(), getShippingCharges(), 
                getAmountPaid(), getOrderedDate(), getReceivedDate()};

        Object constraintValues[] = { getShipmentID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public static Integer getNextShipmentID() {
        Integer nextValueId = YukonSpringHook.getNextValueHelper().getNextValue(TABLE_NAME);
        return nextValueId;
    }

    /**
     * Sort by ordered date so that it is easy to show the most recent
     */
    public static List<Shipment> getAllShipmentsForDeliverySchedule(Integer shipID)
    {
        List<Shipment> shipments = new ArrayList<Shipment>();

        SqlStatement stmt = new SqlStatement("SELECT * FROM " + TABLE_NAME + " WHERE SHIPMENTID IN (SELECT SHIPMENTID FROM " + 
                                             SCHED_MAPPING_TABLE_NAME + " WHERE SCHEDULEID = " + shipID + ") ORDER BY ORDEREDDATE DESC", CtiUtilities.getDatabaseAlias());

        try
        {
            stmt.execute();

            if( stmt.getRowCount() > 0 )
            {
                for( int i = 0; i < stmt.getRowCount(); i++ )
                {
                    Shipment currentShipment = new Shipment();
                    currentShipment.setShipmentID( new Integer(stmt.getRow(i)[0].toString()));
                    currentShipment.setShipmentNumber( stmt.getRow(i)[1].toString() );
                    currentShipment.setWarehouseID( new Integer(stmt.getRow(i)[2].toString() ));
                    currentShipment.setSerialNumberStart( stmt.getRow(i)[3].toString() );
                    currentShipment.setSerialNumberEnd( stmt.getRow(i)[4].toString() );
                    currentShipment.setShipDate(new Date(((java.sql.Timestamp)stmt.getRow(i)[5]).getTime()));
                    currentShipment.setActualPricePerUnit( new Double(stmt.getRow(i)[6].toString()));
                    currentShipment.setSalesTotal( new Double(stmt.getRow(i)[7].toString()) );
                    currentShipment.setSalesTax( new Double(stmt.getRow(i)[8].toString()) );
                    currentShipment.setOtherCharges( new Double(stmt.getRow(i)[9].toString()) );
                    currentShipment.setShippingCharges( new Double(stmt.getRow(i)[10].toString()) );
                    currentShipment.setAmountPaid( new Double(stmt.getRow(i)[11].toString()) );
                    currentShipment.setOrderedDate( new Date(((java.sql.Timestamp)stmt.getRow(i)[12]).getTime()) );
                    currentShipment.setReceivedDate( new Date(((java.sql.Timestamp)stmt.getRow(i)[13]).getTime()) );

                    shipments.add(currentShipment);
                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return shipments;
    }

    /**
     * Sort by ordered date so that it is easy to show the most recent
     */
    public static List<Shipment> getAllShipmentsForInvoice(Integer invoiceID)
    {
        List<Shipment> shipments = new ArrayList<Shipment>();

        SqlStatement stmt = new SqlStatement("SELECT * FROM " + TABLE_NAME + " WHERE SHIPMENTID IN (SELECT SHIPMENTID FROM " + 
                                             INVOICE_MAPPING_TABLE_NAME + " WHERE INVOICEID = " + invoiceID + ") ORDER BY ORDEREDDATE DESC", CtiUtilities.getDatabaseAlias());

        try
        {
            stmt.execute();

            if( stmt.getRowCount() > 0 )
            {
                for( int i = 0; i < stmt.getRowCount(); i++ )
                {
                    Shipment currentShipment = new Shipment();
                    currentShipment.setShipmentID( new Integer(stmt.getRow(i)[0].toString()));
                    currentShipment.setShipmentNumber( stmt.getRow(i)[1].toString() );
                    currentShipment.setWarehouseID( new Integer(stmt.getRow(i)[2].toString() ));
                    currentShipment.setSerialNumberStart( stmt.getRow(i)[3].toString() );
                    currentShipment.setSerialNumberEnd( stmt.getRow(i)[4].toString() );
                    currentShipment.setShipDate(new Date(((java.sql.Timestamp)stmt.getRow(i)[5]).getTime()));
                    currentShipment.setActualPricePerUnit( new Double(stmt.getRow(i)[6].toString()));
                    currentShipment.setSalesTotal( new Double(stmt.getRow(i)[7].toString()) );
                    currentShipment.setSalesTax( new Double(stmt.getRow(i)[8].toString()) );
                    currentShipment.setOtherCharges( new Double(stmt.getRow(i)[9].toString()) );
                    currentShipment.setShippingCharges( new Double(stmt.getRow(i)[10].toString()) );
                    currentShipment.setAmountPaid( new Double(stmt.getRow(i)[11].toString()) );
                    currentShipment.setOrderedDate( new Date(((java.sql.Timestamp)stmt.getRow(i)[12]).getTime()) );
                    currentShipment.setReceivedDate( new Date(((java.sql.Timestamp)stmt.getRow(i)[13]).getTime()) );

                    shipments.add(currentShipment);
                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return shipments;
    }

    /**
     * 
     * Need to make sure that only shipments under the same plan are returned.
     */
    public static List<Shipment> getAllUnassignedShipmentsForInvoiceUse(Integer planID)
    {
        List<Shipment> shipments = new ArrayList<Shipment>();

        SqlStatement stmt = new SqlStatement("SELECT * FROM " + TABLE_NAME + " WHERE SHIPMENTID IN (SELECT SHIPMENTID FROM " + 
                                             SCHED_MAPPING_TABLE_NAME + " WHERE SCHEDULEID IN (SELECT SCHEDULEID FROM "+ 
                                             DeliverySchedule.TABLE_NAME + " WHERE PURCHASEPLANID = " + planID + ")) AND SHIPMENTID " +
                                             "NOT IN (SELECT SHIPMENTID FROM " + INVOICE_MAPPING_TABLE_NAME + 
                                             ") ORDER BY ORDEREDDATE DESC", CtiUtilities.getDatabaseAlias());

        try
        {
            stmt.execute();

            if( stmt.getRowCount() > 0 )
            {
                for( int i = 0; i < stmt.getRowCount(); i++ )
                {
                    Shipment currentShipment = new Shipment();
                    currentShipment.setShipmentID( new Integer(stmt.getRow(i)[0].toString()));
                    currentShipment.setShipmentNumber( stmt.getRow(i)[1].toString() );
                    currentShipment.setWarehouseID( new Integer(stmt.getRow(i)[2].toString() ));
                    currentShipment.setSerialNumberStart( stmt.getRow(i)[3].toString() );
                    currentShipment.setSerialNumberEnd( stmt.getRow(i)[4].toString() );
                    currentShipment.setShipDate(new Date(((java.sql.Timestamp)stmt.getRow(i)[5]).getTime()));
                    currentShipment.setActualPricePerUnit( new Double(stmt.getRow(i)[6].toString()));
                    currentShipment.setSalesTotal( new Double(stmt.getRow(i)[7].toString()) );
                    currentShipment.setSalesTax( new Double(stmt.getRow(i)[8].toString()) );
                    currentShipment.setOtherCharges( new Double(stmt.getRow(i)[9].toString()) );
                    currentShipment.setShippingCharges( new Double(stmt.getRow(i)[10].toString()) );
                    currentShipment.setAmountPaid( new Double(stmt.getRow(i)[11].toString()) );
                    currentShipment.setOrderedDate( new Date(((java.sql.Timestamp)stmt.getRow(i)[12]).getTime()) );
                    currentShipment.setReceivedDate( new Date(((java.sql.Timestamp)stmt.getRow(i)[13]).getTime()) );

                    shipments.add(currentShipment);
                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return shipments;
    }

    public Date getShipDate() {
        return shipDate;
    }

    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }

    public Integer getShipmentID() {
        return shipmentID;
    }

    public void setShipmentID(Integer shipmentID) {
        this.shipmentID = shipmentID;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Double getOtherCharges() {
        return otherCharges;
    }

    public void setOtherCharges(Double otherCharges) {
        this.otherCharges = otherCharges;
    }

    public Double getSalesTax() {
        return salesTax;
    }

    public void setSalesTax(Double salesTax) {
        this.salesTax = salesTax;
    }

    public Double getSalesTotal() {
        return salesTotal;
    }

    public void setSalesTotal(Double salesTotal) {
        this.salesTotal = salesTotal;
    }

    public String getSerialNumberEnd() {
        return serialNumberEnd;
    }

    public void setSerialNumberEnd(String serialNumberEnd) {
        this.serialNumberEnd = serialNumberEnd;
    }

    public String getSerialNumberStart() {
        return serialNumberStart;
    }

    public void setSerialNumberStart(String serialNumberStart) {
        this.serialNumberStart = serialNumberStart;
    }

    public Double getShippingCharges() {
        return shippingCharges;
    }

    public void setShippingCharges(Double shippingCharges) {
        this.shippingCharges = shippingCharges;
    }

    public Double getActualPricePerUnit() {
        return actualPricePerUnit;
    }

    public void setActualPricePerUnit(Double actualPricePerUnit) {
        this.actualPricePerUnit = actualPricePerUnit;
    }

    public String getShipmentNumber() {
        return shipmentNumber;
    }

    public void setShipmentNumber(String shipmentNumber) {
        this.shipmentNumber = shipmentNumber;
    }

    public Integer getWarehouseID() {
        return warehouseID;
    }

    public void setWarehouseID(Integer warehouseID) {
        this.warehouseID = warehouseID;
    }

    public Date getOrderedDate() {
        return orderedDate;
    }

    public void setOrderedDate(Date orderedDate) {
        this.orderedDate = orderedDate;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public Integer getScheduleID() {
        return scheduleID;
    }

    public void setScheduleID(Integer scheduleID) {
        this.scheduleID = scheduleID;
    }


}
