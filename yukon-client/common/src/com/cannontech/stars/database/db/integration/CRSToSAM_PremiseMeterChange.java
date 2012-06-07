package com.cannontech.stars.database.db.integration;

import java.util.ArrayList;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;

public class CRSToSAM_PremiseMeterChange extends DBPersistent {

    private Integer changeID; 
    private Integer premiseNumber = null;
    private String newDebtorNumber = "";
    private String transID = "";     
    private String streetAddress1 = "";   
    private String streetAddress2 = "";
    private String cityName = "";       
    private String stateCode = "";       
    private String zipCode = "";     
    private String firstName = "";       
    private String lastName = "";        
    private String homePhone = "";       
    private String workPhone = "";       
    private String oldMeterNumber = "";  
    private String newMeterNumber = "";
    private String siteNumber = "";
    
    public static final String CONSTRAINT_COLUMNS[] = { "ChangeID" };

    public static final String SETTER_COLUMNS[] = { "PremiseNumber","NewDebtorNumber", "TransID", "StreetAddress1", "StreetAddress2", 
                                                    "CityName", "StateCode", "ZipCode", "FirstName", "LastName",
                                                    "HomePhone", "WorkPhone", "OldMeterNumber", "NewMeterNumber", "SiteNumber"};

    public static final String TABLE_NAME = "CRSToSAM_PremiseMeterChange";


public CRSToSAM_PremiseMeterChange() {
    super();
}

public void add() throws java.sql.SQLException 
{
    Object setValues[] = { getChangeID(), getPremiseNumber(), getNewDebtorNumber(), getTransID(), getStreetAddress1(),
    	getStreetAddress2(), getCityName(), getStateCode(), getZipCode(), getFirstName(), getLastName(), getHomePhone(), 
    	getWorkPhone(), getOldMeterNumber(), getNewMeterNumber(), getSiteNumber()};

    add( TABLE_NAME, setValues );
}

public void delete() throws java.sql.SQLException 
{
    Object constraintValues[] = { getChangeID() };

    delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
}

public Integer getChangeID() 
{
    return changeID;
}

public Integer getPremiseNumber()
{
    return premiseNumber;
}

public String getNewDebtorNumber() 
{
    return newDebtorNumber;
}

public String getTransID()
{
    return transID;
}

public String getStreetAddress1()
{
    return streetAddress1;
}

public String getStreetAddress2()
{
    return streetAddress2;
}

public String getCityName()
{
    return cityName;
}

public String getStateCode()
{
    return stateCode;
}

public String getZipCode()
{
    return zipCode;
}

public String getFirstName()
{
    return firstName;
}

public String getLastName()
{
    return lastName;
}

public String getHomePhone()
{
    return homePhone;
}

public String getWorkPhone()
{
    return workPhone;
}

public String getOldMeterNumber()
{
    return oldMeterNumber;
}

public String getNewMeterNumber()
{
    return newMeterNumber;
}

public String getSiteNumber() {
    return siteNumber;
}

public void retrieve() throws java.sql.SQLException 
{
    Object constraintValues[] = { getChangeID() };

    Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

    if( results.length == SETTER_COLUMNS.length )
    {
        setPremiseNumber( (Integer) results[0] );
        setNewDebtorNumber( (String) results[1] );
        setTransID( (String) results[2] );    
        setStreetAddress1( (String) results[3] );  
        setStreetAddress2( (String) results[4] );
        setCityName( (String) results[5] );      
        setStateCode( (String) results[6] );      
        setZipCode( (String) results[7] );      
        setFirstName( (String) results[8] );       
        setLastName( (String) results[9] );        
        setHomePhone( (String) results[10] );       
        setWorkPhone( (String) results[11] );        
        setOldMeterNumber( (String) results[12] );  
        setNewMeterNumber( (String) results[13] );
        setSiteNumber( (String) results[14]);
    }
    else
        throw new Error( getClass() + "::retrieve - Incorrect number of results" );
}

public void setChangeID(Integer newID) 
{
    changeID = newID;
}

public void setPremiseNumber(Integer premNum) 
{
    premiseNumber = premNum;
}

public void setNewDebtorNumber(String newValue)
{
    newDebtorNumber = newValue;
}

public void setTransID(String newValue)
{
    transID = newValue;
}

public void setStreetAddress1(String newValue)
{
    streetAddress1 = newValue;
}

public void setStreetAddress2(String newValue)
{
    streetAddress2 = newValue;
}
public void setCityName(String newValue)
{
    cityName = newValue;
}

public void setStateCode(String newValue)
{
    stateCode = newValue;
}

public void setZipCode(String newValue)
{
    zipCode = newValue;
}

public void setFirstName(String newValue)
{
    firstName = newValue;
}

public void setLastName(String newValue)
{
    lastName = newValue;
}

public void setHomePhone(String newValue)
{
    homePhone = newValue;
}

public void setWorkPhone(String newValue)
{
    workPhone = newValue;
}

public void setOldMeterNumber(String newValue)
{
    oldMeterNumber = newValue;
}

public void setNewMeterNumber(String newValue)
{
    newMeterNumber = newValue;
}

public void setSiteNumber(String siteNumber) {
    this.siteNumber = siteNumber;
}

public void update() throws java.sql.SQLException 
{
    Object setValues[] = { getPremiseNumber(), getNewDebtorNumber(), getTransID(), getStreetAddress1(),
    		getStreetAddress2(), getCityName(), getStateCode(), getZipCode(), getFirstName(), getLastName(), 
    		getHomePhone(), getWorkPhone(), getOldMeterNumber(), getNewMeterNumber(), getSiteNumber()};
    
    Object constraintValues[] = { getChangeID() };

    update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}

public static ArrayList<CRSToSAM_PremiseMeterChange> getAllCurrentPremiseMeterChangeEntries()
{
    ArrayList<CRSToSAM_PremiseMeterChange> changes = new ArrayList<CRSToSAM_PremiseMeterChange>();
    
    SqlStatement stmt = new SqlStatement("SELECT * FROM " + TABLE_NAME, CtiUtilities.getDatabaseAlias());
    
    try
    {
        stmt.execute();
        
        if( stmt.getRowCount() > 0 )
        {
            for( int i = 0; i < stmt.getRowCount(); i++ )
            {
                CRSToSAM_PremiseMeterChange currentEntry = new CRSToSAM_PremiseMeterChange();
                currentEntry.setChangeID(new Integer(stmt.getRow(i)[0].toString()));
                if(stmt.getRow(i)[1] != null)
                    currentEntry.setPremiseNumber(new Integer(stmt.getRow(i)[1].toString()));
                if(stmt.getRow(i)[2] != null)
                    currentEntry.setNewDebtorNumber(stmt.getRow(i)[2].toString());
                if(stmt.getRow(i)[3] != null)
                    currentEntry.setTransID(stmt.getRow(i)[3].toString());
                if(stmt.getRow(i)[4] != null)
                    currentEntry.setStreetAddress1(stmt.getRow(i)[4].toString());
                if(stmt.getRow(i)[5] != null)
                    currentEntry.setStreetAddress2(stmt.getRow(i)[5].toString());
                if(stmt.getRow(i)[6] != null)
                    currentEntry.setCityName(stmt.getRow(i)[6].toString());
                if(stmt.getRow(i)[7] != null)
                    currentEntry.setStateCode(stmt.getRow(i)[7].toString());
                if(stmt.getRow(i)[8] != null)
                    currentEntry.setZipCode(stmt.getRow(i)[8].toString());
                if(stmt.getRow(i)[9] != null)
                    currentEntry.setFirstName(stmt.getRow(i)[9].toString());
                if(stmt.getRow(i)[10] != null)    
                    currentEntry.setLastName(stmt.getRow(i)[10].toString());
                if(stmt.getRow(i)[11] != null)
                    currentEntry.setHomePhone(stmt.getRow(i)[11].toString());
                if(stmt.getRow(i)[12] != null)    
                    currentEntry.setWorkPhone(stmt.getRow(i)[12].toString());
                if(stmt.getRow(i)[13] != null)    
                    currentEntry.setOldMeterNumber(stmt.getRow(i)[13].toString());
                if(stmt.getRow(i)[14] != null)
                    currentEntry.setNewMeterNumber(stmt.getRow(i)[14].toString());
                if(stmt.getRow(i)[15] != null)
                    currentEntry.setSiteNumber(stmt.getRow(i)[15].toString());
                
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



}
