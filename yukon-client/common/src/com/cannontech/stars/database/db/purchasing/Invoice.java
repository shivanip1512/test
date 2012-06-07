package com.cannontech.stars.database.db.purchasing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;

public class Invoice extends DBPersistent {

    private Integer invoiceID;
    private Integer purchasePlanID; 
    private String invoiceDesignation;
    private Date dateSubmitted = new Date();
    private String authorized = "N";
    private String authorizedBy = "";
    private String hasPaid = "N";
    private Date datePaid = new Date();
    private Integer totalQuantity = new Integer(0);
    private String authorizedNumber = "";

    public static final String CONSTRAINT_COLUMNS[] = { "InvoiceID" };

    public static final String SETTER_COLUMNS[] = { "PurchasePlanID", "InvoiceDesignation", "DateSubmitted",
        "Authorized", "AuthorizedBy", "HasPaid", "DatePaid", "TotalQuantity", "AuthorizedNumber" };

    public static final String TABLE_NAME = "Invoice";

    private Integer shipmentID;
    public static final String MAPPING_TABLE_NAME = "InvoiceShipmentMapping";


    public Invoice() {
        super();
    }

    @Override
    public void add() throws java.sql.SQLException 
    {
        Object setValues[] = { getInvoiceID(), getPurchasePlanID(), getInvoiceDesignation(), 
                getDateSubmitted(), getAuthorized(), getAuthorizedBy(), getHasPaid(),
                getDatePaid(), getTotalQuantity(), getAuthorizedNumber()};

        add( TABLE_NAME, setValues );
    }

    @Override
    public void addPartial() throws java.sql.SQLException 
    {
        Object setValues[] = { getInvoiceID(), getShipmentID() };

        add( MAPPING_TABLE_NAME, setValues );
    }

    @Override
    public void delete() throws java.sql.SQLException 
    {
        Object constraintValues[] = { getInvoiceID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    @Override
    public void deletePartial() throws java.sql.SQLException 
    {
        Object constraintValues[] = { getInvoiceID() };

        delete( MAPPING_TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    @Override
    public void retrieve() throws java.sql.SQLException 
    {
        Object constraintValues[] = { getInvoiceID() };

        Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if( results.length == SETTER_COLUMNS.length )
        {
            setPurchasePlanID( (Integer) results[0] );
            setInvoiceDesignation( (String) results[1] );
            setDateSubmitted( (Date) results[2] );
            setAuthorized( (String) results[3] );
            setAuthorizedBy( (String) results[4] );
            setHasPaid( (String) results[5] );
            setDatePaid( (Date) results[6] );
            setTotalQuantity( (Integer) results[7]);
            setAuthorizedNumber( (String) results[8]);
        } 
        else
            throw new Error( getClass() + "::retrieve - Incorrect number of results" );
    }


    @Override
    public void update() throws java.sql.SQLException 
    {
        Object setValues[] = { getPurchasePlanID(), getInvoiceDesignation(), 
                getDateSubmitted(), getAuthorized(), getAuthorizedBy(), getHasPaid(),
                getDatePaid(), getTotalQuantity(), getAuthorizedNumber()};		

        Object constraintValues[] = { getInvoiceID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public static final Integer getNextInvoiceID() {
        Integer nextValueId = YukonSpringHook.getNextValueHelper().getNextValue(TABLE_NAME);
        return nextValueId;
    }

    /**
     * Sort by date submitted so that it is easy to show the most recent
     */
    public static List<Invoice> getAllInvoicesForPurchasePlan(Integer planID)
    {
        List<Invoice> invoices = new ArrayList<Invoice>();

        SqlStatement stmt = new SqlStatement("SELECT * FROM " + TABLE_NAME + " WHERE PURCHASEPLANID = " + planID + " ORDER BY DATESUBMITTED DESC", CtiUtilities.getDatabaseAlias());

        try
        {
            stmt.execute();

            if( stmt.getRowCount() > 0 )
            {
                for( int i = 0; i < stmt.getRowCount(); i++ )
                {
                    Invoice currentInvoice = new Invoice();
                    currentInvoice.setInvoiceID( new Integer(stmt.getRow(i)[0].toString()));
                    currentInvoice.setPurchasePlanID( new Integer(stmt.getRow(i)[1].toString()));
                    currentInvoice.setInvoiceDesignation( stmt.getRow(i)[2].toString());
                    currentInvoice.setDateSubmitted(new Date(((java.sql.Timestamp)stmt.getRow(i)[3]).getTime()));
                    currentInvoice.setAuthorized( stmt.getRow(i)[4].toString() );
                    currentInvoice.setAuthorizedBy( stmt.getRow(i)[5].toString() );
                    currentInvoice.setHasPaid( stmt.getRow(i)[6].toString() );
                    currentInvoice.setDatePaid(new Date(((java.sql.Timestamp)stmt.getRow(i)[7]).getTime()));
                    currentInvoice.setTotalQuantity( new Integer(stmt.getRow(i)[8].toString()));
                    currentInvoice.setAuthorizedNumber( stmt.getRow(i)[9].toString() );

                    invoices.add(currentInvoice);
                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return invoices;
    }

    public Integer getPurchasePlanID() {
        return purchasePlanID;
    }

    public void setPurchasePlanID(Integer purchasePlanID) {
        this.purchasePlanID = purchasePlanID;
    }

    public String getInvoiceDesignation() {
        return invoiceDesignation;
    }

    public void setInvoiceDesignation(String invoiceDesignation) {
        this.invoiceDesignation = invoiceDesignation;
    }

    public Date getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(Date dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public Integer getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(Integer invoiceID) {
        this.invoiceID = invoiceID;
    }

    public String getAuthorized() {
        return authorized;
    }

    public void setAuthorized(String authorized) {
        this.authorized = authorized;
    }

    public String getAuthorizedBy() {
        return authorizedBy;
    }

    public void setAuthorizedBy(String authorizedBy) {
        this.authorizedBy = authorizedBy;
    }

    public Date getDatePaid() {
        return datePaid;
    }

    public void setDatePaid(Date datePaidInFull) {
        this.datePaid = datePaidInFull;
    }

    public String getHasPaid() {
        return hasPaid;
    }

    public void setHasPaid(String hasPaid) {
        this.hasPaid = hasPaid;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Integer getShipmentID() {
        return shipmentID;
    }

    public void setShipmentID(Integer shipmentID) {
        this.shipmentID = shipmentID;
    }

    public String getAuthorizedNumber() {
        return authorizedNumber;
    }

    public void setAuthorizedNumber(String authorizedNum) {
        this.authorizedNumber = authorizedNum;
    }
}
