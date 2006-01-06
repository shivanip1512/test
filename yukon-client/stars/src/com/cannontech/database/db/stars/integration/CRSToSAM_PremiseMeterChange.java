package com.cannontech.database.db.stars.integration;

import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;
import java.lang.Integer;
import java.lang.String;
import java.util.ArrayList;
import com.cannontech.common.util.CtiUtilities;

public class CRSToSAM_PremiseMeterChange extends DBPersistent {

    private Integer changeID; 
    private Integer premiseNumber;
    private String newDebtorNumber;
    private String transID;     
    private String streetAddress;   
    private String cityName;       
    private String stateCode;       
    private String zipCode;     
    private String firstName;       
    private String lastName;        
    private String homePhone;       
    private String workPhone;       
    private String oldMeterNumber;  
    private String newMeterNumber;

    public static final String CONSTRAINT_COLUMNS[] = { "ChangeID" };

    public static final String SETTER_COLUMNS[] = { "PremiseNumber","NewDebtorNumber", "TransID", "StreetAddress",
                                                    "CityName", "StateCode", "ZipCode", "FirstName", "LastName",
                                                    "HomePhone", "WorkPhone", "OldMeterNumber", "NewMeterNumber"};

    public static final String TABLE_NAME = "CRSToSAM_PremiseMeterChange";


public CRSToSAM_PremiseMeterChange() {
    super();
}

public void add() throws java.sql.SQLException 
{
    Object setValues[] = { getChangeID(), getPremiseNumber(), getNewDebtorNumber(), getTransID(), getStreetAddress(),
        getCityName(), getStateCode(), getZipCode(), getFirstName(), getLastName(), getHomePhone(), getWorkPhone(),
        getOldMeterNumber(), getNewMeterNumber()};

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

public String getStreetAddress()
{
    return streetAddress;
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

public void retrieve() throws java.sql.SQLException 
{
    Object constraintValues[] = { getChangeID() };

    Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

    if( results.length == SETTER_COLUMNS.length )
    {
        setPremiseNumber( (Integer) results[0] );
        setNewDebtorNumber( (String) results[1] );
        setTransID( (String) results[2] );    
        setStreetAddress( (String) results[3] );  
        setCityName( (String) results[4] );      
        setStateCode( (String) results[5] );      
        setZipCode( (String) results[6] );      
        setFirstName( (String) results[7] );       
        setLastName( (String) results[8] );        
        setHomePhone( (String) results[9] );       
        setWorkPhone( (String) results[9] );        
        setOldMeterNumber( (String) results[10] );  
        setNewMeterNumber( (String) results[11] );
        
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

public void setStreetAddress(String newValue)
{
    streetAddress = newValue;
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

public void update() throws java.sql.SQLException 
{
    Object setValues[] = { getPremiseNumber(), getNewDebtorNumber(), getTransID(), getStreetAddress(),
            getCityName(), getStateCode(), getZipCode(), getFirstName(), getLastName(), getHomePhone(), 
            getWorkPhone(), getOldMeterNumber(), getNewMeterNumber()};
    
    Object constraintValues[] = { getChangeID() };

    update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}

public static ArrayList getAllCurrentPremiseMeterChangeEntries()
{
    ArrayList changes = new ArrayList();
    
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
                currentEntry.setPremiseNumber(new Integer(stmt.getRow(i)[1].toString()));
                currentEntry.setNewDebtorNumber(stmt.getRow(i)[2].toString());
                currentEntry.setTransID(stmt.getRow(i)[3].toString());
                currentEntry.setStreetAddress(stmt.getRow(i)[4].toString());
                currentEntry.setCityName(stmt.getRow(i)[5].toString());
                currentEntry.setStateCode(stmt.getRow(i)[6].toString());
                currentEntry.setZipCode(stmt.getRow(i)[7].toString());
                currentEntry.setFirstName(stmt.getRow(i)[8].toString());
                currentEntry.setLastName(stmt.getRow(i)[9].toString());
                currentEntry.setHomePhone(stmt.getRow(i)[10].toString());
                currentEntry.setWorkPhone(stmt.getRow(i)[11].toString());
                currentEntry.setOldMeterNumber(stmt.getRow(i)[12].toString());
                currentEntry.setNewMeterNumber(stmt.getRow(i)[13].toString());
                
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
