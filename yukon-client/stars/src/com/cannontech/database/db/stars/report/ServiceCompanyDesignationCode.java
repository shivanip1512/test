package com.cannontech.database.db.stars.report;

import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;
import java.lang.Integer;
import java.lang.String;
import java.util.ArrayList;
import com.cannontech.common.util.CtiUtilities;

public class ServiceCompanyDesignationCode extends DBPersistent {

    private Integer designationCodeID;
    private String designationCodeValue;
    private Integer serviceCompanyID;

    public static final String CONSTRAINT_COLUMNS[] = { "DesignationCodeID" };

    public static final String SETTER_COLUMNS[] = { "DesignationCodeValue","ServiceCompanyID" };

    public static final String TABLE_NAME = "ServiceCompanyDesignationCode";


public ServiceCompanyDesignationCode() {
    super();
}

public ServiceCompanyDesignationCode(String codeValue, Integer companyID) {
    super();
    
    SqlStatement stmt = new SqlStatement("SELECT MAX(DESIGNATIONCODEID) + 1 FROM " + TABLE_NAME, CtiUtilities.getDatabaseAlias());
    
    try
    {
        stmt.execute();
        
        if( stmt.getRowCount() > 0 )
        {
            designationCodeID = (new Integer(stmt.getRow(0)[0].toString()));
        }
    }
    catch( Exception e )
    {
        e.printStackTrace();
    }
    
    designationCodeValue = codeValue;
    serviceCompanyID = companyID;
}

public void add() throws java.sql.SQLException 
{
    Object setValues[] = { getDesignationCodeID(), getDesignationCodeValue(), 
        getServiceCompanyID() };

    add( TABLE_NAME, setValues );
}

public void delete() throws java.sql.SQLException 
{
    Object constraintValues[] = { getDesignationCodeID() };

    delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
}

public Integer getDesignationCodeID() 
{
    return designationCodeID;
}

public String getDesignationCodeValue()
{
    return designationCodeValue;
}

public Integer getServiceCompanyID() 
{
    return serviceCompanyID;
}

public void retrieve() throws java.sql.SQLException 
{
    Object constraintValues[] = { getDesignationCodeID() };

    Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

    if( results.length == SETTER_COLUMNS.length )
    {
        setDesignationCodeValue( (String) results[0] );
        setServiceCompanyID( (Integer) results[1] );
    }
    else
        throw new Error( getClass() + "::retrieve - Incorrect number of results" );
}

public void setDesignationCodeID(Integer newCodeID) 
{
    designationCodeID = newCodeID;
}

public void setDesignationCodeValue(String newValue)
{
    designationCodeValue = newValue;
}

public void setServiceCompanyID(Integer newServiceCoID) 
{
    serviceCompanyID = newServiceCoID;
}

public void update() throws java.sql.SQLException 
{
    Object setValues[] = { getDesignationCodeValue(), 
            getServiceCompanyID() };
    
    Object constraintValues[] = { getDesignationCodeID() };

    update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}

public static ArrayList getAllCodesForServiceCompany(int companyID)
{
    ArrayList codes = new ArrayList();
    
    SqlStatement stmt = new SqlStatement("SELECT * FROM " + TABLE_NAME + " WHERE SERVICECOMPANYID = " + companyID + " ORDER BY DESIGNATIONCODEVALUE", CtiUtilities.getDatabaseAlias());
    
    try
    {
        stmt.execute();
        
        if( stmt.getRowCount() > 0 )
        {
            for( int i = 0; i < stmt.getRowCount(); i++ )
            {
                ServiceCompanyDesignationCode newCode = new ServiceCompanyDesignationCode();
                newCode.setDesignationCodeID(new Integer(stmt.getRow(i)[0].toString()));
                newCode.setDesignationCodeValue(stmt.getRow(i)[1].toString());
                newCode.setServiceCompanyID(new Integer(companyID));
                
                codes.add(newCode);
            }
        }
    }
    catch( Exception e )
    {
        e.printStackTrace();
    }
    
    return codes;
}



}
