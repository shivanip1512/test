package com.cannontech.database.db.stars.integration;

import java.util.ArrayList;
import java.util.Date;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;

public class SAMToCRS_PTJ extends DBPersistent {

    private Integer ptjID; 
    private Integer premiseNumber;
    private String debtorNumber;
    private String workOrderNumber;
    private String statusCode;
    private Date timestamp;
    private String starsUserName;

    public static final String CONSTRAINT_COLUMNS[] = { "PTJID" };

    public static final String SETTER_COLUMNS[] = { "PTJID", "PremiseNumber", "DebtorNumber", "WorkOrderNumber", "StatusCode", 
    												"Timestamp", "StarsUserName"};

    public static final String TABLE_NAME = "SAMToCRS_PTJ";


public SAMToCRS_PTJ() {
    super();
}

public void add() throws java.sql.SQLException 
{
    Object setValues[] = { getPTJID(), getPremiseNumber(), getDebtorNumber(), getWorkOrderNumber(),
    		getStatusCode(), getTimestamp(), getStarsUserName()};

    add( TABLE_NAME, setValues );
}

public void delete() throws java.sql.SQLException 
{
    Object constraintValues[] = { getPTJID() };

    delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
}


public void retrieve() throws java.sql.SQLException 
{
    Object constraintValues[] = { getPTJID() };

    Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

    if( results.length == SETTER_COLUMNS.length )
    {
        setPremiseNumber( (Integer) results[0] );
        setDebtorNumber( (String) results[1] );
        setWorkOrderNumber( (String) results[2] );
        setStatusCode( (String) results[3] );
        setTimestamp( (Date) results[4]);
        setStarsUserName( (String) results[5] );
    }
    else
        throw new Error( getClass() + "::retrieve - Incorrect number of results" );
}


public void update() throws java.sql.SQLException 
{
    Object setValues[] = { getPTJID(), getPremiseNumber(), getDebtorNumber(), 
    		getStatusCode(), getTimestamp(), getStarsUserName()}; 
    		
    		Object constraintValues[] = { getPTJID() };

    update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}

public static ArrayList getAllCurrentPTJEntries()
{
    ArrayList changes = new ArrayList();
    
    //Join the additional meter number table to load all additional meter numbers too.
    SqlStatement stmt = new SqlStatement("SELECT * FROM " + TABLE_NAME, CtiUtilities.getDatabaseAlias());
    
    try
    {
        stmt.execute();
        
        if( stmt.getRowCount() > 0 )
        {
            for( int i = 0; i < stmt.getRowCount(); i++ )
            {
            	SAMToCRS_PTJ currentEntry = new SAMToCRS_PTJ();
                currentEntry.setPTJID( new Integer(stmt.getRow(i)[0].toString()));
                currentEntry.setPremiseNumber( new Integer(stmt.getRow(i)[1].toString()));
                currentEntry.setDebtorNumber( stmt.getRow(i)[2].toString());
                currentEntry.setWorkOrderNumber( stmt.getRow(i)[3].toString());
                currentEntry.setStatusCode( stmt.getRow(i)[4].toString());
                currentEntry.setTimestamp(new Date(((java.sql.Timestamp)stmt.getRow(i)[5]).getTime()));
                currentEntry.setStarsUserName( stmt.getRow(i)[6].toString());
                
                changes.add(currentEntry);
            }
        }
    }
    catch( Exception e )
    {
        e.printStackTrace();
    }
    
    return changes;
}

public String getDebtorNumber() {
	return debtorNumber;
}

public void setDebtorNumber(String debtorNumber) {
	this.debtorNumber = debtorNumber;
}

public Integer getPremiseNumber() {
	return premiseNumber;
}

public void setPremiseNumber(Integer premiseNumber) {
	this.premiseNumber = premiseNumber;
}

public Integer getPTJID() {
	return ptjID;
}

public void setPTJID(Integer ptjID) {
	this.ptjID = ptjID;
}


public String getStarsUserName() {
	return starsUserName;
}

public void setStarsUserName(String starsUserName) {
	this.starsUserName = starsUserName;
}

public String getStatusCode() {
	return statusCode;
}

public void setStatusCode(String statusCode) {
	this.statusCode = statusCode;
}

public String getWorkOrderNumber() {
	return workOrderNumber;
}

public void setWorkOrderNumber(String workOrderNumber) {
	this.workOrderNumber = workOrderNumber;
}

public Date getTimestamp() {
	return timestamp;
}

public void setTimestamp(Date timestamp) {
	this.timestamp = timestamp;
}
}
