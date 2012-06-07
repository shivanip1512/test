package com.cannontech.stars.database.db.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;

public class CRSToSAM_PTJAdditionalMeters extends DBPersistent {

    private Integer ptjID; 
    private String meterNumber;

    public static final String CONSTRAINT_COLUMNS[] = { "PTJID" };

    public static final String SETTER_COLUMNS[] = { "PTJID", "MeterNumber"};

    public static final String TABLE_NAME = "CRSToSAM_PTJAdditionalMeters";


public CRSToSAM_PTJAdditionalMeters() {
    super();
}

public void add() throws java.sql.SQLException 
{
    Object setValues[] = { getPTJID(), getMeterNumber()};

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
        setMeterNumber( (String) results[0] );
    }
    else
        throw new Error( getClass() + "::retrieve - Incorrect number of results" );
}


public void update() throws java.sql.SQLException 
{
    Object setValues[] = { getPTJID(), getMeterNumber()}; 
    		
    Object constraintValues[] = { getPTJID() };

    update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}

public static List<CRSToSAM_PTJAdditionalMeters> getAllCurrentPTJAdditionalMeterEntries()
{
    List<CRSToSAM_PTJAdditionalMeters> changes = new ArrayList<CRSToSAM_PTJAdditionalMeters>();
    
    SqlStatement stmt = new SqlStatement("SELECT * FROM " + TABLE_NAME, CtiUtilities.getDatabaseAlias());
    
    try
    {
        stmt.execute();
        
        if( stmt.getRowCount() > 0 )
        {
            for( int i = 0; i < stmt.getRowCount(); i++ )
            {
                CRSToSAM_PTJAdditionalMeters currentEntry = new CRSToSAM_PTJAdditionalMeters();
                currentEntry.setPTJID( new Integer(stmt.getRow(i)[0].toString()));
                currentEntry.setMeterNumber( stmt.getRow(i)[1].toString());
                
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

/**
 * Loads from database all additional ptj meters.  Returns map of ptjID <Integer> to ArrayList (of CRSToSAM_PTJAdditionalMeters objects)
 * @param ptjID_
 * @return
 */
public static Map<Integer, List<CRSToSAM_PTJAdditionalMeters>> retrieveAllCurrentPTJAdditionalMeterEntriesMap()
{
	Map<Integer, List<CRSToSAM_PTJAdditionalMeters>> ptjToAdditionMeterMap = new HashMap<Integer, List<CRSToSAM_PTJAdditionalMeters>>();
    List<CRSToSAM_PTJAdditionalMeters> changes = new ArrayList<CRSToSAM_PTJAdditionalMeters>();
    
    SqlStatement stmt = new SqlStatement("SELECT * FROM " + TABLE_NAME , CtiUtilities.getDatabaseAlias());
    
    try
    {
        stmt.execute();
        
        if( stmt.getRowCount() > 0 )
        {
        	int lastPTJID = -1;
        	CRSToSAM_PTJAdditionalMeters currentEntry = null;
            for( int i = 0; i < stmt.getRowCount(); i++ )
            {
        		currentEntry = new CRSToSAM_PTJAdditionalMeters();
        		currentEntry.setPTJID( new Integer(stmt.getRow(i)[0].toString()));
        		currentEntry.setMeterNumber( stmt.getRow(i)[1].toString());

            	if( currentEntry.getPTJID().intValue() != lastPTJID && lastPTJID > -1)
            	{
            		ptjToAdditionMeterMap.put(lastPTJID, changes);
            		changes = new ArrayList<CRSToSAM_PTJAdditionalMeters>();
            	}
                
                changes.add(currentEntry);
                lastPTJID = currentEntry.getPTJID().intValue();
            }
            if( !changes.isEmpty())	//add the last one
            {
        		ptjToAdditionMeterMap.put(currentEntry.getPTJID(), changes);
            }
        }
    }
    catch( Exception e )
    {
        e.printStackTrace();
    }
    
    return ptjToAdditionMeterMap;
}

public String getMeterNumber() {
	return meterNumber;
}

public void setMeterNumber(String meterNumber) {
	this.meterNumber = meterNumber;
}

public Integer getPTJID() {
	return ptjID;
}

public void setPTJID(Integer ptjID) {
	this.ptjID = ptjID;
}
}
